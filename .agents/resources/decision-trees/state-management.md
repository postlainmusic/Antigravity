# Decision Tree: State Management Strategy

```mermaid
graph TD
    Start[State Scope] --> Scope{Where is state consumed?}
    Scope -- Single Composable Widget --> LocalState[remember { mutableStateOf() }]
    Scope -- Single Screen / Feature Flow --> VMState[ViewModel + MutableStateFlow<UiState>]
    Scope -- Global / App-Wide (Active Project, Theme, Agent State) --> GlobalStore[AppScope / Singleton StateFlow / DataStore Repository]
```

## Immutable Architecture
- All StateFlow objects expose read-only immutable interfaces.
- State updates are processed via pure reducer functions or explicit intent methods.
