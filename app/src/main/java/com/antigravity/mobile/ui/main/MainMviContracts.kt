package com.antigravity.mobile.ui.main

import com.antigravity.mobile.domain.model.AgentStatus
import com.antigravity.mobile.domain.model.ChatMessage
import com.antigravity.mobile.domain.model.FileDiff
import com.antigravity.mobile.domain.model.FileNode
import com.antigravity.mobile.domain.model.PendingAction
import com.antigravity.mobile.domain.model.PlanStep
import com.antigravity.mobile.domain.model.Project
import com.antigravity.mobile.domain.model.TerminalSession

enum class NavigationTab {
    AGENT,
    EDITOR,
    PREVIEW,
    TERMINAL,
    GIT_DIFF
}

data class MainUiState(
    val activeTab: NavigationTab = NavigationTab.AGENT,
    val currentProject: Project? = Project("proj-default", "Antigravity Workspace", "."),
    val fileTree: FileNode? = null,
    val activeFilePath: String? = "index.html",
    val activeFileContent: String = "",
    val chatMessages: List<ChatMessage> = emptyList(),
    val agentStatus: AgentStatus = AgentStatus.IDLE,
    val currentPlan: List<PlanStep> = emptyList(),
    val pendingAction: PendingAction? = null,
    val pendingDiff: FileDiff? = null,
    val terminalSessions: List<TerminalSession> = emptyList(),
    val activeTerminalSessionId: String? = null,
    val previewHtml: String = "",
    val previewConsoleLogs: List<String> = emptyList(),
    val isDrawerOpen: Boolean = false,
    val errorBanner: String? = null
)

sealed interface MainUiEvent {
    data class SelectTab(val tab: NavigationTab) : MainUiEvent
    data class SendUserPrompt(val prompt: String) : MainUiEvent
    data class OpenFile(val path: String) : MainUiEvent
    data class SaveFile(val path: String, val content: String) : MainUiEvent
    data class ApprovePendingAction(val actionId: String) : MainUiEvent
    data class RejectPendingAction(val actionId: String) : MainUiEvent
    data class ApplyDiff(val diff: FileDiff) : MainUiEvent
    data class DiscardDiff(val diff: FileDiff) : MainUiEvent
    data class RunTerminalCommand(val command: String) : MainUiEvent
    data class RefreshPreview(val url: String? = null) : MainUiEvent
    data class ToggleDrawer(val open: Boolean) : MainUiEvent
    data object ClearError : MainUiEvent
}
