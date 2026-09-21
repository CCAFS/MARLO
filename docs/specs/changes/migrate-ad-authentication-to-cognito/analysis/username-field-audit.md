# `users.username` — can MARLO work without it?

**Analysis ID:** `CHG-COGNITO-USERNAME-AUDIT-001`
**Revision:** 3 — §1.1 corrected and §11 added 2026-09-09; runtime verification in §10
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

> **Corrected 2026-09-09.** Revisions 1 and 2 said the field is left "with **no writer at all**" by
> discarding derivation, and therefore freezes. **That was wrong about the timing**, and the error mattered
> because it implied an urgency that does not exist. Cognito is a per-Global-Unit specificity, so while any
> unit still has it off, AD keeps writing the column — see §11. The paragraph below is restated against the
> milestone that actually causes the freeze.

`users.username` has four writers today `[V]` — `APCustomRealm.getCgiarNickname:333-338` plus the three
admin-creation sites (§4) — and all four read `DirectoryPerson.getLogin()`, so all four disappear **with
the library**, not with the Cognito decision. `CognitoIdentityMapperImpl:88-103` already decided to write
nothing.

The freeze therefore begins at **Gate 1** (functional AD retirement: zero runtime `adauth` calls), not
before. Until then, every CGIAR user signing in through a Global Unit whose `cognito_auth_active` is off
takes the local form → `APCustomRealm`'s AD branch → `getCgiarNickname`, and the column is written and
repaired exactly as it always was `[V]`.

From Gate 1 onward the field keeps what it holds and receives nothing new, so the share of nulls grows as
accounts are created. That makes the degradation **gradual and non-retroactive** `[I]` — no existing user
loses anything on the day it happens — and it makes the decision **reversible**: if `OQ-18` lands, a
replacement for `getCgiarNickname` repairs the field on each sign-in exactly as today, and the null
population self-heals for anyone who logs in.

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

## 5. Real impacts

> Revision 1 titled this section *"three, and only three"*. A fourth was found during the runtime verification of §10 and is recorded as §5.4. The original three are unchanged.

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

### 5.4 `DirectoryPerson.getLogin()` is dereferenced unguarded by three reachable writers

Found 2026-09-09 while tracing `SearchUserAction` (§10). Not a null-`users.username` consequence — the
opposite: it is how the column gets *written*, and it can fail before it ever writes.

```java
newUser.setUsername(person.getLogin().toLowerCase());
```

`CrpUsersAction:641`, `ManageUsersAction:159` (global) and `ManageUsersAction:261` (center) all do this,
guarded only by `person.isFound()` `[V]`. But `DirectoryPerson.found(email, login, firstName, lastName,
source)` applies `Objects.requireNonNull` to **`source` alone** `[V]`, and `LdapDirectoryService:73` passes
`user.getLogin()` straight through from `adauth` without checking it `[V]`. So a found person with a null
login NPEs at all three sites.

**The real defect is the undeclared contract.** `DirectoryService`'s type-level Javadoc is meticulous about
its invariants — never throws, never returns null, `source` never null, five enumerated outcomes — and says
**nothing** about whether `login`, `firstName` or `lastName` can be null on a found result `[V]`. Three
consumers assume they cannot. A future provider behind the same seam (`COGNITO_CLAIMS`, `AD_MIRROR`,
CLARISA) has no way to know that assumption exists.

Whether `adauth` can actually return a found `LDAPUser` with a null login is **`[OQ]`** — it is a
third-party JAR and cannot be determined from this repository.

Two ways to close it, and the choice is a design decision rather than a fix:

| | Approach | Trade-off |
|---|---|---|
| **(a)** | Guard the three call sites | Local and contained, but leaves the contract as ambiguous as it is now for the next provider |
| **(b)** | Make `found()` require a non-null `login`, and state it in the `DirectoryService` Javadoc | Fixes the cause, and the constructor then protects every future provider. **More invasive:** if `adauth` can return a null login, `LdapDirectoryService` must decide what that case becomes — most likely `NOT_FOUND` — which is a small but real behaviour change |

