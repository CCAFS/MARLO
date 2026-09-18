# Cognito claims inventory — what the pool actually tells MARLO about a user

**Measured, not inferred.** Every value below came from a real corporate sign-in against the live pool.
Where something is unverified, this document says so rather than filling the gap with a plausible answer.

| Field | Value |
|---|---|
| Pool | `us-east-1_o9y9Yq5pO` (`us-east-1`), domain `ost-toc.auth.us-east-1.amazoncognito.com` |
| Identity provider | `CGIAR-AzureAD` — **SAML**, issuer `https://sts.windows.net/6afa0e00-fa14-40b7-8a2e-22a7f8c357d5/` |
| Measurements | **2026-09-04** (U-3, `c.gamboa`, scope `openid email`) · **2026-09-08** (this document, `k.tanaka`, scope `openid email profile`) |
| Related | [`README.md`](./README.md) · `archive/2026-09-07-…auth-flow/execution.md` §32 · Jira **A2-2459** |

> **Why real values are recorded here.** Both measurements are the authors' own accounts, and the 2026-09-04
> values are already recorded verbatim in `execution.md` §32. An inventory that paraphrased its evidence
> would be exactly the kind of document this spec family has had to correct twice.

---

## 1. The headline, in one table

The login wizard needs four things from a directory lookup — the contract `DirectoryPerson` declares. Here is
what Cognito supplies, by scope:

| `DirectoryPerson` field | `openid email` | `openid email profile` |
|---|---|---|
| `getEmail()` | ✅ `email` | ✅ `email` |
| `getFirstName()` | ❌ absent | ✅ **`given_name`** |
| `getLastName()` | ❌ absent | ✅ **`family_name`** |
| `getLogin()` | ❌ absent | ❌ **absent** |

**Two of the four gaps close by changing one string.** The third does not close from MARLO at all.

> **U-3's conclusion was right for its scope and too broad as a statement.** It measured `openid email` and
> reported that the token carries no name. True — but the absence was a consequence of the scope MARLO
> requests, not of what the pool can emit. Recorded here so the correction is not lost.

---

## 2. The full inventory

**19 claims** with `openid email profile`; 16 with `openid email` (the three marked *profile only* are the
difference). Only 9 say anything about the person.

### 2.1 User data

| Claim | Value observed (`k.tanaka`) | What it actually is |
|---|---|---|
| `email` | `K.Tanaka@CGIAR.ORG` | The address. **Mixed case, uppercase domain** — see §4.2 |
| `given_name` | `Kenji` | First name. *profile only.* Clean and usable |
| `family_name` | `Tanaka` | Last name. *profile only.* Clean and usable |
| `name` | `K.TANAKA@cgiar.org` | **The UPN, not a display name.** *profile only.* See §4.1 |
| `sub` | `027e8c63-6158-4c01-ab69-8f05c2bff04a` | The pool's stable identifier for this identity. See §6 |
| `cognito:username` | `cgiar-azuread_k.tanaka@cgiar.org` | Provider name + UPN. **Not the AD login.** See §4.1 |
| `cognito:groups` | `["us-east-1_o9y9Yq5pO_CGIAR-AzureAD"]` | The federation group. **Not application roles** |
| `email_verified` | `false` | See §4.3 |
| `identities` | `dateCreated: 1764599346614`, `userId: K.TANAKA@cgiar.org`, `providerName: CGIAR-AzureAD`, `providerType: SAML`, `issuer: https://sts.windows.net/6afa0e00-…/`, `primary: true` | The Azure AD link |

### 2.2 Protocol claims — no user data

`aud` · `iss` · `exp` · `iat` · `auth_time` · `jti` · `origin_jti` · `nonce` · `at_hash` · `token_use`

---

## 3. The AD login is not there, and cannot be put there from MARLO

The account measured on 2026-09-08 has `users.username = ktanaka`. Every login-shaped claim in its token:

```
cognito:username    cgiar-azuread_k.tanaka@cgiar.org    ✗
name                K.TANAKA@cgiar.org                  ✗
identities.userId   K.TANAKA@cgiar.org                  ✗
preferred_username  <ABSENT>
username            <ABSENT>
```

All three present values are the same UPN, with or without the provider prefix. **`ktanaka` appears nowhere.**

This is the second account measured with an identical result — `c.gamboa` → `cgamboa` on 2026-09-04. The
pattern is the provider's, not one account's misconfiguration.

**Why, precisely.** The pool's attribute mapping reads *"User pool attribute: **username** ←
.../identity/claims/username"* in the AWS console, which looks as though it produces a `username` claim. It
does not: it feeds the pool's **own** username attribute, which Cognito prefixes with the provider name and
emits as `cognito:username`. And the Azure AD claim being mapped returns the **UPN**, not the
`sAMAccountName`.

**Stripping the prefix does not work and must stay rejected.** `cgiar-azuread_k.tanaka@cgiar.org` minus the
prefix is `k.tanaka@cgiar.org` — an email, not `ktanaka`. The prefix hides that the suffix is the wrong value
too, so the change would look fixed and stay broken.

**The only fix is outside MARLO** — see §7.

---

## 4. Three traps

### 4.1 `name` and `cognito:username` are named like what they are not

`name` holds an email. `cognito:username` holds a provider-prefixed email. Code that treats either as a
person's name or login writes a wrong value that looks plausible. This has already happened once:
`FeedbackQACommentsAction:180`, `:403` render `getUsername()` as a display name, and writing
`cognito:username` into `users.username` produced `cgiar-azuread_c.gamboa@cgiar.org Gamboa` in the UI. That
write was removed by T17; the trap remains for the next reader.

### 4.2 Email casing — the normalization is load-bearing

The pool asserted `K.Tanaka@CGIAR.ORG`. The database holds `k.tanaka@cgiar.org`. The sign-in succeeds only
because `UserMySQLDAO.normalizeEmail` trims and lowercases the **input** before comparing against
`LOWER(u.email)` (T07, OQ-9).

Remove that normalization and every CGIAR sign-in fails as *account not found*. There is now token evidence
for what was previously a defensive measure.

### 4.3 `email_verified` is `false`

For a federated corporate identity. MARLO does not check it, which is why sign-in works. **Adding a check on
it as a hardening measure would break every CGIAR login.** Recorded so nobody adds it in good faith.

---

## 5. `/oauth2/userInfo` adds nothing

Called with the access token from the same exchange: **8 fields, all a subset of the ID token.**

```
email, email_verified, family_name, given_name, identities, name, sub, username
```

The only difference is cosmetic — the key is `username` instead of `cognito:username`, and the value is the
same `cgiar-azuread_k.tanaka@cgiar.org`. There is no second source of user data here, and no reason for MARLO
to call this endpoint.

---

## 6. `sub` — an observation, not a recommendation

`sub` is a stable UUID that does not change when the person's email changes. OQ-9 chose the normalized email
as the join key to `users`, and R-D2 was retired with a surviving operational obligation: *an email change
re-links nothing automatically and must be an administrative edit to `users.email`.*

`sub` would not have that obligation. Changing the join key now would mean a new column, a backfill, and
re-opening a closed decision — this document does not propose it. It records that a more stable identifier
exists and was available, so a future reader weighing the email-change problem knows the option is real.

---

## 7. What would have to change, and who owns it

| To get | Change | Owner |
|---|---|---|
| `given_name`, `family_name` | `OAUTH_SCOPE` in `CognitoLoginAction:178` → `"openid email profile"`, and update the test that pins the scope (`everyOtherAuthorizeParameterIsUnchangedByIdentityProvider`) | **MARLO** — one line plus a test |
| The AD login | Azure AD must emit a claim sourced from `user.onpremisessamaccountname`, **and** Cognito must map it. Not to `username` (produces `cognito:username`, prefixed) — to a target MARLO can read | **Azure AD tenant owner + pool owner.** This is OQ-18 |
| Anything about a third party | The admin API (`ListUsers` / `AdminGetUser`) | **Pool owner.** Blocked: `marlouser` has no `cognito-idp` permissions at all (verified 2026-09-08) |

