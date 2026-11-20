# Partner request routing per Global Unit (CLARISA or MARLO) — Execution Log

## Document Control

| Item | Value |
|---|---|
| Spec ID | CHG-PARTROUTE-001 |
| Spec path | `docs/specs/changes/partner-request-routing-specificity/` |
| Branch | `revive-local-partner-request-approval` |
| Approval Mode | gated (requirements Document Control) |
| Started | 2026-11-20 |
| Leader | Claude Code session, Opus (T1) |
| Implementer | `akili-implementer` wrapper → `sonnet` (T2) |
| Reviewer | `akili-reviewer` wrapper → `opus` (T3), tools `Read, Grep, Glob` |
| Budget (design §15) | 5 tasks · ~470 LOC · 7 review rounds |

**Persona drift (recorded before the first spawn).** `akili doctor --agents` (akili-specs v2.45.0) reports all
four personas in `.agents/` as `UNMARKED` (no section markers). The deployed `implementer.md` does not state the
60-call checkpoint bound. `--fix` was not run: personas are constitutional files and this is a spec branch
(CLAUDE.md *Shared-File Write Discipline*). The drift was reported to the user, and is a pending item for `staging`.

## Task Execution History

### CHG-PARTROUTE-001-T01 — Specificity seed: migration, constants, parity test

- **Final status:** PASS
- **Date:** 2026-11-20
- **Implementer attempts:** 1
- **Effort:** medium · **Skills:** `marlo-migration`, `marlo-verify` (the task's `marlo-commit` is kept by the Leader,
  who commits)

**Attempt 1**

- Files changed:
  - `marlo-web/src/main/resources/database/migrations/V2_6_0_20261120_1306__AddPartnerRequestsToClarisaSpecificity.sql` (new)
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java`
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java`
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/projects/PartnerRoutingSpecificityParityTest.java` (new, 4 tests)
- Implementer verification:
  - Clean compile exit 0: 2431 files in marlo-data, 1065 in marlo-web, 147 test files.
  - `mvn -o -pl marlo-web -am test`: 1364 run, 0 failures, 0 errors.
  - Checkstyle delta 0. Both `APConstants.java` are LF and stayed LF.
- DB runs (throwaway schemas, source `aiccradb` read only, because `aiccradb1` does not exist locally):

  | Run | `parameters` rows for the key | `custom_parameters` |
  |---|---|---|
  | Copy (types 1–5), before | none | 1616 |
  | Copy, run 1 | 1 per type 1–4 | 1616 |
  | Copy, run 2 | 1 per type 1–4 | 1616 |
  | Empty schema, no types, runs 1 and 2 | 0, no error | n/a |
  | Empty schema, types 1, 2, 3, 5, runs 1 and 2 | 1 per type 1, 2, 3 | 0 |

  Throwaway schemas `partroute_t01_tmp` and `partroute_t01_empty` dropped; `SHOW DATABASES LIKE 'partroute%'` empty.
- Red run: baseline `grep -c partner_requests_to_clarisa` → 0 in both `APConstants.java` and in every migration
  (observed at `0edf846105` by the spec). The parity test was not run against the pre-change tree; falsifier (a) is
  its observed red.
- Falsifiers (observed red, then green after revert):
  - (a) marlo-data constant `partner_request_to_clarisa` →
    `ComparisonFailure: ../marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java PARTNER_REQUESTS_TO_CLARISA expected:<partner_request[s]_to_clarisa> but was:<partner_request[]_to_clarisa>`
  - (b) `User's` in the description →
    `AssertionError: the description holds no apostrophe: When on, partner requests … User's Country-office requests always stay in MARLO …`
  - (c) `NOT EXISTS` dropped → second run returns `1 2`, `2 2`, `3 2`, `4 2` (2 rows per type).
- Evidence re-run (Leader inline, non-author): clean compile exit 0 with 2431 / 1065 / 147 files; full suite
  `mvn -o -pl marlo-web -am test` → `Tests run: 1364, Failures: 0, Errors: 0, Skipped: 0`, BUILD SUCCESS →
  **VERIFIED**.
- Reviewer verdict: **PASS**. The description matches design §3 word for word; the expected key is a literal in the
  test (Disqualifier respected); the four properties are asserted and each falsifier turns them red; the migration
  assumes no ids, is DML only and idempotent, and its filename follows AGENTS.md.
- runtime events: none
- spawns: implementer 27 calls, 128577 tokens, ended complete; reviewer 9 calls, 51440 tokens, ended complete

