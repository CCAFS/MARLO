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
- **Commit:** `7e2be0b4d5` (owner-approved).

### CHG-PARTREQ-001-T03 — `PartnerRequestAction` on the manager (accept partner, office accept / reject)

- **Final status:** PASS (attempt 2)
- **Date:** 2026-11-19
- **Attempts:** 2
- **Effort:** xhigh, the security and email-ordering contract. **effort ceiling: held xhigh** on the rework: the task started at the T2 ceiling, and no in-host Reviewer model differs from `opus` while being at least as strong. A fresh Implementer ran with the full Attempt History.
- **Skills assigned:** `marlo-verify`, `error-handling-patterns` (the task list; `marlo-commit` was kept with the Leader).
- **Review mode:**
  - Attempt 1: parallel lens Reviewers, SECURITY+RISK and RELIABILITY.
  - Attempt 2: a single Reviewer covering all three lenses over the whole diff. This is a deviation from parallel mode, recorded because the fix was a narrow log change and override (e) only requires *a* Reviewer.

#### Attempt 1 — FAIL

- **Files changed:**
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/superadmin/PartnerRequestAction.java` (CRLF kept)
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/superadmin/PartnerRequestReviewGuardTest.java` (new, 12 tests)
- **Baseline (red run, observed):** `grep -c "EmailLayout"` → `0`; `grep -c "isHttpPost"` → `0`. After the change: `PartnerRequestEmails` 7, `isHttpPost` 3.
- **Implementer verification:**
  - Clean compile 2431 / 1065 / 144.
  - Guard test 12 / 0.
  - Full suite 1331 / 0.
  - Checkstyle delta -2 (HEAD 2 → 0).
  - Hygiene: no FIX findings.
- **Falsifiers (reverted):**
  1. `isHttpPost` neutralised → `the manager must not be called on a GET expected:<[]> but was:<[acceptPartnerRequest]>` (offices: `but was:<[acceptOfficeRequests, rejectOfficeRequests]>`).
  2. Mail on every outcome → `a request that was already handled must not be mailed again`.
  3. Outcome object in the map → `outcomes must hold strings only, but held class org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome`.
- **Evidence re-run (Leader-inline):** clean wipe + `mvn -o -pl marlo-data,marlo-web -am test` → exit 0; 2431 / 1065 / 144; guard test `Tests run: 12, Failures: 0`; suite 1331 / 0; CRLF preserved. **VERIFIED.**
- **Reviewer verdicts:** SECURITY+RISK **FAIL** and RELIABILITY **FAIL**, both on the same issue. It is reproduced verbatim from the RELIABILITY report:

  > 1. **Discovered Issue:** When `addCountryOffices` or `rejectCountryOffices` fails, the log line names only the institution id, never the request ids. The lines are `LOG.error("Could not accept the office requests of institution {}", this.requestedInstitutionId(), e)` and `"Could not reject the office requests of institution {}"`. The early-return WARN lines (`"... received no institution or no office request ids"`) name neither the institution nor the ids, even when ids were posted (the test fixture posts `"31"`). The request ids are available in `countryOfficePOJO.getIds()`. A `NumberFormatException` from `parseRequestIds` or a manager rollback therefore leaves a log line that cannot be traced to the failing requests.
  >    * **Violated Rule:** `requirements.md` CHG-PARTREQ-001-OPS-001 says: "Every failure in a request, a review write … MUST produce one log line naming the action and the request id". AC OPS-001 says: "A forced failure in each review action produces one ERROR/WARN line with the action and request id". The task scope says: "failures logged with action + request id".
  >    * **Remediation Suggestion:** Add a null-safe helper for the raw ids, for example `countryOfficePOJO == null ? null : countryOfficePOJO.getIds()`. Log it next to the institution id in both catch blocks and in both early-return WARN lines, and name the action literally (`addCountryOffices` / `rejectCountryOffices`), as `recordOutcomes` already does. Optionally, add an assertion in a test that captures the logs.

  The SECURITY+RISK report states the same issue with the same rule. Its remediation also asks to strip CR/LF from the raw posted ids, to make the early-exit wording name what was missing, and to add a forced-exception test.
- **Leader adjudication:** in scope; the task text says "failures logged with action + request id". Rework attempt 2 consumed.
- **runtime events:** none

#### Attempt 2 — PASS

- **Files changed:** the same two files.
  - All four office failure lines name the action literally, the posted request ids (via the null-safe, CR/LF-stripping `postedRequestIds()`), and the institution id.
  - The early exit says the institution was missing.
  - Advisory-grade extras were taken inside methods already touched:
    - (a) `addPartner` treats a null result or null status as `failed`;
    - (b) an external requester with no name is greeted with the creator's first name (legacy fallback);
    - (c) the cc join is null-safe.
  - 6 new tests capture the log lines through logback `ListAppender` (as in `CognitoLogHygieneTest`).
- **New log lines:**
  - `addCountryOffices: could not accept the office requests 31, 32 of institution 500`
  - `rejectCountryOffices: could not reject the office requests 31,32 of institution 500`
  - `rejectCountryOffices: nothing decided, the posted selection has no institution (request ids 31)`
- **Falsifier (reverted, file byte-identical afterwards):** the ids were removed from the accept catch, and 2 tests went red: `expected:<...the office requests [31, 32 ]of institution 500> but was:<...the office requests []of institution 500>`; `testUnparsableIdsAreLoggedRawWithoutLineBreaks` likewise.
- **Implementer verification:** clean compile 2431 / 1065 / 144; full suite 1337 / 0; Checkstyle delta -2; 609 of 609 lines CRLF.
- **Evidence re-run (Leader-inline):** clean wipe + `mvn -o -pl marlo-data,marlo-web -am test` → exit 0; 2431 / 1065 / 144; `Tests run: 1337, Failures: 0, Errors: 0`; CRLF preserved. **VERIFIED.**
- **Reviewer verdict:** **PASS**. OPS-001 is met on every review-write failure path. SEC-002, FN-004, FN-005, FN-008, FN-010, DD-4 and the §4 JSON contract were re-checked over the whole diff. The legacy senders and `validateEmailNotification` are removed. No Struts XML, FTL or JS was touched.
- **runtime events:** none
- **spawns:**
  - implementer (attempt 1): 38 calls, 187955 tokens, ended complete
  - reviewer (security+risk): 10 calls, 83755 tokens, ended fail
  - reviewer (reliability): 7 calls, 78120 tokens, ended fail
  - implementer (attempt 2): 15 calls, 140566 tokens, ended complete
  - reviewer (attempt 2): 7 calls, 72427 tokens, ended complete

#### ADVISORY (4R, non-gating)

- **Refusal log.** `refuseNonPost` logs the non-POST refusal without the posted ids; `addPartner`'s refusal does include them. A refusal is not a write failure.
- **`recordOutcomes` and email grouping in the outer try.** A `null` list element, or a grouping error after commit, would show `failed` for a decision that was actually applied. The manager contract prevents this today.
- **Full exceptions logged.** The error logs pass the full exception. A future mail exception whose message carries an address would leak it into the log. There is no current evidence of this.
- **Raw i18n keys until T08.** The `marloRequestInstitution.ui.outcome.*` action-message keys are added only in T08 (PR 2). Until then the redirect page shows raw keys.
- **Redirect message store.** `addPartner` action messages must survive the redirect. Verify the `superAdminStack` / result message store in T07 and T11a.
- **Untested paths.** The office emails with the notification switch off, and a batch that is entirely `SKIPPED_EXISTING`, are not tested.
- **Unused fields.** `institutionManager` and `institutionLocationManager` are now unused in `PartnerRequestAction`. Follow-up; cleanup scope stays literal.
- **Behaviour fixes relative to legacy:**
  - a blank external mail no longer becomes an empty "To";
  - a missing send-notification parameter no longer wipes the justification;
  - `countryOfficePOJO` is null-checked before it is used.

#### Spec tension (presented to the owner at this gate)

- **AC FN-008 vs design §10 / FN-010.** AC FN-008 says "each requester gets one office-accepted email". The code groups office emails per Global Unit, because §10 ties the texts and the switch to the request's unit, and FN-010 must honour a mixed switch. A requester whose requests in one batch span two units gets two emails. The proposed amendment is AC FN-008 "…each requester gets one email per Global Unit of their requests". Awaiting the owner.

#### Forward pointers (to be copied into later briefs)

- **→ T07:** check that `addPartner` action messages survive the `redirectAction` to `superadmin/marloInstitutions` (message-store interceptor or a result that preserves them).
- **→ T08:** the `marloRequestInstitution.ui.outcome.*` keys are already used by `addPartner` (`done`, `alreadyHandled`, `notFound`, `wrongKind`, `skippedExisting`, `notAllowed`, `failed`) and must be added in T08.
- **→ T11a (PR 1):** the raw `marloRequestInstitution.ui.outcome.*` keys render until T08 lands. Either accept this for PR 1 or move the keys forward; this is the owner's decision at this gate.
- **→ T12:** follow-ups to record:
  - the unused `institutionManager` / `institutionLocationManager` fields;
  - the full-exception logging note.

