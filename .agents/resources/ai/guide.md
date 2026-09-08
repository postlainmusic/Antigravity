# AI Agent Reference Guide

### What is this?
Architecture reference for orchestrating LLM agents, multi-model providers, context retrieval, tool schemas, and execution lifecycles.

### When to use?
- Implementing AI features, streaming token parsers, tool calling engines, and prompt compressors.

### Model Abstraction Interface
```kotlin
interface ModelProvider {
    val id: String
    val displayName: String
    fun streamCompletion(
        request: CompletionRequest,
        tools: List<ToolDefinition>
    ): Flow<StreamEvent>
}

sealed interface StreamEvent {
    data class ContentChunk(val text: String) : StreamEvent
    data class ToolCallChunk(val callId: String, val toolName: String, val argsJson: String) : StreamEvent
    data class Finish(val reason: String, val tokenUsage: TokenUsage) : StreamEvent
    data class Error(val error: Throwable) : StreamEvent
}
```

### Validation Checklist
- [ ] Tool inputs validated before dispatch.
- [ ] Cancellation interrupts active HTTP/SSE streams immediately.
