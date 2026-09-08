# Context Engineering V2 & Dynamic Token Budgeting

## 1. Context Assembly Pipeline

```
┌─────────────────────────────────────────────────────────────┐
│ 1. User Request                                             │
└──────────────┬──────────────────────────────────────────────┘
               ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. Intent Classification & Task Scope                       │
│    (Trivial Tweak / Refactor / Bug Fix / Arch Feature)      │
└──────────────┬──────────────────────────────────────────────┘
               ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. Multi-Signal Context Retrieval                           │
│    ├─ Active Editor Buffer & Cursor Position                │
│    ├─ Lexical / AST Symbol Ranking (Classes, Methods)       │
│    ├─ Dependency Graph & Import Relationships               │
│    ├─ Recent Compiler / Linter Diagnostics                  │
│    └─ Git Working Tree Status & Uncommitted Diffs           │
└──────────────┬──────────────────────────────────────────────┘
               ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. Scoring & Ranking Engine                                 │
│    Score = w1*AST_Relevance + w2*RecentDiff + w3*ErrorLoc   │
└──────────────┬──────────────────────────────────────────────┘
               ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. Token Budget Allocation & Compression Engine             │
│    (Max 25% of Model Context Window for Initial Prompt)     │
│    ├─ File Summaries for Low-Relevance Nodes                │
│    └─ Full Code with <untrusted_workspace_file> Tags        │
└──────────────┬──────────────────────────────────────────────┘
               ▼
┌─────────────────────────────────────────────────────────────┐
│ 6. Model Input Payload                                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Token Budgeting Tiers

| Task Complexity | Maximum Prompt Budget | Context Compression Strategy |
| :--- | :--- | :--- |
| **Trivial (< 10 lines)** | 2,000 tokens | Active file snippet + exact line context |
| **Standard Feature / Bug** | 8,000 tokens | Active file + AST symbol signatures + recent error log |
| **Complex Refactor / Arch**| 16,000 tokens | Multi-file graph + AST interfaces + git diff overview |

---

## 3. Passive Data Demarcation
To eliminate prompt-injection vectors, all workspace content is wrapped in passive XML tags:
```xml
<untrusted_workspace_file path="src/MainActivity.kt">
... code ...
</untrusted_workspace_file>
```
