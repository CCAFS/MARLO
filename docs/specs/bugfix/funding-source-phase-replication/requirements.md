# Funding Sources — Phase Replication Blocks Save — Requirements

**Spec ID:** BUG-FUNDINGSOURCE-SAVE-001
**Status:** Draft
**Owner:** IBD Team — Kenji Tanaka
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-08-20
**Related PRD sections:** docs/prd.md — funding source management
**Related System Design sections:** docs/ux-ui/design.md — Funding Sources screens
**Related Detailed Design sections:** docs/trd/trd.md §3 (data model), §5 (save pipeline)
**Companion ai-context docs:** reports/ai-context/persistence-replication-managerimpl.md, reports/ai-context/save-validation-matrix.md

---

## 1. Overview

Saving a funding source silently discards every change for part of the funding source population. The report from
the field was "the Funding Sources section only saves when there is a project budget record for that funding
source". The project budget is not the gate: the gate is a funding source that has no `funding_sources_info` row in
one of the phases the save replicates to. Those funding sources are the ones nobody has mapped to a project, which
is why the failure looked correlated with `project_budgets`.

## 2. Problem Statement

`FundingSourceInfoManagerImpl.saveInfoPhase()` inserts a new `FundingSourceInfo` when a future phase has no row for
the funding source. That insert never carries `modificationJustification`, which `FundingSourceInfo.hbm.xml` maps as
`not-null="true"`, so Hibernate raises `PropertyValueException` before reaching MySQL. `MARLOCustomPersistFilter`
runs one transaction per request, so the exception rolls back everything the save wrote: info, budgets,
institutions, divisions and locations. The user is told nothing useful and concludes the section does not save.

A second defect compounds it: when the current phase has no info row, `FundingSource.getFundingSourceInfo(phase)`
falls back to the row of a previous phase, the form binds onto that row, and `save()` moves it forward with
`setPhase(actualPhase)`. That mutates a past phase, which the constitutional baseline declares immutable.

## 3. In-Scope Requirements

### Functional

- **BUG-FS-SAVE-F-001** — Saving a funding source MUST persist every edited field regardless of whether the funding
  source has `project_budgets` rows.
- **BUG-FS-SAVE-F-002** — Phase replication MUST be able to create a `funding_sources_info` row for a future phase
  without violating the `modification_justification` constraint.
- **BUG-FS-SAVE-F-003** — When the current phase has no `funding_sources_info` row, the save MUST create one for the
  current phase and MUST leave the previous phase row unchanged.
- **BUG-FS-SAVE-F-004** — `Action.validate()` MUST NOT raise a NullPointerException when the form does not send a
  file id for `file` or `fileResearch`.

### Non-Functional

- **BUG-FS-SAVE-N-001** — No additional queries per phase beyond the ones the current replication already issues.
- **BUG-FS-SAVE-N-002** — The audit log MUST keep the justification entered by the user on replicated rows.

### Data

- **BUG-FS-SAVE-D-001** — No schema change. Existing rows stay untouched; the missing rows are created by the first
  successful save of each funding source.

### UI

Not applicable — no view change in this spec. The funding window / budget type round-trip in
`fundingSource.ftl` is tracked separately (see Out-of-Scope).

### Security

Not applicable — no change to permissions or interceptor stacks.

## 4. Out-of-Scope

- The funding window (`budgetType`) round-trip: `canEditType()` plus `fundingSource.ftl:215` mean a synced funding
  source only submits the budget type when it has no project budgets, so `funding_sources_info.type` is wiped on
  save in the opposite case. Separate spec / PR.
- The budget type propagation to `project_budgets`, which only updates `projectBudgets.get(0)` and only for the
  phase year (`FundingSourceAction.save()`).
- A Flyway backfill of missing `funding_sources_info` rows for the editable phases. To be decided after measuring
  how many funding sources are affected.
- The same missing-justification pattern in `ProjectInfoManagerImpl` and `DeliverableInfoManagerImpl`. Those do not
  fail today because their hbm mappings do not declare the column not-null and MySQL is not in strict mode.

## 5. Personas Affected

- **PMU / Finance Manager** — primary victims: they edit funding sources and lose the changes with no error.
- **Cluster coordinator / Project leader** — they see stale funding source data mapped into their project budgets.
- **QA reviewer** — section status stays inconsistent with the values actually stored.

## 6. Acceptance Criteria

- **AC-001 (F-001, F-002)** — Given a funding source with no `funding_sources_info` row in the next phase and an end
  date set, when a PMU user edits the title and saves, then the change is stored, a row is created for every future
  phase, and no exception is logged.
- **AC-002 (F-001)** — Given a funding source with zero `project_budgets` rows, when any editable field is changed
  and saved, then the change is stored.
- **AC-003 (F-003)** — Given a funding source whose current phase has no info row, when the user saves, then a new
  row exists for the current phase with the submitted values and the previous phase row keeps its stored values and
  its own phase.
- **AC-004 (F-004)** — Given a funding source with no attached contract file, when the user saves, then validation
  completes and no NullPointerException is raised.
- **AC-005 (N-002)** — Given a user justification on the save, when the replication creates rows in future phases,
  then those rows carry that justification; when no justification is available, they carry a generated one.

## 7. Constitutional Compliance Checklist

- [x] Phase replication: covered in design.md; save path documented, delete path unchanged.
- [x] Save validation: `Action.validate()` + `FundingSourceValidator` + `editFSStack` identified, unchanged.
- [x] Permissions: no new action; `editFSStack` untouched.
- [x] Specificity: not applicable — no feature flag.
- [x] Migrations: not applicable — no schema change.
- [x] i18n: not applicable — no new user-facing string. The generated justification is an audit-log value, not UI.
- [x] License header: no new Java file.
- [x] Code style: 2-space indent and 120 char limit respected. Note: `mvn checkstyle:check` cannot run in this
      checkout (`maven-checkstyle-plugin:2.9.1` is incompatible with Java 17); verified manually.
- [x] REST: not applicable.
- [x] Audit: replicated rows keep the user justification, so `HibernateAuditLogListener` output stays meaningful.
- [x] Dependency floors: untouched.
- [ ] Branching: fix under test directly on `staging` at the owner's request; to be moved to a feature branch
      before the PR.

## 8. Open Questions

- How many funding sources are missing info rows in the editable phases? Decides whether the Flyway backfill of the
  Out-of-Scope list is needed.
- Should `ProjectInfo` / `DeliverableInfo` replication get the same guard now, before any move to MySQL strict mode?

## 9. Decision Log

- 2026-08-20 — Fix the replication insert instead of relaxing the `not-null` mapping — the column is a real audit
  requirement; dropping the constraint would hide the missing justification.
- 2026-08-20 — Create a row for the current phase instead of re-phasing the previous one — past phases are
  immutable (hard rule 1).
- 2026-08-20 — No Flyway backfill in this spec — the replication fix creates the missing rows on the first save;
  a backfill only shortens the wait and needs its own risk assessment.
