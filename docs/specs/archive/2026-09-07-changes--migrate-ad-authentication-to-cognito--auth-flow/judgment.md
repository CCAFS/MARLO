# Judgment Day — Findings Ledger

| Field | Value |
|---|---|
| Target | `docs/specs/changes/migrate-ad-authentication-to-cognito/auth-flow/design.md` |
| Mode | `judgment_day` — blind dual review |
| Round | 1 |
| Date | 2026-08-24 |
| Judges | 2, parallel, blind to each other, read-only, fresh context |
| Author ≠ auditor | **Context axis: yes** (judges saw only artifacts + code). **Model axis: no** — author and both judges ran on the T3 tier model. Recorded rather than left implicit |
| Corroborating scope | `requirements.md`, `proposal.md`, `family.md`, `CLAUDE.md`, `AGENTS.md`, `docs/trd/trd.md`, `docs/infrastructure.md`, and the real source files |
| Status | **Round 1 corrections applied. User chose *Fix only* — no re-judgment was run.** |

## Counts

| Band | Count |
|---|---|
| SEVERE — confirmed by both judges | **6** |
| Confirmed by both, severity contested | **1** |
| SEVERE — single judge, **verified true by parent** | **2** |
| WARNING — confirmed by both | 6 |
| WARNING — single judge | 9 |
| SUGGESTION | 9 |
| Contradictions between judges | **0** |

---

## Confirmed SEVERE (both judges)

### C-1 — `authMode` is a per-Global-Unit decision delivered before the Global Unit exists

Design §9 defines `authMode = is_cgiar_user AND cognitoEnabledFor(selectedGlobalUnit)`, but §4 returns it as a single scalar from `crpByEmail.do`, which runs at wizard **step 1**. `CrpByUserEmailAction.prepare()` receives only the email and its `execute()` *returns* the list of units the user may then pick from (`login.js:507-537`). The Global Unit is chosen after that response.

**Failure:** for any CGIAR user belonging to two Global Units with different flag states — exactly the population rollout phases 1–3 create — the server cannot compute `authMode` when the design says it must. `requirements.md` MIG-001 ("the path used MUST be determined by the Global Unit selected in wizard step 2") is unimplementable as designed. Judge A adds: when `crps.length == 1`, step 2 is skipped entirely (`login.js:530-534`), so the "selected Global Unit" may never be an explicit user action.

**Fix:** move the flag onto each `crps[]` entry (the loop at `CrpByUserEmailAction.java:72-102` already builds one map per unit) and compose `authMode` in `login.js` when a project card is clicked. Update §1, §4, §5, §9, §17.

### C-2 — The FreeMarker-conditional mechanism cannot exist

Design §5 claims the password input is "never emitted into the DOM ... absent" via FreeMarker-conditional sibling blocks. But `loginForm.ftl` is rendered server-side at page load (`login.ftl:17`), before any email is known; line 94 emits `<input id="user.password" ... required/>` unconditionally, and `login.js` only toggles a `hidden` class.

**Failure:** DD-2's entire rationale ("rejected CSS-hiding because a hidden input still posts") rests on a mechanism that does not exist. Implemented literally, the password input stays in the DOM **and in the submitted form** for CGIAR users, so FN-001's negative clause fails on the exact guarantee the design calls structural. Judge A adds that `required` on line 94 would also block HTML5 submission if merely hidden.

**Fix:** specify the real mechanism — `.remove()` the node on the COGNITO branch, or move the Cognito control outside `<form action="login">` — and assert node absence at submit time in D-2's gate. Rewrite §5 and DD-2.

### C-3 — `LoginAction.login(User, GlobalUnit)` cannot be reused verbatim

The method dereferences the Struts-populated **instance field** `user`, not its parameter: `LoginAction.java:268, 269, 274, 277` (`user.setPassword(null)`) and `:281` (`LOG.info("User " + user.getEmail() + ...)`). In `CognitoCallbackAction` that field does not exist.