- **Requirements covered:** FN-004, FN-005, FN-008 (action level), FN-010 (office emails now gated; C5 closed), SEC-002 (accept partner / offices), OPS-001 (these three actions).
- **Decisions made:** these Implementer choices are accepted:
  - overall office `outcome` = `done` if any request was applied, else `skippedExisting` if any was skipped, else the first status;
  - `success` = true when a request was applied or skipped-existing;
  - a mail failure after commit is logged and does not turn the decision into `failed`;
  - office countries in the email are comma-joined (the builder escapes them).
  
  No spec edits (the FN-008 amendment awaits the owner).
- **Issues encountered:** a stale-class `cannot access APConfig` compile error appeared during a falsifier cycle. The falsifiers were redone from a wiped compile.
- **Budget (running), design §15 tripwire: EXCEEDED.**
  - Tasks: 3 / 12.
  - LOC added (code + tests + i18n): T01 966 + T02 353 + T03 950 = **2,269**, against ~1,650.
    - Java prod 957 against ~420 for the whole spec;
    - tests 1,304 against ~380 for the whole spec;
    - i18n 8.
  - Review rounds: 6 / ~20.
- **Final verification:** VERIFIED by the Leader's clean re-run (compile + full suite 1337 / 0).

## Budget Tripwire: after CHG-PARTREQ-001-T03

- **Delta:** 2,269 LOC added after 3 of 12 tasks, against an estimate of ~1,650 for all 12 (+37 % already). On the same trend the spec lands around 4,500–5,500 LOC.
- **Cause:**
  1. Java production code runs at about 2.3× the estimate. The manager carries six methods with refusal rules, and the action rewrite keeps the legacy recipient routing, null guards and log lines (OPS-001 rework).
  2. Tests run at about 3.4× the estimate. Each task proves its falsifiers with doubles that assert their arguments (KZ auth-flow-2), plus log-capture tests.
  3. The design budget did not count the test fixtures needed to meet the task-level disqualifiers.
- **Status:** stopped at the T03 gate for the owner's decision. Not logged `auto-approved`.

### Owner decisions at the T03 gate (2026-11-19)

1. **Budget.** Continue. The ceiling is amended in `design.md` §15 to ~5,000 LOC and ~30 review rounds. Later test tasks reuse the existing doubles.
2. **AC FN-008.** Amended in `requirements.md` §6 and the Decision Log: one email per requester **per Global Unit** of their requests. The closure sweep is recorded in the Decision Log. The `tasks.md` T03 requirements line and the §5 Coverage row were updated.
3. **Action-message keys.** The seven `marloRequestInstitution.ui.outcome.*` keys used by `addPartner` move from T08 to T07 (`tasks.md` T07 and T08 scope). `.outcome.notAuthorized` stays in T08.
4. **Commit.** T03 committed as `b861a90688`.

These are execute-time spec edits. They must be carried as named conformance checks in the next Reviewer brief (T05) and in the one after it.

### CHG-PARTREQ-001-T05 — Edit and reject JSON actions on the manager

- **Final status:** PASS
- **Date:** 2026-11-19
- **Attempts:** 1
- **Effort:** high
- **Skills assigned:** `marlo-verify`, `error-handling-patterns` (task list). `marlo-commit` stays with the Leader.
- **Review mode:** lens checklist, single Reviewer. The `Review: checklist` claim did not skip review, because override (f) applies: SEC-002 is a security surface.

#### Attempt 1

- **Files changed:**
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/json/superadmin/EditPartnerRequestAction.java` (LF kept, +74 / −47)
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/json/superadmin/RejectPartnerRequestAction.java` (CRLF kept, +122 / −54)
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/superadmin/PartnerRequestReviewGuardTest.java` (+262, 8 tests; reuses the T03 doubles)
- **Baseline (red run, observed):** `grep -c "isHttpPost"` → `0`, `0` before the change; `1`, `1` after.
- **Implementer verification:**
  - Clean compile: 2431 / 1065 / 144.
  - Guard test: 26 tests, 0 failures.
  - Full suite: 1345 tests, 0 failures.
  - Checkstyle delta 0.
  - Hygiene: no FIX lines.
- **Falsifiers (reverted):**
  1. `sendNotification` forced to true → `AssertionError: send notification was unchecked`.
  2. Edit `isHttpPost()` disabled → `the manager must not be called on a GET expected:<[]> but was:<[editPartnerRequest]>`.
- **Evidence re-run (Leader-inline):** clean wipe, then `mvn -o -pl marlo-data,marlo-web -am test` → exit 0. Compiled 2431 / 1065 / 144; `Tests run: 1345, Failures: 0, Errors: 0`; LF and CRLF preserved. **VERIFIED.**
- **Reviewer verdict: PASS.**
  - SEC-002, design §8 and DD-7: a non-POST request is refused and writes nothing.
  - Writes go through the manager (§2, §6), and statuses map to the §4 strings.
  - The reject email is sent only after a `DONE`, with `sendNotification` checked and `isEnabled` on the request's managed unit (DD-4, §10, FN-006).
  - The legacy sender and `validateEmailNotification` are removed.
  - OPS-001: every log line names the action and the request id; no log line contains `@`; CR/LF are stripped.
  - `sendNotification` binding has not regressed: it is still read from `getParameters()` in `prepare()`.
  - Named checks for the execute-time spec edits pass: nothing contradicts the amended AC FN-008, and no `.properties` file is touched (T07/T08 split respected).
- **runtime events:** none
- **spawns:** implementer 35 calls, 162539 tokens, ended complete; reviewer 10 calls, 72780 tokens, ended complete

#### ADVISORY (4R, non-gating)

- **Duplicated status mapping.** `statusName` now exists in three actions; it could move into `PartnerRequestOutcome`.
- **Unused dependencies.** `institutionTypeManager` and `locElementManager` in `EditPartnerRequestAction` are no longer used. Removing them changes the injection signature, so it is an owner decision and goes on the follow-up list.
- **Checkbox value.** `Boolean.valueOf` reads a bare `"on"` as false, so T09's JS must post the literal `true` for the notify checkbox.
- **Logging gaps.**
  - A GET that also fails in `prepare()` logs two lines.
  - A skipped email (switch off, or no unit) logs nothing, while the T03 office path logs a WARN.
- **Edit with nothing to change.** It returns `success=true` with `outcome=done`. This is the legacy behaviour.
- **Fail closed in `prepare()`.** The actions now fail closed when `prepare()` cannot read the posted fields; before, they continued with partial data.

#### Forward pointers

- **→ T09:**
  - Post `sendNotification` as the literal `true`, not `on`.
  - Add `type: 'POST'` to all four AJAX calls.
- **→ T12 follow-ups:**
  - Unused managers in `EditPartnerRequestAction`.
  - Shared `statusName`.

#### Plan gap found at this gate (presented to the owner)

- **The current UI breaks if PR 1 ships alone.** `marloInstitutions.js` calls `rejectPartnerRequest.do`, `editPartnerRequest.do`, `rejectOfficesRequest.do` and `acceptOfficesRequest.do` with no `type` (lines 31, 80, 154 and 198), so jQuery sends GET. The accept partner button is also a GET link (`institutionRequestMacro.ftl:58`). T03 and T05 make all five actions POST-only, but the POST conversions are planned in T08 and T09, which ship in PR 2. With PR 1 merged and PR 2 not yet merged, every decision on the current screen answers `notAllowed`. T11a did not catch this because it uses hand-crafted POSTs.

- **Requirements covered:** FN-006, FN-007 (action level), SEC-002 (edit / reject), OPS-001 (edit / reject).
- **Decisions made:** the Implementer's choices are accepted.
  - Fail closed in `prepare()`.
  - Unused manager fields left in place (cleanup scope stays literal).
  - Reject recipient logic duplicated from T03 rather than shared.
  
  No spec edits.
- **Issues encountered:** one full-suite run crashed with `NoClassDefFoundError: Institution`, a stale build. It was clean after a wipe.
- **Budget (running, against the amended ceiling of ~5,000 LOC and ~30 rounds):**
  - Tasks: 4 / 12.
  - LOC added: 2,269 + 458 = **2,727**.
  - Review rounds: 7.
  - Within the ceiling.
- **Final verification:** VERIFIED by the Leader's clean re-run (1345 / 0).
- **Commits:** spec amendments `dcefd59db6`; T05 `529a1c0bf8` (owner-approved).

### Owner decisions at the T05 gate (2026-11-19)

1. **POST bridge moved into T07.** `type: 'POST'` is added to the four AJAX calls, and `sendNotification` is posted as a literal `true` / `false`. The accept link becomes a minimal POST form. This was recorded in the `requirements.md` Decision Log with its closure sweep, and the T07 scope and tests and the T09 scope and red-run baseline were updated in `tasks.md`.
2. **Task order.** The order is now T06 → T07 → T04 → T11a, so the two live tasks share one throwaway DB copy and one app start. T04 still gates the merge of PR 1. A T04 Pivot would change only the `findForUpdate` implementation, which T06 and T07 do not depend on.

The next Reviewer briefs carry these execute-time spec edits as named checks: T07 scope as amended, and the Decision Log entry.

### CHG-PARTREQ-001-T06 — Local request creation and the two alerts (remove CLARISA)

- **Final status:** PASS
- **Date:** 2026-11-19
- **Attempts:** 1
- **Effort:** high
- **Skills assigned:** `marlo-verify`, `error-handling-patterns` (task list). `marlo-commit` stays with the Leader.
- **Review mode:** lens checklist, single Reviewer (`Review: full`; removes a delivered integration).

#### Attempt 1

- **Files changed:**
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/projects/PartnersSaveAction.java` (LF, +32 / −142)
  - `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/json/project/RequestCountryOfficeAction.java` (CRLF, +31 / −33)
  - `marlo-web/src/main/resources/global.properties`: +1, `marloRequestCreation.saveFailed` = "Your request could not be saved. Please try again or contact the MARLO technical team."
  - `marlo-web/src/main/webapp/WEB-INF/global/pages/partnersSave.ftl`: one key line, `sendFailed` → `saveFailed`
  - `marlo-web/src/test/java/org/cgiar/ccafs/marlo/action/projects/PartnersSaveActionRequestTest.java` (new, 4 tests)
