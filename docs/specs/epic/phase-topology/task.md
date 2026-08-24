# Phase Topology & Replication Manager — Tasks

**Spec ID:** EPIC-PHASE-TOPOLOGY
**Status:** Draft
**Owner:** IBD Team — Kenji Tanaka
**Last Updated:** 2026-08-24
**Implements design:** docs/specs/epic/phase-topology/design.md
**Branching:** one feature branch per stage, from `staging`, named `phase-topology-s<n>-<slug>`.
**Target merge:** `staging` (promoted to `main` per release process).

---

## 1. Execution Context

- Java 17. Run with `scripts/run-marlo-java17.sh` (verify the active level in `marlo-parent/pom.xml`).
- Spring profile: `dev` locally (`marlo-web/src/main/resources/config/marlo-dev.properties`, bootstrapped from
  `marlo-test.properties`; never commit credential files).
- Flyway applies migrations from `marlo-web/src/main/resources/database/migrations/`.
- `mvn checkstyle:check` cannot run in this checkout (`maven-checkstyle-plugin:2.9.1` is incompatible with Java
  17). Style compliance is verified manually until that is fixed — see T00.
- The repository has 3 test files, so T05 (parity comparator) is the primary safety net, not unit tests.

## 2. Pre-flight Checklist

- [ ] `requirements.md` and `design.md` reviewed and Approved. This epic is a constitutional change per
      CLAUDE.md: requires the IBD team lead plus at least one of PMU lead, QA lead, Tech lead.
- [ ] **OQ-001, OQ-002, OQ-003 decided by the PMU lead and recorded in the requirements Decision Log.** Seeding
      cannot start before this. These are the three rule variants that are currently decided by accident.
- [ ] **OQ-004 answered:** a production-copy environment exists for T05. If not, stop and redesign stage 3 around
      per-section feature flags before writing code.
- [ ] **OQ-005 answered:** profile set frozen, or scope extended.
- [ ] OQ-006 and OQ-007 verified against the database (T01).
- [ ] `staging` pulled; stage branch created.

## 3. Task List

### Stage 0 — verification and decisions

#### EPIC-PHASE-TOPOLOGY-T00 — Establish a style verification path

- **Depends on:** none
- **Module:** marlo-parent
- **Files touched:** `marlo-parent/pom.xml` (candidate: bump `maven-checkstyle-plugin`)
- **Constitutional checks:** do not downgrade any dependency floor; Struts2 version validated against
  `struts2.version` if the POM is touched.
- **Tests:** not applicable.
- **Done when:** either `mvn checkstyle:check` runs on Java 17, or a documented manual procedure exists and is
  linked from this file.
- **Verification:** run the gate against one already-compliant file and one deliberately non-compliant file.

#### EPIC-PHASE-TOPOLOGY-T01 — Data integrity pre-check (OQ-006, OQ-007, DA-003)

- **Depends on:** none
- **Module:** none (read-only SQL)
- **Files touched:** `docs/specs/epic/phase-topology/agent-context.md` (record findings)
- **Constitutional checks:** read-only; no schema change.
- **Tests:** not applicable.
- **Done when:** four questions are answered with row counts — (a) phases whose `next_phase` belongs to a
  different `global_unit_id`; (b) duplicate `(global_unit_id, description, year, upkeep)` tuples; (c) phase id 377
  and any data authored against `year = 2029`; (d) Global Units with zero `editable = 1` or zero `visible = 1`
  phases.
- **Verification:** findings appended to `agent-context.md` with the date and the environment queried.

### Stage 1 — topology as data (no user-visible change)

#### EPIC-PHASE-TOPOLOGY-T02 — Migration 1: schema

- **Depends on:** T01
- **Module:** marlo-web (migrations)
- **Files touched:** `database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__PhaseReplicationTopology.sql` (new)
- **Constitutional checks:** Flyway naming `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`; no
  natural-key constraint yet (deferred to T12).
- **Tests:** apply and roll back on a scratch schema.
- **Done when:** both tables exist, `phases` has `sequence` and the four audit columns.
- **Verification:** `SHOW CREATE TABLE` for `phases`, `phase_replication_profiles`, `phase_replication_edges`.

