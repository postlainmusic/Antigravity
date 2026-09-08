# Architecture Decision Records (ADR)

## ADR-001: Mobile-Native Jetpack Compose UI Stack
- **Context**: The mobile IDE requires 60/120 FPS responsiveness, smooth gesture navigation, dynamic multi-pane layouts, and deep Android ecosystem integration.
- **Decision**: Use 100% native Kotlin and Jetpack Compose with Material 3 design tokens instead of webview wrappers (Cordova/Capacitor) or Electron/Flutter wrappers.
- **Alternatives Considered**: Flutter, React Native, Embedded WebView (Theia/VSCode Web).
- **Consequences**: Unmatched performance, zero webview latency, native touch handles, and battery efficiency.
- **Status**: ACCEPTED.

---

## ADR-002: Multi-Provider AI Model Abstraction
- **Context**: Users need flexibility between cloud models (Google Gemini 1.5 Pro, Anthropic Claude 3.5 Sonnet, OpenAI GPT-4o) and offline on-device execution (Gemma 2B / ONNX).
- **Decision**: Define a decoupled `ModelProvider` interface with unified streaming SSE protocols, tool calling contracts, and structured outputs.
- **Alternatives Considered**: Vendor lock-in to a single provider SDK.
- **Consequences**: High modularity, zero vendor lock-in, seamless fallback if an API is unavailable.
- **Status**: ACCEPTED.

---

## ADR-003: Three-Tier Sandbox Security Architecture
- **Context**: Autonomous agents executing file modifications and terminal commands must not be able to compromise host user data or OS integrity.
- **Decision**: Categorize all tools into SAFE (auto-executed), MODERATE (policy-controlled), and DANGEROUS (explicit touch confirmation). Enforce strict path canonicalization.
- **Alternatives Considered**: Unrestricted command execution or complete prompt-only sandboxing.
- **Consequences**: Optimal balance between autonomous development speed and user security.
- **Status**: ACCEPTED.

---

## ADR-004: Virtualized Compose Code Editor with AST Tokenization
- **Context**: Rendering large source code files on mobile devices can cause severe memory spikes and UI thread jank if unvirtualized.
- **Decision**: Implement a virtualized line-by-line renderer using `LazyColumn` backed by background coroutine tokenizers for syntax highlighting.
- **Alternatives Considered**: WebView ACE / Monaco editor, monolithic TextView.
- **Consequences**: Constant memory overhead regardless of file length; instant scroll performance.
- **Status**: ACCEPTED.
