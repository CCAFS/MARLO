# Validation Report — Center Email OTP Login

> **Re-validation 2026-10-18 (§13): PASS. The spec is archive-ready once it is merged to `staging`.**
> - T22 (`e3636559b2`) closed V-1, the FAIL, together with V-4 and V-2.
> - The documentation pass closed V-3 and V-5 to V-11.
> - The original verdict below is kept as the record of the first run.

**Original verdict (first run): FAIL, 1 finding. Not archive-ready until V-1 is fixed.** The fix is small.

| Finding | Result | What it is |
|---|---|---|
| V-1 | FAIL | The two OTP endpoints accept any HTTP method, but design §4 specifies POST. This is drift on a security-relevant contract |
| V-2 to V-11 | WARN | One user-facing copy problem and one reintroduced cache-lag residual, already recorded. The rest is documentation drift: stale allow-list text, stale traceability rows, paths to the archived spec, and a wrong figure in `test-report.md` |

Everything else passes:
- **Tasks:** all 21 are done, each with a Reviewer PASS.
- **Build gates, measured against `staging`:** clean compile, Checkstyle delta 0 on 42 files, hygiene, i18n and cache-bust.
- **Requirements:** 47 of 51 IDs PASS at clause level; the other 4 are WARN.
- **Tests:** 986 unit tests and the non-author live re-run are green. No product bug was recorded.

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | `CHG-OTP-LOGIN-001` |
| Spec path | `docs/specs/changes/center-email-otp-login` |
| Branch / HEAD | `A2-2631-Implement-Center-Email-OTP-Authentication` / `6560149089`, rebased on `staging` (`13cf130768`) |
| Date | 2026-10-18 |
| Auditor | Claude Code session, `opus` (T3). The Implementers were `sonnet`, so the auditor is not the author |
| Method | The Leader ran the build gates. Two read-only audit agents ran in parallel: one on requirement coverage at clause level, one on design conformance, figures, stale text, and quality and security. The Leader re-checked every FAIL and WARN it carries against the source |
| Requirements under test | As amended 2026-10-15 (T21) and 2026-10-17 (`CHG-PWD-SWITCH-001`, archived at `docs/specs/archive/2026-10-18-changes--password-login-switch/`) |

## 2. Summary

| Area | Result | Detail |
|---|---|---|
| Task completion | PASS | 21/21 `done [x]`, each with an `execution.md` entry and a Reviewer PASS |
| File existence | WARN | Every expected file exists and every expected deletion happened. Some changed files are not traced in this spec (V-9) |
| Build integrity | PASS | §5 |
| Requirement coverage | PASS with 4 WARN | SEC-003, SEC-008, UI-002, UI-005 (§6) |
| Code quality and security | **FAIL** (V-1) | Hard rules all hold. The other findings are advisory or WARN |
| Design conformance | PASS with WARN | DD-1 to DD-13 match. §4 "POST" does not (V-1). Several design sections still describe the allow-list (V-5) |
| Test evidence | PASS with WARN | One figure and one statement in `test-report.md` are wrong (V-8) |
| Constitution impact | PASS | No shared file was edited. Three TRD/infrastructure items are pending for archive (§10) |

## 3. Task Completion

| Tasks | Result | Evidence |
|---|---|---|
| T01–T21 | PASS | `tasks.md` shows `done [x]` for all 21. `execution.md` has an entry for each, with its verification and Reviewer verdict |
| Definition of Done (`tasks.md` §9) | Open, as expected before merge | The merge to `staging` and the archive hand-off come after validation. The AC evidence item is now met through `test-report.md` |

**Advisory:** the review-round tally in `execution.md` stops at "23 of 28" (T14). T15 to T21 then added about 15 more rounds, but no stop or extension of the 28-round tripwire was recorded. The header still reads "16 tasks". This is folded into V-10.

## 4. File Existence

