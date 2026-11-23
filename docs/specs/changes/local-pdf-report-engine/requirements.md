# Local PDF Report Engine — Requirements

**Spec ID:** CHG-LOCAL-PDF-REPORT-ENGINE-001
**Status:** Approved — `auto-approved (pre-approved mode)`, 2026-11-20
**Owner:** Kenji Tanaka (IBD)
**Reviewers:** pending owner review (pre-approved run)
**Last Updated:** 2026-11-23 (Decision Log: validation remediation)
**Related PRD sections:** docs/prd.md — reporting outputs (summaries)
**Related UX/UI Design sections:** not applicable (no screen changes)
**Related TRD sections:** docs/trd/trd.md §2 (`action/summaries`), §7 Integration Points, §9 Observability
**Related Infrastructure sections:** none — the change adds no runtime component
**Companion ai-context docs:** not applicable (no routing, validation, replication or composition contract changes)
**Proposal:** `proposal.md` · **Spike:** `spike.md`

| Field | Value |
|---|---|
| Type | Change |
| Deliverable | software |
| AI Agent | no — deterministic template rendering, no model decides anything at runtime |
| Depth | **Full** — new integration-replacing component, new dependencies, a migration, and the submission email path |
| Approval Mode | `pre-approved (Kenji Tanaka, 2026-11-20)` — inherited from `proposal.md` |
| Phase 1 gate | `auto-approved (pre-approved mode)` 2026-11-20 |

---

## 1. Overview

MARLO delegates the OICR, Innovation and Cluster summary PDFs to the external `reports-microservice` through
RabbitMQ and S3. This spec adds an in-process engine that renders the same Handlebars templates with the same data,
selectable per Global Unit, so MARLO can stop depending on that service. Retiring the microservice path is a
follow-up spec once the local engine is validated in production.

## 2. Problem Statement

Producing one of these PDFs needs Amazon MQ, the microservice, CLARISA, and two S3 buckets to be up at the same
time. A rejection is invisible to MARLO, which only polls S3 until it gives up. The renderer (PhantomJS) is
abandoned. Evidence: `proposal.md` → *Problem / Current Behavior*.

## 3. In-Scope Requirements

### Functional

- **CHG-LPRE-FN-001** — The system MUST render the OICR, Innovation and Cluster summary PDFs in-process from the
  template stored in `report_configurations` and the data map each summary action builds today.
- **CHG-LPRE-FN-002** — The local engine MUST accept the same payload the summary actions send to the
  microservice (`data.data`, `data.options`, `data.templateData`, `data.fileName`), so both engines receive
  identical input.
- **CHG-LPRE-FN-003** — The local engine MUST provide every Handlebars helper that `reports-microservice`
  registers (`pdf.module.ts`), with the same results for the same inputs, for templates within the supported
  template subset (`design.md` §7.1). *Amended 2026-11-23 (Pivot, owner-approved).* *Pivot 2:* the production
  pipeline's `pdf-creator-node` re-registers `ifCond` with the same body as `pdf.module.ts` (*corrected in T12: not an
  18th helper — the 17 already include it*), and the HTML the local engine produces MUST be byte-identical to Handlebars.js output for the six parity cases (NF-001).
- **CHG-LPRE-FN-008** — The system MUST detect constructs outside the supported template subset: a test MUST fail
  when a production template uses one, and a render MUST log a warning naming them. *Added 2026-11-23 (Pivot).*
- **CHG-LPRE-FN-004** — The local engine MUST reproduce html-pdf's page model: A4 portrait, header and footer
  heights taken from `options.header.height` / `options.footer.height`, `#pageHeader` / `#pageFooter` repeated on
  every page, and `#pageHeader-1` / `#pageFooter-1` (or `-first`) replacing them on page 1. *Pivot 2 (2026-11-23, owner-approved option B — design §18).* Met by running
  html-pdf's own PhantomJS page script, not by emulation.
- **CHG-LPRE-FN-005** — When the engine-selection specificity is on for the user's Global Unit, downloading an OICR,
  Innovation or Cluster summary MUST return the locally rendered PDF in the same response, with the same content
  type and file name as today.
- **CHG-LPRE-FN-006** — When the specificity is on, the project-submission email MUST attach the locally rendered
  Cluster PDF.
