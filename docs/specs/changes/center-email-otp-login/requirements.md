# Center Email OTP Login — Requirements

**Spec ID:** `CHG-OTP-LOGIN-001`
**Status:** Approved (2026-10-09)
**Owner:** IBD Team — Alliance of Bioversity International and CIAT (Kenji Tanaka)
**Reviewers:** Tech lead, QA lead, PMU lead
**Last Updated:** 2026-10-17
**Depth:** Full — new authentication path, migration, security invariants
**Approval Mode:** `gated` (inherited from `proposal.md`)
**Jira:** A2-2631 (parent epic A2-2629)
**Source intent:** [`proposal.md`](./proposal.md) (approved 2026-10-08); portable guide `CENTER_EMAIL_OTP_IMPLEMENTATION_SPEC.md` (cited as *guide §n*)
**Related PRD sections:** `docs/prd.md` §3 (Personas), §7.2 (Quality and security acceptance)
**Related UX/UI Design sections:** `docs/ux-ui/design.md` §7 (Design Tokens), §10 (Accessibility), §11 (Light theme only). The login screen has no §4 Screen Inventory entry; adding one is out of scope, as it was for `CHG-COGNITO-AUTH-001`
**Related TRD sections:** `docs/trd/trd.md` §8.1–8.2 (Authentication, Authorization layers), §9.5 (Rate limiting), §5.4 (Notifications and emails), §3.7 (Specificity tables)
**Related infrastructure:** `docs/infrastructure.md` §2 (session store), §6 (Local Environment: new environment variable)
**Companion ai-context docs:** `reports/ai-context/struts-critical-routing-catalog.md`, `reports/ai-context/interceptor-validator-playbook.md`
**Related specs:** `docs/specs/archive/2026-09-07-changes--migrate-ad-authentication-to-cognito--auth-flow/` (session-establishment pattern reused); `docs/specs/changes/migrate-ad-authentication-to-cognito/family.md` child 3 `directory-retirement` (pending; shares files). Amended by `docs/specs/archive/2026-10-18-changes--password-login-switch/` (`CHG-PWD-SWITCH-001`, 2026-10-17): FN-001, FN-003, FN-005, FN-006, FN-015, FN-019, SEC-009, DA-002, UI-001 and UI-006, plus the Executive Summary, the Glossary and AC-1; see the Decision Log entry of 2026-10-17.

---

## Executive Summary

A **pre-registered** MARLO user of a Global Unit that has switched code sign-in on, and whose email domain that unit has not excluded (amended 2026-10-15, T21), or of a Global Unit that has turned password sign-in off and who does not use Sign in with CGIAR (amended 2026-10-17, `CHG-PWD-SWITCH-001`),
can sign in with a **6-digit code emailed to them**, with no password. The code is valid for 5
minutes, allows 3 wrong attempts and works once. A verified code produces **exactly the session a
password login produces**: same `finishLogin` tail (membership gate 4, roles, custom parameters and landing page). The OTP eligibility check before it is stricter on one point: it requires an *active* membership (Decision Log, 2026-10-13).

The capability is **off by default** and is switched on per Global Unit by the boolean `system_otp_login_active`, with an optional list of excluded email domains. Turning the switch off disables it without a deploy. Password and Cognito logins do not change, except in a Global Unit that turns password sign-in off with `system_password_login_active`: there the code replaces the password for every account that does not use Cognito. *(Corrected 2026-10-17, `CHG-PWD-SWITCH-001`: this paragraph said "is enabled per Global Unit by listing domains" and "Emptying the list switches it off", the allow-list that T21 removed on 2026-10-15; and "Password and Cognito logins do not change", which the password switch qualifies.)*

What the spec adds to the guide for MARLO:
- in production the code never reaches any log; outside production only the one `auth.otp.code` line carries it (deviation from guide §3.11, non-production only). Its only stored copy is the body of the code email's `email_logs` row, readable by super admins and by anyone with database, replica or backup access (deviation from guide §3.2). Both are user decisions of 2026-10-15;
- login is always scoped to a Global Unit;
- rate limits hold across every Production instance;
- CGIAR accounts routed to Cognito cannot use the code to bypass it. *(Amended 2026-10-15, T21: SEC-009 now lets a CGIAR account choose the code unless its domain is excluded. Amended 2026-10-17, `CHG-PWD-SWITCH-001`: in a Global Unit with password sign-in off, the override does not admit a Cognito account: it keeps the code switch and the exclusion as written and is refused a code, as in the first sentence, only when its switch is off or its domain is excluded, FN-005 of that spec.)*

---

## Glossary

| Term | Meaning |
|---|---|
| **Exclusion list** | The email domains that never receive a code in a Global Unit (`system_otp_excluded_email_domains`). Empty excludes no one. Replaces the allow-list *(amended 2026-10-15, T21)* |
| **Eligible user** | An account that is active, has an active `crp_users` membership in the selected Global Unit, and has an email domain the unit has not excluded, in a unit whose switch is on. Being a CGIAR account plays no part (amended 2026-10-15, T21) *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: in a Global Unit that has password sign-in off and a valid code key, the switch and the exclusion do not apply to an account without Cognito in that unit, and a Cognito account in that unit is not admitted by that override: it is eligible only when the switch is on and its domain is not excluded.)* |
| **Challenge** | One issued code, with its expiry, attempt count and consumed state |
| **Request code** | The user action that may issue a challenge and an email |
| **Verify code** | The user action that submits a code against the current challenge |
| **Neutral response** | The response returned for every request-code outcome other than a disabled domain, rate limiting or an internal failure. It is identical whether or not a code was sent |
| **Supersede** | Invalidating every earlier open challenge for the same email when a new one is issued |

---

## 1. Overview

This spec implements A2-2631: email one-time-code authentication, adapted to MARLO's Struts and
Shiro login, Global Unit model and mailer. It follows the approved proposal, Option B: Struts-native,
with the challenge handle in the server session.

## 2. Problem Statement

Staff of partner organisations must hold a MARLO-managed password to sign in. `LoginAction`'s local
branch calls `userManager.login(userEmail, password)` (`LoginAction.java`, `execute()` local branch).
The only alternative is Cognito, which today covers only CGIAR identities (`APCustomRealm.java:118`;
`CognitoAuthSpecificity.java:64-77`).

A2-2629's goal is that users can *"access MARLO securely without depending on CGIAR identity
services"*. A mailbox at a trusted organisation is accepted as sufficient proof of identity for
**authentication only** (guide §1.2, §29).

Current behavior this spec builds on:

| # | Fact | Evidence |
|---|---|---|
| C1 | Login steps: email → `crpByEmail.do` lists the account's Global Units → the user selects one → password or Cognito | `loginForm.ftl:32-74`; `login.js:9-19` |
| C2 | Every path ends in `finishLogin`. It requires a selected Global Unit and `crp_users` membership, then loads `custom_parameters` into the session | `LoginAction.java:417-458` |
| C3 | The external-proof session sequence already exists: stop session → `Subject.login(token)` → reset request session cache → rebind `ActionContext` → `finishLogin` | `CognitoCallbackAction.java:496-555` |
| C4 | `SendMailS.send()` persists the message body in `email_log` and sends on an unobserved thread | `SendMailS.java:394`, `:479` |
| C5 | `SendMailS` drops mail when a Global Unit's notifications are off, and rewrites recipients outside production | `SendMailS.java:314`, `:404-420` |
| C6 | No application-level rate limiting exists; Production is multi-instance | `docs/trd/trd.md` §9.5, §1.2 |
| C7 | No code in `marlo-web` or `marlo-data` reads the client IP (`getRemoteAddr` / `X-Forwarded-For`: 0 hits, run 2026-10-08) | `grep -rn "X-Forwarded-For\|getRemoteAddr" marlo-web/src/main/java marlo-data/src/main/java` |
| C8 | `crpByEmail.do` reveals account existence, name and memberships before authentication | `CrpByUserEmailAction.java:93-137` |
| C9 | The realm refuses inactive users for the password path | `APCustomRealm.java:169` (`if (user.isActive())`) |

---

## 3. In-Scope Requirements

### 3.1 Functional

#### Requirement: Per-Global-Unit enablement

- **CHG-OTP-LOGIN-001-FN-001** — Code sign-in MUST be enabled per Global Unit by the boolean specificity `system_otp_login_active`, off by default. Off or absent MUST disable it for that Global Unit, both in the UI and on the server. It replaces the domain allow-list `crp_otp_allowed_email_domains`. *(Amended 2026-10-15, T21.)* *(Amended 2026-10-17, `CHG-PWD-SWITCH-001` FN-004: "Off or absent MUST disable it" holds only while password sign-in of G is on. While G has password sign-in off and the code key is valid, the code is offered to every account without Cognito in G, whatever this switch says. A Cognito account in G keeps this switch as written.)*
- **CHG-OTP-LOGIN-001-FN-002** — A change to a Global Unit's switch or exclusion list MUST take effect for new requests without a deploy or restart. *(Amended 2026-10-15, T21.)*

