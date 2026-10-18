# Password Login Switch — Proposal

> **The answer first.** Add one specificity per Global Unit, `system_password_login_active` (default **ON**), that
> lets a Global Unit stop accepting passwords on the login page. When it is OFF, the sign-in code of
> CHG-OTP-LOGIN-001 replaces the password for every account that does not use Cognito, even where the code
> switch is OFF or the domain is excluded. The code is the default method, because an account may never have
> received its password. No account is ever left without a way in. The server refuses a password the
> Global Unit does not allow, and the welcome email stops sending a password nobody can use. The change is
> built and shipped ON; turning it OFF in production waits for a proven mail relay and stage 1 data.

---

## 1. Document Control

| Field | Value |
|---|---|
| Spec path | `docs/specs/changes/password-login-switch` |
| Slug | `password-login-switch` — given by the author |
| Type | **Change** |
| Approval Mode | `gated` |
| Status | **Approved** — Kenji Tanaka, 2026-10-16 |
| Date | 2026-10-16 |
| Author | Kenji Tanaka (IBD Team) |
| Jira | none yet. To be created in A2 once the parent epic is confirmed |
| Source | Design decisions taken in conversation on 2026-10-16, recorded in §4 and §11 |
| Depends on | `changes/center-email-otp-login` (CHG-OTP-LOGIN-001), T21 at least: this change reads its two specificities and changes how its domain exclusion applies |
| Parallel-safe | **no** — shares `OtpDomainPolicy`, `OtpEligibility`, `CrpByUserEmailAction`, `ValidateUserAction`, `LoginAction`, `login.js`, `loginForm.ftl` and both `APConstants.java` with CHG-OTP-LOGIN-001 |
| Parent Spec | none (follow-up to, not a child of, `changes/center-email-otp-login`) |

---

## 2. Intent

Let each Global Unit decide that people sign in **without a password**, using the emailed code, while keeping
Cognito for CGIAR accounts where it is on. It is the step that makes "code instead of password" possible, and
later "code only" if Cognito is ever retired.

---

## 3. Problem / Current Behavior

| # | Today | Evidence |
|---|---|---|
| P1 | Every account without Cognito can always sign in with a password. No setting turns it off | `login.js` step 3 composes only `COGNITO` or `LOCAL` (`login.js:567`); `LOCAL` always renders the password (`login.js:609-640`) |
| P2 | A new external account gets a **random 6-digit numeric** password, sent in clear in the welcome email | `CrpUsersAction.java:800` (`RandomStringUtils.randomNumeric(6)`), `email.newUser.part1` `{3}` (`CrpUsersAction.java:1034`). Same generator in 6 more admin actions and in the centre `ManageUsersAction.java:146` (§7) |
| P3 | MARLO has no "forgot password" flow; a user who lost it must write to support | `grep -rln -i "forgotPassword\|resetPassword\|changePassword\|recoverPassword" marlo-web/src/main/java marlo-web/src/main/webapp/WEB-INF` → 0 files (run 2026-10-16) |
| P4 | The password typed by a CGIAR account on a Global Unit **without** Cognito is relayed to Active Directory over LDAP | `APCustomRealm.java:179-187` (`isCgiarUser()` → `ldapAuthenticator`, else `dbAuthenticator`) |
| P5 | On a Cognito Global Unit a CGIAR account's password is already refused, server side | `ValidateUserAction.java:99-115` and `LoginAction#isCgiarCredentialRelayBlocked` (`LoginAction.java:203-239`), SEC-005 of CHG-COGNITO-AUTH-001 |
| P6 | The code is offered when `system_otp_login_active` is ON and the domain is not in `system_otp_excluded_email_domains` | `OtpDomainPolicy.isOffered`, `CrpByUserEmailAction.java:157` (T21, commit `fcce4e7ebd`) |
| P7 | Once signed in, a member switches Global Unit by URL with no new authentication | `ValidSessionCrpInterceptor.java:100-119` (`existCrpUser` then `session.replace(SESSION_CRP, crp)`) |
| P8 | Measured on the local snapshot `aiccradb_actsave`, active AICCRA members: 169 non-`cgiar.org` accounts and 214 `@cgiar.org` accounts with `is_cgiar_user = 0` sign in with a MARLO password; 1 account uses Cognito. All 384 have a stored password | Read-only query, 2026-10-15/16. Production not measured — `UNVERIFIED — confirm at source before relying on it` |
| P9 | The local SMTP host refuses to relay to external domains, so external mail delivery in production is unproven | `453 … as a relay (#4.7.1)`, CHG-OTP-LOGIN-001 `execution.md` T15 entry; production relay is a go-live item of that spec's runbook (Part B) |

