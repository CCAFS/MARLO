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
