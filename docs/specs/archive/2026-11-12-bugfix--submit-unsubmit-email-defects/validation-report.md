# Validation Report — Submit and unsubmit email defects

**Verdict: PASS with 2 WARN, both accepted follow-ups, 0 FAIL.**
- Every requirement and every BUT / AND IT MUST clause has code evidence and test or live evidence.
- On `5ea55f3626`: clean compile, full suite of 1175 tests with 0 failures, and no new Checkstyle violation in any of
  the 9 changed Java files.
- Both WARNs are the post-deploy acceptance on aiccratest, which a local run cannot replace: the T01 measurement and
  the real attachment.

## 1. Document Control

| Field | Value |
|---|---|
| Spec Path | `bugfix/submit-unsubmit-email-defects` |
| Spec ID | BUG-SUBMIT-EMAIL-001 |
| Type / Depth | Bug (Bug Mode) / Lite |
| Validated tree | branch `email-layout-default` at `5ea55f3626`. Spec start `ef5f5c641a`; 10 files, +1805 / −88 under `marlo-web` |
| Date | 2026-11-12 |
| Validator | Opus 5.5 (T3). Implementer `akili-implementer` (sonnet), so author ≠ auditor holds by model. The validator also led execution; independent review came from the `akili-reviewer` (opus, read-only) on every task |
| Inputs | `proposal.md`, `requirements.md`, `design.md`, `judgment.md` (APPROVED), `tasks.md`, `execution.md`, `test-report.md` |

## 2. Summary

| Area | Result | Note |
|---|---|---|
| Task completion | **WARN** | T02–T05 are `[x]` with evidence. T01 is deferred `[~]` with owner approval, and its measurement moves to the post-deploy log |
| File existence | PASS | Every file in design §2 changed as listed. Three new test files |
| Build integrity | PASS | Clean compile 2430 / 1063 / 118, MVN_EXIT=0. 1175 tests, 0 failures |
| Checkstyle | PASS | Each file is at or below the spec-start baseline (table in §5) |
| Requirement coverage | PASS | 7 FN and 3 NF requirements, every clause traced (§6). `test-report.md` is current and reused |
| Live acceptance | **WARN** | FN-001 and P-9 were observed locally (T05) but are accepted only by the post-deploy check |
| Design conformance | PASS | DD-1 to DD-9 implemented as amended, and every execute-time edit is recorded |
| Constitution impact | PASS | No module, boundary or guide change |
| Archive readiness | **Ready**, with the post-deploy check tracked as an open acceptance item | §12 |

## 3. Task Completion

| Task | Status | Evidence | Result |
|---|---|---|---|
| T01 Measure the report time | `[~]` deferred | `execution.md` T01: the owner had no aiccratest access. The measurement moves to the T05 post-deploy INFO lines; local evidence is 3.9–4.4 s | WARN (accepted) |
| T02 Bounded PDF fetch | `[x]`, 3 attempts | Attempt 1 PASS with advisory. Attempt 2 added the owner-approved deadline and failed review on per-read timeouts. Attempt 3 added a per-request budget and localhost socket tests, and passed | PASS |
| T03 Submit attaches the real PDF | `[x]`, 2 attempts | Attempt 1 hit a re-run MISMATCH on a 121-character line. Attempt 2 PASS. Two test rows were added later with owner approval (`REVIEW_SKIPPED`, predicate recorded) | PASS |
| T04 Unsubmit leader, greeting, no self-CC | `[x]`, 1 attempt | Reviewer PASS. Reds observed for ids 1082 and 16 | PASS |
| T05 Local end-to-end check | `[x]` local part | Reviewer PASS on attempt 2, after the evidence record was corrected. The post-deploy acceptance is tracked as open | PASS |

No HALT and no Pivot. Spec edits made during execute, each with a closure sweep:
- the CC rule (T04);
- the T01 deferral;
- the 30 s deadline and the per-request budget, owner-approved;
- the ≤ ~35 s bound and the broker-alarm known limit.

Budget: the LOC tripwire was raised twice by the owner (~480 → ~1200 → ~1350); 7 of 7 review rounds were used.

## 4. File Existence

| Design §2 row | File | Result |
|---|---|---|
| Fetch | `MicroserviceReportAction.java`: **0 removed lines** against the spec start; the existing download path is untouched | PASS |
| Flag | `ReportingSummaryAction.java`: the diff is exactly 2 fields, `generatePdfBytes()`, and the guarded `fetchClusterReportBytes` / `sendClusterReportQueueMessage` branch | PASS |
| Submit | `ProjectSubmissionAction.java`: `ApplicationContext` constructor (`:87, :92`), `createBean` (`:380`), `pdfAttached = pdf != null && pdf.length > 0` (`:365`), `global.untitled` (`:350`), null-safe WARN id (`:222`) | PASS |
| Unsubmit | `UnsubmitProjectAction.java`: `withoutRecipient` (`:181`), `email.greeting.everyone` (`:281`), `global.untitled` (`:284`). The only remaining `getId() != getCurrentUser().getId()` is inside the commented legacy block (`:232`) | PASS |
| i18n | `global.properties`: +1 line (`email.greeting.everyone=all`) | PASS |
| Tests | New `MicroserviceReportFetchTest`, `ReportingSummarySubmitFlagTest` and `ProjectSubmissionPdfStepTest`. Updated `ProjectSubmissionEmailTest` and `UnsubmitProjectEmailTest` | PASS |

