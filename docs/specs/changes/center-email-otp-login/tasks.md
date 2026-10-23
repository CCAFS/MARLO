# Center Email OTP Login — Tasks

**Spec ID:** `CHG-OTP-LOGIN-001`
**Status:** Implemented (T01–T22), in validation (2026-10-18)
**Owner:** IBD Team — Alliance of Bioversity International and CIAT (Kenji Tanaka)
**Last Updated:** 2026-10-18
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
| Environment | Set `otp.hmac.secret` (base64, ≥ 32 decoded bytes) in `marlo-dev.properties`, or export `OTP_HMAC_SECRET`, which takes precedence. Blank it to test SEC-008 (amended 2026-10-16) |

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
  - `SELECT global_unit_type_id FROM parameters WHERE \`key\`='crp_otp_allowed_email_domains'` returns exactly the existing types. *(Superseded by T21, 2026-10-15: the T21 migration removes this key; the check is now `system_otp_login_active` and `system_otp_excluded_email_domains`, one row per existing type.)*
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

- **Status:** done [x] · **Size:** S · **Depends on:** none · **Modules:** marlo-utils, marlo-data, marlo-web
- **Requirements:** SEC-008; DA-002 (constants in both files); NF-002
- **Design:** §2, §5.3 `OtpKeys`, DD-10; Premise Ledger P-18
- **Files:**
  - `APConfig.java`: add `otp.hmac.secret`, `otp.trusted.proxies` and `otp.smtp.timeout.ms`, each as `${key:}`.
  - Both `APConstants.java`: add `OTP_ALLOWED_EMAIL_DOMAINS = "crp_otp_allowed_email_domains"`. *(Superseded by T21, 2026-10-15: removed; `OTP_LOGIN_ACTIVE` and `OTP_EXCLUDED_EMAIL_DOMAINS` replace it.)*
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

- **Status:** done [x] · **Size:** M · **Depends on:** T01 · **Module:** marlo-data
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
  - The allow-list read bypasses Hibernate entirely: native SQL with no `setCacheable` (P-10). *(Superseded by T21, 2026-10-15: the same uncached read now returns the switch and the exclusion.)*
- **Skills:** `marlo-verify`, `error-handling-patterns`

### CHG-OTP-LOGIN-001-T04 — Rate limiting and cooldown persistence

- **Status:** done [x] · **Size:** S · **Depends on:** T01, T02 · **Module:** marlo-data, plus the `OtpRateLimiter` facade in marlo-web
- **Requirements:** SEC-010, FN-008, DA-003
- **Design:** §3 (`otp_rate_limits`, `otp_cooldowns`), §5.1 step 1, §5.3 `OtpRateLimiter`, DD-8
- **Files (new):**
  - `OtpRateLimitDAO`, `OtpRateLimitMySQLDAO`
  - `OtpRateLimitManager`, `OtpRateLimitManagerImpl`
  - `security/otp/OtpRateLimiter.java`
  - `OtpRateLimiterTest.java`
- **Scope:**
  - **Window bucket:** upsert `hits + 1`, then read it back in the same transaction.
  - **Cooldown:** the locking read-decide-write of design §5.1 step 1, as amended 2026-10-13. An exclusive no-op upsert ensures the row, `SELECT … FOR UPDATE` reads it, and Java sets `last_request_at = :now` only when the stored value is at least 30 s older. *Amended 2026-10-13 (Pivot Record: T04). It was a conditional upsert with a read-back compared to `:now`.*
  - **Same-millisecond case (added 2026-10-13):** parallel requests that carry the same `:now` produce exactly one acceptance. Prove it with a parallel SQL probe of at least 20 sessions, and record the count of accepted, rejected and `unavailable` results.
  - Store failure → `unavailable` (design §4, §5.3), i.e. fail closed. *Aligned 2026-10-13; it read "limited".*
  - Purge stale rows opportunistically.
  - Buckets are HMAC'd with `k_bucket`, so no raw email or IP is stored.
- **Tests (OtpRateLimiterTest, fake manager):**
  - limits 5, 20, 10 and 50 trip on the 6th, 21st, 11th and 51st call;
  - cooldown at t+29.9 s → limited, at t+30.0 s → accepted, across a minute boundary as well;
  - store exception → `unavailable` (refused). *Aligned 2026-10-13.*
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

- **Status:** done [x] · **Size:** M · **Depends on:** T02 · **Module:** marlo-web
- **Requirements:** FN-003, FN-009, SEC-001, SEC-003, SEC-009 (`cgiar.org` dropped *(Superseded by T21, 2026-10-15: no `cgiar.org` rule)*), SEC-011, OPS-001 (line shape), SEC-004 (`OtpPending` content)
- **Design:** §5.3 (`OtpCodes`, `OtpDomainPolicy`, `ClientIpResolver`, `OtpPending`, `OtpLog`), DD-9
- **Files (new):**
  - `OtpCodes`, `OtpDomainPolicy`, `ClientIpResolver`, `OtpPending`, `OtpLog`
  - tests for each
- **Tests:**
  - **FN-003 scenario.** These are rejected: `x@attackertrusted.org`, `x@trusted.org.evil.com`, `x@sub.trusted.org`, `a@`, `@trusted.org`, two `@`. `  X@TRUSTED.ORG ` becomes `x@trusted.org`.
  - **Allow-list parsing.** `" @Trusted.ORG , trusted.org, x@y.org, cgiar.org"` parses to `[trusted.org]`. *(Superseded by T21, 2026-10-15: `parseExclusionList` keeps `cgiar.org`.)*
  - **Code format.** A generator stub returning 4219 yields `"004219"`. 10⁵ codes are all 6 digits.
  - **MAC compare.** A malformed stored MAC returns false without throwing.
  - **Mask.** `ana@partner.org` → `a***@partner.org`.
  - **IP resolution.**
    - Untrusted peer with `X-Forwarded-For` → the remote address.
    - Trusted CIDR peer → the right-most untrusted hop.
    - Trusted peer with an empty or malformed header → the remote address.
    - IPv6 works.
  - **OtpLog.** A captured line contains no code, nonce, full email or session id. Newlines in the domain are sanitised.
  - **Hardening (added 2026-10-13, user decision):**
    - `x@\u212Aey.org` (Kelvin sign) and any non-ASCII email or allow-list entry → rejected;
    - a logged `domain` of `evil.org outcome=ok` cannot produce a second `outcome=` field;
    - a hop of `.:` is treated as malformed without a DNS lookup;
    - a null outcome logs `internal_error`.
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

