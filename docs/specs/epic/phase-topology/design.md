# Phase Topology & Replication Manager — Design

**Spec ID:** EPIC-PHASE-TOPOLOGY
**Status:** Draft
**Owner:** IBD Team — Kenji Tanaka
**Last Updated:** 2026-08-24
**Implements requirements:** FN-001..FN-012, NF-001..NF-003, DA-001..DA-007, UI-001..UI-003, SEC-001..SEC-003, OPS-001..OPS-002, MIG-001..MIG-002
**Touches modules:** marlo-web, marlo-data

---

## 1. Architecture Summary

Replication destinations move from derived pointer arithmetic to stored edges. Every call site asks one resolver;
the resolver reads a materialised edge list. `next_phase` is demoted to a presentation `sequence`, which makes
reordering semantically inert.

```
BEFORE                                   AFTER
------                                   -----
XManagerImpl.saveX()                     XManagerImpl.saveX()
  if (PLANNING)  -> next                    resolver.resolveTargets(phase, PROFILE)
  if (REPORTING) -> next.next                 |
     |                                        v
     v  recurse via getNext()             phase_replication_edges  (one query, ordered)
  phaseDAO.find() per level                   |
  full Phase collection per level             v
                                          for (Phase target : targets) { upsert }
```

Reference chain used throughout this spec (verified against `V2_6_0_20180808_1542__UpdatePhases_UpKeep.sql`):

```
P21 -> U21 -> R21 -> P22 -> U22 -> R22 -> P23 ...
POWB  UpKeep  AR     POWB   UpKeep  AR     POWB
Plan  Plan    Rep    Plan   Plan    Rep    Plan
upk=0 upk=1   upk=0  upk=0  upk=1   upk=0  upk=0
```

## 2. Module Footprint

### marlo-data

- New: `data/model/PhaseReplicationProfile.java`
- New: `data/model/PhaseReplicationEdge.java`
- New: `data/dao/PhaseReplicationEdgeDAO.java`, `data/dao/mysql/PhaseReplicationEdgeMySQLDAO.java`
- New: `data/dao/PhaseReplicationProfileDAO.java`, `data/dao/mysql/PhaseReplicationProfileMySQLDAO.java`
- New: `data/manager/PhaseReplicationManager.java`, `data/manager/impl/PhaseReplicationManagerImpl.java`
- New: `data/manager/PhaseReplicationResolver.java`, `data/manager/impl/PhaseReplicationResolverImpl.java`
- New: `data/model/ReplicationProfileEnum.java` (the frozen profile keys)
- New: `resources/xmls/PhaseReplicationProfiles.hbm.xml`, `resources/xmls/PhaseReplicationEdges.hbm.xml`
- Modified: `resources/hibernate.cfg.xml` (two `<mapping>` entries)
- Modified: `resources/xmls/Phases.hbm.xml` (audit columns, `sequence`)
- Modified: `data/model/Phase.java` (audit fields, `sequence`; remove the hardcoded `isActive()`,
  `getModifiedBy()`, `getModificationJustification()`)
- Modified: `data/dao/mysql/PhaseMySQLDAO.java` (`getActivePhase` ordering, `findAll` empty-list contract,
  `appendPhase` / `deletePhase` support)
- Modified: `data/manager/PhaseManager.java` + `impl/PhaseManagerImpl.java`
- Modified: ~103 `data/manager/impl/*ManagerImpl.java` (stage 3; the impact pathway family is out of scope)
- Modified: `config/APConstants.java`

### marlo-web

- New: `action/crp/admin/CrpPhasesManagerAction.java` (or extend `CrpPhasesAction`)
- New: `validation/crp/CrpPhasesValidator.java`
- New: `action/superadmin/PhaseReplicationParityAction.java` (OPS-001, read-only)
- New: `webapp/WEB-INF/crp/views/admin/phaseReplicationMatrix.ftl`
- Modified: `webapp/WEB-INF/crp/views/admin/crpPhases.ftl`
- Modified: `webapp/crp/js/admin/crpPhases.js`, `webapp/crp/css/admin/crpPhases.css` (bump `?YYYYMMDD`)
- Modified: `resources/struts-admin.xml`, `resources/struts-superadmin.xml`
- Modified: `resources/global.properties` (+ `resources/custom/*.properties` where overridden)
- Modified: `action/BaseAction.java` (cache invalidation, `getPhases*` ordering by `sequence`)
- Modified: 11 actions that derive replication destinations from `getNext()` — see §6
- Modified: `config/APConstants.java` (must mirror `marlo-data`)