## 5. Build Integrity

The validator re-ran the gates on `5ea55f3626` with no other build or agent running. It wiped
`marlo-data/target/generated-sources` too, because of the VS Code build race the Tester reported.

| Gate | Result |
|---|---|
| Clean compile + full suite (`mvn -o -pl marlo-web -am test`) | MVN_EXIT=0. Compiled 2430 / 1063 / 118. **Tests run: 1175, Failures: 0, Errors: 0, Skipped: 0** |
| Checkstyle against `ef5f5c641a` | `UnsubmitProjectAction` 2→2, `ProjectSubmissionAction` 1→0, `MicroserviceReportAction` 0→0, `ReportingSummaryAction` 67→67 (only the FileLength count line changes), test files 0→0, three new test files 0 |
| Hygiene | Changed files are clean except one LOG-ARGS at `ReportingSummaryAction:11436`, which was already in the base (a false positive: the argument is an exception) |
| i18n | 1 added line, no apostrophe or brace |
| Environment boot | Done in T05: a worktree build and `cargo:run` on a throwaway DB copy, with HTTP 200 on submit and on the summary download |

## 6. Requirement Coverage

`test-report.md` (PASS, no PRODUCT_BUG) is current for this tree and is the primary evidence. The validator
cross-checked its matrix against `requirements.md` §6 and the code (§4).

| Req | Clause | Task | Evidence | Result |
|---|---|---|---|---|
| FN-001 | THEN exact bytes, file name, type, planning copy; AND IT MUST be the fetched bytes | T02, T03 | `ProjectSubmissionEmailTest` (exact bytes, `getBytesPDF` empty), `ProjectSubmissionPdfStepTest` (real chain); T05: 397 KB and 341 KB PDFs attached locally | PASS. Post-deploy acceptance open (WARN-2) |
| FN-002 | THEN `.noPDF` with the submitted projectID and phaseID; BUT no attachment and no "attached" | T02, T03 | Fetch tests (b)–(g), (j)–(l); submit tests for null and empty bytes and the link | PASS |
| FN-003 | THEN nothing written to the response; AND IT MUST hold when the file is not ready | T02, T03 | Fetch (h); `PdfStepTest` with a recording response (M1 red `[sendError]`); T05: `200 text/html` | PASS |
| FN-004 | THEN TO = leader and named greeting for ids 1082 and 16; BUT not in CC | T04 | `UnsubmitProjectEmailTest` (a)–(c); T05 "Dear MARLO," | PASS (live recipients masked by the support-only rule) |
| FN-005 | THEN "Dear all,"; BUT never "Dear ," | T04, test | (d), (e), plus an inactive or other-phase leader (M4, M5); T05 row 18406 | PASS |
| FN-006 | "Untitled", never `"null"` (submit and unsubmit) | T03, T04 | Title null, blank, and no `ProjectInfo` | PASS |
| FN-007 | WARN with the unit id; email still sent with own texts | T03, T04, test | WARN tests; M6 (submit own texts) | PASS (unsubmit own-texts: accepted gap) |
| NF-001 | Bounded: 5 s connect, per-request budget, 30 s deadline, ≤ ~35 s | T02 | Fetch tests with a fake clock and localhost sockets (red at 3409 ms against a 500 ms budget, before the fix) | PASS. Real timing post-deploy (P-9) |
| NF-002 | Downloads keep streaming with 60 s; AND IT MUST: a download never enters the guarded line | T02, T03 | Additions-only diff and `setConnectionTimeout(60000)` intact; flag test; T05 download `%PDF-1.4` with `Downloading … from S3` only on the download request | PASS |
| NF-003 | i18n | T04 | 1 line reviewed | PASS |
| Owner rules | Notifications off → no PDF wait and no email; support-only | test, T05 | `PdfStepTest` / unsubmit notifications-off tests (M2, M3); T05 rows stored to = support, cc = bcc = NULL | PASS |

## 7. Linting & Code Quality

Checkstyle and hygiene are clean (§5). Error handling: every failure path of the fetch returns null and falls back to
the `.noPDF` copy, so the email is still sent. The submit's WARNs are null-safe. **No security or permission surface
changed.** The support-only and notification-disabled specificities are still enforced in `SendMailS` and the action
gates, and tests and T05 prove it.

### 4R advisory, carried forward (non-gating)

