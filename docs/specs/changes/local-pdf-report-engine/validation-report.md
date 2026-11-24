# Validation Report — Local PDF Report Engine

> Supersedes the 2026-11-23 report (HEAD `acea7ad51a`, pre-Pivot 2), which is kept in git history. Section 8 lists
> what that report said that no longer holds.

## 1. Document Control

| Field | Value |
|---|---|
| Spec | `docs/specs/changes/local-pdf-report-engine/` |
| Branch / HEAD | `feature/local-pdf-report-engine` @ `8e73305d81` (base `2f67c5d2ab`) |
| Date | 2026-11-24 |
| Validators | **Leader (opus):** build gates and the measured parity evidence. **Independent auditor (`akili-reviewer`, opus, read-only, unanchored brief):** coverage and conformance. The implementer was sonnet, so author ≠ auditor holds |
| Architecture validated | Pivot 2 (design §18): Handlebars.java → sanitizer → bundled-image inlining → PhantomJS 2.1.1 shipped inside MARLO, with an empty allowlist |
| `AI Agent` / `Deliverable` | absent → no eval gate, software |

## 2. Summary

**Verdict: FAIL — 2 FAIL, 12 WARN, 0 BLOCKED.**

- **The product works and meets its strongest criterion.** With no configuration, in MARLO's unmodified Docker base image and with the network disabled (`--network none`), the built code gives **PARITY and a 0.000 % pixel diff** against production on all six cases and two probes. The cluster case is the real 481-page one. A render makes no external request.
- **Both FAILs are one-line Hard-rule fixes:**
  - **F1:** a line of 123 characters.
  - **F2:** a missing GPL header in the spike harness.
- **The WARNs are mostly spec text that trails the code:**
  - FN-001 still says "in-process";
  - OPS-001 is not amended;
  - stale out-of-scope, §7.5/§10, tasks §5/§6/§8, file tables and the execution Summary;
  - the gated integration test has never been recorded green on Linux at HEAD;
  - the owner's brand-image confirmation was given but not recorded;
  - the DejaVu Arev notice is missing;
  - the review-round budget was exceeded without a note.

| | Count |
|---|---|
| FAIL | 2 |
| WARN | 12 |
| BLOCKED | 0 |
| Tasks done | T01–T07, T09, T10, T12–T18 · T08 deferred · T11 superseded |

## 3. Task Completion

| Task | Verdict | Note |
|---|---|---|
| T01–T07, T09, T10 | PASS | Code from T03/T04 was retired later (T14), consistently |
| T08 | PASS (deferred) | Retirement spec |
| T11 | PASS (superseded) | Pivot 2; diff kept in `spike/` |
| T12, T13, T14, T15, T18 | PASS | Reviewer PASS and Leader evidence |
| T16 | WARN (W6) | The gated JUnit test never exercises the shipped-binary path. The no-config Docker runs do |
| T17 | WARN (W7) | Brand-image confirmation not recorded (the owner gave it: "sí, súbelas y haz el commit") |

## 4. File Existence

| Check | Verdict |
|---|---|
| All 8 `utils/pdf` classes, `render-page.js`, 6 fonts, `fontconfig/` (31 rules), `phantomjs/` (binary + BSD/LGPL/source/provenance), `images/` (309 manifest rows), migration, constants, 10 test classes, vectors, expected HTML | PASS |
| Retired files absent (`HtmlPdfCompatibility`, `PdfResourceLoader`, compat CSS and their tests, old test images) | PASS |
| File tables in tasks/design are complete | WARN (W10) |

## 5. Build Integrity

Leader-run at HEAD `8e73305d81`:

