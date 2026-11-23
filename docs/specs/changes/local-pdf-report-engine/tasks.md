# Local PDF Report Engine — Tasks

**Spec ID:** CHG-LOCAL-PDF-REPORT-ENGINE-001
**Status:** Ready — Phase 3 gate `auto-approved (pre-approved mode)` 2026-11-20
**Owner:** Kenji Tanaka (IBD)
**Last Updated:** 2026-11-20
**Implements design:** docs/specs/changes/local-pdf-report-engine/design.md
**Branching:** `feature/local-pdf-report-engine` (created `--no-track` from `staging` at `2f67c5d2ab`).
**Target merge:** staging. **No commit or push without the owner's approval** (standing owner rule).

---

## 1. Execution Context

- Java 17 build (`marlo-parent/pom.xml`); Handlebars 4.5.8 needs it (design §4).
- Gates per `marlo-verify`: clean `test-compile` of `marlo-data,marlo-web`; Checkstyle 8.18 delta against HEAD;
  `mvn -q -o -pl marlo-web -am test` for the tests the tasks author. T01 is the only step that needs Maven online
  (new artifacts); every later build may run offline.
- No run script is executed: `scripts/run-marlo-java17.sh` rewrites `marlo-dev.properties` and wipes `target/`.
- Test fixtures: the three production templates and the reference payloads from the spike, copied to
  `marlo-web/src/test/resources/pdf/` (templates from `report_configurations` id 1 of the local copy
  `partroute_local`).

## 2. Pre-flight Checklist

- [x] `requirements.md`, `design.md` approved (pre-approved mode, judgment-day closed — `judgment.md`).
- [x] Feature branch exists, based on current `origin/staging` (`2f67c5d2ab`).
- [x] Spike evidence available (`spike.md`, baseline PDFs in the session scratchpad).

## 3. Task List

### CHG-LPRE-T01 — Dependencies, fonts and compatibility stylesheet
- **Status:** [x]

- **Depends on:** none
- **Module:** marlo-parent, marlo-web
- **Requirements:** NF-002, NF-006 · **Design:** §3, §4, §7.3 (stylesheet), §7.4 (fonts)
- **Files touched:** `marlo-parent/pom.xml` (properties + dependencyManagement with exclusions); `marlo-web/pom.xml`
  (two dependencies); `marlo-web/src/main/resources/pdf/fonts/DejaVuSans.ttf`, `DejaVuSans-Bold.ttf`,
  `LICENSE-DejaVu.txt` (new); `marlo-web/src/main/resources/pdf/html-pdf-compat.css` (new, rules F5, F7–F9 + the
  `img-background` rule, each line commented with template and spike fact).
- **Scope:** Backend route: `none — project conventions` (build configuration and resources only).
- **Tests:** none authored — configuration task.
- **Verification:** `mvn -o dependency:tree -pl marlo-web` before and after (before captured from `2f67c5d2ab`):
  `slf4j-api` 1.7.22, `commons-logging` 1.2, `org.ow2.asm:asm` 9.7.1, `commons-lang3` 3.14.0, `commons-text` 1.10.0,
  `jsoup` 1.15.4 unchanged; `nashorn-core` absent; `pdfbox` 3.0.8 and `handlebars` 4.5.8 present. Clean compile gate
  green. Fonts: `sha256` matches the files from the `node:23` image (spike).
- **Falsifier:** remove the `slf4j-api` exclusion → the tree shows `slf4j-api:2.0.x` resolved for marlo-web.
- **Red run:** predicted — greenfield artifact (observed in T01): the exclusion does not exist yet; the Implementer
  observes the tree without the exclusion first and quotes the resolved `slf4j-api` line.
- **Disqualifier:** a tree taken with stale `~/.m2` metadata or with `-o` before the artifacts were downloaded is
  not evidence; a "BUILD SUCCESS" compiling far fewer than ~1040 marlo-web files means stale classes.
- **Consumers:** none — no symbol is changed.
- **Review:** `full` — dependency changes are Hard rule 11 territory.
- **Skills:** `marlo-verify`.
- **Done when:** both trees quoted in `execution.md`; compile gate green; licence file present.

### CHG-LPRE-T02 — Handlebars helpers with JavaScript semantics
- **Status:** [x]

