# Local PDF Report Engine — Execution Log

## Document Control

| Field | Value |
|---|---|
| Spec | `changes/local-pdf-report-engine` (CHG-LOCAL-PDF-REPORT-ENGINE-001) |
| Approval Mode | `pre-approved (Kenji Tanaka, 2026-11-20)` |
| Branch | `feature/local-pdf-report-engine` from `staging` at `2f67c5d2ab` |
| Commit policy | **No commits** — owner rule (ask before committing / pushing). Each closed task is staged with `git add` as a checkpoint; the next task's diff is `git diff` against the index |
| Time cap | Owner instruction: stop before 10.5 h of work → hard stop 07:00 on 2026-11-21 |
| Leader | `opus` (session) · Implementer wrapper `akili-implementer` (`sonnet`) · Reviewer wrapper `akili-reviewer` (`opus`) |
| Persona drift | `akili doctor --agents` (akili-specs v2.45.0): all four `.agents/*.md` personas `UNMARKED` (MARLO-customised, no section markers). `--fix` not run: it would rewrite constitutional files on a spec branch. Pending item for the owner |
| Budget (design §16) | 7 tasks · ~1,300 production + ~800 test LOC · 11 review rounds |

## Task Execution History

### CHG-LPRE-T01 — Dependencies, fonts and compatibility stylesheet — PASS

- **Date:** 2026-11-20 · **Attempts:** 1 · **Requirements:** NF-002, NF-006 · **Skills:** `marlo-verify` · **Effort:** medium
- **Attempt 1 — files changed:** `marlo-parent/pom.xml`, `marlo-web/pom.xml`, `marlo-web/src/main/resources/pdf/fonts/DejaVuSans.ttf`, `DejaVuSans-Bold.ttf`, `LICENSE-DejaVu.txt`, `marlo-web/src/main/resources/pdf/html-pdf-compat.css`
- **Implementer verification:** BEFORE/AFTER `mvn -o dependency:tree -pl marlo-web` — slf4j-api 1.7.22, commons-logging 1.2, asm 9.7.1, commons-lang3 3.14.0, commons-text 1.10.0, jsoup 1.15.4 unchanged; handlebars 4.5.8, openhtmltopdf-pdfbox 1.1.108, pdfbox 3.0.8 present; nashorn-core absent. Clean compile EXIT=0, 2430 / 1064 / 142 files. Font sha256 equal to the `node:23` originals.
- **Red run (greenfield, observed):** handlebars without exclusions → `[INFO] |  \- org.slf4j:slf4j-api:jar:1.7.22:compile`. **Prediction (2.0.x) not observed** — Maven keeps the nearer 1.7.22 path; the slf4j exclusion is defensive. The load-bearing exclusion is nashorn: without it `org.openjdk.nashorn:nashorn-core:jar:15.7:compile` appears under handlebars. Recorded as the observed value; the T01 falsifier as written cannot discriminate (Reviewer note).
- **Evidence re-run (Leader inline, non-author):** VERIFIED — same tree lines; clean compile EXIT=0 with 2430 / 1064 / 142 files.
- **Reviewer:** PASS (`akili-reviewer`, opus) — diff matches §3/§4/§7.3/§7.4, Hard rule 11 holds.
- **ADVISORY (not gating):** (1) RISK — `LICENSE-DejaVu.txt` is Debian's copyright file; upstream `LICENSE` also carries the Arev notice. (2) RELIABILITY — `#pageHeader span { white-space: nowrap }` applies to every template's header; check OICR/innovation headers against the baselines in T04/T07 → **forward pointer to T04**. (3) RELIABILITY — `-first` header/footer variants not in the F9 selector; no production template uses them (they use `-1`). (4) RESILIENCE — pinning slf4j-api in the parent would be stronger than the exclusion; follow-up only.
- **spawns:** implementer 9 calls, 89,922 tokens, ended complete; reviewer 15 calls, 57,618 tokens, ended complete
- **Gate:** `auto-approved (pre-approved mode)`
- **Running totals vs budget:** tasks 1/7 · LOC ~80 prod (+2 binaries) / ~1,300 · review rounds 1/11

### CHG-LPRE-T02 — Handlebars helpers with JavaScript semantics — in progress

- **Attempt 1 — files:** `marlo-web/src/main/java/org/cgiar/ccafs/marlo/utils/pdf/HandlebarsReportHelpers.java` (new, 819 lines), `marlo-web/src/test/java/org/cgiar/ccafs/marlo/utils/pdf/HandlebarsReportHelpersTest.java` (new), `marlo-web/src/test/resources/pdf/helper-vectors.json` (new, 1432 rows from Handlebars.js 4.7.8)
- **Skills:** `tdd`, `marlo-verify` · **Effort:** high
- **Implementer verification:** test exit 0, 5 tests; red run vs stub "differs from Handlebars.js on 1387 of 1433 rows: row 0 … expected=<Not Provided> actual=<>"; falsifier (ifEquals via String.valueOf) "differs … on 50 of 1432 rows: row 154 … {a=true, b=true} expected=<F> actual=<T>". Clean compile 2430/1065/143; Checkstyle 0.
- **Not Done / Assumptions (verbatim summary of the report's field):** (1) `parseJSON` on list/map follows real JS (coerce to string → `[]`), deviating from design §7.2 "passes through"; (2) JS null vs undefined via a private empty-list sentinel for present-null keys; a literal `null` in a template reaches helpers as undefined; (3) zero-argument helper calls indistinguishable from passing the context — the one such row (`{{#json}}x{{/json}}`, a JS internals dump) was removed (explains 1433 → 1432); (4) collator rules make whitespace/hyphen significant like ICU, not proven for all Unicode; (5) built-in `if`/`unless` treat `0.5` as empty (Handlebars.java) — scope left to T04/decision; (6) extra tests (map-only, number printing, rejects non-strings); (7) no `.length` support; (8) Java 8 syntax for Checkstyle. No blocker, no owed item → no continuation.
- **Execute-time spec edits (decisions):** `design.md` §7.2 `parseJSON` row amended to JS coercion (FN-003 governs; old text contradicted it). `design.md` §7.1 amended: null/undefined distinguishable; built-in `if`/`unless` JS truthiness, assigned to T04. After the review: §7.1 wording corrected (Handlebars.js `if` treats an empty array as false) and literal `null` must reach helpers as JS `null`.
- **Evidence re-run (Leader inline):** VERIFIED — same command, exit 0, 5 tests, 1432 rows.
- **Reviewer attempt 1: FAIL** — Issue 1: literal `null` in a template reaches helpers as `undefined` (Javadoc lines 55–56), no vector, not recorded; violates FN-003 / design §7.1; remediation (a) resolver returns `JS_NULL` for name `null` + Handlebars.js rows for `ifCond a "===" null`, `typeof null`, `json null`, `parseJSON null`, `yesNoHelper null`, each with key present-null and missing; or (b) record the deviation. Issue 2: one vector row disappeared between red run and green run → Leader adjudication: explained by Assumption (3) (`{{#json}}x{{/json}}` removed as a JS internals dump), recorded here. ADVISORY: `if` wording (applied to design); zero-param `or`/`ifOr` slot-0 context quirk; `_generator` path is a temp/personal path and the generator is not committed; `JsNull extends AbstractList` equality caveat; error rows accept any exception; parseJSON failures not logged.
- **spawns:** implementer 33 calls, 199,635 tokens, ended partial (assumptions only); reviewer 19 calls, 98,550 tokens, ended complete
- **Decision (Leader, 2026-11-20):** the built-in `if`/`unless` override (design §7.1 amendment) moved from T04 to T02 attempt 2 — it is engine configuration in the same class; `design.md` §7.1 and `tasks.md` T02 scope updated. Attempt 2 effort bumped to `xhigh`.

### CHG-LPRE-T03 — html-pdf compatibility step and resource loader — in progress

- **Attempt 1 — files:** `marlo-web/src/main/java/org/cgiar/ccafs/marlo/utils/pdf/HtmlPdfCompatibility.java` (new), `PdfResourceLoader.java` (new), tests `HtmlPdfCompatibilityTest.java` (9), `PdfResourceLoaderTest.java` (15)
- **Skills:** `error-handling-patterns`, `tdd`, `marlo-verify` · **Effort:** high
- **Implementer verification:** exit 0, 24 tests; clean compile 2430/1067/145; Checkstyle delta 0; hygiene: six LOG-NULL REVIEW lines traced. Red run: "anAllowedHostIsFetchedOnceAndOnlyWithTheLowercasedHost … AssertionError: actual array was null", "aRedirectIsNotFollowedAndYieldsNothing … expected:<1> but was:<0>" (compat stub NPEs not claimed). Falsifier (endsWith amazonaws.com): "disallowedUrisNeverReachTheConnectionFactory … https://x.s3.amazonaws.com/a.png must not be opened, recorded: [https://x.s3.amazonaws.com/a.png] expected:<0> but was:<1>".
- **Not Done / Assumptions (summary):** adapters not yet wired into a builder (T04); `data:` refused/allowed by URI length only; explicit `:443` allowed; failure cache capped 1024; budget counts fetch time only; concurrent misses both fetch; invalid/unitless heights → 0 (CSS-injection guard); with both `-1` and `-first`, only `-1` used; brace-matched `@page` stripping. No blocker, no owed item.
- **Evidence re-run (Leader inline):** VERIFIED — exit 0, 24 tests.
- **Reviewer attempt 1: FAIL** — (1) `PdfResourceLoader` and nested types public, violating design §9 (only `LocalPdfReportService` and `PdfRenderException` public) → make package-private. (2) Cache bound counts body bytes only; attacker-controlled query-string keys on tiny/zero-byte bodies grow the map without bound; failure map capped by count not bytes → count key size + per-entry overhead, do not cache empty bodies, cap URI length (~2,048), add a many-distinct-keys test. ADVISORY: T04 must register the HTTP factory for http+https and the refusing factory for file/jar/ftp and run an end-to-end SSRF test; read timeout is per read (trickle); `stripAtRules` and comments; uncaught RuntimeException from `open`; sibling: `HandlebarsReportHelpers` public (→ sent to the T02 rework as an addendum).
- **spawns:** implementer 25 calls, 164,296 tokens, ended partial (assumptions only); reviewer 11 calls, 84,030 tokens, ended complete

#### CHG-LPRE-T02 — attempt 2

- **Files:** same three files (rework in place); generator copied with the spec evidence to `docs/specs/changes/local-pdf-report-engine/spike/helper-vectors-generator/` (`generate.js`, `rows.js`; no absolute paths).
- **Implementer verification:** exit 0, 5 tests, **1853** vector rows (421 new). Red run vs attempt-1 code: "differs from Handlebars.js on 31 of 1860 rows: row 1481 template={{typeof null}} context={} expected=<object> actual=<undefined>". Falsifier (remove `|| NULL_LITERAL.equals(name)`): "differs … on 18 of 1853 rows: row 1481 … expected=<object> actual=<undefined>", restored. Class package-private (Leader addendum, design §9). Error rows assert the Handlebars.js message.
- **Not Done / Assumptions (verbatim summary):** (1) a data key literally named `null` shadows the literal — three such rows removed; (2) `{{#if}}`/`{{#unless}}` with no argument not detected — two rows removed; (3) the attempt-1 `{{#json}}x{{/json}}` removal stands. Both residual limits recorded in `design.md` §7.1 (execute-time edit, 2026-11-20).
- **Evidence re-run (Leader inline):** VERIFIED — exit 0, 5 tests; vectors contain no `/private/tmp` or `/Users/` path; class declared `final class` (package-private).
- **spawns:** implementer 30 calls, 160,475 tokens, ended partial (assumptions only)
- **Reviewer attempt 2: FAIL** — Issue 1: the literal-`null` fallback also makes a missing path segment named `null` (`x.null`) read as JS `null` instead of `undefined` — a third divergence not among the two accepted limits in §7.1; remediation is a spec edit (record it) — a code fix is unlikely to be possible because `ValueResolver.resolve(context, name)` cannot tell a path head from a later segment; dropping the fallback would reintroduce attempt-1 issue 1. ADVISORY: `HELPER_NAMES` unused; comment on `JsNull`'s `List` ordering; JSON `null` elements inside parsed lists read as `undefined`.
- **spawns:** reviewer 13 calls, 77,339 tokens, ended complete
- **Leader adjudication:** accepted the reviewer's remediation as a spec decision. Probe: `grep -oE '\{\{[^}]*\bnull\b[^}]*\}\}'` over the three production templates → 0. `design.md` §7.1 now lists three residual limits; `requirements.md` Decision Log entry added (owner to confirm). No code change → attempt 3 is a conformance re-review of the unchanged code against the amended spec.

