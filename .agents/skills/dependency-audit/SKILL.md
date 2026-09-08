---
name: dependency-audit
description: >-
  Audits third-party dependencies, libraries, Gradle plugins, and npm packages for security vulnerabilities, licensing compliance, bloat, and version compatibility.
---

# Dependency Audit Skill

## Purpose
Keeps the dependency tree minimal, secure, modern, and free from licensing conflicts or performance bloat.

## Workflow
1. **Inventory**: Inspect `build.gradle.kts`, `libs.versions.toml`, and `package.json`.
2. **Vulnerability Scan**: Check dependencies against known vulnerability databases (CVEs).
3. **License Verification**: Ensure all libraries use permissive open-source licenses (Apache 2.0, MIT, BSD) and avoid restrictive viral licenses.
4. **Prune Dead Weight**: Identify and remove unused transitive dependencies.
5. **Version Compatibility**: Verify Gradle plugin, Kotlin compiler, and Jetpack Compose BOM version alignments.
