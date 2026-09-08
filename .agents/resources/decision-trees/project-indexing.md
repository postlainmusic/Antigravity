# Decision Tree: Project Indexing Strategy

```mermaid
graph TD
    Start[Workspace Indexing] --> ProjSize{Number of Files in Workspace?}
    ProjSize -- Small / Medium (< 500 files) --> FullAST[Full Symbol Index + In-Memory Trie / SQLite FTS5]
    ProjSize -- Large (> 2,000 files) --> Incremental[Lazy Incremental Indexing + Git Changed Files Only]
```

## Guidelines
- **FTS5 / Trie**: Fast symbol autocomplete and fuzzy path finding.
- **Background Worker**: Indexing runs strictly at idle CPU priority and pauses when battery is low or app is backgrounded.
