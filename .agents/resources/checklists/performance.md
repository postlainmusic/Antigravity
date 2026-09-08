# Performance & Optimization Checklist

- [ ] Zero blocking operations on Main thread (CPU parsing on `Dispatchers.Default`, I/O on `Dispatchers.IO`).
- [ ] Code editor lines virtualized in Compose `LazyColumn`.
- [ ] Terminal streaming ANSI logs rate-limited to avoid layout thrashing.
- [ ] Memory pressure listener clears LRU AST/syntax caches on critical trim memory.
- [ ] App startup time profiled and kept under 1.5 seconds warm / 2.5 seconds cold.
