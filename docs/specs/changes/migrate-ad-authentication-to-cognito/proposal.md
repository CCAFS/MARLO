# Migrate AD/LDAP Authentication to Amazon Cognito — Proposal

## Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/migrate-ad-authentication-to-cognito` |
| Slug | `migrate-ad-authentication-to-cognito` — explicitly supplied by the user; free-text context recorded below, never interpolated into the path |
| Type | **Change** |
| Approval Mode | `gated` (default — no end-to-end mandate was given) |
| Depends on | `none` |
| Parallel-safe | `no` — touches `APConstants.java` (both copies), `BaseAction.java`, and `marlo-parent/pom.xml`, all shared writers |
| Date created | 2026-08-24 |
| Status | **Approved** — 2026-08-24 |
| Decision (OQ-2) | **Option A** — Cognito federated to CGIAR AD; redirect for CGIAR users, local form untouched. Confirmed by the user 2026-08-24, superseding the earlier "keep the form" preference once the AWS federation constraint was established |
| Structure (OQ-7) | **Spec family approved** — see [`family.md`](./family.md) |
| Working branch | `staging-cognito` |
| Evidence base | CodeGraph index (4,251 files) + direct source reads. No assumption below is inferred from documentation alone |

---

## Intent

Replace MARLO's direct LDAP/LDAPS authentication against CGIAR Active Directory with Amazon Cognito, **without touching the local database login flow**, and retire the `org.cgiar.ciat.auth` (`adauth`) library from the codebase entirely.

---

## ⚠️ Read this first: the two stated requirements are mutually exclusive

You asked for **(1)** Cognito federating against CGIAR AD as an external IdP, and **(2)** MARLO keeping its own login form.

**AWS does not allow both.** From the Cognito developer guide:

> *"You can't sign in federated users with API operations like `InitiateAuth` and `AdminInitiateAuth`. Federated users can only sign in with the Login endpoint or the Authorize endpoint."*

A form-post backend call (`InitiateAuth` with `USER_PASSWORD_AUTH`) authenticates **only users native to the Cognito user pool**. A user whose identity lives in an external SAML/OIDC IdP has no password in Cognito for that API to check — the credential never leaves the IdP, which is the entire security property federation buys. Reaching a federated IdP requires a browser redirect through `/oauth2/authorize`.

This is a platform constraint, not a configuration option or a preference to be engineered around. Every option below is honest about which of your two requirements it sacrifices.

**The recommendation (Option A) preserves your local-login requirement exactly and sacrifices only the *CGIAR* half of the form** — CGIAR users get a "Sign in with CGIAR" button that redirects; local users keep the identical form they use today. This is the closest achievable point to what you asked for.

---

## Problem / Current Behavior

### How authentication works today

The entry point is `login.do` → `LoginAction.login()`, which calls `UserManagerImp.login(email, password)`. That builds a Shiro `UsernamePasswordToken` and calls `Subject.login(token)`. Shiro dispatches to the single configured realm, `APCustomRealm`, whose `doGetAuthenticationInfo()` holds the fork:

```
login.do  →  LoginAction.login()
          →  UserManagerImp.login(email, password)
          →  Shiro Subject.login(UsernamePasswordToken)
          →  APCustomRealm.doGetAuthenticationInfo()
                │
                ├─ lookup: username contains "@" ? getUserByEmail : getUserByUsername
                ├─ user.isActive() == false        → USER_DISABLED
                │
                ├─ user.isCgiarUser() == TRUE  ─────────────────┐  ← THE SELECTOR
                │    ├─ getCgiarNickname(user)                  │
                │    │    └─ new LDAPService().searchUserByEmail(email)
                │    │         └─ writes back user.username from AD, persists via userManager.saveUser()
                │    │         └─ null → ERROR_NO_SUCH_USER
                │    └─ ldapAuthenticator.authenticate(user.getUsername(), password)
                │         └─ new LDAPService().authenticateUser() → ADConexion
                │                                                  │
                └─ user.isCgiarUser() == FALSE ─────────────────┐  │
                     └─ dbAuthenticator.authenticate(email, pw) │  │
                          └─ MD5Convert.stringToMD5(password)   │  │
                          └─ AuthenticationManager.veirifyCredentials()
                                                                │  │
          ←─────────────── SimpleAuthenticationInfo(user.getId(), …)
          →  LoginAction.login(loggedUser, loggedCrp)
                ├─ crpUserManager.existCrpUser(userId, crpId)   ← Global Unit membership gate
                ├─ session: SESSION_USER, SESSION_CRP, custom parameters, color
                └─ userManager.saveLastLogin()
```

