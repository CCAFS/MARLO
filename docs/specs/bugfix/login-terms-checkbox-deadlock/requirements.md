# Login Deadlocks When the Terms Checkbox Is Left Unchecked — Requirements

**Spec ID:** BUG-LOGIN-TERMS-001
**Status:** Draft
**Owner:** IBD Team — Kenji Tanaka
**Reviewers:** PMU lead, QA lead, Tech lead
**Last Updated:** 2026-09-15
**Related PRD sections:** docs/prd.md — authentication and first-contact experience
**Related UX/UI Design sections:** docs/ux-ui/design.md — login screen, form controls, error surfaces
**Related TRD sections:** docs/trd/trd.md §security model (authentication paths)
> Those are the filenames on `staging-cognito-impl`, where this spec lands. On `staging` they are still
> `docs/system-design/design.md` and `docs/detailed-design/detailed-design.md`; the rename travels with
> the Cognito cutover.
**Companion ai-context docs:** reports/ai-context/frontend-composition-map.md
**Origin spec:** docs/specs/archive/2026-09-07-changes--migrate-ad-authentication-to-cognito--auth-flow/ (CHG-COGNITO-AUTH-001)

---

## 1. Overview

The terms-and-conditions checkbox on the login form (`loginForm.ftl:161`) carries the HTML5 `required`
attribute. Neither of the two authentication branches handles that attribute correctly, and they fail in
**opposite directions**. With the Cognito specificity **off**, leaving the box unchecked puts the login form
into a permanently unusable state that only a page reload clears. With the Cognito specificity **on**, the
same omission does not block anything client-side; the server refuses the attempt and returns a message that
never mentions the checkbox.

Both defects are reachable by any user on the first screen of the product, and neither produces a MARLO error
message. This spec covers the single shared correction: take the acceptance gate away from the browser's
native constraint validation and give it the same explicit, i18n-keyed treatment every other login validation
already has.

## 2. Problem Statement

### 2.1 Cognito OFF (local password branch) — unrecoverable UI lock

`input#login_next` is a Struts `<s:submit>` (`loginForm.ftl:172`), but its click handler calls
`e.preventDefault()` (`login.js:123-124`). Native constraint validation therefore never runs on the click the
user actually performs. The observed sequence on the password step is:

1. The user clicks **Log in**. `preventDefault()` suppresses HTML5 validation.
2. `checkPassword()` enters `beforeSend` (`login.js:725-735`): `isSubmitting = true`, `input#login_next`
   gets `disabled` plus the `is-loading` spinner, and the "Go back" control is disabled too.
3. `validateUser.do` answers `loginSuccess: true`.
4. The success branch reaches `$("input#login_formSubmit").click()` (`login.js:777`). That control **is** a
   real submit, so constraint validation finally runs — and `#terms`, still unchecked, aborts the submission.
5. Nothing in that branch calls `clearLoginButtonLoading()`. `isSubmitting` stays `true` forever, and
   `updateNextButtonState()` early-returns while it is (`login.js:905-907`).

The user is left looking at a spinning, disabled **Log in** button with a disabled **Go back** link and no
MARLO message. Checking the box afterwards changes nothing, because the button no longer responds. Only F5
recovers the form.

The form does own an escape hatch — `showEmailStep()` calls `clearLoginButtonLoading()`, so "Go back" would
restore everything. `beforeSend` closes it with **two independent locks**, neither of which the success branch
releases: the control is given `disabled` (`login.js:735`), and its handler returns early on `isSubmitting`
(`login.js:222-224`). The way out exists, is on screen, and is bolted shut twice.

Reproduced in isolation against the same markup shape: the form's `submit` event never fires, an `invalid`
event fires on `user.agree`, and `form.checkValidity()` returns `false`.

**Chrome does show its own bubble, and that is worth stating precisely rather than claiming silence.** The
probe confirmed it: focus moves from `login_next` to `terms` across the programmatic `.click()`, which is
what Chrome does when it renders the "Please check this box if you want to proceed" tooltip. So the user is
not told *nothing* — they are told once, by the browser, in English regardless of their MARLO locale, at the
end of the AJAX round trip rather than on the click they made, and the tooltip auto-dismisses after a few
seconds. What is left behind after it fades is a dead button, a dead "Go back", and no on-page trace of why.
An earlier revision of this section said no bubble was drawn; that was an artifact of screenshotting seconds
after the event, not a fact about the browser.

