# Judgment Day — Submit and unsubmit email defects (design)

**Target:** `design.md` (md5 `838e58f4…`) with `requirements.md` (md5 `fd6fe39c…`) and `proposal.md` (md5 `0ec63026…`), at
HEAD `f9dba6a035`. The merge of `staging` after `cc15631fe4` touched none of the cited files.
**Mode:** blind dual review, two read-only judges (sonnet, which differs from the design author opus), with identical
scope and criteria.
**Date:** 2026-11-12. **Round:** 1.

## Status

**JUDGMENT: APPROVED ✅** No severe finding was confirmed by both judges, so no automatic fix round runs.

One severe finding was reported by one judge only (S-1). It is recorded as **suspect**. The orchestrator verified
its premise at the source (below). It and the confirmed warnings go to the owner as a design Adjust round, not as an
automatic fix.

## Counts

| Confirmed severe | Suspect severe | Confirmed warning | Suggestion / single-judge | Contradictions |
|---|---|---|---|---|
| 0 | 1 | 4 | 5 | 0 |

## Ledger

| ID | Severity | Judges | Finding | Orchestrator note |
|---|---|---|---|---|
| S-1 | severe (suspect) | B J-1 (A J-3 touched the singleton, rated low) | Concurrent planning submits share the singleton `ReportingSummaryAction`. `ProjectSubmissionAction.java:366-379` sets `project`, `projectInfo`, `selectedPhase` and others, and the JSON build reads them. Once the bytes are real, one project's report can be attached to another project's email. P-3's "unchanged, accepted / Low" is wrong | **Premise verified:** `ProjectSubmissionAction` receives `ReportingSummaryAction` through its `@Inject` constructor, and Spring resolves the `@Named` singleton (`struts.xml:9-12` uses `StrutsSpringObjectFactory`). Downloads build their own action instance, so only submits share it. The race already existed with real Pentaho PDFs before `6baa6163b2` (2025-12-03) |
| W-1 | warning | A J-1, B J-2 | NF-001 is not bounded. The HEAD and GET `HttpURLConnection`s have no connect or read timeout (`MicroserviceReportAction.java:89-135`). The AMQP handshake and RPC timeouts are not set; `setConnectionTimeout` (`:410`) covers the TCP connect only | Confirmed by both |
| W-2 | warning | A J-3, B J-3 | "Uses locals only" is false. The publish block reads the singleton fields `queueUrl`, `queueName`, `apiKey`, `jsonData`, `clusterReportName` and `clusterReportTemplateData`. `loadData()` can leave them null. The `downloadPDFByURL` call sits inside the publish try-with-resources (`:454`), so the extraction must cut it out | Confirmed by both |
| W-3 | warning | A J-5, B J-4 | `InterruptedException` is swallowed by the existing `catch (Exception)` around `Thread.sleep`. The design does not say what the wait does when interrupted | Confirmed by both (A rated it a suggestion) |
| W-4 | warning | A J-2, B J-6 | DD-6 and P-8 claim `SendMailS` strips the TO from CC. When the actor is the leader, CC `"a, a"` becomes `"a"`, so the leader gets TO and CC. `toEmail` is also used as a regex | Confirmed by both (B rated it a suggestion) |
| I-1 | suggestion | A J-4 | The builder's early returns or a null `reportName` must make `generatePdfBytes()` return null at once, not poll `s3URL + "null"` | — |
| I-2 | suggestion | B J-5 | The report name has minute granularity (`yyyyMMdd_HHmm`, `:11426`). A resubmit in the same minute can get the previous object | Known limit, rare |
| I-3 | suggestion | B J-7 | Test the submit through `generatePdfBytes()` on the existing `FakeSummary` double, not by overriding `summaryPdf()`, so the setters stay tested | — |
| I-4 | suggestion | A J-7, B J-8 | requirements §1 says "five defects" but lists six labels. Proposal §10 option B still says "≤ 60 s connect + 10 s polling" | The proposal is approved; noted only |
| I-5 | suggestion | A J-6 | Line-range drift. The PDF block is `:362-391`. P-3 gives no directory for `ApplicationContextConfig.java` | — |

## Premise Ledger verdicts

| Row | A | B |
|---|---|---|
| P-1 | confirmed | confirmed |
| P-2 | confirmed | confirmed |
| P-3 | partly (impact overstated for downloads) | claim confirmed; impact refuted (S-1) |
| P-4 | confirmed | confirmed |
| P-5 | confirmed | confirmed |
| P-6 | confirmed | confirmed |
| P-7 | confirmed | confirmed |
| P-8 | partly refuted (W-4) | confirmed with caveat (W-4) |
| P-9 | not re-run | not re-run |

**Missing rows named by the judges:** HTTP and AMQP timeout semantics (W-1), the builder's null paths (I-1), the
per-request leak through the singleton (S-1).

## Correction work units

None run by this skill (no confirmed severe finding). Recommended to the owner as a design Adjust round:
S-1, W-1 to W-4, I-1, I-3, I-4 (the requirements wording) and I-5.