| Kind | Items | Result |
|---|---|---|
| New files planned in design §2 and `tasks.md` | DAOs, managers, model, `.hbm.xml`, the `security/otp/*` classes, both actions, `OtpMailSender`, `ReturnUrls`, three migrations | PASS. All present |
| Expected deletions | `OTP_ALLOWED_EMAIL_DOMAINS`, `allowListFor` / `findAllowListValues`, `PLACEHOLDER_MESSAGE`, the inline `sameOriginOrNull` in `CognitoLoginAction` | PASS. All gone. The old key appears only in the two migrations |
| Changed files owned by the archived `CHG-PWD-SWITCH-001` | `CrpAdminManagmentAction`, `CrpPpaPartnersAction`, `CrpProgamRegionsAction`, `CrpSiteIntegrationAction`, `CrpUsersAction`, `ClusterActivitiesAction`, `ProjectPartnerAction`, `ValidateUserAction`, `LoginAction`, `SignInMethodRule`, `WelcomeSignInText`, `V2_6_0_20261016_1525__AddPasswordLoginSwitch.sql` | PASS (legitimate). This spec's design §2 does not list them (V-9) |
| Changed, named by no task | `marlo-web/src/main/resources/config/marlo-test.properties`, recorded only in the requirements Decision Log of 2026-10-16. Commit `89ac516f89` (the `customLogin.css` / `global.css` / `header.ftl` hover rule) has no `[SPEC:]` prefix and no `execution.md` entry | WARN (V-9) |
| Planned, replaced | Extending `CognitoUnloggedStackReachabilityTest` was replaced by `OtpStrutsPackageTest`. Recorded in execution T12 | PASS |

## 5. Build Integrity

Each gate was run by the Leader on HEAD `6560149089`. The Checkstyle, hygiene, i18n and cache-bust checks ran from a temporary worktree soft-reset to `staging`, so they measure the whole branch, not only the last commit.

| Gate | Command | Result |
|---|---|---|
| Clean recompile | `.claude/skills/marlo-verify/scripts/clean-compile.sh` | **PASS.** BUILD SUCCESS, exit 0. Compiled 2415 (marlo-data) / 1066 (marlo-web) / 89 (tests) |
| Checkstyle 8.18, delta against `staging` | `checkstyle.sh --baseline` on every changed product `.java` | **PASS.** Delta 0 on all 42 files |
| Java hygiene | `java-hygiene-check.sh` | **PASS.** The one `FIX LOG-ARGS` hit (`CognitoLogHygieneTest.java:440`) is inside a Javadoc comment, a false positive. The 12 `REVIEW LOG-NULL` items were traced by hand and are all safe (see below) |
| i18n | `i18n-check.sh` | **PASS.** No FIX findings on the 53 added `.properties` lines |
| Cache-bust | `cache-bust-check.sh`, then a manual check of each asset's last change | **PASS** (details below) |
| Unit tests | `mvn -o -pl marlo-web -am test` | PASS, from `test-report.md` §3: 986/0/0. Not re-run, because no code changed after it |
| Environment boot smoke | `scripts/run-marlo-java17.sh`, health check on 8080 | PASS, from `test-report.md` §5.1: booted and served on a throwaway copy |

**The 12 LOG-NULL items:**
- `LogSanitizer.sanitizeForLog` returns `""` for null.
- `failure`, `e` and `cause` are always a caught throwable.
- `unit` is filtered non-null with an id before `logLastResortOnce` (`OtpEligibility.java:371-375`), and it is guarded in `WelcomeSignInText.java:103`.
- `outgoing` is dereferenced earlier in the same method (`OtpMailSender.java:349`).

**Cache-bust:** the script marks references STALE when their date is not today. That is not the right test for a branch audit. The right test is whether each reference is at or after its asset's last change:
- `login.js` last changed 2026-10-17 and is referenced as `?20261017` in `login.ftl` and `401.ftl`.
- `global.css` and `customLogin.css` last changed 2026-10-15 and are referenced as `?20261015b` (`header.ftl`) and `?20261015` (the `@import` in `global.css`).
- The other `global.css` references without a parameter are older than this branch, in files it does not touch.

## 6. Requirement Coverage

Clause-level audit: every scenario and every `BUT` / `AND IT MUST` clause was checked for an owning task, code evidence (file:line), and test evidence. 26 cited tests were opened and read, and all assert the clause they are cited for. All 34 method names cited in `test-report.md` §7 exist.

| Group | IDs | Result |
|---|---|---|
| Functional | FN-001 – FN-019 | PASS (19) |
| Security | SEC-001, 002, 004 – 007, 009 – 011 | PASS (9) |
| Security | SEC-003, SEC-008 | **WARN** (V-6, V-7) |
| Operations | OPS-001 – OPS-005 | PASS (5) |
| Data | DA-001 – DA-003 | PASS (3) |
| UI | UI-001, UI-003, UI-004, UI-006 | PASS (4) |
| UI | UI-002, UI-005 | **WARN** (V-2) |
| Non-functional | NF-001, NF-002 | PASS (2) |