**Failure:** NPE on the success path of every Cognito login, and NPE again on the `existCrpUser` failure path that FN-002 requires to "behave identically to the local-login case". DD-6 also never says *where* the extracted method lives, and the body depends on `getSession()`, `addFieldError()`, `getText()`, `isVisibleTopGUList()`, `setCrpSession()`, `getBaseUrl()` — all `BaseAction` instance members. The refactor is materially larger than DD-6 admits, and R-D1's gate aims at a refactor with no defined shape.

**Fix:** name the destination explicitly (subclass, `protected` method on `BaseAction`, or a helper taking the `BaseAction`), enumerate lines 268/269/274/277/281 as the sites to parameterize, and state the resulting signature.

### C-4 — Deep-link preservation is unimplementable and adds a crash path

`LoginAction.java:290` reads the deep link from `ServletActionContext.getRequest().getHeader("Referer")`, then `:295` calls `.contains(".do")` with **no null guard**. On the Cognito callback the request is a cross-origin top-level navigation: `Referer` is either the Cognito origin (no `.do` → deep link silently lost) or absent (**NPE → 500**). DD-4 enumerates what `state` binds — Global Unit, `nonce`, PKCE verifier — and the return URL is not among them.

**Failure:** FN-004's "Deep-link preserved" scenario is not satisfied, and an IdP sending `Referrer-Policy: no-referrer` turns every CGIAR login into a 500. Both surface only in a live environment, because no test exists.

**Fix:** capture the originating `.do` URL in `CognitoLoginAction`, bind it to the server-side state payload, and pass it into the extracted tail as a parameter. Add the null guard.

### C-5 — Session-ID rotation (SEC-003) is asserted with no mechanism, and the obvious implementation destroys the DD-4 state

§13 lists "id rotated on success — pre ≠ post" as a met response measure. No section describes rotating anything. `MarloShiroConfiguration.java:72-89` wires a plain `DefaultWebSessionManager`; **Shiro 1.x does not rotate the session id on `Subject.login()`**.

**Failure:** SEC-003 is a `MUST` with a dedicated gate (D-8) that would fail against a literal implementation. Worse, DD-4 stores `state`/`nonce`/verifier in that same pre-auth session: a naive `stop()` before the state check discards them; after `LoginAction.login(...)` it discards `SESSION_USER`/`SESSION_CRP`/custom parameters. The ordering is security-relevant and entirely unspecified.

**Fix:** state the ordering explicitly — consume state/nonce/verifier → validate → capture carry-forward values → stop the pre-auth session → create a new one → run the post-auth tail. Say that Shiro provides no automatic rotation, and whether the local path rotates too.

### C-6 — The `Authenticator` seam cannot carry a `CognitoAssertion`

`Authenticator.java:32` declares exactly one method: `Map<String,Object> authenticate(String email, String password)`. DD-1's premise — "the existing seam already supports" this branch — is false, and §2 does not list `Authenticator.java` as modified.

**Failure:** an implementer reaches `CognitoAuthenticator` with no legal way to hand it an assertion. The options are to change the shared interface (which the footprint forbids and `requirements.md` §4 fences off), to smuggle a serialized assertion through the `password` string, or to abandon the seam — a wall on the very file DD-1 is named after.

**Fix:** state the interface change explicitly (a `default` method, or a separate `AssertionAuthenticator`), add the file to §2, and re-argue DD-1 against the real cost — or drop the seam claim and let the realm consume the assertion from the token.

---

## Confirmed by both, severity contested (A: SEVERE · B: WARNING)

### C-7 — §2's realm row is incoherent, and the local prologue *is* edited

§2 says "token-type dispatch; the CGIAR branch routes to Cognito **when the flag is on**". §9 of the same document proves the flag is unreadable there ("At authentication time the session is empty"), and the realm has neither the Global Unit nor a `CustomParameterManager`. The two clauses describe mutually exclusive mechanisms.

