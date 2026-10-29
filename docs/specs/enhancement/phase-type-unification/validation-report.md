# Validation Report — Phase Type Unification and Cycle Names

**Verdict: PASS WITH WARNINGS. 0 FAIL, 0 BLOCKED, 12 WARN, 45 PASS.** The code does what the spec asks. Compile,
the full test suite and the Checkstyle delta are green, re-run by the auditor. The phase type is answered only by
`Phase.is*Phase()` and the same-named `BaseAction` methods. The old methods are gone and Checkstyle blocks them. Both
migrations reproduce their expected counts on a throwaway schema. No WARN blocks archiving. Each one is a deferred
release-day item, an evidence gap the owner already saw at a gate, or spec text that is behind the code. All of them
need to be accepted, or turned into follow-ups, before `/akili-archive`.

---

## 1. Document Control

| Item | Value |
|---|---|
| Spec | `docs/specs/enhancement/phase-type-unification` (ENH-PHASE-TYPE-UNIFICATION-001) |
| Validated commit | `d50883cd86` on `phase-type-unification` |
| Base | `575a1c7aff` = `git merge-base origin/staging HEAD`, and also the current `origin/staging` HEAD |
| Spec range | 18 commits, `9e2a9ed838` to `d50883cd86` |
| Auditor | T3, `opus`. Independent: it wrote no code, and the Implementers were `sonnet` |
| Date | 2026-10-29 |
| Build location | Worktree `MARLO-val`, detached at `d50883cd86`. No Maven ran in the main checkout and no server was started |
| DB access | SELECT only on `aiccradb_actsave`, `aiccradb1` and `aiccradb3`. Writes went only to the throwaway `zz_val_m`, which was dropped (0 `zz_val%` schemas left) |
| `test-report.md` | **Absent.** `/akili-test` was not run, so coverage was verified directly from the tests and the `execution.md` evidence |
| Skills loaded | `marlo-verify`, `cognitive-doc-design` |

## 2. Summary

| Area | PASS | WARN | FAIL | BLOCKED |
|---|---|---|---|---|
| Task completion (§3) | 15 | 1 | 0 | 0 |
| File existence (§4) | 1 | 0 | 0 | 0 |
| Build integrity (§5) | 5 | 0 | 0 | 0 |
| Requirement coverage (§6) | 19 | 7 | 0 | 0 |
| Design conformance (§8) | 5 | 3 | 0 | 0 |
| Constitution impact (§10) | 0 | 1 | 0 | 0 |
| **Total** | **45** | **12** | **0** | **0** |

**What the auditor re-verified, rather than copied:**

| Claim in `execution.md` | Re-run result |
|---|---|
| Clean compile: 2415 + 1062 files | 2415 (marlo-data) + 1062 (marlo-web) + 114 test sources; Maven exit 0 |
| Full suite: 1159 / 0 | 110 surefire reports summed: run 1159, fail 0, err 0, skip 0; Maven exit 0 |
| Checkstyle delta 0 | 57 changed Java files: base 86 (base config), tree 86 (new config). Delta 0 and no per-file delta |
| Rule has 0 hits tree-wide | 0 `RegexpSinglelineJava` hits over 3,599 Java files. The same rule gives 150 hits on the base versions of the changed files, so the rule really runs |
| M1 / M2 counts 12 / 66, idempotent | `zz_val_m` built from `aiccradb_actsave`: M1 12 expected = 12 actual; M2 66 = 66; second runs 0 / 0; 0 other columns changed; 0 wrong names; AICCRA keeps `AR` / `AWPB` / `Progress` (12 each); AICCRA_III, TEST, KT and QATEST get `Planning` / `Progress` / `Reporting` (6 each) |
| AC FN-005 grep | Only the §A3 identifiers are left (§6) |
| Name agreement (P-2), including the old `containsIgnoreCase` loop | 0 rows on `aiccradb_actsave`, `aiccradb1` and `aiccradb3` whose name disagrees with its type |
| INT-2 plan-phase falsifier replaced (T15) | Confirmed: `projectDeliverable.ftl:280, 299` gate the tab on `(reportingPhase \|\| actualPhase.upkeep)`, and the participants macro is called at `:312` inside it |

