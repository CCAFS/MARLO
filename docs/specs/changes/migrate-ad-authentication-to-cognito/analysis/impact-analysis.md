# AD → Cognito — System Impact Analysis

**Analysis ID:** `CHG-COGNITO-AUTH-ANALYSIS-001`
**Scope:** the whole MARLO checkout, not one spec
**Method:** static analysis of the working tree on branch `staging-cognito`
**Date:** 2026-08-24
**Companion:** [`touchpoint-inventory.md`](./touchpoint-inventory.md) — the file-by-file table
**Related specs:** [`../proposal.md`](../proposal.md) · [`../auth-flow/requirements.md`](../auth-flow/requirements.md) · [`../auth-flow/design.md`](../auth-flow/design.md)

> This document answers two questions: **how much of MARLO is affected** by moving CGIAR
> authentication to Amazon Cognito, and **which points must be touched** to make the connection.
> It is an assessment of the codebase as it stands. It does not propose an implementation.

---

## 1. Headline

**The authentication *decision* is concentrated in one method. The Active Directory *dependency* is not.**

That asymmetry is the single most important fact for planning this migration, and it is where the
effort estimate goes wrong if it is missed.

| | Files | Where |
|---|---|---|
| Code that decides *how a user proves identity* | **1 method** | `APCustomRealm.doGetAuthenticationInfo()` — one `if/else` at `:136-145` |
| Code that calls Active Directory for **any** reason | **8 sites across 8 files** | 2 for authentication, **6 for user-directory lookup** |

Replacing the CGIAR login path is a genuinely small change — the seam is real and the design's claim
about it is accurate. **Removing Active Directory from MARLO is a different and larger project**,
because three quarters of the AD calls have nothing to do with logging in: they populate people
pickers, autofill new user records, and validate guest accounts.

**Consequence:** after the `auth-flow` spec ships, MARLO still depends on `adauth`, still holds an
AD service account, and still needs network reachability to CGIAR AD. The spec family already
separates this correctly — child 2 (`directory-retirement`) owns those 6 sites — but the
**benefit of the migration is not realized until child 2 lands**, and that should be stated to
stakeholders up front rather than discovered at rollout.

---

## 2. Current authentication architecture (verified)

### 2.1 The single dispatch point

```
                         Shiro Subject.login(UsernamePasswordToken)
                                        │
                                        ▼
                     APCustomRealm.doGetAuthenticationInfo()   ← marlo-data, 318 LOC
                                        │
                        :115  (UsernamePasswordToken) token    ← UNCONDITIONAL CAST
                                        │
                     :124  lookup users row by email or username
                                        │
                         ┌──────────────┴──────────────┐
              :136  user.isCgiarUser()            :144  else
                         │                              │
        :137 getCgiarNickname(user)            dbAuthenticator.authenticate()
             → LDAPService.searchUserByEmail()       → MD5 vs users.password
             → writes users.username
             → userManager.saveUser()
                         │
        :138 ldapAuthenticator.authenticate()
             → ADConexion bind against CGIAR AD
                         │
                         └──────────────┬──────────────┘
                                        ▼
                     :158  new SimpleAuthenticationInfo(user.getId(), ...)
```

Three facts this diagram makes visible:

1. **The cast at `:115` is unconditional.** Any non-`UsernamePasswordToken` reaching this realm
   throws `ClassCastException`. This is why a Cognito token type must be dispatched *above* it.
2. **The CGIAR path makes two AD round trips per login**, not one — a directory search
   (`getCgiarNickname`) and then a bind. The search is not incidental: it is what populates
   `users.username`, and it **writes to the database** (`userManager.saveUser()`) during
   authentication.
3. **`Authenticator` cannot be reused.** Its only method is
   `Map<String,Object> authenticate(String email, String password)`. There is no parameter that
   could carry a validated token or assertion. Extending the interface would change both existing
   implementations — which the spec family explicitly forbids.

### 2.2 Session handling

`MarloShiroConfiguration` builds the security manager by hand. Relevant settings:

| Setting | Value | Line |
|---|---|---|
| Session timeout | 30 minutes | `:75` |
| Session cookie | `JSESSIONID`, `httpOnly`, path `/` | `sessionIdCookie()` |
| Realm construction | Hand-wired, not classpath-scanned | `:48` |
| `/api/**` filter | **`authcBasic`** — same realm | `:113` |

---

## 3. Every surface that reaches the realm

The spec documents treat `login.do` as the authentication entry point. **There are four.** Two of
them are not named anywhere in `requirements.md`, `design.md`, or `tasks.md`.

| # | Surface | Path to the realm | Authenticated? | Named in the spec? |
|---|---|---|---|---|
| 1 | `login.do` | `LoginAction.login()` → `userManager.login()` → `Subject.login()` | n/a — this *is* login | ✅ Yes |
| 2 | `validateUser.do` | `ValidateUserAction:71` → `userManager.login()` → `Subject.login()` | ❌ **No interceptor stack** — package `homeJson` extends `json-default` | ✅ Yes (task T11) |
| 3 | `/api/**` | Shiro `authcBasic` filter → same realm | Basic auth | ⚠️ Only as an open risk (**R-D4**, no gate) |
| 4 | **`ClarisaPublicAccesFilter:81`** | `subject.login(new UsernamePasswordToken(...))` with a **service account from config** | Filter-driven, for `/swagger/*` pages | ❌ **Not mentioned anywhere** |

### 3.1 Surface 4 is an unrecorded risk

`ClarisaPublicAccesFilter` (registered in `WebAppInitializer:126`) logs in a configured service
account so that Swagger documentation pages render for anonymous visitors:

```java
String user = this.config.getClarisaUser();
String password = this.config.getClarisaPassword();
User userobj = userManager.login(user, password);      // → realm → is_cgiar_user branch
if (userobj != null) {
  UsernamePasswordToken token = new UsernamePasswordToken(user, password);
  subject.login(token);                                 // → realm again
}
```

**If that account's `users.is_cgiar_user = 1`, it authenticates via LDAP today** and will be caught
by any guard that refuses CGIAR password authentication for a flag-enabled unit. The public Swagger
pages would stop rendering.

The account's flag value cannot be determined from source — it lives in
`marlo-${profile}.properties`, which is gitignored. **This is a one-query check that must happen
before any rollout**, and it belongs in the same discovery task as the `/api/**` consumer
enumeration.

### 3.2 The local login path authenticates twice

Worth recording because it affects reasoning about session-ID rotation:

1. Wizard step 3 posts to `validateUser.do`, which calls `userManager.login()` — and
   `UserManagerImp:138` calls `currentUser.login(token)`, **establishing the Shiro session**.
2. The form then submits to `login.do`, which calls `userManager.login()` **again** with the same
   credentials.

So a normal local login performs two full realm authentications. Any claim about "the session id
before and after login" must be precise about *which* of the two it means.

---

## 4. The Active Directory dependency, in full

`adauth` version **5.7** (`marlo-parent/pom.xml:14`), declared as a dependency by both `marlo-data`
and `marlo-web`.

### 4.1 All 8 direct `new LDAPService()` / `ADConexion` sites

| # | File | Line | Purpose | Auth? | Owner |
|---|---|---|---|---|---|
| 1 | `marlo-data/security/APCustomRealm.java` | `:287` | `getCgiarNickname` — username sync during login | ✅ | `auth-flow` |
| 2 | `marlo-data/security/authentication/LDAPAuthenticator.java` | `:61` | The credential bind | ✅ | `auth-flow` |
| 3 | `marlo-web/action/BaseAction.java` | `:4798` | `getOutlookUser(email)` — shared helper | ❌ | `directory-retirement` |
| 4 | `marlo-web/action/center/capdev/ContactPersonAction.java` | `:86` | Contact-person search (AD filter string) | ❌ | `directory-retirement` |
| 5 | `marlo-web/action/center/json/global/ManageUsersAction.java` | `:249` | User autofill on create | ❌ | `directory-retirement` |
| 6 | `marlo-web/action/json/global/SearchUserAction.java` | `:193` | User search picker | ❌ | `directory-retirement` |
| 7 | `marlo-web/utils/searchUsersUtil.java` | `:14` | Utility lookup | ❌ | `directory-retirement` |
| 8 | `marlo-web/validation/superadmin/GuestUsersValidator.java` | `:37` | Guest-user validation | ❌ | `directory-retirement` |

