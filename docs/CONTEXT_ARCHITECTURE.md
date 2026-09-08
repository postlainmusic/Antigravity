# Context Engineering Architecture (V2)

## 1. The 9-Stage Context Pipeline

Context Engineering V2 ensures that only high-signal, relevant, and sanitized code snippets are presented to the AI models. It operates across 9 deterministic pipeline stages:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ 1. Task Intent Understanding                                                │
│    Extract query keywords, targeted symbol names, file paths, and file types│
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│ 2. Repository Discovery & AST Indexing                                      │
│    Scan workspace directory structure; retrieve cached AST symbol declarations│
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│ 3. Candidate Retrieval                                                      │
│    BM25 text match + symbol reference graph traversal                       │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│ 4. Dependency Expansion                                                     │
│    Follow import statements, supertypes, and interface implementations      │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│ 5. Relevance Scoring & Ranking                                              │
│    Score = 0.40 * Lexical + 0.35 * SymbolProximity + 0.15 * Recency + 0.10  │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│ 6. Security & Taint Filtering                                               │
│    Strip secrets via SecretScrubber; tag snippets with TrustLevel           │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│ 7. Context Compression & Code Slicing                                       │
│    Collapse unchanged method bodies into signatures; preserve AST skeletons │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│ 8. Adaptive Budget Allocation                                               │
│    Allocate dynamic token budgets across System, Prompt, Project, Tools     │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼──────────────────────────────────────┐
│ 9. Final Context Assembly                                                   │
│    Serialize structured prompt with XML-demarcated untrusted data blocks    │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Adaptive Context Budgeting

Context budgets are dynamically calculated based on:
- **Target Model Context Window** ($C_{max}$)
- **Task Complexity Tier** (Single file edit vs Multi-module refactoring)
- **Latency & Cost Profile** (Fast interactive prompt vs Deep reasoning run)

### Budget Allocation Formula

$$\text{Budget}_{\text{System}} = \min(2048, 0.05 \cdot C_{max})$$
$$\text{Budget}_{\text{UserIntent}} = \min(4096, 0.10 \cdot C_{max})$$
$$\text{Budget}_{\text{WorkspaceContext}} = \min(32768, 0.60 \cdot C_{max})$$
$$\text{Budget}_{\text{ToolHistory}} = \min(8192, 0.15 \cdot C_{max})$$
$$\text{Budget}_{\text{CompletionReserve}} = \min(8192, 0.10 \cdot C_{max})$$

---

## 3. Context Quality Metrics & Tracking

Every agent interaction logs structured context telemetry:

| Metric | Target | Description |
| :--- | :--- | :--- |
| **Precision** | $\ge 85\%$ | Percentage of included context tokens directly referenced or modified by the model |
| **Recall** | $\ge 95\%$ | Percentage of necessary dependencies and symbol definitions included in prompt |
| **Noise Ratio** | $\le 15\%$ | Tokens representing boilerplate or unrelated files |
| **Token Efficiency** | $\le 20\%$ | Total prompt tokens relative to model context capacity for standard tasks |
| **Taint Leakage** | $0\%$ | Zero raw untrusted prompt injections reaching the system prompt layer |