Separately: `APCustomRealm.java:113-115` performs an **unconditional** `(UsernamePasswordToken) token` cast at the head of `doGetAuthenticationInfo` — executed by every local login. Any token-type dispatch must go there, in the shared prologue, not in a branch.

**Failure:** the claim "the `else` (local) branch is not edited" is true but misleading, and `requirements.md` D-2's gate ("assert the realm's local branch is untouched") passes green while the prologue every local login executes was rewritten.

**Fix:** rewrite the §2 row to describe only an `instanceof` guard inserted above the cast, preserving the `UsernamePasswordToken` path byte-for-byte; delete the "when the flag is on" clause; restate D-2's gate as "the `UsernamePasswordToken` path produces identical behavior".

---

## Single-judge SEVERE — verified true by the parent orchestrator

Protocol classifies these as *suspect* (no auto-fix). Both were checked directly against the code and **both hold**. They are the two most security-relevant findings in the round, so they are surfaced for an explicit decision rather than filed as unconfirmed.

### V-1 — No `is_cgiar_user` gate in the callback → authentication bypass into local accounts

§13 "The local record stays authoritative" enumerates exactly three gates: row exists, `is_active`, `crp_users` membership. **`is_cgiar_user` is absent.** §9 claims the composed check is re-verified "in `CognitoLoginAction`", but that action runs *before* the redirect and, per §1, only resolves the Global Unit and the flag — the user's identity is not known there, so the `is_cgiar_user` half is verified **nowhere**.

**Failure:** many MARLO local accounts are CGIAR staff whose `users.email` is `@cgiar.org` with `is_cgiar_user = 0` — precisely the population `requirements.md` OQ-1 exists to count. Anyone who can authenticate at the CGIAR IdP for such an address obtains a MARLO session for a **local** account without its MD5 password. That is a bypass of the path this spec declares out of scope and untouched.

*Verified by parent:* §13's gate list was read directly; `is_cgiar_user` is genuinely absent.

**Fix:** the callback must assert, before minting the assertion or calling `Subject.login`, that the resolved row has `is_cgiar_user = 1` **and** that the specificity is still enabled for the session-bound Global Unit. Add both to §13 and to the D-2 gate.

### V-2 — `validateUser.do` still relays CGIAR passwords to Active Directory → SEC-005 not achieved

`ValidateUserAction.java:71` — `User user = userManager.login(userEmail, userPassword);` — reached from `login.js:542-553` (`POST /validateUser.do` with `userPassword`). Registered in `struts-home.xml:15-21`, package `homeJson`, **no `requireUser`**. `UserManagerImp.login` → `UsernamePasswordToken` → `APCustomRealm` → LDAP branch.

**Failure:** `requirements.md` SEC-005 states MARLO "MUST NOT accept, transmit, log, or store a CGIAR password at any point in the new flow", and §13 claims "MARLO never receives a CGIAR password". With the flag on, this endpoint stays live and keeps relaying CGIAR credentials to AD — the password-spray surface the migration exists to close. The design's §1 diagram **omits `validateUser.do` entirely**, showing step 3 posting straight to `login.do`, so it also mis-describes the current flow it promises to preserve.

*Verified by parent:* the endpoint, its POST-only guard, its `userManager.login()` call, and the `login.js` caller were all read directly. The finding is accurate.

**Fix:** add `validateUser.do` to §2's footprint; specify that it must refuse to authenticate an `is_cgiar_user = 1` account whose Global Unit has the flag enabled, returning the same generic failure; correct §1 to show the real two-request local flow.

---

## WARNING — confirmed by both judges

