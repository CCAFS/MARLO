# Archive Summary — Revive in-MARLO approval of partner requests

> **Outcome: delivered.** Partner and country-office requests are stored in MARLO for every Global Unit; only superadmins decide them, on POST, through one transactional manager that locks and re-reads the row, so an accept happens exactly once. The support mailbox gets the request alerts and requesters get the decision emails, all six in the branded shell. Both pages use the redesign. Validation: **0 FAIL, 7 accepted WARN**.

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | CHG-PARTREQ-001 |
| Original spec path | `docs/specs/changes/revive-local-partner-request-approval/` |
| Archive path | `docs/specs/archive/2026-11-20-changes--revive-local-partner-request-approval/` |
| Archive date | 2026-11-20 |
| Branch | `revive-local-partner-request-approval` (from `staging` `3e4d08bd7e`); HEAD `ce3952df54`; not pushed beyond `d4dd9c8a3b` |
| Owner | Kenji Tanaka (IBD) |
| Approval mode | gated |

## 2. Final Status

| Item | Result |
|---|---|
| Tasks | 12 / 12 `[x]` |
| Validation | **ARCHIVE-READY WITH ACCEPTED WARNINGS.** First pass: 3 FAIL (F1 design gate, F2 security, F3 `nested-cards`). Re-validation: F2 and F3 fixed; F1 waived by the owner, pending the authority write. 0 FAIL, 7 WARN |
| Accepted by the owner | W1–W7, accepted by invoking `/akili-archive` after the verdict (`validation-report.md` §13) |

## 3. Requirements Delivered

| Group | IDs | Evidence |
|---|---|---|
| Functional | FN-001 … FN-011 (FN-008 amended: one email per requester **per Global Unit**) | T11a / T11b live, unit suites |
| Security | SEC-001 / SEC-002 / SEC-003; staging falsifier C8 reproduced → branch returns 403 | `probes/t11a-falsifier-staging.out` |
| Data | DA-001 atomic accept (proven live), DA-002 no schema change | `probes/t04-*` |
| Operations | OPS-001 logging. OPS-002 is a release precondition the owner runs on production | `execution.md` `## Release note` |
| UI | UI-001 … UI-006 (owner readings recorded for UI-001 and UI-005); owner visual approval | `probes/t11b-review.html` |
| Non-functional | NF-001 (no unit check in Java; AICCRA / AICCRA_III "Cluster C" through i18n), NF-002, NF-003 | `validation-report.md` §6 |

## 4. Files Changed Summary

Diff `3e4d08bd7e..ce3952df54`, product code: about +5,500 / −1,200 lines across 35 files.

| Area | Files |
|---|---|
| marlo-data | `PartnerRequestManager` / `Impl` (six `@Transactional` methods, five of them lock + refresh), `PartnerRequestDAO` / `MySQLDAO` (`findForUpdate`), new `PartnerRequestOutcome` |
| marlo-web Java | New `PartnerRequestEmails`; `PartnersSaveAction` (CLARISA removed), `RequestCountryOfficeAction`, `PartnerRequestAction`, `EditPartnerRequestAction`, `RejectPartnerRequestAction` |
| Config | `struts-json.xml`, `struts-admin.xml`, `global.properties`, `custom/aicrra` / `aiccra3` / `pabra` `.properties` |
| UI | Review screen (`marloInstitutions.ftl`, the three macros, CSS, JS, `allInstitutionsPopup.js`), request page (`partnersSave.ftl` / CSS / JS), menus |
| Tests | `PartnerRequestManagerGuardTest`, `PartnerRequestEmailsTest`, `PartnerRequestReviewGuardTest`, `PartnersSaveActionRequestTest`, `RequestCountryOfficeActionAlertTest` |
| Docs | `docs/specs/domain/admin/agent-context.md` (new `marloInstitutions` section), `reports/ai-context/role-assignment-email-catalog.md` §17 |

## 5. Test Evidence Summary

| Layer | Result |
|---|---|
| Unit | 1,360 tests green; a falsifier was executed red-then-green for every task |
| Live | T04: rollback, lock + refresh, concurrency. T11a / T11b: every AC, the staging falsifier, the six emails as sent |
| Design | Impeccable engine `ready` 0.1.18: source scan 0; request page URL scans 0 after the fix; first-visit captures recorded; critique 26/40 and 28/40 |
| Missing | No `test-report.md` (W1, accepted) |

## 6. Validation Summary

| Finding | Resolution |
|---|---|
| F2: `href` scheme (P1 security, pre-existing) | Fixed in the card macro and in `allInstitutionsPopup.js` (five sinks). The falsifier re-injects all three payloads |
| F3: `nested-cards` | Fixed (`.prq-help`); detector exit 0 |
| F1: first-visit + shared chrome | Waived by the owner (`validation-report.md` §11.1); **pending authority write** on `staging` |

## 7. Accepted Warnings and Follow-ups

| ID | Item | Where it is done |
|---|---|---|
| W2, W3 | Production checks before release: OPS-002 (no recent CLARISA-job inserts) and P-25 (`SELECT @@GLOBAL.sql_mode;`, stop if strict) | owner, production |
| W4, W7 | `docs/ux-ui/design.md`: §7 tokens (DD-13), viewports, a *Design Waivers* section with the three approved rows | `/akili-constitution` on `staging` |
| W1 | No `test-report.md` | accepted |
| W5 | `tasks.md` §9 Definition of Done boxes left unticked | accepted (evidence lives in `execution.md`) |
| W6 | Design-critique P2 items | `execution.md` follow-up 23 |
| — | 25 follow-ups in total. Highlights: CLARISA REST writers without guards (R-7); dead `getClarisaAPI*` / `ExternalPostUtils`; `swagger/dist/js/request.js:524` same `href` pattern; website scheme check on save; office-accepted email lists skipped countries | `execution.md` `## Follow-ups` |
| — | Push and the two PRs to `staging` (PR 1 up to `d7378d5cda`, PR 2 the rest) | owner |

## 8. Historical Notes

- **Budget.** Estimated at ~1,650 LOC, delivered at about 5,500 LOC. The tripwire fired at T03 and the owner raised the ceiling twice. The cause was tests backed by falsifiers, OPS-001 log tests, and the CSS / JS of the redesign.
- **Plan gaps found at the gates, all fixed with the owner's approval:**
  - PR 1 alone would have broken the current screen: the routes became POST-only while the client still sent GET. Fixed by a POST bridge in T07.
  - The edit form's field names never matched the action's parameters (pre-existing, FN-007). Found in T11a.
  - An XSS through the requester's website (pre-existing). Found at validate.
- **Privacy.** The owner's address leaked into a committed probe file. It was scrubbed and the commit was amended before any push. Every later probe was grepped for personal data before commit.
- **Host restart.** Orca restarted during T11a. The worker was resumed from its transcript, with no loss.

## 9. Diagrams

The spec has no `diagrams/` folder; `design.md` §1 holds one inline Mermaid flowchart, which is still accurate. No baseline diagram slot became stale.
