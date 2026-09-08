---
name: visual-qa
description: >-
  Visual quality assurance & mobile ergonomics audit skill: inspects layout alignment, 48dp touch targets, WCAG AA contrast, keyboard insets, and dark obsidian theme fidelity across phone and tablet viewports.
---

# Visual QA & Mobile UX Audit Skill

## Purpose
Enforces uncompromising visual polish, tactile responsiveness, and aesthetic restraint across all mobile interfaces, preventing layout jumps, clipping, and touch target overlap.

## Visual Inspection Checklist

### 1. Spacing, Alignment & Grid
- [ ] 4dp/8dp grid adhered to across all padding, margins, and card insets.
- [ ] Visual anchors aligned along clear horizontal/vertical axes.
- [ ] No awkward single-word wrapping on headers or labels.

### 2. Touch Ergonomics & Reachability
- [ ] Minimum 48x48dp interactive touch target bounds on all buttons, icons, and list rows.
- [ ] Primary action buttons situated in the bottom 40% thumb zone on phone screens.
- [ ] Adequate padding (minimum 8dp) between adjacent interactive chips.

### 3. Keyboard & Insets Behavior
- [ ] Virtual keyboard accessory bar floats directly above IME without layout flicker or occlusion.
- [ ] Screen content scrolls smoothly when the keyboard opens (`WindowInsets.ime`).

### 4. Theme & Contrast (Obsidian Dark)
- [ ] WCAG AA 4.5:1 contrast maintained for all body text against `#090B10` and `#11141D`.
- [ ] Restrained use of brand indigo `#6366F1` and cyan `#38BDF8` accents; zero gratuitous neon clutter.

### 5. Multi-State Completeness
- [ ] **Idle State**: Clean, inviting empty placeholder with clear CTA.
- [ ] **Loading State**: Subtle pulsing skeleton / spinner without layout shifts.
- [ ] **Streaming State**: Real-time token streaming with smooth auto-scroll.
- [ ] **Error State**: Actionable error banner with retry button.
