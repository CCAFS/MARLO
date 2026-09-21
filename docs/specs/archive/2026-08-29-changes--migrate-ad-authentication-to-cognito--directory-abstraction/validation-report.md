# Validation Report — `directory-abstraction` (CHG-COGNITO-DIRABS-001)

## 1. Document Control

| Field | Value |
|---|---|
| **Spec** | `changes/migrate-ad-authentication-to-cognito/directory-abstraction` |
| **Validated** | 2026-08-29 |
| **Branch / HEAD** | `staging-cognito-impl` · `619320bac0` · working tree clean |
| **Merge-base with `staging`** | `c06a8d9f5fa814bc7199bf9268e64398ff93b74b` |
| **Validator** | Leader on `opus` (T3), with **two independent `opus` reviewers** for the judgment phases |
| **`author ≠ auditor`** | **Holds for the implementation** (Implementer ran `sonnet`). **Does NOT hold for T16's evidence base and all of T17**, which the Leader authored — both were routed to the independent reviewers with instructions to scrutinise them harder, and **both re-derived T16 from source and confirmed it** |
| **Inputs** | `requirements.md` · `design.md` · `tasks.md` · `execution.md` (3,284 lines) · `proposal.md` · `judgment.md` · `../family.md` · `../analysis/` |
| **`test-report.md`** | **ABSENT** — `/akili-test` never ran; tests were authored inside their migration tasks. Coverage was verified **directly from the 9 test sources** |

---

## 2. Summary

> ## ✅ REMEDIATION APPLIED 2026-08-29 — both FAILs closed, plus the four corrections that reach other children.
>
> | # | Finding | What landed |
> |---|---|---|
> | **R1** | V-F1 — `email` mapping ungated | `RAW_BACKEND_EMAIL` added, **deliberately different** from the requested email, plus an `assertEquals` on `getEmail()`. **Mutation-proven:** making the seam echo its input parameter now fails with `expected:<[Jane.Smith@CGIAR.ORG]> but was:<[jane.smith@cgiar.org]>` — that mutation passed silently before |
> | **R2** | V-F2 — `NOT_FOUND asserts knowledge` falsified | Clause scoped to its scenario in `requirements.md`; **both shipped Javadocs corrected** (`DirectoryService`, `DirectorySource`) so child 3 inherits the true contract; `tasks.md` T02's live instruction — which would have told a re-implementer to rewrite the falsified claim — corrected too |
> | **R3** | V-W7 — the "five consumers" error | **All 12 loci corrected** across `requirements.md`, `design.md`, `proposal.md`, `execution.md`, including `FN-001`'s unsatisfiable six-consumer scenario and `SC-6a`'s denominator |
> | **R4** | V-W12 — `family.md` | *"rewrites `getOutlookUser()`"* → **deletes**; and *"child 1 writes only `BaseAction` and new files"* → the true 7-file footprint. The parallel-safety conclusion was always right; the premise understated child 1 by six files |
> | **R5** | V-W13 — TRD sync unrecorded | Both `design.md` §8 TRD deliverables added as **item 0** of the pending-for-`staging` list. Without this the sync had **no carrier out of this branch** |
> | **R6** | V-W1 — repudiated claim in shipped test source | `GuestUsersValidatorDirectoryTest` corrected; the clause is now stated as **genuinely uncovered**, with the falsifiable structural substitute named |
>
> **Correction closure ran on R2 and R3:** each defect class was enumerated *before* editing and verified to grep empty *after*. **Gates after remediation: compile exit 0 · 39 tests, 0 failures.**
>
> **Remaining: 0 FAIL · 8 WARN, all carried.** → **Archive-ready.**

**Original verdict (pre-remediation): 2 FAIL, 12 WARN, 0 BLOCKED.**

> **The code is sound. Every finding below is about evidence or documentation, not behavior.**
> Both reviewers re-derived the load-bearing numbers from the tree rather than from the matrix, and
> found no unimplemented requirement, no missed consumer, and no moved protected file.