`directory-abstraction` owns this code and is archived, so either way it needs its own task.

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
| ~~How many existing users have a null `username`~~ | **ANSWERED 2026-09-09 — see §10.2** | — |
| **Whether CLARISA requires `externalUserName`** | A remote API contract, not in this repository | CLARISA team — `OQ-14` |
| **Whether the QA service rejects the literal `null`** | Same | QA service owners — `OQ-14` |
| **Whether `preferred_username` can be a federation mapping target** | Pool alias configuration; already flagged unverified in claims-inventory §7 | Pool owner — `OQ-18` |

None of these blocks the conclusion: **MARLO can work without `users.username`**.

---

## 9. Verification status

Revision 1 stated that no code had been changed. That is no longer true, and this is what shipped on
`staging-cognito-impl`:

| Commit | What |
|---|---|
| `54a0424dbb` | §5.1 — `FeedbackQACommentsAction:180,403` now read `getFirstName()` |
| `1dc36af0b2` | Three further defects of the same class found while sweeping that file: the old-model block's guard validated `reply` while the body read `comment` (an NPE plus the wrong person under `userName_reply`), and `prepare()`'s three `Long.parseLong` calls threw `NumberFormatException` out of an unhandled method |
| `587b0eb6fb` | §5.2 — `QAReportsAction` normalizes the absent values so the QA service stops receiving the text `null` |

**Still not implemented:** §5.2's CLARISA fallback (gated on `OQ-14`) and §5.4 (needs its own task under
a future spec, since `directory-abstraction` is archived).

Line numbers were read against `staging-cognito-impl` and will drift — re-locate before editing, per the
execution plan's session-start step.

---

## 10. Runtime verification — 2026-09-09

Revision 1 was static analysis only. Two of its open items were closed by running the application
locally (`scripts/run-marlo-java17.sh`) and querying the development database `aiccradb1`.

### 10.1 `SearchUserAction` is not reachable — it is dead code

Revision 1 listed `SearchUserAction:91` and `:203` as live consumers. **They are not.** The action has no
mapping in any Struts XML, no convention annotations, and no JavaScript caller — what appeared to call it
(`crpUsers.js:210`) targets `crpByEmail.do`, a different action with a different payload shape `[V]`.

The Struts convention plugin *is* on the classpath and its default locators (`action,actions,struts,struts2`)
would cover the package, which is why revision 1 could not rule it out. Measured, it does not map it `[V]`:

| Probe | Body |
|---|---|
| `/crpByEmail.do` — a real XML-mapped action, used as the positive control | **23 bytes** of JSON |
| 8 candidates for `SearchUserAction`, across 4 namespaces × 3 name forms | 17,500–17,549 bytes |

**A method note worth carrying.** The first attempt read HTTP status codes and concluded the opposite:
MARLO answers **200** for any unmatched `.do` path, serving a ~17.5 KB page templated with whatever name
was requested — `/totally-bogus-path-xyz.do` included. Status codes prove nothing here; a positive control
is mandatory. Two further confirmations: `/json/global/search-user.do` is byte-identical to
`/json/global/zzz-nonexistent.do` apart from the reflected name, and the application log records neither
the action, nor an NPE from its parameter-less `prepare()`, nor the lookup of the probe email.

**Consequence:** two of the six consumers in §3 are moot, and `OQ-12` is answered for this action. The
`getLogin()` gap it exposed is real and lives in the other three sites — §5.4.

### 10.2 The measured population

`aiccradb1`, 3,599 accounts. **`is_cgiar_user` is not usable as the discriminator in this copy** — only one
row carries `1`, which is an artifact of the development dataset. Per the product owner, **in production
every `@cgiar.org` address is `is_cgiar_user = 1`**, so the email domain is the correct proxy:

| Domain | Accounts | No username | Has username |
|---|---|---|---|
| `@cgiar.org` | 1,673 | **1** | 1,672 |
| Other | 1,926 | 181 | 1,745 |

