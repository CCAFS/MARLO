# Validation Report — Local PDF Report Engine

## 1. Document Control

| Field | Value |
|---|---|
| Spec | `docs/specs/changes/local-pdf-report-engine/` |
| Branch / HEAD | `feature/local-pdf-report-engine` @ `acea7ad51a` (base `2f67c5d2ab`) |
| Date | 2026-11-23 |
| Validator | Leader (opus) for build gates; independent auditor (`akili-reviewer`, opus, read-only, unanchored brief) for coverage and conformance. Implementer was sonnet, so author ≠ auditor holds |
| Inputs | proposal, requirements, design, tasks, execution, judgment; no `test-report.md` (`/akili-test` not run) |
| `AI Agent` / `Deliverable` | absent → `no` / software (no eval gate, no video gate) |

## 2. Summary

**Verdict after re-check (2026-11-23): PASS — 0 FAIL, 0 open WARN. One item stays BLOCKED on the owner: NF-001 visual acceptance.** See §13 for the re-check. Sections 3–12 keep the first-pass findings as the audit trail.

| | First pass | After T10 + doc fixes |
|---|---|---|
| FAIL | 1 (F1) | 0 |
| WARN | 7 (W1, W2, W4–W8) | 0 — six fixed, W2 owner-accepted |
| BLOCKED | NF-001 (owner) | NF-001 (owner) |
| Tests on HEAD + working tree | 1,392 | 1,395 |

## 3. Task Completion

| Task | Status | Evidence | Result |
|---|---|---|---|
| T01 Dependencies, fonts, compat CSS | [x] | dependency trees, clean compile, font sha256 | PASS |
| T02 Helpers + subset guard | [x] | 8 attempts, HALT, Pivot; 2,414 helper + 7,712 subset vectors (row counts match the files) | PASS |
| T03 Compat step + loader | [x] | PASS attempt 2 | PASS |
| T04 LocalPdfReportService | [x] | PASS attempt 3, two lenses | PASS |
| T05 Specificity migration | [x] | two throwaway runs: 3 / 38 / 0, then unchanged | PASS |
| T06 Call sites | [x] | reviewer PASS; live downloads confirmed; **submit not exercised live** | WARN (W2) |
| T07 Verification pass | [x] | gates + `spike/t07-production-vs-local.png`; suite predated T09 — **closed here** by the re-run in §5 | PASS |
| T08 Retire microservice | deferred | consistent across tasks, requirements §4, Decision Log, summary | PASS (by design) |
| T09 Live-verification fix | [x] | red run quoted, 42 targeted tests, Leader re-run, reviewer PASS, live re-test ×3 | PASS |

## 4. File Existence

| Check | Result |
|---|---|
| All six `utils/pdf` classes, CSS, fonts + licence, migration, both constants, six test classes, fixtures, 29 images, generators | PASS |
| Changed files no task names (`git diff --name-only 2f67c5d2ab..HEAD`, Leader-run) | PASS — every changed path belongs to a task or the spec folder |
| Design §3 tree / T02 "Files touched" omit `ReportTemplateSubset.java`, its test and `subset-vectors.json` (added by the Pivot); `templates/*.html` listed under T04 but created in T02 | WARN (W6) |

## 5. Build Integrity

All run by the Leader on HEAD `acea7ad51a`, after T09, with no other build active.

| Gate | Command | Result |
|---|---|---|
| Clean recompile | `.claude/skills/marlo-verify/scripts/clean-compile.sh` | exit 0 — 2,430 (marlo-data) / 1,070 (marlo-web) / 148 (tests) files |
| Full test suite | `mvn -o -pl marlo-web -am test` | exit 0 — **1,392 tests, 0 failures, 0 errors** |
| Checkstyle 8.18 | all 15 changed `.java` vs the same files at `2f67c5d2ab` | 74 → 74, **delta 0**; new files 0 |
| Hygiene | `java-hygiene-check.sh` (T07 + T09 runs) | clean; one documented LOG-ARGS false positive (`ReportingSummaryAction` `LOG.warn(…, inner)`) |
| i18n / cache-bust | — | N/A (no `.properties`, no served CSS/JS) |
| Environment boot | `scripts/run-marlo-java17.sh` on throwaway `lpre_live_tmp` | PASS — up in ~80 s, migrations applied, three renders `outcome=ok` |

