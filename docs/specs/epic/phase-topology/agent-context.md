# Phase Topology — Agent Context

**Spec:** EPIC-PHASE-TOPOLOGY
**Last Updated:** 2026-08-24
**Read this first.** Open `requirements.md` / `design.md` / `task.md` only for broad, architectural or formally
tracked work. This file is the operational summary of how phases and replication actually work today, with
evidence.

---

## 1. The data model in one table

`marlo-data/src/main/resources/xmls/Phases.hbm.xml` maps exactly eleven fields.

| Field | Real role |
|---|---|
| `name` | Cosmetic label, per tenant. `POWB` / `AR` / `UpKeep`; AICCRA renamed them to `AWPB` / `Progress`. |
| `description` | **Behaviour discriminator.** Only `Planning` or `Reporting` (`PhaseDescription`, `APConstants.PLANNING` / `REPORTING`). |
| `year` | Reporting year, not the calendar window. |
| `upkeep` | Second discriminator; separates POWB from UpKeep inside `Planning`. |
| `editable` | **The real write-permission switch.** See §4. |
| `visible` | Appears in the timeline or not. Does **not** affect replication. |
| `next_phase` | Self-FK, `ON DELETE RESTRICT`. The replication chain. |
| `start_date` / `end_date` | The only ordering key in the UI today. |
| `crp` | Global Unit (column `global_unit_id`). |

`Phase.hbm.xml` also declares **84 child collections**, and the migrations accumulate **160+ FK declarations
pointing at `phases`**, almost all `RESTRICT`.

## 2. The canonical chain

Verified against `V2_6_0_20180808_1542__UpdatePhases_UpKeep.sql`:

```
POWB(Y) -> UpKeep(Y) -> AR(Y) -> POWB(Y+1) -> UpKeep(Y+1) -> AR(Y+1) -> ...
Planning   Planning     Reporting
upk=0      upk=1        upk=0
```

`next.next` from a Reporting phase therefore does **not** mean "two forward". It means "skip the following POWB
and land on the UpKeep". That is the entire reason structural edits are dangerous today.

## 3. The five replication profiles

Grouped from the 119 `ManagerImpl` classes that walk the chain. Reference chain
`P21 -> U21 -> R21 -> P22 -> U22 -> R22 -> P23`.

| Profile | Managers | from POWB | from UpKeep | from AR | Reference |
|---|---|---|---|---|---|
| Canonical | 84 | U21,R21,P22,... | R21,P22,U22,... | **U22**,R22,P23,... (skips P22) | `ProjectInnovationOrganizationManagerImpl:155-175` |
| Deliverable / upkeep | 9 | none | R21,P22,U22,... | U22,R22,P23,... | `DeliverableCrpManagerImpl:118-141` |
| Funding source | 5 | U21,R21,P22,... | R21,P22,U22,... | none | `FundingSourceInfoManagerImpl:97-102` |
| Highlights | 5 | none | none | **P22**,U22,R22,... | `ProjectHighlightInfoManagerImpl:116-121` |
| Impact pathway | 16 | \* | \* | \* | `CrpProgramOutcomeManagerImpl:230`, called from `OutcomesAction:1081` |

\* No role rule. A separate `replicate(entity, initialPhase)` walks forward from `actualPhase.next`, invoked by
hand from the action layer. **Out of scope for v1** — parts of it do not work (§5).

The first four are the same shape with different edges missing, which is why an explicit edge graph covers them
with no conditional logic. `isPublication` (13 managers) is a **per-entity** predicate, not a phase-pair property,
so it stays in Java; factoring it out collapsed a sixth observed group into Canonical.

Members of the small groups, for grep:

- Funding source: the five `FundingSource*ManagerImpl` (Budget, Division, Info, Institution, Locations).
- Highlights: `ProjectHighlightInfo`, `ProjectHighligthCountry`, `ProjectHighligthType`, `ExternalSourceAuthor`,
  `ProjectLeverage`.
- Deliverable / upkeep: `DeliverableClusterParticipant`, `DeliverableCrp`, `DeliverableDissemination`,
  `DeliverableIntellectualAsset`, `DeliverableMetadataElement`, `DeliverableParticipant`,
  `DeliverablePublicationMetadata`, `DeliverableUser`, `DeliverableQualityCheck`.
- Impact pathway: `CrpAssumption`, `CrpClusterOfActivity`, `CrpOutcomeSubIdo`, `CrpPpaPartner`,
  `CrpProgramOutcome`, plus `ProjectLp6Contribution*`, `Deliverable*` metadata/altmetric and
  `ReportSynthesisFlagshipProgressOutcomeMilestone`.