| Gate | Result |
|---|---|
| Clean compile + full suite: `mvn -o -pl marlo-utils,marlo-data,marlo-web -am test` | Exit 0. Compiled 6 / 2,430 / 1,072 / 152 files. **1,457 tests, 0 failures, 7 skipped.** The 7 skipped tests need `PHANTOMJS_PATH` |
| Checkstyle 8.18 on all 25 changed Java files vs base | Actions 74 → 74 and `APConfig` 9 → 9 (all pre-existing). **New: `PdfImageBundle.java:98` LineLength 123 (F1).** Spike `LocalRender.java:19` LoggingSystemOut (advisory; not production) |
| Measured parity (Docker, Linux x86_64, `tomcat:9.0-jdk17-temurin`, no config, `--network none`) | **PARITY 8/8; 0.000 % pixel diff on pages 1–6 of each case; `blockedUris=0`, `failedFetches=0`** |
| Live wiring on the developer Mac (T15) | 3 downloads `outcome=ok`, no ERROR, no microservice call. macOS fonts differ, as expected (§18.4) |

## 6. Requirement Coverage

| Requirement / clause | Evidence | Verdict |
|---|---|---|
| FN-001 "in-process" | Renders inside MARLO, but in a child PhantomJS process | **WARN (W1)**: text not amended |
| FN-002 same payload | Payload tests | PASS |
| FN-003 17 helpers; HTML byte-identical on the six cases | 2,817 helper vectors; byte-identity test; Leader check on 6 cases | PASS |
| FN-004 html-pdf page model | html-pdf's own page script; parity | PASS (measured) |
| FN-005 / FN-006 / FN-007 including BUT and AND IT MUST clauses | Dispatch test (14); FN-006 owner-accepted on unit evidence | PASS |
| FN-008 guard: test fails AND render warns | 7,712 subset vectors; WARN test | PASS |
| NF-001 measured parity on fixtures **and** real data | `compare.py` + pixel diff, Leader | PASS (measured; not in CI, W5) |
| NF-002 DejaVu only, isolated fontconfig | 6 fonts + Debian rules; font probe | PASS |
| NF-003 / NF-005 pool, deadlines | Saturation and hang tests | PASS |
| NF-004 timeouts | 5 s per resource plus a process deadline (no fetch remains) | PASS |
| NF-006 dependencies | T14 tree diff | PASS |
| NF-007 shipped binary, SHA-256, 0700, override wins, startup check, fail fast | `EmbeddedPhantomJsTest`, renderer tests | PASS |
| DA-001 / DA-002 | Migration runs; templates untouched | PASS |
| UI-001 no new strings | Bare 500 | PASS |
| SEC-001 allowlist (empty) | Spy probe, 14 payloads, 0 hits | PASS (WebSocket bypass is an accepted risk under §18.3 / OQ-4) |
| SEC-002 / SEC-003 / SEC-004 | Sanitizer tests, bypass tests, kill and cleanup tests | PASS |
| OPS-001 "stack trace on failure" | Rejected and timeout renders log message only | **WARN (W2)**: text not amended |
| §6 acceptance scenarios | As above. AC-NF-001, AC-FN-001/002, AC-FN-004 and AC-SEC-001 are automated only in the gated test | **WARN (W5)** |

Eval gate: not applicable.

## 7. Linting & Code Quality

**Hard rules:**

| Rule | Verdict |
|---|---|
| GPL headers: production files | 18/18 PASS |
| GPL headers: spike harness | **FAIL (F2)** |
| Line length | **FAIL (F1)** |
| English; i18n; constants in both `APConstants`; migration safety; no secrets | PASS |
| Licences: PhantomJS BSD+LGPL+source, `render-page.js` MIT, fontconfig, flag-icons MIT, UN SDG | PASS |
| Licences: DejaVu Arev notice | WARN (W8) |

**4R advisory (does not affect the verdict):**
- A failed submission render logs two ERRORs.
- Stale comments: `LocalPdfReportService.java:69-71,325-327` and `marlo-test.properties:235` still say "empty = not installed", but since T16 empty means the shipped binary.
- The images README says PRMS user uploads "stay remote", but every non-bundled URL is blanked.
- The 26 MB binary is in Git and the WAR, and 68 MB is extracted per JVM.
- judgment-day never re-judged §18 or ADR-6.

