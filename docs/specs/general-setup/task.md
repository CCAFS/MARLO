# MARLO — Task Spec Template

**Purpose:** This file defines the *format and conventions* for `task.md` inside any module spec under `docs/specs/`. It is the executable plan that translates an approved `design.md` into ordered, testable steps.

**Pairs with:** `requirements.md` and `design.md` in the same spec folder.

---

## Front matter (top of every `task.md`)

```
# <Module / Feature / Bug> — Tasks

**Spec ID:** <same as requirements.md and design.md>
**Status:** Draft | Ready | In Progress | Done | Superseded
**Owner:** <name / team>
**Last Updated:** YYYY-MM-DD
**Implements design:** docs/specs/<area>/<slug>/design.md
**Branching:** feature branch from staging, named <TICKET-ID>-<Description> (or a descriptive <slug> when the work has no ticket).
**Target merge:** staging (then promoted to main per release process).
```

---

## Required structure

A `task.md` MUST follow this section order. Empty sections MUST stay present with a "Not applicable" justification.

1. **Execution Context** — environment, tools, run script (`scripts/run-marlo-java8.sh` or `run-marlo-java17.sh`), Spring profile.
2. **Pre-flight Checklist** — confirm `requirements.md` and `design.md` are approved; pull latest `staging`; create feature branch.
3. **Task List** — ordered, atomic steps with IDs, dependencies, acceptance, and verification.
4. **Dependency Graph** — visual or list form showing task ordering.
5. **Testing Plan** — explicit unit, integration, regression, and manual testing per task or per group.
6. **Operational Steps** — migration deploy, BI / AI service coordination, configuration changes, env-var changes.
7. **Rollback Plan** — how to undo if production verification fails.
8. **Definition of Done** — criteria for marking the spec Done.

---

## Task ID format

Use the spec prefix + `T` + zero-padded sequence:

```
DOMAIN-INNOVATIONS-002-T01  Create migration for new column
DOMAIN-INNOVATIONS-002-T02  Add APConstants entries (both modules)
DOMAIN-INNOVATIONS-002-T03  Implement DAO + Manager + ManagerImpl
DOMAIN-INNOVATIONS-002-T04  Implement Validator
DOMAIN-INNOVATIONS-002-T05  Implement Action
DOMAIN-INNOVATIONS-002-T06  Wire Struts mapping and interceptors
DOMAIN-INNOVATIONS-002-T07  Implement FTL view + JS
DOMAIN-INNOVATIONS-002-T08  i18n keys
DOMAIN-INNOVATIONS-002-T09  Phase replication tests
DOMAIN-INNOVATIONS-002-T10  Specificity migration + custom_parameters
DOMAIN-INNOVATIONS-002-T11  Manual QA pass against acceptance criteria
DOMAIN-INNOVATIONS-002-T12  Update relevant ai-context doc(s) if scope warrants
```

Numbers are append-only. Removed tasks are marked `(deprecated)` and kept.

---

## Task entry format

Each task MUST include the seven fields below.

```
### DOMAIN-INNOVATIONS-002-T03 — Implement DAO + Manager + ManagerImpl

- **Depends on:** T01, T02
- **Module:** marlo-data
- **Files touched:**
  - data/dao/ProjectInnovationWizardDAO.java (new)
  - data/dao/mysql/MySQLProjectInnovationWizardDAO.java (new)
  - data/manager/ProjectInnovationWizardManager.java (new)
  - data/manager/impl/ProjectInnovationWizardManagerImpl.java (new)
- **Constitutional checks:**
  - GPL header present in every new Java file.
  - Layered pattern preserved (Manager → ManagerImpl → DAO → MySQLDAO).
  - Phase replication implemented per persistence-replication-managerimpl.md.
- **Tests:**
  - Unit: save propagation Planning → all next phases.
  - Unit: delete propagation Planning → all next phases.
  - Unit: save propagation Reporting → UpKeep chain.
- **Done when:**
  - mvn checkstyle:check passes.
  - Unit tests pass locally.
  - No new Snyk critical findings on changed paths.
- **Verification:**
  - Manually invoke save through the action and confirm DB state in current and next phases.
```

