# Directory Abstraction — Execution Log

**Spec ID:** `CHG-COGNITO-DIRABS-001`
**Spec path:** `changes/migrate-ad-authentication-to-cognito/directory-abstraction`
**Harness:** `/akili-execute` — AKILI Leader → Implementer → Reviewer triad
**Working branch:** `staging-cognito-impl` · **Target merge:** `staging`
**Baseline commit:** `8f88e7822534fa2e1a0e94fa6fb5c90b1195a683`
**Approval Mode:** `gated` — every continue/pause gate stops for the user

---

## 1. Document Control

| Element | Value |
|---|---|
| Created | 2026-08-28 |
| Last appended | 2026-08-28 |
| Tasks total | 18 (`DIRABS-T00` … `DIRABS-T17`) |
| Tasks complete | 1 (`T00`) |
| Budget (from `design.md` §9) | 17 tasks (+`T00`) · ~700 LOC · ~20 review rounds |
| Budget consumed | 0 review rounds · 0 LOC |
| Model bindings | Leader `opus` (T1) · Implementer `sonnet` (T2) · Reviewer `opus` (T3) — `author ≠ auditor` holds on both the model and the write axis |

### 1.1 Approval record

The spec entered this run as **`Status: Draft`** under **`Approval Mode: gated`**, with pre-flight items
1–3 unchecked. The Leader stopped before spawning any worker and put the gate to the user, who
**approved as Tech lead on 2026-08-28**. Consequences, all recorded in `requirements.md` §13:

- `requirements.md`, `design.md`, `tasks.md` → `Status: Approved`.
- **`DIRABS-OQ-4` CLOSED — approved.** DEV-2 stands: `getOutlookUser` is **deleted**, not rewired.
  `EXEC-034`'s original wording is superseded by DD-2 for this spec.
- **`DIRABS-OQ-5` CONFIRMED — the `getLogin()` NPE is preserved, not "fixed."** A null guard added at
  any consumer's `.toLowerCase()` is a **defect** in this spec, not a hardening. Every Implementer
  brief for T06–T10 must carry this clause.

### 1.2 Spec defect corrected before execution

`tasks.md` §4 shipped with **no per-task status markers**. `/akili-execute` selects tasks on
`[ ]` / `[~]` / `[x]` and Step 3 flips them, so the approved task list had nowhere to record progress.
A `- **Status:**` line was added to each of the 18 task headings. **No task text was altered** —
mechanical bookkeeping, not a scope change. Recorded in `requirements.md` §13.

### 1.3 Environment deviation binding on every task

`mvn -v` reports **Java 1.8.0_202** in this shell, while `marlo-parent/pom.xml` declares
`<release>17</release>`. This is `DIRABS-T00`'s explicit STOP condition. **Cause is environment, not
spec:** `JAVA_HOME` points at `jdk1.8.0_202`; `C:\Program Files\Java\jdk-17` is present and yields
Maven `17.0.12`.

**Resolution:** every verification command in this spec is run with
`export JAVA_HOME="C:/Program Files/Java/jdk-17"` prefixed, session-scoped. Machine configuration is
**not** mutated. A verification run without that export is **disqualified evidence**, not a failure —
it fails for a reason unrelated to the change under review.

---

## 2. Task Execution History

### `DIRABS-T00` (EXEC-001 … EXEC-006) — Baseline and drift probe

| Field | Value |
|---|---|
| **Status** | **PASS** |
| **Date** | 2026-08-28 |
| **Implementer attempts** | **0 — executed Leader-inline** |
| **Reviewer verdict** | **N/A — no diff exists to audit** |
| **Requirements covered** | None directly. T00 is the precondition that makes every later `file:line` reference in this spec trustworthy |

#### Delegation deviation — recorded, not hidden

T00 was executed **inline by the Leader**, with no Implementer and no Reviewer. Justification:

- The task is **read-only by definition** (`Module: none (read-only)`). It produces evidence, not a
  diff. The Reviewer gate audits a diff; with no diff there is nothing for it to audit, and spawning
  it would be a spawn with an empty payload.
- It is squarely inside the *Delegation Thresholds* inline row — *"a quick check, a puntual
  verification"* — being four greps, a `sed` probe and two version commands.
- Its output is written to this file, which the Leader owns.

