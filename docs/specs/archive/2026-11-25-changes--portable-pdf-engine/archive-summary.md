# Archive Summary — Portable PDF Engine

> **Delivered.** MARLO's local PDF engine now runs on **Linux x86_64, macOS and Windows** with no configuration. The
> PhantomJS 2.1.1 build for each platform ships in the WAR. On a minimal Linux without font libraries, MARLO falls
> back to the libraries it ships. On macOS and Windows the DejaVu fonts are added to the page. On every platform
> checked, the reports contain only DejaVu.
>
> **Evidence:** validation PASS, tests PASS (1,511 tests, 0 failures), Linux parity on 8 of 8 cases, and browser
> E2E on the macOS app and on the MARLO Docker image. **Not covered:** Windows ships unverified at runtime, as
> accepted by the owner.

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | CHG-PORTABLE-PDF-ENGINE-001 |
| Owner | Kenji Tanaka (IBD) |
| Branch | `feature/local-pdf-report-engine` (spec branch; apply-capable branch: `staging`) |
| Commits | `e452378187` (proposal) … `52e6996062` (E2E record), 11 commits; code baseline `e452378187` |
| Approval Mode | gated |
| Archived by | `/akili-archive`, Leader session (opus) |

## 2. Original Spec Path

`docs/specs/changes/portable-pdf-engine/`

## 3. Archive Date

2026-11-25 → `docs/specs/archive/2026-11-25-changes--portable-pdf-engine/`

## 4. Final Status

| Gate | Result |
|---|---|
| Tasks | 6 / 6 `[x]`, each with a closing record (Reviewer PASS) |
| Validation | **PASS, archive-ready.** 0 FAIL; 7 WARN → W1 accepted, W2–W7 fixed (`validation-report.md` §13) |
| Tests | **PASS.** 1,511 / 0 / 0 / 6; 9 gap tests, each shown red before it passed; 2 browser E2E journeys (`test-report.md`) |
| Budget | 6 tasks · ~2,066 Java LOC against a final ~2,100 (planned ~650, re-baselined 4×) · 14 review rounds against 14 (planned 9) · WAR +36.1 MB against a 60 MB cap |

## 5. Requirements Delivered

| Requirement | Delivered as | Status |
|---|---|---|
| FN-001 build per platform, nothing to configure | `PhantomJsBuild` table (OS + arch together); macOS and Windows builds in the WAR | ✅ Linux, macOS · Windows unit-tested only |
| FN-002 minimal Linux renders | startup probe; shipped Debian 12 libraries as a fallback (glibc ≥ 2.36) | ✅ |
| FN-003 libraries visible only to PhantomJS | `LD_LIBRARY_PATH` set only in the child process environment | ✅ |
| FN-004 Windows extraction | `phantomjs.exe`; POSIX calls guarded; owner normaliser for the sweep | ✅ unit · NTFS lock and sweep not run |
| FN-005 only DejaVu | Linux keeps fontconfig; on macOS/Windows a page-font block after the sanitizer, at document start, random family name | ✅ Linux, macOS · Windows open (OQ-7) |
| FN-006 override optional | `pdf.phantomjs.path` still wins and is never needed | ✅ |
| FN-007 no build / old glibc | generic 500, one WARN naming the platform, no hang, no microservice | ✅ |
| NF-001 … NF-006 | parity 8/8; macOS page counts; only the current platform extracted; +36.1 MB; no Maven dependency; first render 1.6–6.9 s | ✅ (NF-002 Windows open) |
| SEC-001 … SEC-003 | SHA-256 before run or load; CHG-LPRE protections unchanged; fonts after the sanitizer, allowlist unchanged | ✅ |
| OPS-001, OPS-002 | provenance and licences for 3 builds and 7 libraries; startup line names platform, build, version, library mode | ✅ |

## 6. Files Changed Summary

