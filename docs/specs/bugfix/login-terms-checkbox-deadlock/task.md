# Login Deadlocks When the Terms Checkbox Is Left Unchecked — Tasks

**Spec ID:** BUG-LOGIN-TERMS-001
**Status:** In Progress — code complete 2026-09-15, awaiting manual QA (T09, T11)
**Owner:** IBD Team — Kenji Tanaka
**Last Updated:** 2026-09-15
**Implements design:** docs/specs/bugfix/login-terms-checkbox-deadlock/design.md
**Branching:** one branch — `staging-cognito-impl`. See §0 for the decision and what it costs.
**Target merge:** `staging-cognito-impl`, reaching `staging` with the Cognito cutover.

---

## 0. Where this ships, and what that costs

**Decision (owner, 2026-09-15): everything ships on `staging-cognito-impl`.** T01–T08 and T10 are one
delivery on one branch. The section below is kept because the evidence behind it still governs the release
order, and whoever schedules that release needs it.

The two defects do not live on the same branch. Verified against `origin/staging` on 2026-09-15:

| Defect | `origin/staging` | `staging-cognito-impl` |
|---|---|---|
| `required` on `#terms` | **present** (`loginForm.ftl:114`) | present (`:161`) |
| Unconditional button lock + stranded `formSubmit` | **present** (`login.js:556`, `:605`) | present (`:728`, `:777`) |
| `user.setAgreeTerms(agree)` revocation | **present** (`ValidateUserAction.java:87`) | present (`:136`) |
| `.attr('checked', …)` dirty-flag defect | **present** (`login.js:483-485`) | present (`:639-643`) |
| `CognitoLoginAction` / `#login-cgiar-button` | absent | present |

**The consequence, stated plainly:** four of the five defects are live on `staging` right now, and this
branch choice means they stay live there until the Cognito feature merges. Users on the current release keep
hitting the deadlock, and keep having `users.agree_terms` silently downgraded, for that whole window.

An earlier revision of this plan cut a Group A from `staging` to close that window independently. It was
withdrawn by the owner. If the Cognito cutover slips, **reviving that split is the mitigation** — the
`staging`-side line numbers above are recorded precisely so it can be revived without re-deriving them.

## 1. Execution Context

- **Java:** 17. `marlo-parent/pom.xml` is the verification source.
- **Run script:** `scripts/run-marlo-java17.sh`. It kills any `cargo:run`, deletes
  `marlo-{utils,data,web}/target` and rewrites `marlo-dev.properties` — never run it while another agent or
  build is active (CLAUDE.md § Concurrency).
- **Spring profile:** local dev; credentials bootstrapped from `marlo-test.properties`. Never commit
  `marlo-${profile}.properties`.
- **Database:** the local MySQL copy of the AICCRA schema is the verification target for DA-001. Query it
  directly rather than inferring the write.
- **Verification gates:** `mvn -q install -DskipTests -pl marlo-web -am` for compile; Checkstyle through the
  invocation the `marlo-verify` skill specifies — `mvn checkstyle:check` throws in this checkout, so the
  jar is run directly and diffed against HEAD. Failures print complete and verbatim; `-q` suppresses passing
  noise only.
- **A green test run proves nothing here.** MARLO has 3 JUnit files repo-wide, no Surefire config, and every
  run script builds `-DskipTests`. No task below claims test-based verification.

## 2. Pre-flight Checklist

- [ ] `requirements.md` and `design.md` reviewed and moved to **Approved**.
- [ ] Q1 and Q3 are both closed (see `requirements.md` §8 and §9) — no question blocks implementation.
      Q2 (report of downgraded `agree_terms` rows) is an operational follow-up, not a gate.
- [ ] `git fetch` and confirm `origin/staging` still matches the §0 table — if the login files moved, re-verify
      the line numbers before editing.
- [ ] Working on `staging-cognito-impl` (§0), with the tree clean before the first edit.
- [ ] Confirm no other agent or build is running before any measurement command.
- [ ] Record the pre-change value of `users.agree_terms` for the test account(s), for T07's verification.

---

## 3. Task List

