# Center Email OTP Login — Test Report

**Overall status: PASS with accepted gaps.** No product bug found. The full unit suite (986 tests, 3 of them new)
passes. An independent, non-author re-run of every live probe passes on a fresh throwaway copy: 139 `ac-probe` and
concurrency checks (the sum of the `SUMMARY` lines of the recorded `akili-test-*.out` files), `ac21` 17/0 and all three `ac17` database shapes, all with 0 failures in the recorded runs. *(Corrected 2026-10-18, validation V-8: this said 178.)*
Every AC from AC-1 to AC-21 has passing evidence for each clause that can be tested on this machine. The gaps that
remain are the ones `requirements.md` §6 already accepts, plus two small ones recorded in §9.

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | `CHG-OTP-LOGIN-001` |
| Spec path | `docs/specs/changes/center-email-otp-login` |
| Branch / HEAD under test | `A2-2631-Implement-Center-Email-OTP-Authentication` / `a76807b4f9` at the time of the run, plus 1 uncommitted test file from Suite 1. The test report itself was committed at `6560149089` *(corrected 2026-10-18, validation V-8)* |
| Date | 2026-10-18 |
| Leader | Claude Code session, `opus` (T1) |
| Testers | `akili-tester` wrapper (`opus`). The Implementers were `sonnet`, so author ≠ tester holds |
| Requirements under test | `requirements.md` as amended 2026-10-15 (T21) and 2026-10-17 (`CHG-PWD-SWITCH-001`) |
| Environment | Throwaway copy `aiccradb_otp_probe` of `aiccradb_actsave`, local SMTP sink on 127.0.0.1:2525, `EMAIL_NOTIFICATION=support.probe@sink.test`. Dropped after the run |

## 2. Summary

| Suite | Tester | Result | Counts |
|---|---|---|---|
| Backend unit | Tester 1 | **PASS** | 986 run, 0 failures, 0 errors, Maven exit 0. Before: 983. 3 new tests |
| Frontend unit | — | **Not run.** `login.js` has no JS test runner, and choosing one is a TRD decision | — |
| Integration (live, non-author re-run of the T15/T19/T21 probes) | Tester 2 | **PASS** | 13 stages run, 0 failures in the recorded runs. 1 harness attempt discarded (§5.3) |
| E2E | — | Not re-run. The T17 and T21 browser walkthroughs are cited (§6) | — |

- **Delegation:** 2 Testers, one after the other, because both build into the same `target/`.
- **Skills:**
  - Tester 1: `marlo-verify`, `tdd`. Effort high.
  - Tester 2: `marlo-verify`, `systematic-debugging`. Effort xhigh.
- **Why the live suite was re-run:** the T15 probes and their T19/T21 stages had only been run by the Implementer who
  wrote them. The Leader's re-run after T21 covered compilation and unit tests, not the live stack. Seven stages
  (`expiry`, `resend`, `smtp`, `blackhole`, `tz`, `nosecret`, `nonprodblank`) had not been run since before T19–T21.

## 3. Backend Unit Tests

Command, run before and after the new tests:
`rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes && mvn -o -pl marlo-web -am test`

| Run | Tests / Failures / Errors | Maven exit | Compiled files (marlo-data / marlo-web / tests) |
|---|---|---|---|
| Baseline | 983 / 0 / 0 | 0 | 2415 / 1066 / 88 |
| Final | 986 / 0 / 0 | 0 | 2415 / 1066 / 89 |

**One run discarded:** the first final run ended with BUILD FAILURE (`ImpactAreasIndicatorMapperImpl.java: error reading
… "toplevel.sourcefile" is null`). The cause was the Checkstyle script, which runs Maven, being started during the
build. That is the known dirty-tree failure, not a code problem. `target/generated-sources` was wiped and the build
run again on its own; that run is the "Final" row.

### 3.1 New tests

File: `marlo-web/src/test/java/org/cgiar/ccafs/marlo/security/otp/OtpSpecificityConstantParityTest.java`. These are
static checks of the shipped sources and SQL. Each was seen red through a temporary product change, reverted right
after. At the end, `git diff -- 'marlo-*/src/main'` is empty.