### The authentication selector

| Item | Value |
|---|---|
| Java field | `User.cgiarUser` (`boolean`) — `marlo-data/.../data/model/User.java:56` |
| DB column | **`users.is_cgiar_user`** (`boolean`, `not-null`) — `marlo-data/src/main/resources/xmls/Users.hbm.xml:27-29` |
| `true` | Authenticate against CGIAR Active Directory via LDAP/LDAPS — **the flow being migrated** |
| `false` | Authenticate against `users.password` (MD5 hash) — **must remain byte-for-byte unchanged** |

The two branches are cleanly separated inside `APCustomRealm`. **Note:** the `Authenticator` interface itself is `(String email, String password) → Map`, so it cannot carry a token-based assertion — see `auth-flow/design.md` DD-1, which drops the seam rather than widening a shared interface for one caller.

### Authorization is fully decoupled from authentication

`APCustomRealm.doGetAuthorizationInfo()` reads roles and permissions from `user_role`, `crp_users`, and `userManager.getPermission()` — **all local database state**. AD contributes nothing to authorization. Roles, permissions, Global Unit membership, and every `canEdit*` interceptor are unaffected by this change.

`SimpleAuthenticationInfo` uses `user.getId()` as the principal, and every downstream consumer resolves identity from that Long. **No downstream code depends on how the user proved who they were.**

---

## Which components participate in the LDAP/AD flow

### Authentication path (4 files)

| File | Role |
|---|---|
| `marlo-data/.../security/APCustomRealm.java` | The selector (`isCgiarUser`), plus `getCgiarNickname()` which performs an **extra** LDAP lookup on every CGIAR login |
| `marlo-data/.../security/authentication/LDAPAuthenticator.java` | `@Named("LDAP")` — wraps `LDAPService.authenticateUser()` → `ADConexion` |
| `marlo-data/.../security/authentication/Authenticator.java` | The interface both password-based flows implement. **Not reusable for Cognito** (signature is `(String, String)`); left unmodified |
| `marlo-data/.../MarloShiroConfiguration.java` | Wires realm, session manager (30 min), `JSESSIONID` cookie, `/api/**` → `authcBasic` |

### Directory-search path (8 more call sites — in scope per your decision)

| File | What it does |
|---|---|
| `marlo-web/.../action/BaseAction.java:4797` | `getOutlookUser(email)` — shared helper, called from admin flows |
| `marlo-web/.../action/crp/admin/CrpUsersAction.java:630` | Auto-fills name/username from AD when adding a CRP user |
| `marlo-web/.../action/json/global/ManageUsersAction.java:151` | Same, global user management |
| `marlo-web/.../action/center/json/global/ManageUsersAction.java:249` | Same, Center user management |
| `marlo-web/.../action/json/global/SearchUserAction.java:193` | User-search autocomplete |
| `marlo-web/.../action/center/capdev/ContactPersonAction.java:86` | AD user search for CapDev contact person |
| `marlo-web/.../validation/superadmin/GuestUsersValidator.java:36` | Rejects a "guest" whose email exists in CGIAR AD |
| `marlo-web/.../utils/searchUsersUtil.java:14` | Utility wrapper |

### Packaging

