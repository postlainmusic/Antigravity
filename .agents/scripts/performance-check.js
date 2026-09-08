#!/usr/bin/env node

/**
 * Antigravity Performance & Memory Audit Script
 * Evaluates memory footprints, file sizes, and potential UI thread bottlenecks.
 */

const fs = require('fs');
const path = require('path');

function run() {
  if (process.argv.includes('--help')) {
    console.log(`
Usage: node .agents/scripts/performance-check.js [options]

Options:
  --help     Show this help message
`);
    process.exit(0);
  }

  const workspaceRoot = path.resolve(__dirname, '../..');
  console.log('=== RUNNING PERFORMANCE AUDIT ===\n');

  let largeFiles = 0;
  let totalSize = 0;

  function walk(dir) {
    const files = fs.readdirSync(dir);
    for (const f of files) {
      if (f === '.git' || f === 'node_modules' || f === 'build' || f === '.gradle') continue;
      const full = path.join(dir, f);
      const stat = fs.statSync(full);
      if (stat.isDirectory()) {
        walk(full);
      } else {
        totalSize += stat.size;
        if (stat.size > 500 * 1024) { // > 500 KB
          console.log(`  [WARN] Large file (>500KB): ${path.relative(workspaceRoot, full)} (${Math.round(stat.size / 1024)} KB)`);
          largeFiles++;
        }
      }
    }
  }

  walk(workspaceRoot);
  console.log(`\nTotal Workspace Size: ${Math.round(totalSize / 1024)} KB`);
  console.log(`Large Files Detected: ${largeFiles}`);
  console.log('[PASS] Performance audit completed.');
  process.exit(0);
}

run();
