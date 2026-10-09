# Center Email OTP Login — Requirements

**Spec ID:** `CHG-OTP-LOGIN-001`
**Status:** Approved (2026-10-09)
**Owner:** IBD Team — Alliance of Bioversity International and CIAT (Kenji Tanaka)
**Reviewers:** Tech lead, QA lead, PMU lead
**Last Updated:** 2026-10-08
**Depth:** Full — new authentication path, migration, security invariants
**Approval Mode:** `gated` (inherited from `proposal.md`)
**Jira:** A2-2631 (parent epic A2-2629)
**Source intent:** [`proposal.md`](./proposal.md) (approved 2026-10-08); portable guide `CENTER_EMAIL_OTP_IMPLEMENTATION_SPEC.md` (cited as *guide §n*)
**Related PRD sections:** `docs/prd.md` §3 (Personas), §7.2 (Quality and security acceptance)
**Related UX/UI Design sections:** `docs/ux-ui/design.md` §7 (Design Tokens), §10 (Accessibility), §11 (Light theme only). The login screen has no §4 Screen Inventory entry; adding one is out of scope, as it was for `CHG-COGNITO-AUTH-001`
**Related TRD sections:** `docs/trd/trd.md` §8.1–8.2 (Authentication, Authorization layers), §9.5 (Rate limiting), §5.4 (Notifications and emails), §3.7 (Specificity tables)
**Related infrastructure:** `docs/infrastructure.md` §2 (session store), §6 (Local Environment: new environment variable)
**Companion ai-context docs:** `reports/ai-context/struts-critical-routing-catalog.md`, `reports/ai-context/interceptor-validator-playbook.md`
**Related specs:** `docs/specs/archive/2026-09-07-changes--migrate-ad-authentication-to-cognito--auth-flow/` (session-establishment pattern reused); `docs/specs/changes/migrate-ad-authentication-to-cognito/family.md` child 3 `directory-retirement` (pending; shares files)

---

## Executive Summary

A **pre-registered** MARLO user whose email domain is allow-listed for the Global Unit they select
can sign in with a **6-digit code emailed to them**, with no password. The code is valid for 5
minutes, allows 3 wrong attempts and works once. A verified code produces **exactly the session a
password login produces**: same membership gate, roles, custom parameters and landing page.

The capability is **off by default** and is enabled per Global Unit by listing domains. Emptying
the list switches it off without a deploy. Password and Cognito logins do not change.

What the spec adds to the guide for MARLO:
- the code never reaches `email_log` or any log;
- login is always scoped to a Global Unit;
- rate limits hold across every Production instance;
- CGIAR accounts routed to Cognito cannot use the code to bypass it.

---

## Glossary

| Term | Meaning |
|---|---|
| **Allow-list** | The set of email domains for which a Global Unit enables code sign-in. Empty means off |
| **Eligible user** | An account that is active, a `crp_users` member of the selected Global Unit, on an allow-listed domain, and not refused by the Cognito guard (`SEC-009`) |
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

- **CHG-OTP-LOGIN-001-FN-001** — Code sign-in MUST be enabled per Global Unit by a configurable domain allow-list. An empty or absent list MUST disable it for that Global Unit, both in the UI and on the server.
- **CHG-OTP-LOGIN-001-FN-002** — A change to a Global Unit's allow-list MUST take effect for new requests without a deploy or restart.

##### Scenario: Feature off

- GIVEN Global Unit G has an empty allow-list
- WHEN a user selects G on the login page
- THEN no code sign-in option is shown
- AND a request-code call addressed to G directly is refused, with no challenge and no email
- BUT it must NOT change the password or Cognito step for G

##### Scenario: Live change

- GIVEN G's allow-list is changed from empty to `partner.org`
- WHEN the next login page for G is loaded
- THEN the option is offered to eligible `@partner.org` users, with no restart

#### Requirement: Exact domain matching

- **CHG-OTP-LOGIN-001-FN-003** — The server MUST decide eligibility by **exact equality** between the domain of the normalised email (trimmed, lower-cased, the part after the single `@`) and a normalised allow-list entry. Normalising an entry trims it, lower-cases it, strips one leading `@`, drops it when empty or still containing `@`, and de-duplicates. Suffix, substring and sub-domain matches MUST NOT qualify unless that sub-domain is listed itself (guide §6.2–6.3).

##### Scenario: Lookalike rejected

- GIVEN G's allow-list is `trusted.org`
- WHEN codes are requested for `x@attackertrusted.org`, `x@trusted.org.evil.com` and `x@sub.trusted.org`
- THEN each request is treated as a non-allow-listed domain
- AND IT MUST accept `  X@TRUSTED.ORG ` as `x@trusted.org`

#### Requirement: Request a code

