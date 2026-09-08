# Compose UI Reference Guide

### What is this?
Best practices for declarative UI in Jetpack Compose, state hoisting, recomposition safety, and Material 3 theming.

### When to use?
- Building user interfaces, screen layouts, animations, and design system components.

### Recommended Implementation
```kotlin
@Composable
fun EditorScreen(
    state: EditorUiState,
    onEvent: (EditorUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { EditorTopBar(fileName = state.activeFileName) },
        bottomBar = { KeyboardAccessoryBar(onSymbolClick = { onEvent(EditorUiEvent.InsertSymbol(it)) }) },
        modifier = modifier
    ) { padding ->
        CodeView(
            lines = state.lines,
            cursor = state.cursorPosition,
            modifier = Modifier.padding(padding)
        )
    }
}
```

### Common Failure Modes
- Reading rapidly changing state (e.g. scroll offset) directly in composable body without `derivedStateOf`.
- Using mutable collections (like `ArrayList`) as parameters instead of immutable lists.
