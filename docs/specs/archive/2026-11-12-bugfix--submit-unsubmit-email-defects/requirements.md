# Submit and unsubmit email defects — Requirements

**Spec ID:** BUG-SUBMIT-EMAIL-001
**Status:** Approved 2026-11-12
**Owner:** Kenji Tanaka (IBD)
**Reviewers:** IBD tech lead
**Last Updated:** 2026-11-12
**Type:** Bug (Bug Mode)
**Deliverable:** software
**Approval Mode:** gated
**Depth:** Lite
**Source:** `proposal.md` (approved 2026-11-12). Its Bug Diagnosis is the source of truth for causes and evidence
**Depends on:** `changes/email-layout-default` (branch `email-layout-default`, not yet in `staging`)
**Related PRD sections:** docs/prd.md — notifications to project leaders and PMU (submission workflow)
**Related UX/UI Design sections:** none — email only
**Related TRD sections:** none changed
**Companion ai-context docs:** none apply

> **Answer first.** A planning submit email attaches the real summary PDF, and says "attached" only when it
> carries bytes. Otherwise it falls back to the link copy within a short time budget. The submit page never
> receives the PDF. Unsubmit always addresses and greets the project leader by name, and greets "all" when there
> is no leader. A missing title reads "Untitled", never "null". The fallback WARN names the unit.

## 1. Overview

Corrects the six defects diagnosed in `proposal.md` §3 and §9: D1, D1b, D2, D3, D4 and D5.

## 2. Problem Statement

Everything below is cited in `proposal.md` §3 (re-run at `cc15631fe4`):
- **D1 / D1b:** the planning PDF is always 0 bytes (`ReportingSummaryAction.java:1210, 1394`). The microservice
  download writes into the submit's own HTTP response (`MicroserviceReportAction.java:91, 112, 126`). The production
  effect of the response write is UNVERIFIED — confirm at source before relying on it (Q2).
- **D2:** a null title renders as `null` (`ProjectSubmissionAction.java:347`, `UnsubmitProjectAction.java:284`).
- **D3:** with no PL, the greeting reads "Dear ," and TO is empty (`UnsubmitProjectAction.java:165-168, 276-281`).
- **D4:** `Long` ids are compared with `!=` (`UnsubmitProjectAction.java:166, 278`), so the outcome depends on
  whether the id is above 127.
- **D5:** the fallback WARN carries no unit id (both `unitTexts()` methods).

## 3. In-Scope Requirements

### Functional

- **BUG-SUBMIT-EMAIL-001-FN-001 — Real attachment.** A planning-cycle submit email MUST attach the generated
  summary PDF when its bytes are obtained within the submit budget (NF-001). Its file name and `application/pdf`
  type stay as today.
- **…-FN-002 — Honest fallback.** When the PDF cannot be obtained (unreachable queue, file not ready within the
  budget, download error, or zero bytes), the email MUST use the unit's `.noPDF` copy with the summary link and MUST
  attach nothing.
- **…-FN-003 — Submit response untouched.** The submit request MUST NOT write PDF bytes, headers or an error status
  into its own HTTP response. The page the user receives is the submission page, as for a reporting-cycle submit.
- **…-FN-004 — Leader always addressed.** An unsubmit email MUST have the project leader of the current phase as TO,
  and MUST greet the leader by first name, including when the leader is the user who unsubmits. In that case the
  leader MUST NOT also be in CC. The result MUST be the same for every user id (Q1, owner 2026-11-12).
- **…-FN-005 — No leader.** When the project has no leader in the current phase, the unsubmit greeting MUST read
  "Dear all,", like the submit email. It MUST NOT read "Dear ,".
- **…-FN-006 — No "null" title.** When the project has no title in the current phase, submit and unsubmit emails
  MUST show the unit's `global.untitled` text ("Untitled") where the title goes.
- **…-FN-007 — Traceable fallback.** The WARN logged when a unit's texts cannot be read MUST include that unit's id.

### Non-Functional

- **…-NF-001 — Submit budget.** Obtaining the PDF in the submit MUST be bounded: connection about 5 s, and waiting
  for the file about 20 s (owner, Q5). After that, FN-002 applies. The final values are set from the generation time
  measured on aiccratest during execute.
- **…-NF-002 — Downloads unchanged.** The summary, innovation and OICR download actions MUST keep streaming the PDF to
  their own response, exactly as today.
- **…-NF-003 — i18n.** Any new or changed `.properties` line follows `MessageFormat` rules: `''` for an apostrophe,
  and no brace that is not an argument.

### Data / UI / Security

Not applicable. No schema, screen or permission change.

## 4. Out-of-Scope

Taken from `proposal.md` §6:
- `email_logs.fileContent` NULL.
- The 143-site `Long` comparison sweep, including the 4 sibling actions.
- Per-unit PDF templates or file names (Q6: keep the shared report).
- Re-enabling Pentaho.
- The permission check, justification and escaping findings on unsubmit.

## 5. Personas Affected

| Persona | Effect |
|---|---|
| Leaders, PMU / PMC / PIU | Receive the real PDF, or a link when it is not available |
| Submitters | The submission page renders normally |
| A leader who unsubmits their own project | Addressed and greeted by name, whatever their id |

## 6. Acceptance Criteria

