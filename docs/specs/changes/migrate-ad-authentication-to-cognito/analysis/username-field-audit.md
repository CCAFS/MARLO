# `users.username` — can MARLO work without it?

**Analysis ID:** `CHG-COGNITO-USERNAME-AUDIT-001`
**Revision:** 1
**Scope:** every reader and writer of the `users.username` column, across Java, FreeMarker, JavaScript, the ORM mapping, SQL, Pentaho reports and i18n properties
**Method:** static analysis of the working tree on branch `staging-cognito-impl`, 2026-09-09. No database access — see §8
**Companions:** [`cognito-claims-inventory.md`](./cognito-claims-inventory.md) · [`adauth-retirement-analysis.md`](./adauth-retirement-analysis.md)
**Related:** [`../family.md`](../family.md) — `OQ-14`, `OQ-18`

> **The question this document answers:** the AD login is the one field Cognito cannot supply
> ([`cognito-claims-inventory.md`](./cognito-claims-inventory.md) §3, measured twice). If MARLO stops
> writing `users.username` altogether, **what breaks?** And is the answer small enough that the
> retirement does not have to wait on `OQ-18`?

**The answer: yes.** Two lines of code produce visibly wrong output; two outbound integrations send a
degraded identity string. Nothing else in the checkout depends on the field. No migration is needed.

---

## 1. The decision this document records

| Field | Value |
|---|---|
| Date | **2026-09-09** |
| Decided by | Product owner |
| Decision | **Accept a null `users.username`.** Sign-in by email only for accounts created after retirement |
| Rejected | **Deriving or synthesizing a username** (from the email local-part, from `cognito:username`, or typed by an admin) |
| Deferred, not blocking | `OQ-18` — mapping `sAMAccountName` at federation time. If it lands later it *repairs* the field; it is an improvement, not a prerequisite |

**Why derivation was rejected.** `k.tanaka@cgiar.org` yields `k.tanaka`, and the AD login is
`ktanaka` — measured on two accounts (§3 of the claims inventory). A derived value is not the login;
it is a value correct for nothing, which is precisely the write `T17` already reverted for
`cognito:username` on 2026-09-04. An admin typing it by hand has the same defect with a human in the
loop instead of a formula.

### 1.1 The consequence that makes the decision safe

Discarding derivation leaves `users.username` with **no writer at all**. It has four today `[V]` —
`APCustomRealm.getCgiarNickname:333-338` plus the three admin-creation sites (§4) — and all four read
`DirectoryPerson.getLogin()`, so all four disappear with the library.
`CognitoIdentityMapperImpl:88-103` already decided to write nothing.

The field therefore becomes **frozen**: it keeps what it holds, receives nothing new, and the share of
nulls grows as accounts are created. This makes the degradation **gradual and non-retroactive** `[I]` —
no existing user loses anything on retirement day — and it makes the decision **reversible**: if
`OQ-18` lands, a replacement for `getCgiarNickname` repairs the field on each sign-in exactly as today,
and the null population self-heals for anyone who logs in.

---

## 2. The field is nullable at every layer

| Layer | Evidence |
|---|---|
| Schema | `V1_0_0_20160818_0910__UsersUpdate.sql:27` — `username varchar(255) DEFAULT NULL`; `:40` — `UNIQUE KEY username_UNIQUE` `[V]` |
| ORM | `Users.hbm.xml:18-19` — `<column name="username" unique="true" />`, **no `not-null="true"`** `[V]` |
| Uniqueness | MySQL permits **many** NULLs in a UNIQUE index, so a null-by-default population does not collide `[V-MySQL]` |

**No migration is required.** This is the finding that makes the rest of the audit cheap.

---

## 3. Every consumer, with a verdict

40 `.prpt` Pentaho reports, all `.hbm.xml` mappings, every migration and stored procedure, and all
project `.js` / `.ftl` were swept. The complete result:

### 3.1 Dies with the library — no action

| Site | Why |
|---|---|
| `APCustomRealm:172` — `ldapAuthenticator.authenticate(user.getUsername(), password)` | **The only functionally load-bearing read in the checkout.** It is the LDAP bind identity. No bind, no need for `sAMAccountName` `[V]` |
| `APCustomRealm.getCgiarNickname:319-346` | The writer itself `[V]` |

### 3.2 Already null-safe — no action