This deviation applies to **T00 only**. It does **not** extend to `T11`, `T13`, `T16` or `T17`, which
are also read-only: those reconcile against a changed tree and carry gate semantics, so they follow
the normal flow. `T00` runs before any change exists.

#### Evidence

**Toolchain — STOP condition hit and resolved (see §1.3):**

```
$ mvn -v
Java version: 1.8.0_202, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk1.8.0_202\jre
                                   ↑ STOP: marlo-parent/pom.xml declares <release>17</release>

$ export JAVA_HOME="C:/Program Files/Java/jdk-17"; mvn -v
Java version: 17.0.12, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-17
                                   ↑ resolved, session-scoped
```

**Baseline commit:**

```
$ git rev-parse HEAD
8f88e7822534fa2e1a0e94fa6fb5c90b1195a683
```

**Working tree — the T00 disqualifier:** `git status --short` returned **empty**. No pre-existing
change existed, so runbook `P11` (*stop and ask; do not stash, revert or commit*) was not triggered.
Branch confirmed `staging-cognito-impl`.

**Import inventory — `grep -rn "^import org.cgiar.ciat"`:**

| Module | Files | Import lines |
|---|---|---|
| `marlo-web/src` | **8** | 14 — `BaseAction:103-104` · `center/capdev/ContactPersonAction:24-25` · `center/json/global/ManageUsersAction:24-25` · `crp/admin/CrpUsersAction:48` · `json/global/ManageUsersAction:24` · `json/global/SearchUserAction:30-31` · `utils/searchUsersUtil:3-4` · `validation/superadmin/GuestUsersValidator:23-24` |
| `marlo-data/src` | **2** | 4 — `security/APCustomRealm:28-29` · `security/authentication/LDAPAuthenticator:21-22` |

This is exactly the eight-row + two-row set `requirements.md` §2.1 asserts. Note the `marlo-data`
count of **2 files** here confirms the `judgment.md` **JD-1** correction: `APCustomRealm` **is** an
importer, so `T11`'s post-change expectation of **3** `marlo-data` files (the two above plus the new
`LdapDirectoryService`) is the correct one, and `EXEC-040`'s expectation of 2 is the inherited defect
already flagged for the runbook at archive time.

**Construction-site inventory — `grep -rn "new LDAPService()\|new ADConexion"` — 9 sites:**

| Site | Fate in this spec |
|---|---|
| `BaseAction:4803` | Deleted (T05) |
| `center/capdev/ContactPersonAction:86` (`LDAPService`), `:93` (`ADConexion`) | Deleted (T14) |
| `center/json/global/ManageUsersAction:249` | Migrated (T10) |
| `json/global/SearchUserAction:193` | Migrated (T09) |
| `validation/superadmin/GuestUsersValidator:37` | Migrated (T08) |
| `utils/searchUsersUtil:14` | Untouched — `main()`, unreachable; child 3 |
| `marlo-data` `APCustomRealm:287` | Untouched — Capability A; child 2 |
| `marlo-data` `LDAPAuthenticator:61` | Untouched — Capability A; child 2 |

`CrpUsersAction` and `json/global/ManageUsersAction` hold **no** construction site of their own — they
call `BaseAction.getOutlookUser`. This reconciles with `requirements.md` §13's recorded correction that
the construction sites and the migrated consumers are **different sets**. This table is the anchor
`DIRABS-T16` reconciles its post-change count against.

**Drift probe — every cited `file:line`. Result: ZERO drift.**

