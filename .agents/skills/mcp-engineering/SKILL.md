---
name: mcp-engineering
description: >-
  Designs, registers, configures, and monitors Model Context Protocol (MCP) clients and servers over Stdio and SSE transports for developer tooling and external services.
---

# MCP Engineering Skill

## Purpose
Enables the agent to connect with external MCP tools (GitHub, documentation lookup, browser automation, databases) following the official MCP specification.

## Core Capabilities
1. **MCP Client Manager**:
   - Spawns local Stdio processes (Node.js/Python scripts) or connects to SSE endpoints.
   - Performs JSON-RPC 2.0 protocol handshake (`initialize`, `notifications/initialized`).
   - Discovers tools via `tools/list` and converts them to native agent tool definitions.
2. **Security & Permission Gate**:
   - Intercepts all `tools/call` invocations and checks user permission policy before dispatching.
3. **Resilience**:
   - Recovers from dead connections with exponential backoff and informs the agent if a tool becomes unavailable.

## Configuration Format
Stored in `.agents/mcp_config.json` with clear arguments, environment variables, and risk tiers.
