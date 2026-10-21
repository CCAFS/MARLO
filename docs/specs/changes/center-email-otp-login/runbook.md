# Runbook and rollout checklist: Center email one-time-code sign-in

**Spec:** `docs/specs/changes/center-email-otp-login` (A2-2631) · **Task:** CHG-OTP-LOGIN-001-T16 · **Requirement:** OPS-004

**What this is.** Part A is the operating runbook: turn the sign-in code on and off for a Global Unit, handle the
secret and the proxy list, triage "I did not get my code", and what support may say. Part B is the go-live checklist.
Part C records the walk of this runbook on the local stack. Part D lists the shared-file edits that wait for
`/akili-archive` on `staging`.

**Status (2026-10-13).** The feature is **merge-ready and not go-live-ready.** Every item in Part B that needs
infrastructure, the product owner or PMU is still open and **blocks go-live, not merge**. No infra answer has been
obtained yet; none is claimed here.

> **Amended 2026-10-17 (`CHG-PWD-SWITCH-001`).** A Global Unit can turn password sign-in off with `system_password_login_active`. While it is off and the code key is valid, the code is the way to sign in for every account that does not use Sign in with CGIAR, and the rules below about the code switch and the exclusion list apply only to Sign in with CGIAR accounts of that unit. The operating procedure for that switch (prerequisites, turn off, turn back on, log lines) is [`docs/specs/archive/2026-10-18-changes--password-login-switch/runbook.md`](../../archive/2026-10-18-changes--password-login-switch/runbook.md). Each affected row below carries its own note.

## Quick path

| I want to | Do this |
|---|---|
| Enable code sign-in for a unit | Set the secret (A2), then turn the unit's `system_otp_login_active` on in the admin UI, with an optional `system_otp_excluded_email_domains` (A1) *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: or turn password sign-in off for the unit, which makes the code the default method; see the runbook of that spec)* |
| Disable it for a unit | Turn the unit's `system_otp_login_active` off in the admin UI. It applies on the next request, with no restart (A1, proved in C) *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: this does not remove the code from a unit that has password sign-in off; turn that switch back on first)* |
| Disable it everywhere, fastest | Turn every unit's switch off. Unsetting the secret also works but needs a restart (A2) |
| Find out why a code did not arrive | A5. Search `auth.otp.` in the application log |
| Answer a user who says "no code" | A6. One neutral answer for every ineligible account |

---

# Part A. Runbook

## A1. Enable and disable per Global Unit (DD-12, DD-13)

*Rewritten 2026-10-15 for T21. The build reads the switch and the exclusion; the T21 migration removes the old allow-list `crp_otp_allowed_email_domains`.*

The feature uses **two specificities per Global Unit**, both stored with the `system_` prefix. Values live in `custom_parameters`, one row per unit. Both ship **off**. Change them **only through the admin UI** (DD-12); SQL is for evidence and for the falsifier, not for operations.

| Key | Type | Meaning |
|---|---|---|
| `system_otp_login_active` | Boolean | **On:** every account with access to the unit (the user exists and is active, with an active membership in the unit) can ask for a code, CGIAR or not; the terms box must be ticked on each request. **Off:** nobody can *(Amended 2026-10-21, doc drift check against the code: eligibility is `OtpEligibility.eligibilityBasis`: user found, `users.is_active`, then `existActiveCrpUser`. Terms are not a stored acceptance: it is the per-request `agree=true` box, checked at request step 5 (`terms_required`))* *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: while the unit has `system_password_login_active` off and the code key is valid, accounts without Cognito get the code whatever this says; only a Cognito account follows it)* |
| `system_otp_excluded_email_domains` | Text | Domains that never get a code in this unit, for example `cgiar.org` to keep CGIAR staff on Cognito. Empty means no exclusion *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: while the unit has password sign-in off, it excludes only accounts that use Sign in with CGIAR)* |

To change them:
1. Sign in as a super admin and open **System Admin > PTF Parameters** (`/superadmin/marloParameters.do`).
2. Expand the unit, open the **Specificities** tab and type `otp` in the search box.
3. **Enable:** turn `system_otp_login_active` on. Optionally fill in the exclusion. **Disable:** turn the switch off. Press **Save**.
4. Check that `crpByEmail.do` for an account of that unit reports `otpEnabled: true` (or `false`), and that the login page shows (or hides) "Email me a sign-in code".

The exclusion list follows these rules (`OtpDomainPolicy.parseExclusionList`):

| Rule | Detail |
|---|---|
| Matching | **Exact domain** after trimming and lower-casing. No wildcard and no subdomain: `partner.org` does not exclude `mail.partner.org` |
| Cleaning | One leading `@` is stripped. Empty entries, entries that still contain `@`, and entries with any character outside `0x21`-`0x7E` (non-ASCII, **or a space inside the entry**) are dropped. Duplicates are merged. **Separate entries with commas only** |
| `cgiar.org` | No special treatment in code. Exclude it explicitly when a unit wants Cognito to stay the only method for CGIAR staff (rollout stage 1, B5) |
| Length | `custom_parameters.value` is `varchar(500)` |

Behaviour of the screen, observed in the walk (Part C):

- It lists the parameters of the unit's **type**. The first save writes a `custom_parameters` row for each unit that lacked one
  (four rows for this key, three of them blank, which is still off). It is a side effect of the screen, not of the feature.
