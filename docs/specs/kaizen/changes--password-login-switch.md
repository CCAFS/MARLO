# Kaizen Entry — changes/password-login-switch

## Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/password-login-switch` (archived to `docs/specs/archive/2026-10-18-changes--password-login-switch/`) |
| Date | 2026-10-18 |
| Branch | `A2-2631-Implement-Center-Email-OTP-Authentication` (spec branch; apply-capable branch is `staging`) |
| Archive Run | 1 |
| Approval Mode | gated |

## Metrics

| Signal | Value | Source |
|---|---|---|
| Tasks executed | 8 | `tasks.md` |
| Reviewer FAIL rework attempts | 6 (T05 ×2, T06 ×1, T07 ×1, T08 ×2) | `execution.md` — Budget Tracking |
| HALTs / FATAL_FAILs | 0 / 0. Budget tripwires: 3 (T05, T07, T08), each raised by the user | `execution.md` — Budget Tripwire: T05; T07 attempt 3; T08 attempt 2 |
| Review rounds | 19 against a budget of 10 | `execution.md` — Budget Tracking |
| LOC | Production ~1,000 against an estimate of ~420; tests ~4,860 against ~700 | `validation-report.md` WARN-5 |
| Pivots | 0 | `execution.md` |
| PRODUCT_BUGs | 0 | `test-report.md` |
| Judgment-day severe findings | 5 (C-1 to C-4, N-1), all fixed before code | `requirements.md` Decision Log; `judgment.md` |
| Validation FAIL / WARN | 0 / 5 (WARN-1 to WARN-4 fixed, WARN-5 here) | `validation-report.md` |
| Tasks closed under `REVIEW_WAIVED` | 0 | `execution.md` |
| Tasks closed under `REVIEW_SKIPPED` | 0 | `execution.md` |
| Escaped defects (§3) | 0 | — |

## Lessons

- **KZ-changes--password-login-switch-1 — A decision record restated a conditional rule as shorthand, and the shorthand spread.** (Product + Methodology, Medium)
  - Root cause: DD-8 summarised the rule as "a Cognito account is refused under the override". The design §1 "Code" row says that a Cognito account is not admitted by the override but keeps the code switch and the exclusion as written. T08 copied the shorthand into the amendment notes, and the condition fell off: 7 places, then 4, then 2 more.
  - Evidence:
    - `execution.md` T08, attempt 1 FAIL ("drop the 'under the override' qualifier") and attempt 2 FAIL ("2 surviving siblings").
    - The attempt-2 ADVISORY names DD-8 as "the source of this defect class".
    - `validation-report.md` WARN-2.
  - Standardization: → P1 (local), P2 (upstream)
- **KZ-changes--password-login-switch-2 — The review-round budget was undercounted, and the LOC budget was never tallied.** (Product + Methodology, Low)
  - Root cause, two parts:
    - Design §18 budgeted one review round per task, plus one extra for each of the two hardest tasks. But every `full`-review security or cross-spec task (T05, T07, T08) took 3 attempts.
    - The `execution.md` Budget Tracking table has no LOC column, so the LOC tripwire could not fire.
  - This is the second spec in a row to overrun its round budget: `directory-abstraction` used 25 of ~20.
  - Evidence:
    - `execution.md` — Budget Tracking (19 / 10, three raises).
    - `validation-report.md` WARN-5.
    - `kaizen/changes--migrate-ad-authentication-to-cognito--directory-abstraction.md` — Metrics, "Review rounds consumed".
  - Standardization: → P3 (local), P4 (upstream)

Recurrence of an existing lesson → P5 (`digest-update`).

## Noted, not a lesson

