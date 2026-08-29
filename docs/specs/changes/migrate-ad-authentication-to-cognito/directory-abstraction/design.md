# Directory Abstraction — Design

**Spec ID:** `CHG-COGNITO-DIRABS-001`
**Depth:** Standard
**Status:** **Approved** — 2026-08-28, approved by the user acting as Tech lead at the `/akili-execute` gate
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Last Updated:** 2026-08-28
**Implements requirements:** `DIRABS-FN-001` … `FN-009`, `NF-001` … `NF-008`, `SEC-001`, `OPS-001`, `ARCH-001`
**Touches modules:** `marlo-data` (**5** new files), `marlo-web` (7 files modified)
**Pairs with:** [`requirements.md`](./requirements.md) · [`tasks.md`](./tasks.md) · [`../family.md`](../family.md)
**Reversion challenge (Step 2.3):** run **inline**, not delegated — the session operates under a standing instruction not to spawn subagents. Three reversions challenged; **one changed the design.** See §10.0.

---

## 1. Executive Summary

One interface, one implementation, **five** callers migrated onto it, zero behavior change.

`DirectoryService.findByEmail(String) → DirectoryPerson` becomes the only way `marlo-web` obtains a
corporate person's identity. `LdapDirectoryService` sits behind it and delegates to `adauth` verbatim.
`adauth` is untouched — still in three POMs, still on the classpath.

**Two design decisions differ from the source execution plan, and both came from reading the code:**

| | Execution plan says | This design says | Why |
|---|---|---|---|
| **DD-2** | Rewire `BaseAction.getOutlookUser` to delegate to an injected `DirectoryService` (`EXEC-034`) | **Delete the method.** Inject `DirectoryService` into its two callers instead | `getOutlookUser` has exactly 2 callers and zero template references. Deleting it makes MARLO's widest shared file *smaller* and gives it no new dependency |
| **DD-3** | `DirectorySource` has 7 values; a backend failure is indistinguishable from not-found | **Add an 8th value, `ERROR`.** A lookup failure is distinguishable | The reversion challenge proved that collapsing them turns an AD outage into a **false** *"this email does not exist in the Active Directory"* message on an admin endpoint — and that endpoint is more likely reachable than the analysis assumed |

**DD-3 supersedes `DIRABS-FN-002`'s original collapse-everything contract and removes the accepted
deviation DEV-1 entirely.** `requirements.md` is amended accordingly (§10.0, challenge **C-4**).

---

## 2. Architecture Overview

### 2.1 The seam

```
                        marlo-web callers
   CrpUsersAction · ManageUsersAction (json/global) · GuestUsersValidator
   SearchUserAction · ManageUsersAction (center/json/global)
                              │
                              │  DirectoryPerson — never LDAPUser
                              ▼
              ┌──────────────────────────────────────────┐
              │  DirectoryService          (marlo-data)  │
              │  DirectoryPerson findByEmail(String)     │
              │  · never throws                          │
              │  · never returns null                    │
              │  · source always populated               │
              └──────────────────┬───────────────────────┘
                                 │  exactly ONE implementation in this spec
                                 ▼
              ┌──────────────────────────────────────────┐
              │  LdapDirectoryService  @Named            │
              │  the ONLY file permitted to import       │
              │  org.cgiar.ciat  — deleted in child 3    │
              └──────────────────┬───────────────────────┘
                                 ▼
                        adauth 5.7  →  CGIAR AD
```

**Layer placement.** `DirectoryService` lives in `marlo-data`, alongside
`security/authentication/Authenticator`. That is where MARLO already keeps a directory-facing
abstraction with a `@Named` implementation, and it keeps `marlo-web` free of the concept of a
directory *provider* — `marlo-web` only knows there is one. Consistent with `docs/trd/trd.md` §2's
module boundaries: `marlo-data` owns persistence and directory access; `marlo-web` owns actions.

### 2.2 What does **not** change

| Untouched | Why it matters |
|---|---|
| `Authenticator` / `LDAPAuthenticator` / `DBAuthenticator` / `AuthenticationManager` | Capability A. Owned by child 2, protected here |
| `APCustomRealm` | Capability A, including its own `LDAPService` call at `:287` |
| `BaseAction()` and `BaseAction(APConfig)` constructors | **DD-2's whole point** — no subclass `super(config)` call is affected |
| Every `pom.xml` | `DIRABS-NF-004` |
| `struts.xml`, `struts-home.xml`, `struts-json.xml` | No route added or changed |
| Both `APConstants.java` | Only `ContactPersonAction`'s *reads* of four constants go |
| `global.properties` | No new or changed i18n key |

### 2.3 Dependency-injection mechanism — verified, not assumed

| Fact | Evidence |
|---|---|
| Actions and validators are Spring beans; constructor `@Inject` works | `@ComponentScan(basePackages = "org.cgiar.ccafs.marlo")` at `MarloDatabaseConfiguration:45`; `CrpUsersAction:117-120` injects 10 dependencies today |
| Struts uses the Spring object factory, autowiring by name as a fallback | `struts.xml:9-12` — `StrutsSpringObjectFactory`, `autoWire=name` |
| `@Named` on an implementation + injection by interface type resolves | `LDAPAuthenticator:38` is `@Named("LDAP")`, consumed via `@Named("LDAP") Authenticator` in `APCustomRealm:88` |
| A validator can take an `@Inject` constructor without losing `BaseValidator`'s field injection | `BaseValidator:52-53` uses `@Inject protected APConfig config`; `ReportSynthesisSectionValidator:82` adds an `@Inject` constructor on top |

**Consequence:** `LdapDirectoryService` needs **no qualifier value**. With exactly one implementation,
injection by interface type is unambiguous. Child 3 adds qualifiers and the `directory.source` switch
when a second implementation appears (`EXEC-073`).

---

## 3. Extended Directory Structure

