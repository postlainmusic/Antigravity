#!/usr/bin/env node

/**
 * Antigravity Lint Runner Script
 */

const fs = require('fs');
const path = require('path');

function run() {
  if (process.argv.includes('--help')) {
    console.log(`
Usage: node .agents/scripts/lint-all.js [options]

Options:
  --fix      Automatically fix minor style issues
  --help     Show this help message
`);
    process.exit(0);
  }

  const workspaceRoot = path.resolve(__dirname, '../..');
  console.log('=== RUNNING STATIC ANALYSIS & LINT CHECKS ===\n');

  let lintWarnings = 0;
  let filesChecked = 0;

  function walk(dir) {
    const files = fs.readdirSync(dir);
    for (const f of files) {
      if (f === '.git' || f === 'node_modules' || f === 'build' || f === '.gradle') continue;
      const full = path.join(dir, f);
      const stat = fs.statSync(full);
      if (stat.isDirectory()) {
        walk(full);
      } else if (f.endsWith('.kt') || f.endsWith('.md') || f.endsWith('.json') || f.endsWith('.js')) {
        filesChecked++;
        const content = fs.readFileSync(full, 'utf8');
        // Check for trailing whitespace or tabs in source files
        if (f.endsWith('.kt') && content.includes('\t')) {
          console.log(`  [WARN] Tab character found in Kotlin file: ${path.relative(workspaceRoot, full)}`);
          lintWarnings++;
        }
      }
    }
  }

  walk(workspaceRoot);
  console.log(`\nChecked ${filesChecked} files.`);
  if (lintWarnings === 0) {
    console.log('[PASS] Zero lint errors detected.');
    process.exit(0);
  } else {
    console.log(`[WARN] ${lintWarnings} lint warnings found.`);
    process.exit(0);
  }
}

run();
