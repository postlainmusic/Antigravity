# Release Management & Verification

## 1. Versioning Strategy
We use Semantic Versioning (`MAJOR.MINOR.PATCH`):
- `MAJOR`: Incompatible UI paradigm or architectural changes.
- `MINOR`: New features (e.g. new model provider, new terminal capabilities).
- `PATCH`: Bug fixes, security patches, performance tuning.

---

## 2. Release Gate Checklist
Before producing a release artifact:
- [ ] Run `node .agents/scripts/test-all.js` (100% pass rate required).
- [ ] Run `node .agents/scripts/lint-all.js` (Zero lint errors).
- [ ] Run `node .agents/scripts/security-check.js --strict` (Zero security warnings).
- [ ] Run `node .agents/scripts/performance-check.js` (No oversized assets).
- [ ] ProGuard / R8 code shrinking rules verified in `proguard-rules.pro`.
- [ ] Version code & name updated in `app/build.gradle.kts`.

---

## 3. Build Commands
- Debug Build: `node .agents/scripts/build-debug.js` (or `./gradlew assembleDebug`)
- Release Signed Build: `node .agents/scripts/build-release.js` (or `./gradlew assembleRelease`)
