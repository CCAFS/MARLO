import fs from 'node:fs';
import path from 'node:path';

const sourceFile = 'dist/lambda.js';
const outputDir = 'lambda-package';
const outputFile = path.join(outputDir, 'lambda.js');

if (!fs.existsSync(sourceFile)) {
  console.error(`ERROR: ${sourceFile} not found. Run "npm run build" first.`);
  process.exit(1);
}

fs.rmSync(outputDir, { recursive: true, force: true });
fs.mkdirSync(outputDir, { recursive: true });
fs.copyFileSync(sourceFile, outputFile);

const stats = fs.statSync(outputFile);
console.log(`Lambda package ready: ${outputFile} (${(stats.size / 1024 / 1024).toFixed(2)} MB)`);
