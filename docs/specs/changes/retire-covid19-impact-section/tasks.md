# Retire The COVID-19 Impact Section — Tasks

**Spec ID:** CHG-RETIRE-COVID19
**Status:** Approved (2026-09-29)
**Owner:** Kenji Tanaka
**Last Updated:** 2026-09-29
**Implements design:** docs/specs/changes/retire-covid19-impact-section/design.md
**Branching:** `staging`, by the user's choice for this session's work
**Target merge:** already on `staging`; promoted to `main` per the release process

---

## 1. Execution Context

| Item | Value |
|---|---|
| Java | 17 — `scripts/run-marlo-java17.sh` only for the manual checks, never while another agent builds (`CLAUDE.md` → *Concurrency*) |
| Compile gate | `~/.claude/skills/marlo-verify/scripts/clean-compile.sh` (clean recompile of marlo-data + marlo-web) |
| Checkstyle gate | `~/.claude/skills/marlo-verify/scripts/checkstyle.sh --baseline <changed .java files>` — delta must be 0 |
| Hygiene gate | `~/.claude/skills/marlo-verify/scripts/java-hygiene-check.sh` |
| Local DB | `aiccradb1`–`aiccradb4`, read-only except a throwaway schema created and dropped by T04 |
| Unit tests | Not claimed: no test covers this feature (`grep -rln "ProjectImpacts\|Covid19" marlo-*/src/test` → 0) |

## 2. Pre-flight Checklist

- [ ] `requirements.md` and `design.md` approved; `judgment.md` reads `JUDGMENT: APPROVED ✅`.
- [ ] Working tree clean apart from the user's own `docs/specs/bugfix/activities-save-performance/`, which no task touches.
- [ ] **Baseline for FN-004**: record the project menu entries and completeness marks of one AICCRA project from the
      currently deployed test environment, or from a local run taken before T01. Owner: the user, at the first HITL pause.

## 3. Task List

### CHG-RETIRE-COVID19-T01 — Remove the screen, the report and their routes

- **Size:** S · **Depends on:** none · **Module:** marlo-web
- **Requirements:** FN-002, FN-003 (scenario *Summaries board*; scenario *Retired routes* — route removal only, the
  404 is observed in T05)
- **Design:** §3 marlo-web, §5, §9, DD-1
- **Files touched:**
  - delete `action/projects/ProjectImpactsAction.java`, `action/summaries/ImpactCovid19SummaryAction.java`
  - delete `webapp/WEB-INF/crp/views/projects/projectImpacts.ftl`, `webapp/crp/js/projects/projectImpacts.js`,
    `resources/pentaho/crp/ImpactCovid19.prpt`
  - edit `resources/struts-projects.xml` — remove `{crp}/impacts` and `{crp}/impactCovid19Summary`
  - edit `webapp/WEB-INF/crp/views/projects/menu-projects.ftl` — remove the `covid19` entry (line 38)
  - edit `webapp/WEB-INF/crp/views/summaries/boardSummaries.ftl` — remove the report card (line 74)
- **Constitutional checks:** keep each file's line endings — `menu-projects.ftl` and `boardSummaries.ftl` are LF
  (`grep -c $'\r'` → 0 for both at `12e063e323`); `struts-projects.xml` is checked the same way before editing.
- **Tests:** none exist; see Verification.
- **Done when:** clean recompile succeeds, and the sweep below returns 0.
- **Verification:**
  - `grep -rnI -E "ProjectImpactsAction|ImpactCovid19SummaryAction|projectImpacts\.(ftl|js)|ImpactCovid19\.prpt|impactCovid19Summary|'covid19'|\{crp\}/impacts\b" marlo-web/src/main` → 0 hits.
  - Clean recompile of marlo-data + marlo-web: BUILD SUCCESS.
- **Falsifier:** restoring only the `covid19` line in `menu-projects.ftl` makes the sweep return 1 hit while the build
  stays green — the grep, not the compiler, is the gate for FTL and XML.
