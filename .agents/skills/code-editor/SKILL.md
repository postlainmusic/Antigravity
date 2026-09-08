---
name: code-editor
description: >-
  Architects, implements, and tunes the mobile code editor component with syntax highlighting, line numbers, cursor/selection handling, undo/redo, code folding, and touch interactions.
---

# Code Editor Skill

## Purpose
Builds a high-performance, touch-friendly native code editor in Jetpack Compose capable of rendering large source files smoothly at 60/120 FPS.

## Features & Architecture
1. **Virtualized Line Rendering**:
   - Uses `LazyColumn` or custom Canvas-backed text layout to render only visible lines on screen.
2. **Syntax Highlighting**:
   - Tokenizes language keywords, strings, comments, types, and numbers asynchronously.
   - Highlights Kotlin, Java, JavaScript, TypeScript, HTML, CSS, JSON, Markdown, and Shell scripts.
3. **Touch & Cursor Ergonomics**:
   - Magnifier loupe during drag-selection.
   - Double-tap word selection, triple-tap line selection.
   - Persistent virtual keyboard accessory bar for code symbols.
4. **Editor Operations**:
   - Search & Replace with Regex support.
   - Multi-level Undo / Redo buffer.
   - Real-time diagnostic squiggles for compiler/linter errors.
