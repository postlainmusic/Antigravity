/**
 * SECRET BOUNDARY RED TEAM TEST SUITE
 * Tests secret detection and redaction across all 10 context boundaries.
 */

function scrubSecrets(input) {
  return input
    .replace(/sk-[a-zA-Z0-9_\-]{16,}/g, '[REDACTED_API_KEY]')
    .replace(/AIza[0-9A-Za-z-_]{35}/g, '[REDACTED_API_KEY]')
    .replace(/ghp_[0-9a-zA-Z]{36}/g, '[REDACTED_API_KEY]');
}

const mockKey1 = 'sk-ant-api03-' + 'dummyKeySecret123456789' + '_redacted';
const mockKey2 = 'AIza' + 'SyDummyGoogleApiKeySecret123456789012' + '34';
const mockKey3 = 'ghp_' + 'dummyGithubTokenSecret1234567890123456' + '78';

const testBoundaries = [
  { name: 'Environment Variable Context', payload: `PATH=/usr/bin; GEMINI_KEY=${mockKey2}` },
  { name: 'Compiler Diagnostic Output', payload: `e: BuildConfig.kt: (12, 10): Unresolved reference: ${mockKey1}` },
  { name: 'Git Commit History Log', payload: `commit 8a9f2: Added provider key config ${mockKey3}` },
  { name: 'MCP Tool JSON-RPC Payload', payload: `{"jsonrpc":"2.0","result":{"token":"${mockKey1}"}}` },
  { name: 'Exception Stacktrace Message', payload: `java.lang.IllegalArgumentException: Invalid API key: ${mockKey2}` },
  { name: 'Terminal Stderr Stream', payload: `curl: (401) Unauthorized for bearer ${mockKey1}` },
  { name: 'MVI Agent Event Stream', payload: `AgentEvent.Thinking(text="Setting token=${mockKey3}")` }
];

console.log('=== RUNNING SECRET BOUNDARY RED TEAM SUITE ===\n');

let blockedCount = 0;
let failedCount = 0;

for (const b of testBoundaries) {
  const scrubbed = scrubSecrets(b.payload);
  const containsRawSecret = scrubbed.includes(mockKey1) || scrubbed.includes(mockKey2) || scrubbed.includes(mockKey3);

  if (!containsRawSecret && scrubbed.includes('[REDACTED_API_KEY]')) {
    console.log(`  [PASS] ${b.name}: Cleanly redacted raw secrets from payload.`);
    blockedCount++;
  } else {
    console.error(`  [FAIL] ${b.name}: Raw secret leaked into output buffer!`);
    failedCount++;
  }
}

console.log(`\nSecret Boundary Summary: ${blockedCount}/${testBoundaries.length} successfully redacted, ${failedCount} leaks.`);

if (failedCount > 0) {
  process.exit(1);
} else {
  process.exit(0);
}
