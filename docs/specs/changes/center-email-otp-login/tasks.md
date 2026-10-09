# Center Email OTP Login — Tasks

**Spec ID:** `CHG-OTP-LOGIN-001`
**Status:** Ready (2026-10-09)
**Owner:** IBD Team — Alliance of Bioversity International and CIAT (Kenji Tanaka)
**Last Updated:** 2026-10-09
**Implements design:** `docs/specs/changes/center-email-otp-login/design.md`. Judgment Day: `APPROVED` (`judgment.md`)
**Branching:** `A2-2631-Implement-Center-Email-OTP-Authentication`, branched from `staging`
**Target merge:** `staging`, then promoted to `main` through the release process
**Approval Mode:** `gated`

---

## 1. Execution Context

| Item | Value |
|---|---|
| Java / build | Java 17. Compile gate: the clean recompile from root `CLAUDE.md` *Agent-Lean Verification Commands* (`marlo-verify` skill) |
| Checkstyle | Run the checkstyle 8.18 jar directly against the changed files and diff the result against HEAD (`marlo-verify`). `mvn checkstyle:check` cannot run in this checkout |
| Unit tests | `mvn -q -o -pl marlo-web -am test`. Keep `-am` (memory: test suite needs `-am`) |
| Local stack | `docs/infrastructure.md` §6. `scripts/run-marlo-java17.sh` is **never** run while a delegated agent is active (root `CLAUDE.md` *Concurrency*) |
| Database | Probes and migration checks run on a **throwaway copy** of local `aiccradb1`, never on the original |
| Environment | Export `OTP_HMAC_SECRET` (base64, ≥ 32 decoded bytes) for local runs. Unset it to test SEC-008 |

## 2. Pre-flight Checklist

- [ ] `requirements.md` and `design.md` approved (Phase 1 and Phase 2 gates; Judgment Day `APPROVED`).
- [ ] On branch `A2-2631-Implement-Center-Email-OTP-Authentication`, rebased on the latest `staging`.
- [ ] No other AKILI session or Maven build running in this checkout.

---

## 3. Task List

Field conventions:
- `Review` uses the `/akili-execute` Step 2.3 bands.
- `Falsifier` is the input that must turn the gate red.
- `Red run` is the failing observation required before the fix counts.
- `Disqualifier` is what makes a green reading worthless.
- `Consumers` lists the readers swept for that task.

**No task is `skip-eligible`.** Every task sits on an authentication path.

### CHG-OTP-LOGIN-001-T01 — Migration: tables and catalog seed

- **Status:** done [x] · **Size:** S · **Depends on:** none · **Module:** marlo-web (resources)
- **Requirements:** DA-001, DA-002, DA-003 (storage); FN-001 (seed off)
- **Design:** §3, DD-6, DD-8; Premise Ledger P-11, P-23
- **Files:** `database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__AddEmailOtpLogin.sql` (new). Take the timestamp from `date +%Y%m%d_%H%M`.
- **Scope:**
  - Create the `otp_challenges`, `otp_rate_limits` and `otp_cooldowns` tables, with their columns and indices as specified in design §3.
  - Seed the catalog `parameters` row with `INSERT … SELECT` from `global_unit_types` where `id IN (1,2,3,4)` and the row does not exist yet.
  - Do not insert any `custom_parameters` row.
- **First step (settles P-23):** build a clean database from migrations only. Record which of the types 1–4 exist.
- **Tests:** apply the migration **twice** on a throwaway copy of `aiccradb1`, then once on the clean database.
- **Verification:**
  - `SHOW CREATE TABLE` for all three tables.
  - `SELECT global_unit_type_id FROM parameters WHERE \`key\`='crp_otp_allowed_email_domains'` returns exactly the existing types.
  - `custom_parameters` count for the key = 0.
  - Second run: no error and no duplicate rows.
- **Falsifier:** a fixed-id insert for type 4 against the clean database must fail its FK. Prove the `INSERT … SELECT` form does not fail, on the same database.
- **Red run:** n/a. A migration is verified by applying it, not by a failing test.
- **Disqualifier:** a run that never reaches the clean database, or that runs against the original `aiccradb1`, is not evidence.
  - *Amended 2026-10-09 at execute time (user decision, `execution.md` T01):* the v1 + migrations history cannot be replayed from empty (three legacy breaks, the last `V1_0_0_20160819_0951__Project_types.sql` `Duplicate column name 'is_regional'`). The clean-database role is filled by a synthetic database holding the real `global_unit_types` and `parameters` DDL with types 1–3 only. A run against the original `aiccradb1` is still not evidence.
- **Consumers:** none. These are new tables. The new key has no reader until T03.
- **P-23 outcome (2026-10-09):** not measurable on a migration-only database, because none can be built. The `aiccradb1` copy holds types 1–5, and the migration seeds 1–4. From source, `V2_5_0_20180320_0915` inserts id 4 unconditionally, which contradicts the P-23 concern. The `INSERT … SELECT` seed is safe either way: it produced no FK failure on the synthetic database without type 4.
- **Review:** `full` — a migration runs on every environment and database (memory: migrations must be safe).
- **Done when:**
  - Every `marlo-migration` checklist item is ticked.
  - The P-23 outcome is recorded here.
  - The filename timestamp comes from the real clock.
- **Skills:** `marlo-migration`, `marlo-verify`

### CHG-OTP-LOGIN-001-T02 — Constants, configuration and `OtpKeys`

