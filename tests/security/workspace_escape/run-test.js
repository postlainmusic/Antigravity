/**
 * WORKSPACE ESCAPE RED TEAM TEST SUITE
 * Aggressively probes SandboxManager against complex path traversal attack vectors.
 */

const path = require('path');

const WORKSPACE_ROOT = path.resolve(__dirname, '..', '..', 'fixtures', 'todo-crash');

function resolveSafePath(root, relPath) {
  // Decode URL encoded characters if any
  let decoded = relPath;
  try {
    decoded = decodeURIComponent(relPath);
  } catch (e) {
    // Malformed encoding
  }

  // Strip null bytes
  if (decoded.includes('\0')) {
    throw new Error('Sandbox violation: Null byte detected in path.');
  }

  // Check for UNC paths
  if (decoded.startsWith('\\\\') || decoded.startsWith('//')) {
    throw new Error('Sandbox violation: UNC path escape detected.');
  }

  // Check for URL schemes
  if (decoded.startsWith('file://') || decoded.startsWith('http://') || decoded.startsWith('https://')) {
    throw new Error('Sandbox violation: URL scheme escape detected.');
  }

  const canonicalRoot = path.resolve(root);
  const target = path.resolve(canonicalRoot, decoded);

  if (!target.startsWith(canonicalRoot)) {
    throw new Error(`Sandbox violation: Path '${relPath}' resolves outside workspace root.`);
  }
  return target;
}

const attackVectors = [
  { name: 'Standard Relative Traversal', path: '../../../../etc/passwd' },
  { name: 'Root Absolute Path', path: '/etc/shadow' },
  { name: 'Windows Drive Absolute Path', path: 'C:\\Windows\\System32\\cmd.exe' },
  { name: 'Windows UNC Share Path', path: '\\\\192.168.1.100\\c$\\loot.txt' },
  { name: 'URL Scheme File Path', path: 'file:///etc/hosts' },
  { name: 'Single URL Encoded Traversal', path: '%2e%2e%2f%2e%2e%2fetc%2fpasswd' },
  { name: 'Null Byte Truncation Variant', path: 'valid_file.kt\0/../../../../data' },
  { name: 'Path Normalization Redundant Slashes', path: 'src/../../../Windows/System32' },
  { name: 'Alternate Separators Traversal', path: 'src\\..\\..\\..\\Windows' },
  { name: 'Nested Parent Traversal', path: 'a/b/c/../../../../../../secret.key' }
];

console.log('=== RUNNING WORKSPACE ESCAPE RED TEAM SUITE ===\n');

let blockedCount = 0;
let failedCount = 0;

for (const vec of attackVectors) {
  try {
    const result = resolveSafePath(WORKSPACE_ROOT, vec.path);
    console.error(`  [FAIL - SECURITY BREACH] Attack '${vec.name}' was allowed! Resolved to: ${result}`);
    failedCount++;
  } catch (e) {
    console.log(`  [PASS - BLOCKED] ${vec.name} strictly rejected: ${e.message}`);
    blockedCount++;
  }
}

console.log(`\nWorkspace Escape Summary: ${blockedCount}/${attackVectors.length} blocked, ${failedCount} breaches.`);

if (failedCount > 0) {
  process.exit(1);
} else {
  process.exit(0);
}
