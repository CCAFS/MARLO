# Projects — Requirements

**Spec ID:** DOMAIN-PROJECTS-001
**Status:** Draft
**Owner:** IBD Team
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-07-29
**Related PRD sections:** docs/prd.md §5.1, §6.1, §7.1
**Related System Design sections:** docs/system-design/design.md §3.1, §4.4, §6.1, §6.3
**Related Detailed Design sections:** docs/detailed-design/detailed-design.md §2.1, §3.3, §4.2, §5.1
**Companion ai-context docs:** reports/ai-context/frontend-composition-map.md, reports/ai-context/save-validation-matrix.md, reports/ai-context/interceptor-validator-playbook.md, reports/ai-context/struts-critical-routing-catalog.md, reports/ai-context/persistence-replication-managerimpl.md

## 1. Overview

This spec defines the baseline requirements for MARLO's Projects / Clusters module. The module is the operational
workspace where cluster teams manage project description, partners, locations, activities, deliverables, innovations,
studies, policies, outcomes, budgets, safeguards, feedback status, and submission flows.

## 2. Problem Statement

Projects is one of MARLO's highest-traffic and highest-risk areas because it combines long forms, phase-aware
persistence, permission-gated edit flows, repeated child records, and downstream reporting dependencies. A module-level
spec is needed so future changes can be traced against a stable set of requirements instead of relying only on scattered
Struts mappings, FTL files, validators, and manager implementations.

## 3. In-Scope Requirements

### Functional

- DOMAIN-PROJECTS-001-FN-001 — The system MUST allow authorized project users to list projects through the existing
  Projects list flow.
- DOMAIN-PROJECTS-001-FN-002 — The system MUST allow authorized users to edit project description, partners, locations,
  activities, outcomes, budgets, deliverables, innovations, studies, policies, leverages, safeguards, and impacts through
  existing Struts `.do` actions.
- DOMAIN-PROJECTS-001-FN-003 — The system MUST preserve the current add/edit/delete behavior for list/detail flows such
  as deliverables, innovations, studies, policies, highlights, and project outcomes.
- DOMAIN-PROJECTS-001-FN-004 — The system MUST keep project submission separate from normal section saves.
- DOMAIN-PROJECTS-001-FN-005 — The system MUST keep summary/download actions read-oriented and routed through existing
  summary action classes.

### Non-Functional

- DOMAIN-PROJECTS-001-NF-001 — Projects pages MUST remain server-rendered through Struts 2 + FreeMarker/FTL.
- DOMAIN-PROJECTS-001-NF-002 — New Projects changes MUST follow MARLO Java and JavaScript style rules from `AGENTS.md`.
- DOMAIN-PROJECTS-001-NF-003 — New Projects changes MUST avoid introducing new frontend frameworks or client-side
  routing.

### Data

- DOMAIN-PROJECTS-001-DA-001 — Saves to phased project child data MUST preserve the forward-only phase replication
  contract documented in `reports/ai-context/persistence-replication-managerimpl.md`.
- DOMAIN-PROJECTS-001-DA-002 — Deletes of phased project child data MUST mirror save replication targets.
- DOMAIN-PROJECTS-001-DA-003 — New schema changes for Projects MUST ship as Flyway migrations under
  `marlo-web/src/main/resources/database/migrations/`.

### UI

- DOMAIN-PROJECTS-001-UI-001 — Projects views MUST use existing FTL composition, macros, includes, and per-section JS.
- DOMAIN-PROJECTS-001-UI-002 — Repeated editable records MUST follow the existing expandable/list-detail patterns and
  must preserve indexed field binding.
- DOMAIN-PROJECTS-001-UI-003 — User-facing strings MUST be i18n-keyed in `global.properties` or program-specific
  `custom/*.properties`.

### Security

- DOMAIN-PROJECTS-001-SEC-001 — Mutating Projects actions MUST declare an explicit Struts interceptor stack.
- DOMAIN-PROJECTS-001-SEC-002 — Project-level edit flows MUST use `editProjectsStack`, `editProjectsBudgetStack`, or
  `editProjectListStack` plus the relevant item-level edit interceptor where applicable.
- DOMAIN-PROJECTS-001-SEC-003 — New write behavior MUST NOT bypass the manager layer or the action-level permission
  checks.

### Operations

- DOMAIN-PROJECTS-001-OPS-001 — Changes to critical save sections MUST update the relevant ai-context document when
  routing, validation, replication, or composition contracts change.
- DOMAIN-PROJECTS-001-OPS-002 — Local verification for Projects work MUST use Java 17 and the current Maven/Cargo setup.

## 4. Out-of-Scope

- Public write APIs for Projects.
- Replacing Struts `.do` flows with Spring MVC for internal Projects screens.
- Replacing FreeMarker/FTL with a SPA framework.
- Reworking BI dashboards or AI services that consume Projects data.
- Retiring legacy project-adjacent summary flows.

## 5. Personas Affected

- Cluster / Project Coordinator — primary editor of project planning and reporting sections.
- Cluster / Project Leader — reviewer/sign-off role for project content.
- PMU — consumes completeness, submission, and synthesis outputs from project data.
- QA Reviewer — reviews project outputs and feedback-related fields.
- Program / CRP Admin — manages project structures, users, roles, and related configuration.
- AI service consumer — indirectly affected through AI services that consume project data.

## 6. Acceptance Criteria

AC for FN-002:
- Given an authenticated user with edit rights to a project,
- When they open a Projects section route such as `/projects/{crp}/description.do`,
- Then the request MUST pass through the configured Struts action and interceptor stack,
- And the matching FTL view MUST render.

AC for DA-001:
- Given a save touches phased project child data,
- When the save completes successfully,
- Then current and future phases MUST reflect the manager-specific replication contract,
- And past phases MUST remain unchanged.

AC for UI-001:
- Given a Projects UI change adds or modifies a form field,
- When the field is rendered,
- Then the view MUST use existing FTL composition and form macro conventions unless a documented exception is approved.

AC for SEC-001:
- Given a new mutating Projects action is added,
- When `struts-projects.xml` is reviewed,
- Then the action MUST declare an explicit interceptor stack and MUST NOT rely on anonymous default write-path behavior.

## 7. Constitutional Compliance Checklist

- [x] Phase replication: required for phased project child data and covered in design.md.
- [x] Save validation: existing Action.validate() + Validator + interceptor stack patterns identified.
- [x] Permissions: existing Projects stacks and item-level edit interceptors identified.
- [x] Specificity: not required by this baseline; use `parameters` + `custom_parameters` for conditional future changes.
- [x] Migrations: required for schema changes under the standard Flyway path.
- [x] i18n: required for all user-facing strings.
- [x] License header: required for every new Java file.
- [x] Code style: Checkstyle and repository style rules apply.
- [x] REST: out of scope for this baseline; future `/api/*` work must use Spring MVC and DTOs.
- [x] Audit: auditable project changes must preserve audit columns and listener integration.
- [x] Dependency baseline: versions declared in `marlo-parent/pom.xml` must not be downgraded without approval.
- [x] Branching: feature branches start from the staging integration branch; production receives promoted merges only.

## 8. Open Questions

- Which Projects sections should be classified as "critical save sections" beyond the current ai-context matrix?
- Which legacy Projects summary routes still need explicit ownership and regression coverage?
- Should Projects get a generated route/action/view inventory as part of the verification commands pack?

## 9. Decision Log

- 2026-07-29 — Create a module-level baseline spec for Projects — Rationale: Projects is broad enough that future
  changes need module-specific traceability before implementation.
