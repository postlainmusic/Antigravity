package com.antigravity.mobile.core.di

import com.antigravity.mobile.core.agent.AgentOrchestrator
import com.antigravity.mobile.core.agent.ContextEngine
import com.antigravity.mobile.core.agent.DiffManager
import com.antigravity.mobile.core.agent.PermissionManager
import com.antigravity.mobile.core.model.ClaudeModelProvider
import com.antigravity.mobile.core.model.GeminiModelProvider
import com.antigravity.mobile.core.model.LocalModelProvider
import com.antigravity.mobile.core.model.ModelRegistry
import com.antigravity.mobile.core.model.ModelRouter
import com.antigravity.mobile.core.model.OpenAiModelProvider
import com.antigravity.mobile.core.preview.LocalDevServer
import com.antigravity.mobile.core.security.CredentialStore
import com.antigravity.mobile.core.security.KeyStoreCredentialStore
import com.antigravity.mobile.core.security.SandboxManager
import com.antigravity.mobile.core.terminal.TerminalManager
import com.antigravity.mobile.core.tools.ToolRegistry
import com.antigravity.mobile.domain.usecase.ApplyFileDiffUseCase
import com.antigravity.mobile.domain.usecase.ExecuteAgentTaskUseCase
import com.antigravity.mobile.domain.usecase.IndexWorkspaceUseCase
import com.antigravity.mobile.domain.usecase.RunTerminalCommandUseCase
import java.io.File

interface AppContainer {
    val sandboxManager: SandboxManager
    val credentialStore: CredentialStore
    val modelRegistry: ModelRegistry
    val modelRouter: ModelRouter
    val toolRegistry: ToolRegistry
    val contextEngine: ContextEngine
    val permissionManager: PermissionManager
    val diffManager: DiffManager
    val terminalManager: TerminalManager
    val devServer: LocalDevServer
    val agentOrchestrator: AgentOrchestrator
    val executeAgentTaskUseCase: ExecuteAgentTaskUseCase
    val applyFileDiffUseCase: ApplyFileDiffUseCase
    val indexWorkspaceUseCase: IndexWorkspaceUseCase
    val runTerminalCommandUseCase: RunTerminalCommandUseCase
}

class DefaultAppContainer(
    workspaceDir: File = File(".")
) : AppContainer {
    override val sandboxManager: SandboxManager = SandboxManager(workspaceDir)
    override val credentialStore: CredentialStore = KeyStoreCredentialStore()

    override val modelRegistry: ModelRegistry = ModelRegistry().apply {
        register(GeminiModelProvider { credentialStore.getCredential("gemini") ?: "" })
        register(ClaudeModelProvider { credentialStore.getCredential("claude") ?: "" })
        register(OpenAiModelProvider { credentialStore.getCredential("openai") ?: "" })
        register(LocalModelProvider())
    }

    override val modelRouter: ModelRouter = ModelRouter(modelRegistry)
    override val toolRegistry: ToolRegistry = ToolRegistry(sandboxManager)
    override val contextEngine: ContextEngine = ContextEngine(sandboxManager)
    override val permissionManager: PermissionManager = PermissionManager()
    override val diffManager: DiffManager = DiffManager()

    override val agentOrchestrator: AgentOrchestrator = AgentOrchestrator(
        modelRouter = modelRouter,
        toolRegistry = toolRegistry,
        contextEngine = contextEngine,
        permissionManager = permissionManager,
        diffManager = diffManager
    )

    override val terminalManager: TerminalManager = TerminalManager(sandboxManager)
    override val devServer: LocalDevServer = LocalDevServer(sandboxManager)

    override val executeAgentTaskUseCase: ExecuteAgentTaskUseCase = ExecuteAgentTaskUseCase(agentOrchestrator)
    override val applyFileDiffUseCase: ApplyFileDiffUseCase = ApplyFileDiffUseCase(sandboxManager)
    override val indexWorkspaceUseCase: IndexWorkspaceUseCase = IndexWorkspaceUseCase(sandboxManager)
    override val runTerminalCommandUseCase: RunTerminalCommandUseCase = RunTerminalCommandUseCase(terminalManager)
}
