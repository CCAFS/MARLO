# Directory Abstraction — Requirements

**Spec ID:** `CHG-COGNITO-DIRABS-001`
**Requirement prefix:** `DIRABS`
**Depth:** **Standard**
**Type:** Change · **Approval Mode:** `gated`
**Status:** **Approved** — 2026-08-28, approved by the user acting as Tech lead at the `/akili-execute` gate
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Reviewers:** Tech lead, IBD Team lead
**Last Updated:** 2026-08-28
**Parent Spec:** [`../family.md`](../family.md) — child row 1
**Proposal:** [`proposal.md`](./proposal.md)
**Related PRD sections:** `docs/prd.md` §4.1 goal 4 (multi-program flexibility), §4.1 goal 7 (self-hostable)
**Related UX/UI Design sections:** none — no UI surface
**Related TRD sections:** `docs/trd/trd.md` §2 (domain modules), §3.4 (layered persistence), §8.1 (authentication), §10 (testing strategy), §14.5 MO-1/MO-2 (modifiability), §14.7 TS-1/TS-2 (testability)
**Source analysis:** [`../analysis/adauth-retirement-analysis.md`](../analysis/adauth-retirement-analysis.md) Rev 3 §4.5, §5.3 · [`../analysis/adauth-retirement-execution-plan.md`](../analysis/adauth-retirement-execution-plan.md) CP2–CP3
**Companion ai-context docs:** none — this spec touches no routing, save-pipeline, replication, or composition contract

---

## 1. Overview

Put every corporate-user lookup in MARLO behind **one interface**, with `adauth` still implementing it.

This spec delivers **no user-visible change**. Its entire value is structural: it removes a third-party
Active Directory type from MARLO's business classes and reduces the eventual provider swap from a
refactor across six classes to a single bean plus a config value.

**Depth justification — why Standard and not Full.** The work is cross-cutting (7 files modified across
2 Maven modules, 4 files created) and sits inside an authentication-migration programme, which argues
for Full. But it changes no behavior, adds no dependency, adds no configuration, touches no schema, and
its risk register, rollback procedure and per-task STOP conditions **already exist** in the execution
plan. Re-deriving them at Full depth would duplicate an authored artifact rather than add rigor.
Standard, citing the execution plan for rollback and risk, is the honest depth.

---

## 2. Problem Statement

`org.cgiar.ciat.auth` (`adauth` 5.7) gives MARLO two capabilities. This spec concerns only the second:
**corporate user lookup** — `LDAPService.searchUserByEmail(email) → LDAPUser`, used so an administrator
can create a MARLO `users` row for a CGIAR person who is not present and may not exist in MARLO yet.

Three verified facts define the problem. **All line numbers verified against `staging-cognito-impl`
on 2026-08-27** — where they differ from the source analysis, the value here is authoritative.

### 2.1 `adauth` types are imported into eight `marlo-web` classes; six are migrated

| # | File | Line | How it reaches `adauth` | Fate in this spec |
|---|---|---|---|---|
| 1 | `action/BaseAction.java` | `:4802` method, imports `:103-104` | `new LDAPService()` inside `getOutlookUser()` | **Method deleted** (DD-2) |
| 2 | `action/crp/admin/CrpUsersAction.java` | `:630`, import `:48` | calls `this.getOutlookUser(...)` | Migrated |
| 3 | `action/json/global/ManageUsersAction.java` | `:151`, import `:24` | calls `this.getOutlookUser(...)` | Migrated |
| 4 | `validation/superadmin/GuestUsersValidator.java` | `:36` own copy, `:55` call, imports `:23-24` | **its own duplicate** of `getOutlookUser` (declared `public`) | Migrated, duplicate deleted |
| 5 | `action/json/global/SearchUserAction.java` | `:193`, imports `:30-31` | `new LDAPService()` directly | Migrated |
| 6 | `action/center/json/global/ManageUsersAction.java` | `:249`, imports `:24-25` | `new LDAPService()` directly | Migrated (see DD-3) |

Two further `adauth` importers in `marlo-web` are **deliberately not migrated**:

| File | Line | Why not |
|---|---|---|
| `action/center/capdev/ContactPersonAction.java` | `:86`, `:93`, imports `:24-25` | Its AD code is **deleted**, not migrated — it is never read (§2.2) |
| `utils/searchUsersUtil.java` | `:14`, imports `:3-4` | A `main()` method, unreachable. It also reads `LDAPUser.getAttributes()`, a field `DirectoryPerson` deliberately does not carry. Deleted in child 3 |

> **Correction applied to `proposal.md` on 2026-08-27.** The proposal originally presented the six
> `new LDAPService()` sites as "the six consumers to migrate". Reading the code showed they are
> **different sets**: `ContactPersonAction` and `searchUsersUtil` hold `LDAPService` calls but are not
> migrated, while `CrpUsersAction` and `json/global/ManageUsersAction` are migrated consumers with no
> `LDAPService` call of their own. `proposal.md` § *Problem* fact 1 now carries the corrected
> eight-row table, and this table remains the authoritative copy.

### 2.2 `ContactPersonAction` constructs AD objects on a live endpoint and never reads them

`searchContact` is registered at **`struts-json.xml:1041`**, so the method executes on every hit:

| Line | Code | Read anywhere? |
|---|---|---|
| `:86` | `LDAPService service = new LDAPService();` | **Never.** The variable is unused |
| `:88-91` | four reads of `APConstants.{GENERICUSER,GENERICPASSWORD,HOSTNAME,PORT}_AD` | Only by `:93` |
| `:93` | `ADConexion adConection = new ADConexion(genericUser, genericPassword, hostName, port)` | **Never.** No read of `adConection` exists |
| `:95-96` | `adConection.searchUsers(...)` | **Commented out** |
| `:99` | `List<AdUser> ad_users = adUsermanager.searchUsers(queryParameter);` | **This is the live search** — reads the `ad_user` table |
| `:58-71` | `getADFilter(String)` | Builds a `String`; needs no `adauth` type. Its only call site is the commented-out `:95-96` |

This is not dead weight — it is a **live runtime AD connection attempt** on every request, whose result
is discarded. Gate 1 of the parent programme requires zero runtime `ADConexion` constructions.

### 2.3 The eventual replacement provider is undecided, and one candidate is contested

`DEC-002` is `PENDING`; Protected Action `P12` forbids an agent from selecting a provider. `OQ-21`
(can Cognito `ListUsers` resolve a never-signed-in person?) is open and decidable by one API call.

**This spec is unaffected either way** — analysis §4.5 establishes the abstraction is identical under
every candidate, and `DirectorySource` reserves `COGNITO_CLAIMS` for a Cognito-backed implementation.

---

## 3. Glossary

| Term | Meaning |
|---|---|
| **Capability B** | Corporate user lookup: obtaining a CGIAR person's identity by email so MARLO can create a `users` row. The person is **absent** |
| **Consumer** | A `marlo-web` class that today consumes an `LDAPUser` |
| **Seam** | `DirectoryService` — the single interface every consumer calls after this spec |
| **Equivalence** | Byte-identical observable outcome before and after, for the same input. **The acceptance criterion of this spec** |
| **Raw value** | A field returned exactly as `adauth` returned it, with no case or whitespace transformation applied by the abstraction |
| **Functional retirement** | MARLO never invokes `adauth` at runtime. **Not reached by this spec** — `adauth` is still the implementation |