| ID | Finding |
|---|---|
| W-1 | **`APConfig` `@Value` fields have no defaults** (63 of them, zero using `${key:default}`), and `PropertySourcesPlaceholderConfigurer` runs with `ignoreUnresolvablePlaceholders = false`. Adding Cognito properties breaks **startup** on every deployment whose gitignored properties file lacks the keys — so §14 phase 0 is **not** "inert". `marlo-test.properties` (the tracked bootstrap template) is missing from §2 |
| W-2 | **Terms-of-service acceptance is silently dropped for CGIAR users.** `ValidateUserAction.java:87` (`user.setAgreeTerms(agree)`) is the only writer and is reached only from the password branch. `users.agree_terms` would never update for COGNITO users — a compliance regression the "everything after `LoginAction.login()` is identical" framing conceals, because it happens *before* that point |
| W-3 | **§17's LOC budget is internally short.** Components sum to 820 of the ~850 total, leaving ~30 lines for a suite that must cover six SEC-001 rejection classes plus D-3, D-6, D-8 — in a repo with three test classes total |
| W-4 | **The specificity can never be enabled for Global Unit types 2 or 5.** §3 creates `parameters` rows for types (1,3,4) per `AGENTS.md`, but FN-004 explicitly routes type 2 → `centerDashboard.do`. Those units stay on LDAP permanently, so §14 phase 4 can never be reached for them |
| W-5 | **`crpByEmail.do`'s `user` map is built inside the CRP loop** (`CrpByUserEmailAction.java:89`), so a user with zero Global Units returns `user == null` and no `authMode`. §4's contract table presents the field as unconditional. Also needs a constructor/DI change to reach `CustomParameterManager` |
| W-6 | **The UX/UI citation is wrong.** `docs/ux-ui/design.md` §4.1 is "Home — `home/dashboard.ftl`". The login page has **no entry** in the §4 Screen Inventory at all, so §7's palette rule has no screen-level baseline |

---

## WARNING — single judge

| ID | Judge | Finding |
|---|---|---|
| W-7 | A | **`login.error.invalidUserCrp` is not in `global.properties`** — it exists only in `custom/ciat.properties:88`. For every non-CIAT Global Unit the FN-002 failure path renders the raw key |
| W-8 | A | **Neither new action names an interceptor stack**, contradicting TRD §4.3 rule 1 ("every new `.do` action MUST declare an interceptor stack"). `unloggedStack` already exists (`struts.xml:198-203`) and is unmentioned. Leaves `requirements.md` §7's checklist item unclosed |
| W-9 | A | **§11's observability baseline is false.** The membership-failure branch (`LoginAction.java:263-271`) emits **no log line at all**, and the success line (`:281`) logs only the email, not the Global Unit. §11 presents new logging as parity with a baseline that does not exist |
| W-10 | B | **§2 names the wrong stylesheet.** `.login-form-button` lives in `customLogin.css:338/353/358`, not `global.css`, and is styled only as a descendant of `.login-button-container` — so "reuses the existing class, no new spacing" does not hold for a control placed elsewhere |
| W-11 | B | **DD-3's rejection rationale condemns DD-4's own mechanism.** DD-3 rejects "populating the session before authentication" as a fixation surface; DD-4 does exactly that. The real objection (leaking custom parameters to an unauthenticated visitor) goes unstated |
| W-12 | B | **SEC-001/SEC-002 mechanisms are asserted, not specified:** whether state/nonce/verifier are read-and-deleted, whether code single-use is MARLO's or Cognito's, and that Cognito's `token_use: "id"` must be checked (ID and access tokens share a JWKS). A unit test cannot be written against an unstated mechanism |
| W-13 | B | **`marlo-parent/pom.xml` is missing from §2's footprint**, though the same section's closing paragraph and `family.md` both name it. The AWS SDK / JWT addition is invisible to the 11-task budget |
| W-14 | A+B | **Cross-document counts inherited from the parent docs are wrong.** `family.md` says "7 call sites" then enumerates **eight**. `proposal.md` says "9 imports" — actual is **18 imports across 10 files**. And a **second committed `adauth` file-repo exists at `marlo-web/src/main/resources/libs/.../adauth/` with 11 more versions**, unmentioned anywhere, plus `marlo-web/pom.xml:77` declares the dependency. `design.md` itself asserts none of these and is clean; the corrections belong in `proposal.md` and `family.md` before child 2 is specified |

---

## SUGGESTION

