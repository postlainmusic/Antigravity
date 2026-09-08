# Design System Specification: Obsidian Design Language

## 1. Color Tokens (Dark Theme Native)

```
┌─────────────────────────────────────────────────────────────┐
│ Obsidian Palette Tokens                                     │
├──────────────────────┬─────────────┬────────────────────────┤
│ Token Name           │ Hex Value   │ Role                   │
├──────────────────────┼─────────────┼────────────────────────┤
│ DeepObsidian         │ #090B10     │ Base Canvas Background │
│ DarkSurface          │ #11141D     │ Panel / Card / Drawer  │
│ DarkSurfaceElevated  │ #181D29     │ Modal / Floating Chips │
│ DarkBorder           │ #222938     │ Structural Borders     │
│ DarkBorderSubtle     │ #1B202D     │ Secondary Dividers     │
│ IndigoGlow           │ #6366F1     │ Primary Brand Accent   │
│ IndigoGlowLight      │ #818CF8     │ Hover / Active State   │
│ CyanNeon             │ #38BDF8     │ Secondary Accent       │
│ EmeraldSuccess       │ #10B981     │ Diff Add / Passed      │
│ DiffAddGreenBg       │ #064E3B     │ Diff Add Background    │
│ RoseError            │ #EF4444     │ Diff Delete / Errors   │
│ DiffDeleteRedBg      │ #7F1D1D     │ Diff Delete Background │
│ AmberWarning         │ #F59E0B     │ Pending Approval       │
│ TextPrimary          │ #F8FAFC     │ High-Contrast Body     │
│ TextSecondary        │ #94A3B8     │ Muted Metadata         │
│ TextMuted            │ #64748B     │ Disabled / Line Numbers│
└──────────────────────┴─────────────┴────────────────────────┘
```

---

## 2. Typography Scale (Developer-Centric)

- **Display Large**: 28sp / Bold / Sans-Serif (Screen Titles)
- **Headline Medium**: 22sp / SemiBold / Sans-Serif (Section Headers)
- **Title Medium**: 16sp / Medium / Sans-Serif (Top Bar, Card Titles)
- **Body Large**: 15sp / Regular / Sans-Serif (Chat Bubbles, Editor Content)
- **Body Medium**: 13sp / Regular / Sans-Serif (Descriptions, Timestamps)
- **Label Small**: 11sp / Medium / Sans-Serif (Status Badges, File Tags)
- **Code Mono**: 13sp / Regular / JetBrains Mono (Code Editor, Terminal, Diffs)

---

## 3. Motion Language & Curves

- **Fast Micro-interaction**: `150ms` (`FastOutSlowInEasing`) - Button presses, chip toggles.
- **Panel Slide / Expansion**: `250ms` (`CubicBezier(0.2, 0.0, 0.0, 1.0)`) - File drawer, bottom sheet.
- **Spring Haptic Physics**: `dampingRatio = Spring.DampingRatioLowBouncy`, `stiffness = Spring.StiffnessMedium` - Diff apply animations, status pill pulse.

---

## 4. Reusable Component Inventory

1. **Agent Message Bubble**: Displays user/agent turns with markdown support and code snippets.
2. **Action Chip**: Fast command trigger (e.g. `npm test`, `git status`).
3. **Code Card**: Syntax-highlighted code block with copy and apply buttons.
4. **Project Card**: Project item in dashboard with last modified timestamp.
5. **Status Pill**: Compact status indicator (e.g. `LIVE PREVIEW READY`, `EXECUTING TOOL`).
6. **Tool Timeline Card**: Visual progress step card showing active tool and exit status.
7. **File Tree Row**: Recursive directory entry with folder/file icon.
8. **Diff Block**: Colored line chunk with old/new line numbers.
9. **Terminal Log Line**: ANSI color parsed terminal row.
10. **Empty State Placeholder**: Icon, title, description, and primary CTA button.
11. **Humane Error Card**: Explains what happened, why, and provides an "Ask AI to Fix" button.
12. **Virtual Keyboard Accessory Bar**: Horizontal scrollable symbol bar (`{ } [ ] ( ) ; : < > -> $`).