- **Baselines (observed) → after:**
  - `isAiccra()\|sendPartnerRequest\|ExternalPostUtils` in `PartnersSaveAction`: `5` → `0`
  - `"[MARLO-"` in `RequestCountryOfficeAction`: `1` → `0`
  - the commented legacy mail block: `1` → `0`
- **Implementer verification:**
  - Clean compile: 2431 / 1065.
  - `-Dtest=PartnersSaveAction*Test`: 7 tests (3 existing + 4 new), 0 failures.
  - Full suite: 1349 tests, 0 failures.
  - Checkstyle delta −1.
  - Hygiene and i18n: no FIX findings.
- **Falsifier (reverted):** reintroducing `if (this.isAiccra()) { return SUCCESS; }` before the save gives `Tests run: 4, Failures: 4`, including `java.lang.AssertionError: the manager must be called once expected:<1> but was:<0>` for the AICCRA-unit case.
- **Evidence re-run (Leader-inline):** clean wipe, then `mvn -o -pl marlo-data,marlo-web -am test` → exit 0.
  - Compiled 2431 / 1065 / 145.
  - `Tests run: 1349, Failures: 0, Errors: 0`.
  - Both greps return `0`.
  - LF and CRLF line endings preserved.
  
  **VERIFIED.**
- **Reviewer verdict:** **PASS**.
  - The CLARISA branch, its imports and the commented-out block are removed. Every unit now goes through `createPartnerRequest`.
  - A save failure logs, sets `requestFailed` and returns `INPUT`, with no mail.
  - The alerts are sent only after the save returns and only when the session unit (re-read from the database) has the switch on. Each goes To plus bcc `email.notification`, with builder arguments as in §10. The review URL is built server-side (P-15).
  - The request-source assumption holds: `add*Message` still sets `requestSource`.
  - `validate()` is untouched.
  - The tests run with an AICCRA unit and assert the arguments the double receives.
  - The named checks pass: no conflict with the T07 POST bridge or with the AC FN-008 amendment.
- **runtime events:** none
- **spawns:** implementer: 33 calls, 178175 tokens, ended complete. reviewer: 9 calls, 60140 tokens, ended complete.

#### ADVISORY (4R, non-gating)

- **Dead `StringBuilder`.** `save()` builds a `StringBuilder` that nothing reads, and the name `add*Message` hides that these methods now only set the source. Candidate for a rename or cleanup.
- **Orphaned key.** `marloRequestCreation.sendFailed` has no users left in `partnersSave.ftl`. Check for other users before any removal.
- **Source not asserted.** No test sets `pageRequestName`, so the source that reaches the alert is `null` in every test.
- **Working-directory-dependent test.** `testNoReferenceToTheExternalServiceRemains` reads paths relative to `marlo-web/`, so it fails when the IDE runs it from the repository root.
- **`RequestCountryOfficeAction` untested.** Its switch gate moved to `isEnabled(loggedCrp)`, and no test covers that.
- **Behaviour changes:**
  - The unused project lookup was removed.
  - `RequestCountryOfficeAction` still answers `result=1` when the mail fails or the switch is off.
  - The INFO log now records the user id instead of the email.

#### Follow-ups for T12 (P-7 dead code; not deleted, cleanup scope stays literal)

- `APConfig.getClarisaAPIHost`, `getClarisaAPIUsername`, `getClarisaAPIPassword`
- `ExternalPostUtils` (both copies)
- `marloRequestCreation.sendFailed`
- The `add*Message` naming and the unused `StringBuilder` in `PartnersSaveAction`

#### Forward pointers

- **→ T11a:** check the office-alert switch-off path live, since it has no unit test. Also check that the new-partner alert's source line is filled for each opener module.
- **→ T10:** keep `requestFailed` and the `saveFailed` key in the redesigned page. `sendFailed` becomes orphaned.

- **Requirements covered:** FN-001, FN-002, FN-003, FN-010 (alerts), NF-001, OPS-001 (request actions), AC FN-001 (unit level).
- **Decisions made:** the Implementer's choices are accepted:
  - the source string for an office request is `"P" + projectID`;
  - `RequestCountryOfficeAction` keeps its `result` contract;
  - office rows are still saved one per country (design §4).
  
  No spec edits.
- **Issues encountered:** a stale-class compile error in the falsifier run, cleared by a wipe.
- **Budget (running, amended ceiling ~5,000 LOC / ~30 rounds):**
  - Tasks: 5 / 12.
  - LOC added: 2,727 + 333 = **3,060**.
  - Review rounds: 8.
  - Within the ceiling.
- **Final verification:** VERIFIED by the Leader's clean re-run (1349 / 0).
- **Commit:** `c209aa74d0` (owner-approved).

### CHG-PARTREQ-001-T07 — Struts security config and menus (+ outcome keys and POST bridge, owner-moved)

- **Final status:** PASS
- **Date:** 2026-11-19
- **Attempts:** 1
- **Effort:** medium
- **Skills assigned:** `marlo-verify` (task list). `marlo-commit` stays with the Leader.
- **Review mode:** lens checklist, single Reviewer. A `Review: checklist` claim did not skip review, because overrides (b) and (f) apply: shared contract and security surface.

#### Attempt 1

- **Files changed** (all LF, as at HEAD):
  - `struts-json.xml` (+28 / −2)
  - `struts-admin.xml` (+6 / −1)
  - `global.properties` (+7 `marloRequestInstitution.ui.outcome.*`)
  - `menu-superadmin.ftl` (1 line uncommented)
  - `menu-admin.ftl` (−1)
  - `institutionRequestMacro.ftl` (accept link → `method="post"` form; confirm moved to `onsubmit`)
  - `superadmin/marloInstitutions.ftl` (cache-buster `?20261119`)
  - `marloInstitutions.js` (+4 `type: 'POST'`)
  - `PartnerRequestReviewGuardTest.java` (+2 static tests)
- **Baselines (observed) → after:**
  - Guard counts: `0 0 0 0` / admin `0` → all present.
  - `menu-superadmin.ftl:34`: was `[#--` → live.
  - `menu-admin.ftl:14`: was live → removed.
  - `grep -c "type: *'POST'"`: `0` → `4`.
- **Implementer verification:**
  - Full suite: 1351 tests, 0 failures.
  - `xmllint --noout`: OK.
  - Checkstyle delta 0.
  - i18n and hygiene: no FIX findings.
  - The cache-bust script flagged a comment in `rejectInstitutionPopup.ftl:48`. It is a false positive.
- **Falsifier (reverted):** moving `superAdminValidation` after `defaultStack` in `rejectPartnerRequest` gives `AssertionError: rejectPartnerRequest: superAdminValidation must come before the stack (guard 294, stack 251)`.
- **Evidence re-run (Leader-inline):**
  - `xmllint --noout` on both XML files: OK.
  - `grep -c "type: *'POST'"`: `4`.
  - Clean wipe + `mvn -o -pl marlo-data,marlo-web -am test` → exit 0: compiled 2431 / 1065 / 145; `Tests run: 1351, Failures: 0, Errors: 0`.
  - All files LF, as at HEAD.
  
  **VERIFIED.**
- **Reviewer verdict: PASS.**
  - JSON chain is `logErrorStatus → requireUser → validPhase → superAdminValidation → defaultStack → SecurityControl`, as in §4.
  - `includeProperties` is as specified.
  - The admin route follows the amended §4 row and redirects to `/superadmin`.
  - Menus, keys and the POST bridge match the amended scope.
  - No new `*.json` path (Hard rule 3).
  - The static tests isolate each `<action>` element, so the disqualifier does not apply.
  - SEC-003 holds. The guard refuses before `prepare()`, and an anonymous user is still stopped by `requireUser`. The order is the same as `superAdminStack`.
  - `searchInstitutionsName.do` is a shared read-only institution search, outside SEC-001.
- **runtime events:** none
- **spawns:**
  - implementer: 20 calls, 122110 tokens, ended complete
  - reviewer: 15 calls, 63553 tokens, ended complete

#### Execute-time spec edit (recorded when made)

- `design.md` §2 (`struts-admin.xml` row) and §4 (admin route row), and `tasks.md` T07 scope: the admin route lists no explicit `logErrorStatus`.
  - **Why:** `crpAdminStack` already runs it, and the existing `ErrorStatusLogStackCoverageTest` requires it exactly once. The spec's list failed that test with "admin/{crp}/marloInstitutions must run logErrorStatus exactly once ... was:<2>".
  - **Requirement meaning:** unchanged. SEC-003 is unaffected.
  - **Closure sweep:**
    - Forward grep of `logErrorStatus` across the spec: the `design.md` §2 and §4 and `tasks.md` T07 hits are amended. The JSON rows keep it correctly.
    - `judgment.md` is historical and was not edited.
    - Backward: nothing cites the admin route's interceptor list.