##### Scenario: Feature off

- GIVEN Global Unit G has `system_otp_login_active` off
- WHEN a user selects G on the login page
- THEN no code sign-in option is shown
- AND a request-code call addressed to G directly is refused, with no challenge and no email
- BUT it must NOT change the password or Cognito step for G
- *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: this scenario holds while password sign-in of G is on. With it off, a request-code call for an account without Cognito in G is accepted although this switch is off.)*

##### Scenario: Live change

- GIVEN G's switch is turned on, with an empty exclusion list
- WHEN the next login page for G is loaded
- THEN the option is offered to every eligible user of G, whatever the email domain, with no restart

#### Requirement: Domain exclusion, exact matching

- **CHG-OTP-LOGIN-001-FN-003** — A Global Unit MAY exclude email domains from code sign-in through `system_otp_excluded_email_domains` (comma-separated, empty by default). An account MUST NOT get a code when the domain of its normalised email (trimmed, lower-cased, the part after the single `@`) **equals exactly** a normalised exclusion entry. Normalising an entry trims it, lower-cases it, strips one leading `@`, drops it when empty or still containing `@`, and de-duplicates. Suffix, substring and sub-domain matches MUST NOT count: a sub-domain is excluded only when listed itself. An email containing any character outside printable ASCII MUST be rejected, and such an entry dropped. No domain, `cgiar.org` included, is fixed in code. *(Amended 2026-10-15, T21.)* *Previously an allow-list with the same matching rules; the ASCII rule dates from 2026-10-13 (T05).* *(Amended 2026-10-17, `CHG-PWD-SWITCH-001` FN-004, FN-005: while G has password sign-in off and the code key is valid, the exclusion applies only to Cognito accounts in G; an account without Cognito in G gets the code even when its domain is excluded.)*

##### Scenario: Lookalike rejected

- GIVEN G's switch is on and its exclusion list is `excluded.org`
- WHEN codes are requested for `x@attackerexcluded.org`, `x@excluded.org.evil.com` and `x@sub.excluded.org`
- THEN none of them is excluded
- AND IT MUST exclude `  X@EXCLUDED.ORG ` as `x@excluded.org`

#### Requirement: Request a code

- **CHG-OTP-LOGIN-001-FN-004** — When an **eligible user** requests a code for Global Unit G, the system MUST create exactly one challenge bound to that email and G, and send exactly one email containing the code.
- **CHG-OTP-LOGIN-001-FN-005** — When G's switch is on and the domain is not excluded, but the account is not eligible (unknown, inactive, or not a member of G; amended 2026-10-15, T21: the CGIAR refusal of SEC-009 is gone), the system MUST return the **neutral response**. It MUST NOT send an email, and any challenge state it records MUST be unable to authenticate and MUST behave, on later requests and verifications, exactly as an eligible account's challenge does (amended 2026-10-09, Judgment Day JD-1/JD-2). *(Amended 2026-10-17, `CHG-PWD-SWITCH-001` FN-004, FN-005, SEC-004: the precondition "G's switch is on and the domain is not excluded" holds while password sign-in of G is on. While it is off and the code key is valid, the precondition is that the password switch is off, and an account that is a Cognito account in G (`is_cgiar_user = 1` and `cognito_auth_active` on) is not admitted by the override: it keeps the code switch and the exclusion as written, so it is eligible only when the switch is on and its domain is not excluded; otherwise it gets this neutral response, with a decoy challenge and no email. Unknown, inactive and non-member accounts still get it.)*
- **CHG-OTP-LOGIN-001-FN-006** — When G's switch is off, or the email's domain is on G's exclusion list, the system MUST return an explicit "not enabled for this email" message that names no account state. *(Amended 2026-10-15, T21.)* *(Amended 2026-10-17, `CHG-PWD-SWITCH-001` DD-4: while G has password sign-in off and the code key is valid, step 4 no longer answers "not enabled for this email" for G, because the switch and the exclusion do not decide there; it admits the request and the eligibility check that follows decides. With password sign-in on, or without a valid code key, it is as written.)*
- **CHG-OTP-LOGIN-001-FN-007** — A new code request for the same email MUST **supersede** every earlier open challenge for that email, atomically with issuing the new one.
- **CHG-OTP-LOGIN-001-FN-008** — Consecutive code requests for the same email MUST be at least 30 seconds apart, enforced by the server. An earlier request MUST receive the rate-limited response and change nothing.

##### Scenario: Eligible user receives a code

- GIVEN `ana@partner.org` is active and a member of G, G's switch is on, and `partner.org` is not on G's exclusion list
- WHEN she requests a code for G
- THEN one challenge exists for her and G
- AND one email with a 6-digit code reaches `ana@partner.org`
- AND the page shows the code step with the masked destination `a***@partner.org`

##### Scenario: Ineligible account gets the neutral response

- GIVEN `bob@partner.org` exists but is not a member of G (or is inactive, or does not exist)
- WHEN a code is requested for him on G
- THEN the response is byte-for-byte the same page state as in the eligible scenario, apart from the masked email it echoes
- BUT it must NOT send an email, or record any challenge that can authenticate
- AND IT MUST answer a later resend and any later verification exactly as for an eligible account (same statuses, same attempt and expiry behavior)
- AND IT MUST NOT write any log line that distinguishes the cause where a client can see it

#### Requirement: The code

- **CHG-OTP-LOGIN-001-FN-009** — A code MUST be a 6-digit string drawn uniformly from `000000`–`999999` by a cryptographically secure generator, with leading zeros kept (guide §10).
- **CHG-OTP-LOGIN-001-FN-010** — A challenge MUST expire 5 minutes after it is issued. The server MUST enforce the expiry at verification, independently of any cleanup.

#### Requirement: Verify a code

- **CHG-OTP-LOGIN-001-FN-011** — A correct code submitted within the expiry for an open challenge MUST sign the user in to G through the same tail as a password login: the same `crp_users` gate, session attributes, `custom_parameters`, last-login update and landing-page routing.
- **CHG-OTP-LOGIN-001-FN-012** — A wrong code MUST increment the challenge's attempt count. The 3rd wrong code MUST exhaust the challenge and say so. An exhausted challenge MUST never authenticate, even with the correct code.
- **CHG-OTP-LOGIN-001-FN-013** — A challenge MUST authenticate at most once. A second submission of the code, sequential or concurrent, MUST be refused.
- **CHG-OTP-LOGIN-001-FN-014** — A challenge that is expired, superseded, consumed or exhausted MUST be refused, even when its stored row still exists.
- **CHG-OTP-LOGIN-001-FN-015** — Eligibility MUST be re-checked after the code is consumed and before the session is issued. A user deactivated, removed from G, or no longer offered the code by G (switch off or domain excluded, FN-001, FN-003) since the request MUST be refused. *(Amended 2026-10-15, T21.)* *(Amended 2026-10-17, `CHG-PWD-SWITCH-001` DD-4: the re-check applies the rule in force when the code is verified. While G has password sign-in off, a user without Cognito in G is not refused for a switch off or an excluded domain, and a Cognito account in G is refused only when its switch is off or its domain is excluded, as written. A user whose unit turned password sign-in back on since the request is refused when the switch is off or the domain is excluded, as written.)*
- **CHG-OTP-LOGIN-001-FN-016** — A verification with no challenge in the current session (none requested, or the session lost) MUST receive the same answer as a wrong code. A verification after a neutral-response request MUST answer exactly as it would for an eligible account's challenge (amended 2026-10-09, RJ-5).

##### Scenario: Successful sign-in matches a password sign-in

- GIVEN Ana holds an open challenge for G
- WHEN she submits the correct code within 5 minutes
- THEN she lands where a password login for G would land her
- AND her session carries the same user, Global Unit and `custom_parameters` keys
- AND the challenge is marked consumed
- BUT it must NOT change her roles or `crp_users` rows

##### Scenario: Attempts exhausted

- GIVEN Ana's challenge has 2 wrong attempts
- WHEN she submits a 3rd wrong code
- THEN she sees "too many attempts, request a new code"
- AND IT MUST refuse a later submission of the correct code for that challenge

##### Scenario: Concurrent replay

- GIVEN an open challenge with code `123456`
- WHEN 10 verifications of `123456` arrive at the same time
- THEN exactly one signs in
- AND the other nine are refused

#### Requirement: Email content

