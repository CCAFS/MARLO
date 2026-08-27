# Analysis — provenance and link resolution

These four documents are the **source of truth** for the `migrate-ad-authentication-to-cognito` spec
family. They were authored on branch `staging-cognito` and copied here **byte-identically** on
2026-08-27, when the family was rebuilt on `staging-cognito-impl` without merging.

| Document | Lines | What it answers |
|---|---|---|
| [`adauth-retirement-analysis.md`](./adauth-retirement-analysis.md) | 1,643 | **Revision 3 — the source of truth.** How MARLO replaces both `adauth` capabilities. Its Capability B conclusion is **contested** — see § *Contested finding* |
| [`adauth-retirement-execution-plan.md`](./adauth-retirement-execution-plan.md) | 1,505 | The runbook: 9 checkpoints, ~70 `EXEC-` tasks, Decision Registry, 15 Protected Actions, session-start procedure |
| [`impact-analysis.md`](./impact-analysis.md) | 311 | System impact: the single dispatch point, all 4 realm surfaces, the hardcoded-credential finding |
| [`touchpoint-inventory.md`](./touchpoint-inventory.md) | 174 | Every file to change, create, or verify unchanged — with exact line numbers |

**Do not edit these to fix a link.** They are the authored analysis; the resolution map below exists
so their links can be followed without modifying them.

---

## Link resolution map

The documents contain relative links written on `staging-cognito`. Three targets do not exist on this
branch — they are **real, approved artifacts elsewhere**, not broken references:

| Link in the documents | Resolves to |
|---|---|
| `../family.md` | ✅ [`../family.md`](../family.md) — present, rebuilt for this branch (3 children) |
| `../proposal.md` | ❌ not on this branch → `git show staging-cognito:docs/specs/changes/migrate-ad-authentication-to-cognito/proposal.md` |
| `../auth-flow/requirements.md` | ❌ not on this branch → `git show staging-cognito:docs/specs/changes/migrate-ad-authentication-to-cognito/auth-flow/requirements.md` |
| `../auth-flow/design.md` | ❌ not on this branch → `git show staging-cognito:docs/specs/changes/migrate-ad-authentication-to-cognito/auth-flow/design.md` |
| `../auth-flow/tasks.md` | ❌ not on this branch → same pattern |

Reading them with `git show` requires no merge, no branch switch, and no working-tree change.

---

## Known line-number drift on `staging-cognito-impl`

The documents' line references were exact against `staging-cognito`. Verified 2026-08-27 against
**this** checkout:

| Reference in the documents | Actual here | Impact |
|---|---|---|
| `BaseAction.getOutlookUser()` at `:4797-4811`, imports `:103-104` | `new LDAPService()` at **`:4803`** | ~5 lines. `EXEC-034` must re-locate before editing |
| `ContactPersonAction` `new LDAPService()` at `:86` | `:86` ✅ | none |
| `ContactPersonAction` `new ADConexion(...)` at `:93` | `:93` ✅ | none |
| `ManageUsersAction` (center/json/global) at `:249` | `:249` ✅ | none |
| `SearchUserAction` at `:193` | `:193` ✅ | none |
| `searchUsersUtil` at `:14` | `:14` ✅ | none |
| `GuestUsersValidator` at `:37` | `:37` ✅ | none |
| `APCustomRealm` LDAP call at `:287` | `:287` ✅ | none |
| `LDAPAuthenticator` at `:61` | `:61` ✅ | none |

**`EXEC-003` is the designated drift probe** and must run before any code change, per the execution
plan's mandatory session-start step S7. The above is a spot-check, not a substitute for it.

---

## Terminology reminder

The analysis uses these words precisely; mixing them caused the sequencing errors it had to correct:

| Term | Means | Reached at |
|---|---|---|
| **Functional retirement** | MARLO never invokes `adauth` at runtime. Library still in Maven, still on the classpath, still compiling — **deliberately, as a rollback net** | Checkpoint 6 → **Gate 1** |
| **Stabilization** | An agreed calendar window proving it works. **Mandatory.** `adauth` stays present throughout | Checkpoint 7 |
| **Physical retirement** | Dependency, JARs, legacy classes, constants, service account and firewall rules deleted | Checkpoint 8 → **Gate 2** |

> **The migration objective is functional retirement.** Physical retirement is the cleanup that
> follows it, not the goal that drives it.

---

## Contested finding — under verification, not corrected

**Analysis §1.4 and §4.3 candidate 1 are disputed by the product owner as of 2026-08-27.** The
documents are **not** edited to reflect either position; the dispute is tracked as **`OQ-21`** in
[`../family.md`](../family.md).

| Position | Claim |
|---|---|
| Analysis Revision 3, `[V-AWS]` | Cognito cannot serve Capability B. `ListUsers` / `AdminGetUser` read the User Pool's own directory; under SAML/OIDC federation a federated identity gets a pool profile only on first successful sign-in |
| Product owner | Cognito can. It exposes email-based lookup and federates to CGIAR AD as an external provider, so the email should suffice |

**Why the documents were left alone.** Editing an authored `[V-AWS]` finding to match an unverified
counter-claim would destroy the evidence chain the tag exists to protect — and editing it the other
way would dismiss the domain owner. The honest third option is the one taken: record both positions
and the test that decides between them.

**The test** — one API call, once the pool is federated to CGIAR AD:

```
aws cognito-idp list-users \
  --user-pool-id <pool> \
  --filter 'email = "<someone>@cgiar.org"'
```

with a person who has **never** signed in to MARLO through Cognito, on a pool with **no**
pre-provisioned users.

| Result | Consequence |
|---|---|
| Returns the person | Candidate 1 is viable. It becomes the cheapest option, `OQ-3b` and `OQ-15` become moot for Capability B, and Bucket B's 27–36 day estimate drops materially. **Analysis §1.4 and §4.3 then need a Revision 4.** |
| Returns empty | Analysis Revision 3 stands as written. No document changes |

**Neither outcome changes `directory-abstraction`'s scope** — the abstraction is identical under all
candidates, and `DirectorySource.COGNITO_CLAIMS` already reserves the slot for a Cognito-backed
implementation.
