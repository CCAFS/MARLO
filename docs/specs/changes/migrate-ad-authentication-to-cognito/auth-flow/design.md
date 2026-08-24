# CGIAR Authentication via Amazon Cognito — Design

**Spec ID:** `CHG-COGNITO-AUTH-001`
**Status:** Draft — **revision 2** (post Judgment Day round 1)
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Last Updated:** 2026-08-24
**Depth:** Full
**Requirements:** [`requirements.md`](./requirements.md)
**Review ledger:** [`judgment.md`](./judgment.md) — round 1: 6 confirmed SEVERE + 1 severity-contested + 2 parent-verified. All corrected here; **no re-judgment was run** (user chose *Fix only*)
**Parent spec:** [`../family.md`](../family.md) — child 1 of 2
**Related TRD sections:** §4.1–4.3, §8.1–8.4, ADR-1, ADR-2, ADR-3, **ADR-6 (superseded in part — DD-8)**
**Related UX/UI Design:** `docs/ux-ui/design.md` §7 (tokens), §10 (accessibility), §11 (light theme). *The login screen has **no entry** in the §4 Screen Inventory — see R-D8*
**Related infrastructure:** `docs/infrastructure.md` §2, §4

---

## Executive Summary

The design adds one new Shiro token type, two Struts actions, and a per-Global-Unit flag on an existing JSON endpoint. It changes no existing authentication behavior that is not explicitly gated.

| Seam | Already exists | What we do |
|---|---|---|
| `APCustomRealm.doGetAuthenticationInfo` | Casts `AuthenticationToken` → `UsernamePasswordToken` unconditionally at its head | Insert an `instanceof` guard **above** the cast. The `UsernamePasswordToken` path below it is preserved byte-for-byte |
| `crpByEmail.do` (`CrpByUserEmailAction`) | Returns `crps[]` (one map per unit) + `user{name, agree}` | Add `cognitoEnabled` to **each `crps[]` entry** |
| `APConfig` (`marlo-utils`) | 63 `@Value` fields, ~60 typed getters | Cognito getters, **with defaults** |

**Architecture tier: LITE.** No new deployable, datastore, queue, or topology change.

> ### What round 1 of Judgment Day changed
>
> Six confirmed-severe findings killed mechanisms this document previously asserted. The corrections are structural, not cosmetic:
>
> | Was | Is now |
> |---|---|
> | `authMode` as one scalar from `crpByEmail.do` | `cognitoEnabled` **per Global Unit**; mode composed client-side at selection (§4, §5) |
> | Password input absent via "FreeMarker conditionals" | FreeMarker cannot branch on data that arrives by XHR after render. Mechanism is now explicit DOM removal (§5, DD-2) |
> | `LoginAction.login()` "reused verbatim" | It dereferences an instance field the callback cannot populate. Extraction is now specified with a destination and a signature (§2, DD-6) |
> | Deep link via `Referer` | Unavailable cross-origin, and NPEs when absent. Return URL now travels in server-side state (§13, DD-4) |
> | Session rotation asserted as satisfied | No mechanism existed and Shiro provides none. Ordering now specified (§13, DD-9) |
> | `CognitoAuthenticator implements Authenticator` | The interface is `(String, String) → Map`; it cannot carry an assertion. **The `Authenticator` seam is no longer used** (DD-1) |
>
> Two further findings came from a single judge and were verified directly against the code: a **missing `is_cgiar_user` gate** that would have let an IdP token log in as a *local* account (§13), and **`validateUser.do`**, an unauthenticated endpoint that still relays CGIAR passwords to AD and was absent from this document entirely (§4, §5).

---

## 1. Architecture Summary

The local flow is **two requests**, not one — the previous revision of this diagram omitted `validateUser.do` and was wrong about the flow it promised to preserve.

