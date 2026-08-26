# AI Services — Per-Global-Unit Section Content — Tasks

**Spec ID:** DOMAIN-AI-SERVICES-001
**Status:** Draft
**Owner:** IBD Team
**Last Updated:** 2026-08-26
**Implements design:** docs/specs/domain/ai-services/design.md
**Jira:** A2-2433 (child of A2-2055 — Enhancements 2026)
**Branching:** feature branch from `staging`, named `A2-2433-AiSectionPerGlobalUnit`.
**Target merge:** `staging` (then promoted to `main` per release process).

## 1. Execution Context

- Java 17. Run locally with `scripts/run-marlo-java17.sh` (verify the level against `marlo-parent/pom.xml`).
- Spring profile: local `marlo-<profile>.properties`, bootstrapped from `marlo-test.properties` (never committed).
- Flyway runs on Tomcat startup; confirm application through `flyway_schema_history`.
- Database: MySQL. The affected table is `ai_report_configuration` (3 rows in production today).
- Reference: `docs/specs/domain/ai-services/agent-context.md` for the as-built module map.

## 2. Pre-flight Checklist

- [ ] `requirements.md` and `design.md` reviewed and approved.
- [ ] **OQ-1 answered by PMU** — confirm that a tool row belongs to exactly one Global Unit (no shared/global rows).
      If the answer is "shared rows are needed", stop: DA-003 and ADR-1 change, and T01 must be redesigned.
- [ ] **OQ-2 answered** — run against production and record the result:
      ```sql
      SELECT gu.id, gu.acronym, cp.value
      FROM custom_parameters cp
      JOIN parameters p ON p.id = cp.parameter_id
      JOIN global_units gu ON gu.id = cp.global_unit_id
      WHERE p.`key` = 'ai_section_active' AND cp.is_active = 1;
      ```
      Any Global Unit other than AICCRA with `value = 'true'` will visibly lose its cards on deploy and must be told.
- [ ] **Pre-flight data check** — confirm every existing row is attributable to AICCRA:
      ```sql
      SELECT COUNT(*) FROM ai_report_configuration;                          -- expect 3 in production
      SELECT COUNT(*) FROM ai_report_configuration WHERE is_active = 0;      -- expect 0
      ```
- [ ] Pull latest `staging`; create the feature branch from it.
- [ ] Confirm `AiReportConfigurationManager` still has exactly one caller (`AiAction`):
      `grep -rlI "AiReportConfigurationManager" marlo-web/src/main/java marlo-data/src/main/java`

## 3. Task List

### DOMAIN-AI-SERVICES-001-T01 — Flyway migration: tenant column, FK, index, backfill, NOT NULL

- **Depends on:** Pre-flight checklist (OQ-1, OQ-2, data check).
- **Module:** marlo-web
- **Files touched:**
  - `resources/database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__AiReportConfigurationPerGlobalUnit.sql` (new)
- **Constitutional checks:**
  - Naming follows `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`.
  - Timestamp sorts after `V2_6_0_20260826_1000`.
  - FK named `<table>_global_units_FK`, matching the precedent in `V2_6_0_20260724_1433__UpdateActivityTitlesTable.sql`.
  - No credentials, no environment-specific values other than the AICCRA id `45`.
- **Tests:**
  - Run against a database with the 3 seeded rows: all three end up with `global_unit_id = 45`.
  - Run against a database with an empty `ai_report_configuration`: completes, `NOT NULL` applied (MIG-002).
- **Done when:**
  - Migration applies cleanly on a local restore of a production-like schema.
  - `flyway_schema_history` shows success.
  - `SELECT COUNT(*) FROM ai_report_configuration WHERE global_unit_id IS NULL;` returns 0.
- **Verification:** `SHOW CREATE TABLE ai_report_configuration;` shows the column `NOT NULL`, the FK and the index.

### DOMAIN-AI-SERVICES-001-T02 — Entity + Hibernate mapping

- **Depends on:** T01
- **Module:** marlo-data
- **Files touched:**
  - `java/.../data/model/AiReportConfiguration.java` (add `globalUnit` field + accessors)
  - `resources/xmls/AiReportConfigurations.hbm.xml` (add `globalUnit` many-to-one + the five audit properties;
    update the in-file explanatory note, which currently documents the columns as unmapped)
- **Constitutional checks:**
  - Existing GPL header preserved; no new Java file.
  - 2-space indent, 120 char limit, braces on same line.
  - Mapping shape mirrors `UserIdeas.hbm.xml`.
- **Tests:**
  - Application starts with `hibernate.hbm2ddl.auto=validate` — this is the real gate: a mismatch between mapping and
    schema fails startup.
  - Load a row and assert `getGlobalUnit()`, `isActive()`, `getCreatedBy()` are populated (previously `active` was
    always `true` and `createdBy` always `null`).
