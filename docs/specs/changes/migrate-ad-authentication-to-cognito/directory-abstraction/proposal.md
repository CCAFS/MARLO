# Directory Abstraction — Proposal

## Document Control

| Field | Value |
|---|---|
| **Spec Path** | `changes/migrate-ad-authentication-to-cognito/directory-abstraction` |
| **Proposal ID** | `CHG-COGNITO-DIRECTORY-ABSTRACTION-001` |
| **Slug** | `directory-abstraction` — derived from the free-text argument *"mira la problemática de los 4 archivos de docs/cognito y saca la tarea que nos permita solucionar ese problema"*; the path is dictated by the existing family manifest, not by the argument |
| **Type** | **Change** |
| **Approval Mode** | **`gated`** — no end-to-end mandate was given |
| **Parent Spec** | [`../family.md`](../family.md) — child row **1** |
| **Depends on** | **none** |
| **Parallel-safe** | **yes** (with `auth-flow`, in a separate `git worktree`) |
| **Status** | Draft — awaiting approval |
| **Date** | 2026-08-27 |
| **Working branch** | `staging-cognito-impl` |
| **Source of truth** | [`../analysis/adauth-retirement-analysis.md`](../analysis/adauth-retirement-analysis.md) **Revision 3** §4.5, §5.3 · [`../analysis/adauth-retirement-execution-plan.md`](../analysis/adauth-retirement-execution-plan.md) CP2–CP3 |
| **Maps to** | Execution plan **Checkpoints 2–3**, tasks `EXEC-030` … `EXEC-053` |

---

## Intent

**Put every corporate-user lookup in MARLO behind one interface, while `adauth` is still the thing
implementing it.**

This is the one piece of the `adauth` retirement programme that **no unanswered question blocks**. It
changes no behavior, deletes no library, and removes no dependency — and it converts the eventual
provider swap from an unbounded refactor across six classes into a single wiring change.

Analysis §4.5 states it directly:

> *"This is what makes Step 2 safe to build before the decision is made — and it is the highest-value
> early work in the programme… It is worth building even if OQ-3b never resolves."*

---

## Problem / Current Behavior

MARLO reaches CGIAR Active Directory through `org.cgiar.ciat.auth` (`adauth` 5.7) for **two different
capabilities**. This proposal concerns only the second.

| | Capability A — Authentication | **Capability B — Corporate user lookup** |
|---|---|---|
| Call | `LDAPService.authenticateUser(email, password)` | `LDAPService.searchUserByEmail(email)` → `LDAPUser` |
| Sites | 2 | **6** |
| Is the person present? | Yes, typing a password | **No** — an admin typed their email |
| Purpose | Prove identity | Discover `email`, `first_name`, `last_name`, `username` **so MARLO can create a row in its own `users` table** |
| Owned by | child 2 `auth-flow` | **this proposal (the seam) + child 3 (the provider)** |

### The concrete problem, in three facts

**1. `adauth` types are imported directly into eight `marlo-web` classes.** Verified on this checkout
2026-08-27. **Two distinct sets overlap here, and conflating them is easy** — a class can hold its own
`new LDAPService()` call, or merely consume an `LDAPUser` someone else fetched, or both:

| # | File | Line | Reaches `adauth` how | Fate in this spec |
|---|---|---|---|---|
| 1 | `action/BaseAction.java` | `:4802`, imports `:103-104` | own `new LDAPService()` in `getOutlookUser()` | **method deleted** |
| 2 | `action/crp/admin/CrpUsersAction.java` | `:630`, import `:48` | consumes `LDAPUser` via `this.getOutlookUser()` | **migrated** |
| 3 | `action/json/global/ManageUsersAction.java` | `:151`, import `:24` | consumes `LDAPUser` via `this.getOutlookUser()` | **migrated** |
| 4 | `validation/superadmin/GuestUsersValidator.java` | `:36` own copy, `:55` call, imports `:23-24` | **its own duplicate** of `getOutlookUser` (declared `public`) | **migrated**, duplicate deleted |
| 5 | `action/json/global/SearchUserAction.java` | `:193`, imports `:30-31` | own `new LDAPService()` | **migrated** |
| 6 | `action/center/json/global/ManageUsersAction.java` | `:249`, imports `:24-25` | own `new LDAPService()` | **migrated** |
| 7 | `action/center/capdev/ContactPersonAction.java` | `:86`, `:93`, imports `:24-25` | own `new LDAPService()` **and** `new ADConexion()` | **deleted, not migrated** — never read (fact 2) |
| 8 | `utils/searchUsersUtil.java` | `:14`, imports `:3-4` | own `new LDAPService()` | **left alone** — `main()`, no caller, and it reads `getAttributes()`, which `DirectoryPerson` does not carry. Deleted in child 3 |

