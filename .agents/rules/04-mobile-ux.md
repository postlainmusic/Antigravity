# 04 - Mobile UX & Interaction Guidelines

## Touch-First Engineering
1. **Touch Targets**: Minimum target size of 48x48dp with at least 8dp padding between adjacent interactive elements.
2. **Ergonomic Reachability**:
   - Primary action buttons, bottom sheets, navigation bars, and keyboard accessory bars must remain in the bottom thumb zone on handheld devices.
   - Secondary and destructive actions belong behind confirmation sheets or explicit long-press / swipe interactions.
3. **Keyboard Experience**:
   - Provide a persistent, customizable code accessory bar (symbols: `{ } [ ] ( ) < > / \ $ = : ; ->`) above the virtual keyboard.
   - Smooth `imePadding` and keyboard insets transitions without layout jumps.
4. **Haptic Feedback**:
   - Deliver subtle, tactile feedback on critical actions (breakpoint toggles, git commit, agent approval, swipe actions).
