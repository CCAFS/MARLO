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

### T03 — `Phase` type methods + equivalence — PASS

- **Date:** 2026-10-28
- **Final status:** PASS (attempt 1)
- **Requirements covered:**
  - FN-001, FN-003, FN-004;
  - FN-002 on the real data and the FN-002 rows of the test table;
  - INT-3 (`Phase.isReporting()` with a null description);
  - AC FN-001/002, including the real-data clause.
- **Effort / skills:** `high`; `tdd`, `marlo-verify`.
- **Isolation:** worktree. The Implementer found a stale base (`9a2cf412ef`) and ran `git reset --hard 87b57f4c20`
  before any work, as its brief required.

#### Attempt 1

- **Files changed:**
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/model/Phase.java` (+26; CRLF kept):
    - `isPlanningPhase()` = `APConstants.PLANNING.equals(description) && Boolean.FALSE.equals(upkeep)`;
    - `isProgressPhase()` = `Boolean.TRUE.equals(upkeep)`;
    - `isReportingPhase()` = `APConstants.REPORTING.equals(description)`;
    - `isReporting()` (still `Boolean`) delegates to `isReportingPhase()`.
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/data/model/PhaseTypeEquivalenceTest.java` (new, 15 tests, GPL
    header). Old answers come from the real `BaseAction` methods through `getActualPhase()`. New answers are
    pinned as DD-3 literals.
  - `BaseActionPhaseTypeCharacterizationTest.java`: only the `Phase.isReporting()` null-description row changes,
    from throws to `false` (INT-3). That is a consequence of the delegation T03 orders, though the file is not
    in T03's file list (Reviewer advisory). No other T02 expectation changed.
- **Mapping:** `Phases.hbm.xml` lists its properties explicitly (property access), so the new getters are
  unmapped. `@Expose` is field-level, so Gson is unaffected.
- **Evidence re-run (non-author, Leader-inline, in the worktree at `fc0777c253`):**
  - The diff covers 3 files.
  - Clean compile: 2415 + 1062 + 112 test sources.
  - `mvn -o -pl marlo-data,marlo-web -am test -Dtest=PhaseTypeEquivalenceTest,BaseActionPhaseTypeCharacterizationTest,BaseActionGlobalUnitRulesTest,ProjectActivitiesValidatorCancelledTest`
    → `Tests run: 43, Failures: 0, Errors: 0`.
  - SQL comparison (`cmp.sql`, SELECT only): old rules translated from the old bodies (`BINARY name IN ('POWB','AWPB')`,
    `upkeep = 1`, BINARY description) versus DD-3, with M1 simulated. Results per schema:

    | Schema | Rows | Differ after M1 | Differ on raw data (the `Planing` rows) | Null or empty fields |
    |---|---|---|---|---|
    | `aiccradb1`–`4` (each) | 338 | **0** | 6 | 0 |
    | `aiccradb_actsave` | 392 | **0** | 12 | 0 |

    The columns are NOT NULL, so the empty phase and null-field cases are covered by the unit test only.
  - Falsifier: the test's cycle (line 90) was changed to `!phase.isReportingPhase()`. Result:
    `Tests run: 15, Failures: 5`, for example `empty phase cycle vs isPlanningActive expected:<false> but was:<true>`
    and `POWB/Planing/0 planning cycle expected:<false> but was:<true>`. The edit was reverted.

  **VERIFIED.**
- **Red run:** no red was observed against a missing implementation, because the methods were written before
  the test ran. The falsifier is the only observed red.
- **Checkstyle:** delta 0 (HEAD 0, tree 0) on the 3 files.
- **Reviewer verdict:** PASS (`opus`, checklist mode). It checked:
  - the methods match DD-3 word for word and are null-safe;
  - the delegation changes only the null-description case;
  - expected values are DD-3 literals and old-code results, never values from the new methods;
  - the SQL translation of the old methods is faithful.

  ISSUES: none.
- **ADVISORY (recorded, no rework):**
  - SCOPE: the characterization-test edit sits outside T03's file list, but is necessary. It is recorded above.
  - RISK: serializing `Phase` through its getters (Struts JSON or Jackson) would now emit `planningPhase`,
    `progressPhase` and `reportingPhase`, as it already emits `reporting`. Grep for direct `Phase`
    serialization before T04. Forward pointer to T04.
  - READABILITY: once the DD-3 domain note is settled, add one comment in `Phase` pointing to it.
- **runtime events:** none.
- **spawns:**
  - implementer 41 calls, 124,724 tokens, ended complete;
  - reviewer 12 calls, 61,080 tokens, ended complete.

#### Spec tensions (raised at the T03 gate; the code follows DD-3, which matches today's old methods)

1. AC FN-003/004 says "each MUST return false" for `description = null`. DD-3 and today's `isProgressActive`
   give progress = true on `UpKeep/null/1`. The Reviewer classes this as a wording defect, with a design note:
   - DD-3 classifies by `upkeep` alone for progress. So `Reporting/1`, impossible today under OPS-003, would be
     both reporting and progress.
   - On `UpKeep/null/1` the new cycle is `true` where the old `isPlanningActive` is `false`.
   - Proposal: DD-3 names its domain as the OPS-003 combinations.
2. AC FN-003/004 says "each false" for `upkeep = null`. DD-3 and today's `isReportingActive` give
   `AR/Reporting/null` reporting = true. This is a wording defect.
3. FN-002 says "for every phase … plan = `isPOWB()`", but `isPOWB()` reads the name and DD-3 never does. The
   equivalence holds only where the name agrees with the type (P-2 / OPS-003). This is a wording defect; the
   AC already scopes it to the table plus the real data.

#### Forward pointers

- **T04:** grep for direct `Phase` bean serialization.
- **T04:** the `getCurrentCycle()` null branch is uncovered (from T02).
- **T04:** the empty-session row is a unit-test path (from T02).

#### Running totals vs budget (design §17)

- Tasks closed: 4 of 16 (T00–T03).
- LOC: 279 + 235 = 514 (budget T01–T03: 335). The overrun is in test LOC; the total of ~1,270 still holds a
  margin of about 760 for 12 tasks. Watch it at T05 and T06.
- Review rounds: 3.

### T04 — `BaseAction` same-named methods + delegation of the old ones — PASS

