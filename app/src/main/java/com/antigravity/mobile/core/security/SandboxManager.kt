package com.antigravity.mobile.core.security

import com.antigravity.mobile.domain.model.TrustLevel
import java.io.File

class SandboxManager(private val workspaceRoot: File) {

    private val blacklistedCommands = listOf(
        Regex("""rm\s+-[rf]{1,2}\s+[/~]"""),
        Regex("""mkfs(\.\w+)?"""),
        Regex(""":\(\)\{\s*:\|:&\s*\};:"""), // Fork bomb
        Regex("""dd\s+if=.*of=/dev/"""),
        Regex("""chmod\s+-R\s+777\s+/"""),
        Regex("""curl\s+.*\|\s*(ba)?sh"""),
        Regex("""wget\s+.*\|\s*(ba)?sh""")
    )

    init {
        if (!workspaceRoot.exists()) {
            workspaceRoot.mkdirs()
        }
    }

    /**
     * Resolves and verifies that a target path is strictly contained inside the workspace root.
     * Throws SecurityException if a path traversal attempt is detected.
     */
    @Throws(SecurityException::class)
    fun resolveSafePath(relativePath: String): File {
        val rootCanonical = workspaceRoot.canonicalFile
        val target = File(rootCanonical, relativePath).canonicalFile

        if (!target.path.startsWith(rootCanonical.path)) {
            throw SecurityException("Sandbox violation: Path '$relativePath' resolves outside workspace root '$rootCanonical'")
        }
        return target
    }

    /**
     * Inspects a command line string for malicious or destructive commands.
     */
    fun isCommandDangerous(command: String): Boolean {
        val trimmed = command.trim()
        return blacklistedCommands.any { it.containsMatchIn(trimmed) }
    }

    /**
     * Evaluates trust metadata and prevents lower-trust input from overriding higher-trust policy.
     */
    fun validateTrustHierarchy(sourceTrust: TrustLevel, requiredTrust: TrustLevel): Boolean {
        return sourceTrust.ordinal <= requiredTrust.ordinal
    }
}
