# Release Readiness Checklist

- [ ] Version code and version name incremented in `build.gradle.kts`.
- [ ] ProGuard / R8 code shrinking verified with zero serialization reflection breaks.
- [ ] Dependency licenses checked for compliance (no AGPL/GPL conflicts).
- [ ] Security scan clean (no embedded keys or insecure network configs).
- [ ] Release APK / AAB compiled and signed.
- [ ] Release notes and changelog updated in `docs/RELEASE.md`.