### The shared tasks (both branches of the login form)

#### BUG-LOGIN-TERMS-001-T01 — Remove `required`, add the message element

- **Depends on:** —
- **Module:** marlo-web
- **Files touched:**
  - `webapp/WEB-INF/global/pages/loginForm.ftl` (modified)
- **Implements:** UI-001, UI-002
- **Constitutional checks:**
  - No literal user-facing string; the new `<p>` resolves an i18n key (T02 supplies it).
  - The new element joins the existing `invalidField` block so `cleanWrongData()` clears it — no new
    show/hide mechanism.
- **Tests:** none authored (no test harness for FTL rendering; see §1).
- **Done when:**
  - `#terms` carries no `required` attribute.
  - `<p class="invalidField termsRequired hidden">` sits with the other message elements.
  - The comment on the removal states *why* the attribute could not work, per design §5.1.
- **Verification:**
  - Render the login page and confirm in the DOM: `document.getElementById('terms').required === false`,
    and `document.querySelector('.invalidField.termsRequired')` is present and hidden.

#### BUG-LOGIN-TERMS-001-T02 — Add the i18n key

- **Depends on:** —
- **Module:** marlo-web
- **Files touched:**
  - `resources/global.properties` (modified)
- **Implements:** UI-003
- **Constitutional checks:**
  - English only. Placed with the other `login.error.invalidField.*` keys (around `global.properties:1613`).
  - `custom/*.properties` deliberately NOT touched. `InternationalitazionFileInterceptor:101` loads a
    program bundle only when the session carries a CRP, which it does not at login, so a key added there is
    unreachable (Q3, closed).
- **Tests:** none.
- **Done when:** `login.error.invalidField.termsRequired` exists and reads as an instruction, matching the
  voice of its neighbours ("Please enter a password." / "Please select a project to continue.").
- **Verification:** the rendered message on the page is the key's text, not the key.

#### BUG-LOGIN-TERMS-001-T03 — `termsAccepted()` helper + LOCAL branch guard

- **Depends on:** T01, T02
- **Module:** marlo-web
- **Files touched:**
  - `webapp/global/js/login/login.js` (modified)
- **Implements:** FN-001
- **Constitutional checks:**
  - The guard sits **before** `checkPassword()`, so no lock is ever taken on a refused attempt.
  - Ordering preserved: `voidPassword` still reports first when both are wrong.
- **Tests:** none authored.
- **Done when:**
  - `termsAccepted()` exists and is the only place the checkbox is read for validation.
  - The password branch returns without calling `checkPassword()` when it returns false.
- **Verification:** with the box unchecked, the browser Network panel shows **no** request to
  `validateUser.do` after activating **Log in**.

#### BUG-LOGIN-TERMS-001-T04 — `wrongData()` handling for a client-only message

- **Depends on:** T03
- **Module:** marlo-web
- **Files touched:**
  - `webapp/global/js/login/login.js` (modified)
- **Implements:** UI-001, NF-003, and design ADR-3
- **Constitutional checks:**
  - Every existing message type keeps its current behaviour unchanged — red line, focus target and Slack post.
  - `postMessageToSlack` is **not** modified; it is only bypassed for the new type.
- **Tests:** none authored.
- **Done when:**
  - `termsRequired` focuses `#terms`, paints no `wrongData` class on the login inputs, and issues no Slack XHR.
  - The comment names the synchronous-XHR reason (`utils.js:799`), not just the intent.
- **Verification:**
  - Network panel: activating **Log in** with the box unchecked produces **zero** requests, including to
    `hooks.slack.com`.
  - `document.activeElement` is `#terms` after the refusal.
  - Regression: trigger `emailRequired` and `incorrectPassword` and confirm both still post to Slack and still
    paint the red line.

#### BUG-LOGIN-TERMS-001-T05 — `checkValidity()` safety net before the programmatic submit

- **Depends on:** T03
- **Module:** marlo-web
- **Files touched:**
  - `webapp/global/js/login/login.js` (modified)
