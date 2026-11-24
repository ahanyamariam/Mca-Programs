'use strict';

// Dependency-free "lint": syntax-check every .js file under src/, test/ and scripts/.
const { spawnSync } = require('node:child_process');
const fs = require('node:fs');
const path = require('node:path');

const root = path.join(__dirname, '..');
const dirs = ['src', 'test', 'scripts'];

function collect(dir) {
  return fs.readdirSync(dir, { withFileTypes: true }).flatMap((entry) => {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) return collect(full);
    return entry.name.endsWith('.js') ? [full] : [];
  });
}

let failed = false;
for (const file of dirs.flatMap((d) => collect(path.join(root, d)))) {
  const result = spawnSync(process.execPath, ['--check', file], { encoding: 'utf8' });
  if (result.status !== 0) {
    failed = true;
    console.error(`FAIL ${path.relative(root, file)}\n${result.stderr}`);
  } else {
    console.log(`ok   ${path.relative(root, file)}`);
  }
}
process.exit(failed ? 1 : 0);
