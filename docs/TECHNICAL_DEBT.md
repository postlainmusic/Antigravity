# Technical Debt & Architecture Refactoring Register

## 1. Active Technical Debt Register

| Debt ID | Subsystem | Description | Root Cause / Context | Risk / Impact | Remediation Plan | Priority | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **DEBT-001** | Model Platform | Hard-coded model IDs in provider classes | Initial quick prototype relied on static string matching | Adding new models requires modifying core engine classes | Implement `ModelDescriptor`, `ModelCapabilities`, and `ModelRouter` | High | In Progress |
| **DEBT-002** | Security | SharedPreferences used for storing test API keys | Prototype convenience | Plaintext token risk if device is rooted or backup inspected | Replace with `CredentialStore` + `AndroidKeyStore` AES-256-GCM | Critical | In Progress |
| **DEBT-003** | Editor | Basic `LazyColumn` without tokenized piece table buffer | Fast UI demonstration | Potential frame drops on very large 50k+ line files | Implement Viewport-aware rendering + background token caching | High | In Progress |
| **DEBT-004** | Terminal | Simulated local subshell in mock provider | Local Android PTY permissions vary across OEM kernels | Full Linux CLI tools require Termux or remote SSH host | Implement `TerminalBackend` abstraction (`Local`, `Remote`, `Sandboxed`) | Medium | In Progress |
| **DEBT-005** | Agent Engine | Unlimited self-healing loops on syntax errors | Naive retry loop without token quota | Risk of runaway API cost or infinite hallucination loops | Enforce 3-attempt retry budget, classification, & atomic rollback | High | In Progress |
| **DEBT-006** | Preview | Unrestricted file access flag on WebView settings | Standard Android WebView default | Malicious webpage script could read local application sandbox files | Enforce `allowFileAccess=false` and origin lock to `127.0.0.1` | High | In Progress |

---

## 2. Technical Debt Governance Policy

1. **Debt Cap**: No phase may proceed if there are unresolved `Critical` technical debts.
2. **Refactoring Budget**: 20% of every engineering cycle is allocated specifically to debt remediation and architecture hardening.
3. **Automated Audits**: `node .agents/scripts/project-health-check.js` scans and verifies the technical debt register against open codebase items.