---

## 4. System Context & Scope

### In scope

`marlo-data`: a new `security/directory/` package (**4** types: `DirectoryPerson`, `DirectorySource`, `DirectoryService`, `DirectoryLookupException`) and `security/directory/impl/` (**1** type: `LdapDirectoryService`) — **5 new files** in total.
`marlo-web`: 7 files modified. Execution-plan tasks `EXEC-030` … `EXEC-053`.

### Out of scope

| Left to | What |
|---|---|
| child 2 `auth-flow` | All Cognito code, `APCustomRealm`, `LDAPAuthenticator`, the specificity flag, AWS SDK dependencies, Cognito environment variables |
| child 3 `directory-retirement` | Selecting/implementing the Capability B provider, `directory.source` switch, `UsernameAllocator`, deleting `adauth`, deleting the four `*_AD` constants, deleting `getADFilter`, deleting `searchUsersUtil` |
| the security track | Neutralizing the hardcoded AD service-account credential (`APConstants:646-647` / `:706-707`) |
| a separate ticket | `UserMySQLDAO.searchUser:144-156` HQL-injection shape (pre-existing, already reachable) |

### Protected — appearance in any diff is a defect

`APCustomRealm.java` · `LDAPAuthenticator.java` · `Authenticator.java` · `DBAuthenticator.java` ·
`AuthenticationManager.java` · `MD5Convert.java` · `users.password` · every `pom.xml` · everything under
`libs/**` · `ContactPersonAction:99` and below · `ContactPersonAction:58-71` (`getADFilter`) · both
`APConstants.java` · `global.properties` · `struts-home.xml` · `struts-json.xml` ·
`database/migrations/` · `utils/searchUsersUtil.java`

---

## 5. Stakeholders / Personas

| Persona | Impact |
|---|---|
| **All end users** (`docs/prd.md` §3) | **None.** Zero user-visible change is the requirement, not a side effect |
| Program / Super Admin | None now. They are the persona the *provider* decision will eventually affect (child 3) |
| Developer / AKILI Implementer | Primary beneficiary — `MO-2` change cost drops for any future directory work |
| Tech lead | Owns DD-2 and DD-3 |

---

## 6. Functional Requirements

### DIRABS-FN-001 — A single lookup seam

The system **SHALL** expose exactly one interface for corporate-user lookup, `DirectoryService`, with
exactly one method: `DirectoryPerson findByEmail(String email)`.

#### Scenario: Every consumer goes through the seam

- **GIVEN** the six consumers listed in §2.1
- **WHEN** any of them needs a corporate person's identity by email
- **THEN** it **MUST** call `DirectoryService.findByEmail(email)`
- **AND** it **MUST NOT** import any `org.cgiar.ciat` type
- **BUT** it must **NOT** construct `LDAPService`, `ADConexion`, or any other `adauth` type directly
- **AND IT MUST** receive `DirectoryService` through constructor injection, consistent with the existing
  `@Inject` constructor pattern (`CrpUsersAction:117-120`, `ReportSynthesisSectionValidator:82`)

---

### DIRABS-FN-002 — The seam never throws

`DirectoryService.findByEmail` **MUST NOT** propagate an exception under any input or backend condition,
**and MUST record in `source` why it failed** so a caller that needs the distinction can act on it.

> **Amended 2026-08-27 by `design.md` DD-3.** This requirement originally collapsed *every* failure into
> `found == false`, on the grounds that `BaseAction.getOutlookUser` does. The Step 2.3 reversion
> challenge (`design.md` §10.0, **C-4**) showed that collapse produces a **false statement to an
> administrator** during an AD outage — `center/…/ManageUsersAction.create():127-131` maps a null result
> to `manageUsers.email.doesNotExist`, so an outage would report that a real CGIAR employee does not
> exist, and an admin acting on that would create them as a **non-CGIAR** user with hand-typed names.
> `DirectorySource.ERROR` fixes it for one enum constant. **DEV-1 is withdrawn** (§8).

#### Scenario: Invalid input

- **GIVEN** a caller invokes `findByEmail`
- **WHEN** the email is `null`, blank, or malformed
- **THEN** it **MUST** return a `DirectoryPerson` with `found == false`
- **AND** `source` **MUST** be `NOT_FOUND` — invalid input is not a backend failure
- **BUT** it must **NOT** throw, and must **NOT** return `null`
- **AND IT MUST** make no network call for a `null` or blank email
- **AND** *(clarified 2026-08-28 — see Decision Log)* **"malformed" is defined as: not `null`, not blank,
  but failing a minimal well-formedness check — a single `@` with a non-empty local part and a domain
  part containing at least one `.`.** It is deliberately **not** a full RFC 5322 validation: an
  over-strict predicate would reject unusual-but-valid corporate addresses that resolve today.
- **AND** the `NOT_FOUND` outcome for a malformed email **MUST** be reached by **discriminating inside
  the exception handler**, not by validating before the lookup. A malformed email **is** passed to the
  backend, exactly as today; if the backend then throws, the implementation checks well-formedness and
  returns `NOT_FOUND` for a malformed input and `ERROR` only for a well-formed one. Rationale: the
  no-network-call guarantee above is scoped to `null`/blank alone, which means a malformed email is
  **expected** to reach the backend; and confining the predicate to the failure path makes a mistake in
  it unable to produce a spurious `NOT_FOUND` for a valid address that would otherwise have resolved.

#### Scenario: The directory answers, and the person is absent

- **GIVEN** a well-formed email the directory does not resolve
- **WHEN** `findByEmail` is invoked
- **THEN** it **MUST** return `found == false` with `source == NOT_FOUND`
- **AND IT MUST** be reachable only after the directory actually answered — `NOT_FOUND` asserts
  knowledge, not absence of knowledge

#### Scenario: Backend failure

- **GIVEN** the directory backend is unreachable, times out, or throws
- **WHEN** `findByEmail` is invoked with a well-formed email
- **THEN** it **MUST** return `found == false`
- **AND** `source` **MUST** be **`ERROR`**, never `NOT_FOUND`
- **AND** the failure **MUST** be logged at `error` with the email and the cause — today it is
  swallowed with no log at all, which is unobservable
- **AND** *(clarified 2026-08-28)* the `error` log **MUST NOT** be emitted when the thrown failure is
  attributable to a malformed email — that path is `NOT_FOUND` per the *Invalid input* scenario, and
  logging it at `error` would fill the log with admin typos while burying real outages
- **BUT** it must **NOT** throw — the *contract* never throws; a caller may choose to
- **AND IT MUST** leave a caller that reads only `found` behaving **exactly** as today: `ERROR` and
  `NOT_FOUND` are both `found == false`, so the five callers that ignore `source` are unaffected

#### Scenario: A caller that must not silently degrade

- **GIVEN** `center/json/global/ManageUsersAction.validateOutlookUser`, which today has **no
  `try/catch`** at `:255` and therefore propagates an `adauth` exception