- **Implements:** FN-003, and design ADR-2
- **Constitutional checks:**
  - Does **not** reintroduce the double-submit closed by `c28a12c643` (NF-002): the lock still applies
    unconditionally to attempts that actually start a request.
  - Uses `closest("form")`, not a Struts-generated id.
- **Tests:** none authored.
- **Done when:** a submit refused by constraint validation releases `isSubmitting`, the spinner, the button
  and the "Go back" control, and shows `serverError`.
- **Verification:**
  - Temporarily re-add `required` to `#terms` in the running page
    (`document.getElementById('terms').required = true`), bypass the T03 guard from the console by ticking and
    un-ticking after the click is dispatched, and confirm the form recovers instead of stranding.
  - Restore the page state afterwards; this is a console-only probe, not a code change.

#### BUG-LOGIN-TERMS-001-T06 — Checkbox state via `.prop()`

- **Depends on:** —
- **Module:** marlo-web
- **Files touched:**
  - `webapp/global/js/login/login.js` (modified)
- **Implements:** FN-005
- **Constitutional checks:** none beyond style.
- **Tests:** none authored.
- **Done when:** the `if/else` at the `data.user.agree` site is replaced by a single
  `.prop('checked', data.user.agree === true)`, and a `null` from `CrpByUserEmailAction:110` renders unchecked.
- **Verification:**
  - Reach step 3 for a user with `agree_terms = 1`, click the checkbox twice (setting its dirty flag), use
    **Go back**, enter an account with `agree_terms = 0`, reach step 3 again.
  - The rendered tick and `document.getElementById('terms').checked` must agree, and must reflect the second
    account.

#### BUG-LOGIN-TERMS-001-T07 — `ValidateUserAction` records acceptance, never revokes it

- **Depends on:** —
- **Module:** marlo-web
- **Files touched:**
  - `java/org/cgiar/ccafs/marlo/action/json/global/ValidateUserAction.java` (modified)
- **Implements:** DA-001
- **Constitutional checks:**
  - Write still goes through `userManager.saveLastLogin(user)` — the `@Transactional` path. `saveUser` is not
    transactional and the write would not be flushed.
  - 2-space indent, 120-char limit, braces on the same line, mandatory block on the `if`.
  - No GPL header task: the file is not new.
- **Tests:** none authored — no harness (§1). Verified against the database instead.
- **Done when:** `setAgreeTerms` is reached only when `Boolean.TRUE.equals(agree)`.
- **Verification:**
  1. `SELECT agree_terms FROM users WHERE email = '<test account>';` → confirm it is `1`.
  2. Log in with the box **unchecked** (which T03 now refuses client-side), then POST `validateUser.do`
     directly with `agree=false` to exercise the server path.
  3. Re-query: `agree_terms` MUST still be `1`.
  4. Repeat with a `0` account and `agree=true`: it MUST become `1`.

#### BUG-LOGIN-TERMS-001-T08 — Bump the cache-busting parameter on both entry points

- **Depends on:** T03, T04, T05, T06
- **Module:** marlo-web
- **Files touched:**
  - `webapp/WEB-INF/global/views/login/login.ftl` (modified)
  - `webapp/WEB-INF/global/pages/error/401.ftl` (modified)
- **Implements:** NF-001
- **Constitutional checks:**
  - **Both** files. `401.ftl` includes the same `loginForm.ftl` and is a real entry point, not a copy.
  - This is the compatibility-critical step (design §14): a cached `login.js` against the new
    `loginForm.ftl` yields a page with neither `required` nor the guard.
- **Tests:** none.
- **Done when:** both lines reference a parameter newer than the branch's current value
  (`?20260803v2` on `staging`, `?20260902v1` on `staging-cognito-impl`), **in the format
  `cache-bust-check.sh` accepts** — `?YYYYMMDD` or `?YYYYMMDD-N`. The repository's historic `vN` suffix is
  reported STALE by that gate no matter how fresh the date is.
- **Verification:** load `/login.do` and a 401 page in a browser with a warm cache; confirm both fetch the new
  `login.js` URL and that the guard is active on both.

#### BUG-LOGIN-TERMS-001-T09 — Manual QA pass, Cognito OFF

