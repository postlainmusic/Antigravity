# Decision Tree: Local vs Remote Execution

```mermaid
graph TD
    Start[Execution Task] --> Heavy{Is task heavy compile / resource intensive?}
    Heavy -- Yes --> HasCloud[Is remote build server configured?]
    HasCloud -- Yes --> Remote[Execute via Remote Worker]
    HasCloud -- No --> LocalThrottled[Execute Locally with Low Priority / Worker Throttling]
    Heavy -- No --> Safe{Is task safe shell / node script / preview?}
    Safe -- Yes --> Local[Execute Locally on Device Engine]
    Safe -- No --> Gated[Prompt User Permission Before Execution]
```

## Summary
- **Local Execution**: Lightweight preview dev servers, node/js execution, local file indexing, git status, unit test runners.
- **Remote / Cloud Workers**: Heavy Android Gradle compilation, cloud Docker runners, large repository full-AST builds.