- **Red run:** n/a — no automated test exists for this surface.
- **Disqualifier:** a sweep run with a pattern other than the one above, or scoped to fewer directories, is not
  evidence; quote the command as run.
- **Consumers:** `menu-projects.ftl:38` and `boardSummaries.ftl:74` are the only readers of the two screens (P-5);
  `ProjectImpactsAction.java:295` is the only reader of `Permission.PROJECT_COVID19_*` (P-6), deleted here.
- **Review:** `checklist` — whole-file deletions plus three small edits; the grep and the compile carry the evidence.
- **Skills:** `marlo-verify`.

### CHG-RETIRE-COVID19-T02 — Remove the section from web validation and completeness

- **Size:** M · **Depends on:** T01 · **Module:** marlo-web
- **Requirements:** FN-001 (scenarios *Project menu of an active unit* and *A stale section name reaches the
  validator*, including `BUT it must NOT change the completeness of any project` and `AND IT MUST NOT raise a server
  error`), FN-004
- **Design:** §3 marlo-web, §7, DD-5, P-3, P-4
- **Files touched:**
  - delete `validation/projects/ProjectImpactsValidator.java`
  - edit `validation/projects/ProjectSectionValidator.java` — remove `validateProjectImpactCovid()`, the
    `ProjectImpactsManager` and `ProjectImpactsValidator` fields, constructor parameters and imports
  - edit `validation/BaseValidator.java` — remove the `saveMissingFields(ProjectImpacts, …)` overload and its import
  - edit `action/json/project/ValidateProjectSectionAction.java` — remove both `case IMPACTS` blocks (`:136`, `:538`)
  - edit `action/BaseAction.java` — remove `isYearToShowSectionCovid19()`
  - edit `config/APConstants.java` — remove `CRP_COVID_REQUIRED`, `CRP_SHOW_SECTION_IMPACT_COVID19`,
    `CRP_SHOW_SECTION_IMPACT_COVID19_RANGES_YEARS`
- **Constitutional checks:** save pipeline of every surviving section untouched (Hard rule 2).
- **Tests:** none exist; see Verification.
- **Done when:** clean recompile, Checkstyle delta 0 and the hygiene check are all green; the diff of
  `ValidateProjectSectionAction` and `ProjectSectionValidator` touches only `IMPACTS` / impact-covid lines.
- **Verification:**
  - Clean recompile of marlo-data + marlo-web: BUILD SUCCESS.
  - `checkstyle.sh --baseline` on the five edited Java files: delta 0.
  - `java-hygiene-check.sh`: no `FIX` line (pre-existing findings in these files are fixed or reported, per the skill).
  - `git diff -U0` of `ValidateProjectSectionAction.java` and `ProjectSectionValidator.java`: every removed line
    belongs to the impacts/COVID branch — read and quoted in the task report.
  - `ValidateProjectSectionAction.java:709` still sets `validSection` from `ProjectSectionStatusEnum.value(…) != null`
    before any `switch` (unchanged line, re-read).
- **Falsifier:** removing the `IMPACTS` enum value in T03 while one `case IMPACTS` is left here makes the T03 compile
  red; deleting a neighbouring `case` by mistake shows up in the `git diff -U0` read.
- **Red run:** n/a — no automated test exists for section validation.
- **Disqualifier:** a green compile does not prove FN-004. The FN-004 claim rests on the diff read above plus the
  T05 human check; if the diff removes any line outside the impacts branch, the task is not done.
- **Consumers:** `ProjectLeaderEditAction.java:94` iterates `ProjectSectionStatusEnum.values()` with no `IMPACTS`
  case; `BaseAction.java:4906` and `:6819` guard `value() == null` (P-4). `menu-projects.ftl:38`, the only caller of
  `isYearToShowSectionCovid19()`, is removed by T01.
- **Review:** `full` — touches `BaseAction`, `BaseValidator` and the section validator that every project section
  shares.
- **Skills:** `marlo-verify`, `error-handling-patterns`.

### CHG-RETIRE-COVID19-T03 — Remove the persistence layer in marlo-data

