---
name: ai-context-engineering
description: >-
  Extracts, indexes, scores, and compresses workspace context, AST symbols, active files, git status, compiler diagnostics, and terminal outputs for token-efficient agent prompting.
---

# AI Context Engineering Skill

## Purpose
Maximizes agent reasoning accuracy while minimizing token overhead and latency by selectively retrieving only relevant project context.

## Context Assembly Strategy
1. **System Prompt**: Core persona, active rules, available tools, execution environment constraints.
2. **Project Index**: Lightweight file map + symbol outline (classes, functions, interfaces).
3. **Active Working Context**:
   - Currently open file and cursor position.
   - Recent compiler / linter errors.
   - Active git status (staged/unstaged diffs).
   - Recent terminal logs / test failures.
4. **Targeted Semantic & AST Retrieval**:
   - Retrieve full file content only when explicitly relevant to the user's intent.
5. **Context Window Pruning**:
   - Compress older trajectory turns into concise milestone summaries.

## Rules
- Keep initial prompt payload under 25% of the model's context window.
- Never inject binary assets or node_modules / build artifacts.
