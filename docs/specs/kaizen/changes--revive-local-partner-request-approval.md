# Kaizen Entry — changes/revive-local-partner-request-approval

## Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/revive-local-partner-request-approval` |
| Date | 2026-11-20 |
| Branch | revive-local-partner-request-approval |
| Branch Context | spec. No `Integration Branch:` pin; `Default Branch: staging` |
| Archive Run | 1 |
| Approval Mode | gated |

## Metrics

| Signal | Value | Source |
|---|---|---|
| Tasks executed | 12 (T07 and T10 reopened once each by owner decision) | tasks.md, execution.md |
| Reviewer FAIL rework attempts | 9 | execution.md |
| HALTs / FATAL_FAILs | 0 / 0 | execution.md |
| Pivots | 0 | execution.md |
| Budget tripwires | 1 (T03: 2,269 LOC vs ~1,650). Ceiling raised twice, to ~5,000 and then ~5,800 | execution.md, *Budget Tripwire* |
| PRODUCT_BUGs | n/a (no `test-report.md`) | — |
| Plan gaps found at gates | 3, all fixed with the owner's approval: PR 1 POST-only vs a GET client (T05 gate); edit field-name mismatch, pre-existing (T11a); requester-website XSS, pre-existing (validate F2) | execution.md, validation-report.md |
| Validation FAIL / WARN | First pass 3 / 6, re-validation 0 / 7 | validation-report.md §2, §13 |
| Tasks closed under `REVIEW_WAIVED` (by flag) | 0 | execution.md |
| Tasks closed under `REVIEW_SKIPPED` (by task) | 0 | execution.md |
| Escaped defects (§3) | 0 (no `REVIEW_SKIPPED` tasks) | — |
| Runtime events | 1 provider/session death (host restart, T11a), recovered at rung 3 | execution.md, T11a |

The nine rework attempts:

