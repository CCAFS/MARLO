# Password Login Switch — Design

**Spec ID:** `CHG-PWD-SWITCH-001`
**Status:** Approved — Kenji Tanaka, 2026-10-16 (Judgment Day APPROVED after two correction rounds)
**Owner:** IBD Team (Kenji Tanaka)
**Last Updated:** 2026-10-18
**Depth:** Full
**Requirements:** [`requirements.md`](./requirements.md)
**Baseline commit:** `160ec7dc4a` (every `file:line` below was read there)
**Exploration:** one read-only scout, citations as run; findings carried into §17 (Premise Ledger)

---

## Executive Summary

One pure rule decides, for an account in a Global Unit, which of **Cognito, code and password** it may use. Every
consumer reads that rule: the pre-auth lookup that feeds step 3, the two password endpoints, the two code endpoints
and the welcome email. The password switch rides on the uncached query `CHG-OTP-LOGIN-001` already runs: at most one
query per lookup, now also run when the key is missing or the identifier is a username (NF-002 note). It is parsed **on unless the value is exactly `false`**, because the shared reader turns a
missing value into an empty string. The only account-aware part of the rule (is this a Cognito account?) uses the
same Cognito resolver as step 3, read per unit before the account lookup, so neutrality holds.

---

## 1. Architecture Summary

```
                       SignInMethodRule (pure, new)
   inputs: keysConfigured · cognitoAccountInG · passwordSwitch · codeSwitch · exclusion · email
   output: { COGNITO?, CODE?, PASSWORD? }
        ▲              ▲                 ▲                  ▲                    ▲
        │              │                 │                  │                    │
 crpByEmail.do   validateUser.do     otpRequestCode.do   otpVerifyCode.do    welcome email
 (passwordEnabled,  login.do          (step 4 + isEligible) (FN-015 re-check)  (7 admin senders,
  otpEnabled)       (password gate)                                            via one helper)
        │
     login.js step 3: 1 method → shown directly · 2 methods → choice menu
```

Per-unit settings come from one uncached native query (`OtpChallengeMySQLDAO` SPECIFICITY_SQL) that already reads the
two code keys; the password key joins the same key list.

### The rule (normative for every consumer)

For an account in Global Unit G (all values read for G). The code also needs an **email** identifier: when step 1
received a username, the code is not usable and FN-010 shows the "use your email" message instead.

| Method | Available when |
|---|---|
| **Cognito** | the account is a Cognito account in G (`is_cgiar_user = 1` and `cognito_auth_active` resolves on) |
| **Code** | the key is configured **and** (code switch on **and** domain not excluded) — **or** — the key is configured **and** the password switch is off **and** the account is not a Cognito account in G (rule 3d) |
| **Password** | the account is not a Cognito account in G **and** (the password switch is on **or** the key is not configured) |

Consequences, checked by the exhaustive test of the rule's task: a non-Cognito account always has the code or the
password, or — when only the code is left and the identifier is a username — the FN-010 message; a Cognito account
always has Cognito; at most two methods at once.

---

## 2. Module Footprint

### marlo-web
- **New** `security/otp/SignInMethodRule.java` — the pure rule of §1, plus `isPasswordSwitchOn(String)` ("on unless the
  trimmed value equals `false`, ignoring case").
- `security/otp/OtpEligibility.java`:
  - key list gains `system_password_login_active` (one query, DD-3);
  - gets `CustomParameterManager` and `ParameterManager` back for `CognitoAuthSpecificity` (DD-4);
  - `isOffered(email, G)` (step 4, account-blind) admits the override;
  - `isEligible(email, G)` refuses a Cognito account under the override, after `existActiveCrpUser` (DD-4);
  - **new** `methodsFor(identifier, units, cognitoAccountByUnit)` → per unit `{otpEnabled, passwordEnabled}` for
    `crpByEmail.do` (replaces the call to `otpEnabledFor`, DD-6);
  - **new** `passwordGate(identifier, unitIdOrNull)` → `PasswordGateDecision` for the password gate (DD-5): `isRefused()` plus
    `leaveRealmMessage(session)`, which leaves the message the realm would leave for that account kind, plus
    `leaveRememberMeFailure(...)`, which runs Shiro's failed-login remember-me handling without a credential check (amended at
    execute time, T05);
  - **new** once-per-unit ERROR memory for the last resort (§11).
