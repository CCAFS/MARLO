# Portable PDF Engine — Requirements

**Spec ID:** CHG-PORTABLE-PDF-ENGINE-001
**Status:** Approved — owner, 2026-11-24 (Phase 1, 2 and 3 gates)
**Owner:** Kenji Tanaka (IBD)
**Reviewers:** owner
**Last Updated:** 2026-11-24
**Related PRD sections:** docs/prd.md — reporting outputs (summaries)
**Related UX/UI Design sections:** not applicable (no screen changes)
**Related TRD sections:** docs/trd/trd.md §2 (`action/summaries`), §9 Observability
**Related Infrastructure sections:** docs/infrastructure.md §6 (local stack: no PhantomJS setup needed after this change)
**Companion ai-context docs:** not applicable (no routing, validation, replication or composition contract changes)
**Proposal:** `proposal.md` · **Extends:** `archive/2026-11-24-changes--local-pdf-report-engine` (prefix `CHG-LPRE`)

| Field | Value |
|---|---|
| Type | Change |
| Deliverable | software |
| AI Agent | no — deterministic HTML-to-PDF rendering, no model decides anything at runtime |
| Depth | **Standard** — no data, migration, auth or API change; third-party binaries and their verification are in scope |
| Approval Mode | gated (inherited from `proposal.md`) |
| Branch | `feature/local-pdf-report-engine` — same branch as the extended spec; nothing reaches `staging` until both are done (proposal OQ-1) |

---

## 1. Overview

The local PDF engine (CHG-LPRE) renders the OICR, Innovation and Cluster summary PDFs inside MARLO with
PhantomJS 2.1.1. Today it runs with no configuration on one platform only. This spec makes it run, with no property
and nothing installed, on every platform a MARLO server or developer machine uses: Linux x86_64 (including a
minimal image), macOS and Windows x86_64. The PDFs must still match production.

## 2. Problem Statement

What MARLO does today:

- The shipped binary is used on Linux x86_64 only (`EmbeddedPhantomJs.java:153-158`). Everywhere else a developer has
  to download PhantomJS and set `pdf.phantomjs.path`.
- Even on Linux x86_64, the binary needs `libfontconfig` and `libfreetype` installed on the host. In
  `debian:bookworm-slim` it fails with `libfontconfig.so.1: cannot open shared object file` (proposal P-2, run
  2026-11-24).
- Extraction uses POSIX permission calls (`EmbeddedPhantomJs.java:195`), which Windows does not support.
  `UNVERIFIED — confirm at source before relying on it` (no Windows host was available).
- The bundled DejaVu fonts reach PhantomJS only through fontconfig (`PhantomJsRenderer.java:609`). On macOS the PDFs
  come out in Helvetica and paginate differently: 444 pages against 482 on Linux for cluster 102076 (proposal P-6, P-7).

The owner's goal (2026-11-24): *"que funcione para todos los sistemas operativos sin necesidad de definir rutas ni
instalar nada externo"*.

## 3. In-Scope Requirements

### Functional

- **CHG-PPE-FN-001** — With `pdf.phantomjs.path` empty, MARLO MUST select, extract and run a PhantomJS 2.1.1 build it
  ships for the platform it runs on, for each of: Linux x86_64, macOS (x86_64 build; on Apple Silicon it runs through
  Rosetta) and Windows x86_64. No property, environment variable or installed program MAY be required on those
  platforms.
- **CHG-PPE-FN-002** — On Linux x86_64 a render MUST succeed on a host that does not have `libfontconfig`,
  `libfreetype` or their dependencies installed, provided the host's glibc is 2.36 or later. On a host where the
  shipped build already starts with the host's own libraries, those MUST keep being used as today. *Amended
  2026-11-24 (spike S-2): the shipped libraries need glibc ≥ 2.36 (`libexpat` from Debian 12), so they are a fallback,
  not a replacement.*
