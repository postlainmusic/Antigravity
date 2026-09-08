# Agent Orchestration Architecture

## 1. Deterministic State Machine (14 States)

The Antigravity Agent runtime is modeled as a deterministic Finite State Machine (FSM) governed by explicit transition invariants. No impossible states or unhandled edge transitions are permitted.

```
                  ┌──────────────────┐
                  │       IDLE       │◄──────────────────────────┐
                  └─────────┬────────┘                           │
                            │ User Prompt Submitted              │
                            ▼                                    │
                  ┌──────────────────┐                           │
                  │     THINKING     │                           │
                  └─────────┬────────┘                           │
                            │ Needs Multi-Step Plan              │
                            ▼                                    │
                  ┌──────────────────┐                           │
                  │     PLANNING     │                           │
                  └─────────┬────────┘                           │
                            │ Plan Ready / Dispatches Tool       │
                            ▼                                    │
                  ┌──────────────────┐                           │
       ┌──────────┤  EXECUTING_TOOL  │                           │
       │          └─────────┬────────┘                           │
       │ Requires           │ Success                            │
       │ Approval           ▼                                    │
       │          ┌──────────────────┐                           │
       │          │   SELF_HEALING   │                           │
       │          └─────────┬────────┘                           │
       │                    │ Fixed                              │
       ▼                    ▼                                    │
┌───────────────┐ ┌──────────────────┐                           │
│AWAITING_APPRVL│ │    COMPLETED     │───────────────────────────┤
└───────┬───────┘ └──────────────────┘                           │
        │                                                        │
        ├────────────────────────────────────────────────────────┤
        ▼                                                        ▼
┌───────────────┐ ┌──────────────────┐ ┌──────────────────┐ ┌───────────────┐
│   CANCELLED   │ │      FAILED      │ │      PAUSED      │ │PARTIAL_SUCCESS│
└───────────────┘ └──────────────────┘ └──────────────────┘ └───────────────┘
        ▲                   ▲                   ▲                   ▲
        │                   │                   │                   │
┌───────┴───────┐ ┌─────────┴────────┐ ┌────────┴─────────┐ ┌───────┴───────┐
│  RECOVERING   │ │WAITING_FOR_RESRCE│ │ WAITING_FOR_USER │ │(Atomic Rollback)
└───────────────┘ └──────────────────┘ └──────────────────┘ └───────────────┘
```

### 1.1 State Invariants

| State | Entry Trigger | Exit Conditions | Invariants / Constraints |
| :--- | :--- | :--- | :--- |
| `IDLE` | App launch, task completed/cancelled | New user prompt | Zero background CPU consumption; all buffers released |
| `THINKING` | User prompt received | Model emits response tokens or tool call | Model stream active; token counter incrementing |
| `PLANNING` | Multi-step task detected | Plan structured into executable steps | Implementation plan artifact generated |
| `EXECUTING_TOOL` | Tool call received from model | Tool execution finishes with output | Path & command boundaries checked; timeout timer running (max 60s) |
| `AWAITING_APPROVAL` | High-risk tool invocation or Autonomy Level gate | User confirms or rejects action | Pipeline paused; UI presents diff / action modal |
| `SELF_HEALING` | Tool execution resulted in build or syntax error | Diagnostic parsed, corrective patch generated | Max retry budget = 3 attempts; retry token budget <= 8,192 tokens |
| `RECOVERING` | Tool threw unhandled error or budget exceeded | Atomic rollback executed | Reverts working tree to pre-task snapshot |
| `WAITING_FOR_RESOURCE`| Missing external dependency or network offline | Resource becomes available or timeout | Exponential backoff polling (1s, 2s, 4s, 8s) |
| `WAITING_FOR_USER` | Model asks clarifying question | User provides response in chat | Agent context updated with user answer |
| `PAUSED` | User taps Pause in UI | User taps Resume or Cancel | Context frozen in memory |
| `CANCELLED` | User taps Cancel or timeout fires | Transition to IDLE | Coroutine job cancelled; child processes terminated via SIGTERM |
| `PARTIAL_SUCCESS` | Multi-file task succeeded with non-critical warnings | Transition to IDLE | Warnings logged to task memory |
| `FAILED` | Critical error, retry budget exhausted, or user rejected | Transition to IDLE | Error diagnostics preserved for user inspection |
| `COMPLETED` | All task goals fulfilled & verified | Transition to IDLE | Success summary emitted; telemetry recorded |

---

## 2. Autonomy Levels & Permission Gate

| Level | Description | Filesystem Permission | Terminal Commands | Git Actions | Network & MCP | Approval Required |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Level 1** | Read-Only Analyst | Read only | Read-only commands (`ls`, `git status`) | Read only | Read only | Always read-only |
| **Level 2** | Suggest Changes | Read only + Propose Diffs | Read only | Read only | Read only | Diffs displayed in UI |
| **Level 3** | Supervised Editor | Write with user confirmation | Whitelisted build commands | Stage with approval | Registered MCPs | Every file write & build |
| **Level 4** | Bounded Automator | Auto-write workspace files | Auto-run build/test commands | Commit with confirmation | Registered MCPs | Only dangerous commands & pushes |
| **Level 5** | Autonomous Factory | Fully autonomous in workspace | Unrestricted within sandbox | Auto-commit & branch | Full MCP orchestration | Only out-of-sandbox actions |

---

## 3. Bounded Self-Healing Protocol

When a tool or compilation fails, the agent must not enter an infinite loop. It follows the 5-phase bounded self-healing protocol:

```
Tool / Build Failure
        │
        ▼
[Phase 1: Classification]
├── SYNTAX ERROR      -> Parse line number & AST token error
├── COMPILATION ERROR -> Extract compiler diagnostic & missing symbol
├── RUNTIME TEST FAIL -> Extract assertion failure & stack trace
└── PERMISSION ERROR  -> Escalate to user approval
        │
        ▼
[Phase 2: Budget Check]
Attempts <= 3 AND Cumulative Tokens <= 8,192?
├── YES -> Proceed to Phase 3
└── NO  -> ABORT to RECOVERING (Atomic Rollback)
        │
        ▼
[Phase 3: Diagnostic Prompting]
Inject structured failure diagnosis into model context
        │
        ▼
[Phase 4: Surgical Patch Application]
Apply minimal diff to fix isolated failure
        │
        ▼
[Phase 5: Re-verification]
Re-run build/test. If pass -> COMPLETED. If fail -> Increment attempt & loop to Phase 1.
```

---

## 4. Rollback Mechanisms

Antigravity provides 4 distinct rollback tiers:

1. **File Snapshot Rollback**: In-memory byte snapshot taken immediately before modifying any file. If an edit fails, original bytes are restored atomically.
2. **Patch Reversion**: Reverse application of Myers diff patches when multi-hunk edits fail validation.
3. **Git Working Tree Reversion**: `git checkout -- .` and `git clean -fd` inside workspace to restore pristine commit state.
4. **Session Recovery**: Persisted task journal (`Room DB`) allowing interrupted tasks to resume from the last valid checkpoint.
