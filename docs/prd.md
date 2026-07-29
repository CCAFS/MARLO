# MARLO — Product Requirements Document (PRD)

**Status:** Living document. Version: 1.0 (Constitutional Baseline).
**Owner:** IBD Team — Alliance of Bioversity International and CIAT.
**Last Updated:** 2026-04-30.
**Source Inputs:** `MARLO_Technical_Overview.docx` (April 2026), `AGENTS.md`, `reports/ai-context/*`, current source tree (`marlo-web`, `marlo-data`, `marlo-core`, `marlo-utils`, `marlo-parent`).

> This PRD is the constitutional product baseline for MARLO. It frames *what* MARLO is, *who* it serves, and *what success looks like*. Technical details live in `docs/system-design/design.md` and `docs/detailed-design/detailed-design.md`. Module-level specs live under `docs/specs/`.

---

## 1. Overview & Purpose

MARLO (Managing Agricultural Research for Learning and Outcomes) is an open-source, web-based research-management platform that supports the full Planning, Monitoring, Reporting, and Learning (PMRL) cycle of complex multi-country, multi-stakeholder research programs.

The platform turns monitoring, evaluation, and reporting from a periodic compliance exercise into a continuous, evidence-based workflow embedded in how research teams actually work. It serves as the system of record for annual planning, deliverables, innovations, outcome impact case reports (OICRs), quality assurance (QA), and indicator contributions across one or more research programs ("Global Units" — CRPs, Platforms, or Centers).

This PRD documents requirements for the **AICCRA deployment branch** of MARLO (the active production line maintained by the IBD team), while preserving compatibility with multi-program, multi-platform deployments as a first-class capability.

---

## 2. Problem Statement

Large research programs (e.g., AICCRA, CCAFS) operate across many countries, partner institutions, clusters, and donors. Without a shared platform, they default to spreadsheets, email chains, and fragmented reporting, which produces:

- **Information silos** — cluster data is not comparable across the program.
- **Inconsistent reporting** — donors receive heterogeneous, hard-to-validate narratives.
- **Weak traceability** — outputs (deliverables, innovations, OICRs) are disconnected from the indicators they contribute to.
- **High coordination cost** — leadership lacks a single, authoritative view of progress.
- **Compliance risk** — open access, FAIR, and quality requirements are not enforced systematically.
- **Knowledge loss** — historical phases (planning vs. reporting) are overwritten or lost.

MARLO solves this by providing a single, phased, role-based, auditable platform where planning, progress, and reporting data flow through a structured QA process and are exposed both as transactional UIs and as analytical assets (Power BI, AI services).

---

## 3. Target Personas

MARLO serves a layered set of personas. Permissions and UX must reflect each persona's scope.

| Persona | Scope | Primary needs |
|---|---|---|
| **Cluster / Project Coordinator** | Cluster | Enter planning data, register deliverables, innovations, OICRs; respond to QA feedback; meet annual cycle deadlines. |
| **Cluster / Project Leader** | Cluster | Strategic oversight; sign off on cluster narratives; review cluster KPIs. |
| **PMU (Program Management Unit)** | Program | Compliance and synthesis; track completeness and QA across all clusters; produce annual donor reports. |
| **QA Reviewer** | Phase | Provide structured feedback on submissions; mark items validated; track 92%+ quality and 95%+ open-access targets. |
| **Information / Data Owner (KDS)** | Program | Approve definitions, manage data governance, audit changes. |
| **Program / CRP Admin** | Program | Configure cycles, roles, partners, locations, and program-level taxonomies (SLOs, IDOs, cross-cutting issues). |
| **Super Admin** | System | Onboard new Programs/Platforms/Centers, control system-wide parameters and specificities. |
| **Guest User** | Restricted | Limited, controlled external collaboration. |
| **Public reader** | Public | Consume embedded BI dashboards (e.g., "Deliverables by the Numbers"). |
| **AI service consumer** | Program | Use chatbot, text mining, and report generation to query/produce content from program data. |

Personas are mapped to system roles defined in `users` / `crp_users` / `user_role` tables (Section 15 of the Technical Overview).

---

## 4. Goals & Success Metrics

### 4.1 Product goals

