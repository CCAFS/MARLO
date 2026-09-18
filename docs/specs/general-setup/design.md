# MARLO — Design Spec Template

**Purpose:** This file defines the *format and conventions* for `design.md` inside any module spec under `docs/specs/`. It is a methodology template, not a feature design.

**Pairs with:** `requirements.md` and `task.md` in the same spec folder — plus `family.md` when this spec is part of a spec family (template: `docs/specs/general-setup/family.md`).

---

## Front matter (top of every `design.md`)

```
# <Module / Feature / Bug> — Design

**Spec ID:** <same as requirements.md>
**Status:** Draft | In Review | Approved | Implemented | Superseded
**Owner:** <name / team>
**Last Updated:** YYYY-MM-DD
**Implements requirements:** list of requirement IDs covered.
**Touches modules:** marlo-web, marlo-data, marlo-core, marlo-utils (as applicable).
```

---

## Required structure

A `design.md` MUST follow this section order. Empty sections MUST stay present with a "Not applicable" justification.

1. **Architecture Summary** — short paragraph + simple diagram (ASCII or mermaid).
2. **Module Footprint** — exact files / packages that will be added or changed, by Maven module.
3. **Data Model Changes** — new entities, new columns, FK changes, indices, enums; the Flyway migration plan.
4. **API / Action Surface** — new Struts actions, REST endpoints, JSON endpoints (only if pre-existing pattern).
5. **Frontend Composition** — FTL views, macros, includes/imports, JS modules, expandable-block pattern usage.
6. **Persistence & Phase Replication Plan** — explicit save and delete propagation strategy.
7. **Validation & Save Pipeline** — interceptor stack, Action.validate(), Validator class, error surfaces.
8. **Permissions & Edit Gates** — which interceptors / `canEdit*` checks apply.
9. **Specificity / Feature-Flag Strategy** — if this is conditional, the `parameters` + `custom_parameters` plan.
10. **Integration Points** — CLARISA, CGSpace, BI pipeline, AI services, S3, Pusher, etc.
11. **Observability** — logs, metrics, audit columns, dashboards.
12. **Performance & Scalability** — expected load, query patterns, indexing, caching.
13. **Security Considerations** — authentication, authorization, sensitive data handling.
14. **Backwards Compatibility & Rollout** — migrations, dual-running, feature-flag rollout plan, rollback path.
15. **Decision Records** — the design choices made (ADR-style mini-entries).
16. **Open Risks** — risks that survive into implementation.

---

## Module Footprint section format

```
## Module Footprint

### marlo-web
- New: action/projects/innovations/InnovationWizardAction.java
- New: action/projects/innovations/InnovationWizardValidator.java
- New: webapp/WEB-INF/crp/views/projects/innovations/innovationWizard.ftl
- Modified: resources/struts-projects.xml (new <action> mapping)
- Modified: resources/global.properties (i18n keys)

### marlo-data
- New: data/manager/ProjectInnovationWizardManager.java
- New: data/manager/impl/ProjectInnovationWizardManagerImpl.java
- New: data/dao/ProjectInnovationWizardDAO.java
- New: data/dao/mysql/MySQLProjectInnovationWizardDAO.java
- Modified: data/model/ProjectInnovation.java (new column)
- Modified: config/APConstants.java (new specificity key)

### marlo-core / marlo-utils
- Not applicable.
```

---

## Data Model Changes section format

Always include the migration filename, target tables, and a copy of the migration body.

```
## Data Model Changes

### Migration
File: marlo-web/src/main/resources/database/migrations/V2_6_1_20260501_1030__InnovationScalingReadinessStep.sql

```sql
ALTER TABLE project_innovation
  ADD COLUMN scaling_readiness_step TINYINT NULL AFTER scaling_readiness_level;

CREATE INDEX idx_proj_innov_scaling_step ON project_innovation (scaling_readiness_step);
```

### Entity changes
- `ProjectInnovation.java`: add `private Integer scalingReadinessStep;` with getter/setter and JPA mapping.

### Indices, FKs, enums
- New index: `idx_proj_innov_scaling_step`.
- No new FKs.
- No new enums.

### Backfill / data migration
- Not required (NULL default).
```

---

## API / Action Surface section format

