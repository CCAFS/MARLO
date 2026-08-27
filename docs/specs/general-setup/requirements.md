# MARLO — Requirements Spec Template

**Purpose:** This file defines the *format and conventions* for `requirements.md` inside any module spec under `docs/specs/`. It is a methodology template, not a feature spec.

**Where to place a new module spec:**

- `docs/specs/domain/<module>/` — for a domain area (e.g., `domain/deliverables/`, `domain/innovations/`).
- `docs/specs/enhancement/<feature>/` — for a cross-cutting enhancement.
- `docs/specs/bugfix/<slug>/` — for a structured bug-driven spec.
- `docs/specs/epic/<name>/` — for a multi-spec initiative (e.g., `epic/java-17-cutover/`).

Every spec folder MUST contain three files:

- `requirements.md` (this template).
- `design.md` (see `docs/specs/general-setup/design.md`).
- `task.md` (see `docs/specs/general-setup/task.md`).

A spec folder that was **chunked into child specs** MUST also contain:

- `family.md` (see `docs/specs/general-setup/family.md`) — the spec-family manifest tracking child order, dependencies, and status. Its absence means the spec is flat, with no added obligations.

A spec folder MAY also contain `agent-context.md` — a compact, agent-first operational summary read before the longer spec files for routine work.

---

## Front matter (top of every `requirements.md`)

```
# <Module / Feature / Bug> — Requirements

**Spec ID:** <DOMAIN-DELIVERABLES-001 | ENH-DARK-MODE-001 | BUG-PORTFOLIO-SAVE-001 | EPIC-JAVA17-CUTOVER>
**Status:** Draft | In Review | Approved | Implemented | Superseded
**Owner:** <name / team>
**Reviewers:** <PMU lead, QA lead, Tech lead, etc.>
**Last Updated:** YYYY-MM-DD
**Related PRD sections:** docs/prd.md §<n>
**Related UX/UI Design sections:** docs/ux-ui/design.md §<n>
**Related TRD sections:** docs/trd/trd.md §<n>
**Related Infrastructure sections:** docs/infrastructure.md §<n> (when the spec touches environments, deployment, or the local stack)
**Companion ai-context docs:** reports/ai-context/<file>.md (when applicable)
```

---

## Required structure

A `requirements.md` MUST follow this section order. Empty sections MUST still be present with a "Not applicable" justification.

1. **Overview** — one paragraph: what this spec is and why it exists now.
2. **Problem Statement** — the user / operational pain in plain language. Cite the PRD problem area it addresses.
3. **In-Scope Requirements** — numbered functional and non-functional requirements (§ Numbering & Writing rules below).
4. **Out-of-Scope** — explicit list of what this spec does NOT cover.
5. **Personas Affected** — which PRD personas (cluster coordinator, QA reviewer, PMU, admin, etc.) are affected and how.
6. **Acceptance Criteria** — testable rules per requirement (Given/When/Then or equivalent).
7. **Constitutional Compliance Checklist** — confirm each constitutional rule is honored or call out a deliberate deviation.
8. **Open Questions** — questions that block design / implementation.
9. **Decision Log** — append-only list of `YYYY-MM-DD — decision — rationale`.

---

## Requirement numbering

Use a stable prefix per spec, then category, then sequence:

```
<SPEC_PREFIX>-FN-001   Functional requirement
<SPEC_PREFIX>-NF-001   Non-functional requirement
<SPEC_PREFIX>-DA-001   Data / persistence requirement
<SPEC_PREFIX>-UI-001   UI / UX requirement
<SPEC_PREFIX>-API-001  API / integration requirement
<SPEC_PREFIX>-SEC-001  Security / authorization requirement
<SPEC_PREFIX>-OPS-001  Operations / observability requirement
<SPEC_PREFIX>-MIG-001  Migration / data-cutover requirement
```

Numbers are append-only. Removed requirements MUST be marked `(deprecated)` and kept in place to preserve traceability.

---

## Writing rules

