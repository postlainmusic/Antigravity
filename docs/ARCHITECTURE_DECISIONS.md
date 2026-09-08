# Architecture Decision Records (ADR)

This document records the formal Architecture Decision Records (ADR) governing the design, security, and execution model of the Antigravity Mobile system.

---

## ADR-001: 100% Native Jetpack Compose UI Stack
- **Problem**: Hybrid web frameworks (Cordova, Capacitor, Flutter, React Native) introduce runtime bridges, gesture lag, memory bloat, and poor integration with Android touch APIs.
- **Context**: Mobile code editing requires instantaneous 120Hz gesture response, precise text cursor placement, and low memory overhead.
- **Options**:
  1. Hybrid WebView wrapper (VS Code / Theia Web in WebView).
  2. Cross-platform framework (Flutter / React Native).
  3. Native Kotlin with Jetpack Compose Material 3.
- **Decision**: Build the application using 100% native Kotlin and Jetpack Compose with Material 3 tokens.
- **Consequences**: Flawless 60/120fps UI performance, native virtual keyboard integration, direct KeyStore access, minimal battery consumption.
- **Reversal Conditions**: If cross-platform iOS support becomes mandatory without Kotlin Multiplatform capability.

---

## ADR-002: Capability-Based Multi-Model Routing Engine
- **Problem**: Hardcoding model names (e.g., `gemini-1.5-pro`, `gpt-4o`) creates brittle code that breaks when models are updated or deprecated.
- **Context**: Different engineering tasks require different model strengths (e.g., high reasoning for architecture vs low latency for inline completion vs offline privacy for local development).
- **Options**:
  1. Single vendor SDK lock-in.
  2. Hardcoded string-based provider map.
  3. Dynamic capability-based router (`ModelCapabilities`, `ModelRouter`, `ModelRegistry`).
- **Decision**: Implement a decoupled capability-based model abstraction where tasks request capability flags (e.g., `coding`, `reasoning`, `toolUse`, `offline`) and the router selects the best registered provider.
- **Consequences**: Zero vendor lock-in, effortless addition of new models (e.g. Gemini 2.0, Claude 3.5), dynamic fallback during API outages.
- **Reversal Conditions**: None; capability abstraction is strictly superior.

---

## ADR-003: Hardware-Backed CredentialStore Isolation
- **Problem**: Storing API keys in plaintext or standard `SharedPreferences` exposes secrets to rooted devices, backup extractions, or context leaks.
- **Context**: User API credentials must be secured at rest and scrubbed from all LLM context buffers, logs, and Git commits.
- **Options**:
  1. Plain `SharedPreferences`.
  2. AndroidX `EncryptedSharedPreferences` (deprecated/brittle in some OEM Android builds).
  3. Abstract `CredentialStore` interface backed by Android KeyStore with AES-256-GCM encryption.
- **Decision**: Implement an abstract `CredentialStore` interface backed by Android KeyStore master keys and AES-256-GCM encryption.
- **Consequences**: Military-grade encryption at rest, hardware-backed security, zero secret leakage into transcripts.
- **Reversal Conditions**: If running in a test harness without Android KeyStore, fallback to an encrypted in-memory test provider.

---

## ADR-004: Six-Tier Defense-in-Depth Security Sandbox
- **Problem**: AI agents executing file writes and shell commands can be manipulated by malicious workspace files or prompt injections.
- **Context**: Claiming "absolute security" is unscientific; security requires measurable boundaries and defense-in-depth.
- **Options**:
  1. Trust all LLM tool calls.
  2. Tag untrusted files with prompt markers only.
  3. Enforce 6 strict isolation boundaries (Filesystem, Command, Network, Process, Credential, MCP) with explicit trust levels and path canonicalization.
- **Decision**: Implement the 6-tier security sandbox with canonical path resolution, command token validation, and taint propagation.
- **Consequences**: Complete containment of untrusted code execution; prompt injections cannot escape the sandbox.
- **Reversal Conditions**: None.

---

## ADR-005: Viewport-Aware Code Editor Architecture
- **Problem**: Naive text renderers lay out the entire file in memory, causing UI freezes and OOM crashes on large files (50,000+ lines).
- **Context**: Mobile code editors must scroll smoothly and allow rapid cursor placement on any file size.
- **Options**:
  1. Monolithic `BasicTextField` / `TextView`.
  2. Embedded Monaco editor in WebView.
  3. Viewport-aware Compose line virtualizer backed by background tokenization.
