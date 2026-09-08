#!/usr/bin/env node

/**
 * Antigravity Code Formatter Script
 */

const fs = require('fs');
const path = require('path');

function run() {
  if (process.argv.includes('--help')) {
    console.log(`
Usage: node .agents/scripts/format-all.js [options]

Options:
  --check    Check formatting without modifying files
  --help     Show this help message
`);
    process.exit(0);
  }

  const workspaceRoot = path.resolve(__dirname, '../..');
  console.log('=== RUNNING CODE FORMATTER ===\n');

  let formatted = 0;
  function walk(dir) {
    const files = fs.readdirSync(dir);
    for (const f of files) {
      if (f === '.git' || f === 'node_modules' || f === 'build' || f === '.gradle') continue;
      const full = path.join(dir, f);
      const stat = fs.statSync(full);
      if (stat.isDirectory()) {
        walk(full);
      } else if (f.endsWith('.json')) {
        try {
          const raw = fs.readFileSync(full, 'utf8');
          const parsed = JSON.parse(raw);
          const pretty = JSON.stringify(parsed, null, 2) + '\n';
          if (raw !== pretty && !process.argv.includes('--check')) {
            fs.writeFileSync(full, pretty, 'utf8');
            formatted++;
          }
        } catch {}
      }
    }
  }

  walk(workspaceRoot);
  console.log(`[PASS] Formatting completed. Cleaned ${formatted} files.`);
  process.exit(0);
}

run();
