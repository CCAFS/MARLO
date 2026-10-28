# Phase Type Unification and Cycle Names — Requirements

**Spec ID:** ENH-PHASE-TYPE-UNIFICATION-001
**Status:** Draft
**Owner:** IBD Team — Kenji Tanaka
**Reviewers:** IBD team lead, PMU lead, QA lead, Tech lead
**Last Updated:** 2026-10-28
**Type:** Change · **Deliverable:** software · **Depth:** Full (cross-cutting, data migration, 421 sites in 117 files per `call-sites.md` §A1)
**Approval Mode:** gated
**Source:** `proposal.md` in this folder (committed `9e2a9ed838`)
**Related PRD sections:** docs/prd.md §3 (US-ADM-01 configure phases), §7 (phase safety)
**Related UX/UI Design sections:** docs/ux-ui/design.md — phase timeline switcher (label text only)
**Related TRD sections:** docs/trd/trd.md §3.3 (phase-aware data model), §5.2 (replication rules, unchanged)
**Companion ai-context docs:** reports/ai-context/persistence-replication-managerimpl.md (read-only context),
reports/ai-context/save-validation-matrix.md (validators that call the phase methods)

---

## 1. Overview

MARLO decides "what type of phase is this?" in many places and in three different ways: by the phase's display
name, by the text of its `description`, and by its `upkeep` flag. Because of that, renaming a phase is unsafe
today. This spec has two goals:

1. Make the phase type answerable in exactly one place, so that `phases.name` becomes a pure label.
2. Rename AICCRA_III's phases, and those of every later global unit, to **Planning / Progress / Reporting**.

The overriding constraint, set by the owner on 2026-10-28, is that **no current behavior changes and no new
exception is introduced**. The only exceptions are the four intended changes listed in §3 (INT-1..INT-4).

### Glossary

| Term | Meaning |
|---|---|
| Plan phase | The first phase of a year. Named `POWB` (most units) or `AWPB` (AICCRA); `description = 'Planning'`, `upkeep = 0` |
| Progress phase | The mid-year phase. Named `UpKeep` (most units) or `Progress` (AICCRA); `description = 'Planning'`, `upkeep = 1` |
| Reporting phase | The year-end phase. Named `AR`; `description = 'Reporting'`, `upkeep = 0` |
| Planning cycle | Plan phase **or** progress phase. It is what `description = 'Planning'` means today |
| Empty phase | The placeholder `new Phase(null, "", -1)` that `BaseAction.getActualPhase()` returns when there is no session or no global unit |
| Old type methods | `BaseAction.isPOWB()`, `isAWPBActive()`, `isPlanningActive()`, `isProgressActive()`, `isUpKeepActive()`, `isReportingActive()` |
| Units after AICCRA | Global units whose id is greater than the lowest id carrying the acronym `AICCRA`, excluding every unit that carries that acronym |

## 2. Problem Statement

Each claim below cites its evidence as run, at commit `9e2a9ed838`.

1. **The UI prints the raw name.** `Phase.getComposedName()` returns `name + " - " + year`
   (`marlo-data/.../model/Phase.java:210`). It is used by the phase selector (`timeline-phases.ftl:79,123`), the
   dashboard (`dashboard.ftl:205`) and the page title (`titleGenerator.ftl:29`). So the request (show Planning /
   Progress / Reporting) means changing the stored names, or deriving a label.
2. **Logic reads the name.** `isPOWB()` and `isAWPBActive()` return true only for the names `POWB` / `AWPB`
   (`BaseAction.java:6347, 7739`). Validators skip participant and expected-study requirements with
   `!isPOWB()` (`DeliverableValidator:494, 806`, `ProjectExpectedStudiesValidator:755`). Renaming `POWB` alone
   would therefore validate planning as if it were reporting.
3. **The same question is asked several ways.** The plan phase is also asked as
   `isPlanningActive() && !isUpKeepActive()` (7 Java sites: `grep -rnE "isPlanningActive\(\) *&& *!.*is(UpKeep|Progress)Active\(\)" marlo-web/src/main/java`), `isPlanningActive() && !getUpkeep()`
   (`CanEditPowbSynthesisInterceptor:112`) and `isPlanningCycle && !isUpkeepCycle`
   (`projectContributionCrp.ftl:561`). `isProgressActive()` and `isUpKeepActive()` duplicate each other;
   `isUpKeepActive()` throws on a null `upkeep` (`BaseAction.java:8203`).
