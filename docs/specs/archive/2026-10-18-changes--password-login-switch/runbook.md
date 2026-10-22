# Runbook: turn password sign-in off for a Global Unit

**Spec:** `docs/specs/changes/password-login-switch` (`CHG-PWD-SWITCH-001`) · **Task:** T08 · **Requirements:** OPS-001, OPS-002, OPS-003
**Companion:** `docs/specs/changes/center-email-otp-login/runbook.md` (the emailed code itself: secret, relay, "code not received")

**What this is.** One setting per Global Unit, `system_password_login_active`, switches the password off on the login
page. While it is off, the emailed code is how every account that does not use Sign in with CGIAR signs in. This runbook
says what must be true before you turn it off in production, how to turn it off and back on, and which log lines to
watch. Nobody has turned it off in any production unit; this spec does not decide when (requirements §4).

## Quick path

| I want to | Do this |
|---|---|
| Turn the password off for a unit | Check section 2, then System Admin, Parameters (`/superadmin/marloParameters.do`) *(Corrected 2026-10-21: the menu label varies per unit: "Parameters" in the global bundle, "PTF Parameters" in the AICCRA, aiccra3 and PABRA bundles, "CRP Parameters" in the Alliance and test bundles; the URL is `/superadmin/marloParameters.do`)*, the unit, Specificities, `system_password_login_active` off, Save (section 3) |
| Turn it back on, normally | Same screen, switch on, Save. It applies on the **next login attempt**, no restart (section 4) |
| Turn it back on, one statement | The `UPDATE` in section 4 |
| Know whether it is working | Search the application log for `auth.password.` and `auth.otp.request` (section 5) |
| Answer a user who "cannot sign in" | Section 6 |

---

## 1. What the switch does

| Account in the unit | Switch **on** (the default) | Switch **off**, code key valid |
|---|---|---|
| Without Cognito (local account, or a CGIAR account on a unit without Cognito) | Today's methods: the password, plus the code when the code switch is on and the domain is not excluded | **The code only.** It is offered even when `system_otp_login_active` is off and even when the domain is excluded |
| Uses Sign in with CGIAR (`is_cgiar_user = 1` in a unit with `cognito_auth_active` on) | Cognito, plus the code under the code switch | **Unchanged.** The password switch never touches these accounts |
| Any account without Cognito, server **without a valid code key** | Today's methods | **The password.** A missing key is a server fault, not the unit's choice, so the password stays (last resort) *(Corrected 2026-10-21: the row said "any account". An account that uses Sign in with CGIAR never gets the password, key or no key: `SignInMethodRule.methodsFor` computes `password = !cognitoAccountInG && (passwordOn || !keyConfigured)`)* |

What else changes while it is off:

- The server **refuses a password** posted to `validateUser.do` and `login.do` for that unit, **before** any password or LDAP
  check, with exactly the answer a wrong password gets for that kind of account. *(Corrected 2026-10-21: which unit decides: the unit the attempt names counts only when the account is a member of it; otherwise every membership of the account is checked, and any one with the switch off refuses the password (`OtpEligibility.passwordGate`, `decidingUnits`))* The user sees the ordinary failed-login answer, never a message that says the password is off.
- A user who typed a **username** (not an email) in step 1 sees "Password sign-in is not available for this account. Enter your
  email address to receive a sign-in code." with the way back to step 1, because a code needs an email address.
- A **new account's welcome email** carries no password and explains code sign-in. A random password is still stored, so
  turning the switch back on leaves every account with a credential. Five legacy programs (`bigdata`, `alliance`, `eib`,
  `gender`, `pabra`) show the global text of that email, not their own styled copy.
- Stored passwords are **never deleted or changed.**

## 2. Prerequisites for production (OPS-003)

Do not turn the switch off in a production unit until every row is ticked and has an owner.