```
                 BROWSER                          MARLO (Tomcat)                    AWS / CGIAR
                    │
 step 1  email ─────┼──POST crpByEmail.do──────►  CrpByUserEmailAction
                    │◄─{crps[{…,cognitoEnabled}],  (flag resolved per Global Unit)
                    │   user{name, agree}}────────
                    │
 step 2  project ───┤   client-side selection; mode composed here:
                    │   mode = user.isCgiarUser && card.cognitoEnabled ? COGNITO : LOCAL
                    │   (when crps.length == 1 the step is auto-skipped and the
                    │    single card is the selection — same composition)
                    │
 step 3 ────────────┤
   mode = LOCAL     ├──POST validateUser.do────►  ValidateUserAction
   (unchanged)      │                             ├─ REFUSES is_cgiar_user=1 + flag on  ◄── NEW GUARD
                    │◄─{loginSuccess, userName}── ├─ userManager.login() → realm → DBAuthenticator
                    │                             └─ setAgreeTerms()
                    ├──POST login.do───────────►  LoginAction.login()
                    │                             └─ … → APCustomRealm (UsernamePasswordToken path)
                    │                                                        ── MD5 ──► users.password
                    │
   mode = COGNITO   ├──GET  cognitoLogin.do────►  CognitoLoginAction
                    │                             ├─ re-check is_cgiar_user + flag server-side
                    │                             ├─ record terms acceptance
                    │                             ├─ mint state/nonce/PKCE + bind
                    │                             │  {globalUnitId, returnUrl} to session
                    │◄─302 to /oauth2/authorize── └─ build authorize URL
                    │
                    ├──────────────────────────────────────────────────────►  Cognito User Pool
                    │                                                          └─ SAML/OIDC ──► CGIAR IdP
                    │◄─────────────── 302 back with ?code&state ──────────────
                    │
                    ├──GET cognitoCallback.do──►  CognitoCallbackAction extends LoginAction
                    │                             │  ① consume state → read+DELETE nonce,
                    │                             │     verifier, globalUnitId, returnUrl
                    │                             │  ② exchange code ─────────────► /oauth2/token
                    │                             │  ③ validate ID token
                    │                             │     (JWKS, iss, aud, exp, nonce, token_use)
                    │                             │  ④ map claim → users row
                    │                             │  ⑤ GATE: row exists ∧ is_cgiar_user=1
                    │                             │          ∧ is_active ∧ flag still on
                    │                             │  ⑥ rotate session (stop → new)
                    │                             │  ⑦ Subject.login(CognitoAuthenticationToken)
                    │                             │     └─ realm: instanceof → verify assertion
                    │                             └─ ⑧ finishLogin(user, crp, returnUrl)
                    │◄─302 dashboard / deep link──    └─ existCrpUser, session attrs, saveLastLogin
```

**The shared tail is step ⑧.** `finishLogin(User, GlobalUnit, String)` is the extracted body of today's `LoginAction.login(User, GlobalUnit)`; both paths call it, so session attributes, custom parameters, the `crp_users` membership gate, `saveLastLogin`, and post-login routing cannot drift.

---

## 2. Module Footprint

### marlo-parent

| File | Change |
|---|---|
| `pom.xml` | **Modify** — add AWS SDK v2 `cognitoidentityprovider` and a JWT/JOSE library to `dependencyManagement`, with version properties. **No downgrades** (NF-006, hard rule 11) |

### marlo-utils

| File | Change |
|---|---|
| `.../utils/APConfig.java` | **Modify** — Cognito getters. Every new `@Value` uses the `${key:default}` form (§9.3) |

### marlo-data

| File | Change |
|---|---|
| `.../security/CognitoAuthenticationToken.java` | **New** — Shiro `AuthenticationToken` carrying a validated `CognitoAssertion` |
| `.../security/CognitoAssertion.java` | **New** — immutable validated-identity value object |
| `.../security/CognitoTokenValidator.java` (+ `impl/`) | **New** — JWKS retrieval, caching, full token validation. Placed in `security/`, **not** `data/manager/` (that package holds Hibernate service beans) |
| `.../security/APCustomRealm.java` | **Modify** — `instanceof` guard **above** the existing cast (§2.1) |
| `.../MarloShiroConfiguration.java` | **Modify** — inject the validator where the realm is hand-constructed (`:44-49`) |
| `.../security/authentication/Authenticator.java` | **UNCHANGED** — see DD-1 |
| `.../security/authentication/{DB,LDAP}Authenticator.java` | **UNCHANGED** |

### marlo-web

| File | Change |
|---|---|
| `.../action/home/CognitoLoginAction.java` | **New** — guards, terms, state minting, authorize redirect |
| `.../action/home/CognitoCallbackAction.java` | **New** — `extends LoginAction` (DD-6) |
| `.../action/home/LoginAction.java` | **Modify** — extract `finishLogin(User, GlobalUnit, String returnUrl)`; `login()` calls it with the `Referer` value. **One deliberate behavior change: a null guard** (§2.2) |
| `.../action/json/global/CrpByUserEmailAction.java` | **Modify** — add `cognitoEnabled` per `crps[]` entry; move the `user` map **out of the CRP loop**; inject `CustomParameterManager` (constructor change) |
| `.../action/json/global/ValidateUserAction.java` | **Modify** — refuse `is_cgiar_user = 1` accounts whose selected Global Unit has the flag on (§5.3) |
| `.../config/APConstants.java` | **Modify** — specificity constant |
| `resources/struts-home.xml` | **Modify** — register both actions with `unloggedStack` (§8) |
| `resources/global.properties` | **Modify** — new keys **plus `login.error.invalidUserCrp`**, which is missing today (§9.4) |
| `resources/config/marlo-test.properties` | **Modify** — Cognito keys in the tracked bootstrap template (hard rule 12) |
| `webapp/WEB-INF/global/pages/loginForm.ftl` | **Modify** — add the `#login-step-cgiar` block |
| `webapp/global/js/login/login.js` | **Modify** — compose the mode; remove the password node on the COGNITO branch |
| `webapp/global/css/customLogin.css` | **Modify** — **not `global.css`**; `.login-form-button` is defined here (`:338`, `:353`, `:358`) |
| `resources/database/migrations/V*__AddCognitoAuthSpecificity.sql` | **New** |