- **CHG-OTP-LOGIN-001-FN-017** — The email MUST state the application name, the code, the 5-minute expiry, that it works once, that it must not be shared, that it can be ignored if not requested, and the support contact. It MUST NOT contain a link that carries the code (guide §19). **Format (amended 2026-10-15, T20):** the HTML part MUST be wrapped by `EmailLayout` in the selected Global Unit's `email.layout` shell, the format MARLO's role and welcome emails use since A2-2484. The texts MUST be resolved with that Global Unit's custom i18n file (`crp_custom_file`), although no Global Unit is in the session before sign-in. A Global Unit without a shell (base `email.layout={0}`) gets the body as it is. A unit whose shell carries the support contact and the automated-notification notice MAY drop them from its body through its own keys, as the role emails do; FN-017 holds over body plus shell.
- **CHG-OTP-LOGIN-001-FN-018** — The code email MUST be delivered even when the Global Unit's email notifications are disabled. Outside production it MUST follow the same rule as every other MARLO email (OQ-5, resolved 2026-10-15):
  - "outside production" means `APConfig.isProduction()` is false, which includes an unset `marlo.production`;
  - the only recipient is the support mailbox `email.notification`, never the submitted address. If that mailbox is blank or not a single valid address, the email is not sent, counts as failed, and is never sent to the submitted address instead;
  - the subject starts with `(TESTING ENVIRONMENT) `, the prefix `SendMailS.send` uses;
  - the plain and HTML bodies start with a header naming the submitted address, which `SendMailS.send` also prints for the production recipients;
  - in production, nothing changes: the only recipient is the submitted address, and there is no prefix and no header.

#### Requirement: Existing logins unaffected

- **CHG-OTP-LOGIN-001-FN-019** — Password login and Cognito login MUST behave exactly as before for every Global Unit, whether or not code sign-in is enabled. *(Amended 2026-10-17, `CHG-PWD-SWITCH-001` SEC-001: "exactly as before" holds for every Global Unit that has password sign-in on, which is the default. A Global Unit that turns it off refuses the password on the login page; Cognito login still behaves as before.)*

### 3.2 Security

- **CHG-OTP-LOGIN-001-SEC-001** — The code MUST NOT be stored in clear text in the challenge store, application logs, the session, or error messages. Only a keyed MAC bound to the challenge may be stored there (guide §11). **One accepted exception (2026-10-15, Decision Log):** the body of the code email's own `email_logs` row (OPS-005) holds the email exactly as MARLO stores every other email, code included.
- **CHG-OTP-LOGIN-001-SEC-002** — The challenge store MUST NOT hold the email in clear text. A keyed MAC of the normalised email is allowed.
- **CHG-OTP-LOGIN-001-SEC-003** — Every comparison of a MAC or a code MUST run in constant time and MUST NOT throw on malformed input.
- **CHG-OTP-LOGIN-001-SEC-004** — A challenge MUST be usable only by the session that requested it, for the email and Global Unit it was issued for. Nothing returned to the client may be accepted as an authenticated session.
- **CHG-OTP-LOGIN-001-SEC-005** — A successful verification MUST rotate the session, so that the pre-authentication session ID is never the authenticated one.
- **CHG-OTP-LOGIN-001-SEC-006** — Request-code and verify-code MUST add no new way to tell unknown, inactive, non-member and Cognito-routed accounts apart. The pre-existing `crpByEmail.do` exposure (C8) is out of scope and recorded as a risk.
- **CHG-OTP-LOGIN-001-SEC-007** — Code sign-in MUST NOT create users, memberships or role assignments, and MUST NOT change them. Authorization after a code sign-in MUST be identical to authorization after a password sign-in.
- **CHG-OTP-LOGIN-001-SEC-008** — The MAC keys MUST derive from a dedicated secret, at least 256 bits, delivered by the environment. When the secret is absent or too short, code sign-in MUST be unavailable for every Global Unit and MUST NOT fall back to a generated or default key.
- **CHG-OTP-LOGIN-001-SEC-009** — Eligibility for code sign-in MUST NOT depend on `is_cgiar_user`. A CGIAR account with access to G gets a code like any other account, unless its domain is on G's exclusion list (FN-003). On a Cognito-active Global Unit, this makes Cognito one of the CGIAR user's choices, not the only one. *(Amended 2026-10-15, T21.)* *Reverses the 2026-10-08 rule that refused CGIAR accounts on Cognito-active units and ignored `cgiar.org` in the list (OQ-4). Rollout stage 2 (CGIAR accounts on Cognito units) was approved on 2026-10-15 (OQ-9). It still waits for the per-IP limit review.* *(Amended 2026-10-17, `CHG-PWD-SWITCH-001` FN-004, FN-005, DD-4: "MUST NOT depend on `is_cgiar_user`" holds while password sign-in of G is on. While it is off and the code key is valid, eligibility reads whether the account is a Cognito account in G (`is_cgiar_user = 1` and `cognito_auth_active` resolves on, through `CognitoAuthSpecificity`): such an account is not admitted by the override and stays under the code switch and the exclusion as written, and every other active member is eligible whatever the switch and the exclusion say.)*
- **CHG-OTP-LOGIN-001-SEC-010** — Request-code MUST be limited per normalised email (5 per 15 min) and per client IP (20 per 15 min). Verify-code MUST be limited per email (10 per 15 min) and per IP (50 per 15 min). The limits MUST hold across every application instance, MUST be counted before any account lookup, and MUST apply identically to every account state.
- **CHG-OTP-LOGIN-001-SEC-011** — The client IP used for rate limiting MUST NOT be taken from a header an untrusted client can set (guide §17.2 item 6).

##### Scenario: Code never persisted

- GIVEN a full request-and-verify flow
- WHEN `otp_challenges`, `email_logs` and the application log are searched for the 6-digit code and the full email
- THEN the code is found in neither `otp_challenges` nor the log, and in `email_logs` only in the `message` of that email's OPS-005 row (amended 2026-10-15). Outside production the log also carries it, only in the one `auth.otp.code` line of OPS-001
- AND the full email is found in neither `otp_challenges` nor the log, except outside production in the one `auth.otp.code` line (amended 2026-10-15); in `email_logs` it appears only in that row (amended 2026-10-14)

##### Scenario: Missing secret fails closed

- GIVEN the OTP secret is absent or invalid in both `OTP_HMAC_SECRET` and `otp.hmac.secret` *(Amended 2026-10-18, validation V-7: it read "not set in the environment", which ignored the properties file allowed on 2026-10-16)*
- WHEN any Global Unit with its switch on loads the login page
- THEN no code sign-in option is shown, and request-code is refused
- BUT it must NOT prevent the application from starting, or affect password and Cognito logins

### 3.3 Operations

- **CHG-OTP-LOGIN-001-OPS-001** — Each request-code and verify-code call MUST emit exactly one structured log line: event name, outcome from a closed set, Global Unit acronym, email domain and duration. Each email handed to delivery MUST additionally emit exactly one delivery-outcome line. No line may contain the code, the nonce, the full email or the session ID (amended 2026-10-09, JI-1). *(Amended 2026-10-18, T22: each closed outcome set gains `bad_method`, written when the call is not a POST or has no servlet request. The line carries empty `gu=` and `domain=` and nothing else about the call.)* **Exception outside production only** (`!APConfig.isProduction()`, amended 2026-10-15): for each handed-off email, one INFO line `auth.otp.code gu=<acronym> to=<submitted address> code=<code>` is written on the sender's thread after the delivery attempt, so testers and developers can read the code without a mailbox. In production that line never exists.
- **CHG-OTP-LOGIN-001-OPS-002** — An email delivery failure MUST be logged as outcome `email_failed` at WARN level. The user-facing response MUST stay the neutral one.
- **CHG-OTP-LOGIN-001-OPS-003** — Challenges past their expiry by more than 1 hour MUST be purged. A purge failure MUST NOT block sign-in.
- **CHG-OTP-LOGIN-001-OPS-004** — The spec MUST ship a runbook covering enable and disable per Global Unit, "code not received" triage, secret rotation, and the support note that ineligible accounts see "code sent" but receive nothing.
- **CHG-OTP-LOGIN-001-OPS-005** — Each code email the sender accepts into its queue MUST leave exactly one `email_logs` row once the delivery attempt ends, so support can see it in System Admin → Emails (added 2026-10-14, Decision Log):
  - the row holds the recipient, the subject, the date, the Global Unit id, `source_action = /otpRequestCode`, and whether the send succeeded;
  - `message` holds the HTML body as sent, code included, as `SendMailS` stores every MARLO email (amended 2026-10-15, user decision; SEC-001 exception). Outside production it is stored without the test header, as `SendMailS` does. The code MUST NOT appear in any other column;
  - on failure, `error_email` holds the exception class name only, never its message (it can carry the SMTP exchange);
  - no row is written for a request that sends nothing (ineligible, denied, rate-limited), and the row is written after the send, on the sender's thread, so it adds no latency to the response (SEC-006);
  - a failure to write the row is logged and never affects the send or the response;
  - a hand-off rejected before queueing (full queue, a message that cannot be built) writes no row: it is already logged as `email_failed` at once, and a write on the request thread would add latency for eligible accounts only;
  - System Admin → Emails MUST NOT resend these rows, one by one or in bulk: a resent code email would carry a code that has expired or been replaced. The answer to a failed code email is a new request.