- **Status:** pending · **Size:** S · **Depends on:** none · **Modules:** marlo-utils, marlo-data, marlo-web
- **Requirements:** SEC-008; DA-002 (constants in both files); NF-002
- **Design:** §2, §5.3 `OtpKeys`, DD-10; Premise Ledger P-18
- **Files:**
  - `APConfig.java`: add `otp.hmac.secret`, `otp.trusted.proxies` and `otp.smtp.timeout.ms`, each as `${key:}`.
  - Both `APConstants.java`: add `OTP_ALLOWED_EMAIL_DOMAINS = "crp_otp_allowed_email_domains"`.
  - `marlo-web` `APConstants.java` only: add `OTP_PENDING_CHALLENGE`.
  - `security/otp/OtpKeys.java` (new).
  - `OtpKeysTest.java` (new).
- **First step (settles P-18):** start the app locally with `OTP_HMAC_SECRET` exported. Log the getter's **length only** and confirm it is non-zero. Then restart without the variable and confirm the length is 0. If the variable is not bound, stop and use the Pivot Protocol.
- **Tests (OtpKeysTest):**
  - empty secret → unconfigured;
  - non-base64 → unconfigured;
  - 31 decoded bytes → unconfigured;
  - 32 decoded bytes → configured;
  - the three derived keys are pairwise different;
  - the secret value never appears in the log line (capture the appender).
- **Verification:** `mvn -q -o -pl marlo-web -am test -Dtest=OtpKeysTest`, plus the clean compile.
- **Falsifier:** change the length floor to 16 bytes. The 31-byte case must go red.
- **Red run:** write the tests before `OtpKeys`. They must fail on the missing class or on the assertion, not on a setup error.
- **Disqualifier:** a P-18 check made with the secret set in `marlo-dev.properties` instead of the environment proves nothing about environment delivery.
- **Consumers:** `grep -rn "APConstants.OTP_" marlo-*/src` (empty before this task). The two `APConstants` values must be equal.
- **Review:** `checklist`
- **Done when:**
  - P-18 is settled, with its outcome recorded.
  - The tests are green.
  - The two constants are equal.
  - Every new file carries the GPL header.
- **Skills:** `marlo-verify`, `tdd`

### CHG-OTP-LOGIN-001-T03 — `OtpChallenge` persistence and manager

- **Status:** pending · **Size:** M · **Depends on:** T01 · **Module:** marlo-data
- **Requirements:** FN-007, FN-010, FN-012, FN-013, FN-014 (storage side); SEC-001, SEC-002; OPS-003; FN-002 (uncached read)
- **Design:** §3, §5.1 steps 7–8, §5.2 steps 4, 6–7, DD-3, DD-6, DD-12; Premise Ledger P-10, P-15, P-16
- **Files (new):**
  - `OtpChallenge.java`
  - `OtpChallenges.hbm.xml`, registered in `hibernate.cfg.xml`
  - `OtpChallengeDAO`, `OtpChallengeMySQLDAO`
  - `OtpChallengeManager`, `OtpChallengeManagerImpl`
- **Scope:**
  - `issue(...)` supersedes and then inserts, in one transaction.
  - `findByNonce`.
  - `registerMismatch(nonce)` returns the post-update attempts, or a re-read classification when 0 rows were affected.
  - `consume(nonce)`: the conditional update requires `consumed_at` null, `attempts < 3` and `expires_at > now`.
  - `purgeExpired(olderThan)` runs in its own transaction.
  - `allowListFor(key, globalUnitIds)` is a native, uncached select that returns the custom value, or else the default.
  - Every write method is `@Transactional` (memory: MARLO writes need a transactional manager).
- **Tests:**
  - Hand-written fake DAO for the manager's branching (P-17).
  - The real SQL is proven by the T15 probe and by a scripted run of each statement on the throwaway copy.
- **Verification:**
  - Clean compile.
  - Run each native statement once on the throwaway copy, and record the affected-row counts: consume on an exhausted row → 0; consume on an open row → 1; a second consume → 0.
- **Falsifier:** drop `AND attempts < 3` from the consume condition. The exhausted-row consume must then return 1, which shows the check can see JS-1.
- **Red run:** run the scripted SQL against a deliberately wrong statement first, and observe that it returns the wrong count.
- **Disqualifier:** counts read through a session that never committed, or from a different connection than the update, are not evidence.
- **Consumers:** `hibernate.cfg.xml` mapping list. `grep -rn "OtpChallenge" marlo-*/src` (new).
- **Review:** `full` — concurrency correctness (DD-3).
- **Done when:**
  - The statement counts are recorded.
  - No method writes without `@Transactional`.
  - The allow-list read bypasses Hibernate entirely: native SQL with no `setCacheable` (P-10).
- **Skills:** `marlo-verify`, `error-handling-patterns`

### CHG-OTP-LOGIN-001-T04 — Rate limiting and cooldown persistence

- **Status:** pending · **Size:** S · **Depends on:** T01, T02 · **Module:** marlo-data, plus the `OtpRateLimiter` facade in marlo-web
- **Requirements:** SEC-010, FN-008, DA-003
- **Design:** §3 (`otp_rate_limits`, `otp_cooldowns`), §5.1 step 1, §5.3 `OtpRateLimiter`, DD-8
- **Files (new):**
  - `OtpRateLimitDAO`, `OtpRateLimitMySQLDAO`
  - `OtpRateLimitManager`, `OtpRateLimitManagerImpl`
  - `security/otp/OtpRateLimiter.java`
  - `OtpRateLimiterTest.java`
- **Scope:**
  - **Window bucket:** upsert `hits + 1`, then read it back in the same transaction.
  - **Cooldown:** a conditional upsert that sets `last_request_at = :now` only when the stored value is at least 30 s older; then read it back and compare with `:now`.
  - Store failure → limited, i.e. fail closed.
  - Purge stale rows opportunistically.
  - Buckets are HMAC'd with `k_bucket`, so no raw email or IP is stored.