1. **Reliable system of record** for results-based research management across the full PMRL cycle.
2. **Embedded QA** — quality and open-access compliance are enforced *during* data entry, not after.
3. **Phase-safe history** — planning and reporting cycles preserve immutability of past phases.
4. **Multi-program flexibility** — onboarding a new Program/Platform/Center is a controlled but supported operation.
5. **Analytics-ready** — operational data flows into a Medallion (Bronze/Silver/Gold) BI architecture.
6. **AI-augmented** — AI services (text mining, report generator, chatbot) sit *on top of* MARLO data without bypassing its governance.
7. **Open and self-hostable** — GPL-licensed, hostable by partner institutions.

### 4.2 Quantitative success metrics (baseline derived from AICCRA 2021–2025)

| Metric | Target | Rationale |
|---|---|---|
| Open-access compliance on deliverables | ≥ 95% annually | Replicates AICCRA's 2024 96% baseline. |
| Quality rating on validated deliverables | ≥ 92% | Replicates AICCRA's 2024 baseline. |
| QA cycle responsiveness — first response SLA | ≥ 85% | Support framework target; current 89%. |
| QA cycle resolution SLA | ≥ 85% | Support framework target; current 96%. |
| Backup recoverability | Daily, < 24h RPO | Daily S3 backup process. |
| Critical CVEs in production dependencies | 0 open | Post-Jan 2026 SETI remediation baseline. |
| Results BI refresh latency | ≤ 8 hours | Fabric Data Pipeline cadence. |
| QA dashboard refresh latency (active periods) | ≤ 30 minutes | Operational requirement. |

### 4.3 Qualitative goals

- A **new cluster coordinator can complete one full annual cycle** (planning → progress → reporting) without one-on-one support after a structured onboarding.
- A **PMU lead can produce a donor-ready annual report** primarily from MARLO outputs (BI + AI Reports Generator).
- A **partner institution can self-host a MARLO instance** for a new program from public source + this constitution.

---

## 5. Scope

### 5.1 In scope (constitutional baseline)

- Annual Work Plan and Budget (AWPB) — planning per cluster / project.
- Progress monitoring (continuous, intra-year).
- Annual Reporting (AR) — narrative + structured indicators + cluster synthesis.
- Deliverables management (full lifecycle, Open Access, FAIR).
- Innovation tracking with Scaling Readiness (0–9).
- OICRs (Outcome Impact Case Reports) with maturity classification.
- QA workflow with structured feedback comments and validation states.
- POWB (Plan of Work and Budget) and Annual Report synthesis modules.
- Impact Pathway management (outcomes, SLOs, IDOs, cross-cutting issues).
- Funding sources and partner management (CLARISA-integrated).
- Program / Platform / Center administration (multi-tenant Global Units).
- User, role, and permission management (CGIAR Active Directory + internal users).
- Phase-based data model with forward-only replication.
- Specificity feature flags (`parameters` + `custom_parameters`) for per-Global-Unit behavior.
- BI dashboard delivery (Power BI + public embedding tool).
- AI services (text mining, report generator, chatbot) consuming MARLO data via documented contracts.
- Spring MVC REST API under `/api/*` for external integrations (CLARISA, AI services, BI ingestion).
- Audit log of significant changes.

### 5.2 Out of scope (constitutional)

- General-purpose project management (Gantt, time tracking, ticketing) — MARLO is not Jira/MS Project.
- Personal task management — handled by the team's external tools (Freshservice, Jira, Slack).
- Financial accounting or payroll — MARLO holds budget and expenditure metadata, not transactional finance.
- New mobile-native clients — current scope is responsive web only.
- Replacing CGIAR Active Directory — MARLO authenticates *against* it, plus an internal MD5-encrypted fallback.
- Replacing CLARISA, CGSpace, Power BI, or AWS Bedrock — MARLO integrates with them, it does not reimplement them.
- Public write APIs — `/api/*` is not a public open-write surface; access is controlled per consumer.
- Real-time (sub-minute) collaboration — MARLO is form-based with phase-aware persistence, not a live collaborative editor.

### 5.3 Out of scope for the AICCRA branch (today)

- New Struts JSON endpoints beyond existing patterns (per `AGENTS.md` scope guardrails).
- Reintroducing legacy modules (CCAFS-specific synthesis variants `powb2019`, `annualReport2018`) for new programs — these remain frozen for historical compatibility.

