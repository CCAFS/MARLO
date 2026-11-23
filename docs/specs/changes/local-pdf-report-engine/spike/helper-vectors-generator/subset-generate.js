// Generates marlo-web/src/test/resources/pdf/subset-vectors.json with REAL Handlebars.js.
// Usage: node subset-generate.js <repo>/marlo-web/src/test/resources/pdf
//   (reads <dir>/templates/*.html, writes <dir>/subset-vectors.json)
// Env:   HANDLEBARS_MODULE = path of the handlebars 4.7.8 package (default ../baseline/node_modules/handlebars)
//
// Every "expected" is produced by the REFERENCE CLASSIFIER below: Handlebars.parse() builds the real AST and the
// walk collects the categories of design.md section 7.1 that the AST contains. Nothing is typed by hand. A template
// that Handlebars.js cannot parse is not a subset question: it is counted as "parse-error" and left out.
const fs = require('fs');
const path = require('path');
const Handlebars = require(process.env.HANDLEBARS_MODULE || path.resolve(__dirname, '../baseline/node_modules/handlebars'));
const dir = process.argv[2];

/** The reference classifier: the sorted category names that the Handlebars.js AST of the template contains. */
function classify(template) {
  const ast = Handlebars.parse(template);
  const cats = new Set();
  const walk = (n) => {
    if (Array.isArray(n)) { n.forEach(walk); return; }
    if (!n || typeof n !== 'object') { return; }
    switch (n.type) {
      case 'PartialStatement': case 'PartialBlockStatement': case 'Decorator': case 'DecoratorBlock':
        cats.add('partial');
        break;
      case 'PathExpression':
        if (n.depth > 0) { cats.add('parent path'); }
        if (n.parts.indexOf('null') >= 0) { cats.add('path segment named null'); }
        if (n.data && (n.parts[0] === 'odd' || n.parts[0] === 'even')) { cats.add('data variable @' + n.parts[0]); }
        break;
      case 'BlockStatement':
        if (n.path && n.path.type === 'PathExpression' && (n.path.original === 'if' || n.path.original === 'unless')
          && n.params.length === 0) {
          cats.add(n.path.original + ' without argument');
        }
        break;
      default:
    }
    if (n.type === 'Program' && n.blockParams && n.blockParams.length > 0) { cats.add('block parameters'); }
    Object.keys(n).forEach((k) => { if (k !== 'loc') { walk(n[k]); } });
  };
  walk(ast);
  return Array.from(cats).sort();
}

const rows = new Map();
let parseErrors = 0;
const tried = new Set();
const add = (template) => {
  if (tried.has(template)) { return; }
  tried.add(template);
  try { rows.set(template, classify(template)); } catch (e) { parseErrors++; }
};

// ---- building blocks -------------------------------------------------------------------------------------------
// Expressions that are legal as the body of a plain mustache.
const PATHS = ['n', '../n', '../../n', './n', 'this/../n', 'a/../b', 'this.n', 'this', '.', '..', '@odd', '@even',
  '@index', '@key', '@first', '@root.n', '@root.null', '@root.odd', '@root.[null]', '@../odd', '@ odd', '@oddx', '@odd.x',
  '@this.odd', '@[odd]', 'x.null', 'x.[null]', '[null]', 'this.null', './null', 'a.null.b', 'x.[a.null]', 'x.[ null ]',
  'x.[null].y', 'null', 'null.x', 'nullx', 'x.nullx', 'x.Null', 'true', 'false', 'undefined', '1', '-1.5', '"s"', "'s'",
  '[this].x', 'x.[..]', '[..]/x', '[null]/x', '[../n]', '[@odd]', 'x.[this]', 'a .null', 'a . null', 'a. null', 'a .b',
  '.null', '..null', '../[null]', '../x.null', '@root/null', 'a/null', 'a\\.b', '[a\\]b]', 'x.[a\nb]'];
