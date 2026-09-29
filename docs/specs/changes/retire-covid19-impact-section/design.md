# Retire The COVID-19 Impact Section — Design

**Spec ID:** CHG-RETIRE-COVID19
**Status:** Approved (2026-09-29)
**Owner:** Kenji Tanaka
**Last Updated:** 2026-09-29
**Requirements:** [`requirements.md`](./requirements.md)
**Verified at:** `12e063e323`

---

## 1. Executive Summary

Delete the feature from the outside in — views and routes, then actions and validators, then the marlo-data
persistence layer — so the build compiles after every step. One DML-only migration removes the configuration
rows. The two data tables, their rows, and the nullable `section_statuses.project_impact_id` column stay in the
database, unmapped.

## 2. Architecture Overview

The feature is a closed subgraph: every consumer of every symbol it defines is itself part of the feature (P-3 to
P-6). Removing it needs no replacement, only the deletion of three kinds of hook it has into shared code:

| Shared code | Hook to remove |
|---|---|
| `menu-projects.ftl`, `boardSummaries.ftl` | One menu entry and one summary card |
| `ValidateProjectSectionAction` | Two `IMPACTS` cases |
| `ProjectSectionValidator` | One method with its injected manager and validator |
| `BaseValidator` | The `saveMissingFields` overload for impacts |
| `BaseAction` | `isYearToShowSectionCovid19()` |
| `SectionStatus` + DAO + manager | The `projectImpact` association and `getSectionStatusByProjectImpacts()` |
| `ProjectSectionStatusEnum` | The `IMPACTS` value |
| Both `APConstants`, `Permission` | Seven constants: three in marlo-web `APConstants`, two in marlo-data `APConstants`, two in `Permission` |
| `hibernate.cfg.xml`, `SectionStatuses.hbm.xml` | Two mapping entries and one association |

## 3. Module Footprint

### marlo-web

| Delete | Edit |
|---|---|
| `action/projects/ProjectImpactsAction.java` | `action/BaseAction.java` — remove `isYearToShowSectionCovid19()` |
| `action/summaries/ImpactCovid19SummaryAction.java` | `action/json/project/ValidateProjectSectionAction.java` — remove both `IMPACTS` cases |
| `validation/projects/ProjectImpactsValidator.java` | `validation/projects/ProjectSectionValidator.java` — remove `validateProjectImpactCovid()`, its manager and validator fields and constructor parameters, and their imports |
| `webapp/WEB-INF/crp/views/projects/projectImpacts.ftl` | `validation/BaseValidator.java` — remove the `ProjectImpacts` overload of `saveMissingFields` and its import |
| `webapp/crp/js/projects/projectImpacts.js` | `config/APConstants.java` — remove `CRP_COVID_REQUIRED`, `CRP_SHOW_SECTION_IMPACT_COVID19`, `CRP_SHOW_SECTION_IMPACT_COVID19_RANGES_YEARS` |
| `resources/pentaho/crp/ImpactCovid19.prpt` | `resources/struts-projects.xml` — remove the `{crp}/impacts` and `{crp}/impactCovid19Summary` actions |
| | `webapp/WEB-INF/crp/views/projects/menu-projects.ftl` — remove the `covid19` entry |
| | `webapp/WEB-INF/crp/views/summaries/boardSummaries.ftl` — remove the report card |
| | `resources/global.properties` and `custom/*.properties` — remove the keys that no surviving file reads (DD-4) |

### marlo-data

| Delete | Edit |
|---|---|
| `data/model/ProjectImpacts.java`, `ProjectImpactsCategories.java`, `ReportProjectImpactsCovid19DTO.java` | `data/model/SectionStatus.java` — remove the `projectImpact` field, getter and setter, and its term in `toString()` |
| `data/dao/ProjectImpactsDAO.java`, `ProjectImpactsCategoriesDAO.java` and both `mysql/*MySQLDAO.java` | `data/dao/SectionStatusDAO.java`, `mysql/SectionStatusMySQLDAO.java` — remove `getSectionStatusByProjectImpacts()` |
| `data/manager/ProjectImpactsManager.java`, `ProjectImpactsCategoriesManager.java` and both `impl/*ManagerImpl.java` | `data/manager/SectionStatusManager.java`, `impl/SectionStatusManagerImpl.java` — same method |
| `resources/xmls/ProjectImpacts.hbm.xml`, `ProjectImpactsCategories.hbm.xml` | `resources/xmls/SectionStatuses.hbm.xml` — remove the `projectImpact` association |
| | `resources/hibernate.cfg.xml` — remove the two mapping entries |
| | `data/model/ProjectSectionStatusEnum.java` — remove `IMPACTS` |
| | `config/APConstants.java` — remove the two `CRP_SHOW_SECTION_IMPACT_COVID19*` constants |
| | `security/Permission.java` — remove `PROJECT_COVID19_BASE_PERMISSION`, `PROJECT_COVID19_EDIT_PERMISSION` |

