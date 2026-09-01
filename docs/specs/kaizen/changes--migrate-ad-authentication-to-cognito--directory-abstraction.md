# Kaizen Entry — changes/migrate-ad-authentication-to-cognito/directory-abstraction

## Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/migrate-ad-authentication-to-cognito/directory-abstraction` |
| Date | 2026-08-29 |
| Branch | `staging-cognito-impl` — **spec branch** (pin: `staging`) |
| Archive Run | 1 |
| Approval Mode | **gated — no apply menu fired.** Every item below is `pending` for the default-branch apply phase |
| Note | First kaizen entry in this repository — no digest, no prior entry files, so **no recurrence check was possible** |

## Metrics

| Signal | Value | Source |
|---|---|---|
| Tasks executed | 18 (T00–T17), all `[x]` | `tasks.md` |
| Tasks requiring rework | **7** — T02, T03, T08, T10, T11, T13, T17 | `execution.md` task entries |
| Review rounds consumed | **25 of ~20 budgeted — EXCEEDED ~25%** | `execution.md` §1 Document Control |
| HALTs | **2** — T11 (3-attempt ceiling on documentation consistency); **T17 (ceiling; 6 rounds, none passed)** | `execution.md` HALT records |
| FATAL_FAILs | 0 | `execution.md` |
| Pivots | 0 — no `## Pivot Record` exists | `execution.md` |
| PRODUCT_BUGs | 0 | no `test-report.md`; no test held red on a real defect |
| Judgment-day findings | JD-1 and JD-7 acted on during specify | `judgment.md`, cited in `execution.md` |
| Validation FAIL / WARN | **2 / 12** — both FAILs remediated before archive | `validation-report.md` |
| `/akili-quick` escalations | 0 | no `quick-log.md` |
| Drift attributable | *not assessed* — `docs/specs/audits/` holds no report | — |
| Environment blockers | 3 — EB-1 (superseded at T04), EB-2 (held throughout), **EB-3 (newly diagnosed at T14)** | `execution.md` §3 |

**The headline waste (MUDA): `T17`, a checkpoint *report*, consumed 6 of the 25 review rounds and
passed none.** Every code task passed with zero findings. The document describing them did not.

## Lessons

- **KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-1 — A correction note is a self-declared index of a defect class, and nobody harvests it.** (Product + Methodology, **High**)
  - **Root cause (5W1H):** every enumeration in this run was scoped to *classes a reviewer had already
    named*. So each post-check honestly returned empty, and the next audit found a class nobody had
    named yet. A correction note is different in kind: its author has **already written down both the
    defect and its signature** ("this line previously read X"). Grepping X family-wide is mechanical,
    and it was never done.
  - **Evidence:** `execution.md` — T17 HALT record, and the closing section *"The sweep nobody ran"*,
    which tabulates five rounds failing on this one shape. `validation-report.md` — **V-W7, V-W8, V-W10
    and V-W12 are four further hits the sweep would have caught**, found only because two independent
    reviewers went looking.
  - **Why it is not generic:** it names a concrete artifact (a correction note), a concrete operation
    (harvest → extract quoted former text → grep family-wide → any hit outside its own note is a
    surviving sibling), and a concrete failure it prevents.
  - **Standardization:** → P1 (local) · P2 (upstream)

- **KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-2 — A fixture that supplies the value its assertion later checks is not a gate.** (Product + Methodology, **Medium**)
  - **Root cause:** `T06` wrote the disqualifier for exactly this shape — *"an already-lowercase fixture
    would pass either way and prove nothing"* — **as a per-task note rather than a standing rule.** It
    therefore did not travel one field over, and the same shape shipped for `email`: the stub was built
    with the same string the test then requested, so an implementation echoing its own input parameter
    passed the entire suite. `D1`, the spec's self-declared dominant defect class, shipped inside the
    spec that declared it.
  - **Evidence:** `validation-report.md` V-F1; `execution.md` — T06's disqualifier; the remediation's
    mutation proof (`expected:<[Jane.Smith@CGIAR.ORG]> but was:<[jane.smith@cgiar.org]>`).
  - **Standardization:** → P3 (local) · P4 (upstream)