---

## 4. Proposed Outcome

On step 3, a user sees only the methods available to **this account in this Global Unit**:

| Account in the selected Global Unit | Code switch | Domain excluded | Password switch | Step 3 |
|---|---|---|---|---|
| CGIAR (`is_cgiar_user = 1`), Cognito ON | — | — | any | As today: Cognito, plus the code if offered. Never a password (SEC-005) |
| Any other account | ON | no | ON | As today: choice, code or password |
| Any other account | ON | no | **OFF** | **Code only**, shown directly |
| Any other account | ON | yes | ON | As today: password only |
| Any other account | ON | yes | **OFF** | **Code only** — for accounts without Cognito the exclusion does not apply while the password is OFF (rule 3d) |
| Any other account | OFF | any | ON | As today: password only |
| Any other account | OFF | any | **OFF** | **Code only** — default method: with the password OFF the code is always offered (rule 3d) |

"Any other account" covers external accounts, `@cgiar.org` accounts with `is_cgiar_user = 0`, and CGIAR accounts on
a Global Unit without Cognito (their password is the Active Directory one).

**Last resort (OQ-4):** if the server has no valid code key (`OtpKeys.isConfigured()` is false), the password is offered
even where its switch is OFF, and an ERROR line is logged, because a missing key is a server fault, not a Global Unit choice.

**Rule 3d, in one line:** with the password OFF, every account without Cognito gets the code; the code switch and
the domain exclusion only decide whether the code is offered **next to** a password. If all three methods are OFF,
the code appears, for CGIAR accounts too (for a username, the message to use the email address instead).

Turning the switch back ON restores the password on the next login. Stored passwords are never deleted.

---

## 5. Scope

- **Specificity:** `system_password_login_active`, boolean, per Global Unit, default `true`, seeded by a safe Flyway
  migration for the existing types among 1–4. Constants in both `APConstants.java`. A plain-language System Admin
  description in the A2-2524 shape.
- **Uncached read**, like the T21 keys, so a change takes effect on the next login; folded into the same native query
  that already reads both OTP keys for every listed unit.
- **One shared rule** for "which methods does this account have in this Global Unit", used by step 3, by every server
  entry point and by the welcome email. No second copy of the rule.
- **Server enforcement:** `validateUser.do` and `login.do` refuse a password the rule does not allow, before any
  credential check or LDAP relay, with the same neutral message as a wrong password.
- **Code path:** while the password is OFF, the code is offered to every account without Cognito, whatever the code
  switch and the exclusion say (rule 3d); for Cognito accounts both still apply (FN-005).
  `crpByEmail.do` reports `passwordEnabled` per Global Unit next to `cognitoEnabled` and `otpEnabled`.
- **UI:** `login.js` / `loginForm.ftl` render "code only" without the choice menu; cache-buster bumped.
- **Welcome email:** when the rule does not allow a password, the sign-in block reads "How to sign in: no password is
  needed…" instead of the password. One helper used by the 7 senders of `email.newUser.part1`. A random
  password is still stored. Per-unit copy in `custom/aicrra`, `aiccra3`, `test`, and the global file.
- **Descriptions:** amend the System Admin texts of `system_otp_login_active` and
  `system_otp_excluded_email_domains` so they say that, while password sign-in is OFF, both apply only to users of
  Sign in with CGIAR.
- **Key location:** settled in CHG-OTP-LOGIN-001 on 2026-10-16: `otp.hmac.secret` in `marlo-<profile>.properties`
  or the `OTP_HMAC_SECRET` environment variable, which takes precedence. Nothing to do here beyond the OQ-4 rule.
- **Runbook:** how to turn it OFF (prerequisites), how to turn it back ON in an emergency (UI or SQL), what to watch.

---

## 6. Non-Goals

- Re-authentication when switching Global Unit inside a session (P7). **Accepted risk**, decided 2026-10-16: the
  switches govern how a user signs in, not where a member goes next.
- A second switch to separate the MARLO password from the Active Directory password. Cognito already covers the
  Active Directory case.
