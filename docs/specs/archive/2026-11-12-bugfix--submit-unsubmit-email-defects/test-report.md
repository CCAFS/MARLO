# Submit and unsubmit email defects — Test Report

**Status: PASS.** Every acceptance-criterion clause in `requirements.md` §6 has test evidence or a stated evidence type,
including each BUT and AND IT MUST clause. The Tester added 7 tests to close four gaps. Each new test was first seen
red under a deliberate production mutation. No product defect was found. The full `marlo-web` suite is green: 1175
tests, 0 failures, 0 errors.

## 1. Document Control

| Field | Value |
|---|---|
| Spec Path | `bugfix/submit-unsubmit-email-defects` |
| Spec ID | BUG-SUBMIT-EMAIL-001 |
| Deliverable / Depth | software / Lite (Bug Mode) |
| Code under test | branch `email-layout-default`, `32a725b51c`, plus the uncommitted test files below |
| Date | 2026-11-12 |
| Leader | Opus 5.5 (T1) |
| Tester | `akili-tester` (opus). The Implementer is `akili-implementer` (sonnet), with no model escalation in `execution.md`, so author ≠ tester holds |

## 2. Summary

| Item | Value |
|---|---|
| Suites | 1, backend unit. Integration was proven live in T05 and is cited here, not repeated. There is no frontend or E2E suite, because the change has no UI |
| Testers spawned | 1 (Deployment Rule: Lite, one suite) |
| Tests added | 7: new `ProjectSubmissionPdfStepTest` (4) and `UnsubmitProjectEmailTest` (+3) |
| Author tests cited, not rewritten | `MicroserviceReportFetchTest` (16), `ProjectSubmissionEmailTest` (17), `ReportingSummarySubmitFlagTest` (2), `UnsubmitProjectEmailTest` (15) |
| Full suite (Leader re-run) | `rm -rf …/target/classes …/test-classes marlo-data/target/classes marlo-data/target/generated-sources && mvn -o -pl marlo-web -am test` → MVN_EXIT=0. Compiled 2430 / 1063 / 118. **Tests run: 1175, Failures: 0, Errors: 0** (1168 + 7) |
| Checkstyle | Both changed test files: HEAD 0, tree 0, delta 0. The new file carries the GPL header |
| Production code | Unchanged (`git diff --quiet -- marlo-web/src/main marlo-data/src/main` exits 0) |
| Product bugs | None |
| Skills / effort | Tester: `marlo-verify`, `tdd`; effort high |

**Build-race note.** The VS Code Java language server writes into `target/` while Maven runs. During the Tester's run,
one build failed on a corrupted generated source (`FundingSourceSummaryMapperImpl.java: unclosed string literal`).
The Tester did not count it as a red and re-ran every mutation in a temporary worktree, which has since been removed.
The Leader re-run also wiped `marlo-data/target/generated-sources`.

## 3. Backend Unit Tests

### New tests

| Test | Proves |
|---|---|
| `ProjectSubmissionPdfStepTest#aPlanningSubmitWithAPdfAttachesTheMicroserviceBytesAndLeavesItsResponseUntouched` | FN-001 and FN-003 through the real chain `summaryPdf()` → `generatePdfBytes()` → `generateAndSendJson()`. Only the microservice is a double |
| `…#aPlanningSubmitWhosePdfIsNotReady…` | FN-002 and FN-003 when the file is not ready: no 404, header or body on the submit response |
| `…#withNotificationsDisabledASubmitNeitherWaitsForThePdfNorSends` | With notifications disabled (`crp_enable_email_notification`), no summary is built and nothing is sent |
| `…#aFailedUnitTextLookupSendsTheEmailWithTheActionsOwnTexts` | FN-007: "the action's own texts" on submit |
| `UnsubmitProjectEmailTest#withNotificationsDisabledForTheUnitNoUnsubmitEmailIsSent` | No unsubmit email when notifications are disabled |
| `…#aLeaderWhoIsNotActiveInTheCurrentPhaseCountsAsNoLeader` | FN-005: "no **active** PL" |
| `…#aLeaderOnlyInAnotherPhaseCountsAsNoLeader` | FN-005: "in the **current phase**" |

### Reds (each mutation was restored byte-identical)

