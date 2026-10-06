# Project Activities Save Performance And Future-Phase Duplication — Proposal

## Document Control

| Field | Value |
|---|---|
| **Spec Path** | `bugfix/activities-save-performance` |
| **Proposal ID** | `BUG-ACTIVITIES-SAVE-PERFORMANCE-001` |
| **Slug** | `activities-save-performance` — the leading token of the argument; the rest of the argument is context |
| **Type** | **Bug** (performance + data integrity) |
| **Approval Mode** | **`gated`** — no end-to-end mandate was given |
| **Parent Spec** | none — flat spec |
| **Depends on** | **none** |
| **Parallel-safe** | **no** — touches `ActivityManagerImpl`, `DeliverableActivityManagerImpl` and `ActivityMySQLDAO`, which `DeliverableAction`, `ProjectInfoManagerImpl` and `ActivitiesReplicationAction` also use, and it ships a data migration |
| **Status** | **Approved** — 2026-09-29 by Kenji Tanaka (Option B; R1 settled: keep the newest copy) |
| **Date** | 2026-09-29 |
| **Working branch** | `staging` at `12e063e323` |
| **Evidence base** | Code at `12e063e323`; read-only queries on the local copies `aiccradb1`–`aiccradb4`. All four are the same snapshot (last `activities` write on 2026-05-15 19:19), so they count as **one** data point, not four |
| **Module context** | `docs/specs/domain/activities/agent-context.md` |

---

## Intent

**Make the cluster Activities save fast and stop it from writing a new copy of every activity into every
future phase on each save. Then clean up the duplicates the bug has already left in the data.**

The slowness and the duplication share one root cause. A fix for the speed alone that leaves the
duplication in place would still let future phases keep getting corrupted.

---

## Problem / Current Behavior

Saving `/clusters/{crp}/activities` is slow, and it gets slower over time. Four defects stack up on the
same save path:

1. **The future-phase lookup never matches.** `ActivityMySQLDAO.java:126` and `:138` put `composed_id` into
   the HQL without quotes (`" where composed_id=" + composedID`). MySQL reads `composed_id=2077-23192` as a
   subtraction (`= -21115`), so no row ever matches. Verified: the unquoted form returns 0 rows and the
   quoted form returns 9 for project 102077 in phase 430 (`aiccradb2`). Because of this,
   `ActivityManagerImpl.saveActvityPhase` (`:274`) always takes the "create" branch and **inserts a new copy
   of the activity into every later phase on every save**. The copies that already exist are never updated.
2. **Every deliverable link is deleted and re-created on every save.**
   `ProjectActivitiesAction.bindDeliverablesForActivity` (`:717`) builds `DeliverableActivity` objects with a
   null id. `DeliverableActivity.equals()` compares by id, so in
   `ActivityManagerImpl.saveCurrentPhaseDeliverables` (`:315`) the `contains(...)` check always fails. Every
   active link is then deactivated (`:324`) and inserted again (`:335`).
3. **Replication is nested, so the cost grows with the square of the number of phases.**
   `saveActvityPhase` already walks every later phase. For each phase it calls
   `DeliverableActivityManager.saveDeliverableActivity` (`DeliverableActivityManagerImpl.java:140`) and
   `deleteDeliverableActivity` (`:56`), and each of those walks every later phase again.
   - The delete walk (`:81`) looks up rows by the **source phase's** `activity_id`, which never exists in
     another phase, so it runs queries that never find anything.
   - The save walk (`:176`) looks up rows the same way, so it always inserts.
   - Its phase-local activity lookup goes through the broken `getActivitiesByComposedID` (`:186`), so it
     falls back to the source phase's activity. That leaves links whose `id_phase` differs from their
     activity's phase (see the data table below).
4. **Wasted work around the writes.**
   - On POST, `prepare()` (`ProjectActivitiesAction.java:302`) loads everything a GET needs (deliverables
     with phase info, partner persons, `deliverablesMissingActivity`, the title catalog), then throws it
     away at `:541`.
   - `saveActivity` is `@Transactional` per activity, so each activity gets its own flush and its own
     audit listener pass.
   - `HibernateAuditLogListener.relations(...)` (`:586`) walks all of `Project.activities`, which is not
     filtered by phase (1,609 rows for project 102077).
   - The success result redirects to GET, which runs the whole `prepare()` again.

Measured in the local snapshot:

