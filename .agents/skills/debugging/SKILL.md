---
name: debugging
description: >-
  Reproduces issues, isolates root causes from stack traces and compiler outputs, implements minimal surgical fixes, and verifies regression-free recovery.
---

# Debugging Skill

## Purpose
Enables systematic root-cause identification and automated self-healing when runtime errors, compilation failures, or agent malfunctions occur.

## Workflow
1. **Error Capture & Diagnosis**:
   - Parse exact stack traces, line numbers, and error messages from build output / logcat / stderr.
2. **Isolate Root Cause**:
   - Inspect the exact source lines and dependencies involved.
   - Form a concrete hypothesis before modifying code.
3. **Surgical Fix**:
   - Apply the minimal necessary patch to resolve the defect without unintended side-effects.
4. **Verification**:
   - Re-run the failed test or compiler command to confirm the resolution.
   - Execute full regression test suite to ensure no collateral breakage.