| Test | Clause | Red observed |
|---|---|---|
| `bothApConstantsDeclareBothCodeSignInKeysWithTheValueOfTheirParametersKey` | DA-002, Hard rule 4: both constants exist in both `APConstants` files and equal `parameters.key` | `OTP_EXCLUDED_EMAIL_DOMAINS expected:<[system]_otp_excluded_email_...> but was:<[crp]_otp_excluded_email_...>` |
| `theT21MigrationSeedsBothKeysOffForTypesOneToFourAndRemovesTheAllowListInForeignKeyOrder` | DA-002: seeded off for types 1–4; allow-list removed, `custom_parameters` first | `system_otp_login_active is seeded off / empty expected:<[fals]e> but was:<[tru]e>`; with the two `DELETE`s swapped: `the allow-list parameters rows are removed, after their custom_parameters` |
| `theDescriptionLeftStoredForEachKeyIsExactlyTheTextOfTheRequirement` | DA-002 descriptions, as amended by DA-003 | `expected:<...e exclusion applies [only ]to users of Sign in ...> but was:<...e exclusion applies []to users of Sign in ...>` |

Checkstyle 8.18, run directly with a baseline: 0 violations at HEAD and 0 in the working tree, so the delta is 0. The
hygiene check is clean, and the new file has the GPL header.

### 3.2 Existing coverage, cited

The Implementers' tests (about 37 classes on this branch) were mapped clause by clause, not rewritten. §7 gives the
mapping for each AC. The main classes are:
- **Actions:** `OtpRequestCodeActionTest`, `OtpVerifyCodeActionTest`, `CrpByUserEmailActionTest`, `SendFailEmailActionTest`.
- **Rules and mail:** `OtpEligibilityTest`, `OtpDomainPolicyTest`, `OtpMailSenderTest`, `SignInMethodRuleTest`.
- **Limits and storage:** `OtpRateLimiterTest`, `OtpChallengeManagerImplTest`, `OtpRateLimitManagerImplTest`.
- **Primitives:** `OtpCodesTest`, `OtpKeysTest`, `OtpPendingTest`, `ClientIpResolverTest`.
- **Login guards:** `LoginActionCgiarGuardTest`, `ValidateUserActionGuardTest`.

## 4. Frontend Unit Tests

None. MARLO has no JS test runner, and picking one is a stack decision for the TRD (see §9). The `login.js` views are
covered by the browser walkthroughs cited in §6, and their server-side flags by the `live` and `enum` stages.

## 5. Integration Tests

### 5.1 Setup proof

- **Throwaway copy:** `aiccradb_otp_probe` is a fresh `mysqldump --single-transaction --routines --triggers` of
  `aiccradb_actsave`. Both have 619 tables and an identical fingerprint (CHECKSUM TABLE on 13 key tables plus row counts).
- **The app used the copy:**
  - `ps eww` of the JVM on 8080 showed `MYSQL_DATABASE=aiccradb_otp_probe`, the sink and the secret. The secret's
    value was not printed.
  - A password login of probe user 3862 moved `last_login` in the copy to `2026-10-18 11:07:41`, while
    `aiccradb_actsave` still read `2026-10-13 12:56:27`.
  - This check was repeated after every restart.
- **Build:** `scripts/run-marlo-java17.sh`, once (2415 / 1066 / 89, BUILD SUCCESS). At startup Flyway applied the T21
  migration and `AddPasswordLoginSwitch` to the copy, both with `success = 1`.
- **Afterwards:**
  - `aiccradb_actsave` has the same fingerprint as before. Nothing was written to `aiccradb1`–`aiccradb4`.
  - `marlo-dev.properties` was backed up and restored; its SHA-256 is the same before, after and after the restore.
  - The app and the sink are stopped, no `cargo:run` is left, the copy and every scratch database are dropped, and
    the scratch credentials, mbox and console logs are deleted.

Evidence: `probes/output/akili-test-setup-proof.out`, `probes/output/akili-test-fixture.out`.

### 5.2 Stages

All outputs are under `probes/output/` with the prefix `akili-test-`.

