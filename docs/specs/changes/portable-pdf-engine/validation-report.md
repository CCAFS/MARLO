# Validation Report — Portable PDF Engine

> **Verdict: PASS — archive-ready.** After remediation (2026-11-25): **0 FAIL · 0 open WARN** (W1 accepted by the
> owner in the Decision Log; W2–W7 fixed) · 26 advisories recorded.
>
> Every requirement that can be checked on Linux x86_64 and macOS is met in code and backed by evidence. Linux parity
> now holds on **all 8** CHG-LPRE cases (0 differing pages against `e452378187`). The full `marlo-web` suite passes
> with `-am` (1,502/0). Windows ships **unverified at runtime**, accepted by the owner; P-16…P-18 and OQ-7 stay open
> for the first real Windows install.
>
> The first pass (§2–§12 below) is kept as written. §13 records the remediation and the re-check.

## 1. Document Control

| Field | Value |
|---|---|
| Spec | CHG-PORTABLE-PDF-ENGINE-001 · `docs/specs/changes/portable-pdf-engine/` |
| Branch / HEAD | `feature/local-pdf-report-engine` / `f2c1105ce2` (pushed) · code baseline `e452378187` |
| Date | 2026-11-25 |
| Validator | Leader session (opus) for the build and test runs; an independent T3 auditor (opus, read-only, fresh context) for coverage, design and figures. The Implementer was sonnet, so author ≠ auditor |
| Inputs | proposal, requirements, design, tasks, execution (no `test-report.md`: `/akili-test` was not run, so coverage was checked directly) |
| Flags | `Deliverable`: software (no row). `AI Agent`: no row → no eval gate. UI: none → no design review |

## 2. Summary

| Area | Result | Note |
|---|---|---|
| Task completion | **PASS** | T01–T06 `[x]`, each with a closing record and evidence |
| File existence | **PASS** / WARN | Everything the design names is present. The `marlo-test.properties` comment is out of date (W4) |
| Build integrity | **PASS** | Clean compile, Checkstyle no new violations, full suite 1,502/0/0/6 |
| Requirement coverage | **PASS** / WARN | 59 rows: 40 PASS · 14 WARN · 5 BLOCKED · 0 FAIL. Every WARN or BLOCKED row comes from Windows not being run, or from parity covering 3 of 8 cases |
| Design conformance | **WARN** | Three wording drifts with no Decision Log entry (W7) |
| Cross-document figures | **WARN** | `tasks.md` header still says ~650 LOC; metadata dates are stale (W6) |
| Constitution | **PASS** | GPL headers, English only, no pom changes, no shared-file edits, no credential files |
| Proposal alignment | **WARN** | Windows NTFS check and 8-case parity not done |

## 3. Task Completion

| Task | Status | Closing record | Done-when gaps |
|---|---|---|---|
| T01 Ship the macOS and Windows builds | PASS | Reviewer PASS, attempt 1 | none |
| T02 One platform table | PASS | Reviewer PASS, attempt 2 (attempt 1: re-run mismatch, CRLF) | Tests-list item: `LocalPdfEngineDispatchTest` not extended (recorded advisory; not in Done-when) |
| T03 Environment per platform | PASS | Reviewer PASS, attempt 2 | Windows argument sameness verified by reading the code only (recorded) |
| T04 Linux library fallback | PASS | Reviewers A + B PASS, attempt 2 | Only the unit-test half of the falsifier was run. The container half was not (`tasks.md` T04 Falsifier), advisory A-22 |
| T05 DejaVu in the page | PASS | Reviewers A + B PASS, attempt 1b (re-run after the amendment) | The "Linux gated run finds no fonts" half was covered instead by T06's pixel falsifier (18/18 pages differ) |
| T06 Cross-platform verification | PASS | Checklist Reviewer PASS | (4) Windows recorded as not verified, which the task allows |

## 4. File Existence

| Named by design / tasks | At HEAD |
|---|---|
| `PhantomJsBuild.java` (new) | ✅ GPL header, the one platform table |
| `EmbeddedPhantomJs.java`, `PhantomJsRenderer.java`, `LocalPdfReportService.java` | ✅ modified as designed |
| `marlo-utils/.../APConfig.java` (Javadoc: override only) | ✅ CRLF preserved |
| macOS / Windows `.gz`, `.sha256`, `PROVENANCE-*.txt` | ✅ the auditor re-hashed them; decompressed bytes match `.sha256` for all 3 builds |
| `linux-libs/`: 7 libraries, `SHA256SUMS`, `PROVENANCE.txt`, 6 `copyright.*` | ✅ every library matches `SHA256SUMS` |
| Tests: `PhantomJsBuildTest`, `ShippedPhantomJsBuildsTest`, `PhantomJsLibrariesTest` (new); 4 modified | ✅ |
| `render-page.js`, `resources/pdf/fonts/`, any `pom.xml` | ✅ untouched, as required |
| Proposal §5: update the `pdf.phantomjs.path` description | ⚠️ `marlo-test.properties:235-236` still says "Linux x86_64 … override it (developer Mac, other platforms)" — **W4** |

