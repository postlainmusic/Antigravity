---
name: sandbox-engineering
description: >-
  Enforces process isolation, execution boundaries, memory limits, path restriction, and dangerous command interception across agent operations.
---

# Sandbox Engineering Skill

## Purpose
Guarantees that autonomous AI operations and tool commands cannot harm host OS resources, steal private tokens, or compromise user files.

## Sandboxing Layers
1. **Filesystem Isolation**:
   - Every file operation is validated against the active project's root canonical path.
   - Any path resolving outside workspace root throws `SecurityException` and immediately halts execution.
2. **Process Execution Restrictions**:
   - Command blacklist/whitelist filtering (`rm -rf /`, `mkfs`, fork bombs, unauthorized network sockets).
   - Enforce timeouts (default 30s) and memory caps on spawned sub-processes.
3. **Permission Tiers**:
   - `SAFE`: Auto-executed (e.g. read files, list directories, git status).
   - `MODERATE`: Policy-dependent (e.g. file edit, npm install, build command).
   - `DANGEROUS`: Strict user confirmation required (e.g. file deletion, git push force, destructive resets).