- **Date:** 2026-10-28
- **Final status:** PASS (attempt 1)
- **Requirements covered:** FN-002 (as amended), FN-003, FN-004, FN-009, INT-3 (`isUpKeepActive`), NF-001,
  NF-005 bullet 2.
- **Effort / skills:** `high`; `tdd`, `marlo-verify`.
- **Isolation:** worktree. The Implementer started at the stale base `9a2cf412ef` and reset it to `a97f428db4`
  before any work, as its brief required.

#### Decisions made

- 2026-10-28, at the pre-spawn gate (owner): the T04 *Disqualifier* in `tasks.md` was amended. The permitted T02
  changes are now:
  - the INT-3 `isUpKeepActive` throw rows;
  - five rows outside DD-3's domain: `POWB/Planing/0`, `POWB/null/0`, `null/Planning/0`, `UpKeep/Planning/null`,
    `UpKeep/null/1`.

  These rows change because the old plan methods read the name. This follows from the FN-002 amendment made at the
  T03 gate.

#### Attempt 1

- **Files changed:**
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/BaseAction.java` (LF, targeted edits):
    - new `isPlanningPhase()`, `isProgressPhase()` and `isReportingPhase()`, each `Phase p = getActualPhase(); return p != null && p.isXxxPhase();`;
    - the six old methods are `@Deprecated` and delegate: `isPOWB` / `isAWPBActive` → `isPlanningPhase()`;
      `isPlanningActive` → `isPlanningPhase() || isProgressPhase()`; `isProgressActive` / `isUpKeepActive` →
      `isProgressPhase()`; `isReportingActive` → `isReportingPhase()`;
    - `getCurrentCycle()` is now `isReportingPhase() ? APConstants.REPORTING : APConstants.PLANNING`, and can no
      longer return null.
  - `BaseActionPhaseTypeCharacterizationTest.java`:
    - the permitted cells changed: `isUpKeepActive` throw → false on the empty phase, no session, empty session,
      null actual phase and `UpKeep/Planning/null`, plus the five out-of-domain rows;
    - three literal-table tests added for the new methods;
    - the `assertThrows` helper was removed and the null-actual-phase fixture extracted.
- **Implementer verification:** compile 2415 + 1062 + 112; full suite 1137 / 0 / 0; Checkstyle on BaseAction
  HEAD 9 = tree 9 (delta 0), test file 0.
- **Evidence re-run (non-author, Leader-inline, in the worktree at `2415b74203`):**
  - The diff covers 2 files.
  - Clean compile: 2415 + 1062 + 112.
  - `mvn -o -pl marlo-web -am test`: surefire totals **run 1137, fail 0, err 0, skip 0** (108 reports).
  - 0 CR characters in the diff.
  - Falsifier: `isPlanningActive()` was changed to `return this.isPlanningPhase();`. Result: `Tests run: 17, Failures: 3`,
    namely `UpKeep/null/1 isPlanningActive expected:<true> but was:<false>`,
    `UpKeep/Planning/1 isPlanningActive …` and `Progress/Planning/1 isPlanningActive …`. The edit was reverted.

  **VERIFIED.**
- **Reviewer verdict:** PASS (`opus`, checklist mode, weighted to reliability and risk). It checked:
  - every changed T02 cell against the amended Disqualifier, with nothing else changed;
  - the expectations are literals;
  - null safety under P-1;
  - FN-009's literal strings;
  - NF-005 bullet 2;
  - that no call site, field or constant was touched;
  - that no FTL/OGNL clash exists on `planningPhase` / `progressPhase` / `reportingPhase`.

  ISSUES: none.
- **Forward-pointer findings:**
  1. **`Phase` serialization.**
     - Gson in `getPhasesJson()` serializes fields, and REST has no `ResponseEntity<Phase>`.
     - The Implementer's claim that about 140 Struts JSON results serialize BaseAction getters was **refuted by
       the Reviewer**. P-12 holds: `ignoreHierarchy` is not set anywhere, so the default `true` applies, and
       BaseAction getters never reach a JSON payload. No change needed.
  2. **`getCurrentCycle()`** cannot return null any more. The old `catch → null` branch was dead code.
  3. **The empty-session row** is a unit-test path. It is not treated as the production path.
- **ADVISORY (recorded, no rework):**
  - READABILITY: the class Javadoc of `BaseActionPhaseTypeCharacterizationTest` (lines 33-35) still says "as they
    answer today". Three test names contradict their rows after T04:
    - `aNullNameIsNoPlanNameButKeepsItsDescriptionAndUpkeep`;
    - `aNullDescriptionIsNeitherPlanningNorReportingButKeepsItsNameAndUpkeep`;
    - `aMisspelledPlaningDescriptionIsNotAPlanningPhaseButStillAPlanningCycle`.
  - RISK: the out-of-domain flips are safe only because M1 (`2441a8e0f6`) ships with this code. The release notes
    must keep M1 and T04 in the same deploy (PR 1 before or with PR 2).
- **runtime events:** none.
- **spawns:**
  - implementer 57 calls, 145,107 tokens, ended complete;
  - reviewer 22 calls, 68,906 tokens, ended complete.

#### Running totals vs budget (design §17)

- Tasks closed: 5 of 16 (T00–T04).
- LOC: 514 + 202 = 716 (budget T01–T04: 395; total ~1,270). The test LOC overrun continues: about 554 remain for
  T05–T15, against a sum of ~875 for those tasks. **The total tripwire is likely to fire around T06**, so it is
  raised at this gate.
- Review rounds: 4 (budget ~24).
- **Budget decision at the T04 gate:** the owner answered "Si" to a question that offered two options for the
  projected overrun: raise the budget to ~1,600, or stop when it is actually exceeded. The answer does not pick one,
  so the Leader keeps the conservative default: the budget is **not** raised, and the tripwire stops the run when
  the actual total exceeds ~1,270 LOC.

### T12 — Name phases by type at global-unit creation — PASS

- **Date:** 2026-10-28
- **Final status:** PASS (attempt 1). It ran in parallel with T05, in separate worktrees with disjoint files.
- **Requirements covered:** FN-007, INT-1 (creation part); AC FN-007 (as amended).
- **Effort / skills:** `high`; `tdd`, `marlo-verify`.
- **Isolation:** worktree. It started at the stale base `9a2cf412ef` and was reset to `1aeb58a763`.

#### Decisions made

- 2026-10-28, at the pre-spawn gate (owner, option "Confiar en M1"): creation follows DD-3 and does not tolerate the
  typo. A pre-M1 `Planing` template phase is an unknown type and is copied unchanged. The AC for FN-007 in
  `requirements.md` and the *Tests* line of T12 in `tasks.md` were amended to say so.

#### Attempt 1

- **Files changed:**
  - `marlo-data/.../manager/impl/GlobalUnitCreationManagerImpl.java` (+19, LF): `createPhases` calls the new private
    `resolvePhaseName(phase)` after description and upkeep are set. It returns `APConstants.PLANNING`, a private
    `PROGRESS_PHASE_NAME` ("Progress") or `APConstants.REPORTING` by the `Phase` type methods; any other phase keeps
    its trimmed copied name.
  - `marlo-web/.../action/superadmin/GlobalUnitCreateAction.java` (+19, LF): the new private `applyPhaseTypeOfName`
    runs at the end of `parsePhaseLine`. The names `Planning`, `Progress` and `Reporting` become
    `Planning/false`, `Planning/true` and `Reporting/false`. Every other name keeps today's parsing.
  - `marlo-web/src/test/.../data/manager/impl/GlobalUnitCreationPhaseNamingTest.java` (new, 5 tests, GPL header,
    Proxy doubles that record what `savePhase` and `createGlobalUnit` receive, literal expectations).

    | Case | Input | Expected |
    |---|---|---|
    | AICCRA, after M1 | `AWPB/Planning/0`, `Progress/Planning/1`, `AR/Reporting/0` | Planning, Progress, Reporting |
    | AICCRA_III | `POWB/Planning/0`, `UpKeep/Planning/1`, `AR/Reporting/0` | Planning, Progress, Reporting |
    | unknown type | `"  Pilot "/Pilot/0`, `Special/Other/0` | `Pilot`, `Special` |
    | pre-M1 | `AWPB/Planing/0`, `AR/Reporting/0` | `AWPB` (unchanged, `Planing` kept), `Reporting` |
    | text path | the lines `Planning`, `Progress`, `Reporting`, `Pilot` | `Planning/Planning/false`, `Progress/Planning/true`, `Reporting/Reporting/false`, `Pilot/Pilot/false` |
- **Red before implementation (assertion level):** for example
  `expected:<[Planning, Progress, Reporting]> but was:<[AWPB, Progress, AR]>`.
- **Evidence re-run (non-author, Leader-inline, in the worktree at `ca236e29c9`):**
  - the diff covers 3 files;
  - clean compile 2415 + 1062 + 113;
  - full suite: surefire **run 1142, fail 0, err 0, skip 0**;
  - falsifier: manager line 549 changed to `phase.setName(phase.getName())` gives `Tests run: 5, Failures: 3`:
    `expected:<[Planning, Progress, Reporting]> but was:<[AWPB, Progress, AR]>`, `expected:<[AWPB, Reporting]> but was:<[AWPB, AR]>` and
    `expected:<[Planning, Progress, Reporting]> but was:<[POWB, UpKeep, AR]>`. Reverted.

  **VERIFIED.**
- **Checkstyle:** delta 0 (HEAD 3 = tree 3; the three long lines already existed).
- **Not Done / Assumptions (verbatim from the Implementer):**
  - "In the text path, the name decides the type even when an explicit third column is present
    (`Progress|2027|Planning` gives `Planning/true`). The brief left this open."
  - "For input with upkeep true and description `Reporting` (outside the allowed domain, DD-3), the manager names
    the phase `Progress` because the check order is Planning, Progress, Reporting."

  These are assumptions, not owed items, so no continuation was spawned.
- **Reviewer verdict:** PASS (`opus`, checklist mode). Its rulings:
  - **Assumption 1 is a spec gap, for the owner to decide.** `tasks.md` says to derive the type "for the names".
    Honouring the third column instead would contradict itself, because the manager would then rename
    `Planning|2027|Reporting` to `Reporting`. Raised at the T12 gate.
  - **Assumption 2 is in scope.** It sits outside DD-3's domain and cannot be reached: on the text path, upkeep is
    true only with `Planning`, and the template path has no `Reporting/1` row (T00, T03).
  - A `Progress` line on the text path changing from `Progress/Progress/0` to `Progress/Planning/1` is approved
    (tasks.md T12 files; DD-8 "both paths"; INT-1).
- **ADVISORY:**
  - READABILITY: `"Progress"` is written in two places (the manager constant and the action literal). A shared
    `APConstants` constant would be a follow-up and is out of scope here.
  - RELIABILITY: the text-path match is case-sensitive, so `progress|2027` stays an unknown type, as today. The
    screen help could say that case matters.
- **runtime events:** none.
- **spawns:**
  - implementer 30 calls, 140,237 tokens, ended partial (assumptions only);
  - reviewer 8 calls, 56,585 tokens, ended complete.

### T05 — Name-literal sites (§B, Java) with truth tables — PASS

- **Date:** 2026-10-28
- **Final status:** PASS (attempt 1). It ran in parallel with T12.
- **Requirements covered:**
  - FN-005 (Java name literals outside `rest/`);
  - NF-005 with its AC, including the BUT clause;
  - INT-3 as a class of change: the summary sites and the six year filters on the empty phase.
- **Effort / skills:** `xhigh` (save pipeline); `tdd`, `error-handling-patterns`, `marlo-verify`.
- **Review mode:** two Reviewers in parallel, one per lens, because the effort was `xhigh` and the save pipeline is
  touched: (a) risk and reliability, (b) readability and resilience with test validity.
- **Isolation:** worktree. It started at the stale base `9a2cf412ef` and was reset to `1aeb58a763`.

#### Attempt 1

- **Files changed (9):**
  - `ProjectOutcomeValidator.java:191-194` (CRLF kept): becomes `action.isProgressPhase() || action.isReportingPhase()`.
  - `OutcomesMilestonesAction.java` loop: becomes `(current.isProgressPhase() || current.isReportingPhase())`. The `current != null`
    guard and the year clause are kept.
  - `FundingSourcesSummaryAction.java` 498, 697, 1004 and `ProjectsFundingSourcesSummaryAction.java` 611: become
    `getSelectedPhase().isProgressPhase()`.
  - `ExpectedDeliverablesSummaryAction.java` 352-356: becomes `getSelectedPhase() != null && getSelectedPhase().isProgressPhase()`.
  - `BaseAction.java`, the six year-filter sites: become `c.getPhase().isReportingPhase()`. The removed constant was `"AR"`.
  - `PROJECT_INDICATOR_PHASE_PREVIOUS_NAME` is removed from both `APConstants.java` files. `POWB_ACRONYM` is kept (P-9).
  - `PhaseNameSiteTruthTableTest.java` (new, 9 tests): the old expressions are copied with `file:line @9e2a9ed838`
    and evaluated on POWB, AWPB, UpKeep, Progress, AR, the empty phase and a null phase, with literal expectations.
    It runs the real code for the year methods, `validateProjectMilestone` and `getNextPOWBMilestone`.
- **§B inventory:** the pre-edit counts match `call-sites.md`. After the edit, 6 sites remain:
  - the `projectContributionCrp.ftl:539` comment;
  - `crpPhases.ftl:227`;
  - `deliverableMacros.ftl:367, 411` (owned by T08);
  - the `POWB_ACRONYM` declaration and its file-name use.
- **Red before the edit:** 4 of 9 tests failed on behavioral assertions.
- **Evidence re-run (non-author, Leader-inline, in the worktree at `a51a7a0343`):**
  - the diff covers 9 files;
  - clean compile 2415 + 1062 + 113;
  - full suite: surefire **run 1146, fail 0, err 0, skip 0**;
  - `grep PROJECT_INDICATOR_PHASE_PREVIOUS_NAME` over marlo-web, marlo-data and marlo-utils finds 0;
  - falsifier: `ProjectOutcomeValidator` line 193 changed to `!action.isPlanningPhase()` gives
    `ProjectOutcomeValidator:191-194 NEW expression on empty phase expected:<NO> but was:<YES>` and the same on the
    null phase. Reverted.

  **VERIFIED.**
- **Checkstyle:** 18 → 18, delta 0.
- **Reviewer (a), risk and reliability:** PASS. It checked:
  - no validator, save-pipeline or report result changes on a real phase;
  - operator precedence and parentheses are preserved;
  - every INT-3 case is a real INT-3 case: only the in-memory empty phase reached it, and the only catcher logged
    the error and returned an empty list;
  - both `APConstants` copies are in sync;
  - the remaining `"AR"` literals are all in `rest/`.

  ISSUES: none.
- **Reviewer (b), test validity:** PASS. It checked:
  - each copied old expression is faithful to the removed lines;
  - every site has an empty and a null row;
  - no expected value echoes the code under test;
  - the proxies assert their arguments.

  ISSUES: none.
- **Recorded gap (Reviewer b; the owner sees it at the gate):** the summary sites are proven by an expression truth
  table plus a match against the shipped source. The report builders themselves are never executed.
- **Not Done / Assumptions (Implementer, verbatim summary):**
  - `getNextPOWBMilestone` keeps two NPEs that exist today (`current.getNext()` can be null; the empty phase's
    null id is unboxed to `long`). The tests pin both.
  - A null `Phase` reference at the year infos and at the funding summaries still throws, as before.
  - Both are left alone on purpose: they are outside INT-3's list. **Follow-up candidate (bugfix), not in this spec.**
- **ADVISORY (recorded, no rework):**
  - RISK: INT-3's "Known sites" list in `requirements.md` does not name the six year filters or
    `getExpectedStudiesYears` (which now returns years instead of an empty list on a name-less info phase). Add
    them at archive.
  - RELIABILITY: the source-text test reads `src/main/java/...` relative to the working directory. It works under
    Surefire but fails from an IDE or the repo root (`System.getProperty("basedir")` would fix it).
  - RESILIENCE:
    - in the year-method first-filter tables, the empty row tells nothing, because year -1 filters the phase out
      whatever the type check answers; the second-filter table carries that row;
    - the source match is brittle against `this.` removal and would miss `"UpKeep".equals(...)`.
  - READABILITY: for `getExpectedStudiesYears`, a caught exception is shown as THROWS; a separate `SWALLOWED`
    outcome would be clearer.
- **runtime events:** none.
- **spawns:**
  - implementer 89 calls, 280,805 tokens, ended partial (assumptions only);
  - reviewer (a) 15 calls, 85,232 tokens, ended complete;
  - reviewer (b) 11 calls, 70,280 tokens, ended complete.

#### Running totals vs budget (design §17), after T05 and T12

- Tasks closed: 7 of 16 (T00–T05, T12).
- LOC: 716 + 676 (T05: +648 / −28) + 343 (T12) = **~1,735, against a total budget of ~1,270**. The budget for
  T05 + T12 was 270. Most of the overrun is tests: T05's truth-table test alone is 633 lines.
- Review rounds: 7 (budget ~24).
- **BUDGET TRIPWIRE FIRED.** The run stops and the overrun is escalated to the owner (Step 2.4). This is never
  `auto-approved`. Remaining tasks: T06–T11, T13–T15.
- **Owner decisions at the tripwire stop (2026-10-28):**
  - The LOC budget is raised to ~2,800 (`design.md` §17). Execution continues with T06.
  - T12 text path: the name decides the type even when the line carries an explicit third column. This is
    accepted as the owner's decision, closing the spec gap the Reviewer raised.
  - Commit T12 and T05 as separate commits.

### T06 — Java call sites of the old methods (§A1 Java) — PASS

- **Date:** 2026-10-28
- **Final status:** PASS (attempt 1).
- **Requirements covered:** FN-005 (Java part), NF-001, NF-005 (DD-11 rule 1 negations), INT-3
  (`CanEditPowbSynthesisInterceptor:112`).
- **Effort / skills:** default (`medium`); `marlo-verify`, `error-handling-patterns`.
- **Review mode:** full, one Reviewer (`opus`).
- **Isolation:** worktree. STEP 0 confirmed the base `6730a71f52`.

#### Attempt 1

- **Files changed (43, +280 / −144):** the 42 §A1 main-source files, each a 1:1 line replacement (no line-ending
  churn), plus `PhaseNameSiteTruthTableTest.java` (+136, 8 new tests). Mapping applied:
  - `isPOWB()`, `isAWPBActive()` → `isPlanningPhase()`; `!isPOWB()` → `!isPlanningPhase()` (DD-11 rule 1) at
    `DeliverableValidator:494, 806` and `ProjectExpectedStudiesValidator:755`;
  - `isProgressActive()`, `isUpKeepActive()` → `isProgressPhase()`, negations alike;
  - `isReportingActive()` → `isReportingPhase()`, negations alike;
  - `isPlanningActive() && !isUpKeepActive()` → `isPlanningPhase()` at `DeliverableAction:2087, 5594`,
    `DeliverableListAction:166, 300`, `DeliverableStatusAction:76`, `EditDeliverableInterceptor:350, 370`;
  - `CanEditPowbSynthesisInterceptor:112` (`isPlanningActive() && !getActualPhase().getUpkeep()`) → `isPlanningPhase()`;
  - `ValidateProjectSectionAction:406` (`isPlanningActive() && isUpKeepActive()`) → `isProgressPhase()`;
  - bare `isPlanningActive()` → `isPlanningPhase() || isProgressPhase()` (including `CanEditPowbSynthesisInterceptor:165`);
    `ProjectSubmissionAction:304` → `!(isPlanningPhase() || isProgressPhase())`; `ValidateProjectSectionAction:222`
    expanded in place, inner parentheses kept.
  - Five commented-out occurrences renamed so the word grep is clean (`MonitoringOutcomesListAction:177`,
    `BaseAction:5158`, `ValidateProjectSectionAction:587`, `ProjectExpectedStudiesValidator:711`,
    `ProjectOutcomeValidator:152`).
  - `getCurrentCycle` untouched: §A1 lists it only as an argument next to `isUpKeepActive()`.
- **Evidence re-run (non-author, Leader-inline, in the worktree at `0fd8a49423`):**
  - clean compile 2415 + 1062 + 114, Maven exit 0;
  - full suite (`-am`): surefire **run 1159, fail 0, err 0, skip 0** (1151 + 8);
  - grep of the six old names in `marlo-web/src/main/java` → only the 6 `BaseAction` declarations
    (6345, 7691, 7744, 7823, 8074, 8218);
  - `git diff --numstat`: every main-source file has equal added and removed counts.

  **VERIFIED.**
- **Checkstyle (Implementer):** 74 → 74, delta 0.
- **Falsifier (Implementer, observed):** `isPlanningActive() && !isUpKeepActive()` mapped to
  `isPlanningPhase() || isProgressPhase()` → the UpKeep and Progress rows went red (expected NO, was YES). Reverted.
- **Reviewer:** PASS. Every replacement equals the old expression on Planning/0, Planning/1 and Reporting/0; the only
  differences are INT-3 throws on the empty or null phase becoming `false`. Precedence correct at the two embedded
  expansions (:222, :304); no site reads a phase other than the actual one; the test's old side is a copy at
  `9e2a9ed838` and its new side runs the real `BaseAction` methods; no scope creep. ISSUES: none.
- **ADVISORY (recorded, no rework):**
  - RELIABILITY: the T06 truth tables evaluate written-out copies of the new expressions against the real
    `BaseAction` methods, but never read or run the production lines themselves. A later wrong mapping at one site
    would stay green. The class Javadoc (`PhaseNameSiteTruthTableTest.java:66-71`) claims real code or a
    shipped-source read. Fix later with a source-read pin (as T05 did) or narrow the Javadoc.
  - READABILITY: the `:222` table leaves out the `hasSpecificities(...)` term and tests only with it true; with it
    false both sides reduce to the cycle expression. A comment would make that explicit.
  - Existing Spanish comments in untouched lines of `DeliverableListAction`, `ProjectOutcomeAction` and others were
    left alone (outside scope).
- **runtime events:** none.
- **spawns:** implementer 37 calls, 150,066 tokens, ended complete; reviewer 11 calls, 112,760 tokens, ended complete.

#### Running totals vs budget (design §17), after T06

- Tasks closed: 8 of 16 (T00–T06, T12).
- LOC: ~1,735 + 424 (T06: +280 / −144) = **~2,159, against the raised budget of ~2,800**.
- Review rounds: 8.

### T07 — Rendered-HTML capture harness and baseline (NF-002) — PASS (checklist)

- **Date:** 2026-10-28
- **Final status:** PASS (attempt 1). Review mode per task: checklist (test tooling, not shipped; its falsifier is
  the gate). No Reviewer spawn owed.
- **Requirements covered:** NF-002, AC NF-001/002, AC FN-003/004 (page with no session / no global unit).
- **Coordination (OR-2, Concurrency):** the `cargo:run` in the main checkout (PID 77433) belonged to session
  `marlo-14` (staging `575a1c7aff`, QATEST testing, paused). Owner instruction: "paralo tu". The Leader notified
  `marlo-14`, then stopped it with SIGTERM.
- **Database isolation:** `marlo-dev.properties` points at `aiccradb_actsave`; starting the branch there would apply
  M1 to a real schema and break `marlo-14`'s staging build (unknown applied migration). The Leader copied
  `aiccradb_actsave` to the throwaway `zz_t07_actsave` (mysqldump | mysql, 99 s; 619 tables/views, 31 routines, 392
  phases on both sides), backed up `marlo-dev.properties` to the scratchpad and set `mysql.database=zz_t07_actsave`.
  The app started from this branch at `0fa05c4737` via `run-marlo-java17.sh`; Flyway applied only
  `2.6.0.20261028.1817 FixPlaningPhaseDescription` to `zz_t07_actsave`. Check afterwards: `aiccradb_actsave` still has
  12 `Planing` rows and max migration `2.6.0.20261022.1141`.
- **Isolation:** main checkout (the running app serves it). No worktree.

#### Attempt 1

- **Files (new, tooling):** `harness/capture.sh` (64 lines; GET-only page views after one login POST per CRP; refuses
  a non-localhost base; reads `~/.marlo-local-credentials`, never writes them), `harness/diff.sh`, `harness/pages.txt`
  (page list), `harness/README.md` (run, diff, normalization table). Captured HTML lives only in the scratchpad
  (`html-baseline/run1` is the baseline) and is never committed.
- **Reference set (42 pages, all HTTP 200):**
  - AICCRA (45), phases 429 plan / 430 progress / 431 reporting: dashboard (main menu, guide button), projects list,
    description (102086), deliverable (24753, with participants), innovation (1626), budget by partners,
    project contributions, and the study list — 24 pages;
  - AICCRA_III (47), phases 444 / 445 / 446: dashboard, projects list, description, budget by partners,
    contributions on project 102093 — 15 pages (no deliverables, innovations or studies exist there);
  - expected-study form: RTB phase 262 (no AICCRA study rows in the 2026 phases) — 1 page;
  - no global unit: anonymous `login.do` and logged-in `glossary.do` — 2 pages.
- **Normalizations:** N1 `jsessionid`; N2 cache-bust params; N3 CSRF tokens (none present, kept per task); N4 sorted
  Global Unit switcher entries; N5 per-request CSP nonce; N6 line sort on the 40 non-NOGU pages (Hibernate
  collections render in unstable order); N7 collection indexes `[n]` / `_n"` on sorted pages; N8 the duplicated
  alliance-lever id on innovation 1626 / phase 431.
