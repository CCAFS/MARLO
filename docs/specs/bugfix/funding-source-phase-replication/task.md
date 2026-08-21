# Funding Sources — Phase Replication Blocks Save — Tasks

**Spec ID:** BUG-FUNDINGSOURCE-SAVE-001
**Status:** In Progress
**Owner:** IBD Team — Kenji Tanaka
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-08-20

---

## 1. Execution Context

- Java 17, run script `scripts/run-marlo-java17.sh`.
- Java level verified in `marlo-parent/pom.xml` (`<java.version>17</java.version>`).
- Spring profile: local `marlo-<profile>.properties` bootstrapped from `marlo-test.properties`.
- Build used for verification: `mvn -o -pl marlo-web -am -DskipTests compile`.
- `mvn checkstyle:check` cannot run in this checkout: `maven-checkstyle-plugin:2.9.1` fails with
  `NoSuchMethodError: Checker.setClassloader` against checkstyle 8.18 on Java 17, on untouched modules too. Style
  verified manually (2-space indent, no line above 120 chars added).

## 2. Pre-flight Checklist

- [x] `requirements.md` written.
- [x] `design.md` written.
- [ ] Both approved by a reviewer.
- [x] Latest `staging` pulled.
- [ ] Feature branch `fix/funding-source-phase-replication` created from `staging` — deferred: the owner asked to
      test on `staging` first. MUST be done before the PR (constitutional rule 9).

## 3. Task List

### BUG-FUNDINGSOURCE-SAVE-001-T01 — Set the modification justification on replicated info rows

- **Depends on:** —
- **Module:** marlo-data
- **Files touched:**
  - `data/manager/impl/FundingSourceInfoManagerImpl.java` (modified)
- **Constitutional checks:**
  - Layered pattern preserved: the change stays inside the existing ManagerImpl.
  - Forward-only replication unchanged: same PLANNING guard, same recursion over `phase.getNext()`.
  - No new Java file, so no GPL header needed.
- **Tests:**
  - Manual: save a funding source whose next phase has no `funding_sources_info` row and an end date set.
  - Manual: save a funding source that already has rows in every phase (regression, update branch).
- **Done when:**
  - `mvn -o -pl marlo-data -am -DskipTests compile` succeeds. ✔ 2026-08-20
  - No `PropertyValueException` in the log for the first case.
- **Verification:**
  - `SELECT id, id_phase, modification_justification FROM funding_sources_info WHERE funding_source_id = <FS>;`
    shows one row per editable phase, all with a non-blank justification.

**Status:** done (pending runtime verification).

### BUG-FUNDINGSOURCE-SAVE-001-T02 — Fix the null-check precedence in validate()

- **Depends on:** —
- **Module:** marlo-web
- **Files touched:**
  - `action/funding/FundingSourceAction.java` (modified; `FileDB` import added)
- **Constitutional checks:**
  - `Action.validate()` keeps its `if (save)` guard and still delegates to `FundingSourceValidator`.
- **Tests:**
  - Manual: save a funding source with no contract file attached.
  - Manual: on a CRP with `crp_has_research_human`, save with "does research with human subjects" = No.
- **Done when:**
  - `mvn -o -pl marlo-web -am -DskipTests compile` succeeds. ✔ 2026-08-20
- **Verification:**
  - No NullPointerException from `FundingSourceAction.validate` in the log.

**Status:** done (pending runtime verification).

### BUG-FUNDINGSOURCE-SAVE-001-T03 — Create the current-phase info row instead of moving the previous one

- **Depends on:** T01
- **Module:** marlo-web
- **Files touched:**
  - `action/funding/FundingSourceAction.java` (modified: `previousPhaseInfo` field, snapshot in `prepare()`,
    resolution and restore in `save()`, managed funding source on the info row)
- **Constitutional checks:**
  - Hard rule 1 honored: the previous phase row keeps its stored values and its own phase.
  - Save pipeline pattern untouched: `validate()` guarded by `save`, then `Validator`, then the manager chain.