| Cited reference | Probed content | Verdict |
|---|---|---|
| `BaseAction:4802` `getOutlookUser` | `public LDAPUser getOutlookUser(String email) {` | ✅ matches the **corrected** line (`analysis/README.md` recorded `:4797` → `:4802`); no second drift |
| `BaseAction:613` | `public BaseAction() {` | ✅ |
| `BaseAction:620` | `public BaseAction(APConfig config) {` | ✅ |
| `CrpUsersAction:117-120` | `@Inject` + 10-param constructor | ✅ |
| `CrpUsersAction:630` | `LDAPUser LDAPUser = this.getOutlookUser(newUser.getEmail());` | ✅ |
| `CrpUsersAction:638` | `newUser.setUsername(LDAPUser.getLogin().toLowerCase());` | ✅ the `.toLowerCase()` T06 must keep |
| `json/global/ManageUsersAction:151-156` | `getOutlookUser` call, `!= null` branch, `.toLowerCase()` at `:156` | ✅ |
| `GuestUsersValidator:36` | `public LDAPUser getOutlookUser(String email) {` | ✅ **declared `public`** — confirms correction **C-3**; the "private" label was a documentation error |
| `GuestUsersValidator:55-56` | `getOutlookUser` call + `if (LDAPUser != null)` | ✅ |
| `SearchUserAction:191` | `if (userEmail.toLowerCase().endsWith(APConstants.OUTLOOK_EMAIL))` | ✅ the suffix guard T09 must preserve |
| `SearchUserAction:202` | `service.searchUserByEmail(userEmail.toLowerCase())` | ✅ lowercased email is what the lookup receives |
| `SearchUserAction:213-214` | two `.toLowerCase()` calls | ✅ |
| `center/…/ManageUsersAction:248` | `private User validateOutlookUser(String email) {` | ✅ **no `throws` clause** — confirms DD-3a's premise that the exception must be unchecked |
| `center/…/ManageUsersAction:255` | `LDAPUser user = service.searchUserByEmail(email);` | ✅ |
| `center/…/ManageUsersAction:259` | `newUser.setUsername(user.getLogin().toLowerCase());` | ✅ |
| `ContactPersonAction:58` | `public String getADFilter(String criteria) {` | ✅ must survive T14 |
| `ContactPersonAction:88-91` | the four `APConstants.*_AD` reads | ✅ |
| `ContactPersonAction:93` | `new ADConexion(..., Integer.parseInt(port))` | ✅ |
| `ContactPersonAction:99` | `List<AdUser> ad_users = adUsermanager.searchUsers(queryParameter);` | ✅ protected — must not appear in T14's diff |

#### Falsifying input — was it satisfiable?

Yes, and it is what makes this evidence rather than ceremony. The probe would have reported drift on
any moved line; `analysis/README.md`'s known `:4797` → `:4802` correction is precisely a case where an
earlier run of this probe **did** fail and produced a correction. This run found none.

#### Disqualifiers — checked, none triggered

