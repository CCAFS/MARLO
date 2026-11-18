# Submit and unsubmit email defects — Proposal

**Status:** Approved 2026-11-12 by Kenji Tanaka ("sí, de acuerdo con el timeout corto, sigue con specify"); Q5 short submit budget with `.noPDF` fallback confirmed

> **Answer first.** Five defects are confirmed in the project submit and unsubmit emails. The most serious one is
> bigger than first thought. Since 2025-12-03 (`6baa6163b2`, in `main`), **every** planning-cycle submit email
> attaches a 0-byte PDF and says it is attached. The cause is not only a failing microservice: the PDF bytes never
> reach the email on any path. The other four defects are small and isolated: a "null" title, "Dear ," when a
> project has no leader, a `Long` reference comparison in unsubmit, and a fallback WARN with no unit id.
> Recommended fix (owner direction 2026-11-12: "la idea es poder adjuntar el pdf siempre"): fetch the
> generated PDF from S3 into a byte array and attach it, without writing to the submit response. Fall back to
> the link copy only when the PDF cannot be obtained. The four small defects are fixed in place. The route is
> `/akili-specify` in Bug Mode, at Lite depth.

## 1. Document Control

| Field | Value |
|---|---|
| Spec Path | `bugfix/submit-unsubmit-email-defects` |
| Type | Bug |
| Deliverable | software |
| Approval Mode | gated |
| Depth (proposed) | Lite |
| Owner | Kenji Tanaka (IBD) |
| Source | Findings of `changes/email-layout-default` (archived `docs/specs/archive/2026-11-12-changes--email-layout-default/`): T04 advisory and Deferred section, T08 live findings, validation W1. Re-diagnosed here on 2026-11-12. Jira ticket: none yet (open question Q4) |
| Depends on | `changes/email-layout-default` — the code to patch (`assembleEmail`, `unitTexts`) exists only on branch `email-layout-default`, which is not yet merged into `staging` |
| Parallel-safe | no — same two actions as the dependency |

## 2. Intent

Make the submit and unsubmit emails truthful and complete. No email should claim an attachment it does not carry.
No email should print "null" or greet nobody. The recipient rules should do what the code says they do.

## 3. Problem / Current Behavior

