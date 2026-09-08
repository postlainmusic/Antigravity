---
name: filesystem-engineering
description: >-
  Manages workspace file trees, file watching, atomic reads/writes, project search, indexing, file creation/deletion, and path traversal security guards.
---

# Filesystem Engineering Skill

## Purpose
Provides fast, safe, and reactive file operations and workspace tree navigation for mobile projects.

## Capabilities
1. **Workspace Tree Explorer**:
   - Recursive directory traversal with lazy folder expansion.
   - File icon mapping based on extension (Kotlin, Java, JS, TS, JSON, Markdown, etc.).
   - Hidden file filtering (`.git`, `build`, `node_modules`).
2. **Atomic File Operations**:
   - Atomic file writes using temporary staging files to avoid corruption on crashes.
   - Real-time file system watchers notifying UI and agent of external changes.
3. **Search & Indexing**:
   - Fast full-text grep and file path globbing.
   - Path normalization checking to strictly prevent path traversal outside workspace bounds.
