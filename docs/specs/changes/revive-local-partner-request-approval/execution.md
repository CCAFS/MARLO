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