- [ ] **Code key on every instance.** `otp.hmac.secret` (or `OTP_HMAC_SECRET`) is set and no `auth.otp.keys outcome=unconfigured`
  WARN appears after any instance starts (center runbook A2). The key is read **once at start**: an instance without it keeps
  the password for that unit, so users would see different methods depending on the instance.
- [ ] **Mail relay proven for external domains.** A code request for a non-CGIAR test mailbox is delivered through the
  production relay (center runbook A4 and B4). The CGIAR gateway refuses relay to other domains, and with the password off a
  failed code email leaves the user with **no way in**.
- [ ] **Stage 1 delivery data read.** The code has been live in this unit under center runbook B5 stage 1 long enough to read
  real delivery, `auth.otp.delivery outcome=delivered` against `outcome=email_failed` for external domains. This spec sets no
  threshold; the product owner and PMU decide it and write it next to this row.
- [ ] **Production counts measured (OQ-3).** Run the query below for the unit. Accounts counted as local or CGIAR without Cognito
  move to the code; accounts of a unit with Cognito on that have `is_cgiar_user = 1` are not moved by the override: they keep Sign in with CGIAR, and the code only under the code switch and the exclusion.
- [ ] **`marlo.production=true` in production** (otherwise every code goes to the support mailbox and into the log, center runbook B4).
- [ ] **Support has this runbook** and knows the three log lines of section 5.

```sql
-- runbook:count
SELECT COUNT(*) AS active_members,
       SUM(u.is_cgiar_user = 0) AS local_accounts,
       SUM(u.is_cgiar_user = 1) AS cgiar_accounts
FROM crp_users cu JOIN users u ON u.id = cu.user_id
WHERE cu.global_unit_id = <unit id> AND cu.is_active = 1 AND u.is_active = 1;
```

`global_units.global_unit_type_id` is `NOT NULL`, so the resolver of the Cognito flag always finds a type; a unit without one
cannot exist, and the case the T03 review raised (a type-less unit that cannot get a code) cannot occur.

## 3. Turn it off

