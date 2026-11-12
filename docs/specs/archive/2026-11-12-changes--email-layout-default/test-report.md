# Branded email shell by default — Test Report

**Status: PASS.** Every acceptance-criterion clause of `requirements.md` §6, including each BUT / AND, has test or live
evidence. 12 tests were added to close four coverage gaps; each went red under a deliberate production break. No
product defect was found. The full `marlo-web` suite is green: 1137 tests, 0 failures, 0 errors.

## 1. Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/email-layout-default` |
| Spec ID | CHG-EMAIL-LAYOUT-001 |
| Deliverable / Depth | software / Standard |
| Code under test | branch `email-layout-default`, `3612479922` (code at `c24addcdde`) plus the uncommitted test files below |
| Date | 2026-11-12 |
| Leader | Opus 5.5 (T1) |
| Tester | `akili-tester` (opus) — differs from the Implementer model (`akili-implementer`, sonnet; no escalation recorded in `execution.md`) |

## 2. Summary

| Item | Value |
|---|---|
| Suites run | 1 — backend unit. Integration was proven live in T08 (`execution.md` § T08) and is cited, not repeated. No frontend or E2E suite: the change is email text and Java send paths; the rendered email was owner-reviewed in T08 |
| Testers spawned | 1 (Deployment Rule: one substantial suite) |
| Tests added | 12, in 4 existing classes plus a new doubles helper |
| Full suite | `rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes && mvn -o -pl marlo-web -am test` → exit 0; compiled 2430 / 1063 / 115; **Tests run: 1137, Failures: 0, Errors: 0** (1125 before + 12) |
| Leader re-run (non-author) | `mvn -o -pl marlo-web -am test -Dtest=EmailLayoutTest,OtpMailSenderTest,ProjectSubmissionEmailTest,UnsubmitProjectEmailTest,SubmitEmailCopyTest,UnitTextProviderTest,OtpRequestCodeActionTest,EmailPlainTextTest` → exit 0, **157 run, 0 failures, 0 errors** — VERIFIED |
| Checkstyle | marlo-verify `checkstyle.sh --baseline` on the 5 test files: HEAD 0, working tree 0, delta 0 |
| Hygiene | `java-hygiene-check.sh`: exit 0 on the 5 files |
| Product bugs | none |
| Skills / effort | Tester: `marlo-verify`, `tdd`; effort high |

**Flake noted, not counted as evidence.** One intermediate full run failed with 57 errors (`Can't find bundle for base name global`):
`target/classes` held no resources while the VS Code Java language server was running, most likely a concurrent write
to `target/`. An unchanged re-run gave 0 failures, and the final wiped run (resources copied: 2158) is the evidence above.

**Precedent.** The two new send-path tests are the first in MARLO that drive an action's private send path
(`sendNotficationEmail`) — through plain subclasses, reflection and hand-written recording doubles; no Mockito, no
Struts container, no Hibernate.

## 3. Backend Unit Tests

### Files

