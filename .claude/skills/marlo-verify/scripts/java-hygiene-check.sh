#!/usr/bin/env bash
# Static hygiene checks for changed MARLO .java files - the defects a compile and
# Checkstyle both pass straight through:
#
#   EOL        line endings flipped vs HEAD (a CRLF file rewritten as LF buries the diff)
#   JFREE      org.jfree.util.Log used as a logger - no LogTarget is registered, every call is discarded
#   LOG-ARGS   slf4j placeholder count does not match the arguments (renders a literal {} or drops a value)
#   LOG-CONCAT log message built with + instead of {} placeholders, on a changed line
#   LOG-NULL   a method call inside a log argument on a changed line - can NPE out of the catch it sits in
#   SPANISH    Spanish comment or log message - translate it, never delete it
#   NULL-LIST  a method returning a List / Set / Collection has `return null` - return an empty
#              collection instead (FIX when the change touches the method, REVIEW count for the rest)
#
# FIX items fail the gate (exit 1). REVIEW items (LOG-NULL, LOG-CONCAT, NULL-LIST) need a human trace:
# prove the receiver is non-null, or convert the message while you are in the file.
#
#   java-hygiene-check.sh                 # .java files changed vs HEAD (staged + unstaged)
#   java-hygiene-check.sh <file> [...]    # specific files
set -uo pipefail

REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null)" || true
if [ -z "$REPO_ROOT" ]; then
  echo "error: not inside a git checkout - run this from the MARLO repository" >&2
  exit 1
fi
cd "$REPO_ROOT" || exit 1

if [ $# -gt 0 ]; then
  FILES="$(printf '%s\n' "$@")"
else
  FILES="$( { git diff --name-only --diff-filter=d HEAD -- '*.java'
              git diff --cached --name-only --diff-filter=d HEAD -- '*.java'
              git ls-files --others --exclude-standard -- '*.java'; } | sort -u )"
fi

[ -n "$FILES" ] || { echo "no changed .java files - nothing to check"; exit 0; }

HYGIENE_FILES="$FILES" python3 - <<'PY'
import os
import re
import subprocess
import sys

MAX_SPANISH_LINES = 8

LOG_CALL = re.compile(r'\b(?:LOG|LOGGER|log|logger)\s*\.\s*(?:trace|debug|info|warn|error)\s*\(')
THROWABLE = re.compile(r'^(?:e|ex|exc|e\d|t|th|throwable|cause|\w*[Ee]xception|\w*Error)$')
METHOD_CALL = re.compile(r'([A-Za-z_][\w]*|\))\s*\.\s*[A-Za-z_]\w*\s*\(')
# Receivers that cannot be null: static utilities, and the caught throwable itself.
SAFE_RECEIVER = re.compile(r'^(?:String|Objects|Arrays|Collections|Math|Integer|Long|Double|Boolean|'
                           r'ExceptionUtils|StringUtils|LocalDate|LocalDateTime|Instant|this|super|'
                           r'e|ex|exc|t|th|throwable|\w*[Ee]xception)$')
ACCENTS = re.compile(r'[áéíóúñÁÉÍÓÚÑ¿¡]')
SPANISH_WORDS = {
  'que', 'para', 'los', 'las', 'del', 'por', 'con', 'una', 'esta', 'este', 'estos', 'cuando', 'porque',
  'pero', 'sin', 'hay', 'el', 'la', 'de', 'en', 'y', 'se', 'es', 'lo', 'al', 'si', 'mas', 'tambien',
  'obtener', 'guardar', 'eliminar', 'actualizar', 'crear', 'verificar', 'validar', 'agregar', 'buscar',
  'usuario', 'usuarios', 'proyecto', 'proyectos', 'lista', 'datos', 'campo', 'campos', 'aqui', 'donde',
}


def run(*cmd):
  return subprocess.run(cmd, capture_output=True).stdout


