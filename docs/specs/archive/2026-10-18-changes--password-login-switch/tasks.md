# Password Login Switch — Tasks

**Spec ID:** `CHG-PWD-SWITCH-001`
**Status:** Approved — Kenji Tanaka, 2026-10-16
**Last Updated:** 2026-10-18
**Requirements:** [`requirements.md`](./requirements.md) · **Design:** [`design.md`](./design.md) (Judgment Day APPROVED, [`judgment.md`](./judgment.md))
**Branch:** `A2-2631-Implement-Center-Email-OTP-Authentication`
**Depends on:** `CHG-OTP-LOGIN-001` T21 (done, `fcce4e7ebd`)
**Budget (design §18):** 8 tasks · ~1,300 LOC · 10 review rounds — `/akili-execute` stops and escalates when exceeded

## Environment (every task that runs the app)

| Item | Value |
|---|---|
| Database | Throwaway copy `aiccradb_otp_probe` of `aiccradb_actsave`. Never write to `aiccradb_actsave` or `aiccradb1`–`4` |
| Code key | `otp.hmac.secret` in `marlo-dev.properties` (present since 2026-10-16); blank it, or override with an empty `OTP_HMAC_SECRET`, to test the last resort |
| Mail | Local SMTP sink on `127.0.0.1:2525` through `EMAIL_HOST`/`EMAIL_PORT` and the backup pair |
| Start | `scripts/run-marlo-java17.sh` with the environment overrides above; never while another agent builds |
| Cleanup | Stop the app and the sink, drop the copy, delete scratch credentials; `marlo-dev.properties` unchanged except for the key already there |

---

### CHG-PWD-SWITCH-001-T01 — Constants and migration (DA-001, DA-002, DA-003, FN-001)

- **Status:** done [x] · **Size:** S · **Depends on:** none · **Module:** marlo-data, marlo-web
- **Requirements:** FN-001, DA-001, DA-002, DA-003; AC-1, AC-14
- **Design:** §3 (migration and fixed texts), §9
- **Files:** both `config/APConstants.java` (`PASSWORD_LOGIN_ACTIVE`); new
  `V2_6_0_<YYYYMMDD>_<HHMM>__AddPasswordLoginSwitch.sql`; `probes/` migration check (extend `ac21-migration.sh` or a new
  script in this spec folder).
- **Scope:** seed the key for existing types 1–4 (`NOT EXISTS`, format 1, default `'true'`, category 2, DA-002 text);
  `UPDATE` the two `system_otp_*` descriptions to the design §3 texts. DML only, idempotent, no assumed unit id. Do not
  edit `V2_6_0_20261009_1025` or `V2_6_0_20261015_1418`.
- **Tests:** migration twice on the throwaway copy, once on a database without units 45/47, once without unit types;
  texts compared byte for byte with `requirements.md` DA-002 and `design.md` §3; every other `parameters` /
  `custom_parameters` row unchanged (checksum).
- **Falsifier:** change one word of a seeded text → the byte comparison goes red; drop `NOT EXISTS` → second run
  duplicates rows → red.
- **Red run:** n/a — data task; the falsifiers above are the red evidence.
- **Disqualifier:** a run against any database whose name lacks `probe`; a comparison that normalizes whitespace on
  both sides.
- **Consumers:** `CHG-OTP-LOGIN-001` `probes/ac21-migration.sh` reads DA-002 texts of that spec, which T08 rewords —
  re-run it after T08.
- **Review:** `full` — a migration touching auth settings.
- **Done:** migration gate green; constants equal in both files; compile gate.
- **Skills:** `marlo-migration`, `marlo-verify`.

### CHG-PWD-SWITCH-001-T02 — The method rule (design §1, DD-1, DD-2)

- **Status:** done [x] · **Size:** S · **Depends on:** none · **Module:** marlo-web
- **Requirements:** FN-003, FN-004, FN-005, FN-006, FN-007, FN-010 (rule part); SEC-003; AC-9
- **Design:** §1, DD-1, DD-2
- **Files:** new `security/otp/SignInMethodRule.java` (GPL header); its test.
- **Scope:** a pure function of the six inputs (key configured, Cognito account in G, password switch value, code
  switch value, exclusion value, identifier) → `{cognito, code, password, needsEmail}`; `isPasswordSwitchOn` = on
  unless the trimmed value equals `false` ignoring case.
