# CGIAR Authentication via Amazon Cognito — Requirements

**Spec ID:** `CHG-COGNITO-AUTH-001`
**Status:** Draft
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Reviewers:** Tech lead, QA lead, PMU lead
**Last Updated:** 2026-08-24
**Depth:** Full — authentication, external integration, and a staged migration
**Parent spec:** `docs/specs/changes/migrate-ad-authentication-to-cognito` ([`family.md`](../family.md) child 1 of 2)
**Related PRD sections:** `docs/prd.md` §3 (Personas), §7.2 (Quality and security acceptance)
**Related UX/UI Design sections:** `docs/ux-ui/design.md` §7 (Design Tokens), §10 (Accessibility), §11 (Light theme only). *The login screen has **no entry** in the §4 Screen Inventory — adding one is a constitutional edit and is out of scope here (see `design.md` R-D8)*
**Related TRD sections:** `docs/trd/trd.md` §8.1–8.4 (Security & Authorization), §4.3 (Interceptor stacks), ADR-6
**Related infrastructure:** `docs/infrastructure.md` §2, §4
**Companion ai-context docs:** `reports/ai-context/struts-critical-routing-catalog.md`, `reports/ai-context/interceptor-validator-playbook.md`

---

## Executive Summary

MARLO authenticates users through one of two paths, selected by `users.is_cgiar_user`. This spec replaces **only** the CGIAR path — today a direct LDAP/LDAPS bind against CGIAR Active Directory — with Amazon Cognito federating to CGIAR's identity provider. The local database path is untouched.

Three facts from the codebase shape everything below:

1. **The realm is the single dispatch point.** `APCustomRealm.doGetAuthenticationInfo()` chooses between the two password-based paths. Cognito is added as a Shiro **token type**, dispatched by an `instanceof` guard inserted *above* the existing unconditional cast, so the whole `UsernamePasswordToken` path below it is preserved byte-for-byte. The `Authenticator` interface is **not** reused — its signature is `(String email, String password)` and cannot carry a token-based assertion (`design.md` DD-1).
2. **The frontend seam already exists.** The login page is a three-step wizard — email → project → password — and step 1 already calls `crpByEmail.do`, which resolves the user record. That endpoint can report whether the user is a CGIAR user, letting the wizard branch **before** a password is ever requested.
3. **Authorization does not participate.** Roles, permissions, and Global Unit membership come from local tables. Nothing downstream of authentication knows how identity was proven.

Because the Global Unit is chosen in wizard step 2 — before authentication — it can be carried through the OIDC round trip and restored on return. The user's project selection survives the redirect.

---

## Glossary

| Term | Meaning |
|---|---|
| **CGIAR user** | A MARLO user whose row has `users.is_cgiar_user = 1`. Authenticated against CGIAR Active Directory today |
| **Local user** | A MARLO user whose row has `users.is_cgiar_user = 0`. Authenticated against `users.password` (MD5). **Out of scope** |
| **Global Unit** | A CRP, Platform, or Center. Selected in login wizard step 2; scopes the entire session |
| **IdP** | CGIAR's identity provider (ADFS or Entra ID — see OQ-3), the authority for CGIAR credentials |
| **Cognito** | Amazon Cognito User Pool acting as the OIDC broker in front of the IdP |
| **Authorization Code + PKCE** | The OAuth 2.0 flow used. The browser receives a short-lived code; the server exchanges it for tokens |
| **ID token** | The signed JWT Cognito returns, asserting who the user is |
| **Specificity** | MARLO's per-Global-Unit feature flag (`parameters` + `custom_parameters`) |
| **Wizard** | The three-step login form: email → project → password |

---

## 1. Overview

MARLO must stop being an intermediary for CGIAR credentials. Today a CGIAR user types their AD password into a MARLO form, and MARLO binds to Active Directory on their behalf over LDAP. This spec moves that exchange to CGIAR's own identity provider, brokered by Amazon Cognito, so the password never reaches MARLO.

The change is gated by a per-Global-Unit specificity so it can be enabled progressively and switched off without a deploy.

---

## 2. Problem Statement

| Problem | Consequence |
|---|---|
| MARLO receives, holds in memory, and relays CGIAR passwords | MARLO is inside the blast radius of a credential compromise it does not own |
| Authentication depends on LDAP network reach to an internal AD host | Login availability is coupled to VPN/network topology; a network change breaks login |
| MFA and SSO cannot be offered | They must be enforced at the IdP, which MARLO currently bypasses |
| Every CGIAR login performs **two** LDAP round trips | One to resolve the username (`getCgiarNickname`), one to authenticate. Latency and failure surface are doubled |
| The AD integration is a vendored binary (`adauth`) committed into **two** in-repo Maven repositories — 16 versions under `marlo-data`, 11 under `marlo-web` | No upstream security response path; the dependency cannot be patched by MARLO |

