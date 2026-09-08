# Decision Tree: Background Work Execution

```mermaid
graph TD
    Start[Work Requirement] --> Nature{Work Nature?}
    Nature -- Ephemeral / UI Dependent (Token Stream / Syntax Highlight) --> CoroutineScope[CoroutineScope / viewModelScope]
    Nature -- Long-running Execution (Agent Multi-step Build / Git Clone) --> ForegroundService[Android Foreground Service with Notification]
    Nature -- Deferrable Periodic (Project Indexing / Cache Cleanup) --> WorkManager[Android Jetpack WorkManager]
```

## Guidelines
- Foreground services display clear progress notifications with cancellation action buttons.
- Coroutines cancel automatically when their parent lifecycle terminates.