- **Tests:** exhaustive enumeration of all combinations (key × Cognito × code switch × exclusion hit × password switch ×
  identifier shape): every active-member case has ≥1 method or `needsEmail`; Cognito accounts never get password; at
  most two methods; the AC-3…AC-7 rows asserted by name; `isPasswordSwitchOn` for `true`, `false`, `FALSE`, ` false `,
  `""`, `null`, `0`.
- **Falsifier:** drop the "password off ⇒ code" override → the "everything else off" row asserts no method → red;
  parse the switch with `OtpDomainPolicy.isSwitchOn` → `""`/`null` read as off → red.
- **Red run:** write the test first against a stub returning today's T21 behavior; it must fail on the override rows.
- **Disqualifier:** a test whose expected table is computed by the code under test.
- **Consumers:** none yet (new class).
- **Review:** `full` — the rule decides who can authenticate.
- **Done:** tests green, falsifiers red, Checkstyle delta 0.
- **Skills:** `tdd`, `marlo-verify`.

### CHG-PWD-SWITCH-001-T03 — Code endpoints follow the rule (DD-3, DD-4)

- **Status:** done [x] · **Size:** M · **Depends on:** T01, T02 · **Module:** marlo-web
- **Requirements:** FN-002, FN-004, FN-005, FN-006; SEC-003, SEC-004; OPS-002 (ERROR memory); AC-4, AC-5, AC-6, AC-8
- **Design:** §2 `OtpEligibility`, DD-3, DD-4 (reversion challenge), §11, §13 "Neutral override"
- **Files:** `security/otp/OtpEligibility.java`; `action/home/OtpRequestCodeAction.java` (step 4 only);
  `OtpEligibilityTest`, `OtpRequestCodeActionTest`, `OtpVerifyCodeActionTest`, `OtpStrutsPackageTest`.
- **Scope:** key list gains the password key; managers injected back; `isOffered` admits the override; `isEligible`
  resolves the unit's Cognito flag before the lookup on password-off units and refuses a Cognito account after
  `existActiveCrpUser`; `methodsFor(identifier, units, cognitoAccountByUnit)` returning per unit
  `{otpEnabled, passwordEnabled}` with the query always run (early exit at `:159-161` removed); once-per-unit ERROR set
  for the last resort. *(Corrected 2026-10-21: the line reference is pre-change; in the current `OtpEligibility.methodsFor` the unconfigured early exit is gone, the specificity query runs at `:383` whatever the key, and the only early return left is for an empty unit list.)*
- **Tests:** override issues a code with the code switch off and with an excluded domain; Cognito account refused under
  the override with the neutral `sent` and a decoy row; same query count for known, unknown and Cognito accounts on a
  password-off unit; **replacing the four T21 locks** (design P-16): on a password-on unit, zero calls to the Cognito
  resolver and no `is_cgiar_user` read (counting doubles); Spring scan registers the managers; verify re-check refuses a
  Cognito account issued under a later-changed setting.
- **Falsifier:** read the Cognito flag only after the lookup → query-count test red; call the resolver on password-on
  units → zero-call test red; keep the early exit → `methodsFor` with no key returns no `passwordEnabled` → red.
- **Red run:** the override tests fail on today's `isOffered` with `domainNotEnabled`.
- **Disqualifier:** a neutrality test that asserts only the response body, not the issued rows and query counts.
- **Consumers:** `OtpEligibilityTest` (32), `OtpRequestCodeActionTest` (49), `OtpVerifyCodeActionTest` (48),
  `CrpByUserEmailActionTest` (14), `OtpStrutsPackageTest` (8) — design §17.
- **Review:** `full` — security path, partial reversion of T21.
- **Done:** full suite with `-am` green; falsifiers red; Checkstyle delta 0.
- **Skills:** `tdd`, `marlo-verify`, `error-handling-patterns`.

### CHG-PWD-SWITCH-001-T04 — Pre-auth lookup reports both flags (DD-6)

