# Center Email OTP Login — Proposal

> **The answer first.** Add passwordless sign-in to MARLO for **pre-registered** users whose email
> domain is allow-listed for the selected Global Unit. MARLO emails a 6-digit one-time code and, once
> the code is verified, signs the user in through the **existing** Shiro login and `finishLogin` tail.
> The portable guide is sound. In MARLO it must be adapted on four points before it can be specified:
> the mailer persists message bodies (so it would store the code in clear text), login is always scoped
> to a Global Unit, there is no rate limiter, and an existing pre-auth endpoint already reveals which
> accounts exist.

---

## 1. Document Control

| Field | Value |
|---|---|
| Spec path | `docs/specs/changes/center-email-otp-login` |
| Slug | `center-email-otp-login` — derived from the free-text argument |
| Type | **Change** |
| Approval Mode | `gated` |
| Status | **Approved** — Kenji Tanaka, 2026-10-08 |
| Date | 2026-10-08 |
| Author | Kenji Tanaka (IBD Team) |
| Jira | **A2-2631** "Implement Center Email OTP Authentication Following the Technical Guide" (User Story, Open, assigned to Kenji). Parent epic **A2-2629** "Full configuration of Cognito" (Open, owned by another team member) |
| Source guide | `CENTER_EMAIL_OTP_IMPLEMENTATION_SPEC.md` (portable spec derived from PRMS; held outside the repository, in the author's Downloads). Referred to below as **the guide**, with its section numbers (`guide §n`) |
| Depends on | none |
| Parallel-safe | **no** — shares `LoginAction`, `APCustomRealm`, `loginForm.ftl`, `login.js`, `CrpByUserEmailAction` and both `APConstants.java` with the pending child `changes/migrate-ad-authentication-to-cognito/directory-retirement` |
| Parent Spec | none (standalone change; related to, not a child of, `changes/migrate-ad-authentication-to-cognito`) |

---

## 2. Intent

Give staff of trusted partner organisations a way into MARLO that needs **no password and no
federation with their organisation's identity provider**: proving they control a mailbox on an
allow-listed domain is enough to authenticate. Authorization stays exactly what MARLO already grants
that person through `crp_users` and roles.

This serves the A2-2629 goal, *"access MARLO without depending on CGIAR identity services"*, for
users that Cognito federation does not cover.

---

## 3. Problem / Current Behavior

| # | Today | Evidence |
|---|---|---|
| P1 | Two authentication paths exist: local email + MD5 password, and Cognito for CGIAR users when `cognito_auth_active` is on for the Global Unit | `APCustomRealm.java:118` (`UsernamePasswordToken \|\| CognitoAuthenticationToken`); `CognitoAuthSpecificity.isActiveFor` (`marlo-data/.../security/CognitoAuthSpecificity.java:64-77`) |
| P2 | A partner user without a CGIAR identity must hold a MARLO-managed password | `LoginAction.java` local branch → `userManager.login(userEmail, password)` |
| P3 | Login is a step flow: email → `crpByEmail.do` lists the account's Global Units → the user picks one → password or Cognito | `loginForm.ftl:32-74`; `login.js` header comments (lines 9-19) |
| P4 | Every login path ends in `finishLogin`, which **requires** a selected Global Unit and `crp_users` membership (gate 4), then loads `custom_parameters` into the session | `LoginAction.java:417-458` |
| P5 | Session establishment after an external proof is already solved for Cognito: stop the pre-auth session, `Subject.login(token)`, reset the request session cache, rebind `ActionContext`, then call `finishLogin` | `CognitoCallbackAction.java:496-555` |
| P6 | `users.email` is `UNIQUE`; the table's charset/collation is `utf8mb3`; `auto_save` is `NOT NULL` with no default | `SHOW CREATE TABLE users` on local `aiccradb1`, run 2026-10-08 |
| P7 | `SendMailS.send()` writes the full message body to `email_log` and dispatches on a background thread without waiting for the result | `SendMailS.java:394` (`emailLog.setMessage(messageContent)`), `:479` (`new ThreadSendMail(...)`) |
| P8 | `SendMailS` drops mail when the Global Unit's notifications are off, and outside production it rewrites the recipient | `SendMailS.java:314` (`isNotificationDisabled`), `:404-420` (`!config.isProduction()`) |
| P9 | No application-level rate limiting exists; Production runs on multiple EC2 instances | `docs/trd/trd.md` §9.5, §1.2 |
| P10 | `crpByEmail.do` answers an unauthenticated caller with whether the account exists, its full name, `isCgiarUser` and its Global Unit memberships | `CrpByUserEmailAction.java:93-137`; declared in `struts-home.xml:9` under the `json-default` package, called by `login.js` before any authentication |
| P11 | Guest roles exist per Global Unit (`G`: 421 for AICCRA 45, 483 for AICCRA_III 47) | `roles` on local `aiccradb1`, run 2026-10-08 |

---

## 4. Proposed Outcome

A pre-registered user of an enabled Global Unit whose email domain is allow-listed sees **"Email me a
sign-in code"** on the authentication step, in place of (or next to) the password field. They receive
a 6-digit code valid for 5 minutes, enter it, and land in MARLO with **the same session, roles and
redirect** a password login gives them. Disabling the feature is configuration only: an empty
allow-list hides the option and makes the server reject every request. Password and Cognito logins
are unaffected.

---

## 5. Scope

| In scope | Detail |
|---|---|
| Per-Global-Unit specificity | Allow-list of email domains (`custom_parameters`, empty = off), with constants in **both** `APConstants.java` files (Hard rule 4) |
| Challenge store | New `otp_challenges` table (HMAC-only: nonce, email hash, code HMAC, expiry, attempts, consumed_at), Flyway migration |
| Code lifecycle | CSPRNG 6-digit code, nonce-bound HMAC, 5-min TTL checked at verify, atomic 3-attempt cap, atomic single-use consume |
| Struts actions | Request-code and verify-code `.do` actions in `struts-home.xml` (no `/api/*`, no new `*.json`) |
| Session | New Shiro token type dispatched in `APCustomRealm`; establishment reuses the Cognito callback sequence and `finishLogin` |
| Email | A dedicated synchronous send path that **never persists the body**, observes failure, and is not silenced by the notification flag |
| Rate limiting | Per email and per IP, shared across instances (database-backed) |
| Login UI | Code step in `loginForm.ftl` / `login.js`; `crpByEmail.do` gains an `otpEnabled` flag per Global Unit, mirroring `cognitoEnabled` |
| Secret | Dedicated HMAC secret delivered as an environment variable through `APConfig` `${key:}`; fail closed when empty |
| Observability | Structured `auth.otp.*` outcome log lines without code, nonce or full email |
| i18n | All copy in `global.properties` (apostrophes doubled) |
| Tests | Unit tests for normalisation, HMAC, expiry, attempts, consume, rate limit; concurrency against a real MySQL copy |

---

## 6. Non-Goals

| Not doing | Why |
|---|---|
| **Auto-provisioning** unknown users | Decided 2026-10-08: pre-registered only. Unknown addresses get a neutral answer and no email |
| Granting or changing any role | Authorization stays `crp_users` + `user_role` as administered today (guide §22) |
| Fixing the `crpByEmail.do` enumeration oracle (P10) | Pre-existing behavior with its own blast radius on the whole login page; recorded as a risk and proposed as a separate spec |
| Cognito-native email OTP | See Option C; not the path the guide or A2-2631 describes |
| Retiring passwords for OTP users | Local password login keeps working for the same accounts |
| Admin UI for the allow-list | Managed through the existing `custom_parameters` administration or a migration |
| Changes to the `/api/**` REST layer | Hard rule 3 |

---

## 7. Affected Users, Systems, And Specs

| Kind | Item |
|---|---|
| Users | Partner-organisation staff with existing MARLO accounts on allow-listed domains; MARLO Support (new "code not received" triage) |
| Code — `marlo-web` | `LoginAction`, new OTP actions, `CrpByUserEmailAction`, `SendMailS` (or a sibling mailer), `APConfig`, `APConstants`, `struts-home.xml`, `loginForm.ftl`, `login.js`, `global.properties` |
| Code — `marlo-data` | `APCustomRealm`, new `OtpAuthenticationToken`, `OtpChallenge` entity + DAO + `@Transactional` manager, `APConstants` |
| Database | `otp_challenges`, rate-limit storage, specificity seed (`parameters` row, empty) |
| Specs | `changes/migrate-ad-authentication-to-cognito` family — `auth-flow` (archived) established the session pattern this reuses; `directory-retirement` (pending) touches the same files |
| Docs | `docs/trd/trd.md` §8.1 (third auth path), §9.5 (rate limiting); `docs/infrastructure.md` §6 (new env var) |

---

## 8. Visual Reference

- **Source:** None.
- **Location:** n/a.
- **Notes:** Declined on 2026-10-08. The code step reuses the existing step layout of `loginForm.ftl`.
  `/akili-specify` describes it in `design.md` (states: email step → code step → resend cooldown →
  error states, guide §24).

---

## 9. Requirement Delta Preview

### ADDED Requirements

- A Global Unit MAY enable email-code sign-in by listing allowed email domains; empty means off, in the UI and on the server.
- The server MUST match the normalised email's domain **exactly** against the list.
- Requesting a code for an allow-listed, pre-registered, active member of the selected Global Unit MUST create one challenge and send one email. Every other case MUST return the same neutral response and send nothing.
- Codes MUST be CSPRNG, stored only as a nonce-bound HMAC, valid 5 minutes, limited to 3 wrong attempts, and single-use under concurrency.
- A verified code MUST produce the same session, roles, custom parameters and redirect as a password login for that user and Global Unit.
- Code requests and verifications MUST be rate-limited per email and per IP, across all instances.
- The code MUST NOT appear in `email_log`, application logs or any other persisted store.
- Users with `is_cgiar_user = 1` on a Global Unit where Cognito is active MUST NOT be able to use OTP to bypass Cognito (see OQ-4).

### MODIFIED Requirements

- `crpByEmail.do` returns an `otpEnabled` flag per Global Unit next to `cognitoEnabled`.
- `APCustomRealm` accepts a third token type; the `UsernamePasswordToken` and `CognitoAuthenticationToken` branches are unchanged.
- The login authentication step offers the code option when the selected Global Unit enables it for the user's domain.

### REMOVED Requirements

- None.

---

## 10. Approach Options

| | A — Port the guide literally | **B — Struts-native, server-side challenge handle** | C — Cognito-native email OTP |
|---|---|---|---|
| Shape | Three JSON endpoints; client carries an HMAC-signed, length-jittered challenge session; rotation on every mismatch | Two `.do` actions; the nonce lives in the **Shiro pre-auth session**, exactly as `COGNITO_PENDING_AUTHORIZATION` does today; `otp_challenges` keeps the atomic updates | Cognito user pool passwordless sign-in with an email code, through the existing Hosted UI / callback |
| Fits MARLO | Poorly: new JSON endpoints conflict with Hard rule 3; the signed-session machinery exists only because PRMS had no server session | Directly: reuses P5's session sequence, the per-Global-Unit specificity pattern (P1) and the step UI (P3) | Partly: aligns with epic A2-2629, but needs every OTP user to have a pool profile and depends on Cognito decisions that are still open in the Cognito family (e.g. OQ-3) |
| Security invariants | All met | All met. Decoys become trivial (a random nonce in session with no row); no client-visible token to tamper with | Delegated to AWS. Rate limiting, sender and templates are Cognito's |
| Cost | Highest (encoder, rotation, length jitter, extra tests) | Medium | Unknown — tier, pricing and pool configuration `UNVERIFIED — confirm with the A2-2629 owner` |
| Matches A2-2631 | Yes | Yes (the story says "adapted to the application's existing architecture") | No — A2-2631 names the guide, which states the flow is **not** a Cognito flow |

---

## 11. Recommended Approach

**Option B.** It is the smallest path that preserves every REQUIRED BEHAVIOR in the guide:

1. **The session does the binding.** `start` stores `{nonce, emailHash, globalUnitId, exp}` in the
   pre-auth Shiro session. `verify` reads it from there, so the email can't be swapped and nothing
   needs signing. This assumes sessions are shared across instances. The memcached session manager
   ships on the Tomcat classpath (`docs/infrastructure.md`), but whether it runs in Production is
   `UNVERIFIED — confirm at source before relying on it` (R8).
2. **The database guards concurrency.** Two atomic updates on `otp_challenges` enforce the rules:
   `UPDATE … SET attempts = attempts + 1 WHERE nonce = ? AND attempts < 3` and
   `UPDATE … SET consumed_at = NOW() WHERE nonce = ? AND consumed_at IS NULL`. The writes go through a
   `@Transactional` manager.
3. **Login reuses the Cognito sequence.** After a successful consume: stop the session →
   `Subject.login(new OtpAuthenticationToken(userId))` → reset the request session cache → rebind
   `ActionContext` → `finishLogin(user, globalUnit, returnUrl)`. Gate 4 and the custom parameters
   apply unchanged.
4. **Email gets its own path.** A synchronous OTP send that logs only subject, recipient hash and
   outcome, never the body. Whether the non-production recipient rewrite applies to it is decided at
   specify time (OQ-5).
5. **Rate limiting reads the challenge table.** The per-email `start` limit is a `COUNT(*)` over
   `otp_challenges` by `email_hash` inside the window. Per-IP limits, and verify limits, use a small
   counter table. Both are shared by every instance, because the store is RDS.
6. **The resend policy is supersede (guide §25, Option A).** A new code invalidates every earlier
   open challenge for the same email hash, in the same transaction.

Proposed defaults (all open to the specify review): TTL 5 min, 6 digits, 3 attempts; `start` 5 per
15 min per email and 20 per IP; `verify` 10 per email and 50 per IP; resend cooldown 30 s.
Expiration folds into the generic "code incorrect or expired" message.

---

## 12. Risks, Dependencies, And Open Questions

### Risks

| # | Risk | Mitigation |
|---|---|---|
| R1 | **The code ends up in `email_log` in clear text** if the existing `send()` is reused (P7) | Dedicated send path; a test asserts that no `email_log` row contains the code |
| R2 | **Silent non-delivery.** `ThreadSendMail` swallows failures, and the notification flag drops mail (P7, P8) | Synchronous send with its result observed; OTP mail exempt from the notification flag |
| R3 | **No anti-enumeration gain** while `crpByEmail.do` reveals existence, name and memberships (P10) | OTP adds no new oracle (neutral responses everywhere). A follow-up spec is proposed for `crpByEmail.do`. Accepted explicitly, not ignored |
| R4 | **Cognito bypass.** An allow-listed domain could let a CGIAR-migrated user skip Cognito and the SEC-005 relay guard | Never allow-list `cgiar.org`; refuse OTP for `is_cgiar_user = 1` where Cognito is active (OQ-4) |
| R5 | **Shared trust boundary.** Whoever controls a partner mailbox is that user (guide §29) | Pre-registered only, no role changes, short TTL, single use, alerts on attempt spikes |
| R6 | **File collision** with the pending `directory-retirement` child | Not parallel-safe; sequence the work, or coordinate in separate worktrees |
| R7 | **Migration safety.** The specificity seed must not assume Global Unit ids 45/47 exist (migrations seed only up to 28) | Seed only the catalog `parameters` row; enable per Global Unit by configuration (`marlo-migration` skill) |
| R8 | **Multi-instance assumptions.** The pre-auth session must survive instance hops | Confirm the memcached session manager is active in Production (`docs/infrastructure.md` lists it as undocumented) |

### Dependencies

- Environment variable for the HMAC secret in every environment (same delivery model as the Cognito keys).
- SMTP sender with aligned SPF/DKIM/DMARC; partner IT may need to allow-list the sender.

### Open Questions

| ID | Question | Owner |
|---|---|---|
| OQ-1 | Which Global Units, and which partner domains, are enabled first? (Deferred on 2026-10-08) | Product owner |
| OQ-2 | Does Guest (or any baseline role) expose more than intended to OTP-authenticated users? A `getPermissions` audit on the pilot users is required before go-live (guide §33.8) | Kenji + PMU |
| OQ-3 | Does the A2-2629 owner accept a non-Cognito auth path under the Cognito epic, or prefer Option C? | A2-2629 owner |
| OQ-4 | `is_cgiar_user = 1` accounts: always refuse OTP, or refuse only where Cognito is active for the Global Unit? | Kenji |
| OQ-5 | Outside production, does the OTP email go to the real recipient (needed for UAT) or to the support redirect? | Kenji + QA |
| OQ-6 | Should deactivation (`is_active = 0` or removal from `crp_users`) end live sessions? Today no path does this (guide §21) | Tech lead |
| OQ-7 | Is the `crpByEmail.do` enumeration fix (R3) opened as its own spec now? | Kenji |

---

## 13. Success Criteria

| ID | Criterion |
|---|---|
| SC-1 | With an empty allow-list, the option is hidden, every server request is rejected, and password and Cognito logins pass their existing tests unchanged |
| SC-2 | A pre-registered pilot user on an allow-listed domain signs in with the emailed code and gets the same session attributes, roles and landing page as with a password |
| SC-3 | No plaintext code or email address in `otp_challenges`, `email_log` or application logs during a full flow |
| SC-4 | 10 concurrent verifications of the right code yield exactly one login; 10 concurrent wrong codes leave `attempts ≤ 3` (real MySQL) |
| SC-5 | An expired, consumed, superseded or exhausted challenge never authenticates, even with the correct code |
| SC-6 | Exceeding the per-email or per-IP limit returns the rate-limit message, whichever instance serves the request |
| SC-7 | Unknown, inactive, non-member and CGIAR-blocked addresses get a response identical to the success path, and no email is sent |
| SC-8 | Compile and Checkstyle gates are green (`marlo-verify`), and the migration passes the `marlo-migration` checklist on a throwaway database copy |

---

## 14. Next Step

After approval:

```text
/akili-specify changes/center-email-otp-login
```

Recommended depth: **full** (requirements, design with a Premise Ledger, tasks). This is a new
authentication path, with a migration and security invariants. Effort per the routing table: `xhigh`
for the save/consume ordering and session establishment, `max` for the migration.