**Negative constraints, all PASS:**

| Constraint | Evidence |
|---|---|
| No enumeration oracle (FN-005, SEC-006) | Unit tests and live `enum` |
| No code in any log or column (SEC-001, SEC-002, AC-11, AC-18) | Unit tests and live `leak` / `emaillog` |
| No bypass of the exclusion (SEC-009, AC-13) | Unit tests and live `enum` |
| No reuse after a consume or exhaust (FN-012, FN-013) | Unit tests and live concurrency `{ok:1, invalid:9}` |
| No fallback key (SEC-008) | `OtpKeysTest` and live `nosecret` |
| Outside production, nothing delivered to the submitted address (FN-018) | Unit tests and live `nonprod` / `nonprodblank` |

**Weak spot (PASS by composition):** FN-015's "password turned back on since the request" case has no single test. It is proven only by combining `OtpEligibilityTest#ac8` (re-read on every call), `OtpVerifyCodeActionTest#anAccountThatLostEligibility…` (password on), and the archived password-switch AC-8 live run.

## 7. Linting & Code Quality

### 7.1 Hard rules

| Rule | Result |
|---|---|
| 3: Spring MVC owns `/api/*` | PASS. Only `.do` routes were added, in the `homeOtp` package |
| 4: specificities in both `APConstants` files, value equal to `parameters.key` | PASS. Both files agree on `system_otp_login_active`, `system_otp_excluded_email_domains` and `system_password_login_active`. Locked by `OtpSpecificityConstantParityTest` and `PasswordSwitchConstantParityTest` |
| 5: Flyway naming | PASS. All three migrations are named correctly and are newer than the latest on `staging` |
| 6: GPL header | PASS. Present on all new Java files |
| 7: style | PASS (§5) |
| 8: English only, i18n keys | PASS |
| Save path (`@Transactional` managers, no manager call in `validate()`, null-safe logs, no null collections) | PASS |

### 7.2 Security findings

| ID | Severity | Finding | Impact | Fix |
|---|---|---|---|---|
| **V-1** | **FAIL** (Medium) | `struts-home.xml:55-70` and `OtpRequestCodeAction` / `OtpVerifyCodeAction.execute()` accept GET (and every other method). Design §4 (`design.md:191-192`) specifies POST, and the module precedent `ValidateUserAction.java:94-101` rejects non-POST | A `?code=` or `?email=` sent by GET is written to Tomcat and proxy access logs, which goes against SEC-001's "the code never reaches any log". A cross-site GET (for example an `<img>`) can burn a victim's 3 attempts while they are signing in, if the browser sends the session cookie. It also makes the accepted login-CSRF residual (P-9) easier | Reject non-POST at the start of both `execute()` methods. Answer the neutral status (`unavailable` for request, `mismatch` for verify) with no state change and no limiter hit, and log one `auth.otp.*` line. Add one unit test per action that sees GET refused, and red-check it |
| V-3 | WARN (Low) | `CHG-PWD-SWITCH-001` put a **cached** `CognitoAuthSpecificity` read back into `OtpEligibility.eligibilityBasis:329-330`, for password-off units only. The runbook A7 row "Cognito cache lag" was removed at T21 and never re-added | After a super admin turns Cognito on for a password-off unit, other instances can treat a CGIAR account as non-Cognito for up to about an hour, so the override issues it a code | Restore the A7 row in `runbook.md`. That is consistent with PWD DD-4, which rejected an uncached read |
| V-4 | WARN (Low) | `OtpRequestCodeAction.isEligible` (`:336`) and `execute()` (`:261`) catch only `RuntimeException`. Verify was already fixed (`OtpVerifyCodeAction:282`). This is the open T10 advisory | An `Error` from the predicate becomes a 500, and only eligible-path accounts can reach it. That is a narrow oracle | `catch (RuntimeException \| Error e)`, as `handOff` does |

### 7.3 Advisory (4R; not spec violations)

