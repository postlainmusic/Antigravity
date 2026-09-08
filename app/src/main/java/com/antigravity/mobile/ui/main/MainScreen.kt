package com.antigravity.mobile.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antigravity.mobile.ui.agent.AgentChatPanel
import com.antigravity.mobile.ui.components.AppTopBar
import com.antigravity.mobile.ui.components.FileTreeDrawerContent
import com.antigravity.mobile.ui.components.PermissionDialog
import com.antigravity.mobile.ui.editor.CodeEditorScreen
import com.antigravity.mobile.ui.git.GitDiffScreen
import com.antigravity.mobile.ui.preview.PreviewScreen
import com.antigravity.mobile.ui.terminal.TerminalScreen
import com.antigravity.mobile.ui.theme.DarkBorder
import com.antigravity.mobile.ui.theme.DarkSurface
import com.antigravity.mobile.ui.theme.DarkSurfaceElevated
import com.antigravity.mobile.ui.theme.DeepObsidian
import com.antigravity.mobile.ui.theme.IndigoGlow
import com.antigravity.mobile.ui.theme.TextMuted
import com.antigravity.mobile.ui.theme.TextPrimary
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.isDrawerOpen) {
        if (uiState.isDrawerOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FileTreeDrawerContent(
                fileTree = uiState.fileTree,
                onFileSelected = { filePath ->
                    viewModel.handleIntent(MainUiEvent.OpenFile(filePath))
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    projectName = uiState.currentProject?.name ?: "Antigravity",
                    onToggleDrawer = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    }
                )
            },
            bottomBar = {
                AntigravityBottomNav(
                    activeTab = uiState.activeTab,
                    onSelectTab = { viewModel.handleIntent(MainUiEvent.SelectTab(it)) }
                )
            },
            containerColor = DeepObsidian,
            modifier = modifier.fillMaxSize()
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (uiState.activeTab) {
                    NavigationTab.AGENT -> AgentChatPanel(
                        messages = uiState.chatMessages,
                        planSteps = uiState.currentPlan,
                        agentStatus = uiState.agentStatus,
                        onSendMessage = { prompt ->
                            viewModel.handleIntent(MainUiEvent.SendUserPrompt(prompt))
                        }
                    )
                    NavigationTab.EDITOR -> CodeEditorScreen(
                        filePath = uiState.activeFilePath,
                        initialContent = uiState.activeFileContent,
                        onSaveFile = { path, content ->
                            viewModel.handleIntent(MainUiEvent.SaveFile(path, content))
                        }
                    )
                    NavigationTab.PREVIEW -> PreviewScreen(
                        htmlContent = uiState.previewHtml,
                        consoleLogs = uiState.previewConsoleLogs,
                        onRefresh = { viewModel.handleIntent(MainUiEvent.RefreshPreview()) }
                    )
                    NavigationTab.TERMINAL -> TerminalScreen(
                        session = uiState.terminalSessions.find { it.id == uiState.activeTerminalSessionId },
                        onExecuteCommand = { cmd ->
                            viewModel.handleIntent(MainUiEvent.RunTerminalCommand(cmd))
                        }
                    )
                    NavigationTab.GIT_DIFF -> GitDiffScreen(
                        diff = uiState.pendingDiff,
                        onApplyDiff = { diff -> viewModel.handleIntent(MainUiEvent.ApplyDiff(diff)) },
                        onDiscardDiff = { diff -> viewModel.handleIntent(MainUiEvent.DiscardDiff(diff)) }
                    )
                }

                // Gated User Permission Dialog if action requires approval
                uiState.pendingAction?.let { action ->
                    PermissionDialog(
                        action = action,
                        onApprove = { viewModel.handleIntent(MainUiEvent.ApprovePendingAction(it)) },
                        onReject = { viewModel.handleIntent(MainUiEvent.RejectPendingAction(it)) }
                    )
                }
            }
        }
    }
}

@Composable
fun AntigravityBottomNav(
    activeTab: NavigationTab,
    onSelectTab: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = DarkSurface,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder)
    ) {
        NavigationBarItem(
            selected = activeTab == NavigationTab.AGENT,
            onClick = { onSelectTab(NavigationTab.AGENT) },
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Agent") },
            label = { Text("Agent") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoGlow,
                selectedTextColor = IndigoGlow,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = DarkSurfaceElevated
            )
        )
        NavigationBarItem(
            selected = activeTab == NavigationTab.EDITOR,
            onClick = { onSelectTab(NavigationTab.EDITOR) },
            icon = { Icon(Icons.Default.Code, contentDescription = "Editor") },
            label = { Text("Editor") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoGlow,
                selectedTextColor = IndigoGlow,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = DarkSurfaceElevated
            )
        )
        NavigationBarItem(
            selected = activeTab == NavigationTab.PREVIEW,
            onClick = { onSelectTab(NavigationTab.PREVIEW) },
            icon = { Icon(Icons.Default.PlayCircle, contentDescription = "Preview") },
            label = { Text("Preview") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoGlow,
                selectedTextColor = IndigoGlow,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = DarkSurfaceElevated
            )
        )
        NavigationBarItem(
            selected = activeTab == NavigationTab.TERMINAL,
            onClick = { onSelectTab(NavigationTab.TERMINAL) },
            icon = { Icon(Icons.Default.Terminal, contentDescription = "Terminal") },
            label = { Text("Terminal") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoGlow,
                selectedTextColor = IndigoGlow,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = DarkSurfaceElevated
            )
        )
        NavigationBarItem(
            selected = activeTab == NavigationTab.GIT_DIFF,
            onClick = { onSelectTab(NavigationTab.GIT_DIFF) },
            icon = { Icon(Icons.Default.Difference, contentDescription = "Diff") },
            label = { Text("Git") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoGlow,
                selectedTextColor = IndigoGlow,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = DarkSurfaceElevated
            )
        )
    }
}