- **CHG-OTP-LOGIN-001-FN-004** — When an **eligible user** requests a code for Global Unit G, the system MUST create exactly one challenge bound to that email and G, and send exactly one email containing the code.
- **CHG-OTP-LOGIN-001-FN-005** — When the domain is allow-listed but the account is not eligible (unknown, inactive, not a member of G, or refused by `SEC-009`), the system MUST return the **neutral response**. It MUST NOT send an email, and any challenge state it records MUST be unable to authenticate and MUST behave, on later requests and verifications, exactly as an eligible account's challenge does (amended 2026-10-09, Judgment Day JD-1/JD-2).
- **CHG-OTP-LOGIN-001-FN-006** — When the domain is not on G's allow-list, the system MUST return an explicit "not enabled for this email" message that names no account state.
- **CHG-OTP-LOGIN-001-FN-007** — A new code request for the same email MUST **supersede** every earlier open challenge for that email, atomically with issuing the new one.
- **CHG-OTP-LOGIN-001-FN-008** — Consecutive code requests for the same email MUST be at least 30 seconds apart, enforced by the server. An earlier request MUST receive the rate-limited response and change nothing.

##### Scenario: Eligible user receives a code

- GIVEN `ana@partner.org` is active, a member of G, `is_cgiar_user = 0`, and `partner.org` is on G's allow-list
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
- **CHG-OTP-LOGIN-001-FN-015** — Eligibility MUST be re-checked after the code is consumed and before the session is issued. A user deactivated, removed from G, or newly refused by `SEC-009` since the request MUST be refused.
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

- **CHG-OTP-LOGIN-001-FN-017** — The email MUST state the application name, the code, the 5-minute expiry, that it works once, that it must not be shared, that it can be ignored if not requested, and the support contact. It MUST NOT contain a link that carries the code (guide §19).
- **CHG-OTP-LOGIN-001-FN-018** — The code email MUST be delivered even when the Global Unit's email notifications are disabled. Its recipient handling outside production follows the outcome of OQ-5.

#### Requirement: Existing logins unaffected

- **CHG-OTP-LOGIN-001-FN-019** — Password login and Cognito login MUST behave exactly as before for every Global Unit, whether or not code sign-in is enabled.

### 3.2 Security

- **CHG-OTP-LOGIN-001-SEC-001** — The code MUST NOT be stored in clear text anywhere: not in the challenge store, `email_log`, application logs, the session, or error messages. Only a keyed MAC bound to the challenge may be stored (guide §11).
- **CHG-OTP-LOGIN-001-SEC-002** — The challenge store MUST NOT hold the email in clear text. A keyed MAC of the normalised email is allowed.
- **CHG-OTP-LOGIN-001-SEC-003** — Every comparison of a MAC or a code MUST run in constant time and MUST NOT throw on malformed input.
- **CHG-OTP-LOGIN-001-SEC-004** — A challenge MUST be usable only by the session that requested it, for the email and Global Unit it was issued for. Nothing returned to the client may be accepted as an authenticated session.
- **CHG-OTP-LOGIN-001-SEC-005** — A successful verification MUST rotate the session, so that the pre-authentication session ID is never the authenticated one.
- **CHG-OTP-LOGIN-001-SEC-006** — Request-code and verify-code MUST add no new way to tell unknown, inactive, non-member and Cognito-routed accounts apart. The pre-existing `crpByEmail.do` exposure (C8) is out of scope and recorded as a risk.
- **CHG-OTP-LOGIN-001-SEC-007** — Code sign-in MUST NOT create users, memberships or role assignments, and MUST NOT change them. Authorization after a code sign-in MUST be identical to authorization after a password sign-in.
- **CHG-OTP-LOGIN-001-SEC-008** — The MAC keys MUST derive from a dedicated secret, at least 256 bits, delivered by the environment. When the secret is absent or too short, code sign-in MUST be unavailable for every Global Unit and MUST NOT fall back to a generated or default key.
- **CHG-OTP-LOGIN-001-SEC-009** — An account with `is_cgiar_user = 1` MUST be ineligible for code sign-in on any Global Unit where Cognito is active. An allow-list entry of `cgiar.org` MUST be ignored. *(Default pending OQ-4.)*
- **CHG-OTP-LOGIN-001-SEC-010** — Request-code MUST be limited per normalised email (5 per 15 min) and per client IP (20 per 15 min). Verify-code MUST be limited per email (10 per 15 min) and per IP (50 per 15 min). The limits MUST hold across every application instance, MUST be counted before any account lookup, and MUST apply identically to every account state.
- **CHG-OTP-LOGIN-001-SEC-011** — The client IP used for rate limiting MUST NOT be taken from a header an untrusted client can set (guide §17.2 item 6).

##### Scenario: Code never persisted

- GIVEN a full request-and-verify flow
- WHEN `otp_challenges`, `email_log` and the application log are searched for the 6-digit code and the full email
- THEN neither value is found