**ADVISORY (4R, not gating)**
- Reliability: the parity test counts every migration that contains the quoted key. A future description-rewrite
  migration for this key (a precedent exists: `V2_6_0_20260925_1543`, `V2_6_0_20260928_0748`) would turn it red
  without a real defect. Counting only `INSERT INTO parameters` would be narrower.
- Readability: the `NOT EXISTS` assertion checks only that the text is present, not that it filters on type and key.
  The DB run covers the behaviour.
- Risk: the apostrophe regex depends on the tail `1, 'false', 2`, so a later change to the format, default or category
  fails with a less clear message.

**Not Done / Assumptions (Implementer, verbatim summary)**
- The first version of the apostrophe check passed falsifier (b) wrongly, because a stray `'` ended the regex early.
  It was rewritten to anchor on `', <n>, 'false', 2 FROM` and then went red.
- The migration assumes no Global Unit, user or parameter id. A missing type gets no row.
- `rm -rf` of `target/` sometimes printed "Directory not empty", so the wipe was repeated. A `-pl marlo-web` run
  without `-am` once corrupted the generated mapstruct sources, and a full wipe fixed it.

No owed item and no blocker, so this is not a continuation.

- **Requirements covered:** MIG-001 and its AC, NF-001 (constants) and the second clause of AC NF-001.
- **Decisions made:** the parity test lives in `action/projects/` per design §2. Falsifiers (a) and (b) were run
  through JUnitCore on pre-compiled classes, because repeated incremental Maven runs reported spurious
  `APConfig not found` errors (a dirty-tree symptom, see memory *no-concurrent-maven-builds*).
- **Issues encountered:** `aiccradb1` is absent locally, so `aiccradb` was the source of the copy.
- **Budget running totals:** tasks 1 / 5 · LOC 148 / ~470 · review rounds 1 / 7. Within budget.
- **Final verification:** compile and the full suite green (Leader re-run), Checkstyle delta 0, DB counts as above.

### CHG-PARTROUTE-001-T02 — Routing in `PartnersSaveAction` with both-mode tests

- **Final status:** PASS (two parallel lens Reviewers, both PASS)
- **Date:** 2026-11-20
- **Implementer attempts:** 1
- **Effort:** xhigh (security surface, external send, many failure paths) · **Skills:** `marlo-verify`,
  `error-handling-patterns`, `tdd` (`marlo-commit` kept by the Leader)

**Attempt 1**

