# MCP (Model Context Protocol) Checklist

- [ ] JSON-RPC 2.0 handshake completes successfully over Stdio / SSE transports.
- [ ] Tool parameters validated against JSON Schema definitions.
- [ ] Tool permissions categorized into SAFE, MODERATE, and DANGEROUS tiers.
- [ ] Process failures handle automatic reconnection with exponential backoff.
- [ ] Configured MCP servers documented in `docs/MCP_REGISTRY.md`.
