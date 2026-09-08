---
name: testing-engineering
description: >-
  Designs, writes, executes, and validates automated unit tests, integration tests, Compose UI tests, and agent golden-loop regression suites.
---

# Testing Engineering Skill

## Purpose
Ensures rock-solid reliability across all codebase layers through comprehensive, automated test coverage and fast feedback loops.

## Test Strategy
1. **Unit Testing (`src/test/`)**:
   - Test ViewModels with `Turbine` and `kotlinx-coroutines-test`.
   - Test Use Cases, State Reducers, AST Parsers, Context Compressors, and Diff Algorithms.
2. **Integration Testing**:
   - Test MCP client/server handshakes and Tool Registry execution.
   - Test repository caching and Room database queries with in-memory SQLite.
3. **UI & Compose Testing (`src/androidTest/`)**:
   - Compose test rules verifying user interaction flows, accessibility nodes, and state transitions.
4. **End-to-End Agent Suites**:
   - Validate full agent execution: User prompt -> Plan -> Code generation -> Compilation -> Verification.
