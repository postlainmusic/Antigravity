# Testing & Quality Assurance Plan

## 1. Test Levels

### Unit Tests (`app/src/test/`)
- Domain Use Cases (`ExecuteAgentTaskUseCaseTest`, `ApplyDiffUseCaseTest`).
- ViewModels & StateFlow transitions tested with `Turbine`.
- Syntax tokenizer, ANSI escape parser, context compressors, and Myers diff algorithm.

### Integration Tests
- MCP JSON-RPC protocol parser and client lifecycle.
- Room database schema migrations and FTS5 symbol indexing.

### Compose UI Tests (`app/src/androidTest/`)
- Screen rendering, bottom sheet drag gestures, keyboard accessory bar symbol inputs, theme switches.

### Automation Scripts
- `node .agents/scripts/test-all.js`: Executes entire test verification suite.
- `node .agents/scripts/lint-all.js`: Static analysis and style verification.
- `node .agents/scripts/security-check.js`: Secret & path traversal vulnerability scan.
- `node .agents/scripts/performance-check.js`: Workspace memory and large asset audit.
