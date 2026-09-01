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
- [ ] **OQ-9 answered** — which claim is the stable identifier (`sub` / `oid` / email). **Blocks T07.**
- [ ] **OQ-3 answered** — CGIAR IT confirms they will federate MARLO. **Blocks T12 onward**; a "no" returns to the parent proposal, it does not patch this spec.
- [ ] `git pull` on `staging`; branch is `staging-cognito`.
- [ ] A Cognito User Pool + app client exists in a dev account (may be a stub IdP until OQ-3 resolves).

---

## 3. Task List

> **How to read the verification fields.** Each task states three things, and all three are load-bearing:
> **Verification** — the command or check. **Fails when** — a concrete input that makes it report failure; if none can be named, the check is not evidence. **Not evidence when** — the condition under which a green result must be reported as *inconclusive* rather than as a pass.

---

### CHG-COGNITO-AUTH-001-T00 — Discovery: enumerate `/api/**` Basic-auth consumers

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

- **Status:** `[~]` — code complete and **audited** 2026-08-31 on `staging-cognito-impl` (independent Reviewer,
  `sonnet`, verdict PASS-WITH-FINDINGS; see `execution.md` §4). Compile PASS, 44/44 tests PASS, Checkstyle 0
  violations (via a compatible plugin — the pinned one is broken, see `execution.md` EB-1).
  **Not `[x]`: one *Done when* clause is still open.** It requires the single behavioral change to be
  *"explicitly called out in the commit body"*, and nothing has been committed yet. This flips to `[x]` when the
  commit exists with that message — not before.
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

- **Status:** `[~]` — 2026-08-31 on `staging-cognito-impl`. AWS SDK v2 BOM `2.31.30` + `nimbus-jose-jwt 9.48` in
  `dependencyManagement` (both resolution-checked); 7 `@Value` fields all in the `${key:}` form with getters that
  return empty, never `null`; the 7 keys added blank to `marlo-test.properties`. Compile PASS, 47/47 tests PASS,
  Checkstyle 0 violations in `marlo-web`/`marlo-data`/`marlo-utils`, no literal config value in any `.java`.
  **Not `[x]`: the live boot clause is open** — *"the app boots with no Cognito keys present"* needs
  `run-marlo-java17.sh`, which is destructive, and was left for the user to authorise (`execution.md` PS-7).
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

- **Depends on:** T05 · **BLOCKED until OQ-9 is answered**
- **Module:** marlo-data
- **Files touched:** mapping logic in `security/impl/CognitoTokenValidatorImpl.java` or a small collaborator (implementer's call, stated in the report)
- **Scope:** resolve the assertion to a `users` row and apply, **in order**: (1) row exists — no auto-provisioning; (2) **`is_cgiar_user = 1`**; (3) `is_active`; (4) *(membership is gate 4 and lives in `finishLogin`)*. Also set `users.username` from the CGIAR login claim, lowercased.
- **Constitutional checks:** username write goes through `userManager.saveUser()` so the audit listener fires; **`users.email` is never overwritten**.
- **Design refs:** §13.1, FN-002, FN-006
- **Covers:** FN-002 (all three scenarios), FN-006, **SEC-006**
- **Tests (new):**
  - Unit: unknown claim → refused, **and no `users` row is created** (assert the row count is unchanged — FN-002's `MUST NOT auto-provision`).
  - Unit: **`is_cgiar_user = 0` with a matching CGIAR email → refused on gate 2** (SEC-006). Assert the refusal reason is gate 2, not membership.
  - Unit: `is_active = false` → refused with `USER_DISABLED`.
  - Unit: valid CGIAR user → `users.username` set lowercased; **`users.email` unchanged**.
  - Unit: the refusal message for gate 2 is **indistinguishable** from the generic failure (SEC-006's `MUST NOT` reveal).
- **Verification:** `mvn -q test -pl marlo-web -Dtest=CognitoIdentityMappingTest`
- **Fails when:** gate 2 is removed — the `is_cgiar_user = 0` test must then **pass authentication**, which is the bypass Judgment Day found. Delete the gate once, watch it go green, restore it. That red-to-green-to-red cycle is the only proof the gate is doing work.
- **Not evidence when:** OQ-9 is still open. Mapping on email when the answer turns out to be `sub` means every test here encodes the wrong join key.
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
- **Done when:** eight tests pass, Checkstyle passes, and the ordering mutation reddens the expected tests.
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

### CHG-COGNITO-AUTH-001-T12 — Login wizard: mode composition + DOM removal

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
  1. Local user → step 3 is today's password step, pixel-identical.
  2. CGIAR user, flag on → password input is **absent from the DOM** (`document.querySelector('#login-password')` returns `null`) and absent from `new FormData(form)`.
  3. CGIAR user, flag **off** → password step, as today.
  4. Keyboard-only: tab to the control, activate with Enter and with Space.
  5. Screen reader announces the control with a meaningful name.
  6. Single-Global-Unit user (step 2 auto-skipped) → correct step 3.
- **Fails when:** `.remove()` is swapped for `.hide()` — check 2 must then find the node and find it in the FormData. **Run that swap once deliberately**; it is the exact defect Judgment Day found in revision 1.
- **Not evidence when:** verified only by reading the JS. FN-001's clause is about the **DOM at submit time**, and a code read cannot see it. Run it in a browser.
- **Done when:** all six manual checks pass and are recorded with screenshots in `execution.md`.
- **Skills:** `ui-ux-pro-max`

> **Accepted gap (defect class D-5).** This repository has no frontend test runner and no E2E harness, so checks 1–6 have **no automated gate**. Substitutes: the manual walkthrough above, plus a **T6 multimodal review** of the screenshots. Recorded as an accepted risk, not as coverage. Standing up a JS test runner is a TRD stack decision, not an inner-loop improvisation.

---

### CHG-COGNITO-AUTH-001-T13 — i18n keys, including one that is missing today

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
| FN-001 | Local reaches step 3 — byte-for-byte unchanged | **T12** check 1 |
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