**New in this audit:**
- **Readability:** `ReturnUrls.java:79, 89, 118` still log "Cognito login:" for OTP rejections (the T08 advisory). `OtpEligibility.otpEnabledFor` (`:490`) is now called only by tests, but its class Javadoc (`:89-90`) still presents it as the `crpByEmail.do` path.
- **Reliability:** `ReturnUrls` does not list `otprequestcode.do` / `otpverifycode.do` among the authentication endpoints.
- **Resilience:** `auth.otp.ratelimit outcome=store_failure|purge_failed` is missing from the runbook triage (`runbook.md:131` names only `auth.otp.fault`). Runbook B3 checks only the first migration in `flyway_schema_history`, not `…20261015_1418` and `…20261016_1525`.
- **Risk:** `reports/ai-context/struts-critical-routing-catalog.md` has no entry for the public pre-authentication `homeOtp` package. Root guide step 10 asks for one.

**Carried forward from `execution.md`, still open:**

| Task | Open advisories |
|---|---|
| T01 | P-23 `UNVERIFIED` |
| T02 | Comment cites the wrong DD |
| T03 | `hibernate.jdbc.time_zone` unset; no query space on `SPECIFICITY_SQL` |
| T04 | No short lock-wait timeout; a request refused by the cooldown still spends a window hit |
| T05 | Hops not canonicalised; IPv6 /64 rotation |
| T07 | No `assertSame` on the credentials |
| T09 | `sendAsync` Javadoc versus an `Error` thrown on the caller thread |
| T10 | `ClientIpResolver` built per request; `termsRequired` starts the cooldown |
| T11 | `freshSessionMap` duplicated with Cognito |
| T17 | `aria-describedby` / `role="group"`; unused `login.otp.button` key; a literal colour |
| T20 | Support address hard-coded in the shell |
| T21 | `set -e` lines in `ac21-migration.sh`; the `otp.staff@cgiar.org` fixture |

## 8. Design Conformance

| Decision | Result | Evidence |
|---|---|---|
| DD-1 `homeOtp` package | PASS | Extends `marlo-default,json-default`; `otpUnloggedStack`; `root=response`; `ParameterNameAware` allow-lists |
| DD-2 `OtpPending` in the Shiro session | PASS | `OtpRequestCodeAction.java:427`, `OtpVerifyCodeAction.java:293-297` |
| DD-3 conditional SQL | PASS | `CONSUME_SQL` (`consumed_at IS NULL AND attempts < 3 AND expires_at > :now`); conditional increment; cooldown row lock after the T04 pivot |
| DD-4 active membership | PASS | `existActiveCrpUser`; PWD amendment in place |
| DD-5 decoy rows | PASS | `issue()` runs for every request past steps 1–5; only an eligible account's code is mailed |
| DD-6 supersede through `consumed_at` | PASS | `SUPERSEDE_SQL` |
| DD-7 mailer | PASS / WARN | Pool, queue, abort policy, timeouts, non-production rule, `email_logs` row and shell all match. Design §5.3 does not list the `auth.otp.mail` lines or the 30 000 ms clamp; the runbook does (V-5) |
| DD-8 limits | PASS | 5 / 20 / 10 / 50 per 15 min, plus a 30 s cooldown |
| DD-9 client IP | PASS | `X-Forwarded-For` only from a trusted peer, walked right to left, with no DNS lookup |
| DD-10 secret | PASS | At least 32 decoded bytes; the value is never logged |
| DD-11 `ReturnUrls` | PASS | `CognitoLoginAction` delegates to it |
| DD-12 uncached read | PASS | Native `SPECIFICITY_SQL` with no `setCacheable` |
| DD-13 switch plus exclusion | PASS | `OtpDomainPolicy.isOffered` |
| §4 routes "(POST)" | **FAIL** | V-1 |

**Cross-document figures:** the TTL of 5 min, 3 attempts, the 30 s cooldown, the request and verify limits, the 32-byte minimum, the 10 000 ms timeout, the three migration names and the three keys agree across requirements, design, tasks, runbook, the probes README and the code. Two mismatches remain: the `test-report.md` total (V-8) and the `execution.md` header and round tally (V-10).

**Alignment with the proposal:** intent, scope and non-goals still hold as amended. The proposal's allow-list wording is historical, and the requirements Decision Log records its replacement.

### 8.1 Documentation drift (WARN)

