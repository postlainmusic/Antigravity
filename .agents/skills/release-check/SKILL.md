---
name: release-check
description: >-
  Meta-skill executing comprehensive release gate validation: static analysis, security audits, test suites, performance benchmarks, and release build verification.
---

# /release-check Meta-Skill

## Purpose
Comprehensive pre-flight checklist and validation gate before promoting a build to release.

## Execution Sequence
1. **Dependency & Security Audit**:
   - Run `node .agents/scripts/dependency-audit.js` and `node .agents/scripts/security-check.js`.
2. **Static Analysis & Linting**:
   - Run `node .agents/scripts/lint-all.js`.
3. **Automated Test Suite**:
   - Run `node .agents/scripts/test-all.js`.
4. **Performance & Memory Audit**:
   - Run `node .agents/scripts/performance-check.js`.
5. **Release Build Compilation**:
   - Run `node .agents/scripts/build-release.js`.
6. **Final Gate Sign-off**:
   - Verify all checks pass with 0 errors before generating release tags.