**Six classes are migrated** (rows 1–6, tasks `EXEC-034` … `EXEC-039`). Row 7 has its AD code *deleted*
in Checkpoint 3. Row 8 is a deliberate, named exception.

**Nothing can be removed while a third-party type sits in eight import lists.** Every candidate
replacement would otherwise touch all eight sites, and the diff would mix "change the provider" with
"change the call shape" — the two things a review most needs to see apart.

**2. `ContactPersonAction` constructs AD objects on a reachable endpoint and never reads them.**
`searchContact.do` **is registered** (`struts-json.xml:1041`), so `:86` (`new LDAPService()`) and `:93`
(`new ADConexion(...)`) execute on every hit. The `adConection` variable is **never read**; the live
search is `adUsermanager.searchUsers()` at `:99`; `getADFilter`'s only call site is **commented out**.

This is not dead weight — it is a live runtime AD call. Gate 1 requires **zero** runtime `ADConexion`
constructions, so it must go, and it is free to remove.

**3. The provider that would replace `adauth` for Capability B is undecided — and the leading
candidate is contested.** Six candidates are compared in analysis §4.3. `DEC-002` is `PENDING` and
Protected Action **P12** forbids an agent from selecting one.

**Candidate 1 — Cognito admin APIs — is under active dispute, and it is the cheapest candidate if it
holds.** The two positions, stated fairly:

| | Position |
|---|---|
| **Product owner (2026-08-27)** | Cognito **does** expose functions to retrieve a person's information by email. Because Cognito connects to CGIAR AD as an **external provider**, the email is sufficient to obtain that person's identity information |
| **Analysis Revision 3** (§1.4, §4.3 candidate 1, tagged `[V-AWS]`) | `ListUsers` / `AdminGetUser` read the **User Pool's own directory**. Under SAML/OIDC federation a federated identity receives a pool profile **only on first successful sign-in**, and federation exposes no directory-query surface over the IdP. Candidate 1 is therefore marked ❌ on *never-logged-in* and ❌ on *fresh/empty DB* |

**Both can be true at once, and that is the crux.** `ListUsers` genuinely does filter by email and
genuinely does return the person's attributes — **for a person whose pool profile exists.** The
disagreement is entirely about the *never-signed-in* case, which is the exact case Capability B has to
serve:

> An administrator provides the email of a corporate user who **may not yet exist in MARLO**, and
> **may never have authenticated through Cognito**. MARLO must obtain that person's corporate identity
> information and create the user in MARLO's own `users` table.
> — analysis §1.3

**This is settled empirically, not by argument.** See `OQ-21` in [`../family.md`](../family.md): once
the pool is federated to CGIAR AD, call `ListUsers` with `filter=email="<someone>@cgiar.org"` for a
person who has **never** signed in to MARLO through Cognito, on a pool with no pre-provisioned users.
One API call decides it. If it returns the person, candidate 1 wins on cost and OQ-3b/OQ-15 stop
mattering for Capability B — a material saving against the 27–36 day Bucket B estimate.

### Why that third fact is the reason this proposal exists

**The abstraction is identical under all candidates — including a Cognito-backed one — so this child's
scope does not change whichever way `OQ-21` resolves** (analysis §4.5).

The seam already anticipates it: `DirectorySource` carries **`COGNITO_CLAIMS`** as one of its seven
values, so a `CognitoDirectoryService` slots in behind the same interface as any other candidate, with
no change to a single consumer.

That is the argument for approving this child now rather than waiting for the dispute to resolve: the
work is the same either way, and it is what makes the dispute cheap to act on once it *is* resolved —
the winner becomes one class plus one config value instead of a refactor across six classes.

---

## Proposed Outcome

After this change:

| Observable | Before | After |
|---|---|---|
| `marlo-web` classes importing `org.cgiar.ciat.auth` | **8** | **1** — only `utils/searchUsersUtil.java` (see below) |
| Runtime `ADConexion` constructions | 1 (on every `searchContact.do` hit) | **0** |
| Reachable Capability B paths | 5 direct `LDAPService` sites + 3 indirect `LDAPUser` consumers | **1 interface, 1 implementation** |
| Swapping the Capability B provider costs | a refactor across 6 classes | **one `@Named` bean + one config value** |
| User-visible behavior | — | **identical** |
| `adauth` dependency | present in 3 POMs | **present in 3 POMs, unchanged** |

**Two `adauth` importers remain by design, in different modules:**

| File | Module | Why it stays | Removed in |
|---|---|---|---|
| `utils/searchUsersUtil.java` | `marlo-web` | A `main()` with no caller. It also reads `LDAPUser.getAttributes()`, an attribute map `DirectoryPerson` deliberately does not carry | child 3 |
| `security/directory/impl/LdapDirectoryService.java` | `marlo-data` | **The one file permitted to import `adauth`** — that is its whole purpose | child 3 |

`LDAPAuthenticator.java` in `marlo-data` also keeps its `adauth` import: it is Capability A, owned by
child 2, and protected here.

**Behavior equivalence is the acceptance criterion, not a hope.** `LdapDirectoryService` reproduces
`BaseAction.getOutlookUser()` (`:4802`) verbatim in behavior — `setInternalConnection(!config.isProduction())`,
`searchUserByEmail(email)`, `try/catch → null`. A test that can only pass by changing production
`searchUserByEmail(email)`, `try/catch → null`. A test that can only pass by changing production
behavior means the implementation is wrong, not the test.

---

## Scope

### In scope — Checkpoints 2 and 3

| Task | What |
|---|---|
| `EXEC-030` | `DirectoryPerson` (immutable: `found`, `email`, `login`, `firstName`, `lastName`, `source`, with a `notFound(email)` factory) and `DirectorySource` enum (`LDAP, DIRECTORY_API, CLARISA, COGNITO_CLAIMS, AD_MIRROR, INVITATION, NOT_FOUND`) |
| `EXEC-031` | `DirectoryService` interface — one method, `DirectoryPerson findByEmail(String)`. Contract: null/blank/malformed email → `notFound`, **never throws**; backend failure → `notFound`, **never throws**; `source` always populated |
| `EXEC-032` | `LdapDirectoryService` — the `adauth`-backed implementation, `@Named` bean following the existing `@Named("LDAP")` pattern. **The only file in the new package permitted to import `org.cgiar.ciat`** |
| `EXEC-033` | `DirectoryServiceContractTest` (abstract, reusable) + `LdapDirectoryServiceTest` |
| `EXEC-034` | Migrate `BaseAction` — `getOutlookUser(String) → LDAPUser` becomes `findCorporateUser(String) → DirectoryPerson` |
| `EXEC-035` | Migrate `CrpUsersAction` — preserve the `setCgiarUser(true)` / name / username assignments exactly |
| `EXEC-036` | Migrate `json/global/ManageUsersAction` — widest surface, 15 FTL pages |
| `EXEC-037` | Migrate `GuestUsersValidator` — delete its duplicate of the helper (declared `public`); `found` replaces `LDAPUser != null` |
| `EXEC-038` | Migrate `SearchUserAction` — **do not delete the class**; `OQ-12` is unresolved and deletion is child 3 |
| `EXEC-039` | Migrate `center/json/global/ManageUsersAction` — unreachable, but must still compile. **Migrate; do not delete** |
| `EXEC-040` | Verify `marlo-web` is free of `adauth` types — the exit criterion, proven by `grep` |
| `EXEC-050` | Delete `ContactPersonAction:86`, `:93` and the four local constant reads at `:88-91`, plus the two now-unused imports |
| `EXEC-051` | `ContactPersonActionTest` — assert the same map structure from a stubbed `AdUserManager`, and that no `adauth` type is instantiated |
| `EXEC-052` | Re-inventory remaining runtime call sites against the `EXEC-005` baseline. Expected: **3 live sites** — 2 Capability A + `LdapDirectoryService` |
| `EXEC-041`, `EXEC-053` | Checkpoint reports |

Also in scope: `EXEC-001` … `EXEC-006` (**Checkpoint 0** — toolchain, baseline, the `EXEC-003` drift
probe, the `EXEC-005` call-site inventory) as a precondition, if not already recorded.

### Out of scope — explicitly

