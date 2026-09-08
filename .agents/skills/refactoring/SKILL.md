---
name: refactoring
description: >-
  Simplifies, decouples, modularizes, and cleans up complex code paths while strictly preserving existing functional behavior and passing tests.
---

# Refactoring Skill

## Purpose
Improves internal code quality, readability, maintainability, and extensibility without modifying external behavior.

## Strategy
1. **Safety Net**: Verify all relevant unit and regression tests pass before initiating any refactoring.
2. **Small Incremental Steps**:
   - Extract Methods / Use Cases.
   - Introduce Parameter Objects or Sealed Class states.
   - Replace complex boolean flags with explicit state machines.
3. **Continuous Verification**: Re-run tests after each individual refactoring step.
4. **Zero Regression**: Ensure 100% test pass rate and no altered public API contracts.