### marlo-core / marlo-utils

Not affected.

### Documentation

| Edit |
|---|
| `docs/ux-ui/design.md` §4 *Screen Inventory* — remove the `projects/projectImpacts.ftl` line (P-13). A constitutional document: the edit is limited to that one line and is this spec's own deliverable, by user decision 2026-09-29 (`judgment.md` JD-2) |

## 4. Data Model Changes

### Migration

One file, `V2_6_0_<YYYYMMDD>_<HHMM>__RetireCovid19ImpactSection.sql`, timestamp from the real clock. DML only.
Delete order, children before parents (P-8):

1. `role_permissions`, then `center_role_permissions`, whose `permission_id` points at the two permissions,
   matched by `permissions.permission` string;
2. `permissions` with those two strings;
3. `custom_parameters` whose `parameter_id` points at the three keys;
4. `parameters` with those three keys;
5. `section_statuses` with `section_name = 'impacts'`.

### Entity changes

Unmapped, not dropped: `project_impacts`, `project_impacts_categories`, `section_statuses.project_impact_id` and its
FK `section_statuses_impacts`.

### Indices, FKs, enums

No DDL. `ProjectSectionStatusEnum` loses `IMPACTS` (P-4).

### Backfill / data migration

None. DA-004 keeps every row of the two tables.

## 5. API / Action Surface

### Struts actions (.do)

`/projects/{crp}/impacts` and `/projects/{crp}/impactCovid19Summary` are removed. A request to either now gets a
**404**: Struts finds no action in `/projects`, the inherited `default-action-ref` cannot resolve `login` there, and
the dispatcher answers the resulting `ConfigurationException` with `SC_NOT_FOUND` (P-10). Never a 500 (FN-003).

### Spring MVC REST

Not affected.

### Existing JSON endpoints

`validateProjectSection` keeps its contract; `impacts` becomes an unknown section name, which it already rejects
before its `switch` (P-4).

## 6. Persistence & Phase Replication Plan

Not applicable — no save or delete chain survives; the retired manager is deleted with its callers.

## 7. Validation & Save Pipeline

The `IMPACTS` branch disappears from project section validation and from completeness. Every other section's
validator is untouched. `crp_covid_required` was the only switch that made the section count, and it is `false` in
every active unit (P-1), so no project's completeness changes (FN-004).

## 8. Permissions & Edit Gates

The two permission strings and their grants are deleted. `GlobalUnitCreationManagerImpl` clones grants by role
acronym from the template unit (P-9), so once the rows are gone a new unit no longer inherits them. No surviving
action checks those strings (P-6).

## 9. Frontend / UX

Two elements removed, both already hidden for every active unit: the `covid19` menu entry and the summaries card.
Neither screen is referenced by any other view (P-5). No asset of a surviving view changes, so no cache-busting
parameter moves.

## 10. Design Decisions