### 3.4 Data & Migration

- **CHG-OTP-LOGIN-001-DA-001** — The challenge store MUST ship as a Flyway migration. It MUST be idempotent and MUST assume no Global Unit, user or role row exists.
- **CHG-OTP-LOGIN-001-DA-002** — The switch `system_otp_login_active` (boolean, default `false`) and the exclusion `system_otp_excluded_email_domains` (string, default empty) MUST be seeded **off** for each of the Global Unit types 1, 2, 3 and 4 that exists in the database at migration time. Type 2 is included because migration-only databases hold "Center" as id 2 (RJ-4). Both keys MUST be stored with the `system_` prefix. Their `parameters.description`, shown on the Super Admin Specificities tab, MUST be exactly (A2-2524 shape: what it does, for whom, then the catch):
  - `system_otp_login_active`: "Lets users of this Global Unit sign in on the login page with a one-time code sent to their email, as an alternative to their password or to Sign in with CGIAR. Only registered users who are active and members of this Global Unit receive a code. Turning it off removes the option on the next login, and users whose email domain is listed in system_otp_excluded_email_domains do not receive a code. Exception: while system_password_login_active is off and the server has a code key, everyone who does not use Sign in with CGIAR signs in with the code, whatever this setting and the exclusion say."
  - `system_otp_excluded_email_domains`: "Email domains that cannot sign in with a one-time code in this Global Unit, even when system_otp_login_active is on. Write exact domains separated by commas, for example cgiar.org,example.org. A subdomain is only excluded if it is listed itself. Leave it empty to exclude no one. Exception: while system_password_login_active is off, the exclusion applies only to users of Sign in with CGIAR."

  Their constants MUST be declared in **both** `APConstants.java` files, equal to `parameters.key`. The same migration MUST remove the `crp_otp_allowed_email_domains` rows and their `custom_parameters` values. It MUST be idempotent and MUST assume no Global Unit id. *(Amended 2026-10-15, T21.)* *(Amended 2026-10-17, `CHG-PWD-SWITCH-001` DA-003: the two descriptions above are the texts now stored; the migration `AddPasswordLoginSwitch` updates the stored descriptions to them. The T21 texts they replace were:)*
  - was `system_otp_login_active` (T21): "Lets users of this Global Unit sign in on the login page with a one-time code sent to their email, as an alternative to their password or to Sign in with CGIAR. Only registered users who are active and members of this Global Unit receive a code. Turning it off removes the option on the next login. Users whose email domain is listed in system_otp_excluded_email_domains never receive a code."
  - was `system_otp_excluded_email_domains` (T21): "Email domains that can never sign in with a one-time code in this Global Unit, even when system_otp_login_active is on. Write exact domains separated by commas, for example cgiar.org,example.org. A subdomain is only excluded if it is listed itself. Leave it empty to exclude no one."
- **CHG-OTP-LOGIN-001-DA-003** — Rate-limit state MUST be stored where every application instance reads the same values.

### 3.5 UI

- **CHG-OTP-LOGIN-001-UI-001** — The authentication step for G MUST offer "Email me a sign-in code" only when the feature is configured, G's switch is on, and the entered email's domain is not on G's exclusion list. Whether the account is CGIAR plays no part. *(Amended 2026-10-15, T21.)* *(Amended 2026-10-17, `CHG-PWD-SWITCH-001` FN-004, UI-001: while G has password sign-in off and the code key is valid, the option is offered to an account without Cognito in G whatever the switch and the exclusion say, and shown directly as the only method. For a Cognito account it is offered under the rule as written.)*
- **CHG-OTP-LOGIN-001-UI-002** — The code step MUST show these states:
  - sending;
  - code sent: neutral text with the masked destination;
  - verifying;
  - one error message per refusal class: wrong code, too many attempts, expired or invalid, too many requests, service unavailable; *(Amended 2026-10-18, T22, validation V-2: "service unavailable" has two texts. `login.otp.error.unavailable` says to try again later or use the password. On a Global Unit with password sign-in off, where the password is refused, `login.js` shows `login.otp.error.unavailablePasswordOff` instead: "Code sign-in is temporarily unavailable. Please try again in a few minutes." The server answers the same `unavailable` status in both cases.)*
  - a resend control with a visible 30-second countdown;
  - "use a different email", which returns to the email step.
- **CHG-OTP-LOGIN-001-UI-003** — The code field MUST:
  - be labelled;
  - accept digits only, with a maximum length of 6;
  - use `inputmode="numeric"` and `autocomplete="one-time-code"`;
  - receive focus when the code step opens.

  Status and errors MUST be announced through an `aria-live` region, with `role="alert"` for errors.
- **CHG-OTP-LOGIN-001-UI-004** — Buttons MUST be disabled while their request is in flight. A double click MUST send one request.
- **CHG-OTP-LOGIN-001-UI-005** — All user-facing text, in the page and in the email, MUST come from i18n keys in `global.properties`. A Global Unit's custom file MAY override the email keys, as it does for every MARLO email (amended 2026-10-15, T20).
- **CHG-OTP-LOGIN-001-UI-006** — When the authentication step for G offers the code and another method, it MUST first ask how the user wants to sign in. The other method is the password, or Cognito for a CGIAR account on a Cognito-active unit (added 2026-10-14, Decision Log; amended 2026-10-15, T21):
  - one control per method: "Email me a sign-in code" (primary), and "Continue with your password" or "Sign in with CGIAR";
  - choosing a method reveals only that method's panel; each panel offers "Choose another way to sign in", which returns to the choice;
  - the code panel names the masked destination and sends nothing until "Send code" is activated;
  - focus moves to the first control of whatever opens (the choice, a panel);
  - when a single method applies (Cognito or the password, with the code off or the domain excluded), it is shown directly, with no choice; *(Amended 2026-10-17, `CHG-PWD-SWITCH-001` UI-001, UI-002, FN-010: with password sign-in of G off, the single method of an account without Cognito is the code. It is shown directly, with the existing code panel, no password field and no "Choose another way to sign in" link. When the identifier typed in step 1 is a username, step 3 shows the "sign in with your email address" message and the way back to step 1 instead.)*
  - Enter never submits a method that is not open.

  The server rules are unchanged: the screen decides what to offer, never what to permit.

### 3.6 Non-Functional

- **CHG-OTP-LOGIN-001-NF-001** — A request-code response MUST return within 15 seconds even when the mail server does not answer. The send is bounded by a timeout, and a timeout counts as `email_failed`.
- **CHG-OTP-LOGIN-001-NF-002** — The change MUST pass the compile and Checkstyle gates (zero new violations against HEAD), and every new Java file MUST carry the GPL header.

---

## 4. Out-of-Scope

| Item | Reason |
|---|---|
| Creating accounts on first login (auto-provisioning) | Decided 2026-10-08: pre-registered users only |
| Fixing `crpByEmail.do`'s pre-auth disclosure (C8) | Separate spec, proposal OQ-7 |
| Ending live sessions on deactivation | Proposal OQ-6, tech-lead decision; no current path does it |
| Cognito-native email OTP | Proposal Option C, rejected for A2-2631 |
| An admin screen for the switch and the exclusion list | Existing `custom_parameters` administration |
| Choosing the pilot Global Units and domains | Configuration, proposal OQ-1 |
| `/api/**` REST authentication | Hard rule 3; untouched |
| Metrics dashboards and alerting | MARLO has no metrics stack; OPS-001 log lines are the signal |

---

## 5. Personas Affected

| Persona | Effect |
|---|---|
| Partner staff (project leaders, coordinators, M&E focal points on units with the switch on) | New passwordless sign-in. Their roles do not change |
| MARLO Support / Super Admin | Enable and disable per Global Unit; triage "code not received" |
| CGIAR staff on Cognito-enabled Global Units | May choose the code instead of Cognito, unless their domain is excluded (SEC-009 *(amended 2026-10-15, T21)*) |
| Local password users | No change (`FN-019`); in a Global Unit that turns password sign-in off (`CHG-PWD-SWITCH-001`) they sign in with the code instead (amended 2026-10-17) |

---

## 6. Defect classes and their gates

