// Production render of one MARLO report payload, exactly as reports-microservice does for 'pdf.generate':
// Handlebars.js + the 17 helpers of pdf.module.ts + pdf-creator-node (html-pdf, PhantomJS), with the payload's
// own templateData and options. Run inside Docker node:23 linux/amd64 (the microservice base image).
// Usage: node prod-render.js <pdf.module.ts> <payload.json> <out.pdf>
const fs = require('fs');
const path = require('path');
const MODULES = process.env.NODE_MODULES_DIR || path.resolve(__dirname, 'node_modules');
const Handlebars = require(path.join(MODULES, 'handlebars'));
const { create } = require(path.join(MODULES, 'pdf-creator-node'));
const [tsFile, payloadFile, outFile] = process.argv.slice(2);
const ts = fs.readFileSync(tsFile, 'utf8').split('\n');
const start = ts.findIndex((l) => l.includes("Handlebars.registerHelper('safeEmpty'"));
const end = ts.findIndex((l, i) => i > start && /^  }$/.test(l));
let code = ts.slice(start, end).join('\n');
code = code.replace('const value: string', 'const value').replace('list: string', 'list');
new Function('Handlebars', 'Exception', code)(Handlebars, Handlebars.Exception);
// pdf-creator-node's require('handlebars') resolves to this same module instance, so it sees the helpers.
const msg = JSON.parse(fs.readFileSync(payloadFile, 'utf8'));
const p = msg.data && msg.data.templateData ? msg.data : msg;
const fontStyle = p.font ? `<style>body { font-family: ${p.font}; }</style>` : '';
const t0 = Date.now();
create({ html: `${fontStyle}${p.templateData}`, data: p.data, type: 'buffer' }, p.options)
  .then((b) => { fs.writeFileSync(outFile, b); console.log('prod ok', b.length, 'bytes', Date.now() - t0, 'ms'); })
  .catch((e) => { console.error('prod FAIL', e && e.message); process.exit(1); });