## 3. Task Completion

All 16 tasks are `[x]`, each with an attempt record, a non-author evidence re-run and an observed falsifier in
`execution.md`.

| Task | Result | Note |
|---|---|---|
| T00 | **WARN** | Ran on local copies by owner exception ("Hazlo localmente ya que es una copia de prod"). The task's own Disqualifier says a local copy is not production evidence (W1) |
| T01–T14 | PASS | T01 falsifier reworded at its gate, approved by the owner; T11 PASS on attempt 2 |
| T15 | PASS | Owner-confirmed. The INT-2 plan-phase falsifier was replaced by the reporting phase; the substitution is valid (§6, FN-008) |

## 4. File Existence

**PASS.** 133 non-spec files changed, plus 4 harness files and 1 epic doc. Design §17 estimated ~135.

| Design §2 / §17 item | Found |
|---|---|
| `Phase.java`: three methods, `isReporting()` delegates | Yes, CRLF kept |
| `GlobalUnitCreationManagerImpl.createPhases`, `GlobalUnitCreateAction.parsePhaseLine` | Yes (`resolvePhaseName`, `applyPhaseTypeOfName`) |
| Both `APConstants`: `PROJECT_INDICATOR_PHASE_PREVIOUS_NAME` removed, `POWB_ACRONYM` kept | Yes; 0 references left |
| 42 §A1 Java + 5 §B Java + 4 extra = 51 main Java files | 51 |
| 73 §A1 FTL | 73 |
| 2 migrations, sorted after every migration on `origin/staging` | `V2_6_0_20261028_1817`, `V2_6_0_20261029_0834` |
| 4 new tests (GPL header) + 2 rewritten doubles | Yes; the GPL header is present on all 4 |
| `configuration/marlo-checkstyle.xml` rule | Yes |
| No `rest/`, `ManagerImpl` replication, or `crpPhases.ftl:227` change | Confirmed |
| No JS change | Confirmed |

## 5. Build Integrity

| Gate | Result | Evidence |
|---|---|---|
| Clean compile | PASS | `rm -rf …/target/classes …` then `mvn -o -pl marlo-data,marlo-web -am test-compile -DskipTests`: 2415 / 1062 / 114, exit 0 |
| Unit tests (`-am`) | PASS | `mvn -o -pl marlo-web -am test`: 1159 / 0 / 0 / 0. Spec tests: Characterization 17, Equivalence 15, TruthTable 17, CreationNaming 5, GlobalUnitRules 10, ActivitiesValidatorCancelled 4 |
| Checkstyle delta vs the merge base | PASS | Checkstyle 8.18 run directly; 86 → 86 over 57 files |
| NF-003 rule | PASS | 0 hits tree-wide; 150 hits on the base code |
| Hygiene | PASS | No EOL flip (`Phase.java` and `ProjectOutcomeValidator.java` stay CRLF); no id literal in either migration (the only digits are `upkeep` 0 / 1) |
| Environment boot smoke | Not run | Forbidden to the auditor (PID 47060 on 8080). The T15 boot on 8081 with Flyway applying M2 is the recorded evidence |

## 6. Requirement Coverage

### By requirement

