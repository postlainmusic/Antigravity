---
name: compose-ui
description: >-
  Implements and optimizes Jetpack Compose UI with recomposition-safe patterns, state hoisting, adaptive layouts, smooth animations, and Material 3 design tokens.
---

# Compose UI Skill

## Purpose
Builds visually stunning, highly performant, and responsive Jetpack Compose components for smartphones, foldables, and tablets.

## Key Directives
1. **Recomposition Guardrails**:
   - Use `@Stable` and `@Immutable` models.
   - Use `key` in `LazyColumn` / `LazyRow` items.
   - Derive transient state using `derivedStateOf`.
   - Never write to state variables inside composable render bodies.
2. **Adaptive Layouts**:
   - Compact (< 600dp width): Single pane with bottom navigation and sliding bottom sheets.
   - Medium / Expanded (>= 600dp width): Dual-pane or triple-pane (File Tree | Editor/Agent | Preview/Terminal).
3. **Animations & Micro-interactions**:
   - Use `AnimatedVisibility`, `Crossfade`, and `animateContentSize` for polished state transitions.
   - Add spring physics (`spring(dampingRatio = Spring.DampingRatioLowBouncy)`) for snappy tactile feel.

## Workflow
1. Scaffold layout using `Scaffold`, `TopAppBar`, and adaptive containers.
2. Build leaf components passing state values and lambda callbacks (`onClick`, `onValueChange`).
3. Add accessibility semantics (`contentDescription`, `heading`, `stateDescription`).
4. Validate rendering performance with Layout Inspector / Compose compiler metrics.
