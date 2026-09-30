# Retire The COVID-19 Impact Section — Requirements

**Spec ID:** CHG-RETIRE-COVID19
**Status:** Approved (2026-09-29)
**Owner:** Kenji Tanaka
**Reviewers:** Tech lead
**Last Updated:** 2026-09-29
**Related PRD sections:** not applicable — removes a feature, adds none
**Related UX/UI Design sections:** docs/ux-ui/design.md §4 *Screen Inventory* — line 161 names the retired view and is removed by this spec (user decision 2026-09-29)
**Related TRD sections:** not applicable — `grep -n -i "covid\|impacts" docs/trd/trd.md` returns no hit for this section
**Companion ai-context docs:** none name the section (`grep -rn -i "covid\|projectImpacts" reports/ai-context` → 0 hits)
**Proposal:** [`proposal.md`](./proposal.md) — Option B approved 2026-09-29, CCAFS rows kept
**Depth:** Standard (re-checked against the design; see `design.md` → *Budget*)

---

## 1. Overview

Remove the project COVID-19 impact section — screen, report, validation, permissions and specificities — while
keeping its two data tables and their 9 historical CCAFS rows. The section is hidden in every active Global Unit
and has had no writes since 2020-06-26.

## 2. Problem Statement

The feature still runs on every project menu build, every project validation, and every Global Unit creation, which
clones its two permissions into 14 role grants for a screen the new unit cannot see. It is carried code with no
user. Evidence is in [`proposal.md`](./proposal.md) → *Problem / Current Behavior*, each line cited there.

## 3. In-Scope Requirements

### Functional

| ID | Requirement |
|---|---|
| CHG-RETIRE-COVID19-FN-001 | The project menu, the project section validation and project completeness MUST NOT offer, run or count a COVID-19 impact section in any Global Unit. |
| CHG-RETIRE-COVID19-FN-002 | The summaries board MUST NOT offer a COVID-19 impact report, and no route MUST serve one. |
| CHG-RETIRE-COVID19-FN-003 | No route MUST serve the COVID-19 impact screen; a request to the retired path MUST get the framework's standard "no action mapped" response, never a server error. |
| CHG-RETIRE-COVID19-FN-004 | For every other project section, menu entries, validation results and completeness MUST be identical before and after. |

### Non-Functional

| ID | Requirement |
|---|---|
| CHG-RETIRE-COVID19-NF-001 | After the change, no source file outside `database/migrations/` and this spec folder MAY reference the retired feature (patterns in *Acceptance Criteria*). |
| CHG-RETIRE-COVID19-NF-002 | The build MUST compile cleanly and add no Checkstyle violation. |

### Data

| ID | Requirement |
|---|---|
| CHG-RETIRE-COVID19-DA-001 | The specificities `crp_show_section_impact_covid19`, `crp_show_section_impact_covid19_ranges_years` and `crp_covid_required` MUST no longer exist in `parameters` or `custom_parameters`. |
| CHG-RETIRE-COVID19-DA-002 | The permissions `crp:{0}:project:{1}:impacts` and `crp:{0}:project:{1}:impacts:canEdit` MUST no longer exist, nor any role grant of them, so a new Global Unit cannot inherit them. |
| CHG-RETIRE-COVID19-DA-003 | Section statuses recorded for the `impacts` section MUST be removed. |
| CHG-RETIRE-COVID19-DA-004 | `project_impacts` and `project_impacts_categories` MUST keep every row they hold today. |

### Migration

| ID | Requirement |
|---|---|
| CHG-RETIRE-COVID19-MIG-001 | The data change MUST be safe in any database: no assumed id, idempotent, DML only, child rows before parents. |

### UI / Security

- **UI:** covered by FN-001 and FN-002; nothing is added.
- **Security:** covered by DA-002 — fewer permissions, no new one.

## 4. Out-of-Scope

- `project_expected_study_info.has_covid_analysis` and `report_synthesis_flagship_progress.relevance_covid`, with
  their i18n keys (`study.general.covidAnalysis*`, `annualReport2018.flagshipProgress.*Covid*`). Separate features.
- Dropping `project_impacts`, `project_impacts_categories`, or `section_statuses.project_impact_id` and its FK.
- `marlo-web/bin/` and `marlo-data/bin/` IDE output.

## 5. Personas Affected

| Persona | Effect |
|---|---|
| Project leaders and coordinators in AICCRA, AICCRA_III, TEST | None visible — the section is already hidden for them |
| Superadmin | Three specificities disappear from Superadmin → Parameters; two permissions disappear from role configuration |

## 6. Acceptance Criteria

### Requirement: The section is gone from the project (FN-001, FN-004)

#### Scenario: Project menu of an active unit

- GIVEN a project of AICCRA, AICCRA_III or TEST
- WHEN its menu is rendered
- THEN no COVID-19 impact entry is present
- AND every other entry, and its completeness check, is the same as before the change
- BUT it must NOT change the completeness of any project

#### Scenario: A stale section name reaches the validator

