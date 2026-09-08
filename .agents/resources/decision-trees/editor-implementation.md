# Decision Tree: Editor Implementation

```mermaid
graph TD
    Start[Editor Subsystem] --> Size{Document Size & Language Complexity?}
    Size -- Standard Source File (< 10,000 lines) --> ComposeNative[Native Compose Virtualized Line Editor + Custom Syntax Engine]
    Size -- Massive Binary / Log Stream (> 50,000 lines) --> PagedChunked[Paged Stream Viewer with Windowed Memory Allocation]
```

## Guidelines
- **Jetpack Compose Native**: Direct touch control, integrated keyboard accessory bar, custom magnifier loupe, instant theme switching, low memory overhead.
- **Tree-Sitter / TextMate Grammar**: Background tokenization off the main UI thread.