No unexpected files changed. The untracked `log.folder_IS_UNDEFINED/` is not committed.

## 5. Build Integrity

| Gate | Command | Result |
|---|---|---|
| Clean compile | `.claude/skills/marlo-verify/scripts/clean-compile.sh` | **BUILD SUCCESS**, 2430 / 1073 / 155 files. The first attempt failed in unrelated tests with `cannot access User` (stale `target/`, the known symptom); the retry was green |
| Checkstyle | checkstyle 8.18 on the 12 Java files changed since `e452378187` | **9 = 9 at baseline, delta 0.** All 9 are pre-existing `getEmail_*` method names in `APConfig.java`, which this spec changes only in Javadoc. The other 11 files have 0 violations |
| Full test suite | `mvn -q -o -pl marlo-web -am test` | **`Tests run: 1502, Failures: 0, Errors: 0, Skipped: 6`** (exit 0). This closes the DoD item that had not been run since T02 |
| Dependencies | `git diff e452378187..HEAD -- '**/pom.xml'` | empty (NF-005, Hard rule 11) |
| Boot smoke | — | Not repeated: T06 (3) booted the stack with the §6.1 route and downloaded three reports (200 `application/pdf`) |

## 6. Requirement Coverage

Coverage was checked scenario by scenario and clause by clause, by quoted clause. No clause was cleared by citing a
different requirement. The full 59-row matrix, with file:line evidence, is in the auditor's working file; it was not
committed. The summary:

### Requirement IDs

| ID | Verdict | Evidence / reason |
|---|---|---|
| FN-001 build per platform, nothing to configure | **WARN** | Linux x86_64 and macOS proven (unit tests, T06 live run). Windows never run |
| FN-002 minimal Linux renders; host libraries kept | PASS | `PhantomJsLibrariesTest`; T06 (2) bookworm-slim: fontlibs 0, 0 of 18 pages differ |
| FN-003 libraries visible only to PhantomJS | PASS | `LD_LIBRARY_PATH` only in the cleared child environment; test asserts `java.library.path` and `getenv` unchanged |
| FN-004 Windows name, permissions, lock, sweep | **WARN** | Name and POSIX guard: unit tests. Lock and sweep never run on NTFS (P-17) |
| FN-005 only DejaVu, text extractable | **WARN** | Linux and macOS PASS. Windows BLOCKED: OQ-7 (fonts may come out as paths) is open |
| FN-006 override wins, stays optional | PASS | `aConfiguredPathAlwaysWinsAndTheShippedBuildIsNeverOpened` |
| FN-007 no build or old glibc: fail fast, one line naming the platform | PASS | Service and library tests; T06 amazonlinux: one WARN with `platform=Linux/amd64`, failures in 121–364 ms |
| NF-001 Linux parity on the 8 cases | **WARN** | 3 fixtures pixel-identical to `e452378187`. 3 real reports and 2 probes not re-run, `compare.py` not run (W2) |
| NF-002 macOS/Windows page counts, DejaVu, extractable text | **WARN** | macOS PASS. Windows BLOCKED |
| NF-003 only this platform's build extracted, no download | PASS | `system()` opens only the chosen resource; `--network none` runs. No test asserts the negative (A-20) |
| NF-004 WAR growth ≤ 60 MB | PASS | 36.1 MB |
| NF-005 no Maven dependency | PASS | empty pom diff |
| NF-006 first render within 30 s / 50 s | PASS | 1.6–6.9 s (Windows not measured) |
| SEC-001 SHA-256 before run or load | PASS | binary and library tamper tests; the auditor re-hashed the resources |
| SEC-002 CHG-LPRE protections unchanged | PASS | arguments are independent of the platform style; Windows sameness checked by reading the code |
| SEC-003 fonts after the sanitizer, allowlist unchanged | PASS | the `</head>` breakout and font-family hijack regressions; allowlist test |
| OPS-001 provenance and licences | PASS | all 3 builds and all 7 libraries; A-1: `SOURCE.txt` is worded for Linux only |
| OPS-002 startup line names platform, build, version | PASS | unit tests (Linux forms) + T06 logs (Linux and macOS) |

