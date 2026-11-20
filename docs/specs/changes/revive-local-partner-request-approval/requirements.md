# Revive in-MARLO approval of partner requests — Requirements

**Spec ID:** CHG-PARTREQ-001
**Status:** Approved 2026-11-19 (Phase 1; amended at Phase 2 — UI scope, judgment rounds 1–2)
**Owner:** Kenji Tanaka (IBD)
**Reviewers:** IBD tech lead
**Last Updated:** 2026-11-19
**Type:** Change
**Deliverable:** software
**Approval Mode:** gated
**Depth:** Standard
**Source:** `proposal.md` (approved by the owner 2026-11-19)
**Related PRD sections:** docs/prd.md — partner and institution management (control lists)
**Related UX/UI Design sections:** docs/ux-ui/design.md — screen inventory `admin/marloInstitutions.ftl`, `superadmin/marloInstitutions.ftl`
**Related TRD sections:** none changed
**Companion ai-context docs:** `reports/ai-context/role-assignment-email-catalog.md` (email copy style); `reports/ai-context/struts-critical-routing-catalog.md` (routes touched)
**Depends on:** `archive/2026-11-12-changes--email-layout-default` (delivered: `EmailLayout`, `email.layout`, `email.role.*`)

> **Answer first.** Partner and country-office requests are stored in MARLO for every Global Unit, never sent to
> CLARISA. Only a superadmin can edit, accept or reject them, and an accept creates the institution exactly once.
> The support mailbox is told about every new request, the requester is told about every decision, and all six of
> these emails use the branded shell.

---

## 1. Overview

Until October 2021 MARLO users requested new partners (institutions) and country offices from inside MARLO, and a
superadmin approved them on Superadmin → *Institutions*. AICCRA then started sending partner requests to CLARISA.
The owner has decided (Option A) that MARLO is again the source of truth for institutions. This spec reconnects the
in-MARLO flow as the only path, closes the security and integrity defects it carried, and moves its emails into the
branded shell.

## 2. Problem Statement

Current behavior, each claim with its evidence (run on `staging` `3e4d08bd7e`, local DB `aiccradb2`):

| # | Today | Evidence |
|---|---|---|
| C1 | In AICCRA / AICCRA_III a partner request is POSTed to CLARISA and no `partner_requests` row is saved; other units save two rows (original + editable copy) | `PartnersSaveAction.java:519-538` (`if (this.isAiccra())`) |
| C2 | The new-partner alert to the support mailbox is commented out for every unit | `PartnersSaveAction.java:543-549` |
| C3 | The new-office alert is sent, but plain, with a subject built in Java | `RequestCountryOfficeAction.java` `execute()`: `subject = "[MARLO-" + …` |
| C4 | Accepted / rejected / office emails use the legacy footer (`email.dear` … `email.bye`) and no shell | `PartnerRequestAction.java` `sendAcceptedNotficationEmail`, `sendAcceptedOfficeNotficationEmail`, `sendRejectOfficeNotificationEmail`; `RejectPartnerRequestAction.sendRejectedNotficationEmail` |
| C5 | The office accepted / rejected emails ignore the unit's email-notification switch; the partner ones honor it | `PartnerRequestAction.java` office senders call `sendMail.send` with no `validateEmailNotification` check |
| C6 | Accept is a GET link (`superadmin/addPartner?requestID=`) with no "already handled" check; a repeat creates a duplicate institution | `institutionRequestMacro.ftl:58`; `PartnerRequestAction.addPartner()` |
| C7 | Accept saves institution, HQ location and both request rows through three managers in separate transactions | `PartnerRequestAction.addPartner()` |
| C8 | Edit, reject, office accept and office reject only require a session user | `struts-json.xml:917-931, 1327-1344` on `jsonStack`; `SecurityJsonInterceptor.intercept()` checks `user != null` only |
| C9 | Errors in edit, reject and office actions are swallowed without a log line | `EditPartnerRequestAction.execute()`, `RejectPartnerRequestAction.execute()`, `PartnerRequestAction.addCountryOffices()` / `rejectCountryOffices()` |
| C10 | The *Institutions* entry is hidden in the AICCRA superadmin menu; the CRP-admin copy (`{crp}/marloInstitutions`, `crpAdminStack`) is visible for other units | `menu-superadmin.ftl:34`; `menu-admin.ftl:14, 30`; `struts-admin.xml:146-152` |
| C11 | `institutions.id` equals the CLARISA code; rows arrived from an external job until at least 2026-11-10; `AUTO_INCREMENT = 11898` | `aiccradb2`: 46 rows added 2026-10-19 → 2026-11-10 at `03/09/11/19:00`, ids 10504–11897 (35 of them 11863–11897) |

