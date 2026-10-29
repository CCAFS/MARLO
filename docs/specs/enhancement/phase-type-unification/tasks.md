# Phase Type Unification and Cycle Names — Tasks

**Spec ID:** ENH-PHASE-TYPE-UNIFICATION-001
**Status:** Draft
**Owner:** IBD Team — Kenji Tanaka
**Last Updated:** 2026-10-28
**Implements design:** docs/specs/enhancement/phase-type-unification/design.md (Judgment Day: `judgment.md`, escalated, then FJ-1..FJ-3 fixed and not re-judged)
**Branching:** `phase-type-unification`, from `staging` (`575a1c7aff`); no upstream set; first push `git push -u origin phase-type-unification`.
**Target merge:** staging.

Task prefix below: `T` = `ENH-PHASE-TYPE-UNIFICATION-001-T`.

---

## 1. Execution Context

| Item | Value |
|---|---|
| Java / build | Java 17. Compile gate (root `CLAUDE.md`): `rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes && mvn -q -o -pl marlo-data,marlo-web -am test-compile -DskipTests`. A healthy run compiles ~2400 + ~1040 files |
| Tests | `mvn -q -o -pl marlo-web -am test` (keep `-am`) |
| Checkstyle | Checkstyle 8.18 run directly, `-c configuration/marlo-checkstyle.xml`, on the changed files, compared with the same files at HEAD (`marlo-verify`) |
| Local app (T07–T10, T15 only) | `scripts/run-marlo-java17.sh`. **It kills every `cargo:run`, wipes `target/` and rewrites `marlo-dev.properties`**, so it runs only when no other session builds in this checkout. Check `ListAgents` and ask the user first (design OR-2) |
| DB | Local `aiccradb_actsave` for the app, read-only SELECTs for checks. Migration dry runs only on a `zz_*` throwaway schema |
| Inventory | `call-sites.md` at `9e2a9ed838`. Re-run its commands at the start of each migration task; a count mismatch stops the task |

## 2. Pre-flight Checklist

- [x] `requirements.md` and `design.md` written; design reviewed by Judgment Day (`judgment.md`).
- [x] Feature branch `phase-type-unification` exists, from `staging` `575a1c7aff`.
- [ ] `575a1c7aff` (template fix from another session) pushed to `origin/staging`, or removed from this branch before the PR (owner).
- [ ] T00 production check done before T03 (OPS-003).
- [ ] OQ2 (external `phases.name` readers) and OQ3 (QA view) answered before T13.

## 3. Task List

### Coverage map (scenario and clause level)

| Requirement / AC clause | Owner task |
|---|---|
| FN-001, AC FN-001/002 "equal for every row", "cycle true for plan and progress", "BUT plan-phase answer not true for progress" | T03 |
| AC FN-001/002 "AND IT MUST be shown on the real data … 0 differences after DA-001" | T03 (SQL comparison on the five copies, quoted) |
| FN-002 (old method = new, non-null upkeep) | T02 (pins old) + T04 (delegation keeps T02 green) |
| FN-003, FN-004, AC FN-003/004/INT-3 "each returns false", "no exception" | T03 (Phase) + T04 (BaseAction) |
| AC FN-003/004 "BUT a page rendered with no session or no global unit MUST NOT change its output" | T07 (captures it) + T08–T10 (diff) |
| NF-005, AC NF-005 (five-input truth tables; "BUT ProjectOutcomeValidator / OutcomesMilestonesAction MUST NOT become true on empty / null") | T05 (§B sites) + T06 (guarded Java sites in §A1) |
| FN-005, AC FN-005/NF-003 (grep returns no hit except §A3; no FTL local or parameter keeps an old name; adding a call fails Checkstyle; "AND IT MUST be shown that no phase-name literal decides a type…") | T11 |
| FN-006, DA-002, AC FN-006/DA-002 (47+ renamed, 45 unchanged, "BUT inconsistent row keeps its name") | T13 |
| FN-007, AC FN-007 (templates 45 and 47; no `Planing`; "BUT unknown type copied unchanged") | T12 |
| FN-008, INT-2, AC FN-008 ("editable, no label, no note"; "AICCRA progress as today"; "BUT lock unchanged in plan / reporting") | T08 (code) + T15 (human check of AICCRA_III progress page) |
| FN-009, AC FN-009 | T02 (pins) + T04 |
| DA-001, MIG-001, MIG-002, AC DA-001/MIG (8 scenarios, expected = pre-run SELECT, "AND IT MUST … no numeric id literal") | T01 (M1) + T13 (M2) |
| NF-001, NF-002, AC NF-001/002 ("byte-identical", "BUT a difference MUST NOT be waved through") | T07 (baseline) + T08, T09, T10 (each batch) |
| NF-003 | T11 |
| NF-004 | every code task (Done criteria) |
| OPS-001, AC OPS-001 | T13 (file order + header) |
| OPS-002, AC OPS-002 | T13 (release checklist) + T15 (records the listing) |
| OPS-003 | T00 |
| OPS-004, AC OPS-004 ("captured before each migration", "BUT no rollback by cutoff and type") | T01 + T13 |
| INT-1 | T12 + T13 |
| INT-3 known sites | T04 (`isUpKeepActive`), T05 (summaries), T06 (`CanEditPowbSynthesisInterceptor:112`), T08 (`deliverableMacros`) |
| INT-4 | T01 |
| Out-of-scope docs (phase-topology agent-context, ai-context, PRD/TRD pending item) | T14 |

