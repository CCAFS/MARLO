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