## 3. In-Scope Requirements

### Functional

- **CHG-PARTREQ-001-FN-001 — Local storage for every unit.** A partner request submitted from any module MUST be stored in MARLO as an original and an editable copy, for every Global Unit, and MUST NOT be sent to CLARISA.
- **CHG-PARTREQ-001-FN-002 — New-partner alert.** When a partner request is stored, an email MUST be sent to the address in the `email.notification` property, with the requester, the requested institution data, the source (module and item) and a link to the review screen.
- **CHG-PARTREQ-001-FN-003 — New-office alert.** When a country-office request is stored, the same mailbox MUST receive an email with the requester, the institution, the countries and the source project; its subject MUST come from i18n.
- **CHG-PARTREQ-001-FN-004 — Accept partner.** Accepting a pending partner request MUST create one institution with the request's name, acronym, website and type, plus one headquarters location in the request's country; mark the request accepted with its date and the new institution; close the original; and notify the requester.
- **CHG-PARTREQ-001-FN-005 — Accept once.** Accepting a request that is no longer pending (accepted, rejected or inactive) MUST create nothing and MUST tell the superadmin the request was already handled.
- **CHG-PARTREQ-001-FN-006 — Reject partner.** Rejecting MUST record the justification, the rejecting user and the date, close both rows, and notify the requester when the superadmin asks for it.
- **CHG-PARTREQ-001-FN-007 — Edit before deciding.** A superadmin MUST be able to edit name, acronym, website, type and country of the editable copy of a pending request, with a justification, without touching the original; an edit of a request that is no longer pending MUST change nothing and report "already handled".
- **CHG-PARTREQ-001-FN-008 — Office decisions.** Accepting office requests MUST add each requested country as a non-HQ location of the institution once, skipping one that already exists; rejecting MUST record the justification. Both MUST notify each requester, honoring the "send notification" choice on reject.
- **CHG-PARTREQ-001-FN-009 — Branded emails.** The six emails (new partner, new office, partner accepted, partner rejected, office accepted, office rejected) MUST be sent inside the branded shell, with the shell's greeting and sign-off style, and every visible text MUST come from i18n keys.
- **CHG-PARTREQ-001-FN-010 — Notification switch.** All six emails MUST honor the Global Unit's email-notification switch.
- **CHG-PARTREQ-001-FN-011 — Review screen reachable.** The *Institutions* entry MUST be shown in the superadmin menu for every Global Unit, AICCRA included.

### Security

- **CHG-PARTREQ-001-SEC-001 — Superadmin only.** Listing, editing, accepting and rejecting partner and office requests MUST be allowed only to a user for whom `canAccessSuperAdmin()` is true, enforced on the server for each action.
- **CHG-PARTREQ-001-SEC-002 — No state change on GET.** Edit, reject and accept partner, and accept / reject office, MUST change data only on an HTTP POST. *(Widened 2026-11-19 at design: edit and reject also write data.)*
- **CHG-PARTREQ-001-SEC-003 — CRP-admin screen closed.** The CRP-admin *Institutions* menu entry MUST be hidden for every Global Unit, and its route MUST refuse non-superadmins.

### UI

*(Added 2026-11-19 at specify Phase 2, owner request: the pages follow the Partners / Innovations redesign.)*

- **CHG-PARTREQ-001-UI-001 — Redesign line.** The review screen and the partner request page MUST use the redesign's visual language: the `--marlo-*` tokens, Figtree type scale, cards, buttons, badges, fields, notes and empty states of the redesigned Partners and Innovations sections. No Bootstrap `list-group`, `btn-success`/`btn-danger` or glyphicon remains on them.
- **CHG-PARTREQ-001-UI-002 — Review screen layout.** The review screen MUST show a page header with a short summary and the pending counts; one card per pending partner request with a status badge, the requested data, the requester, source and date, and the similar institutions found; one card per institution with pending office requests; and an empty state for each list when it has nothing pending.
- **CHG-PARTREQ-001-UI-003 — Changes made by the reviewer are visible.** When a request was edited, its card MUST show which fields differ from the original request, original and edited side by side.
- **CHG-PARTREQ-001-UI-004 — Decisions inside the page.** Edit opens inline in the card; reject and accept ask for confirmation in a dialog styled in the redesign (no browser `confirm()` / `alert()`); reject requires a justification and offers "notify the requester". The outcome (done, already handled, not authorized, failed) MUST be shown as an inline message in the redesign style.
- **CHG-PARTREQ-001-UI-005 — Request page.** The partner request page MUST present one form in a card: name, acronym, type, headquarters country, website, with required markers, help text and field errors in the redesign style; a primary "Send request" action; and success, "could not be saved" and validation states as redesign notes.
- **CHG-PARTREQ-001-UI-006 — Accessibility and width.** Both pages MUST keep visible focus, labels bound to fields, WCAG AA text contrast, keyboard-operable dialogs (focus moves in, Escape closes, focus returns), and work without horizontal scrolling down to 390 px wide.