- **CHG-PPE-FN-003** — Shipped shared libraries MUST be visible only to the PhantomJS process that MARLO starts. They
  MUST NOT be loaded into the JVM and MUST NOT change the environment of any other process.
- **CHG-PPE-FN-004** — Extraction and execution MUST work on Windows. That includes the executable's name, its
  permissions, the per-directory lock and the sweep of stale support directories (CHG-LPRE SEC-004).
- **CHG-PPE-FN-005** — On every supported platform the PDF MUST embed only the DejaVu fonts MARLO ships. No font
  installed on the host MAY appear in the output. The PDF's text MUST stay extractable (selectable and searchable)
  on every platform. *Amended 2026-11-24 (spike S-3): fonts placed in the page make Linux draw text as paths, so Linux
  keeps reaching DejaVu through fontconfig as today.*
- **CHG-PPE-FN-006** — A non-empty `pdf.phantomjs.path` MUST still override the shipped build on every platform, as
  in CHG-LPRE NF-007. The override MUST remain optional, and no supported platform MAY need it.
- **CHG-PPE-FN-007** — On a platform with no shipped build (for example Linux ARM64, Windows ARM64, or a 32-bit
  Linux or macOS), or on a Linux x86_64 host where neither the host's libraries nor the shipped ones let PhantomJS start (no
  font libraries and glibc older than 2.36), with the flag on and no override, a render MUST fail fast with the generic 500 (CHG-LPRE SEC-002). Startup
  MUST log one line that names the platform. The render MUST NOT hang and MUST NOT fall back silently to the
  microservice.

### Non-Functional

- **CHG-PPE-NF-001** — Linux parity MUST stay as measured in CHG-LPRE: PARITY and a 0.000 % pixel difference on the
  8 parity cases.
- **CHG-PPE-NF-002** — On macOS and Windows the three fixtures MUST give the same page count as on Linux, use only
  DejaVu fonts and keep their text extractable. Their layout SHOULD be close to Linux but is not required to be
  identical; Linux stays the parity reference. *Amended 2026-11-24 (spike S-3): with DejaVu, macOS matches Linux in
  fonts and page counts, but lines sit about 4 pt higher and some words move to the next page, because each OS lays
  text out with its own font engine.*
- **CHG-PPE-NF-003** — At runtime MARLO MUST extract only the current platform's build and libraries. It MUST NOT
  download anything.
- **CHG-PPE-NF-004** — The growth of the `marlo-web` WAR MUST be measured and recorded. It MUST NOT exceed 60 MB
  over the CHG-LPRE WAR without the owner's approval.
- **CHG-PPE-NF-005** — No Maven dependency MAY be added or downgraded for this change (Hard rule 11).
- **CHG-PPE-NF-006** — The time to the first render MUST stay within the CHG-LPRE deadlines: 30 s for a submission
  and 50 s for a download. That includes extracting the build and libraries.

### Security

- **CHG-PPE-SEC-001** — Every shipped executable and shared library MUST be checked against a recorded SHA-256 before
  it is run or loaded. A file that does not match MUST NOT be used, and the render MUST fail with the generic 500.
- **CHG-PPE-SEC-002** — The CHG-LPRE protections MUST apply unchanged on every platform: the sanitizer, the request
  allowlist, `--local-url-access=false`, the per-render temporary directory and the deadline kill.
- **CHG-PPE-SEC-003** — The fonts MARLO places in the page MUST be added after the sanitizer, as the bundled images
  are (CHG-LPRE T17), and MUST win over every font the template or payload declares without `!important`. A font
  declared with `!important` (for example an inline style in rich text that a `{{{ }}}` placeholder passes through)
  MAY win; this is an accepted gap, since no production template declares one. The request allowlist MUST NOT be
  widened for them. *Amended 2026-11-24 (spike S-1): the production templates themselves carry a `Montserrat` woff2
  data URI, which PhantomJS 2.1.1 cannot use; refusing template font data URIs would change Linux behavior for no
  gain.*

