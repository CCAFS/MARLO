# Validation Report — Branded email shell by default

**Verdict: PASS.** Three WARNs were found and resolved in the spec documents (owner option 2, §11). All 8 tasks are done with evidence. Every requirement and every BUT / AND clause has
test or live evidence. The clean build, the Checkstyle delta, the i18n review and the full suite (1137 tests, 0
failures) are green on `ad1a0f2d4c`. There is no FAIL. The three WARNs were drift in the documentation and in
observability; all three are now closed in the documents (§11).

## 1. Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/email-layout-default` |
| Spec ID | CHG-EMAIL-LAYOUT-001 |
| Deliverable / Depth | software / Standard |
| Validated tree | branch `email-layout-default` at `ad1a0f2d4c` (base `staging` `460ecbd5be`, 11 commits, 27 files) |
| Date | 2026-11-12 |
| Validator | Opus 5.5 (T3). The Implementer ran on sonnet, so author ≠ auditor holds |
| Inputs | `proposal.md`, `requirements.md`, `design.md`, `judgment.md` (APPROVED), `tasks.md`, `execution.md`, `test-report.md` |

## 2. Summary

| Area | Result | Note |
|---|---|---|
| Task completion | PASS | 8/8 `[x]`. Each task has an `execution.md` entry with a Reviewer PASS and a non-author re-run |
| File existence | PASS | Every file in design §2 changed as listed. Four new test files |
| Build integrity | PASS | Clean compile 2430 / 1063 / 115 files, `BUILD SUCCESS`, Maven exit 0 |
| Checkstyle | PASS | Delta 0 on all 12 changed Java files against `460ecbd5be` |
| Tests | PASS | 1137 run, 0 failures, 0 errors (re-run by the validator) |
| Requirement coverage | PASS | 11 FN + 3 NF requirements, every AC clause traced (§6) |
| Design conformance | PASS | W1 resolved: design §11 amended to match the shipped WARN messages |
| Spec documents | PASS | W2 and W3 resolved in `requirements.md` |
| Constitution impact | PASS | No shared constitutional file touched. The ai-context catalog was updated in T07 |
| Archive readiness | **Ready** | W1–W3 resolved (§11) |

## 3. Task Completion

| Task | Status | Evidence | Result |
|---|---|---|---|
| T01 Shell becomes the base value | `[x]` 2026-11-11 | execution.md T01. md5 of the moved block `6b52db6b…` | PASS |
| T02 Footer-once | `[x]` | execution.md T02. The footer-once test scans `custom/` | PASS |
| T03 Submit/unsubmit copy + link text | `[x]` | execution.md T03. `SubmitEmailCopyTest` | PASS |
| T04 Submit email | `[x]` | execution.md T04. `ProjectSubmissionEmailTest` | PASS |
| T05 Unsubmit email | `[x]` | execution.md T05. `UnsubmitProjectEmailTest` | PASS |
| T06 Opt-out (Q1: none) | `[x]` | execution.md T06. Q1 quoted. Opt-out test | PASS |
| T07 Docs | `[x]` | execution.md T07. Catalog and OTP spec Decision Log entry | PASS |
| T08 Live verification | `[x]` | execution.md T08. 25 sends (18401–18425). Owner render approval ("1 aprobado") | PASS |

No HALT and no Pivot. There were two spec amendments with owner approval: AC FN-011 at T05 and AC FN-002 at T06.
The owner raised the LOC tripwire to ~950 after T04. The spec finished at ~909 LOC, using 9 of 10 review rounds.

## 4. File Existence

| Design §2 row | File | Changed | Result |
|---|---|---|---|
| resources | `global.properties` | yes | PASS |
| resources | `custom/aicrra.properties`, `custom/aiccra3.properties` | yes | PASS |
| resources | `custom/test.properties` | yes | PASS |
| resources | `custom/ciat.properties` (−2 lines) | yes | PASS |
| resources | `custom/pabra.properties` | yes | PASS |
| Java | `ProjectSubmissionAction.java` | yes | PASS |
| Java | `UnsubmitProjectAction.java` | yes | PASS |
| Java | `EmailLayout.java`, `OtpMailSender.java`: Javadoc and comments only | yes. The validator found 0 non-comment lines in the diff | PASS |
| tests | `EmailLayoutTest`, `UnitTextProviderTest`, `OtpMailSenderTest`, `OtpRequestCodeActionTest` | yes | PASS |
| tests (new) | `SubmitEmailCopyTest`, `ProjectSubmissionEmailTest`, `UnsubmitProjectEmailTest`, `EmailSendDoubles` | created, each with the GPL header | PASS |
| docs | `reports/ai-context/role-assignment-email-catalog.md` | yes | PASS |
| docs | `changes/center-email-otp-login/requirements.md` (+1 Decision Log line, DD-8) | yes | PASS |

