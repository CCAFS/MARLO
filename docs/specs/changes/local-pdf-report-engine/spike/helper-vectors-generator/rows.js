// Vector rows: only {template, context} are authored here; "expected" is produced by Handlebars.js.
const R = [];
const add = (template, context = {}) => R.push({ template, context });
const big = 1e21;
const NAN = { e: '(mathOperator 0 "/" 0)' };
const INF = { e: '(mathOperator 1 "/" 0)' };
const NINF = { e: '(mathOperator -1 "/" 0)' };
const ref = (v, name, ctx) => { if (v && v.e) { return v.e; } if (v !== undefined) { ctx[name] = v; } return name; };
// safeEmpty
add('{{#safeEmpty}}{{x}}{{/safeEmpty}}', {});
add('{{#safeEmpty}}{{x}}{{/safeEmpty}}', { x: '' });
add('{{#safeEmpty}}{{x}}{{/safeEmpty}}', { x: 'abc' });
add('{{#safeEmpty}}{{x}}{{/safeEmpty}}', { x: null });
add('{{#safeEmpty}}{{x}}{{/safeEmpty}}', { x: 'a&b' });
add('{{#safeEmpty}} {{/safeEmpty}}', {});
add('{{#safeEmpty}}0{{/safeEmpty}}', {});
// percentage
add('{{#percentage}}0.5{{/percentage}}');
add('{{#percentage}}{{x}}{{/percentage}}', { x: 0.07 });
add('{{#percentage}}{{x}}{{/percentage}}', { x: 1 });
add('{{#percentage}}{{x}}{{/percentage}}', { x: '' });
add('{{#percentage}}{{x}}{{/percentage}}', { x: 'abc' });
add('{{#percentage}}{{x}}{{/percentage}}', { x: 0.1 });
add('{{#percentage}}{{x}}{{/percentage}}', { x: 12.5 });
add('{{#percentage}} 0.25 {{/percentage}}');
// mathOperator
add('{{mathOperator 1 "+" 1}}');
add('{{mathOperator 1 "/" 2}}');
add('{{mathOperator 1 "/" 0}}');
add('{{mathOperator -1 "/" 0}}');
add('{{mathOperator 0 "/" 0}}');
add('{{mathOperator "12px" "+" 1}}');
add('{{mathOperator "abc" "+" 1}}');
add('{{mathOperator 3 "%" 2}}');
add('{{mathOperator 3 "*" 4}}');
add('{{mathOperator 3 "-" 4.5}}');
add('{{mathOperator a "+" b}}', { a: '1.5', b: '2.5' });
add('{{mathOperator a "+" 1}}', {});
add('{{mathOperator a "*" 100}}', { a: 0.07 });
add('{{mathOperator a "+" b}}', { a: 0.1, b: 0.2 });
add('{{mathOperator a "*" b}}', { a: 1e10, b: 1e12 });
add('{{mathOperator a "/" b}}', { a: 1, b: 3 });
add('{{mathOperator a "+" 1}}', { a: ' 7 ' });
add('{{mathOperator a "+" 1}}', { a: '.5e1' });
add('{{mathOperator a "+" 1}}', { a: '1e' });
add('{{mathOperator a "+" 1}}', { a: '-Infinityx' });
add('{{mathOperator a "+" 1}}', { a: null });
add('{{mathOperator a "+" 1}}', { a: [] });
add('{{mathOperator a "+" 1}}', { a: [3, 4] });
add('{{mathOperator a "*" b}}', { a: 1e-7, b: 1 });
add('{{mathOperator a "*" b}}', { a: 123456789012345680000, b: 10 });
add('{{typeof (mathOperator 3 "%" 2)}}');
add('{{typeof (mathOperator 1 "+" 1)}}');
add('{{#ifCond (mathOperator 1 "+" 1) "===" 2}}T{{else}}F{{/ifCond}}');
// stringContentCompare
add('{{#stringContentCompare "abc" "ABC"}}T{{else}}F{{/stringContentCompare}}');
add('{{#stringContentCompare "abc" "abd"}}T{{else}}F{{/stringContentCompare}}');
add('{{#stringContentCompare "café" "CAFE"}}T{{else}}F{{/stringContentCompare}}');
add('{{#stringContentCompare "Árbol" "arbol"}}T{{else}}F{{/stringContentCompare}}');
add('{{#stringContentCompare "ñandú" "NANDU"}}T{{else}}F{{/stringContentCompare}}');
add('{{#stringContentCompare "" ""}}T{{else}}F{{/stringContentCompare}}');
add('{{#stringContentCompare "a" ""}}T{{else}}F{{/stringContentCompare}}');
add('{{#stringContentCompare a b}}T{{else}}F{{/stringContentCompare}}', { a: 'Straße', b: 'STRASSE' });
add('{{#stringContentCompare a b}}T{{else}}F{{/stringContentCompare}}', { a: 'é', b: 'E' });
add('{{#stringContentCompare a b}}T{{else}}F{{/stringContentCompare}}', { a: 'north  east', b: 'north east' });
add('{{#stringContentCompare 1 "1"}}T{{else}}F{{/stringContentCompare}}');
add('{{#stringContentCompare a "x"}}T{{else}}F{{/stringContentCompare}}', {});
add('{{#stringContentCompare a "x"}}T{{else}}F{{/stringContentCompare}}', { a: null });
add('{{#stringContentCompare "x" 2}}T{{else}}F{{/stringContentCompare}}');
add('{{#stringContentCompare "x"}}T{{else}}F{{/stringContentCompare}}');
add('{{#stringContentCompare "x" "x" "x"}}T{{else}}F{{/stringContentCompare}}');
add('{{#stringContentCompare a "x"}}T{{else}}F{{/stringContentCompare}}', { a: ['x'] });
// inList
add('{{inList "b" "a,b,c"}}');
add('{{inList "d" "a,b,c"}}');
add('{{inList 2 "1,2,3"}}');
add('{{inList 2.0 "1,2,3"}}');
add('{{inList 1.5 "1.5,2"}}');
add('{{inList true "true,false"}}');
add('{{inList "a" "a, b"}}');
add('{{inList " b" "a, b"}}');
add('{{inList "" ""}}');
add('{{inList "x" ""}}');
add('{{inList "x" missing}}', {});
add('{{inList "x" n}}', { n: null });
add('{{inList "" missing}}', {});
add('{{inList missing "a"}}', {});
add('{{inList n "a"}}', { n: null });
add('{{inList "a" 5}}');
add('{{inList "1,2" l}}', { l: ['1,2'] });
add('{{inList l "1,2"}}', { l: [1, 2] });
add('{{inList "a,b" "a,b"}}');
add('{{inList a "x,1e+21"}}', { a: big });
// yesNoHelper
add('{{yesNoHelper x}}', { x: true });
add('{{yesNoHelper x}}', { x: false });
add('{{yesNoHelper x}}', { x: 0 });
add('{{yesNoHelper x}}', { x: 1 });
add('{{yesNoHelper x}}', { x: '' });
add('{{yesNoHelper x}}', { x: 'no' });
add('{{yesNoHelper x}}', { x: [] });
add('{{yesNoHelper x}}', { x: {} });
add('{{yesNoHelper x}}', { x: 0.0 });
add('{{yesNoHelper x}}', { x: null });
add('{{yesNoHelper x}}', {});
add('{{yesNoHelper x "N/A"}}', { x: null });
add('{{yesNoHelper x "N/A"}}', {});
add('{{yesNoHelper x "N/A"}}', { x: true });
add('{{yesNoHelper x "N/A"}}', { x: 0 });
add('{{yesNoHelper x "N/A"}}', { x: '' });
add('{{yesNoHelper x "N/A"}}', { x: [] });
add('{{yesNoHelper x "N/A" "extra"}}', { x: null });
// conditionalAttribiute
add('<td {{conditionalAttribiute a b "class" "on" "off"}}>', { a: 1, b: 1 });
add('<td {{conditionalAttribiute a b "class" "on" "off"}}>', { a: 1, b: 2 });
add('<td {{conditionalAttribiute a b "class" "on" "off"}}>', { a: 1, b: '1' });
add('<td {{conditionalAttribiute a b "class" "on" "off"}}>', { a: 0, b: '' });
add('<td {{conditionalAttribiute a b "class" "on" "off"}}>', {});
add('<td {{conditionalAttribiute a b "style" "color: red" "x"}}>', { a: 'x', b: 'x' });
add('<td {{conditionalAttribiute a b "title" t "x&y"}}>', { a: 'x', b: 'y', t: 'z' });
add('<td {{conditionalAttribiute a b "title" t f}}>', { a: 'x', b: 'x', t: 'say "hi" & <b>', f: 'f' });
add('<td {{conditionalAttribiute a b "title" t f}}>', { a: 'x', b: 'x', t: "it's `x` =", f: 'f' });
add('<td {{conditionalAttribiute a b "w" t f}}>', { a: 'x', b: 'x', t: 1.0, f: 'f' });
add('<td {{conditionalAttribiute a b "w" t f}}>', { a: 'x', b: 'x', t: null, f: 'f' });
add('<td {{conditionalAttribiute a b "w" t f}}>', { a: 'x', b: 'y', t: 1 });
add('<td {{conditionalAttribiute a b "w" t f}}>', { a: 'x', b: 'y', t: 1, f: [1, [2, 3]] });
add('<td {{conditionalAttribiute a b "w" t f}}>', { a: 'x', b: 'y', t: 1, f: {} });
add('<td {{conditionalAttribiute a b "w" "t"}}>', { a: 'x', b: 'y' });
add('<td {{conditionalAttribiute a b "w"}}>', { a: 'x', b: 'y' });
// safeString
add('{{safeString "<b>x</b>"}}');
add('{{safeString x}}', { x: '<i>&</i>' });
add('{{safeString x}}', {});
add('{{safeString x}}', { x: null });
add('{{safeString x}}', { x: 1 });
add('{{safeString x}}', { x: 1.5 });
add('{{safeString x}}', { x: true });
add('{{safeString x}}', { x: [1, 2] });
add('{{safeString x}}', { x: {} });
// ifEquals / ifNotEquals
const EQ = [
  [1, 1], [1, '1'], [0, ''], [true, 'true'], [true, 1], [false, 0], [false, ''], [true, '1'], [[], ''], [[], 0], [[], false],
  [[1], 1], [[1, 2], '1,2'], [{}, '[object Object]'], [{}, {}], [[], []], ['a', 'a'], ['a', 'A'], ['1', 1.0], ['1.0', 1],
  ['1e0', 1], [' 1 ', 1], ['0x10', 16], ['abc', 0], [null, 0], [null, ''], [null, false], [null, null], [2, 2.0], [0, false],
  ['', false], [' ', 0], ['\t\n', 0], ['Infinity', INF], ['-Infinity', NINF], ['NaN', NAN], [NAN, NAN], ['1', '1.0'],
  [10, '10'], ['b', 'a'], [[null], ''], [[[]], ''], [[0], false], [[0], true], [{}, true], [{ a: 1 }, 'x'],
];
EQ.forEach(([a, b]) => {
  ['ifEquals', 'ifNotEquals'].forEach((h) => { const c = {}; const x = ref(a, 'a', c); const y = ref(b, 'b', c); add(`{{#${h} ${x} ${y}}}T{{else}}F{{/${h}}}`, c); });
});
add('{{#ifEquals a b}}T{{else}}F{{/ifEquals}}', {});
add('{{#ifEquals a b}}T{{else}}F{{/ifEquals}}', { a: null });
add('{{#ifEquals a b}}T{{else}}F{{/ifEquals}}', { b: null });
add('{{#ifEquals a 0}}T{{else}}F{{/ifEquals}}', {});
add('{{#ifEquals a ""}}T{{else}}F{{/ifEquals}}', { a: null });
add('{{#ifEquals a false}}T{{else}}F{{/ifEquals}}', {});
add('{{#ifEquals a 1}}T{{else}}F{{/ifEquals}}', { a: true });
add('{{#ifEquals a 1}}T{{else}}F{{/ifEquals}}', { a: 1.0 });
add('{{#ifEquals a 1}}{{a}}{{x}}{{else}}F{{/ifEquals}}', { a: 1, x: '<' });
add('{{#ifEquals a b}}T{{/ifEquals}}', { a: 1, b: 2 });
add('{{#ifNotEquals a b}}T{{/ifNotEquals}}', { a: 1, b: 2 });
// ifCond
const OPS = ['==', '===', '!=', '!==', '<', '<=', '>', '>=', '&&', '||'];
const PAIRS = [
  [1, 1], [1, '1'], [0, ''], [true, 'true'], [true, 1], [null, undefined], [undefined, undefined], [null, 0], [undefined, 0],
  ['a', 'b'], ['b', 'a'], ['a', 'a'], ['10', '9'], ['10', 9], [10, '9'], ['abc', 1], [1, 'abc'], [NAN, NAN], [NAN, 1],
  [1, 2], [2, 1], [1.0, 1], [[], ''], [[], 0], [{}, {}], [[], []], ['', 0], [false, 0], [true, 2], [null, null], [null, false],
  [0, 0], ['', ''], [[1], 1], [[2], [10]], ['Z', 'a'], ['é', 'f'], ['2', '10'], [' 2 ', 10], [INF, 1], [NINF, '-Infinity'], [INF, INF],
  [{}, 1], [[], true], [1, true], [false, false], [0, false], [1e21, 1e21], [0.1 + 0.2, 0.3], ['', null], [undefined, null],
];
PAIRS.forEach(([a, b]) => {
  OPS.forEach((op) => { const ctx = {}; const x = ref(a, 'a', ctx); const y = ref(b, 'b', ctx); add(`{{#ifCond ${x} "${op}" ${y}}}T{{else}}F{{/ifCond}}`, ctx); });
});
add('{{#ifCond a "<>" b}}T{{else}}F{{/ifCond}}', { a: 1, b: 1 });
add('{{#ifCond a "" b}}T{{else}}F{{/ifCond}}', { a: 1, b: 1 });
add('{{#ifCond a op b}}T{{else}}F{{/ifCond}}', { a: 1, b: 1, op: 5 });
add('{{#ifCond a op b}}T{{else}}F{{/ifCond}}', { a: 1, b: 1, op: ['=='] });
add('{{#ifCond a op b}}T{{else}}F{{/ifCond}}', { a: 1, b: 1, op: '==' });
add('{{#ifCond a "==" b}}T{{/ifCond}}', { a: 1, b: 2 });
add('{{#ifCond 1 "===" 1.0}}T{{else}}F{{/ifCond}}');
add('{{#ifCond 1 "===" 1.5}}T{{else}}F{{/ifCond}}');
add('{{#ifCond "1" "===" 1}}T{{else}}F{{/ifCond}}');
add('{{#ifCond (mathOperator 3 "-" 2) "===" 1}}T{{else}}F{{/ifCond}}');
add('{{#ifCond a "===" b}}T{{else}}F{{/ifCond}}', { a: 2, b: 2 });
add('{{#ifCond a "===" b}}T{{else}}F{{/ifCond}}', { a: 2, b: 2.0 });
add('{{#ifCond a "===" b}}T{{else}}F{{/ifCond}}', { a: 0.5, b: 0.5 });
add('{{#ifCond a "===" b}}T{{else}}F{{/ifCond}}', { a: 9007199254740993, b: 9007199254740992 });
add('{{#ifCond a "===" b}}T{{else}}F{{/ifCond}}', { a: 3000000000, b: 3000000000.0 });
// or / ifOr / ifAnd
const TV = [null, undefined, 0, NAN, INF, '', false, [], {}, 'a', 1, '0', 'false', -1, ' ', true, [0], [null]];
TV.forEach((v) => {
  [['{{or X}}'], ['{{#ifOr X}}T{{else}}F{{/ifOr}}'], ['{{#ifAnd X}}T{{else}}F{{/ifAnd}}']].forEach(([t]) => { const c = {}; add(t.replace('X', ref(v, 'v', c)), c); });
});
add('{{or a b}}', { a: 0, b: '' });
add('{{or a b}}', { a: 0, b: 'x' });
add('{{or a b c}}', { a: 0, b: '', c: [] });
add('{{or a b c}}', { a: 0, b: '', c: {} });
add('{{or a b}}', {});
add('{{or a b}}', { a: null, b: null });
add('{{or (mathOperator 0 "/" 0) b}}', { b: 0 });
add('{{or a b}}', { a: false, b: 'false' });
add('{{#ifOr a b}}T{{else}}F{{/ifOr}}', { a: 0, b: '' });
add('{{#ifOr a b}}T{{else}}F{{/ifOr}}', { a: 0, b: [] });
add('{{#ifOr a b c}}T{{else}}F{{/ifOr}}', { a: 0, b: '', c: 'x' });
add('{{#ifOr a b c}}T{{else}}F{{/ifOr}}', { a: 0, b: '' });
add('{{#ifOr a b (mathOperator 0 "/" 0)}}T{{else}}F{{/ifOr}}', { a: 0, b: '' });
add('{{#ifAnd a b}}T{{else}}F{{/ifAnd}}', { a: 1, b: 'x' });
add('{{#ifAnd a b}}T{{else}}F{{/ifAnd}}', { a: 1, b: '' });
add('{{#ifAnd a b}}T{{else}}F{{/ifAnd}}', { a: [], b: {} });
add('{{#ifAnd a b}}T{{else}}F{{/ifAnd}}', { a: [], b: 0 });
add('{{#ifAnd a b c}}T{{else}}F{{/ifAnd}}', { a: 1, b: 'x', c: null });
add('{{#ifAnd a b c}}T{{else}}F{{/ifAnd}}', { a: 1, b: 'x', c: [] });
add('{{#ifAnd a b}}T{{else}}F{{/ifAnd}}', {});
add('{{#ifAnd a b}}T{{/ifAnd}}', { a: 0, b: 1 });
add('{{#ifOr a b}}T{{/ifOr}}', { a: 0, b: 0 });
add('{{#ifCond a "&&" b}}T{{else}}F{{/ifCond}}', { a: [], b: {} });
add('{{#ifCond a "||" b}}T{{else}}F{{/ifCond}}', { a: '', b: 0 });
add('{{#if (or a b)}}T{{else}}F{{/if}}', { a: [], b: 0 });
add('{{#if (or a b)}}T{{else}}F{{/if}}', { a: 0, b: '' });
// typeof
[null, 0, 1.5, 'a', '', true, false, [], {}, [1], { a: 1 }].forEach((v) => add('{{typeof v}}', { v }));
add('{{typeof (mathOperator 0 "/" 0)}}');
add('{{typeof (mathOperator 1 "/" 0)}}');
add('{{typeof v}}', {});
add('{{typeof "a"}}');
add('{{typeof 1}}');
add('{{typeof 1.5}}');
add('{{typeof true}}');
add('{{typeof (parseJSON "[1]")}}');
add('{{typeof (parseJSON "null")}}');
add('{{typeof (parseJSON "5")}}');
add('{{typeof (typeof 1)}}');
add('{{typeof (or 1 2)}}');
add('{{typeof (safeString "x")}}');
add('{{typeof (json v)}}', { v: 1 });
add('{{typeof (json v)}}', {});
add('{{typeof (json v)}}', { v: null });
// json
[null, 0, 1, 1.0, 1.5, -0.5, 'a', '', 'say "hi"\n\t\\ / \u0001 é 😀  ', true, false, [], {}, [1, 2], [[]], [[], {}], [{}],
  { a: 1 }, { a: [1, { b: 2 }], c: null, d: 'x' }, { a: [], b: {} }, { 'k"ey': 1, '': 2, '1': 3, b: 4, a: 5 },
  [null, 'x', true], { c: 1e21, d: 1e-7, e: 100, f: 0.1 }, 1e300,
  { a: { b: { c: { d: [1, [2, [3]]] } } } }, '<b>&</b>', "it's", '`=',
].forEach((v) => add('{{json v}}', { v }));
add('{{json v}}', {});
add('{{json (mathOperator 0 "/" 0)}}');
add('{{json (mathOperator 1 "/" 0)}}');
add('{{json (parseJSON "[1,\\"a\\"]")}}');
add('{{{json v}}}', { v: { a: [1, 2], b: 'x<y' } });
add('{{json "a b"}}');
add('{{json 1}}');
add('{{json 1.0}}');
add('{{json true}}');
add('{{json (parseJSON s)}}', { s: '{"a":[1,2,{"b":null}],"c":"d"}' });
// parseJSON
const PJ = ['[1,2,3]', '{"a":1}', '{"a":1', '', 'null', 'true', '5', '5.0', '1e2', '"str"', 'undefined', 'NaN', "{'a':1}", '[1,]', ' [1] ', '[]', '{}',
  '01', '+1', '.5', '1.', '[1] x', '{"a":1,"a":2}', '"\\u00e9"', '-0', '1E400', '[1e21]', '{"a":[{"b":"c"}]}', '\t[1]\n', '"a\nb"', 'Infinity', '[1.0,2.50]'];