| Lens | Finding | Source |
|---|---|---|
| Resilience | A publish blocked by a RabbitMQ memory or disk alarm is not bounded, and the download path already has the same exposure. Slow response headers are bounded only per read. Fix later with a watchdog that calls `disconnect()` | T02 attempt 3, design §10 known limit |
| Resilience | `sendNotficationEmail()` runs `summaryPdf()` before its own `validateEmailNotification()`. This is safe today because `submitProject()` checks first. Moving the PDF step inside the send gate would protect against a future caller | Tester |
| Reliability | The cross-unit submit through a stale session uses the session unit's texts and file name (T05 row 18403). This behavior is pre-existing | T05 |
| Reliability | A null actor email or first name shows `"null"` in the unsubmit CC and `{1}`. `SendMailS` would merge CC entries when the TO is empty. Both are pre-existing | T04 |
| Readability | Fully qualified `java.net` / `java.io` names in the fetch; declarations at the end of the class; test import order | T02 |

## 8. Design Conformance

| Check | Result |
|---|---|
| DD-1: a per-submit `ReportingSummaryAction` via `createBean` | PASS. P-10 was verified at runtime (T05, 3 submits, 0 wiring errors) |
| DD-2: the download path untouched apart from one guarded line | PASS (§4) |
| DD-3: the fetch uses locals, never `ServletActionContext` and never `loadData` | PASS (test (h), review) |
| DD-4: short budget with a 30 s deadline, ≤ ~35 s | PASS, as amended in §10 |
| DD-5 to DD-9 | PASS |
| Proposal alignment (option B, Q1, Q5, Q6, option 1) | PASS. Non-goals are respected; nothing else was refactored |

**Cross-document figure check**

| Figure | Result |
|---|---|
| Premise Ledger "11 rows, 10 verified, 1 UNVERIFIED" | Matches §16: 11 rows, P-9 UNVERIFIED with local evidence, P-10 verified |
| Tests: 1168 + 7 = 1175; author tests 16 + 17 + 2 + 15 = 50 | Consistent across `execution.md`, `test-report.md` and this run |
| Budget: design §15 ~480 LOC / 7 rounds against actual | The overrun is recorded with two owner raises (`execution.md` Budget Adjustments); 7 of 7 rounds |
| Worst case "≈ 90 s" (old) | Superseded in design §10 by "about 30 s, at most about 35 s". The old value survives only in the amendment note and in the `execution.md` history |
| Proposal §10 option B "≤ 60 s connect + 10 s polling" | Stale against the final design (5 s connect and a 30 s deadline). The proposal is the frozen approved intent and judgment I-4 already noted it. **INFO**, no edit |
| requirements §1 "six defects" | Matches D1, D1b and D2–D5 |

**Design Review (UI):** not applicable. No screen changes; the submission page is untouched and T05 confirmed it renders.

## 9. Test Evidence Summary

| Suite | Tests | Result |
|---|---|---|
| Spec classes (fetch 16, submit 17, PDF step 4, flag 2, unsubmit 18) | 57 | PASS |
| Full `marlo-web` suite | 1175 | PASS (validator re-run) |
| Live local check (T05) | 4 scenarios | PASS |
| Falsifiers | Every new test was red under a mutation (T02–T04, test phase M1–M6) | PASS |

## 10. Agent Guide / Constitution Impact

No `## Constitution Impact` block. No module, boundary or public surface changed beyond the two actions' constructors,
which Struts and the tests construct. No root-guide claim was falsified. **Pending for `/akili-archive`:** the persona
refresh (`akili doctor --agents --fix` on `staging`, owner decision recorded in `execution.md`).

## 11. Remediation

| # | Level | Finding | Action | Status |
|---|---|---|---|---|
| WARN-1 | WARN | T01 deferred `[~]` | Read the `fetchClusterReportBytes` INFO elapsed-ms lines of the first aiccratest planning submits. Re-set the constants if the slowest successful fetch is near the 20 s polling budget | Accepted follow-up (owner, 2026-11-12) |
| WARN-2 | WARN | FN-001 and P-9 have local evidence only | Post-deploy check on aiccratest: a planning submit email with a non-empty PDF that opens, plus the normal submission page. This also answers Q2 | Accepted follow-up, tracked in T05 |
| INFO | — | Proposal §10 option B figures are stale | None; the proposal is a frozen record | — |
| A1–A5 | Advisory | The §7 advisories | Candidate follow-up proposals, not part of this spec | Recorded |
| Q4 | Open | Jira ticket | Owner decision | Open |

## 12. Archive Readiness Recommendation

**Ready to archive.** There is no FAIL. T02–T05 are `[x]`. T01 and the live attachment acceptance are accepted
post-deploy follow-ups. The tests cover every key scenario, and every change made during execution is recorded in the
spec documents.

```text
/akili-archive bugfix/submit-unsubmit-email-defects
```
