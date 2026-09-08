# Tool System Platform Architecture

## 1. Tool Platform Architecture

The Antigravity Tool Platform is a robust, modular runtime that provides AI agents with safe, audited, and observable access to development capabilities. Tools are never executed directly by string matching; they must pass through schema validation, permission checks, sandbox verification, and structured auditing.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            Tool Platform Pipeline                           │
│                                                                             │
│  Model Tool Call ──► ToolRegistry (Lookup & Schema Check)                   │
│                            │                                                │
│                            ▼                                                │
│                     ToolPermission (Autonomy & Safety Tier Gate)            │
│                            │                                                │
│                            ▼                                                │
│                     SandboxManager (Path & Command Boundaries)              │
│                            │                                                │
│                            ▼                                                │
│                     ToolExecutor (Timeout & Coroutine Cancellation)         │
│                            │                                                │
│                            ▼                                                │
│                     ToolResult (Structured Output & Trust Tagging)          │
│                            │                                                │
│                            ▼                                                │
│                     ToolAudit (Structured Event Telemetry & Scrubbing)      │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Tool Descriptor & Schema Definition

Each tool in the platform is defined via a complete `ToolDescriptor`:

| Field | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | Unique tool identifier (e.g. `read_file`, `write_file`, `run_terminal_command`) |
| `description` | `String` | Human-readable explanation and prompt guidance for LLM invocation |
| `parametersSchema` | `JsonObject` | Strict JSON Schema defining required/optional arguments and types |
| `permissionTier` | `ToolPermissionTier` | `READ_ONLY` (Safe), `WORKSPACE_WRITE` (Moderate), `COMMAND_EXECUTION` (Dangerous), `DESTRUCTIVE` (Critical) |
| `sideEffects` | `List<SideEffect>` | Declared side effects: `FILE_MODIFICATION`, `PROCESS_SPAWN`, `NETWORK_OUTBOUND`, `GIT_MUTATION` |
| `timeoutMs` | `Long` | Execution timeout (default 30,000ms; max 120,000ms for builds) |
| `cancellationPolicy`| `CancellationPolicy`| Immediate thread interruption, SIGTERM process kill, or graceful rollback |

---

## 3. Core Tool Catalog

| Tool Name | Permission Tier | Side Effects | Description |
| :--- | :--- | :--- | :--- |
| `list_dir` | `READ_ONLY` | None | Lists directory contents with sizes and file types |
| `read_file` | `READ_ONLY` | None | Reads file content with slice notation and byte offsets |
| `write_file` | `WORKSPACE_WRITE` | `FILE_MODIFICATION` | Atomically creates or updates a workspace file with snapshot backup |
| `replace_file_content` | `WORKSPACE_WRITE` | `FILE_MODIFICATION` | Applies single contiguous search-and-replace edit |
| `multi_replace_file_content`| `WORKSPACE_WRITE`| `FILE_MODIFICATION` | Applies multiple non-contiguous patch chunks |
| `grep_search` | `READ_ONLY` | None | Fast regex search across workspace files |
| `file_search` | `READ_ONLY` | None | Fast glob/filename matching |
| `run_command` | `COMMAND_EXECUTION`| `PROCESS_SPAWN` | Executes sandboxed terminal command with real-time stdout streaming |
| `git_status` | `READ_ONLY` | None | Inspects git working tree and staged index |
| `git_diff` | `READ_ONLY` | None | Computes unified or per-hunk diff |
| `git_commit` | `WORKSPACE_WRITE` | `GIT_MUTATION` | Stages changes and creates a signed local git commit |
| `mcp_call_tool` | Dynamic | Dynamic | Dispatches tool call to a connected Model Context Protocol server |

---

## 4. Untrusted Tool Output Handling

Tool output (stdout, stderr, file contents, MCP JSON payloads) is strictly **Level 4 Untrusted Data**.
- Outputs are scanned by `SecretScrubber` before being stored or presented.
- Output text is enclosed in `<tool_output tool_name="..." trust_level="DATA">` XML tags.
- Models are constrained by system prompt rules to interpret tool output solely as data results.

---

## 5. Tool Audit & Observability

Every tool execution emits an immutable audit event:
```json
{
  "eventId": "evt_98231a4",
  "timestamp": 1725850000000,
  "toolName": "write_file",
  "invokingModel": "gemini-1.5-pro",
  "argumentsHash": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
  "permissionGranted": true,
  "executionTimeMs": 42,
  "exitCode": 0,
  "bytesWritten": 1420,
  "secretsScrubbed": 0
}
```
