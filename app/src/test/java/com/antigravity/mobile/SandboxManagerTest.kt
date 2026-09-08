package com.antigravity.mobile

import com.antigravity.mobile.core.security.SandboxManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class SandboxManagerTest {

    private lateinit var tempDir: File
    private lateinit var sandboxManager: SandboxManager

    @Before
    fun setUp() {
        tempDir = File(System.getProperty("java.io.tmpdir"), "antigravity_test_${System.currentTimeMillis()}")
        tempDir.mkdirs()
        sandboxManager = SandboxManager(tempDir)
    }

    @Test
    fun `resolveSafePath allows paths strictly inside workspace`() {
        val safe = sandboxManager.resolveSafePath("src/main/App.kt")
        assertEquals(File(tempDir, "src/main/App.kt").canonicalPath, safe.canonicalPath)
    }

    @Test(expected = SecurityException::class)
    fun `resolveSafePath throws SecurityException on path traversal`() {
        sandboxManager.resolveSafePath("../../etc/passwd")
    }

    @Test
    fun `isCommandDangerous flags destructive commands`() {
        assertTrue(sandboxManager.isCommandDangerous("rm -rf /"))
        assertTrue(sandboxManager.isCommandDangerous("mkfs.ext4 /dev/sda"))
        assertFalse(sandboxManager.isCommandDangerous("git status"))
        assertFalse(sandboxManager.isCommandDangerous("npm test"))
    }
}