| Req | Result | Owner task | Evidence |
|---|---|---|---|
| FN-001 | PASS | T03 | Three methods on `Phase`; no fourth "cycle" method |
| FN-002 (amended) | PASS | T02, T03, T04 | `PhaseTypeEquivalenceTest`; T03 SQL comparison 0 differences; auditor name-agreement check 0 |
| FN-003 | PASS | T03, T04 | Empty-phase rows false on all four |
| FN-004 | PASS | T03, T04 | Null-field rows do not throw; `getCurrentCycle()` can no longer return null |
| FN-005 | PASS | T05, T06, T08–T11 | AC grep re-run: only `struts-json.xml:74`, `global.js:39, 210`, `javascript-constants.ftl:30-31` (left-hand side), the 7 JS reads and the `V2_6_0_20260930_0948` comment. Name-literal grep: only `crpPhases.ftl:227` plus `GlobalUnitCreateAction:499` (see W5) |
| FN-006 | PASS | T13 | Auditor dry run, see §2 |
| FN-007 | PASS | T12 | `GlobalUnitCreationPhaseNamingTest`: AICCRA after M1, AICCRA_III, unknown type, pre-M1 `Planing`, text path |
| FN-008 | PASS | T08, T15 | `deliverableMacros.ftl:367, 411` → `!progressPhase`. Browser: AICCRA_III 445 unlocked (0/0/0), 446 locked (1/2/18), AICCRA 430 unchanged |
| FN-009 | PASS | T02, T04 | Characterization rows: `Planning` / `Planning` / `Reporting` / `Planning` |
| DA-001 | PASS | T01 | 12 / 12, `Planing` left 0 |
| DA-002 | PASS | T13 | 66 / 66; the type guard is present |
| MIG-001 | PASS | T01, T13 | 8 scenarios recorded (12/66, 0/0, 0/0, 0/0, 6/0, 12/48, 1/0, 12/65); the auditor reproduced scenarios 1 and 2 |
| MIG-002 | PASS | T01, T13 | No id literal; DML only; only `description` / `name` written |
| NF-001 | **WARN** | T02–T10 | Holds by tests plus the HTML diff, with recorded reach gaps (W4) |
| NF-002 | **WARN** | T07–T10 | 186 pages `IDENTICAL` per batch, recorded and re-run by the Leader. The auditor could not re-capture (no server). Gaps in W4 |
| NF-003 | PASS | T11 | Re-verified (§5) |
| NF-004 | PASS | all | Per-task gates recorded; final state re-run |
| NF-005 | **WARN** | T05, T06 | Truth tables exist for every §B and guarded site, but T06 evaluates written-out copies rather than the production lines (W3) |
| OPS-001 | PASS | T13 | M2 sorts last; its header names T11 and M1 |
| OPS-002 | **WARN** | T13, T15 | Command in the release checklist; the production listing is deferred to release day. The local listing is **not empty** (2 drafts) (W2) |
| OPS-003 | **WARN** | T00 | Local copies, not production (W1) |
| OPS-004 | **WARN** | T01, T13 | Capture SELECTs are in both migration headers and in the checklist; the capture itself is deferred to release day (W2) |
| INT-1 | PASS | T12, T13, T15 | M2 applied by Flyway in the T15 rehearsal; OQ1 confirmed |
| INT-2 | PASS | T08, T15 | As FN-008 |
| INT-3 | **WARN** | T04–T08 | The behavior is correct, but the "Known sites" list in requirements §3 is incomplete (W5) |
| INT-4 | PASS | T01 | 12 rows repaired |

### Clause-level (`BUT` / `AND IT MUST`)

