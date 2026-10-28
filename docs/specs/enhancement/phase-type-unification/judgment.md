# Phase Type Unification — Judgment Day Ledger

**Target:** design gate of `/akili-specify enhancement/phase-type-unification`. Immutable snapshot
`judgment-target-r1`, with these sha256 values:

| File | sha256 |
|---|---|
| `design.md` | `5a289418…f3c` |
| `requirements.md` | `c7b45538…065` |
| `call-sites.md` | `bb8c0b49…e91` |
| `proposal.md` | `3ea64768…72fc` |

**Source:** `9e2a9ed838`. **Date:** 2026-10-28.
**Judges:** A and B, blind, read-only, model `sonnet` (the author was a different model).
**Skill resolution:** `judgment-day`; project rules from root `CLAUDE.md` and `AGENTS.md`.

## Round 1 — frozen ledger

### Confirmed severe (both judges)

| ID | Sources | Finding | Evidence |
|---|---|---|---|
| JD-1 | A-1, B-1 | `ProjectOutcomeValidator:193` **has** a null guard: `getActualPhase() != null && getName() != null && !POWB && !AWPB`. With a null or empty actual phase the old condition is false. A plain `!isPlanningPhase()` would be true, a behavior change outside INT-1..3. Design §7 misdescribes the site. The same check is needed at `OutcomesMilestonesAction:426-428` | `ProjectOutcomeValidator.java:191-194` |
| JD-2 | A-4, B-2 | `description = 'Planing'` is on **12** rows in `aiccradb_actsave`, not 6: unit 50 (QATEST), created 2026-10-28 17:26 from the AICCRA template, copied the typo. P-6 does not reproduce. The S1 rollback "by the 6 known ids" and the fixed migration row counts are wrong | `SELECT … WHERE description='Planing'` on `aiccradb_actsave` |

### Confirmed issue, severity split (A severe, B finding)

| ID | Sources | Finding | Evidence |
|---|---|---|---|
| JD-3 | A-2, B-5, B-6 | **The inventory is incomplete, and the closed replacement table has no rule for FTL locals or macro parameters that share an old method's name.** Missing: `innovationTemplates.ftl` (`[#local isProgressActive = action.isProgressActive()]`, `required=!isProgressActive`), `projectInnovation.ftl:113` (`[#assign isProgressActive …]`), and the macro parameters `isReportingActive=…` in `deliverableListTemplate.ftl`, `homeDashboard.ftl` and `deliverableList.ftl:188`. The FN-005 grep would also hit the action name `isProgressActive` (`struts-json.xml:74`), its URL in `global.js:39, 210`, the JS globals and a migration comment. A blind whole-word rewrite would break the endpoint | judges' greps; `call-sites.md` §A has 441 entries, not the 449 the design states |

### Contradiction settled by a source re-run

| ID | Sources | Outcome |
|---|---|---|
| JD-4 | A-3 (severe) vs B-9 (warning) | **A-3 refuted.** Struts JSON plugin 6.8.0: `JSONResult.java:87 ignoreHierarchy = true` by default, and `DefaultJSONWriter.java:226-291` serializes the root action with `Introspector.getBeanInfo(clazz, clazz.getSuperclass())`, so only the action's own properties. BaseAction getters are not serialized. P-12 stands; its citation must name this default (B-9) |

### Confirmed findings (both, non-severe)

| ID | Sources | Finding |
|---|---|---|
| JD-5 | A-5, B-4 | P-11: `0a17b19671` is **not an ancestor** of HEAD (it is only on `logging-json-appender`), and `configuration/marlo-checkstyle.xml` has no Regexp rule. NF-003 has no base in this branch; the rule must be written from scratch here. Correct the "Verified at" cell |
| JD-6 | A-6, A-7, B-6 | Counts disagree. The budget parts add up to 1,255, not 1,100. The plan-phase combination has 7 Java sites (+1 interceptor), not 9. FTL `reportingActive` has 201 occurrences, not 205. §A has 441 entries, not 449. "~420" and "~450" appear for the same thing |
| JD-7 | A-9, B-7 | The production check of `GROUP BY description, upkeep` against the type rules must run **before S2**, not only before M2. The strict code ships in S2/S3 |
| JD-8 | A-10, B-8 | INT-3 is incomplete. Other unguarded error paths: `isUpKeepActive()` with a null actual phase; `CanEditPowbSynthesisInterceptor:112` unboxes `getUpkeep()`; `deliverableMacros.ftl:367, 411` on the empty phase. FN-002 must be scoped to non-null `upkeep` (the column is NOT NULL) |

