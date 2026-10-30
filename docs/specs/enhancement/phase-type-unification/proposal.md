# Phase Type Unification and Cycle Names — Proposal

## 1. Document Control

| Field | Value |
|---|---|
| Spec Path | `enhancement/phase-type-unification` — under `enhancement/` rather than the `changes/` default because the change is cross-cutting (phase model, `BaseAction`, validators, FTL, global unit creation) and `enhancement/` is the taxonomy folder that fits (root `CLAUDE.md`, *Spec taxonomy*) |
| Slug | `phase-type-unification` — first taken as `phase-cycle-names` from the leading token of the argument; renamed by the owner on 2026-10-28 to match the wider scope and the branch |
| Branch | `phase-type-unification`, created 2026-10-28 from `origin/staging` (`b1d3992dde`) and fast-forwarded to local `staging` (`575a1c7aff`, the reviewed template fix, not yet on `origin`). Worked in the main checkout; the temporary worktree was removed at the owner's request. Upstream unset on purpose, so a bare `git push` cannot target `staging` |
| Type | Change |
| Deliverable | software |
| Approval Mode | gated |
| Requirement Source | No Jira ticket. Findings of the 2026-10-28 working session: code sweeps plus the local DB copies `aiccradb_actsave`, `aiccradb1`–`aiccradb4` |
| Visual Reference | None (see §8) |
| Owner | IBD Team — Kenji Tanaka |
| Status | Approved — specified. Where this proposal's figures differ from `requirements.md` / `design.md` (site counts, `9 Java sites`, migration row counts, the M2 naming families), the spec documents win; Judgment Day round 1 corrected them (`judgment.md`) |
| Last Updated | 2026-10-28 |
| Depends on | none |
| Parallel-safe | no — touches `BaseAction.java`, ~70 FTL files and `phases` data, shared with `epic/phase-topology` (Draft) |
| Related specs | `docs/specs/epic/phase-topology/` (Draft). Its `agent-context.md` §1 calls `name` a "cosmetic label"; this change makes that true |

## 2. Intent

1. AICCRA_III and every global unit created after AICCRA show their three annual phases as **Planning**,
   **Progress** and **Reporting** instead of `POWB`, `UpKeep` and `AR`. A newly created global unit gets those
   names by default.
2. The code stops deciding a phase's type from text in scattered places. **One set of methods on `Phase`** answers
   "which type of phase is this?", and the overlapping `BaseAction` methods are unified into it.
3. **No current behavior changes**, except the three intended changes listed in §4, and no new exception is
   introduced anywhere.

## 3. Problem / Current Behavior

