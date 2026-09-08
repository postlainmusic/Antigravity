# 10 - Git & Version Control Guidelines

## Standards
1. **Safety First**:
   - Never run destructive git commands (`git reset --hard`, `git push --force`, `git clean -fdx`) without explicit user consent.
   - Always check `git status` and dirty working tree state before switching branches or applying agent diffs.
2. **Commit Conventions**:
   - Follow Conventional Commits: `feat(scope): ...`, `fix(scope): ...`, `refactor(scope): ...`, `test(scope): ...`, `docs(scope): ...`.
   - Keep commits atomic and focused with clear explanatory descriptions.