| Defect class | Gate that catches it | Automated? |
|---|---|---|
| Code that does not compile | Clean recompile (`marlo-verify` skill) | Yes |
| Style violations | Checkstyle jar run directly, delta against HEAD | Yes |
| Wrong normalisation, matching, code format, MAC, expiry or attempt arithmetic | JUnit unit tests (`mvn -q -o -pl marlo-web -am test`) | Yes |
| Timing side channel in a MAC or code comparison (SEC-003) | Inspection of `OtpCodes.java`: every comparison goes through `MessageDigest.isEqual`. The automated part is the malformed-input tests, which assert that a comparison never throws. **Accepted** *(Added 2026-10-18, validation V-6; before this row SEC-003 was accepted only in `test-report.md` §9)* | Partial: the no-throw half only |
| Concurrency: double consume, attempts above 3, lost supersede | Parallel probe against a **throwaway copy** of the local MySQL. No in-repo harness runs Hibernate against a real database | **No** — scripted probe at the execute HITL pause, with recorded output |
| Code or email persisted (SEC-001/002) | `SELECT` over `otp_challenges` and `email_logs` (the code only in the `message` of the OPS-005 row, the recipient expected there), plus a grep of the run's log, after a real flow | Manual at the HITL pause |
| Session tail divergence from the password login (FN-011) | Session-attribute comparison between one password login and one code login on the local stack | Manual at the HITL pause |
| Oracle leakage between account states (SEC-006) | Rendered-response comparison across the four states | Unit test for the action results, plus a manual browser check |
| UI states and accessibility | Browser walkthrough (claude-in-chrome) of every UI-002 state; DOM attribute check for UI-003 | Manual at the HITL pause. An attribute check proves presence, not screen-reader behavior; that gap is accepted |
| Rate limits across instances (SEC-010) | Unit test over the shared store, single local instance | **Partial.** Multi-instance behavior cannot be reproduced locally. Shared state holds structurally, because the store is the one database; the residual risk is accepted |
| i18n apostrophes and braces | `.properties` diff review (`marlo-verify`) | Yes |
| Unsafe migration | `marlo-migration` checklist, applied twice on a throwaway database copy | Yes, scripted |
| Deliverability (spam folder, partner filters) | Real mailbox at the pilot domain | **No** — accepted risk until pilot (proposal R-dependencies) |

---

## 7. Acceptance Criteria

| AC | Requirement | Given / When / Then |
|---|---|---|
| AC-1 | FN-001, FN-019 | Given every switch off, when the existing login tests run, then they pass unchanged and no OTP control renders *(Amended 2026-10-17, `CHG-PWD-SWITCH-001` AC-2: the criterion is run with password sign-in on everywhere; with it off in a unit, the code option renders there by design.)* |
| AC-2 | FN-003 | Given the exclusion `excluded.org`, the lookalike and normalisation inputs of the FN-003 scenario resolve as specified (unit test) (amended 2026-10-15, T21) |
| AC-3 | FN-004, FN-009, FN-017 | Given an eligible user, when a code is requested, then one challenge row exists and one email arrives containing a 6-digit code and every FN-017 element |
| AC-4 | FN-005, SEC-006 | Given each of unknown, inactive and non-member (amended 2026-10-15, T21), when a code is requested, then the result and page state equal the eligible one, no email is sent, and a later resend and verifications answer as for the eligible account; no recorded challenge can authenticate |
| AC-5 | FN-011, SEC-005, SEC-007 | Given a correct code, when verified, then the session ID differs from the pre-auth one, the session attributes match a password login for the same user and Global Unit, and the role rows are unchanged |
| AC-6 | FN-012 | Given 3 wrong codes, then the 3rd says "too many attempts" and the correct code is refused afterwards |
| AC-7 | FN-013 | Given 10 concurrent correct verifications on a database copy, then exactly 1 succeeds |
| AC-8 | FN-010, FN-014 | Given a challenge 5 min 1 s old, or superseded, when the correct code is submitted, then it is refused |
| AC-9 | FN-015 | Given the user is removed from G after requesting, when the correct code is submitted, then it is refused and the challenge is consumed |
| AC-10 | FN-007, FN-008 | Given a resend after 30 s, then the old code is refused and the new one works. A resend within 30 s returns the rate-limited response |
| AC-11 | SEC-001, SEC-002, OPS-001 | After a full flow, the code appears in neither `otp_challenges` nor the log, and in `email_logs` only in the `message` of the OPS-005 row (amended 2026-10-15). Outside production it also appears in exactly one `auth.otp.code` line per handed-off email, and in production in no log line, and the full email appears in neither `otp_challenges` nor the log, except outside production in the one `auth.otp.code` line (amended 2026-10-15) (the `email_logs` row of OPS-005 holds the recipient, as every MARLO email row does; amended 2026-10-14); exactly one `auth.otp.request` or `auth.otp.verify` line per call, plus one `auth.otp.delivery` line per handed-off email (amended 2026-10-09, RJ-7) |
| AC-12 | SEC-008 | Given no secret, the option is hidden, request-code is refused, and the application starts |
| AC-13 | SEC-009, FN-003, UI-006 | Given an `is_cgiar_user = 1` account on a Cognito-active G with the switch on: with an empty exclusion list, step 3 offers "Sign in with CGIAR" and "Email me a sign-in code", and the code signs the account in. With `cgiar.org` excluded, step 3 shows only "Sign in with CGIAR" and a direct request-code call answers not-enabled (amended 2026-10-15, T21) |
| AC-14 | SEC-010 | Given 6 requests in 15 min for one email, then the 6th is rate-limited. The per-IP limit trips across different emails |
| AC-15 | FN-018, OPS-002 | Given notifications are off for G, the code still arrives. Given SMTP is down, the response is neutral and `email_failed` is logged |
| AC-16 | UI-001–UI-006 | Browser walkthrough of every state, with the attributes present and one request per double click; for UI-006, every step-3 branch (Cognito, password-only, choice → code, choice → password, back to the choice) |
| AC-17 | DA-001, DA-002 | The migration applies twice cleanly on a throwaway copy and on a database without Global Units 45/47 |
| AC-18 | OPS-005 | After a delivered and a failed code email, each left one `email_logs` row with `source_action = /otpRequestCode`, the HTML body with its code in `message` (amended 2026-10-15), `succes_email` 1 and 0, and the code in no other column; an ineligible request left none; the failed row is listed by System Admin → Emails but resending it, by id or in bulk, sends nothing |
| AC-19 | FN-018 | Given `marlo.production=false`, when an eligible user requests a code, then the sink receives exactly one email, addressed only to `email.notification`, with the `(TESTING ENVIRONMENT) ` subject prefix, a header naming the submitted address, and a code that signs the user in; the submitted address receives nothing. Its `email_logs` row holds the submitted address in `to_email`, the support mailbox in `bbc_email` and the subject without the prefix. With `email.notification` blank, nothing is sent and the delivery line says `email_failed` |
| AC-20 | FN-017, UI-005 | Given AICCRA (whose custom file defines `email.layout`), when a code email is sent, then its HTML part is the AICCRA shell (MARLO logo, body card, band, footer) around a body with the code, resolved from AICCRA's custom keys. Its text part still carries the code and the expiry, and the images point at the instance's CDN or base URL. Given a unit without a shell, the HTML is the bare body as before |
| AC-21 | DA-002, FN-001, FN-002 | The T21 migration applies twice cleanly on a throwaway copy and on a database without Global Units 45/47. It leaves both `system_otp_*` keys seeded off and no `crp_otp_allowed_email_domains` row. Turning the switch on, and adding or removing an excluded domain, take effect on the next request |

---

## 8. Constitutional Compliance Checklist

- [x] Phase replication: not applicable. No phased data is read or written.
- [x] Save validation: not applicable to a save section. Request-code and verify-code validate input in their actions; no manager save chain on phased entities.
- [x] Permissions: the new actions run before authentication. Their interceptor stack is declared in `design.md` and must match the existing pre-auth login actions.
- [x] Specificity: the switch and the exclusion list go through `parameters` + `custom_parameters`, with constants in both `APConstants.java` files (DA-002).
- [x] Migrations: Flyway with `V<…>_<YYYYMMDD>_<HHMM>__<Description>.sql`, timestamp from the real clock (DA-001).
- [x] i18n: UI-005.
- [x] License header: NF-002.
- [x] Code style: NF-002.
- [x] REST: no `/api/*` change.
- [x] Audit: challenges are throw-away authentication state and are not audited entities (guide §9.1). No auditable entity changes.
- [x] Dependency floors: no dependency downgrade. Any new dependency is named in `design.md`.
- [x] Branching: `A2-2631-Implement-Center-Email-OTP-Authentication` from `staging`.

---

## 9. Open Questions