| # | Current behavior | Evidence |
|---|---|---|
| P1 | AICCRA_III (47) and later units name their phases `POWB` / `UpKeep` / `AR`; AICCRA (45) uses `AWPB` / `Progress` / `AR` | `SELECT name, description, upkeep, global_unit_id FROM phases` on `aiccradb_actsave`, `aiccradb1`, 2026-10-28 |
| P2 | The UI shows the raw `phases.name`: `Phase.getComposedName()` returns `name + " - " + year` | `marlo-data/.../model/Phase.java:210`; `timeline-phases.ftl:79,123`, `dashboard.ftl:205`, `titleGenerator.ftl:29` |
| P3 | A phase's type is decided three different ways: by **name** (`isPOWB()` / `isAWPBActive()`, literals `"UpKeep"`, `"Progress"`, `"AR"`), by **`description` text** (`isPlanningActive()`, `isReportingActive()`), by **`upkeep`** (`isProgressActive()`, `isUpKeepActive()`) | `BaseAction.java:6347, 7694, 7739, 7820, 8064, 8203` |
| P4 | `BaseAction` has 6 overlapping type methods for 4 real questions. `isPOWB()` and `isAWPBActive()` are identical. `isProgressActive()` and `isUpKeepActive()` are identical, except that `isUpKeepActive()` throws when `upkeep` is null. "Is this the plan phase?" is also asked as `isPlanningActive() && !isUpKeepActive()` in 10 places | §5 table U1 |
| P5 | Name, description and upkeep always agree: `POWB/AWPB = Planning/0`, `UpKeep/Progress = Planning/1`, `AR = Reporting/0`, in every global unit of all five local copies. A clean database is consistent too, because `V2_6_0_20180808_1542__UpdatePhases_UpKeep.sql` sets the names from `description` / `upkeep` | DB `GROUP BY name, description, upkeep`, 2026-10-28 |
| P6 | 6 AICCRA phases (ids 435, 436, 438, 439, 441, 442; 2028–2030) carry `description = 'Planing'`. Effects once AICCRA opens AWPB 2028 (start 2027-09-01): 97 `marlo-data` managers skip forward replication; `findCycle("Planning", …)` returns null, so project creation (`ProjectListAction:324`) and `CrpProjectPhases:96` fail; `isPlanningActive()` and `isReportingActive()` are both false | DB query; `grep -rlE 'getDescription\(\)\.equals(IgnoreCase)?\((APConstants\.PLANNING\|"Planning")' marlo-data/src/main/java \| wc -l` → 97 |
| P7 | Global unit creation from the admin screen copies `name`, `description`, `upkeep` from the template's phases; the template defaults to the super admin's current unit, so a unit created from AICCRA gets `AWPB / Progress / AR` | `GlobalUnitCreateAction.java:805-842, 866-880` |
| P8 | The shared-cluster trainees block locks a submitted cluster's numbers in every phase not *named* `Progress`. The exemption for the progress phase (`f8d39fe239`, 2024-07-19) was written for AICCRA. Because the name is hardcoded, AICCRA_III's UpKeep phase would lock (latent bug, never hit: no AICCRA_III UpKeep phase has been opened) | `deliverableMacros.ftl:367, 411`; git history; DB `visible = 0, editable = 0` |
| P9 | REST v2 identifies a phase by the **name** the client sends, in 16 `controllist/items` classes. **None of them is reached from MARLO itself**: no class outside `rest/` uses them, no JS or FTL calls `/api/`, and the owner stated on 2026-10-18 that `/api/**` is not in use | `grep -rlw <Item> marlo-web/src/main/java \| grep -v /rest/` → none; `grep -rn "api/"` over JS/FTL (third-party excluded) → none, 2026-10-28 |
| P10 | Autosave drafts are stored as `<id>_<Class>_<phase.name>_<year>_<CRP_ACRONYM>_<action>.json`; the JSON body uses `phaseID` | `json/autosave/AutoSaveWriterAction:231`, 148 action files, `BaseAction.getAutoSaveFilePath` |

## 4. Proposed Outcome

**No behavior changes, except these three intended ones:**

| # | Intended change | Why |
|---|---|---|
| C1 | AICCRA_III and later units **display** Planning / Progress / Reporting; new units are created with those names | The request |
| C2 | The shared-cluster trainees block applies the progress-phase exemption to **every** progress phase, not only phases named `Progress` | Bug fix, confirmed by the owner 2026-10-28 (P8) |
| C3 | Two error paths return `false` instead of throwing: `isUpKeepActive()` with a null `upkeep`, and `Phase.isReporting()` with a null `description` | "No new exception" contract; no successful path changes |

Everything else, including validations, replication, permissions, submissions, section statuses, summaries and
AICCRA's screens, produces exactly the same result as today. The REST v2 API (`rest/`) is not touched (§6).

## 5. Scope

### Part A — Data fix (first, independent)

| # | Item |
|---|---|
| A1 | **M1** `UPDATE phases SET description = 'Planning' WHERE description = 'Planing';` Prerequisite of Part B: the strict type definitions need it |

### Part B — Phase type unification (no behavior change)

**U1 — The four questions and their single answer:**

| Question | Today (all forms) | After |
|---|---|---|
| Plan phase? | `isPOWB()`, `isAWPBActive()`, `isPlanningActive() && !isUpKeepActive()` (9 Java sites), `isPlanningActive() && !getUpkeep()` (`CanEditPowbSynthesisInterceptor:112`), `isPlanningCycle && !isUpkeepCycle` (`projectContributionCrp.ftl:561`), name literals `"POWB"` / `POWB_ACRONYM` | `phase.isPlanningPhase()` |
| Progress phase? | `isProgressActive()`, `isUpKeepActive()`, `UpKeepActive` / `upKeepActive` in FTL, `isPlanningActive() && isUpKeepActive()` (`ValidateProjectSectionAction:406`), name literals `"UpKeep"` / `"Progress"` | `phase.isProgressPhase()` |
| Reporting phase? | `isReportingActive()`, `reportingActive` in FTL, name literal `"AR"` / `PROJECT_INDICATOR_PHASE_PREVIOUS_NAME` | `phase.isReportingPhase()` |
| Planning cycle (plan **or** progress)? | `isPlanningActive()` alone (25 sites), `planningActive` | `phase.isPlanningPhase() \|\| phase.isProgressPhase()` |

