# Portable PDF Engine — Execution Log

## Document Control

| Field | Value |
|---|---|
| Spec ID | CHG-PORTABLE-PDF-ENGINE-001 |
| Spec path | `changes/portable-pdf-engine` |
| Branch | `feature/local-pdf-report-engine` |
| Start commit | `d4608cb981` |
| Approval Mode | gated |
| Leader | Claude Code session (opus), owner Kenji Tanaka |
| Implementer / Reviewer | `.claude/agents/akili-implementer.md` (sonnet) / `.claude/agents/akili-reviewer.md` (opus, `Read`, `Grep`, `Glob` only) |
| Budget (design §18) | 6 tasks · about 650 Java LOC · 9 review rounds · WAR growth about 36 MB (cap 60 MB) |

### Known persona drift (recorded before the first spawn, 2026-11-24)

`akili doctor --agents` (akili-specs v2.45.0) reports all four `.agents/*.md` personas `UNMARKED`: they are
MARLO's adapted versions, with no template markers. Checked against what this command relies on:
`grep -c` in `.agents/implementer.md` → `STATUS: CHECKPOINT` 0, `60 tool calls` 0, `Not Done / Assumptions` 3. The
Implementer therefore has no checkpoint bound; a long attempt reports only at completion.

- **Decision (owner, 2026-11-24, "opción 1, sigue aquí"):** proceed without `--fix`; the rule is not restated in the
  briefs (the command forbids papering over drift there). Risk judged low: tasks are S/M.
- **Pending item for `staging` (shared file, never edited on this branch):** merge the v2.45.0 persona sections
  (checkpoint protocol, bounded reads) into MARLO's adapted `.agents/*.md` through the constitutional change process.

## Task Execution History

### CHG-PPE-T01 — Ship the macOS and Windows builds