### marlo-data (second constants file)

| File | Change |
|---|---|
| `.../config/APConstants.java` | **Modify** — same constant, identical value |

### 2.1 The realm edit, precisely

`APCustomRealm.java:113-115` today:

```
UsernamePasswordToken userPassToken = (UsernamePasswordToken) token;   ← unconditional cast
```

This prologue executes on **every** local login. The edit inserts an `instanceof` guard **above** it that returns early for `CognitoAuthenticationToken`; everything from the cast down is untouched.

> **This corrects a false comfort in revision 1.** Saying "the `else` (local) branch is not edited" was true and misleading — the shared prologue *is* edited. `requirements.md` D-2's gate must therefore be restated: **"the `UsernamePasswordToken` path through `doGetAuthenticationInfo` produces identical behavior"**, not "the local branch is untouched". A gate phrased the old way passes green over a rewritten prologue.

**No flag logic enters the realm.** The realm has no Global Unit and no session (§9.1). Revision 1's "routes to Cognito when the flag is on" was unimplementable and is deleted.

### 2.2 The one deliberate change to local behavior

`LoginAction.java:290-295` reads `Referer` and calls `.contains(".do")` with **no null guard** — today a request without a `Referer` throws NPE and yields a 500. The extraction adds the guard.

This is strictly a bug fix, but it **is** a behavior change on the local path and is declared here rather than slipped in. Reviewer: expect it; anything else on that path is a FAIL.

---

## 3. Data Model Changes

**No schema change.** `is_cgiar_user`, `username`, `email`, `password`, `last_login`, `agree_terms` are used as today.

### Migration — the specificity only

`V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__AddCognitoAuthSpecificity.sql`, per the `AGENTS.md` template: three `parameters` rows for `global_unit_type_id ∈ (1, 3, 4)`, `category = '2'`, `format = '1'`, `default_value = 'false'`. `custom_parameters` rows are **not** seeded — rollout is operational, not a deploy artifact.

### Global Unit types 2 and 5 — stated, not silently inherited

Verified against the repository: **40 existing specificity migrations use exactly `(1, 3, 4)`**; one uses `5`; **type 2 is used by none.** The `(1,3,4)` choice therefore follows established practice rather than overlooking anything.

The consequence must still be explicit: **a Global Unit of type 2 (Center) or type 5 can never have this flag**, so its CGIAR users stay on LDAP permanently — and `§14` phase 4 is unreachable for them. `LoginAction:299-318` routes types 1–5, so such units exist in the routing surface.

Whether type-2/5 Global Units have CGIAR users is **OQ-11**. If they do, this migration gains rows for them and the deviation from the three-row template is recorded in the Decision Log per `CLAUDE.md`'s deviation rule.

---

## 4. API / Action Surface

Two new Struts `.do` actions in the existing `home` package. No `/api/*` endpoint, no new `*.json` path — hard rule 3 honored.

> **Precision:** `struts.action.extension` is `do,,json` (`struts.xml:21`), so every Struts action is *already* reachable with a `.json` suffix. "No new `*.json` path" is true as **intent and pattern**, not as a URL-space guarantee.

| Action | Method | Purpose |
|---|---|---|
| `cognitoLogin.do` | GET | Re-verify eligibility, record terms, mint state, redirect to `/oauth2/authorize` |
| `cognitoCallback.do` | GET | Validate, gate, rotate session, establish login |

On failure both return `input` → `login.ftl`. **They must expose the same model surface `login` does** — `struts-home.xml:29-33` binds `model=action`, and `loginForm.ftl` reads `crpSession` and `listGlobalUnitTypes`; without them the failure page renders broken.

### `crpByEmail.do` — the corrected contract

The flag is **per Global Unit**, because the specificity is. Revision 1 returned one scalar `authMode` from an endpoint that runs *before* the unit is chosen — and whose response is what decides whether the selection step is even shown.

| Field | Today | After |
|---|---|---|
| `crps[].{id,name,acronym,type,idType}` | unchanged | unchanged |
| `crps[].cognitoEnabled` | — | **new** — boolean, per unit |
| `user.{name,agree}` | unchanged | unchanged |
| `user.isCgiarUser` | — | **new** — boolean |

The client composes `mode = user.isCgiarUser && card.cognitoEnabled`. This is the only formulation that satisfies MIG-001's "both paths coexist" scenario for a user belonging to units with different flag states.

**Two structural issues in the existing action must be fixed as part of this:**

1. The `user` map is built **inside** the `for (GlobalUnit crp : crps)` loop (`:89`), so a user with zero Global Units returns `user == null`. Move it out.
2. The constructor takes `(APConfig, GlobalUnitManager, UserManager)`; resolving the flag needs `CustomParameterManager` injected.

**Unknown email and zero-units both fall through to the existing `emailNotFound` slot** with no `isCgiarUser` and no `crps[]` — FN-001's third scenario forbids disclosing which path an unknown email *would* have used.