---

## 6. User Stories

User stories are grouped by persona. Each links to the modules where the corresponding feature lives.

### 6.1 Cluster Coordinator

- **US-CC-01 — Plan annual work.** As a Cluster Coordinator, I want to enter my cluster's annual plan (objectives, deliverables, targets, partners) under the active POWB phase so that I can submit a complete plan before the planning deadline.
- **US-CC-02 — Track deliverable progress.** As a Cluster Coordinator, I want to update deliverable status, dissemination, and quality fields during the year so that progress is visible to PMU and QA.
- **US-CC-03 — Register innovations and OICRs.** As a Cluster Coordinator, I want to create an innovation with its Scaling Readiness level and link supporting deliverables so that downstream BI and donor reports reflect cluster contributions accurately.
- **US-CC-04 — Respond to QA feedback.** As a Cluster Coordinator, I want to see QA feedback comments on my entries and resolve them so that my cluster reaches the 92% quality threshold.

### 6.2 QA Reviewer

- **US-QA-01 — Review submissions.** As a QA Reviewer, I want a queue of pending items per cluster and section so that I can systematically validate and comment on them.
- **US-QA-02 — Add structured feedback.** As a QA Reviewer, I want to attach typed feedback comments to specific fields so that improvements are unambiguous and trackable.
- **US-QA-03 — Track QA throughput.** As a QA Reviewer, I want a live dashboard of feedback resolution times so that I can hit the 30-minute QA refresh and SLA targets.

### 6.3 PMU / Program Lead

- **US-PMU-01 — Cluster completeness view.** As a PMU lead, I want a real-time completeness dashboard so that I can chase incomplete clusters before the deadline.
- **US-PMU-02 — Program-wide synthesis.** As a PMU lead, I want to generate the annual report (narrative + indicators + cluster summaries) from MARLO so that donor reporting is consistent.
- **US-PMU-03 — Generate AI-assisted narratives.** As a PMU lead, I want the AI Reports Generator service to draft cluster-specific narratives grounded in MARLO data so that I can save time without losing evidence linkage.

### 6.4 Program / CRP Admin

- **US-ADM-01 — Configure phases.** As a Program Admin, I want to configure the POWB / UpKeep / AR phases for a year so that the platform's phase-based data flows reflect the program's actual cycle.
- **US-ADM-02 — Manage roles & partners.** As a Program Admin, I want to assign roles to users by Cluster / Project and manage partner institutions so that access is contextual and accurate.
- **US-ADM-03 — Toggle specificities.** As a Program Admin, I want to enable or disable a feature behavior (specificity) per Global Unit so that programs can opt into capabilities without forking the platform.

### 6.5 Super Admin

- **US-SA-01 — Onboard a new Global Unit.** As a Super Admin, I want a controlled procedure to register a new CRP / Platform / Center so that the program inherits MARLO's governance with its own configuration.
- **US-SA-02 — System health visibility.** As a Super Admin, I want notifications, email tracking, and bulk replication tools so that I can operate the platform at scale.

### 6.6 Public reader

- **US-PUB-01 — Consume public dashboards.** As a public reader, I want to view the program's deliverables and FAIR/Open Access metrics through embedded Power BI dashboards so that the program meets its transparency commitments.

### 6.7 AI service consumer

- **US-AI-01 — Conversational data exploration.** As an authenticated stakeholder, I want to query MARLO data through the chatbot with citations and hyperlinks so that I trust the answer is grounded in MARLO content.
- **US-AI-02 — Innovation extraction.** As a PMU lead, I want the Text Mining service to propose structured innovation candidates from research outputs so that I can validate them rather than transcribing manually.

---

## 7. Acceptance Criteria

Acceptance criteria are stated at the product level. Each module spec under `docs/specs/domain/<module>/` will refine these into concrete, testable rules. Module specs MUST follow the format defined in `docs/specs/general-setup/requirements.md`.

### 7.1 Cross-cutting acceptance