| Left to | What |
|---|---|
| **child 2 `auth-flow`** | `APCustomRealm`, `MarloShiroConfiguration`, `LoginAction`, `CrpByUserEmailAction`, `ValidateUserAction`, `loginForm.ftl`, `login.js`, the specificity constant and migration, all Cognito code, all AWS SDK dependencies |
| **child 3 `directory-retirement`** | Selecting and implementing the Capability B provider, `UsernameAllocator`, `CorporateDomainPolicy`, the `directory.source` switch, the provisioning flow, the frontend name-assumption change, the tripwires, Gate 1, stabilization, and all physical deletion |
| **the security track** | Neutralizing the hardcoded AD service-account credential (`F-2`, `APConstants:646-647` / `:706-707`) and requesting rotation from CGIAR IT. **Deleting the constants is child 3** |
| **a separate ticket** | The `UserMySQLDAO.searchUser:144-156` HQL-injection shape — pre-existing and already reachable (analysis `R15` note) |

### Protected — appearance in any diff is a defect

`git diff --stat` is the check.

`APCustomRealm.java` · `LDAPAuthenticator.java` · `Authenticator.java` · `DBAuthenticator.java` ·
`AuthenticationManager.java` · `MD5Convert.java` · `users.password` · **every `pom.xml`** ·
**everything under `libs/**`** · `ContactPersonAction:99` and below · `ContactPersonAction:58-71`
(`getADFilter` — leave it; child 3 deletes it) · both `APConstants.java` files · `global.properties` ·
`struts-home.xml` · `database/migrations/`

---

## Non-Goals

1. **Removing `adauth`.** Not the dependency, not the JARs, not one legacy class. `adauth` is *more*
   entrenched after this change, not less — it gains a dedicated implementation class.
2. **Choosing the Capability B provider.** `DEC-002` is `PENDING`; **P12** forbids it.
3. **Changing any behavior.** Zero-diff behavior is the acceptance criterion.
4. **Touching authentication.** The two Capability A sites belong to child 2 and are protected here.
5. **Adding a mocking framework.** `DEC-005` is `PENDING` and this child does not need it — see Risks.
6. **Adding a cache.** `ad_user` is at most an optional decorator, and only in child 3 (§1.5).

---

## Affected Users, Systems, And Specs

| Affected | How |
|---|---|
| **End users** | **Not at all.** No user-visible change. This is the point |
| Program / Super Admins | No change now. They are the users the Capability B *provider* decision will eventually affect (child 3) |
| `marlo-web` | 7 files modified — 6 migrated consumers + `ContactPersonAction` (AD code deleted). `searchUsersUtil` deliberately untouched |
| `marlo-data` | 4 new files under `security/directory/` and `security/directory/impl/` |
| `marlo-utils`, `marlo-core`, `marlo-parent` | Untouched |
| Database | **No schema change, no migration** |
| Specs | [`../family.md`](../family.md) child 1 · unblocks child 3's provider swap · **no dependency on child 2** |
| Runbooks | `reports/ai-context/interceptor-validator-playbook.md` is **not** affected — this is not the interceptor stack. No `reports/ai-context/*` contract changes |

---

## Visual Reference

- **Source:** None.
- **Location:** n/a.
- **Notes:** Backend-only refactor with no UI surface. No screen, form, FTL macro, or design token
  changes. The frontend change that *does* belong to this programme — the provisioning form no longer
  assuming the backend fills names (analysis `R4`, `crpUsers.js:274-283`, `crpUsers.ftl:64,67`) — is
  child 3's, and must ship **with** the provider, never before it. A mockup would add nothing here.

---

## Requirement Delta Preview

### ADDED

- `DirectoryService.findByEmail(String) → DirectoryPerson` as the single seam for corporate-user
  lookup, with an explicit **never-throws** contract: invalid input and backend failure both return
  `notFound`, so callers degrade rather than fail (pre-empts `R7`).
- `DirectoryPerson.source` as part of the contract — a caller, a log line, and a support ticket can
  tell *"the corporate directory confirmed this person"* apart from *"we assumed it from the email
  domain."*
- `DirectoryServiceContractTest`, reusable by every future implementation — so child 3's provider swap
  is **covered by construction** rather than needing new tests.
- MARLO's **first authentication-adjacent tests** (currently zero exist).

### MODIFIED