#### CHG-LPRE-T03 — attempt 2

- **Files:** `PdfResourceLoader.java`, `PdfResourceLoaderTest.java` (rework in place); `HtmlPdfCompatibility*` unchanged.
- **Effort:** xhigh (bumped).
- **Implementer verification:** exit 0, 29 tests (9 + 20). Package-private class/nested types; entry cost key×2 + body + 64; empty bodies not cached; URIs > 2048 refused (blocked); periodic purge on insert; `download` catches IOException|RuntimeException. Red run vs attempt-1 behaviour: "Tests run: 20, Failures: 4, Errors: 1 — manyDistinctQueryKeysOnATinyBodyStayWithinTheCacheBound … expected:<12001> but was:<12000>; anEmptyBodyIsNotCached … expected:<2> but was:<1>". Falsifier (drop key size): "… expected:<12001> but was:<12000>", restored.
- **Not Done / Assumptions:** 200 + empty body returns an empty array, not cached; test-only accessor `accountedCacheBytes()`. No blocker.
- **Evidence re-run (Leader inline):** VERIFIED — exit 0, 29 tests; `class PdfResourceLoader` package-private.
- **spawns:** implementer 14 calls, 131,887 tokens, ended partial (assumptions only)
- **Reviewer attempt 2: PASS** (`akili-reviewer`, opus) — both findings closed; SEC-001 rows all assert on the recording double; URI rebuilt from parsed parts so the checked host is the contacted host.
- **ADVISORY (not gating):** per-read timeout allows a trickling allowlisted server to exceed the §7.5 overrun → check elapsed vs `FetchBudget` inside the read loop (**forward pointer to T04**); empty 200 bodies refetched each render; concurrent misses both fetch.
- **Forward pointers carried to T04 (Leader):** (a) wire `httpStreamFactory` for both `http` and `https` and the refusing factory for `file`, `jar`, `ftp`; confirm `data:` images still load; one end-to-end SSRF test through the real renderer (`<img src="file:///etc/passwd">`, `<link href="http://169.254.169.254/">`) asserting recorded connections (T03 review 1 advisory); (b) T01 advisory — `#pageHeader span { white-space: nowrap }` applies to all templates: compare OICR/innovation headers against the baselines.
- **Final status:** PASS on attempt 2 · **Gate:** `auto-approved (pre-approved mode)`
- **spawns (attempt 2):** reviewer 9 calls, 70,813 tokens, ended complete

### CHG-LPRE-T05 — Specificity migration and constants — PASS

- **Date:** 2026-11-20 · **Attempts:** 1 · **Requirements:** DA-001, FN-007 · **Skills:** `marlo-migration`, `marlo-verify` · **Effort:** high
- **Files:** `marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java` (+1 line, :179), `marlo-web/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java` (+1 line, :185), `marlo-web/src/main/resources/database/migrations/V2_6_0_20261120_2222__AddLocalPdfReportEngineSpecificity.sql` (new; timestamp from `date`)
- **Implementer verification:** throwaway copy `lpre_t05_tmp`: expected 38 units (types 1/3/4); red (before) = 0 parameters rows; run 1 → 3 / 38 / 0 non-false; run 2 → unchanged; empty copy → 3 / 0; user-3-absent → 38 rows with NULL audit columns; falsifier (guardless custom insert) 38 → 76; throwaway DBs dropped. Clean compile 2430/1067/145; Checkstyle delta 0; consumers grep before none / after the 2 constants + migration.
- **Evidence re-run (Leader inline):** VERIFIED — separate throwaway copy `lpre_verify_tmp`: expected 38; before 0/0/0; run1 3/38/0; run2 3/38/0; dropped (schemata count 0); constants equal key; no hardcoded `global_unit_id`.
- **Reviewer:** PASS (`akili-reviewer`, opus) — matches §5, Hard rules 4/5, exemplar clause for clause; no issues.
- **Assumption:** description and justification wording chosen by the Implementer.
- **spawns:** implementer 17 calls, 110,701 tokens, ended complete; reviewer 10 calls, 48,502 tokens, ended complete
- **Gate:** `auto-approved (pre-approved mode)`
- **Running totals vs budget:** tasks 3/7 closed (T01, T03, T05) + T02 HALT · LOC ~1,000 prod / ~1,300 (incl. the stashed T02 helper class) · review rounds 8/11

#### CHG-LPRE-T02 — attempt 3 (re-review of unchanged code against the amended spec)