| ID | Question | Blocks | Default used here |
|---|---|---|---|
| OQ-1 | Pilot Global Units and domains | Rollout only | All seeded off |
| OQ-2 | Baseline-role exposure audit for pilot users (`getPermissions`) | Go-live | — |
| OQ-3 | Does the A2-2629 owner accept a non-Cognito path under the epic? | Go-live | Proceed per A2-2631 |
| OQ-4 | `is_cgiar_user = 1`: refuse always, or only where Cognito is active? | SEC-009 | **Superseded 2026-10-15 (T21):** `is_cgiar_user` plays no part; exclusion is per domain, per unit *(Amended 2026-10-17, `CHG-PWD-SWITCH-001`: `is_cgiar_user` is read again, only while the unit has password sign-in off, to keep a Cognito account under the code switch and the exclusion as written (SEC-009))* |
| OQ-9 | Does the owner of Cognito (A2-2629 owner or CGIAR IT) approve CGIAR accounts choosing the code on Cognito-active units? | Rollout stage 2 | **Resolved 2026-10-15:** approved by Kenji Tanaka (Decision Log). Stage 2 still waits for the per-IP limit review |
| OQ-5 | Outside production, does the OTP email go to the real recipient or the support redirect? | FN-018, UAT | **Resolved 2026-10-15:** the support mailbox, as every MARLO email does outside production (Decision Log) |
| OQ-6 | End live sessions on deactivation? | — | Out of scope |
| OQ-7 | Open a spec for `crpByEmail.do` disclosure? | — | Out of scope |
| OQ-8 | Proxy topology in Production and test: which hop supplies the client IP? | SEC-011 | `UNVERIFIED` — settled in `design.md` |

---

## 10. Decision Log

- 2026-10-08 — Pre-registered users only, with no auto-provisioning — Rationale: MARLO login is Global-Unit-scoped with a `crp_users` gate; auto-creating memberships would grant access by domain alone (user decision).
- 2026-10-08 — Challenge handle in the server session, not a client-carried signed token — Rationale: MARLO has server sessions; this removes the encoder, rotation and decoy-length machinery of guide §12 (proposal Option B).
- 2026-10-08 — Supersede on resend (guide §25 Option A) — Rationale: one valid code at a time; avoids PRMS weakness W12.
- 2026-10-08 — Expiry folds into one "code incorrect or expired" message — Rationale: fewer client-visible states.
- 2026-10-08 — `cgiar.org` ignored in allow-lists — Rationale: CGIAR identities belong to Cognito; prevents a configuration slip from bypassing it (proposal R4). *(Superseded by T21, 2026-10-15: there is no allow-list and no CGIAR rule; see the entries of 2026-10-15 below.)*
- 2026-10-09 — Judgment Day round 1 amendments (`judgment.md`) — FN-005, its scenario and AC-4 now allow a non-authenticating decoy challenge record; OPS-001 adds one delivery line per handed-off email; DA-002 seeds only existing types 1, 3, 4. Rationale: a decoy with no record answered resends and supersede differently from a real account (JD-1, JD-2).
- 2026-10-09 — Deviations from the approved proposal, kept in `proposal.md` as historical intent: decoys are records with an undeliverable code (proposal said "no row"); the code email is sent asynchronously (proposal said "synchronous"), because a synchronous send makes response latency reveal eligibility (SEC-006). Fixed rate-limit windows may admit up to 2× a SEC-010 limit across a window edge; accepted at expected volumes.
- 2026-10-09 — OQ-5 default adopted in design: the code email always goes to the submitted address, in every environment. Still requires confirmation.
- 2026-10-09 — Adjust round on the re-judgment info rows RJ-1 to RJ-11 (`judgment.md`):
  - FN-008 is enforced as a sliding 30-s interval, not a fixed window.
  - FN-016 is reworded.
  - AC-11 is aligned with the amended OPS-001.
  - DA-002 includes type 2.
  - The victim-supersede and residual-timing risks are accepted in `design.md` §13.
  - Closure sweep (forward): `grep -n -iE "one \`auth.otp.\*\` line|types 1, 3 and 4|neutral-response request|1-h TTL|window \(15 min, or 30 s"` over `requirements.md` and `design.md` → only amended lines remain.
  - Closure sweep (backward): AC-10 (FN-008) and AC-17 (DA-002) re-read; they still hold.
- Closure sweep (2026-10-09): forward `grep -n -iE "no challenge|no row|create a challenge|exactly one structured|every Global Unit type|synchronous|byte-for-byte"` over `requirements.md`, `design.md`, `proposal.md` — remaining hits are intentional: FN-001 scenario (disabled domain truly writes nothing), design DD-5/DD-7 deviation notes, and `proposal.md` (approved, historical). Backward: `requirements.md` §6/§7 rows citing FN-005/OPS-001 re-read and updated (AC-4).
- 2026-10-13 — T05 hardening (user decision, `execution.md` T05): FN-003 rejects non-ASCII emails and entries; logged `domain` and `gu` values are reduced to `[a-z0-9.-]`; the code is sent only to the normalised email in `OtpPending`. Rationale: the T05 Reviewer showed that Unicode case folding could map a lookalike address onto another account, and that an attacker-supplied domain could forge a log field.
- 2026-10-13 — OTP eligibility requires an active membership (user decision, `execution.md` T06 rework after the T15 Reviewer FAIL) — `OtpEligibility` calls `existActiveCrpUser` (`crp_users.is_active = 1`) at request and at the FN-015 re-check, not the activity-blind `existCrpUser`. This deliberately departs from the password-login parity of DD-4 on this one point. Rationale: MARLO removes a member by soft delete (`CrpUserMySQLDAO.deleteCrpUser` sets `active=false`), so the old gate mailed a code to, and signed in, a removed member, violating FN-005 ("not a member of G"), FN-015 and AC-9 (live probe with `is_active=0`). Scope: OTP path only. The password-login gap (`LoginAction.finishLogin`, `ValidSessionCrpInterceptor`, the center `ValidSessionInterceptor`) is unchanged and tracked separately (A2-2637, under the epic Bugs 2026 A2-2054, filed 2026-10-13 with live evidence: a removed member still signs in with a password, and keeps edit rights while an old session is open or while roles survive the removal), outside this spec.
- 2026-10-14 — Step 3 asks for the sign-in method when both the code and the password apply (user decision after reviewing the PRMS login and two recorded prototypes) — UI-006 is added. On the LOCAL branch with `otpEnabled`, step 3 shows a choice, "Email me a sign-in code" or "Continue with your password", and each opens its own panel; the password field is no longer shown beside the code button. Rationale: in the local walk the user, an external Center member with no password, read the password field and the code button side by side as a two-factor step and asked whether the password was required. PRMS asks for the method first, but MARLO cannot: Cognito and the code are enabled per Global Unit, which is only known after step 2, so the choice moves to step 3, where `crpByEmail.do` has already resolved which methods apply to this user in this unit. A method that does not apply is never offered, and a single method is shown directly, which keeps the Cognito spec's 2026-09-04 rule ("a selection step offering only one valid method is artificial navigation"). Cognito stays exclusive for CGIAR users (SEC-005 of CHG-COGNITO-AUTH-001, re-confirmed by the user). Scope: frontend only (`loginForm.ftl`, `login.js`, `global.properties`); no endpoint, flag or server rule changes. Task T17.
- 2026-10-14 — Code emails are recorded in `email_logs` for support (user decision while reviewing which specificities the code email honours) — OPS-005 and AC-18 are added; AC-11 and DD-7 are amended. Rationale: with only the `auth.otp.delivery` line, which carries no address, support could not answer "I did not get the code" from System Admin → Emails. The row keeps the original reasons of DD-7 intact: the body is replaced by a placeholder so the code is never stored (SEC-001), the row is written after the send on the sender's thread so it adds no latency oracle (SEC-006), and only real sends are recorded. The resend action skips these rows, since a resend would mail no code or an expired one. Unchanged: the code email still ignores `crp_enable_email_notification`, `crp_email_support_team` and the non-production redirect (FN-018, OQ-5 still open). Task T18.
- 2026-10-15 — OQ-5 resolved: outside production the code email follows MARLO's non-production rule (user decision: "en testing no salgan correos a usuarios externos"). FN-018 is amended, AC-19 is added, and the 2026-10-09 default (real recipient in every environment) is withdrawn. Rule:
  - `SendMailS.send` already sends every email outside production only to the support mailbox `email.notification`. It does so by replacing TO with the BCC, which every caller sets to that mailbox. It also prefixes the subject with `(TESTING ENVIRONMENT) ` and prints the production recipients at the top of the body;
  - the code email now does the same. It reads `email.notification` directly instead of relying on a BCC convention, and fails closed when that mailbox is missing;
  - its `email_logs` row matches `SendMailS`: the submitted address in `to_email`, the support mailbox in `bbc_email` outside production, and the subject without the prefix.

  Unchanged, by design:
  - `crp_enable_email_notification` and `crp_email_support_team` are still ignored. Honouring them in production would block sign-in for a program with notifications off, or send its users' codes to support.

  Trade-off: in UAT the tester reads the code from the support mailbox, and that mailbox receives sign-in codes from test environments. Production is unaffected. Task T19.