- **Done when:** `mvn checkstyle:check` passes and the app boots against the migrated schema.
- **Verification:** enable `show_sql` and confirm the `SELECT` now includes `global_unit_id` and `is_active`.

### DOMAIN-AI-SERVICES-001-T03 — DAO: scoped finder + HQL cleanup

- **Depends on:** T02
- **Module:** marlo-data
- **Files touched:**
  - `java/.../data/dao/AiReportConfigurationDAO.java` — add `findByGlobalUnit(long globalUnitId)` with javadoc
    stating the `null`-when-empty contract (ADR-3).
  - `java/.../data/dao/mysql/AiReportConfigurationMySQLDAO.java` — implement it following
    `ActivityTitleMySQLDAO.findByGlobalUnit`; change `findAll()`'s `where is_active=1` to the mapped property
    (ADR-5).
- **Constitutional checks:**
  - Layered pattern preserved (Manager → ManagerImpl → DAO → MySQLDAO).
  - Tenant predicate is a bound parameter, not string concatenation.
- **Tests:**
  - Unit: rows of Global Unit A are returned for A and not for B.
  - Unit: an inactive row of A is not returned.
  - Unit: a Global Unit with no rows returns `null` (contract, ADR-3).
- **Done when:** `mvn checkstyle:check` passes; unit tests green.
- **Verification:** `show_sql` output contains `global_unit_id=?` and the active predicate.

### DOMAIN-AI-SERVICES-001-T04 — Manager: expose the finder, make the write path transactional

- **Depends on:** T03
- **Module:** marlo-data
- **Files touched:**
  - `java/.../data/manager/AiReportConfigurationManager.java` — declare the scoped finder.
  - `java/.../data/manager/impl/AiReportConfigurationManagerImpl.java` — delegate; add `@Transactional` to
    `saveAiReportConfiguration` (DA-006).
- **Constitutional checks:**
  - Writes go through a `@Transactional` manager — without it the pool rolls the save back silently.
  - `deleteAiReportConfiguration` keeps its existing `@Transactional`.
- **Tests:**
  - Unit: save a new row with `createdBy` set → row exists after commit with a non-null `created_by`.
  - Unit: `deleteAiReportConfiguration(id)` → `is_active = 0` in the database (DA-005; this is the regression that
    proves the old no-op is fixed).
  - Unit: the deleted row no longer appears in `findByGlobalUnit`.
- **Done when:** unit tests green; `mvn checkstyle:check` passes.
- **Verification:** inspect the row in the database after each test.

### DOMAIN-AI-SERVICES-001-T05 — Action: scope the read to the session Global Unit

- **Depends on:** T04
- **Module:** marlo-web
- **Files touched:**
  - `java/.../action/ai/AiAction.java` — replace `findAll()` with the scoped finder using
    `this.getCurrentCrp().getId()`; keep the `try/catch` and the `LOG.error("Error loading AI report
    configurations", e)` message (OPS-001); update the class javadoc note about the data source.
- **Constitutional checks:**
  - The Global Unit id comes from the session (`APConstants.SESSION_CRP`), never from a request parameter (SEC-001).
  - Null-safety: `getCurrentCrp()` can return `null` on a broken session; the existing `try/catch` must still leave
    the page renderable.
- **Tests:**
  - Integration: user of Global Unit A sees only A's cards.
  - Integration: user of a Global Unit with no rows sees the empty state (FN-002).
- **Done when:** both integration checks pass locally.
- **Verification:** log in as AICCRA and as a second Global Unit; compare rendered cards.

### DOMAIN-AI-SERVICES-001-T06 — Template note

- **Depends on:** T05
- **Module:** marlo-web
- **Files touched:**
  - `webapp/WEB-INF/crp/views/ai/aiDashboard.ftl` — update only the `[#-- … --]` data-source note to say the rows are
    scoped to the current Global Unit. **No markup change** (UI-001).
- **Constitutional checks:**
  - No CSS/JS asset changes, so no cache-busting `?YYYYMMDD` bump is required.
  - The `reportConfigurations??` guard is preserved (ADR-3).
- **Tests:** visual diff of the rendered page before/after for AICCRA — must be identical.
- **Done when:** rendered HTML for AICCRA is byte-identical to the pre-change render (NF-002).
- **Verification:** save both renders and `diff` them.

### DOMAIN-AI-SERVICES-001-T07 — Update the module documentation

- **Depends on:** T06
- **Module:** docs
- **Files touched:**
  - `docs/specs/domain/ai-services/agent-context.md` — rewrite "The tenant caveat" (no longer true), the persistence
    caveats (soft delete and write path now work), and the `findAll()` section.
  - `docs/specs/domain/ai-services/task.md` (this file) — verification notes per task.
