# Antigravity Engineering Capability Matrix

| Category | Capability | Available | Quality | Skill | Rule | Tool | MCP | Script | Test | Owner | Priority | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Product** | Product Discovery & PRDs | Yes | High | `product-discovery` | `00-master` | N/A | N/A | N/A | `test-all.js` | Product CTO | High | ACTIVE |
| **Product** | Architecture Decisions (ADR)| Yes | High | `product-architecture`| `00-master` | N/A | N/A | N/A | `test-all.js` | System Architect | Critical | ACTIVE |
| **Android** | Modern Android Architecture | Yes | High | `android-architecture`| `02-android` | N/A | N/A | `build-debug`| Unit Tests | Android Principal | Critical | ACTIVE |
| **Android** | Coroutines & Flow UDF | Yes | High | `android-architecture`| `02-android` | N/A | N/A | `test-all.js`| Unit Tests | Android Principal | Critical | ACTIVE |
| **Compose** | Jetpack Compose BOM 2024+ | Yes | High | `compose-ui` | `03-compose` | N/A | N/A | `lint-all.js`| Compose UI | UI Director | Critical | ACTIVE |
| **Compose** | Recomposition Guardrails | Yes | High | `compose-ui` | `03-compose` | N/A | N/A | `lint-all.js`| Compose UI | UI Director | High | ACTIVE |
| **UI** | Dark Obsidian Design System | Yes | High | `design-system` | `13-design` | N/A | N/A | `format-all` | Compose UI | Visual Design Dir | High | ACTIVE |
| **UI** | Visual QA & Layout Checks | Yes | High | `visual-qa` | `13-design` | N/A | Chrome | `test-all.js`| Golden Tests | Visual Design Dir | High | ACTIVE |
| **UX** | Touch-first Thumb Ergonomics | Yes | High | `mobile-ux` | `04-mobile` | N/A | N/A | N/A | Compose UI | Mobile UX Director| High | ACTIVE |
| **UX** | Keyboard Symbol Accessory | Yes | High | `mobile-ux` | `04-mobile` | N/A | N/A | N/A | Compose UI | Mobile UX Director| High | ACTIVE |
| **AI** | Multi-Model Abstraction | Yes | High | `ai-agent-architecture`|`05-ai-agent`| N/A | N/A | N/A | Unit Tests | AI Agent Architect| Critical | ACTIVE |
| **AI** | Prompt Injection Defense | Yes | High | `prompt-injection-defense`|`07-security`| N/A| N/A | `security-check`| Red Team | Security Engineer | Critical | ACTIVE |
| **Agent** | CTO Orchestrator Engine | Yes | High | `cto-orchestration` | `00-master` | Registry | N/A | `test-all.js`| Golden Tests | Product CTO | Critical | ACTIVE |
| **Agent** | Deterministic State Machine | Yes | High | `ai-agent-architecture`|`05-ai-agent`| Registry | N/A | `test-all.js`| Unit Tests | AI Agent Architect| Critical | ACTIVE |
| **Context** | Token Budgeting & Scoring | Yes | High | `ai-context-engineering`|`05-ai-agent`| `grep_search`| N/A| `performance-check`| Unit Tests | Context Engineer | Critical | ACTIVE |
| **MCP** | Stdio/SSE Client Engine | Yes | High | `mcp-engineering` | `06-mcp` | Registry | Local | N/A | Unit Tests | MCP Architect | High | ACTIVE |
| **Filesystem**| Sandboxed Canonical Tree | Yes | High | `filesystem-engineering`|`07-security`| `read_file`| N/A | `security-check`| Unit Tests | Security Engineer | Critical | ACTIVE |
| **Editor** | Virtualized Compose Lines | Yes | High | `code-editor` | `08-perf` | `replace_file`|N/A| `lint-all.js`| Unit Tests | Android Principal | High | ACTIVE |
| **Terminal** | PTY Shell & ANSI Parser | Yes | High | `terminal-engineering`| `08-perf` | `run_command`|N/A| `test-all.js`| Unit Tests | Backend Engineer | High | ACTIVE |
| **Git** | Visual Diff & Rollback | Yes | High | `git-engineering` | `10-git` | `git_status` | N/A| `project-health`| Unit Tests | System Architect | High | ACTIVE |
| **Preview** | Embedded WebView & Console | Yes | High | `preview-engineering`| `08-perf` | N/A | N/A | N/A | Web Preview | Backend Engineer | High | ACTIVE |
| **Backend** | Local Dev Micro-Server | Yes | High | `preview-engineering`| `08-perf` | N/A | N/A | N/A | Web Preview | Backend Engineer | High | ACTIVE |
| **Security** | Sandbox & Secret Scrubber | Yes | High | `security-engineering`|`07-security`| All Tools | N/A | `security-check`| Unit Tests | Security Engineer | Critical | ACTIVE |
| **Security** | Adversarial Red Team | Yes | High | `red-team` | `07-security`| N/A | N/A | `security-check`| Red Team Suite | Red Team Engineer | Critical | ACTIVE |
| **Testing** | Automated Regression Suite | Yes | High | `testing-engineering`| `09-testing`| All Tools | N/A | `test-all.js`| Unit/UI Tests | QA Director | Critical | ACTIVE |
| **Testing** | Golden Tasks Dataset | Yes | High | `testing-engineering`| `09-testing`| All Tools | N/A | `test-all.js`| Golden Suite | QA Director | High | ACTIVE |
| **Performance**| 60/120 FPS Frame Budget | Yes | High | `performance-engineering`|`08-perf` | N/A | N/A | `performance-check`| Benchmarks | Performance Eng | High | ACTIVE |
| **Accessibility**| TalkBack & WCAG AA Contrast| Yes | High | `accessibility` | `12-a11y` | N/A | N/A | `lint-all.js`| UI Semantics | Accessibility Eng | Medium | ACTIVE |
| **Release** | R8 Shrinking & Signing | Yes | High | `release-engineering`| `00-master` | N/A | N/A | `build-release`| Release Check | Release Engineer | High | ACTIVE |
| **Observability**| Quality Dashboard & Metrics| Yes | High | `cto-orchestration` | `00-master` | N/A | N/A | `project-health`| Scorecard | Product CTO | High | ACTIVE |