##### Scenario: Missing secret fails closed

- GIVEN the OTP secret is not set in the environment
- WHEN any Global Unit with a non-empty allow-list loads the login page
- THEN no code sign-in option is shown, and request-code is refused
- BUT it must NOT prevent the application from starting, or affect password and Cognito logins

### 3.3 Operations

- **CHG-OTP-LOGIN-001-OPS-001** — Each request-code and verify-code call MUST emit exactly one structured log line: event name, outcome from a closed set, Global Unit acronym, email domain and duration. Each email handed to delivery MUST additionally emit exactly one delivery-outcome line. No line may contain the code, the nonce, the full email or the session ID (amended 2026-10-09, JI-1).
- **CHG-OTP-LOGIN-001-OPS-002** — An email delivery failure MUST be logged as outcome `email_failed` at WARN level. The user-facing response MUST stay the neutral one.
- **CHG-OTP-LOGIN-001-OPS-003** — Challenges past their expiry by more than 1 hour MUST be purged. A purge failure MUST NOT block sign-in.
- **CHG-OTP-LOGIN-001-OPS-004** — The spec MUST ship a runbook covering enable and disable per Global Unit, "code not received" triage, secret rotation, and the support note that ineligible accounts see "code sent" but receive nothing.

### 3.4 Data & Migration

- **CHG-OTP-LOGIN-001-DA-001** — The challenge store MUST ship as a Flyway migration. It MUST be idempotent and MUST assume no Global Unit, user or role row exists.
- **CHG-OTP-LOGIN-001-DA-002** — The allow-list specificity MUST be seeded **off** for each of the Global Unit types 1, 2, 3 and 4 that exists in the database at migration time (type 2 included because migration-only databases hold "Center" as id 2; RJ-4), with its constant declared in **both** `APConstants.java` files and equal to `parameters.key`.
- **CHG-OTP-LOGIN-001-DA-003** — Rate-limit state MUST be stored where every application instance reads the same values.

### 3.5 UI

- **CHG-OTP-LOGIN-001-UI-001** — The authentication step for G MUST offer "Email me a sign-in code" only when G enables code sign-in and the entered email's domain is on G's allow-list.
- **CHG-OTP-LOGIN-001-UI-002** — The code step MUST show these states:
  - sending;
  - code sent: neutral text with the masked destination;
  - verifying;
  - one error message per refusal class: wrong code, too many attempts, expired or invalid, too many requests, service unavailable;
  - a resend control with a visible 30-second countdown;
  - "use a different email", which returns to the email step.
- **CHG-OTP-LOGIN-001-UI-003** — The code field MUST:
  - be labelled;
  - accept digits only, with a maximum length of 6;
  - use `inputmode="numeric"` and `autocomplete="one-time-code"`;
  - receive focus when the code step opens.

  Status and errors MUST be announced through an `aria-live` region, with `role="alert"` for errors.
- **CHG-OTP-LOGIN-001-UI-004** — Buttons MUST be disabled while their request is in flight. A double click MUST send one request.
- **CHG-OTP-LOGIN-001-UI-005** — All user-facing text, in the page and in the email, MUST come from i18n keys in `global.properties`.

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
| An admin screen for the allow-list | Existing `custom_parameters` administration |
| Choosing the pilot Global Units and domains | Configuration, proposal OQ-1 |
| `/api/**` REST authentication | Hard rule 3; untouched |
| Metrics dashboards and alerting | MARLO has no metrics stack; OPS-001 log lines are the signal |

---

## 5. Personas Affected

| Persona | Effect |
|---|---|
| Partner staff (project leaders, coordinators, M&E focal points on allow-listed domains) | New passwordless sign-in. Their roles do not change |
| MARLO Support / Super Admin | Enable and disable per Global Unit; triage "code not received" |
| CGIAR staff on Cognito-enabled Global Units | No change; refused by `SEC-009` |
| Local password users | No change (`FN-019`) |

---

## 6. Defect classes and their gates