| # | Mutation | Red, verbatim |
|---|---|---|
| M1 | `summaryPdf()` goes through `generateAndSendJson(); return getBytesPDF();` | `the submit response receives no call from the PDF step expected:<[]> but was:<[sendError]>` |
| M2 | Notification guard removed from `submitProject()` | `no summary is built, so no PDF wait expected:<0> but was:<1>` |
| M3 | Unsubmit `validateEmailNotification` returns true | `no unsubmit email when the unit disabled notifications expected:<0> but was:<1>` |
| M4 | `Project.getLeaderPerson`: `isActive()` replaced by `!= null` | `an inactive leader is not addressed expected:<[]> but was:<[ana@unit.test]>` |
| M5 | `Project.getLeaderPerson`: phase filter dropped | `a past-phase leader is not addressed expected:<[]> but was:<[ana@unit.test]>` |
| M6 | The submit `unitTexts()` fallback returns the base provider instead of `this` | `the subject comes from the action's own provider expected:<[own:submit.email.subject]> but was:<[[MARLO] AICCRA project C77 has been submitted]>`. All 17 author tests stayed green under this mutation, so it was a real gap |

## 4. Frontend Unit Tests

Not applicable. The change touches no JavaScript, CSS or FreeMarker.

## 5. Integration Tests

T05 ran live on a throwaway DB copy with mail restricted to the support mailbox. See `execution.md` § T05 for the
evidence:
- The planning submit returned `200 text/html` and attached real PDFs (397 KB and 341 KB). Each fetch took about
  4 s and came from its own summary instance; P-10 was verified at runtime.
- Unsubmit greeted the leader, or "all" when there was no leader.
- The download stayed on its own path, with a `Downloading … from S3` line only on the download request.

The live check is not repeated here.

## 6. E2E Tests

Not applicable. The only user surface is the submission page and the email. T05 covers the page live, and the
post-deploy check on aiccratest covers the attachment.

## 7. Coverage & Traceability