Addresses `docs/prd.md` §7.2 (quality and security acceptance).

---

## 3. In-Scope Requirements

### 3.1 Functional

#### CHG-COGNITO-AUTH-001-FN-001 — Branch the login wizard by user type

The system **SHALL** determine, after the user submits their email in wizard step 1 and before any password is requested, whether the account authenticates against CGIAR or locally, and present the corresponding step 3.

##### Scenario: CGIAR user reaches step 3

- **GIVEN** a user whose `users.is_cgiar_user = 1` and whose Global Unit has the Cognito specificity enabled
- **WHEN** they complete wizard step 1 (email) and step 2 (project selection)
- **THEN** step 3 **MUST** present a single "Sign in with CGIAR" control instead of a password field
- **AND** the password input **MUST NOT** be rendered, focusable, or present in the submitted form
- **BUT** it **MUST NOT** change what step 1 or step 2 look like or how they behave
- **AND IT MUST** keep the selected Global Unit visible in step 3, exactly as today

##### Scenario: Local user reaches step 3 — unchanged

- **GIVEN** a user whose `users.is_cgiar_user = 0`
- **WHEN** they complete steps 1 and 2
- **THEN** step 3 **MUST** be byte-for-byte the password step that exists today
- **AND IT MUST** authenticate through the unmodified `DBAuthenticator` MD5 path
- **BUT** it **MUST NOT** contact Cognito, construct an authorize URL, or read any Cognito configuration

##### Scenario: Email not found

- **GIVEN** an email matching no `users` row
- **WHEN** the user submits step 1
- **THEN** the existing `emailNotFound` message **MUST** be shown
- **BUT** the response **MUST NOT** disclose whether an unknown email would have been CGIAR or local

---

#### CHG-COGNITO-AUTH-001-FN-002 — Authenticate a CGIAR user through Cognito

The system **SHALL** authenticate CGIAR users by an OAuth 2.0 Authorization Code flow with PKCE against Cognito, which federates to the CGIAR IdP.

##### Scenario: Successful sign-in

- **GIVEN** a CGIAR user who has selected a Global Unit and activated "Sign in with CGIAR"
- **WHEN** they authenticate successfully at the CGIAR IdP
- **THEN** they **MUST** return to MARLO already signed in, scoped to the Global Unit they selected before leaving
- **AND** the session **MUST** carry the same attributes a local login produces: `SESSION_USER`, `SESSION_CRP`, the Global Unit's custom parameters, and the session colour
- **AND** `users.last_login` **MUST** be updated
- **AND IT MUST** resolve identity to the pre-existing `users` row — the local record is the authority for who the user is inside MARLO
- **BUT** it **MUST NOT** create, modify, or auto-provision a `users` row

##### Scenario: The user is not a member of the selected Global Unit

- **GIVEN** a CGIAR user who authenticated successfully at the IdP
- **AND** who is not present in `crp_users` for the selected Global Unit
- **WHEN** they return to MARLO
- **THEN** the existing `login.error.invalidUserCrp` message **MUST** be shown
- **AND** the Shiro session **MUST** be cleared and the subject logged out
- **AND IT MUST** behave identically to the local-login case for the same condition

##### Scenario: MARLO account is inactive

- **GIVEN** a user whose IdP authentication succeeds but whose `users` row is inactive
- **WHEN** they return to MARLO
- **THEN** access **MUST** be denied with the existing `USER_DISABLED` message
- **AND IT MUST** treat the local `is_active` flag as authoritative regardless of IdP state

---

#### CHG-COGNITO-AUTH-001-FN-003 — Preserve Global Unit selection across the redirect

The system **SHALL** carry the Global Unit chosen in wizard step 2 through the OIDC round trip and restore it on return.

##### Scenario: Selection survives

- **GIVEN** a CGIAR user who selected Global Unit `X` in step 2
- **WHEN** they complete sign-in at the IdP and return
- **THEN** the session **MUST** be scoped to Global Unit `X`
- **BUT** it **MUST NOT** ask them to select a project again
- **AND IT MUST** reject a returned value that does not match the one MARLO issued, rather than trusting the value present on return

