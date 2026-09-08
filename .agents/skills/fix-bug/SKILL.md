---
name: fix-bug
description: >-
  Meta-skill orchestrating systematic bug resolution: reproduction, log analysis, root cause isolation, minimal surgical fix, regression testing, and verification.
---

# /fix-bug Meta-Skill

## Purpose
Systematic defect remediation with zero regressions.

## Execution Sequence
1. **Reproduce & Capture**:
   - Parse error stack trace or reproduce anomalous behavior with a failing unit test.
2. **Diagnose & Isolate**:
   - Activate `debugging` to inspect exact variable states, coroutine lifecycles, and data invariants.
3. **Form Hypothesis & Implement Fix**:
   - Apply the minimal surgical patch to address the root cause directly.
4. **Verify & Regress**:
   - Re-run the reproducing test to confirm resolution.
   - Run the entire test suite to guarantee zero collateral breakage.
5. **Document**:
   - Add notes to `docs/PROJECT_MEMORY.md` if the defect uncovered a broader edge case.
