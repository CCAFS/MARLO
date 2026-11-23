# Local PDF Report Engine — Proposal

## Document Control

| Field | Value |
|---|---|
| **Spec Path** | `changes/local-pdf-report-engine` |
| **Proposal ID** | `CHG-LOCAL-PDF-REPORT-ENGINE-001` |
| **Slug** | `local-pdf-report-engine` — the leading token of the argument; the rest of the argument is context |
| **Type** | **Change** (replace an external integration with an in-process one) |
| **Deliverable** | `software` |
| **Approval Mode** | **`pre-approved (Kenji Tanaka, 2026-11-20)`** — mandate: *"sigue todo el proceso con las opciones recomendadas, no estaré disponible entonces avísame solo cuando termines para esta tarea"*. Routine gates auto-pass and are logged; HALT, Pivot, budget tripwire, `FATAL_FAIL`, `PRODUCT_BUG` and destructive actions still stop. Standing owner rules kept: no commit and no push without asking |
| **Parent Spec** | none — flat spec |
| **Depends on** | **none** |
| **Parallel-safe** | **no** — touches `ReportingSummaryAction`, `BaseStudySummaryData`, `ProjectInnovationSummaryAction`, both `APConstants`, `APConfig`, the `marlo-web` POM and `report_configurations` |
| **Status** | **Approved** — `auto-approved (pre-approved mode)`, 2026-11-20; recommended options taken: Option A gated by the spike, rollout behind a specificity, work on a feature branch |
| **Date** | 2026-11-20 |
| **Working branch** | `feature/local-pdf-report-engine` (created `--no-track` from `staging` at `2f67c5d2ab`) |
| **Requirement source** | Owner request in session (2026-11-20): *"la idea es implementar esta funcionalidad dentro de marlo y no depender del software para generar reportes"*. No Jira ticket linked yet |
| **Evidence base** | MARLO code at `2f67c5d2ab`; `AllianceBioversityCIAT/one-cgiar-microservices` at `6f16514` (2026-06-11); `pdf-creator-node@2.3.5` package source; read-only queries on the local copies `partroute_local`, `aiccradb2`–`aiccradb4`, `aiccradb_actsave` |

---

## Intent

**Generate the OICR, Innovation and Cluster/Project summary PDFs inside MARLO, from the same Handlebars
templates, so that producing a report no longer depends on an external microservice, a RabbitMQ broker, an S3
round-trip and a CLARISA API-key check.**

The rendering work the microservice does is small — fill a Handlebars template, convert HTML to PDF. Everything
else in the chain (queue, bucket, polling, API key, Slack alerts) exists only because the work runs in another
process.

---

## Problem / Current Behavior

### How a summary PDF is produced today

| Step | Where | Evidence |
|---|---|---|
| 1. A summary action builds a JSON `{pattern: "pdf.generate", data: {data, options, templateData, fileName, bucketName, apiKey}}` | MARLO | `BaseStudySummaryData.java:1513-1544`, `ReportingSummaryAction.java:7846-7889`, `ProjectInnovationSummaryAction.java:1629-1673` |
| 2. `MicroserviceReportAction` publishes it to RabbitMQ (Amazon MQ, queue `microservice.queueName`) | MARLO | `MicroserviceReportAction.java` — `sendOICRsQueueMessage`, `sendInnovationsQueueMessage`, `sendClusterReportQueueMessage`, `DefaultFetchTransport.publish` |
| 3. `reports-microservice` consumes the `pdf.generate` message and validates `apiKey` against CLARISA | external | `reports-microservice/src/domain/api/pdf/pdf.controller.ts:45`; `shared/interceptors/microservice.intercetor.ts` (`validateApiKey`) |
| 4. It compiles the template with Handlebars (17 custom helpers) and renders it with `pdf-creator-node` → `html-pdf` → **PhantomJS** | external | `pdf/pdf.module.ts`; `pdf.service.ts:81`; `pdf-creator-node@2.3.5/index.js:47` |
| 5. It uploads the PDF to S3 bucket `microservice.bucketName` | external | `pdf.service.ts` — `PutObjectCommand` |
| 6. MARLO polls `microservice.s3.url + fileName` with `HEAD`, then `GET`s the file and streams it to the browser | MARLO | `downloadPDFByURL` (5 × 2 s); `fetchClusterReportBytes` (10 polls, 30 s total deadline) |

