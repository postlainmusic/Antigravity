# Data Ownership, Privacy & Security Architecture

## 1. Data Residency & Boundaries

| Data Type | Residency Location | Shared with AI Provider? | Encryption Level |
| :--- | :--- | :---: | :--- |
| **Project Source Code** | On-Device Storage | Only when relevant to active prompt | Android OS Sandbox |
| **API Keys & Tokens** | Hardware KeyStore | **NEVER** | AES-256 GCM (EncryptedSharedPreferences) |
| **Chat Transcripts** | Local Room Database | Only active conversation window | AES-256 DB Encryption |
| **Terminal Output** | In-Memory / Local Log | **NEVER** | Redacted on emission |
| **Git Credentials** | Hardware KeyStore / SSH | **NEVER** | Hardware KeyStore |

---

## 2. Inviolable Privacy Principles

1. **Zero Silent Project Uploads**: The application never transmits workspace files to remote servers without explicit user request or prompt context requirement.
2. **Hardware-Backed Secret Storage**: API keys are encrypted using MasterKeys AES-256 backed by the Android hardware KeyStore.
3. **Automatic Secret Redaction**: All string streams emitted to the UI or written to logs are scrubbed of API keys (`sk-...`, `AIza...`, `ghp_...`, Bearer tokens).
4. **User Control**: Users can configure which directories are excluded from agent indexing via `.agents/rules/` and `.gitignore`.