| Metric | Value | Query basis |
|---|---|---|
| `deliverable_activities` active / inactive | 222,129 / **1,240,637** | `group by is_active` |
| Rows for one (activity, deliverable, phase) triple | up to **89** | `group by activity_id, deliverable_id, id_phase` |
| Surplus active activity copies (same `project_id, composed_id, id_phase`) | **2,329** in 1,016 groups, 21 projects | `having count(*) > 1` |
| Active links whose `id_phase` ≠ their activity's `id_phase` | **16,490** | join on `activities` |
| Project 102077: active activities in phase 429 → 430 | **9 → 81** | `group by id_phase` |
| Composite index on `deliverable_activities(activity_id, deliverable_id, id_phase)` | **none** | `show index` |

End-to-end latency of a save has **not been measured**
(`UNVERIFIED — confirm at source before relying on it`; owner: `/akili-specify` baseline task).

---

## Proposed Outcome

- A save updates the copies that already exist in future phases instead of inserting new ones.
- A save does not touch deliverable links that did not change.
- Replication visits each future phase **once** per activity, with no second walk nested inside it.
- The POST skips the view-only loads it would discard anyway.
- The duplicates already in the data are resolved by a safe, idempotent migration, and the lookups get
  composite indexes.
- Forward replication keeps its current semantics (Hard rule 1): past phases are never written.

---

## Scope

| # | In scope |
|---|---|
| S1 | Bind `composed_id` as a query parameter in `getActivitiesByComposedID` and `getActivitiesByComposedIDPhaseIDProjectID` |
| S2 | Reconcile deliverable links by `deliverable.id`, not entity `equals()`: keep the unchanged ones, deactivate the removed ones, add the new ones |
| S3 | Flatten replication: inside the `ActivityManagerImpl` phase walk, write each phase's links through the DAO, not through the `DeliverableActivityManager` methods that replicate again |
| S4 | Fix `deletActivityPhase` (`:124`) so deleting an activity actually reaches its future copies. Today it depends on the same broken lookup |
| S5 | Skip the view-only loads in `prepare()` on POST |
| S6 | One transaction for the whole activities save, not one per activity |
| S7 | Flyway migration: composite indexes on `deliverable_activities` and `activities(project_id, composed_id, id_phase)` |
| S8 | Flyway migration: deactivate surplus duplicate activity copies and re-point or deactivate their links, including the 16,490 phase-mismatched links. Safe by default: no hardcoded ids, idempotent, DML only, FK order respected, tested twice on a throwaway copy |
| S9 | Regression tests: a lookup test proving a hyphenated `composed_id` matches; a save test proving a second save creates no new rows |

## Non-Goals

- Purging the 1.24 M inactive `deliverable_activities` rows. They are history the audit trail may
  reference; physical deletion would be a separate decision.
- Changing the Reporting → upkeep replication branch (`ActivityManagerImpl.java:255–263`). It is live even
  though its comment says "Uncomment", and this spec keeps that behavior exactly as it is.
- Fixing the `'Planing'` typo in `phases.description` for 2028–2030. It changes replication in every
  module, not just this one.
- `ProjectActivitiesValidator` running before `bindActivitiesFromRequest()`. It is a real bug on the same
  path, but a separate one; to be tracked as its own bugfix.
- `DeliverableAction` behavior beyond what S3 changes in the shared manager.

---

## Affected Users, Systems, And Specs

| Area | Impact |
|---|---|
| Cluster leaders editing Activities (AICCRA) | Faster saves; future phases stop filling with duplicates |
| Users of later phases (Progress 2026 onward) | Duplicated activities disappear once S8 runs |
| `DeliverableAction` (deliverable save) | Uses the same `DeliverableActivityManager` save/delete, so it inherits S3 |
| `ProjectInfoManagerImpl:256` (`copyActivity` on phase replication) | Reads `getDeliverableActivities()`; affected by S2/S8 data shape |
| `ActivitiesReplicationAction` (superadmin) | Calls `saveActivity`; inherits S1/S3/S6 |
| `CrpActivityAction` / `getActivityTitleRelations` | Groups by `composed_id`, which currently hides the duplicates; row counts change after S8 |
| Summaries (`ProjectActivitiesSummaryAction`, BI) | Counts per phase drop after S8 |
| Specs | `docs/specs/domain/activities/agent-context.md` (Forward Replication section to update); `reports/ai-context/persistence-replication-managerimpl.md` |

## Visual Reference

- Source: None
- Location: n/a
- Notes: backend and data only. No UI change.

---

## Bug Diagnosis

