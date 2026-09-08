# Tool Execution Reality Audit

## 1. Tool Platform Implementation Matrix

| Tool Name | Implementation State | Actual Execution Mechanism | Mocks / Stubs Involved | Failure Modes & Error Behavior |
| :--- | :--- | :--- | :--- | :--- |
| `read_file` | **REAL** | `File(safePath).readText()` with line slices and byte limits | None | Throws `FileNotFoundException` if missing; `SecurityException` if outside workspace |
| `write_file` | **REAL** | Atomic snapshot backup + `File.writeText()` | None | Throws `IOException` on disk full; `SecurityException` on traversal |
| `replace_file_content` | **REAL** | Contiguous substring match & replacement | None | Returns error if target content not found or non-unique |
| `multi_replace_file_content` | **REAL** | Multi-chunk line range replacement | None | Validates line bounds; errors if chunk overlap detected |
| `list_dir` | **REAL** | `File.listFiles()` with metadata extraction | None | Returns empty list if directory is empty |
| `grep_search` | **REAL** | Regex search over workspace text files | None | Max 50 matches; ignores binary files |
| `run_command` | **PARTIAL** | `ProcessBuilder` on local OS | Filtered on dangerous commands | Blocked if matching dangerous blacklists (`rm -rf`, `curl | sh`) |
| `git_status` | **PARTIAL** | `git status --porcelain` via subshell | Bypassed if `.git` repo not initialized | Returns status text |
| `git_diff` | **REAL** | Myers Diff hunk calculation in Kotlin | None | Computes exact line additions, deletions, line numbers |
| `git_commit` | **PARTIAL** | `git commit -m` via subshell | Bypassed if git user.email unset | Returns commit SHA |
| `mcp_call_tool` | **SIMULATED** | JSON-RPC 2.0 schema validation & dispatch | Mock server response used in test harness | Returns tool result JSON or timeout error after 10s |
| `dev_server_preview` | **REAL** | NanoHTTPD / JDK HttpServer on `127.0.0.1:<port>` | None | Returns HTTP 404 for missing static files |

---

## 2. Limitations & Environmental Notes

1. **Terminal PTY on Android**: Standard Android applications cannot spawn an unrestricted root PTY shell due to SELinux policies (`isolatedProcess` / `app_data_file`). Terminal execution utilizes `ProcessBuilder` with local application sandbox binaries (or connects via `RemoteSshTerminalBackend` to a developer workstation).
2. **MCP Stdio Processes**: Spawning child Node.js/Python processes for MCP servers requires those runtimes to be installed in the host environment.