- **Dirty working tree** (T00's stated disqualifier): tree was clean. Not triggered.
- **Toolchain:** the JDK 8 reading **was** a STOP and is resolved in §1.3, not waved through.

#### Cannot prove

The inventory proves **presence and location**, nothing about behavior or equivalence. It does not
show that any consumer can be migrated safely — that is T04 (contract) and T06–T10 (per consumer). It
also cannot show whether `center/json/global/ManageUsersAction` is *reachable*: `OQ-12` is unresolved,
and the convention-plugin configuration inverts the presumption toward reachable, which is why T10
implements the `ERROR` branch rather than dismissing it.

#### Decisions made

| Decision | Rationale |
|---|---|
| Resolve the JDK mismatch **per-command**, not by changing `JAVA_HOME` machine-wide | The STOP condition is about what the build actually runs, not about the user's environment. Mutating machine config is a side effect outside this spec's scope and outside its rollback plan |
| Run T00 **inline** with no Reviewer | No diff exists. Justified above; explicitly does not extend to the other read-only tasks |
| Take **T05 sequencing option (a)** — T05+T06+T07 as one atomic commit | `tasks.md` T05 requires the choice be recorded. (a) is the spec's recommendation: compilation is never broken and `NF-007` is preserved for the trio as one revertible unit. **Recorded here in advance; re-confirm at T05.** |

#### Issues encountered

One, resolved: the Java 8 toolchain (§1.3). No spec defect, no code defect.

#### Final verification result

**PASS.** Baseline recorded, tree clean, branch correct, toolchain resolved, inventories captured, and
**every cited `file:line` in the spec reconciles with zero drift.**

---

### `DIRABS-T00.5` — Baseline compile *(not a spec task; Leader pre-flight)*

Not a task in `tasks.md`. Run at the user's instruction at the approval gate, on the **untouched**
tree, so that the first Implementer compile failure is attributable: a break appearing after T01 is
then known to be T01's, not pre-existing.

```
$ export JAVA_HOME="C:/Program Files/Java/jdk-17"
$ mvn -q install -DskipTests -pl marlo-web -am
```

**Result: FAILED — environmental, not a code defect.** Verbatim:

```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-war-plugin:3.3.2:war (default-war)
on project marlo-web: Could not create classes archive: Problem creating jar: Execution exception
(and the archive is probably corrupt but I could not delete it):
java.io.FileNotFoundException:
D:\projects\MARLO\marlo-web\target\classes\org\cgiar\ccafs\marlo\action\ai\AiAction.class
(The process cannot access the file because it is being used by another process) -> [Help 1]
```

**Compilation succeeded; WAR packaging failed.** Evidence for that split, which matters because it
determines whether the tree is healthy:

| Check | Result |
|---|---|
| `marlo-utils` jar | built `08:32` |
| `marlo-data` jar | built `08:33` · 2402 `.class` files |
| `marlo-web` `BaseAction.class` | compiled fresh within the run |
| `marlo-web` WAR | **absent** — the `maven-war-plugin` step is where it died |

**Root cause — a persistent Windows file lock.** The holder is the **VS Code Java language server**
(`redhat.java` 1.55.0 / Eclipse JDT LS, PID `34448`, JDK 21, 744 MB), which keeps
`marlo-web/target/classes/**` open for indexing. Probed and confirmed **still held** after the build
exited:

```
[System.IO.File]::Open(...,'Open','ReadWrite','None')
→ STILL LOCKED: The process cannot access the file ... because it is being used by another process
```

Nothing is listening on `8080`, so this is not a stray Tomcat/Cargo instance.

#### Why this is a spec-wide blocker, not a one-off

`tasks.md` §3.1 makes `mvn -q install -DskipTests -pl marlo-web -am` the compile gate for **every**
task. `install` implies `package`, which is exactly the step the lock kills. Left unaddressed, all 17
remaining tasks would fail their compile gate **for a reason unrelated to the change under review** —
the textbook §3.3 *"disqualifies the evidence"* case, and the worst kind, because it looks like a code
failure. `DIRABS-T12` is affected twice over: `scripts/run-marlo-java17.sh` **deletes**
`marlo-{utils,data,web}/target` before building, which the same lock will block.

#### Leader process defect, corrected

The first run was invoked as `mvn ... | tail -60; echo "EXIT=$?"`. `$?` after a pipe reports **`tail`'s**
status, so the harness recorded **exit 0** on a build that had failed. The failure was caught only
because the Leader checked for build artifacts instead of trusting the exit code.

**Correction, binding on every brief in this spec:** any piped verification command must use
`set -o pipefail` and report `${PIPESTATUS[0]}`, or not pipe at all. An exit code laundered through a
pipe is not evidence. This is recorded because a silent false-green on a compile gate would have
invalidated every downstream task report in the run.

#### User decision — 2026-08-28: the compile gate is DEFERRED

The lock was put to the user with three options (kill PID 34448 · user closes VS Code / disables the
Java extension · deviate the gate to `mvn -q test-compile`). The user chose **none of them**:

> *"por ahora, no es necesario compilar. lo revisaremos más adelante"*

**Recorded consequence, and it is a reduction in evidence, not a neutral change.** MARLO is a compiled
monolith, and the root guides call the compile gate *"the authoritative gate — a broken build is the
failure that matters."* With it deferred, **no task in this run can be reported as compile-verified.**
Each task's evidence set falls back to:

| Gate | Status while the compile gate is deferred |
|---|---|
| `mvn -q install -DskipTests -pl marlo-web -am` | **DEFERRED** by user decision. Not run, not claimed |
| `mvn -q checkstyle:check` | **Runs.** Checkstyle parses source with its own parser, so it still catches syntax errors, the GPL header and the 120-char limit — but **no type errors**: a wrong signature, a missing import or a bad return type passes it |
| `grep` scope/isolation checks | **Run.** Unaffected |
| `git diff --stat` scope review | **Run.** Unaffected |
| Reviewer read of the diff | **Runs.** Carries more weight than usual precisely because the compile gate is absent |

**What is therefore genuinely unproven for every task until the gate is restored:** that the code
compiles. A `PASS` in this log during the deferral means *"checkstyle-clean, in scope, and
spec-conformant on review"* — it does **not** mean *"builds."* `DIRABS-T12` (the Spring context smoke
check) is **unreachable** while this stands, since it requires a WAR and `run-marlo-java17.sh` deletes
`target/` — which the same lock blocks.

This is tracked as an **open item that must be closed before the spec's Definition of Done can be
claimed**: `tasks.md` §10 requires `mvn -q install -DskipTests -pl marlo-web -am` green, and that box
cannot be ticked from this run's evidence.