const ARGS = ['a', '../a', 'a ../b', '@odd', '@even', 'null', 'a null', '"x"', "'x'", '"../x"', "'../x'", '"{{../x}}"',
  '"@odd"', '"as |x|"', '"x.null"', '"}}"', '"{{"', '"\\""', "'\\''", '"a\\" ../c', '"a\\\\" ../c', '"a\\\\\\" ../c',
  "'a\\\\' ../c", '(g a)', '(g ../a)', '(g "x y")', '(g (h a) b)', '(g (h ../a) b)', '(g null)', '(g @odd)', '(g a.null)',
  '(g a=b)', '(g a=../b)', '( g a )', '(g\na\n)', 'k=v', 'k=../v', 'k=@odd', 'k=null', 'k=x.null', 'k = v', 'k =../v',
  'k= @even', 'null=1', 'true=1', 'if=1', 'k=(g a)', 'k=(g ../a)', 'k=(g "x y")', 'k = ( g a b )', 'k=(g (h "x y") z)',
  'includeZero=true', 'includeZero = true', 'includeZero=(eq a b)', 'includeZero = (eq a b)', 'k=(h "x y")',
  'k=(h "x y") j=(i ../z)', 'a includeZero=true', 'a includeZero=(eq a b)', '(eq a b)', '(eq a b) includeZero=(eq a b)',
  '(eq ../a b)', '(or a b) k=(h "x y")', 'a b k=(g (h "}}") ../c)', '"x" k="../y"', 'a "k=v" ../b', 'a "k=v"',
  '"k=" ../b', '"k=v"', 'a k="as |x|"', 'k="{{../n}}"', 'k=\'@odd\'', 'a=b c'];
const WRAPS = [
  (e) => '{{' + e + '}}', (e) => '{{ ' + e + ' }}', (e) => '{{~' + e + '~}}', (e) => '{{~ ' + e + ' ~}}',
  (e) => '{{{' + e + '}}}', (e) => '{{{ ' + e + ' }}}', (e) => '{{~{' + e + '}~}}', (e) => '{{&' + e + '}}',
  (e) => '{{~&' + e + '}}', (e) => '{{\n' + e + '\n}}', (e) => '{{&\t' + e + '\t~}}'];

// 1. every path under a few wrappers, bare and as an argument of a helper call
PATHS.forEach((p) => {
  WRAPS.forEach((w) => { add(w(p)); });
  add('{{h ' + p + '}}');
  add('{{h a ' + p + ' b}}');
  add('{{h k=' + p + '}}');
  add('{{h (g ' + p + ')}}');
  add('{{#h ' + p + '}}x{{/h}}');
  add('{{#if ' + p + '}}x{{/if}}');
  add('{{#if a}}x{{else if ' + p + '}}y{{/if}}');
  add('{{#each xs}}{{' + p + '}}{{/each}}');
  add('{{> p ' + p + '}}');
});

// 2. every argument shape on helper calls, plain blocks and the if / unless family
ARGS.forEach((a) => {
  WRAPS.slice(0, 2).forEach((w) => { add(w('h ' + a)); });
  add('{{#h ' + a + '}}x{{/h}}');
  ['if', 'unless'].forEach((name) => {
    ['#' + name, '# ' + name, '~#' + name, '^' + name].forEach((open) => {
      add('{{' + open + ' ' + a + '}}x{{/' + name + '}}');
    });
    add('{{#' + name + '\n' + a + '\n~}}x{{/' + name + '}}');
    add('{{#' + name + ' z}}x{{else ' + name + ' ' + a + '}}y{{/' + name + '}}');
    add('{{#' + name + ' z}}x{{else ' + name + ' ' + a + '}}y{{else}}w{{/' + name + '}}');
    add('{{#each xs}}{{#' + name + ' ' + a + '}}x{{else}}y{{/' + name + '}}{{/each}}');
    add('{{#' + name + ' ' + a + '}}x{{^}}y{{/' + name + '}}');
    add('{{{{' + name + ' ' + a + '}}}}raw{{{{/' + name + '}}}}');
    add('{{#*' + name + ' ' + a + '}}x{{/' + name + '}}');
    add('{{* ' + name + ' ' + a + '}}');
  });
  add('{{> p ' + a + '}}');
  add('{{#> p ' + a + '}}x{{/p}}');
  add('{{#*inline "p" ' + a + '}}x{{/inline}}');
  add('{{#each ' + a + '}}x{{/each}}');
  add('{{#each xs as |v|}}{{h ' + a + '}}{{/each}}');
});

// 3. the if / unless family: spellings of the opener, the closer, the else and the inverse
const NAMES = ['if', 'unless', 'each', 'with', 'ifEquals', 'foo', '[if]', '[unless]', 'this.if', './if', 'if.x', 'iff', 'ifs',
  'If', '@if', '../if', 'this/if', 'unlessx', '[if].x'];