There are no unexpected production files. `EmailPlainTextTest` was listed as "none expected" and is unchanged.

## 5. Build Integrity

The validator re-ran every gate after `ad1a0f2d4c`, with no other build or agent running.

| Gate | Command | Result |
|---|---|---|
| Compile + tests | `rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes && mvn -o -pl marlo-web -am test` | exit 0, `BUILD SUCCESS`. Compiled 2430 (marlo-data) / 1063 (marlo-web) / 115 (tests). **Tests run: 1137, Failures: 0, Errors: 0, Skipped: 0** |
| Checkstyle | marlo-verify `checkstyle.sh` on each changed `.java` at HEAD and at `460ecbd5be` | `UnsubmitProjectAction` 2→2, `ProjectSubmissionAction` 1→1, the other 10 0→0 (4 new). **Delta 0** |
| Java hygiene | marlo-verify `java-hygiene-check.sh` on the 12 files | exit 0 |
| i18n | the 55 added `.properties` lines since base, of which 43 are non-comment | 0 undoubled `'`, 0 braces that are not arguments |
| Environment boot | not run | Not warranted: T08 already booted the stack and sent 25 emails live |

The `[ERROR] [OtpEligibility]` lines in the log come from other test classes that exercise error paths on purpose.
They are not failures.

## 6. Requirement Coverage

`test-report.md` is current: its code under test is `c24addcdde` plus the test files, which were committed unchanged
in `ad1a0f2d4c`. It is reused as the primary evidence. The validator cross-checked the matrix against
`requirements.md` §6 clause by clause and spot-checked the rows below in the code and data.

| Req | Clause | Task(s) | Evidence | Validator spot-check | Result |
|---|---|---|---|---|---|
| FN-001 / FN-006 | Shell for a unit with no file, all five emails. BUT no body loss. AND support and notice once | T01, T02, T04, T05, T08 | OMS / PSE / UPE / ELT tests. T08 QATEST rows | `grep -c '^email.layout='` → only `global.properties` | PASS |
| FN-002 | Opt-out: body only. AND footer once; for OTP the notice at most once | T01, T06 | ELT, OMS, PSE, UPE opt-out tests | The DD-9 block is in the comment above `email.layout` (`global.properties:3900`) | PASS |
| FN-003 | AICCRA / AICCRA_III role, welcome and OTP unchanged. BUT no change in support, greeting or sign-off | T01, T02, T08 | One-off comparison of 105 keys (0 differ), T01 md5, T08, owner acceptance | No `email.role.*` line of aicrra or aiccra3 in the diff | PASS (accepted gap 1: not a committed test) |
| FN-004 | One definition | T01 | ELT#testOnlyGlobalDefinesTheShell… | grep above | PASS |
| FN-005 | Footer once for every overriding unit and for OTP. AND alliance keeps `getStarted` | T02 | ELT footer-once tests, which scan `custom/` | Overrides left: alliance (`getStarted`), aicrra and aiccra3 (all five). ciat and pabra have none | PASS |
| FN-007 | Link to project P and phase F in both cycles. BUT no 102080 / 407 | T03, T04, T08 | PSE send-path tests, SECT, T08 Query B | `grep -l projectID=102080` → no file. `values[6]` is now set unconditionally | PASS |
| FN-007 | Failed PDF → `.noPDF`. BUT no "attached" | T04 | PSE#aFailedPdfInPlanning… | `pdfAttached` is computed after PDF generation | PASS (empty-array PDF deferred by the owner) |
| FN-008 / FN-009 | Appendix A copy. AND no Flagship and no `<br><br>` | T03 | SECT | 0 PMU / Flagship / `<br><br>` in the three base keys | PASS |
| FN-010 | to / cc / bcc, subject and attachment unchanged. BUT the subject may change in the FN-011 case | T04, T05, T08 | PSE / UPE recording doubles. 0 changed `*.email.subject` lines | Both `sendMail.send` calls keep their recipient and attachment arguments. Only subject and body now come from `email` | PASS (accepted gap 3) |
| FN-011 | Own unit's texts. BUT no "Technical Implementation Team" in AICCRA. AND the reverse case | T04, T05, T08 | PSE / UPE cross-unit sequence. T08 rows 18412–18415 | `unitTexts()` reads `loggedCrp` (submit) and `globalUnitProject.getGlobalUnit()` (unsubmit) | PASS |
| NF-001 | Opting a unit in or out needs no Java change | T06 | DD-9 is i18n only | — | PASS |
| NF-002 | i18n correctness | T01–T03, T06 | Validator review in §5 | 0 hits | PASS |
| NF-003 | Readable plain-text part | T03, T08 | SECT, EmailPlainTextTest, T08 attempt 2 | — | PASS |

