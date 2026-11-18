# Kaizen Entry — bugfix/submit-unsubmit-email-defects

## Document Control

| Field | Value |
|---|---|
| Spec Path | `bugfix/submit-unsubmit-email-defects` (archived to `docs/specs/archive/2026-11-12-bugfix--submit-unsubmit-email-defects/`) |
| Date | 2026-11-12 |
| Branch | `email-layout-default` (a spec branch; the apply-capable branch is `staging`) |
| Archive Run | 1 |
| Approval Mode | gated |

## Metrics

| Signal | Value | Source |
|---|---|---|
| Tasks executed | 5 (T01 deferred, T02–T05) | `tasks.md` |
| Reviewer FAIL rework attempts | 2 Reviewer FAILs (T02 attempt 2, T05 attempt 1) and 1 evidence re-run MISMATCH (T03 attempt 1). T02 attempt 2 was itself an owner-approved hardening from an advisory | `execution.md` T02, T03, T05 |
| HALTs / FATAL_FAILs | 0 / 0 | `execution.md` |
| Pivots | 0 | `execution.md` |
| PRODUCT_BUGs | 0 | `test-report.md` |
| Judgment-day severe findings | 0 confirmed. 1 suspect severe (S-1, the singleton race), verified real by the orchestrator. 4 confirmed warnings | `judgment.md` |
| Validation FAIL / WARN | 0 / 2 (both post-deploy acceptance, accepted) | `validation-report.md` |
| Review rounds | 7 of 7 | `execution.md` Summary |
| LOC | ~1 330 at the end of execute (+~475 at test) against an estimate of ~480. The tripwire was raised twice (~1 200, ~1 350) | `execution.md` Budget Adjustments |
| Tasks closed under `REVIEW_WAIVED` | 0 | `execution.md` |
| Tasks closed under `REVIEW_SKIPPED` | 1 (the owner-approved T03 test addition) | `execution.md` |
| Escaped defects (§3) | 0 in code. The test phase found one untested clause (FN-007 "own texts", M6) that the 17 author tests missed | `test-report.md` §3 |

## Lessons

- **KZ-bugfix--submit-unsubmit-email-defects-1 — A timeout figure in a design is a premise about the API's semantics, and it was never written as one.** (Product + Methodology, Medium)
  - **Root cause:** design §10 stated worst-case waits ("≈ 90 s", then "30 s at most") derived from the timeout
    *values*, assuming `HttpURLConnection` read timeouts bound the whole request. They apply to each read. No Premise
    Ledger row named the semantics or cited a source.
  - **What it cost:** T02 attempt 2 failed review on exactly this, and the T02 fake transport encoded the same wrong
    assumption (`min(latency, timeoutMs)`), so its tests could not catch it. The bound was rewritten twice.
  - **Evidence:**
    - `execution.md` — T02 attempt 1 ADVISORY (~145 s) and attempt 2 Reviewer FAIL (per-read timeout).
    - `design.md` §10, amended twice.
  - **Standardization:** → P1 (local), P2 (upstream).
- **KZ-bugfix--submit-unsubmit-email-defects-2 — A negative log assertion named a line the code never prints, so it could not fail.** (Product + Methodology, Medium)
  - **Root cause:** T05's scope and falsifier said "no `downloadPDFByURL` line". That is a method name, and no log
    statement emits it. The count was 0 in every possible run, and both the Implementer and the Leader quoted it as
    evidence. The discriminating line (`Downloading the … PDF from S3`) existed but was not chosen, because nobody
    grepped the source for the message before writing the check.
  - **Evidence:**
    - `tasks.md` T05 scope and falsifier.
    - `execution.md` T05 Reviewer FAIL (attempt 1).
  - **Standardization:** → P3 (local), P4 (upstream).