| Site | Evidence |
|---|---|
| `tawkto-widget.ftl:38` | `${(currentUser.username)!"No User name"}` — FreeMarker default operator already present `[V]` |
| `marloUsers.js:33-38` | `return data \|\| '<i>No Username<i>'` — the superadmin table **already renders "No Username"** `[V]` |
| `crpUsers.ftl:224` | Commented out `[V]` |
| `GlobalUnitCreationManagerImpl:511` | Last-resort fallback only (the logged-in creator's id is tried first); its SQL filters `username is not null` (`UserMySQLDAO:152,156`) and a null return is handled `[V]`. `V2_6_0_20260409_1645__SeedMinimumDataForFirstGlobalUnitCreation.sql:5` seeds `username = 'superadmin'` independently of AD, so fresh installs keep the fallback `[V]` |

> **`marloUsers.js` is the strongest single piece of evidence in this audit.** A null `username`
> already has a rendered representation in MARLO's own admin UI. The state this decision introduces is
> one the application was already written to expect — not a new one.

### 3.3 Every username query fails closed

| Query | Behaviour with a null column |
|---|---|
| `UserMySQLDAO:61` — `where username = :username` | A null or blank parameter never matches, by SQL null semantics. Cannot resolve an account `[V]` |
| `GlobalUnitMySQLDAO:44-45` — `where (cpUser.user.email = :email or cpUser.user.username = :email)` | The OR branch never matches; Global Unit resolution proceeds by email `[V]` |
| `UserMySQLDAO:151-164` — super-admin heuristic | Explicitly guards `username is not null` `[V]` |

**No null-username account can be reached by submitting a null or empty value.** The field's absence
opens no authentication hole `[I]`.

### 3.4 Not this field — false positives to discard

Each of these matches a `username` grep and has nothing to do with `users.username`:

| Site | What it actually is |
|---|---|
| `AiAction:99` | `username = firstName + " " + lastName`. A different value with a confusing name `[V]` |
| `login.js:5` | `var username = $("input[name='user.email']")` — the **input element**, not the column. `.login-echoed-username` echoes what the visitor typed `[V]` |
| `feedbackAutoImplementation.js:348,366,433,487` | `attr('username')` on `.commentContainer`, but neither `forms.ftl:1207` nor `:1557` emits that attribute — it already returns `undefined`. **Pre-existing defect, unrelated to this decision** `[V]` |
| `QATokenItem`, `NewQATokenAuthDTO`, `QATokenAuthDTO` | REST API credentials `[V]` |
| `MarloDatabaseConfiguration:81` | Database pool credentials `[V]` |
| `PartnersSaveAction:537` | `epu.setUsername(config.getClarisaAPIUsername())` — the CLARISA API credential, not the person `[V]` |

---

## 4. The writers that disappear

All four read `DirectoryPerson.getLogin()` and lowercase it:

| Site | Context |
|---|---|
| `APCustomRealm.getCgiarNickname:333-338` | Login path. Persists via `userManager.saveUser` when the field was null `[V]` |
| `json/global/ManageUsersAction:159` | Admin creates a user `[V]` |
| `crp/admin/CrpUsersAction:641` | Admin grants Global Unit access `[V]` |
| `center/json/global/ManageUsersAction:261` | Center admin creates a user `[V]` |

`CognitoIdentityMapperImpl:88-103` is a deliberate non-writer, with the reasoning recorded inline `[V]`.

---

## 5. Real impacts — three, and only three

### 5.1 `FeedbackQACommentsAction:180` and `:403` — a latent bug becomes a visible `null`

```java
replyMap.put("approvalUserName",
  (reply.getUserApproval() != null && reply.getUserApproval().getFirstName() != null
    && reply.getUserApproval().getLastName() != null)
      ? reply.getUserApproval().getUsername() + " " + reply.getUserApproval().getLastName() : "");
```

The guard proves `getFirstName()` non-null and the expression then reads `getUsername()` `[V]`. Java
string concatenation renders a null reference as the literal `"null"`, so the QA comments UI shows
**`"null Tanaka"`**. Today it shows `cgamboa Gamboa` — already wrong, merely plausible-looking. This is
the same trap recorded as claims-inventory §4.1.

**The fix is `getFirstName()`** — the field the guard already validated, and evidently the intent. It is
correct independently of this decision and should not wait for it. **Two lines.**

### 5.2 Two outbound integrations send a degraded identity

| Integration | Behaviour with a null username |
|---|---|
| `QAReportsAction:59` → `qa.cgiar.org` | Sends the **4-character literal `null`** — see §5.3. Token stays valid, page renders `[V]` |
| `PartnersSaveAction:553` → CLARISA `POST /api/partner-requests/create` | `org.json.JSONObject.put` with a null value **removes the key**, so `externalUserName` is absent from the payload `[V]` |

Both payloads carry `email` and a user id alongside, so the person remains identifiable in both systems
`[V]`. Whether either service *requires* the field is **`OQ-14`** — see §7.

Optional hardening, if `OQ-14` comes back demanding a value: pass the **email** for QA (already the
guaranteed-non-null identity key per `OQ-9`, and `qa_token_auth` carries `name` in its own column), and
**`firstName + " " + lastName`** for CLARISA (the field is named `externalUserName` and
`externalUserMail` already travels separately). Neither is a synthesized *username* — they are existing
values placed in a field whose contract is a display identity `[I]`.

### 5.3 A trap: the QA token does **not** propagate NULL

The obvious reading is that `MD5(CONCAT(appuser, username, NOW()))`
(`V2_6_0_20200506_1757__CreateFunction_getQAToken.sql:29`) yields NULL for a null username — MySQL
`CONCAT` returns NULL if any argument is NULL — which would make `qualityAssessment.ftl:28` render
`${qATokenAuth.token}` on a null and fail the page.

**It does not happen.** `QATokenAuthMySQLDAO:77` builds the call by string concatenation into a SQL
literal `[V]`:

```java
"SELECT getQAToken('" + name + "','" + username + "','" + email + "','" + smoCode + "','" + userId + "')"
```

Java stringifies the null *before* SQL sees it, so the function receives the text `'null'`, not SQL
NULL. `CONCAT` gets a valid string, `MD5` returns a valid token, and the iframe renders. The cost is
data quality — `qa_token_auth.username` and the identity sent to `qa.cgiar.org` become the word
`null` — **not availability.**

> Recorded because the wrong reading is the intuitive one, and it changes the severity from *page
> outage* to *cosmetic*.

**Adjacent finding, out of scope.** That same line interpolates `name` (which is
`firstName + " " + lastName`) into a SQL string literal with no escaping `[V]`. A surname containing an
apostrophe — O'Brien — breaks the query today, independently of this decision. It deserves its own
ticket.

---

## 6. Accepted consequences

| # | Consequence | Status |
|---|---|---|
| 1 | Accounts created after retirement can sign in **by email only**. Six sites resolve a submitted value as a username when it carries no `@` — `APCustomRealm:162`, `LoginAction:215`, `CognitoLoginAction:294`, `ValidateUserAction:231`, `CrpByUserEmailAction:98`, `UserManagerImp:145` — and `login.js:132-134` deliberately skips the email-format check so the server can resolve either `[V]` | **Accepted 2026-09-09.** Existing accounts unaffected |
| 2 | **The login UI advertises the feature.** `global.properties:1576,1608,1610,1642,1652` plus `custom/{alliance,aiccra3,aicrra,pabra,test}.properties` all read *"Email or username"* `[V]`. It stays true for existing users and becomes a partial promise as the null share grows | **Recorded, no change now.** Revisit if the null share becomes the majority |
| 3 | No admin UI can set the field. **No `.ftl` in the checkout renders an input for `username`** `[V]`, so there is no path for a human to populate it even if someone wanted to | **Consistent with the decision** |

---

## 7. What this changes in the manifest

**`OQ-14`'s premise is now wrong.** It reads *"Do CLARISA `partner-requests` and the QA service accept a
**synthesized** username?"* Synthesis is rejected (§1), so the question becomes:

> **Do CLARISA `partner-requests` and the QA service accept an absent or null username?** — with the
> per-service behaviour of §5.2 as the concrete input, and §5.2's fallbacks as the answer if either
> service says no.

**`OQ-18` is downgraded from prerequisite to improvement.** §1.1 shows the decision is reversible, so
mapping `sAMAccountName` no longer gates retirement — it only repairs the field afterwards.

Neither edit is applied here. `family.md` is the manifest, and its prior structural edits are marked
*HITL-approved*; these are proposed for the owner to apply.

---

## 8. What this document does not establish

| Unknown | Why it is not answerable here | Who answers |
|---|---|---|
| **How many existing users have a null `username`** | No database access in this session — the local MySQL rejected `root` and no `marlo-*.properties` exists on the machine. This sizes consequence 6.2, it does not change any verdict above | One SQL query. Related to `OQ-1` |
| **Whether CLARISA requires `externalUserName`** | A remote API contract, not in this repository | CLARISA team — `OQ-14` |
| **Whether the QA service rejects the literal `null`** | Same | QA service owners — `OQ-14` |
| **Whether `preferred_username` can be a federation mapping target** | Pool alias configuration; already flagged unverified in claims-inventory §7 | Pool owner — `OQ-18` |

None of the four blocks the conclusion: **MARLO can work without `users.username`**, at a cost of two
obligatory lines and two optional fallbacks.

---

## 9. Verification status

**No code has been changed.** This document is the analysis; §5.1 and §5.2 describe work that is
proposed, not implemented. Line numbers were read against `staging-cognito-impl` on **2026-09-09** and
will drift — re-locate before editing, per the execution plan's session-start step.