**Carried advisories (`execution.md`):**
- slf4j-api not pinned (T01).
- T02 helper quirks: zero-argument `or`/`ifOr`, `JsNull` equality, `parseJSON` not logged, inline `{{if}}`, U+2028/U+2029.
- T06: unused `ERROR` return; IOException logged as "JSON"; `loadData()` reads the database with the flag on.
- T09: the in-pool `ExecutionException` path is not pinned.
- T12: WARN without a template hash prefix; the subset gaps.
- T13: unknown sanitizer bypass → WebSocket egress (accepted).
- T16: a pre-marker build older than 24 h can be swept; outdated QtWebKit/OpenSSL (in OQ-4).
- T17: a literal bucket `src=` in triple-stash text still gets inlined.

## 8. Design Conformance

- **§18 matches the code:**
  - the pipeline order;
  - the process environment, which also sets `PATH` and `LANG`, unlisted in §18;
  - stream caps;
  - `OPENSSL_CONF`;
  - the trusted-SVG exception;
  - the noexec fail-fast;
  - the lock-based sweep.
- **Drift: WARN.**
  - **W3:** the out-of-scope bullet still says PhantomJS installation is infrastructure work.
  - **W4:** the banner calls §7.5 and §10 "live", but they describe retired fetch and logging behaviour.
  - **W9:** tasks §5, §6 and §8, the header date, and T16 "against a constant" are stale.
  - **W10:** file tables are incomplete.
  - **W11:** the execution Summary, Document Control and commits are stale; the final vector count of 2,817 is not recorded.
  - **W12:** 40 review rounds against an estimate of about 31, with no budget note.
- **Cross-document figures:** 17 helpers, 309 images, 6 fonts, 31 rules, pool 3 / queue 6, 30 / 50 s, pages 5/6/7 and 481 are all consistent. The vector count (W11) and the review-round count (W12) are not.

**Proposal alignment:**

| Criterion | Verdict |
|---|---|
| Intent | PASS (wording: W1) |
| Scope and non-goals | PASS |
| S1 (superseded by measured NF-001) | PASS |
| S2 (no MQ/S3/CLARISA, no external request) | PASS |
| S3 | PASS |
| S4, S5 | Deferred to the retirement spec and the staging TRD item |

**Obsolete in the 2026-11-23 report:**
- HEAD and test counts;
- "NF-001 BLOCKED on owner visual acceptance";
- rows and advisories about OpenHTMLtoPDF, `HtmlPdfCompatibility` and `PdfResourceLoader` (fetch limits, 27 dots/px, 32 MB cache);
- W8 (images on S3);
- the 482-page figure.

## 9. Test Evidence Summary

| Evidence | Result |
|---|---|
| Full suite at HEAD | 1,457 tests, 0 failures, 7 skipped (`PHANTOMJS_PATH`) |
| Helper / subset vectors (real Handlebars.js oracle) | 2,817 / 7,712 rows |
| Byte-identical HTML vs Handlebars.js | 6/6 cases |
| Measured parity, `--network none` | 8/8 PARITY, 0.000 % pixel diff |
| Hostile probe (spy server) | 14 payloads, 0 hits |
| Restart-leak and noexec probes | Stale dir swept; noexec WARN with fast failure |
| Gated `PhantomJsRenderingTest` on Linux at HEAD | **Not recorded (W5)** |

No `test-report.md` exists; `/akili-test` was not run.

## 10. Agent Guide / Constitution Impact

There are no `## Constitution Impact` notes, and no module or boundary changed.

Pending on `staging` (shared files):
- TRD §7 should list the local engine and the shipped binary;
- `.agents` persona drift.

Both are carried to `/akili-archive`.

## 11. Remediation