def eol_kind(data):
  crlf = data.count(b'\r\n')
  lf = data.count(b'\n') - crlf
  if crlf == 0 and lf == 0:
    return 'none'
  if crlf == 0:
    return 'LF'
  if lf == 0:
    return 'CRLF'
  return 'mixed (%d CRLF / %d LF)' % (crlf, lf)


def changed_lines(path, is_new, line_count):
  if is_new:
    return set(range(1, line_count + 1))
  out = run('git', 'diff', '-U0', '--ignore-cr-at-eol', 'HEAD', '--', path).decode('utf-8', 'replace')
  lines = set()
  for m in re.finditer(r'^@@ -\S+ \+(\d+)(?:,(\d+))? @@', out, re.M):
    start, count = int(m.group(1)), int(m.group(2) or '1')
    lines.update(range(start, start + count))
  return lines


def scan_call(text, start):
  """Return (end_index, [top-level args]) for the call whose '(' ends at start-1."""
  depth, i, args, cur = 1, start, [], []
  while i < len(text):
    c = text[i]
    if c in '"\'':
      j = i + 1
      while j < len(text) and text[j] != c:
        j += 2 if text[j] == '\\' else 1
      cur.append(text[i:j + 1])
      i = j + 1
      continue
    if c in '([{':
      depth += 1
    elif c in ')]}':
      depth -= 1
      if depth == 0:
        args.append(''.join(cur).strip())
        return i, args
    if c == ',' and depth == 1:
      args.append(''.join(cur).strip())
      cur = []
    else:
      cur.append(c)
    i += 1
  return len(text), args


def literals(expr):
  return re.findall(r'"((?:[^"\\]|\\.)*)"', expr)


def outside_literals(expr):
  return re.sub(r'"(?:[^"\\]|\\.)*"', '""', expr)


# Short imperative comments ("Bindear phase.id", "=== Finalizando binding ===") carry one Spanish word
# at most, so a leading Spanish verb counts on its own.
SPANISH_LEAD = re.compile(r'^[\W_]*(?:bindear|finalizando|iniciando|inicializar|obtener|guardar|validar|'
                          r'verificar|asegurar|luego|buscar|agregar|eliminar|crear|actualizar|recorrer|'
                          r'llenar|cargar|limpiar|revisar|mostrar|enviar|asignar|calcular|se\s)', re.I)


def looks_spanish(fragment):
  if SPANISH_LEAD.match(fragment):
    return True
  # An accent alone is a name (@author Hermes Jiménez), not a sentence: it needs one Spanish word too.
  words = set(re.findall(r'[a-záéíóúñ]+', fragment.lower()))
  hits = len(words & SPANISH_WORDS)
  if ACCENTS.search(fragment) and hits >= 1:
    return True
  return len(words) >= 3 and hits >= 2


COLLECTION_METHOD = re.compile(
  r'(?:(?:public|protected|private)\s+)?(?:static\s+)?(?:final\s+)?(?:synchronized\s+)?(?:<[^{;()]*>\s*)?(?:java\.util\.)?'
  r'(?:List|ArrayList|LinkedList|Set|HashSet|LinkedHashSet|SortedSet|TreeSet|Collection)\s*<[^{;()]*>\s+'
  r'(\w+)\s*\([^)]*\)\s*(?:throws\s+[\w.,\s]+)?\{')


def blank_code_noise(text):
  """Blank comments and string/char literals, keeping every offset and newline, so braces inside them do not
  move the end of a method and a 'return null;' inside a string or comment is not counted."""
  out, i, n = [], 0, len(text)
  while i < n:
    if text.startswith('//', i):
      j = text.find('\n', i)
      j = n if j < 0 else j
      out.append(' ' * (j - i))
      i = j
    elif text.startswith('/*', i):
      j = text.find('*/', i + 2)
      j = n if j < 0 else j + 2
      out.append(re.sub(r'[^\n]', ' ', text[i:j]))
      i = j
    elif text.startswith('"""', i):
      j = text.find('"""', i + 3)
      j = n if j < 0 else j + 3
      out.append(re.sub(r'[^\n]', ' ', text[i:j]))
      i = j
    elif text[i] in '"\'':
      q, j = text[i], i + 1
      while j < n and text[j] != q and text[j] != '\n':
        j += 2 if text[j] == '\\' else 1
      j = min(j, n - 1)
      out.append(q + ' ' * (j - i - 1) + text[j])
      i = j + 1
    else:
      out.append(text[i])
      i += 1
  return ''.join(out)


