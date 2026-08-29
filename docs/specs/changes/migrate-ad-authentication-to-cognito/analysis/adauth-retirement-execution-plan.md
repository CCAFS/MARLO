# `adauth` Retirement — Execution Plan

**Plan ID:** `CHG-COGNITO-ADAUTH-RETIREMENT-EXEC-001`
**Source of truth:** [`adauth-retirement-analysis.md`](./adauth-retirement-analysis.md) — **Revision 3**
**Spec family:** [`../family.md`](../family.md) · [`../auth-flow/tasks.md`](../auth-flow/tasks.md)
**Working branch:** `staging-cognito`
**Created:** 2026-08-26

> **This is a runbook, not an analysis.** The analysis explains *what and why*. This document explains
> *how, in what order, with what checks, and where execution must stop*.
>
> It is written to be executed by an AI coding agent (Akili or equivalent), **one controlled unit at a
> time**, across multiple sessions, resuming safely from the `Execution State` section below.

**The execution model is:**

```
inspect → implement ONE controlled unit → verify → report → continue only when safe
```

---

## Execution State

> **The executing agent MUST update this block after every completed task**, and commit it with the
> task's changes. It is the only mechanism that makes execution resumable.

```text
Current checkpoint:        CP2 COMPLETE  ->  CP3 NEXT (not started)
Last completed task:       EXEC-041 (Checkpoint 2 report) = DIRABS-T13
Gate 1 (functional):       NOT REACHED  -- and NOT claimed. Two Capability A sites
                           remain live by design: APCustomRealm:287 and
                           LDAPAuthenticator:61. Both belong to child 2.
Stabilization:             NOT STARTED
Gate 2 (physical):         NOT REACHED

Capability B provider:     UNDECIDED (DEC-002 still PENDING). CP2 was built to be
                           correct under ALL six candidates, so this does not block it.

Last execution date:       2026-08-29
Last commit:               (this commit)
Executed by:               AKILI /akili-execute -- Leader/Implementer/Reviewer triad,
                           spec changes/migrate-ad-authentication-to-cognito/directory-abstraction
Working branch:            staging-cognito-impl  -- NOTE: this document's header still
                           declares "staging-cognito". The impl branch is authoritative
                           for CP2-CP3; flagged for correction at archive time.
Baseline commit (CP0):     8f88e7822534fa2e1a0e94fa6fb5c90b1195a683
Toolchain verified:        YES -- Java 17.0.12. NOTE: this shell defaults to JDK 1.8,
                           so every Maven command MUST be prefixed with
                           export JAVA_HOME="C:/Program Files/Java/jdk-17".
                           A run without it is disqualified evidence, not a failure.
```

> **CP2 result in one line:** `adauth` is **still the implementation**, **nothing was removed**, and
> **observable behavior is unchanged.** The change is structural only — `marlo-web` business code no
> longer names an AD type. Full evidence, command by command, in the spec's `execution.md`
> (`## CHECKPOINT RESULT — CP2`).
>
> **Two defects in this runbook were found while executing it**, both in `EXEC-040`'s expected output,
> and both corrected in the child spec rather than here (see *Shared-File Write Discipline*):
> its `marlo-data` list **omits `APCustomRealm`** (`judgment.md` JD-1), and its `marlo-web` expectation
> of one importer is **unsatisfiable as written** — it states a post-`EXEC-050` end state at a
> pre-`EXEC-050` checkpoint and scopes to `marlo-web/src`, which sweeps in the test source root.
> **Both are flagged for this document at archive time.**

### Checkpoint ledger

| CP | Name | Status | Completed | Gate |
|---|---|---|---|---|
| 0 | Baseline and safety | **`COMPLETE (SCOPED)`** | 2026-08-28 | Baseline `8f88e78`; **zero drift** across all 19 cited `file:line` refs; toolchain 17 verified. CP0's content is discharged by `DIRABS-T00` per the approved collapse in the child spec's `design.md`. ⚠️ **`EXEC-004`'s baseline build/style/test triple was NOT obtained** — compile was **deferred by user decision**, `checkstyle:check` is **UNVERIFIABLE** in this checkout (`maven-checkstyle-plugin:2.9.1` vs a forced `checkstyle:8.18`; `pom.xml` protected), and **no baseline `mvn test` was recorded**. **No standalone CP0 `CHECKPOINT RESULT` was emitted.** A later task did obtain green `install` + `test` (see CP2), but **not as `EXEC-004`'s pre-migration baseline.** ⚠️ **`EXEC-010`/`EXEC-030` gate on "CP0 = PASS" — read this cell, not the status word, before relying on it** |
| 1 | Cognito authentication | `NOT STARTED` | — | **child 2 `auth-flow`** — not this child |
| 2 | Isolate `adauth` | **`COMPLETE`** | 2026-08-29 | `EXEC-030`…`041` = `DIRABS-T01`…`T13`. `install` + 33 tests green; `D8` closed by the added `DIRABS-T12`; `adauth` still the implementation, nothing removed, behavior unchanged |
| 3 | Remove unnecessary runtime AD usage | `NOT STARTED` | — | `EXEC-050`…`053` = `DIRABS-T14`…`T17`. Removes the unread `new LDAPService()` / `new ADConexion` that execute on **every `searchContact.do` hit** |
| 4 | **Capability B decision gate** | `NOT STARTED` | — | **STOP GATE** |
| 5 | Implement selected Capability B provider | `NOT STARTED` | — | — |
| 6 | Cut over to zero `adauth` runtime usage | `NOT STARTED` | — | **GATE 1** |
| 7 | Stabilization | `NOT STARTED` | — | — |
| 8 | Physical retirement | `NOT STARTED` | — | **GATE 2** |

---

## Decision Registry

Execution **must respect these statuses**. A task whose governing decision is `PENDING` or
`REQUIRES … APPROVAL` **must not be executed**.

| ID | Decision | Status | Owner | Blocks |
|---|---|---|---|---|
| **DEC-001** | Cognito replaces CGIAR authentication; Option A (federated redirect), local form preserved | **APPROVED** — `family.md` Decision Log, 2026-08-24 | Parent proposal | — |
| **DEC-002** | **Capability B provider** — the replacement for `LDAPService.searchUserByEmail()` | **PENDING** | IBD Team lead + CGIAR IT | **CP5 entirely**; CP6 cannot complete |
| **DEC-003** | Stabilization window duration and Gate 1 signing authority | **PENDING** — analysis recommends ≥ 8 weeks / one full reporting cycle (OQ-19) | IBD Team lead | CP7 → CP8 |
| **DEC-004** | Physical removal of `adauth` (Maven, JARs, legacy classes) | **REQUIRES POST-STABILIZATION APPROVAL** | IBD Team lead + Tech lead | **CP8 entirely** |
| **DEC-005** | Add a test-scoped mocking dependency (Mockito) to `marlo-parent` | **PENDING** — analysis §2.8: no mocking framework exists today | Tech lead | Test tasks in CP1, CP2, CP5 |
| **DEC-006** | Infrastructure execution authority — may the agent close firewall rules / disable the AD service account? | **PENDING — assume NO** | Network ops + CGIAR IT | CP8 infrastructure tasks |

### DEC-002 — the format an approval must take

Until a human writes this block into the registry, **CP5 is blocked**:

```text
DEC-002 — Capability B provider
Status: APPROVED
Date:
Approved by:
CAPABILITY_B_PROVIDER = <CORPORATE_DIRECTORY_API | CLARISA | LDAP_BRIDGE | INVITATION_JIT | other>
Rationale:
Evidence that it satisfies:
  - a corporate person who exists in the corporate directory
  - who does NOT yet exist in MARLO users
  - who may NEVER have authenticated through Cognito
  - on a fresh MARLO instance with an empty database
Credentials/config to be provisioned:
Endpoint/contract reference:
```

---

## Protected Actions — the agent MUST NOT do these without explicit approval

| # | Forbidden action | Unblocked by |
|---|---|---|
| P1 | Delete any `adauth` JAR under `marlo-*/src/main/resources/libs/` | DEC-004 + Gate 1 accepted |
| P2 | Remove the `adauth` Maven dependency from any `pom.xml` | DEC-004 + Gate 1 accepted |
| P3 | Delete `LDAPAuthenticator`, `LdapDirectoryService`, `ADLoginMessages`, or any legacy AD class | DEC-004 + Gate 1 accepted |
| P4 | Remove or modify firewall rules | DEC-006 |
| P5 | Disable or modify the AD service account | DEC-006 |
| P6 | Modify production secrets, or any `marlo-${profile}.properties` outside the tracked template | Explicit, per-change |
| P7 | Modify production Cognito configuration (app client, IdP, callback URLs) | Explicit, per-change |
| P8 | Any destructive database operation — `DROP`, `TRUNCATE`, `DELETE` without a `WHERE`, data migrations | Explicit, per-change |
| P9 | Push to any remote, or deploy to any environment | Explicit — **repo push policy: commits stay local** (`CLAUDE.md`) |
| P10 | Merge branches, rebase, force-push, or `git reset --hard` | Explicit |
| P11 | Discard, stash-drop, or overwrite uncommitted work the agent did not create | **Never** — stop and report instead |
| P12 | **Select the Capability B provider** | DEC-002 — this is a human architectural decision |
| P13 | Commit directly to `main` | **Never** (`CLAUDE.md` hard rule 9) |
| P14 | Skip a checkpoint, or execute a later checkpoint's tasks early | **Never** |
| P15 | Downgrade any dependency version in `marlo-parent/pom.xml` | Explicit (`CLAUDE.md` hard rule 11) |

**If a task appears to require a protected action, the agent stops and reports. It does not
improvise a workaround.**

---

## Mandatory session-start procedure

**Every session begins here. No exceptions, including resumed sessions.**

