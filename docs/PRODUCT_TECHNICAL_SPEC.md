# Technical Specification: Antigravity Mobile Core Architecture

## 1. Subsystem Architecture Map

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION (Jetpack Compose)                  │
│                                                                        │
│   AgentChatPanel   |   CodeEditorScreen   |   PreviewScreen            │
│   TerminalScreen   |   GitDiffScreen      |   FileTreeDrawer           │
│                           ▲                                            │
│                           │ (Collects StateFlow<MainUiState>)          │
│                           ▼ (Dispatches MainUiEvent)                   │
│                    MainViewModel (MVI Reducer)                         │
└───────────────────────────┬────────────────────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────────────────────┐
│                       DOMAIN USE CASES (Pure Kotlin)                   │
│                                                                        │
│  ExecuteAgentTaskUseCase     |     ApplyFileDiffUseCase                │
│  IndexWorkspaceUseCase       |     RunTerminalCommandUseCase           │
└───────────────────────────┬────────────────────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────────────────────┐
│                    DATA & CORE PLATFORM ENGINES                        │
│                                                                        │
│  ┌───────────────────────┐  ┌───────────────────────┐  ┌─────────────┐ │
│  │   AI Agent Platform   │  │   Workspace Manager   │  │ Tool Engine │ │
│  │ ├─ ModelProvider      │  │ ├─ FileTree & Grep    │  │ ├─ FileTool │ │
│  │ ├─ ContextEngine (V2) │  │ ├─ DiffManager        │  │ ├─ TermTool │ │
│  │ └─ AgentOrchestrator  │  │ └─ Symbol Indexer     │  │ └─ McpClient│ │
│  └───────────────────────┘  └───────────────────────┘  └─────────────┘ │
│                                                                        │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │   SandboxManager  |  SecretScrubber  |  LocalDevServer & Bridge   │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Event Model & Data Contracts

### Agent Event Stream Protocol
```kotlin
sealed interface AgentEvent {
    data class Started(val conversationId: String) : AgentEvent
    data class Thinking(val content: String) : AgentEvent
    data class PlanUpdated(val steps: List<PlanStep>) : AgentEvent
    data class ToolStarted(val callId: String, val toolName: String, val argsJson: String) : AgentEvent
    data class ToolCompleted(val callId: String, val toolName: String, val result: ToolResult) : AgentEvent
    data class FileDiffCreated(val fileDiff: FileDiff) : AgentEvent
    data class AwaitingApproval(val action: PendingAction) : AgentEvent
    data class Completed(val summary: String) : AgentEvent
    data class Error(val message: String, val recoverable: Boolean = true) : AgentEvent
}
```

### Tool Invocation Contract
All tools return a strongly-typed `ToolResult`:
```kotlin
data class ToolResult(
    val callId: String,
    val toolName: String,
    val output: String,
    val isError: Boolean = false,
    val executionDurationMs: Long = 0
)
```

---

## 3. Sandboxed Local Dev Server & Preview Bridge

1. **Embedded Static & WebSocket Server**: Listens on `http://localhost:3000` to serve generated HTML/CSS/JS workspace assets.
2. **Bi-Directional JavaScript Console Bridge**: Injects a lightweight snippet into rendered pages:
```javascript
window.console.log = function(...args) {
    window.AntigravityBridge.postMessage(JSON.stringify({ type: 'log', message: args.join(' ') }));
};
window.onerror = function(msg, url, line) {
    window.AntigravityBridge.postMessage(JSON.stringify({ type: 'error', message: msg + ' at ' + line }));
};
```
3. Captured errors are routed directly to the IDE developer console and agent self-healing loops.