| ID | Finding | Fix |
|---|---|---|
| **V-2** | UI-002 / UI-005 copy was not updated for the password switch. `global.properties:6687` `login.otp.error.unavailable=…Please try again later or use your password.` is shown by `login.js:1347, 1382, 1419, 1459` on every unit, including password-off units where the password is refused. `:6688` `domainNotEnabled` ("Please use your password.") is never reached there, by design | Add a password-off variant chosen from `passwordEnabled`, for example "…Please try again later or sign in with CGIAR." Cache-bust `login.js` |
| **V-5** | Live text that still describes the allow-list or the CGIAR refusal, with no supersession marker. See the table below | Rewrite or mark each one, then run the correction-closure sweep |
| **V-6** | SEC-003 (constant time) is accepted only in `test-report.md` §9. It is not in requirements §6 | Add a §6 row: proven by inspection of `OtpCodes.java:118` (`MessageDigest.isEqual`) |
| **V-7** | SEC-008's scenario reads "the OTP secret is not set in the environment". The Decision Log of 2026-10-16 also allows `otp.hmac.secret` in the properties file, so the scenario as worded is not the fail-closed condition (`test-report.md` §5.3 attempt 1) | Amend it to "absent or invalid in both `OTP_HMAC_SECRET` and `otp.hmac.secret`" |
| **V-8** | `test-report.md` has three errors. (1) Its total of "178 checks" is wrong: the `akili-test-*.out` SUMMARY lines add up to **139**, plus 17 from `ac21`. (2) §6 says "None of the UI files has changed since those walkthroughs", but `33fa702aad` (2026-10-17) changed `login.js` and `loginForm.ftl` after T17 and T21; the archived password-switch `t06-walkthrough.md` re-walked them. (3) §1 says HEAD plus 1 uncommitted file; it is now `6560149089` | Correct the three statements |
| **V-9** | Traceability outside the task list: `marlo-test.properties` changed with no owning task; commit `89ac516f89` has no `[SPEC:]` prefix and no execution entry; design §2 lists none of the `CHG-PWD-SWITCH-001` files or `UnitTextProvider` | Add one `execution.md` note naming these, with their owner |
| **V-10** | `tasks.md` §5 coverage rows are stale: SEC-009 "Cognito guard · `cgiar.org` ignored", FN-001 / DA-002 without T21, and no rows for UI-006, AC-21 or T17–T21. §10 PR strategy stops at T16. The `execution.md` and design headers say "16 tasks". `tasks.md:774` cites the static lock `theCodePathNeverReadsIsCgiarUserOrTheCognitoSpecificity`, which password-switch T03 removed | Update the rows and headers; cite the counting-double tests instead |
| **V-11** | Dangling paths to `docs/specs/changes/password-login-switch/`, which is now archived: `requirements.md:17, 462, 467, 469, 470`, `runbook.md:14` (two references), `probes/README.md:318` | Repoint all of them to `docs/specs/archive/2026-10-18-changes--password-login-switch/` |

**V-5 sites (live text that is now false):**

| Location | Replacement |
|---|---|
| `tasks.md:887` §8 Rollback step 1, "Empty the Global Unit's `custom_parameters` value" | "Turn `system_otp_login_active` off for the unit in the admin UI. This applies on the next request (DD-12, DD-13). It does not switch the code off in a unit whose `system_password_login_active` is off." |
| `design.md:409-410` §14 rollback, "empty the value" | "Turn `system_otp_login_active` off. This applies on the next request, on every instance, but not while the unit's password switch is off." |
| `design.md:406-407` §14 pilot, "one Global Unit and one domain… expand domain by domain" | Pilot on one unit with the exclusion set to `cgiar.org` and the switch on; expand unit by unit; stage 2 empties the exclusion (runbook B5) |
| `design.md:352` §9, "the rollback 'empty the list'" | "…the rollback 'switch off'…" |
| `design.md:75, 92` "Adds `OTP_ALLOWED_EMAIL_DOMAINS`" | List `OTP_LOGIN_ACTIVE`, `OTP_EXCLUDED_EMAIL_DOMAINS` (T21) and `PASSWORD_LOGIN_ACTIVE` (`CHG-PWD-SWITCH-001`) |
| `design.md:162-166` §3, catalog seed `crp_otp_allowed_email_domains` | Append a note that T21 superseded it |
| `design.md:361` §10, "Cognito: `CognitoAuthSpecificity` for SEC-009" | "Read only on password-off units (`CHG-PWD-SWITCH-001` DD-4)" |
| `tasks.md:81, 122, 138, 183, 216-219, 315-317, 405-407` (T02, T03, T05, T06, T10, T13 scope) | Add "*(Superseded by T21, 2026-10-15)*", the convention T09 already uses |
| `probes/README.md:92` AC-12 "with the allow-list set" | Mark it as a pre-T21 run and cite `akili-test-nosecret.out` 6/0 |
| `runbook.md:364` Part D, base TRD §8.1 text | Rewrite it as a single current text before `/akili-archive` applies it |