### Operations

- **CHG-PPE-OPS-001** — Each shipped binary and library MUST have a provenance file (upstream URL, upstream SHA-256,
  stored SHA-256, retrieval date) and its licence texts next to it.
- **CHG-PPE-OPS-002** — The startup log line MUST name the platform, the chosen build and the PhantomJS version, as it
  does today for Linux.

## 4. Out-of-Scope

- A native build for Linux ARM64 or Apple Silicon: none exists for PhantomJS 2.1.1.
- Replacing PhantomJS with another engine.
- Retiring the reports microservice (`changes/retire-reports-microservice`).
- Template or payload changes; the flag, the migration and the call sites from CHG-LPRE.
- Installing Rosetta. On Apple Silicon it is part of macOS, and macOS offers it on first use.

## 5. Personas Affected

| Persona | Effect |
|---|---|
| IBD developers (macOS, Windows) | Generate the summary PDFs locally with no setup |
| IBD operations / infrastructure | No packages to install on servers; only x86_64 and an exec-allowed temp dir remain to confirm |
| PMU, cluster coordinators, report readers | None visible — same PDFs |

## 6. Acceptance Criteria

### Requirement: Shipped build per platform (FN-001, FN-006, FN-007, OPS-002)

#### Scenario: Supported platform, no configuration
- GIVEN `pdf.phantomjs.path` is empty and the flag is on for the unit
- WHEN MARLO starts on Linux x86_64, macOS (Intel or Apple Silicon) or Windows x86_64, and a summary PDF is downloaded
- THEN the render uses the shipped build for that platform and the download is `200 application/pdf`
- AND the startup log names the platform, the build and `version=2.1.1`
- BUT it must NOT read any property or environment variable to find the binary
- AND IT MUST pick the build from the OS and the architecture together (an ARM64 Linux never gets the x86_64 build)

#### Scenario: Override set
- GIVEN `pdf.phantomjs.path` names an executable
- WHEN a render runs on any platform
- THEN that executable is used and the shipped build is not extracted

#### Scenario: Platform with no build
- GIVEN Linux ARM64 (or Windows ARM64, or a 32-bit Linux or macOS), no override, flag on
- WHEN a summary PDF is downloaded
- THEN the answer is the generic 500 within the deadline
- AND startup logged one line naming the platform as unsupported
- BUT it must NOT call the microservice and must NOT hang

### Requirement: Minimal Linux (FN-002, FN-003)

#### Scenario: No font libraries installed
- GIVEN a Linux x86_64 host with glibc ≥ 2.36 and without `libfontconfig` and `libfreetype` (for example
  `debian:bookworm-slim`), with networking disabled
- WHEN the three fixtures are rendered
- THEN each render ends `outcome=ok`
- AND the PDFs are pixel-identical to the same fixtures rendered in the MARLO Tomcat image
- AND IT MUST NOT change the library path of the JVM or of any process other than the PhantomJS it starts

#### Scenario: Host libraries present
- GIVEN a Linux x86_64 host where the shipped build starts with the host's own libraries (the MARLO Tomcat image)
- WHEN a render runs
- THEN the shipped libraries are not used
- BUT it must NOT behave differently from CHG-LPRE on that host

#### Scenario: Old glibc and no font libraries
- GIVEN a Linux x86_64 host with glibc < 2.36 and no font libraries (for example `amazonlinux:2023`)
- WHEN a summary PDF is downloaded with the flag on
- THEN the outcome is the FN-007 one: the generic 500 and one startup line that says why

### Requirement: Windows extraction (FN-004)

#### Scenario: Windows host
- GIVEN Windows x86_64, no override
- WHEN the first render runs
- THEN the build is extracted as an executable Windows can run, verified, locked and used
- AND a later startup sweeps stale support directories as on Linux
- BUT it must NOT call any POSIX-only file API on Windows

### Requirement: Same fonts everywhere (FN-005, NF-001, NF-002)

