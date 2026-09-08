# Git Reference Guide

### What is this?
Version control integration, staging, diff rendering, commit creation, and branch management.

### Diff Calculation Engine
- Computes unified and side-by-side diff chunks using Myers diff algorithm.
- Classifies changes into:
  - `DiffChunk.Addition` (Green `#10B981`)
  - `DiffChunk.Deletion` (Red `#EF4444`)
  - `DiffChunk.Context` (Unchanged `#94A3B8`)
- Allows users to review, stage individual hunks, or perform full rollback.
