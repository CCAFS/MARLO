# Revive in-MARLO approval of partner requests — Execution Log

## Document Control

| Item | Value |
|---|---|
| Spec ID | CHG-PARTREQ-001 |
| Spec path | `docs/specs/changes/revive-local-partner-request-approval/` |
| Branch | `revive-local-partner-request-approval` (from `staging`; spec commit `eb982ae8d6`) |
| Approval Mode | gated |
| Leader | Claude Code session, `opus` (T1) |
| Implementer / Reviewer wrappers | `akili-implementer` (`sonnet`, T2) / `akili-reviewer` (`opus`, T3) |
| Started | 2026-11-19 |

## Task Execution History

### CHG-PARTREQ-001-T01 — Transactional manager, locking DAO and outcome type

- **Final status:** PASS
- **Date:** 2026-11-19
- **Attempts:** 1
- **Effort:** xhigh (transaction and lock semantics; correctness-critical)
- **Skills assigned:** `marlo-verify`, `error-handling-patterns`, `tdd` (task list; `marlo-commit` kept with the Leader, since the owner approves every commit)
- **Review mode:** parallel lens Reviewers (effort xhigh, data-integrity surface): RELIABILITY + RISK, each with baseline spec conformance

#### Attempt 1

- **Files changed:**
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/PartnerRequestDAO.java` (CRLF kept)
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/mysql/PartnerRequestMySQLDAO.java` (CRLF kept)
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/manager/PartnerRequestManager.java` (CRLF kept)
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/manager/impl/PartnerRequestManagerImpl.java`
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/model/PartnerRequestOutcome.java` (new, GPL header)
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/data/manager/impl/PartnerRequestManagerGuardTest.java` (new, 14 tests)
- **Baseline (red run, observed before editing):** `grep -c "acceptPartnerRequest\|createPartnerRequest\|findForUpdate"` on the manager and DAO interfaces → `0`, `0` (greenfield prediction held).
- **Implementer verification:**
  - Clean compile (`marlo-verify` `clean-compile.sh`): BUILD SUCCESS, 2431 marlo-data, 1064 marlo-web, 142 test files.
  - `mvn -o -pl marlo-web -am test -Dtest=PartnerRequestManagerGuardTest -DfailIfNoTests=false` → 14 run, 0 failures (`-DfailIfNoTests=false` needed because upstream modules have no tests).
  - Full suite `mvn -q -o -pl marlo-web -am test` → 1313 run, 0 failures, 0 errors, exit 0.
  - Checkstyle 8.18 on the 6 files: 0 at HEAD, 0 in tree, delta 0.
  - Hygiene: no FIX findings; CRLF preserved. REVIEW `LOG-NULL` (`existing.getId()` in a branch where `existing` is non-null) and pre-existing `NULL-LIST` on `findAll` — no action.
- **Falsifiers (executed against post-change code, each reverted, final run green):**
  1. Pending check removed in `acceptPartnerRequest` → `acceptTwiceCreatesOneInstitution`, `acceptRefusesWithoutWritingAnything`: `java.lang.AssertionError: expected:<ALREADY_HANDLED> but was:<DONE>`.
  2. Institution built from the original → `acceptBuildsTheInstitutionFromTheCopyAndClosesBothRows`: `org.junit.ComparisonFailure: expected:<[Edited] Name> but was:<[Original] Name>`.
  3. Locks in caller order, fixture `[9, 3]` → `officeLocksAreTakenInAscendingIdOrder`: `expected:<[3, 9]> but was:<[9, 3]>`; `officeMethodsRefuseEachIdThatIsNotAPendingOfficeOfTheInstitution`: `expected:<[3, 4, 5, 6, 20, 99]> but was:<[20, 99, 6, 5, 4, 3]>`; `officeAcceptAddsOnlyTheCountriesThatAreNotThereYet`: `expected:<3> but was:<9>`.
