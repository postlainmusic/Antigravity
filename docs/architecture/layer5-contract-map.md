# Layer 5 Architecture Contract & Dependency Map

## 1. Executive Summary & Component Mapping

This document reconciles all module contracts, interface boundaries, constructors, and dependency graphs across the **Layer 5 Android Architecture**.

---

## 2. Comprehensive Component Contract Matrix

| Component | Interface / Type | Implementation | Dependencies | Consumers | Current Mismatch / Drift | Correct Contract & Action |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **SandboxManager** | `SandboxManager` | `SandboxManager(workspaceRoot: File)` | `File`, `URLDecoder` | `ToolRegistry`, `ContextEngine`, `TerminalManager`, `ApplyFileDiffUseCase`, `IndexWorkspaceUseCase`, `LocalDevServer` | Root prefix matching hardened; cross-platform separator normalized | Retain hardened contract. Boundary check: `target == root || target.startsWith(root + sep)`. |
| **PermissionManager**| `PermissionManager` | `PermissionManager` | `AutonomyLevel`, `PermissionTier` | `AgentOrchestrator`, `MainViewModel` | Non-exhaustive `when` in `requiresUserApproval` (missing `DESTRUCTIVE`); lacking explicit Autonomy Tier evaluation | Make `when` exhaustive across all 4 tiers (`SAFE`, `MODERATE`, `DANGEROUS`, `DESTRUCTIVE`). Add `evaluatePermission(tool, autonomyLevel)`. |
| **ModelProvider** | `ModelProvider` | `GeminiModelProvider`, `ClaudeModelProvider`, `OpenAiModelProvider`, `LocalModelProvider` | `CompletionRequest`, `ToolDefinition` | `ModelRegistry`, `ModelRouter` | Clean capability-based provider interface | Retain `streamCompletion(request, tools): Flow<StreamChunk>`. |
| **ModelRouter** | `ModelRouter` | `ModelRouter(registry: ModelRegistry, policy: ModelPolicy)` | `ModelRegistry`, `ModelPolicy` | `AgentOrchestrator` | `MainViewModel` directly instantiated `GeminiModelProvider` instead of routing through `ModelRouter` | `MainViewModel` & `AgentOrchestrator` depend strictly on `ModelRouter`. Remove hardcoded provider references. |
| **ToolRegistry** | `ToolRegistry` | `ToolRegistry(sandboxManager: SandboxManager)` | `BaseTool`, `SandboxManager` | `AgentOrchestrator` | Fully functional; registers 6 core tools | Retain contract `execute(callId, toolName, argsJson): ToolResult`. |
| **AgentOrchestrator**| `AgentOrchestrator` | `AgentOrchestrator(...)` | `ModelRouter`, `ToolRegistry`, `ContextEngine`, `PermissionManager`, `DiffManager` | `ExecuteAgentTaskUseCase`, `MainViewModel` | Constructor took `modelRouter` but `MainViewModel` passed `modelProvider = modelProvider` | Reconcile constructor invocation in `MainViewModel` to pass `modelRouter`. |
| **ContextEngine** | `ContextEngine` | `ContextEngine(sandboxManager: SandboxManager)` | `SandboxManager` | `AgentOrchestrator` | Token assembly & injection defense active | Retain `assembleSystemPrompt(...)` with `<untrusted_workspace_file>` tags. |
| **TerminalManager** | `TerminalManager` | `TerminalManager(sandboxManager: SandboxManager, backend: TerminalBackend)` | `SandboxManager`, `TerminalBackend` | `RunTerminalCommandUseCase`, `MainViewModel` | Sandboxed runner with dangerous command blacklist | Retain `executeCommand(sessionId, commandLine)`. |
| **DiffManager** | `DiffManager` | `DiffManager` | `FileDiff`, `DiffHunk`, `DiffChunk` | `AgentOrchestrator`, `ApplyFileDiffUseCase` | Computes line-by-line additions and deletions | Retain `computeDiff(filePath, oldText, newText): FileDiff`. |
| **UIComponents** | Composable Functions | `AppTopBar`, `FileTreeDrawerContent`, `PermissionDialog` | Compose Material 3 | `MainScreen` | Missing import for `Modifier.fillMaxSize` in `FileTreeDrawerContent` | Add `import androidx.compose.foundation.layout.fillMaxSize`. |
| **MainViewModel** | `ViewModel` | `MainViewModel(...)` | `AppContainer` (Composition Root) | `MainActivity`, `MainScreen` | God-object instantiation; `AgentStatus.ERROR` used instead of `AgentStatus.FAILED` | Wire dependencies via `AppContainer`. Replace `AgentStatus.ERROR` with `AgentStatus.FAILED`. |
| **MainMviContracts**| State & Event Models | `MainUiState`, `MainUiEvent`, `NavigationTab` | Domain Models | `MainViewModel`, `MainScreen` | Clean MVI contract | Retain unidirectional data flow. |

---

## 3. Directional Dependency Graph

```text
[ Presentation Layer: Jetpack Compose UI ]
  MainScreen, AgentChatPanel, CodeEditorScreen, GitDiffScreen, PreviewScreen, TerminalScreen, SettingsScreen
        │
        ▼ (MVI Events / StateFlow)
[ Presentation Logic: ViewModels ]
  MainViewModel
        │
        ▼ (Invokes UseCases)
[ Domain Layer: UseCases & Core Models ]
  ExecuteAgentTaskUseCase, ApplyFileDiffUseCase, IndexWorkspaceUseCase, RunTerminalCommandUseCase
  AgentStatus, AutonomyLevel, TrustLevel, PlanStep, PendingAction, FileDiff, TerminalSession
        │
        ▼ (Orchestration & Routing)
[ Core Orchestration Engine ]
  AgentOrchestrator  <───>  PermissionManager (Autonomy Tiers)
        │
        ├──► ContextEngine (Context Engineering V2)
        ├──► DiffManager (Myers Diff / Rollback Engine)
        ├──► ModelRouter (Capability-based dispatch)
        │         └──► ModelRegistry ──► [ Gemini | Claude | OpenAI | Local Gemma 2B ]
        └──► ToolRegistry (Security Sandbox Gates)
                  └──► [ read_file | write_file | replace_file_content | list_dir | grep_search | run_command ]
                            │
                            ▼
[ Infrastructure / Security Foundation ]
  SandboxManager (Path Traversal & Boundary Containment)
  KeyStoreCredentialStore (AES-256-GCM Hardware Vault)
  SecretScrubber (Context & Log Redaction)
  TerminalManager (PTY & Sandboxed Execution)
```

---

## 4. Required Contract Reconciliation Actions

1. **Permission Architecture**:
   - Update `PermissionManager.kt` to make `when` exhaustive across all `PermissionTier` values.
   - Implement `evaluatePermission(tool: ToolDefinition, autonomyLevel: AutonomyLevel): PermissionDecision`.
2. **Model Router Wiring**:
   - Establish `AppContainer` composition root in `AntigravityApp.kt` or `core/di/AppContainer.kt`.
   - Inject `ModelRouter` with registered providers into `AgentOrchestrator`.
3. **Enum & State Symbol Alignment**:
   - Replace any stray `AgentStatus.ERROR` with canonical `AgentStatus.FAILED`.
4. **UI Imports & Compose Compatibility**:
   - Fix missing `fillMaxSize` import in `UIComponents.kt`.
   - Verify all Material 3 and Compose API usages.