## 6. Requirement Coverage

| Requirement / clause | Task | Code | Test | Result |
|---|---|---|---|---|
| FN-001/002 payload contract | T04 | `LocalPdfReportService.java:344-372` | `invalidPayloadsAreRejectedWithoutQuotingThem`, `aPayloadWithOnlyTheRequiredFieldsRenders` | PASS |
| AC-FN-001/002 "text contains the report title and section headings" | T04 | — | not asserted (page count and fonts only) | WARN (W1) |
| FN-003 helper semantics | T02 | `HandlebarsReportHelpers` | 2,414 vectors; `registersExactlyTheSeventeenHelpers…` | PASS (within the subset) |
| FN-008 subset guard — "a test MUST fail", "a render MUST log a warning" | T02, T04 | `ReportTemplateSubset`; service `:561-572` | `productionTemplatesAreInsideTheSubset`, `aProductionTemplateEditedWithAParentPathIsFlagged`, `oneWarnNamesTheUnsupportedConstructs…` | PASS |
| FN-004 header/footer emulation | T03, T04 | `HtmlPdfCompatibility.java:74-127` | `firstPageFooterVariantIsOnPageOne…`, `headerAndFooterMoveFirstInBodyAsRunningElements` | PASS |
| FN-005 flag on, download; "must NOT publish… nor poll S3"; "MUST log one render line" | T06, T04 | three call sites; service `:634` | `*DownloadWithTheFlagOnRendersToTheResponse…` ×3 (microservice doubles empty); `everyRenderWritesOneInfoLine…`; live log | PASS |
| FN-006 "the email carries the local PDF bytes" | T06 | `ReportingSummaryAction:7932-7937` | `clusterSubmitWithTheFlagOn…` reaches `generatePdfBytes` only; no live submit | WARN (W2) |
| FN-007 flag off / absent; "must NOT invoke the local engine" | T05, T06 | call sites | `*FlagOff*` ×4, `clusterDownloadWithNoFlagInTheSession…` | PASS |
| SEC-001 allowlist; "must NOT open a connection" | T03, T04 | `PdfResourceLoader`; service `:519-521` | `disallowedUrisNeverReachTheConnectionFactory`, `resourcesOutsideTheAllowlistNeverReachTheNetworkThroughTheRealRenderer` | PASS |
| SEC-002 generic failure response | T04, T06, T09 | service `:435-449` | `aTemplateThatFailsToCompileAnswers500…`, T09 unexpected-exception tests | PASS (text itself: F1) |
| UI-001 "no i18n string changes" / Hard rule 8 | — | `GENERIC_ERROR_MESSAGE` `:94`, sent `:443`; `web.xml` has no `<error-page>`, so Tomcat shows it | — | **FAIL (F1)** |
| NF-001 page count | T04, T07 | — | 5 / 6 / 7 asserted | PASS |
| NF-001 reviewer acceptance of visual differences | T07 | — | `execution.md` T07 list | **BLOCKED — owner** |
| NF-002 bundled fonts only | T01, T04 | service `:110,515-518` | font-set assertion | PASS |
| NF-003 pool / queue | T04 | `:393-394,589-594` | `withThePoolAndTheQueueFullTheNextCallFailsAtOnce`, `aQueuedRenderWhoseCallerGivesUp…` | PASS |
| NF-004 fetch timeouts | T03 | `PdfResourceLoader:406-407` | `connectionsUseTheDocumentedTimeouts…` | PASS (budget claim: W5) |
| NF-005 submission deadline | T04, T06 | `:88`, `ReportingSummaryAction:7934` | `aRenderWhoseFetchBlocksForeverFailsAtTheSubmissionDeadline` | PASS |
| NF-006 dependency baseline | T01 | `marlo-parent/pom.xml:658-685` | dependency-tree evidence | PASS |
| DA-001 specificity | T05 | both `APConstants` (value = `parameters.key`); migration | throwaway runs | PASS |
| DA-002 templates untouched | T07 | — | migrations diff | PASS |
| OPS-001 "a failure MUST log the cause with its stack trace" | T04 | `:620-632` | — | WARN (W4) |