| Stage | AC | Result | Output |
|---|---|---|---|
| `ac17-migration.sh` | AC-17 | All 3 database shapes: exit 0 on both runs, nothing duplicated | `ac17-migration.out` |
| `ac21-migration.sh` | AC-21 | 17/0 | `ac21-migration.out` |
| `concurrency-probe.sh` (first after start) | AC-7, FN-007/008/012/013 | 13/0. Case 1: `{ok:1, invalid:9}`, one consumed row | `concurrency-http.out` |
| `concurrency-probe-sql.sh` | AC-7 | 4/0. S1 has exactly 1 winner; REPEATABLE-READ on all 19 pool connections | `concurrency-sql.out` |
| `enum attempts removed limits resend` | AC-2, AC-3, AC-4, AC-6, AC-8, AC-9, AC-10, AC-13, AC-14 | 34/0 | `enum-attempts-removed-limits-resend.out` |
| `session leak emaillog nonprod live` | AC-1, AC-5, AC-11, AC-18 (including the super-admin resend half), AC-19, AC-21 | 56/0, 0 NOT RUN | `session-leak-emaillog-nonprod-live.out` |
| `smtp` | AC-15 | 4/0 | `smtp.out` |
| `blackhole` | AC-15, NF-001 | 2/0. HTTP 200 in 0.097 s; `email_failed ms=10042` at WARN | `blackhole.out` |
| `expiry` | AC-8 | 1/0. `invalid` at 301.1 s | `expiry.out` |
| `nonprodblank` (restart, `EMAIL_NOTIFICATION=` empty) | AC-19 | 4/0 | `nonprodblank.out` |
| `nosecret` (restart, `OTP_HMAC_SECRET=` empty) | AC-12 | 6/0 | `nosecret.out` |
| `concurrency-probe.sh` + `tz` (restart, `Asia/Tokyo`) | AC-7, AC-8 time zone | 13/0 and 2/0 | `concurrency-http-tz-Tokyo.out`, `tz-Tokyo.out` |
| `postlogin`, `shortsecret` | SEC-005, SEC-008 | **Not re-run.** Outside the brief; `postlogin` needs the type-99 fixture. The Implementer's runs stand | `ac-probe.main-run.out`, `ac-probe.shortsecret.out` |

### 5.3 Discarded attempt: `nosecret` with the variable unset

The first `nosecret` attempt failed 3 of 7 checks:

```
  the unit's switch is on; request-code -> sent ; otpEnabled flags ['true'] ; challenge rows 1
  => FAIL [AC-12] request-code refused (domainNotEnabled), no challenge, no mail
  => FAIL [AC-12] the option is hidden: otpEnabled false for every unit
  => FAIL [AC-12] the startup log carries the auth.otp.keys WARN (outcome=unconfigured) and not the secret value   (0 line(s), secret value in them: False)
```

**Root cause: the harness, not the product.** The gitignored local `marlo-dev.properties` now holds a non-empty
`otp.hmac.secret`. With `OTP_HMAC_SECRET` unset, the app correctly fell back to it, which is the documented
precedence: the environment variable wins only when set. The app really was configured.

The second attempt set `OTP_HMAC_SECRET=` empty, the method the password-switch AC-7 run used, and passed 6/0:
- request-code answered `domainNotEnabled`, with no challenge row and no mail;
- verify-code answered `unavailable`;
- the password login still worked;
- the startup log carried `auth.otp.keys outcome=unconfigured`.

The attempt 1 output is kept as `akili-test-nosecret.attempt1-env-unset-file-secret-used.out`. No probe script was edited.

### 5.4 Harness notes and differences from the recorded runs

- **Borrowed harness pieces:** Tester 2 used `fixtures.sh` and `tools/sink.py` from the archived
  `CHG-PWD-SWITCH-001`. In the copy only, it gave super admin 3815 a fixture address and test password, so the AC-18
  resend half could run. T21 had recorded that half as NOT RUN.