`getCurrentCycle()` stays: it produces the `"Planning"` / `"Reporting"` text stored as a key in `submissions`,
`section_statuses` and permission strings. It is computed from `isReportingPhase()`.

**Steps:**

| Step | What | No-change guard |
|---|---|---|
| B1 | **Characterization tests** pinning today's results of every old method over a phase table: plan / progress / reporting, with old names and AICCRA names, the empty phase, `upkeep = null`. The 2 tests that override old methods (`BaseActionGlobalUnitRulesTest:104`, `ProjectActivitiesValidatorCancelledTest:46`) are rewritten to set the phase instead of overriding a method | Green on the current code before anything changes |
| B2 | Add to `Phase`: `isPlanningPhase()`, `isProgressPhase()`, `isReportingPhase()`. The existing `isReporting()` delegates to `isReportingPhase()` | Equivalence test: old result equals new result for every row |
| B3 | Old `BaseAction` methods delegate to `getActualPhase().isXxxPhase()` and are marked `@Deprecated` | B1 stays green |
| B4 | Migrate every use, in batches, with the exact replacement table (§5, B4 table). Includes the name-literal sites: `ProjectOutcomeValidator:193`, `OutcomesMilestonesAction:427` (its `current.getName() != null` loop guard is kept), the 3 summaries (`FundingSources…:498, 697, 1004`, `ProjectsFundingSources…:611`, `ExpectedDeliverables…:354`), and the 6 `PROJECT_INDICATOR_PHASE_PREVIOUS_NAME` sites in `BaseAction`. Nothing under `rest/` | Per batch: clean compile, Checkstyle delta, tests with `-am`, token counts, **rendered-HTML diff** (§11) |
| B5 | Remove the old methods, the write-only fields `planningActive` / `reportingActive` and their public setters (`BaseAction:471, 487, 8924, 8928`, no callers), and `PROJECT_INDICATOR_PHASE_PREVIOUS_NAME` from both `APConstants.java` (amended at specify, 2026-10-28: `POWB_ACRONYM` stays because `ProgressReportProcessPOISummaryAction:1498` builds a report file name with it; see `design.md` P-9). Add a Checkstyle rule against reintroducing them, as `0a17b19671` did for console printing | Old-token count is 0; compile fails on any missed Java use |

**B4 replacement table (whole-word, closed list):**

| Old form | Count (Java / FTL) | New form |
|---|---|---|
| `isReportingActive()` | 76 / 1 | `getActualPhase().isReportingPhase()` |
| `reportingActive` | 4 / 205 | `actualPhase.reportingPhase` (keep any `!default` exactly) |
| `isUpKeepActive()` | 52 / 1 | `getActualPhase().isProgressPhase()` |
| `upKeepActive`, `UpKeepActive` | 0 / 7, 0 / 6 | `actualPhase.progressPhase` |
| `isProgressActive()` | 11 / 2 | `getActualPhase().isProgressPhase()` |
| `isPOWB()`, `isAWPBActive()` | 4 / 16, 3 / 8 | `getActualPhase().isPlanningPhase()` |
| `isPlanningActive() && !isUpKeepActive()` and the 2 other plan-phase combinations | 10 / 1 | `getActualPhase().isPlanningPhase()` |
| `isPlanningActive() && isUpKeepActive()` | 1 / 0 | `getActualPhase().isProgressPhase()` |
| `isPlanningActive()`, `planningActive` | 35 / 1, 4 / 1 | `(phase.isPlanningPhase() \|\| phase.isProgressPhase())` |

Counts are `grep -w` occurrences on 2026-10-28 and include the declarations. **Excluded from the replacement:**
the class `ProgressActiveValidationAction` and its `struts-json.xml:75` mapping; the FTL variable name
`reportingActiveMenu` (`main-menu.ftl:4`; only its right-hand side changes); the setters, which are removed by
hand in B5. The JS globals in `javascript-constants.ftl:30-31` keep their names; only their right-hand side changes.

**Kept on purpose:** `crpPhases.ftl:227` keeps `phase.name == "POWB"`. It is a 2019-only lock that deliberately
does not match AICCRA's `AWPB 2019` (phase 396), and a type check would lock that phase. M2 renames no 2019 phase.

### Part C — Cycle names