| # | Today | Evidence |
|---|---|---|
| D1 | Every planning-cycle submit email attaches a 0-byte PDF and says "The cluster summary is attached as a PDF", in every unit | `ReportingSummaryAction.java:1210-1211`: `generatePentahoReport = false`, so `execute()` always takes the microservice branch. `:1391-1394`: `generateAndSendJson()` then `bytesPDF = os.toByteArray()`, but nothing writes to `os` on that branch, so the array is always empty. `ProjectSubmissionAction.java:383,392`: `ByteBuffer.wrap(empty)` counts as `pdfAttached`. Live: T08 attachment downloaded at 0 bytes (archived `execution.md` T08) |
| D1b | When the microservice is reachable, the submit request's own HTTP response receives the PDF stream | `MicroserviceReportAction.java:89-91, 112, 126-137`: `downloadPDFByURL` writes to `ServletActionContext.getResponse()`, which during a submit is the response of `{crp}/submit` (`struts-projects.xml:1140`; `execute()` returns `INPUT` → `projectSubmission.ftl`). If the file is not ready it calls `response.sendError(404)` on that response. **UNVERIFIED — confirm at source before relying on it**: production effect not observed (locally the microservice is unreachable). Owner to check on aiccratest (Q2) |
| D2 | A project with no title in the phase is mailed as `"null"` | `ProjectSubmissionAction.java:347` and `UnsubmitProjectAction.java:284`: `projectInfo.getTitle()` passed to `MessageFormat`, which renders a null argument as `null`. Live: T08, project 102095, phase 444. **Reachable only through the SuperAdmin override:** `menu-projects.ftl:224-232` lets a SuperAdmin submit an incomplete project, while everyone else needs `completed` (`:253`) and `ProjectDescriptionValidator:106` requires a valid title. Production data: 0 submit or unsubmit emails with a `"null"` title. The 4 untitled project-phase rows in the local copy (102094, 102095, 102099, 102103) were all created by SuperAdmin 1082 during local QA and have no submission |
| D3 | Unsubmit greets "Dear ," and sends with an empty TO when the project has no leader in the phase | `UnsubmitProjectAction.java:165-168, 276-281`: `getLeaderPerson(phase) == null` leaves `toEmail = ""` and `values[0] = ""`. Production data: 2 of 974 unsubmit emails (`email_logs` 17895, 18395). Neither project had a PL at send time: 102093 has no PL row at all, and the PL of 102102 was created at 00:19:29, after email 18395 at 00:11:27. Both are test projects (102093 created by user 3824 on 2025-07-11; 102102 by SuperAdmin 1082 during local QA) and reached unsubmit through the SuperAdmin override (D2) |
| D4 | Unsubmit compares user ids with `!=`, which compares `Long` references. For ids above 127 the test "leader is not the actor" is always true | `UnsubmitProjectAction.java:166, 278`. `MarloBaseEntity.getId()` returns `Long`. The session user (`BaseAction:3083`) and the Hibernate-loaded leader are distinct instances. Live: T08 `email_logs` 18411, where leader 1082 was the actor and the email still had TO = 1082 and "Dear MARLO,". **The result is inconsistent, not just inverted:** ids −128..127 are boxed to cached instances, so for them `!=` compares correctly and the intended branch runs (TO empty, "Dear ,"). AICCRA has real leaders in that range (users 16 and 22, who unsubmitted their own clusters in 2022: `submissions` 4726, 4727). Of 1,281 unsubmits in the local copy, 96 were made by a PL of the same project (approximation: PL in any phase) |
| D5 | The fallback WARN of both `unitTexts()` does not say which unit failed | `ProjectSubmissionAction.unitTexts()`, `UnsubmitProjectAction.unitTexts()`. Validation W1 of the archived spec; design §11 was amended to match |

## 4. Proposed Outcome

- A planning submit email attaches the real summary PDF, and claims the attachment only when it carries bytes.
  Every submit email carries the working summary link (`{6}`).
- The submit request never writes the PDF into its own HTTP response (D1b).
- A missing title never prints `null`.
- A project without a leader is greeted with a neutral greeting, never "Dear ,".
- The leader/actor rule compares ids by value. When the leader is the actor, the leader stays TO and is greeted by name for every id (Q1).
- The fallback WARN names the unit id.

## 5. Scope

| In | Files |
|---|---|
| D1, D1b | `MicroserviceReportAction` (new byte-returning fetch), `ReportingSummaryAction` (expose the fetched bytes on the microservice branch, or the submit calls the fetch directly — decided at specify), `ProjectSubmissionAction` (attach only non-empty bytes) |
| D2 | Both actions: title fallback |
| D3 | `UnsubmitProjectAction`: greeting fallback, with an i18n key if new copy is needed |
| D4 | `UnsubmitProjectAction:166, 278`: `Objects.equals` (or `.equals` with a null guard) |
| D5 | Both `unitTexts()` WARN messages |
| Tests | `ProjectSubmissionEmailTest`, `UnsubmitProjectEmailTest`: regression tests, red before and green after |

## 6. Non-Goals

- `email_logs.fileContent` stored as NULL (it is NULL on most attachment rows since at least 2025-06): separate
  investigation.
- The sweep of the 143 `Long` `==` / `!=` comparisons in 78 files, including the 4 siblings listed in §9: separate,
  optional.
- The response-writing pattern of the innovation, OICR and summary download actions, where it is intended.
- Re-enabling the Pentaho path.
- The unsubmit findings already excluded by the previous spec: no permission check on `unsubmitProject.do`, blank
  justification, unescaped `{7}` / `{3}`, stale menu.
- The per-unit copy of the `.noPDF` texts (already rewritten for AICCRA, AICCRA_III and the base).

## 7. Affected Users, Systems, And Specs

