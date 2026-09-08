# Offline Capability Matrix & Network Failure Degradation

## 1. Offline Execution Architecture

Antigravity Mobile supports local on-device operation. When network access is disabled or unavailable, the system operates in **Degraded Offline Mode**.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         OFFLINE MODE STATUS MATRIX                          │
├────────────────────────────────┬─────────────────┬──────────────────────────┤
│ Subsystem / Feature            │ Offline State   │ Behavior & Degradation   │
├────────────────────────────────┼─────────────────┼──────────────────────────┤
│ 1. Project Opening & Browsing  │ OPERATIONAL     │ 100% Local Disk I/O      │
│ 2. AST Symbol Indexing         │ OPERATIONAL     │ 100% Local BM25 Parser   │
│ 3. Context Engine Retrieval    │ OPERATIONAL     │ Local AST Slicing        │
│ 4. Local Model (Gemma 2B ONNX) │ OPERATIONAL     │ Local NPU/CPU Inference  │
│ 5. Cloud LLMs (Gemini, Claude) │ UNAVAILABLE     │ Emits NetworkError       │
│ 6. Tool Platform (File, Diff)  │ OPERATIONAL     │ Local In-Memory & Disk   │
│ 7. Local Terminal Subshell     │ OPERATIONAL     │ Local Android Process    │
│ 8. Remote SSH Terminal         │ UNAVAILABLE     │ Socket Connection Fails  │
│ 9. Web Preview (LocalDevServer)│ OPERATIONAL     │ Bound to 127.0.0.1 Loop  │
│ 10. Remote MCP Servers (SSE)   │ UNAVAILABLE     │ Isolated; Falls back     │
│ 11. Local MCP Servers (Stdio)  │ OPERATIONAL     │ Child Process Stdio RPC  │
│ 12. KeyStore Credential Vault  │ OPERATIONAL     │ Local Hardware KeyStore  │
└────────────────────────────────┴─────────────────┴──────────────────────────┘
```

---

## 2. Degraded State Indicators in UI

When offline:
- **Model Router**: Automatically filters registry to `descriptors.filter { it.capabilities.offline }` (e.g. `gemma-2b-it`).
- **UI Banner**: `AgentChatPanel` displays an amber indicator: `Offline Mode Active: Local Model (Gemma 2B) engaged`.
- **Cloud Requests**: Attempting to invoke a cloud-only model returns a structured error: `NetworkUnavailableException: Model requires cloud connectivity. Switch to Local Gemma or restore internet connection.`
