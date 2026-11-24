# Kaizen Entry — changes/local-pdf-report-engine

## Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/local-pdf-report-engine` |
| Date | 2026-11-24 |
| Branch | feature/local-pdf-report-engine (spec branch — Branch Context `spec`; apply-capable branch: `staging`) |
| Archive Run | 1 |
| Approval Mode | pre-approved (owner), with owner-approved HALT / Pivot / budget continuations |

## Metrics

| Signal | Value | Source |
|---|---|---|
| Tasks executed | 18 (16 PASS, T08 deferred, T11 superseded) | tasks.md |
| Reviewer FAIL rework attempts | ≈ 20 — T02 ×7, T13 ×4 (incl. 2 Leader-evidence reworks), T12 ×2, T04 ×2, T03, T07, T10, T11, T17 ×1 each | execution.md |
| HALTs / FATAL_FAILs | 2 HALTs (T02 retry cap; T13 retry cap, twice owner-extended) / 0 | execution.md — HALT: CHG-LPRE-T02; T13 attempts 4–5 |
| Pivots | 2 (T02 template subset; Pivot 2 — OpenHTMLtoPDF → PhantomJS) | execution.md — Pivot Record, Pivot Record 2 |
| PRODUCT_BUGs | 1 (live render NPE on real `null` payload values → T09) | execution.md — Live verification |
| Judgment-day severe findings | 2 rounds before approval | judgment.md |
| Validation FAIL / WARN | pass 1: 1 / 7 · pass 2: 2 / 12 → all closed | validation-report.md |
| Budget | 40 review rounds vs 11 planned (raised to 21, then ~31) — every overrun owner-approved | execution.md — Budget note |
| Tasks closed under `REVIEW_WAIVED` / `REVIEW_SKIPPED` | 0 / 0 (one tiny tweak in T16 accepted against the reviewer's own remediation, recorded) | execution.md |
| Escaped defects (§3) | 4 found after a task closed, all on the target platform: Ubuntu OpenSSL config (T13), minimal fontconfig (T13), PDFBox tab extraction in the gated test (validation), 123-char line reported "Checkstyle ok" (validation) | execution.md; validation-report.md |

## Lessons

- **KZ-changes--local-pdf-report-engine-1 — A "replace system X" spec must fix its acceptance oracle as an automated diff against X on real data before choosing the replacement.** (Product + Methodology, High)
  - Root cause: NF-001 defined parity as "same page count + a reviewer accepts the side-by-side", and the spike
    validated the engine on three synthetic fixtures only. The engine choice (OpenHTMLtoPDF) was therefore made
    against a subjective, fixture-only criterion; when the owner required "igual que producción", the first real-data
    measurement (T11) showed fixture-fitted emulation getting the 481-page cluster *worse* (478 pages) — a Pivot after
    seven tasks had shipped.
  - Evidence: requirements.md — NF-001 (original text + 2026-11-23 amendment); execution.md — T11 real-data table and
    Pivot Record 2; spike.md — "Answer" (three fixtures).
  - Standardization: → P1 (local), P2 (upstream)

- **KZ-changes--local-pdf-report-engine-2 — Evidence for a platform-dependent task must come from the target platform and representative data before the task closes.** (Product + Methodology, High)
  - Root cause: tasks closed on macOS runs and synthetic fixtures while the product runs on Linux with real payloads.
    Four defects escaped that way: real payloads with `null` values (PRODUCT_BUG → T09), Ubuntu's `openssl.cnf`
    breaking PhantomJS, a minimal fontconfig breaking parity, and gated tests that had never run on Linux (PDFBox tab
    extraction) — each found only when the Leader ran the target environment.
  - Evidence: execution.md — Live verification (PRODUCT_BUG), T13 Leader evidence (Linux, two runs), Validation
    remediation (gated test on Linux, 3 failures).
  - Standardization: → P3 (local), P4 (upstream)

- **KZ-changes--local-pdf-report-engine-3 — The Leader re-runs the style gate on the final diff after every follow-up edit; an Implementer's "Checkstyle ok" is a claim, not evidence.** (Product, Medium)
  - Root cause: the T18 follow-up added a 123-character line after the gate the Implementer reported; the Reviewer
    (read-only) checked line lengths by sampling. Only the validation-time Checkstyle run over all changed files
    caught it (F1).
  - Evidence: validation-report.md — §5 Build Integrity, F1; execution.md — T18 follow-up ("Checkstyle ok").
  - Standardization: → P5

## Noted, not a lesson

- A Leader claim ("an 18th helper `ifCond`") was written into four spec documents before being checked at the source;
  the Implementer refuted it (T12). Correction closure was swept — close to KZ-…auth-flow-1 but not a recurrence (the
  sweep ran). Watch for a second occurrence.
- Docker Desktop served a stale view of the repo's `target/` to containers twice; copying classes to the scratchpad
  avoided it. Tooling friction, not a spec defect.
- zsh does not word-split an unquoted variable holding several `docker` flags (one failed measurement run).
- Spec docs trailed the code repeatedly (validation pass 2: 12 WARNs, mostly stale text) — Pivot 2 added §18 on top
  instead of rewriting §1–§17. Acceptable for an audit trail; a rewrite-on-pivot rule may be worth it if it recurs.

## Pending Items

### P1

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `docs/specs/general-setup/requirements.md` |
| Edit | Under Non-Functional guidance add: "When the change replaces an existing system's output (renderer, exporter, integration), state the parity requirement as an automated comparison against that system on real production data, and require the spike to measure it before the design picks an engine." |
| Severity | High |
| Status | pending |

### P2

| Field | Value |
|---|---|
| Kind | upstream |
| Target | methodology |
| Edit | `/akili-propose` spike guidance: for replacement specs, the spike must produce an automated parity oracle (original vs candidate) and run it on real data, not only fixtures, before the engine/approach is chosen. Source: KZ-changes--local-pdf-report-engine-1. |
| Severity | High |
| Status | pending |

### P3

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `.agents/leader.md` |
| Edit | Append: "A task whose behaviour depends on the runtime platform or on production data closes only after its evidence ran on that platform (e.g. a Linux container matching the deploy image) and on at least one real-data input; macOS or fixture-only runs are recorded as partial evidence." |
| Severity | High |
| Status | pending |

### P4

| Field | Value |
|---|---|
| Kind | upstream |
| Target | methodology |
| Edit | `.agents/leader.md` (packaged persona): add the target-platform / representative-data closure rule of P3. Source: KZ-changes--local-pdf-report-engine-2. |
| Severity | High |
| Status | pending |

### P5

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `.agents/leader.md` |
| Edit | Append: "After any follow-up edit on a task, the Leader re-runs the style gate (Checkstyle 8.18 via `marlo-verify`) over every Java file changed since the spec's base commit before closing; the Implementer's reported gate is not accepted as evidence." |
| Severity | Medium |
| Status | pending |

### P6

| Field | Value |
|---|---|
| Kind | factual-sweep |
| Target | root `CLAUDE.md` and `AGENTS.md` — "Honest state of testing" note |
| Edit | Replace "36 test files … 35 of them carrying `@Test` methods, 271 in total" with the counts measured at apply time (on 2026-11-24 this branch had 152 test files, 144 with `@Test`, and the `-am` suite ran 1,458 tests). Re-measure on `staging` before writing. |
| Severity | Low |
| Status | pending |

### P7

| Field | Value |
|---|---|
| Kind | trd-adr |
| Target | `docs/trd/trd.md` |
| Edit | Decision (supersedes: none — new): "Summary PDFs (OICR, Innovation, Cluster) can be rendered inside MARLO behind the specificity `local_pdf_report_engine_active`: Handlebars.java (17 helpers, HTML byte-identical to Handlebars.js) → sanitizer → bundled template images → PhantomJS 2.1.1 shipped in the WAR, run as a local process with html-pdf's page script, Debian fontconfig rules and DejaVu fonts; no external request. The reports microservice remains the path while the flag is off, until `changes/retire-reports-microservice`." Also add the transitional reports microservice and the local engine to TRD §7 Integration Points. Source: archive/2026-11-24-changes--local-pdf-report-engine design §18, ADR-CHG-LPRE-6. |
| Severity | Medium |
| Status | pending |

### P8

| Field | Value |
|---|---|
| Kind | guide-sync |
| Target | `.agents/*.md` personas |
| Edit | `akili doctor --agents` (akili-specs v2.45.0) reported all four personas `UNMARKED` (MARLO-customised, no section markers). Decide on `staging` whether to add the markers (`--fix`) or record the customisation as intentional. |
| Severity | Low |
| Status | pending |