No `PRODUCT_BUG` was found. Every negative constraint (BUT) and every strict validation (AND IT MUST) maps to a
named test. The owner accepted all four gaps in `test-report.md` §9 on 2026-11-11.

## 7. Linting & Code Quality

Checkstyle delta 0, hygiene clean (§5). Error handling: both `unitTexts()` methods catch `RuntimeException`, which
covers a null unit or a null `loggedCrp`. They log a WARN and fall back to the action's own provider, so the email
is still sent, as DD-6 requires. **No security or permission surface changed.** The unescaped `{7}` stays out of
scope (design §13).

### 4R advisory — new (non-gating)

| Lens | Finding |
|---|---|
| Reliability | Pre-existing, out of scope. `UnsubmitProjectAction:165-167, 276-281` compares `Long` ids with `!=`, which compares object references. Ids above 127 are not cached, so the "leader is the actor" test is effectively always true. Requirements §4 describes the "Dear ," case as "when the project leader is the user who unsubmits". The real mechanism is the reference comparison combined with a missing leader name. The same pattern appears in 4 sibling actions and in 143 comparisons across 78 files. Route it to the planned submit/unsubmit bugfix proposal |
| Readability | `LOG` is `static final` in `UnsubmitProjectAction` and `static` in `ProjectSubmissionAction` (carried from T05) |

### 4R advisory — carried forward from `execution.md` (unresolved, non-gating)

| From | Finding |
|---|---|
| T01 | `OtpMailSender` Javadoc wraps early. The `global.properties` comment says "the block below" where the block is above the line. (The sender-level wrap gap was closed by `/akili-test`.) |
| T02 | `getResource("/custom").toURI()` works only while test resources are a directory. The OTP footer test has no `units.size()` guard |
| T03 | `contains("407")` could give a false positive on future copy. `"phaseID=407"` would be more precise. `#cccccc` is hardcoded in the blockquote |
| T03 | T03 must never be cherry-picked without T04: planning emails would render `href="null"`. Ship the branch whole |
| T04 | Empty-array PDF from the microservice path: a 0-byte attachment, and the email still says "attached". Deferred by the owner. Possible fix: `buffer.remaining() > 0` |
| T04 | `unitTexts()` does one DB read even when notifications are off. Negligible |
| T05 | `{7}` / `{3}` are inserted raw into HTML. `projectInfo.getTitle()` / `.get(0)` can throw after the save. Both pre-existing |
| T06 | The opt-out fixture scans by line prefix. It could be bounded between the "Opt-out block" marker and `email.layout=` |
| T07 | Catalog L36–40 still says submissions call the `email.*` keys. After DD-4 they read `email.role.*` |
| T08 | FN-010 recipients are code evidence, not live. The FN-011 page-load order is the Implementer's account. "Dear ," without a leader name |

## 8. Design Conformance

