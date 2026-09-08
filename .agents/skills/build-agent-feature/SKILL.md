---
name: build-agent-feature
description: >-
  Meta-skill orchestrating agent platform development: tool schema definition, state machine extensions, context retrieval scoring, permission tiering, and self-healing tests.
---

# /build-agent-feature Meta-Skill

## Purpose
End-to-end development of AI agent tools, reasoning loops, model providers, and autonomous execution capabilities.

## Execution Sequence
1. **Define Tool Contract**:
   - Write strongly-typed JSON schema with parameter definitions, types, and descriptions.
2. **Implement Tool Executor**:
   - Implement execution logic in Kotlin/Java with timeout and cancellation hooks.
3. **Assign Security Tier**:
   - Classify as `SAFE`, `MODERATE`, or `DANGEROUS` and register with `PermissionManager`.
4. **Context & Prompt Integration**:
   - Update `ContextEngine` and system prompts to make the tool discoverable to LLMs.
5. **Testing & Failure Recovery**:
   - Write unit tests covering normal execution, timeout, parameter validation error, and agent self-repair recovery.
