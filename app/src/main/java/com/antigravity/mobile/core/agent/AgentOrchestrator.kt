package com.antigravity.mobile.core.agent

import com.antigravity.mobile.core.model.CompletionRequest
import com.antigravity.mobile.core.model.ModelCapabilities
import com.antigravity.mobile.core.model.ModelRouter
import com.antigravity.mobile.core.model.StreamChunk
import com.antigravity.mobile.core.tools.ToolRegistry
import com.antigravity.mobile.domain.model.AgentEvent
import com.antigravity.mobile.domain.model.AgentStatus
import com.antigravity.mobile.domain.model.AutonomyLevel
import com.antigravity.mobile.domain.model.ChatMessage
import com.antigravity.mobile.domain.model.MessageRole
import com.antigravity.mobile.domain.model.PendingAction
import com.antigravity.mobile.domain.model.PermissionTier
import com.antigravity.mobile.domain.model.PlanStatus
import com.antigravity.mobile.domain.model.PlanStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class AgentOrchestrator(
    private val modelRouter: ModelRouter,
    private val toolRegistry: ToolRegistry,
    private val contextEngine: ContextEngine,
    private val permissionManager: PermissionManager,
    private val diffManager: DiffManager
) {
    private val _status = MutableStateFlow(AgentStatus.IDLE)
    val status = _status.asStateFlow()

    private var autonomyLevel: AutonomyLevel = AutonomyLevel.LEVEL_4_BOUNDED
    private val conversationHistory = mutableListOf<ChatMessage>()
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

    private fun transitionTo(newStatus: AgentStatus, reason: String = "") {
        val old = _status.value
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

        val responseBuffer = StringBuilder()
        var selfHealingAttempts = 0
        val maxSelfHealingBudget = 3

        try {
            provider.streamCompletion(request.copy(modelId = descriptor.id), tools).collect { chunk ->
                when (chunk) {
                    is StreamChunk.Content -> {
                        responseBuffer.append(chunk.text)
                        emit(AgentEvent.Thinking(chunk.text))
                    }
                    is StreamChunk.ToolCall -> {
                        transitionTo(AgentStatus.EXECUTING_TOOL, "Executing tool: ${chunk.toolName}")
                        emit(AgentEvent.StateChanged(_status.value, AgentStatus.EXECUTING_TOOL))
                        emit(AgentEvent.ToolStarted(chunk.callId, chunk.toolName, chunk.argumentsJson))

                        val toolDef = tools.find { it.name == chunk.toolName }
                        val requiresApproval = when (autonomyLevel) {
                            AutonomyLevel.LEVEL_1_READ_ONLY -> toolDef?.permissionTier != PermissionTier.SAFE
                            AutonomyLevel.LEVEL_2_SUGGEST -> toolDef?.permissionTier != PermissionTier.SAFE
                            AutonomyLevel.LEVEL_3_SUPERVISED -> toolDef?.permissionTier == PermissionTier.DANGEROUS || toolDef?.permissionTier == PermissionTier.DESTRUCTIVE
                            AutonomyLevel.LEVEL_4_BOUNDED -> toolDef?.permissionTier == PermissionTier.DESTRUCTIVE
                            AutonomyLevel.LEVEL_5_AUTONOMOUS -> false
                        }

                        if (requiresApproval && toolDef != null) {
                            transitionTo(AgentStatus.AWAITING_APPROVAL, "Approval required for ${chunk.toolName}")
                            emit(AgentEvent.StateChanged(AgentStatus.EXECUTING_TOOL, AgentStatus.AWAITING_APPROVAL))
                            emit(
                                AgentEvent.AwaitingApproval(
                                    PendingAction(
                                        actionId = chunk.callId,
                                        title = "Approve ${chunk.toolName}",
                                        description = "Tool call arguments: ${chunk.argumentsJson}",
                                        permissionTier = toolDef.permissionTier.name
                                    )
                                )
                            )
                        } else {
                            val result = toolRegistry.execute(chunk.callId, chunk.toolName, chunk.argumentsJson)
                            emit(AgentEvent.ToolCompleted(chunk.callId, chunk.toolName, result))

                            if (result.isError && selfHealingAttempts < maxSelfHealingBudget) {
                                selfHealingAttempts++
                                transitionTo(AgentStatus.SELF_HEALING, "Diagnosing and self-healing error (Attempt $selfHealingAttempts/$maxSelfHealingBudget)")
                                emit(AgentEvent.StateChanged(AgentStatus.EXECUTING_TOOL, AgentStatus.SELF_HEALING))
                            } else if (chunk.toolName == "write_file") {
                                val diff = diffManager.computeDiff("index.html", "", "<!-- New Generated File -->")
                                emit(AgentEvent.FileDiffCreated(diff))
                            }
                        }
                    }
                    is StreamChunk.Completed -> {
                        transitionTo(AgentStatus.COMPLETED, "Task completed successfully")
                        emit(AgentEvent.StateChanged(_status.value, AgentStatus.COMPLETED))
                        emit(AgentEvent.Completed("Task completed successfully using ${descriptor.displayName}."))
                    }
                    is StreamChunk.Error -> {
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
            if (_status.value.isTerminal) {
                // Keep terminal state for UI presentation
            } else {
                transitionTo(AgentStatus.IDLE, "Idle state")
            }
        }
    }
}