### Data

- **CHG-PARTREQ-001-DA-001 — Atomic accept.** Each accept MUST commit the institution, its location and the request rows together, or nothing.
- **CHG-PARTREQ-001-DA-002 — No schema change.** The spec MUST work on the current `partner_requests`, `institutions` and `institution_locations` tables.

### Operations

- **CHG-PARTREQ-001-OPS-001 — Failures are logged.** Every failure in a request, a review write, or building / dispatching an email MUST produce one log line naming the action and the request id, through SLF4J, never with the user's email. (SMTP delivery failures happen in the mail thread, which logs them itself.)
- **CHG-PARTREQ-001-OPS-002 — Sync precondition.** The release MUST NOT be applied to an environment until a production check shows no `institutions` row added by the CLARISA job after the cut-over date.

### Non-Functional

- **CHG-PARTREQ-001-NF-001 — No unit-specific Java.** No new `isAiccra()` or hardcoded unit check; behavior is the same for all units.
- **CHG-PARTREQ-001-NF-002 — i18n hygiene.** Every added or changed `.properties` line passes the apostrophe / brace check (`marlo-verify` i18n gate).
- **CHG-PARTREQ-001-NF-003 — Gates.** Clean compile, Checkstyle delta 0 against HEAD, and the `marlo-web` suite green with `-am`.

## 4. Out-of-Scope

- Turning off the CLARISA sync job (outside this repository).
- Pushing MARLO-created institutions to CLARISA.
- The REST endpoints CLARISA used to create requests (`InstitutionItem`, `CountryOfficeRequestItem`) and the `marloRequestInstitution.clarisa.*` keys.
- Deleting `admin/marloInstitutions.ftl` and its route (separate cleanup).
- Cleaning withdrawn CLARISA codes in `institutions`.
- Redesigning other pages that use the partner request (projects, innovations, studies, funding sources) beyond the request page itself; the country-office request modal in `projectPartners.ftl`.
- A shared component library or a change to `marlo-redesign.css` tokens.

## 5. Personas Affected

| Persona | Effect |
|---|---|
| Any MARLO user requesting a partner or office (cluster leader, coordinator, PMU) | AICCRA requests no longer go to CLARISA; they get a branded decision email |
| Superadmin | Reviews requests on Superadmin → *Institutions* for every unit, on a redesigned screen |
| CRP admin | Loses the *Institutions* entry (it never let them accept) |
| Support team | Receives a branded alert for each new partner and office request |

## 6. Acceptance Criteria

**AC FN-001 — Local storage**
- GIVEN a logged-in user in AICCRA on a project's partners page
- WHEN they submit a partner request with name, type and country
- THEN two `partner_requests` rows exist (original with `partner_request_id` NULL, copy pointing to it), both active
- AND the page shows the "request sent" message
- BUT no HTTP call is made to CLARISA
- AND IT MUST behave the same in a non-AICCRA unit

**AC FN-002 / FN-003 — Alerts**
- GIVEN the unit's email-notification switch is on
- WHEN a partner (or office) request is stored
- THEN one email goes to `email.notification`, inside the shell, naming requester, institution data and source
- AND the partner alert links to Superadmin → *Institutions*
- BUT the email MUST NOT be sent when the switch is off, and the request is still stored

**AC FN-004 / FN-005 / DA-001 — Accept partner**
- GIVEN a pending request and a superadmin
- WHEN the superadmin accepts it
- THEN exactly one institution and one HQ location exist for it, the copy is `acepted=1` with date and `institution_id`, both rows are inactive, and the requester gets the accepted email
- WHEN the same accept is sent again (double click, back button, replayed POST)
- THEN no institution is created and the superadmin sees an "already handled" message
- AND IT MUST leave no institution behind if saving the location or the request fails