#### EPIC-PHASE-TOPOLOGY-T03 — Entities, mappings, DAOs, managers

- **Depends on:** T02
- **Module:** marlo-data
- **Files touched:** see design §2 (`PhaseReplicationProfile`, `PhaseReplicationEdge`, their DAO / MySQLDAO /
  Manager / ManagerImpl, `ReplicationProfileEnum`, two `.hbm.xml`, `hibernate.cfg.xml`, `Phases.hbm.xml`,
  `Phase.java`)
- **Constitutional checks:** GPL header on every new Java file; layered pattern Manager -> ManagerImpl -> DAO ->
  MySQLDAO; `@Transactional` on every write method (a write with no transactional manager in the chain is silently
  rolled back); new queries use bound parameters, not string concatenation.
- **Tests:** not applicable at this stage (no behaviour change).
- **Done when:** the app boots, Hibernate validates both new mappings, `Phase` no longer hardcodes `isActive()`
  or a fabricated `getModifiedBy()`.
- **Verification:** boot with `show_sql` and confirm the mappings load; read one seeded profile through the manager.

#### EPIC-PHASE-TOPOLOGY-T04 — Migration 2: seed edges and `sequence` (MIG-001)

- **Depends on:** T03, and the OQ-001..003 decisions
- **Module:** marlo-web (migrations)
- **Files touched:** `database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__SeedPhaseReplicationEdges.sql` (new)
- **Constitutional checks:** derived from each Global Unit's live chain, never from a hardcoded cycle assumption;
  idempotent or guarded so a re-run cannot duplicate edges.
- **Tests:** covered by T05.
- **Done when:** the four profiles exist, every Global Unit has `sequence` backfilled, and the edge count per
  profile matches the chain-walk expansion documented in design §3.
- **Verification:** T05 reports zero differences.

### Stage 2 — parity and the read-only view

#### EPIC-PHASE-TOPOLOGY-T05 — Parity comparator (OPS-001)

- **Depends on:** T04
- **Module:** marlo-web
- **Files touched:** `action/superadmin/PhaseReplicationParityAction.java` (new),
  `webapp/WEB-INF/global/views/superadmin/phaseReplicationParity.ftl` (new), `resources/struts-superadmin.xml`
- **Constitutional checks:** GPL header; read-only, superadmin-gated; no write path.
- **Tests:** seed one deliberate edge difference and confirm the comparator reports it.
- **Done when:** for every (phase, profile) pair of every Global Unit, the resolver's ordered target list equals
  the list produced by the current hardcoded logic, and the report is empty (AC-001).
- **Verification:** run against the production copy identified in OQ-004; attach the report to the spec folder.

#### EPIC-PHASE-TOPOLOGY-T06 — Read-only replication graph view

- **Depends on:** T05
- **Module:** marlo-web
- **Files touched:** `action/crp/admin/CrpPhasesManagerAction.java` (new, read-only for now),
  `webapp/WEB-INF/crp/views/admin/phaseReplicationMatrix.ftl` (new), `resources/struts-admin.xml`,
  `resources/global.properties`, `webapp/WEB-INF/crp/views/admin/menu-admin.ftl`
- **Constitutional checks:** `crpAdminStack`; i18n keys, no literal strings; `overflow-x: auto` container per
  UI-002; bump `?YYYYMMDD` on any asset touched.
- **Tests:** manual walkthrough on a Global Unit with 20+ phases.
- **Done when:** an admin can read which phase replicates to which, per profile, without reading source.
- **Verification:** PMU and support confirm the view answers a real support question.

### Stage 3 — migrate the call sites

#### EPIC-PHASE-TOPOLOGY-T07 — Migrate one representative per profile

- **Depends on:** T05
- **Module:** marlo-data
- **Files touched:** `ProjectInnovationOrganizationManagerImpl` (CANONICAL), `DeliverableCrpManagerImpl`
  (DELIVERABLE_UPKEEP), `FundingSourceInfoManagerImpl` (FUNDING_SOURCE), `ProjectHighlightInfoManagerImpl`
  (HIGHLIGHTS)
