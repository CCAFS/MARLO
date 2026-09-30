# Retire The COVID-19 Impact Section — Proposal

## Document Control

| Field | Value |
|---|---|
| **Spec Path** | `changes/retire-covid19-impact-section` |
| **Proposal ID** | `CHG-RETIRE-COVID19-IMPACT-SECTION-001` |
| **Slug** | `retire-covid19-impact-section` — the leading token of the argument; the rest of the argument is context |
| **Type** | **Change** (retirement of a dormant feature) |
| **Approval Mode** | **`gated`** — no end-to-end mandate was given |
| **Parent Spec** | none — flat spec |
| **Depends on** | **none** |
| **Parallel-safe** | **no** — touches `BaseAction`, `BaseValidator`, `ProjectSectionValidator`, `SectionStatus`, both `APConstants` and `struts-projects.xml`, all of them shared with most other specs |
| **Status** | **Approved** — 2026-09-29 by Kenji Tanaka (Option B; CCAFS rows kept) |
| **Date** | 2026-09-29 |
| **Working branch** | `staging` at `618dc3925a` |
| **Evidence base** | Read-only queries on the local copies `aiccradb1`–`aiccradb4`, and the code at `618dc3925a` |

---

## Intent

**Remove the project COVID-19 impact section end to end — its screen, report, validation, permissions,
specificities and data — because no active Global Unit shows it and nobody has written to it since June 2020.**

The section was added in May 2020 for the CGIAR research programmes. Those programmes are closed, but the
feature still runs in every request that builds the project menu, validates a project, or clones roles for a
new Global Unit.

---

## Problem / Current Behavior

### The feature is wired up but unreachable

| Claim | Evidence |
|---|---|
| The menu entry shows only when `crp_show_section_impact_covid19` is on **and** the phase year falls in `crp_show_section_impact_covid19_ranges_years` | `menu-projects.ftl:38`; `BaseAction.isYearToShowSectionCovid19()` (`BaseAction.java:8334`) returns `false` on an empty range |
| The summary card is never offered to AICCRA, whatever the flag says | `boardSummaries.ftl:74` — `… && !action.isAiccra()` |
| `crp_covid_required` makes the section count toward project completeness | `ProjectImpactsValidator.java:71`, `ValidateProjectSectionAction.java:539` |
| In `aiccradb1`/`aiccradb2`, the three specificities are `false` / empty / `false` for AICCRA (45), AICCRA_III (47) and TEST (48) | `custom_parameters` query, 2026-09-29 |
| In `aiccradb3`/`aiccradb4`, AICCRA has `crp_show_section_impact_covid19 = true` but an **empty** range, so the section stays hidden | same query |
| The only Global Unit that ever had it visible is CCAFS (1), range `2020-2021`, and CCAFS is inactive | same query; `global_units.is_active = 0` |

### The data is historical and belongs to one closed programme

| Table | Rows (all 4 local DBs) | Detail |
|---|---|---|
| `project_impacts` | **9** | All CCAFS projects, years 2019–2021, last `active_since` **2020-06-26** |
| `project_impacts_categories` | 5 | Lookup values |
| `section_statuses` with `section_name = 'impacts'` or `project_impact_id` set | **0** | — |
| `permissions` | 2 (`562`, `563`) | `crp:{0}:project:{1}:impacts` and `:impacts:canEdit` |
| `role_permissions` on those two | 262–276 | 14 per Global Unit, **including 45, 47 and 48** |

AICCRA never wrote a row. The 14 grants per unit on AICCRA, AICCRA_III and TEST are there only because
`GlobalUnitCreationManagerImpl` clones every role permission of the template unit
(`roleManager.cloneRolePermissionsByAcronym`) — so each new unit inherits a permission for a screen it cannot see.

### Secondary defect

`CRP_COVID_REQUIRED` is declared only in `marlo-web`'s `APConstants.java:51`, not in `marlo-data`'s — a breach of
`CLAUDE.md` Hard rule 4 (`UNVERIFIED` — whether any `marlo-data` code ever needed it; nothing references it there
today).

---

## Proposed Outcome

- No project menu entry, screen, Struts route, validator branch, summary, Pentaho report or i18n key for the
  COVID-19 impact section exists.
- The three specificities and the two permissions are gone from `parameters`, `custom_parameters`, `permissions`,
  `role_permissions` and `center_role_permissions`, so a new Global Unit no longer inherits them.
