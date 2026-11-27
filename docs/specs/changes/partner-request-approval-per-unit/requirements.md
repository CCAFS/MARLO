# Partner request approval per Global Unit — Requirements

**Spec ID:** CHG-PARTUNIT-001
**Status:** Approved 2026-11-27 (Phase 1; Q-6 closed by the owner; SEC-001 narrowing approved at the Phase 2 gate)
**Owner:** Kenji Tanaka (IBD)
**Reviewers:** IBD tech lead
**Last Updated:** 2026-11-27
**Type:** Change
**Deliverable:** software
**AI Agent:** no
**Approval Mode:** gated
**Depth:** Full. The proposal said Standard; raised because the change rewrites the authorization of five write routes (auth work is Full by the `/akili-specify` depth table)
**Source:** `proposal.md` (approved by the owner 2026-11-27, Q-1 to Q-4 resolved as recommended)
**Depends on:** `archive/2026-11-20-changes--revive-local-partner-request-approval` (`CHG-PARTREQ-001`) and `archive/2026-11-20-changes--partner-request-routing-specificity` (`CHG-PARTROUTE-001`), both in `staging` at `a54160ab47`
**Related PRD sections:** docs/prd.md — partner and institution management (control lists)
**Related UX/UI Design sections:** docs/ux-ui/design.md — screen inventory `admin/marloInstitutions.ftl`, `superadmin/marloInstitutions.ftl`
**Related TRD sections:** docs/trd/trd.md §8.2 (authorization layers), §8.3 (edit gates)
**Companion ai-context docs:** `reports/ai-context/struts-critical-routing-catalog.md`, `reports/ai-context/interceptor-validator-playbook.md`, `reports/ai-context/role-assignment-email-catalog.md` §17, `docs/specs/domain/admin/agent-context.md` § *Partner Requests*

> **Answer first.** The admin of a Global Unit lists, edits, accepts and rejects the partner and country-office
> requests **of that unit**, and an accept creates the institution at once. Who may decide is checked **per
> request, against the request's own unit**, so a crafted id or a stale tab cannot reach another unit's request. The
> superadmin keeps a global list with the unit shown and can still decide anything. An accept is refused when the
> same institution (same normalized name **and** same headquarters country) already exists. New-request alerts go to
> the unit's admins, with the support mailbox in copy.

---

## 1. Overview

`CHG-PARTREQ-001` revived local approval of partner requests and made it superadmin-only. `CHG-PARTROUTE-001` let
each unit route its requests to CLARISA or keep them in MARLO. Both kept one global reviewer. The owner has decided
that approvals belong to each Global Unit (2026-11-27). The data already supports it: every request row carries
the unit it was raised in. This spec scopes the listing, the decision rules, the routes and the alert email by that
unit, and protects the shared institution catalog that more approvers will now write to.

## 2. Problem Statement

Requests are **stored** per unit but **reviewed** globally:

- Each partner request (original and copy) is stamped with the session unit (`PartnersSaveAction.java:445-446`), and each office request too (`RequestCountryOfficeAction.java:122`). No row lacks a unit in the local copy (`partroute_local`, 2026-11-27: `select count(*) from partner_requests where global_unit_id is null` → `0`), and requests come from 17 units.
- The review list loads every unit's requests (`PartnerRequestAction.java:251-255`, `partnerRequestManager.findAll()` with no unit filter).
- The five decision methods check existence, kind and pending state, never the unit (`PartnerRequestManagerImpl` `refusePartner` / `refuseOffice`, read 2026-11-27).
- Every review route requires the global superadmin privilege (`struts-superadmin.xml:136-158`, `struts-json.xml:917-945`, `:1341-1370`; `BaseAction.canAccessSuperAdmin()` = `Permission.FULL_PRIVILEGES`, `BaseAction.java:707-709`).
- The new-request alert goes to the global support mailbox and links to the superadmin screen (`PartnersSaveAction.java:669`).

So a unit's own administrators cannot act on what their users ask for, and one superadmin carries every unit's
queue. Moving the queue to the units adds a second problem this spec must handle: `institutions` is one catalog for
every unit, with no unit column (`PartnerRequestManagerImpl.java:125-132` inserts with no unit), and accept checks
nothing before inserting. The catalog already holds 57 groups of rows with the same normalized name (case and spaces ignored, accents
kept); in 42 of them the rows also share a headquarters country (`partroute_local`, 2026-11-27, design Premise
Ledger P-15).