**Filterable-attribute constraint, for whoever designs the mapping.** `ListUsers` can filter only on a closed
set of standard attributes — `username`, `email`, `phone_number`, `name`, `given_name`, `family_name`,
`preferred_username`, `sub`, `cognito:user_status`, `status`. **Custom (`custom:`) attributes are not
filterable.** So mapping the AD login to `custom:ad_login` would make it *readable* but never *searchable*;
only `preferred_username` is both. Whether `preferred_username` can be a federation mapping target depends on
the pool's alias configuration — **unverified**, and it must be checked against the pool rather than assumed.

---

## 8. The question this inventory exists to answer

The point of the Cognito migration is to stop depending on the `org.cgiar.ciat.auth` library. So the
operative question is not *what can Cognito tell us* in the abstract — it is **whether Cognito can supply
what each AD consumer actually reads.** Enumerated against the working tree on 2026-09-08:

| Consumer | Asks about | Fields read |
|---|---|---|
| `CrpUsersAction:636-641` | **a third party** | `isFound`, `getFirstName`, `getLastName`, `getLogin` |
| `ManageUsersAction:156-159` (global) | **a third party** | `isFound`, `getFirstName`, `getLastName`, `getLogin` |
| `ManageUsersAction:258-264` (center) | **a third party** | the same, plus `getSource` |
| `SearchUserAction:197-204` | **a third party** | the same, plus `getEmail` |
| `GuestUsersValidator:45` | **a third party** | `isFound` |
| `APCustomRealm.getCgiarNickname:329-341` | **the person signing in** | the AD login |

`utils/searchUsersUtil.java` also holds a direct `new LDAPService()`, but it is a `main()` method with no
callers — developer scratch, to be deleted with the retirement rather than replaced.

### 8.1 The conclusion

**Cognito claims can replace none of the six.**

* **Five of the six ask about a third party.** An ID token describes only the person who just authenticated.
  That is not a scope or configuration limit — it is what OIDC is. No claim can be requested that answers a
  question about somebody else.
* **The one that concerns the authenticated user needs the AD login** — the single field the pool does not
  carry (§3), measured twice.

So `given_name` and `family_name`, the useful find of 2026-09-08, **serve no existing AD consumer.** They
would serve only a capability MARLO does not have today: provisioning a user at first sign-in.

### 8.2 What the migration actually replaces

| `adauth` capability | Replacement |
|---|---|
| Authenticate (`LDAPAuthenticator`) | ✅ **Cognito.** Shipped and working |
| Directory lookup (`DirectoryService.findByEmail`) | ❌ **No replacement.** Structurally outside what Cognito claims can do |
| Repair `users.username` at sign-in (`getCgiarNickname`) | ❌ Depends on OQ-18 |

`directory-abstraction` did its job: all five third-party consumers now go through **one seam**,
`DirectoryService.findByEmail`, so putting a different source behind it is a contained change. That source
simply cannot be Cognito.

The sibling Alliance application already runs this separation in production — Cognito authenticates, and a
locally mirrored table synced from the HR system supplies the directory (§9). MARLO's own
`DirectorySource` enum already reserves the alternatives: `DIRECTORY_API`, `CLARISA`, `AD_MIRROR`,
`INVITATION`.

### 8.3 Consequence for how the open questions are ranked

**OQ-18** — mapping the `sAMAccountName` — resolves **one consumer of six**.
**OQ-21** and the choice of directory source resolve **five**.

The plan currently treats them as comparable. They are not.

And one thing can now be stated without waiting on anyone: **the AD retirement is blocked on the directory,
not on authentication.** Authentication is done.