- `action/home/OtpRequestCodeAction.java` — step 4 uses the widened `isOffered`; no other step moves.
- `action/home/OtpVerifyCodeAction.java` — no logic change; inherits through `OtpEligibility.isEligible` (`:280`). Its
  constructor changes because `LoginAction`'s does.
- `action/json/global/CrpByUserEmailAction.java` — computes `cognitoEnabled` per unit **first**, then one
  `methodsFor` call; adds `passwordEnabled` per unit (DD-6).
- `action/json/global/ValidateUserAction.java`, `action/home/LoginAction.java` — the password gate (DD-5);
  `OtpEligibility` injected. `LoginAction`'s constructor change reaches its subclasses `OtpVerifyCodeAction` and
  `CognitoCallbackAction`.
- **New** `utils/WelcomeSignInText.java`, a `@Named` bean injected into the 7 senders (DD-7).
- The 7 senders: `CrpUsersAction`, `CrpProgamRegionsAction`, `CrpPpaPartnersAction`, `CrpSiteIntegrationAction`,
  `CrpAdminManagmentAction`, `ProjectPartnerAction`, `ClusterActivitiesAction`.
- `config/APConstants.java` — `PASSWORD_LOGIN_ACTIVE = "system_password_login_active"`.
- Webapp: `global/js/login/login.js`; `WEB-INF/global/pages/loginForm.ftl` (the FN-010 message block); cache-buster in
  `WEB-INF/global/views/login/login.ftl:4` and `WEB-INF/global/pages/error/401.ftl:4`.
- Resources: `email.newUser.part1.codeSignIn` in `global.properties`, `custom/aicrra`, `custom/aiccra3`,
  `custom/test` (UI-003); the FN-010 message key in `global.properties` (UI-005).
- Docs: **new** `docs/specs/changes/password-login-switch/runbook.md` (OPS-003); amendments in
  `docs/specs/changes/center-email-otp-login/` (DD-8).

### marlo-data
- `config/APConstants.java` — the same constant.
- No DAO or manager change: the key list is a bound parameter (`OtpChallengeMySQLDAO.java:140`).

### marlo-utils
- None.

---

## 3. Data Model Changes

### Migration
`V2_6_0_<YYYYMMDD>_<HHMM>__AddPasswordLoginSwitch.sql`, timestamp from the real clock (`marlo-migration` skill),
modelled on `V2_6_0_20261015_1418` (`:22-44`):
- Seed `system_password_login_active` for `global_unit_types` 1–4 that exist, guarded by `NOT EXISTS`: format `1`
  (boolean), default `'true'`, category `2`, description = DA-002 text verbatim.
- `UPDATE parameters SET description = …` for `system_otp_login_active` and `system_otp_excluded_email_domains`, all
  types, to the texts below. Idempotent by nature.
- No `custom_parameters` row: every unit starts on.
- DML only; no assumed Global Unit id; the applied migrations `V2_6_0_20261009_1025` and `V2_6_0_20261015_1418` are
  not edited.

**Fixed texts (DA-003):**
- `system_otp_login_active`: "Lets users of this Global Unit sign in on the login page with a one-time code sent to
  their email, as an alternative to their password or to Sign in with CGIAR. Only registered users who are active and
  members of this Global Unit receive a code. Turning it off removes the option on the next login, and users whose
  email domain is listed in system_otp_excluded_email_domains do not receive a code. Exception: while
  system_password_login_active is off and the server has a code key, everyone who does not use Sign in with CGIAR signs
  in with the code, whatever this setting and the exclusion say."