const OPENERS = ['#', '# ', '~#', '^'];
NAMES.forEach((name) => {
  OPENERS.forEach((open) => {
    ['', ' a', ' k=v', ' (g a)'].forEach((args) => {
      add('{{' + open + name + args + '}}x{{/' + name + '}}');
      add('{{' + open + name + args + '}}x{{else}}y{{/' + name + '}}');
      add('{{' + open + name + args + '~}}x{{~^~}}y{{~/' + name + '}}');
    });
  });
  ['else', '~else', ' else', '\nelse\n', '^else'].forEach((el) => {
    ['', ' a', ' k=v', ' (g a)'].forEach((args) => {
      add('{{#' + name + ' z}}x{{' + el + ' ' + name + args + '}}y{{/' + name + '}}');
      add('{{#if z}}x{{' + el + ' ' + name + args + '}}y{{else}}w{{/if}}');
      add('{{#unless z}}x{{' + el + ' ' + name + args + '}}y{{/unless}}');
    });
  });
  add('{{#if z}}x{{elseif ' + name + '}}y{{/if}}');
  add('{{#if z}}x{{elseif}}y{{/if}}');
  add('{{#if z}}x{{else' + name + '}}y{{/if}}');
  add('{{elseif}}{{else}}{{else ' + name + '}}');
});
['{{#if a}}x{{else}}y{{/if}}', '{{#if a}}x{{^}}y{{/if}}', '{{#if a}}x{{else if b}}y{{else if c}}z{{else}}w{{/if}}',
  '{{#if a}}x{{else if}}y{{else if}}z{{/if}}', '{{#if a}}x{{else unless}}y{{else if b}}z{{/if}}',
  '{{#if a}}{{#if}}x{{/if}}{{/if}}', '{{#if a}}{{#unless}}x{{/unless}}{{/if}}', '{{^if}}x{{else}}y{{/if}}',
  '{{^if a}}x{{else if}}y{{/if}}', '{{^unless}}x{{else unless a}}y{{/unless}}', '{{#if}}{{else}}{{/if}}',
  '{{#if}}{{^}}{{/if}}', '{{#if ../a}}{{else if ../b}}{{else if}}{{/if}}', '{{~#if~}}x{{~else if~}}y{{~/if~}}',
  '{{~#if a~}}x{{~else if~}}y{{~/if~}}', '{{#if (if)}}x{{/if}}', '{{#if (if a)}}x{{/if}}', '{{if}}', '{{if a}}',
  '{{(if)}}', '{{#each xs}}{{#if @first}}a{{else}}{{#if}}b{{/if}}{{/if}}{{/each}}',
  '{{#if a}}x{{else}}{{#if}}y{{/if}}{{/if}}', '{{#if a}}x{{else}}{{#unless}}y{{/unless}}{{/if}}'].forEach(add);

