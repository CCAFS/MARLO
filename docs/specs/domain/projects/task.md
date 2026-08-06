# Projects — Tasks

**Spec ID:** DOMAIN-PROJECTS-001
**Status:** Draft
**Owner:** IBD Team
**Last Updated:** 2026-07-29
**Implements design:** docs/specs/domain/projects/design.md
**Branching:** feature branch from the staging integration branch.
**Target merge:** staging integration branch, then promoted to production per release process.

## 1. Execution Context

- Java: MARLO currently uses Java 17.
- Local run script: `scripts/run-marlo-java17.sh` (or `.bat` on Windows).
- Spring profile: `dev` for local development unless a task explicitly requires another profile.
- Local properties: `marlo-dev.properties` is gitignored; bootstrap from `marlo-test.properties`.
- Primary config: `marlo-web/src/main/resources/struts-projects.xml`.

## 2. Pre-flight Checklist

- [ ] Confirm `requirements.md` and `design.md` are approved for the target change.
- [ ] Pull latest staging integration branch.
- [ ] Create a feature branch for the specific Projects change.
- [ ] Inspect existing action, validator, view, JS, manager, DAO, and migration patterns before editing.
- [ ] Confirm whether the target flow is in `reports/ai-context/save-validation-matrix.md`.

## 3. Task List

### DOMAIN-PROJECTS-001-T01 — Confirm target route and ownership

- **Depends on:** None
- **Module:** marlo-web
- **Files touched:** Usually none
- **Constitutional checks:** Route must stay in Struts `.do` unless explicitly scoped as REST.
- **Tests:** Not applicable
- **Done when:** The exact action mapping, interceptor stack, action class, view, and JS file are identified.
- **Verification:** Inspect `struts-projects.xml` and the matching action/view files.

### DOMAIN-PROJECTS-001-T02 — Inspect save and validation pipeline

- **Depends on:** T01
- **Module:** marlo-web
- **Files touched:** Usually none
- **Constitutional checks:** `Action.validate()` + Validator + `if (save)` pattern must be preserved where present.
- **Tests:** Not applicable
- **Done when:** The action save method, validator class, and invalid-field behavior are documented for the target change.
- **Verification:** Compare against `reports/ai-context/save-validation-matrix.md` and
  `reports/ai-context/interceptor-validator-playbook.md`.

### DOMAIN-PROJECTS-001-T03 — Inspect persistence and phase replication

- **Depends on:** T01
- **Module:** marlo-data
- **Files touched:** Usually none
- **Constitutional checks:** Forward-only phase replication must be preserved for phased data.
- **Tests:** Not applicable
- **Done when:** The manager save/delete path and replication behavior are understood before editing.
- **Verification:** Inspect the relevant `*ManagerImpl` and compare against
  `reports/ai-context/persistence-replication-managerimpl.md`.

### DOMAIN-PROJECTS-001-T04 — Implement the scoped code change

- **Depends on:** T01, T02, T03 as applicable
- **Module:** marlo-web, marlo-data
- **Files touched:** Specific to the change
- **Constitutional checks:** GPL header for new Java files, manager-layer persistence, explicit interceptor stack,
  i18n for user-facing strings, no new JSP views.
- **Tests:** Unit/action/manual tests as required by the change.
- **Done when:** The requested behavior is implemented with minimal unrelated churn.
- **Verification:** Local diff review and targeted compile/test command.

### DOMAIN-PROJECTS-001-T05 — Add or update migrations when data changes

- **Depends on:** T04 when schema changes are required
- **Module:** marlo-web, marlo-data
- **Files touched:** `marlo-web/src/main/resources/database/migrations/`, entities, managers, DAOs
- **Constitutional checks:** Migration follows current naming convention; old malformed migration names are not copied.
- **Tests:** Migration applies cleanly in the target environment.
- **Done when:** Schema and entity mappings are aligned.
- **Verification:** Review migration SQL and run the project startup/build path that applies migrations where available.