- **Size:** M · **Depends on:** T02 · **Module:** marlo-data
- **Requirements:** FN-001, NF-002, DA-004 (tables kept, only unmapped)
- **Design:** §3 marlo-data, §4 *Entity changes*, DD-2, DD-5, P-7
- **Files touched:**
  - delete `data/model/ProjectImpacts.java`, `ProjectImpactsCategories.java`, `ReportProjectImpactsCovid19DTO.java`
  - delete `data/dao/ProjectImpactsDAO.java`, `ProjectImpactsCategoriesDAO.java`, `mysql/ProjectImpactsMySQLDAO.java`,
    `mysql/ProjectImpactsCategoriesMySQLDAO.java`
  - delete `data/manager/ProjectImpactsManager.java`, `ProjectImpactsCategoriesManager.java`,
    `impl/ProjectImpactsManagerImpl.java`, `impl/ProjectImpactsCategoriesManagerImpl.java`
  - delete `resources/xmls/ProjectImpacts.hbm.xml`, `ProjectImpactsCategories.hbm.xml`
  - edit `data/model/SectionStatus.java` — field, getter, setter and the `toString()` term (`:67`, `:127`,
    `:219-220`, `:271`)
  - edit `data/dao/SectionStatusDAO.java`, `mysql/SectionStatusMySQLDAO.java`, `data/manager/SectionStatusManager.java`,
    `impl/SectionStatusManagerImpl.java` — remove `getSectionStatusByProjectImpacts()`
  - edit `resources/xmls/SectionStatuses.hbm.xml` — remove the `projectImpact` association
  - edit `resources/hibernate.cfg.xml` — remove the two mapping entries
  - edit `data/model/ProjectSectionStatusEnum.java` — remove `IMPACTS("impacts")`
  - edit `config/APConstants.java` — remove the two `CRP_SHOW_SECTION_IMPACT_COVID19*` constants
  - edit `security/Permission.java` — remove `PROJECT_COVID19_BASE_PERMISSION`, `PROJECT_COVID19_EDIT_PERMISSION`
- **Constitutional checks:** constants removed from **both** `APConstants` files between T02 and T03 (Hard rule 4).
- **Tests:** none exist; see Verification.
- **Done when:** clean recompile, Checkstyle delta 0 and the hygiene check are all green, and the sweep returns 0.
- **Verification:**
  - Clean recompile of marlo-data + marlo-web: BUILD SUCCESS, with the file counts printed by the script (a
    marlo-web count far below the usual ~1,046 means stale classes were reused and the run is not evidence).
  - `grep -rnI -E "ProjectImpacts|project_impact|IMPACTS\(|PROJECT_COVID19|CRP_SHOW_SECTION_IMPACT_COVID19" marlo-data/src/main marlo-web/src/main | grep -v "/database/"` → 0 hits.
  - `checkstyle.sh --baseline` on the edited Java files: delta 0.
- **Falsifier:** leaving the `ProjectImpacts.hbm.xml` entry in `hibernate.cfg.xml` after deleting the class compiles
  but fails at Hibernate start-up — the grep above catches it (`ProjectImpacts` in `hibernate.cfg.xml` → 1 hit);
  leaving the `toString()` term makes the compile red.
- **Red run:** n/a — no automated test exists for the mappings.
- **Disqualifier:** an incremental compile is not evidence (`marlo-verify` Gate 1); only the clean recompile counts.
- **Consumers:** every consumer of the deleted symbols is inside T01–T03 (P-3); no Spring XML bean wiring
  (annotation-driven, both judges).
- **Review:** `checklist` — deletions of self-contained classes plus mechanical edits; compile and grep carry the
  evidence.
- **Skills:** `marlo-verify`.

### CHG-RETIRE-COVID19-T04 — Migration that removes the configuration rows

- **Size:** S · **Depends on:** none (applies with the same deploy) · **Module:** marlo-web (migrations)
- **Requirements:** DA-001, DA-002, DA-003, DA-004, MIG-001 (scenarios *First run of the migration* — including
  `AND IT MUST match rows by key, permission string or section name` — and *Second run, or a database without the
  rows* — including `BUT it must NOT delete a parameter or permission whose key merely starts with a retired key`)
