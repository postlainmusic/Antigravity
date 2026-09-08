---
name: accessibility
description: >-
  Audits and implements TalkBack screen reader support, accessible touch targets, WCAG AA color contrast, dynamic font scaling, and keyboard/switch navigation.
---

# Accessibility Skill

## Purpose
Ensures the mobile development environment is universally accessible to all engineers, including those using assistive technologies.

## Checkpoints
1. **TalkBack & Semantics**:
   - Explicit `contentDescription` on all icon buttons and graphic elements.
   - Group related semantic information using `Modifier.semantics(mergeDescendants = true)`.
2. **Touch Targets**:
   - Minimum 48x48dp interactive touch target bounds.
3. **Typography & Contrast**:
   - Support Android system font scaling from 100% up to 200% without layout clipping.
   - Maintain at least 4.5:1 contrast ratio for normal text and 3:1 for large UI components.
4. **Motion & Transitions**:
   - Honor system reduced motion preferences to minimize vestibular discomfort.
