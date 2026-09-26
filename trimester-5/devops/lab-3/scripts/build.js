'use strict';

// Copies src/ into dist/ so CI has a build artifact to upload.
const fs = require('node:fs');
const path = require('node:path');

const root = path.join(__dirname, '..');
const dist = path.join(root, 'dist');

fs.rmSync(dist, { recursive: true, force: true });
fs.cpSync(path.join(root, 'src'), dist, { recursive: true });
console.log(`Built ${fs.readdirSync(dist).length} file(s) into dist/`);
