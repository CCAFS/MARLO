# Test Report — Portable PDF Engine

> **Overall: PASS.** All 1,511 tests in the `marlo-web` suite pass with `-am` (0 failures, 6 skipped). That
> includes 9 new tests that close the five clauses validation found untested. Each new test was shown to fail on its
> own assertion before it passed. No `PRODUCT_BUG`. Two browser E2E journeys were added at the owner's request
> (2026-11-25): the local app on macOS and the MARLO Docker image (Linux amd64, Tomcat 9). In both, 3 of 3 reports
> were downloaded by UI clicks: `200 application/pdf`, DejaVu only, text on every page. Integration evidence comes
> from the T06 runs and the validation remediation, and is cited: Linux containers and 8/8 parity cases. The one gap
> is Windows at runtime, which the owner accepted on 2026-11-25.

## 1. Document Control

| Field | Value |
|---|---|
| Spec | CHG-PORTABLE-PDF-ENGINE-001 · `docs/specs/changes/portable-pdf-engine/` |
| Branch / base | `feature/local-pdf-report-engine` @ `aa99aac628` + uncommitted test additions |
| Date | 2026-11-25 |
| Leader | Claude Code session (opus), T1 |
| Tester | `akili-tester` wrapper (opus), effort `high`. Implementers were sonnet, so author ≠ tester holds |
| Deliverable / AI Agent | software / no (no render-evidence or agent-eval suite) |

## 2. Summary

| Item | Value |
|---|---|
| Suites planned | 1 delegated (backend unit), plus integration and E2E cited from existing evidence |
| Testers spawned | 1 (sequential; one suite) |
| Inline work | Leader re-ran the tests and Checkstyle to check the Tester's evidence (below) |
| New tests | 9, in `PhantomJsLibrariesTest` (+5), `EmbeddedPhantomJsTest` (+1), new `PhantomJsResourceAccessTest` (3); test helper `IsolatedPdfClassLoader`; 2 new fake modes in `fake-phantomjs.sh` (5 lines added, none removed) |
| Production code changed | none (`git diff --quiet -- marlo-web/src/main` → clean) |
| E2E | 2 browser journeys (macOS app, Docker image Linux amd64): 6/6 downloads `200 application/pdf`, DejaVu only, decoy binary never run (§6) |
| Results | PDF package `Tests run: 209, Failures: 0, Errors: 0, Skipped: 6`; full suite `Tests run: 1511, Failures: 0, Errors: 0, Skipped: 6` |
| Red proof | 6 temporary production mutations (M1–M6), applied together and reverted by `git checkout`, turned all 9 new tests red, each on its own assertion: `Tests run: 53, Failures: 10` (the tenth is an existing test M6 also breaks) |
| Gates | clean compile 2430/1073/157; Checkstyle `--baseline` on the 4 touched test files HEAD=0, working tree=0, delta=0; hygiene 0 FIX |
| Leader re-check | `git diff --quiet -- marlo-web/src/main` clean; PDF package re-run `209/0/0/6`; Checkstyle delta 0 |
| Skills | `marlo-verify`, `tdd` (Tester) |

## 3. Backend Unit Tests

### New (this run)

