# Proposal — Portable PDF Engine

> **Answer first:** MARLO's local PDF engine only works with no configuration on Linux x86_64 when two system
> libraries are already installed. This change ships what PhantomJS needs for Linux, macOS and Windows inside
> MARLO, so the engine runs on all three with no `pdf.phantomjs.path` and nothing to install. Linux ARM64 stays
> out of reach, because no PhantomJS build exists for it.

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | CHG-PORTABLE-PDF-ENGINE-001 |
| Spec Path | `changes/portable-pdf-engine` |
| Slug | `portable-pdf-engine`, given by the user |
| Type | Change |
| Deliverable | software |
| Approval Mode | gated |
| Status | Approved — owner, 2026-11-24; implemented on `feature/local-pdf-report-engine`, 2026-11-25 |
| Date | 2026-11-24 |
| Owner | Kenji Tanaka |
| Requirement source | Owner request in session, 2026-11-24: *"la idea es que funcione para todos los sistemas operativos sin necesidad de definir rutas ni instalar nada externo"*, then *"vamos con la opción 1"*. No Jira ticket linked yet |
| Builds on | `archive/2026-11-24-changes--local-pdf-report-engine` (CHG-LOCAL-PDF-REPORT-ENGINE-001) |
| Depends on | `archive/2026-11-24-changes--local-pdf-report-engine` (delivered); developed on the same branch, `feature/local-pdf-report-engine` (see §12, OQ-1) |
| Parallel-safe | no — it edits the engine classes delivered by the spec above |

## 2. Intent

The local PDF engine should run wherever MARLO runs: a server, a teammate's Mac or a Windows workstation. It
should need no property and no installed program, because everything it needs ships inside MARLO.

## 3. Problem / Current Behavior

| # | Current behavior | Evidence |
|---|---|---|
| P-1 | The shipped binary is used only on Linux x86_64. Every other platform needs `pdf.phantomjs.path` | `EmbeddedPhantomJs.java:153-158` (`isSupportedPlatform()`: `linux` and `amd64`/`x86_64` only); `PhantomJsRenderer.java:479` |
| P-2 | On a minimal Linux x86_64 the shipped binary cannot start | `docker run --platform linux/amd64 --network none debian:bookworm-slim /pjs/phantomjs --version` → `error while loading shared libraries: libfontconfig.so.1`; `ldd` → `libfontconfig.so.1 => not found`, `libfreetype.so.6 => not found` (run 2026-11-24) |
| P-3 | Besides those two, the Linux binary also links `libexpat`, `libpng16`, `libbz2`, `libbrotlidec`, `libbrotlicommon` and `libz` from the system | `ldd` in `tomcat:9.0-jdk17-temurin` (run 2026-11-24) |
| P-4 | On Linux ARM64 the binary does not run | `docker run --platform linux/arm64 … /pjs/phantomjs --version` → `rosetta error: failed to open elf at /lib64/ld-linux-x86-64.so.2` (run 2026-11-24) |
| P-5 | Extraction is POSIX-only: it sets `rwx------` and names the file `phantomjs` with no `.exe` | `EmbeddedPhantomJs.java:93, 95, 195` (`Files.setPosixFilePermissions`). On Windows that call throws `UnsupportedOperationException` — `UNVERIFIED — confirm at source before relying on it` (no Windows host was available) |
| P-6 | The bundled DejaVu fonts reach PhantomJS only through fontconfig (`FONTCONFIG_FILE`), which PhantomJS reads only on Linux. On macOS the PDFs come out in Helvetica, not DejaVu | `PhantomJsRenderer.java:609`; live run 2026-11-24, `pdffonts oicr.pdf` → `Helvetica`, `Helvetica-Bold`, `Helvetica-Light`, `Helvetica-Oblique` |
| P-7 | Because of P-6, a macOS render is not the same as production: the real cluster 102076 gives 444 pages on macOS against 482 on Linux | `archive/…local-pdf-report-engine/execution.md`, T09 live run (482) and T15 Mac run (444); 444 again live on 2026-11-24 |
| P-8 | Some engine tests are gated to Linux and are skipped elsewhere | `PhantomJsRenderingTest.java:89, 102, 327` (`isLinux()`, `isLinuxX64()`) |

## 4. Proposed Outcome

- MARLO picks the right shipped PhantomJS build for the OS it runs on, extracts it, verifies its SHA-256 and runs
  it. This works on Linux x86_64, macOS and Windows x86_64.
