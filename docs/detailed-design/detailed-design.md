# MARLO — Detailed Design (Technical Blueprint)

**Status:** Living document. Version: 1.0 (Constitutional Baseline).
**Owner:** IBD Team — Alliance of Bioversity International and CIAT.
**Last Updated:** 2026-04-30.
**Related:** [docs/prd.md](../prd.md), [docs/system-design/design.md](../system-design/design.md), [AGENTS.md](../../AGENTS.md), [reports/ai-context/*](../../reports/ai-context/).

> This document captures the *technical* shape of MARLO: modules, data, APIs, security, observability, and testing. It encodes the patterns the codebase already follows (see `AGENTS.md` and `reports/ai-context/*`) and the architectural commitments that future module specs MUST honor.

---

## 1. System Overview

MARLO is a Java EE web application that:

- Persists transactional research-management data in a phase-aware MySQL database.
- Renders form-driven UIs over Apache Struts 2 + FreeMarker.
- Exposes a Spring MVC REST API under `/api/*` for trusted integrations.
- Enforces authentication and authorization via Apache Shiro + CGIAR Active Directory.
- Replicates phased data forward through a recursive `ManagerImpl` pattern.
- Feeds an external Microsoft Fabric / Power BI ecosystem (Bronze / Silver / Gold).
- Hosts AI services (Text Mining, Reports Generator, Chatbot) on AWS that consume MARLO data via the REST API and the database.
- Runs on AWS for production (Virginia) and on-premise at CIAT Palmira for Test, with Staging coming online.

### 1.1 High-level architecture

```
                +-----------------------+        +---------------------+
                |  CGIAR Active         |        |  CLARISA            |
                |  Directory            |        |  (institutions,     |
                +-----------+-----------+        |  geo, taxonomy)     |
                            |                    +----------+----------+
                       (Shiro auth)                         |
                            v                               | (REST)
+--------------------+  +---v-------------------+   +-------v---------+
|  End-user browser  |->| MARLO Web (Tomcat 9) |-->|  CLARISA client |
|  (FTL + jQuery)    |  | Struts 2 + Spring MVC|   +-----------------+
+--------------------+  +---+-------------------+
                            |
                            | Hibernate (HQL/SQL)
                            v
                   +--------+----------+    +----------------------+
                   |  AWS RDS (MySQL)  |    | AWS S3 (backups,     |
                   +--------+----------+    | document store)      |
                            |               +----------------------+
                            | (extract / pipelines)
                            v
                   +--------+----------+
                   | Microsoft Fabric  |
                   | Lakehouse (Bronze |
                   | / Silver / Gold)  |
                   +--------+----------+
                            |
                            v
                   +--------+----------+    +----------------------+
                   | Power BI Service  |<-->| Public embed tool    |
                   +-------------------+    +----------------------+

                   +-------------------+    +----------------------+
                   | AWS Lambda        |    | AWS Bedrock          |
                   | (AI/automation)   |<-->| (Claude, Titan)      |
                   +--------+----------+    +----------+-----------+
                            |                          |
                            v                          v
                   +--------+--------------------------+----+
                   | Amazon OpenSearch (vector indices)     |
                   +----------------------------------------+
```

### 1.2 Deployment topology

- **Production (AWS, Virginia)** — multiple EC2 instances: core application, frontend, CLARISA integration, automation, backup. Lambda for specific AI/automation tasks. RDS MySQL as system of record. S3 for backups and documents.
- **Test (CIAT Palmira)** — on-premise; reachable via FortiClient VPN.
- **Staging** — being built out to mirror Production behind GlobalProtect VPN.
- **CI/CD** — Jenkins (https://automation.prms.cgiar.org/) is triggered by the GitHub Actions workflows under `.github/workflows/` (`jenkins-trigger*.yml`). Dedicated SonarCloud workflow files are not present in this checkout; treat Sonar/static-analysis execution as CI-environment dependent unless the workflow is added.

---

## 2. Domain Modules & Responsibilities

MARLO is structured as a multi-module Maven project. The aggregator (`marlo-aggregator`, `pom.xml` at repo root) drives a build over five modules.

| Module | Responsibility | Key contents |
|---|---|---|
| `marlo-parent` | Dependency and plugin management. No executable code. | `pom.xml` declares `struts2`, `hibernate`, `spring`, `shiro`, `mysql`, `flyway`, etc. (see `marlo-parent/pom.xml`). |
| `marlo-utils` | Pure utility classes (dates, strings, file processing for Excel/CSV/PDF, JSON helpers, constants). | `org.cgiar.ccafs.marlo.utils.*`. |
| `marlo-core` | Cross-cutting configuration: Shiro security wiring, Hibernate session factory, custom security utilities. | `MarloShiroConfiguration`, `MarloLocalSessionFactoryBean`, `MarloDatabaseConfiguration`. |
| `marlo-data` | Domain layer: JPA/Hibernate entities (`model`), `dao`/`mapper`, Manager interfaces and implementations. Audit listeners. | `org.cgiar.ccafs.marlo.data.{model,dao,manager,mapper}`; `IAuditLog`, `HibernateAuditLogListener`, `AuditColumnHibernateListener`. |
| `marlo-web` | Web tier: Struts actions, REST controllers, FreeMarker templates, validators, interceptors, Spring MVC config, web resources, SQL migrations. | `org.cgiar.ccafs.marlo.action.*`, `rest.controller.v2.*`, `validation.*`, `interceptor.*`. |

### 2.1 Action package map (`marlo-web`)

Top-level Struts action packages and their domains:

- `action/ai` — AI-related screens (e.g., `AiAction`).
- `action/annualReport` — Annual Report synthesis sections.
- `action/bi` — BI dashboard hub (`BiReportsAction`).
- `action/center` — CGIAR Center program flows.
- `action/crp` — CRP-level admin and shared flows.
- `action/deliverable` — Deliverable lifecycle screens.
- `action/downloads` — File/report downloads.
- `action/funding` — Funding sources.
- `action/home` — Home dashboard.
- `action/impactpathway` — Impact pathway management.
- `action/json` — Existing JSON endpoints (use punctually only).
- `action/powb` — POWB synthesis sections.
- `action/projects` — Project / cluster CRUD (Description, Partners, Locations, Activities, Deliverable, Innovation, Highlight, Case Study, Outcomes, Budgets…).
- `action/publications` — Publication flows.
- `action/qa` — Quality Assurance flows.
- `action/report` — Reporting (legacy, see also `annualReport`).
- `action/summaries` — PDF / export summaries.
- `action/superadmin` — System-level administration.
- `action/synthesis` — Cross-CRP synthesis.
- `action/tip` — Technology Innovation Profile.
- `action/BaseAction.java` — Common base class (inject `APConfig`, exposes `hasSpecificities`, current user/phase/session helpers).
- `action/MapGeolocation.java` — Geolocation helper action.
- `action/UnhandledExceptionAction.java` — Global error handler.

### 2.2 REST package (`marlo-web/src/main/java/.../rest`)

- `rest/controller/v2/controllist/` — REST endpoints (e.g., `Deliverables`, `Innovations`, `ExpectedStudies`, `Projects`, `QAToken`, `Institutions`, `GeneralLists`, `Expenditures`, `ProgressTowards`, `SrfLists`).
- `rest/controller/v2/controllist/items/` — sub-resource endpoints.
- `rest/dto/` — Data transfer objects (separated from `data.model` entities).
- `rest/mappers/` — MapStruct mappers between entities and DTOs.
- `rest/services/` — Service-layer logic for REST.
- `rest/errors/` — REST error handling and standardized responses.

### 2.3 Configuration packages (`marlo-web` core)

- `config/ApplicationContextConfig.java` — Spring application context.
- `config/MarloRestApiConfig.java` — Spring MVC for `/api/*`.
- `config/MarloSwaggerConfiguration.java` — Springdoc OpenAPI.
- `config/MarloFlywayConfiguration.java` — Flyway migrations.
- `config/MarloBusinessIntelligenceConfiguration.java` — BI config.
- `config/WebAppInitializer.java` — Servlet container bootstrap.
- `interceptor/` — Struts interceptors (auth, CRP validation, edit gates).
- `validation/` — Action-side validators (e.g., `ProjectDescriptionValidator`).
- `security/` — App-level security helpers complementing Shiro.

---

## 3. Data Model & Entities

### 3.1 System of record

- **Engine:** MySQL (AWS RDS in production).
- **ORM:** Hibernate 5.4.x via JPA-style annotations on entities under `marlo-data/.../data/model`.
- **Connection pool:** HikariCP (current checkout: 2.4.6; upgrade to 5.x is a modernization target that requires compatibility validation).
- **Schema migrations:** Flyway 4.0.1 (`marlo-web/src/main/resources/database/migrations/`); no raw schema changes outside migrations.

### 3.2 Entity scale

The data layer holds **544 entity classes**, **456 manager interfaces** (with corresponding `*Impl`), and **455 DAOs** at the time of writing. Every persisted entity follows the layered pattern (see §3.4).

### 3.3 Phase-aware data model

Phases are first-class. The `phases` table enumerates every cycle moment (POWB / UpKeep / AR per year). Phased entities (deliverables, partners, innovations, OICRs, etc.) carry an explicit `phase_id` foreign key.

**Replication contract** (per `reports/ai-context/persistence-replication-managerimpl.md`):

1. Saves are forward-only: changes apply to current and future phases, never past.
2. From `PLANNING`, replicate through every `phase.getNext()` until exhausted.
3. From `REPORTING`, replicate to `phase.getNext().getNext()` (UpKeep) and onward.
4. Delete operations mirror save operations across the same phase chain.
5. Section-specific skip rules apply (e.g., publication deliverables skip replication).
6. Duplicate prevention filters target phases before insert.

Any change to a `ManagerImpl` save path MUST preserve this contract. Add tests that exercise the replication chain end-to-end.

### 3.4 Layered persistence pattern

For every persisted entity:

- **Manager interface** — defines business operations (e.g., `DeliverableFundingSourceManager`).
- **ManagerImpl** — implements business logic, including phase replication.
- **DAO interface** — defines persistence operations.
- **MySQLDAO** — concrete Hibernate implementation (HQL / SQL queries).

This pattern is non-negotiable: new entities MUST follow it. New code MUST NOT bypass the manager layer to talk to the DAO directly.

### 3.5 Audit logging

- `IAuditLog` interface marks auditable entities.
- `HibernateAuditLogListener` and `AuditColumnHibernateListener` (in `marlo-data/`) capture changes.
- Audit columns (`created_by`, `modified_by`, `active_since`, etc.) are populated automatically.
- Significant write paths MUST keep audit columns intact.

### 3.6 Reference data

- CLARISA-backed: institutions, countries / regions, partner types, indicator taxonomies. Synchronization is integration-driven; do NOT hand-edit these tables.
- Internal taxonomies: SLOs, IDOs, cross-cutting issues, Scaling Readiness levels, deliverable types, OICR maturity classes — managed via Admin / Super Admin screens and migrations.

### 3.7 Specificity / parameter tables

- `parameters` — one row per (`global_unit_type_id`, `key`) feature flag definition.
- `custom_parameters` — one row per (`parameter_id`, `global_unit_id`) override.
- `category = '2'` and `format = '1'` mark a specificity (boolean-like).

The constitutional flow for adding a specificity is documented in `AGENTS.md` §"Specificity Implementation Guide" and MUST be followed.

---

## 4. API Surface & Contracts

### 4.1 Two routing layers

| Layer | Pattern | Owner | Purpose |
|---|---|---|---|
| Struts 2 | `*.do`, occasionally `*.json` | Internal UI flows | All form-driven user interactions. |
| Spring MVC | `/api/*` | External consumers | REST API for trusted integrations (CLARISA, AI services, BI ingestion). |

`struts.xml` enforces the boundary: `struts.action.excludePattern = /api/*`.

### 4.2 Internal Struts routing

- Mapping files: `struts.xml` + per-domain `struts-<area>.xml` (e.g., `struts-projects.xml`, `struts-admin.xml`, `struts-powb.xml`, `struts-annualReport.xml`, `struts-impactPathway.xml`, `struts-fundingSources.xml`, `struts-bi.xml`, `struts-ai.xml`, `struts-qa.xml`, `struts-superadmin.xml`, `struts-publications.xml`, `struts-studies.xml`, `struts-summaries.xml`, `struts-synthesis.xml`, `struts-tip.xml`, `struts-data.xml`, `struts-home.xml`, `struts-json.xml`, `struts-api.xml`, `struts-report.xml`, `struts-center-*.xml`, plus legacy `struts-powb2019.xml` / `struts-annualReport2018.xml`).
- Critical routing catalog: see `reports/ai-context/struts-critical-routing-catalog.md`.

### 4.3 Interceptor stacks

Defined under `<package name="marlo-default" namespace="/" extends="struts-default">` in `struts.xml`. Key stacks (per `reports/ai-context/interceptor-validator-playbook.md`):

- `crpAdminStack` — auth/session + admin access + `canEditCrpAdmin`.
- `impactPathwayStack` — auth/session + `canEditImpactPathway`.
- `editProjectsStack` — auth/session + `canEditProject`.
- `editFSStack` — auth/session + `editFunding`.
- `editPowbStack` — auth/session + `canEditPowbSynthesis`.
- `editReportSynthesisStack` — auth/session + `canEditReportSynthesis`.
- `editProjectListStack` — lightweight, used before per-action interceptors (`editDeliverable`, `editInnovation`, `editAi`, `editHighlight`, `editCaseStudy`, `editProjectOutcome`).
- `homeStack` — landing/dashboard stack.
- `projectListStack` — includes `SecurityControl`; used for existing JSON endpoints.

Constitutional rules:

1. Every new `.do` action MUST declare an interceptor stack — no anonymous defaults for write paths.
2. Stacks MUST run permission checks (`canEdit*`, `editFunding`, `accessibleAdmin`) before action execution.
3. Action-side `validate()` MUST be guarded with `if (save) { ... }` and call the matching `Validator`.

### 4.4 REST API (Spring MVC, `/api/v2/*`)

- Controllers live in `rest/controller/v2/controllist/`.
- DTOs are explicit; entities are not serialized directly.
- MapStruct mappers (`rest/mappers/`) translate entities ↔ DTOs.
- Errors flow through `rest/errors/`.
- OpenAPI is provided by Springdoc (replaces legacy Springfox/Swagger).
- Authentication: REST uses Shiro-backed token-based auth (e.g., `QAToken` controller). Public-write surfaces are not constitutional.

API design principles:

1. Resources are nouns; verbs come from HTTP methods.
2. Versioning is path-based (`/api/v2/...`); breaking changes increment the version.
3. Pagination, filtering, and sort parameters follow consistent query-string conventions.
4. Errors are JSON with stable `code` + human-readable `message` fields.

### 4.5 Existing Struts JSON endpoints

`*.json` extensions are configured (`struts.action.extension = do,,json`). They exist for legacy reasons and are scoped to existing patterns. Per `AGENTS.md`: do NOT introduce new JSON paths unless an existing pattern in the same module mandates it.

---

## 5. Backend Workflows & Business Rules

### 5.1 Save pipeline (critical)

Per `reports/ai-context/save-validation-matrix.md` and `interceptor-validator-playbook.md`:

```
HTTP request -> Struts mapping -> Interceptor stack -> Action.validate() -> Validator -> Action.save() / Manager save chain
```

For every critical save section:

1. The interceptor stack runs first (auth, session, edit rights).
2. Struts triggers `validate()`.
3. The action guards with `if (save) { ... validator.validate(this, entity, true); }`.
4. The validator populates invalid fields and action errors.
5. The action checks `hasErrors()` / invalid field map and either returns INPUT or proceeds to persist.
6. The Manager's save method persists the current entity, then recursively replicates forward (§3.3).
7. The audit listeners record the change.

### 5.2 Phase replication rules (recap)

- Planning save → replicate to all next phases.
- Planning delete → remove from all next phases.
- Reporting save → replicate to upkeep (`next.next`) and forward.
- Reporting delete → remove from upkeep chain forward.
- Section-specific skip flags (e.g., `isPublication`) MUST be honored.

### 5.3 Specificity gating

Branch behavior on a specificity flag:

```java
if (this.hasSpecificities(APConstants.MY_SPECIFICITY_KEY)) {
  // enabled behavior
} else {
  // default behavior
}
```

In FTL:

```ftl
[#if action.hasSpecificities('my_specificity_key')]
  [#-- enabled fragment --]
[/#if]
```

The constant value MUST equal the `parameters.key`. Both `APConstants.java` files (one in `marlo-data/`, one in `marlo-web/`) MUST be kept in sync.

### 5.4 Notifications and emails

- Notifications are tracked in dedicated tables and exposed in Admin.
- Outbound email templates are stored under `marlo-web/src/main/resources/template/`.
- Email tracking and delivery state are visible to admins.
- Real-time UI notifications use Pusher (`pusher-app.js`).

### 5.5 Background jobs

- Quartz scheduling (`quartz.properties`) runs periodic tasks (e.g., AICCRA-specific reminders, automation triggers).
- Daily MySQL → S3 backup is operated outside the JVM (scripted on the AWS side).
- AWS Lambda functions handle AI-side processing and async ingestion to OpenSearch.

---

## 6. Frontend Architecture & State Boundaries

### 6.1 Stack

- **Templating:** FreeMarker 2.x (`.ftl`) — pull-based MVC with Struts2.
- **Styling:** custom CSS over Bootstrap. Sources under `marlo-web/src/main/webapp/global/css/`.
- **JS libraries:** managed via Bower (`bower_components/`) plus targeted modules in `marlo-web/src/main/webapp/global/js/` and `crp/js/`.
- **Build:** Grunt (`Gruntfile.js`) for asset orchestration.

### 6.2 Composition (per `reports/ai-context/frontend-composition-map.md`)

- `[#include]` for layout fragments (`header.ftl`, `main-menu.ftl`, `footer.ftl`, `breadcrumb.ftl`, `messages.ftl`, area submenus).
- `[#import ... as alias]` for macro libraries (`forms.ftl` → alias `customForm`, area-specific macro files).
- Macro calls (`[@customForm.input ... /]`) for form rendering.
- Hidden template macros (`isTemplate=true`) for repeated blocks (cloned/reindexed by JS).

### 6.3 State boundaries

- **Server state** = canonical. The Action populates form-bound objects (e.g., `project`, `deliverable`, `powbSynthesis`, `reportSynthesis`); the form posts indexed collections back.
- **Client state** = transient form view. `autoSave.js` periodically posts drafts; `fieldsValidation.js` provides client-side validation; `discardChangesPopup.ftl` guards against accidental loss.
- **No SPA.** The constitutional default is full-page navigation between `.do` actions. AJAX/JSON is reserved for existing patterns only.
- **No client-side routing.** URL state is server-driven.

### 6.4 Reusable UI primitives

See `docs/system-design/design.md` §8. Constitutional rule: extend existing macros before introducing new components.

---

## 7. Integration Points

| Integration | Direction | Mechanism | Purpose |
|---|---|---|---|
| CGIAR Active Directory | Inbound (auth) | Shiro realm | Enterprise SSO. |
| CLARISA | Outbound | REST | Reference data (institutions, geography, taxonomy). |
| CGSpace | Outbound | REST | Open-access deposit metadata, DOI lookup. |
| Power BI Service / Microsoft Fabric | Outbound (data + embed) | DB extracts + REST + JS embed | Dashboards. |
| AWS S3 | Outbound | AWS SDK | Backups, document storage. |
| AWS Bedrock (Claude, Titan) | Outbound | AWS SDK | RAG generation, embeddings. |
| Amazon OpenSearch | Outbound | AWS SDK | Vector indices for Reports Generator. |
| Pusher | Outbound | WebSocket-like | Real-time UI notifications. |
| Pentaho Reporting | Embedded | Library | PDF / report generation. |
| iText | Embedded | Library | PDF assembly. |
| Apache POI | Embedded | Library | Excel import/export. |
| Trumbowyg, Chosen, Pickadate, DataTables, Cytoscape | Embedded (frontend) | Bower-managed | Form widgets, tables, graphs. |
| Jenkins | Outbound (CI) | HTTP webhook | Build pipeline trigger. |
| SonarCloud | Outbound (CI) | GitHub Action | Static analysis. |
| Slack | Outbound | Webhook | Build notifications. |
| Freshservice | External | UI | Support tickets and access requests. |

Integration contracts MUST be explicit. Module specs touching an integration MUST cite the contract version and any rate-limit / SLA assumptions.

---

## 8. Security & Authorization Model

### 8.1 Authentication

- **Primary:** Apache Shiro 1.13.0 with a CGIAR Active Directory realm (`adauth-5.6.jar` packaged under `marlo-web/src/main/resources/libs`).
- **Fallback:** Internal users with MD5-hashed passwords (legacy; new accounts SHOULD use AD when possible).
- **Session:** server-side, container-managed; protected by Shiro session manager.
- **Production network:** GlobalProtect VPN required.
- **Test network:** FortiClient VPN required.

### 8.2 Authorization layers

1. **Authentication (`users` table)** — identity.
2. **Project access (`crp_users` table)** — which Programs/Platforms a user can access.
3. **Role assignment (`user_role` table)** — which permissions the user holds via roles.

Roles (per Technical Overview §15.1):

- Super Admin — system-wide.
- Admin — project-level (Program/Platform/Center).
- Project / Cluster Leader — strategic leadership.
- Project / Cluster Coordinator — operational coordination.
- PMU — administrative & compliance.
- QA / Reviewer — phase-level QA.
- Guest User — restricted external collaboration.

Cluster scope is established via Partner Institutions, ensuring contextual access.

### 8.3 Edit gates (per-section)

Interceptors (`canEditProject`, `canEditAi`, `canEditDeliverable`, `editFunding`, `canEditPowbSynthesis`, `canEditReportSynthesis`, `canEditImpactPathway`, `canEditCrpAdmin`, `accessibleAdmin`, `canEditCrp`, `canEditSynthesis`, `canEditPublication`, `editInnovation`, `editHighlight`, `editCaseStudy`, `editProjectOutcome`, `editFunding`) gate mutating actions before they reach the action class.

### 8.4 REST authentication

- `/api/*` endpoints authenticate via tokens (e.g., `QAToken`) wired through Shiro.
- DTO boundaries prevent accidental exposure of internal fields.
- `errors/` package provides standardized 4xx / 5xx responses.
- Public unauthenticated read endpoints, if any, MUST be explicitly enumerated in their controller and reviewed in module specs.

### 8.5 Web security hardening and dependency baseline

`marlo-parent/pom.xml` is the source of truth for dependency versions in the active checkout. The current security-aligned baseline includes:

- Apache Struts2 ≥ 6.4.0.
- Tomcat Catalina ≥ 9.0.96.
- Spring Framework ≥ 5.3.39.
- Jackson ≥ 2.17.x.
- Springdoc OpenAPI is present; legacy Swagger/Springfox artifacts may still exist until dependency cleanup is completed.

Known modernization exceptions in this checkout:

- HikariCP remains at 2.4.6.
- Groovy remains at 2.4.8.

Do not claim HikariCP ≥ 5.x or Groovy ≥ 2.4.21 until those upgrades are implemented and validated. Downgrades from the versions declared in `marlo-parent/pom.xml` require an explicit, documented rationale and PMU sign-off.

### 8.6 Data security

- Database credentials are environment-scoped (`marlo-${profile}.properties`).
- Property files with credentials (e.g., `marlo-dev.properties`) are git-ignored.
- Backups encrypted at rest (S3 SSE).
- Network access controlled by VPN policy.

### 8.7 Vulnerability management cadence

- Snyk scans on the monorepo each release cycle.
- Semi-annual review of legacy components flagged in §16.4 (Pentaho, iText).
- New critical CVEs introduced by a change block merge to `staging`.

---

## 9. Error Handling & Observability

### 9.1 Logging

- SLF4J + Logback (`logback.xml`).
- Per-class logger (`LoggerFactory.getLogger(...)`).
- `INFO` for high-level flow, `WARN` for recoverable anomalies, `ERROR` for failures with stack trace.
- No `System.out` / `printStackTrace` in production code.

### 9.2 Action-level error handling

- `UnhandledExceptionAction` is the global Struts fallback.
- Error views: `WEB-INF/global/views/403.ftl`, `404.ftl`, `500.ftl`.
- User-visible errors flow through the page-level message banner (`messages.ftl`); never expose stack traces.

### 9.3 REST error handling

- Centralized in `rest/errors/`.
- Errors return JSON with stable shape (`code`, `message`, optional `details`).
- 4xx for client errors; 5xx for server errors.
- DTO validation errors include the field path.

### 9.4 Observability

- **Operational dashboards** — QA Module and Cluster Completeness Status (Power BI) provide near-real-time operational visibility.
- **Application metrics** — current logging is the primary signal; dedicated APM (e.g., New Relic, Elastic APM) is an open gap.
- **Alerting** — Slack integration for build failures; production runtime alerting is operationally managed and an open gap for this constitution.
- **Backups** — daily backup status is tracked operationally; failures should escalate to the support team.

### 9.5 Rate limiting

- Internal `.do` actions inherit Tomcat-level concurrency limits.
- REST endpoints are not currently rate-limited at the application layer — an open gap if external partner usage grows.

---

## 10. Testing Strategy

### 10.1 Static analysis

- **Checkstyle** — `mvn checkstyle:check` against `configuration/marlo-checkstyle.xml`. Gate for merges.
- **SonarCloud/static analysis** — CI-environment dependent in this checkout; no dedicated `sonarcloud.yml` or `sonartest.yml` workflow file is present.
- **Snyk** — security scans in the modernization cycle.

### 10.2 Unit tests

- Java unit tests use JUnit 4.13.2 (`junit.version` in `marlo-parent/pom.xml`).
- Manager-layer logic SHOULD have unit tests, especially for phase replication rules.
- Existing coverage is uneven across the data layer; new specs MUST add tests for any new save path.

### 10.3 Integration / regression tests

- QA team operates a structured manual test plan (see Technical Overview §11) covering functional, regression, UI, compatibility scenarios.
- Test cases live in Jira (Xray-style). Defects flow through Jira lifecycle (report → fix → retest → close).
- Constitutional commitment: changes to a critical save section (per `reports/ai-context/save-validation-matrix.md`) MUST be retested against their `Validator` and interceptor stack.

### 10.4 Non-functional testing

- Load/stress/performance/usability testing combine black-box, white-box, and grey-box methodologies (Technical Overview §11).
- Load tests focus on end-of-cycle peaks (annual planning and reporting deadlines).

### 10.5 BI testing

- Each BI release passes through Dev / Test / Prod deployment pipelines with dedicated QA validation per stage.

### 10.6 AI service testing

- AI services include grounding rules: every chatbot response cites sources. A formal hallucination-rate evaluation harness is an open gap (see PRD §9).

---

## 11. Technical Constraints & Assumptions

### 11.1 Hard constraints

1. **Java + Struts2 + Hibernate + Spring + FreeMarker + Tomcat 9 + MySQL** — the constitutional stack. Module specs MUST work within it.
2. **GPL license** — all source contributions inherit the project's GPL license; the file header in `AGENTS.md` is mandatory.
3. **Code style** — Eclipse formatter (`configuration/ccafs-java-style-config.xml`), 2-space indent, 120 char line limit, braces on same line, mandatory blocks for `if/while/for/do`. Max file length 3500 lines (Checkstyle).
4. **Naming** — package regex `^[a-z]+(\\.[a-z][a-z0-9]*)*$`; `Action` suffix mandatory for Struts actions (Struts convention).
5. **English only** — code, identifiers, inline comments. User-facing content is i18n-keyed (per `AGENTS.md`).
6. **Phased data is forward-only** — past phases are immutable.
7. **Multi-module Maven layout** — adding a top-level module is a constitutional event.
8. **Spring MVC owns `/api/*`** — Struts is excluded from this prefix.
9. **Branch protection** — `main` (production) accepts merges only from `staging`. Feature branches start from `staging`.

### 11.2 Soft constraints (assumptions that may evolve)

- The platform targets a desktop-first browser experience.
- The Power BI / Microsoft Fabric ecosystem remains the analytics substrate.
- AWS remains the production cloud.
- Partner programs follow a phased annual cycle (POWB / UpKeep / AR).

### 11.3 Run scripts

- MARLO currently uses Java 17.
- Use `scripts/run-marlo-java17.sh` (or `.bat`) for local runs.
- Use `marlo-parent/pom.xml` to verify the active Java level if this changes in a future branch.
- Use `scripts/run-marlo-java8.sh` (or `.bat`) only for legacy Java 8 branches/profiles.
- Local property files (`marlo-dev.properties`) are gitignored; bootstrap from `marlo-test.properties` template.

### 11.4 Environment / configuration

- Spring profiles: `dev`, `api`, `pro`, `test`. The active profile selects `marlo-${spring.profiles.active}.properties`.
- Local Tomcat via Cargo Maven plugin; production Tomcat 9 on EC2.

---

## 12. Architecture Decision Records (ADR snapshots)

These are the decisions that shape MARLO at constitution time. Future ADRs SHOULD live under `docs/specs/general-setup/decisions/` (or per-module spec `decisions/` folders) and reference this baseline.

| # | Decision | Status | Notes |
|---|---|---|---|
| ADR-1 | Struts 2 for internal flows; Spring MVC for `/api/*` | Accepted | Boundary enforced by `struts.action.excludePattern`. |
| ADR-2 | FreeMarker as the view engine | Accepted | All views are `.ftl`; no JSP for new work. |
| ADR-3 | Hibernate with manual layered managers/DAOs | Accepted | No JPA repositories or Spring Data; preserve the explicit pattern. |
| ADR-4 | Forward-only phase replication in `ManagerImpl` | Accepted | Recursive across `phase.getNext()`. |
| ADR-5 | Specificity feature flags via `parameters` + `custom_parameters` | Accepted | Standard feature-flag mechanism for per-Global-Unit behavior. |
| ADR-6 | Apache Shiro for authentication and authorization | Accepted | CGIAR AD is the primary realm; internal MD5 fallback retained for legacy. |
| ADR-7 | Microsoft Fabric Lakehouse + Power BI for analytics | Accepted | Bronze / Silver / Gold; refresh every 8 hours (Results) / 30 minutes (QA). |
| ADR-8 | AWS Bedrock (Claude + Titan) + OpenSearch for AI services | Accepted | Multi-stage RAG pipelines; responses cite sources. |
| ADR-9 | Jenkins-driven CI/CD with Slack notifications | Accepted | Triggered by GitHub Actions (`jenkins-trigger.yml`). |
| ADR-10 | Dependency baseline follows the active POM | Accepted | Current floors and modernization exceptions are documented in §8.5. |
| ADR-11 | Light theme only | Accepted | See `docs/system-design/design.md` §11. |
| ADR-12 | No new Struts JSON paths beyond existing patterns | Accepted | Per `AGENTS.md` scope guardrails. |
