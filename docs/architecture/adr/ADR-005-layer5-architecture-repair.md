# ADR-005: Layer 5 Architecture Contract Reconciliation & Composition Root

## Status
Accepted

## Context
During CI integration and unit testing, architectural drift was identified across multiple core components:
1. `PermissionManager` had non-exhaustive `when` statements missing `PermissionTier.DESTRUCTIVE` and lacked explicit evaluation for the 5 Autonomy Levels.
2. `MainViewModel` bypassed `ModelRouter` and directly instantiated `GeminiModelProvider`, violating dependency inversion and capability-based routing.
3. `UIComponents.kt` was missing an import for `fillMaxSize`.
4. `AgentStatus.ERROR` was referenced instead of the canonical `AgentStatus.FAILED`.

## Decision
1. Reconcile all domain models and state enums to be internally consistent.
2. Introduce `AppContainer` as the canonical composition root for the application, constructing `SandboxManager`, `CredentialStore`, `ModelRouter`, `ToolRegistry`, `ContextEngine`, `PermissionManager`, `DiffManager`, and `AgentOrchestrator`.
3. Harden `PermissionManager` with exhaustive `evaluatePermission(tool, autonomyLevel)` returning `PermissionDecision`.
4. Require `MainViewModel` to consume `AppContainer` via constructor injection.

## Consequences
- **Positive**: Clean separation of concerns, zero god-object coupling in ViewModels, 100% compile-time type safety across all `when` expressions, deterministic multi-provider model routing.
- **Negative**: Requires maintaining `AppContainer` as new core services are added.
