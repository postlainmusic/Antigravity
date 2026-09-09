# Android Build & Environment Compatibility Matrix

## 1. Toolchain & Environment Matrix

| Component | Target Specification | Minimum Supported | Tested Runtime | Compatibility Status |
| :--- | :--- | :--- | :--- | :--- |
| **Java / JDK** | OpenJDK 17 (Temurin) | JDK 17 | JDK 17.0.12 (GitHub Ubuntu / Android Studio) | **VALIDATED** |
| **Gradle** | 8.10.2 | 8.5.0 | 8.10.2 | **VALIDATED** |
| **Android Gradle Plugin** | 8.5.2 | 8.4.0 | 8.5.2 | **VALIDATED** |
| **Kotlin** | 2.0.20 | 2.0.0 | 2.0.20 (Compose Compiler Plugin) | **VALIDATED** |
| **Compose BOM** | 2024.09.00 | 2024.06.00 | 2024.09.00 | **VALIDATED** |
| **compileSdk** | 35 (Android 15 Vanilla Ice Cream) | 34 | 35 | **VALIDATED** |
| **targetSdk** | 35 | 34 | 35 | **VALIDATED** |
| **minSdk** | 26 (Android 8.0 Oreo) | 26 | 26 | **VALIDATED** |

---

## 2. Rationale & Build Invariants

1. **JDK 17 Requirement**: Kotlin 2.0.20 and AGP 8.5.2 require JDK 17 as baseline.
2. **Kotlin 2.0 Compose Compiler**: Kotlin 2.0 uses the unified Compose compiler plugin `org.jetbrains.kotlin.plugin.compose`, eliminating external version-locked compiler dependencies.
3. **CompileSDK 35**: Supports edge-to-edge rendering by default while retaining backward compatibility to API level 26 (`minSdk = 26`).
4. **Reproducible Wrapper**: Gradle wrapper tracks executable mode `100755` with normalized LF line endings and official `gradle-wrapper.jar` (43.5 KB).