| Test | Requirement — clause | What is asserted | Red observed (mutation) |
|---|---|---|---|
| `PhantomJsLibrariesTest.aHostProbeThatTimesOutKeepsTheHostLibrariesForEveryRenderAndNeverOpensTheShippedOnes` | FN-002 / DD-2 — a probe that cannot be judged keeps HOST ("those MUST keep being used as today") | returns within `VERSION_TIMEOUT_MS`+5 s; DEBUG `cause=DeadlineExpiredException`; mode `host`; no `libs/`; the libraries double records nothing; `libpath.version` `[unset]`; render environment has no `LD_LIBRARY_PATH`; no second probe | M1 → `expected:<[host]> but was:<[bundled]>` |
| `…aHostProbeThatCannotStartKeepsTheHostLibrariesAndNeverOpensTheShippedOnes` | FN-002 / DD-2 | `cause=PdfRenderException`; mode `host`; no `libs/`; double records nothing; no `--version` run; render fails at once | M1 → same |
| `…aRetryThatCannotStartMakesTheBuildUnavailableWithOneWarnAndNoRender` | FN-007 — "MUST fail fast … MUST NOT hang" | double received `expected` + `open` for each of the 7 libraries, checked against a literal map; `LIBRARIES_UNAVAILABLE_MESSAGE`; mode `unavailable`; no render process; exactly 1 WARN with the glibc hint and `platform=Linux/amd64` | M2 → `prepared a build whose retry with the shipped libraries could not be judged` |
| `…aRetryThatHangsIsKilledAtTheVersionTimeoutAndTheBuildIsUnavailable` | FN-007 — must NOT hang | as above, plus finishes within the timeout + 5 s; `cause=DeadlineExpiredException`; two probes, the second with `libs` | M2 → same |
| `…onMacOsAndWindowsTheShippedLibrariesAreNeverOpenedNorExtracted` | NF-003 — "only the current platform's build and libraries" | Mac aarch64 / x86_64, Windows amd64: double records nothing; mode `n/a`; no `libs/` | M3 → `Mac OS X/aarch64 expected:<[n/a]> but was:<[bundled]>` |
| `EmbeddedPhantomJsTest.theStartupLineNamesTheMacOsAndWindowsPlatformTheirBuildAndNoLibraries` | OPS-002 — line names platform, build, version | Mac ×2, Windows ×2: exactly 1 INFO `…version=2.1.1, platform=<os>/<arch>, build=macosx|windows, libraries=n/a, path=…phantomjs[.exe]`; no WARN; each build opened once | M6 → `…build=macosx, libraries=host…` |
| `PhantomJsResourceAccessTest.eachPlatformOpensOnlyItsOwnBuildAndChecksumAndNothingOfTheOtherPlatforms` | NF-003 | real `system()` + `extractTo` behind a recording class loader: the list of opened resource **names** equals `[<own>.sha256]`, then `[<own>.sha256, <own>.gz]`, for Linux, macOS and Windows; the expected names are literals | M4 → list showed all three checksums |
| `…aPlatformWithNoBuildOpensNoResourceAtAll` | NF-003 + FN-001 "OS and architecture together" | Linux/aarch64: `extractTo` throws `IOException`; recorded list empty | M4 → `expected:<[]> but was:<[…3 checksums]>` |
| `…aMissingDejaVuResourceRefusesTheRenderAndPhantomJsNeverRunsWithoutTheFonts` | FN-005 / DD-3 — "only the DejaVu fonts … No font installed on the host MAY appear" | control render first; then `DejaVuSans.ttf` hidden: `PdfRenderException` with exactly `PhantomJS render could not complete (IOException)` (no path); the font was requested after hiding; `fake.started()` empty (no fallback to an unfonted page) | M5 → `rendered without the DejaVu fonts in the page` |

### Existing (cited, unchanged)

`PhantomJsBuildTest` (platform matrix), `ShippedPhantomJsBuildsTest` (integrity of the 3 builds), `EmbeddedPhantomJsTest`
(Windows seam, override wins, tamper, wrong checksum), `PhantomJsLibrariesTest` (BUNDLED/HOST, tampered/missing/extra
library, `java.library.path` unchanged), `PhantomJsRendererTest` (environments per style, owner normaliser, page-font
position, `</head>` breakout and family-hijack regressions, Linux byte identity, allowlist), `LocalPdfReportServiceTest`
(unsupported platform → generic 500, startup lines), `LocalPdfEngineDispatchTest` (flag on → microservice never called).

### Considered, not added