| Clause | Owner | Result | Clearance |
|---|---|---|---|
| AC FN-001/002 "BUT plan-phase answer MUST NOT be true for a progress phase" | T03 | PASS | `Phase.isPlanningPhase()` requires `Boolean.FALSE.equals(upkeep)`; covered by test rows |
| AC FN-001/002 "AND IT MUST be shown on the real data … 0 differences after DA-001" | T03 | PASS | `execution.md` T03 table; auditor check 0 |
| AC NF-005 "BUT `ProjectOutcomeValidator:191-194` and `OutcomesMilestonesAction:426-428` MUST NOT become true on the empty or a null phase" | T05 | PASS | Both are `isProgressPhase() \|\| isReportingPhase()`, false on empty and null; falsifier observed red |
| AC FN-003/004 "BUT a page rendered with no session or no global unit MUST NOT change its output" | T07–T10 | PASS | NOGU `login.do` / `glossary.do` in every batch diff |
| AC FN-005 "AND IT MUST be shown that no phase-name literal decides a type outside `rest/` and `crpPhases.ftl:227`" | T11 | PASS with drift | Re-run: one more literal at `GlobalUnitCreateAction:499`. It parses a typed name into the type of a new phase (owner decision, T12) and does not classify an existing phase. Not named in FN-005 (W5) |
| AC FN-006 "BUT a row whose description / upkeep does not match its name's type MUST keep its name" | T13 | PASS | Scenario 8 (65); falsifier 66 red |
| AC FN-007 "BUT a template phase of an unknown type MUST be copied unchanged" | T12 | PASS | `Pilot` / `Special` and pre-M1 `AWPB/Planing` rows |
| AC FN-008 "BUT the lock MUST NOT change in any plan or reporting phase" | T08, T15 | PASS | Reporting observed locked. The plan phase cannot render the deliverable tab, and `!progressPhase` reads only `upkeep`, which is 0 in both plan and reporting, so the reporting case covers the plan case (advisory A6 notes the `publication.ftl:240` path) |
| AC DA-001/MIG "AND IT MUST be shown that neither migration contains a numeric id literal" | T01, T13 | PASS | Auditor grep of the non-comment SQL |
| AC OPS-004 "BUT a rollback MUST NOT be attempted by cutoff and type alone" | T13 | PASS | Both headers and checklist step 5 say so |
| AC NF-001/002 "BUT a difference MUST NOT be waved through as cosmetic" | T08–T10 | PASS | Every batch `IDENTICAL`; no waved difference |

## 7. Linting & Code Quality

Checkstyle delta 0 (§5). Null and return-value pass: every new method is null-safe on `getActualPhase()` (P-1).
`getCurrentCycle()` lost its dead `catch → null`. No "not found" return value changed.

**4R advisory findings** carried forward from `execution.md`. None was taken, and none drives the verdict:

| # | Lens | Advisory | Source |
|---|---|---|---|
| A1 | Risk | M1 must ship with or before the T04+ code: the out-of-domain flips rely on it. M2 ships only after the code (OPS-001) | T04 |
| A2 | Reliability | T05 summary builders are never executed; the sites are proven only by a truth table plus a source match | T05 |
| A3 | Reliability | Pre-existing NPEs in `getNextPOWBMilestone` (`getNext()` null; null id unboxed). Bugfix candidate | T05 |
| A4 | Reliability | T06 truth tables do not pin the production lines; the class Javadoc overclaims | T06 |
| A5 | Resilience | Checkstyle rule severity is `warning`. It gates only through the delta-vs-HEAD practice; a CI that fails only at `error` would treat it as advisory. The regex misses method references and split calls | T11 |
| A6 | Readability / evidence | `publication.ftl:240` also renders the shared-cluster macro, in any phase. The T15 claim "tab absent in plan" holds for `projectDeliverable.ftl` only. Equivalence on plan holds by code reading (`upkeep = 0`) | Auditor |
| A7 | Readability | Test `aMisspelledPlaningDescriptionIsNotAPlanningPhaseButStillAPlanningCycle` asserts the opposite of its name; class Javadoc still says "as they answer today" | T04, T11 |
| A8 | Readability | Local `isUpkeepCycle` (`projectContributionCrp.ftl:559`) could become `isProgressCycle`; `"Progress"` is written in two places (manager constant, action literal) | T09, T12 |
| A9 | Risk | M2 compares `description` with the table collation (case-insensitive), while Java compares exactly; `BINARY` would align them | T13 |
| A10 | Resilience | `PhaseNameSiteTruthTableTest` reads `src/main/java` relative to the working directory, so it fails from an IDE or the repo root | T05 |
| A11 | Out of scope | `\n` in the shared-cluster name cell (pre-existing; separate bugfix); the hardcoded "Cluster submitted." label | T15 |

