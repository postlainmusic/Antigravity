# 07 - Security & Sandbox Guidelines

## Security Rules
1. **Workspace Sandboxing**:
   - Strictly prohibit path traversal outside the active project root (`../` path normalization checks).
   - Reject attempts to read or modify system files, OS registries, or private user credentials.
2. **Secret & Key Protection**:
   - API keys and tokens must be stored in Android EncryptedSharedPreferences or KeyStore.
   - Automatically redact secrets, tokens, and authorization headers from logs, UI event streams, and error diagnostics.
3. **Execution Guardrails**:
   - Filter dangerous shell commands (`rm -rf /`, fork bombs, format commands, unauthorized curl exfiltration).
   - Prompt user confirmation before executing any destructive operations.