The cluster PDF also travels as an email attachment on project submission:
`ProjectSubmissionAction.java:404` → `ReportingSummaryAction.generatePdfBytes()` (`:7745`) → `fetchClusterReportBytes`.

### What that dependency costs

| Problem | Evidence |
|---|---|
| A report needs five external pieces up at once — Amazon MQ, the microservice, CLARISA (API-key check), S3 bucket `microservice-reports`, and S3 bucket `marlo-pdf-resources-dev` for template images | Steps 2–6 above; image URLs in all three templates in `report_configurations` |
| Publishing is fire-and-forget: if the microservice rejects the message (invalid key, template error), MARLO only learns by polling S3 until it gives up | `sendOICRsQueueMessage` returns `SUCCESS` after `basicPublish`; the rejection goes to the microservice's Slack, not to MARLO (`microservice.intercetor.ts`) |
| The renderer is PhantomJS, abandoned upstream | `pdf-creator-node@2.3.5` depends on `html-pdf@^3.0.1` → `phantomjs-prebuilt@^2.1.16` (`reports-microservice/package-lock.json:7207-7219`); PhantomJS end-of-life `UNVERIFIED — confirm at source before relying on it` |
| The local dev profile publishes to the production queue | `marlo-dev.properties:148` — `microservice.queueName=cgiar_ms2_prod_reports_queue` |
| Generated PDFs sit in a bucket that is fetched with no credentials, under a guessable name | `downloadPDFByURL` uses a plain `HttpURLConnection`; names follow `AICCRA-Cluster-<id>-Summary-<datetime>.pdf` (`ReportingSummaryAction.java:7853`); bucket ACL `UNVERIFIED — confirm at source before relying on it` |
| MARLO's own architecture document does not list this integration | `docs/trd/trd.md` §7 has no row for the reports microservice, RabbitMQ or Amazon MQ |
| RabbitMQ is on the classpath for this one flow only | `com.rabbitmq` / `org.springframework.amqp` imported only by `MicroserviceReportAction.java` (repo-wide grep over `marlo-web`, `marlo-data`, `marlo-utils`) |

### What the templates rely on

| Fact | Evidence |
|---|---|
| Three Handlebars templates stored in `report_configurations` (`oicr_template_data`, `innovation_template_data`, `project_template_data`), one row read via `findAll().get(0)` | `MicroserviceReportAction.loadData()`; `ReportingSummaryAction.loadClusterTemplateData()` (`:7723`) |
| Columns are `TEXT` (max 65,535 bytes). Innovation template is **65,334 bytes** — 201 bytes of headroom; OICR 64,620; project 63,120 | `DESCRIBE report_configurations` and `LENGTH()` on `partroute_local`, same on `aiccradb2`/`3`/`_actsave`; `aiccradb4` innovation = 64,758 |
| Templates ship through Flyway migrations | e.g. `V2_6_0_20260212_1000__UpdateStyles.sql`, `V2_6_0_20260122_1540__FixGeneralFormats.sql` |
| Helpers used: `safeEmpty`, `ifEquals`, `ifOr`, plus built-in `if` / `each` / `with` and Mustache-style sections | token count over the three templates |
| Page header/footer use `html-pdf`'s `#pageHeader` / `#pageFooter` convention; options are A4 portrait, border 0, header 40 mm, footer 30 mm | `id="pageHeader"` / `id="pageFooter"` in all three templates; `ReportingSummaryAction.java:7825-7834` and the two sibling option blocks |
| Montserrat is embedded as base64 **woff2**; layout uses `display:flex`, `display:grid` (project), `transform`, `box-shadow`, `border-radius`, `position:fixed` | CSS scan of the three templates |

---

## Proposed Outcome

- A user who downloads an OICR, Innovation or Cluster summary PDF gets it rendered by MARLO in the same request, from the
  same template and data, with no queue, no bucket and no polling.
- The submission email carries the cluster PDF produced the same way.
- A render failure is reported in MARLO's own log with the template, report and cause — not inferred from a missing S3
  object.
- While the new engine is validated, a specificity selects the engine per Global Unit, so the external path stays
  available as a fallback until it is retired.
- Once retired, MARLO has no RabbitMQ dependency and no `microservice.*` configuration keys.

---

## Scope

