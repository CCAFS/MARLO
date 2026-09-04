# CGIAR Authentication via Amazon Cognito — Execution Log

**Spec ID:** `CHG-COGNITO-AUTH-001`
**Spec path:** `changes/migrate-ad-authentication-to-cognito/auth-flow`
**Harness:** none — **direct single-agent implementation**, not the `/akili-execute` triad. See §1.2.
**Working branch:** `staging-cognito-impl` — **the authoritative branch for this migration.** · **Target merge: NONE.** Superseded 2026-09-02: no promotion is authorised. The next step is a **dedicated migration environment**, where this branch is deployed and the federated path is validated for the first time. See §27.
**Baseline commit:** `5ff42642e9`

---

## 1. Document Control

| Element | Value |
|---|---|
| Created | 2026-08-31 |
| Last appended | 2026-08-31 |
| Tasks total | 14 (`T00` … `T14`) |
| Tasks complete | 9 (`T01`–`T06`, `T08`, `T10`, `T11`). **`T01` closed at commit `046c31e7dc`**, whose body names its behavioral change; `T03` still has its live-boot clause open (PS-7). `T04` was reopened by T06 and re-closed |
| Tasks reachable on this branch | **6** (`T01`–`T06`). `T07` is blocked by OQ-9, `T12` onward by OQ-3 |
| Review rounds consumed | **12** — T01+T02 (§4), T03+T04 (§6) on `sonnet`; T05 (§7), T06 (§8, §9, §10), T08 (§11, §12), T10 (§13, §14), T11 (§15 FAIL, §16 PASS) on `opus`. **Four tasks FAILed at least once; T06 twice** |

### 1.1 How this spec arrived on this branch

`auth-flow/` was authored and approved on `staging-cognito` (6 commits this branch does not have) and was
**deliberately not duplicated here** — `family.md` § *Cross-branch state* records that duplicating 1,647 lines
would create two authorities for one child.

On 2026-08-31 the user directed that all work continue on `staging-cognito-impl` through to testing, with the
move to `staging` decided at a later validation. The four spec files were therefore brought across with:

```
git checkout staging-cognito -- docs/specs/changes/migrate-ad-authentication-to-cognito/auth-flow/
```

**This is a verbatim git-level transfer, not a re-specification.** The content is byte-identical to
`staging-cognito`'s, so the two branches do not diverge on these files and a later merge sees the same blob on
both sides. The checkout was scoped to `auth-flow/` precisely because the parent folder on `staging-cognito`
carries an **older two-child `family.md`**; an unscoped checkout would have silently reverted this branch's
three-child manifest, which is the newer authority. Post-transfer check: `family.md` still lists 3 children.

**This log is the point at which `auth-flow/` starts to diverge from `staging-cognito`.** From here on,
`tasks.md` (status lines) and this file are branch-local. That is the accepted merge cost of the user's
decision, and it is now one more file than the single-file conflict `family.md` predicted.

### 1.2 Governance gap — read before trusting any PASS in this log

The AKILI methodology binds `/akili-execute` to a Leader → Implementer → Reviewer triad in which the Reviewer
runs a **different model** than the Implementer and holds **no write tools** (root `CLAUDE.md` → *Enforced
bindings*). `author ≠ auditor` is described there as structural, not a preference.

**None of that applied to T01 or T02.** Both were implemented directly by a single agent that also wrote their tests and
declared its own result. Every gate recorded below is real and reproducible — the commands and their output are
given — but **no independent party checked the work**. The correct reading of `T01 PASS` in this log is
"the author's gates are green", not "the task is reviewed".

Closing this is a decision for the user: either run an audit over the accumulated diff, or accept the
gap explicitly and record it.

---

## 2. Environment blockers

| # | Blocker | Effect | Status |
|---|---|---|---|
| **EB-1** | **`mvn checkstyle:check` cannot run in this checkout.** `marlo-parent/pom.xml:827-833` pins `maven-checkstyle-plugin:2.9.1` against `checkstyle:8.18`. Plugin 2.9.1 calls `Checker.setClassloader(ClassLoader)`, removed in Checkstyle 8.x, so the goal dies with `NoSuchMethodError` **before reading a single source file** | The root guides list Checkstyle as a **Required hard gate**. It is currently unrunnable as configured, repo-wide and independent of any spec | **Open.** Not fixed here: `marlo-parent/pom.xml` is child 2's file only for the AWS SDK and JOSE additions in T03, and a plugin re-pin is outside T01's scope. Worked around per below |
| **EB-2** | `JAVA_HOME` on this machine points at `jdk1.8.0_202` while `java` on `PATH` is 17. Maven follows `JAVA_HOME`, so every `mvn` invocation compiled under Java 8 and failed with `invalid flag: --release` | A `mvn install -DskipTests` run can report success while compiling **nothing** ("Nothing to compile - all classes are up to date"), which is how the first baseline in this session came back falsely green | **Worked around.** Every command in §3 is prefixed `JAVA_HOME="/c/Program Files/Java/jdk-17"`. Any future session on this machine must do the same or its gates are worthless |
| **EB-3** | **Checkstyle enforces nothing.** `configuration/marlo-checkstyle.xml:7` sets `severity=warning` for every module and `LineLength` (`:41-43`) does not override it, so `checkstyle:check` **exits 0 over real violations** — verified against a 149-character line | Every "Checkstyle: 0 violations" recorded in §3 for T01–T06 means only "the goal ran"; it is **not** evidence that hard rule 7 holds. Found by the T06 audit | **Open.** A `severity=error` flip is a shared-config change and would surface pre-existing violations across the repository; sized and decided separately from this migration (§27.4 Category B). Until then, line length is measured directly (`awk 'length>120'`), not via the plugin |
| **EB-4** | **Incremental build state produces both false reds and false greens in this checkout.** Observed in one session: a `cannot find symbol` in a file nobody touched; a `.class` with *"Unresolved compilation problems"* baked in; an installed `marlo-utils` missing methods its source has; and a run reporting **`Tests run: 0`**. All cleared by targeted `rm -rf target/…`, none related to any change. Reported independently by the T08 implementer | **A test count from this repository is only trustworthy after a `clean`.** An intermediate green or red says nothing on its own | **Open.** Every count recorded from T08 onward is taken after `mvn clean -pl marlo-web`. Earlier counts in this log were not, and should be read as indicative |
| **EB-5** | **`pkill` does not exist in this environment, and `scripts/run-marlo-java17.sh` depends on it.** The script's "stop any previous container" step fails silently, so a prior `cargo:run` keeps running, holds port 8080, and leaves `marlo-web/target/cargo/configurations/tomcat9x` populated. The next boot then dies on `Invalid configuration dir [...] must point to an empty directory` while still printing `BUILD SUCCESS` and `Press Ctrl-C to stop the container...` | **A boot can report success without ever starting a container**, and a `curl` against 8080 then answers from the *stale* one. Both readings of that state — "it built but does not serve", and "it serves" — are wrong | **Open**, worked around. Substitute: `Get-NetTCPConnection -LocalPort 8080` to find the PID, then `taskkill //PID <pid> //F`, then `rm -rf marlo-web/target/cargo`. Item for the migration environment (§27.4 Category B): make the script kill step portable. **Sharpened 2026-09-02:** stopping the *launcher* does not stop the container either — the background shell running the script was killed and the Maven JVM kept listening on 8080 regardless. A live port 8080 is therefore never evidence that the boot you just ran succeeded; check the PID against the boot you intended |

**EB-1 workaround, and its limit.** Style was verified by invoking a *compatible* plugin against MARLO's own
config, without editing any POM:

```
mvn -pl marlo-web org.apache.maven.plugins:maven-checkstyle-plugin:3.3.1:check \
  -Dcheckstyle.config.location=../configuration/marlo-checkstyle.xml \
  -Dcheckstyle.includeTestSourceDirectory=true
```

→ `EXIT=0`, zero violations, test sources included. **This is real evidence about the code and no evidence at
all about the gate.** The project's own gate remains broken, and a task that reports "Checkstyle passes" while
running the pinned plugin is reporting a crash it did not read.

---

## 3. Task log

### CHG-COGNITO-AUTH-001-T01 — Extract `finishLogin` from `LoginAction`

**Result: PASS (author-declared, unreviewed — see §1.2)** · 2026-08-31 · attempt 1

**Files changed (1 production, 1 test):**

| File | Change |
|---|---|
| `marlo-web/.../action/home/LoginAction.java` | `login(User, GlobalUnit)` reduced to a one-line caller; body moved to `protected String finishLogin(User, GlobalUnit, String returnUrl)`; the `Referer` read moved up into the caller; null guard added |
| `marlo-web/src/test/java/.../action/home/LoginActionFinishLoginTest.java` | **new** — 5 tests, 4 hand-rolled manager doubles, one `Proxy`-based `HttpServletRequest` |

**Exactly one behavioral change**, as T01 requires: `if (urlAction.contains(".do") …)` became
`if (urlAction != null && urlAction.contains(".do") …)`. The `user`-field dereferences T01 protects were not
touched — the success log line still reads the field, and T08 remains what makes it non-null on the Cognito path.

#### How the equivalence claim was earned

T01's *Not evidence when* clause rules out routing tests written by reading the extracted method. So four of the
five tests drive the **public `login(User, GlobalUnit)` entry point**, never `finishLogin`, which means the exact
same assertions run before and after the extraction.

**Run against the pre-extraction code** (the RED state, recorded because it is the evidence):

```
Tests run: 4, Failures: 1, Errors: 0
absentRefererDoesNotThrowAndFallsThroughToTypeRouting:
  NullPointerException: Cannot invoke "String.contains(java.lang.CharSequence)" because "urlAction" is null
```

That NPE is T01's *Fails when* condition, observed rather than asserted: three tests pinned existing behavior
green, and the fourth proved the unguarded dereference is real. **A null-guard test that had never been run
against the old code would have been decorative** — this one was.

**Run after the extraction:** `Tests run: 5, Failures: 0, Errors: 0`.

#### Two test assumptions the RED run falsified

Both were the author's errors, not the code's, and both are recorded because a green suite that was edited into
greenness is worth less than one that was corrected for a stated reason:

1. **`LOGIN` is `"login"`, not `"LOGIN"`.** It is `com.opensymphony.xwork2.Action.LOGIN`, inherited through
   `ActionSupport`; MARLO declares no such constant. The tests now assert `Action.LOGIN` / `Action.SUCCESS` /
   `Action.INPUT` rather than string literals, so a future rename cannot leave them silently passing.
2. **The routing test originally passed a null `Referer`**, which made it trip the very NPE that a *different*
   test owns — leaving it un-runnable against the old code and worth nothing as equivalence evidence. It now
   passes a benign non-`.do` URL, so it measures routing and only routing.

#### The fifth test, and why it is different in kind

`finishLoginAcceptsAnExplicitReturnUrlWithNoRequestPresent` calls `finishLogin` directly with no request
installed at all — how T09's `CognitoCallbackAction` will call it, since a federated callback has no `Referer`.
It **cannot** exist before the extraction, so it is **not** equivalence evidence. It is reachability evidence:
proof the seam T08 and T09 depend on is actually usable. It is labelled as such in the source.

#### Test-infrastructure decisions

- **No new dependency, no POM edit.** `marlo-parent/pom.xml` is where child 2 adds the AWS SDK, and `DEC-005`
  (test-scoped Mockito) is still `PENDING`; touching it here would create exactly the collision `family.md`
  § *Parallel-safety* warns about. Collaborators are hand-rolled doubles, matching the pattern child 1
  established in `CrpUsersActionDirectoryTest`.
- **`HttpServletRequest` is a `java.lang.reflect.Proxy`** answering only `getHeader("Referer")` — the single
  request interaction this path makes — and type-correct defaults elsewhere. `spring-test` is present in the
  local `~/.m2` but was not used, for the same POM-avoidance reason.
- **`ActionContext.of(…).bind()` is required before `ServletActionContext.setRequest(…)`** on Struts 6.8:
  `setRequest` delegates to `ActionContext.getContext()`, which is null outside a live request. The first run
  failed on this with 8 errors and no useful signal. `ActionContext.clear()` in `@After`.
- **The surefire-2.12.4 scanner hazard carries over from child 1**: no method declared on the test class may
  name `LoginAction` in its signature, or the scanning fork dies with a bare `NoClassDefFoundError` before any
  test runs. The reflection helpers take `Object`.

#### Gates

| Gate | Command | Result |
|---|---|---|
| Compile | `JAVA_HOME=<jdk-17> mvn -q install -DskipTests -pl marlo-web -am` | **EXIT=0** |
| Tests (whole module) | `JAVA_HOME=<jdk-17> mvn -pl marlo-web test` | **44/44 PASS**, 0 failures, 0 errors |
| Checkstyle | plugin `3.3.1` + `configuration/marlo-checkstyle.xml`, test sources included | **EXIT=0**, 0 violations — see EB-1 for what this does and does not prove |

The 44 figure is consistent with the kaizen entry's independent measurement of **39** passing tests before this
task: 39 + 5 new = 44. It also re-confirms that root `CLAUDE.md`'s "3 JUnit 4 test files" claim is stale
(kaizen **P7** — recorded, not owned by this spec; §27.4 Category C).

#### Not done, deliberately

T01's *Done when* also requires the single behavioral change to be **called out in the commit body**. Nothing
has been committed yet; that obligation moves to whoever commits this work.

---

### CHG-COGNITO-AUTH-001-T02 — Specificity: migration + constants

**Result: PASS** · 2026-08-31 · attempt 1 · **audited, see §4**

**Key chosen: `cognito_auth_active`. Constant: `COGNITO_AUTH_ACTIVE`.** Neither `design.md` nor `tasks.md` pins
the literal string — they specify the row shape and the two-file rule but leave the name open. It was derived
from the convention actually in use (`ai_section_active`, `crp_bi_module_active`, `portfolio_feature_active`):
lowercase snake, `_active` suffix for a boolean switch, no `crp_` prefix since this is not CRP-scoped.

**Files changed (4 — one more than `tasks.md` lists, see below):**

| File | Change |
|---|---|
| `database/migrations/V2_6_0_20260831_0900__AddCognitoAuthSpecificity.sql` | **new** — 3 rows, types 1/3/4, `format='1'`, `default_value='false'`, `category='2'` |
| `marlo-web/.../config/APConstants.java` | **new constant** |
| `marlo-data/.../config/APConstants.java` | **same constant, identical value** |
| `scripts/verify-specificity-constants.sh` | **new** — see *The grep T02 demands* |

**Version ordering checked, not assumed.** Flyway's history table in this schema is `schema_version` (not
`flyway_schema_history`), and its newest applied entry is `2.6.0.20260827.0747`. The new migration is
`2.6.0.20260831.0900`, which sorts after it.

#### The grep T02 demands

T02's *Fails when* clause says the two-file rule must be checked mechanically: *"Write that grep; without it the
two-file rule is checked by eye."* `scripts/verify-specificity-constants.sh <CONSTANT>` compares three artifacts
that must agree on one string — the `marlo-web` constant's value, the `marlo-data` constant's value, and the
`parameters`.`key` the migration INSERTs — and exits non-zero on any disagreement.

It was proven to fail, not merely written. With one character changed in the `marlo-web` constant:

```
FAIL: the two APConstants disagree on COGNITO_AUTH_ACTIVE
  marlo-web  = 'cognito_auth_activo'
  marlo-data = 'cognito_auth_active'
FAIL: no migration INSERTs a parameters.key matching 'cognito_auth_activo'
EXIT=1
```

Restored, it exits 0. Run as a control against an unrelated existing specificity
(`HOMEPAGE_HIDE_SECTION_MAP`) it also passes, so it is not tautologically green on its own author's work.

**This file is beyond T02's declared *Files touched* list**, which names only the migration and the two
`APConstants`. It is added under the *Fails when* clause, which asks for a check that cannot be satisfied by any
of those three files. Flagged here rather than slipped in.

#### Database verification — done on a real schema, then undone

`marlo-dev.properties` points at a live local MySQL schema (`aiccradb5`), so T02's *Tests* and *Verification*
were run for real rather than deferred.

T02's *Not evidence when* clause rules out a database that already had the rows. Pre-state was confirmed
**0 rows** for the key before applying anything.

```
global_unit_type_id  key                  format  default_value  category
1                    cognito_auth_active  1       false          2
3                    cognito_auth_active  1       false          2
4                    cognito_auth_active  1       false          2
```

The second half of the *Tests* clause — *"confirm the flag reads `false` for every Global Unit"* — was checked by
resolving `COALESCE(custom_parameters.value, parameters.default_value)` across every unit:

| Check | Result |
|---|---|
| `custom_parameters` rows for this key | **0** — correct; rollout is operational, not a deploy artifact |
| Global Units resolving to `true` | **0** |
| Global Units covered by the flag at all | **38** |
| Global Units of type 2 or 5 | **2** (`CIAT50`, `Alliance`, both type 2) |

**Then rolled back.** The rows were applied with the `mysql` client, not by Flyway, so `schema_version` gained
no entry — leaving them would have meant Flyway inserting three *more* rows on the next real startup, for six
duplicates. Post-rollback the schema is back to its exact pre-state (349 `parameters` rows). Verified.

#### OQ-11 — measured, and **not** closed

`design.md` §3 leaves open whether type-2/5 Global Units have CGIAR users, since the `(1,3,4)` template means
they can never carry this flag. Measured here:

| Unit | Type | Active users | of which `is_cgiar_user = 1` |
|---|---|---|---|
| `CIAT50` | 2 | 30 | **0** |
| `Alliance` | 2 | 1 | **0** |

**This does not answer OQ-11**, and the reason matters: across the entire schema, `users` splits
**3584 rows at `is_cgiar_user = 0` and zero rows at `1`**. This database has no CGIAR users *anywhere*, so it
returns "no CGIAR users" for every Global Unit of every type — the type-2 result is not a finding about type 2.
It is what a database with no CGIAR users must return.

The same measurement bears on **OQ-1** (*"How many users have `is_cgiar_user = 1`?"*) with the same caveat:
the answer **for `aiccradb5` is 0**, which also means **no user in this schema would take the Cognito path at
all**, so this database cannot exercise the flow end to end. OQ-1 and OQ-11 both need the production schema, or
one that has CGIAR users. Recorded rather than treated as closed.

#### Gates

| Gate | Result |
|---|---|
| Compile — `mvn -q install -DskipTests -pl marlo-web -am` | **EXIT=0** |
| Tests — `mvn -pl marlo-web test` | **44/44 PASS** (unchanged; T02 adds no tests, per its plan — its verification is the SQL) |
| Checkstyle — `marlo-web`, plugin 3.3.1 + MARLO config | **EXIT=0**, 0 violations |
| Checkstyle — `marlo-data` | **EXIT=0**, 0 violations — run separately because T02 is the first task in this spec to touch `marlo-data` |
| Two-file rule — `scripts/verify-specificity-constants.sh COGNITO_AUTH_ACTIVE` | **EXIT=0** |

All Maven commands carry `JAVA_HOME=<jdk-17>` per **EB-2**. Checkstyle uses the compatible-plugin workaround per
**EB-1**; the project's pinned gate is still broken.

#### Not done, deliberately

No `custom_parameters` row was seeded, and none should be: `design.md` §3 is explicit that enabling a unit is an
operational act. The flag ships off for all 38 covered units.

---


---

### CHG-COGNITO-AUTH-001-T03 — Dependencies + configuration that cannot break startup

**Result: PASS on everything verifiable without a running server** · 2026-08-31 · attempt 1 · **unreviewed**
**One *Done when* clause is deliberately still open** — the live application boot. See *The boot, and why it is not claimed* below.

**Files changed (4):**

| File | Change |
|---|---|
| `marlo-parent/pom.xml` | AWS SDK v2 **BOM import** + `nimbus-jose-jwt`, both in `dependencyManagement`, with version properties |
| `marlo-utils/.../utils/APConfig.java` | 7 `@Value` fields, **all** in the `${key:}` form, + 7 getters + one normalizer |
| `marlo-web/resources/config/marlo-test.properties` | the 7 keys, blank, with a comment saying they are optional and env-supplied |
| `marlo-web/src/test/java/.../utils/APConfigCognitoDefaultsTest.java` | **new** — 3 tests |

#### Dependency choices, and why

Both artifacts went into `dependencyManagement` **only**. No module declares either, so nothing enters the WAR
yet — the task is version declaration, and a later task adds the actual dependency where it is used.

- **AWS SDK v2 `2.31.30`, imported as the BOM** rather than pinning `cognitoidentityprovider` alone, so every
  AWS artifact a later task needs resolves to one consistent version set. Importing a BOM manages versions and
  adds no dependency.
- **`nimbus-jose-jwt 9.48`, deliberately not 10.x.** 10.x requires Java 11+; the 9.x line is Java 8+. MARLO
  builds on 17, so 10.x would work *today* — but hard rule 10 still recognises legacy Java 8 branches, and a
  version property in the parent POM is exactly the thing such a branch inherits. 9.48 costs nothing and removes
  that failure mode. This is the library on the hot path of every federated login (local JWKS signature
  validation), not the SDK.
- **Both resolve from the network**, verified with `mvn dependency:get` before they were written into the POM.
  A version property that cannot resolve is not caught by any build in this repo, because nothing depends on it.
- **No downgrades** (hard rule 11 / NF-006): both are new additions.

#### Verification — a standing test, not a one-off experiment

T03's *Fails when* says to remove a `:default`, watch the context fail, then restore. Done that way it proves the
point once and leaves nothing behind. It is a **regression test** here instead:

| Test | What it pins |
|---|---|
| `everyCognitoGetterReturnsEmptyWhenTheKeyIsAbsent` | The task's stated test — all 7 getters return `""`, not `null`, no exception |
| `aPlaceholderWithoutADefaultBreaksTheContext` | A probe bean carrying `${cognito.client.id}` **without** a default is registered into the same context; the refresh must fail with `BeanCreationException` naming that placeholder. If this ever stops throwing, the empty defaults have stopped being load-bearing and design.md §9.3's "phase 0 is inert" is backed by nothing |
| `theSamePlaceholderWithAnEmptyDefaultResolvesCleanly` | The control. Identical probe, differing only by the `:`. Without it, a green failure-test would be consistent with the context breaking for some unrelated reason in this hand-built setup |

**The property source is built by stripping every `cognito.*` key out of the real tracked
`marlo-test.properties`** — T03's *Not evidence when* clause made executable (*"the whole point is the un-updated
case"*). A curated map of just the keys the context needs would prove nothing, because it would silently supply
whatever `APConfig` happens to require.

No test dependency was added: `spring-context`, `spring-beans` and `spring-core` are already on `marlo-web`'s
test classpath. `marlo-parent/pom.xml` gained no test-scoped anything, so `DEC-005` stays untaken.

#### A pre-existing defect this test exposed

Building the context revealed that **`APConfig` declares two placeholders with no default that the tracked
template does not supply**: `email.pmu` and `clarisa.wos.link2`. An environment bootstrapped from
`marlo-test.properties` per hard rule 12 therefore **fails Spring context startup on them**, before reaching
anything Cognito.

That is the exact failure mode T03 exists to prevent, in a file T03 already modifies — and it was still left
unfixed, because it is not this task's scope and audit finding 1 was precisely about unrelated repairs riding
along in a task diff. Instead the two keys are pinned in the test as `KNOWN_TEMPLATE_GAPS`, supplied as dummies
so the test can measure its own question, with an assertion that **fails if the template ever starts providing
them** (so the list can only shrink). The useful side effect: adding a new `@Value` to `APConfig` without a
matching template key now fails a test instead of failing a deployment. Recorded as **PS-5**.

#### The boot, and why it is not claimed

*Done when* also requires: *"the app boots with no Cognito keys present."* **That has not been done, and is not
claimed.** `marlo-dev.properties` on this machine carries none of the 7 keys, so the environment is already in
exactly the right state to prove it — but the boot itself runs `scripts/run-marlo-java17.sh`, which kills any
`cargo:run`, **deletes `marlo-{utils,data,web}/target`, and rewrites `marlo-dev.properties`**. That is a
destructive operation on a working tree with uncommitted work in it, and it was left for the user to authorise.

What the Spring context test proves and a boot would add:

- **Proven:** `APConfig` — the class that would break — constructs against a property set with no Cognito keys,
  and the same context *does* fail when a default is removed.
- **Not proven:** that the full application context, with every other bean, servlet filter and Struts
  initialisation, starts. Nothing in this diff touches those, but "nothing touches them" is an argument, not a
  measurement.

#### Skill assignment not followed, deliberately

`tasks.md` assigns T03 the `aws-serverless` skill. Root `CLAUDE.md`'s *Skill Map* lists `aws-serverless` among
the packaged skills that **"do not apply and are deliberately absent"** from MARLO's stack table. Its subject is
Lambda, API Gateway and DynamoDB; this task adds two version properties to a Maven POM. The root guide is
constitutional and the spec was authored on another branch before that map was fixed, so the guide wins. The
contradiction itself is real and outlives this task — recorded as **PS-6**.

#### Gates

| Gate | Result |
|---|---|
| Compile — `mvn -q install -DskipTests -pl marlo-web -am` | **EXIT=0** |
| Tests — `mvn -pl marlo-web test` | **47/47 PASS** (44 + 3 new) |
| Checkstyle — `marlo-web`, `marlo-data`, `marlo-utils` | **EXIT=0** each, 0 violations |
| No literal config in Java — `grep -rn 'cognito' --include='*.java'` | Only placeholders, key names and test scaffolding. **No literal value** |
| All 7 placeholders carry `:` | Verified; a grep for a Cognito `@Value` *without* a default returns nothing |


---

### CHG-COGNITO-AUTH-001-T04 — `CognitoAssertion` + `CognitoAuthenticationToken`

**Result: PASS** · 2026-08-31 · attempt 1 · **unreviewed**