4. **A data typo breaks the type check.** `description = 'Planing'` sits on AICCRA's 2028–2030 plan and progress
   phases (ids 435, 436, 438, 439, 441, 442) in all five local copies. **It spreads**: unit 50 (QATEST), created on
   2026-10-28 from the AICCRA template, copied it, so `aiccradb_actsave` holds 12 such rows and `aiccradb1`–`4`
   hold 6 (`SELECT COUNT(*) FROM phases WHERE description = 'Planing'`). Production may hold more. Once
   AWPB 2028 opens (start 2027-09-01), the planning-gated replication in 97 managers is skipped, and
   `findCycle("Planning", …)` returns null. That breaks project creation (`ProjectListAction:324`).
5. **New units inherit whatever names their template has.** `GlobalUnitCreateAction.java:805-842` copies the
   template's phase `name`, `description` and `upkeep`. A unit created from AICCRA gets `AWPB / Progress / AR`
   and the typo: unit 50 in `aiccradb_actsave` is that case.
6. **A hardcoded name causes a latent bug.** The shared-cluster trainees lock exempts only phases *named*
   `Progress` (`deliverableMacros.ftl:367, 411`). That exemption was written in `f8d39fe239` (2024-07-19) for
   AICCRA's progress phase, because `isSubmit()` matches the shared `Planning` cycle and would otherwise lock a
   cluster submitted in the plan phase. AICCRA_III's progress phase is named `UpKeep`, so it would lock. No
   AICCRA_III UpKeep phase has been opened yet (`visible = 0`, `editable = 0` locally).

PRD alignment: PRD §7 *phase safety* and the US-ADM-01 phase configuration story.

## 3. In-Scope Requirements

### Intended behavior changes (the only ones allowed)

- **INT-1** — AICCRA_III and every later unit display Planning / Progress / Reporting, and new units are created
  with those names (FN-006, FN-007, DA-002).
- **INT-2** — The shared-cluster trainees exemption applies to every progress phase (FN-008).
- **INT-3** — Expressions that throw today on a missing phase or a null phase field stop throwing. They return
  the value of the new expression, usually `false`; `!progressPhase` at `deliverableMacros.ftl` returns `true`
  (FN-004, RJ-6). Known sites:
  - `isUpKeepActive()` with a null actual phase or a null `upkeep` (`BaseAction:8203`);
  - `Phase.isReporting()` with a null `description`;
  - `CanEditPowbSynthesisInterceptor:112`, which unboxes `getActualPhase().getUpkeep()`;
  - `deliverableMacros.ftl:367, 411` (`actualPhase.name != 'Progress'`) on a null actual phase;
  - summary sites that call `getSelectedPhase().getName().equals(…)` on a null name (`FundingSourcesSummaryAction:498, 697, 1004`, `ProjectsFundingSourcesSummaryAction:611`).

  A site with a null guard today keeps answering `false`, never `true`, when the guard fails (NF-005).
- **INT-4** — DA-001 repairs data: the `Planing` phases become recognized as plan or progress phases. On the
  local copies they are all closed (`editable = 0`, `visible = 0`). Production state is checked by OPS-003.

### Functional

- ENH-PHASE-TYPE-UNIFICATION-001-FN-001 — The phase type MUST be answerable for any phase object through exactly
  three questions: is it the plan phase, the progress phase, or the reporting phase. The planning cycle MUST be
  expressed as "plan phase or progress phase", not as a fourth question.
- ENH-PHASE-TYPE-UNIFICATION-001-FN-002 — For every phase whose `description` is `Planning` or `Reporting` and
  whose `upkeep` is not null (`phases.upkeep` is `NOT NULL`), the three answers MUST equal what the old type
  methods answer today:
  - plan phase = today's `isPOWB()`;
  - progress phase = today's `isProgressActive()`;
  - reporting phase = today's `isReportingActive()`;
  - planning cycle = today's `isPlanningActive()`.
- ENH-PHASE-TYPE-UNIFICATION-001-FN-003 — For the empty phase, all three answers and the planning cycle MUST be
  `false`, as every old type method answers today.
- ENH-PHASE-TYPE-UNIFICATION-001-FN-004 — Asking the type of any phase object, including one with a null
  `description`, null `upkeep` or null `name`, MUST NOT throw.