1. **Phase safety** — A save in the current phase MUST replicate forward (current → all future phases) per the rules in `reports/ai-context/persistence-replication-managerimpl.md`. Past phases are immutable.
2. **Save validation** — Every critical save section MUST follow the `Action.validate()` + `Validator` + interceptor stack pattern documented in `reports/ai-context/save-validation-matrix.md`. Sections without a validator (e.g., `PortfolioManagementAction`) MUST be flagged as risk areas before being changed.
3. **Permissions** — Every `.do` action MUST be backed by an interceptor stack that enforces edit rights for the section (see `reports/ai-context/struts-critical-routing-catalog.md`). REST endpoints under `/api/*` MUST authenticate via the dedicated REST security path (Apache Shiro + token).
4. **Auditability** — Significant entity changes MUST be captured by `IAuditLog` / `HibernateAuditLogListener`. Changes to data definitions MUST be tracked through the Freshservice ticketing system.
5. **Migrations** — Every schema change MUST ship as a Flyway migration under `marlo-web/src/main/resources/database/migrations/` using the naming pattern `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`.
6. **Specificities (feature flags)** — New conditional behavior MUST be implemented through the `parameters` / `custom_parameters` flow defined in `AGENTS.md` (constants in both `APConstants.java` files; backend uses `BaseAction.hasSpecificities(...)`; frontend uses `action.hasSpecificities('<key>')`).
7. **i18n** — User-facing strings MUST be placed in `marlo-web/src/main/resources/global.properties` (or program-specific `custom/*.properties`) — never hardcoded in FTL or Java.
8. **Localization of code** — All code, identifiers, and inline comments MUST be in English (per `AGENTS.md`).
9. **License header** — Every new Java file MUST carry the GPL header from `AGENTS.md`.

### 7.2 Quality and security acceptance

1. **Checkstyle** — `mvn checkstyle:check` MUST pass against `configuration/marlo-checkstyle.xml`.
2. **CVEs** — Critical Snyk findings introduced by a change MUST be remediated before merge to `aiccra-staging`.
3. **Dependency baselines (current checkout)** — dependency versions declared in `marlo-parent/pom.xml` MUST NOT be downgraded without explicit approval. Current security-aligned floors include Struts2 ≥ 6.4.0, Tomcat Catalina ≥ 9.0.96, Spring Framework ≥ 5.3.39, and Jackson ≥ 2.17.x. HikariCP 2.4.6 and Groovy 2.4.8 remain documented modernization exceptions until upgraded and validated.
4. **QA completeness** — A change to a critical save section MUST be retested against its `Validator` and its interceptor stack before deploy.

### 7.3 Operational acceptance

1. **CI/CD** — Merges to integration branches MUST trigger the Jenkins pipeline. Slack notifications confirm success/failure (existing convention).
2. **Branching** — `AICCRA` (production) is merge-only from `aiccra-staging`. Direct commits to production are forbidden.
3. **Backups** — The daily MySQL → S3 backup job MUST remain green; monitoring and alerting on failures is a constitutional requirement.
4. **Environments** — Test (CIAT Palmira), Staging (in build-out), Production (AWS Virginia). New features MUST be validated in Test before reaching `aiccra-staging`.

---

## 8. Assumptions, Dependencies, & Constraints

### 8.1 Assumptions

- AICCRA-style programs (multi-cluster, donor-funded, results-based) are the primary product context.
- The Phase model (POWB / UpKeep / AR) reflects how partner programs operate.
- Stakeholders accept the "forward-only replication" rule for phased data.
- Power BI / Microsoft Fabric and AWS (RDS, S3, Lambda, Bedrock) remain the primary analytics and AI substrates.
- Apache Shiro + CGIAR Active Directory remain the identity backbone.

### 8.2 External dependencies

| Dependency | Purpose | Risk class |
|---|---|---|
| CGIAR Active Directory | Authentication | Hard |
| CLARISA | Reference data (institutions, geography, taxonomy) | Hard |
| CGSpace | Open-access deposit and DOI metadata | Soft |
| Power BI / Microsoft Fabric | Results BI + Process Monitoring dashboards | Hard |
| AWS RDS (MySQL) | System of record | Hard |
| AWS S3 | Backups, document storage | Hard |
| AWS Lambda | AI/automation jobs | Soft |
| AWS Bedrock (Claude, Titan) | Chatbot, RAG, embeddings | Soft |
| Amazon OpenSearch | Vector index for Reports Generator | Soft |
| Jenkins (PRMS automation) | CI/CD | Hard |
| Freshservice | Support tickets | Soft |
| Slack | Build notifications | Soft |
| GlobalProtect VPN (production) / FortiClient (testing) | Network access | Hard |