## 3. In-Scope Requirements

### Functional

- **CHG-PARTUNIT-001-FN-001 — The unit admin's list.** Admin → *Request Institutions* MUST list the pending partner
  requests and the pending country-office requests whose Global Unit is the unit the admin is working in, and no
  other unit's request.
- **CHG-PARTUNIT-001-FN-002 — The unit admin decides.** An admin of a unit MUST be able to edit, accept and reject a
  pending partner request of that unit, and accept and reject pending country-office requests of that unit, with
  the same outcomes, locks and messages `CHG-PARTREQ-001` delivered for the superadmin.
- **CHG-PARTUNIT-001-FN-003 — Accept is final.** Accepting a partner request as a unit admin MUST create the
  institution in the same action, with no further approval step.
- **CHG-PARTUNIT-001-FN-004 — The superadmin's global list.** Superadmin → *Request Institutions* MUST keep listing
  the pending requests of every unit, MUST show the unit of each request, and MUST let the superadmin narrow the list
  to one unit.
- **CHG-PARTUNIT-001-FN-005 — The superadmin still decides.** A superadmin MUST be able to edit, accept and reject
  any unit's request, as today (owner decision Q-4).
- **CHG-PARTUNIT-001-FN-006 — Duplicate guard.** Accepting a partner request MUST be refused, with nothing written,
  when an active institution already exists whose name equals the request's name after normalization (case-insensitive,
  leading and trailing spaces removed, inner runs of spaces collapsed to one) **and** one of whose headquarters
  locations is the request's country (an institution may have more than one). The refusal MUST name the existing
  institution. *(Q-2 and Q-6, closed 2026-11-27.)*
- **CHG-PARTUNIT-001-FN-007 — No override.** The duplicate refusal MUST NOT be bypassable from the screen. The
  reviewer's way forward is to edit the request (FN-002) or reject it.
- **CHG-PARTUNIT-001-FN-008 — Office batches are checked per request.** In a batch of office requests, a request
  the user may not decide (SEC-001) MUST be refused on its own, and the requests the user may decide MUST still be
  processed.
- **CHG-PARTUNIT-001-FN-009 — Alert to the unit's admins.** The new-partner and new-office alerts MUST be addressed
  to the active admins of the request's unit, with the support mailbox in copy, and MUST link to that unit's admin
  review screen. When the unit has no active admin with an email, the alert MUST go to the support mailbox as today.
  The unit's email-notification switch MUST keep applying (`CHG-PARTREQ-001` FN-010).
- **CHG-PARTUNIT-001-FN-010 — Decision emails unchanged.** The four emails sent to the requester after a decision
  MUST keep their recipients, texts and timing (`CHG-PARTREQ-001`, `role-assignment-email-catalog.md` §17); the
  approver in copy is whoever decided, admin or superadmin.
- **CHG-PARTUNIT-001-FN-011 — Pending requests carry over.** Requests stored before the release MUST appear in their
  unit's admin list with no data migration.

### UI

