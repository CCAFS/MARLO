# AI Services — Per-Global-Unit Section Content — Tasks

**Spec ID:** DOMAIN-AI-SERVICES-001
**Status:** In Progress — T01..T07 implemented, T08 pending. See §9 for the open risks.
**Owner:** IBD Team
**Last Updated:** 2026-08-27
**Implements design:** docs/specs/domain/ai-services/design.md
**Jira:** A2-2433 (child of A2-2055 — Enhancements 2026)
**Branching:** feature branch from `staging`. Actual branch: `A2-2433-Make-the-AI-CCRA-section-ai-module-configurable-per-Global-Unit`.
**Implementation commit:** `be714a066a`.
**Target merge:** `staging` (then promoted to `main` per release process).

## 1. Execution Context

- Java 17. Run locally with `scripts/run-marlo-java17.sh` (verify the level against `marlo-parent/pom.xml`).
- Spring profile: local `marlo-<profile>.properties`, bootstrapped from `marlo-test.properties` (never committed).
- Flyway runs on Tomcat startup; confirm application through `flyway_schema_history`.
- Database: MySQL. The affected table is `ai_report_configuration` (3 rows in production today).
- Reference: `docs/specs/domain/ai-services/agent-context.md` for the as-built module map.

## 2. Pre-flight Checklist

- [ ] `requirements.md` and `design.md` reviewed and approved. **Still open** — both are `Status: Draft`.
- [x] **OQ-1 answered** — one Global Unit per row, no shared rows. Confirmed by the requester on 2026-08-27 when
      approving `NOT NULL`; **not** a PMU sign-off. Original text: confirm that a tool row belongs to exactly one Global Unit (no shared/global rows).
      If the answer is "shared rows are needed", stop: DA-003 and ADR-1 change, and T01 must be redesigned.
- [ ] **OQ-2 NOT answered — open operational risk.** The query below was never run against production. If any
      non-AICCRA Global Unit has `ai_section_active = true`, it will visibly lose its cards on deploy. **This must be
      run and coordinated with PMU before merge.** Query:
      ```sql
      SELECT gu.id, gu.acronym, cp.value
      FROM custom_parameters cp
      JOIN parameters p ON p.id = cp.parameter_id
      JOIN global_units gu ON gu.id = cp.global_unit_id
      WHERE p.`key` = 'ai_section_active' AND cp.is_active = 1;
      ```
      Any Global Unit other than AICCRA with `value = 'true'` will visibly lose its cards on deploy and must be told.
- [ ] **Pre-flight data check NOT run** — no database was available. Mitigated in code instead: the migration guards
      the backfill and removes rows it cannot attribute. Query:
      ```sql
      SELECT COUNT(*) FROM ai_report_configuration;                          -- expect 3 in production
      SELECT COUNT(*) FROM ai_report_configuration WHERE is_active = 0;      -- expect 0
      ```
- [x] Pull latest `staging`; create the feature branch from it. `staging` was clean and level with `origin/staging`
      at `a8a22eb96e`.
