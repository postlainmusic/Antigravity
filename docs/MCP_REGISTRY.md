# Model Context Protocol (MCP) Registry

## Overview
This registry catalogs all MCP servers configured within the Antigravity workspace, detailing their capabilities, authentication mechanisms, risk levels, and permissions.

---

## Registered MCP Servers

### 1. `project-tools` (Workspace Local)
- **Purpose**: Autonomous workspace health monitoring, test runner invocation, and lint validation.
- **Transport**: Stdio (`node .agents/scripts/project-health-check.js --json`).
- **Permissions**: Read-only workspace inspection.
- **Risk Level**: `SAFE`.
- **Reason for Installation**: Enables the AI agent to independently verify project structure and test suites.

### 2. `data-agent-kit` (Global Builtin)
- **Purpose**: GCP dataset discovery, bigquery analytics, and active editor context inspection.
- **Transport**: Stdio (Node.js bundle proxy).
- **Risk Level**: `SAFE` / Read-Only.

### 3. `chrome-devtools` (Global Schema Available)
- **Purpose**: Headless browser automation, DOM inspection, screenshot capture.
- **Transport**: Stdio.
- **Risk Level**: `MODERATE`.
- **Reason for Evaluation**: UI preview testing and visual regression verification.

---

## MCP Evaluation & Security Policy
1. Every candidate server must be evaluated against the 7 security criteria outlined in the constitution.
2. Servers requesting network access or shell execution require explicit registration and risk classification.
3. Unverified or single-purpose third-party MCP servers are rejected in favor of native local tool implementations.