| # | Item |
|---|---|
| C-1 | **Global unit creation**: `GlobalUnitCreationManagerImpl.createPhases` names each phase from its type (Planning / Progress / Reporting) when the type is one of the three known types; any other template phase keeps today's copy. The text path (`parsePhaseLine`, not submitted by any FTL) derives `description` / `upkeep` only for those three names |
| C-2 | **Shared-cluster block** (`deliverableMacros.ftl:367, 411`): the exemption uses `actualPhase.progressPhase` (intended change C2) |
| C-4 | **M2**: change the stored `name` values (the column is not renamed) `POWB → Planning`, `UpKeep → Progress`, `AR → Reporting` for phases of units with `id > MIN(id)` of the units whose `acronym = 'AICCRA'`, excluding every unit that carries that acronym. A row is changed only when its `description` + `upkeep` match its name's type |
| C-5 | **Drafts check in the M2 deploy window**: list draft files of the renamed units whose phase token is `POWB` / `UpKeep` / `AR`. The owner expects none (2026-10-28). If any exist, rename the token, dry run first, matching the acronym exactly and longest first (`AICCRA` is a prefix of `AICCRA_III`) |

### Part D — Docs

Correct `epic/phase-topology/agent-context.md` §1. Update `reports/ai-context/` where a contract doc names the old
methods. PRD (`docs/prd.md:157, 214`) and TRD (`docs/trd/trd.md:170, 570`) describe the cycle as POWB / UpKeep / AR:
recorded as a pending item for `staging` through the constitutional process, not edited by this spec.

## 6. Non-Goals

- Changing AICCRA (45) phase names.
- Renaming the `phases.name` column.
- Replication, `next_phase`, or any `ManagerImpl` rule (`epic/phase-topology`). The ~525 `getDescription()` reads in
  managers, `findCycle`, `getCrpPhase` and `getPhasesByCycles` keep reading `description` in this spec.
- **The REST v2 API (`rest/`)**. It is not reached from MARLO (P9) and is not in use (owner, 2026-10-18), so it is
  left unchanged, name comparisons and `"AR"` literals included. Consequence, accepted: after M2, an external call
  that sends `POWB` / `UpKeep` / `AR` for a renamed unit no longer finds the phase, and 4 sites
  (`InnovationItem:564`, `PolicyItem:550`, `ActionTakenItem:171`, `ExpendituresItem:171`) would throw
  `NoSuchElementException`. If `/api/**` comes back into use, a name resolver is the follow-up (R5).
- **A `reporting` column — to evaluate at the end of the process.** A `reporting` flag beside `upkeep` (owner's
  choice, 2026-10-28): `0/0` plan, `0/1` progress, `1/0` reporting, backfilled from `description = 'Reporting'`;
  `1/1` must be rejected (DB `CHECK` if production MySQL ≥ 8.0.16, otherwise in code). After Part B only the three
  `Phase` methods change. The evaluation must cover the uses that store the cycle text as a key: ~525
  `getDescription()` reads, `findCycle`, `submissions.cycle`, `section_statuses.cycle`, permission keys
  (`BaseAction.generatePermission:2008` and the DB procedure `getPermissions`), the BI views `vw_bi_feedback` /
  `tmp_all_data`.
