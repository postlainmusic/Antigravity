---
name: product-architecture
description: >-
  Defines modular system architecture, cross-cutting boundaries, subsystem communication protocols, data flows, and maintains Architecture Decision Records (ADRs).
---

# Product Architecture Skill

## Purpose
Establishes the structural backbone of the entire product, ensuring high cohesion, low coupling, and scalability across platform subsystems.

## When to Use
- Designing new modules, IPC channels, or major subsystems.
- Evaluating architectural tradeoffs (e.g. SQLite vs Room vs DataStore, Local LLM vs Cloud API).
- Authoring Architecture Decision Records (ADRs).

## Subsystem Map
1. **Core UI & Presentation**: Jetpack Compose, ViewModels, Design System, Navigation.
2. **AI Agent Platform**: Orchestration, State Machine, Context Engine, Tool Dispatcher.
3. **Execution & Tools**: Code Editor Engine, Terminal Subsystem, Local HTTP Dev Server / WebView Preview.
4. **Data & Storage**: Project Workspace Manager, Git Manager, Secure Credentials Store.
5. **Ecosystem & MCP**: Model Context Protocol client for external tools and services.

## Workflow
1. Analyze functional requirements and non-functional constraints (latency, memory, battery).
2. Define interface contracts between subsystems using Kotlin interfaces and immutable data transfer objects.
3. Document decisions in `docs/DECISIONS.md` using the ADR format.
4. Validate architectural boundaries (ensure no cyclic dependencies or UI leaks).