PJ.forEach((s) => add('{{json (parseJSON s)}}', { s }));
PJ.forEach((s) => add('{{typeof (parseJSON s)}}', { s }));
add('{{json (parseJSON s)}}', {});
add('{{typeof (parseJSON s)}}', {});
add('{{json (parseJSON s)}}', { s: null });
add('{{typeof (parseJSON s)}}', { s: null });
add('{{#each (parseJSON s)}}[{{this}}]{{/each}}', {});
add('{{#each (parseJSON s)}}[{{this}}]{{else}}EMPTY{{/each}}', { s: '[1,2.0,"x"]' });
add('{{#each (parseJSON s)}}[{{this}}]{{else}}EMPTY{{/each}}', { s: 'oops' });
add('{{#each (parseJSON s)}}[{{this}}]{{else}}EMPTY{{/each}}', { s: null });
add('{{#each (parseJSON s)}}[{{@key}}={{this}}]{{else}}EMPTY{{/each}}', { s: '{"a":1,"b":[2]}' });
add('{{json (parseJSON s)}}', { s: [1, 2] });
add('{{json (parseJSON s)}}', { s: [] });
add('{{json (parseJSON s)}}', { s: ['[1]'] });
add('{{json (parseJSON s)}}', { s: {} });
add('{{json (parseJSON s)}}', { s: { a: 1 } });
add('{{json (parseJSON s)}}', { s: 5 });
add('{{json (parseJSON s)}}', { s: 1.5 });
add('{{json (parseJSON s)}}', { s: true });
add('{{json (parseJSON s)}}', { s: false });
add('{{json (parseJSON s)}}', { s: 0 });
add('{{or (parseJSON s) false}}', { s: null });
add('{{or (parseJSON s) false}}', {});
add('{{or (parseJSON s) false}}', { s: 'null' });
add('{{or (parseJSON s) false}}', { s: 'bad' });
add('{{or (parseJSON s) false}}', { s: '0' });
add('{{#ifCond (parseJSON s) "==" 0}}T{{else}}F{{/ifCond}}', { s: 'null' });
add('{{#ifCond (parseJSON s) "==" 0}}T{{else}}F{{/ifCond}}', { s: 'x' });
add('{{parseJSON s}}', { s: '[1,2]' });
add('{{parseJSON s}}', { s: '{"a":1}' });
add('{{parseJSON s}}', { s: '"a&b"' });
add('{{parseJSON s}}', { s: '1.0' });
add('{{parseJSON s}}', { s: '1e21' });
add('{{parseJSON s}}', { s: 'null' });
// null vs missing at top level and in rendering / builtins
add('[{{x}}]', { x: null });
add('[{{x}}]', {});
add('[{{x}}]', { x: false });
add('[{{x}}]', { x: 0 });
add('[{{x}}]', { x: true });
add('[{{#if x}}T{{else}}F{{/if}}]', { x: null });
add('[{{#unless x}}T{{else}}F{{/unless}}]', { x: null });
add('[{{#with x}}T{{else}}F{{/with}}]', { x: null });
add('[{{#each x}}T{{else}}F{{/each}}]', { x: null });
add('[{{#if x}}T{{else}}F{{/if}}]', { x: [] });
add('[{{#ifEquals x y}}T{{else}}F{{/ifEquals}}]', { x: null, y: null });
// number printing
[1, 1.5, -1, 0, 100, 1e21, 1e-7, 123456789.123456789, 0.000001, 1e300, 0.1 + 0.2, 5e-324, 1.7976931348623157e308, 123456789012345680000,
  4.35, 2 ** 53, 0.5, 1 / 3, 100 * 1.1, 1e20, 999999999999999900000, 12345.6789e-10].forEach((x) => add('{{x}}', { x }));
