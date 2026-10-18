# Password Login Switch — Test Report

**Overall status: PASS with accepted gaps.** The `/api/**` part of AC-12 does not apply: HTTP Basic on `/api/**` is not used
in MARLO today (user, 2026-10-18). No product bug found. All 983 unit tests and every live probe that can run on the
local stack pass. One gap remains open:

- **AC-13:** live behaviour of 6 of the 7 welcome-email senders. They are covered only by a static check.

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | `CHG-PWD-SWITCH-001` |
| Spec path | `docs/specs/changes/password-login-switch` |
| Branch / HEAD under test | `A2-2631-Implement-Center-Email-OTP-Authentication` / `6a0c65e5e4` (+ 4 uncommitted test files of Suite 1) |
| Date | 2026-10-18 |
| Leader | Claude Code session, `opus` (T1) |
| Testers | `akili-tester` wrapper (`opus`). Implementers were `sonnet`, so author ≠ tester holds |
| Environment | Throwaway copy `aiccradb_otp_probe` of `aiccradb_actsave`, with a local SMTP sink on 127.0.0.1:2525. Dropped after the run |

## 2. Summary

| Suite | Tester | Result | Counts |
|---|---|---|---|
| Backend unit | Tester 1 | PASS. Its own status line read FAIL, but only because of the 2 test gaps; no assertion failed | 983 run, 0 failures, 0 errors, exit 0. Before: 976. 7 new tests |
| Frontend unit | — | **Not run.** `login.js` has no JS test runner, and choosing one is a TRD decision | — |
| Integration (live, non-author re-run of T08) | Tester 2 | PASS | AC-2 70/0, AC-10 23/0, AC-8 8/0, override 10/0, runbook SQL 5/0, AC-7 11/0, AC-12 2/0 + 1 NOT RUN, FN-009 14/0 |
| E2E | — | Not re-run. The T06 browser walkthrough (`probes/output/t06-walkthrough.md`) is cited | — |

- **Delegation:** 2 Testers, run one after the other. Both build into the same `target/`, and the run script wipes it.
- **Skills:**
  - Tester 1: `marlo-verify`, `tdd`.
  - Tester 2: `marlo-verify`, `systematic-debugging`.
- **Why the live suite was re-run:** the T08 probes had been run only by their author. Then the copy was dropped before an independent re-run.

## 3. Backend Unit Tests

Command, run before and after the new tests:
`rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes && mvn -o -pl marlo-web -am test`

- **Before:** 976 tests, 0 failures, exit 0. Clean compile of 2415 / 1066 / 85 files (marlo-data / marlo-web / tests).
- **After:** 983 tests, 0 failures, exit 0. Clean compile of 2415 / 1066 / 88 files.

The 7 new tests below were each seen red through a product mutation that was then reverted. At the end
`git diff -- marlo-*/src/main` is empty.

| New test | Clause | Red observed |
|---|---|---|
| `CrpByUserEmailActionTest#ac11OnAPasswordOffUnitBothFlagsAreTheSameForEveryAccountStateOfTheSameKind` | AC-11, SEC-005 | Mutation leaking `!usrDB.isActive()` → `inactive, member unit: otpEnabled expected:<true> but was:<false>` |
| `security/otp/PasswordSwitchConstantParityTest` (2) | DA-001 constants in both `APConstants` | `ComparisonFailure … expected:<…login_[active]> but was:<…login_[enabled]>` |
| `utils/WelcomeSendersUseTheSignInHelperTest` (2) | AC-13, all 7 senders use the helper (static, §6 gate) | `ClusterActivitiesAction` reverted to the old `getText(...)` → expected set ≠ actual set |
| `utils/WelcomeCodeSignInCopyTest` (2) | FN-008, UI-003: rendered copy keeps its arguments, has no Password label | A single `'` restored in `custom/aiccra3.properties` → `argument {0} of today's copy must be rendered` |

Checkstyle, run directly and compared with HEAD on the 4 test files: delta 0. The hygiene check is clean.

## 4. Frontend Unit Tests

None. MARLO has no JS test runner, and picking one is a stack decision for the TRD, not something this command can make
(see Accepted Gaps). The views in `login.js` are covered by the T06 browser walkthrough (§6).

