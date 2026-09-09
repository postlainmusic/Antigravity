package com.antigravity.mobile.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AgentStatus {
    IDLE,
    THINKING,
    PLANNING,
    EXECUTING_TOOL,
    AWAITING_APPROVAL,
    SELF_HEALING,
    COMPLETED,
    CANCELLED,
    FAILED,
    PAUSED,
    WAITING_FOR_RESOURCE,
    WAITING_FOR_USER,
    RECOVERING,
    PARTIAL_SUCCESS;

    val isTerminal: Boolean
        get() = this in listOf(COMPLETED, CANCELLED, FAILED, PARTIAL_SUCCESS)

    val isActive: Boolean
        get() = this in listOf(THINKING, PLANNING, EXECUTING_TOOL, SELF_HEALING, RECOVERING)
}

@Serializable
enum class AutonomyLevel {
    LEVEL_1_READ_ONLY,
    LEVEL_2_SUGGEST,
    LEVEL_3_SUPERVISED,
    LEVEL_4_BOUNDED,
    LEVEL_5_AUTONOMOUS
}

@Serializable
enum class TrustLevel {
    SYSTEM,
    DEVELOPER_POLICY,
    USER_INTENT,
    PROJECT_DATA,
    TOOL_OUTPUT,
    EXTERNAL_CONTENT;

    val isAuthoritative: Boolean
        get() = this == SYSTEM || this == DEVELOPER_POLICY || this == USER_INTENT
}

sealed interface AgentEvent {
    data class Started(val conversationId: String) : AgentEvent
    data class Thinking(val content: String) : AgentEvent
    data class PlanUpdated(val steps: List<PlanStep>) : AgentEvent
    data class ToolStarted(val callId: String, val toolName: String, val argsJson: String) : AgentEvent
    data class ToolCompleted(val callId: String, val toolName: String, val result: ToolResult) : AgentEvent
    data class FileDiffCreated(val fileDiff: FileDiff) : AgentEvent
    data class AwaitingApproval(val action: PendingAction) : AgentEvent
    data class StateChanged(val oldStatus: AgentStatus, val newStatus: AgentStatus, val reason: String = "") : AgentEvent
    data class TerminalOutput(val text: String) : AgentEvent
    data class Error(val message: String, val recoverable: Boolean = true) : AgentEvent
    data class Completed(val summary: String) : AgentEvent
}

@Serializable
enum class PermissionTier {
    SAFE,
    MODERATE,
    DANGEROUS,
    DESTRUCTIVE
}

@Serializable
sealed interface PermissionDecision {
    data object Approved : PermissionDecision
    data class RequiresApproval(val action: PendingAction) : PermissionDecision
    data class Denied(val reason: String) : PermissionDecision
    data class Blocked(val reason: String) : PermissionDecision
    data class SecurityViolation(val violationMessage: String) : PermissionDecision
}

@Serializable
data class ToolParameter(
    val name: String,
    val type: String,
    val description: String,
    val required: Boolean = true
)

@Serializable
data class ToolDefinition(
    val name: String,
    val description: String,
    val parameters: List<ToolParameter>,
    val permissionTier: PermissionTier = PermissionTier.SAFE,
    val timeoutMs: Long = 30000L
)

@Serializable
data class ToolCall(
    val callId: String,
    val toolName: String,
    val arguments: Map<String, String>
)

@Serializable
data class ToolResult(
    val callId: String,
    val toolName: String,
    val output: String,
    val isError: Boolean = false,
    val trustLevel: TrustLevel = TrustLevel.TOOL_OUTPUT,
    val secretsRedacted: Int = 0
)

@Serializable
enum class DiffType {
    ADDITION,
    DELETION,
    CONTEXT
}

@Serializable
data class DiffChunk(
    val type: DiffType,
    val content: String,
    val oldLineNum: Int? = null,
    val newLineNum: Int? = null
)

@Serializable
data class DiffHunk(
    val oldStart: Int,
    val oldCount: Int,
    val newStart: Int,
    val newCount: Int,
    val chunks: List<DiffChunk>,
    val isStaged: Boolean = true
)

@Serializable
data class FileDiff(
    val filePath: String,
    val oldContent: String,
    val newContent: String,
    val hunks: List<DiffHunk> = emptyList(),
    val additionsCount: Int = 0,
    val deletionsCount: Int = 0
)

@Serializable
data class TerminalOutputLine(
    val text: String,
    val isError: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class TerminalSession(
    val id: String,
    val title: String,
    val workingDir: String,
    val outputHistory: List<TerminalOutputLine> = emptyList(),
    val isRunning: Boolean = false
)