- GIVEN a request to validate the section `impacts` (a cached page, a hand-typed URL)
- WHEN the section validation handles it
- THEN it is treated as an unknown section, the same way any other unknown name is
- AND IT MUST NOT raise a server error

### Requirement: No report and no screen (FN-002, FN-003)

#### Scenario: Summaries board

- GIVEN any Global Unit, AICCRA excluded or not
- WHEN the summaries board is rendered
- THEN no COVID-19 impact report card is offered

#### Scenario: Retired routes

- GIVEN the paths `{crp}/impacts` and `{crp}/impactCovid19Summary`
- WHEN either is requested
- THEN the framework answers as for any unmapped action
- BUT it must NOT return a 500 or a stack trace

### Requirement: Nothing references the feature (NF-001, NF-002)

#### Scenario: Closing sweep

- GIVEN the finished change
- WHEN the whole repository is searched for `ProjectImpacts|projectImpacts|ImpactCovid19|impactCovid19|Covid19|
  covid19|COVID19|ProjectSectionStatusEnum\.IMPACTS|case IMPACTS|crp_covid_required|CRP_COVID_REQUIRED|
  PROJECT_COVID19|project_impact` outside `target/`, `bin/`, `.git/` and `database/migrations/`
- THEN the only hits are in this spec folder and in the out-of-scope features listed in §4
- AND IT MUST be run and quoted in the task that closes the spec, with the exact command

### Requirement: Configuration is removed, history is kept (DA-001 … DA-004, MIG-001)

#### Scenario: First run of the migration

- GIVEN a database holding the three specificities, the two permissions with their grants, and `impacts` section
  statuses
- WHEN the migration runs
- THEN those rows are gone
- AND every row of `project_impacts` and `project_impacts_categories` is still there
- AND IT MUST match rows by key, permission string or section name — never by numeric id

#### Scenario: Second run, or a database without the rows

- GIVEN the migration already applied, or a database that never had these rows
- WHEN it runs
- THEN it changes 0 rows and succeeds
- BUT it must NOT delete a parameter or permission whose key merely starts with a retired key

## 7. Defect Classes And Their Gates

| Defect class this spec can produce | Gate that catches it | Automated? |
|---|---|---|
| A Java reference left to a deleted class or method | Clean recompile of `marlo-data` + `marlo-web` (`marlo-verify` Gate 1) | Yes |
| A reference left in FTL, JS, `.properties` or `struts-*.xml` — **the compiler cannot see these** | The closing whole-repo sweep of §6 | Yes, as a command whose output is quoted |
| An i18n key deleted that another screen still uses | Per-key whole-repo grep before deletion, including dynamic prefixes (`breadCrumb.menu.${label}`) | Yes, per key |
| A migration that deletes too much, too little, or fails on a second run | Two runs on a throwaway copy of the touched tables, with a decoy key and a synthetic foreign row | Yes |
| A migration that fails in AICCRA FSRP | No database access from here — covered only by the no-assumed-id rule and the synthetic row | **No — accepted risk**, bounded by idempotency: a failed run can be re-run |
| A change in menu or completeness for the remaining sections | Not automatable in this repository (no test covers the menu) | **No — human check** at the execute HITL pause: open one AICCRA project menu before and after |

## 8. Constitutional Compliance Checklist

- [x] Phased data forward-only — no phased data is written.
- [x] Save pipeline unchanged for every surviving section.
- [x] Spring MVC `/api/*` untouched.
- [x] Specificities: removed from **both** `APConstants`, and the three keys removed from `parameters`.
- [x] Schema and data changes ship as a Flyway migration with the real-clock filename.
- [x] No new Java file, so no new GPL header is needed.
- [x] Code style: Checkstyle delta must be zero (NF-002).
- [x] English only.
- [x] Branch: `staging`, as chosen by the user.
- [x] No dependency version change.
- [x] No credential file touched.

## 9. Open Questions

| # | Question | Status |
|---|---|---|
| OQ-1 | What happens to the CCAFS rows? | **Resolved 2026-09-29 — kept** (DA-004) |
| OQ-2 | Does AICCRA FSRP hold rows in `project_impacts` or grants on the two permissions? | Open, non-blocking: the migration is safe either way, and DA-004 keeps its data |

## 10. Decision Log

- 2026-09-29 — Retire code and configuration, keep the two tables — Rationale: every behavior goes, the migration
  stays DML-only and atomic, and no historical data is destroyed without a decision (proposal Option B).
- 2026-09-29 — Keep the 9 CCAFS rows — Rationale: user decision; the table drop is a later, separate change.
- 2026-09-29 — Remove the `projectImpacts.ftl` line from `docs/ux-ui/design.md` as this spec's own deliverable — Rationale: a constitutional
  document naming a screen that no longer exists; the edit is one line, approved by the user after Judgment Day JD-2.
- 2026-09-29 — Depth raised from Lite to Standard — Rationale: the design resolves to five tasks across two modules
  plus a migration, which is above what Lite describes.
