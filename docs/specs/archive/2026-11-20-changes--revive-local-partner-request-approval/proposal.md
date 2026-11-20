# Proposal — Revive in-MARLO approval of partner requests

**Answer first:** AICCRA partner requests go to CLARISA today and leave no trace in MARLO. The old in-MARLO
approval flow (Superadmin → *Institutions*) is still in the code, only disconnected. This change reconnects it as
the single path, makes MARLO the source of truth for institutions (Option A), lets only superadmins approve, sends
new-request alerts to the support mailbox, and moves every partner-request email into the branded email shell.

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | CHG-PARTREQ-001 |
| Spec Path | `changes/revive-local-partner-request-approval` |
| Type | Change |
| Deliverable | software |
| Approval Mode | gated |
| Owner | Kenji Tanaka (IBD) |
| Date | 2026-11-19 |
| Status | Approved by the owner, 2026-11-19 |
| Requirement source | Owner decisions in conversation, 2026-11-19: *"seria la opcion a y aprueba superadmin, y llega al buzon de soporte … los emails deben tomar el nuevo formato"*. Input analysis: same conversation. No Jira ticket yet |
| Depends on | `archive/2026-11-12-changes--email-layout-default` (delivered — provides `EmailLayout` and the `email.layout` shell) |
| Parallel-safe | yes — no shared migration or API contract with an active spec |

## 2. Intent

Institutions requested by MARLO users must be reviewed and created **inside MARLO** by a superadmin, as they were
until October 2021, instead of being sent to CLARISA.

## 3. Problem / Current Behavior

| # | Current behavior | Evidence |
|---|---|---|
| P1 | In AICCRA, a partner request is POSTed to CLARISA (`/api/partner-requests/create`) and **no `partner_requests` row is saved**. Other units still save locally | `PartnersSaveAction.java:519-538`; introduced by `8efd43ddfe` (2021-10-07) |
| P2 | The review screen is hidden for AICCRA: the *Institutions* entry is commented out in the AICCRA branch of both menus | `menu-superadmin.ftl:34`, `menu-admin.ftl:30` |
| P3 | Nobody is told a new request exists: the alert to the notification mailbox is commented out | `PartnersSaveAction.java` `save()`, the commented `sendMail.send(config.getEmailNotification() …)` block |
| P4 | Accepting creates the institution with the table's auto-increment id. In this DB, `institutions.id` is the CLARISA code, inserted by an external scheduled job (`added` always at `HH:00:01`; 898 rows in 2026, last 2026-11-10). `MAX(id)=11897`, `AUTO_INCREMENT=11898` — the next local id is the next CLARISA code | `PartnerRequestAction.addPartner()`; queried on local `aiccradb2`, 2026-11-19 |
| P5 | `addPartner` has no "already handled" guard: a second hit on the link creates a duplicate institution. It is a GET that changes state | `PartnerRequestAction.addPartner()`; `institutionRequestMacro.ftl:58` |
| P6 | Accept is not atomic: institution, HQ location and the two request rows are saved through three managers in separate transactions | `PartnerRequestAction.addPartner()` |
| P7 | Edit, reject and the office actions swallow exceptions without logging (`catch → success=false`); `addCountryOffices` / `rejectCountryOffices` dereference `countryOfficePOJO` before their null check | `EditPartnerRequestAction`, `RejectPartnerRequestAction`, `PartnerRequestAction` |
| P8 | The CRP-admin copy of the screen (`{crp}/marloInstitutions`) shows an Accept button that targets the superadmin-only `superadmin/addPartner` | `institutionRequestMacro.ftl:58`, `struts-superadmin.xml:148` |
| P10 | **Only the Accept of a new institution is superadmin-only.** Edit, reject, office accept and office reject are `*.do` JSON actions on `jsonStack`, whose only check is `SecurityJsonInterceptor` = "a user is in session". Any logged-in user can reject or edit any request, or accept an office request (which writes `institution_locations`) | `struts-json.xml:917-931`, `:1327-1344`; `SecurityJsonInterceptor.intercept()` |
| P9 | All partner-request emails use the legacy plain format (`email.dear` … `email.bye`), not the branded shell every role / welcome / OTP / submit email now uses. The office-request alert builds its subject in Java (`"[MARLO-" + …`) instead of i18n | `PartnerRequestAction`, `RejectPartnerRequestAction`, `RequestCountryOfficeAction.java:33` |

Historic data confirms the flow worked: ~5,000 requests across 18 units until mid-2020; AICCRA (unit 45) used it
from 2020-07 to 2021-08 (26 requests, 15 accepted, 11 rejected) — local `aiccradb2`, `partner_requests`.

## 4. Proposed Outcome

