# Center Email OTP Login — Design

**Spec ID:** `CHG-OTP-LOGIN-001`
**Status:** Approved (2026-10-09). Judgment Day `APPROVED`; round 1 and Adjust fixes applied (see [`judgment.md`](./judgment.md))
**Owner:** IBD Team — Alliance of Bioversity International and CIAT (Kenji Tanaka)
**Last Updated:** 2026-10-09
**Implements requirements:** FN-001–FN-019, SEC-001–SEC-011, OPS-001–OPS-004, DA-001–DA-003, UI-001–UI-005, NF-001–NF-002 (all of `requirements.md` §3)
**Touches modules:** `marlo-utils` (`APConfig`), `marlo-data`, `marlo-web`
**Exploration:** scout pass at commit `398d7b4058`; its citations are carried into §17 *Premise Ledger*. Judgment Day round 1 re-ran them (`judgment.md`)

---

## Executive Summary

The design adds **two pre-auth `.do` actions with a JSON result**, `otpRequestCode` and
`otpVerifyCode`. They live in a new `homeOtp` Struts package with a declared unlogged stack and
an explicit JSON `root`, and `login.js` calls them from the authentication step.

**Every request writes a challenge row.** An eligible account gets a code by email. An ineligible
account gets a **decoy**: a row whose code is random and never sent. Supersede, attempts, expiry and
consume therefore behave identically for every account state, inside the database. Eligibility is
decided only after a successful consume.

**What surrounds the challenge:**
- The session holds only a handle: nonce, email, Global Unit, return URL, terms flag.
- A verified code logs in through a **third Shiro token type**, then replays the **complete Cognito callback sequence** into the unchanged `finishLogin` tail.
- The allow-list is a string specificity, read **without** the Hibernate caches.
- The HMAC secret comes from the environment.
- The code email goes through a **new asynchronous mailer** with timeouts. It is never persisted, and its outcome is logged.

Password and Cognito paths do not change behavior.

---

## 1. Architecture Summary

```text
loginForm.ftl / login.js  (step 1 email → crpByEmail.do → step 2 Global Unit → step 3 auth)
        │  crpByEmail.do also returns  otpEnabled  per Global Unit (configured + domain + SEC-009 only)
        │
        ├── password ──► login.do (unchanged)
        ├── Cognito  ──► cognitoLogin.do / cognitoCallback.do (unchanged)
        └── OTP ──► otpRequestCode.do {email, globalUnitId, agree}           (JSON, homeOtp package)
                      rate limits (incl. 30-s cooldown bucket) → configured → domain → terms
                      → eligible?  ── yes: issue(row, real code) + mail async
                                    └ no:  issue(row, random undeliverable code), no mail
                      (both: supersede open rows for the email, same transaction)
                      Shiro session ← OtpPending{nonce, email, globalUnitId, returnUrl, agree}
             ──► otpVerifyCode.do {code}                                     (JSON)
                      rate limits → pending? → row checks → MAC
                      ├ mismatch: conditional attempts+1 → re-read → mismatch | exhausted
                      └ match: conditional consume (open AND attempts<3 AND not expired)
                               → eligibility re-check (decoy fails here) → agree_terms + saveLastLogin
                               → setUser(detached) → stop session → Subject.login(OtpAuthenticationToken)
                               → reset request session cache → fresh SessionMap
                               → finishLogin(user, gu, returnUrl) → {status:ok, redirect}
```

---

## 2. Module Footprint

### marlo-utils
- Modified: `utils/APConfig.java`. Adds `otp.hmac.secret`, `otp.trusted.proxies` and `otp.smtp.timeout.ms`, each `@Value("${key:}")` with an empty default, plus getters.

### marlo-data
- New: `data/model/OtpChallenge.java` + `resources/xmls/OtpChallenges.hbm.xml`, registered in `hibernate.cfg.xml`.
- New: `data/dao/OtpChallengeDAO.java`, `data/dao/mysql/OtpChallengeMySQLDAO.java`. Holds the conditional native updates and the **uncached** allow-list read (DD-12).
- New: `data/dao/OtpRateLimitDAO.java`, `data/dao/mysql/OtpRateLimitMySQLDAO.java`. Native SQL only; no entity.
- New: `data/manager/OtpChallengeManager.java` + `impl/OtpChallengeManagerImpl.java`.
- New: `data/manager/OtpRateLimitManager.java` + `impl/OtpRateLimitManagerImpl.java`.
- Both managers carry `@Transactional` on every method that writes (JI-8).
- New: `security/OtpAuthenticationToken.java`.
- Modified: `security/APCustomRealm.java`. `supports()` accepts the new token, and a no-I/O branch sits next to the Cognito one.
- Modified: `config/APConstants.java`. Adds `OTP_ALLOWED_EMAIL_DOMAINS`.

### marlo-web
- New, package `security/otp/`:
  - `OtpKeys`
  - `OtpCodes`
  - `OtpDomainPolicy`
  - `OtpEligibility`
  - `OtpRateLimiter` (a thin facade over the manager)
  - `OtpPending`
  - `OtpLog`
  - `ClientIpResolver`
- New: `security/ReturnUrls.java`. Holds `sameOriginOrNull`, extracted from `CognitoLoginAction`, which now delegates to it (DD-11).
- New: `utils/OtpMailSender.java`.
- New: `action/home/OtpRequestCodeAction.java` and `action/home/OtpVerifyCodeAction.java`. Both extend `LoginAction` to reach `finishLogin`.
- Modified: `action/json/global/CrpByUserEmailAction.java`. Adds the `otpEnabled` key per Global Unit.
- Modified: `resources/struts-home.xml`. New package `homeOtp` (DD-1).
- Modified: `config/APConstants.java`. Adds `OTP_ALLOWED_EMAIL_DOMAINS` and `OTP_PENDING_CHALLENGE`.
- Modified: `webapp/WEB-INF/global/pages/loginForm.ftl`. New `#login-step-otp` block.
- Modified: `webapp/global/js/login/login.js`. OTP mode, code step, countdown, request sequencing.
- Modified: `resources/global.properties`. Page and email copy.
- Modified: the FTL that references `login.js`. Bump the `?YYYYMMDD` cache-buster.
- New: `resources/database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__AddEmailOtpLogin.sql`. Take the timestamp from the real clock.
- New tests (JUnit 4, hand-written fakes):
  - `OtpDomainPolicyTest`
  - `OtpCodesTest`
  - `OtpKeysTest`
  - `OtpEligibilityTest`
  - `OtpRateLimiterTest`
  - `ClientIpResolverTest`
  - `OtpRequestCodeActionTest`
  - `OtpVerifyCodeActionTest`
  - `ReturnUrlsTest`
  - extended: `APCustomRealmDispatchTest`, `CrpByUserEmailActionTest`, and the stack coverage tests (P-21)