### Scenario clauses (41)

| Scenario | Clauses | PASS | WARN | BLOCKED |
|---|---|---|---|---|
| Supported platform, no configuration | 5 | 3 | 2 (Windows) | — |
| Override set | 2 | 2 | — | — |
| Platform with no build | 4 | 4 | — | — |
| Minimal Linux (3 scenarios) | 9 | 9 | — | — |
| Windows host | 4 | 1 (no POSIX API) | — | 3 |
| Fonts on macOS and Windows | 5 | 1 | 4 (Windows) | — |
| Linux parity unchanged | 2 | — | 2 (3 of 8 cases) | — |
| Tampered file | 3 | 3 | — | — |
| Template or payload declares its own font | 4 | 3 | 1 (Windows) | — |
| Defect-class gates | 3 | 1 | — | 2 (Windows) |
| **Total** | **41** | **27** | **9** | **5** |

The negative and strict clauses (`BUT it must NOT` / `AND IT MUST`) that can be checked on Linux and macOS each have
explicit code and test evidence: no property read to find the binary, OS + arch together, no microservice and no
hang, no library path change for other processes, no different behavior on a host with its own libraries, no POSIX
API on Windows (unit test), refusal of a tampered WAR resource, no host font (macOS), fonts not routed through the
sanitizer.

**Agent eval gate:** not applicable (`AI Agent` not recorded).

## 7. Linting & Code Quality

Checkstyle delta 0 (§5). Hygiene was clean at every task. Style checked by eye: no line over 120 columns, largest file
1,379 lines, braces everywhere.

### 4R advisory findings (advisory — do not drive the verdict)

| # | Lens | Finding |
|---|---|---|
| A-2 | Reliability | `EmbeddedPhantomJsTest.theRealResourceExtractsToTheKnownBuild` extracts before its `Assume`. On a host with no build (Linux/Windows ARM64) it now **errors** instead of skipping, a small regression from baseline. Fix: move the `Assume` first |
| A-3 | Risk (doc accuracy) | `libs/` is created with default permissions; the real protection is the 0700 support directory around it. The T04 Reviewer B note "`libs/` 0700" overstates this |
| A-12 | Risk | A `java.io.tmpdir` containing `:`, `;`, `$`, or a relative path would make the loader search outside `libs/` (T04) |
| A-13 | Resilience | A probe that "could not be judged" fixes the mode at HOST or UNAVAILABLE for the life of the JVM; no unit test covers those branches |
| A-14 | Reliability | `shipsLibraries()` keys on `FontMode.LINUX_FONTCONFIG`, so a future Linux build of another architecture would inherit the x86_64 libraries |
| A-16 | Reliability | A BOM, comment or processing instruction before `<!DOCTYPE` puts the font block at offset 0 and switches macOS/Windows to quirks mode (no current template has one) |
| A-9 | Risk | Between renders, files are accepted on a size/mtime/file-key fingerprint, not re-hashed (accepted CHG-LPRE design). `PROVENANCE.txt` "again before use" overstates this |
| A-19 | Risk | Page fonts follow the host build even when the override is set (not recorded as a decision) |
| A-24 | Reliability | Pre-existing flaky `anInterruptedThreadKillsTheProcessAndKeepsItsInterruptFlag` (`FakePhantomJs` pid-file race) |
| A-17 / A-18 | Evidence | Proposal P-3 lists `libbz2` but it is not shipped, with no explanation. Cluster 102076 is 444/482 pages in the proposal and 169 pages in the T06 live run, with no explanation (different DB or phase?) |
| Others | Readability | A-1 `SOURCE.txt` Linux-only wording; A-4 stale "Windows joins in T06" test comments; A-5 test-only overload; A-6 `toLowerCase()` without `Locale.ROOT`; A-7, A-8 test-only parsing; A-10, A-11 Windows owner/argument notes; A-15 `HOST_MESSAGE` holds the BUNDLED text; A-20 no NF-003 negative test; A-21 ambiguous OQ numbering in the proposal; A-23 font IOException message; A-25 Consumers-list miss, 103-call T06 spawn, Flyway repair of the local DB; A-26 §11 "Two new WARN lines" lists one INFO |

## 8. Design Conformance

