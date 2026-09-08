---
name: security-engineering
description: >-
  Audits credentials, manages secure key storage, sanitizes outputs, redacts secrets from logs, and prevents injection attacks and data leakage.
---

# Security Engineering Skill

## Purpose
Protects user credentials, API keys, private source code, and runtime data against exfiltration, unauthorized access, and malicious dependencies.

## Key Protocols
1. **API Key & Token Management**:
   - Secure storage using Android KeyStore and hardware-backed encryption.
   - Zero hardcoded secrets in source code or version control.
2. **Secret Redaction Pipeline**:
   - Regex interceptor filtering API keys (`sk-...`, `AIza...`, `ghp_...`, Bearer tokens) before saving to disk or emitting to UI/logs.
3. **Dependency & Supply Chain Security**:
   - Verify package integrity and block vulnerable dependencies during automated package installation.
4. **Input Sanitization**:
   - Sanitize all terminal inputs and model outputs before execution to prevent command injection and template breakout.