- New: `docs/specs/changes/center-email-otp-login/runbook.md` (OPS-004).
- New: `docs/specs/changes/center-email-otp-login/probes/` (§12 concurrency probe script and its recorded output).

### Shared-file note
`LoginAction`, `APCustomRealm`, `loginForm.ftl`, `login.js` and both `APConstants` are also targets of
the pending Cognito child `directory-retirement`. This spec is not parallel-safe with it.

---

## 3. Data Model Changes

### Migration — `V2_6_0_<YYYYMMDD>_<HHMM>__AddEmailOtpLogin.sql`

The migration is built with the `marlo-migration` skill.
- It is idempotent: `CREATE TABLE IF NOT EXISTS`, and seeds through `INSERT … SELECT … WHERE NOT EXISTS`.
- It references no `global_unit_id`, user or role.

**Table `otp_challenges`** (InnoDB, utf8mb4). It holds one row per request, decoys included (DD-5).

| Column | Type | Null | Notes |
|---|---|---|---|
| `id` | BIGINT AUTO_INCREMENT | NO | PK |
| `nonce` | VARCHAR(32) | NO | **UNIQUE**. 22-char base64url of 16 random bytes |
| `email_hmac` | CHAR(64) | NO | Index. Hex HMAC of the normalised email (SEC-002) |
| `global_unit_id` | BIGINT | NO | **No FK**. Throw-away auth state |
| `code_hmac` | CHAR(64) | NO | Hex HMAC of `nonce|code`. For a decoy, the code is random and discarded unsent (SEC-001) |
| `expires_at` | DATETIME(3) | NO | Index. UTC |
| `attempts` | TINYINT | NO | Default 0 |
| `consumed_at` | DATETIME(3) | YES | Set on success **or supersede** (DD-6) |
| `created_at` | DATETIME(3) | NO | Default `CURRENT_TIMESTAMP(3)` |

No column distinguishes a decoy from a real row. The row cannot tell, because eligibility is evaluated
only at consume time (DD-5).

**Table `otp_rate_limits`**

| Column | Type | Null | Notes |
|---|---|---|---|
| `bucket` | CHAR(64) | NO | PK part 1. Hex HMAC of `<route>|<dimension>|<value>` |
| `window_start` | DATETIME | NO | PK part 2. Start of the bucket's fixed 15-min window |
| `hits` | INT | NO | Atomic upsert increment |

**Table `otp_cooldowns`** (RJ-1). This is a sliding interval, not a window.

| Column | Type | Null | Notes |
|---|---|---|---|
| `bucket` | CHAR(64) | NO | PK. Hex HMAC of `cooldown|email|<normalised>` |
| `last_request_at` | DATETIME(3) | NO | Time of the last accepted request |

**Catalog seed.** The rows are inserted **from `global_unit_types`**, by selecting the ids among 1, 2, 3 and 4 that exist. A database missing a type gets no row and no FK failure (P-23, JI-10). Type 2 is included because migration-only databases hold "Center" as id 2 (RJ-4). Each row has:
- key `crp_otp_allowed_email_domains`;
- format 4 (string);
- category 2;
- `default_value` empty.

No `custom_parameters` rows are seeded (DA-002).

### Entity
`OtpChallenge` maps `otp_challenges` through hbm, following the `EmailLog` pattern (P-16). It is neither
auditable nor soft-deletable (guide §9.1).

### Backfill
Not applicable.

---

## 4. API / Action Surface

### Struts package `homeOtp` (DD-1)

- `namespace="/"`, `extends="marlo-default,json-default"` (P-26).
- Declares `otpUnloggedStack`, which is the same interceptors as `cognitoUnloggedStack`: `logErrorStatus`, `i18nFile`, `defaultStack`.
- Both actions use that stack and a `json` result with `root = response`, `noCache = true` and `excludeNullProperties = true`. `response` is a small serializable map the action fills. **No other action getter is serialized** (JD-5).
- The package declares a **global `input` result** of the same JSON shape. A conversion or validation interruption answers `{status: unavailable}`, never a 500 (RJ-9).
- Both actions implement `org.apache.struts2.action.ParameterNameAware` (present in struts2-core 6.8.0, P-28). They accept **only** `email`, `globalUnitId` and `agree`, or `code`. None of the setters inherited from `LoginAction` can be bound from the request (RJ-9).

| Route | Class | Input | `response` |
|---|---|---|---|
| `/otpRequestCode.do` (POST) | `OtpRequestCodeAction` | `email`, `globalUnitId`, `agree` | `{status, destination?}` |
| `/otpVerifyCode.do` (POST) | `OtpVerifyCodeAction` | `code` | `{status, redirect?}` |

`status` is a closed vocabulary, shared by the server, `login.js` and i18n.

