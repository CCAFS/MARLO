# CHG-PWD-SWITCH-001-T06 walkthrough (2026-10-17)

Environment: working tree = HEAD 03d7e602af plus the T06 diff; app on :8080 against the throwaway copy `aiccradb_otp_probe`
(clone of `aiccradb_actsave`, `MYSQL_DATABASE` in the JVM environment, migration `V2_6_0_20261016_1525` applied by Flyway on start);
SMTP sink on 127.0.0.1:2525; claude-in-chrome, real clicks and typing only. The state read after each click comes from the page
(`dump()`, a read-only snapshot of visibility and `methodView`). Unit 45 (AICCRA) switches set per row with SQL in the copy only
(`custom_parameters`); unit 47 (AICCRA_III) keeps the defaults. Fixture user 3862 (non-CGIAR, members of 45 and 47), `users.username` set in the copy.
A first start of the build had a bad generated `FundingSourceSummaryMapperImpl` (unqualified `FundingSourceInfo`, bean missing at start);
`rm -rf marlo-data/target` and a rebuild of marlo-data then marlo-web fixed it. Not related to this task.

| # | Setup (otp / password) | Action | Expected | Observed | Verdict |
|---|---|---|---|---|---|
| R | otp on, pw off; served JS = HEAD `login.js` (red run) | email, Log in | password-off unit hides the password | choice menu with "Continue with your password" shown, `#login-password` present | RED (as required) |
| 1 | on / off, email | email, Log in | code only: no password field, no choice, no "Choose another way", info filled with masked address, Send code | `pwField 0`, `choice false`, `back false`, info "We will email a one-time code to k***@outlook.com. No password needed.", Send code visible, `methodView code` | PASS |
| 2 | as 1 | click Send code | existing code panel states | step "Enter your sign-in code", "Resend in 30 s", mail at the sink | PASS |
| 3 | as 1 | "Use a different email", retype, Log in | back to step 1, then code only again | email step, then code only | PASS |
| 4 | as 1 | Go back, Log in again (reopen step 3) | code only, no password | `pwField 0`, `methodView code` | PASS |
| 5 | multi-unit: 45 off, 47 pw on | project step: pick 47, Log in, Go back, pick 45, Log in | 47 shows the password, 45 code only again | 47: `pwField 1`, direct password, Log in visible; then 45: code only, `pwField 0` | PASS |
| 6 | on / on | Log in | unchanged choice, code first | choice + "Continue with your password" | PASS |
| 7 | as 6 | click code, then "Choose another way", then "Continue with your password" | back link visible; password view | `back true`; password view `logIn true` | PASS |
| 8 | switch flipped off then on between lookups, same page | code only (off) then, after Go back x2 and re-lookup, choice (on), click code | `resetMethodView` / `showMethodView` restore the back link | `back true` in the code panel | PASS |
| 9 | otp off, pw on (unit 47) | pick 47, Log in | unchanged direct password | `methodView direct`, password field, Log in visible | PASS |
| 10 | on / off, username `probe.t06` | Log in x2 | FN-010: message, no password field, no Log in, no terms, "Use a different email" | message "Password sign-in is not available for this account. Enter your email address to receive a sign-in code.", `pwField 0`, `logIn false`, `terms false`, action visible | PASS |
| 11 | as 10 | click "Use a different email", type the email, Log in x2 | step 1 with the typed value kept, then code only | `emailVal probe.t06` kept, then `methodView code` | PASS |
| 12 | old-page fallback: card attribute removed from `#crp-AICCRA` in the DOM, DB pw off | Go back, pick card, Log in | absent attribute reads as on: choice with password | `attr undefined`, `pwVar true`, choice + password shown | PASS |
| 13 | COGNITO, otp off, pw on (user 3862 set `is_cgiar_user=1`; unit 45 already has `cognito_auth_active=true` in the copy) | email, Log in x2 | unchanged: "Sign in with CGIAR" alone, no password, no choice, no code | `cgiarBtn true`, `pwField 0`, `choice false`, `codeBtn false` | PASS |
| 14 | COGNITO, otp on, pw on | Go back x2, re-lookup, Log in x2 | unchanged choice: Sign in with CGIAR + Email me a code, no password | `choice true`, `choiceCgiar true`, `choicePw false`, `pwField 0` | PASS |
| 15 | COGNITO, otp on, pw off | same | same as 14 (password switch does not matter) | identical to 14 | PASS |
| 16 | COGNITO, otp off, pw off | same | same as 13 | identical to 13 | PASS |

Rows 13-16 stop at the step-3 view; no Cognito button was clicked and no redirect started. The server reports `passwordEnabled=false` for a Cognito
account whatever the switch, and the COGNITO arms ignore it, as designed.

Scratch changes for 13-16: `UPDATE users SET is_cgiar_user=1 WHERE id=3862` (copy only), reverted to 0 afterwards. The Cognito parameter
(`custom_parameters` unit 45, `cognito_auth_active`) was not changed. Final switch state, unit 45: otp true, password false.

## Falsifier (attempt 2, supersedes the attempt 1 paragraph, whose "unreachable" claim was wrong)

Reachable path for the `:523-524` read: a multi-unit user, AICCRA first in `crpByEmail.do` `crps`, **no `CRP` cookie** (cleared with
`document.cookie` expiry, path `/marlo-web`; `verifyCookie("CRP")` false after reload). On the first lookup of a fresh page the AICCRA
auto-click (`:751-752`) runs before the `data-*` attributes are written (`:777-783`), so the click handler captures nothing:
`selectedGlobalUnitId` stays `null` while the AICCRA card is `.active`. Pressing "Log in" on the project step without clicking a card reaches
the `:523` branch. Observed on the project step: `gu null`, cards `crp-AICCRA:active:pw=false`, `crp-AICCRA_III:pw=true`. Fixture: user 3862,
unit 45 otp on / password off. Real clicks only.

| Run | Served `login.js` | Result | Verdict |
|---|---|---|---|
| F1 | `:529` (`passwordEnabled = $activeCard.attr(...)`) removed | step 3: `gu 45`, `pwVar true`, `pwField 1`, choice menu with "Continue with your password" on a password-off unit | RED |
| F2 | restored (`cmp` against `src` identical) | same path: `gu 45`, `pwVar false`, `pwField 0`, code only, `back false` | GREEN (also the positive row: no cookie, no card click, intact read gives code only) |
| F3 | click-site read (`:101`) removed (attempt 1, second falsifier) | choice with password button, `pwField 1` | RED |