- **KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-3 — In an append-only audit log, absolute intra-file line citations invalidate themselves.** (Product + Methodology, **Medium**)
  - **Root cause:** `execution.md` cites its own line numbers as evidence pointers. The file is
    append-only and grew to 3,284 lines, so **every append shifted every later citation** — including
    citations written in the same round that shifted them. Two audit rounds failed partly on this, and
    the load-bearing case was the pair of pointers offered as proof of the review-round derivation,
    which by then landed mid-blockquote.
  - **Evidence:** `execution.md` — T17 rounds 4 and 5; `validation-report.md` V-W10.
  - **Standardization:** → P5 (local) · P6 (upstream)

## Noted, not a lesson

*The recurrence feed for later retrospectives — each below the lesson bar on its own, but worth watching.*

- **`perl`/`sed` reported success on UTF-8 markdown without changing anything, four times** (three by the
  Leader, once in T15's mutation testing where CRLF defeated `$`-anchored substitutions and produced a
  false green). The rule *"UTF-8 markdown is edited with `Edit`; verify the mutation landed before
  believing the result"* was learned in-run and held afterwards. Watch for recurrence in a spec that
  does heavy document editing.
- **A number restated five times (0 → ≥6 → 21 → 23 → 25) left dependent derivations behind at every
  step.** Related to lesson 1 but distinct: the defect is a *derived* figure, not a quoted one.
- **Two gate tasks (T13, T16) ran Leader-inline although `execution.md` §1 explicitly withheld the T00
  inline exception from them.** T16's late independent audit passed with zero blocking findings, so no
  harm landed — but the deviation was justified with a basis the log had already denied.
- **A Leader paraphrase was presented as a verbatim quotation** and travelled through three documents
  including the parent runbook before validation caught it.
- **`/akili-test` never ran.** Tests were authored inside their migration tasks, so no artifact
  cross-checks assertion-to-clause — which is precisely how V-F1 and V-W3 survived to validation.

## Pending Items

> **Spec branch — nothing below was written.** All items await the apply phase on `staging`.

### P1

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `.agents/reviewer.md` (append-only) |
| Edit | Add: *"**Correction-note sweep.** A correction note is a self-declared index of a defect class. Harvest every note quoting its own former text (`previously read`, `this cell read`, `used to read`), grep that text across the spec family, and treat any hit outside the note itself as a surviving sibling."* |
| Severity | High |
| Status | pending |

### P2

| Field | Value |
|---|---|
| Kind | standardization |
| Target | **Upstream — AKILI methodology repository** (`/akili-validate`, Correction Closure) |
| Edit | Correction Closure currently sweeps *the corrected value*. Add the inverse sweep: *the set of correction notes is itself the index of every defect class the spec has already identified* — harvest and grep them. |
| Severity | High |
| Status | pending — recommended for upstreaming |

### P3

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `.agents/tester.md` (append-only) |
| Edit | Add: *"**A fixture must not supply the value its assertion later checks.** If the double is built from the same input the test then passes in, the assertion cannot distinguish a correct mapping from an echo of the request — it is not a gate. Make the double's value differ."* |
| Severity | Medium |
| Status | pending |

### P4

| Field | Value |
|---|---|
| Kind | standardization |
| Target | **Upstream — AKILI methodology repository** (`docs/specs/general-setup/task.md`, *Disqualifies the evidence*) |
| Edit | Note that a task-level *Disqualifies the evidence* clause describing a **fixture shape** generalises across sibling fields and requirements, and should be promoted to a spec-wide rule rather than repeated per task. |
| Severity | Medium |
| Status | pending — recommended for upstreaming |

### P5

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `docs/specs/general-setup/task.md` |
| Edit | Add: *"`execution.md` is append-only. Cite **section headings**, not absolute line numbers, for intra-file references — every append invalidates a line-number self-citation, including ones written in the same pass."* |
| Severity | Medium |
| Status | pending |

### P6

| Field | Value |
|---|---|
| Kind | standardization |
| Target | **Upstream — AKILI methodology repository** (execution-log template) |
| Edit | Same rule as P5, at the template level — it is a property of every append-only AKILI audit log, not of this project. |
| Severity | Medium |
| Status | pending — recommended for upstreaming |

### P7