## 5. Integration Tests

The tester rebuilt the copy from `aiccradb_actsave`. Before any write, `ps eww` showed the JVM using
`MYSQL_DATABASE=aiccradb_otp_probe`. Each start was a clean build with BUILD SUCCESS.

| AC / clause | Command | Result | Output (`probes/output/`) |
|---|---|---|---|
| AC-2 / FN-003 | `ac-live.sh all-on` + `center-email-otp-login/probes/ac-probe.sh enum live attempts resend removed limits nonprod session leak expiry` | 70/0 | `suite2-ac2-switch-on-ac-probe.out` |
| AC-10 / SEC-001, SEC-002, OPS-001 | `ac-live.sh ac10` | 23/0. A refusal is byte-equal to a wrong password, cookies included. No credential check, no LDAP, no session | `suite2-ac10.out` |
| AC-8 / FN-002 | `ac-live.sh ac8` | 8/0. Raw SQL, same JVM pid, both directions | `suite2-ac8.out` |
| AC-3 to AC-6 | `ac-live.sh override` | 10/0 | `suite2-override.out` |
| OPS-003 | `ac-live.sh runbook-sql` | 5/0, run with `PROBE_RUNBOOK=runbook.md` (H2) | `suite2-runbook-sql.out` |
| AC-7 / FN-006, OPS-002 | Restart with `OTP_HMAC_SECRET=""`, then `ac-live.sh ac7` | 11/0, one ERROR per unit | `suite2-ac7.out` |
| AC-12 / SEC-006, CLARISA | `ac-live.sh ac12` | PASS: session read from the JVM | `suite2-ac12.out` |
| AC-12 / SEC-006, `/api/**` | `ac-live.sh ac12` | Answers the same with the switch on and off. **Authentication N/A** (not used in MARLO today): 401 even with the switch on (pre-existing `HibernateException` in `getUserByEmail`) | `suite2-ac12.out` |
| FN-009 / AC-13 (`CrpUsersAction`) | `probes/fn009.py`: password switch of unit 45 off, user added through POST `/admin/AICCRA/crpUsers.do` | 14/0. Results below the table | `suite2-fn009.out` |
| T05 advisory walk | `ac-live.sh walk` | 1/0. Item 1 (a real AD refusal) cannot be reached offline | `suite2-walk.out` |

FN-009 results:
- The stored password is non-null and in MD5 form.
- The welcome email carries the code copy and no Password row.
- The control run with the switch on gets today's email, and its password hashes to the stored value.

**Falsifiers (AC-8):**

