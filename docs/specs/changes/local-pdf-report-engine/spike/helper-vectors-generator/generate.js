// Generates marlo-web/src/test/resources/pdf/helper-vectors.json with REAL Handlebars.js.
// Usage: node generate.js <pdf.module.ts> <output.json>
const fs = require('fs');
const path = require('path');
const BASE = path.resolve(__dirname, '../baseline/node_modules');
const Handlebars = require(path.join(BASE, 'handlebars'));
const rows = require('./rows.js');
const [tsFile, outFile] = process.argv.slice(2);
const ts = fs.readFileSync(tsFile, 'utf8').split('\n');
const start = ts.findIndex((l) => l.includes("Handlebars.registerHelper('safeEmpty'"));
const end = ts.findIndex((l, i) => i > start && /^  }$/.test(l));
let code = ts.slice(start, end).join('\n');
code = code.replace('const value: string', 'const value').replace('list: string', 'list');
console.error = () => {};
new Function('Handlebars', 'Exception', code)(Handlebars, Handlebars.Exception);

const ser = (v, dbl) => {
  if (v === null) return 'null';
  if (Array.isArray(v)) return '[' + v.map((x) => ser(x, dbl)).join(',') + ']';
  switch (typeof v) {
    case 'number': return dbl && Number.isInteger(v) && Math.abs(v) < 1e21 ? String(v) + '.0' : JSON.stringify(v);
    case 'object': return '{' + Object.keys(v).map((k) => JSON.stringify(k) + ':' + ser(v[k], dbl)).join(',') + '}';
    default: return JSON.stringify(v);
  }
};
const hasNumber = (v) => v !== null && (typeof v === 'number' || (typeof v === 'object' && Object.values(v).some(hasNumber)));
const out = [];
const seen = new Set();
const push = (template, ctxText) => {
  const key = template + '\u0000' + ctxText;
  if (seen.has(key)) { return; }
  seen.add(key);
  let expected;
  try { expected = Handlebars.compile(template)(JSON.parse(ctxText)); } catch (e) { expected = { error: String(e.message || e).slice(0, 80) }; }
  out.push('{"template":' + JSON.stringify(template) + ',"context":' + ctxText + ',"expected":' + JSON.stringify(expected) + '}');
};
rows.forEach((r) => {
  push(r.template, ser(r.context, false));
  if (hasNumber(r.context)) { push(r.template, ser(r.context, true)); }
});
const gen = 'node generate.js <reports-microservice>/src/domain/api/pdf/pdf.module.ts <repo>/marlo-web/src/test/resources/pdf/helper-vectors.json'
  + ' (script kept with the spec evidence; handlebars ' + Handlebars.VERSION + '; helpers copied from pdf.module.ts at 6f16514;'
  + ' every row with a number is emitted twice, integral numbers as 1 and as 1.0, so Java sees Integer and Double)';
fs.mkdirSync(path.dirname(outFile), { recursive: true });
fs.writeFileSync(outFile, '{\n"_generator":' + JSON.stringify(gen) + ',\n"rows":[\n' + out.join(',\n') + '\n]}\n');
console.log('rows', out.length);