- **ac17 (a): 0 old-key rows before, 4 after.** The Implementer's copy predated T21; here Flyway had already applied
  T21 at startup, which deleted the 4 empty old-key `custom_parameters` rows that `aiccradb_actsave` holds (units 45,
  47, 48 and 49). That is a second live confirmation of AC-21, through Flyway itself. `ac21` then removed the rows
  `ac17` re-seeded, with 17/0.
- **SQL probe pool:** 19 REPEATABLE-READ connections, against 15 recorded. That is the pool size, not a behaviour change.
- **Everything else matches the recorded runs:** AC-5 compares 104 attributes, AC-7 gives `{ok:1, invalid:9}`, the
  AC-4 sequence is identical and AC-15 times out at about 10 s.
- **Leftover:** the shared local `marlo-dev.log` holds this run's `auth.otp.code` lines. That is expected outside
  production, and the codes are dead.

## 6. E2E Tests

Not re-run. Cited:
- `probes/output/t17-walkthrough.out` (UI-006, every step-3 branch);
- `probes/output/t21-walkthrough.out` (external account, CGIAR account on Cognito with the code, `cgiar.org` excluded
  live, switch off);
- the archived `CHG-PWD-SWITCH-001` T06 walkthrough (password switch off).

*(Corrected 2026-10-18, validation V-8: this said "None of the UI files has changed since those walkthroughs", which was wrong.)* `login.js` and `loginForm.ftl` changed after the T17 and T21 walkthroughs, in commit `33fa702aad` (2026-10-17, `CHG-PWD-SWITCH-001` T06: the code-only and use-your-email views). The archived password-switch walkthrough `docs/specs/archive/2026-10-18-changes--password-login-switch/probes/output/t06-walkthrough.md` re-walked them. T22 (2026-10-18) then changed `login.js` in `otpShowError` only (the password-off "unavailable" text); that is covered by the recorded node check of `execution.md` T22, not by a browser walkthrough. The server-side flags those views read (`crpByEmail`
`otpEnabled`) were re-checked live in this run: all `false` with the switch off (`live`), and the AC-13 cases in `enum`.

## 7. Coverage & Traceability

Abbreviations:
- **Test classes:** Req = `OtpRequestCodeActionTest`, Ver = `OtpVerifyCodeActionTest`, Elig = `OtpEligibilityTest`,
  Dom = `OtpDomainPolicyTest`, Mail = `OtpMailSenderTest`, CBU = `CrpByUserEmailActionTest`,
  RL = `OtpRateLimiterTest`, CM = `OtpChallengeManagerImplTest`, RLM = `OtpRateLimitManagerImplTest`.
- **Live stages:** "Live" means a stage listed in §5.2.

