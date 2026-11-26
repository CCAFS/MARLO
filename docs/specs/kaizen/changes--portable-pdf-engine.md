# Kaizen Entry — changes/portable-pdf-engine

## Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/portable-pdf-engine` (archived to `docs/specs/archive/2026-11-25-changes--portable-pdf-engine/`) |
| Date | 2026-11-25 |
| Branch | `feature/local-pdf-report-engine` (spec branch; apply-capable branch is `staging`) |
| Archive Run | 1 |
| Approval Mode | gated |

## Metrics

| Signal | Value | Source |
|---|---|---|
| Tasks executed | 6 | tasks.md |
| Reviewer FAIL rework attempts | 3: T02 ×1 (evidence re-run mismatch, CRLF), T03 ×1 (DD-6 owner compare), T04 ×1 (FN-007 platform in WARN; OPS-001 provenance) | execution.md — T02, T03, T04 entries |
| HALTs / FATAL_FAILs | 0 / 0. Budget tripwires: 3 (T03, T04 resume, T05 close), each raised by the owner | execution.md — Budget running totals |
| Pivots | 0 formal. 1 Pivot-lite: T05 reopened after two PASSes for a spec tension (insertion point and family name) | execution.md — T05 "Gate" and "Decision" |
| PRODUCT_BUGs | 0 | test-report.md |
| Judgment-day severe findings | not run | — |
| Validation FAIL / WARN | 0 / 7 (W1 accepted; W2–W7 fixed) | validation-report.md §2, §13 |
| Tasks closed under `REVIEW_WAIVED` (by flag) | 0 | execution.md |
| Tasks closed under `REVIEW_SKIPPED` (by task) | 0 | execution.md |
| Escaped defects (§3) | 0 against a `REVIEW_SKIPPED` task. 2 found after close in reviewed tasks: A-2 (test errors instead of skipping on hosts with no build, T02 code) and W4 (stale `marlo-test.properties` comment) | validation-report.md §7, §11 |
| Budget | LOC ~2,066 against ~650 planned (tests ~1,300 against ~400 estimated); review rounds 14 against 9 planned | design.md §18; execution.md — Summary |
| Drift attributable | n/a (no `drift-*.md` report) | docs/specs/audits/ |

## Lessons

- **KZ-changes--portable-pdf-engine-1 — A trusted transform added after the sanitizer was placed by text search over sanitized output, so content the sanitizer kept as text could decide where it landed.** (Product + Methodology, Medium)
  - Root cause: `tasks.md` T05 Scope said "inserted before `</head>`", and `design.md` §13 required only that the
    fonts come "after the sanitizer". Nothing in the design required the insertion point to be independent of
    template or payload content. A head `<style>` holding `</head><img onerror>` survives `sanitize()` as raw text,
    and a first-`</head>` search lands inside it and turns it into markup. Both attempt-1 Reviewers PASSed. The hole
    surfaced only as RISK advisories, which reopened a closed task through a spec amendment and cost 2 more review
    rounds.
  - Evidence: execution.md — T05 "Attempt 1 — Reviewer verdicts" (ADVISORY RISK, B and A) and "Gate (2026-11-25)";
    requirements.md §9 Decision Log 2026-11-25 (T05 amendment); test-report.md §3 regression
    `aHeadEndTagInsideAHeadStyleOrMeta…`.
  - Standardization: → P1 (local template), P2 (upstream).

## Noted, not a lesson

- **The closure sweep missed a quoted log text (recurrence of KZ-…-auth-flow-1).** The 2026-11-24 amendment added
  the old-glibc case to FN-007 ("one line that names the platform"). Its sweep never touched the WARN text quoted in
  `design.md` §11. The Implementer copied that quote exactly and failed T04 attempt 1. → recorded as `digest-update`
  P3.
- **The budget missed test volume (recurrence of KZ-changes--password-login-switch-2).** Tests were ~1,300 lines
  against ~400 estimated (≈1.7 test lines per main line). The tripwire fired three times. Earlier specs overran the
  same way (`changes--email-layout-default`, `bugfix--submit-unsubmit-email-defects`). → recorded as `digest-update`
  P4.
- **An enumeration missed a site (pattern of KZ-…-directory-abstraction-1).** W4: the search for Linux-only
  assumptions missed the `marlo-test.properties` comment, so it fell outside the "fix in the same file" scope.
  Single instance this spec; feeds the recurrence check.
