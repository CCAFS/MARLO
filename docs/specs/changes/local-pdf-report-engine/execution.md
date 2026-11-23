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