- ENH-PHASE-TYPE-UNIFICATION-001-FN-005 — After the migration, no code in `marlo-web/src/main` outside `rest/`
  MAY decide a phase's type by comparing its `name` with a literal or a constant. Exception: the 2019 lock in
  `crpPhases.ftl:227` stays as it is (§9, 2026-10-28). The old type methods and the write-only
  `planningActive` / `reportingActive` fields and setters MUST no longer exist.
- ENH-PHASE-TYPE-UNIFICATION-001-FN-006 — The plan, progress and reporting phases of every unit after AICCRA
  MUST be named `Planning`, `Progress` and `Reporting`. AICCRA's phases and those of every earlier unit MUST
  keep their current names.
- ENH-PHASE-TYPE-UNIFICATION-001-FN-007 — A global unit created from the admin screen MUST name each phase from
  its type: Planning, Progress or Reporting. This holds whatever template it copies, AICCRA included. A
  template phase whose type is none of the three MUST be copied as it is today.
- ENH-PHASE-TYPE-UNIFICATION-001-FN-008 — The shared-cluster trainees block (note, submitted-cluster lock,
  "Cluster submitted." label, save warning) MUST apply its progress-phase exemption to every progress phase.
- ENH-PHASE-TYPE-UNIFICATION-001-FN-009 — `getCurrentCycle()` MUST keep returning exactly `"Reporting"` for a
  reporting phase and `"Planning"` otherwise, including for the empty phase. It feeds `submissions.cycle`,
  `section_statuses.cycle` and `findCycle`.

### Data / Migration

- ENH-PHASE-TYPE-UNIFICATION-001-DA-001 — Every phase whose `description` is `Planing` MUST be corrected to
  `Planning`.
- ENH-PHASE-TYPE-UNIFICATION-001-DA-002 — The stored `name` of a plan, progress or reporting phase of a unit
  after AICCRA MUST be changed to `Planning` / `Progress` / `Reporting`, from either naming family such a unit can
  hold: `POWB` / `UpKeep` / `AR`, or `AWPB` / `Progress` / `AR` when it was created from the AICCRA template. The
  `name` column MUST NOT be renamed. A row MUST be changed only when its `description` and `upkeep` match the
  type its current name stands for.
- ENH-PHASE-TYPE-UNIFICATION-001-MIG-001 — Both migrations MUST hold in every scenario of §6 AC for MIG-001:
  - a database with no global units or no phases;
  - a database without AICCRA;
  - a later unit that also carries the acronym `AICCRA`;
  - orphan phases;
  - inconsistent rows;
  - a second run.
- ENH-PHASE-TYPE-UNIFICATION-001-MIG-002 — The migrations MUST NOT use a hardcoded id, MUST be DML-only, and
  MUST touch no foreign-key column.

### Non-Functional

- ENH-PHASE-TYPE-UNIFICATION-001-NF-001 — Apart from INT-1..INT-4, the result of every migrated call site MUST
  be identical to today for every phase. This covers validation, permissions, replication triggers,
  submissions, section statuses, summaries and every rendered page.
- ENH-PHASE-TYPE-UNIFICATION-001-NF-002 — The rendered HTML of the reference page set (§6 AC for NF-002) MUST be
  byte-identical before and after each migration batch, except for cache-bust parameters.
- ENH-PHASE-TYPE-UNIFICATION-001-NF-003 — Reintroducing a removed old type method in Java MUST fail the
  Checkstyle gate.
- ENH-PHASE-TYPE-UNIFICATION-001-NF-004 — The compile and Checkstyle gates MUST pass at every task boundary
  (`marlo-verify`).
- ENH-PHASE-TYPE-UNIFICATION-001-NF-005 — Every migrated site MUST give the same result as today on five inputs:
  - a plan phase;
  - a progress phase;
  - a reporting phase;
  - the empty phase;
  - a null phase (where the site can receive one).

  The only exception is a throw replaced by the value of the new expression (INT-3). Two consequences:
  - A site whose old expression is an inline guarded or negated test on a raw phase field (`x != null && name
    != null && …`) MUST keep returning `false` when the guard fails. Such a test becomes the positive complement
    among the three types (JD-1).
  - A negation of an old null-safe `BaseAction` method (`!isPOWB()`, `!isReportingActive()`, …) MUST stay a
    bare negation of its replacement, because both return `false` on the empty and the null phase (RJ-1).
    The exception is `isUpKeepActive()`, which throws there today and falls under INT-3 (FJ-2).