- **Status:** done [x] · **Size:** S · **Depends on:** T03 · **Module:** marlo-web
- **Requirements:** FN-002, FN-006, FN-010 (data part); SEC-005; OPS-002; NF-002; AC-7, AC-11
- **Design:** DD-6, §4, §11, §12, P-17, P-18
- **Files:** `action/json/global/CrpByUserEmailAction.java`; `CrpByUserEmailActionTest`.
- **Scope:** two passes (Cognito per unit, then one `methodsFor`, then write `otpEnabled` and `passwordEnabled`);
  failure → `otpEnabled=false`, `passwordEnabled=true` for non-Cognito accounts, ERROR line; replace the lock at
  `CrpByUserEmailActionTest.java:314` with "exactly one query".
- **Tests:** one query per call with several units, with no key and with a username; `passwordEnabled` per unit for each
  §4 row; last-resort ERROR logged once per unit across repeated calls; no new user field; response unchanged with
  every switch on.
- **Falsifier:** compute `passwordEnabled` before the Cognito pass → Cognito accounts get `true` → red; log the ERROR on
  every call → once-per-unit test red.
- **Red run:** the `passwordEnabled` assertions fail on today's response (field absent).
- **Disqualifier:** a query-count assertion on a mock that cannot count native queries.
- **Consumers:** `login.js` `loadAvailableItems` (T06); `CrpByUserEmailActionTest` (14).
- **Review:** `checklist` — data shaping on top of T03's rule.
- **Done:** tests green; falsifiers red.
- **Skills:** `marlo-verify`, `api-design-principles` (response shape only).

### CHG-PWD-SWITCH-001-T05 — Password gate on the two endpoints (DD-5)

- **Status:** done [x] · **Size:** M · **Depends on:** T03 · **Module:** marlo-web
- **Requirements:** SEC-001, SEC-002, SEC-003, SEC-006; OPS-001; AC-10, AC-12
- **Design:** DD-5, §12, §13, P-2, P-13, P-15
- **Files:** `action/json/global/ValidateUserAction.java`; `action/home/LoginAction.java` (constructor change reaches
  `OtpVerifyCodeAction`, `CognitoCallbackAction`); their tests.
- **Scope:** refuse before `userManager.login` when the key is configured and the switch is off for the attempt's unit;
  unit counts only if a membership, else sweep every membership; unknown account not refused; query failure → today's
  behavior + ERROR; one INFO line; wrong-password message.
- **Tests:** refusal for a member of a password-off unit; foreign password-on `globalUnitId` → sweep refuses
  (the C-2 bypass); unknown account → credential check as today; key missing → no refusal; query failure → no refusal +
  ERROR; no `userManager.login` / LDAP call on refusal (double); response identical to a wrong password; SEC-005 guard
  still refuses Cognito accounts; CLARISA filter untouched (static check).
- **Falsifier:** trust `globalUnitId` without the membership check → bypass test red; place the gate after
  `userManager.login` → "no credential call" test red.
- **Red run:** the refusal tests fail on today's endpoints (password accepted).
- **Disqualifier:** a test that asserts the message only, without the "no credential call" double.
- **Consumers:** `ValidateUserActionGuardTest` (7), `LoginActionCgiarGuardTest` (6), `LoginActionFinishLoginTest` (17),
  `LoginActionLogoutTest` (3), `CognitoLogHygieneTest`, `CognitoCallbackActionTest`, `CognitoLoginActionTest`,
  `OtpVerifyCodeActionTest` — design §17.
- **Review:** `full` — authentication gate.
- **Done:** full suite with `-am` green; falsifiers red; Checkstyle delta 0.
- **Skills:** `tdd`, `marlo-verify`, `error-handling-patterns`.

### CHG-PWD-SWITCH-001-T06 — Step 3 views (§5, UI-001, UI-002, UI-004, UI-005, FN-010)

- **Status:** done [x] · **Size:** S · **Depends on:** T04 · **Module:** marlo-web (webapp)
- **Requirements:** UI-001, UI-002, UI-004, UI-005, FN-010; AC-3, AC-15
- **Design:** §5, P-1, P-12
- **Files:** `global/js/login/login.js`; `WEB-INF/global/pages/loginForm.ftl` (FN-010 block);
  `WEB-INF/global/views/login/login.ftl:4`, `WEB-INF/global/pages/error/401.ftl:4` (cache-buster);
  `global.properties` (FN-010 message key).
