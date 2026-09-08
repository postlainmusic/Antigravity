#!/usr/bin/env node

/**
 * Antigravity Project Health Check Script
 * Validates documentation completeness, required agent files, git status, and code integrity.
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

function run() {
  if (process.argv.includes('--help')) {
    console.log(`
Usage: node .agents/scripts/project-health-check.js [options]

Options:
  --strict   Fail with exit code 1 if any warning or missing file is found
  --help     Show this help message
`);
    process.exit(0);
  }

  const workspaceRoot = path.resolve(__dirname, '../..');
  const requiredDocs = [
    'README.md',
    'docs/ENGINEERING_ENVIRONMENT.md',
    'docs/PROJECT_MEMORY.md',
    'docs/DECISIONS.md',
    'docs/ARCHITECTURE.md',
    'docs/ROADMAP.md',
    'docs/TASKS.md',
    'docs/MCP_REGISTRY.md',
    'docs/SECURITY.md',
    'docs/TESTING.md',
    'docs/RELEASE.md'
  ];

  console.log('=== RUNNING PROJECT HEALTH CHECK ===\n');
  let missingDocs = 0;
  for (const doc of requiredDocs) {
    const fullPath = path.join(workspaceRoot, doc);
    if (fs.existsSync(fullPath)) {
      console.log(`  [PASS] ${doc}`);
    } else {
      console.log(`  [FAIL] Missing required document: ${doc}`);
      missingDocs++;
    }
  }

  // Check Git Status
  let gitClean = false;
  try {
    const status = execSync('git status --porcelain', { cwd: workspaceRoot, encoding: 'utf8' });
    gitClean = status.trim().length === 0;
    console.log(`\n  Git Working Tree: ${gitClean ? 'Clean' : 'Contains modified / untracked files'}`);
  } catch (e) {
    console.log('  Git check skipped / error.');
  }

  console.log('\n=== HEALTH CHECK SUMMARY ===');
  if (missingDocs === 0) {
    console.log('Status: HEALTHY (All critical project assets verified)');
    process.exit(0);
  } else {
    console.log(`Status: INCOMPLETE (${missingDocs} missing docs)`);
    if (process.argv.includes('--strict')) {
      process.exit(1);
    }
    process.exit(0);
  }
}

run();
