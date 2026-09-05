# CGIAR Authentication via Amazon Cognito — Tasks

**Spec ID:** `CHG-COGNITO-AUTH-001`
**Status:** Draft
**Owner:** IBD Team
**Last Updated:** 2026-08-24
**Requirements:** [`requirements.md`](./requirements.md) · **Design:** [`design.md`](./design.md) · **Review:** [`judgment.md`](./judgment.md)
**Budget (design §17):** 14 tasks · ~950 production LOC · ~350 test LOC · 18 review rounds

---

## 1. Execution Context

| Item | Value |
|---|---|
| Java | 17 (`marlo-parent/pom.xml` → `java.version`) |
| Run script | `./scripts/run-marlo-java17.sh` (repo root) → `http://localhost:8080/marlo-web/` |
| Spring profile | `dev` → `marlo-web/src/main/resources/config/marlo-dev.properties` (gitignored; bootstrap from `marlo-test.properties`) |
| Build | `mvn -q install -DskipTests -pl marlo-web -am` |
| Style gate | `mvn -q checkstyle:check` — **hard gate**, not advice |
| Tests | `mvn -q test -pl marlo-web` · single class: `-Dtest=ClassName` |
| Test stack | **JUnit 4.13.2** + Surefire. Not JUnit 5 — it is not on the classpath |
| Branch | `staging-cognito` (from `staging`, merges to `staging`) |
| CodeGraph | Initialized. Prefer `codegraph node/callers/impact` over exploratory reads |

> **Test-suite reality.** `marlo-web/src/test/java/` holds **three** JUnit 4 classes and none touches authentication. There is no test root in `marlo-data`, no frontend runner, no E2E harness. Every test in this plan is **new**. A green `mvn test` before T02 lands proves nothing about this spec.

---

## 2. Pre-flight Checklist

- [ ] `requirements.md` and `design.md` approved by Tech lead + one of PMU/QA lead.
- [x] **OQ-9 answered 2026-09-02** — the stable identifier is the corporate **`email`**, normalized (trim + lowercase). A different corporate email is a **different user**. **T07 unblocked.**
- [x] **OQ-3 answered 2026-09-02 — dissolved.** No new federation is requested: the existing IBD Cognito setup is already integrated with the CGIAR corporate directory. MARLO reuses it via the 7 `cognito.*` environment variables. **T12–T14 unblocked**; the "returns to the parent proposal" branch cannot trigger.
- [ ] `git pull` on `staging`; branch is `staging-cognito`.
- [ ] A Cognito User Pool + app client exists in a dev account. **Superseded in shape by OQ-3's closure:** the pool exists and is not MARLO's to create. What is still needed is an **app client whose callback allowlist includes MARLO's redirect URI**, plus the 7 key values for the target environment — a request to the pool's owner, not a provisioning task in this spec.

---

## 3. Task List

> **How to read the verification fields.** Each task states three things, and all three are load-bearing:
> **Verification** — the command or check. **Fails when** — a concrete input that makes it report failure; if none can be named, the check is not evidence. **Not evidence when** — the condition under which a green result must be reported as *inconclusive* rather than as a pass.

---

### CHG-COGNITO-AUTH-001-T00 — Discovery: enumerate `/api/**` Basic-auth consumers
- **Status:** `[ ]` — **OPEN and explicitly UNRESOLVED as of 2026-09-02.** Not code: an inventory owned by
  IBD, gated on **OQ-4** (*who calls `/api/**` with Basic auth, and are any of them CGIAR users?*). **It
  cannot be closed from this checkout and must not be closed by assumption.** The risk is real and silent:
  federated identities **cannot use Basic auth**, so if any consumer is a CGIAR user in a migrated Global
  Unit, that surface stops authenticating the day the flag is enabled, with no error visible in the login
  flow. A second consumer found during T11b belongs on this list: `ClarisaPublicAccesFilter:79` binds a
  configured service account through the same realm — not a SEC-005 relay, but it breaks silently if that
  account is ever `is_cgiar_user = 1` in a migrated unit. See `execution.md` §27.3.

- **Depends on:** none — **runs first, in parallel with T01**
- **Module:** investigation only, no code
- **Why first:** `MarloShiroConfiguration.java:113` maps `/api/**` → `authcBasic` through the **same realm**. Federated identities cannot use Basic auth, so that surface breaks silently for any CGIAR user who calls it. Risk **R-D4** currently has **no gate**.
- **Scope:**
  - Enumerate `/api/v2/*` consumers (server logs, CGIAR partners, internal jobs, Power BI / Fabric pulls).
  - For each, determine whether it authenticates as a `is_cgiar_user = 1` account.
  - Record the finding in `design.md` §16 R-D4 and open a follow-up spec if any consumer is affected.
  - **Also correct TRD §8.4**, which says `/api/*` authenticates "via tokens (e.g. `QAToken`)" while the code says `authcBasic`. *(Shared-file rule: the TRD edit is recorded as a pending item and applied on `staging`, not on this branch.)*
- **Covers:** OQ-4, R-D4
- **Verification:** a written consumer list with an authentication mode per entry, reviewed by the Tech lead.
- **Fails when:** a consumer is found that authenticates as a CGIAR account — the task then blocks phase 1 rollout until a token strategy exists.
- **Not evidence when:** the list is derived only from code grep. Runtime logs must confirm it; an endpoint with no callers *in code* may still have external callers.
- **Done when:** the list exists, R-D4 is updated with either "no affected consumers" or a named follow-up, and the TRD correction is queued.

---

### CHG-COGNITO-AUTH-001-T01 — Extract `finishLogin` from `LoginAction` (behavior-preserving)

- **Status:** `[x]` — code complete and **audited** 2026-08-31 (independent Reviewer, `sonnet`,
  PASS-WITH-FINDINGS; `execution.md` §4). Compile PASS, 44/44 tests at the time, 0 lines over 120.
  **The last open clause closed at commit `046c31e7dc`**, whose body names the single behavioral change (the
  `returnUrl` null guard) as the *Done when* requires.
- **Depends on:** none — **must land before every Cognito task**
- **Module:** marlo-web
- **Files touched:** `action/home/LoginAction.java` (modify)
- **Scope:**
  - Extract the body of `login(User loggedUser, GlobalUnit loggedCrp)` into
    `protected String finishLogin(User loggedUser, GlobalUnit loggedCrp, String returnUrl)`.
  - `login(User, GlobalUnit)` becomes a two-line caller passing `ServletActionContext.getRequest().getHeader("Referer")`.
  - Add a **null guard** on `returnUrl` before `:295`'s `.contains(".do")`.
  - Do **not** change the `user`-field dereferences at `:268`, `:269`, `:274`, `:277`, `:281`. They stay; T08 makes the field non-null on the Cognito path.
- **Constitutional checks:** Checkstyle (2-space, 120-char, same-line braces); no GPL header needed (existing file).
- **Design refs:** DD-6, §2.2
- **Covers:** R-D1
- **Tests (new):**
  - Unit: `finishLogin` with a `returnUrl` containing `.do` → returns `LOGIN` with `url` set.
  - Unit: `returnUrl` containing `logout` → falls through to type routing.
  - Unit: **`returnUrl == null` → does not throw**, falls through to type routing.
  - Unit: each Global Unit type 1–5 routes as `:299-318` does today.
- **Verification:** `mvn -q test -pl marlo-web -Dtest=LoginActionFinishLoginTest` + `mvn -q checkstyle:check`
- **Fails when:** the null-guard test is run against the pre-extraction code — it must **NPE**. That is the proof the guard is real and not decorative.
- **Not evidence when:** the routing tests were written by reading the new code rather than the old. Write them against `:299-318` **before** extracting; if they pass on both, the extraction preserved behavior.
- **Done when:** all four tests pass, Checkstyle passes, and the diff contains **exactly one** behavioral change (the null guard), explicitly called out in the commit body.
- **Skills:** `tdd`

---

### CHG-COGNITO-AUTH-001-T02 — Specificity: migration + constants

- **Status:** `[x]` — 2026-08-31 on `staging-cognito-impl`, **audited** (independent Reviewer, `sonnet`, verdict
  PASS-WITH-FINDINGS; see `execution.md` §4). Key `cognito_auth_active`; migration applied against a live MySQL
  schema and verified (3 rows, all `false`), then rolled back so Flyway can apply it for real. Compile PASS,
  44/44 tests PASS, Checkstyle 0 violations in **both** `marlo-web` and `marlo-data`.
  **The audit found a real defect in `scripts/verify-specificity-constants.sh`, since fixed** — it checked only
  that the key string appeared *somewhere* in the `VALUES` tuple, so a typo'd `key` whose `description` held the
  correct string passed. It now resolves the `key` column's position from each statement's own column list.
- **Depends on:** none
- **Module:** marlo-web, marlo-data
- **Files touched:**
  - `resources/database/migrations/V<...>__AddCognitoAuthSpecificity.sql` (new)
  - `marlo-web/.../config/APConstants.java` (modify)
  - `marlo-data/.../config/APConstants.java` (modify)
- **Scope:** three `parameters` rows for `global_unit_type_id ∈ (1,3,4)`, `category='2'`, `format='1'`, `default_value='false'`. **No `custom_parameters` seeded.** Constant in both files, value **identical** to `parameters.key`.
- **Constitutional checks:** `AGENTS.md` specificity workflow; Flyway naming `V<major>_<minor>_<patch>_<YYYYMMDD>_<HHMM>__<Description>.sql`; constant in **both** modules.
- **Design refs:** §3, §9
- **Covers:** MIG-001, OQ-11
- **Tests:** manual — apply the migration locally, confirm three rows, confirm the flag reads `false` for every Global Unit.
- **Verification:** `SELECT * FROM parameters WHERE \`key\` = '<key>';` returns exactly 3 rows, all `default_value='false'`.
- **Fails when:** the constant's value is edited to differ from `parameters.key` — a grep comparing the two must then report a mismatch. **Write that grep**; without it the two-file rule is checked by eye.
- **Not evidence when:** verified on a database that already had the rows from a previous run. Verify on a fresh schema or after a targeted delete.
- **Done when:** rows exist, both constants match the key exactly, Checkstyle passes.

---

### CHG-COGNITO-AUTH-001-T03 — Dependencies + configuration that cannot break startup

- **Status:** `[x]` — **closed 2026-09-01 by a live boot** (`execution.md` §17.3): 0 `cognito.*` keys present,
  0 `Dispatcher initialization failed` / `startup failed` / `SEVERE`, Tomcat 9.x embedded up on 8080,
  `GET /marlo-web/` -> 302 and `login.do` -> 200. That boot also **found the defect no gate could see** -- a `--`
  em-dash inside an XML comment in `struts-home.xml` that took the whole context down (§17.1); fixed and covered
  by `StrutsConfigurationWellFormedTest` in commit `f70972a55b`.
  Implementation record, 2026-08-31 on `staging-cognito-impl`: AWS SDK v2 BOM `2.31.30` + `nimbus-jose-jwt 9.48` in
  `dependencyManagement` (both resolution-checked); 7 `@Value` fields all in the `${key:}` form with getters that
  return empty, never `null`; the 7 keys added blank to `marlo-test.properties`. Compile PASS, 47/47 tests PASS,
  Checkstyle 0 violations in `marlo-web`/`marlo-data`/`marlo-utils`, no literal config value in any `.java`.
  **Audited** (independent `sonnet` Reviewer, PASS-WITH-FINDINGS; §6). One finding: a false claim in the test javadoc about resource copying, since corrected. The `aws-serverless` skill was deliberately not loaded — root `CLAUDE.md` says it does not
  apply to MARLO (PS-6).
- **Depends on:** none
- **Module:** marlo-parent, marlo-utils, marlo-web
- **Files touched:**
  - `marlo-parent/pom.xml` (modify) — AWS SDK v2 `cognitoidentityprovider` + a JWT/JOSE library, in `dependencyManagement` with version properties
  - `marlo-utils/.../utils/APConfig.java` (modify) — Cognito getters
  - `marlo-web/src/main/resources/config/marlo-test.properties` (modify) — the tracked bootstrap template
