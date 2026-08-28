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

### EB-1 — ~~The compile gate is deferred~~ → **SUPERSEDED at `T04`. Compilation IS available.**

> **CORRECTED 2026-08-28 at `T04`. The original text below was too broad and under-claimed the
> available evidence for four tasks. Read the correction first.**

**What was recorded:** *"`mvn install` is not run in this spec run. No task may be reported as
compile-verified."* That framing was derived from the `T00.5` baseline failure and the user's decision to
defer rather than resolve the file lock.

**What is actually true.** The lock only ever blocked the **`maven-war-plugin` packaging step**;
compilation itself succeeded even in the failed baseline run. `mvn test` does not package a WAR. At `T04`
this was tested rather than assumed:

```
$ export JAVA_HOME="C:/Program Files/Java/jdk-17"
$ mvn -q -pl marlo-web -am test
Results : Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
MVN_EXIT=0
```

Run **twice** — by the `T04` Implementer and independently by the Leader. `-am` builds
`marlo-utils` → `marlo-data` → `marlo-web`.

| Maven phase | Status | Why |
|---|---|---|
| `compile` / `test-compile` | ✅ **AVAILABLE** | Writes `target/classes`; the JDT lock does not prevent javac from writing the files it needs |
| `test` | ✅ **AVAILABLE** | No packaging involved |
| `package` / `install` (the WAR) | ❌ **BLOCKED** | `maven-war-plugin` cannot re-archive `AiAction.class` while the language server holds it |

#### Retroactive consequence — T01, T02 and T03 ARE compile-verified

The green run compiled **every production file this spec has added**: `DirectoryPerson`,
`DirectorySource`, `DirectoryLookupException`, `DirectoryService`, `LdapDirectoryService`. So the
qualifier attached to the T01/T02/T03 entries — *"does not mean it compiles"* — **is lifted as of
2026-08-28.** Those entries retain their original wording as the honest record of what was known when
they were written; this section is the correction, and it is the one to believe.

**What remains genuinely blocked, and it is exactly one thing:** `DIRABS-T12`, the Spring context smoke
check. It needs a deployable WAR *and* `scripts/run-marlo-java17.sh`, which deletes
`marlo-{utils,data,web}/target` before building — both blocked by the same lock. T12 is the **only**
evidence for `D8` (Spring wiring), so this remains a real gap; it is now a narrow one rather than a
spec-wide one.

**`tasks.md` §10's Definition of Done still cannot be fully ticked** — it names
`mvn -q install -DskipTests -pl marlo-web -am`, and `install` is the blocked phase. Closing it needs the
lock released (reload the VS Code window with the Java extension disabled, or stop the language server).

**Leader process note.** This is the second assumption in this run recorded as a standing limit without
being probed (the first was T03's *"the `LDAPService` constructor might throw"*, falsified by
decompiling the jar). Both cost real evidence quality while they stood. `.agents/leader.md` →
*Deferring a check (test the assumption first)* exists precisely for this, and twice it was applied only
after the fact. **Flagged for Kaizen.**

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

### `DIRABS-T02` (EXEC-031) — the `DirectoryService` interface

| Field | Value |
|---|---|
| **Status** | **PASS on attempt 2** (of a 3-attempt ceiling) |
| **Date** | 2026-08-28 |
| **Module** | `marlo-data` |
| **Skills assigned** | `error-handling-patterns` — concurred with `tasks.md`. The whole artifact is a never-throws contract plus the `NOT_FOUND` / `ERROR` discrimination; the skill is load-bearing here, not ceremony |
| **Effort** | attempt 1 `medium` → attempt 2 **`high`** (rework rule: bump one level per retry) |

#### 🔧 SPEC DEFECT found and corrected during this task — `tasks.md` miscounted §5.1

The Implementer reported that `tasks.md` T02 demanded *"all six rows of design §5.1"* while §5.1's
table has only **five** rows. **Verified at the source by the Leader, and independently re-verified by
the Reviewer** (`design.md:218-224` = 5 rows · `:226` = the invariants line · `:228` = a separate
no-network-call clause).

This is a **documentation off-by-one in `tasks.md`, not a design error** — §5.1's content is complete
and correct; only the count describing it was wrong. It is therefore a correction, **not a Pivot**: no
requirement or design decision is overturned, and no scope changes.

**Two-direction sweep per the Correction Closure rule — the miscount was at FOUR sites, not one:**

| Site | Was | Now |
|---|---|---|
| `tasks.md:144` (T02) | "all six rows of design §5.1" | "all five rows of design §5.1's table, plus the three invariants and the no-network-call clause" |
| `tasks.md:196` (**T04**) | "Encodes all six rows of design §5.1" | "all five rows of design §5.1's table, the three invariants, and the no-network-call clause" |
| `tasks.md:532` (Testing Plan) | "All six §5.1 rows" | "All five §5.1 table rows, the three invariants, the no-network-call clause" |
| `design.md:290` | "Encodes all six rows of §5.1" | "all five rows of §5.1's table, plus the three invariants and the no-network-call clause" |

**The three sites beyond the one reported are the whole justification for the sweep.** Correcting only
the cited site would have left **T04** — the contract test, this spec's dominant gate — demanding a
sixth row that does not exist, and that error would have surfaced as a wasted review round two tasks
later. Post-sweep grep for `six rows|six §5` across the spec folder returns **empty**.

Not touched: the `six` in `family.md` and `analysis/**` refers to the **six Capability B candidates**, a
different and correct count.

#### ⚠️ Leader-caused defect — recorded against the Leader, not the Implementer