- **Depends on:** T01–T08
- **Module:** marlo-web
- **Files touched:** none (verification only; findings recorded in this file)
- **Implements:** verification of FN-001, FN-003, FN-004, FN-005, DA-001, UI-001, UI-002, NF-001, NF-002
- **Constitutional checks:** none.
- **Tests:** the manual matrix in §5.
- **Done when:** every row of §5's "Cognito OFF" block passes and is recorded here with a note.
- **Verification:** see §5.

---

## 3.1 Verification log — 2026-09-15

Branch `staging-cognito-impl` at `fc2d49791d`.

| Task | State | Note |
|---|---|---|
| T01 | done | `required` removed from `#terms`; `<p class="invalidField termsRequired hidden">` added after `selectProject` |
| T02 | done | `login.error.invalidField.termsRequired` added after `selectProject`. `custom/*.properties` untouched — Q3 |
| T03 | done | `termsAccepted()` added; LOCAL guard after the `voidPassword` branch, before `checkPassword()` |
| T04 | done | `wrongData()` skips the input red line, focuses `#terms`, returns before `postMessageToSlack` for this type only |
| T05 | done | `checkValidity()` net before `#login_formSubmit.click()`, via `closest("form")` |
| T06 | done | `.attr('checked', …)` → `.prop('checked', data.user.agree === true)` |
| T07 | done | `setAgreeTerms` reached only on `Boolean.TRUE.equals(agree)`; write still through `saveLastLogin` |
| T08 | done | Both FTLs bumped `?20260902v1` → `?20260915` |
| T10 | done | COGNITO guard is the first statement of the `#login-cgiar-button` handler. `CognitoLoginAction` **not touched** (SEC-001) |
| T13 | done | **New, found by walking the real flow.** `change` handler on `#terms` clears the terms message when the user ticks the box |
| T09 | **partial** | Cognito-OFF walked on the running stack; see below. Blocked only on a successful login |
| T11 | **mostly done** | Cognito-ON walked after the flag was enabled locally; see below |

One helper, two call sites — `login.js` `termsAccepted()` with the LOCAL guard and the COGNITO guard — so the
branches cannot drift apart.

**Gates run**

- **Clean compile** — `target/classes` wiped, then `mvn -q install -DskipTests -pl marlo-web -am`.
  **Exit code 0, no output.** The wipe is not optional: an incremental `mvn compile` reports SUCCESS on code
  that does not compile.
- **Checkstyle** — `checkstyle.sh --baseline` on `ValidateUserAction.java`: **HEAD 0, tree 0, delta 0.**
  (`mvn checkstyle:check` cannot run in this checkout at all — plugin 2.9.1 against checkstyle 8.18 throws
  `NoSuchMethodError`. That is a plugin mismatch, not a code finding.)
- **Cache-busting** — `cache-bust-check.sh`: `login.ftl:4` and `error/401.ftl:4` both `ok ?20260915`. The
  further hits the script reports in `loginForm.ftl` are **comments mentioning `login.js`**, not script
  references; that file loads no asset.
- **JS syntax** — `node --check` on `login.js`: OK. A parse check, not a behaviour test.

**Walked on the running stack (localhost:8080, 2026-09-15).** The instance was already up and confirmed
to be serving `login.js?20260915` with `termsAccepted` present, so the destructive run script was not used.

| Scenario | Result |
|---|---|
| Page renders | `#terms.required === false`; the `termsRequired` element exists with the right text; 11 `invalidField` elements |
| `form.checkValidity()`, password filled, box **ticked** (happy path) | **`true`** — T05's net is a no-op here. This was the one regression that would have broken every login, and it is cleared |
| `form.checkValidity()`, password filled, box **unticked** | **`true`**, zero invalid controls — the exact condition that used to be `false` and stranded the submit |
| **A1** — click Log in, box unticked | Only `termsRequired` visible (1 message); focus on `#terms`; button enabled, no spinner; "Go back" enabled; `isSubmitting === false`; no red line; **zero network requests** — no `validateUser.do`, no Slack |
| Guard with box ticked | `termsAccepted()` returns `true`; the flow proceeds |
| **A7 / FN-005** | `crpByEmail.do` returned `agree=true` for `marloAdmin` (`users.agree_terms = 1`) and `.prop()` ticked the box correctly — T06 verified against real data |
| T13 regression | Ticking the box clears only the terms message; an `incorrectPassword` message survives toggling the checkbox |