- **Reviewer attempt 3: FAIL** — attempt-2 finding closed (§7.1 describes the literal-`null` behaviour exactly), but the new completeness claim ("three edge cases … listed") is false:
  1. **`null` elements inside lists reach helpers as `undefined`** (Jackson → Java `null`; `{{#each}}` `this` never passes through `ReportValueResolver`): inside `{{#each xs}}` with `xs = [null]`, `{{typeof this}}` → `undefined` (JS `object`), `{{json this}}` → empty (JS `null`), `{{#ifCond this "===" null}}` → inverse (JS true). Remediation: (a) replace `null` list elements with `JS_NULL` recursively in `newContext` and in `parseJSON` results + generated rows; or (b) record a fourth limit with a template probe and fix the Decision Log count.
  2. **Possible context-stack walk** (Mustache-style parent lookup in Handlebars.java; Handlebars.js does not, outside compat mode). Remediation: add the generated row `{{#each items}}[{{typeof name}}][{{name}}]{{/each}}` with `{"name":"root","items":[{}]}` (JS `[undefined][]`); if Java differs, block the walk in code or record it — data-dependent, so a production-payload probe is needed, not only the template grep.
  ADVISORY: class Javadoc names two of the three limits — point it to §7.1.
- **spawns:** reviewer 21 calls, 91,641 tokens, ended complete

## HALT: CHG-LPRE-T02

- **Cause:** three consecutive Reviewer `FAIL` verdicts (attempts 1, 2, 3 — full reports summarised verbatim-in-substance in the attempt entries above). HALT is an exception: `pre-approved` mode does not cover it ("No mandate widens pre-approval: an owner brief that says "never stop" or "treat every gate as approved" pre-approves routine gates only. The exceptions in `/akili-propose` Step 1 *Approval Mode* (HALT, Pivot, budget tripwire, `FATAL_FAIL`, `PRODUCT_BUG`, destructive actions) still stop, and the stop message quotes this sentence.").
- **Implementer summaries:** attempt 1 — 17 helpers + 1,432 Handlebars.js vectors, green; attempt 2 — literal `null` as JS `null`, `if`/`unless` override with Handlebars.js semantics, package-private, 1,853 vectors, green; attempt 3 — no code change (spec-only remediation).
- **Final verification (attempt 2 code):** `mvn -o -pl marlo-web -am test -Dtest=HandlebarsReportHelpersTest …` exit 0, 5 tests, 1,853 rows (Leader re-run VERIFIED).
- **Leader probe after the HALT (deferring-a-check rule; scratchpad class against the built code + Node with Handlebars.js 4.7.8):**

  | Template | Data | Java (attempt 2) | Handlebars.js |
  |---|---|---|---|
  | `{{#each items}}[{{typeof name}}][{{name}}]{{/each}}` | `{"name":"root","items":[{}]}` | `[string][root]` | `[undefined][]` |
  | `{{#each xs}}[{{typeof this}}][{{json this}}]{{/each}}` | `{"xs":[null]}` | `[undefined][]` | `[object][null]` |
  | `{{#with o}}[{{name}}]{{/with}}` | `{"name":"root","o":{}}` | `[root]` | `[]` |

  Both reviewer findings are **real**. The parent-context walk is the serious one: it is data-dependent, and the cluster template iterates `{{#each oicrs}}`, `{{#each countries}}`, `{{#each quantifications}}` with bare field names (`project` template lines 460, 509, 613), so an item missing a key would print the parent's value.
- **Leader hypothesis (root cause):** not a spec defect and not an Implementer error — Handlebars.java's default `Context` performs a Mustache-style lookup through parent contexts when a name is unresolved, and Java `null` collapses JS `null`/`undefined` for list elements. Both are fixable in `HandlebarsReportHelpers` (a resolver/context that stops at the current scope unless the path uses `../` or `@root`, and recursive `JS_NULL` substitution for list elements), with generated vectors for each.
- **Rollback (tree held other PASSed work → pathspec only, reversible):** `git stash push -u -m "HALT CHG-LPRE-T02 attempt 3 — local-pdf-report-engine (restore with git stash pop)" -- marlo-web/src/main/java/org/cgiar/ccafs/marlo/utils/pdf/HandlebarsReportHelpers.java marlo-web/src/test/java/org/cgiar/ccafs/marlo/utils/pdf/HandlebarsReportHelpersTest.java marlo-web/src/test/resources/pdf/helper-vectors.json` → `stash@{0}`. Stash used instead of `git clean` so ~1,000 lines and 1,853 vectors are recoverable. Unattributed paths: none. Post-restore `git status --porcelain`: only T01/T03/T05 changes and the spec folder. Clean compile of the remaining tree: EXIT=0, 2430/1066/144.
- **Blocked downstream:** T04 (depends on T02), T06 (depends on T04), T07.
- **Question for the owner:** approve a fourth T02 attempt that (1) blocks the parent-context lookup and (2) maps `null` list elements to JS `null`, each proven by Handlebars.js-generated rows — restoring the stashed files as its starting point — and then continue T04 → T07? Or prefer recording these as accepted limits (not recommended: the parent walk can print wrong data)?

### HALT resolution: CHG-LPRE-T02 — owner decision 2026-11-23

- **Owner (Kenji Tanaka), 2026-11-23:** "sí, aprueba el cuarto intento de T02". Fourth attempt approved: block the parent-context lookup and map `null` list elements to JS `null`, each proven by Handlebars.js-generated rows, starting from the stashed attempt-2 code.
- **Restore:** `git stash pop stash@{0}` → the three T02 files are back, untracked. Closed work committed beforehand at the owner's request: `c5fbf857d0` (spec), `8f8bb78cea` (T01), `73681d5bed` (T03), `d51a4b5164` (T05).
- **Execute-time spec edit:** `design.md` §7.1 — lookup does not walk parent contexts; `null` list elements are JS `null`.
- **Effort ceiling:** held `xhigh` (T2 ceiling); Reviewer stays `opus`, distinct from the `sonnet` Implementer.

#### CHG-LPRE-T02 — attempt 4 (owner-approved)

- **Effort:** xhigh (ceiling held) · **Skills:** `tdd`, `marlo-verify`
- **Files:** `HandlebarsReportHelpers.java` (`ScopedContext extends Context`: unprefixed names resolve only in the current scope; `../`, `this.`, `@root`, `@index` via `super.get`; replicated `root()`; `@first`/`@last` as booleans; recursive `JS_NULL` for `null` list elements and map values in `newContext` and `parseJSON`; block helpers get `{}` as `this` on a `null` context, as in Handlebars.js), `helper-vectors.json` (2,295 rows, +442), generator `rows.js` (spec copy in sync). Test class unchanged.
- **Implementer verification:** exit 0, 5 tests; clean compile 2430/1067/145; Checkstyle delta 0; hygiene ok. Red run vs attempt-2 code: 117 of 2,298 rows failing — "row 1853 {{#each items}}[{{typeof name}}][{{name}}]{{/each}} {name=root, items=[{}]} expected=<[undefined][]> actual=<[string][root]>"; "row 1854 {{#each xs}}[{{typeof this}}][{{json this}}]{{/each}} {xs=[null]} expected=<[object][null]> actual=<[undefined][]>". Falsifier (force every lookup through `super.get`): "differs … on 38 of 2295 rows: row 1853 … expected=<[undefined][]> actual=<[string][root]>", restored (`cmp`).
- **Template diff (production templates + reference payloads, Java vs Handlebars.js):** identical after removing whitespace for all three; differences are standalone-tag line breaks and one indentation (innovation line 453).
- **Not Done / Assumptions (verbatim summary):** block-parameter scope leak — three generated rows Java cannot match were removed and reported (`{{#each items as |it|}}[{{it.name}}][{{name}}]{{/each}}`, `{{#each items as |it|}}[{{typeof name}}]{{/each}}`, `{{#with o as |p|}}[{{typeof name}}]{{/with}}`); `null` map values also converted to `JS_NULL` (rows 2063–2085). Leader: templates use no block parameters (`grep -cE 'as \|'` → 0 on all three) → recorded as the fourth accepted limit in `design.md` §7.1 and the Decision Log (execute-time edits, 2026-11-23). No blocker, no owed item.
- **Evidence re-run (Leader inline):** VERIFIED — exit 0, 5 tests, 2,295 rows; whitespace-stripped hashes equal for oicr/innovation/project.
- **spawns:** implementer 40 calls, 213,662 tokens, ended partial (reported limits)
- **Reviewer attempt 4: FAIL** — both attempt-3 findings closed in code (scoping, `JS_NULL` elements, faithful `root()`/path-chain replication, thread-safe). New issue: the scoping also confines **data variables** — `@index`, `@key`, `@first`, `@last` are lost inside a scope nested within `{{#each}}` (`{{#each items}}{{#with sub}}[{{@index}}]{{/with}}{{/each}}` → `[]`, Handlebars.js `[0]`; and a block helper over a `null` item renders a new `{}` child, so `{{#each xs}}{{#ifEquals this null}}{{@index}}{{/ifEquals}}{{/each}}` with `xs=[null]` → empty, JS `0`). Not among the four §7.1 limits, no vector. Remediation: send `@`-prefixed heads (except `@root`) up the parent chain; add Handlebars.js rows; falsifier. ADVISORY: partials would still walk parents (no partials in the templates: `grep -c '{{>'` → 0 on all three); class Javadoc lists three limits instead of four; comment on `ReportValueResolver`'s `containsKey` branch; `withJsNulls` deep copy per render.
- **Leader probe (production impact):** `@index`/`@last` appear in innovation lines 587, 628, 671, 833 and project line 1377; all sit directly inside `{{#each}}` or inside `{{#ifEquals}}`/`{{#unless}}` on a non-null item, which keeps the same context → not affected in practice; only a `null` list item under a block helper would lose `@index`. Parity defect, not a live one.
- **spawns:** reviewer 19 calls, 101,118 tokens, ended complete
- **Status:** the owner approved exactly one further attempt after the HALT; attempt 4 failed → stop and ask the owner (no fifth attempt without approval).
- **Owner (Kenji Tanaka), 2026-11-23:** "sí, aprueba el quinto intento de T02" — fifth attempt approved: `@`-prefixed data variables (except `@root`) resolve through the parent chain as in Handlebars.js; plain names stay confined.