- **Depends on:** T01
- **Module:** marlo-web
- **Requirements:** FN-003 · **Design:** §7.1 (resolver, number printing), §7.2
- **Files touched:** `utils/pdf/HandlebarsReportHelpers.java` (new, GPL header);
  `src/test/java/.../utils/pdf/HandlebarsReportHelpersTest.java` (new);
  `src/test/resources/pdf/helper-vectors.json` (new).
- **Scope:** register the 17 helpers of design §7.2 on a `Handlebars` instance configured with the map resolver
  and JavaScript number printing; override built-in `if`/`unless` with Handlebars.js semantics and let a literal
  `null` reach helpers as JS `null` (design §7.1 as amended 2026-11-20 — moved here from T04 during execution).
  *Pivot 2026-11-23:* also add `ReportTemplateSubset` (lists unsupported constructs, design §7.1) with a test over
  the three production templates (copied to `src/test/resources/pdf/templates/`), fix `../` under a `null` item,
  and resolve `@odd`/`@even` as `undefined`. Requirements: FN-003, FN-008. Backend route: `test-first` (tdd) · stack: project conventions.
- **Tests:** table-driven test over `helper-vectors.json`: each row is a template snippet, a context and the
  **expected output produced by Handlebars.js 4.7.8 with the helpers copied from `pdf.module.ts`** (generated once
  with Node from the cloned microservice; the generator command is recorded in the test resource header). Rows
  cover every helper and every quirk named in §7.2 (`0 == ""`, `true == "true"`, `[] == ""`, `1.0` printing,
  `NaN`, accents in `stringContentCompare`, `yesNoHelper` one-argument `null` (→ `[object Object]`) and two-argument `null` (→ `No`, fallback ignored), `parseJSON` missing vs `null`,
  `conditionalAttribiute` escaping, `json` indentation), plus one row asserting the registered helper names equal
  the 17 names of `pdf.module.ts`.
- **Verification:** `mvn -q -o -pl marlo-web -am test -Dtest=HandlebarsReportHelpersTest`.
- **Falsifier:** change `ifEquals` to compare `String.valueOf` forms → the `true == "true"` and `0 == ""` rows fail.
- **Red run:** predicted — greenfield artifact (observed in T02): the test runs first against an empty helper class
  and its failure on the `safeEmpty` row's assertion is quoted.
- **Disqualifier:** expected values typed by hand instead of produced by Handlebars.js are not evidence (they would
  encode the Java author's belief, KZ-…-directory-abstraction-2: a fixture must not supply the value its assertion
  checks).
- **Consumers:** none outside the new package.
- **Review:** `full`.
- **Skills:** `tdd`, `marlo-verify`.
- **Done when:** all vectors green on MARLO's classpath (settles P-9); helper-name row green.

### CHG-LPRE-T03 — html-pdf compatibility step and resource loader
- **Status:** [x]

- **Depends on:** T01
- **Module:** marlo-web
- **Requirements:** FN-004, SEC-001, NF-004 · **Design:** §7.3, §11
- **Files touched:** `utils/pdf/HtmlPdfCompatibility.java`, `utils/pdf/PdfResourceLoader.java` (new);
  `src/test/java/.../utils/pdf/HtmlPdfCompatibilityTest.java`, `PdfResourceLoaderTest.java` (new).
- **Scope:** jsoup parse → strip `@page`/`@font-face` → move header/footer variants to running elements appending
  to their style → generated page CSS from header/footer heights → `<br>` whitespace trim → compat stylesheet
  appended → `W3CDom`. Resource loader: single allow/deny decision, timeouts, 3 MB body cap, 20 s per-render
  budget, 32 MB LRU with 1 h TTL, 60 s negative cache, no redirects, `data:` ≤ 2 MB. Backend route:
  `error-handling-patterns` (failure and budget paths) · stack: project conventions.
- **Tests:**
  - Compatibility: given HTML with `#pageHeader`, `#pageFooter`, `#pageFooter-1` and an inline style on
    `#pageHeader`, the output has them first in `<body>` with `running()` appended (original style kept), the
    template `@page`/`@font-face` gone, and whitespace beside `<br>` removed (but kept inside `<pre>`).
  - Loader (with a recording connection factory test double that **records the URI it receives**, KZ-…-auth-flow-2):
    `file:///etc/passwd`, `jar:file:...!/x`, `http://marlo-pdf-resources-dev...`, `https://169.254.169.254/`,
    `https://evil.example/`, `https://user@marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com/x`,
    `https://marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com:8443/x` → zero recorded connections;
    `https://MARLO-PDF-RESOURCES-DEV.s3.us-east-1.amazonaws.com/a.png` → one connection to the lowercased host;
    a 302 response is not followed; a second request for the same URI within TTL makes no new connection; a body
    over 3 MB is dropped.
