# Antigravity System Architecture Specification

## 1. Executive Overview

Antigravity Mobile is a native Android AI-powered software development environment designed to provide mobile developers with autonomous, high-precision code engineering capabilities. The architecture follows a strict decoupled multi-tier design separating the mobile presentation layer, autonomous domain orchestration, capability-based AI model routing, sandboxed security enforcement, and modular tool/MCP integrations.

```
┌───────────────────────────────────────────────────────────────────────────────┐
│                           PRESENTATION LAYER (Jetpack Compose)                 │
│  Dark Obsidian Theme | Viewport-Aware Code Editor | MVI MainViewModel         │
│                                                                               │
│  ┌──────────────────┐ ┌──────────────────┐ ┌────────────────┐ ┌─────────────┐ │
│  │ Agent Chat Panel │ │ Code Editor (V2) │ │ Terminal View  │ │ Web Preview │ │
│  │ & Plan Stream    │ │ Viewport Virtual │ │ ANSI / PTY     │ │ Sandboxed   │ │
│  └────────▲─────────┘ └────────▲─────────┘ └───────▲────────┘ └──────▲──────┘ │
│           │                    │                   │                 │        │
│  ┌────────┴────────────────────┴───────────────────┴─────────────────┴──────┐ │
│  │                MainViewModel & UI State Flow (MVI)                       │ │
│  └─────────────────────────────────────▲────────────────────────────────────┘ │
└────────────────────────────────────────┼──────────────────────────────────────┘
                                         │
┌────────────────────────────────────────▼──────────────────────────────────────┐
│                             DOMAIN & USE CASES                                │
│                                                                               │
│  ExecuteAgentTaskUseCase  |  ApplyDiffUseCase  |  IndexWorkspaceUseCase       │
│  RunTerminalCommandUseCase |  StartDevServerUseCase | ManageCredentialsUseCase│
└────────────────────────────────────────┬──────────────────────────────────────┘
                                         │
┌────────────────────────────────────────▼──────────────────────────────────────┐
│                              AGENT ENGINE & CORE                              │
│                                                                               │
│  ┌───────────────────────┐  ┌───────────────────────┐  ┌──────────────────┐   │
│  │   Agent Orchestrator  │  │   Context Engine V2   │  │ Model Platform   │   │
│  │ ├─ 14-State Engine    │  │ ├─ AST Symbol Indexer │  │ ├─ ModelRouter   │   │
│  │ ├─ Autonomy Gate (1-5)│  │ ├─ Adaptive Budgeting │  │ ├─ ModelRegistry │   │
│  │ └─ Rollback & Healing │  │ └─ Taint Filtering    │  │ └─ ModelPolicy   │   │
│  └───────────┬───────────┘  └───────────┬───────────┘  └────────┬─────────┘   │
│              │                          │                       │             │
│  ┌───────────▼──────────────────────────▼───────────────────────▼─────────┐   │
│  │                         Tool & MCP Platform                            │   │
│  │  ToolRegistry  |  ToolPermission  |  ToolExecutor  |  MCPClientLayer   │   │
│  └──────────────────────────────────────┬─────────────────────────────────┘   │
└─────────────────────────────────────────┼─────────────────────────────────────┘
                                          │
┌─────────────────────────────────────────▼─────────────────────────────────────┐
│                       SECURITY, STORAGE & INFRASTRUCTURE                      │
│                                                                               │
│  ┌─────────────────────────┐ ┌──────────────────────┐ ┌─────────────────────┐ │
│  │ Security Sandbox        │ │ CredentialStore      │ │ Storage Subsystem   │ │
│  │ ├─ 6 Security Boundaries│ │ ├─ Android KeyStore  │ │ ├─ Room DB          │ │
│  │ ├─ SecretScrubber (GCM) │ │ ├─ AES-256-GCM       │ │ ├─ DataStore Prefs  │ │
│  │ └─ Path Canonicalization│ │ └─ Secret Redaction  │ │ └─ FileSystem Repo  │ │
│  └─────────────────────────┘ └──────────────────────┘ └─────────────────────┘ │
└───────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Core Subsystem Architecture

### 2.1 Capability-Based Model Platform
Instead of hard-coding static model identifiers (e.g. `gemini-1.5-pro` or `claude-3-5-sonnet`), the architecture decouples model invocation through:
- **`ModelDescriptor`**: Metadata describing a specific model (provider, version, context window, cost, latency tier).
- **`ModelCapabilities`**: Feature flags defining supported features (`coding`, `reasoning`, `longContext`, `vision`, `toolUse`, `structuredOutput`, `lowLatency`, `offline`, `privacySensitive`, `lowCost`).
- **`ModelRouter`**: Dynamically evaluates task requirements (e.g., high reasoning for architecture vs low latency for autocomplete) against the active `ModelPolicy` and dispatches to the optimal provider.
- **`ModelRegistry`**: In-memory and configurable registry of active providers and local on-device LLMs.

### 2.2 Security Sandbox & Credential Isolation
- **`CredentialStore`**: Hardware-backed credential management using Android KeyStore master keys to encrypt API tokens via AES-256-GCM. Plaintext keys are never stored in memory longer than necessary, never printed in logs, and scrubbed from agent context.
- **6 Security Boundaries**: Comprehensive isolation across Filesystem, Command, Network, Process, Credential, and MCP boundaries (see `docs/SECURITY_BOUNDARIES.md`).
- **Defense-in-Depth Trust Hierarchy**: Content is tagged with strict `TrustLevel` (`SYSTEM` > `DEVELOPER_POLICY` > `USER_INTENT` > `PROJECT_DATA` > `TOOL_OUTPUT` > `EXTERNAL_CONTENT`). Project files and tool outputs are strictly treated as DATA, never instructions.

### 2.3 Viewport-Aware Code Editor Subsystem
- **Decoupled Architecture**: Separate components for `TextBuffer` (gap buffer/piece table), `ViewportManager`, `SyntaxHighlighter` (cached token stream), `SelectionManager`, `UndoRedoStack`, `DiagnosticsOverlay`, and `DiffLayer`.
- **Viewport Virtualization**: Only lines visible within the viewport window (+ 15-line buffer) are laid out and styled in Compose, ensuring constant O(1) memory footprint and smooth 60/120 FPS scrolling regardless of whether the file has 100 or 50,000 lines.

### 2.4 Terminal Subsystem & Execution Backends
- **`TerminalBackend` Abstraction**:
  - `LocalBackend`: Local subshell execution with pseudoterminal handling where supported.
  - `RemoteBackend`: Secure SSH/WebSocket connection to a remote development host or workstation.
  - `SandboxedBackend`: Restricted command runner enforcing explicit command whitelists and workspace isolation.
- **`AnsiParser`**: High-performance ANSI escape sequence parser converting raw terminal stdout into styled Compose `AnnotatedString` spans.

### 2.5 Sandboxed Web Preview Subsystem
- **Local Dev Server**: Embeds a zero-dependency lightweight HTTP server serving workspace static assets.
- **WebView Security Shield**: Origin restricted to `http://127.0.0.1:<random_port>/`, strict message schema validation over the JavaScript bridge, disabled `file://` access, and sandboxed iframe isolation.

