# 11 - Code Quality & Static Analysis Guidelines

## Kotlin & Multiplatform Best Practices
1. **Null Safety & Immutability**:
   - Prefer `val` over `var`. Never use `!!` (force unwrap); use `?.let`, `?:`, or explicit `requireNotNull` with contextual error messages.
2. **Exhaustive Enums & Sealed Hierarchies**:
   - Use `sealed interface` or `sealed class` for UI states, agent events, and domain outcomes. Ensure `when` expressions are exhaustive without relying on default `else` unless strictly justified.
3. **Structured Concurrency**:
   - Never launch unmanaged coroutines (`GlobalScope`). Always bind coroutines to `viewModelScope`, `lifecycleScope`, or structured supervisor scopes.
