# Reusable Design System Component Guidelines

## 1. Top Application Bar (`AppTopBar`)
- Height: 56dp.
- Left action: Project drawer toggle button.
- Title: Active project name (`TextPrimary`, 16sp Medium).
- Bottom border: 1dp `DarkBorder`.

## 2. Virtual Keyboard Accessory Bar (`KeyboardAccessoryBar`)
- Height: 44dp.
- Background: `DarkSurface`.
- Content: Horizontal scrolling row of developer symbol chips (`{ } [ ] ( ) ; : < > -> = $`).
- Chip padding: 4dp horizontal, 6dp vertical.

## 3. Chat Message Bubbles (`ChatMessageBubble`)
- User Bubble: `DarkSurfaceElevated` with 1dp `IndigoGlow` border; aligned to the right.
- Agent Bubble: `DarkSurface` with 1dp `DarkBorder` border; aligned to the left with Avatar icon.
- Corner radius: 14dp.

## 4. Live Preview Viewport (`PreviewScreen`)
- Viewport toggle bar: Mobile (360dp), Tablet (600dp), Full (100%).
- Integrated developer console below preview with real-time JS error capture.

## 5. Visual Git Diff Viewer (`GitDiffScreen`)
- Additions: `DiffAddGreenBg` (30% alpha) with `+` prefix.
- Deletions: `DiffDeleteRedBg` (30% alpha) with `-` prefix.
- Action Bar: Side-by-side "Discard" (Rose) and "Apply Diff" (Emerald) buttons.