- **Tests (OtpRateLimiterTest, fake manager):**
  - limits 5, 20, 10 and 50 trip on the 6th, 21st, 11th and 51st call;
  - cooldown at t+29.9 s → limited, at t+30.0 s → accepted, across a minute boundary as well;
  - store exception → limited.
- **Verification:**
  - Unit tests.
  - The scripted cooldown SQL on the throwaway copy, with two `:now` values 1 s apart that straddle a minute boundary: second request rejected.
- **Falsifier:** replace the cooldown with a fixed 30-s window. The straddle case must go red (RJ-1).
- **Red run:** the straddle test, written first, fails on the assertion.
- **Disqualifier:** a test that uses one timestamp for both requests cannot see the edge.
- **Consumers:** none (new).
- **Review:** `full`
- **Done when:** the tests are green, and the straddle SQL result is recorded.
- **Skills:** `marlo-verify`, `tdd`

### CHG-OTP-LOGIN-001-T05 — Pure OTP helpers

- **Status:** pending · **Size:** M · **Depends on:** T02 · **Module:** marlo-web
- **Requirements:** FN-003, FN-009, SEC-001, SEC-003, SEC-009 (`cgiar.org` dropped), SEC-011, OPS-001 (line shape), SEC-004 (`OtpPending` content)
- **Design:** §5.3 (`OtpCodes`, `OtpDomainPolicy`, `ClientIpResolver`, `OtpPending`, `OtpLog`), DD-9
- **Files (new):**
  - `OtpCodes`, `OtpDomainPolicy`, `ClientIpResolver`, `OtpPending`, `OtpLog`
  - tests for each
- **Tests:**
  - **FN-003 scenario.** These are rejected: `x@attackertrusted.org`, `x@trusted.org.evil.com`, `x@sub.trusted.org`, `a@`, `@trusted.org`, two `@`. `  X@TRUSTED.ORG ` becomes `x@trusted.org`.
  - **Allow-list parsing.** `" @Trusted.ORG , trusted.org, x@y.org, cgiar.org"` parses to `[trusted.org]`.
  - **Code format.** A generator stub returning 4219 yields `"004219"`. 10⁵ codes are all 6 digits.
  - **MAC compare.** A malformed stored MAC returns false without throwing.
  - **Mask.** `ana@partner.org` → `a***@partner.org`.
  - **IP resolution.**
    - Untrusted peer with `X-Forwarded-For` → the remote address.
    - Trusted CIDR peer → the right-most untrusted hop.
    - Trusted peer with an empty or malformed header → the remote address.
    - IPv6 works.
  - **OtpLog.** A captured line contains no code, nonce, full email or session id. Newlines in the domain are sanitised.
- **Verification:** `mvn -q -o -pl marlo-web -am test -Dtest='Otp*Test,ClientIpResolverTest'`
- **Falsifier:** make the domain match use `endsWith`. The `attackertrusted.org` case must go red. Make the compare use `String.equals`; that is caught by review, because constant time cannot be tested.
- **Red run:** the tests are written first and fail on their assertions.
- **Disqualifier:** a test that builds its expected value from the same normaliser it is testing cannot fail (KZ-…-directory-abstraction-2).
- **Consumers:** none (new).
- **Review:** `checklist`
- **Done when:**
  - All tests are green.
  - `OtpPending` is `Serializable` and holds no code or MAC.
- **Skills:** `tdd`, `marlo-verify`

### CHG-OTP-LOGIN-001-T06 — `OtpEligibility`

- **Status:** pending · **Size:** S · **Depends on:** T03, T05 · **Module:** marlo-web
- **Requirements:** FN-004, FN-005 (predicate), FN-015, SEC-009, FN-001 (domain plus configured)
- **Design:** §5.1 step 6, DD-4, §6 (the `otpEnabled` variant); Premise Ledger P-12, P-13
- **Files:** `security/otp/OtpEligibility.java` and `OtpEligibilityTest.java` (both new)
- **Scope:**
  - Full predicate: allow-list match, user exists, user active, `existCrpUser`, not (CGIAR user **and** `CognitoAuthSpecificity.isActiveFor(...)`).
  - Disclosure-safe variant for `crpByEmail`: configured, domain match and SEC-009 only.
- **Tests:**
  - Each refusal reason alone returns false: unknown, inactive, non-member, CGIAR on a Cognito-active unit, domain not allowed, unconfigured.
  - A CGIAR user on a unit where Cognito is **inactive** is eligible (OQ-4 default).
  - The variant ignores active state and membership.
- **Verification:** `-Dtest=OtpEligibilityTest`
- **Falsifier:** remove the `existCrpUser` term. The non-member case must go red.
- **Red run:** the tests are written first.
- **Disqualifier:** fakes that return a hard-coded `true` regardless of the arguments they receive. Assert the userId and globalUnitId each fake received (KZ-…-auth-flow-2).
- **Consumers:** T10, T11, T13. They are the only callers.
- **Review:** `checklist`
- **Done when:** the tests are green and record the argument assertions.
- **Skills:** `tdd`, `marlo-verify`

### CHG-OTP-LOGIN-001-T07 — Shiro token and realm branch

- **Status:** pending · **Size:** S · **Depends on:** T02 · **Module:** marlo-data, plus a test in marlo-web
- **Requirements:** FN-011 (authentication), SEC-007, FN-019
- **Design:** §5.4; Premise Ledger P-3, P-19
- **Files:**
  - `security/OtpAuthenticationToken.java` (new): carries `userId`, rejects null.
  - `security/APCustomRealm.java`: `supports()`, plus a no-I/O branch above the cast.
  - `APCustomRealmDispatchTest.java`: extended.
