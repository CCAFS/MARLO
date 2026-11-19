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
