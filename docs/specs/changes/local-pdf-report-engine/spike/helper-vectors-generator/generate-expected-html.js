// Writes the HTML that production builds with REAL Handlebars.js: the helpers of pdf.module.ts (ifCond included;
// pdf-creator-node re-registers an ifCond with the same body) compiling `templateData` against `data.data`.
//
// Fixtures (what the test resources hold):
//   NODE_MODULES=<dir with handlebars> node generate-expected-html.js <pdf.module.ts> fixtures \
//     <repo>/marlo-web/src/test/resources/pdf/templates <repo>/marlo-web/src/test/resources/pdf/payloads \
//     <repo>/marlo-web/src/test/resources/pdf/expected-html
//   (one <name>.html per <name>.html template + <name>-payload.json, whose templateData is "__TEMPLATE__")
// Full payloads (templateData inside, e.g. real data kept outside the repo):
//   NODE_MODULES=<dir> node generate-expected-html.js <pdf.module.ts> payload <payload.json> <out.html>
const fs = require('fs');
const path = require('path');
const BASE = path.resolve(process.env.NODE_MODULES || path.join(__dirname, '../baseline/node_modules'));
const Handlebars = require(path.join(BASE, 'handlebars'));
const [tsFile, mode, ...rest] = process.argv.slice(2);
const ts = fs.readFileSync(tsFile, 'utf8').split('\n');
const start = ts.findIndex((l) => l.includes("Handlebars.registerHelper('safeEmpty'"));
const end = ts.findIndex((l, i) => i > start && /^  }$/.test(l));
const code = ts.slice(start, end).join('\n').replace('const value: string', 'const value').replace('list: string', 'list');
console.error = () => {};
new Function('Handlebars', 'Exception', code)(Handlebars, Handlebars.Exception);

const html = (template, data) => Handlebars.compile(template)(data);
if (mode === 'fixtures') {
  const [tplDir, payloadDir, outDir] = rest;
  fs.mkdirSync(outDir, { recursive: true });
  fs.readdirSync(tplDir).filter((f) => f.endsWith('.html')).sort().forEach((f) => {
    const name = f.replace(/\.html$/, '');
    const payload = JSON.parse(fs.readFileSync(path.join(payloadDir, name + '-payload.json'), 'utf8'));
    const out = html(fs.readFileSync(path.join(tplDir, f), 'utf8'), payload.data.data);
    fs.writeFileSync(path.join(outDir, f), out);
    console.log(f, out.length);
  });
} else {
  const [payloadFile, out] = rest;
  const p = JSON.parse(fs.readFileSync(payloadFile, 'utf8')).data;
  fs.writeFileSync(out, html(p.templateData, p.data));
}