- **A negative existence claim was "verified" by grepping one identifier.** The T08 pending note said the DD-6 `crpByEmail.do` ERROR "has no matching log call" after a search for `LOG`. The action logs through `this.logger` (`CrpByUserEmailAction.java:181`). Validation caught it (WARN-3). This is the same family as the "enumeration scoped to what was already seen" root cause: watch for it in absence claims.
- **The Leader's T08 brief put the environment cleanup inside attempt 1,** so no non-author could re-run the live probes until `/akili-test` rebuilt the copy. When a task's evidence is live, the cleanup belongs after the Reviewer verdict.
- **`/akili-test` paid for itself:** it found a falsifier that did not test what it claimed (a Hibernate native query cannot be cached, so falsifier A only proved "switch never read"), and two harness defects. Contrast `directory-abstraction`, where no test phase ran.

## Pending Items

*Recorded on a spec branch. Apply on `staging`.*

### P1

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `docs/specs/general-setup/design.md` (Decision Records guidance) |
| Edit | A decision record that restates a conditional rule quotes its full condition or cites the rule table by section; never a shorthand such as "X is refused under Y", which loses its condition when copied. |
| Severity | Medium |
| Status | pending |

### P2

| Field | Value |
|---|---|
| Kind | upstream |
| Target | methodology |
| Edit | Spec templates (design decision records, amendment notes): a restated conditional rule must carry its full condition or cite the normative rule table; shorthand restatements are a known source of correction-sweep failures (KZ-changes--password-login-switch-1). |
| Severity | Medium |
| Status | pending |

### P3

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `.agents/leader.md` (append, budget tracking) |
| Edit | The `execution.md` Budget Tracking table tallies every measure the design budget names (tasks, LOC, review rounds), not review rounds alone. When estimating, budget 2–3 review rounds for each `full`-review security or cross-spec task. |
| Severity | Low |
| Status | pending |

### P4

| Field | Value |
|---|---|
| Kind | upstream |
| Target | methodology |
| Edit | `/akili-execute` Budget Tracking: add a column for every budgeted measure (LOC included) so each tripwire can fire; `/akili-specify` budget guidance: a `full`-review security task averages ~3 review rounds, not 1 (two MARLO specs overran 25/20 and 19/10). |
| Severity | Low |
| Status | pending |

### P5

| Field | Value |
|---|---|
| Kind | digest-update |
| Target | `KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-1` |
| Edit | Add `changes/password-login-switch` as a source spec. Recurrence note: in T08 a regex family sweep found the quoted wording but missed 2 paraphrased siblings (a Decision Log opening sentence and a runbook support row). Only reading every added line by meaning closed it (`t08-cognito-line-review.out`, 66 lines). Refinement: a sweep greps the wording **and** reads each added line that mentions the rule's subject. |
| Severity | High (unchanged) |
| Status | pending |

### P6

| Field | Value |
|---|---|
| Kind | factual-sweep |
| Target | root `CLAUDE.md:201` and `AGENTS.md:285` ("Honest state of testing") |
| Edit | Stale: "36 test files … 35 of them carrying `@Test` methods, 271 in total". Measured 2026-10-18 on `A2-2631`: 88 test files, 85 carrying `@Test`, 990 `@Test` annotations, 983 tests run by `mvn -o -pl marlo-web -am test`. Re-measure on `staging` at apply time: the figures depend on which branches have merged. Keep the Surefire / JaCoCo / `-DskipTests` sentence, which is still true. |
| Severity | Medium |
| Status | pending |

### P7

| Field | Value |
|---|---|
| Kind | trd-adr |
| Target | `docs/trd/trd.md` §8.1 Authentication |
| Edit | No ADR is superseded, so no number is allocated. Add the sign-in method rule: per Global Unit, `system_password_login_active` (default on, read uncached) can turn password sign-in off on the login page. While it is off and the code key is valid, the emailed one-time code is the default method for every account without Sign in with CGIAR, and the password endpoints refuse a password before any credential check. Without a valid code key the password stays as last resort. One rule (`SignInMethodRule`) serves step 3, the password and code endpoints, and the welcome email. Apply together with the pending `CHG-OTP-LOGIN-001` §8.1 text (its `runbook.md`, TRD table). |
| Severity | Medium |
| Status | pending |