## 8. Design Conformance

| Check | Result | Note |
|---|---|---|
| Architecture (DD-1, DD-2): two layers, same three names | PASS | `Phase` + `BaseAction`, null-safe |
| DD-3 strict definitions | PASS | Code matches word for word |
| DD-11 negation rules and guarded sites | PASS | Bare negations kept; complements only at the raw-field guarded sites |
| Migrations (design §3, DD-10) | PASS | As designed; the stricter M2 guard differs from DD-3 only on combinations OPS-003 forbids |
| Proposal success criteria (§13) | PASS | All met except the production run of the migrations, which is release-day work |
| Cross-document figures | **WARN** | See below (W7) |
| Requirements text vs code | **WARN** | INT-3 list and FN-005 exception list are behind the code (W5) |
| Status bookkeeping | **WARN** | `tasks.md` and `execution.md` are inconsistent with the facts (W6) |

**Cross-document figure check**

| Figure | Claimed | Measured / other doc | Status |
|---|---|---|---|
| Tests | 1159 (T06, T11) | 1159 | Agrees |
| Compile files | 2415 / 1062 | 2415 / 1062 | Agrees |
| Harness pages | 186 (T10) | `pages.txt` 186; **design §17 says "114 pages"** | Drift |
| M2 scenarios | 66 / 48 / 65 (req §6, tasks T13, exec T13) | 66 reproduced | Agrees |
| §A1 files | 117 = 44 Java + 73 FTL | 42 main Java + 73 FTL in the diff (the other 2 are tests) | Agrees |
| Total files | ~135 (design §17, req §9) | 133 + 4 harness + 1 doc | Agrees |
| LOC | ~3,287 vs ~3,500 (exec close) | **3,018** churn (code, tests, config, harness, epic doc); 2,680 without harness and docs | Drift (overstated; under budget either way) |
| LOC | ~1,270 (`tasks.md` §9 and PR strategy) | Superseded twice in `design.md` §17, never updated in `tasks.md` | Drift |

## 9. Test Evidence Summary

| Evidence | Kind | Status |
|---|---|---|
| 4 new test classes + 2 rewritten doubles (54 spec tests) | Unit, characterization, equivalence, truth table, creation | Green in the 1159 run |
| Falsifiers T01–T15 | Observed red, quoted | Recorded per task |
| HTML diff harness, 186 pages, 3 batches | Rendered | `IDENTICAL` per batch (recorded) |
| M1 / M2 8-scenario dry runs | Data | Recorded; scenarios 1–2 reproduced by the auditor |
| T15 browser check, Flyway rehearsal | Manual / HITL | Owner-confirmed |
| `test-report.md` | — | Absent; `/akili-test` not run. Not required for archive, but the gaps in W3 and W4 are what such a run would close |

## 10. Agent Guide / Constitution Impact

**WARN (W8).** `execution.md` has no `## Constitution Impact` section, and no module boundary moved. Pending
shared-file items for `staging`:

- PRD `:157`, `:214` and TRD `:170`, `:570` (POWB / UpKeep / AR wording). **Also TRD `:176`**, which names
  `UpKeep` in the replication rule and is not on the pending list.
- Persona drift: `akili doctor --agents --fix`.
- A Kaizen candidate: worktree Implementers started from a stale base (`9a2cf412ef`) in five tasks.

The epic `phase-topology/agent-context.md` was updated as the spec's own deliverable (T14). `reports/ai-context/` has
no old names.

## 11. Remediation