| Gap | Reason |
|---|---|
| FN-007 through the dispatch path with the platform seam (T02 Tests item) | Already proven when two existing tests are read together. The four action call sites only call `render` / `renderToResponse`, which the dispatch double records (`ReportingSummaryAction:7934/7939`, `ProjectInnovationSummaryAction:1692`, `BaseStudySummaryData:1565`). The dispatch rows show the microservice is never called when the flag is on, even when the render fails. `LocalPdfReportServiceTest.anUnsupportedPlatform…` proves the generic 500 for an unsupported platform. A new test would need a public test-only factory |

## 4. Frontend Unit Tests

Not applicable: the spec changes no UI, FTL, CSS or JS (`render-page.js` untouched).

## 5. Integration Tests (cited evidence)

| Evidence | Result | Source |
|---|---|---|
| Gated `PhantomJsRenderingTest`, Linux `tomcat:9.0-jdk17-temurin`, `--network none` | `OK (8 tests)` | `execution.md` T06 (1) |
| Gated `PhantomJsRenderingTest`, macOS shipped build | 8 run / 6 skipped (the other platforms); fonts `[DejaVuSans, DejaVuSans-Bold]`, 5/6/7 pages | T05, T06 (3) |
| Linux parity, 8/8 CHG-LPRE cases vs `e452378187` | 0 differing pages: 3 fixtures (T06) + OICR 3508, Innovation 1558, Cluster 102076 (481 pages), missing-flag and font probes | T06 (1), `validation-report.md` §13 |
| Minimal Linux `debian:bookworm-slim` (fontlibs 0) | `libraries=bundled`; 0/18 pages differ | T06 (2) |
| Old glibc `amazonlinux:2023` (2.34) | one FN-007 WARN with platform; renders fail in 121–364 ms | T06 (2) |
| Falsifier: page fonts forced on Linux | 18/18 fixture pages differ; real cases differ; cluster times out | T06 (1), §13 |

## 6. E2E Tests (browser, 2026-11-25)

Owner request: "haz las dos pruebas antes de archivar". One Tester (`akili-tester`, opus) ran the two journeys one
after the other at HEAD `fbd2a59a86` and changed no code. The Leader re-checked the six PDFs with poppler and checked
the cleanup.

**Shared setup:**
- Database: local `lpre_live_tmp`, with `local_pdf_report_engine_active=true` for AICCRA (unit 45).
- `pdf.phantomjs.path` empty.
- Login through the login page with the local dev credentials, which were never printed.
- Every download triggered by real clicks in Chrome through Playwright, with a screenshot of each click target.

**Decoy for the "must NOT read any property or environment variable" clause:**
- Both runs started with `PHANTOMJS_PATH` and `PHANTOMJS_BIN` pointing at a decoy `phantomjs`, and the decoy first
  on `PATH`.
- The decoy writes a marker file when it runs. It **never ran**.
- The property side rests on code reading: only `os.name` and `os.arch` are read.

### Journey A — local app, macOS (Apple Silicon), `run-marlo-java17.sh`

Startup line: `PhantomJS available for local PDF renders, version=2.1.1, platform=Mac OS X/aarch64, build=macosx, libraries=n/a, path=…/phantomjs`

| Report | Triggered by | HTTP / type | Pages | Fonts | Words (all pages) | Render line |
|---|---|---|---|---|---|---|
| OICR 3508 | Clusters → C102080 → OICRs → PDF icon | 200 `application/pdf` | 4 | DejaVuSans, DejaVuSans-Bold | 523, 0 empty pages | `outcome=ok pages=4 elapsedMs=6516 blockedUris=0 failedFetches=0` |
| Innovation 1558 | Clusters → C102084 → Innovations → PDF icon | 200 | 6 | same | 927, 0 empty | `outcome=ok pages=6 elapsedMs=8932 …=0` |
| Project 102076 | Summaries → "Full cluster report" → Generate | 200 | 169 | same | 25,783, 0 empty | `outcome=ok pages=169 elapsedMs=19932 …=0` |