| Phase | Result |
|---|---|
| 1. Task completion | **PASS** — 18/18 `[x]`, every one citing `execution.md` |
| 2. File existence | **PASS** — 5 new production, 9 new test, 7 modified, all present |
| 3. Build integrity | **PASS** (compile, tests) · **BLOCKED** (checkstyle, EB-2 — re-confirmed live, not inherited) |
| 4. Requirement coverage | **59/59 items audited — 51 PASS · 6 WARN · 2 FAIL.** §9's denominator is arithmetically correct |
| 5. Quality / 4R | **PASS** + 6 advisory findings |
| 6. Design conformance | **PASS** — all 9 design decisions verified in source; 1 recorded deviation |
| 6b. Cross-document figures | **WARN** — 6 findings, one spanning **11 loci across 3 approved documents** |
| 6c. Constitutional | **PASS** — Hard rules 3, 4, 5, 6, 7, 8, 11 re-derived independently |

**The two FAILs are evidence-integrity, not defects.** Neither ships a bug; **both would let one ship next.**

---

## 3. Task Completion — PASS

| Check | Result |
|---|---|
| Tasks `[x]` | **18 / 18** |
| Tasks with an evidence pointer | **18 / 18** |
| Definition of Done | 14 ticked · **1 correctly unticked** (`SC-10`, an open success criterion) |

**T17 warrants a note.** It consumed **6 review rounds and passed none** — it reached the 3-attempt ceiling, HALTed, and closed under user authorisation by applying must-fix findings and carrying the rest. That history is recorded in its own entry and in the HALT record. The task is complete; the *process* was the most expensive in the spec, for a document rather than for code.

---

## 4. File Existence — PASS

| Group | Expected | Found |
|---|---|---|
| New production (`marlo-data/.../security/directory/`) | 5 | **5** |
| New tests (`marlo-web/src/test/`) | 9 | **9** |
| Modified production | 7 | **7** — 67 insertions / 111 deletions |

The production surface is **net smaller**, which is what `design.md` predicted.

---

## 5. Build Integrity

| Gate | Command | Result |
|---|---|---|
| Compile | `mvn -q install -DskipTests -pl marlo-web -am` | **PASS** (exit 0) |
| Tests | `mvn -q -pl marlo-web test` | **PASS** — **39 tests, 0 failures, 0 errors, 0 skipped** |
| Checkstyle | `mvn -q checkstyle:check` | **BLOCKED** — exit 1, `NoSuchMethodError: Checker.setClassloader` |

**EB-2 was re-tested, not inherited.** `maven-checkstyle-plugin:2.9.1` against the forced `checkstyle:8.18` still throws the exact API incompatibility the spec recorded at T01. The spec's treatment — **UNVERIFIABLE, never "passed"** — is accurate and current.

**Substitute evidence, independently verified:** GPL header on **all 14** new `.java` files; **zero lines over 120 characters** across all new files (the only over-length line under `src/test` pre-dates this spec).

**Environment boot smoke:** not re-run. `T12`'s manual Spring-context start remains the only `D8` evidence — an accepted risk, not a closed gate.

---

## 6. Requirement Coverage — 59/59 audited

**§9's denominator is correct**: 17 scenarios · 15 `BUT` · 16 `AND IT MUST` · 11 NF/SEC/OPS/ARCH = **59**, recounted clause by clause against `requirements.md`.

**51 PASS · 6 WARN · 2 FAIL · 0 BLOCKED.**

### 🔴 V-F1 — FAIL · `FN-005` *Found person*: the `email` mapping is gated by evidence that structurally cannot fire

`LdapDirectoryService:73` maps `DirectoryPerson.email ← user.getEmail()`. `requirements.md:286-287` requires all four fields to equal their `LDAPUser` getters, untransformed.