**The Leader's attempt-1 brief passed down the wrong count.** The Implementer, given "six rows" against
a five-row source, resolved the conflict by promoting the no-network-call clause to a sixth numbered
*outcome*. That produced the exact defect the Reviewer then failed the task on. The Implementer's
handling was correct in every respect available to it: it detected the discrepancy, stated its reading
explicitly in `Not Done / Assumptions`, and flagged it for adjudication rather than silently choosing.

**This FAIL is attributable to the Leader's brief.** It is recorded here because a rework attempt
charged to an Implementer that did the right thing would misrepresent the run — and because the
attempt-2 brief must therefore carry the *corrected* count rather than ask the worker to re-derive it.

The Reviewer was told, at dispatch, that the Leader had caused the framing and was instructed **not to
soften its verdict for that reason**. It did not. That is the `author ≠ auditor` gate working as
designed: independence that survives the orchestrator's own admission of error is worth more than
independence that has never been tested against it.

#### Attempt 1 — verification evidence

| Gate | Result |
|---|---|
| Compile | **DEFERRED — EB-1.** Not run, not claimed |
| Checkstyle | **UNRUNNABLE — EB-2.** Not run, not claimed. Known blocker was passed *into* the brief so the Implementer would not spend a second task rediscovering it |
| Isolation | `grep "org.cgiar.ciat"` → **EMPTY** ✅ |
| Scope | `git status --short` → **exactly 1 new file** ✅ (the 2 modified spec docs are the Leader's correction sweep) |
| Line length | `awk 'length>120'` → **EMPTY** ✅ (Reviewer measured the longest at ~106) |

#### Attempt 1 — Reviewer verdict: `STATUS: FAIL`, 1 issue (recorded verbatim)

> **1. Discovered Issue:** The type-level Javadoc asserts *"`findByEmail` resolves to exactly one of
> the following six outcomes"* over a six-item `<ol>` in which **item 6 is not an outcome**. It is a
> constraint on item 1, and its own text says so — *"Outcome 1 (a `null` or blank `email`) is reached
> with **no network call**"*. Because items 1 and 6 necessarily co-occur, *"exactly one of the
> following six"* is a false mutual-exclusivity claim: on the `null`-email path two of the six
> enumerated items hold simultaneously, and item 6 can never hold alone. The available misreading is
> concrete and damaging — a reader can conclude the contract defines **two distinct** `null`-email
> outcomes, one that makes a network call and one that does not, when §5.1 defines one outcome and
> forbids the call unconditionally. The method-level Javadoc propagates the error and adds a second
> inaccuracy, referring readers to *"the full six-outcome **table**"* when the type-level construct is
> an ordered list, not a table (the only table is §5.1's, and it has five rows). This is not cosmetic
> here: `design.md:254` makes this Javadoc *"the contract"*, `DirectoryServiceContractTest` (T04)
> enumerates its cases from §5.1, and child 3's provider inherits this file verbatim — a wrong
> cardinality in a contract outlives the task that wrote it.
>
> **Violated Rule:** `design.md` §5.1 (`:218-228`) and §6.1 (`:254`) — *"`DirectoryService` |
> interface, 1 method | Javadoc **is** the contract (§5.1). Every clause of §5.1 stated explicitly."* A
> contract whose enumeration contradicts itself does not state §5.1's structure; it restates it with a
> false quantifier.
>
> **Remediation Suggestion:** Purely local to the Javadoc; no signature, structure or member change.
> (a) Make the `<ol>` **five** items — the five §5.1 rows, which items 1-5 already mirror exactly — and
> change the lead-in to *"resolves to exactly one of the following five outcomes."* (b) Delete item 6
> and re-state it as its own clause after the invariants paragraph, mirroring §5.1's own shape. (c) In
> the method Javadoc, replace *"the full six-outcome table"* with *"the full five-outcome contract"*.
> Keep the `NOT_FOUND` / `ERROR` paragraph and the invariants paragraph exactly as written.

**Leader adjudication: FAIL upheld, in scope, rework warranted.** The finding is a genuine §5.1/§6.1
conformance violation rather than a style preference, because §6.1 makes this Javadoc *the contract*
and T04 plus child 3 both inherit it. The remediation is local to prose in one file — cheap to apply
and expensive to leave.

#### Attempt 1 — what the Reviewer confirmed clean

Recorded so attempt 2 does not disturb work that already passed: exactly one method with the mandated
`DirectoryPerson findByEmail(String email)` signature · **no `throws` clause** · no annotation, constant
or default method · no `org.cgiar.ciat` import (same-package types need none) · GPL header
byte-consistent with `AGENTS.md:40-53` *including that template's own `at your option)` typo* · all
`@link` targets resolve · all HTML tags balanced · every active Checkstyle module satisfied · **the
`NOT_FOUND` / `ERROR` paragraph, which the Reviewer called "the strongest part of the file"** — it
states the asymmetry *and* why collapsing it yields a false statement, which is exactly what T10
depends on.

Assumption 1 (**no `@author` tag**) was audited and resolved as **correct**: `marlo-checkstyle.xml` has
no `JavadocType`/`authorFormat` module, `AGENTS.md`'s template carries no `@author`, and three siblings
in the same new package have none — in-package precedent rightly beat a single older cross-package
exemplar.

#### `ADVISORY` findings from attempt 1 — recorded, non-gating, and they die here

Neither may be folded into attempt 2; the Reviewer explicitly scoped the invariants paragraph out of
the fix, and an advisory may not widen a task.

1. **READABILITY —** `{@link DirectoryPerson#found}` / `#notFound` carry no parameter list, and
   `DirectoryPerson` has both a private `boolean found` field and a static `found(...)` factory, so bare
   `#found` is ambiguous and may bind to the field. **Pre-existing precedent, not new drift** — the
   committed T01 sibling uses the same bare form (`DirectoryPerson.java:26`), and no `maven-javadoc-plugin`
   exists in any POM, so no gate evaluates it.
2. **READABILITY —** *"Invariants … with no exception"* uses "exception" in its English sense two words
   from a never-throws contract. §5.1's own *"on every path"* avoids the collision.

#### Attempt 2 — the fix

Effort bumped `medium` → **`high`** per the rework rule. The Reviewer's FAIL report was passed
**verbatim**, with an Attempt History line (*"attempt 1 promoted the no-network-call clause to a sixth
outcome — do not repeat"*), the **corrected** row count so the worker did not re-derive it, and an
explicit DO-NOT-TOUCH list covering everything attempt 1 had already passed.

Three edits, exactly as the remediation specified, and nothing else:

| | Attempt 1 | Attempt 2 |
|---|---|---|
| **(a)** lead-in + list | *"exactly one of the following **six** outcomes"*, `<ol>` of 6 | *"…**five** outcomes"*, `<ol>` of 5 |
| **(b)** no-network-call | Item 6 **inside** the enumeration | Its own paragraph **after** the invariants: *"**Outcome 1 makes no network call.** A `null` or blank `email` must fail fast, before any bind or connection to the backend."* |
| **(c)** method Javadoc | *"the full six-outcome **table**"* | *"the full five-outcome **contract**"* |

The Javadoc now **mirrors §5.1's own structure and ordering** — five-row table, then invariants, then
the no-network-call clause. The Reviewer called this *"a structural improvement over attempt 1, not
merely a deletion."*

Implementer's `Not Done / Assumptions`: **none.**

#### Attempt 2 — verification evidence

| Gate | Result |
|---|---|
| Compile | **DEFERRED — EB-1.** Not run, not claimed |
| Checkstyle | **UNRUNNABLE — EB-2.** Not run, not claimed |
| Isolation | `grep "org.cgiar.ciat"` → **EMPTY** ✅ (no imports at all; the linked types are same-package) |
| Scope | `git status --short` → **1 new source file** ✅ |
| Line length | `awk 'length>120'` → **EMPTY** ✅ (Reviewer measured longest at ~105) |
| HTML balance | `ol` 1/1 · `li` 5/5 · `b` 4/4 · `em` 2/2 · 6 conventional unclosed `<p>` ✅ — independently re-counted by the Reviewer |

#### Attempt 2 — Reviewer verdict: `STATUS: PASS`

> The false quantifier is resolved — the enumeration now states design §5.1's five table rows as five
> mutually-exclusive outcomes, with the no-network-call constraint moved out of the list into its own
> paragraph in §5.1's own order, and the method Javadoc updated to match. Every DO-NOT-TOUCH item
> verified intact at the source, all STOP conditions hold, and reading-based compile and style checks
> are clean.

The Reviewer was asked three ordered questions and answered all three: **(1)** FAIL resolved — yes;
**(2)** no regression, each DO-NOT-TOUCH item *verified at the source rather than accepted on the
Implementer's word*; **(3)** no new defect from the relocated prose — and it noted the relocation is a
**precision gain**, because scoping the guarantee to outcome 1 matches `requirements.md:187` and
`design.md:228` exactly and avoids over-promising no-network behavior for a *malformed* email.

#### ⚠️ Reviewer-to-Leader handoff — a verification the Reviewer could not perform, and did not fake

The Reviewer stated plainly:

> *"**Caveat on method:** with no `Bash` I verified this by content coherence and `@link` resolution,
> not by `git diff` — a byte-identity claim is the Leader's to confirm."*

This is the `tools: Read, Grep, Glob` allowlist working as designed: the Reviewer has no `Bash`
precisely so it cannot mutate what it audits, and the honest consequence is that byte-identity is
outside its reach. **It flagged the limit instead of asserting a check it could not run.**

**Leader confirmation, run inline:**

```
$ git diff 6342520abf -- .../DirectoryPerson.java .../DirectorySource.java .../DirectoryLookupException.java
(no output)
```

**Empty → the three T01 files are byte-identical to their commit `6342520`.** Confirmed.

Full file set touched since the approval commit `4b0bf72`: 3 spec documents + the 4 `.java` files of the
new `security/directory/` package. **No protected file (§3.2) appears anywhere** — no `pom.xml`, no
`APConstants.java`, no `global.properties`, no `struts*.xml`, no migration.

#### Requirements covered

| Requirement | How |
|---|---|
| `DIRABS-FN-001` *"exactly one method"* | One abstract method, `DirectoryPerson findByEmail(String email)`. No default method, no constant, no annotation |
| `DIRABS-FN-002` *"**MUST NOT** propagate an exception under any input or backend condition"* | No `throws` clause — the contract is enforced at type level, not merely documented. Javadoc states *never throws* explicitly |
| `DIRABS-FN-002` *"must **NOT** throw, and must **NOT** return `null`"* | Both stated in the type-level invariants paragraph and restated on the method |
| `DIRABS-FN-002` no-network-call clause | Its own paragraph, scoped to outcome 1 only |
| `DIRABS-FN-003` *"`source` must **NOT** be `null` on any path"* | Stated in the invariants and in the `@return` tag |
| **The `NOT_FOUND` / `ERROR` distinction** (an explicit STOP condition) | A dedicated paragraph the Reviewer called *"the strongest part of the file"* in attempt 1 and confirmed intact in attempt 2. States the asymmetry — `NOT_FOUND` **asserts knowledge**, `ERROR` **asserts the absence of knowledge** — and why collapsing them produces a false statement. T10 is the sole consumer that reads `source` and depends on this |
| `DIRABS-NF-006` / `NF-008` | GPL header matching `AGENTS.md:40-53` including the template's inherited `at your option)` typo (deliberately not "fixed" — matching precedent beats correcting it unilaterally). English only. Clean against every applicable Checkstyle module |
| `DIRABS-ARCH-001` | The seam that makes a provider swap *one bean + one value*; §6.1 designates this Javadoc as **the contract** future providers inherit |

#### `ADVISORY` from attempt 2 — recorded, non-gating, dies here

**READABILITY —** the relocated clause refers into the `<ol>` **positionally** (*"**Outcome 1** makes no
network call"*). Since this Javadoc *is* the contract (`design.md:254`), a future reorder of the list
would silently falsify the clause; a self-describing lead-in (*"A `null` or blank `email` (outcome 1)
makes no network call"*) would survive reordering. The Reviewer explicitly judged it **"not worth a
third attempt — record only,"** and the Leader concurs: spending the last of three attempts on a
robustness-to-hypothetical-edit preference would be exactly the misuse the attempt ceiling guards
against.

#### Decisions made

| Decision | Rationale |
|---|---|
| Corrected the §5.1 row count in the **spec** rather than bending the code to `tasks.md`'s wrong number | The design was right and the task file miscounted it. Fixing the count at all four sites is cheaper than propagating a wrong contract into T04 and child 3 |
| Treated it as a **correction, not a Pivot** | No requirement or design decision is overturned and no scope changes — §5.1's content was always complete. A Pivot Record would overstate what happened |
| Charged the attempt-1 FAIL to the **Leader's brief**, not the Implementer | The Implementer detected the discrepancy, declared it, and escalated rather than silently choosing. Recording it otherwise would misrepresent the run |
| Declined the attempt-2 advisory | Reviewer's own recommendation, and correct on the attempt budget |

#### Final verification result

**PASS on attempt 2.** Same honest qualifier as every task in this run: this means *"spec-conformant on
independent source review, in scope, and style-clean by reading."* It does **not** mean *"compiles"* or
*"passed Checkstyle"* — both gates are unavailable (EB-1, EB-2) and neither is claimed.

Review rounds consumed: **3 of the ~20 budgeted** (T01: 1 · T02: 2). On track — `design.md` §9
deliberately budgeted extra FAILs because equivalence review here is line-by-line.

---

### `DIRABS-T03` (EXEC-032) — `LdapDirectoryService`

| Field | Value |
|---|---|
| **Status** | **PASS on attempt 2** — *on inspection only; see the qualifier below* |
| **Date** | 2026-08-28 |
| **Module** | `marlo-data` · **File:** `security/directory/impl/LdapDirectoryService.java` (new, 103 lines) |
| **Skills** | `error-handling-patterns` — concurred; this is a never-throws contract with a two-way failure discrimination and a conditional log |
| **Effort** | `xhigh` on **both** attempts. **Deliberately not bumped on retry:** the rework rule says raise one level, but the next level is `max` and the tier rule forbids `max` on a T2 tier. `xhigh` is the ceiling there — needing more would mean escalating the *tier*, not the dial |
| **Review rounds** | **1** (attempt 1 was never reviewed — see below) |

#### 🔧 SPEC GAP found and closed mid-task — `FN-002` asserted an outcome with no definition and no mechanism

**Attempt 1 was never sent to the Reviewer.** Its `Not Done / Assumptions` surfaced that `FN-002`
*Invalid input* required `malformed → NOT_FOUND` while **never defining "malformed"** and never naming a
mechanism. Its implementation therefore let a malformed email fall through to
`LDAPService.searchUserByEmail`, which yields **`ERROR`** if `adauth` throws.

**Why that was a regression rather than a mere gap.** `ERROR` is the one value
`center/…/ManageUsersAction` reads (DD-3 / DD-3a): on `ERROR` it throws `DirectoryLookupException`,
surfacing as a **500**. Today the same admin typo is caught and reported as
`manageUsers.email.doesNotExist`. The fall-through would have turned a clear message into a server
error — the *opposite* direction from this spec's equivalence goal, and invisible until someone typed a
bad address in production.

**Why the loop was stopped instead of consuming a review round.** The command's Pivot/error handling is
explicit: evidence that the spec itself is wrong or unviable stops the loop, and rework attempts are
not spent on a broken spec. A Reviewer would have returned a correct FAIL against a requirement **no
implementation could satisfy without a human decision**. Escalated to the HITL gate instead.

**Resolution — new `design.md` **DD-11**, approved 2026-08-28.** Keep the lookup; discriminate inside
the catch. `isWellFormed` is minimal by design (one `@`, non-empty local part, domain containing a `.`;
explicitly **not** RFC 5322). Rejected alternatives are recorded in DD-11 and in `requirements.md` §13.

**Amendment sweep — five sites:**

| Site | Change |
|---|---|
| `requirements.md` `FN-002` *Invalid input* | Defines "malformed"; specifies the catch-discrimination mechanism and why not pre-validation |
| `requirements.md` `FN-002` *Backend failure* | The `error` log **must not** fire for a malformed-email failure |
| `design.md` §5.1 row 2 | Annotated: reached by discriminating inside the catch, see DD-11 |
| `design.md` **DD-11** (new) | Full decision, code, rejected alternatives, implications for T03/T04/T10 |
| `tasks.md` T03 scope | The two-branch catch + the `isWellFormed` bounds |
| `tasks.md` **T04** | **A mandatory new assertion** — malformed + backend throws → `NOT_FOUND`, **not** `ERROR` |

**The T04 row is the one that matters most.** Without it DD-11 would have had **no gate at all**. T04's
*falsifying input* table now names the defect explicitly: an implementation returning `ERROR` for a
malformed email must FAIL — *"which is exactly what T03's first attempt did, and what DD-11 exists to
prevent."* The defect is now converted into a test that catches its return.

#### ⚠️ Second Leader-caused brief defect — recorded

**The gap was reachable because the Leader's attempt-1 brief pointed at §4.3, DD-3 and DD-4 but never
quoted `FN-002`'s *Invalid input* scenario**, which resolves the question outright. The Implementer's
own reasoning was sound — it declined to *"invent validation semantics `adauth` doesn't have"*, which is
DD-4's logic applied correctly — and it was incomplete only because the governing text was not in front
of it.

This is the **second** brief defect in this run (T02's was the §5.1 row miscount). Both were caught by
an Implementer that declared its reading rather than choosing silently. **Pattern worth naming for
Kaizen: the Leader's pointer briefs have twice omitted the requirements scenario that settles the
task's central ambiguity, while including the design sections.** Design tells the worker *how*;
requirements tell it *what must be true*. Pointing at only the former is what produced both defects.

#### Attempt 2 — verification evidence

| Gate | Result |
|---|---|
| Compile | **DEFERRED — EB-1.** Not run, not claimed |
| Checkstyle | **UNRUNNABLE — EB-2.** Not run, not claimed |
| Isolation | `grep -rln "org.cgiar.ciat" security/directory/` → **exactly one file**, `impl/LdapDirectoryService.java` ✅ — independently re-run by the Reviewer, which also confirmed that under JD-1's `^import` gate it is exactly one |
| Scope | `git status --short` → **1 new source file** ✅ |
| Line length | Reviewer independently ran `^.{121,}$` across the whole package → **no matches** ✅ (verified, not accepted on report) |
| Committed siblings | Leader-run `git diff e8d5d9e -- <4 files>` → **empty** ✅ all four T01/T02 files byte-identical |
| Full change set since `4b0bf72` | 4 spec docs + 5 `.java` files in the new package. **No protected file (§3.2) anywhere** ✅ |

#### Attempt 2 — Reviewer verdict: `STATUS: PASS`

> The field mapping is a faithful, transformation-free transcription of §4.3,
> `setInternalConnection(!config.isProduction())` is exactly equivalent to the baseline's if/else, and
> all four DD-11 clauses hold — the lookup stays unconditional, discrimination lives inside the catch,
> `isWellFormed` is consulted only on the failure path and is unreachable with `null`, and the `error`
> log fires only on the well-formed branch. **This PASS means the mapping is correct on inspection, not
> that it was executed.**

**Verification the Reviewer performed independently rather than accepting on report** — worth recording,
because with both automated gates down this is what carries the task: it resolved
`APConfig.isProduction()` at `marlo-utils/.../APConfig.java:849` to a **primitive `boolean`**, proving
`!this.config.isProduction()` carries no unboxing NPE and reads the flag exactly once as the baseline
does; confirmed `DirectoryPerson.found(...)`'s **positional argument order** against the call site;
confirmed every `LDAPUser` getter resolves against live usage at `SearchUserAction:211-214`; and swept
`isWellFormed`'s boundary cases (`"@x.org"`, `"a@@b.c"`, `"a@b"` → false; `"a@b.c"`, leading/trailing
space → true).

**On the damaging-misclassification risk the brief asked it to hunt:** it found **none from an
implementation defect.** The only inputs yielding `NOT_FOUND` on a genuine outage are single-label-domain
addresses (`user@intranet`) — and that is DD-11's own minimal definition as adjudicated at the gate, not
a coding choice. The predicate is a faithful transcription of it, and its errors lean toward `true`,
which is the safe direction. It also confirmed `isWellFormed` is **unreachable with `null`** (line 61
returns before the `try`), so its `@param` contract is honored rather than merely asserted.

It ruled the class-level Javadoc edit **in scope, not drift**: the file is new so there is no prior text
to drift from, and after DD-11 a class doc describing only the `ERROR` addition would be *inaccurate*.

#### ⚠️ Reviewer-to-Leader handoff — again declared, not faked

> *"I have no `Bash`, so `git status` is not independently reproducible by me … 'unmodified since
> commit' rests on the Leader's `git status --short`."*

Second time this run the Reviewer has named a limit of its `Read, Grep, Glob` allowlist instead of
asserting a check it could not run. **Leader confirmation is recorded in the evidence table above** —
`git diff e8d5d9e` over all four committed siblings returned empty.

#### `ADVISORY` findings — recorded, non-gating. Two are consequential and are promoted to forward pointers.

**1. RELIABILITY → ⚠️ RAISED, THEN FALSIFIED BY PROBE. NOT a defect and NOT a limit. Corrected below.**

**What the Reviewer raised.** `new LDAPService()` (line 65) and `setInternalConnection(...)` (line 66)
sit **outside** the `try` — where DD-11's snippet and the baseline put them — so an exception from either
would **escape `findByEmail`**, breaching the unconditional *never throws* invariant. The Reviewer
inferred a `FN-002` / `NF-001` tension (never-throws vs. observable equivalence) and a consequence for
T04: that `LdapDirectoryServiceTest`, constructing a **real** `LDAPService`, would **error out** in a CI
or dev environment lacking AD configuration.

**The Leader initially recorded that as an accepted limit. That was wrong, and it was wrong for a
diagnosable reason: the premise was never tested.** `.agents/leader.md` → *Deferring a check (test the
assumption first)* requires one bounded probe before recording any assumption-based deferral. Applied
late, it took two minutes:

```
$ javap -c -p -cp adauth-5.7.jar org.cgiar.ciat.auth.LDAPService

public org.cgiar.ciat.auth.LDAPService();
  0: aload_0
  1: invokespecial  java/lang/Object."<init>":()V
  4: aload_0
  5: iconst_1
  6: putfield       internalConnection:Z
  9: return

public void setInternalConnection(boolean);
  0: aload_0
  1: iload_1
  2: putfield       internalConnection:Z
  5: return
```

Plus: **no static initializer** (`static {}` count = 0), and `private boolean internalConnection` is the
class's only field.

**Findings, all three of which change the conclusion:**

| Claim | Verdict |
|---|---|
| `new LDAPService()` can throw | **FALSE.** `super()` plus one boolean field write. No config read, no network, no I/O, no class-load work that could fail |
| `setInternalConnection(boolean)` can throw | **FALSE.** A single `putfield` |
| A `FN-002` / `NF-001` tension exists here | **FALSE — there is nothing to trade.** Nothing before the `try` can throw, so `findByEmail`'s never-throws invariant **holds unconditionally as written.** T03 is *stronger* than the advisory suggested, not weaker |

**Corrected record: there is no accepted limit, no deviation, and nothing for a reader to be warned
about.** The earlier "accepted tension" text was a Leader error and is struck. The Reviewer's caution was
reasonable from the diff alone — it has no `Bash` and could not decompile the jar — and raising it was
right; the failure to probe it before recording it as a standing limit was the Leader's.

**What survives for T04, on a different and simpler basis.** A test still cannot substitute the backend,
because `findByEmail` constructs `LDAPService` internally — but the reason is plain injectability, **not**
a throwing constructor. And the probe removes the design constraint that made this awkward:

- `LDAPService` is **not `final`**; `searchUserByEmail(String)` and `setInternalConnection(boolean)` are
  **public and non-final** → a test subclass can override them, and `super()` is provably safe.
- Because the constructor cannot throw, **its position relative to the `try` is semantically
  irrelevant** — so a seam may be introduced without any equivalence argument to answer.

Recorded as a forward pointer to T04 on that corrected basis. See the T04 entry for the decided design.

**2. RISK → PENDING DECISION, raised to the user at this gate.**
`DirectoryService`'s **committed** Javadoc still lists outcome 2 (*malformed → `NOT_FOUND`*) and outcome
5 (*backend throws → `ERROR`*) as flat alternatives, with **nothing stating that outcome 2 wins when
both apply**. DD-11 invented that precedence and its *Implications* names T03, T04 and T10 — **but not
T02.**

**This is an incompleteness in the Leader's own DD-11 sweep.** The Correction Closure rule requires
grepping references *to* the corrected section; §6.1 designates `DirectoryService`'s Javadoc as **the
contract**, so amending §5.1 row 2 should have flagged it. The Reviewer correctly notes the Implementer
was right to leave it alone — modifying a committed sibling is a T03 STOP condition. Not fixed
unilaterally, because editing a T02 deliverable outside T02 is a boundary the Leader should not cross on
its own judgment. **Presented to the user for a decision.**

**3. READABILITY —** line 85 uses Markdown `**only**` inside Javadoc where the package convention (and
every sibling) is HTML; it renders as literal asterisks. Cosmetic; no Checkstyle module inspects Javadoc.

**4. READABILITY —** the class Javadoc's *"reproduces `getOutlookUser`'s observable behavior exactly,
with **one** deliberate addition"* undercounts: there are **three** divergences — the `ERROR` + log path,
the malformed → `NOT_FOUND` branch, and the **null/blank fail-fast with no network call**, which the
baseline lacked and the doc never mentions. The Reviewer notes DD-3a already corrected this same unearned
*"exactly"* in `design.md`, and this phrasing reintroduces it in miniature.

#### Final verification result

**PASS on attempt 2 — on inspection, not on execution.** The strongest qualifier of any task so far, and
it is the spec's own: `tasks.md` T03 states *"compile + checkstyle passing says **nothing** about the
mapping. This task's real gate is T04, and it must not be reported verified before T04 runs."* Both
gates are additionally unavailable (EB-1, EB-2). **T03 is not behaviorally verified and must not be
reported as such until T04 executes against it.**

Review rounds consumed: **4 of the ~20 budgeted** (T01: 1 · T02: 2 · T03: 1).

---

### `DIRABS-T04` (EXEC-033) — Contract test, fake, and LDAP implementation test

| Field | Value |
|---|---|
| **Status** | **PASS on attempt 1** |
| **Date** | 2026-08-28 |
| **Module** | `marlo-web` (test) + two approved edits in `marlo-data` |
| **Skills** | `error-handling-patterns`, `tdd` — both per `tasks.md`; concurred |
| **Effort** | `xhigh`. Not `max`: the tier rule forbids it on T2, and escalating the tier would put the Implementer on the Reviewer's model and collapse `author ≠ auditor` |
| **Significance** | **This task produced the spec's first executable evidence.** T01–T03 were PASSed on inspection only |

#### 🟢 THE HEADLINE — `mvn test` runs green, and it was run twice

```
$ export JAVA_HOME="C:/Program Files/Java/jdk-17"
$ mvn -q -pl marlo-web -am test

Running org.cgiar.ccafs.marlo.data.model.ProjectPartnerTest          Tests run: 1,  Failures: 0, Errors: 0
Running org.cgiar.ccafs.marlo.rest...projectPage.ProjectPageItemTest  Tests run: 1,  Failures: 0, Errors: 0
Running org.cgiar.ccafs.marlo.security.directory.LdapDirectoryServiceTest
                                                                     Tests run: 8,  Failures: 0, Errors: 0
Running org.cgiar.ccafs.marlo.utils.URLShortenerTest                 Tests run: 3,  Failures: 0, Errors: 0

Results : Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
MVN_EXIT=0
```

Run by the Implementer and **independently re-run by the Leader** in the quiet window after the worker
reported. Both green. **This also supersedes EB-1** — see §3, EB-1, corrected.

MARLO had **3** test files before this task, one with its only test body commented out. It now has 6, and
**8 of the 13 tests are this spec's.**

#### Files

**New — 3 test classes in `marlo-web/src/test/java/.../security/directory/`:**

```
DirectoryServiceContractTest.java   195 lines  (abstract, provider-agnostic, reused verbatim by child 3)
LdapDirectoryServiceTest.java       159 lines  (extends it; supplies the LDAP stubs)
FakeDirectoryService.java           105 lines  (hand-rolled DirectoryService double for T06–T10)
```

**Modified — the two HITL-approved widenings** (`design.md` DD-12 and DD-11 *Implications*; T03's STOP
conditions would otherwise forbid both). Reviewer confirmed each is exactly minimal:

| File | Edit |
|---|---|
| `impl/LdapDirectoryService.java` | `new LDAPService()` → `this.newLdapService()`, plus the 3-line `protected` factory. **The call stayed at line 65, outside the `try` that opens at 68** — same position as before |
| `DirectoryService.java` | One Javadoc paragraph: outcome 2 (malformed) **takes precedence over** outcome 5 (backend throws) when both apply |

Reviewer verified byte-for-byte that the null/blank fail-fast, `setInternalConnection(!isProduction())`,
the raw `found(...)` mapping, the DD-11 catch discrimination and `isWellFormed` are all intact.

#### ✅ The disqualifier was met by mutation, not by argument

`tasks.md` T04's disqualifier: *"a green suite that never exercised the exception path [is disqualified].
**The contract test must be able to fail** — if every assertion passes against a deliberately broken
stub, the test is tautological and proves nothing."*

**The Implementer broke production code, observed red, and reverted with a verified zero-diff:**

| Mutation applied to `LdapDirectoryService` | Observed |
|---|---|
| Well-formed exception branch returns `NOT_FOUND` instead of `ERROR` | `AssertionError: expected:<ERROR> but was:<NOT_FOUND>` — 1 failure, rest green |
| `isWellFormed` check removed; catch always returns `ERROR` — **T03's original bug restored** | `AssertionError: DD-11: malformed input wins over a backend failure expected:<NOT_FOUND> but was:<ERROR>` — exactly 1 failure |

**The Reviewer did not accept this on report.** It traced all 7 contract tests against
`LdapDirectoryService`'s actual control flow and **independently derived both mutation outcomes**,
confirming the counts match exactly ("1 failure, rest green" / "exactly 1 failure").

**Consequence that matters beyond this task: DD-11 now has a proven gate.** The defect discovered at T03
— and the 500-instead-of-a-message regression it would have caused through T10 — is now caught by a test
that demonstrably goes red when it returns.

**Assertions the two mutations did not cover, which the Reviewer checked by construction instead:**

- **Fail-fast (assertions 1/2/4):** remove the null/blank guard and `findByEmail("   ")` reaches the
  throwing stub → counter becomes 1 → `assertEquals(0, …)` fails; `findByEmail(null)` NPEs inside
  `isWellFormed` and escapes → test errors. **Red either way.**
- **The counter is wired to the real path** — `LdapDirectoryService` calls `searchUserByEmail` on the
  stub, and `createFailingService()` resets the count per test.
- **Raw fields (assertion 6):** `"JSmith"` / `"Jane"` / `"Smith"` are three **distinct** values asserted
  with exact `assertEquals`, so a lowercasing **or a slot-swapping** mapping fails. Satisfies §6.3's
  *"why `login = "JSmith"` specifically"*.

#### ⚠️ Finding worth carrying forward: one required assertion is structurally unfailable

**Assertion 10 (`assertNotNull(person.getSource())`) cannot fail.** `DirectoryPerson`'s private
constructor already performs `Objects.requireNonNull(source, …)` (`DirectoryPerson.java:85`), so an
instance with a null source is unconstructible. The assertion is mandated verbatim by `tasks.md` T04 and
is harmless, but the record should be accurate:

> **`DIRABS-FN-003`'s real gate is the T01 type's `requireNonNull`, not these seven assertions.**

That is the stronger guarantee — enforced by the type system rather than by test coverage — but the
coverage matrix should not read as though the tests are what secure it. *(The Reviewer attributed the
`requireNonNull` to T02; it is in fact T01's `DirectoryPerson`. The substance is unaffected.)*

#### DD-9 — reusability audited on shape, not just on absence

The Leader verified mechanically that the abstract class's only `org.cgiar.ciat` occurrence is a Javadoc
line *asserting its own absence*, with the two real imports confined to `LdapDirectoryServiceTest`. **The
Reviewer went further and audited whether the seam *shapes* leak**, which is the question that decides
whether child 3 inherits or rewrites:

- All five seams are expressed in `DirectoryPerson`'s own vocabulary plus a `DirectorySource` hook. **No
  leak found.** A Cognito- or CLARISA-backed provider satisfies them by stubbing its own client.
- Two details make it *genuinely* rather than accidentally reusable: `failingServiceInvocationCount()`'s
  Javadoc pins the per-factory-call reset semantics (the subtle part a child would get wrong), and **the
  contract does not assume a malformed email reaches the backend** — a provider that pre-validates to
  `NOT_FOUND` still passes, while one that pre-validates to `ERROR` fails. That is exactly the right
  discrimination to inherit.
- `appliesInternalConnectionFromConfigBeforeSearching` was **correctly pushed down** into the LDAP
  subclass; `setInternalConnection` is an LDAP-only concept with no place in a provider-agnostic class.
  The Reviewer verified the test's premise at source: `APConfig` has a public no-arg constructor with no
  I/O and `isProduction()` returns `false` when `PRODUCTION` is null (`APConfig.java:849-855`), so the
  expectation is grounded rather than lucky.

#### 📄 Documentation drift corrected by the Leader

`design.md` §6.3, `design.md` DD-9 and `tasks.md` T04 all described the contract test as having **"one
abstract factory method."** The delivered shape has **five abstract seams** — and that is the *better*
design, since one factory cannot express the *no-match*, *found* and *failing* backends the five §5.1
rows require. **DD-12 already presupposed the richer shape.**

Corrected at all three sites, with a revision note under DD-9. The Implementer correctly did **not** touch
`design.md` beyond its two approved widenings — spec maintenance is the Leader's, and this is a correction
to a *description*, not a deviation from intent.

#### Checkstyle scoping finding — EB-2 costs this task nothing

The Reviewer established that `configuration/marlo-checkstyle.xml` is a 9-module config at
`severity=warning` and **the plugin does not set `includeTestSourceDirectory`** — so **test sources are
outside Checkstyle's scope entirely.** EB-2's unrunnable gate therefore removes no coverage from a
test-only task. Recorded because it also means a future repaired Checkstyle would still not inspect these
files.

#### Constraint sweep — all clean

JUnit 4 only ✅ · **zero mocking-framework imports** ✅ (`DEC-005` respected — hand-rolled fakes) · GPL
header on all three, matching the repo template ✅ · import layout, indent and wrapping match
`URLShortenerTest.java`'s idiom ✅ · English only ✅ · no §3.2 protected file in the diff ✅ ·
`DirectoryPerson` unmodified, **with no test seam or widened visibility** ✅ · exactly 3 new files under
the test package, no stray scratch tests ✅ · `awk 'length>120'` empty ✅.

#### Requirements covered

| Requirement | How |
|---|---|
| `FN-002` *Invalid input* | Assertions 1, 2, 3 + the zero-invocation check (4) |
| `FN-002` *Directory answers, absent* | Assertion 5 |
| `FN-002` *Backend failure* | Assertion 7 — **and its DD-11 counterpart, 8** |
| `FN-002` never throws / never null | Assertion 11, exercised on all 7 rows including both backend-throws rows |
| `FN-003` | Assertion 10 — **but see the finding above: the type's `requireNonNull` is the real gate** |
| `FN-005` *Found person* | Assertion 6, with three distinct mixed-case values |
| `FN-005` *Not found* | Assertion 9 — `assertNull` on login/firstName/lastName, **not `""`** |
| **DD-11** | **Assertion 8, mutation-proven** |
| **DD-9 / `ARCH-001`** | The abstract class is provider-clean and its seams audited for leakage |
| `NF-006` | GPL headers; style matches the repo idiom (Checkstyle out of scope for tests) |

#### `ADVISORY` findings — recorded, non-gating. Two are consequential and are surfaced to the user.

**1. RELIABILITY — assertion 4's counter is live only *transitively*.** It is non-vacuous today only
because test 7's `ERROR` outcome is reachable solely via the throw in the same stub method that
increments. **Deleting `invocationCount++` from `ThrowingLdapService` would silently make the
zero-invocation assertions vacuous and nothing would go red.** One line closes it — a positive control
`assertEquals(1, this.failingServiceInvocationCount())` in test 7 — which would also document the seam
for child 3. **Not applied:** an advisory may neither gate nor widen an approved task, and this concerns
the integrity of the spec's only executable gate, so it is **surfaced to the user** rather than absorbed
quietly.

**2. RELIABILITY — `FN-004`'s email clause is ungated anywhere in the spec.** Assertion 6 does not assert
`getEmail()`. The Reviewer is right that this is not a T04 gate failure — T04's clause-level set names
only `FN-002`, `FN-003` and `FN-005` *Not found* — but the consequence stands: `FN-004`'s *"must **NOT**
lowercase … `login` or `email`"* is covered for `login` and **not for `email`**. The proposed test is
better than a plain assertion: have the backend return a **differently-cased** email than the one looked
up (`"Jane.Smith@cgiar.org"` for a `"jane.smith@cgiar.org"` lookup) and assert the backend's value —
proving simultaneously that the impl reads `user.getEmail()` rather than echoing the request, **and** that
it does not case-fold it. **Surfaced to the user.**

**3. READABILITY —** `appliesInternalConnectionFromConfigBeforeSearching` proves the *value* received,
not the *ordering* its name claims (`FN-005`: *"MUST be applied before the search"*). Either record
`searchAlreadyCalled` when the setter fires, or rename to `…AppliesInternalConnectionFromConfig`.

**4. DD-9 wording, for archive-time —** `createFailingService()`'s Javadoc says *"any backend call
**throws**"*, which is exception-flavored wording in a provider-agnostic file that **child 3 inherits
verbatim**. The contract only needs *"the backend call fails."*

**5. RISK — the transient `testCompile` failure.** The Implementer reported, rather than hid, one
`testCompile` failure mid-session on **pre-existing unrelated files**
(`ProjectPage`/`ProjectPageItem`/`web.filter` symbols not found) that did not recur across four
subsequent runs. The Reviewer corroborated it as not attributable to this diff and named the likely
cause: `CLAUDE.md` *Concurrency* — *"a build taken beside an active agent is wrong rather than merely
slow."* The Implementer was running builds in rapid succession while copying production files for its
mutation testing. **Mitigation actually available:** two independent clean green runs have since been
observed (the Implementer's final run and the Leader's confirmation run). A from-scratch `clean` run is
**not** available — `mvn clean` on `marlo-web` would need to delete the locked `target/classes`. Recorded
as a residual risk, watched at each subsequent task.

#### Final verification result

**PASS on attempt 1 — and for the first time in this run, without an "on inspection only" qualifier.**
The suite compiles, executes, and is **demonstrably falsifiable** on its two most important assertions.
The Reviewer's summary:

> All 11 required assertions are present and genuinely falsifiable — I independently derived both
> mutation outcomes from the control flow and they match the Implementer's report exactly, so this suite
> is real executable evidence for `D1` and the sole DD-11 gate, **not a green tautology.**

Review rounds consumed: **5 of the ~20 budgeted** (T01: 1 · T02: 2 · T03: 1 · T04: 1).

---
