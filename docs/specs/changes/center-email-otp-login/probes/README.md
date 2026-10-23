# CHG-OTP-LOGIN-001 -- T15 probes and recorded output (attempt 2)

Attempt 2 ran on 2026-10-13, branch `A2-2631-Implement-Center-Email-OTP-Authentication`, HEAD `ece163ef2e` (includes the
T06 fix: `OtpEligibility` requires an **active** membership). No production code changed: `git diff --stat` was empty before,
during and after (the falsifier ran against a scratch copy of the DAO through `PROBE_DAO`, so `src` was never edited).
Attempt 1 ran on `d743181e58`; the one output file kept from it unchanged is `output/concurrency-probe-http.FALSIFIER-red.out`
(section 4).

> **Amended 2026-10-17 (`CHG-PWD-SWITCH-001`).** Every verdict and run below was taken with the password switch `system_password_login_active` absent or on, the only state that existed. Where a verdict says the code option is hidden, the password is accepted or `domainNotEnabled` is answered, it describes a unit with that switch on. The re-run of the stages with the switch on everywhere, the ac21 re-run after the DA-002 reword, and the switch-off evidence are in section 12.

## 1. What is here

| File | What it is |
|---|---|
| `concurrency-probe.sh` | HTTP-level concurrency probe against the running app (design section 12, four cases plus a deadlock hunt). **Case 4 runs first**: the limiter purge is throttled (section 4) |
| `concurrency-probe-sql.sh` | Statement-level probe. Reads the real conditional statements from `OtpChallengeMySQLDAO.java` and fires them from parallel MySQL sessions released by a `GET_LOCK` barrier. Prints the isolation level (section 6) |
| `ac-probe.sh` | Acceptance probe, stage by stage (enumeration, attempts, resend, expiry, removal, limits, session parity, leak check, `email_logs` row (`emaillog`, added by T18), non-production recipient (`nonprod` and `nonprodblank`, added by T19), live change, no or short secret, SMTP, black-hole SMTP, time zone, post-login failure) |
| `ac17-migration.sh` | The T01 migration applied twice on three database shapes (AC-17); records the invocation and the database names |
| `ac21-migration.sh` | The T21 migration (switch and exclusion, old allow-list removed) applied twice on a throwaway copy that holds a value on the old key, on a database with no Global Unit 45 or 47, and on one with no Global Unit types (AC-21). Reads the two DA-002 descriptions from `requirements.md` and fails if the table text differs; prints PASS or FAIL per check |
| `tools/SessDump.java` | Reads every Shiro session of a JVM started with a JDWP agent, over JDI, without invoking any method in it. Used for AC-5 and for the nonce of AC-11. GPL header, braces on every block; `output/sessdump-javac.out` is its `javac` |
| `output/` | The recorded output of every run quoted below. Local paths are shown as `<logs>/...` |

All scripts refuse to run unless the connected database name contains `probe`. The two that drive the application
(`concurrency-probe.sh`, `ac-probe.sh`) also abort when the first answered request leaves **no challenge row in the probe copy**
(`(guard: ...)` lines in the output): the first guard only sees the database the probe itself opens, not the one the app uses.

## 2. Setup used (so the run can be repeated)

- **Throwaway database.** `aiccradb_otp_probe`: `mysqldump --single-transaction --routines --triggers` of `aiccradb_actsave`
  loaded into a fresh database (619 tables each; same `users` and `custom_parameters` counts). `aiccradb_actsave` and
  `aiccradb1`-`4` were never written to.
- **App pointed at the copy with the environment only**: `MYSQL_DATABASE=aiccradb_otp_probe` (the URL in
  `marlo-dev.properties` interpolates `${mysql.database}`, and the environment beats the `@PropertySource` file). No
  tracked file was edited. **Proof before any write:** `ps eww` of the JVM listening on 8080 showed
  `MYSQL_DATABASE=aiccradb_otp_probe`, and a password login of user 3862 moved `users.last_login` from `12:56:27` to
  `14:50:03` in the copy while `aiccradb_actsave` still read `12:56:27`.
- **Secret** `OTP_HMAC_SECRET=$(openssl rand -base64 32)`, environment only, a new value per start.
- **SMTP** `EMAIL_HOST/EMAIL_PORT(+HOSTBACKUP/PORTBACKUP)=127.0.0.1:2525`, a local sink (nothing relays). **Since T19 a local run is
  non-production (`marlo.production=false`), so every code email reaches the sink addressed to the support mailbox
  (`email.notification`), whoever asked for it, with `(TESTING ENVIRONMENT) ` in the subject and `To: <submitted address>` at the top
  of both bodies. The probe therefore finds a user's code by that body header, never by the envelope or the To header, and takes the
  mailbox it expects from `PROBE_SUPPORT`.** The T19 run set `EMAIL_NOTIFICATION=support.probe@sink.test` in the environment, so the
  recorded output names a fixture mailbox, not the one in `marlo-dev.properties`. "SMTP down" is the
  sink stopped; the "black hole" is a listener that accepts the connection and never answers (`ac-probe.sh blackhole`).
- **Fixtures, in the copy only:**
  - **since T21**, `ac-probe.sh` sets the standing fixture itself at the start of every run: for the unit `PROBE_GU` (AICCRA, 45),
    `system_otp_login_active = true` and `system_otp_excluded_email_domains = excluded.test` (`custom_parameters` rows, the way the
    Super Admin Specificities tab writes them). Before T21 the fixture was a `custom_parameters` row on parameter 397
    (`crp_otp_allowed_email_domains`, value `outlook.com`); that key no longer exists. Running `ac-probe.sh` with no stage only
    sets the fixture, which the concurrency probes also need;
  - users `otp.inactive@` (`is_active=0`), `otp.nonmember@`, `otp.cgiar@` (`is_cgiar_user=1`, member of 45), **`otp.staff@cgiar.org`
    (`is_cgiar_user=1`, member of 45; added for T21)**,
    **`otp.softdeleted@` (member of 45 with `crp_users.is_active=0`, the state `CrpUserMySQLDAO.deleteCrpUser` leaves)**, and a
    pool `otp.p01..p30@outlook.com` (active members of 45);
  - user 3862, shown here as `probe.user@outlook.com` (active, non-CGIAR, member of 45) with a test password set in the copy
    (`UPDATE users SET password = MD5(...)`), passed to the probe as `PROBE_PW_PASS`. **No real address appears anywhere in this
    folder**;
  - for the post-login case: `global_unit_types` 99, unit 9901 `PROBEGU` with its own `parameters` row for type 99
    (`system_otp_login_active`, default `true`, since T21; before it, the allow-list `outlook.com`), member `otp.p20@`. (The migration seeds types 1-4 only, so the type 99 `parameters` row in
    `ac17-migration.out` section (a) is this fixture, not the migration.)
