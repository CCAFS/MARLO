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