**`getEmail()` is asserted nowhere in the directory tests — zero occurrences.** The contract test stops after `login`, `firstName`, `lastName`, `source`.

Worse, **the fixture could not assert it even if it tried.** `LdapDirectoryServiceTest` builds the stub with the *same string* the test then passes to `findByEmail`, so a mutation returning the **request parameter** instead of `user.getEmail()` passes the entire suite.

This is **`D1`, the spec's own self-declared dominant defect class**, and it is the exact shape of T06's own disqualifier — *"an already-lowercase fixture would pass either way and prove nothing"* — applied to `email` instead of `login`. `FN-005` *Not found*'s *"`email` MUST echo the requested email"* is uncovered for the same reason.

**Code is correct. The claimed gate is not a gate.**
**Remediation:** one `assertEquals` on `getEmail()`, plus a fixture whose backend email **differs** from the requested one.

### 🔴 V-F2 — FAIL · `FN-002`'s *"`NOT_FOUND` asserts knowledge"* clause is falsified by the spec's own DD-11 — and the contradiction shipped into the Javadoc child 3 inherits

`requirements.md:205` — *"**AND IT MUST** be reachable only after the directory actually answered — `NOT_FOUND` asserts knowledge, not absence of knowledge."*

**Three shipped paths return `NOT_FOUND`; only one involves a directory answer:**

| Path | Directory answered? |
|---|---|
| `LdapDirectoryService:61-63` — null/blank input | **No — no backend call at all** |
| `:80` — malformed email, after the backend threw | **No — the backend failed** |
| `:70-72` — person absent | Yes |

DD-11 **mandated** the second path on 2026-08-28. The clause was never reconciled.

**The contradiction is in-tree, in the file T02 declared *"the Javadoc **is** the contract"*:** `DirectoryService.java:55` states `NOT_FOUND` *"asserts knowledge: the directory was reached, it answered"*, and `DirectorySource.java:30-31` repeats it. **Child 3 inherits this Javadoc verbatim as DD-9's reusable contract.**

**Remediation is to the text, not the code:** scope the clause to the *"directory answered"* scenario, and amend both Javadoc blocks to name the two knowledge-free `NOT_FOUND` paths.

### 🟡 Coverage WARNs

| ID | Finding |
|---|---|
| **V-W1** | **A repudiated claim survives in shipped test source.** `GuestUsersValidatorDirectoryTest:159-162` says the field-injection clause is *"covered by `DIRABS-T12`'s app-start check"*. `tasks.md:325` repudiates exactly that, in a box titled **CORRECTED**. Five loci were corrected; **this one — the only one in a compiled file — was missed** |
| **V-W2** | **`DirectoryLookupException` cannot wrap the cause both `requirements.md:228` and `design.md:255` promise.** `center/…/ManageUsersAction:267` passes `null`, because `DirectoryPerson` carries no cause. The trade-off is commented in-code and logged — **but neither approved document was amended**, so both still assert a property the shipped code cannot hold |
| **V-W3** | **`FN-006` *CrpUsersAction*'s `AND IT MUST` i18n clause has no test, and the gap is unrecorded.** Source-correct at `:644-645` / `:658-659`; T07 asserts **both** of its i18n keys, T06 asserts neither. Evidence is a source read only |
| **V-W4** | **`NF-007` unmet in letter for two consumers.** T05+T06+T07 shipped as one commit, so `CrpUsersAction` and `json/global/ManageUsersAction` are not independently revertible. Intent preserved; **`tasks.md:260` misstates which option forfeits it** — (a) forfeits the letter, (b) forfeits the outcome |
| **V-W5** | **The `setInternalConnection` assertion is one-sided.** A hardcoded `setInternalConnection(true)` passes identically; the production branch is unreachable in a unit test. Honest given the constraint, **not recorded as thin** |
| **V-W6** | **The 59-item taxonomy excludes ~21 normative plain-`AND` `MUST` clauses** — the true normative surface is closer to 80. §9 never claims otherwise, but a closer reads "59" as the total. **All 21 were checked individually: every one is covered; three rest on source reads alone** |

