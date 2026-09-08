# Multi-Tier Agent Memory Architecture & Autonomy Model

## 1. Memory Layer Partitioning

```
┌─────────────────────────────────────────────────────────────┐
│ 1. SESSION MEMORY (In-Memory / Volatile)                    │
│    - Active conversation turns, tool calls, streaming state │
├─────────────────────────────────────────────────────────────┤
│ 2. TASK MEMORY (Ephemeral Workspace Task Context)           │
│    - Active goal, plan steps, pending diffs, recent errors  │
├─────────────────────────────────────────────────────────────┤
│ 3. PROJECT MEMORY (Persistent: docs/PROJECT_MEMORY.md)      │
│    - High-level vision, module boundaries, tech debt       │
├─────────────────────────────────────────────────────────────┤
│ 4. ARCHITECTURAL MEMORY (Persistent: docs/DECISIONS.md)     │
│    - Immutable Architecture Decision Records (ADRs)         │
├─────────────────────────────────────────────────────────────┤
│ 5. USER PREFERENCES (Persistent: Android DataStore)         │
│    - Selected AI model, theme mode, autonomy level, keys    │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Agent Autonomy Tiers

| Autonomy Level | Name | Behavior & Gates |
| :--- | :--- | :--- |
| **Level 1** | Interactive / Gated | Prompts user confirmation before modifying any file or running any command. |
| **Level 2** | Safe Auto-Edit | Automatically reads and writes non-destructive files; prompts for terminal execution. |
| **Level 3** | Auto-Edit + Test | Automatically writes files, runs unit tests, and presents diffs for review. |
| **Level 4** | Autonomous Self-Healing | Automatically edits, runs builds/tests, diagnoses compiler errors, and fixes them autonomously. |
| **Level 5** | Full Autonomous Pipeline | Executes end-to-end features (plan -> code -> test -> diff -> preview) within sandbox bounds. |

---

## 3. Task Sizing & Planning Matrix

- **Trivial (< 10 lines)**: No formal plan; execute directly.
- **Small (Single file bug / component)**: 2-step plan (edit -> test).
- **Medium (Multi-file feature)**: 3-5 step plan (inspect -> edit -> test -> verify).
- **Large / Architectural**: Full implementation plan with ADR check before execution.
