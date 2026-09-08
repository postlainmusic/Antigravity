# Security Policy & Sandbox Architecture

## 1. Threat Model & Boundaries
- **Untrusted Projects**: A cloned repository or downloaded workspace could contain malicious scripts or path traversal attempts.
- **Untrusted AI Outputs**: Generated code or tool arguments could attempt system modifications or sensitive data exfiltration.
- **Exposed Credentials**: Accidental inclusion of API keys or user tokens in conversation transcripts, UI event streams, or logs.

---

## 2. Sandbox Defense Layers

### Layer 1: Strict Workspace Canonical Path Containment
All file operations (`readFile`, `writeFile`, `listDir`, `deleteFile`) normalize paths and verify:
```kotlin
if (!canonicalTarget.startsWith(workspaceCanonicalRoot)) {
    throw SecurityException("Security violation: path traversal prohibited")
}
```

### Layer 2: Permission Gating Tiers
- **SAFE**: Read files, search AST symbols, check git status, run static analyzer. (Auto-executed).
- **MODERATE**: Create files, edit source code, run unit test runner, install packages. (Policy controlled).
- **DANGEROUS**: Delete files, force-push git branches, run arbitrary shell commands. (Requires explicit user touch confirmation).

### Layer 3: Secret Redaction
All string streams passed to the UI or logs pass through a regex scrubber masking:
- OpenAI (`sk-...`)
- Google Gemini (`AIza...`)
- GitHub Tokens (`ghp_...`, `gho_...`)
- AWS / GCP Service Account Keys
- Bearer Authorization Headers

### Layer 4: Secure Key Storage
API keys and authentication tokens are encrypted using the Android KeyStore provider with AES-256 GCM encryption via `EncryptedSharedPreferences`.