- **Verification:** `mvn -q -o -pl marlo-web -am test -Dtest='HtmlPdfCompatibilityTest,PdfResourceLoaderTest'`.
- **Falsifier:** compare hosts without lowercasing / allow any `*.amazonaws.com` → the 169.254 or evil rows stay
  green only if the double does not record; the double records, so the mutated "allow suffix amazonaws.com" makes a
  connection recorded for `https://x.s3.amazonaws.com/` (add that row) and the test fails.
- **Red run:** predicted — greenfield artifact (observed in T03).
- **Disqualifier:** a loader test that asserts only on the returned value (null image) without inspecting what the
  double recorded proves nothing about SSRF.
- **Consumers:** none outside the new package.
- **Review:** `full` — security surface.
- **Skills:** `error-handling-patterns`, `tdd`, `marlo-verify`.
- **Done when:** both test classes green; every SEC-001 scenario row present.

### CHG-LPRE-T04 — LocalPdfReportService (payload, renderer, execution model, response)
- **Status:** [x]

- **Depends on:** T02, T03
- **Module:** marlo-web
- **Requirements:** FN-001, FN-002, FN-004, NF-001, NF-002, NF-003, NF-005, SEC-002, OPS-001 · **Design:** §7.1,
  §7.4, §7.5, §10
- **Files touched:** `utils/pdf/LocalPdfReportService.java`, `utils/pdf/PdfRenderException.java` (new);
  `utils/pdf/PdfResourceLoader.java` (failed-fetch counter, added during execution 2026-11-23);
  `src/test/java/.../utils/pdf/LocalPdfReportServiceTest.java` (new);
  `src/test/resources/pdf/templates/{oicr,innovation,project}.html`, `src/test/resources/pdf/payloads/*.json` (new).
- **Scope:** parse the microservice payload; compile (SHA-256-keyed cache of 8) and apply the template; run the
  compatibility step; render with DejaVu fonts under `sans-serif`/`Montserrat`/`DejaVu Sans`, 27 dots per pixel,
  every `XRLog` category at SEVERE (design §7.4 as superseded 2026-11-23); pool 3 / queue 6, deadlines 30 s and 50 s, cancelled tasks removed from the queue, late
  completion WARN; `render` and `renderToResponse` with the headers of `downloadPDFByURL`; one INFO line per render,
  ERROR with cause on failure, no template or payload text logged; pool shutdown on destroy. Backend route:
  `error-handling-patterns` · stack: project conventions.
- **Tests:**
  - Page counts: the three production templates with their reference payloads render 5 / 6 / 7 pages (AC-NF-001),
    with the font list of the PDF (PDFBox) equal to DejaVu Sans regular + bold.
  - Header/footer placement (AC-FN-004): page 1 text contains the `-1` footer sentence and pages ≥ 2 the default
    one, located in the bottom 30 mm band by text position.
  - Payload errors: missing `templateData`, non-object `data.data` → `PdfRenderException`.
  - Deadline (NF-005): a render whose resource fetch blocks forever (test double) returns `PdfRenderException`
    within 30 s ± 2 s on the submission entry point, and the queue no longer holds the cancelled task.
  - Saturation (NF-003): with the pool and queue full, the next call fails immediately.
  - Response (SEC-002): a template that fails to compile → `sendError(500, <generic message>)` on a mock response,
    and the message contains neither template text nor exception text.
- **Verification:** `mvn -q -o -pl marlo-web -am test -Dtest=LocalPdfReportServiceTest`.
- **Falsifier:** set dots-per-pixel back to the default (20) → the OICR fixture renders 4 pages and the page-count
  assertion fails (spike: 4 pages at default scale).
- **Red run:** predicted — greenfield artifact (observed in T04).
- **Disqualifier:** page counts measured with fonts other than the bundled DejaVu (font fallback) are not evidence —
  the font assertion must pass in the same test; a deadline test that uses real network is flaky and not evidence.