- **CHG-LPRE-FN-007** — When the specificity is off or absent, every path MUST behave exactly as today
  (microservice).

### Non-Functional

- **CHG-LPRE-NF-001** — Each of the three templates rendered from the reference data MUST have the same page
  count as the production PhantomJS render, and a reviewer MUST accept the side-by-side comparison (`spike.md`).
  *Amended 2026-11-23 (owner: "los reportes deben verse igual que en produccion"):* the local render MUST match the
  production render, measured by `spike/parity/compare.py` against the production pipeline in Docker
  (`spike/parity/prod-render.js`): (1) same page count; (2) the same words on every page, so page breaks fall in
  the same place; (3) every word within ±3 pt of its production position; (4) the same extracted text — no
  ligature glyphs production does not emit. Generation timestamps are ignored. It applies to the three reference
  fixtures **and** to real-data renders of the same three reports. Pixel identity is not required: the engines
  differ.
- **CHG-LPRE-NF-002** — Text MUST render in DejaVu Sans regular/bold (the production font), with the
  characters the font covers rendered rather than replaced. *Pivot 2 (2026-11-23, owner-approved option B — design §18).* PhantomJS MUST see only the bundled DejaVu fonts
  (an isolated fontconfig), whatever fonts the host has installed.
- **CHG-LPRE-NF-003** — Concurrent local renders MUST be capped; a request that cannot start a render within a
  bounded wait MUST fail rather than queue indefinitely.
- **CHG-LPRE-NF-004** — Every remote fetch made during a render MUST have connect and read timeouts. *Pivot 2 (2026-11-23, owner-approved option B — design §18).* PhantomJS's
  per-resource timeout (5 s) and the render's process deadline bound every fetch.
- **CHG-LPRE-NF-005** — The submission path MUST keep the time bound set by `bugfix/submit-unsubmit-email-defects`:
  the submit is never blocked indefinitely by PDF generation.
- **CHG-LPRE-NF-006** — New dependencies MUST NOT change the versions of libraries MARLO already resolves
  (SLF4J API, Commons Logging, ASM) and MUST NOT downgrade any dependency (Hard rule 11). *Pivot 2 (2026-11-23, owner-approved option B — design §18).* OpenHTMLtoPDF and
  PDFBox leave the production classpath (PDFBox may stay test-scoped for PDF assertions).
- **CHG-LPRE-NF-007** — The PhantomJS binary path MUST come from configuration (`pdf.phantomjs.path`, empty in
  the tracked template). Its availability and version (2.1.1) MUST be checked once at startup and logged; when it is
  missing, a flag-on render MUST fail fast with the generic 500 (SEC-002), never hang and never fall back silently. *Pivot 2 (2026-11-23, owner-approved option B — design §18).*

### Data

- **CHG-LPRE-DA-001** — The engine-selection flag MUST be a specificity in `parameters` (default `false`) with the
  constant in both `APConstants` files, shipped as an idempotent Flyway migration (Hard rules 4, 5).
- **CHG-LPRE-DA-002** — The templates in `report_configurations` MUST NOT change in this spec.

### UI

- **CHG-LPRE-UI-001** — Not applicable: no screen, form or i18n string changes. The PDF is the only output.

### Security

- **CHG-LPRE-SEC-001** — During a render the engine MUST load remote resources only over `https` from an
  allowlist of hosts, and MUST NOT load `file:`, `jar:`, plain `http` or any other host. User-entered rich text
  reaches the template through triple-stash fields, so any URL in it is untrusted.
- **CHG-LPRE-SEC-002** — A render failure MUST NOT expose template source, stack traces or internal URLs to the
  user.
- **CHG-LPRE-SEC-003** — Before PhantomJS loads the HTML, `<script>` elements, `on*` event attributes and
  `javascript:` URLs MUST be removed from it (the production templates contain none; user rich text could). PhantomJS
  MUST run with local URL access off, and its page script MUST abort every request outside SEC-001's allowlist. *Pivot 2 (2026-11-23, owner-approved option B — design §18).*
