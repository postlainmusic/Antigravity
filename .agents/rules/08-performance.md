# 08 - Performance & Resource Optimization

## Mobile Performance Standards
1. **60/120 FPS UI Smoothness**:
   - Zero work on the Main thread. All parsing, syntax highlighting, diff computation, and I/O run on background coroutines (`Dispatchers.Default` / `Dispatchers.IO`).
   - Use windowed / virtualized rendering for code editor lines and terminal logs (`LazyColumn` with keyed items).
2. **Memory Efficiency**:
   - Stream large files and logs in chunks rather than loading multi-megabyte payloads into memory.
   - Cache ASTs and syntax trees with LRU policies and memory pressure listeners (`ComponentCallbacks2.onTrimMemory`).
3. **Battery & Background Consumption**:
   - Pause indexing and active previews when the app enters the background.
   - Batch UI event updates (rate-limit streaming token emissions to 30-60 Hz UI redraws).