#### ADVISORY (4R, non-gating)

- **Observability.** On the admin route `logErrorStatus` runs inside the stack, after the guards, so a 401 or 403 refusal there is not logged. Authorization is unaffected.
- **Readability.**
  - `i18nFile` and `requireUser` run twice on the admin route. This is harmless and follows the precedent; a one-line XML comment would explain it.
  - The test checks `contains("success,outcome")`, which the offices value also satisfies. An exact per-action value would catch a dropped `outcomes.*`.

#### Forward pointers

- **→ T11a**, live proof of:
  - the 403 for a non-superadmin on all five routes;
  - the admin redirect;
  - `addPartner` messages surviving the redirect (`superAdminStack` includes `keepRedirectMessages`, verified from config only);
  - the accept form and the four AJAX POSTs from the current screen;
  - the AICCRA superadmin menu entry.
- **→ T12 follow-up:** log the admin-route refusals.

- **Requirements covered:** SEC-001, SEC-002 and SEC-003 (configuration level; behaviour is proven in T11a), FN-011.
- **Decisions made:** the admin-route amendment above. The other Implementer choices are accepted:
  - `searchInstitutionsName.do` left GET and unguarded;
  - keys without placeholders.
- **Issues encountered:** none.
- **Budget (running, amended ceiling ~5,000 LOC / ~30 rounds):**
  - Tasks: 6 / 12.
  - LOC added: 3,060 + 119 = **3,179**.
  - Review rounds: 9.
- **Final verification:** VERIFIED by the Leader's clean re-run (1351 / 0).
- **Commit:** `d4dd9c8a3b` (owner-approved).

### CHG-PARTREQ-001-T04 — Live proof of atomicity and locking (settles P-5, P-21)

- **Final status:** PASS
- **Date:** 2026-11-19
- **Attempts:** 1
- **Effort:** xhigh
- **Skills assigned:** `marlo-verify`, `systematic-debugging` (task list).
- **Review mode:** single evidence Reviewer (`Review: full`).
- **Environment (owner-approved at the T07 gate):**
  - Throwaway copy `aiccradb_partreq_test` was made from `aiccradb2` with `mysqldump --single-transaction`. It holds 623 tables, 10974 institutions and 5079 partner requests.
  - `marlo-dev.properties` was pointed at the copy temporarily and then restored byte-for-byte.
  - The MySQL `general_log` was enabled per capture and restored to `0` / `TABLE`.

#### Attempt 1

- **Files changed:**
  - `design.md` §16: P-5 and P-21 rows cite the probes; the count line was updated.
  - New files under `probes/`: `README.md` (T04 section), `t04-probe.py`, `t04-falsifier-lockonly-gate.out`, `t04-a-fault-injection.out`, `t04-b-concurrent-gate.out`, `t04-b-concurrent-natural.out`, `t04-c-ordering.out`, `t04-office-batch.out`.
  - No product code changed.
- **Falsifier, run first (red):**
  - Change: `refresh` replaced by `buildLockRequest(PESSIMISTIC_WRITE).lock(entity)`, so the row is locked but its state is not re-read.
  - Run: two POSTs to `addPartner`, released together from a third connection's lock gate. Starts were 0.3 ms apart and the requests overlapped.
  - Result: **2 institutions** (11898, 11899) and 2 HQ locations for one request; `institutions` 10974 → 10976. JD-1 is reproduced.
- **(a) Fault injection:**
  - Change: an `IllegalStateException` thrown after the institution, location and copy saves.
  - Result: the log shows the inserts, then `ROLLBACK`. Counts are unchanged and the request is still pending (`acepted=NULL`). The action logged the failure and redirected. **DA-001 holds.**
- **(b) Concurrent accepts, clean code:**
  - Gated run: starts 1.3 ms apart, overlapping (`data_lock_waits` = 2 at release). Result: +1 institution, one `DONE` and one `ALREADY_HANDLED`.
  - Three natural races, 0.1 ms apart: +1 institution each time.
  - SQL: `select partnerreq0_.id as id1_200_0_ … from partner_requests partnerreq0_ where partnerreq0_.id=5134 for update`, issued from both connections 48 µs apart.
  - **FN-005 and P-21 hold.**
- **(c) Commit before mail:**
  - A second connection polled 208 times, about 3 ms apart. Its first sighting already showed the institution, the location and `acepted=1`; no partial state was ever visible.
  - `COMMIT` at 17:02:27.484; message built at .541, 58 ms after the commit.
  - `email_logs` row 18271 was written. **DD-4 and P-5 hold.**
- **Office batch:** `{"5143":"done","5144":"skippedExisting"}`, and `institutions_locations` +1 (exactly one row).
- **Cleanup verified by the Leader (non-author re-check):**
  - `git diff -- marlo-data marlo-web` is empty (0 lines).
  - No extra worktree remains.
  - `marlo-dev.properties` has `mysql.database=aiccradb2`.
  - `@@general_log` is `0` and `log_output` is `TABLE`.
  - `aiccradb2` is unchanged: 10974 institutions, 5079 partner requests.
  - No `cargo:run` process; port 8080 is closed.
  - No DB password in the probe files.
  - A credentials-file string match in `t04-falsifier-lockonly-gate.out` was a false positive: a 3-character value matched timestamp digits.
  
  **VERIFIED.**
- **Reviewer verdict:** **PASS.**
  - The probes prove the claims and no disqualifier applies.
  - The lock-only variant is the faithful falsifier: DD-3 defines "lock without refresh" as the defect, and removing the line outright would also have removed the lock.
  - The requests were truly parallel: barrier threads with separate sessions, and `data_lock_waits` = 2.
  - The probe refuses any schema not named `*partreq_test*`.
  - The `created_by` ALTER on the copy does not affect transaction, lock or commit semantics.
- **runtime events:** none
- **spawns:** implementer: 96 calls, 250733 tokens, ended complete; reviewer: 12 calls, 68098 tokens, ended complete.

#### Environment deviations (recorded)

- **Builds ran from a throwaway `git worktree`.** In the main checkout, the VS Code Java language server (`redhat.java`) had overwritten about 20 classes in `marlo-web/target/classes` with Eclipse-compiler stubs, including `WebAppInitializer`, so the app would not start. The worktree was removed afterwards, and the main `target/classes` was deleted because it held a stale lock-only class. **Lesson:** close VS Code, or wipe the classes, before any local run from the main checkout.
- **Schema change on the copy only.** `ALTER TABLE institutions ALTER COLUMN created_by SET DEFAULT 0` was run on `aiccradb_partreq_test` only (see P-25).
- **Real mail sent.** Mail is actually sent locally. Five test mails went to the configured `email.notification`.

#### Execute-time spec edits (recorded when made)

- `design.md` §16, P-5 and P-21: changed from UNVERIFIED to the probe citations. The Implementer made this edit as the task's deliverable. The Leader appended the copy-schema note to both rows.
- `design.md` §16: a new row **P-25** (High, UNVERIFIED). Production accept relies on non-strict SQL mode: `institutions.created_by` is NOT NULL with no default and is not mapped by the entity. This is pre-existing, since the legacy `addPartner` saved the same entity. The count line now reads `Verified 24 · UNVERIFIED 1`. The meaning of no requirement changes.
- `probes/README.md`: Leader notes on the missing first run (it failed on `created_by`) and on the doubled log lines.

#### ADVISORY (non-gating)

- **P-25 is a release risk.** If production runs in strict mode, every accept fails. The fix options are a guarded Flyway migration adding `DEFAULT 0` (via `marlo-migration`) or mapping `created_by` in `Institution`. This needs an owner decision (Pivot-class spec gap; presented at this gate).

- **Requirements covered:** DA-001 (live), FN-005 (concurrent, live), AC FN-004/FN-005/DA-001 "leave no institution behind", FN-008 office batch (live), DD-3 / DD-4 confirmed.
- **Budget (running, amended ceiling ~5,000 LOC / ~30 rounds):**
  - Tasks: 7 / 12.
  - LOC (product + tests + i18n): unchanged at **3,179**; the probe driver of ~400 lines is evidence and is not counted.
  - Review rounds: 10.
- **Final verification:** VERIFIED (cleanup re-check by the Leader; probe evidence reviewed).

### Owner decisions at the T04 gate (2026-11-19)

1. **P-25 becomes a release precondition.** The owner checks production `@@GLOBAL.sql_mode` before the release, together with OPS-002. If the mode is strict, the release stops and the spec is reopened (DA-002 amendment). No code change is made now. The precondition is recorded in `tasks.md` §7.
2. **Commit** of the T04 evidence, approved by the owner.
3. **T11a** goes ahead on `aiccradb_partreq_test`.
- **Commit:** `cad7c5d4e4` (owner-approved).

### CHG-PARTREQ-001-T11 (part T11a) — End-to-end verification of PR 1 on a throwaway DB

- **Status:** PASS on attempt 3. All PR-1 ACs are evidenced.
  - Attempt 1: FN-007 was red on the current screen (a pre-existing field-name mismatch).
  - The owner decided at the T11a gate to fix it in T07; after that bridge FN-007 passes (`t11a-ui-edit-rerun.out`).
  - Attempt 2 added the FN-001 page-message evidence and the privacy scrub.
  - Attempt 3 corrected two stale verdict lines.
- **Date:** 2026-11-19
- **Attempts:** 3
- **Effort:** high
- **Skills assigned:** `marlo-verify`, `playwright-cli`.

