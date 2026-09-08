# Security Incident Investigation: Workspace Escape Red Team Breaches

## 1. Executive Summary

During the execution of GitHub Actions `security.yml` on `ubuntu-latest` (Linux runner), the Workspace Escape Red Team suite recorded **2 security breaches (8/10 blocked, 2 breaches)**, triggering a hard Security Blocker.

This document records the exact technical root cause, the end-to-end path trace, and the comprehensive security hardening applied across the Kotlin codebase and test harnesses.

---

## 2. Incident Identification & Reproducibility

### Breach 1: Windows Drive Absolute Path on POSIX/Linux
- **Test Name**: `Windows Drive Absolute Path`
- **Attack Payload**: `C:\Windows\System32\cmd.exe`
- **Input Representation**: Windows absolute drive path with backslashes.
- **Workspace Root on Linux**: `/home/runner/work/Antigravity/Antigravity/tests/fixtures/todo-crash`
- **Canonicalization Behavior on Linux**: On POSIX/Linux systems, backslash `\` is NOT a path separator; it is treated as a valid literal character in a filename. `path.resolve(workspaceRoot, "C:\\Windows\\System32\\cmd.exe")` resolved to `/home/runner/.../todo-crash/C:\Windows\System32\cmd.exe`.
- **Resolved Path**: `/home/runner/work/Antigravity/Antigravity/tests/fixtures/todo-crash/C:\Windows\System32\cmd.exe`
- **Expected Result**: **BLOCKED (SecurityException / Sandbox Violation)**.
- **Actual Result**: **ALLOWED (Security Breach)** because the resolved path string started with `workspaceRoot`.
- **Failure Reason**: Lack of cross-platform path normalization. Windows drive letter prefixes (`^[a-zA-Z]:`) and backslash separators were not normalized prior to POSIX resolution.

---

### Breach 2: Alternate Separators Traversal on POSIX/Linux
- **Test Name**: `Alternate Separators Traversal`
- **Attack Payload**: `src\..\..\..\Windows`
- **Input Representation**: Backslash-separated parent directory traversal.
- **Workspace Root on Linux**: `/home/runner/work/Antigravity/Antigravity/tests/fixtures/todo-crash`
- **Canonicalization Behavior on Linux**: On POSIX/Linux, `src\..\..\..\Windows` was treated as a single literal filename with backslashes instead of directory traversal segments.
- **Resolved Path**: `/home/runner/work/Antigravity/Antigravity/tests/fixtures/todo-crash/src\..\..\..\Windows`
- **Expected Result**: **BLOCKED (SecurityException / Sandbox Violation)**.
- **Actual Result**: **ALLOWED (Security Breach)**.
- **Failure Reason**: On POSIX systems, `path.resolve` did not recognize `\` as separator, failing to traverse up and therefore failing to detect the `..` escape.

---

### Latent Flaw 3: Sibling Directory Prefix Matching (Zip Slip Variant)
- **Vulnerability**: `target.path.startsWith(workspaceRoot.path)`
- **Risk**: If workspace is `/home/user/workspace`, an attacker path resolving to `/home/user/workspace_evil/secret.txt` evaluates `startsWith("/home/user/workspace")` as `true`!
- **Fix**: Must enforce exact directory boundary check: `target.equals(root) || target.startsWith(root + separator)`.

---

## 3. End-to-End Execution Trace

```text
Attacker-Controlled Payload (e.g. "C:\Windows\System32\cmd.exe" or "src\..\..\..\Windows")
        │
        ▼
[Layer 1: Raw String Pre-Filter]
• Recursively decode URL encoding (up to 3 iterations to prevent %252e double-encoding)
• Strip/Reject null bytes (\0)
• Detect and reject URL schemes (file://, http://, https://)
• Detect and reject UNC paths (\\, //)
• Detect Windows drive letter absolute paths (^[a-zA-Z]:) on any platform
        │
        ▼
[Layer 2: Cross-Platform Separator Normalization]
• Replace all backslashes '\' with standard '/'
• Collapse redundant slashes ('///' -> '/')
        │
        ▼
[Layer 3: Canonical Path & Symlink Resolution]
• Canonicalize path via File.canonicalFile / fs.realpathSync
• Resolve nearest existing parent if target file does not yet exist
        │
        ▼
[Layer 4: Strict Directory Boundary Enforcement]
• Verify: resolvedPath == rootPath || resolvedPath.startsWith(rootPath + File.separator)
• Verify relative path from root does not start with '..'
        │
        ▼
[Layer 5: Tool Authorization]
• PermissionManager checks Autonomy Tier
• Authorized filesystem operation executes safely
```

---

## 4. Root Cause Summary & Code Fixes

1. **Normalized Separators**: All backslashes are normalized to standard separators before path resolution.
2. **Drive Letter Sanitization**: Windows drive paths (`^[a-zA-Z]:`) are detected and rejected as unauthorized absolute path escapes when operating inside a project workspace.
3. **Directory Boundary Check**: The prefix check is strictly hardened with `root + File.separator` to eliminate sibling directory escapes.
4. **Iterative URL Decoding**: Multi-pass URL decoding catches double-encoded payloads (`%252e%252e%252f`).