| File | Change |
|---|---|
| `marlo-web/src/test/java/org/cgiar/ccafs/marlo/utils/EmailSendDoubles.java` | new (184 lines, GPL header): `SendMailS` stand-in that records every argument, config and lookup stand-ins (the lookup records which unit's custom file is read), documented opt-out texts |
| `…/action/projects/ProjectSubmissionEmailTest.java` | +6 tests |
| `…/action/json/project/UnsubmitProjectEmailTest.java` | +3 tests |
| `…/utils/EmailLayoutTest.java` | +2 tests |
| `…/utils/OtpMailSenderTest.java` | +1 test |

Author tests from execute, cited rather than rewritten: `EmailLayoutTest`, `UnitTextProviderTest`, `OtpMailSenderTest`,
`OtpRequestCodeActionTest`, `EmailPlainTextTest`, `SubmitEmailCopyTest`, `ProjectSubmissionEmailTest`,
`UnsubmitProjectEmailTest`.

### Falsifiers (production mutated, one run, reverted with `git checkout -- marlo-web/src/main`)

| New test | Mutation | Red message |
|---|---|---|
| `OtpMailSenderTest#aUnitWithoutACustomFileSendsTheHtmlPartInsideTheBaseShellWithTheFooterOnce` | `OtpMailSender` sends `htmlBody` unwrapped | `AssertionError: wrapped in the base shell: <div style="font-family:Segoe UI…` |
| `EmailLayoutTest#testOnlyGlobalDefinesTheShellAndACustomFileMayOnlyOptOut` | `email.layout=<div>{0}</div>` added to `alliance.properties` | `custom files that define a shell of their own: [alliance.properties] expected:<0> but was:<1>` |
| `EmailLayoutTest#testCiatAndPabraInheritTheBaseRoleFooterAndShowItOnce` | `email.role.bye` with the notice added to `ciat.properties` | `[ciat.properties still overrides email.role.bye, … do-not-reply notice x2, …] expected:<0> but was:<3>` |
| `ProjectSubmissionEmailTest#theSendPathKeepsRecipientsSubjectAndAttachmentWithThePdf` | cc and bcc swapped in the PDF send call | `CC is the submitter, then the project leader expected:<[ana@unit.test, leo]@unit.test> but was:<[notify]@unit.test>` |
| `…#theSendPathWithoutAPmuAddressMailsTheSubmitter` | same | `expected:<[ana@unit.test, leo]@unit.test> but was:<[notify]@unit.test>` |
| `…#theReportingCycleSendsNoAttachmentAndLinksTheSubmittedPhase` | link phase hardcoded to `407` | `reporting links the submitted project and phase: <table role="presentation"…` |
| `…#aFailedPdfInPlanningSendsTheNoPdfCopyAndNoAttachment` | `assembleEmail(…, isPlanningActive())` instead of `pdfAttached` | fails on `contains("download the cluster summary as a PDF</a>")` |
| `…#eachSubmissionUsesTheTextsOfItsOwnUnitWhateverWasSentBefore` | `unitTexts()` always looks up unit 45 | `AICCRA_III subject expected:<…[Technical Implementation Team] [C77]…> but was:<…[cluster] [C77]…>` |
| `UnsubmitProjectEmailTest#theSendPathKeepsRecipientsAndSubjectAndAttachesNothing` | to and cc swapped | `TO is the project leader expected:<[]ana@unit.test> but was:<[luis@unit.test, ]ana@unit.test>` |
| `…#eachUnsubmissionUsesTheTextsOfTheProjectsUnitWhateverWasSentBefore` | `unitTexts()` always returns aicrra | `expected:<…AICCRA_III [Technical Implementation Team [C77]…> but was:<…[cluster [C77]…>` |
| `ProjectSubmissionEmailTest#aUnitWithTheDocumentedOptOutSendsBothVariantsPlainWithTheFooterOnce` | `email.role.support.noCrpAdmins` appended twice | `with PDF support address expected:<1> but was:<2>` |
| `UnsubmitProjectEmailTest#aUnitWithTheDocumentedOptOutSendsItPlainWithTheFooterOnce` | same | `support address expected:<1> but was:<2>` |

Under the last mutation only the two opt-out tests went red; the in-shell tests stayed green, as they should — the
in-shell support key is empty.

## 4. Frontend Unit Tests

Not applicable: the change touches no JavaScript, CSS or FreeMarker view.

## 5. Integration Tests

Live verification ran as task T08 against a throwaway copy of the AICCRA database (25 sends, `email_logs`
18401–18425; QATEST, AICCRA, AICCRA_III). Queries, owner verdicts and cleanup are recorded in `execution.md` § T08 and
cited in the matrix below. Not repeated here.

## 6. E2E Tests

Not applicable. The only visual surface is the rendered email, a human-only check approved by the owner in T08
("1 aprobado").

## 7. Coverage & Traceability

Abbreviations: PSE `ProjectSubmissionEmailTest`, UPE `UnsubmitProjectEmailTest`, OMS `OtpMailSenderTest`, SECT
`SubmitEmailCopyTest`, ELT `EmailLayoutTest`. "(new)" marks a test added by this run.

| Requirement | Scenario / clause | Test type | Test file or evidence | Result | Gap or notes |
|---|---|---|---|---|---|
| FN-001/006 | THEN a unit with no custom file: OTP inside the shell | unit, sender | OMS#aUnitWithoutACustomFileSendsTheHtmlPartInsideTheBaseShellWithTheFooterOnce (new) | PASS | closed the sender-level gap (T01 advisory) |
| FN-001/006 | THEN no-file unit: role and welcome inside the shell | unit + live | ELT#testBaseLayoutWrapsTheBodyInTheShell, #testRoleEmailShowsTheFooterOnce…; T08 rows 18401/18402 | PASS | `CrpUsersAction` itself is not driven (gap 2) |
| FN-001/006 | THEN no-file unit: submit and unsubmit inside the shell | unit, send path | PSE#eachSubmission… (unit 50), UPE#eachUnsubmission… (unit 50) | PASS | |
| FN-001/006 | BUT must NOT lose any part of the body | unit | ELT#testBaseLayoutWrapsTheBodyInTheShell; OMS (new); SECT exact copy | PASS | |
| FN-001/006 | AND support address once, notice once | unit + live | OMS (new); PSE/UPE `assertShellOnce`; ELT footer-once tests; T08 Query A | PASS | |
| FN-002 | THEN an opted-out unit sends the body without the shell | unit | ELT#testUnitThatCopiesTheDocumentedOptOutBlock…; OMS#aUnitThatOptsOutOfTheShell…; PSE#aUnitWithTheDocumentedOptOut… (new); UPE#aUnitWithTheDocumentedOptOut… (new) | PASS | submit/unsubmit opt-out newly covered |
| FN-002 | AND support and notice once; OTP: support once, notice at most once (amended 2026-11-11) | unit | same tests | PASS | |
| FN-003 | THEN AICCRA / AICCRA_III role, welcome and OTP text unchanged | one-off + live | resolved values of every `email.*`, `login.otp.*`, `footer.*` key (105 per unit, layout included), `460ecbd5be` vs HEAD: 0 differ for aicrra and aiccra3; T08 18403/18404 vs 18300/18301; T01 md5; owner acceptance | PASS | not a committed test (gap 1) |
| FN-003 | BUT must NOT differ in support line, greeting or sign-off | one-off | same comparison (`email.role.support`, `.dear`, `.bye` included) | PASS | |
| FN-004 | only `global.properties` defines the shell; a custom file may only opt out with `{0}` | unit | ELT#testOnlyGlobalDefinesTheShellAndACustomFileMayOnlyOptOut (new) | PASS | |
| FN-005 | footer once: base, alliance, aicrra, aiccra3 — role and OTP | unit | ELT#testRoleEmailShowsTheFooterOnce…, #testOtpEmailShowsTheFooterOnce… | PASS | |
| FN-005 | ciat and pabra (no role override left) | unit | ELT#testCiatAndPabraInheritTheBaseRoleFooterAndShowItOnce (new) | PASS | |
| FN-005 | AND alliance keeps its `getStarted` override | unit | ELT#testRoleEmailShowsTheFooterOnce… (scan includes alliance) | PASS | |
| FN-011 | THEN an AICCRA submit/unsubmit after an AICCRA_III one uses the AICCRA copy | unit, send path | PSE#eachSubmissionUsesTheTextsOfItsOwnUnit… (new), UPE#eachUnsubmissionUsesTheTextsOfTheProjectsUnit… (new) — also assert the unit id the lookup double received | PASS | |
| FN-011 | BUT no "Technical Implementation Team" in AICCRA subject or body | unit + live | same; UPE#testAiccraUsesClusterWords…; T08 18412/18413 | PASS | |
| FN-011 | AND the reverse: AICCRA_III after AICCRA uses its own copy | unit + live | same tests (sequence 47, 45, 47, 50); T08 18414/18415 | PASS | |
| FN-011 | unsubmit reads the project's origin unit | unit | UPE send-path tests (`crp_custom_file@<origin id>`) | PASS | |
| FN-007 (no PDF) | THEN a failed PDF in planning sends the `.noPDF` copy with the link | unit, send path | PSE#aFailedPdfInPlanningSendsTheNoPdfCopyAndNoAttachment (new); PSE#testAiccraWithoutPdf… | PASS | not reproducible live (T08) |
| FN-007 (no PDF) | BUT must NOT say "attached" | unit | same (`!contains("attached")`, attachment arguments null) | PASS | empty-array PDF: owner-deferred, not tested |
| FN-007 (link) | THEN the link carries the submitted `projectID` and `phaseID`, planning and reporting | unit + live | PSE#theSendPathKeepsRecipients… (phase 501), #theReportingCycle… (phase 503); SECT#testPabraSubmitEmailsLinkTheSummaryFromTheArgument; T08 Query B | PASS | PABRA at copy level; link code is shared |
| FN-007 (link) | BUT no `projectID=102080` or `phaseID=407` | unit + live | same + SECT `checkUnit`; T08 `has_bad=0` | PASS | |
| FN-008/009 | THEN body matches Appendix A with every argument filled | unit | SECT#testAiccraCopyMatchesAppendixA1 / A2, #testBaseCopy… | PASS | |
| FN-008/009 | AND no "Flagship" in the base; no `<br><br>` in the six keys | unit + live | SECT `checkUnit`, #testBaseCopy…; T08 Query A (flagship 0) | PASS | |
| FN-010 | THEN to / cc / bcc unchanged | unit, send path | PSE#theSendPathKeepsRecipients…, #theSendPathWithoutAPmuAddressMailsTheSubmitter; UPE#theSendPathKeepsRecipients… — exact arguments the recording double received | PASS | expected values are the pre-change rules, untouched by the spec diff (gap 3) |
| FN-010 | THEN subject unchanged | unit + git + live | literal subjects per unit; 0 changed `(un)submit.email.subject` lines since `460ecbd5be`; T08 attempt 2 before/after | PASS | |
| FN-010 | THEN attachment name unchanged | unit, send path + live | PSE#theSendPath… (`Full_Cluster_Report-AICCRA-C77-\d{8}-\d{4}\.pdf`, `application/pdf`); T08 before/after | PASS | |
| FN-010 | BUT the subject may differ in the FN-011 case | — | FN-011 rows | PASS | |
| NF-002 | every changed `.properties` line survives `MessageFormat` | review + unit | 43 added lines since `460ecbd5be`: 0 single `'`, 0 non-argument braces; SECT renders the keys; ELT#testAiccraEmailCopyKeepsItsApostrophes | PASS | |
| NF-003 | the plain-text part reads as sentences with arguments filled | unit + live | SECT (`EmailPlainText.fromHtml`); EmailPlainTextTest; OMS (text part unwrapped); T08 attempt 2 (9 rows, markup 0) | PASS | |
| Defect class | compile / Checkstyle / i18n / footer / link / shell / copy | gates | §2 and above | PASS | |
| Defect class | the email looks wrong when rendered | human | T08 owner approval | PASS (human) | cannot be automated here |

## 8. Remediation

None required: no failing test and no product defect in scope.

## 9. Accepted Gaps

1. **FN-003 is not a committed test.** The "before" values exist only in git history, so no repeatable unit test can
   compare them. Evidence: the Tester's one-off comparison (0 of 105 keys differ per AICCRA unit), T08, the T01 md5
   and the owner's acceptance.
2. **Role and welcome emails are not driven through `CrpUsersAction`.** Coverage rests on the `EmailLayout.wrap`
   tests over the base properties plus live rows 18401/18402; driving the action would cost far more setup than the
   email path is worth.
3. **FN-010 recipients are proven against the pre-change rules in the code, not a before/after send.** Live recipient
   values are masked by `SendMailS.isRestrictedToSupport`, so live data cannot show them either.
4. **Out of scope by owner decision (2026-11-11), not tested:** empty PDF attached when the summary microservice is
   unreachable; the "null" project title; `email_logs.fileContent` stored as NULL; "Dear ," on unsubmit without a
   leader name.