> **A parity argument that does not exist.** `getCgiarNickname` (`APCustomRealm:329-341`) repairs **only**
> `users.username`. It does not touch `first_name` or `last_name`, and neither does any other login path. So
> there is no name-refresh behaviour that retirement would remove and a Cognito claim would have to restore.
> Recorded because the opposite is easy to assume.

---

## 9. What this does NOT resolve

**The directory capability.** The token describes only the person who just authenticated. There is no way to
ask an OIDC flow about a third party. The three admin sites that create users from a directory lookup —
`CrpUsersAction:641`, `ManageUsersAction:159` (global) and `:261` (center) — need data about people who have
**never signed in**, and nothing in this inventory helps them.

That remains **OQ-21**: whether `ListUsers` can find a federated identity that has never signed in. Still
open, and it decides whether `directory-retirement` is viable as planned.

**Prior art worth knowing.** The sibling Alliance application (`alliance-research-indicators`) uses Cognito
for authentication only, through ROAR Management, and solves the directory problem with a completely separate
pipeline: an `alliance_user_staff` table (`carnet`, `first_name`, `last_name`, `email`, `center`, `status`)
synced weekly from AGRESSO. **Cognito authenticates; AGRESSO supplies the directory.** Its user model has no
username field either. Two caveats before treating it as a template: AGRESSO covers Alliance staff, while
MARLO serves CGIAR users from other centers; and a weekly sync replaces a live lookup, which is a real
behavioural change.

---

## 10. How to reproduce

Only a real corporate sign-in can produce this data — there is no headless path. Verified: the app client
credentials are valid (`invalid_grant` on a bogus code, not `invalid_client`) but `client_credentials` returns
`invalid_scope`, so no machine-to-machine route exists.

The 2026-09-08 method, and the discipline it must keep:

1. Set `OAUTH_SCOPE` to `"openid email profile"` in `CognitoLoginAction`.
2. Capture `access_token` alongside `id_token` in `ExchangeResult` (only needed for §5).
3. **After** `validation.isAccepted()` — never before, so nothing inspects an unverified value — decode the ID
   token payload and log **field names** plus the values of an identity-candidate allowlist.
4. Never log the raw ID token, the access token, the authorization code, `state`, `nonce`, or the PKCE
   verifier. Route every value through `LogSanitizer` (strips CR/LF, caps at 200 chars).
5. **Run `CognitoLogHygieneTest` before booting.** It was 15/15 with the diagnostic in place. A diagnostic
   that leaks a secret is worse than the question it answers.
6. Revert afterwards. The scope change is deliberately caught by
   `everyOtherAuthorizeParameterIsUnchangedByIdentityProvider`, which fails while the diagnostic is applied —
   that red test is the reminder that the change is temporary.

Results land in `${log.folder}/marlo-${log.instance}.log`, greppable as `[COGNITO-DIAG]`.

---

## 11. Open decisions this inventory hands over

| # | Decision | Blocked by |
|---|---|---|
| 1 | Make `profile` permanent, to gain `given_name` / `family_name`. **Deferred**: §8.1 shows no existing AD consumer can use them, so the scope should travel with the feature that consumes it, not ahead of it | Nobody — MARLO's call |
| 2 | Whether to auto-provision a CGIAR user at first Cognito sign-in, now that name and surname are available. Today gate 1 forbids it (FN-002, "MUST NOT auto-provision") | Product decision; changes a stated requirement |
| 3 | Whether to pursue the `sAMAccountName` mapping (OQ-18) or accept a null `users.username` for accounts created after AD retirement | Pool and tenant owners — **A2-2459** |
| 4 | Whether Cognito can serve directory lookups at all | **OQ-21** — one `ListUsers` call, once IAM permits it |
| 5 | Grant MARLO's runtime IAM user `cognito-idp` permissions — it has none, and would need them if decision 4 goes Cognito's way | Infrastructure. Not in any current plan |