- **Log level.** `logback.xml` with `log.test=true`, `log.console=true`: the root level is `ALL` (`DEBUG` lines are present),
  `org.hibernate`/`org.apache.shiro`/`org.apache.struts2` are `WARN`. Two sinks were searched: the console log of the run and
  `marlo-dev.log` (`<logs>/marlo-dev.log`). `auth.otp.*` lines are `INFO`/`WARN`, so nothing that concerns the OTP lines was
  suppressed. **Limit:** Hibernate bind-value logging is off, so a leak through SQL parameter logging is covered by the database
  search, not by the log search; the code is never bound as a SQL parameter, only its HMAC.
- **Build.** `scripts/run-marlo-java17.sh` once (clean build: 2415 / 1062 / 71 files in marlo-data / marlo-web / test-classes,
  `BUILD SUCCESS`); restarts of the same build with `mvn -o -pl marlo-web cargo:run` for the 16-byte secret, the no-secret and
  the Tokyo passes. The JDWP agent (`JAVA_TOOL_OPTIONS`) is set on every start for the `session` and `leak` stages.
- **One run was discarded**: a first full `ac-probe.sh` run was started twice by mistake and the first process stayed alive,
  resetting the OTP tables under the second (one AC-4 "resend" check failed). Nothing else ran; the clean run is the recorded one
  and the failing stage passes alone (a harness error, not an app defect).
- **JVM time zone.** The machine is `-05`. A second pass ran with `-Duser.timezone=Asia/Tokyo`.

## 3. Verdict per acceptance criterion (requirements section 7)

`PASS` = evidence recorded, `NOT VERIFIED` = the reason is given. The main run is `ac-probe.main-run.out`
(`SUMMARY: 59 checks, 0 failed`).