| Who / what | Effect |
|---|---|
| Cluster / project leaders, PMU, PMC, PIU | Planning submit emails carry the real summary PDF again (empty since 2025-12-03) |
| Submitters (planning) | The submit still waits for the PDF, as today (Q5), but no longer gets the PDF or a 404 written into its page |
| Unsubmit recipients | A correct greeting, and TO / greeting per the decided leader rule |
| Archived spec `changes/email-layout-default` | Its FN-010 ("attachment unchanged") assumed a real attachment; this spec makes that true |
| `ReportingSummaryAction`, `MicroserviceReportAction` | Changed for the submit path only. The download actions (summaries, innovations, OICRs) keep streaming to their response |

## 8. Visual Reference

- Source: None.
- Location: n/a. The emails use the shell and the Appendix A copy approved in `changes/email-layout-default`.
- Notes: backend-only change. Any new greeting copy is reviewed as text at specify time.

## 9. Bug Diagnosis

### Observed Symptom

- **Planning submit:** the email has a 0-byte PDF attached and says it is attached. The user opens an empty or
  broken file.
- **Unsubmit without a leader:** "Dear ,".
- **Unsubmit by the leader:** the leader gets the email as TO and as CC, greeted by name. This is the opposite of
  what the code intends.
- **Untitled project:** the email shows `"null"`.

### Reproduction Steps

1. **D1:** on any unit, in a planning phase, submit a complete project. Inspect the attachment: 0 bytes. Locally the
   microservice is unreachable; in production the bytes are empty for the reason in §3 regardless.
   Deterministic by code.
2. **D3:** unsubmit a project that has no active PL person in the current phase → "Dear ," and an empty TO.
3. **D4:** as the project leader (user id > 127), unsubmit your own project → TO = you, "Dear <your name>,".
4. **D2:** unsubmit or submit a project whose `projects_info.title` is NULL in the current phase → `"null"`.
5. **D5:** make `custom_parameters` unreadable for the unit → the WARN line has no unit id.

### Root Cause (confirmed)

| # | Root cause | Confirmed by |
|---|---|---|
| D1 | The microservice branch of `ReportingSummaryAction.execute()` never writes into `os`, so `bytesPDF` is always `new byte[0]`. The PDF goes to S3 and to the HTTP response, never to the caller. `ProjectSubmissionAction` takes "a non-null buffer" to mean "attached" | Code read of all three methods. `generatePentahoReport` is hard-coded `false` since `6baa6163b2` (2025-12-03, in `main`, `staging`, `dev`). T08 0-byte download |
| D1b | `downloadPDFByURL` assumes it runs in a download request | Code read. Production effect UNVERIFIED (Q2) |
| D2 | A null `String` argument is formatted by `MessageFormat` as `null` | JDK behavior. T08 live row |
| D3 | No fallback when `getLeaderPerson(phase)` is null | `email_logs` 17895 / 18395 with the PL timeline (§3) |
| D4 | Reference comparison of boxed `Long` | Live 18411. JLS boxing cache only covers −128..127 |
| D5 | Messages written without the id | Code read |

### Blast Radius

