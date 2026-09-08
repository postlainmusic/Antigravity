# Android Engineering Reference Guide

### What is this?
Comprehensive reference for Modern Android Architecture (MAD) using Kotlin, Jetpack Compose, Coroutines, StateFlow, Room, and WorkManager.

### When to use?
- Implementing repositories, use cases, ViewModels, and data sources.
- Handling Android lifecycle events, process death, and configuration changes.

### When NOT to use?
- Pure platform-agnostic algorithms (use plain Kotlin domain modules).

### Recommended Implementation
```kotlin
// ViewModel Pattern
class ProjectViewModel(
    private val getProjectUseCase: GetProjectUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProjectUiState>(ProjectUiState.Loading)
    val uiState: StateFlow<ProjectUiState> = _uiState.asStateFlow()

    fun handleIntent(event: ProjectUiEvent) {
        when (event) {
            is ProjectUiEvent.LoadProject -> loadProject(event.id)
            is ProjectUiEvent.SaveFile -> saveFile(event.path, event.content)
        }
    }
}
```

### Common Failure Modes
- Running disk I/O on `Dispatchers.Main`.
- Retaining Activity/Context references in ViewModels or singletons.

### Validation Checklist
- [ ] ViewModel tested with `Turbine`.
- [ ] Flows collected with `collectAsStateWithLifecycle()`.