| AC | Verdict | Evidence |
|---|---|---|
| AC-1 | **PASS** (re-run 2026-10-15, T21; the first run's environment note still applies) | The switch turned off through the database with no restart (stage `live`, `output/ac-probe.t21-live.out`): `request-code` answers `domainNotEnabled`, no mail, no challenge row; `crpByEmail` returns `otpEnabled:false` for every unit; password login still lands on `/AICCRA/crpDashboard.do`. The existing login tests pass unchanged in the full suite (`Tests run: 832, Failures: 0, Errors: 0`, `mvn -o -pl marlo-web -am test`), among them `LoginActionFinishLoginTest`, `CognitoLoginActionTest`, `CognitoCallbackActionTest`, `LoginActionCgiarGuardTest`, `CrpByUserEmailActionTest`. **Environment note:** the live Cognito hand-off was not exercised (the walkthrough stopped before the redirect to the identity provider, `output/t21-walkthrough.out`, 2a). History: the T15 run emptied the old allow-list instead (`output/ac-probe.main-run.out`) |
| AC-2 | **PASS** (re-run 2026-10-15, T21, amended) | `OtpDomainPolicyTest` is green in the suite (34 tests; the lookalike, normalisation and `cgiar.org`-is-an-ordinary-entry cases). End to end, with the exclusion `excluded.test` (stage `enum`, `output/ac-probe.t21-enum.out`): `x@excluded.test` and `  Y@EXCLUDED.TEST ` answer `domainNotEnabled`; `x@attackerexcluded.test`, `x@excluded.test.evil.com`, `x@sub.excluded.test` and `someone@gmail.com` are not excluded and get the neutral `sent` (unknown accounts: no mail, one row each); `"  Probe.User@OUTLOOK.COM "` is accepted as the normalised address and mailed |
| AC-3 | **PASS** (re-run 2026-10-15, T19, amended for the non-production rule) | One row per request; the mail has the application name, a 6-digit code, the 5-minute expiry, "only once", "do not share", "ignore if not requested", `MARLOSupport@cgiar.org`, no link, and exactly one recipient, which outside production is the support mailbox, never the typed address (it is named in the body header) (stage `enum`, `output/ac-probe.t19-enum.out`). In production the one recipient is the typed address (`OtpMailSenderTest`, not run live: `marlo.production` stays false in this environment) |
| AC-4 | **PASS** (re-run 2026-10-15, T21; **five** account states, the CGIAR one is no longer refused) | Stage `enum`, `output/ac-probe.t21-enum.out`: eligible, unknown, inactive, non-member and **soft-deleted membership (`crp_users.is_active=0`)**. The answer, the other headers and the cookie names are identical (only the masked `destination` echoes the typed address); no mail for the four ineligible states; five rows of one shape (no column marks a decoy); the sequence wrong, wrong, resend inside 30 s, wrong, correct-or-random answers `mismatch, mismatch, rateLimited, exhausted, exhausted` for all five; after a real 31 s wait the resend answers `sent` then a wrong code `mismatch` for all five, and still only the eligible account is mailed. "No recorded challenge can authenticate" is the T11 unit test `OtpVerifyCodeActionTest`, search `aDecoyGuessed`. History: the 2026-10-15 T19 run had a sixth state, `is_cgiar_user=1` on a Cognito-active unit, answered neutrally and not mailed (`output/ac-probe.t19-enum.out`); T21 reverses that rule (AC-13). **Residual, as designed (RJ-10): the timing of an eligible request was not measured** |
| AC-5 | **PASS** | Password login and code login for user 3862 on AICCRA: 104 session attributes each, the same names and values (the per-session CSP `nonce` and the random avatar `color` that `LoginAction` picks on every login excluded), including every `custom_parameters` key; the session ID after the code login differs from the pre-auth one; the pre-auth session is not authenticated, the new one is (`authed()` reads the principal from the JVM, because `crpDashboard.do` answers HTTP 200 to anonymous callers too); same landing; `user_roles` and `crp_users` checksum unchanged; `last_login` updated (stage `session`) |
| AC-6 | **PASS** | `mismatch, mismatch, exhausted`, then the correct code answers `exhausted` (stage `attempts`; also case 2 of the HTTP probe) |
| AC-7 | **PASS** | HTTP: 10 parallel correct verifies on one challenge, all started before the first finished (start spread 0.4 ms; 0.5 ms on the Tokyo pass): `{ok: 1, invalid: 9}`, one consumed row. Statement level: 10 parallel `CONSUME_SQL`, all 10 sessions blocked on the barrier when released: exactly one session reports 1 affected row (`concurrency-probe-http.green.out`, `concurrency-probe-http.tz-Tokyo.out`, `concurrency-probe-sql.green.out`) |
| AC-8 | **PASS** | Real clock: a challenge verified 301.1 s after issue answers `invalid` (stage `expiry`, `ac-probe.main-run.out`). Row aged in the copy to 5 min 1 s: `invalid`; 25 s before expiry: `ok`. Superseded (another session requested after 31 s): the old code answers `invalid` though its row exists, the new one `ok` (stage `resend`) |
| AC-9 | **PASS** under the removal MARLO performs (soft delete) | Stage `removed` of `ac-probe.main-run.out`, with the T06 fix (`existActiveCrpUser`). The removal is `UPDATE crp_users SET is_active = 0` for the member's row on the unit, which is what `CrpUserMySQLDAO.deleteCrpUser` writes. **(1)** A challenge issued while the member was active, then soft-deleted before the verify: the correct code answers `invalid`, the challenge row is consumed, and the pre-auth session is not authenticated (read from the JVM). **(2)** A code request for the soft-deleted member answers the neutral `{"status":"sent","destination":"o***@outlook.com"}`, the same as for an active member, and 0 mails are delivered; the same state is the sixth row of the AC-4 enumeration (identical response, no mail). **(3) Supporting evidence, not the removal MARLO performs:** deleting the `crp_users` row outright gives `invalid`, consumed, no authenticated session. **(4) Control:** the same member, active again, signs in with a new code (`ok`), so the gate does not refuse everybody. This is a deliberate departure from password-login parity (decided after the attempt 1 FAIL): the code login is stricter, the password login (`finishLogin`, `ValidSessionCrpInterceptor` and the center `ValidSessionInterceptor` use `existCrpUser`; `ValidPhaseInterceptor` already uses `existActiveCrpUser`) is unchanged and tracked separately |
| AC-10 | **PASS** | Resend inside 30 s: `rateLimited`, row count and open rows unchanged. After a real 31 s: same-session resend `sent`, the old code `mismatch`, the new one `ok` (stage `resend`) |
| AC-11 | **PASS** (re-run 2026-10-15 for T19, amended AC-11: the code in `email_logs.message` and, outside production, in one log line) | Full flow request, wrong, correct (stage `leak`, `output/ac-probe.t19-nonprod-leak-emaillog.out`): `otp_challenges` has no code, no full email, no local part (the nonce is stored by design: it is the lookup key). `email_logs`: exactly one new row, `source_action = /otpRequestCode`; **the code is in the `message` of that row and in no other column**; the full email and the local part are in no column but `to_email`, which holds the recipient (OPS-005). Both logs (the console log and `marlo-dev.log`) searched for the code, the nonce (read from the session over JDI), the full email, the pre-auth and the authenticated session IDs: the nonce and both session IDs have 0 hits; **the code and the full email are only in the one `auth.otp.code gu=AICCRA to=<address> code=<code>` INFO line of the run (outside production, OPS-001), exactly one in each log, and that line's code is the one in the email**. The run's `auth.otp.*` lines are exactly one `request`, two `verify` (wrong, ok), one `delivery` and one `code`. History: the 2026-10-14 run (T18) recorded the code in no column and no log line; the 2026-10-13 run recorded 0 `email_logs` rows. **Not run live: the production half** (no `auth.otp.code` line, no code or address in any line), because `marlo.production` stays false here; `OtpMailSenderTest` covers it, with a falsifier that writes the line in production and turns the test red |
| AC-12 | **PASS** | Two starts, one file each. **No secret** (`ac-probe.nosecret.out`): `login.do` HTTP 200, `request-code` answers `domainNotEnabled` *(this row's "with the allow-list set" is the pre-T21 run, `ac-probe.nosecret.out`; the allow-list no longer exists. The post-T21 re-run is `akili-test-nosecret.out`: 6 checks, 0 failed)*, no challenge, no mail, `otpEnabled:false`, `verify-code` answers `unavailable`, password login works; the startup log line is `[WARN ] ... auth.otp.keys outcome=unconfigured: the one-time-code secret is not set; code sign-in is unavailable.`; `ps eww` of the JVM owning port 8080: `OTP_HMAC_SECRET absent`. **16-byte secret** (`ac-probe.shortsecret.out`): the same answers; the startup line is `[WARN ] ... auth.otp.keys outcome=unconfigured: the one-time-code secret decodes to fewer than 32 bytes; code sign-in is unavailable.`; `ps eww`: `OTP_HMAC_SECRET present, value redacted (24 characters)`; the probe checks that the secret value is not in the WARN line (`secret value in them: False`) |
| AC-13 | **PASS** (re-run 2026-10-15, T21, amended) | Stage `enum`, `output/ac-probe.t21-enum.out`, on AICCRA (`cognito_auth_active = true`, switch on). With the exclusion `excluded.test`: `crpByEmail` for `otp.staff@cgiar.org` (`is_cgiar_user=1`) answers `isCgiarUser true`, `cognitoEnabled true`, `otpEnabled true` (the two methods are offered); both CGIAR accounts (`otp.cgiar@outlook.com`, `otp.staff@cgiar.org`) are mailed exactly one code each and the code signs each in (`ok`, authenticated sessions read from the JVM). With `cgiar.org` excluded: a direct `request-code` call answers `domainNotEnabled`, no mail, no challenge row; `crpByEmail` keeps `cognitoEnabled true` and turns `otpEnabled false` for that account, and stays `true` for an `@outlook.com` account of the same unit. The page half is `output/t21-walkthrough.out` (branches 2 and 3). History: until T21 the row was the `cgiar-refused` state of AC-4 |
| AC-14 | **PASS** (single instance, as accepted in requirements section 6) | Six requests for one email with the cooldown cleared between them: five `sent`, the 6th `rateLimited`. 22 requests from one IP for 22 different emails: the 21st and 22nd are `rateLimited`. Verify: the 11th verify for one email is `rateLimited`; of 52 verifies with no challenge from one IP the last 2 are `rateLimited` (stages `limits`, `attempts`) |
| AC-15 | **PASS** | `crp_enable_email_notification` set `false` for AICCRA (a password login then carries `"false"` in its session): the code email still arrives; the saved value `true` is restored afterwards. Sink stopped: HTTP 200 `{"status":"sent","destination":"p***@outlook.com"}` in 0.03 s and `auth.otp.delivery outcome=email_failed` at `[WARN ]` (stage `smtp`, `ac-probe.main-run.out`). A listener that accepts and never answers: HTTP 200 in 0.169 s, then `outcome=email_failed ms=10110` at WARN, the 10 s `otp.smtp.timeout.ms` default (`ac-probe.blackhole.out`) |
| AC-16 | **PASS** for what a browser can show; **screen-reader behavior and the reCAPTCHA overlap NOT VERIFIED** | Section 5 (walkthrough of attempt 1; the UI files did not change in attempt 2, only `OtpEligibility` did) |
| AC-17 | **PASS** | `ac17-migration.sh`, output in `ac17-migration.out` with the invocation and the database names. The migration applied twice more to the copy (`aiccradb_otp_probe`, which already had it): the same `parameters` rows, no duplicate, exit 0. On `otp_probe_ac17_b`, which has no `global_units` table at all (so no units 45/47) and `global_unit_types` 1-5: applied twice, 3 tables, 4 `parameters` rows (types 1-4, category 2). On `otp_probe_ac17_c`, with no `global_unit_types` rows: applied twice, 0 rows, no error. Both scratch databases were dropped. **T21 note:** the T21 migration (AC-21) removes the key this migration seeded; AC-17 still describes the T01 file on its own |
| AC-18 | **PASS** (re-run 2026-10-15, T19: full body, non-production columns) | Stage `emaillog`, `output/ac-probe.t19-nonprod-leak-emaillog.out`. **Delivered:** one row, `succes_email` 1, no error, the source, unit 45, `tried` 1, the submitted address in `to_email`, the mail's subject **without** the `(TESTING ENVIRONMENT) ` prefix the sink received, the support mailbox in `bbc_email`; cc, file and message id NULL; **`message` is the HTML part of the sent email with its code and without the non-production header (checked for equality against the sink's copy)**; the code is in no other column and no other column holds a six-digit run; a resend inside the cooldown answers `rateLimited` and adds no row. **Failed** (sink stopped, then restarted): the answer is still `sent`, one row with `succes_email` 0 and `error_email = MailConnectException` (a bare class name), the support mailbox in `bbc_email`, the body with its code in `message` and nowhere else. History: the 2026-10-14 run (T18) stored a placeholder body. **Sends nothing:** the five ineligible states of AC-4 and a domain off the allow-list leave no row and no mail. **Resend**, as a super admin of the copy: `emailLogs.do` lists the failed code email; `sendFailEmail.do` by id and filtered on the source, list and resend, select nothing; the code-email rows are unchanged (checksum) and no mail reaches the sink. **Control:** a failed row of another source is selected by id, in the filtered list and in the filtered resend, so the empty answers are not a broken request |
| AC-19 | **PASS** (2026-10-15, T19) | Stages `nonprod` and `nonprodblank`, `output/ac-probe.t19-nonprod-leak-emaillog.out` and `output/ac-probe.t19-nonprodblank.out`. **`nonprod`** (8 checks, `marlo.production=false`): one eligible request leaves exactly one message at the sink; the SMTP recipient, the To header and nothing else (no Cc or Bcc) is the support mailbox `email.notification`; the subject is `(TESTING ENVIRONMENT) Your MARLO sign-in code`; the plain part starts with `To: probe.user@outlook.com` and a separator line, the HTML part with the same header and `<br>`; the submitted address is no SMTP recipient and in no To header of any message at the sink; the code from that email signs the user in (`ok`); the `email_logs` row has the submitted address in `to_email`, the support mailbox in `bbc_email`, the unprefixed subject, and a `message` equal to the production HTML body. **`nonprodblank`** (4 checks, the application restarted with `EMAIL_NOTIFICATION=` empty): the response is still `sent`, 0 messages at the sink, 0 `email_logs` rows, one `auth.otp.delivery outcome=email_failed` line at WARN (cause `AddressException` on the `auth.otp.mail` line), and no `auth.otp.code` line. **Not run live: production** (`marlo.production` stays false here): the recipient, the absent prefix and header, the null `bbc_email` and the per-send read of the flag are `OtpMailSenderTest` cases, each with a falsifier that turns it red |
| AC-20 | **PASS** (2026-10-15, T20; row added 2026-10-21 by the doc drift check against the code) | Unit and live, as `test-report.md` records it (FN-017, UI-005: the AICCRA shell around the HTML part, the text part keeps the code, a unit without a shell gets the bare body; mail shell tests and `theEmailIsRenderedWithTheCustomFile…`; live `nonprod` at the sink). Live run of the T20 verification notes in `tasks.md`, on a fresh `aiccradb_otp_probe` (AICCRA, `crp_custom_file = aicrra`, `marlo.production=false`, sink mailbox `support.probe@sink.test`): stage `nonprod`, **8 checks, 0 failed**. The sink's HTML part is the test header, then the AICCRA shell (logo and lockup, grey card with the title, the code, the expiry, once, do not share and ignore lines, the hills band, the footer note and the `mailto:` support link), images from the instance base URL; the `email_logs` row holds the wrapped HTML without the header. No output file under `output/` was recorded for this run, and a screenshot was taken but not committed. The other probe stages were not re-run for T20 |
| AC-21 | **PASS** (2026-10-15, T21) | `ac21-migration.sh`, `output/ac21-migration.out` (**17 checks, 0 failed**). On a throwaway copy of `aiccradb_actsave` that holds the T01 migration and a value on the old key (one child row): applied twice, 4 rows of each new key (types 1-4; the switch boolean, default `false`; the exclusion text, empty default; category 2), 0 old `parameters` rows, 0 old `custom_parameters` rows, nothing duplicated on the second run, no `custom_parameters` row for either new key (every unit starts off), every other `parameters` and `custom_parameters` row unchanged (295 and 1623 rows, checksummed), both `description` values equal to the DA-002 text on all 4 rows, and the T01 migration file untouched. On `otp_probe_ac21_b` (21 seeded units, ids < 29, no unit 45 or 47, the old key with one custom value): applied twice, 4/4/0/0. On `otp_probe_ac21_c` (no `global_unit_types` rows): applied twice, 0 `parameters` rows. Both scratch databases were dropped. **Through Flyway** (the application start on a second, fresh copy that held the same fixture): `schema_version` records `V2_6_0_20261015_1418__ReplaceOtpAllowListWithSwitchAndExclusion.sql` with success 1, 8 new rows are there, 0 old rows, and the fixture child row on the old key was deleted first. **Live change:** stage `live`, `output/ac-probe.t21-live.out` (7 checks, 0 failed): turning the switch on, adding and removing an excluded domain, and emptying the exclusion all take effect on the next request, with no restart. **Falsifier:** the DAO read served through the Hibernate query cache (`output/ac-probe.t21-live-FALSIFIER-red.out`): 3 of 7 red, the switch turned off and the domain added to the exclusion are not seen |

Defect-class gates of requirements section 6 with no automated check: concurrency (section 4), persistence leak
(AC-11), session-tail divergence (AC-5), oracle between account states (AC-4), UI states (section 5) all have a
recorded run above. Rate limits across instances remain the accepted partial (single local instance).

## 4. Concurrency probe and the falsifier

Green runs on the final build, 13 and 4 checks, 0 failed (`output/concurrency-probe-http.green.out`,
`output/concurrency-probe-http.tz-Tokyo.out`, `output/concurrency-probe-sql.green.out`):

| Case | Result |
|---|---|
| 4 (runs first) ten parallel request-code for one email on fresh buckets, with a 3-hour-old expired `otp_challenges` row, **a 2-hour-old `otp_cooldowns` row and a 2-day-old `otp_rate_limits` window** pre-seeded, plus two controls inside the retention (a 10-minute-old cooldown, a 1-hour-old window) | `{sent: 1, rateLimited: 9}`, 1 open row, 1 mail, 0 `unavailable`; the stale challenge, the stale cooldown and the stale window are gone; both controls remain |
| 1 ten parallel correct verifies | `{ok: 1, invalid: 9}`, exactly one consumed row |
| 2 ten parallel wrong codes | `{exhausted: 8, mismatch: 2}`, `attempts` ends at 3, `consumed_at` null, the correct code then `exhausted` |
| 3 three wrong + one correct after exhaustion (5 rounds) | 0 sign-ins, 0 consumes |
| 3b race burst from `attempts=0`, 25 rounds | outcomes `ok` at attempts 0/1/2 and `no-ok` at 3 only; never "signed in with attempts=3" |
| 4b eight rounds of the same on fresh emails (deadlock hunt) | every round 1 `sent`, 9 `rateLimited`, 1 open row, 0 `unavailable` |
| S1 ten parallel `CONSUME_SQL` | one session affects 1 row |
| S2 ten parallel `INCREMENT_ATTEMPTS_SQL` | exactly 3 affect a row, `attempts` 3 |
| S3 exhausted row, 3 increments + 4 consumes fired together, 10 rounds | 0 consumes |
| S3b from `attempts=0`, 30 rounds | never consumed with `attempts=3` |

**Cooldown and window purge during an accept (the T04 pointer).** `OtpRateLimiter.purgeStaleRows` runs after the counting, at
most once per `PURGE_INTERVAL` (10 minutes) per application instance, and the first call after a start is always due. The probe
is made deterministic by running case 4 first on a freshly started app, so the first request of the burst to finish owns the
purge, concurrent with the other nine accepts. The cutoffs are `WINDOW_RETENTION` 1 day and `COOLDOWN_RETENTION` 1 hour. A
negative control (`output/concurrency-probe-http.throttle-not-due.out`, the same probe started again within 10 minutes) leaves
both stale rows in place and the check reports `FAIL` naming the throttle, so the check can fail and a pass means the purge ran
inside the burst. The Tokyo pass restarted the app and passed the same case.

**Isolation level** (recorded by `concurrency-probe-sql.sh`, see section 6): `REPEATABLE-READ` globally, in the probe's sessions
and in the 15 connections the application pool held open on the copy while the probe ran. The S1-S3b statements are single-row
conditional `UPDATE`s, so they do not depend on the level (an `UPDATE ... WHERE` locks the current row version under
`REPEATABLE-READ` and `READ-COMMITTED` alike); the S4 deadlock and the gap locks of case 4 do, which is why the level is recorded.

Informational, S4 (statement level, no cooldown in front): ten parallel supersede + insert transactions for one email
end with one open row, but nine of them die with MySQL error 1213 (deadlock on the `email_hmac` gap locks). That is the
fail-closed `unavailable` path. Through HTTP the cooldown's row lock admits one request at a time, so cases 4 and 4b never see it.
**Accepted risk handed to T16 with it:** if the limiter purge ever deadlocked against an accept, the visible result would be
`unavailable` (fail-closed), not a wrong answer; case 4 did not reproduce it in the two runs where the purge was due (normal and
Tokyo passes), which is evidence, not a proof of absence.

**Falsifier (JS-1).** `CONSUME_SQL` without `AND attempts < 3`:

- Statement level, re-run in attempt 2 because `concurrency-probe-sql.sh` changed: the probe read a scratch copy of the DAO
  through `PROBE_DAO` (`src` untouched). **`S3: FAIL (consumes=10)`** and **`S3b: FAIL (violations=25 of 30)`**; S1 and S2 still pass
  (`output/concurrency-probe-sql.FALSIFIER-red.out`).
- HTTP level, **attempt 1, kept** (the probe's case 3b did not change; only case 4 and the guards did): after a clean rebuild with
  the edit, **`case 3b: FAIL (violations=16 of 60 rounds)`**, outcomes such as `('ok', 'attempts=3', 'consumed=1')`: a correct code
  signed in after the third wrong code had exhausted the challenge. Case 3 stayed green, because the Java pre-check of design
  step 4 refuses first; only the statement-level S3 and the HTTP race burst 3b see the missing condition
  (`output/concurrency-probe-http.FALSIFIER-red.out`).

## 5. Browser walkthrough (claude-in-chrome, local app, throwaway copy) -- attempt 1, not repeated

T14 and the UI did not change between the attempts (the only change is `OtpEligibility`), so the walkthrough was not repeated.
Driven with real keystrokes and clicks where the check is about input, with `$.ajax` stubs only to delay or fail a response.

| Check | Result |
|---|---|
| UI-001 | "Email me a sign-in code" shows for the allowed unit and a user whose domain is on the list |
| UI-004 | Two synchronous clicks on Request sent 1 `otpRequestCode.do`; the button is disabled and reads "Sending..." while in flight. Enter pressed three times sent 1 `otpVerifyCode.do`. Double click on Verify with the right code: 1 verify, one `auth.otp.verify outcome=ok` line, redirected to `AICCRA/crpDashboard.do` |
| UI-003 | Typing `1a2b-3 4567` leaves `123456`. `inputmode=numeric`, `autocomplete=one-time-code`, `maxlength=6`, `<label for>` "Sign-in code", `#login-otp-status role=status aria-live=polite`, `#login-otp-error role=alert`; the field is focused when the step opens |
| UI-002 states | sending, code sent with `p***@outlook.com`, wrong code ("That code is not correct..."), too many attempts, expired or invalid (challenge aged in the copy), too many requests (11th verify), service unavailable (response stubbed to fail), resend countdown "Resend in 28 s" then "Resend code", "Use a different email" returns to the email step with the address kept |
| Go back during an in-flight request, several units | The user `otp.p15@` has four units. With the request delayed 3 s, "Go back" was ignored (still on the password step); the late `sent` then opened the code step for the selected unit |
| "Log in" during an in-flight request | **Observation, not a failure:** "Log in" is not disabled while the code request is in flight. A password attempt started (`validateUser.do` fired), failed, and the late `sent` then opened the code step, because the drop rule (`currentStep` changed or `isSubmitting`) is evaluated when the answer lands, after the password check ended. The mail was really sent, so the page is consistent. A T16 or UX note, not a UI-004 violation |
| `requestSeq` falsifier | The served copy of `login.js` (under `target/cargo`, not `src`) was patched and restored (`cmp` against `src` identical). With only the two `seq !== otpRequestSeq` guards disabled, a late answer after "Use a different email" is still dropped: **the step check and the in-flight guard make `requestSeq` redundant in every reachable sequence.** **Observation for T14: the `requestSeq` falsifier as written there cannot fail**, because disabling the sequence check alone changes no outcome; only with the step check disabled too does the late `sent` jump to the code step, so a falsifier that can fail must disable that guard as well |
| Expired state, sign-in success | See the `invalid` and `ok` rows above |

**NOT VERIFIED, needs a human or another environment:**
- reCAPTCHA overlap (a code entered while reCAPTCHA is showing): needs the Google widget, not available locally.
- Screen-reader behavior (accepted gap, requirements section 6): attributes are present, announcement is not tested.
- Visual comparison of password-only and Cognito units against a HEAD build: only the code-level identity (T14 Reviewer) and the `crpByEmail` flags were checked.
- The "CGIAR sign-in" wording on a `failed` answer (T14 advisory) was not triggered in the browser.
- Real mailbox deliverability (accepted risk until the pilot).

## 6. Forward pointers closed here

| Pointer | Result |
|---|---|
| **Transaction isolation** (T03 adjudication) | `@@global.transaction_isolation=REPEATABLE-READ` and `@@session.transaction_isolation=REPEATABLE-READ`, printed by `concurrency-probe-sql.sh` next to the statement dump (`output/concurrency-probe-sql.green.out`). The application sets none itself: `MarloDatabaseConfiguration` configures Hikari with no isolation and Hibernate with no `hibernate.connection.isolation`, and a search of `marlo-web/src/main` and `marlo-data/src/main` finds no isolation setting. Live proof, read from `performance_schema.variables_by_thread` while the app was running on the copy: the same output records `REPEATABLE-READ x 15` for the connections open on `aiccradb_otp_probe`. Server 8.0.43 |
| `MARLOCustomPersistFilter` on `/otpVerifyCode.do` and `/otpRequestCode.do` | **Does not run** (attempt 1 evidence, no output file kept). Its `LOG.debug("begin doFilter ...")` never appeared in a console log of 20 000 lines at level `ALL` with 2 500 lines naming the two routes. Consistent with the `WebAppInitializer` filters never seeing `.do` |
| Autocommit outside `@Transactional` | Every write the two actions make is visible to a second connection as soon as the call returns (challenge rows, attempts, `consumed_at`, `last_login`, the cooldown row): the probes read the rows through a second connection right after each call |
| Concurrent first-ever issue, deadlock | Case 4 and 4b, 3 runs of 10 parallel first-ever requests plus 16 rounds of 4b: 0 `unavailable` |
| Purge against the cooldown, `issue` before purge | Section 4: stale `otp_challenges`, `otp_cooldowns` and `otp_rate_limits` rows are purged during the accept burst, controls survive, 1 `sent`, 0 `unavailable` |
| Non-UTC JVM | The machine is `-05`; a second pass on `-Duser.timezone=Asia/Tokyo` (`ac-probe.tz-Tokyo.out`, `concurrency-probe-http.tz-Tokyo.out`): correct code accepted, expiry boundary `-2 s` refused and `+20 s` accepted, all 13 concurrency checks green. `created_at`/`expires_at` are written as UTC text (`20:10:30.016` / `20:15:30.016` against UTC now `20:10:30.080`, with the JVM on Tokyo) |
| Post-login failure path | **Reproduced, a known gap shared with Cognito route C** (stage `postlogin`). With a member of unit 9901 (type 99, no landing page) the verify answers `{"status":"failed","redirect":".../login.do?authError=cognitoFailed"}` and the session cookie it sets **is authenticated** (principal present in the Shiro session). The code was consumed and the challenge is spent; the user, who proved the mailbox, holds a session whose unit attributes were never written. T16 documents it; a `logout()` hardening would belong in a task covering both flows |
| AC-11 `email_logs` check | Section 3, AC-11 |
| AC-12 startup proof against a 2xx route | Section 3, AC-12 (`login.do` HTTP 200 with the secret absent and with a 16-byte secret) |
| T12 binding with the secret configured | `email`, `globalUnitId`, `agree` bind (a request is mailed to the typed address on an allowed domain) and `code` binds (a verify reaches the store); extra `url`, `crp`, `user.email`, `globalUnit` parameters on both routes are ignored and the redirect stays `.../AICCRA/crpDashboard.do` (stage `attempts`) |
| T14 leftovers | Section 5 |
| Mail server that never answers (NF-001) | `ac-probe.blackhole.out`: HTTP 200 `sent` in 0.169 s; the delivery line `outcome=email_failed ms=10110` followed at WARN |

## 7. How to repeat

```
export PROBE_MYSQL='<command opening mysql -N on the throwaway copy>'   PROBE_MBOX=<sink mbox>
export PROBE_JDWP=auto PROBE_JAVA_CP=<dir with SessDump.class> PROBE_LOGS=<this run's console log>:<marlo-dev.log>
export PROBE_PW_EMAIL=probe.user@outlook.com PROBE_PW_PASS=<the test password set in the copy>
export PROBE_SINK_STOP='<command that stops the sink>'
javac -d <dir> probes/tools/SessDump.java
probes/concurrency-probe.sh            # first thing after the app starts (case 4 needs the purge throttle due)
probes/concurrency-probe-sql.sh
probes/ac-probe.sh enum attempts removed limits resend session leak live postlogin expiry smtp
PROBE_SINK_STOP='<command that stops the sink>' PROBE_SINK_START='<command that restarts the sink>' \
  PROBE_SA_EMAIL=<a super admin of the copy> PROBE_SA_PASS=<its test password> probes/ac-probe.sh emaillog   # T18 / AC-18
export PROBE_SUPPORT=<the email.notification the application runs with>   # T19: every code email goes to this mailbox outside production
probes/ac-probe.sh nonprod              # T19 / AC-19
# blank half: restart the application with EMAIL_NOTIFICATION= (empty), PROBE_LOGS=<its console log>, then:
probes/ac-probe.sh nonprodblank
# without both PROBE_SINK_* the failed half and the resend half print NOT RUN; without PROBE_SA_* the resend half does
probes/ac-probe.sh blackhole           # restart the sink afterwards
PROBE_ADMIN_MYSQL=... PROBE_ADMIN_DUMP=... probes/ac17-migration.sh
PROBE_ADMIN_MYSQL=... PROBE_ADMIN_DUMP=... probes/ac21-migration.sh   # T21 / AC-21; PROBE_COPY_DB defaults to aiccradb_otp_probe
```
Since T21, every `ac-probe.sh` run first sets the standing fixture (the switch on and the exclusion `excluded.test` for `PROBE_GU`); the
fixture users of section 2 must exist in the copy, including `otp.staff@cgiar.org`.
The no-secret and 16-byte passes restart the app with the variable unset or set to `openssl rand -base64 16`, and run
`ac-probe.sh nosecret` (with `PROBE_EXPECT_SECRET_ENV=absent|present`). The Tokyo pass restarts the app with
`-Duser.timezone=Asia/Tokyo` added to `JAVA_TOOL_OPTIONS` and runs `concurrency-probe.sh` and `ac-probe.sh tz`.
The app must be started with `JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:0`
for the `session` and `leak` stages. The agent listens on a new ephemeral port after every debugger detaches, which is why
`PROBE_JDWP=auto` re-reads it from the JVM that owns port 8080. To rerun the falsifier, copy `OtpChallengeMySQLDAO.java`
elsewhere, drop `AND attempts < ...` from `CONSUME_SQL` in the copy and point `PROBE_DAO` at it.

## 8. Cleanup

The app and the SMTP sink were stopped, the sink mbox and the environment files deleted, and `aiccradb_otp_probe` and both
scratch databases dropped. `aiccradb_actsave` after the run: `otp_challenges`/`otp_cooldowns`/`otp_rate_limits` 0/0/0, OTP
`custom_parameters` 0, `custom_parameters` total 1623, no `global_unit_types` 99, and `users.last_login` of 3862 unchanged at
`2026-10-13 12:56:27` (proof in the T15 attempt 2 report).

## 9. T18 run (2026-10-14)

`ac-probe.sh leak emaillog` on a clean build of the T18 working tree (2415 / 1062 / 72 files), with the copy recreated for
T17 and the section 2 fixtures added again: `probe.user@`, `otp.inactive@`, `otp.nonmember@`, `otp.cgiar@` and
`otp.softdeleted@` (all `@outlook.com`), and a test password for one super admin of the copy (`PROBE_SA_*`). Result:
`SUMMARY: 32 checks, 0 failed`.

Two things went wrong on the way, both outside the code under test:

- **Fixture users inserted by hand need `agree_terms`.** With it NULL, `User.getAgreeTerms()` answers `false`, so Hibernate
  sees the user loaded by the eligibility check as dirty. The commit of `issue` then flushes an update, and the audit
  listener throws `IllegalStateException` (no user in an unauthenticated request), so the request answered `unavailable`
  (`auth.otp.fault stage=issue`, found with `jdb` on the JDWP port). No real row has a NULL `agree_terms` (0 in the copy),
  so this is a fixture artifact, not a T18 regression. The fixtures were set to `agree_terms = 0`.
- **The first `emaillog` control check went red on the probe's own order.** It resent the control row by id first; the
  sink accepted it, the row became sent, and the filtered list and resend (both on "not sent") rightly no longer selected
  it. The control now lists by id and in bulk first and resends last; the re-run is the recorded one.

## 10. T19 run (2026-10-15)

`ac-probe.sh nonprod leak emaillog`, then `enum`, then (after a restart with a blank mailbox) `nonprodblank`, on the T19 working tree
(HEAD `ed87f3955f`) built by `scripts/run-marlo-java17.sh`, with `marlo.production=false`. The copy `aiccradb_otp_probe` was rebuilt from
`aiccradb_actsave` (619 tables, 3596 users, 1623 `custom_parameters` rows at the start, the same as the source) and given the section 2
fixtures again; user 3862 was renamed `probe.user@outlook.com` and a super admin of the copy `probe.admin@outlook.com` in the copy only, both
with a test password. Results: `nonprod leak emaillog` `SUMMARY: 43 checks, 0 failed`; `enum` 11 checks, 0 failed; `nonprodblank` 4 checks,
0 failed. Before any write, `ps eww` of the JVM on 8080 showed `MYSQL_DATABASE=aiccradb_otp_probe`, the sink's host and port, the
`EMAIL_NOTIFICATION` of the run and `OTP_HMAC_SECRET` present (value not printed); `marlo-dev.properties` had the same SHA-256 before the build,
after the start and after the run.

**What the sink recorded for one code email:** SMTP recipient `<support.probe@sink.test>`, `To: support.probe@sink.test`, subject
`(TESTING ENVIRONMENT) Your MARLO sign-in code`, and nothing naming the submitted address except the first line of each body.

Things that changed in the probe, so a reader of an older output is not confused:

- A user's code is found by the body header `To: <submitted address>`, no longer by the envelope.
- The AC-11 log checks allow the code and the full email only in the one `auth.otp.code` line, and require exactly one per log.
- The AC-18 checks expect the full HTML body in `message`, the support mailbox in `bbc_email` and the unprefixed subject. The probe no longer
  prints a row's `message`, only its length, because it holds the code.
- The earlier T15 and T18 outputs in `output/` are unchanged and describe the code of their date.
- **Not re-run for T19:** `attempts`, `resend`, `expiry`, `removed`, `limits`, `session`, `live`, `smtp`, `blackhole`, `tz`, `postlogin`. Their
  checks only read a code through the helper that now follows the header, and no code under test in them changed.

Cleanup as on 2026-10-14: the application and the sink were stopped, the sink mbox, the app logs and the credential files deleted, and
`aiccradb_otp_probe` dropped. `aiccradb_actsave` and `aiccradb1`-`4` were never written to.

## 11. T21 run (2026-10-15)

Per-unit switch and domain exclusion instead of the allow-list (DD-13). Working tree = HEAD `ac4dcc50fd` plus the T21 diff, built clean
(2415 / 1064 / 78 files in marlo-data / marlo-web / test-classes, `BUILD SUCCESS`), `marlo.production=false`. The copy `aiccradb_otp_probe`
was cloned from `aiccradb_actsave` (619 tables, 3596 users, 1623 `custom_parameters` rows, the same as the source), given the section 2
fixtures (the user addresses as before, plus `otp.staff@cgiar.org`) and a value on the old allow-list key for AICCRA, so Flyway had a child row to
delete first. The application applied the T21 migration itself at start (`schema_version` row, success 1). Before any write, `ps eww` of the JVM on 8080
showed `MYSQL_DATABASE=aiccradb_otp_probe`, the sink's host and port, `EMAIL_NOTIFICATION=support.probe@sink.test` and `OTP_HMAC_SECRET` present
(value not printed); `marlo-dev.properties` had the same SHA-256 before the build, after the start and after the run.

| Stage or script | Result | Output |
|---|---|---|
| `ac21-migration.sh` | 17 checks, 0 failed | `output/ac21-migration.out` |
| `enum` | 18 checks, 0 failed (AC-2, AC-3, AC-4, AC-13, FN-003, FN-005, FN-006, FN-016) | `output/ac-probe.t21-enum.out` |
| `live` | 7 checks, 0 failed (AC-1, FN-002, FN-003, AC-21) | `output/ac-probe.t21-live.out` |
| `attempts removed limits nonprod postlogin emaillog` | 30 checks, 0 failed, 1 NOT RUN (the resend half of AC-18 needs a super admin login; `SendFailEmailActionTest` covers it) | `output/ac-probe.t21-more.out` |
| `session leak` | 20 checks, 0 failed (AC-5, AC-11) | `output/ac-probe.t21-session-leak.out` |
| browser walkthrough of the step-3 branches | external with the code, CGIAR on Cognito with the code, CGIAR with `cgiar.org` excluded, switch off: all PASS | `output/t21-walkthrough.out` |
| `live` against a build whose DAO read goes through the Hibernate query cache (falsifier, not committed) | 3 of 7 red | `output/ac-probe.t21-live-FALSIFIER-red.out` |

Things that went wrong on the way, all outside the code under test:

- **One harness failure in the first `enum` run.** The probe sent `x@excluded.test` and then `  X@EXCLUDED.TEST `, which normalise to the same address,
  so the second hit the 30 s cooldown and answered `rateLimited` (counted before any lookup, by design, SEC-010). The second request now uses another local
  part; the recorded run is the second one.
- **The first falsifier attempt was red for the wrong reason.** Marking the native query cacheable without declaring its scalar types made every request fail
  (`IllegalStateException: aliases expected length is 0; actual length is 3`, answer `unavailable`). The recorded falsifier declares the three scalars, so the
  read succeeds and is stale.
- **An incremental `mvn install` after the DAO edit reported `class file for APConfig not found`** on test compilation, and a later start failed on a missing mapper
  bean. A wipe of the three `target` directories fixed both. Nothing in the code changed.

Cleanup: the application and the sink were stopped, the scratch credentials, the sink mbox and the logs deleted, and `aiccradb_otp_probe` dropped
(both `ac21` scratch databases were dropped by the script). `aiccradb_actsave` after the run: `otp_challenges`/`otp_cooldowns`/`otp_rate_limits` 0/0/0,
`custom_parameters` 1623, no `system_otp_*` parameter, no `global_unit_types` 99, 619 tables, `users.last_login` of 3862 unchanged at
`2026-10-13 12:56:27`. `aiccradb1`-`4` were never written to.


## 12. Re-run for CHG-PWD-SWITCH-001 (2026-10-17)

**Moved.** The record of this run, with the stage list, results and output names, lives in `docs/specs/archive/2026-10-18-changes--password-login-switch/probes/README.md` section 2. The one script of this folder that was re-run here is `ac21-migration.sh`:

| Script | Result | Output |
|---|---|---|
| `ac21-migration.sh` after DA-002 was reworded by `CHG-PWD-SWITCH-001` DA-003 | 17 checks, 0 failed | `output/ac21-migration.pwd-switch-rerun.out` |
| the same, first run, with a `custom_parameters` row for `system_otp_login_active` left in the copy by the earlier probes | 1 FAIL: check "no custom_parameters row seeded for either key" saw the fixture row, not a migration row; the two description checks passed. The row was deleted and the run repeated | `output/ac21-migration.pwd-switch-rerun-1-fixture-row-red.out` |