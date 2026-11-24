# Portable PDF Engine — Tasks

**Spec ID:** CHG-PORTABLE-PDF-ENGINE-001
**Status:** Approved — owner, 2026-11-24 (Phase 1, 2 and 3 gates)
**Owner:** Kenji Tanaka (IBD)
**Last Updated:** 2026-11-24
**Implements:** `requirements.md` (CHG-PPE-*) via `design.md` DD-1 … DD-7

> **Answer first:** six tasks, in this order: ship the binaries (T01), choose them per platform (T02), fix the process
> environment for Windows (T03), add the Linux library fallback (T04), add DejaVu to the page on macOS/Windows (T05),
> and run the cross-platform verification (T06). About 650 Java lines; one PR.

---

## 1. Execution Context

| Item | Value |
|---|---|
| Branch | `feature/local-pdf-report-engine` (owner decision, proposal OQ-1); nothing to `staging` until done |
| Java | 17 (`marlo-parent/pom.xml`) |
| Build gate | `marlo-verify`: clean recompile, Checkstyle delta, hygiene, tests with `-am` |
| Containers | Docker Desktop, `--platform linux/amd64`: `tomcat:9.0-jdk17-temurin`, `debian:bookworm-slim`, `amazonlinux:2023` |
| macOS host | This Apple Silicon Mac (Rosetta present) |
| Windows host | None yet (OQ-2) |
| Run script | Not needed: every check runs through tests or the spike harness. If the app is started, follow `docs/infrastructure.md` §6 and never while an Implementer is building |

## 2. Pre-flight Checklist

- [ ] `requirements.md` and `design.md` approved at their gates.
- [ ] On `feature/local-pdf-report-engine`; working tree clean apart from the untracked `log.folder_IS_UNDEFINED/`.
- [ ] No other AKILI session or Maven build running in this checkout.

## 3. Task List

### CHG-PPE-T01 — Ship the macOS and Windows builds
- **Status:** [x] · **Size:** S · **Depends on:** none · **Module:** marlo-web (resources, tests)
- **Requirements:** FN-001 (the builds exist), SEC-001 (the checksums), OPS-001, NF-004 · **Design:** DD-7, §2, Spike S-4/S-5
- **Files touched:** `resources/pdf/phantomjs/phantomjs-2.1.1-macosx.gz`, `.sha256`,
  `phantomjs-2.1.1-windows.exe.gz`, `.sha256`, `PROVENANCE-macosx.txt`, `PROVENANCE-windows.txt`,
  `third-party.txt` / `LICENSE.BSD` checked identical across the three zips; a resource-integrity test.
- **Scope:** download both zips from `github.com/Medium/phantomjs/releases/download/v2.1.1/`; check the zip SHA-256
  (`538cf488…150f8c1` macOS, `d9fb0562…c7dcc8` Windows); store `bin/phantomjs` / `bin/phantomjs.exe` unmodified,
  `gzip -9n`; record upstream URL, zip SHA-256, binary SHA-256 and the date in the provenance file, in the shape of
  the existing `PROVENANCE.txt`. Record that the Windows build is a 32-bit, UPX-compressed PE (design §17).
  Backend route: `stack other; implement lead existing design support none — project conventions; test-suite lead the spec's scenarios support none — project conventions`
- **Tests:** for each of the three shipped builds, the gzip resource decompresses to bytes whose SHA-256 equals its
  `.sha256` file.
- **Verification:** `mvn -q -o -pl marlo-web -am test -Dtest=<integrity test>`; `ls -l` of the two `.gz` (17.1 MB and
  18.1 MB expected ±0.1).
- **Falsifier:** change one hex digit of `phantomjs-2.1.1-windows.exe.sha256` → the integrity test fails on the
  Windows row and only that row.
- **Red run:** `predicted — greenfield artifact (observed in T01)`.
- **Disqualifier:** a test that hashes the resource without decompressing it checks the gzip, not the binary MARLO
  will run; it is not evidence.
- **Consumers:** none — no code reads these resources yet (T02 does).
- **Review:** `checklist` — binary resources and one data-driven test; the provenance text is the review surface.
- **Skills:** `marlo-verify`, `marlo-commit`.
- **Done when:** the integrity test is green for the three builds, the falsifier red was observed and quoted in
  `execution.md`, and both provenance files name the URL and both checksums.

