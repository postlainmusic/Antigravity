package com.antigravity.mobile.core.security

object SecretScrubber {

    private val secretPatterns = listOf(
        Regex("""sk-[a-zA-Z0-9]{32,}"""),                     // OpenAI Keys
        Regex("""AIza[0-9A-Za-z-_]{35}"""),                  // Google API Keys
        Regex("""gh[pousr]_[0-9a-zA-Z]{36}"""),              // GitHub Tokens
        Regex("""Bearer\s+[a-zA-Z0-9_\-\.]{20,}"""),         // Authorization Bearer
        Regex("""(?i)password\s*=\s*['"][^'"]+['"]"""),      // Generic Passwords
        Regex("""(?i)secret\s*=\s*['"][^'"]+['"]""")         // Generic Secrets
    )

    /**
     * Sanitizes strings before emitting to UI event streams or persisting to disk.
     */
    fun scrub(input: String): String {
        var result = input
        for (pattern in secretPatterns) {
            result = pattern.replace(result) { match ->
                val full = match.value
                if (full.startsWith("Bearer ", ignoreCase = true)) {
                    "Bearer [REDACTED_TOKEN]"
                } else if (full.length > 8) {
                    "${full.take(4)}...[REDACTED]...${full.takeLast(3)}"
                } else {
                    "[REDACTED_SECRET]"
                }
            }
        }
        return result
    }
}