---

## 7. Linting & Code Quality

**Checkstyle is BLOCKED (EB-2).** Substitutes verified independently: GPL header on 14/14 new files, zero lines >120, import grouping conforming to `configuration/ccafs-java-style.importorder`.

### Advisory — 4R sweep (non-gating)

| Lens | Finding |
|---|---|
| **Resilience / Risk** | `LdapDirectoryService:65-66` constructs `LDAPService` **outside** the `try`. The never-throws invariant holds only because DD-12's `javap` probe proved **`adauth` 5.7**'s constructor is `super()` + one `putfield`. **That is a property of a pinned jar, not of a contract** — and the contract test cannot detect a regression, because the test overrides `newLdapService()`. **If child 3 bumps or swaps the jar, the invariant becomes conditional silently.** Worth a comment at `:65` naming the dependency |
| **Resilience** | `:75` catches `Exception`, not `Throwable`. An `Error` — e.g. `NoClassDefFoundError` once child 3 starts removing the jar — escapes and breaks the never-throws contract. Equivalent to today; **child 3 is the phase that makes it reachable** |
| **Risk / privacy** | `DirectoryPerson.toString()` masks `email` and `login` per DD-6 but prints `firstName` and `lastName` **in full**. DD-6's own rationale — *"corporate personnel data that will reach log lines"* — applies to all four equally |
| **Risk / privacy** | `DirectoryLookupException` keeps the email out of its message but exposes it via `getEmail()`. Any handler logging it reintroduces the exposure |
| **Readability** | `GuestUsersValidator.validate:41-49` reassigns its own parameter. Pre-existing, correctly preserved for equivalence — flagged so nobody "cleans it up" |
| **Reliability — do not fix** | `person.getLogin().toLowerCase()` NPEs on a found person with a null login, at four call sites. **`OQ-5` confirms this is preserved on purpose**; a null guard would be a defect in this spec |

---

## 8. Design Conformance — PASS

All nine design decisions verified **in source**, not from the log:

| Decision | Verdict |
|---|---|
| **DD-2** delete `getOutlookUser`, don't rewire | ✅ zero declarations, zero calls repo-wide |
| **DD-3** `ERROR` as the 8th `DirectorySource` | ✅ 8 constants, 3 produced here / 5 reserved for child 3 |
| **DD-3a** `extends RuntimeException`, **not** `AuthorizationException` | ✅ verified outside that hierarchy — `struts.xml:543-545` maps it to 403 |
| **DD-4** raw values, consumers keep `.toLowerCase()` | ✅ no transformation in the seam; all four consumer call sites preserved |
| **DD-6** immutable, masked `toString` | ✅ all fields final; a null-source instance is unconstructible |
| **DD-9** abstract reusable contract test, 5 seams | ✅ provider-agnostic; child 3 reuses it verbatim |
| **DD-10** no Spring context test | ✅ none added; T12 is the recorded substitute |
| **DD-11** malformed discriminated inside the catch | ✅ exactly the specified shape; `error` log on the well-formed branch only |
| **DD-12** `protected newLdapService()` | ✅ called **outside the `try`, in the original position** |

**One deviation, recorded not silent:** the `null` cause in `DirectoryLookupException` (see **V-W2** — the code is right, the documents were not amended).

### 6b. Cross-document figures — WARN

> The command's premise, and this spec proves it: *"two documents agreeing is not evidence — it is often one wrong idea copied forward."*

#### 🟡 V-W7 — the headline: *"the **five** consumers that read only `found`"* is wrong. It is **four**

Measured from the tree:

