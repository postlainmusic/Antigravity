# Product Genesis: Engineering Risk Register

| Risk ID | Category | Risk Description | Severity | Likelihood | Mitigation Strategy | Owner |
| :--- | :--- | :--- | :---: | :---: | :--- | :--- |
| **RSK-001** | **Security** | Prompt injection via untrusted workspace files attempting to bypass sandbox | High | High | Wrap all file text in `<untrusted_workspace_file>` tags; instruct model to treat repo content strictly as passive data. | Security Engineer |
| **RSK-002** | **Security** | Path traversal attacks (`../../`) accessing OS files | Critical | Medium | Canonical path resolution (`target.canonicalPath.startsWith(root.canonicalPath)`); throw `SecurityException`. | Security Engineer |
| **RSK-003** | **Security** | Leaking API keys or tokens into UI logs or conversation streams | High | Medium | Pass all emitted strings through `SecretScrubber` regex filter; store keys in Android KeyStore. | Security Engineer |
| **RSK-004** | **Performance** | High-frequency token streaming causing Compose recomposition jank | Medium | High | Batch token stream updates to 30-60 Hz UI frame rates; use `@Immutable` and `derivedStateOf`. | Performance Eng |
| **RSK-005** | **Performance** | Memory exhaustion from 50,000+ line code files | High | Medium | Virtualized line-by-line rendering using Compose `LazyColumn` with keyed line items. | Android Principal |
| **RSK-006** | **Performance** | Background CPU drain on mobile battery | Medium | High | Pause file indexing and preview servers when app is backgrounded or battery is low. | Android Principal |
| **RSK-007** | **UX** | Virtual keyboard occluding editor cursor or action buttons | High | High | Integrate `WindowInsets.ime` padding with persistent 44dp `KeyboardAccessoryBar`. | Mobile UX Director |
| **RSK-008** | **AI** | Infinite agent retry loops on persistent compiler errors | Medium | Medium | Implement strict 3-iteration self-healing budget; escalate to user if unresolved. | AI Agent Architect |
| **RSK-009** | **AI** | Hallucinated terminal commands or fake execution | High | Low | Enforce real process execution via `ProcessBuilder` or PTY; capture real stdout/stderr. | Backend Engineer |
| **RSK-010** | **Android** | Process death causing loss of active conversation trajectory | Medium | Medium | Persist conversation state and project metadata to Room DB / DataStore with `SavedStateHandle`. | Android Principal |