- **WHEN** `findByEmail` returns `source == ERROR`
- **THEN** it **MUST** throw `DirectoryLookupException` (unchecked, wrapping the cause) rather than return `null` — see `design.md` **DD-3a** for why unchecked and why it must not extend `AuthorizationException`
- **AND** it **MUST** still return `null` on a genuine `NOT_FOUND`, which is what `create():128` handles
- **BUT** it must **NOT** report `manageUsers.email.doesNotExist` for a backend failure — that message
  would be factually false and can lead to a permanently mis-classified `users` row
- **AND IT MUST** be the **only** caller that reads `source`; the other five read `found` alone

### DIRABS-FN-003 — `source` is always populated

`DirectoryPerson.source` **MUST** carry a non-null `DirectorySource` on every returned instance.

#### Scenario: Attribution is answerable

- **GIVEN** any `DirectoryPerson` returned by any implementation
- **WHEN** a support engineer or a log line inspects it
- **THEN** `source` **MUST** distinguish *"the corporate directory confirmed this person"* from
  *"this was assumed"* or *"not found"*
- **AND** the `LDAP` implementation **MUST** set `source == LDAP` on a found person
- **AND** it **MUST** set `source == NOT_FOUND` when the directory answered and the person is absent
- **AND IT MUST** set `source == ERROR` when the lookup itself failed — **never `NOT_FOUND`**, because that would assert knowledge the system does not have
- **BUT** `source` must **NOT** be `null` on any path

---

### DIRABS-FN-004 — Raw values, no transformation in the abstraction

`DirectoryPerson` **MUST** carry every field exactly as `adauth` returned it. The abstraction
**MUST NOT** apply case, trim, or any other transformation.

#### Scenario: Login case is preserved for the caller to handle

- **GIVEN** `LDAPUser.getLogin()` returns `"JSmith"`
- **WHEN** `LdapDirectoryService` maps it to `DirectoryPerson.login`
- **THEN** `login` **MUST** equal `"JSmith"`
- **AND** each consumer **MUST** keep its own existing `.toLowerCase()` call
- **BUT** the abstraction must **NOT** lowercase, uppercase, or trim `login` or `email`
- **AND IT MUST** be true that `users.username` receives the same value it receives today

> **Why this is a requirement and not a detail.** Every consumer today writes
> `LDAPUser.getLogin().toLowerCase()`. If the abstraction lowercases *and* the consumer keeps its call,
> the result is unchanged (harmless). If the abstraction lowercases *and* a migration removes the
> consumer's call as "now redundant", the result is still unchanged — **until a future implementation
> stops lowercasing**, at which point mixed-case usernames reach a `UNIQUE` column
> (`Users.hbm.xml:19`) and are forwarded to CLARISA `partner-requests` and the QA token service
> (analysis `R5`, `R17`). Keeping the transformation at the call site keeps the blast radius where it
> is today. `EXEC-032`'s STOP rule states the same constraint.

---

### DIRABS-FN-005 — LDAP implementation equivalence

`LdapDirectoryService` **MUST** reproduce the observable behavior of `BaseAction.getOutlookUser`
(`:4802-4816`) exactly.

#### Scenario: Found person

- **GIVEN** an email that `adauth` resolves to an `LDAPUser`
- **WHEN** `findByEmail` is invoked
- **THEN** `found` **MUST** be `true`
- **AND** `firstName`, `lastName`, `login`, `email` **MUST** equal `LDAPUser.getFirstName()`,
  `getLastName()`, `getLogin()`, `getEmail()` respectively, untransformed
- **AND** `setInternalConnection(!config.isProduction())` **MUST** be applied before the search,
  matching today's `if (config.isProduction()) { false } else { true }`
- **BUT** it must **NOT** cache, retry, or pool the connection — today's code does none of those
- **AND IT MUST** be the only file in `security/directory/**` that imports `org.cgiar.ciat`

#### Scenario: Not found

- **GIVEN** an email `adauth` does not resolve
- **WHEN** `findByEmail` is invoked
- **THEN** `found` **MUST** be `false` and `email` **MUST** echo the requested email
- **AND IT MUST** leave `login`, `firstName`, `lastName` null rather than empty strings — a caller
  distinguishing null from `""` today must keep distinguishing them

---

### DIRABS-FN-006 — Consumer behavior preserved, per consumer

Each migrated consumer **MUST** preserve its **observable behavior for the same inputs** — the same
field assignments, the same JSON keys and values, the same messages, the same propagation. Internally,
a genuine `NOT_FOUND` becomes distinguishable from a backend `ERROR`; that distinction is invisible to
the five consumers that read only `found`, and is the point for the sixth.

#### Scenario: `CrpUsersAction` (`:630-657`)

- **GIVEN** a guest user is created with a CGIAR email that resolves
- **WHEN** the action runs
- **THEN** `newUser` **MUST** receive `firstName`, `lastName`, `username` (lowercased at the call site),
  and `setCgiarUser(true)`, exactly as today
- **AND** `isCGIARUser` **MUST** be set `true`
- **BUT** the non-resolving branch must **NOT** change: it still requires both `firstName` and
  `lastName` from the form, generates a 6-digit numeric password, and sets `setCgiarUser(false)`
- **AND IT MUST** keep the same `saving.saved.guestRole` i18n message on both branches

#### Scenario: `json/global/ManageUsersAction` (`:151-165`)

- **GIVEN** a user is added by email
- **WHEN** the email resolves
- **THEN** `firstName`, `lastName`, `username` (lowercased at the call site) and `setCgiarUser(true)`
  **MUST** be set and `addUser()` called, as today
- **BUT** the non-resolving branch's trim-and-length validation on `firstName`/`lastName` must **NOT**
  change, nor must the `manageUsers.email.notAdded` / `manageUsers.email.validation` messages

#### Scenario: `GuestUsersValidator` (`:36-56`)

- **GIVEN** the validator runs
- **WHEN** it determines whether the user is a CGIAR user
- **THEN** `isCGIARUser` **MUST** derive from `DirectoryPerson.found`, replacing `LDAPUser != null`
- **AND** its duplicate of `getOutlookUser` at `:36-50` — declared `public`, not private — **MUST** be deleted
- **BUT** the call to `validateGuestUsers(...)` and the field-error handling below it must **NOT** change
- **AND IT MUST** keep receiving `config` through the inherited `@Inject protected APConfig config`
  field on `BaseValidator:52-53` — adding an `@Inject` constructor must not break field injection

#### Scenario: `SearchUserAction` (`:191-223`)

- **GIVEN** a search for an `@cgiar.org` email that resolves
- **WHEN** the JSON response is built
- **THEN** `userFound` **MUST** contain `newUser=true`, `id=-1`, `name`, `lastName`,
  `username` (lowercased at the call site), `email` (lowercased at the call site), `cgiar=true`,
  `active=false`, `autosave=false` — same keys, same values. *(Corrected 2026-08-28: the original clause also required "same order of insertion". `userFound` is a `HashMap` (`SearchUserAction:75`), which does not preserve insertion order; its iteration order is a function of the key set alone, so inserting the same nine keys already yields the same order. The order clause was subsumed by the key clause and is dropped. See Decision Log.)*