- `BaseAction.getOutlookUser(String) → LDAPUser` becomes `findCorporateUser(String) → DirectoryPerson`.
  Wide caller set (9,753 LOC) — shared writer, serialize.
- The 6 migrated consumers consume `DirectoryPerson` instead of `LDAPUser`. **Field-for-field identical
  assignments.**
- `ContactPersonAction.searchADUser()` stops constructing AD objects. Same JSON, same `ad_user` source.

### REMOVED

- `org.cgiar.ciat.auth` imports from 6 `marlo-web` classes.
- The unread `LDAPService` / `ADConexion` construction and the four local constant reads in
  `ContactPersonAction`.
- `GuestUsersValidator`'s duplicate of `getOutlookUser` (declared `public`, no external caller).

**Nothing else. No dependency, no JAR, no constant, no class.**

---

## Approach Options

### Option 1 — Build the seam now, `adauth` behind it *(recommended)*

Create `DirectoryService` + `LdapDirectoryService`, migrate the 6 consumers, eliminate the
`ContactPersonAction` construction. Zero behavior change.

| | |
|---|---|
| ✅ | Unblocked by **everything** — not `DEC-002`, not OQ-3, not OQ-3b, not `DEC-005` |
| ✅ | Runs in parallel with child 2 in a separate worktree (disjoint file sets) |
| ✅ | Reduces child 3 from "refactor 6 classes + implement a provider" to "implement a provider" |
| ✅ | Independently revertible per consumer — each is one commit |
| ✅ | Eliminates one runtime AD call site toward Gate 1, for free |
| ✅ | Produces the contract test that covers the future swap |
| ⚠️ | Adds a class that exists only to be deleted in child 3 |
| ⚠️ | Touches `BaseAction` (9,753 LOC, wide caller set) |

### Option 2 — Wait for `DEC-002`, then do the seam and the provider together

| | |
|---|---|
| ✅ | No throwaway `LdapDirectoryService` |
| ✅ | One migration instead of two |
| ❌ | **Blocked on OQ-3b / OQ-15 with no committed answer date, both owned outside MARLO** |
| ❌ | The provider commit would mix "new call shape" with "new provider" — the review could not separate a refactor bug from a provider bug |
| ❌ | If R2 materializes (no candidate available), this work never happens *and* the type leakage stays forever |

### Option 3 — Migrate consumers to a local helper, no interface

Replace `LDAPUser` with a MARLO DTO returned by a static helper, no `DirectoryService`.

| | |
|---|---|
| ✅ | Slightly less code |
| ❌ | No seam. The provider swap is still an edit in every consumer |
| ❌ | A static helper is not injectable, so the contract test is not reusable and `DEC-005` becomes necessary |
| ❌ | Discards the `source` field, which is what makes a support ticket answerable |

---

## Recommended Approach

**Option 1.**

It is the smallest change that is genuinely useful, and the only one that is available today. The
reasoning that decides it:

1. **It is blocked by nothing.** Every other piece of this programme waits on a question owned by
   CGIAR IT. This does not.
2. **It cannot break anything.** No behavior change is not an aspiration here — it is the acceptance
   criterion, verified by a contract test plus a per-consumer `git diff` review.
3. **It is worth doing even if the programme is cancelled.** A third-party AD type in six business
   classes' imports is a defect on its own terms. §4.5: *"worth building even if OQ-3b never resolves."*
4. **It de-risks the decision that is blocked.** With the seam in place, each of the six candidates
   becomes one class plus one config value — which makes the `DEC-002` conversation cheaper and less
   irreversible, and removes "how big is the refactor" from a decision that should be about
   capability.
5. **The throwaway class is the price of the option, and it is small.** `LdapDirectoryService` is
   ~40 lines whose body is copied from an existing method. Option 2 saves those 40 lines by paying for
   them with an unbounded wait and an unreviewable provider commit.

**Sequencing note:** the execution plan orders CP2 after CP1 and Protected Action **P14** forbids
executing a later checkpoint early. That ordering is real but its *reason* is not — analysis §4.5 and
`EXEC-030`'s own preconditions both say *"Independent of CP1 — may run in parallel."* Approving this
proposal is the explicit authorization that reconciles the two. **Record it in the family Decision Log
so a later reader does not read it as a skipped checkpoint.**

---

## Risks, Dependencies, And Open Questions

### Risks