- **Design:** §4, §8, DD-3, DD-6, P-8, P-12
- **Files touched:** new `V2_6_0_<YYYYMMDD>_<HHMM>__RetireCovid19ImpactSection.sql`, name from
  `~/.claude/skills/marlo-migration/scripts/new-migration.sh RetireCovid19ImpactSection`.
- **Constitutional checks:** real-clock filename (Hard rule 5); DML only; no id, global unit or user assumed.
- **Tests:** two runs on a throwaway schema, below.
- **Done when:** the table below is filled from the two runs and matches the expected counts.
- **Verification:** create `zz_mig_test` with `CREATE TABLE … LIKE` + `INSERT … SELECT` copies from `aiccradb2` of
  `parameters`, `custom_parameters`, `permissions`, `role_permissions`, `center_role_permissions`, `section_statuses`,
  `project_impacts`, `project_impacts_categories`; re-add the five FKs of P-8; add decoys — a parameter
  `crp_covid_required_other`, a permission `crp:{0}:project:{1}:impactsOther` with one role grant, and one
  `section_statuses` row named `impacts` (the environment-we-cannot-see case). Run the migration twice; record, before
  and after each run: rows of the three keys, the two permissions, their grants, `impacts` statuses, both decoys,
  `project_impacts` (9) and `project_impacts_categories` (5). `DROP DATABASE zz_mig_test`; confirm it is gone.
  Expected: run 1 removes 3 keys × 3 types, their `custom_parameters`, 2 permissions, 276 grants (in `aiccradb2`),
  and the synthetic `impacts` status; decoys and both data tables unchanged; run 2 changes 0 rows.
- **Falsifier:** replacing one `=` with `LIKE '<key>%'` deletes a decoy — the decoy count drops from 1 to 0 and the
  task fails; swapping the `role_permissions` and `permissions` deletes makes run 1 fail on the FK.
- **Red run:** execute the falsifier above once on the throwaway schema and record the red result before
  reverting it.
- **Disqualifier:** a run on copies without the FKs does not test the delete order; a run where the "before" count
  of a target is 0 proves nothing about that target; a run against `aiccradb1`–`4` themselves is forbidden.
- **Consumers:** `GlobalUnitCreationManagerImpl.createGlobalUnit()` → `cloneRolePermissionsByAcronym` reads the
  deleted grants (P-9); it clones nothing for them afterwards.
- **Review:** `full` — data deletion on every environment, including one no one here can see.
- **Skills:** `marlo-migration`, `marlo-verify`.

### CHG-RETIRE-COVID19-T05 — i18n, documentation and closing sweep

- **Size:** S · **Depends on:** T01, T02, T03, T04 · **Module:** marlo-web, docs
- **Requirements:** NF-001 (scenario *Closing sweep*, including `AND IT MUST be run and quoted`), FN-003 (the 404
  observation), FN-004 (human check)
- **Design:** DD-4, §3 *Documentation*, P-10, P-11, P-13
- **Files touched:** `resources/global.properties`, the `custom/*.properties` that hold the keys, `docs/ux-ui/design.md`
  (line 161 only).
