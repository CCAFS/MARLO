# Password Login Switch — Requirements

**Spec ID:** `CHG-PWD-SWITCH-001`
**Status:** Approved — Kenji Tanaka, 2026-10-16 (Phases 1–3 of `/akili-specify`; Judgment Day APPROVED)
**Owner:** IBD Team — Alliance of Bioversity International and CIAT (Kenji Tanaka)
**Reviewers:** Tech lead, QA lead, PMU lead
**Last Updated:** 2026-10-18
**Depth:** Full — changes who can authenticate with which credential, adds a migration, and edits the security rules of another spec
**Approval Mode:** `gated` (inherited from `proposal.md`)
**Jira:** none yet (proposal OQ-1)
**Branch:** `A2-2631-Implement-Center-Email-OTP-Authentication` (proposal OQ-2, resolved 2026-10-16)
**Source intent:** [`proposal.md`](./proposal.md) (approved 2026-10-16)
**Related PRD sections:** `docs/prd.md` §3 (Personas), §7.2 (Quality and security acceptance)
**Related UX/UI Design sections:** `docs/ux-ui/design.md` §7 (Design Tokens), §10 (Accessibility). The login screen has no §4 Screen Inventory entry; adding one is out of scope, as for `CHG-OTP-LOGIN-001`
**Related TRD sections:** `docs/trd/trd.md` §8.1 (Authentication), §3.7 (Specificity tables), §5.4 (Notifications and emails)
**Related specs:** `docs/specs/changes/center-email-otp-login/` (`CHG-OTP-LOGIN-001`, T21 and later): this spec amends its FN-001, FN-003, FN-005, FN-006, FN-015, SEC-009, DA-002 and UI-006. `docs/specs/archive/2026-09-07-changes--migrate-ad-authentication-to-cognito--auth-flow/` (`CHG-COGNITO-AUTH-001`): its SEC-005 is kept unchanged

---

## Executive Summary

A super admin can turn **password sign-in off for one Global Unit** with a new specificity,
`system_password_login_active` (default **on**). While it is off, an account that has no Cognito in that Global Unit
signs in with the **emailed code**, which becomes the default method: it is offered even if the code switch is off or
the account's domain is excluded, because an account may never have received its password. Accounts that sign in with
Cognito keep their current options. No combination of settings leaves an account without a method, and a server
without a valid code key falls back to the password. The server refuses a password the Global Unit does not allow,
and the welcome email stops sending a password the new account cannot use.

| Aspect | Today | After this spec, switch ON (default) | After this spec, switch OFF |
|---|---|---|---|
| Account without Cognito, code offered | Choice: code or password | Same | **Code only** |
| Account without Cognito, code not offered | Password only | Same | **Code only** (default method) |
| CGIAR account on a Cognito Global Unit | Cognito, plus the code if offered | Same | Same |
| Server has no valid code key | Password (code hidden) | Same | **Password** (last resort) |
| New account's welcome email | Carries the password | Same | Explains code sign-in, no password |

---

## Glossary