1. A user requesting a partner from any module gets a saved request in MARLO, never a CLARISA call.
2. The support mailbox receives a branded email announcing the new request, with a link to the review screen.
3. A superadmin opens Superadmin → *Institutions*, edits if needed, and accepts or rejects.
4. Accept creates the institution and its HQ location **once**, atomically, with a MARLO-owned id.
5. The requester receives a branded accepted / rejected email. The same holds for country-office requests.

## 5. Scope

| In scope | Notes |
|---|---|
| Remove the CLARISA POST from `PartnersSaveAction` so every unit saves locally | Includes `sendPartnerRequest()` and the hardcoded `misAcronym "aiccra"` |
| Restore the *Institutions* entry in the AICCRA branch of `menu-superadmin.ftl` | Superadmin only |
| Restore the new-request alert to the support mailbox (partner **and** country office) | Recipient: the `email.notification` property (`config.getEmailNotification()`) — owner decision, 2026-11-19 |
| Server-side superadmin check on every review action: edit, reject, accept, office accept, office reject | Fixes P10 |
| Hide the CRP-admin *Institutions* entry for every unit (`menu-admin.ftl:14`) and make `{crp}/marloInstitutions` superadmin-only | Fixes P8 — owner decision Q2, 2026-11-19. The view and route are **not** deleted here |
| Move the 6 emails into the branded shell (`EmailLayout.wrap`) with `email.role.*`-style keys | New request, accepted, rejected, office request, office accepted, office rejected |
| Accept made atomic and idempotent, through six `@Transactional` manager methods in `marlo-data` (create, edit, accept, reject, accept offices, reject offices — refined at specify) | Fixes P5, P6 |
| Edit, reject, accept, office accept and office reject only on POST | Fixes P5 (widened at specify: SEC-002) |
| Log swallowed exceptions; fix null-check order | Fixes P7 |
| Precondition check: CLARISA institution sync confirmed off before release | See §12 |
| Update `docs/specs/domain/admin/agent-context.md` with a `marloInstitutions` section | It lists the screen as undocumented |

## 6. Non-Goals

- Turning off the CLARISA sync job itself — it lives outside this repository (owner: CLARISA / infra).
- Pushing MARLO-created institutions back to CLARISA (that was Option C, rejected).
- The REST endpoints CLARISA used to create requests in MARLO (`InstitutionItem`, `CountryOfficeRequestItem`) and
  their `clarisa.*` email keys — left untouched.
- ~~Redesigning the review screen UI beyond what the fixes need.~~ *Moved into scope 2026-11-19 (owner, at
  specify Phase 2): the review screen and the partner request page follow the Partners / Innovations redesign.*
- Cleaning withdrawn CLARISA codes already in `institutions` (see memory: 277 withdrawn codes).
- Deleting the now-unreachable `admin/marloInstitutions.ftl` view and its route — a separate cleanup, proposed
  only.

## 7. Affected Users, Systems, And Specs

| Kind | Item |
|---|---|
| Users | Requesters in projects, funding sources, studies, capdev, POWB, AR; superadmins; support team |
| Java | `PartnersSaveAction`, `PartnerRequestAction`, `EditPartnerRequestAction`, `RejectPartnerRequestAction`, `RequestCountryOfficeAction`; `PartnerRequestManager(Impl)` in `marlo-data` |
| Views / JS / CSS | `menu-superadmin.ftl`, `menu-admin.ftl`, superadmin `marloInstitutions.ftl`, `institutionRequestMacro.ftl`, `rejectInstitutionPopup.ftl`, `allInstitutionsPopup.ftl`, `marloInstitutions.js` / `.css`, `partnersSave.ftl` / `.js` / `.css` (redesign) |
| Config | `struts-json.xml` (review routes), `struts-admin.xml` (CRP-admin route) |
| i18n | `global.properties` + `custom/aicrra`, `aiccra3`, `alliance`, `pabra`, `reap`, `test` (`marloRequestInstitution.*`) |
| Data | `partner_requests`, `institutions`, `institution_locations` — no schema change expected |
| External | CLARISA institution sync job (must be off) |
| Specs | `archive/2026-11-12-changes--email-layout-default` (shell contract), `domain/admin/agent-context.md` |

## 8. Visual Reference

- Source: Incumbent redesign (established world), added 2026-11-19 — the owner asked that the pages follow the
  design line of the redesigned Partners and Innovations sections.
- Location: `marlo-web/src/main/webapp/global/css/marlo-redesign.css` (`--marlo-*` tokens),
  `crp/css/projects/projectPartners.css` (`.ptn-*`), `crp/css/projects/deliverableForm.css` /
  `innovationForm.css` (`.dlf-*`), `crp/css/admin/crpPhases.css` (admin list screen on `--marlo-*`).
- Notes: screens covered — Superadmin → *Institutions* review screen (with its reject and all-institutions popups)
  and the partner request page `partnersSave.ftl`. The email look is fixed by the delivered `email.layout` shell.

## 9. Requirement Delta Preview

### ADDED Requirements

