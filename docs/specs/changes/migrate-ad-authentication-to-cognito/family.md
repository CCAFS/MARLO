# Migrate AD Authentication to Cognito — Spec Family Manifest

| Field | Value |
|---|---|
| Parent spec path | `docs/specs/changes/migrate-ad-authentication-to-cognito` |
| Date created | 2026-08-24 |
| Last updated | 2026-08-24 |
| Spec-family status | `open` |
| Owner | IBD Team — Alliance of Bioversity International and CIAT |
| Working branch | `staging-cognito` |
| Parent proposal | [`proposal.md`](./proposal.md) — approved 2026-08-24 |

---

## Children

| # | Spec Path | Depends on | Parallel-safe | Status |
|---|---|---|---|---|
| 1 | `changes/migrate-ad-authentication-to-cognito/auth-flow` | none | no | `pending` |
| 2 | `changes/migrate-ad-authentication-to-cognito/directory-retirement` | `changes/migrate-ad-authentication-to-cognito/auth-flow` | no | `pending` |

> **This table is the exhaustive child set of the spec family.** No AKILI command creates a child
> spec folder without a prior manifest row. Adding a row is a HITL-approved manifest edit, not a
> side effect of execution.

---

## Child scope boundaries

### 1 — `auth-flow`

Replaces the CGIAR authentication branch (`users.is_cgiar_user = 1`) with Amazon Cognito.

**Delivers:** the Cognito token type and validator, the realm token-type dispatch, session
establishment, ID-token validation, the specificity feature flag, configuration keys, and the
login-page change.

**Explicitly leaves to child 2:** the 8 directory-search call sites and the removal of the `adauth`
dependency.

**Does not touch:** `Authenticator.java`, `DBAuthenticator`, `LDAPAuthenticator`, `AuthenticationManager`, `MD5Convert`, `users.password`, or
the `UsernamePasswordToken` path through `APCustomRealm.doGetAuthenticationInfo()` (an `instanceof` guard is inserted *above* the existing cast; everything from the cast down is byte-for-byte preserved). The local login flow is a non-goal
by construction, not by care.

### 2 — `directory-retirement`

Removes `org.cgiar.ciat.auth` from the codebase entirely.

**Delivers:** a directory-search abstraction replacing `LDAPService` at the 8 remaining call sites
(`BaseAction.getOutlookUser`, `CrpUsersAction`, both `ManageUsersAction` classes, `SearchUserAction`,
`ContactPersonAction`, `GuestUsersValidator`, `searchUsersUtil`), deletion of the `adauth` dependency
from `marlo-parent/pom.xml`, `marlo-data/pom.xml`, and `marlo-web/pom.xml`, and deletion of the
committed file-repos — `marlo-data/src/main/resources/libs/org/cgiar/ciat/auth/adauth/` (16 versions, 1.1 → 5.7) **and** `marlo-web/src/main/resources/libs/org/cgiar/ciat/auth/adauth/` (11 versions, 1.1 → 2.2). Both, plus the `adauth` dependency in `marlo-parent/pom.xml`, `marlo-data/pom.xml`, and `marlo-web/pom.xml`.

**Depends on child 1** for a hard reason, not a soft one: `APCustomRealm.getCgiarNickname()` calls
`LDAPService` on every CGIAR login. The jar cannot be deleted while that call site exists, and child
1 is what removes it.

---

## Why this split

| Reason | Detail |
|---|---|
| **Different risk profiles** | Child 1 can lock every CGIAR user out of MARLO. Child 2 can break an autocomplete. They should not share a rollback decision |
| **Different rollout shapes** | Child 1 needs staged per-Global-Unit enablement with a live rollback path. Child 2 ships normally |
| **Blocking independence** | An authentication defect must never be held up by a directory-search defect, and vice versa |

---

## Why neither child is parallel-safe

Both write shared files that the root guides list as high-contention:

| Shared writer | Child 1 | Child 2 |
|---|---|---|
| `marlo-data/.../config/APConstants.java` | adds the specificity constant | removes the four `*_AD` constants |
| `marlo-web/.../config/APConstants.java` | adds the specificity constant | removes the four `*_AD` constants |
| `marlo-web/.../action/BaseAction.java` | — | rewrites `getOutlookUser()` |
| `marlo-parent/pom.xml` | adds AWS SDK v2 + JWT library | removes `adauth` |
| `database/migrations/` | specificity migration | — |

Running them concurrently would collide on both `APConstants.java` files and on `marlo-parent/pom.xml`.
Serialize.

---

## Carried-forward open questions

These were unresolved at parent-proposal approval and are inherited by the children named below.
A child spec may not close a question it does not own.

| # | Question | Owned by | Blocks |
|---|---|---|---|
| ~~OQ-2~~ | ~~Option A or Option B?~~ → **RESOLVED 2026-08-24: Option A** (federated, redirect for CGIAR users; local form untouched) | Parent — closed | ~~Child 1~~ — unblocked |
| OQ-3 | Which IdP does CGIAR run (ADFS / Entra ID), and will CGIAR IT federate MARLO? | Parent — external dependency | Child 1 implementation, not its specification |
| OQ-1 | How many users have `is_cgiar_user = 1`, and how many resolve in CGIAR AD today? | Child 1 | Rollout sizing |
| OQ-4 | Who consumes `/api/**` with Basic auth, and are any of them CGIAR users? | Child 1 | Risk R-4 — federated identities cannot use Basic auth |
| OQ-5 | Is `ad_user` populated by a live job, or a stale one-time import? | Child 2 | Directory-search target |
| OQ-6 | Microsoft Graph or the `ad_user` mirror? Cognito `ListUsers` is **not** a substitute — a federated pool contains only users who have already signed in | Child 2 | Child 2 design |

---

## Decision Log

| Date | Decision | Rationale |
|---|---|---|
| 2026-08-24 | Split the approved scope into two children | Authentication and directory retirement have different blast radii and different rollback costs. Bundling them would make an autocomplete regression a reason to roll back an authentication migration |
| 2026-08-24 | Both children marked `Parallel-safe: no` | Both write `APConstants.java` (×2) and `marlo-parent/pom.xml` |
| 2026-08-24 | OQ-2 held at the parent, not delegated to child 1 | It selects between two incompatible architectures; a child spec cannot make that call after the fact |
| 2026-08-24 | **OQ-2 resolved: Option A** (Cognito federated to CGIAR AD) | AWS does not permit `InitiateAuth` for federated users, so Option B would have required abandoning federation and running a User Migration Lambda against LDAP with no defined removal date. Option A is the only variant with a real end state. The user's earlier preference for preserving the login form was superseded once the constraint was established; the local form is preserved regardless, and only the CGIAR half becomes a redirect button |