- `system_otp_excluded_email_domains`: "Email domains that cannot sign in with a one-time code in this Global Unit,
  even when system_otp_login_active is on. Write exact domains separated by commas, for example
  cgiar.org,example.org. A subdomain is only excluded if it is listed itself. Leave it empty to exclude no one.
  Exception: while system_password_login_active is off, the exclusion applies only to users of Sign in with CGIAR."

### Entity changes · Indices · Backfill
Not applicable — no schema change.

---

## 4. API / Action Surface

| Endpoint | Change |
|---|---|
| `crpByEmail.do` | Each unit entry gains `passwordEnabled` (boolean). `otpEnabled` follows the rule (it can be true with the code switch off; it is false for a username identifier). No user field added |
| `validateUser.do` | Refuses a password the attempt's unit does not allow (DD-5), before `userManager.login` (`:118`) |
| `login.do` | Same refusal before `userManager.login` (`:348`) |
| `otpRequestCode.do` | Step 4 (`:377-379`) admits the override; step 6 (`isEligible`) excludes Cognito accounts from it |
| `otpVerifyCode.do` | Unchanged code; FN-015 re-check inherits the rule |

No new action, no new interceptor stack, no `/api/*` change.

---

## 5. Frontend Composition

`login.js` `showPasswordStep()` (`:497-648`) gains two LOCAL arms, driven by a new card attribute
`data-password-enabled`, set in `loadAvailableItems` next to the existing ones (`:761-764`) and read at **both**
capture sites (`:96-97`, `:523-524`):

| Mode | `otpEnabled` | `passwordEnabled` | View |
|---|---|---|---|
| COGNITO | false | — | unchanged (`:569-589`) |
| COGNITO | true | — | unchanged (`:590-605`) |
| LOCAL | true | true | unchanged choice (`:633-634`) |
| LOCAL | false | true | unchanged direct password (`:635-639`) |
| LOCAL | true | **false** | **new, code only:** `#login-password` removed as in the COGNITO arms; `showMethodView("code")` directly; `#login-method-code-back` hidden; `#login-method-info` filled with the masked destination (today it is filled only by the choice click, `:342-357`). `resetMethodView` shows the back link again |
| LOCAL | false | **false** | **new, FN-010:** only possible for a username identifier. Shows the "use your email" message and the existing "use a different email" action back to step 1; no password field |

A missing `data-password-enabled` (an old cached page against a new server) reads as `true`. Cache-buster
`login.js?<new date>` in both FTL references. No CSS change.

---

## 6. Persistence & Phase Replication Plan
Not applicable — no phased entity is read or written.

## 7. Validation & Save Pipeline
Not applicable — no save path. The password gate is an authentication check, not a validator.

## 8. Permissions & Edit Gates
Not applicable — no authorization change; `finishLogin` and its membership gate are untouched.

---

## 9. Specificity / Feature-Flag Strategy

| Key | Read by | Missing value means |
|---|---|---|
| `system_password_login_active` (new) | the uncached SPECIFICITY_SQL, through `OtpEligibility` | **on** (DD-2) |
| `system_otp_login_active`, `system_otp_excluded_email_domains` | same query | off / empty (unchanged) |
| `cognito_auth_active` | `CognitoAuthSpecificity.isActiveFor` (unchanged resolver, also used by step 3) | off (unchanged) |

Constants in both `APConstants.java` (`marlo-web` `:150-156`, `marlo-data` `:126-132` hold the neighbours).

---

## 10. Integration Points
Mail only: the welcome email path through `SendMailS`, unchanged except for its body text. No external service.

## 11. Observability

| Event | Level | Line |
|---|---|---|
| Password refused by the switch | INFO | `auth.password.refused gate=password_switch gu=<acronym>` (no email, no password) — OPS-001 |
| Password offered as last resort | ERROR | `auth.password.last_resort gu=<acronym> reason=code_key_unconfigured`, **at most once per Global Unit per start** (an in-memory set in `OtpEligibility`), so an unauthenticated caller cannot repeat it — OPS-002 |
| Code issued by the override | existing `auth.otp.request outcome=sent` plus `via=password_off` | — |

The existing `auth.otp.keys outcome=unconfigured` WARN (once per start) stays as `CHG-OTP-LOGIN-001` defines it.