- **Constitutional checks:** save and delete paths stay symmetric; `isPublication` stays in Java (FN-005);
  `@Transactional` preserved.
- **Tests:** for each, save and delete in a Planning, an UpKeep and a Reporting phase; assert the affected phase
  set matches the pre-migration set row for row.
- **Done when:** the four representatives read only the resolver and T05 still reports zero differences.
- **Verification:** manual save through the UI in all three phase roles, DB state compared before and after.

#### EPIC-PHASE-TOPOLOGY-T08 — Migrate remaining managers in batches

- **Depends on:** T07
- **Module:** marlo-data
- **Files touched:** the remaining ~99 `*ManagerImpl.java` with `getNext()`, excluding the 16 impact-pathway
  managers (out of scope). Delete `DeliverableManagerImpl.saveDeliverablePhase()` (line 715, dead code).
- **Constitutional checks:** as T07, per batch. One batch = one profile subset, one commit, one review.
- **Tests:** T05 re-run after each batch.
- **Done when:** no `ManagerImpl` in scope derives a destination from `getNext()`.
- **Verification:** `grep -rl "getNext()" marlo-data/src/main/java/.../manager/impl` returns only the
  out-of-scope impact-pathway files.

#### EPIC-PHASE-TOPOLOGY-T09 — Migrate the `marlo-web` call sites

- **Depends on:** T07
- **Module:** marlo-web
- **Files touched:** `DeliverableMetadataByWOS` (5 ternaries at 532, 572, 607, 650, 697), `OutcomesAction`,
  `ProjectListAction`, `DeliverableListAction`, `FundingSourceAction`, `FundingSourceListAction`,
  `ExpectedCRPProgress2019Action`, `OutcomesMilestonesAction`, `ProjectBudgetByPartnersReplicationAction`,
  `ProjectPartnersReplication`
- **Constitutional checks:** replication logic belongs in the manager layer; move it there rather than porting the
  ternary.
- **Tests:** manual walkthrough of the deliverable metadata-by-WOS flow and both bulk replication screens.
- **Done when:** no `marlo-web` action derives a replication destination from `getNext()`.
- **Verification:** grep as in T08, over `marlo-web/src/main/java`.

#### EPIC-PHASE-TOPOLOGY-T10 — Fix the phase DAO and BaseAction defects

- **Depends on:** T03
- **Module:** marlo-data, marlo-web
- **Files touched:** `PhaseMySQLDAO.java` (`getActivePhase` ordering by `sequence` and empty-result guard;
  `findAll` returning an empty list rather than `null`), `BaseAction.java` (`getPhases`, `getAllPhases`,
  `getAllCreatedPhases`, `getPhasesImpact`, `getPhasesByCycles` ordering by `sequence`; cache invalidation per
  OPS-002)
- **Constitutional checks:** bound parameters in any rewritten query.
- **Tests:** a Global Unit with zero `editable + visible` phases must not NPE.
- **Done when:** AC-010 passes and `getActivePhase()` no longer depends on `MAX(id)`.
- **Verification:** two concurrent sessions; save a phase change in one and confirm the other sees it without
  re-login.

#### EPIC-PHASE-TOPOLOGY-T11 — Rewrite the replication ai-context doc

- **Depends on:** T08, T09
- **Module:** docs
- **Files touched:** `reports/ai-context/persistence-replication-managerimpl.md`
- **Constitutional checks:** CLAUDE.md requires updating the ai-context docs when replication contracts change.
  This file currently documents `next.next` as the standing contract.
- **Tests:** not applicable.
- **Done when:** the doc describes the resolver, the four profiles and the edge table, and no longer instructs
  agents to recurse through `getNext()`.
- **Verification:** a fresh agent reading only the ai-context doc implements a new phase-scoped section correctly.

#### EPIC-PHASE-TOPOLOGY-T12 — Migration 3: natural-key and sequence constraints

- **Depends on:** T01, T10
- **Module:** marlo-web (migrations)
- **Files touched:** `database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__PhaseNaturalKeyConstraint.sql` (new), plus a
  data-cleanup migration if T01 found violations
