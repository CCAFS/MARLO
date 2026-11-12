# Archive Summary — Submit and unsubmit email defects

**Outcome: delivered and validated (PASS, 2 accepted WARNs).** The emails changed as follows:
- A planning submit email attaches the real summary PDF. It is fetched per submit within a bounded budget and never
  written into the submit page.
- The unsubmit email always addresses and greets the leader, with no self-CC. It greets "all" when there is no
  leader.
- "Untitled" replaces "null", and the fallback WARNs name the unit.

The post-deploy check on aiccratest is the only open item.

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | BUG-SUBMIT-EMAIL-001 |
| Owner | Kenji Tanaka (IBD) |
| Type / Depth | Bug (Bug Mode) / Lite |
| Branch | `email-layout-default` (stacked on the archived `changes/email-layout-default`). Not pushed at archive time; it merges into `staging` |
| Last spec commit before archive | `dca25313ed` |

## 2. Original Spec Path

`docs/specs/bugfix/submit-unsubmit-email-defects/`

## 3. Archive Date

2026-11-12 → `docs/specs/archive/2026-11-12-bugfix--submit-unsubmit-email-defects/`

## 4. Final Status

| Phase | Result | Artifact |
|---|---|---|
| Propose | Approved; option B (attach the real PDF) chosen over A | `proposal.md` |
| Specify | Approved; judgment round 1 APPROVED, adjust round 1 applied | `requirements.md`, `design.md`, `tasks.md`, `judgment.md` |
| Execute | T02–T05 PASS; T01 deferred to post-deploy. No HALT, no Pivot | `execution.md` |
| Test | PASS; 7 tests added, 1175 / 0 / 0 | `test-report.md` |
| Validate | PASS; 0 FAIL, 2 accepted WARNs | `validation-report.md` |

## 5. Requirements Delivered

| Req | Delivered |
|---|---|
| FN-001 | The planning submit attaches the PDF bytes fetched from the microservice (observed locally at 397 KB and 341 KB in about 4 s) |
| FN-002 | When the fetch fails, times out or returns empty bytes, the `.noPDF` copy is sent with the submitted projectID and phaseID |
| FN-003 | The submit page is never overwritten by the PDF or by a 404 |
| FN-004 | The leader is always TO and greeted by name, whatever the id, and is never also in CC |
| FN-005 | "Dear all," when there is no active PL in the phase |
| FN-006 | "Untitled" instead of "null" on submit and unsubmit |
| FN-007 | Fallback WARNs carry the unit id |
| NF-001 | Bounded: 5 s connect, a per-request budget, a 30 s deadline, at most about 35 s |
| NF-002 | Downloads are unchanged apart from one guarded line they never enter |
| NF-003 | One i18n line, `email.greeting.everyone=all` |

## 6. Files Changed Summary

`git diff --stat ef5f5c641a..dca25313ed -- marlo-web`: 10 files, +1805 / −88.

| Area | Files |
|---|---|
| Java, production | `MicroserviceReportAction` (new bounded `fetchClusterReportBytes`; additions only), `ReportingSummaryAction` (2 fields, `generatePdfBytes()`, 1 guarded line), `ProjectSubmissionAction` (`ApplicationContext`, per-submit `createBean`, `summaryPdf()`, non-empty attachment, "Untitled", WARN id), `UnsubmitProjectAction` (leader rule, `withoutRecipient`, greeting and title fallbacks, WARN id) |
| i18n | `global.properties` (+1) |
| Tests | New: `MicroserviceReportFetchTest` (16), `ReportingSummarySubmitFlagTest` (2), `ProjectSubmissionPdfStepTest` (4). Updated: `ProjectSubmissionEmailTest` (17), `UnsubmitProjectEmailTest` (18) |

## 7. Test Evidence Summary

- Full `marlo-web` suite: 1175 run, 0 failures, re-run by the validator after a clean compile.
- Every new test was red before its fix or under a deliberate mutation. Two of them are localhost socket tests for a
  slow server and a silent server.
- T05 ran a live local check on a throwaway DB copy with mail restricted to support: the submit page rendered, the
  real PDF was attached, unsubmit used the correct greetings, and the download path was unchanged. The P-10 wiring
  was verified at runtime.

## 8. Validation Summary

PASS, with 0 FAIL. Checkstyle is at or below the spec-start baseline on all 9 Java files. Every clause is traced to a
test or check. The two WARNs are T01, which is deferred, and the FN-001/P-9 acceptance. Both need aiccratest after
the deploy.

## 9. Accepted Warnings Or Follow-Ups

| # | Follow-up | Route |
|---|---|---|
| 1 | **Post-deploy check on aiccratest.** Run a planning submit; the email must carry a non-empty PDF that opens, and the submission page must render. Read the `Cluster report fetch … elapsedMs=` INFO lines; this closes T01, P-9 and Q2 | Owner, after deploy |
| 2 | Persona refresh: `akili doctor --agents --fix` on `staging` | Kaizen pending item |
| 3 | A publish blocked by a broker alarm, and slow response headers, are bounded only by a later watchdog | Candidate proposal |
| 4 | Move `summaryPdf()` inside the notification gate in `sendNotficationEmail()` | Candidate proposal |
| 5 | A cross-unit submit through a stale session uses the session unit's texts (T05 row 18403) | Candidate proposal |
| 6 | A null actor email or first name shows "null" in the unsubmit CC and `{1}`; `SendMailS` merges CC entries when the TO is empty | Candidate proposal |
| 7 | `email_logs.fileContent` NULL; the 143-site `Long` comparison sweep | Already on the list from `changes/email-layout-default` |
| 8 | Jira ticket (Q4) | Owner |

## 10. Historical Notes

- **Re-diagnosis at propose.** The empty PDF was not caused by a failing microservice. `generatePentahoReport` has
  been hard-coded `false` since `6baa6163b2` (2025-12-03), and the microservice branch never fills `bytesPDF`. Every
  planning submit since then attached 0 bytes and streamed the PDF, or a 404, into its own response.
- **Shared singleton.** `ReportingSummaryAction` is a Spring singleton injected into the submit. A judge flagged it
  (S-1, suspect), and the orchestrator verified it in the Struts 6.8.0 bytecode. It led to a per-submit `createBean`
  instance (DD-1).
- **Leader comparison.** The `Long !=` comparison made the leader rule depend on whether the id was below 128 (the
  JVM's boxing cache). The owner chose today's majority behavior: the leader is addressed and greeted.
- **Timeout semantics.** `HttpURLConnection` timeouts apply per read, not to the whole request. A design bound of
  about 90 s was wrong, and so was a later "30 s at most"; the final bound is about 35 s with a per-request budget.
  This cost one Reviewer FAIL.
- **Local microservice.** The microservice turned out to be reachable from local, so T05 observed real attachments.

## 11. Diagrams

There is one inline Mermaid diagram (`design.md` §1, the fetch architecture). There is no `diagrams/` folder, and no
baseline diagram slot went stale.