- A "forgot password" flow (P3). The code is the recovery path where the switch is OFF.
- Deleting or rotating stored passwords.
- The welcome text for CGIAR accounts on Cognito Global Units ("(Your Outlook Password)"). Noted as a separate
  improvement.
- Authentication outside the login page: the technical CLARISA account (`ClarisaPublicAccesFilter.java:79`) and HTTP
  Basic on `/api/**` (`MarloShiroConfiguration.java:140`). Both keep working; a stored password still works on `/api/**`
  (accepted risk, Judgment Day C-1).
- Turning the switch OFF in production. That is an operational decision behind the prerequisites in §12.

---

## 7. Affected Users, Systems, And Specs

| Area | Files / owners |
|---|---|
| Specificity and reads | new migration; both `APConstants.java`; `OtpChallengeMySQLDAO` / manager (shared uncached query) |
| Method rule | `OtpEligibility` / `OtpDomainPolicy` (while the password is OFF, the exclusion applies only to Cognito accounts); new shared rule for password availability |
| Password entry points | `ValidateUserAction` (`validateUser.do`), `LoginAction#login` (`login.do`). Enumerate every caller of `userManager.login` and every `UsernamePasswordToken` (KZ-…directory-abstraction-1) |
| Pre-auth lookup | `CrpByUserEmailAction` (`crpByEmail.do`) |
| UI | `login.js`, `loginForm.ftl` (+ `login.ftl`, `401.ftl` cache-buster) |
| Welcome email | `CrpUsersAction`, `CrpProgamRegionsAction`, `CrpPpaPartnersAction`, `CrpSiteIntegrationAction`, `CrpAdminManagmentAction`, `ProjectPartnerAction`, `ClusterActivitiesAction`; `global.properties`, `custom/aicrra`, `custom/aiccra3`, `custom/test`. `utils/SendEmails.java:278-323` builds the same email but has no caller in `marlo-web/src/main/java` (grep 2026-10-16) — confirm and leave as is. Centre `ManageUsersAction.java:146` sets a password and sends no welcome email |
| Specs | `changes/center-email-otp-login`: FN-001, FN-003, FN-005, FN-006, FN-015, SEC-009, DA-002 and UI-006 are amended by this change (requirements Decision Log). `changes/migrate-ad-authentication-to-cognito` (archived): SEC-005 unchanged |
| Users | external and `is_cgiar_user = 0` accounts on Global Units that turn the password OFF |

---

## 8. Visual Reference