| Check | Recorded as | Result |
|---|---|---|
| **Already fixed?** | `git fetch --all`; `git log --all --since=2026-06-01 -- UnsubmitProjectAction.java ProjectSubmissionAction.java` → only `cc97bbd74d`, `1f097a6895` (email-layout-default), `0fa05c4737`, `647d3460ff` (refactors). `git log --all -i --grep='unsubmit\|empty pdf\|0-byte\|Dear ,\|getBytesPDF\|summary pdf'` → no fix commit | Not fixed on any branch |
| **Live path?** | Submit: `{crp}/submit` (`struts-projects.xml:1140`) → `ProjectSubmissionAction.execute()` → `submitProject()` → `sendNotficationEmail()` (`:225`) → `isPlanningActive()` → `reportingSummaryAction.execute()` (`:380`) → `generatePentahoReport == false` (`ReportingSummaryAction:1210`) → `generateAndSendJson()` → `sendClusterReportQueueMessage` → `downloadPDFByURL`; back in `execute()` `bytesPDF = os.toByteArray()` (`:1394`) → `getBytesPDF()` (`:383`). Unsubmit: `unsubmitProject.do` → `UnsubmitProjectAction.execute()` → `sendNotficationEmail(Project)` (`:155`) → `:165-168`, `:276-284` | On path. Every patched line is reached by the reproductions (T08 live for D1, D2, D4; prod rows for D3) |
| **Siblings on the same state** | `getBytesPDF()` callers that email: only `ProjectSubmissionAction:383` (the center `ProjectSubmissionAction` / `IPSubmissionAction` use other summary actions on the Pentaho path). `downloadPDFByURL` callers: `MicroserviceReportAction:298` (innovations), `:359` (OICRs), `:454` (clusters) — all download requests, writing the response on purpose. `Long !=` on user ids: `ImpactSubmissionAction:173`, `UnSubmitImpactpathwayAction:147`, `EmailTrackingCommentAction:148`, `EmailTrackingReactionCommentAction:165` (out of scope, §6) | Scope stays the two actions. The siblings go to the separate sweep |
| **Downstream consumers** | `email_logs` readers; `projectSubmit.js` (unsubmit JSON response unchanged); tests `ProjectSubmissionEmailTest` (PDF-attached cases assert the planning copy and the attachment arguments; they change), `UnsubmitProjectEmailTest`, `SubmitEmailCopyTest` (the planning copy keys stay); archived spec FN-010 | The submit tests' PDF cases gain a fetch double |

Data integrity and security: none. No write path changes. D1b may today corrupt the submit page response, which is a
user-facing break but not a data problem.

### Fix Strategy

Logic changes, so `/akili-specify` in Bug Mode at Lite depth, with a regression test per defect (red before, green
after). Not `/akili-quick`.

## 10. Approach Options

| Option | D1 / D1b | Cost | Risk |
|---|---|---|---|
| A | Submit stops calling `ReportingSummaryAction`. It always sends the `.noPDF` copy with the `{6}` link | Smallest: removes ~30 lines, no new integration | Planning emails lose the attachment. **Rejected by the owner (2026-11-12): the PDF must be attached** |
| **B (recommended)** | Add a byte-returning fetch to `MicroserviceReportAction` (poll S3, read into `byte[]`; never touch the HTTP response) and use it from the submit. Attach it when it has bytes; empty or failed → `.noPDF` | New method plus a polling wait inside the submit request (≤ 60 s connect + 10 s polling) | Restores the attachment. Couples submit latency to RabbitMQ and S3 (Q5). The polling loop and the download must be testable without the network (seam on the URL fetch) |
| C | Keep the call; treat `length == 0` as no PDF (`buffer.remaining() > 0`) | One line | Honest wording, but D1b and the latency stay. Every planning submit still does the work for nothing |

Option B is the smallest path that meets the owner's goal: it attaches the real PDF, removes the response hijack,
and keeps an honest fallback. Option C alone is not enough: the bytes are empty on every path, so it would never
attach anything.

D2–D5 have one option each:
- **D2:** fall back to the project identifier (`C<id>`, already in `{3}`) or an empty title — to decide at specify. Low value: SuperAdmin-only path, 0 production emails.
- **D3:** a neutral greeting through an i18n value. Low value in practice: SuperAdmin-only path, 2 test emails.
- **D4:** drop the leader/actor exclusion, so TO and greeting always use the leader (Q1). Real value: today the outcome depends on whether the leader's id is below 128.
- **D5:** add `unit.getId()` / `loggedCrp.getId()` to the WARN message, as a null-safe argument.

## 11. Recommended Approach

Option B for D1 / D1b, plus the four in-place fixes. One spec, Lite depth, about 5 tasks:
1. A byte-returning PDF fetch in `MicroserviceReportAction` (the download actions keep `downloadPDFByURL`).
2. Submit attaches the fetched PDF, or falls back to `.noPDF`.
3. Unsubmit leader rule and greeting.
4. Title fallback and WARN ids.
5. Tests.