**Files changed (2 production, 1 test):** both new classes in `marlo-data/.../security/`, GPL headers present.
The test lives in **`marlo-web`**, as T04's own *Verification* command specifies — `marlo-data` has no test root.

#### Two design questions T04 left to the implementer

**1. `getPrincipal()` and `getCredentials()` both return the assertion.** Shiro's contract separates *who is
claiming* from *the proof offered*, but under DD-5 the proof was already verified before this object could
exist. There is no separate credential left to present, and inventing one would imply a verification step the
realm does not perform. `getPrincipal()` equally cannot return a specific claim: **which claim identifies the
account is OQ-9, owned by T07**. Returning the assertion keeps that open rather than hard-coding an answer in
a class written three tasks earlier.

**2. The assertion does not carry the identity claim's *name*.** Only its value. Whatever T07 decides applies
to every assertion identically, so the claim name is a constant of the deployment, not per-assertion data.
Adding a field for it now would be speculative structure.

Two smaller calls, both recorded because they are behavior a later task will rely on:

- **The raw ID token is not carried.** The assertion exists only after signature, issuer, audience, expiry,
  nonce and `token_use` have all passed, so the token has no further use; holding it would keep a live
  credential alive for no gain.
- **Required values are validated in the constructor.** A blank identity or email fails loudly at construction
  rather than reaching the realm and resolving to "no such user" — which would read as a login failure instead
  of the coding error it is. `usernameClaim` is deliberately **optional**: whether `sAMAccountName` is mapped at
  federation time is OQ-18 and is not MARLO's to decide, so an absent one normalizes to `null` rather than to a
  blank string a caller could mistake for a username.

#### Immutability is checked structurally, and the check was proven to bite

T04's *Not evidence when* clause rules out the obvious suite: *"the test only asserts getters return what the
constructor received. That is a tautology; assert that no mutator exists via reflection."*

So `assertStructurallyImmutable` never constructs an instance. It asserts the class is `final` (no subclass can
add mutable state or override a getter), every non-static field is `private final`, and no declared method is a
mutator — including the general rule that **a void instance method on a value object can only exist to change
state**.

*Fails when* required proof that this bites. Two probes, both run:

| Probe | Outcome |
|---|---|
| Add a setter, leave the field `final` | **Does not compile** — `cannot assign a value to final variable email`. The compiler catches this before any test does |
| **De-finalize the field AND add the setter** | **Compiles cleanly**, and `assertionIsStructurallyImmutable` **fails**: `CognitoAssertion.email must be final` |

The second probe is the one that matters: it is the only shape of this mistake that survives compilation, and
it is exactly what the test exists to catch. Restored afterwards; the file was diffed against its pre-probe
copy and is byte-identical, and the suite returned to 8/8.

`toStringDoesNotLeakTheIdentityClaim` pins one more thing: the identity claim is the join key to a MARLO
account and `toString()` output reaches logs, so it must not appear there. The email may — MARLO already logs
it on every successful login.

#### Gates

| Gate | Result |
|---|---|
| Compile — `mvn -q install -DskipTests -pl marlo-web -am` | **EXIT=0** |
| Tests — `mvn -pl marlo-web test` | **55/55 PASS** (47 + 8 new) |
| Tests — `-Dtest=CognitoAssertionTest`, T04's stated command | **8/8 PASS** |
| Checkstyle — `marlo-web`, `marlo-data`, `marlo-utils` | **EXIT=0** each, 0 violations |
| GPL headers on both new files | Present |


---

### CHG-COGNITO-AUTH-001-T05 — `CognitoTokenValidator`, the security core

**Result: PASS** · 2026-08-31 · attempt 1 · **awaiting independent audit**

**First task in this spec run under the intended AKILI binding.** Implemented by the `akili-implementer`
persona on **`sonnet` (T2)**, which frees `opus` (T3) to audit it — restoring `author != auditor` on **both**
axes at once, instead of the §4.1 compromise where an `opus` author forced the auditor down a tier. This is
the arrangement §4.1 said was the right call before the security core, and T05 is that core.

**Files:** `security/CognitoTokenValidator.java` (interface, with a `RejectionReason` enum and a `Result`
value object that is accepted or rejected, never both) and `security/impl/CognitoTokenValidatorImpl.java`,
both in `marlo-data` with GPL headers; `CognitoTokenValidatorTest.java` in `marlo-web`; and
**`marlo-data/pom.xml`**, which T05's *Files touched* does not list — see below.

#### The JWKS seam

`JwksSource` is the only I/O boundary in the class. Production wiring uses `RemoteJwksSource`
(`JWKSet.load(URL)` against `APConfig.getCognitoJwksUri()`); tests inject an in-memory set built from keys
they generate with nimbus. A second constructor takes `(expectedIssuer, expectedAudience, JwksSource)`
directly, so the whole class is exercisable with **no `APConfig` and no network** — which is what DD-5 asked
for and what makes T05's *Not evidence when* clause satisfiable rather than aspirational.

One detail worth keeping: when `cognito.jwks.uri` is unset, `RemoteJwksSource` throws `MalformedURLException`
*inside* `fetch()` rather than failing construction. That is what keeps an unconfigured environment inert at
**startup** — the guarantee T03 exists to provide — instead of moving the failure to application boot.

#### Two implementer decisions, escalated rather than silently made

**1. `token_use` is checked inside the trust gate, not downstream — kept, and renamed.**
The implementer folded `token_use == "id"` into the same method as signature and `kid` verification, and
flagged it as a deliberate coupling introduced to satisfy T05's *Fails when* clause (case 7 must redden when
the signature check is stubbed).

Reviewed as Leader and **kept**, because the reasoning is design-faithful rather than test-driven:
`design.md` §13.2 states outright that Cognito signs ID **and** access tokens with the identical JWKS, so a
valid signature by itself does not establish that a token is this pool's *ID* token. `token_use` is part of
what "trusted" means here, not a later concern.

**What was wrong was the name.** The method was called `verifySignature` while doing three things, which is
exactly what made a sound design read as a contortion. Renamed to **`verifyTrustedIdToken`**. The class
javadoc records why the coupling exists and warns that separating the checks later would silently stop the
mutation from proving case 7.

**2. OQ-9 was assumed, and the assumption was not visible in the code.**
The implementer used `claims.getSubject()` — the `sub` claim — as the assertion's identity claim, and
reported the assumption honestly. But **it reported it in its own summary, not in the source**, where the
line carried no marker at all. T07 is the task that owns OQ-9 and is the reader who most needs that signal.
A comment now names OQ-9 at the exact line, says `sub` is a defensible placeholder rather than a resolution,
and states that T07 changes that one argument and nothing else in the class.

#### `marlo-data/pom.xml` — outside T05's declared file list, and unavoidable

T03 declared `nimbus-jose-jwt` in `dependencyManagement` only, which is a version declaration and nothing
else. T05 cannot compile without a real `<dependency>`, and its *Files touched* list names only the two Java
files. Declared here rather than discovered later, the same way T02's verifier script was.

#### PS-8 — the gate the previous audit asked for, and its result

Audit finding §6.3 required `mvn dependency:tree` to be diffed before and after, because an unpinned
transitive can arrive silently and hard rule 11 has no automated gate.

**Result: `nimbus-jose-jwt:9.48` has zero transitive dependencies.** It is a leaf in `marlo-data`'s tree.
Pure addition, nothing managed, no version changed anywhere. PS-8 is discharged for this task.

**A pre-existing misalignment was found while checking, and is not T05's:** `marlo-data` carries
`jackson-annotations:2.9.5` against `jackson-core`/`jackson-databind` at `2.18.9`. The 2.9.5 copy arrives
transitively from **`io.swagger:swagger-models:1.5.21`**, not from nimbus, and `jackson-annotations` is not
managed in `marlo-parent` at all. Verified by reading the tree's parent line rather than inferring it — the
first reading of this attributed it to nimbus, which was wrong. Recorded as **PS-9**.

#### The mutation — re-run independently, not taken on report

T05's *Fails when* is the clause that separates a real validation suite from a decorative one, so it was
**re-run by the Leader after the rename**, rather than accepted from the implementer's summary. The trust
gate was stubbed to `return null` unconditionally:

```
Failed tests:   unsignedTokenIsRejected(...)
  tokenSignedWithAnUntrustedKeyIsRejected(...)
  accessTokenUseIsRejectedDespiteAValidSignature(...)

Tests run: 9, Failures: 3, Errors: 0, Skipped: 0
```

Exactly cases **1, 2 and 7** red; 3–6, 8 and 9 stayed green — the precise set T05 names. Restored from a
pre-mutation copy, `diff` reported byte-identical, and the full suite returned to 65/65.

#### Logging discipline

T05's constitutional check is *no token value in any log statement*. Every one of the class's ten log calls
was read: each renders a `RejectionReason` enum constant or an exception's class name. **No raw token, no
signature, no claim value, no email** reaches a log.

#### Gates — all re-run by the Leader, not taken on report

| Gate | Result |
|---|---|
| Compile | **EXIT=0** |
| Tests, whole module | **65/65 PASS** (56 baseline + 9 new; no regression) |
| Tests, T05's own command `-Dtest=CognitoTokenValidatorTest` | **9/9 PASS** |
| Checkstyle — `marlo-data`, `marlo-web` | **EXIT=0** each, 0 violations |
| Mutation | cases 1/2/7 red, restored byte-identical |
| PS-8 dependency tree | pure addition, no version change |


---

### CHG-COGNITO-AUTH-001-T06 — Realm token-type dispatch

**Result: PASS — after two FAIL verdicts and three audit rounds** · 2026-08-31

> **Evidence below covers the code as it now stands**, re-run after the second repair. An earlier version of
> this entry claimed "3 tests" and "74/74" — counts taken before `supports()` and two later tests existed.
> That stale record was itself an audit finding (§10 F2), and it is the reason `[x]` was withheld twice.
Implemented by the `akili-implementer` on `sonnet`; audited on `opus`; **verdict FAIL** (§8). Repaired by the
Leader, which required **reopening T04** and **amending `design.md` §2.1**.

**Files:**

| File | Change |
|---|---|
| `security/APCustomRealm.java` | the `instanceof` guard above the cast, **plus a `supports()` override** — without which the guard is never reached |
| `MarloShiroConfiguration.java` | the validator declared as a singleton bean (not injected into the realm) |
| `security/CognitoAuthenticationToken.java` | **reopened T04** — carries and requires the resolved `users.id` |
| `APCustomRealmDispatchTest.java` | **new, 5 tests** — 3 dispatch, 1 end-to-end reachability through the real `Subject.login`, 1 proving an unknown token type is still refused |
| `CognitoAssertionTest.java` | updated for the new token contract |
| `design.md` §1, §2, §2.1, DD-1, DD-5 | two amendment blocks plus four wording corrections |

#### What attempt 1 got right

The guard sits **above** the unconditional cast, is I/O-free, and leaves the entire local path below it
untouched. `Authenticator.java`, `DBAuthenticator.java` and `LDAPAuthenticator.java` are absent from the
diff (DD-1). The audit cleared all of that, plus startup safety on an unconfigured environment and the
singleton scope of the validator bean.

**The implementer also resolved a spec ambiguity correctly and flagged it.** T06 says *"wire the validator
into the hand-constructed realm at `MarloShiroConfiguration.java:44-49`"*. It read that as a **location**, not
an injection target, and declared the bean beside the realm instead of injecting it. That is right: DD-5
guarantees the realm never holds a raw token, so a validator field on `APCustomRealm` would be provably dead,
and injecting an unused collaborator into a security-critical class is worse than not injecting it. The audit
agreed, and added a point the Leader had missed — **neither T08 nor T09 lists `MarloShiroConfiguration` in its
files, so T06 is the only authorized opportunity to create this bean.** `design.md` §2's footprint line
("inject the validator where the realm is hand-constructed") is now drift; recorded as **PS-11**.

#### The FAIL — the principal type

`new SimpleAuthenticationInfo(assertion, assertion, this.getName())` made the **assertion** the Shiro
principal. The local path produces a `Long` (`user.getId()`). MARLO has roughly twenty **unguarded**
`(Long) getPrincipal()` sites:

| Site | Effect |
|---|---|
| `AddUserIdFilter:52` | runs on every `*.do` / `*.json` / `/api/*` request → `ClassCastException` → HTTP 500 |
| `APCustomRealm.doGetAuthorizationInfo:187` | `ClassCastException` on every permission check |
| `AddSessionToRestRequestFilter:178` | same, on `/api/**` |
| `AbstractMarloDAO:451` | inside `catch (Exception)` — **fails silently**, nulling `created_by` / `modified_by` |
| ~14 REST v2 controllers | `ClassCastException` |

The Leader found the `doGetAuthorizationInfo` cast; **the audit found the rest and traced the actual sequence**:
`finishLogin` returns `SUCCESS` → `struts-home.xml:33-35` redirects to the dashboard → that is a *new HTTP
request* → `AddUserIdFilter` runs first and throws. The dashboard is the only destination for Global Unit
types 1/3/4/5, so the path **cannot reach a page at all**. Not "in principle" — the next request.

**No task in the plan fixed it.** T07 resolves the claim to a `users` row in the *callback* (§13.3 step ③),
before login, and never touches the realm; T08 and T09 are actions. Verified against all three task entries.

**This was a spec gap, not only an implementation slip.** `design.md` §2.1, DD-1 and DD-5 all describe what the
realm *consumes* and never state what it must *produce*; T06 said only "returning `SimpleAuthenticationInfo`
built from the assertion". The implementer followed both literally and correctly.

#### The repair, and why it reopened a closed task

Three coordinated changes:

1. **`design.md` §2.1 gains an explicit invariant** — *the principal MUST be the `users.id` (`Long`) on every
   path*. Recorded with the consumer table and with the rejected alternative: widening the ~20 consumer sites
   is exactly the destabilization of working paths DD-1 exists to prevent, for the benefit of one caller.
2. **`CognitoAuthenticationToken` carries the resolved `users.id` and requires it in its constructor** — which
   reopened **T04**, marked `[x]`. Requiring it is not decoration: §13.3 resolves identity and applies the four
   gates at step ③, **before** `Subject.login(...)` at step ⑥, so a token that cannot be built without an id
   makes that ordering structural instead of conventional. This type cannot exist for a person who has not
   already passed the gates. `getPrincipal()` now returns the id; `getCredentials()` stays the assertion.
   *(This also retires the "both accessors return the assertion" design from T04, which the T03/T04 audit did
   not flag and which this finding shows was wrong.)*
3. **The realm** passes `cognitoToken.getUserId()`. Still zero I/O, DD-5 intact.

**This is not OQ-9.** OQ-9 asks which Cognito *claim* joins to the `users` row. This asks what Shiro carries
afterwards, and the answer is fixed by twenty existing consumers, not by CGIAR IT.

The tests now assert the **type**, not just the value (`principal instanceof Long`), with the reason inline —
without that, the regression returns silently.

#### Three smaller audit findings, all closed

- **A 149-character line** in the new test (hard rule 7). Fixed. It also exposed **EB-3** — see §2.
- **A vacuous assertion**: `assertEquals(user.getPassword(), info.getCredentials())` in test 2 was
  `null == null`, because `cgiarUser()` never set a password. It would have held whatever the realm did.
  The fixture now sets one.
- **A stale javadoc cross-reference** to an `execution.md` T06 section that did not yet exist — the same defect
  class §6.2 already closed once. This section is that reference.

#### What the audit cleared, against the Leader's own suspicions

- **Test 2 is sound.** The Leader suspected that stubbing `getCgiarNickname` hollowed out its claim. It does
  not: the test passes a `RecordingAuthenticator` as the LDAP authenticator and asserts both that it was
  called **and** that it received the right username, while the DB double throws on any call. The LDAP branch
  is positively observed, not inferred from an absence of exceptions.
- **The ordering mutation is redundant, not missing.** With the guard below the cast, test 3 throws
  `ClassCastException` on its own — T06's *Fails when* is structurally satisfied by the test suite.
- **The pre-change capture is adequate.** Running the two local tests green against the unmodified realm does
  discharge the *Not evidence when* clause: the assertions encode observed pre-change outputs, not values
  derivable from the fixture.

#### Coverage gap, recorded not closed

D-2's restated gate is *"the `UsernamePasswordToken` path produces identical behavior"*. Three tests cover the
`getUserByEmail` branch on two user shapes. **Uncovered:** the `getUserByUsername` branch, `user == null`,
`!isActive` → `USER_DISABLED`, the `getCgiarNickname == false` arm, the `IncorrectCredentialsException` path,
and the `session.setAttribute(LOGIN_MESSAGE, …)` writes — a real output of the "identical behavior" contract
that no test observes. The audit rated this a coverage gap rather than a failure, since the guard is a pure
prepend on a type nothing else can produce. Recorded as **PS-12**.

#### Gates (Leader-run, after the repair)

| Gate | Result |
|---|---|
| Compile | **EXIT=0** |
| Tests | **76/76 PASS** (module) · **5/5** for `-Dtest=APCustomRealmDispatchTest` |
| Line length, measured directly per **EB-3** | **0** lines over 120 in anything this spec added |
| DD-1 | the three authenticator files absent from the diff |


---

### CHG-COGNITO-AUTH-001-T08 — `CognitoLoginAction`, the authorize redirect

**Result: FAIL on attempt 1 (six findings, four blocking), repaired — awaiting re-audit** · 2026-08-31
Implemented by the `akili-implementer` on `sonnet`; audited on `opus` → **FAIL** (§11). Repaired by the Leader,
which required amending **`design.md` §5.4, §8, §13.1, §1 and §2** and **T09's scope**.

**Files:** `action/home/CognitoLoginAction.java` (new), `struts-home.xml`, `global.properties`, both
`APConstants.java`, `CognitoLoginActionTest.java` (new, 8 tests), `CognitoUnloggedStackReachabilityTest.java`
(new).

#### What attempt 1 got right, and it is the most important thing in this entry

**The implementer refused to follow T08's Scope where following it would have shipped a 404.** T08 says
*"Register with `unloggedStack`"*. It read that stack's members and found that `ValidCrpActionInterceptor:50-62`
splits the action name on `"/"` and returns `NOT_FOUND` whenever there is no second segment — before
`crpManager` is even consulted. `cognitoLogin` is a flat name, like `login.do`. Following the spec literally
would have made the action **permanently unreachable**.

It defined `cognitoUnloggedStack` instead and wrote a test that drives the real interceptor to prove the claim.
The Leader verified it independently; the audit re-derived it a third time without relying on the test.

**This is the same defect class that cost T06 two FAIL verdicts — caught before the audit this time, because
the implementer's brief named that failure mode explicitly.** Telling the next implementer what the last one
got wrong is cheaper than another audit round.

#### The audit's four blocking findings

| # | Finding | Repair |
|---|---|---|
| **1** | **DD-4's explicitly rejected alternative was implemented.** State was stored under `"cognito.pending." + state` — the keyed map DD-4 rejected *"as unbounded session growth"*. Nothing removes an entry but a callback bearing that exact state, so an anonymous caller could loop the endpoint on one cookie and grow a heap-resident session for the full 30-minute timeout | **Fixed keys** in both `APConstants`. One caller, one pending authorization; last-writer-wins across tabs, exactly as DD-4 accepted |
| **2** | **Anyone could write — and revoke — any CGIAR user's `agree_terms`.** `agree` was never checked for `TRUE`, and `email` is unverified, so `GET /cognitoLogin.do?email=victim@cgiar.org&agree=false` rewrote a third party's compliance record. Separately, nothing required acceptance, so a user could complete sign-in having declined — DD-2 puts this path's control outside the form, so HTML5 `required` cannot fire | **Check stays, write moves to T09.** §5.4 amended with the split and the reasoning |
| **3** | **The chosen persistence path is the one this repo documents as not persisting.** `UserMySQLDAO.saveLastLogin` carries `@Transactional` with a comment saying that without it *"`agree_terms` [is] never persisted"*; `saveUser` has none, and `ValidateUserAction` writes this same column through `saveLastLogin`. The evidence offered was `assertTrue(saveUserCalled)` against a double — a presence assertion, structurally incapable of showing a row changed | **Dissolved** by finding 2's repair: T08 no longer writes at all. Recorded in §5.4 as a binding constraint on T09 |
| **4** | **Open redirect after authentication.** `returnUrl` came from the `Referer` of a **GET navigation**, so the linking page chooses it — and can set `Referrer-Policy: unsafe-url` to defeat truncation. It reached `finishLogin`, whose only test is `.contains(".do")`, which `https://evil.do/` satisfies on its origin alone. The victim authenticates for real at the CGIAR IdP and is redirected off-site | Same-origin guard at mint time, with a test covering four hostile shapes including the `marlo.example.org.evil.com` look-alike |

**Finding 4's repair taught the same lesson the audit had just taught about T06.** The guard was first placed in
`execute()` — and the new test failed, because `authorize(String)` is the seam every other caller and every
other test uses. **A guard on one entry point is bypassable by all the others.** It now lives with the value's
use, not with one door.

#### The two remaining findings

- **5 — the record, not the code.** `design.md` §8, its §2 footprint table, and **T09's own Scope** still
  mandated `unloggedStack`. The audit named the consequence precisely: an implementer reading the authoritative
  documents ships the callback unreachable — *"the same defect, one task later, in the half that receives the
  authorization code."* All corrected, with the reasoning recorded in §8 rather than left in an XML comment.
- **6 — both new i18n keys cannot render.** `refuse()` calls `addFieldError("loginMessage", …)`, but
  `loginForm.ftl` has no `[@s.fielderror]`, `generalMessages.ftl` iterates `actionErrors` only, and
  `struts.ui.theme=simple` emits nothing. A refusal returns HTTP 200, the login page, and **no explanation**.
  T08's constitutional check *"i18n keys for every message"* is satisfied literally, which is the problem.
  Recorded as **PS-15** against T13, which owns the rendering slot.

#### What the audit cleared

The randomness is `SecureRandom`, 32 bytes each, base64url no-pad → 43 chars, a strict subset of RFC 7636
§4.1's charset and inside its 43–128 length; `code_challenge` is `BASE64URL(SHA-256(verifier))` with `S256`,
matching §4.2 exactly. The enumeration sweep came back clean: same i18n key, same `INPUT` result, same
location, same status, and `user == null` folded into the not-a-CGIAR-account branch — no observable
divergence. Fail-closed on an unconfigured environment holds. Hard rules 3, 6, 7, 8, 10 and 12 all verified.

**One advisory worth carrying into T10/T11:** the three §9.2 resolution points will disagree if any is written
differently. T08 resolves `COALESCE(active custom value, parameters.default_value)`; MARLO's runtime convention
elsewhere (`BaseAction.hasSpecificities`) ignores `default_value` entirely. Inert today because the migration
seeds `'false'`, but an operator flipping a catalog default to `'true'` as a global enable would make T08 say
enabled while T10/T11 say disabled. **PS-16:** extract one shared resolver before T10 writes a fourth reading.

#### Gates (Leader-run, after the repair)

| Gate | Result |
|---|---|
| Tests | **85/85 PASS**, on a `clean` run — see **EB-4** for why that qualifier is load-bearing |
| Line length, measured directly (EB-3) | 0 over 120 in anything T08 touched |
| Specificity two-file rule | `verify-specificity-constants.sh COGNITO_AUTH_ACTIVE` → EXIT=0 |
| Hard rule 3 | no new `*.json` path in `struts-home.xml` |


---

### CHG-COGNITO-AUTH-001-T10 — `crpByEmail.do`: per-unit flag + two structural fixes

**Result: FAIL on attempt 1 (four issues), repaired — awaiting a second re-audit** · 2026-08-31
Implemented by the `akili-implementer` on `sonnet`; audited on `opus` → **FAIL** (§13); repaired; re-audited →
**PASS-WITH-FINDINGS** (§14), which found this entry did not exist. It exists now.

