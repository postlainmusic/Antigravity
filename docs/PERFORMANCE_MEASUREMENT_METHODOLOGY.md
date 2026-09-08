# Performance Measurement Methodology

## 1. Complete Latency Decomposition

To avoid conflating local orchestration speed with full end-to-end AI latency, Antigravity decomposes every task into 15 distinct measurable phases:

```
Total AI Workflow Latency =
    T_intent        (Intent & Prompt Parsing)
  + T_discovery     (Workspace File Tree & AST Discovery)
  + T_retrieval     (Symbol Index Lookup & BM25 Ranking)
  + T_assembly      (Context Compression & Demarcation)
  + T_routing       (Model Capability Evaluation)
  + T_network_conn  (TLS Handshake & HTTP/2 Session Init)
  + T_ttft          (Time to First Token from LLM)
  + T_inference     (Full Token Stream Generation)
  + T_tool_select   (Tool Call Extraction & Schema Parse)
  + T_permission    (Autonomy Level & Sandbox Verification)
  + T_tool_exec     (Local File Mutation / Process Spawn)
  + T_diff          (Myers Hunk Diff Generation)
  + T_build         (Gradle / Build Tool Execution)
  + T_test          (JUnit / Test Runner Verification)
  + T_emission      (MVI UI Event Dispatch & Compose Layout)
```

---

## 2. Latency Breakdown by Phase (Empirical vs Expected Production)

| Pipeline Phase | Synthetic Harness Duration | Expected Real Tier A (Flagship) | Expected Real Tier B (Mid-Range) | Expected Real Tier C (Entry) | Measurement Source |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1. Intent Parsing** | $< 0.1\text{ ms}$ | $1.2\text{ ms}$ | $2.5\text{ ms}$ | $5.0\text{ ms}$ | Regex Intent Tokenizer |
| **2. Project Discovery** | $1.2\text{ ms}$ | $15\text{ ms}$ | $35\text{ ms}$ | $80\text{ ms}$ | `File.walkTopDown()` (1,000 files) |
| **3. Context Retrieval** | $2.1\text{ ms}$ | $12\text{ ms}$ | $25\text{ ms}$ | $60\text{ ms}$ | In-Memory AST Symbol Index |
| **4. Context Assembly** | $1.8\text{ ms}$ | $4\text{ ms}$ | $8\text{ ms}$ | $18\text{ ms}$ | XML Demarcation & Token Counter |
| **5. Model Routing** | $< 0.2\text{ ms}$ | $0.5\text{ ms}$ | $1.0\text{ ms}$ | $2.0\text{ ms}$ | `ModelRouter.selectOptimalModel()` |
| **6. Network Connect** | $0.0\text{ ms}$ (Bypassed) | $60\text{ ms}$ (TLS reuse) | $90\text{ ms}$ | $140\text{ ms}$ | OkHttp3 TLS / HTTP/2 Connect |
| **7. Time to First Token (TTFT)** | $0.0\text{ ms}$ (Bypassed) | $350\text{ ms}$ (Gemini/Claude) | $450\text{ ms}$ | $600\text{ ms}$ | SSE Stream First Chunk |
| **8. Stream Inference (300 tokens)**| $0.0\text{ ms}$ (Bypassed)| $800\text{ ms}$ | $1100\text{ ms}$ | $1600\text{ ms}$ | SSE Token Stream Duration |
| **9. Tool Selection & Parse** | $0.4\text{ ms}$ | $1.5\text{ ms}$ | $3.0\text{ ms}$ | $7.0\text{ ms}$ | `kotlinx.serialization` JSON |
| **10. Permission Gate** | $< 0.1\text{ ms}$ | $0.2\text{ ms}$ | $0.4\text{ ms}$ | $0.9\text{ ms}$ | `PermissionManager.evaluate()` |
| **11. File Mutation** | $1.2\text{ ms}$ | $4.0\text{ ms}$ | $8.0\text{ ms}$ | $18.0\text{ ms}$ | Atomic `File.writeText()` |
| **12. Diff Generation** | $1.5\text{ ms}$ | $6.0\text{ ms}$ | $14.0\text{ ms}$ | $32.0\text{ ms}$ | Myers Diff Hunk Algorithm |
| **13. Gradle Build** | $0.0\text{ ms}$ (Bypassed) | $2800\text{ ms}$ (Daemon warm) | $5500\text{ ms}$ | $12000\text{ ms}$ | `gradlew assembleDebug` |
| **14. Test Execution** | $0.0\text{ ms}$ (Bypassed) | $1200\text{ ms}$ | $2400\text{ ms}$ | $5200\text{ ms}$ | `gradlew testDebugUnitTest` |
| **15. MVI Result Emission** | $0.6\text{ ms}$ | $3.0\text{ ms}$ | $6.0\text{ ms}$ | $15.0\text{ ms}$ | Compose Recomposition Frame |
| **TOTAL ORCHESTRATION ONLY** | **$\mathbf{8.9\text{ ms}}$** | **$\mathbf{47.4\text{ ms}}$** | **$\mathbf{103.3\text{ ms}}$** | **$\mathbf{237.9\text{ ms}}$** | Excluding Network & Compiler |
| **TOTAL FULL AI + BUILD PIPELINE** | **N/A (Bypassed)** | **$\mathbf{5.2\text{ s}}$** | **$\mathbf{9.6\text{ s}}$** | **$\mathbf{19.8\text{ s}}$** | Complete Real-World Loop |

---

## 3. Benchmarking Standards & Statistical Rigor

Every performance metric reported in Antigravity must state:
1. **State**: Cold start vs Warm state (JIT warmed up, Gradle daemon running).
2. **Device Hardware**: Tier A (Snapdragon 8 Gen 3), Tier B (Snapdragon 7 Gen 3), Tier C (Snapdragon 6 Gen 1), or Desktop Dev Host.
3. **Network State**: Real Wi-Fi (50Mbps), 4G LTE, or Offline on-device.
4. **Sample Size**: Minimum 20 iterations.
5. **Statistical Distribution**: Record **Min, Median (p50), p95, p99, and Max**.
