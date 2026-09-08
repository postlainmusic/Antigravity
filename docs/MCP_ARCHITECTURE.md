# Model Context Protocol (MCP) Architecture

## 1. Executive Summary

The Antigravity Mobile MCP architecture decouples the core development environment from external MCP server implementations. MCP is completely **modular and optional**: the application remains 100% functional even if zero MCP servers are registered or if an external MCP server disconnects.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          MCP Client Architecture                            │
│                                                                             │
│  ┌───────────────────────┐         ┌──────────────────────────────────────┐ │
│  │   Agent Orchestrator  │         │          MCP Client Manager          │ │
│  │ ├─ Dispatches Intent  │────────►│ ├─ Server Discovery & Health Monitor │ │
│  │ └─ Collects Results   │         │ ├─ JSON-RPC 2.0 Transport (Stdio/SSE)│ │
│  └───────────────────────┘         │ └─ Permission Gate & Schema Mapping  │ │
│                                    └──────────────────┬───────────────────┘ │
│                                                       │                     │
│                        ┌──────────────────────────────┴──────────────┐      │
│                        ▼                                             ▼      │
│             ┌─────────────────────┐                       ┌────────────────┐│
│             │ Local Stdio Servers │                       │ Remote SSE Svr ││
│             │ (Node / Python / Go)│                       │ (HTTPS Gateway)││
│             └─────────────────────┘                       └────────────────┘│
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. MCP Lifecycle & State Management

Each registered MCP server transitions through a managed lifecycle:

```
[DISCOVERED] ──► [INITIALIZING] ──► [READY] ──► [SERVING] ──► [CLOSING] ──► [TERMINATED]
                       │               │           │
                       ▼               ▼           ▼
                   [FAILED] ◄──────────────────────┘ (Heartbeat timeout / Error)
```

1. **Discovery**: Scans workspace `.agents/mcp_config.json` and user preferences.
2. **Initialization**: Establishes transport (`Stdio` or `SSE`), exchanges protocol version capabilities (`tools`, `resources`, `prompts`).
3. **Health Monitoring**: Sends non-blocking ping frames every 30 seconds.
4. **Failure Isolation**: If an MCP server crashes or times out (10s threshold), the client isolates the failure, unregisters the server's tools, and notifies the agent without crashing the workspace.

---

## 3. Security & Permission Sandboxing for MCP

- **No Implicit Privileges**: MCP tools are assigned `ToolPermissionTier.READ_ONLY` by default. Any MCP tool requesting filesystem modifications or process execution requires explicit user authorization.
- **Schema Validation**: Inbound and outbound JSON-RPC payloads are strictly validated against registered JSON Schemas.
- **Data Demarcation**: Content returned from MCP servers is tagged with `TrustLevel.EXTERNAL_CONTENT`.

---

## 4. MCP Registry Schema (`.agents/mcp_config.json`)

```json
{
  "mcpServers": {
    "git-analyzer": {
      "command": "node",
      "args": ["./tools/git-mcp.js"],
      "env": {
        "NODE_ENV": "production"
      },
      "disabled": false,
      "autoApprove": ["git_log", "git_blame"]
    }
  }
}
```
