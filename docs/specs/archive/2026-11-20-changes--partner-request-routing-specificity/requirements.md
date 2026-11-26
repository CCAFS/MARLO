# Partner request routing per Global Unit (CLARISA or MARLO) — Requirements

**Spec ID:** CHG-PARTROUTE-001
**Status:** Approved 2026-11-20 (Phase 1; Q2 and Q4 closed by the owner; amended at judgment rounds 1–2, FN-010 approved at the Phase 2 gate)
**Owner:** Kenji Tanaka (IBD)
**Reviewers:** IBD tech lead
**Last Updated:** 2026-11-20
**Type:** Change
**Deliverable:** software
**AI Agent:** no
**Approval Mode:** gated
**Depth:** Standard
**Source:** `proposal.md` (approved by the owner 2026-11-20)
**Depends on:** `archive/2026-11-20-changes--revive-local-partner-request-approval` (spec `CHG-PARTREQ-001`, delivered on branch `revive-local-partner-request-approval`, not merged into `staging`)
**Related PRD sections:** docs/prd.md — partner and institution management (control lists)
**Related UX/UI Design sections:** docs/ux-ui/design.md — screen inventory `superadmin/marloInstitutions.ftl`, `superadmin/marloParameters.ftl`, Project Partners
**Related TRD sections:** none changed
**Companion ai-context docs:** `reports/ai-context/struts-critical-routing-catalog.md` (routes touched: `{crp}/partnerSave`, `superadmin/marloInstitutions`)

> **Answer first.** A new specificity, `partner_requests_to_clarisa`, decides per Global Unit where a partner
> request goes. **Off (the default):** the request is stored in MARLO and a superadmin decides it, exactly as the
> revive spec delivered. **On:** the request is sent to CLARISA, nothing is stored in MARLO, no support alert is
> sent, the superadmin "Request Institutions" menu entry is hidden, and the Partners page shows the support email
> instead of the country-office request link. Country-office requests stay in MARLO in both modes.

---

## 1. Overview

The revive spec (`CHG-PARTREQ-001`) made MARLO the only destination of partner requests, for every Global Unit, and
removed the AICCRA-only CLARISA path. The owner now wants the choice back, per unit and without a deploy: some units
send requests to CLARISA as before, others keep the in-MARLO approval. This spec adds that switch through the
standard specificity mechanism (`parameters` + `custom_parameters`).

## 2. Problem Statement

Today every unit stores partner requests locally (branch HEAD `0edf846105`, `PartnersSaveAction` has no CLARISA
call). Before the revive (`staging` `3e4d08bd7e`), AICCRA posted its requests to CLARISA and stored nothing
(`git show 3e4d08bd7e:…/PartnersSaveAction.java`, `:519 if (this.isAiccra())`, `:531 sendPartnerRequest`). A unit
that relies on CLARISA as the owner of its institution list has no way back, and the only previous way to express
the difference was a hardcoded `isAiccra()`, which the revive spec removed on purpose (its NF-001).

## 3. In-Scope Requirements

### Functional

- **CHG-PARTROUTE-001-FN-001 — One switch per unit.** The system MUST decide the destination of a partner request
  with a per-Global-Unit specificity whose key is `partner_requests_to_clarisa`. `true` means **CLARISA mode**; any
  other state — `false`, no value for the unit, or a value that is not `true` — means **MARLO mode**.