- **Scope:** `data-password-enabled` set at `:761-764` and read at `:96-97`, `:523-524` (missing → `true`); code-only
  view (password removed, back link hidden, info filled); FN-010 view with the "use a different email" action;
  `resetMethodView` restores the back link. *(Corrected 2026-10-21: the line references are pre-change; in the current `login.js` the attribute is set at `:782` (in the attribute map at `:778-783`) and read at `:101` and `:529`.)*
- **Tests:** no JS harness — **substitute:** claude-in-chrome walkthrough on the throwaway copy of every §5 row plus a
  username on a password-off unit and an old-page fallback (attribute absent); record `probes/output/` walkthrough file;
  i18n gate on the new key.
- **Falsifier:** remove the attribute read at `:523-524` *(Corrected 2026-10-21: now `:529`)* → reopening step 3 after "Go back" shows the password on a
  password-off unit → walkthrough row red.
- **Red run:** before the change, a password-off unit still shows the password field (walkthrough).
- **Disqualifier:** a walkthrough step driven by synthetic events where a real click is possible, reported as a pass.
- **Consumers:** none beyond the two FTL references (P-12).
- **Review:** `checklist` — visual result reviewed at the HITL pause (T6 visual check).
- **Done:** walkthrough recorded; cache-buster bumped; i18n gate.
- **Skills:** `marlo-verify`, `frontend-design`.

### CHG-PWD-SWITCH-001-T07 — Welcome email (DD-7, FN-008, FN-009, UI-003)

- **Status:** done [x] · **Size:** M · **Depends on:** T03 · **Module:** marlo-web
- **Requirements:** FN-008, FN-009, UI-003; AC-13
- **Design:** DD-7, P-9, P-10, P-11
- **Files:** new `utils/WelcomeSignInText.java` (`@Named`, GPL header); the 7 senders (`CrpUsersAction`,
  `CrpProgamRegionsAction`, `CrpPpaPartnersAction`, `CrpSiteIntegrationAction`, `CrpAdminManagmentAction`,
  `ProjectPartnerAction`, `ClusterActivitiesAction`); `global.properties`, `custom/aicrra`, `custom/aiccra3`,
  `custom/test` (`email.newUser.part1.codeSignIn`).
- **Scope:** helper returns today's text when the password is available (and for Cognito accounts), the code-sign-in
  text otherwise; `{3}` = unit acronym; each sender passes its unit (`globalUnit` in `CrpUsersAction`, `loggedCrp`
  elsewhere); password generation and storage untouched.
- **Tests:** helper unit tests for each case; one test per sender proving it calls the helper with its unit (or a grep
  gate recorded: zero remaining direct `getText("email.newUser.part1"` in the 7 files); i18n gate; the AICCRA body
  rendered once in the sink and compared with the approved mockup.
- **Falsifier:** leave one sender on the direct `getText` → grep gate red; pass the session unit in `CrpUsersAction`
  instead of `globalUnit` → its test red.
- **Red run:** helper tests fail before the helper exists.
- **Disqualifier:** a "rendered" check that inspects the template string instead of the sent HTML.
- **Consumers:** run `grep -rln "CrpUsersAction\|CrpProgamRegionsAction\|CrpPpaPartnersAction\|CrpSiteIntegrationAction\|CrpAdminManagmentAction\|ProjectPartnerAction\|ClusterActivitiesAction" marlo-web/src/test/java` first and list every hit here.
- **Review:** `checklist` — copy plus wiring; the rule itself is T02/T03.
- **Done:** tests green; grep gate 0; i18n gate; Checkstyle delta 0.
- **Skills:** `marlo-verify`.

### CHG-PWD-SWITCH-001-T08 — Cross-spec amendments, runbook, end-to-end evidence (DD-8, OPS-003)

- **Status:** done [x] · **Size:** M · **Depends on:** T01–T07 · **Module:** docs, probes
- **Requirements:** OPS-001, OPS-002, OPS-003; FN-002, FN-006, SEC-006, SEC-007; AC-2, AC-7, AC-8, AC-10, AC-12
- **Design:** DD-8, §11, §14, §16
- **Files:** `docs/specs/changes/center-email-otp-login/requirements.md` (amendment notes on FN-001, FN-003, FN-005,
  FN-006, FN-015, SEC-009, DA-002, UI-006; Decision Log; stale Executive Summary), its `design.md`/`runbook.md` where
  they state the old rule, and re-run of its `probes/ac21-migration.sh`; new `runbook.md` in this folder; probes and
  `probes/output/` here.