- **KZ-bugfix--submit-unsubmit-email-defects-3 — The VS Code Java language server writes into `target/` concurrently with Maven.** (Product, Low)
  - **Root cause:** `CLAUDE.md` *Concurrency* names concurrent Maven, Cargo and agents, but not the IDE's language
    server. It rebuilds into the same `target/` tree.
  - **What it cost:** a corrupted generated source (`FundingSourceSummaryMapperImpl.java: unclosed string literal`)
    and a false-red build in this spec's test phase. A `Can't find bundle global` flake in
    `changes/email-layout-default`'s test phase had the same suspected cause. Wiping only `target/classes` misses
    `generated-sources`.
  - **Evidence:**
    - `test-report.md` §2 "Build-race note".
    - Archived `changes/email-layout-default` `test-report.md` §2 "Flake noted".
  - **Standardization:** → P5.

Recurrence of an existing root cause → P6 (`digest-update`).

## Noted, not a lesson

- **The fake that assumed the property under test** (the T02 transport charged `min(latency, timeoutMs)`) is the same
  family as `KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-2` (a fixture supplies what its
  assertion checks). It is captured under lesson 1's evidence rather than as a separate recurrence. Watch for it in
  doubles of time and IO.
- **The evidence re-run caught a Checkstyle claim made from totals** (T03: 67→68 hidden in "delta 0" because one
  violation moved). Comparing sorted messages, not counts, closed it. The non-author re-run worked as designed.
- **The test phase paid for itself again.** Mutation M6 showed that 17 author tests left FN-007's "own texts" clause
  untested. This is the third spec in a row where `/akili-test` found a real gap.
- **The owner's safety questions shaped scope late.** Questions such as "is it fully safe" and "does it follow the
  support-only rules" became explicit test rows (notifications disabled, support-only live rows). A specify-time
  checklist line for the email specificities would have put them in the requirements up front.

## Pending Items

*Recorded on a spec branch. Apply on `staging`.*

### P1

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `docs/specs/general-setup/design.md` (Premise Ledger guidance) |
| Edit | A timeout, retry or deadline figure the design states is a premise about the API's semantics. Its row names, with a primary source, whether each timeout bounds a single call, a single read, or the whole request, and the worst case is computed from that semantics, not from the values alone. |
| Severity | Medium |
| Status | pending |

### P2

| Field | Value |
|---|---|
| Kind | upstream |
| Target | methodology |
| Edit | `/akili-specify` Premise Ledger: classify timeout, deadline and retry claims as `data-env` (runtime-bound, citation rule (f)), requiring the API's per-call versus per-read versus total semantics with a source. A worst-case bound derived from timeout values alone is a falsifiable premise (KZ-bugfix--submit-unsubmit-email-defects-1). |
| Severity | Medium |
| Status | pending |

### P3

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `docs/specs/general-setup/task.md` (Falsifier / Disqualifier guidance) |
| Edit | A check that asserts a log line is absent or present names the exact message the code emits, found by grepping the source for the logging call. A method or class name is not a log line. Quote the source line beside the check. |
| Severity | Medium |
| Status | pending |

### P4

| Field | Value |
|---|---|
| Kind | upstream |
| Target | methodology |
| Edit | `/akili-specify` Falsifiability: a negative log or output assertion must cite the emitting statement (`file:line`) of the message it counts. Otherwise it is a check that cannot fail (KZ-bugfix--submit-unsubmit-email-defects-2). |
| Severity | Medium |
| Status | pending |

### P5

| Field | Value |
|---|---|
| Kind | guide-sync |
| Target | root `CLAUDE.md` / `AGENTS.md`, section *Concurrency* (table) |
| Edit | Add a row: "**The IDE's Java language server (VS Code / JDT) also builds into `target/`.** Close it or pause its build during Maven runs, and wipe `marlo-data/target/generated-sources` with the classes when a build fails strangely. A race corrupted a generated mapper on 2026-11-12." |
| Severity | Low |
| Status | pending |

### P6

| Field | Value |
|---|---|
| Kind | digest-update |
| Target | KZ-changes--password-login-switch-2 |
| Edit | Recurrence: add `bugfix/submit-unsubmit-email-defects` as a source. The LOC estimate undercounted again (~480 estimated, ~1 330 at execute end, ~1 800 with the test phase). This time the drivers were tests and two owner-approved hardenings, and the tripwire fired twice. This is the third consecutive spec to overrun, after `password-login-switch` and `email-layout-default` (pending P4 there). Raise severity to Medium if not already raised. |
| Severity | Medium |
| Status | pending |

### P7

| Field | Value |
|---|---|
| Kind | guide-sync |
| Target | `.agents/*.md` (all four personas) |
| Edit | Run `akili doctor --agents --fix` (akili-specs v2.44.0). All four personas were reported `UNMARKED`, and the implementer persona lacks the 60-call checkpoint bound. The owner deferred this to `staging` on 2026-11-12 (`execution.md` Document Control). |
| Severity | Low |
| Status | pending |
