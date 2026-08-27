# Migrate AD Authentication to Cognito — Spec Family Manifest

| Field | Value |
|---|---|
| Parent spec path | `docs/specs/changes/migrate-ad-authentication-to-cognito` |
| Spec Family ID | `CHG-COGNITO-FAMILY` |
| Date created | 2026-08-24 (on `staging-cognito`) · **rebuilt on this branch 2026-08-27** |
| Last updated | 2026-08-27 |
| Spec-family status | `open` |
| Owner | IBD Team — Alliance of Bioversity International and CIAT |
| Working branch | **`staging-cognito-impl`** |
| Source analysis | [`analysis/`](./analysis/) — 4 documents, `adauth-retirement-analysis.md` **Revision 3** is the source of truth |

> **Branch note — read before editing.** This manifest was originally authored on `staging-cognito`
> (commit `73fab253`) with two children. It was **rebuilt on `staging-cognito-impl`** on 2026-08-27 at
> the user's explicit direction, from the four analysis documents, without merging `staging-cognito`.
> See § *Cross-branch state* for exactly what exists where, and § *Decision Log* 2026-08-27 for the
> merge-conflict consequence.

---

## Scope Summary

MARLO authenticates and looks up CGIAR corporate users through the `org.cgiar.ciat.auth` (`adauth`)
library, which binds directly to CGIAR Active Directory. The programme replaces both capabilities and
then removes the library — **functional retirement first (zero runtime calls, library still
installed as a rollback net), physical deletion only after a stabilization window.**

The scope was chunked because `adauth` provides **two capabilities with different answers**:

| | Capability A — Authentication | Capability B — Corporate user lookup |
|---|---|---|
| Call sites today | 2 | 6 |
| Is the person present? | Yes, typing a password | **No** — an admin typed their email |
| Replacement | Amazon Cognito, federated to the corporate IdP | **Undecided** — 6 candidates. Candidate 1 (Cognito admin APIs) is **contested**, see OQ-21 |
| Status | Designed and specified | Open architectural decision (`DEC-002`) |

Cognito solves A completely. **Whether it can also serve B is the open question OQ-21**, and it is the
single highest-leverage unknown in the programme:

- **Analysis Revision 3 §1.4 (`[V-AWS]`)**: no. `ListUsers` / `AdminGetUser` read the User Pool's own
  directory, and under SAML/OIDC federation a federated identity receives a pool profile only on first
  successful sign-in — so no Cognito mechanism reaches a corporate person who has never logged in.
- **Product owner, 2026-08-27**: yes. Cognito exposes email-based lookup, and it federates to CGIAR AD
  as an external provider, so the email should be sufficient.

**Both readings agree that `ListUsers` filters by email and returns attributes.** They disagree only
about the *never-signed-in* person — which is exactly the case Capability B exists to serve. **One API
call against the real federated pool settles it** (OQ-21). Until then, no candidate is selected and
`DEC-002` stays `PENDING`.

---

## Children

| # | Spec Path | Depends on | Parallel-safe | Status |
|---|---|---|---|---|
| 1 | `changes/migrate-ad-authentication-to-cognito/directory-abstraction` | none | **yes** | `pending` |
| 2 | `changes/migrate-ad-authentication-to-cognito/auth-flow` | none | **yes** | `pending` |
| 3 | `changes/migrate-ad-authentication-to-cognito/directory-retirement` | `directory-abstraction`, `auth-flow` | no | `pending` |

> **This table is the exhaustive child set of the spec family.** No AKILI command creates a child
> spec folder without a prior manifest row. Adding a row is a HITL-approved manifest edit, not a
> side effect of execution.

**Row 1 is new** (approved 2026-08-27, see Decision Log). **`#` is build order by readiness, not
priority** — `directory-abstraction` leads because it is the only child with no unresolved external
blocker.

---

## Child scope boundaries

### 1 — `directory-abstraction` *(new)*

Puts every corporate-user lookup behind one interface, **still implemented by `adauth`**.

**Delivers:** `DirectoryPerson`, `DirectorySource`, `DirectoryService`, and `LdapDirectoryService`
(which delegates to `adauth` verbatim); migration of the 6 `marlo-web` consumers off `LDAPUser`;
elimination of the reachable-but-unread `ADConexion` construction in `ContactPersonAction`; a reusable
`DirectoryServiceContractTest`. Maps to **Checkpoints 2–3** of the execution plan (EXEC-030 … EXEC-053).

**Zero behavior change by construction.** `LdapDirectoryService` reproduces
`BaseAction.getOutlookUser()` exactly; equivalence is the acceptance criterion.

**Explicitly leaves to child 3:** selecting and implementing the Capability B provider, deleting
`adauth`, deleting the AD constants, and all infrastructure cleanup.