### Suspect (one judge)

| ID | Source | Finding |
|---|---|---|
| JD-9 | B-3 | A unit after AICCRA created from the AICCRA template before S4 (unit 50 locally) holds `AWPB / Progress / AR`. M2 renames only `POWB/UpKeep/AR`, so the unit ends as `AWPB / Progress / Reporting`, which breaks FN-006 |
| JD-10 | A-8 | M1 flips `isPlanningActive()` from false to true on the closed `Planing` phases. That is DA-001's purpose, but it is not listed as an intended change |
| JD-11 | A-11 | `crpPhases.ftl:59-63` `cycTone` classifies a phase by `description?contains(…)`: a fourth classifier, untouched by this design |
| JD-12 | B-10 | §11 "would show a template error" is untested wording |

### Premise Ledger outcome (round 1)

- **Confirmed by both:** P-1, P-3, P-4, P-7, P-8, P-9, P-10, P-14.
- **Partial or caveated:** P-2 (local only), P-5 (count 201).
- **Refuted:** P-6 (JD-2), P-11 (JD-5), P-13 (JD-3).
- **Stands after the source re-run:** P-12 (JD-4).
- **Missing row:** the null-guard semantics of negated name tests (JD-1).

## Counters

- Round: 1 of 2.
- Confirmed severe: 2 (JD-1, JD-2).
- Severity-split: 1 (JD-3).
- Contradictions: 1, settled (JD-4).
- Confirmed findings: 4.
- Suspect: 4.
- Fix rounds used: 0. Re-judgments used: 0.

**Status:** round-one correction applied (owner chose *Fix and Re-judge*, 2026-10-28).

## Round 1 — correction and scoped re-judgment

**Correction:** applied by the orchestrator to JD-1, JD-2, JD-3, JD-5 to JD-12. JD-9 re-validated the migrations on
a throwaway schema: expected equalled actual in all 8 scenarios, and the schema was dropped. The delta is
`fix-delta.diff` (1,295 lines) in snapshot `judgment-target-r2`.

**Re-judgment:** two blind read-only judges (sonnet) saw only the frozen ledger plus the delta.

| JD | Judge A | Judge B |
|---|---|---|
| JD-1 | PARTIAL (RA-1) | RESOLVED |
| JD-2, JD-4, JD-5, JD-7 to JD-12 | RESOLVED | RESOLVED |
| JD-3 | RESOLVED (RA-2 miscount) | PARTIAL (RB-1) |
| JD-6 | PARTIAL (RA-2) | PARTIAL (RB-2, RB-3) |

### Re-judgment findings

| ID | Source | Severity | Finding |
|---|---|---|---|
| RJ-1 | RA-1 (A severe); B's own analysis agrees on the semantics | severe, single judge (suspect) | DD-11 / NF-005 say a site that "negates a name or type test" becomes the positive complement and is "never a bare `!`". That would also rewrite `!isPOWB()`, `!isReportingActive()`, `!isPlanningActive()` and `!isProgressActive()`, which negate null-safe `BaseAction` methods and are TRUE on the empty phase. About 30 sites, among them `DeliverableValidator:494, 806`, `ProjectExpectedStudiesValidator:755`, `projectContributionCrp.ftl:200` and `studiesTemplates.ftl`. The complement would flip them to FALSE. Judge B independently confirmed that a bare `!isPlanningPhase()` keeps equality there. Only **inline guarded tests on raw `Phase` / `getName()`** need the complement. This is a fix-caused wording defect |
| RJ-2 | RB-1, RA-4 | finding | §A2 omits the reads of the macro parameter at `deliverableListTemplate.ftl:14, 121`. A batch following §A2 literally would leave `isReportingActive` undefined in `deliverablesList`, an FTL error |
| RJ-3 | RB-2 | finding | "§B adds one file not already in §A1" is wrong. 5 §B files would be edited outside §A1: OutcomesMilestonesAction, ExpectedDeliverablesSummaryAction, FundingSourcesSummaryAction, ProjectsFundingSourcesSummaryAction, APConstants. The ~131 total is off by about 3 |
| RJ-4 | RB-3, RA-2 | finding | P-5 says 205 `reportingActive` occurrences, but that number counts occurrences in 201 lines; state both. §A3 has 13 entries, not 12. "About 113 rename-only files" is unexplained |
| RJ-5 | RB-4 | finding (fix-caused) | After JD-9, two families map to one new name. A reversal "by type" of M2 cannot restore `AWPB` / `Progress`. M2 must capture its pre-run `(id, name)` list, as M1 does |
| RJ-6 | RA-3 | warning | `deliverableMacros.ftl:367, 411` throws on a null name today and becomes `!progressPhase`, which is TRUE. INT-3 says throws become `false`. Reword to "the throw becomes the value of the new expression" |
| RJ-7 | RA-4, RB-5 | warning | A stray "Requirements NF-003; P-11." sits under DD-11, and DD-11 precedes DD-10 |
| RJ-8 | RB-6 | warning | `proposal.md` keeps the old migration table; mark it superseded |