| In scope |
|---|
| An in-process PDF render service in `marlo-web`: Handlebars compile + HTML-to-PDF, returning bytes |
| Port of the helpers the templates use (`safeEmpty`, `ifEquals`, `ifOr`), with the same semantics as `pdf.module.ts` |
| Translation of the `#pageHeader` / `#pageFooter` convention and the A4 / 40 mm / 30 mm options to the new engine |
| Rewiring the three callers and `ReportingSummaryAction.generatePdfBytes()` to the new service |
| Engine-selection specificity through `parameters` + `custom_parameters`, constants in both `APConstants` (Hard rule 4) |
| Flyway migration widening the three template columns to `MEDIUMTEXT`, then template adjustments the new engine needs |
| Serving template images and the font from MARLO itself instead of `marlo-pdf-resources-dev` |
| A throwaway **spike** that renders the real OICR template with sample data, compared side by side with a current PDF — it gates `/akili-specify` |
| Retirement phase: remove `MicroserviceReportAction`'s queue/S3 paths, the `struts-report.xml` actions, the RabbitMQ dependencies and the `microservice.*` keys in `APConfig`, `marlo-test.properties` and the per-environment files |
| TRD §7 / §1.2 sync (recorded as a pending item if executed on a spec branch) |

## Non-Goals

- The AI summary Lambda (`summary.microservice.url`, `AIReportService`) — a different service with a different purpose.
- Redesigning the PDFs. The target is parity with today's output; visual improvements are a later change.
- The Pentaho-based summaries (39 `.prpt` reports) and the iText / POI exports — untouched.
- Changing the data each summary sends (`jsonData` maps stay as built today).
- Removing the hardcoded `AICCRA-` prefix in generated file names — noted, out of scope.
- Changes to the external `one-cgiar-microservices` repository.

---

## Affected Users, Systems, And Specs

| Affected | How |
|---|---|
| Users who download OICR / Innovation / Cluster summaries | Same PDF, produced in-request |
| Project submitters and the submission email recipients | Cluster PDF attachment produced in-process |
| Production EC2 Tomcat instances | CPU / memory per render moves into the JVM (sizing in Risks) |
| `reports-microservice`, Amazon MQ queue, bucket `microservice-reports`, CLARISA MIS connection for MARLO | No longer called after retirement |
| `docs/trd/trd.md` §7, §1.2 | Integration row added for the transition, then removed; embedded libraries row added |
| `MicroserviceReportFetchTest` (16 `@Test`) | Retired with the code it covers; replaced by tests of the new service |
| Related specs | `bugfix/submit-unsubmit-email-defects` introduced `fetchClusterReportBytes` (`@akili-spec` tag in `MicroserviceReportAction.java`); its timeout contract must be honoured or superseded explicitly |

---

## Visual Reference

- Source: **None** (backend change).
- Location: —
- Notes: the visual baseline is the **current PDF output** for each of the three reports. The spike produces a
  side-by-side comparison (current vs new) that becomes the visual reference for `/akili-specify`.

---

## Requirement Delta Preview

### ADDED Requirements

- MARLO renders summary PDFs in-process from the `report_configurations` templates and the summary data map.
- The engine is selectable per Global Unit via a specificity; default stays on the external path until validated.
- A render failure is logged with report type, file name and cause, and the user gets an explicit error instead of a
  timeout.
- Template images and the font are served from MARLO.

### MODIFIED Requirements

- `ReportingSummaryAction.generatePdfBytes()` returns bytes from the local engine (no 30 s polling budget).
- `report_configurations` template columns become `MEDIUMTEXT`.
- Templates adjusted for the new engine (header/footer mechanism, font format, unsupported CSS).

### REMOVED Requirements (retirement phase)

- Publishing report requests to RabbitMQ; polling and downloading from S3.
- `/report/{crp}/sendOICRsQueueMessage` and `/report/{crp}/fetchPDF` (no caller under `marlo-web/src/main/webapp`).
- `microservice.*` configuration keys and the `spring-amqp` / `amqp-client` dependencies.

---

## Approach Options