### Operations

- ENH-PHASE-TYPE-UNIFICATION-001-OPS-001 — The name change (DA-002) MUST NOT reach a database before the code
  that answers the phase type without reading the name (FN-001..FN-005).
- ENH-PHASE-TYPE-UNIFICATION-001-OPS-002 — In the release window of DA-002, autosave drafts of the renamed units
  whose file name carries `POWB`, `AWPB`, `UpKeep` or `AR` MUST be listed. Any found MUST be renamed to the new
  phase name before users resume.
- ENH-PHASE-TYPE-UNIFICATION-001-OPS-003 — Before the first code task ships, a read-only production check MUST
  confirm that every phase's `description` + `upkeep` is one of `Planning/0`, `Planning/1`, `Reporting/0` or
  `Planing/*`. Any other combination stops the rollout until the strict definitions are re-evaluated (JD-7).
- ENH-PHASE-TYPE-UNIFICATION-001-OPS-004 — Each data migration's affected rows MUST be captured, with their prior
  values, before it runs, so that it can be reversed row by row (FJ-1). A by-type reversal of DA-002 is impossible
  because two naming families map to the same new name.

## 4. Out-of-Scope

- The REST v2 API under `rest/`. MARLO does not call it: no class outside `rest/` uses its 16 `controllist`
  items, and no JS or FTL calls `/api/`. The owner stated on 2026-10-18 that `/api/**` is not in use. It keeps
  its name comparisons.
- A `reporting` column on `phases`. It is to be evaluated at the end of the process (proposal §6).
- The ~525 `getDescription()` reads, mostly replication managers, plus `findCycle`, `getCrpPhase` and
  `getPhasesByCycles`. They keep reading `description`.
- Renaming AICCRA's phases. Renaming the `phases.name` column.
- The QA view `qa_innovations` (`phase_name = 'AR'`), which lives outside the repo. It is coordinated with the
  QA team, not changed here.
- Two cleanups found and confirmed harmless, kept as they are:
  - the `|| isUpKeepActive()` budget-execution branches, which are no-ops because no progress phase holds an
    execution of its own year (0 rows locally);
  - the redundant `isPlanningActive()` at `CanEditPowbSynthesisInterceptor:165`, which is always true inside
    the line-112 block (lines 112–220).
- The PRD and TRD wording (POWB / UpKeep / AR). It is a pending item for `staging` through the constitutional
  process.
- `crpPhases.ftl:59-63` (`cycTone`) picks the CRP Admin row tone by `description?contains("progress" / "report")`.
  It is a display classifier, unaffected by DA-002 and kept as it is (JD-11).

## 5. Personas Affected

| Persona | Effect |
|---|---|
| Cluster Coordinator, AICCRA_III and later units | New phase names (INT-1); progress-phase shared-cluster fix (INT-2); nothing else |
| Cluster Coordinator, AICCRA and earlier units | None |
| PMU / Program Admin | New names in CRP Admin phases for renamed units |
| Super admin | New units are created with Planning / Progress / Reporting |
| QA Reviewer | `qa_innovations` coordination before DA-002 |
| Developers | One set of phase type methods; old names blocked by Checkstyle |

## 6. Acceptance Criteria

**AC for FN-001, FN-002:**
- Given a table of phases covering each type:
  - plan phase as `POWB/Planning/0` and `AWPB/Planning/0`;
  - progress phase as `UpKeep/Planning/1` and `Progress/Planning/1`;
  - reporting phase as `AR/Reporting/0`;
- When each old type method and its replacement are evaluated on the same phase,
- Then the two results MUST be equal for every row,
- And the planning cycle MUST be true for both the plan and the progress phase,
- BUT the plan-phase answer MUST NOT be true for a progress phase,
- AND IT MUST be shown on the real data: a row-by-row comparison of every phase of the five local DB copies
  reports 0 differences after DA-001.

**AC for NF-005 (site truth tables):**
- Given every site in `call-sites.md` §B and every Java site in §A1 whose old expression carries its own null
  guard or negation,