| # | Step | Command / action | Stop if |
|---|---|---|---|
| S1 | Identify the branch | `git rev-parse --abbrev-ref HEAD` | Not `staging-cognito` or an agreed feature branch off it |
| S2 | Inspect working tree | `git status --short` | — |
| S3 | **Identify pre-existing uncommitted changes** | Record every modified/untracked path from S2 that this plan did not create | **Any unrelated uncommitted work exists → report and ask before touching anything** (P11) |
| S4 | Read the Execution State | This document, top | State block is missing or internally inconsistent |
| S5 | Read the relevant analysis section | `adauth-retirement-analysis.md`, the sections named by the next task | The task references a section that no longer exists |
| S6 | **Verify the toolchain** | See `EXEC-001` | `mvn -v` does not report Java 17 |
| S7 | **Verify source files still match the analysis** | Run the drift probe in `EXEC-003` | Any line reference has drifted → **report before modifying code** |
| S8 | Determine the next task | First task whose checkpoint is not `PASS` and whose preconditions hold | Preconditions unmet |
| S9 | Execute **one** task | — | — |
| S10 | Verify, update Execution State, report | — | — |

> **S3 is the one most likely to be skipped and the one that causes the worst damage.** MARLO is a
> shared repository. If `git status` shows work the agent did not create, the correct action is to
> stop and ask — never to stash, revert, or commit it.

---

## How to read a task

Every task below carries the same fields. **All of them are mandatory to satisfy before moving on.**

```text
EXEC-nnn — <title>
  Objective        what it accomplishes
  Why              which analysis finding or principle requires it
  Preconditions    what must already be true
  Files changed    expected paths
  Files PROTECTED  paths that must NOT appear in the diff
  Instructions     concrete steps
  Verification     exact commands and checks
  Expected result  objective definition of success
  Rollback         how to revert this task alone
  STOP if          conditions under which the agent must not continue
```

### Canonical verification commands

Discovered from this repository — **do not substitute alternatives without checking**:

| Purpose | Command | Notes |
|---|---|---|
| Toolchain check | `mvn -v` | **Must report Java 17** — see `EXEC-001` |
| Compile | `mvn -q -o compile -pl marlo-web -am` *(drop `-o` if offline resolution fails)* | |
| Build | `mvn -q install -DskipTests -pl marlo-web -am` | `CLAUDE.md` canonical |
| **Checkstyle gate (mandatory)** | `mvn -q checkstyle:check` | Config: `configuration/marlo-checkstyle.xml` |
| Unit tests | `mvn -q test -pl marlo-web` | Only 3 test classes exist today |
| Package (WAR) | `mvn -q package -DskipTests -pl marlo-web -am` | Needed for WAR content checks in CP8 |
| Run locally | `./scripts/run-marlo-java17.sh` | `docs/infrastructure.md` §6 |
| Diff review | `git diff` · `git diff --stat` · `git status --short` | |

**The `-q` asymmetry:** `-q` suppresses Maven's passing INFO stream only. **Failures print complete
and verbatim** — they are evidence and must never be trimmed in a report.

---

# CHECKPOINT 0 — Baseline and safety

**No functional changes.** This checkpoint exists so that every later failure is attributable to our
changes rather than to the environment or to pre-existing drift.

---

### EXEC-001 — Establish and record the Java 17 toolchain

- **Objective:** guarantee every build/test command in this plan runs on the JDK MARLO targets.
- **Why:** `marlo-parent/pom.xml:74` sets `<java.version>17</java.version>` and `:814` sets
  `<release>${java.version}</release>` [analysis §7.1]. **A verified environment hazard:** the default
  `JAVA_HOME` on the analysis machine was `C:\Program Files\Java\jdk1.8.0_202` and `mvn -v` reported
  **Java 1.8.0_202**. Compiling with `release 17` on a JDK 8 toolchain **fails**, and the failure looks
  like a code error. Without this task, Checkpoint 0 fails for an unrelated reason.
- **Preconditions:** none.
- **Files changed:** none.
- **Files PROTECTED:** all — this task changes no file.
- **Instructions:**
  1. `mvn -v` — record the reported Java version.
  2. If it is not 17, locate a JDK 17. On the analysis machine: `C:\Program Files\Java\jdk-17` exists.
     ```bash
     ls "/c/Program Files/Java"           # Git Bash
     ```
  3. Export it **for the session**, do not modify system settings:
     ```bash
     export JAVA_HOME="/c/Program Files/Java/jdk-17"
     export PATH="$JAVA_HOME/bin:$PATH"
     mvn -v
     ```
     PowerShell equivalent: `$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'`
  4. Record the exact `JAVA_HOME` used in the Checkpoint 0 report.
- **Verification:** `mvn -v` reports `Java version: 17.*`.
- **Expected result:** every subsequent Maven command in this plan runs on JDK 17.
- **Rollback:** none needed — session-scoped environment only.
- **STOP if:** no JDK 17 is available on the machine. Report; do not attempt to lower the project's
  Java level (that would violate P15).

---

### EXEC-002 — Capture the baseline

- **Objective:** record an immutable reference point.
- **Why:** every later "did we break it?" question is answered by diffing against this.
- **Preconditions:** `EXEC-001` PASS.
- **Files changed:** this document's `Execution State` block only.
- **Files PROTECTED:** all source and configuration.
- **Instructions:**
  1. `git rev-parse --abbrev-ref HEAD` → must be `staging-cognito`.
  2. `git rev-parse HEAD` → record as **Baseline commit**.
  3. `git status --short` → record verbatim.
  4. If S3 flagged unrelated uncommitted work, **stop here and report** (P11).
  5. Write the branch, commit SHA, date, and executor into `Execution State`.
- **Verification:** `Execution State` shows a non-empty `Baseline commit`.
- **Expected result:** a recorded starting point.
- **Rollback:** revert the `Execution State` edit.
- **STOP if:** the branch is `main` (P13), or unrelated uncommitted work exists.

---

### EXEC-003 — Verify the repository still matches Revision 3

- **Objective:** confirm the analysis's line references are still accurate before trusting them.
- **Why:** the analysis was written against a specific working tree. A drifted reference silently
  invalidates a later task's instructions. `CLAUDE.md` (CodeGraph staleness) makes the same point.
- **Preconditions:** `EXEC-002` PASS.
- **Files changed:** none.
- **Files PROTECTED:** all.
- **Instructions —** run each probe and compare with the expected value from the analysis:

  ```bash
  # 1. adauth version and declarations  (analysis §2.1)
  grep -n "ciat-adauth.version" marlo-parent/pom.xml            # expect :14 → 5.7
  grep -rn "adauth" marlo-*/pom.xml                             # expect 3 declarations

  # 2. The 8 direct call sites  (analysis §2.3)
  grep -rn "org.cgiar.ciat.auth" marlo-*/src --include="*.java"

  # 3. Capability A sites
  grep -n "new LDAPService()" marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/APCustomRealm.java
  grep -n "new LDAPService()" marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/authentication/LDAPAuthenticator.java

  # 4. Capability B sites
  grep -n "getOutlookUser" -r marlo-web/src --include="*.java"

  # 5. The unused-but-constructed ADConexion  (analysis §2.3 note, §5.3 E1)
  grep -n "ADConexion\|adUsermanager.searchUsers" \
    marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/center/capdev/ContactPersonAction.java

  # 6. Committed AD credentials  (analysis §2.5)
  grep -n "GENERICUSER_AD\|GENERICPASSWORD_AD\|HOSTNAME_AD\|PORT_AD" \
    marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java \
    marlo-web/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java

  # 7. Committed JAR count  (analysis §2.1 — expect 27)
  find marlo-*/src/main/resources/libs/org/cgiar/ciat -name "*.jar" | wc -l

  # 8. Test-suite reality  (analysis §2.8 — expect 3)
  find . -path "*/src/test/*" -name "*.java" -not -path "*/target/*" | wc -l
  ```

- **Verification:** every probe matches the analysis. Record a **drift table** of any mismatch.
- **Expected result:** zero drift, or a documented drift list.
- **Rollback:** n/a — read-only.
- **STOP if:** **any** probe disagrees with the analysis. Report the drift and pause. The working tree
  is the truth; the analysis is the map. **Do not "fix" the code to match the analysis.**

---

### EXEC-004 — Baseline build, style, and test run

- **Objective:** prove the repository is green *before* we touch it.
- **Why:** a pre-existing failure attributed to our change costs a day of the wrong investigation.
- **Preconditions:** `EXEC-001`, `EXEC-003` PASS.
- **Files changed:** none (`target/` output is ignored).
- **Files PROTECTED:** all source and configuration.
- **Instructions:**
  ```bash
  mvn -q install -DskipTests -pl marlo-web -am   # build
  mvn -q checkstyle:check                        # mandatory gate
  mvn -q test -pl marlo-web                      # 3 test classes
  ```
  Record for each: exit code, wall-clock time, and **the complete verbatim output of any failure**.
- **Verification:** all three exit `0`.
- **Expected result:** a recorded green baseline.
- **Rollback:** n/a.
- **STOP if:** any command fails. A red baseline must be understood — and either fixed under a separate
  ticket or explicitly accepted in writing — **before** any migration task begins.

---

### EXEC-005 — Record the runtime call-site inventory

- **Objective:** create the machine-checkable "before" number that Gate 1 will drive to zero.
- **Why:** Gate 1's claim is *"0 runtime `LDAPService` calls, 0 runtime `ADConexion` calls"*
  [analysis §6.3]. That is only provable against a recorded starting inventory.
- **Preconditions:** `EXEC-003` PASS.
- **Files changed:** none. The inventory goes into the Checkpoint 0 report.
- **Files PROTECTED:** all.
- **Instructions:** produce this table, one row per site, from §2.3 and §2.4 of the analysis, and
  confirm each by `grep`:

  | Site | File:line | Capability | Reachable at runtime? |
  |---|---|---|---|
  | 1 | `APCustomRealm:287,295` | A (uses a B call) | Yes — every corporate login |
  | 2 | `LDAPAuthenticator:53,61` | A | Yes — every corporate login |
  | 3 | `BaseAction:4798,4806` | B | Yes — via 2 callers |
  | 4 | `SearchUserAction:193,202` | B | **Probe** — no Struts XML registration (OQ-12) |
  | 5 | `center/.../ManageUsersAction:249,255` | B | **Probe** — no Struts XML registration (OQ-12) |
  | 6 | `ContactPersonAction:86,93` | — | **Yes** — `searchContact.do` is registered (`struts-json.xml:1042`) |
  | 7 | `searchUsersUtil:14` | — | No — `main()` |
  | 8 | `GuestUsersValidator:37,45` | B | Yes |