---

### T00 — Production read-only checks before code (OPS-003, OQ4 part 1)
- **Status:** [x] done (2026-10-28, on the local copies by owner decision; `execution.md` T00) · **Size:** XS · **Depends on:** none · **Module:** none (DB read)
- **Requirements:** OPS-003; settles P-2 for production.
- **Design refs:** §14 S0; P-2.
- **Scope:** run against production, read-only:
  - `SELECT description, upkeep, COUNT(*) FROM phases GROUP BY 1, 2`;
  - `SELECT COUNT(*) FROM phases WHERE description = 'Planing'`;
  - MySQL `SELECT VERSION()`, which the end-of-process `reporting`-column evaluation needs.

  Record the output in `execution.md`.
- **Tests:** none (data check).
- **Verification:** every combination is in {`Planning/0`, `Planning/1`, `Reporting/0`, `Planing/0`, `Planing/1`}.
- **Falsifier:** a production row such as `Reporting/1` or `Planning` with another spelling → the check reports it and T03 is blocked.
- **Red run:** n/a — data check, no code under test.
- **Disqualifier:** output taken from a replica older than the last phase change, or from a local copy, is not production evidence.
- **Consumers:** none.
- **Review:** checklist — a read-only query whose only output is a recorded table.
- **Done when:** the output is recorded and the owner confirms no other combination exists, or the rollout is stopped.
- **Skills:** none (the owner runs it).

### T01 — M1: fix the `Planing` typo (DA-001, INT-4, OPS-004)
- **Status:** [x] done (2026-10-28, PASS attempt 1; `execution.md` T01) · **Size:** S · **Depends on:** none · **Module:** marlo-web (migrations)
- **Requirements:** DA-001, MIG-001, MIG-002, OPS-004, INT-4; AC DA-001/MIG-001/MIG-002 (M1 column); AC OPS-004 (M1 part).
- **Design refs:** §3 M1; §14 S1; P-6.
- **Files touched:** `marlo-web/src/main/resources/database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__FixPlaningPhaseDescription.sql`, new, named by the `marlo-migration` script.
- **Scope:**
  - The text-match UPDATE from the validated draft.
  - A header comment stating: no ids, DML only, the reason, and that the pre-run capture SELECT belongs in the release notes.