| Area | Files |
|---|---|
| Main code | `utils/pdf/PhantomJsBuild.java` (new), `EmbeddedPhantomJs.java`, `PhantomJsRenderer.java`, `LocalPdfReportService.java`; `marlo-utils/.../APConfig.java` (Javadoc only) |
| Resources | `pdf/phantomjs/phantomjs-2.1.1-macosx.gz` and `-windows.exe.gz` with their `.sha256` and `PROVENANCE-*.txt`; `pdf/phantomjs/linux-libs/` (7 libraries, `SHA256SUMS`, `PROVENANCE.txt`, 6 `copyright.*`) |
| Config | `config/marlo-test.properties` (comment for `pdf.phantomjs.path`) |
| Tests | new `PhantomJsBuildTest`, `ShippedPhantomJsBuildsTest`, `PhantomJsLibrariesTest`, `PhantomJsResourceAccessTest`, `IsolatedPdfClassLoader`; modified `EmbeddedPhantomJsTest`, `PhantomJsRendererTest`, `PhantomJsRenderingTest`, `LocalPdfReportServiceTest`; `fake-phantomjs.sh` |
| Size | Java ~760 main + ~1,300 tests; WAR +36.1 MB |

No migration, no specificity change, no UI change, no `pom.xml` change.

## 7. Test Evidence Summary

| Level | Evidence |
|---|---|
| Unit | 1,511 / 0 / 0 / 6 with `-am`; PDF package 209 / 0 / 6 |
| Gated real render | Linux Tomcat image `OK (8 tests)`; macOS shipped build: DejaVu only, 5/6/7 pages |
| Containers | Tomcat image and bookworm-slim (no font libraries): 0 differing pages against `e452378187`; amazonlinux:2023: FN-007 WARN, fast failure |
| Parity | 8/8 CHG-LPRE cases pixel-identical to the baseline engine (3 fixtures, 3 real reports incl. a 481-page cluster, 2 probes) |
| Browser E2E | macOS app and MARLO Docker image (linux/amd64): 6/6 downloads by UI clicks → `200 application/pdf`, DejaVu only; a decoy binary on `PATH` and in `PHANTOMJS_PATH` was never run |

## 8. Validation Summary

- **First pass:** 0 FAIL, 7 WARN.
- **W1 (Windows unverified):** accepted by the owner in the Decision Log.
- **W2 (parity evidence):** closed by running the 5 missing parity cases.
- **W4–W7 (documentation and traceability):** fixed with a closure sweep.
- **Final:** PASS, archive-ready.
- **Advisories:** 26, recorded in `validation-report.md` §7. None gates.

## 9. Accepted Warnings Or Follow-Ups

| Item | Owner / where |
|---|---|
| Windows at runtime: P-16, P-17, P-18, OQ-7 open until the first real Windows install (for example icipe) | Owner; `requirements.md` §9 2026-11-25 |
| Merge the v2.45.0 persona sections (checkpoint bound, bounded reads) into MARLO's adapted `.agents/*.md`. The T06 Implementer ran 103 tool calls with no checkpoint | `staging`; kaizen entry pending item |
| The pending CHG-LPRE TRD decision (`kaizen/changes--local-pdf-report-engine.md` P7) must also say the engine ships for Linux x86_64, macOS and Windows | `staging`; kaizen entry pending item |
| Advisory follow-ups worth a future spec (not tasks): `java.io.tmpdir` containing `:`, `;` or `$`; a probe that cannot be judged fixes the mode for the life of the JVM; a BOM or comment before `<!DOCTYPE`; the pre-existing `FakePhantomJs` pid-file flake | `validation-report.md` §7 |
| CodeGraph re-index after the merge (new `PhantomJsBuild`, new tests) | Developer |

## 10. Historical Notes

- **Mid-spec amendments.** Four rounds of execute-time spec amendments, each owner-approved and logged with a
  closure sweep:
  - FN-007 and §11: the WARN names the platform.
  - DD-2: the fallback trigger text.
  - T05: font block insertion at document start and a random family name, prompted by a latent sanitizer bypass
    that both attempt-1 Reviewers raised as advisories.
  - Validation: W5–W7.
- **Budget tripwire fired three times** (T03, T04 resume, T05 close). Tests made up most of the overrun (≈1.7 test
  lines per main line).
- **Real CHG-LPRE inputs reused.** The CHG-LPRE real payloads and probes survived in an earlier session's scratchpad
  and were reused for parity. They were never committed.
- **E2E incidents.** A "Submit cluster" dialog was opened by mistake and cancelled; nothing was written. A local
  `mysql:8.4` tag was overwritten and then restored.

## 11. Diagrams

`design.md` holds one inline Mermaid diagram (the platform selection and render flow). The spec has no `diagrams/`
folder and no interactive view. No baseline diagram slot in `docs/trd/trd.md` was made stale: the TRD does not yet
describe the local PDF engine, which is the pending CHG-LPRE `trd-adr` item.
