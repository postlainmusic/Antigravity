# 00 - Master Engineering Constitution

## Mission Statement
Never optimize for "code generated". Optimize for "feature actually works".
Every component created in this repository must meet enterprise-grade quality, robustness, accessibility, security, and performance standards.

---

## 1. Core Engineering Principles

1. **Reality Over Illusion**: Never fake terminal output, editor features, compilation steps, or test results. Either a capability is fully functional or its current limits are clearly stated.
2. **Inspect Before Modifying**: Never edit or create code blindly. Inspect existing schemas, interfaces, tests, and environment tools first.
3. **Fail-Safe by Default**: Unhandled errors, crashes, and memory leaks are unacceptable. All I/O, network calls, file edits, and agent actions must be wrapped in structured error handling with graceful recovery.
4. **Token & Context Conservation**: Do not dump massive dumps into prompts or skills. Use progressive disclosure, precise indexing, and modular references.
5. **Clean Architecture & Unidirectional Data Flow**: Separation of concerns between Data, Domain, and Presentation layers is mandatory. State flows down; events flow up.

---

## 2. Quality Gates & Definition of Done

A feature, module, or change is **DONE** only when:
- [ ] Code is cleanly formatted and passes static analysis / linter checks.
- [ ] Unit and integration tests are written and passing.
- [ ] No regressions or memory leaks are introduced.
- [ ] Error states, empty states, and loading states are fully designed and handled.
- [ ] Security boundaries (sandbox, path traversal, token masking) are enforced.
- [ ] Documentation and Architecture Decision Records (ADRs) are updated.

---

## 3. Communication & Escalation Standard

- Make autonomous, professional decisions for implementation details, algorithms, refactorings, and optimizations.
- Escalate to the human engineer only for:
  - High-impact scope changes
  - External paid service integrations or credential requirements
  - Irreversible destructive operations on production data or remote repositories
  - Major architectural paradigm shifts