def own_null_returns(body):
  """Offsets of 'return null;' that return from the method itself, not from a lambda or an anonymous class
  declared inside it."""
  stack, found, i = [], [], 0
  while i < len(body):
    c = body[i]
    if c == '{':
      before = body[max(0, i - 300):i].rstrip()
      # The statement that opens this block: text since the previous ';', '{' or '}'.
      statement = re.split(r'[;{}]', before)[-1]
      # 'case X -> {' and 'default -> {' are switch arms: a return there leaves the method, unlike a lambda.
      lambda_body = before.endswith('->') and not re.match(r'\s*(case\b|default\b)', statement)
      anonymous_class = re.search(r'\bnew\s+[\w.<>?,\s]+\((?:[^()]|\([^()]*\))*\)$', before)
      nested = lambda_body or anonymous_class
      stack.append(bool(nested))
    elif c == '}':
      if stack:
        stack.pop()
    elif body.startswith('return', i) and not any(stack):
      m = re.match(r'return\s+null\s*;', body[i:])
      if m and (i == 0 or not (body[i - 1].isalnum() or body[i - 1] == '_')):
        found.append(i)
    i += 1
  return found


def null_collection_methods(text):
  """Yield (name, first_line, last_line, [return-null lines]) for collection methods returning null."""
  code = blank_code_noise(text)
  for m in COLLECTION_METHOD.finditer(code):
    depth, i = 1, m.end()
    while depth and i < len(code):
      depth += code[i] == '{'
      depth -= code[i] == '}'
      i += 1
    nulls = [code.count('\n', 0, m.end() + pos) + 1 for pos in own_null_returns(code[m.end():i - 1])]
    if nulls:
      yield m.group(1), code.count('\n', 0, m.start()) + 1, code.count('\n', 0, i) + 1, nulls


def comment_fragments(text):
  """Yield (line_no, comment_text), skipping the GPL header block."""
  for m in re.finditer(r'//[^\n]*|/\*.*?\*/', text, re.S):
    body = m.group(0)
    if 'GNU General Public License' in body or 'This file is part of' in body:
      continue
    line_no = text.count('\n', 0, m.start()) + 1
    for offset, part in enumerate(body.split('\n')):
      part = re.sub(r'^\s*(?://+|/\*+|\*+/?|\*)', '', part).strip()
      if part and not part.startswith('@author'):
        yield line_no + offset, part


