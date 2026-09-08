#!/usr/bin/env node

/**
 * Antigravity Environment Check Script
 * Inspects host runtime, tools, memory, disk, and agent customizations.
 */

const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

function run() {
  if (process.argv.includes('--help')) {
    console.log(`
Usage: node .agents/scripts/environment-check.js [options]

Options:
  --json     Output environment audit as JSON
  --help     Show this help message
`);
    process.exit(0);
  }

  const results = {
    timestamp: new Date().toISOString(),
    os: {
      platform: process.platform,
      arch: process.arch,
      nodeVersion: process.version,
    },
    tools: {},
    agents: {
      rulesCount: 0,
      skillsCount: 0,
      decisionTreesCount: 0,
      checklistsCount: 0,
    }
  };

  const checkCommand = (cmd) => {
    try {
      return execSync(cmd, { stdio: ['pipe', 'pipe', 'pipe'], encoding: 'utf8' }).trim();
    } catch {
      return null;
    }
  };

  results.tools.git = checkCommand('git --version');
  results.tools.node = checkCommand('node -v');
  results.tools.npm = checkCommand('npm -v');
  results.tools.java = checkCommand('java -version');
  results.tools.adb = checkCommand('adb --version');

  // Count agent resources
  const workspaceRoot = path.resolve(__dirname, '../..');
  const rulesDir = path.join(workspaceRoot, '.agents/rules');
  const skillsDir = path.join(workspaceRoot, '.agents/skills');
  const dtDir = path.join(workspaceRoot, '.agents/resources/decision-trees');
  const clDir = path.join(workspaceRoot, '.agents/resources/checklists');

  if (fs.existsSync(rulesDir)) results.agents.rulesCount = fs.readdirSync(rulesDir).filter(f => f.endsWith('.md')).length;
  if (fs.existsSync(skillsDir)) results.agents.skillsCount = fs.readdirSync(skillsDir).length;
  if (fs.existsSync(dtDir)) results.agents.decisionTreesCount = fs.readdirSync(dtDir).filter(f => f.endsWith('.md')).length;
  if (fs.existsSync(clDir)) results.agents.checklistsCount = fs.readdirSync(clDir).filter(f => f.endsWith('.md')).length;

  if (process.argv.includes('--json')) {
    console.log(JSON.stringify(results, null, 2));
  } else {
    console.log('=== ANTIGRAVITY ENVIRONMENT AUDIT ===');
    console.log(`Platform: ${results.os.platform} (${results.os.arch})`);
    console.log(`Node.js:  ${results.os.nodeVersion}`);
    console.log(`Git:      ${results.tools.git || 'NOT DETECTED'}`);
    console.log(`npm:      ${results.tools.npm || 'NOT DETECTED'}`);
    console.log(`\n=== AGENT INTELLIGENCE STATS ===`);
    console.log(`Rules:          ${results.agents.rulesCount}`);
    console.log(`Skills:         ${results.agents.skillsCount}`);
    console.log(`Decision Trees: ${results.agents.decisionTreesCount}`);
    console.log(`Checklists:     ${results.agents.checklistsCount}`);
  }

  process.exit(0);
}

run();