- **Scope:** amend, sweep forward and backward (the old sentences "applies only while password sign-in is on",
  "enabled by listing domains"); runbook: prerequisites, turn off, turn back on (UI and SQL), log lines; live evidence on
  the throwaway copy.
- **Tests (live):** switch on everywhere → `CHG-OTP-LOGIN-001` `ac-probe.sh` stages green (AC-2); switch off → curl
  `validateUser.do` and `login.do` refused with the wrong-password text and the INFO line (AC-10); live change without
  restart (AC-8); restart with the key blank → password offered and one ERROR per unit (AC-7); CLARISA filter and an
  `/api/**` Basic request still authenticate (AC-12).
- **Falsifier:** read the switch through the cached parameter path → live-change probe red.
- **Red run:** n/a — evidence task over already-tested code.
- **Disqualifier:** any probe run against a non-`probe` database; a live check run while another agent builds.
- **Consumers:** `CHG-OTP-LOGIN-001` documents citing the amended clauses (backward sweep).
- **Review:** `full` — cross-spec security text.
- **Done:** sweeps recorded; probes green and recorded; environment cleaned.
- **Skills:** `marlo-verify`, `cognitive-doc-design`.

---

## Dependency Graph

```text
T01 migration ─┐
T02 rule ──────┴─ T03 code endpoints ─┬─ T04 crpByEmail ── T06 step-3 views ─┐
                                      ├─ T05 password gate ──────────────────┤
                                      └─ T07 welcome email ──────────────────┴─ T08 docs + live evidence
```

No cycles. T01 and T02 are independent; T04, T05 and T07 are independent after T03 (different files). Execution stays
sequential under one Leader unless separate worktrees are used.

## Coverage — scenario and clause level

| Clause | Owning task |
|---|---|
| FN-001 default on, missing reads on | T01 (seed), T02 (`isPasswordSwitchOn`) |
| FN-002 live change, no restart | T03 (uncached key list), T08 (live probe) |
| FN-003 switch on = T21 behavior | T03 (zero-call tests), T08 (AC-2 probes) |
| FN-004 code only, even code off / domain excluded | T02, T03, T06 |
| FN-005 Cognito account unchanged; BUT no code under the override | T02, T03 |
| FN-006 last resort + ERROR | T02, T04, T05, T08 |
| FN-007 no account without a method | T02 (exhaustive) |
| FN-008 / FN-009 welcome email; password still stored | T07 |
| FN-010 username message; MUST NOT show password | T02 (`needsEmail`), T06 |
| SEC-001 refuse before credentials and LDAP (fail-open exception) | T05 |
| SEC-002 same as wrong password | T05 |
| SEC-003 SEC-005 guard unchanged | T05 |
| SEC-004 code endpoints neutral | T03 |
| SEC-005 no new disclosure | T04 |
| SEC-006 CLARISA and `/api/**` untouched | T05 (static), T08 (live) |
| SEC-007 session switch unchanged | T08 (recorded, no code) |
| OPS-001 INFO line | T05 |
| OPS-002 ERROR once per unit per start | T03 (memory), T04 (decision), T08 (live) |
| OPS-003 runbook | T08 |
| DA-001 / DA-002 / DA-003 | T01 |
| UI-001 / UI-002 / UI-004 / UI-005 | T06 |
| UI-003 | T07 |
| NF-001 one rule | T02, reviewed in T03–T07 |
| NF-002 at most one query | T04 |

## Definition of Done

Checked 2026-10-18 by [`validation-report.md`](./validation-report.md) (PASS, no FAIL).

- [x] T01–T08 done with a Reviewer PASS (or a recorded `REVIEW_SKIPPED` / `REVIEW_WAIVED`).
- [x] Compile gate, Checkstyle delta 0, full suite with `-am`, i18n gate, migration gate.
- [x] Live evidence of T06 and T08 recorded under `probes/output/`; environment cleaned.
- [x] `CHG-OTP-LOGIN-001` amended with forward and backward sweeps.
- [x] Switch **on** everywhere after deploy; turning it off in production stays an operational decision (OPS-003).
