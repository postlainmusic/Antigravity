# Antigravity Project Memory

## 1. Product Vision & Mission
Build an autonomous AI-native software engineering environment designed specifically for Android.
The environment empowers developers and vibe coders to plan, write, test, debug, and preview applications effortlessly on mobile and tablet devices with touch-first ergonomics.

---

## 2. System Architecture & Capabilities
- **Platform**: Android Native with Kotlin 2.0+ and Jetpack Compose (Material 3).
- **Architecture**: Clean Architecture (Data, Domain, Presentation) with MVI / Unidirectional Data Flow.
- **AI Core**: Multi-provider LLM abstraction (Gemini, Claude, GPT, Local Gemma/ONNX), streaming token parsing, tool calling engine, AST/symbol context engine, transaction rollback.
- **Tooling Subsystems**:
  - Native Compose virtualized code editor with keyboard symbol accessory bar.
  - Pseudo-terminal (PTY) session manager with ANSI rendering.
  - In-app live web & UI preview with embedded WebView and JS console bridge.
  - Visual Git diff viewer and commit assistant.
  - Multi-tier security sandbox with path traversal defense.

---

## 3. Current Implementation Status
- **Phase A (Intelligence System)**: 100% COMPLETE.
  - 16 Specialized Engineering Rules in `.agents/rules/`.
  - 32 Domain & Meta Skills in `.agents/skills/`.
  - 12 Decision Trees in `.agents/resources/decision-trees/`.
  - 11 Quality Checklists in `.agents/resources/checklists/`.
  - 10 Cross-Platform Automation Scripts in `.agents/scripts/`.
  - 10 Subagent Personas in `.agents/agents/`.
  - 10 Reference Guides in `.agents/resources/`.
  - Lifecycle Hooks and MCP configurations.
- **Phase B (Android Application Implementation)**: IN PROGRESS.

---

## 4. Technical Debt & Open Considerations
- Gradle toolchain setup on host vs containerized builds.
- Dynamic syntax highlighter cache eviction under extreme memory pressure.
