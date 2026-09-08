# MCP (Model Context Protocol) Reference Guide

### What is this?
Guide for connecting to MCP servers via JSON-RPC 2.0 to dynamically register developer tools and resources.

### Protocol Lifecycle
1. **Initialize**: Client sends `{ "method": "initialize", "params": { "protocolVersion": "2024-11-05", "capabilities": {} } }`.
2. **Initialized Notification**: Client sends `{ "method": "notifications/initialized" }`.
3. **Tool Listing**: Client queries `{ "method": "tools/list" }`.
4. **Tool Call**: Client calls `{ "method": "tools/call", "params": { "name": "...", "arguments": { ... } } }`.

### Safe Execution Pattern
- Check permission policy (SAFE / MODERATE / DANGEROUS) before invoking `tools/call`.
- Wrap calls in 30s timeout handler with graceful disconnect recovery.