> **Enumeration caveat (R-D3).** For an email that **exists**, the response now discloses its authentication type. `crpByEmail.do` sits in `homeJson` with no `requireUser` and already discloses existence and Global Unit membership to an unauthenticated caller, so this adds a *type* oracle to an existing *existence* oracle. Accepted, recorded, not treated as free.

---

## 5. Frontend Composition

### 5.1 Why FreeMarker cannot do this

`loginForm.ftl` is included at page render (`login.ftl:17`, and also from `401.ftl:19`). At that moment the server has no email, no user row, and no Global Unit — all three arrive later via the `crpByEmail.do` XHR. **No FreeMarker conditional can branch on `authMode`.** Revision 1 claimed it could and built DD-2's rationale on it.

### 5.2 The actual mechanism

`loginForm.ftl` emits **both** step-3 blocks; `login.js` selects one at runtime:

| Block | Shown when | Content |
|---|---|---|
| `#login-step-password` | `mode === "LOCAL"` | Exactly today's step 3, unrestructured |
| `#login-step-cgiar` | `mode === "COGNITO"` | Selected-project card + echoed identity (both reused) + terms checkbox + a single "Sign in with CGIAR" control |

FN-001's negative clause — *the password input must not be rendered, focusable, or present in the submitted form* — is satisfied by **two independent measures**, because the requirement is security-relevant and one mechanism is one bug away from failing:

1. **DOM removal.** On the COGNITO branch `login.js` calls `.remove()` on `#login-password`. Not `hide()`, not `disabled` — a hidden input still posts, and the `required` attribute (`loginForm.ftl:94`) would additionally block HTML5 submission.
2. **Out of the submit path.** The CGIAR control is an `<a>`/`<button type="button">` navigating to `cognitoLogin.do`. It never submits `<form action="login">`, so even if measure 1 regressed, the password field would not travel with a Cognito sign-in.

**D-2's gate asserts node absence at submit time** — the check must be able to fail, so it asserts on the DOM, not on the code path.

### 5.3 The local flow is two requests, and one of them needed hardening

`login.js:542` posts email **and password** to `validateUser.do` before submitting `login.do`. That endpoint (`ValidateUserAction.java:71`) calls `userManager.login()` → realm → the LDAP branch for CGIAR users, and sits in `homeJson` with **no `requireUser`**.

Left alone, it keeps accepting and relaying CGIAR passwords to Active Directory for flag-enabled units — **SEC-005 would be violated by an endpoint this document did not mention.**

**Guard:** `ValidateUserAction` must refuse to authenticate an `is_cgiar_user = 1` account whose selected Global Unit has the flag enabled, returning the **same generic failure shape** as any other rejection (no new oracle).

### 5.4 Terms of service

`ValidateUserAction.java:87` (`user.setAgreeTerms(agree)`) is the **only** writer of `users.agree_terms`, and it is reached only from the password branch. A COGNITO user bypasses it entirely, so acceptance would silently stop being recorded.

**Decision:** the terms checkbox is rendered in `#login-step-cgiar` and its acceptance is recorded by `CognitoLoginAction` **before** the redirect. Dropping it would be a compliance regression outside this spec's declared blast radius.

### 5.5 The control

Reuses `.login-form-button` from **`customLogin.css`** (`:338`, `:353`, `:358`) — note it is styled only as a descendant of `.login-button-container`, so the control sits inside that container. No new color, type scale, or spacing value (`docs/ux-ui/design.md` §7). Light theme only (§11). A real `<button>`/`<a>`, so it is keyboard-reachable with a role for free (§10).

The redirect is always user-initiated. Nothing navigates on its own.

> **Pre-existing accessibility gap, now shared:** the `.invalidField` slots (`loginForm.ftl:100-107`) carry no `role="alert"`, which `docs/ux-ui/design.md` §10 rule 5 requires. FN-005's messages land in those same slots. Named here so D-5's accepted gap does not quietly absorb a new one.

---

## 6. Persistence & Phase Replication Plan

**Not applicable.** Authentication writes no phased data. The writes are `users.username`, `users.last_login`, and `users.agree_terms` — phase-independent columns on a non-phased table. No `phase.getNext()` recursion, no past-phase concern.

---

## 7. Validation & Save Pipeline

**Not applicable in the constitutional sense** — no `Action.validate()` → `Validator` → manager-save chain. `LoginAction.validate()` is unchanged. The new actions validate an authentication round trip, not a form, and surface failures through the existing `login.ftl` error slots.

---

## 8. Permissions & Edit Gates

Both new actions declare **`unloggedStack`** (`struts.xml:198-203`) — `i18nFile` + `validCrp` + `validSessionCrp` + `defaultStack`, no `requireUser`. They must be reachable without a session, and TRD §4.3 rule 1 requires every new `.do` action to name a stack. Revision 1 said "package default", which left `requirements.md` §7's checklist item unclosed and departed from the TRD without a Decision Log entry.

No `canEdit*` gates: nothing is edited. `doGetAuthorizationInfo()` is **not modified**, so roles, permissions, and `crp_users` scoping behave identically regardless of which path authenticated.