| Field | Value |
|---|---|
| Kind | **factual-sweep** |
| Target | root `CLAUDE.md:189` **and** `AGENTS.md:284` |
| Edit | The claim *"3 JUnit 4 test files exist in the whole repository, one with its only test body commented out"* is **now false — measured: 12 files, 11 with `@Test`, 39 passing tests.** Replace the count, and keep the honest judgment in calibrated form: 39 tests over a codebase this size is still thin coverage, so a green run remains weak evidence — **but it is no longer meaningless**, and this spec's per-consumer suites *are* the gate for the behavior they cover. |
| Severity | **High** |
| Status | pending |
| Why it matters | This sentence is constitution. Left standing it trains every future agent that MARLO has no tests worth running — including the agents for children 2 and 3, who inherit `DirectoryServiceContractTest` as their reusable contract. |

### P8

| Field | Value |
|---|---|
| Kind | **factual-sweep** |
| Target | `marlo-web/src/main/resources/config/.gitignore` and `marlo-core/.../config/.gitignore` |
| Edit | Change the exact-filename rule to `marlo-dev.properties*`. |
| Severity | **Medium** *(downgraded from High — see the premise correction)* |
| Status | **applied 2026-08-31 on `staging-cognito-impl`**, not on `staging`. `.gitignore` is not one of the shared files the Default-Branch discipline protects (that list is root `CLAUDE.md` / `AGENTS.md`, `.agents/*.md`, packaged AKILI templates, `docs/trd/trd.md`), so the edit was in scope for a spec branch. Verified with `git check-ignore -v` against both paths. |
| Why it matters | Root `CLAUDE.md` Hard rule 12 forbids committing credential files; the `.gitignore` as written did not enforce it for a `.bak` derivative holding real database credentials. |
| **Premise correction (2026-08-31)** | This item recorded that `update-marlo-dev-java17.sh` generates `marlo-dev.properties.bak` **"on every local run"**. That is **false**, and the item was written without opening the script. `set_or_append_property` at `scripts/update-marlo-dev-java17.sh:27-29` runs `sed -i.bak …` and then `rm -f "${file}.bak"` on the very next line, so the derivative is deleted inside the same function call. **The real exposure is narrower:** a run interrupted between the two commands, a hand-run `sed -i.bak`, or an editor's own backup file. The fix stays correct as defense-in-depth — a `.gitignore` should not depend on a cleanup line in an unrelated script staying there — but its severity was inflated by an unverified claim. **Lesson: a kaizen item asserting what a script does must cite the lines it read.** |

### P9

| Field | Value |
|---|---|
| Kind | **trd-adr** |
| Target | `docs/trd/trd.md` — **no ADR number allocated** (numbering is an apply-time act) |
| Edit | Two additions declared by `design.md` §8, **withheld correctly from this branch but originally never recorded**, so the sync had no carrier: (1) **§2 domain modules** — one line recording that `marlo-data` gains a `security/directory` package; (2) **§14.5 `MO-2`** — change cost for directory work drops from **6 classes to 1**, evidence the scenario's measure is met. **No existing ADR is superseded.** |
| Severity | Medium |
| Status | pending |

### P10

| Field | Value |
|---|---|
| Kind | **guide-sync** |
| Target | root `CLAUDE.md` → `## Module Guides` |
| Edit | **No edit required — recorded as a verified zero.** The spec added a *package* (`marlo-data/.../security/directory/`), not a module, and its conventions do not diverge from the root. Confirmed on disk: **zero** `marlo-*/CLAUDE.md` files exist, so the table's *"no child guides exist"* row is still true. |
| Severity | Low |
| Status | pending — no-op, recorded so a later pass does not re-derive it |

---

## Carried from the spec's own records

`execution.md` carries **10 pending-for-`staging` items** and a *Carried, not fixed* table; both travel
with the archived spec. Two are surfaced above as P7–P9 because they are constitution-level or
security-adjacent. **One more must not be lost, and belongs to another child:**

> **`EXEC-106` (CP8) instructs child 3 to delete `ContactPersonAction.getADFilter` at `:58-71`. `T14`'s
> import deletion moved it to `:55-68`.** A child-3 agent following the runbook literally deletes three
> lines of the wrong code. Recorded in the runbook's `Execution State` block.
