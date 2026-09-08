# 09 - Testing & Validation Standards

## Testing Pyramid
1. **Unit Tests (`src/test/`)**:
   - Cover all Domain Use Cases, State Machine transitions, LLM request/response parsing, Context Compressors, and Tool Executors.
   - Use `Turbine` for testing `StateFlow` and `SharedFlow`.
   - Mock external networks and file systems using test doubles and in-memory file systems.
2. **Integration Tests**:
   - Test MCP client handshake, tool registration, and error recovery pipelines.
3. **UI / Compose Tests (`src/androidTest/`)**:
   - Verify screen state transitions, bottom sheet gestures, keyboard accessory interactions, and error dialog behaviors.
4. **Agent Golden Loop Verification**:
   - Validate end-to-end task flows (Prompt -> Plan -> Tool Execution -> Diff -> Verification).