- When the old and new expressions are evaluated on a plan, a progress, a reporting, the empty and a null phase,
- Then the results MUST be equal on all five, except a throw becoming `false`,
- BUT `ProjectOutcomeValidator:191-194` and `OutcomesMilestonesAction:426-428` MUST NOT become true on the empty
  or a null phase.

**AC for FN-003, FN-004, INT-3:**
- Given the empty phase, a phase with `upkeep = null`, and a phase with `description = null`,
- When the plan, progress and reporting answers and the planning cycle are evaluated,
- Then each MUST return `false`,
- And no exception MUST be thrown,
- BUT a page rendered with no session or no global unit MUST NOT change its output (NF-002 includes such a page).

**AC for FN-005, NF-003:**
- Given the codebase after the last migration batch,
- When `grep -w` runs for each old method name and FTL property (`isPOWB`, `isAWPBActive`, `isPlanningActive`,
  `isProgressActive`, `isUpKeepActive`, `isReportingActive`, `reportingActive`, `upKeepActive`,
  `UpKeepActive`, `planningActive`) over `marlo-web/src/main` and `marlo-web/src/test`, and a grep for the
  property `POWB` used as an FTL expression (`guide-button.ftl:91, 225` today),
- Then it MUST return no hit, except the excluded identifiers listed in `call-sites.md` §A3:
  - the action name `isProgressActive` (`struts-json.xml:74`) and its URL (`global.js:39, 210`);
  - the JS globals `reportingActive` / `upKeepActive` (`javascript-constants.ftl:30-31` left-hand side, and the 7 JS reads);
  - the comment in the applied migration `V2_6_0_20260930_0948__…sql:3`;
  - `ProgressActiveValidationAction`, `reportingActiveMenu`;
  - `POWB` in comments, i18n keys and string literals.
- And no FTL local, assign or macro parameter MUST still carry an old name (`call-sites.md` §A2 renamed with its
  callers),
- And adding a call to a removed method in a Java file MUST fail Checkstyle,
- AND IT MUST be shown that no phase-name literal decides a type outside `rest/` and `crpPhases.ftl:227`, by a
  grep for `getName()` on phase objects and `.name` on phase variables in FTL, as run.

**AC for FN-006, DA-002:**
- Given a database where AICCRA (45) and AICCRA_III (47) exist,
- When the name-change migration runs,
- Then 47 and every later unit MUST show `Planning`, `Progress` and `Reporting`,
- And AICCRA MUST still show `AWPB`, `Progress` and `AR`,
- BUT a row whose `description` / `upkeep` does not match its name's type MUST keep its name.

**AC for FN-007:**
- Given the admin creation flow with AICCRA (45) as the template, and again with AICCRA_III (47),
- When a unit is created,
- Then its phases MUST be named Planning, Progress and Reporting by type,
- And no phase MUST carry `description = 'Planing'`,
- BUT a template phase of an unknown type MUST be copied unchanged.

**AC for FN-008, INT-2:**
- Given a progress phase of AICCRA_III with the specificity `deliverable_shared_clusters_trainees_active` on,
  and a shared cluster submitted in that year's plan phase,
- When the deliverable participants section is opened,
- Then the cluster's trainee numbers MUST be editable, with no "Cluster submitted." label and no note,
- And AICCRA's progress phase MUST behave exactly as today,
- BUT the lock MUST NOT change in any plan or reporting phase.

**AC for FN-009:**
- Given a plan, a progress, a reporting and the empty phase,
- When `getCurrentCycle()` is called,
- Then it MUST return `Planning`, `Planning`, `Reporting` and `Planning` respectively, as today.

**AC for DA-001, MIG-001, MIG-002** — each scenario runs on a throwaway schema holding `global_units` + `phases`
with both FKs re-added.
- **Expected row counts are computed by a SELECT run just before the migration, on the same schema.** The
  expected DA-001 count is `COUNT(*) WHERE description = 'Planing'`. The expected DA-002 count is the type-guarded
  rows of units after AICCRA, `Planing` counted as `Planning`. Each actual count MUST equal its expected count.
- Fixed numbers are not used, because the data moves: unit 50 appeared on 2026-10-28 (JD-2).
- Reference run on `aiccradb_actsave`, 2026-10-28, after JD-9:

| Scenario | Expected = actual (DA-001 / DA-002) | Required result |
|---|---|---|
| Real copy, first run | 12 / 66 | 47, 48, 49, 50 named Planning / Progress / Reporting; 45 keeps `AR, AWPB, Progress`; no `Planing` left |
| Same copy, second run | 0 / 0 | Idempotent |
| No global unit, no phase | 0 / 0 | No error |
| Global units, no phases | 0 / 0 | No error |
| No AICCRA | 6 / 0 | Typo fixed; nothing renamed |
| A later unit carries the acronym `AICCRA` (48) | 12 / 48 | Unit 48 untouched |
| Orphan phases (`global_unit_id = NULL`) | 1 / 0 | Orphans never renamed |
| Inconsistent row (phase 445 with `upkeep = 0`) | 12 / 65 | That row keeps `UpKeep` |

- AND IT MUST be shown that neither migration contains a numeric id literal for a user, global unit or phase.

**AC for OPS-004:**
- Given a release that applies DA-001 or DA-002,
- When the release runs,
- Then the `(id, description)` rows DA-001 will change and the `(id, name)` rows DA-002 will change MUST be
  captured by a SELECT just before each migration and recorded in the release notes,
- BUT a rollback MUST NOT be attempted by cutoff and type alone.

**AC for NF-001, NF-002:**
- Given the local app running on a DB copy, with the reference page set captured before a batch:
  - project list, project description;
  - deliverable, expected study, budget by partners;
  - the main menu and the guide button;
  - each of those in one plan, one progress and one reporting phase;
  - one page with no global unit;
- When the batch is applied and the same pages are captured again with the same user, phase and data,
- Then each pair MUST be byte-identical, except cache-bust query parameters,
- BUT a difference MUST NOT be waved through as "cosmetic". Any difference is a FAIL until explained by INT-1 or
  INT-2.

**AC for OPS-001:** the name-change migration's file sorts after the merge of the last code task that removes
name-based typing, and its header states the dependency.

**AC for OPS-002:** the release checklist contains the draft listing command, and its output is recorded in the
release notes. An empty listing is the expected result.

### Defect classes and their gates

| # | Defect class this spec can produce | Gate that catches it |
|---|---|---|
| D1 | A Java call site answers a different type than today | Characterization and equivalence unit tests (`mvn -q -o -pl marlo-web -am test`); row-by-row SQL comparison on the five DB copies |
| D2 | A missed or misspelled Java use | Clean compile (`marlo-verify`); the old methods are deleted in the last batch, so a missed use fails to compile |
| D3 | An FTL property that silently resolves to nothing (`!false`, `?exists`) | **No unit test can see it.** Gate: the rendered-HTML diff (NF-002) plus per-batch token counts. Substitute when the app cannot run: none; the batch does not close |
| D4 | A new exception (NPE, unboxing) | Null-matrix unit tests (empty phase, null `upkeep`, null `description`); NF-002 page without a global unit |
| D5 | A migration that fails or writes wrong rows | Throwaway-schema dry run of the 8 scenarios (`marlo-migration` checklist) |
| D6 | The name change applied before the code | OPS-001 ordering check at merge time; human check at the release HITL |
| D7 | Wrong names on unit creation | Unit test on the creation path with templates 45, 47 and an unknown-type phase |
| D8 | INT-2 not effective, or leaking to plan or reporting phases | NF-002 diff shows the expected change only on the progress page; a human check of the AICCRA_III progress page at the HITL pause (the local copy needs that phase made visible on a throwaway DB) |
| D9 | Checkstyle violations or the old names reintroduced | Checkstyle delta against HEAD; the NF-003 rule |
| D10 | A draft orphaned by the rename | OPS-002 listing in the release window (human check) |

**Accepted risk:** an external consumer of `phases.name` outside the repo (Power BI datasets, CLARISA). No
command in this repo can see it. It is asked of the BI owner (§8 OQ2) before DA-002.

## 7. Constitutional Compliance Checklist

- [x] Phase replication: unchanged. No `ManagerImpl` is edited; the `getDescription()` reads in managers stay
  (§4).
- [x] Save validation: the validators keep the same pipeline; only the phase-type call they make changes, with
  identical results (FN-002).
- [x] Permissions: no new action and no interceptor stack change. Interceptors that call the old type methods are
  migrated with identical results.