add('{{x}} {{y}} {{z}}', { x: 1.0, y: 2.50, z: 10 });
add('{{#each xs}}[{{this}}]{{/each}}', { xs: [1, 2.0, 3.5, 1e21, 0.1] });
add('{{#each xs}}[{{this}}]{{/each}}', { xs: [true, false, null, 'a'] });
add('{{x}}', { x: [1, 2] });
add('{{x}}', { x: [1, [2, 3]] });
add('{{x}}', { x: [] });
add('{{x}}', { x: [null, 1] });
add('{{x}}', { x: {} });
add('{{x}}', { x: { a: 1 } });
add('{{x}}', { x: [1.0, 2.5, 'a'] });
add('{{x.y}}', { x: { y: 3.0 } });
add('{{x.y}}', { x: null });
add('{{x.y}}', {});
add('{{x.[0]}}', { x: [1.0, 2] });
add('{{#each m}}[{{@key}}:{{this}}]{{/each}}', { m: { a: 1.0, b: null, c: 'z' } });
add('{{x}}', { x: 'a&b<c>"d\'e`f=g' });
add('{{{x}}}', { x: 'a&b<c>' });
add('{{x}}', { x: '&amp;' });
add('{{length}}', { length: 3 });
add('{{this}}', {});
// helper outputs escape / types
add('{{yesNoHelper x}}{{typeof x}}', { x: 'a' });
add('{{#ifEquals "a" "a"}}<b>&</b>{{/ifEquals}}');
add('{{#safeEmpty}}<b>&</b>{{/safeEmpty}}');
add('{{conditionalAttribiute "a" "a" "x" "<" ">"}}');

