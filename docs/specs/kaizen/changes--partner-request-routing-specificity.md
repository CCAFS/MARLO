# Kaizen Entry — changes/partner-request-routing-specificity

## Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/partner-request-routing-specificity` |
| Date | 2026-11-20 |
| Branch | revive-local-partner-request-approval (spec branch; apply-capable branch is `staging`) |
| Archive Run | 1 |
| Approval Mode | gated |

## Metrics

| Signal | Value | Source |
|---|---|---|
| Tasks executed | 5 | tasks.md |
| Reviewer FAIL rework attempts | 1 (T05 ×1: office-link screenshots taken with partner blocks collapsed) | execution.md — T05, attempt 1 |
| HALTs / FATAL_FAILs | 0 | execution.md |
| Pivots | 0 | execution.md |
| Budget tripwire | 1 (T02: 957 LOC against ~470; owner accepted ~1,050; final 1,047) | execution.md — Budget Tripwire: T02 |
| PRODUCT_BUGs | n/a (`test-report.md` absent) | — |
| Judgment-day severe findings | 2 rounds before approval (J-4, J-6, J-8, RA-1, RB-1…RB-7) | judgment.md, requirements.md Decision Log |
| Validation FAIL / WARN | 0 / 9 (W-5 fixed at validate, 8 open) | validation-report.md |
| Tasks closed under `REVIEW_WAIVED` (by flag) | 0 | execution.md |
| Tasks closed under `REVIEW_SKIPPED` (by task) | 0 | execution.md |
| Escaped defects (§3) | 0 | validation-report.md |
| Out-of-approval live calls | 3 CLARISA calls against 1 approved, all HTTP 401 | execution.md — T05 attempt 1 |

## Lessons

- **KZ-changes--partner-request-routing-specificity-1 — The LOC budget priced the tests as a fraction of the code, but a
  falsifier-backed gate map produces several times the code.** (Product + Methodology, Medium)
  - Root cause: design §15 estimated tests at ~270 LOC against ~130 LOC of Java. The gate map (§17) and the
    Disqualifiers then required about 20 behaviour cases, each with literal payload and log-text assertions, which
    came to 620 test LOC in T02 alone. The estimate was made per file, not per gate-map row, so the tripwire fired
    at T02 (2×).
  - Evidence: execution.md — Budget Tripwire: CHG-PARTROUTE-001-T02 (cause section); design.md §15 Budget.
    Recurrence: `changes--revive-local-partner-request-approval.md` "Noted, not a lesson — Budget estimate" (a 3.4×
    miss on falsifier-backed tests), now promoted.
  - Standardization: → P1 (local), P2 (upstream)
- **KZ-changes--partner-request-routing-specificity-2 — Editing `marlo-dev.properties` does not reconfigure a running
  app, so the "point at an unreachable host" step sent real requests.** (Product, Medium)
  - Root cause: T05 assumed that changing `clarisa.api.host` in `marlo-dev.properties` (or in `target/classes`) would
    redirect the running app. The cargo run reads the configuration packaged inside the war. Only
    `MAVEN_OPTS=-Dclarisa.api.host=…` plus a cargo restart took effect, so two further submits reached the real test
    host, against an owner approval of one call. The Implementer also under-reported the count (2 against 3); the
    Leader's log check caught it.
  - Evidence: execution.md — T05 attempt 1, "Live CLARISA calls: 3, not the 1 approved"; validation-report.md W-4.
  - Standardization: → P3

## Noted, not a lesson

- **Load-induced flaky test.** `OtpMailSenderTest` gave 2 failures and 1 error in a full suite under load average
  ~115–280, and 43 / 0 when run alone (execution.md — T03 evidence re-run). Outside this spec; it feeds recurrence if
  another spec hits it.
- **Collapsed-accordion screenshots.** The T05 attempt 1 FAIL came from capturing the Project Partners page with every
  partner block collapsed. `EXPANDABLE_BLOCKS_AGENT_INSTRUCTIONS.md` exists, but the brief did not name it for a
  screenshot task. One occurrence; below the bar.
- **Persona drift.** `akili doctor --agents` reports all 4 `.agents/` personas UNMARKED, and the implementer persona
  lacks the 60-call checkpoint bound (execution.md — Document Control). It is a constitutional fix for `staging`, not
  a lesson of this spec. No spawn here ran past 60 calls except the T05 attempt 1 Implementer (106 calls), which
  ended complete.
- **Factual sweep.** Root `CLAUDE.md` / `AGENTS.md` still say "36 test files … 271 `@Test`" and "~1040 files in
  marlo-web". The suite now runs 1385 tests and marlo-web compiles 1065 files. This is already queued as
  `changes--revive-local-partner-request-approval.md` P6 / P7 (`factual-sweep`, same targets), so it is not
  duplicated here.

## Pending Items

### P1

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `docs/specs/general-setup/design.md` — Budget section (tripwire for `/akili-execute`) |
| Edit | "Estimate test LOC per gate-map row, not per file: count the behaviour cases the gate map and Disqualifiers require and price each at ~25–30 LOC (literal assertions included). In MARLO, falsifier-backed suites have run 2–3.4× the production code." |
| Severity | Medium |
| Status | pending |

### P2

| Field | Value |
|---|---|
| Kind | upstream |
| Target | methodology |
| Edit | `/akili-specify` Step 2.4 (budget): derive the test-LOC estimate from the gate map's case count instead of a ratio to production code; a spec whose gate map lists N behaviour cases budgets roughly N × 25–30 test LOC. |
| Severity | Medium |
| Status | pending |

### P3

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `docs/infrastructure.md` §6.1 Local Environment (and the mirrored note under `## Concurrency` in root `CLAUDE.md` / `AGENTS.md` if preferred) |
| Edit | "A running `cargo:run` reads `marlo-dev.properties` from the packaged war: editing the source file or `target/classes` changes nothing. To override one value for a live check, restart cargo with `MAVEN_OPTS=-D<key>=<value>`. A live check that must not reach an external system sets that override **before** the first submit." |
| Severity | Medium |
| Status | pending |