---

#### CHG-COGNITO-AUTH-001-FN-004 — Post-login redirect parity

The system **SHALL** apply the same post-login destination rules to a Cognito login as to a local login.

##### Scenario: Deep-link preserved

- **GIVEN** a CGIAR user who was sent to the login page while requesting a `.do` URL
- **WHEN** they complete Cognito sign-in
- **THEN** they **MUST** land on that original URL
- **AND IT MUST** apply the existing per-Global-Unit-type routing (types 1, 3, 4, 5 → dashboard; type 2 → `centerDashboard.do`)
- **BUT** it **MUST NOT** redirect to a `logout` URL, matching current behavior

---

#### CHG-COGNITO-AUTH-001-FN-005 — Failure handling

The system **SHALL** return the user to the MARLO login page with an actionable message for every failure mode of the Cognito flow.

##### Scenario: The user cancels or the IdP denies

- **GIVEN** a CGIAR user who cancels at the IdP, or whose IdP account is disabled, locked, or expired
- **WHEN** control returns to MARLO
- **THEN** the login page **MUST** be shown with a message describing what happened
- **AND IT MUST** be an i18n key resolved from `global.properties`, never a literal string
- **BUT** it **MUST NOT** expose the raw provider error, an authorization code, or any token value to the browser or the logs

##### Scenario: Cognito is unreachable

- **GIVEN** Cognito or the IdP is unavailable
- **WHEN** a CGIAR user attempts sign-in
- **THEN** a service-unavailable message **MUST** be shown
- **AND IT MUST** leave the local login path fully functional, so local administrators retain access

---

#### CHG-COGNITO-AUTH-001-FN-006 — Username synchronization

The system **SHALL** maintain `users.username` for CGIAR users without an LDAP lookup.

##### Scenario: Username kept current

- **GIVEN** a CGIAR user whose ID token carries their CGIAR login identifier
- **WHEN** they sign in successfully
- **THEN** `users.username` **MUST** be set from that claim, lowercased — matching today's `getCgiarNickname()` behavior
- **AND IT MUST NOT** perform any LDAP call to obtain it
- **BUT** it **MUST NOT** overwrite `users.email`, which remains the identity key inside MARLO

---

#### CHG-COGNITO-AUTH-001-FN-007 — Logout

The system **SHALL** terminate the MARLO session on logout and **SHALL NOT** silently re-authenticate the user.

##### Scenario: Logout is not undone by SSO

- **GIVEN** a CGIAR user with an active MARLO session
- **WHEN** they log out
- **THEN** the Shiro session **MUST** be cleared and the cached authorization info invalidated, as today
- **AND IT MUST NOT** be possible for a subsequent page load to restore the session without an explicit new sign-in action
- **BUT** whether the IdP's own SSO session ends is **out of scope** — see OQ-8

---

### 3.2 Security

#### CHG-COGNITO-AUTH-001-SEC-001 — Token validation

The system **SHALL** fully validate every ID token before deriving identity from it.

##### Scenario: Only valid tokens establish a session

- **GIVEN** an ID token returned from the code exchange
- **WHEN** MARLO processes it
- **THEN** it **MUST** verify the signature against the pool's published keys, and that `iss`, `aud`, and `exp` match the configured pool, client, and current time
- **AND IT MUST** verify the `nonce` matches the one MARLO issued for this attempt
- **BUT** it **MUST NOT** derive any identity from an unverified token — decoding without verification is a complete authentication bypass
- **AND IT MUST** reject, with no session created, a token that is unsigned, wrongly signed, expired, issued for another audience, issued by another issuer, or carrying a replayed or absent `nonce`

#### CHG-COGNITO-AUTH-001-SEC-002 — Request integrity

The system **SHALL** protect the authorization round trip against forgery and interception.

- The authorize request **MUST** include an unguessable `state` bound to the user's session, and the callback **MUST** reject a mismatched or absent `state`.
- The flow **MUST** use PKCE.
- The callback URL **MUST** be validated against an exact-match allowlist; wildcards **MUST NOT** be accepted.
- An authorization code **MUST NOT** be accepted twice.

#### CHG-COGNITO-AUTH-001-SEC-003 — Session fixation

On successful authentication the system **MUST** issue a new session identifier and **MUST NOT** promote the pre-authentication session to an authenticated one.

#### CHG-COGNITO-AUTH-001-SEC-004 — Secret handling

