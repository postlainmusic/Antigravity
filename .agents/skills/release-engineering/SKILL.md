---
name: release-engineering
description: >-
  Manages semantic versioning, release builds, ProGuard/R8 optimization, APK/AAB generation, release signing, and pre-flight release gate checklists.
---

# Release Engineering Skill

## Purpose
Ensures production release builds are stable, optimized, secure, signed, and validated before distribution.

## Release Gate Checklist
1. **Compilation & Code Shrinking**:
   - Enable R8/ProGuard code shrinking and obfuscation.
   - Verify ProGuard keep rules for serialization (Kotlinx Serialization / Moshi / Room).
2. **Quality Verification**:
   - Zero critical or high-severity linter/compiler warnings.
   - 100% unit and integration test pass rate.
3. **Artifact Generation**:
   - Generate Signed Release APK / Android App Bundle (AAB).
   - Verify APK size, memory allocations, and startup time on physical or emulated devices.
4. **Changelog & Tagging**:
   - Tag release with semantic version (e.g. `v1.0.0`) and publish release notes.