| AC | Requirement | Clause | Type | Test or stage | Result | Gap or notes |
|---|---|---|---|---|---|---|
| AC-1 | FN-001, FN-019 | Existing login tests pass unchanged | Unit | `LoginActionFinishLoginTest`, `LoginActionLogoutTest`, `CognitoCallbackActionTest` | PASS | Only their constructor wiring changed, no assertion |
| AC-1 | FN-001 | Switch off: no code offered; password and Cognito unchanged | Unit + live | CBU#otpEnabledIsTrueOnlyWhenConfiguredSwitchOnAndDomainNotExcluded, CBU#everyPreExistingKeyAndValueIsUnchangedOnAndOff, `SignInMethodRuleTest`#ac1MissingPasswordSwitchReadsAsOnAndKeepsTodaysMethods; live `live` | PASS | Rendering cited from walkthroughs (§6) |
| AC-2 | FN-003 | Lookalike, suffix and sub-domain not excluded; padded upper case normalised | Unit + live | Dom (lookalike, left-label, sub-domain, padded, non-ASCII tests), Req#lookalikeDomainsAndASubdomain…; live `enum` | PASS | — |
| AC-3 | FN-004, FN-009, FN-017 | One challenge, one email, 6-digit code, every FN-017 element, no link | Unit + live | Req#anEligibleRequestIssuesOneRow…, `OtpCodesTest` leading-zero tests, Mail#theRenderedEmailStatesEveryRequiredElement; live `enum` | PASS | — |
| AC-4 | FN-005, SEC-006 | Unknown, inactive, non-member: same answer and page state, no email; resend and verify identical; no decoy authenticates | Unit + live | Req#everyIneligibleStateAnswersExactlyAsTheEligibleOne…, Req#aResendIsAnsweredLikeAnEligibleOne…, Ver#afterAnyRequestTheVerifySequenceIsTheSame…, Ver#aDecoyGuessed…; live `enum` | PASS | — |
| AC-5 | FN-011, SEC-005, SEC-007 | Session ID rotated; attributes equal a password login; no role-row writes | Unit + live | Ver#theSessionIdIsRotated…, Ver#theSessionCarriesTheLoadedEntity…, Ver#theOnlyWritesAreSaveLastLogin…; live `session` (104 attributes) | PASS | — |
| AC-6 | FN-012 | 3rd wrong code exhausts; correct code refused afterwards | Unit + live | Ver#wrongCodesCountAndTheThirdExhausts…, CM#theThirdWrongCodeExhaustsTheChallenge; live `attempts` | PASS | — |
| AC-7 | FN-013 | 10 concurrent correct verifications: exactly 1 succeeds | Live | `concurrency-probe.sh` `{ok:1, invalid:9}` (twice, once on Tokyo), `concurrency-probe-sql.sh` S1 | PASS | Unit support: CM#consumeSucceedsOnlyWhen…ExactlyOneRow |
| AC-8 | FN-010, FN-014 | 5 min 1 s old, or superseded: refused | Unit + live | Ver#aFiveMinuteOldChallengeIsInvalid…, Ver#aSupersededConsumedOrPurgedChallenge…; live `expiry` (301.1 s), `tz` | PASS | — |
| AC-9 | FN-015 | Removed after the request: refused, challenge consumed | Unit + live | Ver#anAccountThatLostEligibility…; live `removed` | PASS | — |
| AC-10 | FN-007, FN-008 | After a resend the old code is refused and the new one works; a resend under 30 s is rate-limited | Unit + live | CM#issueSupersedesTheEmailsOpenRows…, RL 29.9 s / 30 s, RLM, Req#anyLimitOrTheCooldown…; live `resend` | PASS | — |
| AC-11 | SEC-001, SEC-002, OPS-001 | Code only in the OPS-005 `message`; one `auth.otp.code` line outside production, none in production; one line per call | Unit + live | Req#noLogLineCarriesTheCode…, Ver#everyCallWritesExactlyOneVerifyLine…, Mail production / non-production log tests, `OtpPendingTest`; live `leak`, `emaillog` | PASS | — |
| AC-12 | SEC-008 | No secret: option hidden, request refused, app starts | Unit + live | `OtpKeysTest`, Req#anUnconfiguredSecret…, CBU#unconfiguredMakes…; live `nosecret` (empty variable) | PASS | Literal "variable absent" case not run: the local properties file holds a secret (§5.3, §9) |
| AC-13 | SEC-009, FN-003, UI-006 | CGIAR account offered and signed in with the code; `cgiar.org` excluded answers not-enabled | Unit + live | CBU#aCgiarOrgAddressIsOfferedTheCode…, Req#aCgiarOrgAddressIsMailed…, Ver#aCgiarAccountSignsIn…; live `enum` | PASS | Step-3 screen cited from the T21 walkthrough |
| AC-14 | SEC-010 | 6th request per email limited; per-IP limit trips; untrusted header ignored | Unit + live | RL per-email and per-IP tests, Req#theLimiterCountsEveryBucket…, `ClientIpResolverTest`; live `limits` | PASS | Multiple instances: accepted risk (§9) |
| AC-15 | FN-018, OPS-002 | Delivered with notifications off; SMTP down gives a neutral answer and `email_failed` | Unit + live | Mail#theMailerNeverReachesTheEmailLog…, Mail#aTransportException…, Req#aMailerThatFails…; live `smtp`, `blackhole` | PASS | — |
| AC-16 | UI-001–UI-006 | Browser walkthrough of every state | E2E (cited) | `t17-walkthrough.out`, `t21-walkthrough.out` | PASS (as recorded) | Screen-reader behaviour and the reCAPTCHA overlap NOT VERIFIED (§9) |
| AC-17 | DA-001, DA-002 | Migration applies twice; works without units 45/47 | Live + unit | `ac17-migration.sh` (3 shapes); new `OtpSpecificityConstantParityTest` | PASS | — |
| AC-18 | OPS-005 | One `email_logs` row per delivered or failed email; code only in `message`; none for an ineligible request; never resent | Unit + live | Mail row tests, `SendFailEmailActionTest`; live `emaillog`, including the super-admin resend half | PASS | — |
| AC-19 | FN-018 | Non-production: only the support mailbox, prefix and header; blank mailbox fails closed | Unit + live | Mail outside-production tests; live `nonprod`, `nonprodblank` | PASS | — |
| AC-20 | FN-017, UI-005 | AICCRA shell around the HTML part; text part keeps the code; no shell gives the bare body | Unit + live | Mail shell tests, Req#theEmailIsRenderedWithTheCustomFile…; live `nonprod` at the sink | PASS | — |
| AC-21 | DA-002, FN-001, FN-002 | T21 migration applies twice; keys seeded off; allow-list gone; changes apply on the next request | Live + unit | `ac21-migration.sh` 17/0, Flyway at startup; new `OtpSpecificityConstantParityTest`; Req / CM / Elig next-request tests; live `live` | PASS | — |