- **Constitutional checks:** cleanup and constraint ship as separate migrations, in order.
- **Tests:** apply on a copy where T01's violations are present and confirm the cleanup clears them first.
- **Done when:** both unique constraints exist in every environment.
- **Verification:** attempt to insert a duplicate tuple and confirm rejection.

### Stage 4 — the Phases Manager

#### EPIC-PHASE-TOPOLOGY-T13 — `CrpPhasesValidator`

- **Depends on:** T12
- **Module:** marlo-web
- **Files touched:** `validation/crp/CrpPhasesValidator.java` (new)
- **Constitutional checks:** GPL header; extends `BaseValidator`; the eight rules of design §7.
- **Tests:** one negative case per rule.
- **Done when:** AC-003, AC-004, AC-005, AC-006 pass at the validator level.
- **Verification:** submit each invalid shape through the form and confirm a field-level error plus no write.

#### EPIC-PHASE-TOPOLOGY-T14 — Create, reorder, retire, delete

- **Depends on:** T13
- **Module:** marlo-web, marlo-data
- **Files touched:** `CrpPhasesAction.java` (add `validate()` guarded by `if (save)`; remove the `System.out.println`
  at line 100 and the always-false `String`/`Long` comparison at line 106), `PhaseManager` / `PhaseManagerImpl`
  (`appendPhase`, `reorder`, guarded `deletePhase`), `crpPhases.ftl` (remove the `yearLimit = 2018` gate at line
  58), `crpPhases.js`, `crpPhases.css`, `global.properties`
- **Constitutional checks:** save pipeline `Action.validate()` -> Validator -> manager; `crpAdminStack`; i18n;
  `?YYYYMMDD` bumped; audit fields populated on every write (SEC-003).
- **Tests:** create, reorder, hide, freeze and delete on a scratch Global Unit; delete refused when dependents
  exist.
- **Done when:** AC-004 through AC-009 and AC-011 pass.
- **Verification:** full walkthrough plus a `phase_replication_edges` diff after a reorder, which must be empty
  (AC-005).

#### EPIC-PHASE-TOPOLOGY-T15 — Edge editing and impact reporting

- **Depends on:** T14
- **Module:** marlo-web
- **Files touched:** `CrpPhasesManagerAction.java` (write path), `phaseReplicationMatrix.ftl`,
  `global.properties`, `webapp/crp/js/admin/` (new or extended, with `?YYYYMMDD`)
- **Constitutional checks:** as T14; the confirmation must state that existing rows are not moved (FN-011).
- **Tests:** edit an edge, confirm the impact report, confirm no existing row moved.
- **Done when:** AC-007 passes and the screen links to `marloBulkReplication`.
- **Verification:** edit one edge on a scratch Global Unit, then run the matching bulk replication action and
  confirm the target phase is populated.

#### EPIC-PHASE-TOPOLOGY-T16 — Migration 4: drop `next_phase`

- **Depends on:** T08, T09, T15
- **Module:** marlo-web (migrations), marlo-data
- **Files touched:** `database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__DropPhaseNextPointer.sql` (new),
  `Phases.hbm.xml`, `Phase.java`, `PhaseMySQLDAO.findPreviousPhase`
- **Constitutional checks:** ships in its own deploy, never bundled with other changes (design §16 risk 1).
- **Tests:** `grep -rn "next_phase" --include="*.java" --include="*.xml" --include="*.ftl"` returns nothing
  outside migrations. This is mandatory: `PhaseMySQLDAO` queries by column name, so a missed reference fails at
  runtime with no compiler error, and `findPreviousPhase` has 6 live callers including one
  (`BaseAction.isEvidenceNew():7849`) whose call sits outside its try/catch.
- **Done when:** the column is gone and `findPreviousPhase` is reimplemented on `sequence`.
- **Verification:** exercise all 6 `findPreviousPhase` call paths manually before and after.

#### EPIC-PHASE-TOPOLOGY-T17 — Manual QA pass against acceptance criteria

- **Depends on:** T16
- **Module:** none
- **Files touched:** this file (verification notes)
- **Constitutional checks:** every checklist item in requirements §7 resolved or an explicit deviation logged.
- **Tests:** AC-001 through AC-011.
- **Done when:** every AC has a dated verification note here.
- **Verification:** QA lead sign-off.