- **Tests:**
  - The OTP token authenticates with principal `userId` and no I/O (the exploding fakes from the existing test).
  - The `UsernamePasswordToken` and Cognito dispatch tests stay unchanged and green.
  - An unsupported token is still refused.
- **Verification:** `-Dtest=APCustomRealmDispatchTest`, plus the full `CognitoCallbackActionTest` and `LoginAction*Test`.
- **Falsifier:** put the OTP branch **below** the cast. The OTP test must go red with a `ClassCastException`.
- **Red run:** the new test fails before the branch exists, on `supports()` returning false.
- **Disqualifier:** a run with `-Dtest` limited to the new test hides regressions in the sibling dispatch tests.
- **Consumers:** the 28 files from P-19. The test-side readers are `APCustomRealmDispatchTest`, `CognitoCallbackActionTest`, `CognitoLogHygieneTest`, `LoginActionCgiarGuardTest`, `LoginActionLogoutTest`, `ValidateUserActionGuardTest`, `ShiroRequestSessionCacheResetterTest`, `CognitoUnloggedStackReachabilityTest`, `CognitoAssertionTest`, `CognitoIdentityMappingTest` and `UserMySQLDAOEmailNormalizationTest`. All of them stay green.
- **Review:** `full` — a shared state change (P-19).
- **Done when:** every listed consumer is green and the diff touches no existing branch of the realm.
- **Skills:** `marlo-verify`

### CHG-OTP-LOGIN-001-T08 — Extract `ReturnUrls.sameOriginOrNull`

- **Status:** pending · **Size:** XS · **Depends on:** none · **Module:** marlo-web
- **Requirements:** FN-011 (landing parity, return URL); FN-019
- **Design:** DD-11; Premise Ledger P-22
- **Files:**
  - `security/ReturnUrls.java` (new)
  - `CognitoLoginAction.java`: delegate, no behavior change
  - `ReturnUrlsTest.java` (new)
- **Tests:**
  - `ReturnUrlsTest` covers the cases the private method handled: same origin kept, cross-origin dropped, null, malformed.
  - `CognitoLoginActionTest` is **unchanged** and green.
- **Verification:** `-Dtest='ReturnUrlsTest,CognitoLoginActionTest'`
- **Falsifier:** make `ReturnUrls` keep cross-origin URLs. Both suites must go red.
- **Red run:** `ReturnUrlsTest` is written before the class exists.
- **Disqualifier:** editing `CognitoLoginActionTest` to match makes its green meaningless. The diff must not touch it.
- **Consumers:** `grep -rn "sameOriginOrNull" marlo-web/src`. Only `CognitoLoginAction` today.
- **Review:** `checklist` (reversion challenge recorded at DD-11)
- **Done when:** both suites are green and `CognitoLoginActionTest` has no diff.
- **Skills:** `marlo-verify`

### CHG-OTP-LOGIN-001-T09 — `OtpMailSender` and email copy

- **Status:** pending · **Size:** M · **Depends on:** T02, T05 · **Module:** marlo-web
- **Requirements:** FN-017, FN-018, SEC-001, OPS-002, NF-001, UI-005 (email copy)
- **Design:** §5.3 `OtpMailSender`, DD-7; Premise Ledger P-7, P-8
- **Files:**
  - `utils/OtpMailSender.java` (new)
  - `global.properties`: the email subject and the HTML and text bodies, with apostrophes doubled
  - `OtpMailSenderTest.java` (new)
- **Scope:**
  - Build its own mail session from the `APConfig` SMTP getters, with connect, read and write timeouts.
  - Send to the single submitted recipient on an executor with 2 daemon threads, a queue of 100 and a caught abort policy.
  - Write one `delivery` log line per hand-off.
  - Never write to `email_log`. Never read the notification flag or the support redirect.
  - Render every FN-017 element from i18n keys, HTML-escaped. A render with an unresolved placeholder fails.
- **Tests (seam: a transport stub):**
  - success → `delivered`;
  - transport exception → `email_failed` WARN;
  - queue full → `email_failed`, and the caller is not affected;
  - the rendered body contains the code, "5", the single-use text, the do-not-share text, the ignore text and the support contact, and no `http` URL containing the code;
  - `EmailLogManager` is never invoked (a fake that records calls).
- **Verification:**
  - `-Dtest=OtpMailSenderTest`.
  - `.properties` review for apostrophes and braces (`marlo-verify` Gate 6).
- **Falsifier:** route the send through `SendMailS.send`. The `EmailLogManager`-never-called assertion must go red.
- **Red run:** the tests are written first.
- **Disqualifier:** a test stub that never runs on the executor thread cannot observe the queue-full case.
- **Consumers:** none (new). `global.properties` keys must have no collisions: `grep -n "^login.otp" global.properties` is empty before this task.
- **Review:** `full` — the SEC-001 leak surface.
- **Done when:** the tests are green and the properties are reviewed.
- **Skills:** `marlo-verify`, `error-handling-patterns`

### CHG-OTP-LOGIN-001-T10 — `OtpRequestCodeAction`