**What the walk found that no isolated test could.** At step 1 the form is invalid because `user.password` is
`required`, empty and inside a `display:none` step — the "not focusable" shape ADR-1 rejects option (b) over,
confirmed live. And T13: the user ticks the box, correcting exactly what the message asks for, and the message
stayed on screen, because `$('input.login-input').on("change", …)` does not reach `#terms`.

**Cognito-ON walked (2026-09-15).** The owner enabled the specificity locally: `users.is_cgiar_user = 1`
for `ktanaka` (id 2491, `agree_terms = 1`), and `custom_parameters` rows for `cognito_auth_active`
(parameter 390, type 3) on AICCRA (45) and AICCRA_III (47), both `value='true'`. Cognito is genuinely
configured in `marlo-dev.properties` (pool, domain, `CGIAR-AzureAD` provider, callback on localhost).

| Scenario | Result |
|---|---|
| Branch composition | Entering the CGIAR user's email reached **`#login-step-cgiar`** with `isCgiarUser === true`, `cognitoEnabled === true`, the AICCRA card carrying `data-cognito-enabled="true"` |
| Origin-spec FN-001 still holds | **Zero** `input[type=password]` in the DOM on this branch — `.remove()`, not hidden |
| **B1** — Sign in with CGIAR, box unticked | **URL unchanged**; no request to `cognitoLogin.do`; only `termsRequired` visible with the right text; focus on `#terms`; step still `#login-step-cgiar`; control still enabled |
| **B3 / SEC-001** — crafted `GET cognitoLogin.do?...&agree=false` | HTTP 200 rendering `login.ftl` with `invalidField cognitoFailed` **not** carrying `hidden`; `cognitoUnavailable` correctly hidden; the granular `cognitoNotEligible` class **never appears** |
| **B2 server half** — same request with `agree=true` | **HTTP 302** to the real authorize URL with `state`, `nonce`, `code_challenge_method=S256` and `identity_provider=CGIAR-AzureAD`. The redirect was deliberately **not followed**, so no credential was ever presented to AWS |
| **B4 / SEC-002** — a refusal for a *different* reason (non-CGIAR account, `agree=true`) | The rendered message markup is **byte-identical** to B3's. The two reasons are indistinguishable from outside, exactly as SEC-006 requires |

**Still not verified, and why.**

- **T07 (`ValidateUserAction`) — now executed.** The owner performed two real password logins
  (`ktanaka` 15:57:59, `marloSAdmin` 15:57:02, both on the LOCAL branch — AICCRA's `custom_parameters` row
  had been set back to `'false'`, so the CGIAR account was not diverted to Cognito and SEC-005 did not
  block it). Both rows show `last_login` advanced to those timestamps and `agree_terms` still `1`. That
  proves the action runs without error, that `saveLastLogin` persists, and that the happy path leaves the
  compliance column alone.

  The structural question the happy path cannot answer — is `setAgreeTerms` really behind the guard, and is
  `saveLastLogin` really outside it — was settled against the compiled class instead:

  ```
  332: invokevirtual java/lang/Boolean.equals
  335: ifeq 345                                  <- false jumps past the write
  342: invokevirtual User.setAgreeTerms          <- only reachable when TRUE
       (345 = jump target)
  350: invokeinterface UserManager.saveLastLogin <- after it: always runs
  ```

  `setAgreeTerms` sits inside the branch, `saveLastLogin` after the jump target. The `ifeq` **is** the
  non-revocation, and `last_login` was never put at risk.

  What is still not exercised at runtime is `agree=false` alongside a valid password. That is deliberate and
  no longer reachable: the client guard blocks an unticked submission, so the UI cannot produce it. T07 is
  now defence-in-depth against a crafted POST, and the bytecode above is the evidence for it.