**Root cause of the regression.** The lock is newer than the defect. Before commit `c28a12c643`
(`:bug: fix(auth) A2-2106`, 2026-08-03), the spinner and the `disabled` attribute were applied **only when the
checkbox was checked** — that is, only on the path where the submit would actually go through. That accidental
guard is what kept the unchecked case recoverable. `c28a12c643` removed the condition for a legitimate reason
(its own comment: the conditional lock "let a second attempt be fired on top of the first one") and, in doing
so, converted a recoverable annoyance into a permanent deadlock.

### 2.2 Cognito ON (CGIAR branch) — no client gate, and a misleading refusal

`#login-cgiar-button` is `<button type="button">` (`loginForm.ftl:181`). It never submits the form, so
`required` cannot fire, and its handler performs no check of its own: it navigates straight to
`cognitoLogin.do` carrying `&agree=false` (`login.js:242-247`).

The server does catch it — `CognitoLoginAction.java:322-325` refuses with `login.error.cognitoNotEligible` —
but `refuse()` (`CognitoLoginAction.java:556-563`) collapses every account-shaped key into the single public
category `cognitoFailed`, as SEC-006 of the origin spec requires. The user lands back on `login.ftl`
(`struts-home.xml:48-56`), at step 1, with:

> "We could not complete CGIAR sign-in. Please try again or use your MARLO password."

Nothing points at the checkbox, and the selected Global Unit is lost. A user who did not notice the box the
first time will not notice it the second time either; the loop is opaque and self-perpetuating.

The origin spec's design §5.4 explicitly anticipated that `required` "cannot fire" on this path and added the
server guard for exactly that reason. What it did not examine is that on the *local* path `required` fires at
the wrong moment.

### 2.3 The three defects compound into a self-sustaining loop

Taken singly, each defect below reads as minor. Walking the flow shows they chain, and that the chain has an
entrance and no exit.

A user who has already accepted the terms receives the checkbox **pre-ticked** from `crpByEmail.do`, and never
meets the bug. So the question that decides this spec's severity is how a healthy account joins the population
that does. It joins like this:

```
  account with agree_terms = 1, logging in normally
                    |
                    |  user clicks the checkbox twice (ticks, unticks)
                    v
  [1] the checkbox's dirty-value flag is now set
                    |
                    |  .attr('checked', true) can no longer restore it  (SS 2.4)
                    |  showEmailStep() does not reset it either
                    v
  [2] the user reaches step 3 with the box UNTICKED
                    |
                    |  Log in  ->  validateUser.do { agree: false }
                    v
  [3] agree_terms DEMOTED 1 -> 0 in the database      (SS 2.5)
      and the submit is then blocked                  (SS 2.1)
                    |
                    |  F5, the only way out
                    v
  [4] next login: crpByEmail.do now returns agree=false
      -> the box arrives unticked from the start
                    |
                    +--------> back to [2], every time
```

**One stray click converts a working account into one that deadlocks on every login**, until the user works
out unaided that the checkbox is the cause. Nothing in the flow restores `agree_terms` to `1` except a
successful login, and a successful login is precisely what is blocked.

This is why the three fixes are one fix. Repairing only the deadlock leaves the machinery that puts accounts
into the broken state fully intact — those users would simply stop hanging and start being refused, on every
attempt, forever.

The checkbox is also the **only** control in this form whose state survives step navigation. `showEmailStep()`
clears the password field, the cards, the button and the messages; it never touches `#terms.checked`. And it
is written from three uncoordinated places: the server render (always unticked), `crpByEmail.do`, and the
user.

### 2.4 The checkbox state is set through an ineffective API

`login.js:639-643` uses `$('input#terms').attr('checked', …)` to pre-tick the box from `data.user.agree`
(sourced at `CrpByUserEmailAction.java:110`). `.attr()` writes the **content attribute**, which stops driving
the rendered state once the element's dirty-value flag is set — that is, once the user has clicked the
checkbox even once. This is step [1] of the loop above.

### 2.5 A silently revoked compliance record

`ValidateUserAction.java:135-138` runs `user.setAgreeTerms(agree)` unconditionally whenever the password
validates, before any of the above. With the box unchecked, every blocked attempt **overwrites a previously
recorded `users.agree_terms = 1` with `0`**. The acceptance the user granted on an earlier session is
destroyed by an attempt that then fails to complete.

This is step [3] of the loop: the write that makes the broken state stick.

### 2.6 Dead parameter

`name="user.agree"` is inert in the POST to `login.do`. `User` (marlo-data) exposes `agreeTerms`, not `agree`,
and `LoginAction` reads neither. The attribute's only live effect today is the `required` that produces §2.1.

## 3. In-Scope Requirements

### Functional