**Files:** `action/json/global/CrpByUserEmailAction.java` (the declared deliverable),
`security/CognitoAuthSpecificity.java` (**new** — PS-16's shared resolver),
`data/dao/mysql/ParameterMySQLDAO.java` (**not in T10's declared file set** — see below),
`action/home/CognitoLoginAction.java` (T08's file — now delegates to the resolver),
`CrpByUserEmailActionTest.java` (**new**, 5 tests).

#### PS-16 discharged: one resolver, and it had to exist

T08 resolved this flag with `COALESCE(active custom value, parameters.default_value)`;
`BaseAction.hasSpecificities` ignores `default_value` entirely. Two readings already, and §9.2 resolves the
flag at **three** points — one of which is T11's guard against CGIAR credential relay. `CognitoAuthSpecificity`
is now the only reading in production code, verified by grep.

#### The catalog read could never execute — and would have locked every user out

The audit rated this PLAUSIBLE because it could not run code. **Confirmed here.**
`ParameterMySQLDAO.getParameterByKey` built HQL filtering on `global_unit_type_id`; `Parameters.hbm.xml:27`
maps that association as the property **`globalUnitType`** — the column is not a property — and
`AbstractMarloDAO.findAll(String)` calls `createQuery`, which is HQL. **Parse-time failure on every call.**

The chain that made it fatal: T02 seeds no `custom_parameters`, so **every** Global Unit reaches the catalog
branch → throws → `CrpByUserEmailAction`'s per-entry `catch` swallows it → `crps[]` comes back empty →
`login.js:418` renders `emailNotFound`. **Every user, local included, locked out at wizard step 1**, with only
*"unable to add flagship to crps list"* in a log line that names the wrong class.

It survived because its one pre-existing caller sits in a rarely-taken branch. **T10 is what made it
load-bearing**, which is why repairing it belongs to T10 even though the file is outside its declared set.

**The repair is not minimal, and that is recorded rather than glossed:** a method that always threw now
returns. `CrpAdminManagmentAction:1018-1032`'s `parameters.size() == 0` branch therefore changes from
"always 500" to "performs a `custom_parameters` INSERT" — an untested save path in a section this task never
declared. **PS-19.**

**Evidence, and its limit.** The re-audit accepted structural verification without a live boot, because every
construct has an executing in-repo precedent at the same Hibernate version: `p.key` with an alias
(`CustomParameterMySQLDAO:87`, live on every request), `p.globalUnitType.id` (`cp.crp.id` there),
`createQuery(String, Class)` returning `org.hibernate.query.Query` (`UserMySQLDAO:62`, same module). The SQL
equivalent was also run against the live schema and resolved a real row. **A live boot is still warranted for
PS-19's insert path** — the thing nobody has ever executed.

#### Three mutations, all now red; two of them used to survive

The suite could not discriminate the resolver's two central semantics. Every fixture in T10 *and* T08 used a
catalog default of `"false"` and, where an override existed, an active override of `"true"`.

| Mutation | Before | After |
|---|---|---|
| `if (override != null) return true` — ignore the override's **value** | green | **red** |
| read the catalog before the override — invert COALESCE precedence | green | **red** |
| pass `globalUnit.getId()` instead of `getGlobalUnitType().getId()` | green | **red** |

```
Failed tests: anActiveOverrideOfFalseBeatsACatalogDefaultOfTrue: expected:<false> but was:<true>   (1 and 2)
Failed tests: anActiveOverrideOfFalseBeatsACatalogDefaultOfTrue: expected:<true> but was:<false>   (3)
```

The first mutation is not academic: **it is MIG-001's rollback.** "The flag is the rollback" means setting
`custom_parameters.value` to `'false'`; a resolver that ignores the value leaves Cognito **enabled** at all
three §9.2 points. Closed by `anActiveOverrideOfFalseBeatsACatalogDefaultOfTrue`, which inverts both fixtures:
catalog `"true"`, active override `"false"`.

The third was closed by fixing the *stub*, not the test body — it returned the same catalog row for any id, so
the argument was never checked. Unit ids and type ids are now disjoint ranges, because overlapping ranges let
the wrong one resolve anyway. The hazard is real: `ParameterDAO` names the argument `globalUnitTypeId` while
`ParameterManager` names the same argument `globalUnitId`.

#### The zero-units disclosure — adjudicated, not stretched

T10's Scope and test 2 require a zero-unit account to get a well-formed response; `design.md` §4 required the
opposite for that same input. Both could not hold. §4 is amended with the before/after wire payloads, and
**R-D3's acceptance is widened explicitly**: its rationale ("existence and membership are already disclosed")
was true for accounts *with* Global Units and false for accounts with none. Accepted because `login.js:418`
collapses both cases to one UI state, so the disclosure is wire-only.

#### Gates

| Gate | Result |
|---|---|
| Tests | **90/90 PASS** on a clean run (EB-4) |
| Line length, measured directly (EB-3) | 0 over 120 in any touched file |
| `ParameterMySQLDAO` diff | **24/3** after restoring from HEAD and re-applying only the method + import. The first attempt converted the whole file's line endings and showed **116/95** — PS-13's defect, repeated |
| Specificity two-file rule | `verify-specificity-constants.sh COGNITO_AUTH_ACTIVE` → EXIT=0 |


---

### CHG-COGNITO-AUTH-001-T11 — Harden `validateUser.do` against CGIAR credential relay

**Result: implemented, Leader-corrected — awaiting audit** · 2026-08-31
Implemented by the `akili-implementer` on `sonnet`. **95/95 tests on a clean run** (EB-4), 0 lines over 120.

**Files:** `action/json/global/ValidateUserAction.java` (the declared deliverable),
`ValidateUserActionGuardTest.java` (**new**, 5 tests).

**Why this task exists:** the endpoint is unauthenticated, accepts a password, and relays it to Active
Directory through `userManager.login()` → realm → LDAP. For a migrated Global Unit that relay must stop.
Judgment Day found it (V-2) in a path the design originally did not mention.

#### The implementer flagged an ambiguity; it was a requirement violation

T11's Scope says *"whose **selected** Global Unit has the flag enabled"* — and this endpoint is never told
which unit was selected. Verified: `login.js`'s `checkPassword()` posts `userEmail`, `userPassword` and
`agree`, nothing else. The implementer resolved it by refusing when **any** of the account's units is
migrated, argued as fail-closed, and declared the reading rather than burying it.

**That reading is forbidden by MIG-001.** Its *Both paths coexist* scenario states that for a CGIAR user in a
migrated unit *X* and a non-migrated unit *Y*, **"the path used MUST be determined by the Global Unit selected
in wizard step 2"**. "Any membership" locks that user out of *Y*'s local login — a regression during exactly
the staged rollout MIG-001 exists to describe.

**Corrected:** an optional `globalUnitId` parameter. When present, **only that unit decides**. When absent,
the guard still checks every membership, and **that fallback must stay fail-closed** — otherwise an attacker
reaching the endpoint directly, bypassing the wizard, rounds the guard by omitting the parameter. The javadoc
says so explicitly so a later "simplification" cannot quietly invert it. **PS-20** records that T12 must send
the value the wizard already has.

#### The test that decides the task, and the one that was missing

T11's *Not evidence when* is the sharpest clause in the spec: *"a response can be shaped correctly while the
LDAP bind still happened — the password would already have left MARLO. Assert the call never occurred."*

The implementer got this right: test 1's double **throws** if `login()` is invoked, so a fall-through fails
loudly instead of passing on a well-shaped JSON body.

What was missing was MIG-001's own scenario. Added, and **proven to bite**: reverting to "any membership"
reddens it with *"selecting the non-migrated unit must still authenticate via LDAP"*.

> **A method note worth keeping.** The first attempt at that mutation silently did not apply — a `perl`
> pattern that did not match — and the suite stayed green. Read carelessly, that is "the mutation ran and
> nothing broke", which would have recorded a test as load-bearing without ever testing it. It was caught by
> counting occurrences before believing the result. **A mutation that reports green must be proven to have
> been applied before it is proven to have failed to matter.**

#### PS-17, accounted for rather than waved past

The guard resolves the user *before* deciding, so `UserMySQLDAO.getUser(String)`'s pre-existing
string-concatenated native SQL is now reached slightly earlier and on **every** request to this endpoint, not
only migrated-CGIAR ones. It is the same method with the same argument the endpoint's own
`userManager.login()` already reached, so no second or differently-shaped path into the sink was opened — but
the change is not security-neutral, and the implementer said so unprompted. PS-17 stays open.

#### Gates

| Gate | Result |
|---|---|
| Tests | **95/95 PASS** on a clean run |
| Named test — `-Dtest=ValidateUserActionGuardTest` | **5/5** |
| Mutation (guard ignores the flag) | test 2 red, as T11 requires |
| Mutation (guard ignores the selected unit) | the MIG-001 test red |
| Line length, measured directly (EB-3) | 0 over 120 |
| PS-16 | all three §9.2 resolution points call `CognitoAuthSpecificity` — verified by grep, now with all three present |

## 4. Independent audit of T01 + T02 — 2026-08-31

**Reviewer:** `akili-reviewer` persona, model **`sonnet`**, tools `Read, Grep, Glob` only.
**Verdicts:** T01 **PASS-WITH-FINDINGS** · T02 **PASS-WITH-FINDINGS** · *"Would I let T03 start? Yes, conditionally."*
**Review rounds consumed: 1.**

### 4.1 Why the Reviewer ran `sonnet` and not `opus`

Root `CLAUDE.md`'s *Enforced bindings* table pins the Reviewer to `opus` — but that table assumes the Implementer
is `sonnet` (T2). Here the author was an `opus` session, so an `opus` reviewer would have satisfied the tier while
**breaking the rule the tier exists to serve**: `author != auditor` on the model axis. `sonnet` is the registry's
own T3 fallback, and it keeps both axes intact — different model, and no write tools.

**The trade is real and is recorded, not hidden:** a T2-tier auditor on a security-adjacent diff is thinner than a
T3 one. It was judged acceptable because the diff is small and largely mechanical. A deeper `opus` pass remains
available and is the right call before **T05**, the token-validation core, where depth matters more than it does
here. **This does not close §1.2 for future tasks** — it closes it for T01 and T02 only.

### 4.2 What the audit cleared

The Reviewer independently traced the highest-risk thing in the diff — the `Referer` read moving out of the callee
into the caller — and cleared it with reasoning the author had not written down: the old code read the header only
*after* membership validation, so it never ran on the two `INPUT` failure branches, while the new code reads it
unconditionally. It confirmed by `Grep` that `login(User, GlobalUnit)` has **exactly one caller** in `marlo-web`,
always inside a live Struts request, and that `getHeader` is a pure idempotent read — so the reordering is not
observable. It also verified the five protected `user`-field dereferences survived byte-identical at their new
offsets, that no forbidden file appears in the diff, and that no test passes by tautology.

That is what an independent audit is for, and it is the reason this one was worth running.

### 4.3 Findings, and what was done about each

| # | Finding | Disposition |
|---|---|---|
| **2** | **`verify-specificity-constants.sh` could report `OK` on a migration whose key column is misspelled**, when another column held the correct string | **REAL — fixed.** See §4.4 |
| **3** | T01 was marked `[x]` while its *Done when* clause *"called out in the commit body"* cannot be true — nothing is committed | **Accepted.** T01 is now `[~]`, not `[x]`, with the open clause named in `tasks.md` itself rather than only here |
| **1** | The two `.gitignore` hunks belong to kaizen **P8**, not to T01 or T02, and appear in the audited diff with no attribution in `tasks.md` or this log | **Partly accepted — see §4.5** |
| **4** | T02's literal *Verification* query returns 0 rows at rest, because the author rolled the migration back | **Accepted, no action.** The Reviewer agreed the rollback was correct (it prevents Flyway double-inserting to 6 rows) and rated this non-blocking. Already disclosed in §3 |
| **5** | The claimed pre-extraction RED run cannot be independently verified from a single squashed working state | **Accepted, unfixable after the fact.** This is §1.2's gap exactly. The mitigation for future tasks is to commit the RED state, not to argue about it |

### 4.4 Finding 2 — the audit was right about the defect and wrong about the exploit

The Reviewer's concrete scenario was a migration whose description read
`'Feature flag cognito_auth_active for Amazon Cognito'`. **Run as given, that scenario does not reproduce**: the
old pattern required the value as a *complete quoted literal*, and there the string sits unquoted inside a longer
description. But the weakness it named — no check of *column position* — was real, and a sharper variant
(description equal to exactly `'cognito_auth_active'` while the key column reads `'cognito_atuh_active'`) **did**
produce a false `OK`. Verified before fixing, and re-verified after.

The check now resolves the key column's index from each statement's own column list and reads the value at that
position, so it assumes nothing about the `AGENTS.md` column order.

**Fixing it exposed three further defects of the author's own, all found by testing rather than by reading:**

1. **`close` is an awk built-in.** Using it as a variable made every invocation a syntax error — and the failing
   run still *looked* like a pass, because a broken awk emits zero hits and zero hits reads as "not found". A
   green-looking FAIL is the worst possible failure mode for a lint.
2. **The 1,581-migration scan was unusable**, taking over two minutes. A `grep -rl` prefilter now narrows the
   field before the expensive column-aware pass.
3. **Comment blocks corrupted the parse.** This spec's own migration carries a `--` header containing both `;`
   and `1 (CRP), 3 (Platform)`; the `;` split a statement and the `(` was read as the column list, so the real
   migration reported **2 rows instead of 3**. Comments are now stripped before flattening, and the parse is
   anchored at the `INSERT` keyword. *Caught only because the control run on an unrelated specificity returned 3
   while this spec's returned 2 — a single-subject test would have shipped it.*

**The repaired script then found a genuine pre-existing repository defect it was not aimed at:**
`CRP_BI_MODULE_ACTIVE` is declared in `marlo-web`'s `APConstants` but **not** in `marlo-data`'s, violating root
`CLAUDE.md` Hard rule 4, while `crp_bi_module_active` has **3 live rows** in the database. Out of scope for T02
and left untouched; recorded as **PS-3** below.

It also needed a second real behavior: `ai_section_active` reaches `parameters` through an `UPDATE ... SET` in a
rename migration, not an `INSERT`. Reporting that valid key as a FAIL would have trained everyone to ignore the
tool, so rename provenance is now recognised and reported as such.

Final state, all re-verified: the exploit fails (`EXIT=1`), this spec's migration reports the correct 3 rows,
and two unrelated existing specificities pass — one by `INSERT`, one by rename.

### 4.5 Finding 1 — accepted in substance, corrected in fact

The Reviewer is right that a diff audited as "T01+T02" must not silently contain unrelated work, and right about
the consequence: a later `git bisect` would attribute those lines to the wrong task.

**One correction to the finding as written.** It states the change has "zero paper trail". It has one — kaizen
item **P8** in `docs/specs/kaizen/changes--migrate-ad-authentication-to-cognito--directory-abstraction.md`
records it as *applied 2026-08-31 on `staging-cognito-impl`*, with its severity downgraded and its original
premise corrected. The Reviewer never saw that file **because the author's brief did not point at it** — a
briefing defect, not a record that was missing. The real gap is narrower and still real: the change is absent
from *this* log and from `tasks.md`.

**Disposition:** the `.gitignore` hunks are **not** part of T01 or T02 and must be committed **separately**, with
their own message referencing kaizen P8. Recorded here so the boundary survives into commit time, which is the
only moment it can still be got right.

---

## 5. Pending items — **RECLASSIFIED 2026-09-02**
> **This section was headed "Pending items for `staging`". That framing is superseded.** No promotion is
> authorised. These items are carried forward to the **dedicated migration environment** (§27.4), where this
> branch will be deployed and the federated path validated for the first time. Nothing below is a staging
> task, and nothing below authorises a merge, a rebase, a cherry-pick, or a deployment.

*(Supersedes the earlier §4 numbering; PS-1 and PS-2 are unchanged.)*

| # | Item | Why it is not applied here |
|---|---|---|
| PS-1 | Kaizen **P7** — correct the "3 JUnit 4 test files" claim in root `CLAUDE.md` and `AGENTS.md`. Two independent runs here confirm it is false (44 tests) | Both are shared files under the Default-Branch discipline |
| PS-2 | **EB-1** — re-pin `maven-checkstyle-plugin` to a version compatible with Checkstyle 8.x, so the project's Required gate can actually run | `marlo-parent/pom.xml`; outside T01/T02 scope. **T03 edits this POM anyway**, which is the natural moment |
| PS-4 | Kaizen **P8** (the two `.gitignore` hunks) must be **committed separately** from T01/T02, referencing P8 | Audit finding 1; see §4.5 |
| PS-3 | **`CRP_BI_MODULE_ACTIVE` is missing from `marlo-data`'s `APConstants`**, violating Hard rule 4, while its key has 3 live rows in the database. Found by the repaired verifier | Pre-existing and unrelated to this spec. Fixing it here would repeat exactly the scope error finding 1 identifies |
| PS-5 | **`marlo-test.properties` does not declare `email.pmu` or `clarisa.wos.link2`**, which `APConfig` requires with no default, so an environment bootstrapped from the template per hard rule 12 fails Spring startup on them. Pinned as `KNOWN_TEMPLATE_GAPS` in `APConfigCognitoDefaultsTest` | Pre-existing; outside T03's scope. Two lines to fix, but in a file T03 already touches — deliberately not ridden along (audit finding 1) |
| PS-6 | **`tasks.md` assigns T03 the `aws-serverless` skill, while root `CLAUDE.md`'s Skill Map lists it among skills that "do not apply" to MARLO** | `CLAUDE.md` is a shared file. The contradiction needs resolving once, outside this migration, not re-litigated per task |
| PS-7 | **T03's live boot verification** — start the app with no Cognito keys present and confirm it boots | Needs `scripts/run-marlo-java17.sh`, which deletes `target/` and rewrites `marlo-dev.properties`. Left for the user to authorise; see the T03 entry |
| PS-9 | **`marlo-data` carries `jackson-annotations:2.9.5` against `jackson-core`/`jackson-databind` at `2.18.9`.** The old copy arrives transitively from `io.swagger:swagger-models:1.5.21`, and `jackson-annotations` is not managed in `marlo-parent` at all | Pre-existing; found while discharging PS-8 and unrelated to any task here |
| PS-8 | **DISCHARGED for T05** — the tree was diffed and `nimbus-jose-jwt:9.48` proved to be a leaf: pure addition, no version changed anywhere. **Still binding on any future task that declares an AWS artifact**, since it is the BOM that manages a wide GA set, and T05 declared no AWS dependency at all | Audit finding 3 (§6.3) |
| PS-10 | **Move the JWKS fetch off `CognitoTokenValidatorImpl`'s instance monitor** (fetch outside the lock, publish inside it), so one slow call cannot serialize concurrent logins behind it | Audit F2. The call is now bounded at ~2 s and refreshes are rare, so the worst case is bounded; restructuring locking in security code this session cannot load-test is the riskier change |
| PS-11 | **`design.md` §2 footprint line is drift** — it says "inject the validator where the realm is hand-constructed", but DD-5 makes injection into the realm dead code. Should read "declare the validator bean alongside the realm's hand-construction" | Audit of T06; a one-line spec correction, not a code change |
| PS-12 | **D-2 coverage gap** — the `getUserByUsername` branch, `user == null`, `!isActive`, the `getCgiarNickname == false` arm, the `IncorrectCredentialsException` path and the `LOGIN_MESSAGE` session writes are unexercised by T06's three tests | Audit of T06 rated it a coverage gap, not a failure: the guard is a pure prepend on a type nothing else produces |
| PS-13 | **~14 lines of whitespace/line-ending churn in `MarloShiroConfiguration.java`** — byte-identical `-`/`+` pairs on three imports and the untouched `securityManager(...)` method | Marks a security-config method as modified when it is not, and poisons `git blame`. `--renormalize` was tried and made it worse (141/114); left rather than destabilise the file |
| PS-14 | **DD-5 should state its own consequence:** the realm trusts `token.getUserId()` with no verification, so `CognitoAuthenticationToken` is a "log in as this `users.id`" capability mintable anywhere on the `marlo-data` classpath | Intended, and equally true before the repair, but now unmistakable. One line in DD-5, not a code change |
| PS-15 | **T08's two new i18n keys cannot render.** `refuse()` uses `addFieldError("loginMessage", …)`; `loginForm.ftl` has no `[@s.fielderror]`, `generalMessages.ftl` reads `actionErrors` only, and `struts.ui.theme=simple` emits nothing — a refusal is silent | **Re-targeted 2026-08-31 by the T08 re-audit: T13 is the wrong owner.** T13's *Files touched* is `global.properties` only, and both keys are already there — T13 would find nothing to do and close this without fixing it. The real fix is an FTL change in **T12's** file set (the `.invalidField` family in `loginForm.ftl`, which §5.5 already flags for `role="alert"`) |
| PS-16 | **Extract one shared `cognito_auth_active` resolver before T10.** T08 resolves `COALESCE(active custom value, parameters.default_value)`; `BaseAction.hasSpecificities` ignores `default_value` entirely. Inert today, but an operator flipping a catalog default to `'true'` would make the three §9.2 points disagree | Audit of T08, advisory. T10 and T11 must call it rather than write a fourth reading |
| PS-17 | **`UserMySQLDAO.getUser(String)` and `getEmailByUsername` build native SQL by string concatenation** on a value that reaches them straight from an unauthenticated request. Parameterise both | Pre-existing and already reachable through `APCustomRealm:159` via `validateUser.do` / `login.do`, so **not T10's to fix** — but T10's reordering moved the sink to the first statement of an unauthenticated endpoint that echoes the matched row, so it must not be booked as security-neutral. Audit of T10, finding 4. T11 touches the sibling endpoint and inherits the note |
| PS-18 | **`CustomParameterMySQLDAO.findAll()` has the same defect T10 repaired in `ParameterMySQLDAO`** — HQL filtering on `is_active`, a column, where the mapped property is `active` | Found while verifying that finding. Not on any Cognito path, so out of scope here; it will throw for whoever calls it first |
| PS-19 | **`CrpAdminManagmentAction:1018-1032`'s `parameters.size() == 0` branch changed from "always throws" to "performs a `custom_parameters` INSERT"** when T10 repaired `getParameterByKey`. It is an untested save path — no `validate()`, no `Validator`, no test — and if no catalog row exists for `crp_has_regions` at that Global Unit type it now NPEs or violates NOT NULL *after* two other saves have run | A consequence of repairing a method that had always thrown, not of T10's own scope. Needs a live exercise of CRP Admin Management save, which this branch cannot do. Audit of T10, finding 2 |
| PS-20 | **T12 must post `globalUnitId` to `validateUser.do`.** The wizard knows the selected unit by step 3 and does not send it; without it T11's guard falls back to "any membership is migrated", which locks a mixed-membership CGIAR user out of their non-migrated unit — the case MIG-001's *Both paths coexist* scenario forbids | **And the server must keep validating it against the account's memberships** — an earlier revision narrowed on the raw parameter and `&globalUnitId=99999` switched the guard off entirely. If T12 ships the parameter while that check is absent, every client bypasses the guard, not just a crafted one. The parameter and the validated per-unit branch already exist in `ValidateUserAction`; only the one-line `login.js` change is missing, and `login.js` belongs to T12's file set. T12 is blocked by OQ-3 |
| PS-21 | **`login.do` still relays a migrated CGIAR password to Active Directory.** `LoginAction.login():186-187` calls `userManager.login(userEmail, password)` through the same realm and the same LDAP bind T11 just closed on `validateUser.do`, and it is unauthenticated. T12's protection for it is **client-side only** (`.remove()` of the password input), so the endpoint stays postable | **SEC-005 says "any MARLO endpoint", and after T11 it is still false.** design §5.3 itself names the local flow as *two* requests and hardened only one. Needs a design amendment plus a named task **before T12 ships a UI that assumes the server is protected**. Out of T11's declared file set. Audit of T11, finding 5 |
| PS-22 | **`ValidateUserAction`'s refusal path logs nothing.** OPS-001 requires every authentication attempt to log outcome, path and Global Unit — a **blocked credential relay** is the single event operators most need during a staged rollout, and it is currently invisible | T14's scope names the new actions and `LoginAction` but not `ValidateUserAction`. Add it when T14 unblocks. Audit of T11, carry-forward |
| PS-23 | **A migrated CGIAR account with no *surviving* membership still relays to AD.** `GlobalUnitMySQLDAO.crpUsers` filters `cpUser.active=1 AND cp.active=1 AND cp.login=1`, so a deactivated `crp_users` row, or a unit with `login=0`, yields an empty list and the fail-closed sweep finds nothing to block on | Not attacker-controllable, and the same filter governs all three §9.2 resolution points, so this is not resolver drift — it is the boundary of a membership-derived flag model. Audit of T11, finding 3 |
| PS-24 | **`ValidateUserAction` resolves the account in a different order than `APCustomRealm` does** — email-then-username unconditionally, where the realm branches on `@`; and `crpUsers()` is passed the raw parameter rather than the resolved `user.getEmail()`. A malformed `users` row whose `email` equals another row's `username` would let the guard judge one row while the realm authenticates another | Requires an administrator-provisioned malformed row, so not attacker-reachable — but two lines make the "guard resolution ⊇ realm resolution" property an invariant instead of a coincidence. Audit of T11, finding 2 |

---

## 6. Independent audit of T03 + T04 — 2026-08-31

**Reviewer:** `akili-reviewer` persona, model **`sonnet`**, tools `Read, Grep, Glob` only. Same rationale as §4.1.
**Verdicts:** T03 **PASS-WITH-FINDINGS** · T04 **PASS-WITH-FINDINGS**
**On T05:** *"Not as-is"* — one finding had to be closed first. It has been.
**Review rounds consumed: 2** (cumulative).

The brief pointed this Reviewer at the kaizen entry file, which §4.5 identified as the *briefing* defect behind
the previous audit's one wrong claim. No repeat of that finding.

### 6.1 Finding 1 — `CognitoAssertion` was not `Serializable`. CONFIRMED, fixed.

**Shiro's `AuthenticationToken` extends `java.io.Serializable`.** Verified directly against the jar:

```
public interface org.apache.shiro.authc.AuthenticationToken extends java.io.Serializable
```

`CognitoAuthenticationToken` implements that interface — and the author had even given it a
`serialVersionUID`, which is evidence the requirement was *noticed for the wrapper and not propagated to the
one field it holds*. Serializing the token therefore threw `NotSerializableException: CognitoAssertion`.

**Latent today, not tomorrow.** `MarloShiroConfiguration` wires an in-memory `MemorySessionDAO` and no
`CacheManager`, so nothing serializes the token — which is precisely why none of T04's tests caught it,
including the one aimed squarely at *"getPrincipal()/getCredentials() behave as Shiro expects"*. Shiro's
contract for a token type **includes** being serializable, so T04's own stated test goal was not fully met.
It stops being latent when a `CacheManager` is wired (Shiro caches `AuthenticationInfo`), a session store is
clustered, or Tomcat session persistence is enabled — and **T06 is the task that puts a `CognitoAssertion`
into Shiro's live principal path**.

**Fixed, and the defect was reproduced before it was.** A round-trip serialization test was written first and
run against the unfixed classes:

```
Tests in error:
  tokenSurvivesASerializationRoundTrip: org.cgiar.ccafs.marlo.security.CognitoAssertion
```

That bare class name is `NotSerializableException`'s message. `CognitoAssertion` then gained
`implements Serializable` and a `serialVersionUID`; every field type (`String`, `Instant`) already was. The
test is now a standing guard rather than a one-time repair, and the class javadoc records **why** the
interface is required, so a future reader does not remove it as decoration.

### 6.2 Finding 2 — a false claim in the author's own javadoc. CONFIRMED, corrected.

`APConfigCognitoDefaultsTest`'s class comment justified reading the template from disk by asserting that
*"`config/*.properties` is not copied into `target/classes` by this module's build, so there is nothing to
load from the classpath."*

**That is false**, and the Reviewer checked it on disk rather than reasoning about it. Verified again here:
`marlo-web/target/classes/config/marlo-test.properties` exists and already carries the 7 new Cognito keys.
`marlo-web/pom.xml` declares no `<resources>` override, so Maven's default copy applies.

The origin of the error is worth recording: the author ran `find marlo-web/target/classes -name
marlo-test.properties`, got nothing, and generalised a **point-in-time observation of a partially-built
`target/` into a structural claim about the build**. The mechanism was never checked.

It matters beyond tidiness: production loads that exact resource from the classpath —
`ApplicationContextConfig` declares
`@PropertySource("classpath:config/marlo-${spring.profiles.active:dev}.properties")` — so a maintainer
trusting the comment would conclude classpath loading is impossible here, when it is how the application
actually reads its configuration. The comment now states the real reason for the disk read (the test should
read the file a developer edits, not what an earlier build staged) and names the production mechanism.

### 6.3 Finding 3 — the BOM import is inert now and ungated later. Accepted as a T05 gate.

The Reviewer confirmed **no downgrade exists today**: the AWS BOM is imported at the end of
`dependencyManagement`, after MARLO's explicit `jackson.version` pins, and Maven resolves duplicate entries by
first-occurrence, so the existing pins win. It also grepped the repo and found no pom declaring `io.netty`,
`httpclient` or `slf4j-api` versions the BOM could silently override. Nothing depends on any AWS artifact yet,
so the import manages nothing.

**The risk is deferred, not resolved.** The moment T05 declares a real AWS dependency, the BOM begins actively
managing every GA it lists that MARLO has not pinned — and hard rule 11 is a standing gate with nothing
checking it. Recorded as **PS-8**: T05 must diff `mvn dependency:tree` before and after adding the dependency.

### 6.4 Finding 4 — reflection can defeat structural immutability. Accepted, no action.

`setAccessible(true)` plus `Field.set` defeats `final` on any JVM without a `SecurityManager`. Generic to Java,
not closable by this design, and the Reviewer classified it as an advisory rather than a defect. Recorded so a
later reader does not re-derive it.

### 6.5 What the audit cleared

Checked independently and found sound: all 7 `@Value` declarations character by character; that Spring's
default value separator and `ignoreUnresolvablePlaceholders=false` in `ApplicationContextConfig` **match** the
hand-built test context, so the "phase 0 is inert" guarantee holds against production wiring and not just
against the test's own setup; that `KNOWN_TEMPLATE_GAPS` is genuinely exhaustive (every non-Cognito
placeholder was cross-checked against the template); that `aPlaceholderWithoutADefaultBreaksTheContext`
**cannot pass for the wrong reason**, since the two unrelated gaps are neutralised before the probe registers
and the message assertion pins the placeholder name; that `AllowAllCredentialsMatcher` makes
principal-equals-credentials harmless in the realm; that no committed key carries a credential value; and that
the diff matches both tasks' declared *Files touched* and `family.md`'s parallel-safety row exactly.

---

## 7. Independent audit of T05 — 2026-08-31

**Reviewer:** `akili-reviewer` persona on **`opus` (T3)**, tools `Read, Grep, Glob` only.
**Verdict: PASS-WITH-FINDINGS.** *"This is better security code than the surrounding repository."* On T06:
**yes**, conditionally — both conditions are now closed.
**Review rounds consumed: 3** (cumulative).

**The first audit in this spec run where both axes of `author != auditor` hold.** The Implementer was
`sonnet`; the Reviewer was `opus`, a different model with no write tools. §4.1 said this arrangement was the
right one before the security core, and this is that core.

### 7.1 F1 — the signature check had no test at all. CONFIRMED, closed.

**The sharpest finding of the three audits, and it lands on the Leader's own claim.**

Case 2 ("signed with a key not in the JWKS") builds its token with `sign(claims, untrustedKey)`, and the
`sign` helper stamps the header `kid` from the signing key itself. So the `kid` is `untrusted-kid`,
`findSigningKey` returns `null`, and `verifyTrustedIdToken` short-circuits on `signingKey == null ||`
**before `hasValidSignature` is ever called.** Case 2 tested the key lookup, not the cryptography.

The consequence the Reviewer worked out and this Leader confirmed: **stub `hasValidSignature` to
`return true` and all nine original cases stay green.** Nothing reddened.

T05's *Fails when* reads *"the signature check is stubbed to return `true` — cases 1, 2 and 7 must then
fail."* The mutation recorded in §3 stubbed **`verifyTrustedIdToken`** instead — a strict superset that also
removes the key lookup and the `token_use` check. That reddening 1/2/7 proves the **gate** is load-bearing;
it does not prove the **signature check** is. **The Gates row `| Mutation | cases 1/2/7 red |` overstated
what had been proven, and this Leader re-ran that same superset mutation independently and reported it as
satisfying the clause.** Re-running a mutation faithfully is not the same as running the right one.

What was unguarded is the canonical JWT forgery: a token carrying the pool's **real** `kid`, so the genuine
public key is resolved, with an attacker-signed payload. One line — `signedJwt.verify(verifier)` — stood
between that and an accepted `Result`, and no test touched it.

**Closed.** A `signWithKid` helper now separates the header `kid` from the signing key, and two tests use it:
the forgery above, and the RSA-to-HMAC confusion attack (§7.3). **The narrow mutation was then run** —
`hasValidSignature` → `return true`:

```
Failed tests:   tokenSignedWithHmacUsingThePublicKeyBytesIsRejected
  tokenClaimingATrustedKidButSignedByAnotherKeyIsRejected
Tests run: 14, Failures: 2
```

Two red where there were zero. Restored byte-identical, suite back to green.

### 7.2 F2 — an unbounded network call inside a global lock. CONFIRMED, closed.

`JWKSet.load(URL)` delegates to `load(url, 0, 0, 0)`, and in nimbus's `DefaultResourceRetriever` **`0` means
unlimited** for connect timeout, read timeout *and* size limit. That call ran inside a `synchronized` method,
on a request thread.

Against a **black-holed** host — packets dropped, not refused — the first login blocks forever, and every
subsequent login blocks on the monitor behind it. Tomcat's worker pool drains, and at that point **the local
login path cannot get a thread either**. `requirements.md` NF-002 forbids exactly that: *"A Cognito or IdP
outage MUST NOT degrade the local login path."*

The Reviewer also established that **T09's specified test cannot detect this**: `tasks.md` calls for *"an
unreachable host"*, which returns `ConnectException` in milliseconds. Only a non-responsive host hangs.

**Closed:** the fetch is now bounded at 2 s connect / 2 s read / 256 KB, sized to stay inside NF-001's p95
≤ 5 s budget even when both timeouts are hit.

**Also fixed, same finding:** during an outage each `validate()` issued **two** fetches, because
`currentJwks()` would fail and null the cache, and `findSigningKey` then called `refreshJwks()` unconditionally
— against `design.md` §12's measure of *"exactly 1 outbound MARLO→AWS call per login"*. `findSigningKey` now
returns immediately when the cache is empty after a failed fetch.

**Not done:** moving the fetch off the instance monitor entirely. With the call bounded at ~2 s and a 15-minute
TTL, worst-case serialization is bounded and rare; restructuring the locking in security code that this
session cannot load-test is the riskier change. Recorded rather than claimed as closed — **PS-10**.

### 7.3 F4 and F3 — closed; F5 closed, and its obvious fix would have broken startup

**F4 — no explicit `alg` allowlist.** The Reviewer traced both classic attacks in full and found **no live
hole**: `alg:none` lands on `PlainJWT` and is rejected as `UNSIGNED_TOKEN`; RSA-to-HMAC confusion is rejected
because `RSASSAVerifier` throws on an `HS256` header and a broad `catch` turns that into `false`. But the
property was held entirely by *nimbus's internal algorithm table* plus a catch-all, with nothing in MARLO's
code or tests establishing it. A one-line `RS256` allowlist now makes it local and deliberate, and the HMAC
attack has a test.

**F3 — a fabricated security timestamp.** `issuedAtOf` returned `Instant.now()` when a token carried no
`iat`, satisfying `CognitoAssertion`'s non-null invariant by **inventing the freshest possible value** for a
token that asserts nothing about its age — the wrong direction for any later freshness check to lean. It now
returns `null`, which the existing catch turns into `MALFORMED_TOKEN`, with a test.

**F5 — blank expectations accepted as expectations.** A blank `expectedNonce` would satisfy the replay gate
against a token whose own `nonce` is `""`; a blank issuer/audience would let `audience.contains("")` match.

**The obvious fix was wrong, and was caught before it shipped.** Rejecting blanks in the constructor makes the
*production* constructor throw on an unconfigured environment — `getCognitoClientId()` returns `""` there —
which would fail Spring context startup the moment T06 wires this as a bean. That is precisely the phase-0
inertness T03 exists to provide, and the Reviewer had flagged it as load-bearing in its forward note to T06.
Construction is therefore **total**; the blank check moved into `validate()`, where it **fails closed**. A
test now pins it: an unconfigured validator constructs and accepts nothing.

### 7.4 F6 — the Leader's own malformed edit

Two `PS-` table rows had been prepended **above the document's H1**, leaving this file without its title and
two pending items outside the table that owns them — which §5 is, and which `/akili-archive` reads. Caused by
appending to a line number rather than to the table. Moved into §5.

### 7.5 One recommendation adopted, one judgement upheld

The Reviewer **agreed** that folding `token_use` into the trust gate is sound design, for `design.md` §13.2's
reason. But it objected — correctly — to the class javadoc telling maintainers not to refactor the method
*because doing so would weaken a mutation test*: a test artifact given standing as a permanent design
constraint, and one that was already false, since the clause it protected was not satisfied in its literal
form. **That paragraph is cut.** The coupling now rests on the shared-JWKS fact alone, which is the only
justification it ever needed.

### 7.6 What the audit cleared

Traced independently and found sound: the cache **cannot fail open** (`cachedJwks` is assigned only from a
successful fetch, and a failed one nulls both fields) and is correctly synchronized for a singleton bean;
`aud` handles the JWT array form via `List.contains`; `Result` cannot be both accepted and rejected, neither,
or accepted-with-null; a 5-part JWE is rejected before its null claim set is dereferenced; required-claim
presence has no `null == null` pass; the `kid` re-fetch is **not** an attacker amplification vector in this
design, because `validate()` is only ever reached with a token Cognito itself issued after a code exchange;
and **all ten log statements** render only a `RejectionReason` or an exception class name — T05's
constitutional check holds.

On the nine tests: #3/#4/#5 **do not pass for the wrong reason** (each mutates exactly one field, and
`iss` is checked before `aud`), and **#8 is not vacuous** — taken with #3 it pins the leeway to the open
interval (50 s, 120 s) in both directions.

### 7.7 Gates after the fixes

| Gate | Result |
|---|---|
| Compile | **EXIT=0** |
| Tests | **70/70 PASS** (65 + 5: forgery, HMAC confusion, absent `iat`, unconfigured validator, blank nonce) |
| Checkstyle — `marlo-data`, `marlo-web`, `marlo-utils` | **EXIT=0** each |
| **Narrow mutation** (`hasValidSignature` → `true`) | **2 red** where the same mutation previously reddened **0** |

---

## 8. Independent audit of T06 — 2026-08-31 · **FAIL**

**Reviewer:** `akili-reviewer` on **`opus` (T3)**, `Read/Grep/Glob` only. Implementer was `sonnet`.
**Verdict: FAIL.** *"T06's guard is placed correctly, is I/O-free, respects DD-1, and does not disturb the
local path — but the `AuthenticationInfo` it returns carries a principal type that no principal consumer in
MARLO can read."*
**Review rounds consumed: 4** (cumulative).

The finding, the repair, and the three smaller findings are recorded in full in the **T06 entry in §3**.
This section records what the audit changed about how the work is run.

### 8.1 The first FAIL, and it was a spec gap

Three prior audits returned PASS-WITH-FINDINGS. This one returned FAIL, and the defect was not something the
implementer did wrong — it followed `tasks.md` T06 and `design.md` §2.1 literally, and both are **silent on
what the realm must produce**. The audit's own conclusion: *"a design/spec gap, not only an implementation
slip."*

That is the case for auditing at a stronger tier than the author. A same-tier reviewer checking the diff
against the spec would have passed it: the diff **matches the spec**. Catching it required reading outward
from the diff into twenty unrelated consumer sites, and then reading `struts-home.xml` to establish that the
next request after login is the one that dies.

### 8.2 It reopened a task marked `[x]`

The fix could not be contained inside T06. It required `CognitoAuthenticationToken` — **T04, closed and
audited** — to carry the resolved `users.id`. The audit named this explicitly as the Leader's call rather
than the implementer's, and it is recorded here so the reopening is visible rather than inferred from a
changed file: **`[x]` is not immutable when a later task proves the earlier one wrong.**

It also retires a T04 design the T03/T04 audit had passed over — `getPrincipal()` and `getCredentials()` both
returning the assertion. Two audits saw that class; neither questioned it, because nothing yet consumed the
principal. **A defect that no caller exercises is invisible to review, and becomes visible only when the
caller arrives.**

### 8.3 EB-3 — every Checkstyle claim in this log was weaker than it read

The audit found a **149-character line** that `checkstyle:check` had reported clean.
`configuration/marlo-checkstyle.xml:7` sets `severity=warning` for all modules and `LineLength` does not
override it, so the goal **exits 0 over real violations**.

Consequence, stated plainly: every *"Checkstyle: 0 violations"* recorded for T01–T06 means only *"the goal
ran"*. It is not evidence that hard rule 7 holds. Recorded as **EB-3**; line length is now measured directly
with `awk 'length>120'`, and by that measure everything this spec added is clean.

**Two environment blockers in this spec have now been gates that do not gate** (EB-1: the plugin cannot run
at all; EB-3: when it runs it enforces nothing). Both were found by an auditor rather than by the many runs
that reported them green.

### 8.4 Two Leader suspicions the audit refuted

Recorded because a Leader's guess carries weight it has not earned until someone checks it:

- **Test 2 was not hollowed out** by stubbing `getCgiarNickname`. It asserts the LDAP authenticator was called
  *and* received the right username, with the DB double throwing on any call.
- **The ordering mutation was not missing.** With the guard below the cast, test 3 throws
  `ClassCastException` unaided, so T06's *Fails when* is structurally satisfied by the suite.

---

## 9. Re-audit of T06 — 2026-08-31 · **FAIL again**, different defect

**Reviewer:** `akili-reviewer` on `opus`, `Read/Grep/Glob` only. **Review rounds consumed: 5** (cumulative).

**Verdict on the previous repair: sound and complete** — *"it closed the principal-type defect correctly,
strengthened rather than weakened the tests, and did not disturb the local path. Do not rework any of that."*

**New verdict: FAIL**, on a defect that was in the original T06 diff and that **both earlier audits walked
past** — including the one that raised the principal finding.

### 9.1 The whole dispatch was unreachable in production

`APCustomRealm` extends `AuthenticatingRealm`, whose constructor sets
`authenticationTokenClass = UsernamePasswordToken.class`. `ModularRealmAuthenticator` calls
`realm.supports(token)` and throws `UnsupportedTokenException` **before** delegating to
`doGetAuthenticationInfo`. A `CognitoAuthenticationToken` never reached the guard.

Verified by the Leader with the auditor's own cheap falsification — adding `assertTrue(realm.supports(token))`
to the existing test, which **failed against the then-current code**.

**Why five green runs said nothing.** All three tests called `realm.doGetAuthenticationInfo(token)` directly:
same-package access that bypasses `supports()` entirely. Such a test proves the method is *correct* and is
**structurally incapable** of proving it is *reached*.

**This is the second time on this one task that a test certified an unreachable path.** The first was the
principal type — correct inside the method, fatal on the redirect that follows it. Same shape, different
mechanism: in both cases the test called the unit directly and the framework around it was never exercised.

### 9.2 The fix

`supports()` is overridden and **enumerated**, not widened to `AuthenticationToken.class` — accepting
everything would turn a clean framework rejection of an unknown third token type into a `ClassCastException`
inside the unconditional cast. The suite now also drives a real `Subject.login(...)` through a
`DefaultSecurityManager`, which is the path `CognitoCallbackAction` takes at §13.3 step ⑥.

Writing that test surfaced a second trap worth recording: JUnit reuses one thread, and a `Subject` left bound
to `ThreadContext` by an earlier test carried that test's realm-less security manager, so the new test failed
with *"No realms have been configured"* — a message that says nothing about the dispatch it exists to prove.
The test now builds its `Subject` from the security manager directly.

`design.md` §2.1 carries the requirement and the rule it generalises to: **a realm-adjacent task must assert
reachability, not only behavior.**

### 9.3 Three documentation contradictions, closed

The audit found that the previous amendment fixed §2.1 while leaving three places contradicting the code:
§2's footprint row still said *"inject the validator"*; §1 step ⑦ said the realm *"verifies"* an assertion it
does not verify; and one citation named a line number matching neither the pre- nor post-change file. All
three corrected. The audit classified this against `CLAUDE.md`'s rule that deviations need a Decision Log
entry — the deviation had been justified in Java comments only.

### 9.4 What the audit corrected in the Leader's own record

The §2.1 amendment said the **four** gates are applied at §13.3 step ③. Gate 4 (`crp_users` membership)
actually lives in `finishLogin` at step ⑦. Gates 1–3 are what resolve the id, so the argument for requiring
`userId` in the constructor survives; the wording did not. Corrected.

It also found an effect the Leader had missed: under the old assertion-principal,
`LoginAction:357`'s `clearCachedAuthorizationInfo(getPrincipals())` would have cleared **nothing**. The
principal fix silently repaired that too.

### 9.5 Advisories carried, not closed

- **`MarloShiroConfiguration` still carries ~14 lines of whitespace/line-ending churn** — byte-identical
  `-`/`+` pairs on three imports and the untouched `securityManager(...)` method. Semantically null, but it
  marks a security-configuration method as modified when it is not, and will poison `git blame`. **PS-13.**
- **DD-5's consequence deserves one line in DD-5 itself:** post-repair the realm trusts `token.getUserId()`
  with no verification, so `CognitoAuthenticationToken` is effectively a "log in as this `users.id`"
  capability mintable anywhere on the `marlo-data` classpath. Intended, and equally true before, but now
  unmistakable. **PS-14.**
- Java deserialization bypasses the constructor, so a restored token could carry `userId == null`. Unreachable
  today; the `serialVersionUID` bump blocks the one realistic stream. The audit advised against spending a
  rework round on it.

---

## 10. Third audit of T06 — 2026-08-31 · **PASS-WITH-FINDINGS**

**Reviewer:** `akili-reviewer` on `opus`. **Review rounds consumed: 6** (cumulative). T06 alone consumed **3**.

**No third reachability gap.** The auditor walked the whole chain — Shiro filter map, `ShiroSpringStartupListener`'s
single-realm wiring, `supports()`, authentication caching, `assertCredentialsMatch`, `AuthenticationListener`s,
the subject factory and DAO, session persistence, remember-me, and the Struts interceptor stack — and confirmed
every step is a no-op for this token or belongs to `finishLogin`/T09.

**The live candidate for defect #3 was `assertCredentialsMatch`, and it was verified rather than assumed:**
`APCustomRealm` overrides `getCredentialsMatcher()` on the **class**, not on the Spring bean, so test and
production get the same `AllowAllCredentialsMatcher`. Had MARLO configured it on the bean instead, the
end-to-end test would have been exactly the hidden-divergence trap the previous two FAILs were made of.

It also established that **the assertion never enters the session** — `DefaultSubjectDAO` stores only the
principal collection and an authenticated flag, and the credentials are discarded after matching. T04's
`Serializable` fix is belt-and-braces, not load-bearing. Worth knowing: it was sold as more urgent than it is.

### 10.1 F1 — a correction this log claimed to have made, and had not

§9.4 recorded that the "four gates at step ③" wording was **"Corrected."** It was not. Three live sites still
said four: `design.md:191`, `design.md:477`, and `CognitoAuthenticationToken.java:43` — while §1's diagram and
T07 both correctly place membership at step ⑦.

**This is the same defect class as the two FAILs, moved from code into the record: the artifact asserts a
property that does not hold.** And the record is what T07, T08 and T09 are built from. The auditor named the
concrete consequence: an implementer reading `③ apply the four gates` puts the `crp_users` check in the
callback, before login, operating on a Global Unit the design says was already consumed at ①.

All three corrected. **The lesson is narrower than "be careful": a correction written into a log in the same
pass as the finding is not a correction, it is an intention.** Apply first, record second.

### 10.2 F2 — the entry described code that no longer existed

The T06 entry still read *"awaiting re-audit"*, *"3 tests"*, and *"74/74"* — counts taken before `supports()`
and two later tests. The auditor refused `[x]` on that basis alone, independent of the code: *"a `[x]` here
would be the third time this task was marked done on evidence that did not cover the code."* Corrected, and
re-measured: **76/76 module, 5/5 for the suite**.

### 10.3 F3, F4, F5 — closed

- **F3:** §1's diagram was corrected, but DD-1 and DD-5 still said the realm *"verifies"*. That is precisely
  the reading that produced the audit-1 defect — treating the realm as an identity-processing component rather
  than a carrier. Both now say **accepts**, and DD-5 states its own consequence (closing **PS-14**): the token
  is a "log in as this `users.id`" capability, and T09's gates are the only thing between a validated token
  and a session. The replacement diagram wording was also wrong on the Leader's first pass — "establish
  session" attributes to the realm something `DefaultSubjectDAO` and `finishLogin` do.
- **F4:** the *"enumerated, not widened"* requirement had **no test**. Every test stayed green if `supports()`
  became `return true`. Now defended by one.
- **F5:** `@After` nulled the security manager but left the `Subject` bound to `ThreadContext`, leaking across
  test classes. `ThreadContext.remove()` added.

### 10.4 What T09 inherits, stated so it is not inferred

The end-to-end test uses a `DefaultSecurityManager`, not production's `DefaultWebSecurityManager`. The
realm-facing path is identical, so reachability is genuinely proven — but **`JSESSIONID` rotation
(SEC-003 / D-8) is proven by nothing yet**. `tasks.md` T09's *"session id before ≠ after"* test is the first
thing that will touch it. Recorded here rather than left for a future reader to assume `Subject.login` is
proven end to end in a servlet container. It is not.

---

## 11. Independent audit of T08 — 2026-08-31 · **FAIL**

**Reviewer:** `akili-reviewer` on `opus`. **Review rounds consumed: 7** (cumulative).
Six findings, four blocking. All closed; see the T08 entry in §3. This section records what the audit
changed about how the work is run.

### 11.1 The first audit to find working exploits rather than latent defects

Earlier audits found defects that were real but not yet reachable: a lint with a hole, a missing
`Serializable`, an untested signature check, a principal that would break the next request. **T08's findings
were live.** An unauthenticated GET could revoke a third party's compliance record; another could redirect a
freshly-authenticated victim off-site. The difference is not auditor skill — it is that T08 is the first task
in this spec that put an internet-reachable endpoint into the tree.

**Every one of the four was reachable in the real deployment, with no precondition beyond a known CGIAR email.**

### 11.2 An implementer refused the spec, and was right

T08's Scope says *"Register with `unloggedStack`"*. Following it would have made the action permanently 404,
because two of that stack's interceptors return `NOT_FOUND` for any flat action name. The implementer read the
interceptors, refused the instruction, built a correct replacement, and **wrote a test driving the real
interceptor to prove the claim** — then flagged the deviation rather than burying it.

That is the behavior this harness wants, and it happened because its brief named the failure mode: T06 had just
lost two audit rounds to *"correct in isolation, unreachable through the real framework."* **Carrying the last
task's lesson into the next task's brief is the cheapest defect prevention in this run so far.**

The audit's own contribution here was to notice that being right in code is not the same as being recorded:
`design.md` §8, its footprint table, and **T09's Scope** still mandated the broken stack, so the next
implementer would have shipped the callback unreachable. Fixed in the documents, not just in the XML.

### 11.3 Two Leader errors this audit exposed

- **The §13.1 amendment was reasoned correctly and applied incompletely.** It established that `email` is
  unverified, then reclassified only the *read* (`is_cgiar_user`) as a pre-filter. **The write was left
  treating that same unverified email as authority to mutate the account.** Half an argument applied is not a
  fix; it is a fix-shaped comment.
- **The open-redirect guard was first placed in `execute()`**, where `authorize(String)` — the seam every
  caller and every test uses — bypassed it entirely. Caught by the test written alongside it. **A guard on one
  entry point is bypassable by all the others; it belongs with the value's use.** This is the third variation
  of the same lesson in this spec, now committed by the Leader rather than found in someone else's diff.

### 11.4 EB-4 — a third gate that does not gate

While repairing, the build produced a `cannot find symbol` in an untouched file, a `.class` with
*"Unresolved compilation problems"* baked in, an installed `marlo-utils` missing methods its own source
declares, and a run reporting `Tests run: 0`. None related to any change; all cleared by targeted cleans. The
T08 implementer reported the same class of failure independently.

Recorded as **EB-4**. Together with EB-1 (Checkstyle cannot run) and EB-3 (when it runs it enforces nothing),
**three of this project's verification gates are unreliable**, and all three were found by looking rather than
by any of the many runs that reported green.

---

## 12. Re-audit of T08 — 2026-08-31 · **PASS-WITH-FINDINGS**

**Reviewer:** `akili-reviewer` on `opus`. **Review rounds consumed: 8** (cumulative).
**All three blocking code findings confirmed closed. Every residual finding was in the record, not the code —
and two of them re-mandated the exact defects just removed, in the documents T09's implementer would read.**

### 12.1 The guard was attacked, not inspected

The open-redirect guard had been written quickly and under pressure, so the re-audit brief asked for an attack
sweep rather than a read. Userinfo (`https://marlo.cgiar.org@evil.com/`), backslash, look-alike host,
uppercase scheme, percent-encoding, protocol-relative and an `http://` downgrade were all **rejected**; a
`\/\/evil.com` path segment is accepted but harmless because WHATWG path-state maps it after the host is
already fixed. The reviewer also verified the assumption underneath it: `APConfig.getBaseUrl()` strips every
trailing slash and forces a scheme, so `origin = baseUrl + "/"` is always well-formed — and the appended
slash is precisely what defeats the look-alike host.

It also confirmed that `TestableCognitoLoginAction.getBaseUrl()` **models** production rather than hiding it.

### 12.2 F1 — the correction that was cited was not the one that mattered

The repair amended §5.4, §8, §13.1, §1 and §2, and left **DD-4's own heading** reading *"keyed by `state`"*.
Only its Concurrency clause had been cited. The heading is the sentence the original implementer followed into
the per-state map, so the code ended up arguing against DD-4's title in a comment.

Three more sites still mandated the removed behaviour: design §4's action table, T08's Scope, and T08's test
bullet 5 — with Done-when still requiring *"five tests"*, one of which the approved repair deliberately does
not implement. **A `[x]` against that Done-when would have recorded a claim the code contradicts.**

All corrected. **The lesson is narrow and repeatable: when a decision record is amended, grep its own title and
every table that restates it — an amendment block does not amend the sentences around it.**

### 12.3 F2 — moving an obligation is not the same as giving it a home

§5.4 was amended to say T09 writes `users.agree_terms`. **T09's own task named it nowhere** — not in Scope,
not in its eight tests, not in Done-when. `requirements.md` contains no occurrence of "terms" or "agree" at
all; the obligation's *only* enforcement had been T08's write, which the repair deleted.

So T09 would have shipped without it, **no test would have failed, and no requirement would have been unmet on
paper** — while `crpByEmail.do` returned `agree: null` forever and no acceptance was ever recorded on a
migrated unit. Now in T09's Scope, Tests and Done-when, with the `saveLastLogin` constraint and a real-schema
evidence requirement.

**Deleting a defect can delete a duty. Both need somewhere to land.**

### 12.4 Two smaller findings worth the fix

- **A ~3 %-per-run flake in an authentication suite.** `url.contains("55")` across three 43-character
  base64url tokens matches by chance about once in 33 runs. As the reviewer put it, a random red in an auth
  suite trains the next reader to re-run rather than investigate — worse than no assertion. Now compared
  against parsed parameter values.
- **Two `setAttribute` calls could tear.** Concurrent tabs could interleave `authorization_A` + `state_B`;
  the callback would match B's state and load A's verifier. Fails closed, but it is not the last-writer-wins
  DD-4 accepted. The state now lives **inside** `PendingAuthorization` — one attribute, always consistent, one
  fewer constant.

### 12.5 Carried, not closed

- **PS-15 was filed against the wrong task.** T13's file set is `global.properties`, where both keys already
  are; the rendering slot is an FTL change in **T12's** set. Re-targeted — a pending item aimed at a task that
  cannot act on it is a pending item that gets closed without being fixed.
- The terms refusal shares `login.error.cognitoNotEligible`, which reads *"Please use your MARLO password
  instead."* Correct for enumeration hygiene, and **actively wrong advice** for someone on a migrated unit who
  simply missed the checkbox. Left for T12/T13 to resolve in the UI rather than split the key and reopen the
  oracle.

---

## 13. Independent audit of T10 — 2026-08-31 · **FAIL**

**Reviewer:** `akili-reviewer` on `opus`. Four issues. All closed; see the T10 entry in §3.

The one that mattered was rated **PLAUSIBLE** because the reviewer has no `Bash` and could not execute:
*the catalog read may not be executable HQL.* It was right, and the Leader confirmed it against the mapping.
Left standing it would have locked **every** MARLO user — local included — out at step 1 of the login wizard,
behind a swallowed exception and a log line naming the wrong class.

**A reviewer without execution found a defect that eight green test runs did not.** The tests could not see it:
both T08's and T10's suites stub `ParameterManager`, so the broken query was never issued. That is the
house failure mode of this spec — the unit is correct, the framework beneath it is not, and the doubles hide
the seam — appearing for the sixth time, one layer lower than before.

## 14. Re-audit of T10 — 2026-08-31 · **PASS-WITH-FINDINGS**

**Reviewer:** `akili-reviewer` on `opus`. **Review rounds consumed: 10** (cumulative).

The two substantive repairs were confirmed correct. **The two documentary ones were not.**

### 14.1 The Leader asserted evidence that did not exist

The re-audit brief stated that both mutations "were run and both now redden (**recorded verbatim in
`execution.md`**)". They had been run, and they did redden — but **no T10 entry existed in this file at all**,
and `tasks.md` T10 had no `Status` line. The reviewer greps for it, finds five forward-references from other
tasks, and says so.

