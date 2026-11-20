# Archive Summary — Partner request routing per Global Unit (CLARISA or MARLO)

> **Delivered.** A per-Global-Unit specificity, `partner_requests_to_clarisa`, routes partner requests either to
> CLARISA or to the in-MARLO approval flow. Every unit starts in MARLO mode, so deploying changes nothing. Validation:
> PASS with warnings, 0 FAIL.

## 1. Document Control

| Item | Value |
|---|---|
| Spec ID | CHG-PARTROUTE-001 |
| Owner | Kenji Tanaka (IBD) |
| Branch | `revive-local-partner-request-approval`. It merges into `staging` with or after the revive work (CHG-PARTREQ-001) |
| Commits | `0edf846105` propose, `d19d4096d6` specify, `97954adbe3` T01, `b7a564a475` T02, `9ed5666173` T03 + T04, `3b34c2ceeb` T05, `b44a5421d5` validate |

## 2. Original Spec Path

`docs/specs/changes/partner-request-routing-specificity/`

## 3. Archive Date

2026-11-20

## 4. Final Status

**Complete.** 5 / 5 tasks `[x]`, each with an independent Reviewer PASS. No FAIL. `test-report.md` was not produced:
the owner accepted the execute-phase test evidence (validation W-6).

## 5. Requirements Delivered

| Area | Requirements |
|---|---|
| Routing | FN-001 … FN-006, FN-010 (stored parameters decide; CLARISA mode sends and stores nothing; visible failure, no fallback) |
| Unchanged paths | FN-007 (office requests local), FN-009 (pending requests decidable), NF-002 (MARLO mode regression-free) |
| Switch | FN-008 (Superadmin → Parameters → Specificities), MIG-001 (seed for types 1–4 that exist, no `custom_parameters` row) |
| UI | UI-001 (menu entry), UI-002 (country-office link), UI-003 (failure text by mode) |
| Security / ops | SEC-001 (review guards unchanged), SEC-002 (no secrets or email in logs or pages), OPS-001 (one log line per failure) |
| Hygiene | NF-001 (constant in both `APConstants`, no `isAiccra()`), NF-003 (n/a), NF-004 (gates) |

## 6. Files Changed Summary

| File | Change |
|---|---|
| `marlo-web/.../action/projects/PartnersSaveAction.java` | Routing branch: `readClarisaMode()`, `sendToClarisa()`, `buildClarisaPayload()`, `protected postToClarisa()` seam |
| `marlo-data/.../config/APConstants.java`, `marlo-web/.../config/APConstants.java` | `PARTNER_REQUESTS_TO_CLARISA` |
| `V2_6_0_20261120_1306__AddPartnerRequestsToClarisaSpecificity.sql` | Idempotent `parameters` seed |
| `menu-superadmin.ftl`, `projectPartners.ftl`, `partnersSave.ftl` | Menu entry, office link and failure text follow the mode |
| `PartnersSaveActionClarisaModeTest.java` (new), `PartnerRoutingSpecificityParityTest.java` (new), `PartnersSaveActionRequestTest.java` | Both-mode suite, parity and template pins, DD-5 rewrite of the old "no CLARISA" pin |
| `docs/specs/domain/admin/agent-context.md` | § *Routing mode* |

Total 1,047 LOC. The budget tripwire fired at T02 against ~470; the owner accepted ~1,050.

## 7. Test Evidence Summary

| Layer | Result |
|---|---|
| Unit / static | Parity (7), CLARISA mode (about 20), MARLO mode (DD-5): green |
| Regression | Full `marlo-web` suite with `-am`: 1385 / 0 / 0 |
| Falsifiers | T01 (a)–(c), T02 (a)–(f), T03 (a)–(c), plus T05's DB falsifier: each observed red, then green |
| Live (T05) | 9 steps plus the falsifier on throwaway DBs. Key screenshots are hashed in `execution.md` |

## 8. Validation Summary

**PASS with warnings** (`validation-report.md`). Build, coverage (21 / 21), design conformance and the constitution
check all PASS. W-5 (stale "T6" task references) was fixed during validation.

## 9. Accepted Warnings Or Follow-Ups

| # | Item | Owner / where |
|---|---|---|
| W-1 | **P-10:** the CLARISA test host answers 401 to the configured credentials. Get valid credentials per environment before switching any unit | Owner |
| W-2 | **P-9:** the `misAcronym` CLARISA accepts for non-AICCRA units and for `AICCRA_III` | Owner, with the CLARISA team |
| W-3 | Budget overrun (accepted) | Kaizen L1 |
| W-4 | 3 live CLARISA calls against 1 approved (all 401) | Kaizen L2 |
| W-6 | No `test-report.md` | Accepted |
| W-7 | `.agents/` persona drift (UNMARKED) | `staging` |
| W-8 | `OtpMailSenderTest` flaky under load | Outside this spec |
| — | Release notes: the switch, the login lag of menu and link, leftover pending requests, the `AICCRA_III` caveat, the `clarisa.api.*` requirement, and that the first Parameters save writes default rows | `marlo-release-notes` |

## 10. Historical Notes

- This spec reverses CHG-PARTREQ-001's "no specificity flag" decision (DD-1 there). The archived revive spec is not
  edited; this spec's requirements Decision Log is the record.
- DD-2: the submit reads the unit's stored parameters, not the session, because the session can hold another unit's
  value after a unit switch. Proven live by the T05 falsifier.
- A session restart (Orca) interrupted the T03 evidence re-run; it resumed with no state lost.

## 11. Diagrams

The spec has no `diagrams/` folder; the only diagram is the inline Mermaid flow in `design.md` §1. No baseline
diagram slot was made stale: the TRD's CLARISA context diagrams already show an outbound CLARISA integration.
