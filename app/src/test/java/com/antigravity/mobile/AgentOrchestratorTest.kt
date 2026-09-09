package com.antigravity.mobile

import com.antigravity.mobile.core.agent.AgentOrchestrator
import com.antigravity.mobile.core.agent.ContextEngine
import com.antigravity.mobile.core.agent.DiffManager
import com.antigravity.mobile.core.agent.PermissionManager
import com.antigravity.mobile.core.model.GeminiModelProvider
import com.antigravity.mobile.core.model.ModelRegistry
import com.antigravity.mobile.core.model.ModelRouter
import com.antigravity.mobile.core.security.SandboxManager
import com.antigravity.mobile.core.tools.ToolRegistry
import com.antigravity.mobile.domain.model.AgentEvent
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class AgentOrchestratorTest {

    private lateinit var tempDir: File
    private lateinit var sandboxManager: SandboxManager
    private lateinit var modelRouter: ModelRouter
    private lateinit var toolRegistry: ToolRegistry
    private lateinit var contextEngine: ContextEngine
    private lateinit var permissionManager: PermissionManager
    private lateinit var diffManager: DiffManager
    private lateinit var orchestrator: AgentOrchestrator

    @Before
    fun setUp() {
        tempDir = File(System.getProperty("java.io.tmpdir"), "orch_test_${System.currentTimeMillis()}").apply { mkdirs() }
        sandboxManager = SandboxManager(tempDir)
        val registry = ModelRegistry().apply {
            register(GeminiModelProvider { "mock-key" })
        }
        modelRouter = ModelRouter(registry)
        toolRegistry = ToolRegistry(sandboxManager)
        contextEngine = ContextEngine(sandboxManager)
        permissionManager = PermissionManager()
        diffManager = DiffManager()

        orchestrator = AgentOrchestrator(
            modelRouter = modelRouter,
            toolRegistry = toolRegistry,
            contextEngine = contextEngine,
            permissionManager = permissionManager,
            diffManager = diffManager
        )
    }

    @Test
    fun testOrchestratorExecutionEmitsEvents() = runTest {
        val events = orchestrator.executeTask("explore workspace").toList()
        assertTrue(events.isNotEmpty())
        assertTrue(events.any { it is AgentEvent.Started })
        assertTrue(events.any { it is AgentEvent.PlanUpdated })
        assertTrue(events.any { it is AgentEvent.Completed || it is AgentEvent.ToolCompleted })
    }
}