### marlo-core / marlo-utils

- Not applicable.

## 3. Data Model Changes

### Migration 1 — schema

File: `marlo-web/src/main/resources/database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__PhaseReplicationTopology.sql`

```sql
CREATE TABLE `phase_replication_profiles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key` varchar(64) NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` text NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `phase_replication_profiles_key_UQ` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `phase_replication_edges` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `profile_id` bigint(20) NOT NULL,
  `source_phase_id` bigint(20) NOT NULL,
  `target_phase_id` bigint(20) NOT NULL,
  `sequence` int(11) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `active_since` datetime NULL,
  `created_by` bigint(20) NULL,
  `modified_by` bigint(20) NULL,
  `modification_justification` text NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `phase_replication_edges_UQ` (`profile_id`,`source_phase_id`,`target_phase_id`),
  KEY `phase_replication_edges_source_IX` (`source_phase_id`,`profile_id`,`sequence`),
  CONSTRAINT `phase_replication_edges_profile_FK` FOREIGN KEY (`profile_id`)
    REFERENCES `phase_replication_profiles` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `phase_replication_edges_source_FK` FOREIGN KEY (`source_phase_id`)
    REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `phase_replication_edges_target_FK` FOREIGN KEY (`target_phase_id`)
    REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `phases`
  ADD COLUMN `sequence` int(11) NULL AFTER `next_phase`,
  ADD COLUMN `active_since` datetime NULL,
  ADD COLUMN `created_by` bigint(20) NULL,
  ADD COLUMN `modified_by` bigint(20) NULL,
  ADD COLUMN `modification_justification` text NULL;

ALTER TABLE `phases`
  ADD CONSTRAINT `phases_created_by_FK` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `phases_modified_by_FK` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);
```

The natural-key uniqueness of DA-003 is deliberately NOT in this migration; see Migration 3.

### Migration 2 — seed (MIG-001)

File: `V2_6_0_<YYYYMMDD>_<HHMM>__SeedPhaseReplicationEdges.sql`

Profiles seeded from the variants found in code:

| key | name | source semantics |
|---|---|---|
| `CANONICAL` | Canonical | 84 managers. Planning -> from `next` forward. Reporting -> from `next.next` forward. |
| `DELIVERABLE_UPKEEP` | Deliverable metadata | 9 managers. Planning only when `upkeep` -> from `next` forward. POWB none. Reporting -> from `next.next` forward. |
| `FUNDING_SOURCE` | Funding source | 5 managers. Planning -> from `next` forward. Reporting none. |
| `HIGHLIGHTS` | Highlights | 5 managers. Reporting -> from `next` forward (see OQ-002). Planning none. |

`sequence` is derived by walking each Global Unit's live `next_phase` chain. The edge set per profile is the
suffix-of-chain expansion of the table above.

Resolved targets on the reference chain, which the seed must reproduce:

| Profile | from POWB | from UpKeep | from AR |
|---|---|---|---|
| `CANONICAL` | U21,R21,P22,U22,R22,P23,... | R21,P22,U22,R22,P23,... | U22,R22,P23,U23,... (skips P22) |
| `DELIVERABLE_UPKEEP` | (none) | R21,P22,U22,R22,... | U22,R22,P23,... |
| `FUNDING_SOURCE` | U21,R21,P22,U22,R22,... | R21,P22,U22,... | (none) |
| `HIGHLIGHTS` | (none) | (none) | P22,U22,R22,P23,... |

`sequence` on `phases` is backfilled in the same migration from the chain walk.

### Migration 3 — constraints, after data cleanup

File: `V2_6_0_<YYYYMMDD>_<HHMM>__PhaseNaturalKeyConstraint.sql`

```sql
ALTER TABLE `phases`
  ADD CONSTRAINT `phases_natural_key_UQ` UNIQUE (`global_unit_id`,`description`,`year`,`upkeep`);