Plus **3 indirect callers** of `BaseAction.getOutlookUser()`:
`CrpUsersAction:630`, `ManageUsersAction:151`, and `GuestUsersValidator:55` (its own copy).

**2 of 8 sites are authentication. 6 are directory lookup.** That is the ratio that sizes the
remaining work.

### 4.2 Hardcoded AD service credentials — SECURITY FINDING

```java
public static final String GENERICUSER_AD     = "ldapuser";
public static final String GENERICPASSWORD_AD = "ldap2005";
```

Present in **both** constants files:
- `marlo-data/.../config/APConstants.java:646-647`
- `marlo-web/.../config/APConstants.java:706-707`

A service-account password is committed in source, duplicated, and in git history. This is
independent of the Cognito migration — it is true today — but the migration is the natural moment
to remediate it, and `auth-flow`'s **SEC-004** ("no credential literal in any `.java`") is written
in terms that this existing code already violates.

**Rotating this password requires the AD service account to be re-provisioned, which is a CGIAR IT
action with its own lead time.** It should be raised now, not at cutover.

---

## 5. Frontend surface

The login wizard is `loginForm.ftl` (153 lines) driven by `login.js` (802 lines).

Three steps: **email → project → password**, with `crpByEmail.do` called at the end of step 1.
The branch point the migration needs already exists; nothing structural must be invented.

**However — `loginForm.ftl` is included by two pages, not one:**

| Page | Line |
|---|---|
| `WEB-INF/global/views/login/login.ftl` | `:17` |
| `WEB-INF/global/pages/error/401.ftl` | `:19` |

Any change to the wizard's step-3 composition must be verified on **both**. A CGIAR branch added
only to the login page leaves session-expiry re-authentication (the 401 page) on the password path.
`tasks.md` T12 names only `loginForm.ftl` — which is correct as a *file*, but its six manual checks
should be run twice, once per host page.

### 5.1 `crpByEmail.do` cannot express the required data today

`CrpByUserEmailAction.execute()` has two structural problems, both confirmed:

1. **The `user` map is built inside the `for (GlobalUnit crp : crps)` loop** (`:72` opens the loop,
   `:89` builds the map). A user with **zero** Global Units never enters the loop, so `user` stays
   `null`. The response is malformed for exactly the population that most needs a clear message.
2. **It returns no per-unit flag and no `isCgiarUser`.** The wizard therefore has no basis on which
   to branch. This endpoint is the data source for the entire frontend decision, and it currently
   carries none of the required information.

---

## 6. Findings not recorded in the existing spec documents

The `auth-flow` spec is unusually rigorous and its code citations are **exact** — every line
reference checked in this analysis matched the working tree. These are gaps in coverage, not
errors of fact.

| # | Finding | Severity | Why it matters |
|---|---|---|---|
| **F-1** | `ClarisaPublicAccesFilter` is a fourth realm consumer using a config-supplied service account | **High** | If that account is `is_cgiar_user = 1`, public Swagger access breaks at rollout. Cannot be determined from source |
| **F-2** | Hardcoded AD credentials in both `APConstants.java` files | **High** | Pre-existing, but remediation needs CGIAR IT lead time |
| **F-3** | `loginForm.ftl` is included by `401.ftl` as well as `login.ftl` | **Medium** | Session-expiry re-auth would keep the password path for CGIAR users |
| **F-4** | **No mocking framework in the repository** — no Mockito, no PowerMock, no Hamcrest; JUnit 4.13.2 only | **Medium** | The plan calls for ~37 new tests, several of which must assert *"this collaborator was never called"*. Achievable with hand-rolled doubles, but the cost is unpriced |
| **F-5** | TRD §8.1 says `adauth-5.6.jar` is "packaged under `marlo-web/src/main/resources/libs`" | **Low** | Wrong twice: the version is **5.7**, and it is a **Maven dependency** — that directory contains only `org/`, `pentaho/`, `rhino/` |
| **F-6** | TRD §8.4 says `/api/*` authenticates "via tokens (e.g. `QAToken`)" | **Low** | The code says `authcBasic` (`MarloShiroConfiguration:113`). Already noted in task T00 |
| **F-7** | The local login path authenticates **twice** (`validateUser.do`, then `login.do`) | **Low** | Affects how session-rotation claims must be phrased |
| **F-8** | `login.do` declares no named interceptor stack in `struts-home.xml` | **Low** | TRD §4.3 rule 1 nominally requires one; pre-existing drift |

