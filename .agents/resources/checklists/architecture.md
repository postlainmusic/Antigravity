# Architecture & Quality Checklist

- [ ] Clear separation of concerns between Data, Domain, and Presentation layers.
- [ ] No direct Android framework dependencies inside Domain Use Cases.
- [ ] Unidirectional Data Flow (UDF) enforced; State flows down, Events flow up.
- [ ] Immutable models used across layer boundaries.
- [ ] Thread safety: All disk/network I/O dispatched to `Dispatchers.IO`.
- [ ] Structured concurrency: No orphan `GlobalScope.launch` calls.
- [ ] ADR documented in `docs/DECISIONS.md` for major architectural additions.