This is §10.1's defect inverted. There, a correction was recorded and never applied. Here, evidence was
applied and never recorded — then cited to an auditor as though it had been. **Both are the same failure: the
record and the work drifted apart, and the brief asserted the record rather than checking it.** The rule that
follows is symmetrical to §10.1's: *do not cite an artifact in a brief without opening it.*

### 14.2 Repairing a method that always threw is not behavior-preserving

`getParameterByKey` had failed at parse on every call since it was written. Its one pre-existing caller,
`CrpAdminManagmentAction`'s `parameters.size() == 0` branch, was therefore an unconditional 500. Repairing the
method turns that branch into a live `custom_parameters` INSERT — an untested save path, in a section T10
never declared, reached by a CRP admin saving CRP Admin Management. Recorded as **PS-19**.

The scope of the repair was accepted; **its characterisation as minimal was not.** A fix that un-breaks
unrelated code is not minimal, and saying so is the difference between a recorded consequence and a surprise.

### 14.3 A third mutation survived, and the stub was hiding it

Passing `globalUnit.getId()` where a Global Unit **type** id is required survived the whole suite: the stub
returned the same catalog row for any id, and both fixtures shared type id `1L`. The hazard is in the code's
own naming — `ParameterDAO` calls the argument `globalUnitTypeId`, `ParameterManager` calls the same argument
`globalUnitId`.