| Requirement | Scenario / clause | Test type | Test or evidence | Result | Gap / notes |
|---|---|---|---|---|---|
| FN-001 | THEN exact bytes B, file name, `application/pdf`, planning copy | unit (author) | ProjectSubmissionEmailTest#thePlanningSendAttachesExactlyTheBytesTheSummaryProduced | PASS | |
| FN-001 | AND IT MUST be the fetched bytes, not a re-read of `bytesPDF` | unit (author) | same test (`FakeSummary.getBytesPDF` is empty); T03 falsifier | PASS | |
| FN-001 | Microservice bytes → real `ReportingSummaryAction` → `SendMailS` | integration-unit (new) | ProjectSubmissionPdfStepTest#aPlanningSubmitWithAPdf… | PASS | |
| FN-002 | THEN `.noPDF` with the link carrying projectID and phaseID on failure, timeout or 0 bytes | unit (author) | ProjectSubmissionEmailTest#aFailedPdfInPlanning…, #aSummaryWithoutBytes…; MicroserviceReportFetchTest (b)–(g), (j)–(l) | PASS | |
| FN-002 | BUT no attachment and no "attached" | unit (author + new) | same tests; PdfStepTest#…NotReady… | PASS | |
| FN-003 | THEN nothing is written to the submit response | unit (author + new) | FetchTest#theSubmitResponseReceivesNoCallInAnyCase; PdfStepTest (bytes and null cases) | PASS | Closed at submit level (M1) |
| FN-003 | AND IT MUST hold when the file is not ready | unit (author + new) | FetchTest#neverReady…; PdfStepTest#…NotReady… | PASS | |
| NF-002 | THEN downloads still stream to their response | unit (author) + live | ReportingSummarySubmitFlagTest#aPlainGenerationQueuesTheMessageAndNeverFetches; T05 S4 | PASS | |
| NF-002 | THEN with today's 60 s connection timeout | diff evidence | `MicroserviceReportAction.java` diff: 0 removed lines; `sendClusterReportQueueMessage()` keeps `setConnectionTimeout(60000)` | PASS (static) | Accepted gap 1 |
| NF-002 | AND IT MUST: a download never sets the submit flag | unit (author) + static | FlagTest; `pdfForSubmit = true` only in `generatePdfBytes()`, which has no setter | PASS | |
| FN-004 | THEN TO = leader and "Dear <first>," for ids 1082 (distinct `Long`s) and 16 | unit (author) | UnsubmitProjectEmailTest#aLeaderWhoIsTheActor…, #…WithACachedIdIsAddressedToo | PASS | |
| FN-004 | BUT the leader is not in CC | unit (author) | same, plus #aDifferentLeaderIsToAndTheActorIsTheOnlyCopy | PASS | Live recipients are masked by the support-only rule |
| FN-005 | THEN "Dear all,"; BUT never "Dear ," | unit (author) | #aProjectWithoutALeader…, #aLeaderWithABlankFirstName… | PASS | |
| FN-005 | "no active PL in the current phase" | unit (new) | #aLeaderWhoIsNotActiveInTheCurrentPhase…, #aLeaderOnlyInAnotherPhase… | PASS | M4, M5 |
| FN-006 | Submit: "Untitled" for a null or blank title and for a missing ProjectInfo; BUT never `"null"` | unit (author) | ProjectSubmissionEmailTest#aProjectWithoutTitle…, #aProjectWithoutProjectInfo… | PASS | |
| FN-006 | Unsubmit: "Untitled"; BUT never `"null"` | unit (author) | UnsubmitProjectEmailTest#aProjectWithoutATitleShowsUntitledAndNeverNull | PASS | |
| FN-007 | WARN contains the unit id; the email is still sent | unit (author) | ProjectSubmissionEmailTest#aFailedUnitTextLookupWarns…; UnsubmitProjectEmailTest#aFailingCustomFileLookupWarns… | PASS | |
| FN-007 | The email uses the action's own texts (submit) | unit (new) | PdfStepTest#aFailedUnitTextLookupSendsTheEmailWithTheActionsOwnTexts | PASS | M6. Unsubmit: accepted gap 2 |
| NF-001 | Bounded: connect, per-request budget, 30 s deadline, ≤ ~35 s | unit (author) | MicroserviceReportFetchTest (deadline, budget, loopback sockets) | PASS | Real timing: post-deploy (P-9) |
| NF-003 | i18n line | review | T04 G-i18n (`email.greeting.everyone=all`) | PASS | |
| Owner rule | Notifications disabled: no PDF wait, no submit or unsubmit email | unit (new) | PdfStepTest#withNotificationsDisabled…; UnsubmitProjectEmailTest#withNotificationsDisabled… | PASS | M2, M3 |
| Owner rule | Support-only (`crp_email_support_team`) | live | T05 rows 18401–18406: to = support mailbox, cc = bcc = NULL | PASS | Enforced in `SendMailS`, which this spec does not change |
| Reporting cycle | No PDF step, `.noPDF`, no attachment | unit (author) | ProjectSubmissionEmailTest#theReportingCycleSendsNoAttachmentAndLinksTheSubmittedPhase | PASS | |

## 8. Remediation

None required. No test is failing and no product defect was found.

**Advisory (non-gating):** `sendNotficationEmail()` runs `summaryPdf()`, which can wait up to about 35 s, before its
own `validateEmailNotification()` check. Today that is harmless, because its only caller, `submitProject()`, checks
the flag first (M2 shows the cost if that outer check is removed). Moving the PDF step inside the send check would
make it robust to a future caller. Recorded for the follow-up list, not changed here.

## 9. Accepted Gaps

1. **NF-002, the 60 s timeout of the download path, is proven by diff, not by a test.** The download builds its
   `ConnectionFactory` inline, so there is no seam to observe it, and a behavioral test would have to wait 60 s.
2. **FN-007 "the action's own texts" on unsubmit.** The `DrivenUnsubmit.getText` harness answers from the base
   files, so the action's provider is indistinguishable from the base copy. The submit side is covered (M6).
3. **FN-001 against the real microservice, and its timing.** No automated check, as the spec's defect-class table
   says. T05 observed it locally; acceptance is the post-deploy check on aiccratest, which also settles P-9.
4. **Real TO / CC recipients live.** `email_logs` stores the recipients after the support-only restriction. The
   recording-double tests cover the pre-restriction arguments.