- **Status:** done [x] · **Size:** S · **Depends on:** T03, T05 · **Module:** marlo-web
- **Requirements:** FN-004, FN-005 (predicate), FN-015, SEC-009, FN-001 (domain plus configured)
- **Design:** §5.1 step 6, DD-4, §6 (the `otpEnabled` variant); Premise Ledger P-12, P-13
- **Files:** `security/otp/OtpEligibility.java` and `OtpEligibilityTest.java` (both new)
- **Scope:**
  - *(Superseded by T21, 2026-10-15: the predicate is now: the unit's switch on and the domain not excluded, then the rest; there is no CGIAR check on a password-on unit. The text below is the original.)* Full predicate: allow-list match, user exists, user active, `existActiveCrpUser` (an **active** membership, `crp_users.is_active = 1`; requirements Decision Log 2026-10-13), not (CGIAR user **and** `CognitoAuthSpecificity.isActiveFor(...)`).
  - Disclosure-safe variant for `crpByEmail`: configured, domain match and SEC-009 only.
- **Tests:**
  - Each refusal reason alone returns false: unknown, inactive, non-member, soft-deleted member (`is_active = 0`), CGIAR on a Cognito-active unit *(Superseded by T21, 2026-10-15: no longer a refusal on a password-on unit)*, domain not allowed *(now: switch off or domain excluded)*, unconfigured.
  - A CGIAR user on a unit where Cognito is **inactive** is eligible (OQ-4 default).
  - The variant ignores active state and membership.
- **Verification:** `-Dtest=OtpEligibilityTest`
- **Falsifier:** remove the membership term; the non-member case must go red. Revert to the activity-blind `existCrpUser`; the soft-deleted cases in `OtpEligibilityTest`, `OtpRequestCodeActionTest` and `OtpVerifyCodeActionTest` must go red.
- **Red run:** the tests are written first.
- **Disqualifier:** fakes that return a hard-coded `true` regardless of the arguments they receive. Assert the userId and globalUnitId each fake received (KZ-…-auth-flow-2).
- **Consumers:** T10, T11, T13. They are the only callers.
- **Review:** `checklist`
- **Done when:** the tests are green and record the argument assertions.
- **Skills:** `tdd`, `marlo-verify`

### CHG-OTP-LOGIN-001-T07 — Shiro token and realm branch

- **Status:** done [x] · **Size:** S · **Depends on:** T02 · **Module:** marlo-data, plus a test in marlo-web
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

- **Status:** done [x] · **Size:** XS · **Depends on:** none · **Module:** marlo-web
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

- **Status:** done [x] · **Size:** M · **Depends on:** T02, T05 · **Module:** marlo-web
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
  - Never write to `email_log`. Never read the notification flag or the support redirect. (Superseded for `email_log` by T18, 2026-10-14.)
  - Render every FN-017 element from i18n keys, HTML-escaped. A render with an unresolved placeholder fails.
- **Tests (seam: a transport stub):**
  - success → `delivered`;
  - transport exception → `email_failed` WARN;
  - queue full → `email_failed`, and the caller is not affected;
  - the rendered body contains the code, "5", the single-use text, the do-not-share text, the ignore text and the support contact, and no `http` URL containing the code;
  - `EmailLogManager` is never invoked (a fake that records calls). (Superseded by T18, 2026-10-14: one row per queued email, without the code.)
- **Verification:**
  - `-Dtest=OtpMailSenderTest`.
  - `.properties` review for apostrophes and braces (`marlo-verify` Gate 6).
- **Falsifier:** route the send through `SendMailS.send`. The `EmailLogManager`-never-called assertion must go red. (Superseded by T18, 2026-10-14: the falsifier is now the static lock on `SendMailS`.)
- **Red run:** the tests are written first.
- **Disqualifier:** a test stub that never runs on the executor thread cannot observe the queue-full case.
- **Consumers:** none (new). `global.properties` keys must have no collisions: `grep -n "^login.otp" global.properties` is empty before this task.
- **Review:** `full` — the SEC-001 leak surface.
- **Done when:** the tests are green and the properties are reviewed.
- **Skills:** `marlo-verify`, `error-handling-patterns`

### CHG-OTP-LOGIN-001-T10 — `OtpRequestCodeAction`

- **Status:** done [x] · **Size:** M · **Depends on:** T03, T04, T05, T06, T08, T09 · **Module:** marlo-web
- **Requirements:** FN-001, FN-004, FN-005 (and its scenario: BUT and both AND IT MUST clauses), FN-006, FN-007, FN-008, SEC-006 (request side), SEC-008, SEC-010, OPS-001, NF-001
- **Design:** §4 status table, §5.1 (all 11 steps), DD-5
- **Files:** `action/home/OtpRequestCodeAction.java` and `OtpRequestCodeActionTest.java` (both new)
- **Files added 2026-10-13 (user-approved scope amendment, `execution.md` T10):** `@Named` plus an `@Inject` constructor on `security/otp/OtpKeys`, `OtpRateLimiter`, `OtpCodes` and `OtpEligibility`, so that every action shares one instance of each. Test-seam constructors carry no `@Inject`. The app must start with the four beans (`login.do` 200), proven as in T09.
- **Tests (fakes for the managers, the mailer and the eligibility predicate):**
  - **Order:** the rate-limit buckets are counted before any user lookup (the lookup fake records the call order).
  - **Neutrality:** for unknown, inactive, non-member and CGIAR-refused accounts, `response` equals the eligible case except for the masked email. A row is issued in every state. The mailer is called **only** when eligible.
  - **Rejections:**
    - domain not allowed → `domainNotEnabled`, with no issue *(Superseded by T21, 2026-10-15: switch off or domain excluded)*;
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

- **Status:** done [x] · **Size:** L · **Depends on:** T03, T04, T05, T06, T07, T08 · **Module:** marlo-web
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
  - Gate-4 refusal inside `finishLogin` → `failed` with the `authError=cognitoFailed` redirect (`LoginAction.AUTH_ERROR_FAILED`; clarified 2026-10-13), and **no NPE**.
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

- **Status:** done [x] · **Size:** S · **Depends on:** T10, T11 · **Module:** marlo-web
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

- **Status:** done [x] · **Size:** S · **Depends on:** T03, T06 · **Module:** marlo-web
- **Requirements:** UI-001, SEC-006 (no new disclosure), FN-002 (live)
- **Design:** §6 `crpByEmail.do` row; Premise Ledger P-20
- **Files:**
  - `CrpByUserEmailAction.java`: add the `otpEnabled` key per unit, using one uncached query per call, and only when configured. *(Amended 2026-10-21, doc drift check against the code, `CHG-PWD-SWITCH-001` T04: the query now runs even when the secret is unconfigured, because `passwordEnabled` needs the password switch: one query for all three keys, `system_password_login_active` included, for every listed unit; `OtpEligibility.methodsFor`, `OtpEligibility.java:382-383`.)*
  - `CrpByUserEmailActionTest.java`: extended.
- **Tests:**
  - The flag is true only when configured, the domain is allowed and SEC-009 passes. *(Superseded by T21, 2026-10-15: configured, switch on and domain not excluded; SEC-009 no longer gates it on a password-on unit.)*
  - The flag is identical for an active and an inactive user, and for a member and a non-member listed unit (JI-11).
  - Unconfigured → no allow-list query (a fake counts the calls) and every flag false. *(Superseded by T21, 2026-10-15: it is the switch and exclusion query that is not run.)* *(Amended 2026-10-21, doc drift check against the code, `CHG-PWD-SWITCH-001` T04: no longer true: unconfigured makes exactly one specificity query (the three keys, every listed unit) and every `otpEnabled` is false; test `CrpByUserEmailActionTest.unconfiguredMakesExactlyOneSpecificityQueryAndOtpIsFalseForEveryUnit`.)*
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

- **Status:** done [x] · **Size:** M · **Depends on:** T12, T13 · **Module:** marlo-web (webapp)
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

- **Status:** done [x] · **Size:** M · **Depends on:** T01–T14 · **Module:** spec folder (`probes/`)
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

- **Status:** done [x] · **Size:** S · **Depends on:** T15 · **Module:** spec folder
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

### CHG-OTP-LOGIN-001-T17 — Step 3 method choice (UI-006)

- **Status:** done [x] · **Size:** S · **Depends on:** T14 · **Module:** marlo-web (webapp)
- **Requirements:** UI-006 (new 2026-10-14), UI-001, UI-004, UI-005, FN-019 (password and Cognito unchanged)
- **Design:** §6 rows "`login.js` mode" and "Method choice (T17)"; Decision Log 2026-10-14 in `requirements.md`
- **Files:**
  - `loginForm.ftl`: the choice block and the code panel inside `#login-step-password`; the password panel reuses the existing `#login-password` and "Log in".
  - `login.js`: the LOCAL arm of `showPasswordStep()` when `otpEnabled === true`; panel switching; focus; Enter guard; reset on leaving step 3.
  - `global.properties`: the new strings (heading, two method buttons, code-panel text with `{0}` for the masked address, "Send code", "Choose another way to sign in"), apostrophes doubled.
  - The CSS the login page already loads, for the method buttons and the panel, reusing `login-form-button`.
  - The FTL that references `login.js` (and the CSS, if touched): bump the `?YYYYMMDD` cache-buster.
- **Out of scope:** any Java, endpoint, flag, Struts or server rule; the Cognito branch; the code step `#login-step-otp` beyond reaching it from "Send code".
- **Tests:** a browser walkthrough with claude-in-chrome on the local stack (throwaway database copy, mail to a local sink), covering:
  - CGIAR user on a Cognito unit: only "Sign in with CGIAR", no choice, no password input in the DOM (as today);
  - external user on a unit without the code: password field directly, no choice (as today);
  - external user on a code unit: the choice; "Continue with your password" → password panel → sign-in with a password works; "Choose another way" → the choice; "Email me a sign-in code" → code panel with the masked address → "Send code" → the code step → verify → dashboard;
  - Enter pressed on the choice and on the code panel does not submit `login.do` (network log);
  - "Go back" from the choice and "use a different email" from the code step: step 3 reopens on the choice;
  - focus lands on the first control of each opened view.
- **Verification:** compile gate; Checkstyle n/a (no Java); i18n review of every added `.properties` line; a recorded GIF and network log of the walkthrough saved under `probes/`.
- **Verification notes (2026-10-14):** compile exit 0 (Implementer and Leader re-run); Checkstyle n/a; i18n clean; Reviewer PASS; walkthrough recorded in `probes/output/t17-walkthrough.out` (the GIF stays local: it shows a personal test address).
- **Falsifier:** remove the Enter guard. Pressing Enter on the code panel then posts `login.do` with an empty password, which the network log must show.
- **Red run:** n/a. JS unit tests do not exist in MARLO.
- **Disqualifier:** a walkthrough started before the page JS has initialised; a walkthrough against `aiccradb_actsave` or with mail pointed at a real relay.
- **Consumers:** `showEmailStep()`, `showProjectStep()`, the "Go back" handler, the T14 OTP handlers (`#login-otp-button`, and "use a different email", the code step's only exit), `crpUsers.js` (P-20).
- **Review:** `full`
- **Done when:** the walkthrough covers every branch above, the cache-buster is bumped, and the properties are reviewed.
- **Skills:** `marlo-verify`, `frontend-design`, `claude-in-chrome`


### CHG-OTP-LOGIN-001-T18 — Record code emails in `email_logs` for support (OPS-005)

- **Status:** done [x] · **Size:** S · **Depends on:** T09, T10 · **Module:** marlo-web
- **Requirements:** OPS-005 (new 2026-10-14), AC-18, AC-11 (amended), SEC-001, SEC-006, OPS-002, FN-018 (unchanged)
- **Design:** §5.3 `OtpMailSender` (amended 2026-10-14), §11, DD-7 amendment; Decision Log 2026-10-14 in `requirements.md`
- **Files:**
  - `utils/OtpMailSender.java`: inject `EmailLogManager`; after the delivery attempt of a queued message, on the worker thread, save one row; a public `SOURCE_ACTION = "/otpRequestCode"` and the placeholder constant. *(Superseded by T19, 2026-10-15, recorded 2026-10-21 by the doc drift check against the code: there is no placeholder constant; `message` holds the full wrapped HTML body, code included.)*
  - `action/home/OtpRequestCodeAction.java`: the `CodeSender` seam also passes the Global Unit id, so the row carries `global_unit_id`.
  - `action/json/global/SendFailEmailAction.java`: `selectedEmailLogs()` drops rows whose `source_action` is `OtpMailSender.SOURCE_ACTION`, for the single id and for the filtered bulk path.
  - `OtpMailSenderTest.java`, `OtpRequestCodeActionTest.java`: updated; a test for `SendFailEmailAction` (new or extended).
  - `runbook.md` (A4 sender row, A5 support shortcut): already amended with the spec; check it matches the code.
  - `probes/ac-probe.sh` and `probes/README.md` (added after the attempt 1 review): the AC-11 check follows the amended AC-11, a new stage proves AC-18, and the ledger rows for AC-11 and AC-18 are re-recorded from a new run.
- **Scope:**
  - Row fields: `to_email` = the recipient, `subject` = the rendered subject, `date_email` = now, `global_unit_id`, `source_action`, `tried = 1`, `succes_email` = true on delivery and false on failure, `error_email` = the exception class simple name on failure, `message` = "Sign-in code email (code withheld)". `cc`, `bbc`, file columns stay null. *(Superseded by T19, 2026-10-15, recorded 2026-10-21 by the doc drift check against the code: `message` is the full wrapped HTML body, code included; `bbc_email` is the support mailbox outside production and null in production.)*
  - The save goes through `EmailLogManager.saveEmailLog` (`@Transactional`), after `delivered` or `email_failed` is logged. Any exception from the save is caught, logged at WARN with the class name only, and swallowed: the worker keeps running.
  - A hand-off rejected before queueing (full queue, a message that cannot be built, a bad recipient) writes no row.
  - No row anywhere on the request thread; nothing changes in the response or its timing.
- **Out of scope:** `SendMailS`, `ThreadSendMail`, the notification and support-redirect specificities, OQ-5, the System Admin → Emails UI, any migration.
- **Tests:**
  - delivered → one saved row with `succes_email` true, the placeholder message, the source, the unit id, and the code in no field (check every string field); *(Superseded by T19, 2026-10-15, recorded 2026-10-21 by the doc drift check against the code: the code is allowed in `message` only, and in no other field.)*
  - transport failure → one row with `succes_email` false and the class name as error, no exception message;
  - queue full and unbuildable message → no row;
  - a save that throws → the delivery line is still written and the next send still works;
  - `SendFailEmailAction`: an OTP row is excluded by id and in bulk, a non-OTP failed row is still selected;
  - `OtpRequestCodeAction`: the unit id reaches the seam on the eligible path; the neutral path still calls the seam never.
  - The T09 test `theMailerNeverReachesTheEmailLogTheNotificationFlagOrTheSupportRedirect` is narrowed: `SendMailS`, `ThreadSendMail`, the two specificity keys (`CRP_EMAIL_NOTIFICATIONS`, `CRP_EMAIL_SUPPORT_TEAM` and their key strings) and the redirect methods stay forbidden; `EmailLogManager`/`EmailLog` are now required.
- **Verification:** compile gate (clean classes); Checkstyle delta against HEAD on the changed Java files; `mvn -q -o -pl marlo-web -am test -Dtest='OtpMailSenderTest,OtpRequestCodeActionTest,SendFailEmailAction*Test' -DfailIfNoTests=false`; a local run on `aiccradb_otp_probe` with the sink: one delivered and one failed code email give the two rows, and an ineligible request gives none.
- **Falsifier:** write the rendered body (`message` with the code) instead of the placeholder; the code-in-no-field test must go red. Remove the `source_action` filter; the resend exclusion test must go red. *(Superseded by T19, 2026-10-15, recorded 2026-10-21 by the doc drift check against the code: the first falsifier is inverted by T19: storing the placeholder instead of the body turns the body test red.)*
- **Red run:** the new tests run red before the change.
- **Disqualifier:** a test that saves on the calling thread instead of the executor thread; a local run against `aiccradb_actsave` or with mail pointed at a real relay.
- **Consumers:** `OtpRequestCodeAction.handOff` (the only caller of the seam); System Admin → Emails (`EmailLogsAction`, list only) and `SendFailEmailAction` (resend).
- **Verification notes (2026-10-14):** clean compile 2415 / 1062 / 72 files; tests green with `-am` (Leader re-run after attempt 1: 130 tests in six classes, 0 failures; attempt 2: `OtpMailSenderTest` 24, `SendFailEmailActionTest` 5); Checkstyle delta 0 on the changed Java files; no `.properties` change. Falsifiers red: the rendered body in `message` (code-in-no-field test), the source filter removed (4 of 5 resend tests), the delivery line after the save (order test), a specificity key in the sender (static lock). Live: `ac-probe.sh leak emaillog` on a clean build against `aiccradb_otp_probe` and the sink, `SUMMARY: 32 checks, 0 failed` (`probes/output/ac-probe.t18-leak-emaillog.out`, ledger rows AC-11 and AC-18 in `probes/README.md`). Reviewer PASS on attempt 3.
- **Review:** `full` — the SEC-001 leak surface and the SEC-006 timing surface.

### CHG-OTP-LOGIN-001-T19 — Align the code email with MARLO's email rules: non-production recipient and full body in `email_logs` (FN-018, OQ-5, OPS-005)

- **Status:** done [x] · **Size:** S · **Depends on:** T09, T18 · **Module:** marlo-web, plus `probes/`
- **Requirements:** FN-018 (amended 2026-10-15), AC-19 (new), OPS-005, SEC-001 and AC-11, AC-18 (amended 2026-10-15: full body), SEC-006, AC-15 (unchanged)
- **Design:** §5.3 `OtpMailSender` (amended 2026-10-15), DD-7 amendment 2026-10-15, §14 step 4; Decision Log 2026-10-15 in `requirements.md`
- **Files:**
  - `utils/OtpMailSender.java`:
    - outside production, route to `email.notification` and add the subject prefix and the body header;
    - fill the row as `SendMailS` does, with the full HTML body in `message`;
    - remove `PLACEHOLDER_MESSAGE`.
  - `OtpMailSenderTest.java`: the production and non-production cases below.
  - `probes/ac-probe.sh`, `probes/README.md`: the AC-11 and AC-18 checks follow the amended text (the code is allowed only in the `message` of the OPS-005 row). Local runs are non-production, so every code email now reaches the sink addressed to `email.notification`. The probe must find a user's code by the body header naming the submitted address, not by the envelope or TO, and a new stage proves AC-19. Re-record the ledger rows the run touches.
  - `runbook.md` (A4 and A5) is already amended with the spec. Check that it matches the code.
- **Scope:**
  - "Non-production" is `!config.isProduction()`, read on every send, not cached at construction.
  - **Non-production:**
    - TO is `config.getEmailNotification()`, parsed through the existing `singleAddress` check;
    - a blank or invalid value makes `build` fail, which logs `email_failed` at once and writes no row; never fall back to the submitted address;
    - the subject is `"(TESTING ENVIRONMENT) " + subject`;
    - the plain body starts with `To: <submitted>` and a separator line. The HTML body starts with the same header, the address HTML-escaped, using the `escapeHtml` helper;
    - the header is a fixed English marker, not an i18n key, like `SendMailS`'s;
    - the code, expiry and the rest of the copy are unchanged.
  - **Production:** byte-for-byte the current message; only the submitted address.
  - **Row (OPS-005):**
    - `to_email` is the submitted address in both environments;
    - `bbc_email` is the support mailbox outside production and null in production;
    - `subject` has no prefix in either.
  - `OtpLog` delivery `domain` stays the submitted address's domain.
  - **Body in the row:**
    - `message` is the rendered HTML part exactly as built for production, with the code and without the non-production header;
    - it is still written on the worker after the attempt, and still excluded from resend;
    - the code appears in no other column.
  - **Code in the log, non-production only:** after the delivery attempt, on the worker, write one INFO line `auth.otp.code gu=<acronym> to=<submitted> code=<code>`. Use the same per-send `isProduction()` value that routed the email, and write it for delivered and failed sends alike. In production, write no such line, and no other log line may contain the code.
- **Out of scope:** `SendMailS`, `crp_enable_email_notification`, `crp_email_support_team`, the resend exclusion (unchanged), the System Admin → Emails UI, any i18n or migration change.
- **Tests:**
  - non-production, delivered: one message whose only recipient (TO, no CC or BCC) is the support mailbox. The subject starts with the prefix; both parts start with the header naming the submitted address, escaped in HTML; the code is still in the body. The row has `to_email` = submitted, `bbc_email` = support, an unprefixed subject, and `message` equal to the production HTML body with the code and without the test header;
  - non-production with `email.notification` null, blank or a list: no transport call, an `email_failed` delivery line, no row, and the submitted address is never a recipient;
  - production: the recipient is the submitted address only, with no prefix, no header and a null `bbc_email`. The existing T09 and T18 assertions still pass;
  - log line: outside production, exactly one `auth.otp.code` INFO line per handed-off email, for delivered and failed sends, with the unit, the submitted address and the code; in production, no captured log line contains the code or the submitted address;
  - probe: the `leak` check allows the code only in the `auth.otp.code` line, since local runs are non-production;
  - `isProduction()` is read per send: flip it between two sends on one instance and each send follows the current value.
- **Verification:**
  - compile gate with clean classes;
  - Checkstyle delta against HEAD on the changed Java files;
  - `mvn -q -o -pl marlo-web -am test -Dtest='OtpMailSenderTest,OtpRequestCodeActionTest,SendFailEmailActionTest' -DfailIfNoTests=false`;
  - a live run on a fresh throwaway copy (`aiccradb_otp_probe` rebuilt from `aiccradb_actsave` with the T15 fixtures) and the local sink, with `marlo.production=false`. Run the new stage plus `leak emaillog`, and record the sink's TO and subject for one code email;
  - afterwards, clean up as on 2026-10-14: stop the app and the sink, drop the copy, and delete the scratch credentials.
- **Falsifier:**
  - write the `auth.otp.code` line in production too: the production no-code-in-log test goes red;
  - store the placeholder instead of the body: the body test goes red;
  - store the body with the non-production header: the body test goes red;
  - send to the submitted address when non-production: the recipient test goes red;
  - fall back to the submitted address when the support mailbox is blank: the fail-closed test goes red;
  - cache `isProduction()` in the constructor: the per-send test goes red.
- **Red run:** the new tests run red before the change.
- **Disqualifier:**
  - a live run against `aiccradb_actsave` or `aiccradb1`–`4`;
  - mail pointed at a real relay;
  - a test that sets `marlo.production` in a tracked properties file.
- **Consumers:** `OtpRequestCodeAction.handOff` (unchanged seam); System Admin → Emails (reads the row).
- **Review:** `full`. The change decides who receives a sign-in code (SEC-001).
- **Verification notes (Implementer, 2026-10-15; the Leader sets the status):**
  - Red run: `OtpMailSenderTest` before the change, 32 tests, 10 failures. The `auth.otp.code` tests were added after and run red through falsifier F7.
  - Green: `OtpMailSenderTest,OtpRequestCodeActionTest,SendFailEmailActionTest` 82 tests, 0 failures; full `mvn -q -o -pl marlo-web -am test` 792 tests, 0 failures. Clean compile 2415 / 1063 / 77 files, BUILD SUCCESS. Checkstyle on the two changed Java files: HEAD 0, working tree 0.
  - Falsifiers, each red then restored: placeholder body (4 failures), body with header (1), submitted address as recipient (3), fallback on a blank mailbox (1), `isProduction()` cached in the constructor (10), code line in production (7), no code line at all (4).
  - Live run on a fresh `aiccradb_otp_probe`: `nonprod leak emaillog` 43 checks, `enum` 11, `nonprodblank` 4, all passing. Output in `probes/output/ac-probe.t19-*.out`; `probes/README.md` section 10 and the AC-2, AC-3, AC-4, AC-11, AC-18 and AC-19 rows.
  - The production half (recipient, no prefix or header, null `bbc_email`, no code line) is covered by unit tests only: `marlo.production` stayed false.

### CHG-OTP-LOGIN-001-T20 — Branded format for the code email (FN-017, UI-005, AC-20)

- **Status:** done [x] · **Size:** S · **Depends on:** T19 · **Module:** marlo-web, plus `probes/`
- **Requirements:** FN-017 and UI-005 (amended 2026-10-15), AC-20 (new), FN-018, OPS-005, SEC-001, SEC-006 (unchanged)
- **Design:** DD-7 amendment "branded format, T20"; requirements Decision Log 2026-10-15 (branded format). Reference implementation: A2-2484, `utils/EmailLayout.java`, `utils/EmailPlainText.java`, `SendEmails` role emails, the `email.layout` and `email.role.*` keys of `custom/aicrra.properties`, and `reports/ai-context/role-assignment-email-catalog.md`.
- **Files:**
  - `utils/OtpMailSender.java`: wrap the HTML body with `EmailLayout.wrap(texts, assetsUrl, body)`.
  - `action/home/OtpRequestCodeAction.java`, or a small helper: give the sender a text provider for the selected unit, plus the assets URL (`getBaseUrlCdn()`).
  - `global.properties`, and `custom/aicrra.properties`, `custom/aiccra3.properties`, `custom/test.properties`: per-unit OTP email copy in the role-email style.
  - The tests.
  - `probes/ac-probe.sh` only if a check reads the HTML layout.
- **Scope:**
  - **Text provider for the selected unit:**
    - Read the unit's `crp_custom_file` parameter, the same one `InternationalitazionFileInterceptor` uses.
    - Resolve the email keys from `custom/<file>.properties` first, falling back to `global.properties`.
    - Build the provider for this email only. Do not mutate the shared `LocalizedTextProvider`, the request's provider or the session: they are shared across requests and users.
    - Resolve it before the hand-off, so the read adds no work to the eligible path that the ineligible path does not also do (SEC-006). The simplest way is to resolve it for every request that reaches the send decision, or after the response. If either is impossible, state the residual timing difference in the report.
  - **Shell:** wrap only the HTML part, with `EmailLayout.wrap`. The text part keeps its own key.
    - The non-production header (T19) goes before the wrapped HTML, as `SendMailS` does.
    - The `email_logs` row stores the wrapped HTML as sent to the user, without the test header.
  - **Copy for units with a shell:** override `login.otp.email.subject` and `login.otp.email.html` in `aicrra`, `aiccra3` and `test`, in the role-email style:
    - subject with the unit's own role-email prefix: `[AICCRA] Your MARLO sign-in code` for `aicrra` and `aiccra3`, `[MARLO] …` for `test`;
    - `<p>` paragraphs, the code prominent;
    - no support line or do-not-reply text, which the shell's footer carries.

    Keep `{0}`–`{3}` argument semantics. Keep every FN-017 element across body plus shell. Double every apostrophe (MessageFormat). Units without a shell keep the global copy unchanged.
  - **Images:** `{1}` is the CDN or base URL, as `EmailLayout.wrap(BaseAction, …)` uses. No link carries the code.
- **Out of scope:** `EmailLayout`, `EmailPlainText` and `SendMailS` themselves; the page copy; other emails.
- **Tests:**
  - AICCRA-like provider with a shell: the HTML part is the shell around the body with the code and has the `{1}` URL; the text part has the code.
  - Global-only provider: the HTML part equals the bare body (shell `{0}`).
  - The per-unit provider falls back to global for keys the unit does not define.
  - The shared provider and the session are untouched.
  - Non-production: the header precedes the wrapped HTML, and the row stores the wrapped HTML without the header.
  - The existing T09, T18 and T19 assertions still pass.
- **Verification:**
  - compile gate;
  - Checkstyle delta;
  - targeted tests and the full suite with `-am`;
  - i18n gate on every added `.properties` line;
  - a local visual check: send one AICCRA code email to the local sink, render its HTML part in the browser, and record a screenshot (not committed) plus the structure in the report;
  - afterwards, clean up the environment as on 2026-10-15.
- **Falsifier:**
  - skip the wrap: the shell test goes red;
  - resolve with the global provider only: the AICCRA test goes red.
- **Disqualifier:**
  - mutating a shared text provider or the session;
  - a live run against `aiccradb_actsave` or `aiccradb1`–`4`;
  - mail to a real relay.
- **Review:** `full`. Pre-auth i18n resolution, SEC-006 timing, the email the user receives.
- **Verification notes (Implementer, 2026-10-15; the Leader sets the status):**
  - Files: new `utils/UnitTextProvider.java` (a `TextProvider` over the unit's `custom/<crp_custom_file>` bundle, then `global`, formatted with `MessageFormat`; the file name must match `[A-Za-z0-9_-]{1,64}`); `OtpMailSender.sendAsync` takes the assets URL and wraps the HTML part with `EmailLayout.wrap`; `OtpRequestCodeAction` gets a `CustomParameterManager`, reads `crp_custom_file` and builds the provider; `login.otp.email.subject` and `login.otp.email.html` in `aicrra`, `aiccra3` and `test`. `test` uses the `[MARLO]` subject prefix, like its own role emails; the other two use `[AICCRA]`.
  - Clean compile 2415 / 1064 / 78 files, BUILD SUCCESS. Checkstyle on the 8 changed Java files: HEAD 0, working tree 0. i18n gate: no FIX finding, no apostrophe in the added lines.
  - Targeted `OtpMailSenderTest,OtpRequestCodeActionTest,UnitTextProviderTest,OtpStrutsPackageTest,OtpVerifyCodeActionTest,EmailLayoutTest`: 156 tests, 0 failures. Full `mvn -q -o -pl marlo-web -am test`: 811 tests, 0 failures (baseline 792, plus 19 new).
  - Falsifiers, each red then restored: wrap skipped (4 failures); request provider handed over instead of the unit's (1 failure, 2 errors); custom file read on the eligible path only (2); row stores the header (2); custom file ignored (8). The new tests cannot run before the change (the signatures differ), so the falsifiers are the red evidence.
  - SEC-006: the `crp_custom_file` read and the bundle load happen once for every request that reaches the eligibility check, eligible or not, before the challenge row, and a failure answers with the request's own provider without changing the answer. The one residual difference is the `MessageFormat` rendering of the body and the shell, which runs on the request thread for an eligible account only, like the body rendering that T09 already did there.
  - Live run on a fresh `aiccradb_otp_probe` (AICCRA, `crp_custom_file = aicrra`, `marlo.production=false`, sink mailbox `support.probe@sink.test`): `nonprod` 8 checks, 0 failed. The sink's HTML part is the header, then the AICCRA shell: logo and lockup, grey card with the title, the code, the expiry, once, do not share and ignore lines, the hills band, the footer note with the automated-notification line, and the `mailto:` support link. The images load from the instance base URL. The `email_logs` row holds the wrapped HTML without the header. A screenshot was taken and not committed.
  - Not re-run for T20: the other probe stages. `ac-probe.sh` needed no change: the checks that read the HTML part look for the code between tags or compare it with the row, and both still hold.
  - Cleanup as on 2026-10-15: the application, the sink and the page server stopped, the copy dropped, the scratch credentials, mbox and logs deleted. `marlo-dev.properties` had the same SHA-256 before and after. `aiccradb_actsave` and `aiccradb1`-`4` were never written to.

### CHG-OTP-LOGIN-001-T21 — Per-unit switch and domain exclusion instead of the allow-list (DD-13)

- **Status:** done [x] — definition approved and implemented 2026-10-15; Reviewer PASS · **Size:** M · **Depends on:** T20 · **Module:** marlo-data, marlo-web, plus `probes/` and the runbook
- **Requirements:**
  - **amended 2026-10-15:** FN-001, FN-002, FN-003, FN-005, FN-006, SEC-009, DA-002, UI-001, UI-006, AC-1, AC-2, AC-4, AC-13;
  - **new:** AC-21, OQ-9;
  - **superseded:** OQ-4.
- **Design:**
  - DD-13 (new) and DD-12 (amended);
  - §5.1 steps 4 and 6, §5.3 `OtpDomainPolicy`, §6 `crpByEmail.do`, §9;
  - requirements Decision Log 2026-10-15 (T21).
- **Files (expected):**
  - **Migration:** a new `V2_6_0_<YYYYMMDD>_<HHMM>__ReplaceOtpAllowListWithSwitchAndExclusion.sql`, built with the `marlo-migration` skill, timestamp from the real clock.
    - Seed `system_otp_login_active` (boolean format, default `false`) and `system_otp_excluded_email_domains` (string, default empty) for the existing types among 1–4, with the exact descriptions of DA-002. They must read clearly on the Super Admin Specificities tab.
    - Delete the `crp_otp_allowed_email_domains` `custom_parameters` rows, then its `parameters` rows.
    - Idempotent, no assumed Global Unit id, FK order respected.
    - Do not edit `V2_6_0_20261009_1025`.
  - **`APConstants.java` (both files):** `OTP_LOGIN_ACTIVE`, `OTP_EXCLUDED_EMAIL_DOMAINS`; remove `OTP_ALLOWED_EMAIL_DOMAINS`.
  - **Uncached reads (DD-12):** the uncached read in `OtpChallengeMySQLDAO`/manager covers both keys, for one unit and for the `crpByEmail.do` batch.
  - **`OtpEligibility`:** the switch and the exclusion; no `is_cgiar_user`, no `CognitoAuthSpecificity`. *(Amended 2026-10-17, `CHG-PWD-SWITCH-001` T03: both are read again, on password-off units only.)*
  - **`OtpDomainPolicy`:** exclusion semantics; no `cgiar.org` drop.
  - **`OtpRequestCodeAction` step 4:** the switch and the exclusion.
  - **`CrpByUserEmailAction` `otpEnabled`.**
  - **`login.js`, `loginForm.ftl`:** UI-006 choice for the Cognito branch, "Sign in with CGIAR" against "Email me a sign-in code". Bump the cache-buster.
  - **Tests.**
  - **`probes/ac-probe.sh` fixtures:** set the switch and the exclusion instead of the allow-list; re-record the AC rows that change.
- **Scope:** exactly the rule of DD-13. Everything else stays as T01–T20 left it:
  - statuses and limits;
  - neutral response;
  - mail, `email_logs` and the non-production rule;
  - branded format.
- **Out of scope:**
  - renaming other `crp_` keys;
  - a "code only" mode;
  - per-IP limit changes (rollout stage 2, B5);
  - editing the archived Cognito spec (the change is approved through OQ-9 and recorded in this spec).
- **Tests:**
  - switch off answers `domainNotEnabled`, hides the option and writes no row;
  - switch on with an empty exclusion: an `is_cgiar_user = 1` account on a Cognito-active unit gets a code and signs in;
  - an excluded domain answers `domainNotEnabled`, and `otpEnabled` is false;
  - the FN-003 lookalikes are not excluded and the normalised form is;
  - no read of `is_cgiar_user` or `CognitoAuthSpecificity` on the code path (static lock) *(Superseded by `CHG-PWD-SWITCH-001` T03: see the note under "Static lock" below)*;
  - `crpByEmail.do` batch: one query for every listed unit;
  - UI-006: a CGIAR account on a Cognito unit with the code on sees the choice; with its domain excluded it sees only "Sign in with CGIAR";
  - the migration on a throwaway copy, twice, plus a database without units 45/47 (AC-21).
- **Verification:**
  - compile gate;
  - Checkstyle delta;
  - targeted tests and the full suite with `-am`;
  - i18n gate if `.properties` change;
  - migration gate on a throwaway copy;
  - a browser walkthrough of the three step-3 branches (external, CGIAR on Cognito with the code, CGIAR with `cgiar.org` excluded);
  - the `ac-probe.sh` stages touched by the fixtures;
  - clean up afterwards.
- **Falsifier:**
  - keep a CGIAR check: the CGIAR-gets-a-code test goes red;
  - treat the list as an allow-list: the exclusion tests go red;
  - read the switch through the cached path: the live-change check goes red.
- **Disqualifier:**
  - a domain fixed in code;
  - editing an applied migration;
  - writes to `aiccradb_actsave` or `aiccradb1`–`4`.
- **Review:** `full`. The change decides who can authenticate (SEC-009 reversal), and it adds a migration.
- **Verification notes (Implementer, 2026-10-15; the Leader sets the status):**
  - **Files:**
    - Migration `V2_6_0_20261015_1418__ReplaceOtpAllowListWithSwitchAndExclusion.sql` (clock `20261015_1418`), DML only. It seeds both keys for the types among 1-4 that exist, with the DA-002 texts verbatim, then deletes the old key's `custom_parameters` and then its `parameters`. `V2_6_0_20261009_1025` is untouched.
    - `APConstants` in both modules: `OTP_LOGIN_ACTIVE` and `OTP_EXCLUDED_EMAIL_DOMAINS` replace `OTP_ALLOWED_EMAIL_DOMAINS`.
    - Data layer: `OtpChallengeDAO.findSpecificityValues(keys, ids)`, one native uncached statement for both keys and every listed unit, and `OtpChallengeManager.specificityValuesFor(keys, ids)`. The old `allowListFor` and `findAllowListValues` are gone.
    - `OtpDomainPolicy`: `parseExclusionList`, `isExcluded`, `isSwitchOn` (only the text `true`, trimmed, any case) and `isOffered(switch, exclusion, email)`. The `cgiar.org` drop is removed.
    - `OtpEligibility`: no longer takes `CustomParameterManager` or `ParameterManager`. New `isOffered(email, unit)` (request step 4); `isEligible` and `otpEnabledFor(email, units)` use it. *(Amended 2026-10-21, doc drift check against the code, `CHG-PWD-SWITCH-001`: `OtpEligibility` takes `CustomParameterManager` and `ParameterManager` again and reads the Cognito flag on password-off units; `otpEnabledFor` has no main-code caller any more, since `CrpByUserEmailAction` uses `methodsFor`.)*
    - `OtpRequestCodeAction` step 4 calls `eligibility.isOffered`. `CrpByUserEmailAction` passes no `cgiarUser`; its `cognitoEnabled` read is unchanged. *(Amended 2026-10-21, doc drift check against the code, `CHG-PWD-SWITCH-001`: `CrpByUserEmailAction` now passes `cognitoAccountByUnit` to `methodsFor`, built from `cognitoEnabled` and `usrDB.isCgiarUser()`.)*
    - UI: `login.js` (`mode == "COGNITO"` with `otpEnabled` opens the choice; `setChoiceAlternative`; the Cognito handler is shared) and `loginForm.ftl` (`#login-method-cgiar-button`, container ids, `#login-external-user-label`). No new i18n key (`login.cgiarSignIn`), no CSS change. Cache-buster `?20261014` to `?20261015` in `login.ftl` and `401.ftl`.
    - Probes: `ac-probe.sh` (standing fixture, `enum`, `live`, `emaillog`, `postlogin` text), new `ac21-migration.sh`, six new outputs under `probes/output/`, `probes/README.md` (section 11, AC-1, AC-2, AC-4, AC-13, AC-17, AC-21).
    - Runbook, code facts only (see Deviations).
  - **Compile gate:** classes wiped, 2415 / 1064 / 78 files, BUILD SUCCESS (the last run on the final tree). **Checkstyle:** 10 main and 7 test Java files, HEAD 0, working tree 0, delta 0. **Hygiene:** all 17 files `ok`. **i18n gate:** no `.properties` change. **Cache-bust:** `login.js` bumped in both references; the other lines the script flags in `loginForm.ftl` are comments that name the file. `node --check` clean.
  - **Tests:** targeted, 9 classes, 269 tests, 0 failures (`OtpDomainPolicyTest` 34, `OtpEligibilityTest` 32, `OtpChallengeManagerImplTest` 34, `CrpByUserEmailActionTest` 14, `OtpRequestCodeActionTest` 49, `OtpVerifyCodeActionTest` 48, `OtpStrutsPackageTest` 8, `OtpMailSenderTest` 42, `UnitTextProviderTest` 8). Full `mvn -o -pl marlo-web -am test`: **832 tests, 0 failures** (baseline 811, plus 21 net).
  - **Migration gate (AC-21):** `ac21-migration.sh`, 17 checks, 0 failed (`output/ac21-migration.out`). Throwaway copy with a child row on the old key, applied twice; a database with no units 45 or 47, applied twice; one with no unit types, applied twice. Then Flyway applied it on a second fresh copy at application start (`schema_version` success 1).
  - **Falsifiers, each red then restored by hash:**
    - a CGIAR check kept in `isEligible`: 7 failures, including the CGIAR-gets-a-code tests and the static lock;
    - the list read as an allow-list: 80 failures and 5 errors of 177, the exclusion tests among them;
    - the specificity read remembered between calls: `everyCallReadsTheSwitchAndTheExclusionAgain...` red;
    - `cgiar.org` dropped again in the parser: 9 failures;
    - the DAO read through the Hibernate query cache (live stack): the `live` stage 3 of 7 red (`output/ac-probe.t21-live-FALSIFIER-red.out`).
  - **Static lock:** `OtpEligibilityTest.theCodePathNeverReadsIsCgiarUserOrTheCognitoSpecificity` scans `OtpEligibility`, `OtpDomainPolicy`, `OtpRequestCodeAction`, `OtpVerifyCodeAction` and the `otpEnabledByUnit` method of `CrpByUserEmailAction`, with comments stripped, for `isCgiarUser`, `is_cgiar_user`, `CognitoAuthSpecificity`, the Cognito key and a literal `"cgiar.org"`; a second test checks that `OtpEligibility` holds no parameter manager. The action tests' fake `CustomParameterManager` answers only `crp_custom_file`, so a Cognito read would also fail at run time.
    *(Superseded by `CHG-PWD-SWITCH-001` T03, 2026-10-16: the static lock and its second test were removed. Counting-double tests now prove that on a password-on unit the Cognito flag is never resolved and `is_cgiar_user` is never read: `OtpEligibilityTest#onAPasswordOnUnitTheCognitoFlagIsNeverResolvedAndIsCgiarUserIsNeverRead` (line 794), `OtpRequestCodeActionTest#onAPasswordOnUnitTheCognitoFlagIsNeverResolvedAndIsCgiarUserIsNeverRead` (line 897) and `OtpVerifyCodeActionTest#onAPasswordOnUnitTheRecheckNeverResolvesTheCognitoFlagForAnyAccountState` (line 813). The counting doubles are shown to see a read on a password-off unit by `OtpRequestCodeActionTest#theCountingDoublesDoSeeACognitoReadAndACgiarReadOnAPasswordOffUnit`.)*
  - **Live probes** (throwaway copy, sink on 2525, `ps eww` proof before any write): `enum` 18/0, `live` 7/0, `attempts removed limits nonprod postlogin emaillog` 30/0 with 1 NOT RUN (the resend half of AC-18 needs a super admin login), `session leak` 20/0.
  - **Browser walkthrough** (`output/t21-walkthrough.out`): external with the code (choice, password panel, code panel, back, code sign-in); CGIAR on Cognito with the code (choice with "Sign in with CGIAR" next to "Email me a sign-in code", no password control in the form, terms gate on the Cognito control, code sign-in, Enter guard); `cgiar.org` excluded live, no restart (single "Sign in with CGIAR", unchanged); switch off (password only, unchanged). The Cognito redirect itself was not followed.
  - **Deviations and assumptions:**
    - **UI route.** The choice for a Cognito unit reuses `#login-step-password` (the choice and the code panel already live there) instead of `#login-step-cgiar`. The password input is still removed from the document on this branch, and "External user" is hidden. UI-006 says the choice is "Sign in with CGIAR" or the password; I read "choosing a method reveals only that method's panel" as: for Cognito, the control itself starts the redirect, with the terms gate, since there is no panel behind it.
    - **Single read point.** Step 4 goes through `OtpEligibility.isOffered` rather than the action reading the manager itself. The read count of design section 12 is unchanged: one at step 4, one inside `isEligible`.
    - **Stale spec text (Leader).** Design section 6 still says "`COGNITO` keeps priority" in the `login.js` mode row and "The Cognito branch and every single-method case never render the choice" in the T17 row; both contradict DD-13 and UI-006 as amended. The T17 note in `execution.md` says the same. I did not edit `design.md`, `requirements.md` or `execution.md`.
    - **Runbook edits (code facts only):** Quick path rows, the `parseExclusionList` name, the A6 state table (the CGIAR row is gone, seven states, first four neutral, the not-enabled row and note, `denied_domain`), the A7 "Cognito cache lag" row (the re-check no longer reads the Cognito flag), the OQ-4 row (superseded). Part C is the 2026-10-13 walk record and is left as it was.
    - **Not re-run:** `expiry`, `resend`, `smtp`, `blackhole`, `tz`, `nosecret`, `nonprodblank` and the concurrency probes (they restart the application or wait minutes, and no code they exercise changed; the concurrency probes need the standing fixture, which `ac-probe.sh` with no stage sets). `ac17-migration.sh` is unchanged and still describes the T01 file.
    - **Probe harness.** One `enum` failure in the first run came from the probe's own request order (same normalised address, 30 s cooldown); fixed in the probe. The first falsifier attempt on the DAO failed for the wrong reason (a native cacheable query needs declared scalars) and was redone. Both are in `probes/README.md` section 11.
  - **Environment after the task:** application and sink stopped, nothing on 8080 or 2525, `aiccradb_otp_probe` and both scratch databases dropped, scratch credentials, mbox and logs deleted. `marlo-dev.properties` SHA-256 `4b617eb7...06311` before and after. `aiccradb_actsave` unchanged (OTP tables 0/0/0, 1623 `custom_parameters`, no `system_otp_*` parameter); `aiccradb1`-`4` never written to.

### CHG-OTP-LOGIN-001-T22 — Require POST on the code endpoints; catch `Error` in request; password-off copy (validation V-1, V-4, V-2)

- **Status:** done [x]. Definition proposed 2026-10-18 by the Leader, from `validation-report.md`, and approved by the user the same day. Implemented 2026-10-18; Reviewer PASS on attempt 1 (see `execution.md`).
- **Size and placement:** S · **Depends on:** T21 · **Module:** marlo-web (actions, `login.js`, `loginForm.ftl`, `global.properties`)
- **Origin:** `/akili-validate` 2026-10-18. Three findings:
  - **V-1 (FAIL):** design §4 routes both endpoints as POST, but neither action checks the method.
  - **V-4 (WARN):** `OtpRequestCodeAction` catches only `RuntimeException`.
  - **V-2 (WARN):** the copy for `unavailable` says "use your password" on password-off units, where the password is refused.
- **Requirements:** SEC-001 ("the code never reaches any log": a GET puts `?code=` in access logs), SEC-004, SEC-006, OPS-001, UI-002, UI-005. **Amended:** OPS-001's closed outcome sets each gain one value (see the Decision Log entry this task writes).
- **Design:** §4 routes "(POST)", which this task enforces. §5.1 and §5.2 gain a step 0. §11 log lines.
- **Files (expected):**
  - **`OtpRequestCodeAction.execute()`.** Step 0, before `process()`: when `ServletActionContext.getRequest()` is null, or its method is not `POST` (compared ignoring case), answer `{status: unavailable}`. Do nothing else: no limiter hit, no session read or write, no entity read, no challenge row, no mail. Log one `auth.otp.request outcome=bad_method` line. Follow the precedent in `ValidateUserAction.java:94-101`. Also widen the `catch` in `execute()` (`:261`) and in `isEligible` (`:336`) to `RuntimeException | Error`, as `handOff` already does.
  - **`OtpVerifyCodeAction.execute()`.** The same step 0: answer `{status: unavailable}` with no attempt counted, no challenge consumed, no session change and no limiter hit. Log one `auth.otp.verify outcome=bad_method` line.
  - **`OtpLog`.** Add `BAD_METHOD` to `RequestOutcome` and to `VerifyOutcome`. Rendered as `bad_method`, the way the existing values are.
  - **`global.properties`.** New key `login.otp.error.unavailablePasswordOff=Code sign-in is temporarily unavailable. Please try again in a few minutes.` (no apostrophe, no brace). Add it to a `custom/*.properties` file only if that file already overrides `login.otp.error.unavailable`.
  - **`loginForm.ftl`.** One more `<span data-otp-copy="unavailablePasswordOff">` in `#login-otp-copy`.
  - **`login.js`.** In `otpShowError`, map `unavailable` to `unavailablePasswordOff` when `passwordEnabled === false`. Change nothing else. Bump the cache-buster in `login.ftl` and `401.ftl` to `?20261018`.
  - **Spec text, in the same change:**
    - design §4: add a note that a non-POST request is refused, with no state change;
    - design §5.1 and §5.2: add step 0;
    - design §11: add `bad_method`;
    - requirements: add a Decision Log entry for 2026-10-18 amending OPS-001's outcome sets, and add the key to UI-002;
    - runbook triage: add a row for `bad_method`.
- **Scope:** exactly the three findings. Statuses, limits, the neutral response, the mail and the UI flow all stay as T21 and `CHG-PWD-SWITCH-001` left them.
- **Out of scope:**
  - the other validation findings: V-3 and V-5 to V-11 are a separate documentation commit;
  - CSRF tokens on these endpoints (the P-9 residual stays accepted);
  - a Struts `httpMethod` interceptor for the whole package;
  - changing `domainNotEnabled` copy (it is never reached on password-off units, by design).
- **Tests (each seen red):**
  - `OtpRequestCodeActionTest`. Each of GET, HEAD, PUT and a null request answers exactly `{status: unavailable}`, with zero calls to the limiter, the managers, the session and the mailer, and one `bad_method` line. POST keeps today's path (an existing test stays green unchanged). An `Error` thrown by the eligibility predicate answers the neutral `sent` for an eligible-shaped request, writes `internal_error`, and does not propagate.
  - `OtpVerifyCodeActionTest`. GET with a correct pending code answers `{status: unavailable}`. The challenge stays unconsumed, attempts are unchanged, the session id is unchanged and the session still holds the pending challenge. A following POST with the same code signs in.
  - `OtpStrutsPackageTest` stays green, or is extended only if the action wiring changes.
  - The existing log-hygiene tests stay green. The `bad_method` line carries no code, nonce, MAC or full email.
- **Verification:**
  - clean compile gate;
  - Checkstyle delta 0 on the changed files;
  - targeted classes, then the full `mvn -o -pl marlo-web -am test`;
  - i18n gate (`''` rule);
  - cache-bust gate;
  - `node --check` on `login.js`;
  - a hygiene check.
  - No live stack is needed: the method check runs before any store or session access. The unit tests prove that through counting doubles.
- **Falsifier:**
  - remove the method check: the GET tests go red;
  - move the check after `process()`: the "zero limiter or session calls" assertions go red;
  - narrow the catch back to `RuntimeException`: the `Error` test goes red;
  - drop the `passwordEnabled` branch: a static check of `login.js` goes red, or a manual check is recorded.
- **Disqualifier:**
  - a GET that changes any state;
  - a distinct answer to GET that reveals the account state;
  - `ServletActionContext.getRequest()` dereferenced without a null check;
  - `getText` copy with a single apostrophe.
- **Review:** `full`. It changes the input surface of an authentication endpoint.
- **Verification notes (2026-10-18; the Leader sets the status):**
  - **Files:**
    - `OtpRequestCodeAction` and `OtpVerifyCodeAction`: step 0 in `execute()`. A null `this.request` (the `ServletRequestAware` field, filled by `servletConfig` in `defaultStack`) or a method other than POST is passed to `refuse()`, which only sets `{status: unavailable}`. The request action's two catches now take `RuntimeException | Error`.
    - `OtpLog`: `BAD_METHOD` in both enums.
    - `global.properties`: `login.otp.error.unavailablePasswordOff`.
    - `loginForm.ftl`: one copy span.
    - `login.js` `otpShowError`: the password-off mapping.
    - Cache-buster set to `?20261018` in `login.ftl` and `401.ftl`.
    - Spec text in design §4, §5.1, §5.2 and §11; requirements UI-002, OPS-001 and the Decision Log entry of 2026-10-18; a runbook triage row.
  - **Tests (red, then green):**
    - Request: every non-POST method (GET, HEAD, PUT, DELETE, OPTIONS, `""`, null) and a null servlet request answer `unavailable` with no limiter, store, user or realm event. Before the fix the answer was `{status=sent, destination=a***@partner.org}`.
    - Request: the `bad_method` line carries no secret.
    - Request: an `Error` from the eligibility predicate answers exactly as an ineligible account does (red: `NoClassDefFoundError` propagated). An `Error` that escapes the whole call is contained (red: `AssertionError: boom`).
    - Verify: GET with the correct pending code leaves the challenge unconsumed, 0 attempts counted, the same session id and the same pending object, and the following POST signs in. Red: `{status=ok, redirect=…}`.
    - `OtpLogTest` closed sets updated (red: `expected:<7> but was:<8>`).
    - Mutations: with the check moved after `process()`, the event-list assertions go red (`consume` ran). With the catches narrowed, both `Error` tests go red.
    - `login.js` falsifier: recorded manually. The real `otpShowError` was run under node with stubs: it returns `unavailable` at HEAD and `unavailablePasswordOff` now when `passwordEnabled === false`, and `unavailable` both times when it is true.
  - **Gates:**
    - The Implementer ran them in a scratch mirror, because test-compile failed in the checkout at that moment.
    - The Leader re-ran them in the real checkout as a non-author: clean recompile 2415 / 1066 / 89 BUILD SUCCESS; full `mvn -o -pl marlo-web -am test` 994 / 0 / 0 (986 before); Checkstyle delta 0 on all 6 changed Java files; i18n no FIX.
    - Implementer: hygiene ok, `node --check` ok, cache-bust ok. The other lines the cache-bust script flags in `loginForm.ftl` are comments that name the file.
  - **Deviations accepted by the Reviewer:**
    - `this.request` is used instead of `ServletActionContext.getRequest()`, so there is no NPE when no context is bound.
    - The old test `aRequestWithNoServletRequestAtAllIsCountedInTheUnknownBucket` was replaced by its refusal counterpart.
    - `FakeRequest` now has `getMethod`, defaulting to POST.
    - The extra OPS-001 and step-11 spec text keeps the closed sets consistent.

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
T14 ── T17 step-3 method choice (added 2026-10-14)
T09 + T10 ── T18 email_logs row for support (added 2026-10-14)
T09 + T18 ── T19 non-production recipient rule (added 2026-10-15)
T19 ── T20 branded email format (added 2026-10-15)
T20 ── T21 per-unit switch and domain exclusion (added 2026-10-15)
T21 ── T22 POST-only code endpoints, Error catch, password-off copy (added 2026-10-18; graph amended 2026-10-21, doc drift check against the code)
```

There are no cycles. **Parallel-safe groups** (disjoint files): {T01, T02, T08}; then {T03, T04, T05, T07, T09}; then {T06}; then {T10, T11, T13}. Execution stays sequential under one Leader; run in parallel only through separate worktrees.

---

## 5. Coverage — scenario and clause level

| Requirement clause | Owning task(s) |
|---|---|
| FN-001 scenario "Feature off": option hidden · direct call refused, no row or email · BUT password/Cognito step unchanged | T13 + T14 (hidden) · T10 (`domainNotEnabled`, no issue) · T14 + T15 AC-1 · T21 (switch) |
| FN-002 scenario "Live change", no restart | T03 (uncached read) · T16 (raw-SQL falsifier) · T21 (switch and exclusion on the uncached read) |
| FN-003 scenario: three lookalikes rejected · AND IT MUST accept trimmed upper case · exclusion semantics | T05 · T21 |
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
| FN-017 email content, no code link; branded shell per unit | T09 · T20 AC-20 |
| FN-018 delivered with notifications off; outside production only to the support mailbox, prefixed, fail closed (OQ-5 resolved) | T09 · T15 AC-15 · T19 AC-19 |
| FN-019 password and Cognito unchanged | T07 · T08 · T14 · T15 AC-1 |
| SEC-001 scenario "Code never persisted" (T22: GET no longer carries a code to access logs) | T03 · T09 · T15 AC-11 · T19 AC-11 · T22 |
| SEC-002 no clear email in the challenge store | T03 · T15 AC-11 |
| SEC-003 constant time, no throw | T05 |
| SEC-004 bound to session, email and unit; nothing client-side accepted as a session | T05 (`OtpPending`) · T11 · T12 (only `response`) |
| SEC-005 session rotation | T11 · T15 AC-5 |
| SEC-006 no new oracle | T10 · T11 · T13 · T15 AC-4 |
| SEC-007 no role or membership writes; identical authorization | T07 · T11 |
| SEC-008 scenario "Missing secret fails closed" · BUT app starts and other logins work | T02 · T10 · T13 · T15 AC-12 |
| SEC-009 no dependence on `is_cgiar_user`: the code switch and the exclusion decide; the Cognito read exists only on password-off units *(T05, T06 and T15 AC-13 originally owned the superseded "Cognito guard · `cgiar.org` ignored" rule)* | T21 · T15 AC-13 (re-run at T21) · `CHG-PWD-SWITCH-001` T03 |
| SEC-010 limits shared, counted before lookup, state-independent | T04 · T10 · T11 · T15 AC-14 |
| SEC-011 untrusted headers ignored | T05 |
| OPS-001 one request/verify line per call, one delivery line per email; the non-production `auth.otp.code` line; `bad_method` outcome | T05 · T09 · T10 · T11 · T15 AC-11 · T19 AC-11 · T22 |
| OPS-002 delivery failure logged, response neutral | T09 · T15 AC-15 |
| OPS-003 purge, failure non-blocking | T03 · T10 |
| OPS-004 runbook | T16 |
| OPS-005 `email_logs` row (full body since 2026-10-15; the code in no other column), not resendable | T18 AC-18 · T19 AC-18 |
| DA-001 migration safe and idempotent | T01 |
| DA-002 seed off, existing types 1–4, constants in both files | T01 · T02 · T21 (switch and exclusion keys replace the allow-list; AC-21) |
| DA-003 shared rate-limit store | T01 · T04 |
| UI-001 option only when enabled | T13 · T14 |
| UI-002 every state (T22: password-off copy for "unavailable") | T14 · T22 |
| UI-003 field attributes, focus, aria | T14 |
| UI-004 no double submit | T14 |
| UI-005 i18n | T09 · T14 · T20 (branded email) |
| UI-006 step-3 method choice | T17 · T21 |
| AC-21 the T21 migration applies twice, keys seeded off, allow-list gone | T21 |
| NF-001 bounded latency | T09 (async, timeouts) · T10 |
| NF-002 compile, Checkstyle, GPL header | every Java task, via `marlo-verify` |

*(Rows brought up to date 2026-10-18, validation V-10: T17–T22 and the T21 amendments.)*

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
| Set `otp.hmac.secret` (properties or `OTP_HMAC_SECRET`) and `otp.trusted.proxies` | Per environment, before the pilot |
| Enable a pilot unit through `custom_parameters` (admin UI) | After T16's rollout checklist |
| `docs/trd/trd.md` §8.1/§9.5 and `docs/infrastructure.md` §6 updates | Pending items for `/akili-archive` on `staging` |

## 8. Rollback Plan

1. Turn `system_otp_login_active` off for the unit in the admin UI. This applies on the next request (DD-12, DD-13). It does not switch the code off in a unit whose `system_password_login_active` is off; turn that back on first *(rewritten 2026-10-18, validation V-5; the original step said to empty the allow-list value, which T21 removed)*.
2. Blank `otp.hmac.secret` (and unset `OTP_HMAC_SECRET` if set) and restart every instance (the secret is read once at start). The feature becomes inert everywhere.
3. Revert the merge on `staging` and redeploy. The tables are harmless.
4. Drop `otp_challenges`, `otp_rate_limits` and `otp_cooldowns`. Only in-flight codes are lost.

Password and Cognito logins are unaffected at every level.

## 9. Definition of Done

- [ ] T01–T22 done, each with its verification notes.
- [ ] Every AC in `requirements.md` §7 recorded `PASS` in `probes/README.md`. A `NOT VERIFIED` is allowed only for an accepted gap named in requirements §6.
- [ ] Compile and Checkstyle gates green (zero new violations against HEAD); unit suite green with `-am`.
- [ ] Every UNVERIFIED premise either settled, or listed as a go-live blocker in `runbook.md`.
- [ ] In production no log line contains a code (outside production only the `auth.otp.code` line does), and in `email_logs` the code appears only in the body of its OPS-005 row (accepted 2026-10-15); evidence recorded.
- [ ] Merged to `staging` through the PR strategy below; A2-2631 status checked per `marlo-commit`.
- [ ] Shared-file pending items handed to `/akili-archive`.

## 10. PR Strategy

The estimate is about 2,300 LOC, so the work ships as three chained PRs into `staging`. The feature stays inert until a secret is set and a unit is enabled, so each PR merges safely on its own.

| PR | Tasks | Review first | Out of scope |
|---|---|---|---|
| 1 — Data and security core | T01–T09 | T03 consume/mismatch SQL, T07 realm branch | Actions, UI |
| 2 — Actions and routing | T10–T13 | T11 session sequence, T12 JSON root and parameter filter | UI |
| 3 — UI and verification | T14–T22 | T15 probe output, T14 walkthrough; then T21 (switch and exclusion) and T22 (POST only) | — |

*(Updated 2026-10-18, validation V-10: T17–T22 were added after this plan. T17–T22 are counted in PR 3, whose scope grew accordingly; if the branch is split differently at merge time, that choice supersedes this table.)*

Each PR description links its predecessor and successor, following the `cognitive-doc-design` review-empathy rules.