Each fix gets a regression test that drives the send path through the `EmailSendDoubles` built in the previous
spec. Apply `KZ-changes--migrate-ad-authentication-to-cognito--auth-flow-2`: assert the arguments the recording
double receives (attachment null, TO value, greeting), not only that `send` ran. Apply the pending lesson
`KZ-changes--email-layout-default-1`: any AC that asserts greeting words is checked against the exact copy.

## 12. Risks, Dependencies, And Open Questions

| # | Question / risk | Owner | Blocks |
|---|---|---|---|
| Q1 | *(Answered 2026-11-12 by Kenji Tanaka: "puede ir como destinatario como pasa hoy".)* When the leader is the actor, the leader stays TO and is greeted by name — production's current behavior for ids above 127, now made true for every id. The `!=` comparisons are replaced so the result no longer depends on the id value | — | — |
| Q2 | **D1b in production.** On aiccratest, does submitting a planning cluster show the PDF, an error page, or the normal submission page? This sets D1b's severity. It does not change Option B | Kenji Tanaka | none (informative) |
| Q3 | *(Closed 2026-11-12 by choosing Option B: the planning "attached" copy stays in use.)* | — | — |
| Q5 | **Latency.** Today the S3 wait is 5 × 2 s, and a report the renderer takes longer than 10 s to produce is never fetched; the RabbitMQ connection timeout is 60 s. Recommendation (pending owner confirmation): a submit-only budget — connection timeout ~5 s and S3 polling up to ~20 s — then fall back to `.noPDF`. The real generation time is **UNVERIFIED — confirm at source before relying on it**: measure it on aiccratest during execute, and set the budget from that measurement | Kenji Tanaka | specify |
| Q6 | *(Answered 2026-11-12 from the code: keep today's behavior.)* Every unit already gets the same microservice report: `generatePentahoReport` is `false` for all units, `loadClusterTemplateData()` takes the first `report_configurations` row (the table has no unit column; 6 rows locally), the file is named `AICCRA-Cluster-<id>-Summary-<date>.pdf`, and only the data carries the unit acronym (`loggedCenter`). The fix attaches that same PDF; per-unit templates or file names are out of scope | — | — |
| Q4 | Jira: is there an A2 ticket, or should one be created (parent epic to confirm)? | Kenji Tanaka | commit messages only |
| R1 | **Branch.** The fix patches code that exists only on `email-layout-default`. Either branch from it (stacked PR) or wait until it merges into `staging` | Kenji Tanaka | execute |
| R2 | Without a reachable microservice (local, or an outage), planning submits send the `.noPDF` copy — the honest fallback | — | Accepted |

Active Lessons applied: `KZ-…auth-flow-2` (test doubles assert their arguments), `KZ-…directory-abstraction-1`
(the sibling enumeration in §9 was done by grep across the tree, not limited to the two named files).

## 13. Success Criteria

- A planning submit whose PDF fetch returns bytes attaches them (same file name and `application/pdf` as before),
  uses the planning copy, and never writes to the HTTP response. Proven by a test with a fetch double that
  returns known bytes and asserts the exact bytes the send double received.
- A planning submit whose fetch fails or returns no bytes sends no attachment and uses the `.noPDF` copy with
  `projectID` and `phaseID` of the submission. Proven by a test.
- No submit or unsubmit email contains `"null"` as a title. Proven by a test with a null title.
- An unsubmit of a project with no leader contains no "Dear ,". Proven by a test.
- When the leader is the actor, TO is the leader and the greeting names the leader, both for an id above 127
  (distinct `Long` instances, e.g. 1082) and for one below 128 (e.g. 16). Proven by a test that builds the ids with `new Long` / `Long.valueOf` above 127.
- The WARN of both `unitTexts()` contains the unit id.
- Clean compile, Checkstyle delta 0, `-am` suite green.

## 14. Next Step

```text
/akili-specify bugfix/submit-unsubmit-email-defects
```

Bug Mode, Lite. Q5 is confirmed at the specify gate (Q1 and Q6 answered 2026-11-12). R1 (the branch) is decided before execute.
