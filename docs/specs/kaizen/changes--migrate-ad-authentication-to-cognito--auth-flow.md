# Kaizen Entry — changes/migrate-ad-authentication-to-cognito/auth-flow

## Document Control

| Field | Value |
|---|---|
| Spec | `changes/migrate-ad-authentication-to-cognito/auth-flow` (`CHG-COGNITO-AUTH-001`) |
| Date | 2026-09-07 |
| Branch context | **Spec branch** (`staging-cognito-impl` ≠ default `staging`) — no shared file was written; every proposal below is a pending item awaiting the default-branch apply phase |
| Lessons | 2 (L1 Product, L2 Methodology) · 1 recurrence recorded as a digest update |
| Pending items | 5 — **all applied 2026-09-17 on `staging`** |

## Metrics

| Signal | Value | Method |
|---|---|---|
| Tasks | 25, all `[x]` | `tasks.md` status count |
| Automated tests | 207, 0 failures | measured, quiet window, two runs |
| Reviewer FAIL rework | **9 task ids carry a FAIL verdict** — T06, T07, T08, T09, T10, T11, T12, T18, T22 | grep over `execution.md` prose. **The validation report says eight.** Both are greps, not a counter; the signal that matters is that rework was frequent and recorded, not the exact integer |
| `PRODUCT_BUG` | **0** | A naive grep returns 2; both are **negations** (*"No PRODUCT_BUG was found"*, *"Not a PRODUCT_BUG"*). Counted by exclusion |
| Pivot Records | 0 | — |
| HALT / FATAL_FAIL | 0 | — |
| Validation | 0 FAIL · 3 WARN | `validation-report.md` |
| Live-environment findings | 7 (V-1, V-2, U-3, V-4, V-5, V-6, V-7) — **one was a code defect** | `execution.md` §32.6, §34–§45 |
| Implementers correcting their instructions | 6, of which 5 were the Leader's | `execution.md` §44.2, §48.6 and others |

**Not a clean run.** Lessons follow.

## Lessons

### L1 — A correction applied only where the finding pointed is not applied — **Product**

**Three times in one spec**, a correction was made to the instances a finding cited and the same claim survived
elsewhere:

| Occurrence | What survived | Caught by |
|---|---|---|
| T18 | FN-001's two *scenarios* were re-amended; the **preamble blockquote that frames them** still asserted the withdrawn text | The round-2 audit, a full FAIL round |
| Task ledger | T16 kept a duplicate `[ ]` status line and a `Not [x]` caveat contradicting its own closure; T15 was still `[ ]` after shipping | Counting pending work for the user |
| OQ-3 | The key count stayed at 7 after T15 added the eighth | `/akili-validate`'s cross-document figure check |

**Root cause:** the methodology already names *Correction Closure* (`/akili-specify`), but nothing in the
execute or specify loop **requires evidence that the sweep ran**. A correction and a swept correction are
indistinguishable in a diff, so the cheaper one happens.

**Evidence:** `execution.md` §40.6, §46, §49.2; `tasks.md` T18 round-2 FAIL.

**Standardization proposed (P3):** one line in `docs/specs/general-setup/requirements.md`'s amendment guidance
requiring an amendment to record the sweep it ran, in both directions.

### L2 — A test double that discards its arguments proves nothing, and looks identical to one that checks them — **Methodology**

`RecordingTokenExchangeClient` implemented `exchange(authorizationCode, redirectUri, codeVerifier)` and kept
**none** of the three. SEC-002 requires PKCE. The suite was green at 201 tests and **nothing proved the
verifier ever reached the exchange** — a regression passing `null` would have broken every CGIAR login with a
fully green build. Found by `/akili-test`'s traceability pass, not by any test.

**Root cause:** a double's *shape* satisfies the compiler and the reader. Nothing in the Tester contract asks
whether the arguments a collaborator receives are asserted anywhere. This is the cheapest possible instance of
this spec's dominant defect — *correct in isolation, dead through the real framework* — which recurred **nine**
times here.

**Evidence:** `test-report.md` §8 finding 17; `execution.md` §48.2.

**Standardization proposed (P4):** one line in `.agents/tester.md` — when a double stands in for a collaborator
the production code passes arguments to, at least one test must assert those arguments, or the omission is
recorded as a gap.

**Methodology target:** this belongs upstream in the AKILI Tester persona, not only in MARLO's copy.

## Noted, not a lesson

- **The Checkstyle gate is inert repo-wide** — the plugin pairing throws and `severity="warning"` would enforce
  nothing anyway. Pre-existing, repo-scoped, accepted as debt. Not this spec's root cause.
- **`LoginAction.java:518-528` is dead code that reads as a safety net** — it clears an authorization cache
  using principals `logout()` has already nulled. Pre-existing and untouched; FN-007 is satisfied by Shiro.