// 4. block parameters
['#each xs as |x|', '#each xs as |x y|', '#each xs as  |x|', '#each xs as\n|x|', '#each xs as |x| ', '#each xs as |x|\n',
  '#each xs as|x|', '#each xs as | x |', '#each xs as |x y z|', '#each xs as ||', '#each xs k=v as |x|', '#each xs k=(g a) as |x|',
  '#each (g a) as |x|', '#each xs as', '#each xs "as |x|"', '#each "as |x|"', '#each xs k="as |x|"', "#each xs 'as |x|'",
  '#each as', '#each as |x|', '#each xs as |as|', '#with a as |b|', '#if a as |b|', '#unless a as |b|', '#if as |b|',
  '#foo as |b|', '#each ../xs as |x|', '#each xs as |x|~', '^each xs as |x|', '^if a as |b|', '#each xs as |x.y|',
  '#each xs as |@x|', '#each xs as |[x]|', '#each xs as |null|', '#each xs as |true|'].forEach((open) => {
  const name = open.replace(/^[#^]/, '').split(/\s/)[0];
  add('{{' + open + '}}{{x}}{{/' + name + '}}');
  add('{{~' + open + '~}}{{x}}{{~/' + name + '~}}');
  add('{{' + open + '}}{{x}}{{else}}{{y}}{{/' + name + '}}');
});
['{{#each xs as |x|}}a{{else each ys as |y|}}b{{/each}}', '{{#each xs}}a{{else each ys as |y|}}b{{/each}}',
  '{{#if a}}a{{else if b as |y|}}b{{/if}}', '{{#each xs}}a{{else with ys as |y|}}b{{else}}c{{/each}}',
  '{{^each xs as |x|}}a{{else}}b{{/each}}', '{{^each xs as |x|}}a{{/each}}', '{{#each xs as |x|}}{{#each x as |y|}}{{/each}}{{/each}}',
  '{{foo as |x|}}', '{{foo "as |x|"}}', '{{foo bar as |x|}}', '{{> p as |x|}}', '{{#> p as |x|}}x{{/p}}',
  '{{#*inline "p" as |x|}}x{{/inline}}', '{{{{raw as |x|}}}}x{{{{/raw}}}}', '{{{foo as |x|}}}', '{{#each xs}}as |x|{{/each}}',
  'as |x| {{x}}', '{{!-- {{#each xs as |x|}} --}}', '\\{{#each xs as |x|}}{{/each}}', '{{#each xs as |x|}}\\{{../n}}{{/each}}'].forEach(add);

// 5. partials and decorators
['> p', '>p', '>  p', '>\np', '~> p', '~>p', '> p ~', '> "p"', "> 'p'", '> [p]', '> p.q', '> p/q', '> ../p', '> @odd', '> (g a)',
  '> (lookup . "x")', '> (lookup ../a "x")', '> p a', '> p a=b', '> p a b=c', '> p ../a', '> p a=../b', '> p "x"', '> p null',
  '> p @even', '> p a.null', '> "p" "q"', '> null', '> 1', '> p k="{{> q}}"', '> p "as |x|"', '> p (g a)'].forEach((b) => {
  add('{{' + b + '}}');
  add('{{' + b + '~}}');
  add('a{{' + b + '}}b');
  add('{{#' + b.replace(/^~?/, '') + '}}x{{/p}}');
  add('{{#each xs}}{{' + b + '}}{{/each}}');
});
['{{#> p}}x{{/p}}', '{{#> p a}}x{{/p}}', '{{#> p}}{{> q}}{{/p}}', '{{#> p}}x{{else}}y{{/p}}', '{{~#> p~}}x{{~/p~}}',
  '{{#>p}}x{{/p}}', '{{#> p}}{{#> q}}x{{/q}}{{/p}}', '{{#> [p]}}x{{/p}}', '{{#> p.q}}x{{/p.q}}',
  '{{#*inline "p"}}x{{/inline}}', '{{#*inline "p"}}{{../n}}{{/inline}}', '{{#* inline "p"}}x{{/inline}}',
  '{{#*inline p}}x{{/inline}}', '{{~#*inline "p"~}}x{{~/inline~}}', '{{#*inline "p"}}{{#*inline "q"}}x{{/inline}}{{/inline}}',
  '{{*inline "p"}}', '{{* inline "p"}}', '{{*foo}}', '{{~*foo~}}', '{{#*foo a}}x{{/foo}}', '{{#* foo}}x{{/foo}}',
  '{{{*foo}}}', '{{&*foo}}', '{{#*foo}}{{> p}}{{/foo}}', '{{#each xs}}{{#*inline "p"}}x{{/inline}}{{/each}}',
  '{{> p}}{{> q}}', '{{#> p}}{{/p}}', '{{#> p}}\n{{/p}}', '{{#*inline "p"}}{{/inline}}', 'x{{>p}}y{{>q}}z',
  '{{!-- {{> p}} --}}', '\\{{> p}}', '\\\\{{> p}}', '{{{{raw}}}}{{> p}}{{{{/raw}}}}', '{{foo ">"}}', '{{foo ">" p}}',
  '{{foo "#*inline"}}', '{{foo ">p" "#> q"}}'].forEach(add);

// 6. escapes, comments and raw blocks, with the constructs each one may contain
const CONSTRUCTS = ['{{../n}}', '{{@odd}}', '{{@even}}', '{{#if}}x{{/if}}', '{{#unless}}x{{/unless}}', '{{> p}}',
  '{{#each xs as |x|}}x{{/each}}', '{{x.null}}', '{{x.[null]}}', '{{#*inline "p"}}x{{/inline}}', '{{#if a}}x{{else if}}y{{/if}}',
  '{{{../n}}}', '{{&../n}}', '{{h (g ../a)}}', '{{h k=(g @odd)}}', '{{n}}', '{{#if a}}x{{/if}}'];
const ESC = ['\\', '\\\\', '\\\\\\', '\\\\\\\\'];
CONSTRUCTS.forEach((c) => {
  add(c);
  ESC.forEach((e) => {
    add(e + c);
    add('a' + e + c);
    add(c + e + c);
    add(e + c + e + c);
    add(e + c + '{{../m}}');
    add(e + c + ' \\' + c);
  });
  ['{{!', '{{~!', '{{!--', '{{~!--'].forEach((o) => {
    ['}}', '--}}', '--~}}', '~}}'].forEach((cl) => {
      add(o + ' ' + c + ' ' + cl);
      add(o + ' ' + c + ' ' + cl + c);
      add(o + ' ' + cl + c);
    });
  });
  add('{{!-- ' + c + ' --}} {{!-- ' + c + ' --}}');
  add('{{! a }} ' + c + ' }}');
  add('{{! a }}' + c + '{{! b }}');
  add('{{!-- -- }} ' + c + ' --}}');
  add('{{!-- }} ' + c + ' --}}');
  add('{{!-- {{!-- ' + c + ' --}}');
  add('{{!-- --}}' + c + '{{!-- --}}');
  add('{{! {{!-- }}' + c + '--}}');
  add('{{{{raw}}}}' + c + '{{{{/raw}}}}');
  add('{{{{raw}}}} ' + c + ' ' + c + ' {{{{/raw}}}}' + c);
  add('{{{{raw}}}}{{{{inner}}}}' + c + '{{{{/inner}}}}{{{{/raw}}}}');
  add('{{{{raw}}}}{{{{inner}}}}{{{{/inner}}}}' + c + '{{{{/raw}}}}');
  add('{{{{raw}}}}\\' + c + '{{{{/raw}}}}');
  add('{{{{raw}}}}{{!-- ' + c + '{{{{/raw}}}}');
  add('{{{{raw}}}}{{{{/raw}}}}' + c);
  add('{{{{raw ' + c.replace(/[{}]/g, '') + '}}}}x{{{{/raw}}}}');
  add('{{{{raw}}}}x{{{{/raw}}}}{{{{raw}}}}' + c + '{{{{/raw}}}}');
  add('{{foo "' + c + '"}}');
  add("{{foo '" + c + "'}}");
  add('{{foo "' + c + '" ../x}}');
  add('{{foo k="' + c + '"}}');
  add('{{foo (g "' + c + '")}}');
  add('{{foo "}}" ../x}}');
  add('{{foo "}}" ' + c + '}}');
});
['{{{{raw}}}}x{{{{/raw}}}}', '{{{{raw a=b}}}}x{{{{/raw}}}}', '{{{{raw a}}}}x{{{{/raw}}}}', '{{{{raw ../a}}}}x{{{{/raw}}}}',
  '{{{{if}}}}x{{{{/if}}}}', '{{{{if a}}}}x{{{{/if}}}}', '{{{{unless}}}}x{{{{/unless}}}}', '{{{{if k=v}}}}x{{{{/if}}}}',
  '{{{{ if }}}}x{{{{/if}}}}', '{{{{if}}}}{{{{if}}}}x{{{{/if}}}}{{{{/if}}}}', '{{{{if}}}}{{#if}}x{{/if}}{{{{/if}}}}',
  '{{{{raw}}}}{{{{/raw}}}}', '{{{{raw}}}}{{{{raw}}}}{{{{/raw}}}}{{{{/raw}}}}', '{{{{raw}}}}x{{{{/raw }}}}', '{{{{raw}}}}x{{{{/other}}}}',
  '{{{{raw}}}}x', '{{{{raw}}}}x{{{{/raw}}', '{{#if}}{{{{raw}}}}{{#if}}{{{{/raw}}}}{{/if}}', '{{{{raw.x}}}}x{{{{/raw.x}}}}',
  '{{{{raw.null}}}}x{{{{/raw.null}}}}', '{{{{@odd}}}}x{{{{/@odd}}}}', '{{{{raw "}}}}"}}}}x{{{{/raw}}}}',
  '{{{{raw}}}}\\{{{{/raw}}}}', '\\{{{{raw}}}}{{../n}}{{{{/raw}}}}', '\\\\{{{{raw}}}}{{../n}}{{{{/raw}}}}'].forEach(add);

// 7. brackets, literals and odd spellings of the lexer
['{{[null]}}', '{{[ null ]}}', '{{[nu]}}', '{{["null"]}}', "{{['null']}}", '{{x.[null]}}', '{{x.[a.null]}}', '{{x.[null].[null]}}',
  '{{[a b].null}}', '{{[a\\]b].null}}', '{{[a\\\\].null}}', '{{[../x]}}', '{{[..]}}', '{{[.]}}', '{{[this]}}', '{{[this].null}}',
  '{{this.[null]}}', '{{this/null}}', '{{./null}}', '{{../null}}', '{{@root.[null]}}', '{{@root/null}}', '{{null}}', '{{ null }}',
  '{{null null}}', '{{h null}}', '{{h null null}}', '{{h k=null}}', '{{h null=null}}', '{{null=1}}', '{{h null.x}}',
  '{{h x.null y}}', '{{#if null}}x{{/if}}', '{{#ifEquals a null}}x{{/ifEquals}}', '{{#ifCond a "===" null}}x{{/ifCond}}',
  '{{#if a}}x{{else if null}}y{{/if}}', '{{h undefined}}', '{{h true false}}', '{{h 1 2.5 -3}}', '{{h nullable}}',
  '{{h null-x}}', '{{h null_x}}', '{{h x.null-x}}', '{{h x-null}}', '{{h @null}}', '{{@null}}', '{{@root.nullx}}',
  '{{h this.null}}', '{{this.null.x}}', '{{x.null.y.z}}', '{{h (g x.null)}}', '{{h k=(g null)}}', '{{h k=(g x.[null])}}',
  '{{h "a" x.null}}', '{{h "x.null"}}', "{{h 'x.null'}}", '{{h "[null]"}}', '{{h [null]}}', '{{h [nul]}}', '{{h .null}}',
  '{{h a.[b c].null}}', '{{h a.[b\nc]}}', '{{h [a\nb].null}}', '{{h ../[null]}}', '{{@odd}}{{@even}}', '{{@odd.x}}', '{{@even.x.y}}',
  '{{h @odd @even}}', '{{h k=@odd j=@even}}', '{{#each xs}}{{#if @odd}}a{{/if}}{{/each}}', '{{#each xs}}{{#if @even}}a{{/if}}{{/each}}',
  '{{#if a}}{{@odd}}{{else}}{{@even}}{{/if}}', '{{@first}}{{@last}}{{@index}}{{@key}}{{@root}}{{@root.odd}}', '{{@root.n}}{{@oddx}}{{@evenx}}',
  '{{@ odd}}', '{{@\nodd}}', '{{@odd }}', '{{@Odd}}', '{{@odd1}}', '{{@odd-x}}', '{{@odd_x}}', '{{@[odd]}}', '{{@root.[odd]}}',
  '{{@this.even}}', '{{@./even}}', '{{@../even}}', '{{@../../odd}}', '{{../@odd}}', '{{h "@odd" \'@even\'}}', '{{n}}{{../n}}{{n}}',
  '{{../n}}{{../n}}', '{{#each a}}{{#each b}}{{../../n}}{{../n}}{{/each}}{{/each}}', '{{..}}', '{{h ..}}', '{{../}}', '{{.. /n}}',
  '{{.. n}}', '{{h .. n}}', '{{h ../ n}}', '{{h a. ./n}}', '{{h ../.}}', '{{h ../..}}', '{{h ../this}}', '{{h ../this.x}}',
  '{{h a ..b}}', '{{ ../n }}', '{{~../n~}}', '{{{../n}}}', '{{&../n}}', '{{~{../n}~}}', '{{\n../n\n}}', '{{h\n../n}}',
  '{{h\t../n}}', '{{h\r\n../n}}', '{{h\u00a0../n}}', '{{h\ufeff../n}}', '{{h\u2028../n}}', '{{h\u3000../n}}', '{{h\u2003null.x}}'].forEach(add);

// 8. text around the mustaches: the lexer reads the text between them by its own rules
const TEXT = ['', 'a', '\\', '\\\\', '{', '}', '{{{', '}}', '\\{', 'a\\\\\\', '<a href="../up">', '{ {../n}}', '{{ ../n'];
['{{../n}}', '{{@odd}}', '{{#if}}x{{/if}}', '{{> p}}', '{{x.null}}', '{{#each xs as |x|}}x{{/each}}'].forEach((c) => {
  TEXT.forEach((a) => {
    TEXT.forEach((b) => {
      add(a + c + b);
    });
  });
});

// 9. combinations: seeded fragments glued at random; only templates that Handlebars.js parses are kept
let seed = 20261123;
const rnd = () => { seed = (seed + 0x6D2B79F5) | 0; let t = Math.imul(seed ^ (seed >>> 15), 1 | seed);
  t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t; return ((t ^ (t >>> 14)) >>> 0) / 4294967296; };
const pick = (l) => l[Math.floor(rnd() * l.length)];
const FRAGMENTS = ['{{n}}', '{{../n}}', '{{@odd}}', '{{@even}}', '{{@index}}', '{{x.null}}', '{{x.[null]}}', '{{#if a}}', '{{#if}}',
  '{{#unless}}', '{{#unless a}}', '{{/if}}', '{{/unless}}', '{{else}}', '{{else if}}', '{{else if a}}', '{{^}}', '{{#each xs}}',
  '{{#each xs as |x|}}', '{{/each}}', '{{> p}}', '{{#> p}}', '{{/p}}', '{{!-- {{../n}} --}}', '{{! ../n }}', '\\{{../n}}',
  '\\\\{{../n}}', '\\\\\\{{../n}}', '{{{{raw}}}}', '{{{{/raw}}}}', '{{h (g ../a) k=(i "x y")}}', '{{h "{{../a}}"}}', 'text', ' ', '\n',
  '\\', '{{{../n}}}', '{{&@odd}}', '{{~#if~}}', '{{~/if~}}', '{{h k=null}}', '{{h null}}', '{{#*inline "p"}}', '{{/inline}}',
  '{{#with o}}', '{{/with}}', '{{#h a}}', '{{/h}}', '{{#if a includeZero=(eq a b)}}', '{{^if}}', '{{^if a}}', '{{else unless}}',
  '{{#unless a}}', '{{../../n}}', '{{@root.null}}', '{{this.null}}', '{{this.n}}'];
let attempts = 0;
const randomFrom = rows.size;
while (rows.size < randomFrom + 600 && attempts < 400000) {
  attempts++;
  const n = 1 + Math.floor(rnd() * 6);
  const parts = [];
  for (let i = 0; i < n; i++) { parts.push(pick(FRAGMENTS)); }
  const t = parts.join(rnd() < 0.3 ? ' ' : '');
  if (tried.has(t)) { continue; }
  tried.add(t);
  try { rows.set(t, classify(t)); } catch (e) { parseErrors++; }
}

// 10. the production templates: read from the test resources, referenced by path (they are 60 KB each)
const out = [];
const counts = {};
rows.forEach((expected, template) => {
  expected.forEach((c) => { counts[c] = (counts[c] || 0) + 1; });
  out.push('{"template":' + JSON.stringify(template) + ',"expected":' + JSON.stringify(expected) + '}');
});
const production = [];
['oicr', 'innovation', 'project'].forEach((name) => {
  const file = path.join(dir, 'templates', name + '.html');
  const expected = classify(fs.readFileSync(file, 'utf8'));
  if (expected.length !== 0) { throw new Error(name + '.html is outside the subset: ' + expected); }
  production.push('{"templateResource":"/pdf/templates/' + name + '.html","expected":[]}');
});
const empty = Array.from(rows.values()).filter((e) => e.length === 0).length;
const gen = 'node subset-generate.js <repo>/marlo-web/src/test/resources/pdf (script kept with the spec evidence; handlebars '
  + Handlebars.VERSION + '; "expected" = sorted categories of design.md 7.1 found in the Handlebars.parse() AST by the reference'
  + ' classifier in the script; templates that Handlebars.js cannot parse are excluded: ' + parseErrors + ' parse-error templates)';
fs.writeFileSync(path.join(dir, 'subset-vectors.json'),
  '{\n"_generator":' + JSON.stringify(gen) + ',\n"rows":[\n' + production.concat(out).join(',\n') + '\n]}\n');
console.log('rows', out.length + production.length, 'empty', empty, 'parse-error', parseErrors, JSON.stringify(counts));
