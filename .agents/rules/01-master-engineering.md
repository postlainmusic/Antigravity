# 01 - Master Engineering Standards

## Operating Philosophy
- Every software component is treated as a production system.
- Code should be self-documenting, modular, and resilient against hardware, network, and environmental anomalies.
- Treat developer experience (DX) and user experience (UX) as first-class architectural requirements.

## Core Rules
1. **Zero Mocking of Core Runtime**: Never replace real execution with hardcoded mock timeouts unless explicitly in a unit test environment.
2. **Deterministic State**: Avoid global mutable state. All state must be encapsulated in managed stores (StateFlow, ViewModel, Room, or DataStore).
3. **Graceful Degradation**: If an AI model, network connection, or tool fails, the system must downgrade smoothly and notify the user with actionable next steps.
