# Directory Abstraction — Tasks

**Spec ID:** `CHG-COGNITO-DIRABS-001`
**Status:** **Approved** — 2026-08-28, approved by the user acting as Tech lead at the `/akili-execute` gate
**Owner:** IBD Team — Alliance of Bioversity International and CIAT
**Last Updated:** 2026-08-28
**Implements design:** [`design.md`](./design.md) · **Requirements:** [`requirements.md`](./requirements.md) · **Review:** [`judgment.md`](./judgment.md)
**Parent:** [`../family.md`](../family.md) child 1 · **Runbook:** [`../analysis/adauth-retirement-execution-plan.md`](../analysis/adauth-retirement-execution-plan.md) CP2–CP3
**Branching:** working branch `staging-cognito-impl`. **Target merge:** `staging` (then `main` by merge only, per `CLAUDE.md` Hard rule 9).

> **Dual task IDs, on purpose.** Each task carries `DIRABS-Tnn` (this repo's template convention) **and**
> the `EXEC-nnn` ID from the approved runbook. Neither is dropped: the `T` id orders this spec, the
> `EXEC` id keeps it reconciled with the execution plan across sessions.

---

## 1. Execution Context

| Element | Value |
|---|---|
| Java | **17** — `marlo-parent/pom.xml` is the authoritative source. `mvn -v` must report 17 |
| Modules touched | `marlo-data` (5 new files), `marlo-web` (7 modified, 9 new test classes) |
| Spring profile | `dev` for any local run |
| Local run | `scripts/run-marlo-java17.sh` from the repo root — **required exactly once**, in `DIRABS-T12` |
| Spec-family status | `../family.md` child 1 · `Depends on: none` · `Parallel-safe: yes` with `auth-flow` |
| Worktree | If `auth-flow` runs concurrently, **each session needs its own `git worktree`** — two `mvn` runs in one checkout contend for the same `target/` |

### Skills per task (from the root guides' `## Skill Map`)

| Skill | Tasks | Why |
|---|---|---|
| `error-handling-patterns` | T02, T03, T04, T10 | The never-throws contract, the `NOT_FOUND` / `ERROR` discrimination, and the one consumer that propagates |
| `systematic-debugging` | any task whose verification fails | Standard trigger, not task-assigned |
| `tdd` | T04 | The contract test is written before the consumers depend on it |

**`api-design-principles` does not apply** — this spec adds no REST endpoint. No UI skill applies: there is no UI surface.

---

## 2. Pre-flight Checklist

- [x] `requirements.md` and `design.md` approved by the Tech lead. — **2026-08-28**, user acting as Tech lead at the `/akili-execute` gate.
- [x] `DIRABS-OQ-4` answered (DEV-2 — deleting `getOutlookUser` rather than rewiring it). — **approved**; closed by the approval above.
- [x] `DIRABS-OQ-5` answered (the `getLogin()` NPE is **preserved**, not "fixed"). — **confirmed**; a null guard added at `.toLowerCase()` is a defect, not a hardening.
- [x] `git status --short` is clean, or every pre-existing change is recorded and left alone (runbook step **S3** — *"the one most likely to be skipped and the one that causes the worst damage"*).
- [x] Working branch is `staging-cognito-impl`.
- [x] `DIRABS-T00` complete (baseline + drift probe). — **zero drift**; evidence in `execution.md` §2.

---

## 3. Shared Task Conventions

Defined once; every task inherits them rather than restating them.

### 3.1 Verification commands

| Gate | Command |
|---|---|
| Compile | `mvn -q install -DskipTests -pl marlo-web -am` |
| Style | `mvn -q checkstyle:check` |
| Tests | `mvn -q -pl marlo-web test` |
| Scope | `git diff --stat` reviewed against § 3.2 |

**`-q` suppresses passing noise only. A failure prints complete and verbatim** — never truncated,
summarized, or paraphrased. That asymmetry is the root guides' rule and it is not optional.

### 3.2 Protected files — appearance in any diff is a defect, in every task

`marlo-data`: `security/APCustomRealm.java` · `security/authentication/{Authenticator,LDAPAuthenticator,DBAuthenticator,AuthenticationManager}.java` · `config/APConstants.java`
`marlo-web`: `config/APConstants.java` · `utils/MD5Convert.java` · `utils/searchUsersUtil.java` · `action/center/capdev/ContactPersonAction.java:99` **and every line below** · `action/center/capdev/ContactPersonAction.java:58-71` (`getADFilter`)
Anywhere: **every `pom.xml`** · everything under `**/resources/libs/**` · `resources/global.properties` · `resources/struts*.xml` · `resources/database/migrations/**` · `data/model/Users.hbm.xml`

### 3.3 Every task's evidence rules

Three fields appear in every task and mean the same thing throughout:

| Field | Question it answers |
|---|---|
| **Falsifying input** | What concrete input would make this check report **FAIL**? A check nothing can fail is not evidence, however green it reports |
| **Disqualifies the evidence** | When is a produced reading **worthless**, as distinct from failing? An inconclusive verification is a legitimate outcome and must be reported as one — never collapsed into a pass because a command exited `0` |
| **Cannot prove** | What the check is structurally blind to. A `grep` proving an import is gone proves **isolation, not equivalence** |

### 3.4 Commit discipline

**One task, one commit.** Each consumer migration must be independently revertible
(`DIRABS-NF-007`). Commit message per the repo standard; never let narration become a commit message.

---

## 4. Task List

### DIRABS-T00 (EXEC-001 … EXEC-006) — Baseline and drift probe

- **Status:** `[x]` — PASS 2026-08-28, Leader-inline (read-only, no diff). Zero drift; see `execution.md` §2
- **Depends on:** none · **Module:** none (read-only) · **Size:** S
- **Requirements covered:** none directly — it is the precondition that makes every later line reference trustworthy.
- **Scope:** record the Java 17 toolchain; capture the baseline commit; run the **drift probe** against every `file:line` this spec cites; record the runtime `adauth` call-site inventory that `DIRABS-T16` reconciles against.
- **Verification:**
  ```
  mvn -v                      # must report 17
  git rev-parse HEAD          # record as the baseline
  grep -rn "^import org.cgiar.ciat" marlo-web/src marlo-data/src --include="*.java"
  grep -rn "new LDAPService()\|new ADConexion" marlo-web/src marlo-data/src --include="*.java"
  ```
- **Falsifying input:** any cited line that has moved. `analysis/README.md` already records one known drift (`getOutlookUser` `:4797` → `:4802`); a second would mean the checkout moved again.
- **Disqualifies the evidence:** a dirty working tree. If `git status` shows changes this plan did not create, **stop and ask** — do not stash, revert, or commit them (runbook `P11`).
- **Done when:** the inventory is recorded and every cited line either matches or has its correction written down.
- **STOP if:** `mvn -v` does not report 17, or an unrelated uncommitted change exists.

---

### DIRABS-T01 (EXEC-030) — Value types: `DirectoryPerson`, `DirectorySource`, `DirectoryLookupException`

- **Status:** `[x]` — PASS 2026-08-28, attempt 1, Reviewer PASS. Compile + checkstyle gates unavailable (EB-1, EB-2); see `execution.md`
- **Depends on:** T00 · **Module:** `marlo-data` · **Size:** S · **Skills:** —
- **Design:** §4.1, §4.2, §6.1, **DD-3**, **DD-3a**, **DD-6**
- **Requirements covered:**
  - `DIRABS-FN-003` — *"`source` must **NOT** be `null` on any path"* (the type makes it unconstructible)
  - `DIRABS-FN-005` scenario *Not found* — *"**AND IT MUST** leave `login`, `firstName`, `lastName` null rather than empty strings"*
  - `DIRABS-NF-006` (GPL header, style)
- **Files touched (new):** `security/directory/DirectoryPerson.java` · `security/directory/DirectorySource.java` · `security/directory/DirectoryLookupException.java`
- **Scope:**
  - `DirectorySource`: **8** constants — `LDAP, NOT_FOUND, ERROR, DIRECTORY_API, CLARISA, COGNITO_CLAIMS, AD_MIRROR, INVITATION`.
  - `DirectoryPerson`: 6 final fields, no setters, two factories — `found(...)` and `notFound(email, source)`. `notFound` **takes a source**; that is what makes DD-3 work.
  - `equals`/`hashCode` on all fields. **`toString` must not print `login` or `email` in full** — corporate personnel data that will reach log lines.
  - `DirectoryLookupException extends RuntimeException`, carrying the requested email and the cause.
- **Tests:** none yet — nothing consumes these.
- **Verification:** compile · checkstyle · `grep -rn "org.cgiar.ciat" marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/` → **empty** · `git diff --stat` → exactly 3 new files.
- **Falsifying input:** a `.java` file without the GPL header (checkstyle FAILs); a 121-char line; an `org.cgiar.ciat` import in any of the three (the grep FAILs).
- **Disqualifies the evidence:** a compile that succeeds only because the files are unreferenced proves they **parse**, not that they model the contract correctly. That is T04's job.
- **Cannot prove:** that `toString` actually masks the fields — checkstyle cannot see it. **Reviewer must read it.**
- **Done when:** 3 compiling files, 8 enum constants, `notFound` requires a `source` argument.
- **Rollback:** `git revert <sha>` — nothing references them.
- **STOP if:** `DirectorySource` has 7 values, or `notFound` hardcodes `NOT_FOUND`. Either breaks DD-3 before it starts.

---

### DIRABS-T02 (EXEC-031) — `DirectoryService` interface

- **Status:** `[x]` — PASS 2026-08-28, attempt 2 of 3 (attempt 1 FAILed on a Javadoc cardinality defect traced to the Leader's brief). Compile + checkstyle gates unavailable (EB-1, EB-2); see `execution.md`
- **Depends on:** T01 · **Module:** `marlo-data` · **Size:** S · **Skills:** `error-handling-patterns`
- **Design:** §5.1, §6.1 · **Requirements:** `DIRABS-FN-001`, `DIRABS-FN-002`, `DIRABS-FN-003`
- **Files touched (new):** `security/directory/DirectoryService.java`
- **Scope:** one method — `DirectoryPerson findByEmail(String email)`. **The Javadoc *is* the contract**, and must state all five rows of design §5.1's table, plus the three invariants and the no-network-call clause, explicitly: never throws · never returns `null` · `source` never `null`. It must state that `NOT_FOUND` asserts knowledge (*the directory answered*) and `ERROR` asserts the absence of knowledge (*the lookup failed*).
- **Requirements covered — clause level:**
  - `FN-001` *"exactly one method"*
  - `FN-002` *"**MUST NOT** propagate an exception under any input or backend condition"*
  - `FN-002` *"must **NOT** throw, and must **NOT** return `null`"*
  - `FN-003` *"`source` must **NOT** be `null` on any path"*
- **Tests:** none — interfaces have no behavior. T04 encodes the contract.
- **Verification:** compile · checkstyle · `git diff --stat` → 1 new file.
- **Falsifying input:** a signature other than `findByEmail(String) → DirectoryPerson`; a `throws` clause on the method.
- **Cannot prove:** **nothing about behavior.** An interface plus Javadoc is a *presence assertion* — the contract is only real once T04 executes it.
- **Done when:** the interface compiles and every §5.1 row appears in the Javadoc.
- **STOP if:** the signature drifts, or the Javadoc omits the `NOT_FOUND` / `ERROR` distinction.

---

### DIRABS-T03 (EXEC-032) — `LdapDirectoryService`

- **Status:** `[x]` — PASS 2026-08-28, attempt 2 of 3 (attempt 1 unreviewed; stopped on the FN-002 spec gap now closed as DD-11). **On inspection only** — behavioral equivalence unproven pending T04. See `execution.md`
- **Depends on:** T02 · **Module:** `marlo-data` · **Size:** M · **Skills:** `error-handling-patterns`
- **Design:** §4.3, §6.1, **DD-3**, **DD-4**, **DD-11** · **Requirements:** `DIRABS-FN-005`, `FN-004`, `FN-002`, `FN-003`
- **Files touched (new):** `security/directory/impl/LdapDirectoryService.java`
- **Files PROTECTED (beyond §3.2):** `LDAPAuthenticator.java` — same shape, different class. Do not edit it.
- **Scope:** `@Named` (**no qualifier value** — DD-5), `@Inject` constructor taking `APConfig`, mirroring `LDAPAuthenticator:38,47-51`. Reproduce `BaseAction.getOutlookUser` (`:4802-4816`) step for step: `new LDAPService()`, `setInternalConnection(!config.isProduction())`, `searchUserByEmail(email)`.
  - Found → `found(...)` with `source = LDAP`, **every field raw** (DD-4).
  - `null` return → `notFound(email, NOT_FOUND)`.
  - Caught exception → **discriminate per DD-11**: if the email is well-formed → `notFound(email, **ERROR**)` **and a log at `error`** with the email and cause (today the exception is swallowed with no log at all); if the email is **malformed** → `notFound(email, **NOT_FOUND**)` **and no `error` log** — invalid input is not a backend failure (`FN-002` *Invalid input*).
  - `isWellFormed` is a **minimal** check per DD-11: one `@`, non-empty local part, domain containing at least one `.`. **Not RFC 5322.** It is consulted **only** on the failure path, never before the lookup.
  - `null`/blank email → `notFound(email, NOT_FOUND)` **with no network call**.
  - **The only file in `security/directory/**` permitted to import `org.cgiar.ciat`.**
- **Requirements covered — clause level:**
  - `FN-005` *Found*: *"**AND IT MUST** be the only file in `security/directory/**` that imports `org.cgiar.ciat`"*; *"must **NOT** cache, retry, or pool the connection"*
  - `FN-004` *"the abstraction must **NOT** lowercase, uppercase, or trim `login` or `email`"*
  - `FN-002` *Invalid input*: *"**AND IT MUST** make no network call for a `null` or blank email"*
  - `FN-002` *Backend failure*: *"`source` **MUST** be **`ERROR`**, never `NOT_FOUND`"*; *"the failure **MUST** be logged at `error`"*
  - `FN-003` *"**MUST** set `source == LDAP` on a found person"*
- **Tests:** T04.
- **Verification:** compile · checkstyle · `grep -rln "org.cgiar.ciat" marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/` → **exactly** `impl/LdapDirectoryService.java`.
- **Falsifying input:** an `org.cgiar.ciat` import appearing in any other file of the new package.
- **Disqualifies the evidence:** compile + checkstyle passing says **nothing** about the mapping. This task's real gate is T04, and it must not be reported verified before T04 runs.
- **Cannot prove:** equivalence. That is T04 (contract) and T06–T10 (per consumer).
- **Done when:** the class compiles and no consumer uses it yet.
- **STOP if:** the mapping changes semantics — lower-casing a field `getOutlookUser` did not lower-case; returning `""` where `null` was returned; using `NOT_FOUND` on the exception path. **Equivalence is the whole point.**

---

### DIRABS-T04 (EXEC-033) — Contract test, fake, and LDAP implementation test

- **Status:** `[x]` -- PASS 2026-08-28, attempt 1. **First executable evidence of the spec:** `mvn -q -pl marlo-web -am test` green (13 tests, run twice), and assertions 7-8 mutation-proven falsifiable. See `execution.md`
- **Depends on:** T03 · **Module:** `marlo-web` (test) · **Size:** M · **Skills:** `error-handling-patterns`, `tdd`
- **Design:** §6.3, **DD-9** · **Requirements:** `FN-002`, `FN-003`, `FN-005`, `DIRABS-NF-006`
- **Files touched (new):** `marlo-web/src/test/java/.../security/directory/{FakeDirectoryService,DirectoryServiceContractTest,LdapDirectoryServiceTest}.java`
- **Files touched (modified) — HITL-approved widening, 2026-08-28, see **DD-12** and DD-11 *Implications*:**
  - `marlo-data/./security/directory/impl/LdapDirectoryService.java` — extract `protected LDAPService newLdapService()`; `findByEmail` calls `this.newLdapService()` **in the same position, still outside the `try`**. Three lines. Normally a T03 file protected by T03's STOP conditions.
  - `marlo-data/./security/directory/DirectoryService.java` — add one sentence to the Javadoc stating that **outcome 2 (malformed) takes precedence over outcome 5 (backend throws) when both apply** (DD-11). Normally a T02 file. It lands here because the contract test encodes the same precedence, so the assertion and the prose ship together.
- **Scope:**
  - `FakeDirectoryService`: hand-rolled, settable canned responses **plus a call recorder** (email received, invocation count). **No mocking framework** — `DEC-005` is `PENDING` and this spec deliberately does not request it (taking it would edit `marlo-parent/pom.xml` and break parallel-safety with `auth-flow`).
  - `DirectoryServiceContractTest`: **abstract**, with **five abstract seams** (revised at T04 from "one abstract factory method"  -- one factory cannot express *no-match*, *found* and *failing*). Encodes all five rows of design §5.1's table, the three invariants, and the no-network-call clause. **Reused verbatim by child 3's provider** — DD-9.
  - `LdapDirectoryServiceTest extends DirectoryServiceContractTest`.
  - Tests live in `marlo-web/src/test` even for `marlo-data` types: `marlo-data` has **no test source root**, and creating one is out of scope (`DIRABS-NF-004`).
- **Requirements covered — clause level:** every `FN-002` clause; `FN-003` *"**BUT** `source` must **NOT** be `null` on any path"*; `FN-005` *Not found* *"**AND IT MUST** leave `login`, `firstName`, `lastName` null rather than empty strings"*.
- **Tests — the required assertions:**
  - `null`, blank and malformed email → `found == false`, `source == NOT_FOUND`, **no throw**, **not null**.
  - Directory answered, absent → `source == NOT_FOUND`.
  - **Backend throws on a WELL-FORMED email → `found == false` AND `source == ERROR`.**
  - **Backend throws on a MALFORMED email → `found == false` AND `source == NOT_FOUND`, NOT `ERROR`** (DD-11). **Mandatory.** This branch is the only gate on DD-11, and it is the branch whose absence would let an admin typo surface as a 500 through T10.
  - Not-found result → `assertNull(login)`, `assertNull(firstName)`, `assertNull(lastName)` — **not `""`**.
  - `source` non-null on every path.
- **Verification:** `mvn -q -pl marlo-web test`
- **Falsifying input — named per assertion, because this is the spec's dominant gate:**
  | Assertion | Input that makes it FAIL |
  |---|---|
  | exception on a well-formed email → `ERROR` | an implementation that returns `NOT_FOUND` on that path |
  | exception on a malformed email → `NOT_FOUND` | an implementation that returns `ERROR` for it — which is exactly what T03's first attempt did, and what DD-11 exists to prevent |
  | not-found → null fields | an implementation defaulting to `""` |
  | never throws | an implementation that lets the backend exception escape |
  | no network call on blank | a fake counting invocations, asserting zero |
- **Disqualifies the evidence:** a green suite that never exercised the exception path. **The contract test must be able to fail** — if every assertion passes against a deliberately broken stub, the test is tautological and proves nothing.
- **Cannot prove:** that the *consumers* map correctly. That is T06–T10.
- **Done when:** the contract test is abstract, reusable, and green against `LdapDirectoryService`.
- **STOP if:** a test can only pass by changing production behavior — **fix T03, not the test.**

---

### DIRABS-T05 (EXEC-034) — Delete `BaseAction.getOutlookUser` *(DD-2 — deviates from the runbook)*

- **Status:** `[x]` -- PASS 2026-08-28, attempt 1, atomic with T05+T06+T07 (option (a)). Both gates green: install exit 0, 20 tests. See `execution.md`
- **Depends on:** T04 · **Module:** `marlo-web` · **Size:** S
- **Design:** **DD-2**, §10.0 **C-1** · **Requirements:** `DIRABS-FN-007`
- **Files touched:** `action/BaseAction.java` — **deletions only**
- **Scope:** delete `getOutlookUser` (`:4802-4816`) and both `org.cgiar.ciat` imports (`:103-104`). **Add nothing.** `BaseAction` receives **no** `DirectoryService`, declares no field, no setter, no constructor parameter.
  > **This deviates from `EXEC-034`'s original wording**, which said to rewire the method. DD-2 records why; `requirements.md` §8 **DEV-2** records it as an approved deviation. Do not "restore" the rewire.
- **Requirements covered — clause level:** `FN-007` *"**MUST** be gone"*; *"`BaseAction()` (`:613`) and `BaseAction(APConfig)` (`:620`) **MUST** be unchanged"*; *"**BUT** `BaseAction` must **NOT** declare, inject, or reference `DirectoryService` in any form"*; *"**AND IT MUST** be verified that no FreeMarker template, JavaScript file, or Struts XML references `getOutlookUser`"*.
- **Tests:** none of its own — the two callers' tests (T06, T07) cover the behavior. **This is deliberate and stated:** a deletion has no behavior to test.
- **Verification:**
  ```
  mvn -q install -DskipTests -pl marlo-web -am     # T06/T07 not yet migrated -> WILL FAIL to compile
  grep -rn "getOutlookUser\|outlookUser\|OutlookUser" . --include="*.java" --include="*.ftl" \
       --include="*.js" --include="*.xml" --include="*.properties" | grep -v "/target/"
  grep -n "org.cgiar.ciat\|DirectoryService" marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/BaseAction.java
  sed -n '613p;620p' marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/BaseAction.java
  ```
- **⚠️ Sequencing note, and it is not optional.** Deleting the method **breaks compilation** at `CrpUsersAction:630` and `json/global/ManageUsersAction:151` until T06 and T07 land. Two options, and the choice must be recorded in `execution.md`:
  - **(a) recommended** — do T05, T06, T07 as **one commit**. Compilation is never broken; the three are one atomic change.
  - **(b)** do them as three commits on a short-lived local branch, accepting that the intermediate two do not compile. **`DIRABS-NF-007`'s independent-revertibility requirement is then unmet for these three**, so (a) is preferred.
- **Falsifying input:** any `getOutlookUser` reference surviving anywhere; `DirectoryService` appearing in `BaseAction`; either constructor line differing.
- **Disqualifies the evidence:** a compile failure whose cause is *not* the two known callers. If anything else fails, a third caller existed and C-1's premise was wrong — **stop and report**.
- **Cannot prove:** that the two callers now behave identically. T06 and T07.
- **Done when:** the method and both imports are gone, both constructors are byte-identical, and (with T06/T07) compilation is green.
- **Rollback:** `git revert <sha>`.
- **STOP if:** a caller outside `CrpUsersAction` and `json/global/ManageUsersAction` appears. That invalidates DD-2 and needs a design decision, not a workaround.

---

### DIRABS-T06 (EXEC-035) — Migrate `CrpUsersAction`

- **Status:** `[x]` -- PASS 2026-08-28, attempt 1, atomic with T05+T06+T07 (option (a)). Both gates green: install exit 0, 20 tests. See `execution.md`
- **Depends on:** T04, T05 · **Module:** `marlo-web` · **Size:** M
- **Design:** §4.3, §6.2, **DD-4** · **Requirements:** `FN-001`, `FN-004`, `FN-006` *CrpUsersAction*
- **Files touched:** `action/crp/admin/CrpUsersAction.java` · `marlo-web/src/test/java/.../CrpUsersActionDirectoryTest.java` *(new)*
- **Scope:** add `DirectoryService` to the existing `@Inject` constructor (`:117-120`, currently 10 params). Replace `this.getOutlookUser(...)` at `:630`. Delete import `:48`. **Keep `.toLowerCase()` at `:638`.**
- **Requirements covered — clause level:**
  - `FN-006` *"`newUser` **MUST** receive `firstName`, `lastName`, `username` (lowercased at the call site), and `setCgiarUser(true)`, exactly as today"*; *"`isCGIARUser` **MUST** be set `true`"*
  - *"**BUT** the non-resolving branch must **NOT** change: it still requires both `firstName` and `lastName` from the form, generates a 6-digit numeric password, and sets `setCgiarUser(false)`"*
  - *"**AND IT MUST** keep the same `saving.saved.guestRole` i18n message on both branches"*
  - `FN-001` *"**AND IT MUST** receive `DirectoryService` through constructor injection"*; *"must **NOT** construct `LDAPService` … directly"*
  - `FN-004` *"**AND** each consumer **MUST** keep its own existing `.toLowerCase()` call"*
- **Tests:** `CrpUsersActionDirectoryTest` — drive with `FakeDirectoryService` returning `login = "JSmith"` and assert `newUser.getUsername()` **equals `"jsmith"`**; assert `setCgiarUser(true)`; assert the not-found branch's password generation and `setCgiarUser(false)`; assert `ERROR` behaves identically to `NOT_FOUND` here.
- **Verification:** compile · checkstyle · `mvn -q -pl marlo-web test` · `grep -n "org.cgiar.ciat" <file>` → empty · `git diff --stat` → 1 source + 1 test.
- **Falsifying input:** the fake returns `"JSmith"`; a version that stopped lowercasing writes `"JSmith"` and the assertion FAILs. **An already-lowercase fixture would pass either way and prove nothing** — that is why the mixed case is mandatory.
- **Disqualifies the evidence:** asserting only *that a username was written* rather than *which*. A test of that shape is worthless for `D1`, the dominant defect class.
- **Cannot prove:** the real AD returns what the fake returns. No task in this spec can — there is no integration harness.
- **Done when:** the diff touches one source file and its test; behavior is unchanged.
- **STOP if:** the diff touches more than the named consumer, or the mapping is not one-to-one.

---

### DIRABS-T07 (EXEC-036) — Migrate `json/global/ManageUsersAction`

- **Status:** `[x]` -- PASS 2026-08-28, attempt 1, atomic with T05+T06+T07 (option (a)). Both gates green: install exit 0, 20 tests. See `execution.md`
- **Depends on:** T04, T05 · **Module:** `marlo-web` · **Size:** M
- **Requirements:** `FN-001`, `FN-004`, `FN-006` *json/global/ManageUsersAction*
- **Files touched:** `action/json/global/ManageUsersAction.java` · `.../ManageUsersActionDirectoryTest.java` *(new)*
- **Scope:** same pattern at `:151-156`. Delete import `:24`. **Keep `.toLowerCase()` at `:156`.** Widest surface — **15 FTL pages** consume this action.
- **Requirements covered — clause level:**
  - `FN-006` *"`firstName`, `lastName`, `username` (lowercased at the call site) and `setCgiarUser(true)` **MUST** be set and `addUser()` called, as today"*
  - *"**BUT** the non-resolving branch's trim-and-length validation on `firstName`/`lastName` must **NOT** change, nor must the `manageUsers.email.notAdded` / `manageUsers.email.validation` messages"*
- **Tests:** `"JSmith"` → asserts `"jsmith"`; asserts `addUser()` called on found and **not** called on not-found; asserts both i18n messages on their exact branches; asserts the trim-and-length guard still rejects `"   "`.
- **Verification / falsifying input / disqualifiers:** as T06.
- **Cannot prove:** that the 15 FTL pages render unchanged. **No automated gate exists** — the JSON shape is asserted, the rendering is not. Recorded as a gap, not silently covered.
- **STOP if:** the i18n keys move, or the diff exceeds one source file plus its test.

---

### DIRABS-T08 (EXEC-037) — Migrate `GuestUsersValidator`

- **Status:** `[x]` -- PASS 2026-08-28, attempt 2 of 3 (attempt 1 FAILed: an unfalsifiable config assertion, an unverified Surefire mechanism stated as fact, and a latent NPE). Both gates green. No getOutlookUser implementation remains in the repo. See `execution.md`
- **Depends on:** T04 · **Module:** `marlo-web` · **Size:** M
- **Design:** §6.2, **DD-8**, §10.0 **C-3** · **Requirements:** `FN-001`, `FN-006` *GuestUsersValidator*
- **Files touched:** `validation/superadmin/GuestUsersValidator.java` · `.../GuestUsersValidatorDirectoryTest.java` *(new)*
- **Scope:** delete its duplicate of the helper at `:36-50` — **declared `public`, not private** (C-3; the "private" label was a documentation error). Add an `@Inject` constructor taking `DirectoryService`, following `ReportSynthesisSectionValidator:82`. At `:55-56`, `found` replaces `LDAPUser != null`. Delete imports `:23-24`.
- **Requirements covered — clause level:**
  - `FN-006` *"`isCGIARUser` **MUST** derive from `DirectoryPerson.found`"*; *"its duplicate … **MUST** be deleted"*
  - *"**BUT** the call to `validateGuestUsers(...)` and the field-error handling below it must **NOT** change"*
  - *"**AND IT MUST** keep receiving `config` through the inherited `@Inject protected APConfig config` field on `BaseValidator:52-53` — adding an `@Inject` constructor must not break field injection"*
- **Tests:** assert `isCGIARUser` true/false from `found`; assert `validateGuestUsers` still invoked with the same arguments; **assert `config` is non-null after construction** — the field-injection clause has no other gate.
- **Verification:** compile · checkstyle · tests · `grep -n "org.cgiar.ciat" <file>` → empty.
- **Falsifying input:** a constructor that shadows or reassigns `config` — the non-null assertion FAILs.
- **Disqualifies the evidence:** a unit test that constructs the validator with `new` bypasses Spring entirely, so it **cannot** prove field injection still works. The `config` assertion is only meaningful under a Spring-managed instance — which MARLO cannot test (`D8`). **Report this clause as covered by `DIRABS-T12`'s app-start check, not by this test.**
- **Cannot prove:** Spring wiring. See T12.
- **STOP if:** `validateGuestUsers` or the field-error handling appears changed in the diff.

---

### DIRABS-T09 (EXEC-038) — Migrate `SearchUserAction`

- **Status:** `[x]` -- PASS 2026-08-28, attempt 1. Both gates green, 28 tests. Zero-invocation assertion mutation-proven. Closes FN-004's previously ungated email clause. See `execution.md`
- **Depends on:** T04 · **Module:** `marlo-web` · **Size:** M
- **Requirements:** `FN-001`, `FN-004`, `FN-006` *SearchUserAction*
- **Files touched:** `action/json/global/SearchUserAction.java` · `.../SearchUserActionDirectoryTest.java` *(new)*
- **Scope:** add `DirectoryService` to the `@Inject` constructor. Replace `:193-205` (including its own `try/catch → null`) with one call, **still passing the lowercased email** as `:202` does. Delete imports `:30-31`. **Keep `.toLowerCase()` at `:213` and `:214`.** **Do not delete the class** — `OQ-12` is unresolved and deletion is child 3.
- **Requirements covered — clause level:**
  - `FN-006` *"`userFound` **MUST** contain `newUser=true`, `id=-1`, `name`, `lastName`, `username`, `email`, `cgiar=true`, `active=false`, `autosave=false` — same keys, same values"* *(order clause dropped 2026-08-28 — `userFound` is a `HashMap`; see `requirements.md` §13)*
  - *"**AND** the lookup **MUST** still be given the lowercased email, as `:202` does today"*
  - *"**BUT** the not-found branch must **NOT** change: `newUser=false`, `cgiar=false`, `cgiarNoExist=true`"*
  - *"**AND IT MUST** preserve the `APConstants.OUTLOOK_EMAIL` suffix guard at `:191`"*
- **Tests:** assert all **9** keys **and their exact values** (a `size() == 9` check plus a per-key `assertEquals`; **no order assertion** — see the 2026-08-28 correction); assert the 3-key not-found shape; assert a non-`@cgiar.org` email **never reaches** `findByEmail` (the fake's call recorder must show zero invocations); `"JSmith"` → `"jsmith"`.
- **Falsifying input:** a lost, renamed or mis-valued key breaks the per-key `assertEquals`; a dropped key breaks the `size() == 9` check; a lost suffix guard makes the zero-invocation assertion FAIL. *(The original text named "a `LinkedHashMap` replaced by a `HashMap`" — an impossible mutation: the map is already a `HashMap`. Corrected 2026-08-28.)*
- **Disqualifies the evidence:** asserting key **presence** without values -- a `containsKey` sweep would pass against a mapping that lost a value, swapped `name` for `lastName`, or dropped a `.toLowerCase()`. Every key needs an `assertEquals` on its exact value. *(The original text also demanded an ordered comparison; that clause was dropped 2026-08-28 -- see the correction at the Tests and Falsifying-input lines above and `requirements.md` §13.)*
- **STOP if:** the class is deleted, or the suffix guard is moved.

---

### DIRABS-T10 (EXEC-039) — Migrate `center/json/global/ManageUsersAction` *(the `ERROR` branch)*

- **Status:** `[ ]`
- **Depends on:** T04 · **Module:** `marlo-web` · **Size:** M · **Skills:** `error-handling-patterns`
- **Design:** §5.2, §6.2, **DD-3**, **DD-3a** · **Requirements:** `FN-001`, `FN-002` *A caller that must not silently degrade*, `FN-006` *center/json/global/ManageUsersAction*
- **Files touched:** `action/center/json/global/ManageUsersAction.java` · `.../CenterManageUsersActionDirectoryTest.java` *(new)*
- **Scope:** add `DirectoryService` to the `@Inject` constructor. Rewrite `validateOutlookUser` (`:248-263`) — **the only consumer that reads `source`**:
  - `found` → set `firstName`, `lastName`, `username` (lowercased, `:259`) on the **`newUser` field** and return it. **The side effect on the field is part of the contract.**
  - `NOT_FOUND` → return `null`, which `create():128` handles as `manageUsers.email.doesNotExist`.
  - **`ERROR` → throw `DirectoryLookupException`**, preserving the propagation `:255` has today.
  - Delete imports `:24-25`. **Do not delete the class** (`OQ-12`).
- **Requirements covered — clause level:**
  - `FN-002` *"**MUST** throw `DirectoryLookupException` … rather than return `null`"*; *"**AND** it **MUST** still return `null` on a genuine `NOT_FOUND`"*; *"**BUT** it must **NOT** report `manageUsers.email.doesNotExist` for a backend failure"*; *"**AND IT MUST** be the **only** caller that reads `source`"*
  - `FN-006` *"**MUST** set … on the `newUser` **field** and return it"*; *"**BUT** the class must **NOT** be deleted"*; *"**AND IT MUST** throw `DirectoryLookupException` when `source == ERROR`"*
- **Tests — three branches, all mandatory:**
  1. `found` → field mutated, instance returned, `"JSmith"` → `"jsmith"`.
  2. `NOT_FOUND` → returns `null`; `create()` produces `manageUsers.email.doesNotExist`.
  3. **`ERROR` → `@Test(expected = DirectoryLookupException.class)`**, and `create()` does **not** produce `manageUsers.email.doesNotExist`.
- **Verification:** compile · checkstyle · tests · `grep -n "org.cgiar.ciat" <file>` → empty · **`DirectoryLookupException` must not extend `org.apache.shiro.authz.AuthorizationException`** — `struts.xml:543-545` maps that to **403**, not the 500 today's exception produces.
- **Falsifying input:** branch 3 FAILs against any implementation that returns `null` on `ERROR` — which is exactly the behavior `judgment.md` JD-7 / DD-3a exist to prevent. A version extending `AuthorizationException` FAILs the class-hierarchy check.
- **Disqualifies the evidence:** asserting *"an exception was thrown"* without the type. `@Test(expected = ...)` with a specific class is required; a broad `Exception` assertion would pass for the wrong reason.
- **Cannot prove:** whether this class is reachable at all. `OQ-12` is unresolved, and the convention-plugin configuration (`struts.xml:25-28`, no locator restriction) suggests it **is** — which is why this branch is implemented rather than dismissed.
- **STOP if:** `create()` is changed. Only `validateOutlookUser` and the constructor are in scope.

---

### DIRABS-T11 (EXEC-040) — Isolation gate *(corrected pattern)*

- **Status:** `[ ]`
- **Depends on:** T05–T10 · **Module:** none (read-only) · **Size:** S
- **Requirements:** `DIRABS-NF-002`, `DIRABS-FN-009` · **Review:** `judgment.md` **JD-1**
- **Scope:** prove `marlo-web` business code no longer knows about `adauth` types.
- **Verification — use this exact pattern; the unscoped one is broken:**
  ```
  grep -rn "^import org.cgiar.ciat.auth" marlo-web/src  --include="*.java"
  # EXPECTED, exactly one file:
  #   utils/searchUsersUtil.java          (main(), unreachable - deleted in child 3)

  grep -rn "^import org.cgiar.ciat.auth" marlo-data/src --include="*.java"
  # EXPECTED, exactly three files:
  #   security/APCustomRealm.java                      (Capability A - untouched, child 2)
  #   security/authentication/LDAPAuthenticator.java   (Capability A - untouched, child 2)
  #   security/directory/impl/LdapDirectoryService.java (this spec)
  ```
  > **Why `^import` and not the bare string.** `judgment.md` **JD-1**, confirmed by both judges:
  > `ocs/ws/client/WSMarlo.java` contains `org.cgiar.ciat.abw.control.logic` in **annotation string
  > literals** with no import at all. The unscoped pattern matched it and reported 9 `marlo-web` files.
  > **And the original `marlo-data` expectation of 2 files omitted `APCustomRealm`** — that defect is
  > inherited from `EXEC-040` itself and is flagged for the runbook at archive time.
- **Requirements covered — clause level:** `FN-009` *"this file **MUST** be the **only** remaining `org.cgiar.ciat` importer in `marlo-web/src`"*; *"it **MUST NOT** be migrated"*; *"**BUT** it must **NOT** be deleted here"*; *"**AND IT MUST** be named in the verification's expected output, so its presence reads as a decision rather than a missed consumer"*.
- **Falsifying input:** any file in either output that is not on the expected list.
- **Disqualifies the evidence:** running the **unscoped** pattern. It produces a number that looks like a failure and is not — and a reviewer who "fixes" the expectation to match it destroys the gate.
- **Cannot prove — state this in the task report:** this grep proves **isolation, not equivalence.** It says the import is gone; it says nothing about whether the replacement behaves the same. That is T04 and T06–T10.
- **Done when:** both outputs match exactly.
- **STOP if:** an unexpected file appears — a consumer was missed.

---

### DIRABS-T12 — Spring context smoke check *(spec-added; no `EXEC-` equivalent)*

- **Status:** `[ ]`
- **Depends on:** T11 · **Module:** none · **Size:** S
- **Design:** **DD-10** · **Requirements:** the `D8` substitute in `requirements.md` §9; the field-injection clause of `FN-006` *GuestUsersValidator*
- **Why this task exists:** MARLO has **no Spring context test**. A missing or ambiguous `@Named` bean **compiles, passes checkstyle, and passes `mvn test`**, failing only at Tomcat startup — which CI never exercises (`Dockerfile` builds with `-Dmaven.test.skip=true`). This is the spec's largest blind spot, and this manual check is its only available substitute. **It is a task rather than a footnote so it cannot be skipped silently.**
- **Scope:** start the application once and confirm it serves a page and that a `DirectoryService`-dependent flow constructs.
- **Verification:**
  ```
  scripts/run-marlo-java17.sh            # from the repo root
  curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/marlo-web/    # expect 2xx or 3xx
  ```
  Then, in the running instance, open **one** admin user-creation screen so `CrpUsersAction` and
  `GuestUsersValidator` are actually instantiated by Spring.
- **⚠️ Concurrency — mandatory.** `run-marlo-java17.sh` **kills any `cargo:run` process, deletes `marlo-{utils,data,web}/target`, and rewrites `marlo-dev.properties`** before building. **Never run it while another agent is working in this checkout.** If `auth-flow` is running in parallel, it is in its own worktree and this is safe; otherwise wait for the worker to report.
- **Falsifying input:** a missing `@Named` on `LdapDirectoryService`, or a second implementation making the injection ambiguous — the context fails to start and the `curl` never returns 2xx.
- **Disqualifies the evidence:** a `curl` that returns 2xx from a **stale WAR**. Confirm the build in this run actually compiled the new classes before trusting the response. A 2xx after a failed build is worthless.
- **Cannot prove:** anything about a multi-instance environment. Local runs use a single Cargo instance with **no memcached**, so kryo session-serialization defects (`TS-3`) remain undetectable here. Out of scope; recorded.
- **Done when:** the app boots, serves a page, and an admin user screen renders.
- **STOP if:** the context fails to start. That is a wiring defect, and it is exactly what this task exists to catch.

---

### DIRABS-T13 (EXEC-041) — Checkpoint 2 report

- **Status:** `[ ]`
- **Depends on:** T12 · **Size:** S
- **Scope:** emit the `CHECKPOINT RESULT` for CP2. **State explicitly: `adauth` is still the implementation. Nothing was removed. Observable behavior is unchanged.** Record which T05 sequencing option (a or b) was taken. Update the runbook's `Execution State` block and commit it with the task.
- **Verification:** the report exists, names every task's evidence, and the `Execution State` block is internally consistent.
- **Disqualifies the evidence:** a report claiming a verification that was never run. Each line must cite the command and its outcome.

---

### DIRABS-T14 (EXEC-050) — Eliminate the AD construction in `ContactPersonAction`

- **Status:** `[ ]`
- **Depends on:** T13 · **Module:** `marlo-web` · **Size:** S
- **Design:** §6.2, **DD-7**, §10.0 **C-2** · **Requirements:** `DIRABS-FN-008`
- **Files touched:** `action/center/capdev/ContactPersonAction.java` — **deletions only**
- **Scope:** delete `:86` (`new LDAPService()`), `:88-91` (the four local `APConstants.*_AD` reads), `:93` (`new ADConexion(...)`), and imports `:24-25`. **Change nothing else.**
- **Files PROTECTED (task-specific, beyond §3.2):** `:99` (`adUsermanager.searchUsers`) **and every line below** · `:58-71` (`getADFilter` — leave it; child 3 deletes it) · both `APConstants.java` (the four constants themselves survive) · the commented-out block at `:96-97` (leave, or remove only as a comment)
- **Requirements covered — clause level:**
  - `FN-008` *"no `LDAPService` and no `ADConexion` **MUST** be constructed"*; *"`:86`, `:88-91`, `:93` and the two imports **MUST** be deleted"*
  - *"**BUT** `:99` and every line below it must **NOT** appear in the diff"*
  - *"**AND** `getADFilter` at `:58-71` **MUST** remain"*
  - *"**AND IT MUST** remain true that the four `APConstants.*_AD` constants still exist"*
- **Verification:**
  ```
  mvn -q install -DskipTests -pl marlo-web -am && mvn -q checkstyle:check
  git diff marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/center/capdev/ContactPersonAction.java
  grep -n "ADConexion\|LDAPService" <file>          # expect empty
  grep -n "adUsermanager.searchUsers" <file>        # expect present, at :99 area
  grep -n "getADFilter" <file>                      # expect present
  grep -rn "GENERICUSER_AD\|PORT_AD" marlo-data/src marlo-web/src --include="APConstants.java"  # expect present
  ```
- **Falsifying input:** `adUsermanager.searchUsers` or any line below `:99` appearing in the diff; `getADFilter` missing; a constant deleted from either `APConstants.java`.
- **Disqualifies the evidence:** a passing compile alone. The endpoint's output is T15's gate.
- **Cannot prove:** that `searchContact.do` still returns the same JSON. **T15.**
- **Note (C-2):** removing `Integer.parseInt(PORT_AD)` at `:93` also removes a throw that would fire **today** if `PORT_AD` were malformed. That is a change in the *"now works where it used to fail"* direction, on dead code. Recorded so a reviewer does not read it as an unnoticed side effect.
- **STOP if:** `:99` or below appears in the diff.

---

### DIRABS-T15 (EXEC-051) — `ContactPersonActionTest`

- **Status:** `[ ]`
- **Depends on:** T14 · **Module:** `marlo-web` (test) · **Size:** M
- **Requirements:** `DIRABS-FN-008` *"the returned `users` list **MUST** have the same map keys and values as today, sourced from `adUsermanager.searchUsers(queryParameter)`"*
- **Files touched (new):** `marlo-web/src/test/java/.../ContactPersonActionTest.java`
- **Scope:** stub `AdUserManager` to return 2 `AdUser` rows; assert `searchADUser()` produces 2 maps with matching keys and values, and that **no `adauth` type is instantiated**.
- **Tests:** the map shape (`idUser`, `firstName`, `lastName`, `email`, …) with values traced to the stub; the `idUser` counter starting at 1; the empty-result path.
- **Verification:** `mvn -q -pl marlo-web test`
- **Falsifying input:** a stub returning 2 rows where the assertion expects 2 maps with those exact values — a version that lost a key or renamed one FAILs.
- **Disqualifies the evidence:** asserting the map **size** only. Size is not shape.
- **Cannot prove:** that the real `ad_user` query returns the same rows. The DB is stubbed out; **no task in this spec covers the real query.** Recorded as a gap.
- **STOP if:** the test requires a production change to pass.

---

### DIRABS-T16 (EXEC-052) — Re-inventory the runtime call sites

- **Status:** `[ ]`
- **Depends on:** T15 · **Module:** none (read-only) · **Size:** S
- **Requirements:** `DIRABS-FN-008`, and the `SC-8` count
- **Scope:** reproduce `DIRABS-T00`'s inventory with a *"still reachable?"* column. **Expected end state — exactly 3 live `adauth` call sites:**
  | Site | Status after CP3 |
  |---|---|
  | `APCustomRealm:287` | **Live** — Capability A, child 2 |
  | `LDAPAuthenticator:61` | **Live** — Capability A, child 2 |
  | `LdapDirectoryService` *(new)* | **Live** — the single Capability B site, swapped in child 3 |
  | `BaseAction` | **Deleted** |
  | `CrpUsersAction`, `ManageUsersAction` � --2, `SearchUserAction`, `GuestUsersValidator` | **Migrated** |
  | `ContactPersonAction` | **Eliminated** |
  | `searchUsersUtil` | Unreachable (`main()`) — child 3 |
- **Verification:** `grep -rn "new LDAPService()\|new ADConexion" marlo-web/src marlo-data/src --include="*.java"` reconciles with the table and with T00's baseline.
- **Falsifying input:** a fourth live site, or a site the analysis called unreachable turning out to be reachable.
- **Disqualifies the evidence:** a count that does not reconcile against T00. Without the baseline the number is unanchored — **report it as inconclusive rather than as 3.**
- **STOP if:** the count is not 3, or reconciliation fails.

---

### DIRABS-T17 (EXEC-053) — Checkpoint 3 report

- **Status:** `[ ]`
- **Depends on:** T16 · **Size:** S
- **Scope:** emit `CHECKPOINT RESULT` for CP3, including T16's table and its reconciliation with T00. Update the runbook `Execution State`. **State explicitly that Gate 1 is NOT reached** — two Capability A sites remain live, and reaching zero is child 2's and child 3's work.
- **Verification:** the report reconciles; the `Execution State` block is consistent.

---

## 5. Dependency Graph

```
T00 (baseline + drift probe)
 └── T01 (value types + exception)
      └── T02 (DirectoryService interface)
           └── T03 (LdapDirectoryService)
                └── T04 (contract test + fake + LDAP test)   � --�── the gate for D1/D2/D3
                     │
                     ├── T05 (delete BaseAction.getOutlookUser)
                     │    ├── T06 (CrpUsersAction)        ── atomic with T05, see T05 note (a)
                     │    └── T07 (json/global/ManageUsersAction) ─┘
                     ├── T08 (GuestUsersValidator)        ── independent of T05
                     ├── T09 (SearchUserAction)           ── independent of T05
                     └── T10 (center/…/ManageUsersAction)  ── independent of T05, owns the ERROR branch
                          │
                          └── T11 (isolation gate — corrected grep)
                               └── T12 (Spring context smoke check)   � --�── the only D8 evidence
                                    └── T13 (CP2 report)
                                         └── T14 (ContactPersonAction deletions)
                                              └── T15 (ContactPersonActionTest)
                                                   └── T16 (re-inventory)
                                                        └── T17 (CP3 report)
```

**No cycles.** T08, T09 and T10 depend only on T04 and could run concurrently **in separate worktrees**;
in one checkout they serialize on `target/`. T05+T06+T07 are one atomic unit (see T05).

---

## 6. Testing Plan

| Layer | Coverage | Reality check |
|---|---|---|
| **Unit — contract** | `DirectoryServiceContractTest` (abstract) + `LdapDirectoryServiceTest`. All five §5.1 table rows, the three invariants, the no-network-call clause, and the `NOT_FOUND` / `ERROR` split | MARLO's **first** authentication-adjacent tests. Reused verbatim by child 3 |
| **Unit — per consumer** | 5 classes, one per migrated consumer. Every assertion names the **exact** expected value | The only gate for `D1`, the dominant defect class |
| **Unit — endpoint** | `ContactPersonActionTest` with a stubbed `AdUserManager` | Does **not** cover the real `ad_user` query |
| **Integration** | **None.** No harness exists | Gap, recorded — not silently claimed |
| **E2E** | **None.** No harness exists | Gap, recorded |
| **Manual** | `DIRABS-T12` app-start + one admin user screen | The **only** evidence for `D8` (Spring wiring) |
| **Mocking framework** | **None** — hand-rolled fakes. `DEC-005` deliberately not requested | Taking it would edit `marlo-parent/pom.xml` and break parallel-safety with `auth-flow` |

**A green suite is not sufficient evidence in this repository.** Before this spec there were 3 test
files, one with its body commented out. The gates that carry weight here are compile, checkstyle, the
per-task `git diff` review, and T12's manual start.

---

## 7. Operational Steps

**None.** No migration, no configuration change, no environment variable, no BI or AI-service
coordination, no deployment. `DIRABS-NF-003`, `NF-004`, `NF-005`.

The Cognito environment variables recorded in `../family.md` § *Configuration delivery* belong to
**child 2**; this child introduces no configuration at all.

---

## 8. Rollback Plan

| Scope | Action |
|---|---|
| One consumer | `git revert <sha>` — each consumer is its own commit (`DIRABS-NF-007`) |
| T05+T06+T07 | One revert if committed atomically (option a); three reverts in reverse order otherwise |
| The whole spec | Revert T17 → T01 in reverse order. **`adauth` was never removed**, so no restoration is needed — the library, its JARs and its dependency were untouched throughout |
| Production | **Not applicable.** This spec deploys nothing |

**Nothing in this spec is irreversible.** That is a property of the design (nothing deleted but dead
code and one method with two verified callers), not of the rollback plan.

---

## 9. Clause-Level Coverage Matrix

**Requirement-ID presence is not closure.** Every scenario and every `BUT` / `AND IT MUST` clause has a
named owning task. **17 scenarios · 15 `BUT` clauses · 16 `AND IT MUST` clauses · 11 NF/SEC/OPS/ARCH
requirements = 59 items.**

| Requirement | Scenario | Owning task(s) |
|---|---|---|
| `FN-001` | Every consumer goes through the seam | T02 (contract) · **T06, T07, T08, T09, T10** (each clause per consumer) |
| `FN-002` | Invalid input | T03, **T04** |
| `FN-002` | Directory answers, person absent | T03, **T04** |
| `FN-002` | Backend failure | T03, **T04** |
| `FN-002` | A caller that must not silently degrade | **T10** |
| `FN-003` | Attribution is answerable | T01, T03, **T04** |
| `FN-004` | Login case preserved for the caller | T03 (raw mapping) · **T06, T07, T09, T10** (each keeps its `.toLowerCase()`) |
| `FN-005` | Found person | T03, **T04** |
| `FN-005` | Not found | T01 (null-not-empty) , **T04** |
| `FN-006` | `CrpUsersAction` | **T06** |
| `FN-006` | `json/global/ManageUsersAction` | **T07** |
| `FN-006` | `GuestUsersValidator` | **T08** + T12 (the field-injection clause) |
| `FN-006` | `SearchUserAction` | **T09** |
| `FN-006` | `center/json/global/ManageUsersAction` | **T10** |
| `FN-007` | BaseAction shrinks, no new dependency | **T05** |
| `FN-008` | The endpoint is behaviorally identical | **T14** (deletions) + **T15** (output) |
| `FN-009` | The exception is explicit, not an oversight | **T11** |

| Non-functional | Owning task(s) |
|---|---|
| `NF-001` observable equivalence | T04, T06–T10, T15 + per-task `git diff` |
| `NF-002` import-scoped isolation counts | **T11** |
| `NF-003` no new configuration | every task's `git diff` (`APConfig.java` must not appear) |
| `NF-004` no dependency change | every task's `git diff` (no `pom.xml`) |
| `NF-005` no schema change | every task's `git diff` (no migration) |
| `NF-006` GPL header + style | T01–T04, T15 · `mvn -q checkstyle:check` |
| `NF-007` independent revertibility | § 3.4 + T05's sequencing note |
| `NF-008` English only, no new user-facing string | Reviewer audit, every task |
| `SEC-001` no credential-handling change | **T14** (the `*_AD` constants survive) + `APConstants.java` protected everywhere |
| `OPS-001` source attribution | T01, T03, **T04** |
| `ARCH-001` swap = one bean + one value | T02, T03, **T04** (`DD-9`'s reusable contract) + design review |

### Clauses whose owning task **cannot** fully prove them — named, not hidden

| Clause | Owner | What is genuinely uncovered |
|---|---|---|
| `FN-006` *GuestUsersValidator* — *"**AND IT MUST** keep receiving `config` through the inherited field"* | T08 + T12 | A unit test using `new` bypasses Spring, so only T12's app start is real evidence |
| `FN-006` *json/global/ManageUsersAction* — the 15 FTL pages | T07 | The JSON shape is asserted; **the rendering is not.** No automated gate exists |
| `FN-008` — *"sourced from `adUsermanager.searchUsers`"* | T15 | The manager is stubbed; **the real `ad_user` query is untested** |
| `FN-005` *Found person* — the real AD's return values | T04 | The backend is faked; **no integration harness exists** |
| `D8` Spring wiring | T12 | A single manual start. Closing it properly is `docs/trd/trd.md` §14.9 item 8 |

**These are accepted gaps, recorded so no one reads a green suite as covering them.**

---

## 10. Definition of Done

- [ ] T00 … T17 complete, each with its verification evidence in `execution.md`.
- [ ] `SC-1` … `SC-11` in [`proposal.md`](./proposal.md) all satisfied.
- [ ] The isolation gate (T11) returns **exactly** 1 file for `marlo-web` and **3** for `marlo-data`.
- [ ] Exactly **3** live `adauth` call sites remain (T16), reconciled against T00.
- [ ] `mvn -q install -DskipTests -pl marlo-web -am` and `mvn -q checkstyle:check` green.
- [ ] `mvn -q -pl marlo-web test` green, with **9 new test classes** present.
- [ ] T12's app-start check performed and recorded.
- [ ] No protected file (§ 3.2) appears in any diff.
- [ ] `adauth` still present in all 3 POMs and on the classpath — **this child removes nothing**.
- [ ] `../family.md` child 1 `Status` moved to `done`; child 3's `Depends on` satisfied.
- [ ] The `EXEC-040` defect (its `marlo-data` expectation omits `APCustomRealm`) flagged for the execution plan at archive time.
- [ ] **Gate 1 explicitly NOT claimed.** Two Capability A sites remain live by design.

---

## 11. Decision Log

| Date | Decision | Rationale |
|---|---|---|
| 2026-08-27 | **Dual task IDs** (`DIRABS-Tnn` + `EXEC-nnn`) | The repo template wants a `T` sequence; the runbook and the family manifest are keyed on `EXEC-*`. Dropping either would break a real cross-reference |
| 2026-08-27 | **T05+T06+T07 recommended as one atomic commit** | Deleting `getOutlookUser` breaks compilation until both callers migrate. Three commits would leave two non-compiling intermediates and forfeit `NF-007` for those three |
| 2026-08-27 | **T12 added — no `EXEC-` equivalent** | `D8` (Spring wiring) has no automated gate in MARLO and the runbook has no task for it. A manual check written as a task cannot be skipped silently the way a footnote can. **Raises the task count from the design's budget of 16 to 17** |
| 2026-08-27 | Per-consumer tests live **inside** their migration task, not in a separate test task | One task, one commit, one revert. A separate test task would make a consumer's change and its proof separately revertible, which is worse |
| 2026-08-27 | `DEC-005` (Mockito) **deliberately not requested** | A hand-rolled fake for a one-method interface is trivial, and taking the dependency would edit `marlo-parent/pom.xml`, breaking parallel-safety with `auth-flow` |
| 2026-08-27 | Uncoverable clauses **listed explicitly** in § 9 rather than mapped to a task that cannot prove them | A coverage matrix that claims closure it does not have is worse than one that names its gaps |