**Does not touch:** `APCustomRealm.java`, `LDAPAuthenticator.java`, `Authenticator.java`,
`DBAuthenticator`, `AuthenticationManager`, any `pom.xml`, or anything under `libs/**`. The two
Capability A call sites belong to child 2 and are protected here.

**Why it is unblocked:** analysis §4.5 — the abstraction is **identical under all six Capability B
candidates**, so it is not gated by `DEC-002`, OQ-3, or OQ-3b. It is described there as *"the
highest-value early work in the programme… worth building even if OQ-3b never resolves."*

### 2 — `auth-flow`

Replaces the CGIAR authentication branch (`users.is_cgiar_user = 1`) with Amazon Cognito.

**Delivers:** the Cognito token type and validator, realm token-type dispatch, session establishment,
ID-token validation, the specificity feature flag, configuration keys, and the login-page change.
Maps to **Checkpoint 1**.

**Explicitly leaves to children 1 and 3:** the 6 directory-search call sites and removal of `adauth`.

**Does not touch:** `Authenticator.java`, `DBAuthenticator`, `LDAPAuthenticator`,
`AuthenticationManager`, `MD5Convert`, `users.password`, or the `UsernamePasswordToken` path through
`APCustomRealm.doGetAuthenticationInfo()` — an `instanceof` guard is inserted *above* the existing
cast and everything from the cast down is preserved byte-for-byte. The local login flow is a non-goal
**by construction, not by care**.

**Blocked externally, not technically:** OQ-3 (will CGIAR IT federate MARLO) is a hard blocker for
implementation, though not for specification. Its spec is already written — see § *Cross-branch state*.

### 3 — `directory-retirement`

Removes `org.cgiar.ciat.auth` from the codebase entirely, and reaches both gates.

**Delivers:** the selected Capability B provider behind the child-1 interface, `UsernameAllocator`,
`CorporateDomainPolicy`, the `directory.source` switch, the provisioning-flow and frontend changes,
the `adauth` tripwires, the cutover to zero runtime usage (**Gate 1**), the stabilization window, and
physical deletion of the dependency, the two committed file-repos (16 + 11 jar versions), the legacy
classes and the four AD constants (**Gate 2**). Maps to **Checkpoints 4–8**.

**Depends on child 1** because the provider is swapped *behind* the interface child 1 creates —
without it, the swap is an unbounded refactor across 6 classes instead of one wiring change.

**Depends on child 2** for a hard reason: `APCustomRealm.getCgiarNickname()` calls `LDAPService` on
every CGIAR login. The jar cannot be deleted while that call site exists, and child 2 is what removes it.

---

## Why this split

| Reason | Detail |
|---|---|
| **Different risk profiles** | Child 2 can lock every CGIAR user out of MARLO. Child 1 cannot change behavior at all. Child 3 can break an autocomplete and, at Gate 2, is irreversible. They must not share a rollback decision |
| **Different rollout shapes** | Child 2 needs staged per-Global-Unit enablement with a live rollback path. Child 1 ships as an ordinary refactor. Child 3 spans an 8-week stabilization window |
| **Blocking independence** | An authentication defect must never hold up a directory refactor, or vice versa |
| **One child is unblocked and two are not** | This is the reason row 1 was added. Bundling the abstraction into child 3 would have parked ~10 days of ready, zero-risk, high-leverage work behind an unanswered question owned by CGIAR IT |

---

## Parallel-safety

**Children 1 and 2 are `Parallel-safe: yes`** — their file sets are disjoint:

| Shared writer the root guides flag | Child 1 | Child 2 | Child 3 |
|---|---|---|---|
| `marlo-data/.../config/APConstants.java` | — | adds the specificity constant | removes the four `*_AD` constants |
| `marlo-web/.../config/APConstants.java` | — | adds the specificity constant | removes the four `*_AD` constants |
| `marlo-web/.../action/BaseAction.java` | **rewrites `getOutlookUser()`** | — | — |
| `marlo-parent/pom.xml` | — *(unless `DEC-005` lands, see below)* | adds AWS SDK v2 + a JOSE library | removes `adauth` |
| `database/migrations/` | — | specificity migration | — |
| `global.properties` | — | login copy | directory copy |
| `struts-home.xml` | — | 2 new actions | — |

**Child 3 is `Parallel-safe: no`** against both others — it writes every file they write.

**Two caveats on children 1 and 2 running concurrently:**

1. **`DEC-005` (add test-scoped Mockito) would create a collision** on `marlo-parent/pom.xml`. Child 1
   does **not** need it — a hand-rolled fake `DirectoryService` is trivial for contract tests, and the
   analysis confirms MARLO's collaborators are constructor-injected interfaces (§2.8). Keep child 1
   free of `DEC-005` and the disjointness holds.
2. **Separate `git worktree` is mandatory, not optional.** Two `mvn` runs in one checkout contend for
   the same `target/`; a build running beside another worker measures the contention, not the change
   (root guides → `## Concurrency`).

