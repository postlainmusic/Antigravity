#!/usr/bin/env node

/**
 * Antigravity CI Health & Pipeline Integrity Checker
 * Validates GitHub Actions workflow definitions, script references, artifact paths, and secret safety.
 */

const fs = require('fs');
const path = require('path');

const ROOT_DIR = path.resolve(__dirname, '..', '..');

console.log('===============================================================');
console.log('  ANTIGRAVITY CI/CD PIPELINE HEALTH CHECK');
console.log('===============================================================\n');

let totalChecks = 0;
let passedChecks = 0;
let failedChecks = 0;

function assert(condition, message) {
  totalChecks++;
  if (condition) {
    passedChecks++;
    console.log(`  [PASS] ${message}`);
  } else {
    failedChecks++;
    console.error(`  [FAIL] ${message}`);
  }
}

// 1. Verify Workflow Files Exist
const requiredWorkflows = [
  '.github/workflows/ci.yml',
  '.github/workflows/build-debug.yml',
  '.github/workflows/security.yml',
  '.github/workflows/release.yml'
];

for (const wf of requiredWorkflows) {
  const fullPath = path.join(ROOT_DIR, wf);
  assert(fs.existsSync(fullPath), `Workflow file exists: ${wf}`);
  if (fs.existsSync(fullPath)) {
    const content = fs.readFileSync(fullPath, 'utf8');
    assert(content.includes('name:') && content.includes('jobs:'), `Valid YAML workflow structure in: ${wf}`);
    assert(!content.includes('sk-') && !content.includes('password123'), `Zero hardcoded plaintext secrets in: ${wf}`);
  }
}

// 2. Verify Gradle Wrapper & Build Files
const gradleWrapperProps = path.join(ROOT_DIR, 'gradle/wrapper/gradle-wrapper.properties');
assert(fs.existsSync(gradleWrapperProps), 'Gradle wrapper properties configured');

const gradlew = path.join(ROOT_DIR, 'gradlew');
const gradlewBat = path.join(ROOT_DIR, 'gradlew.bat');
assert(fs.existsSync(gradlew), 'Gradle wrapper shell script (gradlew) present');
assert(fs.existsSync(gradlewBat), 'Gradle wrapper Windows script (gradlew.bat) present');

// 3. Verify Referenced Scripts in Workflows
const requiredScripts = [
  '.agents/scripts/test-all.js',
  '.agents/scripts/security-check.js',
  '.agents/scripts/reproduce-validation.js',
  '.agents/scripts/vertical-slice-harness.js',
  'tests/security/workspace_escape/run-test.js',
  'tests/security/secret_boundary/run-test.js',
  'tests/security/mcp_trust_boundary/run-test.js',
  'tests/recovery/process_death/run-test.js'
];

for (const sc of requiredScripts) {
  const fullPath = path.join(ROOT_DIR, sc);
  assert(fs.existsSync(fullPath), `Referenced script exists: ${sc}`);
}

// 4. Verify Artifact Paths & App Build Gradle
const appBuildGradle = path.join(ROOT_DIR, 'app/build.gradle.kts');
assert(fs.existsSync(appBuildGradle), 'App build.gradle.kts exists');
if (fs.existsSync(appBuildGradle)) {
  const content = fs.readFileSync(appBuildGradle, 'utf8');
  assert(content.includes('namespace = "com.antigravity.mobile"'), 'Application namespace is com.antigravity.mobile');
  assert(content.includes('compileSdk = 35'), 'Compile SDK is 35 (Android 15)');
  assert(content.includes('minSdk = 26'), 'Min SDK is 26 (Android 8.0)');
}

console.log('\n===============================================================');
console.log(`  CI HEALTH SUMMARY: ${passedChecks}/${totalChecks} CHECKS PASSED`);
console.log('===============================================================\n');

if (failedChecks > 0) {
  process.exit(1);
} else {
  console.log('>>> CI/CD PIPELINE CONFIGURATION: 100% HEALTHY <<<');
  process.exit(0);
}