---

## 3. Environment Blockers — spec-wide, both pre-existing

Recorded once here; every task entry references this section rather than restating it.

### EB-1 — The compile gate is deferred (user decision)

See §1.3 and the `T00.5` entry above. `mvn install` is not run in this spec run. **No task may be
reported as compile-verified.**

### EB-2 — The Checkstyle gate cannot execute, and would be weak even if it could

Discovered at `T01`. Two independent defects, both **pre-existing repo drift** and both outside this
spec's scope to fix:

**(a) The plugin cannot run.** `marlo-parent/pom.xml:827-833` pins `maven-checkstyle-plugin:2.9.1`
(2012) while forcing `com.puppycrawl.tools:checkstyle:8.18` (2019) as a plugin dependency. Plugin
2.9.1 calls `Checker.setClassloader(ClassLoader)`, a method modern Checkstyle removed. Verbatim:

```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-checkstyle-plugin:2.9.1:check ...
An API incompatibility was encountered while executing
org.apache.maven.plugins:maven-checkstyle-plugin:2.9.1:check:
java.lang.NoSuchMethodError: 'void com.puppycrawl.tools.checkstyle.Checker.setClassloader(java.lang.ClassLoader)'
```

It dies **before scanning any source file**, so its failure is evidence about the build, never about
the code under review.

**(b) Even when runnable, it would not gate.** Found independently by the `T01` Reviewer:
`configuration/marlo-checkstyle.xml` sets `severity=warning` globally with no submodule override,
while `maven-checkstyle-plugin:check` defaults to `violationSeverity=error`. A repaired plugin would
therefore report violations as **warnings and pass the build**.

**Why this matters beyond one task.** Root `CLAUDE.md` and `AGENTS.md` both list
`mvn -q checkstyle:check` as **"Required … a hard gate, not advisory."** In this checkout that claim
is **false on both counts** — the gate cannot run, and its configuration would not fail a build if it
did. Any future session reading the guides will over-trust a green Checkstyle run.

**Why it is not fixed here.** The fix edits `marlo-parent/pom.xml`, which is (i) a protected file
under `tasks.md` §3.2 (*"every `pom.xml`"*), (ii) a violation of `DIRABS-NF-004` (no dependency
change), and (iii) the exact reason `DEC-005` (Mockito) was deliberately declined — it would break
this child's parallel-safety with `auth-flow`.

**PENDING ITEM for the default branch (`staging`).** Per the Shared-File Write Discipline, this
session is on spec branch `staging-cognito-impl` and must not edit the root guides. Recorded for
application on `staging`:

| Pending edit | Target |
|---|---|
| Correct the Checkstyle row: it is **not** a runnable gate in this checkout; state the plugin/core incompatibility and the `severity=warning` weakness | root `CLAUDE.md` → *Agent-Lean Verification Commands* · `AGENTS.md` → same table |
| Consider a real remediation spec: upgrade `maven-checkstyle-plugin`, or align `checkstyle` core to what 2.9.1 supports, plus `violationSeverity` | new spec — **not** this one |

**Consequence for every remaining task in this spec:** the two required automated gates are both
unavailable. Style and compilability are audited by the **Reviewer reading the diff**, which is
weaker than a green build and is labelled as such in every entry below.

---

### `DIRABS-T01` (EXEC-030) — Value types: `DirectoryPerson`, `DirectorySource`, `DirectoryLookupException`

| Field | Value |
|---|---|
| **Status** | **PASS** |
| **Date** | 2026-08-28 |
| **Implementer attempts** | **1** — PASS on the first attempt, no rework |
| **Reviewer verdict** | **`STATUS: PASS`** |
| **Module** | `marlo-data` |
| **Skills assigned** | **none** — deviation from nothing; `tasks.md` assigns none and the Leader concurred. This is value-type authoring, not error-flow design; `error-handling-patterns` belongs to T02/T03/T04/T10 |
| **Effort** | `high` (one level above the T2 default) — these three types are the foundation all 16 later tasks build on, and an error in the enum count or the `notFound` signature would propagate through the whole spec |

#### Files changed — 3 new, 0 modified

```
marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/DirectorySource.java          (  91 lines, new)
marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/DirectoryPerson.java          ( 211 lines, new)
marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/DirectoryLookupException.java (  55 lines, new)
```