| Measurement | Value |
|---|---|
| Classes injecting `DirectoryService` | **5** |
| Classes importing `DirectorySource` (i.e. reading `source`) | **1** — `center/json/global/ManageUsersAction` |
| **Therefore found-only consumers** | **4** |

`design.md` **states both answers**: `:19` and §2.1's caller box say *"five callers migrated"* — **correct**; `:242` says *"Five callers never learn that `ERROR` exists. One does"* — that is 5 + 1 = 6, **contradicted by the five-row table printed directly above it.**

**Root cause:** the "six consumers" set is `requirements.md` §2.1 rows 1–6, whose **row 1 is `BaseAction`**. **DD-2 removed `BaseAction` from the consumer set** — the table's own *Fate* column says *"Method deleted (DD-2)"*, and `FN-007` forbids `BaseAction` from referencing `DirectoryService` "in any form". Every *"five found-only + one source-reader"* sentence is the **pre-DD-2 arithmetic, carried past the decision that invalidated it.**

**11 uncorrected loci across 3 approved documents:** `design.md:242`, `:395`, `:456`, `:462` · `requirements.md:221`, `:232`, `:308`, `:458`, `:610` · `proposal.md:177`, `:289`.

**Two consequences beyond prose:**
1. **`FN-001`'s scenario is literally unsatisfiable.** It reads *"GIVEN the **six** consumers listed in §2.1 … THEN it MUST call `DirectoryService.findByEmail`"*. Applied to row 1 (`BaseAction`), that is **forbidden by `FN-007`**.
2. **`SC-6a` is ticked against a denominator that does not exist.** Its substance is fully covered — every found-only consumer is tested — but the criterion names a set size of five.

**No code change is implied.**

| ID | Finding |
|---|---|
| **V-W8** | `requirements.md:30` still says *"**4** files created"*. It is **5** — a surviving **pre-DD-3a** locus, a sibling of the very reconciliation row that records the change. Contradicted by `requirements.md:115`, `:554`, `design.md:9`, `proposal.md:251`, `:501` |
| **V-W9** | `requirements.md:554` scopes the **GPL-header check to 5 files**; there are **14** new `.java` files. `NF-006` itself says *"every new `.java` file"*. **No actual violation** — all 14 verified — but the certification covers 5 of 14 |
| **V-W10** | **The review-round count is 23 in one locus and 25 in another, three lines apart.** `execution.md:21` establishes **25** with a derivation that sums correctly; `:3219` still says *"23 rounds as of this writing"*, three lines above `:3222`'s *"at a total of 25"*. **A live instance of the very defect class `execution.md`'s closing section was written to describe** |
| **V-W11** | **The deferred `design.md` budget enumeration is itself incomplete by one locus.** Pending item 9 names six LOC loci; **`design.md:336` — "Review rounds ~20" — is a seventh exceeded budget figure and is not in the list.** The enumeration reproduces, one file over, the partial-correction failure it was written to prevent |
| **V-W12** | **`family.md:160` still says child 1 *"rewrites `getOutlookUser()`"*.** It **deletes** it (DD-2, FN-007). `proposal.md:498` corrected this phrasing in `proposal.md` and **did not sweep `family.md`** — the manifest child 2 and child 3 read first. The parallel-safety **conclusion** still holds; it is wrong reasoning reaching a right answer |

### 6c. Constitutional alignment — PASS

| Hard rule | Verdict |
|---|---|
| **3** — Spring MVC owns `/api/*` | ✅ no new route, no new `*.json` Struts path, no interceptor |
| **4** — constants in **both** `APConstants.java` | ✅ all four `*_AD` byte-identical in both (`SEC-001`) |
| **5** — Flyway migrations | ✅ zero added |
| **6** — GPL header on every new `.java` | ✅ **14 / 14** |
| **7** — style, ≤120 chars | ✅ zero violations across all new files |
| **8** — English only, i18n-keyed | ✅ no new user-facing string; `global.properties` untouched |
| **11** — no dependency downgrade | ✅ `adauth` 5.7 untouched in all 3 POMs |
| **Module boundaries** | ✅ the seam sits in `marlo-data/security/directory/`, beside `security/authentication/` |