### Observed Symptom

Saving the cluster Activities section takes a long time and gets slower as the project accumulates saves.
The same saves also leave duplicate activities in every later phase (project 102077: 9 in phase 429,
81 active in phase 430).

### Reproduction Steps

1. As a cluster editor, open `/clusters/{crp}/activities` for a project in the editable Planning phase
   (AICCRA: AWPB 2026, id 429) that has activities linked to deliverables.
2. Save without changing anything.
3. Query `activities` for that project, grouped by `id_phase`. **Expected:** the count in every later phase
   stays the same. **Actual:** each later phase gains one new active row per activity.
4. Query `deliverable_activities` for one of those activities. **Expected:** unchanged. **Actual:** the
   existing links are set `is_active=0` and new rows are inserted.

The SQL half is shown deterministically by: `composed_id=2077-23192` → 0 rows; `composed_id='2077-23192'` →
9 rows (project 102077, phase 430). The UI half follows from the code path below. It was not run
end to end in this session (`UNVERIFIED — confirm at source before relying on it`; owner: the regression test
in S9). The data carries the same evidence: in every phase from 430 to 443, project 102077 has copies
inserted in the same batches on the same dates (2025‑10‑24, 2025‑11‑13, 2025‑11‑14, 2026‑05‑12).

### Root Cause (confirmed)

`ActivityMySQLDAO.java:126` and `:138` compare a `VARCHAR` column against an unquoted string that
contains `-`. MySQL parses it as an arithmetic expression, casts the column to a number, and nothing ever
matches. Every replication step that relies on those lookups falls back to inserting. On top of that,
the id-based `equals()` on freshly bound `DeliverableActivity` objects forces a delete-and-recreate of
every link, and the nested phase walk in `DeliverableActivityManagerImpl` squares the number of writes.

### Blast Radius

| Check | Recorded as | Result |
|---|---|---|
| **Already fixed?** | `git fetch --all`; `git log --all --since=2025-01-01 -- ActivityMySQLDAO.java ActivityManagerImpl.java DeliverableActivityManagerImpl.java` → last touches of `:126` / `:138` are `94d22ca949` (2025-05-15) and `622916c3d0` (2025-08-08), both introducing the unquoted form; `git grep "composed_id='\|composed_id=:"` over all refs → no hit; `git log --all --grep` for composed / activity slow / duplicate → nothing relevant. No Jira ticket given | **Not fixed** on any branch |
| **Live path?** | POST `{crp}/activities` (`struts-projects.xml:948`, `editProjectsStack`) → `ProjectActivitiesAction.save()` `:746` → `saveActivitiesNewData()` → `activityManager.saveActivity` `:872` → `ActivityManagerImpl.saveActivity` `:239` → branch `PLANNING` `:251` (phase 429 `description='Planning'`) → `saveActvityPhase` `:274` → `getActivitiesByComposedIDPhaseIDProjectID` `:277` → `ActivityMySQLDAO:138` | **On the live path.** Batch dates in phases 430–443 match save events |
| **Siblings on the same state** | `ActivityManagerImpl.deletActivityPhase` `:124/128`: same lookup, so a delete never reaches future copies. `DeliverableActivityManagerImpl.saveDeliverableActivityPhase` `:186`: same lookup, falls back to the wrong-phase activity (the 16,490 mismatched links). `DeliverableActivityManagerImpl.deleteDeliverableActivityPhase` `:81`: looks up by the source `activity_id`, never matches. No other DAO concatenates `composed_id` unquoted (`grep 'composed_id\s*=\s*"\s*+' marlo-data`) | All folded into S1–S4 |
| **Downstream consumers** | `DeliverableAction` (`deliverableActivityManager.save/deleteDeliverableActivity`); `ActivitiesReplicationAction` (`saveActivity`); `ProjectInfoManagerImpl:256` (`copyActivity`); `CrpActivityAction.groupRelations` (groups by `composed_id`); `ProjectActivitiesSummaryAction`; `BaseAction.canBeDeleted(..., ActivityTitle)`. Tests: only `CrpActivityActionTest` touches the domain, and it does not cover these managers | Listed in Affected Systems; S9 adds the missing coverage |

**Data integrity:** duplicate active activities are visible in future phases and inflate per-phase counts
and reports. Links pointing across phases can attach a deliverable to the wrong phase's activity. **No
security impact.**

### Fix Strategy

