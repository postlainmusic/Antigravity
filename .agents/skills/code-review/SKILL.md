---
name: code-review
description: >-
  Performs rigorous peer review across Kotlin code, architecture adherence, error handling, security considerations, test coverage, and performance.
---

# Code Review Skill

## Purpose
Provides critical, independent code analysis to prevent defects, anti-patterns, security vulnerabilities, or performance bottlenecks from entering the codebase.

## Review Dimensions
1. **Correctness & Logic**: Does the code accurately meet requirements? Are edge cases (nullability, empty collections, network loss) handled?
2. **Architecture & Clean Code**: Does it follow MVI/UDF? Is business logic decoupled from presentation?
3. **Security & Sandbox**: Are paths normalized? Are secrets protected? Are dangerous commands gated?
4. **Performance**: Is work kept off the Main thread? Are recompositions minimized?
5. **Test Coverage**: Are unit and integration tests present and passing?