- The change reads the database directly, so it applies **on the next request on every instance**. The Hibernate caches
  are not involved (DD-12). The other specificities keep their cached read.

**Rollback, cheapest first** (design section 14). Only the first is "next request":

1. Turn `system_otp_login_active` off in the admin UI: live, per unit (T21). *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: it does not switch the code off in a unit that has password sign-in off)*
2. Blank `otp.hmac.secret` (and unset `OTP_HMAC_SECRET` if the environment uses it): the secret is read **once at start** (`OtpKeys`), so this needs a restart of every instance.
3. Revert the deploy.
4. Drop the three `otp_*` tables. This is destructive and the last resort.

## A2. Secret: generate, set, rotate

| Item | Value |
|---|---|
| Generate | `openssl rand -base64 48` (standard base64 that decodes to at least 32 bytes; base64url or a shorter value is refused). Store it as **a single line**. The value is trimmed when read (`APConfig.getOtpHmacSecret`), so a trailing newline or surrounding spaces are harmless; whitespace **inside** the value is refused by the decoder and the feature stays off *(Amended 2026-10-21, doc drift check against the code: the earlier text said a trailing newline was refused; `APConfig.getOtpHmacSecret` trims the value through `cognitoSetting`)* |
| Where | The property `otp.hmac.secret` in the per-environment `marlo-<profile>.properties` (gitignored), like the other credentials. The environment variable `OTP_HMAC_SECRET` also works and **takes precedence** (Spring reads system properties, then the environment, then the file). Use the same route the environment already uses for `cognito.client.secret` (amended 2026-10-16; P-18) |
| Without it, or too short | The feature is **off**. `OtpKeys` logs **one WARN**, `auth.otp.keys outcome=unconfigured: ... <reason>`, **without the value, its length or its decoded length**. Startup is not affected. Every request-code call answers `domainNotEnabled`, except a call that is not a POST, which answers `unavailable` and logs `bad_method` before the key is checked (step 0, T22) *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: the password stays available in **every** unit that has the code key missing, whatever `system_password_login_active` says (last resort), and one ERROR `auth.password.last_resort` per unit is logged at the first lookup; see the runbook of that spec, section 5)* *(Amended 2026-10-21, doc drift check against the code: the `bad_method` exception added. The `auth.password.last_resort` ERROR is written only for a unit whose password switch is off, once per unit per application start, at the first `crpByEmail.do` lookup or welcome-email build that answers for that unit, not at startup (`OtpEligibility.logLastResortOnce`). Format: `auth.password.last_resort gu=<acronym> reason=code_key_unconfigured`)* |
| Storage | Never in a tracked file. The `marlo-test.properties` template carries the key **empty**. A missing key never stops the application: `${otp.hmac.secret:}` defaults to empty and the feature stays off |
| Production | **Do not set the key in production before the release is deployed** (it is inert but must not be staged early) |
| Rotation | Generate a new value, set it on **every** instance, restart them. It **invalidates every open code** (the MAC keys change), so users mid-sign-in must request a new one. Because the restart is required and Shiro sessions live in memory (`new DefaultWebSessionManager()`, `ShiroSpringStartupListener.java:51`), no pending challenge survives it: a code issued before the change always answers `mismatch` ("Not correct"), spends no attempt and never reaches `exhausted`. The user must request a new code. *(Amended 2026-10-21, doc drift check against the code: the earlier text said an old code spent attempts and ended in `exhausted`, which cannot happen without a pending challenge)* Codes live 5 minutes, so rotate when traffic is low |
| Per-environment check | After start, confirm there is **no** `auth.otp.keys outcome=unconfigured` line |

## A3. Trusted proxies: `OTP_TRUSTED_PROXIES`

The rate limits are per client IP. Behind a proxy or load balancer every request arrives from the proxy, so without
this list **all users share one IP bucket** (20 code requests and 50 verifies per 15 minutes in total). P-6 asks whether
a proxy exists at all (Part B).

