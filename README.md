# MARLO

**Managing Agricultural Research for Learning and Outcomes**

[![License: GPL v3](https://img.shields.io/badge/license-GPLv3-blue.svg)](./LICENSE)
[![GitHub issues](https://img.shields.io/github/issues/CCAFS/MARLO.svg)](https://github.com/CCAFS/MARLO/issues)

MARLO is an open-source, web-based research management platform that supports the full Planning, Monitoring, Reporting, and Learning (PMRL) cycle of complex multi-country, multi-stakeholder research programs. Originally built within the CGIAR system and now actively maintained by the IBD Team at the Alliance of Bioversity International and CIAT, MARLO is the system of record for annual planning, deliverables, innovations, outcome impact case reports (OICRs), and indicator contributions across one or more research programs.

This repository hosts the **AICCRA** production line of MARLO. The platform has been deployed at scale on programs such as **CCAFS** (2017+) and **AICCRA** (2021–present), where it powered planning and reporting for 11 clusters across multiple African countries and supported the generation of 1,440+ knowledge products with 95%+ open-access compliance and 92%+ quality ratings.

---

## Why MARLO

MARLO replaces the spreadsheets, email chains, and fragmented reporting that complex research programs typically rely on. It provides:

- A single, phase-aware system of record (POWB / UpKeep / Annual Report).
- Embedded quality assurance with structured reviewer feedback.
- Built-in Open Access and FAIR compliance tracking.
- A multi-tenant model for running multiple Programs / Platforms / Centers on one platform.
- An analytics path through Microsoft Fabric (Bronze / Silver / Gold) and Power BI.
- AI-augmented services (Text Mining, Reports Generator, Chatbot) that consume MARLO data without bypassing its governance.
- Audit-friendly forward-only phase replication: past data is immutable, future data inherits planned changes.

For the full product framing, see [`docs/prd.md`](./docs/prd.md).

---

## Core capabilities

- **Annual Work Plan and Budget (AWPB)** — planning per cluster / project.
- **Progress Monitoring** — continuous, intra-year tracking.
- **Annual Reporting (AR)** — narrative + structured indicators + cluster synthesis.
- **Deliverables Management** — full lifecycle, Open Access, FAIR principles.
- **Innovation Tracking** — Scaling Readiness 0–9, typology, geographic tagging.
- **Outcome Impact Case Reports (OICRs)** — structured documentation with maturity classification.
- **Quality Assurance** — structured feedback workflows between reviewers and implementers.
- **Funding & Partner Management** — CLARISA-integrated.
- **Impact Pathway Management** — SLOs, IDOs, cross-cutting issues.
- **Business Intelligence** — embedded Power BI dashboards (results, QA, completeness, public).
- **AI Services** — text mining, narrative generation, conversational data exploration (AWS Bedrock + Claude + Titan + OpenSearch).
- **Multi-tenant Administration** — onboard a new Program / Platform / Center with its own roles, phases, partners, locations, and per-program feature flags ("specificities").

---

## Technology stack

| Concern | Technology |
|---|---|
| Language | Java (8 and 17 branches coexist) |
| Web framework (internal flows) | Apache Struts 2 (≥ 6.4.0) |
| Web framework (REST API) | Spring MVC under `/api/*` (Spring ≥ 5.3.39) |
| ORM | Hibernate 5.4.x |
| Database | MySQL (AWS RDS in production) |
| Connection pool | HikariCP (≥ 5.x) |
| Servlet container | Apache Tomcat 9 (≥ 9.0.96) |
| Templating | FreeMarker (`.ftl`) |
| Build | Apache Maven (multi-module) + Grunt (frontend assets) |
| DB migrations | Flyway |
| Security | Apache Shiro 1.13.0 + CGIAR Active Directory |
| Dependency injection | Google Guice + Spring |
| API docs | Springdoc OpenAPI |
| Frontend assets | Bower (jQuery, Bootstrap, DataTables, Chosen, Pickadate, Trumbowyg, Cytoscape, Font Awesome, etc.) |
| Real-time notifications | Pusher |
| Reporting / export | Pentaho Reporting, iText, Apache POI |
| Analytics | Microsoft Fabric (Lakehouse, Bronze/Silver/Gold) + Power BI |
| AI | AWS Bedrock (Claude, Titan), Amazon OpenSearch, AWS Lambda |
| CI/CD | GitHub Actions → Jenkins; SonarCloud; Snyk |

Dependency floors (post-January 2026 SETI security modernization) MUST NOT be downgraded — see [`docs/detailed-design/detailed-design.md`](./docs/detailed-design/detailed-design.md) §8.5.

---

## Repository layout

```
MARLO/
├── marlo-parent/        Root Maven aggregator: dependency versions + plugin config (no executable code)
├── marlo-utils/         Pure utility classes (dates, strings, Excel/CSV/PDF, JSON helpers)
├── marlo-core/          Cross-cutting configuration: Shiro, Hibernate session factory, DB config
├── marlo-data/          Domain layer: 540+ JPA entities, Manager/ManagerImpl, DAO/MySQLDAO, audit listeners
├── marlo-web/           Web tier: Struts actions, Spring REST controllers, FTL templates, validators,
│                        interceptors, JS/CSS, SQL migrations
├── docs/                Constitutional documents (PRD, system design, detailed design, spec templates)
├── reports/ai-context/  Operational runbooks for routing, save/validation, persistence, composition
├── scripts/             Run scripts (Java 8 and Java 17 variants), property updaters
├── configuration/       Code style, Checkstyle, formatter configs
├── .github/workflows/   GitHub Actions: Jenkins trigger, SonarCloud
├── AGENTS.md            Operational ground truth for AI/human contributors
├── CLAUDE.md            Entry point for AI assistants — points to AGENTS.md and docs/
└── EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md  Debugging runbook for accordion-style UIs
```

For the module breakdown in detail, see [`docs/detailed-design/detailed-design.md`](./docs/detailed-design/detailed-design.md) §2.

---

## Quick start (local development)

### Prerequisites

- Java 8 (default branches) **or** Java 17 (branches whose name contains `java17` / `java_17`).
- Maven 3.8.x.
- MySQL 8.x (local instance for development).
- Network access to a CIAT environment if connecting to shared test data:
  - Test (CIAT Palmira): **FortiClient VPN**.
  - Staging / Production (AWS): **GlobalProtect VPN**.

### Clone and build

```bash
git clone https://github.com/CCAFS/MARLO.git
cd MARLO
git checkout staging   # branch off here for new work
```

### Configure local properties

The properties files holding credentials are gitignored. Bootstrap from the provided template:

```bash
cp marlo-web/src/main/resources/config/marlo-test.properties \
   marlo-web/src/main/resources/config/marlo-dev.properties
# edit marlo-dev.properties to point at your local MySQL and credentials
```

Active Spring profile selects which file is loaded: `marlo-${spring.profiles.active}.properties`. Available profiles: `dev`, `api`, `pro`, `test`.

### Run

Use the run script that matches the branch's Java version:

```bash
# Java 8 branches
scripts/run-marlo-java8.sh           # macOS / Linux
scripts/run-marlo-java8.bat          # Windows

# Java 17 branches (name contains java17 / java_17)
scripts/run-marlo-java17.sh
scripts/run-marlo-java17.bat
```

Flyway migrations apply automatically on Tomcat startup. The app comes up on the local Cargo Tomcat instance.

### Quality gates

```bash
mvn checkstyle:check     # mandatory before commit
mvn test                  # unit tests
```

SonarCloud and Snyk run automatically on push / PR via GitHub Actions.

---

## Branching & release model

| Branch | Role | Rules |
|---|---|---|
| `main` | Production | Merge-only from `staging`. **No direct commits.** |
| `staging` | Stable integration | All feature branches merge here. |
| `dev` | Experimentation | Highly unstable; integration testing only. |
| `<TICKET-ID>-<Description>` | Feature branches | Created from `staging`; merged back into it. Use a descriptive `<slug>` when the work has no ticket. |

CI/CD: a push to any branch triggers the Jenkins job `marlo-<branch-suffix>`. Slack receives success/failure notifications.

---

## Documentation map

The repository follows a **Spec-Driven Development (SDD)** methodology. The documents below form the constitutional baseline; module-level work lives under `docs/specs/`.

### Constitutional baseline

- [`AGENTS.md`](./AGENTS.md) — operational ground truth: language rules, file headers, code style, Checkstyle, migration naming, specificity workflow, file organization, run scripts.
- [`CLAUDE.md`](./CLAUDE.md) — entry point for AI assistants; lists the 12 hard rules and the doc-reading order.
- [`docs/prd.md`](./docs/prd.md) — Product Requirements: problem, personas, goals, success metrics, scope, user stories, acceptance, assumptions, open questions.
- [`docs/system-design/design.md`](./docs/system-design/design.md) — UI/UX system blueprint: information architecture, screen inventory, navigation, layout patterns, components, accessibility.
- [`docs/detailed-design/detailed-design.md`](./docs/detailed-design/detailed-design.md) — technical blueprint: modules, data model, phase replication contract, API surface, save pipeline, security, observability, testing, ADR snapshots.

### Spec methodology and taxonomy

Every module spec under `docs/specs/` MUST follow these templates:

- [`docs/specs/general-setup/requirements.md`](./docs/specs/general-setup/requirements.md)
- [`docs/specs/general-setup/design.md`](./docs/specs/general-setup/design.md)
- [`docs/specs/general-setup/task.md`](./docs/specs/general-setup/task.md)

Spec folders live under:

- `docs/specs/domain/<module>/` — module-level specs (projects, deliverables, innovations, OICRs, POWB, annual report, QA, admin, auth, BI, AI services).
- `docs/specs/enhancement/<feature>/` — cross-cutting features.
- `docs/specs/bugfix/<slug>/` — bug-driven specs needing structured trace.
- `docs/specs/epic/<name>/` — multi-spec initiatives (e.g., `epic/java-17-cutover/`, `epic/tenant-onboarding/`).

### Operational runbooks

The `reports/ai-context/` folder holds authoritative operational guides for the most-touched flows:

- [`frontend-composition-map.md`](./reports/ai-context/frontend-composition-map.md) — FTL composition (`#include`, `#import`, macros, expandable-block templates).
- [`save-validation-matrix.md`](./reports/ai-context/save-validation-matrix.md) — save pipeline matrix for the 10 critical sections.
- [`persistence-replication-managerimpl.md`](./reports/ai-context/persistence-replication-managerimpl.md) — phase replication contract in `ManagerImpl` save / delete chains.
- [`struts-critical-routing-catalog.md`](./reports/ai-context/struts-critical-routing-catalog.md) — Struts routing catalog with action classes and view results.
- [`interceptor-validator-playbook.md`](./reports/ai-context/interceptor-validator-playbook.md) — interceptor stacks and `Action.validate()` patterns.
- [`EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md`](./EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md) — debugging accordion-style list UIs.

---

## Hard rules (non-negotiable)

These rules are constitutional. Deviations require an explicit, justified Decision Log entry inside the relevant module spec.

1. **Phased data is forward-only.** Saves replicate to current and future phases; past phases are immutable.
2. **Save pipeline pattern** is mandatory for critical sections: `Action.validate()` guarded by `if (save)` → `Validator` → manager save chain.
3. **Spring MVC owns `/api/*`.** Struts is excluded from this prefix. Do not introduce new `*.json` Struts paths beyond existing patterns.
4. **Specificities** (per-Global-Unit feature flags) go through the `parameters` + `custom_parameters` flow, with constants in *both* `APConstants.java` files (`marlo-data/` and `marlo-web/`).
5. **Schema changes ship as Flyway migrations** under `marlo-web/src/main/resources/database/migrations/` with `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql` naming.
6. **GPL header** on every new Java file (template in `AGENTS.md`).
7. **Code style:** 2-space indent, 120 char line limit, braces on same line, mandatory blocks for `if/while/for/do`, max file length 3500 lines. `mvn checkstyle:check` is a gate.
8. **English only** in code, identifiers, and inline comments. User-facing strings MUST be i18n-keyed.
9. **Branching:** never commit directly to `main`.
10. **Run scripts** match the branch's Java version (Java 17 only on branches containing `java17` / `java_17`).
11. **Dependency floors** (post-January 2026 SETI baseline) MUST NOT be downgraded.
12. **Never commit credentials.** `marlo-${profile}.properties` is gitignored.

---

## Security

MARLO completed a comprehensive security modernization in January 2026 (executed by SETI). All critical vulnerabilities were remediated and the dependency baseline above was established. Snyk scans run on the monorepo each release cycle. Semi-annual reviews are recommended for legacy components (Pentaho, iText).

To report a security issue, contact the IBD team at `Marlosupport@cgiar.org` rather than opening a public issue.

---

## Contributing

1. Read [`CLAUDE.md`](./CLAUDE.md) and [`AGENTS.md`](./AGENTS.md).
2. Branch from `staging` as `<TICKET-ID>-<Description>` (e.g. `A2-2395-Rename-narratives-to-Period-targets`), or as a descriptive `<slug>` when the work has no ticket.
3. Draft the spec under `docs/specs/...` (`requirements.md` → `design.md` → `task.md`).
4. Get the spec reviewed before implementation.
5. Implement, keeping `task.md` up to date with verification notes.
6. Update relevant `reports/ai-context/*.md` files when the change alters routing, validation, replication, or composition contracts.
7. Open a PR into `staging`. Ensure Checkstyle, SonarCloud, and Snyk are clean.

---

## Support & contact

- **MARLO Support:** `Marlosupport@cgiar.org`
- **Maintainer:** IBD Team — Alliance of Bioversity International and CIAT
- **GitHub:** [`CCAFS/MARLO`](https://github.com/CCAFS/MARLO)
- **Wiki:** [github.com/CCAFS/MARLO/wiki](https://github.com/CCAFS/MARLO/wiki)
- **Support tickets** are managed through Freshservice; access requests follow the data-governance ticketing process.

---

## License

MARLO is distributed under the [GNU General Public License v3](./LICENSE). Every Java source file MUST carry the GPL header documented in [`AGENTS.md`](./AGENTS.md).
