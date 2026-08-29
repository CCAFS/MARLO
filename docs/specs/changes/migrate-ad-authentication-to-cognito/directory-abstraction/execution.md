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

#### ✅ EB-1 FULLY CLOSED at T05–T07 — the lock released and the WAR builds

**2026-08-28, during T05–T07: the file lock released on its own.** The VS Code Java language server let go
of `marlo-web/target/classes/**` (most likely after its indexing settled), and the blocked phase started
working. Leader-verified before relying on it:

```
$ [System.IO.File]::Open('...\AiAction.class','Open','ReadWrite','None')
WRITABLE - lock RELEASED
$ ls -la marlo-web/target/*.war
-rw-r--r-- 270737187  Aug 28 11:15  marlo-web/target/marlo-web.war
```

**Both required gates then run green. Leader-run independently, not taken on the Implementer's report:**

```
$ export JAVA_HOME="C:/Program Files/Java/jdk-17"
$ mvn -q install -DskipTests -pl marlo-web -am     → INSTALL_EXIT=0     ← the WAR packages
$ mvn -q -pl marlo-web -am test                    → TEST_EXIT=0
                                                      Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
```

| Gate | Status as of 2026-08-28 |
|---|---|
| `mvn -q install -DskipTests -pl marlo-web -am` | ✅ **AVAILABLE and GREEN** — `tasks.md` §10's Definition of Done box is now tickable |
| `mvn -q -pl marlo-web -am test` | ✅ **AVAILABLE and GREEN** — 20 tests |
| `mvn -q checkstyle:check` | ❌ Still unrunnable — **EB-2, a different and unrelated defect** |
| `DIRABS-T12` (Spring context smoke check) | ✅ **NOW REACHABLE** — needs a WAR + `run-marlo-java17.sh`; both available |

**The lock may return.** It is a property of the editor session, not of the repository, and nothing this
spec did released it. Every subsequent task should run the install gate while it is available and record
the result, rather than assuming it will hold.

**Net correction to this section's history:** EB-1 was recorded too broadly (all compilation deferred),
then narrowed at T04 (only packaging blocked), and is now **closed** (nothing blocked). Each step was a
tightening toward what was always true. The original entries for T01–T04 keep their contemporaneous
wording; **this block is the current state.**

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

> **PARTIALLY SUPERSEDED at `T04`.** Compilation became available (see EB-1). The sentence above
> remains accurate for **checkstyle only**. Flagged with the other supersession residuals as pending
> for `/akili-validate`; not swept here, because T11's authorised sweep is closed.

---

### EB-3 — VS Code's Java language server compiles into Maven's output directories

**Diagnosed at `T14`, 2026-08-29. This is the single root cause behind three separate blockages
previously recorded as unrelated:** the `T00.5` WAR lock, the `T12` mid-build failure, and `T14`'s
`testCompile` failure. Recording it as one blocker replaces three coincidences with one mechanism.

**The mechanism.** The `redhat.java` extension's JDT Language Server builds the project continuously
and writes to the **same directories Maven owns** -- `marlo-web/target/classes` and
`marlo-web/target/generated-sources/annotations` -- and runs its own annotation processing there.
Maven and the language server then race for the same files.

**The evidence, and why it is conclusive rather than circumstantial:**

| Observation | What it rules out |
|---|---|
| `mvn clean` failed with `Failed to delete ...target\classes\libs\org\pentaho\...\libpixie\8.0.0.6-352` | Not a code defect; a live process holds the directory |
| Killed the LS (pid 10736); it **respawned at 09:36:20**, within seconds, as pid 19756 | Killing the process is not a workable remedy -- VS Code resurrects it |
| `marlo-web/target/classes` measured **0** class files, then **317** a minute later **with no Maven process running** | Conclusive. Something other than Maven writes to Maven's output directory |
| With VS Code fully closed: `clean install` exit 0 (1206 classes), `test` exit 0 (33 tests) | The tree and the code were never the problem |

**The three symptoms this explains, all previously mis-attributed:**

1. `cannot access APConfig / class file for APConfig not found` in test compilation -- partial class
   files written by the LS, not a classpath defect. **The Leader initially argued against the
   Implementer's lock diagnosis on the strength of this symptom, and was wrong**; the symptom was
   downstream of the lock, not an alternative to it.
2. `maven-war-plugin` failing at `T00.5` and `T12` -- the same handles, biting at a different phase.
3. MapStruct `Internal error in the mapping processor: NullPointerException ... toplevel.sourcefile
   is null` while reading `PolicyOwnerTypeMapperImpl.java` -- a generated source the LS had written
   and still held.

**Remedy, in order of durability.** Killing the LS process does **not** work (it respawns). Closing
VS Code works and was used here. Disabling the `redhat.java` extension is the durable fix for anyone
running gates repeatedly. A per-repo `java.import.gradle`/`java.jdt.ls` output redirect would be
better still and is **not** attempted here -- it edits user machine configuration, which is outside
this spec's scope.

**PENDING ITEM for the default branch (`staging`).** Per the Shared-File Write Discipline this entry
records, rather than applies, the guide change:

| Pending edit | Target |
|---|---|
| Add to the Concurrency section: the JDT Language Server is a **second writer** to `target/`, not merely a lock holder. Any Maven gate must be run with VS Code closed or `redhat.java` disabled | root `CLAUDE.md` -> *Concurrency* -- adjacent to the existing `run-marlo-java17.sh` sharpener |

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
| Isolation | `grep -rl "org.cgiar.ciat" marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/` | **EMPTY** ✅ — ⏱ **correct at T01 only.** T03 adds `impl/LdapDirectoryService.java` to this same package and it imports `adauth` **by mandate** (`FN-005`); from T03 the right expectation is **exactly one file**. *(Root, `-rl` and time-scope added 2026-08-28 — this is T01's own row and the previous sweep annotated T02's by mistake.)* — the new package does not know AD exists |
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
| Isolation | `grep -rl "org.cgiar.ciat" marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/` → **EMPTY** ✅ — ⏱ **correct at T02 only**, for the reason given in **T01's evidence row above**: T03 adds `impl/LdapDirectoryService.java` to this package and it imports `adauth` by mandate, so from T03 the expectation is exactly one file. *(Root and time-scope added 2026-08-28; **label corrected from "T01 only" — this is T02's row**, mislabelled by the previous sweep.)* |
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
| Isolation | `grep -rl "org.cgiar.ciat" marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/` → **EMPTY** ✅ (no imports at all; the linked types are same-package) — ⏱ **correct at T02 only**, for the reason given in the T01 entry: from T03 the expectation is exactly one file. *(Root and time-scope added 2026-08-28.)* |
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
| Isolation | `grep -rln "org.cgiar.ciat" marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/` → **exactly one file**, `impl/LdapDirectoryService.java` *(root added 2026-08-28: the original record omitted it, and the command became root-dependent once T04 created `marlo-web/src/test/.../security/directory/LdapDirectoryServiceTest`, which imports `adauth` by design under DD-12)* ✅ — independently re-run by the Reviewer, which also confirmed that under JD-1's `^import` gate it is exactly one |
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
for child 3. **Not applied.** An advisory may neither gate nor widen an approved task. Surfaced to the user at the T04 gate; **user decision 2026-08-28: record and proceed.** It dies here. If it later matters it returns as its own proposal, not as scope smuggled into this spec.

**2. RELIABILITY — `FN-004`'s email clause is ungated anywhere in the spec.** Assertion 6 does not assert
`getEmail()`. The Reviewer is right that this is not a T04 gate failure — T04's clause-level set names
only `FN-002`, `FN-003` and `FN-005` *Not found* — but the consequence stands: `FN-004`'s *"must **NOT**
lowercase … `login` or `email`"* is covered for `login` and **not for `email`**. The proposed test is
better than a plain assertion: have the backend return a **differently-cased** email than the one looked
up (`"Jane.Smith@cgiar.org"` for a `"jane.smith@cgiar.org"` lookup) and assert the backend's value —
proving simultaneously that the impl reads `user.getEmail()` rather than echoing the request, **and** that
it does not case-fold it. **Surfaced to the user at the T04 gate; user decision 2026-08-28: record and proceed.** Recorded here as a known, named coverage gap in `FN-004`: the `login` half is gated by assertion 6, the `email` half is gated nowhere in this spec.

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

### `DIRABS-T05` + `T06` + `T07` (EXEC-034 / 035 / 036) — one atomic unit

| Field | Value |
|---|---|
| **Status** | **PASS on attempt 1** — all three |
| **Date** | 2026-08-28 |
| **Sequencing** | **Option (a)** — one atomic commit, as `tasks.md` T05 requires be recorded. Pre-registered at T00, confirmed at the T04 gate. Compilation is never broken and `NF-007` holds for the trio as a single revertible unit |
| **Skills** | `error-handling-patterns` |
| **Effort** | `xhigh` |
| **Review rounds** | **1** |

#### Both required gates green — Leader-verified independently

```
$ export JAVA_HOME="C:/Program Files/Java/jdk-17"
$ mvn -q install -DskipTests -pl marlo-web -am     → INSTALL_EXIT=0
$ mvn -q -pl marlo-web -am test                    → TEST_EXIT=0
                                                      Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
```

13 → **20 tests**. The 7 new ones are 3 (`CrpUsersActionDirectoryTest`) + 4
(`ManageUsersActionDirectoryTest`). The Reviewer corroborated the arithmetic — 3 + 4 = 7 exactly —
confirming **neither new class was silently dropped from Surefire's scan**, which matters given the
scanner quirk below.

#### T05 — deletions only, verified three ways

| Check | Result |
|---|---|
| Diff content | **Only deletions**: imports `:103-104` and `getOutlookUser` with its Javadoc. Nothing added |
| `grep "org.cgiar.ciat\|DirectoryService" BaseAction.java` | **EMPTY** — satisfies `FN-007`'s *"must **NOT** declare, inject, or reference `DirectoryService` in any form"* |
| Constructors | Bodies **byte-identical**. Reviewer noted they shifted from `:613`/`:620` to `:610`/`:617` — **exactly 3 lines: the 2 imports plus one blank. Nothing else moved.** That arithmetic is itself proof the change is deletions-only |
| Surviving `getOutlookUser` in `src/main` | Only `GuestUsersValidator:36,55` — **its own duplicate, T08's, correctly untouched** — plus one Javadoc mention |
| Third caller (the STOP condition) | **None appeared.** C-1's premise held |

**DD-2 delivered as designed:** MARLO's widest shared class got *smaller* and gained **no** new
dependency. `EXEC-034`'s rewire was correctly **not** restored (DEV-2 / `OQ-4`).

#### T06 / T07 — equivalence verified against the files, not the diff

The Reviewer read both migrated actions in full rather than judging from the diff, and confirmed
**verbatim preservation** of everything the diff does not show:

- `ManageUsersAction:162-178` — the four-part `!= null && trim().length() > 0` guard, both i18n keys
  (`manageUsers.email.notAdded`, `manageUsers.email.validation`), `emailStatus.put("status", true)`, both
  `return SUCCESS`.
- `CrpUsersAction:646-661` — the **redundant second `isCGIARUser = false`**, `RandomStringUtils.randomNumeric(6)`,
  the **duplicate `setModificationJustification`**, and the **silent no-op when the form has no names**.
- `String password = this.getText("email.outlookPassword")` still assigned **before** the branch (`:635`).
- **No null guard at either `.toLowerCase()`** — `DIRABS-OQ-5`'s preserved NPE is intact at both sites.

**Scope, measured:** `DirectoryService|DirectoryPerson` across all of `marlo-web/src` matches exactly
**7 files** — the 2 consumers, the 2 new tests, and the 3 unmodified T04 test files. Nothing under
`action/center/**`, no §3.2 protected file, no Mockito, GPL headers present, longest line 117 chars.

#### The 611-line test — audited for honesty, and it is honest

The Leader flagged the size (611 lines to exercise a ~25-line branch) as the run's main risk: at that
scale a test can end up asserting its own scaffolding. The Reviewer answered all four questions
directly, and the answers are worth recording because they will govern T09/T10/T15:

| Question | Finding |
|---|---|
| Is the asserted code **production** code? | **Yes. Every override is inert with respect to every assertion.** `save()` is not overridden; the branch at `:633-661` runs unmodified — the field assignments, `isCGIARUser = true`, `userManager.saveUser`, `randomNumeric(6)` and `person.getLogin().toLowerCase()` are all real. **And more production code runs than was claimed:** `notifyRoleAssigned` is **`private`** and therefore cannot be overridden, so it executes for real on both paths |
| Does overriding `getText(...)` make the i18n assertions circular? | **No.** `CrpUsersActionDirectoryTest` has **zero** message assertions, so the concern does not arise there. In `ManageUsersActionDirectoryTest:125,150` the pattern is `assertEquals(action.getText(K), action.getMessage())` with `getText` overridden to the **identity** function — and identity is **injective**, so the assertion reads *"production called `getText` with exactly key K."* A different key in production returns a different string and the assertion fails. **Sound observability, not a test asserting its own map** |
| Does `canAcessCrpAdmin() → true` mask a defect? | **No — it is required, not permissive.** It is the outermost gate at `:609`; without it `save()` returns `NOT_AUTHORIZED` and the migrated branch is unreachable. Nothing in `:633-661` reads authorization state |
| Is the scaffolding **falsifiable**? | **Yes, and this is the real test of it.** `sendMailNewUser` is no-oped but **captures the `password` argument production computed**. If the not-found branch stopped generating one, the captured value would be `"email.outlookPassword"` (from the identity `getText`) and fail the `\d{6}` regex. Both suites carry the mandatory mixed-case `"JSmith"` → `"jsmith"` fixture, so **`D1` is genuinely covered, not tautologically passed** |