### 2.6 Agent State Machine & Autonomy Tiers
- **14 Deterministic States**: Fully specified transition table preventing race conditions or deadlocks (`IDLE`, `THINKING`, `PLANNING`, `EXECUTING_TOOL`, `AWAITING_APPROVAL`, `SELF_HEALING`, `COMPLETED`, `CANCELLED`, `FAILED`, `PAUSED`, `WAITING_FOR_RESOURCE`, `WAITING_FOR_USER`, `RECOVERING`, `PARTIAL_SUCCESS`).
- **Autonomy Tiers (Level 1 to 5)**: Precise permission enforcement mapping user-selected autonomy levels to allowed filesystem, command, network, and Git operations.

---

## 3. Data Flow & Event Streaming

1. **User Prompt Dispatch**: User submits intent via `AgentChatPanel` -> `MainViewModel` wraps intent in `AgentIntent.SubmitPrompt` -> dispatches to `AgentOrchestrator`.
2. **Context Assembly**: `ContextEngine` queries AST index, open editor tabs, git status, and recent terminal outputs, filtering out untrusted injections and fitting within the adaptive token budget.
3. **Model Selection & Query**: `ModelRouter` selects the optimal model descriptor based on task requirements, streaming tokens back via Coroutine `Flow<StreamChunk>`.
4. **Tool Execution & Sandboxing**: When a `StreamChunk.ToolCall` is received, `PermissionManager` validates permissions against the active Autonomy Level. If approved, `SandboxManager` canonicalizes paths and validates command boundaries before `ToolExecutor` runs the action.
5. **UI State Update**: Results emit structured events (`Planning`, `ToolExecuted`, `PatchProposed`) to `StateFlow<MainUiState>`, updating the Compose UI seamlessly.