- A minimal Linux x86_64 with no `libfontconfig` or `libfreetype` renders PDFs with no package installed.
- The PDFs use the bundled DejaVu fonts on every OS, so a Mac or Windows render matches the Linux one.
- `pdf.phantomjs.path` remains as an optional override, for example for a server whose temp dir is `noexec`. No
  environment needs it.
- On a platform with no build (Linux ARM64), the engine fails fast with one clear log line and the existing
  generic 500, as it does today.

## 5. Scope

| In | Detail |
|---|---|
| Binaries | Official PhantomJS 2.1.1 builds from the same source as today (`github.com/Medium/phantomjs` releases): `macosx` (17.1 MB zip) and `windows` (18.2 MB zip), each with its SHA-256 and provenance file, next to the existing Linux one |
| Linux libraries | Ship the shared libraries from P-2 and P-3 for Linux x86_64 and point the process at them (for example `LD_LIBRARY_PATH` in the existing minimal environment). Licences go next to them |
| Platform selection | Replace the Linux-only check with one platform table: OS + arch → resource, checksum, file name |
| Windows extraction | No POSIX calls on Windows, the file named `phantomjs.exe`, and directory locking and the stale-directory sweep checked on NTFS |
| Fonts | Load DejaVu into the page with `@font-face` (data URIs, which `render-page.js:74` already allows for `font/*`). Aliases cover the family names the templates use, so no OS font is needed |
| Tests | Each platform's selection and extraction unit-tested on any host. The real render gated test runs on each OS that is available; the Linux run also covers a minimal image |
| Docs | Provenance and licence files per binary and library; update the `pdf.phantomjs.path` description (override only); `docs/infrastructure.md` note for OQ-4 |

## 6. Non-Goals

- Linux ARM64 and native Apple Silicon: no PhantomJS build exists for them. Apple Silicon runs the macOS x86_64
  build through Rosetta.
- Replacing PhantomJS with another engine. OpenHTMLtoPDF was rejected in Pivot 2 because it did not match
  production.
- Retiring the reports microservice: that is still `changes/retire-reports-microservice` (T08 of the previous spec).
- Changing the report templates or the payloads.
- Windows on ARM and 32-bit systems.

## 7. Affected Users, Systems, And Specs

| Affected | How |
|---|---|
| Developers (macOS, Windows) | Generate and check reports locally with no setup |
| IBD operations / infrastructure | No packages to install on the servers; OQ-4 shrinks to "x86_64, and an exec-allowed temp dir" |
| PMU and report users | No visible change |
| Code | `marlo-web/.../utils/pdf/EmbeddedPhantomJs`, `PhantomJsRenderer`, `LocalPdfReportService`; `pdf/phantomjs/` resources; possibly `render-page.js`; `APConfig` (description only) |
| Build | The WAR grows by about 35 MB for the two binaries, plus the Linux libraries (size to measure) |
| Specs | Extends `archive/2026-11-24-changes--local-pdf-report-engine` (NF-007, design §18.4) |

## 8. Visual Reference

- Source: None
- Location: n/a
- Notes: no UI change. The reports must look the same as today; the visual check is the existing parity
  comparator (`spike/parity/` in the archived spec), run per OS.

## 9. Requirement Delta Preview

### ADDED Requirements

- The engine MUST select a shipped PhantomJS build for Linux x86_64, macOS (x86_64 build, also used on Apple
  Silicon) and Windows x86_64 with no configuration.
- On Linux x86_64 the engine MUST render with no `libfontconfig` or `libfreetype` installed on the host.
- Every shipped binary and library MUST be checked against its recorded SHA-256 before it is run.
- Renders MUST use only the bundled DejaVu fonts on every OS.

### MODIFIED Requirements

- CHG-LPRE-NF-007 (the binary ships inside MARLO): from "Linux x86_64 only" to "Linux x86_64, macOS and Windows".
- `pdf.phantomjs.path`: from "required outside Linux x86_64" to "optional override, never needed".
- NF-001 parity: measured on Linux as today, and also checked on macOS and Windows renders.

### REMOVED Requirements

- The requirement that non-Linux platforms configure `pdf.phantomjs.path`.

## 10. Approach Options