**AC FN-006 — Reject partner**
- WHEN a superadmin rejects with a justification and "send notification" checked
- THEN the copy is `acepted=0` with justification, user and date, both rows are inactive, and the requester gets the rejected email with the justification
- BUT with "send notification" unchecked, no email is sent

**AC FN-007 — Edit**
- WHEN a superadmin edits the copy with a justification
- THEN the copy changes and is marked modified, and the original is unchanged
- BUT an edit of a request that is no longer pending (or racing an accept) changes nothing and reports "already handled"

**AC FN-008 — Offices**
- WHEN a superadmin accepts office requests for countries A and B, and A already exists for the institution
- THEN only B is added (non-HQ), every request is closed, and each requester gets one office-accepted email per Global Unit of their requests
- WHEN rejecting with a justification
- THEN every request records it and, if asked, each requester gets one office-rejected email per Global Unit of their requests

**AC FN-009 / FN-010 — Emails**
- THEN each of the six emails is wrapped by the shell and contains no legacy footer text (`email.support.noCrpAdmins`, `email.bye`)
- AND every visible text resolves from an i18n key
- BUT none is sent when the unit's switch is off

**AC SEC-001 / SEC-002 / SEC-003 — Authorization**
- GIVEN a logged-in user who is not a superadmin
- WHEN they call edit, reject, accept partner, accept office or reject office (even crafting the request by hand), or open `{crp}/marloInstitutions`
- THEN the action refuses with "not authorized" and no row changes
- AND a GET to any of the five review actions, even from a superadmin, changes nothing
- AND the CRP-admin menu shows no *Institutions* entry in any unit

**AC UI-001 / UI-002 / UI-003 — Review screen**
- GIVEN two pending partner requests (one edited) and one institution with two pending offices
- WHEN a superadmin opens Superadmin → *Institutions*
- THEN the header shows "2 partner requests · 1 institution with office requests", each request is a card with its status badge, requester, source and date, and the edited one shows original vs edited for the changed fields only
- AND with nothing pending, each list shows its own empty state
- BUT no Bootstrap `list-group`, `btn-success`, `btn-danger` or glyphicon appears in the rendered page

**AC UI-004 — Decisions**
- WHEN the superadmin clicks Reject
- THEN a redesign dialog asks for a justification and offers "notify the requester"; confirming without a justification shows a field error and sends nothing
- AND after any decision an inline message states the outcome (done / already handled / not authorized / failed)
- BUT no browser `confirm()` or `alert()` is shown

**AC UI-005 — Request page**
- WHEN a user opens the partner request page and submits without name, type or country
- THEN each missing field shows its error under the field in the redesign style and the request is not sent
- AND a successful submit shows a success note; a save failure shows the "could not be saved" note

**AC UI-006 — Accessibility and width**
- THEN at 390 px and at 1280 px neither page scrolls horizontally, every control shows a focus ring, and the dialogs trap focus and close on Escape

**AC FN-011** — In AICCRA, Superadmin → Tools shows *Institutions* and opens the review screen.

**AC OPS-001** — A forced failure in each review action produces one ERROR/WARN line with the action and request id and no email address.

## 7. Defect Classes And Their Gates

| Defect class | Gate |
|---|---|
| Code does not compile / stale classes | Clean compile (`marlo-verify`) |
| Style regressions | Checkstyle delta vs HEAD |
| Duplicate or partial institution on accept | Unit test on the manager's refusal rules (repeat call on a non-pending request, wrong kind) **plus** live checks on a throwaway DB copy: a forced failure after the institution save leaves no row, and two concurrent accepts create one institution (unit tests on doubles cannot prove rollback or locking) |
| Authorization bypass on a JSON action | Unit test of the guard + live check as non-superadmin |
| Email not in shell / legacy footer left / wrong recipient | Unit test on the email builder (captured send arguments) |
| i18n apostrophe / brace loss | `marlo-verify` i18n gate |
| Page does not match the redesign / looks off | **No automated check** for visual fidelity — substitute: screenshots at 1280 and 390 px reviewed by the owner at the HITL pause, and `/akili-validate`'s design gate (Impeccable critique + audit) |
| Horizontal overflow, missing focus ring, low contrast | Rendered measurement in a real browser (scroll width vs viewport at two widths, focus visible, contrast) in the live task; Impeccable detector when its engine is available |
| Legacy markup left (`list-group`, `btn-success`, glyphicon) | Grep of the two rendered pages / their FTL in the UI tasks |
| Email renders badly in a mail client | **No automated check** — substitute: live send on a throwaway DB copy and owner review at the HITL pause (as in `email-layout-default`) |
| CLARISA sync still running in an environment | **No check in the repo** — substitute: OPS-002 production query before release (owner) |