- **Evidence re-run (Leader-inline, non-author):** `rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes marlo-web/target/generated-sources && mvn -o -pl marlo-data,marlo-web -am test -Dtest=PartnerRequestManagerGuardTest -DfailIfNoTests=false` → exit 0; Compiling 2431 / 1064 / 142; `Tests run: 14, Failures: 0, Errors: 0, Skipped: 0`. **VERIFIED.**
- **Reviewer verdicts:**
  - RELIABILITY lens: **PASS**. The six methods match design §6 (method table, "copy" / "pending", ascending lock order, `SKIPPED_EXISTING`) and today's field assignments; no self-invocation bypasses the proxy; existing methods unchanged; every refusal branch executed; double arguments asserted, no echo fixtures. The Implementer's six judgment calls were checked against the spec, and none contradicts an approved clause.
  - RISK lens: **PASS**. The DAO lock + refresh, the outcome type (never serialised) and the six transactional methods match DD-2/3/4. Each new DAO has exactly one `@Named` implementation, and no `new PartnerRequestManagerImpl(` exists outside the test. No deadlock path found within the task. Rollback and locking are not proven, which is T04's job (R-8).
- **runtime events:** none
- **spawns:** implementer 54 calls, 222147 tokens, ended complete; reviewer (reliability) 23 calls, 94383 tokens, ended complete; reviewer (risk) 24 calls, 99057 tokens, ended complete

#### ADVISORY (4R, non-gating)

- **Repeated country in one batch.** It works only because `institution_locations` uses an `identity` generator: `findByLocation` runs under `FlushMode.COMMIT`, yet still sees the earlier insert. No unit test covers it, because the double never registers saved locations. **Behaviour change:** the repeat now closes as `acepted=false` plus a justification, where today it closes as `acepted=true`. This is endorsed by design §6; mention it in the release note.
- **Empty or unknown edit.** An edit with no values, an unknown type id, or an unknown ISO code returns `DONE` and writes nothing. Today the action would set null.
- **Justification-only edit blanks text fields.** This is inherited from today's form contract. The interface Javadoc omits the "only when something is edited" condition.
- **`acceptPartnerRequest` may throw `IllegalStateException`** (copy with no type or country). The rollback is correct, but the action must catch it.
- **OpenSessionInView after rollback.** After a rollback, Spring clears the session, so the entities loaded in `prepare()` are detached. An action that renders after a failure can then hit `LazyInitializationException`. Actions should redirect or reload instead.
- **Unlocked original.** `original.setActive(false)` merges a possibly stale parent row. The original is never edited, so the exposure is low. Worth a line in `agent-context.md` (T12), next to R-7.
- **`findForUpdate` issues two queries** when the row is not in the session: a plain load, then a locking refresh. T04 should read the SQL log with that in mind.
- **Test quality.** In `createSavesTheOriginalThenTheLinkedCopy`, the `assertSame` depends on the double returning its input. `editChangesTheCopy…` and `rejectRecords…` do not assert `locks`, although the lock is asserted elsewhere.
- **Javadoc.** `PartnerRequestOutcome.getInstitution()` mentions only the office accept, but `rejectOfficeRequests` fills it too.

#### Forward pointers (to be copied into later briefs)

- **→ T03 (`addPartner`):** catch exceptions from `acceptPartnerRequest`, including `IllegalStateException`. After a failure, redirect rather than render the session's entities (OpenSessionInView rollback clears the session).
- **→ T04:** read the SQL log knowing `findForUpdate` issues a plain load before the `for update` refresh. Add a live check: two pending office requests for the same country in one batch give `DONE` + `SKIPPED_EXISTING` and one saved location.
- **→ T05 (edit):** `editPartnerRequest` returns `DONE` for an empty or unknown-value edit. The action maps statuses only, so no extra handling is required by the spec. Note it for T11a.
- **→ T12:** add the unlocked-original note and the batch-repeat behaviour change (`acepted=false`) to `agent-context.md` and the release-note line.

- **Requirements covered:** FN-004, FN-005, FN-006, FN-007, FN-008 (manager level), DA-002 (no migration), AC FN-001 (two rows created in one transaction, unit level). DA-001 is pending its live proof in T04.
- **Decisions made:** The Implementer's signature choices are accepted as within the spec:
  - `editPartnerRequest(long, String, String, String, Long, String, String)`;
  - `createPartnerRequest` returns the saved copy;
  - `PartnerRequestOutcome` carries a nested `Status`, `requestId` and `isNotifiable()`;
  - the office methods take `(List<Long>, long)`;
  - an office row with no country is `WRONG_KIND`;
  - accept on an incomplete copy throws `IllegalStateException` before any write.
  
  No spec edits.
- **Issues encountered:**
  - An incremental compile after changing marlo-data failed ("class file for PartnerRequestDAO not found"). Wiping the classes fixed it.
  - MapStruct `generated-sources` read errors appeared twice after a wipe; a re-run passed. Wiping `marlo-web/target/generated-sources` too avoids it.
  - One `rm` reported `marlo-data/target/classes: Directory not empty`, probably from the IDE language server. The build counts were still correct.