| Item | Value |
|---|---|
| Artifact | `org.cgiar.ciat.auth:adauth`, version property `ciat-adauth.version` = **`5.7`** (`marlo-parent/pom.xml:14`) |
| Source | **Two file-based Maven repos committed into the repo**: `marlo-data/src/main/resources/libs/.../adauth/` (16 versions, 1.1 → 5.7) and `marlo-web/src/main/resources/libs/.../adauth/` (11 versions, 1.1 → 2.2) |
| Consumers | `marlo-data/pom.xml:27`, `marlo-web/pom.xml:76` |

> **Documentation drift:** `docs/trd/trd.md` §8.1 says `adauth-5.6.jar` under `marlo-web/src/main/resources/libs`. Both facts are wrong — the version is **5.7** and the repo lives in **`marlo-data`**. The TRD needs correcting regardless of whether this proposal is approved.

### Existing partial decoupling

`ad_user` is a **local mirror table** (`AdUser` entity, `AdUserManager`, `AdUserMySQLDAO`). `ContactPersonAction` already queries `adUsermanager.searchUsers()` against the database rather than LDAP. Some directory search has already moved off live LDAP — this reduces the retirement work and gives a proven pattern for the rest.

---

## Proposed Outcome

| Actor | Today | After |
|---|---|---|
| CGIAR user (`is_cgiar_user = 1`) | Types email + password into MARLO's form; MARLO relays credentials to AD over LDAP | Clicks **Sign in with CGIAR**; authenticates at CGIAR's IdP through Cognito; MARLO never sees the password |
| Local user (`is_cgiar_user = 0`) | Types email + password; MD5 compared against `users.password` | **Identical. Unchanged. Same form, same code path, same DAO.** |
| MARLO server | Holds AD service-account credentials; needs LDAP network reach to the CGIAR AD Global Catalog | Holds only an OIDC client ID/secret; no LDAP egress; no user passwords in memory |
| MFA / SSO | Impossible — MARLO is a credential intermediary | Available; enforced at the IdP, no MARLO code involved |
| Roles & permissions | Local DB | **Unchanged — local DB** |

---

## Scope

### In scope

1. Replace the CGIAR authentication branch with a Cognito-brokered OIDC flow.
2. Add an OIDC callback endpoint and token validation; map the verified identity to the existing `users` row and issue the same Shiro session.
3. Dispatch on Shiro token type inside the single realm. `Authenticator.java`, `DBAuthenticator`, and `LDAPAuthenticator` are all left unmodified (see `auth-flow/design.md` DD-1).
4. Migrate the 8 directory-search call sites off `adauth`.
5. Remove the `adauth` dependency and its committed file-repo.
6. Move all AD/Cognito configuration into `marlo-${profile}.properties`.
7. Feature-flag the new flow via a **specificity** so rollout is per-Global-Unit and instantly reversible.
8. Correct `docs/trd/trd.md` §8.1 and `docs/infrastructure.md` §4.

### Out of scope (non-goals)

- **The local DB login flow.** `DBAuthenticator`, `AuthenticationManager`, `MD5Convert`, and `users.password` are not touched. Not refactored, not "improved", not moved.
- Authorization: `user_role`, `crp_users`, permissions, `canEdit*` interceptors.
- The Global Unit selection step and `crpUserManager.existCrpUser()` gate.
- `/api/v2/*` token auth (`QAToken`) — but see Risk R-4, which is not the same thing.
- Migrating local users into Cognito.
- Replacing Shiro. Shiro remains the session and authorization layer.

---

## Affected Users, Systems, And Specs

| Affected | Detail |
|---|---|
| Users | Every user with `users.is_cgiar_user = 1`. **Count unknown — see OQ-1.** Local users: zero impact |
| Modules | `marlo-data` (realm, authenticators, config), `marlo-web` (login action, FTL, 7 search call sites, `BaseAction`), `marlo-parent` (dependency) |
| Shared writers | `APConstants.java` **×2**, `BaseAction.java`, `marlo-parent/pom.xml` → this spec is **not parallel-safe** with any other |
| Infrastructure | New: Cognito User Pool + app client + SAML/OIDC IdP federation to CGIAR. Removed: LDAP egress from app EC2 |
| External | CGIAR IT must register MARLO as a relying party and supply IdP metadata — **an external dependency on another team's calendar** |
| Constitutional docs | `docs/trd/trd.md` §8.1, §8.5, ADR-6; `docs/infrastructure.md` §4 |
| Related specs | None existing. `docs/specs/domain/auth/` does not exist yet |

