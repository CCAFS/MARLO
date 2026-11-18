# Archive Summary — Branded email shell by default

**Outcome: delivered and validated (PASS).** Every Global Unit now sends role, welcome, OTP, submit and unsubmit
emails inside one branded shell, with the footer shown once. Submit and unsubmit use their own unit's texts and
link to the project and phase that were submitted. All 8 tasks passed, 1137 tests are green, the owner approved
the render, and validation found 0 FAIL (its 3 WARNs were closed in the documents).

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | CHG-EMAIL-LAYOUT-001 |
| Owner | Kenji Tanaka (IBD) |
| Depth / Deliverable | Standard / software |
| Branch | `email-layout-default` from `staging` `460ecbd5be`. Not pushed at archive time; merges into `staging` |
| Last spec commit before archive | `cc15631fe4` |

## 2. Original Spec Path

`docs/specs/changes/email-layout-default/`

## 3. Archive Date

2026-11-12 → `docs/specs/archive/2026-11-12-changes--email-layout-default/`

## 4. Final Status

| Phase | Result | Artifact |
|---|---|---|
| Propose | Approved 2026-11-11 | `proposal.md` |
| Specify | Approved after 2 judgment rounds (JUDGMENT: APPROVED) | `requirements.md`, `design.md`, `tasks.md`, `judgment.md` |
| Execute | 8/8 PASS. 1 Reviewer FAIL (T08 attempt 1). No HALT, no Pivot | `execution.md` |
| Test | PASS. 12 tests added, 1137 / 0 / 0 | `test-report.md` |
| Validate | PASS. 0 FAIL. 3 WARN, all resolved | `validation-report.md` |

## 5. Requirements Delivered

| Req | Delivered |
|---|---|
| FN-001 / FN-006 | The shell is the base `email.layout`. It applies to units with no custom file, and to submit and unsubmit |
| FN-002 | The opt-out block (DD-9) is documented in `global.properties` and tested. No unit uses it (Q1) |
| FN-003 | AICCRA / AICCRA_III role, welcome and OTP output unchanged (0 of 105 keys differ) |
| FN-004 | One shell definition, in `global.properties` |
| FN-005 | Footer once for every unit. The ciat and pabra role overrides were removed |
| FN-007 | The summary link carries the submitted project and phase in both cycles. A failed PDF sends the `.noPDF` copy |
| FN-008 / FN-009 | Appendix A copy for AICCRA, AICCRA_III and the neutral base |
| FN-010 | Recipients, subject wording and attachment unchanged |
| FN-011 | Submit and unsubmit resolve every text from the unit each one names |
| NF-001–003 | No Java or database change is needed per unit. i18n is clean. The plain-text part is readable |

## 6. Files Changed Summary

`git diff --stat 460ecbd5be..cc15631fe4`: 28 files: 8 in this spec folder and 20 outside it.

| Area | Files |
|---|---|
| Java (prod) | `ProjectSubmissionAction`, `UnsubmitProjectAction` (assembly extracted, unit provider, shell). `EmailLayout` and `OtpMailSender` changed in Javadoc only |
| i18n | `global.properties`, `custom/aicrra`, `aiccra3`, `test`, `ciat`, `pabra` |
| Tests | 4 new (`SubmitEmailCopyTest`, `ProjectSubmissionEmailTest`, `UnsubmitProjectEmailTest`, `EmailSendDoubles`) and 4 updated (`EmailLayoutTest`, `UnitTextProviderTest`, `OtpMailSenderTest`, `OtpRequestCodeActionTest`) |
| Docs | `reports/ai-context/role-assignment-email-catalog.md`. `changes/center-email-otp-login/requirements.md` (DD-8 note) |

Size: ~909 LOC against a ~380 estimate. The owner raised the tripwire twice (to ~800, then ~950). 9 of 10 review
rounds were used.

## 7. Test Evidence Summary

- Full `marlo-web` suite: 1137 run, 0 failures, 0 errors. The validator re-ran it on 2026-11-12 after a clean
  compile.
- 12 tests added by `/akili-test`. Each went red under a deliberate production mutation.
- Live: 25 sends on a throwaway AICCRA DB copy (`email_logs` 18401–18425). The owner approved the render.

## 8. Validation Summary

PASS. Checkstyle delta 0 on the 12 changed Java files. Hygiene clean. Every AC clause is traced. W1 (the fallback
WARN has no unit id) was resolved by amending design §11. W2 (a stale license checklist row) and W3 (a misplaced
Decision Log entry) were fixed in `requirements.md`.

## 9. Accepted Warnings Or Follow-Ups

| # | Follow-up | Route |
|---|---|---|
| 1 | Submit/unsubmit bugfix: empty PDF attached when the summary microservice fails (`buffer.remaining() > 0`); "null" project title; "Dear ," fallback; `Long !=` id comparison in `UnsubmitProjectAction`; unit id in the fallback WARN | `/akili-propose` (bugfix) |
| 2 | `email_logs.fileContent` stored as NULL on attachment rows | Separate investigation |
| 3 | Sweep of 143 `Long` `==` / `!=` id comparisons in 78 files | Optional, separate |
| 4 | Catalog L36–40 still says submissions use the `email.*` keys | Kaizen pending item P3 (apply on `staging`) |
| 5 | Pre-existing, out of scope: no server-side permission check on `unsubmitProject.do`; blank justification; unescaped `{7}` / `{3}`; stale menu after submit | Separate tickets |
| 6 | T03 must never be cherry-picked without T04 (`href="null"` in planning) | Merge the branch whole |

## 10. Historical Notes

- Judgment round 1 found that the design premise "submit runs `i18nFile`" was false (JD-1). It became DD-6:
  both actions read their own unit's custom file through `UnitTextProvider`.
- At execute, two AC clauses contradicted the verbatim copy they governed: "PMC" in AC FN-011, and the
  do-not-reply notice in the OTP part of AC FN-002. Both were amended with owner approval (Decision Log, T05 and
  T06).
- Q1 was settled by owner decision: every unit gets the shell and none opts out.
- `/akili-test` built the first MARLO tests that drive an action's private send path without Mockito, Struts
  or Hibernate.

## 11. Diagrams

One inline Mermaid diagram (`design.md` §1, architecture flow). There is no `diagrams/` folder and no
interactive view. No baseline diagram slot went stale.
