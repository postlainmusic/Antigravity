# Device Startup & Runtime Execution Specifications

## 1. Startup Lifecycle & Macrobenchmark Targets

Startup performance on real Android hardware is divided into three device classes:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           ANDROID STARTUP LIFECYCLE                         │
│                                                                             │
│  OS Process Fork ──► Application.onCreate() ──► Activity.onCreate()         │
│                              │                          │                   │
│                              ▼                          ▼                   │
│                     Init KeyStore Vault         SetContent { Theme }        │
│                     Init ModelRegistry          Init MainViewModel (MVI)    │
│                              │                          │                   │
│                              └──────────────┬───────────┘                   │
│                                             ▼                               │
│                                    First Frame Rendered                     │
│                                 (Time to Interactive: TTI)                  │
└─────────────────────────────────────────────────────────────────────────────┘
```

| Lifecycle Metric | Tier A Target (Flagship) | Tier B Target (Mid-Range) | Tier C Target (Entry) | Verification Method |
| :--- | :--- | :--- | :--- | :--- |
| **Cold Startup (Process Fork to First Frame)**| $< 600\text{ ms}$ | $< 1000\text{ ms}$ | $< 1800\text{ ms}$ | AndroidX Macrobenchmark `StartupMode.COLD` |
| **Warm Startup (Activity Resume)** | $< 200\text{ ms}$ | $< 350\text{ ms}$ | $< 600\text{ ms}$ | AndroidX Macrobenchmark `StartupMode.WARM` |
| **Initial Dalvik/ART Heap Memory** | $< 45\text{ MB}$ | $< 35\text{ MB}$ | $< 28\text{ MB}$ | Android Profiler Memory Allocation |
| **Peak Workspace Memory (50k lines)** | $< 250\text{ MB}$ | $< 200\text{ MB}$ | $< 150\text{ MB}$ | Viewport-aware line layout buffer |
| **Startup ANR / Crash Rate** | $0.00\%$ | $0.00\%$ | $0.00\%$ | Android Vitals Telemetry |

---

## 2. Invariant Runtime Rules

1. **Zero Disk I/O on Main Thread**: All repository indexing, file reading/writing, and KeyStore decryption operations execute strictly on `Dispatchers.IO`.
2. **Recomposition Safety**: State flows in `MainViewModel` use immutable data classes; Compose layouts utilize `@Stable` and `@Immutable` contracts.
3. **Keyboard Insets Handling**: `Modifier.imePadding()` and `WindowInsets.ime` ensure the code editor accessory bar floats cleanly above Gboard / SwiftKey keyboards.
