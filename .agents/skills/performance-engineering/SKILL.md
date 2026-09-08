---
name: performance-engineering
description: >-
  Profiles, benchmarks, and optimizes app startup time, frame rendering (60/120fps), Compose recompositions, memory footprints, CPU utilization, and battery drain.
---

# Performance Engineering Skill

## Purpose
Guarantees lightning-fast startup, buttery smooth 60/120 FPS interactions, low memory usage, and zero UI thread jank.

## Optimization Areas
1. **Compose Rendering**:
   - Eliminate unnecessary recompositions using `@Stable`, `@Immutable`, and `derivedStateOf`.
   - Use `key` in `LazyColumn` and virtualize heavy code rendering.
2. **Background Concurrency**:
   - Shift AST parsing, syntax highlighting, grep indexing, and disk I/O to `Dispatchers.Default` and `Dispatchers.IO`.
3. **Memory & Cache Management**:
   - Bound LRU caches for AST syntax trees and file buffers.
   - Implement `onTrimMemory` listeners to purge ephemeral caches on low memory warnings.
4. **Network & Token Efficiency**:
   - Stream model responses and batch UI redraws to avoid excessive view updates.