#### Scenario: Fonts on macOS and Windows
- GIVEN the three fixtures
- WHEN they are rendered on macOS (and on Windows when a host is available)
- THEN `pdffonts` lists only DejaVu faces
- AND the page counts equal the Linux page counts
- AND IT MUST keep the text extractable (`pdftotext` returns the words of every page)
- BUT it must NOT list Helvetica, Arial or any other host font

#### Scenario: Linux parity unchanged
- GIVEN the 8 CHG-LPRE parity cases on Linux x86_64 with networking disabled
- WHEN they are rendered after this change
- THEN the result is PARITY with a 0.000 % pixel difference, as before

### Requirement: Verification and isolation (SEC-001, SEC-002, SEC-003)

#### Scenario: Tampered file
- GIVEN an extracted executable or library whose bytes differ from its recorded SHA-256
- WHEN a render needs it
- THEN it is re-extracted from the WAR or refused, and the render never runs the altered file
- AND IT MUST refuse with the generic 500 when the resource in the WAR itself does not match

#### Scenario: Template or payload declares its own font
- GIVEN a template or payload whose HTML declares its own `@font-face` and `font-family` (as the production templates
  do with `Montserrat`)
- WHEN it is rendered on macOS or Windows
- THEN the PDF still embeds only DejaVu
- AND the request allowlist is the CHG-LPRE one, unchanged
- BUT the fonts MARLO places must NOT go through the sanitizer's input (they are added after it)

### Defect classes and their gates

| Defect class this spec can produce | Gate that catches it |
|---|---|
| Java that does not compile | Clean recompile (`marlo-verify` Gate 1) |
| Wrong build chosen for an OS/arch pair | Unit tests over the full platform table, asserting the resource, checksum and file name the selector used (KZ-…-auth-flow-2) |
| Shipped Linux libraries missing or incompatible with the host glibc | Real render in `debian:bookworm-slim` and in the MARLO Tomcat image, `--network none` |
| Libraries leaking into the JVM or other processes | Unit test on the child-process environment; the JVM's own `java.library.path` unchanged |
| POSIX-only call reached on Windows | Unit tests with a non-POSIX file-system double; real render on a Windows host when one is available |
| Host font used instead of DejaVu | `pdffonts` on macOS renders of the fixtures (and Windows when available) |
| Text drawn as paths (not extractable) | `pdftotext` word count per page, every platform rendered |
| Shipped libraries used where host ones worked (behavior drift on the production image) | Unit test on the start-up probe order; Linux parity run in the Tomcat image |
| Layout or pagination regression | Linux parity comparator, 8 cases, `--network none`; page counts per OS |
| Tampered binary or library run | Unit tests that alter bytes after extraction and in the resource |
| Template font winning over DejaVu on macOS/Windows | `pdffonts` on the production templates (they declare `Montserrat`) |
| Checkstyle violation | Checkstyle delta against HEAD (`marlo-verify` Gate 2) |
| WAR size over budget | `ls -l` of the built WAR compared with the CHG-LPRE WAR |

**Gap:** Windows has no automated check unless a Windows host or VM is available (OQ-2). Without one, Windows is
covered by unit tests only and is recorded as **not verified at runtime**.

**Accepted risk:** Apple Silicon depends on Rosetta, which is part of macOS, not MARLO.

## 7. Constitutional Compliance Checklist

- [x] Phase replication: not applicable — nothing is saved or deleted.
- [x] Save validation: not applicable.
- [x] Permissions: no new action; the summary actions keep their interceptor stacks.
- [x] Specificity: unchanged — `local_pdf_report_engine_active` from CHG-LPRE.
- [x] Migrations: none.
- [x] i18n: no user-facing strings.
- [ ] License header: every new Java file carries the GPL header (checked at execute).
- [ ] Code style: Checkstyle delta 0 on changed files (checked at execute).
- [x] REST: not applicable.
- [x] Audit: not applicable.
- [x] Dependency floors preserved; no Maven dependency added (NF-005).
- [x] Branching: `feature/local-pdf-report-engine` (from `staging`), target `staging`; one merge with CHG-LPRE.

