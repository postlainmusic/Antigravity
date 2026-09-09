package com.antigravity.mobile.core.agent

import com.antigravity.mobile.core.model.CompletionRequest
import com.antigravity.mobile.core.model.ModelCapabilities
import com.antigravity.mobile.core.model.ModelRouter
import com.antigravity.mobile.core.security.SandboxManager
import com.antigravity.mobile.core.tools.ToolRegistry
import com.antigravity.mobile.domain.model.AgentEvent
import com.antigravity.mobile.domain.model.AgentStatus
import com.antigravity.mobile.domain.model.AutonomyLevel
import com.antigravity.mobile.domain.model.ChatMessage
import com.antigravity.mobile.domain.model.MessageRole
import com.antigravity.mobile.domain.model.PermissionDecision
import com.antigravity.mobile.domain.model.PlanStatus
import com.antigravity.mobile.domain.model.PlanStep
import com.antigravity.mobile.domain.model.ToolResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class AgentOrchestrator(
    private val modelRouter: ModelRouter,
    private val toolRegistry: ToolRegistry,
    private val contextEngine: ContextEngine,
    private val permissionManager: PermissionManager,
    private val diffManager: DiffManager,
    private val sandboxManager: SandboxManager
) {
    private val _status = MutableStateFlow(AgentStatus.IDLE)
    val status = _status.asStateFlow()

    private var autonomyLevel: AutonomyLevel = AutonomyLevel.LEVEL_4_BOUNDED
    private val conversationHistory = mutableListOf<ChatMessage>()
    private val pendingApprovals = ConcurrentHashMap<String, CompletableDeferred<Boolean>>()
    private var isPaused = false

    fun setAutonomyLevel(level: AutonomyLevel) {
        this.autonomyLevel = level
    }

    fun pause() {
        isPaused = true
        transitionTo(AgentStatus.PAUSED, "Task execution paused by user")
    }

    fun resume() {
        isPaused = false
        transitionTo(AgentStatus.THINKING, "Task execution resumed")
    }

    /** Resume a tool call that is waiting on the user's permission decision. */
    fun resolveApproval(actionId: String, approved: Boolean) {
        pendingApprovals.remove(actionId)?.complete(approved)
    }

    private fun transitionTo(newStatus: AgentStatus, reason: String = "") {
        _status.value = newStatus
    }

    fun executeTask(userPrompt: String, activeFilePath: String? = null): Flow<AgentEvent> = flow {
        val convId = UUID.randomUUID().toString()
        transitionTo(AgentStatus.THINKING, "Starting task execution")
        emit(AgentEvent.StateChanged(AgentStatus.IDLE, AgentStatus.THINKING))
        emit(AgentEvent.Started(convId))

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = userPrompt
        )
        conversationHistory.add(userMessage)

        val systemPrompt = contextEngine.assembleSystemPrompt(activeFilePath)
        val request = CompletionRequest(
            modelId = "auto",
            messages = conversationHistory,
            systemPrompt = systemPrompt,
            requiredCapabilities = ModelCapabilities(coding = true, toolUse = true)
        )

        val (provider, descriptor) = try {
            modelRouter.selectOptimalModel(request)
        } catch (e: Exception) {
            transitionTo(AgentStatus.FAILED, e.message ?: "No model available")
            emit(AgentEvent.Error("Model routing failed: ${e.message}", recoverable = false))
            return@flow
        }

        val tools = toolRegistry.getAllDefinitions()
        transitionTo(AgentStatus.PLANNING, "Generating task execution plan")
        emit(AgentEvent.StateChanged(AgentStatus.THINKING, AgentStatus.PLANNING))

        val plan = listOf(
            PlanStep(UUID.randomUUID().toString(), "Analyze workspace & requirements with ${descriptor.displayName}", PlanStatus.IN_PROGRESS),
            PlanStep(UUID.randomUUID().toString(), "Execute tool operations within security sandbox", PlanStatus.PENDING),
            PlanStep(UUID.randomUUID().toString(), "Verify code changes & run golden gate tests", PlanStatus.PENDING)
        )
        emit(AgentEvent.PlanUpdated(plan))

        var selfHealingAttempts = 0
        val maxSelfHealingBudget = 3

        try {
            provider.streamCompletion(request.copy(modelId = descriptor.id), tools).collect { chunk ->
                when (chunk) {
                    is com.antigravity.mobile.core.model.StreamChunk.Content -> emit(AgentEvent.Thinking(chunk.text))
                    is com.antigravity.mobile.core.model.StreamChunk.ToolCall -> {
                        transitionTo(AgentStatus.EXECUTING_TOOL, "Executing tool: ${chunk.toolName}")
                        emit(AgentEvent.StateChanged(_status.value, AgentStatus.EXECUTING_TOOL))
                        emit(AgentEvent.ToolStarted(chunk.callId, chunk.toolName, chunk.argumentsJson))

                        val toolDef = tools.find { it.name == chunk.toolName }
                        val decision = if (toolDef != null) {
                            permissionManager.evaluatePermission(toolDef, autonomyLevel, chunk.callId, chunk.argumentsJson)
                        } else {
                            PermissionDecision.Blocked("Tool '${chunk.toolName}' is not registered.")
                        }

                        when (decision) {
                            is PermissionDecision.Approved -> {
                                executeApprovedTool(
                                    callId = chunk.callId,
                                    toolName = chunk.toolName,
                                    argumentsJson = chunk.argumentsJson,
                                    maxSelfHealingBudget = maxSelfHealingBudget,
                                    selfHealingAttempts = selfHealingAttempts
                                )?.let { selfHealingAttempts = it }
                            }
                            is PermissionDecision.RequiresApproval -> {
                                transitionTo(AgentStatus.AWAITING_APPROVAL, "Approval required for ${chunk.toolName}")
                                emit(AgentEvent.StateChanged(AgentStatus.EXECUTING_TOOL, AgentStatus.AWAITING_APPROVAL))
                                emit(AgentEvent.AwaitingApproval(decision.action))

                                val approval = CompletableDeferred<Boolean>()
                                pendingApprovals[decision.action.actionId] = approval
                                val approved = approval.await()
                                pendingApprovals.remove(decision.action.actionId)

                                if (approved) {
                                    transitionTo(AgentStatus.EXECUTING_TOOL, "Permission granted for ${chunk.toolName}")
                                    emit(AgentEvent.StateChanged(AgentStatus.AWAITING_APPROVAL, AgentStatus.EXECUTING_TOOL))
                                    executeApprovedTool(
                                        callId = chunk.callId,
                                        toolName = chunk.toolName,
                                        argumentsJson = chunk.argumentsJson,
                                        maxSelfHealingBudget = maxSelfHealingBudget,
                                        selfHealingAttempts = selfHealingAttempts
                                    )?.let { selfHealingAttempts = it }
                                } else {
                                    transitionTo(AgentStatus.IDLE, "User denied ${chunk.toolName}")
                                    emit(AgentEvent.ToolCompleted(
                                        chunk.callId,
                                        chunk.toolName,
                                        ToolResult(chunk.callId, chunk.toolName, "DENIED by user", isError = true)
                                    ))
                                    emit(AgentEvent.Error("Operation denied by user: ${chunk.toolName}", recoverable = true))
                                }
                            }
                            is PermissionDecision.Blocked -> {
                                emit(AgentEvent.Error("Operation blocked by policy: ${decision.reason}", recoverable = true))
                                emit(AgentEvent.ToolCompleted(chunk.callId, chunk.toolName, ToolResult(chunk.callId, chunk.toolName, "BLOCKED: ${decision.reason}", isError = true)))
                            }
                            is PermissionDecision.Denied -> {
                                emit(AgentEvent.Error("Permission denied: ${decision.reason}", recoverable = true))
                            }
                            is PermissionDecision.SecurityViolation -> {
                                transitionTo(AgentStatus.FAILED, "Security violation: ${decision.violationMessage}")
                                emit(AgentEvent.StateChanged(_status.value, AgentStatus.FAILED))
                                emit(AgentEvent.Error("SECURITY VIOLATION: ${decision.violationMessage}", recoverable = false))
                            }
                        }
                    }
                    is com.antigravity.mobile.core.model.StreamChunk.Completed -> {
                        transitionTo(AgentStatus.COMPLETED, "Task completed successfully")
                        emit(AgentEvent.StateChanged(_status.value, AgentStatus.COMPLETED))
                        emit(AgentEvent.Completed("Task completed successfully using ${descriptor.displayName}."))
                    }
                    is com.antigravity.mobile.core.model.StreamChunk.Error -> {
                        transitionTo(AgentStatus.FAILED, "Model stream error")
                        emit(AgentEvent.StateChanged(_status.value, AgentStatus.FAILED))
                        emit(AgentEvent.Error("Model execution error: ${chunk.error.message}"))
                    }
                }
            }
        } catch (e: Exception) {
            transitionTo(AgentStatus.FAILED, "Execution exception")
            emit(AgentEvent.StateChanged(_status.value, AgentStatus.FAILED))
            emit(AgentEvent.Error("Orchestration failure: ${e.message}"))
        } finally {
            pendingApprovals.clear()
            if (!_status.value.isTerminal) transitionTo(AgentStatus.IDLE, "Idle state")
        }
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<AgentEvent>.executeApprovedTool(
        callId: String,
        toolName: String,
        argumentsJson: String,
        maxSelfHealingBudget: Int,
        selfHealingAttempts: Int
    ): Int? {
        val arguments = parseArguments(argumentsJson)
        val targetFile = arguments["targetFile"] ?: arguments["filePath"]
        val oldContent = if (targetFile != null) {
            runCatching {
                sandboxManager.resolveSafePath(targetFile).takeIf { it.exists() && it.isFile }?.readText().orEmpty()
            }.getOrDefault("")
        } else ""

        val result = toolRegistry.execute(callId, toolName, argumentsJson)
        emit(AgentEvent.ToolCompleted(callId, toolName, result))

        if (result.isError && selfHealingAttempts < maxSelfHealingBudget) {
            val nextAttempt = selfHealingAttempts + 1
            transitionTo(AgentStatus.SELF_HEALING, "Diagnosing and self-healing error (Attempt $nextAttempt/$maxSelfHealingBudget)")
            emit(AgentEvent.StateChanged(AgentStatus.EXECUTING_TOOL, AgentStatus.SELF_HEALING))
            return nextAttempt
        }

        if (!result.isError && (toolName == "write_file" || toolName == "replace_file_content") && targetFile != null) {
            val newContent = runCatching {
                sandboxManager.resolveSafePath(targetFile).takeIf { it.exists() && it.isFile }?.readText()
            }.getOrNull()
            if (newContent != null) {
                emit(AgentEvent.FileDiffCreated(diffManager.computeDiff(targetFile, oldContent, newContent)))
            }
        }
        return selfHealingAttempts
    }

    private fun parseArguments(argumentsJson: String): Map<String, String> {
        return runCatching {
            Json.parseToJsonElement(argumentsJson)
                .jsonObject
                .mapValues { it.value.jsonPrimitive.content }
        }.getOrDefault(emptyMap())
    }
}