- **AND** the lookup **MUST** still be given the lowercased email, as `:202` does today
- **BUT** the not-found branch must **NOT** change: `newUser=false`, `cgiar=false`, `cgiarNoExist=true`
- **AND IT MUST** preserve the `APConstants.OUTLOOK_EMAIL` suffix guard at `:191` — the lookup is
  reached only for `@cgiar.org` emails

#### Scenario: `center/json/global/ManageUsersAction` (`:248-263`)

- **GIVEN** `validateOutlookUser(email)` is invoked
- **WHEN** the email resolves
- **THEN** it **MUST** set `firstName`, `lastName`, `username` (lowercased) on the `newUser` **field**
  and return it — the side effect on the field is part of the contract
- **AND** it **MUST** return `null` when not found
- **BUT** the class must **NOT** be deleted — `OQ-12` (convention-plugin exposure) is unresolved and
  deletion belongs to child 3
- **AND IT MUST** throw `DirectoryLookupException` when `source == ERROR`, preserving the Struts-level outcome it has today — same `java.lang.Exception` mapping, same 500 page, different subtype (`design.md` DD-3a). It is the only consumer that reads `source`

---

### DIRABS-FN-007 — `BaseAction.getOutlookUser` is removed, not rewired

`BaseAction` **MUST NOT** gain a `DirectoryService` dependency. `getOutlookUser` **MUST** be deleted
along with its two `org.cgiar.ciat` imports at `:103-104`.

#### Scenario: BaseAction shrinks and takes no new dependency

- **GIVEN** `BaseAction` is 9,753 lines with a very wide subclass set
- **WHEN** this spec completes
- **THEN** `getOutlookUser` and both `org.cgiar.ciat` imports **MUST** be gone
- **AND** `BaseAction`'s constructors `BaseAction()` (`:613`) and `BaseAction(APConfig)` (`:620`)
  **MUST** be unchanged, so no subclass `super(config)` call is affected
- **BUT** `BaseAction` must **NOT** declare, inject, or reference `DirectoryService` in any form
- **AND IT MUST** be verified that no FreeMarker template, JavaScript file, or Struts XML references
  `getOutlookUser` — confirmed 2026-08-27: the only references are `CrpUsersAction:630` and
  `json/global/ManageUsersAction:151`, both Java

---

### DIRABS-FN-008 — `searchContact.do` stops constructing AD objects, output unchanged

#### Scenario: The endpoint is behaviorally identical

- **GIVEN** a request to `searchContact` (`struts-json.xml:1041`) with a query parameter
- **WHEN** `searchADUser()` runs
- **THEN** the returned `users` list **MUST** have the same map keys and values as today, sourced from
  `adUsermanager.searchUsers(queryParameter)` at `:99`
- **AND** no `LDAPService` and no `ADConexion` **MUST** be constructed
- **AND** `:86`, `:88-91`, `:93` and the two `org.cgiar.ciat` imports at `:24-25` **MUST** be deleted
- **BUT** `:99` and every line below it must **NOT** appear in the diff
- **AND** `getADFilter` at `:58-71` **MUST** remain — it is deleted in child 3, and it needs no
  `adauth` import because it returns a `String`
- **AND IT MUST** remain true that the four `APConstants.*_AD` constants still exist; only their reads
  in this method are removed

---

### DIRABS-FN-009 — `searchUsersUtil` is deliberately left alone

#### Scenario: The exception is explicit, not an oversight

- **GIVEN** `utils/searchUsersUtil.java` imports `LDAPService` and `LDAPUser` at `:3-4`
- **WHEN** `EXEC-040`'s verification grep runs
- **THEN** this file **MUST** be the **only** remaining `org.cgiar.ciat` importer in `marlo-web/src/main` **once `DIRABS-T14` has removed `ContactPersonAction`'s imports** *(scoping corrected 2026-08-28: the original clause said `marlo-web/src` unqualified, which (a) sweeps in `src/test`, where `LdapDirectoryServiceTest` imports `adauth` by design under **DD-12** to stub `LDAPService`, and (b) describes a post-T14 end state that `DIRABS-T11` was checking before T14 runs. See Decision Log.)*
- **AND** it **MUST NOT** be migrated: it is a `main()` method with no caller, and it reads
  `LDAPUser.getAttributes().get("userAccountControl")` at `:25` — an attribute map `DirectoryPerson`
  deliberately does not carry
- **BUT** it must **NOT** be deleted here — deletion is child 3, task `EXEC-101`+
- **AND IT MUST** be named in the verification's expected output, so its presence reads as a decision
  rather than a missed consumer

---

## 7. Non-Functional Requirements

| ID | Requirement | Verification |
|---|---|---|
| **DIRABS-NF-001** | **Zero behavior change.** No user-visible or API-visible difference for any input | Per-consumer scenario tests (§6) + full `git diff` review per task |
| **DIRABS-NF-002** | After completion, an **import-scoped** grep **MUST** return exactly these files. `marlo-web/src/main`: **1** — `utils/searchUsersUtil.java`. `marlo-web/src/test`: **1** — `security/directory/LdapDirectoryServiceTest.java` (permitted by **DD-12**, which stubs `LDAPService`). *(Scoping clarified 2026-08-28: "After completion" was always correct — the defect was `DIRABS-T11` applying this end-state list at a pre-T14 checkpoint, and the unqualified `marlo-web/src` sweeping in the test source root.)* `marlo-data/src`: **3** — `security/APCustomRealm.java`, `security/authentication/LDAPAuthenticator.java`, `security/directory/impl/LdapDirectoryService.java` | `grep -rl "^import org.cgiar.ciat.auth" <root> --include="*.java"` -- **`-rl`, not `-rn`: the counts below are FILE counts, and `-rn` emits one line per import, so a literal `-rn` run returns 6 where this says 3 (corrected 2026-08-28)** (`EXEC-040`, **corrected** — see below) |
| **DIRABS-NF-003** | **No new configuration.** No property, no environment variable, no `APConfig` field | `git diff` on `APConfig.java` is empty |
| **DIRABS-NF-004** | **No dependency change.** No `pom.xml` in any module appears in any diff | `git diff --stat -- '**/pom.xml'` is empty |
| **DIRABS-NF-005** | **No schema change.** No Flyway migration is added | `git diff --stat -- 'marlo-web/src/main/resources/database/**'` is empty |
| **DIRABS-NF-006** | Every new `.java` file carries the GPL header from `AGENTS.md`; 2-space indent; ≤120-char lines; braces same line; mandatory blocks | ⚠️ **NOT `mvn -q checkstyle:check`** — UNVERIFIABLE (EB-2), and out of scope to repair since `pom.xml` is protected. Even repaired it would skip **9 of this child's new files** (no `includeTestSourceDirectory`). **Actual evidence:** `awk 'length>120'` per task **plus** the Reviewer reading header and style at the source, recorded task by task in `execution.md`. *(Corrected 2026-08-28.)* |
| **DIRABS-NF-007** | Each consumer migration **MUST** be independently revertible by a single `git revert` | one consumer per commit |
| **DIRABS-NF-008** | English only in code, identifiers and comments; no new user-facing string | Reviewer audit |
| **DIRABS-SEC-001** | **No change to credential handling.** The hardcoded AD service-account constants are neither read nor moved nor deleted by this spec — only `ContactPersonAction`'s four *reads* of them are removed | `git diff` on both `APConstants.java` is empty |
| **DIRABS-OPS-001** | `DirectorySource` on every result makes *"the directory confirmed this"* distinguishable from *"we assumed it"* in logs and support tickets | Design review; asserted by `DIRABS-FN-003` |
| **DIRABS-ARCH-001** | After completion, swapping the Capability B provider **MUST** require changing one `@Named` bean and one configuration value — no consumer edit | Design review + `/akili-validate` |