| Term | Meaning |
|---|---|
| **Global Unit (G)** | A row of `global_units` (for example AICCRA). The unit chosen in step 2 of the login page. Not a `crp_programs` program |
| **Password** | Any secret typed in the password field: the MARLO-stored password (`is_cgiar_user = 0`) or the Active Directory password relayed over LDAP (`is_cgiar_user = 1` on a Global Unit without Cognito) |
| **Cognito account in G** | An account with `is_cgiar_user = 1` in a Global Unit whose `cognito_auth_active` resolves on. Every other account is "without Cognito in G" |
| **Code key** | The server's `otp.hmac.secret` (or `OTP_HMAC_SECRET`). "Valid" means `CHG-OTP-LOGIN-001` treats the code feature as configured |
| **Code offered (today's rule)** | `system_otp_login_active` on in G and the email domain not in `system_otp_excluded_email_domains` of G (`CHG-OTP-LOGIN-001` FN-001, FN-003) |
| **Password switch** | The new specificity `system_password_login_active` of G |
| **Default method** | The code, offered to an account without Cognito in G while the password switch of G is off, whatever the code switch and the exclusion say |
| **Last resort** | The password, offered while the server has no valid code key, whatever the password switch says |

---

## 1. Overview

`CHG-OTP-LOGIN-001` added the emailed code as an **alternative** to the password. This spec lets a Global Unit make
it the **replacement**: one switch turns the password off, the code takes its place for every account that does not
use Cognito, and the welcome email follows. It ships with the switch on everywhere, so deploying it changes nothing
until a super admin turns it off.

---

## 2. Problem Statement

Claims about today's behavior carry their evidence inline; each is re-checked at Phase 2 into the design's Premise
Ledger.

- Every account without Cognito can always sign in with a password; no setting turns it off (`login.js:567` composes
  only `COGNITO` or `LOCAL`, and `LOCAL` always renders the password, `login.js:609-640`).
- A new external account gets a 6-digit numeric password mailed in clear (`CrpUsersAction.java:800`,
  `email.newUser.part1` argument `{3}` at `CrpUsersAction.java:1034`; the same generator in six more admin actions and in
  the centre `ManageUsersAction.java:146`).
- MARLO has no "forgot password" flow (`grep -rln -i "forgotPassword\|resetPassword\|changePassword\|recoverPassword"
  marlo-web/src/main/java marlo-web/src/main/webapp/WEB-INF` → 0 files, run 2026-10-16). A user who lost the password
  writes to support.
- On a Global Unit without Cognito, a CGIAR account's password is relayed to Active Directory over LDAP
  (`APCustomRealm.java:179-187`).
- The PRD's security acceptance (`docs/prd.md` §7.2) is better served by a mailbox-bound one-time code than by a
  6-digit password sent once by email.

---

## 3. In-Scope Requirements

### 3.1 Functional

- **CHG-PWD-SWITCH-001-FN-001** — Each Global Unit MUST have a password switch, `system_password_login_active`, that
  is **on** unless a super admin turns it off. A missing value MUST read as on.
- **CHG-PWD-SWITCH-001-FN-002** — A change to the switch MUST take effect on the next login attempt, with no restart.
- **CHG-PWD-SWITCH-001-FN-003** — While the switch of G is on, sign-in in G MUST behave exactly as it does after
  `CHG-OTP-LOGIN-001` T21.
- **CHG-PWD-SWITCH-001-FN-004** — While the switch of G is off and the code key is valid, an account **without
  Cognito in G** MUST be offered the code as its only method, even when the code switch of G is off and even when its
  domain is excluded in G.
- **CHG-PWD-SWITCH-001-FN-005** — A **Cognito account in G** MUST keep exactly today's methods whatever the password
  switch says: Cognito, plus the code only when the code switch is on and its domain is not excluded.
- **CHG-PWD-SWITCH-001-FN-006** — While the code key is not valid, the password MUST be offered to every account
  without Cognito in G, whatever the password switch says (last resort).
- **CHG-PWD-SWITCH-001-FN-007** — No combination of the Cognito flag, the code switch, the exclusion list, the password
  switch and the code-key state MAY leave an account that is active and a member of G with no method. When the only
  method is the code and the user typed a username in step 1, step 3 MUST tell the user to enter the email address
  instead and offer the way back to step 1 (FN-010).
- **CHG-PWD-SWITCH-001-FN-010** — When the code is the only method for the account in G and the identifier typed in
  step 1 is not an email address, step 3 MUST show the message to sign in with the email address instead, with the
  action to go back to step 1, and MUST NOT show a password field or a dead end. *(Added 2026-10-16, Judgment Day C-3.)*
- **CHG-PWD-SWITCH-001-FN-008** — When an admin creates an account for G and the password is not available to that
  account in G, the welcome email MUST omit the password and explain that the account signs in with a code sent to its
  email. In every other case the welcome email MUST stay as it is today.
- **CHG-PWD-SWITCH-001-FN-009** — Account creation MUST still store a password, so that turning the switch back on
  never leaves an account without a stored credential.

#### Scenario: Password off, code on, domain not excluded

- GIVEN G has the password switch off, the code switch on and an empty exclusion list, and the code key is valid
- WHEN an active external member of G reaches step 3
- THEN the code is the only method shown, directly, without the choice menu
- BUT a password posted directly to the password endpoints for G MUST be refused
- AND IT MUST be refused with the same message as a wrong password

#### Scenario: Password off, everything else off (default method)

- GIVEN G has the password switch off, the code switch off and Cognito off, and the code key is valid
- WHEN any active member of G, CGIAR or not, reaches step 3
- THEN the code is the only method shown
- AND the request-code endpoint MUST issue a code for that member in G

#### Scenario: Password off, domain excluded

- GIVEN G has the password switch off, the code switch on and `example.org` excluded, and the code key is valid
- WHEN an active member of G with an `@example.org` address and no Cognito reaches step 3
- THEN the code is the only method shown
- BUT the same member MUST see the password only, as today, once the password switch is back on

#### Scenario: Cognito account unaffected

- GIVEN G has Cognito on, the password switch off, the code switch on and `cgiar.org` excluded
- WHEN a member with `is_cgiar_user = 1` and an `@cgiar.org` address reaches step 3
- THEN only "Sign in with CGIAR" is shown, as today
- BUT a direct request for a code for that member in G MUST NOT issue a code

#### Scenario: Last resort

- GIVEN G has the password switch off and the server starts without a valid code key
- WHEN an active member of G without Cognito reaches step 3
- THEN the password is shown
- AND a password posted for G MUST be accepted under today's rules
- AND an ERROR line MUST record that the password was offered because the code key is missing

#### Scenario: Username typed, code only

- GIVEN G has the password switch off and the code key is valid
- WHEN an active member of G without Cognito types the username, not the email, in step 1 and reaches step 3
- THEN step 3 shows the message to sign in with the email address instead, with the action to go back to step 1
- BUT it MUST NOT show a password field
- AND IT MUST show the code once the member restarts with the email address

#### Scenario: Live change

- GIVEN G has the password switch off and a member is on step 3 with the code only
- WHEN a super admin turns the switch on and the member starts the login again
- THEN the member sees today's methods, with no restart of the application

#### Scenario: Welcome email

- GIVEN G has the password switch off and the code key is valid
- WHEN an admin of G creates an external account
- THEN the welcome email has no password and explains code sign-in for G
- AND a password MUST still be stored for the account
- BUT an account created the same way in a Global Unit with the switch on MUST receive today's email with its password

### 3.2 Security

- **CHG-PWD-SWITCH-001-SEC-001** — Every endpoint that accepts a password typed on the login page MUST refuse it when
  the password is not available to that account in the Global Unit of the attempt, **before** any credential check and
  before any LDAP relay. *(Exception: if the settings query itself fails, the endpoint behaves as today and logs an
  ERROR — fail-open, Decision Log 2026-10-16.)*
- **CHG-PWD-SWITCH-001-SEC-002** — That refusal MUST be indistinguishable, in response and message, from a wrong
  password, so it reveals nothing about the account.
- **CHG-PWD-SWITCH-001-SEC-003** — `CHG-COGNITO-AUTH-001` SEC-005 MUST hold unchanged: a Cognito account in G never
  signs in with a password in G.
- **CHG-PWD-SWITCH-001-SEC-004** — The request-code and verify-code endpoints MUST apply the same rule as step 3,
  including the default-method override, and MUST keep every neutrality guarantee of `CHG-OTP-LOGIN-001` (FN-005,
  SEC-006): an account that is not eligible gets the same answer as before this spec.
- **CHG-PWD-SWITCH-001-SEC-005** — The pre-auth lookup MUST NOT disclose more about an account than it does today.
  Whether the password is available in G MUST be derivable from what the lookup already returns plus the Global Unit's
  own settings.
- **CHG-PWD-SWITCH-001-SEC-006** — Authentication that does not go through the login page MUST keep working unchanged:
  the CLARISA technical account (`ClarisaPublicAccesFilter.java:79`) and HTTP Basic authentication on `/api/**`
  (`MarloShiroConfiguration.java:140`). The switch governs the login page only. *(Amended 2026-10-16, Judgment Day
  C-1: `/api/**` Basic is out of scope as an accepted risk; a stored password keeps working there. Amended 2026-10-18:
  `/api/**` Basic is not used in MARLO today, so its part of AC-12 is N/A.)*
- **CHG-PWD-SWITCH-001-SEC-007** — Switching Global Unit inside an authenticated session MUST stay as it is today.
  This is an accepted risk (Decision Log), not an oversight.

### 3.3 Operations

- **CHG-PWD-SWITCH-001-OPS-001** — Every refusal of a password because of the switch MUST log one INFO line naming the
  gate and the Global Unit, without the password.
- **CHG-PWD-SWITCH-001-OPS-002** — The last-resort path MUST log one ERROR line, at most once per Global Unit per
  application start, the first time the password is offered in a Global Unit only because the code key is missing.
  It MUST NOT be possible to repeat the line from an unauthenticated request. *(Amended 2026-10-16, Judgment Day W-4.)*
- **CHG-PWD-SWITCH-001-OPS-003** — The runbook MUST state the prerequisites for turning the switch off in production
  (mail relay proven for external domains, stage 1 delivery data), how to turn it back on from System Admin or with one
  SQL statement, and which log lines to watch.

### 3.4 Data & Migration

- **CHG-PWD-SWITCH-001-DA-001** — The switch MUST ship as a Flyway migration that seeds `system_password_login_active`
  (boolean, default `true`) for the existing Global Unit types among 1–4, is idempotent and assumes no Global Unit id.
  Its constants MUST be declared in both `APConstants.java`, equal to the key.
- **CHG-PWD-SWITCH-001-DA-002** — Its System Admin description MUST be, verbatim: "Lets users of this Global Unit sign
  in with a password on the login page. Turning it off makes the one-time code sent to their email the way to sign
  in, for everyone who does not use Sign in with CGIAR, even if system_otp_login_active is off or their email domain is
  excluded. Users of Sign in with CGIAR are not affected. If the server has no code key, the password stays available."
- **CHG-PWD-SWITCH-001-DA-003** — The same migration MUST update the descriptions of `system_otp_login_active` and
  `system_otp_excluded_email_domains` so that each says that, while password sign-in is off, it applies only to users
  of Sign in with CGIAR (FN-005). The exact texts are fixed in `design.md`. *(Amended 2026-10-16, Judgment Day W-3.)*

### 3.5 UI

- **CHG-PWD-SWITCH-001-UI-001** — When an account has a single method in G, step 3 MUST show it directly, without the
  choice menu. When it has two, step 3 MUST show the existing choice menu with the code first.
- **CHG-PWD-SWITCH-001-UI-002** — The "code only" view MUST reuse the existing code panel and its states (sending,
  sent, cooldown, error), with no password field in the document and no "Choose another way to sign in" link.
- **CHG-PWD-SWITCH-001-UI-003** — The welcome-email text MUST be i18n-keyed, in `global.properties` and in
  `custom/aicrra`, `custom/aiccra3` and `custom/test`, with apostrophes doubled. The other custom files that override
  `email.newUser.part1` MAY fall back to the global text. *(Amended 2026-10-16, Judgment Day C-4.)*
- **CHG-PWD-SWITCH-001-UI-005** — The FN-010 message MUST be i18n-keyed in `global.properties`.
- **CHG-PWD-SWITCH-001-UI-004** — Changed JavaScript and FreeMarker assets MUST bump their cache-buster.

### 3.6 Non-Functional

- **CHG-PWD-SWITCH-001-NF-001** — The rule MUST be decided by one server-side component; step 3, the password
  endpoints, the code endpoints, the pre-auth lookup and the welcome email MUST all use it.
- **CHG-PWD-SWITCH-001-NF-002** — Reading the switch MUST NOT add a database round trip to the pre-auth lookup beyond
  the uncached query `CHG-OTP-LOGIN-001` already runs for the code settings: at most one query per lookup. That query
  now also runs where `CHG-OTP-LOGIN-001` skipped it (key missing, username identifier). *(Note 2026-10-16, Judgment
  Day re-judgment.)*

---

## 4. Out-of-Scope

| Item | Why |
|---|---|
| Re-authentication when switching Global Unit inside a session | Accepted risk (SEC-007) |
| A second switch for the Active Directory password | Cognito already covers that case (proposal §11) |
| A "forgot password" flow | The code is the recovery path where the switch is off |
| Deleting or rotating stored passwords | Not needed; stored passwords stay for when the switch is turned back on |
| The welcome text for Cognito accounts ("(Your Outlook Password)") | Separate improvement |
| Turning the switch off in any production Global Unit | Operational decision behind OPS-003 |

---

## 5. Personas Affected

| Persona | Effect |
|---|---|
| External partner staff | With the switch off: code only. With it on: unchanged |
| `@cgiar.org` accounts with `is_cgiar_user = 0` | Treated as accounts without Cognito: code only when the switch is off |
| CGIAR accounts on a Cognito Global Unit | Unchanged |
| CGIAR accounts on a Global Unit without Cognito | With the switch off: code only (no more Active Directory password relay) |
| Global Unit admins creating accounts | The welcome email adapts on its own |
| Super admins | One new specificity and two reworded descriptions |
| MARLO Support | Fewer "lost password" requests where the switch is off; must know the last-resort log line |

---

## 6. Defect classes and their gates

| Defect class | Gate | Automated? |
|---|---|---|
| A password accepted where the switch forbids it (direct POST) | Unit tests on both password endpoints + a live `curl` probe on a throwaway copy | Yes |
| An account left with no method | A unit test that enumerates every combination of the six inputs of AC-9 | Yes |
| Step 3 shows the wrong methods | Browser walkthrough of every row of the Executive Summary table on a throwaway copy | **No** — `login.js` has no test harness; substitute: claude-in-chrome walkthrough recorded, reviewed at the HITL pause |
| The switch read through a cache (change not live) | Live-change probe; falsifier: read through the Hibernate query cache | Yes (probe) |
| Missing key does not fall back | Unit test with an unconfigured code key + live start with the key blank | Yes |
| Neutrality broken on the code endpoints | Existing `CHG-OTP-LOGIN-001` neutrality tests and `ac-probe.sh enum`, re-run | Yes |
| A welcome-email sender left on the old text | Unit test of the shared helper + one grep per sender proving all 7 call it | Yes |
| A password entry point missed | Recorded enumeration of every `userManager.login` caller, every `UsernamePasswordToken` and every Shiro filter that authenticates with a password (`authcBasic`, `authc`) | Inspection |
| Unsafe migration | Migration gate on a throwaway copy, twice, plus a database without units 45/47 | Yes |
| i18n apostrophe or brace dropped | i18n gate on every changed `.properties` line | Yes |
| Code that does not compile | Clean compile gate | Yes |

---

## 7. Acceptance Criteria

| AC | Requirement | Criterion |
|---|---|---|
| AC-1 | FN-001, DA-001 | After the migration, every Global Unit reads the switch as on; a unit with no `custom_parameters` row reads it as on |
| AC-2 | FN-003 | With the switch on everywhere, the existing `CHG-OTP-LOGIN-001` suites and `ac-probe.sh` stages pass unchanged |
| AC-3 | FN-004, UI-001, UI-002 | Scenario "Password off, code on, domain not excluded" |
| AC-4 | FN-004, SEC-004 | Scenario "Password off, everything else off" |
| AC-5 | FN-004 | Scenario "Password off, domain excluded" |
| AC-6 | FN-005, SEC-003 | Scenario "Cognito account unaffected" |
| AC-7 | FN-006, OPS-002 | Scenario "Last resort" |
| AC-8 | FN-002 | Scenario "Live change" |
| AC-9 | FN-007, FN-010 | Every combination of Cognito flag × code switch × exclusion × password switch × key state × identifier shape (email or username) yields at least one method, or the FN-010 message for a username |
| AC-15 | FN-010, UI-005 | Scenario "Username typed, code only" |
| AC-10 | SEC-001, SEC-002, OPS-001 | A password POST to each password endpoint for G with the switch off returns the wrong-password response, makes no credential check and no LDAP call, and logs one INFO line |
| AC-11 | SEC-005 | The pre-auth lookup's response for an account differs from today's only by the new per-unit field, and that field is the same for every account of the same kind in G |
| AC-12 | SEC-006 | The CLARISA technical login and an `/api/**` Basic request still authenticate with the switch off. *(Amended 2026-10-18: the `/api/**` part is N/A, because HTTP Basic on `/api/**` is not used in MARLO today; user decision, Decision Log)* |
| AC-13 | FN-008, FN-009, UI-003 | Scenario "Welcome email", for each of the 7 senders |
| AC-14 | DA-002, DA-003 | The three descriptions in `parameters` equal the fixed texts byte for byte |

---

## 8. Constitutional Compliance Checklist

- [x] Phase replication: not applicable — no phased data.
- [x] Save validation: not applicable — no section save path changes.
- [x] Permissions: no new action; existing interceptor stacks unchanged.
- [x] Specificity: `system_password_login_active` through `parameters` + `custom_parameters`, constants in both `APConstants.java` (DA-001).
- [x] Migrations: one Flyway migration with the `V<...>__<Description>.sql` naming (DA-001).
- [x] i18n: new welcome-email text keyed in `global.properties` and three `custom/*.properties` (UI-003); FN-010 message in `global.properties` (UI-005).
- [x] License header: any new Java file carries the GPL header.
- [x] Code style: Checkstyle delta 0; 2-space indent; 120-character lines.
- [x] REST: not applicable — no `/api/*` change.
- [x] Audit: specificity changes are audited by the existing System Admin flow.
- [x] Dependency floors: no dependency change.
- [x] Branching: `A2-2631-Implement-Center-Email-OTP-Authentication` (proposal OQ-2).

---

## 9. Open Questions

| ID | Question | Blocks | Default used here |
|---|---|---|---|
| OQ-1 | Parent epic of the Jira issue | Jira only | — |
| OQ-3 | Production counts of accounts that would move to code only | Turning the switch off | Measure before (OPS-003) |
| OQ-5 | With the password off, should a Cognito account also get the code as default method? | — | **Resolved 2026-10-16 (Kenji Tanaka):** no; FN-005 as written (Decision Log) |

OQ-2 and OQ-4 of the proposal and OQ-5 are resolved (Decision Log).

---

## 10. Decision Log

- 2026-10-16 — One switch per Global Unit, default on, covering the MARLO password and the Active Directory password (user decision). Rationale: Cognito already covers the Active Directory case.
- 2026-10-16 — Rule 3d: with the password off, the code is the default method for every account without Cognito, even with the code switch off or the domain excluded (user decision: "si todos están apagados debe quedar por defecto el código porque puede que un usuario no haya recibido contraseña en su momento"). Replaces rule 3c of the design conversation.
- 2026-10-16 — Last resort: without a valid code key the password stays available (user decision, proposal OQ-4). Rationale: a missing key is a server fault, not a Global Unit choice.
- 2026-10-16 — Switching Global Unit inside a session stays as today (user decision: "no hay problema con el global unit luego de que tenga acceso"). Accepted risk: a member who signs in where the password is allowed can move to a Global Unit where it is not. The same already holds for Cognito.
- 2026-10-16 — The welcome email gets an alternative sign-in block when the password is not available; a random password is still stored (user decision after reviewing the rendered AICCRA email).
- 2026-10-16 — The spec lives on `A2-2631-Implement-Center-Email-OTP-Authentication` (user decision, proposal OQ-2). Rationale: the code depends on `CHG-OTP-LOGIN-001` T21.
- 2026-10-16 — Cross-spec amendment: `CHG-OTP-LOGIN-001` FN-001, FN-003, FN-005, FN-006, FN-015, SEC-009, DA-002 and UI-006 are amended: while password sign-in is off, the code switch and the exclusion apply only to Cognito accounts, step 4 no longer answers `domainNotEnabled` for that unit, a Cognito account is not admitted by the override (the code switch and the exclusion still apply to it as written), and a single method is shown directly; DA-002's two descriptions are reworded (DA-003). Recorded in that spec's Decision Log by the task that ships it.
- 2026-10-16 — OQ-5: a Cognito account in G keeps today's methods whatever the password switch says (user decision, on the recommendation that rule 3d solves a missing-password problem those accounts do not have, and that a `cgiar.org` exclusion must keep CGIAR users on Cognito). Consequence: while the password is off, the code endpoints check after the account lookup whether the account is a Cognito account in G, with the same neutral answer.
- 2026-10-16 — Judgment Day round 1 (two blind judges, `judgment.md`): four confirmed severe findings, corrected on the user's choice ("aplica la corrección con las tres recomendaciones"):
  - **C-1** `/api/**` HTTP Basic accepts a password outside the login page. Out of scope, accepted risk: the switch governs the login page; a separate issue reviews API authentication. SEC-006 amended.
  - **C-2** the password gate must take the attempt's unit only when the account is a member of it, otherwise sweep every membership (design DD-5).
  - **C-3** a username in step 1 cannot receive a code; FN-010 adds the "enter your email" message instead of a dead end.
  - **C-4** the welcome-email copy is required in `global` plus `aicrra`, `aiccra3`, `test`; five legacy custom files fall back to the global text. UI-003 amended.
- 2026-10-16 — Judgment Day round 1, re-judgment (both judges): one new severe finding and warnings, corrected in the final round:
  - **N-1** the last-resort ERROR could not fire, because no reader saw the switch while the key was missing. The pre-auth lookup now always reads the unit settings and decides the ERROR (design DD-6).
  - **Fail-open exception to SEC-001:** if the settings query fails, the password gate and the pre-auth lookup behave as today (password allowed) and log an ERROR. A database fault must not become a lock-out; the login itself already fails on such a fault.
  - **Timing, accepted:** a password refused by the switch answers faster, so response time can show that an account belongs to a password-off unit. The pre-auth lookup already lists an account's Global Units to anyone who types its email.
- 2026-10-16 — Execute-time decisions on T05 (user decisions during `/akili-execute`, `execution.md` T05):
  - **SEC-002 residual, accepted:** an active CGIAR account that a directory search or bind would reject before checking its password gets the CGIAR wrong-password message on refusal. That covers an account with no directory entry, a locked or expired directory account, and a directory outage. A real failure would show the directory's own answer instead. Reproducing that answer needs an LDAP call before the refusal, which SEC-001 and AC-10 forbid. What leaks is that the switch is off in the unit, which step 3 already shows. The case is pinned by a test and checked in the T08 walkthrough.
  - **SEC-002 includes the remember-me cookie:** a refusal must send the same `Set-Cookie: rememberMe=deleteMe` that Shiro sends after a failed `Subject.login`. Shiro's failed-login handling is invoked without a credential check, so SEC-001 still holds.
  - **Out of scope, separate bugfix spec:** `finishLogin` grants a session through the activity-blind `existCrpUser`, so a removed member of a password-off unit can still sign in with a password there. Also, the SEC-005 guard compares the raw, unnormalised identifier. Both predate this change.
- 2026-10-18 — `/api/**` HTTP Basic is not used in MARLO today (user decision during `/akili-test`). The `/api/**` part
  of AC-12 and of SEC-006 is N/A, not a gap; the separate issue foreseen by Judgment Day C-1 is not needed. Its local
  401 (`HibernateException` in `APCustomRealm` → `getUserByEmail`) is not chased. If `/api/**` comes back into use,
  re-check it with `probes/ac-live.sh ac12`.