- **Verification:** the table is complete and each `File:line` was confirmed by `grep`.
- **Expected result:** a recorded baseline inventory of **8 direct sites**.
- **Rollback:** n/a.
- **STOP if:** a site cannot be located.

---

### EXEC-006 — Checkpoint 0 report

Emit the standard `CHECKPOINT RESULT` block (see *Checkpoint report format*), including:
the `JAVA_HOME` used, the baseline commit SHA, the drift table (or "none"), the three command exit
codes, and the runtime call-site inventory.

**Then update `Execution State` and the checkpoint ledger.**

---

# CHECKPOINT 1 — Cognito authentication

**This checkpoint does not invent a flow.** `auth-flow/tasks.md` already specifies tasks
`CHG-COGNITO-AUTH-001-T00` … `T14` with per-task dependencies, verification, and evidence rules.
**That document is authoritative for the *content* of each task; this plan governs the *protocol*.**

> **Do not remove LDAP authentication in this checkpoint.** The realm keeps its
> `UsernamePasswordToken` path byte-for-byte, and the corporate branch stays live for every Global
> Unit whose specificity flag is off. Coexistence *is* the rollback mechanism.

### Configuration mapping (analysis §4.6)

| Standard variable | MARLO property | Rule |
|---|---|---|
| `COGNITO_CLIENT_ID` | `cognito.client.id` | **`@Value("${cognito.client.id:}")` — empty default is mandatory** |
| `COGNITO_CLIENT_SECRET` | `cognito.client.secret` | Same. **Never** commit a value to `marlo-test.properties` |
| `COGNITO_LINK` | `cognito.domain` | Same |
| `COGNITO_REGION` | `cognito.region` | Same |
| `COGNITO_REDIRECT_URI` | `cognito.redirect.uri` | Same — must equal `cognitoCallback.do` exactly |
| `COGNITO_POOL_ID` | `cognito.pool.id` | Same |
| *(added)* | `cognito.logout.uri` | Same |

> **Why the empty default is not optional:** `APConfig`'s 63 existing `@Value` fields use none, and
> `PropertySourcesPlaceholderConfigurer` runs with `ignoreUnresolvablePlaceholders = false`. A bare
> placeholder makes **every environment fail to boot**, including environments that never enable
> Cognito [analysis §4.6].

### Task mapping

Execute in dependency order. Each `EXEC-nnn` wraps the corresponding `auth-flow` task with this
plan's protocol: **inspect → verify preconditions → change → `mvn` → `git diff` → report**.

| EXEC | auth-flow task | Unit | Depends on | Notes |
|---|---|---|---|---|
| EXEC-010 | *(pre-flight)* | Confirm auth-flow's blocking OQs are answered | CP0 | **OQ-3** and **OQ-9** — see below |
| EXEC-011 | `T01` | Extract `finishLogin` from `LoginAction` (behavior-preserving) | EXEC-010 | Includes the deliberate `Referer` null-guard fix at `:290-295` |
| EXEC-012 | `T02` | Specificity: migration + `APConstants` ×2 | EXEC-010 | **Shared writer — serialize** |
| EXEC-013 | `T03` | Dependencies + configuration that cannot break startup | EXEC-010 | The table above. **Adds a JOSE library; does NOT touch `adauth`** |
| EXEC-014 | `T04` | `CognitoAssertion` + `CognitoAuthenticationToken` | EXEC-013 | |
| EXEC-015 | `T05` | `CognitoTokenValidator` — the security core | EXEC-013, EXEC-014 | |
| EXEC-016 | `T06` | Realm token-type dispatch | EXEC-014, EXEC-015 | **`instanceof` guard ABOVE the cast at `APCustomRealm:115`** |
| EXEC-017 | `T07` | Identity mapping: claim → `users` row, with four gates | EXEC-015 | **BLOCKED until OQ-9 is answered** |
| EXEC-018 | `T08` | `CognitoLoginAction` — authorize redirect | EXEC-012, EXEC-013, EXEC-016 | |
| EXEC-019 | `T09` | `CognitoCallbackAction` — validate, gate, rotate, log in | EXEC-011, EXEC-015…018 | |
| EXEC-020 | `T10` | `crpByEmail.do`: per-unit flag + two structural fixes | EXEC-012 | |
| EXEC-021 | `T11` | Harden `validateUser.do` against CGIAR credential relay | EXEC-012, EXEC-020 | |
| EXEC-022 | `T12` | Login wizard: mode composition + DOM removal | EXEC-018, EXEC-020 | **Verify on BOTH host pages** — `login.ftl:17` and `error/401.ftl:19` |
| EXEC-023 | `T13` | i18n keys, including one missing today | EXEC-018, EXEC-019, EXEC-022 | Includes the **login copy** move (analysis §5.2) |
| EXEC-024 | `T14` | Log hygiene + observability | EXEC-019 | |
| EXEC-025 | *(new)* | Authentication test suite | EXEC-016, EXEC-019 | See DEC-005 |
| EXEC-026 | — | Checkpoint 1 report | all above | |

---

### EXEC-010 — Pre-flight: confirm the blocking Open Questions

- **Objective:** confirm the two questions that can invalidate this checkpoint's output are answered.
- **Why:** `auth-flow/tasks.md` §2 declares both as blocking. Building `T07` against the wrong
  identity claim encodes the wrong join key into ~10 tests.
- **Preconditions:** CP0 = PASS.
- **Files changed:** the Decision Registry, if an answer arrives.
- **Files PROTECTED:** all source.
- **Instructions:** verify and record:
  - **OQ-3** — CGIAR IT confirms they will federate MARLO. *Blocks EXEC-018 onward.*
  - **OQ-9** — which claim is the stable identifier (`sub` / `oid` / email). *Blocks EXEC-017.*
  - **OQ-16** — is `clarisa.publicUser` an `is_cgiar_user = 1` account? One SQL query. *Blocks CP6.*
  - **OQ-11** — do Global Unit types 2/5 have corporate users? *Affects CP6 reachability.*
  - **OQ-18** — will the corporate login be mapped to `preferred_username`? **Raise with the
    federation request, not later** [analysis §4.1.4].
- **Verification:** each question has a recorded answer or an owner and a date.
- **Expected result:** EXEC-011…016 and EXEC-020…021 may proceed on OQ-3 alone; EXEC-017 waits on OQ-9.
- **Rollback:** n/a.
- **STOP if:** OQ-3 is answered **no** — the programme returns to the parent proposal (analysis §12.3).

---

### EXEC-011 … EXEC-024 — shared execution protocol

For each task, the agent performs, in order:

1. **Read** the corresponding `CHG-COGNITO-AUTH-001-Tnn` section of `auth-flow/tasks.md` **in full**.
2. **Read** the analysis sections it cites.
3. **Inspect** every file the task names, at the exact lines, **before** editing.
4. **Verify preconditions** — all upstream EXEC tasks are PASS.
5. **Make the change**, and only that change.
6. **Compile:** `mvn -q install -DskipTests -pl marlo-web -am`
7. **Style:** `mvn -q checkstyle:check`
8. **Test:** `mvn -q test -pl marlo-web`
9. **Review:** `git diff` — read it, do not skim. `git diff --stat` for the file list.
10. **Assert the protected set is absent from the diff** (below).
11. **Report** using the task-result format.
12. **Commit** the single task with its `Execution State` update.

**Files PROTECTED across all of Checkpoint 1** — their appearance in any diff is a defect:

| Protected path | Why |
|---|---|
| `marlo-*/pom.xml` — the `adauth` declarations | P2. Adding a JOSE library is fine; touching `adauth` is not |
| `marlo-*/src/main/resources/libs/**` | P1 |
| `security/authentication/Authenticator.java` | Its signature cannot carry an assertion — DD-1 |
| `security/authentication/DBAuthenticator.java` | The local MD5 path is out of scope |
| `security/authentication/LDAPAuthenticator.java` | Capability A's `adauth` binding stays until CP8 |
| `APCustomRealm.doGetAuthorizationInfo()` | Authorization does not participate in this migration |
| `utils/MD5Convert.java`, `users.password` column | No data migration |
| Everything below `APCustomRealm:115`'s cast | The `UsernamePasswordToken` path is byte-for-byte preserved |

**Per-task STOP conditions:**

- The upstream task is not PASS.
- Compilation, checkstyle, or tests fail and the cause is **not** this task's change.
- The diff touches a protected path.
- The task's `auth-flow` section references a file or line that no longer matches (drift → report).
- The change would require selecting the Capability B provider (P12).

**Per-task rollback:** each task is one commit. `git revert <sha>` reverts it independently. Tasks
`T01`, `T06`, and `T09` touch the login path — verify a **local (non-corporate) login still works**
after reverting, not just that it compiles.

---

### EXEC-025 — Authentication test suite

- **Objective:** cover the realm dispatch and the token validator.
- **Why:** analysis §2.8 — zero tests touch authentication today. `mvn test` green proves nothing.
- **Preconditions:** EXEC-016, EXEC-019 PASS. **DEC-005 resolved** (Mockito or hand-rolled doubles).
- **Files changed:** `marlo-web/src/test/java/**` (new).
- **Files PROTECTED:** all production source.
- **Instructions:** implement `CognitoTokenValidatorTest` and `RealmTokenDispatchTest` per analysis
  §5.9. `RealmTokenDispatchTest` must assert the `UsernamePasswordToken` path is **unchanged**, not
  merely that it works.