- **Tests:**
  - Manual: funding source with no info row in the current phase — edit the title and save.
  - Manual: funding source with an info row in the current phase — regression, values still saved once.
  - Manual: autosave draft present (`isAutoSave`) — save still works.
- **Done when:**
  - The new row exists for the current phase with the submitted values.
  - The previous phase row is byte-identical to its pre-save state except for audit history entries.
- **Verification:**
  - Compare `funding_sources_info` for the funding source before and after the save, per phase.

**Status:** done (pending runtime verification).

### BUG-FUNDINGSOURCE-SAVE-001-T04 — Runtime verification against the acceptance criteria

- **Depends on:** T01, T02, T03
- **Module:** —
- **Files touched:** none
- **Constitutional checks:** none.
- **Tests:** AC-001 to AC-005 of `requirements.md`.
- **Done when:** every AC passes on a local run with a copy of production-like data.
- **Verification:** `scripts/run-marlo-java17.sh`, then the SQL checks of T01 and T03.

**Status:** pending.

### BUG-FUNDINGSOURCE-SAVE-001-T05 — Update the ai-context replication runbook

- **Depends on:** T04
- **Module:** —
- **Files touched:**
  - `reports/ai-context/persistence-replication-managerimpl.md` (modified)
- **Constitutional checks:** ai-context docs updated when a replication contract changes.
- **Tests:** none.
- **Done when:** the runbook states that any row created by a phase-replication path must carry
  `modificationJustification`, and names the entities whose mapping enforces it.
- **Verification:** review by a second reader.

**Status:** pending.

### BUG-FUNDINGSOURCE-SAVE-001-T06 — Assess the same gap in Projects and Deliverables

- **Depends on:** T04
- **Module:** marlo-data (investigation only)
- **Files touched:** none in this task.
- **Constitutional checks:** none.
- **Tests:** none.
- **Done when:** a decision is recorded on whether `ProjectInfoManagerImpl.saveInfoPhase()` and
  `DeliverableInfoManagerImpl.saveInfoPhase()` need the same guard. Both create rows without a justification into
  columns that are `NOT NULL` in the DDL; they survive today only because their hbm mappings do not declare
  `not-null` and MySQL is not in strict mode.
- **Verification:** `SELECT @@sql_mode;` on each environment, recorded in the Decision Log.

**Status:** pending.

## 4. Dependency Graph

```
T01 ──┬─> T03 ──┬─> T04 ──┬─> T05
      │         │         └─> T06
T02 ──┴─────────┘
```

T01 and T02 are independent of each other. T03 depends on T01 because the row it creates is inserted through the
replication path fixed in T01.

## 5. Testing Plan

- **Unit:** none added. The module has no test harness for these managers today, and adding one requires a Hibernate
  test fixture that does not exist in `marlo-data`. Recorded as debt in the Decision Log.
- **Integration:** manual through the Struts action, since the failure only reproduces with the full request
  transaction of `MARLOCustomPersistFilter`.
- **Regression:** funding source with rows in every phase; funding source with project budgets; synced funding
  source; funding source with an autosave draft; duplicate (`copy`) flow; delete flow.
- **Manual:** AC-001 to AC-005.

## 6. Operational Steps

- No migration, no configuration change, no environment variable change.
- No BI or AI service coordination needed.
- After deploy, watch the log for `PropertyValueException` and for
  "Exception occurred when trying to commit transaction" on `fundingSource.do`.

## 7. Rollback Plan

Revert the two Java files and redeploy. No data undo is needed: the `funding_sources_info` rows created by the fix
are ordinary rows that the previous code reads through the update branch of `saveInfoPhase()`.

## 8. Definition of Done

- [ ] AC-001 to AC-005 verified on a local run.
- [x] `marlo-data` and `marlo-web` compile.
- [ ] Regression list of §5 walked through.
- [ ] `reports/ai-context/persistence-replication-managerimpl.md` updated.
- [ ] Change moved to a feature branch from `staging` and merged into `staging` after review.
- [ ] Decision recorded for T06.