Cognito configuration **MUST** live in `marlo-${profile}.properties`, which is gitignored. No pool ID, client ID, client secret, domain, or callback URL may appear in a `.java` file. The client secret **MUST** be sourced from the deployment's secret store.

#### CHG-COGNITO-AUTH-001-SEC-005 — No credential handling for CGIAR users

MARLO **MUST NOT** accept, transmit, log, or store a CGIAR password at any point in the new flow.

##### Scenario: No endpoint keeps relaying CGIAR credentials

- **GIVEN** a Global Unit with the Cognito specificity enabled
- **AND** a user whose `users.is_cgiar_user = 1`
- **WHEN** any MARLO endpoint receives that user's email and a password
- **THEN** it **MUST** refuse to authenticate them against Active Directory
- **AND IT MUST** return the same generic failure shape as any other rejection, disclosing nothing new
- **BUT** it **MUST NOT** refuse a local (`is_cgiar_user = 0`) account on the same endpoint

> This scenario exists because the login wizard reaches **two** endpoints with the password — `validateUser.do` before `login.do` — and only one of them was visible in the first draft of the design.

#### CHG-COGNITO-AUTH-001-SEC-006 — A federated identity MUST NOT unlock a local account

The system **SHALL** verify that the account resolved from a Cognito identity is itself a CGIAR account before establishing a session.

##### Scenario: IdP credential cannot open a local account

- **GIVEN** a `users` row whose email is a CGIAR address **but** whose `is_cgiar_user = 0`
- **WHEN** someone authenticates successfully at the CGIAR IdP for that address and returns to MARLO
- **THEN** the sign-in **MUST** be refused and no session created
- **AND IT MUST** be refused on `is_cgiar_user`, not merely on Global Unit membership — membership may well succeed
- **BUT** it **MUST NOT** reveal that the account exists under a different authentication mode

> Without this, a valid IdP credential would grant access to a **local** account without its MD5 password — bypassing the exact path this spec declares out of scope and untouched. `OQ-1` exists partly to size this population.

---

### 3.3 Migration & Operations

#### CHG-COGNITO-AUTH-001-MIG-001 — Specificity-gated rollout

The flow **SHALL** be gated by a per-Global-Unit specificity, following the `AGENTS.md` specificity workflow.

##### Scenario: Instant rollback without deploy

- **GIVEN** the Cognito flow is enabled for Global Unit `X`
- **WHEN** an operator sets the specificity to `false` for `X`
- **THEN** CGIAR users of `X` **MUST** immediately return to the LDAP flow on their next login attempt
- **AND IT MUST NOT** require a code change, a build, or a redeploy
- **BUT** it **MUST NOT** affect any other Global Unit

##### Scenario: Both paths coexist

- **GIVEN** Global Unit `X` has the flag enabled and Global Unit `Y` does not
- **WHEN** a CGIAR user who belongs to both signs in
- **THEN** the path used **MUST** be determined by the Global Unit selected in wizard step 2
- **AND IT MUST** resolve the flag before authentication, since the session is not yet populated at that point

> **Constraint discovered in code:** `BaseAction.hasSpecificities()` reads the session, and custom parameters are only written to the session *after* a successful login. The flag therefore **cannot** be read through `hasSpecificities()` at authentication time and must be resolved directly from the selected Global Unit. `design.md` owns the mechanism.

#### CHG-COGNITO-AUTH-001-OPS-001 — Observability

Authentication outcomes **SHALL** be logged with enough detail to diagnose a failure and no more.

- Every attempt **MUST** log outcome, which path was taken, and the Global Unit.
- Logs **MUST NOT** contain tokens, authorization codes, `state`, `nonce`, or passwords.

#### CHG-COGNITO-AUTH-001-OPS-002 — LDAP remains available during rollout

Until every Global Unit is migrated, the LDAP path **MUST** stay functional. Removing `adauth` is child spec 2's work and **MUST NOT** happen here.

---

### 3.4 Non-Functional