- [x] Confirmed `AiReportConfigurationManager` has exactly one caller (`AiAction`). `Clone.java` also names the
      entity, but only as a string in a code-generator `main()`. Command:
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
- **Verification notes (2026-08-27) — written, NOT executed (R2).** Shipped as
  `V2_6_0_20260827_0747__AddGlobalUnitToAiReportConfiguration.sql`, FK named `ai_report_configuration_global_units_FK`.
  The backfill is guarded with `AND EXISTS (SELECT 1 FROM global_units WHERE id = 45)` and a `DELETE` removes rows it
  cannot attribute. This matters: Global Unit 45 exists only in the real databases, while the three rows are seeded
  unconditionally by an earlier migration. Unguarded, the FK add fails with MySQL 1452 and — DDL not being
  transactional — the migration lands half applied and Flyway halts every later one.
  Empty-table safety (MIG-002) holds by reasoning: zero-row `UPDATE`/`DELETE` are successes in MySQL, and `NOT NULL`
  plus a FK validate nothing on a table with no rows. Not idempotent, so a re-run after a `flyway repair` fails on
  `duplicate column`; no MARLO migration is idempotent.

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
- **Verification notes (2026-08-27) — DONE.** `globalUnit` many-to-one plus the five audit properties mapped; the
  in-file note now documents them as mapped. GPL headers untouched, no new Java file.
  Instead of the `hbm2ddl.auto=validate` boot check (no database), the full Hibernate metadata was built offline from
  the 458 mappings declared in `hibernate.cfg.xml`: it resolves, and `AiReportConfiguration` binds all ten properties
  with `global_unit_id` as `optional=false`. The emitted `SELECT` includes `global_unit_id` and `is_active`, confirmed
  from the compiled query plan rather than from `show_sql`. **The boot check against a real schema is outstanding
  (R4).** `mvn checkstyle:check` cannot run (R6); style checked by hand.

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
- **Verification notes (2026-08-27) — DONE.** Implemented as `findAllByGlobalUnit`, returning an **empty list** rather
  than the `null` of ADR-3, and `findAll()` was **removed** rather than having its HQL fixed per ADR-5. Both came out
  stricter than the plan — an empty list cannot NPE, and with no unscoped read the tenant filter cannot be bypassed —
  so they are left as built for the team to confirm when it reviews the (still Draft) design.
  The tenant predicate is a bound parameter (`:globalUnitId`), and the layered pattern is preserved.
  The HQL was compiled against the real metadata and emits exactly:
  `... from ai_report_configuration aireportco0_ where aireportco0_.global_unit_id=? and aireportco0_.is_active=1`
  `order by aireportco0_.id` — note `arc.globalUnit.id` resolves to the FK column with no join, and `ORDER BY` makes
  card order deterministic. **The three unit tests are NOT written** (R3).

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
- **Verification notes (2026-08-27) — partially DONE.** `@Transactional` added to `saveAiReportConfiguration`;
  `deleteAiReportConfiguration` keeps its own. The finder is exposed on the manager.
  The soft-delete fix is established by construction rather than by test: `AuditColumnHibernateListener` resolves
  audit properties through `ArrayUtils.indexOf(propertyNames, ...)` on the entity metamodel, so mapping `active`,
  `createdBy` and `activeSince` in T02 is exactly what makes `setActive(false)` reach `is_active` and what lets an
  insert populate `created_by`. **All three unit tests are NOT written (R3).**

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
- **Verification notes (2026-08-27) — code DONE, verification NOT done.** `AiAction.prepare()` reads the scoped
  finder with `this.getCurrentCrp().getId()`; the id comes from the session, never from a request parameter
  (SEC-001). The `try/catch` and the `LOG.error("Error loading AI report configurations", e)` line are preserved
  (OPS-001), and a `getCurrentCrp() == null` branch now logs a warning and falls back to an empty list so the page
  stays renderable. The class javadoc was rewritten.
  **Neither integration check was run (R3).**

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
- **Verification notes (2026-08-27) — DEVIATES from UI-001 and NF-002 (R5).** The plan allowed only a comment
  change, but the requester explicitly asked for the breadcrumb to read `AI-CCRA`, so the template now
  renders a **single** breadcrumb entry instead of two, and `breadCrumb.menu.ai` changed from `AI` to `AI-CCRA` in
  `global.properties` and `custom/test.properties`. The dropped second entry read `AICHAT BOT` and linked back to
  this same page. Two i18n keys were also added for the empty state
  (`userIdea.noReportsConfigured`, `.noReportsConfiguredDescription`), which the template had been supplying as
  `default=` literals; wording provided by the requester.
  Consequently the rendered HTML for AICCRA is **not** byte-identical: the breadcrumb differs by design (R5).
  The `reportConfigurations??` guard is preserved. No CSS/JS asset changed, so no cache-busting bump was needed.

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
- **Verification notes (2026-08-27) — DONE.** `agent-context.md` rewritten: the tenant caveat is replaced by a
  "Tenancy" section, the persistence caveats now describe what the mapping fixed, the `findAll()`-returns-null
  section is replaced, and a schema-history table plus updated change recipes were added. Its stale claim that this
  folder holds no spec set was corrected — that claim is why this plan was missed on the first pass.
  A new Known Gap #7 records the still-AICCRA-specific section label. Commit prefixed
  `[SPEC:docs/specs/domain/ai-services]` as required.

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

- [ ] All acceptance criteria from requirements.md §6 verified. **Reasoned as satisfied by the code, none executed** —
      no database or running application was available.
- [ ] OQ-1, OQ-2 and OQ-3 resolved and recorded in the Decision Log. **OQ-1 answered by the requester (not PMU);
      OQ-2 still open and blocking (see Pre-flight); OQ-3 not addressed.**