## 8. Open Questions

| # | Question | Owner | Blocks |
|---|---|---|---|
| OQ-1 | ~~Does any MARLO Tomcat run on ARM64 (Graviton)?~~ **Answered for dev, staging and prod:** the deployed image is built for x86_64 and run as is (Decision Log, scope entry). Still open for future installs (for example icipe) | Infrastructure / icipe | Only a future ARM64 install, which gets FN-007 |
| OQ-2 | Which Windows host or VM can run the real render? | Owner | Runtime verification of FN-004 and NF-002 on Windows |
| OQ-3 | Is there an A2 Jira ticket, and under which epic? | Owner | Commit references only |
| OQ-4 | ~~Do `@font-face` data-URI fonts in PhantomJS 2.1.1 give the same metrics as fontconfig?~~ **Answered (spike S-3):** on macOS, same fonts and page counts, not the same layout; on Linux they turn text into paths | — | — |
| OQ-5 | ~~Do the shipped Linux libraries run on the oldest image MARLO deploys to?~~ **Answered (spike S-2):** they need glibc ≥ 2.36; pixel-identical output on `debian:bookworm-slim`; fail on `amazonlinux:2023` (2.34) | — | — |
| OQ-6 | Is glibc ≥ 2.36 (or font libraries installed) guaranteed on every MARLO Linux host? The current Tomcat image has both | Infrastructure | Nothing for the current image; a host below 2.36 without font libraries gets FN-007 |
| OQ-7 | Do PhantomJS's Windows fonts placed in the page stay extractable text, as on macOS, or become paths, as on Linux? | Windows run (OQ-2) | FN-005 on Windows |

## 9. Decision Log

- 2026-11-24 — Option A (portable PhantomJS) chosen by the owner — keeps parity with production; options B and C
  in the proposal.
- 2026-11-24 — Developed on `feature/local-pdf-report-engine`; nothing to `staging` until both specs are done — owner.
- 2026-11-24 — Spike amendments (design §Spike): FN-002 (shipped libraries are a fallback, glibc ≥ 2.36), FN-005
  (text stays extractable; Linux keeps fontconfig), FN-007 (adds the old-glibc case), NF-002 (macOS/Windows close,
  not identical) and SEC-003 (fonts added after the sanitizer, allowlist unchanged). Closure sweep, forward: the
  superseded "same text" (NF-002), "refused" font data URIs (SEC-003) and unconditional library use (FN-002) were
  grepped across this folder — every hit is one of the scenario or table rows amended here. `proposal.md` keeps the
  original wording as the record of intent (§4 "matches the Linux one", §13 success criteria). Backward: nothing
  cites these IDs yet.
- 2026-11-24 — Scope kept in full (all six tasks, the Linux library fallback included) — owner, after comparing it
  with a reduced option without T04 (about 200 fewer Java lines, 0.9 MB less). Rationale: icipe or another team may
  later install MARLO on Windows or on a Linux without Docker; production behavior is the same in both options,
  because on the current image the shipped libraries are never used. Evidence for "the servers are Linux x86_64 with
  the font libraries": `marlo-dev-cicd.yml`, `marlo-staging-ci.yml` and `marlo-prod-ci.yml` build the repository
  `Dockerfile` (`FROM tomcat:9.0-jdk17-temurin`, `Dockerfile:14`) on `ubuntu-latest` with no `--platform`, push it to
  ECR, and the server runs it; `dpkg -l` in that image lists `libfontconfig1` and `libfreetype6`. This answers OQ-1
  (ARM64) for the current environments. Owner condition: no environment variable, property or path is set by anyone;
  `LD_LIBRARY_PATH` exists only inside the child environment MARLO builds (FN-001, FN-003).