- **Constitutional checks:** commit prefixed `[SPEC:docs/specs/domain/ai-services]`.
- **Tests:** not applicable.
- **Done when:** the agent-context no longer describes behaviour that the change removed.
- **Verification:** re-read the doc against the merged code.

### DOMAIN-AI-SERVICES-001-T08 — QA pass against acceptance criteria

- **Depends on:** T07
- **Module:** n/a
- **Files touched:** none.
- **Constitutional checks:** none.
- **Tests:** every AC in requirements.md §6 walked manually.
- **Done when:** all ACs pass or a deviation is accepted with PMU sign-off.
- **Verification:** QA records the result per AC in the Jira issue A2-2433.

## 4. Dependency Graph

```
Pre-flight (OQ-1, OQ-2, data check)
  └── T01 (migration)
        └── T02 (entity + mapping)
              └── T03 (DAO)
                    └── T04 (manager)
                          └── T05 (action)
                                └── T06 (template note)
                                      └── T07 (docs)
                                            └── T08 (QA pass)
```

Strictly linear: the mapping needs the column, the DAO needs the mapping, and the action needs the DAO.

## 5. Testing Plan

### Unit
- `findByGlobalUnit`: tenant isolation, inactive-row exclusion, `null` on empty.
- `deleteAiReportConfiguration`: persists `is_active = 0` (the regression that proves the old no-op is gone).
- `saveAiReportConfiguration`: commits with a non-null `created_by`.

### Integration
- End-to-end render: HTTP GET `/ai/{crp}/ai.do` → `editAiStack` → `AiAction` → template, for a Global Unit with rows
  and for one without.
- Tenant isolation: confirm no request parameter can widen the scope (SEC-001) — attempt a crafted request and
  confirm `validSessionCrp` rejects a mismatched `{crp}`.

### Regression (manual, QA team)
- AICCRA AI section: three cards, unchanged copy and links, working outbound links with `user_email` / `user`
  appended.
- Menu: the `AI-CCRA` entry still appears only where `ai_section_active` is true.
- Smoke the rest of the `projects` package, since the `ai` Struts package extends it.

### Non-functional
- Not applicable at load level: single-digit rows, one indexed query per render.

### Accessibility
- Not applicable: no markup change (UI-001).

## 6. Operational Steps

### Migration deploy
- Flyway runs on Tomcat startup. Confirm the row in `flyway_schema_history` and re-run the pre-flight count queries
  post-deploy, expecting zero nulls.

### Coordination
- If OQ-2 found a non-AICCRA Global Unit with the flag on, notify PMU and that program **before** deploy: their AI
  section changes from three AICCRA cards to an empty state.
- No BI, CLARISA, CGSpace or AI-service coordination needed — the table is not exported and the external services are
  untouched.

### Configuration
- None. No new specificity, no new environment variable.

### Backups
- Production DB backup verified green within 24h prior to deploy (the `NOT NULL` step is the only non-additive
  change).

## 7. Rollback Plan

### Code
- Revert the merge commit on `staging`; re-deploy the previous artifact.
- The reverted code calls `findAll()`, which ignores `global_unit_id` — so the application keeps working with the
  migrated schema. **No schema rollback is required for a code rollback.**

### Data
- The migration is additive plus a `NOT NULL` constraint. If the constraint itself must be undone:
  `ALTER TABLE ai_report_configuration MODIFY COLUMN global_unit_id bigint(10) NULL;`
- Do not drop the column: the backfilled values are the only record of tenant ownership.

### Feature flag
- Not applicable — this change ships unconditionally (design.md §9). To hide the section entirely for a Global Unit,
  set its `ai_section_active` custom parameter to `false`; note that logged-in sessions only pick that up after
  re-login or a `crp_refresh` cycle.

## 8. Definition of Done

- [ ] All acceptance criteria from requirements.md §6 verified.
- [ ] OQ-1, OQ-2 and OQ-3 resolved and recorded in the Decision Log.
- [ ] Constitutional compliance checklist confirmed in the PR, including the two recorded deviations
      (no phase replication, no Validator) with their rationale.
- [ ] `mvn checkstyle:check` passes.
- [ ] Application boots with `hbm2ddl.auto=validate` against the migrated schema.
- [ ] SonarCloud: no new blockers / critical issues.
- [ ] Snyk: no new critical findings on changed paths.
- [ ] QA pass complete; defects closed or accepted with PMU sign-off.
- [ ] Documentation updated:
  - `agent-context.md` no longer describes the removed behaviour.
  - This `task.md`: every task marked done with verification notes.
  - No `reports/ai-context/*.md` update needed — routing, validation and replication contracts are unchanged.
- [ ] A2-2433 transitioned and linked to the merge commit.
- [ ] Merged to `staging`.
- [ ] Promoted to `main` via the release pipeline; production render verified for AICCRA and for one other Global Unit.