- **Null test (Implementer):** `diff.sh run1 run2` … `run1 run5` → `IDENTICAL: 43 files compared` each.
- **Falsifier (Implementer, observed):** the deployed `guide-button.ftl:24` changed `reportingActive` →
  `planningActive` (source tree untouched) → `diff.sh run1 fal1` named the 15 AICCRA_III pages plus the manifest.
  Reverted (`cmp` against the backup matched); `diff.sh run1 fal2` → `IDENTICAL: 43 files compared`.
- **Evidence re-run (non-author, Leader-inline):** a fresh capture `leader-rerun` → exit 0, all 42 pages 200,
  `diff.sh run1 leader-rerun` → `IDENTICAL: 43 files compared`. `git status` shows only the new `harness/`.

  **VERIFIED.**
- **Checklist:** GET-only after login ✓; localhost guard ✓; credentials not written ✓; baseline stable across 6
  captures ✓; falsifier observed ✓; run coordinated with other sessions ✓; captures not committed ✓.
- **Known limits (recorded for T08–T10):**
  - N6 hides a pure reordering on the 40 sorted pages; a boolean rename in FTL changes content, not order.
  - The falsifier change did not alter the AICCRA pages (only AICCRA_III), presumably because AICCRA renders the
    guide-button branch differently with both phase types open; not confirmed. Batch diffs on AICCRA pages are
    still content diffs, so a changed rendering would show.
  - AICCRA_III carries no deliverable, innovation or study pages; those are covered on AICCRA only.
  - The app stays up on `zz_t07_actsave` for T08–T10. `marlo-dev.properties` must be restored from the scratchpad
    backup and `marlo-14` told when the FTL batches finish.