- **The counting error I made twice in one document** (§48.1) — hand-typed grades, then a "derived" script that
  read the wrong column. Real, and already fixed by a rule recorded in place: *a derived number is only derived
  once two independent tools agree.* Too specific to this session to be a durable lesson.

## Pending Items

*Recorded on a spec branch. **Applied 2026-09-17 on `staging`** — all five items are closed; P4 keeps an open upstream recommendation to the AKILI methodology repository.*

### P1 — `trd-adr` · severity: medium

**Target:** `docs/trd/trd.md` §8.4.
**Stale text:** `/api/*` authenticates "via tokens (e.g. `QAToken`)".
**Replacement:** `/api/**` is mapped to `authcBasic` through the same realm — `MarloShiroConfiguration.java:113`.
**Why:** the TRD contradicts the code on an authentication surface. Queued since T00; **not** an ADR
supersession, so no ADR number is allocated here.
**Status:** **applied 2026-09-17** on `staging`. The `/api/**` → `authcBasic` correction landed in `docs/trd/trd.md` §8.4. **Citation corrected at apply time:** the mapping is at `marlo-data/src/main/java/org/cgiar/ccafs/marlo/MarloShiroConfiguration.java` in `filterChainDefinitionMap`, not `:113` as this item recorded. No ADR number allocated — nothing is superseded.

### P2 — `factual-sweep` · severity: high

**Target:** root `CLAUDE.md:189` and the same claim in `AGENTS.md`.
**Stale text:** *"3 JUnit 4 test files exist in the whole repository … A green test run is not meaningful
verification evidence here."*
**Replacement:** 31 test files and 207 tests run under the default Maven lifecycle; a green run is meaningful
evidence for the code those tests cover.
**Nuance to preserve:** the adjacent claim that there is no explicit Surefire configuration **is still true** —
`marlo-parent/pom.xml` and `marlo-web/pom.xml` declare none. Correct the count and the conclusion, not that.
**Why high:** a guide is constitution. This sentence currently trains every future agent to discount the
repository's own test suite.
**Status:** **applied 2026-09-17** on `staging`, with the text reconciled at apply time. This item and `directory-abstraction` P7 targeted the same two lines with different figures (31/207 here, 12/39 there); **both were stale.** Measured on apply day: **36 test files, 35 carrying `@Test`, 271 `@Test` methods**, still no Surefire configuration. The applied text carries those figures and the calibrated judgment both items asked for.

### P3 — `standardization` (L1) · severity: medium

**Target:** `docs/specs/general-setup/requirements.md`, amendment guidance.
**Proposed line:** an amendment must record the closure sweep it ran — forward for the superseded value, backward
for documents citing the amended section. An unswept correction is relocated, not applied.
**Status:** **applied 2026-09-17** on `staging` as writing rule 8 in `docs/specs/general-setup/requirements.md`.

### P4 — `standardization` (L2) · severity: high

**Target:** `.agents/tester.md`.
**Proposed line:** when a test double stands in for a collaborator the production code passes arguments to, at
least one test must assert those arguments; otherwise record the omission as an explicit gap.
**Also:** upstream to the AKILI methodology repository — the root cause is the persona, not MARLO.
**Status:** **applied 2026-09-17** on `staging` — appended to `.agents/tester.md` under *Prove Behavior, Not Count*. The upstream recommendation to the AKILI methodology repository **remains open**; see the upstream backlog.

### P5 — `digest-update` · severity: raised

**Recurrence.** The sibling spec `directory-abstraction`'s lesson names the root cause *"every enumeration in
this run was scoped to classes a reviewer had already seen"*. **It recurred twice here**, both times in the
Leader's own work: T11b's *"there is no third route"* (a third existed in `ClarisaPublicAccesFilter:79`), and
`execution.md` §37.1 enumerating the nine `refuse()` callers while missing three refusals returning from the
shared tail — which became **V-7**.

**Action:** raise the severity of the existing root cause rather than open a duplicate lesson.
**Status:** **applied 2026-09-17** on `staging`. Merged into the `## Active Lessons` digest at `docs/specs/kaizen-log.md`: severity raised on the existing root cause and `auth-flow` added as a source spec, with the recurrence recorded. No duplicate lesson opened.

## Carried from the spec's own records

These survive the archive and live in `archive-summary.md` §7 with named owners:

- §27.4 items **8a, 8b, 8c** — NOT OBSERVED, dedicated environment.
- FN-001's UI clauses — blocked on a frontend runner, a TRD stack decision.
- FN-006 S12 — belongs to `directory-abstraction`.
- SEC-004 deployment secret store, SEC-002 callback allowlist — IBD/DevOps.
- **WARN-1 Checkstyle** — accepted repository-level debt, explicitly **not** PASS.
- The **independence limitation** on four Leader-written artefacts.