- **CHG-LPRE-SEC-004** — Each render MUST run in its own temporary directory, removed afterwards, and the
  PhantomJS process MUST be killed when the render's deadline expires. *Pivot 2 (2026-11-23, owner-approved option B — design §18).*

### Operations

- **CHG-LPRE-OPS-001** — Each local render MUST log one line with report file name, outcome, page count or byte
  size, and elapsed time; a failure MUST log the cause with its stack trace.

## 4. Out-of-Scope

- Removing the microservice path, the RabbitMQ dependencies and the `microservice.*` keys — follow-up spec
  `changes/retire-reports-microservice` (to be proposed after production validation).
- Moving template images from S3 into MARLO; widening the `report_configurations` columns.
- The AI summary Lambda (`summary.microservice.url`); the Pentaho, iText and POI reports.
- Redesigning the PDFs; changing the data the summary actions build.
- Editing `docs/trd/trd.md` (constitutional document) — recorded as a pending item.
- Installing PhantomJS 2.1.1 and `fontconfig`/`freetype` on the deployed environments, or adding them to the
  `Dockerfile`: governed infrastructure work (`docs/infrastructure.md`), handed to the infrastructure team with the
  questions in OQ-4. Until then the specificity stays `'false'` everywhere. *Pivot 2 (2026-11-23, owner-approved option B — design §18).*

## 5. Personas Affected

| Persona | Effect |
|---|---|
| Cluster coordinators / project leaders downloading summaries | Same PDF, produced in-request, when the flag is on |
| Project submitters and email recipients | Cluster PDF attachment produced in-process |
| Global Unit administrators | Can switch the engine through the specificity |
| IBD operations | Failures visible in MARLO's log; fewer external dependencies once retired |

## 6. Acceptance Criteria

### Requirement: Engine selection (FN-005, FN-006, FN-007)

#### Scenario: Flag on, download

- GIVEN `local_pdf_report_engine_active` is `true` for the user's Global Unit
- WHEN the user downloads an OICR, Innovation or Cluster summary
- THEN the response carries the locally rendered PDF with content type `application/pdf` and the report file name
- BUT it must NOT publish a message to RabbitMQ nor poll S3
- AND IT MUST log one render line (OPS-001)

#### Scenario: Flag off or absent

- GIVEN the flag is `false` or has no `custom_parameters` row for the unit
- WHEN any of the four paths runs
- THEN the microservice method of that path is called with the same arguments as before
- BUT it must NOT invoke the local engine

#### Scenario: Remote resource outside the allowlist (SEC-001)

- GIVEN rich text containing an image on `file:`, plain `http`, an IP literal or a host not on the allowlist
- WHEN the template is rendered locally
- THEN the PDF is produced without that image
- BUT it must NOT open a connection to that location


- **AC-FN-001/002** — Given the OICR template and the reference data payload, when the local engine renders it,
  then the result is a valid PDF whose text contains the report title and section headings of the template.
- **AC-FN-003** — Given each helper and a table of inputs, when it is evaluated, then its output equals the output
  of the `pdf.module.ts` implementation for the same input (e.g. `safeEmpty` of an empty block → `Not Provided`;
  `mathOperator 1 "+" 1` → `2`; `parseJSON` of invalid JSON → empty list; `ifEquals 1 "1"` → true branch).
- **AC-FN-004** — Given a template with `#pageHeader`, `#pageFooter` and `#pageFooter-1`, when rendered to three
  pages, then page 1 shows the `-1` footer and pages 2–3 show the default footer, and none of them appears in the
  body flow.
- **AC-FN-005** — Given the flag is on, when a user downloads an OICR summary, then the response is
  `application/pdf` with the same file name pattern as today and no message is published to RabbitMQ.
- **AC-FN-006** — Given the flag is on, when a project is submitted, then the email carries the local PDF bytes.
- **AC-FN-007** — Given the flag is off, when any of the paths runs, then the microservice methods are called as
  before and the local engine is not.
- **AC-NF-001** — Rendering the three templates with the reference data yields 5 / 6 / 7 pages respectively.
  *Amended 2026-11-23:* and `spike/parity/run.sh` reports `PARITY` for each of the three fixtures and for the
  real-data payloads captured from the local AICCRA copy (OICR 3508, Innovation 1558, Cluster 102076).