- Files changed:
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/projects/PartnersSaveAction.java` (+166 / −15)
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/projects/PartnersSaveActionRequestTest.java` (+35 / −8)
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/projects/PartnersSaveActionClarisaModeTest.java` (new, +585)
- What was built:
  - FN-010 guard at the routing point, after the existing type / country guard and before any store or send.
  - `readClarisaMode()`, plus the `clarisaMode` field read through `isClarisaMode()`.
  - `sendToClarisa()`, `buildClarisaPayload()` and the `protected int postToClarisa(url, json)` seam. The seam uses
    Basic auth, JSON headers, named 10 / 10 / 30 s timeouts and try-with-resources.
  - Log lines exactly as in design §11.
  - The MARLO branch is unchanged.
  - DD-5: the pin was rewritten, and `testMarloModeNeverCallsClarisaEvenForAnAiccraUnit` was added.
- Implementer verification:
  - Clean compile exit 0: 2431 / 1065 / 148 test files.
  - Suite: 1382 run, 0 failures, 0 errors.
  - `grep -c "isAiccra()"` → 0 and `grep -cE "\"(Cluster|AICCRA|aiccra)\""` → 0 on `PartnersSaveAction.java`.
  - Checkstyle delta 0 (HEAD 0, tree 0). All files LF.
- Red run:
  - Baseline `grep -ci clarisa PartnersSaveAction.java` → 0 at `0edf846105` (spec).
  - The new tests were written **after** the code, so there was no test-first red. The falsifier reds below are the
    red evidence.
- Falsifiers (each observed red, then reverted; the final full suite ran after all reverts):
  - (a) `if (!clarisaMode)` → 22 failures and errors across both suites, e.g.
    `testAiccraUnitSavesTheOriginalAndTheEditableCopyAndAlertsSupport expected:<[success]> but was:<[input]>` and
    `testClarisaModeSendsOnceAndStoresNothing: the seam is called once expected:<1> but was:<0>`.
  - (b) `this.hasSpecificities(...)` → `testStoredFalseWinsOverATrueInTheSession: the seam is never called`, + 9.
  - (c) `e` passed to `LOG.error` → `testTimeoutIsAVisibleFailureAndItsMessageStaysOutOfTheLog: no throwable is
    attached expected null, but was:<ThrowableProxy…>`. `testConnectionFailureIsAVisibleFailureLoggedByClassName` fails
    the same way.
  - (d) hardcoded `"aiccra"` → `testPayloadAcronymIsTheLowerCaseAcronymOfTheUnit: expected:<aiccra[_iii]> but
    was:<aiccra[]>`; `testPayloadSourceIsTheGlobalTextForAUnitWithoutAnOverride` (`expected:<[pab]ra> but was:<[aicc]ra>`).
  - (e) **stand-in:** "Cluster C" replaced by "Project P" in the source text →
    `testPayloadCarriesEveryFieldAndTheUnitsOwnSourceText: expected:<Partner request [Cluster C]77 - …> but
    was:<Partner request [Project P]77 - …>`. The literal mutation "build the source from the global texts only" cannot
    be applied in production code, because the action never chooses a bundle; `getText` does. Reviewer 1 judged the
    stand-in adequate: it proves the assertion tells the unit's wording apart from the global one, and the global-only
    case is a separate test.
  - (f) store before branching → `testClarisaModeSendsOnceAndStoresNothing: CLARISA mode: the manager must not be
    called`, + 11.
- Test → gate map:
  - D1: `testClarisaModeSendsOnceAndStoresNothing`, `testMarloModeAiccraUnitStoresOnceAndNeverCallsClarisa`,
    `testMarloModeNeverCallsClarisaEvenForAnAiccraUnit`.
  - D2: `testPayloadCarriesEveryFieldAndTheUnitsOwnSourceText` (real `custom/aicrra.properties`, URL and 11 fields as
    literals), `testPayloadAcronymIsTheLowerCaseAcronymOfTheUnit` (unit 47),
    `testPayloadSourceIsTheGlobalTextForAUnitWithoutAnOverride`, `testPayloadSourceFallsBackWhenThePageHadNoItem`.
  - D3: `testRejectionIsAVisibleFailureWithOneWarning` (400), `testConnectionFailureIsAVisibleFailureLoggedByClassName`,
    `testTimeoutIsAVisibleFailureAndItsMessageStaysOutOfTheLog`, `testMissingConfigurationFailsWithoutCallingClarisa`,
    `testUnitWithoutAcronymFailsWithoutCallingClarisa`, `testNoUnitStoresNothingSendsNothingAndLogsOnce`.
  - D4: `onlyEvent()` used by every failure test. It asserts one event on the action's logger, no throwable, and no
    email, username, password or `Authorization`. The exception messages carry those values.
  - D11: `testStoredFalseWinsOverATrueInTheSession`, `testAnythingButTrueInTheStoredValueIsMarloMode` (`"TRUE "`, `yes`,
    `""`, null), `testATrueOnAnInactiveRowIsMarloMode`, `testIncompleteRowsAreSkippedAndAMissingRowIsMarloMode`.
  - `isClarisaMode`: `testThePageSeesTheModeThatHandledTheSubmit`.
- Evidence re-run (Leader inline, non-author):
  - Clean compile exit 0, 2431 / 1065 / 148.
  - `mvn -o -pl marlo-web -am test` → `Tests run: 1382, Failures: 0, Errors: 0, Skipped: 0`, BUILD SUCCESS.
  - Both greps → 0.
  - Result: **VERIFIED**.
- Reviewer verdicts: parallel lens mode, because the effort is xhigh and the task touches a security surface.
  - **Lens 1, conformance + reliability / resilience: PASS.** Routing, FN-010, `readClarisaMode()` and the §11 lines
    match the spec. D1–D4, D11 and DD-5 are covered, and no Disqualifier applies. Leader questions:
    - (1) `postToClarisa` reads the credentials a second time. That is forced by the task's `(url, json)` signature
      and design §10's "Basic auth from `config…`". The check-once goal holds, because the second read comes after
      the non-blank check, so the P-16 getters cannot log.
    - (2) The (e) stand-in is adequate.
    - (3) The D2 assertions are literals.
  - **Lens 2, conformance + risk / security / readability: PASS.**
    - SEC-002, OPS-001 (six lines matching §11, one event, no throwable) and NF-001 hold.
    - D4 is strong: the appender sits on the action's logger and is restored afterwards.
    - Timeouts and try-with-resources are present.
    - Every log argument is null-safe.
- runtime events: none
- spawns: implementer 27 calls, 202989 tokens, ended complete; reviewer (lens 1) 17 calls, 90892 tokens, ended
  complete; reviewer (lens 2) 15 calls, 80889 tokens, ended complete

**ADVISORY (4R, not gating)**
- Risk, SEC-002 in shared logging: the root logger in `logback.xml` is set to `ALL`, and nothing caps `org.apache.http`.
  **Leader probe:** `mvn dependency:list` on marlo-web shows `commons-logging:1.2` and `httpclient:4.5.3`, with **no**
  `jcl-over-slf4j`. So HttpClient logs through JUL at its default INFO level, and its DEBUG header and wire output does
  not reach logback by default. The `log4j-jul` bridge is present, but it is only active if
  `java.util.logging.manager` is set. Low risk, and it predates this diff. It is not live-verified; T05 can grep the
  log for `Authorization` after the live call.
- Reliability: `org.json` `put(key, null)` drops the key. With no website, type or user, the body omits that field
  instead of sending null. Check against what CLARISA expects (P-10).
- Reliability: the two existing "Failed to get projectInfo" `LOG.error` lines in `save()` run before routing. One
  submission can therefore produce two events on the action's logger. This is outside the OPS-001 count.
- Resilience: the Basic-auth header building in `postToClarisa` is exercised by no unit test, since every suite
  overrides the seam. T05's live call is its only check. Timeouts are present but untested (recorded gap).
- Reliability: two active rows for the key would make the mode depend on `HashSet` order. A unique (unit, parameter)
  constraint likely prevents that; this is a T04 note.
- Readability: `CLARISA_DEFAULT_SOURCE` fills both the `requestSource` fallback and `externalUserComments`, but its
  comment names only one use. The no-acronym check uses `isBlank`, which is stricter than "null".

**Not Done / Assumptions (Implementer)**
- Timeouts are named constants only and are not unit-tested (recorded gap, per the Disqualifier).
- `postToClarisa` re-reads the username and password from `config` (see Lens 1, answer 1).
- Basic auth is built with `Base64` instead of the deprecated `BasicScheme.authenticate`. The header content is the
  same.
- A blank acronym counts as no acronym.
- A null-unit MARLO-mode request is no longer stored with a null unit (FN-010, NF-002's stated exception).

No owed item and no blocker, so this is not a continuation.

- **Requirements covered:** FN-001…FN-006, FN-010, SEC-002, OPS-001, NF-001 (no `isAiccra`, no literal, constant
  used), NF-002.
- **Decisions made:** parallel lens review, because the effort is xhigh and the task touches a security surface
  (Step 2.3 table).
- **Issues encountered:** two falsifier runs, (b) and (d), first failed on a dirty-tree compile error. A full wipe and
  rerun produced the reds quoted above.
- **Budget running totals:** tasks 2 / 5 · LOC **957** (T01 148 + T02 809) against ~470 · review rounds 3 / 7 (T01: 1,
  T02: 2 lens verdicts). **LOC TRIPWIRE EXCEEDED, see the block below.**
- **Final verification:** compile and the full suite green (Leader re-run), greps 0, Checkstyle delta 0.

## Budget Tripwire: CHG-PARTROUTE-001-T02

- **Measure:** LOC. Estimate ~470 for the whole spec (design §15: Java ~130, tests ~270, SQL ~35, FTL ~12, docs ~25).
  Actual after 2 of 5 tasks: **957**, a delta of +487, about 2×.
- **Cause:**
  - The tests: T02 alone added 620 test lines against the ~270 test estimate for the whole spec.
    `PartnersSaveActionClarisaModeTest` is 585 lines with about 20 cases. Each case maps to a row the gate map
    requires (D1–D4, D11, `isClarisaMode`), and each carries the literal payload and log-text assertions the
    Disqualifiers demand.
  - Java: +166 / −15 against ~130.
  - T01's parity test (~120 lines) was also unbudgeted beyond the "~270 tests" line.
- **Other measures:** tasks 2 / 5 and review rounds 3 / 7 are within budget.
- **Remaining estimate:** T03 (FTL ~12, plus static pins in the parity test of about 60), T04 (docs ~25), T05 (no
  code). Projected total about 1,050 LOC.
- **Status:** stopped for the owner. Per `/akili-execute` §2.4, *"No mandate widens pre-approval … the budget
  tripwire … still stop"*. Not auto-approved.
- **Owner decision (2026-11-20):** the overrun is accepted ("sí, acepta el sobrecosto, haz el commit y continúa con
  T03"). The new working budget is about 1,050 LOC for the spec, the projection above. Review rounds and tasks are
  unchanged.

### CHG-PARTROUTE-001-T04 — Admin agent-context: the two modes

- **Final status:** PASS
- **Date:** 2026-11-20
- **Implementer attempts:** 1 (ran in parallel with T03: disjoint files, no build in T04)
- **Effort:** low · **Skills:** `cognitive-doc-design` (`marlo-commit` kept by the Leader)

**Attempt 1**

- Files changed: `docs/specs/domain/admin/agent-context.md` (+35 / −4). The § *Partner Requests* intro was rewritten,
  a new `### Routing mode (partner_requests_to_clarisa)` subsection was added, and the "Ask for a partner" flow row
  was qualified.