- **Verification:** `mvn -q test -pl marlo-web`; both suites run and pass.
- **Expected result:** the realm seam is covered in both directions.
- **Rollback:** delete the new test classes.
- **STOP if:** DEC-005 is still `PENDING` and the assertions genuinely require a mocking framework —
  report which assertions, and pause.

---

### EXEC-026 — Checkpoint 1 report

Emit `CHECKPOINT RESULT`. Explicitly confirm:
- local (non-corporate) login is unchanged;
- `adauth` is **still declared, still packaged, still wired** — intentionally;
- the protected file set is absent from the cumulative diff (`git diff --stat <baseline>..HEAD`).

---

# CHECKPOINT 2 — Isolate `adauth`

**Purpose:** MARLO business code must stop knowing about `LDAPService` and `LDAPUser`.

**Not the purpose:** removing `adauth`. The implementation introduced here **delegates to `adauth`**
and preserves current behavior exactly.

**Why it can run now:** the abstraction is identical under all six Capability B candidates
[analysis §4.5], so it is not blocked by DEC-002.

---

### EXEC-030 — Create `DirectoryPerson` and `DirectorySource`

- **Objective:** add the value types. No consumers yet.
- **Why:** analysis §4.5. `LDAPUser` leaks into 6 `marlo-web` imports [analysis §2.4]; nothing can be
  removed while it does.