#### Attempt 1 — verification evidence

| Gate | Command | Result |
|---|---|---|
| Compile | *(not run)* | **DEFERRED — EB-1.** No compile claim is made for this task |
| Checkstyle | `mvn -q checkstyle:check` | **UNRUNNABLE — EB-2.** `NoSuchMethodError` before scanning any file. Reported as *inconclusive*, not as a pass |
| Isolation | `grep -rn "org.cgiar.ciat" .../security/directory/` | **EMPTY** ✅ — the new package does not know AD exists |
| Scope | `git status --short` / `git diff --stat` | **Exactly 3 new `.java` files in one new directory** ✅. No protected file (§3.2) present |
| Line length | `awk 'length>120'` (Leader-run) | **EMPTY** ✅. Reviewer measured the longest line at ~103 chars |

**The two required gates produced no evidence.** Per §3.3, that is an *inconclusive* verification, and
it is recorded as inconclusive rather than collapsed into a pass because two commands exited without
objecting.

#### What actually carries this task's verification

With both automated gates dead, the Reviewer's source read **is** the primary evidence, and it was
briefed to stand in for both. What it did:

- **Checked all 10 task STOP conditions at the source**, not from the diff. All hold.
- **Stood in for Checkstyle** by reading `configuration/marlo-checkstyle.xml` and checking all three
  files against every one of its 12 active modules, plus `ccafs-java-style-config.xml` for indent and
  continuation-indent behavior. Clean.
- **Stood in for the compiler** with a symbol-by-symbol type read: constructor arities and types at
  all six call sites, `@Override` signatures against real supertype methods, `char + String`
  concatenation in `mask`, same-class private-field access in `equals`, `<release>17</release>` plus
  `sourceEncoding=UTF-8` versus the non-ASCII em dashes in the Javadoc. Verdict: *"nothing that would
  fail to compile, and nothing I am unsure resolves."*
- **Verified the GPL header against repo precedent** rather than the `AGENTS.md` snippet, finding it
  **byte-identical to `LDAPAuthenticator.java:1-14`**. (The `AGENTS.md` template's trailing space on
  line 2 is incidental to the document; both variants exist in the tree.)

#### Requirements covered

| Requirement | How |
|---|---|
| `DIRABS-FN-003` — *"`source` must **NOT** be `null` on any path"* | Single private constructor with `Objects.requireNonNull(source, ...)`; both static factories route through it; class is `final`, no setters. **The type makes it unconstructible**, which is what the requirement asked for |
| `DIRABS-FN-005` *Not found* — *"leave `login`, `firstName`, `lastName` null rather than empty strings"* | `notFound` passes literal `null` for all three |
| **DD-3** — `notFound` takes a `source` | `notFound(String email, DirectorySource source)`. **Not** hardcoded to `NOT_FOUND` — this is the mechanism that lets T03 return `ERROR` on a backend failure and T10 discriminate it |
| **DD-3a** — propagation subtype | `DirectoryLookupException extends RuntimeException` directly. **Does not** extend `org.apache.shiro.authz.AuthorizationException`; no Shiro import anywhere in the package. Preserves the 500 rather than remapping to 403 via `struts.xml:543-545` |
| **DD-6** — immutable, no attribute map, masked `toString` | 6 final fields, no setters, `equals`/`hashCode` over all 6, `toString` routes `email` and `login` through `mask()`. No `attributes` map carried |
| `DIRABS-FN-004` — raw values | No transformation applied anywhere in the type; the class Javadoc states the invariant explicitly |
| `DIRABS-NF-006` / `NF-008` | GPL header byte-identical to repo precedent; clean against every active Checkstyle module; English-only identifiers and comments |
| `DIRABS-OPS-001` — source attribution | `DirectorySource` with 8 constants, each Javadoc'd as *introduced by this spec* (3) or *reserved for child 3* (5), reproducing design §4.2's column exactly |

#### Decisions made