```
marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/
├── APCustomRealm.java                          (unchanged — Capability A)
├── authentication/                             (unchanged — Capability A)
│   ├── Authenticator.java
│   ├── LDAPAuthenticator.java
│   ├── DBAuthenticator.java
│   └── AuthenticationManager.java
└── directory/                                  ← NEW package
    ├── DirectoryPerson.java                    ← NEW
    ├── DirectorySource.java                    ← NEW
    ├── DirectoryService.java                   ← NEW
    ├── DirectoryLookupException.java           ← NEW  (unchecked; thrown by consumers, never by the service)
    └── impl/
        └── LdapDirectoryService.java           ← NEW  (only adauth importer here)

marlo-web/src/main/java/org/cgiar/ccafs/marlo/
├── action/
│   ├── BaseAction.java                                       ← getOutlookUser DELETED
│   ├── crp/admin/CrpUsersAction.java                         ← migrated
│   ├── json/global/ManageUsersAction.java                    ← migrated
│   ├── json/global/SearchUserAction.java                     ← migrated
│   ├── center/json/global/ManageUsersAction.java             ← migrated
│   └── center/capdev/ContactPersonAction.java                ← AD construction DELETED
├── validation/superadmin/GuestUsersValidator.java            ← migrated, duplicate deleted
└── utils/searchUsersUtil.java                                ← DELIBERATELY UNTOUCHED

marlo-web/src/test/java/org/cgiar/ccafs/marlo/
├── security/directory/
│   ├── DirectoryServiceContractTest.java       ← NEW (abstract, reusable by child 3)
│   ├── LdapDirectoryServiceTest.java           ← NEW
│   └── FakeDirectoryService.java               ← NEW (hand-rolled test double)
├── action/crp/admin/CrpUsersActionDirectoryTest.java              ← NEW
├── action/json/global/ManageUsersActionDirectoryTest.java         ← NEW
├── action/json/global/SearchUserActionDirectoryTest.java          ← NEW
├── action/center/json/global/CenterManageUsersActionDirectoryTest.java ← NEW
├── action/center/capdev/ContactPersonActionTest.java              ← NEW
└── validation/superadmin/GuestUsersValidatorDirectoryTest.java    ← NEW
```

**Tests live in `marlo-web/src/test/java` even for the `marlo-data` types.** `marlo-data`,
`marlo-core` and `marlo-utils` have **no test source root at all** (`touchpoint-inventory.md` §6).
Creating one is a `pom.xml`-adjacent change this spec has excluded (`DIRABS-NF-004`), and it would
break parallel-safety with `auth-flow`. Recorded as a knowing compromise — see DD-10.

---

## 4. Data Model

**No schema change. No Flyway migration.** (`DIRABS-NF-005`)

### 4.1 `DirectoryPerson` — an Option carrying its own provenance

Immutable value type. All fields final, set once at construction, no setters.

| Field | Type | Contract |
|---|---|---|
| `found` | `boolean` | `true` only when the directory returned a person |
| `email` | `String` | The **requested** email on a not-found result; the **returned** email on a found one. Raw, untransformed |
| `login` | `String` | Raw. `null` when not found — **never `""`** (`DIRABS-FN-005`) |
| `firstName` | `String` | Raw. `null` when not found |
| `lastName` | `String` | Raw. `null` when not found |
| `source` | `DirectorySource` | **Never null** (`DIRABS-FN-003`) |

Two static factories, so no caller can construct an inconsistent instance:

| Factory | Produces |
|---|---|
| `found(email, login, firstName, lastName, source)` | `found = true` |
| `notFound(email, source)` | `found = false`, all name/login fields `null` |

**`notFound` takes a `source`** rather than hardcoding `NOT_FOUND` — that is what makes DD-3 work.

### 4.2 `DirectorySource` — 8 values, not 7

| Value | Meaning | Introduced by |
|---|---|---|
| `LDAP` | The AD bind confirmed this person | this spec |
| `NOT_FOUND` | The directory answered, and the person is not there | this spec |
| **`ERROR`** | **The directory could not be reached or failed. Nothing is known about the person** | **this spec — DD-3** |
| `DIRECTORY_API` | A corporate directory API (candidate 2) | child 3 |
| `CLARISA` | CLARISA (candidate 3) | child 3 |
| `COGNITO_CLAIMS` | Cognito ID-token claims (candidate 1, contested — `OQ-21`) | child 3 |
| `AD_MIRROR` | The local `ad_user` mirror (candidate 5, cache only) | child 3 |
| `INVITATION` | Invitation + JIT provisioning (candidate 6) | child 3 |

**`ERROR` is the only deviation from the analysis's 7-value list**, and it is additive. Rationale in
DD-3.

### 4.3 Field-mapping table — the equivalence contract

This is the table a reviewer checks line by line. **`LdapDirectoryService` applies no transformation.**

| `DirectoryPerson` | ← from | Transformation applied by the abstraction | Who lowercases, and where |
|---|---|---|---|
| `found` | `LDAPUser != null` | none | n/a |
| `email` | `LDAPUser.getEmail()` | **none** | `SearchUserAction:214` keeps its `.toLowerCase()` |
| `login` | `LDAPUser.getLogin()` | **none** | All four writers keep their `.toLowerCase()`: `CrpUsersAction:638`, `json/global/ManageUsersAction:156`, `SearchUserAction:213`, `center/…/ManageUsersAction:259` |
| `firstName` | `LDAPUser.getFirstName()` | none | — |
| `lastName` | `LDAPUser.getLastName()` | none | — |
| `source` | `LDAP` on found, `NOT_FOUND` on a null return, `ERROR` on a caught exception | n/a | n/a |

**Not carried:** `LDAPUser.getAttributes()`. Its only reader is `searchUsersUtil:25`
(`userAccountControl`), which is unreachable and is deleted in child 3 (`DIRABS-FN-009`,
`DIRABS-OQ-1`).

---

## 5. API Design

**No HTTP API changes.** No `/api/*` endpoint, no new Struts action, no route, no interceptor.
`SearchUserAction`, both `ManageUsersAction` classes and `ContactPersonAction` are existing Struts JSON
actions whose **response shapes are unchanged** — that is `DIRABS-FN-006` and `DIRABS-FN-008`.

### 5.1 The internal contract — `DirectoryService.findByEmail`

The single method's behavioral contract, which `DirectoryServiceContractTest` encodes and every future
implementation inherits:

| Input / condition | Returns | `source` |
|---|---|---|
| `null` or blank email | `notFound` | `NOT_FOUND` |
| Malformed email | `notFound` | `NOT_FOUND` — reached by discriminating **inside the catch**, see **DD-11** |
| Well-formed email, person present | `found(...)` | `LDAP` |
| Well-formed email, person absent | `notFound` | `NOT_FOUND` |
| Backend unreachable / timeout / throws | `notFound` | **`ERROR`** |

Invariants, on every path: **never throws · never returns `null` · `source` never `null`.**

**A `null` or blank email makes no network call** (`DIRABS-FN-002`) — fail fast, before the bind.

### 5.2 How the two collapse modes coexist

This is the crux of DD-3, and it is why one enum value buys real equivalence.

| Caller | Reads | Effect of an AD outage | Same as today? |
|---|---|---|---|
| `CrpUsersAction` | `found` only | Treated as "not a CGIAR user" | ✅ identical — `getOutlookUser` caught and returned `null` |
| `json/global/ManageUsersAction` | `found` only | same | ✅ identical |
| `GuestUsersValidator` | `found` only | same | ✅ identical |
| `SearchUserAction` | `found` only | same | ✅ identical — it has its own `try/catch → null` at `:203-205` |
| **`center/json/global/ManageUsersAction`** | **`found` *and* `source == ERROR`** | **Propagates** — via `DirectoryLookupException` | ✅ same Struts handling and same 500 page; **different exception subtype** — see DD-3a |