- **A (`ac8.FALSIFIER.patch`, the author's):** red, 5 of 8 failed.
  - The tester found it does **not** test a stale cache. Hibernate cannot cache this native scalar query (`IllegalStateException: aliases expected length is 0; actual length is 3`), so the read fails open and the switch is never read.
  - It proves only that the probe notices a build that ignores the switch.
- **B (`suite2-ac8.FALSIFIER-B.patch`, new, temporary):** the DAO keeps the first values for the life of the JVM. Red 3/8: the off-to-on change is missed. This is a real stale-value red.
  - Limit: in this probe order the on-to-off direction cannot be falsified.
- **Restored build:** green 8/0. `git diff -- marlo-*/src/main` is empty.

**Harness defects found (no product impact):**

- **H1:** `fixtures.sh` says user 3862 is a member of units 45 and 47, but it inserts only the unit 45 membership. The T08 run passed because a row left by an earlier task was still there.
- **H2:** `ac-live.sh runbook-sql` resolves `runbook.md` from the current folder, so it crashes when run from the spec folder.

**Cleanup:** app and sink stopped, and nothing listens on :8080 or :2525. `aiccradb_otp_probe` dropped.
`aiccradb_actsave` is untouched. Scratch credentials deleted. The `marlo-dev.properties` hash is unchanged.

## 6. E2E Tests

Not re-run in this phase. The step-3 views (UI-001, UI-002, UI-004, UI-005; AC-3, AC-15) are covered by the T06 browser walkthrough
(`probes/output/t06-walkthrough.md`). It was reviewed with a Reviewer PASS and a visual check by the user at the T06 HITL pause.
That evidence was produced by the task's author.

## 7. Coverage & Traceability

Unit rows come from Tester 1. Each mapping was confirmed by opening the test body, not by matching names. Live rows come from Tester 2.

| Requirement | Scenario | Test Type | Test File or Command | Result | Gap or Notes |
|---|---|---|---|---|---|
| FN-001 default on, missing reads on | AC-1 | unit | `SignInMethodRuleTest#ac1MissingPasswordSwitchReadsAsOnAndKeepsTodaysMethods`, `#ac1EmptyPasswordSwitchValueIsOn`, `#ac1NullPasswordSwitchValueIsOn`, `#passwordSwitchValueThatIsNotExactlyFalseIsOn`; `OtpEligibilityTest#onlyAPasswordSwitchThatIsExactlyFalseAdmitsTheOverride`; `WelcomeSignInTextTest#aMissingSwitchReadsAsOn` | PASS | Seed on a real database: `probes/ac1-ac14-migration.sh` → `probes/output/ac1-ac14-migration.green.out` (live, cited) |
| FN-002 live change | AC-8 | unit | `OtpEligibilityTest#ac8ThePasswordSwitchIsReadAgainOnEveryCallSoAChangeAppliesOnTheNextRequest`; `OtpRequestCodeActionTest#ac8TurningThePasswordBackOnAppliesToTheNextRequestOfTheSameWorld` | PASS | Unit part only (no cache between calls). Live: Suite 2 (`probes/output/ac8.*`) |
| FN-003 switch on = T21 | AC-2 | unit | `SignInMethodRuleTest#fn003UsernameOnAPasswordOnUnitKeepsThePassword`, `#everyCombinationYieldsTheMethodsOfTheRuleTable`; `OtpRequestCodeActionTest#onAPasswordOnUnitTheCognitoFlagIsNeverResolvedAndIsCgiarUserIsNeverRead`; `OtpVerifyCodeActionTest#onAPasswordOnUnitTheRecheckNeverResolvesTheCognitoFlagForAnyAccountState`; `CrpByUserEmailActionTest#everyPreExistingKeyAndValueIsUnchangedOnAndOff`; every pre-existing CHG-OTP-LOGIN-001 test green in the 983 | PASS | Live `ac-probe.sh` stages: Suite 2 |
| FN-004 code only, even code off / excluded | AC-3, AC-4, AC-5 | unit | `SignInMethodRuleTest#ac3…`, `#ac4…`, `#ac5…`; `OtpEligibilityTest#ac4WithThePasswordOffTheCodeIsOfferedWhileTheCodeSwitchIsOff`, `#ac5…`, `#ac4a…`, `#ac5a…`; `CrpByUserEmailActionTest#everyRowOfTheRuleForAnAccountWithoutCognito` (A3, A4); `OtpRequestCodeActionTest#ac4…`, `#ac5…`; `OtpVerifyCodeActionTest#ac4TheRecheckAcceptsAMemberUnderTheOverride…` | PASS | "shown directly, without the choice menu" is UI: T06 walkthrough `probes/output/t06-walkthrough.md` |
| AC-3 BUT password POST refused, AND same message as wrong password | Password off, code on | unit | `ValidateUserActionGuardTest#thePasswordSwitchRefusesAMemberOfAPasswordOffUnitAndMakesNoCredentialCall`, `#aRefusedLocalAccountAnswersAsARealWrongPasswordDoes`, `#aRefusedCgiarAccountAnswersAsARealWrongPasswordDoes`, `#aRefusedInactiveAccountAnswersAsARealWrongPasswordDoes`, `#aRefusalSendsTheSameCookiesAsARealWrongPasswordOnTheWebSecurityManager`; same five in `LoginActionCgiarGuardTest` | PASS | Both password endpoints |
| AC-4 AND request-code MUST issue a code, CGIAR or not | Everything else off | unit | `OtpRequestCodeActionTest#ac4WithThePasswordOffACodeIsIssuedAndMailedWhileTheCodeSwitchIsOff` (non-CGIAR, endpoint); `OtpEligibilityTest#anAccountIsACognitoAccountOnlyWhenItIsCgiarAndTheUnitHasCognito` (CGIAR, no Cognito → eligible; same `isEligible` the endpoint calls); `CrpByUserEmailActionTest#aCognitoAccountNeverGetsThePasswordAndKeepsItsCodeSettings` row C4 | PASS | CGIAR case proven at the predicate, not at the endpoint (thin composition; accepted) |
| AC-5 BUT password only once the switch is back on | Domain excluded | unit | `SignInMethodRuleTest#ac5PasswordOffDomainExcludedGivesTheCodeAndThePasswordOnlyOnceTheSwitchIsBackOn` | PASS | |
| FN-005 / SEC-003 Cognito unchanged | AC-6 | unit | `SignInMethodRuleTest#ac6…`, `#aCognitoAccountAlwaysHasCognitoAndNeverThePassword`; `CrpByUserEmailActionTest#aCognitoAccountNeverGetsThePasswordAndKeepsItsCodeSettings`; `ValidateUserActionGuardTest#aMigratedCgiarAccountIsStillRefusedBySec005WithTheGateLive`; same in `LoginActionCgiarGuardTest`; `WelcomeSignInTextTest#aCognitoAccountKeepsTodaysTextEvenWithThePasswordOff` | PASS | |
| AC-6 BUT direct code request MUST NOT issue a code | Cognito account | unit | `OtpRequestCodeActionTest#ac6ACognitoAccountIsRefusedUnderTheOverrideWithTheNeutralSentAndADecoyRow`; `OtpEligibilityTest#ac6ACognitoAccountIsRefusedUnderTheOverrideAndOnlyAfterTheMembershipCheck`; `OtpVerifyCodeActionTest#aCodeIssuedUnderTheOverrideIsRefusedAtTheRecheckOnceTheAccountBecomesACognitoAccount` | PASS | Asserts zero mails and neutral response |
| FN-006 last resort | AC-7 | unit | `SignInMethodRuleTest#ac7LastResortPasswordWhenTheCodeKeyIsMissing`; `CrpByUserEmailActionTest#withNoKeyThePasswordIsOfferedToEveryAccountWithoutCognito`; `OtpEligibilityTest#ac7MethodsForRunsItsOneQueryAlsoWithoutACodeKeyAndTheLastResortIsThePassword`; `ValidateUserActionGuardTest#withoutTheCodeKeyTheGateNeverRefusesAndReadsNothing` (AND accepted under today's rules); same in `LoginActionCgiarGuardTest`; `OtpRequestCodeActionTest#ac7WithoutAKeyTheOverrideSendsNothingAndReadsNothing` | PASS | Live ERROR: Suite 2 (`probes/output/ac7.green.out`) |
| AC-7 AND ERROR line / OPS-002 once per unit, not repeatable | Last resort | unit | `OtpEligibilityTest#ops002TheLastResortIsLoggedOnceAtErrorPerUnitAndNamesTheUnitOnly`, `#ops002NothingIsLoggedWhenThePasswordIsNotOfferedOnlyBecauseOfTheMissingKey`, `#ops002TheMemoryBelongsToTheInstanceSoItStartsEmptyWithTheApplication`; `CrpByUserEmailActionTest#theLastResortErrorIsWrittenOncePerUnitAcrossRepeatedCalls`, `#noLastResortErrorWhenTheKeyIsValid` | PASS | Live: Suite 2 |
| FN-007 no account without a method | AC-9 | unit | `SignInMethodRuleTest#everyActiveMemberHasAMethodOrIsToldToUseTheEmail` (all combinations of Cognito × code switch × exclusion × password switch × key × identifier shape), `#noCombinationOffersMoreThanTwoMethods`, `#everyCombinationYieldsTheMethodsOfTheRuleTable`, `#everyRawFormOfTheSwitchGivesTheSameRowAsItsOnOrOffMeaning` | PASS | |
| FN-010 / AC-15 username → use your email, MUST NOT show password | Username typed | unit | `SignInMethodRuleTest#fn010UsernameWithOnlyTheCodeLeftNeedsTheEmailAndOffersNoPassword`, `#theCodeIsNeverOfferedToAUsernameOrWithoutTheKey`; `OtpEligibilityTest#ac15MethodsForAUsernameRunsOneQueryAndAPasswordOffUnitNeedsTheEmail`; `CrpByUserEmailActionTest#aUsernameGetsNoCodeAndTheSameOneQuery`; gates: `ValidateUserActionGuardTest#anAccountTypedAsItsUsernameIsGatedToo`, `LoginActionCgiarGuardTest#anAccountTypedAsItsUsernameIsGatedThroughItsStoredEmail` | PASS | Message rendering, back action, "code once restarted with email": UI → T06 walkthrough (non-unit) |
| FN-008 welcome email omits password when unavailable; unchanged otherwise | Welcome email | unit | `WelcomeSignInTextTest#passwordOffUsesTheCodeTextWithTheUnitAcronymAndNoPassword`, `#passwordOnKeepsTodaysTextWithThePassword`, `#aCgiarAccountWithoutCognitoInTheUnitGetsTheCodeText`, `#withoutAValidCodeKeyThePasswordIsTheLastResortAndTheTextIsToday`, `#theSwitchIsReadForTheUnitItWasGivenAndForNoOther`, `#aFailingSettingsReadKeepsTodaysText` | PASS | |
| FN-008 / UI-003 rendered copy carries no password, all args survive MessageFormat | Welcome email | unit | **NEW** `utils/WelcomeCodeSignInCopyTest#theRenderedCodeCopyCarriesEveryArgumentAndNoPasswordLabel`, `#todaysCopyInTheSameFilesPrintsTheFourthArgumentAsThePassword` (fixture discriminates); cited `WelcomeEmailCodeValidityTest#everyWelcomeCopyStatesTheRealExpiryInMinutes` | PASS | Red: single `'` in `custom/aiccra3.properties` → "argument {0} of today's copy must be rendered". Note: AICCRA copies (today's and new) never print `{4}`; pre-existing, not a defect of this spec |
| AC-13 for each of the 7 senders | Welcome email | unit (static lock, §6 gate) | **NEW** `utils/WelcomeSendersUseTheSignInHelperTest#everySenderBuildsTheWelcomeTextThroughTheHelperAndNoOneElseDoes`, `#onlyTheHelperAndTheCallerlessLoaderNameTheOldKey`; behavioural for CrpUsersAction: `CrpUsersActionWelcomeMailTest#theHelperReceivesTheUnitTheAccountIsCreatedFor` | PASS | Red: ClusterActivitiesAction reverted to `getText("email.newUser.part1", …)` → both methods fail. Six senders other than CrpUsersAction have no behavioural test (would need each action's full constructor graph) — static lock only |
| FN-009 password still stored | Welcome email | inspection | Diff of the 7 senders `5f11bc37e2..HEAD`: only the text call changed; `RandomStringUtils.randomNumeric(6)` + `setPassword` / `BaseAction.addUsers` untouched; `CrpUsersActionWelcomeMailTest` asserts the generated password reaches the helper | PARTIAL | TEST_GAP: no unit test proves storage while the switch is off (save path of the senders needs DB managers). Live Suite 2 / T07 evidence recommended |
| BUT switch-on unit → today's email with password | Welcome email | unit | `WelcomeSignInTextTest#passwordOnKeepsTodaysTextWithThePassword`, `#theSwitchIsReadForTheUnitItWasGivenAndForNoOther` | PASS | |
| SEC-001 refuse before credential check and LDAP; fail-open exception | AC-10 | unit | `ValidateUserActionGuardTest#thePasswordSwitchRefuses…MakesNoCredentialCall` (loginCalls empty → no realm, no LDAP), `#aGlobalUnitIdTheAccountDoesNotBelongToCannotRoundThePasswordGate`, `#theUnitOfTheAttemptDecidesWhenTheAccountIsAMemberOfIt`, `#withNoUnitNamedTheGateSweepsEveryMembership`, `#anUnknownAccountIsNotRefusedAndTheCredentialCheckRunsAsToday`, `#aFailingSettingsQueryFailsOpenWithOneErrorLine`, `#anIdentifierTheRealmNormalisesStillMeetsTheGateThroughTheStoredEmail`; mirrored in `LoginActionCgiarGuardTest`; `PasswordGateScopeTest#everyCallerOfTheCredentialCheckIsAKnownDoorAndTheGateGuardsTheLoginPageOnes` (gate before `userManager.login`) | PASS | Live: Suite 2 |
| SEC-002 indistinguishable | AC-10 | unit | `…#aRefusedLocalAccountAnswersAsARealWrongPasswordDoes`, `#aRefusedCgiarAccount…`, `#aRefusedInactiveAccount…`, `#aRefusalSendsTheSameCookies…`, `#theAcceptedResidualACgiarAccountTheDirectoryWouldRejectGetsTheCgiarTextOnRefusal` (both endpoints); `OtpEligibilityTest#aRefusalLeavesWhatTheRealmLeavesForEachKindOfAccount`, `#aRefusalRunsTheRememberMeFailedLoginHandling…` | PASS | Timing difference accepted in Decision Log |
| OPS-001 one INFO line, gate + unit, no password | AC-10 | unit | `ValidateUserActionGuardTest#thePasswordSwitchRefuses…` asserts exactly one INFO `auth.password.refused gate=password_switch gu=OFFUNIT` and that nothing logged mentions the email or the password; `OtpEligibilityTest#theGateRefusesAMemberOfAPasswordOffUnitAndLogsOneLineWithoutTheAccount` | PASS | |
| SEC-004 code endpoints apply the rule, neutral | AC-4 | unit | `OtpRequestCodeActionTest#everyAccountStateGetsTheNeutralAnswerWithThePasswordOffAndTheCodeSwitchOff`, `#sec004OnAPasswordOffUnitEveryAccountPaysTheCognitoReadBeforeTheLookup`, `#aRequestThatIssuedNoRealCodeNeverCarriesViaUnderTheOverride`, `#anUnknownUnitIsNotRescuedByTheOverrideOfAnotherUnit`; `OtpEligibilityTest#sec004…`, `#theOverrideWidensOnlyTheSwitchAndTheExclusionNeverTheAccountChecks`; `OtpVerifyCodeActionTest#theRecheckReadsTheCognitoFlagForTheUnitAfterTheConsumeAndBeforeTheAccountLookup` | PASS | |
| SEC-005 / AC-11 lookup discloses no more; new field same for every account of a kind | — | unit | `CrpByUserEmailActionTest#everyPreExistingKeyAndValueIsUnchangedOnAndOff` (key sets: only `passwordEnabled` added), `#anUnknownEmailDisclosesNeitherIsCgiarUserNorCrps`, `#otpEnabledIsIdenticalForAnActiveAndAnInactiveUser…` (password-on only); **NEW** `CrpByUserEmailActionTest#ac11OnAPasswordOffUnitBothFlagsAreTheSameForEveryAccountStateOfTheSameKind` | PASS | Was a GAP: no test proved account-blindness of `passwordEnabled`/override `otpEnabled` on a password-off unit. Red: activity leaked into the Cognito-account map → "inactive, member unit: otpEnabled expected:<true> but was:<false>" |
| SEC-006 CLARISA and `/api/**` untouched | AC-12 | unit (static) | `PasswordGateScopeTest#theFilesTheSwitchMustNotGovernDoNotMentionIt`, `#everyBuilderOfAPasswordTokenIsAKnownDoor` | PASS | Live authentication: Suite 2 |
| SEC-007 session switch unchanged | — | unit (static) | `PasswordGateScopeTest#theFilesTheSwitchMustNotGovernDoNotMentionIt` (`ValidSessionCrpInterceptor` does not mention the switch) | PASS | Accepted risk; no behaviour to assert |
| OPS-003 runbook | — | document | `docs/specs/changes/password-login-switch/runbook.md` | n/a | Not unit-testable |
| DA-001 migration seed, idempotent, no unit id | AC-1 | live probe | `probes/ac1-ac14-migration.sh` (+ FALSIFIER outputs) | PASS (cited) | Live DB, not unit |
| DA-001 constants in both APConstants equal the key | — | unit | **NEW** `security/otp/PasswordSwitchConstantParityTest#bothApConstantsDeclareTheSwitchWithTheValueOfItsKey`, `#theMigrationSeedsExactlyTheKeyTheConstantsName` | PASS | Was a GAP. Red: marlo-data value changed → `ComparisonFailure … expected:<…login_[active]> but was:<…login_[enabled]>` |
| DA-002 / DA-003 descriptions byte for byte | AC-14 | live probe | `probes/ac1-ac14-migration.sh` desc_check → `probes/output/ac1-ac14-migration.green.out` | PASS (cited) | Live DB, not unit |
| NF-001 one rule | — | unit | `SignInMethodRuleTest` (rule); consumers call it: `OtpEligibilityTest#methodsForAnswersEveryCellOfTheRuleFromOneQueryWithAllThreeKeys`, `#theGateReadsThePasswordSwitchLikeTheRuleDoes`; **NEW** `WelcomeSendersUseTheSignInHelperTest` (no sender bypasses the rule) | PASS | |
| NF-002 at most one query per lookup | — | unit | `CrpByUserEmailActionTest#oneQueryPerCallForExactlyTheLoadedUnitsInListedOrder`, `#unconfiguredMakesExactlyOneSpecificityQueryAndOtpIsFalseForEveryUnit`, `#aUsernameGetsNoCodeAndTheSameOneQuery`, `#theCognitoFlagIsReadOncePerUnitAndTheRuleAddsNoRead` | PASS | |
| UI-001 / UI-002 / UI-004 / UI-005 | AC-3, AC-15 | browser (non-unit) | `probes/output/t06-walkthrough.md` | cited | Out of this suite; `login.js` has no harness. UI-005 key `login.otp.usernameOnly` exists in `global.properties` (presence only) |
| AC-2 / FN-003 (live) | Switch on everywhere | integration | `ac-live.sh all-on` + `ac-probe.sh` (10 stages) | PASS | Non-author re-run |
| AC-7 / OPS-002 (live) | Last resort | integration | `ac-live.sh ac7` with a blank key | PASS | |
| AC-8 / FN-002 (live) | Live change | integration | `ac-live.sh ac8` + falsifiers A, B | PASS | On-to-off staleness not falsifiable in this probe order |
| AC-10 (live) | — | integration | `ac-live.sh ac10` | PASS | |
| AC-12 CLARISA (live) | — | integration | `ac-live.sh ac12` | PASS | |
| AC-12 `/api/**` authenticates (live) | — | integration | `ac-live.sh ac12` | N/A | `/api/**` is not used in MARLO today (user, 2026-10-18). The switch provably does not change its answer |
| FN-009 password still stored (live) | Welcome email | integration | `suite2-fn009.out` | PASS | Closes the unit PARTIAL for `CrpUsersAction`. Stored, not proven usable |

## 8. Remediation

No product defects. Harness and documentation fixes, none of them blocking. R1–R3 are deferred until the live probes are next re-run (user, 2026-10-18):

| # | Item | Fix |
|---|---|---|
| R1 | H1: `fixtures.sh` misses the unit 47 membership of user 3862 | Insert the row in `fixtures.sh` |
| R2 | H2: `ac-live.sh runbook-sql` depends on the current folder | Resolve `runbook.md` relative to the script |
| R3 | `ac8.FALSIFIER.patch` is described as a cache falsifier | Re-describe it in `probes/README.md` and the T08 entry as "switch never read". Keep falsifier B as the stale-value red |
| R4 | The 4 new test files are uncommitted | Committed with this report |

## 9. Accepted Gaps

| Gap | Reason | Owner / next step |
|---|---|---|
| AC-12: `/api/**` Basic authenticates | **Not applicable.** `/api/**` HTTP Basic is not used in MARLO today (user, 2026-10-18). The probe shows the switch does not change its answer; it returns 401 locally because of a pre-existing `HibernateException` in `APCustomRealm` → `getUserByEmail` | None. If `/api/**` comes back into use, re-run `ac-live.sh ac12` on an environment where it authenticates |
| AC-13 live behaviour for 6 of the 7 senders | Each action needs its full constructor graph. The static lock (`WelcomeSendersUseTheSignInHelperTest`) plus the live `CrpUsersAction` run cover the shared path | Accepted. Revisit if a sender diverges |
| FN-009 "usable" password | With the switch off nobody sees the generated password, so only storage can be proven | Accepted. The password becomes usable again when the switch is turned back on, which the control run proves |
| Frontend unit for `login.js` | No JS runner in MARLO (a TRD decision) | T06 walkthrough cited; runner choice to the TRD open questions |
| E2E not re-run | Author walkthrough evidence with a user visual check | Re-run the walkthrough if `login.js` changes |
