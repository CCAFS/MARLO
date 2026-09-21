# AD → Cognito — Touchpoint Inventory

**Analysis ID:** `CHG-COGNITO-AUTH-ANALYSIS-001`
**Companion:** [`impact-analysis.md`](./impact-analysis.md) — the assessment this table supports
**Date:** 2026-08-24 · **Branch:** `staging-cognito` · **Method:** static analysis of the working tree

> Every file that must be touched, verified, or deliberately left alone. Line numbers are exact
> against this checkout and were each confirmed individually. **`Risk`** rates the consequence of
> getting the change wrong, not its difficulty.

---

## 1. Must change — core migration

| # | File | Module | Lines | What is there today | What must change | Risk |
|---|---|---|---|---|---|---|
| 1 | `security/APCustomRealm.java` | data | `:115` cast · `:136-145` branch | Unconditional `(UsernamePasswordToken) token`; `if (isCgiarUser)` → LDAP, else → DB | Insert an `instanceof` guard **above** `:115`. Everything from the cast down stays byte-for-byte | 🔴 **Critical** — this method authenticates every user |
| 2 | `MarloShiroConfiguration.java` | data | `:44-48` | Realm built by hand with 2 authenticators | Wire the token validator into the realm constructor | 🔴 **Critical** — a broken bean stops startup |
| 3 | `action/home/LoginAction.java` | web | `:232-323` | `login(User, GlobalUnit)`; reads `Referer` at `:290` with **no null guard** at `:295` | Extract the tail so a callback can reuse it; add the null guard | 🔴 **Critical** — the local login path runs through it |
| 4 | `action/json/global/CrpByUserEmailAction.java` | web | `:72` loop · `:89` map | `user` map built **inside** the loop → `null` for a user with 0 units. No `isCgiarUser`, no per-unit flag | Move the map out of the loop; add `isCgiarUser` and a per-entry flag | 🟠 High — the wizard's only data source |
| 5 | `action/json/global/ValidateUserAction.java` | web | `:71` | Unauthenticated, accepts a password, calls `userManager.login()` → realm → LDAP | Refuse CGIAR accounts on flag-enabled units, with an unchanged failure shape | 🔴 **Critical** — relays CGIAR passwords to AD |
| 6 | `config/APConstants.java` | **data** | — | — | Add the specificity constant | 🟡 Shared writer — serialize |
| 7 | `config/APConstants.java` | **web** | — | — | Same constant, identical value | 🟡 Shared writer — serialize |
| 8 | `utils/APConfig.java` | utils | 63 `@Value` fields | No Cognito keys | Add getters, **every one with `${key:default}`** | 🟠 High — a missing default breaks startup |
| 9 | `marlo-parent/pom.xml` | parent | `:14` `ciat-adauth.version=5.7` | No AWS SDK, no JOSE library | Add both in `dependencyManagement`. **Downgrade nothing** | 🟠 High — affects all modules |
| 10 | `resources/struts-home.xml` | web | `:27-37` login, `:9-21` `homeJson` | No Cognito routes | Register 2 new actions with a **named** interceptor stack | 🟡 Medium — **T08 and T09 both edit this file; never parallel** |
| 11 | `webapp/WEB-INF/global/pages/loginForm.ftl` | web | 153 lines; step blocks at `:23`, `:36`, `:76` | 3-step wizard, password input at `:94` | Add a CGIAR step-3 block | 🟠 High — **included by 2 pages, see §4** |
| 12 | `webapp/global/js/login/login.js` | web | 802 lines; `:396` `crpByEmail.do`, `:545` `validateUser.do`, `:302` `showPasswordStep()` | Wizard state machine | Compose the branch; **`.remove()`** the password node, never `.hide()` | 🟠 High — a hidden input still submits |
| 13 | `webapp/global/css/customLogin.css` | web | — | Login-specific styles | Reuse `.login-form-button` and the existing palette. **Not `global.css`** | 🟢 Low |
| 14 | `resources/global.properties` | web | — | `login.error.invalidUserCrp` exists **only** in `custom/ciat.properties:88` | Add the missing key + all new messages | 🟡 Shared writer — serialize |

---

## 2. Must create — new files

| # | Proposed file | Module | Purpose |
|---|---|---|---|
| 15 | `security/CognitoAssertion.java` | data | Immutable validated identity |
| 16 | `security/CognitoAuthenticationToken.java` | data | Shiro `AuthenticationToken` wrapper — the type the realm guard dispatches on |
| 17 | `security/CognitoTokenValidator.java` + `impl/` | data | JWKS fetch, cache, full ID-token validation |
| 18 | `action/home/CognitoLoginAction.java` | web | Authorize redirect: state, nonce, PKCE |
| 19 | `action/home/CognitoCallbackAction.java` | web | Code exchange, validation, gates, session rotation |
| 20 | Migration `V…__AddCognitoAuthSpecificity.sql` | web | 3 `parameters` rows; **no `custom_parameters`** |
| 21 | Test classes (~8) | web | ~37 tests — **all new**, see §6 |

Every new `.java` file needs the **GPL header** (`AGENTS.md`).

---

## 3. Must verify UNCHANGED — the regression surface

Their appearance in any diff is a defect. `git diff --stat` is the check.

| File | Module | LOC | Why it must not move |
|---|---|---|---|
| `security/authentication/Authenticator.java` | data | 34 | Signature `authenticate(String, String)` cannot carry a token — extending it changes both implementations |
| `security/authentication/LDAPAuthenticator.java` | data | 92 | The AD bind. Still serves non-migrated units |
| `security/authentication/DBAuthenticator.java` | data | 52 | The local MD5 path — explicitly out of scope |
| `security/authentication/AuthenticationManager.java` | data | — | Untouched by design |
| `utils/MD5Convert.java` | — | — | Local password hashing |
| `users.password` column | data | — | No data migration in this change |