- **Scope:** pool id, region, client id, client-secret reference, domain, callback URL, JWKS URI. **Every new `@Value` uses `${key:default}` with an empty default.**
- **Constitutional checks:** no dependency downgrades (hard rule 11 / NF-006); no credential literal in any `.java` (SEC-004); `marlo-dev.properties` stays gitignored (hard rule 12).
- **Design refs:** §9.3, §2
- **Covers:** SEC-004, NF-006
- **Tests (new):** unit — every Cognito getter returns empty (not null, not an exception) when the property is absent.
- **Verification:** `mvn -q install -DskipTests -pl marlo-web -am` **on a checkout whose `marlo-dev.properties` has none of the new keys**, then start the app and confirm it boots.
- **Fails when:** the `:default` suffix is removed from any new `@Value` — the context must then fail with `BeanCreationException`. Prove it once, deliberately, then restore.
- **Not evidence when:** verified on a machine whose properties file was already updated. **The whole point is the un-updated case** — this is the exact defect that would make design §14's "phase 0 is inert" false.
- **Done when:** the app boots with no Cognito keys present, and `grep -rn 'cognito' --include='*.java'` shows no literal values.
- **Skills:** `aws-serverless`

---

### CHG-COGNITO-AUTH-001-T04 — `CognitoAssertion` + `CognitoAuthenticationToken`

