# Performance Reference Guide

### What is this?
Guidelines for maintaining 60/120 FPS rendering, low memory footprint, and non-blocking background workers.

### Composable Stability Rules
1. Annotate domain models used in Compose with `@Immutable` or `@Stable`.
2. Replace `List<T>` with `ImmutableList<T>` (from `kotlinx.collections.immutable`) to enable smart skipping during recomposition.
3. Use `remember` with precise calculation keys.

### Virtualized Lazy Rendering
```kotlin
LazyColumn(
    modifier = Modifier.fillMaxSize(),
    state = listState
) {
    items(
        count = lines.size,
        key = { index -> index }
    ) { lineIndex ->
        EditorLineItem(
            lineNumber = lineIndex + 1,
            lineText = lines[lineIndex]
        )
    }
}
```
