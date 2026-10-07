---
name: marlo-verify
description: "Verification gate for the MARLO repository (marlo-web / marlo-data, Struts 2 + Spring MVC, FreeMarker views): prove a Java change really compiles with a clean recompile, run Checkstyle through the invocation that actually works here, confirm CSS/JS cache-busting params were bumped, and run the Java hygiene check (flipped CRLF line endings, the discarding org.jfree.util.Log logger, slf4j placeholder mismatches, log arguments that can NPE, Spanish comments and log messages, collection methods that return null), run tests with -am, and catch undoubled apostrophes in edited .properties. Use this before claiming that a MARLO change compiles, builds, is Checkstyle-clean, is ready to commit, or is done, and any time a .css or .js file under marlo-web was edited. Do not report a MARLO change as verified without it: plain `mvn compile` returns BUILD SUCCESS on code that does not compile, and `mvn checkstyle:check` cannot run in this checkout at all."
---

# MARLO verification gate

Several failure modes in this repo make ordinary verification lie. Each gate below exists
because it already produced a broken commit or a change that never reached users. Run the
gates that apply to what you touched, then say which ones you ran — an unqualified "it
builds" is what this skill exists to prevent.

The scripts live in `scripts/` next to this file and must run from inside the MARLO checkout.

## Gate 1 — Java changes: clean recompile

```bash
.claude/skills/marlo-verify/scripts/clean-compile.sh
```

Wipes `target/classes` and runs `mvn -o -pl marlo-data,marlo-web -am test-compile -DskipTests`.
Pass a module list as the first argument for a narrower change (`clean-compile.sh marlo-data`).

**Why the wipe matters:** Maven compares each source file against its existing `.class` and
skips what it thinks is unchanged. `CrpProgamRegionsAction` once referenced `BaseAction`'s
private `LOG` — a plain `mvn compile` reported BUILD SUCCESS, and the broken commit reached
`origin`. `mvn clean` on its own is not a substitute; it can fail here with "Failed to delete
target/classes", which is why the script does `rm -rf` first.

A healthy full run compiles **~2403 files in marlo-data and ~1043 in marlo-web**. The script
warns when marlo-web compiles far fewer, because that means Maven reused stale classes and the
result proves nothing about your change. Treat that warning as a failed gate, not noise.

## Gate 2 — Java changes: Checkstyle

```bash
.claude/skills/marlo-verify/scripts/checkstyle.sh --baseline <changed files...>
```

`mvn -pl marlo-web checkstyle:check` **cannot run in this checkout**: maven-checkstyle-plugin
2.9.1 against checkstyle 8.18 throws `NoSuchMethodError: Checker.setClassloader`. That is a
plugin/dependency mismatch and says nothing about the code, so don't report it as a code
problem. The script calls checkstyle 8.18 directly with `configuration/marlo-checkstyle.xml`,
resolving the dependency tree through Maven once and caching it (hand-picking jars fails —
8.18 also needs antlr 2.7.7, commons-logging and Saxon-HE).

`--baseline` checks the same files as they exist at HEAD and prints both counts. Use it: this
codebase carries a lot of pre-existing violations and only the **delta** is yours.
`BaseAction.java` alone is 9 violations at HEAD, including `FileLength` (9402 lines vs the 3500
limit) and several `MethodLength` hits. Reporting those as "introduced by this change" is wrong.

Rules worth knowing before you write the code: 120-char lines, 3500-line files, 150-line
methods, 2-space indent, braces on the same line, mandatory blocks for `if/while/for/do`.

## Gate 3 — CSS or JS edited under marlo-web

```bash
.claude/skills/marlo-verify/scripts/cache-bust-check.sh
```

With no arguments it inspects every `.css`/`.js` changed against HEAD, finds each FTL that
references it, and flags any reference whose `?YYYYMMDD` param is stale or missing.

**Why:** without the bump, browsers and the CDN keep serving the previous asset, so a correct
source change silently never reaches users or the reviewer. Bump it in the same change, without
being asked. Convention is `?YYYYMMDD`, with a suffix for a second bump the same day
(`?20250717-1`). Add a param when the reference has none.