| # | Risk | Rating | Mitigation |
|---|---|---|---|
| **DA-1** | **`BaseAction` is a 9,753-LOC shared writer with a very wide caller set.** A signature change ripples | 🟠 High | `EXEC-034` is its own task and its own commit. Compile + Checkstyle + full `git diff` read. The 3 indirect callers (`CrpUsersAction:630`, `ManageUsersAction:151`, `GuestUsersValidator:55`) are migrated in their own tasks immediately after |
| **DA-2** | **A non-equivalent mapping.** `null` used to mean "not in AD"; if `found` is derived differently, a caller silently changes behavior | 🟠 High | `LdapDirectoryService` copies the existing body verbatim in behavior. `DirectoryServiceContractTest` asserts found/not-found, null and malformed input, and exception→`notFound`. **`EXEC-032`'s STOP rule: if a test can only pass by changing production behavior, fix the implementation, not the test** |
| **DA-3** | **No mocking framework exists** (`DEC-005` `PENDING`). Assertions of the form *"this collaborator was never called"* need hand-rolled spies | 🟡 Medium | **This child does not need `DEC-005`.** A fake `DirectoryService` for contract tests is a class with one method. Collaborators are constructor-injected interfaces and the `BaseAction` hooks are `public` and non-final (§2.8). **Keeping `DEC-005` out also preserves parallel-safety with child 2** — it would otherwise collide on `marlo-parent/pom.xml` |
| **DA-4** | **Line-number drift.** `BaseAction.getOutlookUser` moved from `:4797` to `:4802` | 🟡 Medium | `EXEC-003` drift probe is mandatory before any code change (session-start step S7). Drift is spot-checked in [`../analysis/README.md`](../analysis/README.md) |
| **DA-5** | **`OQ-12` — the convention plugin may expose `SearchUserAction` and `center/…/ManageUsersAction`**, neither of which is in any Struts XML | 🟡 Medium | **Migrate both, delete neither.** `EXEC-038` and `EXEC-039` say so explicitly. This child is safe either way; only child 3's deletion is gated on the probe |
| **DA-6** | **`ExternalPostUtils` gets reused for a future directory client** — it installs a trust-all `X509TrustManager` and disables SNI (`R13`) | 🟡 Medium | Not reachable in this child (no HTTP client is written). Carry the prohibition forward into child 3's design |
| **DA-7** | **`searchContact.do` behavior changes.** `EXEC-050` edits a live endpoint | 🟡 Medium | `:99` and everything below is **protected**. `EXEC-050`'s STOP rule: if `adUsermanager.searchUsers()` or any line below `:99` appears in the diff, halt. `EXEC-051` locks the JSON contract |
| **DA-8** | **Family-manifest merge conflict.** This branch's `family.md` and `staging-cognito`'s will conflict | 🟡 Medium | Accepted, recorded in the family Decision Log. Conflict surface is deliberately **one file** — `auth-flow/` and the parent `proposal.md` were not duplicated |
| **DA-9** | **A reviewer reads this as a skipped checkpoint** (P14) | 🟢 Low | Approval is recorded in the family Decision Log with §4.5 as the cited authority |

### Dependencies

| Dependency | Status |
|---|---|
| Checkpoint 0 (`EXEC-001` … `EXEC-006`) | **Required precondition.** Not yet run — the execution-state block reads `NOT STARTED` |
| `DEC-002` (Capability B provider) | `PENDING` — **does not block this child** |
| `DEC-005` (Mockito) | `PENDING` — **deliberately not needed** |
| child 2 `auth-flow` | **No dependency.** Parallel-safe in a separate worktree |
| CGIAR IT / OQ-3 / OQ-3b / OQ-15 / OQ-20 | **None of them block this child** |
| **`OQ-21`** (is Cognito `ListUsers` viable for Capability B?) | **Open — does not block this child.** The seam is identical whichever way it resolves; `DirectorySource.COGNITO_CLAIMS` already reserves the slot |
| Cognito environment variables | **Not needed by this child.** Child 1 introduces **zero** configuration — `LdapDirectoryService` reads the same `config.isProduction()` the existing code already reads. Child 2 owns the Cognito keys, delivered as env vars (family manifest § *Configuration delivery*) |
| Java 17 toolchain | `EXEC-001` verifies; `mvn -v` must report 17 |

### Open Questions

