# Decision Tree: Database & Persistence Strategy

```mermaid
graph TD
    Start[Data Persistence Need] --> DataType{Data Characteristics?}
    DataType -- Key-Value / User Preferences / Tokens --> Prefs[Android DataStore / EncryptedSharedPreferences]
    DataType -- Structured Relational / Chat Trajectories / Symbol Index --> RoomDB[Room Database / SQLite with WAL Mode]
    DataType -- Raw Source Files / Project Assets --> FileSys[Direct Workspace Filesystem with Atomic Staging]
```

## Guidelines
- **Room**: Agent conversations, task checkpoints, symbol index, project metadata.
- **DataStore**: User settings, theme mode, model provider selections, API endpoints.
- **EncryptedSharedPreferences / KeyStore**: API keys, OAuth tokens, SSH keys.
