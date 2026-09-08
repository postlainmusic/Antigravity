# Security & Sandbox Checklist

- [ ] Path traversal checks enforce strict containment within the active workspace.
- [ ] API keys and sensitive tokens encrypted via Android KeyStore.
- [ ] Log output scrubber redacts authorization tokens, secrets, and private keys.
- [ ] Blacklisted shell commands (`rm -rf /`, format disks, fork bombs) blocked.
- [ ] User approval gated for high-risk operations (file deletion, git force push).
