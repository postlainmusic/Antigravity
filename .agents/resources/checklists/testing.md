# Testing & Quality Gate Checklist

- [ ] Domain Use Cases covered by Unit Tests with 100% assertion accuracy.
- [ ] ViewModels tested with `Turbine` and `kotlinx-coroutines-test`.
- [ ] Compose UI tests verify screen states (Empty, Loading, Active, Error).
- [ ] End-to-end agent task flow verified (Prompt -> Plan -> Code -> Build -> Verify).
- [ ] All tests passing locally before commits or PR merges.