| Field | Value |
|---|---|
| Final status | **PASS** (attempt 1) |
| Date | 2026-11-24 |
| Requirements covered | FN-001 (the builds exist), SEC-001 (checksums), OPS-001, NF-004 (sizes) |
| Skills / effort | `marlo-verify` (task's `marlo-commit` dropped from the Implementer brief: the Leader commits, Step 3) · effort `low` (mechanical) |
| spawns | Implementer 8 calls, 110,050 tokens, ended complete; Reviewer 13 calls, 59,226 tokens, ended complete |

**Attempt 1**
- Files changed (all new): `marlo-web/src/main/resources/pdf/phantomjs/phantomjs-2.1.1-macosx.gz` (17,060,127 B),
  `phantomjs-2.1.1-macosx.sha256`, `phantomjs-2.1.1-windows.exe.gz` (18,140,268 B),
  `phantomjs-2.1.1-windows.exe.sha256`, `PROVENANCE-macosx.txt`, `PROVENANCE-windows.txt`;
  `marlo-web/src/test/java/org/cgiar/ccafs/marlo/utils/pdf/ShippedPhantomJsBuildsTest.java` (83 lines).
- Implementer verification: zips re-checked (macOS `538cf488…f8c1`, Windows `d9fb0562…dcc8`);
  `mvn -q -o -pl marlo-web -am test -Dtest=ShippedPhantomJsBuildsTest` → `Tests run: 3, Failures: 0, Errors: 0`;
  `LICENSE.BSD` and `third-party.txt` byte-identical (`cmp`) across both zips and the repo copies.
- **Red run (falsifier executed against the post-change code):** first digit of
  `phantomjs-2.1.1-windows.exe.sha256` changed `1`→`0` →
  `Tests run: 3, Failures: 1, Errors: 0, Skipped: 0` /
  `decompressedBinaryMatchesItsChecksumFile[phantomjs-2.1.1-windows.exe](...ShippedPhantomJsBuildsTest)  <<< FAILURE!` /
  `org.junit.ComparisonFailure: decompressed phantomjs-2.1.1-windows.exe expected:<[0]cf9b2aa732bd0c65a83c...> but was:<[1]cf9b2aa732bd0c65a83c...>`;
  Linux and macOS rows passed; file restored → 3/3.
- **Evidence re-run (Leader-inline, non-author): VERIFIED.** `gunzip -c <gz> | shasum -a 256` equals the `.sha256`
  for both new builds (`2b8c719e…a053`, `1cf9b2aa…fcffb`); `clean-compile.sh` → BUILD SUCCESS, `Compiling 2430` /
  `1072` / `153` files; `mvn -o -pl marlo-web -am test -Dtest=ShippedPhantomJsBuildsTest` exit 0, 3/3;
  `checkstyle.sh --baseline ShippedPhantomJsBuildsTest.java` → `HEAD=0 working tree=0 delta=0` (run after the
  Reviewer flagged that no Checkstyle output was in the evidence); `java-hygiene-check.sh` → ok.
- Reviewer verdict: **PASS** — every listed file present and nothing else touched; provenance has every OPS-001
  field; the test hashes the decompressed stream (disqualifier does not apply); one row per build, so the falsifier
  fails one row only.
- **Not Done / Assumptions (Implementer, verbatim):** "The provenance files point to the existing `LGPL-2.1.txt`
  and `SOURCE.txt`. I assumed they cover all three builds, since the zips ship the same `third-party.txt`. I did not
  review their content." / "The tests do not execute the macOS or Windows binaries; they only check their hashes."
  — assumptions only, no owed item, no blocker: not a continuation.

**ADVISORY (4R, recorded only — never a task):**
- Risk (licence accuracy): `SOURCE.txt` is worded for the Linux build only, although its source pointers (tag
  2.1.1, pinned qtbase/qtwebkit) apply to all three builds. Not in T01's file list.
- Risk (unverified claim): `PROVENANCE-windows.txt` says "It runs on 64-bit Windows"; Windows has not been run yet
  (S-4, OQ-2, P-16…P-18).
- Reliability: the test parses `.sha256` with its own split, while `EmbeddedPhantomJs.readExpectedSha256` also
  lower-cases; harmless with today's files.

**Budget running totals (design §18):** tasks 1/6 · Java LOC 83/~650 · review rounds 1/9 · WAR growth so far
35.2 MB (binaries) of ~36 MB, cap 60 MB.
- Decision (owner, 2026-11-24, after the PASS): `PROVENANCE-windows.txt` reworded from "It runs on 64-bit Windows" to
  "expected to run on 64-bit Windows (WoW64); not yet run (design.md 17, OQ-2)" — a T01 file, wording only, so the
  advisory's unverified claim is not shipped. `SOURCE.txt` left as is (not a T01 file).

### CHG-PPE-T02 — One platform table chooses the shipped build

| Field | Value |
|---|---|
| Final status | **PASS** (attempt 2) |
| Date | 2026-11-24 |
| Requirements covered | FN-001, FN-004 (file name, permissions), FN-006, FN-007 · DD-1, DD-5 |
| Skills / effort | `tdd`, `marlo-verify` (task's `marlo-commit` dropped: the Leader commits) · effort `medium`, bumped to `high` for attempt 2 |
| spawns | Implementer 35 calls, 179,567 tokens, ended complete (attempt 1); Implementer resumed by message for attempt 2, 5 calls, 185,483 tokens (cumulative context), ended complete; Reviewer 19 calls, 93,968 tokens, ended complete |

**Attempt 1 — implicit FAIL (evidence re-run MISMATCH)**
- Files changed: new `utils/pdf/PhantomJsBuild.java`, `PhantomJsBuildTest.java`; modified `EmbeddedPhantomJs.java`,
  `PhantomJsRenderer.java`, `LocalPdfReportService.java`, `EmbeddedPhantomJsTest.java`,
  `LocalPdfReportServiceTest.java`, `PhantomJsRendererTest.java`, `marlo-utils/.../APConfig.java`.
- Implementer verification: the 4 named classes 77/0; pdf package 155 run, 0 failures, 7 skipped; clean recompile
  2430/1073/154; Checkstyle delta 0; "Hygiene check: no FIX lines".
- Falsifiers executed: (1) `Mac OS X`/`aarch64` → none: `macOsOnIntelOrAppleSiliconGetsTheMacBuild fails with
  java.lang.AssertionError: Mac OS X/aarch64 has a build` (Tests run: 7, Failures: 1, Errors: 1); (2) `Linux`/`aarch64`
  → x86_64 build: `linuxOnArmGetsNoBuild fails with java.lang.AssertionError: Linux/aarch64 has no build`
  (Tests run: 7, Failures: 1). Restored → 7/7.
- **Evidence re-run (Leader-inline): MISMATCH.** `java-hygiene-check.sh` exit 1:
  `=== marlo-utils/src/main/java/org/cgiar/ccafs/marlo/utils/APConfig.java` /
  `FIX    EOL        line endings changed CRLF -> LF; restore CRLF (real edits: 2 lines)`; `git diff --stat`
  2087 lines on `APConfig.java` vs 3 with `--ignore-cr-at-eol`; CRLF lines HEAD 1043 → 0. The other checks matched:
  clean recompile 2430/1073/154, named classes 77/0 three times in a row with 0 forked-VM crashes, full suite
  `mvn -o -pl marlo-utils,marlo-data,marlo-web -am test` → `Tests run: 1471, Failures: 0, Errors: 0, Skipped: 7`.
- Reviewer: not run (MISMATCH is an implicit FAIL).

**Attempt 2 — PASS**
- Delivery: rework sent by `SendMessage` to the attempt-1 Implementer (its context survived; effort `high`), with the
  MISMATCH evidence verbatim and the attempt history ("attempt 1 rewrote APConfig.java with LF — do not repeat").
- Files changed: `APConfig.java` restored from HEAD, Javadoc line re-applied with a CRLF-preserving edit.
- **Evidence re-run (Leader-inline): VERIFIED.** `APConfig.java` diff 3 lines (2 insertions, 1 deletion), same with
  `--ignore-cr-at-eol`; `java-hygiene-check.sh` exit 0, no FIX (two REVIEW `LOG-NULL` at
  `LocalPdfReportService` L408); `clean-compile.sh marlo-utils,marlo-data,marlo-web` BUILD SUCCESS 2430/1073/154;
  `checkstyle.sh --baseline` over the 9 Java files `HEAD=9 working tree=9 delta=0`; the 4 named classes 77/0.
- Reviewer verdict: **PASS** — the table returns a build for exactly the DD-1 rows, OS and arch checked together;
  Linux keeps the CHG-LPRE resource paths; POSIX permissions only when the store supports `posix`; override priority
  unchanged; FN-007 WARN names the platform and the render answers the generic 500; the matrix asserts every field;
  the L408 `LOG-NULL` claim verified in the source (both values non-null by construction).
- **Not Done / Assumptions (Implementer, verbatim):** "PhantomJsBuild red run: I wrote the class before its test, so
  there is no clean red for it. The baseline reds you listed for EmbeddedPhantomJs were not re-run. The two falsifiers
  show the matrix test discriminates." / "Flaky surefire VM crash: … went away once I ran Maven one command at a time."
  / "Windows arch matching: Windows x86 (32-bit) is accepted only on Windows." / "FontMode and EnvironmentStyle are
  carried by the table but nothing reads them yet (T03 to T05)." — assumptions only, no owed item, no blocker.
- Issues encountered: the forked-VM crash and a `struts-report.xml` copy failure in attempt 1 coincided with
  overlapping Maven runs inside the Implementer; three sequential Leader runs showed no crash.

**ADVISORY (4R, recorded only — never a task):**
1. Reliability: no test asserts `build=` in the startup line (`EmbeddedPhantomJsTest.java:747`, `:794`).
2. Spec gap → presented as an amendment at the gate: OPS-002 and the "Supported platform" scenario require the
   startup line to name the platform; no task owns that clause.
3. Spec wording → presented as an amendment at the gate: FN-007 says "any 32-bit OS" has no build, DD-1 maps
   Windows `x86` to the build.
4. Readability (KZ-…-auth-flow-2): the Windows-seam test counts `posixStore` calls but does not assert the path it
   received.
5. Resilience: that test calls `Files.getPosixFilePermissions` itself (diff line 370), which throws on a real
   Windows host — relevant to T06's Windows run.
6. Coverage location: `LocalPdfEngineDispatchTest` was not extended; covered by the service-level test plus the
   existing flag-on dispatch tests.

**Budget running totals (design §18):** tasks 2/6 · Java LOC 83 + 182 changed (+256 new) ≈ 520/~650 (main and tests;
APConfig counted as 3) · review rounds 3/9 (T01 1, T02 2: one MISMATCH, one PASS) · WAR growth 35.2 MB / cap 60 MB.
**Note:** LOC is already at 80 % of the budget after 2 of 6 tasks.
- Decision (owner, 2026-11-24, at the T02 gate): T02 committed as `4cae4e4f6d`; the two spec-tension amendments
  approved and applied — FN-007 "32-bit Linux or macOS" (`requirements.md` §3 and §6), OPS-002 platform clause
  assigned to T04 (`tasks.md` T04 requirements line and coverage table; `design.md` §11 example). Recorded in the
  `requirements.md` Decision Log with its closure sweep. Carried as named conformance checks into the T03 and T04
  Reviewer briefs.

### CHG-PPE-T03 — Child-process environment per platform, Windows sweep

| Field | Value |
|---|---|
| Final status | **PASS** (attempt 2) |
| Date | 2026-11-24 |
| Requirements covered | FN-003, FN-004 (Windows environment, sweep), SEC-002 · DD-4, DD-6 |
| Skills / effort | `marlo-verify` (task's `marlo-commit` dropped: the Leader commits) · effort `medium`, `high` for attempt 2 |
| spawns | Implementer 21 calls, 127,700 tokens, ended complete (attempt 1); same Implementer resumed by message, 4 calls, 134,375 tokens cumulative, ended complete (attempt 2); Reviewer 17 calls, 71,612 tokens, ended complete (attempt 1); Reviewer 5 calls, 53,192 tokens, ended complete (attempt 2) |

**Decision (Leader, recorded before the spawn):** the environment style and the `FONTCONFIG_FILE` rule follow the host
platform (`PhantomJsBuild.forPlatform(os.name, os.arch)`), whether or not `pdf.phantomjs.path` is set; a host with no
build keeps today's POSIX environment with `FONTCONFIG_FILE`. Reason: an override on Windows with the POSIX
environment (`PATH=/usr/bin:/bin`, `OPENSSL_CONF=/dev/null`) could not work; DD-4 is silent on overrides.

**Attempt 1 — Reviewer PASS, reopened by the owner**
- Files changed: `utils/pdf/PhantomJsRenderer.java`, `PhantomJsRendererTest.java`.
- Implementer verification: `PhantomJsRendererTest` 43/0; pdf package 162 run, 0 failures, 7 skipped; clean recompile
  2430/1073/154; Checkstyle delta 0; hygiene ok. SystemRoot fallback `C:\Windows` for null/empty/blank.
- Falsifier executed (WINDOWS given the POSIX style) → 3 of 43 failed: `expected:<[NUL]> but was:<[/dev/null]>`;
  `theWindowsEnvironmentHasTheWindowsKeysAndValuesAndNothingPosix: expected:<{SystemRoot=D:\WinRoot,
  PATH=D:\WinRoot\System32, OPENSSL_CONF=NUL, ...}> but was:<{PATH=/usr/bin:/bin, OPENSSL_CONF=/dev/null,
  HOME=render-home, TMPDIR=render-home, LANG=C.UTF-8}>`; `windowsFallsBackToCWindowsWhenTheJvmHasNoSystemRoot:
  expected:<C:\Windows> but was:<null>`. Restored.
- Evidence re-run (Leader-inline): **VERIFIED** — `--stat` equals `--ignore-cr-at-eol`; hygiene exit 0, no FIX or
  REVIEW; clean recompile 2430/1073/154; Checkstyle `HEAD=0 working tree=0 delta=0`; pdf package 162/0/7.
- Reviewer: **PASS**, with a reliability/risk advisory: the owner prefix was stripped on every OS and on the owner
  side only, while DD-6 says "on Windows" (a domain-joined Linux `DOMAIN\user` would stop matching; `OTHERDOMAIN\kenji`
  would match `kenji`).
- **Owner decision (2026-11-24, "opción b, ajústalo"):** correct it within T03 to follow DD-6 literally — counted as
  attempt 2 (rework; Reviewer required, override e).

**Attempt 2 — PASS**
- Delivery: rework sent by `SendMessage` to the attempt-1 Implementer (effort `high`), with the Reviewer finding
  verbatim and the attempt history ("attempt 1 stripped only the owner side, on every OS").
- Files changed: the same two. New `isSameOwner(owner, userName, style)`: WINDOWS →
  `ownerName(owner).equals(ownerName(userName))`; anything else → `userName.equals(owner)` (HEAD behavior). A
  4-argument `sweepStaleSupportDirectories` overload takes the style; the 3-argument signature is unchanged.
  `theArgumentsAreTheSameOnEveryPlatformStyle` renamed `theArgumentsAreTheSameOnLinuxAndMacos`.
- Falsifier executed (condition changed to `if (style != null)`, so POSIX strips too) → 45 run, 1 failure:
  `onPosixTheOwnerIsComparedExactlyAsItIsReported(...PhantomJsRendererTest) <<< FAILURE! java.lang.AssertionError`
  (the `assertFalse(isSameOwner("DOMAIN\\kenji", "kenji", POSIX))` assertion). Restored.
- Evidence re-run (Leader-inline): **VERIFIED** — `--stat` equals `--ignore-cr-at-eol` (2 files, +236 −6); hygiene
  exit 0, no FIX or REVIEW; `clean-compile.sh` BUILD SUCCESS; Checkstyle `HEAD=0 working tree=0 delta=0`; pdf
  package `Tests run: 164, Failures: 0, Errors: 0, Skipped: 7`.
- Reviewer: **PASS** — POSIX comparison identical to HEAD (same arguments, same order); the sweep and the child
  environment pick the same style on the same host; every owner-requested case tested; attempt-1 parts undisturbed.
- **Not Done / Assumptions (Implementer, verbatim):** "No test drives `sweepIfStale` with a real directory owner of
  the form `DOMAIN\user`, because this file system cannot report one. `isSameOwner` is tested directly instead." /
  "The Windows argument sameness stays a recorded gap until T06" / "P-16 and P-17 stay UNVERIFIED until the Windows
  run." — assumptions and recorded gaps, no owed item, no blocker.

**Recorded gap (Reviewer, attempt 1):** the "arguments, allowlist and `--local-url-access=false` identical for all
three styles" test runs Linux and macOS only; the fake PhantomJS is a `/bin/sh` script and cannot run with the Windows
`PATH`. The property holds in the code (nothing that builds the arguments depends on the style); Windows sameness
rests on reading the code until T06.

**ADVISORY (4R, recorded only — never a task):**
- Risk: on Windows `OTHERDOMAIN\kenji` matches `user.name=kenji` (DD-6 as written); matters only with a temp
  directory shared between accounts — note for the T06 Windows run.
- Reliability: the WINDOWS branch uses `equals`; a case difference makes the sweep skip (safe direction) — check with
  P-17 in T06.
- Readability: no test calls the 4-argument `sweepStaleSupportDirectories` overload.
- Risk (attempt 1): `SystemRoot` goes into `PATH` unchecked; its source is the JVM's own environment.

**Budget running totals (design §18) — TRIPWIRE:** tasks 3/6 · **Java LOC 757 added (T01 83 + T02 438 + T03 236),
against a budget of about 650** — main code 303 of the estimated ~250, tests 454 of the estimated ~400 · review rounds
5/9 (T01 1, T02 2, T03 2) · WAR growth 35.2 MB / cap 60 MB. **Budget exceeded at T03: run stopped and escalated to the
owner before T04.** Cause: T02 and T03 needed more seams and more matrix rows than estimated, and T03's DD-6
correction added a second round.
- **Decision (owner, 2026-11-24, at the T03 gate, budget tripwire):** "sí, haz el commit y sigue con la opción 1" —
  T03 committed as `6ce75d9411` (with the post-T02 spec amendments); scope kept in full; budget re-baselined in
  `design.md` §18 to about 1,150 Java LOC and 11 review rounds. The tripwire now compares against the new figures.

### CHG-PPE-T04 — Linux library fallback — PARKED `[~]` (owner stop, 2026-11-24)

| Field | Value |
|---|---|
| Status | **Parked `[~]`** — owner: "paremos aquí por hoy". Implementation done and evidence VERIFIED; **no Reviewer verdict yet** |
| Requirements | FN-002, FN-003, FN-007 (old glibc), SEC-001 (libraries), OPS-002 (library mode, platform) · DD-2 |
| Skills / effort | `tdd`, `error-handling-patterns`, `marlo-verify` · effort `high` |
| spawns | Implementer 45 calls, 261,554 tokens, ended complete; Reviewer A (conformance + reliability/resilience) and Reviewer B (security/risk), parallel lens mode, both stopped by the Leader at the owner's stop before reporting (no verdict; not a FAIL, not a runtime event) |

**Attempt 1 (uncommitted, in the working tree)**
- Files changed: `utils/pdf/EmbeddedPhantomJs.java`, `PhantomJsRenderer.java`, `LocalPdfReportService.java`;
  `src/test/resources/pdf/fake-phantomjs.sh` (modes `libs-required`, `loader-fail`); new
  `src/test/java/.../utils/pdf/PhantomJsLibrariesTest.java` (14 tests); new
  `src/main/resources/pdf/phantomjs/linux-libs/` (7 `*.so*.gz`, `SHA256SUMS`, `PROVENANCE.txt`, 6 `copyright.<package>`;
  924,837 bytes). Java diff vs `6ce75d9411`: 6 files +414 −14 plus the new test.
- Shipped packages (one `debian:bookworm-slim` install, Debian 12.15): `libfontconfig1 2.14.1-4`,
  `libfreetype6 2.12.1+dfsg-5+deb12u4`, `libexpat1 2.5.0-1+deb12u5`, `libpng16-16 1.6.39-2+deb12u6`,
  `libbrotli1 1.0.9-2+deb12u1`, `zlib1g 1:1.2.13.dfsg-1`; highest symbol `GLIBC_2.36`; no `libc`/`libm`.
- Falsifiers executed: A (probe always BUNDLED) → `withHostLibrariesNothingIsExtractedAndNoLibraryPathIsGiven:
  expected:<[host]> but was:<[bundled]>` (+1 error, startup-line test failed); B (never fall back) →
  `withoutHostLibrariesTheShippedOnesAreExtractedVerifiedAndGivenToPhantomJsOnly: expected:<[bundled]> but
  was:<[host]>` (5 failures, 2 errors). Restored.
- **Evidence re-run (Leader-inline): VERIFIED.** `--stat` equals `--ignore-cr-at-eol`; hygiene exit 0, 0 FIX;
  clean compile 2430/1073/155; Checkstyle `HEAD=0 working tree=0 delta=0`; pdf package `Tests run: 178, Failures: 0,
  Errors: 0, Skipped: 7`. Containers, `--network none`, spike harness:
  - `debian:bookworm-slim`: `fontlibs=0`; INFO `PhantomJS does not start with the host libraries; using the libraries
    shipped with MARLO`; oicr/innovation/project `outcome=ok` (557,407 / 1,102,324 / 74,472 bytes; 5/6/7 pages);
    per-page `pdftoppm -r 72 | shasum` vs the Tomcat-image renders → 0 differing pages for all three.
  - `tomcat:9.0-jdk17-temurin`: `fontlibs=4`; no fallback line; JUnitCore `PhantomJsRenderingTest` → `OK (7 tests)`;
    startup `PhantomJS available for local PDF renders, version=2.1.1, platform=Linux/amd64, build=linux-x86_64,
    libraries=host, path=…`.
  - `amazonlinux:2023` (glibc 2.34): `fontlibs=0`; WARN `PhantomJS does not start with the host libraries nor the
    shipped ones (glibc older than 2.36?): local PDF renders will fail`; render `outcome=failed`, no hang.
- **Not Done / Assumptions (Implementer, summary of the verbatim report kept in this session):** the WARN/INFO are
  logged by `PhantomJsRenderer` so "once" holds; only stderr containing "error while loading shared libraries" counts
  as a missing library, any other failure (timeout included) keeps HOST; one extra `--version` at startup also in HOST
  mode; a distinct WARN when library extraction or checksum fails (sets UNAVAILABLE); `shipsLibraries()` keys on
  `FontMode.LINUX_FONTCONFIG`; `libraries=n/a` on macOS, Windows and with an override; `verifyLibraries` rejects a
  link or any extra file in `libs/`.

**To resume (`/akili-execute changes/portable-pdf-engine` picks up this `[~]` task):** do not re-implement. Keep the
working tree, re-run the evidence (it is cheap), then spawn the two parallel lens Reviewers on the diff (A:
conformance + reliability/resilience; B: security/risk — library verification before load, TOCTOU/planted `.so`,
`LD_LIBRARY_PATH` confined to the child, stderr-driven mode flip, SEC-002 leaks, HOST mode identical to CHG-LPRE).
The re-baselined budget (design §18: about 1,150 LOC, 11 review rounds) applies; T04 adds about 414 + test lines.

**Resumed 2026-11-25 (`/akili-execute`, Leader opus).** Working tree unchanged since the park (file mtimes
2026-11-24 16:19–16:21). Evidence re-run (Leader-inline, no other agent or build active): **VERIFIED** —
hygiene exit 0, 0 FIX (6 REVIEW `LOG-NULL`: `LocalPdfReportService.java:409` reads `current`, already dereferenced
above; `PhantomJsRenderer.java:1132, 1152` `e.getClass()` on a caught exception); clean compile 2430/1073/155
BUILD SUCCESS; Checkstyle `HEAD=0 working tree=0 delta=0` on the 4 Java files; `mvn -q -o -pl marlo-web -am test
-Dtest='org.cgiar.ccafs.marlo.utils.pdf.*Test,LocalPdfEngineDispatchTest'` → exit 0, `Tests run: 192, Failures: 0,
Errors: 0, Skipped: 7` (`PhantomJsLibrariesTest` 14/14). Container runs not repeated (code unchanged; T06 re-runs
them at the final commit).

**Budget tripwire (design §18, re-baselined 2026-11-24) — run stopped before the Reviewers, escalated to the owner.**
T04's Java diff is +386/−14 main and +569 test (`PhantomJsLibrariesTest.java`, new) → about 955 added lines.
Running total about 1,712 Java LOC against the re-baselined about 1,150 (T04 estimate was 250–300). Review rounds
5/11 so far. Cause: the test file alone is 569 lines (14 tests, each building a fake support directory and fake
library set); main code 386 lines against the estimated ~250–300 (library verification rejects links and extra
files, distinct WARN paths for extraction and checksum failures).
- **Decision (owner, 2026-11-25, at the T04 tripwire):** "sí, sube el presupuesto y lanza los reviewers" — budget
  re-baselined in `design.md` §18 to about 1,900 Java LOC (review rounds 11 unchanged); T04 kept as implemented;
  the two parallel lens Reviewers launched on the diff. Execute-time spec edit: `design.md` §18 (budget row only, no
  requirement meaning changed).

**Attempt 1 — Reviewer verdicts (parallel lens mode, effort `high` + security surface, 2026-11-25)**
- **Reviewer B (security + risk, opus): PASS.** Libraries hashed in memory before any write (`CREATE_NEW`, cleanup on
  failure; `SHA256SUMS` names admit no `/`; 32 MB per-library cap); `libs/` 0700 / files 0600, exact 7-name listing,
  links rejected, re-hash on fingerprint change before every render; `LD_LIBRARY_PATH` only in the cleared
  `ProcessBuilder` map, never in HOST mode or with an override, `java.library.path`/`System.getenv` asserted unchanged;
  stderr judged only for `--version` while UNDECIDED, never on render; SEC-002 surfaces untouched; provenance and six
  copyright files present, no libc/libm.
  ADVISORY (recorded only): RISK — a `java.io.tmpdir` containing `:`, `;` or `$` (or relative) would make the loader
  search outside the verified `libs/`; suggested refusal of BUNDLED on such paths. RISK — between renders a file is
  accepted on size/mtime/file key without re-hash (design §13, accepted). RESILIENCE — a transient failure pins the
  mode for the JVM's life (fails safe). SPEC TENSION — OPS-001 provenance fields; FN-007 platform in the old-glibc WARN
  (both raised as FAIL issues by Reviewer A, below).
- **Reviewer A (conformance + reliability/resilience, opus): FAIL** — verbatim:

> STATUS: FAIL
>
> SUMMARY: Reviewer A (parallel lens: spec conformance gate + reliability/resilience). The library logic matches DD-2,
> tasks.md T04, FN-002, FN-003 and SEC-001: mode decided once inside the `synchronized` `supportDirectory()`; HOST mode
> has no `libs/` and no `LD_LIBRARY_PATH`, and the T03 environment tests still pass in that mode; a missing, altered or
> extra library forces a rebuild, which does not probe again; every failure path is bounded by `VERSION_TIMEOUT_MS`;
> the tests check what the fake received (`env.*`, `libpath.version`), not just the outcome; §18 changes only the
> budget row (conforms). Two spec clauses are not met; both are small fixes.
>
> 1. **Discovered Issue:** The startup line for the old-glibc case does not name the platform. When neither set of
>    libraries works, the only startup line is the renderer's WARN `PhantomJS does not start with the host libraries
>    nor the shipped ones (glibc older than 2.36?): local PDF renders will fail`; the extraction/checksum WARN (`The
>    libraries shipped with MARLO could not be extracted …`) has the same gap. `LocalPdfReportService.checkBinary`
>    deliberately returns early on `LIBRARIES_UNAVAILABLE_MESSAGE`, so nothing else names the platform. The
>    Implementer's own `amazonlinux:2023` evidence shows the line without it.
>    **Violated Rule:** `requirements.md` CHG-PPE-FN-007 (old-glibc case added by the 2026-11-24 amendment): "Startup
>    MUST log one line that names the platform." T04 lists "FN-007 (old-glibc case)". Tension: `design.md` §11 quotes
>    the WARN text without the platform and the Implementer copied it exactly; the requirement takes precedence.
>    **Remediation Suggestion:** add the platform to both UNAVAILABLE WARNs, e.g. `"{} (glibc older than 2.36?),
>    platform={}: local PDF renders will fail"` with `embedded.platform()`; assert `platform=Linux/amd64` in
>    `whenNeitherWorksOneWarn…` and `aDamagedLibraryResource…`; amend the §11 WARN text as an execute-time spec edit
>    with a Decision Log entry — or the Leader treats it as spec tension and gets the owner to amend FN-007.
> 2. **Discovered Issue:** `linux-libs/PROVENANCE.txt` lacks two provenance fields: no upstream URL per artifact (only
>    the host `deb.debian.org` and `bookworm-security`), and no upstream SHA-256 (the downloaded `.deb` packages are not
>    hashed; only the decompressed libraries are). The T01 provenance files record the upstream archive hash separately
>    from the hash the application checks; this one does not, so the libraries cannot be tied to Debian's signed index.
>    **Violated Rule:** `requirements.md` CHG-PPE-OPS-001: "Each shipped binary and library MUST have a provenance file
>    (upstream URL, upstream SHA-256, stored SHA-256, retrieval date)"; `tasks.md` coverage table ("OPS-001 provenance
>    and licences | T01, T04").
>    **Remediation Suggestion:** for each of the six packages record the pool URL of the `.deb` and its SHA-256 — in
>    the same `debian:bookworm-slim` container, `apt-cache show <pkg>=<version>` gives `Filename` and `SHA256`;
>    optionally the SHA-256 of each stored `.gz`.
>
> ADVISORY: RESILIENCE — a timeout/IOException in the host probe pins HOST until restart (renders then exit 127 on a
> host that needs the bundle); a retry timeout pins UNAVAILABLE with a possibly wrong glibc hint; matches DD-2 and the
> judgment call; consider re-probing when the mode was set because the probe "could not be judged". RELIABILITY — no
> unit test covers the "could not be judged" branches (probe timeout / start failure, retry throwing). RESILIENCE —
> tension: DD-2 matches "cannot open shared object file", tasks.md and the code match the broader "error while loading
> shared libraries" (probably the better behaviour; align DD-2). RELIABILITY — `shipsLibraries()` keys on
> `FontMode.LINUX_FONTCONFIG`; a future Linux build of another architecture would inherit the x86_64 libraries; a
> per-build flag is safer. RELIABILITY — §12 says "on Linux with host libraries, nothing changes", but HOST mode now
> runs `--version` twice at startup. READABILITY — test constant `HOST_MESSAGE` holds the BUNDLED INFO text; rename.

**Leader adjudication (attempt 1):** both FAIL issues confirmed against the source (`requirements.md:75-76` FN-007,
`:112-113` OPS-001; `design.md:107-108` §11; `PROVENANCE.txt` names only the archive host). Both are in T04 scope.
Attempt 1 consumed (Reviewer `FAIL`). Spec tensions to put to the owner at this gate before attempt 2: FN-007 vs
design §11 WARN text; DD-2 "cannot open shared object file" vs tasks.md "error while loading shared libraries".
- spawns (attempt 1, this session): Reviewer A 22 calls, 116,986 tokens, ended fail; Reviewer B 14 calls, 99,267
  tokens, ended complete. Review rounds 7/11 (two verdicts this attempt).
- **Decision (owner, 2026-11-25, at the T04 attempt-1 gate):** "sí, aplica ambas enmiendas y lanza el intento 2".
  Execute-time spec edits (no requirement meaning changed), recorded in `requirements.md` §9 Decision Log with the
  closure sweep: `design.md` §11 (old-glibc WARN names `platform=…`; every UNAVAILABLE WARN names the platform, per
  FN-007) and `design.md` DD-2 (trigger "error while loading shared libraries", aligned with `tasks.md` T04 and the
  code). Both carried as named conformance checks in the attempt-2 Reviewer briefs and once more in T05's.

**Attempt 2 (rework, effort `xhigh`, Implementer sonnet, 2026-11-25)**
- Files changed: `PhantomJsRenderer.java` (`prepareLibraries`: both UNAVAILABLE WARNs gain `platform={}` with
  `embedded.platform()` — `"{}, platform={}: local PDF renders will fail"` and `"{} (glibc older than 2.36?),
  platform={}: local PDF renders will fail"`); `PhantomJsLibrariesTest.java` (`whenNeitherWorksOneWarn…` and
  `aDamagedLibraryResourceRefusesTheRenderWithOneWarnAndNoRun` assert `platform=Linux/amd64`);
  `linux-libs/PROVENANCE.txt` (additions only: per-package pool URL, `.deb` size and index SHA-256, how confirmed,
  retrieval 2026-11-24 / confirmed 2026-11-25, SHA-256 of each stored `.gz`). `SHA256SUMS` and libraries untouched.
- Upstream values (all six versions still in the Debian index; URLs fetched 200; downloaded `.deb` hashes equal the
  index; `dpkg-deb -x` libraries equal every `SHA256SUMS` line): libfontconfig1 `16ee38d3…0649` (main),
  libfreetype6 `8043e479…ac44` (main), libexpat1 `cae5ded6…5d79` (security), libpng16-16 `940b91d4…640c` (security),
  libbrotli1 `e27b3d65…6d8b` (security), zlib1g `d7dd1d14…8ce4` (main).
- Implementer verification: clean compile 2430/1073/155; Checkstyle `HEAD=0 working tree=0 delta=0`; hygiene exit 0,
  0 FIX (new REVIEW `LOG-NULL` L1145/L1161 `embedded.platform()` traced: `embedded` already dereferenced, `platform()`
  null-safe); tests `Tests run: 192, Failures: 0, Errors: 0, Skipped: 7`.
- Falsifier (issue 1) red: platform dropped from the glibc WARN → `Tests run: 14, Failures: 1 …
  whenNeitherWorksOneWarnWithTheGlibcHintAndEveryRenderFailsWithoutRunningPhantomJs … AssertionError: the only startup
  line names the platform (FN-007): PhantomJS does not start with the host libraries nor the shipped ones (glibc older
  than 2.36?): local PDF renders will fail` (`PhantomJsLibrariesTest.java:328`); restored (SHA-256 equals backup). The
  extraction-WARN falsifier was not run (assertion checked green only).
- Not Done / Assumptions (verbatim summary of the Implementer's field — assumptions only, no owed item, no blocker →
  no continuation): the noexec WARN (`PhantomJsRenderer` L1080, "cannot be executed from the temp directory …") sets
  `embeddedBlocked`, not `LibraryMode.UNAVAILABLE`, so §11 does not cover it and it is unchanged; retrieval dates as
  above; libfreetype6 is indexed in both `bookworm` and `bookworm-security` at the same version — the `debian/pool/main`
  URL (the one apt used, hash verified) is recorded; spec docs untouched.
- **Evidence re-run (Leader-inline): VERIFIED** — hygiene exit 0, 0 FIX; clean compile 2430/1073/155 BUILD SUCCESS;
  Checkstyle delta 0; `Tests run: 192, Failures: 0, Errors: 0, Skipped: 7`; `SHA256SUMS` hash `8d0865cf…da92`
  unchanged and every `gunzip -c <lib>.gz | shasum` equals its `SHA256SUMS` line.
- spawns (attempt 2): Implementer 37 calls, 148,237 tokens, ended partial (Not Done / Assumptions, assumptions only).

**Attempt 2 — Reviewer verdicts (parallel lens mode, effort `xhigh`; override (e) rework, 2026-11-25)**
- **Reviewer A (conformance + reliability/resilience, opus): PASS.** Both attempt-1 issues fixed: both UNAVAILABLE
  WARNs name the platform, the old-glibc text matches amended §11 word for word, each asserted on what the logger
  received; `startupOnAHostThatCannotRunItAddsNoSecondLineToTheRenderersOneWarn` keeps FN-007's "one line";
  `PROVENANCE.txt` covers every OPS-001 clause (upstream URL, upstream SHA-256, stored SHA-256, retrieval date,
  licences). Amended DD-2 equals `LOADER_FAILURE_TEXT`; Decision Log sweep claims hold. Implementer assumptions judged
  consistent: the noexec WARN is neither FN-007 case and never sets UNAVAILABLE; libfreetype6 at `debian/pool/main`
  satisfies OPS-001. Issues: none.
- **Reviewer B (security + risk, opus): PASS.** Delta limited to the two WARN lines, two assertions and
  `PROVENANCE.txt`. `e.getMessage()` comes only from the fixed-text `PdfRenderException` of `extractLibraries`
  (no path, contents or stderr); `embedded.platform()` is null-safe and already logged at HEAD
  (`LocalPdfReportService.java:398`); the thrown exception still carries only `LIBRARIES_UNAVAILABLE_MESSAGE`. Pool
  paths match source packages and versions; the uncompressed-library list equals `SHA256SUMS`; the `.deb` hashes are
  documentation, not a runtime claim. Verify-before-write, `libs/` checks, `LD_LIBRARY_PATH` confinement and probe
  gating unchanged. Limit: the Debian index was not fetched by the Reviewer (offline); the values rest on the
  Implementer's container evidence. Issues: none.

ADVISORY (4R, recorded only — never a task):
- RELIABILITY (A, attempt 2): a re-extraction failure after startup in BUNDLED mode keeps BUNDLED; every render then
  fails closed with the generic exception and no platform WARN — consider one WARN the first time it happens.
- RISK (B, attempt 2): `PROVENANCE.txt:60-61` says libraries are checked "again before use"; between renders that is
  the size/mtime/file-key fingerprint, re-hashed only when it changes (design §13) — wording could say so.
- Carried from attempt 1 (still standing): `java.io.tmpdir` with `:`, `;`, `$` or a relative path (B); probe that
  "could not be judged" pins HOST/UNAVAILABLE for the JVM's life, and no unit test for those branches (A);
  `shipsLibraries()` keyed on `FontMode.LINUX_FONTCONFIG` (A); HOST mode runs `--version` twice at startup, so §12's
  "nothing changes" overstates (A); test constant `HOST_MESSAGE` holds the BUNDLED text (A).

**Closing record — CHG-PPE-T04: PASS (attempt 2 of 3), 2026-11-25**

| Field | Value |
|---|---|
| Final status | **PASS** — Reviewer A PASS + Reviewer B PASS on attempt 2 |
| Attempts | 2 (attempt 1: Reviewer A FAIL — FN-007 platform, OPS-001 provenance; Reviewer B PASS) |
| Requirements covered | FN-002 (three "Minimal Linux" scenarios), FN-003, FN-007 (old glibc), SEC-001 (libraries), OPS-001 (libraries), OPS-002 (library mode, platform) |
| Decisions made | Budget re-baselined to about 1,900 LOC (owner, tripwire at resume); execute-time spec edits `design.md` §11 and DD-2 (owner, at the attempt-1 gate; `requirements.md` §9) |
| Issues encountered | Budget tripwire at resume; spec tensions FN-007 vs §11 and DD-2 vs `tasks.md`, resolved by amendment |
| Final verification | Leader re-run VERIFIED: clean compile 2430/1073/155, Checkstyle delta 0, hygiene 0 FIX, `Tests run: 192, Failures: 0, Errors: 0, Skipped: 7`, libraries match `SHA256SUMS`; container runs (attempt 1) bookworm-slim 0 differing pages, Tomcat `libraries=host` + gated test OK (7), amazonlinux:2023 WARN + failed render, no hang |
| continuations | 0 · checkpoints: 0 |
| runtime events | none |
| spawns (this session) | Reviewer A1 22 calls, 116,986 tokens, ended fail; Reviewer B1 14 calls, 99,267 tokens, ended complete; Implementer A2 37 calls, 148,237 tokens, ended partial; Reviewer A2 15 calls, 80,769 tokens, ended complete; Reviewer B2 12 calls, 65,050 tokens, ended complete |

**Budget running totals (design §18, re-baselined 2026-11-25):** tasks 4/6 · Java LOC about 1,717 (T01 83 + T02 438
+ T03 236 + T04 960: main +387/−14, tests 573) against about 1,900 · review rounds 9/11 (T01 1, T02 2, T03 2, T04 4)
· WAR growth 35.2 + 0.9 MB (`linux-libs/` 940 KB) ≈ 36.1 MB / cap 60 MB. Within budget.

### CHG-PPE-T05 — DejaVu in the page on macOS and Windows

**Attempt 1 (Implementer sonnet, effort `high`, 2026-11-25)** — skills `marlo-verify` (task list); `tdd` not assigned
(S task, test-suite route).
- Files changed: `PhantomJsRenderer.java` (+64: `pageFontBlock()` built once — two `@font-face` over
  `data:font/truetype;base64` regular/bold, family `MarloDejaVu`, plus `*{font-family:'MarloDejaVu' !important;}`;
  `withPageFonts()` inserts before the first `</head\s*>` any case, else at document start; `render()` applies it only
  when `usesPageFonts()`); `PhantomJsRendererTest.java` (+115, 5 tests); `PhantomJsRenderingTest.java` (+60/−3:
  `theShippedMacosPhantomJsEmbedsOnlyDejaVuAndKeepsTheTextExtractable`, shipped test also on macOS);
  `LocalPdfReportServiceTest.java` (+11/−2, outside the task's file list: consumer
  `thePhantomJsInputForTheProductionTemplatesIsTheHtmlHandlebarsJsProduces` now expects `withPageFonts(inlined)` on
  page-font hosts, still exact equality).
- Falsifiers: (1) block for Linux too → `PhantomJsRendererTest` 50 run, 5 failures (3 new + 2 existing), first
  `a Linux build gets the sanitizer's output and nothing else expected:<…</style></head>…> but was:<…</style><style
  type="text/css">@font-face{font-family:'MarloDejaVu';…`; Linux gated run not done (no Docker — T06). (2) universal
  rule removed → `oicr fonts: only the DejaVu pair MARLO ships expected:<[DejaVuSans, DejaVuSans-Bold]> but
  was:<[Helvetica, Helvetica-Bold, Helvetica-Light]>` (matches the S-3 red run). Both restored (`cmp`).
- Implementer verification: clean compile 2430/1073/155; Checkstyle delta 0; hygiene 0 FIX; task command first run
  `Tests run: 58, Errors: 1` on the pre-existing `anInterruptedThreadKillsTheProcessAndKeepsItsInterruptFlag`
  (`NumberFormatException: For input string: ""` in `FakePhantomJs.childOf`/`isAlive` — read as a race in the fake
  reading a pid file before it is written; untouched by T05), then 58/0/0/6 three times; whole PDF suite
  `Tests run: 198, Failures: 0, Errors: 0, Skipped: 6`. Production templates (local `lpre_live_tmp.report_configurations`,
  latest row) rendered via a throwaway test: oicr 5 / innovation 6 / project 7 pages, DejaVuSans + DejaVuSans-Bold
  only, words on every page; no font `!important` in them.
- Not Done / Assumptions (summary of the Implementer's field — assumptions only, no owed item, no blocker → no
  continuation): Windows not rendered anywhere (P-18, T06; `PhantomJsBuild.WINDOWS` is `FontMode.PAGE`); a
  `</head>` inside a head comment or attribute would receive the block (author's own markup, left); family name
  `MarloDejaVu` is the Implementer's choice; a stale red-run PDF under `target/` was overwritten by the green run.
- **Evidence re-run (Leader-inline): VERIFIED** — hygiene exit 0; clean compile first attempt failed in
  `maven-resources-plugin` copying `maps/GeoLite2-City.mmdb` (environmental, no code error; disk 32 % used), retry
  BUILD SUCCESS 2430/1073/155; Checkstyle `HEAD=0 working tree=0 delta=0` on the 4 files; whole PDF suite
  `Tests run: 198, Failures: 0, Errors: 0, Skipped: 6`; task command `Tests run: 58, Failures: 0, Errors: 0,
  Skipped: 6` with `theShippedMacosPhantomJsEmbedsOnlyDejaVuAndKeepsTheTextExtractable` and
  `theShippedPhantomJsRendersTheProjectReportWithNoConfiguredPath` **ran** (surefire XML); poppler on the gated
  outputs: oicr 5 pages `DejaVuSans DejaVuSans-Bold` words/page `46 135 144 182 91`; innovation 6 pages same fonts
  `42 170 119 46 68 97`; project 7 pages same fonts `53 16 19 18 14 14 41`.
- spawns: Implementer 45 calls, 176,529 tokens, ended partial (Not Done / Assumptions, assumptions only).

**Attempt 1 — Reviewer verdicts (parallel lens mode, SEC-003 surface, 2026-11-25)**
- **Reviewer A (conformance + reliability/resilience + readability, opus): PASS.** Block shape, build-once, insertion
  after `sanitize()` and image inlining for PAGE builds only, Linux/no-build byte-identity, `render-page.js` and
  allowlist untouched, Disqualifier met (PDFBox fonts + poppler), both falsifier reds quoted; no contradiction with
  amended §11/DD-2. `LocalPdfReportServiceTest` edit judged in scope (forced consumer; T05's Consumers list missed it —
  spec gap recorded). Flagged the "production templates" evidence as not independent unless a `cmp` is quoted.
- **Leader check (inline, read-only DB) settling that point:** latest row of `lpre_live_tmp.report_configurations`
  exported per column and compared with `marlo-web/src/test/resources/pdf/templates/*.html` → oicr 64,621 bytes
  **identical**, innovation 65,335 **identical**, project 63,121 **identical**. The committed gated macOS test therefore
  renders the production templates: "the production templates (they declare `Montserrat`) still give DejaVu only" is
  proven by the test itself.
- **Reviewer B (security + risk, opus): PASS.** Ordering after the sanitizer, allowlist unchanged (font URIs pass the
  existing `font/` rule, each ~1.0/0.94 MB base64 against the 2 MB cap, `blockedUris=0`), Linux/FreeBSD unchanged,
  missing font → `PdfRenderException` with class name only, regex linear, SEC-002 surfaces untouched.

ADVISORY (4R, recorded):
- **RISK (B, high value — owner decision):** plain-text insertion before the first `</head>` is not parse-aware. A head
  `<style>` whose text holds `</head><img src=x onerror=…>` survives `sanitize()` (raw text), `HEAD_END` matches inside
  it, the block's `</style>` closes the outer style early and WebKit parses the `<img onerror>` — script in the render
  page (JS cannot be disabled in PhantomJS 2.1.1; allowlist and `--local-url-access=false` still bound it). Same via a
  head `<meta content="…</head>…">`. Unreachable from user data today (all `{{{ }}}` placeholders come after
  `</head>` in the three templates; `{{ }}` escapes `<`). Suggested: insert at document start, after an optional
  leading `<!DOCTYPE …>` (`!important` already makes order irrelevant), plus a regression test.
- **RISK (A):** the family name `MarloDejaVu` is fixed and public; payload rich text through `{{{ }}}` can declare a
  later `@font-face{font-family:'MarloDejaVu';src:url(data:font/truetype…)}` that the sanitizer keeps and
  `render-page.js` allows, replacing DejaVu without `!important`. Suggested: a per-JVM unpredictable family name.
- RELIABILITY/RESILIENCE: a `</head>` in a head comment would comment the block out (silent Helvetica); DOCTYPE with no
  `</head>` puts the `<style>` before the DOCTYPE (quirks mode); ~2 MB block kept per JVM and HTML copied ~3× per
  render (note for T06 Windows); IOException branch untested and its message drops the resource name.
- READABILITY: `SHIPPED_MACOS_TEST` Javadoc says "tests"; `titles[]` duplicates `expectedText[i][0]`.
- Issues encountered: pre-existing flaky `anInterruptedThreadKillsTheProcessAndKeepsItsInterruptFlag`
  (`NumberFormatException` in `FakePhantomJs.childOf`, race on the pid file) — not T05's.

**Gate (2026-11-25):** both Reviewers PASS, but the two RISK advisories show a tension between `tasks.md` T05 Scope
("inserted before `</head>`") and SEC-002/SEC-003 (the sanitizer's guarantee must hold for what reaches PhantomJS;
only DejaVu). Per Pivot Detection (spec tension), T05 is **not** closed yet: a one-line amendment proposal is put to
the owner before `[x]`.
- spawns: Reviewer A 28 calls, 86,904 tokens, ended complete; Reviewer B 22 calls, 82,951 tokens, ended complete.
  Review rounds 11/11 (T05 2).
- **Decision (owner, 2026-11-25, at the T05 gate):** asked for a recommendation, then "sí, opción 1, enmienda T05 y
  lanza el intento". T05 set back to `[~]`; execute-time spec edits (no requirement meaning changed), recorded in
  `requirements.md` §9 with the closure sweep: `tasks.md` T05 Scope + Tests (insertion at document start after an
  optional DOCTYPE; family name random per JVM; two regression tests), `design.md` DD-3 and §13 SEC-003 bullet,
  `design.md` §18 review rounds 13. Pivot-lite (spec tension): attempt counter not consumed — the re-run is recorded
  as **attempt 1b (post-amendment)**; the attempt-1 brief is superseded by a re-issued brief carrying the amended text.

**Attempt 1b — post-amendment re-run (Implementer sonnet, effort `high`, 2026-11-25)**
- Files changed: `PhantomJsRenderer.java` (`HEAD_END`, the `</head>` search and the public `PAGE_FONT_FAMILY` removed;
  `withPageFonts()` inserts after a leading `<!DOCTYPE …>` matched by `LEADING_DOCTYPE` = `\A\s*<!DOCTYPE[^>]*>`
  (case-insensitive) or at offset 0; `pageFontBlock()` picks the family `pf` + 32 hex from 16 `SecureRandom` bytes once
  per JVM, used by both `@font-face` rules and the universal rule); `PhantomJsRendererTest.java` (insertion tests
  replaced; new `theBlockGoesAfterALeadingDoctypeAndAtTheStartOtherwiseWhateverTheMarkupHolds`; regression (a)+(b)
  `aHeadEndTagInsideAHeadStyleOrMetaDoesNotMoveTheBlockIntoItNorLetAnElementOut` — sent HTML minus the block equals
  `sanitize()` output, block at offset 0 or after the doctype, `Jsoup.parse` counts of `[onerror]`/`img[src=x]` equal
  the sanitized ones; regression (c) `aPayloadFontFaceNamingTheOldFixedFamilyDoesNotReachTheFamilyOfTheUniversalRule`).
  `PhantomJsRenderingTest`/`LocalPdfReportServiceTest` unchanged since attempt 1.
- Falsifiers (each restored, `cmp`): (1) `</head>` text search put back → 3 failures, incl. regression (a)/(b) "…the
  block sits at the start, after the doctype if there is one expected:<15> but was:<46>"; (2) fixed `MarloDejaVu` →
  2 failures, "a name nobody can know ahead of time: MarloDejaVu" and "two @font-face rules of one family
  expected:<2> but was:<3>"; (3) block for Linux too → 5 failures incl. Linux byte-identity.
- Implementer verification: clean compile 2430/1073/155; Checkstyle delta 0; hygiene 0 FIX; targeted 60 run / 0 / 6
  skipped, macOS gated test ran (5/6/7 pages, DejaVu pair); whole PDF suite 200 / 0 / 6; flake did not fire.
- Not Done / Assumptions (assumptions only, no owed item): family prefix `pf` is the Implementer's choice; Windows not
  verified at runtime (T06); scratch log written to `/tmp/claude-502/t05.log` outside the repo.
- **Evidence re-run (Leader-inline): VERIFIED** — hygiene exit 0; clean compile 2430/1073/155 BUILD SUCCESS;
  Checkstyle `HEAD=0 working tree=0 delta=0`; whole PDF suite `Tests run: 200, Failures: 0, Errors: 0, Skipped: 6`;
  surefire: `theShippedMacosPhantomJsEmbedsOnlyDejaVuAndKeepsTheTextExtractable` and
  `theShippedPhantomJsRendersTheProjectReportWithNoConfiguredPath` ran; poppler: oicr 5p, innovation 6p, project 7p,
  each `DejaVuSans DejaVuSans-Bold` only.
- spawns: Implementer 23 calls, 129,682 tokens, ended partial (assumptions only).

**Attempt 1b — Reviewer verdicts (parallel lens mode, 2026-11-25)**
- **Reviewer A (conformance + reliability/resilience + readability, opus): PASS.** Amended Scope/Tests, DD-3 and §13
  met; insertion after `LEADING_DOCTYPE` or at 0 matches the HTML5 tokenizer's doctype end; `project.html` (doctype)
  and `oicr`/`innovation` (no doctype) paths both run in the gated render; a `<style>` before `<html>` lands in the
  implicit head (render confirms 5/6/7, DejaVu only); random family from one builder under `static synchronized`,
  stable per JVM; regression (a)/(b)/(c) assert on what the fake received against `sanitize()` output; attempt-1
  properties preserved; no scope beyond the amendment; Decision Log sweep holds. Issues: none.
- **Reviewer B (security + risk, opus): PASS.** Both attempt-1 advisories closed. Anchored prefix only; a `>` inside a
  quoted doctype identifier ends the doctype in WebKit and jsoup alike; a non-leading payload doctype is dropped by
  jsoup; `\x0B` edge harmless; no ReDoS. The block holds no `<` beyond its own tags and always starts at top level.
  Family: 128-bit `SecureRandom`, never logged nor in exceptions; the PDF exposes font-file names only (per Leader
  poppler, not re-checked by B); a leaked name would add nothing beyond SEC-003's accepted `!important` gap —
  informational. SEC-002 surfaces, allowlist and `render-page.js` unchanged; Linux/no-build byte-identical. Issues:
  none.

ADVISORY (4R, recorded only — never a task):
- RELIABILITY/RISK (A, B): a BOM (`﻿`, not matched by `\s`) or a comment/PI before `<!DOCTYPE` puts the block at
  0 and drops a standards-mode page into quirks mode on macOS/Windows; no fixture or production row has one (A: grep
  0). B: record rather than widen — content-independence is worth more. A: a sentence in DD-3 would prevent reading
  it as a defect later.
- READABILITY (A, B): the regression's doctype oracle copies the production pattern; hard-code offsets or expose the
  pattern package-private.
- RELIABILITY (B): jsoup stands in for WebKit in the breakout regression; no real PhantomJS run of the breakout pages
  — accepted gap.
- Still open from attempt 1: IOException message drops the resource name; ~2 MB block copied ~3× per render (T06).

**Closing record — CHG-PPE-T05: PASS (attempt 1b, post-amendment), 2026-11-25**

| Field | Value |
|---|---|
| Final status | **PASS** — Reviewer A PASS + Reviewer B PASS on attempt 1b (attempt 1 also PASS ×2, reopened by owner-approved amendment) |
| Attempts | 1 consumed (no FAIL); 1b = post-amendment re-run (Pivot-lite, spec tension) |
| Requirements covered | FN-005, NF-002 (macOS), SEC-002, SEC-003; scenarios "Fonts on macOS and Windows" (macOS), "Template or payload declares its own font" |
| Decisions made | `LocalPdfReportServiceTest` edited as a forced consumer (Consumers list gap); T05 amendment (insertion at document start after optional DOCTYPE; random family per JVM; review rounds 13) — `requirements.md` §9 |
| Issues encountered | Pre-existing flaky `anInterruptedThreadKillsTheProcessAndKeepsItsInterruptFlag` (`FakePhantomJs.childOf` race); one environmental `maven-resources-plugin` copy failure (`GeoLite2-City.mmdb`), retry green |
| Final verification | Leader re-run VERIFIED: clean compile 2430/1073/155, Checkstyle delta 0, hygiene 0 FIX, PDF suite 200/0/6, macOS gated test ran, poppler 5/6/7 pages `DejaVuSans DejaVuSans-Bold` only, words on every page; fixtures byte-identical to production rows. Windows not run (P-18, T06); Linux gated (Docker) run deferred to T06 per tasks.md |
| continuations | 0 · checkpoints: 0 · runtime events: none |
| spawns | Implementer 1: 45 calls, 176,529 tokens, ended partial; Reviewer A1 28 calls, 86,904 tokens, complete; Reviewer B1 22 calls, 82,951 tokens, complete; Implementer 1b 23 calls, 129,682 tokens, ended partial; Reviewer A1b 15 calls, 80,188 tokens, complete; Reviewer B1b 18 calls, 76,330 tokens, complete |

**Budget running totals (design §18, re-baselined 2026-11-25 for T05) — TRIPWIRE:** tasks 5/6 · **Java LOC about
2,066** (T01 83 + T02 438 + T03 236 + T04 960 + T05 349: main +75, tests +274) **against about 1,950** · review rounds
**13/13** (T05 4) · WAR growth ≈ 36.1 MB / 60 MB. LOC exceeded by about 116 (the regression tests, +88 lines in 1b);
T06 needs at least 1 more review round (checklist). **Run stopped at the T05 gate and escalated to the owner.**
- **Decision (owner, 2026-11-25, at the T05 tripwire):** "sí, haz el commit, sube el presupuesto y sigue con T06" —
  budget re-baselined in `design.md` §18 to about 2,100 Java LOC and 14 review rounds; T05 committed.

### CHG-PPE-T06 — Cross-platform verification and size

**Attempt 1 (Implementer sonnet, effort `medium`, 2026-11-25)** — evidence only; no production or test code changed,
nothing committed. Sources: final `b537745d0e`, baseline `e452378187` (worktree, classes and WAR built with
`package`, never `install`; worktree removed and pruned). Harness rebuilt in this session's scratchpad (`t06/`:
`T06Render.java` builds `LocalPdfReportService()`, `initFromConfig()`, renders the three fixture payloads;
`run-container.sh`, `pixcmp.sh` = per-page `pdftoppm -r 72 -png` + `shasum`). Leader decision recorded in the brief:
no Windows host known.

**(1) Linux, `tomcat:9.0-jdk17-temurin`, `--platform linux/amd64 --network none`** — fontlibs `4`, glibc Ubuntu 2.43.
Startup `PhantomJS available for local PDF renders, version=2.1.1, platform=Linux/amd64, build=linux-x86_64,
libraries=host, path=…`; renders `outcome=ok` 557,407 / 1,102,324 / 74,472 bytes, 5/6/7 pages (baseline sizes
identical). Pixel comparison baseline vs final: oicr 0/5, innovation 0/6, project 0/7 differing pages (raw bytes
differ — PDF metadata; pixels identical). Gated `PhantomJsRenderingTest` via JUnitCore in the image: `OK (8 tests)`
(shipped project test rendered 7 pages; 7 skip without `PHANTOMJS_PATH`).
**Falsifier red:** scratch copy of `PhantomJsRenderer.java` with `usesPageFonts()` → `return true`, compiled into a
scratch class directory ahead of the final classes (main source never edited): `outcome=ok` but 1,284,784 /
1,719,335 / 279,282 bytes, pixel comparison **5/5, 6/6, 7/7 pages differ**.

**(2) Minimal Linux and old glibc at the final commit** — `debian:bookworm-slim --network none` (JDK copied in):
fontlibs `0`, glibc 2.36-9+deb12u14; `PhantomJS does not start with the host libraries; using the libraries shipped
with MARLO`, startup `… libraries=bundled …`; renders `outcome=ok` 5/6/7, same sizes; pixel comparison vs baseline
0/5, 0/6, 0/7. `amazonlinux:2023` (glibc 2.34, fontlibs 0): one WARN `PhantomJS does not start with the host
libraries nor the shipped ones (glibc older than 2.36?), platform=Linux/amd64: local PDF renders will fail`; renders
`outcome=failed` in 364 / 121 / 224 ms, no hang.

**(3) macOS (Mac OS X/aarch64)** — gated: `mvn -o -pl marlo-web -am test -Dtest=PhantomJsRenderingTest`
(`PHANTOMJS_PATH` unset) → `Tests run: 8, Failures: 0, Errors: 0, Skipped: 6`; shipped macOS test 5/6/7 pages,
`[DejaVuSans, DejaVuSans-Bold]`. Live app (`./scripts/run-marlo-java17.sh`, flag on, no override): startup
`PhantomJS available for local PDF renders, version=2.1.1, platform=Mac OS X/aarch64, build=macosx, libraries=n/a,
path=<tmp>/phantomjs`. Downloads (unit AICCRA): OICR study 3508 → 200 `application/pdf;charset=UTF-8`, 238,065 bytes,
4 pages, `elapsedMs=6921`; innovation 1558 → 200, 1,007,244 bytes, 6 pages, 1499 ms; project 102076 → 200, 8,439,032
bytes, 169 pages, 16,823 ms (18.07 s wall); all `outcome=ok blockedUris=0 failedFetches=0`, `pdffonts` DejaVuSans +
DejaVuSans-Bold only. Notes: the first run-script attempt failed at test-compile (`class file for …BaseAction not
found`), the identical second attempt built and started (cause not investigated — matches the known dirty-`target/`
symptom); `marlo-dev.properties` points at `partroute_local`, which has no `local_pdf_report_engine_active` row, so
`mysql.database=lpre_live_tmp` (flag `true` for unit 45) was set temporarily and **restored** (SHA-1 `b3801c73…`
equals the saved original); the DB flag was not changed; Flyway "Schema `lpre_live_tmp` is up to date".

**(4) Windows: not verified at runtime.** No host known or reachable; none provisioned. **P-16, P-17, P-18 stay open.**

**(5) WAR size** — baseline `e452378187` 304,474,384 bytes; final `b537745d0e` 340,570,002 bytes; growth 36,095,618
bytes = **36.1 MB** (34.4 MiB) against the 60 MB cap (NF-004).

**(6) First render** (first `elapsedMs=` per run): Tomcat image 1,964 ms; bookworm-slim (shipped libraries) 1,565 ms;
macOS gated 6,782 ms; macOS live app (OICR) 6,921 ms — all within 50 s (NF-006). Linux startup under 1.3 s.

- Cleanup: worktree removed; app stopped, no listener on 8080 (an unrelated INVFRIJOL Tomcat 8.5 and a Chrome
  `CLOSE_WAIT` left untouched); Docker containers removed; `marlo-dev.properties` restored; `target/` rebuilt by the
  run script.
- Not Done / Assumptions (assumptions + the Windows gap the task itself provides for; no owed item, no blocker):
  Windows not run; later runs used `docker cp` after bind-mounts stopped working (earlier runs bind-mounted the same
  final classes, confirmed by the startup line); every Linux run used `--platform linux/amd64` (local tag is arm64);
  macOS first render includes PhantomJS extraction and Handlebars warm-up, single sample; the 169-page project report
  is real data, ~17 s.
- **Evidence re-run (Leader-inline): VERIFIED** — `pixcmp.sh` re-run on the kept PDFs: base vs final-tomcat 0/5, 0/6,
  0/7; base vs final-bookworm 0/5, 0/6, 0/7; base vs falsifier 5/5, 6/6, 7/7; startup and render lines re-read from
  `out/final-tomcat.log`, `out/final-bookworm.log`, `out/final-amazon.log` and `app.log` (as quoted above); live PDFs
  re-checked with poppler (4/6/169 pages, DejaVuSans + DejaVuSans-Bold only); `marlo-dev.properties` SHA-1 equals the
  original, `mysql.database=partroute_local`; `git worktree list` shows no T06 worktree; tree clean. Not re-runnable
  by the Leader: the baseline WAR size (worktree removed) — `marlo-web/target/marlo-web.war` now reads 340,571,031
  bytes (+1,029 vs the report) because the run script rebuilt it after the measurement; growth stays 36.1 MB.
- spawns: Implementer 103 calls, ~202,000 tokens, ended partial (Not Done / Assumptions; Windows gap). 103 calls is
  past the v2.45.0 60-call checkpoint bound the adapted persona lacks (persona drift recorded in Document Control).

**Attempt 1 — Reviewer verdict (`checklist`, single Reviewer, override (c) derived evidence, 2026-11-25)**
- **Reviewer (opus): PASS.** Every number and quoted line in this entry traced to the `t06/` artifacts (startup lines
  `out/final-tomcat.log:5`, `out/final-bookworm.log:5-6`, `out/final-amazon.log:5`, `app.log:447`; `OK (8 tests)`
  `out/gated-tomcat.log:81`; `Tests run: 8 … Skipped: 6` `mac-gated.log:119`; `live/*.hdr` 200 + `application/pdf` with
  Content-Length equal to the rendered bytes). Baseline is pre-change code: loaded from the separate worktree
  (`cp-base-run.txt`) and its startup line has the pre-T02 format (no platform/build/libraries). Falsifier real:
  `fals-src/PhantomJsRenderer.java:1085-1087` forces `usesPageFonts()`; repo source unchanged. Windows fallback as the
  task provides. Done-when (1)–(6) met. Issues: none.
- Claims without a backing file (none wrong): per-page pixel counts rest on the Leader's inline `pixcmp.sh` re-run
  (PDFs kept, repeatable); the two WAR byte figures appear in no artifact; "18.07 s wall" is unlogged (only
  `elapsedMs=16823`); the Linux "7 skip" is not printed by JUnitCore; the live `pdffonts` result is the Leader's.
- **Corrections to this entry (Reviewer, recorded here; the lines above stand as written):** for `amazonlinux:2023`
  "fontlibs 0" is **not probed** — `run-container.sh:12` checks `/usr/lib/x86_64-linux-gnu`, Amazon Linux uses
  `/usr/lib64` (the minimal-image claim rests on bookworm-slim, a real 0). The live run's Flyway also logged
  "Successfully repaired metadata table `lpre_live_tmp`.`schema_version`" (`app.log:6270`) — a write to the local
  `lpre_live_tmp` database, harmless.
- ADVISORY (recorded only): for `/akili-validate`, the baseline commit hash, pixel counts and WAR sizes exist only as
  prose — re-measure there if needed; NF-003 ("only the current platform's build extracted") has no T06 artifact
  listing the support directory (`--network none` proves only no download) and NF-001's "8 parity cases" vs T06's 3
  fixtures — validate should map both to their owning evidence. RISK: the scratchpad copy of the gitignored
  `marlo-dev.properties` and `login.out` — **deleted by the Leader on 2026-11-25** after the restore check.

**Closing record — CHG-PPE-T06: PASS (attempt 1), 2026-11-25**

| Field | Value |
|---|---|
| Final status | **PASS** — checklist Reviewer |
| Requirements covered | NF-001 (3 fixtures, pixel-identical), NF-002 (macOS), NF-004 (36.1 MB), NF-006 (≤ 6.9 s), FN-001/OPS-002 (startup line on every run), FN-002/FN-007 re-run at the final commit; P-16…P-18 **open** (Windows not run) |
| Decisions made | No Windows host (Leader assumption in the brief, owner did not name one); live run against `lpre_live_tmp` via a temporary, restored `marlo-dev.properties` change |
| Issues encountered | Bind-mounts stopped working mid-run (switched to `docker cp`); first run-script attempt failed at test-compile (dirty `target/` symptom), second attempt green |
| continuations | 0 · checkpoints: 0 · runtime events: none |
| spawns | Implementer 103 calls, ~202,900 tokens, ended partial (Windows gap); Reviewer 35 calls, 119,996 tokens, ended complete |

**Budget running totals (design §18, re-baselined 2026-11-25 at T05 close):** tasks 6/6 · Java LOC about 2,066 /
about 2,100 · review rounds 14/14 · WAR growth 36.1 MB / 60 MB. Within budget.

## Summary — all tasks complete (2026-11-25)

| Task | Result | Commit |
|---|---|---|
| T01 Ship the macOS and Windows builds | PASS | `197422769b` |
| T02 One platform table chooses the build | PASS | `4cae4e4f6d` |
| T03 Child-process environment per platform | PASS | `6ce75d9411` |
| T04 Linux library fallback | PASS (attempt 2) | `256753d6d9` |
| T05 DejaVu in the page on macOS and Windows | PASS (attempt 1b, post-amendment) | `b537745d0e` |
| T06 Cross-platform verification and size | PASS | (this entry) |

Open for `/akili-validate` and the Definition of Done: Windows not verified at runtime (P-16…P-18 open); the full
`marlo-web` suite with `-am` was not run in this execution (the PDF package suite was, 200/0/6); pending `staging`
items — merge the v2.45.0 persona sections into `.agents/*.md`, drop the macOS PhantomJS note from
`docs/infrastructure.md` §6.

## Validation remediation (2026-11-25, `/akili-validate`, owner: "sí, dale con la opción 1")

- First pass: 0 FAIL · 7 WARN (W1–W7) · 26 advisories (`validation-report.md` §2–§12). Full suite with `-am`:
  `Tests run: 1502, Failures: 0, Errors: 0, Skipped: 6`.
- Leader (spec text): W1 acceptance, W5, W6 and W7 in `requirements.md` §9 (Decision Log 2026-11-25, with closure
  sweep), `design.md` §12, §14, DD-4, `tasks.md` header and §6, `proposal.md` status.
- Implementer (sonnet, effort `medium`; 25 calls, 129,650 tokens, ended complete): A-2 (`Assume` moved first in
  `EmbeddedPhantomJsTest.theRealResourceExtractsToTheKnownBuild`), W4 (`marlo-test.properties` comment), W2 (5
  remaining parity cases: 0 differing pages against `e452378187` for OICR 3508, Innovation 1558, Cluster 102076 (481
  pages), missing-flag and font probes; falsifier red; original CHG-LPRE payloads reused from an earlier scratchpad —
  no app run, no dump line, no credentials).
- Evidence re-run (Leader-inline): VERIFIED — `pixcmp-p.sh` base vs final 0/5, 0/6, 0/481, 0/1, 0/1; base vs
  falsifier: OICR 5/5, innovation 6/6, both probes 1/1 differ; cluster absent (timed out at 50 s); `EmbeddedPhantomJsTest`
  `Tests run: 30, Failures: 0, Errors: 0, Skipped: 0`.
- Reviewer (opus, checklist, 5 calls, 41,726 tokens): PASS on W4 + A-2; the Implementer's first-run "killed at the
  render deadline" in `queryVersion()` is a cold start after a clean build, unrelated → tracked with A-24.
- Result: `validation-report.md` §13 — **PASS, archive-ready**; W1 accepted, W2–W7 fixed.