#### 📌 A `FakeDirectoryService` javadoc imprecision, corrected here

`FakeDirectoryService`'s javadoc (a T04 artifact) says T06 must prove **zero invocations**. **It does not,
and it must not.** `ManageUsersAction`'s trim-and-length guard sits **after** `findByEmail` (`:154`, then
`:164`), so the invocation count is legitimately **1**; the correct instrument for the short-circuit is
`saveUserCallCount == 0`, which is what the Implementer used.

**The zero-invocation assertion belongs to `T09`**, where the `APConstants.OUTLOOK_EMAIL` suffix guard
sits *before* the lookup. The Implementer picked the right instrument; the T04 javadoc is the imprecise
artifact. Recorded rather than edited — the file is committed T04 work and this is a doc nuance, not a
defect.

#### `ADVISORY` findings — one is a hard forward pointer that WILL break T08

**1. ⏭ RISK → MANDATORY FORWARD POINTER FOR `DIRABS-T08`. This is not optional advice.**

`CrpUsersActionDirectoryTest:109` passes **`new GuestUsersValidator()`**. **T08 adds an `@Inject`
constructor taking `DirectoryService` to that class** (`design.md` §6.2, DD-8) — and under JSR-330 the
implicit no-arg constructor at `GuestUsersValidator:33` is normally dropped when an explicit one is
declared. **At that moment this test stops compiling and T08's build breaks.**

`save()` never touches `validator` — the argument is **pure constructor filler**. Cheapest fix is `null`
in its place.

**Ruling: fold the fix into T08, not a rework of T06.** It is a one-token change, T08 already owns the
consequences of that constructor, and reworking a PASSed task for a defect that only manifests inside a
later task would split the change from its cause. **T08's brief MUST carry this**, or T08's first
`mvn test` fails for a reason its Implementer cannot diagnose from its own scope.

**2. ⚠️ RELIABILITY → ESCALATED TO THE USER. A Leader decision is owed before T09/T10/T15.**

**`struts2-junit-plugin` is ALREADY a test-scope dependency** (`marlo-parent/pom.xml:253`,
`marlo-web/pom.xml:73`), so `StrutsJUnit4TestCase` / `StrutsSpringJUnit4TestCase` are on the classpath
**today**. A real `ActionContext` would **remove the `getText` and `getActionName` overrides outright.**

The Implementer's *"MARLO has no action-test precedent"* is true of **precedent** but not of
**capability** — and the distinction has 3× leverage, because T09, T10 and T15 are all action tests that
would otherwise replicate the hand-rolled pattern three more times. Pairs with advisory 3.

**→ LEADER DECISION on advisories 2 and 3, taken at the T05–T07 gate (2026-08-28):** put to the user with
the `struts2-junit-plugin` finding in hand. **Decision: T09/T10/T15 keep the hand-rolled pattern.**
Rationale: it is already green, already reviewed in depth, and the approved spec does not ask for a
Struts test harness — introducing one mid-spec would put an unreviewed test architecture underneath the
only executable gate the spec has. The duplication cost (advisory 3) is accepted as a known price.
**Both advisories are recorded and die here**; neither becomes a task nor widens one. `struts2-junit-plugin`
being already available is worth a *future* proposal, not a mid-flight change.

**3. READABILITY —** ~**360 of the 611 lines** are five full no-op manager implementations, and the two
suites each hand-roll a *differently-behaving* `FakeUserManager` **under the same simple name**. These
break on any interface method addition, and `RoleManager`'s `cloneRolePermissionsByAcronym` /
`ensureSuperAdminRoleAndPermissions` are evidence these interfaces do churn. A shared test-support
package would repay itself across T09/T10/T15. **Correctly NOT done here** — it would add a file outside
T06/T07's declared *Files touched*.

**4. RELIABILITY —** T06's clause list names `saving.saved.guestRole` on both branches but **no assertion
covers it**. Satisfied by *diff absence* (`CrpUsersAction:644-645, 658-659` untouched), which is
legitimate evidence for a preservation clause, and `tasks.md` T06's *Tests:* line does not require it —
but the identity `getText` makes it a one-line assertion and **T07 does exactly that**, so the asymmetry
is gratuitous. Neither suite asserts `getLastEmailReceived()`; low value here, **mandatory in T09** where
the lowercased-email clause lives.

**5. ⏭ SURFACE → carry the RULE forward, NOT the explanation.**

The Implementer reported that `maven-surefire-plugin:2.12.4` crashes the whole test fork
(`NoClassDefFoundError: CrpUsersAction`, zero tests run) when an **outer** test class declares a private
helper whose **parameter type** is a `BaseAction` subclass, and fixed it by typing two reflection helpers
as `Object`.

**Version identification confirmed:** no `maven-surefire-plugin` is declared in either POM, so Maven 3's
default **2.12.4** is in force. Eager parameter-type resolution during scanning is a real and documented
old-Surefire failure mode.

**But the stated mechanism is not internally consistent, and the Reviewer caught it:** `CrpUsersAction`
is *demonstrably loadable* in the test JVM — the nested `TestableCrpUsersAction extends CrpUsersAction`
and the tests pass. So *"resolving `CrpUsersAction` crashes the fork"* cannot be literally true of the
execution classloader; the failure is a **scanner-classloader or ordering artifact**, not a missing
class.

**Recorded as: reproducible symptom, unverified mechanism.** T09/T10/T15 inherit **the rule** — *no
`BaseAction` subclass in an outer test-class method signature* — and must **not** inherit the
explanation as settled fact.

**And the workaround is the only in-scope option regardless:** `tasks.md` §3.2 protects **every
`pom.xml`**, so pinning Surefire 2.22.2/3.x — the obvious real fix — is out of scope for this spec **by
construction**. The type-safety cost is near zero: both helpers are `private static`, three lines, and
immediately drop into untyped reflection (`field.set(...)`), which throws `IllegalArgumentException` on a
wrong-typed argument anyway.

#### Requirements covered

| Requirement | How |
|---|---|
| `FN-007` — *"`getOutlookUser` **MUST** be gone"* | Deleted. Both constructors byte-identical. `BaseAction` references neither `adauth` nor `DirectoryService`. Verified no FTL/JS/XML/properties reference |
| `FN-006` *CrpUsersAction* — every clause | Found branch asserted (`"JSmith"` → `"jsmith"`, `setCgiarUser(true)`, `isCGIARUser`); non-resolving branch preserved by diff absence and asserted (`\d{6}` password, form names, `setCgiarUser(false)`); `ERROR ≡ NOT_FOUND` asserted |
| `FN-006` *json/global/ManageUsersAction* — every clause | Found branch asserted incl. `addUser()`; guard preserved and asserted against whitespace-only names; **both** i18n keys asserted via the injective identity `getText`; `ERROR ≡ NOT_FOUND` asserted |
| `FN-001` | Both consumers receive `DirectoryService` by **constructor injection**; neither constructs `LDAPService` |
| `FN-004` | Both keep their own `.toLowerCase()` at their own call site; the abstraction transforms nothing |
| `OQ-5` | **No null guard added at either `.toLowerCase()`.** The NPE is preserved, as confirmed |
| `NF-007` | One atomic commit for the trio — a single revert restores all three |

#### Final verification result

**PASS on attempt 1 for all three tasks**, with **both** required gates green and independently
re-verified by the Leader. The Reviewer's summary:

> T05 is deletions-only with both `BaseAction` constructors byte-identical and zero surviving references;
> T06/T07 are one-to-one lookup substitutions with every `else` branch, i18n key, transformation and
> preserved NPE intact, and scope confined to exactly the four named files. **The 611-line test is
> honest:** every override is inert with respect to every assertion, the mixed-case `"JSmith"` → `"jsmith"`
> and `\d{6}` password assertions are genuinely falsifiable, and the identity `getText` makes the
> i18n-key assertions sound rather than circular.

Review rounds consumed: **6 of the ~20 budgeted** (T01: 1 · T02: 2 · T03: 1 · T04: 1 · T05–T07: 1).

---

### `DIRABS-T08` (EXEC-037) — Migrate `GuestUsersValidator`

| Field | Value |
|---|---|
| **Status** | **PASS on attempt 2** (of a 3-attempt ceiling) |
| **Date** | 2026-08-28 |
| **Effort** | `xhigh` on both attempts — **not bumped**, same reason as T03: the next level is `max` and the tier rule forbids it on T2 |
| **Skills** | `error-handling-patterns` |

#### 🏁 Milestone reached: no `getOutlookUser` implementation remains in MARLO

Leader-verified and independently re-verified by the Reviewer: a repo-wide grep over `**/src/main/**/*.java`
returns **exactly one hit — a Javadoc line in `LdapDirectoryService.java:34`.** T05 removed `BaseAction`'s
copy; this task removed the `GuestUsersValidator` duplicate. The helper is gone from the codebase.

#### Attempt 1 — what was right

Both gates green, **Leader-verified independently**: `INSTALL_EXIT=0` · `TEST_EXIT=0` ·
**24 tests** (20 baseline + 4 new).

The production diff is minimal and the Reviewer confirmed at the source that everything `tasks.md` T08
protects is **byte-identical**: the `validateGuestUsers(...)` call, the field-error block, and the whole
`validateGuestUsers` method. The `isCGIARUser` reassignment is preserved. `@Named` keeps no qualifier
value. `super()` resolves against the real `BaseValidator()` at `:63`. The deleted method was confirmed
**`public`** (C-3). And `DirectoryService.findByEmail` contracts never-null, so the unguarded
`person.isFound()` is **correct rather than lucky** — a distinction worth recording.

The Reviewer also verified the parts the Leader could not: the spy captures **all four** arguments and
asserts them with `assertSame` on the object identities (the strong form, not `equals`), satisfying
`tasks.md` T08's *"same arguments"* clause; and `errorBehavesIdenticallyToNotFound` genuinely exercises
`Mode.ERROR` → `found == false, source == ERROR`, which is exactly `FN-002`'s *"leave a caller that reads
only `found` behaving exactly as today."*

#### Attempt 1 — Reviewer verdict: `STATUS: FAIL`, 3 issues. Leader adjudication: **all three upheld.**

**Issue 1 — an assertion that cannot fail on the property it claims.**
`configFieldIsNotShadowedByTheNewConstructor` obtains `BaseValidator.class.getDeclaredField("config")`.
**A `Field` from `getDeclaredField` is bound to that declaring class's slot and is immune to shadowing by
construction.** If someone added `private APConfig config;` to `GuestUsersValidator` tomorrow, the write
and the read would both hit the *base* slot and `assertSame` would stay green. The only things that could
redden it — `config` renamed off `BaseValidator`, or the subclass ceasing to extend it — are compile
errors, not shadowing. The Javadoc further claims the field *"remains the **single** field of that name
reachable"*, and **singleness is never checked.**

> **Violated:** `tasks.md` §3.3 *Falsifying input* — *"A check nothing can fail is not evidence, however
> green it reports."*

**Leader note on why this one matters most.** The Implementer was **genuinely honest about the Spring
limitation** — it said plainly that `new` bypasses the container and attributed the clause to T12. But it
then substituted a *different* structural claim that is **also** unfalsifiable, and recorded that one as
proven. Honest framing around an unfalsifiable core is harder to catch than a plain overclaim, and it is
exactly what the Reviewer is for.

**Issue 2 — an unverified causal mechanism written into a source file as established fact.**
The test's Javadoc asserts Surefire crashes *"if the **outer test class itself** declares a method whose
parameter or return type is a `BaseAction` subclass"* and that the nested spy *"is not reached by the
outer-class scan."*

**The Reviewer found the confound, and it is decisive:** T06's crashing signature took a `BaseAction`
**subclass**, while T08's non-crashing spy override takes **`BaseAction` itself** — a type this file
already loads freely as a local variable and a field type. So the single non-failing case varies **two
variables at once** (outer-vs-nested declaration site **and** subclass-vs-base parameter type). One case
moving two variables cannot license the exclusive claim *"specifically outer-class, not nested-class."*
And the record already establishes the stated mechanism is inconsistent with observed behavior.

> **Violated:** `tasks.md` §3.3 *Cannot prove* and *Disqualifies the evidence* — an inconclusive result
> must be reported as inconclusive, never collapsed into a certainty.

**The conservative rule to carry to T09/T10/T15 instead:** symptom reproducible, **cause unverified**.
Observed-safe = a **nested** class taking **`BaseAction` itself** (n=1). Observed-unsafe = an **outer**
test-class method taking a **`BaseAction` subclass** (n=1). A nested signature taking a `BaseAction`
*subclass* is **untested** — avoid it until a case exists.

**Issue 3 — `null` where a real instance costs the same. This one is a correction to the Leader.**
The Leader verified `null` was **safe** (`validator` is referenced only at `CrpUsersAction:983`, inside
`validate()`, not `save()` — the Reviewer independently confirmed the method boundaries at `:981` and
`:604`). **But safe is not the same as best, and the Leader did not ask the second question.**
`this.directoryService` is already in scope two lines above, so
`new GuestUsersValidator(this.directoryService)` is the **same token count**, compiles, carries no
Surefire exposure, and removes the trap outright. As written, the first time that test grows to exercise
`validate()` it dies on a bare NPE with no diagnostic.

