---
name: android-architecture
description: >-
  Guides Modern Android Architecture implementation using Kotlin, Jetpack Compose, ViewModels, StateFlow, Room, WorkManager, Coroutines, and clean layer separation.
---

# Android Architecture Skill

## Purpose
Enforces scalable, testable, and robust Android application architecture adhering to official Google Android guidelines and Unidirectional Data Flow (UDF).

## Architecture Blueprint
- **Presentation**: `Screen` Composable -> `ViewModel` -> `UiState` / `UiEvent`.
- **Domain**: `UseCase` classes executing atomic business workflows.
- **Data**: `RepositoryImpl` coordinates between `LocalDataSource` (Room, Filesystem) and `RemoteDataSource` (Retrofit, Ktor, WebSocket).
- **Concurrency**: Kotlin Coroutines with structured scopes (`viewModelScope`, SupervisorJob) and `Flow`s.

## Workflow
1. **Define UiState**: Sealed interface or immutable data class modeling complete screen state.
2. **Define UiEvent**: Sealed interface representing all user intents.
3. **Implement ViewModel**: Expose `StateFlow<UiState>` and mutate state via private `MutableStateFlow`.
4. **Implement UseCase & Repository**: Inject repositories via interfaces; execute disk/network I/O strictly on `Dispatchers.IO`.
5. **Lifecycle Awareness**: Collect flows in Compose using `collectAsStateWithLifecycle()`.

## Validation
- Ensure ViewModel survives configuration changes without data loss.
- Verify 100% testability of ViewModels and UseCases with unit test runners.