Five callers never learn that `ERROR` exists. One does, because one needs to.

---

## 6. Backend Module Design

### 6.1 `marlo-data` — the new package

| Type | Shape | Notes |
|---|---|---|
| `DirectorySource` | `enum`, 8 constants | No behavior |
| `DirectoryPerson` | immutable value type, 6 final fields, 2 static factories, `toString` that **must not print** `login`/`email` in full — this is corporate personnel data | `equals`/`hashCode` on all fields, so tests can assert whole objects |
| `DirectoryService` | interface, 1 method | Javadoc **is** the contract (§5.1). Every clause of §5.1 stated explicitly |
| `DirectoryLookupException` | `extends RuntimeException`; carries the requested email + wrapped cause | **Never thrown by the service.** Only by a consumer that read `source == ERROR` and chose not to degrade. MUST NOT extend `AuthorizationException` (DD-3a) |
| `impl/LdapDirectoryService` | `@Named`, `@Inject` constructor taking `APConfig` | Mirrors `LDAPAuthenticator`'s shape (`:38`, `:47-51`) |

`LdapDirectoryService.findByEmail` reproduces `BaseAction.getOutlookUser` (`:4802-4816`) step for step:
construct `LDAPService`, apply `setInternalConnection(!config.isProduction())`, call
`searchUserByEmail(email)`, and on any exception fall through to a not-found result. The **only**
addition is that the exception path records `source = ERROR` and logs at `error` with the email and
cause — today the exception is swallowed silently, which is the *one* thing about the current code this
design deliberately improves, because a swallowed exception with no log is unobservable.

> **This is not a behavior change.** The returned value is what it was; only a log line is added.
> Logging is `SLF4J` via the existing `LoggerFactory` pattern (`LDAPAuthenticator:41`).

### 6.2 `marlo-web` — the seven modified files

| File | Change | Requirement |
|---|---|---|
| `BaseAction.java` | **Delete** `getOutlookUser` (`:4802-4816`) and both `org.cgiar.ciat` imports (`:103-104`). **Nothing added.** | `FN-007` |
| `CrpUsersAction.java` | Add `DirectoryService` to the existing `@Inject` constructor (`:117-120`). Replace `this.getOutlookUser(...)` at `:630` with the injected call. Field assignments at `:636-639` keep `.toLowerCase()`. Delete import `:48` | `FN-006` |
| `json/global/ManageUsersAction.java` | Same pattern at `:151-156`. Delete import `:24` | `FN-006` |
| `GuestUsersValidator.java` | **Delete** its duplicate of the helper (`:36-50`, declared `public` — see C-3). Add an `@Inject` constructor taking `DirectoryService`. At `:55-56`, `found` replaces `LDAPUser != null`. Delete imports `:23-24` | `FN-006` |
| `json/global/SearchUserAction.java` | Add `DirectoryService` to the `@Inject` constructor. Replace `:193-205` with one call, **still passing the lowercased email** as `:202` does. Map onto the same 9 `userFound` keys. Delete imports `:30-31` | `FN-006` |
| `center/json/global/ManageUsersAction.java` | Add `DirectoryService` to the `@Inject` constructor. Rewrite `validateOutlookUser` (`:248-263`) to use the seam **and to propagate on `source == ERROR`** (DD-3). Preserve the side effect on the `newUser` field and the `null` return. Delete imports `:24-25` | `FN-006`, DD-3 |
| `center/capdev/ContactPersonAction.java` | **Delete** `:86`, `:88-91`, `:93` and imports `:24-25`. Nothing else. `:99` and below untouched; `getADFilter` (`:58-71`) untouched | `FN-008` |

### 6.3 Testing design

MARLO has **no mocking framework** (`DEC-005` `PENDING`) and this spec deliberately does not request
one — taking it would edit `marlo-parent/pom.xml` and break parallel-safety with `auth-flow`
(`../family.md` § *Parallel-safety*). Hand-rolled doubles are sufficient here, and cheap, because
`DirectoryService` has exactly one method.

| Test | Shape | Catches |
|---|---|---|
| `FakeDirectoryService` | A hand-written implementation with settable canned responses **and a call recorder** (email received, invocation count) | Enables every assertion below |
| `DirectoryServiceContractTest` | **Abstract**, with **five abstract seams** (`createServiceWithNoMatch`, `createServiceWithFoundPerson`, `foundSource`, `createFailingService`, `failingServiceInvocationCount`)  -- revised at T04 from "one abstract factory method", which cannot express *no-match*, *found* and *failing* separately. Encodes all five rows of §5.1's table, plus the three invariants and the no-network-call clause | `D2`, `D3`. **Reused verbatim by child 3's provider** — the swap arrives pre-covered |
| `LdapDirectoryServiceTest` | Extends the contract test | `D2`, `D3` for the LDAP implementation |
| 5 × `…DirectoryTest` (one per migrated consumer) | Drive the consumer with a `FakeDirectoryService` returning a person whose `login` is **`"JSmith"`**, and assert the **exact** value written | **`D1`** — the dominant defect class. Asserting *"a value was written"* would not catch it; asserting *`"jsmith"`* does |
| `ContactPersonActionTest` | Stubbed `AdUserManager` returning 2 `AdUser` rows; assert 2 maps with matching keys and values | `D7` (partially — not the real `ad_user` query) |

**Why `login = "JSmith"` specifically.** A fake returning an already-lowercase login makes the test
pass whether or not the consumer lowercases, and whether or not the abstraction does. Mixed case is
the only input that distinguishes the four possible implementations. This is the falsifying input
`requirements.md` §9 `D1` demands.

---

## 7. Frontend / UX Component Architecture

**Not applicable.** No screen, no FTL template, no JavaScript, no CSS, no design token. The 303
templates under `marlo-web/src/main/webapp` contain **zero** references to `getOutlookUser` or any
`adauth` type — verified 2026-08-27 during the reversion challenge (§10.0, C-1).

The one frontend change this programme does require — the provisioning form no longer assuming the
backend fills names (analysis `R4`, `crpUsers.js:274-283`, `crpUsers.ftl:64,67`) — belongs to child 3
and **must ship with the provider**, never before it.

---

## 8. Shared Contracts / Package Extensions