---

---

## Configuration delivery — Cognito connection settings

**Decided 2026-08-27 by the product owner: the Cognito connection settings are supplied as
environment variables.**

This supersedes what analysis §4.6 assumed. That section maps the Cognito keys onto `APConfig`'s
`@Value` convention with the `${key:default}` form; it did **not** state where the values come from.
They come from the environment, not from a committed `marlo-${profile}.properties` file.

| Consequence | Why it matters |
|---|---|
| **Every Cognito key still needs `${key:default}`** in `APConfig` | The 63 existing `@Value` fields use no defaults. A key with no default and no environment value **fails Spring context startup**, which is a deployment-time failure, not a login-time one. Analysis §4.6 and touchpoint `#8` both flag this as 🟠 High — the env-var decision does not relax it, it makes it more likely to bite, because an unset env var is easier to ship than a missing properties line |
| **No Cognito secret ever enters the repository** | Reinforces `CLAUDE.md` Hard rule 12 and Protected Action `P6`. There is no "add it to `marlo-dev.properties`" step to get wrong |
| **`docs/infrastructure.md` § *Confirmation Needed* item 8 gains a concrete answer for this case** | Secret delivery for Cognito is: environment variables. The rotation policy is still open |
| **Local development needs the same variables exported** | The `## Local Environment` contract in `docs/infrastructure.md` must name them once child 2 defines them, or a developer's local run fails at startup with no obvious cause |

**Owner:** child 2 `auth-flow` defines the key names and the `APConfig` getters. Child 1
(`directory-abstraction`) introduces **no configuration at all** — `LdapDirectoryService` reads the
same `config.isProduction()` the existing code reads, and nothing more.

## Cross-branch state

`staging-cognito` holds 6 commits this branch does not. Nothing was merged.

| Artifact | On `staging-cognito` | On `staging-cognito-impl` (here) |
|---|---|---|
| Parent `proposal.md` | ✅ 424 lines, approved 2026-08-24 | ❌ not duplicated |
| `family.md` | ✅ 2 children | ✅ **this file** — rebuilt, 3 children |
| `analysis/` ×4 | ✅ | ✅ byte-identical copies |
| `auth-flow/requirements.md` · `design.md` · `tasks.md` · `judgment.md` | ✅ 1,647 lines | ❌ not duplicated |
| `directory-abstraction/` | ❌ | ✅ `proposal.md` |
| AKILI constitutional baseline (`.agents/`, Model Routing, `family.md` template) | ❌ | ✅ commit `56a83ed2` |

**Consequence:** the analysis documents contain relative links to `../proposal.md`,
`../auth-flow/requirements.md`, and `../auth-flow/design.md` that **do not resolve on this branch**.
They are not broken references to invent — they point at real, approved artifacts on
`staging-cognito`. See [`analysis/README.md`](./analysis/README.md) for the resolution map.

**Child 2 is not re-specified here on purpose.** Duplicating 1,647 lines of approved spec would create
two authorities for one child and guarantee a conflict when the branches meet.

---

## Carried-forward open questions

A child spec may not close a question it does not own.

| # | Question | Owned by | Blocks |
|---|---|---|---|
| ~~OQ-2~~ | ~~Option A or Option B?~~ → **RESOLVED 2026-08-24: Option A** (federated redirect for CGIAR users; local form untouched) | Parent — closed | — |
| **OQ-3** | Which IdP does CGIAR run, and **will CGIAR IT federate MARLO?** | Parent — external. **Deferred by the owner 2026-08-27 — to be established later** | Child 2 implementation. **Not child 1** |
| **OQ-3b** | Is that IdP one with a queryable HTTP directory API (Entra ID / Graph), or on-prem AD / ADFS with none? | Parent — external. **Deferred 2026-08-27.** May become moot if OQ-21 resolves yes | Child 3 design. **Not child 1** |
| **OQ-21** | **Can Cognito `ListUsers` (filter by `email`) return a CGIAR person who has NEVER signed in to MARLO through Cognito, on a pool with no pre-provisioned users?** The product owner holds that it can, because Cognito federates to CGIAR AD as an external provider. Analysis Revision 3 §1.4 / §4.3 candidate 1 holds that it cannot, because `ListUsers` reads the pool's own directory and a federated identity gets a pool profile only on first sign-in `[V-AWS]`. **Settled by one API call, not by argument** — see the test in `directory-abstraction/proposal.md` § *Problem*. If the answer is **yes**, candidate 1 wins on cost and OQ-3b / OQ-15 stop mattering for Capability B | Parent — **verify against the real pool once federation exists** | Child 3 design. **Not child 1** |
| **OQ-15** | Does CLARISA expose a people / user endpoint? **Ask first — cheapest possible answer, credentials already exist** | Parent — external | Child 3 |
| **OQ-20** | Is the invitation + JIT UX change acceptable to the business? | Parent — business | Child 3 |
| OQ-18 | Will `sAMAccountName` be mapped to `preferred_username` at federation time? | Parent — raise **with** the federation request | Child 2 |
| OQ-19 | How long is the stabilization window, and who signs Gate 1? | IBD Team lead | Child 3 |
| OQ-1 | How many users have `is_cgiar_user = 1`? | Child 2 | Rollout sizing |
| OQ-4 | Who consumes `/api/**` with Basic auth? **Narrowed** — all seeded API accounts are `is_cgiar_user = 0` | Child 2 | Risk R-4 |
| OQ-11 | Do type-2/5 Global Units have corporate users? | Child 2 | Gate 1 reachability |
| OQ-12 | Does the convention plugin expose `SearchUserAction` and `center/…/ManageUsersAction`? | Child 1 **migrates them either way**; only their *deletion* is gated | Child 3 |
| OQ-14 | Do CLARISA `partner-requests` and the QA service accept a synthesized username? | Child 3 | Child 3 |
| OQ-16 | Is `clarisa.publicUser` an `is_cgiar_user = 1` account? One SQL query | Child 2 | Gate 1 |
| OQ-5 | Is `ad_user` populated by a live job or a stale import? **Half-answered: nothing in MARLO writes it** | Child 3 | Candidate 5 only |