| # | Result | Finding | Recommendation |
|---|---|---|---|
| W1 | WARN | OPS-003 (T00) and OQ4 part 2 ran on local copies, not production; the T00 Disqualifier says that is not evidence | Accept the owner exception explicitly, **or** run the T00 GROUP BY read-only on production before the first deploy of T03+ code and record it. **Update the spec** to record the exception in OPS-003 |
| W2 | WARN | Release-day items not yet done: OPS-004 capture for M1 and M2, OPS-002 draft listing (locally 2 drafts, so not empty), `qa_innovations_data` shape check | Execute steps 1–3 of the T13 release checklist and record them in the release notes. **Follow-up (release task)**, not a code change |
| W3 | WARN | NF-005 evidence: T06 tables evaluate copies, not the production lines; T05 summary builders are not executed | **Follow-up** test task: source-read pins as in T05, or narrow the Javadoc |
| W4 | WARN | NF-001/002 reach gaps: true branch of `!reportingPhase` in 33 annual report files not rendered; 4 project FTL files rest on delegation identity; INT-2 visible only in the manual check | Accept (mechanical rename; the false branch renders, so the name resolves). Optional `/akili-test` |
| W5 | WARN | Requirements behind the code: INT-3 "Known sites" misses the six year filters and `getExpectedStudiesYears`; FN-005 exceptions miss `GlobalUnitCreateAction:499` (owner-approved T12 parsing) | **Update the spec** (requirements §3 INT-3 and FN-005) at archive, with a closure sweep |
| W6 | WARN | Status drift: `tasks.md` pre-flight boxes unticked although `575a1c7aff` **is** the `origin/staging` HEAD and T00 is done; DoD §8 all unticked; `execution.md` T15 header says `[~]` but closes "done" | **Update the spec** docs: tick the facts, fill the DoD |
| W7 | WARN | Figure drift: design §17 "114 pages" (actual 186); execution LOC ~3,287 (measured 3,018); `tasks.md` §9 ~1,270 and its 5-PR plan never revised | **Update the spec**: record the measured totals in design §17 and tasks §9 |
| W8 | WARN | Shared-file pending items (PRD / TRD wording, including the extra TRD `:176`; persona drift) | Apply on `staging` through the constitutional process (pending list in `execution.md` T14 + TRD `:176`) |

No FAIL. No BLOCKED.

## 12. Archive Readiness Recommendation

**Ready to archive once the WARNs are accepted or ticketed.**

| Archive criterion | State |
|---|---|
| All tasks `[x]` | Yes |
| No unresolved FAIL | Yes |
| WARNs accepted or followed up | Pending owner: W1 accept; W2 release task; W3 follow-up; W4 accept; W5–W7 spec edits at archive; W8 `staging` items |
| Tests cover the key scenarios | Yes, with the gaps in W3 and W4 |
| Drift reflected in the spec docs | Not yet (W5–W7) |
| Owner reviewed this report | Pending |

The merge gate is separate from the archive gate. Before merging to `staging`, the release notes must say that M1
ships with or before the code, and M2 only after it (A1, OPS-001).

```text
/akili-archive enhancement/phase-type-unification
```

---

## Addendum A — Re-verification after the staging merge and T11a (2026-10-29)

> Written by the Leader, not the auditor. It re-runs the gates on the new HEAD; it does not re-audit the spec.

**Verdict:** the gates hold on `c0c9cbdade`. FN-005 and NF-003 are now met **with a recorded, time-limited exception**
(T11a); every other row of this report stands.

| Item | Value |
|---|---|
| Re-verified commit | `c0c9cbdade` on `phase-type-unification` |
| What changed since `d50883cd86` | `581858f937` (W5–W7 spec fixes, this report); `d3c539cbbd` merge of `staging` (8 files: project focus replication, project list, partner emails, i18n; no phase names, no old methods); `c0c9cbdade` T11a deprecated aliases (`execution.md` T11a) |
| Where | Temporary detached worktree at `c0c9cbdade`, removed afterwards; no build in the main checkout |