Eval gate: not applicable (`AI Agent` absent).

## 7. Linting & Code Quality

Hard rules: GPL header 12/12 new Java files PASS · English only PASS · constant in both `APConstants` PASS · migration naming and safety (NOT EXISTS guards, units by type join, user by subquery, DML only) PASS · no new routes PASS · line length 0 violations PASS · i18n **FAIL (F1)**.

**4R advisory (not verdict-driving):**

| ID | Lens | Note |
|---|---|---|
| A1 | Risk | design §11 says every other protocol is refused; only `file`/`jar`/`ftp` get the refusing factory — the URI resolver is what blocks the rest. Text overstates |
| A2 | Reliability | an `Error` outside the pool still propagates; the deadline clock starts after parsing; the late-finish WARN has no `failedFetches` |
| A3 | Resilience | no payload size cap; real cluster 102076 rendered 482 pages / 2.4 MB (fixture: 7) |
| A4 | Readability | `marlo-parent/pom.xml:674` comment links `danfickle/openhtmltopdf`; the groupId is the `io.github.openhtmltopdf` fork |
| A5 | Risk | `quietRendererLogging` sets the level before holding the strong reference (`:239-241`) — small weak-logger window |
| A6 | Security | the end-to-end SSRF test records network opens only; a `file:` read would not show — no marker-in-PDF probe |

**Unresolved advisories carried from `execution.md`:**

- **T01:** the DejaVu licence file lacks the Arev notice. The `-first` variants are missing from the F9 selector. `slf4j-api` is not pinned.
- **T02:**
  - Zero-argument `or` / `ifOr` quirk.
  - `JsNull` equality caveat.
  - `parseJSON` failures are not logged.
  - Raise the vector floor.
  - Inline `{{if x}}` is not guarded.
- **T03:** the trickle read (W5). Empty 200 responses are refetched. Two concurrent misses both fetch.
- **T04:**
  - The null→empty-resource handling belongs in the loader.
  - No loader tests for the `getFailedFetchCount()` branches.
  - `blockedUris` counts attempts, while `failedFetches` counts distinct URIs.
  - CSS `url()` vectors are untested.
  - The PDFBox and Handlebars loggers are outside the leak test.
  - Decompression bomb: accepted, design §17.
- **T06:**
  - Unused `ERROR` return.
  - The null-service 500 carries no message.
  - A write `IOException` is logged as "JSON".
  - `loadData()` still reads the DB with the flag on.
- **T09:** no test pins the in-pool `ExecutionException` path to exactly one ERROR.

## 8. Design Conformance

These conform:
- §9 visibility: only `LocalPdfReportService` and `PdfRenderException` are public.
- §7.4 fonts, dots per pixel and `XRLog` levels.
- §7.5 pool, queue, deadlines, cancel-and-remove and `@PreDestroy`.
- §7.6 setter injection and the null-service path.
- §11 numeric limits.

Recorded deviations, accepted:
- `Content-Length` is set.
- The file name is sanitized.
- The cluster path uses the `renderClusterReportLocally` helper.
- The null→empty-resource handling sits in the service.