#### CHG-LPRE-T02 — attempt 5 (owner-approved)

- **Effort:** xhigh (ceiling held) · **Files:** `HandlebarsReportHelpers.java`, `helper-vectors.json` (2,396 rows, +101, none removed), generator `rows.js` (in sync, `cmp`). Test class unchanged.
- **Mechanism:** `@`-prefixed heads (except `@root`) go through `super.get` and walk the parent chain; plain names stay confined. **Second defect found and fixed:** `body()`/`otherwise()` decided the null-context case from the helper's first argument instead of `this` (`options.context.model()`), so a missing/null first argument blanked the body — `{{#if r}}R{{else}}[{{d}}]{{/if}}` with `{d:7}` printed `[]` (JS `[7]`); affected `if`, `unless`, `ifEquals`, `ifNotEquals`, `ifCond`, `ifAnd`, `ifOr`. Attempt-4 rows could not see it (expected and actual both `[]`). With enlarged payloads it dropped real content: innovation Poverty/Climate Change lines inside `ifOr this.foodSecurity …`, project `{{#if reference}}…{{else}}{{#if deliverableId}}`.
- **Implementer verification:** exit 0, 5 tests; clean compile 2430/1067/145; Checkstyle delta 0; hygiene ok. Red (attempt-4 code): "differs … on 24 of 2350 rows: row 1908 {{#each xs}}{{#with o}}[{{@index}}][{{@first}}][{{@last}}]{{/with}}{{/each}} … expected=<[0][true][false][1][false][false][2][false][true]> actual=<[][][][][][][][][]>"; body() red: "row 1963 {{#if r}}R{{else}}[{{d}}]{{/if}} context={d=7} expected=<[7]> actual=<[]>". Falsifier (confine `@` again): "differs … on 20 of 2396 rows: row 1908 …", restored.
- **Template diff, Java vs Handlebars.js, whitespace stripped:** enlarged payloads (lists of 2–3 items, `@index`/`@last` lines executed) — innovation 40,942 = 40,942, oicr 38,851 = 38,851, project 20,574 = 20,574, identical; reference payloads identical too.
- **Not Done / Assumptions:** `@key` rows do not discriminate (same key sets); `@index` inside block-parameter blocks falls under limit 4. No blocker.
- **Evidence re-run (Leader inline):** VERIFIED — exit 0, 5 tests, 2,396 rows; `rows.js` in sync; whitespace-stripped hashes equal for the three templates.
- **spawns:** implementer 48 calls, 177,617 tokens, ended complete
- **Reviewer attempt 5: FAIL** — attempt-4 finding and the `body()` defect verified closed (rows 517–575; thread-safe; Javadoc lists the four limits). New unlisted divergence (dates from attempt 4): `../` inside a block-helper body over a `null` item — the `{}` child adds a context level that Handlebars.js does not add. ADVISORY: `@odd`/`@even` exist in Handlebars.java only; `@key` inside `each` over a list untested; Decision Log count stale.
- **Leader probe (Java attempt-5 code vs Handlebars.js 4.7.8):** `{{#each xs}}{{#ifEquals this null}}[{{../n}}]{{/ifEquals}}{{/each}}` with `{"n":"r","xs":[null]}` → Java `[]`, JS `[r]`; `{{#each xs}}{{#safeEmpty}}{{../n}}{{/safeEmpty}}{{/each}}` → Java `Not Provided`, JS `r`; `{{#each xs}}[{{@odd}}][{{@even}}]{{/each}}` with `[1,2]` → Java `[][even][odd][]`, JS `[][][][]`; `{{#each m}}{{#each list}}[{{@key}}]{{/each}}{{/each}}` → both `[0][1]`. None of `../`, `@odd`, `@even`, `as |`, `{{>` occurs in the three production templates.
- **spawns:** reviewer 19 calls, 94,931 tokens, ended complete
- **Status:** fifth (last owner-approved) attempt failed → stop and ask the owner. Leader recommendation recorded below.
- **Leader assessment:** each review finds another Handlebars.java/Handlebars.js divergence in constructs the templates never use, because §7.1 claims a *complete* list of limits. Recommended change of strategy (spec amendment, owner decision): (1) fix the two cheap divergences found (`../` under a `null` item; hide `@odd`/`@even`); (2) redefine FN-003 parity as "the 17 helpers and the template subset the production templates use", proven by the vectors plus identical rendering of the three templates; (3) add a guard test that fails when a stored template uses a construct outside that subset (`../`, block parameters `as |`, partials `{{>`, `@odd`/`@even`, `null` path segments, argument-less `{{#if}}`), so a future template edit cannot silently hit an unsupported case.

## Pivot Record: CHG-LPRE-T02 — 2026-11-23

- **Blocker:** five review rounds; each found another Handlebars.java vs Handlebars.js divergence in template constructs no production template uses (latest: `../` under a `null` item; `@odd`/`@even`), because §7.1 claimed a complete list of limits.
- **Alternatives:** (a) keep chasing full parity (unbounded); (b) accept each new divergence as a listed limit (same chase, on paper); (c) **scope parity to a supported template subset and guard it** — chosen.
- **Owner decision (Kenji Tanaka, 2026-11-23):** "sí, aprueba el cambio de estrategia".
- **Spec amendments:** `requirements.md` FN-003 amended, FN-008 added, Decision Log entry; `design.md` §7.1 rewritten (template context, `if`/`unless`, scoping, supported subset + `ReportTemplateSubset` guard + render-time WARN in T04); `tasks.md` T02 scope extended. No TRD ADR affected.
- **Closure sweep:** forward — `grep -n "Four residual\|four limits\|Three residual\|three Handlebars"` over the spec folder → only historical execution entries remain (intentionally kept as history); backward — references to §7.1 in `tasks.md` (T02 scope) and `requirements.md` (Decision Log) updated.

#### CHG-LPRE-T02 — attempt 6 (pivoted scope)