**Membership is still enforced** inside the shared tail (`existCrpUser`, `LoginAction.java:236`). A valid IdP authentication does not grant access to a Global Unit the user does not belong to.

**Logout (FN-007) is satisfied by changing nothing.** `LoginAction.logout()` (`:326-343`), including the `clearCachedAuthorizationInfo` hack, is untouched, and **no RP-initiated logout is issued** to Cognito — so MARLO logout does not end the user's CGIAR SSO session elsewhere (OQ-8).

---

## 9. Specificity / Feature-Flag Strategy

### 9.1 Why `hasSpecificities()` is unusable here

`BaseAction.hasSpecificities()` (`BaseAction.java:6574`) reads the Shiro session. Custom parameters are written there at `LoginAction.java:242-249` — *after* successful authentication. At authentication time the session is empty, so the helper always returns `false`.

**This is also why no flag logic can live in the realm** (§2.1): the realm has neither a Global Unit nor session data.

### 9.2 Where the flag is resolved — three times, on purpose

| Point | Purpose | Trust |
|---|---|---|
| `crpByEmail.do` | Per Global Unit, so the wizard can render the right step 3 | **Rendering hint only** |
| `CognitoLoginAction` | Re-check before minting state | **Authoritative** |
| `CognitoCallbackAction` | Re-check before establishing the session | **Authoritative** |

A crafted `cognitoLogin.do` for a disabled Global Unit is rejected. The client's answer is never an authorization decision.

Resolution uses `CustomParameterManager.getAllCustomParametersByGlobalUnitId()` — the same manager `LoginAction` already uses, called earlier.

### 9.3 Configuration must not break startup

`APConfig` carries 63 `@Value` fields and **none** use `${key:default}`; `PropertySourcesPlaceholderConfigurer` runs with `ignoreUnresolvablePlaceholders = false`. Adding bare Cognito placeholders would make **the application fail to boot** on every environment whose gitignored `marlo-${profile}.properties` lacks the keys — including environments that never enable Cognito. Revision 1 called phase 0 "inert"; it would not have been.

Therefore: every new `@Value` uses an empty default (`${cognito.client.id:}`), `CognitoLoginAction` fails closed with a configuration error if a required value is blank, and the keys are added to the **tracked** `marlo-test.properties` bootstrap template.

### 9.4 i18n

New user-facing strings are keys in `global.properties` (hard rule 8; DD-7).

**`login.error.invalidUserCrp` — which FN-002 calls "the existing message" — is not in `global.properties`.** It exists only in `custom/ciat.properties:88`, so every non-CIAT Global Unit renders the raw key on that failure path today. Adding it to `global.properties` is part of this change.

---

## 10. Integration Points

| System | Direction |
|---|---|
| Cognito User Pool | Outbound HTTPS (`/oauth2/token`, JWKS) + browser redirect (`/oauth2/authorize`) |
| CGIAR IdP (ADFS / Entra ID) | Reached **only** through Cognito — **OQ-3** |
| CGIAR Active Directory (LDAP) | Unchanged, still live for non-migrated units. Retired by child spec 2 |

JWKS is fetched once and cached with a bounded TTL, re-fetched on an unknown `kid`. Fetching per login would make every sign-in depend on a second network call.

---

## 11. Observability

> **Correction:** revision 1 described new logging as parity with an existing baseline. The baseline does not exist — the membership-failure branch (`LoginAction.java:263-271`) emits **no log line at all**, and the success line (`:281`) logs only the email, not the Global Unit.

| Event | Logged | New? |
|---|---|---|
| Attempt started | email as submitted, Global Unit acronym, resolved mode | **New** |
| Token validation failed | which check failed (signature / `iss` / `aud` / `exp` / `nonce` / `token_use`) — **never the token** | New |
| State or nonce mismatch | the fact only | New |
| Gate rejection | which gate (`is_cgiar_user`, `is_active`, flag, membership) + user id + Global Unit | **New — and the membership line must be added to the local path too**, since it does not exist today |
| Success | email **+ Global Unit** | **Extends** `:281`, which logs only the email |

**Never logged:** ID/access/refresh tokens, authorization codes, `state`, `nonce`, PKCE verifier, any password. Enforced by test (OPS-001 / defect class D-6), not by convention.

Existing SLF4J/Logback setup (TRD §9.1). No new appender.

---

## 12. Performance & Scalability

| Scenario (six-part) | Response measure |
|---|---|
| A CGIAR user → activates sign-in → normal operation, Cognito and IdP healthy → login actions + token exchange ⇒ dashboard reached | **p95 ≤ 5 s**, excluding IdP-side typing (NF-001) |
| A CGIAR user → returns with a valid code → JWKS cached → callback ⇒ validates locally | **exactly 1 outbound MARLO→AWS call per login** |
| An operator → disables the flag for a unit → live production → login flow ⇒ next attempt uses LDAP | **effective next attempt, zero deploys** (MIG-001) |