- Cleanups found during the investigation, which are not bugs and are kept as they are: the `|| isUpKeepActive()`
  branches of budget execution (no-ops: in progress phases no execution has the phase's year; 0 rows locally), and
  the redundant `isPlanningActive()` at `CanEditPowbSynthesisInterceptor:165` (always true inside the line-112
  block, which spans lines 112–220).

## 7. Affected Users, Systems, And Specs

| Affected | How |
|---|---|
| AICCRA_III and later units' users | New phase names; the shared-cluster fix (C2); nothing else |
| AICCRA users | Nothing |
| Super admins creating units | New units get the new names |
| REST v2 API | Not changed; not in use (P9, §6) |
| QA process filling `qa_innovations_data` (outside the repo) | Its view filters `phase_name = 'AR'`; after M2, AICCRA_III reporting rows arrive as `Reporting` (R4) |
| Developers | 6 old `BaseAction` methods are replaced by 3 `Phase` methods; a Checkstyle rule blocks the old names |
| `epic/phase-topology` | Same files; whichever lands second rebases |

## 8. Visual Reference

- Source: None
- Location: —
- Notes: only the text of existing phase labels changes. No new screen, component or layout. Mockup declined by
  the owner (2026-10-28).

## 9. Requirement Delta Preview

### ADDED Requirements

- A phase's type is answered only by `Phase.isPlanningPhase()`, `isProgressPhase()`, `isReportingPhase()`.
- New global units name their phases Planning / Progress / Reporting by type.

### MODIFIED Requirements

- AICCRA_III and later units display Planning / Progress / Reporting.
- The shared-cluster trainees block exempts every progress phase.
- AICCRA phases 2028–2030 are recognized as planning phases (M1).

### REMOVED Requirements

- Logic that depends on `phases.name`.
- `BaseAction.isPOWB()`, `isAWPBActive()`, `isPlanningActive()`, `isProgressActive()`, `isUpKeepActive()`,
  `isReportingActive()` and the write-only `planningActive` / `reportingActive` fields and setters.

## 10. Approach Options

| Option | What | Verdict |
|---|---|---|
| **A. Unify by type, then change the stored names** | Parts A–D | **Recommended.** Removes the cause (logic reading text in many places); new units handled at creation |
| B. Display-only label | Keep `name`; derive the displayed label from the type | Avoids M2 and C-5, but `name` and the label diverge and the request's "update the names" is not met. Still available if M2 is dropped late |
| C. Change names + patch literals (2021 AICCRA precedent) | M2 plus more literals in each comparison | Grows the literal lists; the next rename repeats the work |

## 11. Recommended Approach

Option A, delivered in this order. Each step is releasable and leaves behavior unchanged.

1. **M1.**
2. **B1 → B3**: tests first, then the `Phase` methods, then delegation.
3. **B4** in batches, then **B5**.
4. **C-1, C-2.**
5. **M2 + C-5**, after Part B, **never before it**. On a branch without Part B, `isPOWB()` still reads the name and
   would stop recognizing AICCRA_III's plan phase, so planning would be validated as reporting.

### Migration safety (dry run on a throwaway schema, 2026-10-28, `global_units` + `phases` with both FKs re-added)

> **Superseded (RJ-8):** the counts below were taken before unit 50 existed and before M2 covered the `AWPB` family. The current scenario table, with counts computed before each run, is in `requirements.md` §6 (AC for DA-001, MIG-001, MIG-002).

| # | Scenario | M1 rows | M2 rows | Result |
|---|---|---|---|---|
| 1 | Real copy, first run | 6 | 54 | 47, 48, 49 renamed; 45 keeps `AWPB / Progress / AR`; no `Planing` left |
| 2 | Same copy, second run | 0 | 0 | Idempotent |
| 3 | Empty database: no global unit, no phase | 0 | 0 | No failure |
| 4 | Global units but no phases | 0 | 0 | No failure |
| 5 | No AICCRA row | 0 | 0 | Nothing renamed: the cutoff cannot be evaluated |
| 6 | A later unit also carries the acronym `AICCRA` (`acronym` has no unique index) | 6 | 36 | That unit is treated as AICCRA and left untouched |
| 7 | Orphan phases (`global_unit_id = NULL`) | 1 | 0 | Typo fixed; orphans never renamed |
| 8 | Inconsistent row (`UpKeep` with `upkeep = 0`) | 6 | 53 | That row keeps its name |

Both migrations are DML-only (covered by Flyway's transaction), use no hardcoded id, touch no FK column, and read only
tables created by much older migrations (`V2_0_0_20170411_0900__PhasesProject.sql`,
`V2_5_0_20171027_1505__GlobalUnits.sql`). Collations are case-insensitive, so `aiccra` / `powb` also match.

**New global units are not a migration concern.** M2 runs once over existing rows. A unit created later gets its
names from C-1, in every environment, including one without AICCRA.

### Code safety contract (owner requirement, 2026-10-28: no logic change, no new exception)

| Rule | How it is met |
|---|---|
| Same result for every real phase | Strict definitions: `isReportingPhase` = `"Reporting".equals(description)`; `isPlanningPhase` = `"Planning".equals(description) && Boolean.FALSE.equals(upkeep)`; `isProgressPhase` = `Boolean.TRUE.equals(upkeep)`; cycle = `isPlanningPhase() \|\| isProgressPhase()`. Compared row by row with the old methods on all five local copies: 0 differences after M1 (the 6 `Planing` rows are the only ones before it) |
| Same result for the empty phase | `getActualPhase()` returns `new Phase(null, "", -1)` without a session or unit (`BaseAction:2054, 2065, 2134, 2156, 2163, 2998`). Every new method returns `false` on it, as the old ones do. Defining the cycle as `!isReportingPhase()` was rejected because it returns `true` there |
| No new exception | Constant-first `equals`, `Boolean.TRUE/FALSE.equals`, no unboxing, no `final` methods (Hibernate proxies), primitive `boolean` return (FreeMarker resolves `actualPhase.reportingPhase` only for a primitive `boolean`). `actualPhase` exists in every template: `BaseAction` is the only class extending `ActionSupport` |
| Surrounding guards kept | Each migrated site keeps its null checks and loop guards (e.g. `OutcomesMilestonesAction:426`'s `current.getName() != null`) |
| FTL verified by output, not by reading | Rendered-HTML diff per B4 batch: with the local app and DB copy, capture project list and detail, deliverable, study, budget, menu and guide pages in a plan, a progress and a reporting phase, plus a page without a unit. Output must be identical except cache-bust params. This catches what FTL hides: a misspelled property under `!false` renders `false` silently, and `guide-button.ftl:153, 228` read `UpKeepActive?exists`, which hides a block if unresolved |
| Java verified by the compiler | B5 deletes the old methods, so any missed Java use fails to compile |

## 12. Risks, Dependencies, And Open Questions

| ID | Risk / question | Mitigation |
|---|---|---|
| R1 | **Incomplete enumeration.** The REST list grew from 3 to 16 classes between sweeps (`KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-1`: an enumeration scoped to already-named sites missed siblings twice) | `/akili-specify` reruns every sweep and records each command as run: `getName()` on any `Phase`, FTL `.name`, every token of the B4 table, every `getUpkeep()` / `getDescription()` comparison outside `marlo-data` managers |
| R2 | **M2 ahead of Part B** on another branch or repository | M2 is committed after Part B, and its header states the dependency |
| R3 | **FTL silent failures** in B4 (205 `reportingActive` uses in ~71 files) | Closed whole-word replacement table; per-batch token counts; rendered-HTML diff |
| R4 | **QA view `qa_innovations`** filters `phase_name = 'AR'` and is maintained outside the repo | Coordinate with the QA team before M2: filter by type, or accept both names |
| R5 | **REST v2 left unchanged.** If `/api/**` is reactivated, calls with old names for renamed units fail, 4 of them with HTTP 500 | Reactivating `/api/**` must include a phase-name resolver (exact name first, legacy name by type second, never throwing) |
| R6 | **"After AICCRA" = `id > MIN(AICCRA id)`.** Without AICCRA, M2 renames nothing | Documented; AICCRA_III was created after AICCRA in every known environment |
| R7 | **Production data** may differ from the local copies | Read-only checks before release: count of `description = 'Planing'`; rows whose name does not match their type; `SELECT COUNT(*) FROM project_budget_executions e JOIN phases p ON p.id = e.phase_id WHERE p.upkeep = 1 AND e.year = p.year` must be 0; production MySQL version |
| R8 | **Overlap with `epic/phase-topology`** T09/T10 | Whichever lands second rebases |
| R9 | **Constitutional docs** still describe POWB / UpKeep / AR | Pending item for `staging` |
| R10 | **Size of B4.** ~420 uses; the rendered-HTML diff needs the local app with data in the three phase types | Batches by module; local DB copy and local login already exist |
| OQ1 | Should `crpPhases.ftl` (CRP Admin) keep showing the stored name? After M2 it shows the new one | Default: shows the stored name |
| OQ2 | Does any other external consumer read `phases.name` (Power BI datasets, CLARISA sync)? | Ask the BI owner during specify |

## 13. Success Criteria

- AICCRA_III shows Planning / Progress / Reporting in the selector, dashboard, title and read-only messages; AICCRA
  shows exactly what it shows today.
- The characterization tests from B1 pass unchanged after B3, B4 and B5.
- For every B4 batch, the rendered HTML of the captured pages is identical before and after.
- After B5, `grep -w` finds none of the old tokens, the Checkstyle rule is in place, and no logic compares a phase
  name.
- A unit created with AICCRA (45) as the template gets Planning / Progress / Reporting and no `Planing`.
- Both migrations reproduce the §11 safety table on a throwaway schema before release.
- Compile and Checkstyle gates pass (`marlo-verify`); both migrations pass the `marlo-migration` checklist.

## 14. Next Step

```text
/akili-specify enhancement/phase-type-unification
```

Standard depth. Part A (M1) can ship on its own first.