- **B2's AWS half** — the CGIAR-AzureAD sign-in itself was not completed. No credential was entered.
- **A2 / A5 / A6** (completing a login), **A9** (warm cache), **A10** (double submit) — all need a
  successful authentication.
- **A11** — the full message regression sweep was exercised against the extracted functions, not through
  each real server response.

**T13 is now built and served.** `mvn -q install -DskipTests -pl marlo-web -am` was re-run (exit 0) and the
rebuilt `login.js` copied into the live cargo deployment; the served file is byte-identical to the source.
No restart was needed and the running instance was not disturbed — `login.js` is a static webapp resource,
and it was the only artifact that differed. (Checked before doing it: the deployed `marlo-web` jar already
carried T07's `Boolean.equals` guard immediately before `setAgreeTerms`, and every touched FTL already
matched source.)

Re-verified end to end against the **deployed** code, with no injected handler:

| Scenario | Result |
|---|---|
| B1 on the rebuilt asset | Only `termsRequired` visible; **no navigation**; focus on `#terms` |
| **T13** — the user ticks the box with a real click | The message clears; checkbox ends checked |
| T13 scoping | With an `incorrectPassword` message showing, toggling the checkbox twice leaves it **intact** |

---



### The Cognito-branch task

#### BUG-LOGIN-TERMS-001-T10 — `#login-cgiar-button` guard

- **Depends on:** T03 (the `termsAccepted()` helper must already exist)
- **Module:** marlo-web
- **Files touched:**
  - `webapp/global/js/login/login.js` (modified)
  - `webapp/WEB-INF/global/views/login/login.ftl` (modified — cache-buster, again, for this branch)
  - `webapp/WEB-INF/global/pages/error/401.ftl` (modified — same)
- **Implements:** FN-002
- **Constitutional checks:**
  - **`CognitoLoginAction` is not touched** (SEC-001). The guard at `:322-325` and `refuse()`'s SEC-006
    collapse at `:556-563` stay exactly as they are.
  - No new server-side message, no new error category — SEC-002.
  - Adds a call site only; it must not duplicate `termsAccepted()`.
- **Tests:** none authored.
- **Done when:** the handler returns before `window.location.href` is assigned when the box is unchecked.
- **Verification:** with the box unchecked, activating **Sign in with CGIAR** produces no navigation, the
  Network panel shows no request to `cognitoLogin.do`, and the step stays on `#login-step-cgiar` with its
  control enabled.

#### BUG-LOGIN-TERMS-001-T11 — Manual QA pass, Cognito ON

- **Depends on:** T10
- **Module:** marlo-web
- **Files touched:** none (verification only)
- **Implements:** verification of FN-002, FN-003, FN-004, SEC-001, SEC-002
- **Constitutional checks:** none.
- **Tests:** the manual matrix in §5.
- **Done when:** every row of §5's "Cognito ON" block passes and is recorded here with a note.
- **Verification:** see §5. The SEC-001 row is mandatory and must be exercised with a hand-crafted request,
  not through the UI.

---

### Documentation

#### BUG-LOGIN-TERMS-001-T12 — ai-context reconciliation

- **Depends on:** T09, T11
- **Module:** —
- **Files touched:** none expected.
- **Status:** **Not applicable, verified.** No file under `reports/ai-context/` references `loginForm.ftl` or
  `login.js` (checked 2026-09-15), and this change alters no routing, validation, replication or composition
  contract those documents record. Recorded here so the absence is a finding rather than an omission.
- **Shared-file discipline:** `docs/trd/trd.md`, root `CLAUDE.md` / `AGENTS.md` and `.agents/*.md` are **not**
  edited by this spec. The archived origin spec
  (`docs/specs/archive/2026-09-07-changes--migrate-ad-authentication-to-cognito--auth-flow/design.md` §5.4)
  is an archive and stays as written; this spec's §2.2 carries the correction instead.

---

## 4. Dependency Graph