**Judges contradict** on JD-1 (A PARTIAL / B RESOLVED), and RJ-1 is severe from one judge only. Per the gates
this is escalated for an explicit owner decision. A final bounded fix round (round 2 of 2) remains available.

**Counters:**
- Fix rounds used: 1 of 2. Re-judgments used: 1 of 2.
- Open: 1 suspect severe (RJ-1), 4 findings, 3 warnings.

**Status:** owner chose round 2 (*Fix and Re-judge*), 2026-10-28.

## Round 2 — correction and final scoped re-judgment

**Correction:** applied to RJ-1 to RJ-8. The delta is `fix-delta-r2.diff` (145 lines) in snapshot
`judgment-target-r3`.

| RJ | Judge A | Judge B |
|---|---|---|
| RJ-1 (the severe one) | RESOLVED; site-by-site check, no site mis-mapped | RESOLVED; site-by-site check, no site mis-mapped |
| RJ-2, RJ-3, RJ-4, RJ-6, RJ-7, RJ-8 | RESOLVED | RESOLVED |
| RJ-5 | NOT RESOLVED (FA-1) | NOT RESOLVED (FB-1) |

### Open after round 2

| ID | Sources | Severity | Finding |
|---|---|---|---|
| FJ-1 | FA-1, FB-1 (both) | finding, fix-caused | `design.md` §14 "Rollback after S5" still says M2 is reversed "keyed on the same cutoff and type". This contradicts the corrected S5 row, which restores from the captured `(id, name)` list. The capture step also lives only in the design; it should be an AC (FB-1) |
| FJ-2 | FA-2 | warning | DD-11 rule 1 / NF-005 say the old and new methods "both return false on the empty and the null phase". That is not true of `isUpKeepActive()`, which throws (INT-3). Add "except `isUpKeepActive`, INT-3" |
| FJ-3 | FB-2 | warning | DD-11 rule 2 should note that `deliverableMacros.ftl:367, 411`, which throws today on a missing name, maps to the bare `!progressPhase` (INT-2, INT-3) |

No severe finding is open. Both judges found no behavior change outside INT-1..INT-4 and no new exception
introduced by the delta.

**Counters:**
- Fix rounds used: 2 of 2. Re-judgments used: 2 of 2.
- Open: 0 severe, 1 finding, 2 warnings.

**Terminal state:** the lineage is exhausted with an open finding, so per the protocol it closes as escalated.
The decision goes to the owner.

JUDGMENT: ESCALATED ⚠️

## After escalation — owner decision (2026-10-28)

The owner chose **Correct FJ-1 to FJ-3 and continue to `tasks.md`**. Applied:
- **FJ-1:** `design.md` §14 "Rollback after S5" now restores from the captured `(id, name)` list. New requirement
  OPS-004, with its AC, captures each data migration's affected rows before it runs.
- **FJ-2:** DD-11 rule 1 and NF-005 name `isUpKeepActive()` as the INT-3 exception.
- **FJ-3:** DD-11 rule 2 excludes `deliverableMacros.ftl:367, 411`, which becomes a bare `!progressPhase`
  (INT-2 / INT-3).

Closure sweep: no remaining "by type" reversal in the spec documents. `proposal.md` holds the committed intent,
annotated as superseded.

**AKILI annotation: fixed, not re-judged: FJ-1, FJ-2, FJ-3.** The skill's terminal state above stays
`JUDGMENT: ESCALATED ⚠️`.