- BUG-LOGIN-TERMS-001-FN-001 — On the local password branch, the system MUST refuse to start the
  `validateUser.do` request when the terms checkbox is unchecked, and MUST surface a MARLO field error instead.
  The refusal MUST happen before any button-locking state is applied.
- BUG-LOGIN-TERMS-001-FN-002 — On the Cognito branch, the system MUST refuse to navigate to `cognitoLogin.do`
  when the terms checkbox is unchecked, and MUST surface the same MARLO field error as FN-001.
- BUG-LOGIN-TERMS-001-FN-003 — After any refused login attempt, the login form MUST remain fully operable:
  the **Log in** / **Sign in with CGIAR** control enabled and free of its spinner, the **Go back** control
  enabled, and `isSubmitting` cleared. No user-reachable path may require a page reload to recover.
- BUG-LOGIN-TERMS-001-FN-004 — Checking the box after a refusal and retrying MUST complete the login, with no
  intervening reload.
- BUG-LOGIN-TERMS-001-FN-005 — The rendered state of the terms checkbox MUST agree with its `checked` property
  for every account resolved by `crpByEmail.do`, including a second and subsequent lookup within one page load.

### UI

- BUG-LOGIN-TERMS-001-UI-001 — The error MUST render through the existing `<p class="invalidField …">`
  mechanism in `loginForm.ftl:138-148`, selected by its own second CSS class, so exactly one login message is
  visible at a time (the invariant `cleanWrongData()` maintains).
- BUG-LOGIN-TERMS-001-UI-002 — The `required` attribute MUST be removed from the terms checkbox. MARLO MUST NOT
  depend on a native browser validation bubble for this gate: its text is browser-supplied, outside MARLO's
  i18n, and it appears — when it appears at all — at the end of an AJAX round trip rather than on the click.
- BUG-LOGIN-TERMS-001-UI-003 — The message text MUST be i18n-keyed, per AGENTS.md. No literal string in the FTL
  or in `login.js`.

### Data

- BUG-LOGIN-TERMS-001-DA-001 — `ValidateUserAction` MUST NOT downgrade an existing `users.agree_terms = 1` to
  `0`. Acceptance is recorded; it is not revoked through this endpoint.
- BUG-LOGIN-TERMS-001-DA-002 — No schema change. `users.agree_terms` is used exactly as today.

### Security

- BUG-LOGIN-TERMS-001-SEC-001 — The server-side guard at `CognitoLoginAction.java:322-325` MUST remain. The new
  client-side check is a usability improvement layered in front of it, never a replacement: `cognitoLogin.do`
  is unauthenticated and its parameters are attacker-controlled.