## 8. Constitutional Compliance Checklist

- [x] Phase replication: not applicable — institutions and partner requests are not phased data.
- [x] Save validation: review actions are not form saves; validation lives in the action guards (design).
- [x] Permissions: every review action declares a superadmin check (SEC-001); the requester actions keep their stacks.
- [x] Specificity: none — behavior is the same for every unit (NF-001); Decision Log entry below.
- [x] Migrations: none (DA-002).
- [x] i18n: all new strings keyed (FN-009).
- [x] License header: required on any new Java file.
- [x] Code style: Checkstyle gate (NF-003).
- [x] REST: no `/api/*` change.
- [x] Audit: entities already use the audit hierarchy.
- [x] Dependency floors: unchanged.
- [x] Branching: feature branch from `staging`, merge into `staging`.

## 9. Open Questions

- None blocking. OPS-002 is a release precondition owned by Kenji Tanaka.

## 10. Decision Log

- 2026-11-19 — MARLO is the source of truth for institutions (Option A) — owner decision; avoids the CLARISA id collision.
- 2026-11-19 — Superadmin is the only approver; the CRP-admin screen is hidden everywhere and its route made superadmin-only, files kept — owner decision (proposal Q2).
- 2026-11-19 — New-request alerts go to the `email.notification` property of each environment — owner decision (proposal Q1).
- 2026-11-19 — No specificity flag: the CLARISA branch is removed for every unit — the decision is instance-wide; a per-unit flag would model the wrong thing (proposal §10, Option 1).
- 2026-11-19 — No requests are pending in CLARISA; nothing to migrate — owner statement (proposal Q3).
- 2026-11-19 (execute, T03 gate) — AC FN-008: "one email per requester" becomes "one email per requester **per Global Unit** of their requests" — owner approval; the email texts and the notification switch belong to each request's unit (design §10, FN-010), so one mixed email would bypass one unit's switch. Closure sweep: forward grep of "each requester" / "one email" / "one office" across `requirements.md`, `design.md`, `tasks.md` — hits at `tasks.md` T03 Requirements line and §5 Coverage row updated; `design.md` §10 "each requester" (recipient column) is unaffected; backward: no other spec cites FN-008.
- 2026-11-19 (execute, T05 gate) — PR 1 must work on the current screen: the minimal POST bridge (`type: 'POST'` on the four AJAX calls, accept link → POST form) moves from T08 / T09 into T07 — owner approval; T03 / T05 made the five review actions POST-only while the current JS / macro still send GET. Closure sweep: forward grep of `type: 'POST'` / "POST form" across the spec — `tasks.md` T07 scope + tests, T09 scope updated; T08 "accept as POST form" kept (redesign rewrite); backward: SEC-002 unchanged.
- 2026-11-19 (execute, T11a gate) — FN-007 must work on the current screen in PR 1: the edit form posted field names the action never read (pre-existing, also on `staging`; T11a `probes/t11a-ui-edit.out`). The parameter-name fix joins the T07 POST bridge — owner approval. Closure sweep: forward grep of "edit" in `tasks.md` T07 / T09 scope — both updated; backward: FN-007 text unchanged.
- 2026-11-20 (execute, T10 gate) — The request page's field labels and title move onto i18n keys (`marloRequestCreation.label.*`, `.pageTitle`) — owner approval; they looked up keys that never existed (pre-existing), and the owner's visual review in T11b must see the final copy. Closure sweep: `design.md` §5.5 request-page row and `tasks.md` T10 scope updated; backward: UI-005 / NF-002 unchanged.
- 2026-11-19 — The review screen and the partner request page follow the Partners / Innovations redesign (UI-001…006) — owner request at Phase 2; the proposal non-goal "redesigning the review screen" is withdrawn. Closure sweep: forward grep of "redesign" / "confirm()" across `proposal.md`, `requirements.md`, `design.md` — all hits updated; backward: no other spec cites these sections.