| ID | Severity | Action |
|---|---|---|
| F1 | FAIL | Wrap `PdfImageBundle.java:98` (≤ 120 characters); re-run Checkstyle on the T18 files |
| F2 | FAIL | Add the GPL header to `spike/parity/LocalRender.java` |
| W1, W2, W3 | WARN | Amend FN-001 ("inside MARLO, as a local child process"), OPS-001 (message-only for rejected/timeout) and the out-of-scope bullet |
| W4 | WARN | Mark §7.5 and §10's retired bullets superseded; restate §10 around the page-script counts |
| W5, W6 | WARN | Record one green Linux run of the gated test at HEAD, including a no-argument (shipped binary) variant, or reword the defect-class row and T16 |
| W7 | WARN | Record the owner's confirmation in the Decision Log; update the images README |
| W8 | WARN | Add the upstream DejaVu `LICENSE` (Arev notice) |
| W9, W10, W11, W12 | WARN | Refresh tasks §5/§6/§8/header/T16, the file tables, the execution Summary and Document Control (commits, 2,817 vectors), and a budget note for 40 rounds |
| Advisory | — | Stale comments (`LocalPdfReportService`, `marlo-test.properties`), README "stay remote" wording, the double ERROR on submit |

## 12. Archive Readiness Recommendation

**Not ready.**

1. Fix F1 and F2: code, about 2 lines.
2. Fix the WARNs: W1–W4 and W7–W12 are documentation; W5/W6 need one gated Linux test run.
3. Re-run `/akili-validate` (an incremental re-check is enough).
4. Then run `/akili-archive changes/local-pdf-report-engine`.

The functional result (measured parity, no external request) needs no change.

## 13. Re-check — 2026-11-24 (after "arregla todo")

| ID | Result | Evidence |
|---|---|---|
| F1 | **Closed** | `PdfImageBundle.java:98-100` rewrapped. No line over 120 characters in the changed Java |
| F2 | **Closed** | GPL header in `spike/parity/LocalRender.java`. Its `System.out` stays as an accepted advisory: it is a CLI harness outside every module |
| W1, W2, W3 | **Closed** | FN-001 "inside MARLO … child process"; OPS-001 amended; out-of-scope superseded |
| W4 | **Closed** | §7.5 and §10 markers; §18.2 clause and design date fixed |
| W5 | **Closed** | Gated `PhantomJsRenderingTest` green on Linux: **OK (7 tests)**, `--network none`, after the whitespace fix in the test. The defect-class row is reworded |
| W6 | **Closed** | New shipped-binary test (no-arg service, empty path) passed on Linux |
| W7 | **Closed** | Owner confirmation recorded in the Decision Log and the images README |
| W8 | **Closed** | Full upstream DejaVu 2.37 LICENSE, Arev included |
| W9, W10 | **Closed** | `tasks.md` header, §5, §6, §8, T16 wording and file lists |
| W11 | **Closed** | Summary rows T10–T18 (hashes checked), final figures, stale open items struck |
| W12 | **Closed** | Budget note: 40 rounds, all under explicit owner approvals |

**Gates at the remediated tree:**
- Full suite: 1,458 tests, 0 failures, 8 skipped. The skipped tests are Linux-only or need `PHANTOMJS_PATH`; they were run green in the Linux container.
- Checkstyle: no new production violations.
- Measured parity (T18): 8/8, pixel diff 0.000 %, no network.

**Remaining advisories (not gating):**
- the `APConfig` javadoc for `pdf.phantomjs.path` does not mention that empty means the shipped binary;
- a failed submit render logs two ERRORs;
- the 26 MB binary is in Git and the WAR;
- judgment-day never re-judged §18;
- the carried T02/T06/T09/T12/T13/T16/T17 advisories in §7.

**Verdict after re-check: PASS — 0 FAIL, 0 WARN open, 0 BLOCKED.**

**Archive readiness:** ready once the remediation is committed. Next: `/akili-archive changes/local-pdf-report-engine`.

**Still pending outside this spec:**
- OQ-4, which infrastructure confirms before the flag is enabled anywhere: Linux x86_64, exec-allowed temp dir, egress blocking;
- the TRD §7 and `.agents` items on `staging`;
- the retirement spec (T08).