// literal null / undefined reaching helpers (design 7.1), key present-null and key missing
const NULLCTX = [{ a: null }, {}, { a: 0 }, { a: '' }, { a: false }, { a: 'x' }];
NULLCTX.forEach((c) => {
  add('{{#ifCond a "===" null}}T{{else}}F{{/ifCond}}', c);
  add('{{#ifCond a "!==" null}}T{{else}}F{{/ifCond}}', c);
  add('{{#ifCond a "==" null}}T{{else}}F{{/ifCond}}', c);
  add('{{#ifCond null "===" a}}T{{else}}F{{/ifCond}}', c);
  add('{{#ifEquals a null}}T{{else}}F{{/ifEquals}}', c);
  add('{{#ifNotEquals a null}}T{{else}}F{{/ifNotEquals}}', c);
  add('{{#ifCond a "===" undefined}}T{{else}}F{{/ifCond}}', c);
});
add('{{typeof null}}');
add('{{typeof undefined}}');
add('{{typeof null}}', { null: null });
add('{{json null}}');
add('[{{json undefined}}]');
add('{{parseJSON null}}');
add('{{json (parseJSON null)}}');
add('{{typeof (parseJSON null)}}');
add('{{parseJSON undefined}}');
add('{{json (parseJSON undefined)}}');
add('{{yesNoHelper null}}');
add('{{yesNoHelper null "fallback"}}');
add('{{yesNoHelper undefined "fallback"}}');
add('{{yesNoHelper a null}}', { a: 'x' });
add('{{yesNoHelper a null}}', { a: null });
add('{{yesNoHelper a null}}', {});
add('{{#ifCond null "&&" 1}}T{{else}}F{{/ifCond}}');
add('{{#ifCond null "||" 1}}T{{else}}F{{/ifCond}}');
add('{{#ifCond null "<" 1}}T{{else}}F{{/ifCond}}');
add('{{#ifCond null ">=" 0}}T{{else}}F{{/ifCond}}');
add('{{#ifCond null "==" 0}}T{{else}}F{{/ifCond}}');
add('{{#ifCond 0 "<=" null}}T{{else}}F{{/ifCond}}');
add('{{#ifCond null "==" undefined}}T{{else}}F{{/ifCond}}');
add('{{#ifCond null "===" undefined}}T{{else}}F{{/ifCond}}');
add('{{or null 1}}');
add('{{or null undefined}}');
add('{{#ifOr null 0}}T{{else}}F{{/ifOr}}');
add('{{#ifAnd null 1}}T{{else}}F{{/ifAnd}}');
add('{{#ifAnd 1 null}}T{{else}}F{{/ifAnd}}');
add('{{conditionalAttribiute null null "k" "yes" "no"}}');
add('{{conditionalAttribiute a null "k" "yes" "no"}}', { a: null });
add('{{conditionalAttribiute a null "k" "yes" "no"}}', {});
add('{{conditionalAttribiute a null "k" "yes" "no"}}', { a: 0 });
add('{{conditionalAttribiute "x" "x" null "yes" "no"}}');
add('{{conditionalAttribiute "x" "y" "k" "yes" null}}');
add('{{conditionalAttribiute "x" "x" "k" null "no"}}');
add('{{inList null "a,b"}}');
add('{{inList "a" null}}');
add('{{inList "" null}}');
add('{{safeString null}}');
add('{{safeString undefined}}');
add('{{mathOperator null "+" 1}}');
add('{{mathOperator 1 "+" null}}');
add('{{mathOperator 1 null 1}}');
add('{{#stringContentCompare null "a"}}T{{else}}F{{/stringContentCompare}}');
add('{{#stringContentCompare "a" null}}T{{else}}F{{/stringContentCompare}}');
add('[{{null}}]');
add('[{{undefined}}]');
add('[{{#if null}}T{{else}}F{{/if}}]');
add('[{{#unless null}}T{{else}}F{{/unless}}]');
add('[{{#with null}}T{{else}}F{{/with}}]');
add('[{{#each null}}T{{else}}F{{/each}}]');
add('{{typeof (mathOperator null "+" 1)}}');