---

## Visual Reference

- **Source:** None.
- **Location:** n/a.
- **Notes:** The only UI change is adding a **Sign in with CGIAR** button to `/WEB-INF/global/views/login/login.ftl` and visually separating it from the retained local form. `docs/ux-ui/design.md` §7 requires reusing existing Bootstrap components and the current palette; §11 fixes MARLO to light theme only. If a mockup is wanted before `/akili-specify`, say so and one will be generated into `mockup/`.

---

## Requirement Delta Preview

### ADDED

- OIDC Authorization Code + PKCE flow: an authorize redirect and a callback endpoint.
- ID-token validation: signature against the pool JWKS, plus `iss`, `aud`, `exp`, `nonce`.
- Identity mapping from a verified token claim (email) to the local `users` row.
- A Shiro `AuthenticationToken` subtype carrying a validated assertion, dispatched by token type in the realm.
- A specificity flag gating the new flow per Global Unit.
- Configuration keys in `marlo-${profile}.properties`.
- Directory-search abstraction replacing `LDAPService` at 8 call sites.

### MODIFIED

- `APCustomRealm.doGetAuthenticationInfo()` — the `isCgiarUser` branch routes to Cognito. **The `else` branch is not edited.**
- `LoginAction` — gains callback handling; the existing `login()` path stays for local users.
- `login.ftl` — adds the CGIAR button.
- `BaseAction.getOutlookUser()` — reimplemented against the new directory source.
- `ADLoginMessages` — extended with OIDC error cases; existing values preserved for the local flow.

### REMOVED

- `LDAPAuthenticator` and its `@Named("LDAP")` binding.
- `APCustomRealm.getCgiarNickname()` (its username sync moves into claim mapping).
- All **18** `org.cgiar.ciat.auth` imports, across **10** files.
- The `adauth` dependency and `marlo-data/src/main/resources/libs/org/cgiar/ciat/auth/`.
- `GENERICUSER_AD`, `GENERICPASSWORD_AD`, `HOSTNAME_AD`, `PORT_AD` from both `APConstants.java`.

---

## Approach Options

### Option A — Cognito federated to CGIAR AD, redirect for CGIAR users only ✅ **Recommended**

CGIAR users get a button that redirects through `/oauth2/authorize` to the CGIAR IdP. Local users keep the existing form untouched on the same page.

| | |
|---|---|
| **Satisfies** | Federation (your Q1). Local flow untouched (your requirement 6). Full `adauth` retirement |
| **Sacrifices** | The CGIAR half of MARLO's form becomes a button |
| **Pros** | MARLO never handles a CGIAR password. MFA/SSO become available at no code cost. No password migration. AD stays the source of truth. Standard, well-documented pattern. LDAP egress closes permanently |
| **Cons** | Login page UX changes for CGIAR users. Requires CGIAR IT to federate. Post-callback must re-establish Global Unit selection |
| **Risk** | Medium — concentrated in the callback and session handoff, which is testable |

### Option B — Cognito native user pool, MARLO's form preserved

Users become native pool members; MARLO calls `InitiateAuth` with `USER_PASSWORD_AUTH` from the backend.

| | |
|---|---|
| **Satisfies** | The form stays (your Q2) |
| **Sacrifices** | **Federation — contradicts your Q1.** AD stops being the source of truth |
| **Pros** | Nearly drop-in: swap `LDAPAuthenticator` for a `CognitoAuthenticator` behind the same interface. Smallest diff. Login page unchanged |
| **Cons** | AD passwords are not extractable, so it needs a **User Migration Lambda** that authenticates against LDAP on first sign-in — **LDAP cannot be switched off until the last user has logged in at least once**, possibly never. MARLO still handles passwords. MFA and SSO stay blocked. Two directories drift apart: an AD-side password change or disablement does not propagate |
| **Risk** | High — the "temporary" LDAP bridge is the kind that becomes permanent, and you end up owning Cognito *and* LDAP |

