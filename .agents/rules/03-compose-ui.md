# 03 - Jetpack Compose UI Guidelines

## Principles
1. **Recomposition Safety**:
   - Never perform side-effects directly in composable bodies. Use `LaunchedEffect`, `rememberCoroutineScope`, or `DisposableEffect`.
   - Prefer stable and immutable data types (`@Immutable`, `@Stable`, `kotlinx.collections.immutable`) to prevent unnecessary recompositions.
   - Hoist state to the highest relevant caller to ensure maximum reusability and testability.
2. **Layout & Responsiveness**:
   - Support adaptive window size classes (Compact, Medium, Expanded) for phones, foldables, and tablets.
   - Use `BoxWithConstraints` or WindowSizeClass APIs for dynamic multi-pane layouts.
3. **Design System Adherence**:
   - Use centralized typography, color tokens, shapes, and elevations from the project design system.
   - Avoid hardcoded DP/SP values or ad-hoc raw colors in leaf composables.