**Not one of these blocks child 1.** That is the finding that justified row 1.

---

## Decision Log

| Date | Decision | Rationale |
|---|---|---|
| 2026-08-24 | Split the approved scope into `auth-flow` and `directory-retirement` | Authentication and directory retirement have different blast radii and rollback costs. Bundling them would make an autocomplete regression a reason to roll back an authentication migration |
| 2026-08-24 | OQ-2 held at the parent, not delegated | It selects between two incompatible architectures; a child cannot make that call after the fact |
| 2026-08-24 | **OQ-2 resolved: Option A** (Cognito federated to CGIAR AD) | AWS does not permit `InitiateAuth` for federated users, so Option B required abandoning federation and running a User Migration Lambda against LDAP with no removal date. Option A is the only variant with a real end state |
| 2026-08-26 | Analysis realigned to **functional-first** retirement (Revision 3) | A `new ADConexion(...)` on a reachable action is runtime usage, not dead weight. Revision 2 mislabelled the one thing that actually belongs in the functional phase |
| **2026-08-27** | **Manifest rebuilt on `staging-cognito-impl` rather than merging `staging-cognito`** | User direction, explicit. **Accepted consequence:** this file and `staging-cognito`'s `family.md` will conflict if the branches are ever merged. Mitigation — this file is the newer authority and supersedes; `auth-flow/` and the parent `proposal.md` are deliberately *not* duplicated, so the conflict surface is one file rather than six |
| **2026-08-27** | **Added row 1, `directory-abstraction`** — HITL-approved manifest edit | Analysis §4.5 establishes the abstraction is identical under all six Capability B candidates and therefore blocked by nothing. Leaving it inside `directory-retirement` would park ready, zero-behavior-change work behind OQ-3b, a question owned by CGIAR IT with no committed answer date |
| **2026-08-27** | **Children 1 and 2 marked `Parallel-safe: yes`** (the original manifest marked both its children `no`) | Their file sets are genuinely disjoint once the abstraction is separated from the retirement: child 1 writes only `BaseAction` and new files, child 2 writes neither. Conditional on child 1 not taking `DEC-005` |
| **2026-08-27** | Build order set by **readiness**, not by the original numbering | Child 1 is the only child with no unresolved external blocker. Numbering it 1 makes the manifest state what can actually start |
| **2026-08-27** | **Cognito connection settings are delivered as environment variables** | Product-owner decision. Recorded because analysis §4.6 assumed `APConfig` keys without stating their source. Every key still requires the `${key:default}` form — an unset env var otherwise fails Spring context startup. See § *Configuration delivery* |
| **2026-08-27** | **OQ-3 / OQ-3b deferred; not asked of CGIAR IT yet** | Product-owner decision ("se averigua después"). Recorded so R1 and R2 stay visibly **Critical and unmitigated** rather than quietly assumed away. Neither blocks child 1 |
| **2026-08-27** | **OQ-21 opened: is candidate 1 (Cognito `ListUsers` by email) viable?** Not resolved in either direction | The product owner and analysis Revision 3 disagree, and the disagreement is decidable by a single API call against the real federated pool rather than by argument. Recording it as a testable question — instead of editing the analysis to match either position — keeps the `[V-AWS]` evidence chain intact and makes the cheapest candidate cheap to confirm. **If OQ-21 resolves yes, Bucket B's 27–36 day estimate drops materially and OQ-3b / OQ-15 become moot for Capability B** |
