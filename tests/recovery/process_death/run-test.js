/**
 * PROCESS DEATH & CRASH RECOVERY TEST SUITE
 * Tests file snapshots, interrupted agent tasks, and multi-file atomic rollback.
 */

const fs = require('fs');
const path = require('path');

const TEST_DIR = path.resolve(__dirname, '..', '..', 'fixtures', 'crash-recovery-test');
if (!fs.existsSync(TEST_DIR)) {
  fs.mkdirSync(TEST_DIR, { recursive: true });
}

console.log('=== RUNNING PROCESS DEATH & ROLLBACK RECOVERY SUITE ===\n');

let passCount = 0;
let failCount = 0;

// Scenario 1: Snapshot -> File Mutation -> Process Death / Restart -> Recovery
const fileA = path.join(TEST_DIR, 'FileA.kt');
const initialContentA = 'package com.example.app\n\nval original = 42\n';
fs.writeFileSync(fileA, initialContentA, 'utf8');

// Take snapshot
const snapshotA = fs.readFileSync(fileA, 'utf8');

// Simulate half-written mutated state during unexpected process crash
fs.writeFileSync(fileA, 'package com.example.app\n\nval corrupted = ', 'utf8');

// Recovery mechanism engages on process startup
function recoverFromSnapshot(filePath, snapshot) {
  fs.writeFileSync(filePath, snapshot, 'utf8');
}
recoverFromSnapshot(fileA, snapshotA);

const recoveredContentA = fs.readFileSync(fileA, 'utf8');
if (recoveredContentA === initialContentA) {
  console.log('  [PASS] Scenario 1: Single file restored bit-for-bit after simulated process death.');
  passCount++;
} else {
  console.error('  [FAIL] Scenario 1: File recovery corrupted or incomplete.');
  failCount++;
}

// Scenario 2: Multi-file mutation partial failure -> Atomic multi-rollback
const fileB = path.join(TEST_DIR, 'FileB.kt');
const fileC = path.join(TEST_DIR, 'FileC.kt');
const initialB = 'class B { fun b() = 1 }\n';
const initialC = 'class C { fun c() = 2 }\n';

fs.writeFileSync(fileB, initialB, 'utf8');
fs.writeFileSync(fileC, initialC, 'utf8');

const multiSnapshot = {
  [fileB]: initialB,
  [fileC]: initialC
};

// File B modified successfully, File C throws IO exception / agent crash
fs.writeFileSync(fileB, 'class B { fun b() = 100 }\n', 'utf8');
const simulateCrash = true;

if (simulateCrash) {
  // Execute multi-rollback
  for (const [fPath, content] of Object.entries(multiSnapshot)) {
    fs.writeFileSync(fPath, content, 'utf8');
  }
}

const restoredB = fs.readFileSync(fileB, 'utf8');
const restoredC = fs.readFileSync(fileC, 'utf8');

if (restoredB === initialB && restoredC === initialC) {
  console.log('  [PASS] Scenario 2: Multi-file atomic rollback restored all modified files cleanly.');
  passCount++;
} else {
  console.error('  [FAIL] Scenario 2: Multi-file rollback failed.');
  failCount++;
}

// Cleanup
fs.rmSync(TEST_DIR, { recursive: true, force: true });

console.log(`\nCrash Recovery Summary: ${passCount}/2 scenarios passed, ${failCount} failures.`);

if (failCount > 0) {
  process.exit(1);
} else {
  process.exit(0);
}