### CHG-PPE-T02 — One platform table chooses the shipped build
- **Status:** [x] · **Size:** M · **Depends on:** T01 · **Module:** marlo-web
- **Requirements:** FN-001 (all scenarios of "Shipped build per platform"), FN-004 (executable name, permissions:
  "BUT it must NOT call any POSIX-only file API on Windows"), FN-006 ("Override set"), FN-007 ("Platform with no
  build": generic 500, no microservice, no hang) · **Design:** DD-1, DD-5, P-1, P-2, P-14, P-15
- **Files touched:** `utils/pdf/PhantomJsBuild.java` (new, GPL header), `EmbeddedPhantomJs.java`,
  `PhantomJsRenderer.java` (executable name from the build), `LocalPdfReportService.java` (unsupported-platform line
  names the platform; startup line names the build), `marlo-utils/.../APConfig.java` (Javadoc of
  `pdf.phantomjs.path`: optional override); `PhantomJsBuildTest.java` (new), `EmbeddedPhantomJsTest.java`.
- **Scope:** `PhantomJsBuild.forPlatform(osName, osArch)` returns the build or none, per DD-1:
  Linux + `amd64`/`x86_64`; macOS + `x86_64`/`aarch64`; Windows + `amd64`/`x86_64`/`x86`. Each build carries its
  resource, checksum resource, executable file name, font mode (LINUX_FONTCONFIG / PAGE) and environment style
  (POSIX / WINDOWS). `EmbeddedPhantomJs` takes the build. The POSIX permission call runs only when the target's file
  store supports `"posix"`, behind a seam the test can drive. The override path keeps priority untouched.
  Backend route: `stack other; module-design lead existing design support none — project conventions; implement lead existing design support none — project conventions; test-first lead tdd support none — project conventions`
- **Tests:** `PhantomJsBuildTest` over the full matrix — Linux `amd64`, `x86_64`, `aarch64`, `arm`; `Mac OS X`
  `x86_64`, `aarch64`; `Windows 10`/`Windows 11` `amd64`, `x86`, `aarch64`; `FreeBSD amd64`; empty strings. Each row
  asserts the **resource path, checksum resource, file name and font mode** the selector returned, or none
  (KZ-…-auth-flow-2: assert what the double received, not only that something was returned).
  `EmbeddedPhantomJsTest`: extraction with a non-POSIX file store seam writes `phantomjs.exe`, never calls the
  POSIX view, and still verifies the SHA-256; POSIX extraction keeps `rwx------` (existing assertions).
  `LocalPdfReportServiceTest`: an unsupported platform logs one WARN naming it, and a flag-on render answers the
  generic 500 without calling the microservice (existing CHG-LPRE dispatch test extended with the platform seam).
- **Verification:** `mvn -q -o -pl marlo-web -am test -Dtest='PhantomJsBuildTest,EmbeddedPhantomJsTest,LocalPdfReportServiceTest,LocalPdfEngineDispatchTest'`;
  clean recompile; Checkstyle delta 0 on the touched files; hygiene.
- **Falsifier:** map `Mac OS X`/`aarch64` to none → the macOS-aarch64 row fails; map `Linux`/`aarch64` to the
  x86_64 build → that row fails ("an ARM64 Linux never gets the x86_64 build").
- **Red run:** observed at baseline (`e452378187`, reflection on `EmbeddedPhantomJs.isSupportedPlatform()`):
  `Mac OS X/x86_64 -> false`, `Mac OS X/aarch64 -> false`, `Windows 11/amd64 -> false`, `Windows 10/x86 -> false`,
  `FILE_NAME=phantomjs`. `PhantomJsBuild` itself: `predicted — greenfield artifact (observed in T02)`.
- **Disqualifier:** a matrix test that asserts only "a build was returned" passes on the falsifier's wrong mapping;
  rows must assert the resource path.
- **Consumers:** `EmbeddedPhantomJsTest.java:132-134, 177, 192, 228-229, 309, 525`; `FakePhantomJs.java:39`;
  `LocalPdfEngineDispatchTest` (P-14). `LocalPdfReportService` is read by the three summary actions only
  (P-12) — their behavior is unchanged.
- **Review:** `full` — new selection logic on every platform, and the FN-007 negative path.
- **Skills:** `tdd`, `marlo-verify`, `marlo-commit`.
- **Done when:** the matrix and extraction tests are green, the two falsifier reds were observed and quoted, the
  Linux x86_64 row still yields the CHG-LPRE resource byte for byte, and Checkstyle delta is 0.

### CHG-PPE-T03 — Child-process environment per platform, Windows sweep
- **Status:** [x] · **Size:** S · **Depends on:** T02 · **Module:** marlo-web
- **Requirements:** FN-004 (Windows: "a later startup sweeps stale support directories as on Linux"), SEC-002
  (protections unchanged on every platform), FN-003 ("MUST NOT change the environment of any other process") ·
  **Design:** DD-4, DD-6, P-9, P-13, P-16, P-17
- **Files touched:** `PhantomJsRenderer.java` (`baseEnvironment`, `FONTCONFIG_FILE` on Linux only,
  `sweepIfStale` owner comparison), `PhantomJsRendererTest.java`.
- **Scope:** POSIX style for Linux and macOS (today's keys and values); WINDOWS style: `SystemRoot` copied from the
  JVM's environment, `PATH=<SystemRoot>\System32`, `TEMP`, `TMP`, `USERPROFILE` = the render home,
  `OPENSSL_CONF=NUL`, `LANG`. `FONTCONFIG_FILE` only for LINUX_FONTCONFIG builds. Owner comparison strips a leading
  `DOMAIN\` before comparing with `user.name`.
  Backend route: `stack other; implement lead existing design support none — project conventions; test-suite lead the spec's scenarios support none — project conventions`
- **Tests:** environment key **and value** sets per style (Linux: unchanged, the existing assertions at
  `PhantomJsRendererTest.java:136-142, 545-547` keep passing; macOS: same minus `FONTCONFIG_FILE`; Windows: the
  Windows set, no `/dev/null`, no `/usr/bin`); the owner normaliser on `user`, `DOMAIN\user`, `MACHINE\user`,
  `other`; the sanitizer, allowlist and `--local-url-access=false` arguments identical for all three styles.
- **Verification:** `mvn -q -o -pl marlo-web -am test -Dtest=PhantomJsRendererTest`; clean recompile; Checkstyle
  delta 0; hygiene.
- **Falsifier:** give the Windows build the POSIX style → the Windows environment test fails on `OPENSSL_CONF`
  (`/dev/null` ≠ `NUL`) and on the missing `SystemRoot`.
- **Red run:** baseline behavior observed: every render's environment today is
  `{FONTCONFIG_FILE, HOME, LANG, OPENSSL_CONF, PATH, TMPDIR}` with `OPENSSL_CONF=/dev/null`
  (`PhantomJsRendererTest.java:136-138`, green at `e452378187`). The Windows set and the owner normaliser:
  `predicted — greenfield artifact (observed in T03)`.
- **Disqualifier:** a Windows environment test that checks only the key names passes with POSIX values; values
  must be asserted. P-16 and P-17 stay UNVERIFIED until a Windows run (T06): a green unit test is not evidence that
  Windows starts the process.
- **Consumers:** `PhantomJsRendererTest.java:136-142, 160, 260, 267, 286, 291, 545-547` (P-14).
- **Review:** `full` — the environment is part of the CHG-LPRE security boundary (SEC-002).
- **Skills:** `marlo-verify`, `marlo-commit`.
- **Done when:** tests green, the falsifier red observed and quoted, the Linux key/value set unchanged byte for byte.

### CHG-PPE-T04 — Linux library fallback
- **Status:** [ ] · **Size:** M · **Depends on:** T02, T03 · **Module:** marlo-web (code, resources, tests)
- **Requirements:** FN-002 (all three scenarios of "Minimal Linux": no font libraries; host libraries present
  "BUT it must NOT behave differently from CHG-LPRE"; old glibc), FN-003 ("AND IT MUST NOT change the library path
  of the JVM or of any process other than the PhantomJS it starts"), FN-007 (old-glibc case), SEC-001
  ("Tampered file" scenario, libraries), OPS-002 (library mode and platform — `os.name`/`os.arch` — in the startup line; platform added by the 2026-11-24 amendment) · **Design:** DD-2, §11, §13,
  P-3, P-4, P-5, P-13
- **Files touched:** `resources/pdf/phantomjs/linux-libs/` (7 `*.so*.gz` from Debian 12 per spike S-2,
  `SHA256SUMS`, `PROVENANCE.txt` with package names and versions, the Debian `copyright` files);
  `EmbeddedPhantomJs.java` (library extraction with per-file SHA-256); `PhantomJsRenderer.java` (probe, library
  mode, `LD_LIBRARY_PATH` in the child environment only, completeness and intact checks cover `libs/` in BUNDLED
  mode); `LocalPdfReportService.java` (startup line, FN-007 WARN); tests.
- **Scope:** at `prepare()`/startup on a LINUX build with no override: run `--version` with the host libraries;
  only when it fails with "error while loading shared libraries", extract the libraries into the support directory
  and retry with `LD_LIBRARY_PATH=<support>/libs`; keep the mode for every render; when both fail, WARN once
  (glibc hint) and renders fail with the generic 500. In HOST mode no library is extracted and `LD_LIBRARY_PATH` is
  absent from the child environment.
  Backend route: `stack other; implement lead existing design support none — project conventions; error-model lead error-handling-patterns support none — project conventions; test-first lead tdd support none — project conventions`
- **Tests:** a fake PhantomJS that answers `--version` only when `LD_LIBRARY_PATH` names a directory holding the
  shipped libraries, and otherwise exits 127 with the loader message → mode BUNDLED, libraries extracted and
  verified; a fake that always answers → mode HOST, no `libs/` directory, no `LD_LIBRARY_PATH` in the environment;
  a fake that always fails → WARN once and a generic 500 on render; a library altered after extraction → re-extracted
  before use; a library resource whose bytes do not match `SHA256SUMS` → never written, render refused;
  `System.getProperty("java.library.path")` identical before and after a BUNDLED render.
- **Verification:** unit tests (`-am`); then, from the spike harness against the built classes:
  (1) `debian:bookworm-slim --network none`, no override → the three fixtures render, and per-page
  `pdftoppm -r 72 | shasum` equals the Tomcat-image render for every page;
  (2) `amazonlinux:2023 --network none` → the FN-007 WARN and a failed render with no hang;
  (3) `tomcat:9.0-jdk17-temurin` → startup says `libraries=host` and the gated `PhantomJsRenderingTest` is OK.
- **Falsifier:** make the probe always choose BUNDLED → the HOST test fails (`LD_LIBRARY_PATH` present), and on
  `amazonlinux:2023` with font libraries installed the render breaks; make it never fall back → the BUNDLED test
  fails and `bookworm-slim` returns `phantomExit=127`.
- **Red run:** observed at baseline (`e452378187`), `debian:bookworm-slim --network none`, shipped build, OICR
  fixture: `PDF render failed, file=oicr-fixture.pdf, phantomExit=127` /
  `PDF render file=oicr-fixture.pdf outcome=failed bytes=0 pages=0 elapsedMs=1498 blockedUris=0 failedFetches=0`.
- **Disqualifier:** a container run in an image that already has `libfontconfig` (the Tomcat image) proves nothing
  about FN-002: check `ls /usr/lib/x86_64-linux-gnu | grep -cE 'fontconfig|freetype'` → `0` in the image used. A
  render that ends `ok` but whose pages differ in pixels from the Tomcat image is a failure, not a pass.
- **Consumers:** `PhantomJsRendererTest` (environment sets, P-14); `EmbeddedPhantomJsTest` (extraction);
  `PhantomJsRenderingTest` (gated, Linux).
- **Review:** `full` — new startup decision, a new process-environment variable and third-party binaries.
- **Skills:** `tdd`, `error-handling-patterns`, `marlo-verify`, `marlo-commit`.
- **Done when:** unit tests green; the three container checks done with their output quoted; the falsifier reds
  observed; provenance lists all seven libraries with package versions.

### CHG-PPE-T05 — DejaVu in the page on macOS and Windows
- **Status:** [ ] · **Size:** S · **Depends on:** T02 · **Module:** marlo-web
- **Requirements:** FN-005 ("only DejaVu", "text stays extractable"), NF-002, SEC-003 ("added after the sanitizer",
  "MUST win over every font … without `!important`", "allowlist MUST NOT be widened"); the scenarios "Fonts on macOS
  and Windows" and "Template or payload declares its own font" · **Design:** DD-3, §12, §13, P-6, P-7, P-8, P-10,
  P-11
- **Files touched:** `PhantomJsRenderer.java` (page-font block built once from `pdf/fonts/DejaVuSans.ttf` and
  `DejaVuSans-Bold.ttf`; inserted after `sanitize()` and image inlining for PAGE builds only), `PhantomJsRendererTest.java`,
  `PhantomJsRenderingTest.java` (gated real render on macOS).
- **Scope:** the block holds two `@font-face` rules (`data:font/truetype`, regular and bold) and one universal
  `font-family` rule with `!important`; it is inserted before `</head>`, or at the start of the document when there
  is none. LINUX_FONTCONFIG builds never get it. `render-page.js` is not changed.
  Backend route: `stack other; implement lead existing design support none — project conventions; test-suite lead the spec's scenarios support none — project conventions`
- **Tests:** unit — a PAGE build's HTML holds the block after the sanitizer (a block-like string placed in the input
  is still handled by the sanitizer as before); a LINUX build's HTML is byte-identical to the CHG-LPRE output; the
  allowlist inputs (`allowedHosts`, `allowedDataUrls`) are identical with and without the block. Gated real render on
  macOS with the shipped build (no override): the three fixtures → `pdffonts` lists only `DejaVuSans` and
  `DejaVuSans-Bold`; page counts 5/6/7; `pdftotext` returns words on every page; the production templates (they
  declare `Montserrat`) still give DejaVu only.
- **Verification:** `mvn -q -o -pl marlo-web -am test -Dtest='PhantomJsRendererTest,PhantomJsRenderingTest'` on this
  Mac (the gated test runs because the macOS build now ships); Linux gated run unchanged (T04 verification 3).
- **Falsifier:** insert the block for LINUX builds too → the Linux byte-identity unit test fails, and the Linux gated
  run finds no fonts (`pdffonts` empty, spike S-3); drop the universal rule → the macOS gated test lists Helvetica.
- **Red run:** observed at baseline (`e452378187`), macOS PhantomJS 2.1.1 via override, no page fonts:
  `oicr pages=5 fonts=Helvetica Helvetica-Bold Helvetica-Light`; `innovation pages=6 fonts=Helvetica Helvetica-Bold
  Helvetica-Light Helvetica-Oblique`; `project pages=7 fonts=Arial-BoldMT ArialMT` (spike S-3).
- **Disqualifier:** a unit test that only checks the HTML contains `@font-face` proves presence, not effect: the
  gated `pdffonts` check is required. A macOS pass says nothing about Windows (P-18, T06).
- **Consumers:** `PhantomJsRendererTest` (HTML passed to the fake), `PhantomJsRenderingTest` (Linux font
  assertions at lines 287-296 stay Linux-only).
- **Review:** `full` — it changes what reaches PhantomJS after the sanitizer (SEC-003).
- **Skills:** `marlo-verify`, `marlo-commit`.
- **Done when:** unit and macOS gated tests green; falsifier reds observed and quoted; Linux output byte-identical.

### CHG-PPE-T06 — Cross-platform verification and size
- **Status:** [ ] · **Size:** S · **Depends on:** T01–T05 · **Module:** spec folder (evidence), no code
- **Requirements:** NF-001 ("Linux parity unchanged"), NF-002, NF-003, NF-004, NF-006, FN-001 (startup line on every
  platform run); settles P-16, P-17, P-18 · **Design:** §12, §14, §17, §18
- **Files touched:** `execution.md` (evidence only).
- **Scope:** (1) Linux, Tomcat image, `--network none`: the gated test OK and the three fixtures pixel-identical
  to the pre-change renders (re-render them from `e452378187` in a worktree, never reuse scratchpad files);
  (2) minimal Linux and old glibc from T04, re-run at the final commit; (3) macOS: the gated test and one live
  download per report on the local app, flag on, no override; (4) Windows: if a host exists, the gated test plus
  `--version` — that run settles P-16, P-17 and P-18; if none, record "Windows: not verified at runtime" and keep
  P-16…P-18 open; (5) WAR size: `ls -l marlo-web/target/*.war` against the same build at `e452378187` (≤ 60 MB
  growth); (6) first-render time on each platform run, within 50 s.
  Backend route: `stack other; test-suite lead the spec's scenarios support none — project conventions`
- **Tests:** none new.
- **Verification:** the commands above, each output quoted in `execution.md`.
- **Falsifier:** run (1) against a build where T05's block also reaches Linux → the pixel comparison fails on
  every page.
- **Red run:** `predicted — greenfield artifact (observed in T06)`: the comparison is new evidence at the final
  commit.
- **Disqualifier:** comparing against PDFs made at the final commit (instead of `e452378187`) compares the change
  with itself. A Windows result from a VM without network isolation still counts, but must say so.
- **Consumers:** none.
- **Review:** `checklist` — evidence only.
- **Skills:** `marlo-verify`.
- **Done when:** (1)–(6) recorded; Windows either run or recorded as not verified with P-16…P-18 open.

## 4. Dependency Graph

```
T01 ──> T02 ──> T03 ──> T04 ──┐
           └──> T05 ──────────┴──> T06
```

T05 depends only on T02 and may run after T03 or T04; no task is parallel-safe in one checkout (one AKILI session
per checkout, `CLAUDE.md` Concurrency).

## 5. Testing Plan

### Unit
`PhantomJsBuildTest` (new), `EmbeddedPhantomJsTest`, `PhantomJsRendererTest`, `LocalPdfReportServiceTest`,
`LocalPdfEngineDispatchTest`, the resource-integrity test — always with `-am`.

### Integration
Gated `PhantomJsRenderingTest`: Linux (Tomcat image, `--network none`) and macOS (this Mac). Container runs from the
spike harness: `debian:bookworm-slim`, `amazonlinux:2023`.

### Regression (manual)
One live download per report on the local app (macOS, flag on, no override) — T06 (3).

### Non-functional
WAR size (NF-004), first-render time (NF-006), pixel identity on Linux (NF-001).

### Accessibility
Not applicable — no UI.

### Coverage by scenario and clause

| Scenario / clause | Task |
|---|---|
| Supported platform, no configuration — THEN shipped build, 200 | T02 (selection), T06 (runs) |
| — AND startup log names platform, build, version | T02 (build), T04 (library mode, platform) |
| — BUT must NOT read any property or variable to find the binary | T02 |
| — AND IT MUST pick the build from OS and arch together | T02 (matrix) |
| Override set | T02 |
| Platform with no build — generic 500, startup line, BUT no microservice, no hang | T02 |
| No font libraries installed — ok, pixel-identical, AND IT MUST NOT change other processes' library path | T04 |
| Host libraries present — shipped libraries not used, BUT must NOT behave differently | T04 |
| Old glibc and no font libraries — FN-007 outcome | T04 |
| Windows host — extracted, verified, locked, used; sweep; BUT no POSIX-only API | T02 (name, permissions), T03 (environment, sweep), T06 (runtime) |
| Fonts on macOS and Windows — DejaVu only, page counts, AND IT MUST keep text extractable, BUT no host font | T05, T06 (Windows) |
| Linux parity unchanged | T04 (3), T06 (1) |
| Tampered file — re-extracted or refused; AND IT MUST refuse when the WAR resource does not match | T01 (binaries in the WAR), T02 (binary extraction), T04 (libraries) |
| Template or payload declares its own font — DejaVu only, allowlist unchanged, BUT not through the sanitizer | T05 |
| NF-003 no download, one platform extracted | T02 (only the chosen build's resource is opened), T04 (libraries only in BUNDLED mode) |
| NF-004 WAR growth ≤ 60 MB | T01 (sizes), T06 (5) |
| NF-005 no Maven dependency | every task (Checkstyle/compile gate; `git diff -- '**/pom.xml'` empty) |
| NF-006 first render within the deadlines | T06 (6) |
| OPS-001 provenance and licences | T01, T04 |

## 6. Operational Steps

No migration, specificity, BI/AI coordination or configuration change. Pending item for `staging` (shared file,
recorded here and applied there, never on this branch): `docs/infrastructure.md` §6 can drop the macOS PhantomJS
note once this ships.

## 7. Rollback Plan

### Code
Revert this spec's commits on the branch; CHG-LPRE behavior returns (Linux x86_64 only).

### Data
Not applicable.

### Specificity
Unchanged: `local_pdf_report_engine_active` off turns the whole engine off.

## 8. Definition of Done

- [ ] T01–T06 done with verification notes and quoted reds.
- [ ] Clean recompile; Checkstyle delta 0; hygiene clean; full suite green with `-am`.
- [ ] Linux output pixel-identical to `e452378187`; minimal-Linux and old-glibc runs recorded.
- [ ] macOS: DejaVu only, page counts 5/6/7, extractable text; one live download per report.
- [ ] Windows run, or "not verified at runtime" with P-16…P-18 left open.
- [ ] WAR growth measured and ≤ 60 MB.
- [ ] Committed on `feature/local-pdf-report-engine` through `marlo-commit`; nothing pushed without approval.