### Journey B — MARLO Docker image (`Dockerfile`, `--platform linux/amd64`, Tomcat 9, glibc 2.43)

Built in a temporary worktree (BUILD SUCCESS). The container reached the host MySQL through `host.docker.internal`;
no Tomcat config was changed.

Startup line: `PhantomJS available for local PDF renders, version=2.1.1, platform=Linux/amd64, build=linux-x86_64, libraries=host, path=/usr/local/tomcat/temp/…/phantomjs`

| Report | HTTP / type | Pages | Fonts | Words (all pages) | Render line |
|---|---|---|---|---|---|
| OICR 3508 | 200 `application/pdf` | 5 | DejaVuSans, DejaVuSans-Bold | 563, 0 empty | `outcome=ok pages=5 elapsedMs=4483 blockedUris=0 failedFetches=0` |
| Innovation 1558 | 200 | 6 | same | 928, 0 empty | `outcome=ok pages=6 elapsedMs=3429 …=0` |
| Project 102076 | 200 | 173 | same | 25,887, 0 empty | `outcome=ok pages=173 elapsedMs=8092 …=0` |

**Reading the results:**
- **Page counts differ between the platforms.** Linux gives 5/6/173 and macOS 4/6/169; NF-002 allows macOS to be
  close to Linux rather than identical, with Linux the parity reference. No production page count exists for these
  live records.
- **The project report has a different URL from T06.** The UI's Generate button calls
  `reportingSummary.do?phaseID=431&format=PDF&projectID=102076`, while T06 used `cycle=Reporting&year=2026`. It is
  the same phase.
- **Logs are clean.** Neither log has a PDF or PhantomJS WARN or ERROR.
- **Timings in B are not production figures.** They ran under amd64 emulation.

**Incidents, recorded:**
- **Submit dialog cancelled.** While looking for the report link in A, the Tester clicked "Submit cluster" for
  C102076 and **cancelled** the confirmation dialog. `lpre_live_tmp.submissions` shows no row written that day (the
  latest for 102076 is from 2026-01-05).
- **Local image tag restored.** A DB reachability check pulled an amd64 `mysql:8.4` that replaced the local tag. The
  tag was put back on the original image (`a81d043930e5`) and the pulled image deleted.

**Cleanup verified by the Leader:**
- App stopped and port 8080 free.
- `marlo-dev.properties` SHA-1 `b3801c73…` equals the original.
- Container and image `marlo:e2e-local` removed, together with the 26 build-cache records that held project content.
  The image carried a copy of the local credentials file.
- Worktree removed and the scratch properties file deleted.
- Main checkout clean at `fbd2a59a86`.

## 7. Coverage & Traceability