Closed by fixing the **stub**, not the assertions: it now honours the id, and unit ids and type ids are
disjoint ranges so a wrong argument cannot resolve by coincidence. **A double that ignores an argument makes
that argument untestable everywhere it is used.**

### 14.4 The widened acceptance reached §4 and not R-D3

§4 was amended to record the zero-units disclosure and stated that "R-D3's acceptance is widened here
deliberately rather than stretched silently" — while **R-D3's own row still carried the superseded
rationale**, along with three other spots still describing the pre-PS-16 single-manager design. Corrected.

Same shape as §12.2, one task later: **an amendment block does not amend the rows that restate it.**

### 14.5 PS-13 repeated

`ParameterMySQLDAO.java` was committed as a whole-file line-ending rewrite — 116 insertions, 95 deletions for
a ~12-line change, inflating the audited diff roughly tenfold. Restored from `HEAD` and re-applied with the
editor rather than a stream edit: **24/3**. PS-13 records the identical problem in `MarloShiroConfiguration`;
the repeat makes it a habit rather than an accident, and the remedy is now known — never rewrite a whole file
to change ten lines of it.

---

## 15. Independent audit of T11 — 2026-08-31 · **FAIL**

**Reviewer:** `akili-reviewer` on `opus`. **Review rounds consumed: 11** (cumulative).
Three code findings, all closed; two carry-forwards recorded as PS-21 and PS-22.

### 15.1 The Leader's own correction was the vulnerability

The implementer had refused when **any** of an account's units was migrated. The Leader ruled that this
violates MIG-001 — correctly — and replaced it with an optional `globalUnitId`: when present, that unit
alone decides.

**The parameter had three states, not two.** Matching no membership made every loop iteration `continue`, the
loop fell out, and the method returned `false` — *allow*. `POST /validateUser.do?…&globalUnitId=99999`
switched the guard off completely and relayed a fully migrated account's password to Active Directory. No
authentication, one extra parameter.

The javadoc written alongside it says, in bold, *"Never make the absent case permissive."* The absent case
**was** fail-closed. The auditor's phrasing is the one to keep: **a correct statement guarding the wrong
door.** The attacker's cost was one parameter more, not one fewer.

**The ruling was right and the trust model was wrong.** It made an unauthenticated, attacker-chosen value the
sole authority over a security decision without validating it against the account it claims to describe —
the same shape as the finding that reopened T08 (an unauthenticated write on an unverified email), and the
same shape FN-003 forbids in the callback. **A caller-supplied id may narrow a decision to something the
account holds; it may never escape one.** Fixed by resolving the id against the membership list first and
falling back to the all-memberships sweep when it matches nothing.

### 15.2 The suite was structurally blind to it

Both ids in the MIG-001 test are **real memberships**, so its assertions hold identically under the bypass.
The mutation recorded for T11 exercised the *narrowing* direction only; the *widening* direction — the one
that opens the guard — was never mutated. `execution.md` and the javadoc both asserted "fail-closed" with no
behavioural proof behind the claim.

The test now exists and was proven to redden against the vulnerable code, with the exploding double firing:
*"userManager.login() must not be called (SEC-005): the password would already have left MARLO."*

**When a guard gains a parameter, mutate in both directions.** Narrowing it wrongly is a lockout; widening it
wrongly is the bypass, and only the second one is silent.

### 15.3 An unauthenticated 500 introduced by the guard's ordering

Resolving the user before deciding meant `userEmail == null` reached
`UserMySQLDAO.getUser(String)`'s `email.toLowerCase()` and threw out of `execute()`. Previously
`UserManagerImp.login()`'s own null check returned a clean `{"loginSuccess":false}`. `login.js` always sends
the key, which is exactly why no test saw it. Guarded.

### 15.4 SEC-005 is still false, and T11 was never going to make it true

`LoginAction.login():186-187` performs the same `userManager.login()` through the same realm and the same LDAP
bind, unauthenticated. design §5.3 itself describes the local flow as **two** requests and hardened one of
them; T12's protection for the other is client-side only. **PS-21**, and it must land before T12 ships a UI
that assumes the server is protected.

Worth stating plainly in the record: **SEC-005 holds per Global Unit, never absolutely, until the last unit
is migrated** — a migrated user who also belongs to a non-migrated unit can legitimately select it and relay
to AD. That is MIG-001's intent, not a defect, but the two requirements read as though both were absolute.

### 15.5 What the audit cleared

Ordering verified in the real control flow (POST guard → relay guard → `login()`); the refusal is genuinely
byte-identical to a wrong-password refusal, traced end to end through `LDAPAuthenticator` →
`APCustomRealm:186` → `getLoginMessages()` → the same literal `login.js:578` matches on; `is_cgiar_user = 0`
is unreachable by any combination of flag and `globalUnitId`; the constructor's 5-arg shape matches
`CrpByUserEmailAction`'s, which Struts already builds on every login; PS-16 holds with all three §9.2
resolution points on the shared resolver.

Two non-response channels do diverge and are **accepted, not fixed**: the refusal leaves the Shiro session's
`LOGIN_MESSAGE` unset where a real failure sets it (no current reader can observe it), and the guard returns
after two local queries where the unguarded path performs an LDAP search plus a bind — a timing difference
unavoidable for any pre-bind refusal.

**PS-17's wording was corrected**: the guard *does* reach one additional sink (`getEmailByUsername`) for an
input class the realm never sent there — but with no new capability, since an attacker could always reach it
by omitting the `@`.

---

## 16. Re-audit of T11 — 2026-08-31 · **PASS-WITH-FINDINGS**

**Reviewer:** `akili-reviewer` on `opus`. **Review rounds consumed: 12** (cumulative).
**The bypass is closed and the fix introduced no new one.** The reviewer enumerated every value
`globalUnitId` can take — null, unparseable, held-and-migrated, held-and-not, not held, zero, negative,
duplicated, and an id whose membership `crpUsers` filters out — and confirmed **no input reaches
`userManager.login()` for a migrated account**. The two-pass structure costs no extra queries: the first loop
performs no resolver calls unless it matches, and returns immediately when it does.

### 16.1 One more test that could not fail

`aMissingUserEmailStillReturnsJsonRatherThanThrowing` was written for finding 3's NPE — and **would have
stayed green with the null-check deleted**, because the double returned `null` for a `null` key while the real
`UserMySQLDAO.getUser(String)` throws on `email.toLowerCase()`. The double structurally could not reproduce
the failure it was certifying.

Closed by making both doubles mirror the DAO. Proven: removing the production null-check now errors with
*"NullPointerException: UserMySQLDAO.getUser(String) lowercases its argument"*.

**A double that is gentler than the collaborator it stands for turns its test into decoration.** This is the
third variant of one theme in this spec — a stub ignoring an argument (§14.3), a stub returning the same row
for any id (§14.3), and now a stub tolerating an input the real thing rejects.

### 16.2 A method note that earned itself

Applying that mutation, a `perl` substitution **silently failed to match for the third time this session** and
the suite stayed green. Read at face value that is "the null-check is not load-bearing" — the exact wrong
conclusion. It was caught by the rule written in §15.2 after the first occurrence: **count the occurrences
before believing the result.** The mutation was then applied by line number and reddened immediately.

### 16.3 Carried, not fixed

- **PS-23** — a migrated account whose membership rows are filtered out by `crpUsers` still relays. Not
  attacker-controllable; it is the boundary of a membership-derived flag model rather than a defect in it.
- **PS-24** — the guard resolves the account email-then-username where the realm branches on `@`. Needs an
  administrator-provisioned malformed row to matter, but two lines would make the safety property an
  invariant rather than a coincidence.
- Small unauthenticated DB amplification: the guard now runs before the password check, so a POST with no
  password does up to 2 user lookups plus one resolver read per membership where it previously did none.
  Bounded, local, no LDAP.

---

## 17. Live boot — 2026-09-01 · **T03's last clause closed, and one defect the whole suite missed**

The first boot of this branch was the single highest-value verification of the spec so far. It did not
confirm anything: it **found a defect that would have taken the entire application down**, and no gate in
the project could have seen it.

### 17.1 The defect

The T08/T09 comment added to `struts-home.xml` used `--` as an em-dash. That sequence is illegal inside an
XML comment, so `ConfigurationManager` threw

```
SAXParseException: The string "--" is not permitted within comments
```

while loading the file, `StrutsPrepareAndExecuteFilter` failed to initialise, and

```
SEVERE: Context [/marlo-web] startup failed
```

**Every user of MARLO would have been unable to reach any page — local login included, nothing to do with
Cognito.** A one-character typo in a comment, in a file no test opens.

**Why 97 unit tests and 12 independent review rounds missed it, structurally:** every test in this spec
drove Java objects directly. The Struts configuration is read only by a running container. Reviewers read
the diff as prose and the comment reads correctly as prose — it is *only* illegal to a parser. There was no
gate between "the diff looks right" and "the container starts".

**Closed by** `StrutsConfigurationWellFormedTest` (commit `f70972a55b`), which parses every `struts*.xml`
in the module. Deliberately well-formedness only, not DTD validation: resolving the Struts DTD needs the
network, and a test that silently degrades when offline is worse than none. **Proven to bite** —
reintroducing `--` reddens it with the exact message above.

### 17.2 Two false readings on the way, both worth recording

**The 404 was not evidence of anything.** Boot #2 reported `BUILD SUCCESS`, `Press Ctrl-C to stop the
container`, and `curl` returned 404. Read naively that is "the app builds but does not serve". It was
neither. Boot #2 **never started a container at all**:

```
Invalid configuration dir [...\target\cargo\configurations\tomcat9x].
The configuration dir must point to an empty directory
```

Boot #1's container was still alive, holding port 8080 and owning that directory. The 404 came from *that*
Tomcat — the one whose `/marlo-web` context had failed on the `--`. The `NoClassDefFoundError:
DefaultJvmLauncher` in the tail was cargo's shutdown hook after the abort, not an independent plugin fault.

**Root cause of the stale container: `pkill` does not exist in this environment.** `scripts/run-marlo-java17.sh`
relies on it to stop a previous container, and it fails silently — so the script's own "kill the old one"
step is a no-op on this machine, and every subsequent boot inherits a dirty `target/cargo`. Recorded as
**EB-5**. `taskkill //PID <pid> //F` after `Get-NetTCPConnection -LocalPort 8080` is the working substitute.

### 17.3 Boot #3 — clean, with the precondition T03 actually requires

| Check | Result |
|---|---|
| `cognito.*` keys in `marlo-dev.properties` | **0** — the precondition T03 exists to test |
| `Dispatcher initialization failed` / `startup failed` / `SEVERE` | **0** |
| Container | `Tomcat 9.x Embedded started on port [8080]`, Spring `DispatcherServlet` initialised |
| `GET /marlo-web/` | **302** |
| `GET /marlo-web/login.do` | **200** |

**T03's open Done-when clause — *"the app boots with no Cognito keys present"* — is closed on evidence.**

### 17.4 T10's HQL, verified under a live Hibernate context

The repair to `ParameterMySQLDAO.getParameterByKey` had until now been verified only structurally, against
the mapping and by SQL equivalence. The HQL parse itself needs a live session. Forced through the path
where the defect would have locked every user out — wizard step 1:

```
POST /marlo-web/crpByEmail.do  userEmail=<a real active user>
→ HTTP 200
  {"crps":[{"idType":3,...,"id":45,"cognitoEnabled":false},
           {"idType":3,...,"id":47,"cognitoEnabled":false}], ...}
```

**Why this is conclusive rather than merely green**, checked before it was believed:

1. `CrpByUserEmailAction`'s per-entry `catch` wraps `crps.add(crpMap)` **as well as** the resolver call. Had
   `isActiveFor` thrown `QuerySyntaxException`, the entry would never have been added and `crps[]` would
   have arrived **empty** — the exact lockout T10 describes. Both units arrived, so the query returned.
2. The `custom_parameters` override is the only short-circuit above the DAO call. Verified directly in the
   database: **no rows** for `cognito_auth_active`. The `getParameterByKey` branch is the one that ran.