- **Source:** self-contained HTML mockups, rendered to PNG during the design conversation of 2026-10-16.
- **Location:** not committed (author's decision). Held locally by the author: the step-3 decision tree with the
  three settings and rule 3d, and the AICCRA welcome email today and proposed. §4 and §5 carry their content in text.
- **Notes:** the only UI change on the login page is the "code only" view, which reuses the existing code panel.

---

## 9. Requirement Delta Preview

### ADDED Requirements

- A Global Unit can turn password sign-in OFF with `system_password_login_active`; default ON; effective on the next
  login without restart.
- With the password OFF, an account without Cognito sees **code only**, whatever its domain and whatever the code
  switch says (the code is the default method).
- `validateUser.do` and `login.do` refuse a password the Global Unit does not allow, before any credential check.
- `crpByEmail.do` reports `passwordEnabled` per Global Unit.
- The welcome email omits the password and explains code sign-in when the Global Unit does not allow the password
  for that account.

### MODIFIED Requirements

- CHG-OTP-LOGIN-001 FN-001, FN-003, FN-005, FN-006, FN-015 and SEC-009: while password sign-in is OFF in a Global Unit,
  the code switch and the exclusion apply only to Cognito accounts, step 4 no longer answers `domainNotEnabled`, and a
  Cognito account is not admitted by the override (the code switch and the exclusion still apply to it as written;
  reworded 2026-10-18, validation WARN-2).
- CHG-OTP-LOGIN-001 DA-002: both descriptions say so.
- Step 3 (CHG-OTP-LOGIN-001 UI-006): a single available method is shown directly; the choice appears only with two.

### REMOVED Requirements

- None.

---

## 10. Approach Options

| Option | What | Trade-off |
|---|---|---|
| A. Screen only | Hide the password field when OFF | Smallest, but a direct POST to `validateUser.do` still signs in. Rejected |
| **B. One rule, enforced everywhere** | One server-side rule decides the methods; step 3, the two password endpoints, `crpByEmail.do` and the welcome email all read it | Touches more files, but the screen, the server and the email can never disagree. **Recommended** |
| C. B plus session re-authentication | Also re-check the method when switching Global Unit inside a session | Closes P7, but changes `ValidSessionCrpInterceptor`, used by every page. Declined (§6) |

---

## 11. Recommended Approach

Option **B**, with the decisions taken on 2026-10-16:

1. **One switch** covering the MARLO password and the Active Directory password.
2. **Rule 3d** (replaces 3c, user decision 2026-10-16): password OFF means the code replaces it for every account
   without Cognito, even with an excluded domain or the code switch OFF. The code is the default method because an
   account may never have received its password.
3. **Password and Cognito never together** (SEC-005 unchanged), so step 3 shows at most two methods; with two, the
   choice menu with the code first.
4. **Session switching unchanged** (accepted risk, Decision Log).
5. **Welcome email** alternative block through one helper for the 7 senders.

The change ships with the switch ON everywhere, so deploying it alters nothing until a super admin turns it OFF.

---

## 12. Risks, Dependencies, And Open Questions

| # | Item | Handling |
|---|---|---|
| R1 | **Lock-out by mail failure:** with the password OFF, sign-in depends on mail delivery (P9) | Turn OFF only after the relay is proven for external domains and stage 1 shows reliable delivery. Runbook: alert on `auth.otp.delivery outcome=email_failed`; turn back ON from System Admin or with one SQL update |
| R2 | **Super admin access** during an incident | Runbook names a path that does not depend on mail (Cognito, another Global Unit, or SQL) |
| R3 | **Missed password entry point** | Enumerate every `userManager.login` caller and `UsernamePasswordToken` before design; the CLARISA filter is a known third route (KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-1) |
| R4 | **Session switch (P7)** lets a member reach a password-OFF Global Unit after a password login elsewhere | Accepted 2026-10-16; recorded in the Decision Log, not fixed here |
| R5 | **Rule 3d overrides the code settings**: an excluded domain, or a Global Unit with the code switch OFF, receives codes while the password is OFF | Intended; both descriptions are amended so a super admin is not surprised |
| R8 | **Code not configured on the server** (`otp.hmac.secret` / `OTP_HMAC_SECRET` missing or invalid): the code cannot be issued (SEC-008 fails closed), so with the password OFF nobody without Cognito could sign in | Resolved by OQ-4: while `OtpKeys.isConfigured()` is false the password is offered even where its switch is OFF, and an ERROR line is logged |
| R6 | **Account without a known password** if the switch is turned back ON after it was created with the switch OFF | Stored password exists but was never sent; such a user signs in with the code (if ON) or asks support. Documented |
| R7 | Cross-spec edit of CHG-OTP-LOGIN-001 (FN-001, FN-003, FN-005, FN-006, FN-015, SEC-009, DA-002, UI-006) | Amend that spec with a Decision Log entry pointing here; ship after its T21 |
| OQ-1 | Parent epic in Jira | Ask before creating the issue |
| OQ-2 | Branch: this spec on `A2-2631-Implement-Center-Email-OTP-Authentication`, or a new branch from `staging` once A2-2631 merges | **Resolved 2026-10-16 (Kenji Tanaka):** the current branch, `A2-2631-Implement-Center-Email-OTP-Authentication`; the code depends on T21, which is only there |
| OQ-3 | Production counts of P8 (accounts that would move to code only) | Measure before turning OFF on any Global Unit |
| OQ-4 | If the code feature is not configured on the server (R8), does the password come back as the last resort, or does sign-in stay closed? | **Resolved 2026-10-16 (Kenji Tanaka):** the password comes back as the last resort, since a missing key is a server fault, not a Global Unit choice. Logged as an error |

---

## 13. Success Criteria

- With the switch ON (default), login behaves exactly as after CHG-OTP-LOGIN-001 T21, proven by the existing suites.
- With it OFF, each row of §4 holds on the screen **and** on a direct POST to `validateUser.do` / `login.do`.
- No combination of the three settings leaves an account without a method (a test enumerates all of them); with all
  three OFF, step 3 shows the code (or, for a username, the message to use the email address).
- Turning it ON again restores the password on the next login, with no restart.
- The welcome email sends the alternative block exactly when the rule denies the password, from all 7 senders.
- The CLARISA technical account still authenticates.

---

## 14. Next Step

```text
/akili-specify changes/password-login-switch
```