- 2026-10-15 — The code email's `email_logs` row stores the full body, code included (user decision: "a esa información solo tienen acceso los superadmin y puede servir para soporte"). **This deviates from guide §3.2 ("Never store the OTP in plaintext", REQUIRED BEHAVIOR)**, which A2-2631's acceptance criteria say the implementation follows; record it on the ticket. SEC-001, OPS-005, AC-11, AC-18 and DD-7 are amended. Rationale: MARLO stores every email it sends in full, and support needs to see what the user received. Accepted risk:
  - anyone who can read `email_logs` can sign in as that user while the code is live, that is, at most 5 minutes, before it is used or replaced, and with the attempt limits still applying. That covers super admins in System Admin → Emails, and anyone with access to the database, a replica or a backup;
  - after that window the stored code is useless.

  Unchanged:
  - the challenge store keeps only the MAC;
  - in production, logs never carry the code (see the next entry for non-production);
  - the resend action still skips these rows, since a resend would mail an expired or replaced code.

  Task T19.
- 2026-10-15 — Outside production the log carries the code (user decision, chosen over "always" and "never"). OPS-001, the SEC-001 scenario and AC-11 are amended. Outside production, one INFO line `auth.otp.code gu=<acronym> to=<submitted address> code=<code>` is written per handed-off email, on the sender's thread after the delivery attempt; in production that line never exists.
  - Rationale: testers and developers on test and local environments can read the code without access to the support mailbox, while production logs stay free of codes and addresses.
  - **This deviates from guide §3.11 ("Secrets never in logs", REQUIRED BEHAVIOR)**, outside production only. Accepted risk: anyone who reads a non-production log can sign in as that test user while the code is live (at most 5 minutes).
  - The check is the same `isProduction()` read that routes the email (FN-018), so a production instance with `marlo.production` unset would both redirect codes to support and log them. The rollout checklist requires `marlo.production=true` in production. Task T19.
- 2026-10-15 — The code email uses MARLO's branded email format (user decision: "aplica ese formato a este correo"). FN-017 and UI-005 are amended and AC-20 is added. The format is the A2-2484 one: `EmailLayout.wrap` with the Global Unit's `email.layout` shell, and per-unit copy in the custom i18n file.
  - Before sign-in the session holds no Global Unit, so the request action's text provider resolves only `global.properties`, where the shell is `{0}`. The code email therefore resolves its texts with the selected unit's custom file. Every other MARLO email gets that file from the logged session.
  - The body keeps every FN-017 element; a unit's own keys may move the support contact and the do-not-reply notice to its shell, as the role emails do.
  - Unchanged: FN-018 (non-production recipient, prefix, header), OPS-005 (the row stores the HTML as sent to the user, now with the shell), and the text part. Task T20.
- 2026-10-15 — Code sign-in is switched on per Global Unit for every account with access, with an optional per-unit domain exclusion (user decision while reviewing how the specificity was defined: "la idea es que puedan entrar usuarios externos con esto y no tendríamos todos los dominios posibles"). FN-001, FN-002, FN-003, FN-005, FN-006, SEC-009, DA-002, UI-001, UI-006, AC-1, AC-2, AC-4 and AC-13 are amended; AC-21 and OQ-9 are added; OQ-4 is superseded. Task T21.
  - **Why the allow-list goes:** it needed every partner's domain in advance. It also protected little, because MARLO already mails a new external user their password, so mailbox control already means account access. A code is still only sent to a registered, active account with an active membership.
  - **Why no CGIAR rule:** the rest of MARLO decides "CGIAR" by `is_cgiar_user`. In a local snapshot (`aiccradb_actsave`), 1672 active `@cgiar.org` accounts carry 0, so a rule on that flag would be unreliable. A rule on the domain would fix a domain in code. Each unit can instead exclude domains by configuration.
  - **Keys:** both are stored with the `system_` prefix, ahead of the planned rename of every `crp_` key.
  - **Consequence for Cognito:** on a Cognito-active unit, a CGIAR account may now choose the code instead of Cognito, skipping Azure AD's MFA and conditional access. Its mailbox is itself behind that MFA. This modifies SEC-005 of CHG-COGNITO-AUTH-001, which made Cognito exclusive for CGIAR users. That spec is archived and is not edited here; the change was approved through OQ-9 (next entry).
  - **Rollout in two stages, by configuration only:**
    1. AICCRA switch on, with `cgiar.org` excluded: external users only, which is what A2-2631 asks for.
    2. Exclusion emptied after the per-IP limit review (OQ-9 approved).
  - **Before stage 2:** revisit the per-IP limit (20 requests per 15 min) together with P-6, since CGIAR offices may share one egress IP.
  - **Future option:** a unit could later use the code as its only method (Cognito off). Making it exclusive would need a server-side password block, a reliable relay and an emergency access path; out of scope.
- 2026-10-15 — OQ-9 resolved: CGIAR accounts may choose the code on Cognito-active units (approved by Kenji Tanaka, who owns the Cognito rollout in MARLO: "yo doy el visto bueno").
  - **What Cognito checks at sign-in:** the unit's `cognito_auth_active`, an existing account with the email Azure AD returns, `is_cgiar_user = 1`, an active account and an active membership. It never checks the email domain. The domain `cgiar.org` is fixed in code only at account creation (`CrpUsersAction`, `ManageUsersAction`), where it can set `is_cgiar_user = 1`; nothing rewrites the flag afterwards.
  - **Measured exposure** (local snapshot `aiccradb_actsave`, active AICCRA members): 1 `@cgiar.org` account with `is_cgiar_user = 1`, 214 `@cgiar.org` accounts with 0, and 169 other accounts with 0. Only AICCRA has Cognito on. For the 214, the code replaces a local password, not Cognito; only the one Cognito-enabled account gains a path that skips Azure AD's MFA. That number grows as accounts move to `is_cgiar_user = 1`. Re-run the count on production before stage 2.
  - **Still required before stage 2:** the per-IP limit review (runbook B5).