- [ ] Constitutional compliance checklist confirmed in the PR. **§9 lists the open risks; R1 is blocking.**
- [ ] `mvn checkstyle:check` passes. **Cannot run** — `maven-checkstyle-plugin` 2.9.1 against `checkstyle` 8.18 throws
      `NoSuchMethodError: Checker.setClassloader`, reproduced on clean `staging`. Pre-existing repo defect, not caused
      by this change. Style verified by hand.
- [ ] Application boots with `hbm2ddl.auto=validate` against the migrated schema. **Not done.** Substituted by an
      offline build of the full Hibernate metadata (458 mappings) — necessary but not sufficient.
- [ ] SonarCloud: no new blockers / critical issues. **Not run.**
- [ ] Snyk: no new critical findings on changed paths. **Not run.**
- [ ] QA pass complete; defects closed or accepted with PMU sign-off. **T08 not started.**
- [x] Documentation updated:
  - `agent-context.md` no longer describes the removed behaviour.
  - This `task.md`: verification notes recorded per task, including what was not verified.
  - No `reports/ai-context/*.md` update needed — confirmed by grep: none of them mention this module.
- [ ] A2-2433 transitioned and linked to the merge commit. **Not done** — the Jira issue was read but not updated.
- [ ] Merged to `staging`. **Not done** — commit `be714a066a` is local and unpushed.
- [ ] Promoted to `main` via the release pipeline; production render verified for AICCRA and for one other Global Unit.

## 9. Open Risks Before Merge (recorded 2026-08-27)

Only items that can actually cause a problem. Cosmetic differences from this plan (branch name, migration filename)
and choices that came out stricter than planned (empty list instead of `null`, no unscoped `findAll()`, guarded
backfill, deterministic ordering) are not listed.

### R1 — OQ-2 was never run. Blocking.

The pre-flight query identifying which Global Units have `ai_section_active = true` in production was not executed.
**Any non-AICCRA Global Unit with the flag on will visibly lose its AI cards on deploy**, because the read is now
scoped and only Global Unit 45 has rows. Run the query in §2 and coordinate with PMU before merge. If such a Global
Unit exists, either insert its own rows or accept the empty state with its programme's agreement.

### R2 — The migration was never executed against a database.

No database was available, so the SQL is unverified. The `NOT NULL` step is the only non-additive change in it. Apply
it to a restore of a production-like schema before merge and confirm:

```sql
SELECT COUNT(*) FROM ai_report_configuration WHERE global_unit_id IS NULL;  -- expect 0
SHOW CREATE TABLE ai_report_configuration;                                 -- NOT NULL, FK, index present
```

### R3 — Tenant isolation and the soft-delete fix are asserted by construction, not demonstrated.

No unit or integration test was written. Both behaviours follow from the code, but neither has been observed:

- **Isolation:** the HQL was compiled against the real mappings and emits `where global_unit_id=? and is_active=1`, so
  the filter is provably in the SQL. What is untested is the end-to-end path — that a user of Global Unit A sees only
  A's cards.
- **Soft delete:** `deleteAiReportConfiguration` now reaches `is_active` only because `active` became a mapped
  property. That the `UPDATE` really writes `is_active = 0` has not been checked against a row.

### R4 — The application was never started against the migrated schema.

`hbm2ddl.auto=validate` was not run. A mapping/schema mismatch fails startup, so this is the cheapest remaining
gate. The full Hibernate metadata (458 mappings) was built offline and resolves, with `global_unit_id` bound as
non-optional — necessary, but it does not prove the mapping matches the migrated table.

### R5 — AC "rendered HTML byte-identical for AICCRA" (NF-002) will fail as written.

The breadcrumb changed on purpose, at the requester's instruction: one entry reading `AI-CCRA` instead of two reading
`AI` / `AICHAT BOT`. QA should expect this difference rather than raise it as a defect. Everything else about the
AICCRA render — the three cards, their copy, their links — is unchanged.

### R6 — `mvn checkstyle:check` cannot run, so that DoD gate is unverifiable.

The repo pairs `maven-checkstyle-plugin` 2.9.1 with `checkstyle` 8.18 and throws
`NoSuchMethodError: Checker.setClassloader`. Reproduced on clean `staging`, so it is pre-existing and unrelated to this
change. Style was checked by hand: added lines within 120 chars, 2-space indent, no tabs, no trailing whitespace.
