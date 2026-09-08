package com.antigravity.mobile.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: String,
    val name: String,
    val rootPath: String,
    val lastOpenedTimestamp: Long = System.currentTimeMillis()
)

@Serializable
data class FileNode(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val sizeBytes: Long = 0,
    val children: List<FileNode> = emptyList(),
    val isExpanded: Boolean = false
)

@Serializable
enum class MessageRole {
    USER,
    AGENT,
    SYSTEM,
    TOOL
}

@Serializable
data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val planSteps: List<PlanStep> = emptyList(),
    val pendingAction: PendingAction? = null,
    val isStreaming: Boolean = false
)

@Serializable
enum class PlanStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    SKIPPED
}

@Serializable
data class PlanStep(
    val id: String,
    val title: String,
    val status: PlanStatus = PlanStatus.PENDING,
    val toolName: String? = null
)

@Serializable
data class PendingAction(
    val actionId: String,
    val title: String,
    val description: String,
    val permissionTier: String,
    val targetFile: String? = null,
    val command: String? = null,
    val diffPreview: String? = null
)