| ID | Finding | Result |
|---|---|---|
| W4 | §10 "No template text or payload is logged", yet the FN-008 WARN quotes template tags (≤ 60 chars × 5). Rejected and timed-out renders log ERROR without a stack trace, against OPS-001. Neither exception is written into §10 | WARN |
| W5 | §7.5 "a fetch already in flight can overrun the budget by its own timeouts (≤ 10 s)" is false. The read loop (`PdfResourceLoader.java:418-429`) checks only the per-read timeout, so a slow-trickling allowlisted server holds a render thread longer. The caller's deadline still bounds the user wait. The T03 advisory was never closed or listed in the open items | WARN |
| W8 | Proposal S2 says "no call to Amazon MQ, S3 or CLARISA", but renders still fetch images from two S3 buckets. Moving images under MARLO was deferred without a requirements Decision Log entry; only §4 and design §17 mention it | WARN |

**Cross-document figures:**

| Figure | Result |
|---|---|
| 17 helpers | PASS |
| Pool 3 / queue 6 | PASS |
| Deadlines 30 s / 50 s | PASS |
| Fetch limits: 20 s budget, 5 + 5 s timeouts, 3 MB body, 2 MB `data:`, 32 MB cache, 1 h TTL, 60 s failure memory | PASS |
| Template cache 8 | PASS |
| 27 dots per pixel | PASS |
| Page counts 5 / 6 / 7 | PASS |
| Migration rows 3 / 38 | PASS |
| Vectors 2,414 / 7,712 | PASS |

Drift, recorded as **W7**:
- The requirements Decision Log still says "2,295 vectors" and is not marked superseded.
- Review rounds reached 22 against a budget of 21, with no tripwire entry.

**Proposal alignment:**
- Intent is delivered behind the flag.
- Non-goals were respected.
- S1 (reviewer acceptance) is pending: NF-001.
- S4 and S5 belong to the retirement spec.

## 9. Test Evidence Summary

| Suite | Tests | Covers |
|---|---|---|
| `HandlebarsReportHelpersTest` | 5 (2,414 vectors) | FN-003 |
| `ReportTemplateSubsetTest` | 14 (7,712 vectors) | FN-008 |
| `HtmlPdfCompatibilityTest` | 9 | FN-004 |
| `PdfResourceLoaderTest` | 20 | SEC-001, NF-004 |
| `LocalPdfReportServiceTest` | 28 | FN-001/002/008, SEC-002, NF-001/002/003/005, OPS-001, T09 |
| `LocalPdfEngineDispatchTest` | 14 | FN-005/006/007 |
| Whole `marlo-web` suite on HEAD | 1,392 green | regression |

Live evidence on 2026-11-23:
- **OICR 3508:** 5 pages.
- **Innovation 1558:** 6 pages.
- **Cluster 102076:** 482 pages.
- All three ended `outcome=ok`, with no ERROR, no microservice call and no exception emails.

`/akili-test` was not run (optional), and there is no `test-report.md`.

## 10. Agent Guide / Constitution Impact

There are no `## Constitution Impact` notes. No module was created and no boundary moved, so no child guide is needed.

Pending on `staging`, recorded and not editable on the spec branch:
- TRD §7 integration rows.
- `.agents/*.md` persona drift reported by `akili doctor`.

These are carried to `/akili-archive`: WARN, not blocking.

## 11. Remediation

