# Decision Tree: Sandbox Strategy

```mermaid
graph TD
    Start[Command / Action] --> DangerTier{Action Danger Level?}
    DangerTier -- Read Only / List / Git Status --> AutoSafe[SAFE: Execute Automatically in Isolated Project Jail]
    DangerTier -- File Write / Package Install --> PolicyCheck[MODERATE: Execute if Workspace Policy Permits, Log Transaction]
    DangerTier -- Delete / Force Push / System Mod --> RequirePrompt[DANGEROUS: Hard Gate - Require Explicit User Touch Confirmation]
```

## Guidelines
- Strict path canonicalization prevents directory traversal attacks (`../../`).
- Atomic staging buffers allow instantaneous rollback of agent-generated modifications.