- **Consumers:** none yet (wired in T06).
- **Review:** `full`.
- **Skills:** `error-handling-patterns`, `marlo-verify`.
- **Done when:** all tests green; one render log line observed in test output.

### CHG-LPRE-T05 — Specificity migration and constants
- **Status:** [x]

- **Depends on:** none
- **Module:** marlo-data, marlo-web
- **Requirements:** DA-001, FN-007 · **Design:** §5
- **Files touched:** `marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java`,
  `marlo-web/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java` (constant
  `LOCAL_PDF_REPORT_ENGINE_ACTIVE = "local_pdf_report_engine_active"`);
  `marlo-web/src/main/resources/database/migrations/V2_6_0_<date +%Y%m%d_%H%M>__AddLocalPdfReportEngineSpecificity.sql` (new).
- **Scope:** guarded `parameters` inserts for types 1, 3, 4; explicit `'false'` `custom_parameters` per global unit
  by type; seed user 3 resolved only when present; header comment with ON/OFF meaning, rollout `UPDATE` example by
  acronym, the stale-session reason, and that a unit created from a template unit with the flag on inherits it. Backend route: `none — project conventions` (`marlo-migration`).
- **Tests:** applied twice on a throwaway copy of a local database (`CREATE DATABASE … ` copy of `partroute_local`,
  dropped afterwards): first run inserts 3 `parameters` rows and one `custom_parameters` row per unit of types
  1/3/4; second run inserts nothing. Also applied to a schema with no `global_units` rows → only the 3 `parameters`
  rows.
- **Verification:** row counts quoted from both runs; constant value equals the key in both files (`grep`).
- **Falsifier:** drop the `NOT EXISTS` guard on `custom_parameters` → second run doubles the rows.
- **Red run:** predicted — greenfield artifact (observed in T05).
- **Disqualifier:** running against a real shared database instead of a throwaway copy is forbidden, not merely
  non-evidence.
- **Consumers:** `grep -rn "local_pdf_report_engine_active\|LOCAL_PDF_REPORT_ENGINE_ACTIVE"` → none before this task.
- **Review:** `full` — migration on production data path.
- **Skills:** `marlo-migration`, `marlo-verify`.
- **Done when:** both runs quoted; file name built from the real clock.

### CHG-LPRE-T06 — Wire the four call sites
- **Status:** [ ]

- **Depends on:** T04, T05
- **Module:** marlo-web
- **Requirements:** FN-005, FN-006, FN-007, SEC-002 · **Design:** §7.6
- **Files touched:** `action/summaries/BaseStudySummaryData.java`, `ProjectInnovationSummaryAction.java`,
  `ReportingSummaryAction.java`; tests `src/test/java/.../action/summaries/LocalPdfEngineDispatchTest.java` (new).
- **Scope:** `@Inject` setter for `LocalPdfReportService` on the three classes; at each of the four sites branch on
  `hasSpecificities(APConstants.LOCAL_PDF_REPORT_ENGINE_ACTIVE)`; flag on → local entry point (download:
  `renderToResponse` with the existing report name and `ServletActionContext` response; submit: `render` with the
  30 s deadline into `submitPdf`); flag on + service null → ERROR + 500 / null bytes, never the microservice; flag
  off → the existing microservice call untouched. `StudiesSummaryAction` untouched. Backend route:
  `error-handling-patterns` · stack: project conventions.
- **Tests:** for each site, with recording doubles of `MicroserviceReportAction` and `LocalPdfReportService` that
  record the arguments they receive: flag on → local called with the same JSON and report name, microservice not
  called; flag off → microservice called with the same arguments as before, local not called; flag on + null
  service → microservice not called and the error outcome observed.
- **Verification:** `mvn -q -o -pl marlo-web -am test -Dtest='LocalPdfEngineDispatchTest,ReportingSummarySubmitFlagTest,ProjectSubmissionEmailTest,ProjectSubmissionPdfStepTest,MicroserviceReportFetchTest,ProjectInnovationSummaryActionIdTest'`
  — the existing consumers stay green.
- **Falsifier:** invert the branch at one site → its flag-on and flag-off rows both fail.
- **Red run:** observed before the change — the new dispatch test compiled against the unmodified actions fails on
  the "local called" assertion for every site (Implementer quotes it).