- **runtime events:** first copy attempt failed (`sed: RE error: illegal byte sequence`), retried with `LC_ALL=C`.
- **spawns:** implementer 64 calls, 155,420 tokens, ended complete.

#### Running totals vs budget (design §17), after T07

- Tasks closed: 9 of 16 (T00–T07, T12).
- LOC: ~2,159 + ~159 (harness, tooling) = **~2,318, against ~2,800**.

### T08 — FTL batch 1: `global/macros` (incl. §A2 renames and INT-2) — PASS

- **Date:** 2026-10-28
- **Final status:** PASS (attempt 1), with one open item (INT-2 not observable locally).
- **Requirements covered:** FN-005 (part), FN-008 (code part), INT-2, INT-3 (`deliverableMacros`), NF-001, NF-002.
- **Review mode:** full, one Reviewer (`opus`).
- **Isolation:** main checkout; the app from T07 stays up on `zz_t07_actsave`. Edited source FTL was copied to the
  deployed exploded dir (`marlo-web/target/cargo/configurations/tomcat9x/webapps/marlo-web/`) so the running app
  renders it without a rebuild.

#### Attempt 1

- **Files changed (8 FTL, all LF, every file equal +/− counts):** `global/macros/studiesTemplates.ftl` (42),
  `innovationTemplates.ftl` (17), `deliverableListTemplate.ftl` (10), `deliverableMacros.ftl` (9),
  `homeDashboard.ftl` (8), `forms.ftl` (2); §A2 callers/locals `crp/views/projects/deliverableList.ftl` (3) and
  `projectInnovation.ftl` (1).
