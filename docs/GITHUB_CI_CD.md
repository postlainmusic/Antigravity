# GitHub CI/CD Architecture & Developer Deployment Guide

## 1. Overview & Pipeline Architecture

The Antigravity Mobile CI/CD system is a multi-tier, automated build factory running on GitHub Actions. It enforces continuous code verification, unit testing, security scanning, red-team penetration checks, and automated debug APK & release App Bundle packaging.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       GITHUB ACTIONS CI/CD PIPELINE                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  [Pull Request / Push]                                                      │
│           │                                                                 │
│           ├──────────────┬───────────────────────────────┐                  │
│           ▼              ▼                               ▼                  │
│    [1. Workspace]  [2. Security Gate]         [3. Android Gradle Build]     │
│    • Rules/Skills  • Secret Scrubber          • JDK 17 (Temurin) + Cache    │
│    • FSM Matrix    • Path Traversal Red Team  • Unit Tests (testDebug)      │
│    • 100k Benchmark• MCP Trust Boundary       • Lint Check (lintDebug)      │
│    • Evidence Audit• Process Death Recovery   • Assemble Debug APK          │
│           │              │                               │                  │
│           └──────────────┴───────────────┬───────────────┘                  │
│                                          ▼                                  │
│                             [4. Artifact Upload]                            │
│                             • app-debug.apk                                 │
│                             • Test & Lint HTML Reports                      │
│                             • Reproducible Audit JSON                       │
│                                                                             │
│  [Tag Push 'v*'] ────────────────────────────────────────► [5. Release]     │
│                                                            • assembleRelease│
│                                                            • bundleRelease  │
│                                                            • GitHub Release │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Developer Workflow: Download APK & Test on Real Android Phone

Developers can push code and obtain a freshly built debug APK directly from GitHub Actions without needing Android Studio installed locally:

```
Push Code to GitHub
        │
        ▼
GitHub Actions triggers 'ci.yml' or 'build-debug.yml'
        │
        ▼
Gradle compiles and packages 'app-debug.apk' on cloud runner
        │
        ▼
Navigate to: GitHub Repo ──► 'Actions' tab ──► Select latest workflow run
        │
        ▼
Scroll to bottom: 'Artifacts' ──► Click 'antigravity-app-debug-<commit_sha>'
        │
        ▼
Extract downloaded ZIP ──► Transfer 'app-debug.apk' to Android Phone
        │
        ▼
Install via ADB: 'adb install -r app-debug.apk' or tap to install on device!
```

---

## 3. Workflow Catalog

| Workflow File | Trigger | Purpose | Output Artifacts |
| :--- | :--- | :--- | :--- |
| [.github/workflows/ci.yml](file:///c:/Users/Admin/Documents/GitHub/Antigravity/.github/workflows/ci.yml) | Push & PR to `main`/`master`/`develop` | Full validation, unit tests, lint, security, debug APK | `antigravity-app-debug`, `unit-test-reports`, `lint-reports`, `reproducible-audit-report` |
| [.github/workflows/build-debug.yml](file:///c:/Users/Admin/Documents/GitHub/Antigravity/.github/workflows/build-debug.yml) | Manual (`workflow_dispatch`) | Fast on-demand debug APK build for testing | `antigravity-debug-device-apk-<sha>` |
| [.github/workflows/security.yml](file:///c:/Users/Admin/Documents/GitHub/Antigravity/.github/workflows/security.yml) | Push, PR, & Nightly Schedule (`2:00 AM UTC`) | Red-team traversal, secret leak scanner, MCP trust | Security scan logs |
| [.github/workflows/release.yml](file:///c:/Users/Admin/Documents/GitHub/Antigravity/.github/workflows/release.yml) | Git Tag (`v*.*.*`) or Manual | Production Release APK and Google Play App Bundle (.aab) | Release APK, Release AAB, GitHub Release |

---

## 4. Release Signing & GitHub Secrets Configuration

To enable automated release signing for production Google Play distribution, configure the following **Repository Secrets** in GitHub (*Settings* $\to$ *Secrets and variables* $\to$ *Actions*):

- `ANDROID_KEYSTORE_BASE64`: Base64-encoded string of your production `.jks` release keystore.
- `ANDROID_KEYSTORE_PASSWORD`: Password for the keystore file.
- `ANDROID_KEY_ALIAS`: Key alias name (e.g. `antigravity`).
- `ANDROID_KEY_PASSWORD`: Password for the key alias.

> [!NOTE]
> Debug builds (`assembleDebug`) automatically utilize Android's default debug keystore and do not require any secret configuration.

---

## 5. Local Reproducibility Commands

To reproduce CI pipeline execution locally:

```bash
# 1. Run workspace health and evidence audit
node .agents/scripts/ci-health.js
node .agents/scripts/test-all.js
node .agents/scripts/reproduce-validation.js

# 2. Run Android unit tests & lint (requires JDK 17)
./gradlew testDebugUnitTest
./gradlew lintDebug

# 3. Assemble Debug APK
./gradlew assembleDebug
```
