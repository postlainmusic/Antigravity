---
name: build-ui
description: >-
  Meta-skill orchestrating Jetpack Compose screen and component creation: design system token usage, state hoisting, micro-animations, adaptive multi-pane layouts, and accessibility.
---

# /build-ui Meta-Skill

## Purpose
End-to-end workflow for delivering polished, cinematic, touch-first Jetpack Compose interfaces.

## Execution Sequence
1. **Design System Grounding**:
   - Check `design-system` tokens (colors, typography, spacing, shapes).
2. **State & Interaction Modeling**:
   - Define immutable `UiState` (Idle, Loading, Content, Error, Streaming) and `UiEvent` intents.
3. **Compose Implementation**:
   - Activate `compose-ui` and `mobile-ux`.
   - Implement composables with state hoisting and stable parameters.
   - Add keyboard accessory bars and touch gestures.
4. **Adaptive Layouts**:
   - Test across Compact (phone), Medium (foldable), and Expanded (tablet) breakpoints.
5. **Accessibility & Review**:
   - Activate `accessibility` and `ui-review` for semantics, contrast, and visual rhythm.