| | Option | For | Against |
|---|---|---|---|
| **A** | **Handlebars.java + OpenHTMLtoPDF** (pure Java, inside the WAR) | No infrastructure change — Maven dependencies only. Templates stay Handlebars. Low memory per render. Licences Apache-2.0 / LGPL-2.1, compatible with GPL `UNVERIFIED — confirm at source before relying on it` | No `flex` / `grid` / `box-shadow`; fonts TTF/OTF only; needs well-formed XHTML (jsoup conversion). Original project's maintenance status `UNVERIFIED` — a community fork must be confirmed in the spike |
| B | Handlebars.java + headless Chromium | Highest CSS fidelity | Chromium on every EC2 and in the `Dockerfile` — a governed infrastructure change (`docs/infrastructure.md` §1–§5). 100–300 MB per render `UNVERIFIED`. Output would differ from today's PhantomJS look |
| C | Rewrite the three templates as Pentaho `.prpt` (already embedded) | Engine already in the WAR | Rewrite of ~190 KB of HTML into a different paradigm; loses the HTML-template workflow; Pentaho 8.0 is itself dated |
| D | Self-host Gotenberg or fork the microservice | Least MARLO code | Still a separate service to run and monitor — the dependency the owner wants gone |

## Recommended Approach

**Option A, gated by the spike.**

PhantomJS, today's renderer, does not support `display:grid` and handles unprefixed `flex` poorly `UNVERIFIED —
confirm at source before relying on it`, so the current PDFs are already laid out without those features. That
makes OpenHTMLtoPDF's CSS 2.1 + paged-media subset the closest match to what users see today, with no
infrastructure change. The spike settles it with evidence: if the OICR render is not acceptable after reasonable
template adjustment, fall back to Option B and treat the Chromium install as an infrastructure change.

Delivery order inside the spec: engine + OICR behind the specificity → Innovation → Cluster + submission email →
retirement after production validation. `/akili-specify` decides whether the retirement phase stays a task group
or becomes a follow-up spec.

---

## Risks, Dependencies, And Open Questions

| # | Item | Type | Handling |
|---|---|---|---|
| R1 | Visual parity: the header/footer translation and CSS gaps may change pagination or layout | Risk | Spike + side-by-side comparison per report before the specificity is turned on |
| R2 | Innovation template has 201 bytes of headroom in `TEXT` | Risk | Column migration to `MEDIUMTEXT` ships **before** any template change |
| R3 | Render cost now runs in the Tomcat JVM, including the submission request | Risk | Concurrency cap and timeout in the service; measure render time and heap in the spike |
| R4 | Behaviour contract from `bugfix/submit-unsubmit-email-defects` (submission must not hang on the PDF) | Dependency | Specify keeps an explicit time bound on the local render or records why it is no longer needed |
| R5 | Migrations are safe on any database: no assumed rows in `report_configurations`, idempotent, DML-only where possible | Constraint | `marlo-migration` skill |
| R6 | KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-1: enumerations scoped to already-named classes missed siblings | Lesson | Specify re-enumerates every caller of `MicroserviceReportAction` and every reader of `microservice.*` repo-wide, not only the five files named here |
| Q1 | Is there a Jira ticket (and parent epic) for this work? | Open | Owner |
| Q2 | Is a per-Global-Unit switch needed, or is one global parameter enough? `report_configurations` itself is global (one row) | Open | Decide in specify |
| Q3 | Who owns the CLARISA MIS connection and the Amazon MQ queue, and should they be told when MARLO stops using them? | Open | Owner |
| Q4 | Should already-generated PDFs in `microservice-reports` be kept, and is anything else reading that bucket? | Open | Owner / infra |
| Q5 | Library versions and licences for Option A | Open | Spike confirms; Hard rule 11 applies to any POM change |

---

## Success Criteria

| # | Criterion |
|---|---|
| S1 | Each of the three reports renders locally from the production template and sample data, and a reviewer accepts it side by side with the current PDF |
| S2 | With the specificity on, downloading a summary and submitting a project produce the PDF with no call to Amazon MQ, S3 or CLARISA. *Restated 2026-11-23 (requirements Decision Log, validation W8): template images are still read from the two allowlisted S3 buckets until the retirement spec* |
| S3 | A broken template or data error produces a logged cause and an explicit user error within the configured time bound |
| S4 | After retirement, `grep` finds no `com.rabbitmq`, `org.springframework.amqp` or `microservice.` reference in the three modules, and the compile and Checkstyle gates pass |
| S5 | `docs/trd/trd.md` §7 reflects the final state |

---

## Next Step

```text
/akili-specify changes/local-pdf-report-engine
```

Run the spike first (the **spike** row in Scope): its result fixes the engine choice that `design.md` records.