- **Status:** pending · **Size:** M · **Depends on:** T03, T04, T05, T06, T08, T09 · **Module:** marlo-web
- **Requirements:** FN-001, FN-004, FN-005 (and its scenario: BUT and both AND IT MUST clauses), FN-006, FN-007, FN-008, SEC-006 (request side), SEC-008, SEC-010, OPS-001, NF-001
- **Design:** §4 status table, §5.1 (all 11 steps), DD-5
- **Files:** `action/home/OtpRequestCodeAction.java` and `OtpRequestCodeActionTest.java` (both new)
- **Tests (fakes for the managers, the mailer and the eligibility predicate):**
  - **Order:** the rate-limit buckets are counted before any user lookup (the lookup fake records the call order).
  - **Neutrality:** for unknown, inactive, non-member and CGIAR-refused accounts, `response` equals the eligible case except for the masked email. A row is issued in every state. The mailer is called **only** when eligible.
  - **Rejections:**
    - domain not allowed → `domainNotEnabled`, with no issue;
    - unconfigured → `domainNotEnabled`;
    - `agree=false` → `termsRequired`;
    - cooldown or limit hit → `rateLimited`;
    - store failure → `unavailable`, with no session write.
  - **Session:** `OtpPending` replaces the previous value and holds the email, unit, nonce and return URL.
  - **Purge:** a purge failure still yields `sent`.
  - **Logging:** one `request` line per call.
- **Verification:** `-Dtest=OtpRequestCodeActionTest`
- **Falsifier:** skip `issue()` for ineligible accounts, as the round-1 draft did. The "row issued in every state" assertion must go red (JD-1/JD-2).
- **Red run:** the tests are written first.
- **Disqualifier:** comparing `response` maps built by the test itself instead of the ones the action produced.
- **Consumers:** none yet. The struts registration comes in T12.
- **Review:** `full` — the anti-enumeration contract.
- **Done when:** the tests are green and every FN-005 scenario clause maps to an assertion (§5 coverage table).
- **Skills:** `marlo-verify`, `tdd`, `error-handling-patterns`

### CHG-OTP-LOGIN-001-T11 — `OtpVerifyCodeAction`

- **Status:** pending · **Size:** L · **Depends on:** T03, T04, T05, T06, T07, T08 · **Module:** marlo-web
- **Requirements:** FN-011, FN-012, FN-013, FN-014, FN-015, FN-016, SEC-004, SEC-005, SEC-007, SEC-006 (verify side), OPS-001
- **Design:** §4 status table, §5.2 (all 11 steps), DD-2, DD-3, DD-5; Premise Ledger P-2, P-27
- **Files:** `action/home/OtpVerifyCodeAction.java` and `OtpVerifyCodeActionTest.java` (both new)
- **Tests (the CognitoCallbackActionTest harness pattern, P-17):**
  - No pending challenge → `mismatch`.
  - Bad shape → `mismatch`.
  - Wrong code → `mismatch`; then `exhausted` at 3.
  - `registerMismatch` reports 0 affected on a consumed row → `invalid` (RJ-3).
  - Exhausted challenge with the correct code → `exhausted`.
  - Expired or superseded → `invalid`.
  - Correct code with eligibility lost → `invalid`, and the challenge stays consumed.
  - Decoy challenge with the "correct" random code → `invalid` (a fake supplies it).
  - Success:
    - the session is rotated (the session id differs);
    - `setUser` holds a detached user;
    - `finishLogin` receives the **loaded** entity;
    - `agree_terms` is set only when `pending.agree`;
    - `redirect` = `<base>/<acronym>/crpDashboard.do` for type 1, and `centerDashboard.do` for type 2.
  - Gate-4 refusal inside `finishLogin` → `failed` with the `authError=failed` redirect, and **no NPE**.
  - A null user after consume → `failed`.
  - One `verify` line per call.
- **Verification:**
  - `-Dtest=OtpVerifyCodeActionTest`.
  - Re-run `CognitoCallbackActionTest`, `LoginActionFinishLoginTest` and `CognitoLogHygieneTest`.
- **Falsifier:** delete the `setUser(detached)` call. The gate-4 refusal test must go red with an NPE (P-27).
- **Red run:** the gate-4 test is written first and observed failing on the assertion. A red from harness setup does not count.
- **Disqualifier:** a fake `finishLogin` override that never runs the real tail cannot prove the NPE fix. Use the real `LoginAction.finishLogin`, with fake managers.
- **Consumers:** `LoginAction.finishLogin` callers (P-2). `grep -rn "finishLogin(" marlo-web/src` → `LoginAction:402`, `CognitoCallbackAction:537` and 3 test sites (`LoginActionFinishLoginTest:357`, `CognitoLogHygieneTest:618/638/686`). Their suites stay green.
- **Review:** `lenses` — security, concurrency, session parity. Effort `xhigh`.
- **Done when:** the tests are green, the listed consumers are green, and every FN-012 to FN-016 clause maps to an assertion.
- **Skills:** `marlo-verify`, `tdd`, `error-handling-patterns`

### CHG-OTP-LOGIN-001-T12 — Struts `homeOtp` package

- **Status:** pending · **Size:** S · **Depends on:** T10, T11 · **Module:** marlo-web
- **Requirements:** FN-001 (reachability), SEC-004 (only `response` serialized), NF-002; requirements §8 (declared stack)
- **Design:** §4, DD-1; Premise Ledger P-14, P-21, P-26, P-28
- **Files:**
  - `struts-home.xml`: the `homeOtp` package, `otpUnloggedStack`, the two actions, the global `input` result, `root=response`.
  - Both actions implement `ParameterNameAware`.
  - Stack coverage tests extended.
- **First steps (settle P-21 and P-26):**
  1. Read `ErrorStatusLogStackCoverageTest` and `CognitoUnloggedStackReachabilityTest`, and list what they pin in this task's Consumers.
  2. Prove that `extends="marlo-default,json-default"` loads: add a reachability test modelled on `CognitoUnloggedStackReachabilityTest`.
  3. If it fails, apply the DD-1 fallback.