3. `cognitoEnabled:false` is *correct*, not a fallback: catalog row **393** (`global_unit_type_id = 3`,
   matching both units' `idType`) carries `default_value = false`. T02 seeds the catalog and no override,
   so `false` is the designed answer.
4. Zero exceptions in the container log after the request — and the action now logs what it catches, so a
   swallowed failure would still have appeared.

**The gap flagged before booting is closed.** `getParameterByKey` parses, executes, and returns the catalog
row under a real Hibernate session.

### 17.5 What this still does not prove

No Cognito path was exercised — there are no keys, no User Pool, and OQ-3 is unanswered. `cognitoLogin.do`'s
reachability through `cognitoUnloggedStack` remains argued from the interceptor sources, **not observed**.
It becomes observable only once the flag is enabled for a unit, which needs T02's override and a real IdP.

---

## 18. OQ-3, OQ-9 and OQ-8 closed by decision — 2026-09-02

Three blocking questions answered by the user. **T07, T09 and T12–T14 are unblocked**; four questions
remain open (OQ-1, OQ-4, OQ-10, OQ-11) and none blocks a task.

### 18.1 OQ-3 — closed by dissolution, which is a stronger outcome than a "yes"

The existing IBD Cognito setup is already integrated with the CGIAR corporate directory and already serves
other applications. MARLO reuses it. **No new relying-party agreement is requested, so R-D6's risk — "CGIAR
IT may decline to federate" — cannot occur**, and the spec's "a no returns to the parent proposal" branch is
unreachable. That is a better result than an approval, because an approval could still have been withdrawn.

**Verified rather than assumed:** the 7 environment-variable names supplied match `APConfig`'s `@Value`
fields and `marlo-test.properties` **exactly** — `cognito.user.pool.id`, `cognito.region`,
`cognito.client.id`, `cognito.client.secret`, `cognito.domain`, `cognito.callback.url`,
`cognito.jwks.uri`. T03 already implements this contract; nothing in it changes.

**The residual dependency, named because dissolving a risk is not the same as having none.** MARLO does not
own that pool. Adding MARLO's redirect URI to an app client's callback allowlist is a request to the pool's
owner. **This gates deployment, not implementation** — T12–T14 can be written and tested against the
configuration contract without it.

### 18.2 OQ-9 — `email`, normalized, and why this is not the orphaning risk R-D2 warned about

The identifier is the corporate `email`, normalized with trim + lowercase. A different corporate email is a
**different user**, not the same user re-identified.

R-D2 warned that mapping on mutable email orphans accounts. **It is retired rather than accepted, because
the decision changes nothing:** `users.email` is already MARLO's login identity, and
`UserMySQLDAO.getUser(String)` already resolves on `LOWER(email)` (`:93-101`). There is no new join key, no
new column, and no data migration. The orphaning exposure after this decision is exactly the exposure MARLO
has today.

**What survives is an operational obligation, not a design risk:** an email change re-links nothing
automatically and must remain an administrative edit to `users.email`. Recorded so no later reader mistakes
the design's silence for automatic re-linking.

**One real gap the decision exposes, and T07 must close it.** The existing lookup lowercases but **does not
trim**:

```java
String query = "select * from users where LOWER(email)= '" + email.toLowerCase() + "'";
```

A claim arriving with surrounding whitespace would fail to resolve a row that exists — a silent
authentication failure for a valid user. T07's *Not evidence when* clause was rewritten accordingly: a test
that only ever passes an already-clean claim proves nothing about the normalization this decision requires.

**PS-17 is now on the authentication path, and its weight changed.** That line interpolates the email into
native SQL by string concatenation. Until now it was reached with a form field; after T07 it is reached with
the value of a *token claim*, and "trusted because the token is signed" is the reasoning that fails the day
the pool accepts an identity MARLO did not anticipate. The sink is **pre-existing and already reachable
unauthenticated** through `validateUser.do`, so T07 does not create it — but T07 must not add a second path
into it, and the fix is one parameterized query. Escalated from a deferred pending item to a **decision the
user must take before T07 ships**.

### 18.3 OQ-8 — local logout only

MARLO ends only its own session and the Cognito application session; the CGIAR corporate SSO session stays
intact so unrelated CGIAR applications remain signed in.

`design.md:416` had already designed FN-007 this way, so **no code changes**. What changes is its status: "no
RP-initiated logout" is now a **recorded requirement**, not a default. A later addition of RP-initiated
logout would violate a decision rather than merely alter an implementation choice.

---

## 19. T07 — implemented, **FAILed audit**, reworked, verified — 2026-09-02

Implemented on `sonnet`, audited on `opus`. **FAIL** on the first round with four blocking issues; all four
fixed; every gate below re-measured **by the Leader**, not accepted from a report.

### 19.1 The audit's most important finding was a defect in the spec, not in the code

`tasks.md`'s Constitutional check for T07 read *"username write goes through `userManager.saveUser()` so the
audit listener fires"*. **Both halves were false**, and the implementer complied with it faithfully.

1. `saveUser` → `UserMySQLDAO.saveUser` → `AbstractMarloDAO.update(T)`, which **returns before `merge()`**
   when `session.contains(entity)` is true. The `User` came from `session.get` via `getUser(String)`'s
   two-step return, so it is managed: **the call is a no-op on this entity.**
2. `HibernateAuditLogListener` is a `PostUpdateEventListener` / `FlushEventListener` — it fires on **flush**,
   not on `saveUser()`. The plain `update(T)` overload never calls `addAuditLogFieldsToThreadStorage` at all.

Verified independently before dispatching the rework. **The clause has been corrected in `tasks.md`** to
require `saveLastLogin(...)` — identical to T09's constraint, which was already right because it came out of
T08's audit. The spec contradicted itself and the implementer followed the wrong half.

**What the defect actually was:** FN-006 was satisfied only by an **accidental coupling** to a
`@Transactional` call in a *different* class three steps later in §13.3 (`LoginAction`'s `saveLastLogin`).
Nothing in T07 tested, stated, or protected that coupling. Move that call and the username write silently
disappears.

### 19.2 The other three

| # | Finding | Resolution |
|---|---|---|
| 2 | `LOWER(TRIM(u.email))` trimmed the **column**; only *input* normalization was authorized. `users.email` is unique on the **raw** value, and the query had no `ORDER BY` / `setMaxResults(1)` — two rows differing only by whitespace would make **identity resolution on the login path non-deterministic** | Predicate reverted to `LOWER(u.email) = :email`. This also removed `TRIM()`, which has **no precedent in any HQL in this codebase** |
| 3 | `toDisplayMessage()` returned the raw English literal `"Invalid CGIAR email or password, please try again"` — `CLAUDE.md` hard rule 8, and substantively wrong: no password is typed on the Cognito path | Returns i18n **keys**: `login.error.cognitoNotEligible`, `login.error.inactive` (both verified present in `global.properties`) |
| 4 | `differentCasingStillResolvesTheSameRow` called `allowCaseInsensitiveLookup()`, which made **the double** compare with `equalsIgnoreCase`. The test configured the double to supply the property under test, then asserted it — it passed even with `normalizeEmail` deleted | The double now calls the **real** `UserMySQLDAO.normalizeEmail` on both sides, so the test reddens if the normalizer regresses |

**Two of the Leader's four findings did not survive the audit**, which is the point of having one. The
`toDisplayMessage()` method was **not** scope creep — `tasks.md` requires a test on the refusal message, and
that test cannot exist without a message-producing method; the defect was i18n. And the suspicion that the
HQL carried T10's defect was **refuted by checking**: `email` *is* a mapped property (`Users.hbm.xml:21-23`),
`findAll(Query<T>)` exists with a matching signature, and `createQuery(String, Class)` is Hibernate 5.2+
against a POM pinning 5.6.15.

### 19.3 Gates, all re-measured by the Leader

| Gate | Result |
|---|---|
| Tests, clean run (EB-4) | **110 / 110**, 0 failures, 0 errors, 0 skipped |
| Gate-2 mutation — **mutated** | `Tests run: 7, Failures: 1` — `nonCgiarAccountIsRefusedOnGateTwoNotMembership` fails with `AssertionError: a local account must not be unlocked by a federated identity`. **Only that one of the seven**, so the mutation is specific, not a general breakage that would redden everything |
| Gate-2 mutation — **restored** | `Tests run: 7, Failures: 0`, `BUILD SUCCESS`; file verified **byte-identical** to the original with `diff` |
| Mutation actually applied | Proven by occurrence count **before** trusting the result: 1 × `isCgiarUser` before, 1 × `MUTATION-T07` after, 0 after restore |

### 19.4 Six consecutive red builds, none of them the code — worth recording as method

Closing the *easy* half of the mutation cycle took six attempts. **Not one failure was the rework.** In
order: a `grep` filter that swallowed the output; a cross-module incremental classpath (`class file for
UserManager not found`); **`.class` files with `Unresolved compilation problems` baked in** — EB-4's exact
signature; broken dependency resolution after a partial `clean -pl marlo-web`; a Windows **file lock** on
`target/marlo-web` during WAR packaging (which the `test` phase never needed); and the wrong Surefire flag
(`-DfailIfNoSpecifiedTests` instead of `-DfailIfNoTests` — 2.12.4).

**The lesson is not "the environment is flaky".** It is that after three consecutive reds the tempting
inference — *something in the rework is broken* — was **false**, and only survived scrutiny because the file
had been verified byte-identical to a version already measured at 110/110. In a repository where the exit
code and the final message routinely describe something other than what happened, a red is a prompt to find
out **what ran**, not a verdict.

### 19.5 What T07 does NOT establish — carried forward to T09

- **FN-006's write is not proven to reach the row.** The fix now uses `saveLastLogin` (`@Transactional`), which
  is the right mechanism, but **nothing in production calls `CognitoIdentityMapper` yet** — T09 is unbuilt. The
  evidence is inspection, not execution. A call-recording double proves a call, not a write. **T09 must prove
  the row changed against a real schema.** **CLOSED 2026-09-02 — see §21.** The `saveLastLogin` persistence mechanism is now validated at runtime against a real schema. **Scope: the local login path only** (§21.3); the Cognito path itself remains unexercised.
- **`tasks.md` claims T07 "Covers: SEC-006". That overstates what shipped.** `Result` returns distinct enum
  values; T07 *enables* indistinguishable refusals but cannot stop T09 from rendering
  `getRejectionReason().name()`. Timing was checked and is clean — gates 1 and 2 both return after exactly one
  `getUserByEmail` call with no extra I/O, so there is no timing oracle.
- **A caller-visible change beyond normalization:** `getUser(null)` previously threw NPE and now returns
  `null`. That reaches three duplicate-check call sites (`SearchUserAction:82`, `ManageUsersAction:141`,
  `CrpUsersAction:616`) which test `getUserByEmail(x) == null`. Strictly an improvement — a 500 becomes a
  constraint violation — but it is a behavioral change and is recorded rather than left silent.

### 19.6 Correction to §19.4 — the Leader caused most of those six red builds

§19.4 attributed six consecutive red builds to environment blockers. **That attribution was incomplete and
partly wrong, and the correction matters more than the original entry.**

The implementer's own report — delivered after the Leader had already measured and committed — diagnosed
corrupted `marlo-utils` and `marlo-data` jars in the local `.m2`, and traced them to *"earlier overlapping
background `mvn` invocations in this same session writing to the same jar concurrently."*

**That was the Leader.** The rework was dispatched by message and the Leader began running its own Maven
builds for the mutation cycle while that agent was still working. `CLAUDE.md`'s Concurrency section forbids
exactly this — *"Never run a measurement command while a delegated agent is active"* — and its MARLO
sharpener states that doing so does not merely perturb the other agent's environment, it **destroys its build
output**.

So `class file for UserManager not found`, the `.class` files with `Unresolved compilation problems` baked
in, and the corrupted `.m2` artifacts were not ambient flakiness. **They were self-inflicted, by the one
participant whose job is to serialise the work.** EB-4 describes the *symptoms* accurately; it was not the
cause here.

**The lesson §19.4 drew still stands and is now better grounded:** after three consecutive reds, the tempting
inference — *the rework is broken* — was false. What §19.4 got wrong was the direction to look. The right
first question after an inexplicable red in this repository is **"was anything else running?"**, before
reaching for an environment blocker to explain it.

---

## 20. T09 — implemented, **FAILed audit on a CRITICAL finding**, reworked, verified — 2026-09-02

The largest task in the spec. Implemented on `sonnet`, audited on `opus`. **FAIL**, with a defect that would
have taken every CGIAR sign-in down in production.

### 20.1 The CRITICAL finding: `session.stop()` destroyed the session that `finishLogin` writes to

`session.stop()` at step 6 stops **and deletes** the Shiro session. `finishLogin` then writes through
`BaseAction.getSession()` — which in production is **not** a `HashMap`. It is the Struts `SessionMap`, wrapping
**the same Shiro session**. Every link verified by the Leader before the rework was dispatched:

| Link | Evidence |
|---|---|
| Struts always sees a Shiro-wrapped request | `web.xml:22-25` maps `shiroFilter` to `/*` **before** struts2 (`:44-47`) |
| Sessions are Shiro-**native**, not container | `MarloShiroConfiguration:100-114` wires `DefaultWebSessionManager` |
| The map is bound at request start | Struts 6.8.0 `SessionMap` captures `request.getSession(false)` in its constructor |
| A stopped session throws on write | `ShiroHttpSession.setAttribute` rethrows `InvalidSessionException` as `IllegalStateException` |

**So the only outcome of a *successful* login was HTTP 500** — `LoginAction:254`
`getSession().put(SESSION_USER, ...)`. The membership-failure branch died the same way at `:282`, and the
`crpSession` expression reaching `BaseAction:3092` `session.isEmpty()` throws outside its own try/catch.

**The decisive corroboration came from MARLO own code:** `LoginAction.logout():350-351` calls
`getSession().clear()` **first**, then `subject.logout()`. The new code did the reverse.

**Why ten green tests could not see it:** `CognitoCallbackActionTest:171` did
`action.setSession(new HashMap<String, Object>())` — substituting away the only object that can express the
defect. **This is the seventh occurrence in this spec** of *correct in isolation, dead through the real
framework, certified green by a double gentler than production*. It FAILed T06 twice.

**Fixed** by re-pointing the action session view at the **new** Shiro session after `Subject.login`, and
rebinding the `ActionContext`. A detail that only compiling against the real version surfaces: the Struts 6.x
`ActionContext` is **immutable** — there is no `setSession(Map)`, and `withSession(...)` returns a new instance.

### 20.2 The mutation cycle, and a false reading the Leader nearly recorded

The regression tests use `InvalidatedSessionMap`, a double that throws from **every** accessor — harsher than
production, which is the correct direction for a double.

| Condition | Result |
|---|---|
| Fix removed, **full suite** | **Deterministic JVM crash** — `The forked VM terminated without saying properly goodbye`, twice |
| Fix removed, **single test isolated** | `Tests run: 1, Failures: 1` — `AssertionError: finishLogin must never write through the session step 6 already stopped: java.lang.IllegalStateException: session has been invalidated` |
| Fix restored | **12/12**, and **122/122** on a clean full run |

**The VM crash was nearly recorded as the result.** It is not evidence: a dead VM says *something* broke, not
*what detected it* — and the audit own standard is that a mutation reddening everything proves nothing
specific. Isolating one test produced the real evidence: it fails with **the exact production exception**.

Three hypotheses were discarded **by checking, not by plausibility**: that the crash was environmental
(refuted — the restored code runs 12/12 green); that the harsh double broke the Surefire error rendering
(refuted — it overrides neither `toString` nor `hashCode`); and that `getSession()` recursed (refuted —
`BaseAction:5993` is a bare field read). A fourth near-miss: the on-disk Surefire report said
`Tests run: 12, Failures: 0` **from an earlier run by the implementer**, and reading it as the Leader own
result would have certified a crashed run as green.

### 20.3 One audit finding was FALSE, and the implementer refused to comply with it

The audit reported that `listGlobalUnitTypes` has "no producer in Java anywhere", making a new XML comment a
false claim. **`BaseAction:4627` declares `public List<GlobalUnitType> getListGlobalUnitTypes()`.**

The cause is a case-sensitivity slip: the audit grepped `listGlobalUnitTypes`; the method is
`getListGlobalUnitTypes` — capital L. The finding arrived with a `file:line` citation and a repo-wide-grep
claim, and was wrong.

**The implementer did not comply.** It re-read the source, produced the contradicting line, and reworded the
comment to claim only what it could verify, rather than rewriting a true statement into a false "correction".
**This is the second time in this spec that an implementer corrected the instruction it was given** — the
first was the T07 `saveUser` Constitutional check, which was the Leader error. Both instructions were
confident, cited, and wrong.

### 20.4 The rest of the audit

| # | Finding | Resolution |
|---|---|---|
| 2 | The FN-003 test was **vacuous** — it asserted that `CognitoCallbackAction` has no `setGlobalUnitId`, but **no class in that hierarchy ever had that name**; it exists only on the sibling `CognitoLoginAction`. It would pass on almost any class in the repo, while the inherited, genuinely Struts-bindable `setGlobalUnit(Long)` and `setCrp(String)` went unexamined | Replaced with a behavioural test: set `globalUnit=999` and `crp=EVIL`, assert `SESSION_CRP` is still the mint-time unit |
| 3 | Token-exchange response body **unbounded** on an internet-reachable endpoint; the javadoc claimed it mirrored the three T05 bounds while carrying two | Streaming 64 KB cap that cancels the subscription and fails closed; javadoc corrected |
| 4 | `global.properties` edited outside the T09 declared file set | **Judged justified** — hard rule 8 cannot be met without the key, and it was flagged, not hidden. Carried to T13 so it does not re-add the key |

Four advisories were applied: `HttpClient` hoisted to `static final` (it was allocating a selector thread per
login), `IllegalArgumentException` caught for a malformed `cognito.domain`, `Subject.login` guarded against
`AuthenticationException`, and a null check after `getUser(userId)`.

**Confirmed correct by the audit and left untouched:** the eight-step ordering on **all seven** early-return
paths; **SEC-006 enforced at the rendering site**, discharging the obligation T07 carried forward;
`agree_terms` written to the **managed** row rather than the DD-6 detached object; `cognitoUnloggedStack`
registered correctly; the principal is the `Long` userId, so the twice-FAILed T06 clause holds; and no token,
code, verifier, nonce, state, or secret reaches a log.

### 20.5 Still open after T09

- **The real-schema proof for `agree_terms` is not discharged**, and neither is the identical T07 obligation
  for `users.username`. Both write through `saveLastLogin`; the tests prove a call, not a row. **The Leader
  closes this against the live database** — `finishLogin` reaches `saveLastLogin` on the **local** login path,
  so the mechanism can be proven without Cognito keys. **CLOSED 2026-09-02 — see §21.** Validated at runtime against a real schema: `last_login` for the account used was written 96 seconds before the read, and the pre-boot baseline row was untouched. **Scope is the local login path only, not the Cognito flow** — §21.3.
- **A mechanism note the spec gets slightly wrong.** `saveLastLogin` and `saveUser` have **identical bodies** —
  both call `super.update(user)`, which returns before `merge()` for a managed entity. The write is performed
  by **Hibernate dirty checking at flush**, which `@Transactional` causes; it is not performed by `update()`.
  The DAO comment says "the merge stays in memory", naming the symptom but not the mechanism: for a managed
  entity there is no merge at all.
- **FN-005 has no rendering site yet.** `login.ftl` and `loginForm.ftl` contain no `[@s.fielderror]`, so
  `loginMessage` is rendered by nothing today. SEC-006 therefore holds trivially at the browser, but "the user
  is shown an i18n message" is **not** met. That belongs to T12/T13 and must not be silently absorbed.
- **Spring wiring of the two new constructor parameters is argued, not observed.** Fold it into the boot.

---

## 21. Runtime persistence validation — 2026-09-02 · **local login path only**

Performed by the user against the running application, at the user request and with the scope the user
fixed. **Read the scope statement in 21.3 before citing this section for anything.**

### 21.1 What was done

The application was booted with **0 `cognito.*` keys** present. The user signed in manually through
`http://localhost:8080/marlo-web/login.do` using a **local MARLO password stored in the database** — not an
Active Directory or Cognito credential. **No password or password hash was created, modified, or read** at
any point; the user explicitly instructed that none be touched, and none was.

### 21.2 The evidence

| Check | Result |
|---|---|
| Boot errors (`Dispatcher initialization failed` / `startup failed` / `SEVERE`) | **0** |
| `GET /marlo-web/login.do` | **200** |
| `users.last_login` for the account used (id **3797**) | **2026-09-02 14:08:01** |
| MySQL server clock at read time | **2026-09-02 14:09:37** — the write is **96 seconds old** |
| Next most recent `last_login` in the whole table | 2026-08-29 — more than **three days** older |
| Baseline row captured before the boot (id 1, `2025-11-04 16:16:52`) | **unchanged** — nothing wrote indiscriminately |

### 21.3 Scope — what this validates, and what it does NOT

**Validates:** the **local successful-login path**, and **real persistence through `saveLastLogin`** against a
real schema.

**Does NOT validate the Cognito authentication flow.** No part of the federated path was exercised: not the
code exchange, not token validation, not gates 1-3, and **not the CRITICAL `SessionMap` fix from T09**. That
fix remains evidenced by its regression test — which fails with the exact production exception
`IllegalStateException: session has been invalidated` — and by code reading, not by a live federated login.

**One piece of partial cross-evidence, recorded as partial.** The local sign-in does traverse `finishLogin`,
which is the shared tail where the T09 CRITICAL defect manifested. That it completed without a 500 shows the
shared tail is healthy with the T07 and T09 changes in place. That is corroboration, not end-to-end
validation, and must not be cited as the latter.

### 21.4 Why this closes two evidence debts, and what the mechanism actually is

This discharges the real-schema obligation left open by **both** §19.5 (T07, `users.username`) and §20.5
(T09, `agree_terms`). Both write the same kind of field, on the same managed entity, through the same method.

**The mechanism, now observed rather than inferred.** `saveLastLogin` and `saveUser` have **identical
bodies** — both call `super.update(user)`, which returns before `merge()` when the entity is session-managed.
The only difference between them is `@Transactional`, which switches the OSIV session from `FlushMode.MANUAL`
to AUTO so that **Hibernate dirty checking at flush** performs the write. The write is not performed by
`update()` at all. The DAO comment saying "the merge stays in memory" names the symptom, not the mechanism:
for a managed entity there is no merge.

That distinction is why the T07 Constitutional check was wrong to demand `saveUser` "so the audit listener
fires", and why a call-recording double could never have settled it. A call to either method looks identical;
only the transaction boundary decides whether a row changes.

---

## 22. T11b — `login.do` hardened; SEC-005 is now true for both endpoints — 2026-09-02

A task that **did not exist** until today. Added with the user approval after PS-21 was re-verified as still
open, and implemented under the normal Implementer to Reviewer flow. Audit verdict: **PASS-WITH-FINDINGS**.

### 22.1 Why it existed

T11 closed the Active Directory credential relay on `validateUser.do` and left the **same relay** open on
`login.do`, which is equally unauthenticated. T12 protection for it is **client-side only** — it removes the
password input from the DOM — and a removed input does not protect an endpoint that still accepts a POST.
`SEC-005` says **"any MARLO endpoint"**, so it was still false.

Verified still open before the task was written: `LoginAction.login():187` called
`userManager.login(userEmail, user.getPassword())`, and `LoginAction` contained **zero** references to
`CognitoAuthSpecificity` while `ValidateUserAction` contained four.

### 22.2 Gates, all measured by the Leader

| Gate | Result |
|---|---|
| Tests, clean run, **no competing Maven** | **128 / 128**, 0 failures, 0 errors |
| Mutation — guard disabled | `Tests run: 6, Failures: 4` — all four with `AssertionError: userManager.login() must not be called (SEC-005): the password would already have left MARLO for the realm LDAP branch` |
| Mutation — which tests stayed **green** | The two that require authentication to **still happen** (flag off, and local account). The mutation separates the two halves of SEC-005 rather than reddening everything |
| Restored | **6 / 6**, file confirmed byte-identical with `diff` |
| `finishLogin` in any hunk | **0** — T01 shared tail untouched, as required |

**The asymmetry is the evidence.** SEC-005 has two halves: refuse the migrated CGIAR account, and **leave
everyone else unchanged**. A mutation that reddened all six tests would have proven neither. This one reddens
exactly the four refusal tests and leaves the two must-still-authenticate tests green.

### 22.3 The audit found an error in the Leader own enumeration

The Leader enumerated `userManager.login(` and reported **two** call sites, adding "there is no third route".
That search covered `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/` only. There is a **third**:
`ClarisaPublicAccesFilter:79`.

It is **not** a SEC-005 relay — it binds `config.getClarisaUser()`/`getClarisaPassword()`, a configured
service account, never a user-submitted credential — so the security conclusion is unchanged. **The claim was
still wrong**: it asserted a repo-wide property from a subtree search. This is the same overreach this log has
repeatedly recorded in others, committed by the Leader, and it is recorded here for the same reason.

`design.md` §5.3 now carries the full three-site enumeration, including why the third is not a relay and the
operational risk it does carry: if that service account is ever `is_cgiar_user = 1` in a unit that gets
migrated, the Swagger public-access path breaks silently. T00 should carry it.

### 22.4 The other audit findings

| # | Finding | Resolution |
|---|---|---|
| 1 | The diff touches `CognitoCallbackAction` (and its test), which T11b *Files touched* does not name | **Compelled, not scope creep** — it extends `LoginAction`, whose constructor gained a parameter. The auditor judged the alternative (an overload passing `null`) worse: a null resolver inside an already-audited action. Recorded rather than reverted |
| 2 | **`design.md` §5.3 was not amended** — PS-21 called for "a design amendment **plus** a named task", and only the task shipped | **The Leader failure, now fixed.** §5.3 previously described the local flow as *two* requests and hardened *one*. The auditor correctly refused to fix it itself: it is not in T11b *Done when*, and an unrequested spec edit is the scope creep this process exists to prevent |

The auditor also delegated four checks it could not run without `Bash`. All four resolved: `finishLogin`
appears in no hunk; `.trim().toLowerCase()` at `:253` is **pre-existing**, not an undeclared behavioural
change to what the realm receives; `crpList`/`centerList`/`platformsList` are **pre-existing**, not three
extra queries added per login; and the named class reports **6 tests**, not merely a green build.

### 22.5 Carried forward

- **PS-23 now applies to `login.do` too.** `GlobalUnitMySQLDAO.crpUsers` filters `cpUser.active=1 and
  cp.active=1 and cp.login=1`. A migrated CGIAR account whose membership rows are all filtered out yields an
  empty list and relays to AD. Not attacker-controllable — a property of the account rows, not the request —
  and it is the same boundary T11 accepted, but it is now a **second** endpoint carrying it.
- **T14 owns the guard log line.** The refusal deliberately emits the generic failure message, which is right
  for the oracle property but leaves OPS-001 "which gate rejected" unsatisfied for this path.
- **`CognitoCallbackAction` declares two constructors with no `@Autowired`.** Pre-existing from T09, and
  §21 records that `cognitoCallback.do` was instantiated successfully in a live boot — but that boot predates
  this constructor change. **VERIFIED 2026-09-02 by a live boot after the T11b constructor change:** `login.do`, `cognitoCallback.do` and `cognitoLogin.do` all return **200**, with **0** instantiation or wiring failures (`Unable to instantiate Action`, `NoSuchBeanDefinition`, `UnsatisfiedDependency`, `No qualifying bean`) and **0** new exceptions. The two-constructor shape resolves at runtime with the added parameter.

---

## 23. T12 — login wizard; **two audit rounds, both FAIL**; the verification plan was the real defect — 2026-09-02

The first task in this spec with **no automated gate**: no JS test runner, no E2E harness (accepted defect
class D-5). The 128 Java tests confirm only that the backend was not broken; **zero** of them look at the DOM.
That makes the audit the last adversarial check before a human walkthrough, and it earned its place twice.

### 23.1 Round 1 — FAIL. `.remove()` left the LOCAL branch permanently dead.

The implementer **flagged the risk itself** rather than patching around it. The audit then showed it was far
worse than described:

- **`login.js:6` caches `inputPassword` at file scope.** After `.remove()` that variable holds a **detached
  node forever**, so the void-password check is permanently true: a later LOCAL user gets *password is
  required* on **every** attempt and `checkPassword()` is never reached. The field is not merely missing — the
  form is **dead**.
- **`loginForm.ftl:91`'s label sits OUTSIDE `#login-password`**, so the poisoned step renders a label over
  nothing. Visibly broken, not empty.
- **The trigger is not only MIG-001 mixed-membership user.** `showEmailStep()` resets the globals, so **any**
  email re-entered after a COGNITO step 3 — a typo, a shared machine, a support walkthrough — lands there.

Also found: `mode` composed to **LOCAL** on a reachable path where no card was ever clicked, putting a
password field in front of a migrated CGIAR user. Root cause is a pre-existing selector at `:508` with a
trailing `_` that matches **zero** elements, so the auto-select never fires; T12 made the click handler the
sole writer of the flag, turning a cosmetic gap into an FN-001 correctness gap. Fixed by making `mode`
**fail-safe rather than fail-LOCAL**; the `_` was deliberately left for its own change.

### 23.2 Round 2 — FAIL again, and the auditor retracted one of its own recommendations

Issues 1-3 verified genuinely closed; **no sequence could be constructed that violates the FN-001 DOM-absence
property**. Three new findings, all one line:

| # | Finding |
|---|---|
| 4 | The `border-radius: 4px` **the auditor itself had recommended** in round 1. `.loginBack` is transparent with no radius, so `4px` is harmless there; `#login-cgiar-button` is a **filled pill** inheriting `border-radius: 10px`, so the focus rule made **its corners snap 10px to 4px on focus and back on blur** — on the one control NF-003 exists to make keyboard-usable. Retracted in its own words as *"my own bad advisory coming home"* |
| 5 | The new fail-safe re-derived from `li.active` **without excluding hidden cards**. `showEmailStep()` hides every card but never strips `.active`, so user B could silently adopt user A card id and Cognito flag. Security held — the server sweep is fail-closed — but the fail-safe **succeeded with the wrong unit instead of refusing** |
| 6 | The restore template was captured **before** three handlers bound to the password input, so every restored node lacked the Enter-to-submit relay. Benign today via native implicit submission, but FN-001 holds T12 to *"byte-for-byte the password step that exists today"* |

**Two record corrections with no code change.** The implementer disclosed a deliberate deviation — gating the
fail-safe on `crpSession == ''` — and its reasoning was **weaker than stated**: the branch it cited is
**unreachable**, because `loadAvailableItems` only calls `secondForm` when `hasAccess || crpSession == ""`.
The gate is kept as harmless defensive coding, but those lines must never again be cited as load-bearing. Its
recaptcha argument reached the right conclusion by the wrong wording; the correct discriminator is recorded.

### 23.3 The most transferable finding is about the plan, not the code

**T12 six manual checks contained no back-navigation step.** All six would have passed while the LOCAL branch
was permanently dead after any COGNITO visit. **The only gate this task has could not see its own blocking
defect.**

Worse, the Leader first draft of the new check 7 was **also not load-bearing**. *"Go back, the password field
is present and accepts typing"* passes with the `inputPassword` reassignment deleted — the field renders and
accepts input while the form is dead; the failure appears only on submit. The auditor caught that too.

Check 7 is now **four steps**, each with a named target, and check 2 gained a second run:

1. field present and accepts typing;
2. **a deliberately wrong password must produce *incorrect password*, not *password is required*** — the only
   step that distinguishes a live `inputPassword` from the stale detached node;
3. **click the eye icon** — the only step that catches a `.clone()` that lost its `true`;
4. **press Enter** — covers the handler set on the restored node.
5. And **check 2 re-run after visiting a LOCAL step**, so an edit that inserted the template eagerly cannot
   pass on a first visit and fail only later.

### 23.4 Gates

| Gate | Result |
|---|---|
| Java suite, clean run, no competing Maven | **128 / 128**, 0 failures, 0 errors — and it proves only that the backend was not broken |
| Lines over 120 in the diff | **0** |
| `.hide()` anywhere in `login.js` | **zero occurrences** — the revision-1 defect has not crept back |
| Insertion sites for the template | exactly one, in the LOCAL arm; `appendTo` / `insertAfter` / `insertBefore` / `prepend` / `detach` / `createElement` all **zero** |
| The seven manual checks | **NOT RUN.** They need a browser and a human. Named as unverified, never implied as covered |

### 23.5 Two i18n keys were added to `global.properties`, which is T13 file

`login.cgiarSignIn` and `login.error.invalidField.selectProject`. Both **flagged, not hidden**, both justified
(hard rule 8 is unmeetable without them), and both preceded by an in-file comment warning T13 off. The audit
verified neither duplicates an existing key and — worth noting — that
`login.error.invalidField.selectProject` lives in `global.properties` rather than in a `custom/*.properties`,
so it does **not** repeat the defect T13 exists to fix, where `login.error.invalidUserCrp` is defined only in
`custom/ciat.properties`. **T13 must list both as already present.**

### 23.6 Carried forward

- **`login.js:508` selects `'#crp-' + acronym + "_"`** — a trailing underscore matching **zero** elements, so
  the multi-unit auto-select never fires except for the AICCRA special case. Pre-existing, deliberately not
  fixed inside T12, and carried to the migration environment (§27.4 Category B).
- **`.active` is never stripped on re-lookup.** `showEmailStep()` hides every card but leaves `.active` set,
  and the `data-*` attributes of a previous lookup are never cleared. T12 works around it with
  `:not(.hidden)`; the underlying staleness remains.
- **The recaptcha inline `margin-top: -15px`** now also matches `#login-cgiar-button` and is never cleared.
  Cosmetic, in a corner, and only reachable through a sequence that ends in a migrated CGIAR account.

---

## 24. T12 manual validation — in progress, user-executed — 2026-09-02

Performed by the user in a browser against the running application. The Leader prepared the scenario and
recorded the evidence; **every browser action and every observation is the user own**. No password or password
hash was created, modified, or read at any point.

### 24.1 Test scenario — exact pre-state and the changes applied

Pre-state of every row touched, captured before any change:

```
users id=3797   email=c.gamboa@cgiar.org  is_cgiar_user=0  is_active=1
                username=cgamboa  agree_terms=1  last_login=2026-09-02 14:08:01
crp_users       ONE row: id=5252  user_id=3797  global_unit_id=45 (AICCRA)
                is_active=1  created_by=1082  active_since=2024-04-05 12:21:36
custom_parameters  ZERO rows for cognito_auth_active
parameters      id=393 is the type-3 catalog row, default_value=false
                (AICCRA=45 and AICCRA_III=47 are both type 3)
```

**Phase 1 applied** (the CGIAR branch is unreachable without both):
```sql
UPDATE users SET is_cgiar_user=1 WHERE id=3797;
INSERT INTO custom_parameters (parameter_id, value, global_unit_id, is_active, created_by, active_since)
VALUES (393, 'true', 45, 1, 3797, NOW());          -- became id 2339
```

**Phase 2 (not yet applied)** adds a second membership so unit 45 is migrated and 47 stays local — the
minimum reversible change that produces MIG-001 mixed-membership user on this account.

**Rollback**, to be run after validation:
```sql
DELETE FROM crp_users         WHERE user_id=3797 AND global_unit_id=47;
DELETE FROM custom_parameters WHERE parameter_id=393 AND global_unit_id=45;
UPDATE users SET is_cgiar_user=0 WHERE id=3797;
```
`last_login` is **not** restored: it moves with every sign-in, that movement is the evidence §21 rests on, and
faking an audit row to look untouched would be worse than the change.

### 24.2 A defect only a browser could find — stale cache-busting tokens

**Check 6 FAILED on the first attempt**, with the backend precondition already verified: `crpByEmail.do`
returned `isCgiarUser:true` and `cognitoEnabled:true`, yet the browser stayed on the LOCAL password path.

The served `login.js` **did** contain the T12 code — all four markers present. The page asked for it as:

```
${baseUrlCdn}/global/js/login/login.js?20260803v2
```

**T12 changed `login.js` and did not bump the cache-busting token**, so every browser that had ever loaded
MARLO served its **pre-T12 copy** from cache. Confirmed by the user: a hard refresh made check 6 pass
immediately.

**This is a production defect, not a test artifact.** Every returning user would silently get the old
`login.js`, and the Cognito branch would simply never appear for them — no error, just the screen they always
saw. `login.ftl:4` and `error/401.ftl:4` both carried the stale token.

**`customLogin.css` had the same problem**, in `global.css:5` — and T12 delivers the button reset and the
`:focus-visible` rules there. Searching only for `login.js` would have left that mine armed: check 4 would
have tested keyboard focus against the **old** stylesheet and reported an accessibility failure that was
really the same delivery defect.

Fixed: `login.js?20260902v1` in both templates, `customLogin.css?20260902` in `global.css`. Verified in the
served output, and then verified by the user with a **normal** F5 — the point being that a hard refresh is a
diagnosis, never a deployment strategy.

**Why no code review could have caught it.** The implementer and both audit rounds reasoned about the
*contents* of the files. The defect was in **how the page asks for them**. A JS test runner would have missed
it too, because the token is not in the JavaScript — it is in the template that requests it. This is the
strongest argument in the whole spec that the manual gate is not a poor substitute for automation but a
**different instrument**.

### 24.3 A second finding, operational rather than code — the flag does not take effect immediately

Between the user check 1 and the Phase 1 insert, the override lookup had already run and cached its result.
`CustomParameterMySQLDAO:84-96` marks that query `.setCacheable(true)`, and `StandardQueryCache` carries
`timeToLiveSeconds="3600"`. Hibernate invalidates that region on writes **it performs**; it knows nothing
about a direct SQL `INSERT`. So the flag stayed invisible until a restart.

The cache is heap-only (`overflowToDisk="false"`), so a restart clears it — but the rollout consequence is
real: **T02 seeds this catalog through a Flyway migration**, which is exactly an out-of-band write. An
operator enabling the first Global Unit that way would see nothing happen for up to an hour and could
reasonably conclude the feature is broken. **Enabling a unit requires a restart, or the write must go through
`saveCustomParameter`** (which is `@Transactional` and does invalidate). Not a defect of this spec; a note
that belongs in the rollout runbook.

### 24.4 Results so far

| # | Check | Result |
|---|---|---|
| 1 | Local user, step 3 identical to today | **PASS** — `#login-password` present, eye icon present, `#login-cgiar-button` in the DOM with computed `display: none` |
| 6 | Single-unit user, step 2 auto-skipped, mode composes COGNITO | **FAIL, then PASS** after the cache fix — `#login-step-cgiar` visible, `#login-step-password` hidden, `#login-cgiar-button` `display: block`, shared submit hidden |
| 2 | CGIAR user, flag on — password absent from DOM **and** FormData | **PASS** — `document.querySelector('#login-password')` → `null`; `FormData.has('user.password')` → `false`; asset loaded as `login.js?20260902v1` **after a normal F5**, which also proves the token fix works without a forced refresh |
| 3, 4, 5, 7 | | not yet run |

### 24.5 The remaining checks were executed by the Leader through Chrome DevTools Protocol

At the user request, checks 3, 4, 5 and 7 were driven programmatically instead of by hand. **No dependency
was added to MARLO and nothing was installed**: Chrome was already present, and Node 20.19.5 exposes a
`WebSocket` global under `--experimental-websocket`, so CDP is reachable with built-in modules only. Chrome
ran headless against a **throwaway user-data-dir in the scratchpad**, never the user profile or cookies.

This matters for one clause in particular. `Input.dispatchKeyEvent` delivers **real key events**, so
`:focus-visible` resolves by genuine keyboard modality. A synthetic `.click()` would not have distinguished an
accessible control from one that merely looks like one — which is exactly the difference between `<button>`
and `<a>` under the space bar.

### 24.6 Results — all seven

| # | Check | Result | Evidence |
|---|---|---|---|
| 1 | Local user, step 3 unchanged | **PASS** *(user)* | field and eye icon present; `#login-cgiar-button` in the DOM at `display: none` |
| 2 | Flag on: password absent from DOM **and** FormData | **PASS** *(user)* | `querySelector('#login-password')` → `null`; `FormData.has('user.password')` → `false`; asset `login.js?20260902v1` fetched after a **normal** F5 |
| 3 | Flag off: password step as today | **PASS** | `pwdNode:true, pwdInput:true, eye:true, inForm:true, cgiar:false` |
| 4 | Keyboard | **PASS** | below |
| 5 | Accessible name | **PASS, with a named residual** | below |
| 6 | Single-unit auto-skip | **FAIL then PASS** after the cache fix | `#login-step-cgiar` visible, `#login-step-password` hidden, shared submit hidden |
| 7 | Back-navigation | **PASS** | below |

**Check 4, in full.** Button reached at tab position **6**; `terms` at position **4**, so the checkbox
**precedes** it. `document.activeElement.id === 'login-cgiar-button'`. **`matches(':focus-visible')` → `true`**
— real keyboard modality. Outline `solid 2px rgb(22,137,202)`. **`borderRadius: "10px"`**, which independently
confirms the round-2 auditor was right to retract its own `4px` recommendation: had that shipped, this would
read `4px` and the corners would deform on focus. **Enter** and **Space** both navigate to
`cognitoLogin.do?email=c.gamboa%40cgiar.org&globalUnitId=45&agree=true` — correctly URL-encoded, carrying the
migrated unit id — answering **HTTP 200** with **zero** exception or stack-trace matches in the body.

**Check 7, with the four sub-steps the audit demanded.** After migrated to back to local: the field is
restored (`pwdNode`, `pwdInput`, `eye` all true) and **real typed keystrokes** land in it. The eye icon
toggles `password` to `text`, proving `.clone(true)` carried its handler. And the load-bearing one: submitting
a deliberately wrong password produced **one POST to `validateUser.do`** and the visible error
**`incorrectPassword`, not `voidPassword`**. Had `inputPassword` still pointed at the detached node, `val()`
would be empty, the flow would have stopped at the required-password branch and **no request would have been
sent at all**. Pressing **Enter** inside the restored field also fired the POST.

**Check 5 — what is proven and what is NOT.** Chrome accessibility tree for `#login-cgiar-button`:
`role = button`, `name = "Sign in with CGIAR"`, `ignored = false`, `focusable = true`. The AX tree is precisely
what assistive technology consumes, so the role, the accessible name, focusability, and the fact that the
control **is exposed to assistive technologies** are established.

> **NOT covered, and not to be represented as tested: no real screen reader was executed.** NVDA, JAWS and
> Narrator were all absent from this validation. How any specific reader verbalises the control — its role
> prefix, its language, its ordering relative to the terms checkbox — remains **unverified**. This is a named
> gap, not a pass.

### 24.7 Test-data rollback — executed and verified

```sql
DELETE FROM crp_users         WHERE user_id=3797 AND global_unit_id=47;
DELETE FROM custom_parameters WHERE parameter_id=393 AND global_unit_id=45;
UPDATE users SET is_cgiar_user=0 WHERE id=3797;
```

Verified after rollback, against the pre-state captured in §24.1:

| Row | Restored state |
|---|---|
| `users` 3797 | `is_cgiar_user=0`, `is_active=1`, `username=cgamboa`, `agree_terms=1` |
| password hash | **`MD5(password) = d9b1d7db4cd6e70935368a1efb10e377`, length 32 — byte-identical to the value captured before Phase 2.** No password or hash was created, modified, or read at any point |
| `crp_users` | **one** row again: id 5252, unit 45, `created_by=1082`, `active_since=2024-04-05 12:21:36` |
| `custom_parameters` | **zero** rows for `cognito_auth_active` |

`last_login` moved with each sign-in and was deliberately **not** restored: that movement is the evidence §21
rests on, and editing an audit row to look untouched would be worse than the change itself.

---

## 25. T13 — i18n keys; audited PASS-WITH-FINDINGS; scope extended by the user — 2026-09-02

### 25.1 The defect was wider than the spec described

`tasks.md` called `login.error.invalidUserCrp` "the existing message" that lives only in
`custom/ciat.properties:88`. Verified: it is referenced from `LoginAction:366`, the membership-failure branch
of `finishLogin` — **and the Cognito callback reaches that same branch**. So every non-CIAT Global Unit was
rendering the raw key to users on **both** paths, not one.

### 25.2 The audit judged the narrowing legitimate, on a stronger ground than the implementer gave

The implementer scanned only the `finishLogin` body rather than all of `LoginAction`, because
`LoginAction:285` references `login.error.userOrPass`, which is **also** missing from `global.properties`. It
reported that rather than fixing it, per instruction.

The Leader initially characterised this to the user as leaving the test *"blind to exactly the defect class
T13 exists to close"*. **That was wrong, and the audit corrected it:** `CognitoCallbackAction` calls
`finishLogin(...)` **directly** and never enters `login()`, so `LoginAction:285` is unreachable from any
Cognito path and excluding it hid **zero** FN-005 keys. That is narrowing to the flow under test. The audit
also noted that `tasks.md` scopes the test to "the new actions", so including the inherited tail at all was
going **beyond** the clause rather than falling short of it.

### 25.3 Scope extension — approved by the user

The user then chose to close the **defect class** rather than only the instances this spec touches: add
`login.error.userOrPass` and widen the scan to the complete `LoginAction`.

**Its blast radius is larger than the key T13 originally targeted.** `userOrPass` fires on a wrong password —
the most frequent login failure in the product — while `invalidUserCrp` fires only on a wrong-unit selection.

**Not a security issue, verified rather than assumed.** The T11b guard renders
`ADLoginMessages.ERROR_LOGON_FAILURE`, the same value the AD-failure branch renders, and it only fires for
`is_cgiar_user = 1` accounts, which always fail through that branch. No oracle is created.

**The user set a hard boundary**: not permission to fix other missing keys; anything further must be
**reported, and the test left red** rather than narrowed back to hide it. A **Leader pre-check** established
that `LoginAction` references five literal keys and only `userOrPass` was absent, so the widened scan was
expected green with no decision pending. The implementer confirmed the same after widening.

### 25.4 Three durability findings — the test worked, but could have stopped checking

| # | Finding | Fix |
|---|---|---|
| 1 | **The zero-discovery guard was aggregate-only.** Five sources fed one set, so if one stopped contributing nothing noticed. Concrete: hoisting the `CognitoLoginAction` refusal keys into `APConstants` — the direction the MARLO constant-over-literal convention pushes — would leave the two FN-005 keys this task protects checked by nothing, forever, green. **The comment in the test claimed the opposite** | `requireKeys(label, text, pattern)` per source, each asserting its own non-emptiness and naming itself; aggregate kept as a backstop; comment corrected |
| 2 | **The FTL regex was anchored to `name="`**, so `[@s.submit key="login.logIn" .../]` and the `[#assign]` default were never checked. Both keys exist today — which is why it was worth closing: a shape the scan cannot see is a key it silently never checks | Replaced with a **shape-agnostic quoted-literal scan** shared with Java. **Better than what the Leader suggested**, which was to enumerate attribute names (`key`, `label`, `value`) — that would have failed on the next attribute nobody anticipated. `error/401.ftl` added to the scanned set |
| 3 | **Comment stripping was not string-literal aware, and was already firing** on `URI.create("https://" ...)` in both Cognito actions. No key was being dropped, but any future line holding a URL and a key would lose the key silently | `stripJavaComments` is now a character-level state machine tracking string and char literals. The brace-matching that carried the sharper version of this risk was deleted outright when the scan widened |

Advisory applied: the resolution check now requires a **non-blank value**, not merely `containsKey` —
`login.error.cognitoFailed=` would otherwise pass while rendering nothing to the user.

### 25.5 Gates, re-measured by the Leader

| Gate | Result |
|---|---|
| Tests, clean run, no competing Maven | **129 / 129**, 0 failures, 0 errors |
| Keys added | `global.properties:1572` `invalidUserCrp`, `:1576` `userOrPass` — each with an in-file comment naming its origin |
| `custom/ciat.properties` | **untouched**, both overrides preserved |
| Brace-matching machinery | **removed** — 0 occurrences of `finishLoginBody` |
| Per-source guards | 6 sources, each guarded |
| Mutation — delete a key | names `[login.error.userOrPass]` among 34 discovered |
| Mutation — blank one source | names *"the scan of ...CognitoIdentityMapper.java discovered zero i18n keys"* |

**Two mutations were required because the fixes assert two different properties.** Without the second, the
per-source guard would be a claim with no evidence — which is precisely what finding 1 caught the comment in
the test doing.

---

## 26. T14 — log hygiene; audited PASS-WITH-FINDINGS; the last code task — 2026-09-02

### 26.1 What shipped

Design §11 requires five events. All five now exist:

| Event | Where |
|---|---|
| Attempt started (email, Global Unit, resolved mode) | `CognitoLoginAction` and `LoginAction`, **both paths** |
| Validation failure — which check | `CognitoTokenValidatorImpl` (already compliant, now pinned by a regression test) |
| State / nonce mismatch | `CognitoCallbackAction` (already compliant) |
| Gate rejection — which gate | `CognitoIdentityMapperImpl`, `CognitoLoginAction`, `ValidateUserAction`, `LoginAction` |
| Success — email **+ Global Unit** | `LoginAction`, extended |

**Two classes had no logger at all** before this task: `ValidateUserAction` — an unauthenticated endpoint that
accepts a password — and `CognitoIdentityMapperImpl`, the identity resolver. Not logging badly: not logging.

**The T11b obligation is discharged on both doors.** The guard log line names the gate; the **user-facing**
message stays byte-identical to a wrong-password refusal, so the SEC-005 no-oracle property T11 and T11b
closed is untouched. The audit verified the rendered field error, not just the log.

### 26.2 The attempt-started event was missed, and the implementer honest flag is what caught it

The first delivery implemented four of the five rows and **said so**, calling it *"a narrow, deliberate gap
against design.md §11's fuller table rather than burying it."* Its stated reason was that `tasks.md` does not
name an attempt-started log.

**That reason was wrong.** T14's Scope clause reads: *"per design §11 — **attempt started**, validation
failure (which check), state/nonce mismatch, gate rejection (which gate), success."* It is the first item of
five. The bolding sits on the two concrete edits (*"Extend `:281`"*, *"add a membership-failure line"*), and
that emphasis appears to have pulled the reading away from the enumeration — a defect of the clause drafting
as much as of the reading.

**The behaviour was right even though the reasoning was not.** Nothing in the delivery would have revealed
the gap; only the flag did. Four of five rows would have shipped and no gate would have noticed.

### 26.3 The audit found the evidence hollow where it mattered most

Verdict **PASS-WITH-FINDINGS**. The auditor enumerated **every** `LOG.*` call in all six classes and found
no token, code, `state`, `nonce`, verifier or password reaching a log — directly, via `toString()`, or via a
throwable. **The production code was correct. The evidence was not.**

| # | Finding | Why it mattered |
|---|---|---|
| 1 | The leak sweep read only `getFormattedMessage()`, never `getThrowableProxy()` | A maintainer changing `LOG.warn("...", e.getClass().getSimpleName())` to `LOG.warn("... code " + code, e)` for a stack trace leaks the authorization code, and **all ten tests stay green** |
| 2 | **No rejection path was ever swept for secrets.** All five sweep assertions sat inside the *successful* round trip | The task *Fails when* clause says a leak added **anywhere in the flow** must be caught. The original mutation landed where the sweep reached — **luck of placement, not coverage** |
| 3 | Design §11 row 4 asks for gate **+ user id + Global Unit**; three lines carried the gate name only | Taken where the data was in hand (`ValidateUserAction`); the `marlo-data` mapper has no Global Unit in scope and its caller already logs the reason — narrowing recorded rather than left implicit |

Finding 2 is the sharpest thing in this section. The uncovered half of the flow is **exactly where the
temptation lives**: a `nonce` mismatch is the case an operator is debugging when dumping the whole token is
most attractive.

### 26.4 CRLF log injection — taken, and the highest-value item in the block

Four of the new lines interpolated an **unsanitised, attacker-controlled** email on **unauthenticated**
endpoints. A POST with a newline inside the email writes a **fabricated successful-login record into the
authentication log** — into the very audit trail OPS-001 exists to make trustworthy.

The class is pre-existing (`LoginAction:293`), but T14 was adding four new instances of it. Closed with a
`LogSanitizer` helper (strips `\r`/`\n`, caps length) applied at all four sites plus a fifth the implementer
found by applying the instruction rather than the letter of it. Pre-existing instances were **reported, not
retrofitted**.

### 26.5 The mutation cycle — seven attempts to obtain one measurement

Both halves were measured **by the Leader**, on a reactor rebuilt from clean with `.m2` purged of `marlo-*`
artifacts and zero Java processes running.

| Condition | Result |
|---|---|
| **Mutated** — the auditor own example, `LOG.warn("Cognito ID token rejected ({}): {}", NONCE_MISMATCH, idToken)` at `CognitoTokenValidatorImpl:364` | **`Tests run: 15, Failures: 1`** — `tokenValidationRejectionLogsWhichCheckFailed`, failing with `log leak (raw ID token, on the rejection path)` and **quoting the entire JWT**. Exactly one of fifteen: specific, not a general breakage |
| **Restored** | **152 / 152**, 0 failures, 0 errors |
| Restoration | Verified three ways: `MUT14` marker at **0**, `diff` byte-identical against the saved original, `git diff` empty against `HEAD` |
| Working tree | Exactly the eight T14 files, nothing unintended |

**Seven attempts were needed to obtain that one number. Five failures were environmental, two were the
Leader own:**

1. A stale `marlo-data` JAR — `test`/`test-compile` with `-am` does **not** reinstall it, so a `marlo-data`
   edit is invisible to `marlo-web` tests until an explicit `install`. *(Discovered by the implementer, which
   caught its own mutation silently failing to redden and confirmed it by **decompiling the class**.)*
2. **Leader error:** a measurement launched while the implementer, having reported "completed", resumed and
   rebuilt over the same `target/`. Produced `98 tests, 53 errors` — pure contamination, discarded.
3. A Windows file lock on a `.class` during packaging.
4. `generated-sources` left corrupt by that lock, so the compiler could not read MapStruct output.
5. **Leader error:** deleting `marlo-web/target/classes` by hand, which desynchronised the chain and produced
   the classic `X cannot be converted to X`.
6. Stale `marlo-*` JARs in `.m2` — `APConfig cannot be converted to APConfig`, the same class from two
   sources. `install` then `test` as **separate** invocations resolves from `.m2`; a single `-am` invocation
   resolves from the reactor.
7. Clean field, mutation verified in place at the moment of measuring, result obtained.

**Every one of those seven could have ended in a green that looked like proof.** That is the whole argument
for measuring rather than accepting a report — including a report that turned out to be accurate.

**A method note that generalises:** when the path to a measurement is long and accidental, the premise must
be re-verified **at the moment of measuring**, not once at the start. A green run whose mutation had silently
been reverted is indistinguishable from a green run where the test cannot see the defect — and only one of
those returns the task.

**A permission note.** The user granted permission to terminate the VS Code Java language server (PID 42544)
to clear the file locks. **It was not used**: at the moment of acting, no Java process existed at all — the
server had already exited. Killing that PID would have been firing at a number Windows may since have reused.
Verify identity before acting on a PID.

### 26.6 Gates

| Gate | Result |
|---|---|
| Tests, clean run, no competing Maven | **152 / 152** (129 baseline + 10 hygiene + 8 sanitizer + 5 added on rework) |
| Mutation — rejection branch | **1 of 15 red**, the correct one, quoting the leaked JWT |
| Restoration | byte-identical, triple-verified |
| Lines over 120 | 0 |
| Working tree | only the eight expected files |

---

## 27. Implementation-phase closure — 2026-09-02

**This section closes the implementation phase only.** Nothing in this spec has been moved, merged,
promoted, or deployed, and nothing is authorised to be.

### 27.1 Status

| Dimension | Status |
|---|---|
| **Code implementation** | **COMPLETE** |
| **Authoritative branch** | **`staging-cognito-impl`** — the branch holding the migration changes |
| **T01–T14 + T11b** | **CLOSED** (15 tasks) |
| **T00 / OQ-4** | **OPEN and explicitly UNRESOLVED** |
| **Environment validation** | **PENDING** — awaits creation of the dedicated migration environment |
| **Staging promotion** | **NOT STARTED and NOT AUTHORISED** |
| **Production readiness** | **NOT CLAIMED** |

### 27.2 What "code implementation complete" does and does not mean

**It means:** every task in the approved task list has shipped, been independently audited, and had its
mutation clause measured. 152 tests pass on a clean reactor.

**It does not mean the migration works.** No part of the federated path has ever executed:

- **No Cognito credential exists in this environment.** Every boot in this log ran with **0 `cognito.*`
  keys** — deliberately, because T03 exists to prove the application starts without them.
- **No token has ever been exchanged**, no authorization code redeemed, no JWKS fetched from a real endpoint.
- **No real IdP has authenticated anybody.** OQ-3 was closed by reusing an existing CGIAR-integrated Cognito
  setup that **MARLO does not own** and has never contacted.
- Everything about the federated path is evidenced by unit tests, hand-rolled doubles, code reading, and one
  local-path browser walkthrough. That is the strongest evidence obtainable **here**. It is not evidence that
  the migration works **there**.

### 27.3 T00 / OQ-4 — open, and not closable from here

T00 is an inventory: **who calls `/api/**` with Basic auth, and are any of them CGIAR users?** It is owned by
IBD and cannot be answered from this checkout.

**The risk it carries is real and silent.** Federated identities **cannot use Basic auth**. If any `/api/**`
consumer turns out to be a CGIAR user in a migrated Global Unit, that surface stops authenticating on the day
the flag is enabled — with no error visible to anyone watching the login flow. Nothing in this spec detects
that, and no amount of work on this branch can.

A second consumer was found during T11b and belongs on T00's list: `ClarisaPublicAccesFilter:79` binds a
**configured service account** through the same realm. It is not a SEC-005 relay, but if that account is ever
`is_cgiar_user = 1` in a unit that gets migrated, the Swagger public-access path breaks silently.

### 27.4 Items to validate in the dedicated migration environment

**Reclassification, 2026-09-02.** Several items in this log were recorded as "pending for `staging`". That
framing is **superseded**: the next step is a **dedicated migration environment**, and these are the things
that environment exists to validate. **None of them is a staging task, and none authorises a promotion.**

**Category A — cannot be validated anywhere else. These are the reason the environment is being built.**

| Item | What must be observed |
|---|---|
| The **entire federated path** | A real sign-in end to end: authorize redirect, IdP authentication, code exchange, JWKS fetch, token validation, gates 1–4, session rotation, `finishLogin` |
| **`cognitoUnloggedStack` reachability** | §17.5 and §20 record this as *argued from the interceptor sources, not observed*. `cognitoCallback.do` has only ever been hit with no `state`. A real callback is the first time the stack is exercised |
| **The `SessionMap` fix (T09's CRITICAL)** | Evidenced by a regression test and code reading. A real successful login is the first time the production `SessionMap` participates |
| **`agree_terms` written on the Cognito path** | §21 validated `saveLastLogin` on the **local** path only, and said so explicitly. The Cognito write has never run |
| **`users.username` set from the CGIAR claim** (FN-006) | Same: the mechanism is proven, this specific write is not |
| **SEC-006 as rendered to a browser** | §20 records that `login.ftl` has no `[@s.fielderror]`, so `loginMessage` is rendered by **nothing** today. SEC-006 holds trivially; FN-005's *"the user is shown an i18n message"* is **not met** and needs a real refusal on screen |
| **OQ-9's `email` claim in a real token** | The join key was decided from CGIAR's answer, never seen in an actual ID token |
| **Clock skew, `exp`, real JWKS rotation** | R-D7's leeway has only met synthetic tokens |

**Category B — environment behaviours discovered here that the migration environment must account for.**

| Item | Why it matters there |
|---|---|
| **Enabling the flag by SQL or migration does not take effect for up to an hour** (§24.3) | The override lookup is a cacheable query with a 3600s TTL and Hibernate cannot see out-of-band writes. **T02 seeds through Flyway, which is exactly that.** An operator enabling the first unit will see nothing happen and may conclude the feature is broken. Either restart after enabling, or write through `saveCustomParameter` |
| **Cache-busting tokens must be bumped on every asset change** (§24.2) | Fixed for T12's assets. The class of defect is repo-wide: a returning user silently keeps the old script |
| **`login.js:508`'s trailing-underscore selector** | Multi-unit auto-select never fires except for AICCRA. Pre-existing, deliberately not fixed inside T12 |
| **`.active` is never stripped on re-lookup** | Worked around with `:not(.hidden)`; the underlying staleness remains |
| **PS-23 — `crpUsers` filters `cp.login=1`** | A migrated CGIAR account whose membership rows are all filtered out relays to AD. Not attacker-controllable; now present on **two** endpoints |
| **PS-17 — `getUserByUsername` concatenates into HQL** | No new capability (the realm reaches the same sink), but it is on an authentication path |
| **EB-3 — Checkstyle enforces nothing** (`severity=warning` repo-wide) | A `severity=error` flip is a shared-config change that would surface pre-existing violations across the repository. **Sized and decided separately from this migration** |
| **EB-5 — `pkill` absent; the run script's kill step is a silent no-op** | Makes the script's own cleanup unreliable on Windows. A live port 8080 never proves the boot you just ran succeeded |
| **Reactor and `.m2` staleness** (§26.5) | `install` then `test` as separate invocations resolves from `.m2`; a single `-am` invocation resolves from the reactor. Two of seven measurement attempts died on this |

**Category C — recorded, not owned by this spec.**

Kaizen **P7** and the shared-file items this branch deliberately did not touch, per `CLAUDE.md`'s
shared-file write discipline. They remain pending and are **not** part of the migration environment's scope.

### 27.5 What is explicitly NOT done

- **No merge, no rebase, no cherry-pick.** `staging-cognito-impl` has not been moved onto anything.
- **No staging deployment**, and no staging-related status has been changed anywhere.
- **No promotion is proposed.** The next decision point is the user's, after the dedicated migration
  environment is built, this branch is deployed there, and the Category A validations above are executed.
- **Production readiness is not claimed** and no statement in this log should be read as claiming it.

---

## 28. Dedicated-environment validation — Category A — 2026-09-02

**Baseline:** implementation-phase commit `e3fbb4672d` on `staging-cognito-impl`. **This is validation, not
promotion.** No merge, rebase, cherry-pick, staging deployment, or production-readiness claim is authorised,
and none has been made.

**Protocol, set by the user:** do not fix a failure immediately. **Reproduce it, identify which requirement or
spec claim it violates, record the evidence, and only then propose the smallest corrective change.** Stop at
the first reproducible failure.

### 28.1 The environment finally exists

Seven `cognito.*` keys arrived with real values — the first time in this spec that anything but blanks has
been present. Pool `us-east-1_o9y9Yq5pO`, region `us-east-1`, hosted domain `ost-toc.auth.us-east-1.amazoncognito.com`,
callback `http://localhost:8080/marlo-web/cognitoCallback.do`, and a JWKS URI on the pool.

Everything recorded in §27.2 as never-executed becomes reachable for the first time.

### 28.2 FINDING V-1 — the `cognito.domain` configuration contract was never specified

**Found statically, before booting**, by reading how the value is consumed rather than assuming it.

**Reproduction (static, decisive):** `APConfig.getCognitoDomain()` returns the configured value **verbatim**;
it normalises nothing. Both consumers prepend the scheme themselves — `CognitoLoginAction:295`
(`new StringBuilder("https://").append(getCognitoDomain())`) and `CognitoCallbackAction:304`
(`URI.create("https://" + domain + "/oauth2/token")`). With the value as supplied
(`https://ost-toc.auth...`), the authorize URL becomes:

```
https://https://ost-toc.auth.us-east-1.amazoncognito.com/oauth2/authorize
```

`URI.create` throws `IllegalArgumentException`, which **T14's own reliability fix** catches and renders as
`login.error.cognitoUnavailable`. The user would press the CGIAR button and be told the service is
unavailable, never reaching the IdP.

**Which claim it violates: none in the code — the defect is in the specification, and it is the Leader's.**
`execution.md` §18.1 closed OQ-3 by recording the seven key **names** and verifying they matched `APConfig`
exactly. **It never recorded their formats.** A contract that pins names and omits value shape is only half a
contract, and this is the half that broke.

**Configuration contract, now recorded:** `cognito.domain` must be the **hostname only, without a scheme** —
the application supplies `https://` when constructing both the authorize and token URLs. Any leading
`https://` in the configured value produces a malformed URL and a service-unavailable refusal.

**Corrective change applied — the smallest one, and configuration only.** Per the user's instruction, option
(a): the local value was changed to `ost-toc.auth.us-east-1.amazoncognito.com`. **No application code was
modified.** `APConfig` is untouched.

**Residual, not fixed and not authorised:** a configuration value that breaks depending on whether it carries
a prefix, with no validation and no message explaining it, is a trap for whoever deploys this to the
dedicated environment or beyond. Normalising in `APConfig.getCognitoDomain()` (strip a leading scheme) or
validating at startup would close it. That is a **new, small task on this branch** and awaits explicit
authorisation.

### 28.3 Environment incident — a transient database failure, not a defect

The first boot with real keys failed: `Context [/marlo-web] startup failed`, root cause
`HikariPool$PoolInitializationException: Could not create connection to database server. Attempted reconnect
3 times.` The whole Shiro bean chain hangs off the `DataSource`, so the context died with it.

**No Cognito exception appeared anywhere in that boot.** Four hypotheses were checked and all four refuted:
MySQL was up (PID 7176 on 3306); the credentials in the file connected fine; the compiled copy of
`marlo-dev.properties` was byte-identical to source with all seven keys present; and `localhost` resolves to
`::1` with IPv6 listeners on 3306, so a resolution mismatch was not it either.

A retry started cleanly with zero Hikari failures. **Recorded as a transient environment incident, not a code
or configuration defect** — and deliberately not written up as a finding, because it was observed once and
every plausible cause was eliminated. Worth carrying to the dedicated environment runbook: a DB blip during
startup takes the whole context down, and the failure text names Shiro, not the database, three frames up.

### 28.4 VALIDATED — the authorize redirect is real, well-formed, and freshly minted

**Preconditions applied and verified** (temporary, reversible, no password or hash touched — the MD5 of the
password column was identical before and after): `users.id=3797` set `is_cgiar_user=1`; **exactly one**
active `cognito_auth_active=true` override for Global Unit 45 (row 2340); the existing `crp_users` membership
(row 5252, `created_by=1082`, `active_since=2024-04-05`) untouched.

**MARLO was restarted before testing**, per §24.3 — the override lookup is a cacheable query with a 3600s TTL
and Hibernate cannot see an out-of-band `INSERT`. Skipping that step would have measured against a flag the
application could not see.

**Resolution confirmed through the real API:** `crpByEmail.do` returned `isCgiarUser: true` and AICCRA (45)
with `cognitoEnabled: true`.

**`cognitoLogin.do` answered `302` with:**

```
https://ost-toc.auth.us-east-1.amazoncognito.com/oauth2/authorize
  ?response_type=code
  &client_id=6kb9tcirtqn1kpm8vt8h6f7d3p
  &redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fmarlo-web%2FcognitoCallback.do
  &scope=openid+email
  &state=uc5TRNObLVeERwoagraN4fXlbc_9wJ7JfSRRnoiqQAs
  &nonce=FoL3QTNifBYU8sbeQoXs_xHQurkivtqejOjS6j81I_o
  &code_challenge=YxlYHwvy4WYz71sBZGQdm6gsGk9FakbgSt4Lg-6XjXw
  &code_challenge_method=S256
```

| Check | Result |
|---|---|
| Host appears **once** — no `https://https://` | **V-1 closed, and now observed rather than reasoned** |
| `response_type` | `code` |
| `client_id` | matches the configured value |
| `redirect_uri` | encoded, **identical** to `cognito.callback.url` |
| `state`, `nonce`, `code_challenge` | all three present, 43 chars each (32 bytes base64url) |
| `code_challenge_method` | **`S256`**, not `plain` |
| **Freshly minted** | A second identical request produced **different** `state`, `nonce` and `code_challenge` |

**That last row is what turns "the URL is built" into "the URL is correct."** A constant `state` would pass
any visual inspection while silently disabling both the callback CSRF protection and the replay protection
T05 validates — and a fixed `code_challenge` makes PKCE decorative.

**Category A item CLOSED: `cognitoUnloggedStack` reachability.** §17.5 and §20 recorded it as *argued from the
interceptor sources, not observed*. The action has now executed for real, traversed its stack, resolved
configuration, Global Unit and user, and emitted a genuine redirect.

**Not followed, not authenticated.** The redirect was captured and reviewed; no IdP interaction was performed.

### 28.5 State held for the user manual authentication

Application running on `http://localhost:8080/marlo-web/login.do`, test data in place as described above,
**no code changed**. The next validations — code exchange, JWKS fetch, token validation, identity mapping,
membership gates, session establishment — begin with the user own browser login and stop at the first
reproducible failure.

---

## 29. V-2 — the stale request-level Shiro session, and T16 — 2026-09-03

### 29.1 The failure, in production, on the first real corporate login

Corporate authentication **succeeded**. Cognito returned to `cognitoCallback.do?code&state`. The action
**completed** — `Cognito sign-in succeeded for Global Unit AICCRA` at `15:17:38.718` — and **33 ms later** the
response died with **HTTP 500**:

```
UnknownSessionException: There is no session with id [78a503ec-eebe-46db-ac00-5e69b335633e]
  at ShiroHttpSession.setAttribute            (shiro-web 1.13.0:210)
  at DefaultCspSettings.addCspHeadersWithSession (struts2-core 6.8.0:84)
```

### 29.2 Root cause, read from bytecode rather than inferred

`ShiroHttpServletRequest` caches its `ShiroHttpSession` in a **`protected HttpSession session`** field. The
decompiled `getSession(boolean)` is decisive:

```
47: Subject.getSession(false)   -> existing
62: getfield session
65: ifnull 72                    -> field null -> build a new wrapper
68: iload_3 / 69: ifne 136       -> existing -> RETURN THE CACHED ONE
```

It returns the cached wrapper whenever the field is non-null **and the Subject reports that *some* session
exists**. The test is *"does a session exist"*, **never** *"is the cached wrapper's session the current
one"*. So after `session.stop()` destroys the pre-auth session and `Subject.login()` creates a new one, the
Subject does hold a session, the check passes, and **the dead wrapper is handed back**.

### 29.3 Why T09's fix could not reach it, and why no test caught it

`freshSessionMap()` does `new SessionMap(request)` — re-deriving from **the same request whose cache is
stale**. T09 changed the container, not the contents. And `DefaultCspSettings` never reads `ActionContext`
anyway: it **captures the request reference into a `PreResultListener` lambda before the action runs**, so
nothing the action rebinds afterwards can reach it.

**No test caught it because `InvalidatedSessionMap` is a hand-written double.** It simulates a stopped session
and never exercises the real `ShiroHttpServletRequest` caching that *is* the mechanism. §27.4 Category A
predicted exactly this: *"a real successful login is the first time the production `SessionMap`
participates."* Two audits passed T09; the first real login did not.

### 29.4 No supported alternative exists — demonstrated before reflection was approved

The user declined to approve reflection until this was shown rather than asserted. Each was checked against
the actual jars:

| Candidate | Result |
|---|---|
| `Subject` | Only `login`, `logout`, `getSession(boolean)`. **No renewal API** |
| `WebUtils` | Path and request helpers. **Nothing touches the cached wrapper** |
| `DefaultWebSecurityManager` | No public hook |
| **`subject.logout()`** instead of `stop()` | `removeRequestIdentity` only sets the `IDENTITY_REMOVED_KEY` **request attribute** — and `getSession(boolean)` has **zero references to it** |
| `HttpServletRequest.changeSessionId()` (Servlet 3.1) | Delegates to the *container* session; MARLO uses Shiro **native** sessions |
| Rotating at a different lifecycle point | The cache is populated on the **first** `getSession()`, which happens at step ① reading the pending state — before rotation |

The field is `protected`, with no setter, and no public API clears or invalidates it.

**A supported alternative did exist and was rejected on merit:** removing the CSP interceptor from the
callback's stack. It fixes the **first observed consumer** and leaves the others — and §29.6 shows there is at
least one more. It would also drop a security header. The user rejected it for exactly those reasons.

### 29.5 The fix, and the conditions that make it a managed workaround rather than a hack

`ShiroRequestSessionCacheResetter` — one isolated helper, called **after** `Subject.login()` and before
`freshSessionMap()`. It walks the `ServletRequestWrapper` chain to the real `ShiroHttpServletRequest` and
nulls the cached field, so Shiro rebuilds the wrapper through **its own** construction path against the
session `Subject.login()` just created.

`session.stop()` is untouched. **SEC-003 rotation is preserved.** CSP is untouched — verified: zero changes to
any interceptor stack.

The conditions the user set, and what discharges each:

| Condition | Discharged by |
|---|---|
| Fail soft, never worse | `catch (ReflectiveOperationException \| RuntimeException e)` + a warning |
| **A future Shiro upgrade cannot silently disable it** | A compatibility test asserting the field's **declaring class, name, type, and `protected` modifier** — a retype, a move to a superclass, or a widening each redden it |
| Real-mechanism tests, no doubles for the class under test | An actual `ShiroHttpServletRequest` in every relevant test |
| Reflection documented and tied to Shiro 1.13.0 | The helper javadoc enumerates all five rejected alternatives |

### 29.6 Two Leader errors this task exposed, both corrected

**The `Fails when` clause was wrong.** It asserted that moving the helper call **before** `Subject.login()`
would redden test 2 — *"the ordering is the fix; prove it."* The implementer applied exactly that mutation,
ran the suite **twice**, got green both times, and **investigated instead of accepting a pass that
contradicted the premise**. `Subject.login()`'s session creation runs through `SecurityManager` machinery
independent of the request's cached field, and nothing between the two call sites reads `request.getSession()`.
The Leader asserted an ordering guarantee Shiro does not create. Corrected in `tasks.md`, with the caveat the
audit added: the empirical half was obtained in a harness using `DefaultSecurityManager`, so the conclusion
**survives on analysis, not on the green run**, and must not be cited as empirically proved.

**The mutation that actually proves the fix reveals a second consumer.** Removing the helper entirely reddens
test 4 with the exact production exception — thrown through `SessionMap.put` → **`LoginAction.finishLogin`**,
*not* the CSP interceptor. The stale wrapper has **at least two** consumers.

The audit strengthened this beyond the harness: `struts.xml:540-546` declares a global
`<exception-mapping exception="java.lang.Exception">`, so an `IllegalStateException` from `finishLogin` is
mapped to a result, after which `DefaultActionInvocation` runs its `PreResultListener`s — and CSP then throws
on the same stale wrapper, **masking the first failure**. That reconciles the live trace (a CSP frame, no
`finishLogin` frame) with the harness, and is the decisive argument against removing the CSP interceptor:
doing so would have left `finishLogin` failing into a generic 500 with **no frame pointing at the cause**.

### 29.7 One audit finding was FALSE, and the Leader refuted it

F5 claimed the helper javadoc misdescribed `getSession(boolean)` — specifically the clause *"and the current
Subject reports that some session currently exists"*. The auditor stated plainly that it **could not
decompile the jar** and was reasoning from published source.

**The bytecode in §29.2 shows the javadoc is exactly right.** Offsets 47-69 evaluate `existing` from
`Subject.getSession(false)` and consult it before returning the cached field. The implementer was told not to
act on it.

**This is the second false finding in this spec from an audit reasoning about something it could not
verify** — the first claimed a method had no producer, having grepped a lowercase variant of a capitalised
name. Both times the audit **declared its uncertainty**, which is what made the refutation cheap. An audit
that had asserted either with confidence would have caused a wrong correction.

### 29.8 Gates

| Gate | Result |
|---|---|
| Tests, verified by the Leader | **165 / 165**, 0 failures, 0 errors |
| Mutation — helper removed | Test 4 reddens with `IllegalStateException: UnknownSessionException` through `finishLogin` |
| `session.stop()` / SEC-003 | **unchanged** |
| Interceptor stacks / CSP | **zero changes** |
| Lines over 120 | 0 |

### 29.9 Still open — only a real browser login can close it

**T16's `Done when` is not satisfied by any test in this repository.** It requires the real corporate login to
complete without the exception, in a browser, against the live pool. That is the user's to run. Until then the
fix is evidenced by a real-`ShiroHttpServletRequest` harness and by bytecode — which is the strongest evidence
obtainable here, and is not the same as evidence that it works there.

---

## 30. T16 CLOSED by real E2E evidence — 2026-09-03

The user authenticated through the corporate IdP, Cognito returned to `cognitoCallback.do`, and MARLO landed
on `/AICCRA/crpDashboard.do` with the authenticated user displayed. **No HTTP 500. No
`UnknownSessionException`.**

| # | Requirement | Evidence from the server log |
|---|---|---|
| 1 | Callback received | Four times: `17:22:51`, `17:23:14`, `17:23:30`, `17:25:16` |
| 2 | Code/token processing completed | `Cognito sign-in succeeded for Global Unit AICCRA` on all four |
| 3 | Identity mapped to the MARLO user | `User C.Gamboa@cgiar.org logged in successfully for Global Unit AICCRA` |
| 4 | Success event recorded | Yes — 182 ms after each callback |
| 5 | **`UnknownSessionException`** | **0 occurrences** |
| 6 | Authentication ERROR/SEVERE | **0** |
| 7 | Request continued to the dashboard | `user_permission` lookups and `DownloadGlobalUnitLogoAction` for **AICCRA**, 2.4 s after login |

**Also closed by this run, from §27.4 Category A:** `saveLastLogin` on the **Cognito** path — `last_login`
moved to `2026-09-03 17:25:16`. §21 had validated that mechanism on the local path only and said so.

**An unplanned validation, and a valuable one.** The IdP returned the email as **`C.Gamboa@cgiar.org`** —
different casing from the stored `c.gamboa@cgiar.org` — and it still resolved the row. That is **OQ-9's
normalization working against a real token**, where until now it had only met synthetic values.

**A Leader misreading, corrected within the same turn:** the first callback appeared to lack a success line.
It did not — a `tail -3` had truncated it. All four completed.

---

## 31. U-3 — `users.username` overwritten with Cognito's federated identifier

**Observed:** after the real login, `users.username` for id 3797 changed from **`cgamboa`** to
**`cgiar-azuread_c.gamboa@cgiar.org`**. Recorded as a finding separate from T16; no code or test data changed.

### 31.1 Which claim produced it, and which claim T07 maps

`CognitoTokenValidatorImpl:116` declares `CLAIM_USERNAME_CLAIM = "cognito:username"` and reads it at `:375`
into the assertion. `CognitoIdentityMapperImpl:96-100` lowercases that value and writes it to
`users.username`.

**For a federated principal, `cognito:username` is Cognito's own internal username** — the provider name,
lowercased, prefixed to the subject: `cgiar-azuread_` + `c.gamboa@cgiar.org`. It is **not** a CGIAR login
identifier. It is an artifact of how Cognito names federated users inside its own pool.

### 31.2 The requirement, and the premise nobody verified

FN-006 reads: *"**GIVEN** a CGIAR user whose ID token carries **their CGIAR login identifier** … `users.username`
**MUST** be set from that claim, lowercased — **matching today's `getCgiarNickname()` behavior**."*

And today's behaviour, at `APCustomRealm:334/338`, is `user.setUsername(ldapUser.getLogin().toLowerCase())` —
the **Active Directory login**, i.e. `cgamboa`.

**So FN-006's intent is unambiguous and T07 does not meet it.** This is a **claim-selection error**, not a
coding error: `cognito:username` is not the CGIAR login identifier, and the requirement's `GIVEN` clause
*assumed a claim carrying that identifier exists in the token* without anyone verifying that it does. That
assumption held only as long as no real token was ever seen.

### 31.3 Which claims are actually present — what is known, and what is not

**Known, because the code reads them:** `token_use`, `nonce`, `email`, `cognito:username`. Plus the identity
claim used for the join (OQ-9 settled that on `email`).

**NOT known, and it must not be guessed:** whether this pool's ID token carries any claim holding `cgamboa` —
`preferred_username`, a `custom:` attribute, or `identities[0].userId`. **The validator does not read them, so
nothing in this checkout can answer it.** No raw token, authorization code, or secret was inspected, and none
should be.

**Two safe ways to answer it, both the user's to choose:** read the User Pool's *attribute mapping* for the
`CGIAR-AzureAD` provider in the AWS console — which claim, if any, receives the AD `sAMAccountName`; or
capture one ID token's **claim names and the single value of the candidate claim**, never the token itself.
Until one of those is done, **any correction is guesswork.**

### 31.4 Consumers of `users.username`, and what each does with a federated value

| Consumer | Effect of `cgiar-azuread_c.gamboa@cgiar.org` |
|---|---|
| **`APCustomRealm:172`** — the LDAP bind | **Self-repairing.** `getCgiarNickname` runs first and looks AD up **by email** (`searchUserByEmail`), then **unconditionally overwrites** `user.setUsername(ldapUser.getLogin().toLowerCase())` on both branches (`:333-338`). The bind uses the freshly-corrected value |
| **`APCustomRealm:161`** — `getUserByUsername(username)` | **BREAKS.** This is the branch taken when a user types a login **without** `@`. Typing `cgamboa` finds no row, because the stored value is now the federated string. Email login is unaffected — `:152` uses `getUserByEmail` |
| `ManageUsersAction:262`, `SearchUserAction:91` | Admin JSON exposes the federated string as the user's username |
| **`FeedbackQACommentsAction:180`, `:403`** | Renders `getUsername() + " " + getLastName()` as a **display name** in QA comments — a user-visible `cgiar-azuread_c.gamboa@cgiar.org Gamboa` |
| `ManageUsersAction:159`, `CrpUsersAction:641`, `center/ManageUsersAction:261` | Admin **creation** paths, all setting from `person.getLogin()`. Unaffected, but they establish the convention: `username` is an AD login |

### 31.5 The rollback and coexistence question — the answer is nuanced, and half of it is reassuring

**Rollback by email login: SAFE, and self-healing.** If a unit returns to the local path, a user signing in
with their **email** reaches `getUserByEmail`, then `getCgiarNickname` re-derives the username from AD **by
email** and overwrites the federated value. The AD lookup never keys on the stored username, so a corrupted
one cannot prevent it.

**Rollback by username login: BROKEN.** A user typing `cgamboa` hits `getUserByUsername`, which finds nothing.
They are refused with no indication why. Their own email still works, so the account is recoverable — but the
failure is silent and looks like a wrong password.

**And the repair does not persist.** At `:337-338`, the non-null branch sets the corrected username **in
memory only** — `saveUser` is called solely in the `null` branch at `:334-336`. So even after a successful
email login repairs it for that request, the database keeps the federated value and the next username login
fails again. (Note that `saveUser` is itself the no-op §19.1 documented, so the `null` branch may not persist
either.)

### 31.6 Does `users.username` need writing at all during Cognito login?

**A fair question, and the answer is not obviously yes.**

- **OQ-9 made the normalized corporate email the stable identity.** The Cognito path joins on email and never
  needs `username` to authenticate.
- FN-006's stated purpose is *"maintain `users.username` … **without an LDAP lookup**"* — keeping it fresh so
  the value is available when something else needs it.
- **Today's behaviour is strictly worse than not writing:** it replaces a correct AD login with a value that
  is correct for nothing, and breaks username login until an email login repairs it in memory.

### 31.7 Candidate corrections — compared, none implemented

| # | Correction | Assessment |
|---|---|---|
| **A** | **Map the claim that actually carries the AD login** (`preferred_username`, a `custom:` attribute, or a mapped `identities` value) | **Meets FN-006 as written.** Requires knowing which claim carries it — §31.3. If none does, this option does not exist |
| **B** | **Do not write `username` on the Cognito path at all** | Smallest, safest, and preserves the existing correct value. Leaves FN-006 unmet and needs a requirement amendment. Consistent with OQ-9 making email the identity |
| **C** | **Write only when `users.username` is null** | Never corrupts a good value; still writes a wrong one for new users. A half-measure |
| **D** | **Strip the `cgiar-azuread_` prefix** | **Explicitly not assumed correct, per the user's instruction — and the evidence says it is wrong.** Stripping yields `c.gamboa@cgiar.org`, an **email**, not `cgamboa`. The prefix conceals that the suffix is the wrong value too. It would look fixed and stay broken |

**Recommendation deferred until §31.3 is answered.** If a claim carries the AD login, **A** meets the
requirement as written. If none does, the honest choice is **B** plus an FN-006 amendment recording that the
pool does not expose the identifier the requirement assumed — which would be the third contract gap this
environment has surfaced, after V-1 and T15.

**No code, no test data, and no configuration was changed. The E2E environment is untouched.**