| Contract | Change |
|---|---|
| `docs/trd/trd.md` §2 (domain modules) | `marlo-data` gains a `security/directory` package. A **one-line** addition at archive time; no ADR is superseded |
| `docs/trd/trd.md` §14.5 `MO-2` | Change cost for directory work drops from 6 classes to 1. Worth recording as evidence the scenario's measure is met |
| `reports/ai-context/*` | **None affected.** No routing, save-pipeline, replication, interceptor or composition contract changes |
| `../family.md` child 3 | Inherits `DirectoryService`, `DirectoryPerson`, `DirectorySource` (incl. `ERROR`) and `DirectoryServiceContractTest`. **`DIRABS-OQ-3` is now closed by DD-3** — child 3 no longer needs to add the error signal |

---

## 9. Budget (Step 2.4)

Estimated from the finished design. **`/akili-execute` trips against these** — exceeding one is
information to escalate, not a failure to hide.

| Metric | Expected | Notes |
|---|---|---|
| **Tasks** | **17** + `T00` | 14 implementation + 2 checkpoint reports + **`T12`, the Spring-context smoke check** — added at Phase 3 because `D8` has no automated gate and the runbook has no task for it. Checkpoint 0 (`EXEC-001`…`006`) is collapsed into the single precondition task `T00`, so the original *"plus 6 CP0 tasks"* note is superseded |
| **LOC — production** | **~280 net** | ~215 added in `marlo-data` (5 files, incl. `DirectoryLookupException` ~30); ~65 net in `marlo-web` (deletions offset additions: `BaseAction` −16, `GuestUsersValidator` −15, `ContactPersonAction` −10) |
| **LOC — tests** | **~400** | 9 new test classes. Larger than the production change, deliberately: the dominant defect class has no other gate |
| **LOC — total** | **~700** | Revised from ~650 by Judgment Day JD-7 (`DirectoryLookupException` + its branch test) |
| **Review rounds** | **~20** | 17 first-pass reviews + ~3 rework rounds. Equivalence review is line-by-line, so budget more FAILs than a normal refactor |

**Depth re-check against the design: Standard holds.** 17 tasks and ~700 LOC is squarely
Standard-sized — well above `/akili-quick` or Lite, and it does not need Full, because the risk
register, rollback and STOP conditions live in the execution plan rather than needing derivation here.

**~700 LOC exceeds the ~400 threshold → the PR strategy recommendation belongs in `tasks.md` §3.3.**
Natural boundary: the `marlo-data` seam + contract test (self-contained, mergeable alone, changes
nothing) as PR 1; the six consumer migrations as PR 2; Checkpoint 3 as PR 3.

---

## 10. Design Decisions

### 10.0 Reversion challenges (Step 2.3)

Three decisions remove behavior the codebase already ships. Each was challenged with one question:
**what does removing this break?** Run inline (no subagent, per standing instruction).

#### C-1 — Deleting `BaseAction.getOutlookUser` (DD-2)

| | |
|---|---|
| **What could break** | A caller not found by the initial grep: another Java class, an OGNL expression in a template, a Struts XML result, reflection |
| **Challenge performed** | Repository-wide search for `getOutlookUser`, `outlookUser`, `OutlookUser` across **all** file types (not just `.java`), plus a targeted sweep of all 303 FTL/JSP/VM templates under `marlo-web/src/main/webapp` |
| **Result** | **Exactly 2 external callers**, both Java: `CrpUsersAction:630` and `json/global/ManageUsersAction:151`. **Zero** hits for `getOutlookUser` / `outlookUser` / `OutlookUser` in any template, JavaScript, XML, or properties file. (A case-insensitive search for the bare word `outlook` matches unrelated third-party files — `jquery/AUTHORS.txt` and a PDF under `global/documents/` — neither referencing this code path) |
| **Residual risk** | OGNL can invoke a method with arguments (`action.getOutlookUser('x')`), which a property-name search would miss — but the full-text search covers that form, and it found nothing |
| **Verdict** | ✅ **Deletion is safe and bounded.** Design unchanged |

#### C-2 — Deleting `ContactPersonAction`'s `LDAPService` / `ADConexion` construction (DD-7)

| | |
|---|---|
| **What could break** | (a) A read of `adConection` somewhere below; (b) a side effect of the `ADConexion` constructor that something depends on; (c) `Integer.parseInt(PORT_AD)` throwing today in a way the endpoint relies on |
| **Result (a)** | No read exists. `:95-96` — the only use — is commented out. `:99` onward uses `adUsermanager.searchUsers()` against the `ad_user` **table** |
| **Result (b)** | The constructor performs an AD bind. Its only observable consequences are latency and an entry in AD's own auth log. Nothing in MARLO reads either. **Removing it removes one AD bind per request — which is the point** |
| **Result (c)** | **A real, if inverted, finding.** If `APConstants.PORT_AD` were ever malformed, `Integer.parseInt` at `:93` would throw and `searchContact.do` would 500 **today**. After deletion it would succeed. This is a behavior change in the *"it now works where it used to fail"* direction, on a code path that is dead anyway |
| **Verdict** | ✅ **Deletion is safe.** Design unchanged. Result (c) recorded so a reviewer does not read it as an unnoticed side effect |

#### C-3 — Deleting `GuestUsersValidator`'s private `getOutlookUser` (DD-8)

| | |
|---|---|
| **What could break** | The method is **`public`**, not private — so an external caller or an OGNL expression could reach it |
| **Result** | Its only caller is its own `:55`. No `validator.getOutlookUser` call exists anywhere; `CrpUsersAction:120` injects it as `validator` and never calls that method. Validators are not exposed to views |
| **Verdict** | ✅ **Deletion is safe.** Design unchanged |

#### C-4 — Collapsing "backend failed" into "not found" *(the challenge that changed the design)*

This was not on the original list. It surfaced **while performing C-1**, from a reference the initial
grep had not needed: `center/json/global/ManageUsersAction:127`.