| # | Question | Blocks | Owner |
|---|---|---|---|
| **DA-OQ-1** | Should `DirectoryService` live in `marlo-data` (as the execution plan specifies) or `marlo-core`? The plan says `marlo-data/.../security/directory/`, consistent with `LDAPAuthenticator`'s package | Design phase only | Tech lead — **`/akili-specify` can resolve from the existing pattern** |
| **DA-OQ-2** | Does `findCorporateUser` keep `getOutlookUser` as a deprecated delegating alias for one release, or is the rename hard? A hard rename is a wider diff; an alias leaves a second path to the same call | `EXEC-034` | Tech lead |
| **DA-OQ-3** | `EXEC-052` expects exactly **3** live `adauth` call sites after this child. Is that number accepted as the checkpoint exit criterion, given `EXEC-005`'s baseline has not been captured yet? | `EXEC-052` | IBD Team lead |
| **DA-OQ-4** | Has the parent `proposal.md` on `staging-cognito` (approved 2026-08-24) been superseded by anything, or does its approval still stand for this branch? | Nothing technical; traceability | IBD Team lead |

**No open question here blocks starting.** DA-OQ-1 and DA-OQ-2 are resolved during `/akili-specify`;
DA-OQ-3 is resolved by running Checkpoint 0; DA-OQ-4 is a records question.

---

## Success Criteria

| # | Criterion | How it is verified |
|---|---|---|
| **SC-1** | `grep -rn "^import org.cgiar.ciat.auth" marlo-web/src --include="*.java"` returns **only** `utils/searchUsersUtil.java` | `EXEC-040` |
| **SC-2** | `grep -rn "^import org.cgiar.ciat.auth" marlo-data/src --include="*.java"` returns **exactly three**: `APCustomRealm.java` (Capability A, untouched), `LDAPAuthenticator.java` (Capability A, untouched), `security/directory/impl/LdapDirectoryService.java` (new) | `EXEC-040` |
| **SC-3** | **Zero** runtime `ADConexion` constructions remain in the codebase | `EXEC-050`, `EXEC-052` |
| **SC-4** | `mvn -q install -DskipTests -pl marlo-web -am` and `mvn -q checkstyle:check` both pass | every task |
| **SC-5** | `DirectoryServiceContractTest` + `LdapDirectoryServiceTest` + `ContactPersonActionTest` pass via `mvn -q -pl marlo-web test` | `EXEC-033`, `EXEC-051` |
| **SC-6** | **Behavior equivalence.** Every consumer's field assignments are byte-equivalent to before; no protected file appears in any diff | per-task `git diff` review + Reviewer audit |
| **SC-7** | `searchContact.do` returns the same JSON, from `ad_user`, constructing no AD object | `EXEC-051` |
| **SC-8** | `EXEC-052`'s re-inventory shows exactly **3** live `adauth` call sites, reconciled against `EXEC-005` | `EXEC-052` |
| **SC-9** | Every new `.java` file carries the **GPL header**, 2-space indent, ≤120-char lines | `mvn -q checkstyle:check` + Reviewer audit item 6 |
| **SC-10** | Swapping the provider is demonstrably one `@Named` bean plus one config value | design review, `/akili-validate` |

**SC-6 is the one that matters.** Everything else can pass while a mapping is subtly wrong.

---

## Next Step

```text
/akili-specify changes/migrate-ad-authentication-to-cognito/directory-abstraction
```

`/akili-specify` converts this into `requirements.md`, `design.md`, and `tasks.md`. It has an unusual
advantage here: **`tasks.md` is largely a translation, not a decomposition** — the execution plan
already specifies `EXEC-030` … `EXEC-053` with per-task objectives, preconditions, protected files,
instructions, verification commands, expected results, rollback, and STOP conditions.

Specify should:

1. **Adopt the `EXEC-` task IDs**, so this spec and the execution plan never diverge.
2. Carry every task's **Protected Files** list into `tasks.md` verbatim — it is the Reviewer's checklist.
3. Resolve **DA-OQ-1** and **DA-OQ-2** in `design.md`.
4. Keep the **agent-lean verification commands** from the root guides (`mvn -q …`), with the asymmetry
   rule: failures print complete and verbatim.
5. Add **Checkpoint 0** (`EXEC-001` … `EXEC-006`) as task 0 if it has not been run.

**Before implementation:** run the `EXEC-003` drift probe. `BaseAction` has already moved ~5 lines.