| # | Decision | Rationale | Rejected alternative |
|---|---|---|---|
| DD-1 | Delete outside-in: views and routes → web actions and validators → marlo-data | Each layer only calls inward, so the build compiles after every task and a failure points at one layer | One big deletion: a compile error then implicates about 40 files at once |
| DD-2 | Unmap `project_impacts`, `project_impacts_categories` and `section_statuses.project_impact_id`, keep them in the schema | DA-004; Hibernate ignores an unmapped nullable column (P-7); keeps the migration DML-only | Dropping them now: DDL auto-commits in MySQL and would destroy the CCAFS rows |
| DD-3 | Match every migration row by key, permission string or section name, with exact equality | MIG-001; ids differ across environments; exact equality keeps `…_other` keys safe | `LIKE 'crp_show_section_impact_covid19%'`: would catch the ranges key by accident and anything added later with that prefix |
| DD-4 | Remove an i18n key only when a whole-repo search after the code removal finds no reader, dynamic prefixes included | The retired files use shared keys too (`saving.missingFields`, `project.summary`); `breadCrumb.menu.${label}` is built at runtime (P-11) | A fixed list of keys from the proposal: would delete shared keys or keep dead ones |
| DD-5 | Remove `IMPACTS` from `ProjectSectionStatusEnum` | Every reader resolves names through `value()` and handles `null` before switching, or iterates `values()` without an `IMPACTS` case (P-4) | Keeping a dead enum value: leaves a hook for a section that no longer exists |
| DD-6 | Delete `section_statuses` rows named `impacts` in the migration | Zero rows locally (P-2), but another environment could hold them, and after DD-5 nothing can read them | Leaving them: orphan rows no code can interpret |

### Reversion challenge (Step 2.3)

Every DD removes shipped behavior. One question, asked once for the set: **what does removing this break?**

- **Menu and completeness of the other sections** — not broken: P-4 shows each reader guards unknown names; FN-004
  keeps a human check for what no test covers.
- **A new Global Unit's roles** — not broken: fewer grants are cloned; no surviving permission depends on them.
- **A user with a bookmarked `/impacts` URL** — gets a 404 instead of the screen (P-10). Accepted:
  the screen is already hidden, so no active user reaches it through the UI.
- **Nothing concrete was named that the design does not address.** No DD changes.

## 11. Premise Ledger

Verified 13 · `UNVERIFIED` 0. P-10 was refuted at Judgment Day round 1 and is stated here as corrected (`judgment.md` JD-1).
Blast-radius triggers: `shared-state` (the design changes `SectionStatus`, `ProjectSectionStatusEnum`, `BaseValidator`,
`BaseAction`, both `APConstants`) and `consumer` (it removes exported symbols and a stored enum value) fired;
`live-path` does not apply — the design names no user action it keeps, only ones it removes.

