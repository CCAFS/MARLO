# Login Deadlocks When the Terms Checkbox Is Left Unchecked — Design

**Spec ID:** BUG-LOGIN-TERMS-001
**Status:** Draft
**Owner:** IBD Team — Kenji Tanaka
**Last Updated:** 2026-09-15
**Implements requirements:** FN-001..FN-005, UI-001..UI-003, DA-001, DA-002, SEC-001, SEC-002, NF-001..NF-003
**Touches modules:** marlo-web

---

## 1. Architecture Summary

The gate moves from the browser's native constraint validator — which runs at the wrong moment on one branch
and cannot run at all on the other — into `login.js`, alongside every other login validation. The server-side
Cognito guard stays exactly where it is.

```
                       ┌──────────────────────── step 3 ────────────────────────┐
                       │                                                        │
  LOCAL branch         │   #login-step-password            #login-step-cgiar    │   COGNITO branch
  (cognito OFF)        │   [password] [terms]              [terms]              │   (cognito ON)
                       │        │                               │               │
                       └────────┼───────────────────────────────┼───────────────┘
                                │                               │
                   input#login_next click          #login-cgiar-button click
                                │                               │
                    ┌───────────▼───────────┐       ┌───────────▼───────────┐
                    │ password empty?       │       │                       │
                    │ terms unchecked?  ◄───┼── NEW ┼──►  terms unchecked?  │
                    └───────────┬───────────┘       └───────────┬───────────┘
                       pass     │  fail                pass     │  fail
                                │   └──► wrongData("termsRequired")  ◄──┘
                                │        (no lock taken, form stays usable)
                                │                               │
                    ┌───────────▼───────────┐       ┌───────────▼───────────┐
                    │ lock button + spinner │       │ navigate to           │
                    │ POST validateUser.do  │       │ cognitoLogin.do       │
                    │   ... on success:     │       │   &agree=true         │
                    │   checkValidity()  ◄──┼── NEW │         │             │
                    │   then submit form    │       │         ▼             │
                    └───────────────────────┘       │  server guard STAYS   │
                                                    │  (SEC-001)            │
                                                    └───────────────────────┘
```

Two things are added, not one. The **guards** (FN-001, FN-002) fix this bug. The **`checkValidity()` safety
net** before the programmatic submit (FN-003) fixes the *class* of bug: it guarantees that a submission
refused by constraint validation — for any reason, now or later — releases the lock instead of stranding the
form. Without it, the next `required` attribute anyone adds to this form reopens the same deadlock.

## 2. Module Footprint

### marlo-web

- Modified: `src/main/webapp/WEB-INF/global/pages/loginForm.ftl`
  - remove `required` from `#terms` (UI-002)
  - add one `<p class="invalidField termsRequired hidden">` to the existing message block (UI-001)
- Modified: `src/main/webapp/global/js/login/login.js`
  - guard in the `input#login_next` password branch (FN-001)
  - guard in the `#login-cgiar-button` handler (FN-002)
  - `checkValidity()` safety net before `input#login_formSubmit` (FN-003)
  - `wrongData()` handling for a client-only message type (UI-001, NF-003)
  - `.attr('checked', …)` → `.prop('checked', …)` (FN-005)
- Modified: `src/main/webapp/WEB-INF/global/views/login/login.ftl` — cache-buster (NF-001)
- Modified: `src/main/webapp/WEB-INF/global/pages/error/401.ftl` — cache-buster (NF-001)
- Modified: `src/main/resources/global.properties` — one new key (UI-003)
- Modified: `src/main/java/org/cgiar/ccafs/marlo/action/json/global/ValidateUserAction.java` — DA-001

### marlo-data

- **Not applicable.** No entity, DAO or manager change. `User.agreeTerms` and
  `UserMySQLDAO.saveLastLogin` are used exactly as today.

### marlo-core / marlo-utils

- **Not applicable.**

## 3. Data Model Changes