- **Mapping:** `reportingActive` → `reportingPhase` (36); `action.isPOWB()` / `action.isAWPBActive()` →
  `action.isPlanningPhase()`, negations per DD-11 rule 1 (`forms.ftl:1256-1257`, `studiesTemplates`); §A2 local
  `isProgressActive` → `isProgressPhase` (`innovationTemplates.ftl:136` + 16 reads, `projectInnovation.ftl:113`);
  macro parameter `isReportingActive` → `isReportingPhase` in the 7 definitions, the reads at
  `deliverableListTemplate.ftl:14, 121, 280, 350` and the callers `deliverableList.ftl:188, 191, 226`;
  `deliverableMacros.ftl:367, 411` `actualPhase.name != 'Progress'` → `!progressPhase` (FJ-3, INT-2/INT-3).
- **Evidence re-run (non-author, Leader-inline):**
  - grep of every old name and `actualPhase.name` in `global/macros` → 0;
  - deployed copies `cmp`-equal to the source for all 8 files;
  - fresh T07-set capture vs baseline → `IDENTICAL: 43 files compared`;
  - 9 extra list pages (AICCRA `deliverableList` on 429/430/431; AICCRA_III `deliverableList` and `innovationsList`
    on 444/445/446; script and list in the scratchpad `h-extra/`), added because the T07 set renders none of the
    list macros: re-capture identical to the Implementer's before-capture (original FTL deployed); all 200.

  **VERIFIED.**
