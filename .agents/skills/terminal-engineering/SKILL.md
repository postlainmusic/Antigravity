---
name: terminal-engineering
description: >-
  Builds, manages, and executes real terminal sessions, background processes, ANSI color parsing, interactive stdout/stderr streaming, and session lifecycles.
---

# Terminal Engineering Skill

## Purpose
Provides developer-grade pseudo-terminal (PTY) and shell execution management with live streaming and cancellation controls.

## Capabilities
1. **Process Management**:
   - Spawns subprocesses with customized environment variables and working directory.
   - Captures stdout and stderr streams asynchronously.
   - Supports graceful SIGINT / SIGTERM cancellation and forceful termination.
2. **Terminal UI & ANSI Support**:
   - Parses ANSI 16/256/Truecolor escape sequences into formatted Compose `AnnotatedString`s.
   - Supports auto-scroll with user pause-on-scroll-up.
   - Quick command shortcuts (e.g. `npm test`, `git status`, `ls -la`, `clear`).
3. **Session Lifecycle**:
   - Multi-tab terminal support with persistent session state and execution exit code tracking.