| Defect class | Gate that catches it | Automated? |
|---|---|---|
| Code that does not compile | Clean recompile (`marlo-verify` skill) | Yes |
| Style violations | Checkstyle jar run directly, delta against HEAD | Yes |
| Wrong normalisation, matching, code format, MAC, expiry or attempt arithmetic | JUnit unit tests (`mvn -q -o -pl marlo-web -am test`) | Yes |
| Concurrency: double consume, attempts above 3, lost supersede | Parallel probe against a **throwaway copy** of the local MySQL. No in-repo harness runs Hibernate against a real database | **No** — scripted probe at the execute HITL pause, with recorded output |
| Code or email persisted (SEC-001/002) | `SELECT` over `otp_challenges` and `email_log`, plus a grep of the run's log, after a real flow | Manual at the HITL pause |
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
| AC-1 | FN-001, FN-019 | Given empty allow-lists everywhere, when the existing login tests run, then they pass unchanged and no OTP control renders |
| AC-2 | FN-003 | Given `trusted.org`, then the five lookalike or normalisation inputs in the FN-003 scenario resolve as specified (unit test) |
| AC-3 | FN-004, FN-009, FN-017 | Given an eligible user, when a code is requested, then one challenge row exists and one email arrives containing a 6-digit code and every FN-017 element |
| AC-4 | FN-005, SEC-006 | Given each of unknown, inactive, non-member and CGIAR-refused, when a code is requested, then the result and page state equal the eligible one, no email is sent, and a later resend and verifications answer as for the eligible account; no recorded challenge can authenticate |
| AC-5 | FN-011, SEC-005, SEC-007 | Given a correct code, when verified, then the session ID differs from the pre-auth one, the session attributes match a password login for the same user and Global Unit, and the role rows are unchanged |
| AC-6 | FN-012 | Given 3 wrong codes, then the 3rd says "too many attempts" and the correct code is refused afterwards |
| AC-7 | FN-013 | Given 10 concurrent correct verifications on a database copy, then exactly 1 succeeds |
| AC-8 | FN-010, FN-014 | Given a challenge 5 min 1 s old, or superseded, when the correct code is submitted, then it is refused |
| AC-9 | FN-015 | Given the user is removed from G after requesting, when the correct code is submitted, then it is refused and the challenge is consumed |
| AC-10 | FN-007, FN-008 | Given a resend after 30 s, then the old code is refused and the new one works. A resend within 30 s returns the rate-limited response |
| AC-11 | SEC-001, SEC-002, OPS-001 | After a full flow, neither the code nor the full email appears in `otp_challenges`, `email_log` or the log; exactly one `auth.otp.request` or `auth.otp.verify` line per call, plus one `auth.otp.delivery` line per handed-off email (amended 2026-10-09, RJ-7) |
| AC-12 | SEC-008 | Given no secret, the option is hidden, request-code is refused, and the application starts |
| AC-13 | SEC-009 | Given an `is_cgiar_user = 1` account on a Cognito-active Global Unit, then it gets the neutral response and no email |
| AC-14 | SEC-010 | Given 6 requests in 15 min for one email, then the 6th is rate-limited. The per-IP limit trips across different emails |
| AC-15 | FN-018, OPS-002 | Given notifications are off for G, the code still arrives. Given SMTP is down, the response is neutral and `email_failed` is logged |
| AC-16 | UI-001–UI-005 | Browser walkthrough of every state, with the attributes present and one request per double click |
| AC-17 | DA-001, DA-002 | The migration applies twice cleanly on a throwaway copy and on a database without Global Units 45/47 |

---

## 8. Constitutional Compliance Checklist

- [x] Phase replication: not applicable. No phased data is read or written.
- [x] Save validation: not applicable to a save section. Request-code and verify-code validate input in their actions; no manager save chain on phased entities.
- [x] Permissions: the new actions run before authentication. Their interceptor stack is declared in `design.md` and must match the existing pre-auth login actions.
- [x] Specificity: the allow-list goes through `parameters` + `custom_parameters`, with constants in both `APConstants.java` files (DA-002).
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
| OQ-4 | `is_cgiar_user = 1`: refuse always, or only where Cognito is active? | SEC-009 | Only where Cognito is active; `cgiar.org` always ignored |
| OQ-5 | Outside production, does the OTP email go to the real recipient or the support redirect? | FN-018, UAT | Real recipient on test, so UAT can work. Needs confirmation |
| OQ-6 | End live sessions on deactivation? | — | Out of scope |
| OQ-7 | Open a spec for `crpByEmail.do` disclosure? | — | Out of scope |
| OQ-8 | Proxy topology in Production and test: which hop supplies the client IP? | SEC-011 | `UNVERIFIED` — settled in `design.md` |

---

## 10. Decision Log

- 2026-10-08 — Pre-registered users only, with no auto-provisioning — Rationale: MARLO login is Global-Unit-scoped with a `crp_users` gate; auto-creating memberships would grant access by domain alone (user decision).
- 2026-10-08 — Challenge handle in the server session, not a client-carried signed token — Rationale: MARLO has server sessions; this removes the encoder, rotation and decoy-length machinery of guide §12 (proposal Option B).
- 2026-10-08 — Supersede on resend (guide §25 Option A) — Rationale: one valid code at a time; avoids PRMS weakness W12.
- 2026-10-08 — Expiry folds into one "code incorrect or expired" message — Rationale: fewer client-visible states.
- 2026-10-08 — `cgiar.org` ignored in allow-lists — Rationale: CGIAR identities belong to Cognito; prevents a configuration slip from bypassing it (proposal R4).
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
