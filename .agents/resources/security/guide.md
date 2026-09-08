# Security & Sandbox Reference Guide

### What is this?
Security patterns for isolated execution, path traversal defense, secret protection, and token redaction.

### Path Sanitization Algorithm
```kotlin
fun sanitizeWorkspacePath(baseDir: File, userPath: String): File {
    val targetFile = File(baseDir, userPath).canonicalFile
    if (!targetFile.path.startsWith(baseDir.canonicalPath)) {
        throw SecurityException("Access denied: Path traversal attempted ($userPath)")
    }
    return targetFile
}
```

### Secret Redaction Filter
- Intercepts strings before saving to conversation history or streaming to the UI.
- Replaces API keys (`sk-...`, `AIza...`, `ghp_...`) with redacted markers (`[REDACTED_API_KEY]`).