- **Budget (running, design §15 tripwire):**
  - Tasks: 1 / 12.
  - LOC: 966 added, 2 removed / ~1,650. Java prod ~432 against ~420 for the whole spec; tests 534 against ~380 for the whole spec.
  - Review rounds: 2 / ~20.
  - Not exceeded yet. Both Java estimates (prod and tests) are already used up by T01 alone, so the total is likely to overrun by T03–T06. That will be escalated at the gate where it is crossed.
- **Final verification:** compile + targeted test VERIFIED by the Leader re-run; full suite green (Implementer).
- **Commit:** `2f1f405f12` (owner-approved).

### CHG-PARTREQ-001-T02 — `PartnerRequestEmails` builder and email keys

- **Final status:** PASS
- **Date:** 2026-11-19
- **Attempts:** 1
- **Effort:** medium
- **Skills assigned:** `marlo-verify`, `tdd` (task list; `marlo-commit` kept with the Leader)
- **Review mode:** lens checklist (single Reviewer). The `Review: checklist` claim was not used to skip review: `Consumers` is not `none` (old keys `.accept.email` / `.reject.email`), so a conformance Reviewer was owed.

#### Attempt 1

- **Files changed:**
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/utils/PartnerRequestEmails.java` (new, GPL header)
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/utils/PartnerRequestEmailsTest.java` (new, 6 tests)
  - `marlo-web/src/main/resources/global.properties` (+8 lines in the `marloRequestInstitution` block; old `.accept.email` / `.reject.email` untouched)
- **Baseline (red run, observed):** `grep -c "^marloRequestInstitution.mail\." global.properties` → `0`.
- **Public API (for T03 / T05 / T06 briefs):** all static. `Email` has `getSubject()` and `getBody()`.
  - `isEnabled(GlobalUnit)`
  - `unitTexts(GlobalUnit)`
  - `newPartner(unit, assetsUrl, requesterName, requesterEmail, name, acronym, type, country, website, source, reviewUrl)`
  - `newOffice(unit, assetsUrl, requesterName, requesterEmail, institution, countries, countriesIso, sourceProject)`
  - `partnerAccepted(unit, assetsUrl, dearName, institution, partnerName)`
  - `partnerRejected(unit, assetsUrl, dearName, partnerName, justification)`
  - `officeAccepted(unit, assetsUrl, dearName, institution, countries)`
  - `officeRejected(unit, assetsUrl, dearName, institution, countries, justification)`
- **Implementer verification:**
  - Clean compile: 2431 / 1065 / 143, BUILD SUCCESS.
  - `PartnerRequestEmailsTest`: 6 run, 0 failures.
  - Full suite `mvn -o -pl marlo-web -am test`: 1319 run, 0 failures.
  - Checkstyle delta 0.
  - Hygiene ok.
  - i18n gate: no FIX findings. No apostrophes; the only braces are `{0}`–`{8}`.
- **Falsifiers (executed against post-change code, reverted):**
  1. Drop `EmailLayout.wrap` → `AssertionError: newPartner is not inside the shell` (`PartnerRequestEmailsTest.java:91`).
  2. Skip escaping → `testBodyArgumentsAreEscapedAndSubjectsCarryNoJustification` red (raw `<b>` in the body), and `testSubjectArgumentsAreNotEscaped` red.
  3. Shared provider instead of the unit's → `ComparisonFailure: expected:<[[AICCRA]] Partner verificati...> but was:<[[MARLO]] Partner verificati...>`.
  
  There was no separate pre-code test red: the builder and the tests were written together. The falsifiers are the red evidence.
- **Evidence re-run (Leader-inline, non-author):** clean wipe (`classes`, `test-classes`, `generated-sources`), then `mvn -o -pl marlo-data,marlo-web -am test` → exit 0. Compiled 2431 / 1065 / 143; `PartnerRequestEmailsTest` 6 / 0; full suite `Tests run: 1319, Failures: 0, Errors: 0`. **VERIFIED.**
- **Reviewer verdict:** **PASS** — the diff matches design §10 for all six emails:
  - body shape (dear + key + bye from the unit provider, wrapped);
  - subject and body argument counts;
  - body escaping, including the greeting name; subjects not escaped;
  - the switch rule equals `PartnerRequestAction.java:500`, with null guards added;
  - unit texts from `CRP_CUSTOM_FILE`;
  - the new keys, with the old keys untouched.
  
  The tests use literal expectations (no tautology) and cover the full task list. The GPL header and English-only rules hold, and the Checkstyle delta is 0.