- **Falsifier (Implementer, observed):** parameter renamed in the definition only (`deliverableListTemplate.ftl:4`,
  deployed copy) → the T07 set stayed identical (no page renders that macro), the 6 `deliverableList` pages went red
  (sizes e.g. 516,867 → 109,909 bytes on AICCRA progress). Reverted; `cmp` equal to source.
- **Reviewer:** PASS. Equivalence on Planning/0, Planning/1, Reporting/0, empty and null phase (the old methods
  already delegate); `!progressPhase` reads only `upkeep`, so AICCRA ("Progress"-named) pages do not change and only
  an `UpKeep`-named progress phase changes (INT-2), still behind the `deliverable_shared_clusters_trainees_active`
  specificity; precedence unchanged; every macro caller in the FTL tree updated; no FTL assign shadows the new
  property names; scope matches §A2. ISSUES: none.
- **Open item (INT-2 unobserved):** all 103 `UpKeep`-named progress phases in the local copy have 0 deliverables, so
  the lock/label/note removal cannot be seen in an HTML diff. It is proven by code reading only. The covering check is
  D8: a human look at an AICCRA_III progress deliverable page at the T15 HITL pause, on a throwaway DB.
- **ADVISORY (recorded):**
  - the 3 `deliverableList.ftl` callers still pass the value `=reportingActive` (§A2 gives the full form
    `isReportingPhase=reportingPhase`); T09 (`crp/views/projects`) must convert those three values.
  - Fold the 9 `h-extra` list pages into `harness/pages.txt` (with a fresh baseline) so T09/T10 diffs cover the list
    macros by default.