| Item | Value |
|---|---|
| Variable | `OTP_TRUSTED_PROXIES` (maps to `otp.trusted.proxies`; the mapping is the same as the secret's, but this exact name was not run) |
| Format | Comma separated **IPv4 or IPv6 literals, or CIDR ranges**, for example `10.0.4.0/24,10.0.5.17`. Spaces around entries are trimmed |
| How it is used | If the TCP peer is inside the list, `X-Forwarded-For` is read **from the right** and the first hop **not** in the list is the client. Otherwise the header is ignored |
| Falls back to the peer address | A missing, empty or malformed header from a trusted peer; a header whose hops are all trusted; an untrusted peer |
| Bad entries | An entry that is not an IP literal or CIDR is **ignored**, and `auth.otp.proxies outcome=entries_ignored count=N` is logged at WARN (the resolver is built per request, so the line repeats) |
| Never use | **Link-local addresses** (`fe80::/10`, `169.254.0.0/16`). A zone-suffixed one such as `fe80::1%2` is refused as malformed, so the proxy is not trusted and every user shares its bucket. It fails safe and hurts availability. List the proxy's routable address or its subnet |
| Never use | A range wider than the proxies themselves. Any peer inside the list can set the client IP |
| DNS | No name is ever resolved. Use literals |
| Unset | No peer is trusted. Correct only when Tomcat sees the browser directly |

## A4. Mail delivery

| Item | Value |
|---|---|
| Sender | Dedicated: 2 daemon threads, a queue of 100. It reads the same SMTP settings as the rest of MARLO. It does not go through the general mailer, so it ignores the notification flag and the `crp_email_support_team` redirect; outside production it applies MARLO's test rule itself (Recipient row). It writes its own row in System Admin → Emails, with the full email, code included (see A5) |
| Recipient | **Production:** the submitted address. **Any other environment** (`marlo.production` not `true`): only the support mailbox `email.notification`, with `(TESTING ENVIRONMENT) ` in the subject and the submitted address at the top of the body, the same as every other MARLO email. If that mailbox is not set, the code email fails (`email_failed`) and is never sent to the user instead (FN-018, OQ-5 resolved 2026-10-15) Outside production the value must be a single bare address with no surrounding spaces, or every code email fails. **In every environment, production included,** `email.notification` is also the From address (`OtpMailSender.build`, `require(getEmailNotification(), "sender")`): if it is unset, every code email fails with `email_failed` *(Amended 2026-10-21, doc drift check against the code: From-address dependency added)* |
| `otp.smtp.timeout.ms` | Connect, read and write timeout. **Default 10 000 ms, clamped to 30 000, a malformed value falls back to the default.** Set it like the other `otp.*` values (the environment form is `OTP_SMTP_TIMEOUT_MS`; the mapping is the same as the secret's, but this exact name was not run) |
| Timing | The request answers **`sent` at once**. A failure shows up **later**, at WARN, as `auth.otp.delivery outcome=email_failed ms=<n>`; the cause class name (never its message) is on a separate line, `auth.otp.mail outcome=send_failed cause=<class>`, so search that one for the cause. A full queue, or a failure while building the message, logs `email_failed` at once instead of later. Walk: a mail server that never answered gave HTTP 200 in 0.17 s and `email_failed ms=10110` afterwards |
| **Relay note** | In local and non-production environments the configured host `cluster5out.us.messagelabs.com` is CGIAR's inbound gateway. It delivers only to `@cgiar.org` and refuses relay for anything else (`453 ... as a relay ... (#4.7.1)`). **Code sign-in is meant above all for external users** (stage 1 excludes `cgiar.org`), so production needs a relay that delivers to external domains. **Confirming that is a go-live item (Part B).** To test locally, use a sink: `EMAIL_HOST`/`EMAIL_PORT` and `EMAIL_HOSTBACKUP`/`EMAIL_PORTBACKUP` to `127.0.0.1:2525` |
| Deliverability | Unproven until the pilot. Needs a sender with aligned SPF, DKIM and DMARC. Partner IT may have to allow-list it |

## A5. Code not received: triage (design section 11)

One line per call, **no email, code, nonce or session id**. The one exception is outside production only: the `auth.otp.code` line (A7) carries the address and the code.

`auth.otp.<request|verify|delivery> outcome=<x> gu=<acronym> domain=<domain> ms=<n>`

Find the user's attempt by **time, `domain` and `gu`**, never by address. `delivery` is WARN when it failed, INFO when it
succeeded; `request` and `verify` are INFO.

*(Amended 2026-10-21, doc drift check against the code:)* `auth.otp.request outcome=sent` can end with ` via=password_off` when the code was issued by the password-off override of `CHG-PWD-SWITCH-001` (`OtpLog.LINE_VIA_PASSWORD_OFF`); no other outcome carries it. The `request`, `verify` and `delivery` lines lower-case `gu` and `domain` (`OtpLog.field`); the non-production `auth.otp.code` line (A7) keeps the stored case of `gu` (for example `AICCRA`, not `aiccra`), so search it case-insensitively.

**Step 1. Search `auth.otp.request` around the time of the attempt.**

| What you find | Meaning | Next |
|---|---|---|
| **No line** | The request never reached the app | Browser or network problem, a proxy dropping the POST, or a JavaScript error. Check the proxy log and `otpRequestCode.do` |
| `outcome=sent` | Account eligible, code issued, mail handed to the sender | Step 2 |
| `outcome=neutral` | The account was **ineligible** (see A6). Server-side only; the user saw "code sent" | Fix the account if it is meant to be eligible. Say nothing about why to the user |
| `outcome=denied_domain` | The switch is off, the domain is on the unit's exclusion list, the unit is unknown, or the shape was bad. The user saw "Code sign-in is not available for this email. Please use your password." (`login.otp.error.domainNotEnabled`) *(Amended 2026-10-21, doc drift check against the code: the earlier text called it the "not enabled" message)* | Check A1 for that unit *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: not produced for the switch or the exclusion while the unit has password sign-in off and the code key is valid)* |
| `outcome=unconfigured` | The secret is missing or too short | A2. Look for the startup WARN `auth.otp.keys` |
| `outcome=rate_limited` (note `gu=` is empty, the limit runs before any lookup) | 5 requests per email or 20 per IP per 15 minutes, or the 30 s cooldown | Ask the user to wait. If many users hit it, suspect a shared IP bucket (A3) |
| `outcome=terms_required` | The terms box was not ticked. The cooldown still started | Ask the user to tick it |
| `outcome=bad_method` (note `gu=` and `domain=` are empty: nothing about the call was read) *(Added 2026-10-18, T22)* | The call was not a POST, or had no servlet request. It changed nothing: no limiter hit, no row, no mail, no session write. The caller got `unavailable` | A browser address bar, a link, a crawler or a scanner calling `otpRequestCode.do` with GET (or HEAD, PUT), or a proxy rewriting the method. The page's own `login.js` always POSTs, so a user who sees "unavailable" with this line points to a proxy or redirect that turns the POST into a GET (a 301/302 on `otpRequestCode.do`). Check the proxy rules. Nothing to fix in the account |
| `outcome=internal_error` | Fault, usually with an `auth.otp.fault` line | Developer. Usually the user saw "unavailable"; with an `auth.otp.fault stage=eligibility` line the user saw "code sent" (deliberate: the answer must not depend on the account) |

**Limiter store faults.** Two WARN lines come from the limiter itself, not from a request outcome: `auth.otp.ratelimit outcome=store_failure cause=<class>` (a bucket or cooldown read or write failed; the request fails closed and the user sees "unavailable") and `auth.otp.ratelimit outcome=purge_failed cause=<class>` (the opportunistic purge failed; harmless to the user, but repeated lines mean the table is not being cleaned). Both are for a developer or the database owner; do not tell the user *(Added 2026-10-18, validation advisory)*.

**Step 2. For `sent`, search `auth.otp.delivery` a few seconds later.**

| What you find | Meaning | Next |
|---|---|---|
| `outcome=delivered` | The relay accepted the mail | Deliverability: spam folder, partner filtering, sender authentication, relay logs |
| `outcome=email_failed` (WARN) | The send failed: timeout, refusal, a full queue, or the relay refusing the recipient domain | Read the cause class on the `auth.otp.mail outcome=send_failed` line. A refusal of every external address means the relay note in A4 |
| **No delivery line** | Still in flight (up to the timeout), or the instance restarted | Look again after the timeout |
| `ms=` near the timeout (about 10 000) | The relay is slow or not answering | A4 |

**Shortcut for support: System Admin → Emails.** Every code email the sender queued has a row there with source
`/otpRequestCode`, found by the user's address:

| What you find | Meaning | Next |
|---|---|---|
| A row under **Sent** | The relay accepted the mail | Same as `outcome=delivered` above |
| A row under **Not sent**, with an error class | The send failed | Same as `outcome=email_failed` above. **Do not resend it**: its code has expired or been replaced, and MARLO skips it. Ask the user to request a new code |
| **No row** at the time of the attempt | Usually no code email was queued: the account was ineligible, the domain is not enabled, the user was rate-limited, or the hand-off was rejected. Two other cases leave no row: the send is still in flight (the row is written when the attempt ends, up to the timeout), or the row could not be saved (`auth.otp.mail outcome=log_failed` at WARN; the email itself may have been delivered) | Steps 1 and 2 are the authoritative check: the `request` and `delivery` lines exist even when the row does not |

The row shows the full email, code included, so support can see exactly what the user received. The code is useless once it
expires (5 minutes), is used or is replaced. **Never read a code out to anyone:** whoever is given a live code can sign in as
that user. Outside production the row still shows the user's address in **To**, while the email itself went to the support
mailbox; the row shows that mailbox in **BCC**.

**Step 3. If the user got a code but cannot sign in, search `auth.otp.verify`.**

| `outcome` | The user sees | Meaning |
|---|---|---|
| `ok` | Signed in | Normal |
| `mismatch` | "Not correct" | Wrong code, **or no pending challenge in this session**: a verify that lands on another instance than the request does this even with the right code, which is the P-4 (affinity) symptom. Also **secret rotated or instance restarted** since the code was issued |
| `exhausted` | "Too many incorrect attempts" | Three wrong codes. A new code is needed |
| `invalid` | Expired or invalid | Older than 5 minutes, already used, or replaced by a resend |
| `not_eligible` | `invalid` | The code was accepted but the account lost eligibility in between (verify step 8). If the eligibility check itself threw, the line is `internal_error` instead (with `auth.otp.fault stage=eligibility`); the user still sees `invalid` *(Amended 2026-10-21, doc drift check against the code: lost eligibility moved from the `invalid` row: that case logs `not_eligible` or `internal_error`, never `invalid`)* |
| `rate_limited` | "Too many" | 10 per email or 50 per IP per 15 minutes |
| `failed` | Generic sign-in error | The code was accepted but the shared sign-in tail refused. See the post-login gap in A7 |
| `bad_method` *(Added 2026-10-18, T22)* | "Unavailable" | The verify call was not a POST. No attempt was counted, nothing was consumed and the session is unchanged, so the user can submit the code again. Same causes as the `request` row of step 1 |
| `internal_error` | Usually "Unavailable"; with `stage=eligibility` on the `auth.otp.fault` line, "Invalid or expired" | Fault. Do not tell the user |

## A6. Support note: ineligible accounts

Seven states, **one neutral answer** ("a code is on its way to k***@domain") **for the first four**. Nothing is mailed to them
and nothing in the response, the timing or the page tells them apart (anti-enumeration, FN-005).

| Account state | What the user sees | Mail sent |
|---|---|---|
| Unknown email | "Code sent" | No |
| Inactive user (`users.is_active = 0`) | "Code sent" | No |
| Not a member of that Global Unit | "Code sent" | No |
| **Soft-deleted member** (`crp_users.is_active = 0`, how MARLO removes a member; refused) | "Code sent" | No |
| **Switch off, or the domain is on the unit's exclusion list** | **"Code sign-in is not available for this email. Please use your password."** (a different message, by design, FN-006; key `login.otp.error.domainNotEnabled`, shown by `login.js`) *(Amended 2026-10-21, doc drift check against the code: the earlier text quoted "Not enabled for this project", which the page never shows)* | No *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: while the unit has password sign-in off these two do not apply to an account without Cognito; a Cognito account in that unit keeps them as written: it gets the neutral "Code sent" with no mail only when its switch is off or its domain is excluded)* |
| Eligible account | "Code sent" | Yes |
| Eligible when asked, ineligible when the code is entered | "Invalid or expired" | n/a |

> The domain case is deliberately not neutral: design section 4 and FN-006 answer it with `domainNotEnabled`. The login *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: while the unit has password sign-in off and the code key is valid, this case does not arise for an account without Cognito: the switch and the exclusion do not apply to it, and step 4 admits the request)*
> page only offers the option when the switch is on and the domain is not excluded, so a user normally cannot reach it.

**Support may say:** "If that address is registered for this project and its domain is enabled, a code arrives within a few
minutes. Check spam. You can ask again after 30 seconds, up to 5 times in 15 minutes. A code lasts 5 minutes and allows
3 attempts." They may also tell the user to use the password or CGIAR sign-in they used before. *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: only when the unit has password sign-in on; with it off the password is refused, and the answer is to use the code. Also, "its domain is enabled" is the allow-list wording that T21 removed: since 2026-10-15 the unit has a switch and an exclusion list, and with password sign-in off an account without Cognito gets the code whatever they say)*

**Support must not say:** whether the address exists, is active, belongs to the unit, is a CGIAR account, or why nothing
arrived; and must not read out a `neutral` log line. To fix an account (add a membership, reactivate) support acts in the admin
screens and tells the user only that it was done, **replying only to the registered mailbox**, not to whoever contacted support; otherwise the reply shows the account was not eligible before. Never tell a user about an `internal_error`.

## A7. Accepted risks

| Risk | Effect | Source |
|---|---|---|
| **Clock skew between instances** | Cooldown and expiry use each instance's UTC clock. A skew can delay acceptance of a request by the skew. The JVM time zone does not matter, but see Part B (DST gap) | T04 review |
| **Purge deadlock** | There are two purges. The rate-limit purge runs at most every 10 minutes per instance (`OtpRateLimiter.PURGE_INTERVAL`); the challenge purge (`OtpRequestCodeAction.purge`, `challengeManager.purgeExpired`) runs on every request that issues a code, in its own transaction after the issue. Either can deadlock with a request in the same moment. The request answers **`unavailable`** (fail closed), never a wrong answer. Not reproduced in two runs *(Amended 2026-10-21, doc drift check against the code: the earlier text described one purge throttled to 10 minutes; only the rate-limit purge is throttled)* | T04 review, T15 |
| **Session failure after the mail** | If the session write fails after the mail is handed off, the answer is `unavailable` though a code was mailed. Retrying supersedes it | T10 |
| **Post-login failure gap** | A `RuntimeException` after `Subject.login`, or the unmapped-unit-type branch of `finishLogin`, answers `unavailable` or `failed` but **leaves an authenticated session**. Reproduced in T15. It is **shared with Cognito route C**. A `logout()` on any failure after login would cover both flows; it belongs to a separate task | T11, T15 |
| **"Log in" stays enabled while a code request is in flight** | A password attempt can start meanwhile. A late `sent` is dropped if the step changed or a password attempt is still running (`currentStep !== stepAtStart || isSubmitting`, `login.js`); only otherwise does it open the code step. The page stays consistent and the mail really was sent *(Amended 2026-10-21, doc drift check against the code: the earlier text said the late `sent` always opened the code step; observed in T15 before the guard)* | T15 |
| **Rate-limit scope** | The limits and the cooldown are in the database, so **every instance shares them** (DD-8). What is per instance is the **pending challenge in the Shiro session**, which depends on affinity (P-4), and the purge throttle | DD-8, P-4 |
| **Code in `email_logs`** | The code email's row stores the full body, code included, like every MARLO email. Anyone who can read `email_logs` (super admins, database, replica or backup access) can sign in as that user while the code is live: at most 5 minutes, until it is used or replaced, and within the attempt limits. A deviation from guide §3.2, accepted by the product owner | Requirements Decision Log 2026-10-15 |
| **Code in non-production logs** | Outside production every code email also writes `auth.otp.code gu=<acronym> to=<address> code=<code>` at INFO. Anyone reading a test or local log can sign in as that test user while the code is live (5 minutes). A deviation from guide §3.11 for non-production only; in production the line never exists | Requirements Decision Log 2026-10-15 |
| **Window edge** | A fixed window can admit up to 2x a limit across its edge | Requirements Decision Log |
| **Victim lockout** | Per-email buckets let someone exhaust another person's 5 requests; other sign-in routes are unaffected | Design section 16 *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: in a Global Unit that has password sign-in off the code is the only method of an account without Cognito, so a locked bucket leaves that user with **no way in** for the lockout window. Not decided by that spec; it is recorded in its runbook, section 7, for the product owner)* |
| **Cognito cache lag** *(Restored 2026-10-18, validation V-3)* | On a unit with password sign-in off, `OtpEligibility.eligibilityBasis` reads Cognito state through the **cached** `CognitoAuthSpecificity`. After a super admin turns Cognito on for that unit, other instances can keep the old value for up to about an hour (query cache 3600 s, entity caches 5000 s) and treat a CGIAR account as non-Cognito, so the password-off override issues it a code. The code switch and the exclusion are not affected: they use the uncached read (DD-12). Mitigation: restart the other instances, or wait for the cache to expire. Accepted, consistent with `CHG-PWD-SWITCH-001` DD-4, which rejected an uncached fourth reader of the Cognito flag | `CHG-PWD-SWITCH-001` DD-4; validation V-3 |
| **Deliverability** | Unproven until the pilot | Design section 16 |
| `internal_error` on delivery lines | Lies outside the closed outcome set; tell log dashboards | T05 |

## A8. Not verified locally

| Not verified | Why | Who closes it |
|---|---|---|
| Live Cognito hand-off from the login page | Cognito is not configured on the local stack | Kenji, on test |
| reCAPTCHA overlap (a code entered while the widget shows) | Needs the Google widget | Kenji, on test |
| Screen-reader announcement | Attributes are present (`role=status aria-live=polite`, `role=alert`); the announcement itself was not tested | QA |
| Visual identity of password-only and Cognito units against a HEAD build | Code-level identity only | QA |
| Mail through the real relay | The local relay refuses external domains | Infra (A4) |

## A9. Open item: the password-login gap

`finishLogin` (gate 4) and the session interceptors use the **activity-blind `existCrpUser`**, which ignores
`crp_users.is_active`. A member removed through MARLO's soft delete can still use the password route. The code route
refuses them (a deliberate departure from password-login parity). This is **out of scope** and
**tracked separately as A2-2637** (epic Bugs 2026). Verified live on a throwaway copy: a removed member still signs in with a
password, and keeps edit rights while an old session is open or while roles survive the removal.

---

# Part B. Rollout checklist

**Rule:** every row has an owner; its Blocks column says what it gates. "Settled" means the answer is written next
to the row, with its source. Nothing here has been settled by infra yet.

## B1. Premises and open questions

| Id | What must be settled | Owner | Blocks | State (2026-10-13) |
|---|---|---|---|---|
| **P-4** | Production routes consecutive requests of a browser to the instance that holds its Shiro session. If false, verify finds no pending challenge (`mismatch` on a correct code) | Kenji, with infra | **Go-live** | Open. Code part confirmed in the spec (`ShiroSpringStartupListener.java:51`); the load-balancer stickiness is not. Cognito already relies on the same affinity |
| **P-6** | A reverse proxy or load balancer fronts Tomcat in Production and test, and which hop supplies the client IP. If one exists and `OTP_TRUSTED_PROXIES` is unset, all users share one bucket | Kenji, with infra | **Go-live** | Open. Nothing in the repo; `docs/infrastructure.md` calls it "implied" |
| **P-25** | Production RDS is MySQL 8.0 InnoDB (DD-3 locking assumptions) | Kenji, with infra | **Go-live** | Open. Local is 8.0.43, `REPEATABLE-READ` (P-15, T15) |
| P-18 | `otp.hmac.secret` reaches the app from `marlo-<profile>.properties` or the `OTP_HMAC_SECRET` environment variable (amended 2026-10-16) | Kenji | Go-live, per environment | **Proven locally** (T15 and the walk). Per environment: confirm no `auth.otp.keys outcome=unconfigured` line after start |
| **OQ-1** | Pilot Global Units and domains | Product owner | **Go-live** (nothing is enabled without it) | Open. All units ship off |
| **OQ-2** | Baseline-role exposure for the pilot users (below) | Kenji + PMU | **Go-live** | Open. Procedure in B2 |
| **OQ-3** | Does the A2-2629 owner accept a non-Cognito path under the epic? | A2-2629 owner | **Go-live** | Open. Default: proceed per A2-2631 |
| OQ-4 | `is_cgiar_user = 1`: refuse always or only where Cognito is active | Kenji | Not blocking | **Superseded 2026-10-15 (T21):** `is_cgiar_user` plays no part; each unit's exclusion list decides *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: `is_cgiar_user` is read again, only while the unit has password sign-in off, to keep a Cognito account under the code switch and the exclusion as written)* |
| OQ-5 | Outside production, does the code go to the real recipient or the support redirect? | Kenji + QA | Resolved | **Resolved 2026-10-15:** outside production the code goes only to the support mailbox, as every MARLO email does (T19). UAT testers read their code there |
| Relay | The production relay delivers to **external** domains (A4) | Kenji, with infra | **Go-live** | Open. The local gateway refuses them |
| Sender | SPF, DKIM, DMARC aligned for the sender; partner IT allow-listing | Kenji, with infra | Pilot | Open |
| JVM time zone | Production JVM is UTC, or `hibernate.jdbc.time_zone=UTC` is set. Reads convert through the JVM zone; in a DST zone a UTC wall time in a spring-forward gap reads back one hour off | Kenji, with infra | Go-live (low) | Open. Tokyo and `-05` passes were green (T15) |
| Password gap | Filed as A2-2637 (A9) | Kenji | Not this release | Filed, key in the requirements Decision Log |

## B2. OQ-2: Guest-exposure audit

For **each pilot user**, before the unit is enabled, as a session with no permanent table named `user_permission`
(none exists on the local copy; the procedure runs `DROP TABLE IF EXISTS user_permission` and fills a temporary one):

```sql
CALL getPermissions(<user_id>);
SELECT ro_acronym, crp_acronym, permission FROM user_permission ORDER BY crp_acronym, ro_acronym;
```

Check that every row is one the person should hold after proving only that they control a mailbox. The expected baseline
is the unit's Guest role `G`. Anything above it is a finding to resolve **before** the unit is enabled. Local sample: the
test user in the copy has one role (`G`) and two permission rows in AICCRA. The audit sample is not a production result.

## B3. Database

- [ ] On **staging and production**, run `SHOW CREATE TABLE otp_challenges, otp_cooldowns, otp_rate_limits` (one statement per table) and compare with the migration.
- [ ] Check **by hand** that all three migrations are in `flyway_schema_history` with success = 1 (guide W16: do not assume the deploy applied them): `V2_6_0_20261009_1025__AddEmailOtpLogin`, `V2_6_0_20261015_1418__ReplaceOtpAllowListWithSwitchAndExclusion` (T21) and `V2_6_0_20261016_1525__AddPasswordLoginSwitch` (`CHG-PWD-SWITCH-001`).
- [ ] After T21: querying `parameters` for `system_otp_login_active` and `system_otp_excluded_email_domains` returns one row per type 1 to 4, off and empty. Their current descriptions are the ones `V2_6_0_20261016_1525__AddPasswordLoginSwitch.sql` wrote (`UPDATE parameters SET description`), which reworded the DA-002 text *(Amended 2026-10-21, doc drift check against the code: the earlier text named the DA-002 descriptions)*. No `crp_otp_allowed_email_domains` row is left.
- [ ] No unit has the switch on before the pilot (B5).

## B4. Per environment

- [ ] `otp.hmac.secret` set in the environment's properties or as `OTP_HMAC_SECRET` (A2), and **no** `unconfigured` WARN after start. Not set in production before the release.
- [ ] `OTP_TRUSTED_PROXIES` set from the P-6 answer (A3), no `entries_ignored` WARN.
- [ ] SMTP relay delivers to an external test mailbox (A4).
- [ ] P-4, P-6, P-25 answers written next to B1.
- [ ] OQ-2 audit done for every pilot user (B2).
- [ ] On test, a code request reaches the support mailbox with the `(TESTING ENVIRONMENT)` prefix, and the test user's mailbox receives nothing (FN-018).
- [ ] Production has `marlo.production=true`; otherwise its users' codes go to the support mailbox **and are written to the log**. After the first code request in production, confirm no `auth.otp.code` line exists.

## B5. Pilot and expansion (DD-12, design section 14)

Rollout after T21 (requirements Decision Log 2026-10-15). Both stages are configuration only (A1); never use SQL.

**Stage 1: external users only.**
- [ ] On AICCRA, set `system_otp_excluded_email_domains` to `cgiar.org`.
- [ ] Turn `system_otp_login_active` on.
- [ ] Walk the acceptance flow with a test mailbox: the code arrives, sign-in works, three wrong codes, resend, expiry.
- [ ] Confirm that a CGIAR account still sees only "Sign in with CGIAR".
- [ ] Watch `auth.otp.delivery outcome=email_failed` and `auth.otp.request outcome=rate_limited` for the first days.

**Stage 2: CGIAR accounts may choose the code.**
- [x] OQ-9 approved on 2026-10-15 (requirements Decision Log).
- [ ] Re-run the CGIAR account count on production (the query in the OQ-9 Decision Log entry's terms: active members by `@cgiar.org` and `is_cgiar_user`).
- [ ] Revisit the per-IP limit (20 requests per 15 min) with the P-6 answer, since CGIAR offices may share one egress IP.
- [ ] Empty the exclusion list.

**To stop:** turn the switch off. It applies on the next request. *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: this stops code sign-in only in a unit that has password sign-in on)*

---

# Part C. Walk record

*(Historical record. The walk is dated 2026-10-13, before T21 replaced the allow-list on 2026-10-15; the steps below name the old key and the old value-based enable and disable. Superseded by T21, 2026-10-15: see A1 and B5 for the current procedure and the T21 re-runs in `probes/README.md`.)*

Walked on the local stack, as written in Part A, on **2026-10-13**. Branch `A2-2631-Implement-Center-Email-OTP-Authentication`.

**Setup.** `aiccradb_actsave` was cloned (`mysqldump --single-transaction`) into a fresh `aiccradb_otp_probe` (619 tables each).
The app was pointed at the copy with environment variables only (`MYSQL_DATABASE`), with a fresh `OTP_HMAC_SECRET` and
`EMAIL_HOST`/`EMAIL_PORT` (and the backup pair) at a local sink on `127.0.0.1:2525`. In the copy only, the test user's address
was set to `probe.user@outlook.com`. A clean build by `scripts/run-marlo-java17.sh` preceded the run.

**Proof the app was on the copy, before any write.** `ps eww` of the JVM listening on 8080 showed
`MYSQL_DATABASE=aiccradb_otp_probe`, the sink variables and the presence of the secret (value not printed). After the first
sign-in the copy's newest `users.last_login` moved to `2026-10-13 15:29:46` while `aiccradb_actsave` still read `12:56:27`.

| Step | Action | Result |
|---|---|---|
| 0 | Start state: all four OTP values empty, `crp_otp_allowed_email_domains` has no `custom_parameters` row | Off, as shipped |
| 1 | **Enable through the admin UI.** Local super-admin login, System Admin > PTF Parameters, AICCRA > Specificities > search `otp`, value `outlook.com`, Save | Row `crp_otp_allowed_email_domains` found *(Corrected 2026-10-21, doc drift check against the code: the row name was misspelt `system_otp_allowed_email_domains`)*. The database then held a row for AICCRA (45) with `outlook.com` and blank rows for the other three units |
| 2 | Next request: `crpByEmail` for the test user | `otpEnabled: true`. **No restart** |
| 3 | Login page, test user's address, project AICCRA | "Email me a sign-in code" shown beside "Log in" |
| 4 | Click "Email me a sign-in code" | Log: `auth.otp.request outcome=sent gu=aiccra domain=outlook.com ms=90`, then `auth.otp.delivery outcome=delivered ... ms=120`. The sink held one mail, subject "Your MARLO sign-in code", to the test address |
| 5 | Enter the 6-digit code from the sink, Enter | `auth.otp.verify outcome=ok ... ms=78`. Redirected to the AICCRA dashboard |
| 6 | Sign out. Sign in as super admin. **Disable through the admin UI:** clear the value, Save | The database row for AICCRA held an empty value, `is_active = 1` |
| 7 | Next request: `crpByEmail` for the test user | **`otpEnabled: false`** |
| 8 | Next request: `otpRequestCode` for the test user | **`{"status":"domainNotEnabled"}`**. Log: `auth.otp.request outcome=denied_domain gu=aiccra`. **No restart**: the JVM pid was unchanged from step 2 to the end of the falsifier |

## C2. Falsifier: disable through raw SQL (DD-12)

On the copy, same running app, no restart (pid 61085 before and after):

| Action | `crpByEmail otpEnabled` | `otpRequestCode` for an `@outlook.com` address |
|---|---|---|
| `UPDATE custom_parameters SET value = 'outlook.com'` on the AICCRA row | `true` | `sent` (neutral; the address is unknown, log `outcome=neutral`) |
| `UPDATE custom_parameters SET value = ''` on the same row | **`false`** | **`domainNotEnabled`** |

**Result: the falsifier did not break the claim.** A raw SQL change applies on the next request with no restart, so the DD-12
claim in A1 stands. (A first scripted attempt ran with a quoting error, changed nothing, and was discarded.)

## C3. Notes from the walk

- The per-email limit (5 per 15 minutes) and the 30 s cooldown applied to every request to the test address, including refused
  ones, so later checks used other addresses on the same domain. This is the behaviour A5 describes.
- `rate_limited` lines carry an empty `gu=`, as A5 says.
- The login page keeps a browser-saved address in the field; type over it when walking.

**Cleanup.** The copy was dropped, the app and the sink were stopped, and the mbox and the environment files were deleted.
`marlo-dev.properties` was not changed (hash identical before and after). `aiccradb_actsave` after the walk: `otp_*` tables
0 / 0 / 0 rows, 0 OTP `custom_parameters`, 1623 in total, user 3862 `last_login` `2026-10-13 12:56:27`.

---

# Part D. Pending shared-file items for /akili-archive on staging

On this spec branch these files are **not edited** (root `CLAUDE.md`, Default Branch and Shared-File Write Discipline).
They are recorded here and applied on `staging`.

| File | Section | Change to apply |
|---|---|---|
| `docs/trd/trd.md` | **8.1 Authentication** | Add the Center email one-time-code route as a second non-CGIAR sign-in path beside Cognito. Pre-registered members only: eligibility requires an **active** membership (`crp_users.is_active = 1`). Challenge handle in the Shiro session. `OtpAuthenticationToken` accepted by `APCustomRealm` with no I/O. The session sequence matches Cognito's. Per-unit specificities `system_otp_login_active` (the switch) and `system_otp_excluded_email_domains` (the exclusion), both read uncached (DD-12); they replace the removed `crp_otp_allowed_email_domains`. There is no CGIAR rule: a CGIAR account may choose the code unless its domain is excluded. Password-off override (`CHG-PWD-SWITCH-001`): while a unit has `system_password_login_active` off and the code key is valid, the code is the default method for every account without Cognito, whatever the switch and the exclusion say. An account that uses Sign in with CGIAR is not admitted by that override; it gets the code only when the switch is on and its domain is not excluded. The code endpoints accept POST only. Secret `OTP_HMAC_SECRET`. Known gap shared with Cognito route C (A7) |
| `docs/trd/trd.md` | **9.5 Rate limiting** | Add a bullet for the OTP sign-in endpoints: they are limited in the database (`otp_rate_limits`, `otp_cooldowns`), shared by every instance: request 5 per email and 20 per IP per 15 minutes, 30 s cooldown; verify 10 per email and 50 per IP per 15 minutes; 3 attempts per code; 5-minute expiry. The REST statement is unchanged |
| `docs/infrastructure.md` | **6 Local Environment** | Document `otp.hmac.secret` (`openssl rand -base64 48`, at least 32 decoded bytes, in `marlo-<profile>.properties` or as `OTP_HMAC_SECRET`, which takes precedence; feature off without it) and `OTP_TRUSTED_PROXIES` (comma separated IPv4/IPv6 literals or CIDR, no link-local). Optional `otp.smtp.timeout.ms`. Add to the known local-versus-governed gaps that the local relay refuses non-CGIAR recipients and a local SMTP sink is needed to test. Mention the `MYSQL_DATABASE` and `EMAIL_*` environment overrides used to run against a throwaway copy |