- **Preconditions:** CP0 PASS. *(Independent of CP1 — may run in parallel, but see shared writers.)*
- **Files changed:**
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/DirectoryPerson.java` *(new)*
  - `marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/DirectorySource.java` *(new)*
- **Files PROTECTED:** everything else.
- **Instructions:**
  - `DirectoryPerson`: immutable, `{ boolean found, String email, String login, String firstName, String lastName, DirectorySource source }`, with a `notFound(email)` factory.
  - `DirectorySource`: `LDAP, DIRECTORY_API, CLARISA, COGNITO_CLAIMS, AD_MIRROR, INVITATION, NOT_FOUND`.
  - **GPL header on both files** (`AGENTS.md`). 2-space indent, 120-char lines.
  - **No `org.cgiar.ciat` import may appear in either file.**
- **Verification:**
  ```bash
  mvn -q install -DskipTests -pl marlo-web -am
  mvn -q checkstyle:check
  grep -rn "org.cgiar.ciat" marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/   # expect empty
  git diff --stat    # expect exactly 2 new files
  ```
- **Expected result:** two new compiling classes, zero behavior change.
- **Rollback:** `git revert <sha>` — nothing references them.
- **STOP if:** checkstyle fails on the GPL header or line length.

---

### EXEC-031 — Create the `DirectoryService` interface

- **Objective:** define `DirectoryPerson findByEmail(String email)`.
- **Why:** analysis §4.5 — the single seam every consumer will use.
- **Preconditions:** EXEC-030 PASS.
- **Files changed:** `.../security/directory/DirectoryService.java` *(new)*.
- **Files PROTECTED:** everything else.
- **Instructions:** one method. Javadoc must state the contract explicitly:
  - a null/blank/malformed email returns `DirectoryPerson.notFound(...)`, **never throws**;
  - a backend failure returns `notFound`, **never throws** — callers degrade, they do not fail
    (analysis R7);
  - `source` is always populated.
- **Verification:** build + checkstyle; `git diff --stat` shows 1 new file.
- **Expected result:** the seam exists; nothing implements it yet.
- **Rollback:** `git revert <sha>`.
- **STOP if:** the interface signature drifts from `findByEmail(String) → DirectoryPerson`.

---

### EXEC-032 — Implement `LdapDirectoryService` (delegates to `adauth`)

- **Objective:** one implementation that reproduces `BaseAction.getOutlookUser()` exactly.
- **Why:** analysis §5.3(a) N4 — this is what makes CP2 behavior-preserving. It is deleted in CP8.
- **Preconditions:** EXEC-031 PASS.
- **Files changed:**
  - `.../security/directory/impl/LdapDirectoryService.java` *(new)*
  - Spring wiring — `@Named` bean, following the existing `@Named("LDAP")` pattern in
    `LDAPAuthenticator`.
- **Files PROTECTED:** `LDAPAuthenticator.java`, all poms, all `libs/**`.
- **Instructions:**
  1. Copy the body of `BaseAction.getOutlookUser()` (`:4797-4811`) **verbatim in behavior**:
     `new LDAPService()`, `setInternalConnection(!config.isProduction())`, `searchUserByEmail(email)`,
     `try/catch → null`.
  2. Map `LDAPUser → DirectoryPerson` with `source = LDAP`; `null → notFound(email)`.
  3. **`org.cgiar.ciat` imports are permitted in this file and nowhere else in the new package.**
- **Verification:**
  ```bash
  mvn -q install -DskipTests -pl marlo-web -am && mvn -q checkstyle:check
  # adauth may appear ONLY here within the directory package:
  grep -rln "org.cgiar.ciat" marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/
  # expect exactly: .../impl/LdapDirectoryService.java
  ```
- **Expected result:** a working `adauth`-backed implementation. **No consumer uses it yet.**
- **Rollback:** `git revert <sha>`.
- **STOP if:** the mapping changes semantics — e.g. lower-casing a field `getOutlookUser` did not
  lower-case. Equivalence is the whole point.

---

### EXEC-033 — Contract and equivalence tests

- **Objective:** prove `LdapDirectoryService` returns what `getOutlookUser` returned.
- **Why:** analysis §5.9 — `DirectoryServiceContractTest` is reused for every future implementation,
  so the CP5 provider swap is covered by construction.
- **Preconditions:** EXEC-032 PASS.
- **Files changed:** `marlo-web/src/test/java/**` *(new)*.
- **Files PROTECTED:** all production source.
- **Instructions:** implement `DirectoryServiceContractTest` (abstract, reusable) and
  `LdapDirectoryServiceTest`. Assert: found/not-found; null and malformed email do not throw; `source`
  always populated; a thrown backend exception surfaces as `notFound`.
- **Verification:** `mvn -q test -pl marlo-web`.
- **Expected result:** the contract is executable and reusable.
- **Rollback:** delete the test classes.
- **STOP if:** a test can only pass by changing production behavior — that means EXEC-032 is not
  equivalent. Fix EXEC-032, not the test.

---

### EXEC-034 … EXEC-039 — Migrate consumers, one per task

**One consumer per task. Compile, test, diff, and verify equivalence after each.**

| EXEC | Consumer | File | Change |
|---|---|---|---|
| EXEC-034 | **`BaseAction`** | `action/BaseAction.java:4797-4811`, imports `:103-104` | `getOutlookUser(String) → LDAPUser` becomes `findCorporateUser(String) → DirectoryPerson`, delegating to the injected `DirectoryService`. **9,748 LOC, very wide caller set — shared writer, serialize** |
| EXEC-035 | `CrpUsersAction` | `:630-658`, import `:48` | Consume `DirectoryPerson`. Preserve the `setCgiarUser(true)` / name / username assignments exactly |
| EXEC-036 | `json/global/ManageUsersAction` | `:151-175`, import `:24` | Same. **Widest surface — 15 FTL pages** |
| EXEC-037 | `GuestUsersValidator` | `:36-50` (delete private helper), `:55`, imports `:23-24` | Inject the service. `found` replaces `LDAPUser != null` — a one-line substitution at `:56` |
| EXEC-038 | `SearchUserAction` | `:191-223`, imports `:30-31` | Same. **Do not delete this class** — OQ-12 is unresolved, and deletion is CP8 |
| EXEC-039 | `center/json/global/ManageUsersAction` | `:248-263`, imports `:24-25` | Unreachable [analysis §2.3], but it must still compile once `LDAPUser` leaves `marlo-web`. **Migrate it; do not delete it** |

**Shared per-task fields:**

- **Objective:** move one consumer behind `DirectoryService` with no behavior change.
- **Why:** analysis §5.3(a) C1–C6; §2.4 type leakage.
- **Preconditions:** EXEC-033 PASS; the previous consumer task PASS.
- **Files PROTECTED:** all poms; all `libs/**`; `LDAPAuthenticator.java`; `APCustomRealm.java`
  *(Capability A — CP1 owns it)*; every other consumer not named in this task.
- **Instructions:** replace the `adauth` call with `directoryService.findByEmail(email)`; map
  `DirectoryPerson` fields onto exactly the same assignments as before; delete the now-unused
  `org.cgiar.ciat` imports **from this file only**.
- **Verification:**
  ```bash
  mvn -q install -DskipTests -pl marlo-web -am && mvn -q checkstyle:check && mvn -q test -pl marlo-web
  git diff                       # read it fully
  git diff --stat                # expect 1 file (+ its test)
  grep -n "org.cgiar.ciat" <the file>     # expect empty
  ```
- **Expected result:** one fewer file importing `adauth`; identical runtime behavior.
- **Rollback:** `git revert <sha>` — each consumer is independently revertible.
- **STOP if:** the diff touches more than the named consumer; or the behavior mapping is not
  one-to-one (e.g. a null that used to mean "not in AD" now means something else).

---

### EXEC-040 — Verify `marlo-web` is free of `adauth` types

- **Objective:** prove the isolation goal is met.
- **Why:** the exit criterion for CP2 [analysis §5.3].
- **Preconditions:** EXEC-034…039 PASS.
- **Files changed:** none.
- **Instructions:**
  ```bash
  grep -rn "org.cgiar.ciat.auth" marlo-web/src --include="*.java"
  # EXPECTED remaining, and only these:
  #   utils/searchUsersUtil.java            (main(), unreachable — deleted in CP8)
  #   action/center/capdev/ContactPersonAction.java  (handled next, in CP3)

  grep -rn "org.cgiar.ciat.auth" marlo-data/src --include="*.java"
  # EXPECTED remaining, and only these:
  #   security/authentication/LDAPAuthenticator.java        (Capability A — CP8)
  #   security/directory/impl/LdapDirectoryService.java     (Capability B — CP8)
  ```
- **Verification:** the grep output matches exactly. Any other file is a missed consumer.
- **Expected result:** **every reachable Capability B path now goes through one interface.**
- **Rollback:** n/a — read-only.
- **STOP if:** an unexpected file appears.

---

### EXEC-041 — Checkpoint 2 report

Emit `CHECKPOINT RESULT`. State explicitly: **`adauth` is still the implementation. Nothing was
removed. Behavior is unchanged.**

---

# CHECKPOINT 3 — Remove unnecessary runtime AD usage

**This is functional retirement, not physical retirement.** It eliminates a runtime `ADConexion`
construction. It deletes no library, no JAR, and no dependency.

---

### EXEC-050 — Eliminate the unused AD object construction in `ContactPersonAction`

- **Objective:** stop constructing `LDAPService` and `ADConexion` on a reachable endpoint.
- **Why:** **analysis §2.3 note and §5.3(b) E1 — verified:** `searchContact.do` **is registered**
  (`struts-json.xml:1042`), so `:86` and `:93` execute on every hit. `adConection` is **never read**;
  the live search is `adUsermanager.searchUsers()` at `:99`; `getADFilter`'s only call is
  **commented out** at `:96-97`. Gate 1 requires **0 runtime `ADConexion` calls** — this is one.
- **Preconditions:** CP2 = PASS.
- **Files changed:**
  `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/center/capdev/ContactPersonAction.java`
- **Files PROTECTED:**
  - `:99` — `adUsermanager.searchUsers(queryParameter)` and everything below it. **The endpoint's
    behavior must not change.**
  - `:58-71` — `getADFilter`. **Leave it.** It has no runtime effect; it is deleted in CP8.
  - `APConstants.java` ×2 — the four AD constants. **Leave them.** Deletion is CP8 (`EXEC-105`).
  - All poms; all `libs/**`.
- **Instructions:**
  1. Delete `:86` (`new LDAPService()`) and `:93` (`new ADConexion(...)`) and the four local
     constant reads at `:88-91`.
  2. Delete the `import org.cgiar.ciat.auth.ADConexion;` and `import org.cgiar.ciat.auth.LDAPService;`
     at `:24-25` **only if `getADFilter` does not need them** — it does not; it builds a `String`.
  3. Leave the commented-out block at `:96-97` untouched, or remove it only as a comment.
  4. **Change nothing else in the method.**
- **Verification:**
  ```bash
  mvn -q install -DskipTests -pl marlo-web -am && mvn -q checkstyle:check
  git diff marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/center/capdev/ContactPersonAction.java
  grep -n "ADConexion\|LDAPService" marlo-web/src/main/java/.../ContactPersonAction.java   # expect empty
  grep -n "adUsermanager.searchUsers" marlo-web/src/main/java/.../ContactPersonAction.java  # expect present
  grep -n "getADFilter" marlo-web/src/main/java/.../ContactPersonAction.java                # expect present
  ```
- **Expected result:** `searchContact.do` returns the same JSON, from `ad_user`, **constructing no AD
  object**.
- **Rollback:** `git revert <sha>`.
- **STOP if:** `adUsermanager.searchUsers()` or any line below `:99` appears in the diff.

---

### EXEC-051 — `ContactPersonActionTest`

- **Objective:** lock the equivalence in.
- **Why:** analysis §5.9 — scenario 21 of the regression matrix depends on it.
- **Preconditions:** EXEC-050 PASS.
- **Files changed:** `marlo-web/src/test/java/**` *(new)*.
- **Instructions:** assert `searchADUser()` returns the same map structure from a stubbed
  `AdUserManager`, and that **no `adauth` type is instantiated** (a hand-rolled spy, or the absence of
  the import, per DEC-005).
- **Verification:** `mvn -q test -pl marlo-web`.
- **Expected result:** the endpoint's contract is covered.
- **Rollback:** delete the test.
- **STOP if:** the test requires production changes.

---

### EXEC-052 — Re-inventory the remaining runtime call sites

- **Objective:** record what is left, against EXEC-005's baseline.
- **Why:** this is the number CP6 must drive to zero.
- **Preconditions:** EXEC-050, EXEC-051 PASS.
- **Instructions:** reproduce EXEC-005's table with a "still reachable?" column. Expected state:

  | Site | Status after CP3 |
  |---|---|
  | `APCustomRealm:287,295` | **Live** — Capability A; goes away when Cognito is at 100% (CP6) |
  | `LDAPAuthenticator:53,61` | **Live** — Capability A; same |
  | `LdapDirectoryService` *(new)* | **Live** — the single Capability B site; swapped in CP5/CP6 |
  | `BaseAction`, `CrpUsersAction`, `ManageUsersAction` ×2, `SearchUserAction`, `GuestUsersValidator` | **Migrated** — no direct `adauth` |
  | `ContactPersonAction` | **Eliminated** |
  | `searchUsersUtil` | Unreachable (`main()`) — deleted in CP8 |

- **Verification:** the table is confirmed by `grep`, and reconciles with EXEC-005.
- **Expected result:** **three live `adauth` call sites remain**, all behind an interface or in the
  Capability A path.
- **Rollback:** n/a.
- **STOP if:** a site the analysis called unreachable turns out to be reachable (probe OQ-12).

---

### EXEC-053 — Checkpoint 3 report

Emit `CHECKPOINT RESULT` including the re-inventory table and its reconciliation with EXEC-005.

---

# CHECKPOINT 4 — Capability B decision gate

> ## ⛔ STOP GATE
>
> **This checkpoint implements nothing. It verifies whether a required human decision exists.**

---

### EXEC-060 — Verify DEC-002

- **Objective:** determine whether a Capability B provider has been explicitly approved.
- **Why:** analysis §4.3–§4.4 — six candidates, none selected, gated on OQ-3b and OQ-15. **P12
  forbids the agent from choosing.**
- **Preconditions:** CP3 = PASS.
- **Files changed:** none.
- **Instructions:**
  1. Read `DEC-002` in the Decision Registry.
  2. If `Status: APPROVED` **and** `CAPABILITY_B_PROVIDER` names one of the permitted values **and**
     the four evidence lines are filled in → proceed to CP5.
  3. Otherwise emit the BLOCKED report below and **stop the session**.

- **Verification:** the registry block is complete and internally consistent.
- **Expected result:** either an approved provider, or a clean BLOCKED stop.
- **Rollback:** n/a.
- **STOP if:** `DEC-002` is `PENDING`, ambiguous, or names a provider that the analysis shows cannot
  satisfy the requirement (e.g. Cognito `ListUsers` alone, or `ad_user` alone — analysis §4.1.6, §4.2).
  **In that case report the contradiction rather than complying.**

---

### EXEC-061 — Emit the decision-request report

When BLOCKED, produce exactly this:

```text
STATUS: BLOCKED

Reason:
Capability B provider has not been selected (DEC-002 = PENDING).
Do not continue with provider implementation.

WHAT HAS ALREADY BEEN SAFELY IMPLEMENTED
  - Cognito authentication (CP1): <list of EXEC tasks + commits>
  - DirectoryService abstraction (CP2): business code no longer references
    LDAPService or LDAPUser
  - Unused runtime AD object construction removed (CP3)
  - adauth remains fully installed and functional — rollback intact

REMAINING adauth RUNTIME DEPENDENCIES
  1. APCustomRealm.getCgiarNickname()        Capability A — clears when Cognito reaches 100%
  2. LDAPAuthenticator.authenticate()        Capability A — same
  3. LdapDirectoryService.findByEmail()      Capability B — REQUIRES THIS DECISION

DECISION REQUIRED
  Select CAPABILITY_B_PROVIDER. It must satisfy, verifiably:
    - the person exists in the corporate directory
    - the person does NOT yet exist in MARLO users
    - the person may NEVER have authenticated through Cognito
    - it works on a fresh MARLO instance with an empty database

  Candidates and their blocking questions (analysis §4.3, §4.4):
    CORPORATE_DIRECTORY_API   blocked on OQ-3b (which IdP does CGIAR run?)
    CLARISA                   blocked on OQ-15 (is there a people endpoint?)
    LDAP_BRIDGE               available, but RETAINS Active Directory
    INVITATION_JIT            blocked on OQ-20 (is the UX change acceptable?)
    Cognito ListUsers         CANNOT satisfy the requirement (analysis §4.1.6)
    ad_user mirror            CANNOT satisfy the requirement (analysis §4.2)

TASKS BLOCKED BY THIS DECISION
  EXEC-070 … EXEC-078   (CP5 — provider implementation)
  EXEC-083 … EXEC-086   (CP6 — cutover and Gate 1)
  All of CP7 and CP8

RECOMMENDED NEXT ACTION
  Answer OQ-15 first — one email to the CLARISA team. The credentials, host, and
  APConfig getters already exist [analysis §4.3 candidate 3], so a "yes" is the
  cheapest possible resolution.
  Answer OQ-3b in the same conversation as OQ-3 with CGIAR IT.
```

---

# CHECKPOINT 5 — Implement the selected Capability B provider

> **Execute only after `EXEC-060` returns APPROVED.**

**Provider-specific types must never leave the implementation class.** MARLO business code sees
`DirectoryPerson` and nothing else.

---

### EXEC-070 — Implement `<Provider>DirectoryService`

- **Objective:** a second `DirectoryService` implementation, using the approved provider.
- **Why:** analysis §5.4.
- **Preconditions:** EXEC-060 = APPROVED; CP3 = PASS.
- **Files changed:** `.../security/directory/impl/<Provider>DirectoryService.java` *(new)*; a
  provider client/token helper if required; `APConfig` for the provider's config keys.
- **Files PROTECTED:** `LdapDirectoryService.java` **(the rollback path — do not modify or delete)**;
  all poms except a deliberate, approved HTTP/SDK dependency addition; all `libs/**`.
- **Instructions:**
  1. Implement `findByEmail`, honoring the EXEC-031 contract: **never throw**, always populate
     `source`.
  2. **Do not reuse `ExternalPostUtils`.** Analysis §2.7 verified it installs a trust-all
     `X509TrustManager` and disables SNI. Use a properly validating client.
  3. All new config keys use `${key:}` empty defaults (analysis §4.6).
  4. Timeouts and a failure path that degrades to `notFound` (analysis R7).
  5. No provider type appears in any signature outside this class.
- **Verification:**
  ```bash
  mvn -q install -DskipTests -pl marlo-web -am && mvn -q checkstyle:check
  grep -rn "ExternalPostUtils" marlo-data/src/main/java/org/cgiar/ccafs/marlo/security/directory/  # expect empty
  git diff --stat
  ```
- **Expected result:** a second implementation exists. **`LdapDirectoryService` is still the active
  one** — the switch is EXEC-073.
- **Rollback:** `git revert <sha>` — nothing is wired to it yet.
- **STOP if:** the provider cannot supply `login` / corporate username — that breaks CLARISA and QA
  (analysis R5). Report before proceeding.

---

### EXEC-071 — `UsernameAllocator`

- **Objective:** deterministic `users.username` synthesis with uniqueness retry.
- **Why:** `users.username` is `unique="true"` (`Users.hbm.xml:19`) and is sent to CLARISA
  (`PartnersSaveAction:553`) and the QA token service (`QAReportsAction:59`) [analysis §3.4].
- **Preconditions:** EXEC-070 PASS.
- **Files changed:** `.../security/directory/UsernameAllocator.java` *(new)* + test.
- **Instructions:** deterministic derivation, collision detection against `users`, bounded retry.
  **Skip only if** the approved provider returns a real corporate login (e.g.
  `onPremisesSamAccountName`) for every user — record that decision if so.
- **Verification:** `mvn -q test -pl marlo-web`; determinism and collision tests pass.
- **Expected result:** no username collision is possible.
- **Rollback:** `git revert <sha>`.
- **STOP if:** OQ-14 is unanswered **and** the provider does not supply a real login — CLARISA's and
  QA's tolerance for a synthesized username is unverified.

---

### EXEC-072 — `CorporateDomainPolicy`

- **Objective:** establish `is_cgiar_user` when the provider is silent.
- **Why:** analysis §3.6 — the flag drives ~10 non-auth behaviors, including whether MARLO generates
  and emails a local password.
- **Preconditions:** EXEC-070 PASS.
- **Files changed:** `.../security/directory/CorporateDomainPolicy.java` *(new)* + test;
  `APConfig` (`directory.cgiar.domains`, default `cgiar.org`).
- **Instructions:** the frontend already uses this heuristic (`crpUsers.js:276`) [analysis §3.4]; make
  the backend consistent with it. Config-driven, not a constant.
- **Verification:** build, checkstyle, test.
- **Rollback:** `git revert <sha>`.
- **STOP if:** the policy would flip an **existing** user's `is_cgiar_user` — it applies to creation
  only.

---

### EXEC-073 — Wire the `directory.source` switch

- **Objective:** make the active implementation a **configuration** choice.
- **Why:** analysis §4.6, §6.3 — **this property is what makes the CP6 cutover and its rollback a
  config flip instead of a deploy.**
- **Preconditions:** EXEC-070 PASS.
- **Files changed:** `APConfig`; a Spring selector/factory in `.../security/directory/`;
  `marlo-test.properties` (key with empty/`LDAP` default).
- **Files PROTECTED:** `LdapDirectoryService.java` — it must remain selectable.
- **Instructions:** `directory.source` ∈ `{LDAP, DIRECTORY_API, CLARISA, INVITATION}`, **default
  `LDAP`**. Log the resolved value once at startup.
- **Verification:**
  ```bash
  mvn -q install -DskipTests -pl marlo-web -am && mvn -q checkstyle:check
  # start locally with the default and confirm the log line says LDAP:
  ./scripts/run-marlo-java17.sh
  ```
- **Expected result:** both implementations are selectable; **the default is still `LDAP`**, so
  behavior is unchanged.
- **Rollback:** `git revert <sha>`.
- **STOP if:** the default resolves to anything other than `LDAP`. Changing the active source is
  EXEC-083, not this task.

---

### EXEC-074 — Provisioning flow

- **Objective:** guarantee a complete `users` row when the provider returns `found = false`.
- **Why:** analysis R4 — `crpUsers.js:274-283` hides the name inputs for `@cgiar.org` addresses,
  expecting the backend to fill them. Without this, corporate guest users are created with null names.
- **Preconditions:** EXEC-071, EXEC-072 PASS.
- **Files changed:** `CrpUsersAction`, `json/global/ManageUsersAction`, `GuestUsersValidator`
  (provisioning branches only).
- **Files PROTECTED:** the realm; `UserManagerImp.saveUser`; the `users` schema (P8).
- **Instructions:** on `found = false` **and** the domain policy says corporate → keep
  `setCgiarUser(true)`, require admin-entered names, allocate a username. On `found = true` →
  unchanged behavior.
- **Verification:** `ManageUsersCreateTest`, `GuestUsersValidatorTest` (analysis §5.9); build; tests.
- **Expected result:** **no path can create a corporate user with a null `first_name`, `last_name`, or
  `username`.**
- **Rollback:** `git revert <sha>`.
- **STOP if:** any path can still produce a null username.

---

### EXEC-075 — Frontend: stop assuming the backend fills names

- **Objective:** show and prefill the name inputs instead of hiding them.
- **Why:** analysis §5.4 — the UI half of R4.
- **Preconditions:** EXEC-074 PASS.
- **Files changed:** `webapp/crp/js/admin/crpUsers.js:274-283`;
  `webapp/WEB-INF/crp/views/admin/crpUsers.ftl:64,67`;
  `webapp/global/js/usersManagement.js:129-139`;
  `webapp/WEB-INF/global/macros/usersPopup.ftl`.
- **Files PROTECTED:** `webapp/global/css/global.css` — login/admin styles live elsewhere.
- **Instructions:** show the inputs; prefill when `found`; leave editable when not. Fix the
  pre-existing `isCgiarUser` vs `isCGIARUser` inconsistency at `crpUsers.ftl:64` vs `:67` (the action
  exposes `isCGIARUser()` at `CrpUsersAction:442`) [analysis §5.4]. Update the
  `help="Not required for CGIAR emails"` copy.
- **Verification:** manual — create a user from `{crp}/crpUsers` **and** from at least 3 of the 15
  pages importing `usersPopup.ftl`, in both the found and not-found cases.
- **Expected result:** an admin can always complete user creation.
- **Rollback:** `git revert <sha>`.
- **STOP if:** any of the 15 popup pages breaks.

---

### EXEC-076 — Provider and provisioning tests

- **Objective:** cover the provider and the degradation path.
- **Why:** analysis §5.9, R7.
- **Preconditions:** EXEC-070…075 PASS.
- **Files changed:** `marlo-web/src/test/java/**`.
- **Instructions:** run `DirectoryServiceContractTest` against the new implementation. Add: hit, miss,
  timeout, 4xx, 5xx. **Assert a provider outage degrades to manual entry and does not fail user
  creation.**
- **Verification:** `mvn -q test -pl marlo-web`.
- **Rollback:** delete the tests.
- **STOP if:** the outage test fails — that is a production incident waiting to happen.

---

### EXEC-077 — End-to-end business-flow verification

- **Objective:** prove the whole chain, manually.
- **Why:** analysis §9.3 scenarios 8, 9, 10, 16, 17.
- **Preconditions:** EXEC-076 PASS.
- **Instructions:** with `directory.source` set to the new provider **in a local/test environment
  only**:

  ```text
  Admin enters corporate email
          ↓  DirectoryService.findByEmail()
          ↓  corporate identity information
          ↓  MARLO creates/updates its OWN users record
  ```

  Verify for each of scenarios 8, 9, 10:
  - `users` row created — **in MARLO, not in Cognito**;
  - `is_cgiar_user` correct;
  - `users.username` non-null and unique;
  - `first_name` / `last_name` populated;
  - a **CLARISA partner request** by that user is accepted (scenario 16);
  - a **QA report link** for that user works (scenario 17).

- **Verification:** a completed scenario table with observed values.
- **Expected result:** the requirement in analysis §1.3 is demonstrably met.
- **Rollback:** revert `directory.source` to `LDAP`.
- **STOP if:** scenario 9 (never logged into MARLO) or scenario 10 (fresh/empty database) fails.
  **These are the defining requirements** — a failure here means the provider was the wrong choice,
  and that is a DEC-002 matter, not a bug to patch.

---

### EXEC-078 — Checkpoint 5 report

Emit `CHECKPOINT RESULT` with the scenario table and the evidence for `is_cgiar_user`,
`users.username`, CLARISA, and QA.

---

# CHECKPOINT 6 — Cut over to zero `adauth` runtime usage → **GATE 1**

> **What this checkpoint MUST NOT do:**
> ```text
> remove the Maven adauth dependency        ✗   (P2)
> delete adauth JARs                        ✗   (P1)
> delete LdapDirectoryService               ✗   (P3 — it is the rollback)
> delete LDAPAuthenticator                  ✗   (P3)
> remove network/firewall access            ✗   (P4)
> remove service-account infrastructure     ✗   (P5)
> ```
> **All of that is intentional.** The library stays installed and functional; only its *usage* goes
> to zero. That is what makes this checkpoint reversible in seconds.

---

### EXEC-080 — Add the `adauth` tripwires

- **Objective:** make any residual runtime call loud and attributable.
- **Why:** analysis §5.5 T1/T2, §9.4 E1 — the primary Gate 1 evidence.
- **Preconditions:** CP5 = PASS.
- **Files changed:** `LdapDirectoryService.java`, `LDAPAuthenticator.java` — **log statements only**.
- **Files PROTECTED:** the behavior of both classes. They must still work when selected.
- **Instructions:** at every entry point, log at **ERROR** with a stack trace:
  - `"ADAUTH-TRIPWIRE: Capability B call reached adauth"`
  - `"ADAUTH-TRIPWIRE: Capability A call reached adauth"`
  Do **not** throw. The rollback path must remain functional.
- **Verification:** build; force one call locally with `directory.source=LDAP` and confirm the line
  appears; restore.
- **Expected result:** any `adauth` call is unmissable.
- **Rollback:** `git revert <sha>`.
- **STOP if:** the tripwire alters control flow.

---

### EXEC-081 — Logback appender and alert

- **Objective:** surface `ADAUTH-TRIPWIRE` operationally.
- **Why:** analysis §5.5 T3.
- **Preconditions:** EXEC-080 PASS.
- **Files changed:** `logback.xml` (locate it first — do not assume the path).
- **Instructions:** a dedicated appender filtering on the marker, routed wherever the environment's
  alerting reads.
- **Verification:** trigger a tripwire locally; confirm it reaches the appender.
- **Rollback:** `git revert <sha>`.
- **STOP if:** no logback configuration is found — report and propose an alternative.

---

### EXEC-082 — Glowroot instrumentation *(optional)*

- **Objective:** method-level proof that `org.cgiar.ciat.auth.LDAPService.*` is never entered.
- **Why:** analysis §9.4 E2. **Glowroot is present in the repo** (`Docker/glowroot/`) [V]; whether it
  runs in the target environment is **[OQ]**.
- **Preconditions:** EXEC-081 PASS.
- **Instructions:** confirm Glowroot runs in the target environment. If it does, add a custom
  instrumentation point. If it does not, **record E2 as not-collected** and rely on E1 + E3 + E4.
- **Verification:** the instrumentation appears in the Glowroot UI.
- **Rollback:** remove the config entry.
- **STOP if:** enabling Glowroot would require a production change (P7).

---

### EXEC-083 — Flip `directory.source`, per environment

- **Objective:** Capability B stops using `adauth`.
- **Why:** analysis §5.5 — the cutover.
- **Preconditions:** EXEC-080…082 done; EXEC-077 PASS.
- **Files changed:** **environment properties only** — `marlo-${profile}.properties`, which is
  gitignored. **No source change.**
- **Files PROTECTED:** all source; all poms; all `libs/**`.
- **Instructions:** flip **one environment at a time**, lowest first (local → test → …). Confirm the
  startup log line, then run scenarios 8, 9, 10, 15 before advancing.
- **Verification:** startup log shows the new source; scenarios pass; **no tripwire fires**.
- **Expected result:** zero Capability B calls through `adauth` in that environment.
- **Rollback:** **set `directory.source=LDAP` and restart. Seconds. No deploy, no rebuild.**
- **STOP if:** a tripwire fires, or any scenario regresses. Roll back that environment immediately.

---

### EXEC-084 — Enable the Cognito specificity for 100% of Global Units

- **Objective:** Capability A stops using `adauth`.
- **Why:** `APCustomRealm.getCgiarNickname()` and `LDAPAuthenticator` run on **every** corporate
  login while any unit still has the flag off [analysis §6.2].
- **Preconditions:** EXEC-083 PASS in the target environment; CP1 complete.
- **Files changed:** none — `custom_parameters` rows, an operational change.
- **Files PROTECTED:** the `parameters` migration; all source (P8 — no destructive DB operations).
- **Instructions:** enable per Global Unit, staged, per the `auth-flow` rollout. Track any unit that
  **cannot** hold the specificity (types 2/5 — OQ-11): **those units block Gate 1** and must be
  reported, not worked around.
- **Verification:** every unit with corporate users has the flag on; no tripwire fires.
- **Expected result:** zero Capability A calls through `adauth`.
- **Rollback:** flag off per unit — seconds.
- **STOP if:** any Global Unit with corporate users cannot be enabled. **Gate 1 cannot pass** — report
  it as an OQ-11 blocker.

---

### EXEC-085 — Run the full regression matrix

- **Objective:** confirm nothing regressed at the cutover.
- **Why:** analysis §9.3 — all 24 scenarios.
- **Preconditions:** EXEC-083, EXEC-084 PASS.
- **Instructions:** execute all 24 scenarios. Pay particular attention to:
  - **9** — corporate user who has never logged into MARLO;
  - **10** — fresh/empty MARLO database;
  - **11** — existing MARLO user who has never authenticated through Cognito;
  - **7** — anonymous Swagger (depends on OQ-16);
  - **6** — `/api/**` Basic auth;
  - **5** — 401 re-authentication on **both** host pages;
  - **21** — `searchContact.do` constructs no AD object.
- **Verification:** a completed scenario table, each with an observed result.
- **Expected result:** 24/24 pass.
- **Rollback:** `directory.source=LDAP` + specificity flags off.
- **STOP if:** any scenario fails.

---

### EXEC-086 — **GATE 1 report**

- **Objective:** the formal record of functional retirement.
- **Preconditions:** EXEC-085 = 24/24.
- **Instructions:** emit, in addition to the standard checkpoint block:

```text
GATE 1 — FUNCTIONAL RETIREMENT

Date:                          Environment(s):
Commit:                        Executed by:

ZERO-USAGE ASSERTIONS
  0 authentication calls through adauth        [ ]  evidence:
  0 corporate-user lookups through adauth      [ ]  evidence:
  0 runtime LDAPService calls                  [ ]  evidence:
  0 runtime ADConexion calls                   [ ]  evidence:
  0 CGIAR passwords processed by MARLO         [ ]  evidence:

STILL PRESENT — INTENTIONALLY
  adauth Maven dependency        PRESENT   (3 declarations)
  adauth JARs                    PRESENT   (27 files, ~6.0 MB, still in the WAR)
  LDAPAuthenticator              PRESENT and wired
  LdapDirectoryService           PRESENT and selectable
  ADLoginMessages                PRESENT
  AD firewall rule               OPEN      (required for evidence E3)
  AD service account             ACTIVE

ROLLBACK PATH
  directory.source = LDAP  +  specificity flags off  →  seconds, no deploy
  Rehearsed:  [ ] yes, twice

REGRESSION
  Scenarios passed:  __ / 24
  Failures:

CONFIRMED NEXT STEP
  CHECKPOINT 7 — Stabilization.
  Physical retirement is FORBIDDEN until Gate 1 is accepted AND the
  stabilization window has been formally accepted (DEC-003, DEC-004).
```

- **STOP if:** any zero-usage assertion is unproven. **A Gate 1 report with an unchecked box is not a
  Gate 1 pass.**

---

# CHECKPOINT 7 — Stabilization

**This is validation and observation, not continuous engineering.** Effort is roughly 0.5–1 day per
week across the window [analysis §7.4].

> ## ⛔ **Physical retirement is forbidden until Gate 1 has passed AND the stabilization period has been formally accepted.**
> No task in CP8 may begin while this checkpoint is open. This overrides any instruction to "finish
> the migration".

---

### EXEC-090 — Open the window

- **Preconditions:** EXEC-086 = PASS; **DEC-003 approved** (duration + signing authority).
- **Instructions:** record start date, planned end date, signing authority, and the environments in
  scope. **Confirm the AD firewall rule is still OPEN** — it is the instrument for evidence E3
  [analysis §10.1].
- **STOP if:** DEC-003 is `PENDING`. A window with no agreed end is not a window.

---

### EXEC-091 — Weekly evidence collection

Collect and record every week [analysis §9.4]:

| ID | Evidence | Method | Threshold |
|---|---|---|---|
| E1 | Tripwires never fire | Log search for `ADAUTH-TRIPWIRE` | **0** occurrences |
| E2 | No LDAP method entered | Glowroot *(if EXEC-082 succeeded)* | **0** invocations |
| E3 | No network egress to AD | Firewall hit counters on `*.cgiarad.org:3268` and `:636`, **rule still open** | **0** packets |
| E4 | No JNDI LDAP context | Log inspection for `com.sun.jndi.ldap` | **0** contexts |

**Any non-zero value ends the window and reopens Checkpoint 6.**

---

### EXEC-092 — Periodic scenario re-run

Re-run the full 24-scenario matrix at the **start** and **end** of the window. Re-run scenarios
**9, 10, 11, 14, 15** monthly. (E5)

---

### EXEC-093 — Usage representativeness

- **Why:** analysis §9.4 E6 — *"a quiet window proves nothing."*
- **Instructions:** count distinct corporate logins and corporate users created during the window.
  Confirm the window spans at least one full reporting cycle (E7).
- **STOP if:** corporate user creation did not occur. **Extend the window; do not pass the gate.**

---

### EXEC-094 — Rollback rehearsal

Flip `directory.source` to `LDAP` and back, in a non-production environment, **twice**. Record both.
(E8)

---

### EXEC-095 — Gate 1 acceptance package

Assemble E1–E8 into a single signed artifact. **DEC-003's named authority signs it.** Without that
signature, CP8 does not begin.

---

# CHECKPOINT 8 — Physical retirement → **GATE 2**

> **Preconditions for the entire checkpoint:**
> - `EXEC-095` acceptance package **signed**;
> - **DEC-004 = APPROVED**;
> - Gate 1 held for the full window with zero evidence violations.
>
> **If any is missing, the whole checkpoint is BLOCKED.**

---

### EXEC-100 — Verify authorization

- **Instructions:** confirm the signature, DEC-004's status, and the E1–E8 record. Record the
  approving names and date.
- **STOP if:** anything is missing. Emit `BLOCKED` and stop the session.

---

### EXEC-101 … EXEC-111 — Code and dependency removal

**One task per row. Compile, checkstyle, test, and diff after each.** Analysis §5.7.

| EXEC | Removal | Files |
|---|---|---|
| EXEC-101 | Realm: `getCgiarNickname()` `:285-312`, the `isCgiarUser()` branch `:136-142`, the `ldapAuthenticator` field/param `:83,:88,:93`, imports `:28-29` | `APCustomRealm.java` |
| EXEC-102 | Shiro wiring: `LDAPAuthenticator` out of the `apCustomRealm` bean signature `:44-49`, import `:31` | `MarloShiroConfiguration.java` |
| EXEC-103 | **Delete** `LDAPAuthenticator.java` (92 LOC) | `security/authentication/` |
| EXEC-104 | **Delete** `LdapDirectoryService.java` — the last Capability B site | `security/directory/impl/` |
| EXEC-105 | `ADLoginMessages` — delete, or reduce to `LOGON_SUCCESS` + `USER_DISABLED`; reduce both 10-case switches (`LoginAction:118-149`, `ValidateUserAction:102-133`) — **they must move together** | 3 files |
| EXEC-106 | **Delete** `searchUsersUtil.java`; `ContactPersonAction.getADFilter` `:58-71`; `center/json/global/ManageUsersAction.java` **(subject to OQ-12 — probe first)** | 3 files |
| EXEC-107 | Delete `GENERICUSER_AD`, `GENERICPASSWORD_AD`, `HOSTNAME_AD`, `PORT_AD` (**data** `:645-649`, **web** `:705-709`) | `APConstants.java` ×2 |
| EXEC-108 | **Remove the Maven dependency** — `marlo-parent:14,299-303`; `marlo-data:26-30` **and its `<repositories>` block `:199-204`**; `marlo-web:75-78` (**keep** `marlo-web`'s `<repositories>` — it also serves `pentaho`, `rhino`, `org/fife`) | 3 poms |
| EXEC-109 | **Delete the committed JARs** — `marlo-data/src/main/resources/libs/` entirely (16 versions, 3.6 MB); `marlo-web/src/main/resources/libs/org/cgiar/` (11 versions, 2.4 MB). **Do not touch `libs/org/fife` or `libs/org/pentaho`** | 2 trees |
| EXEC-110 | Remove tripwires T1–T4 and the logback appender | 2–3 files |
| EXEC-111 | i18n residue + docs: TRD §8.1/§8.4, `infrastructure.md`, `reports/ai-context/*` | docs + properties |

**Per-task rollback:** each is one commit; `git revert <sha>`. **EXEC-108 and EXEC-109 together are
the point of no return in practice** — reverting them requires restoring 6 MB of binaries from git
history. Verify the build **before** committing them.

**STOP conditions, all tasks:** compilation fails; checkstyle fails; any test fails; the diff touches
`libs/org/fife` or `libs/org/pentaho`; `marlo-web`'s `<repositories>` block is removed.

---

### EXEC-112 — WAR verification

```bash
mvn -q package -DskipTests -pl marlo-web -am
unzip -l marlo-web/target/*.war | grep -i adauth        # expect EMPTY
unzip -l marlo-web/target/*.war | grep -ciE "pentaho|rhino"   # expect NON-ZERO
mvn dependency:tree -Dincludes=org.cgiar.ciat.auth      # expect no matches, all 5 modules
```

**STOP if:** any `adauth` artifact remains in the WAR, or an unrelated library disappeared from it.

---

### EXEC-113 — Infrastructure cleanup — **recommendations, not execution**

- **Objective:** produce the exact actions for network ops and CGIAR IT.
- **Why:** **P4, P5, DEC-006 — the agent does not execute these.**
- **Instructions:** emit a request document containing, in this order [analysis §10.3]:

```text
INFRASTRUCTURE CLEANUP — REQUESTED ACTIONS  (execute in this order)

  1. Observe 2 weeks post-removal, firewall rule STILL OPEN.
     Expect: zero hits on *.cgiarad.org:3268 and :636.
  2. Close the outbound firewall rules:
       *.CGIARAD.ORG      port 3268   (plaintext Global Catalog)
       *.cgiarad.org      port 636    (LDAPS)
     Hosts: ciatroot4.CGIARAD.ORG, ciatroot5.CGIARAD.ORG,
            azcgccroot1.cgiarad.org, azcgccroot4.cgiarad.org,
            ciatroot1.ciat.cgiarad.org
  3. Observe 1 further week. Expect zero authentication or user-creation errors.
  4. CGIAR IT: disable the AD service account 'ldapuser'.
     NOTE: its password is in this repository's git history — rotation or
     disablement is the only real remediation.
  5. CIAT infra: retire repos.ciat.cgiar.org as an adauth source (optional).

  ORDER MATTERS. Closing the firewall before disabling the account makes any
  failure surface as a network error, which is diagnosable. Reversing them makes
  it surface as an authentication error, which looks like an application bug.
```

- **Expected result:** a handoff document. **No infrastructure was changed by the agent.**
- **STOP if:** anyone asks the agent to execute steps 2–5 without DEC-006.

---

### EXEC-114 — **GATE 2 report**

Run the full §11.2 checklist from the analysis (items 26–47) and emit:

```text
GATE 2 — PHYSICAL RETIREMENT

Date:                     Commit:              Executed by:
Approval (DEC-004):       Gate 1 acceptance:

CHECKS 26–47:  __ / 22 passed
Failures:

grep -rn "org.cgiar.ciat.auth" marlo-*/src        →   (expect empty)
mvn dependency:tree -Dincludes=org.cgiar.ciat.auth →   (expect empty)
find marlo-*/src/main/resources/libs -name "*.jar" →   (expect no adauth)
unzip -l marlo-web/target/*.war | grep adauth      →   (expect empty)

INFRASTRUCTURE
  Firewall closure:       REQUESTED / DONE / NOT AUTHORIZED
  Service account:        REQUESTED / DONE / NOT AUTHORIZED

RESULT:  adauth is physically retired from the MARLO codebase.
```

---

## Checkpoint report format

Emit at the end of **every** checkpoint. `FAIL` or `BLOCKED` **stops execution**.

```text
CHECKPOINT RESULT

Checkpoint:            CP-n — <name>
Status:                PASS / FAIL / BLOCKED

Tasks completed:       EXEC-nnn … (with commit SHAs)

Files changed:         <git diff --stat against the checkpoint's start commit>

Tests executed:        <commands + exit codes>
Compilation result:    <mvn install exit code>
Checkstyle result:     <mvn checkstyle:check exit code>

Behavior verified:     <what was manually confirmed, and how>

adauth state:          Maven: PRESENT/REMOVED
                       JARs:  PRESENT/REMOVED
                       Runtime calls observed: <n>

Open issues:
Unexpected findings:   <anything contradicting the analysis — flag it, do not
                        silently adapt>

Recommended next action:
```

---

## Akili Execution Prompt

Copy this into a new session to run the plan.

```text
Execute the adauth retirement plan against the MARLO repository.

SOURCE DOCUMENTS — read both before doing anything:
  1. docs/specs/changes/migrate-ad-authentication-to-cognito/analysis/
       adauth-retirement-execution-plan.md    ← the runbook you follow
  2. docs/specs/changes/migrate-ad-authentication-to-cognito/analysis/
       adauth-retirement-analysis.md          ← the authoritative WHY

SESSION START — mandatory, in this order:
  1. git rev-parse --abbrev-ref HEAD    (must be staging-cognito)
  2. git status --short
  3. Identify any uncommitted work you did not create. If any exists,
     STOP and report. Never stash, revert, or commit someone else's work.
  4. Read the Execution State block at the top of the execution plan.
  5. Read the analysis sections cited by the next task.
  6. Verify the toolchain: mvn -v MUST report Java 17.
     If not: export JAVA_HOME="/c/Program Files/Java/jdk-17"
  7. Run the EXEC-003 drift probes. If any source line has moved from what the
     analysis states, STOP and report the drift before modifying code.

THEN:
  8. Determine the current checkpoint and the next task from Execution State.
  9. Execute EXACTLY ONE task. Not two. Not "the rest of the checkpoint".
 10. Verify it: mvn install -DskipTests -pl marlo-web -am
                mvn checkstyle:check
                mvn test -pl marlo-web
                git diff        (read it in full)
 11. Confirm no PROTECTED file appears in the diff.
 12. Update the Execution State block and the checkpoint ledger.
 13. Commit the task together with the Execution State update.
 14. Emit the task result, then the CHECKPOINT RESULT if the checkpoint closed.

HARD RULES:
  - Respect the Decision Registry. A task whose decision is PENDING is BLOCKED.
  - Respect the Protected Actions list (P1–P15) without exception.
  - You may NOT select the Capability B provider. That is DEC-002, a human
    decision. If you reach CHECKPOINT 4 without it, emit the BLOCKED report
    from EXEC-061 and stop.
  - You may NOT remove the adauth Maven dependency, delete the JARs, or delete
    LDAPAuthenticator / LdapDirectoryService before Gate 1 is accepted AND
    DEC-004 is approved. Reaching "zero runtime usage" is the goal; deleting
    the library is a separate, later, approved step.
  - You may NOT touch firewall rules, the AD service account, production
    secrets, or production Cognito configuration.
  - Never skip a checkpoint or jump ahead to physical retirement, even if the
    remaining work looks trivial.
  - On FAIL or BLOCKED: stop the session and report. Do not improvise.
  - If the repository contradicts the analysis, the REPOSITORY is right.
    Report the discrepancy; do not edit code to match the document.

MODEL:  inspect → implement one controlled unit → verify → report →
        continue only when safe.

Begin with the session-start procedure and tell me which task you intend to run
before you run it.
```

---

## Appendix — Task index

| Checkpoint | Tasks | Gate |
|---|---|---|
| **CP0** Baseline and safety | EXEC-001 … EXEC-006 | — |
| **CP1** Cognito authentication | EXEC-010 … EXEC-026 | — |
| **CP2** Isolate `adauth` | EXEC-030 … EXEC-041 | — |
| **CP3** Remove unnecessary runtime AD usage | EXEC-050 … EXEC-053 | — |
| **CP4** Capability B decision | EXEC-060 … EXEC-061 | ⛔ **STOP GATE** |
| **CP5** Implement provider | EXEC-070 … EXEC-078 | — |
| **CP6** Cut over | EXEC-080 … EXEC-086 | ✅ **GATE 1** |
| **CP7** Stabilization | EXEC-090 … EXEC-095 | — |
| **CP8** Physical retirement | EXEC-100 … EXEC-114 | ✅ **GATE 2** |