- [x] Specificity: none added.
- [x] Migrations: two Flyway DML migrations named by `marlo-migration` from the real clock.
- [x] i18n: no new user-facing literal. Phase names are data, not i18n keys.
- [x] License header: any new Java file (the test classes) carries the GPL header.
- [x] Code style: Checkstyle delta 0, plus the new rule (NF-003).
- [x] REST: no `/api/*` change (§4).
- [x] Audit: `phases` changes through Flyway, not through the app.
- [x] Dependency floors: unchanged.
- [x] Branching: `phase-type-unification` from `staging`; merge target `staging`.

## 8. Open Questions

- **OQ1** — Should CRP Admin (`crpPhases.ftl`) keep showing the stored name? Default: yes, so after DA-002 it
  shows the new names.
- **OQ2** — Does any consumer outside the repo read `phases.name` (Power BI, CLARISA)? Owner: BI owner, before
  DA-002.
- **OQ3** — The QA team's decision on `qa_innovations` (filter by type, or accept both names). Owner: QA lead,
  before DA-002.
- **OQ4** — Production read-only checks. Owner: release HITL.
  - **Before the first code task (OPS-003):** the `description` / `upkeep` combinations.
  - **Before DA-002:**
    - count of `description = 'Planing'`;
    - rows whose name does not match their type;
    - units after AICCRA that hold `AWPB` / `Progress`;
    - the budget-execution count of proposal R7.

## 9. Decision Log

- 2026-10-28 — Unify the phase type into one method family on `Phase`, not on `BaseAction`. Rationale:
  managers, interceptors, summaries and loops evaluate phases other than the actual one, and `marlo-data`
  cannot reach `BaseAction`.
- 2026-10-28 — Use `isPlanningPhase` / `isProgressPhase` / `isReportingPhase`, with no separate "planning
  cycle" method (owner's naming). Rationale: a second "planning" method would be confused with the plan phase.
- 2026-10-28 — Use strict definitions (`"Planning".equals`, `Boolean.TRUE/FALSE.equals`), with DA-001 as a
  prerequisite. Rationale: the cycle defined as "not reporting" returns true for the empty phase.
- 2026-10-28 — Treat the shared-cluster lock in progress phases as a bug and fix it (INT-2). Rationale: it was
  exempted on purpose for AICCRA in 2024, and the hardcoded name is what keeps that exemption from other units.
- 2026-10-28 — Keep `crpPhases.ftl:227` unchanged. Rationale: a type check would lock AICCRA's AWPB 2019
  (phase 396), and DA-002 renames no 2019 phase.
- 2026-10-28 — Leave `rest/` out. Rationale: MARLO does not call it and `/api/**` is not in use. The resolver is
  the follow-up if it is reactivated.
- 2026-10-28 — Change the stored values of `name`, not the column, and not a display-only label. Rationale: it
  is the owner's request, and after unification `name` carries no logic in `marlo-web` outside `rest/`.
- 2026-10-28 — The `reporting` column is evaluated at the end of the process, not in this spec (owner).
- 2026-10-28 — Full unification (old methods removed) is in scope (owner). Safety comes from characterization
  tests, compile-time removal and the rendered-HTML diff.
- 2026-10-28 — Full unification reconfirmed by the owner after seeing the file estimate: about 135 files. Most of
  the 117 files in `call-sites.md` §A1 only rename uses of the old methods; the rest are the name-literal,
  creation, migration, test and rule files (`design.md` §17, RJ-3).
- 2026-10-28 — Judgment Day round 1 corrections:
  - JD-1: guarded and negated sites keep their truth table on five inputs (NF-005).
  - JD-2: migration counts are computed before the run, not fixed; the typo spreads by template copy.
  - JD-3: the inventory was regenerated; FTL locals and parameters are renamed with their callers; the exclusions
    are listed.
  - JD-7: OPS-003, the production check, runs before the first code task.
  - JD-8: INT-3 is a class of change, with its known sites listed.
  - JD-9: DA-002 also renames `AWPB`.
  - JD-10: INT-4 added.
  - JD-11: `cycTone` is out of scope.

  Closure sweep: the superseded values (`6 rows`, `54`, `~420`, `9 Java sites`, `INT-1..INT-3`) were grepped across
  this folder and updated in `design.md`, `requirements.md` and `call-sites.md`. `proposal.md` keeps its committed
  intent, annotated where it is superseded.
