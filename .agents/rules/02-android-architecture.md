# 02 - Android Architecture Guidelines

## Architecture Overview
The Android application follows **Modern Android Architecture (MAD)** with strict separation of concerns across Data, Domain, and Presentation layers using **MVI (Model-View-Intent)** / **UDF (Unidirectional Data Flow)**.

## Key Rules
1. **Presentation Layer**:
   - Built exclusively with **Jetpack Compose**.
   - ViewModels expose a single immutable `StateFlow<UiState>` and handle user `UiEvent` intents.
   - One-off events (navigation, toast, snackbars) are handled via buffered channels or dedicated single-event flows.
2. **Domain Layer**:
   - Encapsulates pure business logic into reusable Use Cases (`ExecuteAgentTaskUseCase`, `IndexProjectUseCase`, `ApplyDiffUseCase`).
   - Pure Kotlin with no direct Android framework dependencies.
3. **Data Layer**:
   - Repositories mediate between local storage (Room, DataStore, Filesystem) and remote APIs (LLM endpoints, MCP servers, Git remotes).
   - All I/O operations are `suspend` functions or return cold `Flow`s running on `Dispatchers.IO`.
4. **Dependency Injection**:
   - Clean, modular dependency injection (Hilt/Koin or lightweight DI containers) providing testable interfaces.