ALTER TABLE `phases`
  ADD CONSTRAINT `phases_sequence_UQ` UNIQUE (`global_unit_id`,`sequence`);
```

Blocked on OQ-006 and OQ-007: the constraint cannot be added while duplicate or cross-tenant rows exist. A
read-only pre-check query ships with the parity comparator.

### Migration 4 — drop `next_phase`, after stage 3

File: `V2_6_0_<YYYYMMDD>_<HHMM>__DropPhaseNextPointer.sql`. Deferred per DA-007.

### Entity changes

- `Phase.java`: add `sequence`, `activeSince`, `createdBy`, `modifiedBy`, `modificationJustification`. Remove the
  hardcoded `isActive()` returning `true` and `getModifiedBy()` returning a fabricated `User(3)`.
- New `PhaseReplicationProfile`, `PhaseReplicationEdge` extending `MarloBaseEntity`, implementing `IAuditLog`.

### Backfill

- `phases.sequence` from the chain walk (Migration 2).
- `phase_replication_edges` from the chain walk per profile (Migration 2).
- No backfill of `created_by` / `modified_by`; historical authorship is unknown and MUST stay NULL rather than be
  invented.

## 4. API / Action Surface

### Struts actions (.do)

| Route | Action class | Stack | View result |
|---|---|---|---|
| `{crp}/crpPhases` | `CrpPhasesAction` (extended) | `crpAdminStack` | `/WEB-INF/crp/views/admin/crpPhases.ftl` |
| `{crp}/phaseReplication` | `CrpPhasesManagerAction` | `crpAdminStack` | `/WEB-INF/crp/views/admin/phaseReplicationMatrix.ftl` |
| `phaseReplicationParity` | `PhaseReplicationParityAction` | superadmin stack | `/WEB-INF/global/views/superadmin/phaseReplicationParity.ftl` |

### Spring MVC REST

- Not applicable. No `/api/*` surface.

### Existing JSON endpoints

- Not applicable. Per AGENTS.md, no new `*.json` Struts paths.

## 5. Frontend Composition

- `crpPhases.ftl` gains create, reorder and delete controls. The hardcoded `yearLimit = 2018` gate at line 58 and
  its POWB-2019 special case are removed and replaced by the validator.
- `phaseReplicationMatrix.ftl` renders one source-phase x target-phase grid per profile, inside an
  `overflow-x: auto` container (UI-002).
- Reorder uses `jquery-ui` sortable, already a declared `pageLibs` entry in this view.
- `crpPhases.js` gains client-side guards mirroring the validator, plus a delete confirmation. `?YYYYMMDD` bumped
  on both `crpPhases.js` and `crpPhases.css` per UI-003.
- Expandable blocks are not used here; the grid is not an accordion list.

## 6. Persistence & Phase Replication Plan

This section IS the epic. The pattern that replaces the current one:

```java
// BEFORE — repeated in 119 ManagerImpl classes
if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
  this.saveXPhase(phase.getNext(), ...);            // then recurse on getNext()
}
if (phase.getDescription().equals(APConstants.REPORTING)) {
  if (phase.getNext() != null && phase.getNext().getNext() != null) {
    this.saveXPhase(phase.getNext().getNext(), ...);
  }
}

// AFTER
for (Phase target : replicationResolver.resolveTargets(phase, ReplicationProfileEnum.CANONICAL)) {
  this.upsertXInPhase(target, ...);                 // no recursion
}
```

Save and delete paths MUST stay symmetric, as they are today.

Entity-level predicates stay in Java (FN-005):

```java
if (isPublication) { return result; }               // not an edge concern
```

Call sites to migrate in `marlo-web` (§2), all deriving destinations from `getNext()`:
`DeliverableMetadataByWOS` (5 occurrences of the `REPORTING ? next.next : next` ternary at lines 532, 572, 607,
650, 697), `OutcomesAction`, `ProjectListAction`, `DeliverableListAction`, `FundingSourceAction`,
`FundingSourceListAction`, `ExpectedCRPProgress2019Action`, `OutcomesMilestonesAction`,
`ProjectBudgetByPartnersReplicationAction`, `ProjectPartnersReplication`.

`DeliverableManagerImpl.saveDeliverablePhase()` (line 715) is dead code — its only caller is itself. It is deleted,
not migrated. The live path is `DeliverableListAction.addDeliverablePhase()` (line 150).

## 7. Validation & Save Pipeline

`CrpPhasesAction` currently has no `validate()` and no Validator while already writing `editable`, the switch that
gates write permissions platform-wide. The constitutional pattern is restored:

```
crpAdminStack -> CrpPhasesAction.validate() [if (save)] -> CrpPhasesValidator -> PhaseManager / PhaseReplicationManager
```

`CrpPhasesValidator` rules:

1. Every edge's source and target belong to the same Global Unit (FN-012).
2. The edge graph per profile is acyclic (FN-012).
3. `(global_unit_id, description, year, upkeep)` is unique (DA-003).
4. `sequence` is unique and contiguous per Global Unit (DA-005).
5. `startDate < endDate`; `year` coherent with the dates.
6. At least one phase with `visible = 1` and at least one with `editable = 1`, because
   `PhaseMySQLDAO.getActivePhase()` dereferences the first row of a `MAX(id)` result and NPEs on an empty set.
7. Deletion target has no dependent rows and is not referenced by `custom_parameters` (`current_phase`,
   `crp_aiccra_af_start_phase`).
8. `description` is a `PhaseDescription` enum value, never free text.

Also fixed in this action: the `System.out.println` at line 100, and the `String`-vs-`Long` comparison at line 106
(`customParameter.getValue().equals(defaultPhaseID)`) which is always false so the guard never short-circuits.

## 8. Permissions & Edit Gates

- Every route declares `crpAdminStack` (SEC-001).
- No new permission strings. `BaseAction.generatePermission()` appends `:description:year` to `params[0]`, and the
  `getPermissions` stored procedure substitutes `{0}` with `CONCAT(acronym, ':', ph.description, ':', ph.year)`
  across 27 sub-selects, each filtered by `ph.editable = 1`. New phases are therefore covered automatically.
- Consequence to surface in the UI (NF-003): `editable = 1` is a security operation, not a display toggle. It is
  the mechanism that makes past phases immutable, and each newly editable phase multiplies the per-user permission
  temp table.

## 9. Specificity / Feature-Flag Strategy

Not applicable as a product flag. If stage 3 needs a per-Global-Unit or per-section rollout switch, it goes
through `parameters` + `custom_parameters` with the constant value equal to `parameters.key`, declared in both
`APConstants.java` files. Decide alongside OQ-004: if no production-copy environment exists, per-section flags stop
being optional.

## 10. Integration Points

- **Bulk replication tooling** (`DeliverablesReplicationAction`, `FundingSourcesReplicationAction`,
  `ProjectsOutcomesReplicationAction`, `ActivitiesReplicationAction`, `ProjectPartnersReplication`,
  `ProjectBudgetByPartnersReplicationAction`, `marloBulkReplication.ftl`): the only path that backfills an existing
  phase. FN-011 links to it; the two actions that walk `getNext()` are migrated in stage 3.
- **`CrpProjectPhases`**: populates `project_phases` for a phase. Unchanged, but the create flow must point at it.
- **BI / Pentaho summaries**: read phases by `description` and `year`. Unaffected as long as `description` stays
  enum-constrained.
- **CLARISA, CGSpace, AI services, S3, Pusher**: not applicable.

## 11. Observability

- Phase and edge mutations become real audit-log entries via `IAuditLog` / `HibernateAuditLogListener` (DA-004).
- The parity comparator emits a structured report per Global Unit (OPS-001) and is retained as a regression tool.
- Log at WARN when a resolver call returns an empty target list for a phase that has forward neighbours — the
  most likely symptom of a mis-seeded or mis-edited graph.

## 12. Performance & Scalability

- **Improvement.** Today resolving destinations for a phase with N forward targets costs N levels of recursion,
  each with a `phaseDAO.find()` and the load of one of the 84 collections mapped on `Phase`. After: one indexed
  query on `phase_replication_edges_source_IX` plus an in-memory loop (NF-001).
- **Caching.** The edge set per Global Unit is small (tens of rows per profile) and changes rarely; cache it and
  invalidate on write, alongside the phase list invalidation of OPS-002.
- **Cost to watch.** `editable = 1` expands the permission temp table (NF-003).
- **Not addressed.** The replication bodies themselves still stream whole `Phase` collections to find their
  existing row. Out of scope, worth a follow-up spec.

## 13. Security Considerations

- `editable` is an authorization control (§8). Every change to it must be attributable (SEC-003).
- The parity comparator is read-only and superadmin-gated.
- `PhaseMySQLDAO` builds HQL by string concatenation. New queries MUST use bound parameters. See §16 for the
  related migration hazard.

## 14. Backwards Compatibility & Rollout

Four stages. The ordering is forced by MIG-002.

| Stage | Content | User-visible |
|---|---|---|
| 1 | Migrations 1-2, entities, resolver, profile enum. Nothing calls the resolver yet. | No |
| 2 | Parity comparator (OPS-001) + the read-only replication graph view | Read-only view only |
| 3 | Migrate ~103 managers + 11 actions in batches, one profile representative first. Migration 3. Rewrite `persistence-replication-managerimpl.md`. | No |
| 4 | Phases Manager: create, reorder, retire, delete, edge editing. Migration 4. | Yes |

- **Dual-running.** Through stages 1-3, `next_phase` and the edge table coexist; the comparator asserts they
  agree. This is the rollback safety.
- **Rollback.** Stage 1-2 are additive; drop the two tables. Stage 3 rolls back per batch by reverting the batch's
  commits — `next_phase` is still present and correct. After Migration 4 there is no cheap rollback, which is why
  it is last and separate.
- **Intermediate value.** The stage 2 read-only graph view answers "which phase replicates to which, for this
  section" for PMU and support, and validates the model before the ~114-file refactor is committed.

## 15. Decision Records

- **DR-001 — Materialised edges, not a derived closure.** Storing the full target list per source makes the admin
  UI a direct view of stored data, removes the runtime traversal, and makes a cycle harmless (a repeated target,
  deduplicated) instead of fatal.
- **DR-002 — Profiles, not per-section configuration.** Five variants map to domain families, so four seeded
  profiles cover 103 managers. Per-section configuration would be ~103 knobs nobody administers.
- **DR-003 — `sequence` replaces `next_phase` as the order.** Decouples presentation from semantics, which is what
  makes FN-007 (reorder) safe. Also fixes `getActivePhase()` ordering by `MAX(id)`.
- **DR-004 — No `is_active` on `phases`.** See requirements Decision Log.
- **DR-005 — Impact pathway excluded from v1.** Parts of it do not work; see OQ-003.
- **DR-006 — UI last.** See MIG-002.

## 16. Open Risks

1. **No compile-time safety net.** The repository has 3 test files. `PhaseMySQLDAO` queries `phases` by column
   name, not by mapped property: `findCycle()` uses `global_unit_id` and `findPreviousPhase()` uses `next_phase`,
   while `Phases.hbm.xml` maps them as `crp` and `next`. That pattern is repo-wide (674 `findAll("from ...")`
   sites) and works today, but it means dropping `next_phase` in Migration 4 breaks `findPreviousPhase()` at
   runtime with no compiler error. `findPreviousPhase` has 6 live callers, including
   `BaseAction.isEvidenceNew():7849` where the call sits outside the surrounding try/catch. Mitigation: grep for
   the literal column strings as an explicit task, and keep Migration 4 in its own deploy.
2. **The three product decisions (OQ-001..003) are load-bearing.** Seeding cannot start until they are made.
3. **Retroactive edge changes.** FN-011 documents the gap but does not close it. Admins will expect edits to move
   existing data.
4. **Cross-tenant chain (OQ-006).** If Global Unit 26's phases point at Global Unit 24's, the seed derived from
   the live chain would reproduce a cross-tenant graph and Migration 3's constraint would fail.
5. **Batch discipline in stage 3.** 103 managers is a mechanical but large refactor; a partially migrated section
   would replicate through two topologies at once.
6. **Checkstyle gate is unavailable** in this checkout (`maven-checkstyle-plugin:2.9.1` vs Java 17), so style
   compliance across ~114 modified files must be verified another way.