- **Tests:**
  - Reachability through `otpUnloggedStack`.
  - JSON output contains only the `response` keys. For example, no `session`, `user` or `config` key appears in serialised output (render the result with a populated action).
  - A request parameter such as `user.email` or `crpSession` is refused by `acceptableParameterName`.
  - An invalid conversion yields the JSON `input` result.
- **Verification:** the new and extended tests, plus a manual `curl -s -X POST localhost:8080/otpRequestCode.do` on the local stack, showing the JSON shape.
- **Falsifier:** remove `root=response`. The "only `response` keys" test must go red.
- **Red run:** the reachability test fails before the package exists.
- **Disqualifier:** a reachability test that builds the action directly instead of through the configured package proves nothing about the XML.
- **Consumers:** `ErrorStatusLogStackCoverageTest` and `CognitoUnloggedStackReachabilityTest` (P-21, to be completed by the first step).
- **Review:** `full`
- **Done when:** P-21 and P-26 are settled and recorded, and the tests are green.
- **Skills:** `marlo-verify`

### CHG-OTP-LOGIN-001-T13 — `crpByEmail.do` `otpEnabled` flag

- **Status:** pending · **Size:** S · **Depends on:** T03, T06 · **Module:** marlo-web
- **Requirements:** UI-001, SEC-006 (no new disclosure), FN-002 (live)
- **Design:** §6 `crpByEmail.do` row; Premise Ledger P-20
- **Files:**
  - `CrpByUserEmailAction.java`: add the `otpEnabled` key per unit, using one uncached query per call, and only when configured.
  - `CrpByUserEmailActionTest.java`: extended.
- **Tests:**
  - The flag is true only when configured, the domain is allowed and SEC-009 passes.
  - The flag is identical for an active and an inactive user, and for a member and a non-member listed unit (JI-11).
  - Unconfigured → no allow-list query (a fake counts the calls) and every flag false.
  - Every existing key in the response is unchanged.
- **Verification:**
  - `-Dtest=CrpByUserEmailActionTest`.
  - The other P-20 test consumers stay green: `CognitoLoginActionTest`, `LoginActionCgiarGuardTest`, `ValidateUserActionGuardTest`, `ErrorStatusLogStackCoverageTest`.
- **Falsifier:** compute the flag with the full `OtpEligibility`. The active/inactive equality test must go red.
- **Red run:** the tests are written first.
- **Disqualifier:** asserting only the new key would hide a changed existing key. Assert the complete key set.
- **Consumers:** P-20, all 14 files, including `crp/js/admin/crpUsers.js`, which reads the response in admin. Confirm by reading it that the additive key does not affect it.
- **Review:** `checklist`
- **Done when:** the tests are green and the `crpUsers.js` read is recorded.
- **Skills:** `marlo-verify`

### CHG-OTP-LOGIN-001-T14 — Login page: FTL, JS and copy

- **Status:** pending · **Size:** M · **Depends on:** T12, T13 · **Module:** marlo-web (webapp)
- **Requirements:** UI-001, UI-002, UI-003, UI-004, UI-005, FN-019 (password and Cognito UI unchanged), FN-011 (redirect)
- **Design:** §6 (every row); Premise Ledger P-1, P-24
- **Files:**
  - `loginForm.ftl`: `#login-step-otp`.
  - `login.js`: the mode branch at `:471`, the code step, the countdown, `requestSeq`, button locks.
  - `global.properties`: page copy.
  - The FTL that references `login.js`: bump the `?YYYYMMDD` cache-buster.
- **Tests:** a browser walkthrough with claude-in-chrome on the local stack.
  - Every UI-002 state.
  - The DOM attributes: label, `inputmode`, `autocomplete`, `maxlength`, `aria-live`, `role="alert"`.
  - Focus on open.
  - Double click → one request (network log).
  - Password-only and Cognito units look exactly as before.
- **Verification:** a recorded GIF plus network log of the walkthrough, saved under `probes/`.
- **Falsifier:** remove the `requestSeq` check. A "use a different email" during an in-flight request then shows the stale response, which the walkthrough must catch.
- **Red run:** n/a. UI behavior is verified in the walkthrough; JS unit tests do not exist in MARLO.
- **Disqualifier:** a walkthrough started before the page JS has initialised gives false results (memory: browser save tests wait for JS init). An attribute check proves presence only; screen-reader behavior is an accepted gap (requirements §6).
- **Consumers:** `login.js` readers of the mode and flags: `loginForm.ftl` and `crpUsers.js` (P-20). The password, Cognito and terms flows are re-walked.
- **Review:** `full`
- **Done when:** the walkthrough artifacts are recorded, the cache-buster is bumped, and the properties are reviewed.
- **Skills:** `marlo-verify`, `frontend-design`, `claude-in-chrome`

### CHG-OTP-LOGIN-001-T15 — End-to-end verification: probes, leaks and acceptance walkthrough

- **Status:** pending · **Size:** M · **Depends on:** T01–T14 · **Module:** spec folder (`probes/`)
- **Requirements:** AC-1 to AC-17 (requirements §7); the defect-class gates of requirements §6 that have no automated check
- **Design:** §12 probe, §13, §14 steps 4–5
- **Files:** `probes/concurrency-probe.sh` (or `.sql`) and `probes/README.md` with the recorded output (both new).
- **Scope:**
  1. **Concurrency probe** on the throwaway copy, covering the four §12 cases (AC-7 and the FN-012 mixed burst).
  2. **Leak check** after a full flow:
     - `SELECT` over `otp_challenges` and `email_log` for the code and the full email;
     - a grep of the run's log for the code, the nonce and the email (AC-11).
  3. **Session parity:** compare session attributes between one password login and one code login for the same user and Global Unit (AC-5).
  4. **Enumeration:** compare responses across the four account states (AC-4).
  5. **SEC-008:** start with no secret (AC-12).
  6. **FN-018 / OPS-002:** notifications off, then SMTP down (AC-15).
  7. **AC-1 regression:** password and Cognito login.