### DOMAIN-PROJECTS-001-T06 — Verify frontend composition

- **Depends on:** T04
- **Module:** marlo-web
- **Files touched:** FTL and JS files under Projects
- **Constitutional checks:** Use existing macros/includes and per-section JS; no new rendering stack.
- **Tests:** Manual UI walkthrough.
- **Done when:** The page renders, saves, and displays errors/success messages correctly.
- **Verification:** Local browser walkthrough when the app can be run.

### DOMAIN-PROJECTS-001-T07 — Run verification

- **Depends on:** T04, T05, T06
- **Module:** all changed modules
- **Files touched:** None
- **Constitutional checks:** Checkstyle and targeted tests are run or explicitly documented as not run.
- **Tests:** `mvn checkstyle:check` and targeted Maven tests/builds as appropriate.
- **Done when:** Verification results are recorded in the PR/task notes.
- **Verification:** Command output reviewed locally or in CI.

### DOMAIN-PROJECTS-001-T08 — Update context docs when contracts change

- **Depends on:** T07
- **Module:** docs/reports
- **Files touched:** Relevant files under `reports/ai-context/` and this spec when needed
- **Constitutional checks:** Routing, validation, replication, and composition docs stay synchronized.
- **Tests:** Documentation review.
- **Done when:** Any changed contract is reflected in the operational context.
- **Verification:** `rg` checks for stale route/action/view names.

## 4. Dependency Graph

```text
T01 (route ownership)
  ├── T02 (save and validation pipeline)
  └── T03 (persistence and replication)
        └── T04 (implementation)
              ├── T05 (migration, if needed)
              ├── T06 (frontend composition)
              └── T07 (verification)
                    └── T08 (context docs)
```

## 5. Testing Plan

### Unit

- Manager-layer tests for changed phase replication behavior.
- Validator tests for new or changed validation rules where test infrastructure is practical.

### Integration

- Targeted Struts action flow: request -> interceptor stack -> action -> validator -> manager.
- Migration startup/application when schema changes.

### Regression

- Smoke the affected Projects section.
- Smoke nearby sections sharing the same stack or child-record pattern.
- Re-test entries in `save-validation-matrix.md` when touched.

### Non-functional

- Review list/detail pages for N+1 query regressions if loading behavior changes.

### Accessibility

- Confirm labels, validation messages, and keyboard reachability for new or changed fields.

## 6. Operational Steps

### Migration deploy

- Flyway migrations run through the normal application startup path.
- Confirm applied migrations in the target environment when schema changes are included.

### Specificity rollout

- Not applicable unless a future Projects change is feature-flagged.

### BI / AI coordination

- Notify BI or AI owners when project data shape, semantics, or availability changes.

### Backups

- Confirm the standard production backup process is green before deploying destructive or high-risk data changes.

### Notifications

- Use the existing Jenkins/Slack release notification flow.

## 7. Rollback Plan

### Code

- Revert the feature branch merge and redeploy the previous artifact through the normal pipeline.

### Data

- Prefer additive migrations. If destructive changes are unavoidable, document rollback SQL and backup dependency before
  approval.

### Specificity

- For feature-flagged behavior, set the relevant `custom_parameters.value` to `false` for the affected Global Unit.

## 8. Definition of Done

- [ ] Requirements and design are approved for the specific Projects change.
- [ ] All impacted routes, actions, validators, views, JS files, managers, and migrations are identified.
- [ ] Implementation preserves Struts + FTL composition and manager-layer persistence.
- [ ] Phase replication is verified when changed.
- [ ] Checkstyle and targeted tests/builds pass, or skipped verification is documented with rationale.
- [ ] User-facing strings are i18n-keyed.
- [ ] Relevant `reports/ai-context/` docs are updated if contracts changed.
- [ ] Change is merged through the approved staging/production path.
