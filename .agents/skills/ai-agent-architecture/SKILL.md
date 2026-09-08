---
name: ai-agent-architecture
description: >-
  Orchestrates the AI agent platform, deterministic state machine, multi-model abstraction, tool calling lifecycle, streaming events, cancellation, and transaction rollback.
---

# AI Agent Architecture Skill

## Purpose
Builds and maintains the core AI engineering runtime that powers autonomous code generation, multi-file edits, project reasoning, tool calling, and self-healing.

## State Machine
```
[IDLE] 
  │ (User Request)
  ▼
[REASONING] ──► [CREATING_PLAN] ──► [AWAITING_PLAN_APPROVAL (if high-risk)]
  │                                           │
  ▼                                           ▼
[EXECUTING_TOOLS] ◄───────────────────────────┘
  │ ├── Tool 1 (read_file / inspect)
  │ ├── Tool 2 (replace_file_content / edit)
  │ └── Tool 3 (run_command / test)
  │
  ├─► [TOOL_ERROR] ──► [SELF_HEALING_LOOP]
  │
  ▼
[GENERATING_DIFF & VERIFYING]
  │
  ▼
[COMPLETED] / [FAILED]
```

## Core Protocol & Events
- `AgentEvent.Thinking(content: String)`
- `AgentEvent.PlanCreated(steps: List<PlanStep>)`
- `AgentEvent.ToolStarted(toolName: String, args: JsonObject)`
- `AgentEvent.ToolFinished(toolName: String, output: String, isError: Boolean)`
- `AgentEvent.FileChanged(path: String, diff: String)`
- `AgentEvent.AwaitingApproval(action: PendingAction)`
- `AgentEvent.Completed(summary: String)`

## Implementation Workflow
1. Process user prompt through `ContextEngine` to assemble prompt payload.
2. Stream LLM tokens from `ModelProvider`.
3. Intercept tool invocation blocks in model stream.
4. Verify tool permissions against `PermissionManager`.
5. Execute tool via `ToolRegistry` and append tool result to conversation trajectory.
6. Trigger compiler/linter check; if errors occur, re-prompt agent with error diagnostics for auto-repair.