| Item | Result |
|---|---|
| DD-1 one table, OS + arch | Conforms |
| DD-2 probe; trigger "error while loading shared libraries" (amended 2026-11-25) | Conforms |
| DD-3 page fonts at the start / after DOCTYPE, random family (amended 2026-11-25) | Conforms |
| DD-4 environment per style | **Drift:** the Windows list in DD-4 omits `LANG`, while `tasks.md` T03 and the code set `LANG=C.UTF-8` (W7) |
| DD-5, DD-6, DD-7 | Conform |
| §11 log lines (amended) | Conforms (A-26: heading miscounts) |
| §12 "On Linux with host libraries, nothing changes" | **Overstated:** HOST mode adds one `--version` run at startup (W7) |
| §14 "byte-identical, verified by a byte comparison" | **Not met as worded:** raw bytes differ (PDF metadata); pixels are identical (W7) |
| §17 open risks | Conforms (P-16…P-18 recorded UNVERIFIED) |
| Amendment closure | Closed for the FN-007/§11 platform, DD-2 trigger, 32-bit wording and T05 insertion/family amendments. **Not closed** for budget figures and dates (W6) |
| Cross-document figures | Consistent: 6 tasks, 14 review rounds, 36.1 MB, 17.1/18.1 MB, 7 libraries, 5/6/7 pages, glibc 2.36, 2 MB cap, 30/50 s, 60 MB. **Contradictions:** `tasks.md:11` "About 650 Java lines" against the final ~2,100 (2,066 measured); 8 parity cases against 3 run |
| Proposal alignment | Intent met on Linux and macOS. Windows NTFS check and 8-case parity not met (W1, W2). The font mechanism changed from aliases to a universal rule, with recorded reasoning. Non-goals respected |

## 9. Test Evidence Summary

