---
name: failure-recovery
description: >-
  Systematic error triage and self-healing engine: classifies failures (transient, syntax, build, permission, logic), calculates retry budgets, and orchestrates diagnostic repairs or atomic rollbacks.
---

# Failure Recovery & Self-Healing Skill

## Purpose
Enforces disciplined, structured recovery when agent tool calls, builds, tests, or network requests fail, preventing infinite loops and cascading errors.

## Failure Classification & Strategy Matrix

```mermaid
graph TD
    Fail[Failure Detected] --> Classify{Failure Category?}
    
    Classify -- Transient / Rate Limit --> Backoff[Exponential Backoff Retry (Budget: 3)]
    
    Classify -- Syntax / Compilation --> SelfHeal[Compiler Diagnostic Extraction -> Targeted Code Fix (Budget: 3)]
    
    Classify -- Test Assertion Failure --> LogicFix[Inspect Test Diff -> Adjust Implementation Logic (Budget: 2)]
    
    Classify -- Sandbox / Permission Denied --> PromptUser[Escalate to User with Explicit Permission Request]
    
    Classify -- Unrecoverable / Fatal --> Rollback[Atomic Transaction Rollback -> Notify User]
```

## Recovery Protocols
1. **Compilation / Syntax Errors**:
   - Extract exact file name, line number, and compiler message from stderr.
   - Read the surrounding 20 lines of code.
   - Apply minimal patch targeting only the missing symbol/import/bracket.
2. **Retry Budgets**:
   - Strictly limit automated fix attempts to **3 iterations**.
   - If error persists after 3 attempts, halt execution and present clear diagnostics to the user.
3. **Atomic Rollback**:
   - If an agent task encounters an unresolvable failure, offer one-tap revert to the pre-task snapshot.