| ID | Sev | Action | Owner |
|---|---|---|---|
| **F1** | FAIL | Call `response.sendError(500)` without the reason text, as the null-service branch already does; drop or keep the constant for tests only. **Or** record owner approval of the English literal in the requirements Decision Log | Owner decision → Implementer |
| W1 | WARN | Assert the OICR title and section headings with `PDFTextStripper` in `productionTemplatesRender…` | Implementer |
| W2 | WARN | Run a live submit on a throwaway DB with mail disabled, **or** record owner acceptance of unit-level evidence | Owner |
| W4 | WARN | Amend design §10: tag excerpts in the FN-008 WARN; no stack trace on rejected or timed-out renders (by design, message only) | Docs |
| W5 | WARN | Check the fetch budget inside the read loop, **or** correct §7.5 and list it in the open items for the retirement spec | Implementer or Docs |
| W6 | WARN | Make these updates: the §3 tree plus the T02 and T04 file lists; a Testing Plan row for FN-008; FN-008 on T04; the DoD naming T09; the "Last Updated" dates | Docs |
| W7 | WARN | Fix the `execution.md` Summary: T06 and T07 are in `62f0d3a5db` and T09 in `acea7ad51a`. Set the headings' status, record the 22/21 review rounds, and mark "2,295" as superseded | Docs |
| W8 | WARN | Add a requirements Decision Log entry deferring MARLO-served images; restate S2 | Docs |
| NF-001 | BLOCKED | Owner reviews `spike/t07-production-vs-local.png` (and optionally one real cluster PDF) and accepts the layout and pagination differences | Owner |

## 12. Archive Readiness Recommendation

**Not ready.** These must be done before `/akili-archive changes/local-pdf-report-engine`:

1. Resolve F1.
2. Give the NF-001 owner acceptance.
3. Accept or fix W1–W8. W4 and W6–W8 are doc-only edits. W2 and W5 are each either a decision or a small change.

Then re-run `/akili-validate` (an incremental re-check is enough).

## 13. Re-check — 2026-11-23 (after T10 and the documentation fixes)

Incremental re-validation of every first-pass finding. The build gates were re-run by the Leader on the working tree: clean compile 2430 / 1070 / 148; `mvn -o -pl marlo-web -am test` exit 0, **1,395 tests, 0 failures**; Checkstyle on T10's four files HEAD 0 / working 0; hygiene ok.

| ID | First pass | Resolution | Evidence | Now |
|---|---|---|---|---|
| F1 | FAIL | `sendError(500)` with no reason text; constant removed (T10) | `LocalPdfReportService.renderToResponse`; 3 SEC-002 tests assert null reason; requirements Decision Log 2026-11-23 | PASS |
| W1 | WARN | Title and two headings per production template asserted via `PDFTextStripper` (T10) | `productionTemplatesRender…` | PASS |
| W2 | WARN | Owner accepted unit-test evidence for the submit path: "acepto la evidencia de tests del submit" | requirements Decision Log 2026-11-23 | PASS (accepted) |
| W4 | WARN | design §10 amended: tag excerpts in the FN-008 WARN; no stack trace on `rejected` / `timeout` | design §10 | PASS |
| W5 | WARN | Budget checked after the status line and between reads; a budget cut is never remembered or cached; bound restated accurately (T10, reviewer attempt 2 PASS) | `PdfResourceLoader.download` / `fetch`; two new loader tests; design §7.5 | PASS |
| W6 | WARN | design §3 tree (+ `ReportTemplateSubset`, dispatch test, vectors); T02 files; FN-008 on T04; Testing Plan rows FN-008 / T09 / T10; DoD names T09, T10; Last Updated dates | design.md, tasks.md, requirements.md | PASS |
| W7 | WARN | execution.md headings and Summary commits corrected; 22 → 24 review rounds recorded with the approvals that cover them; "2,295" marked superseded | execution.md "Budget note"; requirements Decision Log | PASS |
| W8 | WARN | Decision Log entry: images stay on S3 for this spec; proposal S2 restated | requirements Decision Log; proposal S2 | PASS |
| NF-001 | BLOCKED | Owner acceptance of the layout and pagination differences (`spike/t07-production-vs-local.png`) | — | **BLOCKED (owner)** |

Advisories A1–A6, the carried `execution.md` advisories and the two T10 reviewer advisories remain advisory. They are inputs for the retirement spec, not blockers.

**Archive readiness:** ready once the owner accepts NF-001. After that acceptance is recorded, the next command is `/akili-archive changes/local-pdf-report-engine`.