- **AC-NF-003** — Given the concurrency cap is reached, when a further render waits past the bound, then it fails
  with a logged error and no PDF.
- **AC-SEC-001** — Given rich text containing `<img src="file:///etc/passwd">`, `http://169.254.169.254/` or an
  image on a non-allowlisted host, when rendered, then no request is made to those locations and the render still
  completes.
- **AC-SEC-002** — Given a template that fails to compile, when a download is requested, then the user receives
  an HTTP error without the template text or exception message.

### Defect classes and their gates

| Defect class this spec can produce | Gate that catches it |
|---|---|
| Java that does not compile (dependency API mismatch, wrong types) | Compile gate (clean `test-compile`, `marlo-verify`) |
| Dependency drift: SLF4J 2 / ASM 7 / Commons Logging 1.4 replacing MARLO's versions | `mvn dependency:tree` on `marlo-web` compared before/after (T01) |
| Helper semantics differ from Handlebars.js | Unit tests with JS-derived expected values (T02) |
| Layout or pagination regression versus production | Page-count test on the three templates (automated) **plus** a visual side-by-side review — no automated check can judge "looks the same"; substitute: T6 visual review of the comparison images during `/akili-validate`, then the owner at the HITL pause |
| Header/footer misplaced (in the flow, wrong page) | Text-position test on rendered pages (T03) |
| Remote fetch outside the allowlist (SSRF) | Unit test with a recording stream factory: zero requests to disallowed URIs (T03) |
| Wrong engine chosen / microservice still called with flag on | Unit tests on the dispatch in each caller with test doubles that record calls (T05) |
| Submission blocked by a slow render | Unit test with a render that never finishes: the call returns within the deadline (T04) |
| Unsafe migration (assumed rows, not idempotent) | `marlo-migration` checklist + applied twice on a throwaway database copy (T06) |
| Checkstyle violation | Checkstyle delta against HEAD (`marlo-verify`) |

Accepted risk: production data is richer than the reference data, so pagination parity is proven on the reference
payloads only; production confirmation happens per Global Unit while the flag is on and the microservice remains
available as the fallback.

## 7. Constitutional Compliance Checklist

- [x] Phase replication: not applicable — no persisted entity is saved or deleted.
- [x] Save validation: not applicable — no save path touched.
- [x] Permissions: no new action; existing summary actions keep their interceptor stacks.
- [x] Specificity: `local_pdf_report_engine_active` through `parameters` + `custom_parameters`.
- [x] Migrations: one Flyway migration for the specificity.
- [x] i18n: no new user-facing strings.
- [x] License header: every new Java file carries the GPL header.
- [x] Code style: Checkstyle gate on changed files.
- [x] REST: not applicable.
- [x] Audit: not applicable.
- [x] Dependency floors preserved; new dependencies added without downgrades.
- [x] Branching: `feature/local-pdf-report-engine` from `staging`, merge target `staging`.

## 8. Open Questions

| # | Question | Blocks? |
|---|---|---|
| OQ-1 | Jira ticket and parent epic for this work | No — traceability only |
| OQ-2 | Who owns the CLARISA MIS connection and the Amazon MQ queue, and who tells them at retirement | No — retirement spec |
| OQ-3 | Keep or delete PDFs already in bucket `microservice-reports` | No — retirement spec |
| OQ-4 | Infrastructure (Pivot 2): CPU architecture of every Tomcat instance (PhantomJS 2.1.1 ships for Linux x86_64 only); deployment route (EC2 directly or the `Dockerfile`); where to install the binary and the `fontconfig`/`freetype` libraries (the font rules ship inside MARLO); who owns it; **whether the PhantomJS process's network egress can be limited to the two S3 hosts** (second layer behind SEC-003, design §18.3) | No for the code — yes for enabling the specificity anywhere |

## 9. Decision Log

- 2026-11-20 — Engine: Handlebars.java + OpenHTMLtoPDF (Option A) — the spike matched production page counts on
  all three templates with no infrastructure change (`spike.md`).
- 2026-11-20 — Parity target is the **Linux** PhantomJS render (DejaVu Sans, 1 px ≈ 1 pt), not the macOS one —
  production runs `node:23` on Linux (`spike.md` F1–F3).