Negative constraints are covered explicitly:
- **No enumeration oracle:** AC-4, SEC-006.
- **No code in logs or columns:** AC-11, AC-18.
- **No bypass of the exclusion:** AC-13.
- **No reuse after consume or exhaust:** AC-6, AC-7.
- **No fallback key:** AC-12.
- **No delivery to the submitted address outside production:** AC-19.

The `CHG-PWD-SWITCH-001` amendments (FN-001/003/005/006/015, SEC-009, UI-006) are covered by:
- the `ac4`–`ac8` tests in Req and Ver, and Ver#aCodeIssuedUnderTheOverride…;
- Elig `ac4a`, `ac5a`, `ac6` and `ac7`;
- all of `SignInMethodRuleTest` and CBU `ac11…`;
- the `LoginActionCgiarGuardTest` and `ValidateUserActionGuardTest` gate tests.

## 8. Remediation

No product failure to fix.

Two follow-ups, neither of which blocks this phase:

1. **Rollback text is stale.** `tasks.md` §8 step 1 still says to empty the unit's `custom_parameters` value, which is
   the allow-list procedure. Since T21 the rollback is to set `system_otp_login_active` off. This is documentation
   drift for `/akili-validate`, not a test failure. *(Fixed 2026-10-18: `tasks.md` §8 step 1 and design §14 now describe turning the switch off.)*
2. **Unset secret not runnable locally.** To run the literal unset-variable AC-12 case on this machine, either blank
   `otp.hmac.secret` in the local `marlo-dev.properties` for that run, or accept the empty-variable run as equivalent.
   The fallback the app took is the documented precedence, so the behaviour is correct either way.

## 9. Accepted Gaps

| Gap | Why it is not automated | Status |
|---|---|---|
| SEC-003 constant-time comparison | A unit test cannot measure timing. `OtpCodes.matches` uses `MessageDigest.isEqual`, and the no-throw-on-malformed-input part is unit-tested | Accepted (harness limit) |
| AC-12 with the variable absent | The local properties file holds a secret; the empty-variable run passed (§5.3) | Accepted; see Remediation 2 |
| AC-14 across several instances | Cannot be reproduced locally. Shared state holds structurally, because the store is the one database | Accepted in requirements §6 |
| AC-16 screen-reader behaviour, reCAPTCHA overlap | An attribute check proves presence, not screen-reader behaviour | Accepted in requirements §6; NOT VERIFIED in `probes/README.md` |
| Deliverability (spam folder, partner filters) | Needs a real mailbox at the pilot domain | Accepted until the pilot (requirements §6) |
| Frontend unit tests for `login.js` | No JS test runner; choosing one is a TRD decision | Open TRD question |
| `postlogin`, `shortsecret` not re-run by a non-author | Outside this run's brief | The Implementer's recorded runs stand |