---

## 8. Accepted Deviations
**One deviation remains (DEV-2). DEV-1 was withdrawn during design** — it is kept below, struck
through, because a withdrawn deviation is more useful than a deleted one: it records why the obvious
answer was wrong.

### ~~DEV-1~~ — **WITHDRAWN 2026-08-27.** `center/…/ManageUsersAction` keeps its exception propagation

**What DEV-1 said.** Today `:255` reads `LDAPUser user = service.searchUserByEmail(email);` with **no
`try/catch`**, so an `adauth` exception propagates out of `validateOutlookUser` and out of the action.
A never-throws contract would turn that into `found == false` → `null`. DEV-1 accepted that change,
justified by *"the class is unreachable"* and *"the new behavior is strictly safer."*

**Why it was wrong.** The Step 2.3 reversion challenge (`design.md` §10.0, **C-4**) found two things
the original reasoning had not checked:

| # | Finding |
|---|---|
| 1 | **"Strictly safer" was false.** `create():127-131` maps a `null` return to `manageUsers.email.doesNotExist`. So an **AD outage** would tell an administrator that a real CGIAR employee *does not exist in the Active Directory* — a factual falsehood. An admin acting on it creates the person as **non-CGIAR** with hand-typed names, producing a permanently mis-classified `users` row (`is_cgiar_user = 0`). A Struts 500 is unfriendly; **this is wrong data**, and it is silent |
| 2 | **"Unreachable" was weaker than it looked.** It rested on *"the class appears in no Struts XML"* — true, and verified. But `struts2-convention-plugin` **is on the classpath** (`marlo-web/pom.xml:89`), configured with `struts.convention.action.suffix=Action` and `struts.convention.action.mapAllMatches=true` (`struts.xml:25-28`), with **no `package.locators` or `action.packages` restriction**. The plugin's default locators include `action`, which `org.cgiar.ccafs.marlo.action.center.json.global` matches. Configuration evidence points toward the class being **exposed**. It does not *prove* it — that needs the `OQ-12` runtime probe — but it **inverts the presumption** the deviation was resting on |

**Resolution.** `design.md` **DD-3** adds `DirectorySource.ERROR`. The contract still never throws, but
it now says *why* it failed, so this one consumer propagates on `ERROR` and preserves today's observable
outcome exactly. The other five consumers read only `found` and are untouched.

**Cost of the fix:** one enum constant, one branch in one consumer, one contract-test row.

> **The lesson is worth more than the fix.** Every automated gate would have passed with DEV-1 in
> place — compile, `grep`, and a green test suite (**and Checkstyle, in a checkout where it runs — here it is UNVERIFIABLE, EB-2**). A misleading i18n message on an outage
> path is invisible to all four. It was caught because a reversion got one question asked of it.

### DEV-2 — `BaseAction.getOutlookUser` is deleted rather than rewired to `DirectoryService`

**Execution plan `EXEC-034` says:** *"`getOutlookUser(String) → LDAPUser` becomes
`findCorporateUser(String) → DirectoryPerson`, delegating to the injected `DirectoryService`."*

**This spec instead deletes the method** and injects `DirectoryService` into the two callers directly.
Rationale, the challenge outcome, and the rejected alternative are in `design.md` **DD-2**; the
requirement is `DIRABS-FN-007`. Recorded here because it is a documented deviation from an approved
runbook, not an implementation detail.

---

## 9. Defect Classes and Their Gates

**A gate blind to the defect class this spec most often produces is not a gate.** This spec's dominant
defect is a **silently non-equivalent mapping** — and compile, Checkstyle and `grep` are all blind to it.

| # | Defect class this spec can produce | Caught by | Falsifying input — what would make the check FAIL |
|---|---|---|---|
| **D1** | **Non-equivalent field mapping** — a value transformed, dropped, or defaulted differently | ❌ **No existing gate.** Compile passes, `grep` passes — **and Checkstyle would pass in a checkout where it runs; here it is UNVERIFIABLE (EB-2), see `D6` five rows down** | New per-consumer tests asserting the **exact** assigned value. FAIL input: a fake returning `login="JSmith"` where the consumer writes `"jsmith"` — assert the written value, not that a value was written |
| **D2** | **Null-vs-empty-string change** — `notFound` returning `""` where `null` was returned | ❌ No existing gate | Contract test asserting `assertNull(person.getLogin())` on a not-found result. FAIL input: an implementation using `""` defaults |
| **D3** | **Exception-semantics change** — a lookup failure reported as "not found" | ❌ No pre-existing gate. **DD-3 turns this from an accepted change into a preserved equivalence**, and the new tests are its only gate | Two tests. (a) Contract: a stub that throws MUST yield `found == false` **and `source == ERROR`** — FAIL input: an implementation returning `NOT_FOUND`. (b) Consumer: `center/…/ManageUsersAction` MUST propagate on `ERROR` — FAIL input: a version that returns `null`, which would surface as `manageUsers.email.doesNotExist` |
| **D4** | **Missed consumer** — an unmigrated `adauth` site | ✅ `grep -rl "^import org.cgiar.ciat.auth" <root> --include="*.java"` -- **`-rl`, not `-rn`: the counts below are FILE counts, and `-rn` emits one line per import, so a literal `-rn` run returns 6 where this says 3 (corrected 2026-08-28)** against the exact list in `DIRABS-NF-002` | FAIL input: any file in the output that is not on that list. **This gate is genuinely falsifiable — but only in its corrected form**, see the box below |
| **D5** | **Scope violation** — a protected file edited | ✅ `git diff --stat` reviewed per task against §4's protected list | FAIL input: `pom.xml` or `APConstants.java` appearing in the stat |
| **D6** | **Style / GPL header** on new files | ⚠️ **PARTIAL — `mvn -q checkstyle:check` is UNVERIFIABLE (EB-2) and `pom.xml` is protected, so this class has no automated gate.** Substitute: `awk 'length>120'` per task **plus** the Reviewer reading header and style at the source | FAIL input: a new `.java` file without the header, or a 121-char line — **detected by the Reviewer's read, not by a tool.** *(Re-marked 2026-08-28: this row carried a **✅** asserting a working gate for a plugin that cannot execute, on files it would not read even if repaired — §9's opening sentence is "a gate blind to the defect class this spec most often produces is not a gate", and a ✅ here was exactly that.)* |
| **D7** | **`searchContact.do` output change** | ⚠️ Partial — a new `ContactPersonActionTest` with a stubbed `AdUserManager` | FAIL input: a stub returning 2 `AdUser` rows where the test asserts 2 maps with matching keys. Does **not** cover the real `ad_user` query |
| **D8** | **Spring wiring failure** — bean not found, or ambiguous once a second implementation exists | ❌ **No automated gate. This is the spec's largest blind spot** (see below) | — |