| Decision | Rationale |
|---|---|
| Skills: **none** | Concurred with `tasks.md`. Loading `error-handling-patterns` here would have been cargo-cult — no error flow is authored in T01 |
| Effort: **`high`**, not the `medium` default | Foundation types for 16 downstream tasks; the STOP conditions are exact and a miss propagates |
| Accepted `toString` masking **only** `login` and `email` | DD-6, design §6.1's type table and the T01 task text each name exactly those two fields, three times, with no broader clause. Widening the mask would have been an **unspecified deviation**, not extra safety. Carried forward as an advisory instead |
| Accepted per-constant Javadoc over a separate doc artifact | No spec artifact named a separate file as a T01 deliverable |
| Accepted the exception message omitting the email | Design §6.1 requires the type to **carry** the email; it does, via the field and `getEmail()`. The message location is unspecified, and withholding it is consistent with DD-6's masking intent |

#### Implementer's `Not Done / Assumptions` — carried verbatim, then adjudicated

The Implementer's report declared four items. **Leader adjudication: none is scope still owed by T01.**

| Declared item | Adjudication |
|---|---|
| *"Checkstyle gate is UNVERIFIABLE in this environment, not passed … flagged to the Leader as an environment blocker affecting all future tasks"* | **Environmental, pre-existing. Not T01 scope.** Escalated correctly by the Implementer and promoted to **EB-2** above. It also correctly refused to edit the protected `pom.xml` to chase it |
| *"Compilation is unverified per explicit instruction … I make no compile claim"* | **The Leader's own instruction (EB-1). Not owed.** Refusing to claim an unrun gate is the correct behavior |
| Javadoc-level documentation rather than a separate doc file | **Benign interpretation.** Reviewer confirmed conformant |
| `toString` masks only `login`/`email`; names print in full | **Sent to the Reviewer with no Leader pre-judgment**, as the only item that could have been a real finding. Reviewer independently confirmed **conformant** and re-filed it as an advisory |

#### `ADVISORY` findings (4R lens) — recorded, non-gating, and they die here

Per the Advisory rules these never trigger rework and **may not become tasks or widen an existing
task**. Recorded for the archive/kaizen decision:

1. **RISK —** `firstName`/`lastName` print unmasked in `toString`, so a full corporate name still
   reaches log lines while the login next to it is masked. **This is exactly what the spec asked for
   and must not be changed under this task.** But DD-6's own rationale (*"corporate personnel data …
   will end up in log lines"*) arguably covers a full name too. **Tech-lead decision at archive/kaizen
   time.**
2. **RESILIENCE — forward-looking, affects T10. → recorded as a forward pointer, see below.**
3. **READABILITY —** `mask` is a private *static* placed alphabetically among the instance methods,
   while `found`/`notFound` sit above the fields. The repo's Eclipse sort-members convention would
   group the statics. No Checkstyle module covers declaration order; cosmetic.
4. **READABILITY —** `{@link #found}` is ambiguous between the `boolean` field and the static factory,
   and `DirectorySource`'s Javadoc opens at `<h2>` with no `<h1>`. Javadoc-tool nits only — the
   Reviewer confirmed **no `maven-javadoc-plugin` exists in any POM**, so nothing in the build
   evaluates them.

#### ⏭ FORWARD POINTER — must be copied into the `DIRABS-T10` brief

`DirectoryLookupException` declares only `(String email, Throwable cause)`. But **the cause is
structurally unavailable to the only consumer that throws it**: `LdapDirectoryService` (T03) logs the
throwable and returns `notFound(email, ERROR)`, so `DirectoryPerson` carries no cause forward. T10
will therefore have to write `new DirectoryLookupException(email, null)`.

That compiles and behaves correctly, but a T10 reviewer can easily read the `null` as an oversight.

**Leader ruling on how T10 handles it:** brief T10 to pass `null` **explicitly, with a short comment
naming this pointer**. Do **not** take the Reviewer's alternative suggestion of adding a single-arg
convenience constructor — `DirectoryLookupException` is a **T01** file, and editing it inside T10
would widen T10's approved file set. If that convenience constructor is genuinely wanted, it needs
user approval as a scope change, not an advisory acted on quietly.

#### Issues encountered

**EB-2 was discovered by this task** (Checkstyle unrunnable) and is recorded in §3 rather than here,
since it binds the whole spec. No code defect, no spec defect, no rework round consumed.

#### Final verification result

**PASS — with the honest qualifier that this run's `PASS` means *"spec-conformant on independent
source review, in scope, and style-clean by reading"*, and does NOT mean *"compiles"* or *"passed
Checkstyle."*** Both of those gates are unavailable (EB-1, EB-2) and neither is claimed.

Review rounds consumed: **1 of the ~20 budgeted.**

---
