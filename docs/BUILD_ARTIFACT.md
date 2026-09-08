# Build Artifact & Static Inspection Report

## 1. Build Specification & Configuration

The application build specification is defined in [app/build.gradle.kts](file:///c:/Users/Admin/Documents/GitHub/Antigravity/app/build.gradle.kts) and [gradle/libs.versions.toml](file:///c:/Users/Admin/Documents/GitHub/Antigravity/gradle/libs.versions.toml).

| Configuration Key | Specified Value | Rationale |
| :--- | :--- | :--- |
| **Application ID** | `com.antigravity.mobile` | Unique production package namespace |
| **Debug Application ID** | `com.antigravity.mobile.debug` | Suffix `.debug` allows side-by-side installation |
| **Version Name** | `1.0.0` | Initial release semantic version |
| **Version Code** | `1` | Build counter for Android package manager |
| **Compile SDK** | `35` (Android 15) | Access to latest Android 15 platform APIs |
| **Target SDK** | `35` (Android 15) | Adheres to Google Play 2026 target requirements |
| **Min SDK** | `26` (Android 8.0 Oreo) | Supports 96%+ of active Android devices; KeyStore GCM native support |
| **Java / JVM Compatibility** | Java 17 (`JavaVersion.VERSION_17`) | Required for Kotlin 2.0+ and Compose Compiler |
| **Code Shrinking & ProGuard** | Enabled in Release (`isMinifyEnabled = true`) | Optimization rules in `proguard-rules.pro` |

---

## 2. Android Manifest Static Audit ([AndroidManifest.xml](file:///c:/Users/Admin/Documents/GitHub/Antigravity/app/src/main/AndroidManifest.xml))

### 2.1 Declared Permissions & Justifications

| Permission | Protection Level | Purpose & Justification | Remediation / Audit Note |
| :--- | :--- | :--- | :--- |
| `android.permission.INTERNET` | Normal | Required for cloud AI model streaming (Gemini, Claude, OpenAI) and remote MCP connections | Strictly required. Bounded by Network Security Config. |
| `android.permission.ACCESS_NETWORK_STATE` | Normal | Required to detect offline mode and switch seamlessly to on-device Gemma 2B model | Strictly required for offline mode detection. |
| `android.permission.READ_EXTERNAL_STORAGE` | Dangerous (Max SDK 32) | Required on legacy Android (API 26-32) to open workspace folders outside private sandbox | Scoped with `android:maxSdkVersion="32"`. On Android 13+, scoped Storage Access Framework (SAF) is used. |
| `android.permission.WRITE_EXTERNAL_STORAGE`| Dangerous (Max SDK 29) | Required on legacy Android (API 26-29) to export generated project ZIP archives | Scoped with `android:maxSdkVersion="29"`. |
| `android.permission.VIBRATE` | Normal | Haptic feedback for keyboard accessory bar (`{ } ; -> $`) touch confirmations | Normal permission for developer ergonomics. |

### 2.2 Security Configuration Flags
- `android:exported="true"`: Applied only to `MainActivity` with `android.intent.action.MAIN` filter (required for app launcher). No unexported background services or content providers are exposed.
- `android:usesCleartextTraffic="true"`: Scoped strictly to `127.0.0.1` for the embedded local development server preview (`LocalDevServer.kt`).
- `windowSoftInputMode="adjustResize"`: Guarantees the Compose code editor and accessory bar smoothly resize when the virtual keyboard opens without corrupting layout.

---

## 3. Real Build Execution Status

- **Command**: `.\gradlew.bat assembleDebug`
- **Host Execution Status**: **BLOCKED BY ENVIRONMENT** (JDK 17 and Android SDK 35 not installed in host PATH).
- **Target Artifact Path**: `app/build/outputs/apk/debug/app-debug.apk`
- **Action Required for APK Binary Generation**: Open project in Android Studio on a machine with Android SDK or install OpenJDK 17 + Android Platform-Tools.