- **Disqualifier:** a dispatch test that reads the flag from a mocked `hasSpecificities` override but never goes
  through the real session lookup must state it; at least one row uses a real session map with the key.
- **Consumers:** `ProjectSubmissionEmailTest`, `ProjectSubmissionPdfStepTest`, `MicroserviceReportFetchTest`,
  `ProjectInnovationSummaryActionIdTest`, `ReportingSummarySubmitFlagTest` (design P-13); main-code readers
  `ProjectSubmissionAction`, `LeveragesReportingSummaryAction`, `ProjectContributionLP6PDFSummaryAction`, `SendEmails`
  (unchanged members).
- **Review:** `full`.
- **Skills:** `error-handling-patterns`, `marlo-verify`.
- **Done when:** all listed tests green; diff of the three actions limited to the setter, the field and the four
  branches.

### CHG-LPRE-T07 — Verification pass
- **Status:** [ ]

- **Depends on:** T01–T06
- **Module:** all touched
- **Requirements:** all; NF-001 visual part · **Design:** §13
- **Scope:** full `marlo-verify` gates (clean compile, Checkstyle delta, full `marlo-web` test suite with `-am`,
  hygiene checks); render the three fixtures through the **built** `LocalPdfReportService` and compare page images
  with the production PhantomJS baselines (T6 visual review in `/akili-validate`).
- **Verification:** commands and outcomes quoted in `execution.md`.
- **Falsifier:** n/a — aggregation of the task gates.
- **Red run:** n/a.
- **Disqualifier:** a full-suite run without `-am` tests the stale `marlo-data` jar and is not evidence.
- **Consumers:** n/a.
- **Review:** `checklist` — no new code.
- **Skills:** `marlo-verify`.
- **Done when:** all gates green or each failure reported verbatim.

### CHG-LPRE-T08 — Retire the microservice path (deferred)

- **Status:** deferred to the follow-up spec `changes/retire-reports-microservice`, after production validation
  (requirements §4, Decision Log). Not executed in this spec.

## 4. Dependency Graph

```
T01 ──┬── T02 ──┐
      └── T03 ──┴── T04 ──┐
T05 ──────────────────────┴── T06 ── T07
```

T02 ∥ T03 and T05 ∥ (T01..T04) are parallel-safe; in this single-checkout run they execute sequentially.

## 5. Testing Plan

| Clause | Owning task |
|---|---|
| FN-001/002 payload rendered, errors rejected | T04 |
| FN-003 every helper, every quirk | T02 |
| FN-004 header/footer variants, out of flow | T03 (DOM), T04 (rendered position) |
| FN-005 flag on download — local, BUT must NOT publish/poll | T06 |
| FN-006 submit email bytes | T06 |
| FN-007 flag off/absent — microservice, BUT must NOT call local | T06 (code), T05 (explicit `'false'` rows) |
| NF-001 page counts | T04 · visual parity: T07 + `/akili-validate` T6 review |
| NF-002 DejaVu fonts | T01 (files), T04 (PDF font list) |
| NF-003 bounded wait, fail not queue | T04 |
| NF-004 fetch timeouts | T03 |
| NF-005 submit deadline | T04 (service), T06 (site uses the 30 s entry point) |
| NF-006 no dependency drift | T01 |
| DA-001 specificity + constants | T05 |
| DA-002 templates untouched | T07 (`git diff` shows no migration touching `report_configurations`) |
| SEC-001 allowlist, BUT must NOT connect | T03 |
| SEC-002 generic error | T04, T06 |
| OPS-001 render log line | T04 |

Regression (manual, QA): download the three summaries with the flag on for one unit on a test environment and
compare with the flag off.

## 6. Operational Steps

- Migration deploy: ships with the WAR; inserts disabled rows only.
- Specificity rollout: set the unit's `custom_parameters` row to `'true'` (example in the migration header);
  users pick it up at next login or unit switch.
- No environment variable or properties change.

## 7. Rollback Plan

- Specificity: set the row back to `'false'`.
- Code: revert the branch merge; rows left by the migration are inert.

## 8. Definition of Done

- T01–T07 done with evidence in `execution.md`; T08 handed to the follow-up spec.
- Gates green; visual parity accepted in `/akili-validate`.
- Owner reviews and decides commit, push and merge.