**Not applicable.** No migration, no column, no index, no backfill (DA-002, and the no-backfill decision in
`requirements.md` §9).

The only persistence change is behavioural, in `ValidateUserAction`:

```java
if (user != null) {
  // BUG-LOGIN-TERMS-001-DA-001: acceptance is RECORDED here, never revoked. Writing the raw parameter
  // meant an attempt made with the box unchecked overwrote a previously granted users.agree_terms = 1
  // with 0 -- and, before this spec, that same attempt then failed to complete at all, so the row was
  // downgraded by a login that never happened. Revocation is not an operation this endpoint offers.
  if (Boolean.TRUE.equals(agree)) {
    user.setAgreeTerms(Boolean.TRUE);
  }
  userManager.saveLastLogin(user);
}
```

`saveLastLogin` stays the write path. It is the `@Transactional` sibling; `saveUser` is not, and a write
through it would not be flushed (the origin spec's §5.4 records this, and it is a standing MARLO constraint).

## 4. API / Action Surface

### Struts actions (.do)

No mapping changes. `login`, `cognitoLogin`, `cognitoCallback` and `validateUser` keep their current
`<action>` elements and interceptor stacks.

The behaviour of one existing action changes internally:

| Route | Action class | Change |
|---|---|---|
| `/validateUser.do` | `ValidateUserAction` | `agree=false` no longer downgrades `users.agree_terms` (DA-001) |
| `/cognitoLogin.do` | `CognitoLoginAction` | **Unchanged.** The guard at `:322-325` and `refuse()` at `:556-563` are untouched (SEC-001, SEC-002) |

### Spring MVC REST

- **Not applicable.**

### New JSON endpoints

- **Not applicable.** No new `.do` JSON path is introduced (AGENTS.md rule 3).

## 5. Frontend Composition

### 5.1 `loginForm.ftl`

Two edits. The checkbox loses `required`:

```ftl
[#-- BUG-LOGIN-TERMS-001-UI-002: no "required" here. Native constraint validation cannot express this
     gate on either branch -- the LOCAL "Log in" click is preventDefault()ed (login.js), so it never
     runs there, and the COGNITO control is type="button", so it never runs there either. What it DID
     do was fire on the programmatic submit at the end of the AJAX round trip and strand the form.
     The gate is now explicit in login.js, i18n-keyed, and identical on both branches --]
<input type="checkbox" name="user.agree" id="terms" class="terms" value="true"> ...
```

and the message block gains one sibling, in the same shape as the rest:

```ftl
[#-- BUG-LOGIN-TERMS-001-UI-001: raised client-side by both step-3 branches before any request or
     redirect is started, so the form is never locked on this path --]
<p class="invalidField termsRequired hidden">[@s.text name="login.error.invalidField.termsRequired"/]</p>
```

Placed immediately after `selectProject`, inside the block `cleanWrongData()` clears, so the "exactly one
message visible" invariant holds for free. It sits with the client-raised messages and before the
server-rendered `cognitoUnavailable` / `cognitoFailed` pair, which are a different category.

### 5.2 `login.js` — the two guards

A single helper, so both branches ask the same question:

```js
// BUG-LOGIN-TERMS-001-FN-001/FN-002: the terms gate, shared by both step-3 branches. It replaces the
// HTML5 "required" that used to sit on #terms -- see loginForm.ftl for why that attribute could not
// work here. Returns true when the attempt may proceed; shows the message and returns false otherwise
function termsAccepted() {
  if($('input#terms').is(':checked')) {
    return true;
  }
  wrongData("termsRequired");
  return false;
}
```

**LOCAL branch** — inside the `input#login_next` click handler, in the password branch, *after* the existing
`voidPassword` check and *before* `checkPassword()`:

```js
} else if(inputPassword.val() == "") {
  wrongData("voidPassword");
} else if(!termsAccepted()) {
  // Deliberately before checkPassword(): that function's beforeSend takes the button lock, and the
  // refusal used to arrive only after the request had returned -- with nothing left to release it
  return;
} else {
  checkPassword(username.val(), inputPassword.val());
}
```

Ordering is intentional: an empty password and unchecked terms together report the password first, which is
the behaviour that ships today for the password field.

**COGNITO branch** — first statement of the `#login-cgiar-button` handler:

```js
$('#login-cgiar-button').on('click', function() {
  // BUG-LOGIN-TERMS-001-FN-002: this control is type="button", so HTML5 "required" never fired for it
  // and the redirect used to carry &agree=false to a server that refuses it under the generic
  // "cognitoFailed" category (SEC-006) -- a dead end the user could not diagnose. The server guard
  // stays (SEC-001); this only stops a knowable refusal from costing a round trip and a lost step
  if(!termsAccepted()) {
    return;
  }
  window.location.href = baseUrl + "/cognitoLogin.do" + ...;
});
```

### 5.3 `login.js` — `wrongData()` handling for a client-only message

`wrongData()` does three things beyond showing text, and two of them are wrong for this message:

1. `$('input.login-input').addClass("wrongData")` paints a red underline on the email and password inputs.
   Neither input is what is wrong here.
2. `username.focus()` is the fallback focus target for any type that is not `voidPassword` /
   `incorrectPassword`. At step 3 the email input sits inside a step carrying `.hidden`, which is
   `display: none` (`global.css:6638`). Focusing a `display: none` element is a **no-op**, so the call does
   not move focus anywhere — the message appears and the user's focus stays wherever it was, with nothing
   pointing at the control they actually need to act on. AC for UI-001 requires focus to land on `#terms`.
3. `postMessageToSlack(...)` fires a **synchronous** `XMLHttpRequest` to a Slack webhook
   (`utils.js:788-802`, `open(..., false)`) on every call, in every environment.

A forgotten checkbox is the most common slip this form can produce. Routing it through (3) would block the UI
thread on a webhook round trip and flood `#marlo-notifications` with no diagnostic value.

`wrongData()` therefore branches on the one new type in three places: skip the input red line, focus
`$('input#terms')` instead of `username`, and return before `postMessageToSlack`. A single
`type == "termsRequired"` test, not a collection — there is one such type, and a one-element list of
"kinds of message" invites the next reader to add to it without re-deriving whether the three behaviours
are right for what they are adding.

The comment carries the reason, not just the intent:

```js
// BUG-LOGIN-TERMS-001: this type names a control the user has not completed yet, not an outcome worth
// reporting. It paints no red line on the email/password inputs (neither is what is wrong), it focuses
// the checkbox rather than the email field (which is inside a display:none step here, so focusing it
// does nothing at all), and it posts nothing to Slack -- postMessageToSlack is a SYNCHRONOUS XHR
// (utils.js:799) and a forgotten checkbox is the most frequent slip this form can produce
```

Nothing else in the function changes, and every existing type keeps its current behaviour byte for byte.

### 5.4 `login.js` — the `checkValidity()` safety net (FN-003)

The success branch of `checkPassword()` is the only place that holds the button lock while handing control to
a native submit. It becomes:

```js
} else {
  // Keep the spinner and the lock while the real form submits and the page navigates away.
  // BUG-LOGIN-TERMS-001-FN-003: but a submit that constraint validation refuses NEVER FIRES, and this
  // branch is what holds the lock -- so a refusal here used to leave the button disabled with its
  // spinner, "Go back" disabled and isSubmitting stuck true, recoverable only by reloading. The
  // guards above make the terms case unreachable; this check makes the whole CLASS unreachable, for
  // whatever required control this form grows next.
  var loginFormElement = $("input#login_formSubmit").closest("form")[0];
  if(loginFormElement && typeof loginFormElement.checkValidity == "function"
      && !loginFormElement.checkValidity()) {
    clearLoginButtonLoading();
    updateNextButtonState();
    wrongData("serverError");
    return;
  }
  $("input#login_formSubmit").click();
}
```

`closest("form")` rather than an id: the form's DOM id is generated by the Struts `<s:form>` theme and is not
worth depending on.

This path is not expected to run. If it does, `serverError` is the honest category — the form is in a state
the JS did not anticipate — and the user can act, which is the whole point.

### 5.5 `login.js` — checkbox state (FN-005)

```js
// BUG-LOGIN-TERMS-001-FN-005: .prop(), not .attr(). jQuery's .attr('checked', …) writes the CONTENT
// attribute, which stops driving the rendered state once the user has clicked the checkbox themselves
// (the element's dirty-value flag is then set). A second lookup in the same page load -- "Go back",
// different email -- could therefore show a tick that contradicts #terms.checked
$('input#terms').prop('checked', data.user.agree === true);
```

Replaces the `if/else` at `login.js:639-643`. `=== true` keeps a `null` from `CrpByUserEmailAction:110`
(`usrDB.getAgreeTerms()`) from rendering as checked.

### 5.6 Cache-busting (NF-001)

Both entry points bump together:

- `WEB-INF/global/views/login/login.ftl:4`
- `WEB-INF/global/pages/error/401.ftl:4`

`?20260902v1` → `?20260915`. The repository's own history used a `vN` suffix, but the team's
`cache-bust-check.sh` gate accepts only `?YYYYMMDD` or `?YYYYMMDD-N`, so a `vN` value is reported STALE
however fresh the date is — which trains people to ignore the gate. The plain date form satisfies both. `401.ftl` is not a copy of the login page; it includes the same
`loginForm.ftl` and is a genuine way into this flow (an unauthorized access lands there with
`loginHeadlineKey = "login.headline.unauthorized"`), so a stale `login.js` there ships the bug.

## 6. Persistence & Phase Replication Plan

**Not applicable.** Authentication writes no phased data. `users.agree_terms` and `users.last_login` are
phase-independent columns on a non-phased table; there is no `phase.getNext()` recursion and no past-phase
concern (same finding as the origin spec's §6).

## 7. Validation & Save Pipeline

**Not applicable in its `Action.validate()` + `Validator` form.** This is the authentication path, not a
section save, and MARLO's save-pipeline pattern binds critical *section* saves. No `Validator` class exists or
is added for login.

The equivalent gate is two-layered and is stated here for the record:

| Layer | Where | Purpose |
|---|---|---|
| Client | `login.js` `termsAccepted()` | Usability. Refuses a knowable-bad attempt before any lock or round trip |
| Server | `CognitoLoginAction:322-325` | Compliance. Unchanged; the authoritative refusal for the Cognito path |
| Server | `ValidateUserAction` (DA-001) | Compliance. Records acceptance, never revokes it |

The local password path has no equivalent server-side terms guard today, and this spec does not add one —
see Open Risks.

## 8. Permissions & Edit Gates

No change. `login` keeps its stack, `cognitoLogin` and `cognitoCallback` keep `cognitoUnloggedStack`
(`struts-home.xml:40-46`), `validateUser` keeps its JSON stack. No `canEdit*` gate is involved.

## 9. Specificity / Feature-Flag Strategy

**Not applicable — the fix ships unconditionally for all Global Units.**

No new `parameters` / `custom_parameters` key, and no `APConstants` constant. The correction must hold on
**both** sides of the existing Cognito specificity; gating it behind a flag of its own would leave one of the
two defects live wherever the new flag was off.

## 10. Integration Points

- **Slack `#marlo-notifications`** — the only integration touched, and only by *not* calling it for the new
  message type (§5.3).
- CLARISA, CGSpace, BI, AI services, S3, Pusher — **not applicable.**
- **AWS Cognito** — reached only through the unchanged `cognitoLogin.do` → `/oauth2/authorize` redirect. No
  change to the authorize URL, state, nonce or PKCE handling.

## 11. Observability

- `CognitoLoginAction`'s existing `LOG.info("Cognito login refused: the terms were not accepted")` stays. It
  should become rare once the client guard lands; if it does **not** become rare, that is the signal worth
  having — it means the client guard is being bypassed.
- No new log line is added for the client-side refusal. It is a UI affordance, not an event.
- No new metric, no audit column.

## 12. Performance & Scalability

Net negative cost, which is unusual for a fix:

- One `POST /validateUser.do` avoided per refused local attempt (which also avoids a password check against
  the realm).
- One full page load plus one Cognito round trip avoided per refused CGIAR attempt.
- One **synchronous** Slack XHR avoided per refused attempt (§5.3) — the largest single saving, because it
  blocks the UI thread.

## 13. Security Considerations

- **The server guard is not removed** (SEC-001). `cognitoLogin.do` is unauthenticated and every parameter it
  reads is attacker-controlled; the client check is an affordance for the user, never a control.
- **No new oracle** (SEC-002). `termsRequired` is raised from the state of a checkbox in the user's own
  browser. It is never selected by a server response and cannot distinguish account states. `refuse()`'s
  SEC-006 collapse is untouched.
- **Dropping `required` weakens nothing.** It was never a security control: the Cognito branch already
  bypassed it entirely, and a crafted POST always did.
- **DA-001 narrows a write.** After the change, `validateUser.do` can set `agree_terms` to `TRUE` and never to
  `FALSE` — strictly fewer state transitions reachable from an endpoint that authenticates by password only.

## 14. Backwards Compatibility & Rollout

- No migration, so no deploy ordering constraint and no dual-running window.
- Rollout is a normal `staging` deploy. Both fixes are live the moment the bumped `login.js` is fetched.
- The cache-buster bump (NF-001) is the compatibility-critical step: a cached `login.js` against a new
  `loginForm.ftl` yields a page with no `required` **and** no client guard — that is, a form that lets an
  unaccepted local login through. The two files must ship together, and NF-001 is what makes that true for
  returning browsers.
- Rollback is a revert; see `task.md`.

## 15. Decision Records

### ADR-BUG-LOGIN-TERMS-001-1 — Remove `required`, gate in JS
- **Decision:** Drop the HTML5 `required` from `#terms` and validate in `login.js`.
- **Rationale:** The attribute cannot work on either branch. LOCAL suppresses it on the user's click and then
  triggers it on a programmatic submit after the request; COGNITO never submits the form at all. Its message
  is also browser-supplied, violating the i18n rule.
- **Alternatives considered**, including keeping the browser's own bubble, which was re-examined on
  2026-09-15 and rejected again with reasons:
  - (a) `formnovalidate` on `#login_formSubmit` — silences the attribute without replacing it, so an
    unaccepted local login would go straight through.
  - (b) keep `required` and call `form.reportValidity()` on the click — validates the **whole** form, so the
    terms gate becomes coupled to every other required control, including `user.email` and `user.password`
    sitting in `display:none` steps. An invalid control inside a hidden step is not focusable, and Chrome
    then aborts *silently* — the precise failure shape this spec exists to remove. It also needs per-step
    branching, since one button serves all three steps.
  - (c) keep `required` and use `terms.setCustomValidity(i18nText)` + `terms.reportValidity()` — the only
    variant that puts MARLO's own text in a native bubble, and scoped to one element so (b)'s coupling does
    not arise. Rejected because `setCustomValidity` leaves the control **permanently invalid** until a
    `change` handler clears it; forget that handler, or lose it in a refactor, and the form becomes
    unsubmittable — the deadlock again, in a new costume.
  - (d) moving the checkbox outside the form — makes the COGNITO asymmetry permanent.
- **What the native route does not buy:** it saves one i18n key and one `<p>`, and nothing else. Both (b) and
  (c) still need explicit JS at both activation sites, because `#login-cgiar-button` is `type="button"` and
  never submits this form. What they cost is a message that auto-dismisses after a few seconds, a second
  error system alongside the eight `invalidField` messages already on this screen, and — in (b) — a bubble in
  the browser's UI language rather than MARLO's.
- **Status:** Accepted.

### ADR-BUG-LOGIN-TERMS-001-2 — Add a `checkValidity()` safety net, not just a guard
- **Decision:** Release the button lock when a programmatic submit is refused by constraint validation.
- **Rationale:** The guards make *this* deadlock unreachable. They do nothing about the next `required`
  attribute added to this form. The deadlock's severity — a login screen that only F5 clears — does not
  justify relying on nobody ever doing that again.
- **Alternatives considered:** Guards alone; a blanket timeout releasing the lock after N seconds (rejected:
  hides the failure instead of reporting it).
- **Status:** Accepted.

### ADR-BUG-LOGIN-TERMS-001-3 — Client-only messages bypass the Slack webhook
- **Decision:** `termsRequired` shows its message but posts nothing to Slack and paints no input red line.
- **Rationale:** `wrongData()`'s Slack call is a synchronous XHR. A forgotten checkbox is a routine slip, not
  an incident; routing it through a blocking webhook would both degrade the UI and bury real notifications.
- **Alternatives considered:** Accept the noise (rejected: high volume by construction); make
  `postMessageToSlack` asynchronous (a real improvement, but it touches `utils.js` and every caller in MARLO —
  out of this spec's blast radius, recorded in Open Risks).
- **Shape:** a direct `type == "termsRequired"` test rather than a one-element list of "client-only" types.
  A collection with one member reads as a category and invites additions that have not been checked against
  all three behaviours it switches off.
- **Status:** Accepted.

### ADR-BUG-LOGIN-TERMS-001-4 — `setAgreeTerms` becomes record-only
- **Decision:** `ValidateUserAction` writes `agree_terms` only on `TRUE`.
- **Rationale:** An endpoint whose purpose is to validate a password should not be able to erase a compliance
  record as a side effect — least of all on an attempt that then fails to complete.
- **Alternatives considered:** Keep the unconditional write and rely on the client guard to make `false`
  unreachable (rejected: a crafted POST reaches it directly, and the guard is a UI affordance by design).
- **Status:** Accepted.

### ADR-BUG-LOGIN-TERMS-001-5 — `.prop()` over `.attr()` for the checkbox
- **Decision:** Set the checkbox from `crpByEmail.do` with `.prop('checked', …)`.
- **Rationale:** `.attr()` writes the content attribute, which stops driving the rendering once the element's
  dirty-value flag is set by a user click. The bug is only reachable on a second lookup within one page load,
  which is exactly what "Go back" invites.
- **Status:** Accepted.

## 16. Open Risks

- **Risk: the local password path has no server-side terms guard.** The Cognito path has one
  (`CognitoLoginAction:322-325`); the local path's only enforcement was the `required` attribute this spec
  removes, so after the change a crafted POST to `login.do` can complete a local login without acceptance —
  as it already could before, since `required` is a client-side attribute. The change does not widen the hole,
  but it does remove the last thing that resembled a control. *Mitigation:* raised as Q1-adjacent for the
  reviewers; adding a `LoginAction`-side guard is a candidate follow-up spec, not a silent addition here.
- **Risk: `postMessageToSlack` is a synchronous XHR on the login critical path** for every existing message
  type (`emailRequired`, `invalidEmail`, `emailNotFound`, `voidPassword`, `incorrectPassword`,
  `serverError`, `selectProject`). This spec routes around it; it does not fix it. *Mitigation:* record as a
  separate enhancement; it touches `utils.js` and every caller across MARLO.
- **Risk: rows already downgraded to `agree_terms = 0`** by this defect since 2026-08-03 stay downgraded
  (requirements Q2). *Mitigation:* those users re-accept on their next login, which is the behaviour a
  non-accepted row is supposed to produce. A read-only report is offered if PMU wants the number.
- **Risk: no automated test covers step 3's rendering.** The origin spec recorded the same gap, and MARLO's
  test baseline (3 JUnit files, all runs `-DskipTests`) does not change here. *Mitigation:* the verification
  in `task.md` is explicit, scripted and manual, and states what a green build does **not** prove.
