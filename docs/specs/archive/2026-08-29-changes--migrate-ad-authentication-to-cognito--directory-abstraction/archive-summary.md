# Archive Summary — `directory-abstraction` (CHG-COGNITO-DIRABS-001)

## 1. Document Control

| Field | Value |
|---|---|
| **Spec ID** | `CHG-COGNITO-DIRABS-001` |
| **Original path** | `docs/specs/changes/migrate-ad-authentication-to-cognito/directory-abstraction/` |
| **Archive date** | 2026-08-29 |
| **Family role** | **Child 1 of 3** — parent `changes/migrate-ad-authentication-to-cognito` |
| **Branch** | `staging-cognito-impl` · **spec branch**, not the `staging` pin |
| **Final commit** | `715b543a92` · 17 spec-tagged commits · merge-base `c06a8d9f5f` |

## 2. Final Status

> ### ✅ Complete — 18/18 tasks, 0 FAIL, archive-ready.

| Gate | Result |
|---|---|
| Compile — `mvn -q install -DskipTests -pl marlo-web -am` | **exit 0** |
| Tests — `mvn -q -pl marlo-web test` | **39 tests, 0 failures** |
| Checkstyle — `mvn -q checkstyle:check` | **UNVERIFIABLE (EB-2)** — re-tested live at validation, still `NoSuchMethodError` |
| Validation | **0 FAIL · 8 WARN carried with owners** |

**Gate 1 of the parent programme is explicitly NOT reached, by design.** `adauth` remains the
implementation, remains in all three POMs, remains on the classpath. **This child removed nothing from
it.** Two Capability A sites stay live for child 2; one unreachable site goes in child 3.

## 3. Requirements Delivered

**59 clause-level items audited at validation: 59/59 covered.**

| Requirement | Delivered |
|---|---|
| `FN-001` | All 5 consumers reach the directory through `DirectoryService`; none imports `org.cgiar.ciat` |
| `FN-002` | 4 outcomes incl. the `NOT_FOUND` / `ERROR` split; `center/json/global/ManageUsersAction` is the sole `source` reader and does not silently degrade |
| `FN-003` | `source` non-null on every path; `ERROR` never masquerades as `NOT_FOUND` |
| `FN-004` | Raw values from the seam (DD-4); every consumer keeps its own `.toLowerCase()` |
| `FN-005` | Found / not-found mapping, null-not-empty fields, no cache or retry |
| `FN-006` | All 5 consumer migrations, each behaviorally asserted |
| `FN-007` | `BaseAction.getOutlookUser` **deleted**, no new dependency |
| `FN-008` | `ContactPersonAction`'s dead AD construction eliminated; payload proven unchanged |
| `FN-009` | `searchUsersUtil` left in place by decision, named in both gates |
| `NF-001`…`008`, `SEC-001`, `OPS-001`, `ARCH-001` | All satisfied; `SEC-001` verified in both `APConstants.java` |