## 4. `editable` is an authorization control, not a display toggle

`BaseAction.generatePermission()` (line 2163) injects the phase into the permission string:

```java
paramsRefactor[0] = paramsRefactor[0] + ":" + phase.getDescription() + ":" + phase.getYear();
// crp:{0}:admin  ->  crp:ccafs:Planning:2021:admin
```

The `getPermissions` stored procedure (`V2_6_0_20240827_1048__SPPermissions19.sql`) materialises permissions as a
cross product of permissions x phases, in 27 sub-selects, **all filtered by `ph.editable = 1`**:

```sql
REPLACE(p.permission, '{0}', CONCAT(cp.acronym, ':', ph.description, ':', ph.year))
... JOIN phases ph ON cp.id = ph.global_unit_id ... AND ph.editable = 1
```

Consequences:

- A phase with `editable = 0` grants **no permissions**. That, not a Java check, is what makes past phases
  immutable. Interceptors only reinforce it (`EditProjectInterceptor:304,324`).
- Opening a phase multiplies every user's permission temp table. Creating phases freely has a login cost.
- New phases are covered automatically; no permission rows need to be added.

## 5. Replication that is dead, commented out or incomplete

| Location | Finding |
|---|---|
| `CrpAssumptionManagerImpl.java:73-85` | Empty loop. Walks the chain, loads `outcomeSubIdo` into a local, does nothing with it. Comment: `// TODO i have NO CLUE how to replicate these. Pending.` **Assumptions never replicate.** Caller agrees (`OutcomesAction:1080`). |
| `CrpProgramOutcomeManagerImpl.java:230-253` | Replicates the outcome, but `addCrpSubIdos`, `updateMilestones` and `addCrpIndicators` are commented out (244-246). Partial. |
| `CrpProgramOutcomeManagerImpl.java:267` | `saveCrpProgramOutcome()` is a bare save; the replicating version is commented out at 255-264. |
| `ProjectInfoManagerImpl.java:334-342` | `Planning` branch live, `Reporting` branch commented out. |
| `DeliverableManagerImpl.java:715-770` | `saveDeliverablePhase()` is dead code — only caller is itself. Live path is `DeliverableListAction.addDeliverablePhase()` (line 150), i.e. replication in the action layer. |
| `replicateButton.ftl:3` | Feature behind `[#if reportingActive && false]`, referencing `actualPhase.next.next`. |

## 6. Replication logic duplicated outside `marlo-data`

15 occurrences of `next.next` across 11 `marlo-web` actions. Worst concentration:
`DeliverableMetadataByWOS.java` repeats the same ternary **five times** (lines 532, 572, 607, 650, 697):

```java
phase.getDescription().equals(APConstants.REPORTING) ? phase.getNext().getNext() : phase.getNext()
```

Others: `OutcomesAction`, `ProjectListAction`, `DeliverableListAction`, `FundingSourceAction`,
`FundingSourceListAction`, `ExpectedCRPProgress2019Action`, `OutcomesMilestonesAction`,
`ProjectBudgetByPartnersReplicationAction`, `ProjectPartnersReplication`, `CrpPhasesAction`.

## 7. The three rule divergences that need a product decision

1. **The Reporting skip is one-time, not periodic.** `AR(2021)` skips `POWB(2022)`, but the destination method
   recurses on `getNext()` **without re-checking `description`**, so it then writes `POWB(2023)`, `POWB(2024)` and
   onward. The real invariant is "reporting does not write the immediately following planning". Affects 84
   sections. → OQ-001.
2. **Highlights contradicts it.** `ProjectHighlightInfoManagerImpl:116-121` uses `next`, not `next.next`, so
   reporting highlights **do** write into the following POWB. → OQ-002.
3. **Does the impact pathway enter the graph?** Parts of it do not work (§5). → OQ-003.

## 8. Data-integrity findings

- **No UNIQUE** on `(global_unit_id, description, year, upkeep)`, yet `PhaseDAO.findCycle()` treats it as a
  natural key and returns `list.get(0)` without checking.
- **No cycle detection.** The self-FK allows `A.next = B, B.next = A`. Any save in that chain gives
  `StackOverflowError`; recursion only stops at `if (getNext() != null)`.
- **Phase id 377 has `year = 2029`** (`V2_6_0_20190212_1513__UpdateGenebankPhases.sql`), between neighbours at
  2019 and 2020. Almost certainly a typo for 2019, undetected by anything. → OQ-007.