**Backward direction:**
- `runbook.md` A1 and B5 cite design §14.
- `test-report.md` §8 cites `tasks.md` §8.
- The password-switch closure sweep (`requirements.md:467`) missed `tasks.md` §5, §8 and §10, and design §2, §3, §9 and §14.

## 9. Test Evidence Summary

| Suite | Result | Source |
|---|---|---|
| Backend unit | PASS. 986/0/0, 3 new tests seen red | `test-report.md` §3 |
| Integration, live non-author re-run | PASS. 139 checks plus `ac21` 17/0 plus the `ac17` shapes, 0 failed in the recorded runs | `test-report.md` §5; `probes/output/akili-test-*.out` |
| E2E | PASS as recorded: T17, T21 and the archived password-switch T06 walkthroughs | `test-report.md` §6 (corrected by V-8) |
| `PRODUCT_BUG` | None | — |

Accepted gaps carried from `test-report.md` §9:
- multiple instances (AC-14);
- screen-reader behaviour and the reCAPTCHA overlap (AC-16);
- deliverability;
- no JavaScript test runner.

SEC-003 and the literal "variable absent" case of AC-12 are tracked by V-6 and V-7. **No test covers V-1**, and the fix must add one.

## 10. Agent Guide / Constitution Impact

- **No shared file was edited on this branch.** `git diff staging..HEAD` touches no `CLAUDE.md`, `AGENTS.md`, `.agents/`, `docs/trd/trd.md`, `docs/infrastructure.md` or `reports/` file. The only file outside the two spec folders is `docs/specs/kaizen/changes--password-login-switch.md`, an allowed kaizen record. PASS.
- **Pending items for `/akili-archive` on `staging`** (`runbook.md` Part D):
  1. `docs/trd/trd.md` §8.1: the OTP route. Write it from the current rule, not the stale base text (V-5).
  2. `docs/trd/trd.md` §9.5: the OTP rate limits.
  3. `docs/infrastructure.md` §6: `otp.hmac.secret` / `OTP_HMAC_SECRET`, `OTP_TRUSTED_PROXIES`, `otp.smtp.timeout.ms`, the local SMTP sink and the `MYSQL_DATABASE` / `EMAIL_*` overrides.
  4. Suggested: a `homeOtp` entry in `reports/ai-context/struts-critical-routing-catalog.md`.
- **No module guide applies** (none exists).

## 11. Remediation

| # | Finding | Kind | Priority |
|---|---|---|---|
| 1 | V-1: require POST on both OTP actions, with one unit test each seen red | Code | **Required before archive** |
| 2 | V-2: password-off variant of `login.otp.error.unavailable`, and a cache-bust of `login.js` | Code + i18n | Recommended |
| 3 | V-4: catch `Error` in `OtpRequestCodeAction.isEligible` / `execute()` | Code | Recommended |
| 4 | V-3: restore the runbook A7 "Cognito cache lag" row | Docs | Recommended |
| 5 | V-5, V-10, V-11: stale text, coverage rows, headers and archived-spec paths, plus the correction-closure sweep | Docs | Recommended before archive |
| 6 | V-6, V-7: requirements §6 row for SEC-003; reword the SEC-008 scenario | Docs | Recommended |
| 7 | V-8: correct `test-report.md` (139 + 17, the §6 UI claim, HEAD) | Docs | Recommended |
| 8 | V-9: `execution.md` note on files traced outside the task list | Docs | Low |
| 9 | Advisories in §7.3 | Follow-up | Optional |

## 12. Archive Readiness Recommendation

**Not ready.** V-1 is a FAIL on a security-relevant design contract.

Recommended route:
1. Add a small task T22, "require POST on the OTP endpoints". Run it through the normal Implementer → Reviewer flow and fold in V-4 and V-2.
2. Apply the documentation corrections (V-3, V-5 to V-11), with the correction-closure sweep.
3. Re-run `/akili-validate changes/center-email-otp-login`. Only §5, §7.2 and §8 need re-checking.
4. Then run `/akili-archive changes/center-email-otp-login`, after the merge to `staging` and with the three pending items of §10.

