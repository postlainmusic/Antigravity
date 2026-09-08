# Decision Tree: WebSocket vs HTTP

```mermaid
graph TD
    Start[Communication Channel] --> ChannelType{Interaction Type?}
    ChannelType -- Bi-directional / Real-time Interactive --> WS[WebSocket: Terminal PTY, Live Agent Stream, Preview Console]
    ChannelType -- Request-Response / Standard CRUD / SSE --> HTTP[HTTP/REST / Server-Sent Events: File Uploads, One-off API requests, Token Streaming]
```

## Guidelines
- **HTTP / SSE**: LLM Token streaming (SSE `text/event-stream`), MCP SSE transport, one-off file downloads/uploads.
- **WebSocket**: Interactive PTY terminal sessions, hot-module reload signals, multi-client live sync.