| Gate | Result |
|---|---|
| Clean compile | exit 0; 2415 (marlo-data) + 1062 (marlo-web) + 114 test sources |
| Full suite `-am` | 1160 run, 0 failures, 0 errors, 0 skipped (1159 + the T11a alias test) |
| Checkstyle, spec-wide vs base `575a1c7aff` | 55 Java files changed by the spec (the 8 staging-merge files excluded), 51 existing at base. Base 214 = 143 `RegexpSinglelineJava` (the old calls the spec removed) + 71 other; now 71, none `RegexpSinglelineJava`. **No new violation** |
| New rule tree-wide | 0 hits over `marlo-web`, `marlo-data`, `marlo-utils` `src` |
| Migrations after M1 on HEAD | only M1 `V2_6_0_20261028_1817` and M2 `V2_6_0_20261029_0834` |

**Row changes:**

| Row | Was | Now | Why |
|---|---|---|---|
| FN-005 / NF-003 | PASS | **PASS with exception** | Six `@Deprecated` aliases exist until T11b; the rule still fails any new caller; only two exact markers are exempt (requirements §9, 2026-10-29) |
| Task completion | 16/16 | 17/18 (T11b open) | T11b waits for A2-2607 and A2-2613 on `staging` |

**New WARN:**

| # | Finding | Recommendation |
|---|---|---|
| W9 | `staging` `d65ccdcb75` and `dev` `6ec459d8bc` fix the same project-focus duplication differently; a merge with `dev` makes `ProjectFocusManagerImplReplicationTest` fail (2 errors, NPE at `ProjectFocusManagerImpl.addProjectFocusPhase:58`; seen in the T11a merge simulation) | Not this spec's code. Settle with the owner of that fix at the `dev` merge |

**Archive readiness:** unchanged in substance; archive after T11b, or archive with T11b carried as a follow-up spec.

## Addendum B — Re-validation of NF-006 / T16 and the second staging merge (2026-10-29)

