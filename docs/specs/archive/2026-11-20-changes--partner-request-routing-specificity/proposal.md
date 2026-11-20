# Partner request routing per Global Unit (CLARISA or MARLO) — Proposal

> **Answer first.** A new specificity decides, per Global Unit, where a partner request goes:
>
> - **CLARISA mode** restores the pre-revive behaviour: the request is sent to CLARISA, nothing is stored in MARLO, and there is no in-MARLO approval.
> - **MARLO mode** keeps today's behaviour: the request is stored locally, the support mailbox is alerted, and a superadmin accepts or rejects it in Superadmin → Tools → "Request Institutions".
>
> The CLARISA sending code still exists (dead since the revive spec), so this is mostly wiring a flag around two existing paths. It reverses three approved decisions of the archived revive spec, so the owner has to confirm the mode per unit before specify.

## 1. Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/partner-request-routing-specificity` |
| Slug | `partner-request-routing-specificity`, derived from the free-text request |
| Type | Change |
| Deliverable | software |
| Status | **Approved** by the owner, 2026-11-20 ("sí, apruebo la propuesta") |
| Approval Mode | gated |
| Owner | Kenji Tanaka (IBD) |
| Requirement source | Owner request in conversation, 2026-11-20: "la especificidad es que los partners request vayan a clarisa (como estaba antes) o a marlo y la posibilidad de aceptarlos". The owner then answered the open questions with "haz lo que recomiendas" and asked that the superadmin module be taken into account (2026-11-20). Jira ticket: none given |
| Visual source | None. The change is behavioural; the UI changes only show or hide existing elements |
| Depends on | `archive/2026-11-20-changes--revive-local-partner-request-approval` (delivered on branch `revive-local-partner-request-approval`, not yet merged) |
| Branch | To be cut from `revive-local-partner-request-approval`, or the same branch, because the MARLO-mode code is not on `staging` yet |
| Parallel-safe | no. It touches the same files as the unmerged revive work |
| Date | 2026-11-20 |

## 2. Intent

Let each Global Unit choose, through a specificity, whether its partner requests are handled by CLARISA (the pre-revive behaviour) or by MARLO (the revived local flow), without a code change or redeploy to switch.

## 3. Problem / Current Behavior

| # | Today (branch `revive-local-partner-request-approval`, HEAD `2bb01f62dd`) | Evidence |
|---|---|---|
| C1 | Every unit stores partner requests locally. The CLARISA POST and the `isAiccra()` branch were removed for all units | `PartnersSaveAction` on HEAD (no `sendPartnerRequest`); archived revive `execution.md` T06 |
| C2 | Before the revive (`staging` `3e4d08bd7e`), AICCRA posted to CLARISA and stored nothing; other units stored locally | `git show 3e4d08bd7e:…/PartnersSaveAction.java`: `:519 if (this.isAiccra())`, `:531 sendPartnerRequest`, `:588 public boolean sendPartnerRequest` |
| C3 | The CLARISA client code still exists, with no caller: `ExternalPostUtils` (two copies) and `APConfig.getClarisaAPIHost/Username/Password` | `marlo-web/…/utils/ExternalPostUtils.java`, `marlo-utils/…/utils/ExternalPostUtils.java`, `marlo-utils/…/utils/APConfig.java`; revive follow-up P-7 |
| C4 | The superadmin "Request Institutions" menu entry (`superadmin/marloInstitutions`) is shown for every unit. Before the revive it was commented out in the AICCRA branch | HEAD `menu-superadmin.ftl`; `git show 3e4d08bd7e:…/menu-superadmin.ftl` (line ~34 commented) |
| C5 | Since commit `03e3f97782`, the "Request it here." country-office link is shown for every unit. Before, AICCRA saw "Email MARLOSupport@cgiar.org" | `projectPartners.ftl` at HEAD vs `66947636a6~2` |
| C6 | The menu already supports per-unit visibility through `specificityValue(...)` / `hasSpecificities(...)` | `menu-superadmin.ftl:10-12` (pattern used on every entry) |
| C7 | The revive spec deliberately rejected a specificity flag ("instance-wide decision") and required no unit-specific Java (NF-001) | archived `requirements.md` §10 Decision Log 2026-11-19; `design.md` DD-1 |

**Before the revive (`staging` `3e4d08bd7e`) — the behaviour CLARISA mode restores:**

| Element | Non-AICCRA units | AICCRA / AICCRA_III | Source |
|---|---|---|---|
| "Request a new partner" link on Partners (and the other openers: innovations, studies, funding sources) | Visible; stored in MARLO | **Visible**; the request was POSTed to CLARISA, nothing stored | `projectPartners.ftl:151-158` (only `[#if editable]`); `PartnersSaveAction.java:519` (`isAiccra()` → `sendPartnerRequest`) |
| "Request it here." country-office link on Partners | Visible; stored in MARLO | **Hidden**; showed "Email MARLOSupport@cgiar.org" | `projectPartners.ftl:528-531` (`[#if !action.isAiccra()]`) |
| Superadmin → Tools → "Request Institutions" | Visible | **Hidden** (line commented since `2aed5e0843`, 2021-10-07); route still reachable | `menu-superadmin.ftl:21` / `:34` |
| CRP admin → "Institutions" | Visible | Hidden | `menu-admin.ftl:14` / `:30`. Removed for every unit by the revive (SEC-003); **not restored** in either mode |