// built-in if / unless (design 7.1 amendment): Handlebars.js 4.7.8 truthiness, includeZero, else branches
const IFV = [
  ['missing', undefined], ['null', null], ['false', false], ['true', true], ['zero', 0], ['zerod', 0.0], ['half', 0.5],
  ['one', 1], ['minus', -1], ['empty', ''], ['zeroStr', '0'], ['falseStr', 'false'], ['space', ' '], ['text', 'a'],
  ['emptyList', []], ['zeroList', [0]], ['nullList', [null]], ['emptyList2', [[]]], ['emptyMap', {}], ['map', { a: 1 }],
  ['nullMap', { a: null }], ['big', 1e21], ['tiny', 5e-324], ['neg0', -0.0],
];
IFV.forEach(([name, v]) => {
  const ctx = v === undefined ? {} : { x: v };
  add('[{{#if x}}T{{else}}F{{/if}}]', ctx);
  add('[{{#if x}}T{{/if}}]', ctx);
  add('[{{#unless x}}T{{else}}F{{/unless}}]', ctx);
  add('[{{#unless x}}T{{/unless}}]', ctx);
  add('[{{#if x includeZero=true}}T{{else}}F{{/if}}]', ctx);
  add('[{{#unless x includeZero=true}}T{{else}}F{{/unless}}]', ctx);
  add('[{{#if x includeZero=false}}T{{else}}F{{/if}}]', ctx);
  add('[{{#if x.y}}T{{else}}F{{/if}}]', ctx);
  add('[{{#if x}}A{{else if y}}B{{else}}C{{/if}}]', ctx);
  add('[{{#unless x}}A{{else if y}}B{{else}}C{{/unless}}]', ctx);
});
add('[{{#if x}}A{{else if y}}B{{else}}C{{/if}}]', { x: 0, y: 0.5 });
add('[{{#if x}}A{{else if y}}B{{else}}C{{/if}}]', { x: [], y: [] });
add('[{{#if x}}A{{else if y}}B{{else}}C{{/if}}]', { x: [], y: [1] });
add('[{{#if (mathOperator 0 "/" 0)}}T{{else}}F{{/if}}]');
add('[{{#unless (mathOperator 0 "/" 0)}}T{{else}}F{{/unless}}]');
add('[{{#if (mathOperator 1 "/" 0)}}T{{else}}F{{/if}}]');
add('[{{#if (mathOperator 0 "+" 0)}}T{{else}}F{{/if}}]');
add('[{{#if (mathOperator 1 "/" 2)}}T{{else}}F{{/if}}]');
add('[{{#if (mathOperator 0 "+" 0) includeZero=true}}T{{else}}F{{/if}}]');
add('[{{#if (mathOperator 0 "/" 0) includeZero=true}}T{{else}}F{{/if}}]');
add('[{{#if (mathOperator 3 "%" 2)}}T{{else}}F{{/if}}]');
add('[{{#if (mathOperator "abc" "+" 1)}}T{{else}}F{{/if}}]');
add('[{{#if (parseJSON s)}}T{{else}}F{{/if}}]', { s: '[]' });
add('[{{#if (parseJSON s)}}T{{else}}F{{/if}}]', { s: '[0]' });
add('[{{#if (parseJSON s)}}T{{else}}F{{/if}}]', { s: '{}' });
add('[{{#if (parseJSON s)}}T{{else}}F{{/if}}]', { s: 'null' });
add('[{{#if (parseJSON s)}}T{{else}}F{{/if}}]', { s: 'bad' });
add('[{{#if (parseJSON s)}}T{{else}}F{{/if}}]', { s: '0' });
add('[{{#if (parseJSON s)}}T{{else}}F{{/if}}]', { s: '0.5' });
add('[{{#if (parseJSON s)}}T{{else}}F{{/if}}]', { s: '""' });
add('[{{#if 0}}T{{else}}F{{/if}}]');
add('[{{#if 0.5}}T{{else}}F{{/if}}]');
add('[{{#if 0 includeZero=true}}T{{else}}F{{/if}}]');
add('[{{#if ""}}T{{else}}F{{/if}}]');
add('[{{#if "0"}}T{{else}}F{{/if}}]');
add('[{{#if true}}T{{else}}F{{/if}}]');
add('[{{#if false}}T{{else}}F{{/if}}]');
add('[{{#unless 0}}T{{else}}F{{/unless}}]');
add('[{{#unless 0.5}}T{{else}}F{{/unless}}]');
add('[{{#if x}}[{{this}}]{{else}}F{{/if}}]', { x: 'in' });
add('[{{#if x}}{{../y}}{{/if}}]', { x: 1, y: 'up' });
add('[{{#each xs}}{{#if this}}T{{else}}F{{/if}}{{/each}}]', { xs: [0, 0.5, '', 'a', [], [0], {}, null, false, true, 1] });
add('[{{#each xs}}{{#unless this}}T{{else}}F{{/unless}}{{/each}}]', { xs: [0, 0.5, '', 'a', [], [0], {}, null, false, true, 1] });
add('[{{#if x}}T{{else}}F{{/if}}]', { x: 1.0 });
add('[{{#if x}}T{{else}}F{{/if}}]', { x: 0.0 });
add('[{{#if x}}T{{else}}F{{/if}}]', { x: 1e-7 });
add('[{{#if x}}T{{else}}F{{/if}}]', { x: [0.0] });
add('[{{#if a.b.c}}T{{else}}F{{/if}}]', { a: { b: { c: 0 } } });
add('[{{#if a.b.c}}T{{else}}F{{/if}}]', { a: { b: { c: [] } } });
add('[{{#if a.b.c}}T{{else}}F{{/if}}]', { a: { b: null } });
add('[{{#if @index}}T{{else}}F{{/if}}]');
add('[{{#each xs}}{{#if @index}}T{{else}}F{{/if}}{{/each}}]', { xs: ['a', 'b', 'c'] });
add('[{{#each xs}}{{#if @index includeZero=true}}T{{else}}F{{/if}}{{/each}}]', { xs: ['a', 'b', 'c'] });
add('[{{#if a b}}T{{else}}F{{/if}}]', { a: 1, b: 1 });
add('[{{#unless a b}}T{{else}}F{{/unless}}]', { a: 1, b: 1 });
add('[{{#if a}}T{{else}}F{{/if}}]', { a: [] });
// Scope: a name missing from the current item is undefined (no parent-context walk); null list elements are JS null.
// (attempt 4; design.md section 7.1 amended 2026-11-23)
add('{{#each items}}[{{typeof name}}][{{name}}]{{/each}}', { name: 'root', items: [{}] });
add('{{#each xs}}[{{typeof this}}][{{json this}}]{{/each}}', { xs: [null] });
add('{{#with o}}[{{name}}]{{/with}}', { name: 'root', o: {} });
add('{{#each items}}[{{name}}]{{/each}}', { name: 'root', items: [{}, { name: 'own' }, { name: null }, {}] });
add('{{#each items}}[{{typeof name}}]{{/each}}', { name: 'root', items: [{}, { name: 'own' }, { name: null }] });
add('{{#each items}}[{{#if name}}T{{else}}F{{/if}}]{{/each}}', { name: 'root', items: [{}, { name: 'own' }] });
add('{{#each items}}[{{name.first}}]{{/each}}', { name: { first: 'root' }, items: [{}] });
add('{{#each items}}[{{a.b}}]{{/each}}', { a: { b: 'root' }, items: [{ a: {} }, {}] });
add('{{#each items}}[{{a.b}}]{{/each}}', { a: { b: 'root' }, items: [{ a: { b: 'own' } }] });
add('{{#each a}}{{#each b}}[{{n}}]{{/each}}{{/each}}', { n: 'root', a: [{ n: 'outer', b: [{}, { n: 'inner' }] }] });
add('{{#each a}}{{#each b}}[{{typeof n}}]{{/each}}{{/each}}', { n: 'root', a: [{ n: 'outer', b: [{}] }] });
add('{{#each a}}{{#each b}}[{{n}}]{{/each}}{{/each}}', { n: 'root', a: [{ b: [{}] }] });
add('{{#with o}}{{#with p}}[{{n}}]{{/with}}{{/with}}', { n: 'root', o: { n: 'mid', p: {} } });
add('{{#with o}}{{#each p}}[{{n}}]{{/each}}{{/with}}', { n: 'root', o: { n: 'mid', p: [{}] } });
add('{{#each items}}{{#with sub}}[{{n}}]{{/with}}{{/each}}', { n: 'root', items: [{ n: 'item', sub: {} }] });
add('{{#each o}}[{{name}}]{{/each}}', { name: 'root', o: { a: {}, b: { name: 'x' } } });
add('{{#each items}}{{#ifCond 1 "===" 1}}[{{name}}]{{/ifCond}}{{/each}}', { name: 'root', items: [{}, { name: 'i' }] });
add('{{#each items}}{{#ifEquals 1 1}}[{{name}}]{{/ifEquals}}{{/each}}', { name: 'root', items: [{}, { name: 'i' }] });
add('{{#each items}}{{#ifNotEquals 1 2}}[{{name}}]{{/ifNotEquals}}{{/each}}', { name: 'root', items: [{}] });
add('{{#each items}}{{#ifAnd 1 1}}[{{name}}]{{/ifAnd}}{{/each}}', { name: 'root', items: [{}] });
add('{{#each items}}{{#ifOr 1 0}}[{{name}}]{{/ifOr}}{{/each}}', { name: 'root', items: [{}] });
add('{{#each items}}{{#stringContentCompare "a" "A"}}[{{name}}]{{/stringContentCompare}}{{/each}}', { name: 'root', items: [{}] });
add('{{#each items}}{{#safeEmpty}}{{name}}{{/safeEmpty}}{{/each}}', { name: 'root', items: [{}, { name: 'i' }] });
add('{{#each items}}{{#percentage}}{{pct}}{{/percentage}}{{/each}}', { pct: 0.5, items: [{}, { pct: 0.25 }] });
add('{{#each items}}{{#if x}}[{{name}}]{{/if}}{{/each}}', { name: 'root', items: [{ x: 1 }, { x: 1, name: 'i' }] });
add('{{#each items}}{{#unless x}}[{{name}}]{{/unless}}{{/each}}', { name: 'root', items: [{}] });
add('{{#each items}}{{#if x}}{{else}}[{{name}}]{{/if}}{{/each}}', { name: 'root', items: [{}] });
add('{{#each items}}{{mathOperator n "+" 1}}{{/each}}', { n: 5, items: [{}, { n: 1 }] });
add('{{#each items}}[{{safeString name}}]{{/each}}', { name: 'root', items: [{}] });
add('{{#each items}}[{{#ifCond name "===" "root"}}R{{else}}O{{/ifCond}}]{{/each}}', { name: 'root', items: [{}] });
add('{{#each items}}[{{conditionalAttribiute name "root" "a" "Y" "N"}}]{{/each}}', { name: 'root', items: [{}] });
add('{{#each items}}[{{yesNoHelper name}}]{{/each}}', { name: 'root', items: [{}] });
add('{{#each items}}[{{inList name "a,b"}}]{{/each}}', { name: 'a', items: [{ name: 'a' }] });
add('{{#each items}}[{{or name flag}}]{{/each}}', { name: 'root', flag: 1, items: [{}, { flag: 0 }] });
add('{{#each items}}[{{json name}}]{{/each}}', { name: 'root', items: [{}] });
// parents stay reachable through ../ and @root, and the item itself through this / ./
add('{{#each items}}[{{../name}}]{{/each}}', { name: 'root', items: [{}, { name: 'own' }] });
add('{{#each items}}[{{@root.name}}]{{/each}}', { name: 'root', items: [{}, { name: 'own' }] });
add('{{#each items}}[{{@root.name}}][{{name}}]{{/each}}', { name: 'root', items: [{ name: 'own' }] });
add('{{#each items}}[{{this.name}}][{{./name}}]{{/each}}', { name: 'root', items: [{}, { name: 'own' }] });
add('{{#with o}}[{{../name}}]{{/with}}', { name: 'root', o: {} });
add('{{#with o}}[{{@root.name}}][{{this.name}}]{{/with}}', { name: 'root', o: { name: 'in' } });
add('{{#each a}}{{#each b}}[{{../n}}][{{../../n}}][{{@root.n}}]{{/each}}{{/each}}', { n: 'root', a: [{ n: 'outer', b: [{}] }] });
add('{{#each a}}{{#with b}}[{{../n}}][{{../../n}}]{{/with}}{{/each}}', { n: 'root', a: [{ n: 'outer', b: {} }] });
add('{{#each items}}{{#if x}}[{{../name}}]{{/if}}{{/each}}', { name: 'root', items: [{ x: 1 }] });
add('{{#each items}}{{#ifCond 1 "===" 1}}[{{../name}}]{{/ifCond}}{{/each}}', { name: 'root', items: [{}] });
add('{{#each items}}{{#safeEmpty}}{{../name}}{{/safeEmpty}}{{/each}}', { name: 'root', items: [{}] });
add('{{#each items}}{{#with sub}}[{{../name}}]{{/with}}{{/each}}', { name: 'root', items: [{ name: 'item', sub: {} }] });
add('{{#each items}}[{{@index}}/{{@first}}/{{@last}}][{{name}}]{{/each}}', { name: 'root', items: [{}, { name: 'b' }, {}] });
add('{{#each o}}[{{@key}}][{{@index}}][{{name}}]{{/each}}', { name: 'root', o: { a: {}, b: { name: 'b' } } });
add('{{#each items}}{{#each sub}}[{{@index}}][{{@../index}}][{{@root.name}}]{{/each}}{{/each}}', { name: 'root', items: [{ sub: [1, 2] }, { sub: [3] }] });
// Data variables (@index, @key, @first, @last) stay visible in nested scopes (attempt 5; design.md section 7.1)
add('{{#each xs}}{{#with o}}[{{@index}}][{{@first}}][{{@last}}]{{/with}}{{/each}}', { xs: [{ o: {} }, { o: { a: 1 } }, { o: {} }] });
add('{{#each m}}{{#with this}}[{{@key}}]{{/with}}{{/each}}', { m: { a: { x: 1 }, b: { y: 2 } } });
add('{{#each m}}{{#with this}}[{{@key}}][{{@index}}][{{@first}}][{{@last}}]{{/with}}{{/each}}', { m: { a: { x: 1 }, b: { y: 2 } } });
add('{{#each xs}}{{#ifEquals this null}}[{{@index}}]{{/ifEquals}}{{/each}}', { xs: [null] });
add('{{#each xs}}{{#ifEquals this null}}[{{@index}}]{{/ifEquals}}{{/each}}', { xs: [1, null, 2, null] });
add('{{#each xs}}{{#ifEquals this null}}[{{@first}}][{{@last}}]{{/ifEquals}}{{/each}}', { xs: [null, 1, null] });
add('{{#each m}}{{#ifEquals this null}}[{{@key}}]{{/ifEquals}}{{/each}}', { m: { a: null, b: 1 } });
add('{{#each xs}}{{#if this}}[{{@index}}]{{else}}<{{@index}}>{{/if}}{{/each}}', { xs: [null, 1, 0, 'a'] });
add('{{#each xs}}{{#unless this}}[{{@index}}]{{else}}<{{@index}}>{{/unless}}{{/each}}', { xs: [null, 1, 0, 'a'] });
add('{{#each xs}}{{#if x}}[{{@index}}][{{@first}}][{{@last}}]{{/if}}{{/each}}', { xs: [{ x: 1 }, {}, { x: 1 }] });
add('{{#each xs}}{{#unless x}}[{{@index}}][{{@first}}][{{@last}}]{{/unless}}{{/each}}', { xs: [{ x: 1 }, {}, { x: 1 }] });
add('{{#each xs}}{{#safeEmpty}}[{{@index}}]{{/safeEmpty}}{{/each}}', { xs: [1, 2] });
add('{{#each xs}}{{#safeEmpty}}{{@index}}{{@last}}{{/safeEmpty}}{{/each}}', { xs: [{}, {}] });
add('{{#each xs}}{{#ifOr x y}}[{{@index}}][{{@last}}]{{/ifOr}}{{/each}}', { xs: [{ x: 1 }, {}, { y: 1 }] });
add('{{#each xs}}{{#ifAnd x y}}[{{@index}}]{{else}}<{{@index}}>{{/ifAnd}}{{/each}}', { xs: [{ x: 1, y: 1 }, { x: 1 }] });
add('{{#each xs}}{{#ifCond @index "===" 1}}[one]{{else}}[{{@index}}]{{/ifCond}}{{/each}}', { xs: ['a', 'b', 'c'] });
add('{{#each xs}}{{#ifEquals @index 0}}[first]{{/ifEquals}}{{#ifNotEquals @index 0}}[{{@index}}]{{/ifNotEquals}}{{/each}}', { xs: ['a', 'b', 'c'] });
add('{{#each xs}}{{#if @last}}[last]{{else}}[{{@index}}]{{/if}}{{/each}}', { xs: ['a', 'b', 'c'] });
add('{{#each xs}}{{#unless @last}}[{{@index}}]{{/unless}}{{/each}}', { xs: ['a', 'b', 'c'] });
add('{{#each xs}}{{#if @first}}[first]{{/if}}{{/each}}', { xs: ['a', 'b'] });
add('{{#each xs}}{{#each ys}}[{{@index}}][{{@../index}}]{{/each}}|{{/each}}', { xs: [{ ys: ['a', 'b'] }, { ys: ['c'] }] });
add('{{#each xs}}{{#each ys}}[{{@first}}][{{@last}}][{{@../first}}][{{@../last}}]{{/each}}|{{/each}}', { xs: [{ ys: ['a', 'b'] }, { ys: ['c'] }] });
add('{{#each o}}{{#each this}}[{{@key}}][{{@../key}}]{{/each}}{{/each}}', { o: { a: { x: 1, y: 2 }, b: { z: 3 } } });
add('{{#each xs}}{{#each ys}}[{{@index}}]{{/each}}[{{@index}}]{{/each}}', { xs: [{ ys: [1, 2] }, { ys: [] }, { ys: [3] }] });
add('{{#each xs}}{{#each ys}}{{#with this}}[{{@index}}][{{@../index}}]{{/with}}{{/each}}{{/each}}', { xs: [{ ys: [{ a: 1 }, { b: 2 }] }, { ys: [{ c: 3 }] }] });
add('{{#each xs}}{{#each ys}}{{#with this}}[{{@index}}][{{@first}}][{{@last}}]{{/with}}{{/each}}{{/each}}', { xs: [{ ys: [{ a: 1 }, { b: 2 }] }, { ys: [{ c: 3 }] }] });
add('{{#each xs}}{{#with o}}{{#with p}}[{{@index}}][{{@last}}]{{/with}}{{/with}}{{/each}}', { xs: [{ o: { p: {} } }, { o: { p: { q: 1 } } }] });
add('{{#each xs}}{{#each ys}}{{#if @last}}[{{@../index}}]{{/if}}{{/each}}{{/each}}', { xs: [{ ys: [1, 2] }, { ys: [3] }] });
add('{{#each xs}}{{#each ys}}{{#ifOr @first @last}}[{{@index}}]{{/ifOr}}{{/each}}{{/each}}', { xs: [{ ys: [1, 2, 3] }] });
add('{{#each xs}}{{#with o}}{{#if @first}}[first]{{else}}[{{@index}}]{{/if}}{{/with}}{{/each}}', { xs: [{ o: {} }, { o: {} }] });
add('{{#each xs}}{{#with o}}{{@root.n}}[{{@index}}]{{/with}}{{/each}}', { n: 'root', xs: [{ o: {} }] });
add('{{#each xs}}{{#ifEquals this null}}{{#with this}}[{{@index}}]{{/with}}{{else}}<{{@index}}>{{/ifEquals}}{{/each}}', { xs: [null, 1] });
add('{{#each xs}}{{#with o}}[{{@missing}}][{{typeof @missing}}]{{/with}}{{/each}}', { xs: [{ o: {} }] });
add('{{#each xs}}{{#with o}}[{{@index}}]{{/with}}{{/each}}[{{@index}}]', { xs: [{ o: {} }] });
// A block helper's body renders in the current item (`this`), whatever its first argument is (attempt 5: a missing or
// null first argument must not turn the body's context into an empty object)
add('{{#if r}}R{{else}}[{{d}}]{{/if}}', { d: 7 });
add('{{#unless r}}[{{d}}]{{/unless}}', { d: 7 });
add('{{#if r}}R{{else}}{{#if d}}D[{{d}}]{{/if}}{{/if}}', { d: 7 });
add('{{#if r}}R{{else}}[{{d}}]{{/if}}', { r: null, d: 7 });
add('{{#if r}}R{{else}}[{{d}}]{{/if}}', { r: 0, d: 7 });
add('{{#unless r}}[{{d}}]{{/unless}}', { r: null, d: 7 });
add('{{#ifEquals r 1}}x{{else}}[{{d}}]{{/ifEquals}}', { d: 7 });
add('{{#ifNotEquals r null}}x{{else}}[{{d}}]{{/ifNotEquals}}', { d: 7 });
add('{{#ifEquals r null}}[{{d}}]{{/ifEquals}}', { d: 7 });
add('{{#ifCond r "===" 1}}x{{else}}[{{d}}]{{/ifCond}}', { d: 7 });
add('{{#ifCond r "==" null}}[{{d}}]{{/ifCond}}', { d: 7 });
add('{{#ifOr this.a this.b}}x{{else}}[{{d}}]{{/ifOr}}', { d: 7 });
add('{{#ifOr this.a this.b}}[{{this.c}}]{{/ifOr}}', { b: true, c: 3 });
add('{{#ifOr this.a this.b}}[{{#if this.b}}B{{/if}}[{{c}}]]{{/ifOr}}', { b: true, c: 3 });
add('{{#ifAnd r d}}x{{else}}[{{d}}]{{/ifAnd}}', { d: 7 });
add('{{#stringContentCompare "a" "b"}}x{{else}}[{{d}}]{{/stringContentCompare}}', { d: 7 });
add('{{#each xs}}{{#if r}}R{{else}}[{{d}}]{{/if}}{{/each}}', { xs: [{ d: 7 }, { r: 1, d: 8 }, { r: null, d: 9 }] });
add('{{#each xs}}{{#unless r}}[{{d}}]{{/unless}}{{/each}}', { xs: [{ d: 7 }, { r: 1, d: 8 }] });
add('{{#each xs}}{{#if r}}R{{else}}{{#if d}}D[{{d}}]{{/if}}{{/if}}{{/each}}', { xs: [{ r: 'x' }, { d: 7 }, {}] });
add('{{#each xs}}{{#ifOr this.a this.b}}[{{#if this.b}}B{{/if}}{{#if this.c}}C{{/if}}]{{/ifOr}}{{/each}}', { xs: [{ b: true, c: true }, { a: 1 }, { c: true }] });
add('{{#each xs}}{{#ifEquals r null}}[{{d}}][{{@index}}]{{/ifEquals}}{{/each}}', { xs: [{ d: 7 }, { r: 1, d: 8 }] });
add('{{#each xs}}{{#ifCond r "===" 1}}x{{else}}[{{d}}][{{@index}}]{{/ifCond}}{{/each}}', { xs: [{ d: 7 }, { r: 1, d: 8 }] });
add('{{#with o}}{{#if r}}R{{else}}[{{d}}]{{/if}}{{/with}}', { o: { d: 7 } });
// Not covered (Handlebars.java limit, design.md 7.1): a plain name missing from the item inside a block-parameter block
// ({{#each x as |i|}}, {{#with x as |p|}}) still reads the enclosing scope; Context$BlockParam is package-private.
add('{{#each items as |it i|}}[{{i}}][{{it.name}}]{{/each}}', { items: [{ name: 'a' }, { name: 'b' }] });
add('{{#each a as |x|}}{{#each b as |y|}}[{{x.n}}][{{y.n}}]{{/each}}{{/each}}', { a: [{ n: 1, b: [{ n: 2 }] }] });
add('{{#with o as |p|}}[{{p.name}}][{{name}}]{{/with}}', { name: 'root', o: { name: 'in' } });
add('{{#each items as |name|}}[{{name}}]{{/each}}', { items: [{ name: 'item' }] });
add('{{name}}', { name: 'root' });
add('{{#with o}}{{name}}{{else}}EMPTY{{/with}}', { name: 'root', o: {} });
add('{{#each items}}{{name}}{{else}}EMPTY{{/each}}', { name: 'root', items: [] });
// null elements of lists and maps are JavaScript null
const nullTemplates = [
  '[{{typeof this}}]', '[{{json this}}]', '[{{this}}]', '[{{#ifCond this "===" null}}T{{else}}F{{/ifCond}}]',
  '[{{#ifCond this "==" null}}T{{else}}F{{/ifCond}}]', '[{{#ifCond this "!==" null}}T{{else}}F{{/ifCond}}]',
  '[{{#ifEquals this null}}T{{else}}F{{/ifEquals}}]', '[{{#ifEquals this ""}}T{{else}}F{{/ifEquals}}]',
  '[{{#ifEquals this 0}}T{{else}}F{{/ifEquals}}]', '[{{#ifNotEquals this null}}T{{else}}F{{/ifNotEquals}}]',
  '[{{or this 0}}]', '[{{or this null}}]', '[{{#if this}}T{{else}}F{{/if}}]', '[{{#unless this}}T{{else}}F{{/unless}}]',
  '[{{safeString this}}]', '[{{conditionalAttribiute this null "a" "Y" "N"}}]',
  '[{{conditionalAttribiute this "" "a" "Y" "N"}}]', '[{{yesNoHelper this}}]', '[{{yesNoHelper this "dflt"}}]',
  '[{{#ifAnd this 1}}T{{else}}F{{/ifAnd}}]', '[{{#ifOr this 0}}T{{else}}F{{/ifOr}}]',
  '[{{mathOperator this "+" 1}}]', '[{{#safeEmpty}}{{this}}{{/safeEmpty}}]', '[{{name}}]',
];
[{ xs: [null] }, { xs: [1, null] }, { xs: [null, null, 'a'] }, { xs: [[null]] }, { xs: [{ a: null }, null] }].forEach((data) => {
  nullTemplates.forEach((t) => add('{{#each xs}}' + t + '{{/each}}', data));
});
nullTemplates.forEach((t) => add('{{#each o}}' + t + '{{/each}}', { o: { a: null } }));
nullTemplates.forEach((t) => add('{{#each o.a}}' + t + '{{/each}}', { o: { a: [null] } }));
nullTemplates.forEach((t) => add('{{#each a}}' + t + '{{/each}}', { a: [null] }));
nullTemplates.forEach((t) => add('{{#each (parseJSON s)}}' + t + '{{/each}}', { s: '[null]' }));
nullTemplates.forEach((t) => add('{{#each (parseJSON s)}}' + t + '{{/each}}', { s: '[1,null]' }));
nullTemplates.forEach((t) => add('{{#each (parseJSON s)}}' + t + '{{/each}}', { s: '{"a":null}' }));
nullTemplates.forEach((t) => add('{{#each (parseJSON s)}}' + t + '{{/each}}', { s: '{"a":[null]}' }));
nullTemplates.forEach((t) => add('{{#with (parseJSON s)}}{{#each a}}' + t + '{{/each}}{{/with}}', { s: '{"a":[null]}' }));
nullTemplates.forEach((t) => add('{{#with (parseJSON s)}}' + t + '{{/with}}', { s: 'null' }));
add('{{typeof xs.[0]}}', { xs: [null] });
add('{{json xs}}', { xs: [null] });
add('{{json xs}}', { xs: [1, null, [null], { a: null }] });
add('{{json (parseJSON s)}}', { s: '[null]' });
add('{{json (parseJSON s)}}', { s: '{"a":null}' });
add('{{json (parseJSON s)}}', { s: '{"a":[null,{"b":null}]}' });
add('{{typeof (parseJSON s)}}', { s: '[null]' });
add('{{typeof (parseJSON s)}}', { s: '{"a":null}' });
add('{{#with (parseJSON s)}}{{typeof a}}{{/with}}', { s: '{"a":null}' });
add('{{#with (parseJSON s)}}{{#ifCond a "===" null}}T{{else}}F{{/ifCond}}{{/with}}', { s: '{"a":null}' });
add('{{#with (parseJSON s)}}{{#ifCond b "===" null}}T{{else}}F{{/ifCond}}{{/with}}', { s: '{"a":null}' });
add('{{#ifCond xs.[0] "===" null}}T{{else}}F{{/ifCond}}', { xs: [null] });
add('{{#ifEquals xs.[0] null}}T{{else}}F{{/ifEquals}}', { xs: [null] });
add('{{x}}', { x: [null] });
add('{{x}}', { x: [null, [null], 1] });
add('{{typeof a.b.[0]}}', { a: { b: [null] } });
add('{{#ifCond xs.[1] "===" null}}T{{else}}F{{/ifCond}}', { xs: [null] });
// Supported-subset fixes (Pivot 2026-11-23): ../ under a null item, @odd/@even are undefined, @key in nested each
add('{{#each xs}}{{#ifEquals this null}}[{{../n}}]{{/ifEquals}}{{/each}}', { n: 'r', xs: [null] });
add('{{#each xs}}{{#safeEmpty}}{{../n}}{{/safeEmpty}}{{/each}}', { n: 'r', xs: [null] });
add('{{#each xs}}{{#ifEquals this null}}[{{../n}}][{{@index}}][{{@first}}][{{@last}}]{{/ifEquals}}{{/each}}', { n: 'r', xs: [1, null, 2] });
add('{{#each xs}}{{#ifEquals this null}}[{{../n}}][{{x}}][{{@root.n}}]{{/ifEquals}}{{/each}}', { n: 'r', x: 'top', xs: [null] });
add('{{#each xs}}{{#if this}}[{{../n}}]{{else}}[{{../n}}]{{/if}}{{/each}}', { n: 'r', xs: [null, 1] });
add('{{#each a}}{{#each b}}{{#ifEquals this null}}[{{../n}}][{{../../n}}]{{/ifEquals}}{{/each}}{{/each}}', { n: 'r', a: [{ n: 'o', b: [null] }] });
add('{{#each xs}}[{{@odd}}][{{@even}}]{{/each}}', { xs: [1, 2] });
add('{{#each xs}}{{#if @odd}}O{{else}}E{{/if}}{{/each}}', { xs: [1, 2, 3] });
add('{{#each m}}[{{@odd}}][{{@even}}]{{/each}}', { m: { a: 1, b: 2 } });
add('{{#each xs}}{{#with this}}[{{@odd}}][{{@even}}]{{/with}}{{/each}}', { xs: [{ a: 1 }, { a: 2 }] });
add('{{#each m}}{{#each list}}[{{@key}}]{{/each}}{{/each}}', { m: { a: { list: ['x', 'y'] }, b: { list: ['z'] } } });
add('{{#each m}}{{#each list}}[{{@key}}][{{@../key}}]{{/each}}|{{/each}}', { m: { a: { list: ['x', 'y'] }, b: { list: ['z'] } } });
module.exports = R;