| Option | What | Pros | Cons |
|---|---|---|---|
| **A — Portable PhantomJS** (chosen by the owner) | Ship the three official builds, the Linux libraries and in-page fonts | Same engine as production, so parity holds; no setup on any supported OS | About 35 MB more in the WAR; no Linux ARM64; Rosetta on Apple Silicon; more third-party files to keep licences for |
| B — Pure-Java engine (OpenHTMLtoPDF) | Render in the JVM | Runs anywhere Java runs, ARM64 included | Not identical to production; rejected in Pivot 2 of the previous spec |
| C — Hybrid | PhantomJS where a build exists, Java engine elsewhere | Covers every platform | Two engines to maintain; non-identical output on ARM |

## 11. Recommended Approach

**Option A**, as the owner chose on 2026-11-24. It is the smallest change that meets the intent and keeps
parity with production. It also reuses the existing mechanism (resource → extract → SHA-256 → run). Do it in
this order, each step falsifiable on its own:

1. **Spike (go/no-go)**, before `/akili-specify` freezes the design:
   - Linux minimal image with the shipped libraries via `LD_LIBRARY_PATH`: `--version` and one real render.
   - `@font-face` data-URI fonts in PhantomJS 2.1.1: a macOS render's fonts are DejaVu, and its page count matches
     Linux on the fixtures.
   - Windows build: extract and render on a Windows host or VM.
2. Platform table and extraction (Windows-safe).
3. Shipping the libraries and fonts.
4. Per-OS parity runs and gated tests.

## 12. Risks, Dependencies, And Open Questions

| # | Item | Note |
|---|---|---|
| R-1 | Shipped Linux libraries may clash with the host's glibc on an older or newer distribution | Ship only the libraries `ldd` lists beyond glibc and libstdc++; test on the oldest image MARLO deploys to. Spike item 1 |
| R-2 | PhantomJS 2.1.1 (QtWebKit) may not apply `@font-face` data-URI fonts the way fontconfig does: metrics, bold, fallbacks | If it fails, the fallback is the macOS/Windows render being "close, not identical". Spike item 2 decides |
| R-3 | The WAR grows by about 35 MB or more, and so does every deploy artifact | Owner to accept the size. Lazy per-OS extraction keeps runtime disk use to one build |
| R-4 | More unmaintained third-party binaries ship with MARLO (PhantomJS is unmaintained since 2018; design §18.3's accepted risk) | Same sanitizer, allowlist and `--local-url-access=false` on every OS; provenance per file |
| R-5 | Apple Silicon needs Rosetta, which macOS installs on first use with one prompt | Documented; it is part of the OS, not MARLO |
| R-6 | Linux ARM64 has no build | The engine fails fast as today; OQ-2 decides whether that matters |
| R-7 | KZ-…-directory-abstraction-1: enumerations scoped to what a reviewer already named | The Linux-only assumptions must be enumerated by search across `utils/pdf` and its tests (P-1, P-5, P-6, P-8 are the known ones), not taken from this table |
| R-8 | KZ-…-auth-flow-2: test doubles must assert the arguments they receive | The platform-selection tests must assert which resource, checksum and file name the selector actually used |
| OQ-1 | Base branch: `feature/local-pdf-report-engine` is not merged to `staging` yet | **Decided 2026-11-24 by the owner:** nothing goes to `staging` until all the development is ready. This change is developed **on the same branch**, `feature/local-pdf-report-engine` (owner: a small change on top of work that is nearly all done), and the branch reaches `staging` once, with both specs |
| OQ-2 | Infrastructure: does any MARLO Tomcat run on ARM64 (Graviton)? | If yes, Option A does not cover it and Option C comes back |
| OQ-3 | Which Windows host or VM can run the Windows render test? | Without one, Windows support is unit-tested only and marked unverified |
| OQ-4 | Jira: is there an A2 ticket for this, and under which epic? | None linked yet |

## 13. Success Criteria

- With no `pdf.phantomjs.path` set:
  - On `debian:bookworm-slim` (no fontconfig) the OICR, Innovation and Cluster fixtures render.
  - On macOS the same fixtures render with only DejaVu fonts embedded (`pdffonts`).
  - On Windows the same fixtures render, or this is recorded as unverified with OQ-3 open.
- Linux parity stays at PARITY with 0.000 % pixel diff on the existing 8 cases. A macOS render of the fixtures
  has the same page count as Linux.
- A tampered binary or library is refused (SHA-256) on each OS.
- Linux ARM64 gives one clear log line and the generic 500, with no hang.
- Clean recompile, Checkstyle delta 0, and the suite green with `-am`.

## 14. Next Step

```text
/akili-specify changes/portable-pdf-engine
```

Run spike item 1 (Linux libraries) and spike item 2 (fonts) at the start of specify, before the design is frozen.
