package com.antigravity.mobile

import com.antigravity.mobile.core.security.SandboxManager
import com.antigravity.mobile.core.tools.ToolRegistry
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class ToolRegistryTest {

    private lateinit var tempDir: File
    private lateinit var sandboxManager: SandboxManager
    private lateinit var toolRegistry: ToolRegistry

    @Before
    fun setUp() {
        tempDir = File(System.getProperty("java.io.tmpdir"), "tool_reg_test_${System.currentTimeMillis()}")
        tempDir.mkdirs()
        sandboxManager = SandboxManager(tempDir)
        toolRegistry = ToolRegistry(sandboxManager)
    }

    @Test
    fun `tool registry discovers default core tools`() {
        val defs = toolRegistry.getAllDefinitions()
        assertTrue(defs.any { it.name == "read_file" })
        assertTrue(defs.any { it.name == "write_file" })
        assertTrue(defs.any { it.name == "replace_file_content" })
        assertTrue(defs.any { it.name == "list_dir" })
        assertTrue(defs.any { it.name == "grep_search" })
    }

    @Test
    fun `write_file and read_file cycle works in sandbox`() = runTest {
        val writeResult = toolRegistry.execute(
            callId = "call_1",
            toolName = "write_file",
            argsJson = """{"targetFile":"hello.txt","codeContent":"Hello Antigravity!"}"""
        )
        assertFalse(writeResult.isError)

        val readResult = toolRegistry.execute(
            callId = "call_2",
            toolName = "read_file",
            argsJson = """{"filePath":"hello.txt"}"""
        )
        assertFalse(readResult.isError)
        assertTrue(readResult.output.contains("Hello Antigravity!"))
    }
}