- **Verification:** every AC is recorded `PASS`, `FAIL` or `NOT VERIFIED`, with evidence, in `probes/README.md`.
- **Falsifier:** run the probe against a build where consume lacks `attempts < 3`. The mixed-burst case must report a consume (JS-1).
- **Red run:** the falsifier run above, recorded.
- **Disqualifier:**
  - A probe that runs its 10 requests sequentially is not a concurrency test.
  - A leak grep over a log level that suppresses the relevant lines is not evidence.
  - Record the log level used.
- **Consumers:** n/a (verification only).
- **Review:** `full`
- **Done when:** every AC has a recorded verdict, and any `FAIL` is either fixed through the owning task or escalated.
- **Skills:** `marlo-verify`, `claude-in-chrome`, `systematic-debugging` (on any failure)

### CHG-OTP-LOGIN-001-T16 — Runbook and rollout checklist (T-rollout)

- **Status:** pending · **Size:** S · **Depends on:** T15 · **Module:** spec folder
- **Requirements:** OPS-004; the open premises' owner (Premise Ledger P-4, P-6, P-25); OQ-1, OQ-2, OQ-5
- **Design:** §11, §14, §16
- **Files:** `runbook.md` (new). Pending shared-file items are recorded only: `docs/trd/trd.md` §8.1 and §9.5, and `docs/infrastructure.md` §6 (`OTP_HMAC_SECRET`, `OTP_TRUSTED_PROXIES`). On this spec branch they are pending items for `/akili-archive` on `staging`, per root `CLAUDE.md` *Default Branch & Shared-File Write Discipline*.
- **Scope of the runbook:**
  - enable and disable per Global Unit, through the admin UI only (DD-12);
  - secret generation and rotation;
  - trusted-proxy configuration;
  - code-not-received triage (design §11);
  - the support note on ineligible accounts.
- **Scope of the rollout checklist:**
  - settle P-4 (affinity), P-6 (proxy) and P-25 (RDS version) with infra;
  - the Guest-exposure audit `getPermissions` for pilot users (OQ-2);
  - confirm OQ-5;
  - check by hand that Production applied the migration.
- **Verification:** walk the runbook once on the local stack: enable, sign in, disable, confirm the option is gone on the next request.
- **Falsifier:** disable through raw SQL instead of the admin UI. It must still apply on the next request, which proves DD-12; if it does not, the runbook claim is wrong.
- **Red run:** n/a.
- **Disqualifier:** a walkthrough that never disables cannot verify the rollback claim.
- **Consumers:** n/a.
- **Review:** `checklist`
- **Done when:** the runbook is walked, and the open premises have named infra answers or remain explicitly blocking go-live, not merge.
- **Skills:** `cognitive-doc-design`

---

## 4. Dependency Graph

```text
T01 migration ─┬─ T03 challenge persistence ─┬─ T06 eligibility ─┬─ T10 request action ─┐
               └─ T04 rate limit ────────────┤                   ├─ T11 verify action ──┤
T02 config/keys ┬─ T04                       │                   └─ T13 crpByEmail ─────┤
                ├─ T05 helpers ──────────────┼─ T06                                     │
                ├─ T07 realm ────────────────┼─ T11                                     │
                └─ T09 mailer ───────────────┴─ T10                                     │
T08 ReturnUrls ──── T10, T11                                                            │
T10 + T11 ── T12 struts ──┐                                                             │
T12 + T13 ── T14 UI ──────┴── T15 end-to-end ── T16 runbook / rollout                   ┘
```

There are no cycles. **Parallel-safe groups** (disjoint files): {T01, T02, T08}; then {T03, T04, T05, T07, T09}; then {T06}; then {T10, T11, T13}. Execution stays sequential under one Leader; run in parallel only through separate worktrees.

---

## 5. Coverage — scenario and clause level