> **The `D4` gate was broken in THREE ways. Judgment Day round 1 found the first two; the third was found at `DIRABS-T11` on 2026-08-28 (a scoping-and-timing defect — see §13)** (`judgment.md` JD-1,
> confirmed by both judges). Recorded here because *"a gate that passes while the artifact is wrong"* is
> the exact failure this section exists to prevent — and the original gate failed in the opposite,
> equally useless direction: **it reported failure on a correct implementation.**
>
> | Defect | Original | Corrected |
> |---|---|---|
> | **Missing file** — `security/APCustomRealm.java` imports `org.cgiar.ciat.auth.LDAPService` and `.LDAPUser` at `:28-29` and calls `new LDAPService()` at `:287` (Capability A, untouched by this spec) | `marlo-data` expected **2** files | `marlo-data` expects **3** |
> | **Unscoped pattern** — `ocs/ws/client/WSMarlo.java` contains `org.cgiar.ciat.abw.control.logic` in JAX-WS annotation **string literals**, with no import at all | `grep "org.cgiar.ciat"` matched it, so `marlo-web` returned **9** files today, not the 8 asserted | `grep "^import org.cgiar.ciat.auth"` matches imports only |
> | **Scope-and-timing** *(third defect, found at `DIRABS-T11` 2026-08-28)* — the expected list was scoped to `marlo-web/src`, which sweeps in `src/test` where `LdapDirectoryServiceTest` imports `adauth` **by design** under **DD-12**; and it asserted the **post-T14 end state at a pre-T14 checkpoint**, since `T14` removes `ContactPersonAction`'s imports and runs *after* `T11` | `marlo-web/src` → **1**, checked at `T11` | Three roots checked separately. At `T11`: `src/main` **2**, `src/test` **1**, `marlo-data` **3**. The **`=1` end state was relocated to `T16`, not removed**, and `T16` gained an import-gate re-run it never had — so net gate strength **increased** |
>
> **The missing-file defect is inherited from the approved runbook, not introduced here.** `EXEC-040`'s
> own expected output for `marlo-data/src` lists only `LDAPAuthenticator` and `LdapDirectoryService`,
> omitting `APCustomRealm` exactly as this spec originally did. **`tasks.md` MUST carry the corrected
> pattern and the corrected lists**, and the `EXEC-040` defect is flagged for the execution plan at
> archive time.
### D8 — the unmeasurable class, and its substitute

**MARLO has no Spring context test.** There are 3 test files repository-wide, none of which starts a
context (`docs/trd/trd.md` §10, scenario `TS-2`). A missing or ambiguous `@Named` bean therefore
**compiles and passes `mvn test`** (Checkstyle would not catch it either, and is **UNVERIFIABLE** here — EB-2) — and fails at Tomcat startup, which nothing in
CI exercises (`Dockerfile` builds with `-Dmaven.test.skip=true`).

`@ComponentScan(basePackages = "org.cgiar.ccafs.marlo")` (`MarloDatabaseConfiguration:45`) means a
single `@Named` implementation resolves by type unambiguously, which makes this risk **low for this
spec** — there is exactly one implementation. It becomes **material in child 3**, when a second one
appears.

**Substitute gate — mandatory, manual, at the HITL pause:** start the application once
(`scripts/run-marlo-java17.sh`) and confirm it reaches a served page. This is the only available
evidence that the context wires. It is recorded here as a required manual check rather than left
implicit, and it is **not** delegable to an automated command that does not exist.

**Accepted risk, stated plainly:** closing D8 properly needs a Spring context smoke test, which is
`TS-2`/`TS-1` work in `docs/trd/trd.md` §14.9 item 8 and is **not in this spec's scope**. Adding one
would require a test-scoped dependency decision (`DEC-005`, `PENDING`) and would break this child's
parallel-safety with `auth-flow` by touching `marlo-parent/pom.xml`.

### What the `grep` gates cannot prove

`grep -rl "^import org.cgiar.ciat.auth" <root>` returning the expected file list is a **presence assertion**. *(Corrected 2026-08-28: this read `grep -rn "org.cgiar.ciat"` — **the unscoped pattern, with `-rn`**, forty-eight lines after `D4` corrected both defects. §9 was presenting the corrected gate and the known-broken one three subsections apart.)* It proves the
import is gone. It does **not** prove the replacement behaves the same — that is `D1`, `D2`, `D3`, and
those need the value-asserting tests above. A task whose only verification is the grep has verified
**isolation, not equivalence**, and must say so.

---

## 10. Constitutional Compliance Checklist

- [x] **Phase replication:** Not applicable — this spec touches no `ManagerImpl` save/delete chain and
      no phased entity. `AdUserManager.searchUsers` is read-only.
- [x] **Save validation:** Not applicable — no `Action.validate()` / `Validator` / manager save chain
      is touched. `GuestUsersValidator` is modified, but its validation *logic* is unchanged; only how
      it obtains a corporate person changes.
- [x] **Permissions:** No new action, no new route, no interceptor-stack change. `struts-json.xml` and
      `struts-home.xml` are both protected.
- [x] **Specificity:** Not applicable — no feature flag. `directory.source` belongs to child 3.
- [x] **Migrations:** Not applicable — `DIRABS-NF-005`, no schema change.
- [x] **i18n:** No new or changed user-facing string. `DIRABS-NF-008`.
- [x] **License header:** `DIRABS-NF-006` — GPL header on all **5** new files.
- [x] **Code style:** `DIRABS-NF-006` — ⚠️ `mvn -q checkstyle:check` is a hard gate **in the root guides, but UNVERIFIABLE in this checkout (EB-2)** and out of scope to repair (`pom.xml` protected). Carried by `awk 'length>120'` per task plus the Reviewer reading header and style at the source. *(Corrected 2026-08-28.)*
- [x] **REST:** Not applicable — no `/api/*` endpoint. `SearchUserAction` and both `ManageUsersAction`
      classes are Struts JSON actions, and no new `*.json` path is introduced.
- [x] **Audit:** Not applicable — no auditable entity write is added. Existing `userManager.saveUser`
      calls are unchanged.
- [x] **Dependency floors:** `DIRABS-NF-004` — no `pom.xml` touched, so no floor can move.
- [x] **Branching:** Working branch `staging-cognito-impl`; merge target `staging`. `main` receives
      merges only.

---

## 11. Requirement ID Index