fail = False
for path in os.environ['HYGIENE_FILES'].split('\n'):
  if not path:
    continue
  try:
    data = open(path, 'rb').read()
  except OSError:
    print('=== %s\n  (cannot read - skipped)' % path)
    continue
  head = subprocess.run(['git', 'show', 'HEAD:' + path], capture_output=True)
  is_new = head.returncode != 0
  text = data.decode('utf-8', 'replace').replace('\r\n', '\n')
  line_of = lambda idx: text.count('\n', 0, idx) + 1
  touched = changed_lines(path, is_new, text.count('\n') + 1)
  findings = []

  # EOL: a whole-file rewrite that flipped the endings.
  if not is_new:
    before, after = eol_kind(head.stdout), eol_kind(data)
    if before != after and after != 'none':
      findings.append(('FIX', 'EOL', 0, 'line endings changed %s -> %s; restore %s (real edits: %d lines)'
                       % (before, after, before, len(touched))))

  # JFREE: the discarded logger.
  if re.search(r'^\s*import\s+org\.jfree\.util\.Log\s*;', text, re.M):
    calls = [line_of(m.start()) for m in re.finditer(r'(?<![\w.])Log\s*\.\s*(?:error|warn|info|debug)\s*\(', text)]
    findings.append(('FIX', 'JFREE', calls[0] if calls else 0,
                     'org.jfree.util.Log imported, %d call(s) discarded at runtime (lines %s); move them to '
                     'the class slf4j LOG' % (len(calls), ', '.join(map(str, calls)) or '-')))

  # Log calls: placeholder arity, concatenation, method calls in arguments.
  for m in LOG_CALL.finditer(text):
    end, args = scan_call(text, m.end())
    ln = line_of(m.start())
    call_lines = set(range(ln, line_of(end) + 1))
    if not args or not args[0]:
      continue
    fmt, rest = args[0], args[1:]
    lits = literals(fmt)
    if lits and re.search(r'\+', outside_literals(fmt)):
      if call_lines & touched:
        findings.append(('REVIEW', 'LOG-CONCAT', ln, 'message built with +; use {} placeholders'))
    elif lits and not outside_literals(fmt).strip('" '):
      holders = sum(lit.count('{}') for lit in lits)
      n = len(rest)
      ok = n == holders or (n == holders + 1 and THROWABLE.match(rest[-1] or ''))
      if not ok:
        findings.append(('FIX', 'LOG-ARGS', ln, '%d placeholder(s) for %d argument(s)%s'
                         % (holders, n, ' (last is not a throwable)' if n > holders else '')))
    if call_lines & touched:
      for arg in rest + ([fmt] if re.search(r'\+', outside_literals(fmt)) else []):
        for cm in METHOD_CALL.finditer(outside_literals(arg)):
          receiver = cm.group(1)
          if receiver != ')' and SAFE_RECEIVER.match(receiver):
            continue
          findings.append(('REVIEW', 'LOG-NULL', ln, 'prove non-null: %s' % arg.strip()))
          break
    for lit in lits:
      if looks_spanish(lit):
        findings.append(('FIX', 'SPANISH', ln, 'log message: "%s"' % lit[:90]))
        break

  # NULL-LIST: a collection method that returns null. Callers chain .stream() / .size() on it and fail
  # with an NPE the day the query finds no rows (srf_idos, 2026-10-05). Only methods this change touches
  # fail the gate; the legacy ones in the same file are counted, not forced into an unrelated change.
  legacy = []
  for name, first, last, nulls in null_collection_methods(text):
    if set(range(first, last + 1)) & touched:
      findings.append(('FIX', 'NULL-LIST', nulls[0], '%s() returns null; return an empty collection '
                       '(Collections.emptyList() or the query result) and check its callers' % name))
    else:
      legacy.append(name)
  if legacy:
    findings.append(('REVIEW', 'NULL-LIST', 0, '%d untouched collection method(s) in this file still return '
                     'null: %s' % (len(legacy), ', '.join(legacy[:6]) + (' ...' if len(legacy) > 6 else ''))))

  # Spanish comments anywhere in the file: the rule is to translate what you come across.
  spanish = [(n, c) for n, c in comment_fragments(text) if looks_spanish(c)]
  for n, c in spanish[:MAX_SPANISH_LINES]:
    findings.append(('FIX', 'SPANISH', n, 'comment: %s' % c[:90]))
  if len(spanish) > MAX_SPANISH_LINES:
    findings.append(('FIX', 'SPANISH', 0, '... and %d more Spanish comment line(s)' % (len(spanish) - MAX_SPANISH_LINES)))

  print('=== %s%s' % (path, ' (new file)' if is_new else ''))
  if not findings:
    print('  ok')
  for level, tag, ln, msg in sorted(findings, key=lambda f: (f[0] != 'FIX', f[2])):
    print('  %-6s %-10s %s%s' % (level, tag, ('L%d  ' % ln) if ln else '', msg))
    fail = fail or level == 'FIX'

sys.exit(1 if fail else 0)
PY