| | |
|---|---|
| **What `requirements.md` originally said** | `DIRABS-FN-002` collapsed every failure into `found == false`, because `getOutlookUser` does. `center/ManageUsersAction`'s loss of exception propagation was accepted as **DEV-1**, justified by "the class is unreachable" |
| **What the challenge asked** | What does removing the exception propagation actually *do* on that endpoint? |
| **Finding 1 — the consequence is a false statement to an admin** | `create()` at `:127-131` reads: `newUser = this.validateOutlookUser(...)`; `if (newUser == null) { message = getText("manageUsers.email.doesNotExist"); return SUCCESS; }`. So an **AD outage** would report *"this email does not exist in the Active Directory"* — factually false. Worse, an admin acting on it would create the person as a **non-CGIAR** user with hand-typed names, producing a permanently mis-classified `users` row (`is_cgiar_user = 0`). A 500 is unfriendly; **this is wrong data** |
| **Finding 2 — "unreachable" was a weaker claim than it looked** | The analysis based it on "the class appears in no Struts XML" — verified true. But `struts2-convention-plugin` **is on the classpath** (`marlo-web/pom.xml:89`) with `struts.convention.action.suffix=Action` and `struts.convention.action.mapAllMatches=true` (`struts.xml:25-28`), and **no `package.locators` or `action.packages` restriction is configured**. The plugin's default locators include `action`, which `org.cgiar.ccafs.marlo.action.center.json.global` matches. **Configuration evidence points toward the class being exposed, not toward it being dead.** This does not *prove* reachability — that still needs the `OQ-12` runtime probe — but it **inverts the presumption** |
| **Design change** | Add `DirectorySource.ERROR` (DD-3). `center/ManageUsersAction` propagates on `ERROR` via `DirectoryLookupException`, preserving today's Struts-level outcome (see DD-3a for what is and is not identical). The other five callers read only `found` and are unaffected |
| **Cost** | One enum constant, one branch in one consumer, one contract-test row |
| **Verdict** | 🔴 **Design changed. DEV-1 is withdrawn** — the deviation no longer exists, because equivalence is now preserved. `requirements.md` amended: `FN-002` gains the `ERROR` row, `FN-006`'s center scenario replaces its DEV-1 clause, `DIRABS-OQ-2` is closed, `DIRABS-OQ-3` is closed by DD-3 |

> **This is exactly what Step 2.3 is for.** The alternative path — reaching `tasks.md` with DEV-1
> intact — would have cost an Implementer spawn, a Reviewer spawn and a rework round to learn the same
> thing, *if* anyone noticed. More likely it would have shipped: every gate would have been green,
> because a misleading i18n message on an outage path is not something compile, Checkstyle, `grep` or a
> passing test can see.

---

### DD-1 — `DirectoryService` lives in `marlo-data/security/directory/`

**Problem:** where does a directory-access abstraction belong in a 5-module Maven monolith?

**Decision:** `marlo-data`, in a new `security/directory/` package beside `security/authentication/`.

**Alternatives rejected:**

| Option | Why not |
|---|---|
| `marlo-web` | Puts a directory *provider* concept in the action layer, and `marlo-data`'s `APCustomRealm` would eventually need it too (child 3) |
| `marlo-utils` | It has no Spring context and no `APConfig` access; `LdapDirectoryService` needs both |
| `marlo-core` | Holds configuration and initialization, not domain access |
| A new module | A ROBUST escalation of the structural axis with no scenario demanding it (`docs/trd/trd.md` §13.2) |

**Implications:** `marlo-web` depends on `marlo-data` already, so no POM change (`DIRABS-NF-004`).
Resolves `DIRABS-OQ-1`'s placement half.

---

### DD-2 — Delete `BaseAction.getOutlookUser`; do not rewire it *(reversion — see C-1)*

**Problem:** `EXEC-034` says rewire the method to delegate to an injected `DirectoryService`.
`BaseAction` is 9,753 lines with a very wide subclass set. How does it *get* the service?

**Decision:** **Delete the method.** `CrpUsersAction` and `json/global/ManageUsersAction` each take
`DirectoryService` in their own existing `@Inject` constructor.

**Alternatives rejected:**

| Option | Why not |
|---|---|
| New `BaseAction(APConfig, DirectoryService)` constructor | Every subclass calling `super(config)` must change. Unacceptable ripple for a zero-behavior-change spec |
| `protected DirectoryService` field + setter, autowired by name | Works (`struts.xml:11-12` sets `autoWire=name`), but requires the Spring bean name to equal the property name, which forces a `@Named("directoryService")` qualifier that fights child 3's `directory.source` switch. It also gives MARLO's widest shared file a dependency it does not need |
| `@Inject` on a `BaseAction` field | Whether field injection is processed for Struts-instantiated actions depends on the object-factory path. Unverified, and D8 (no Spring context test) means a wrong guess surfaces only at Tomcat startup |

**Argument:** the method has exactly 2 callers and zero template references (C-1). Both callers already
have `@Inject` constructors — `CrpUsersAction` injects 10 dependencies today. Deleting makes
`BaseAction` **smaller**, takes on **no** new dependency, and leaves both constructors byte-identical.
The cost is +1 constructor parameter in 2 classes.

**Implications:** a documented deviation from an approved runbook — recorded as **DEV-2** in
`requirements.md` §8 and `DIRABS-OQ-4`. The execution plan's `EXEC-034` description should be amended
at archive time, not silently diverged from.

---

### DD-3 — `DirectorySource.ERROR`: a lookup failure is distinguishable *(supersedes DEV-1 — see C-4)*

**Problem:** `getOutlookUser` collapses "not found" and "backend failed" into `null`. Five consumers
depend on that collapse. One consumer (`center/ManageUsersAction`) does **not** collapse it today — it
has no `try/catch`, so a failure propagates. A single never-throws contract cannot serve both.

**Decision:** the contract never throws, **and** carries *why* in `source`. `ERROR` means "nothing is
known about this person." `center/ManageUsersAction` reads it and propagates; the other five read only
`found` and behave exactly as today.

**Alternatives rejected:**

| Option | Why not |
|---|---|
| Collapse everything; accept the change (original DEV-1) | C-4: turns an AD outage into a false *"email does not exist"* and can produce a mis-classified user row. And "the class is unreachable" is weaker than it looked |
| A second, throwing method on the interface | Two methods where one suffices, and it puts failure handling back in the interface's shape rather than in its data |
| Let `LdapDirectoryService` throw for that one caller | Breaks the never-throws invariant the other five rely on, and re-exports an `adauth`-shaped exception into `marlo-web` — defeating the spec |
| Preserve the old behavior by *not* migrating `center/ManageUsersAction` | It would keep importing `LDAPUser`, so `DIRABS-NF-002` fails and `marlo-web` is not isolated |