| ID | Title | Tasks (filled by `tasks.md`) |
|---|---|---|
| `DIRABS-FN-001` | A single lookup seam | — |
| `DIRABS-FN-002` | The seam never throws | — |
| `DIRABS-FN-003` | `source` is always populated | — |
| `DIRABS-FN-004` | Raw values, no transformation | — |
| `DIRABS-FN-005` | LDAP implementation equivalence | — |
| `DIRABS-FN-006` | Consumer behavior preserved (5 scenarios) | — |
| `DIRABS-FN-007` | `getOutlookUser` removed, not rewired | — |
| `DIRABS-FN-008` | `searchContact.do` output unchanged | — |
| `DIRABS-FN-009` | `searchUsersUtil` left alone | — |
| `DIRABS-NF-001` … `NF-008` | Non-functional set | — |
| `DIRABS-SEC-001` | No credential-handling change | — |
| `DIRABS-OPS-001` | Source attribution | — |
| `DIRABS-ARCH-001` | Provider swap is one bean + one value | — |

---

## 12. Open Questions

| # | Question | Blocks | Owner |
|---|---|---|---|
| **DIRABS-OQ-1** | Does `DirectoryPerson` need an `attributes` map to eventually absorb `searchUsersUtil`'s `userAccountControl` read, or is that read abandoned with the file in child 3? | Nothing here — `DIRABS-FN-009` defers it | Tech lead |
| ~~**DIRABS-OQ-2**~~ | ~~Is DEV-1 accepted?~~ → **CLOSED 2026-08-27: moot.** `design.md` DD-3 preserves the propagation, so there is no deviation to accept | — | — |
| ~~**DIRABS-OQ-3**~~ | ~~Should `DirectorySource` gain an `ERROR` value?~~ → **CLOSED 2026-08-27: yes, in this spec.** `design.md` DD-3. Equivalence turned out to *require* it, not merely permit it. Analysis risk `R7` is partially pre-answered; child 3 inherits the distinction for free | — | — |
| ~~**DIRABS-OQ-4**~~ | ~~Is **DEV-2** (delete `getOutlookUser` rather than rewire it, deviating from `EXEC-034`) approved?~~ → **CLOSED 2026-08-28: approved.** Closed by this document's approval, exactly as the note below this table specifies. `EXEC-034` is superseded by DD-2 for this spec; do not "restore" the rewire | — | — |
| ~~**DIRABS-OQ-5**~~ | ~~`LDAPUser.getLogin()` returning null on a *found* person would NPE at every consumer's `.toLowerCase()` **today**. Equivalence requires preserving that NPE.~~ → **CONFIRMED 2026-08-28: the NPE is preserved, not "fixed."** The conservative default the requirements already state is the approved behavior. A consumer migration that adds a null guard at `.toLowerCase()` is a **defect**, not a hardening | — | — |

**Every question that gates this spec is closed as of 2026-08-28.** `-OQ-4` closed at this document's
approval; `-OQ-5` confirmed in the conservative direction (the NPE is preserved). `-OQ-2` and `-OQ-3`
were already closed. **`-OQ-1` remains open and is a child-3 question** — `DIRABS-FN-009` defers it,
and nothing in this spec depends on its answer.

---

## 13. Decision Log