**Shared-File Write Discipline — PASS on the writes, WARN on the record.** No spec commit touches `docs/trd/trd.md`, root `CLAUDE.md`/`AGENTS.md`, `.agents/*` or `general-setup/*`; `docs/trd/trd.md` contains **zero** occurrences of `security/directory` or `DirectoryService`, confirming the §8 TRD addition genuinely was withheld.

**V-W13 — the withheld write was never recorded as a pending item.** `design.md` §8 declares two TRD deliverables (a `§2` package note and a `§14.5 MO-2` note), both marked *"at archive time"*. **Neither appears in `execution.md`'s 9 pending-for-`staging` items nor in its 5 carried items.** The discipline requires the edit to be *recorded* as pending, not merely withheld — **as it stands the TRD sync has no carrier into `staging`.**

---

## 9. Test Evidence Summary

**No `test-report.md`.** `/akili-test` never ran; coverage was verified directly from the 9 test sources.

| Metric | Value |
|---|---|
| Tests | **39**, 0 failures |
| Test files under `src/test` | 12 (11 with `@Test`) — **9 new here, of which 8 are test classes** |
| Executing classes | 10 — `DirectoryServiceContractTest` is abstract and runs via `LdapDirectoryServiceTest` |
| Baseline before this spec | **3 files, one with its only test body commented out** |

**Mutation-based falsifiability was the run's standard** — assertions were watched fail before being reported. `ContactPersonActionTest`'s bytecode test is the load-bearing case: the `adauth` types remain on the test classpath, so a restored `new LDAPService()` would execute and leave the five runtime tests green. Only the constant pool can see the absence.

**The absence of `test-report.md` is structurally consequential in exactly one place:** no artifact cross-checks assertion-to-clause. **That is how V-F1 and V-W3 survived** — both are gaps a requirement-to-test matrix would have surfaced mechanically.

### The spec's self-declared gaps — all confirmed real, correctly scoped, correctly classified

| Gap | Verdict |
|---|---|
| Real `ad_user` query stubbed; **no task covers it** | ✅ honest |
| JSON serialization never exercised | ✅ honest — in-memory payload proven, wire bytes inferred |
| `ContactPersonAction:83` NPE, **deliberately unasserted** | ✅ correct — asserting it would freeze a bug as contract |
| 15 FTL pages' rendering ungated | ✅ honest |
| `FN-006`'s `config`-through-inherited-field clause genuinely uncovered | ✅ honest — and correctly re-classified away from T12 (**V-W1** is the one stale locus) |
| `D8` — one manual start, accepted risk **not** a closed gate | ✅ honest |
| No integration harness / E2E / local memcached | ✅ honest |
| `SC-10` open and unticked | ✅ correct — the config leg belongs to child 3 |
| Checkstyle UNVERIFIABLE | ✅ **re-tested live**, not a stale excuse |

**A gap that is real, named and correctly scoped is the spec being honest, not a failure.**

---

## 10. Agent Guide / Constitution Impact — PASS

`execution.md` carries **no `## Constitution Impact` section**, so no child guide is referenced. Independently confirmed that root `CLAUDE.md`'s **Module Guides** table is still true: `marlo-*/CLAUDE.md` returns **no files**. No child guide exists; no drift.

---

## 11. Remediation

### Must fix before archive

| # | Finding | Action | Cost |
|---|---|---|---|
| **R1** | **V-F1** — `email` mapping ungated | Add `assertEquals` on `getEmail()` in the contract test **and** make the fixture's backend email differ from the requested one | ~10 lines, 1 test file + 1 fixture |
| **R2** | **V-F2** — `NOT_FOUND asserts knowledge` falsified | Scope the `requirements.md:205` clause to the *"directory answered"* scenario; amend `DirectoryService.java:52-58` and `DirectorySource.java:30-31` to name the two knowledge-free paths | ~8 lines, 1 doc + 2 source Javadocs |

