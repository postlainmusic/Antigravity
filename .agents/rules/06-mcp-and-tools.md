# 06 - MCP (Model Context Protocol) & Tools Guidelines

## Protocol & Tool Architecture
1. **Standard MCP Compliance**: Follow the Model Context Protocol JSON-RPC specification over both Stdio and SSE transports.
2. **Strict Tool Schemas**:
   - Every tool exposed to the agent must have a strongly-typed JSON Schema with precise descriptions and mandatory parameters.
3. **Execution Safety**:
   - Classify tools into permission tiers: `SAFE` (read-only, search), `MODERATE` (file write, test execution), and `DANGEROUS` (deletion, git force, arbitrary shell commands).
   - Enforce user authorization policies based on tier before invocation.
4. **Lifecycle & Health**:
   - Heartbeat monitoring, graceful restarts, and timeout limits (default 30s) on all external MCP processes.