- 2026-11-24 — SEC-003 narrowed at the tasks phase: DejaVu wins over every font declared without `!important`. Run:
  `grep -ohE 'font[^;{}]{0,60}!important'` over the three test templates and the three production templates →
  0 hits; `grep -c '{{{'` over the test templates → 16 raw placeholders, through which rich text could carry an
  inline `!important` font. Closure sweep: `design.md` §13 SEC-003 bullet and DD-3 checked — neither claimed
  "any font"; the scenario "Template or payload declares its own font" is about the production templates and stays
  true.
- 2026-11-24 — Execute-time amendments after T02 (owner-approved: "aprueba las enmiendas"), from the T02 Reviewer's
  spec-tension advisories: (1) FN-007 and its scenario: "any 32-bit OS" → "a 32-bit Linux or macOS", matching DD-1,
  which maps Windows `x86` to the shipped 32-bit build; (2) OPS-002's "name the platform" clause, owned by no task,
  is assigned to T04 (which already edits the startup line); `design.md` §11 example gains `platform=…`. Closure
  sweep, forward: `grep -nE '32-bit|OPS-002|names the platform|startup line|build='` over this folder — the FN-007
  bullet and its scenario line, T04's requirements line, the coverage row and the §11 example were the hits that
  carried the superseded value; all amended. `tasks.md` T01 (Windows build "32-bit, UPX-compressed PE") and DD-1
  are consistent and unchanged. Backward: T02 is closed and its code already follows DD-1; no other document cites
  these clauses.
- 2026-11-25 — Execute-time amendments at T04 attempt 1 (owner-approved: "sí, aplica ambas enmiendas y lanza el
  intento 2"), from Reviewer A's FAIL and spec-tension advisory: (1) `design.md` §11 — the old-glibc WARN quoted
  without the platform contradicted FN-007 ("Startup MUST log one line that names the platform"); FN-007 kept as
  written, §11 now quotes the WARN with `platform=…` and states that every UNAVAILABLE WARN names the platform;
  (2) DD-2 — the trigger "cannot open shared object file" aligned with `tasks.md` T04 and the code: "error while
  loading shared libraries", which also covers a host library that is present but cannot be loaded. No requirement's
  meaning changes. Closure sweep, forward: `grep -nE 'cannot open shared object|error while loading|glibc older'`
  over this folder — `design.md` §11 and DD-2 were the hits carrying the superseded values (amended); `requirements.md`
  FN-002 rationale and `proposal.md` P-2 quote the loader's observed output and stay true; `tasks.md` T04 already
  says "error while loading shared libraries". Backward: `tasks.md` T04 Verification (2) cites "the FN-007 WARN",
  which now carries the platform — consistent.
- 2026-11-25 — T05 amended after both Reviewers PASSed attempt 1 (owner-approved: "sí, opción 1, enmienda T05 y lanza
  el intento"), from two RISK advisories that showed `tasks.md` T05 Scope in tension with SEC-002 (the sanitizer
  applies unchanged) and SEC-003 (DejaVu wins over every font declared without `!important`): (1) insertion "before
  `</head>`" by text search could land inside a head `<style>` or attribute the sanitizer keeps as text and turn it
  into markup → the block now goes at the start of the document, after an optional leading DOCTYPE; (2) the fixed
  family `MarloDejaVu` let a payload `@font-face` replace DejaVu → the family name is random per JVM. No requirement's
  meaning changes; both make the code meet SEC-002/SEC-003 as written. Review budget raised to 13 rounds (`design.md`
  §18). Closure sweep, forward: `grep -nE '</head>|universal|MarloDejaVu'` over this folder — `tasks.md` T05 Scope and
  `design.md` DD-3 and §13 carried the superseded insertion point (amended); `design.md` §Spike S-3 records what the
  spike did and stays true; `proposal.md` §Fonts names no insertion point. Backward: T06 (1) falsifier cites "T05's
  block also reaches Linux" — unaffected.