- **CHG-PARTROUTE-001-FN-002 — MARLO mode is the revive behaviour.** In MARLO mode a partner request MUST be stored
  and alerted exactly as `CHG-PARTREQ-001` FN-001 / FN-002 / FN-010 require (original + editable copy stored; the
  support mailbox alerted after the store, honouring the unit's notification switch).
- **CHG-PARTROUTE-001-FN-003 — CLARISA mode sends.** In CLARISA mode a partner request MUST be sent to CLARISA's
  partner-request service with the requested name, acronym, website, institution type, headquarters country, the
  request source (the same module + item text MARLO mode stores, in the submitting unit's own wording from its
  i18n texts — e.g. AICCRA's "Cluster C12 - …" — and never a unit word written into code), the submitting unit's acronym in lower case as the system identifier, and the requesting user; the page
  MUST then show the "Request sent" confirmation.
- **CHG-PARTROUTE-001-FN-004 — CLARISA mode stores nothing.** In CLARISA mode the system MUST NOT create any
  `partner_requests` row and MUST NOT send the support-mailbox alert, whether the send succeeds or fails.
- **CHG-PARTROUTE-001-FN-005 — CLARISA failure is visible, never silently redirected.** When CLARISA rejects the
  request (non-2xx), cannot be reached, the CLARISA connection is not configured, or the unit has no acronym to
  identify it, the request page MUST be shown
  again with the entered data and an i18n "could not be sent" message. The system MUST NOT fall back to storing the
  request in MARLO.
- **CHG-PARTROUTE-001-FN-006 — The submitting unit decides.** The mode applied to a submission MUST be the mode of
  the Global Unit the request page was opened in (the session unit), read at submit time from that unit's **stored**
  parameters, so a value left in the session by another unit can never decide it.
- **CHG-PARTROUTE-001-FN-007 — Office requests stay local.** Country-office requests MUST keep their current
  behaviour (stored in MARLO, decided by a superadmin) in both modes.
- **CHG-PARTROUTE-001-FN-008 — Switch from the Parameters screen.** A superadmin MUST be able to set
  `partner_requests_to_clarisa` per unit from Superadmin → *Parameters*, listed under *Specificities*, and the saved
  value MUST decide that unit's **next partner-request submission** (FN-006); the menu entry and the
  country-office link (UI-001, UI-002) MAY keep the previous mode until the user's next login or unit switch.
- **CHG-PARTROUTE-001-FN-009 — Pending requests survive a switch.** Requests stored while a unit was in MARLO mode
  MUST stay stored, listed and decidable after the unit moves to CLARISA mode.
- **CHG-PARTROUTE-001-FN-010 — No unit, no guess.** When the request has no Global Unit at submit time (the session
  holds none, or the database re-read returned no unit), the request MUST NOT be stored nor sent; the request page MUST be shown
  again with the entered data and the "could not be saved" message, and one log line MUST record it. When the
  database re-read failed but the session unit exists, the mode is read from that unit's parameters as loaded at
  login. *(Added 2026-11-20, judgment J-8; narrowed at re-judgment RA-1 / RB-1. Owner approval at the Phase 2 gate.)*

### UI

- **CHG-PARTROUTE-001-UI-001 — Review menu entry follows the mode.** The Superadmin → Tools → "Request Institutions"
  entry (`marloInstitutions`) MUST be shown when the session unit is in MARLO mode and hidden when it is in CLARISA
  mode, for AICCRA and non-AICCRA menu variants alike. Only the entry is hidden; the route is unchanged.
- **CHG-PARTROUTE-001-UI-002 — Country-office link follows the mode.** On Project Partners (editable), MARLO mode
  MUST show "Missing a country office? Request it here." opening the office request dialog; CLARISA mode MUST show
  "Missing a country office? Email MARLOSupport@cgiar.org." with a `mailto:` link and MUST NOT render the dialog
  trigger.
- **CHG-PARTROUTE-001-UI-003 — Failure text matches the mode.** The request page failure message MUST read "could
  not be saved" in MARLO mode (today's `marloRequestCreation.saveFailed`) and when the request has no unit (FN-010),
  and "could not be sent" in CLARISA mode.

### Security

- **CHG-PARTROUTE-001-SEC-001 — Review guards unchanged.** Hiding the menu entry MUST NOT weaken the review route:
  listing, editing and deciding stay superadmin-only and POST-only (`CHG-PARTREQ-001` SEC-001 / SEC-002) in both
  modes.
- **CHG-PARTROUTE-001-SEC-002 — Credentials stay out of logs and pages.** The CLARISA username, password,
  authorization header and the requester's email MUST NOT appear in any log line or in the rendered page.

### Operations

- **CHG-PARTROUTE-001-OPS-001 — One line per CLARISA failure.** Every CLARISA failure MUST produce exactly one log
  line (one log event, with no stack trace) **from the partner-request action's logger** through SLF4J, naming the outcome (HTTP status, or the exception
  class) and the unit id; a success MAY produce one INFO line with the status. The ERROR lines the shared
  configuration getters already write for a missing CLARISA value are outside this count. *(Narrowed 2026-11-20,
  judgment J-6.)*

### Migration

- **CHG-PARTROUTE-001-MIG-001 — Parameter seed only.** One Flyway migration MUST add `partner_requests_to_clarisa`
  to `parameters` for each unit type among 1, 2, 3 and 4 **that exists** in `global_unit_types` (format 1, default
  `false`, category 2; type 2 because a migration-only database holds "Center" as id 2 — the precedent of
  `V2_6_0_20261015_1418`), MUST be idempotent (a second
  run adds nothing), MUST succeed on a clean database, and MUST NOT write `custom_parameters` rows (owner decision
  D1: every unit starts in MARLO mode).

### Non-Functional

- **CHG-PARTROUTE-001-NF-001 — Unit-specific only through the switch.** No new `isAiccra()`, unit acronym check, or
  hardcoded `global_unit_id` may decide routing; the key MUST be read through a constant declared in **both**
  `APConstants.java` files with a value equal to `parameters.key`.
- **CHG-PARTROUTE-001-NF-002 — MARLO mode regression-free.** Every behaviour `CHG-PARTREQ-001` delivered MUST hold
  unchanged in MARLO mode; its tests stay green, except tests that pinned the *absence* of a CLARISA path, which are
  rewritten to pin MARLO-mode behaviour instead. One stated exception: a request with **no unit at all** is no longer
  stored with an empty unit (FN-010).
- **CHG-PARTROUTE-001-NF-003 — i18n hygiene.** Every added or changed `.properties` line passes the `marlo-verify`
  apostrophe / brace check.
- **CHG-PARTROUTE-001-NF-004 — Gates.** Clean compile, Checkstyle delta 0 against HEAD on changed Java files, and
  the `marlo-web` suite green with `-am`.

## 4. Out-of-Scope

- How CLARISA processes, approves or notifies on requests; syncing MARLO-created institutions to CLARISA.
- The CLARISA REST writer endpoints (`InstitutionItem`, `CountryOfficeRequestItem`) — unchanged (`CHG-PARTREQ-001` R-7).
- Sending country-office requests to CLARISA (owner decision D4).
- Blocking the `superadmin/marloInstitutions` route in CLARISA mode (only the menu entry hides).
- Restoring the CRP-admin "Institutions" entry (removed by `CHG-PARTREQ-001` SEC-003; stays removed).
- The institutions search / filter toolbar, the redesigned screens, and the email shell.
- Making a mode switch take effect inside an already-open session (see FN-008; R4 of the proposal).

## 5. Personas Affected

| Persona | Effect |
|---|---|
| Requester (any unit user with edit rights) | CLARISA mode: request goes to CLARISA; failure shows "could not be sent". MARLO mode: unchanged |
| Superadmin | Switches the mode in *Parameters*; sees "Request Institutions" only for MARLO-mode session units |
| MARLO support mailbox | Alerted only for MARLO-mode submissions |
| CLARISA team / API | Receives requests again from CLARISA-mode units |

## 6. Acceptance Criteria

### FN-001 / FN-006 — Mode resolution

- GIVEN a unit with no `custom_parameters` row for `partner_requests_to_clarisa`
- WHEN a user of that unit submits a valid partner request
- THEN the request is handled in MARLO mode
- AND IT MUST resolve the mode from the session unit at submit time
- BUT it must NOT treat a value other than `true` (e.g. `TRUE ` with a space, `yes`, empty) as CLARISA mode — any
  non-`true` reading is MARLO mode, mirroring `Boolean.parseBoolean`

### FN-002 / NF-002 — MARLO mode unchanged

- GIVEN a MARLO-mode unit with notifications on
- WHEN a valid partner request is submitted
- THEN one original and one editable copy are stored, then one support alert is sent, and "Request sent" is shown
- AND IT MUST NOT contact CLARISA

### FN-003 / FN-004 — CLARISA mode success

- GIVEN a CLARISA-mode unit and a CLARISA service that answers 2xx
- WHEN a valid partner request is submitted
- THEN CLARISA receives exactly one request carrying the entered name, acronym, website, type id, country ISO-2, the
  request source, the unit acronym in lower case and the requesting user's id
- AND IT MUST carry the unit's own source text: for an AICCRA project request, the text of
  `custom/aicrra.properties` ("Cluster C<id> - <title>"); for a unit without an override, the global text
  ("Project P<id> - <title>")
- BUT the Java code must NOT contain a unit word ("Cluster", "AICCRA") as a literal
- AND "Request sent" is shown
- BUT it must NOT store any `partner_requests` row
- AND it must NOT send the support alert

### FN-005 / UI-003 / OPS-001 — CLARISA mode failure

- GIVEN a CLARISA-mode unit
- WHEN CLARISA answers non-2xx, OR throws on connect, OR the `clarisa.api.*` values are missing / placeholders
- THEN the request page is shown again with the entered data and the "could not be sent" message
- AND exactly one line of the action's logger names the outcome and the unit id
- BUT it must NOT store the request in MARLO as a fallback
- AND it must NOT send the support alert
- AND IT MUST NOT write the requester's email, the CLARISA username, password or authorization header to the log

### FN-010 — Unknown unit

- GIVEN the request has no Global Unit (the session holds none, or the re-read returned no unit: `loggedCrp` is null)
- WHEN a valid partner request is submitted
- THEN the request page is shown again with the entered data and the "could not be saved" message
- AND one log line records it
- BUT it must NOT store the request
- AND it must NOT call CLARISA

### FN-007 — Office requests

- GIVEN a CLARISA-mode unit
- WHEN a country-office request is submitted (from any entry point that still offers it)
- THEN it is stored in MARLO exactly as today

### FN-008 — Parameters screen

- GIVEN a superadmin on Superadmin → *Parameters*
- WHEN they set `partner_requests_to_clarisa` to `true` for unit X and save
- THEN a `custom_parameters` row for unit X holds `true`
- AND the next partner request submitted from X is handled in CLARISA mode, without a new login
- AND a user of X who logs in afterwards also sees the CLARISA-mode menu and link
- BUT it must NOT change the mode of any other unit

### FN-009 — Pending requests after a switch

- GIVEN unit X has pending requests stored in MARLO mode
- WHEN X is switched to CLARISA mode
- THEN those requests stay in `partner_requests`, listed by the review screen and decidable by a superadmin

### UI-001 / SEC-001 — Review menu entry

- GIVEN a superadmin whose session unit is in CLARISA mode
- WHEN the superadmin menu renders (AICCRA or non-AICCRA variant)
- THEN "Request Institutions" is not shown
- AND IT MUST still show it when the session unit is in MARLO mode
- BUT it must NOT change the route's guards: a non-superadmin is still refused and a GET still changes nothing

### UI-002 — Country-office link

- GIVEN an editable Project Partners page in a CLARISA-mode unit
- WHEN a partner block renders
- THEN it shows "Missing a country office? Email MARLOSupport@cgiar.org." with a `mailto:` link
- BUT it must NOT render the `data-target="#requestModal"` trigger
- AND in MARLO mode the "Request it here." trigger is rendered as today

### MIG-001

- GIVEN a clean database, and separately a copy of the AICCRA schema
- WHEN the migration runs, and then runs its statements a second time
- THEN `parameters` holds exactly one `partner_requests_to_clarisa` row per existing unit type among 1, 2, 3, 4
- AND `custom_parameters` gains no rows

### NF-001

- GIVEN the changed Java files
- WHEN they are searched for `isAiccra(`, a unit acronym literal, or a numeric unit id in the routing path
- THEN none is found; both `APConstants.java` declare the same constant with value `partner_requests_to_clarisa`

## 7. Defect classes and their gates

| # | Defect class this spec can produce | Gate that catches it |
|---|---|---|
| D1 | Wrong branch: CLARISA-mode request stored, or MARLO-mode request sent | Unit tests driving `save()` in both modes with recording doubles for the manager, the mail sender and the CLARISA client (arguments asserted, KZ auth-flow-2) |
| D2 | Wrong or incomplete CLARISA payload / URL / auth | Unit test asserting the arguments the CLARISA client double receives; one live call against a CLARISA test endpoint **if one is reachable** (see Q3) — otherwise an accepted risk recorded at validate |
| D3 | Failure not surfaced, or silent fallback to local storage | Unit tests for non-2xx, exception and missing-config paths |
| D4 | Secret or email in logs | Log-appender assertion in the failure tests |
| D5 | Compile error (Java stricter than the runner) | Clean compile gate (`marlo-verify`) |
| D6 | Constant mismatch between `APConstants` copies and `parameters.key` | Parity test reading both files and the migration (pattern of `OtpSpecificityConstantParityTest`) |
| D7 | Unsafe or non-idempotent migration | `marlo-migration` checklist; run twice on a throwaway DB copy and on a clean schema |
| D8 | FTL condition wrong (menu entry / office link shown in the wrong mode) | **No FTL render harness exists.** A static test pins that each condition reads the key; the behaviour itself is a **human check at the HITL pause** on the local stack, both modes, recorded with screenshots |
| D9 | Parameters screen does not list or save the new key per unit | Human check on the local stack (FN-008), same pause as D8 |
| D10 | i18n apostrophe / brace | `marlo-verify` i18n gate |
| D11 | Stale mode after a unit switch inside one session (Q4) | Unit test: session holds `true`, the unit's stored parameters say MARLO → request stored, CLARISA not called. Menu / link staleness is accepted (FN-008) |

## 8. Constitutional Compliance Checklist

- [x] Phase replication: not applicable — `partner_requests` is not phased-replicated; nothing new is persisted in CLARISA mode.
- [x] Save validation: `PartnersSaveAction.validate()` unchanged; `{crp}/partnerSave` keeps `projectsStack`.
- [x] Permissions: no new action; the review routes keep `superAdminStack` and their POST guards.
- [x] Specificity: `parameters` + `custom_parameters`, constants in both `APConstants.java` (NF-001).
- [x] Migrations: one Flyway migration, `V<…>_<YYYYMMDD>_<HHMM>__<Description>.sql` from the real clock.
- [x] i18n: no new user-facing string — the existing, unused `marloRequestCreation.sendFailed` is reused (design P-14); NF-003 applies only if a later change edits a `.properties` line.
- [x] License header: any new Java test file carries the GPL header.
- [x] Code style: Checkstyle delta 0 (NF-004).
- [x] REST: no `/api/*` change.
- [x] Audit: not applicable.
- [x] Dependency floors: no dependency change (the CLARISA call reuses the HttpClient already on the classpath).
- [x] Branching: same branch as the unmerged revive work (`revive-local-partner-request-approval`), merging into `staging` with it.

## 9. Open Questions

| # | Question | Why it matters | Proposed answer |
|---|---|---|---|
| Q1 | *(open, carried to `design.md` as an UNVERIFIED premise)* The pre-revive payload hardcoded `misAcronym: "aiccra"`. What should a non-AICCRA unit in CLARISA mode send? | A wrong value files the request under the wrong system in CLARISA | Send the session unit's acronym in lower case, and record it `UNVERIFIED` until CLARISA confirms the accepted values |
| Q2 | ~~The pre-revive `requestSource` was `"<project acronym> Cluster"`. Keep it?~~ | — | **Closed 2026-11-20, amended at judgment J-4:** send the module + item source MARLO mode already stores, in the unit's own i18n wording (FN-003) |
| Q3 | *(open, carried to `design.md` as an UNVERIFIED premise)* Is there a CLARISA test endpoint and credentials for a live call? | Decides whether D2 has a live gate or is an accepted risk | Owner to confirm; default: accepted risk + client-double test |
| Q4 | ~~Stale `true` in session after a unit switch (`ValidSessionCrpInterceptor.java:101-104` removes only the new unit's keys)~~ | — | **Closed 2026-11-20:** the submit reads the unit's stored parameters (FN-006); menu / link may lag until next login (FN-008) |

## 10. Decision Log

- 2026-11-20 — Add the per-unit specificity `partner_requests_to_clarisa` (Option A of the proposal) — owner request; per unit and switchable without deploy.
- 2026-11-20 — **Reverses `CHG-PARTREQ-001`** decision "No specificity flag" (its Decision Log 2026-11-19), its DD-1, and narrows its FN-001 (local storage now for MARLO-mode units only), FN-011 (menu entry now for MARLO-mode units only) and NF-001 (unit-specific behaviour now allowed only through this specificity). The archive is not edited; this log is the record.
- 2026-11-20 — Default MARLO for every unit; the migration writes no `custom_parameters` rows (owner D1).
- 2026-11-20 — CLARISA mode hides only the "Request Institutions" menu entry; the route stays superadmin-only and reachable (owner D3).
- 2026-11-20 — Office requests stay local in both modes (owner D4).
- 2026-11-20 — Q2 closed: CLARISA mode sends the module + item request source, without "Cluster" — no AICCRA wording in outgoing text.
- 2026-11-20 — Q4 closed: the submission reads the mode from the unit's stored parameters, not the session; the menu entry and office link keep reading the session and may lag until the next login. Closure sweep: forward grep of "next login" / "unit switch" / "1, 3 and 4" / "1, 3, 4" over this folder — every hit updated (FN-008, its AC, MIG-001 and its AC); backward: `proposal.md` §4 / R4 still say "next login" for the whole switch — kept as historical intent, superseded here.
- 2026-11-20 — Judgment round 1 (J-4, owner option a): FN-003 sends the unit's own i18n source text; AICCRA's "Cluster C<id>" is its own vocabulary, not hardcoded wording. Replaces the AC "must NOT contain Cluster" with "no unit word as a Java literal". J-6: OPS-001 counts lines of the action's logger only. J-8: FN-010 added. Closure sweep: forward `grep -n "Cluster\|exactly one log\|without \"Cluster\"" requirements.md design.md` — the earlier Q2 log line above is kept as history and superseded by this one; design §10 updated in the same round. Backward: `grep -rn "FN-003\|OPS-001" design.md` — §10, §11 and the gate map updated.
- 2026-11-20 — Judgment round 2 (re-judgment RA-1/RB-1 … RB-7): FN-010 narrowed to "no unit at all" (a failed DB re-read falls back to the session unit's login-time parameters), listed as NF-002's one exception; UI-003 names the FN-010 text; FN-005 adds "no acronym"; OPS-001 is one single-line event (design no longer logs the throwable); §8 i18n item reworded. Closure sweep: forward `grep -n "unitReadFromDb\|could not be read\|R-6 (SEC\|stack trace deliberate" design.md requirements.md` → 0 hits; backward `grep -n "FN-010\|UI-003\|OPS-001" design.md` → §5, §9, §11, §17, DD-2, P-17 consistent.
