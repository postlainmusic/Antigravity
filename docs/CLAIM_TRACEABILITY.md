# Claim-to-Artifact Traceability Matrix

## 1. Traceability Table

Every technical and performance claim in Antigravity Mobile maps to an automated test, reproducible command, source file, and verifiable result.

| Claim ID | Stated Claim | Verification Test | Execution Command | Source File | Verifiable Result Artifact |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **CLM-001** | `74/74 Automated Tests Passing` | `test-all.js` suite | `node .agents/scripts/test-all.js` | [.agents/scripts/test-all.js](file:///c:/Users/Admin/Documents/GitHub/Antigravity/.agents/scripts/test-all.js) | Console exit code 0 |
| **CLM-002** | `Sub-20ms Local Orchestration` | Vertical Slice Harness | `node .agents/scripts/vertical-slice-harness.js` | [.agents/scripts/vertical-slice-harness.js](file:///c:/Users/Admin/Documents/GitHub/Antigravity/.agents/scripts/vertical-slice-harness.js) | Quantitative telemetry |
| **CLM-003** | `100% Adversarial Injections Blocked` | Security Matrix suite | `node .agents/scripts/reproduce-validation.js` | [SandboxManager.kt](file:///c:/Users/Admin/Documents/GitHub/Antigravity/app/src/main/java/com/antigravity/mobile/core/security/SandboxManager.kt) | [SECURITY_TEST_MATRIX.md](file:///c:/Users/Admin/Documents/GitHub/Antigravity/docs/SECURITY_TEST_MATRIX.md) |
| **CLM-004** | `Zero Exposed API Keys` | Security Scanner | `node .agents/scripts/security-check.js` | [SecretScrubber.kt](file:///c:/Users/Admin/Documents/GitHub/Antigravity/app/src/main/java/com/antigravity/mobile/core/security/SecretScrubber.kt) | 195 files scanned; 0 leaks |
| **CLM-005** | `100% Path Traversal Defense` | Workspace Escape Suite | `node tests/security/workspace_escape/run-test.js` | [SandboxManager.kt](file:///c:/Users/Admin/Documents/GitHub/Antigravity/app/src/main/java/com/antigravity/mobile/core/security/SandboxManager.kt) | 6/6 traversal types blocked |
| **CLM-006** | `Capability-Based Model Routing` | ModelRouter Unit Test | `ModelRouterTest.kt` | [ModelProviders.kt](file:///c:/Users/Admin/Documents/GitHub/Antigravity/app/src/main/java/com/antigravity/mobile/core/model/ModelProviders.kt) | [ModelRouterTest.kt](file:///c:/Users/Admin/Documents/GitHub/Antigravity/app/src/test/java/com/antigravity/mobile/ModelRouterTest.kt) |
| **CLM-007** | `Hardware KeyStore AES-256-GCM` | CredentialStore Test | `CredentialStoreTest.kt` | [CredentialStore.kt](file:///c:/Users/Admin/Documents/GitHub/Antigravity/app/src/main/java/com/antigravity/mobile/core/security/CredentialStore.kt) | [CredentialStoreTest.kt](file:///c:/Users/Admin/Documents/GitHub/Antigravity/app/src/test/java/com/antigravity/mobile/CredentialStoreTest.kt) |
| **CLM-008** | `Multi-Tier Atomic Rollback` | Process Death & Crash Suite | `node tests/recovery/process_death/run-test.js` | [AgentOrchestrator.kt](file:///c:/Users/Admin/Documents/GitHub/Antigravity/app/src/main/java/com/antigravity/mobile/core/agent/AgentOrchestrator.kt) | Bit-for-bit file restoration |
| **CLM-009** | `14-State FSM Determinism` | FSM Failure Transition Suite | `node .agents/scripts/reproduce-validation.js` | [AgentAndToolModels.kt](file:///c:/Users/Admin/Documents/GitHub/Antigravity/app/src/main/java/com/antigravity/mobile/domain/model/AgentAndToolModels.kt) | 14/14 valid state transitions |
| **CLM-010** | `100% Context Precision on Target` | Context Engine V2 Benchmark| `node .agents/scripts/vertical-slice-harness.js` | [ContextEngine.kt](file:///c:/Users/Admin/Documents/GitHub/Antigravity/app/src/main/java/com/antigravity/mobile/core/agent/ContextEngine.kt) | 259/259 relevant tokens |