- Project completeness is unchanged for every active unit, because the section already counts as complete for
  them (`crp_covid_required = false`).
- The 9 CCAFS rows in `project_impacts` are **kept**, together with both tables (decision Q-1).

---

## Scope

Inventory produced by a whole-repo search for `ProjectImpacts|project_impacts|ImpactCovid19|covid19|IMPACTS|
crp_covid_required` over `marlo-*/src`, `report-orchestrator`, `docs/trd` and `reports/ai-context`, plus the
database FK, routine and view catalogue. Nothing was found in tests, `report-orchestrator`, the TRD, the ai-context
runbooks, or any stored routine or view.

| Layer | Items |
|---|---|
| **Web actions** | `ProjectImpactsAction`, `ImpactCovid19SummaryAction` |
| **Validation** | `ProjectImpactsValidator`; `ProjectSectionValidator.validateProjectImpactCovid()` and its injected manager and validator; `BaseValidator.saveMissingFields(ProjectImpacts, …)`; the two `IMPACTS` cases in `ValidateProjectSectionAction` |
| **Persistence (marlo-data)** | `ProjectImpacts`, `ProjectImpactsCategories`, `ReportProjectImpactsCovid19DTO`; their DAOs, MySQL DAOs, managers and manager impls; `SectionStatus.projectImpact` and `getSectionStatusByProjectImpacts()` in the DAO and manager; `ProjectImpacts.hbm.xml`, `ProjectImpactsCategories.hbm.xml`, the `projectImpact` mapping in `SectionStatuses.hbm.xml`, two entries in `hibernate.cfg.xml` |
| **Enum and constants** | `ProjectSectionStatusEnum.IMPACTS`; `Permission.PROJECT_COVID19_*`; `CRP_COVID_REQUIRED`, `CRP_SHOW_SECTION_IMPACT_COVID19`, `CRP_SHOW_SECTION_IMPACT_COVID19_RANGES_YEARS` in both `APConstants`; `BaseAction.isYearToShowSectionCovid19()` |
| **Views and assets** | `projectImpacts.ftl`, `crp/js/projects/projectImpacts.js`, the `covid19` entry in `menu-projects.ftl`, the card in `boardSummaries.ftl`, `pentaho/crp/ImpactCovid19.prpt` |
| **Routing** | `{crp}/impacts` and `{crp}/impactCovid19Summary` in `struts-projects.xml` |
| **i18n** | The section's keys in `global.properties` and in `aicrra`, `aiccra3`, `pabra`, `alliance`, `test` `.properties` — exact key list to be enumerated at specify time |
| **Data (one safe migration)** | `role_permissions` and `center_role_permissions` rows for the two permissions → `permissions` rows → `custom_parameters` → `parameters` for the three keys; plus `section_statuses` rows for `impacts` (0 locally, guarded for other environments) |

---

## Non-Goals

- **The other COVID-19 fields.** `project_expected_study_info.has_covid_analysis` (`studiesTemplates.ftl`) and
  `report_synthesis_flagship_progress.relevance_covid` (`AR2018_flagshipProgress.ftl`) belong to Expected Studies
  and the 2018 Annual Report. They are separate features with their own data and are not touched.
- **Dropping the `project_impacts` and `project_impacts_categories` tables** in this spec — see Recommended
  Approach; the drop is a deliberate follow-up.
- **The `marlo-web/bin/` and `marlo-data/bin/` build leftovers** that also contain copies of these files. They are
  IDE output, not source.
- Any change to how other sections compute completeness.

---

## Affected Users, Systems, And Specs

| Affected | How |
|---|---|
| AICCRA, AICCRA_III, TEST users | No visible change — the section is already hidden for them |
| Superadmin → Parameters | Three specificities disappear from the list |
| New Global Units | Stop inheriting 14 dormant role grants and three dormant specificities |
| `docs/specs/domain/projects/` | Checked: `agent-context.md` does not mention the section, so no update is expected |
| `reports/ai-context/struts-critical-routing-catalog.md`, `save-validation-matrix.md` | Checked: neither names the section today, so no update is expected |
| AICCRA FSRP database | Not visible from here — the migration must be safe there regardless of what it holds |

---

## Visual Reference

- Source: **None**
- Notes: a removal. The only UI effect is a menu entry and a summary card that are already hidden for every active
  Global Unit.

---

## Requirement Delta Preview

### REMOVED Requirements