### Option C — `CUSTOM_AUTH` Lambda triggers proxying to AD

MARLO's form posts to Cognito; Lambda triggers authenticate against LDAP.

| | |
|---|---|
| **Verdict** | **Not recommended.** Keeps LDAP permanently, adds Lambda, adds VPC networking, and delivers neither SSO nor MFA. It buys the appearance of migration without its benefits |

### Recommendation

**Option A**, because it is the only option that satisfies the requirement you stated first and is the only one with a real end state — Option B's LDAP bridge has no defined removal date, which means "migrating to Cognito" would leave MARLO operating two identity systems indefinitely.

Requirement 6 (*"the local login flow must remain unchanged"*) is honored more strictly under A than under B: A adds a parallel path and never edits the `else` branch, whereas B rewrites the shared authenticator wiring both flows pass through.

**If the login form is a hard, non-negotiable product constraint**, then Q1 must yield and Option B becomes the path — but that decision should be made explicitly, with its permanent-LDAP consequence understood, not discovered during implementation.

---

## Recommended scope split (spec family)

The approved scope is two distinct changes with different risk profiles. Recommendation — split into a family before `/akili-specify`:

| # | Spec | Depends on | Parallel-safe | Rationale |
|---|---|---|---|---|
| 1 | `changes/migrate-ad-authentication-to-cognito/auth-flow` | none | no | Authentication only. Security-critical, needs staged rollout and a live rollback |
| 2 | `changes/migrate-ad-authentication-to-cognito/directory-retirement` | spec 1 | no | The 7 search call sites + `adauth` removal. Lower risk; can follow at its own pace |

The dependency is real: `adauth` cannot be deleted while `APCustomRealm.getCgiarNickname()` still calls it. Splitting also means an authentication problem never blocks on an autocomplete problem.

If you approve the split, `family.md` gets written in the parent folder **before** any child folder is created (closed-set rule). If you prefer one spec, say so and it proceeds flat.

---

## Required changes

### Application

| Area | Change |
|---|---|
| `marlo-data` security | New Cognito authenticator behind the existing `Authenticator` seam; realm routes the `isCgiarUser` branch to it |
| `marlo-web` login | Callback action, state/nonce handling, session establishment, Global Unit selection after callback |
| Session | Reuse the existing Shiro `DefaultWebSessionManager` (30 min, `JSESSIONID`, `httpOnly`). **No new session mechanism** |
| Directory search | One interface replacing `LDAPService` at 8 call sites |
| Dependencies | Add AWS SDK v2 (`cognitoidentityprovider`) + a JWT library. Remove `adauth`. **No downgrades** below `marlo-parent/pom.xml` floors (hard rule 11) |
| i18n | New keys in `global.properties` |

### AWS

| Component | Purpose |
|---|---|
| Cognito User Pool | Identity broker |
| SAML 2.0 / OIDC IdP | Federation to CGIAR AD (ADFS or Entra ID) — **requires CGIAR IT** |
| App client | Confidential client, Authorization Code + PKCE, callback allowlist per environment |
| Domain | Hosted domain for `/oauth2/authorize` |
| Secrets Manager / SSM | Client secret — **never** in `APConstants.java` |

### Configuration

All of the following move into `marlo-${profile}.properties` (gitignored, bootstrapped from `marlo-test.properties`): user-pool ID, region, client ID, client-secret reference, domain, callback URL, JWKS URI. Per environment: dev, test, staging, pro.

### Security

- Validate the ID token fully — signature via JWKS, plus `iss`, `aud`, `exp`, `nonce`. A decode-without-verify is a complete authentication bypass.
- `state` for CSRF; PKCE for code interception.
- Rotate the Shiro session ID on successful login (session-fixation).
- Callback URL allowlist — exact match, no wildcards.
- Never log tokens or authorization codes.

---

## Migration, rollout, and rollback

### Rollout (per Global Unit, using the existing specificity mechanism)

