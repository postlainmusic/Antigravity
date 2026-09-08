# Testing Reference Guide

### What is this?
Unit, integration, and UI testing patterns for Android, Coroutines, StateFlow, and AI state machines.

### ViewModel StateFlow Testing with Turbine
```kotlin
@Test
fun `loadProject emits Loading then Success`() = runTest {
    val viewModel = ProjectViewModel(fakeGetProjectUseCase)
    viewModel.uiState.test {
        assertEquals(ProjectUiState.Initial, awaitItem())
        viewModel.handleIntent(ProjectUiEvent.LoadProject("proj-123"))
        assertEquals(ProjectUiState.Loading, awaitItem())
        val successState = awaitItem() as ProjectUiState.Success
        assertEquals("proj-123", successState.project.id)
    }
}
```
