# 15 - Documentation & Knowledge Preservation Guidelines

## Standards
1. **Living Documentation**:
   - `docs/PROJECT_MEMORY.md`, `docs/DECISIONS.md`, `docs/ARCHITECTURE.md`, `docs/TASKS.md`, and `docs/ROADMAP.md` must be updated alongside every major PR/commit.
2. **Architecture Decision Records (ADR)**:
   - Any significant design choice (e.g. database choice, editor rendering engine, IPC transport) must be documented as an ADR in `docs/DECISIONS.md` with Context, Decision, Alternatives, and Consequences.
3. **API & Code Comments**:
   - Public APIs, domain use cases, and complex state machine transitions must have clean Kotlin KDoc / JSDoc comments.