- **Effort:** xhigh (ceiling held) · **Requirements:** FN-003 (amended), FN-008 (new)
- **Files:** `HandlebarsReportHelpers.java` (null-item body via `ScopedContext.nullItemBody` — no extra context level, keeps the item's parent, extension map and data; `@odd`/`@even` → `undefined`; Javadoc points to the subset guard), `ReportTemplateSubset.java` (new, package-private: `findUnsupported(String)` → partials, block parameters, parent paths, `null` path segments, `@odd`/`@even`, argument-less `if`/`unless`; ignores comments, escaped mustaches and string literals; bare `null` argument supported), `ReportTemplateSubsetTest.java` (new, 13 tests), `helper-vectors.json` (2,414 rows, +18, none removed), `src/test/resources/pdf/templates/{oicr,innovation,project}.html` (production templates), generator `rows.js` (in sync).
- **Implementer verification:** exit 0, 18 tests (5 + 13); clean compile 2430/1068/146; Checkstyle delta 0; hygiene ok. Red (attempt-5 code): "differs … on 14 of 2414 rows: row 2396 {{#each xs}}{{#ifEquals this null}}[{{../n}}]{{/ifEquals}}{{/each}} {n=r, xs=[null]} expected=<[r]> actual=<[]>"; "row 2404 {{#each xs}}[{{@odd}}][{{@even}}]{{/each}} expected=<[][][][]> actual=<[][even][odd][]>"; subset stub: "flagsOddAndEvenDataVariables … expected:<1> but was:<0>". Falsifier (subset ignores `../`): 5 of 13 failing incl. "aProductionTemplateEditedWithAParentPathIsFlagged … [] expected:<1> but was:<0>", restored.
- **Template diff (whitespace-stripped, Java = Handlebars.js):** enlarged payloads innovation 40,942, oicr 38,851, project 20,574; reference payloads 39,287 / 38,851 / 8,421 — all identical, no regression vs attempt 5.
- **Assumptions:** extra Handlebars.js rows (data variables / `@root` / `../../n` / `if`-`else` under a null item, `@odd` in nested scopes, `@key`/`@../key`); scanner follows the lexer (`{{! }}` ends at the first `}}`); known scanner edge: bracketed segments with dots/spaces, raw blocks. No blocker.
- **Evidence re-run (Leader inline):** VERIFIED — exit 0, 18 tests, 2,414 rows; `rows.js` in sync; test template fixtures byte-identical to the production dumps.
- **spawns:** implementer 40 calls, 167,342 tokens, ended complete
- **Reviewer attempt 6: FAIL** (first verdict under the pivoted contract) — runtime side conforms (null-item body keeps `../`, `@index`, data frame as Handlebars.js `wrapProgram`; `@odd`/`@even` undefined; no regression; thread-safe); guard completeness gap: argument-less `if`/`unless` missed when spelled `{{# if}}`, `{{#if includeZero = true}}` (spaces around `=`), or `{{^if}}`/`{{^unless}}`. Remediation: opener `^(?:[#^]|else)\s*(if|unless)\b`, collapse `\s*=\s*` before splitting, three test rows. ADVISORY: escaped-comment / `\\{{` scanner edges; data key named `null` invisible to a template scan (→ T04 payload check, `design.md` §7.1 amended); `isDataVariable` `startsWith("@root")` → `equals`.
- **spawns:** reviewer 12 calls, 90,372 tokens, ended complete
- **Leader decision:** the Pivot restarted the rework loop under the new contract; attempt 6 = post-Pivot attempt 1 of 3. Attempt 7 (post-Pivot 2) proceeds without a new owner stop. Forward pointer to T04: payload check for a key named `null`, reported in the subset WARN.

#### CHG-LPRE-T02 — attempt 7 (post-Pivot attempt 2)

- **Files:** `ReportTemplateSubset.java` (opener `^(?:[#^]\s*|else\s+)(if|unless)\b`, `\s*=\s*` collapsed before splitting), `ReportTemplateSubsetTest.java` (flagged: `{{# if}}`, `{{#if includeZero = true}}`, `{{^if}}`, `{{^unless}}`; not flagged: the same with an argument, `{{else if x}}`, `{{else unless b}}`, `{{^items}}`, `{{^iffy}}`), `HandlebarsReportHelpers.java` (`isDataVariable`: `!"@root".equals(name)`, advisory).
- **Implementer verification:** exit 0, 18 tests; clean compile exit 0; Checkstyle 0/0; hygiene ok. Red vs attempt-6 guard: "findings for {{# if}}x{{/if}}: [] expected:<1> but was:<0>". Falsifiers: opener reverted → "{{# if}}x{{/if}}: [] expected:<1> but was:<0>"; opener without `^` → "{{^if}}x{{/if}}: [] expected:<1> but was:<0>"; no `=` collapse → "{{#if includeZero = true}}x{{/if}}: [] expected:<1> but was:<0>"; all restored (`cmp`).
- **Evidence re-run (Leader inline):** VERIFIED — exit 0, 18 tests.
- **spawns:** implementer 14 calls, 105,494 tokens, ended complete
- **Reviewer attempt 7: FAIL** — attempt-6 finding closed (all four spellings flagged, falsifiers convincing). Two remaining scanner misses of §7.1-listed constructs: (1) hash value that is a subexpression with whitespace (`{{#if includeZero=(eq a b)}}`, `{{#unless k=(h "x y")}}`) is miscounted as having a positional argument; (2) `\\{{…}}` (escaped backslash) is a real mustache in Handlebars.js but the guard's `(?<!\\)` lookbehind skips it — affects every construct. ADVISORY: `{{elseif}}` may lex as `{{else if}}` in Handlebars.js (`OPEN_INVERSE_CHAIN` has no word boundary) — settle with one vector.
- **spawns:** reviewer 10 calls, 59,407 tokens, ended complete
- **Leader decision:** attempt 8 = post-Pivot attempt 3 of 3 (last). Brief changes approach: the scanner must follow the Handlebars.js lexer grammar (`handlebars.l` in node_modules) rather than ad-hoc regexes, and a **differential test** compares the guard's verdict with Handlebars.js on generated spellings, so gaps are found by the machine before review.

#### CHG-LPRE-T02 — attempt 8 (post-Pivot attempt 3 of 3)

- **Approach (Leader brief):** guard correctness measured against Handlebars.js itself instead of hand-patched regexes.
- **Files:** `ReportTemplateSubset.java` (rewritten: `Lexer` = port of the 45 Handlebars.js 4.7.8 lexer rules and the `mu`/`emu`/`com`/`raw` states incl. `\{{` vs `\\{{`; `Reader` = grammar-faithful mustache reader for paths, hash `ID =`, subexpressions, block params), `ReportTemplateSubsetTest.java` (13 kept + `agreesWithTheHandlebarsJsAstOnEveryVector`), `subset-vectors.json` (new, 7,712 rows from a classifier over the real `Handlebars.parse()` AST; 548 KB), `subset-generate.js` (new, spec evidence folder). `HandlebarsReportHelpers.java` untouched.
- **Implementer verification:** exit 0, 19 tests (5 + 14); also 0 disagreements on 18,556 fresh rows from another seed; clean compile 2430/1068/146; Checkstyle 0; hygiene ok. Red vs attempt-7 scanner: "949 of 7712 rows disagree with Handlebars.js" incl. `{{#if k=(h "x y")}}x{{/if}} expected=[if without argument] actual=[]`, `{{#if includeZero=(eq a b)}}x{{/if}} expected=[if without argument] actual=[]`, `\\{{../n}} expected=[parent path] actual=[]`. Falsifier (`\\{{` treated as escaped): "337 of 7712 rows disagree … template=\\{{> p}} expected=[partial] actual=[]", restored.
- **Assumptions:** decorators count as `partial`; only block `if`/`unless` flagged (not `{{if}}` mustache or `(if)` subexpression); a final `null` segment that Handlebars.js cannot parse stays flagged (parse errors excluded from vectors).
- **Execute-time spec edit:** `design.md` §7.1 guard description updated (lexer port + AST-differential vectors).
- **Evidence re-run (Leader inline):** VERIFIED — exit 0, 19 tests; no absolute paths in the generator or vectors.
- **spawns:** implementer 35 calls, 208,445 tokens, ended complete
- **Reviewer attempt 8: PASS** (`akili-reviewer`, opus) — attempt-7 findings closed; lexer port checked rule by rule against `parser.js` (44 rules, states, actions) and `helpers.js` (`preparePath`, `id`); the AST oracle maps every §7.1 construct correctly; no construct under-generated (smallest category 196 rows); test strict both ways. ADVISORY: raise the `rows.size() >= 500` floor; write U+2028/U+2029 as escapes; inline `{{if x}}` / `{{unless x}}` (non-block) not guarded — possibly divergent, outside §7.1, unused.
- **spawns:** reviewer 17 calls, 107,759 tokens, ended complete
- **Final status:** PASS on attempt 8 (owner-approved continuations after the HALT; Pivot at attempt 5) · **Requirements:** FN-003 (amended), FN-008 · **Gate:** see the budget tripwire below — not auto-approved.

## Budget tripwire — after CHG-LPRE-T02

- **Budget (design §16):** 7 tasks · ~1,300 production + ~800 test LOC · 11 review rounds.
- **Actuals:** tasks 4/7 closed (T01, T02, T03, T05) · production LOC ≈ 2,600 (`utils/pdf` + POMs + migration + constants) · test LOC ≈ 975 plus generated vectors (2,414 helper rows, 7,712 subset rows) · **review rounds 12** (T01 1, T02 8, T03 2, T05 1).
- **Cause:** T02 — Handlebars.java's scoping and null handling diverge from Handlebars.js in ways the spike did not reveal; five rounds chasing parity, then the owner-approved Pivot (supported subset + guard) and three rounds hardening the guard into a Handlebars.js lexer port. Production LOC roughly doubled for the same reason (`HandlebarsReportHelpers` ~1,000 lines, `ReportTemplateSubset` ~600).
- **Remaining estimate:** T04 (render service) ~400 LOC + tests, T06 (call sites) ~120 LOC + tests, T07 verification → about 3–5 more review rounds.
- **Stop:** the tripwire is an exception — "No mandate widens pre-approval: an owner brief that says "never stop" or "treat every gate as approved" pre-approves routine gates only. The exceptions in `/akili-propose` Step 1 *Approval Mode* (HALT, Pivot, budget tripwire, `FATAL_FAIL`, `PRODUCT_BUG`, destructive actions) still stop, and the stop message quotes this sentence." Owner decision needed: raise the budget and continue with T04 → T07, or stop here.
- **Owner decision (Kenji Tanaka, 2026-11-23):** "sí, haz el commit y sigue con T04" — budget raised; continue T04 → T07. New tripwire: review rounds 17, production LOC ~3,200 (T04/T06/T07 estimate added to actuals).

### CHG-LPRE-T04 — LocalPdfReportService — in review

- **Effort:** xhigh · **Skills:** `error-handling-patterns`, `marlo-verify` · **Requirements:** FN-001, FN-002, FN-004, FN-008, NF-001–NF-005, SEC-001, SEC-002, OPS-001
- **Files (new):** `utils/pdf/LocalPdfReportService.java` (653 lines), `utils/pdf/PdfRenderException.java`, `LocalPdfReportServiceTest.java` (19 tests), `src/test/resources/pdf/payloads/*-payload.json`, `src/test/resources/pdf/images/` (29 PNGs, 1.1 MB, captured once from the two allowlisted S3 hosts and served by the test's connection double — no network in tests). No hooks added to committed classes.
- **Implementer verification:** full command exit 0, 67 tests; clean compile 2430/1070/147; Checkstyle delta 0; hygiene: 4 REVIEW lines traced safe. Page counts OICR 5 / innovation 6 / project 7 (production), fonts exactly DejaVuSans + DejaVuSans-Bold; render log "PDF render file=oicr-fixture.pdf outcome=ok bytes=552077 pages=5 elapsedMs=964 blockedUris=0". Red (stub): "AssertionError: not a PDF"; "anInvalidPayloadAndAFullQueueAlsoAnswerTheGeneric500: expected:<500> but was:<null>". Falsifier (dots-per-pixel 20): "oicr page count expected:<5> but was:<4>", restored. SSRF end-to-end through the real renderer: `file:`, `jar:`, `ftp:`, `http://` (incl. 169.254.169.254 and the allowlisted host over http), other hosts, CSS `@import`/`url()` → zero opens; allowlisted https image fetched once; `data:` images load. Deadline ≈30 s ± 2 s, queue empty after; saturation fails < 2 s; late completion WARN.
- **Forward pointer T01 (header nowrap):** headers match the Linux baselines (project "CLUSTER SUMMARY" at (50.6, 48.6) vs (50.0, 47.9)) → closed. **New observation → T07:** project pages 2+ body starts ~36 pt lower than the baseline (y≈149 vs ≈113); page count unchanged.
- **Defect in committed T03 code, worked around in T04:** OpenHTMLtoPDF `NaiveUserAgent.openStream` dereferences the stream factory result without a null check; the loader returns `null` for a refused/failed fetch → one failed image NPE'd the whole render. The service wraps both factories in `neverNull(...)` (an `FSStream` with a null stream = resource not found). Recorded; moving the fix into `PdfResourceLoader` would change its tests.
- **Deviations:** `Content-Length` also set; `Content-Disposition` file name sanitised (control chars, `"`, `\` → `_`).
- **Continuation (Leader, 2026-11-23):** `design.md` §7.4 amended — `css-parse` category to SEVERE (11 warnings per OICR render → 0); new test asserts it while a render ERROR still logs. 67 tests green after.
- **Assumptions:** subset WARN quotes up to 60 chars of the template tag per finding, max 5 findings.
- **Evidence re-run (Leader inline):** VERIFIED — exit 0, 67 tests, 0 `css-parse WARNING` lines in the run.
- **spawns:** implementer 74 calls, 270,978 tokens (incl. continuation), ended complete
- **Reviewer attempt 1 (conformance + reliability lens): FAIL** — rejected (queue full) and timed-out renders log only an INFO outcome line; `renderToResponse` answers 500 with no ERROR trace → violates AC-NF-003 ("fails with a logged error"), OPS-001, design §10. Remediation: ERROR (message only, no stack) for `rejected`/`timeout`; tests assert exactly one ERROR. Execution model, thread safety, css-parse SEVERE, FN-008 WARN, deviations and `neverNull` judged conforming. ADVISORY: direct test for a 404 allowlisted image and a 404 `<link>` stylesheet (CSS `getReader()` path); follow-up to move null→`NO_RESOURCE` into `PdfResourceLoader`; race between `future.get` timeout and `abandoned.set(true)` (check `isDone()`); post-shutdown calls report "queue full"; `RENDERER_LOGGERS` growth; no shutdown test.
- **spawns:** reviewer (conformance) 17 calls, 102,273 tokens, ended complete
- **Reviewer attempt 1 (security lens): FAIL** — SSRF wiring holds (resolver + http/https factory + refusing file/jar/ftp; base URI ignored; jsoup→W3C DOM, no XXE; `neverNull` opens nothing; e2e test asserts on recorded opens). Issues: (1) the 2 MB `data:` cap is probably bypassed for images — `PdfBoxUserAgent.getImageResource` decodes embedded base64 before the URI resolver (design §11, SEC-001); remediation: e2e test with an over-cap `data:` image, enforce in the DOM pass if it renders. (2) OpenHTMLtoPDF's own `load` logger (WARNING) may write full rejected URIs / malformed `data:` payloads to `catalina.out`; tests capture only logback, not JUL (design §10); remediation: JUL handler on every `XRLog` logger in the SSRF and compile-failure tests, raise `XRLog.LOAD` (and any URI-quoting category) to SEVERE if records appear. ADVISORY: decompression-bomb images (→ recorded as accepted risk in `design.md` §17); no payload size cap; move null→`NO_RESOURCE` into the loader (follow-up).
- **spawns:** reviewer (security) 11 calls, 107,012 tokens, ended complete
- **Leader adjudication:** both lens FAILs in scope (AC-NF-003/OPS-001/§10; SEC-001/§10/§11). Attempt 2: effort `xhigh` (ceiling held).

#### CHG-LPRE-T04 — attempt 2

- **Files:** `LocalPdfReportService.java`, `LocalPdfReportServiceTest.java` (committed classes unchanged; `PdfResourceLoader` edited only for a falsifier and restored).
- **Changes:** ERROR (message only) for `rejected`/`timeout`; timeout/completion race handled via `cancel(true)` result; distinct "shut down" message; `XRLog.LOAD` and `XRLog.EXCEPTION` → SEVERE.
- **Findings:** security item 1 **refuted** for OpenHTMLtoPDF 1.1.108 — an over-cap `data:` image renders 0 images in `<img src>`, `style="background-image:url(…)"` and `<td background>`; just-under-cap loads; falsifier (remove the loader's length check) → "an image over MAX_DATA_URI_CHARS reached the renderer … expected:<0> but was:<1>" — the loader's resolver cap is what holds; no DOM pass added. Security item 2 **confirmed** — before the change `com.openhtmltopdf.load | WARNING | Unable to load CSS from https://marlo-pdf-resources-dev…/SECRET-URI-MARKER.css` and `load`/`exception` WARNINGs quoting `data:image/png;base64,SECRETDATAMARKER`; after → zero WARNING-or-above records.
- **Red runs:** saturation "a rejected render must log exactly one ERROR (AC-NF-003): [] expected:<1> but was:<0>"; shutdown "PDF render rejected: the render queue is full". **Falsifiers:** ERROR branch disabled → same saturation failure; restored.
- **Tests:** 71 green (4 new: shutdown; over-cap/under-cap `data:`; failing resources with JUL capture on every `XRLog` logger; 404 image + 404 stylesheet via `neverNull`). Clean compile 2430/1070/147; Checkstyle 0; hygiene: 3 REVIEW traced.
- **Execute-time spec edit:** `design.md` §7.4 — `load` and `exception` categories SEVERE.
- **Not Done / Assumptions:** cancel/completion race has no direct test (needs an interleaving seam); `data:` inside `<style>` never loads (fails closed).
- **Evidence re-run (Leader inline):** VERIFIED — exit 0, 71 tests; no `load`/`exception` WARNING lines in the run; committed classes unchanged.
- **spawns:** implementer 37 calls, 176,066 tokens, ended complete
- **Reviewer attempt 2 (conformance + reliability): PASS** — AC-NF-003 finding closed (exactly one ERROR for rejected/timeout/shutdown, proven by red + falsifier); §7.4 amendments and §17 conforming. ADVISORY: `newSession()` before `try`; uncaught non-`PdfRenderException` RuntimeException path; deadline clock starts after parsing; misleading late WARN edge; rename `anInvalidPayloadAndAFullQueueAlsoAnswerTheGeneric500`; static `RENDERER_LOGGERS` growth.
- **Reviewer attempt 2 (security): FAIL** — item 1 (`data:` cap) refuted, item 2 fixed for `load`/`exception`; new: `general` category still at WARNING and OpenHTMLtoPDF's link manager likely logs user hrefs (`<a href="#missing">`, invalid URI) in full — unproven, untested (design §10). Remediation: add link cases to the JUL leak test, red first; deny-by-default (all categories SEVERE) if records appear. ADVISORY: SEVERE `exception`/`load` hides non-fatal fetch failures while §17 promises a "blocked/failed fetch count" → add `failedFetches`; untested CSS url vectors (`list-style-image`, `content:url()`, `border-image`, `@font-face`); hardcoded English `GENERIC_ERROR_MESSAGE` vs Hard rule 8.
- **spawns:** reviewers 13 + 17 calls, 105,345 + 102,092 tokens, ended complete
- **Leader adjudication:** security FAIL in scope (§10). Attempt 3 (last): deny-by-default for every XRLog category (`design.md` §7.4 superseding note), link cases in the leak test (red first), `failedFetches` on the INFO line (`design.md` §10 amended to match §17). `GENERIC_ERROR_MESSAGE`: kept as a fixed English string — the existing download path already answers with literal English (`MicroserviceReportAction.downloadPDFByURL`: "PDF not available after several attempts"); it is a Tomcat error-page reason, not a MARLO screen; recorded for the owner.

#### CHG-LPRE-T04 — attempt 3 (last)

- **Files:** `LocalPdfReportService.java` (every `XRLog` category SEVERE; `RENDERER_LOGGERS` keyed map; `newSession()` inside `try`; INFO line `… blockedUris={} failedFetches={}`), `LocalPdfReportServiceTest.java` (link cases in the leak test; assertion: no WARNING-or-above from ANY OpenHTMLtoPDF logger; all categories SEVERE; new `anAllowlistedFetchThatFailsIsCountedInTheRenderLineAndIsNotABlockedUri`; test renamed `anInvalidPayloadAnswersTheGeneric500`), `PdfResourceLoader.java` (committed T03 class, +17/−1: per-session concurrent `failed` set and package-private `getFailedFetchCount()`; `PdfResourceLoaderTest` green).
- **Red runs:** (a) attempt-2 code: "the renderer's own log quotes 'SECRET-URI-MARKER': com.openhtmltopdf.general | WARNING | Could not find valid target for link. Link href = #SECRET-URI-MARKER" — **leak confirmed** (only the `#` anchor case; spaced URL and `javascript:` produced no record); (b) before the counter: "PDF render file=inline-test.pdf outcome=ok bytes=984 pages=1 elapsedMs=434 blockedUris=0". **Falsifier:** `general` back to WARNING → same leak failure; restored.
- **Implementer verification:** exit 0, 72 tests; clean compile 2430/1070/147; Checkstyle delta 0; hygiene REVIEW lines traced.
- **Assumption:** `failedFetches` counts distinct failed allowlisted URIs per render (not attempts).
- **Evidence re-run (Leader inline):** VERIFIED — exit 0, 72 tests; zero OpenHTMLtoPDF WARNING lines in the run.
- **spawns:** implementer 19 calls, 145,427 tokens, ended complete
- **Reviewer attempt 3 (conformance + reliability): PASS** — §7.4 superseding note and §10 `failedFetches` met in practice; no regression; loader change additive. ADVISORY: `tasks.md` T04 scope/files drift (fixed by the Leader: "every `XRLog` category at SEVERE", `PdfResourceLoader.java` listed); no loader-level tests for the budget-spent / remembered-failure / counts-once branches of `getFailedFetchCount()`; `blockedUris` counts attempts while `failedFetches` counts distinct URIs; late-finish WARN lacks `failedFetches`; non-`PdfRenderException` RuntimeException before `parse` (carried).
- **Reviewer attempt 3 (security): PASS** — `general` leak closed (red + falsifier); `XRLog` registers exactly 12 categories at class load (constant pool checked), so no later category escapes; `failedFetches` logs a number only; loader allowlist decision intact. ADVISORY: set the strong reference before `setLevel` (weak JUL loggers); re-apply levels per render to remove the init-order dependency; untested CSS url vectors; PDFBox/Handlebars loggers outside the leak test's scope — record the boundary.
- **spawns:** reviewers 14 + 27 calls, 104,771 + 127,111 tokens, ended complete
- **Final status:** PASS on attempt 3 (both lenses) · **Requirements:** FN-001, FN-002, FN-004, FN-008, NF-001–NF-005, SEC-001, SEC-002, OPS-001

## Budget tripwire — after CHG-LPRE-T04

- **Raised budget (owner, 2026-11-23):** 17 review rounds · ~3,200 production LOC.
- **Actuals:** tasks 5/7 closed (T01–T05) · review rounds **18** (T01 1, T02 8, T03 2, T04 6 — three attempts × two parallel lenses, T05 1) · production LOC ≈ 3,330 (`utils/pdf` 3,176 + POMs, migration, constants) · test LOC ≈ 2,080 plus generated vectors and test images.
- **Cause:** T04 ran the mandated parallel lens reviewers (security surface), so each attempt costs two verdicts; two security findings were real (the `load`/`exception` and `general` loggers quoted user content).
- **Remaining:** T06 (four call sites + dispatch tests, ~150 LOC, 1–2 rounds) and T07 (verification, 1 round).
- **Stop:** budget tripwire — "No mandate widens pre-approval: an owner brief that says "never stop" or "treat every gate as approved" pre-approves routine gates only. The exceptions in `/akili-propose` Step 1 *Approval Mode* (HALT, Pivot, budget tripwire, `FATAL_FAIL`, `PRODUCT_BUG`, destructive actions) still stop, and the stop message quotes this sentence." Owner decision needed.
- **Owner decision (Kenji Tanaka, 2026-11-23):** "sí, haz el commit y sigue con T06" — budget raised to ~21 review rounds; continue T06 → T07.

### CHG-LPRE-T06 — Wire the four call sites — in review

- **Effort:** high · **Skills:** `error-handling-patterns`, `marlo-verify` · **Requirements:** FN-005, FN-006, FN-007, SEC-002
- **Files:** `BaseStudySummaryData.java` (+30), `ProjectInnovationSummaryAction.java` (+28), `ReportingSummaryAction.java` (+57/−…; 105 insertions, 10 deletions in total); new `LocalPdfEngineDispatchTest.java` (14 rows). All three actions LF before and after (CR count 0, HEAD also 0).
- **Behaviour:** `@Inject` setter + field per class; branch on `hasSpecificities(APConstants.LOCAL_PDF_REPORT_ENGINE_ACTIVE)`; flag on → `renderToResponse(json, name, ServletActionContext.getResponse())` (downloads) / `render(json, SUBMIT_DEADLINE_MS)` (submit, 30 s; failure → `submitPdf` null); flag on + null service → ERROR log, `sendError(500)` on downloads, `submitPdf` null, never the microservice; flag off → original call unchanged; `bytesPDF` untouched.
- **Deviation (Leader-accepted):** the cluster local branch lives in a private helper `renderClusterReportLocally` — inline, `generateAndSendJson` exceeded `MethodLength` (+1 Checkstyle violation); with the helper the delta is 0.
- **Implementer verification:** 81 tests green (dispatch 14 + all five consumers + service); Checkstyle HEAD=74 / working=74, delta 0; hygiene: no EOL/JFREE/SPANISH findings. Red (setter+field, no branches): 9 of 14 failing, e.g. "oicrDownloadWithTheFlagOnRendersToTheResponseAndSkipsTheMicroservice … one local call expected:<1> but was:<0>"; "clusterSubmitWithTheFlagOnRendersWithTheSubmissionDeadlineAndSkipsTheMicroservice … expected:<[render]> but was:<[]>". Falsifier (invert the Innovation branch): 3/3 Innovation rows failing incl. "…FlagOff… expected:<[queueInnovations]> but was:<[]>", restored.
- **Disqualifier check:** every row reads the flag through the real `BaseAction.hasSpecificities` from a session map (`"true"`, `"false"`, absent).
- **Assumptions:** Innovation/OICR rows call `generateAndSendJson` directly with minimal fixtures (existing caught NPEs in data gathering); null-service branches return `ERROR` (callers ignore it); anonymous downloads keep the microservice (flag follows the session).
- **Evidence re-run (Leader inline):** VERIFIED — clean compile 2430/1070/148, exit 0, 81 tests; line endings unchanged.
- **spawns:** implementer 52 calls, 210,177 tokens, ended complete
- **Reviewer: PASS** (`akili-reviewer`, opus) — all four sites match §7.6 (flag on / off / on + null service); submit uses the 30 s entry point and never touches the response; `bytesPDF` unchanged; returning `ERROR` changes nothing in Struts (callers ignore it); setter injection honoured on both paths (P-8 `createBean`, P-10 `StrutsSpringObjectFactory` → `populateBean`; package component-scanned); tests read the flag through the real session and assert the doubles' arguments. ADVISORY: first `@Inject` setters in MARLO actions — exercise the container wiring live in T07 (**forward pointer to T07**); the `ERROR` return is unused (comment it); the null-service 500 lacks the generic message; write `IOException` lands in catches whose log says "JSON"; the cluster path still calls `microserviceReportAction.loadData()` (a DB read of `report_configurations`) with the flag on — record for the retirement spec.
- **spawns:** reviewer 31 calls, 109,969 tokens, ended complete
- **Final status:** PASS on attempt 1 · **Gate:** `auto-approved (pre-approved mode)` · **Running totals:** tasks 6/7 · review rounds 19 / 21 · production LOC ≈ 3,435

### CHG-LPRE-T07 — Verification pass — in review

- **Run by:** the Leader (inline verification; no code written), 2026-11-23, on `feature/local-pdf-report-engine` (committed T01–T05 + uncommitted T06).
- **Gate 1 — clean recompile** (`.claude/skills/marlo-verify/scripts/clean-compile.sh`): exit 0, BUILD SUCCESS, 2430 (marlo-data) / 1070 (marlo-web) / 148 (tests) files.
- **Gate 2 — Checkstyle 8.18:** all new files under `utils/pdf` (main + test) and `LocalPdfEngineDispatchTest.java`: 0 violations. T06 actions `--baseline`: HEAD=74, working tree=74, delta 0. `APConstants.java` (both) vs base `2f67c5d2ab`: 0 → 0.
- **Gate 3 — cache-bust:** not applicable (no served CSS/JS changed; `pdf/html-pdf-compat.css` is a classpath resource, not referenced by an FTL).
- **Gate 4 — hygiene** (`java-hygiene-check.sh`): no EOL / JFREE / SPANISH / NULL-LIST findings. One `FIX LOG-ARGS` at `ReportingSummaryAction.java:11481` is a **false positive**: `LOG.warn("Unable to resolve project template data via reflection", inner)` — `inner` is the caught `Exception` (the heuristic does not recognise the variable name); pre-existing, shifted by T06's added lines. `REVIEW LOG-CONCAT` at BaseStudySummaryData:1559, ProjectInnovationSummaryAction:1686, ReportingSummaryAction:7924: string literals split for the 120-column limit, placeholders already used.
- **Gate 5 — full test suite with `-am`** (`mvn -o -pl marlo-web -am test`): exit 0, **1,388 tests, 0 failures, 0 errors**.
- **Gate 6 — i18n:** not applicable (no `.properties` changed).
- **Visual parity (NF-001, T6 review by the Leader) — corrected after the T07 review:** image
  `spike/t07-production-vs-local.png`; for each template, production PhantomJS (Linux, spike baseline) on top, local
  engine below. **Provenance of the local rows:** `marlo-web/target/local-pdf-report/{oicr,innovation,project}.pdf`,
  written at 10:25 by `LocalPdfReportServiceTest.productionTemplatesRenderWithTheProductionPageCountsAndOnlyTheBundledFonts`
  (`LocalPdfReportServiceTest.java:552–564`) during the Gate 5 run — i.e. the built `LocalPdfReportService.render(...)`
  on the production templates (`src/test/resources/pdf/templates/`) and payloads (`src/test/resources/pdf/payloads/`).
  Page counts match (5 / 6 / 7). Differences, all layout/pagination, no content missing:
  - **OICR:** "Other cross-cutting dimensions / No" ends local page 4 instead of starting production page 5; production
    appears to repeat the Evidence-and-reference table header at the top of page 4, local does not (low resolution).
  - **Innovation:** content reflows from page 3 — "SECTION: knowledge sharing and Scaling potential / Anticipated users /
    Actors" moves up to local page 3, "Levels by scaling potential / Technical excellence / Demand and Investment" up to
    local page 5; local page 6 starts mid-block and is about a third full.
  - **AICCRA Cluster Summary (project template):** pages 2+ body starts ~36 pt lower; the cover's three badges wrap onto
    two rows.
  Visual acceptance of these differences is the owner's (NF-001) at the HITL pause / `/akili-validate`.
- **DA-002 (templates untouched):** `git diff 2f67c5d2ab --name-only -- marlo-web/src/main/resources/database/migrations`
  → only `V2_6_0_20261120_2222__AddLocalPdfReportEngineSpecificity.sql`; `git diff 2f67c5d2ab -- …/migrations | grep -ci
  report_configurations` → 0. No migration touches `report_configurations`.
- **Regression (manual, QA) step of the Testing Plan:** pending — QA activity outside T07 (flag on for one unit on a test
  environment, compare the three summaries with the flag off).
- **Not run — live wiring (T06 forward pointer):** a real flag-on download and submit on the running app (first `@Inject` setters in MARLO actions). Needs `scripts/run-marlo-java17.sh` (kills `cargo:run`, wipes `target/`, rewrites `marlo-dev.properties`) and a `custom_parameters` row set to `'true'` in the local database → owner decision (HITL).
- **Reviewer attempt 1: FAIL** — gates and the LOG-ARGS false positive confirmed; evidence gaps: DA-002 not evidenced; provenance of the local images not stated; visual-parity text overstated parity (OICR/innovation reflows, template name). ADVISORY: pair the live-wiring run with one real-data render per template. All fixes are `execution.md` edits — applied above (attempt 2 = same verification, corrected record).
- **spawns:** reviewer 12 calls, 58,640 tokens, ended complete
- **Reviewer attempt 2: PASS** — all three evidence gaps closed, each checked against its source (migrations folder, test lines 98/244–246/276–278/552–564, the image).
- **spawns:** reviewer 9 calls, 50,644 tokens, ended complete
- **Final status:** PASS on attempt 2 · **Gate:** `auto-approved (pre-approved mode)` · **Running totals:** tasks 7/7 (T08 deferred by design) · review rounds 21 / 21

## Summary

| Task | Result | Attempts | Commit |
|---|---|---|---|
| T01 Dependencies, fonts, compat CSS | PASS | 1 | `8f8bb78cea` |
| T02 Helpers + template subset guard | PASS | 8 (HALT after 3, owner-approved 4–5, Pivot, 3 post-Pivot) | `c168c70f30` |
| T03 Compatibility step + resource loader | PASS | 2 | `73681d5bed` |
| T04 LocalPdfReportService | PASS | 3 (two parallel lenses) | `af0a387e69` |
| T05 Specificity migration + constants | PASS | 1 | `d51a4b5164` |
| T06 Wire the four call sites | PASS | 1 | uncommitted — owner to approve |
| T07 Verification pass | PASS | 2 (evidence record) | uncommitted — owner to approve |
| T08 Retire the microservice path | deferred | — | follow-up spec `changes/retire-reports-microservice` |

**Verification (T07):** clean recompile 2430 / 1070 / 148; full `marlo-web` suite with `-am` 1,388 tests green; Checkstyle delta 0; hygiene clean (one documented false positive); page counts 5 / 6 / 7 equal to production with DejaVu fonts only; visual differences listed under T07.

**Open items for the owner (HITL):**
1. Visual acceptance of the layout/pagination differences (NF-001) — `spike/t07-production-vs-local.png`.
2. Live wiring check on the running app: flag on for one unit in the local database, one download per report and one project submission (first `@Inject` setters in MARLO actions) — needs the run script, which wipes `target/` and rewrites `marlo-dev.properties`.
3. Manual QA regression on a test environment (Testing Plan).
4. `GENERIC_ERROR_MESSAGE` kept as a fixed English string (consistent with `downloadPDFByURL`); Hard rule 8 reading to confirm.
5. Accepted residual risks: decompression-bomb images (§17), layout not interruptible (§17).
6. Pending items on `staging` (shared files, not editable on the spec branch): TRD §7 integration rows; `.agents/*.md` persona drift reported by `akili doctor --agents`.
7. Follow-ups for the retirement spec: move the null→`NO_RESOURCE` handling into `PdfResourceLoader`; header-only image dimension check; the cluster path still calls `microserviceReportAction.loadData()` (a DB read) with the flag on; inline `{{if x}}` not guarded; loader-level tests for `getFailedFetchCount()` branches.

**Next AKILI steps:** `/akili-test changes/local-pdf-report-engine` (optional — the tasks authored their suites), then `/akili-validate changes/local-pdf-report-engine` (T6 visual review included).