- Red run (sweep before editing): `grep -n "CLARISA\|every Global Unit"` → 2 hits. The variant sweep (`never sent`,
  `only destination`, `always stored`, `no CLARISA`, `isAiccra`) → 0.
- Sweep hits and actions:
  - `:106` `CLARISA_ACRONYM_PMU` (REST v2): kept, because it is unrelated.
  - Old `:278` "no CLARISA call is made on this path": **rewritten**. It now reads "no CLARISA call on the review
    path; in CLARISA mode the request is POSTed to CLARISA at submit and never stored". This was the absolute claim
    the falsifier targets.
  - `:315` flow row: qualified to "MARLO mode; CLARISA mode sends instead".
  - "every Global Unit": 0 hits before and after.
- Code citations: `PartnersSaveAction.java` `:292`, `:503-509`, `:511-513`, `:521`, `:582`, `:605-617`, `:639`, and
  `APConstants` `:135` / `:159`. The templates are cited by name and design §5 / DD-6 only, because T03 was editing
  them at the time.
- Evidence re-run (Leader inline): both sweeps reproduce. The sampled citations (`:292`, `:503`, `:511-513`, `:521`,
  `:582`, `:605`, `:639`) point at the stated code → **VERIFIED**.
- Reviewer verdict: **PASS**. All six scope items are present and cited, and no sentence contradicts the spec. The
  behaviour claims match `b7a564a475`. The sweep covers the old absolute claims, so the Disqualifier does not apply.
  The template behaviour is left to T05's live check.