- **Tests:** throwaway-schema dry run of the 8 scenarios with expected = pre-run SELECT, using the harness from design §3; second run = 0.
- **Verification:** the harness output quoted in `execution.md`; `grep -E "[0-9]{2,}" <file>` shows no id literal.
- **Falsifier:** add a decoy row (`description = 'Plan B'`) to the real-copy scenario and change the WHERE to `description LIKE 'Plan%'` → actual 13 exceeds expected 12, collateral 1 → red. (Amended 2026-10-28: the first wording, "scenario 1 actual exceeds expected", could not go red, because the mysql CLI's `ROW_COUNT()` counts changed rows and `Planning`→`Planning` changes nothing. Through Flyway/JDBC the count is matched rows, so release-log counts are not comparable with the harness.)
- **Red run:** observed in T01 (`execution.md` T01): `9 extra: decoy 'Plan B' row  expected=12  actual=13 … collateral=1  FAIL`.
- **Disqualifier:** a dry run against `aiccradb1`–`4` or `aiccradb_actsave` instead of a `zz_*` schema is not evidence, and it is forbidden.
- **Consumers:** the 97 planning-gated managers and `findCycle` callers start recognizing the repaired rows (INT-4).
- **Review:** full — data migration on production data.
- **Done when:**
  - all 8 scenarios equal expected;
  - the falsifier's red is observed and quoted;
  - the `marlo-migration` checklist is reported;
  - the throwaway schema is dropped.
- **Skills:** `marlo-migration`, `marlo-commit`.

### T02 — Characterization tests of today's phase-type behavior (B1)
- **Status:** [x] done (2026-10-28, PASS attempt 1; `execution.md` T02) · **Size:** M · **Depends on:** none · **Module:** marlo-web (tests)
- **Requirements:** FN-002, FN-009 (pins the old results); NF-001 baseline.
- **Design refs:** §14 S2; DD-5; P-1, P-10.
- **Files touched:**
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/BaseActionPhaseTypeCharacterizationTest.java` (new, GPL header);
  - rewrite of the two doubles `BaseActionGlobalUnitRulesTest:104` and `ProjectActivitiesValidatorCancelledTest:46`, so they set the actual phase instead of overriding an old method.
- **Scope:** a table of inputs, each evaluated through every old type method and `getCurrentCycle()`, with today's expected value or `throws` asserted:
  - `POWB/Planning/0`, `AWPB/Planning/0`;
  - `UpKeep/Planning/1`, `Progress/Planning/1`;
  - `AR/Reporting/0`;
  - the empty phase, a null actual phase, `upkeep = null`, `description = null`.
- **Tests:** this task is the test.
- **Verification:** `mvn -q -o -pl marlo-web -am test -Dtest=BaseActionPhaseTypeCharacterizationTest,BaseActionGlobalUnitRulesTest,ProjectActivitiesValidatorCancelledTest`.
- **Falsifier:** flip `isPOWB()` to also accept `"UpKeep"` → the `UpKeep/Planning/1` row goes red on its assertion.
- **Red run:** `predicted — greenfield artifact (observed in T02)`: apply the falsifier, quote the assertion failure, revert.
- **Disqualifier:** a red from Spring or session setup rather than from the asserted value is not a red (`tdd`: assertion-level red). An expected value written from intent rather than from running the old code is not characterization.
- **Consumers:** the two rewritten tests (P-10).
- **Review:** full — this pins the contract everything after it relies on.
- **Done when:**
  - green on the current code;
  - the falsifier red is observed;
  - the two doubles no longer override old methods, and still assert what they asserted.
- **Skills:** `tdd`, `marlo-verify`, `marlo-commit`.

### T03 — `Phase` type methods + equivalence (B2)
- **Status:** [x] done (2026-10-28, PASS attempt 1; `execution.md` T03) · **Size:** S · **Depends on:** T00, T01 (strict definitions need M1, DD-3), T02 · **Module:** marlo-data + marlo-web tests
- **Requirements:** FN-001, FN-002, FN-003, FN-004; AC FN-001/002 (all clauses), AC FN-003/004 (Phase part).
- **Design refs:** §1; DD-1; DD-3; P-2, P-3.
- **Files touched:**
  - `marlo-data/.../data/model/Phase.java`: `isPlanningPhase()`, `isProgressPhase()`, `isReportingPhase()`, primitive `boolean`, not `final`, null-safe; `isReporting()` delegates;
  - `marlo-web/src/test/java/.../data/model/PhaseTypeEquivalenceTest.java` (new).
- **Scope:**
  - The methods.
  - A unit test over the same input table as T02, asserting new = old.
  - The row-by-row SQL comparison of old versus new rules on the five local copies, with M1 simulated, quoted in `execution.md`.
- **Tests:** `PhaseTypeEquivalenceTest`.
- **Verification:**
  - compile gate;
  - `mvn -q -o -pl marlo-web -am test -Dtest=PhaseTypeEquivalenceTest`;
  - the SQL comparison reports 0 differences.
- **Falsifier:** define the planning cycle as `!isReportingPhase()` → the empty-phase row goes red (true where old is false).
- **Red run:** `predicted — greenfield artifact (observed in T03)`.
- **Disqualifier:** a comparison that excludes the empty or the null-field rows does not cover FN-003 / FN-004.
- **Consumers:** `timeline-phases.ftl:148`, `BaseAction:3279, 3369`, `DeliverablesReplicationAction:256, 460` and the summaries via `isReporting()` (P-13); behavior unchanged.
- **Review:** full.
- **Done when:**
  - the test and the SQL comparison are green;
  - the falsifier red is observed;
  - the compile gate passes and the Checkstyle delta is 0.
- **Skills:** `tdd`, `marlo-verify`, `marlo-commit`.

### T04 — `BaseAction` same-named methods + delegation of the old ones (B3)
- **Status:** [x] done (2026-10-28, PASS attempt 1; `execution.md` T04) · **Size:** S · **Depends on:** T03 · **Module:** marlo-web
- **Requirements:** FN-002, FN-003, FN-004, FN-009, INT-3 (`isUpKeepActive`); NF-001.
- **Design refs:** DD-2; §14 S2; P-1.
- **Files touched:** `action/BaseAction.java`:
  - adds `isPlanningPhase()`, `isProgressPhase()`, `isReportingPhase()`, each null-safe on `getActualPhase()`;
  - the six old methods delegate to them and are marked `@Deprecated`;
  - `getCurrentCycle()` is computed from `isReportingPhase()`.
- **Tests:** T02 stays green, except the `isUpKeepActive` throw rows, which become `false` (INT-3): that row's expectation changes, and the commit names it.
- **Verification:** compile gate; `mvn -q -o -pl marlo-web -am test`.
- **Falsifier:** delegate `isPlanningActive()` to `isPlanningPhase()` (plan only) → T02's `UpKeep/Planning/1` row for `isPlanningActive` goes red.
- **Red run:** `predicted — greenfield artifact (observed in T04)`.
- **Disqualifier:** any T02 expectation edited other than the following invalidates the run:
  - the INT-3 `isUpKeepActive` rows;
  - the five rows outside DD-3's domain, whose name disagrees with the type or which have a null field:
    `POWB/Planing/0`, `POWB/null/0`, `null/Planning/0`, `UpKeep/Planning/null`, `UpKeep/null/1`. They change
    because the old plan methods read the name (FN-002 as amended at the T03 gate). Each changed cell is named
    in the commit.

  (Amended 2026-10-28 at the T04 pre-spawn gate, approved by the owner.)
- **Consumers:** all of `call-sites.md` §A1, through the delegates; no call site changes yet.
- **Review:** full.
- **Done when:** T02 green with only the INT-3 rows changed; falsifier observed; gates pass.
- **Skills:** `tdd`, `marlo-verify`, `marlo-commit`.

### T05 — Name-literal sites (§B, Java) with truth tables (NF-005)
- **Status:** [x] done (2026-10-28, PASS attempt 1; `execution.md` T05) · **Size:** M · **Depends on:** T04 · **Module:** marlo-web (+ both `APConstants`)
- **Requirements:** FN-005 (Java name literals), NF-005 and its AC (including the "BUT … MUST NOT become true" clause), INT-3 (summary sites).
- **Design refs:** §7; DD-11 rule 2; P-9, P-15.
- **Files touched:**
  - `validation/projects/ProjectOutcomeValidator.java:191-194` (complement);
  - `action/annualReport/y2018/OutcomesMilestonesAction.java:426-428` (complement; loop guard kept);
  - `action/summaries/FundingSourcesSummaryAction.java:498, 697, 1004`, `ProjectsFundingSourcesSummaryAction.java:611`, `ExpectedDeliverablesSummaryAction.java:352-356` (`selectedPhase.isProgressPhase()`);
  - `BaseAction.java:3966, 3982, 4230, 4243, 4584, 4597` (`c.getPhase().isReportingPhase()`);
  - remove `PROJECT_INDICATOR_PHASE_PREVIOUS_NAME` from both `APConstants.java`. `POWB_ACRONYM` stays (P-9).
- **Tests:** `PhaseNameSiteTruthTableTest` (new): each site's old and new expression on plan, progress, reporting, empty and null phase.
- **Verification:**
  - compile gate;
  - tests with `-am`;
  - `grep -rn "PROJECT_INDICATOR_PHASE_PREVIOUS_NAME" marlo-web/src marlo-data/src` → 0;
  - `call-sites.md` §B commands show only `crpPhases.ftl:227`, the comment at `projectContributionCrp.ftl:539`, the declaration and the file-name use of `POWB_ACRONYM`, and the FTL sites owned by T08.
- **Falsifier:** write `ProjectOutcomeValidator`'s replacement as `!isPlanningPhase()` → the empty-phase and null-phase rows go red (true where old is false).
- **Red run:** `predicted — greenfield artifact (observed in T05)`.
- **Disqualifier:** a truth table that omits the empty or the null input does not prove NF-005.
- **Consumers:** the validators' callers (save pipeline) and summaries; the 6 BaseAction year methods feed studies, innovations and policies FTL (P-9).
- **Review:** full — the save pipeline.
- **Done when:** all truth tables equal (except throws → INT-3 value); falsifier observed; gates pass.
- **Skills:** `tdd`, `error-handling-patterns`, `marlo-verify`, `marlo-commit`.

### T06 — Java call sites of the old methods (§A1 Java)
- **Status:** [x] done (2026-10-28, PASS attempt 1; `execution.md` T06) · **Size:** L · **Depends on:** T05 · **Module:** marlo-web
- **Requirements:** FN-005 (Java part), NF-001, NF-005 (DD-11 rule 1 negations), INT-3 (`CanEditPowbSynthesisInterceptor:112`).
- **Design refs:** §5 replacement table (Java column); DD-11; §7; §8.
- **Files touched:** the 42 non-test Java files in `call-sites.md` §A1 (159 lines). Each use becomes the same-named `BaseAction` method or, for a phase other than the actual one, the `Phase` method:
  - the 7 + 1 plan-phase combinations → `isPlanningPhase()`;
  - `ValidateProjectSectionAction:406` → `isProgressPhase()`;
  - negations follow DD-11 rule 1;
  - `isPlanningActive()` → `(isPlanningPhase() || isProgressPhase())`.
- **Tests:**
  - T02, T03, T05 green;
  - `PhaseNameSiteTruthTableTest` extended with every guarded or combined site (the 8 combinations, `CanEditPowbSynthesisInterceptor:112, 165`, `ProjectSubmissionAction:304`).
- **Verification:**
  - compile gate;
  - tests;
  - `grep -rnwE "isPOWB|isAWPBActive|isPlanningActive|isProgressActive|isUpKeepActive|isReportingActive" marlo-web/src/main/java` → only the `BaseAction` declarations and `struts-json.xml` is not Java (§A3).
- **Falsifier:** map `isPlanningActive() && !isUpKeepActive()` to `isPlanningPhase() || isProgressPhase()` → the truth-table progress row goes red.
- **Red run:** `predicted — greenfield artifact (observed in T06)`.
- **Disqualifier:** a green compile with a Java use left, found by the grep, means the task is not done. "It compiles" proves nothing while the delegates exist.
- **Consumers:** interceptors, validators and summaries listed in §A1; the 2 tests already rewritten in T02.
- **Review:** full.
- **Done when:** grep clean except declarations; truth tables equal; gates pass.
- **Skills:** `error-handling-patterns`, `marlo-verify`, `marlo-commit`.

### T07 — Rendered-HTML capture harness and baseline (NF-002)
- **Status:** [x] done (2026-10-28, PASS attempt 1, checklist review; `execution.md` T07) · **Size:** M · **Depends on:** T04 (the code under baseline must already delegate) · **Module:** tooling (scratch, not shipped)
- **Requirements:** NF-002, AC NF-001/002, AC FN-003/004 "BUT a page rendered with no session or no global unit…".
- **Design refs:** DD-5; §16 OR-1, OR-2.
- **Files touched:** a capture script outside the shipped tree (spec folder `harness/` or scratchpad), recorded in `execution.md`.
- **Scope:**
  - Log in locally (`~/.marlo-local-credentials`) and capture the HTML of the reference set as one user:
    - project list, project description;
    - deliverable (including the participants section), expected study, innovation;
    - budget by partners, project contributions;
    - main menu and guide button.
  - Capture each in one plan, one progress and one reporting phase of AICCRA (45) and AICCRA_III (47), with progress phases opened only on a `zz_*` copy if needed, never in a shared DB.
  - Also capture one page without a global unit.
  - Normalize only the cache-bust query parameters and CSRF tokens. Store the baseline.
- **Tests:** a second capture with no code change → byte-identical (the harness's own null test).
- **Verification:** two consecutive baseline runs are identical.
- **Falsifier:** change one FTL `reportingActive` to `planningActive` on a captured page → the diff names that page.
- **Red run:** `predicted — greenfield artifact (observed in T07)`.
- **Disqualifier:**
  - two baseline runs that differ, from timestamps, random ids or order, make every later diff worthless until normalized;
  - a capture taken while another session's server runs the checkout is not evidence.
- **Consumers:** T08, T09, T10.
- **Review:** checklist — test tooling, not shipped; its falsifier is the gate.
- **Done when:** baseline stable across two runs; falsifier observed; the run is coordinated with other sessions.
- **Skills:** `playwright-cli`, `marlo-verify`.

### T08 — FTL batch 1: `global/macros` (incl. §A2 renames and INT-2)
- **Status:** [x] done (2026-10-28, PASS attempt 1; INT-2 unobserved locally, see `execution.md` T08) · **Size:** M · **Depends on:** T07 · **Module:** marlo-web (FTL)
- **Requirements:** FN-005 (part), FN-008, INT-2, INT-3 (`deliverableMacros`), NF-001, NF-002; AC FN-008 (code part).
- **Design refs:** §5 (table, §A2 rule, exclusions); DD-6; DD-11 rule 1 and FJ-3.
- **Files touched:** every `global/macros/*.ftl` in §A1:
  - `studiesTemplates.ftl`, `innovationTemplates.ftl`, `deliverableListTemplate.ftl`, `homeDashboard.ftl`, `deliverableMacros.ftl`, `forms.ftl`, …;
  - the §A2 renames: locals `isProgressActive` → `isProgressPhase`; macro parameter `isReportingActive` → `isReportingPhase` in the 7 definitions, their reads (`deliverableListTemplate.ftl:14, 121, 280, 350`) and **every** caller (`deliverableList.ftl:188, 191, 226` …, found by grep in this batch).
- **Tests:** HTML diff against the T07 baseline.
- **Verification:**
  - the diff is identical everywhere except the expected INT-2 change on the AICCRA_III progress deliverable page: lock, label and note gone;
  - token count for these files = 0 for old names.
- **Falsifier:** rename the macro parameter in its definition only → the deliverable list renders the reporting columns differently → diff red.
- **Red run:** `predicted — greenfield artifact (observed in T08)`.
- **Disqualifier:** any diff not explained by INT-2 is a FAIL, never "cosmetic" (AC NF-002).
- **Consumers:** every page including these macros (T07 set); `publications/publication.ftl:240` (participants macro).
- **Review:** full.
- **Done when:** diff clean except INT-2; counts zero; falsifier observed.
- **Skills:** `playwright-cli`, `marlo-verify`, `marlo-commit`.

### T09 — FTL batch 2: `crp/views/projects`
- **Status:** [x] done (2026-10-28, PASS attempt 1; `execution.md` T09) · **Size:** M · **Depends on:** T08 · **Module:** marlo-web (FTL)
- **Requirements:** FN-005 (part), NF-001, NF-002; INT-3 (`menu-projects.ftl` `UpKeepActive`).
- **Design refs:** §5.
- **Files touched:** every `crp/views/projects/*.ftl` in §A1, for example `projectContributionCrp.ftl` (including `:561` → `planningPhase`), `menu-projects.ftl`, `projectInnovation.ftl:113` (§A2 local), `deliverableList.ftl` (if not done in T08), `projectCcafsOutcomes.ftl`.
- **Tests / Verification:** HTML diff clean; token count zero for these files.
- **Falsifier:** drop the `!` of a `!reportingActive` → the diff shows the field in the reporting page.
- **Red run:** `predicted — greenfield artifact (observed in T09)`.
- **Disqualifier:** as T08.
- **Consumers:** project section pages.
- **Review:** full.
- **Done when:** diff clean; counts zero; falsifier observed.
- **Skills:** `playwright-cli`, `marlo-verify`, `marlo-commit`.

### T10 — FTL batch 3: remaining FTL (`global/pages`, home, publications, annual report, POWB, admin)
- **Status:** pending · **Size:** M · **Depends on:** T09 · **Module:** marlo-web (FTL)
- **Requirements:** FN-005 (part), NF-001, NF-002.
- **Design refs:** §5 (including `POWB` property and exclusions).
- **Files touched:** the rest of §A1 FTL:
  - `guide-button.ftl:91, 225` (`POWB?exists && POWB` → `planningPhase`) and `:153, 228` (`UpKeepActive`);
  - `main-menu.ftl` (right-hand side only, `reportingActiveMenu` kept);
  - `javascript-constants.ftl:30-31` (right-hand side only; JS names kept);
  - `projectsListTemplate.ftl:12`, `publicationsList.ftl`, etc.
- **Tests / Verification:** HTML diff clean, including the page without a global unit and the JS globals' rendered values; token count zero for all FTL except §A3.
- **Falsifier:** misspell `reportingPhase` in `javascript-constants.ftl` → the `var reportingActive = false` line differs on the reporting page → red.
- **Red run:** `predicted — greenfield artifact (observed in T10)`.
- **Disqualifier:** as T08.
- **Consumers:** the 7 JS reads (`safeguards.js:116`, `projectDescription.js:287`, `deliverableInfo.js:618, 784, 814`, `projectStudy.js:607`, `outcomes.js:169`).
- **Review:** full.
- **Done when:** diff clean; FTL counts zero; falsifier observed.
- **Skills:** `playwright-cli`, `marlo-verify`, `marlo-commit`.

### T11 — Remove the old methods, fields and setters; Checkstyle rule (B5)
- **Status:** pending · **Size:** S · **Depends on:** T06, T10 · **Module:** marlo-web + `configuration/`
- **Requirements:** FN-005, NF-003; AC FN-005/NF-003 (all clauses including "AND IT MUST be shown that no phase-name literal decides a type…").
- **Design refs:** DD-9; §13; P-8, P-11, P-12.
- **Files touched:**
  - `BaseAction.java`: delete the six old methods, `planningActive` / `reportingActive` fields and setters (`:471, 487, 8924, 8928`);
  - `configuration/marlo-checkstyle.xml`: a `RegexpSinglelineJava` rule for the six names.
- **Scope:** **first step, settling P-11:** add the rule and seed one forbidden call in a scratch Java file; observe the Checkstyle red; remove the seed.
- **Tests:** compile gate; full test suite with `-am`.
- **Verification:**
  - the AC FN-005 grep set returns only the §A3 identifiers;
  - the phase-name literal grep (`getName()` on phases in Java, `.name` on phase variables in FTL) returns only `crpPhases.ftl:227` and display uses.
- **Falsifier:** re-add `public boolean isPOWB()` and a call → Checkstyle red. Remove the rule → green, which proves the rule is the gate.
- **Red run:** `predicted — greenfield artifact (observed in T11)`.
- **Disqualifier:** a Checkstyle run through `mvn checkstyle:check` is not evidence; it cannot run here (`marlo-verify`).
- **Consumers:** every former caller, now on the new names; the rewritten tests.
- **Review:** full.
- **Done when:** compile, tests and Checkstyle green; seeded red observed; greps clean.
- **Skills:** `marlo-verify`, `marlo-commit`.

### T12 — Name phases by type at global-unit creation (FN-007)
- **Status:** [x] done (2026-10-28, PASS attempt 1; `execution.md` T12) · **Size:** S · **Depends on:** T03 · **Module:** marlo-data + marlo-web
- **Requirements:** FN-007, INT-1 (creation part); AC FN-007 (all clauses).
- **Design refs:** DD-8; P-7.
- **Files touched:**
  - `GlobalUnitCreationManagerImpl.java:519-543` (`createPhases`): name from type when the type is known;
  - `GlobalUnitCreateAction.java:490-525` (`parsePhaseLine`): derives `description` / `upkeep` for the names Planning / Progress / Reporting only;
  - a new test `GlobalUnitCreationPhaseNamingTest`.
- **Tests:** templates as AICCRA (`AWPB/Progress/AR`, after M1), AICCRA_III (`POWB/UpKeep/AR`), and an unknown-type phase. A pre-M1 `AWPB/Planing/0` template phase is an unknown type, copied unchanged and documented as such. (Amended 2026-10-28 at the T12 pre-spawn gate, owner: creation relies on M1.)
- **Verification:** compile gate; test with `-am`.
- **Falsifier:** copy the template name unchanged → the AICCRA-template case asserts `Planning` and gets `AWPB` → red.
- **Red run:** `predicted — greenfield artifact (observed in T12)`.
- **Disqualifier:** a test that builds the expected name from the same mapping the code uses is an echo, not evidence (`KZ-…auth-flow-2`, fixture rule). Expected values are literal strings.
- **Consumers:** the admin creation screen (both paths, P-7).
- **Review:** full.
- **Done when:** test green; falsifier observed; gates pass.
- **Skills:** `tdd`, `marlo-verify`, `marlo-commit`.

### T13 — M2: change stored phase names (DA-002, OPS-001, OPS-002, OPS-004)
- **Status:** pending · **Size:** S · **Depends on:** T11, T12, OQ2, OQ3 · **Module:** marlo-web (migrations) + release checklist
- **Requirements:** FN-006, DA-002, MIG-001, MIG-002, OPS-001, OPS-002, OPS-004, INT-1; AC FN-006/DA-002 (including "BUT inconsistent row keeps its name"), AC DA-001/MIG (M2 column), AC OPS-001, AC OPS-002, AC OPS-004.
- **Design refs:** §3 M2; DD-10; §14 S5 and the rollback paragraph; proposal R10.
- **Files touched:**
  - the `V2_6_0_<YYYYMMDD>_<HHMM>__RenamePhasesAfterAiccra.sql` migration (new; it sorts after T01's file and states its dependency on T11 in its header);
  - the release checklist in `execution.md`:
    - the pre-run capture SELECT of `(id, name)`;
    - the draft listing: `ls <autosave.folder> | grep -E '_(POWB|AWPB|UpKeep|AR)_[0-9]{4}_(<acronyms after AICCRA>)_'`, longest acronym first.
- **Tests:** 8-scenario dry run on `zz_*` with expected = pre-run SELECT (including unit 50); second run = 0.
- **Verification:** harness output quoted; no id literal; the file order is checked against T01 and the merge of T11.
- **Falsifier:** remove the type guard → scenario 8 (phase 445 with `upkeep = 0`) actual 66 ≠ expected 65 → red.
- **Red run:** `predicted — greenfield artifact (observed in T13)`.
- **Disqualifier:** a dry run on a real copy is forbidden and not evidence. Counts compared against fixed numbers instead of the pre-run SELECT are not evidence (JD-2).
- **Consumers:** `call-sites.md` §C:
  - autosave drafts (148 files; OPS-002);
  - REST v2 (out of scope, R5);
  - display (39 lines);
  - `qa_innovations` (OQ3);
  - external readers (OQ2).
- **Review:** full — production data.
- **Done when:** all scenarios equal expected; falsifier observed; checklist written; OQ2 and OQ3 answered by their owners.
- **Skills:** `marlo-migration`, `marlo-commit`.

### T14 — Documentation
- **Status:** pending · **Size:** S · **Depends on:** T11 · **Module:** docs
- **Requirements:** proposal Part D; requirements §4 (PRD/TRD pending item).
- **Design refs:** §2 (docs); requirements §4.
- **Files touched:**
  - `docs/specs/epic/phase-topology/agent-context.md` §1: `name` is now a label only; the type comes from `Phase` methods;
  - `reports/ai-context/save-validation-matrix.md` and `interceptor-validator-playbook.md`, where they name the old methods (grep first);
  - a pending-item note for PRD `:157, 214` and TRD `:170, 570` in `execution.md`. They are not edited here (shared-file rule).
- **Tests:** none.
- **Verification:** `grep -rnE "isPOWB|isReportingActive|isUpKeepActive|isPlanningActive|isProgressActive|isAWPBActive" docs reports` → only historical spec text.
- **Falsifier:** leave one ai-context mention → the grep shows it.
- **Red run:** n/a — documentation only, no executable check beyond grep.
- **Disqualifier:** editing PRD / TRD / `CLAUDE.md` / `AGENTS.md` on this branch violates the shared-file rule.
- **Consumers:** agents reading those docs.
- **Review:** checklist — documentation text.
- **Done when:** grep clean; pending item recorded.
- **Skills:** `cognitive-doc-design`, `marlo-commit`.

### T15 — Manual QA and release HITL
- **Status:** pending · **Size:** S · **Depends on:** T08, T13 · **Module:** none
- **Requirements:** AC FN-008 (human part), AC OPS-002 (record), OQ1, OQ4 (part 2); defect class D8 / D10.
- **Design refs:** §16; §14 S5.
- **Scope:**
  - On a `zz_*` copy with an AICCRA_III progress phase opened, check the deliverable participants section: a cluster submitted in the plan phase is editable, with no label and no note. AICCRA's progress phase is unchanged.
  - Confirm OQ1 with the owner.
  - Run the OQ4 part 2 checks and the draft listing; record both.
- **Tests:** manual.
- **Verification:** screenshots and the listing recorded in `execution.md`.
- **Falsifier:** run the check on a plan phase → the lock must still be there; if not, INT-2 leaked.
- **Red run:** n/a — manual HITL.
- **Disqualifier:** a check done on the shared local DB with the phase opened for everyone, or done by reading the template, is not evidence.
- **Consumers:** release.
- **Review:** checklist — human verification record.
- **Done when:** every item recorded and confirmed by the owner.
- **Skills:** `playwright-cli`.

## 4. Dependency Graph

```
T00 ─┐
T01 ─┼─► T03 ─► T04 ─► T05 ─► T06 ─────────────┐
T02 ─┘          │                              ├─► T11 ─► T13 ─► T15
                └─► T07 ─► T08 ─► T09 ─► T10 ──┘     ▲
T03 ─► T12 ──────────────────────────────────────────┘
T11 ─► T14
T08 ─► T15
```

T02 can start immediately. T07 needs the app, so schedule it when no other session builds. T12 can run in
parallel with T05–T10.

## 5. Testing Plan

### Unit
- `BaseActionPhaseTypeCharacterizationTest` (T02).
- `PhaseTypeEquivalenceTest` (T03).
- `PhaseNameSiteTruthTableTest` (T05, T06).
- `GlobalUnitCreationPhaseNamingTest` (T12).

### Integration / rendered
- The HTML diff harness (T07–T10): the reference set in plan, progress and reporting phases of units 45 and 47, plus a page without a global unit.

### Data
- The 8-scenario dry runs (T01, T13).
- The production read-only checks (T00, T15).

### Regression (manual)
- T15: the INT-2 page; plan and reporting deliverable pages; project submission email; POWB synthesis access.

## 6. Operational Steps

- **Order:** M1 can ship first. M2 ships in the release after T11 and T12 are merged, never before (OPS-001).
- **Before M2:**
  - OQ2: BI and CLARISA owners confirm whether they read `phases.name`;
  - OQ3: QA updates `qa_innovations` to accept `Reporting`;
  - API consumers: none (REST unused).
- **At the M2 deploy:**
  - with the app stopped, list the drafts (OPS-002);
  - capture `(id, name)` (OPS-004);
  - start the app;
  - Flyway applies M2;
  - check `flyway_schema_history`.
- **Concurrency:** no Maven build or `run-marlo-java17.sh` while another session works in this checkout.

## 7. Rollback Plan

- **Code:** revert per task commit. Every task leaves the app working, because old names delegate until T11.
- **M1:** data repair; not reverted. If ever needed, restore from the captured ids.
- **M2:** restore each row from the captured `(id, name)` list (OPS-004). Never by cutoff and type (FJ-1).

## 8. Definition of Done

- [ ] Every coverage-map row closed by its task, with the evidence quoted in `execution.md`.
- [ ] Every falsifier observed red and quoted; every predicted red replaced by the observed one.
- [ ] Compile gate, Checkstyle delta (with the new rule) and the test suite with `-am` green at the end.
- [ ] HTML diffs clean for T08–T10, except INT-2.
- [ ] Both migrations pass their 8 scenarios; `marlo-migration` checklist reported.
- [ ] OQ1–OQ4 answered and recorded.
- [ ] Constitutional checklist of `requirements.md` §7 confirmed in the PR.
- [ ] `575a1c7aff` either on `origin/staging` or removed from the branch before the PR.

## 9. Estimate and PR Strategy

| Task | LOC (approx.) |
|---|---|
| T00, T15 | 0 (records) |
| T01 | 15 |
| T02 | 200 |
| T03 | 120 |
| T04 | 60 |
| T05 | 150 |
| T06 | 170 |
| T07 | harness, not shipped |
| T08 | 120 |
| T09 | 100 |
| T10 | 80 |
| T11 | 80 |
| T12 | 120 |
| T13 | 25 |
| T14 | 30 |
| **Total shipped** | **~1,270** |

**PR strategy:** about 1,270 LOC, above the ~400-LOC guideline. Five chained PRs to `staging`:
1. PR 1: T01 (M1), independent.
2. PR 2: T02–T05 (contract, tests, name-literal sites).
3. PR 3: T06–T10 (call-site migration, HTML-diff evidence).
4. PR 4: T11 + T12 + T14 (removal, rule, creation, docs).
5. PR 5: T13 (M2), released last.

Each PR description says what to review first and links the previous and next PR.
