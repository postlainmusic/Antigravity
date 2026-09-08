# Antigravity Product Roadmap

## Milestone 1: Engineering Intelligence Platform (Phase A) - COMPLETED
- [x] Environment and host runtime audit (`docs/ENGINEERING_ENVIRONMENT.md`).
- [x] Master engineering constitution & 15 specialized rules (`.agents/rules/`).
- [x] Complete domain & meta skill library (32 skills in `.agents/skills/`).
- [x] Decision trees & engineering checklists (`.agents/resources/`).
- [x] Cross-platform automation scripts (`.agents/scripts/`).
- [x] Hooks and MCP server configs (`.agents/hooks.json`, `.agents/mcp_config.json`).
- [x] Living documentation and architecture records.

## Milestone 2: Android Core Infrastructure & AI Platform (Phase B1) - IN PROGRESS
- [ ] Android Gradle project scaffolding with Kotlin 2.0 & Jetpack Compose BOM.
- [ ] Core Design System (Obsidian theme, Color tokens, Typography scales, Shape tokens).
- [ ] Model Provider Engine (Gemini, Claude, OpenAI, Local Ollama / Mock fallback).
- [ ] AI Agent Orchestrator & State Machine with real-time UI event stream.
- [ ] Tool Registry & Sandboxed Tool Executor (File Read, Write, Grep, Terminal, Git).

## Milestone 3: Editor, Terminal, Git & Preview Subsystems (Phase B2)
- [ ] Virtualized Compose Code Editor with syntax highlighting & keyboard accessory bar.
- [ ] Terminal PTY & session manager with ANSI rendering.
- [ ] Live embedded web preview with WebView console bridge.
- [ ] Visual Git Diff Viewer with hunk staging & commit generator.

## Milestone 4: Polish, Testing & Release Hardening (Phase B3)
- [ ] Unit & UI test suites.
- [ ] Tablet / Foldable adaptive multi-pane layout testing.
- [ ] ProGuard / R8 optimization & release APK signing.