- 2026-10-16 — The code secret may live in `marlo-<profile>.properties` like the other credentials (user decision: "dejemoslo en el properties como los demas secretos"). The key is `otp.hmac.secret`; the `OTP_HMAC_SECRET` environment variable keeps working and takes precedence, so a server can use the same route it uses for `cognito.client.secret` (the template notes that Cognito's values come from the environment on deployed servers).
  - **No code change:** `APConfig` already reads `${otp.hmac.secret:}` from every Spring source; only its comment changed.
  - **Missing key never breaks startup:** the placeholder defaults to empty, `cognitoSetting` turns `null` into `""` and trims, and `OtpKeys` turns an empty or invalid value into one `unconfigured` WARN with the feature off (`OtpKeysTest`, AC-12).
  - **Template:** `marlo-test.properties` gains the three `otp.*` keys, empty. Runbook A2, P-18, B4 and the tasks environment rows are amended; the `docs/infrastructure.md` §6 item stays a pending shared-file edit.

- 2026-10-17 — Amended by `CHG-PWD-SWITCH-001` (`docs/specs/archive/2026-10-18-changes--password-login-switch/`, task T08, its DD-8; decided by Kenji Tanaka on 2026-10-16 in that spec's Decision Log). A Global Unit can turn password sign-in off with the specificity `system_password_login_active` (default on). While it is off and the code key is valid, the code is the default method for every account without Cognito in that unit, whatever `system_otp_login_active` and the exclusion say, and an account that uses Sign in with CGIAR in that unit is not admitted by that override: it keeps the code switch and the exclusion as written. Every clause below carries an amendment note that points there; the original text is untouched.
  - **Clauses amended:** FN-001, FN-003, FN-005, FN-006, FN-015, SEC-009, DA-002 (the two descriptions are replaced by the texts of that spec's DA-003, the former ones kept as "was" lines) and UI-006, the eight the cross-spec amendment named. The backward sweep found five more that state the old rule and were amended too: FN-019 ("exactly as before"), UI-001, the Glossary entry "Eligible user", the Personas row "Local password users" and AC-1. The scenario "Feature off" of FN-001 and the OQ-4 row got a note as well.
  - **Executive Summary corrected:** it said the capability is "enabled per Global Unit by listing domains" and that "Emptying the list switches it off", which described the allow-list T21 removed on 2026-10-15, and that "Password and Cognito logins do not change". It now names the switch and the exclusion and carries the password-off exception. The first paragraph and the last bullet of the list ("CGIAR accounts routed to Cognito cannot use the code to bypass it", reversed by T21) carry notes.
  - **`design.md`:** notes on the executive paragraph, the `crpByEmail.do` diagram, the status table row `domainNotEnabled`, steps 4 and 8 of request and verify, the struck-through Cognito check, the `crpByEmail.do`, `login.js` mode and method-choice rows, the load row, the victim-lockout rows, and the DD-13 bullets on `is_cgiar_user`, the status vocabulary and step 3. **`runbook.md`:** a banner, the quick-path rows, the A1 table and rollback step, the key-missing row of A2, the `denied_domain` row of A5, the A6 table row, its blockquote and the support sentence, the A7 lockout row, the B1 OQ-4 row, "To stop" in B5, and the pending TRD 8.1 text of Part D, which also drifted at T21. **`probes/README.md`:** a header note and section 12.
  - **New risk, recorded and not decided:** the victim lockout of A7 (SEC-010 buckets) leaves a user of a password-off unit with no method for the window, where it used to leave the password. Written down in two rows of `design.md`, in the runbook A7 and in the runbook of `CHG-PWD-SWITCH-001` section 7, for the product owner.
  - **Closure sweep, forward** (`docs/specs/archive/2026-10-18-changes--password-login-switch/probes/sweeps.sh`, raw run in its `output/sweeps.out`): five families of the superseded sentences over `requirements.md`, `design.md`, `runbook.md` and `probes/README.md` of this spec and `requirements.md`, `design.md` and `runbook.md` of that one. S1 `listing domains|Emptying the list|enabled per Global Unit by|only while password sign-in is on|applies only while password`; S2 `Password and Cognito (logins|paths) do not change|exactly as before|do not change behavior|logins are unaffected`; S3 `domainNotEnabled|not enabled for this email|Not enabled for this project|Off or absent MUST disable`; S4 `plays no part|MUST NOT depend on|no longer read by the code path|is_cgiar_user. no longer|nobody can`; S5 `single method|shown directly|every single-method case|only when the feature is configured`. Every hit is an amendment note, or intentional: design steps 2 and 3 of request-code (a bad shape and a missing key still answer `domainNotEnabled`), the dated walk record of runbook Part C (2026-10-13), the dated verdict rows of `probes/README.md` (covered by its header note), the Decision Log entries of 2026-10-14 and 2026-10-15, the `CHG-PWD-SWITCH-001` lines that quote the former wording of the clauses they amend, the lines of this entry that quote the patterns, and one unrelated match in the A5 `internal_error` row of the runbook ("must not depend on the account"). The phrase "applies only while password sign-in is on" is in no live document: it was the reading of `system_otp_login_active` that Judgment Day W-3 of that spec rejected.
  - **Closure sweep, backward:** (B1) the full clause ids `CHG-OTP-LOGIN-001-(FN-001|FN-003|FN-005|FN-006|FN-015|FN-019|SEC-009|DA-002|UI-001|UI-006)` appear in the repository only as the headings of this file's own clauses, all amended. (B2) The short ids outside the two spec folders are cited by tests and sources only (`OtpEligibility`, `OtpDomainPolicy`, the login page files, two migrations); `docs/trd/trd.md`, `docs/prd.md`, `docs/infrastructure.md`, `docs/ux-ui/` and `reports/` cite none. (B3) Code comments that cite the old rule are listed in the report of the task, not edited, because T08 may not change production files. (B4) The CLARISA filter, HTTP Basic (`APCustomRealm`, `UserManagerImp`) and the Shiro configuration do not reference the password switch. The documents of this spec that keep the old text as history (`proposal.md`, `tasks.md`, `execution.md`, `judgment.md`) were not edited: they are append-only records.
  - **Probes re-run:** `ac21-migration.sh` after the DA-002 reword: 17 checks, 0 failed (`probes/output/ac21-migration.pwd-switch-rerun.out`; the first run had one FAIL on a fixture row, kept as `..-1-fixture-row-red.out`). The `ac-probe.sh` stages run with the password switch on everywhere: recorded in `docs/specs/archive/2026-10-18-changes--password-login-switch/probes/output/ac2-switch-on-ac-probe.out` and its re-run.
  - **Correction, same day (KZ-directory-abstraction-1, KZ-auth-flow-1):** the first version of eleven notes said a Cognito account is refused outright while password sign-in is off. The rule (`CHG-PWD-SWITCH-001` design section 1, DD-8, FN-005) is that the override does not admit a Cognito account: it keeps the code switch and the exclusion as written and gets the neutral answer only when its switch is off or its domain is excluded. Fixed in FN-005, FN-015, SEC-009, the Glossary "Eligible user", the Executive Summary bullet, OQ-4, UI-006, `design.md` (steps 4 and 8, the struck-through Cognito check, the `is_cgiar_user` bullet) and `runbook.md` (A5 row, OQ-4 row, pending TRD 8.1 text). Sweep: `Cognito account.{0,100}(refus|not eligible|ineligible)`, `refuses? (a )?Cognito`, `Cognito (refusal|check)`, `to refuse`, `refused it` over this spec's `requirements.md`, `design.md`, `runbook.md`, `probes/README.md` and the other spec's `runbook.md`; every remaining hit is qualified or an unrelated word. Second sweep, after the reviewer found two lines the first wording missed: the widened patterns `Sign in with CGIAR.{0,60}(refus|cannot|not eligible)`, `cannot use the code` and `CGIAR.{0,80}(refus|cannot get|no code)`, plus a read of every added line that mentions Cognito, CGIAR or Sign in with CGIAR in each sentence against the rule (list: `docs/specs/archive/2026-10-18-changes--password-login-switch/probes/output/t08-cognito-line-review.out`); a regex alone is not enough, because the defect is a meaning, not a phrase.
- 2026-10-18 — Both code endpoints require POST; `OtpRequestCodeAction` catches `Error` (`/akili-validate`, `validation-report.md` V-1, V-4, V-2; task T22; approved by the user the same day). Design section 4 already routed both endpoints as POST, but neither action checked the method, so a GET with `?code=` or `?email=` was served and its query string reached the access logs, against SEC-001. Now a request that is not a POST, or that has no servlet request, answers `{status: unavailable}` and changes nothing: no limiter hit, no session read or write, no store read, no challenge row, no mail, no attempt counted, nothing consumed. The answer is the same for every account, so SEC-006 holds.
  - **OPS-001 amended (closed outcome sets):** `bad_method` is added to the `request` set (`sent`, `neutral`, `denied_domain`, `terms_required`, `rate_limited`, `unconfigured`, `internal_error`) and to the `verify` set (`ok`, `mismatch`, `exhausted`, `invalid`, `not_eligible`, `rate_limited`, `failed`, `internal_error`). One line per call still holds. AC-11 is unchanged: it counts lines, not outcomes.
  - **UI-002 amended:** the new key `login.otp.error.unavailablePasswordOff` (no apostrophe, no brace) is the "service unavailable" text on a password-off unit, where "use your password" would point at a refused method (V-2). `loginForm.ftl` renders it in `#login-otp-copy` and `login.js` selects it in `otpShowError` when `passwordEnabled === false`. No `custom/*.properties` file overrides `login.otp.error.unavailable`, so none gets the new key.
  - **V-4:** the `catch` in `OtpRequestCodeAction.execute()` and the one in `isEligible` take `RuntimeException | Error`, as the mail hand-off already did. An `Error` from the eligibility predicate answers the neutral `sent`, writes the decoy row and logs `internal_error`, the same as a `RuntimeException`.
  - **Out of scope, unchanged:** the other validation findings (V-3, V-5 to V-11), CSRF tokens on these endpoints (the P-9 residual stays accepted), a Struts `httpMethod` interceptor for the package, and the `domainNotEnabled` copy (never reached on a password-off unit, by design). Statuses, limits, the neutral response, the mail and the UI flow are as T21 and `CHG-PWD-SWITCH-001` left them.
  - **Design:** sections 4, 5.1 and 5.2 (step 0) and 11 carry the same change. **Runbook:** a triage row for `bad_method`.
- 2026-10-18 — Documentation corrections after `/akili-validate` (`validation-report.md` V-3, V-5 to V-11; approved by the user the same day). No product behaviour changed.
  - **SEC-008 scenario amended (V-7):** "Missing secret fails closed" now reads "absent or invalid in both `OTP_HMAC_SECRET` and `otp.hmac.secret`". The earlier wording named only the environment, so a run with the properties key set did not meet its precondition (`test-report.md` §5.3 attempt 1). The clause itself is unchanged.
  - **SEC-003 gate row added (V-6):** section 6 now records that constant time is proven by inspection of `OtpCodes.java` (`MessageDigest.isEqual`), with the malformed-input no-throw tests as the automated part. Accepted.
  - **Stale text swept (V-5, V-10, V-11):** the allow-list and CGIAR-refusal wording left in `design.md` (sections 2, 3, 9, 10, 14), `tasks.md` (task scopes, section 5 coverage rows, section 8 rollback, section 10), `runbook.md` Part D and `probes/README.md` was rewritten or marked "Superseded by T21". Every reference to the password-switch spec's former active folder now points to its archived folder.
  - **Restored (V-3):** runbook A7 row "Cognito cache lag" for password-off units.