## 12. Performance & Scalability
`crpByEmail.do` makes **at most one** uncached query per call (NF-002): the new key joins the bound key list. Where
today it makes none (key missing, or a username), it now makes that one query, because `passwordEnabled` needs the
switch (DD-6). The Cognito
resolver is already called per unit there (`:131-132`). The password endpoints add, before the credential check, one account lookup, one membership read and one call to
the same query (for the attempt's unit, or for every membership in the sweep, DD-5). Today `ValidateUserAction:241`
returns before the membership read for accounts with `is_cgiar_user = 0`; with this change every password POST pays
it. The code endpoints add one Cognito resolution per request, only while the password switch of G is off.

---

## 13. Security Considerations

- **Server-side refusal before credentials (SEC-001).** The gate runs before `userManager.login` in both endpoints,
  so no hash comparison and no LDAP bind happens. It answers exactly what a wrong password answers for the account's kind
  (SEC-002, amended at execute time, T05): no message for a local account, `ERROR_LOGON_FAILURE` for a CGIAR account,
  `USER_DISABLED` for an inactive one. It also sends the remember-me `deleteMe` cookie of a failed login. Accepted
  residual: CGIAR states that only the directory reveals (requirements Decision Log, 2026-10-16 T05).
- **Membership-scoped gate (DD-5).** The attempt's unit counts only when the account is a member of it; otherwise the
  gate sweeps every membership. A caller cannot name a foreign password-on unit to get a credential check (the defect
  the SEC-005 guard already fixed, `ValidateUserAction.java:246-266`).
- **Timing.** A refusal skips the hash or LDAP work, and only members of a password-off unit can be refused, so
  response time can reveal that an account belongs to a password-off unit. Accepted (requirements Decision Log): the
  pre-auth lookup already lists an account's Global Units to anyone who types its email (`CrpByUserEmailAction`
  `:118-145`), and each unit's switch is visible in step 3 (`passwordEnabled`).
- **Neutral override (SEC-004).** Step 4 stays account-blind. For every request on a password-off unit the unit's
  Cognito flag is resolved **before** the lookup, whether the account exists or not. The `is_cgiar_user` refusal sits
  **after** `existActiveCrpUser`, so every account that reaches the lookup pays the same queries; a refusal returns the
  neutral `sent` and a decoy row (`OtpRequestCodeAction:394-409`).
- **Pre-auth disclosure (SEC-005).** `passwordEnabled` is a function of the unit's settings, the key state and
  `isCgiarUser && cognitoEnabled`, all already in the response or public (`CrpByUserEmailAction:116`, `:131-132`).
- **Outside the login page (SEC-006).** `ClarisaPublicAccesFilter.java:79-82` and `/api/**` HTTP Basic
  (`MarloShiroConfiguration.java:140`) are not touched. A stored password keeps working on `/api/**` while the switch
  is off — accepted risk (Judgment Day C-1). *(Amended 2026-10-18: `/api/**` Basic is not used in MARLO today, so no
  separate issue is needed; requirements Decision Log.)*
- **Session switch (SEC-007).** `ValidSessionCrpInterceptor` is not touched (accepted risk).

---

## 14. Backwards Compatibility & Rollout

1. **Deploy.** The migration seeds the switch **on** for every type; `crpByEmail.do` reports `passwordEnabled = true`
   everywhere; behavior equals T21.
2. **Pilot.** Prerequisites (OPS-003): relay proven for external domains, stage 1 delivery data, production counts
   (OQ-3). Turn the switch off on one Global Unit in System Admin.
3. **Rollback, cheapest first:** turn the switch back on (next login); `UPDATE custom_parameters` to `true`; revert the
   deploy. Stored passwords were never touched.

---

## 15. Decision Records

### DD-1 — One pure rule, consumed everywhere
- **Decision:** `SignInMethodRule` holds the table of §1; every consumer calls it.
- **Why:** NF-001; six consumers (`crpByEmail.do`, `validateUser.do`, `login.do`, request-code, verify-code, the
  welcome email) drifting apart is the defect this prevents.