## 4. Dependency Graph

```
T00 (style gate)      T01 (data pre-check)
                        |
                        v
                      T02 (migration: schema)
                        |
                        v
                      T03 (entities + resolver)
                        |
                        +--> T10 (DAO / BaseAction fixes)
                        |
                        v
                      T04 (migration: seed)   <-- blocked on OQ-001..003
                        |
                        v
                      T05 (parity comparator) <-- blocked on OQ-004
                        |
                        +--> T06 (read-only graph view)   [stage 2 value milestone]
                        |
                        v
                      T07 (one representative per profile)
                        |
                        +--> T08 (remaining managers, batched)
                        +--> T09 (marlo-web call sites)
                                |
                                v
                              T11 (rewrite ai-context doc)
                                |
                              T12 (migration: constraints)   <-- also needs T01, T10
                                |
                                v
                              T13 (validator)
                                |
                                v
                              T14 (create / reorder / retire / delete)
                                |
                                v
                              T15 (edge editing + impact report)
                                |
                                v
                              T16 (migration: drop next_phase)   [separate deploy]
                                |
                                v
                              T17 (manual QA)
```

## 5. Testing Plan

- **Unit.** Only meaningful for the resolver and the validator, and only if a test harness is stood up — the
  repository currently has 3 test files. Add them under `marlo-web/src/test/java/` following the existing layout.
- **Parity (the primary net).** T05, re-run after every stage-3 batch. A non-empty report blocks the batch.
- **Regression, manual, per profile.** Save and delete in a Planning, an UpKeep and a Reporting phase for one
  section per profile, comparing the affected phase set before and after migration.
- **Regression, targeted.** The 6 `findPreviousPhase` call paths (T16). The deliverable metadata-by-WOS flow (T09).
  Both bulk replication screens (T09).
- **Manual QA.** AC-001..AC-011 (T17).
- **Not covered.** Automated end-to-end. Out of reach in this checkout; called out as a residual risk.

## 6. Operational Steps

1. Deploy T02 (schema) — additive, no downtime.
2. Deploy T04 (seed) — run T05 immediately after and before any manager migration.
3. Deploy stage-3 batches — each batch is independently deployable; T05 gates each one.
4. Deploy T12 (constraints) — after the data cleanup migration, in the same window.
5. Deploy stage 4 — announce to CRP admins that `editable = 1` expands the permission set (NF-003) and that a new
  phase starts empty until bulk replication runs (FN-011).
6. Deploy T16 — **its own deploy window**, nothing else bundled.
- No BI, AI-service, env-var or configuration coordination required.
- Notify MARLO support before stage 4 so the new screens are known before the first ticket.

## 7. Rollback Plan

| Stage | Rollback |
|---|---|
| 1 | Drop `phase_replication_profiles` and `phase_replication_edges`; drop the added `phases` columns. Additive, no data loss. |
| 2 | Revert the commits. Read-only, nothing persisted. |
| 3 | Revert the batch's commits. `next_phase` is still present and correct throughout, which is the reason DA-007 keeps it. |
| 4, before T16 | Revert the commits; the edge table stays but nothing writes to it. |
| 4, after T16 | No cheap rollback: `next_phase` is gone. Restore requires re-adding the column and rebuilding it from `sequence` plus the edge set. This is why T16 is last and deployed alone. |

## 8. Definition of Done

- [ ] AC-001 through AC-011 verified, each with a dated note in §3.
- [ ] No `ManagerImpl` or `marlo-web` action in scope derives a replication destination from `getNext()`.
- [ ] T05 reports zero differences on the production copy.
- [ ] `reports/ai-context/persistence-replication-managerimpl.md` describes the resolver, not `next.next`.
- [ ] Every constitutional checklist item in requirements §7 resolved, or a deviation recorded in the Decision Log.
- [ ] OQ-001 through OQ-007 closed, each with a Decision Log entry.
- [ ] Style gate green (T00), or the manual procedure evidenced for the ~114 modified files.
- [ ] The impact pathway exclusion is carried into its own spec folder, not left implicit.