- **Possible cross-tenant chain.** `V2_6_0_20181126_1102__GenebankPhases.sql` clones Global Unit 24's phases into
  Global Unit 26 with an `INSERT ... SELECT` that copies `next_phase` verbatim. The follow-up migration corrected
  years only. **Unverified against the database.** → OQ-006.
- **Two orders that can diverge:** `next_phase` (semantic, the only thing replication reads) and `startDate`
  (presentation, the sort key of every phase list). Nothing validates coherence, and the date inputs already exist
  in `crpPhases.ftl:71` behind `display:none`.
- **No audit on `phases`.** `Phase.isActive()` is hardcoded `true` (line 649), `getModifiedBy()` returns a
  fabricated `User` with id 3 (348-352), `getModificationJustification()` returns `""` (342). There is no
  `is_active` column and none should be added — see the requirements Decision Log.

## 9. Robustness findings in the phase code

- `PhaseMySQLDAO.getActivePhase()` (97-110): orders by **`MAX(id)`**, not `startDate`, so a phase created for an
  older year becomes "active" (used by the REST v2 `ProjectItem:89`). NPEs on an empty result —
  `map.get("id").toString()` over a NULL `MAX()`.
- `PhaseMySQLDAO.findAll()` returns **`null`** for an empty table; `BaseAction.getPhases()` streams it directly.
- `CrpPhasesAction` has **no `validate()` and no Validator** while writing `editable`. Violates CLAUDE.md rule 2.
- `CrpPhasesAction:106`: `customParameter.getValue().equals(defaultPhaseID)` compares `String` to `Long`, always
  false, so the guard never short-circuits.
- `CrpPhasesAction:100`: `System.out.println`.
- `CrpPhasesAction.save()` (118-121): clears `crp_phases`, `crp_phases_impact`, `crp_all_phases` and
  `crp_current_phase` **only from the acting admin's session**. Everyone else keeps the stale list until re-login.
- `crpPhases.ftl:58`: hardcoded `yearLimit = 2018` plus a POWB-2019 special case gating open/close.
- `BaseAction.getPhasesByCycles()` (4851-4866): the only phase list method that does not sort; two nested loops
  with `equalsIgnoreCase`.
- **Hardcoded year literals:** 20 conditionals in Java (`SendEmails:134,226`; `BaseAction:7327`; `isPhaseOne()`
  hardcoding `ccafs` and year <= 2016) and ~40 in FTL.

## 10. The migration hazard that has no compiler safety net

`PhaseMySQLDAO` queries `phases` **by column name, not by mapped property**: `findCycle()` uses `global_unit_id`
and `findPreviousPhase()` uses `next_phase`, while `Phases.hbm.xml` maps them as `crp` and `next`. The pattern is
repo-wide — 674 `findAll("from ...")` sites, and only one mapped property in the entire codebase has an underscore
in its name — and it works today.

The consequence for this epic is concrete: **dropping `next_phase` breaks `findPreviousPhase()` at runtime with no
compile error.** It has 6 live callers, and at `BaseAction.isEvidenceNew():7849` the call sits *outside* the
surrounding try/catch. Combined with 3 test files in the whole repository, this is why the parity comparator
(OPS-001 / T05) is the plan's only real safety net, and why dropping the column is its own deploy (T16).

## 11. What already exists that the module must not duplicate

- **Chain construction:** `GlobalUnitCreationManagerImpl.createPhases()` (521-545) already builds a chain by
  linking `next`. Share it, do not reimplement.
- **Bulk backfill:** `DeliverablesReplicationAction`, `FundingSourcesReplicationAction`,
  `ProjectsOutcomesReplicationAction`, `ActivitiesReplicationAction`, `ProjectPartnersReplication`,
  `ProjectBudgetByPartnersReplicationAction`, `marloBulkReplication.ftl`. Replication fires on save only, never
  retroactively, so a newly created phase starts **empty** until one of these runs.
- **Phase membership:** `CrpProjectPhases` / `projectPhases.ftl` populates `project_phases`.
- **Phase-id holders:** `custom_parameters` keys `current_phase` and `crp_aiccra_af_start_phase` store phase ids.
  Deleting a phase must repoint them.
- **Autosave draft keys:** `BaseAction:2494` builds them from `phase.getName()` + `phase.getYear()`. Renaming a
  phase orphans every draft.

## 12. Verification log

Append findings here as OQ items are answered against a real database.

| Date | Question | Environment | Finding |
|---|---|---|---|
| | OQ-006 cross-tenant `next_phase` | | |
| | OQ-007 phase 377 `year = 2029` | | |
| | DA-003 duplicate natural keys | | |
| | Global Units with no editable/visible phase | | |
