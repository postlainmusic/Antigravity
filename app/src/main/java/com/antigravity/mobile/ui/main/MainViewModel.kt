package com.antigravity.mobile.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.mobile.core.di.AppContainer
import com.antigravity.mobile.core.di.DefaultAppContainer
import com.antigravity.mobile.domain.model.AgentEvent
import com.antigravity.mobile.domain.model.AgentStatus
import com.antigravity.mobile.domain.model.ChatMessage
import com.antigravity.mobile.domain.model.FileDiff
import com.antigravity.mobile.domain.model.MessageRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(
    private val container: AppContainer = DefaultAppContainer()
) : ViewModel() {

    private val sandboxManager = container.sandboxManager
    private val executeAgentTaskUseCase = container.executeAgentTaskUseCase
    private val applyFileDiffUseCase = container.applyFileDiffUseCase
    private val indexWorkspaceUseCase = container.indexWorkspaceUseCase
    private val runTerminalCommandUseCase = container.runTerminalCommandUseCase
    private val terminalManager = container.terminalManager
    private val devServer = container.devServer
    private val credentialStore = container.credentialStore
    private val agentOrchestrator = container.agentOrchestrator

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadWorkspace()
        observeTerminal()
        observeDevServer()
    }

    fun handleIntent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.SelectTab -> _uiState.update { it.copy(activeTab = event.tab) }
            is MainUiEvent.SendUserPrompt -> onSendPrompt(event.prompt)
            is MainUiEvent.OpenFile -> onOpenFile(event.path)
            is MainUiEvent.SaveFile -> onSaveFile(event.path, event.content)
            is MainUiEvent.ApprovePendingAction -> onApproveAction(event.actionId)
            is MainUiEvent.RejectPendingAction -> onRejectAction(event.actionId)
            is MainUiEvent.ApplyDiff -> onApplyDiff(event.diff)
            is MainUiEvent.DiscardDiff -> _uiState.update { it.copy(pendingDiff = null) }
            is MainUiEvent.RunTerminalCommand -> onRunTerminalCommand(event.command)
            is MainUiEvent.RefreshPreview -> onRefreshPreview()
            is MainUiEvent.ToggleDrawer -> _uiState.update { it.copy(isDrawerOpen = event.open) }
            is MainUiEvent.ClearError -> _uiState.update { it.copy(errorBanner = null) }
        }
    }

    fun saveApiKey(providerId: String, apiKey: String) {
        credentialStore.storeCredential(providerId, apiKey)
    }

    private fun loadWorkspace() {
        val tree = indexWorkspaceUseCase()
        val defaultPreview = devServer.getIndexHtmlContent()
        _uiState.update {
            it.copy(
                fileTree = tree,
                previewHtml = defaultPreview
            )
        }
    }

    private fun observeTerminal() {
        viewModelScope.launch {
            terminalManager.sessions.collect { sessions ->
                _uiState.update { it.copy(terminalSessions = sessions) }
            }
        }
        viewModelScope.launch {
            terminalManager.activeSessionId.collect { activeId ->
                _uiState.update { it.copy(activeTerminalSessionId = activeId) }
            }
        }
    }

    private fun observeDevServer() {
        viewModelScope.launch {
            devServer.consoleLogs.collect { logs ->
                _uiState.update { it.copy(previewConsoleLogs = logs) }
            }
        }
    }

    private fun onSendPrompt(prompt: String) {
        if (prompt.isBlank()) return

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = prompt
        )
        val agentBubble = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.AGENT,
            content = "",
            isStreaming = true
        )

        _uiState.update {
            it.copy(
                chatMessages = it.chatMessages + userMsg + agentBubble,
                agentStatus = AgentStatus.THINKING,
                errorBanner = null
            )
        }

        viewModelScope.launch {
            executeAgentTaskUseCase(prompt, _uiState.value.activeFilePath).collect { event ->
                when (event) {
                    is AgentEvent.Thinking -> updateAgentMessage(event.content)
                    is AgentEvent.PlanUpdated -> _uiState.update { it.copy(currentPlan = event.steps) }
                    is AgentEvent.AwaitingApproval -> {
                        _uiState.update {
                            it.copy(
                                pendingAction = event.action,
                                agentStatus = AgentStatus.AWAITING_APPROVAL
                            )
                        }
                    }
                    is AgentEvent.FileDiffCreated -> {
                        _uiState.update {
                            it.copy(
                                pendingDiff = event.fileDiff,
                                activeTab = NavigationTab.GIT_DIFF
                            )
                        }
                    }
                    is AgentEvent.Completed -> {
                        _uiState.update {
                            it.copy(
                                agentStatus = AgentStatus.COMPLETED,
                                previewHtml = devServer.getIndexHtmlContent()
                            )
                        }
                        loadWorkspace()
                    }
                    is AgentEvent.Error -> {
                        _uiState.update {
                            it.copy(
                                agentStatus = AgentStatus.FAILED,
                                errorBanner = event.message,
                                pendingAction = null
                            )
                        }
                    }
                    is AgentEvent.StateChanged -> {
                        _uiState.update { it.copy(agentStatus = event.newStatus) }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun updateAgentMessage(chunk: String) {
        _uiState.update { state ->
            val msgs = state.chatMessages.toMutableList()
            val last = msgs.lastOrNull()
            if (last != null && last.role == MessageRole.AGENT) {
                msgs[msgs.size - 1] = last.copy(content = last.content + chunk, isStreaming = true)
            }
            state.copy(chatMessages = msgs)
        }
    }

    private fun onOpenFile(path: String) {
        try {
            val file = sandboxManager.resolveSafePath(path)
            if (file.exists() && file.isFile) {
                _uiState.update {
                    it.copy(
                        activeFilePath = path,
                        activeFileContent = file.readText(),
                        activeTab = NavigationTab.EDITOR,
                        isDrawerOpen = false
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorBanner = e.message) }
        }
    }

    private fun onSaveFile(path: String, content: String) {
        try {
            val file = sandboxManager.resolveSafePath(path)
            file.writeText(content)
            _uiState.update {
                it.copy(
                    activeFileContent = content,
                    previewHtml = devServer.getIndexHtmlContent()
                )
            }
            loadWorkspace()
        } catch (e: Exception) {
            _uiState.update { it.copy(errorBanner = e.message) }
        }
    }

    private fun onApproveAction(actionId: String) {
        _uiState.update { it.copy(pendingAction = null, agentStatus = AgentStatus.EXECUTING_TOOL) }
        agentOrchestrator.resolveApproval(actionId, approved = true)
    }

    private fun onRejectAction(actionId: String) {
        _uiState.update { it.copy(pendingAction = null, agentStatus = AgentStatus.IDLE) }
        agentOrchestrator.resolveApproval(actionId, approved = false)
    }

    private fun onApplyDiff(diff: FileDiff) {
        applyFileDiffUseCase(diff).onSuccess {
            _uiState.update {
                it.copy(
                    pendingDiff = null,
                    previewHtml = devServer.getIndexHtmlContent(),
                    activeTab = NavigationTab.PREVIEW
                )
            }
            loadWorkspace()
        }.onFailure { e ->
            _uiState.update { it.copy(errorBanner = "Failed to apply diff: ${e.message}") }
        }
    }

    private fun onRunTerminalCommand(command: String) {
        val activeId = _uiState.value.activeTerminalSessionId ?: return
        runTerminalCommandUseCase(activeId, command)
    }

    private fun onRefreshPreview() {
        _uiState.update { it.copy(previewHtml = devServer.getIndexHtmlContent()) }
    }
}