A shared stylesheet such as `annualReportGlobal.css` is included from many views — bump **every**
reference the script lists, not just the first. These FTLs often use CRLF endings, so a
`perl -0pi` pattern matching on `\n` silently does nothing; edit the line with `sed` instead.

Typical reference, at the top of the view:

```
[#assign customCSS = [ "${baseUrlMedia}/css/admin/locations.css?20260820" ] /]
```

## Gate 4 — Java changes: hygiene check

```bash
.claude/skills/marlo-verify/scripts/java-hygiene-check.sh
```

With no arguments it checks every `.java` changed against HEAD (staged, unstaged and untracked).
It catches what a clean compile and Checkstyle both let through, including collection methods that
return null. `FIX` lines fail the gate
(exit 1). `REVIEW` lines need you to trace the code yourself: a heuristic cannot prove that a
receiver is non-null.

| Tag | Level | Scope | What to do |
|---|---|---|---|
| `EOL` | FIX | file | Line endings flipped vs HEAD. Restore the original ending — see below |
| `JFREE` | FIX | file | `org.jfree.util.Log` has no `LogTarget` registered, so every call, exception included, is discarded. Move each call to the class's slf4j `LOG` |
| `LOG-ARGS` | FIX | whole file | `{}` count ≠ argument count. A trailing throwable is bound as the exception and does not consume a `{}` |
| `SPANISH` | FIX | whole file | Translate the comment or log message. **Never delete it**: it is another developer's reasoning |
| `LOG-NULL` | REVIEW | changed lines | A method call inside a log argument. Prove the receiver non-null (early return, `instanceof`, ternary) or guard it |
| `LOG-CONCAT` | REVIEW | changed lines | Message built with `+`. Convert to `{}` placeholders while you are in the file |
| `NULL-LIST` | FIX | touched methods | A method returning `List` / `Set` / `Collection` has `return null`. Return the query result or `Collections.emptyList()`, then check its callers |
| `NULL-LIST` | REVIEW | rest of the file | Count of untouched collection methods that still return null. Informational: fix them in their own change |

**EOL.** Some `.java` files are CRLF while their neighbours are LF (`SendMailS`,
`ThreadSendMail`, `RestConnectionUtil`, `AutoSaveReader`). A script that splits on `\n` and
rejoins rewrites every line: `SendMailS.java` once showed 1219 changed lines for a 16-line edit.
The finding prints the real edit count so you can compare. Prefer the Edit tool or `sed` on
specific lines for these files.

**LOG-NULL.** A log line that throws is worse than no log line. It usually sits in a `catch`,
and the new exception escapes the handler that was meant to contain the failure. A bare
variable is never the hazard, because slf4j prints `null`, so do not wrap plain arguments in
null checks. Calls on static utilities and on the caught throwable (`e.getMessage()`) are not
flagged. `BaseAction.getActualPhase()` looks total but returns null on three paths. Trace it;
do not assume.

**SPANISH.** CLAUDE.md rule 8 requires English. Translate the meaning, and keep identifiers and
structure markers (`1.`, `2. DELETION PASS:`). The only thing to delete instead of translating
is pure development scaffolding: logs that trace every field or loop iteration. Say which ones
you delete. Emoji and console markers (`✅`, `===`, `>>>`) are removed along with the
translation. `@author` lines are skipped, and an accented name alone is not flagged. Past 8
lines per file, the finding is collapsed into a count.

**NULL-LIST.** Most MARLO DAO methods were written as `if (list.size() > 0) return list; return null;`,
and callers chain `.stream()` straight onto the result. On 2026-10-05, after the innovation, OICR and
deliverable DAOs were fixed, 480 collection methods still returned null: 441 in DAOs, 34 in REST items, 3 in
managers and 2 in actions. Nothing fails until a table is empty: on
2026-10-05 an environment with no `srf_idos` rows took the innovation page down with an unhandled NPE.
Hibernate's `query.list()` never returns null, so `return list;` is the whole fix. Before changing an
existing method, check its callers for code that reads `null` as "not found", or that does `.get(0)` /
`findFirst().get()` after only a `!= null` check: an empty list sends those down a new path. The rule
fails only on methods the change touches. The legacy ones in the same file are counted as REVIEW and left
for their own change, so touching one line of a DAO does not force rewriting its neighbours.

