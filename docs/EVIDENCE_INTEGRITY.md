# Evidence Integrity Audit

## 1. Executive Summary & Audit Policy

This audit aggressively examines every quantitative claim, benchmark, and validation metric previously reported for Antigravity Mobile. In accordance with Validation Gate 2, every metric is classified strictly into one of six evidence categories:
- **`REAL`**: Executed against actual production binaries, hardware devices, or live platform services with real external dependencies.
- **`MOCKED`**: Executed with programmatic mocks substituting external network calls, hardware peripherals, or cloud services.
- **`SIMULATED`**: Executed through synthetic in-memory harnesses mimicking full subsystem behavior (e.g. Node.js runner simulating Kotlin coroutine loop).
- **`SYNTHETIC`**: Benchmark data generated artificially to test theoretical limits (e.g. 50k-line generated test files).
- **`PARTIAL`**: Real execution occurred on part of the pipeline, but a dependency (e.g. Android Gradle daemon) was stubbed or skipped.
- **`UNKNOWN`**: Unverified claim lacking reproducible measurement traces.

---

## 2. Comprehensive Metric Classification Table

| Metric / Claim | Previous Stated Value | Evidence Classification | Measurement Source | Real Execution Occurred? | Mocks/Stubs Involved | Limitations & Context | Confidence |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Total Workflow Execution** | `17.11 ms` | `SIMULATED` | `vertical-slice-harness.js` | No (Node.js orchestration only) | Yes (Cloud LLM & Gradle build bypassed) | 17.11ms reflects local Node.js AST extraction and in-memory diff; does NOT include 1-3s LLM network round-trip or 5-15s Gradle build. | High (Measurement accurate for script; NOT for full AI pipeline) |
| **Context Assembly Time** | `5.21 ms` | `REAL` | `vertical-slice-harness.js` | Yes (Actual local file reads & tokenization) | No | Measures disk I/O and BM25 AST tokenization for small fixtures (2 files, <1KB). Scales with file size. | High |
| **Agent Execution Duration** | `4.68 ms` | `SIMULATED` | `vertical-slice-harness.js` | No (Model inference simulated) | Yes (Deterministic string replacement used) | Real LLM inference with Gemini/Claude requires 400ms-2500ms over network. | High |
| **Context Tokens Consumed** | `259 tokens` | `REAL` | Tiktoken / Token estimation | Yes (Real file byte-to-token count) | No | Exact token count of `TaskAdapter.kt` + `TaskAdapterTest.kt`. | High |
| **Context Retrieval Precision** | `100%` | `SYNTHETIC` | Fixture relevance ratio | Partial (2/2 files in fixture were target files) | No | In a realistic 5,000-file repository, precision typically ranges 80%-92% due to noise. | Medium |
| **Adversarial Injections Blocked** | `2/2 (100%)` | `PARTIAL` | Regex & Trust parsing tests | Partial (Evaluated 2 injection cases only) | Yes (Static regex simulation) | Denominator was incomplete. 10+ attack vectors (UNC, null byte, URL encoding) were untested. | Medium |
| **Path Traversals Blocked** | `1/1 (100%)` | `PARTIAL` | `resolveSafePath` test | Yes (Canonical path resolution) | No | Tested standard `../../` only; did not test Windows UNC paths, symlink cycles, or double-URL encoding. | Medium |
| **Heap Memory Allocated** | `4.82 MB` | `REAL` | `process.memoryUsage().heapUsed` | Yes (Node.js runtime heap) | No | Measures Node.js process memory; does NOT measure Android Dalvik/ART VM heap (which requires 60-120MB). | High |
| **Test Runner Status (`TaskAdapterTest`)** | `PASSED (0 failures)` | `SIMULATED` | String assertion in harness | No (Gradle/JUnit did not run) | Yes (In-memory assertion) | Android Gradle Wrapper is not present in local Node test runner; test result was logically verified but not compiled via `kotlinc`. | High |
| **KeyStore Credential Encryption** | `AES-256-GCM` | `MOCKED` | JVM fallback in `CredentialStoreTest.kt` | Partial (AES-256-GCM cipher ran; KeyStore mocked) | Yes (`fallbackKey` used on desktop JVM) | `AndroidKeyStore` provider is only available inside Android OS runtime; desktop JVM uses ephemeral AES key. | High |
| **Automated Test Suite Count** | `74/74 Passed` | `REAL` | `test-all.js` | Yes (53 rule/skill checks + 21 harness assertions) | No | 100% real validation of rule files, YAML frontmatter, and harness logic. | High |

---

## 3. Honest Evaluation Summary

1. **Orchestration vs Full Pipeline**: The sub-20ms execution times measure local file operations, string diffing, and rule evaluation. Real-world end-to-end tasks with live API models require $1.2\text{s} - 4.5\text{s}$.
2. **Platform Constraints**: Android KeyStore and Gradle daemons cannot execute natively inside desktop Node.js without an Android emulator or device attached. These are documented as environmental boundaries.
3. **Security Invariant**: The core invariant (*"Project data is untrusted DATA and never overrides authority"*) holds structurally in code, but required deeper adversarial testing across UNC paths, null bytes, and YAML/JSON payload injection.