- **Status:** `[x]` — 2026-08-31 on `staging-cognito-impl`. Both classes `final`, all fields `private final`,
  no mutators; GPL headers present. Immutability is asserted **structurally via reflection**, never by
  round-tripping a constructor, and was proven to bite: de-finalizing a field plus adding a setter compiles and
  makes the test fail (`CognitoAssertion.email must be final`), then restored byte-identical.
  9/9 for `-Dtest=CognitoAssertionTest`; 56/56 for the module. Checkstyle 0 violations in all three modules.
  **Audited** (independent `sonnet` Reviewer, PASS-WITH-FINDINGS; `execution.md` §6). **The audit found a real
  defect, since fixed:** `CognitoAssertion` was not `Serializable`, while Shiro's `AuthenticationToken` — which
  `CognitoAuthenticationToken` implements — extends `Serializable`. Latent under today's in-memory session DAO,
  live the moment T06 puts an assertion into Shiro's principal path. Reproduced, then fixed, with a round-trip
  test now standing guard. `usernameClaim` stays optional on purpose (OQ-18 is not MARLO's to decide).
  **REOPENED 2026-08-31 by T06's audit, then re-closed.** `CognitoAuthenticationToken` now carries the
  resolved `users.id` and **requires it in its constructor**; `getPrincipal()` returns that id, not the
  assertion. The original "both accessors return the assertion" design was wrong, and **two audits passed
  over it** because nothing consumed the principal yet — see `execution.md` §8.2. This is not OQ-9: that
  question is which *claim* joins to the `users` row, not what Shiro carries afterwards.
- **Depends on:** T03
- **Module:** marlo-data
- **Files touched:** `security/CognitoAssertion.java` (new), `security/CognitoAuthenticationToken.java` (new)
- **Scope:** an immutable value object carrying the validated identity (claim value, email, username claim, issued-at) and a Shiro `AuthenticationToken` wrapping it. **No I/O, no framework calls.**
- **Constitutional checks:** GPL header on both new files; Checkstyle.
- **Design refs:** DD-5, §2
- **Tests (new):** unit — the assertion is immutable; `getPrincipal()`/`getCredentials()` behave as Shiro expects.
- **Verification:** `mvn -q test -pl marlo-web -Dtest=CognitoAssertionTest` + Checkstyle
- **Fails when:** a setter is added or a field de-finalized — the immutability test must fail.
- **Not evidence when:** the test only asserts getters return what the constructor received. That is a tautology; assert that no mutator exists via reflection.
- **Done when:** tests pass, GPL headers present, Checkstyle passes.

---

### CHG-COGNITO-AUTH-001-T05 — `CognitoTokenValidator` — the security core

- **Status:** `[x]` — 2026-08-31 on `staging-cognito-impl`. Implemented by the `akili-implementer` on `sonnet`
  and **audited on `opus`** — the first task here with both `author != auditor` axes intact (`execution.md` §7,
  verdict PASS-WITH-FINDINGS). Compile PASS, **70/70 tests**, Checkstyle 0 violations in three modules,
  all ten log statements carry only a rejection reason.
  **The audit found that the nine tests never reached the cryptography**: case 2's token carried an unknown
  `kid`, so the trust gate short-circuited before the signature check, and stubbing `hasValidSignature` left
  all nine green. Closed with the canonical forgery test (real `kid`, attacker signature) plus an RSA-to-HMAC
  confusion test; the **narrow** mutation now reddens 2 where it previously reddened 0.
  Also closed: an unbounded JWKS fetch inside a lock (NF-002 — 2 s/2 s/256 KB now), a fabricated `iat`, a
  missing `RS256` allowlist, and blank expectations passing as expectations. **PS-10** remains open.
- **Depends on:** T03, T04
- **Module:** marlo-data
- **Files touched:** `security/CognitoTokenValidator.java` (new), `security/impl/CognitoTokenValidatorImpl.java` (new)
- **Scope:** JWKS fetch + bounded-TTL cache + re-fetch on unknown `kid`; full validation producing a `CognitoAssertion` or a typed rejection.
- **Constitutional checks:** GPL headers; Checkstyle; **no token value in any log statement**.
- **Design refs:** §13.2, DD-5
- **Covers:** **SEC-001 in full** — every clause
- **Tests (new) — one per rejection class, all pure unit, no network:**

  | # | Input | Expect |
  |---|---|---|
  | 1 | Unsigned token | reject |
  | 2 | Signed with a key not in the JWKS | reject |
  | 3 | `exp` in the past beyond leeway | reject |
  | 4 | `aud` = another client id | reject |
  | 5 | `iss` = another issuer | reject |
  | 6 | `nonce` absent, or not the expected value | reject |
  | 7 | **`token_use = "access"`** | reject — Cognito signs ID and access tokens with the same JWKS |
  | 8 | `exp` just inside the leeway | **accept** (R-D7) |
  | 9 | Fully valid | accept, assertion populated |

- **Verification:** `mvn -q test -pl marlo-web -Dtest=CognitoTokenValidatorTest` — all nine.
- **Fails when:** the signature check is stubbed to return `true` — cases 1, 2 and 7 must then fail. **Run that mutation once**; a validation suite that stays green with verification disabled is testing nothing.
- **Not evidence when:** any test reaches the network. A suite that needs connectivity is a suite that gets skipped in CI and rots. Fixtures are static, generated once.
- **Done when:** nine tests pass offline, the deliberate-stub mutation reddens cases 1/2/7, Checkstyle passes.
- **Skills:** `tdd`, `error-handling-patterns`

---

### CHG-COGNITO-AUTH-001-T06 — Realm token-type dispatch

- **Status:** `[x]` — 2026-08-31, after **three audit rounds and two FAIL verdicts** (`execution.md` §8, §9, §10).
  Implemented on `sonnet`, audited on `opus`. Compile PASS, **76/76 tests**, 5/5 for its own suite, 0 lines over
  120 measured directly (**EB-3**: Checkstyle enforces nothing).
  **Two defects, both the same shape — correct code that production never reaches, certified green by tests
  that called the unit directly instead of through the framework:**
  1. The principal was the `CognitoAssertion`, while ~20 unguarded `(Long) getPrincipal()` sites consume it, so
     the dashboard redirect right after login died in `AddUserIdFilter`. **A spec gap** — `design.md` §2.1 and
     T06 both said only what the realm *consumes*, never what it must *produce*. Fixed by an explicit invariant
     in §2.1 and by **reopening T04** so the token carries the resolved `users.id`.
  2. The dispatch was **unreachable**: `AuthenticatingRealm` defaults `authenticationTokenClass` to
     `UsernamePasswordToken`, so `ModularRealmAuthenticator` threw `UnsupportedTokenException` before the guard
     ran. Fixed with an enumerated `supports()` override plus an end-to-end test through the real
     `Subject.login`.
  **The scope line below is drift** (PS-11): T06 declares "wire the validator into the hand-constructed realm",
  but DD-5 makes injection into the realm dead code — the validator is a bean *beside* it. The scope also never
  named the `supports()` requirement that `design.md` §2.1 now carries.
- **Depends on:** T04, T05
- **Module:** marlo-data
- **Files touched:** `security/APCustomRealm.java` (modify), `MarloShiroConfiguration.java` (modify)
- **Scope:** insert an `instanceof CognitoAuthenticationToken` guard **above** the unconditional cast at `:113-115`, returning `SimpleAuthenticationInfo` built from the assertion. Everything from the cast down is **unmodified**. Wire the validator into the hand-constructed realm at `MarloShiroConfiguration.java:44-49`.
- **Constitutional checks:** `Authenticator.java`, `DBAuthenticator.java`, `LDAPAuthenticator.java` **must not appear in the diff** (DD-1).
- **Design refs:** §2.1, DD-1
- **Covers:** defect class **D-2**
- **Tests (new):**
  - Unit: a `UsernamePasswordToken` for a local user produces **byte-identical** `AuthenticationInfo` before and after the change.
  - Unit: a `UsernamePasswordToken` for a CGIAR user still routes to the LDAP branch (flag has no effect inside the realm).
  - Unit: a `CognitoAuthenticationToken` returns info built from the assertion and performs **no** I/O.
- **Verification:** `mvn -q test -pl marlo-web -Dtest=APCustomRealmDispatchTest` + `git diff --stat` shows the three authenticator files untouched.
- **Fails when:** the guard is placed *below* the cast — the Cognito test must then throw `ClassCastException`. This is the whole point of the task; prove the ordering.
- **Not evidence when:** the local-path test was written after the edit. Capture the expected `AuthenticationInfo` from the **pre-change** code and assert against that captured value.
- **Done when:** three tests pass, the three authenticator files are absent from the diff, Checkstyle passes.

---

### CHG-COGNITO-AUTH-001-T07 — Identity mapping: claim → `users` row, with four gates
- **Status:** `[x]` — 2026-09-02, after an audit **FAIL** and one rework round (`execution.md` §19).
  Implemented on `sonnet`, audited on `opus`. **110/110 tests on a clean run** (EB-4); gate-2 mutation cycle
  **re-measured by the Leader**, not accepted from a report. Four blocking findings fixed, **one of which was a
  defect in this spec's own Constitutional check**, corrected above. **Two obligations carried to T09:** prove
  FN-006's username write reaches the row against a real schema (nothing calls the mapper yet), and enforce
  SEC-006 at the rendering site — T07 enables indistinguishable refusals but cannot enforce them.

- **Depends on:** T05 · **UNBLOCKED 2026-09-02 — OQ-9 closed: the join key is the corporate `email`, normalized (trim + lowercase)**
- **Module:** marlo-data
- **Files touched:** mapping logic in `security/impl/CognitoTokenValidatorImpl.java` or a small collaborator (implementer's call, stated in the report)
- **Scope:** resolve the assertion to a `users` row **by the `email` claim, normalized with trim + lowercase (OQ-9)**, and apply, **in order**: (1) row exists — no auto-provisioning; (2) **`is_cgiar_user = 1`**; (3) `is_active`; (4) *(membership is gate 4 and lives in `finishLogin`)*. Also set `users.username` from the CGIAR login claim, lowercased.
- **Scope extension — approved by the user 2026-09-02, and justified rather than assumed.** `UserMySQLDAO.getUser(String)` (`:93-101`) is the lookup this task now depends on, and it violates the identity contract OQ-9 just defined in two ways. Both are fixed **inside T07**:
  1. **Normalize with `trim()` + lowercase before lookup.** The current code lowercases but does not trim, so a claim arriving with surrounding whitespace silently fails to resolve a row that exists — an authentication failure for a valid user. Verified in the dev database: **0 stored emails have surrounding whitespace**, so trimming the *input* strictly widens matching and cannot change which row matches an already-clean value.
  2. **Replace the concatenated native SQL with a parameterized query.** The value interpolated there is, after this task, a **token claim** rather than a form field. "Trusted because the token is signed" is the reasoning that fails the day the pool accepts an identity MARLO did not anticipate.
  **Why not deferred to `staging` as PS-17 originally was:** the sink is pre-existing and already reachable unauthenticated through `validateUser.do`, so this task does not create it — but the user's instruction is explicit that the new authentication path must not ship while relying on it. Recorded as a justified scope extension, not silent scope creep.
  **Preserve the return path.** `getUser(String)` currently resolves an id and then returns `this.getUser(Long)`, which is `super.find(User.class, id)`. Keep that two-step shape — parameterize and normalize the *lookup* only. Changing the return to a directly-queried entity is a behavioral change to a method with callers outside this spec, and it is not what was authorized.
  **Blast radius, measured:** `getUser(String)` has exactly **one** caller, `UserManagerImp:104`. Verified in the dev database: **0 duplicate emails** under `LOWER(TRIM(email))`, so normalization introduces no identity collision.
- **Constitutional checks:** **`users.email` is never overwritten.**
  - **Username write — CORRECTED 2026-09-02 after the T07 audit.** This clause previously read *"username write
    goes through `userManager.saveUser()` so the audit listener fires"*. **Both halves of that were false, and the
    implementer complied with it faithfully — the defect was the spec's.** (1) `saveUser` → `UserMySQLDAO.saveUser`
    → `AbstractMarloDAO.update(T)` **returns before `merge()`** when `session.contains(entity)` is true, and the
    `User` here came from `session.get`, so it is managed: the call is a **no-op on this entity**. (2)
    `HibernateAuditLogListener` is a `PostUpdateEventListener` / `FlushEventListener` — it fires on **flush**, not
    on `saveUser()`, and the plain `update(T)` overload never calls `addAuditLogFieldsToThreadStorage` at all.
  - **Constraint:** write through `userManager.saveLastLogin(...)`, **not** `saveUser(...)` — identical to T09's
    constraint (below), which was already correct because it was derived from T08's audit. `UserMySQLDAO.saveLastLogin`
    carries `@Transactional`; `saveUser` does not, and this checkout's OSIV session is `FlushMode.MANUAL`, so without
    a transaction the change stays in memory.
  - **Not evidence when:** a call-recording double asserts `saveUser` was invoked. That proves a call, not a write,
    and this spec has already shipped two defects of that shape. **Prove the row changed against a real schema, as
    T02 did** — or, if no schema-backed harness is reachable, record the gap explicitly rather than discharging the
    clause with a presence assertion.
  - **Why this matters beyond tidiness:** as written, FN-006 was satisfied only by an accidental coupling to a
    `@Transactional` call in a *different* class three steps later in §13.3 (`LoginAction`'s `saveLastLogin`).
    Nothing in T07 tested, stated, or protected that coupling. Move that call and the username write silently
    disappears.
- **Design refs:** §13.1, FN-002, FN-006
- **Covers:** FN-002 (all three scenarios), FN-006, **SEC-006**
- **Tests (new):**
  - Unit: unknown claim → refused, **and no `users` row is created** (assert the row count is unchanged — FN-002's `MUST NOT auto-provision`).
  - Unit: **`is_cgiar_user = 0` with a matching CGIAR email → refused on gate 2** (SEC-006). Assert the refusal reason is gate 2, not membership.
  - Unit: `is_active = false` → refused with `USER_DISABLED`.
  - Unit: valid CGIAR user → `users.username` set lowercased; **`users.email` unchanged**.
  - Unit: the refusal message for gate 2 is **indistinguishable** from the generic failure (SEC-006's `MUST NOT` reveal).
- **Verification:** `mvn -q test -pl marlo-web -Dtest=CognitoIdentityMappingTest`
- **Fails when:** gate 2 is removed. **Clarified 2026-09-02 — the original wording was ambiguous at the exact point where the task is decided.** It said *"watch it go green… that red-to-green-to-red cycle"*, but what goes green is the **account that should not get in**, not the test: the test asserts *refusal*, so removing the gate makes it **red**. Both the implementer and the Leader had to reason past that phrasing. The cycle to run and report is: **mutate → the guard test goes RED → restore → it goes GREEN again.** A mutation that reddens nothing means the test was never load-bearing; a mutation that reddens *everything* means it was too broad to prove anything about this gate specifically.
- **Verified 2026-09-02 by the Leader:** mutated → `Tests run: 7, Failures: 1`, `nonCgiarAccountIsRefusedOnGateTwoNotMembership` fails with `AssertionError: a local account must not be unlocked by a federated identity`, **and only that one of the seven**; restored → `Tests run: 7, Failures: 0`, file confirmed byte-identical with `diff`. Mutation proven applied by occurrence count before the result was believed.
- **Not evidence when:** the join is written case- or whitespace-sensitively. OQ-9 resolved to `email` **normalized**, so a test that only ever passes an already-lowercase, already-trimmed claim proves nothing about the normalization the decision requires. At least one test MUST pass a claim with different casing and surrounding whitespace and still resolve the same row.
- **Done when:** five tests pass, the gate-2 mutation demonstrably opens the bypass, Checkstyle passes.
- **Skills:** `tdd`

---

### CHG-COGNITO-AUTH-001-T08 — `CognitoLoginAction` — authorize redirect

- **Status:** `[x]` — 2026-08-31, after **two audit rounds** (`execution.md` §11 FAIL, §12 PASS-WITH-FINDINGS).
  Implemented on `sonnet`, audited on `opus`. **85/85 tests on a clean run** (see **EB-4** for why that
  qualifier is load-bearing), 0 lines over 120 measured directly.
  **The four blocking findings were live exploits, not latent defects** — this is the first task here to put an
  internet-reachable endpoint in the tree: an unauthenticated GET could revoke a third party's `agree_terms`;
  another could redirect a freshly-authenticated victim off-site; the state store was the unbounded keyed map
  DD-4 rejected; and the persistence path chosen is the one `UserMySQLDAO` documents as not persisting.
  Repairs required amending **`design.md` §1, §2, §5.4, §8, §13.1** and **T09's scope**.
  **The implementer correctly refused this task's own Scope line:** `unloggedStack` would have made the action
  permanently 404 — see the `cognitoUnloggedStack` correction above and in design §8.
- **Depends on:** T02, T03, T06
- **Module:** marlo-web
- **Files touched:** `action/home/CognitoLoginAction.java` (new), `resources/struts-home.xml` (modify)
- **Scope:** server-side re-check of `is_cgiar_user` **and** the Global Unit's flag; **require** terms acceptance (the write moved to T09 — §5.4); mint `state`/`nonce`/PKCE verifier; bind `{state, globalUnitId, returnUrl, nonce, verifier}` to the Shiro session under a **fixed** key; 302 to `/oauth2/authorize`. Register with **`cognitoUnloggedStack`** — see design §8; `unloggedStack` would make this action permanently unreachable.
- **Constitutional checks:** GPL header; **named interceptor stack** (TRD §4.3 rule 1); no new `*.json` path; i18n keys for every message.
- **Design refs:** §4, §5.4, §8, §9.2, DD-4
- **Covers:** FN-002 (initiation), **SEC-002**, §5.4 terms
- **Tests (new):**
  - Unit: a request naming a Global Unit whose flag is **off** → refused. *(The client's `cognitoEnabled` is a rendering hint, never an authorization input.)*
  - Unit: a request for an `is_cgiar_user = 0` account → refused.
  - Unit: `state`, `nonce`, and verifier are each **unguessable** and differ across two invocations.
  - Unit: the authorize URL carries `state`, `nonce`, `code_challenge`, `code_challenge_method=S256`.
  - Unit: a request that does not accept the terms is refused, and **nothing is written** to the user's row. *(Replaced 2026-08-31: this bullet read "terms acceptance is persisted before the redirect", which the audit proved was an unauthenticated write letting anyone revoke a third party's record — see §5.4's amendment.)*
- **Verification:** `mvn -q test -pl marlo-web -Dtest=CognitoLoginActionTest` + Checkstyle
- **Fails when:** the server-side flag re-check is removed — the first test must then succeed in producing a redirect for a disabled unit.
- **Not evidence when:** randomness is asserted by "the two values differ". Two calls to a broken generator can differ by luck. Assert the length and character space, and that 100 invocations produce 100 distinct values.
- **Done when:** the six tests above pass (bullet 5 was replaced, not dropped — see its note), **`cognitoUnloggedStack`** is declared in `struts-home.xml` and `CognitoUnloggedStackReachabilityTest` proves why, Checkstyle passes.
- **Skills:** `aws-serverless`, `error-handling-patterns`

---

### CHG-COGNITO-AUTH-001-T09 — `CognitoCallbackAction` — validate, gate, rotate, log in
- **Status:** `[x]` — 2026-09-02, after an audit **FAIL** carrying a **CRITICAL** finding, and one rework round
  (`execution.md` §20). Implemented on `sonnet`, audited on `opus`. **122/122 tests on a clean run** (EB-4);
  the session-invalidation mutation **re-measured by the Leader**, not accepted from a report. **The audit
  caught a defect that would have made every successful Cognito login return HTTP 500 in production**, which
  ten green tests could not see because they substituted a `HashMap` for the session. One audit finding was
  itself **false** and was refuted by the implementer with evidence — see §20.3.

- **Depends on:** T01, T05, T06, T07, T08
- **Module:** marlo-web
- **Files touched:** `action/home/CognitoCallbackAction.java` (new, `extends LoginAction`), `resources/struts-home.xml` (modify)
- **Scope:** the eight-step ordering in design §13.3 — consume-and-delete state/nonce/verifier/globalUnitId/returnUrl → exchange code → validate → map + gates → capture locals → `session.stop()` → `Subject.login(CognitoAuthenticationToken)` → `finishLogin(user, crp, returnUrl)`. Populate the inherited `user` field with a **detached** `User` carrying only the email. Register with **`cognitoUnloggedStack`** — `unloggedStack` would make the callback permanently 404, see design §8.
- **Constitutional checks:** GPL header; named stack; i18n keys; the `input` result must expose the same model surface as `login` (`model=action`, `crpSession`, `listGlobalUnitTypes`).
- **Design refs:** §1 ⑧, §13.3, DD-6, DD-9
- **Covers:** FN-002, FN-003, FN-004, FN-005, **SEC-003**, defect classes **D-3**, **D-8**
- **ALSO OWNS, added 2026-08-31 after T08's audit: persisting `users.agree_terms`.** §5.4 was amended so the
  write happens here, not in `CognitoLoginAction` — that endpoint is unauthenticated and its `email` unverified,
  so writing there let anyone set or revoke a third party's compliance record. **This obligation existed in no
  task until now**: `requirements.md` never mentions terms at all, and its only enforcement was T08's deleted
  write, so T09 would have shipped without it and nothing would have failed.
  - **Constraint:** write through `userManager.saveLastLogin(...)`, **not** `saveUser(...)`.
    `UserMySQLDAO.saveLastLogin` carries `@Transactional` with a comment stating that without it "the merge
    stays in memory and `last_login` / `agree_terms` are never persisted"; `saveUser` has no such annotation,
    and `ValidateUserAction` — the local path's writer of this same column — uses `saveLastLogin` for exactly
    that reason.
  - **Evidence:** prove the row changed against a real schema, as T02 did. A call-recording double cannot tell
    you whether anything was written, and this spec has already shipped two defects of that shape.
- **Tests (new):**
  - Unit + real-schema: after a successful callback, `users.agree_terms` reflects the acceptance carried
    through state — written via `saveLastLogin`, verified by reading the row back, not by asserting a call.
  - Integration: valid round trip → session scoped to the Global Unit **issued at `cognitoLogin.do`**, not one supplied on return (FN-003's `MUST NOT` trust the returned value).
  - Integration: a tampered `globalUnitId` on the callback URL is **ignored** — the session-bound value wins.
  - Integration: **session id before ≠ after** (SEC-003 / D-8).
  - Integration: replaying a consumed `state` → refused on the missing entry.
  - Integration: user not in `crp_users` → `login.error.invalidUserCrp`, session cleared, **no NPE** (this is C-3's failure path).
  - Integration: deep link — a `.do` return URL carried in state lands there; a null one falls through to the dashboard.
  - Integration: `Referer` absent entirely → **no 500** (C-4).
  - Integration (**NF-002 — isolation**): with the Cognito domain and JWKS URI pointed at an unreachable host, a **local** user's `login.do` still succeeds end to end. The CGIAR path fails with the service-unavailable message (FN-005); the local path is untouched.
- **Verification:** `mvn -q test -pl marlo-web -Dtest=CognitoCallbackActionTest`
- **Fails when:** `session.stop()` is moved before step ① — the state-consumption tests must then fail with a missing entry. The ordering is the design; prove it is load-bearing.
- **Not evidence when:** the tests use a mock that returns a pre-built `CognitoAssertion`, skipping T05's validator. At least one test must run the real validator end to end, or SEC-001 and SEC-003 are verified in isolation and never together.
- **Done when:** **nine** tests pass (corrected 2026-09-02 — the clause said "eight" while the *Tests (new)* list above carries nine entries; `§5 Testing Plan`'s "~8" is stale the same way. Flagged by the implementer, confirmed by the auditor), Checkstyle passes, and the ordering mutation reddens the expected tests. **Delivered: 12 tests** — the nine listed, the SEC-006 rendering test that discharges T07's carried obligation, and two session-invalidation regression tests added after the audit.
- **Skills:** `tdd`, `error-handling-patterns`

---

### CHG-COGNITO-AUTH-001-T10 — `crpByEmail.do`: per-unit flag + two structural fixes

- **Status:** `[x]` — 2026-08-31, after **two audit rounds** (`execution.md` §13 FAIL, §14 PASS-WITH-FINDINGS).
  Implemented on `sonnet`, audited on `opus`. **90/90 tests on a clean run**, 0 lines over 120 measured directly.
  **PS-16 discharged:** `CognitoAuthSpecificity` is now the only reading of `cognito_auth_active` in production.
  **The audit found the catalog query could never execute** — HQL filtering on a column, not a mapped property —
  which with T02 seeding no `custom_parameters` would have emptied `crps[]` for every user and rendered as
  "email not found". Repaired in `ParameterMySQLDAO`, outside this task's declared file set, with the
  consequence for `CrpAdminManagmentAction` recorded as **PS-19**. Three resolver mutations now redden; two of
  them, including **MIG-001's rollback**, previously survived the whole suite green.
- **Depends on:** T02
- **Module:** marlo-web
- **Files touched:** `action/json/global/CrpByUserEmailAction.java` (modify)
- **Scope:**
  - Add `cognitoEnabled` to **each `crps[]` entry** and `isCgiarUser` to the `user` map.
  - **Move the `user` map construction out of the `for (GlobalUnit crp : crps)` loop** (`:89`) — today a user with zero units returns `user == null`.
  - Inject `CustomParameterManager` (constructor signature change).
- **Constitutional checks:** Checkstyle; no new `*.json` path (this action already exists).
- **Design refs:** §4
- **Covers:** FN-001 (all three scenarios), MIG-001 "both paths coexist"
- **Tests (new):**
  - Unit: a user in two units with **different** flag states gets **different** `cognitoEnabled` per entry. *(This is the C-1 defect; without it MIG-001 is unimplementable.)*
  - Unit: a user with **zero** Global Units returns a well-formed response, not `user == null`.
  - Unit: an **unknown email** returns neither `isCgiarUser` nor `crps[]` — FN-001's third scenario forbids disclosing which path it would have used.
- **Verification:** `mvn -q test -pl marlo-web -Dtest=CrpByUserEmailActionTest` + Checkstyle
- **Fails when:** `cognitoEnabled` is hoisted to a single scalar on `user` — the mixed-flags test must then be unable to express two values.
- **Not evidence when:** the mixed-flags test uses two units with the **same** flag state. It would pass under the broken scalar design and prove nothing.
- **Done when:** three tests pass, the `user` map is built outside the loop, Checkstyle passes.

---

### CHG-COGNITO-AUTH-001-T11 — Harden `validateUser.do` against CGIAR credential relay

- **Status:** `[x]` — 2026-08-31, after **two audit rounds** (`execution.md` §15 FAIL, §16 PASS-WITH-FINDINGS).
  **97/97 tests on a clean run**, compile EXIT=0, 0 lines over 120 measured directly (EB-3).
  **The FAIL was a bypass in the Leader's own correction:** an optional `globalUnitId` that matched no
  membership fell out of the loop returning "allow", so `&globalUnitId=99999` switched the guard off and
  relayed a migrated password to AD. A caller-supplied id may now only narrow to a unit the account holds.
  Also closed: an unauthenticated NPE→500 on a missing `userEmail`, and two tests that could not fail.
  **SEC-005 is still not absolute** — `login.do` relays through the same realm (**PS-21**).
- **Depends on:** T02, T10
- **Module:** marlo-web
- **Files touched:** `action/json/global/ValidateUserAction.java` (modify)
- **Scope:** refuse to authenticate an `is_cgiar_user = 1` account whose selected Global Unit has the flag enabled, returning the **same generic failure shape** as any other rejection.
- **Why this task exists:** the endpoint is unauthenticated (`homeJson`, no `requireUser`), accepts a password, and calls `userManager.login()` → realm → LDAP. Left alone it keeps relaying CGIAR passwords to AD for migrated units — **SEC-005 would be violated by a path the design originally did not mention** (Judgment Day V-2).
- **Constitutional checks:** Checkstyle; the existing POST-only guard preserved.
- **Design refs:** §5.3
- **Covers:** **SEC-005** (its new scenario)
- **Tests (new):**
  - Unit: `is_cgiar_user = 1` + flag on → refused, **and `userManager.login()` is never called** (assert on the collaborator, not just the response).
  - Unit: `is_cgiar_user = 1` + flag **off** → unchanged behavior, still authenticates via LDAP.
  - Unit: `is_cgiar_user = 0` → **unchanged in every case** (SEC-005's `BUT MUST NOT` clause).
  - Unit: the refusal is byte-identical to a wrong-password refusal (no new oracle).
- **Verification:** `mvn -q test -pl marlo-web -Dtest=ValidateUserActionGuardTest`
- **Fails when:** the guard checks only `is_cgiar_user` and ignores the flag — test 2 must then fail, because a non-migrated CGIAR user would be locked out.
- **Not evidence when:** the first test asserts only the HTTP response. A response can be shaped correctly while the LDAP bind still happened — **the password would already have left MARLO.** Assert the call never occurred.
- **Done when:** four tests pass, Checkstyle passes.

---


### CHG-COGNITO-AUTH-001-T11b — Harden `login.do` against CGIAR credential relay

- **Status:** `[x]` — 2026-09-02, audited **PASS-WITH-FINDINGS** (`execution.md` §22). **128/128 clean**; mutation re-measured by the Leader, reddening exactly the four refusal tests and leaving the two must-still-authenticate tests green. **SEC-005 is now true for both endpoints.** The audit found an error in the Leader own enumeration (§22.3) and a missing design amendment, both since fixed. Originally added 2026-09-02, approved by the user. T11 closed the AD credential relay on
  `validateUser.do` and left the *same relay* open on `login.do`, which is also unauthenticated. Recorded as
  **PS-21** at the time, with the note that it "needs a design amendment plus a named task **before T12 ships a
  UI that assumes the server is protected**". This is that task. Verified still open 2026-09-02:
  `LoginAction.login():187` calls `userManager.login(userEmail, user.getPassword())`, and `LoginAction` contains
  **zero** references to `CognitoAuthSpecificity` while `ValidateUserAction` contains four.
- **Depends on:** T02, T10, T11
- **Module:** marlo-web
- **Files touched:** `action/home/LoginAction.java` (modify)
- **Scope:** in `login()` — **not** in `finishLogin()`, which runs after authentication and is shared with the
  Cognito path — refuse to authenticate an `is_cgiar_user = 1` account whose selected Global Unit has the flag
  enabled, **before** `userManager.login()` is reached. Return the **same generic failure** a wrong password
  returns. Resolve the flag through the shared `CognitoAuthSpecificity` resolver (PS-16), never a re-implementation.
- **Why this task exists:** `SEC-005` says **"any MARLO endpoint"**. After T11 it is still false. `design.md` §5.3
  itself describes the local flow as **two** requests and hardened only one. T12's protection for this one is
  **client-side only** — it removes the password input from the DOM — so the endpoint stays postable with `curl`.
  A UI that assumes the server is protected must not ship before the server is protected.
- **Constitutional checks:** Checkstyle; i18n keys for any message; **`finishLogin` is not touched** — it is T01's
  extracted shared tail and the Cognito path depends on it.
- **Design refs:** §5.3 · **Covers:** **SEC-005** (its remaining endpoint)
- **Tests (new):**
  - Unit: `is_cgiar_user = 1` + flag on → refused, **and `userManager.login()` is never called.** Assert on the
    collaborator: make the double **throw** if `login()` is invoked, so a fall-through fails loudly instead of
    passing on a well-shaped response.
  - Unit: `is_cgiar_user = 1` + flag **off** → unchanged, still authenticates via LDAP.
  - Unit: `is_cgiar_user = 0` → **unchanged in every case** (SEC-005's `BUT MUST NOT` clause).
  - Unit: the refusal is indistinguishable from a wrong-password refusal — no new oracle.
  - Unit: **the mixed-membership case MIG-001 forbids.** A CGIAR user in a migrated unit *X* and a non-migrated
    unit *Y*, selecting *Y*, **must still authenticate via LDAP.** T11 FAILed its first audit on the inverse of
    this, and its correction then FAILed on a bypass — read `execution.md` §15 before writing this one.
- **Verification:** `mvn -q test -pl marlo-web -Dtest=LoginActionCgiarGuardTest`
- **Fails when:** the guard is placed **after** `userManager.login()` — test 1 must then fail, because the LDAP
  bind already happened. Prove the ordering, not just the response.
- **Not evidence when:** a test asserts only the returned result string. A response can be shaped correctly while
  the password has already left MARLO. **Assert the call never occurred.**
- **Also required — the Leader must verify, not assume:** whether `login.do` reaches the relay by a second path.
  T11's audit found the guard placed on "a correct statement guarding the wrong door". Enumerate every call into
  `userManager.login(...)` on this action before declaring the endpoint closed.
- **Done when:** five tests pass, the ordering mutation reddens test 1, Checkstyle passes, and `finishLogin` is
  absent from the diff.
- **Skills:** `tdd`, `error-handling-patterns`

---
### CHG-COGNITO-AUTH-001-T12 — Login wizard: mode composition + DOM removal
- **Status:** `[x]` — 2026-09-02. Code complete after **two audit FAIL rounds** (`execution.md` §23), and
  **all seven manual checks executed and passed** (§24). **128/128 Java tests clean** — which proves only that
  the backend was not broken; zero of them see the DOM. Checks 1, 2 and 6 were run by the user in a browser;
  checks 3, 4, 5 and 7 by the Leader through Chrome DevTools Protocol with **no dependency added to MARLO**.
  **Manual validation found a defect no code review could see:** T12 changed `login.js` and `customLogin.css`
  without bumping their cache-busting tokens, so every returning user would have silently kept the pre-T12
  assets and the Cognito branch would never have appeared for them (§24.2).
  **One named residual, deliberately NOT claimed as tested:** check 5 is proven only through the Chrome
  accessibility tree — role, accessible name, focusability, exposure to assistive technology. **No real screen
  reader (NVDA, JAWS, Narrator) was executed** (§24.6).
- **Depends on:** T08, T10
- **Module:** marlo-web
- **Files touched:**
  - `webapp/WEB-INF/global/pages/loginForm.ftl` (modify) — add `#login-step-cgiar`
  - `webapp/global/js/login/login.js` (modify)
  - `webapp/global/css/customLogin.css` (modify) — **not `global.css`**
- **Scope:** compose `mode = user.isCgiarUser && card.cognitoEnabled` at project selection (including the auto-skip path when `crps.length == 1`); show the matching step-3 block; on the COGNITO branch **`.remove()` `#login-password`**; the CGIAR control is an `<a>`/`<button type="button">` to `cognitoLogin.do`, inside `.login-button-container`; carry the terms checkbox into the CGIAR block.
- **Constitutional checks:** reuse `.login-form-button` and the existing palette (`docs/ux-ui/design.md` §7); **light theme only** (§11); keyboard-reachable with an accessible name (§10); English source strings are i18n keys.
- **Design refs:** §5
- **Covers:** FN-001 (all clauses), NF-003, NF-004
- **Tests:** **no automated frontend harness exists** — see the gap note below.
- **Verification (manual, at the HITL pause):**
  1. ~~Local user → step 3 is today's password step, pixel-identical.~~ **Superseded by T18** (added
     2026-09-04): T18 puts an *External user* label on `#login-step-password`, so step 3 is deliberately no
     longer pixel-identical to this pre-Cognito baseline. Re-stated by T18 check 1: the label is visible and
     the password field, eye icon and Log in button all behave exactly as before.
  2. CGIAR user, flag on → password input is **absent from the DOM** (`document.querySelector('#login-password')` returns `null`) and absent from `new FormData(form)`. **Re-run this one a second time after visiting a LOCAL step**, so the restore template exists: an edit that inserted the template eagerly would pass a first-visit check and fail only here.
  3. ~~CGIAR user, flag **off** → password step, as today.~~ **Superseded by T18** (added 2026-09-04): re-stated
     by T18 check 3 — password step **with** the *External user* label.
  4. Keyboard-only: tab to the control, activate with Enter and with Space.
  5. Screen reader announces the control with a meaningful name.
  6. Single-Global-Unit user (step 2 auto-skipped) → correct step 3.
  7. **Back-navigation — added 2026-09-02 after the T12 audit, and it is four steps for a reason.** CGIAR user, migrated unit → step 3 → **Go back** → select a **non-migrated** unit → then, in order:
     1. the password field is **present** and accepts typing;
     2. type a **deliberately wrong** password and press "Log in" — the message must be *incorrect password*, **not** *password is required*. This is the step that distinguishes a live `inputPassword` from the stale detached node; **check 7 without it would pass with the reassignment deleted**;
     3. click the **eye icon** and confirm the field toggles to plain text — the only step that catches a `.clone()` that lost its `true`;
     4. press **Enter** in the field and confirm it submits.
     > **Why this check exists.** The original six checks contain **no back-navigation**, so all six would have passed while the LOCAL branch was permanently dead after any COGNITO visit — the blocking defect the T12 audit found. The four sub-steps exist because the first one alone is **not load-bearing**: the field renders and accepts typing even when the form is dead, and the failure appears only on submit.
- **Fails when:** `.remove()` is swapped for `.hide()` — check 2 must then find the node and find it in the FormData. **Run that swap once deliberately**; it is the exact defect Judgment Day found in revision 1.
- **Not evidence when:** verified only by reading the JS. FN-001's clause is about the **DOM at submit time**, and a code read cannot see it. Run it in a browser.
- **Done when:** all **seven** manual checks pass (corrected 2026-09-02 — check 7 was added because the original six could not see the blocking defect the audit found) and are recorded with screenshots in `execution.md`.
- **Skills:** `ui-ux-pro-max`

> **Accepted gap (defect class D-5).** This repository has no frontend test runner and no E2E harness, so checks 1–6 have **no automated gate**. Substitutes: the manual walkthrough above, plus a **T6 multimodal review** of the screenshots. Recorded as an accepted risk, not as coverage. Standing up a JS test runner is a TRD stack decision, not an inner-loop improvisation.

---

### CHG-COGNITO-AUTH-001-T13 — i18n keys, including one that is missing today
- **Status:** `[x]` — 2026-09-02, audited **PASS-WITH-FINDINGS** (`execution.md` §25), three durability
  findings fixed, plus a **user-approved scope extension**. **129/129 clean**; both mutations re-measured by
  the Leader. The defect was wider than this task described — `invalidUserCrp` reaches users on **both** the
  local and the Cognito paths — and the extension closed the sibling defect `login.error.userOrPass`, whose
  blast radius is larger still.

- **Scope extension — approved by the user 2026-09-02.** Also add **`login.error.userOrPass`** to
  `global.properties`, and **remove the `finishLogin`-only restriction** so the test scans the complete
  `LoginAction`.
  **Why:** it is the *same defect class* this task exists to close — referenced from `LoginAction:285`,
  defined **only** in `custom/ciat.properties:86`, so every non-CIAT Global Unit renders the raw key. Its
  blast radius is **larger** than `invalidUserCrp`'s: it fires on a wrong password, the single most frequent
  login failure in the product, while `invalidUserCrp` fires only on a wrong-unit selection.
  **Not a security issue, verified rather than assumed:** T11b's guard renders
  `ADLoginMessages.ERROR_LOGON_FAILURE`, the same value the AD-failure branch renders, and the guard only
  fires for `is_cgiar_user = 1` accounts, which always fail through that branch. No oracle is created.
  **The original narrowing was legitimate, not evasion** — the T13 audit established that
  `CognitoCallbackAction` calls `finishLogin(...)` directly and never enters `login()`, so `LoginAction:285`
  is unreachable from any Cognito path and excluding it hid **zero** FN-005 keys. The extension widens the
  test beyond the Cognito flow deliberately, because the user chose to close the defect class rather than
  only the instances this spec touches.
  **Hard boundary, stated by the user:** this is **not** permission to fix other missing i18n keys. If the
  full scan finds anything beyond `userOrPass`, it must be **reported, not fixed** — and the test left red
  rather than narrowed back to hide it. **Leader pre-check, 2026-09-02:** `LoginAction` references five
  literal keys (`duplicated`, `invalidUserCrp`, `selectCrp`, `userOrPass`, `validation.field.required`) and
  **only `userOrPass` is absent**, so the widened scan is expected green with no further decision pending.
- **Depends on:** T08, T09, T12
- **Module:** marlo-web
- **Files touched:** `resources/global.properties` (modify)
- **Scope:** keys for the CGIAR control, every FN-005 failure message, and **`login.error.invalidUserCrp`** — which FN-002 calls "the existing message" but which exists **only** in `custom/ciat.properties:88`, so every non-CIAT Global Unit renders the raw key today.
- **Constitutional checks:** hard rule 8 — no hardcoded user-facing strings. `ADLoginMessages`'s literal-string values are **left untouched** (DD-7).
- **Design refs:** §9.4, DD-7
- **Covers:** FN-005
- **Tests (new):** unit — every key referenced by the new actions resolves in `global.properties`.
- **Verification:** `mvn -q test -pl marlo-web -Dtest=CognitoI18nKeysTest`
- **Fails when:** a key is referenced in Java but absent from `global.properties` — the test must name the missing key. Remove one deliberately to confirm the test reports *which*, not just *that*.
- **Not evidence when:** the test enumerates keys from `global.properties` and checks they exist. That is circular. Enumerate from the **Java/FTL call sites** and check they resolve.
- **Done when:** the test passes, `invalidUserCrp` is in `global.properties`, Checkstyle passes.

---

### CHG-COGNITO-AUTH-001-T14 — Log hygiene + observability
- **Status:** `[x]` — 2026-09-02, audited **PASS-WITH-FINDINGS** (`execution.md` §26). **152/152 clean**, and
  **both halves of the mutation cycle measured by the Leader**: the rejection-branch leak reddens exactly one
  test of fifteen, quoting the whole JWT; restored, byte-identical, green. The audit found the production log
  statements **correct** and the **evidence** hollow — no rejection path was swept for secrets, so the task
  own *Fails when* clause was satisfied only by where the first mutation happened to land. Also closed a CRLF
  log-injection vector that would have let an unauthenticated POST write a **fabricated successful-login
  record** into the authentication log. **This is the last code task in the spec.**

- **Depends on:** T08, T09, T11
- **Module:** marlo-web, marlo-data
- **Files touched:** log statements across the new actions and validator; `LoginAction.java` (extend the success line, add the membership-failure line)
- **Scope:** per design §11 — attempt started, validation failure (which check), state/nonce mismatch, gate rejection (which gate), success. **Extend `:281` to include the Global Unit** and **add** a membership-failure line, which does not exist today.
- **Constitutional checks:** Checkstyle; English only.
- **Design refs:** §11
- **Covers:** **OPS-001**, defect class **D-6**
- **Tests (new):**
  - Unit: a full successful flow emits **no** token, code, `state`, `nonce`, or verifier into a captured log appender.
  - Unit: each rejection path logs which check failed.
  - Unit: the success line contains the Global Unit.
- **Verification:** `mvn -q test -pl marlo-web -Dtest=CognitoLogHygieneTest`
- **Fails when:** a `LOG.debug("token=" + idToken)` is added anywhere in the flow — test 1 must catch it. Add one deliberately, watch it red, remove it.
- **Not evidence when:** the appender captures only `INFO` and above. Capture at `TRACE` — a leak at `DEBUG` is still a leak, and production log levels change.
- **Done when:** three tests pass, Checkstyle passes.

---

## 4. Dependency Graph

```
T00 (discovery, /api/** consumers) ──────────────── independent, blocks phase-1 rollout only
T01 (extract finishLogin) ─────────┐
T02 (specificity + constants) ──┐  │
T03 (deps + config) ──┐         │  │
                      ▼         │  │
                     T04 ──► T05 ──► T06 ──► T07*        (* blocked by OQ-9)
                                │      │       │
                      T02 ──────┴──────┴──► T08 ─────┐
                      T02 ──► T10 ──► T11            │
                                │                    ▼
                     T01, T05, T06, T07, T08 ─────► T09
                                │                    │
                     T08, T10 ──┴──────────► T12 ────┤
                                             T12 ──► T13
                                    T08, T09, T11 ─► T14
```

**No cycles.** Serial spine: `T03 → T04 → T05 → T06 → T07 → T09`.

### Parallel-safe within this spec

| Wave | Tasks | Note |
|---|---|---|
| 1 | **T00, T01, T02, T03** | Fully disjoint files |
| 2 | **T04, T10** | Different modules |
| 3 | **T05, T11** | T11 depends only on T02+T10 |
| 4 | T06 → T07 → T08 | Serial |
| 5 | **T09, T12** | Different layers |
| 6 | T13, T14 | Cleanup |

**Never parallel:** T02 and any other task touching `APConstants.java`; T08 and T09 both edit `struts-home.xml`.

---

## 5. Testing Plan

| Layer | Tasks | Count (approx) |
|---|---|---|
| Unit — pure, no container | T01, T03, T04, T05, T07, T10, T11, T13, T14 | ~30 |
| Integration — Shiro session + actions | T09 | ~8 |
| Manual — browser | T12 | 6 checks |
| Manual — DB | T02 | 1 |

**Every test in this plan is new.** `marlo-web/src/test/java/` holds three JUnit 4 classes today, none touching authentication.

### Scenario → task coverage (closes at clause level)

| Requirement | Scenario / clause | Owning task |
|---|---|---|
| FN-001 | CGIAR reaches step 3; `MUST NOT` render/focus/submit password | **T12** (mechanism) + T10 (mode data) |
| FN-001 | Local reaches step 3 under an explicit *External user* heading | **T12** (mechanism) + **T18** check 1 |
| FN-001 | Email not found; `MUST NOT` disclose path | **T10** test 3 |
| FN-002 | Successful sign-in; `MUST NOT` auto-provision | **T07** test 1, **T09** test 1 |
| FN-002 | Not a member → `invalidUserCrp`, session cleared | **T09** test 5 + **T13** |
| FN-002 | Inactive account | **T07** test 3 |
| FN-003 | Selection survives; `MUST` reject a value MARLO did not issue | **T09** tests 1–2 |
| FN-004 | Deep link preserved; type routing; `MUST NOT` return to logout | **T01** tests 1–4, **T09** tests 6–7 |
| FN-005 | Cancel/deny; i18n key; `MUST NOT` expose raw error or code | **T09**, **T13**, **T14** test 1 |
| FN-005 | Cognito unreachable; local path still works | **T09** + T12 check 3 |
| FN-006 | Username from claim, lowercased; `MUST NOT` LDAP; `MUST NOT` touch email | **T07** test 4 |
| FN-007 | Logout unchanged; no silent re-auth | **Satisfied by not changing `LoginAction.logout()`** — verified by its absence from every diff |
| SEC-001 | All six rejection classes + `token_use` | **T05** tests 1–9 |
| SEC-002 | `state`, PKCE, exact-match callback, single-use code | **T08** tests 3–4, **T09** test 4 |
| SEC-003 | New session id on success | **T09** test 3 |
| SEC-004 | No credential literal in `.java` | **T03** verification |
| SEC-005 | No endpoint relays a CGIAR password | **T11** tests 1–4 |
| SEC-006 | Federated identity must not unlock a local account | **T07** tests 2 + 5 |
| MIG-001 | Instant rollback; both paths coexist | **T02** + **T10** test 1 |
| OPS-001 | Outcome, path, Global Unit logged; no secrets | **T14** |
| OPS-002 | LDAP stays functional | **T11** test 2 |
| NF-001 | Round trip p95 ≤ 5 s | **T09** — measured at the HITL pause, not asserted in a unit test |
| NF-002 | A Cognito/IdP outage `MUST NOT` degrade the local login path | **T09** test 8 |
| NF-003 | Keyboard reachable, visible focus, accessible name | **T12** checks 4–5 |
| NF-004 | Existing Bootstrap components + palette; light theme only | **T12** check 1 + Reviewer token audit |
| NF-005 | Checkstyle passes | **Every task's** `mvn -q checkstyle:check` gate |
| NF-006 | No dependency downgraded | **T03** — `git diff marlo-parent/pom.xml` shows additions only |

**Every scenario and every `BUT`/`AND IT MUST` clause has a named owning task.** No gap is discharged by citing a different requirement.

---

## 6. Operational Steps

1. **Migration deploy** — T02's Flyway migration ships with the build. No `custom_parameters` seeded; every Global Unit is off.
2. **Configuration** — Cognito keys added to `marlo-${profile}.properties` in **every** environment **before** deploy. T03's defaults mean a missing key does not break startup, but `cognitoLogin.do` fails closed.
3. **AWS** — User Pool, app client (confidential, Authorization Code + PKCE), hosted domain, exact-match callback allowlist per environment, SAML/OIDC federation to the CGIAR IdP (**OQ-3**).
4. **Rollout** — enable per Global Unit: internal test unit → pilot CRP (one full reporting week) → progressive.
5. **BI / AI** — no coordination needed; neither consumes the login path. **Unless T00 finds otherwise.**

---

## 7. Rollback Plan

| Level | Action | Cost |
|---|---|---|
| Per Global Unit | `custom_parameters` value → `'false'` | **Seconds, no deploy.** The primary path |
| Code | Revert the branch, redeploy | Hours |
| Data | **None needed** — no user record is altered, moved, or pre-provisioned |
| Migration | The `parameters` rows are inert when no `custom_parameters` enable them. Leave them |

The flag is the rollback. Everything else is a fallback for a defect the flag cannot switch off.

---

## 8. Definition of Done

- [ ] All 14 tasks `[x]` with Reviewer PASS recorded in `execution.md`.
- [ ] `mvn -q install -DskipTests -pl marlo-web -am` and `mvn -q checkstyle:check` pass.
- [ ] `mvn -q test -pl marlo-web` passes, including ~37 new tests.
- [ ] Every scenario in §5's table maps to a passing test or a recorded manual check.
- [ ] **The gate-2 mutation (T07) demonstrably opens the bypass and closing it demonstrably shuts it.**
- [ ] **The `.remove()` → `.hide()` swap (T12) demonstrably fails check 2.**
- [ ] A local user's login is verified unchanged end to end.
- [ ] The flag disables the flow **without a deploy**, verified live.
- [ ] `grep -rn 'cognito' --include='*.java'` shows no literal configuration values.
- [ ] T00's consumer list exists and R-D4 is resolved or has a named follow-up.
- [ ] Pending items queued for `staging`: the TRD §8.4 correction and the DD-8 ADR-6 supersession.

---

## 9. Conventions Reminders

- GPL header on **every** new `.java` file.
- 2-space indent, 120-char lines, same-line braces, mandatory blocks, ≤3500 lines.
- English only in code and comments; user-facing strings are i18n keys.
- The specificity constant value **must equal** `parameters.key`, in **both** `APConstants.java` files.
- Never commit `marlo-dev.properties`.
- **Do not touch** `DBAuthenticator`, `LDAPAuthenticator`, `Authenticator.java`, `AuthenticationManager`, `MD5Convert`, or `users.password`. Their appearance in any diff is a FAIL.

---

### CHG-COGNITO-AUTH-001-T15 — `identity_provider`: route straight to the corporate IdP

- **Status:** `[ ]` — **added 2026-09-03. A new requirement identified during real E2E validation, not a
  defect.** The implementation does exactly what the spec asked; the spec never asked for this.
- **Why it exists:** clicking *Sign in with CGIAR* currently lands the user on **Cognito's Hosted UI
  provider-selection screen**. That is Cognito's correct default when `/oauth2/authorize` names no provider:
  it lists every IdP enabled on the app client. The team's other applications route **straight** to the
  corporate IdP, and MARLO must match. `design.md:78-79` draws *"Cognito User Pool └─ SAML/OIDC ──► CGIAR
  IdP"* and §9 says only *"redirect to `/oauth2/authorize`"* — **the design assumed direct routing and never
  specified the mechanism.** A repo-wide search of `requirements.md`, `design.md` and `tasks.md` finds no
  mention of *Hosted UI*, *identity_provider*, *provider selection*, or the provider name. Same shape as V-1:
  the intent was described, the contract was not.
- **Depends on:** T03 (config surface), T08 (`buildAuthorizeUrl`)
- **Module:** marlo-utils, marlo-web
- **Files touched:** `utils/APConfig.java` (add the eighth setting), `action/home/CognitoLoginAction.java`
  (append the parameter), `config/marlo-test.properties` (blank key, as T03 did for the other seven)
- **Scope:** add `cognito.identity.provider` following the **exact** pattern of the existing seven —
  `@Value("${cognito.identity.provider:}")`, a getter delegating to `cognitoSetting(...)` so `null` becomes
  `""` and the value is trimmed. Append `&identity_provider=<url-encoded value>` to the authorize URL
  **only when the configured value is non-empty**.
- **Constitutional checks:** **no literal provider name in any `.java`** — T03's verification clause and
  SEC-004; the value is configuration, and it differs per pool and per environment. Checkstyle; English only.
- **Deployment note — required, and it must be written down:** the property is optional **at code level** so
  an environment without federation keeps working unchanged. **For any MARLO environment where CGIAR Cognito
  authentication is enabled it is REQUIRED**: without it Cognito renders its Hosted UI and the expected direct
  corporate SSO flow does not happen. Absent ≠ safe default; absent = wrong screen.
- **Tests (new/updated):**
  - The parameter **is included** when the property is configured.
  - The value is **URL-encoded** — assert with a provider name containing a character that must encode
    (a space or `&`), not with a name that is already URL-safe, which would prove nothing.
  - The parameter is **omitted entirely** when the property is empty — not `identity_provider=`, absent.
  - **Every other authorize parameter is unchanged**: `response_type`, `client_id`, `redirect_uri`, `scope`,
    `state`, `nonce`, `code_challenge`, `code_challenge_method=S256`. Assert the full parameter set, so an
    accidental reordering or drop is caught.
- **Verification:** `mvn -q test -pl marlo-web -Dtest=CognitoLoginActionTest`
- **Fails when:** the parameter is appended unconditionally — the empty-property test must then find
  `identity_provider=` with no value in the URL.
- **Not evidence when:** the encoding test uses an already-URL-safe provider name. `CGIAR-AzureAD` encodes to
  itself, so a test using it proves the parameter is present, never that it is encoded.
- **Done when:** four tests pass, Checkstyle passes, no literal provider name in any `.java`, and the
  authorize redirect observed live carries the parameter.
- **Skills:** `tdd`

---

### CHG-COGNITO-AUTH-001-T16 — V-2: the stale request-level Shiro session after rotation
- **Status:** `[x]` — **CLOSED 2026-09-03 by real E2E evidence** (`execution.md` §30): the corporate login completed through the live pool, landed on the AICCRA dashboard, and produced **0** `UnknownSessionException` and **0** authentication errors. Also closed `saveLastLogin` on the Cognito path. Audited **PASS-WITH-FINDINGS** (`execution.md` §29); all
  four findings fixed. **165/165 verified by the Leader.** **Not `[x]`: the `Done when` requires the real
  corporate login to complete without the exception, in a browser, against the live pool** — only the user can
  run it. One audit finding (F5) was **false** and was refuted from bytecode; the Leader's own `Fails when`
  clause was also wrong and is corrected below.

- **Status:** `[ ]` — **added 2026-09-03. A real E2E defect (V-2), reproduced against the live Cognito pool.**
- **The failure, observed:** corporate authentication succeeded, Cognito returned to `cognitoCallback.do?code&state`,
  the action **completed** (`Cognito sign-in succeeded for Global Unit AICCRA`), and **33 ms later** the response
  died with `HTTP 500` — `UnknownSessionException: There is no session with id [78a503ec-...]` thrown from
  `DefaultCspSettings.addCspHeadersWithSession` → `ShiroHttpSession.setAttribute`.
- **Root cause, verified in bytecode, not inferred:** `ShiroHttpServletRequest` caches its `ShiroHttpSession`
  in a **`protected HttpSession session`** field, and `getSession(boolean)` does `getfield session` and returns
  it **without revalidating** — it only constructs a new wrapper when that field is `null`. `session.stop()`
  destroys the underlying Shiro session; `Subject.login()` creates a new one; **nothing clears the cache**. Any
  component reaching the request afterwards gets the dead wrapper. Struts' CSP interceptor does exactly that on
  every response, and — decisively — it **captures the request reference into a `PreResultListener` lambda
  before the action runs**, so nothing the action rebinds afterwards can reach it.
- **Why T09's fix did not cover it:** T09 re-points the **action's** `SessionMap` and rebinds `ActionContext`.
  `freshSessionMap()` calls `new SessionMap(request)`, which re-derives from the **same request whose cache is
  stale** — the container changed, the contents did not. And `DefaultCspSettings` never reads `ActionContext`.
- **Why no test caught it:** `InvalidatedSessionMap` is a hand-written double. It simulates a stopped session
  but never exercises the real `ShiroHttpServletRequest` caching that *is* the mechanism. §27.4 Category A
  predicted this in these words: *"a real successful login is the first time the production `SessionMap`
  participates."*
- **Depends on:** T09 · **Module:** marlo-web
- **Files touched:** `action/home/CognitoCallbackAction.java`, plus a new isolated helper and its tests
- **Scope — the narrowly scoped compatibility workaround, approved by the user 2026-09-03:**
  - **Preserve `session.stop()` and SEC-003 rotation.** The pre-auth session must still be destroyed before
    the new one is established. This task does **not** change when or whether rotation happens.
  - Clear **only** the stale cached request-session reference, and only **after** `Subject.login()` has
    established the new session.
  - Keep the reflection **isolated in one clearly documented helper**, not scattered at the call site.
  - **Fail soft** at runtime: if the field is absent, renamed, or inaccessible (sealed jar, security manager,
    JPMS), log a warning and continue. Degrading to today's behaviour is acceptable; degrading to something
    worse is not.
  - **Do not weaken or remove CSP.** Removing the interceptor would hide the first observed consumer of the
    stale session and introduce a security regression; other consumers would still fail later with an
    unrelated-looking trace.
  - **Document why reflection is necessary**, tied explicitly to the Shiro 1.13.0 constraint: `Subject` exposes
    no renewal API; `WebUtils` exposes nothing that touches the cached wrapper; `DefaultWebSecurityManager`
    offers no hook; `subject.logout()` only sets the `IDENTITY_REMOVED_KEY` **request attribute**, and
    `getSession(boolean)` **never consults it** (zero references in its bytecode); `changeSessionId()` does not
    apply to Shiro native sessions.
- **Tests (new) — the point is to exercise the mechanism, not to re-simulate the symptom:**
  1. **Compatibility test.** Fails if Shiro's expected internal structure changes — the field's declaring
     class, name, and type. A future Shiro upgrade must produce a **red test**, never a silently disabled
     workaround. This is the condition that turns a fragile hack into a managed one.
  2. **Real-mechanism test.** Build an actual `ShiroHttpServletRequest`, force it to cache its
     `ShiroHttpSession`, stop the session, invoke the helper, and assert `getSession(false)` resolves the
     **new** session without throwing. **No hand-written doubles.**
  3. **The test that bites.** The same scenario **without** the helper must throw `UnknownSessionException`.
     Without this, test 2 proves nothing.
  4. **Post-action access.** Capture the request **before** the callback runs, run the full callback, then
     `setAttribute` through `request.getSession()` — the reproduction of the real failure path the CSP
     interceptor takes.
  5. **Fail-soft.** A request that is not a `ShiroHttpServletRequest` must not break anything.
- **Verification:** `mvn -q test -pl marlo-web -Dminify.skip=true`
- **Fails when — CORRECTED 2026-09-03, the original clause was wrong and the implementer proved it.** This
  clause read: *"the helper is called **before** `Subject.login()` — test 2 must then fail... The ordering is
  the fix; prove it."* **It does not fail.** The implementer applied that exact mutation, ran the suite twice
  cleanly, and got green both times — then investigated instead of accepting a pass that contradicted the
  premise. Reason: `Subject.login()`'s eager session creation runs through `SecurityManager`/`SessionManager`
  machinery entirely independent of the request's cached field, and **nothing between the two call sites
  touches `request.getSession()`**, so clearing either side rebuilds identically. The Leader asserted an
  ordering guarantee that Shiro's internals do not create.
  **The mutation that does bite, and is the real proof:** remove the helper call entirely. Test 4 then fails
  with `IllegalStateException: UnknownSessionException: There is no session with id [...]`, thrown through
  `ShiroHttpSession.getAttribute` → `SessionMap.put` → `LoginAction.finishLogin` → `CognitoCallbackAction`.
  **Note what that path reveals:** the failing consumer in the test is `finishLogin`'s own `SessionMap.put`,
  **not** the CSP interceptor. So the stale wrapper has **at least two** consumers, which independently
  vindicates rejecting "just remove the CSP interceptor" — that would have hidden one and left the other.
  The call site stays **after** `Subject.login()` regardless: it is the position that matches the intent, even
  though Shiro does not enforce it.
- **Not evidence when:** any test substitutes a double for `ShiroHttpServletRequest`. That is precisely the
  substitution that let V-2 reach production.
- **Done when:** five tests pass, Checkstyle passes, and **the real corporate login completes without the
  exception** — which only the user can confirm, in a browser.
- **Skills:** `tdd`

---

### CHG-COGNITO-AUTH-001-T17 — U-3: stop writing `users.username` on the Cognito path

- **Status:** `[x]` — **CLOSED 2026-09-04 by real E2E evidence.** One real corporate login with `users.username` restored to `cgamboa`: the value stayed **byte-identical**, `last_login` advanced `11:37:26 → 21:15:58`, the AICCRA dashboard was reached, and there were **0** `UnknownSessionException` and **0** authentication errors. Audited **PASS-WITH-FINDINGS** (three LOW documentation items). **167/167** clean, mutation reddening exactly the three T17 tests. Originally added 2026-09-04 after real E2E evidence. Not a coding error: T07 did exactly what
  FN-006 asked. **The requirement's premise was false**, and only a real federated token could show it.
- **The evidence, from one real corporate login** (`execution.md` §31, §32): the ID token carries **16
  claims** and the CGIAR/AD login `cgamboa` is in **none** of them. `username` **absent**;
  `preferred_username` **absent**; `cognito:username` present but it is Cognito's own federated identifier
  (`cgiar-azuread_c.gamboa@cgiar.org`); `email` present. The pool mapping *"User pool attribute: username"*
  feeds the pool's username — Cognito prefixes it and emits it as `cognito:username` — and the AD claim
  returns the **UPN**, not the `sAMAccountName`. **No mapping inside MARLO can produce `cgamboa`.**
- **Depends on:** T07 · **Module:** marlo-data
- **Files touched:** `security/impl/CognitoIdentityMapperImpl.java`
- **Scope — the decision, taken by the user 2026-09-04:**
  - **Do not modify `users.username` during Cognito authentication.** Remove the write at
    `CognitoIdentityMapperImpl:96-100`.
  - Keep resolving the MARLO user by **normalized corporate email** (OQ-9). It is sufficient and is the
    identity key.
  - **Do not** derive a username from the email. **Do not** strip the provider prefix. **Do not** persist
    `cognito:username`.
  - A `null` or blank `users.username` **stays** null or blank — no value is invented.
  - `users.email` is still never overwritten.
- **Why the previous behaviour was worse than doing nothing:** it replaced a correct AD login with a value
  correct for nothing — breaking username-based local login (`APCustomRealm:161`, `getUserByUsername`, which
  is the branch taken when a user types a login without `@`) and surfacing as a display name in QA comments
  (`FeedbackQACommentsAction:180`, `:403`). The LDAP path remains the only writer and repairs the value by
  looking Active Directory up **by email** (`APCustomRealm:333-338`).
- **Constitutional checks:** `users.email` untouched; gates 1-3 and their ordering unchanged; SEC-006's
  indistinguishable refusals unchanged; no change to what the user sees.
- **Tests (new/updated):**
  1. **A populated username is preserved byte-for-byte** across a successful Cognito login — assert the exact
     prior value, not merely "not null".
  2. **A `null` username stays `null`** — nothing is invented.
  3. **A blank username stays blank** — the trimmed-empty case is distinct from `null` and must not become a
     derived value either.
  4. **The email is still never overwritten**, and the user is still resolved by the normalized email.
  5. **Existing T07 tests that asserted the username WAS set must be updated, not deleted** — their intent
     (the write happens) is inverted by this decision, and the updated assertion must fail if the write
     returns.
- **Verification:** `mvn -q clean test -pl marlo-web -Dminify.skip=true`
- **Fails when:** the write is restored — tests 1-3 must all redden. Re-adding `user.setUsername(...)` is a
  one-line regression and the suite must catch it.
- **Not evidence when:** a test asserts only that the username is "not corrupted". Assert the **exact** prior
  value; a test that would pass on any non-federated string does not pin the contract.
- **Done when:** the tests pass, the full clean suite passes, no diagnostic code remains anywhere, and **a
  real corporate login leaves `users.username` unchanged in the database** — which only the user can confirm.
- **Skills:** `tdd`

---

### CHG-COGNITO-AUTH-001-T18 — Make the external path explicit in the UI

- **Status:** `[x]` — added 2026-09-04, **closed 2026-09-05.** A UX requirement, not a defect. Deliberately
  **minimal**: the flow was not redesigned. Audited **PASS** on round 3; rounds 1 and 2 FAILed against the
  governing documents, not the code (see `execution.md` §33). All five manual checks pass, the last one
  after a redeploy — `global.properties` is bundled in `WEB-INF/lib/marlo-web-*.jar` and `struts.devMode=false`,
  so the key could not resolve until the jar was rebuilt.
- **Why it exists:** step 3's local block shows a password field with no statement of *which* authentication
  method the user is on. During a migration where two methods coexist, that is ambiguous. The CGIAR path
  already announces itself through its labelled button; the external path announces nothing.
- **Depends on:** T12 · **Module:** marlo-web
- **Governing documents re-amended 2026-09-04, before this task closed.** FN-001 and `design.md` §5.2 had
  been amended earlier the same day to require an *External user* **control** revealing the password only
  once chosen, plus absence **by construction** on the CGIAR path. The first T18 audit FAILed against that
  text. Both clauses are now withdrawn with a Decision Log entry — the method is a **heading**, not a gate.
  This task implements the re-amended text.
- **Files touched:** `webapp/WEB-INF/global/pages/loginForm.ftl`, `resources/global.properties` (one key)
- **Scope — small on purpose:**
  - Add a clear, **i18n-backed** *External user* label or heading to `#login-step-password`.
  - `#login-step-password` and `#login-step-cgiar` are structurally identical today — headline, selected
    project, echoed email — so place the label consistently with that structure. Reuse existing classes
    (`login-subtext` — the class steps 1 and 2 already pair with `login-headline`); introduce **no new**
    **colour, spacing or type scale**.
  - Add the key to `global.properties`. That file is **T13's** declared deliverable; adding one key here is a
    crossing, so **flag it in the report** exactly as T09 and T12 did, and check it duplicates nothing.
- **Explicitly NOT in scope — do not do these:**
  - **Do not** change `mode = isCgiarUser && cognitoEnabled`. It is correct and stays byte-identical.
  - **Do not** add a method-selection step or any extra click. The matrix resolves to exactly one valid method,
    so an intermediate choice would be an artificial navigation step.
  - **Do not** rewrite T12's password creation/restoration mechanism. It cost two audit rounds and a blocking
    defect (a permanently dead form, `execution.md` §23) and is validated by a real corporate login. **The
    password field may remain in the DOM during steps 1 and 2** — the requirement is that it is removed before
    the CGIAR path continues, which `.remove()` already guarantees.
  - **Do not** touch `login.js` structurally. The local branch already reaches the block and already creates
    the field.
  - **Do not** touch any backend: `CognitoLoginAction`'s six gates, T11/T11b's relay guards, T15, T16, T17,
    `crpByEmail.do`, `cognitoLogin.do`, `validateUser.do` all stay exactly as they are.
- **Constitutional checks:** hard rule 8 — the label is an i18n key, never a literal. Light theme only,
  existing palette (`docs/ux-ui/design.md` §7, §11). Keyboard reachability unchanged (§10).
- **Must be preserved, and each is already validated:** back navigation, keyboard focus order, the password
  visibility toggle, Enter submission, and the **shared** `.terms-container` gating both paths.
- **Tests:** **no automated frontend harness exists** (accepted defect class D-5). Verification is manual.
- **Verification (manual):**
  1. **Local user reaches step 3** → the *External user* label is visible, the password field, eye icon and
     Log in button all behave exactly as before. **This supersedes T12's check 1**, which required step 3 to
     be *pixel-identical* to today — it deliberately is not any more.
  2. **CGIAR user, flag on** → the CGIAR block is shown, the label does **not** appear there, and
     `document.querySelector('#login-password')` is still `null` and absent from `new FormData(form)`.
  3. **CGIAR user, flag off** → the password step **with** the label. **Supersedes T12's check 3.**
  4. The label text is **rendered, not a raw key** — a missing entry would surface as
     `login.externalUser` on screen, which is exactly the class of defect T13 exists to close.
  5. **Back-navigation (T12's check 7) re-run unchanged**, including its four sub-steps: field present, a
     deliberately wrong password produces *incorrect password* and not *password is required*, the eye icon
     toggles, and Enter submits.
- **Fails when:** the label is added to the shared region rather than to `#login-step-password` — check 2 must
  then find it on the CGIAR path, where it is wrong.
- **Not evidence when:** verified only by reading the FTL. A missing i18n key renders as the raw key and looks
  fine in source. **Look at the screen.**
- **Done when:** the five manual checks pass, the Java suite stays at **167/167**, Checkstyle-relevant style
  holds, and T12's checks 1 and 3 are updated in `tasks.md` to match.

---

### CHG-COGNITO-AUTH-001-T19 — V-4: a return URL may never be an authentication endpoint

- **Status:** `[x]` — 2026-09-05. Fixes **V-4** (`execution.md` §34), found in the live environment. **Not** an
  authentication defect: authentication succeeded completely and the session was established. This is
  post-authentication routing.
  Implemented entirely inside `sameOriginOrNull()` plus one new private helper, `isAuthenticationEndpoint(String)`,
  as scoped. **174/174 tests on a clean run** (167 + 7 new), compile PASS, `finishLogin` and `LoginAction.java`
  untouched. The mutation cycle was measured, not asserted: disabling the rejection reddened exactly **4** of the
  20 tests in `CognitoLoginActionTest` -- the three helper-level tests (callback-with-query, `cognitoLogin.do`,
  case-variant) **and test 7, the integration-level test through `authorize(...)`**, confirming test 7 is the one
  that catches it as the task predicted. Restored, `git diff` confirmed byte-identical to the pre-mutation state.
  Checkstyle fails on pre-existing **EB-1** (`maven-checkstyle-plugin:2.9.1` vs `checkstyle:8.18`), reported, not
  chased.
  **Audited PASS** (round 1, 2026-09-05), *after* this block was first written — the implementer marked the
  task `[x]` before the audit ran and the narrative above pre-stated its outcome. The status is correct now;
  the sequencing was not. The audit confirmed the same-origin control flow is right after its inversion
  (`!(E∨S) ≡ ¬E∧¬S`), that no input reaches the accept branch while resolving to an authentication endpoint,
  that the deep-link guarantee holds, and that test 7 genuinely crosses the Shiro seam rather than a double.
  It carried **one substantive advisory**: four of this task's own `Fails when` bypass classes have no
  executable coverage — see `execution.md` §35.
- **Depends on:** T09 (the defect is latent since then) · **Module:** marlo-web
- **Files touched:** `action/home/CognitoLoginAction.java` (one private method), plus tests
- **Root cause, already confirmed — do not re-derive it:** `returnUrl` is the `Referer` of the GET navigation
  to `cognitoLogin.do` (`CognitoLoginAction:323-324`), filtered only by `sameOriginOrNull()` (`:346-363`),
  which compares **origin only**. A same-origin `cognitoCallback.do?code=…&state=…` therefore passes, is
  stored in `PendingAuthorization`, and after a successful login is consumed by `LoginAction:425-428`
  (`contains(".do") && !contains("logout")`) → `LOGIN` → redirect **back into the callback** → the single-use
  `PendingAuthorization` is gone → refusal → login page.

#### Scope

- A `returnUrl` **MUST NOT** resolve to an authentication endpoint. Reject **at least**: `cognitoLogin.do`,
  `cognitoCallback.do`, `validateUser.do`.
- **Preserve same-origin validation exactly as it is.** This adds a rejection; it removes none.
- **Validate the normalized path, never a substring.** `contains()` is the very idiom that produced this
  defect — `!urlAction.contains("logout")` is its sibling. Parse the URL, take the path, normalize it, and
  compare its final segment against a **closed set**, case-insensitively.
- **When rejected, store `null`** — not `""`, not a rewritten URL. `null` is what makes `finishLogin` fall
  through to its `switch` and return `SUCCESS`, which is the existing, already-validated route to the
  dashboard. Do not add a new branch to `finishLogin`.
- **Preserve legitimate same-origin deep links.** A user deep-linked to `…/aiccra/projects.do?id=1` and sent
  through login must still land there. This is real functionality, not incidental behaviour.
- **Preserve every existing Cognito authentication and session behaviour**: the six `CognitoLoginAction`
  gates, PKCE, `state`/`nonce`, T11/T11b, T15, T16, T17. Nothing outside this one method changes.

#### Explicitly NOT in scope

- **V-5 — the refusal path rendering the login form at the callback's own URL.** Recorded separately in
  `execution.md` §34.5 on the user's explicit instruction. It needs the field-error message preserved across a
  redirect, which is a different problem with a different failure mode. **Do not touch it here.** Fixing V-4
  alone makes the reported symptom impossible.
- `finishLogin`'s `.do` heuristic itself. It is shared with the local login path, which has worked for years
  because `login.do` rescues an authenticated user at `LoginAction:304-325`. Narrowing it is a larger,
  riskier change than this defect requires.
- No backend gate, no session handling, no frontend, no i18n.

#### Where the guard goes, and why

Inside `sameOriginOrNull()`, which is called from `authorize(String)`. That method's own comment (`:209-211`)
records why: *"authorize(String) is the seam every caller and every test uses, so a guard on one entry point
would be bypassable by all the others."* The same reasoning binds this guard. Do not add it at
`execute()`.

Log a rejection distinctly from the off-site case, and — as that line already does — **without echoing the
URL**.

#### Tests

- `sameOriginOrNull` / `authorize`: **(1)** callback URL **with query** rejected; **(2)** `cognitoLogin.do`
  rejected; **(3)** a **case-variant** endpoint rejected; **(4)** a legitimate deep link still **accepted**;
  **(5)** an external origin still rejected; **(6)** `null` and blank still return `null`.
- **(7) Integration-level, through `authorize(returnUrl)`, asserting the stored
  `PendingAuthorization.getReturnUrl()` is `null`** — not the helper in isolation. **This one is the point of
  the task.** Nine times in this spec the defect shape has been *correct in isolation, dead through the real
  framework, certified green by a double gentler than production* (`execution.md` §27). A test that only calls
  the private helper is that shape exactly.
- **(8) Mutation:** removing the auth-endpoint rejection **MUST** turn at least one test red. Measure it; do
  not assert it. Test 7 is the one that should catch it.

#### Verification

- `mvn -q install -DskipTests -pl marlo-web -am`; suite **≥ 167 + new**, no regressions.
- **Check for a live Maven JVM before measuring.** Six red builds in T07/T11b came from ignoring this.
- `checkstyle:check` is expected to fail on pre-existing **EB-1**; report it, do not chase it.

#### Fails when

- The check is a substring match, so `…/notCognitoLogin.do` or a query parameter containing the literal
  `cognitoCallback.do` is rejected — a legitimate deep link silently becomes a dashboard redirect.
- Path normalization is skipped, so `…/x/../cognitoCallback.do`, a URL-encoded path, a `;jsessionid` suffix,
  or a trailing `/` slips through. **Normalize, then compare.**
- A malformed URL throws instead of being rejected. `null` is the safe answer for anything unparseable.
- Rejection stores `""` instead of `null` — `finishLogin`'s guard is `urlAction != null && …`, so `""` takes
  the same fall-through, but only by accident. Be explicit.

#### Not evidence when

- Verified only against the helper. See test 7.
- Verified only by a green suite. The suite was green **while this defect was live**, through eleven real
  corporate logins.

#### Done when

Tests 1–8 pass, the mutation in test 8 is **measured** red, the suite has no regressions, and V-4's reported
symptom is reasoned through end to end: a rejected return URL yields `null` → `finishLogin` returns `SUCCESS`
→ `${crpSession}/crpDashboard`.

- **Skills:** `tdd`, `systematic-debugging`

#### T19 — declared coverage extension (approved by the user 2026-09-05)

- **Status:** `[x]` — **closed 2026-09-05. Test hardening only. No behavioural change.** 11 tests added,
  production code byte-identical, suite **185/185**, all six mutations measured red one at a time.
  **The E2 row of this table was wrong** and the implementer caught it: `…/x/../cognitoCallback.do` is
  rejected with or without `.normalize()`. The discriminating case puts the traversal *after* the callback
  segment (`…/cognitoCallback.do/foo/..`). See `execution.md` §35.5.
  stays as it is. This locks down behaviour it **already implements** but that nothing currently proves.
- **Why:** T19's mutation (test 8) removes the whole guard. **Six weaker mutations leave the suite entirely
  green**, and four of them reopen bypass classes T19's own `Fails when` list names (`execution.md` §35.2).
  The gap is between the task's required-coverage list and its `Fails when` list — a **specification** defect,
  not an implementation one. The implementer wrote exactly the eight tests it was given.
- **Module:** marlo-web · **Files touched:** `CognitoLoginActionTest.java` only

**The rule that governs this extension, and it is absolute:**

> **Do not change production behaviour to make a test pass.** If any new test fails against the current
> implementation, **STOP and report the discrepancy** before touching `CognitoLoginAction.java`. A failing test
> here means one of two things — the test is wrong, or T19 has a real defect the audit missed — and **which one
> it is, is not the implementer's call to make silently.**

**Tests to add.** Each is named with the mutation it must kill:

| # | Input | Expected | Mutation it kills |
|---|---|---|---|
| E1 | `…/cognitoCallback%2Edo` | rejected | `getPath()` → `getRawPath()` |
| E2 | `…/x/../cognitoCallback.do` | rejected | delete `.normalize()` |
| E3 | `…/cognitoCallback.do;jsessionid=ABC` | rejected | delete the `;` strip |
| E4 | `…/cognitoCallback.do/` | rejected | delete the trailing-slash loop |
| E5 | a URL `new URI(...)` cannot parse (e.g. a raw space) | rejected → `null` | `return true` → `false` on `URISyntaxException` |
| E6 | a case-variant that only a locale-sensitive `toLowerCase()` would mishandle | rejected | `Locale.ROOT` → default locale |
| E7 | `…/cognitoCallback.do/../projects.do` | **accepted** | — |

**E7 is not optional and is not symmetry for its own sake.** It is the single case that distinguishes this
implementation from a `contains()` one: a substring check rejects it, and the correct implementation accepts it
because the URL resolves to `/projects.do`. Without E7 the suite cannot tell the two apart.

**E6 needs care.** `Locale.ROOT` matters for the Turkish dotted-I: under `tr-TR`, `"I".toLowerCase()` yields
`ı` (dotless), so `COGNITOCALLBACK.DO` would not match the set. A test that merely varies case proves nothing
about the locale — **it must actually exercise the locale**, or state plainly in a comment that it does not and
that the mutation therefore survives. **Do not pretend.** If it cannot be done cleanly in this harness, say so
and leave E6 out with the reason recorded — an honest gap beats a test that looks like coverage.

**The acceptance direction and the `getBaseUrl()` double.** `TestableCognitoLoginAction.getBaseUrl()` returns a
fixed value, which hides a real production risk: `APConfig.getBaseUrl()` returns `null` on an unconfigured
`BASE_URL` and force-prefixes `http://` onto a scheme-less value, so an http/https mismatch behind a proxy
would make the same-origin check discard **every** deep link — **with the suite still green** (`execution.md`
§35.3). Add coverage that varies the base URL through the double rather than relying on the one fixed value:
at minimum a `null` base URL, and a base URL with and without a trailing slash. **State clearly in the report
what these can and cannot prove** — a unit test cannot verify `APConfig`'s real behaviour, and claiming
otherwise would be exactly the "certified green by a double gentler than production" pattern this spec has hit
nine times.

**Verification:** every new test passes **against the unmodified production code**; the full suite is
`174 + new`, no regressions; each mutation in the table above is **measured** red, one at a time, not asserted.
Report T19's status as PASS only if all of that holds.