| Requirement | Scenario / clause | Test type | Test or evidence | Result | Notes |
|---|---|---|---|---|---|
| FN-001 | Shipped build per platform; OS + arch together; no property read | Unit + integration + E2E | `PhantomJsBuildTest`; `PhantomJsResourceAccessTest` ×2; T06 (1)(3); §6 journeys A+B (decoy env and PATH binary never run) | PASS | Windows runtime: accepted gap |
| FN-002 | Minimal Linux; host libraries kept; probe that cannot be judged | Unit + integration | `PhantomJsLibrariesTest` (incl. 2 new); T06 (2) | PASS | |
| FN-003 | Library path only in PhantomJS | Unit | `withoutHostLibraries…GivenToPhantomJsOnly` | PASS | |
| FN-004 | Windows name, permissions; BUT no POSIX API | Unit (seam) | `aWindowsExtraction…NeverCallsThePosixView…`; owner-normaliser tests | PASS (unit) | Lock and sweep on NTFS: accepted gap |
| FN-005 | Only DejaVu, text extractable; no host font | Unit + integration + E2E | `PhantomJsRendererTest` font tests; `aMissingDejaVuResource…` (new); macOS gated + live | PASS (Linux, macOS) | Windows (OQ-7): accepted gap |
| FN-006 | Override wins | Unit | `aConfiguredPathAlwaysWins…` | PASS | |
| FN-007 | No build / old glibc → generic 500, one line naming the platform, no hang, no microservice | Unit + integration | `anUnsupportedPlatform…`; 2 new retry tests; dispatch rows; T06 amazonlinux | PASS | |
| NF-001 | Linux parity on 8 cases | Integration | §5 parity row | PASS | |
| NF-002 | macOS/Windows page counts, DejaVu | Integration | macOS gated | PASS (macOS) | Windows: accepted gap |
| NF-003 | Only this platform's build and libraries; no download | Unit + integration | 3 new tests; `--network none` runs | PASS | |
| NF-004 / NF-006 | WAR ≤ 60 MB; first render within the deadline | Measurement | T06 (5)(6): 36.1 MB; 1.6–6.9 s | PASS | |
| NF-005 | No Maven dependency | Static | empty pom diff | PASS | |
| SEC-001 | Tampered binary/library re-extracted or refused; WAR resource mismatch refused | Unit | existing tamper/checksum tests | PASS | |
| SEC-002 | CHG-LPRE protections on every platform | Unit | `theArgumentsAreTheSameOnLinuxAndMacos`, environment tests | PASS | Windows argument sameness by code reading (recorded) |
| SEC-003 | Fonts after the sanitizer; allowlist unchanged | Unit | `pageBuildsGetTheDejaVuBlockAtTheStart…`, breakout and hijack regressions, allowlist test | PASS | |
| OPS-001 | Provenance and licences | Static | `ShippedPhantomJsBuildsTest`; validation re-hash | PASS | |
| OPS-002 | Startup line: platform, build, version | Unit + integration | Linux forms (existing) + macOS/Windows forms (new); T06 logs | PASS | |

Every negative or strict clause (`BUT it must NOT` / `AND IT MUST`) that can be checked off Windows is mapped above
with test or run evidence.

## 8. Remediation

None required: no FAIL, no `PRODUCT_BUG`.

Notes for maintainers (advisory):
- **`PhantomJsResourceAccessTest` changes JVM settings during the test.** It sets `os.name` / `os.arch` for one
  `system()` call and restores them in `finally`. That is safe with Surefire's serial run here, and unsafe if tests ever
  run in parallel.
- **`IsolatedPdfClassLoader` depends on how resources are loaded.** It sees resources only while the engine loads them
  through its class loader (`Class.getResourceAsStream`). A move to named modules or the context class loader would turn
  these tests red, which is the safe direction.
- **Stale `target/` crashes.** After the mutations were reverted, the first package run crashed with
  `NoClassDefFoundError: GlobalUnit` (the known stale-`target/` symptom) and passed after a clean compile.
- **OTP auth WARN lines are not new.** The full suite prints `auth.otp.fault … NoClassDefFoundError` WARN lines from
  OTP auth tests. They already appear at baseline (3 in the validation run) and are unrelated to this spec.
- **Known flakes did not reproduce.** Neither `anInterruptedThreadKillsTheProcessAndKeepsItsInterruptFlag` nor the
  macOS cold-start timeout fired in this run.

## 9. Accepted Gaps

| Gap | Reason | Recorded |
|---|---|---|
| Windows at runtime: the process starting, directory lock and stale-directory sweep on NTFS, page fonts kept as text (P-16, P-17, P-18, OQ-7) | No Windows host; Windows is not a production platform | `requirements.md` §9 Decision Log 2026-11-25 (owner) |
| E2E automation in CI | The browser journeys (§6) need a logged-in session, the local DB and the specificity flag; run manually with Playwright and recorded | §6 |
| `compare.py` against the production microservice | Its inputs (reports-microservice source) are not in this repo. Parity holds through pixel identity with `e452378187`, where CHG-LPRE measured PARITY | `validation-report.md` §13 |