| `status` | Route | Meaning | i18n key |
|---|---|---|---|
| `sent` | request | **Neutral response**, for eligible *and* ineligible accounts. `destination` is the masked submitted email, always present (FN-004/005) | `login.otp.sent` |
| `domainNotEnabled` | request | Domain not on the Global Unit's list, unknown Global Unit, or feature unconfigured (FN-006, SEC-008) | `login.otp.error.domain` |
| `termsRequired` | request | `agree` is not `true` (parity with Cognito's refusal of `agree=false`; independent of account state) | existing terms message key |
| `rateLimited` | both | Any limit, including the 30-s cooldown (FN-008, SEC-010) | `login.otp.error.rateLimited` |
| `unavailable` | both | Internal failure; fail closed | `login.otp.error.unavailable` |
| `mismatch` | verify | Wrong code with attempts left, or no pending challenge (FN-016) | `login.otp.error.mismatch` |
| `exhausted` | verify | The wrong code that reached 3, or an exhausted challenge (FN-012) | `login.otp.error.exhausted` |
| `invalid` | verify | Expired, consumed, superseded, or not eligible after consume (FN-014/015) | `login.otp.error.invalid` |
| `ok` | verify | Signed in; `redirect` holds the URL to navigate to | — |
| `failed` | verify | `finishLogin` refused after login | Same generic category as Cognito's `AUTH_ERROR_FAILED` |

**Why JSON and not `/api/*` or a form post (Hard rule 3):** the login page's pre-auth calls are
already JSON `.do` actions (P-14). A form post would re-render `login.ftl` and lose the step state on
every wrong code.

### Spring MVC REST
Not applicable.

---

## 5. Backend Module Design

### 5.1 Request code — `OtpRequestCodeAction`

Steps run in this fixed order. **No step before step 6 reads account state**, so steps 1–5 answer
identically for every account (JD-1).

1. **Rate limits**, before anything else (SEC-010). The limiter counts three buckets:
   - `request|email|<normalised>`, limit 5 per 15 min;
   - `request|ip|<resolved IP>`, limit 20 per 15 min;
   - the cooldown in `otp_cooldowns` (FN-008, RJ-1).

   For the cooldown, the request computes `now` once in Java. One transaction then:
   - upserts the bucket, setting `last_request_at = now` only if the stored value is at least 30 s older than `now`;
   - reads the value back. It equals `now` only when this request was accepted.

   This gives a true "at least 30 seconds apart" interval with no reliance on affected-row semantics.

   An atomic upsert per bucket makes concurrent requests safe (JI-12). Over any limit, or a rejected cooldown → `rateLimited`.
2. **Shape.** The email must have valid syntax, at most 254 characters, and exactly one `@`; `globalUnitId` must be numeric. Otherwise → `domainNotEnabled`.
3. **Configured?** If `OtpKeys` is unconfigured → `domainNotEnabled` (SEC-008).
4. **Domain.** Read the Global Unit's allow-list through the uncached read (DD-12), then normalise and match exactly. `cgiar.org` is ignored. No match, or an unknown Global Unit → `domainNotEnabled`.
5. **Terms.** If `agree` is not `true` → `termsRequired`.
6. **Eligibility** (`OtpEligibility`, the single predicate):
   - the user exists, by case-insensitive lookup (P-12);
   - `isActive()`;
   - `existCrpUser(user, gu)`, the same gate as `finishLogin` (P-2);
   - **not** (`isCgiarUser()` **and** `CognitoAuthSpecificity.isActiveFor(gu, customParameterManager, parameterManager)`) (SEC-009).
7. **Issue.** `OtpChallengeManager.issue(emailHmac, gu, nonce, codeHmac, expiresAt)` runs in **one transaction**: it supersedes the email's open rows, then inserts the new row. It runs for **both** outcomes. The code is a fresh CSPRNG code:
   - **eligible:** the code is handed to `OtpMailSender.sendAsync`;
   - **ineligible:** the code is discarded.

   A store failure → `unavailable`, and nothing is written to the session.
8. **Purge** rows expired more than 1 h ago, in a **separate** transaction after the issue commits. A failure is caught and logged, and never affects the response (OPS-003, JI-7).
9. **Session.** Store a fresh `OtpPending` (nonce, normalised email, `globalUnitId`, `returnUrl = ReturnUrls.sameOriginOrNull(Referer)`, `agree`). It **replaces** any previous value.
10. **Respond** `sent`, with the masked submitted email.
11. **Log** one `request` line (OPS-001). Outcomes: `sent`, `neutral`, `denied_domain`, `terms_required`, `rate_limited`, `unconfigured`, `internal_error`.

### 5.2 Verify code — `OtpVerifyCodeAction`

1. **Rate limits:** `verify|email|<pending.email>` (10 per 15 min) and `verify|ip|<IP>` (50 per 15 min). With no pending, only the IP bucket is counted. The email key comes from the session, so **no lookup precedes counting** (JD-3).
2. **Shape.** `code` must be exactly 6 digits. Otherwise → `mismatch`.
3. **No `OtpPending`** → `mismatch` (FN-016).
4. **Load the row** by `pending.nonce`. A missing row (purged, or never written) → `invalid`. Then, in order:
   - `consumed_at` is set → `invalid`;
   - `attempts ≥ 3` → `exhausted`;
   - `now > expires_at` → `invalid`.
5. **Compare** in constant time: `HMAC(nonce|code)` against `code_hmac` (SEC-003).
6. **Mismatch** → `OtpChallengeManager.registerMismatch(nonce)`. In one transaction it runs a conditional increment (`attempts + 1` only while `consumed_at` is null and `attempts < 3`), then re-reads `attempts`.
   - Affected 1 and a new value of 3 → `exhausted`.
   - Affected 1 and a new value below 3 → `mismatch`.
   - Affected 0: re-read the row.
     - Consumed or expired → `invalid`.
     - `attempts ≥ 3` → `exhausted`.

     This answers a concurrent supersede or consume the same way as the step 4 pre-checks (RJ-3).

   Decoys run through this same path, because they are real rows (DD-5).
7. **Match** → `OtpChallengeManager.consume(nonce)`. This is a conditional update that sets `consumed_at` only while `consumed_at` is null **and** `attempts < 3` **and** `expires_at > now` (JS-1). If affected ≠ 1 → `invalid` (FN-013).
8. **Remove `OtpPending`.** Re-run `OtpEligibility` for `pending.email` and Global Unit, **including the uncached allow-list**. Not eligible → `invalid` (FN-015). A decoy that guessed its 1-in-10⁶ random code also ends here, and its answer is the same `invalid` an expired code gets.
9. **Session sequence.** This is the **complete** Cognito order (`CognitoCallbackAction.java:476-529`, JD-4):
   1. Load the user entity, `loggedUser`. If it is null (deleted since the eligibility check) → `failed`, matching Cognito's guard at `CognitoCallbackAction.java:477-482` (RJ-2).
   2. If `pending.agree`, call `setAgreeTerms(TRUE)` (record, never revoke, as at `ValidateUserAction.java:143-146`).
   3. `saveLastLogin(user)`. This is the write path that persists `agree_terms`. `finishLogin` calls it again (P-2), the same double call Cognito makes.
   4. `setUser(detached user carrying only the email)`. `finishLogin` dereferences the inherited `user` on its refusal paths (P-27).
   5. Stop the session.
   6. `Subject.login(new OtpAuthenticationToken(userId))`. An `AuthenticationException` → `failed`.
   7. `ShiroRequestSessionCacheResetter.clearCachedSession`.
   8. `freshSessionMap()` → `setSession`, and rebind `ActionContext`.
10. **`finishLogin(loggedUser, gu, pending.returnUrl)`**. Pass the loaded entity, never the detached `user` field: `finishLogin` reads `loggedUser.getId()` for gate 4 (RJ-2).
    - `SUCCESS` → `redirect = <baseUrl>/<crpSession>/crpDashboard.do`, the target of the `login` and `cognitoCallback` `success` results (`struts-home.xml:74-76, 86-88`). Types 1, 3, 4 and 5 return `SUCCESS` (`LoginAction.java:497-511`).
    - `LOGIN` → `redirect = url`. For type 2, `finishLogin` sets this to `centerDashboard.do`; for a `.do` return URL, it is that URL.
    - `INPUT` → `failed`, with `redirect = /login.do?authError=failed` (Cognito V-7 parity).
11. **Log** one `verify` line. Outcomes: `ok`, `mismatch`, `exhausted`, `invalid`, `not_eligible`, `rate_limited`, `failed`, `internal_error`.

### 5.3 Supporting components

| Component | Behavior | Req |
|---|---|---|
| `OtpKeys` | Reads `APConfig.getOtpHmacSecret()` once. The value must be **standard base64 that decodes to ≥ 32 bytes** (≥ 256 bits; generate it with `openssl rand -base64 48`). Anything else → **unconfigured**, logged once at WARN without the value. Derives `k_email`, `k_code` and `k_bucket` as `HMAC-SHA256(decoded, "<tag>\0")` with distinct tags (JI-3) | SEC-008 |
| `OtpCodes` | `SecureRandom.nextInt(1_000_000)`, zero-padded to 6. 16-byte nonce. HMAC-SHA256 hex. Compares with `MessageDigest.isEqual` on decoded bytes; returns false on any shape error and never throws | FN-009, SEC-001/003 |
| `OtpDomainPolicy` | Pure: `normaliseEmail`, `domainOf` (exactly one `@`), `parseAllowList` (guide §6.2 rules plus dropping `cgiar.org`), `isAllowed`, `mask` | FN-003, SEC-009 |
| `OtpEligibility` | The one predicate (§5.1 step 6), called at request and after consume | FN-004/005/015 |
| `OtpRateLimiter` → `OtpRateLimitManager` | **Window buckets.** In its own `@Transactional` call, a bucket upserts `hits = hits + 1` for `(bucket, window_start)`, then reads `hits` back in the same transaction. Over the limit → limited.<br>**Cooldown.** The conditional upsert-and-read-back on `otp_cooldowns` (§5.1 step 1).<br>A store failure **fails closed** (`unavailable`). Windows older than 1 day and cooldown rows older than 1 h are purged opportunistically in a separate transaction | SEC-010, FN-008, DA-003 |
| `ClientIpResolver` | Starts from `getRemoteAddr()`. If that address falls inside an entry of `otp.trusted.proxies` (comma-separated IPv4/IPv6 addresses **or CIDR ranges**), walks `X-Forwarded-For` from the right and takes the first hop not inside the list. A missing, empty or malformed header from a trusted peer → `getRemoteAddr()`. Headers from untrusted peers are ignored (JI-5) | SEC-011 |
| `OtpPending` | Serializable session value under `APConstants.OTP_PENDING_CHALLENGE`: nonce, normalised email, `globalUnitId`, `returnUrl`, `agree`. No code and no MAC. Binds the challenge to session, email and Global Unit (SEC-004, JD-3) | SEC-004 |
| `OtpMailSender` | Builds its own `javax.mail` `Session` from the `APConfig` SMTP getters `SendMailS` uses (P-8), plus connect, read and write timeouts (`otp.smtp.timeout.ms`, default 10 000).<br>Sends to **one** recipient on a dedicated executor: 2 daemon threads, a bounded queue of 100, and an abort policy that is caught.<br>A rejection, a timeout or a send error is logged as an `auth.otp.delivery outcome=email_failed` line at WARN; a success as `outcome=delivered`. The response was already `sent` for every state, so a rejection reveals nothing (JI-4).<br>It never touches `email_log`, the notification flag or the support redirect. The recipient is always the submitted address, in every environment (OQ-5 default, JI-2) | SEC-001, OPS-002, FN-018, NF-001 |
| `OtpLog` | slf4j. `auth.otp.<request|verify|delivery> outcome=<enum> gu=<acronym> domain=<domain> ms=<n>`. Values pass through `LogSanitizer`. No code, nonce, full email or session ID. Writes exactly one `request` or `verify` line per call, and one `delivery` line per handed-off email (requirements OPS-001 as amended, JI-1) | OPS-001/002 |

### 5.4 Realm — `APCustomRealm`

`supports()` gains `|| token instanceof OtpAuthenticationToken`. A branch **above** the existing cast
returns `SimpleAuthenticationInfo(userId, token, realmName)` with no I/O, mirroring Cognito (P-3).
Authorization is untouched (SEC-007).

---

## 6. Frontend / UX Component Architecture

| Piece | Design | Req |
|---|---|---|
| `crpByEmail.do` | Each Global Unit entry gains `otpEnabled`. It is `true` when the feature is configured, the user's domain is allowed for that unit, and SEC-009 does not refuse the user. It does **not** use `isActive()` or membership, so it discloses nothing beyond today's `isCgiarUser` and unit list (JI-11).<br>The allow-lists for all listed units come from **one** uncached query per call, and only when the feature is configured (RJ-6) | UI-001, SEC-006 |
| `login.js` mode | Extends the decision at `login.js:471`. `COGNITO` keeps priority. Otherwise the mode is `LOCAL`, plus an **"Email me a sign-in code"** button when `otpEnabled`. The terms check runs first, as for the other buttons. The password field stays | UI-001, FN-019 |
| States | *Sending* (button disabled, "Sending…"), *code sent* (neutral text with the masked destination), *verifying* ("Verifying…"), one inline error per `status`, a resend countdown, and "use a different email" | UI-002 |
| `#login-step-otp` (FTL) | Status text in an `aria-live="polite"` region with `role="status"`; errors with `role="alert"`. A labelled code input: `inputmode="numeric"`, `autocomplete="one-time-code"`, `maxlength="6"`, digits stripped on input. **Focus moves to the input when the step opens** (JI-6). A Verify button, and a Resend button showing "Resend in N s" for 30 s. This is the first timer code in `login.js` (P-24). A "use a different email" link | UI-002, UI-003 |
| Request sequencing | `requestSeq` is incremented on every call and on "use a different email". Stale responses are dropped. Buttons are disabled while their request is in flight, and Enter submits | UI-004 |
| Expired / exhausted | The cooldown is cleared so that Resend is enabled at once. The server still enforces its own 30-s bucket | UI-002 |
| Success | `window.location.href = redirect` | FN-011 |
| Copy | Every string is a `global.properties` key, with apostrophes doubled. The email covers the FN-017 items. Light theme with the existing login tokens | UI-005, FN-017 |
| Cache-busting | Bump the `?YYYYMMDD` on the `login.js` reference | — |

Browser console logging is limited to HTTP status.

---

## 7. Persistence & Phase Replication Plan

Not applicable. Neither table is phased. Writes go only through the two `@Transactional` managers.

## 8. Validation & Save Pipeline

Not applicable as a save section. Each action validates the shape of its own input (§5.1 step 2,
§5.2 step 2) and answers with a `status`; it never throws. There is no `Validator`, because no domain
entity is saved.

## 9. Specificity / Feature-Flag Strategy

| Item | Value |
|---|---|
| Key | `crp_otp_allowed_email_domains` (comma-separated string) |
| Constant | `OTP_ALLOWED_EMAIL_DOMAINS` in **both** `APConstants.java` files, equal to the key (Hard rule 4) |
| Resolution | An active `custom_parameters` row for the Global Unit wins; otherwise the catalog `default_value` (empty). This is the shape of `CognitoAuthSpecificity.isActiveFor` (P-10) |
| Read path | **A native, uncached select** in `OtpChallengeDAO` (DD-12), used by request-code, verify-code and `crpByEmail.do`'s `otpEnabled`. A change, or the rollback "empty the list", applies on the next request on every instance |
| Off | Empty or absent value, **or** an unconfigured secret |

## 10. Integration Points

| System | Use |
|---|---|
| SMTP (existing `APConfig` settings) | One message per eligible issue, via `OtpMailSender` |
| Shiro | New token type; existing session manager |
| Cognito | Read-only: `CognitoAuthSpecificity` for SEC-009 |

## 11. Observability

- The `auth.otp.request`, `auth.otp.verify` and `auth.otp.delivery` lines (§5.3).
- **Code-not-received triage (runbook):**
  - a `delivery outcome=email_failed` line means the send failed;
  - a `request outcome=neutral` line means the account was ineligible (server-side only);
  - neither line means the request never arrived.

## 12. Performance, Scalability & Concurrency Verification

| Topic | Design |
|---|---|
| Load | A request-code call costs:<br>• 2 window upserts and 1 cooldown upsert;<br>• 1 uncached allow-list read;<br>• 1 user lookup;<br>• 1 transaction (supersede + insert).<br>The mail send runs off the request thread (NF-001). `crpByEmail.do`, which is pre-auth and unthrottled as it is today, gains **one** uncached query per call, and only when the feature is configured (RJ-6) |
| Volume | Expected in the tens per day. Decoy rows are bounded by the rate limits and purged after 1 h |
| Concurrency probe | A script under `probes/` runs against a **throwaway copy** of the local MySQL. It runs four cases: 10 parallel correct verifies (exactly 1 consume); 10 parallel wrong codes (`attempts` ends at 3); a mixed burst of 3 wrong + 1 correct after exhaustion (0 consumes); and concurrent requests for one email (at most 1 open row). Output is recorded at the execute HITL pause (requirements §6) |

## 13. Security Considerations

| Threat (guide §29) | Mitigation |
|---|---|
| T1 brute force | 3 attempts enforced in the consume condition itself; 5-min TTL; shared per-email, per-IP and cooldown buckets |
| T2 replay | Conditional consume |
| T3 enumeration | Steps 1–5 of request-code read no account state. Every request writes a row and supersedes, so verify-side behavior is identical (DD-5). The mail send is async. `otpEnabled` excludes active state. **Residual:** `crpByEmail.do` (C8) |
| T6 database leak | HMAC-only columns; no column marks a decoy |
| T7/T8 tampering, cross-user | Nothing client-side to tamper with; the pending challenge is bound to session + email + Global Unit |
| T9 escalation | No role or membership writes |
| T10 races | Conditional updates, atomic upsert buckets, supersede + insert in one transaction (§12 probe) |
| T11 session theft | Session rotated on login (SEC-005) |
| T15 lookalikes | Exact match |
| T16 log leakage | `OtpLog` only; `email_log` is never written |
| Victim lockout | A third party can spend a user's per-email buckets and block code sign-in for 15 min. **Accepted**: password and Cognito logins are unaffected |
| Victim code supersede | A third party can request for a victim's email once every 30 s, superseding the victim's open code (RJ-11). The victim then needs a new code. The per-email bucket caps this at 5 per 15 min. **Accepted** for the same reason |
| Residual timing | An eligible request does more database work than an ineligible one: the membership read, the SEC-009 reads and the mail hand-off. The gap is a few indexed reads, and the send is async. It is not measured. **Accepted**, and recorded as residual for SEC-006 (RJ-10); the pre-existing `crpByEmail.do` disclosure dominates it |
| Login CSRF | **Residual, parity:** no login form has a CSRF token (P-9) |

## 14. Backwards Compatibility & Rollout

1. **Migration.** The feature ships off. Confirm by hand that Production actually applied it (guide W16).
2. **Deploy.** Without a valid `OTP_HMAC_SECRET` the feature is inert.
3. **Per environment:**
   - set the secret and `OTP_TRUSTED_PROXIES`;
   - settle P-4 (affinity), P-6 (proxy), P-18 (environment binding) and P-25 (RDS).
4. **Pilot:** enable one Global Unit and one domain (OQ-1) with a test mailbox, then run the AC walkthrough.
5. **Expand** domain by domain.

**Rollback**, cheapest first. All of these apply on the next request (DD-12):
- empty the value;
- unset the secret;
- revert the deploy;
- drop the tables.

---

## 15. Decision Records

### DD-1 — JSON `.do` actions in a dedicated `homeOtp` package
- **Decision:** a package extending `marlo-default` and `json-default`, with `otpUnloggedStack` and a JSON `root` of `response`.
- **Rationale:** `homeJson` lacks `logErrorStatus` and `i18nFile`. A root-less JSON result on a `LoginAction` subclass would serialize every getter (JD-5). Pre-auth JSON on the login page already exists (P-14), and a form post would lose step state.
- **Fallback if P-26 fails:** declare the `json` result type inside the `home` package.
- **Reversion challenge:** n/a.

### DD-2 — Challenge handle in the Shiro session
- **Decision:** `OtpPending` (nonce, email, Global Unit, return URL, terms) in the session; the client sends only the code.
- **Rationale:** there is no client token to sign or rotate, and the binding is free (SEC-004).
- **Constraint:** Shiro native sessions are per-JVM memory (P-4). This is the same affinity Cognito's pending authorization already needs.

### DD-3 — Correctness in the database
- **Decision:** single-row conditional updates with checked counts, a re-read inside the same transaction for the attempts value, the 3-attempt cap inside the consume condition (JS-1), and atomic upserts for buckets.
- **Rationale:** correct at InnoDB REPEATABLE-READ without explicit locks (P-15).

### DD-4 — Pre-registered only; one eligibility predicate
- **Decision:** `OtpEligibility` is reused at request, after consume and in `otpEnabled`. The variant used by `otpEnabled` omits active state and membership (JI-11).
- **Rationale:** parity with `finishLogin` (P-13).

### DD-5 — Decoys are real rows with an undeliverable code
- **Decision:** every request that passes steps 1–5 issues a row and supersedes, eligible or not. Only eligible issues are mailed. Eligibility is re-checked after consume.
- **Rationale:** supersede, attempts and expiry are then identical for every account state, enforced by the same atomic statements. This closes JD-1 and JD-2, which a session-side mirror could not, because supersede crosses sessions. It also equalizes the database work between the two paths.
- **Deviation from the approved proposal**, which described the decoy as "a random nonce in session with no row". Recorded in `requirements.md` §10.
- **Alternatives:** session-side mirroring (round-1 draft; broken by cross-session supersede); the PRMS always-`mismatch` decoy (an exhaustion oracle).

### DD-6 — Supersede reuses `consumed_at`
- **Decision:** a new issue sets `consumed_at` on the email's open rows.
- **Rationale:** one column and one refusal path (`invalid`).

### DD-7 — Dedicated asynchronous mailer; no `email_log`
- **Decision:** `OtpMailSender` as specified in §5.3.
- **Rationale:** `SendMailS` persists bodies, has no timeouts, and honors flags the code email must ignore (P-7). Async removes the latency oracle. The `delivery` line keeps failures observable (OPS-002).
- **Deviation from the approved proposal**, which said "synchronous send" (JI-2). Async is required by SEC-006; recorded in `requirements.md` §10.
- **Trade-off:** the code email does not appear in the admin email-tracking view.

### DD-8 — Database-backed rate limiting
- **Decision:** `otp_rate_limits` fixed-window upserts for the SEC-010 limits, plus `otp_cooldowns` as a sliding interval for FN-008. Both go through a `@Transactional` manager.
- **Rationale:** shared by every instance by construction. The cooldown is sliding because FN-008 states a minimum spacing, which a fixed window breaks across its edge (RJ-1).
- **Trade-off:** the fixed windows allow up to 2× a SEC-010 limit across a window edge. Accepted at these volumes, and recorded in `requirements.md` §10.

### DD-9 — Client IP through a trusted-proxy list with CIDR
- **Decision:** `ClientIpResolver` as specified in §5.3.
- **Rationale:** no IP code or valve exists (P-5). Setting the list is a go-live step, because an unset list behind a proxy would put every user in one IP bucket (P-6).

### DD-10 — Secret: base64, at least 32 decoded bytes, checked at use
- **Decision:** an invalid or short secret makes the feature unconfigured. Startup is unaffected (SEC-008, JI-3).

### DD-11 — Extract `sameOriginOrNull`
- **Decision:** move the method to `security/ReturnUrls`. Cognito delegates to it without changing behavior; OTP reuses it.
- **Reversion challenge — "what does moving this break?":** only Cognito's mint-time validation, which `CognitoLoginActionTest` pins. That suite must stay green and unchanged.

### DD-12 — Uncached allow-list read
- **Decision:** a native select of the active `custom_parameters.value`, falling back to `parameters.default_value`, for the key and Global Unit.
- **Rationale:** the Hibernate caches are per-JVM ehcache: the query cache has a 3600-s TTL, and the `Parameter` and `CustomParameter` entity caches 5000 s (P-10, RJ-6). Through them, the "empty the list" rollback could lag more than an hour on other instances.
- **Scope:** the other specificities keep the cached path; only this key bypasses it.

### Reversion challenge summary
No DD removes, disables or inverts shipped behavior. DD-11 only relocates code, and its challenge is recorded above.

---

## 16. Open Risks

| Risk | Mitigation / owner |
|---|---|
| `crpByEmail.do` discloses account existence (C8) | Separate spec (OQ-7) |
| Login CSRF parity (P-9) | Accepted; candidate follow-up spec |
| Affinity (P-4), proxy (P-6), environment binding (P-18) unconfirmed | Go-live checklist; T-rollout task |
| Victim lockout through per-email buckets | Accepted; other logins unaffected |
| Deliverability to partner domains | Pilot mailbox; partner IT allow-lists the sender |
| Shared files with `directory-retirement` | Sequence the work |

---

## 17. Premise Ledger

**Count:** 28 premises. 21 are verified and 7 are `UNVERIFIED` (3 High, 4 Low). Adjusted after the round-1 re-judgment: P-21 is downgraded and P-28 is added.

**Blast-radius triggers fired:**
- `live-path`: "sign in with a code" on the login page.
- `shared-state`: `APCustomRealm.supports`, the `finishLogin` tail and its inherited `user` field, the `login.js` mode decision, and the session store.
- `consumer`: the `crpByEmail.do` response shape, the struts action registrations, and `APConstants`.

| # | Claim | Class | Citation (as run) | Verified at | If false | Settled by |
|---|---|---|---|---|---|---|
| P-1 | The login reaches authentication through this chain: `login.js` → `crpByEmail.do` (`CrpByUserEmailAction.java:93`) → Global Unit card → mode branch at `login.js:471` → form POST (`loginForm.ftl:17`) → `LoginAction.login()` `:297` → `login(User, GlobalUnit)` `:401` → `finishLogin` `:417`. OTP inserts at `:471` | `live-path` | the cited lines (re-cited after JI-13) | `398d7b4058` | The UI entry point moves, and the §6 tasks change. **High** | — |
| P-2 | `finishLogin` is the shared tail. It holds gate 4 `existCrpUser` (`:421`), the session attributes and `custom_parameters` (`:423-433`), and the reload plus `saveLastLogin` (`:479-480`) | `location` | `LoginAction.java:417-481` | `398d7b4058` | The FN-011 parity design changes. **High** | — |
| P-3 | The realm's Cognito branch returns `SimpleAuthenticationInfo(userId, assertion, name)` with no I/O, and the credentials matcher is allow-all | `existence` | `APCustomRealm.java:117-118, 143-146, 75` | `398d7b4058` | The §5.4 design changes. **High** | — |
| P-4 | Production routes a browser's consecutive requests to the instance that holds its Shiro session. Shiro native sessions are per-JVM: `ShiroSpringStartupListener.java:51` installs a fresh `DefaultWebSessionManager` with no SessionDAO, and container memcached replication does not apply to Shiro native sessions. The Cognito pending authorization already depends on the same affinity, and Cognito is live | `data-env` | `UNVERIFIED — confirm at source before relying on it`. Code part: `ShiroSpringStartupListener.java:51`, `MarloShiroConfiguration.java:99-107`. Affinity part: secondary only (AD-246 says Cognito sign-in is live) | — | If false, verify lands on another instance with no pending challenge, so `OtpPending` must move to the database. **High** | Infrastructure check of load-balancer stickiness before the pilot. Owner: Kenji, T-rollout task |
| P-5 | No code reads the client IP, and no `RemoteIpValve` exists in the repo | `existence` | `grep -rn "getRemoteAddr\|X-Forwarded-For\|RemoteIpValve" marlo-web marlo-data Docker docs` → 0 code hits. `find . -name server.xml` → none | `398d7b4058` | DD-9 is unnecessary. **Low** | — |
| P-6 | A reverse proxy or load balancer fronts Tomcat in Production and test | `data-env` | `UNVERIFIED — confirm at source before relying on it`. Both judges searched and found nothing in the repo; `docs/infrastructure.md:316` (secondary) calls it "implied" | — | If one exists and is unconfigured, every user shares one IP bucket. **High** | Infrastructure check before the pilot. Owner: Kenji, T-rollout task |
| P-7 | `SendMailS` persists the body (`ThreadSendMail.java:72`, `SendMailS.java:394`) and sets no SMTP timeouts | `existence` | `grep -rn "mail.smtp.timeout\|connectiontimeout\|mail.smtp.writetimeout" marlo-*/src/main` → 0 | `398d7b4058` | DD-7 is unnecessary. **Low** | — |
| P-8 | The SMTP settings come from the `APConfig` getters used at `SendMailS.java:344-348` | `location` | `SendMailS.java:344-353` | `398d7b4058` | `OtpMailSender` needs other config. **Low** | — |
| P-9 | No Struts token interceptor is configured | `existence` | `grep -rnw "token\|tokenSession" marlo-web/src/main/resources/*.xml` → 0 | `398d7b4058` | The CSRF risk changes. **Low** | — |
| P-10 | `custom_parameters` are read through a cacheable query, and both entities use a read-write second-level cache. The caches are per-JVM ehcache: `timeToLiveSeconds="3600"` on `StandardQueryCache`, `5000` on the two entity caches | `data-env` | `CustomParameterMySQLDAO.java:86-90`; `CustomParameters.hbm.xml:7`; `MarloDatabaseConfiguration.java:109-110`; `marlo-web/src/main/resources/ehcache.xml:24-30` (query cache 3600 s), `:44-55` (`Parameter` and `CustomParameter` 5000 s) | `398d7b4058` | DD-12 is unnecessary. **Low** | — |
| P-11 | `custom_parameters.value` is `varchar(500)`, and `parameters.format = 4` is used for string settings | `data-env` | `SHOW CREATE TABLE custom_parameters`; `SELECT format, category, COUNT(*) FROM parameters GROUP BY …` (local `aiccradb1`) | `398d7b4058` | The allow-list needs other storage. **Low** | — |
| P-12 | The user lookup by email is case-insensitive and trimmed | `existence` | `UserMySQLDAO.java:94, 109, 118` | `398d7b4058` | `OtpEligibility` normalises itself. **Low** | — |
| P-13 | `existCrpUser` ignores `crp_users.is_active` | `existence` | `CrpUserMySQLDAO.java:67-74` | `398d7b4058` | The DD-4 parity statement changes. **Low** | — |
| P-14 | Pre-auth JSON actions already serve the login page (`crpByEmail`, `validateUser` in `homeJson`) | `other` | `struts-home.xml:7-22` | `398d7b4058` | DD-1 must be revisited under Hard rule 3. **High** | — |
| P-15 | MySQL 8.0 InnoDB runs at REPEATABLE-READ locally | `data-env` | `SELECT @@version, @@transaction_isolation` → `8.0.43`, `REPEATABLE-READ` | `398d7b4058` | DD-3 needs locking. **Low** | — |
| P-16 | A native `executeUpdate` with an affected-row count exists, and `EmailLog` is a plain hbm/DAO/manager trio | `existence` | `grep -rn executeUpdate marlo-data/src/main/java/org/cgiar/ccafs/marlo/data/dao/mysql` → 11; `RoleMySQLDAO.java:135-137, 167-171`; `EmailLog.java:10`; `EmailLogMySQLDAO.java:37-42`; `EmailLogManagerImpl.java:73, 104` | `398d7b4058` | The DAO pattern differs. **Low** | — |
| P-17 | The tests use JUnit 4.13.2 and hand-written fakes; Mockito is absent | `other` | `marlo-parent/pom.xml:17, 394-396`; `grep -rli mockito --include=pom.xml .` → 0; `CognitoCallbackActionTest.java:155-194` | `398d7b4058` | The test design changes. **Low** | — |
| P-18 | The environment variable `OTP_HMAC_SECRET` reaches `@Value("${otp.hmac.secret:}")` through Spring's system-environment property source | `data-env` | `UNVERIFIED — confirm at source before relying on it`. Both judges: plausible (`APConfig.java:188`, `CoreAppContextConfig:50`), not proven. `grep -rn System.getenv` → 0 | — | The secret cannot be delivered, so the feature cannot be enabled. **High** | First step of the `APConfig` task: start locally with the variable exported and assert the getter. Owner: that task |
| P-19 | `APCustomRealm.supports()` is shared by every login path. Its siblings are the `UsernamePasswordToken` branch (`:149`+) and the Cognito branch (`:143-146`), pinned by `APCustomRealmDispatchTest` | `shared-state` | `APCustomRealm.java:117-149`; `grep -rlE "APCustomRealm" marlo-web/src marlo-data/src` → 28 files | `398d7b4058` | The realm change is redesigned. **High** | — |
| P-20 | The readers of the `crpByEmail.do` response or its flag are: `login.js`, `crp/js/admin/crpUsers.js`, `loginForm.ftl`, `CrpByUserEmailAction`, `ValidateUserAction`, `CognitoLoginAction`, `CognitoAuthSpecificity`, `ParameterMySQLDAO`, and the tests `CrpByUserEmailActionTest`, `CognitoLoginActionTest`, `LoginActionCgiarGuardTest`, `ValidateUserActionGuardTest` and `ErrorStatusLogStackCoverageTest` | `consumer` | `grep -rlE "crpByEmail\|cognitoEnabled\|CrpByUserEmailAction" marlo-web/src marlo-data/src` → 14 files | `398d7b4058` | A consumer breaks on the added key. **Low** (additive) | — |
| P-21 | The struts stack coverage tests enumerate action registrations and may pin the action list | `consumer` | `UNVERIFIED — confirm at source before relying on it`. The P-19/P-20 sweeps named `ErrorStatusLogStackCoverageTest` and `CognitoUnloggedStackReachabilityTest`, but their contents have not been read (RJ-8) | — | Those tests go red when the actions are added. **Low** | First step of the struts task: read both tests and list the pinned registrations in that task's Consumers field. Owner: that task |
| P-22 | `sameOriginOrNull` is private to `CognitoLoginAction` | `existence` | `CognitoLoginAction.java:403` | `398d7b4058` | DD-11 is unnecessary. **Low** | — |
| P-23 | Global Unit type 4 exists on every database the migration runs on. `V2_6_0_20260409_1645…sql:34-36` inserts id 4 only `WHERE NOT EXISTS (… id = 4 OR LOWER(name) = 'center')`. `V2_5_0_20171027_1500__GlobalUnitTypes.sql:26` already seeds id 2 as `Center`, so on a migration-only database id 4 may be skipped | `data-env` | `UNVERIFIED — confirm at source before relying on it` (contradicting evidence cited) | — | A fixed-id seed would fail its FK. The design already seeds by selecting existing types (§3), so the impact is **Low** | Migration task: apply on a clean throwaway database built from migrations. Owner: that task |
| P-24 | `login.js` has no timer code | `existence` | `grep -nE "setInterval\|setTimeout\|countdown" marlo-web/src/main/webapp/global/js/login/login.js` → 0 | `398d7b4058` | Reuse an existing timer. **Low** | — |
| P-25 | Production RDS is MySQL 8.0 InnoDB | `data-env` | `UNVERIFIED — confirm at source before relying on it` | — | DD-3 is re-checked. **Low** | Infrastructure confirmation. Owner: Kenji, T-rollout |
| P-26 | Struts 6 accepts a comma-separated list of parent packages (`extends="marlo-default,json-default"`), and `json-default` comes from the struts2-json plugin on the classpath | `other` | `UNVERIFIED — confirm at source before relying on it`. No multi-parent package exists in the repo (`grep -rn 'extends="[^"]*,' marlo-web/src/main/resources/struts*.xml` → 0) | — | Use the DD-1 fallback (declare the `json` result type in `home`). **Low** | First step of the struts task: a reachability test like `CognitoUnloggedStackReachabilityTest`. Owner: that task |
| P-27 | `finishLogin` dereferences the inherited `user` field on its refusal paths, and Cognito sets a detached user first | `shared-state` | `LoginAction.java:459-469` (`user.setPassword(null)`); `CognitoCallbackAction.java:489-492` (`setUser(detachedUser)`). Readers of `user` on this path: these refusal branches only | `398d7b4058` | Without `setUser`, gate-4 refusals NPE. **High** | — |
| P-28 | struts2-core 6.8.0 provides `org.apache.struts2.action.ParameterNameAware`, which lets an action refuse request parameter names | `existence` | `unzip -l ~/.m2/repository/org/apache/struts/struts2-core/6.8.0/struts2-core-6.8.0.jar \| grep ParameterNameAware` → `org/apache/struts2/action/ParameterNameAware.class` (plus the deprecated `com.opensymphony` alias) | `398d7b4058` | RJ-9 needs an interceptor-level `excludeParams` instead. **Low** | — |

---

## 18. Budget (tripwire for `/akili-execute`)

| Measure | Expected |
|---|---|
| Tasks | 16. The concurrency probe and the uncached read add work over the round-1 draft, and Phase 3 split the runbook and rollout out of the verification task |
| LOC (production + tests + FTL/JS/properties/SQL) | ~2,300 (≈1,300 production, ≈1,000 tests) |
| Review rounds | 23 (one per task, plus 7 of margin for the realm, the two actions, the managers and `login.js`) |

Depth check: Full holds.