**Tactic — control resource demand.** Today's CGIAR login makes **two** LDAP round trips (`getCgiarNickname` + `authenticateUser`, two distinct `LDAPService` instantiations). The new flow makes one server-side call. Cached JWKS is *maintain multiple copies* applied to signing keys.

Scalability is not architecturally significant: login volume is bounded by the user base, sessions remain server-side, no new shared state.

---

## 13. Security Considerations

### 13.1 The gate list — corrected

A valid IdP token proves who the person is **at CGIAR**. It does not grant MARLO access. **Four** local gates apply, in order:

1. A `users` row exists for the mapped claim — **no auto-provisioning**.
2. **`users.is_cgiar_user = 1`.**
3. `users.is_active` is true.
4. `crp_users` membership holds for the session-bound Global Unit.

> **Gate 2 was missing from revision 1, and its absence was an authentication bypass.** Many MARLO *local* accounts belong to CGIAR staff and carry an `@cgiar.org` address with `is_cgiar_user = 0` — the population `requirements.md` OQ-1 exists to count. Without gate 2, anyone able to authenticate at the CGIAR IdP for such an address would obtain a MARLO session for a **local** account, without its MD5 password — bypassing the very path this spec declares out of scope and untouched.
>
> The flag is **also** re-checked here, not only in `CognitoLoginAction`: that action runs before the redirect and does not yet know who the user is, so it can verify the Global Unit's flag but not `is_cgiar_user`. Both halves of §9's formula must be verified where identity is known.

**Authentication is federated; authorization never leaves MARLO.**

### 13.2 Token validation — mechanisms, not assertions

Revision 1 asserted "single-use code" and "replayed nonce rejected" without saying how. A unit test cannot be written against an unstated mechanism, and a nonce left in the session is by definition replayable.

| Check | Mechanism |
|---|---|
| Signature | Against the pool JWKS, cached, re-fetched on unknown `kid` |
| `iss`, `aud`, `exp` | Against configured pool and client; `exp` with a **small, explicitly bounded** clock-skew leeway (R-D7) |
| `nonce` | Compared to the session value, which is **read and deleted atomically** on first use. Replay then fails on the missing entry, not on comparison |
| **`token_use == "id"`** | Cognito signs ID **and** access tokens with the same JWKS. Without this check an access token passes signature validation |
| `state` | Read and deleted atomically with the nonce; mismatch or absence rejects |
| Code single-use | **Enforced by Cognito's token endpoint**, not by MARLO. Stated explicitly because it places that assertion outside MARLO's test surface |

PKCE is used. The callback URL is validated against an **exact-match** allowlist; no wildcards.

### 13.3 Session-ID rotation (SEC-003) — ordering is the design

Shiro 1.x does **not** rotate the session id on `Subject.login()`, and `MarloShiroConfiguration.java:72-89` wires a plain `DefaultWebSessionManager` with no fixation protection. Revision 1 listed rotation as a satisfied measure with no mechanism anywhere.

The ordering is security-relevant because DD-4 stores `state`, `nonce`, verifier, `globalUnitId`, and `returnUrl` in that same pre-auth session — stopping it too early discards them, too late discards `SESSION_USER`/`SESSION_CRP`/custom parameters:

```
① read + DELETE state, nonce, verifier, globalUnitId, returnUrl from the pre-auth session
② exchange code, validate token          (needs nonce from ①)
③ map claim → users row; apply the four gates of §13.1
④ capture carry-forward values into locals
⑤ subject.getSession().stop()            ← pre-auth session destroyed
⑥ subject.login(CognitoAuthenticationToken)  ← new session created
⑦ finishLogin(user, crp, returnUrl)      ← populates the NEW session
```

**The local path is not rotated by this spec.** It has the same pre-existing exposure it has today; changing it is a behavior change to the path this spec promises not to touch. Recorded as R-D9 rather than silently left out.

### 13.4 Quality-attribute scenarios

| Scenario | Response measure |
|---|---|
| Attacker → forged/altered ID token → callback ⇒ rejected, no session | **100 % rejection across all six invalid classes** (SEC-001) |
| Attacker → replays a consumed `state`/`nonce` → callback ⇒ refused on the missing entry | **no session created** |
| Attacker → cross-site authorization request → callback ⇒ mismatched `state` refused | **no session created** |
| Attacker → fixes a session id pre-login → callback ⇒ rotated at ⑤–⑥ | **pre ≠ post** (SEC-003) |
| Attacker → holds a valid CGIAR credential for a **local** account → callback ⇒ gate 2 refuses | **no session created** |
| CGIAR credential leaks → MARLO ⇒ holds nothing to revoke | **blast radius excludes MARLO** (SEC-005) |

---

## 14. Backwards Compatibility & Rollout

| Phase | State | Rollback |
|---|---|---|
| 0 | Code deployed, `parameters` default `false`, Cognito keys present in every environment's properties file (§9.3). Nobody's behavior changes | n/a — inert **only if 9.3 is honored** |
| 1 | Enabled for one internal test Global Unit | Flag → `false`. **Seconds, no deploy** |
| 2 | Pilot CRP, held one full reporting week | Same |
| 3 | Progressive enablement | Same, per unit |
| 4 | *(child spec 2)* remove `adauth`, close LDAP egress | **One-way door.** Code revert + redeploy |