```
T01 (loginForm.ftl)  --+
T02 (i18n key)       --+--> T03 (helper + LOCAL guard) --+--> T04 (wrongData handling) --+
                                                         +--> T05 (checkValidity net)  --+
                                                         +--> T10 (COGNITO guard)       --+
T06 (.prop checkbox)  -----------------------------------------------------------------  +
T07 (ValidateUserAction) --------------------------------------------------------------  +
                                                                                          |
                                                                                          v
                                                                                   T08 (cache-bust)
                                                                                          |
                                                                            +-------------+-------------+
                                                                            v                           v
                                                                   T09 (QA, cognito OFF)      T11 (QA, cognito ON)
                                                                            +-------------+-------------+
                                                                                          v
                                                                                T12 (docs: N/A, recorded)
```

T03 must land before T10: the COGNITO guard is a call site of the helper T03 introduces, not a second copy of
it. T06 and T07 are independent of the guard work. T08 gates both QA passes, because a stale cached `login.js`
invalidates either one.

## 5. Testing Plan

### Unit

**Not applicable, and stated deliberately.** MARLO has 3 JUnit 4 files repo-wide, one with its only test body
commented out, no Surefire configuration, no JaCoCo, and every run script builds `-DskipTests`. Authoring the
first-ever test for `login.js` (a global-scope jQuery file with no module boundary and no JS test runner in
the build) is a larger change than this fix. **No task above claims test-based verification**, and a green
build must not be reported as evidence that this bug is fixed.

### Integration

- `validateUser.do` reached directly with `agree=false` for an account whose `agree_terms` is `1` — the row is
  unchanged (T07).
- `cognitoLogin.do` reached directly with `agree=false` — still refused, still collapsed to `cognitoFailed`
  (T10, SEC-001).

### Manual matrix — the primary evidence

**Cognito OFF** (any Global Unit without the specificity; verify on `staging`):

| # | Scenario | Expected |
|---|---|---|
| A1 | Password step, valid password, box **unchecked**, activate **Log in** | `termsRequired` message shown; **no** `validateUser.do` request; **no** Slack request; button enabled, no spinner; "Go back" enabled |
| A2 | Continue from A1: tick the box, activate **Log in** again | Login completes. No reload in between (FN-004) |
| A3 | Password step, empty password **and** box unchecked | `voidPassword` shown — one message only, not two |
| A4 | Box unchecked, press **Enter** in the password field | Same as A1. No stranded state |
| A5 | Account with `agree_terms = 1`; complete a login with the box **ticked** | Row stays `1`; `last_login` updated |
| A6 | Account with `agree_terms = 0`; complete a login with the box ticked | Row becomes `1` |
| A7 | Tick/untick the box, **Go back**, different email, return to step 3 | Rendered tick matches `#terms.checked` and matches the new account (FN-005) |
| A8 | Reach the login form via a 401 page | Identical behaviour to A1–A2 (NF-001 — the `401.ftl` bump) |
| A9 | Warm cache from before the fix, then reload | New `login.js` fetched; guard active (NF-001) |
| A10 | Two rapid activations of **Log in** with the box ticked | One `validateUser.do` request only (NF-002 — no double-submit regression) |
| A11 | Trigger `emailRequired`, `invalidEmail`, `voidPassword`, `incorrectPassword`, `serverError`, `selectProject` | Each still paints the input red line, focuses its usual target, and posts to Slack (T04 regression) |

**Cognito ON** (a CGIAR user in a Global Unit with the specificity; verify on `staging-cognito-impl`):