---

## 7. Blast radius summary

### 7.1 By change class

| Class | Files | Notes |
|---|---|---|
| **Must change** — core migration | **14** | See [`touchpoint-inventory.md`](./touchpoint-inventory.md) §1 |
| **Must create** — new code | **~8** | Token type, assertion, validator, 2 actions, tests |
| **Must verify unchanged** — regression surface | **5** | `Authenticator`, `DBAuthenticator`, `LDAPAuthenticator`, `AuthenticationManager`, `MD5Convert` |
| **Affected, deferred to child 2** | **11** | 6 direct AD sites + 3 indirect callers + 2 shared helpers |
| **Newly discovered, currently unscoped** | **2** | `ClarisaPublicAccesFilter`, `401.ftl` |

### 7.2 By module

| Module | Impact | Detail |
|---|---|---|
| `marlo-data` | **High** | The realm, both authenticators, Shiro config, `APConstants` |
| `marlo-web` | **High** | Login action, 2 JSON endpoints, routing, FTL, JS, CSS, i18n, `APConstants` |
| `marlo-utils` | **Low** | `APConfig` getters only |
| `marlo-parent` | **Low** | Dependency declarations |
| `marlo-core` | **None** | — |

### 7.3 What is *not* affected

Worth stating explicitly, because it bounds the regression surface:

- **Authorization is untouched.** Roles, permissions, and Global Unit membership come from
  `users` / `crp_users` / `user_role`. Nothing downstream of authentication knows how identity was
  proven. `doGetAuthorizationInfo()` does not change.
- **No schema change.** Every column needed (`username`, `email`, `is_cgiar_user`, `is_active`,
  `agree_terms`, `last_login`) already exists.
- **The local (MD5) path is untouched** — provided the `instanceof` guard goes *above* the cast at
  `:115` rather than replacing it.
- **Phase replication, the save pipeline, and every domain module** are outside this change
  entirely.

---

## 8. Sequencing implications

Three items must be resolved **before** code, because a wrong answer invalidates work already done:

| Item | Blocks | Why it cannot be deferred |
|---|---|---|
| **OQ-9** — which claim is the stable identifier (`sub` / `oid` / email) | Identity mapping, and every test that joins on it | Mapping on email when the answer is `sub` encodes the wrong join key in ~10 tests |
| **OQ-3** — will CGIAR IT federate MARLO | The entire migration | A "no" returns to the parent proposal; it does not patch the child spec |
| **Clarisa service account flag** (F-1) | Rollout | One SQL query. Cheap now, an outage later |

Two items are pre-existing and independent — they can start immediately and in parallel:

- **F-2** — AD credential rotation (needs CGIAR IT lead time; start the request now).
- **F-4** — decide whether a test-scope mocking dependency is added before test authoring begins.

---

## 9. Assessment

**The spec's technical claims hold.** Every line reference verified against the working tree.
The realm seam is real, the frontend seam is real, and the assertion that authorization does not
participate is correct.

**The scope framing needs one correction.** The `auth-flow` spec delivers *Cognito login*; it does
not deliver *AD independence*. Six directory-search call sites, the `adauth` dependency, and the AD
service account all survive it. If the migration's business case rests on retiring the AD
dependency, that case is closed by **child 2**, not by this spec — and the two should be planned and
communicated as one programme.

**Three surfaces need scoping before rollout**, not before coding: `ClarisaPublicAccesFilter`,
the `/api/**` Basic-auth consumers, and the 401 page's copy of the login wizard.