Also unchanged: `APCustomRealm.doGetAuthorizationInfo()` — **authorization does not participate in
this migration at all.**

---

## 4. Newly discovered — currently unscoped

Neither appears in `requirements.md`, `design.md`, or `tasks.md`.

| # | File | Line | Issue | Action needed |
|---|---|---|---|---|
| **F-1** | `web/filter/ClarisaPublicAccesFilter.java` | `:78-85` | Authenticates a **config-supplied service account** through the same realm, to render public Swagger pages. Registered at `WebAppInitializer:126` | **Run one SQL query**: is that account `is_cgiar_user = 1`? If yes, public Swagger breaks at rollout |
| **F-3** | `webapp/WEB-INF/global/pages/error/401.ftl` | `:19` | Includes `loginForm.ftl` — the wizard renders here too | Run T12's six manual checks on **both** host pages, not just `login.ftl:17` |

---

## 5. Deferred to child spec 2 (`directory-retirement`)

**These survive the `auth-flow` spec.** MARLO still depends on `adauth` after it ships.

### 5.1 Direct `new LDAPService()` / `ADConexion` — non-authentication

| # | File | Line | Purpose |
|---|---|---|---|
| 1 | `action/BaseAction.java` | `:4798` | `getOutlookUser(email)` — the shared helper |
| 2 | `action/center/capdev/ContactPersonAction.java` | `:86` | Contact-person search; builds an AD filter string at `:59-70` |
| 3 | `action/center/json/global/ManageUsersAction.java` | `:249` | User autofill on create |
| 4 | `action/json/global/SearchUserAction.java` | `:193` | User search picker |
| 5 | `utils/searchUsersUtil.java` | `:14` | `searchUserByEmail("@cgiar.org")` |
| 6 | `validation/superadmin/GuestUsersValidator.java` | `:37` | Guest-user validation (own copy of `getOutlookUser`) |

### 5.2 Indirect callers of `BaseAction.getOutlookUser()`

| File | Line |
|---|---|
| `action/crp/admin/CrpUsersAction.java` | `:630` |
| `action/json/global/ManageUsersAction.java` | `:151` |
| `validation/superadmin/GuestUsersValidator.java` | `:55` |

### 5.3 Dependency and credentials

| Item | Location |
|---|---|
| `adauth` **5.7** dependency | `marlo-parent/pom.xml:14`, `:301-302`; used by `marlo-data/pom.xml:29`, `marlo-web/pom.xml:77` |
| **Hardcoded AD service credentials** (**F-2**) | `marlo-data/.../APConstants.java:646-647` · `marlo-web/.../APConstants.java:706-707` |

---

## 6. Test-infrastructure reality

| Fact | Value |
|---|---|
| Existing test classes in the whole repo | **3** |
| Test root in `marlo-data`, `marlo-core`, `marlo-utils` | **None** |
| Tests touching authentication today | **0** |
| Frontend / E2E harness | **None** |
| Test framework | JUnit **4.13.2** + `struts2-junit-plugin` |
| **Mocking framework** | ❌ **None** — no Mockito, no PowerMock, no Hamcrest (**F-4**) |

The three existing classes:
`ProjectPartnerTest.java` · `ProjectPageItemTest.java` · `URLShortenerTest.java`

**Implication.** ~37 planned tests must be written with hand-rolled doubles. This is feasible — the
collaborators (`UserManager`, `GlobalUnitManager`, `CrpUserManager`, `CustomParameterManager`) are
interfaces injected via constructor, and the `BaseAction` hooks the login path uses
(`getSession():6014`, `getBaseUrl():2511`, `isVisibleTopGUList():8744`, `setCrpSession():9236`) are
all `public` and non-final, so a test subclass works.

Two places where it gets harder, and where the decision on adding a test dependency should be made:

- **`SecurityUtils.getSubject()` is a static**, reached on the failure branches of both
  `APCustomRealm` and `LoginAction`. Without PowerMock those branches need a different seam.
- Several planned assertions are of the form ***"this collaborator was never called"*** — e.g.
  proving `userManager.login()` did not run, which is what distinguishes "the response looked right"
  from "the password never left MARLO." A hand-rolled spy can record calls, but it must be written
  deliberately for each collaborator.

---

## 7. Shared writers — serialization constraints

Two tasks touching any of these cannot run in parallel, however disjoint their intent
(`.agents/leader.md` → *MARLO Directory Boundaries*).

| Shared file | Collides because |
|---|---|
| `APConstants.java` ×2 | Every specificity adds a constant to **both** |
| `global.properties` | All i18n additions land in one file |
| `struts-home.xml` | Both new actions register here |
| `database/migrations/` | **Ordering** collides, not content |
| `marlo-parent/pom.xml` | Dependency management for all modules |
| `BaseAction.java` | 9,000+ lines, very wide caller set |

**Additional constraint not visible in the file list:** tasks that each run `mvn` contend for the
same `target/` in one checkout. Disjoint source files are necessary but **not sufficient** for
parallel execution — a build running beside another worker measures the contention, not the change.

---

## 8. Counts

| Category | Count |
|---|---|
| Files to modify | **14** |
| Files to create | **~8** |
| Files to verify unchanged | **6** |
| Newly discovered, unscoped | **2** |
| Deferred to child 2 | **11** (6 direct + 3 indirect + 2 dependency/credential sites) |
| **Total files in the blast radius** | **~41** |
| Direct AD call sites in the codebase | **8** (2 auth · 6 directory lookup) |
| Surfaces reaching the realm | **4** (2 documented · 1 as an open risk · 1 undocumented) |
| Core files LOC (the 10 principal ones) | **2,279** |