Phase 4 is not in this spec, and **is unreachable for type-2/5 Global Units** while the specificity cannot exist for them (§3, OQ-11). `OPS-002` keeps LDAP functional throughout.

**Migration-free by construction.** No user record is altered, moved, or pre-provisioned. A user's first Cognito login reads the same row their last LDAP login read — which is why rollback is a flag flip, not a data restore.

---

## 15. Decision Records

**DD-1 — The realm consumes the assertion directly; the `Authenticator` seam is NOT used.**
*Problem:* add a third authentication mechanism without destabilizing two working ones.
*Revision 1 was wrong.* It claimed "the existing seam already supports" this. `Authenticator.java:32` declares exactly `Map<String,Object> authenticate(String email, String password)` — it cannot carry a validated assertion. Every workaround was worse: changing the shared interface would force edits to `DBAuthenticator` (which `family.md` declares untouched), and smuggling a serialized assertion through the `password` parameter is precisely what D-2 exists to catch.
*Decision:* keep one realm; dispatch on token type; the realm verifies the `CognitoAssertion` carried by the token. **`Authenticator.java`, `DBAuthenticator`, and `LDAPAuthenticator` are not modified at all.**
*Rejected:* a `default` method on `Authenticator` (widens a shared interface for one caller); a second Shiro `Realm` (changes how *every* login resolves, including local ones).

**DD-2 — The password input is removed from the DOM, and the control sits outside the form.**
*Problem:* FN-001 forbids the password input being rendered, focusable, or present in the submitted form.
*Revision 1 was wrong.* It claimed FreeMarker conditionals made the input structurally absent. `loginForm.ftl` renders before the email is known, so no server-side conditional can branch on it (§5.1).
*Decision:* two independent measures — runtime `.remove()` plus a control that never submits the form (§5.2).
*Rejected:* CSS hiding (a hidden input still posts, and `required` at `:94` would block submission).