| # | Claim | Class | Citation (as run) | Verified at | If false | Settled by |
|---|---|---|---|---|---|---|
| P-1 | No active Global Unit shows or requires the section | data-env | `custom_parameters` ⨝ `parameters` where key like `%covid%`, all four `aiccradb*`: AICCRA/AICCRA_III/TEST have required `false` and an empty range; `aiccradb3`/`4` show `true` with an empty range; only inactive CCAFS had a range | `12e063e323` | Retirement would remove a live section — **the spec's premise**, Impact High | — |
| P-2 | No `section_statuses` row is named `impacts` or has `project_impact_id` set | data-env | `select count(*) from section_statuses where section_name='impacts'` → 0; `… where project_impact_id is not null` → 0 (all four `aiccradb*`, re-run by both judges) | `12e063e323` | DD-6 would delete real data, Low | — |
| P-3 | Every consumer of `ProjectImpactsManager`, `ProjectImpactsCategoriesManager`, `ProjectImpactsValidator`, `ReportProjectImpactsCovid19DTO`, `validateProjectImpactCovid`, `getSectionStatusByProjectImpacts`, `isYearToShowSectionCovid19` is in the delete or edit list of §3 | consumer | `grep -rln "<symbol>" marlo-web/src marlo-data/src` per symbol, excluding `target/`: hits limited to `ProjectImpactsAction`, `ImpactCovid19SummaryAction`, `ProjectSectionValidator`, `ValidateProjectSectionAction`, `BaseValidator`, `BaseAction`, `menu-projects.ftl` and the feature's own model, DAO and manager files | `12e063e323` | A task gains an unlisted file, Low | — |
| P-4 | Every reader of `ProjectSectionStatusEnum` survives the removal of `IMPACTS` | shared-state | `grep -rn "ProjectSectionStatusEnum.values()\|ProjectSectionStatusEnum\.value(" marlo-web/src/main/java marlo-data/src/main/java`: `BaseAction.java:4906` returns `false` on `null` before its switch; `BaseAction.java:6819` checks `!= null`; `ValidateProjectSectionAction.java:709` sets `validSection` from `value() != null` before the switches at `:124` and `:242`; `ProjectLeaderEditAction.java:94` iterates `values()` with no `IMPACTS` case | `12e063e323` | DD-5 is reverted, Low | — |
| P-5 | No surviving view links to the two retired screens | consumer | `grep -rnI -E "[\"']impacts[\"']" marlo-web/src/main marlo-data/src/main`: only `menu-projects.ftl:38` (removed) and `center/…/programImpacts.ftl` (an unrelated field name) | `12e063e323` | T01 gains a view, Low | — |
| P-6 | The two permission constants are read only by `ProjectImpactsAction` | consumer | `grep -rn "PROJECT_COVID19_" marlo-web/src/main marlo-data/src/main` → `ProjectImpactsAction.java:295` only, besides `Permission.java` | `12e063e323` | T02 gains a caller, Low | — |
| P-7 | `section_statuses.project_impact_id` is nullable | data-env | `information_schema.columns` for `aiccradb2`: `project_impact_id nullable=YES` | `12e063e323` | DD-2 cannot leave it unmapped; a DDL change would be needed, High → it is verified | — |
| P-8 | The only FKs into the rows deleted are `role_permissions.permission_id`, `center_role_permissions.permission_id` → `permissions`, `custom_parameters.parameter_id` → `parameters`, `section_statuses.project_impact_id` → `project_impacts`, `project_impacts.project_impact_category_id` → `project_impacts_categories` | shared-state | `information_schema.key_column_usage` where `referenced_table_name in ('permissions','parameters','custom_parameters','project_impacts','project_impacts_categories')`, all four `aiccradb*`; no routine, view, trigger or event references `project_impacts` | `12e063e323` | The delete order in §4 changes, Low | — |
| P-9 | New Global Units clone role grants by acronym from the template | other | `GlobalUnitCreationManagerImpl.java` `createGlobalUnit()` → `roleManager.cloneRolePermissionsByAcronym(templateGlobalUnitId, …)` | `12e063e323` | DA-002's inheritance claim needs a code change instead, Low | — |
| P-10 | A request to an unmapped path under `/projects` gets a 404, never a 500 — **corrected**: the draft claimed a fall-through to `login` | other | `struts2-core-6.8.0-sources.jar` `DefaultConfiguration.java:604-625` resolves `default-action-ref` only inside the requested namespace; `login` is in package `home`, namespace `/` (`struts-home.xml:25,79`); `Dispatcher.java:746-748` answers `ConfigurationException` with `SC_NOT_FOUND`; every `struts-projects.xml` action is `{crp}/<literal>` except `reportingSummary`; no `unknown-handler` in `struts*.xml` | `12e063e323` | FN-003 needs a different mechanism, Low | — (the runtime response is also observed in T05) |
| P-11 | Breadcrumb labels are resolved as `breadCrumb.menu.${item.label}` at runtime | location | `breadcrumb.ftl:13,15,18`; `projectImpacts.ftl:19` passes `"label":"projectImpacts"` | `12e063e323` | DD-4's dynamic-prefix rule is unnecessary, Low | — |
| P-12 | Flyway 4.0.1 runs each SQL migration in its own transaction | other | `marlo-parent/pom.xml:29` `flyway.version=4.0.1`; `flyway-core-4.0.1-sources.jar` `SqlMigrationExecutor.java:75-77` `executeInTransaction()` returns `true` | `12e063e323` | A failure could leave partial deletes — mitigated anyway by idempotency (MIG-001), Low | — |
| P-13 | Outside code, the only file naming the retired feature is `docs/ux-ui/design.md:161` | consumer | `grep -rlIE "<NF-001 pattern>" .` excluding `target/`, `bin/`, `.git/`, `database/migrations/` → code files in §3, this spec folder, and `docs/ux-ui/design.md` (both judges, `judgment.md` JD-2) | `12e063e323` | The closing sweep fails on an unlisted file, Low | — |

## 12. Budget

| Measure | Estimate |
|---|---|
| Tasks | 5 |
| LOC | about 2,200 deleted — 19 whole files, of which the 18 text files hold 2,103 lines (`wc -l`, measured) and the 19th is the binary `ImpactCovid19.prpt`; plus roughly 100 lines in the edited files — and about 30 added (the migration) |
| Review rounds | 1 per task |

Tripwire for `/akili-execute`: more than 5 tasks, more than 60 LOC added, or a second rework round on any task stops
the run for the user.