## 4. Proposed Outcome

A boolean specificity (working name **`partner_requests_to_clarisa`**; `true` = CLARISA mode, default `false` = MARLO mode) read with the **session unit** through `BaseAction.hasSpecificities(...)`:

| Behaviour | MARLO mode (`false`, default) | CLARISA mode (`true`) |
|---|---|---|
| Partner request submit (`PartnersSaveAction`) | Stores the original and the copy locally and sends the support alert (today) | POSTs to CLARISA (restored `sendPartnerRequest`); stores nothing; shows "sent" or "could not be sent" (`requestFailed`) |
| Support alert email | Sent | Not sent (CLARISA notifies on its side) |
| Superadmin → Tools → **"Request Institutions"** menu entry (`superadmin/marloInstitutions`) | Visible | **Hidden** (owner decision 2026-11-20: "no debe aparecer cuando la especificidad de clarisa esté encendida ya que las solicitudes no quedarían en el sistema"). Same mechanism as the pre-revive AICCRA menu. Only the menu entry is hidden; the route stays superadmin-only and reachable by URL |
| Review / decide in MARLO | Available (superadmin + POST) | No requests of that unit arrive, so nothing to decide. Routes keep their guards |
| Country-office request link on Partners | "Request it here." (today) | Restores the pre-revive text: "Email MARLOSupport@cgiar.org" |
| Switching a unit's mode | Superadmin → *Parameters* (`marloParameters` → `CrpParametersAction`) edits the unit's `custom_parameters`. No deploy needed; the value is cached in the session (`hasSpecificities` → `getSessionValue`, `BaseAction.java:6221`), so it takes effect at the user's next login or crp refresh | Same |
| Pending requests when a unit moves to CLARISA mode | Remain listed and decidable in "Request Institutions" | Still stored and decidable: from a MARLO-mode session unit, or by direct URL (see R6) |
| Institutions search / filter toolbar | Always visible | Always visible (outside the flag) |

## 5. Scope

- **Specificity:**
  - migration for `parameters` (types 1, 3, 4; `default_value 'false'`; category 2; format 1);
  - optional `custom_parameters` rows for the units that start in CLARISA mode, guarded with `WHERE EXISTS` on `global_units` (AICCRA 45 and AICCRA_III 47 exist only in real databases; `marlo-migration` skill);
  - the constant in **both** `APConstants.java` (`marlo-data` and `marlo-web`), equal to `parameters.key`.
- **`PartnersSaveAction`:** branch on the specificity instead of the old `isAiccra()`; restore `sendPartnerRequest` from `3e4d08bd7e`, reusing the existing `ExternalPostUtils` and `APConfig` getters; keep OPS-001 logging (no email in logs).
- **Superadmin menu:** the "Request Institutions" entry in `menu-superadmin.ftl` is shown only when the session unit is in MARLO mode, through the menu's existing `visible` expression.
- **Partners page:** the country-office link follows the specificity; CLARISA mode restores "Email MARLOSupport@cgiar.org".
- **Superadmin Parameters:** the new parameter appears in Superadmin → *Parameters* (category 2, Specificities) so a superadmin can switch a unit's mode. The specify phase must confirm the listing shows category-2 parameters and saves a per-unit value.
- **Tests:** both modes for the submit path (unit, with a CLARISA client double that asserts its arguments; KZ auth-flow-2); live check of both modes on a throwaway DB copy.
- **Docs:** `docs/specs/domain/admin/agent-context.md` § Partner Requests; the release note.

## 6. Non-Goals

- Changing how CLARISA itself processes requests, or syncing MARLO-created institutions to CLARISA.
- The CLARISA REST writer endpoints (`InstitutionItem`, `CountryOfficeRequestItem`); they keep their current behaviour (revive R-7).
- Country-office **requests** going to CLARISA. Pre-revive, office requests were always local (`RequestCountryOfficeAction`); they stay local in both modes (owner decision, 2026-11-20).
- Blocking the `superadmin/marloInstitutions` route itself in CLARISA mode. Only the menu entry is hidden, as before the revive.
- The search / filter toolbar, the redesigned screens, and the email shell; they are unchanged.

## 7. Affected Users, Systems, And Specs

| Who / what | Effect |
|---|---|
| Requesters in a CLARISA-mode unit | Their request goes to CLARISA again; no MARLO decision email |
| Superadmin | Reviews MARLO-mode units' requests in "Request Institutions". The menu entry is hidden when the session unit is in CLARISA mode. Switches a unit's mode from Superadmin → *Parameters* |
| Support mailbox | Alerts only for MARLO-mode units |
| CLARISA API | Receives requests again from CLARISA-mode units (needs valid `clarisa.api.*` config per environment) |
| Specs | Reverses the archived revive spec's DD-1, NF-001 and its 2026-11-19 "No specificity flag" decision. `/akili-specify` records the reversal in this spec's Decision Log; the archive is never edited |