- BUG-LOGIN-TERMS-001-SEC-002 — The refusal MUST NOT become a new account oracle. The new message fires on a
  client-observable condition (the state of a checkbox in the user's own browser) and MUST NOT be reachable
  from any server response that distinguishes account states. SEC-005 / SEC-006 of the origin spec are
  unaffected: `refuse()`'s collapse behaviour is not modified.

### Non-Functional

- BUG-LOGIN-TERMS-001-NF-001 — Both FTLs that load `login.js` MUST have their cache-busting parameter bumped:
  `login.ftl:4` and `error/401.ftl:4`. `401.ftl` includes the same `loginForm.ftl` and is a real entry point to
  this flow, not a copy.
- BUG-LOGIN-TERMS-001-NF-002 — The fix MUST NOT reintroduce the double-submit `c28a12c643` closed. The button
  lock stays unconditional for attempts that actually start a request.
- BUG-LOGIN-TERMS-001-NF-003 — The error message MUST be announced to assistive technology the way the existing
  login messages are; the correction MUST NOT regress the focus behaviour of either step-3 branch.

## 4. Out-of-Scope

- Redesigning the terms-acceptance model (for example, moving acceptance out of login into a first-run
  interstitial). This spec restores correct behaviour of the control that exists.
- Removing the dead `name="user.agree"` binding (§2.4). It is documented here for traceability; changing the
  posted parameter name touches `LoginAction`'s parameter surface and belongs to its own change.
- Any modification to `refuse()`'s SEC-006 collapse, to the Cognito state/nonce/PKCE handling, or to
  `CognitoCallbackAction`'s own `setAgreeTerms(Boolean.TRUE)` write.
- The recaptcha behaviour after three failed passwords.
- Translating the new key into `custom/*.properties`. Program bundles are not loaded on the login page at
  all; see Q3 and the Decision Log.

## 5. Personas Affected

- **Every persona.** This is the login screen; Cluster Coordinator, QA Reviewer, PMU and Admin all pass
  through it.
- **CGIAR-authenticated users in a Cognito-enabled Global Unit** (§2.2) — currently the only population that
  can complete a sign-in flow while having declined the terms, were the server guard ever bypassed, and the
  population that sees the misleading `cognitoFailed` message.
- **MARLO Support** — receives the tickets this defect generates, and currently has no message text to
  triage from.

## 6. Acceptance Criteria

**AC for FN-001, FN-003, UI-001:**
- Given a Global Unit with the Cognito specificity **off**, and a user on the password step with a valid
  password typed and the terms checkbox unchecked,
- When they activate **Log in**,
- Then `validateUser.do` MUST NOT be called,
- And the `invalidField` message for unaccepted terms MUST be the only login message visible,
- And **Log in** MUST remain enabled with no spinner, and **Go back** MUST remain enabled.

**AC for FN-002, FN-003:**
- Given a Global Unit with the Cognito specificity **on**, and a CGIAR user on the `#login-step-cgiar` step
  with the terms checkbox unchecked,
- When they activate **Sign in with CGIAR**,
- Then the browser MUST NOT navigate to `cognitoLogin.do`,
- And the same `invalidField` message MUST be shown,
- And the step MUST remain on `#login-step-cgiar` with its control enabled.

**AC for FN-004:**
- Given either branch immediately after the refusal above,
- When the user checks the terms checkbox and activates the control again,
- Then the login MUST proceed normally (request sent / redirect performed), with no page reload in between.

**AC for FN-005:**
- Given a user who has manually toggled the terms checkbox during the current page load,
- When they use **Go back**, enter a different email, and reach step 3 again,
- Then the rendered checkbox MUST match the new account's `agree` value from `crpByEmail.do`,
- And `document.getElementById('terms').checked` MUST equal what is rendered.

**AC for DA-001:**
- Given a user whose `users.agree_terms` is already `1`,
- When any login attempt is made with the checkbox unchecked,
- Then `users.agree_terms` MUST still be `1` afterwards.

**AC for SEC-001:**
- Given a request issued directly to `cognitoLogin.do?...&agree=false`, bypassing the browser form,
- When the action runs,
- Then it MUST still refuse, and MUST still render the collapsed `cognitoFailed` category.

**AC for UI-002:**
- Given the rendered login page in any branch,
- When the DOM is inspected,
- Then `#terms` MUST NOT carry the `required` attribute,
- And `document.getElementById('login').checkValidity()` MUST be `true` with the box unchecked and the other
  visible fields filled.

**AC for NF-001:**
- Given the deployed build,
- When `login.ftl` and `error/401.ftl` are served,
- Then both MUST reference `login.js` with a cache-busting value newer than `20260902v1`.

## 7. Constitutional Compliance Checklist

- [x] Phase replication: **Not applicable.** Authentication writes no phased data; `users.agree_terms` is a
      phase-independent column on a non-phased table (same finding as the origin spec's §6).
- [x] Save validation: **Not applicable in the `Action.validate()` + `Validator` sense.** This is the
      authentication path, not a section save. The equivalent gate — a client-side check plus the existing
      server guard — is specified in `design.md`.
- [x] Permissions: no action mapping changes; `cognitoUnloggedStack` and the `login` stack are untouched.
- [x] Specificity: no new flag. The fix is unconditional and must hold on **both** sides of the existing
      Cognito specificity — that is the point of the spec.
- [x] Migrations: **Not applicable.** No schema change (DA-002).
- [x] i18n: one new key in `global.properties`; no hardcoded user-facing strings (UI-003).
- [x] License header: **Not applicable.** No new Java file.
- [x] Code style: Checkstyle passes; 2-space indent; 120-char limit on the touched Java file.
- [x] REST: **Not applicable.** No `/api/*` change.
- [x] Audit: **Not applicable.** `users` is not an `IAuditLog` entity in this path.
- [x] Dependency floors preserved: no dependency change.
- [x] Branching: feature branch from `staging`; merge target `staging`.
- [x] Cache-busting: NF-001 covers both referencing FTLs.

## 8. Open Questions

- **Q1 — CLOSED 2026-09-15.** The control stays **enabled**; activating it with the box unticked shows the
  i18n message and moves focus to the checkbox. See the Decision Log for the two alternatives and why they
  were not taken.
- **Q2.** `users.agree_terms` is currently reset to `0` for every user who has hit this defect since
  2026-08-03. Does PMU want a one-off report of affected rows, or is the forward-only fix (DA-001) enough?
  A backfill is *not* proposed: MARLO cannot distinguish "reset by this bug" from "genuinely never accepted".
- **Q3 — CLOSED 2026-09-15, by mechanism rather than by precedent.** The new key goes in `global.properties`
  only. `InternationalitazionFileInterceptor` adds a program's `custom/<acronym>.properties` bundle **only
  inside `if (session.containsKey(APConstants.SESSION_CRP))`** (`:101`), and at login time the session has no
  CRP — the same emptiness the origin spec recorded for `hasSpecificities` (its design §474). A
  program-specific bundle is therefore never loaded on `/login.do`. The `login.agree` entries that already sit
  in `custom/aicrra.properties`, `custom/aiccra3.properties`, `custom/alliance.properties` and others are
  unreachable dead weight for exactly this reason, and the two keys added most recently to this screen
  (`login.error.invalidField.selectProject` and the `cognito*` pair) live in `global.properties` alone.

## 9. Decision Log

- 2026-09-15 — Treat both branches in one spec rather than two — Rationale: they are the same control failing
  in two directions, and a fix applied to only one branch leaves the login screen internally inconsistent. The
  origin spec's §5.4 already established that the checkbox is shared by both step-3 blocks.
- 2026-09-15 — Remove `required` rather than move the checkbox or add `formnovalidate` — Rationale: native
  constraint validation cannot express this gate correctly here. It is suppressed on the click the user makes
  (`preventDefault`) and fires on a programmatic click at the end of an AJAX round trip, where its bubble is
  both late and un-translated. Every other login validation in this form is already explicit and i18n-keyed;
  this one becomes consistent with them.
- 2026-09-15 — Keep the server-side Cognito guard (SEC-001) — Rationale: `cognitoLogin.do` is unauthenticated.
  The origin spec added that guard deliberately; removing it because a client check now exists would reopen
  the exact compliance regression it was written to close.
- 2026-09-15 — No backfill of `users.agree_terms` — Rationale: the column cannot distinguish a bug-induced
  reset from a genuine non-acceptance, and writing `1` into rows that may never have accepted would
  manufacture a compliance record. Forward-only correction, with Q2 left open for a read-only report.
- 2026-09-15 — Leave `name="user.agree"` in place (Out-of-Scope) — Rationale: it is inert, and renaming it
  changes `LoginAction`'s accepted parameter surface, which is a wider blast radius than this bug warrants.
- 2026-09-15 — The new i18n key ships in `global.properties` only (closes Q3) — Rationale: not a style
  preference. `InternationalitazionFileInterceptor:101` gates the program bundle on a session CRP that does
  not exist yet at login, so `custom/*.properties` is unreachable from this screen. Adding the key there would
  create an override nobody can ever read.
- 2026-09-15 — The gate shows a message on activation; the control is never disabled (closes Q1) — Rationale:
  it matches how every other validation in this same form already behaves (`voidPassword`, `emailRequired`,
  `selectProject` all leave the control live and explain themselves), and a grey control at step 3 — after the
  user has supplied an email, a Global Unit and a password — reads as "something is wrong" while withholding
  what. **Alternative B (disable the control)** was rejected on cost and reach, not on taste: `#login-cgiar-button`
  has no disabled-state management at all today (`updateNextButtonState` targets `input#login_next` alone), so
  B means building state machinery for a second control plus a `change` binding on the checkbox, and a disabled
  button typically leaves the tab order, so keyboard and screen-reader users get no explanation at all (NF-003).
  B's genuine virtue — a state gate cannot be bypassed by a future activation path that forgets to call the
  guard — is real but is bought in the wrong layer: any client-side gate yields to a crafted POST. That
  robustness belongs to the server-side question recorded in `design.md` §16. **Alternative C (both)** was
  rejected because its message would fire unpredictably: a disabled button does not reliably dispatch a click,
  a fact this very file already documents at `login.js:180-186`, so C is B plus code that runs only sometimes.
- 2026-09-15 — FN-005 (`.prop()`) and DA-001 are core to this spec, not adjacent cleanups — Rationale: the flow
  analysis in §2.3 shows they are the entrance and the latch of a self-sustaining loop. Fixing the deadlock
  alone leaves accounts still being pushed into the broken state; they would stop hanging and start being
  refused on every attempt instead.
- 2026-09-15 — **Branch split withdrawn at the user's direction: all of it ships on `staging-cognito-impl`.**
  The earlier plan cut Group A from `staging` so the live deadlock would not wait on the Cognito cutover.
  The owner chose the single-branch route instead. Consequence, recorded so nobody rediscovers it in
  production triage: **the deadlock, the `agree_terms` revocation and the `.attr()` defect are live on
  `staging` today and will stay live there until the Cognito feature merges.** The evidence that they are
  live on `staging` is in `task.md` §0; it was verified, not assumed, and is kept for whoever decides the
  release order.