### 8.3 Constitutional constraints

- **Tech stack** — Java 17 + Struts 2 + Hibernate + Spring (REST) + FreeMarker + Tomcat 9 + MySQL. `marlo-parent/pom.xml` verifies the active Java level; Java 8 scripts remain only for legacy branches/profiles.
- **Multi-module Maven** — `marlo-parent` aggregates `marlo-utils`, `marlo-core`, `marlo-data`, `marlo-web`. No new top-level module without a constitutional update.
- **Web framework boundary** — `.do` (Struts) for internal flows; `/api/*` (Spring MVC) for REST. JSON via Struts is *punctual* and limited to existing patterns (per `AGENTS.md`).
- **Phased persistence** — Changes that touch `ManagerImpl` save logic MUST preserve the recursive replication contract (`reports/ai-context/persistence-replication-managerimpl.md`).
- **Code style** — 2-space indent, 120 char line limit, braces on same line, mandatory blocks for `if/while/for/do`, max file length 3500 lines (Checkstyle).
- **License** — GNU GPL.

---

## 9. Open Questions

These are the live, unresolved questions the constitution cannot answer alone. They MUST be resolved (or explicitly deferred) by an `epic` spec before they block product evolution.

1. **Java 8 legacy cleanup** — MARLO currently uses Java 17, while Java 8 run scripts still coexist for legacy branches/profiles. Define when the Java 8 scripts and profiles can be retired.
2. **Frontend modernization** — FreeMarker + jQuery + Bootstrap is the current UI stack. A move toward server-rendered components with progressive enhancement (or partial React islands) has been discussed but not committed. Constitutional default remains FTL + macros.
3. **Public REST API surface** — `/api/*` is currently used by trusted integrations. A formal external partner API (rate-limited, versioned beyond v2, OAuth2) is not yet committed.
4. **Staging environment parity** — Staging is "currently being implemented." Until it mirrors production, hot fixes still validate against Test (CIAT Palmira). Define and ratify the parity contract.
5. **Tenant onboarding playbook** — Onboarding a new Program/Platform is "controlled" today. A repeatable, scripted procedure (and the migration of seed data) would unlock the "MARLO as a Service" promise. Should live as an `epic` spec.
6. **AI services governance** — Citations and grounding are required, but a formal evaluation harness for hallucination rate and SLA on AI services is not yet in place.
7. **Legacy modules retirement** — `powb2019`, `annualReport2018`, and Pentaho/iText components are flagged for semi-annual review. Retire-or-keep decisions need explicit per-module specs.
8. **Per-program visual identity** — Specificities and `custom/*.properties` give per-program text and feature toggles, but the depth of *visual* customization (logo, palette, components) per Global Unit is not yet fully documented. See `docs/system-design/design.md` §12.

---

## 10. Document map

- **PRD (this document)** — Why MARLO exists and what success looks like.
- `docs/system-design/design.md` — UI/UX system blueprint (FTL composition, macros, navigation, design tokens, accessibility).
- `docs/detailed-design/detailed-design.md` — Technical blueprint (modules, data model, API surface, security, observability, testing).
- `docs/specs/general-setup/` — Methodology templates for module specs (`requirements.md`, `design.md`, `task.md`).
- `docs/specs/domain/<module>/` — Module-level SDD specs (e.g., `domain/projects/`, `domain/deliverables/`, `domain/innovations/`, `domain/oicrs/`, `domain/powb/`, `domain/annual-report/`, `domain/qa/`, `domain/admin/`, `domain/auth/`, `domain/bi/`, `domain/ai-services/`).
- `docs/specs/enhancement/<feature>/` — Cross-cutting feature enhancements that don't belong to a single domain.
- `docs/specs/bugfix/<slug>/` — Bug-driven specs requiring a structured trace.
- `docs/specs/epic/<name>/` — Multi-spec initiatives (e.g., `epic/java-17-cutover/`, `epic/tenant-onboarding/`).
- `reports/ai-context/*` — Operational runbooks for routing, validation, persistence, composition. Treat these as authoritative companion docs for module work.
- `AGENTS.md` — Repo-level operational guidance for AI agents (style, file headers, specificity workflow, run scripts).