```
## API / Action Surface

### Struts actions (.do)
| Route | Action class | Stack | View result |
|---|---|---|---|
| /projects/{crp}/innovationWizard | InnovationWizardAction | editProjectListStack + editInnovation | /WEB-INF/crp/views/projects/innovations/innovationWizard.ftl |

### Spring MVC REST
- Not applicable.

### Existing JSON endpoints
- Not applicable. (Per AGENTS.md: do not introduce new JSON paths unless an existing pattern requires it.)
```

---

## Persistence & Phase Replication Plan section format

```
## Persistence & Phase Replication Plan

Pattern: standard MARLO ManagerImpl recursive replication
(see reports/ai-context/persistence-replication-managerimpl.md).

### Save flow
1. ProjectInnovationWizardManagerImpl.save(...) persists current entity.
2. Read currentPhase = phaseDao.find(...).
3. If currentPhase is PLANNING and currentPhase.getNext() != null,
   call addInnovationWizardPhase(...) for next phase, then recurse.
4. If currentPhase is REPORTING, replicate to currentPhase.getNext().getNext()
   (UpKeep) and recurse.
5. Filter target phases for duplicates before insert.

### Delete flow
- Mirror save flow with deleteInnovationWizardPhase(...).
- Maintain parity: if save propagates to phase X, delete propagates to phase X.

### Section-specific skip rules
- Not applicable. (No "publication-like" exclusions for this entity.)

### Tests
- New unit tests for both save and delete propagation chains.
```

---

## Validation & Save Pipeline section format

```
## Validation & Save Pipeline

- Interceptor stack: editProjectListStack + editInnovation (see struts-projects.xml).
- Action.validate(): guarded with `if (save) { ... }`.
- Validator class: `InnovationWizardValidator` extending `BaseValidator`.
- On invalid: action populates `invalidFields` and action errors;
  view re-renders with field-level + page-level messages.
- On valid: ManagerImpl save chain runs (see Persistence section).
```

---

## Permissions & Edit Gates section format

```
## Permissions & Edit Gates

- Interceptors:
  - `requireUser` (auth)
  - `validCrp` (CRP scope)
  - `editProjectsStack` base
  - `editInnovation` (per-innovation edit gate)
- Required role(s): Cluster Coordinator (write), QA Reviewer (read+comment).
- REST: not applicable.
```

---

## Specificity / Feature-Flag Strategy section format

If conditional, follow the `AGENTS.md` specificity workflow exactly:

```
## Specificity / Feature-Flag Strategy

- Specificity key: `innovation_scaling_readiness_wizard_enabled`
- APConstants constant: `INNOVATION_SCALING_READINESS_WIZARD_ENABLED`
  - Added in marlo-data/.../APConstants.java
  - Added in marlo-web/.../APConstants.java
- Migration: V2_6_1_20260501_1031__SpecInnovationScalingReadinessWizard.sql
  - INSERT into parameters for global_unit_type_id 1, 3, 4 (CRP, Platform, Center).
  - INSERT into custom_parameters for AICCRA (global_unit_id = <id>) with value = 'true'.
- Backend gating: `if (this.hasSpecificities(APConstants.INNOVATION_SCALING_READINESS_WIZARD_ENABLED))`.
- Frontend gating: `[#if action.hasSpecificities('innovation_scaling_readiness_wizard_enabled')]`.
```

If not conditional, write "Not applicable — feature ships unconditionally for all Global Units."

---

## Decision Records section format

Each decision is a self-contained mini-ADR. Append-only.

```
## Decision Records

### ADR-DOMAIN-INNOVATIONS-002-1 — 4-step wizard layout
- Decision: Adopt a 4-step layout grouping levels {0–2, 3–5, 6–7, 8–9}.
- Rationale: User testing showed 9-step layout felt hostile.
- Alternatives considered: Single accordion, 9-step linear wizard.
- Status: Accepted.

### ADR-DOMAIN-INNOVATIONS-002-2 — Reuse expandable-block pattern
- Decision: Render evidence list inside each step using the hidden-template pattern.
- Rationale: Consistency with the rest of MARLO.
- Status: Accepted.
```

---

## Open Risks section format

```
## Open Risks
- Risk: phase replication interaction with publication-class innovations is unclear.
  Mitigation: review publication path before merge; add explicit test.
- Risk: BI pipeline expects scaling_readiness_level only.
  Mitigation: coordinate with BI team to add scaling_readiness_step in next Bronze refresh.
```