| Phase | Action | Exit criterion |
|---|---|---|
| 0 | Cognito + federation configured in dev; MARLO code merged behind a specificity defaulting to `false` | Nothing changes for anyone |
| 1 | Enable for one internal test Global Unit | IBD team logs in successfully; roles and permissions verified intact |
| 2 | Enable for one pilot CRP | One full reporting week with no auth incidents |
| 3 | Progressive enablement | All Global Units enabled |
| 4 | Remove `adauth`, close LDAP egress, delete AD constants | Grep shows zero `org.cgiar.ciat.auth` references |

Per `AGENTS.md`, the specificity needs a `parameters` migration for `global_unit_type_id` 1, 3, 4, `custom_parameters` rows for rollout, and a constant in **both** `APConstants.java` files whose value equals `parameters.key` exactly.

### Rollback

| Phase | Rollback | Cost |
|---|---|---|
| 0–3 | Set the specificity to `false` for the affected Global Unit | **Seconds. No deploy.** This is why the flag is not optional |
| 4 | Code revert + redeploy + reopen LDAP egress | Hours |

Phase 4 is the one-way door. It should not be crossed until phase 3 has held for a defined, agreed period.

---

## Risks, Dependencies, And Open Questions

### Risks

| ID | Risk | Severity | Mitigation |
|---|---|---|---|
| ~~R-1~~ | ~~Requirements Q1 and Q2 are incompatible~~ | ~~Blocking~~ — **closed** | Resolved 2026-08-24: **Option A** chosen. The CGIAR form becomes a redirect button; the local form is untouched |
| R-2 | Total lockout of all CGIAR users if the callback or token validation is wrong | Critical | Specificity flag with instant per-GU rollback; the local flow is unaffected and always available |
| R-3 | **CGIAR IT is an external dependency** and may not federate on your timeline | High | Confirm feasibility with CGIAR IT **before** approving. This can halt the spec after work has started |
| R-4 | `/api/**` uses `authcBasic`, which routes through the **same** `APCustomRealm`. A CGIAR user calling the REST API with Basic auth today authenticates via LDAP. Federated identities **cannot** authenticate via Basic auth — that surface breaks silently | **High — easy to miss** | Enumerate REST consumers before phase 1. May need a separate token strategy. This risk alone justifies a discovery task |
| R-5 | `getCgiarNickname()` writes `user.username` from AD on every login. Removing it may leave usernames stale or unset | Medium | Map the equivalent claim in the callback; verify no downstream consumer breaks |
| R-6 | Email is the join key between the token and `users`. A CGIAR email change or case mismatch orphans the account | Medium | Normalize to lowercase (as `LoginAction` already does); prefer a stable claim (`sub`/`oid`) with email as fallback; consider persisting the stable ID |
| R-7 | Users with `is_cgiar_user = 1` whose email is **not** in CGIAR AD currently fail with `ERROR_NO_SUCH_USER`. These accounts may be latent and undiscovered | Medium | Audit `users` before phase 1 (OQ-1) |
| R-8 | The `ad_user` mirror table's freshness and update mechanism are unknown | Medium | Investigate before relying on it for directory search |
| R-9 | **The automated test suite is three JUnit 4 classes**, none covering authentication (`docs/infrastructure.md` §6). There is no regression net for the most security-sensitive code in the system | **High** | `/akili-test` must author auth coverage. Treat a green `mvn test` as proving nothing here |
| R-10 | Cognito is a new runtime dependency on the login path; an outage means no CGIAR logins | Medium | Accept and document; local admin accounts remain a break-glass path |

### Dependencies

| Dependency | Owner | Blocking? |
|---|---|---|
| Cognito User Pool + federation to CGIAR AD | CGIAR IT + IBD DevOps | **Yes** |
| AWS SDK v2 + JWT library added to `marlo-parent/pom.xml` | IBD | Yes |
| Callback URLs registered per environment | IBD DevOps | Yes |
| Decision on Option A vs B | **You** | **Yes — OQ-2** |

### Open Questions