1. Sign in as a super admin and open **System Admin > Parameters** (`/superadmin/marloParameters.do`) *(Corrected 2026-10-21: the menu label varies per unit: "Parameters" in the global bundle, "PTF Parameters" in the AICCRA, aiccra3 and PABRA bundles, "CRP Parameters" in the Alliance and test bundles; the URL is `/superadmin/marloParameters.do`)*.
2. Expand the unit, open the **Specificities** tab and type `password` in the search box.
3. Switch `system_password_login_active` **off** and press **Save**. The first save writes a `custom_parameters` row for each
   unit that lacked one, blank for the others, which still reads as **on** (a blank value is not `false`). *(Corrected 2026-10-21: read from the code, not run live: `CrpParametersAction.prepare()` pre-fills each missing row with the parameter's default value, `'true'` for this key, and `marloParameters.ftl` then pre-checks **Yes**, so the first Save writes `true` for the other units, not blank. They stay on either way)*
4. Check `crpByEmail.do` for an account of that unit: the unit entry has `"passwordEnabled": false` and, for an account without Cognito,
   `"otpEnabled": true`. Load the login page for the unit with that account: step 3 shows the code directly, with no password field.
5. Watch section 5 for the first day.

The value is parsed as **on unless it is exactly `false`** (case and spaces ignored). Anything else, including a blank, a typo or
a missing row, leaves the password on, so a mistake never locks a unit out.

## 4. Turn it back on

**Normally** (live on the next login attempt, no restart, proved in the evidence record): the same screen, switch
`system_password_login_active` **on**, Save.

**With one SQL statement**, when the admin screen is not available. Replace `<unit id>` with `global_units.id`:

```sql
-- runbook:turn-on
UPDATE custom_parameters cp JOIN parameters p ON p.id = cp.parameter_id
SET cp.value = 'true'
WHERE p.`key` = 'system_password_login_active' AND cp.global_unit_id = <unit id>;
```

One row is affected. If the unit has no row, the password is already on. Deleting the row has the same effect. The statement
bypasses the audit of the admin screen; use the screen when you can. **Rollback ladder, cheapest first:** turn the
switch on (next login attempt); the `UPDATE` above; revert the deploy. Stored passwords were never touched, so nothing else
needs repair.

## 5. Log lines to watch (OPS-001, OPS-002)

Search the application log. No line below carries an email address, a password or a code.

| Line | Level | Meaning | What to do |
|---|---|---|---|
| `auth.password.refused gate=password_switch gu=<acronym>` | INFO | One password was refused because the unit has the switch off (one per refused attempt, from `validateUser.do` or `login.do`) | Expected after you turn the switch off. A steady stream means users still try passwords: check the login page shows the code view and the welcome emails |
| `auth.password.last_resort gu=<acronym> reason=code_key_unconfigured` | **ERROR** | The password is offered in this unit **only** because this instance has no valid code key. Written **once per unit per application start**, never from an unauthenticated repeat | **Fix the key** (center runbook A2) and restart that instance. Until then the unit's password stays on there |
| `auth.password.gate outcome=settings_unavailable fail_open=true` | ERROR | The settings query failed, so the gate let the password through, as before this spec | A database fault. The login itself fails on the same fault; investigate the database |
| `unable to resolve the sign-in methods; answering the password for accounts without Cognito` | ERROR | The settings query of the login lookup (`crpByEmail.do`) failed, so step 3 offered the password and no code to accounts without Cognito, as before this spec (DD-6). Logged by `CrpByUserEmailAction` under the logger name `FlaghshipsByCrpAction` (an older naming slip), with the stack trace | A database fault; investigate the database |
| `auth.otp.request outcome=sent gu=<acronym> ... via=password_off` | INFO | A code was issued **because** the unit has the switch off (the code switch was off, or the domain excluded, or just the default method) | Expected. Compare with `auth.otp.delivery` for the mail (center runbook A5) |
| `auth.otp.keys outcome=unconfigured` | WARN | The code key is missing or invalid on this instance; the feature is off there | Same as the last-resort line |
| `welcome.email outcome=settings_unavailable fallback=password_text gu=<acronym>` | ERROR | The settings query failed while a welcome email was built, so the email went out with the password text, as before this spec (`WelcomeSignInText`). The line carries the stack trace and no email address | A database fault; investigate the database. The user's sign-in itself is not affected |
| `auth.password.gate outcome=remember_me_unavailable` | WARN | Shiro's remember-me handling threw while building the refusal; the refusal itself still happened | Developer; no user impact |

## 6. Support notes

| A user says | Likely cause | Answer |
|---|---|---|
| "My password is rejected" and the unit has the switch off | The unit turned the password off | "Sign in with the code sent to your email." The page shows only the code |
| "The page asks for my email, I typed my username" | Step 3 shows the "enter your email address" message | Enter the email address registered in MARLO |
| "I did not get the code" | Delivery, or the account is not an active member | Center runbook A5 and A6. Say nothing about whether the address exists |
| A CGIAR user "cannot use the code" | The account uses Sign in with CGIAR, and the unit's code switch is off or its domain is excluded | Correct: the password switch does not apply to such accounts; they get the code only under the code switch and the exclusion |
| "I got a welcome email with no password" | The unit has the switch off | Correct: the email explains code sign-in |

## 7. Accepted limits (requirements Decision Log)

| Limit | Effect |
|---|---|
| Switching Global Unit inside a signed-in session (SEC-007) | A member who signed in where the password is allowed can move to a unit where it is not. Same as today for Cognito |
| `/api/**` HTTP Basic (SEC-006) | The switch governs the login page only. A stored password keeps working on `/api/**` Basic while the unit has the switch off. `/api/**` Basic is not used in MARLO today (user, 2026-10-18), so no separate issue is open |
| CLARISA technical account | Unchanged by the switch |
| Timing | A refused password answers faster than a checked one, so response time can show that an account belongs to a switch-off unit. The login lookup already lists an account's units to anyone who types its email |
| CGIAR account on a unit without Cognito, directory outage | A refusal says the CGIAR wrong-password text; a directory that does not answer would say nothing. What leaks is that the switch is off, which step 3 already shows |
| Removed member of a switch-off unit | `finishLogin` still uses the activity-blind membership read, so a removed member can still sign in with a password where it is allowed. Predates this change (bugfix spec to come) |
| **Open, not decided here:** victim lockout of the code | The code is limited to 5 requests per email and 20 per IP per 15 minutes (center runbook A7, SEC-010). Where the password is off, someone who spends a user's 5 requests leaves that user, whose only method is the code, with no way in until the window ends. Before this spec the same attack left the password as an alternative. The product owner decides whether that is acceptable for a unit before its switch is turned off |
| Fail-open on a settings fault | If the settings query fails, the password stays allowed and an ERROR is logged, so a database fault cannot lock users out |

## 8. Evidence record (T08, 2026-10-17)

Run on the throwaway copy `aiccradb_otp_probe` (clone of `aiccradb_actsave`), against an application built clean from the
working tree at HEAD `9b2448578d` (2415 / 1066 files, `BUILD SUCCESS`), SMTP sink on `127.0.0.1:2525`. The recorded outputs are in
`probes/output/`; the scripts are `probes/ac-live.sh` and `probes/fixtures.sh`.

| Claim | Check | Result | Output |
|---|---|---|---|
| Switch on everywhere: today's behavior (AC-2) | the `CHG-OTP-LOGIN-001` stages `enum live attempts resend removed limits nonprod session leak expiry` | 68 of 70 checks pass; the 2 others are the probe reading the SMTP envelope from a sink that wrote it elsewhere, and pass in a re-run (26 of 26) | `ac2-switch-on-ac-probe.out`, `ac2-switch-on-ac-probe.rerun-enum-nonprod.out` |
| A password is refused where the switch is off (AC-10, SEC-001, SEC-002, OPS-001) | `validateUser.do` and `login.do` with the correct password, a CGIAR account, controls | 23 of 23. Same body, status and `Set-Cookie` as a wrong password on a unit with the switch on; no credential check, no LDAP attempt, no session; one INFO line, no password or address in it | `probes/output/ac10.green.out` |
| The change is live, no restart (AC-8, FN-002) | raw SQL both ways, same JVM pid | 8 of 8. **Falsifier:** with the switch read through the Hibernate query cache, 5 of 8 fail | `ac8.green.out`, `ac8.FALSIFIER-red.out`, `ac8.FALSIFIER-restored-green.out` |
| A blank code key leaves the password on (AC-7, OPS-002) | application started with an empty `OTP_HMAC_SECRET` | 11 of 11. Both units offer the password, no code is issued, the password signs in, exactly one ERROR per unit however many lookups | `ac7.green.out` |
| The code is the default method while the password is off (AC-3 to AC-6) | code switch off, domain excluded, Cognito account | 10 of 10, including the `via=password_off` line | `override.green.out` |
| CLARISA and `/api/**` Basic are untouched (AC-12) | CLARISA session read from the JVM; Basic with the switch off and on | CLARISA authenticated: pass. **`/api/**` Basic: not verified**, it answers 401 on this stack even with the switch on, before any code of this spec (probes README, section 3) | `ac12.green.out` |
| The SQL of this runbook works (OPS-003) | the two statements run as written | 5 of 5 | `runbook-sql.green.out` |
| Cross-spec amendment (DD-8) | forward and backward sweeps; `ac21-migration.sh` re-run | recorded in the Decision Log of `CHG-OTP-LOGIN-001`; 17 of 17 | `sweeps.out`, `../center-email-otp-login/probes/output/ac21-migration.pwd-switch-rerun.out` |

Full record, what was not run and why: [`probes/README.md`](./probes/README.md).