**Argument:** this is the **Result-type pattern** — the return value carries success *and* the reason
for failure, so each caller handles it at the level where it can act (`error-handling-patterns` →
*Result Type Pattern*, *Handle at Right Level*, *Don't Swallow Errors*). It costs one enum constant.
It buys observable-outcome equivalence on a possibly-live admin endpoint (DD-3a states the exact boundary), and it pre-answers analysis risk `R7`
(*"a directory outage breaks user creation"*) rather than deferring it to child 3.

**Implications:** the enum has 8 values, not the analysis's 7 — additive, no child-3 rework.
`DIRABS-OQ-3` is **closed**. `DEV-1` is **withdrawn**. Child 3 inherits the distinction for free, and
should consider surfacing it as a distinct user-facing message (*"the directory is temporarily
unavailable"*) — a new i18n key, and therefore **out of scope here**, since `global.properties` is
protected in this spec.

---

#### DD-3a — The propagation mechanism: `DirectoryLookupException` *(added by Judgment Day round 1, JD-7)*

DD-3 said `center/…/ManageUsersAction` *"propagates"* on `source == ERROR` and *"preserves today's
observable outcome exactly."* **Judge A was right that this left the mechanism unspecified and the word
"exactly" unearned.** An implementer had no instruction for that branch. Resolved:

**The service never throws; the consumer chooses to.** A fifth new type is added to the directory
package:

| Type | Shape | Who throws it |
|---|---|---|
| `DirectoryLookupException extends RuntimeException` | Carries the requested email and the wrapped cause | **Never `LdapDirectoryService`.** Only a consumer that has read `source == ERROR` and decided not to degrade |

**It must be unchecked, and that is forced by the existing code, not chosen for convenience.**
`private User validateOutlookUser(String email)` (`:248`) declares **no `throws` clause** and wraps
nothing in `try/catch`, so whatever `adauth` raises today is already unchecked. A checked exception
would not compile without changing that signature — which would ripple to `create()` and out.

**It must not extend `org.apache.shiro.authz.AuthorizationException`**, for a specific reason given
below.

**Equivalence, stated precisely instead of as "exactly":**

| Layer | Today | After | Same? |
|---|---|---|---|
| Exception **class** | whatever `adauth` raises | `DirectoryLookupException` | ❌ different — and this is unavoidable once the seam exists |
| Checked / unchecked | unchecked | unchecked | ✅ |
| `validateOutlookUser` signature | no `throws` | no `throws` | ✅ |
| Struts handling | `struts.xml:540-542` maps `java.lang.Exception` → `unhandledException` → chains to `unhandledExceptionAction` → `/WEB-INF/global/pages/error/500.ftl` | `RuntimeException` is an `Exception`, so it takes **the same mapping** | ✅ |
| What the administrator sees | the 500 page | the 500 page | ✅ |
| What the administrator does **not** see | — | `manageUsers.email.doesNotExist` — the false message DD-3 exists to prevent | ✅ the point |

**So the honest claim is: the exception *subtype* differs; the *handling and the observable outcome* do
not.** `design.md` previously overstated this as "exactly"; the wording is corrected.

**Why `AuthorizationException` matters here.** `struts.xml:543-545` maps
`org.apache.shiro.authz.AuthorizationException` to `403` instead. An exception extending it would
render a **403 instead of a 500** — a real, silent behavior change. `DirectoryLookupException` extends
`RuntimeException` directly, and a task-level check must confirm it.

**Verified, not assumed:** `struts.xml:540-546` inspected 2026-08-27 — exactly two global mappings
(`java.lang.Exception` → `unhandledException`, `AuthorizationException` → `403`).

**Test consequence.** JUnit 4's `@Test(expected = DirectoryLookupException.class)` makes this branch
assertable by type. `IllegalStateException` was rejected for exactly that reason: unrelated code could
raise it, so a test asserting it would pass for the wrong reason — a gate blind to its own defect class.

**Budget consequence.** `marlo-data` gains a **5th** new file (~30 LOC with the GPL header). §9's totals
are updated accordingly.

---

### DD-4 — Raw values; every transformation stays at its current call site

**Problem:** all four username writers call `getLogin().toLowerCase()`; `SearchUserAction` also calls
`getEmail().toLowerCase()`. Should the abstraction normalize instead?

**Decision:** **no transformation in the abstraction.** Fields are raw. Every consumer keeps its
existing `.toLowerCase()` exactly where it is.

**Argument:** normalizing centrally works *today* and fails *later*. The failure mode is specific: a
migration removes a consumer's now-"redundant" call, then a future implementation stops normalizing,
and mixed-case values reach `users.username` — a `UNIQUE` column (`Users.hbm.xml:19`) that is
forwarded to CLARISA `partner-requests` and the QA token service (analysis `R5`, `R17`). Keeping the
transformation where it is keeps the blast radius where it is. `EXEC-032`'s STOP rule states the same
constraint independently.

**Implications:** `DIRABS-FN-004`; the `"JSmith"` test input in §6.3 exists to enforce it.

---

### DD-5 — Constructor injection by interface type; no qualifier value yet

**Decision:** `@Named` (no value) on `LdapDirectoryService`; consumers inject `DirectoryService` by
type through their existing `@Inject` constructors.

**Argument:** with exactly one implementation, type resolution is unambiguous. Adding a qualifier now
would need consumers to name a specific provider — the coupling this spec exists to remove. Child 3
adds qualifiers **and** the `directory.source` switch together (`EXEC-073`), which is one coherent
change rather than two half-changes.

**Implications:** `DIRABS-ARCH-001` — the swap is one bean plus one config value. **`D8` risk is real
but minimal here** (one implementation cannot be ambiguous) and becomes material in child 3.

---

### DD-6 — `DirectoryPerson` is immutable and carries no attribute map

**Decision:** all fields final, no setters, two static factories, no `Map<String,Object> attributes`.

**Argument:** immutability makes the object safe to log, cache and pass across layers, and the
factories make an inconsistent instance (`found = true` with a null login, say) unconstructible. The
attribute map has exactly one reader in the entire repository — `searchUsersUtil:25`, unreachable,
deleted in child 3. Carrying an untyped map to serve a dead `main()` would export `adauth`'s attribute
vocabulary into MARLO's own type, which is the coupling being removed.

**`toString` must not print `login` or `email` in full.** This is corporate personnel data and
`DirectoryPerson` will end up in log lines. Truncate or mask.

---

### DD-7 — Delete `ContactPersonAction`'s AD construction; change nothing else *(reversion — C-2)*

**Decision:** delete `:86`, `:88-91`, `:93` and imports `:24-25`. Do **not** touch `:99` or below,
and do **not** delete `getADFilter` (`:58-71`) or the four `APConstants.*_AD` constants.

**Argument:** the deleted lines produce a live AD bind on every `searchContact.do` request whose result
is never read. `getADFilter` returns a `String` and needs no `adauth` import, so it can stay for child
3 to delete alongside the constants — keeping this spec's diff minimal and each deletion reviewable in
the phase that owns it.

---

### DD-8 — Delete `GuestUsersValidator`'s duplicate; add an `@Inject` constructor *(reversion — C-3)*

**Decision:** delete `:36-50`; add an `@Inject` constructor taking `DirectoryService`.

**Argument:** the duplicate is a byte-for-byte copy of `BaseAction.getOutlookUser` with no external
caller (C-3). `BaseValidator:52-53` uses `@Inject` **field** injection for `config`, and
`ReportSynthesisSectionValidator:82` proves a subclass can add an `@Inject` constructor without
disturbing it — Spring processes both. `DIRABS-FN-006` asserts `config` still arrives.

---

### DD-9 — The contract test is abstract and reusable

> **Revised 2026-08-28 at `T04`:** where this decision and §6.3 say *"one abstract factory method"*, the
> delivered and reviewed shape is **five abstract seams** — `createServiceWithNoMatch()`,
> `createServiceWithFoundPerson(email, login, firstName, lastName)`, `foundSource()`,
> `createFailingService()` and `failingServiceInvocationCount()`. A single factory cannot express the
> *no-match*, *found* and *failing* backends the five §5.1 rows require, and **DD-12 already
> presupposes the richer shape.** The T04 Reviewer confirmed the seams are provider-clean and correctly
> shaped, so this is a correction to the description, not a deviation from the intent.

**Decision:** `DirectoryServiceContractTest` is abstract with one factory method; `LdapDirectoryServiceTest`
extends it.

**Argument:** the contract, not the implementation, is what every future provider must satisfy. Child
3's provider extends the same class and is covered by construction — which is what makes DD-5's
"one bean, one config value" claim true in *testing* as well as in wiring. This is the single highest-leverage
test artifact in the spec.

---

### DD-10 — No Spring context test; app-start check is the substitute *(accepted risk)*

**Decision:** do not add a Spring context smoke test. Require **one manual application start** at the
HITL pause instead.

**Argument:** MARLO has 3 test files and no context test (`docs/trd/trd.md` §10, `TS-2`). A missing or
ambiguous `@Named` bean compiles and passes `mvn test` (Checkstyle would not catch it either, and is **UNVERIFIABLE** here — EB-2), failing only at Tomcat
startup — which CI never exercises (`Dockerfile` uses `-Dmaven.test.skip=true`). Adding a context test
needs `DEC-005` and a `marlo-parent/pom.xml` edit, which would break this child's parallel-safety with
`auth-flow`.

**Implications:** `D8` in `requirements.md` §9 is an **accepted risk with a named manual substitute**,
not an unacknowledged gap. Closing it properly is `docs/trd/trd.md` §14.9 item 8. Tests also live in
`marlo-web/src/test` even for `marlo-data` types, for the same reason — `marlo-data` has no test root.

---

### DD-11 — A malformed email is discriminated **inside the catch**, not validated before the lookup *(added 2026-08-28; closes a gap surfaced by `T03`)*

**The gap.** `FN-002` *Invalid input* and §5.1 row 2 both assert that a malformed email yields
`NOT_FOUND`. Neither defined **"malformed"**, and neither said how the outcome is reached. `T03`'s first
implementation therefore let a malformed email fall through to `LDAPService.searchUserByEmail` — which
produces `ERROR` if `adauth` throws.

**Why that is a regression, not just a gap.** `ERROR` is the one value `center/…/ManageUsersAction`
reads (DD-3, DD-3a): on `ERROR` it throws `DirectoryLookupException`, which surfaces as a **500**. Today
that same admin typo is caught and reported as `manageUsers.email.doesNotExist`. So the fall-through
turns a clear message into a server error — the *opposite* direction from this spec's equivalence goal.

**Decision.** Keep the lookup, discriminate on the failure path:

```java
try {
  user = service.searchUserByEmail(email);
} catch (Exception e) {
  if (isWellFormed(email)) {
    LOG.error("Directory lookup failed for '{}'", email, e);
    return DirectoryPerson.notFound(email, DirectorySource.ERROR);
  }
  return DirectoryPerson.notFound(email, DirectorySource.NOT_FOUND);  // invalid input is not a backend failure
}
```

**"Malformed" is defined minimally:** a single `@`, a non-empty local part, and a domain part containing
at least one `.`. **Deliberately not RFC 5322** — an over-strict predicate would reject
unusual-but-valid corporate addresses that resolve today, which is the failure mode DD-4 warns about in
a different guise.

**Why inside the catch and not before the call.**

| Reason | |
|---|---|
| §5.1's no-network-call guarantee is scoped to `null`/blank **only** | Which means a malformed email is *expected* to reach the backend. Pre-validating would deviate from the spec's own scoping |
| A flawed predicate cannot do damage here | On the success path the predicate is never consulted, so a valid-but-unusual address that **resolves** is unaffected. Pre-validation would let a too-strict predicate reject it outright |
| It preserves the baseline's network behavior exactly | `getOutlookUser` calls unconditionally; so does this |

**Also decided:** the `error` log fires **only** on the well-formed branch. Logging admin typos at
`error` would bury real outages in noise — and the log exists precisely so an outage is observable.

**Implications:** `T03` implements the predicate; `T04` gains a mandatory assertion for the branch
(malformed + backend throws → `NOT_FOUND`, **not** `ERROR`); `T10` is unchanged but its `ERROR` branch
is now *more* precisely true — it fires only for a well-formed email's genuine backend failure.

**`T02` is also implicated — added 2026-08-28 after the first sweep missed it.** `DirectoryService`'s
Javadoc is *the contract* (§6.1), and it lists outcome 2 (*malformed → `NOT_FOUND`*) and outcome 5
(*backend throws → `ERROR`*) as flat alternatives with **nothing stating that outcome 2 wins when both
apply**. Precedence is precisely what DD-11 had to invent, and child 3's provider author reads that
interface — not this decision — as the contract. **The Javadoc must state the precedence explicitly.**
Assigned to `T04`, where the contract test encodes the same precedence, so the assertion and the prose
it documents land in one commit. Recorded as an incompleteness in this decision's own correction sweep,
not as a new discovery.

---

### DD-12 — A `protected` factory seam for the `LDAPService`, so the contract test exercises the real mapping *(added 2026-08-28)*

**The problem.** `LdapDirectoryService.findByEmail` constructs `LDAPService` internally, so
`LdapDirectoryServiceTest` cannot substitute the backend. Without a seam, the contract test can only run
against real `adauth` — which covers the `ERROR` branch by accident (an unreachable AD throws) and leaves
*found*, *null-return*, and the DD-11 *malformed* branch untestable. Since T04 is this spec's dominant
gate, that is not an acceptable coverage floor.

**The probe that shaped this decision.** The T03 Reviewer raised a concern that `new LDAPService()` might
throw in an environment without AD configuration, implying a `FN-002` / `NF-001` tension and a fragile
test harness. **Decompiling the shipped jar falsified it:**

```
$ javap -c -p -cp marlo-data/src/main/resources/libs/.../adauth-5.7.jar org.cgiar.ciat.auth.LDAPService

public LDAPService();                      public void setInternalConnection(boolean);
  0: aload_0                                 0: aload_0
  1: invokespecial Object."<init>"            1: iload_1
  4: aload_0                                  2: putfield internalConnection:Z
  5: iconst_1                                 5: return
  6: putfield internalConnection:Z
  9: return
```

`private boolean internalConnection` is the class's only field, and there is **no static initializer**.
So:

| Fact established by the probe | Consequence |
|---|---|
| The constructor **cannot throw** — `super()` plus one boolean write, no config read, no I/O | There is **no** `FN-002`/`NF-001` tension. `findByEmail`'s never-throws invariant holds unconditionally as written |
| `setInternalConnection` **cannot throw** — one `putfield` | Same |
| `LDAPService` is **not `final`**; `searchUserByEmail(String)` is **public and non-final** | A test subclass can override the lookup, and `super()` is provably safe |
| The constructor's position relative to the `try` is **semantically irrelevant** | A seam can be introduced with no equivalence argument to answer |

**Decision.** Extract construction to a `protected` factory on `LdapDirectoryService`:

```java
protected LDAPService newLdapService() {
  return new LDAPService();
}
```

`findByEmail` calls `this.newLdapService()` **in the same position, still outside the `try`**.
`LdapDirectoryServiceTest` defines a test-local subclass overriding it to return a stub that
`extends LDAPService` with `searchUserByEmail` overridden to return canned values or throw.

**Why this shape over the alternatives.**

| Alternative | Rejected because |
|---|---|
| No seam; run the test against real `adauth` | Only the `ERROR` branch is reachable, and only by accident. *Found*, *null-return* and DD-11's *malformed* branch — the branch DD-11 exists to protect — would all be untestable |
| Override `findByEmail` itself in a test subclass | Then the test does not exercise the **real mapping**, which is the only thing T04 is for |
| A second constructor taking a `Supplier<LDAPService>` | Adds public/package surface to a class DD-5 deliberately kept minimal, for no gain over three lines |

**Why the test subclass exercises the right thing.** Everything under review stays production code: the
null/blank fail-fast, `setInternalConnection(!isProduction())`, the raw field mapping, the DD-11 catch
discrimination, and `isWellFormed`. Only the *backend call* is substituted — which is the one thing that
cannot be executed in this repository at all.

**Implications:** `T04` owns the three-line production edit to `LdapDirectoryService` (a T03 file) and the
`DirectoryService` Javadoc precedence sentence (a T02 file). **Both are deliberate, HITL-approved
widenings of T04's file set**, recorded here because `tasks.md` T03's STOP conditions otherwise protect
those files. Reverting T04 correctly reverts the seam with the tests that need it.

---

## 11. Decision Log

| Date | Decision | Rationale |
|---|---|---|
| 2026-08-27 | Seam in `marlo-data/security/directory/` (DD-1) | Beside `security/authentication/`, where MARLO already keeps a directory abstraction with a `@Named` impl |
| 2026-08-27 | Delete `BaseAction.getOutlookUser` (DD-2) | C-1 proved 2 callers, 0 template references. Avoids giving the widest shared file a new dependency |
| 2026-08-27 | **Add `DirectorySource.ERROR` (DD-3); withdraw DEV-1** | C-4: collapsing failure into not-found turns an AD outage into a false *"email does not exist"* and can create a mis-classified user row — on a class whose "unreachable" status the convention-plugin configuration undermines. One enum constant buys true equivalence |
| 2026-08-27 | Raw values; transformations stay at call sites (DD-4) | Central normalization works today and fails silently the first time an implementation stops doing it — into a `UNIQUE` column that reaches CLARISA and the QA service |
| 2026-08-27 | No qualifier value on the impl (DD-5) | One implementation resolves by type. Qualifiers arrive with the `directory.source` switch in child 3, as one coherent change |
| 2026-08-27 | No attribute map on `DirectoryPerson` (DD-6) | Its only reader is an unreachable `main()`. Carrying it would export `adauth`'s vocabulary into MARLO's own type |
| 2026-08-27 | Reversion challenges run **inline**, not delegated | Standing session instruction not to spawn subagents. Recorded so the absence of a delegated reviewer is visible rather than inferred |
| 2026-08-27 | Accept `D8` with a manual app-start substitute (DD-10) | A Spring context test needs `DEC-005` + a `pom.xml` edit, which would break parallel-safety with `auth-flow` |
| 2026-08-28 | **DD-12 added** — a `protected` factory seam so T04's contract test exercises the real mapping; T04 also fixes the `DirectoryService` Javadoc precedence gap DD-11's first sweep missed | Decompiling `adauth-5.7.jar` falsified the premise that `new LDAPService()` could throw (`super()` + one boolean write, no static init), so there is no `FN-002`/`NF-001` tension and no equivalence objection to a seam. Without one, only the `ERROR` branch of T04 would be reachable, including a blind spot on the very DD-11 branch. Both edits are HITL-approved widenings of T04's file set |
| 2026-08-28 | **DD-11 added** — a malformed email is discriminated inside the catch, and "malformed" is defined minimally | `T03`'s Implementer surfaced that §5.1 row 2 asserted an outcome with no definition and no mechanism. The fall-through it produced would turn an admin typo into a 500 via T10's `ERROR` branch, where today it is a clear message. Resolved at the HITL gate; `T04` gains a mandatory assertion for the branch |
| 2026-08-27 | Budget: **17** tasks (+`T00`) / ~700 LOC / ~20 review rounds — revised at Phase 3 | Estimated from the finished design, revised from ~650 by Judgment Day JD-7. Exceeds ~400 LOC, so `tasks.md` carries a 3-PR recommendation |
| 2026-08-27 | **Judgment Day round 1 — 5 findings confirmed by both judges, applied; 4 single-judge findings applied on recommendation. No scoped re-judgment** (user chose *Fix only*) | Ledger: [`judgment.md`](./judgment.md). Neither judge challenged the architecture — DD-1, DD-2, DD-3, DD-5, DD-6, DD-9 and DD-10 all survived, with both judges independently confirming DD-2's and DD-3's premises against the code. The defects were bookkeeping plus one unspecified mechanism |
| 2026-08-27 | **`DirectoryLookupException` added (DD-3a)** — closes JD-7 | DD-3 said the consumer "propagates" without naming what. It must be unchecked (`validateOutlookUser:248` has no `throws` clause), must not extend `AuthorizationException` (`struts.xml:543-545` maps that to 403 instead of 500), and makes the branch assertable by type in JUnit 4. Replaces the unearned claim "preserving today's outcome exactly" with the verified boundary: same handling, different subtype |
| 2026-08-27 | **`D4` gate re-scoped to `^import org.cgiar.ciat.auth`; expected lists corrected** — closes JD-1 | The spec's only "genuinely falsifiable" gate reported **failure on a correct implementation**, twice over: it omitted `APCustomRealm` from the `marlo-data` count (2 → 3 files) and its unscoped pattern matched `WSMarlo.java`'s `org.cgiar.ciat.abw` string literals. The first defect is inherited from `EXEC-040` and is flagged for the execution plan at archive time |