| # | Question | Blocks |
|---|---|---|
| OQ-1 | How many users have `is_cgiar_user = 1`, and how many of those emails actually resolve in CGIAR AD today? (`SELECT is_cgiar_user, COUNT(*) FROM users GROUP BY is_cgiar_user`) | Rollout sizing, R-7 |
| ~~OQ-2~~ | ~~Option A or Option B?~~ → **RESOLVED 2026-08-24: Option A** | ~~Everything~~ — closed |
| OQ-3 | Which IdP does CGIAR run — ADFS or Entra ID — and will they federate MARLO? | R-3, and feasibility overall |
| OQ-4 | Who consumes `/api/**` with Basic auth, and are any of those callers CGIAR users? | R-4 |
| OQ-5 | Is `ad_user` populated by a live job, or is it a stale one-time import? | Directory-search target |
| OQ-6 | Should directory search move to Microsoft Graph, or lean on `ad_user`? Cognito `ListUsers` is **not** a substitute — a federated pool only contains users who have already signed in at least once | Spec 2 design |
| OQ-7 | Split into a spec family (recommended), or keep one flat spec? | Structure |

---

## Success Criteria

| # | Criterion |
|---|---|
| SC-1 | A CGIAR user signs in through Cognito and lands on the same dashboard, with the same roles and permissions, as before |
| SC-2 | **A local user's login is byte-for-byte unchanged** — same form, same MD5 path, same DAO. Verified by diff: the `else` branch of `doGetAuthenticationInfo()` is untouched |
| SC-3 | `grep -rn "org.cgiar.ciat.auth"` over `marlo-web` and `marlo-data` returns **zero** results |
| SC-4 | `marlo-data/src/main/resources/libs/org/cgiar/ciat/auth/` is deleted and no `adauth` dependency remains in any POM |
| SC-5 | All Cognito configuration — pool ID, region, client ID, client-secret reference, domain, callback URL, JWKS URI — lives in `marlo-${profile}.properties`, never in a `.java` file. The retired `*_AD` constants are gone from both `APConstants.java` |
| SC-6 | The specificity flag disables the new flow per Global Unit **without a deploy**, verified in a live environment |
| SC-7 | ID-token validation rejects: bad signature, expired, wrong `aud`, wrong `iss`, replayed `nonce` — each proven by a test |
| SC-8 | `mvn -q checkstyle:check` and `mvn -q install -DskipTests -pl marlo-web -am` pass |
| SC-9 | Authorization is provably unchanged: `user_role`, `crp_users`, and `getPermission()` produce identical results before and after |
| SC-10 | App servers have no LDAP network egress after phase 4 |

---

## Next Step

OQ-2 is resolved (**Option A**) and the spec family is approved. `family.md` is written. Specify child 1:

```text
/akili-specify changes/migrate-ad-authentication-to-cognito/auth-flow
```

Child 2 (`directory-retirement`) is specified after child 1 lands — it cannot proceed while
`APCustomRealm.getCgiarNickname()` still calls `LDAPService`.

> **OQ-3 remains open and is not a blocker for specifying.** Whether CGIAR IT will federate MARLO is
> an external dependency that blocks *implementation*, not *specification*. It should be confirmed
> before `/akili-execute` starts, because a "no" invalidates Option A entirely and forces a return to
> this proposal — not a patch to the spec.

---

## Free-text context as supplied

> Analyze and propose the migration of MARLO's external authentication flow from Active Directory/LDAP to Amazon Cognito. MARLO has two login flows depending on a user attribute stored in the database. One flow authenticates locally against the MARLO database. The second uses a custom library to authenticate against Active Directory through LDAP/LDAPS. The local authentication flow must remain unchanged. Only the Active Directory/LDAP flow should be evaluated for migration. Document: how authentication works today; which components participate in the LDAP/AD flow; the proposed target flow using Amazon Cognito; required application, AWS, configuration, and security changes; migration considerations, risks, dependencies, rollout, and rollback; and what must remain unchanged to preserve the existing local-login flow. Do not implement changes yet.