## 8. Visual Reference

- Source: None
- Notes: No new screen. Only the visibility of an existing menu entry and an existing link changes.

## 9. Requirement Delta Preview

### ADDED Requirements
- A per-Global-Unit specificity selects CLARISA or MARLO handling of partner requests; the default is MARLO.
- In CLARISA mode a partner request is sent to CLARISA, nothing is stored locally, and no support alert is sent. A CLARISA failure shows "could not be sent" and logs one line without an email address.

### MODIFIED Requirements
- **Revive FN-001** "stored in MARLO for every Global Unit and MUST NOT be sent to CLARISA" becomes "stored in MARLO for MARLO-mode units; sent to CLARISA for CLARISA-mode units".
- **Revive FN-011** (the "Request Institutions" entry for every unit) becomes "for MARLO-mode units; hidden when the session unit is in CLARISA mode".
- **Revive NF-001** (no unit-specific Java) becomes "unit-specific behaviour only through this specificity; no `isAiccra()` or hardcoded unit ids".

### REMOVED Requirements
- None. Both behaviours remain available.

## 10. Approach Options

| Option | How | Trade-off |
|---|---|---|
| **A. Specificity (`parameters` + `custom_parameters`)** | The standard MARLO mechanism (AGENTS.md *Specificity Implementation Guide*), read with `hasSpecificities` | Per unit, switchable by data; the established pattern; needs a migration and constants in both `APConstants` |
| B. Config property (`marlo-<env>.properties`) | One switch per environment | Not per unit, so it cannot express "AICCRA to CLARISA, others to MARLO"; rejected |
| C. Restore `isAiccra()` | Revert T06 for AICCRA | Hardcoded unit, the exact thing NF-001 removed; not switchable; rejected |

## 11. Recommended Approach

**Option A.** It is the project's standard switch, it is per unit, and it reuses the dead CLARISA client, so the smallest safe path is mostly wiring:

- one `if` in `PartnersSaveAction`;
- one restored method;
- a migration;
- two constants;
- two template conditions.

## 12. Risks, Dependencies, And Decisions

**Decisions (owner, 2026-11-20 — "haz lo que recomiendas", with the superadmin module considered):**

| # | Decision |
|---|---|
| D1 | Default is MARLO for every unit. **AICCRA and AICCRA_III start in MARLO** (as approved in the revive spec), so the migration seeds only `parameters` and writes no `custom_parameters` rows. A unit is moved to CLARISA from Superadmin → *Parameters* |
| D2 | CLARISA mode replicates the pre-revive request side. The request is POSTed to CLARISA, nothing is stored locally, no support alert is sent, and the office link shows "Email MARLOSupport@cgiar.org" |
| D3 | The superadmin **"Request Institutions"** menu entry is **hidden in CLARISA mode** (owner decision, 2026-11-20: requests are not stored in the system then). Only the menu entry is hidden; the route stays superadmin-only |
| D4 | Office requests stay local in both modes |
| D5 | Flag key `partner_requests_to_clarisa`: `true` means CLARISA, `false` (default) means MARLO |
| D6 | No Jira ticket |

**Risks:**

| # | Item | Notes |
|---|---|---|
| R1 | Institution-id collision returns | A unit in MARLO mode creates local ids while the CLARISA sync job runs for the instance (revive P-16 / OPS-002). Switching units between modes does not remove that risk; the OPS-002 check still applies |
| R2 | CLARISA API credentials | Must be configured per environment for CLARISA-mode units; a missing config gives "could not be sent" |
| R3 | Merge order | Depends on the unmerged revive branch; must ship with or after it |
| R4 | Session-cached flag | A mode switch takes effect at the next login or crp refresh, not instantly. Document it in the Parameters help or the release note |
| R6 | Leftover pending requests | A unit switched to CLARISA mode can still have pending requests stored from MARLO mode. The menu then hides the screen for that session unit, but those requests stay decidable from a MARLO-mode session unit or by URL. Mention this in the release note and the Parameters help |
| R5 | Superadmin *Parameters* listing | Must show and save this category-2 parameter per unit; verify at specify (`CrpParametersAction`) |
| KZ | Active lesson KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-1 | Enumerate every site that branches on partner routing: submit, alert, office link, REST writers, superadmin menu. Do not stop at the named ones |

## 13. Success Criteria

- With the flag `false`, every behaviour of the revive spec is unchanged (its tests stay green).
- With the flag `true` for a unit: a partner request from that unit reaches CLARISA (proven with a client double and one live call against a CLARISA test endpoint, or a recorded stub), no `partner_requests` row is created, no support alert is sent, and the office link shows the MARLOSupport email text.
- A superadmin can switch a unit's mode from Superadmin → *Parameters*.
- In CLARISA mode the "Request Institutions" menu entry is hidden for that session unit.
- In MARLO mode the entry is visible and the screen works as today.
- The migration is safe on a clean database and idempotent, and both `APConstants` copies are identical.
- No `isAiccra()` or hardcoded unit id is introduced in Java.

## 14. Next Step

After the owner approves this proposal:

```text
/akili-specify changes/partner-request-routing-specificity
```