---

## Dependency Graph section format

Use a simple list or a mermaid block. Keep it readable.

```
## Dependency Graph

T01 (migration)
  └── T02 (APConstants)
        └── T03 (DAO + Manager)
              ├── T04 (Validator)
              │     └── T05 (Action)
              │           └── T06 (Struts mapping)
              │                 └── T07 (FTL + JS)
              │                       └── T08 (i18n)
              │                             └── T11 (manual QA)
              └── T09 (replication tests)
T10 (specificity migration) runs in parallel; gates T11 if rollout is conditional.
T12 (ai-context doc updates) runs after T11.
```

---

## Testing Plan section format

Cover every layer relevant to the change.

```
## Testing Plan

### Unit
- ProjectInnovationWizardManagerImpl.save() Planning → all-next replication.
- ProjectInnovationWizardManagerImpl.delete() Planning → all-next propagation.
- Validator: required-field rules.

### Integration
- End-to-end save: HTTP POST → interceptor stack → action → manager → DB.
- Confirm interceptor denial returns 403/redirect (not silent drop).

### Regression (manual, QA team)
- Project Description, Project Partners, Project Deliverable smoke (sections that share editProjectListStack).
- Re-test save-validation matrix coverage of affected sections.

### Non-functional
- Load profile: simulate end-of-cycle peak (cluster coordinators submitting concurrently).

### Accessibility
- Keyboard navigation through wizard.
- Screen reader announces step changes.
```

---

## Operational Steps section format

```
## Operational Steps

### Migration deploy
- Flyway runs on Tomcat startup; confirm migration applied via flyway_schema_history.

### Specificity rollout
- Insert custom_parameters row for AICCRA (global_unit_id = <id>) with value = 'true'
  *after* code is deployed, *not before*.

### BI / AI coordination
- Notify BI team: new column scaling_readiness_step lands in Bronze on next refresh.
- Notify AI Reports Generator owner: new field available for narrative generation.

### Backups
- Production DB backup at S3 verified green within 24h prior to deploy.

### Notifications
- Slack #marlo-deploys on Jenkins success/failure (existing convention).
```

---

## Rollback Plan section format

```
## Rollback Plan

### Code
- Revert merge commit on staging.
- Re-deploy previous artifact via Jenkins.

### Data
- Migration is additive (NULL column + index). No destructive rollback needed.
- If rollback required, leave the column in place; feature flag (specificity) gates UI.

### Specificity
- Set custom_parameters.value = 'false' for affected global_unit_id to disable instantly.
```

---

## Definition of Done section format

```
## Definition of Done

- [ ] All acceptance criteria from requirements.md verified.
- [ ] All constitutional compliance checklist items confirmed in PR.
- [ ] mvn checkstyle:check passes.
- [ ] SonarCloud: no new blockers / critical issues.
- [ ] Snyk: no new critical findings.
- [ ] QA test pass complete; defects closed or accepted with PMU sign-off.
- [ ] Documentation updated:
  - This task.md: every task marked done with verification notes.
  - reports/ai-context/<file>.md updated if the change altered routing, validation, or replication contracts.
- [ ] Merged to staging.
- [ ] Promoted to main via the release pipeline.
- [ ] Deployment confirmed in production; backup green; QA dashboard confirms refresh latency.
```

---

## Conventions reminders

- **Branching:** never commit directly to `main`. Feature branches start from `staging` and merge back into it. `dev` is unstable and used only for integration experiments.
- **Java version:** match the run script to the branch. If the branch contains `java17` / `java_17`, use `scripts/run-marlo-java17.sh`. Otherwise `scripts/run-marlo-java8.sh`.
- **Local properties:** `marlo-${profile}.properties` files with credentials are gitignored; bootstrap from `marlo-test.properties`.
- **Migration naming:** `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`.
- **Specificity naming:** snake_case key in `parameters`, UPPER_SNAKE_CASE constant in `APConstants`, identical string value.
- **Hooks / pre-commit:** never bypass (`--no-verify`) without an explicit, documented reason; failing hooks are signal, not noise.