| Level | Evidence |
|---|---|
| Unit | Full suite 1,502 / 0 / 0 / 6. PDF package 200 / 0 / 6. New: `PhantomJsBuildTest` (platform matrix), `ShippedPhantomJsBuildsTest` (3 builds' integrity), `PhantomJsLibrariesTest` (14), T05 regressions |
| Gated real render | Linux Tomcat image `OK (8 tests)`; macOS shipped build: fonts `[DejaVuSans, DejaVuSans-Bold]`, 5/6/7 pages |
| Container | Tomcat image: 0/18 pages differ from baseline. bookworm-slim (fontlibs 0): 0/18. amazonlinux:2023: FN-007 WARN, fast failure. Falsifier (block on Linux): 18/18 differ |
| Live | macOS app: OICR / innovation / project → 200 `application/pdf`, DejaVu only, 4 / 6 / 169 pages |
| Not run | Windows (P-16…P-18, OQ-7); 3 real reports + 2 probes of the 8 parity cases; `compare.py` |
| `test-report.md` | Absent (`/akili-test` not run) |

## 10. Agent Guide / Constitution Impact

No `## Constitution Impact` block in `execution.md`. The spec adds one class inside the existing `utils/pdf` package
and creates or reshapes no module, so no child guide or `## Module Guides` entry is needed. No root guide, persona,
template or TRD was edited on this branch.

Pending `staging` items carried from `execution.md`:
- Merge the v2.45.0 persona sections into `.agents/*.md`. Still valid.
- Drop "the macOS PhantomJS note" from `docs/infrastructure.md` §6. That note **does not exist** on any branch (W5).

A CodeGraph re-index is due at archive time.

## 11. Remediation

| # | Severity | Finding | Remediation | Owner |
|---|---|---|---|---|
| W1 | WARN | Windows never run: FN-001, FN-004, FN-005 and NF-002 on Windows are unit-tested or unverified; P-16…P-18 and OQ-7 open | Accept explicitly in the Decision Log ("Windows ships unverified at runtime"), or run T06 (4) on a Windows VM | Owner decision |
| W2 | WARN | NF-001 asks for the 8 parity cases; 3 were run | Re-run the 3 real reports + 2 probes in the Tomcat image with `--network none` (plus `compare.py`), or amend NF-001 to "3 fixtures pixel-identical + Linux HTML byte identity" with a Decision Log entry | Owner decision |
| W4 | WARN | `marlo-test.properties:235-236` comment still says Linux-only, override on Mac | Reword: shipped for Linux x86_64, macOS and Windows; set a path only to override it | Quick fix (comment only) |
| W5 | WARN | Pending `staging` item targets a nonexistent `docs/infrastructure.md` note | Replace it with "§6: no PhantomJS setup needed on any OS" or drop it | Quick fix (spec text) |
| W6 | WARN | `tasks.md:11` "About 650 Java lines"; `Last Updated` 2026-11-24 on three amended documents; `proposal.md` status "Proposed" | Point `tasks.md:11` at design §18 (final ~2,100), bump the dates, set the proposal status | Quick fix (spec text) |
| W7 | WARN | Design drift with no Decision Log entry: §14 byte comparison, §12 "nothing changes", DD-4 missing `LANG` | Amend §14 to "pixel-identical (PDF metadata differs)", §12 to "one extra `--version` at startup", DD-4 to include `LANG`; log the amendment with a closure sweep | Quick fix (spec text) |
| A-2 | advisory (optional) | Test errors instead of skipping on hosts with no build | Move the `Assume` before `extractTo` (one line) | Optional |

## 12. Archive Readiness Recommendation (first pass — superseded by §13)

**Not yet archive-ready.** No FAIL remains, and the shipped behavior is correct wherever it could be checked. The
spec can be archived once:

1. The owner accepts or fixes **W1** (Windows) and **W2** (parity scope), recorded in the Decision Log.
2. **W4–W7** are fixed. They are small edits: one properties comment, plus spec text with a Decision Log entry and a
   closure sweep.
3. The owner reviews this summary.

Then:

```text
/akili-archive changes/portable-pdf-engine
```

## 13. Remediation and Re-check (2026-11-25)

Owner decision: "sí, dale con la opción 1" (fix everything).

| # | Action | Evidence | Result |
|---|---|---|---|
| W1 | Owner acceptance recorded in `requirements.md` §9: Windows ships unverified at runtime; P-16…P-18 and OQ-7 open until the first real Windows install | Decision Log entry 2026-11-25 | **Accepted** |
| W2 | The 5 missing parity cases rendered in `tomcat:9.0-jdk17-temurin`, `--platform linux/amd64 --network none`, baseline `e452378187` (temporary worktree, `package` only, removed) against final `f2c1105ce2`; per-page `pdftoppm -r 72` hash. Inputs are the original CHG-LPRE real payloads and probe files, found intact in an earlier session's scratchpad (`t15/noflag.json`, `fonts.json`, `realdata/*-real.json`). The app was not run, there was no dump line, and no credentials were used | OICR 3508: 5/5 pages, 0 differ · Innovation 1558: 6/6, 0 · Cluster 102076: 481/481, 0 · missing-flag probe: 1/1, 0 · font probe: 1/1, 0. All `outcome=ok blockedUris=0 failedFetches=0`; final startup `libraries=host`. Falsifier (page fonts forced on Linux): OICR 5/5, innovation 6/6 and both probes differ; the cluster times out at 50 s. Leader re-ran the comparison and got the same counts. Raw bytes differ even baseline against baseline (cluster 21,366,979 vs 20,682,107 on two baseline runs), so byte comparison is meaningless, as W7 says | **PASS** — with T06's 3 fixtures, 8 of 8 cases. Parity with production holds by transitivity: CHG-LPRE measured PARITY at the baseline engine |
| W3 | Full suite with `-am` | `Tests run: 1502, Failures: 0, Errors: 0, Skipped: 6` (§5) | **PASS** |
| W4 | `marlo-test.properties:235-236` comment reworded: optional override; shipped for Linux x86_64, macOS or Windows; key still empty, LF kept | Reviewer (checklist) PASS: agrees with FN-001, FN-006 and the `APConfig` Javadoc | **Fixed** |
| W5 | `tasks.md` §6: the nonexistent `docs/infrastructure.md` item replaced by "none" | Decision Log 2026-11-25 + closure sweep | **Fixed** |
| W6 | `tasks.md` header now gives LOC as planned ~650 / delivered ~2,066; `Last Updated` set to 2026-11-25 on requirements, design and tasks; `proposal.md` status set | same entry | **Fixed** |
| W7 | `design.md` §14 changed to pixel-identical (metadata differs), §12 to one extra startup `--version`, DD-4 Windows list gains `LANG` | same entry; sweep: the only remaining "byte-identical" is T05's HTML claim, which is true | **Fixed** |
| A-2 | `EmbeddedPhantomJsTest.theRealResourceExtractsToTheKnownBuild`: `Assume` moved before extraction | Reviewer PASS; Checkstyle delta 0; hygiene 0 FIX; clean compile 2430/1073/155; `Tests run: 30, Failures: 0, Errors: 0` (Leader re-run green; one first-run cold-start timeout seen by the Implementer, judged unrelated → joins A-24) | **Fixed** |

**Updated coverage:** NF-001 and the "Linux parity unchanged" clauses move from WARN to **PASS**. The Windows rows stay
WARN or BLOCKED and are **accepted** (W1). No FAIL.

**Archive readiness: ready.** All tasks are `[x]`. No FAIL remains. The only WARN is accepted in the Decision Log.
Tests cover the key scenarios on Linux and macOS. Drift is reflected in the spec. Next step:

```text
/akili-archive changes/portable-pdf-engine
```