| Task | Attempts | Cause |
|---|---|---|
| T03 | 1 | OPS-001 request ids missing from the log |
| T08 | 1 | Cache-buster date |
| T09 | 1 | Cache-buster letter suffix (implicit FAIL found by the Leader's re-run) |
| T11a | 2 | Evidence gap; stale verdict lines |
| T11b fixes | 1 | Acronym scope and §5.2 |
| T12 | 1 | Docs contradicted the code |
| Validate F2 | 1 | Missed sink in the popup script |
| T11b evidence | 1 | AC readings |

## Lessons

- **KZ-changes--revive-local-partner-request-approval-1 — The Leader's brief stated the cache-buster convention from memory and caused two rework attempts.** (Product, Medium)
  - **Root cause:**
    - The T08 brief said "`?20261120` or later" and the T09 brief prescribed a letter suffix (`?20261119c`).
    - The `marlo-verify` Gate 3 script accepts only `?YYYYMMDD` (the edit date) or a same-day `-N` suffix.
    - The convention lives in the gate script and a memory note, not in the root guides, so the brief paraphrased it wrongly.
    - An earlier `?20261119b` (T07 reopen) passed review unnoticed.
  - **Evidence:** execution.md — T08 attempt 1 (Reviewer FAIL, Gate 3) and T09 attempt 1 (implicit FAIL: `STALE … ?20261119c`).
  - **Standardization:** → P1.

- **KZ-changes--revive-local-partner-request-approval-2 — A PR split by layer shipped a server contract change ahead of its client.** (Product + Methodology, Medium)
  - **Root cause:**
    - `tasks.md` put T03 / T05 / T07, which make the five review routes POST-only, in PR 1.
    - It put the client changes (T08 / T09: the AJAX `type: 'POST'` and the accept form) in PR 2.
    - Nothing in task decomposition checks that each PR still works against the client it ships with.
    - The gap surfaced only at the T05 gate, and the T11a live run then found the edit form's field names never matched the action.
  - **Evidence:**
    - execution.md — T05 *Plan gap found at this gate*.
    - T11a FN-007 row (`t11a-ui-edit.out`).
    - `requirements.md` Decision Log entries "(execute, T05 gate)" and "(execute, T11a gate)".
  - **Standardization:** → P2 (local template) and P3 (upstream).

- **KZ-changes--revive-local-partner-request-approval-3 — Live-probe evidence committed to a public repository leaked the owner's email address.** (Product, Medium)
  - **Root cause:**
    - `probes/t04-c-ordering.out` captured raw `email_logs` output containing the local `email.notification` address, which is the owner's mailbox.
    - It was committed (`cad7c5d4e4`). No gate scans probe files for personal data before a commit.
    - The leak was caught by a Leader grep after T11a and fixed by amending the unpushed commit.
    - From then on, every brief and Leader re-run added a privacy grep by hand.
  - **Evidence:** execution.md — T11a, *Leader privacy check of the probe files*, and *Owner decisions at the T11a gate*, item 2.
  - **Standardization:** → P4.

## Noted, not a lesson

- **Sink sweeps scoped too narrowly.** A security sweep was scoped to the files already named (`marloInstitutions.js` and the FTL templates) and missed `allInstitutionsPopup.js` on the same page. The same root cause appeared in T12, where the docs generalised "every action calls `isHttpPost()`". This is a recurrence of `KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-1`. → P5 (`digest-update`).
- **Budget estimate.** The estimate missed the cost of falsifier-backed tests by about 3.4× (tests 1,304 LOC against ~380 by T03). Feeds the next budget recurrence check.
- **Two tooling false readings.** The `impeccable detect` launcher hit `EAGAIN` in T08 and was recorded "Unevaluated", although the engine was ready (validate confirmed `ready`, 0.1.18). zsh did not word-split a file list, which produced empty Checkstyle and grep runs twice (Leader at validate; T12 Implementer). Both were caught; below the lesson bar.
- **Incomplete clean-compile wipe.** The documented wipe list (`target/classes`, `test-classes`) was not enough. `maven-status` and the `generated-*` folders also had to be wiped to avoid stale-class and MapStruct read errors (T01, T11b). → P7 (`factual-sweep`).

## Pending Items

### P1

| Field | Value |
|---|---|
| Kind | standardization |
| Target | root `CLAUDE.md` (and the mirrored `AGENTS.md`) — `## Agent-Lean Verification Commands` table, add a row |
| Edit | `\| Cache-busting \| .claude/skills/marlo-verify/scripts/cache-bust-check.sh \| **Required** when CSS/JS change: the referencing FTL carries ?YYYYMMDD = the edit date; a second bump the same day uses -N (-1, -2), never a letter or a future date. Quote the script's ok line \|` |
| Severity | Medium |
| Status | pending |

### P2

| Field | Value |
|---|---|
| Kind | standardization |
| Target | `docs/specs/general-setup/task.md` — task decomposition / PR split guidance |
| Edit | When tasks are split across PRs, each PR must work against the client it ships with: a server contract change (HTTP method, parameter names, response shape) whose client update lives in a later PR needs a bridge task in the same PR, or the split is invalid. |
| Severity | Medium |
| Status | pending |

### P3

| Field | Value |
|---|---|
| Kind | upstream |
| Target | methodology |
| Edit | `/akili-specify` tasks decomposition: add a "PR shippability" check. For every PR boundary, list the contracts the PR changes and confirm each consumer that ships in the same PR still works; a consumer updated only in a later PR requires a bridge task. (Source: KZ-changes--revive-local-partner-request-approval-2) |
| Severity | Medium |
| Status | pending |

### P4

| Field | Value |
|---|---|
| Kind | standardization |
| Target | root `CLAUDE.md` / `AGENTS.md` — `## Hard rules` item 12 (credential files) |
| Edit | Append: "Live-probe evidence under `docs/specs/**/probes/` is committed: before each commit, grep it for the local `email.notification` address, personal emails or names, home or scratchpad paths, and DB credentials; write `<notification mailbox>`." |
| Severity | Medium |
| Status | pending |

### P5

| Field | Value |
|---|---|
| Kind | digest-update |
| Target | KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-1 |
| Edit | Add `changes/revive-local-partner-request-approval` as a source spec. Recurrence: a validate-time security sink sweep scoped to the files already named missed `allInstitutionsPopup.js` on the same page (validation-report.md §13, F2 attempt 1), and T12 docs generalised "every action calls `isHttpPost()`". Severity stays High. |
| Severity | High |
| Status | pending |

### P6

| Field | Value |
|---|---|
| Kind | factual-sweep |
| Target | root `CLAUDE.md` (line ~201) and `AGENTS.md` (line ~285): "Honest state of testing in MARLO (measured 2026-09-17)" |
| Edit | Stale: "36 test files exist under `marlo-web/src/test/java/`, 35 of them carrying `@Test` methods, 271 in total." Replace with: "146 test files exist under `marlo-web/src/test/java/` with 1,367 `@Test` methods (measured 2026-11-20; a full `-am` run executes 1,360)." Keep the rest of the paragraph. |
| Severity | Low |
| Status | pending |

### P7

| Field | Value |
|---|---|
| Kind | factual-sweep |
| Target | root `CLAUDE.md` (line ~194) and `AGENTS.md` (line ~278): Compile gate command |
| Edit | Stale: the wipe list `rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes` alone. Replace with `rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes marlo-web/target/generated-sources marlo-web/target/generated-test-sources marlo-web/target/maven-status marlo-data/target/maven-status`. Without the last four, stale classes and MapStruct `generated-sources` read errors recurred (execution.md T01, T11b). |
| Severity | Low |
| Status | pending |

### P8

| Field | Value |
|---|---|
| Kind | guide-sync |
| Target | `docs/specs/domain/admin/agent-context.md` line ~317, and `reports/ai-context/role-assignment-email-catalog.md` line ~508 |
| Edit | Repoint the spec citations to the archived folder: `changes/revive-local-partner-request-approval/probes/t04-*.out` → `archive/2026-11-20-changes--revive-local-partner-request-approval/probes/t04-*.out`. The catalog's `(changes/revive-local-partner-request-approval; …` gets the same archive path. The spec ID (CHG-PARTREQ-001) and the reference at agent-context line ~276 stay valid as text. |
| Severity | Low |
| Status | pending |