What the check counts: a `return null;` that returns from the method itself. One inside a lambda body or an
anonymous class is ignored, and so are braces and `return null` inside comments, string literals and text
blocks. A `return null` inside a `case X -> { }` switch arm is counted, because it does leave the method. It
covers public, protected, private and package-private methods, generic ones (`<T> List<T>`), and
`java.util.List<...>` written in full. Caller review
is not optional: an FTL template that reads the result with a `!` default (`action.x()!{}`) behaves differently
on an empty list than on null.

`LOG-ARGS` and `SPANISH` scan the whole file on purpose. Pre-existing mismatches and Spanish
comments hide in the lines you did not touch, and the rule is to fix what you come across in a
file you are editing. If the file is under heavy debt, say so and fix it in its own commit
instead of burying it in the feature diff.

## Gate 5 — only when the task ran tests: run them with `-am`

```bash
mvn -o -pl marlo-web -am test
```

Never `mvn -pl marlo-web test`, which is what `CLAUDE.md` lists today. Without `-am`, Maven takes marlo-data
from `~/.m2`, so the tests run against whatever the last `install` left there. On 2026-09-28 this gave three
wrong results in one session: a `@Transactional` fix reported as not working because the jar had no
annotation, a failure on classes that did exist, and an earlier "271 green" that ran on a mixed classpath.

When the local server is up, run the tests in a throwaway `git worktree`. Gate 1 wipes `target/`, and so does
the run script. Only claim tests when the task actually wrote or exercised them.

## Gate 6 — .properties edited under marlo-web: apostrophes and braces

```bash
.claude/skills/marlo-verify/scripts/i18n-check.sh
```

With no arguments it checks only the lines added or changed in `marlo-web/src/main/resources/**.properties`
(`config/` is skipped). `FIX` lines fail the gate (exit 1).

**Why:** `MarloLocalizedTextProvider` extends `GlobalLocalizedTextProvider`, and Struts 6.8 passes every
value through `java.text.MessageFormat`, even when it has no arguments. A single `'` opens a quoted section
and is dropped: `can't be found.` renders as `cant be found.` The repo writes the apostrophe twice
(`can''t`, or `project\''s`, since properties loading turns `\'` into `'`). A `{` that is not a `{0}` or
`{1,date}` argument is parsed as a format element, which matters for CSS inside an HTML email shell (`REVIEW`).

The check covers changed lines only, on purpose. On 2026-09-30 the existing files already held about 340
undoubled apostrophes, 65 of them in `global.properties`. Those are a separate finding: report them, and do
not fold them into an unrelated change.

## Before reporting done — null and return-value pass

Scripts cannot prove this; do it by reading, on every change, without being asked:

- Every expression the change reads or returns cannot throw: null receivers (`getActualPhase()` can return an
  empty `Phase` with a null id), `get(0)` on an empty list, null elements before `findFirst()`, a null `Long`
  unboxed into a `long` parameter, `Long.parseLong` on user input.
- The "not found" return value is the same as before. Callers often read `null` as a signal: replacing it with
  an empty object in `DeliverableAction.getTraineesIndicator()` would have linked a transient
  `CrpProgramOutcome` to the deliverable. Grep the callers of every method whose return value changed.
- Say in the report which cases you checked.

## Reporting the result

State the verification you actually ran and what it printed — "clean recompile of
marlo-data + marlo-web: BUILD SUCCESS, 2403 + 1043 files" carries information; "it compiles"
does not. If a gate did not apply, say so. If one failed, show the failing lines rather than
summarizing them away.

Once the gates are green, the commit itself follows the `marlo-commit` skill.