| Requirement clause | Owning task(s) |
|---|---|
| FN-001 scenario "Feature off": option hidden · direct call refused, no row or email · BUT password/Cognito step unchanged | T13 + T14 (hidden) · T10 (`domainNotEnabled`, no issue) · T14 + T15 AC-1 |
| FN-002 scenario "Live change", no restart | T03 (uncached read) · T16 (raw-SQL falsifier) |
| FN-003 scenario: three lookalikes rejected · AND IT MUST accept trimmed upper case | T05 |
| FN-004 eligible: one row and one email · masked destination | T10 · T15 AC-3 |
| FN-005 scenario: same page state · BUT no email, no authenticating record · AND IT MUST answer resend and verify identically · AND IT MUST NOT log a client-visible cause | T10 (neutrality, row in every state) · T11 (decoy → `invalid` / same statuses) · T05 + T10 (`OtpLog`; `neutral` is server-side only) · T15 AC-4 |
| FN-006 explicit domain message | T10 |
| FN-007 supersede atomic | T03 · T15 probe |
| FN-008 30-s spacing | T04 (straddle) · T10 |
| FN-009 code format | T05 |
| FN-010 5-min expiry enforced at verify | T03 (consume condition) · T11 · T15 AC-8 |
| FN-011 scenario: same landing · same session attributes · consumed · BUT roles and memberships unchanged | T11 · T15 AC-5 · T07 (SEC-007) |
| FN-012 scenario: 3rd wrong → exhausted · AND IT MUST refuse the later correct code | T03 · T11 · T15 probe (mixed burst) |
| FN-013 scenario: 10 concurrent → exactly one | T03 · T15 probe |
| FN-014 expired, superseded, consumed or exhausted refused even with the row present | T11 · T15 AC-8 |
| FN-015 eligibility re-check after consume | T06 · T11 · T15 AC-9 |
| FN-016 no pending → mismatch; after neutral request → same as eligible | T11 |
| FN-017 email content, no code link | T09 |
| FN-018 delivered with notifications off; recipient per OQ-5 | T09 · T15 AC-15 · T16 (OQ-5) |
| FN-019 password and Cognito unchanged | T07 · T08 · T14 · T15 AC-1 |
| SEC-001 scenario "Code never persisted" | T03 · T09 · T15 AC-11 |
| SEC-002 no clear email in the challenge store | T03 · T15 AC-11 |
| SEC-003 constant time, no throw | T05 |
| SEC-004 bound to session, email and unit; nothing client-side accepted as a session | T05 (`OtpPending`) · T11 · T12 (only `response`) |
| SEC-005 session rotation | T11 · T15 AC-5 |
| SEC-006 no new oracle | T10 · T11 · T13 · T15 AC-4 |
| SEC-007 no role or membership writes; identical authorization | T07 · T11 |
| SEC-008 scenario "Missing secret fails closed" · BUT app starts and other logins work | T02 · T10 · T13 · T15 AC-12 |
| SEC-009 Cognito guard · `cgiar.org` ignored | T05 · T06 · T15 AC-13 |
| SEC-010 limits shared, counted before lookup, state-independent | T04 · T10 · T11 · T15 AC-14 |
| SEC-011 untrusted headers ignored | T05 |
| OPS-001 one request/verify line per call, one delivery line per email | T05 · T09 · T10 · T11 · T15 AC-11 |
| OPS-002 delivery failure logged, response neutral | T09 · T15 AC-15 |
| OPS-003 purge, failure non-blocking | T03 · T10 |
| OPS-004 runbook | T16 |
| DA-001 migration safe and idempotent | T01 |
| DA-002 seed off, existing types 1–4, constants in both files | T01 · T02 |
| DA-003 shared rate-limit store | T01 · T04 |
| UI-001 option only when enabled | T13 · T14 |
| UI-002 every state | T14 |
| UI-003 field attributes, focus, aria | T14 |
| UI-004 no double submit | T14 |
| UI-005 i18n | T09 · T14 |
| NF-001 bounded latency | T09 (async, timeouts) · T10 |
| NF-002 compile, Checkstyle, GPL header | every Java task, via `marlo-verify` |

Every clause has an owner. None is cleared by citing another requirement.

---

## 6. Testing Plan

| Layer | Content |
|---|---|
| Unit (JUnit 4, fakes) | T02, T04, T05, T06, T07, T08, T09, T10, T11, T12, T13 |
| SQL on a throwaway copy | T01 (applied twice, plus a clean database), T03 statement counts, T04 straddle |
| Concurrency | T15 probe (4 cases) |
| Regression | Existing `Cognito*Test`, `LoginAction*Test`, `CrpByUserEmailActionTest`, `ValidateUserActionGuardTest`, stack coverage tests; T15 AC-1 browser pass |
| Manual / browser | T14 walkthrough; T15 AC walkthrough |
| Accessibility | T14 attribute and focus checks. Screen-reader behavior is an accepted gap |

## 7. Operational Steps

| Step | When |
|---|---|
| Flyway applies the migration on startup; confirm in `flyway_schema_history` | Every environment, before enabling anything |
| Set `OTP_HMAC_SECRET` and `OTP_TRUSTED_PROXIES` | Per environment, before the pilot |
| Enable a pilot unit through `custom_parameters` (admin UI) | After T16's rollout checklist |
| `docs/trd/trd.md` §8.1/§9.5 and `docs/infrastructure.md` §6 updates | Pending items for `/akili-archive` on `staging` |

## 8. Rollback Plan

1. Empty the Global Unit's `custom_parameters` value. This applies on the next request (DD-12).
2. Unset `OTP_HMAC_SECRET`. The feature becomes inert everywhere.
3. Revert the merge on `staging` and redeploy. The tables are harmless.
4. Drop `otp_challenges`, `otp_rate_limits` and `otp_cooldowns`. Only in-flight codes are lost.

Password and Cognito logins are unaffected at every level.

## 9. Definition of Done

- [ ] T01–T16 done, each with its verification notes.
- [ ] Every AC in `requirements.md` §7 recorded `PASS` in `probes/README.md`. A `NOT VERIFIED` is allowed only for an accepted gap named in requirements §6.
- [ ] Compile and Checkstyle gates green (zero new violations against HEAD); unit suite green with `-am`.
- [ ] Every UNVERIFIED premise either settled, or listed as a go-live blocker in `runbook.md`.
- [ ] No `email_log` row and no log line contains a code; evidence recorded.
- [ ] Merged to `staging` through the PR strategy below; A2-2631 status checked per `marlo-commit`.
- [ ] Shared-file pending items handed to `/akili-archive`.

## 10. PR Strategy

The estimate is about 2,300 LOC, so the work ships as three chained PRs into `staging`. The feature stays inert until a secret is set and a unit is enabled, so each PR merges safely on its own.

| PR | Tasks | Review first | Out of scope |
|---|---|---|---|
| 1 — Data and security core | T01–T09 | T03 consume/mismatch SQL, T07 realm branch | Actions, UI |
| 2 — Actions and routing | T10–T13 | T11 session sequence, T12 JSON root and parameter filter | UI |
| 3 — UI and verification | T14–T16 | T15 probe output, T14 walkthrough | — |

Each PR description links its predecessor and successor, following the `cognitive-doc-design` review-empathy rules.
