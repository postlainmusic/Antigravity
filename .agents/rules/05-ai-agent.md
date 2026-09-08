# 05 - AI Agent Architecture & Orchestration Guidelines

## Architecture Principles
1. **Explicit State Machine**: The agent lifecycle follows a deterministic state machine (`IDLE` -> `THINKING` -> `PLANNING` -> `EXECUTING_TOOL` -> `AWAITING_APPROVAL` -> `RECOVERING` -> `COMPLETED` / `FAILED`).
2. **Context Engine & Token Management**:
   - Never inject unbounded file dumps into the context.
   - Use AST symbol indexing, BM25/vector retrieval, and git diff summaries to assemble a dense, high-relevance prompt.
3. **Multi-Model Abstraction**:
   - Decouple agent orchestration from model providers via unified interfaces (`ModelProvider`, `CompletionRequest`, `StreamChunk`).
   - Support streaming tokens, tool calling, and structured JSON outputs across all providers (Google Gemini, Anthropic Claude, OpenAI, Local models).
4. **Approval & Rollback**:
   - Any file modification, terminal execution, or package install must generate an atomic transaction with previewable diff and rollback capability.