- **runtime events:** none.
- **spawns:** implementer 20 calls, 112,856 tokens, ended complete; reviewer 20 calls, 101,267 tokens, ended complete.

#### Running totals vs budget (design §17), after T08

- Tasks closed: 10 of 16 (T00–T08, T12).
- LOC: ~2,318 + 184 (T08: +92 / −92) = **~2,502, against ~2,800**.

### T09 — FTL batch 2: `crp/views/projects` — PASS

- **Date:** 2026-10-28
- **Final status:** PASS (attempt 1).
- **Requirements covered:** FN-005 (part), NF-001, NF-002; INT-3 (`menu-projects.ftl:221`).
- **Review mode:** full, one Reviewer (`opus`).
- **Isolation:** main checkout; app on `zz_t07_actsave`; source FTL copied to the deployed dir.

#### Attempt 1

- **Harness extension (tooling):** `harness/pages.txt` 42 → 114 pages: the 9 list pages from T08 plus 63 section
  pages (partners, activities, locations, ccafsOutcomes, outcomes, otherContributions, highlights, outputs,
  budgetByFlagship, contributionCrp) for AICCRA and AICCRA_III in the three phases. No new normalization. New pre-edit
  baseline `t09-base`; the first capture was unstable because the first GET of a progress-phase `contributionCrp`
  page creates the missing milestone row (existing action behaviour, on the throwaway schema only); bases 2–4 are
  byte-identical (`IDENTICAL: 115 files compared`). The README records the side effect.
- **Files changed (21 FTL, +120 / −121):** every §A1 row for `crp/views/projects`. `reportingActive` →
  `reportingPhase` (incl. negations, DD-11 rule 1); `menu-projects.ftl:57, 73, 75, 221` `upKeepActive`/`UpKeepActive`
  → `progressPhase`; `projectDeliverable.ftl:467` → `reportingPhase || progressPhase`; `projectContributionCrp.ftl`
  `:200` `!action.isPOWB()` → `!action.isPlanningPhase()`, `:366` → `action.isReportingPhase()`, `:559` →
  `action.isProgressPhase()`, `:560-561` `isAwpbCycle = isAiccraRules && planningPhase` with the dead
  `isPlanningCycle` local removed (it was itself an §A1 row); `deliverableList.ftl:188, 191, 226` caller values →
  `=reportingPhase` (closes the T08 advisory). CRLF kept in `projectContributionsCrpList.ftl`.
- **Evidence re-run (non-author, Leader-inline):**
  - old-name grep over `crp/views/projects` → 0 (Implementer: 120 before);
  - only `projectContributionCrp.ftl` has unequal +/− (one deleted line); deployed copies `cmp`-equal source;
  - fresh 114-page capture vs `t09-base` → `IDENTICAL: 115 files compared`, all 200;
  - `t09-base` equals the T07 baseline byte for byte on the 42 shared pages.

  **VERIFIED.**
- **Falsifier (Implementer, observed):** deployed `projectLocations.ftl:42` `[#if !reportingPhase]` →
  `[#if reportingPhase]` → a 31-line block disappeared from the AICCRA plan locations page and appeared on the
  reporting one. Reverted; recapture identical; `cmp` equal.
- **Reviewer:** PASS. Each change is a rename to the method the old one already delegates to; `:560` equals
  `planningPhase` on every input (a plan phase is never a progress phase under DD-3); all 121 §A1 rows migrated
  (incl. `projectInnovation.ftl:113` in T08); the §B comment at `projectContributionCrp.ftl:539` stays by design;
  `actualPhase.upkeep` reads (`deliverableList.ftl:121`, `projectDeliverable.ftl:280, 299`) are correctly out of
  scope (not in §5/§A1/§B; FN-005 forbids only name literals); `menu-projects.ftl:221` is a listed INT-3 case already
  effective since T04; no shadowing; harness GET-only apart from the recorded side effect. ISSUES: none.
- **ADVISORY:** README wording (GET-only claim, AICCRA_III coverage) — fixed by the Leader in the README before commit.
  Local `isUpkeepCycle` (`projectContributionCrp.ftl:559`) could become `isProgressCycle` in a later cleanup.
  Evidence reach: no captured page was confirmed to render `projectBudgetByCoAs.ftl`, `projectOverviewByMogs.ftl`,
  `projectStudies.ftl` or `deliverableInfo.ftl`; those rest on the delegation-identity argument.
- **runtime events:** none.
- **spawns:** implementer 42 calls, 156,719 tokens, ended complete; reviewer 24 calls, 114,755 tokens, ended complete.

#### Running totals vs budget (design §17), after T09

- Tasks closed: 11 of 16 (T00–T09, T12).
- LOC: ~2,502 + 241 (FTL) + ~80 (harness) = **~2,823, at the ~2,800 budget**. Remaining: T10 (M), T11, T13, T14 (S),
  T15 (HITL).
- **BUDGET TRIPWIRE FIRED (second time).** Escalated to the owner at the T09 gate. Owner decision (2026-10-28): LOC
  budget raised to ~3,500 (`design.md` §17); commit T09 and continue with T10.

### T10 — FTL batch 3: remaining FTL (`global/pages`, home, publications, annual report, POWB, admin) — PASS

- **Date:** 2026-10-28
- **Final status:** PASS (attempt 1).
- **Requirements covered:** FN-005 (FTL part complete), NF-001, NF-002.
- **Review mode:** full, one Reviewer (`opus`).
- **Isolation:** main checkout; app on `zz_t07_actsave`; source FTL copied to the deployed dir.

#### Attempt 1

