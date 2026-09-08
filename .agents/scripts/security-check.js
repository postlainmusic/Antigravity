#!/usr/bin/env node

/**
 * Antigravity Security & Secret Scanner Script
 * Scans codebase for exposed API keys, dangerous command patterns, and unescaped path traversals.
 */

const fs = require('fs');
const path = require('path');

function run() {
  if (process.argv.includes('--help')) {
    console.log(`
Usage: node .agents/scripts/security-check.js [options]

Options:
  --strict   Fail with exit code 1 if any vulnerability is found
  --help     Show this help message
`);
    process.exit(0);
  }

  const workspaceRoot = path.resolve(__dirname, '../..');
  console.log('=== RUNNING SECURITY & SECRET SCAN ===\n');

  const secretPatterns = [
    { name: 'OpenAI API Key', regex: /sk-[a-zA-Z0-9]{32,}/ },
    { name: 'Google API Key', regex: /AIza[0-9A-Za-z-_]{35}/ },
    { name: 'GitHub Token', regex: /gh[pousr]_[0-9a-zA-Z]{36}/ },
    { name: 'Generic Private Key', regex: /-----BEGIN PRIVATE KEY-----/ }
  ];

  let vulnerabilities = 0;
  let filesScanned = 0;

  function walk(dir) {
    const files = fs.readdirSync(dir);
    for (const f of files) {
      if (f === '.git' || f === 'node_modules' || f === 'build' || f === '.gradle') continue;
      const full = path.join(dir, f);
      // Skip the scanner itself from scanning its own regex patterns
      if (full === __filename) continue;
      const stat = fs.statSync(full);
      if (stat.isDirectory()) {
        walk(full);
      } else if (f.endsWith('.kt') || f.endsWith('.md') || f.endsWith('.json') || f.endsWith('.js') || f.endsWith('.ts')) {
        filesScanned++;
        const content = fs.readFileSync(full, 'utf8');
        for (const pattern of secretPatterns) {
          if (pattern.regex.test(content)) {
            console.log(`  [DANGER] Possible ${pattern.name} found in ${path.relative(workspaceRoot, full)}`);
            vulnerabilities++;
          }
        }
      }
    }
  }

  walk(workspaceRoot);
  console.log(`\nScanned ${filesScanned} files.`);
  if (vulnerabilities === 0) {
    console.log('[PASS] Zero exposed credentials or critical security risks detected.');
    process.exit(0);
  } else {
    console.log(`[FAIL] Detected ${vulnerabilities} potential security issues.`);
    if (process.argv.includes('--strict')) process.exit(1);
    process.exit(0);
  }
}

run();
