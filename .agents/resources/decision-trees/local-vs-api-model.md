# Decision Tree: Local Model vs API Model

```mermaid
graph TD
    Start[User Task Request] --> Offline{Is device offline?}
    Offline -- Yes --> LocalOnly[Local ONNX / Gemma 2B on-device]
    Offline -- No --> TaskType{Task Complexity?}
    TaskType -- Simple Autocomplete / Syntax --> LocalSmall[Local On-Device Model for Low Latency]
    TaskType -- Multi-file Refactor / Deep Reasoning / Architecture --> CloudModel[Cloud API: Gemini 1.5 Pro / Claude 3.5 Sonnet / GPT-4o]
```

## Guidelines
- **Local (Gemma 2B / ONNX / ExecuTorch)**: Autocomplete, line completion, single-function doc generation, offline tasks.
- **Cloud API (Gemini / Claude / OpenAI)**: Multi-step planning, tool orchestration, deep context synthesis, multi-file code generation.