- **Rejected:** per-consumer conditions (the drift risk `CognitoAuthSpecificity` was created to remove, PS-16).

### DD-2 — The password switch is on unless it says `false`
- **Decision:** parse with `isPasswordSwitchOn`, not `OtpDomainPolicy.isSwitchOn`.
- **Why:** the shared reader returns `""` for a missing unit, type row or value (P-3); with the code switch that reads
  as off, which is safe there and a lock-out here (FN-001).
- **Rejected:** relying on the seeded default alone (a type without the row, or an unknown unit, would read off).

### DD-3 — The switch rides on the existing uncached query
- **Decision:** add the key to `OtpEligibility`'s key list (`:83-84`).
- **Why:** NF-002 and FN-002 with no DAO change (P-4).

### DD-4 — Cognito state through the existing resolver, read per unit before the lookup
- **Decision:** `OtpEligibility` gets `CustomParameterManager` and `ParameterManager` back and calls
  `CognitoAuthSpecificity.isActiveFor`. In the code endpoints it is resolved for **every** request on a password-off
  unit, before the account lookup; the `is_cgiar_user` refusal runs after `existActiveCrpUser`. On a password-on unit it
  is never called. `crpByEmail.do` passes the per-unit value it computes.
- **Why:** step 3 uses that resolver (`CrpByUserEmailAction:131-132`); a second reading of the same flag could disagree
  with it (PS-16 of `CHG-COGNITO-AUTH-001`). Reading it per unit, before the lookup, keeps known and unknown accounts on
  the same queries.
- **Rejected:** adding `cognito_auth_active` to the uncached key list — a fourth reader of the flag.
- **Reversion challenge (Step 2.3), extended by Judgment Day W-1:** this partly reverts T21's "no `is_cgiar_user`, no
  `CognitoAuthSpecificity` on the code path". *What does removing it break?*
  1. **Four T21 tests** pin that shape: two whole-file static locks (`OtpEligibilityTest.java:424-433`, `:436-442`),
     the constructor-signature lock and the Spring-scan test (`OtpRequestCodeActionTest.java:930-951`, `:968-975`).
     They are **replaced**, not loosened: behavioral tests with counting doubles prove that on a password-on unit
     `OtpEligibility` makes **zero** calls to the Cognito resolver and never reads `is_cgiar_user`, and the scan test
     registers the two managers.
  2. **SEC-009 of `CHG-OTP-LOGIN-001`** becomes false under the override — amended (DD-8).
  3. **Timing** — closed by the read order above.
  No remaining breakage found.

### DD-5 — The password gate is membership-scoped
- **Decision:** both endpoints refuse a password, right before the credential call, with the wrong-password response for
  the account's kind (§13; amended at execute time, T05) and
  one INFO line, when the code key is configured and the switch is off for **the unit of the attempt**.
- **Unit of the attempt:** `validateUser.do` takes its `globalUnitId` parameter (`ValidateUserAction:59`) and
  `login.do` its `crp` acronym (`LoginAction:310`) — **only when the account is a member of that unit**. When the unit
  is missing, unknown or not a membership, the gate sweeps every membership of the account and refuses if any has the
  password off. Same narrowing and fail-closed sweep as the SEC-005 guard (`ValidateUserAction.java:246-266`).