> **Violated:** `.agents/reviewer.md` §2 *Stability & Integrity* — a latent NPE was introduced into a file
> that previously had none, when a strictly better substitute existed.

#### 📌 EB-2 CORRECTED — it is now *unverifiable*, not merely differently-symptomatic

The Implementer reported Checkstyle failing with a **`PluginContainerException`** (*"Number of foreign
imports: 1"*) rather than EB-2's recorded `NoSuchMethodError`, and **verified it is pre-existing** by
stashing its diff and reproducing the failure on the unmodified `e21a57a` tree. Correct methodology; the
diff is conclusively cleared.

**But the Reviewer's reading of the symptom change is the important part:** the two failures sit at
**different depths**. `NoSuchMethodError` is a *runtime* incompatibility reached **after** the plugin
loaded and began executing. `PluginContainerException` is a **classloader-realm construction failure** —
the plugin **never ran at all**. So the new failure **masks** the old one.

> **The correct entry is not "EB-2's symptom changed" but: EB-2 is now UNVERIFIABLE, not resolved. A
> second, earlier blocker is stacked in front of it.** Anyone who later fixes the realm error should
> expect the `NoSuchMethodError` to still be waiting behind it.

Likely trigger is local `~/.m2` state or a Maven/JDK difference between the two recorded runs — **not the
repository.** Still out of scope: `pom.xml` is protected.

#### ⏭ FORWARD POINTER for `DIRABS-T12` — the `config` clause may now be vacuous

The Leader found that `config`'s only use was inside the deleted method, so **this class references
`config` nowhere.** The Reviewer drew the consequence the Leader had not:

> **`FN-006`'s `config` clause is now *vacuously satisfiable* for this class.** If Spring's field
> injection silently failed here, **nothing would break**, because no code path reads it.

So T12's app-start check will prove the context starts and that `DirectoryService` resolves into the new
constructor — both real and worth having — **but it will not prove `config` is populated on this bean, and
must not be recorded as though it did.**

Two honest options, to be decided when T12 is briefed:
1. Gate the clause with the falsifiable structural check from remediation 1 (no subclass field named
   `config`; the inherited field is still `protected` and still `@Inject`-annotated), or
2. Mark the clause **explicitly not applicable to `GuestUsersValidator` after T08**, since the dependency
   it protects no longer exists.

#### Undeclared items the Reviewer identified — none fatal, all worth recording

1. **A substituted test was not declared.** `tasks.md` T08 says *"assert `config` is non-null after
   construction"*; the Implementer wrote a shadowing check instead. **That was the right call** — the
   literal instruction contradicts the task's own disqualifier — but *resolving a spec self-contradiction
   is precisely what belongs in `Not Done / Assumptions`.*
2. **No test executes the real `validateGuestUsers` or the post-call field-error block.** All three
   behavioral tests use the spy. `tasks.md` gates those by diff inspection, so it is legitimate — but it
   is a coverage boundary that should be stated rather than left for the Reviewer to find.
3. The `null` tradeoff was **hedged** (*"in the paths this test exercises"*) rather than declared as a
   known limitation with the constraint that makes it true.
4. **`config` is referenced nowhere in the class** — the single most consequential fact about the clause,
   and the Implementer's report did not surface it.

#### `ADVISORY` — recorded, non-gating

- **READABILITY —** the new test's `Does not prove` paragraph is *unusually good*, and the Reviewer's
  framing is worth keeping: it is **the model the three issues should be raised to, not lowered from.**
  Fixed, this file becomes the reference for how the remaining consumer migrations document their seams.
- **RISK —** `BaseValidator` and `GuestUsersValidator` are **both `@Named`**, with the latter extending the
  former, so a `BaseValidator`-typed injection point has two candidates. **Pre-existing, unchanged by this
  diff, out of scope** — flagged only so T12 does not misattribute it to this task if the context start
  surfaces it.

#### Attempt 2 — the fix, and the falsifiability demonstration

Effort **held at `xhigh`** (next level is `max`, forbidden on T2). All three remediations applied, nothing
else touched. The production file was **out of rework scope** and stayed so.

| | Attempt 1 | Attempt 2 |
|---|---|---|
| **1** `config` check | `assertSame` roundtrip through `BaseValidator.class.getDeclaredField` — **immune to shadowing by construction** | Loop over `GuestUsersValidator.class.getDeclaredFields()` asserting **none is named `config`**, plus `@Inject`/`protected` checks on the inherited handle |
| **2** Surefire note | Stated an unverified mechanism **as fact** | Symptom / cause separated; both observed cases scoped **n=1**; the third marked **untested** |
| **3** `CrpUsersActionDirectoryTest:109` | `null` — a latent NPE | `new GuestUsersValidator(this.directoryService)` — same token count, trap gone |

**The falsifiability demonstration — by A/B mutation, the standard this run set at T04:** the Implementer
temporarily added `private APConfig config;` to `GuestUsersValidator`, rebuilt clean, and observed:

```
Tests run: 4, Failures: 1, Errors: 0, Skipped: 0
configFieldIsNotShadowedByTheNewConstructor  <<< FAILURE!
java.lang.AssertionError: GuestUsersValidator must not declare its own field named config: doing so
would shadow BaseValidator's inherited field
	at ...GuestUsersValidatorDirectoryTest.configFieldIsNotShadowedByTheNewConstructor(...:167)
```

**Only the target assertion failed — the other three stayed green**, which is what proves *specificity*,
not merely falsifiability. Then reverted, zero residual diff, 24 green.

**Reviewer corroboration of the evidence's internal consistency** (a nice touch, since a fabricated
mutation report is otherwise hard to detect): the file contains exactly **4** `@Test` methods, matching
the reported `Tests run: 4, Failures: 1`, and the failing line `:167` is the loop's `assertFalse`.

#### ⚖️ The Leader pressed the Reviewer on whether the fix was partial — and it was, in a bounded way

The Leader asked specifically whether the two accompanying `assertTrue`s (`@Inject` present, `protected`)
are falsifiable or *"decoration riding along with a now-genuine first assertion"*, warning that recording
a partial improvement as complete is the same failure mode caught in attempt 1.

**The Reviewer answered plainly: they are riding along.** A subclass's `@Inject` constructor **cannot**
change a superclass field's annotations or modifiers, so neither assertion can fail as a consequence of
the change under review. It went further and checked the spec: **`design.md` §6.2 lists seven modified
`marlo-web` files and `BaseValidator.java` is not among them** — so they are not even an in-spec
tripwire, and their value is limited to a refactor outside this spec.

**Why this is nonetheless a PASS and not a partial fix waved through:** the clause's *actual* subject — no
subclass shadowing — **is** now guarded by a genuinely falsifiable, A/B-demonstrated check. The two extra
assertions are **described accurately rather than overclaimed**, and they are exactly what the Reviewer's
own attempt-1 remediation prescribed. That makes them **advisory polish**, and spending attempt 3 on them
would be the misuse the attempt ceiling guards against.

#### Attempt 2 — verification

| Gate | Result |
|---|---|
| `mvn -q install -DskipTests -pl marlo-web -am` | **`INSTALL_EXIT=0`** — Leader-verified independently |
| `mvn -q -pl marlo-web -am test` | **`TEST_EXIT=0` · 24 tests** — unchanged count, as expected for a rework that only hardened assertions |
| Zero mutation residue | **Verified positively, not accepted:** the Reviewer ran `rg "config\|APConfig\|org\.cgiar\.ciat"` over `GuestUsersValidator.java` → **no matches**, which simultaneously proves the A/B field left nothing behind *and* satisfies T08's own isolation grep |
| Import churn | Correct — every import used; `APConfig`'s removal was **required**, not optional |
| Checkstyle | Not run. **EB-2 remains unverifiable** (see the correction above) |

**Reviewer's stated verification limit, again declared rather than faked:** *"I have `Read`/`Grep`/`Glob`
only, so 'unchanged' is established by content and by the unchanged 24-test count, not by a byte diff."*
The Leader's byte-level confirmation is in the table above.

#### `ADVISORY` from attempt 2 — recorded, non-gating

**1. ⏭ READABILITY → routed by the Leader, not left to chance.**
`CrpUsersActionDirectoryTest:78-85` **still states the falsified Surefire mechanism as fact**, while the
new `GuestUsersValidatorDirectoryTest:56-58` now says the record refutes it. **A T09/T10/T15 author who
opens the T06 file first extracts the wrong causal model.** Fixing it was outside the three remediations
and no remaining task legitimately touches that file.

> **Leader ruling:** do **not** open a passing file to edit a comment, and do **not** mint a task for it.
> Instead, **the conservative rule is carried directly in the T09, T10 and T15 briefs** — the brief is the
> transmission mechanism and the Leader controls it, which neutralises the harm without touching the
> file. The inconsistency is recorded here as a known doc defect for archive time.

**2. READABILITY —** the two `BaseValidator` assertions are inert with respect to this change (analysed
above). Also `:156-157` attributes Spring's ability to populate the field to `@Inject` **and** `protected`
visibility; **strictly only `@Inject` is the enabler — Spring injects private fields too.** The wording
faithfully echoes `FN-006`'s literal text, so this is imprecision rather than a false claim.

**3. 📌 CORRECTION to the recorded coverage boundary — matters for T12's gap list.**
Declaration #2 was imprecise *in the conservative direction*: the post-call block at
`GuestUsersValidator:51-56` **is** executed by all four tests (the spy inherits `validate`). What no test
exercises is **either branch body** — no `addActionError`, no `addActionMessage` path. Understating
coverage is the safe error, but the boundary is recorded correctly as **"neither branch taken,"** not
*"block never runs."*

**4. ⚠️ RISK — ADOPTED AS AN OPERATIONAL PROTOCOL, not merely recorded.**
**Two independent transient Windows file-lock build failures have now occurred:** T04's `testCompile` and
T08's `maven-resources-plugin` file-copy. Both cleared on a bare retry with no code change. With
Checkstyle down and the root guides warning that a green test run is weak evidence in this repository,
**`mvn install` is this spec's only meaningful gate — so flakiness in it is a risk to every remaining
task.**

> **Protocol adopted for T09 → T17, and to be carried in every remaining brief:**
> **one bare retry is permitted; every retry MUST be reported.** Never silently absorbed — *silent
> absorption is how a genuine compile failure gets misread as a flake.*

#### Requirements covered

| Requirement | How |
|---|---|
| `FN-006` *"`isCGIARUser` **MUST** derive from `DirectoryPerson.found`"* | `if (person.isFound())`; asserted on both the found and not-found paths |
| `FN-006` *"its duplicate … **MUST** be deleted"* | Deleted. **No `getOutlookUser` implementation remains anywhere in `src/main`** |
| `FN-006` *"**BUT** the call to `validateGuestUsers(...)` and the field-error handling below it must **NOT** change"* | Byte-identical, verified at the source by the Reviewer. Absent from the diff |
| `FN-006` *"**AND IT MUST** keep receiving `config` through the inherited field"* | **Structurally guarded** (no subclass shadowing — falsifiable and A/B-demonstrated). **Spring field injection itself is NOT covered here** — see the T12 forward pointer above, including that the clause is now *vacuously satisfiable* for this class |
| `FN-001` | `DirectoryService` by constructor injection; no `LDAPService` constructed |
| `FN-002` *"a caller that reads only `found` behaves exactly as today"* | `errorBehavesIdenticallyToNotFound` — genuinely exercises `Mode.ERROR` → `found == false, source == ERROR` |

#### Final verification result

**PASS on attempt 2.** Both required gates green and Leader-verified. The Reviewer's summary:

> All three attempt-1 issues are resolved — the `config` assertion is now falsifiable and
> A/B-demonstrated, the Surefire note separates symptom from cause with `n=1` scoping and an explicitly
> untested third case, and the `null` argument is replaced by a field assigned earlier in `setUp()`.
> Nothing that passed regressed: the production file carries zero mutation residue.

Review rounds consumed: **8 of the ~20 budgeted** (T01: 1 · T02: 2 · T03: 1 · T04: 1 · T05–T07: 1 · T08: 2).

---

### `DIRABS-T09` (EXEC-038) — Migrate `SearchUserAction`

| Field | Value |
|---|---|
| **Status** | **PASS on attempt 1** |
| **Date** | 2026-08-28 |
| **Effort** | `xhigh` · **Skills** `error-handling-patterns` · **Review rounds** 1 |
| **Gates** | `INSTALL_EXIT=0` · `TEST_EXIT=0` · **28 tests** (24 → 28). Leader-verified independently. **Zero retries needed** |

#### 🔧 SPEC DEFECT found and corrected BEFORE dispatch — the insertion-order clause

`FN-006` *SearchUserAction* required asserting the nine `userFound` keys **"in insertion order"**, and
`tasks.md` T09 named *"a `LinkedHashMap` replaced by a `HashMap`"* as the falsifying mutation. **Both
rested on a misreading of the code, found by the Leader while preparing the brief:**

- `userFound` is **already a `HashMap`** (`SearchUserAction:60,75`) — the named mutation is **impossible**.
- A `HashMap` does **not** preserve insertion order. Its iteration order is a function of the **key set
  alone**, so inserting the same nine keys already yields the same order.
- **The order clause was therefore subsumed by the key clause** and could add nothing falsifiable about
  the defect that actually matters: a lost, renamed or mis-valued key.