1. **Imperative voice.** "The system MUST …", "The Cluster Coordinator MUST be able to …".
2. **One requirement per bullet.** Compound requirements split into multiple numbered items.
3. **Testable.** Every requirement must be verifiable by a test, an inspection, or a UI walkthrough.
4. **Cite the source.** When a requirement comes from the PRD, an ai-context doc, a Jira ticket, or a Freshservice ticket, cite it inline (`(see docs/prd.md §6.2)`).
5. **No solutions in requirements.** Implementation choices belong in `design.md`.
6. **English only.** Per `AGENTS.md`. User-facing strings reference i18n keys, not literal text.
7. **Honor constitutional constraints.** Phased replication, specificity flow, save validation pattern, interceptor stacks, GPL header, Checkstyle, code style — all are non-negotiable unless an explicit, justified deviation is recorded in the Decision Log.

---

## Acceptance-criteria style

Use Given/When/Then or numbered behavior assertions. Reference the requirement IDs:

```
AC for FN-001:
- Given the user is an authenticated Cluster Coordinator with edit rights to cluster X,
- When they save the Project Description form with a missing required field,
- Then the page MUST re-render with the field-level error and the page-level message banner,
- And the data MUST NOT be persisted,
- And the audit log MUST NOT record a write.
```

---

## Constitutional compliance checklist (copy-paste into every requirements.md)

```
- [ ] Phase replication: covered in design.md with explicit save and delete paths.
- [ ] Save validation: Action.validate() + Validator + interceptor stack identified.
- [ ] Permissions: every new action declares its interceptor stack.
- [ ] Specificity: feature flag added through parameters + custom_parameters when conditional.
- [ ] Migrations: every schema change ships as a Flyway migration with the V<...>__<Description>.sql naming.
- [ ] i18n: no hardcoded user-facing strings; keys added to global.properties (and custom/*.properties when program-specific).
- [ ] License header: every new Java file carries the GPL header from AGENTS.md.
- [ ] Code style: Checkstyle passes; 2-space indent; 120 char line limit.
- [ ] REST: any new /api/* endpoint uses Spring MVC, DTOs, Springdoc OpenAPI, and the rest/errors handlers.
- [ ] Audit: auditable changes integrate with IAuditLog / HibernateAuditLogListener.
- [ ] Dependency floors (post-Jan 2026 SETI baseline) preserved.
- [ ] Branching: feature branch created from staging; merge target is staging; production main receives merges only.
```

---

## Sample `requirements.md` skeleton (copy when starting a new spec)

```
# Innovations — Scaling Readiness Wizard — Requirements

**Spec ID:** DOMAIN-INNOVATIONS-002
**Status:** Draft
**Owner:** Innovations team
**Reviewers:** PMU lead, QA lead
**Last Updated:** 2026-04-30
**Related PRD sections:** docs/prd.md §6.1, §6.3
**Related UX/UI Design sections:** docs/ux-ui/design.md §3, §6.3
**Related TRD sections:** docs/trd/trd.md §3, §5.1
**Companion ai-context docs:** reports/ai-context/save-validation-matrix.md

## 1. Overview
...

## 2. Problem Statement
...

## 3. In-Scope Requirements

### Functional
- DOMAIN-INNOVATIONS-002-FN-001 — The system MUST present a stepwise wizard for entering Scaling Readiness levels 0..9.
- DOMAIN-INNOVATIONS-002-FN-002 — ...

### Non-Functional
- DOMAIN-INNOVATIONS-002-NF-001 — Wizard initial load p95 ≤ 1.5s on a warm Tomcat.

### Data
- DOMAIN-INNOVATIONS-002-DA-001 — A new column innovation.scaling_readiness_step (TINYINT NULL) MUST be added via Flyway migration ...

### UI
- DOMAIN-INNOVATIONS-002-UI-001 — The wizard MUST render via expandable blocks per the pattern in EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md.

### Security
- DOMAIN-INNOVATIONS-002-SEC-001 — The wizard route MUST be gated by canEditProject + editInnovation.

## 4. Out-of-Scope
- Public REST API for innovations write (covered separately).
- Mobile-optimized layout (light theme only, desktop-first).

## 5. Personas Affected
- Cluster Coordinator (primary).
- QA Reviewer (review path).
- PMU (BI rollup).

## 6. Acceptance Criteria
...

## 7. Constitutional Compliance Checklist
- [x] Phase replication: covered in design.md (planning + reporting paths).
- [x] Save validation: ProjectInnovationAction.validate() + ProjectInnovationValidator.
- ...

## 8. Open Questions
- Should partial wizard progress autosave to draft?

## 9. Decision Log
- 2026-04-30 — Adopt a 4-step wizard (vs 9 steps) — Rationale: cluster coordinators reported the 9-step version felt hostile in user testing.
```
