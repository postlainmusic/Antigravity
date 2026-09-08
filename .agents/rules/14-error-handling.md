# 14 - Error Handling & Resilience Guidelines

## Guidelines
1. **Result & Domain Errors**:
   - Use `Result<T>` or custom domain sealed error models (`AppError.NetworkError`, `AppError.AuthError`, `AppError.SandboxViolation`, `AppError.ToolExecutionError`).
   - Never swallow exceptions silently in empty `catch` blocks.
2. **User-Centric Messaging**:
   - Error messages presented in the UI must explain *what happened*, *why*, and provide an actionable remedy or retry button.
3. **Agent Self-Healing**:
   - If an agent tool call fails (e.g. syntax error in generated code, missing import), the agent loop must capture the stderr/compiler output, analyze the failure, and autonomously attempt an iterative fix before giving up.