| ID | Requirement |
|---|---|
| `CHG-COGNITO-AUTH-001-NF-001` | The complete CGIAR sign-in round trip **SHOULD** complete within 5 s at p95, excluding time the user spends typing at the IdP. Today's double LDAP round trip is the baseline to beat |
| `CHG-COGNITO-AUTH-001-NF-002` | A Cognito or IdP outage **MUST NOT** degrade the local login path |
| `CHG-COGNITO-AUTH-001-NF-003` | The login page **MUST** continue to satisfy `docs/ux-ui/design.md` §10: keyboard reachable, visible focus, accessible name on every control |
| `CHG-COGNITO-AUTH-001-NF-004` | New UI **MUST** reuse existing Bootstrap components and the current palette (`docs/ux-ui/design.md` §7). Light theme only (§11) — no dark-only colors |
| `CHG-COGNITO-AUTH-001-NF-005` | Checkstyle **MUST** pass: 2-space indent, 120-char lines, same-line braces, mandatory blocks, ≤3500-line files |
| `CHG-COGNITO-AUTH-001-NF-006` | No dependency in `marlo-parent/pom.xml` may be downgraded (`CLAUDE.md` hard rule 11) |

---

## 4. Out-of-Scope

| Excluded | Note |
|---|---|
| **The local database login flow** | `DBAuthenticator`, `AuthenticationManager`, `MD5Convert`, `users.password`. Not touched, not refactored, not moved |
| Directory search and `adauth` removal | Child spec 2 (`directory-retirement`) |
| Authorization | `user_role`, `crp_users`, permissions, `canEdit*` interceptors |
| Migrating local users into Cognito | Never in scope |
| Replacing Shiro | Shiro remains the session and authorization layer |
| `/api/v2/*` token auth (`QAToken`) | But see OQ-4 — `/api/**` Basic auth is a **different** surface and is a live risk |
| IdP-side MFA policy | Enabled at the IdP once federation exists; no MARLO code involved |
| Ending the IdP SSO session on MARLO logout | OQ-8 |

---

## 5. Personas Affected

| Persona (`docs/prd.md` §3) | Impact |
|---|---|
| Cluster Coordinator, QA Reviewer, PMU / Program Lead, Program Admin | If CGIAR users: step 3 becomes a button and they authenticate at CGIAR. Everything after login is unchanged |
| Super Admin | Same, plus responsibility for the specificity rollout |
| Local (non-CGIAR) users | **No impact whatsoever** |
| Public reader | None — no authentication involved |
| AI service consumer | None from this spec, **pending OQ-4** |

---

## 6. Defect classes and their gates

Per the AKILI rule that a gate blind to the dominant defect class is not a gate:

| # | Defect class this spec can produce | Gate |
|---|---|---|
| D-1 | Token validation accepts an invalid token → **authentication bypass** | Automated unit tests, one per rejection case in SEC-001. **This is the highest-severity class and it is fully automatable — there is no excuse for leaving it to inspection** |
| D-2 | The local login path is altered | Automated: assert **the `UsernamePasswordToken` path through `doGetAuthenticationInfo()` produces identical behavior**, and that the password input is **absent from the DOM at submit time** on the COGNITO branch. Plus a diff review. *(Restated after Judgment Day round 1: the earlier wording — "the realm's local branch is untouched" — would pass green over a rewritten shared prologue, since `APCustomRealm.java:113-115` casts unconditionally at the head of the method, before any branch.)* |
| D-3 | Global Unit is lost or substituted across the redirect | Automated integration test asserting the restored unit equals the issued one, and that a tampered value is rejected |
| D-4 | The specificity does not actually gate, or cannot roll back | **Manual verification in a live environment.** No automated check can prove "no deploy required". Recorded as a HITL check at the end of phase 1 |
| D-5 | Login page accessibility or visual regression | **No automated gate exists in this repo** — there is no frontend test runner and no visual harness. Substitute: manual keyboard + screen-reader walkthrough at the HITL pause, plus a T6 multimodal review of a screenshot. Recorded as an accepted gap, not silently uncovered |
| D-6 | A secret leaks into source or logs | Automated grep in CI-equivalent form: no Cognito config literal in any `.java`; log assertions for token/code absence |
| D-7 | `/api/**` Basic auth breaks for CGIAR users | **No gate until OQ-4 is answered.** Recorded as an accepted risk with a discovery task; this is a known blind spot, not an oversight |
| D-8 | Session fixation | Automated test: session ID before ≠ session ID after |

**Accepted blind spots:** D-5 (no frontend/visual test infrastructure) and D-7 (unknown consumers). Both are recorded rather than papered over. `docs/infrastructure.md` §6 documents that the repository's entire automated suite is three JUnit 4 classes — a green `mvn test` proves nothing about this spec, and every requirement above needs a test written for it.

---

## 7. Constitutional Compliance Checklist