Not cosmetic: it changes logic and data, and it ships a data migration. Route: `/akili-specify` (Lite) in
**Bug Mode**, with a mandatory regression test that is red before the fix and green after (S9).

---

## Approach Options

| Option | What | Pros | Cons |
|---|---|---|---|
| **A. Lookup only** | S1 + S4 + S8 | Smallest diff; stops the duplication | The save stays slow: delete-and-recreate plus the quadratic walk remain, and the inactive rows keep growing |
| **B. Targeted fix (recommended)** | S1–S9 | Fixes the root cause and the main cost multipliers while keeping the replication semantics; every change is local to three classes and one action | Touches managers shared with `DeliverableAction`; the migration needs careful design |
| **C. Set-based rewrite** | Replace the per-phase recursion with bulk SQL per save | Fastest possible | Bypasses Hibernate and the audit listener (audit trail loss), a large behavior surface, and it conflicts with the ManagerImpl replication pattern in the TRD |

## Recommended Approach

**Option B.** It is the smallest change that removes both the corruption and the cost multipliers without
changing *what* is replicated. Split into ordered tasks: S1 + S9 lookup test first (the root cause), then
S2–S4, then S5–S6, then the index migration (S7), and last the cleanup migration (S8) behind its own
review. That keeps the risky data step separate and reversible in review.

---

## Risks, Dependencies, And Open Questions

| # | Item | Type |
|---|---|---|
| R1 | **Cleanup rule — decided 2026-09-29 (Kenji Tanaka): keep the newest copy** in each `(project_id, composed_id, id_phase)` group, since it reflects the latest source save. The other copies are deactivated, and their active links are re-pointed to the survivor or deactivated when the survivor already has the same deliverable. Accepted trade-off: an edit made directly on an older copy in a later phase is not carried over | Decision |
| R2 | The local snapshot is from 2026-05-15, so production counts will differ. Re-measure on a fresh copy before sizing the migration, and never hardcode ids (FSRP differs; see the migration-safety memory) | Risk |
| R3 | Changing `DeliverableActivityManagerImpl` also changes deliverable saves (`DeliverableAction`) | Risk |
| R4 | Adding indexes on a 1.4 M-row table locks it during DDL. Needs a maintenance-window note or an `ALGORITHM=INPLACE` check | Risk |
| R5 | Moving to one transaction changes audit grouping (one flush instead of N). Confirm the audit log still records the section save | Risk |
| R6 | Per the module Decision Log (2026-08-28), the catalog deletion rule is out of scope and not reopened | Constraint |
| R7 | Latency baseline not measured yet. Specify should add a baseline step (Hibernate statement count per save before/after) | Open question |
| R8 | **Blocking — S1 overwrites an open Reporting phase from a Planning save.** Measured 2026-10-06 on a local AICCRA copy (C102076, branch for A2-2614): with S1 applied, saving AWPB 2026 (phase 429) unchanged replicated forward and updated the 8 activities of AR 2026 (phase 431), setting `activityStatus` from Complete to On-going and `activityProgress` to NULL. Both phases are editable at the same time. The lookup has been broken since `79580a8218` (2025-08-08); before that, replication used a Java filter and did update later phases, so this is the original semantics, but it has not run for 14 months and the Reporting data entered since then would be lost. Decide which phases and fields a Planning save may write (for example, stop at the first editable Reporting phase, or never copy reporting-only fields) before S1 ships. S2 shipped separately in A2-2614 without S1 | Decision |

Kaizen: no Active Lesson targets this domain. `KZ-…directory-abstraction-1` (enumerations scoped to classes a
reviewer already named) applies to the sibling sweep. The Blast Radius enumeration above was done by grepping
the whole of `marlo-data`, not by starting from the named classes.

## Success Criteria

1. A hyphenated `composed_id` lookup returns the existing copy (regression test, red → green).
2. Saving an unchanged Activities section twice creates **zero** new `activities` rows and **zero** new
   `deliverable_activities` rows.
3. The number of SQL statements per save grows linearly with the number of phases, not quadratically,
   measured with Hibernate statistics before and after.
4. After S8, no `(project_id, composed_id, id_phase)` group has more than one active row, and no active link
   has an `id_phase` different from its activity's.
5. The migration is idempotent: a second run changes 0 rows. It is tested twice on a throwaway copy.
6. Compile and Checkstyle gates pass (`marlo-verify`).

## Next Step

```text
/akili-specify bugfix/activities-save-performance
```

Run in **Bug Mode** (Lite). R1 is settled: S8 keeps the newest copy.
