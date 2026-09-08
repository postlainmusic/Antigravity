package com.antigravity.mobile.core.security

import com.antigravity.mobile.domain.model.TrustLevel
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

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
     * Enforces cross-platform normalization, UNC rejection, null-byte rejection, and directory boundary checks.
     * Throws SecurityException if a path traversal attempt is detected.
     */
    @Throws(SecurityException::class)
    fun resolveSafePath(relativePath: String): File {
        // 1. Iterative URL decoding (up to 3 passes to catch double encoding)
        var decoded = relativePath
        for (i in 0 until 3) {
            try {
                val next = URLDecoder.decode(decoded, StandardCharsets.UTF_8.name())
                if (next == decoded) break
                decoded = next
            } catch (e: Exception) {
                break
            }
        }

        // 2. Reject Null Bytes
        if (decoded.contains('\u0000')) {
            throw SecurityException("Sandbox violation: Null byte detected in path '$relativePath'")
        }

        // 3. Reject URL Schemes
        if (decoded.startsWith("file://", ignoreCase = true) ||
            decoded.startsWith("http://", ignoreCase = true) ||
            decoded.startsWith("https://", ignoreCase = true)
        ) {
            throw SecurityException("Sandbox violation: URL scheme detected in path '$relativePath'")
        }

        // 4. Reject UNC and Windows Network Paths
        if (decoded.startsWith("\\\\") || decoded.startsWith("//")) {
            throw SecurityException("Sandbox violation: UNC path escape detected in path '$relativePath'")
        }

        // 5. Cross-platform separator normalization (convert backslashes to standard forward slashes)
        var normalized = decoded.replace('\\', '/')

        // 6. Detect Windows drive letter absolute paths (e.g. C:/Windows) on all platforms
        if (normalized.matches(Regex("""^[a-zA-Z]:.*"""))) {
            throw SecurityException("Sandbox violation: Windows drive absolute path detected in '$relativePath'")
        }

        // 7. Check for root absolute paths
        val rootCanonical = workspaceRoot.canonicalFile
        val target = if (normalized.startsWith("/")) {
            // Absolute path: resolve directly
            File(normalized).canonicalFile
        } else {
            // Relative path: resolve against workspace root
            File(rootCanonical, normalized).canonicalFile
        }

        // 8. Strict directory boundary verification (eliminates Zip Slip sibling directory escapes)
        val rootPath = rootCanonical.path
        val targetPath = target.path
        val isInside = targetPath == rootPath || targetPath.startsWith(rootPath + File.separator) || targetPath.startsWith("$rootPath/")

        if (!isInside) {
            throw SecurityException("Sandbox violation: Path '$relativePath' resolves outside workspace root '$rootPath'")
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
