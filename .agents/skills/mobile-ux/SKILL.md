---
name: mobile-ux
description: >-
  Designs and validates touch-first ergonomics, gestures, virtual keyboard accessory bars, bottom sheets, navigation patterns, and adaptive mobile interactions.
---

# Mobile UX Skill

## Purpose
Ensures that developer tooling and AI interactions feel natural, effortless, and lightning-fast on mobile devices without desktop clunkiness.

## Ergonomic Blueprint
1. **Thumb Zone Architecture**:
   - Keep high-frequency touch interactions (prompt bar, run button, diff accept/reject, file quick-switcher) in the bottom 40% of the screen.
2. **Keyboard Handling**:
   - Float a 44dp accessory bar above the IME keyboard with developer symbols: `{ } ( ) [ ] ; : " ' = + - * / \ < > ! ? $ # @ & |`.
   - Prevent layout flicker on keyboard open/close using `WindowInsets.ime`.
3. **Gestures**:
   - Swipe from left edge: Toggle Project File Tree drawer.
   - Swipe down on sheet: Collapse secondary panel.
   - Double-tap editor line: Quick agent action menu (Explain, Refactor, Add Test).
4. **Haptics**:
   - Provide subtle haptic feedback (`HapticFeedbackType.LongPress` / `TextHandleMove`) on key state events.