## 13. Re-validation (2026-10-18)

**Verdict: PASS.** Ready to archive after the merge to `staging`.

Scope: as §12 planned, only §5 (build), §7.2 (security findings) and §8 (design conformance and documentation drift) were re-checked. Nothing else changed.

### 13.1 Findings

| Finding | Closed by | Evidence |
|---|---|---|
| **V-1** (FAIL) | T22, `e3636559b2` | Both actions answer `{status: unavailable}` to a non-POST request, or to a call with no servlet request, before any state is touched. They log `outcome=bad_method`. Tests cover GET, HEAD, PUT, DELETE, OPTIONS, `""` and null, each seen red. The verify test with a GET leaves the challenge unconsumed, attempts unchanged and the session unchanged, and the following POST signs in. Reviewer PASS. `this.request` is set in production, because `otpUnloggedStack` includes `defaultStack`, which runs `servletConfig` |
| V-4 | T22 | The request action's two catches take `RuntimeException \| Error`. Both narrowing mutations go red |
| V-2 | T22 | `login.otp.error.unavailablePasswordOff` is used by `otpShowError` when `passwordEnabled === false`. Checked by a recorded node falsifier. `login.js?20261018` |
| V-3 | Documentation pass | Runbook A7 "Cognito cache lag" row restored |
| V-5 | Documentation pass | Every site in the V-5 table is rewritten or marked "Superseded by T21". The rollback in `tasks.md` §8 and `design.md` §14 now turns `system_otp_login_active` off and names the password-off limit. The runbook Part D text for TRD §8.1 is one current text |
| V-6, V-7 | Documentation pass | Requirements §6 has a SEC-003 row (`MessageDigest.isEqual` at `OtpCodes.java:118`, confirmed). The SEC-008 scenario reads "absent or invalid in both `OTP_HMAC_SECRET` and `otp.hmac.secret`", with a Decision Log entry |
| V-8 | Documentation pass | `test-report.md` now gives 139 plus `ac21` 17/0, cites the `33fa702aad` re-walk and the T22 node check, and names HEAD `6560149089` |
| V-9, V-10 | Documentation pass | `execution.md` Leader note on files traced outside the task list. Design §2 now lists those files. `tasks.md` §5 coverage rows are updated and rows added for UI-006, AC-21 and T22. The removed static lock is replaced by the counting-double tests. Headers updated. The review-round tripwire overrun (about 38 rounds against a budget of 28) is recorded honestly |
| V-11 | Documentation pass | No reference to `docs/specs/changes/password-login-switch/` remains in the spec folder, apart from this report's finding text and raw probe outputs |
| T22 Reviewer advisories | Documentation pass | Design §5.1 step 1 and the `OtpRequestCodeAction` Javadoc now read "first after step 0" (a comment-only Java change) |

### 13.2 Gates on the final tree

- **Clean recompile:** 2415 / 1066 / 89, BUILD SUCCESS. Run twice: after T22, and after the documentation pass.
- **Full test suite:** `mvn -o -pl marlo-web -am test` gives 994 / 0 / 0. This was run after T22; the documentation pass changed only a comment.
- **Checkstyle:** delta 0 on every changed Java file.
- **i18n:** no FIX findings.

### 13.3 Correction closure

The superseded values were grepped across the spec folder: `crp_otp_allowed_email_domains`, `OTP_ALLOWED_EMAIL_DOMAINS`, the "empty the list" wording, the `cgiar.org` refusal wording, `changes/password-login-switch` and `178`. Every remaining hit is one of four kinds:
- historical text, marked as superseded or amended;
- current T21 text;
- an append-only execution entry, now covered by the Leader note;
- raw probe output.

`proposal.md` and `judgment.md` are historical and are not edited.

### 13.4 Still open

These do not block the archive:
- **Advisories:** the §7.3 advisories, plus the T22 note that the `i18nFile` interceptor runs before step 0.
- **Accepted gaps:** the ones listed in `test-report.md` §9.
- **Pending for `/akili-archive` on `staging`:** the three items of §10. Copy TRD §8.1 from the rewritten runbook Part D text.

**Next:** merge to `staging` (Definition of Done item, A2-2631 status per `marlo-commit`), then `/akili-archive changes/center-email-otp-login`.