| ID | Finding |
|---|---|
| G-1 | DD-8 says "the realm is unchanged" while §2 lists it as Modify — say "the single-realm architecture is unchanged" |
| G-2 | FN-007 (logout) is satisfied by omission; one line in §8 stating `LoginAction.logout()` is untouched and no RP-initiated logout is issued would close it explicitly |
| G-3 | `CognitoTokenValidator` is placed under `data/manager/`, a package holding 456 Hibernate service beans (403 named `*ManagerImpl`). `security/` fits better and keeps DD-5's "pure unit test" property obvious |
| G-4 | `struts.action.extension` is `do,,json` (`struts.xml:21`), so every action is *already* reachable as `.json`. §4's "no new `*.json` path" is true by intent, not by URL space — worth one clarifying sentence |
| G-5 | DD-4 binds state/nonce/verifier to a single session; two concurrent login attempts in two tabs overwrite each other. Decide last-writer-wins vs. a keyed map |
| G-6 | `docs/ux-ui/design.md` §10 rule 5 requires form errors in a `role="alert"` region; the existing `.invalidField` slots (`loginForm.ftl:100-107`) have none. FN-005 messages land in those same slots |
| G-7 | R-D4 is the sharpest finding in the document — consider folding the TRD §8.4 correction (`/api/*` says tokens; `MarloShiroConfiguration.java:113` says `authcBasic`) into the same discovery task |
| G-8 | §5 should name the `input`-result model surface (`struts-home.xml:29-33` binds `model=action`; `loginForm.ftl` reads `crpSession` and `listGlobalUnitTypes`) or the failure page renders broken |
| G-9 | Judge B: `APConfig` has 59–60 getters / 63 `@Value` fields — "~60" is accurate |

---

## What both judges independently confirmed as sound

- The specificity migration shape — types (1,3,4), `category='2'`, `format='1'`, `default_value='false'`, no seeded `custom_parameters`, constant in **both** `APConstants.java` with value == `parameters.key`, correct Flyway naming. Matches `AGENTS.md` exactly.
- `CLAUDE.md` hard rule 3 — both new actions are `.do` in the existing `home` package; no `/api/*`, no new `*.json` path; `struts.action.excludePattern` untouched.
- Hard rules 1, 2, 5, 6, 8, 9, 11, 12 correctly assessed. §6 and §7's "not applicable" reasoning is sound.
- The `hasSpecificities()` constraint — line 6574, session-backed, genuinely useless at authentication time. §9's premise is correct.
- The two-LDAP-round-trip baseline (§12) — verified: two distinct `LDAPService` instantiations per CGIAR login.
- **Authorization is genuinely untouched** — `doGetAuthorizationInfo()` reads only `user_role`/`crp_users`/`getPermission()`, independent of which authenticator ran.
- The enumeration-oracle caveat (§4, R-D3) is factually correct and honestly rather than defensively recorded.
- `DBAuthenticator`, `AuthenticationManager`, `MD5Convert`, `users.password` correctly absent from the footprint — the local *authenticator* is untouched. The residual local-login risk is at the realm prologue (C-7) and in the shared `LoginAction` tail (C-3).
- Verified counts: 16 `adauth` versions in `marlo-data`, three JUnit 4 test classes, ~60 `APConfig` getters, `BaseAction.java:6574`, 11 tasks + 3 reworks = 14 rounds. All §2 file paths exist as stated.

---

## Terminal state

**Not reached — lineage left open by choice.** Round 1 findings are frozen above. All 6 confirmed SEVERE, the severity-contested C-7, both parent-verified findings (V-1, V-2), and every WARNING were corrected in `design.md` revision 2, with backward sweeps into `requirements.md`, `proposal.md`, and `family.md`.

**The user chose *Fix only*, so no re-judgment ran.** The corrections are therefore **unverified by an independent auditor** — the fixes carry the same single-author risk that produced the original six findings. Fix rounds remaining: **2**. Re-running judgment on the delta is available at any time and is the cheapest way to close that gap.