- runtime events: none
- spawns: implementer 9 calls, 96255 tokens, ended complete; reviewer 7 calls, 45116 tokens, ended complete
- **Not Done / Assumptions (Implementer):** template line numbers are not cited, since T03 was editing those files;
  "menu hidden in CLARISA mode" is taken from DD-6 and FN-009. Neither is an owed item.
- **Requirements covered:** FN-008 (documentation); proposal §5 "Docs".
- **Decisions made:** T04 ran in parallel with T03 (Step 1 *Parallel Execution*: disjoint write sets, and T04 runs no
  build). The Reviewer's advisory from T02 about "two active rows" was **not** added, because it is not in T04's
  scope (*Advisory Never Becomes A Task*).
- **Budget running totals:** tasks 3 / 5 · LOC 996 (T01 148 + T02 809 + T04 39), within the owner-accepted ~1,050 ·
  review rounds 4 / 7.
- **Final verification:** sweep clean; citations verified.

### CHG-PARTROUTE-001-T03 — Templates: menu entry, office link, failure text

- **Final status:** PASS
- **Date:** 2026-11-20
- **Implementer attempts:** 1 (ran in parallel with T04)
- **Effort:** medium · **Skills:** `marlo-verify` (`marlo-commit` kept by the Leader)

**Attempt 1**

- Files changed (FTL +8 / −4, test +43):
  - `menu-superadmin.ftl`: both `institutions` entries end in `&& !action.hasSpecificities('partner_requests_to_clarisa')`.
    The original `?then` ternary is wrapped in parentheses so that the `&&` binds to all of it.
  - `projectPartners.ftl`: `[#if action.hasSpecificities('partner_requests_to_clarisa')]` wraps the office help. The
    CLARISA branch holds `country.email` + `mailto:MARLOSupport@cgiar.org` + "."; the `[#else]` branch keeps today's
    `#requestModal` trigger.
  - `partnersSave.ftl`: `[#if action.clarisaMode]` → `sendFailed`, otherwise `saveFailed`.
  - `PartnerRoutingSpecificityParityTest.java`: 3 static pins (the class now has 7 tests). Expected strings are literals.
  - No `.properties`, JS, CSS or struts change.