- 2026-11-20 — Templates stay untouched; html-pdf behaviours are emulated by the local engine — both engines must
  render the same database templates during the rollout (CHG-LPRE-DA-002).
- 2026-11-20 — Engine selection per Global Unit via specificity rather than a global property — Hard rule 4, and it
  lets one unit validate before others (proposal Q2).
- 2026-11-20 — Retirement split into a follow-up spec — it requires production validation time that this run
  cannot include.
- 2026-11-20 — Column widening to `MEDIUMTEXT` deferred — no template changes in this spec, so the 201-byte
  headroom is not consumed (proposal R2).
- 2026-11-20 — FN-003 parity is proven by Handlebars.js-generated vectors; Handlebars.java edge cases that no
  production template exercises are accepted and listed in `design.md` §7.1 (execute-time decision under
  pre-approved mode — owner to confirm at review).
- 2026-11-23 — After the T02 HALT the owner approved a fourth attempt; the parent-context walk and `null` list
  elements are fixed in code (2,295 vectors at the time; *superseded* — 2,414 helper vectors and 7,712 subset vectors after the Pivot, `execution.md` T02). The accepted limits are now four (§7.1), the fourth being block
  parameters, which the production templates do not use.
- 2026-11-23 — **Pivot (owner-approved):** FN-003 parity is scoped to a supported template subset; constructs
  outside it are detected by a guard (FN-008) instead of being chased one by one. Reason: five review rounds each
  found another Handlebars.java divergence in constructs no production template uses.
- 2026-11-23 — **Template images stay on S3 for this spec** (validation W8). Renders still fetch images from the two
  allowlisted S3 buckets (SEC-001); moving them under MARLO belongs to the retirement spec. Proposal success
  criterion S2 therefore reads, for this spec: *no call to Amazon MQ, to the `microservice-reports` bucket or to
  CLARISA; image reads from the two allowlisted buckets remain*.
- 2026-11-23 — **Submission path accepted on unit-test evidence (owner, validation W2).** FN-006 is evidenced by
  `LocalPdfEngineDispatchTest` (`clusterSubmitWithTheFlagOn…` reaches `generatePdfBytes` with the 30 s deadline);
  no live submit was run because it changes project state and emails real recipients. Owner words: "acepto la
  evidencia de tests del submit".
- 2026-11-23 — **No reason text on the failure 500 (validation F1).** The fixed English sentence is removed rather
  than i18n-keyed: the response is Tomcat's error page, which MARLO does not localize, and the null-service branch
  already answers a bare 500 (Hard rule 8, UI-001). Implemented in T10.
- 2026-11-23 — **Layout parity raised from "same page count + reviewer acceptance" to measured parity (owner).**
  The owner did not accept the layout differences of T07 ("los reportes deben verse igual que en produccion")
  and approved T11 with the criterion proposed by the Leader (same pages, same words per page, ±3 pt, same text)
  and a raised budget. Pixel identity is explicitly out of reach (different layout engines); the measurement
  replaces the eyeball review. First measurement: 0 of 3 fixtures at parity (`execution.md` T11).
- 2026-11-23 — **Pivot 2 (owner-approved option B): render with PhantomJS itself, run by MARLO as a local
  process.** Reason: T11 showed that emulating PhantomJS's WebKit layout inside OpenHTMLtoPDF matches the fixtures
  but not real data (the 481-page cluster came out 478 pages, worse than before T11), and each data set revealed new
  divergences. A throwaway prototype — Java Handlebars HTML handed to html-pdf/PhantomJS 2.1.1 — reached **PARITY on
  all six cases**, including the 481-page cluster (`execution.md` "Option B feasibility proof"). Consequences: the
  OpenHTMLtoPDF engine, the html-pdf compatibility step and the Java resource loader are retired; MARLO depends on an
  unmaintained external binary (PhantomJS 2.1.1, last release 2018) that the infrastructure team must install (OQ-4);
  the principle "no infrastructure change" (proposal, design ADR-2) is withdrawn. T11's emulation code was discarded
  unmerged (`spike/t11-discarded-emulations.diff`). Owner words: "sí, sigue con B y haz primero la prueba de paridad",
  then "sí, sigue con las tres".