- **runtime events:** none
- **spawns:** implementer 30 calls, 156950 tokens, ended complete; reviewer 13 calls, 64359 tokens, ended complete

#### ADVISORY (4R, non-gating)

- **RELIABILITY — lazy custom parameters.** `unitTexts` / `isEnabled` read `unit.getCustomParameters()`, a lazy Hibernate collection. `UnsubmitProjectAction.unitTexts` uses `customParameterManager` with a `RuntimeException` fallback. A detached unit would throw here.
- **READABILITY.** `unitTexts` matches `CRP_CUSTOM_FILE` with `equals`, while `isEnabled` uses `equalsIgnoreCase`.
- **RISK.** Subject arguments are user-supplied and unescaped (as intended). Stripping CR/LF would be cheap defence in depth.
- **RISK.** `reviewUrl` must be built on the server, never taken from request input.
- **Correction of an Implementer claim.** The Implementer reported that `aicrra` does not override `email.role.dear` / `.bye`. That is wrong: `custom/aicrra.properties:3176,3180`, `aiccra3` (3198/3202) and `reap` (3196/3200) override both, as P-12 says. The builder reads them through the unit provider, so behaviour is correct. The override values currently equal the base, so no test can show the override is used; the subject prefix proves the unit routing.

#### Forward pointers (to be copied into later briefs)

- **→ T03 / T05 / T06:**
  - Call the builder with a **managed** `GlobalUnit` (inside the action, session open). A detached unit's lazy `customParameters` will throw.
  - Build `reviewUrl` server-side from the base URL.
  - Pass `dearName` = the recipient's first name.
  - Pass `newOffice` both `countries` (names) and `countriesIso`.
- **→ T11b:** the owner reviews the copy rendered from `email_logs`, not this list.

- **Owner-facing copy (for the T11 review):**
  - `marloRequestInstitution.mail.supportName` = "MARLO support team". The two alerts greet "Dear MARLO support team,".
  - `.mail.newPartner`: "A new partner has been requested in MARLO and is waiting for your review." Then a list:
    - Requested by {0} ({1})
    - Institution {2}
    - Acronym {3}
    - Type {4}
    - Country {5}
    - Website {6}
    - Requested from {7}
    
    It closes with "Review the request in Superadmin, Institutions: <link {8}>".
  - `.mail.newOffice`: "A new country office has been requested in MARLO and is waiting for your review." Then a list:
    - Requested by {0} ({1})
    - Institution {2}
    - Countries {3}
    - Requested from project {4}
  - `.mail.accepted`: "We have added the following institution to MARLO, as you requested:" then **{0}**.
  - `.mail.rejected`: "We were unable to approve your request to add the institution **{0}**. The reason given was:" then the quoted *{1}*.
  - `.mail.officeAccepted`: "We have added the following country office(s) of **{0}** to MARLO, as you requested:" then {1}.
  - `.mail.officeRejected`: "We were unable to approve your request to add the following country office(s) of **{0}**:" then {1}, "The reason given was:" and the quoted *{2}*.
- **Requirements covered:** FN-002, FN-003, FN-009, FN-010 (builder level), NF-002. Sending and gating in the actions follow in T03 / T05 / T06.
- **Decisions made:** these Implementer choices are accepted as within the spec:
  - the `dearName` argument;
  - `newOffice` taking `countries` + `countriesIso`;
  - `isEnabled(null)` returns false.
  
  No spec edits.
- **Issues encountered:**
  - A first `-q` full-suite run died with `NoClassDefFoundError: GlobalUnit`, caused by a stale test class left by the incremental falsifier recompiles. A clean recompile fixed it, and the Leader's clean re-run was green.
- **Budget (running):**
  - Tasks: 2 / 12.
  - LOC: ~1,342 added (T01 966 + T02 ~376 diff lines) / ~1,650.
  - Review rounds: 3 / ~20.
  - Not exceeded, but 81 % of the LOC estimate is used after 2 of 12 tasks. An overrun is expected around T03 and will stop the run at that gate.
- **Final verification:** VERIFIED by the Leader's clean re-run (compile + full suite).