**R2 is the one that travels:** child 3 inherits that Javadoc verbatim.

### Should fix before archive — corrections that reach other children

| # | Finding | Action |
|---|---|---|
| **R3** | **V-W7** — the "five consumers" error at 11 loci | Correct all 11, **and** `FN-001`'s six-consumer scenario, **and** `SC-6a`'s denominator. Apply **correction closure**: grep the superseded value family-wide |
| **R4** | **V-W12** — `family.md:160` *"rewrites `getOutlookUser()`"* | Correct to *"deletes"*; also `family.md:261`'s *"child 1 writes only `BaseAction` and new files"* (it modified 7) |
| **R5** | **V-W13** — TRD sync never recorded | Add the two `design.md` §8 TRD deliverables to the pending-for-`staging` list |
| **R6** | **V-W1** — repudiated claim in shipped test source | Correct `GuestUsersValidatorDirectoryTest:159-162` |

### Carry to `/akili-archive` or a follow-up

`V-W2` (amend the two documents to match the `null`-cause reality) · `V-W3` · `V-W4` · `V-W5` · `V-W6` · `V-W8` · `V-W9` · `V-W10` · `V-W11` · the 6 4R advisories · the 9 pending-for-`staging` items · **`EXEC-106`'s stale `getADFilter :58-71` → `:55-68`** (a child-3 deletion instruction that would remove the wrong lines).

### The sweep that would have found four of these

`execution.md` records it and **it was never run**:

> **A correction note is a self-declared index of a defect class.** Harvest every *"previously read"* / *"this cell read"* / *"used to read"* note, extract the quoted former text, grep it family-wide, and treat any hit outside its own correction note as a surviving sibling.

**V-W7, V-W8, V-W10 and V-W12 are four hits this sweep would have caught.** Its value is now demonstrated rather than argued. ~40 correction notes exist in this spec.

---

## 12. Archive Readiness Recommendation

> ### ✅ ARCHIVE-READY — `R1`–`R6` applied and verified 2026-08-29.

| Criterion | Status |
|---|---|
| All required tasks `[x]` | ✅ 18/18 |
| **No unresolved FAIL** | ✅ **both closed** — V-F1 mutation-proven, V-F2 corrected in the requirement **and** both shipped Javadocs |
| WARNs accepted or tracked | ✅ 4 fixed (V-W1, V-W7, V-W12, V-W13); **8 carried** with owners |
| Tests cover key requirements | ✅ 59/59 audited; the two unfirable gates now fire — **39 tests, 0 failures** |
| Drift reflected in the docs | ✅ all 12 consumer-count loci corrected; TRD sync recorded as pending |
| User reviewed | ✅ remediation authorised and applied |

**Next:** `/akili-archive changes/migrate-ad-authentication-to-cognito/directory-abstraction`

**Carried into archive, with owners:** V-W2 (amend both documents to the `null`-cause reality) · V-W3 · V-W4 · V-W5 · V-W6 · V-W8 · V-W9 · V-W10 · V-W11 · the 6 4R advisories · the **10** pending-for-`staging` items · **`EXEC-106`'s stale `getADFilter :58-71` → `:55-68`**, a child-3 deletion instruction that would remove the wrong lines · and **the correction-note sweep**, whose value four of this validation's own findings demonstrated.

**What this spec actually delivered, and it holds up:** every corporate-user lookup in MARLO now goes through one interface; `new ADConexion` is gone from the codebase; `getOutlookUser` has zero implementations; the repository went from **3 test files, one with its only body commented out, to 39 passing tests**; and `adauth` is untouched in all three POMs — **Gate 1 is explicitly NOT reached, by design, and the report says so at eight loci.**