- **Unknown account:** no membership, so the gate does not refuse; the credential check fails as today.
- **Query failure:** the gate treats the switch as on (today's behavior) and logs an ERROR. This is a deliberate
  **fail-open** exception to SEC-001, unlike the SEC-005 guard's fail-closed sweep, recorded in the requirements
  Decision Log. Rationale: a failing database already fails the login itself; the switch must not turn a database
  fault into a lock-out.
- **Key missing:** the gate never refuses (last resort); it does not need to log, since the ERROR is decided in
  `crpByEmail.do` (DD-6).
- **Why:** SEC-001/002 without the foreign-unit bypass (Judgment Day C-2).

### DD-6 — `otpEnabled` and `passwordEnabled` are computed on the server, in one call
- **Decision:** `CrpByUserEmailAction` makes two passes: the first computes `cognitoEnabled` per unit (today's loop,
  `:122-145`), then one `methodsFor(identifier, units, cognitoAccountByUnit)` call, then a second pass writes
  `otpEnabled` and `passwordEnabled` into each unit entry (today `otpEnabled` is written inside the loop at `:135`, from
  the map computed at `:120`).
- **Shape:** per unit `{otpEnabled, passwordEnabled}` from **one** uncached query, which **always runs** when there is
  at least one unit — also when the code key is missing and when the identifier has no domain — because
  `passwordEnabled` needs the switch value. Today's early exit before the query (`OtpEligibility.java:159-161`) is
  removed, and its test lock (`CrpByUserEmailActionTest.java:314`, "unconfigured makes no specificity query") is
  replaced by one that asserts a single query. Key missing → `otpEnabled = false`, and `passwordEnabled = true` for
  non-Cognito accounts; when the switch of that unit is off, this is the last resort and the once-per-unit ERROR of §11
  is decided here (Judgment Day N-1). A username → `otpEnabled = false`; `passwordEnabled` follows the rule, so a
  password-off unit gives the FN-010 view.
- **Failure:** if the query throws, `otpEnabled = false` and `passwordEnabled = true` for non-Cognito accounts (today's
  behavior), with an ERROR line — the same fail-open choice as the gate's DD-5 failure path, recorded as an exception to
  SEC-001 in the requirements Decision Log.
- **Why:** NF-001, NF-002; `login.js` stays a renderer.

### DD-7 — The welcome email uses one bean and one alternative key
- **Decision:** `WelcomeSignInText` is a `@Named` bean (it needs `OtpEligibility` and `OtpKeys`), injected into the 7
  senders. `part1(action, user, unit, password, supportText)` returns `email.newUser.part1` (today's text) when the
  password is available to that account in that unit — a Cognito account keeps today's "(Your Outlook Password)" text —
  and `email.newUser.part1.codeSignIn` otherwise. Same five arguments; `{3}` becomes the unit acronym.
- **Unit passed:** `CrpUsersAction` passes its `globalUnit` (`:725`, `:816`); the other six pass their `loggedCrp`
  field (P-9).
- **Unchanged:** password generation and storage (FN-009; `BaseAction.java:696-697` stores it for non-CGIAR accounts).
- **Copy (UI-003 as amended):** `global.properties`, `custom/aicrra`, `custom/aiccra3`, `custom/test`. `bigdata`,
  `alliance`, `eib`, `gender`, `pabra` fall back to the global text (Judgment Day C-4, user decision).
- **Rejected:** keeping one key and blanking `{3}` — the label "Password:" would remain.

### DD-8 — `CHG-OTP-LOGIN-001` is amended, not forked
- **Decision:** its FN-001, FN-003, FN-005, FN-006, FN-015, SEC-009, DA-002 and UI-006 get an amendment note pointing
  here: under a password-off unit, step 4 admits the override instead of answering `domainNotEnabled`; the code switch
  and exclusion apply only to Cognito accounts; a Cognito account is not admitted by the override (the code switch and the exclusion still apply to it as written); a single method is
  shown directly. *(Reworded 2026-10-18, validation WARN-2: it read "a Cognito account is refused under the
  override", which was misread as an unconditional refusal.)* Its stale Executive Summary ("enabled by listing domains") is corrected in the same task.

---

## 16. Open Risks

| Risk | Handling |
|---|---|
| Mail delivery fails while the switch is off | OPS-003 prerequisites; turn the switch back on (next login) |
| Five custom files get the unstyled global text | Accepted (DD-7); copy can be added later per unit |
| Session switch reaches a password-off unit | Accepted (SEC-007) |
| `login.js` has no automated test | Browser walkthrough of every §5 row, reviewed at the HITL pause |
| A stored password still works on `/api/**` HTTP Basic | Accepted (SEC-006, Judgment Day C-1). N/A since 2026-10-18: `/api/**` Basic is not used in MARLO today |

---

## 17. Premise Ledger

**Count:** 18 verified · 0 `UNVERIFIED` (P-2 corrected and P-13–P-18 added after Judgment Day round 1).
**Blast-radius triggers:** `live-path` (the design names the step-3 user action and the two password endpoints),
`shared-state` (`OtpEligibility` is read by three production consumers), `consumer` (the `crpByEmail.do` response shape and the
`OtpEligibility` constructor change).

| # | Claim | Class | Citation (as run) | Verified at | If false | Settled by |
|---|---|---|---|---|---|---|
| P-1 | Step 3 picks COGNITO when `isCgiarUser && cognitoEnabled`, else LOCAL, from card attributes set by `loadAvailableItems` | live-path | `login.js:567`; attributes `:761-764`, read `:96-97`, `:523-524`; `isCgiarUser` `:834` | `160ec7dc4a` | §5 view table changes (High) | — |
| P-2 | The two password endpoints are the only login-page callers of `userManager.login`; outside the login page, CLARISA and `/api/**` Basic (P-13) authenticate with a password | live-path | `grep -rn "userManager.login(\|new UsernamePasswordToken" marlo-web/src/main/java marlo-data/src/main/java` → 8 hits: `ClarisaPublicAccesFilter:79,81`, `LoginAction:313(c),325(c),348`, `ValidateUserAction:100(c),118`, `UserManagerImp:153` | `160ec7dc4a` | DD-5 misses an entry point (High) | — |
| P-3 | The shared specificity reader returns `""` for a missing unit, type row or value | data-env | `OtpChallengeManagerImpl.java:119-120`; javadoc `OtpChallengeManager.java:103-105` | `160ec7dc4a` | DD-2 unnecessary (Low) | — |
| P-4 | SPECIFICITY_SQL takes the key list as a bound parameter and is uncached | location | `OtpChallengeMySQLDAO.java:78-83`, `:136-140` | `160ec7dc4a` | DD-3 needs a DAO change (Low) | — |
| P-5 | Request-code step 4 runs before the account lookup; the lookup is inside `isEligible` | live-path | `OtpRequestCodeAction.java:377-379`, `:388-389`; `OtpEligibility.java:132` | `160ec7dc4a` | DD-4 placement (High) | — |
| P-6 | An ineligible account still gets a neutral `sent` and a decoy row | shared-state | `OtpRequestCodeAction.java:394-409`, `:423-428` | `160ec7dc4a` | SEC-004 needs new work (High) | — |
| P-7 | The verify FN-015 re-check calls `OtpEligibility.isEligible` | shared-state | `OtpVerifyCodeAction.java:278-286`, `:358-363` | `160ec7dc4a` | the verify task must change its logic (Low) | — |
| P-8 | `OtpEligibility` consumers: request (`:312-330`), verify (`:280`), `crpByEmail.do` (`:157`); `OtpKeys` is injected | shared-state | `grep -rn "eligibility\.\|otpEligibility\." marlo-web/src/main/java` (scout); `OtpEligibility.java:86,93` | `160ec7dc4a` | missed consumer (High) | — |
| P-9 | The 7 senders build `email.newUser.part1` with the password as `{3}`; passwords are generated and stored outside the text call | location | `CrpUsersAction:800-801,816,1034-1035`; `CrpProgamRegionsAction:357,391-392`; `CrpPpaPartnersAction:330,366-367`; `CrpSiteIntegrationAction:233,268-269`; `CrpAdminManagmentAction:250,284-285`; `ProjectPartnerAction:774,811-813`; `ClusterActivitiesAction:296,330-331`; storage `BaseAction:696-697` | `160ec7dc4a` | DD-7 call sites (High) | — |
| P-10 | `SendEmails` has no caller | existence | `grep -rn "SendEmails" marlo-web/src/main/java \| grep -v utils/SendEmails.java` → 0 | `160ec7dc4a` | an 8th sender (Low) | — |
| P-11 | Nine properties files override `email.newUser.part1` | data-env | `grep -ln "^email.newUser.part1" marlo-web/src/main/resources/custom/*.properties marlo-web/src/main/resources/global.properties` → 9 | `160ec7dc4a` | DD-7 copy scope (Low) | — |
| P-12 | `login.js` is referenced by `login.ftl:4` and `401.ftl:4`, both `?20261015` | consumer | `grep -rn "login/login.js\|login\.js" marlo-web/src/main/webapp marlo-web/src/main/resources \| grep -v "\.js:"` | `160ec7dc4a` | cache-buster misses a page (Low) | — |
| P-13 | `/api/**` authenticates with HTTP Basic through `APCustomRealm` (DB or LDAP) | live-path | `MarloShiroConfiguration.java:140`; `APCustomRealm.java:117-119,179-187` (both judges) | `160ec7dc4a` | SEC-006 scope (High) | — |
| P-14 | `crpByEmail.do` resolves a username as well as an email | live-path | `CrpByUserEmailAction.java:101-104`; `GlobalUnitMySQLDAO.java:45` | `160ec7dc4a` | FN-010 unnecessary (High) | — |
| P-15 | `validateUser.do` leaves an authenticated Shiro subject on success | other | `UserManagerImp.java:151-157` | `160ec7dc4a` | DD-5 severity of a bypass (High) | — |
| P-16 | Four T21 tests pin "no Cognito read" and the `OtpEligibility` constructor | consumer | `OtpEligibilityTest.java:424-433,436-442`; `OtpRequestCodeActionTest.java:930-951,968-975` | `160ec7dc4a` | DD-4 test plan (Low) | — |
| P-17 | `otpEnabledFor` returns before the query when the key is missing or there is no domain, and a test pins it | location | `OtpEligibility.java:159-161`; `CrpByUserEmailActionTest.java:314` | `160ec7dc4a` | DD-6 removes the early exit and replaces the lock (Low) | — |
| P-18 | `crpByEmail.do` computes `otpEnabledByUnit` at `:120`, before the per-unit Cognito loop | location | `CrpByUserEmailAction.java:118-145` | `160ec7dc4a` | DD-6 reorder (Low) | — |

**Consumers of the changed contracts** (copied into the owning tasks' `Consumers` field):
- `crpByEmail.do` response: `login.js` `loadAvailableItems`; `CrpByUserEmailActionTest` (14 tests).
- `OtpEligibility` constructor and methods: `OtpEligibilityTest` (32), `OtpRequestCodeActionTest` (49),
  `OtpVerifyCodeActionTest` (48), `CrpByUserEmailActionTest` (14), `OtpStrutsPackageTest` (8).
- Password endpoints and `LoginAction`'s constructor: `ValidateUserActionGuardTest` (7), `LoginActionCgiarGuardTest` (6),
  `LoginActionFinishLoginTest` (17), `LoginActionLogoutTest` (3), `CognitoLogHygieneTest` (builds `ValidateUserAction`
  at `:861`, `:905`), `CognitoCallbackActionTest`, `CognitoLoginActionTest`, `OtpVerifyCodeActionTest`.
- Static locks and scan: P-16.
- Welcome email: the 7 senders' test classes, found by `grep -rln "CrpUsersAction\|CrpProgamRegionsAction\|CrpPpaPartnersAction\|CrpSiteIntegrationAction\|CrpAdminManagmentAction\|ProjectPartnerAction\|ClusterActivitiesAction" marlo-web/src/test/java` in the owning task.

---

## 18. Budget (tripwire for `/akili-execute`)

| Measure | Expected |
|---|---|
| Tasks | 8 |
| LOC (production + tests + copy + migration + docs) | ~1,300 (production ~420, tests ~700, copy/migration/docs ~180) |
| Review rounds | 10 (one per task, two for the rule-and-eligibility task and the password-gate task) |

Grown from 7 / ~1,100 / 9 by Judgment Day round 1: the FN-010 username view, the membership-scoped gate, the
`crpByEmail.do` reorder, the replaced T21 locks and the runbook.