- **CHG-PARTUNIT-001-UI-001 — Admin menu entry.** The Admin menu MUST show *Request Institutions* to a superadmin or a
  user who administers the session unit (SEC-001's definition) when the unit is in MARLO mode, and MUST hide it in CLARISA mode, like the superadmin
  entry (`CHG-PARTROUTE-001` UI-001).
- **CHG-PARTUNIT-001-UI-002 — Same screen.** The admin route MUST render the redesigned review screen of
  `CHG-PARTREQ-001` (dialogs, inline outcomes, accessibility), and every decision taken from it MUST return to the
  admin screen, never redirect to the superadmin one.
- **CHG-PARTUNIT-001-UI-003 — Unit on the superadmin list.** Each partner request and each office group on the
  superadmin list MUST show the unit acronym; the unit filter MUST offer only units that have pending requests.
- **CHG-PARTUNIT-001-UI-004 — Duplicate message.** The duplicate refusal MUST show an i18n message naming the
  existing institution's name and id, on the screen the reviewer is on.

### Security

- **CHG-PARTUNIT-001-SEC-001 — Who may decide.** Editing, accepting or rejecting a request MUST be allowed only to a
  superadmin, or to a user who **administers the request's own Global Unit**: an active user with an active
  membership of that unit, in a unit whose admin area is switched on, holding there the `CRP-Admin` role or the admin
  edit privilege (owner decision Q-1; membership and admin-area conditions added at judgment round 1, J-7 / J-10,
  because without them the admin screen cannot open). The unit the user's session points to MUST NOT decide this.
  The same definition decides who sees the admin list (SEC-003), the menu entry (UI-001) and who is alerted (FN-009).
- **CHG-PARTUNIT-001-SEC-002 — Refusal writes nothing and tells nothing.** A refused decision MUST write nothing,
  send no email, and MUST NOT return any data of the refused request (name, acronym, requester, unit) in the
  response.
- **CHG-PARTUNIT-001-SEC-003 — Who may list.** The admin list MUST be reachable only by a superadmin or a user who
  administers the session unit (SEC-001's definition, not the wider existing admin-area rule); every other user MUST
  be refused before any request is read.
- **CHG-PARTUNIT-001-SEC-004 — A request without a unit.** A request whose unit is empty MUST be decidable only by a
  superadmin and MUST appear only on the superadmin list.
- **CHG-PARTUNIT-001-SEC-005 — POST-only kept.** Every decision route MUST keep refusing a non-POST with nothing
  written (`CHG-PARTREQ-001` SEC-002), for admins and superadmins alike.

### Operations

- **CHG-PARTUNIT-001-OPS-001 — One line per cross-unit refusal.** Each request refused under SEC-001 MUST produce
  exactly one WARN line naming the action, the user id, the request id and the request's unit id, with no request
  content and no email address.
- **CHG-PARTUNIT-001-OPS-002 — One line per duplicate refusal.** Each FN-006 refusal MUST produce exactly one INFO
  line naming the request id and the matched institution id.

### Non-Functional

- **CHG-PARTUNIT-001-NF-001 — No unit-specific code.** No `isAiccra()`, unit acronym literal or hardcoded
  `global_unit_id` may decide listing, authorization or recipients.
- **CHG-PARTUNIT-001-NF-002 — Revive guarantees hold.** Every behaviour `CHG-PARTREQ-001` and `CHG-PARTROUTE-001`
  delivered MUST hold for both roles: one institution per accept under concurrent clicks, the refusal order
  (not found, wrong kind, already handled), POST-only, strings-only JSON, the routing modes. Their tests stay green,
  except those pinning "superadmin only", which are rewritten to pin SEC-001.
- **CHG-PARTUNIT-001-NF-003 — i18n hygiene.** Every added or changed `.properties` line passes the `marlo-verify`
  apostrophe / brace check.
- **CHG-PARTUNIT-001-NF-004 — Gates.** Clean compile, Checkstyle delta 0 against HEAD on changed Java files, and the
  `marlo-web` suite green with `-am`.

### Removed

- **CHG-PARTUNIT-001-RM-001 — The unused admin view.** `crp/views/admin/marloInstitutions.ftl`, which is no longer
  rendered (`agent-context.md` § *JSON contract and UI*), MUST be removed or replaced by the shared screen, so one
  view serves both routes.

## 4. Out-of-Scope

- A per-unit institution catalog; `institutions` stays shared.
- A second approval step by a superadmin (owner decision 2026-11-27).
- Cleaning the 42 existing same-name same-country groups in `institutions` (P-15 of the design).
- The REST writers `InstitutionItem` / `CountryOfficeRequestItem` (`CHG-PARTREQ-001` R-7). `/api/*` is not routed in this deployment.
- Who may submit a request, and the CLARISA routing switch (stays superadmin, `CHG-PARTROUTE-001` FN-008).
- The CLARISA id collision of identity-generated institution ids (proposal R-4).
- Duplicate detection for country-office requests (an existing location is already skipped, `SKIPPED_EXISTING`).

## 5. Personas Affected

| Persona | Effect |
|---|---|
| Unit admin (`CRP-Admin` of the unit) | New duty: reviews the unit's partner and office requests; receives the new-request alerts |
| Superadmin | Keeps the global list, now with the unit shown; fallback reviewer |
| Requester | Unchanged forms; the decision email may now name a unit admin as approver |
| MARLO support mailbox | In copy of new-request alerts instead of the main recipient (main recipient only for units with no admin) |

## 6. Acceptance Criteria

### FN-001 / SEC-003 — The unit admin's list

- GIVEN pending requests of units 45 and 47, and a user who is `CRP-Admin` of 45 only, working in 45
- WHEN they open Admin → *Request Institutions*
- THEN they see the pending partner and office requests of 45
- BUT it must NOT show any request of 47
- AND IT MUST refuse a user who administers neither unit and is not a superadmin, before any request is read

### FN-002 / FN-003 — The unit admin decides

- GIVEN a pending partner request of unit 45 with a type and a country, and the admin of 45
- WHEN the admin accepts it
- THEN exactly one institution with its headquarters location is created, the request is closed as accepted, and the requester is emailed with the admin in copy
- AND IT MUST NOT require any further approval for the institution to be usable
- AND edit, reject, accept offices and reject offices behave for the admin as they do for a superadmin today

### SEC-001 / SEC-002 / OPS-001 — Cross-unit refusal

- GIVEN the admin of 45 only, and a pending request of 47
- WHEN they post accept, reject or edit for that request id (crafted, or from a page opened earlier)
- THEN nothing is written and no email is sent
- AND the response says "not allowed" and carries no name, acronym, requester or unit of the request
- AND exactly one WARN line names the action, the user id, the request id and unit 47
- BUT it must NOT matter which unit the admin's session points to: the request's unit decides

### SEC-001 — Session unit does not decide

- GIVEN a user who is `CRP-Admin` of 47 but whose session points to 45
- WHEN they post a decision for a pending request of 47
- THEN the decision is applied
- AND, the other way round, an admin of 45 whose session points to 47 is refused for a request of 47

### FN-004 / FN-005 / UI-003 — The superadmin's global list

- GIVEN pending requests of 45 and 47
- WHEN a superadmin opens Superadmin → *Request Institutions*
- THEN both units' requests are listed, each with its unit acronym
- AND narrowing to 47 shows only 47's requests
- AND the superadmin can decide a request of either unit

### FN-006 / FN-007 / UI-004 / OPS-002 — Duplicate guard

- GIVEN an active institution "World  Vision International " with headquarters GB
- WHEN a reviewer accepts a request named "world vision international" with country GB
- THEN no institution is created, the request stays pending, and the message names the existing institution and its id
- AND exactly one INFO line names the request id and the existing institution id
- BUT it must NOT refuse a request "Ministry of Agriculture" with country KE when the only "Ministry of Agriculture" rows are in other countries
- AND it must NOT refuse when the matching institution is inactive
- AND IT MUST NOT offer a way to accept anyway

### FN-008 — Mixed office batch

- GIVEN an institution with pending office requests from units 45 and 47, and the admin of 45
- WHEN a batch holding both ids is accepted (crafted request)
- THEN the 45 request is applied and the 47 request is refused with nothing written for it

### FN-009 — Alert recipients

- GIVEN unit 45 with notifications on and one active `CRP-Admin` with an email
- WHEN a user of 45 submits a partner request, and separately a country-office request
- THEN each alert is addressed to that admin, with the support mailbox in copy, and links to `/admin/<unit>/marloInstitutions`
- AND for a unit with no active admin with an email, the alert goes to the support mailbox alone
- BUT with notifications off, no alert is sent

### FN-010 — Decision emails

- GIVEN a request decided by the admin of 45
- WHEN the decision email is built
- THEN the requester is the recipient, the admin is in copy and the support mailbox in blind copy, as today

### FN-011 — Carry-over

- GIVEN a request of 45 stored before the release
- WHEN the release is deployed with no data script
- THEN the admin of 45 sees it in the list

### SEC-004 — Request without a unit

- GIVEN a pending request with an empty unit
- WHEN an admin of any unit posts a decision for it
- THEN it is refused with nothing written
- AND it is listed and decidable on the superadmin list only

### SEC-005 — POST-only

- GIVEN an admin of 45 and a pending request of 45
- WHEN any decision route is called with GET
- THEN nothing is written and the answer is "not allowed"

### UI-001 / UI-002 — Admin menu and screen

- GIVEN the admin of 45, with 45 in MARLO mode
- WHEN the Admin menu renders
- THEN *Request Institutions* is shown and opens the redesigned screen under the admin route
- AND after any decision the admin stays on the admin screen
- BUT in CLARISA mode the entry is hidden
- AND IT MUST NOT show the entry to a user who cannot administer the unit

### RM-001

- GIVEN the change is complete
- WHEN the views are searched
- THEN one review template serves both routes and `crp/views/admin/marloInstitutions.ftl` no longer exists as a separate copy

## 7. Defect classes and their gates

| # | Defect class this spec can produce | Gate that catches it |
|---|---|---|
| D1 | Cross-unit write: an admin decides another unit's request (crafted id, stale tab, mixed batch) | Unit tests on each of the five decision paths with a request of another unit, asserting nothing saved and no mail sent through recording doubles (arguments asserted, KZ auth-flow-2); manager-level test of the unit check; live check on the local stack with two units |
| D2 | Authorization read from the session unit instead of the request's unit | Unit test pair of the SEC-001 "session unit does not decide" scenario: the falsifier is a guard that reads the session |
| D3 | Listing leak: another unit's request on the admin list | Unit test of the listing with two units' rows; live check |
| D4 | Duplicate guard false negative (case, spaces) or false positive (same name, other country; inactive match) | Unit tests with the AC fixture: one row per normalization case, one same-name-other-country row, one inactive row |
| D5 | Two concurrent accepts of two **different** requests for the same institution both create it | **No unit-test harness can exercise it.** Probe on a throwaway DB copy (pattern of `CHG-PARTREQ-001` probes `t04-*`); if the design accepts the residual risk instead, it is recorded here as an accepted risk |
| D6 | Alert sent to the wrong people, or link to the wrong screen | Unit test asserting the recipients and the link the mail double receives, for a unit with admins and one without |
| D7 | Route config: an admin route still passing through `superAdminValidation`, or a decision route an admin cannot reach | Static test pinning the interceptor refs of the changed routes; live check of every decision as an admin |
| D8 | FTL wrong: menu entry, unit column, filter, duplicate message, return to the wrong screen | **No FTL render harness exists.** Human check at the HITL pause on the local stack, admin and superadmin, both routing modes, recorded with screenshots |
| D9 | Data of a refused request leaked in the JSON body | Unit test on the JSON actions' published properties for a refused request |
| D10 | Revive / routing regressions | The existing `CHG-PARTREQ-001` and `CHG-PARTROUTE-001` suites, `marlo-web` with `-am` |
| D11 | Compile error | Clean compile gate (`marlo-verify`) |
| D12 | i18n apostrophe / brace | `marlo-verify` i18n gate |

## 8. Constitutional Compliance Checklist

- [x] Phase replication: not applicable — `partner_requests` and `institutions` are not phase-replicated.
- [x] Save validation: no section save path changes; decisions keep their manager-owned transactions.
- [x] Permissions: every changed route declares its interceptor stack (D7); the decision rule is SEC-001.
- [x] Specificity: none added; the existing `partner_requests_to_clarisa` gates the admin entry (UI-001).
- [x] Migrations: none expected. If the design adds an index or column, it ships as a Flyway migration through `marlo-migration`.
- [x] i18n: new texts (duplicate message, unit column, filter) are i18n keys (NF-003).
- [x] License header: any new Java file carries the GPL header.
- [x] Code style: Checkstyle delta 0 (NF-004).
- [x] REST: no `/api/*` change.
- [x] Audit: not applicable beyond the existing `created_by` on the created institution.
- [x] Dependency floors: no dependency change.
- [x] Branching: `revive-local-partner-request-approval`, reused by owner decision, merging into `staging`.

## 9. Open Questions

| # | Question | Why it matters | Proposed answer |
|---|---|---|---|
| Q-1 | ~~Which role is "admin of the unit"~~ | — | **Closed 2026-11-27:** `CRP-Admin` role or admin edit privilege, in the request's unit (SEC-001). **Narrowed at judgment round 1** with active membership and `crp_admin_active` (SEC-001); the narrowing was approved by the owner at the Phase 2 gate, 2026-11-27 |
| Q-2 | ~~Duplicate rule~~ | — | **Closed 2026-11-27:** block, no override (FN-006, FN-007). The key is amended by Q-6 |
| Q-3 | ~~Alert recipients~~ | — | **Closed 2026-11-27:** unit admins in To, support in CC (FN-009) |
| Q-4 | ~~Superadmin decides?~~ | — | **Closed 2026-11-27:** yes (FN-005) |
| Q-5 | Jira ticket | Traceability | Owner |
| Q-6 | **Duplicate key: name only, or name + headquarters country?** | 15 of the 57 same-name groups in `institutions` (accent-sensitive; 16 of 61 under the column's accent-insensitive collation) are different institutions in different countries (e.g. "Ministry of Agriculture" in LB/LR, FJ, LV, MW). Name only would block a legitimate new ministry | **Closed 2026-11-27:** name + headquarters country, matching any headquarters row of the institution (FN-006) |
| Q-7 | Should the unit admin also see the "similar institutions" list for every unit's catalog when deciding? | The catalog is shared; similar names in other countries help catch near-duplicates FN-006 cannot | Yes: the existing search stays global (no change) |

## 10. Decision Log

- 2026-11-27 — Approval moves to the Global Unit admin; accept creates the institution directly — owner decision ("el admin aprueba directamente").
- 2026-11-27 — **Supersedes `CHG-PARTREQ-001` SEC-001** (superadmin only) **and SEC-003** (CRP-admin screen closed), and **`CHG-PARTROUTE-001` SEC-001** (review guards stay superadmin-only). Extends `CHG-PARTROUTE-001` UI-001 to the admin menu. `CHG-PARTREQ-001` FN-011 (superadmin menu entry) stays. The archives are not edited; this log is the record.
- 2026-11-27 — Q-1 to Q-4 closed as recommended in the proposal (owner: "apruebo, haz lo que recomiendas en Q-1 a Q-4").
- 2026-11-27 — Depth raised from Standard to Full: the change rewrites authorization on five write routes.
- 2026-11-27 — Q-6 closed: the duplicate key is normalized name + headquarters country, any headquarters row of the institution matching (owner: "sí, continúa con la fase 2" after the recommendation). Rationale: 16 of 61 same-name groups are distinct institutions in different countries (corrected at judgment round 1: 15 of 57 under the guard's accent-sensitive normalization). Closure sweep: forward `grep -n "Q-6\|headquarters country" requirements.md` — FN-006, § 9 Q-2 / Q-6 and the answer-first block consistent; backward: the proposal's Q-2 ("exact normalized name") is kept as history, superseded here.
- 2026-11-27 — The request's unit, not the session unit, decides who may act (SEC-001), confirmed by the owner with the same answer: requiring both adds no protection and refuses legitimate multi-unit admins.
- 2026-11-27 — Judgment round 1 (J-1 … J-24, owner: "corrige y re-juzga, incluye los avisos"). SEC-001 narrowed to "administers": active membership and `crp_admin_active` added (J-7, J-10); SEC-003 and UI-001 now use that definition instead of the wider admin-area rule (J-4); § 2 counts restated under the guard's accent-sensitive normalization: 57 / 42 instead of 61 / 45 (J-6); the duplicate key stays accent-sensitive, as FN-006 names case and spaces only (J-24). Closure sweep: forward `grep -n "61\|45 of\|16 of\|existing admin rule\|may administer" requirements.md design.md proposal.md` — requirements § 2, SEC-003, Q-6 row and the Q-6 log line updated; the remaining hits are the 61/16 history kept inside the corrected sentences; design P-15, § 3, DD-6 carry 57 / 42; `proposal.md` carries none of these numbers. Backward: `grep -n "SEC-001\|SEC-003\|UI-001" design.md` — § 5 menu, § 7.1, § 9, DD-1 consistent.
- 2026-11-27 — Judgment round 2 (RA-2 / RB-4, RA-4): § 4 Out-of-Scope "45 existing" → 42, missed by round 1's sweep because its pattern was `45 of`; UI-001 text now names SEC-001's definition; the Q-1 row records the narrowing and its pending approval. Closure sweep, widened: forward `grep -n "45\b\|61\|16 of\|may administer\|existing admin rule" requirements.md design.md` — every remaining hit is either the 61 / 16 history inside corrected sentences, unit id 45 (AICCRA) or unrelated (`:5610`-style line numbers); backward: `grep -n "UI-001\|Q-1" design.md` — § 5 menu and § 9 consistent.
- 2026-11-27 — Phase 2 gate: the owner approved the design and the SEC-001 narrowing ("continúa con la fase 3, apruebo la ampliación de SEC-001").