No username in the table is an email address (0 rows match `%@%`).

**What this establishes for §1.1 and §6.** The CGIAR population is 99.94% populated, and those values came
from Active Directory — precisely what retirement removes. The 181 absent usernames are almost entirely
**non-CGIAR** accounts, which never had one because AD never supplied it. So the empty state is not
introduced by this decision: **it already works today, in 181 accounts, having broken nothing.** Growth
comes only from CGIAR accounts created after retirement; no existing account loses anything.

This also sharpens consequence 6.2: 3,417 of 3,599 accounts carry a username today, so username sign-in is
the majority entry path and stays available to every one of them.

### 10.3 An unrelated observation, by design

`/crpByEmail.do` answers **unauthenticated** and distinguishes an existing account from a missing one,
returning the display name, `isCgiarUser`, `agree`, and the Global Unit list including `cognitoEnabled`
`[V]`. This is deliberate — it is step 1 of the login wizard (`login.js:711`), which must know before
authentication whether to show the local password field or the Cognito redirect. Recorded as a known
property, not a defect: it does permit account enumeration, and whether that matters is a product call.

---

## 11. The specificity asymmetry — authentication is gradual, the directory is not

Recorded 2026-09-09 at the product owner's direction: **Cognito is a specificity, so while it is off for a
Global Unit that unit must keep using the current flow.** Verified against the working tree, and it has a
consequence the earlier analysis did not state.

### 11.1 Only authentication is gated

Five call sites resolve `cognito_auth_active`, and **all five are on the login path** `[V]`:

| Site | Role |
|---|---|
| `LoginAction:229,236` | blocks CGIAR credential relay when the flag is on |
| `CognitoLoginAction:519` | authoritative pre-filter before the authorize redirect |
| `CognitoCallbackAction` | authoritative gate after the redirect (via the shared resolver) |
| `ValidateUserAction:249,256` | login wizard |
| `CrpByUserEmailAction:126` | login wizard rendering hint |

**No consumer of the directory reads it** — not `ManageUsersAction`, not `CrpUsersAction`, not
`GuestUsersValidator`, not `APCustomRealm.getCgiarNickname` `[V]`. `DirectoryService.findByEmail(email)`
does not even take a Global Unit to decide on.

### 11.2 What that means for the two capabilities

| | Capability A — authentication | Capability B — directory lookup |
|---|---|---|
| Gated by the specificity? | **Yes**, per Global Unit | **No** |
| Rollout shape | Gradual, unit by unit | **All-or-nothing across every unit at once** |
| While the flag is off somewhere | That unit authenticates through `LDAPAuthenticator`, and `getCgiarNickname` still writes `users.username` | Unchanged — every unit shares one directory source |

So the user-creation flow **must keep using AD for as long as AD exists**, exactly as the product owner
requires — and it does so without any extra work, because it has no per-unit gate to get wrong.

### 11.3 Consequences to carry

1. **`adauth` cannot reach functional retirement until every Global Unit has the flag on.** Authentication
   is gradual by design, so Gate 1 is gated on the *slowest* unit, not on the first.
2. **Replacing the directory source is a single cut for the whole platform.** If it were ever required to
   follow the specificity, a gate would have to be added — and there is nowhere obvious to put it:
   `CrpUsersAction` has `selectedGlobalUnitAcronym`, but `ManageUsersAction.create()` is a global JSON
   action with no unit context, and the `DirectoryService` signature carries none `[V]`.
3. **A person can belong to one unit with the flag on and another with it off.** `LoginAction:229-237`
   already handles that — the selected unit decides, otherwise a fail-closed sweep across memberships — so
   during rollout such a user still has their username written whenever they sign in through the AD path
   `[V]`.
4. **This is why `OQ-21` is the binding question.** Migrating authentication is already solved and is
   gradual; supplying the directory after AD is gone is one decision that lands on every unit
   simultaneously.