- **A test change escaped review.** A-2: `theRealResourceExtractsToTheKnownBuild` ran extraction before its `Assume`
  after T02, so it errors instead of skipping on a host with no build. Two T02 Reviewers missed it, because nothing
  runs on such a host in this checkout.
- **A spawn ran far past the checkpoint bound.** The T06 Implementer used 103 tool calls with no checkpoint. Cause:
  the known persona drift (MARLO's adapted `.agents/implementer.md` predates the 60-call bound). → P5.
- **Real inputs lived only in a scratchpad.** The CHG-LPRE real payloads used for parity survived only by chance in
  an earlier session's scratchpad. A later parity re-check could lose them.

## Pending Items

### P1

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `docs/specs/general-setup/design.md` (security section guidance) |
| Edit | When a design adds a trusted transform to content that has already passed a sanitizer (inserting markup, fonts or scripts), it states the insertion anchor and shows that the anchor cannot be moved by template or payload text: a fixed position such as document start or after a leading DOCTYPE, never a text search over sanitized output. |
| Severity | Medium |
| Status | pending |

### P2

| Field | Value |
|---|---|
| Kind | upstream |
| Target | methodology |
| Edit | `/akili-specify` design template, security section: "A trusted transform applied after a sanitizer must name a content-independent insertion anchor; a text search over sanitized output is a sanitizer bypass, because raw-text contexts (`<style>`, attribute values) survive sanitization." Source: KZ-changes--portable-pdf-engine-1. |
| Severity | Medium |
| Status | pending |

### P3

| Field | Value |
|---|---|
| Kind | digest-update |
| Target | KZ-changes--migrate-ad-authentication-to-cognito--auth-flow-1 |
| Edit | Add source spec `changes/portable-pdf-engine` and raise severity Medium → High. Recurrence note: "the forward sweep must include strings the spec quotes from code (log messages, error texts) — the FN-007 amendment's sweep skipped the WARN quoted in design §11 and the Implementer reproduced the stale quote into a Reviewer FAIL (T04 attempt 1)." |
| Severity | High |
| Status | pending |

### P4

| Field | Value |
|---|---|
| Kind | digest-update |
| Target | KZ-changes--password-login-switch-2 |
| Edit | Add source spec `changes/portable-pdf-engine` and raise severity Low → Medium. Recurrence note: "LOC budgets keep omitting test volume — here tests were ~1,300 lines against ~400 estimated (≈1.7 test lines per main line, matrix and seam-heavy tasks); size test LOC per task from its Tests list rather than a flat share." |
| Severity | Medium |
| Status | pending |

### P5

| Field | Value |
|---|---|
| Kind | guide-sync |
| Target | `.agents/implementer.md`, `.agents/leader.md`, `.agents/reviewer.md`, `.agents/tester.md` |
| Edit | Merge the akili-specs v2.45.0 persona sections (checkpoint protocol with the 60-call / 3-cycle bound and the seven checkpoint fields; bounded reads) into MARLO's adapted personas, through the constitutional change process. Recorded in this spec's execution.md Document Control ("Known persona drift", 2026-11-24). |
| Severity | Medium |
| Status | pending |

### P6

| Field | Value |
|---|---|
| Kind | trd-adr |
| Target | `docs/trd/trd.md` |
| Edit | Decision (supersedes: none — amends the pending CHG-LPRE decision in `docs/specs/kaizen/changes--local-pdf-report-engine.md` P7; apply together, no ADR number here): "The local PDF engine ships the PhantomJS 2.1.1 build for Linux x86_64, macOS (Intel and Apple Silicon) and Windows x86/x86_64 in the WAR, chosen by OS and architecture together; `pdf.phantomjs.path` is an optional override only. On Linux x86_64 without font libraries (glibc ≥ 2.36) MARLO extracts seven verified Debian 12 libraries and gives `LD_LIBRARY_PATH` to the PhantomJS child only; on macOS and Windows the DejaVu fonts are placed in the page after the sanitizer. Any other platform fails fast with the generic 500." Source: archive/2026-11-25-changes--portable-pdf-engine design DD-1…DD-7. |
| Severity | Medium |
| Status | pending |