- **Harness extension:** `pages.txt` 114 → 186 (72 `Y_*` pages): publications list (AICCRA, AICCRA_III, three phases;
  RTB 262), the publication form (RTB 262), all 14 `annualReport` and 19 `annualReport2018` sections (RTB 262), all 11
  `powb` and 6 `powb2019` sections (RTB 260), and a few AICCRA/AICCRA_III annual report and POWB pages. RTB stands in
  where AICCRA answers an internal error or has no publication-type deliverable. Pre-edit `t10-base` vs `t10-base2` →
  `IDENTICAL: 187 files compared`.
- **Files changed (45 FTL, +55 / −55, all LF):** `guide-button.ftl:24, 222` → `reportingPhase`, `:91, 225`
  `POWB?exists && POWB` → `planningPhase`, `:153, 228` `UpKeepActive` → `progressPhase`; `javascript-constants.ftl:30-31`
  right-hand sides → `reportingPhase` / `progressPhase` (JS names kept); `main-menu.ftl:4, 48, 55, 61` (right-hand sides;
  `reportingActiveMenu` kept; `:55, 61` INT-3 behind `'visible': false &&`); `projectsListTemplate.ftl:12`
  `!reportingActive && !planningActive` → `!reportingPhase && !(planningPhase || progressPhase)`, `:174`;
  `publicationsList.ftl:24`, `publication.ftl:68`, `replicateButton.ftl:3`; the 14 `annualReport_*` + template and 19
  `AR2018_*` + template `[#if !reportingActive]`; `menu-annualReport.ftl:114`, `menu-AR2018.ftl:112` (inside a
  comment), `menu-powb.ftl:116`, `menu-powb2019.ftl:126`.
- **Evidence re-run (non-author, Leader-inline):**
  - whole `WEB-INF` FTL grep of the old tokens → only `javascript-constants.ftl:30-31` (`var reportingActive=`,
    `var upKeepActive=`: §A3 JS names); `POWB?exists` → 0; remaining bare `POWB` hits are titles and comments;
  - deployed copies `cmp`-equal source;
  - fresh 186-page capture vs `t10-base` → `IDENTICAL: 187 files compared`; `t10-base` equals `t09-base` on all
    shared pages.

  **VERIFIED.**
- **JS globals (Implementer):** plan false/false, progress false/true, reporting true/false, POWB page false/false,
  NOGU pages false/false — identical before and after.
- **Falsifier (Implementer, observed):** deployed `javascript-constants.ftl` `(reportingPhase)` → `(reportingPhas)` →
  AICCRA reporting 431 rendered `var reportingActive= false;` against the baseline's `true`. Restored; value back to
  `true`; `cmp` equal.
- **Reviewer:** PASS. Every swap matches a §5 row and the old methods are aliases of the new ones; the plan/progress
  PDF links at `guide-button.ftl:225-230` prove the capitalised `POWB` / `UpKeepActive` resolved to true before the
  change; JS contract kept (no `.js` in the diff); no shadowing; `crpPhases.ftl:227` (§B literal) untouched as
  required. ISSUES: none.
- **ADVISORY:**
  - README undercounted GET side effects: the annual report and POWB `prepare()` methods save a missing synthesis row
    on a GET (e.g. `CrpProgressAction.java:511`, `ToCAdjustmentsAction.java:359`). **Fixed by the Leader in the
    harness README before commit** (capture twice; never against a real schema).
  - Coverage gap (recorded, not counted as covered): no capture renders the true branch of `!reportingPhase` in the
    33 annual report section/template files (those pages answer not-found outside reporting). Proven by the
    delegation identity only. `menu-annualReport.ftl:114` is dead code (`false && …`); `menu-AR2018.ftl:112` is a
    comment.
- **runtime events:** the Implementer left two self-matching wait loops (`pgrep -f harness/capture.sh` found their own
  command lines) that kept re-waking the agent; the Leader killed them and stopped the agent after its final report.
- **spawns:** implementer 80 calls, ~168,671 tokens, ended complete (stopped by the Leader afterwards); reviewer 19
  calls, 100,361 tokens, ended complete.

#### Running totals vs budget (design §17), after T10

- Tasks closed: 12 of 16 (T00–T10, T12).
- LOC: ~2,823 + 110 (FTL) + ~85 (harness) = **~3,018, against ~3,500**.

#### Server-log check after T10 (owner question: "were all the exceptions the expected ones?")

The Leader classified every exception in the app log for T07–T10 (`scratchpad/t07_server.log`):

- **FreeMarker:** 6 errors `Macro "deliverablesList" has no parameter with name "isReportingPhase"` at a single
  minute — the T08 falsifier (parameter renamed to `isReportingZzz` in the deployed copy), as intended. All other
  template errors are data-driven and fire at the same count on every capture of the same page set, before and after
  the edits: `project.startDate` missing in `projectOutcomes.ftl:25` / `projectCcafsOutcomes.ftl:26` (12 per T09
  capture), and on the AR2018 pages a `?starts_with` on a null value plus one `OutcomesMilestonesAction.getOutcome`
  call with a null argument (14 per T10 capture: base, base2, new and the Leader re-run alike). None references a
  renamed name.
- **Java:** NPE in `DeliverableAction` sort comparators (`:1985`, `:2017`, null institution), NPE in
  `EditProjectOutcomeInterceptor:168, 402`, `ArrayIndexOutOfBoundsException` in `EditProjectInterceptor:127` (no
  `projectID` parameter), `InaccessibleObjectException` in `AutoSaveReader` (Java 17 reflection), and the
  `UnhandledExceptionAction` error pages (the AICCRA annual report sections). None of those lines was changed on this
  branch, and no stack frame passes through `BaseAction.is*Phase` or `Phase.is*Phase`.
- **Conclusion:** no exception was introduced by T06–T10. The pre-existing ones are out of scope (bugfix candidates).

### Run paused (2026-10-28, owner request after T10)

- Next eligible task: **T11** (remove the old methods, fields and setters; Checkstyle rule). It needs Maven, so run it
  in a worktree. Then T14 (docs), T13 (M2; needs OQ2/OQ3 answered), T15 (manual QA HITL).
- Local environment left by this run: `marlo-dev.properties` restored to `aiccradb_actsave` (backup of the T07 state
  in the scratchpad); the throwaway schema `zz_t07_actsave` is kept for T15 and the harness; the app started at T07
  may still be running on port 8080 against `zz_t07_actsave` until someone restarts it.