> Written by the auditor (T3, `opus`; T16's Implementer was `sonnet`). It re-runs every gate on the new HEAD and
> audits the one requirement added after validation, NF-006. Rows not named below stand as in §2–§12 and
> Addendum A.

**Verdict: PASS WITH WARNINGS stands. NF-006 PASS. 0 FAIL, 0 BLOCKED; 1 new WARN (W10, spec text), closed the same day.**

| Item | Value |
|---|---|
| Validated commit | `e0bb416b52` on `phase-type-unification` |
| Changes since `c0c9cbdade` | `1a233d503e` (Addendum A record); `6c4880ff5c` merge of `staging` (role labels from `.properties`, PMU/PMC → PIU i18n: `Role.java`, `BaseAction.getRoleLabel`, `CrpUsersAction`, `crpUsers.ftl`, `main-menu.ftl`, three `.properties`; no phase code); `2821305719` NF-006 + T16 spec; `e0bb416b52` T16 code |
| Where | Detached worktree `MARLO-val2` at `e0bb416b52`, removed afterwards. No Maven in the main checkout, no server, no DB access |

### Gates (re-run by the auditor)

| Gate | Result |
|---|---|
| Clean compile | Maven exit 0; 2415 (marlo-data) + 1062 (marlo-web) + 115 test sources |
| Full suite `-am` | Maven exit 0; 111 surefire reports summed: 1164 run, 0 failures, 0 errors, 0 skipped (1160 + 4 in `PhaseNameLiteralFtlTest`) |
| Checkstyle, spec-wide vs base `575a1c7aff` | 58 Java files changed by the spec's own first-parent commits (merge-only files excluded; `BaseAction.java` kept, since both sides touch it), 53 existing at base. Base 258 = 150 `[RemovedPhaseTypeMethod]` + 22 `[PhaseNameLiteral]` + 86 other; now 86, none from either rule. **No new violation** |
| Both rules, tree-wide | 0 `[RemovedPhaseTypeMethod]`, 0 `[PhaseNameLiteral]`, 0 `[RegexpSinglelineJava]` over every Java file of `marlo-web`, `marlo-data`, `marlo-utils` `src` (main and test) |
| Rules really run | The base copies above give 150 and 22 hits; the Leader's scratch probe (`execution.md` T16) shows each rule firing and the alias marker exempting only the FN-005 rule |

**Counting note.** Since T16 the NF-003 rule reports as `[RemovedPhaseTypeMethod]`. The `RegexpSinglelineJava`
counts in §2 and Addendum A were taken before that and are not comparable with a new run that greps the old label.

### NF-006 — clause-level coverage

| Clause (AC NF-006) | Evidence | Result |
|---|---|---|
| Java: a phase-name comparison with a literal in `src/main` outside `rest/` → Checkstyle reports it (both AC examples) | Seeded `LoginAction.java:53` → `[PhaseNameLiteral]`; probe `"POWB".equals(this.getActualPhase().getName())` → reported; staging extracts: all expected sites caught (31 hits with variants) | PASS |
| FTL: such a comparison → the suite fails naming file and line | Seeded `zzSeed.ftl:1` → `AssertionError … [WEB-INF/global/macros/zzSeed.ftl:1]`; staging `deliverableMacros.ftl:367, 411` and dev `:385, 429` caught as fixtures | PASS |
| On the tree both gates pass, only the three exceptions allowed | Tree-wide 0 hits (above); FTL test green with exactly one allowance use (`crpPhases.ftl:227`, matched by file and content, required to match once); `rest/` suppressed by id; `applyPhaseTypeOfName` not matched by construction | PASS |
| BUT null check, name-to-name and display MUST NOT be reported | `ProjectExpectedStudiesValidator:760, 774`, `rest/` lookups, summary file names, `${phase.name}` uses: 0 hits on the tree | PASS |
| D11 | Both gates in place; a `dev` merge carrying `deliverableMacros.ftl:385` will fail the suite until it is converted | PASS |
| T16 closing record | Reviewer PASS, attempt 3; two FAILs recorded with their fixes (`execution.md` T16) | PASS |

### Rows changed

| Row | Was | Now |
|---|---|---|
| Task completion | 17/18 (T11b open) | 18/19 (T11b open; T16 done) |
| Requirement coverage | NF-006 did not exist | NF-006 PASS |
| DoD (`tasks.md` §8) "T16 done and re-validated" | open | met by this addendum |

### New WARN

| # | Finding | Recommendation |
|---|---|---|
| W10 | `design.md` has no entry for NF-006. DD-9 describes only the NF-003 rule and its filter "with literal formats"; the T16 design (second rule `PhaseNameLiteral`, the `marlo-checkstyle-suppressions.xml` file scoped by id to `rest/` and `src/test`, the NF-003 rule's new id `RemovedPhaseTypeMethod` and the filter now matching that id because 8.18 matches `checkFormat` against the class name first) lives only in `execution.md` T16. `tasks.md:338` still says the filter covers "only `RegexpSinglelineJava`" | **Closed 2026-10-29:** DD-9 extended with the T16 design, `design.md` §2 repository-root files updated, `tasks.md` T11a filter bullet reworded. Closure sweep: `only \`RegexpSinglelineJava\`` and `checkFormat` grepped across this folder; remaining hits are history (`execution.md`, this report's earlier sections) |

### Advisory (carried from T16, not gating)

- `.claude/skills/marlo-verify/scripts/checkstyle.sh` does not `cd` to the repo root; from a subdirectory the
  relative suppressions file is missed and the run over-reports (never under-reports). Fix on `staging`, shared
  tooling.
- The Java rule is single-line and receiver-name based; its blind spots are listed in the rule comment.

**Archive readiness:** unchanged — archive after T11b, or archive with T11b carried as a follow-up spec; W10 is
closed (spec text updated).