- [x] **Phase replication:** Not applicable — authentication writes no phased data. `users.last_login` and `users.username` are phase-independent.
- [x] **Save validation:** Not applicable — no `Action.validate()` save pipeline. The login action's existing `validate()` is preserved unchanged.
- [x] **Permissions:** both new actions declare `unloggedStack` (`struts.xml:198-203`) — reachable unauthenticated, and named per TRD §4.3 rule 1. See `design.md` §8.
- [ ] **Specificity:** flag added via `parameters` + `custom_parameters`, constant in **both** `APConstants.java` files, value identical to `parameters.key`.
- [ ] **Migrations:** the specificity ships as a Flyway migration named `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`.
- [ ] **i18n:** all new user-facing strings are keys in `global.properties`. *(Note: the existing `ADLoginMessages` enum holds literal English strings — new messages follow the constitutional rule rather than that precedent. See DL entry.)*
- [ ] **License header:** GPL header on every new `.java` file.
- [ ] **Code style:** Checkstyle passes.
- [x] **REST:** Not applicable — the callback is a Struts action in the existing `home` package, not a new `/api/*` endpoint. No new `*.json` Struts path is introduced (`CLAUDE.md` hard rule 3).
- [ ] **Audit:** `users.username` writes go through the existing `saveUser` path and its audit listener.
- [ ] **Dependency floors:** AWS SDK and JWT libraries are additions; nothing is downgraded.
- [x] **Branching:** working on `staging-cognito`, branched from and merging to `staging`.

---

## 8. Open Questions

| # | Question | Blocks | Owner |
|---|---|---|---|
| OQ-3 | Which IdP does CGIAR run (ADFS / Entra ID), and will CGIAR IT federate MARLO? | **Implementation, not specification.** A "no" invalidates the approach and returns to the parent proposal | CGIAR IT |
| OQ-1 | `SELECT is_cgiar_user, COUNT(*) FROM users GROUP BY is_cgiar_user` — and how many of those emails resolve at the IdP? | Rollout sizing; FN-002 orphan handling | IBD |
| OQ-4 | Who calls `/api/**` with Basic auth, and are any of them CGIAR users? Federated identities cannot use Basic auth, so that surface breaks silently | D-7 has no gate until answered | IBD |
| OQ-8 | On MARLO logout, should the IdP SSO session also end (RP-initiated logout)? Ending it may sign the user out of unrelated CGIAR applications | FN-007's boundary | PMU + CGIAR IT |
| OQ-9 | Which token claim is the stable user identifier — `sub`, `oid`, or email? Email is mutable and would orphan an account on change | FN-002 identity mapping | IBD + CGIAR IT |
| OQ-10 | For a CGIAR user whose Global Unit has **not** enabled the flag: LDAP as today. Confirmed — but should a user belonging to a mix of enabled and disabled units see anything different? | FN-001 edge case | PMU |
| OQ-11 | Do any **type-2 (Center)** or **type-5** Global Units have CGIAR users? Verified: 40 existing specificity migrations use `global_unit_type_id` in (1,3,4) and **none** uses type 2, so a Center can never enable this flag and its CGIAR users stay on LDAP permanently — making phase 4 unreachable for them | `design.md` §3, §14; R-D10 | PMU |

---

## 9. Decision Log

| Date | Decision | Rationale |
|---|---|---|
| 2026-08-24 | Option A — Cognito federated to CGIAR AD, redirect for CGIAR users only | AWS does not permit `InitiateAuth` for federated users. Inherited from the parent proposal |
| 2026-08-24 | Branch the wizard at step 1→2, not at step 3 | `crpByEmail.do` already resolves the user record; adding the CGIAR flag to its response is a minimal change and means a password field is never rendered for a CGIAR user |
| 2026-08-24 | Carry the Global Unit through the OIDC round trip rather than re-asking after callback | The parent proposal assumed selection would move after login. Reading the code showed step 2 precedes authentication, so the selection can be preserved — better UX than the proposal predicted |
| 2026-08-24 | The specificity is resolved from the selected Global Unit, not via `hasSpecificities()` | `hasSpecificities()` reads the session, which is unpopulated at authentication time. Discovered in `BaseAction:6574` |
| 2026-08-24 | New login messages use i18n keys, not the `ADLoginMessages` literal-string precedent | `CLAUDE.md` hard rule 8. Following the existing pattern would propagate a constitutional violation. Existing enum values are left untouched — changing them is not this spec's scope |
| 2026-08-24 | D-5 and D-7 recorded as accepted blind spots | The repository has no frontend/visual test harness, and `/api/**` consumers are unknown. Naming them is what keeps them from being mistaken for coverage |