- Implementer verification:
  - Compile exit 0 and suite 1385 / 0 / 0.
  - Greps: `partner_requests_to_clarisa` 2 in the menu and 1 in `projectPartners.ftl`; `sendFailed` 1; no `struts-*.xml`
    in the diff.
  - Checkstyle delta 0.
- Red run: baseline at `0edf846105`, `grep -c partner_requests_to_clarisa` → 0 in all three templates, and `sendFailed`
  → 0 (spec).
- Falsifiers (observed red, then green after revert):
  - (a) Condition removed from one `institutions` entry → `bothInstitutionsMenuEntriesAreHiddenInClarisaMode:
    AssertionError: the institutions entry carries the condition`. (The task's wording is "edit only the `[#else]`
    branch"; the pin requires both entries, so either form goes red.)
  - (b) Trigger moved into the CLARISA branch → `theOfficeHelpKeepsTheRequestTriggerOutOfTheClarisaBranch:
    AssertionError: CLARISA branch has no request trigger`.
  - (c) `sendFailed` replaced by `requestFailed` → `theRequestPageReadsBothFailureKeysByMode: AssertionError:
    sendFailed is referenced`.
- Evidence re-run (Leader inline, non-author):
  - Clean compile exit 0 (2431 / 1065 / 148), and the greps reproduce.
  - First full suite, run under extreme machine load (load average about 115–280, 17½ minutes): 1385 run, **2
    failures and 1 error, all in `OtpMailSenderTest`**: `theCodeLineFollowsTheFlagReadForThatSend expected:<0> but
    was:<1>`, `successDeliversOnceToTheSubmittedRecipientAndLogsDelivered expected:<1> but was:<11>`, and an error in
    `aFullQueueLogsEmailFailedAndTheCallerIsNotAffected`. T03 does not touch that class, and its assertions depend on
    timing (async mail queue).
  - Diagnosis: `OtpMailSenderTest` run alone → `Tests run: 43, Failures: 0, Errors: 0`.
  - Second full suite on the same clean-compiled classes → `Tests run: 1385, Failures: 0, Errors: 0, Skipped: 0`,
    BUILD SUCCESS.
  - Result: **VERIFIED**. The first-run mismatch was load-induced flakiness in an unrelated class, not caused by the
    change, so no rework attempt was charged.
  - A session restart (Orca) interrupted the diagnosis between the two runs. Nothing was lost: the tree was intact
    and no Maven process was left running.
- Reviewer verdict: **PASS**.
  - The CLARISA office markup matches design §5.
  - The `visible` expression is valid FreeMarker, with the same edit in both branches.
  - FN-010 shows `saveFailed`, because the action returns before `clarisaMode` is set (a primitive `false`).
  - The pins use literals and catch (a)–(c).
  - No guard or struts change (SEC-001). The Disqualifier gap (no render proof) belongs to T05.
- runtime events: none on the attempt (the Leader's re-run was interrupted by a session restart and resumed)
- spawns: implementer 11 calls, 100127 tokens, ended complete; reviewer 9 calls, 50062 tokens, ended complete
- Completion line: `Design route: none — no playbook run`

**ADVISORY (4R, not gating)**
- Reliability: `theRequestPageReadsBothFailureKeysByMode` would still pass if the two branches were swapped. Asserting
  the exact `[#if action.clarisaMode]…sendFailed…[#else]` text would close that.
- Reliability: the office pin cuts at the first `[#else]` / `[/#if]`. A nested `[#if]` added to the CLARISA branch
  later would shift the cut. Not reachable today.
- Readability: `MARLOSupport@cgiar.org` is literal in the template, restored verbatim by design §5. It is an address,
  not an i18n string.
- Flakiness: `OtpMailSenderTest` is timing-sensitive under heavy load. That is outside this spec, recorded for kaizen
  at archive.

- **Not Done / Assumptions (Implementer):** falsifier (a) was applied to the first entry, not only the `[#else]` one;
  the static pins do not prove rendering (T05). Neither is an owed item.
- **Requirements covered:** UI-001, UI-002, UI-003, SEC-001 (static); behaviour is proven in T05.
- **Budget running totals:** tasks 4 / 5 · LOC 1,047 (T01 148 + T02 809 + T04 39 + T03 51), within the
  owner-accepted ~1,050 · review rounds 5 / 7.
- **Final verification:** compile and the full suite green (second Leader run), greps reproduce, Checkstyle delta 0.