- New-request emails (partner and office) reach the support mailbox inside the branded shell.
- Accepting a request that is no longer active is refused with a message and creates nothing.
- Accept runs in one transaction: institution, HQ location and both request rows commit together or not at all.

### MODIFIED Requirements

- Partner requests from every unit are stored in MARLO; no unit calls CLARISA.
- Accepted / rejected / office emails use the branded shell and `email.role.*`-style greeting and sign-off; the
  office-request subject comes from i18n.
- Edit, reject, accept, office accept and office reject are POST actions.
- The superadmin menu shows *Institutions* for AICCRA.

### REMOVED Requirements

- The CLARISA partner-request POST (`sendPartnerRequest`). The "request failed" UI state is kept and now means "could not be saved" (design DD-8).

## 10. Approach Options

| Option | What | Pros | Cons |
|---|---|---|---|
| **1. Single local path** | Delete the CLARISA branch; every unit uses the local flow | Smallest diff; matches Option A (institutions are instance-wide, not per unit); removes a hardcoded AICCRA check | Going back to CLARISA needs a code change |
| 2. Specificity flag | Keep both paths behind a `parameters` / `custom_parameters` key | Reversible by data | Per-unit flag for an instance-wide decision: a unit left on CLARISA while the sync is off would lose requests. Needs a migration and constants in both `APConstants` |
| 3. Config property | Instance-level `marlo-*.properties` switch | Reversible per environment | Keeps dead CLARISA code alive; another untested branch |

## 11. Recommended Approach

**Option 1.** Option A makes MARLO the institution master for the whole instance, so a per-unit switch models the
wrong thing, and the other forks (FSRP, REAP, Alliance…) already run the local path. Within it:

1. `marlo-data`: six `@Transactional` methods on `PartnerRequestManager` (create, edit, accept, reject, accept
   offices, reject offices) that lock and reload the row, refuse a request that is no longer pending, and write
   institution + location + request rows together — refined at specify (`design.md` §6).
2. `marlo-web`: actions call them, log failures, accept writes only on POST, and send emails through a small shared
   builder that wraps the body with `EmailLayout.wrap`.
3. i18n: new shell-style copy for the six emails in `global.properties`; unit files override only subject prefixes.
4. Remove the CLARISA POST; restore the superadmin menu entry and the support alert.

## 12. Risks, Dependencies, And Open Questions

| # | Item | Mitigation / owner |
|---|---|---|
| R1 | **ID collision if the CLARISA sync is still running** when the first local accept happens (P4). The owner believes the sync is no longer running (2026-11-19), but the local copy `aiccradb2` holds 46 institutions added between 2026-10-19 and 2026-11-10, all at sync-like times (`03:00`, `09:00`, `11:00`, `19:00`), ids 10504–11897 | Hard precondition: confirm on the production DB that no `institutions` row was added after the last known sync, before the first local accept. If rows keep arriving, stop and decide with the CLARISA owner. Release note says so |
| R2 | Requests already pending in CLARISA are not in MARLO | Closed: the owner confirms none are pending (2026-11-19) |
| R3 | Removing the CLARISA path also affects AICCRA_III (`aiccra3`) — intended under Option A | Confirm both AICCRA instances share the decision |
| R4 | Email copy edits touch `.properties` — `MessageFormat` apostrophes | `marlo-verify` i18n gate |
| R5 | Superadmin screen loads every request of every unit with `findAll()` twice | Accept as-is (low volume) unless it shows in testing |
| R6 | Hidden CRP-admin copy still exists for non-AICCRA units with a non-working Accept (P8) | Closed by Q2: hidden and superadmin-only |

**Open questions**

- **Q1.** Closed: the recipient is the `email.notification` property of each environment.
- **Q2.** Closed (owner, 2026-11-19): hide the CRP-admin *Institutions* screen for every unit and make its route
  superadmin-only; keep the files.
- **Q3.** Closed: no requests are pending in CLARISA.
- **Q4.** Open: the sync is believed off, but the local copy shows rows up to 2026-11-10 (R1). Check production
  before release.

## 13. Success Criteria

- A request from a project in AICCRA creates one parent + one child `partner_requests` row and no CLARISA call.
- The support mailbox gets the new-request email inside the shell (`email_logs` row, rendered check).
- A superadmin accept creates exactly one institution + one HQ location; a repeated accept creates nothing.
- Reject and office accept/reject send shell emails to the requester; all six emails render in the shell.
- Clean compile, Checkstyle delta 0, i18n gate clean, unit tests for the manager's refusal rules and the email builder, and live rollback / concurrency checks on a throwaway DB copy.
- Both pages render in the Partners / Innovations redesign language with no Bootstrap `list-group`, `btn-success`/`btn-danger` or glyphicon, and pass the owner's screenshot review at 1280 and 390 px (added with the UI scope).

## 14. Next Step

```text
/akili-specify changes/revive-local-partner-request-approval
```
