# Phase Type Unification and Cycle Names — Execution Log

## Document Control

| Item | Value |
|---|---|
| Spec | `docs/specs/enhancement/phase-type-unification` (ENH-PHASE-TYPE-UNIFICATION-001) |
| Branch | `phase-type-unification` |
| Approval Mode | gated |
| Started | 2026-10-28 |
| Leader | Claude Code session, `opus` (T1) |
| Implementer / Reviewer wrappers | `.claude/agents/akili-implementer.md` (`sonnet`) / `.claude/agents/akili-reviewer.md` (`opus`) |

### Run notes (2026-10-28)

- **Persona drift (pending item for `staging`).** `akili doctor --agents` reports all four `.agents/*.md`
  personas as `UNMARKED` (no version markers). `--fix` would rewrite shared files on a spec branch, which the
  shared-file rule forbids. Decision (owner asked for the Leader's recommendation): run with the current
  personas and apply `akili doctor --agents --fix` on `staging`.
- **Concurrency.** Another session holds a `cargo:run` (started by `run-marlo-java17.sh`) in this checkout.
  T02 and every later Maven task wait until it stops (owner's choice). T01 needs no Maven run.
- **T00** (production read-only check, OPS-003) is the owner's task. It is pending and must be done before T03.

## Task Execution History

### T01 — M1: fix the `Planing` typo — PASS

- **Date:** 2026-10-28
- **Final status:** PASS (attempt 1)
- **Requirements covered:** DA-001, MIG-001, MIG-002 (M1 column), OPS-004 (M1 part), INT-4.
- **Effort / skills:** `high` (migration on production data; the effort dial says `max`, but `max` is barred on
  T2, and the SQL is one text-match statement). Skills: `marlo-migration`. `marlo-commit` was dropped from the
  brief because the Leader commits.

#### Attempt 1

- **Files changed:** `marlo-web/src/main/resources/database/migrations/V2_6_0_20261028_1817__FixPlaningPhaseDescription.sql`
  (new; the name came from the `marlo-migration` script, using the real clock).
- **Statement:** `UPDATE phases SET description = 'Planning' WHERE description = 'Planing';`. The header states:
  no ids, DML only, the reason, the OPS-004 capture `SELECT id, description FROM phases WHERE description = 'Planing';`
  for the release notes, and that rollback must not go by text alone.
- **Implementer verification:** throwaway-schema harness `m1_harness.sh` (session scratchpad). It builds a
  `zz_m1_*` schema per scenario from the `aiccradb_actsave` structure and data, with both `phases` FKs
  re-added, and runs the migration file itself. Expected = `COUNT(*) WHERE description='Planing'` taken just
  before the run, on the same schema. Actual = `ROW_COUNT()`. It also checks other columns, collateral rows
  and leftover `Planing` rows, then drops every `zz_*` schema.

  ```
  1 real copy, first run             expected=12  actual=12  other_cols_changed=0 repaired_rows=12 planing_left=0 collateral=0  PASS
  2 same copy, second run            expected=0   actual=0   other_cols_changed=0 repaired_rows=0 planing_left=0 collateral=0  PASS
  3 no global unit, no phase         expected=0   actual=0   other_cols_changed=0 repaired_rows=0 planing_left=0 collateral=0  PASS
  4 global units, no phases          expected=0   actual=0   other_cols_changed=0 repaired_rows=0 planing_left=0 collateral=0  PASS
  5 no AICCRA                        expected=6   actual=6   other_cols_changed=0 repaired_rows=6 planing_left=0 collateral=0  PASS
  6 later unit also AICCRA           expected=12  actual=12  other_cols_changed=0 repaired_rows=12 planing_left=0 collateral=0  PASS
  7 orphan phase (unit NULL)         expected=1   actual=1   other_cols_changed=0 repaired_rows=1 planing_left=0 collateral=0  PASS
  8 inconsistent row (445 upkeep=0)  expected=12  actual=12  other_cols_changed=0 repaired_rows=12 planing_left=0 collateral=0  PASS
  9 extra: decoy 'Plan B' row        expected=12  actual=12  other_cols_changed=0 repaired_rows=12 planing_left=0 collateral=0  PASS
  remaining zz_ schemas: 0
  ```
- **No id literal:** `grep -nE "[0-9]{2,}"` finds hits only on header comment lines, and they are spec ids
  (`…-001-T01`, `DA-001`). The UPDATE contains no digit.
- **Falsifier (deviation, see Issues):**
  - The task's falsifier, `LIKE 'Plan%'` (scenario 1 actual exceeds expected), did **not** go red.
  - The author added scenario 9 (a decoy `Plan B` row) as a substitute. The `LIKE` variant then gives:
    `9 extra: decoy 'Plan B' row  expected=12  actual=13  other_cols_changed=0 repaired_rows=12 planing_left=0 collateral=1  FAIL`.
- **`marlo-migration` checklist (from the Implementer's report):**
  - no assumed ids;
  - idempotent (scenario 2);
  - DML only, with no FK column touched;
  - FK order and backfill: n/a;
  - tested on throwaway schemas (scenarios 2–5, 7);
  - the only environment assumption is that the `Planing` text exists; with 0 matching rows the run still succeeds;
  - collation caveat: `phases.description` is `utf8mb3_general_ci`, so the match is case- and trailing-space-insensitive. No such variant exists locally.
- **Evidence re-run (non-author, Leader-inline):** `m1_harness.sh` and `m1_harness.sh falsifier.sql` were
  re-executed and gave identical output. **VERIFIED.**
- **Reviewer verdict:** PASS (`opus`, checklist mode). It confirmed:
  - the harness executes the repository file (`cat "$MIG"` in the same session that reads `ROW_COUNT()`), not a copy;
  - the filename and version order: it is the newest `V2_6_0_*` file and no `V2_7+` exists;
  - the no-id claim;
  - the OPS-004 header;
  - the substitute falsifier is valid.
  
  ISSUES: none. ADVISORY: none (under 50 LOC).
- **runtime events:** none.
- **spawns:**
  - implementer 12 calls, 94,400 tokens, ended complete;
  - reviewer 10 calls, 52,959 tokens, ended complete.

#### Issues encountered

- **Spec tension, raised at the T01 gate.** The T01 falsifier in `tasks.md` cannot go red when measured with the
  mysql CLI. `ROW_COUNT()` counts *changed* rows: `Planning`→`Planning` changes nothing, and no real description
  other than `Planing` / `Planning` starts with `Plan`. The decoy-row scenario proves what the falsifier was
  meant to prove: the harness catches a WHERE clause that matches too much.
- **Reviewer note to carry.** Through Flyway (Connector/J, `useAffectedRows=false`), an UPDATE reports *matched*
  rows, not changed rows. So the update count in a release log is not comparable with the harness's `actual` if
  the WHERE ever widens.

#### Decisions made

- 2026-10-28 — Execute-time spec edit, approved by the owner at the T01 gate: the `tasks.md` T01 *Falsifier* and
  *Red run* were reworded to the decoy-row scenario, and the observed red was recorded. No requirement's meaning
  changed. The next task's Reviewer brief carries this as a named conformance check.

#### Running totals vs budget (design §17)

- Tasks closed: 1 of 16.
- LOC: 21 (budget for T01: 15; total ~1,270).
- Review rounds: 1 (budget ~24).
- Within the tripwire.

### T02 — Characterization tests of today's phase-type behavior — PASS

- **Date:** 2026-10-28
- **Final status:** PASS (attempt 1)
- **Requirements covered:** FN-002 and FN-009 (the old results are pinned); NF-001 baseline. INT-3 throw rows
  pinned as throws.
- **Effort / skills:** `high`. This pins the contract every later task relies on. Skills: `tdd`, `marlo-verify`.
- **Isolation:** the Implementer ran in a git worktree (`isolation: worktree`), because another session's
  `cargo:run` holds this checkout's `target/` (owner chose "seguir" at the T01 gate).

#### Attempt 1

- **Files changed:**
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/BaseActionPhaseTypeCharacterizationTest.java` (new, 14 tests, GPL header);
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/BaseActionGlobalUnitRulesTest.java`: the `isUpKeepActive` override is removed; the session carries a POWB/Planning/0 phase plus `setPhaseID`;
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/validation/projects/ProjectActivitiesValidatorCancelledTest.java`: the `isReportingActive` override is removed; an AR/Reporting or POWB/Planning phase is set from the `reporting` flag; the assertions are unchanged.
  - No `src/main` change.
- **Asserted table.** Every input resolves through the real `getActualPhase()`. Columns are `isPOWB` /
  `isAWPBActive` / `isPlanningActive` / `isProgressActive` / `isUpKeepActive` / `isReportingActive` /
  `getCurrentCycle`.

  | Input (name/description/upkeep) | Answers |
  |---|---|
  | POWB/Planning/0, AWPB/Planning/0 | T/T/T/F/F/F/Planning |
  | UpKeep/Planning/1, Progress/Planning/1 | F/F/T/T/T/F/Planning |
  | AR/Reporting/0 | F/F/F/F/F/T/Reporting |
  | POWB/Planing/0 | T/T/F/F/F/F/Planning |
  | empty phase | F/F/F/F/throws NPE/F/Planning |
  | null actual phase | F/F/F/F/throws NPE/F/Planning |
  | no / empty session | same as the empty phase |
  | UpKeep/Planning/null | F/F/T/F/throws NPE/F/Planning |
  | POWB/null/0 | T/T/F/F/F/F/Planning |
  | UpKeep/null/1 | F/F/F/T/T/F/Planning |
  | null/Planning/0 | F/F/T/F/F/F/Planning |
  | null/Reporting/0 | F/F/F/F/F/T/Reporting |

  `Phase.isReporting()` is pinned too: it throws an NPE on a null description. That goes beyond the brief, and
  INT-3 names it.
- **Implementer verification (deviation):** the worktree was created from `9a2cf412ef`, an older staging
  merge, not from `2441a8e0f6`. The Implementer's own compile, test and falsifier runs therefore covered
  different code (`BaseAction.java` differs by 15 lines) and are **not evidence for the spec base**. They
  reported: compile 2415 + 1064 + 110 files, 28 tests green, `isPOWB` flip red, Checkstyle delta 0.
- **Evidence re-run (non-author, Leader-inline), on the spec base.** The commit `75f5cda2f8` was cherry-picked
  onto `2441a8e0f6` in the worktree as `574f5d1cca` (branch `t02-on-spec`). The pick was clean and touched 3
  files only.
  - Clean compile: 2415 (marlo-data) + 1062 (marlo-web) + 111 test sources.
  - `mvn -o -pl marlo-data,marlo-web -am test -Dtest=BaseActionPhaseTypeCharacterizationTest,BaseActionGlobalUnitRulesTest,ProjectActivitiesValidatorCancelledTest`
    → `Tests run: 28, Failures: 0, Errors: 0, Skipped: 0`, BUILD SUCCESS.
  - Falsifier as specified: `isPOWB()` was made to also accept `"UpKeep"`, anchored to `BaseAction.java:7742`.
    It went red at assertion level, then the edit was reverted and the tree was clean:
    ```
    java.lang.AssertionError: UpKeep/Planning/null isPOWB expected:<false> but was:<true>
    java.lang.AssertionError: UpKeep/null/1 isPOWB expected:<false> but was:<true>
    java.lang.AssertionError: UpKeep/Planning/1 isPOWB expected:<false> but was:<true>
    ```
    An earlier unanchored edit hit `isAWPBActive()` instead (its body is identical) and also went red on its
    assertions.

  **VERIFIED.**
- **Reviewer verdict:** PASS (`opus`, checklist mode). It checked:
  - every expected value against the old bodies at `2441a8e0f6`;
  - that the doubles assert exactly what they asserted before;
  - that no fixture supplies the value its assertion checks;
  - the GPL header and code style.
- **ADVISORY (recorded, no rework):**
  - RELIABILITY: record the falsifier deviation as it happened. Done above, and the specified `isPOWB` flip was
    re-observed on the spec base.
  - READABILITY: the empty-session row reaches the empty phase only because `ActionContext.getContext()` is
    null in a unit test (`BaseAction:2161-2163` catch). With a real ActionContext the path differs. T03/T04
    should not read this row as the production path.
  - RELIABILITY: `getCurrentCycle()`'s `catch → null` branch is never exercised, because `isReportingActive()`
    cannot throw. T04 must not assume a null cycle is covered. Carry this into the T04 brief.
- **runtime events:** none.
- **spawns:**
  - implementer 36 calls, 125,735 tokens, ended complete (wrong worktree base; see the deviation above);
  - reviewer 13 calls, 63,799 tokens, ended complete.

#### Decisions made

- 2026-10-28 — The worktree-base deviation was resolved by cherry-picking onto the spec base and re-running all
  evidence there. No rework attempt was consumed: the code was correct, and only where it had been measured was
  wrong.
- **Forward pointers (copy them into the T04 brief):**
  - the `getCurrentCycle()` null branch is not covered;
  - the empty-session row is a unit-test path.
- **Process note:** a worktree-isolated Implementer must be told to verify `git log -1` equals the spec HEAD
  before working. Its base can differ from the Leader's HEAD.

#### Running totals vs budget (design §17)

- Tasks closed: 2 of 16.
- LOC: 21 + 258 = 279 (budget T01 + T02: 215). The overrun is in the test LOC. The design budget already noted
  that tests were undercounted. It is within the total tripwire.
- Review rounds: 2 (budget ~24).

### T00 — Production read-only checks before code (OPS-003) — PASS (owner exception)

- **Date:** 2026-10-28
- **Final status:** PASS by owner decision. The `tasks.md` T00 *Disqualifier* states that a local copy is not
  production evidence. On 2026-10-28 the owner ruled: "Hazlo localmente ya que es una copia de prod". The
  check therefore ran on the local copies, and this record keeps the exception visible.
- **Run by:** Leader-inline, read-only SELECTs only (Review: checklist).
- **MySQL version:** `8.0.43` (local server).
- **Freshness of each copy:**

  | Schema | Last Flyway install | Last auditlog |
  |---|---|---|
  | `aiccradb_actsave` | 2026-10-23 09:58 | 2026-10-27 14:54 |
  | `aiccradb1`, `aiccradb2` | 2026-10-09 16:07 | none |
  | `aiccradb3`, `aiccradb4` | 2026-06-25 08:38 | no auditlog table |

- **`SELECT description, upkeep, COUNT(*) FROM phases GROUP BY 1, 2`:**

  | Schema | Planing/0 | Planing/1 | Planning/0 | Planning/1 | Reporting/0 | Other |
  |---|---|---|---|---|---|---|
  | `aiccradb_actsave` | 6 | 6 | 128 | 115 | 137 | none |
  | `aiccradb1`–`4` | 3 | 3 | 113 | 100 | 119 | none |

- **`SELECT COUNT(*) FROM phases WHERE description = 'Planing'`:** 12 in `aiccradb_actsave`, 6 in each of
  `aiccradb1`–`4`.
- **Verification:** every combination is in {`Planning/0`, `Planning/1`, `Reporting/0`, `Planing/0`,
  `Planing/1`}. **Met.** The strict definitions (DD-3) stand. P-2 is settled for the copies the owner accepts
  as production.
- **Falsifier:** a combination outside the set would be listed in the "Other" column. None appeared.
- **Residual risk (carried to the release HITL):** production rows changed after 2026-10-27 are not covered.
  OQ4 part 2 (before M2) re-runs the production counts.