**One success criterion remains open by design:** `SC-10` (*"swapping the provider is one `@Named` bean
plus one config value"*) — its config half belongs to **child 3**, and its `/akili-validate` leg is
recorded, unticked.

## 4. Files Changed

| Group | Count | Detail |
|---|---|---|
| **New production** | **5** | `marlo-data/.../security/directory/` — `DirectoryService`, `DirectoryPerson`, `DirectorySource`, `DirectoryLookupException`, `impl/LdapDirectoryService` |
| **Modified production** | **7** | `BaseAction` · `ContactPersonAction` · `center/json/global/ManageUsersAction` · `crp/admin/CrpUsersAction` · `json/global/ManageUsersAction` · `json/global/SearchUserAction` · `GuestUsersValidator` |
| **New tests** | **9 files / 8 test classes** | `FakeDirectoryService` is a hand-rolled double with no `@Test` |
| **Net production delta** | **67 insertions / 111 deletions** | The production surface is **smaller** than before |
| **Protected files touched** | **0** | 6 POMs, both `APConstants.java`, `APConfig.java`, migrations — all verified unchanged against the merge-base |

## 5. Test Evidence

| Metric | Before | After |
|---|---|---|
| Test files in the repository | **3** — one with its only body commented out | **12** (11 with `@Test`) |
| Tests | 1 meaningful | **39, 0 failures** |

**`test-report.md` was never produced** — `/akili-test` did not run; tests were authored inside their
own migration tasks. Coverage was therefore verified **directly from the 9 test sources** at validation.
**The absence is explicitly accepted, and its one structural consequence is recorded:** no artifact
cross-checks assertion-to-clause, which is how validation's `V-F1` and `V-W3` survived to that stage.

**Mutation-based falsifiability was the standing evidence rule** — no assertion was reported without
first being watched fail. The load-bearing example: `ContactPersonActionTest`'s bytecode assertion. The
`adauth` types remain on the test classpath, so a restored `new LDAPService()` would execute happily
and leave all five runtime tests green. **Only the compiled constant pool can see the absence.**

## 6. Validation Summary

Two independent `opus` reviewers, 59/59 items, plus a late independent audit of `T16` — the gate task
that had run Leader-inline without a Reviewer.

| Finding | Resolution |
|---|---|
| **V-F1** — the `email` mapping was gated by evidence that **could not fire** (the fixture supplied the value it then asserted) | **Fixed and mutation-proven** |
| **V-F2** — `FN-002`'s *"`NOT_FOUND` asserts knowledge"* clause was falsified by the spec's own `DD-11`, and the contradiction had reached the **Javadoc child 3 inherits** | **Fixed** in the requirement, both shipped Javadocs, and `tasks.md` T02's live instruction |
| **V-W7** — *"the five consumers that read only `found`"* is **four**; wrong at 12 loci in 4 documents | **All 12 corrected**, including `FN-001`'s unsatisfiable scenario and `SC-6a`'s denominator |
| **V-W12** — `family.md` said child 1 *"rewrites"* `getOutlookUser` and *"writes only `BaseAction`"* | **Corrected** — it deletes it, and touches 7 production files |
| **V-W13** — the two TRD deliverables were withheld correctly but **never recorded**, so the sync had no carrier into `staging` | **Recorded** as pending item 0 |
| **V-W1** — a repudiated claim survived in shipped test source | **Corrected** |

## 7. Accepted Warnings & Follow-Ups

**8 WARNs carried, each with an owner.** None blocks archive.

| Carried item | Owner |
|---|---|
| The real `ad_user` query is stubbed — **no task in this spec covers it** | future spec |
| JSON serialization never exercised (in-memory payload proven, wire bytes inferred) | future spec |
| `ContactPersonAction:83` NPEs on a missing query parameter — **deliberately unasserted**, since asserting it would freeze a bug as contract | future spec |
| `json/global/ManageUsersAction`'s 15 FTL pages — rendering ungated | future spec |
| `FN-006`'s `config`-through-inherited-field clause — genuinely uncovered | `trd.md` §14.9 item 8 |
| `D8` Spring wiring — one manual start, **accepted risk, not a closed gate** | `trd.md` §14.9 item 8 |
| No integration harness, no E2E, no local memcached (`TS-3`) | programme level |
| Checkstyle **UNVERIFIABLE** (EB-2); `includeTestSourceDirectory` also unset | separate remediation spec |

**⚠️ One follow-up reaches another child and must not be lost:** `EXEC-106` (CP8) instructs child 3 to
delete `ContactPersonAction.getADFilter` at **`:58-71`**. `T14`'s import deletion moved it to
**`:55-68`** — a child-3 agent following the runbook literally deletes the wrong lines. Recorded in the
runbook's `Execution State`.

## 8. Historical Notes

**What this spec actually changed.** Every corporate-user lookup in MARLO now passes through one
interface. `new ADConexion` no longer exists anywhere in the codebase — `searchContact.do` had been
opening a live Active Directory connection on **every request** and discarding the result.
`getOutlookUser` has zero implementations. And MARLO went from 3 test files to 39 passing tests.

**What it cost, recorded honestly.** **25 review rounds against a ~20 budget — exceeded by ~25%.**
`T17`, the checkpoint *report*, consumed **6 of those and passed none**: it reached the 3-attempt
ceiling, HALTed, and closed under user authorisation. **Every code task passed with zero findings; the
document describing them did not.** All six T17 rounds failed on one shape — a correction note
describing its own former text, with that former text still live in a sibling locus.

**Three environment blockers, one of them newly diagnosed.** `EB-1` (compile deferral) was superseded at
`T04`. `EB-2` (Checkstyle cannot execute) held all the way through and was re-tested at validation rather
than inherited. **`EB-3` was diagnosed at `T14` and explains three previously separate blockages under
one mechanism:** VS Code's `redhat.java` JDT language server **writes into** `marlo-web/target/classes`
and `target/generated-sources` — it is a *second writer*, not merely a lock holder. Killing it does not
work; VS Code respawns it in seconds.