| Date | Decision | Rationale |
|---|---|---|
| 2026-08-27 | Depth **Standard**, not Full | Cross-cutting but zero-behavior-change, and the risk register, rollback and STOP conditions already exist in the execution plan. Full depth would duplicate an authored artifact |
| 2026-08-27 | Corrected the proposal's consumer set | Reading the code showed the six `new LDAPService()` sites and the six migrated consumers are **different sets**. `ContactPersonAction` and `searchUsersUtil` are `LDAPService` sites that are not migrated; `CrpUsersAction` and `json/global/ManageUsersAction` are migrated consumers with no `LDAPService` call of their own |
| 2026-08-27 | `DirectoryPerson` carries **raw** values; consumers keep their `.toLowerCase()` | Moving the transformation into the abstraction would work today and break silently the first time an implementation stops applying it, sending mixed-case usernames into a `UNIQUE` column and on to CLARISA and the QA service (`R5`, `R17`) |
| 2026-08-27 | ~~Backend failure collapses into `found == false`~~ → **REVERSED same day by `design.md` DD-3** | The original reasoning was that `getOutlookUser` collapses them, so equivalence requires it. That was true for five of six consumers and **false for the sixth**, which has no `try/catch`. See the next row |
| 2026-08-27 | **`DirectorySource.ERROR` added; DEV-1 withdrawn** (`design.md` DD-3, challenge C-4) | Collapsing a lookup failure into "not found" makes `center/…/ManageUsersAction.create()` report `manageUsers.email.doesNotExist` during an AD outage — telling an admin a real employee does not exist, and inviting a mis-classified `users` row. The "it's unreachable anyway" justification also weakened once the convention plugin's configuration was read. One enum constant restores true equivalence |
| 2026-08-27 | **DEV-2** — delete `getOutlookUser` instead of rewiring it | See `design.md` DD-2. Verified there are exactly 2 callers and no FTL/JS/XML reference, so deletion is bounded — and it avoids giving MARLO's widest shared file a new dependency |
| 2026-08-27 | **D8 (Spring wiring) recorded as an accepted risk with a manual substitute** | MARLO has no Spring context test and adding one requires `DEC-005` and a `marlo-parent/pom.xml` edit, which would break this child's parallel-safety with `auth-flow`. A one-time app-start check at the HITL pause is the honest substitute |
| 2026-08-27 | **Judgment Day round 1 — 5 findings confirmed by both judges, applied; 4 single-judge findings applied on recommendation. No scoped re-judgment** (user chose *Fix only*) | Ledger: [`judgment.md`](./judgment.md). Neither judge challenged the architecture — DD-1, DD-2, DD-3, DD-5, DD-6, DD-9 and DD-10 all survived, with both judges independently confirming DD-2's and DD-3's premises against the code. The defects were bookkeeping plus one unspecified mechanism |
| 2026-08-27 | **`DirectoryLookupException` added (DD-3a)** — closes JD-7 | DD-3 said the consumer "propagates" without naming what. It must be unchecked (`validateOutlookUser:248` has no `throws` clause), must not extend `AuthorizationException` (`struts.xml:543-545` maps that to 403 instead of 500), and makes the branch assertable by type in JUnit 4. Replaces the unearned claim "preserving today's outcome exactly" with the verified boundary: same handling, different subtype |
| 2026-08-28 | **`SC-4` and `SC-9` annotated — both named `mvn checkstyle:check`, which cannot run here** | Found by applying instance 6's lesson: **reading every success criterion as prose instead of grepping for a pattern.** `SC-4` required checkstyle to *pass* and `SC-9` named it as its verification, but the gate is environmentally unrunnable (**EB-2**) and `pom.xml` is protected, so the spec is **forbidden to fix it**. `SC-9` was doubly wrong: the **`maven-checkstyle-plugin`** sets no `includeTestSourceDirectory` (a **plugin** parameter -- the ruleset XML could never carry it), so **test sources are outside checkstyle's scope entirely** — and **9 of this child's new `.java` files are tests**, i.e. the tool was cited to check exactly the files it would never read. Both are DoD-gated, so without these notes a closer would record failures for a defect this spec cannot repair. `SC-9`'s actual evidence — `awk 'length>120'` per task plus the Reviewer reading header and style at the source — is now named, and its original *"Reviewer audit item 6"* half was always sound |
| 2026-08-28 | **`SC-11` re-formulated — it was unsatisfiable against the approved design, with a *destructive* remediation path** | `SC-11` asserted that `security/directory/` *"contains no reference to Cognito, CLARISA, Microsoft Graph, `ad_user`"*, verified by a bare package `grep`. **`DirectorySource.java` names all four by mandate** (`design.md` §4.2, `EXEC-030`, the 8-value enum), so the criterion **reported failure on correct code** — and, being DoD-gated via `tasks.md` §10, it was load-bearing. **It is worse than `SC-1`'s equivalent defect:** `SC-1` failing produced a false alarm, whereas the obvious way to make old `SC-11` pass is to **delete `CLARISA`, `COGNITO_CLAIMS`, `AD_MIRROR` and `DIRECTORY_API` from an 8-value enum child 3 inherits** — `tasks.md` T11's *"reviewer who fixes the expectation to match"* hazard aimed at **production code** instead of a comment. Re-formulated to distinguish a **reserved vocabulary value** from a **provider-specific dependency**, which is the property the seam actually needs: no provider import, dependency or logic outside `impl/LdapDirectoryService`. `DIRABS-ARCH-001` states the same intent soundly at requirement level and needed no change. **This was the sixth instance of one Leader failure mode in this run** — *correcting a definition while missing the documents that quote, index or gate it* — and the first that **no pattern-based grep could reach**, because `SC-11` states the rule in **prose**, naming no path, no count and no literal. Also corrected alongside it: the *"Two `adauth` importers remain"* block in `proposal.md`, which said two, listed two, added a third in prose, and **omitted `APCustomRealm` entirely — literally JD-1's own omission, in a document already corrected twice for it** |
| 2026-08-28 | **`DIRABS-T11`'s isolation gate re-timed, not relaxed; `FN-009` and `NF-002` scoped; a post-T14 re-run added at `T16`** | T11's gate **failed against a correct implementation**: it expected **1** `marlo-web/src` importer and found **3**. Both extras were legitimate — `ContactPersonAction` because **T14 deletes its imports and T14 runs *after* T11**, and `LdapDirectoryServiceTest` because **DD-12** authorises it to stub `LDAPService`, plus the grep scoped to `marlo-web/src` and so swept in `src/test`. **`NF-002`'s *"after completion"* wording was always correct** — the defect was T11 applying an end-state list at a mid-spec checkpoint. **Critically, the `=1` expectation was preserved and relocated to `T16`, not removed**, and `T16` gained an import-gate re-run it never had: nothing previously verified `ContactPersonAction`'s imports were gone, only its *construction sites*, and a file can drop the construction while keeping the import. Net gate strength therefore **increased** — a relaxation compensated by a strictly stronger downstream check is a **re-timing, not a weakening**, which is the line T11's own disqualifier draws. Also recorded: this spec created a **second** false positive for the unscoped pattern — `DirectoryServiceContractTest.java:31`, whose Javadoc asserts it never imports `org.cgiar.ciat` and is caught for saying so — so **the unscoped pattern gets monotonically worse as the spec documents the isolation it achieves** |
| 2026-08-28 | **`FN-006` *SearchUserAction*: the "same order of insertion" clause dropped, and its falsifying input corrected** | The clause required asserting the nine `userFound` keys in insertion order, and named "a `LinkedHashMap` replaced by a `HashMap`" as the falsifying mutation. **Both rest on a misreading of the code:** `userFound` is already a `HashMap` (`SearchUserAction:60,75`), so the named mutation is impossible, and a `HashMap` does not preserve insertion order at all. Because a `HashMap`'s iteration order is a function of the key set alone, inserting the same nine keys already yields the same order -- the order clause was **subsumed** by the key clause and could add nothing falsifiable about the defect that matters (a lost, renamed or mis-valued key). Rejected alternatives: keep an order assertion as a guard against a future map-type swap (falsifiable but fragile -- `HashMap` order can change between JVM releases, breaking the test with no MARLO change); switch the map to `LinkedHashMap` to make the clause true (would change the JSON key order `searchUsers.do` returns today, violating `NF-001` equivalence -- the spec's own acceptance criterion -- to satisfy a mis-written clause) |
| 2026-08-28 | **"Malformed" defined, and the malformed→`NOT_FOUND` mechanism specified — closes a gap surfaced by `T03`'s Implementer** | `FN-002` *Invalid input* asserted the **outcome** (`malformed → NOT_FOUND`) but never defined "malformed" or said how to reach it, so `T03`'s first implementation let a malformed email fall through to `adauth` — which yields `ERROR` if the library throws. That violates *Invalid input* and has a concrete consequence: `T10` discriminates on `ERROR` and throws `DirectoryLookupException`, so an admin's typo would produce a **500 page instead of the "does not exist" message it produces today** — a regression, not merely a gap. Resolved at the HITL gate by **discriminating inside the catch**: the lookup still happens (the no-network-call guarantee is scoped to `null`/blank only, so a malformed email is *expected* to reach the backend), and on a thrown failure well-formedness decides `ERROR` vs `NOT_FOUND`. Confining the predicate to the failure path means a flawed predicate cannot produce a spurious `NOT_FOUND` for a valid address that would have resolved. Rejected alternatives: pre-call validation (deviates from the no-network-call scoping, and an over-strict predicate would reject unusual-but-valid corporate addresses); leave-and-record (`T04` could not then test §5.1 row 2 deterministically, and the `T10` 500 risk would ship uncovered) |
| 2026-08-28 | **Approved for execution; `OQ-4` closed, `OQ-5` confirmed** | The user, acting as Tech lead at the `/akili-execute` approval gate, approved `requirements.md` and `design.md`. `OQ-4` closes by its own terms (*"answered at this document's approval"*). `OQ-5` is confirmed in the conservative direction: the `getLogin()` NPE is **preserved**. Recorded explicitly so no later reviewer reads a preserved NPE as an overlooked bug and "fixes" it |
| 2026-08-28 | **Toolchain finding at `T00`: `JAVA_HOME` pointed at JDK 8** | `mvn -v` reported `1.8.0_202` against `marlo-parent/pom.xml`'s `<release>17</release>` — `DIRABS-T00`'s STOP condition. Resolved per-command with `JAVA_HOME=C:/Program Files/Java/jdk-17` (Maven then reports 17.0.12) rather than by mutating machine configuration. **Every verification command in this spec must export it**, or the compile gate fails for a reason unrelated to the change under review |
| 2026-08-28 | **`tasks.md` §4 gained per-task status markers** | The approved task list shipped with no `[ ]` / `[~]` / `[x]` markers, so `/akili-execute` had nowhere to record progress and no basis for task selection. Adding them is mechanical bookkeeping, not a scope change: no task text was altered |
| 2026-08-27 | **`D4` gate re-scoped to `^import org.cgiar.ciat.auth`; expected lists corrected** — closes JD-1 | The spec's only "genuinely falsifiable" gate reported **failure on a correct implementation**, twice over: it omitted `APCustomRealm` from the `marlo-data` count (2 → 3 files) and its unscoped pattern matched `WSMarlo.java`'s `org.cgiar.ciat.abw` string literals. The first defect is inherited from `EXEC-040` and is flagged for the execution plan at archive time |
