# AI Agent Checklist

- [ ] Deterministic state machine transitions implemented and tested.
- [ ] Context pruning prevents prompt overflow (< 25% window usage for base prompt).
- [ ] Multi-model abstraction supports Gemini, Claude, OpenAI, and Local models.
- [ ] Tool execution is wrapped in timeouts and cancellation listeners.
- [ ] Multi-file edits produce previewable diffs with one-tap rollback.
- [ ] Compilation / runtime failures trigger iterative self-healing loops.
- [ ] UI receives real-time structured events (Planning, Tooling, Output, Diff, Error).