- A project can have a COVID-19 impact section, shown by year range and optionally required for completeness.
- A COVID-19 impact summary can be downloaded from the summaries board.
- Roles can be granted `crp:{0}:project:{1}:impacts[:canEdit]`.

### MODIFIED Requirements

- Creating a Global Unit no longer clones grants for the removed permissions (a consequence of deleting them, not a
  change to `GlobalUnitCreationManagerImpl`).

### ADDED Requirements

- none

---

## Approach Options

| Option | What it does | Pros | Cons |
|---|---|---|---|
| **A. Switch it off only** | Migration sets the three specificities to `false` / empty everywhere | Tiny, zero code risk | The ~40 files, the permissions, the report and the grant cloning all stay; nothing is actually retired |
| **B. Retire code and configuration, keep the two tables** | Delete all code in Scope; one DML-only migration removes permissions, grants, specificities and `impacts` section statuses | Every consumer is gone; the migration is all-or-nothing under Flyway's transaction; the 9 CCAFS rows survive untouched for a later decision | Two orphan tables and one unused nullable FK column (`section_statuses.project_impact_id`) remain until the follow-up |
| **C. Retire everything, drop the tables** | B plus `DROP TABLE` and dropping the FK column | Leaves nothing behind | MySQL DDL auto-commits, so a DML + DDL migration can land half applied; destroys the only historical data before anyone has decided to |

---

## Recommended Approach

**Option B**, in two tasks:

1. **Code removal**, ordered from the edges inwards so each step compiles: views and routes → actions and summary →
   validators and the `IMPACTS` enum cases → marlo-data model, DAO, manager and Hibernate mappings → constants.
2. **One safe data migration**, following the project rule for migrations:
   - rows matched by key or permission string, never by id (`562`/`563` are local ids only);
   - DML only, child rows before parents (`role_permissions` / `center_role_permissions` → `permissions`;
     `custom_parameters` → `parameters`);
   - idempotent; tested twice on a throwaway copy of the touched tables, including a synthetic row for FSRP.

The table drop becomes a separate, later change once the CCAFS data decision is made. It is the smallest path that
retires every behavior, keeps the migration atomic, and does not destroy data nobody has signed off on.

**Depth:** `/akili-specify` **Lite** — the work is mechanical deletion with one migration, no new behavior.

---

## Risks, Dependencies, And Open Questions

| # | Item | Mitigation / owner |
|---|---|---|
| R-1 | **An enumeration that stops at the classes already named** leaves a live reference behind — the root cause behind `KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-1` | Specify re-runs the whole-repo search as a closing gate and records it; compile + Checkstyle catch Java, but not FTL, JS, `.properties` or `struts-projects.xml` |
| R-2 | Removing `ProjectSectionStatusEnum.IMPACTS` breaks any code that parses a stored `section_name = 'impacts'` into the enum | 0 such rows locally; the migration deletes them where they exist (FSRP) before the code stops knowing the value |
| R-3 | `SectionStatus.projectImpact` is mapped to `section_statuses.project_impact_id`, which has an FK to `project_impacts` | Unmapping a nullable column is safe for Hibernate; the column and FK stay until the table-drop follow-up |
| R-4 | A role in FSRP could hold the permissions under a different id | Match by `permissions.permission` string, not id |
| R-5 | `center_role_permissions` also references `permissions` | 0 rows on these permissions in all 4 local DBs; still included in the delete order for other environments |
| Q-1 | ~~What happens to the 9 CCAFS rows?~~ **Resolved 2026-09-29: keep them.** `project_impacts` and `project_impacts_categories` stay with their data; this spec does not touch them | Kenji Tanaka |
| Q-2 | Does AICCRA FSRP have any row in `project_impacts`? | Cannot be seen from here — **user** or whoever has FSRP access |
| Q-3 | Should the `CRP_COVID_REQUIRED` rule-4 breach be recorded anywhere, given the constant disappears? | Resolved by deletion; noted for the audit trail only |

---

## Success Criteria

- The closing whole-repo search for the Scope patterns returns only migrations and this spec.
- Clean recompile of `marlo-data` + `marlo-web` succeeds; Checkstyle delta is zero; the Java hygiene check is clean.
- The migration, run twice on a throwaway copy, removes exactly the permission, grant, specificity and section-status
  rows it targets and nothing else, and changes 0 rows on the second run.
- For AICCRA, AICCRA_III and TEST, the project menu and the completeness of every project are identical before and
  after.

---

## Next Step

```text
/akili-specify changes/retire-covid19-impact-section
```