- **Decision**: Implement a dedicated editor subsystem that only lays out visible viewport lines (+ buffer), with syntax highlighting computed on background coroutines.
- **Consequences**: O(1) memory overhead, consistent 60/120fps scrolling, instant file opening.
- **Reversal Conditions**: None.

---

## ADR-006: Deterministic 14-State Agent Finite State Machine
- **Problem**: Ad-hoc boolean flags for agent execution lead to race conditions, orphaned background processes, and unrecoverable error states.
- **Context**: Agent tasks involve multi-turn tool loops, user approval gates, cancellation, and self-healing.
- **Options**:
  1. Unstructured coroutine jobs with mutable boolean flags.
  2. Deterministic 14-state FSM with explicit transition matrix and event stream.
- **Decision**: Model the agent runtime as a formal 14-state FSM (`IDLE`, `THINKING`, `PLANNING`, `EXECUTING_TOOL`, `AWAITING_APPROVAL`, `SELF_HEALING`, `COMPLETED`, `CANCELLED`, `FAILED`, `PAUSED`, `WAITING_FOR_RESOURCE`, `WAITING_FOR_USER`, `RECOVERING`, `PARTIAL_SUCCESS`).
- **Consequences**: Zero invalid state transitions, complete auditability, deterministic cancellation and pause/resume.
- **Reversal Conditions**: None.

---

## ADR-007: Bounded Self-Healing and Multi-Tier Rollback
- **Problem**: When an agent encounters build or syntax errors, unbounded self-healing can cause runaway token costs, infinite loops, and repository corruption.
- **Context**: Automated repair must be bounded, classified, and reversible.
- **Options**:
  1. Single attempt then fail.
  2. Unbounded retry loop.
  3. Bounded self-healing (max 3 attempts, token quota, failure classification) with multi-tier rollback (file snapshot, patch reversion, git rollback).
- **Decision**: Enforce a strict 3-attempt budget with failure classification (Syntax, Build, Logic, Permission) and automatic atomic rollback on failure.
- **Consequences**: Predictable API spending, zero repository corruption, automated error recovery for common typos.
- **Reversal Conditions**: None.

---

## ADR-008: Terminal Execution Backend Abstraction
- **Problem**: Android OS restricts root access and PTY allocation; different deployment targets (local Termux, remote SSH, sandboxed container) have different capabilities.
- **Context**: Terminal operations must work seamlessly across local devices, remote development VMs, and sandboxed emulators.
- **Options**:
  1. Hardcode local `Runtime.getRuntime().exec()`.
  2. Implement `TerminalBackend` interface (`LocalBackend`, `RemoteBackend`, `SandboxedBackend`).
- **Decision**: Implement the `TerminalBackend` interface, allowing dynamic switching between local Android execution, remote SSH workstations, and sandboxed runners.
- **Consequences**: Clean separation of terminal concerns; transparent remote dev server support.
- **Reversal Conditions**: None.

---

## ADR-009: Sandboxed Web Preview with Isolated Message Bridge
- **Problem**: Running user web apps inside an embedded WebView can expose native Android APIs to malicious or vulnerable client-side JavaScript.
- **Context**: Mobile developers building web applications need a live preview with console log capture without compromising device security.
- **Options**:
  1. Standard WebView with full JavaScript interface injection.
  2. Local dev server bound to `127.0.0.1` with sandboxed WebView, disabled file access, and typed JSON-RPC message bridge.
- **Decision**: Isolate the preview inside a sandboxed WebView bound strictly to local loopback ports with disabled file access and strict message validation.
- **Consequences**: Safe live web preview with zero native API exposure.
- **Reversal Conditions**: None.

---

## ADR-010: Weighted Multi-Metric Golden Task Benchmark
- **Problem**: Binary pass/fail assertions fail to measure subtle agent regressions, token inefficiency, or security violations.
- **Context**: AI agent evaluation requires measuring functional correctness, tool precision, safety adherence, and token economy.
- **Options**:
  1. Unit tests only.
  2. Binary golden task pass/fail.
  3. Weighted multi-dimensional rubric ($30\%$ correctness, $25\%$ security, $15\%$ tool accuracy, $10\%$ token efficiency, $10\%$ action economy, $5\%$ latency, $5\%$ regression) with critical severity gating.
- **Decision**: Implement the weighted evaluation framework across 10 realistic benchmark tasks with adversarial traps.
- **Consequences**: Objective, regression-proof quality gating for every AI model and agent update.
- **Reversal Conditions**: None.