**Resolved at the HITL gate: the order clause is dropped.** Corrected at four sites (`requirements.md`
`FN-006`; `tasks.md` T09's requirements-covered line, Tests line, and Falsifying-input line) with a
Decision Log entry recording **both rejected alternatives:**

| Rejected | Why |
|---|---|
| Keep an order assertion as a guard against a future map-type swap | Falsifiable but **fragile** — `HashMap` iteration order can change between JVM releases, breaking the test with no MARLO change |
| Switch the map to `LinkedHashMap` so the clause becomes true | Would **change the JSON key order `searchUsers.do` returns today**, violating `NF-001` equivalence — the spec's own acceptance criterion — to satisfy a mis-written clause |

**⚠️ And the Leader's sweep missed a fifth site.** The T09 Reviewer found `tasks.md:331` still reading
*"asserting key presence without values **and order** … needs an ordered comparison"*. **Cause: the
Leader's grep pattern was `order of insertion|insertion order|LinkedHashMap|their order`, and the bare
phrase `"and order"` matches none of them.** Fixed on the spot, and re-swept with a sharper `\border\b`
pattern that returns only the corrections themselves plus two legitimate *"reverse order"* rollback
references.

> **Third recurrence of the same Leader pattern in this run** (after the §5.1 row miscount and the
> `FN-002` *Invalid input* omission): **the sweep is only as good as the pattern, and a phrase-specific
> grep misses paraphrases of the same claim.** Flagged for Kaizen alongside the other two.

#### The production change

```
- LDAPService service = new LDAPService();  +  setInternalConnection if/else  +  its OWN try/catch
+ DirectoryPerson person = this.directoryService.findByEmail(userEmail.toLowerCase());
```

`if (userLDAP != null)` → `if (person.isFound())`; the four getters re-pointed. **Nothing else.**

Leader-verified: the `APConstants.OUTLOOK_EMAIL` suffix guard is **unmoved and byte-identical at `:193`**;
`grep "org.cgiar.ciat"` → **empty**; the class was **not** deleted (`OQ-12` unresolved — deletion is
child 3's); the 3-key not-found and 4-key non-`cgiar.org` branches are absent from the diff.

**The removed `try/catch` — Reviewer-verified as safe, with the propagation surface unchanged
byte-for-byte.** The old catch wrapped **only** `service.searchUserByEmail(...)`. That call now sits
inside `LdapDirectoryService.findByEmail`'s own `catch (Exception e)`, which maps every throw to
`notFound(..., ERROR)`. What the old code left *outside* its try — `new LDAPService()`,
`setInternalConnection`, `config.isProduction()` — is the same set that remains outside a catch today,
now living inside `LdapDirectoryService`. **Nothing previously swallowed can now propagate.** And
`person.isFound()` cannot NPE: the contract forbids a null return and the only implementation returns
non-null on all five paths.

#### 🎯 The two assertions this task existed to establish

**Assertion 5 — ZERO INVOCATIONS, and T09 is the first task where it is the correct instrument.** The
guard at `:193` sits **before** `findByEmail` at `:195`. (T07's guard sits *after*, so its count is
legitimately 1 — `FakeDirectoryService`'s Javadoc is imprecise on this and the Implementer was warned in
the brief.)

**The Reviewer established the assertion is not redundant with the shape assertion**, which is the real
question: *a mutation that **hoists** `findByEmail` above the guard while preserving the branch structure
would leave the 4-key shape intact and redden **only** the invocation count.* That is exactly the
T07-shaped pattern this task exists to exclude, so the assertion carries value no other assertion
supplies.

**Demonstrated by mutation** — guard changed to `if (true)` → `Tests run: 28, Failures: 1`, the single
failure being `nonCgiarEmailNeverReachesTheDirectoryLookup`. Reverted with a verified clean diff, green
again.

**Reviewer corroboration worth recording as a technique:** it noted the Implementer reported
**`Failures: 1`, not `Errors: 1`.** An `AssertionError` is what the predicted path produces; had the
mutation instead driven an NPE, Surefire would have classed it as an **Error**. *The reported
classification matches the predicted mechanism* — weak but real evidence the mutation was **executed
rather than narrated.**

**Assertion 3 — `FN-004`'s `email` half is now gated, closing a gap the T04 Reviewer had identified and
that had no owner.** The `login` half was covered from T04; the `email` half was **ungated anywhere in the
spec**.

The Implementer declared a judgment call: it fed a mixed-case **email** (`"Jane.Smith@CGIAR.org"`) in
addition to the mandated mixed-case **login** (`"JSmith"`), reasoning that an already-lowercase fixture
would make the assertion trivially true. **The Reviewer verified the whole chain rather than the
assertion:**

- `DirectoryPerson.found()` stores **raw** values, and says so in its class Javadoc.
- `FakeDirectoryService.findByEmail` returns `this.response` **verbatim** in `FOUND` mode — it does **not**
  synthesize a person from the received email.
- Therefore `person.getEmail()` is exactly `"Jane.Smith@CGIAR.org"`, and **only the consumer's
  `.toLowerCase()` at `:204`** can produce the asserted `"jane.smith@cgiar.org"`.

That distinguishes *"the consumer lowercased it"* from *"the consumer echoed a value that was already
lowercase"* — the precise failure mode the brief asked it to rule out. The mixed-case email also does
independent work the login could not: **it is what makes the `getLastEmailReceived()` assertion
non-trivial**, gating `FN-006`'s *"the lookup MUST still be given the lowercased email"*.

**And the Reviewer's ruling on whether this was scope creep is worth quoting:** the reasoning *"is not an
invention — it is `design.md` §6.3's own stated rationale applied to the field the spec left ungated.
Extending a spec's rationale to close a gap the spec identified is **conformance, not deviation**."*

#### Falsifiability of the remaining assertions — the Reviewer pressed each, none is decoration

| Assertion | What reddens it |
|---|---|
| **1** (9 keys + values) | A dropped/added key (`size()`), a `name`/`lastName` swap, a lost `.toLowerCase()` on either field, any flipped boolean. The `invocationCount == 1` check additionally catches a refactor that looks up **twice** |
| **4** (3-key not-found) | **The single most likely wrong implementation of this whole migration: `person != null` instead of `person.isFound()`.** Since the seam never returns null, that mutation enters the *found* branch and NPEs on `person.getLogin().toLowerCase()`. **This assertion is what pins the null-check → `isFound()` translation** — and the same hazard applies to T10 |
| **6** (`ERROR` ≡ `NOT_FOUND`) | Any consumer-side read of `source` — **notably a copy of T10's `throw new DirectoryLookupException` on `ERROR`, a live copy-paste hazard since T10 is the adjacent task doing exactly that.** It pins that `SearchUserAction` is *not* the source-reading consumer. Thin, but not empty |

#### Surefire landmine — not hit, and the reason is materially cleaner than T06's

Verified rather than assumed: `SearchUserAction`'s public API (`setUserEmail:257`, `getUserFound:237`)
plus the constructor **fully drive `execute()`**, so **no reflection helper was needed at all** — the risky
signature shape never arose. The only nested type is `FakeUserManager`, whose methods take
`Long`/`String`/`User`. **Neither the observed-unsafe nor the untested pattern occurs.**

#### Transient-lock protocol — third data point, and the first clean one

**Zero retries needed.** Now 2 of 4 recent tasks affected, 0 here — consistent with the lock being
**sporadic rather than progressive**. Recorded because a pattern that only counts failures is not a
pattern.

#### `ADVISORY` — recorded, non-gating, they die here

1. **READABILITY —** `SearchUserActionDirectoryTest:32-33` statically imports `assertFalse` and
   `assertTrue`; **neither is used** (all 24 assertions are `assertEquals`). Outside Checkstyle's scope
   (no `includeTestSourceDirectory`), but two dead imports in a brand-new file.
2. **READABILITY —** `SearchUserAction:53` declares the injected field **non-`final`**, while
   `CrpUsersAction` and `json/global/ManageUsersAction` declared theirs `final`. The Reviewer checked: no
   spec or Checkstyle rule requires it (`design.md`'s finality mandates govern `DirectoryPerson` only) and
   it **matches the non-final `userManager` in this very class** — so it is consistency-across-consumers,
   not a defect. *"Not worth a rework cycle; worth one keystroke if the file is reopened."*
3. **The `tasks.md:331` doc defect** — raised by the Reviewer and **already fixed by the Leader**, as
   described above. Not deferred.

#### Requirements covered

| Requirement | How |
|---|---|
| `FN-006` — the 9 keys and values | `size() == 9` + a per-key `assertEquals`. **No order assertion** (clause dropped) |
| `FN-006` — *"the lookup MUST still be given the lowercased email"* | `findByEmail(userEmail.toLowerCase())` at `:195`; gated by `getLastEmailReceived()` against a **mixed-case** input |
| `FN-006` — *"**BUT** the not-found branch must **NOT** change"* | Absent from the diff; asserted as the exact 3-key shape |
| `FN-006` — *"**AND IT MUST** preserve the `OUTLOOK_EMAIL` suffix guard"* | Unmoved and byte-identical at `:193`; gated by the **zero-invocation** assertion |
| `FN-004` | **Both** output `.toLowerCase()` calls survive (`username` `:203`, `email` `:204`), and **both are now gated** — the `email` half for the first time in this spec |
| `FN-001` | `DirectoryService` by constructor injection; no `LDAPService` constructed |
| `FN-002` | `ERROR ≡ NOT_FOUND` asserted; the redundant `try/catch` removed with the propagation surface unchanged |

#### Final verification result

**PASS on attempt 1.** Both gates green and Leader-verified.

> The production change is a faithful, behavior-preserving swap onto the seam — all four `FN-006`
> *SearchUserAction* clauses hold — **and all four new tests are genuinely falsifiable, including the two
> the spec was relying on this task to establish.**

Review rounds consumed: **9 of the ~20 budgeted** (T01: 1 · T02: 2 · T03: 1 · T04: 1 · T05–T07: 1 ·
T08: 2 · T09: 1).

---

### `DIRABS-T13` (EXEC-041) — **CHECKPOINT 2 REPORT**

| Field | Value |
|---|---|
| **Status** | **PASS** |
| **Date** | 2026-08-29 |
| **Executed by** | Leader inline (synthesis of this audit trail); **audited by an independent Reviewer** |
| **Checkpoint** | **CP2 — "Isolate `adauth`" → COMPLETE** |

---

## ✅ CHECKPOINT RESULT — CP2

### The three statements `tasks.md` T13 requires, stated first and plainly

> **1. `adauth` is still the implementation.** `LdapDirectoryService` calls
> `LDAPService.searchUserByEmail()` exactly as `BaseAction.getOutlookUser` did. Every lookup in MARLO
> still reaches CGIAR AD through `org.cgiar.ciat.auth`.
>
> **2. Nothing was removed.** `adauth` 5.7 remains in all three POMs and on the classpath. Its JARs are
> untouched under `marlo-data/src/main/resources/libs/`. No dependency, no **committed** configuration, no schema,
> no i18n key changed.
>
> **3. Observable behavior is unchanged.** This checkpoint delivers **no user-visible change**. Its whole
> value is structural: `marlo-web` business code no longer names an AD type.

**Gate 1 is explicitly NOT reached and NOT claimed.** Two Capability A call sites remain live by design
(`APCustomRealm:287`, `LDAPAuthenticator:61`) — they belong to **child 2**. Reaching zero runtime `adauth`
usage is child 2's and child 3's work.

### T05 sequencing — the choice this report must record

**Option (a) was taken: T05 + T06 + T07 landed as ONE atomic commit** (`e21a57a`). Deleting
`BaseAction.getOutlookUser` breaks compilation at both callers, so the three shipped together —
compilation was never broken, and `DIRABS-NF-007`'s independent revertibility holds for the trio as a
single revertible unit. Pre-registered at T00, confirmed at the T04 gate.

### Per-task evidence — command and outcome for every line

*Per T13's disqualifier — "a report claiming a verification that was never run" — no row below states a
result that was not observed. Where a gate was unavailable, the row says so instead of omitting it.*

| Task | Command | Outcome |
|---|---|---|
| **T00** | `mvn -v` · `git rev-parse HEAD` · `grep -rn "^import org.cgiar.ciat"` · `grep -rn "new LDAPService()\|new ADConexion"` | Baseline `8f88e78`; **zero drift across all 19 cited `file:line` refs**; 14 import lines / 8 files in `marlo-web`, 4 / 2 in `marlo-data`; 9 construction sites. **STOP hit:** `mvn -v` reported JDK 1.8 → resolved per-command with `JAVA_HOME` |
| **T01** | `awk 'length>120'` · `grep -rl "org.cgiar.ciat" …/security/directory/` | Empty · empty *(correct at T01 only — T03 later adds the one permitted importer)*. Reviewer PASS attempt 1 |
| **T02** | same, + Reviewer source read | Empty · 1 new file. **PASS attempt 2** — attempt 1 FAILed on a Javadoc cardinality defect traced to the Leader's brief |
| **T03** | `grep -rln "org.cgiar.ciat" …/security/directory/` | **Exactly one file**, `impl/LdapDirectoryService.java`. **PASS attempt 2** — attempt 1 stopped on the `FN-002` spec gap, closed as **DD-11** |
| **T04** | `mvn -q -pl marlo-web -am test` | **`Tests run: 13, Failures: 0` — the spec's first executable evidence.** Assertions 7 and 8 **mutation-proven**: breaking each branch produced exactly the predicted red |
| **T05+T06+T07** | `mvn install -DskipTests` · `mvn test` | `INSTALL_EXIT=0` · **`Tests run: 20, Failures: 0`**. `BaseAction` grep for `org.cgiar.ciat\|DirectoryService` → **empty**; both constructors byte-identical |
| **T08** | same | `INSTALL_EXIT=0` · **`Tests run: 24`**. **No `getOutlookUser` implementation remains anywhere in `src/main`.** PASS attempt 2 — the `config` assertion was rebuilt to be falsifiable and **A/B-demonstrated** |
| **T09** | same | `INSTALL_EXIT=0` · **`Tests run: 28`**, no retry. Zero-invocation assertion **mutation-proven**; closed `FN-004`'s previously ungated `email` clause |
| **T10** | same | `INSTALL_EXIT=0` · **`Tests run: 33`**, no retry. **PASS attempt 2** -- attempt 1 FAILed on the same falsified-mechanism comment defect as T08. The `ERROR` branch is **two-sided mutation-proven**: removing the throw turns both `ERROR` tests red **and** makes the `NOT_FOUND` message test wrongly pass |
| **T11** | `grep -rl "^import org.cgiar.ciat.auth" <3 roots>` | `src/main` **2** · `src/test` **1** · `marlo-data` **3** — independently re-run by the Reviewer at each of the **three** T11 attempts, plus the Leader's own run, plus a fourth Reviewer re-run during the T13 audit. The six previously-affected production files at **0**. **Reached the 3-attempt HALT ceiling on documentation consistency** (never on the gate) and was lifted by user decision into a scoped spec-wide doc sweep |
| **T12** | `scripts/run-marlo-java17.sh` · `curl` · log search | **`BUILD SUCCESS`**; WAR @ 08:31 after `target/` deletion; five new classes in the **deployed** jar; **`HTTP 302`** root, **`HTTP 200`** on `crpUsers.do`; **0** occurrences of the four bean-exception types |

### Gates — honest status

| Gate | Status |
|---|---|
| `mvn -q install -DskipTests -pl marlo-web -am` | ✅ **GREEN**, Leader-verified independently |
| `mvn -q -pl marlo-web -am test` | ✅ **GREEN — 33 tests, 0 failures.** MARLO had **3** test files before this spec, one with its only body commented out |
| `mvn -q checkstyle:check` | ❌ **UNVERIFIABLE, not passed and not failed** — **EB-2**. `maven-checkstyle-plugin:2.9.1` vs a forced `checkstyle:8.18`; `pom.xml` is §3.2-protected so repair is out of scope. Substitute: `awk 'length>120'` per task + Reviewer source read |
| **D8** (Spring wiring) | ✅ **Substitute evidence obtained by T12** — **`D8` remains an *accepted risk*, not a closed gate.** `requirements.md` §9 is explicit: closing it properly needs a Spring context test and is **not in this spec's scope**. T12's check is **manual, one-time and not repeatable in CI**. It was, however, **demonstrated falsifiable** — red in run #2 on an unresolvable dependency, green in run #3. A real context test is `docs/trd/trd.md` §14.9 item 8. *(Corrected 2026-08-29: this row read "CLOSED by T12", which escalated a one-time manual substitute into a closed gate and contradicted `requirements.md:492` and `:528-531`.)* |

### What CP2 delivered

| | Before | After |
|---|---|---|
| `marlo-web` production classes importing `adauth` | **8** | **2** — `searchUsersUtil` (`main()`, child 3) and `ContactPersonAction` (**T14 removes it, this checkpoint's successor**) |
| `getOutlookUser` implementations | **2** (`BaseAction` + the `GuestUsersValidator` duplicate) | **0** |
| Consumers behind the seam | 0 | **5** |
| Tests in the repository | **3** files | **10 test files** (11 under `src/test` incl. `FakeDirectoryService`), **33 tests** |
| Provider swap cost | a refactor across six classes | **one `@Named` bean + one config value** |

### Deviations from the runbook, both approved and recorded

- **DEV-2 / `OQ-4`** — `EXEC-034` said to *rewire* `BaseAction.getOutlookUser`; **DD-2 deletes it** instead. Approved at the `/akili-execute` gate. `BaseAction` gained no dependency and got smaller.
- **`OQ-5`** — the `getLogin()` NPE on a found person is **preserved, not "fixed."** A null guard at any `.toLowerCase()` is a defect in this spec.
- **`DIRABS-T12` has no `EXEC-` equivalent** — added by this spec because `D8` had no gate in the runbook.

### Defects found in the approved runbook, flagged for archive time

1. **`EXEC-040`'s `marlo-data` expected output omits `APCustomRealm`** — inherited by this spec and corrected here (`judgment.md` **JD-1**).
2. **`EXEC-040`'s `marlo-web` expectation is unsatisfiable as written** — it demands one importer at a checkpoint that legitimately has more, and scopes to `marlo-web/src`, sweeping in the test source root. Corrected here; the `=1` end state was **relocated to T16, not removed**.

### Known gaps at CP2 — named, not hidden

| Gap | Status |
|---|---|
| Checkstyle | **Unverifiable** (EB-2). Out of scope to repair |
| `json/global/ManageUsersAction`'s **15 FTL pages** | JSON shape asserted; **rendering is not.** No automated gate exists |
| The real `ad_user` query | Stubbed in tests; untested against the database |
| Real AD return values | Faked; **no integration harness exists** |
| Multi-instance behavior | Single Cargo instance, **no memcached** — `TS-3` kryo defects undetectable locally |
| **`D8` itself** | **Not closed** — an *accepted risk* with a one-time manual substitute (T12). A repeatable Spring context test is `trd.md` §14.9 item 8, out of scope |
| **Open documentation debt — CP2 ships with this** | **2 items pending `/akili-validate`** (residual `Checkstyle`-as-working-gate prose in `requirements.md` §9 and `design.md:401`; the asymmetric supersession at `execution.md`'s §2 gate table) **+ 3 comment touch-ups in shipped test files** — **one of which, `CrpUsersActionDirectoryTest.java:78-85`, still states a falsified Surefire mechanism as fact.** That one is a live trap for the next author who opens the file; it was deliberately left unedited and the corrected rule is routed through briefs instead |
| **`EXEC-004`'s pre-migration baseline** | **Never obtained.** Compile was deferred at T00.5, checkstyle is UNVERIFIABLE, and no baseline `mvn test` was recorded. Green `install` + `test` were obtained *later* (T04 onward) but **not as CP0's baseline**. `EXEC-010`/`EXEC-030` gate on "CP0 = PASS" — child 2 must read CP0's ledger cell, not its status word |

---

**CP2 is COMPLETE. CP3 (`T14`–`T17`) is next: eliminate the AD construction in `ContactPersonAction`,
prove its endpoint unchanged, re-inventory the call sites, and report.**

---

### `DIRABS-T12` — Spring context smoke check *(the only `D8` evidence)*

| Field | Value |
|---|---|
| **Status** | **PASS** — the context started clean and the seam resolved |
| **Date** | 2026-08-29 |
| **Executed by** | **Leader, inline** — see the delegation-failure record below |
| **Review rounds** | 0 (no diff; the deliverable is evidence) |

#### 🎯 The result, and why it is real evidence rather than a green light

**`DirectoryService` resolves in a live Spring container.** The decisive argument is not the HTTP code —
it is **bean scope**:

`LdapDirectoryService` and `GuestUsersValidator` are both **`@Named` with no `@Scope`/prototype** and both
take an **`@Inject` constructor**. They are therefore **singletons, instantiated eagerly at context
startup**. `GuestUsersValidator`'s constructor takes `DirectoryService`. So:

> **A missing `@Named`, or a second implementation making the injection ambiguous, would have aborted the
> context at startup — before serving a single request.** It did not.

That is precisely the falsifying input `tasks.md` T12 names, and it is what `D8` exists to catch: such a
defect **compiles, passes all 33 tests, and fails only at Tomcat startup**, which CI never exercises.

#### ✅ And the check was *demonstrated falsifiable* — by an unplanned natural experiment

This run produced its own red-then-green pair, which is the standard this spec has held since T04:

| Run | Condition | Result |
|---|---|---|
| **#2** | `marlo-dev.properties` missing `microservice.queueUrl` | `APConfig` → `UnsatisfiedDependencyException` → **context aborted**, `HTTP 404`, WAR deployed but not serving |
| **#3** | property added | **0 bean exceptions**, context started, `HTTP 302` root / `HTTP 200` on `crpUsers.do` |

**We watched this exact check go red for a dependency that could not resolve, then green when it could.**
The mechanism is proven live in this container — so the green is evidence, not an absence of news.

#### Evidence, in the order the disqualifier requires

| # | Check | Result |
|---|---|---|
| **1** | Build succeeded **this run** | `BUILD SUCCESS`; WAR **270,735,343 bytes @ 08:31**, and the script had deleted all three `target/` dirs first — a stale WAR was impossible |
| **2** | The five new classes in the **deployed** artifact | All **PRESENT** in `.../cargo/configurations/tomcat9x/webapps/marlo-web/WEB-INF/lib/marlo-data-4.5.1-SNAPSHOT.jar` (**7 entries** under `security/directory/`, jar @ 08:37) — this is what makes the HTTP result meaningful rather than accidental |
| **3** | Health check | `GET /marlo-web/` → **HTTP 302** (2xx/3xx as required) |
| **4** | The four bean exceptions | `NoSuchBeanDefinitionException`, `NoUniqueBeanDefinitionException`, `UnsatisfiedDependencyException`, `BeanCreationException` → **0 occurrences**. `Could not resolve placeholder` → **0**. `Tomcat 9.x Embedded started on port [8080]` → present |
| **5** | Exercise a consumer | **`crp/admin/crpUsers.do` → HTTP 200** — `CrpUsersAction` is one of the five migrated consumers. `crp/admin/users.do` → 200. **Zero** bean failures after exercising |

Server **shut down**; 0 listeners on 8080, 0 cargo/tomcat processes, `curl` → `000`.

#### ⚠️ What this task does NOT cover — the correction applied 2026-08-28

**It does not discharge `FN-006`'s `config` field-injection clause for `GuestUsersValidator`.** After T08
deleted `getOutlookUser`, that class references `config` **nowhere**, so if Spring's field injection
silently failed **nothing would break** and a green start certifies a **no-op**. The clause is gated by
**T08's structural check** instead. `tasks.md` said the opposite at three loci until this was corrected.

Also out of reach (§6.4): single Cargo instance, **no memcached** — kryo session-serialisation defects
(`TS-3`) stay invisible; no load balancer; HTTP not HTTPS; `/marlo-web/` context path vs `ROOT` in the
image.

#### 🔴 Delegation failure — the subagent was replaced, and the reason is recorded

The T12 Implementer **twice ended its turn having set up a background wait and never delivered its
report** — `leader.md`'s *"idle is not delivered"* case. Poked once per protocol; it repeated the same
pattern. **Replaced rather than poked twice**, and the Leader took the task inline: T12 authors no code
(a script, a `curl`, a log read), and the poll-to-push bridge that defeated the worker is native to the
Leader's harness.

**And the replacement was botched, which is the more useful record.** The Leader began running the script
**while the subagent was still alive**, because it read the notification's `completed` status as *"the
agent is finished."* It was not — the agent had a live background child and launched its own build.

> **Two destructive builds ran concurrently in one checkout**, and the first `BUILD FAILURE` — *"Fatal
> error compiling: marlo-utils-4.5.1-SNAPSHOT.jar"*, a jar that had built successfully seconds earlier and
> then vanished — was the other run's `target/` deletion landing mid-compile.

This is exactly the corruption `CLAUDE.md`'s *Concurrency* section describes, caused by the Leader that
had cited that section when dispatching. **Lesson, stated so it survives: a `completed` task notification
means the agent's turn ended, not that its work stopped.** Kill the agent explicitly before taking its
task inline; never infer termination from status.

#### Retries — three, each reported with a distinct cause, per §3.0

| # | Cause | Class |
|---|---|---|
| **1** | The Leader's own concurrent build (above) | **Self-inflicted** — deliberately *not* counted toward the transient-lock pattern, which would otherwise misreport environment stability |
| **2** | The **JDT LS WAR lock returned** (`CrossCuttingDimensionAction.class` in use) | **EB-2, environmental.** Resolved by the user closing VS Code — verified released before relaunching |
| **3** | **10 property keys missing** from `marlo-dev.properties` | **Configuration** — see below |

**The lock's return was predicted in writing.** EB-1's closure block recorded: *"The lock may return. It is
a property of the editor session, not of the repository."* That note turned what would have been a
baffling failure into a thirty-second diagnosis.

#### The 10 missing properties — and why enumerating the class mattered again

Spring **fails fast**, reporting only the *first* unresolved placeholder (`microservice.queueUrl`).
Chasing that one alone would have meant **ten build-fail-fix cycles of ~4 minutes each**. Comparing the
**key sets** of `marlo-dev.properties` against `marlo-test.properties` found all ten at once:
`log.instance`, `microservice.{apiKey,bucketName,password,queueName,queueUrl,reporting.url,s3.url,userName}`,
`summary.microservice.url`.

**Same methodology that fixed the documentation sweep: enumerate the class, do not chase the reported
instance.**

All ten are non-secret `XXXXXX` placeholders in the template — including the ones named `password` and
`apiKey` — so they were copied verbatim. **No secret was invented.** T12 exercises neither the reporting
microservice nor S3.

#### 🔒 Credential-exposure near-miss — caught by the user, and worth a standing note

The Leader created `marlo-dev.properties.pre-t12.bak` as a safety copy. **That file was NOT gitignored**:
`marlo-web/src/main/resources/config/.gitignore` lists **exact filenames**
(`marlo-dev.properties`, `marlo-api.properties`, `marlo-pro.properties`, `marlo-test.properties`), so any
derivative — `.bak`, `.orig`, `.old` — falls outside the protection. `git status` listed it as untracked,
i.e. **a `git add -A` would have staged a file containing the real MySQL password.**

Verified never committed (`git log --all` on the path → empty; the last three commits contain zero
credential files) and **deleted**. `marlo-dev.properties` itself is correctly ignored throughout.

> **Standing hazard, worth raising with the team:** MARLO's own
> `scripts/update-marlo-dev-java17.sh` runs `sed -i.bak`, so it **generates `marlo-dev.properties.bak` on
> every local run** — the same unprotected shape. The `.gitignore` rule should be `marlo-dev.properties*`.
> **Recorded as a pending item for `staging`** (shared-file discipline: not edited from this spec branch).

Also verified before running: `update-marlo-dev-java17.sh` rewrites **only** `marlo.baseUrl`,
`file.downloads` and `clarisa.summariesPDF` — **no `mysql.*` key** — so the user's connection settings
survive the script.

#### Environment pre-check — probed, not assumed

Per `leader.md`'s *"test the assumption first"*, every precondition was probed **before** paying the
script's destructive cost: JDK 17 ✓ · port 8080 free ✓ · **MySQL running on `localhost:3306`** ✓ ·
schema `aiccradb5` with **597 tables** ✓ · **`users` has 3583 rows** — a real dev dataset, which
`infrastructure.md` §6.1 says must be obtained from the IBD team and could not be assumed ✓.

#### Final verification result

**PASS.** `D8` — the spec's largest blind spot — now has the evidence `DD-10` designed this task to
produce, **and that evidence is demonstrably falsifiable**, having been observed red in run #2 and green
in run #3 on the same mechanism.

---

### `DIRABS-T10` (EXEC-039) — `center/json/global/ManageUsersAction`, the `ERROR` branch

| Field | Value |
|---|---|
| **Status** | **PASS on attempt 2** (of a 3-attempt ceiling) |
| **Date** | 2026-08-28 |
| **Effort** | `xhigh`, held on retry (`max` forbidden on T2) · **Skills** `error-handling-patterns` |
| **Gates** | `INSTALL_EXIT=0` · `TEST_EXIT=0` · **33 tests** (28 → 33), Leader-verified. **Zero retries** |

#### Why this consumer is unlike the other five — confirmed at the source

`:255` had **no `try/catch`**. Every other migrated consumer swallowed its `adauth` exception; **this one
never did** — a failure propagated through `create()` and Struts to a 500. That is the concrete reason
`DirectorySource.ERROR` exists: `findByEmail` never throws, so without an explicit re-raise an AD outage
would silently become `null` and `create()` would report `manageUsers.email.doesNotExist` — **telling an
administrator that a real CGIAR employee does not exist.**

#### The production change — Reviewer-verified as correct and total

```java
DirectoryPerson person = this.directoryService.findByEmail(email);
if (person.isFound()) { ...mutate the newUser FIELD...; return newUser; }
if (person.getSource() == DirectorySource.ERROR) {
  // The service never throws; DirectoryPerson carries no cause on an ERROR result.
  throw new DirectoryLookupException(email, null);
}
return null;
```

**Branch-order totality, verified rather than assumed.** The Leader asked whether `isFound()` first could
mask the `ERROR` branch. The Reviewer traced it to the factories: `DirectoryPerson` has only `found(...)`
and `notFound(email, source)`, private constructor, `source` non-null; `LdapDirectoryService` produces
`found` **only** with `LDAP` and `ERROR` **only** via `notFound(email, ERROR)`; `FakeDirectoryService`
likewise. **A `found` person carrying `ERROR` is not producible**, and is excluded semantically by
`DirectorySource`'s own Javadoc (`ERROR` = *"Nothing is known about the person"*). `return null` is
reachable only for a non-`ERROR` not-found — in the shipped tree, exactly `NOT_FOUND`. All three paths
return or throw.

Leader-verified: **`create()` does not appear in the diff** (T10's STOP condition) · `org.cgiar.ciat`
count **0** · class not deleted · `DirectoryLookupException.java` untouched (the T01 ruling against a
convenience constructor was honored) · no other consumer touched.

#### 🎯 The falsifiability demonstration — the strongest evidence produced in this run

The Implementer mutated the `ERROR` branch to `return null;` and produced **two-sided** evidence:

| Leg | Observation |
|---|---|
| **RED** | `Tests run: 6, Failures: 2, Errors: 0` — both `ERROR` tests failed with type-specific messages |
| **WRONGLY-PASSES** | A temporary 6th test feeding an `ERROR` input into the same assertion the `NOT_FOUND` test uses was **absent from the failures list — it passed**, reproducing exactly the false *"email does not exist"* message DD-3 exists to prevent |
| **REVERTED-GREEN** | No mutation marker in the production file; 33/33; both gates green |

**Why the second leg is the one that matters.** Showing a test go red proves the *test* works. Showing a
*different* test **pass when it should not** proves the **defect is real and silent** — which was DD-3's
entire argument, and until now existed only as reasoning in a design document. It is now a reproducible
observation.

**The Reviewer audited all three legs and all hold**, including a corroboration technique first used at
T09: under the mutation, JUnit 4's `ExpectException` throws a bare `AssertionError`, and test 3b's
`fail(...)` also throws `AssertionError` — which its `catch (DirectoryLookupException expected)` clause
**cannot swallow**. Two `AssertionError`s = **`Failures: 2, Errors: 0`**, exactly as reported.
*"A narrated mutation would have had no reason to get that classification right."*

#### Assertion strength — Reviewer's findings

Test 1 is the strong form: `assertSame` compares against the value read **back out of the `newUser`
field by reflection after `create()`**, and `FakeUserManager.saveUser` returns the same instance it
receives, so a `validateOutlookUser` that built a *new* `User` would redden it. The `"JSmith"` → `"jsmith"`
fixture is genuinely mixed-case. And `getUsers().size() == 1` independently proves `addUser()` ran —
**which also closes a loophole where a non-matching suffix would have made `assertSame` pass vacuously.**
No test catches a broad `Exception` or `Throwable` anywhere.

#### Attempt 1 — Reviewer verdict: `STATUS: FAIL`, 1 issue. **Leader adjudication: upheld.**

**The new test file states the FALSIFIED Surefire mechanism as established fact.**
`CenterManageUsersActionDirectoryTest.java:65-70` asserts the scanner *"resolves declared-method parameter
types on this outer test class while probing for `@Test` methods"* — precisely the mechanism the record
already falsified. The symptom half is hedged, but **the cause is asserted, the `n=1` scoping is absent,
the *"or return type"* half was never observed at all**, and the cross-reference points at
`CrpUsersActionDirectoryTest` — *the file that carries the wrong mechanism* — rather than at the
corrected note.

> **Violated:** `tasks.md` §3.3 *Cannot prove* / *Disqualifies the evidence*. **And this is the same
> defect already upheld as T08 Issue 2**, whose remediation was recorded verbatim as *"the conservative
> rule to carry to T09/T10/T15."*

**Why it is a FAIL and not an advisory, in the Reviewer's words:** *"T10 is the first file authored after
that correction that needed the note, so it sets the pattern the remaining tasks copy."* T15 is still to
come.

#### ⚖️ Attribution — and it is split, not simply the worker's fault

**The T10 brief did carry the corrected framing**, explicitly: *"Symptom reproducible, cause UNVERIFIED —
the mechanism written at T06 was later falsified by the record"*, with observed-unsafe at `n=1`,
observed-safe at `n=2`, and nested+subclass marked untested. The Implementer had it and wrote a different,
falsified account into the file, **and did not declare the divergence in `Not Done / Assumptions`.**

**But the brief supplied that framing as *context for the Implementer's own decisions* — "ignore its
causal explanation and use the scoping above" — and never stated an obligation to reproduce it in
authored documentation.** A worker could reasonably read it as guidance for what to avoid rather than
text to carry into a Javadoc.

**So: worker non-compliance on the substance, Leader imprecision on the instruction.** Recorded split
rather than assigned wholly to either, because the fix differs — the attempt-2 brief now says *explicitly*
that any Surefire note authored in a new file must use the corrected framing.

#### `ADVISORY` from attempt 1 — recorded, non-gating

1. **READABILITY —** `FakeDirectoryService.java:19` scopes itself to *"consumer tests (DIRABS-T06 .. T09)"*,
   which **no longer covers its consumer set** now that T10 uses it. The Implementer was **right** not to
   touch it (existing test file, protected by constraint). Recorded as a **pending doc touch-up for T15 or
   archive** so it is not lost.
2. **RISK —** `directoryLookupExceptionDoesNotExtendAuthorizationException` is a tautology against the
   current tree — nothing in this spec edits `DirectoryLookupException`, and changing its superclass is a
   compile-visible edit. **But the Reviewer distinguished it from the decorative T08 assertion it failed
   earlier:** `tasks.md` T10's *Verification* **mandates this exact check** and names it under *Falsifying
   input*, and it is the cheapest durable pin on DD-3a against the plausible future refactor — *"make it
   an `AuthorizationException` so Shiro handles it"* — that would silently turn a **500 into a 403**. Kept.
   Noted only that it asserts a hierarchy fact about a `marlo-data` type from a `marlo-web` test; if a
   `DirectoryLookupException` unit test ever exists in `marlo-data`, that is its natural home.
3. **READABILITY —** the one declared cosmetic (`* ` → `*`, a trailing space on the Javadoc line being
   rewrapped to add `@throws`) is **acceptable**, and the Reviewer noted approvingly that the other
   trailing-space Javadoc lines in the same file were **left alone rather than opportunistically cleaned.**

#### Attempt 2 — the fix, and a chain of copied claims that ends here

Comment-only. The `inject` Javadoc rewritten to the conservative framing; **nothing else touched.** Gates
re-verified: `TEST_EXIT=0`, **33 tests — count unchanged**, which is the expected signature of a
comment-only edit and itself weak evidence no code moved. `awk 'length>120'` empty. **No retries.**

**The Implementer did better than the remediation asked.** It was told to drop or hedge the unobserved
*"or return type"* claim. It **kept it and marked it unobserved** — *"(whether a `BaseAction` subclass
return type has the same effect is unobserved)"*. The Reviewer's judgment on that choice is worth
recording:

> *"Silent deletion would have erased a plausible second unsafe shape from the record, and a T15 author
> writing a helper that **returns** a `BaseAction` subclass would read the note's silence as clearance.
> Keeping the hazard while stripping its evidentiary status is strictly more informative than dropping
> it. The Implementer chose the stronger fix over the one I asked for."*

#### 🔗 THE CHAIN — an unverified claim propagated four times by copying, and each link is instructive

This is the most transferable finding of the run, and it is not about any one file:

| Link | What happened |
|---|---|
| **T06** | Wrote down a *causal mechanism* for the Surefire crash, inferred from one failure |
| **T08** | Correctly identified the mechanism as falsified and rewrote the note — **but carried one half of it forward**, still asserting *"parameter **or return type**"* as observed when the return-type case was never observed at all |
| **T10 attempt 1** | Copied from T06's file (the nearest exemplar) and restated the **fully falsified** mechanism as fact |
| **Leader** | Routed T10 to cite T08's note as canonical — **without reading it.** It contained the smaller defect |
| **T10 attempt 2** | Now the **most precise** statement in the spec, and more precise than the file it cites |

**The Reviewer owned its link without prompting:** its attempt-1 remediation designated
`GuestUsersValidatorDirectoryTest` canonical *"without reading its wording — which my own contract §1
forbids ('read the pointed-at sections at the source, never a recollection')."* It also noted the line
range the Implementer cited (`:53-64`) is **exactly correct** — *"the citation is precise even though the
target is not."*

**The pattern, stated so it survives this spec:** the failure was never that someone asserted something
false. It is that **an unverified claim propagated by copying while nobody re-read the source being
copied.** Every link had the correct instinct — infer, correct, follow the exemplar, cite the corrected
note — and every link skipped the same step. The gate caught it each time the file was actually in front
of a reader.

#### Resolution on the T08 residual — Leader proposal, Reviewer-sharpened, adopted

1. **No file edits.** `GuestUsersValidatorDirectoryTest` is a committed T08 test file, and an advisory may
   not mint a task.
2. **Re-point the routing: T15 and any later brief cite T10's note, not T08's.** The Reviewer sharpened
   the *reason*, and it is the better one: T08 presents outer-vs-nested as *"what the evidence actually
   supports"*, while **only T10 names that inference `unlicensed`. T10 is canonical on the epistemics, not
   merely on the return-type detail.**
3. **Queue the T08 one-line edit** (`"parameter or return type"` → `"parameter type"`) on the **pending
   doc touch-up list** that already holds the stale `FakeDirectoryService` scoping — applied on `staging`
   per the Shared-File Write Discipline. A recorded pending item, not a minted task.

The Reviewer's closing judgment on proportionality: T08's defect is *"superseded with the correction
adjacent in the same test package, not unmitigated, and a dedicated task for four words of comment would
cost more than the defect."*

#### 📋 PENDING DOC TOUCH-UPS — carried to T15 / archive, applied on `staging`

| Item | Where |
|---|---|
| `"parameter or return type"` → `"parameter type"` | `GuestUsersValidatorDirectoryTest.java:61-62` (T08) |
| Scope note *"consumer tests (DIRABS-T06 .. T09)"* no longer covers its consumer set — T10 uses it too | `FakeDirectoryService.java:19` (T04) |
| The falsified mechanism still stated as fact | `CrpUsersActionDirectoryTest.java:78-85` (T06) — deliberately unedited; correct rule routed through briefs instead |

#### Requirements covered

| Requirement | How |
|---|---|
| `FN-002` *"MUST throw `DirectoryLookupException` … rather than return `null`"* | `throw` on `source == ERROR`; `@Test(expected = DirectoryLookupException.class)` with the **specific** class |
| `FN-002` *"**AND** it **MUST** still return `null` on a genuine `NOT_FOUND`"* | `return null` as the final branch; asserted via `create()`'s `manageUsers.email.doesNotExist` |
| `FN-002` *"**BUT** it must **NOT** report `manageUsers.email.doesNotExist` for a backend failure"* | `assertNull(action.getMessage())` after catching the exception — **and the mutation proved the inverse fails** |
| `FN-002` *"**AND IT MUST** be the **only** caller that reads `source`"* | Only this file calls `getSource()`; no other consumer touched |
| `FN-006` *"MUST set … on the `newUser` **field** and return it"* | `assertSame` against the field read back by reflection after `create()` — instance identity, not equality |
| `FN-006` *"**BUT** the class must **NOT** be deleted"* | Present; `create()` absent from the diff |
| **DD-3a** — must not extend `AuthorizationException` | `assertFalse(AuthorizationException.class.isAssignableFrom(...))`. A tautology against the current tree, **but mandated by `tasks.md` T10's Verification** and the cheapest durable pin against the plausible *"make it an `AuthorizationException` so Shiro handles it"* refactor that would silently turn a **500 into a 403** |
| `FN-004` | `.toLowerCase()` kept at the call site; `"JSmith"` → `"jsmith"` asserted |

#### Final verification result

**PASS on attempt 2.** Both gates green, Leader-verified.

> The rewritten Javadoc is honest on every axis the attempt-1 FAIL named … and the note is now the most
> precise statement of the Surefire rule in the spec — **more precise than the T08 file my own attempt-1
> remediation wrongly designated canonical.**

Review rounds consumed: **11 of the ~20 budgeted** (T01: 1 · T02: 2 · T03: 1 · T04: 1 · T05–T07: 1 ·
T08: 2 · T09: 1 · T10: 2).

---

## HALT: `DIRABS-T11` (EXEC-040) — Isolation gate

| Field | Value |
|---|---|
| **Status** | **`[~]` HALTED** — 3-attempt ceiling reached, all three FAILs on documentation consistency |
| **Date** | 2026-08-28 |
| **Code changed by this task** | **NONE.** T11 is read-only (`Module: none`) |
| **Substantive claim** | ✅ **VERIFIED, and re-verified independently at all three attempts** |
| **Review rounds** | **3** → running total **14 of ~20** |

### The substantive result is not in doubt

Independently re-run by the Reviewer at every attempt, with exact filename matches:

```
marlo-web/src/main  → 2   utils/searchUsersUtil.java  ·  action/center/capdev/ContactPersonAction.java
marlo-web/src/test  → 1   security/directory/LdapDirectoryServiceTest.java
marlo-data/src      → 3   APCustomRealm  ·  LDAPAuthenticator  ·  impl/LdapDirectoryService
```

**The six previously-affected `marlo-web` production files are at 0** — `BaseAction` plus all five migrated
consumers. `DIRABS-NF-002`'s isolation goal is met. **Nothing about the code is blocked.**

### Delegation deviation

Executed **Leader-inline, no Implementer** — three greps and a reconciliation is inline work under the
Delegation Ceiling. **A Reviewer was spawned at every attempt**, exactly as recorded at T00 (*"the T00
inline exception does not extend to T11/T13/T16/T17"*).

### Attempt history — a converging sequence, not a stuck loop

| Attempt | Reviewer verdict | What it found |
|---|---|---|
| **1** | FAIL, **4 issues** | The Definition of Done, T11's clause-level `FN-009` quote, the §8 traceability matrix, and one genuinely invalid byte |
| **2** | FAIL, **2 issues** | A duplicated paragraph (revert-and-redo residue) and **`proposal.md`'s `SC-1`** — normative and DoD-gated |
| **3** | FAIL, **1 issue** | **`proposal.md`'s `SC-11`** — normative, DoD-gated, and with a *destructive* remediation path |

**4 → 2 → 1.** Each attempt fixed everything found and the Reviewer located new instances of the *same
pattern* in places the previous sweep had not reached. That is progress, not thrash — but the ceiling is
the ceiling, and it is reached.

### 🔴 The blocking issue — `SC-11`, and why it is worse than `SC-1` was

`proposal.md:452`, `SC-11`, asserts that `security/directory/` *"contains no reference to Cognito,
CLARISA, Microsoft Graph, `ad_user`, or any other candidate provider — only `LdapDirectoryService` names a
backend"*, verified by *"`grep` over the new package."*

**But `DirectorySource.java` is in that package and names all of them**, because `design.md` §4.2 and
`EXEC-030` **require** it to:

```
:41,:73  CLARISA           :42,:78  COGNITO_CLAIMS
:43,:84  AD_MIRROR / ad_user        DIRECTORY_API
```

**Why this is more dangerous than `SC-1`, in the Reviewer's framing:** *"`SC-1` failing produced a false
alarm. `SC-11` failing has a plausible, destructive remediation: an agent or closer trusting the criterion
over the design **deletes `CLARISA`, `COGNITO_CLAIMS`, `AD_MIRROR` and `DIRECTORY_API` from an approved
8-value enum that child 3 inherits** — the 'reviewer who fixes the expectation to match' hazard
`tasks.md:412` warns about, pointed at **production code** instead of a comment."*

`SC-11` also self-contradicts its own document: `proposal.md:187`, `proposal.md:124`, `design.md` §4.2,
`tasks.md:123` and `requirements.md:94` all mandate the literals it forbids.

**`DIRABS-ARCH-001` is the sound requirement-level statement of the same intent and needs no change** — it
is verified by design review, not by a package grep. Only `SC-11`'s grep formulation is broken, and it is
the **sole locus**; no other document restates it.

### ⛔ Automatic Rollback deliberately NOT performed — and this is a judgment call, recorded

`/akili-execute` Step 4 says a HALT runs `git restore .` and `git clean -fd` so as not to *"leave broken
code for the user to clean up."* **Applying it here would cause the damage it exists to prevent:**

1. **No code changed in this task.** There is no broken code to revert. The Reviewer confirmed it.
2. The uncommitted working tree is **entirely spec-documentation corrections** — T11's and T16's
   expectations, `SC-1`, the D4 box, the JD-1 supersession pointer, the inventory scoping.
3. A rollback would therefore **restore the defective, DoD-gated `SC-1` and the defective T11
   expectations** — reinstating known defects that would report failure on correct code.

The rule's purpose is to protect the user from a half-finished code change. **Here it would delete six
rounds of verified correction and reinstate a defect.** Rolling back is recorded as *inapplicable*, not
skipped.

### Leader hypothesis on the root cause

**Not spec ambiguity and not a worker failure. It is a Leader sweep-methodology defect, now diagnosed
across six instances in this run:**

> **When I correct a definition, I sweep the documents that *contain* it and miss the documents that
> *quote, index, or gate* it.**

The refinement each instance added:

| # | Where the miss was | What it taught |
|---|---|---|
| 1 | `tasks.md` T04's row count | Correct every site, not the one that was reported |
| 2 | `FN-002`'s *Invalid input* scenario | Brief the **requirements**, not only the design |
| 3 | `tasks.md:331`'s *"and order"* | A phrase-specific grep misses **paraphrases** |
| 4 | DoD + coverage matrix + clause quote | Sweep what **indexes** the definition |
| 5 | `proposal.md`'s `SC-1` | Sweep what **gates** the definition, not just what contains it |
| 6 | `proposal.md`'s `SC-11` | **Prose statements of the rule name no path, no count and no literal — no grep I wrote could reach them** |

Instance 6 is the one no pattern-based sweep can close. It requires reading the success-criteria table
**as prose, in full**, whenever a scoping rule changes.

### Also recorded from attempt 3 — encoding, resolved

All seven family files are **valid UTF-8**, independently confirmed by the Reviewer via four byte-level
tests (mojibake markers, malformed lead bytes, orphan continuations, illegal ranges) — the perl
`Wide character` warnings did **not** produce double-encoding this time. Repaired glyphs are semantically
correct (`×`, `◄──`), and the arrow idiom matches `analysis/adauth-retirement-analysis.md`.

**The earlier damage, and its cause, for the record:** a `perl -pi -e 's/\x97/ --/g'` several tasks back
ate the **middle byte of valid multi-byte characters** (`×` = `0xC3 0x97`, `◄` = `0xE2 0x97 0x84`), leaving
orphaned lead bytes — some of which reached commits. A later `perl -0pi` with `\x{2014}` and no `-CSD`
double-encoded **192 lines** of `tasks.md`, recovered by reverting to `f6d1822` and redoing the edits with
the `Edit` tool. **And the detection method was itself the root cause of the confusion:** scanning
`[\x80-\x9f]` flags the continuation bytes of every legitimate em-dash, which is why "318 / 1748 / 998 bad
bytes" was reported and was almost entirely false. **`iconv -f UTF-8 -t UTF-8` is the authoritative check**
and is now the one in use.

### `ADVISORY` from attempt 3 — recorded, non-gating

1. **`proposal.md:147-155`** — *"Two `adauth` importers remain by design"* lists two, prose adds
   `LDAPAuthenticator` for three; **the end state is four — `APCustomRealm` is absent.** *Literally JD-1's
   omission, in a document corrected twice for it.* Not a FAIL: it is a rationale table with no grep
   attached, and both normative loci (`SC-2`, `NF-002`) name `APCustomRealm` correctly.
2. **`execution.md:889`** — T03's evidence line records `grep … security/directory/` → *"exactly one file"*
   **with no root**. True when T03 ran; now root-dependent, since `marlo-web/src/test/.../security/directory/`
   holds `LdapDirectoryServiceTest`. A past-tense evidence record, not a forward expectation, so it gates
   nothing — but any restatement must be rooted explicitly.
3. The `requirements.md` §9 `D4` box now advertises **three** failure modes against a **two-row** table.
   Making it three rows would keep the box the index it claims to be.

### Resolution — the HALT was lifted into a scoped spec-wide documentation sweep

**User decision, 2026-08-28:** stop treating this as T11 rework and treat it as what it was — *a
spec-wide documentation-consistency audit that T11 surfaced.* **T11 is `[x]`:** its gate was verified
independently **five times**, no code ever changed, and the residue is documentation.

#### The methodology change, which is the transferable finding

Nine consecutive misses had one cause: **I fixed the sites a reviewer reported.** The fix was to change
the *unit* of the sweep:

> **Enumerate every locus of a defect *class* mechanically → fix → verify the class greps empty.**

It justified itself immediately. Class A had **13** loci; the reviewer's slice showed 4 and my first
enumeration found 7. Among the ones only a class derivation reached: **`tasks.md` §3.1 — the shared
verification-commands table every task inherits.** No findings list contained it.

#### The four classes — and two of them are *absence* defects

| Class | Shape | Loci | Why a presence-grep misses it |
|---|---|---|---|
| **A** | `checkstyle` asserted as a working gate | 13 | Needs *token **unaccompanied by** its annotation* — a token grep returns 30 hits and cannot separate the 13 dangerous from the 17 benign |
| **B** | normative `grep -rn` stating **file** counts | 4 | Literal execution returns 6 where the text says 3 |
| **C** | grep expectations true only at one task | 4 | *"EMPTY"* is correct at T01 and false from T03 |
| **D** | preconditions declared *"binding on every task"* and **absent from the document that issues the commands** | 3 | **Pure absence.** `JAVA_HOME`: 9 occurrences in `execution.md`, **0 in `tasks.md`.** No presence-grep can ever reach this |

**Class D carried the highest harm.** Without it a closer runs §3.1's compile row verbatim, hits JDK 8
against `<release>17</release>`, and records **FAIL on correct code** — on the spec's only meaningful
gate. Its third instance, the `pipefail` protocol, is worse still: it is the one precondition whose
violation lets a **failed** gate be recorded as **passed**, and this run already produced exactly that
false green (see §2's Leader process defect).

**And a fifth class, found only because it was asked for as a class:** *a finding recorded here about a
`[ ]` task, where `tasks.md`'s entry is not silent but **contradictory**.* `tasks.md` was instructing
T12's closer, at three loci, to record the `config` clause as covered by the app start — which this log
says **must not be recorded**, because after T08 the class references `config` nowhere and a green start
certifies a no-op. Corrected at all three, plus a new *Cannot prove* on T12.

#### Two more things learned, both about the *shape* of a partial fix

1. **A partial annotation is worse than none.** Annotating T01 and T14 made the *absence* of an
   annotation at T02/T03/T06/T08/T10 read as confirmation that checkstyle runs there.
2. **A case-sensitive sweep of this spec is structurally incomplete** — it spells the tool both
   `checkstyle` and `Checkstyle`. Six loci survived on that alone, including two **verbatim siblings** of
   a sentence already annotated.

#### 📋 PENDING for `/akili-validate` — named, not silently dropped

Both are documentation-only; neither blocks code, and the substantive gate is verified.

| Item | Where |
|---|---|
| Residual `Checkstyle`-as-working-gate prose in blindness claims | `requirements.md` §9 opening · `design.md:401` — the reviewer judged these acceptable as *blindness* claims |
| Asymmetric supersession: `execution.md` §2's gate table says checkstyle *"Runs"* **upstream** of EB-2, which says the plugin cannot execute | `execution.md:282`. Generalisable check: for every `SUPERSEDED`/`CORRECTED` marker, look for a locus stating the pre-correction fact **earlier in reading order** — no grep expresses that |

#### Leader tooling failure — three times in this one task, same cause

`perl` byte/delimiter manipulation on UTF-8 markdown damaged files three times, worst of all
**prepending `X` to 2210 of `execution.md`'s 2212 lines** — this file, the run's whole audit trail. Each
was detected and reverted exactly (verified against the committed version, which has zero such lines).
**Rule, no longer hedged: UTF-8 markdown is edited with the `Edit` tool. `perl`/`sed` only for pure
ASCII with non-colliding delimiters.** And the detection method mattered as much as the fix:
`[\x80-\x9f]` byte scans flag the continuation bytes of every legitimate em-dash — **`iconv -f UTF-8 -t
UTF-8` is the authoritative validity check**, with line and column.

---

---

### `DIRABS-T14` (EXEC-050) — Eliminate the AD construction in `ContactPersonAction`

| Field | Value |
|---|---|
| **Status** | **PASS** |
| **Date** | 2026-08-29 |
| **Implementer attempts** | **1** |
| **Reviewer verdict** | **PASS -- zero findings**, five caveats |
| **Gates** | `mvn -q clean install -DskipTests -pl marlo-web -am` **exit 0** (1206 classes) - `mvn -q -pl marlo-web test` **exit 0, 33 tests, 0 failures** |
| **Requirements covered** | `DIRABS-FN-008` (all six clauses) - `DIRABS-SEC-001` |

#### The diff: 1 file, 10 deletions, 0 insertions

Two **non-contiguous** blocks plus the import pair. The `queryParameter` assignment sat *between*
them at old `:87`, so a single "delete `:86-93`" edit would have removed it and broken the build.
The brief flagged this explicitly; the Implementer handled it correctly.

```
- import org.cgiar.ciat.auth.ADConexion;          (:24)
- import org.cgiar.ciat.auth.LDAPService;         (:25)
- LDAPService service = new LDAPService();        (:86)
  String queryParameter = StringUtils.trim(...);  (:87)  KEPT - sits between the blocks
- four APConstants.*_AD local reads               (:88-91)
- ADConexion adConection = new ADConexion(..., Integer.parseInt(port));   (:93)
  queryParameter = queryParameter.trim();         (:95)  KEPT
```

#### Why this was not dead-code cleanup

`searchContact` is routed at `struts-json.xml:1041`, so `searchADUser()` runs on **every hit** -- and
MARLO opened a live Active Directory connection per request and discarded it. `service` was never
read. `adConection`'s only reader is a **commented-out** line. The real lookup is
`adUsermanager.searchUsers()`, which reads the `ad_user` **database table**, not AD.

This was the last `new ADConexion(...)` construction in MARLO source.

#### STOP condition: not triggered

`:99` (`adUsermanager.searchUsers`) and every line below it are **absent from the diff** -- verified
independently by the Leader (grep over the diff for `adUsermanager.searchUsers|setUsers|idUser` -> 0)
before the Reviewer was spawned. `getADFilter` survives at `:55-68`, body byte-identical, shifted -3
by the import deletion. All four `APConstants.*_AD` constants survive in **both** `APConstants.java`
files, which is `SEC-001`; only the *local reads* were removed.

#### Reviewer caveat 4, closed -- and a correction to the caveat itself

The Reviewer noted that C-2 analysed `new ADConexion(...)`'s side effect but **never**
`new LDAPService()`'s, and recorded it as a decision-vs-oversight gap. Closed by bytecode:

```
$ javap -p -c -cp adauth-5.7.jar org.cgiar.ciat.auth.LDAPService
public org.cgiar.ciat.auth.LDAPService();
  0: aload_0
  1: invokespecial java/lang/Object."<init>":()V
  4: aload_0
  5: iconst_1
  6: putfield      internalConnection:Z
  9: return
static initializer blocks: 0
```

`super()`, one boolean write, return. No network, no I/O, no `<clinit>`. Deleting `new LDAPService()`
removes **nothing** observable -- strictly stronger than C-2's "at worst we lose an effect never read".

**The caveat was itself slightly wrong, and the correction matters more than the closure.** This spec
*does* already record that bytecode, at `execution.md:949` and `design.md:717`, from T05's DD-12 work.
What is missing is the **cross-reference from C-2**, so a reader arriving at C-2 cannot find it. That
is documentation debt, not an analysis gap. Cross-reference added to `design.md` §10.0.

**Leader process note.** The first `javap` run was against **`adauth-5.5`**, not the 5.7 the POM
declares (`marlo-parent/pom.xml:14`) -- a `find | head -1` picked the lowest version directory. The
conclusion was stated before the jar was verified, then corrected against 5.7. Same defect class as
the earlier `.m2` path guesses in this task: **resolve the artifact, do not assume its path.**

#### C-2: the accepted behavior change, restated

Removing `Integer.parseInt(port)` also removes a `NumberFormatException` that **would fire today** if
`APConstants.PORT_AD` were malformed -- on a path whose result is discarded. Direction: *now works
where it used to fail*. Accepted, not a defect. **No guard was added**, deliberately: adding one to
"preserve" the throw would reintroduce it.

#### What T14 does NOT prove

That `searchContact.do` returns the same JSON. A green compile does not gate an endpoint's payload.
The Reviewer checked the **bean surface** (no field, getter, or signature added or removed; the
`<result type="json">` envelope at `struts-json.xml:1041-1048` serialises public bean properties, and
`getADFilter(String)` takes an argument so was never one). **The rendered payload is T15's gate.**

#### Environment

Both gates were **UNVERIFIABLE** on first attempt and were resolved, not waived. Root cause diagnosed
and recorded as **EB-3**. The Implementer's handling is worth recording as correct practice: it ran
the sanctioned bare retry, then **stashed its edit, rebuilt unmodified HEAD, reproduced the identical
failure class in a file it never touched, and restored the edit** -- a control test that isolated its
change as innocent -- and then **refused to claim "33 tests green"**, reporting the gate inconclusive
instead. The Leader's subsequent green run confirms the baseline held at **exactly 33**.

Checkstyle remains **UNVERIFIABLE** (EB-2). Style judged by reading: 2-space indent, longest touched
line ~113 chars, and the import grouping stays valid after removing a whole group (group `1=org.cgiar`
is now empty; an empty group takes no separator, per `configuration/ccafs-java-style.importorder`).

---

### `DIRABS-T15` (EXEC-051) — `ContactPersonActionTest`

| Field | Value |
|---|---|
| **Status** | **PASS** |
| **Date** | 2026-08-29 |
| **Authored by** | **`akili-tester` (opus), not the Implementer** -- see the delegation note below |
| **Attempts** | **1** |
| **Reviewer verdict** | **PASS -- zero findings**, six caveats + one spec-hygiene note to the Leader |
| **Gates** | `clean install` **exit 0** - `mvn -q -pl marlo-web test` **exit 0, 39 tests, 0 failures** (baseline 33 + 6) |
| **Requirements covered** | `DIRABS-FN-008`, all clauses -- see the coverage list below |

#### Delegation choice, recorded because it deviates from the execute triad

T15 was dispatched to **`akili-tester` (opus)**, not `akili-implementer` (sonnet). Reason: T15 exists to
verify T14, and **the Implementer wrote T14**. Having one model both change the code and author the test
that declares the change safe is precisely what `author != tester` prevents. The Reviewer (opus, no write
tools) audited as usual, so both independence axes hold.

#### The six tests, and which one actually gates T14

| Test | Gates |
|---|---|
| `twoStubRowsProduceTwoMapsWithExactKeysAndTracedValues` | Row shape: 2 rows -> 2 maps, **exact** 4-key set, `idUser` is `Integer`, every value traced, list order, returns `SUCCESS` |
| `idUserCounterStartsAtOneAndIncrementsPerRow` | `idUser++` precedes the `put`; 3 rows separate an off-by-one from a stalled counter |
| `trimmedQueryParameterIsWhatReachesTheManager` | *"sourced from `adUsermanager.searchUsers(queryParameter)`"* -- `"  smith  "` proves both trims and exactly one call |
| `emptyManagerResultProducesAnEmptyUsersList` | Empty path: non-null, empty, `SUCCESS` |
| `nullManagerResultProducesAnEmptyUsersListNotNull` | The `:93` null branch. **Not named in `tasks.md`** -- added because it is a real branch |
| `compiledActionReferencesNoActiveDirectoryType` | *"no `adauth` type is instantiated"*, read off the compiled constant pool |

**The last one is the only test that gates what T14 actually did, and the reason is worth recording.**
The `adauth` types remain on the test classpath (`LdapDirectoryServiceTest` imports them under DD-12), so
a restored `new LDAPService()` **would execute happily and leave the other five tests green**. A runtime
assertion structurally cannot observe the *absence* of a construction; only the constant pool can. The
test reads `ContactPersonAction.class` off the classpath, decodes ISO-8859-1 (byte-preserving), and
asserts neither `org/cgiar/ciat` nor `org.cgiar.ciat` appears. It uses **string literals, not imports**,
so `src/test` stays at exactly one `adauth` importer and T16's gate is intact.

#### Mutation evidence -- 10 mutations, every assertion watched fail

M1 renamed a key - M2 made the counter 0-based - M3 deleted a `put` - M4 crossed a value to the wrong
getter - M5 made the null path return null - M6 dropped both trims - **M7 restored the `adauth` import and
construction** - M8 made `idUser` a `String` - M9 returned `INPUT` - M10 reversed row order.

**M7 is load-bearing for T14: it is the only mutation the other five tests do not notice.**

**A false green, self-caught, and it is the same defect class the Leader hit three times in this run.**
M3 and M5 **silently failed to apply** on first attempt -- the file has CRLF endings, so `$`-anchored
`perl` substitutions did not match, and the run went green against **unmutated** source. The Tester
detected it by **grepping the mutated file rather than trusting the run**, and re-ran both. The general
rule this run has now paid for four times: *`perl`/`sed` report success without having changed anything;
verify the mutation landed before believing the result.* The Reviewer additionally confirmed, by reading,
that M3 and M5 land on assertions that are load-bearing by construction (`assertEquals(EXPECTED_KEYS,
row.keySet())` and `assertNotNull`), so the near-miss leaves no residual doubt.

M10's run also reported `Errors: 1` -- `Collections.reverse(null)` in the null-path test, an artifact of
probe placement. Reviewer concurred, and noted the substantive catch does not depend on that run.

#### Two defects corrected at this gate

**1. A spec defect in `tasks.md`, found by the Reviewer and pointed at the Leader.** T15's *Tests* line
listed the map keys as `` (`idUser`, `firstName`, `lastName`, `email`, ...) `` -- **with a trailing
ellipsis.** The source puts exactly four (`ContactPersonAction:97-100`). The ellipsis invites a
**superset** assertion, which is *the same defect direction as `FN-006`* -- the insertion-order clause
written against a `HashMap` that this spec already corrected at five loci. Corrected: the line now states
four keys, names set-equality, and records why.

**2. A Javadoc overclaim in the test, corrected by the Leader AFTER review.** Disclosed here in full,
because the committed artifact then differs from the reviewed one. Comment-only, zero behavioral effect,
gates re-run green (39 tests) after the edit:

```
- * deletion of the dead Active Directory construction left the JSON payload byte-identical:
+ * deletion of the dead Active Directory construction left the payload's shape and values unchanged.
+ * Note the deliberate limit: these tests never invoke the JSON result and never assert HashMap
+ * key order, so the emitted JSON's field order is NOT proven identical, and no claim of a
+ * byte-identical response is made here:
```

*"Byte-identical"* is stronger than six tests that never invoke the JSON result and deliberately never
assert `HashMap` key order establish. Left alone, a future reader could cite the comment as proof of a
property nobody checked.

#### Known gaps -- named, not closed

| Gap | Status |
|---|---|
| **The real `ad_user` query is entirely stubbed.** `AdUserManagerImpl` and its Hibernate DAO never execute | Already recorded in `tasks.md` T15 *Cannot prove*. **No task in this spec covers it.** Still open |
| **JSON serialization is not exercised.** The in-memory payload is proven; the wire bytes are inferred | New. Out of reach of a plain JUnit run |
| **`ContactPersonAction:83` NPEs when the query parameter is absent** -- a latent production defect | **Deliberately not asserted.** Asserting it would freeze a bug as this spec's contract; fixing it is barred by T15's *STOP if the test requires a production change*. Reviewer concurred and asked that it be recorded here rather than living only in the Tester's report. **Recorded.** |
| Struts request-parameter plumbing bypassed by the `getParameters()` override | Bounded: the test still drives the real `Parameter.getMultipleValues()[0]` and the real trim chain |
| Checkstyle | **UNVERIFIABLE** (EB-2). Also `includeTestSourceDirectory` is unconfigured, so this file is out of its scope even if the plugin ran |

#### Reviewer caveats not otherwise actioned

- The bytecode test is only as fresh as `target/classes`. Fresh under the documented command (`test`
  compiles main first); stale only against a hand-staged `target/` -- **which is exactly EB-3**.
- The test reads `ContactPersonAction.class` only, not hypothetical nested classes. The class has none.