- **Tests:** none exist; see Verification.
- **Done when:** every step below has its output quoted in the task report.
- **Verification:**
  1. **i18n (DD-4):** for each candidate key (`breadCrumb.menu.projectImpacts`, `projects.menu.impacts.covid19`,
     `summaries.board.report.impactCovid19Summary*`, `projects.impacts.*`, `summaries.impacts.*`), run
     `grep -rnI "<key>" marlo-web/src/main marlo-data/src/main | grep -v "\.properties:"` and delete the key from every
     `.properties` file only when it returns 0. Dynamic prefixes: `breadCrumb.menu.${label}` is only built from a
     `label` a view passes (P-11), so `breadCrumb.menu.projectImpacts` goes once `projectImpacts.ftl` is gone. Keys
     used by surviving files (`saving.missingFields`, `project.summary`, …) stay.
  2. **Documentation:** remove the `projects/projectImpacts.ftl` line from `docs/ux-ui/design.md` §4.
  3. **Closing sweep (NF-001):** `grep -rlIE "ProjectImpacts|projectImpacts|ImpactCovid19|impactCovid19|Covid19|covid19|COVID19|ProjectSectionStatusEnum\.IMPACTS|case IMPACTS|crp_covid_required|CRP_COVID_REQUIRED|PROJECT_COVID19|project_impact" . | grep -vE "/target/|/bin/|\.git/|database/migrations/"`
     → only this spec folder and the out-of-scope features of `requirements.md` §4 (`studiesTemplates.ftl`,
     `AR2018_flagshipProgress.ftl` and their i18n keys, `report-orchestrator` `has_covid_analysis`). Every other hit
     is a defect.
  4. **Human checks (HITL, local stack, run only when no other agent is building):** `/projects/<crp>/impacts.do`
     and `/projects/<crp>/impactCovid19Summary.do` return 404, not 500 (P-10); the AICCRA project menu and
     completeness marks equal the Pre-flight baseline (FN-004).
- **Falsifier:** leaving one retired key in `aicrra.properties` makes the closing sweep return that file — the sweep
  is red on it; a 500 on either retired path fails step 4.
- **Red run:** n/a — the closing sweep is itself the negative check.
- **Disqualifier:** if the stack cannot be started, step 4 is reported as **not run**, never as passed; the spec then
  stays open at the HITL pause.
- **Consumers:** the `.properties` keys shared with surviving views (P-3 list of shared keys; re-derived by step 1).
- **Review:** `checklist` — text removals gated by the per-key and closing greps.
- **Skills:** `marlo-verify`, `marlo-commit` (for the commit proposal).

## 4. Dependency Graph

```
T01 ──► T02 ──► T03 ──┐
                      ├──► T05
T04 ──────────────────┘
```

T04 is independent of T01–T03 and can run in any order before T05.

## 5. Testing Plan

| Type | Coverage |
|---|---|
| Unit | Not applicable — no test covers this feature, and the change deletes rather than adds behavior |
| Integration | Clean recompile after T01, T02 and T03; migration double-run on a throwaway schema (T04) |
| Regression (manual) | T05 step 4: retired routes return 404; AICCRA project menu and completeness equal the baseline |
| Non-functional | Checkstyle delta 0 on every edited Java file |
| Accessibility | Not applicable — no surviving UI changes |

## 6. Operational Steps

- **Migration deploy:** applies automatically with the next deploy (Flyway, one transaction, P-12).
- **Specificity rollout:** none — three specificities are removed.
- **BI / AI coordination:** none — `report-orchestrator` has no reference to the section (both judges).
- **Backups:** the standard pre-deploy backup; the only deleted rows are configuration rows.
- **Notifications:** none.

## 7. Rollback Plan

- **Code:** revert the task commits; every deleted file comes back from git.
- **Data:** the deleted rows are configuration only — three `parameters` × three types, their `custom_parameters`,
  two `permissions` and their grants. Restoring them means re-running the 2020 migrations' inserts
  (`V2_6_0_20200504_0849`, `V2_6_0_20200509_1015`, `V2_6_0_20200518_1348`, `V2_6_0_20201009_0800`), with the
  descriptions later rewritten by `V2_6_0_20260925_1543` in a new migration. `project_impacts` and
  `project_impacts_categories` are never touched.
- **Specificity:** not applicable after rollback of the data above.

## 8. Definition of Done

- [ ] Every acceptance criterion of `requirements.md` verified, each with its output quoted.
- [ ] Constitutional compliance checklist confirmed.
- [ ] Clean recompile green; Checkstyle delta 0; hygiene check clean.
- [ ] Migration double-run table recorded in T04.
- [ ] Closing sweep quoted in T05 with only allowed hits.
- [ ] Human checks of T05 step 4 done, or recorded as not run with the spec kept open.
- [ ] This `tasks.md`: every task marked done with verification notes.
- [ ] Committed on `staging` through `marlo-commit`.