**DD-3 — The specificity is resolved from the selected Global Unit, not `hasSpecificities()`.**
*Problem:* the session-backed helper is empty at authentication time (`BaseAction.java:6574`).
*Decision:* query `CustomParameterManager` directly, server-side, at the three points in §9.2.
*Rejected:* **pre-populating the session with the Global Unit's custom parameters before authentication.** The objection is not that a pre-auth session exists — DD-4 deliberately uses one — but that custom parameters carry authorization-relevant configuration, and writing them for an unauthenticated visitor exposes a Global Unit's configuration to anyone who reaches step 2. *(Revision 1 gave "session fixation" as the reason, which condemned DD-4's own mechanism; §13.3 handles fixation directly.)*

**DD-4 — Round-trip state lives in the server-side session, keyed by `state`.**
*Problem:* Global Unit, nonce, verifier, and return URL must survive the redirect and must not be attacker-controllable.
*Decision:* bind all four server-side; the callback reads them from the session, never from the returned URL. Read-and-delete on first use (§13.2).
*Rejected:* encoding any of them in the redirect URI or a client cookie — that lets a caller choose the Global Unit they return with, turning a UX convenience into an access-control input. `crp_users` would still block it, but that check should not be the only thing standing there.
*Concurrency:* two tabs signing in simultaneously overwrite one another. **Last-writer-wins is accepted**; the loser sees a `state` mismatch and retries. A keyed map was rejected as unbounded session growth for a rare case.

**DD-5 — Validate in the callback; keep the realm I/O-free.**
*Problem:* Shiro realms performing network calls are hard to test — today's `LDAPAuthenticator` demonstrates it.
*Decision:* the callback validates and produces a `CognitoAssertion`; the realm verifies an already-validated value object.
*Consequence:* every SEC-001 rejection case is a **pure unit test** — no network, no container, no mock server. With three test classes in the entire repository, testability decided this, not elegance.

**DD-6 — `CognitoCallbackAction extends LoginAction`; the tail becomes `finishLogin(User, GlobalUnit, String)`.**
*Problem:* the callback must produce a session indistinguishable from a local login's.
*Revision 1 was wrong.* It said the method could be "reused verbatim". `LoginAction.login(User, GlobalUnit)` dereferences the **Struts-populated instance field** `user` at `:268`, `:269`, `:274`, `:277`, and `:281` — a field the callback does not populate, giving an NPE on the success path *and* on the `existCrpUser` failure path FN-002 requires.
*Decision:*
- `CognitoCallbackAction extends LoginAction`, so every `BaseAction` member the tail uses (`getSession`, `addFieldError`, `getText`, `isVisibleTopGUList`, `setCrpSession`, `getBaseUrl`) exists.
- Before calling the tail, the callback sets the inherited `user` field to a **detached, non-Hibernate-managed** `User` carrying only the email. The tail's field reads then behave exactly as on the local path, and `user.setPassword(null)` cannot dirty a managed entity.
- The return URL becomes a **parameter**. `LoginAction.login()` passes `request.getHeader("Referer")`; the callback passes the value carried in state.
*Behavior change (declared):* a null guard on the return URL (§2.2).

**DD-7 — New messages use i18n keys; `ADLoginMessages` is left alone.**
The existing enum holds literal English strings, violating hard rule 8. New messages are keys in `global.properties`; existing enum values are untouched. Following the precedent would propagate the violation; fixing the enum is scope this spec did not ask for.

**DD-8 — TRD ADR-6 is superseded in part.**
ADR-6 records *"Apache Shiro for authentication and authorization; CGIAR AD is the primary realm."* Shiro remains the session and authorization layer and **the single-realm architecture is unchanged** — but the CGIAR **credential authority** moves to Cognito federation. Recorded as superseding in part, to be written by `/akili-archive`. ADR-6 is not edited in place.

**DD-9 — Rotate the session on the Cognito path only.**
*Problem:* SEC-003 requires a new session id on successful authentication; Shiro provides none automatically.
*Decision:* explicit `stop()` → `login()` at steps ⑤–⑥ (§13.3), on the Cognito path only.
*Rejected:* rotating the local path too. It is the correct fix for a real pre-existing exposure, but it is a behavior change to the path this spec promises not to touch. Recorded as R-D9 for separate work rather than folded in silently.

---

## 16. Open Risks

| ID | Risk | Source | Mitigation |
|---|---|---|---|
| R-D1 | The `finishLogin` extraction (DD-6) silently changes local-login behavior | Design | Its own task, its own review, done **first**. One declared change only (§2.2); Reviewer FAILs any other delta |
| R-D2 | Identity claim unresolved — `sub`, `oid`, or email? Mapping on mutable email orphans accounts | **OQ-9** | Blocks the mapping task. Design supports any; the choice is CGIAR IT's |
| R-D3 | `crpByEmail.do` becomes an authentication-type oracle for a known email | Design §4 | Accepted. Existence and membership are already disclosed; consider rate limiting as separate work |
| R-D4 | `/api/**` `authcBasic` breaks for CGIAR users — federated identities cannot use Basic auth | **OQ-4** | **No gate exists.** Discovery task before phase 1. TRD §8.4 also drifts (`MarloShiroConfiguration.java:113` says `authcBasic`, TRD says tokens) — fold that correction into the same task |
| R-D5 | No frontend or visual test harness, so the wizard branch has no automated UI gate | D-5 | Manual keyboard/screen-reader walkthrough at the HITL pause + T6 review of a screenshot |
| R-D6 | CGIAR IT may decline to federate | **OQ-3** | Blocks implementation, not specification |
| R-D7 | Clock skew causes spurious `exp` rejections | Design | Small, explicitly bounded leeway — tested, not an open window |
| R-D8 | `docs/ux-ui/design.md` §4 Screen Inventory has **no login-screen entry**, so §7's palette rule has no screen-level baseline | Judgment round 1 | Adding it is a **constitutional edit** (§4 preamble) and is out of scope here. Recorded as an inventory gap |
| R-D9 | The **local** login path still does not rotate its session id | DD-9 | Pre-existing exposure, deliberately not changed here. Separate work |
| R-D10 | Type-2 (Center) and type-5 Global Units can never enable the flag | §3, **OQ-11** | Stated. If they have CGIAR users, the migration gains rows and a Decision Log deviation entry |

---

## 17. Budget

Tripwire for `/akili-execute`. Exceeding it is information, not failure — the Leader stops and escalates.

| Metric | Expected |
|---|---|
| Tasks | **14** |
| Production LOC | **~950** (≈680 Java, ≈130 JS, ≈70 FTL/CSS, ≈70 SQL/properties) |
| **Test LOC** | **~350**, budgeted separately |
| **Total LOC** | **~1,300** |
| Review rounds | **18** (14 + 4 expected reworks on the security-critical tasks) |

> **Revision 1's budget was internally short:** its components summed to 820 of a ~850 total, leaving ~30 lines for a suite that must cover six SEC-001 rejection classes plus D-3, D-6, and D-8 — in a repository with three test classes in total. Under-provisioning the security tests by an order of magnitude would have tripped the tripwire on the highest-value task, producing noise instead of information.

**Depth re-check:** **Full** is confirmed — 14 tasks, an external integration, a staged rollout, and a security-critical validation surface. Well over ~400 LOC, so `tasks.md` carries a multi-PR recommendation.

**Reversion challenge (Step 2.3):** DD-2 removes the password input for CGIAR users — behavior that ships today. *What does removing it break?* A CGIAR user whose Global Unit has the flag **off** must still get the password field; that is why the mode composes `isCgiarUser` **with** the per-unit flag rather than keying on `is_cgiar_user` alone (§4). Judgment round 1 found the deeper form of the same defect: revision 1 could not even *compute* that composition at the point it claimed to. No test covers the current step-3 rendering, which is exactly why the challenge was worth its cost — and why D-2's gate asserts on the DOM.