**AC FN-001 — attachment (regression for D1)**
- GIVEN a planning-cycle submit whose PDF fetch returns non-empty bytes B within the budget
- WHEN the email is sent
- THEN the send carries exactly B, the file name `Full_Cluster_Report-<unit acronym>-C<projectID>-<yyyyMMdd-HHmm>.pdf`
  (today's `ProjectSubmissionAction.getFileName()`) and `application/pdf`, with the planning ("attached") copy
- AND IT MUST be the bytes the fetch returned (asserted byte for byte), not a re-read of `bytesPDF`.

**AC FN-002 — fallback**
- GIVEN the fetch fails, times out, or returns 0 bytes
- WHEN the email is sent
- THEN the `.noPDF` copy is used, with a link carrying the submitted `projectID` and `phaseID`
- BUT it must NOT attach anything or contain "attached".

**AC FN-003 — response**
- GIVEN any planning-cycle submit, with or without a PDF
- THEN nothing is written to the submit's HTTP response by the PDF step (no content type, no body, no `sendError`)
- AND IT MUST hold when the file is not ready within the budget, which today produces `sendError(404)`.

**AC NF-002 — downloads**
- GIVEN the summary, innovation and OICR download actions
- THEN they still stream to their response with today's 60 s connection timeout
- AND IT MUST hold with the one guarded line the submit adds to `generateAndSendJson()`: a download instance never
  sets the submit flag, so it never enters it (design DD-2).

**AC FN-004 — leader is the actor (regression for D4)**
- GIVEN leader L, who is also the user who unsubmits, with id 1082 (above 127, as distinct `Long` instances) and,
  separately, with id 16 (below 128)
- WHEN the unsubmit email is built
- THEN TO is L's email and the greeting is "Dear <L's first name>," in both cases
- BUT it must NOT list L's address in CC.

**AC FN-005 — no leader (regression for D3)**
- GIVEN a project with no active PL in the current phase
- WHEN it is unsubmitted
- THEN the greeting is "Dear all,"
- BUT it must NOT contain "Dear ,".

**AC FN-006 — no title (regression for D2)**
- GIVEN a project whose title in the current phase is null
- WHEN it is submitted, and separately unsubmitted
- THEN the body shows "Untitled" in place of the title
- BUT it must NOT contain `"null"`.

**AC FN-007 — WARN**
- GIVEN the unit's custom-file lookup throws
- THEN one WARN is logged, containing the unit id, and the email is still sent with the action's own texts.

### Defect classes and their gates

| Defect class | Gate | Automated? |
|---|---|---|
| Java does not compile | Clean compile (marlo-verify) | Yes |
| Style violation | Checkstyle delta against the base (marlo-verify) | Yes |
| Wrong attachment, copy or recipient | Send-path tests with the recording `SendMailS` double, asserting exact arguments | Yes |
| PDF step writes to the response | Test with a response double that records any write or `sendError` | Yes |
| Waits longer than the budget | Fetch test with an injectable clock or fetcher that never becomes ready, asserting the fallback and the attempt count | Yes |
| Real microservice behavior and timing | **No automated check** (no queue or S3 locally). Substitute: a live planning submit on aiccratest during execute, with the owner checking the email and the page at the HITL pause | No — human check |
| Apostrophe or brace in new copy | i18n review (marlo-verify) | Partly |

## 7. Constitutional Compliance Checklist

- [x] Phase replication / save validation / permissions / specificity / migrations / REST / audit: not applicable.
- [x] i18n: reuses `global.untitled`. A greeting key may be added (NF-003).
- [ ] License header: any new Java file carries the GPL header — at execute.
- [ ] Code style: Checkstyle delta 0 — at execute.
- [x] Dependency floors: untouched.
- [x] Branching: `email-layout-default` (R1, owner 2026-11-12); merge target `staging`.

## 8. Open Questions

| # | Question | Owner | Blocks |
|---|---|---|---|
| Q2 | What aiccratest shows today after a planning submit (PDF, error, or the page) | Kenji Tanaka | none |
| Q4 | Jira ticket and parent epic | Kenji Tanaka | commits only |
| R1 | *(Answered 2026-11-12: same branch, `email-layout-default`.)* | — | — |

## 9. Decision Log

- 2026-11-12 — Option B: attach the real PDF (owner: "la idea es poder adjuntar el pdf siempre"). Option A was rejected.
- 2026-11-12 — Q1: the leader stays TO and is greeted by name, for every id.
- 2026-11-12 — Q5: a short submit-only budget (about 5 s to connect, about 20 s to wait for the file), then the `.noPDF` copy.
- 2026-11-12 — Q6: the same microservice report for every unit, as today.
- 2026-11-12 — Amended at design: AC NF-002 said the download code path is "unchanged in the diff"; design DD-2 extracts the publish block with the timeout as a parameter, so the AC now requires unchanged behavior (60 s timeout, response streaming). Closure sweep: forward `grep -n 'unchanged in the diff' *.md` → none left; backward: no document cites AC NF-002.
- 2026-11-12 — After the review of today's submit outcomes (PDF shown in the tab when ready, 404 when not, page after a queue timeout), the owner chose option 1: the submit always shows its submission page and the PDF travels in the email. Showing the PDF as a formal `stream` result was rejected (larger, Standard depth). FN-003 stands.
- 2026-11-12 — Amended after judgment round 1 (`judgment.md`, owner: "sí, los tres, aplica los ajustes"): §1 counts six defects (I-4); AC NF-002 now names the one guarded line (design DD-2 no longer extracts the publish block); FN-004 and its AC add "not also in CC" (W-4: `SendMailS` keeps a self-CC). Closure sweep: forward `grep -n -E 'five defects|publish block is shared|only the timeout becomes' *.md` → only `judgment.md` (frozen record) and the 2026-11-12 design-amendment entry above (history); backward: `grep -n 'AC NF-002\|AC FN-004\|FN-004' design.md tasks.md` → design §2/§14 updated in the same round.
- 2026-11-12 — D2 and D3 are kept in scope. They are reachable only through the SuperAdmin override (`proposal.md` §3).
