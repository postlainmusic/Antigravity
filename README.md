# Antigravity Mobile: AI-Native Software Engineering for Android

<div align="center">

**An autonomous, touch-first AI software engineering environment designed specifically for Android.**

[![CI](https://img.shields.io/badge/CI-GitHub%20Actions-brightgreen.svg)](.github/workflows/ci.yml)
[![Security](https://img.shields.io/badge/Security-Red%20Team%20100%25-blue.svg)](.github/workflows/security.yml)
[![Platform](https://img.shields.io/badge/platform-Android%20%7C%20Jetpack%20Compose-blue.svg)]()
[![Kotlin](https://img.shields.io/badge/kotlin-2.0+-purple.svg)]()
[![License](https://img.shields.io/badge/license-Apache%202.0-orange.svg)]()

</div>

---

## 🌟 Key Capabilities

- 🤖 **Autonomous AI Agent Platform**: Natural language to verified code with capability-based routing, multi-step planning, tool calling, multi-file edits, and bounded self-healing compiler recovery.
- ⚡ **Multi-Model Abstraction**: Native streaming integration with Google Gemini 1.5 Pro, Anthropic Claude 3.5 Sonnet, OpenAI GPT-4o, and on-device Gemma 2B models.
- 📱 **Touch-First Mobile UX**: Ergonomic bottom-zone interaction, virtual keyboard accessory bar for code symbols (`{ } [ ] ( ) ; : < > -> $`), and adaptive phone/tablet layouts.
- 💻 **Virtualized Native Code Editor**: 60/120 FPS syntax highlighting, line virtualization, search/replace, and diagnostic squiggles in pure Jetpack Compose.
- 🖥️ **Integrated Terminal (PTY)**: Multi-session shell execution with ANSI color rendering and streaming stdout/stderr.
- 🌐 **Live Embedded Preview**: Local HTTP dev server on `127.0.0.1`, embedded sandboxed WebView, and bi-directional JavaScript console bridge.
- 🔀 **Visual Git & Diff Viewer**: Side-by-side and unified diffs, hunk-level staging, AI commit generator, and multi-tier atomic rollback.
- 🛡️ **Tiered Security Sandbox**: 6-boundary isolation matrix, Android KeyStore AES-256-GCM encryption, and secret redaction.

---

## 🚀 CI/CD & Device Deployment

Antigravity Mobile features a full GitHub Actions automated build factory:

```bash
# 1. Run local CI/CD health check
node .agents/scripts/ci-health.js

# 2. Run automated test suites & evidence audit
node .agents/scripts/test-all.js
node .agents/scripts/reproduce-validation.js

# 3. Build & Install on connected Android device via ADB
node .agents/scripts/deploy-device.js
```

Detailed CI/CD documentation and APK download instructions: [docs/GITHUB_CI_CD.md](docs/GITHUB_CI_CD.md).

---

## 🧠 AI Engineering Brain (`.agents/`)

This workspace houses a self-improving intelligence system:
- **Rules (`.agents/rules/`)**: 16 focused engineering constitutions.
- **Skills (`.agents/skills/`)**: 37 domain and meta-skills with progressive disclosure.
- **Decision Trees (`.agents/resources/decision-trees/`)**: 12 architectural trade-off trees.
- **Checklists (`.agents/resources/checklists/`)**: 11 quality gates.
- **Scripts (`.agents/scripts/`)**: Cross-platform automation scripts.

---

## 🏛️ Architecture & Governance Documentation

- [System Architecture](docs/ARCHITECTURE.md)
- [Security Boundaries & Matrix](docs/SECURITY_BOUNDARIES.md)
- [Agent 14-State Orchestration](docs/AGENT_ARCHITECTURE.md)
- [Context Engineering V2](docs/CONTEXT_ARCHITECTURE.md)
- [Tool Platform](docs/TOOL_ARCHITECTURE.md)
- [MCP Architecture](docs/MCP_ARCHITECTURE.md)
- [Formal ADRs (001-010)](docs/ARCHITECTURE_DECISIONS.md)
- [GitHub CI/CD Guide](docs/GITHUB_CI_CD.md)
- [GitHub CI/CD Validation Report](docs/GITHUB_CI_CD_VALIDATION.md)
- [Evaluation Framework & Golden Tasks](docs/EVALUATION_FRAMEWORK.md)

---

## 📄 License
Apache License 2.0. Built with Google Antigravity.