| # | Scenario | Expected |
|---|---|---|
| B1 | `#login-step-cgiar`, box **unchecked**, activate **Sign in with CGIAR** | `termsRequired` shown; **no** navigation; no `cognitoLogin.do` request; control enabled; step unchanged |
| B2 | Continue from B1: tick the box, activate again | Redirect to Cognito proceeds and sign-in completes (FN-004) |
| B3 | Hand-crafted `GET /cognitoLogin.do?email=…&globalUnitId=…&agree=false` | Still refused; `login.ftl` renders `cognitoFailed`; server log carries "the terms were not accepted" (SEC-001) |
| B4 | Same as B3 with an ineligible account | Indistinguishable output from B3 (SEC-002 / SEC-006 — the collapse is intact) |
| B5 | Mixed-membership CGIAR user: a Cognito unit and a non-Cognito unit | Each card routes to its own branch, and the guard fires on whichever step-3 block is shown |
| B6 | COGNITO step reached, **Go back**, then a LOCAL unit selected | The password field is restored and the LOCAL guard applies (no regression of the origin spec's restore path) |

### Regression (manual, QA team)

- Login as each persona (Cluster Coordinator, QA Reviewer, PMU, Admin) and confirm the dashboard loads.
- Logout → login round trip.
- The recaptcha path: three wrong passwords, then confirm the button remains under recaptcha's control and the
  terms guard does not interfere with it.

### Accessibility

- Keyboard only: reach and toggle `#terms` with Space, activate the step-3 control with Enter, and confirm
  focus lands on `#terms` after a refusal on both branches.
- Confirm the new message is announced the way the existing `invalidField` messages are — matching them is
  the requirement (NF-003), not exceeding them.

### Non-functional

- No load profile. This path only removes work (design §12).

## 6. Operational Steps

### Migration deploy

**Not applicable.** No Flyway migration (DA-002).

### Specificity rollout

**Not applicable.** No new flag. The existing Cognito specificity is untouched, and the fix must hold on both
of its sides.

### Configuration / environment

None. No property, no env var, no Cognito setting.

### CDN / cache

The only operational step that matters: confirm after deploy that both `login.do` and a 401 page serve the
bumped `login.js` URL. If `baseUrlCdn` fronts a CDN with its own TTL, confirm the new URL is being fetched
rather than assuming the bump was enough.

### BI / AI coordination

**Not applicable.** No BI column, no AI service surface.

### Notifications

- MARLO Support should be told the deadlock is fixed, so open tickets described as "the login button spins
  forever" can be closed against this change.
- Expect a **drop** in `#marlo-notifications` volume after T04 (client-only messages no longer post). That is
  intended, not an outage of the notifier.

## 7. Rollback Plan

### Code

- Revert the merge commit on `staging-cognito-impl` and redeploy.
- **The cache-buster must be reverted with the rest.** Reverting `login.js` while leaving a bumped parameter
  serves the old script under a new URL, which is harmless; the dangerous direction is the other one —
  reverting `loginForm.ftl` (restoring `required`) while a browser still holds the new `login.js`. Revert as a
  single unit.

### Data

- `ValidateUserAction`'s change (T07) is a narrowing: it writes strictly fewer values than before. Rolling it
  back restores the ability to revoke `agree_terms`, which is the defect — so if a rollback is ever needed for
  an unrelated reason, T07 should be kept if it can be isolated.
- No migration to undo.

### Specificity

Not available as a kill switch — deliberately, since the fix is unconditional (design §9). Rollback is code
revert only.

## 8. Definition of Done

- [ ] Every acceptance criterion in `requirements.md` §6 verified, and the verifying row in §5 recorded here.
- [ ] Merged to `staging-cognito-impl`.
- [ ] `mvn -q install -DskipTests -pl marlo-web -am` passes from a clean `target/classes` — an incremental
      `mvn compile` reports SUCCESS on code that does not compile, so the clean step is not optional.
- [ ] Checkstyle passes on `ValidateUserAction.java` via the working invocation (`marlo-verify` skill), diffed
      against HEAD.
- [ ] Both cache-busting parameters bumped and confirmed served (T08).
- [ ] The Slack-volume drop observed and confirmed as intended, not as a broken notifier.
- [ ] `users.agree_terms` verified against the local AICCRA schema copy for all four T07 cases.
- [ ] SEC-001 verified with a hand-crafted request, not through the UI (B3).
- [ ] No test-based claim made anywhere in this file or in the PR description (§5).
- [ ] Q2 answered or explicitly deferred with an owner. Q1 and Q3 already closed.
- [ ] Commits follow the MARLO convention, with the `[SPEC:docs/specs/bugfix/login-terms-checkbox-deadlock]`
      prefix on any commit touching this folder.
- [ ] Promoted to `main` via the release pipeline, and production verified: log in once with the box
      unchecked and confirm the form stays usable.
