# Observability & Runtime Telemetry Architecture

## 1. Internal Engineering Telemetry

To ensure continuous self-improvement without compromising user privacy, the agent platform tracks the following anonymized local telemetry metrics:

| Metric Name | Description | Target Threshold | Alert Trigger |
| :--- | :--- | :--- | :--- |
| `task_success_rate` | Percentage of tasks reaching completion without manual abort | > 95% | < 90% |
| `task_duration_ms` | End-to-end latency from prompt to final verified diff | < 15,000 ms | > 30,000 ms |
| `tool_failure_rate` | Ratio of tool calls returning errors | < 5% | > 10% |
| `build_failure_rate` | Ratio of builds encountering compiler errors | < 8% | > 15% |
| `self_healing_cycles`| Average repair iterations per compilation failure | < 1.2 cycles | > 2.5 cycles |
| `prompt_token_budget`| Token footprint of assembled context | < 25% window | > 35% window |
| `model_latency_ms` | Time-to-first-token (TTFT) and token streaming speed | < 800 ms TTFT | > 2,500 ms TTFT |

---

## 2. Privacy-First Logging Protocol

1. **Zero Personally Identifiable Information (PII)**: No telemetry contains user code snippets, repository names, email addresses, or IP addresses.
2. **Local Metric Buffering**: Metrics are stored locally in Room database and retained for 7 days before automated pruning.
3. **Opt-in Telemetry**: Users can disable telemetry completely in Settings > Privacy.