#### Attempt 1

- **runtime events:** provider/session death ×1 → rung 3 (resume-by-message).
  - The host (Orca) restarted mid-run.
  - **Leader tree probe before resuming:** no app on 8080, no extra worktree, `marlo-dev.properties` on `aiccradb2`, `general_log` 0, HEAD `cad7c5d4e4`, clean tree.
  - **The copy was untouched by T11a:** `partner_requests` max 5144, `email_logs` max 18272 (T04's), 0 users or parameters differing from `aiccradb2`.
  - The worker's context survived and the worker was resumed by message.
- **Files written:**
  - 21 `probes/t11a-*` files (drivers and outputs).
  - `probes/README.md` § T11a.
  - No product code (`git diff -- marlo-data marlo-web` is empty).
- **Staging falsifier (red, observed):** `staging` `3e4d08bd7e` was built in a throwaway worktree. A non-superadmin (copy user 22, CRP-Admin of AICCRA) sent hand-crafted POSTs to the five review routes. All five were accepted and rows changed, including a new institution, so **C8 is reproduced** (`t11a-falsifier-staging.out`). The same script on the branch got 403 on the four JSON routes. `addPartner` returns a 200 "Permission denied" page, and no row changed (`t11a-sec-nonsa-branch.out`).
- **AC results:**

| AC | Evidence | Result |
|---|---|---|
| FN-001 | rows: `t11a-switch-off.out` (AICCRA 5214/5215), `t11a-fn001-non-aiccra.out` (CCAFS 5223/5224); no CLARISA: `t11a-fn001-no-clarisa.out`; "request sent" message and the rows read right after submit: `t11a-fn001-message.out` (attempt 2) | **PASS.** AICCRA (project 102076, rows 5249/5250) and CCAFS (project 1, rows 5251/5252): after the `messageSent=true` redirect the page renders "The new partner request was sent succesfully. You will receive a confirmation message as soon as it has been processed." (a literal in `partnersSave.ftl`, no i18n key; T10 replaces it with `marloRequestCreation.success.*`). The rows read right after submit show the original with `partner_request_id` NULL and the copy pointing to it, both active. No CLARISA reference in the source or the logs |
| FN-002 / FN-003 | `t11a-fn002-new-partner-alert.html`, `t11a-ui-office-accept.out`, `t11a-switch-off.out` | PASS. The alert carries requester, data, source "Cluster: (102076)…" and the review link. The office alert is sent. With the switch off, nothing is sent and the request is still stored |
| FN-004 / FN-005 / DA-001 | `t11a-ui-accept.out`, `t11a-ops001.out` | PASS. Accept from the screen creates one institution and one HQ location, and both rows are closed. A replay gives "This request was already handled." and no second institution. A forced failure leaves nothing behind |
| FN-006 | `t11a-ui-reject.out` | PASS. Notify checked sends one email, unchecked sends none. Justification, user and date are recorded |
| FN-007 | `t11a-ui-edit.out` (red, before the bridge), `t11a-edit-api.out`, **`t11a-ui-edit-rerun.out`** (after the T07 edit-field bridge) | **PASS after the bridge** (4 / 4): a valid edit from the screen gives `done`, the copy is changed and `is_modified=1`, and the original is unchanged; the reload shows the edited values; a stale-page edit of a handled request gives `alreadyHandled` with no change; a missing justification is blocked client-side (0 calls). Before the bridge it was red: the field names did not match. The same mismatch exists on `staging`, so it is pre-existing |
| FN-008 | `t11a-ui-office-accept.out`, `t11a-ui-office-reject.out`, `t11a-office-multiunit.out` | PASS. Kenya is added once as non-HQ, and France is skipped as existing. Reject honours notify. A batch over units 45 and 47 sends 2 emails, as amended in AC FN-008 |
| FN-009 / FN-010 | `t11a-emails-fn009.out`, `t11a-switch-off.out` | PASS. The six emails were read from the sent HTML: shell present, no legacy footer, no raw key. With the switch off, 0 emails |
| SEC-001 / SEC-002 | `t11a-sec-nonsa-branch.out`, `t11a-sec-sa-get-branch.out` | PASS. A non-superadmin gets 403 / Permission denied with no row change. A superadmin GET changes nothing |
| SEC-003 | `t11a-menus-branch.out` | PASS. There is no CRP-admin entry in AICCRA or CCAFS. The admin route redirects a superadmin and refuses a CRP admin |
| FN-011 / P-15 | `t11a-menus-branch.out` | PASS. "Request Institutions" is in the AICCRA superadmin menu, and the page opens without `phaseID` |
| OPS-001 | `t11a-ops001.out` | PASS for the five actions. Each logs one ERROR with the action and request id and no email address. A failure during email build or dispatch was not forced |
| Redirect messages (T03/T07) | `t11a-ui-accept.out` | PASS. The messages render as text, not raw keys |

- **Email safety:**
  - Probe requests were created by copy user 2491. Its copy email was set to the notification mailbox, and `external_user_mail` was empty.
  - `marlo.production=false` makes `SendMailS` send only to `email.notification`, with no cc or bcc.
  - Every `email_logs` row of the run (18277–18296) went to the notification mailbox.
  - The emails of users 1082 and 22 were set to `example.invalid` on the copy for the run.
- **Cleanup (worker-reported; spot-checked by the Leader):**
  - Users 2491, 22 and 1082 and switch 2184 restored on the copy (0 rows differ from `aiccradb2`).
  - Checkout `marlo-dev.properties` never edited; the worktrees carried their own copy.
  - `general_log` never enabled. App stopped. Worktrees removed.
  - `aiccradb2` unchanged.
- **Leader privacy check of the probe files:**
  - No DB password.
  - The owner's own address (the local `email.notification`) appeared in `t04-c-ordering.out:26`, a file already committed in `cad7c5d4e4`, which is local and unpushed. It is scrubbed to `<notification mailbox>` in the working tree; amending the history is pending the owner's decision.
  - `MARLOSupport@cgiar.org` (the public support contact inside the email shell) is kept.
- **spawns:** implementer: 121 calls, 321443 tokens, ended complete (after one `died` → resumed).

#### Findings outside the ACs (recorded)

1. **Superadmin is a global privilege, not a per-unit one.** User 2491 is PC in AICCRA and SuperAdmin in other units, and was accepted on all five routes (`t11a-sec-nonsa-branch-user2491-superadmin-elsewhere.out`). This matches SEC-001 as written (`canAccessSuperAdmin()` = `FULL_PRIVILEGES`, design §8). It is recorded for `agent-context.md` (T12).
2. **Misleading save message on the review page.** After a decision, the page also shows the generic "The Information was correctly saved. Please keep in mind that the fields highlighted below are missing or incorrect." This is pre-existing. → T08: the redesigned page must not render the generic save note.
3. **Browser `confirm()` on accept** (current screen). PR 2 removes it (T09).
4. **Edit with an empty type or unknown country.** It returns `done`, keeps the old type and country, and applies the other fields (T05 pointer confirmed live).

### Owner decisions at the T11a gate (2026-11-19)

1. **FN-007 is fixed in PR 1.** The edit AJAX call now sends the parameter names the action reads. T07 is reopened (`[~]`) for this edit-field bridge. The decision is recorded in the `requirements.md` Decision Log and in the `tasks.md` T07 / T09 scope, with its closure sweep. After the fix, the FN-007 live check is re-run and T11a is closed.
2. **Privacy fix.** `cad7c5d4e4` was amended to `8dd63c27a9` so that only the scrubbed `probes/t04-c-ordering.out` changes (the owner's address → `<notification mailbox>`). The commit was local and unpushed, and `HEAD` is now clean of the address.
3. **T11a evidence** is committed after the FN-007 re-run.

### CHG-PARTREQ-001-T07 (reopened) — Edit-field bridge

- **Final status:** PASS (reopening, attempt 1)
- **Date:** 2026-11-19
- **Effort:** medium
- **Skills:** `marlo-verify`
- **Review:** single Reviewer; override (e) applies because this is rework of a closed task.

#### What changed

- **Files:** `marloInstitutions.js`. The edit call's `data:` no longer uses `serializeObject()`; it is an explicit object built from the same card's fields. `superadmin/marloInstitutions.ftl` cache-buster changed `?20261119` → `?20261119b`. Both files LF.
- **Field mapping.** The JS keys match the action's parameter names. Constants were resolved in the web `APConstants`.

| JS key | Source | Form field |
|---|---|---|
| `requestID` | `PARTNER_REQUEST_ID` = "requestID" | hidden `requestID` |
| `institutionName` | `INSTITUTION_NAME` = "institutionName" | `name` |
| `institutionAcronym` | literal in the action | `acronym` |
| `institutionWebPage` | literal in the action | `webPage` |
| `institutionTypeID` | `INSTITUTION_TYPE_REQUEST_ID` = "institutionTypeID" | `type` (id) |
| `countryID` | `COUNTRY_REQUEST_ID` = "countryID" | `country` (ISO alpha-2, the same format `editPartnerRequest(..., countryIso, ...)` expects) |
| `justification` | `JUSTIFICATION_REQUEST` = "justification" | `modificationJustification` |

#### Verification

- **Falsifier (static):** the brief's prediction `grep -c institutionName` → `0` before did not hold. The count was `2` before because the reject popup already uses that name, and `3` after, the new hit being in the edit `data:` block. The live FN-007 check is the real falsifier: `t11a-ui-edit.out` was red before this change.
- **Evidence re-run (Leader-inline):**
  - `node --check` OK.
  - `grep -c "type: *'POST'"` = `4`.
  - The 7 keys equal the resolved constants and literals.
  
  **VERIFIED.**
- **Reviewer verdict:** **PASS**.
  - The values come from the same card (`$request` scope).
  - The names match `EditPartnerRequestAction.java:150-157`.
  - The country is ISO alpha-2 end to end: select → action → `locElementDAO.findISOCode`.
  - Scope held: no macro rename, no restyle.
  - The named checks for the T11a-gate Decision Log entry and the T07/T09 scope amendments pass.
- **runtime events:** none
- **spawns:** implementer 6 calls, 89671 tokens, ended complete; reviewer 6 calls, 41965 tokens, ended complete.
- **Advisory:** the `marlo-verify` cache-bust script flags `rejectInstitutionPopup.ftl:48`, which is pre-existing and out of scope.
- **Budget (running):**
  - Tasks: 7 / 12 (T07 closed again).
  - LOC added: 3,179 + 9 = **3,188**.
  - Review rounds: 12.

#### T11a — attempts 2 and 3, and the final verdict

- **Attempt 1 Reviewer: FAIL.**
  - (1) The AC FN-001 clause "the page shows the 'request sent' message" was not evidenced: only `messageSent=true` appeared in a redirect URL, and the cited rows had been read after the accept.
  - (2) The FN-007 row was stale.
  - Advisory: scrub the personal data.
- **Attempt 2 (worker resumed):**
  - `t11a-fn001-message.out`: the page message was captured in AICCRA and CCAFS, and the rows were read right after submit, with the switch off and 0 mails.
  - Privacy scrub of the `t11a-*` files:
    - requester names → `<requester>`;
    - home and scratchpad paths → `<home>` / `<scratchpad>`;
    - the default username in the driver removed;
    - the traceback trimmed from `t11a-ui-reject.out`.
    
    No measured value changed.
  - The Leader updated the FN-001 and FN-007 rows.
  - **Reviewer: FAIL.** Two stale verdict lines still said FN-007 failed (correction-note sweep).
- **Attempt 3:** the Leader rewrote the T11a status line and the `probes/README.md` § T11a verdict.
  - **Reviewer: PASS.** Every PR-1 AC is evidenced and the record is internally consistent.
- **Leader re-checks (each attempt):**
  - no app and no worktree;
  - `aiccradb2` unchanged (10974 / 5079);
  - `general_log` 0;
  - `git diff -- marlo-data` empty;
  - the marlo-web diff equals the reviewed T07 bridge;
  - in the probe files: 0 hits for home paths, `/private/tmp` or the owner's name, and the only email address is the public `MARLOSupport@cgiar.org`.
- **runtime events:** provider/session death ×1 → rung 3 (resume-by-message). Recorded in attempt 1.
- **spawns:**
  - implementer: resumed ×3 (FN-007 re-run 37 calls / 364639 tokens; attempt 2 11 calls / 391323 tokens), each ended complete;
  - reviewer: attempt 1 29 calls / 76194 tokens, ended fail; attempt 2 12 / 53889, ended fail; attempt 3 5 / 43858, ended complete.

#### ADVISORY (non-gating, from the T11a reviews)

- **SKIPPED_EXISTING office email.** The office-accepted email lists a skipped country (France, already present) as "added". Per design §6 that requester does get the office-accepted email; the follow-up is for the copy to list only the countries actually added. → T12 follow-up.
- **Unit in office email subjects.** Per-unit office emails for unit 47 carry the subject prefix `[AICCRA]`. The new-partner alert subject repeats the unit: `[AICCRA - AICCRA]`. → owner review of the email renders in T11b.
- **DA-001 evidence.** `t11a-ops001.out` may fail before the insert, so it is not the DA-001 proof. DA-001 is proven by T04 (`t04-a-fault-injection.out`).
- **Legacy review macro.** It throws on a request with no institution type, which cuts the page off. Seen on COPY fixture 5226. → T08: the rewritten macro must tolerate a missing type or country.
- **Literal "request sent" text.** The message is a literal in `partnersSave.ftl` (with the typo "succesfully"). T10 replaces it with `marloRequestCreation.success.*` keys.

#### Forward pointers

- **→ T08:**
  - tolerate a request with no type or country;
  - do not render the generic "Information was correctly saved" note;
  - keep the T07 POST-form accept.
- **→ T09:** keep the edit-call parameter names and the four `type: 'POST'`.
- **→ T11b:** owner review of the email subjects (unit prefix).
- **→ T12:**
  - superadmin is a global privilege;
  - the SKIPPED_EXISTING email copy;
  - the earlier follow-ups.

- **Requirements covered (live):**
  - FN-001 to FN-011;
  - SEC-001 to SEC-003 (staging falsifier C8 reproduced → branch 403);
  - OPS-001 (review actions);
  - DA-001 via T04.
- **Budget (running):**
  - Tasks: 8 / 12 (T11a done; T11b pending).
  - LOC: **3,188**.
  - Review rounds: 15.
- **PR 1 scope complete:** T01–T07 + T11a.
- **Commit:** `d7378d5cda` (owner-approved). The PR 1 scope is complete on the branch. No push and no PR yet; the owner has not asked for either.

### CHG-PARTREQ-001-T08 — Review screen markup and CSS (`.prq`)

- **Final status:** PASS (attempt 2)
- **Date:** 2026-11-19
- **Attempts:** 2
- **Effort:** high on attempt 1; xhigh on attempt 2 (rework bump).
- **Skills:** `impeccable-bridge` (design route: playbooks layout, typeset, colorize and harden; ENV-2; mode Operate; polish light; motion none), `marlo-verify`.
- **Review mode:** lens checklist, single Reviewer (`Review: full`), with the screenshots read.

#### Attempt 1 — FAIL

- **Files changed** (all LF, as at HEAD):

| File | Lines | Change |
|---|---|---|
| `institutionRequestMacro.ftl` | +249 / −171 | rewritten |
| `rejectInstitutionPopup.ftl` | +104 / −63 | reject and accept dialogs |
| `allInstitutionsPopup.ftl` | +25 / −27 | rewritten |
| `views/superadmin/marloInstitutions.ftl` | +75 / −23 | header and counts, `#prqNotes`, diff libs dropped from `pageLibs`, no `generalMessages.ftl` |
| `marloInstitutions.css` | +437 / −17 | `.prq` on `--marlo-*` |
| `global.properties` | +74 | 73 `marloRequestInstitution.ui.*` keys, `.outcome.notAuthorized` included |

  Evidence: 13 `probes/t08-*` files and `probes/README.md` § T08.
- **Baseline legacy-markup grep (observed):** macro, view, reject popup, all-institutions popup → `13 / 1 / 3 / 0` before, `0 / 0 / 0 / 0` after.
- **Overflow baseline:** the old page at 390 px measured 390 vs 390, so there was no overflow before the change. The new page measured 390 vs 390 and 1280 vs 1280, with the empty state also clean. Figtree `600 16px` was loaded (`t08-baseline-old.out`, `t08-measure-new.out`, `t08-measure-empty.out`).
- **Falsifiers (reverted, `t08-falsifier.out`):**
  - A `btn-success` left in the macro → grep `1`.
  - `.prq-card { min-width: 600px }` → 632 vs 390 at 390 px.
- **Screenshots:**
  - `t08-review-1280.png`, `t08-review-390.png`
  - `t08-edit-form-1280.png`
  - `t08-empty-1280.png`, `t08-empty-390.png`
  - `t08-dialog-reject-390.png`, `t08-dialog-reject-1280.png`
  
  Only the probe fixtures are shown: the other pending requests on the copy were deactivated during the capture, then restored. The requester is shown as "Test Requester".
- **Edge probe (`t08-edge-null-and-escape.out`):**
  - A request with no type, no country or no requester renders "Unknown" / "Not provided".
  - An injected `<b>` is escaped.
  - Attempt 1's own first draft returned a 500 on a request with no requester; it was fixed and verified. That failed render sent 2 test-env "Exception occurred" mails to the notification mailbox. Their `email_logs` rows were deleted on the copy.
- **Evidence re-run (Leader-inline):**
  - Legacy-markup grep `0 0 0 0`, and no `confirm(` in the new markup.
  - The 74 added `.properties` lines have no undoubled apostrophe and no non-numeric braces.
  - All files LF.
  - The probe files contain no personal data.
  - No app, no worktree, main `marlo-dev.properties` on `aiccradb2`, and `aiccradb2.partner_requests` still 5079.
  
  **VERIFIED.**
- **Reviewer: FAIL.** Verbatim:

  > 1. **Discovered Issue:** The stylesheet reference uses `marloInstitutions.css?20261120`, but the edit date is 2026-11-19. If T09 edits this CSS tomorrow (2026-11-20), the convention gives it `?20261120`, the URL stays the same and the cache is not busted. That is the exact failure the convention exists to stop. The gate's script would also flag this reference: `cache-bust-check.sh` accepts only today's date or today plus a suffix.
  >    * **Violated Rule:** `design.md` §5.3 "Cache-busting: new `?YYYYMMDD` on every CSS/JS file touched". `marlo-verify` skill Gate 3 (named in T08 Skills): "Convention is `?YYYYMMDD`, with a suffix for a second bump the same day".
  >    * **Remediation Suggestion:** Set the reference to the real edit date, `?20261119`, or `?20261119-1` to match the same-day suffix style. Re-run `.claude/skills/marlo-verify/scripts/cache-bust-check.sh` and record its `ok` line.

- **Leader note:** the cause was the Leader's own brief, which said "`?20261120` or later". The convention is the edit date.

#### Attempt 2 — PASS

- **Changes:**
  - `marloInstitutions.ftl:11`: `?20261120` → `?20261119`.
  - [advisory-grade] `institutionRequestMacro.ftl:128`: the country `selected` comparison is null-safe on both sides.
  - [advisory-grade] `marloInstitutions.css:58`: `.prq-notes:empty` → `.prq-notes:not(:has(*))`.
  
  The screenshots stay valid: the layout is unchanged with fixtures present.
- **Evidence re-run (Leader-inline):**
  - `cache-bust-check.sh` → `ok  .../superadmin/marloInstitutions.ftl:11 - ?20261119`. The admin view's `STALE` line is out of scope (DD-9).
  - Legacy grep `0 0 0 0`.
  - No `20261120` left in the diff.
  
  **VERIFIED.**
- **Reviewer: PASS.**
  - The fix matches §5.3 and Gate 3.
  - Both advisory edits are within scope.
  - The three spec amendments match the shipped code and change no meaning in UI-001…006.
- **runtime events:** none
- **spawns:**
  - implementer, attempt 1: 111 calls, 321841 tokens, ended complete
  - reviewer, attempt 1: 26 calls, 112057 tokens, ended fail
  - implementer, attempt 2: 10 calls, 88531 tokens, ended complete
  - reviewer, attempt 2: 9 calls, 45158 tokens, ended complete

#### Execute-time spec edits (recorded when made, from the attempt-1 advisories)

- `design.md` §5.5 "Review page" row: the summary shipped as `.summary.partners.one` / `.many` and `.summary.offices.one` / `.many`, because one key cannot handle singular and plural.
- `design.md` §5.2 "Inline edit form" row: plain `.prq-*` inputs with unique ids, instead of `customForm.select`, which duplicated the ids `type` / `country` on every card (UI-006).
- `design.md` §5.2 token row: the dialog scrim literal `rgba(31, 42, 55, .45)` is recorded beside the focus-ring literal.
- No requirement meaning changes.

#### Completion report items (required by "Done when")

- **Playbooks run:** layout, typeset, colorize, harden.
- **Polish light:** fixed a centred legend, an empty "changes" block on an edited request with no differing fields, a checkbox focus rule overridden by Bootstrap, and the null-requester crash.
- `finish review: not run (AKILI execute)`.
- **Detector:** `impeccable detect` failed with `spawnSync … EAGAIN`, so it is **Unevaluated**, not passed. To be run in `/akili-validate`.

#### ADVISORY (non-gating)

- **Card actions.** A solid red Reject beside a solid blue Accept on every card. The incumbent uses solid danger only inside dialogs. → owner review in T11b.
- **Checkbox focus evidence.** `t08-focus-contrast.out` records the checkbox's default ring captured before the fix. Re-capture it in T11b.
- **Admin view.** The admin view's CSS reference has no cache-buster. It is not rendered and is out of scope (DD-9).

#### Forward pointers → T09 (selectors broken by the new markup; copy into the T09 brief)

- **Removed ids and classes:**
  - `#myModal` and `#rejectOfficeRequest` are now `#prqRejectDialog`, `#prqRejectOfficesDialog`, `#prqAcceptDialog`, `#prqAcceptOfficesDialog` and `#allInstitutions`.
  - `.rejectButton`, `.sendEmailInput`, `.requestInfo`, `.btn-group`, `.editForm` and `.hiddenTitle` are gone.
- **Controls are buttons with `data-prq-action`:**
  - Values: `edit`, `reject`, `accept`, `reject-offices`, `accept-offices`, `save-edit`, `cancel-edit`.
  - Dialogs use `data-prq-close` and `data-prq-confirm`; the all-institutions trigger is `data-prq-open=allInstitutions`.
  - Request and institution ids come from `data-request-id` / `data-institution-id`, not from classes.
- **Edit form:**
  - Inputs are already named with the action's names (`requestID`, `institutionName`, `institutionAcronym`, `institutionWebPage`, `institutionTypeID`, `countryID`, `justification`).
  - Ids are `prq-edit-*-<id>`.
  - `type="submit"` with no `action`, so the JS must call `preventDefault`.
- **`hidden` attribute.** Forms, dialogs and `[data-prq-similar]` use the `hidden` attribute, and `.prq [hidden] { display: none !important }`. `.show()` / `.modal()` / `slideDown` will not reveal them, so the JS must toggle the attribute.
- **Similar institutions.** `findSameness` must read the name from `data-prq-name`; `.hiddenTitle` is gone. The block keeps the `.sameness` class and its inner `ul`.
- **Strings.** JS strings come from `data-outcome-*` and `data-text-*` on `#prqPage`.
- **Page updates.** Notes go into `#prqNotes`; summary and counts are `[data-prq-summary]` and `[data-prq-count]`.
- **Kept selectors:** `.partnerRequestItem`, `#partnerRequestItem-<id>`, `#officesRequestItem-<id>`, `.officeRequest`, `input.institutionID`, `.acceptRequestForm`, `.loading`.
- **Office card.** A checkbox's `label` is no longer the only label in its parent.
- **Accept.** The accept form currently submits with no confirmation; T09's `prqDialog` must gate it.

- **Requirements covered:** UI-001, UI-002, UI-003, UI-006 (markup and CSS), NF-002 (keys).
- **Budget (running, amended ceiling ~5,000 LOC / ~30 rounds):**
  - Tasks: 9 / 12.
  - LOC added: 3,188 + 964 = **4,152**.
  - Review rounds: 17.
  - Still under the ceiling, with T09, T10, T11b and T12 left. T10 (request page FTL + CSS + JS) is likely to cross ~5,000, which would stop the run at that gate.
- **Final verification:** VERIFIED (Leader re-runs, attempts 1 and 2).
- **Commit:** `cdcd092d62` (owner-approved).

### CHG-PARTREQ-001-T09 — Review screen behaviour: `prqDialog`, POST, inline outcomes

- **Final status:** PASS (attempt 2)
- **Date:** 2026-11-19
- **Attempts:** 2
- **Effort:** high on attempt 1; xhigh on attempt 2 (rework bump).
- **Skills:** `impeccable-bridge` (design route: playbooks harden and clarify; ENV-2; mode Operate; polish light; motion none), `marlo-verify`.
- **Review mode:** single Reviewer covering SECURITY, ACCESSIBILITY / RELIABILITY and spec conformance. The parallel-lens mode was not used, because the xhigh effort came only from the rework bump on a one-token cache-buster fix; this deviation is recorded here.

#### Attempt 1 — implicit FAIL (Leader evidence re-run)

- **Files changed:**
  - `marloInstitutions.js` rewritten (+673 / −195).
  - Cache-buster in `superadmin/marloInstitutions.ftl`: `?20261119b` → `?20261119c`.
  - New probes: `probes/t09-keyboard-walk.out`, `t09-outcomes-1280.png`, `t09-card-note-1280.png`.
- **Greps, before → after:** `confirm(|alert(|noty(` went `6` → `0`. `type: 'POST'` stayed at `4`.
- **Falsifiers:**
  - With the Tab trap removed (on the served copy only), the first Tab left the dialog (`inOpenDialog=false`). The task predicted the 4th Tab, but the dialog has only three focusable stops. With the trap restored, focus stays inside and Escape returns it to the opener.
  - A kept `confirm(` on a scratch copy gives grep `1`.
- **Keyboard walk (keyboard only, from the opener):**
  - The dialog has `role=dialog`, `aria-modal` and `aria-labelledby`. Focus moves to the justification, Tab cycles inside, and Escape returns focus to the opener.
  - Reject with an empty justification shows a field error and sends nothing. With notify off, it makes one POST, shows a `done` note, removes the card, updates the count (4 → 3) and sends 0 email.
  - Accept goes through the dialog, then a full-page POST and a redirect note.
  - A valid edit posts the action's names, gives `done` and shows the note after the reload.
  - A simulated 403 or 401 shows `notAuthorized`; a 500 shows `failed`.
  - Offices: nothing selected shows an error. Accept returns `{"5269":"done","5270":"skippedExisting"}`. Reject empties the list and shows the empty state.
- **Email safety:** the probe requester on the copy has the notification address. `marlo.production=false`. The run produced 2 `email_logs` rows, both to that mailbox.
- **Leader evidence re-run:**
  - Passed: `node --check` OK, greps `0` / `4`, the edit names and `sendNotification` present, LF, no line over 120 characters, no personal data in `t09-*`, clean environment, `aiccradb2` unchanged.
  - **Gate 3 failed:** `STALE  .../superadmin/marloInstitutions.ftl:6 - ?20261119c (today is 20261119)`. The script accepts only `?YYYYMMDD` or a same-day `-N` suffix. The letter suffix came from the Leader's brief, and `?20261119b` in the T07 reopening had the same defect (that one is superseded now). This counts as an implicit FAIL and consumed attempt 1.

#### Attempt 2 — PASS

- **Change:** `?20261119c` → `?20261119-1` (the committed `?20261119b` is replaced).
- **Leader re-run:** Gate 3 reports `ok … superadmin/marloInstitutions.ftl:6 - ?20261119-1`. The other lines the script flags are FTL comment text and the admin view (DD-9), all out of scope. **VERIFIED.**
- **Reviewer:** **PASS**. Points checked:
  - SEC-002: four POST calls. Accept is gated by `#prqAcceptDialog` and then a native `form.submit()`, which cannot loop.
  - The seven edit names are kept.
  - `sendNotification` is a literal `true` / `false`.
  - §5.4 is fully met: role and aria attributes, the opener is remembered, focus moves in, the Tab / Shift+Tab trap holds, Escape and close work unless a request is in flight, focus returns to the opener, and an empty justification shows `.prq-error` with `aria-invalid` and sends nothing.
  - §4: 403 and 401 map to `notAuthorized`.
  - XSS: every note is built with `.text()` / `createTextNode`. The old `findSameness` HTML concatenation of `composedName` was removed, which closes a pre-existing XSS path.
  - Strings come only from `data-` attributes.
  - The walk is valid: it is keyboard-only from the opener.
  - The Implementer's assumptions are accepted. Cards leave on statuses that are no longer pending (§5.3). The edit reloads the page. Notes are prefixed with the request name. The 403 is simulated here; the real one is T11a's evidence.
- **runtime events:** none
- **spawns:**
  - implementer, attempt 1: 80 calls, 257004 tokens, ended complete.
  - implementer, attempt 2: 4 calls, 83196 tokens, ended complete.
  - reviewer: 14 calls, 89711 tokens, ended complete.

#### Completion report items

- **Playbooks:** harden, clarify.
- **Polish light:** the in-card note sat flush against the card edges; it now goes at the end of the card body. Checked again on a screenshot.
- `finish review: not run (AKILI execute)`.
- **Detector:** not run. The engine reported `consent needed`, so the launcher-unavailable path was followed. To be run in `/akili-validate`.

#### ADVISORY (non-gating)

- **Edit fallback, RELIABILITY.** If the `sessionStorage` write fails, `finishEdit` removes the edited card even though the request is still pending. The fix is to show the card note and close the form when status is `done` and the note cannot be queued. → T12 follow-up list.
- **Expired session, RESILIENCE.** If `requireUser` answers with an HTML login page (200) rather than a 401, the user sees `failed` instead of `notAuthorized`. → check live in T11b.
- **Unknown statuses, READABILITY.** An unknown status shows the `failed` text in a warning-styled note.
- **Office accept with no existing locations, RELIABILITY.** The accepted-country chip does not appear until the page reloads.

#### Forward pointers

- **→ T11b:**
  - Real expired-session behaviour (401 vs a login redirect) on the AJAX calls.
  - Owner review of the screenshots and dialogs.
  - Re-capture the checkbox focus ring (T08 advisory).
  - The office-chip behaviour.
- **→ T12:** the edit-fallback defect and the office-chip refresh go on the follow-up list.

- **Requirements covered:** UI-004, UI-006 (dialogs), SEC-002 (AJAX POST).
- **Budget (running, amended ceiling ~5,000 LOC / ~30 rounds):**
  - Tasks: 10 / 12.
  - LOC added: 4,152 + 674 = **4,826**.
  - Review rounds: 19.
  - T10 (request page: FTL, CSS, JS, i18n) will cross the ~5,000 ceiling, which will stop the run at T10's gate unless the owner raises the ceiling first.
- **Final verification:** VERIFIED. Leader re-runs on both attempts; Gate 3 `ok` on attempt 2.
- **Commit:** see the T09 commit (owner-approved).

### Owner decisions at the T09 gate (2026-11-19)

1. **Budget ceiling raised** to ~5,800 LOC and ~30 → ~35 review rounds. This is recorded in `design.md` §15 as a second amendment. Crossing the new ceiling stops the run again.
2. **T10 proceeds.** The date moved to 2026-11-20, so the cache-busters for T10's files are `?20261120`.

### CHG-PARTREQ-001-T10 — Partner request page redesign

- **Final status:** PASS
- **Date:** 2026-11-20
- **Attempts:** 1
- **Effort:** high
- **Skills:** `impeccable-bridge` (route: layout, clarify, harden; ENV-2; Operate; polish light; motion none), `marlo-verify`.
- **Review mode:** lens checklist, single Reviewer (`Review: full`), screenshots read.

#### Attempt 1

- **Files changed** (all LF, as at HEAD):

| File | Change |
|---|---|
| `partnersSave.ftl` | +116 / −101; `.prq` form card; one help note; success card |
| `partnersSave.css` | +195 / −37 |
| `partnersSave.js` | +57 / −44 |
| `global.properties` | +4 `marloRequestCreation.success.title/.text/.again/.close` |

  New probe files: `probes/t10-live.out`, `t10-baseline-old.out`, and 11 `t10-*.png` (old, new, errors, success, savefailed, dropdown; at 390 and 1280).
- **Greps (before → after):**
  - `sent succesfully`: `1` → `0`
  - legacy markup: `3` → `0`
  - `name="save"`: `1` → `1`
- **Overflow:**
  - Old page at 390: 390 / 390, Helvetica, Figtree not loaded.
  - New page: 390 / 390, 1280 / 1280 and 600 / 600; the success card and the open select list are also clean.
  - Figtree is loaded. Contrast ≥ 4.5.
- **Live checks** (copy DB only; email switch of unit 45 off during the run and then restored; 0 new `email_logs` rows):
  - **All fields missing:** a field error under name, type and country, plus a warning note. Nothing stored.
  - **Country only missing:** one error under country; the typed values are kept.
  - **Valid submit:** 2 rows and the success card. A double click still stores 2 rows. Enter also submits.
  - **Forced failure:** a temporary `BEFORE INSERT` trigger on the copy, dropped afterwards (0 triggers left). Result: `saveFailed` danger note, values kept, `ERROR` log line.
- **Falsifier:** with `name="save"` removed, a valid submit stores 0 rows (red). Restored, it stores 2 rows (green).
- **Openers exercised:**
  - Project partners.
  - Innovation (`context` carried into `request_source`).
  - Study.
  - Funding source, opened by URL: on the copy the page is not editable, so the link is not rendered.
  - Capdev, POWB and AR not exercised, because the copy has no editable page for them. This is deferred to T11b.
- **Implementer completion items:**
  - Playbooks run: layout, clarify, harden.
  - Polish light fixed three defects:
    - a header border inherited from global styles;
    - a 6 px overflow from the select-list search box at 390;
    - an `outline:none` that hid the select focus ring.
  - `finish review: not run (AKILI execute)`.
  - The impeccable source scan had no findings. The URL scans are Unevaluated (login needed).
- **Evidence re-run (Leader-inline):**
  - Greps `0` / `1` / `0`.
  - All 13 bound names present.
  - `node --check` OK.
  - Gate 3 `ok … partnersSave.ftl:5 - ?20261120` (CSS) and `:4 - ?20261120` (JS).
  - Keys without apostrophes. LF.
  - No personal data in `t10-*`.
  - Clean environment, no trigger left, `aiccradb2` unchanged.
  
  **VERIFIED.**
- **Reviewer: PASS.**
  - `.prq` on existing `--marlo-*` tokens (about 27 checked).
  - §5.3 binding kept exactly.
  - The help slideshow is now one note.
  - The success copy comes from the §5.5 keys.
  - Every UI-005 state is present and matches AC UI-005 / UI-006 in the screenshots.
  - `aria-disabled` plus a guard is correct, because `save` is load-bearing.
  - Removing `message.success` is safe (no reference to it).
  - No client-side blocking, so the server errors stay the source.
  - The raw `${…}` values are auto-escaped (Struts 6.8 `dashboard.ftl:211-214`).
- **runtime events:** none
- **spawns:** implementer 124 calls, 288973 tokens, ended complete; reviewer 22 calls, 81908 tokens, ended complete

#### Execute-time spec edit (recorded when made)

- `design.md` §5.6, request page "Loading": implemented with `aria-disabled` and a guard instead of the `disabled` attribute, because a disabled submit drops `save` (§5.3). Requirement meaning is unchanged.

#### ADVISORY (non-gating)

- **i18n, pre-existing.** The labels "Name", "Type", "Acronym" and "Country" look up keys that do not exist and render fallback text. The page title `[#assign title = "Request new institution" /]` (`partnersSave.ftl:2`) is hardcoded. Both are outside the closed §5.5 key list. → Owner decision at this gate: a §5.5 amendment adding `marloRequestCreation.label.*` (and "Headquarters country" per UI-005), or a T12 follow-up.
- **Duplicate rule.** `.prq-btn` is declared twice in `partnersSave.css`.
- **Opener regression.** Capdev, POWB and AR are still to be checked in T11b.

#### Forward pointers

- **→ T11b:**
  - Opener regression for capdev, POWB and AR.
  - Owner review of the request page at 1280, 390 and the short heights.
- **→ T12:** the orphaned `marloRequestCreation.sendFailed` key; the label keys and the title, if not amended now.

- **Requirements covered:** UI-001, UI-005, UI-006 (request page), NF-002.
- **Budget (running, ceiling ~5,800 LOC / ~35 rounds):**
  - Tasks: 11 / 12 (T11b and T12 left).
  - LOC added: 4,826 + 372 = **5,198**.
  - Review rounds: 20.
- **Final verification:** VERIFIED.
