# Kaizen Entry — changes/email-layout-default

## Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/email-layout-default` (archived to `docs/specs/archive/2026-11-12-changes--email-layout-default/`) |
| Date | 2026-11-12 |
| Branch | `email-layout-default` (spec branch; the apply-capable branch is `staging`) |
| Archive Run | 1 |
| Approval Mode | gated |

## Metrics

| Signal | Value | Source |
|---|---|---|
| Tasks executed | 8 | `tasks.md` |
| Reviewer FAIL rework attempts | 1 (T08 ×1: evidence gaps on NF-003 and FN-010) | `execution.md` — T08, Attempt 1 |
| HALTs / FATAL_FAILs | 0 / 0 | `execution.md` |
| Review rounds | 9 against a budget of 10 | `execution.md` — Summary |
| LOC | ~909 against an estimate of ~380 (tests ~180 estimated). Tripwire raised twice by the owner: ~800 after T02, ~950 after T04 | `execution.md` — Budget Adjustment (after T02, after T04) |
| Pivots | 0 | `execution.md` |
| PRODUCT_BUGs | 0 | `test-report.md` |
| Judgment-day severe findings | 5 confirmed (JD-1 to JD-5) plus 2 suspect severe (JS-1, JS-2). All fixed before code | `judgment.md` |
| Spec amendments at execute | 2 (AC FN-011 at T05, AC FN-002 at T06) | `requirements.md` §9 Decision Log |
| Validation FAIL / WARN | 0 / 3 (all resolved in the documents) | `validation-report.md` |
| Tasks closed under `REVIEW_WAIVED` | 0 | `execution.md` |
| Tasks closed under `REVIEW_SKIPPED` | 0 | `execution.md` |
| Escaped defects (§3) | 0 | `test-report.md`, `validation-report.md` |

## Lessons

- **KZ-changes--email-layout-default-1 — An AC asserted words that the verbatim copy it governs does not contain.** (Product + Methodology, Medium)
  - Root cause: `requirements.md` fixed the email texts verbatim in Appendix A (and, for opt-out, the restored
    pre-shell values of DD-9). In the same document, the ACs asserted keywords about those texts. Nobody checked
    the keywords against the verbatim text before approval. AC FN-011 required "PMC" in the unsubmit email, but
    the approved A.1 unsubmit copy has none. AC FN-002 required the do-not-reply notice in every opted-out email,
    but the restored OTP html never carried it. Both contradictions were found only at execute. Each one stopped
    a task at a gate and cost an amendment with owner approval. Two judgment rounds did not catch either.
  - Evidence: `requirements.md` §9 — "Amended at execute (T05 gate…)" and "Amended at execute (T06 gate…)";
    `execution.md` — T05 ADVISORY "Spec: record the AC FN-011 vs Appendix A.1 tension", T06 entry.
  - Standardization: → P1 (local), P2 (upstream)

Recurrence of an existing root cause → P4 (`digest-update`).

## Noted, not a lesson

- **The design asserted a runtime premise without running it.** DD-7 said "submit runs `i18nFile`", which was false.
  Judgment caught it (JD-1) and the Premise Ledger was corrected (P-3). The gate worked, so this is not a lesson.
  Watch for live-path premises recorded as `location` claims without a live probe.
- **T08 attempt 1 failed on prose claims instead of quoted results** (NF-003 and FN-010). This is the same family
  as the `password-login-switch` note on T08: live-verification briefs should name the exact query or output to
  quote for each AC row up front.
- **A defect cause was described from the code's intent, not its behavior.** Requirements §4 attributes "Dear ,"
  to "the project leader is the user who unsubmits". The real mechanism is a `Long !=` reference comparison
  (`UnsubmitProjectAction:165-167, 276-281`) plus a missing leader name. It was found in `/akili-test` follow-up
  and routed to the bugfix proposal.
- **The `/akili-test` pass found 4 coverage gaps after execute.** Examples: the send paths were not driven, and
  the submit/unsubmit opt-out was untested. It closed them with 12 tests. It paid for itself again, as in
  `password-login-switch`.

## Pending Items

*Recorded on a spec branch. Apply on `staging`.*

### P1

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `docs/specs/general-setup/requirements.md` (Acceptance Criteria guidance) |
| Edit | When an AC asserts that a text contains or lacks a word or element, and the spec also fixes that text verbatim (a copy appendix, a restored legacy value), grep the verbatim text for each asserted word before approval and quote the hit in the AC. |
| Severity | Medium |
| Status | pending |

### P2

| Field | Value |
|---|---|
| Kind | upstream |
| Target | methodology |
| Edit | `/akili-specify` (and the judgment-day checklist): cross-check every keyword an AC asserts against any verbatim text the same spec fixes (copy appendix, restored values); a mismatch is a spec contradiction that otherwise surfaces only at execute (KZ-changes--email-layout-default-1). |
| Severity | Medium |
| Status | pending |

### P3

| Field | Value |
|---|---|
| Kind | guide-sync |
| Target | `reports/ai-context/role-assignment-email-catalog.md` (paragraph "As of A2-2484…", ~L36–40) |
| Edit | Replace "that still call the original `email.*` keys unchanged." with "that still call the original `email.*` keys unchanged — except project submit and unsubmit, which read `email.role.*` inside the shell since CHG-EMAIL-LAYOUT-001 (DD-4)." |
| Severity | Low |
| Status | pending |

### P4

| Field | Value |
|---|---|
| Kind | digest-update |
| Target | KZ-changes--password-login-switch-2 |
| Edit | Recurrence: add `changes/email-layout-default` as a source. The LOC estimate undercounted test volume again (~909 against ~380, tests estimated at ~180). The LOC tripwire did fire this time (raised after T02 and T04), so the tally half of the lesson held, while the estimate half recurred. Severity Low → Medium. |
| Severity | Medium |
| Status | pending |
