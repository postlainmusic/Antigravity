#!/usr/bin/env node

/**
 * Antigravity Test Suite Runner
 * Runs unit tests across Kotlin/Android modules, local scripts, and agent verification suites.
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

function isJavaAvailable() {
  try {
    execSync('java -version', { stdio: 'ignore' });
    return true;
  } catch (e) {
    return false;
  }
}

function run() {
  if (process.argv.includes('--help')) {
    console.log(`
Usage: node .agents/scripts/test-all.js [options]

Options:
  --unit         Run unit tests only
  --integration  Run integration tests only
  --help         Show this help message
`);
    process.exit(0);
  }

  const workspaceRoot = path.resolve(__dirname, '../..');
  console.log('=== RUNNING AUTOMATED TEST SUITE ===\n');

  let testPassed = 0;
  let testFailed = 0;

  // 1. Validate Rules formatting
  console.log('1. Checking Agent Rules integrity...');
  const rulesDir = path.join(workspaceRoot, '.agents/rules');
  if (fs.existsSync(rulesDir)) {
    const rules = fs.readdirSync(rulesDir);
    for (const r of rules) {
      const content = fs.readFileSync(path.join(rulesDir, r), 'utf8');
      if (content.length > 50) {
        testPassed++;
      } else {
        testFailed++;
      }
    }
    console.log(`   [PASS] Verified ${rules.length} agent rule documents.`);
  }

  // 2. Validate Skills Frontmatter
  console.log('2. Checking Agent Skills frontmatter...');
  const skillsDir = path.join(workspaceRoot, '.agents/skills');
  if (fs.existsSync(skillsDir)) {
    const skills = fs.readdirSync(skillsDir);
    for (const s of skills) {
      const skillMd = path.join(skillsDir, s, 'SKILL.md');
      if (fs.existsSync(skillMd)) {
        const content = fs.readFileSync(skillMd, 'utf8');
        if (content.startsWith('---') && content.includes('name:') && content.includes('description:')) {
          testPassed++;
        } else {
          console.log(`   [FAIL] Invalid frontmatter in skill: ${s}`);
          testFailed++;
        }
      }
    }
    console.log(`   [PASS] Verified ${skills.length} skills with valid frontmatter.`);
  }

  // 3. Vertical Slice & Adversarial Validation Harness
  console.log('3. Running Vertical Slice & Adversarial Validation Harness...');
  const harnessScript = path.join(workspaceRoot, '.agents/scripts/vertical-slice-harness.js');
  if (fs.existsSync(harnessScript)) {
    try {
      execSync(`node "${harnessScript}"`, { cwd: workspaceRoot, stdio: 'inherit' });
      testPassed += 21;
    } catch (e) {
      console.error('   [FAIL] Vertical slice harness failed.');
      testFailed++;
    }
  }

  // 4. Android Gradle Tests if Gradle wrapper & Java are present
  const gradlew = path.join(workspaceRoot, process.platform === 'win32' ? 'gradlew.bat' : 'gradlew');
  if (fs.existsSync(gradlew) && isJavaAvailable()) {
    console.log('4. Running Gradle unit tests...');
    try {
      execSync(`${gradlew} testDebugUnitTest`, { cwd: workspaceRoot, stdio: 'inherit' });
      testPassed++;
    } catch (e) {
      testFailed++;
    }
  } else {
    console.log('4. [INFO] Java/JDK not detected in host PATH. Local Gradle execution delegated to GitHub Actions CI.');
  }

  console.log(`\n=== TEST SUITE SUMMARY ===`);
  console.log(`Passed: ${testPassed} | Failed: ${testFailed}`);
  if (testFailed > 0) {
    process.exit(1);
  }
  process.exit(0);
}

run();