| Check | Result | Note |
|---|---|---|
| DD-1 to DD-6, DD-9, DD-10 | PASS | Implemented as recorded |
| DD-5: variant chosen after PDF generation | PASS | `pdfAttached` is computed after the generation block, and both sends use it |
| DD-6: whole email from the named unit, fallback on failure | PASS | Subject, body, footer and shell all come from `texts` |
| §11: "logs one WARN with the unit id" | WARN (W1) → resolved | Neither WARN message includes the unit id. Unsubmit logs "The texts of the unit owning the project could not be read…"; submit logs "The texts of the submitting unit could not be read…". The fallback works, but the log cannot tell which unit's `crp_custom_file` failed |
| P-14: 10 of the 30 senders wrap after this spec | PASS | `grep -rl 'EmailLayout\.wrap'` → 11 files (10 senders plus `OtpMailSender`), 41 call sites |
| Proposal success criteria (§13) and non-goals (§6) | PASS | All six criteria have evidence. No unit outside AICCRA / AICCRA_III / PABRA had its submit copy changed. The legacy `email.bye` is untouched |
| Visual Reference | PASS | The shell was reused verbatim (T01 md5). The owner approved the render in T08 |

**Cross-document figure check**

| Figure | Documents | Result |
|---|---|---|
| Budget ~380 LOC, "matches the design budget" | design §15, tasks.md "Estimate" vs execution.md (~909, tripwire ~950) | The overrun is recorded with owner approval in the `execution.md` Budget Adjustments. Planning documents keep the plan. PASS |
| 30 senders: 10 wrap, 20 do not | requirements §4, proposal §6, design P-14 | Consistent, re-counted. PASS |
| 1125 + 12 = 1137 tests | execution.md Summary, test-report.md, this run | Consistent. PASS |
| 43 changed `.properties` lines | test-report NF-002 | Re-counted: 43 non-comment lines (55 with comments). PASS |
| requirements §7: "License header: no new Java file" | requirements §7 vs the diff | WARN (W2) → resolved. Four new Java test files exist, all with the GPL header; the row now lists them |
| Decision Log placement | requirements §9 vs the end of the file | WARN (W3) → resolved. The "Amended by judgment round 2" entry sat under Appendix A.5; it now follows round 1 in §9 |

**Design Review (UI).** Not applicable. The spec changes no screen. The only visual surface is the rendered email,
which `requirements.md` assigns to a human check. The owner approved the 25 rendered emails in T08.
`impeccable-bridge` was not loaded, and no detector rows apply.

## 9. Test Evidence Summary

| Suite | Tests | Result | Source |
|---|---|---|---|
| Backend unit (spec classes) | 157 across 8 classes | PASS | test-report.md §2, Leader re-run |
| Full `marlo-web` suite | 1137 | PASS | Re-run by the validator, §5 |
| Live (T08) | 25 sends, Queries A–C, before/after | PASS | execution.md T08 |
| Render | 25 emails | Owner approved | execution.md T08 |
| Falsifiers | 12 new tests, each red under a production mutation | PASS | test-report.md §3 |

## 10. Agent Guide / Constitution Impact

`execution.md` has no `## Constitution Impact` section. No module boundary or public surface moved, and none of
`CLAUDE.md`, `AGENTS.md`, `.agents/*`, the TRD or the templates was touched. The ai-context catalog was updated in
T07. **Pending for `/akili-archive`:** the T07 advisory, a one-clause note at catalog L36–40 saying that submit and
unsubmit now read `email.role.*`.

## 11. Remediation

| # | Level | Finding | Action | Status |
|---|---|---|---|---|
| W1 | WARN | Fallback WARN omits the unit id (design §11) | Owner option 2: design §11 amended to describe the shipped messages; adding the id goes to the submit/unsubmit bugfix proposal | Resolved 2026-11-12 |
| W2 | WARN | requirements §7 "no new Java file" is stale | Row now lists the four new test files with the GPL header | Resolved 2026-11-12 |
| W3 | WARN | Round-2 Decision Log entry sits under Appendix A.5 | Moved into §9 after the round-1 entry | Resolved 2026-11-12 |
| A1 | Advisory | `Long !=` in `UnsubmitProjectAction`. The §4 description of "Dear ," is imprecise | Fold into the submit/unsubmit bugfix proposal (with the empty PDF and the null title). Keep the 143-site sweep separate | Open — follow-up spec |
| A2 | Advisory | Catalog L36–40 | Apply at `/akili-archive` | Open — archive |

## 12. Archive Readiness Recommendation

**Ready to archive.** All tasks are `[x]`, there is no FAIL, the tests cover every key scenario, and W1–W3 are
closed in the spec documents (correction recorded in the `requirements.md` Decision Log, 2026-11-12, with its
closure sweep). The two advisories stay open for the bugfix proposal and for `/akili-archive`.

```text
/akili-archive changes/email-layout-default
```
