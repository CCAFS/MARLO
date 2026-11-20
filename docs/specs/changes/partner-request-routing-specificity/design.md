# Partner request routing per Global Unit (CLARISA or MARLO) — Design

**Spec ID:** CHG-PARTROUTE-001
**Status:** Approved 2026-11-20 — judgment APPROVED (round 2); FN-010 approved with Phase 3 ("sí, continúa con las tareas")
**Owner:** Kenji Tanaka (IBD)
**Last Updated:** 2026-11-20
**Implements requirements:** FN-001…FN-010, UI-001…UI-003, SEC-001, SEC-002, OPS-001, MIG-001, NF-001…NF-004
**Touches modules:** marlo-web, marlo-data (constant only)
**Verified at:** `0edf846105` (branch `revive-local-partner-request-approval`)

> **Answer first.** One `if` in `PartnersSaveAction.save()`: the action reads `partner_requests_to_clarisa` from the
> submitting unit's **stored** parameters (not the session). True → build the CLARISA payload and POST it through a
> small overridable seam; false → today's store-and-alert path, untouched. The two templates (superadmin menu,
> Project Partners office link) read the same key through `hasSpecificities`. One idempotent migration seeds
> `parameters`; one constant goes into both `APConstants`.

---

## 1. Architecture Summary

```mermaid
flowchart TD
  A[Request page<br/>{crp}/partnerSave POST] --> V[validate&#40;&#41; unchanged]
  V --> S[save&#40;&#41;: build original + copy, request source]
  S --> U{loggedCrp<br/>present?}
  U -- no: FN-010 --> F
  U -- yes --> M{readClarisaMode&#40;&#41;<br/>loggedCrp stored params}
  M -- false / absent --> L[createPartnerRequest → sendNewPartnerAlert<br/>today's path]
  M -- true --> P[buildClarisaPayload]
  P --> C{config present?}
  C -- no --> F[requestFailed, INPUT, 1 log line]
  C -- yes --> H[postToClarisa seam → status]
  H -- 2xx --> OK[messageSent, SUCCESS]
  H -- non-2xx / exception / timeout --> F
```

The menu and the office link are not on this path: they render from the session value, which may lag after a
switch (accepted, FN-008).

## 2. Module Footprint

### marlo-web
- Modified: `action/projects/PartnersSaveAction.java` — private `readClarisaMode()`, field `clarisaMode` with public `isClarisaMode()` (set once at the branch, read by the page),
  `buildClarisaPayload(...)`, `protected postToClarisa(...)` seam, the branch in `save()`.
- Modified: `config/APConstants.java` — `PARTNER_REQUESTS_TO_CLARISA = "partner_requests_to_clarisa"`.
- Modified: `webapp/WEB-INF/global/views/superadmin/menu-superadmin.ftl` — the `institutions` entry's `visible`, both
  branches.
- Modified: `webapp/WEB-INF/crp/views/projects/projectPartners.ftl` — the office-link block (`:541-544`).
- Modified: `webapp/WEB-INF/global/pages/partnersSave.ftl` — failure message key by mode (`:37-39`), using the
  existing, unused `marloRequestCreation.sendFailed` (`global.properties:2684`, P-14). No `.properties` change.
- New: `resources/database/migrations/V2_6_0_<YYYYMMDD>_<HHMM>__AddPartnerRequestsToClarisaSpecificity.sql`
  (timestamp from `date +%Y%m%d_%H%M` at execute time).
- Modified tests: `action/projects/PartnersSaveActionRequestTest.java` (rewrite `testNoReferenceToTheExternalServiceRemains`;
  add the mode to the scenario).
- New tests: `action/projects/PartnersSaveActionClarisaModeTest.java`, with its own `APConfig` test subclass that
  answers the three CLARISA getters (the shared `EmailSendDoubles.EnvelopeConfig` answers none, P-17);
  `action/projects/PartnerRoutingSpecificityParityTest.java` (constants + migration, including "no `'` in the
  description", + template conditions).

### marlo-data
- Modified: `config/APConstants.java` — the same constant.

### Docs
- `docs/specs/domain/admin/agent-context.md` § Partner Requests — the two modes and the switch.

## 3. Data Model Changes

- **Migration** (MIG-001): `INSERT INTO parameters … SELECT gut.id, 'partner_requests_to_clarisa', '<description>', 1,
  'false', 2 FROM global_unit_types gut WHERE gut.id IN (1,2,3,4) AND NOT EXISTS (same type + key)`, the pattern of
  `V2_6_0_20261015_1418` (P-12). DML only, no id assumed, no `custom_parameters` row (owner D1).
- Description (shown on the Parameters screen): "When on, partner requests made in this Global Unit are sent to
  CLARISA and are not stored in MARLO, and the Request Institutions menu entry is hidden. When off, requests are
  stored in MARLO for a superadmin to accept or reject. Country-office requests always stay in MARLO. The menu and the
  Partners page follow a change at the next login or unit switch." The text holds no apostrophe (it sits in a
  single-quoted SQL literal); the parity test asserts it (J-3).
- Unit types 1–4 that exist, not AGENTS.md's literal 1, 3, 4: a migration-only database holds "Center" as type 2
  (P-12). Recorded deviation from the guide's template (J-16).
- No entity, column, index or FK change.

## 4. API / Action Surface

| Route | Action | Change |
|---|---|---|
| `{crp}/partnerSave` | `PartnersSaveAction` | Branch in `save()`; stack `projectsStack`, results unchanged |
| `superadmin/marloInstitutions` (+ its POST actions) | unchanged | Only the menu entry's visibility changes (SEC-001) |
| `superadmin/marloParameters` | `CrpParametersAction` | Unchanged; lists and saves the new key (P-6) |

Outbound: `POST {clarisa.api.host}/api/partner-requests/create`, JSON, Basic auth — the pre-revive contract (P-2, P-10).
No `/api/*` change.

## 5. Frontend Composition

**Design route:** behaviour-only. No new screen, component or token: existing elements are shown or hidden, and one
message key changes. `impeccable-bridge` not loaded — no visual design in scope (proposal §8). Page modes: n/a.

| Surface | MARLO mode | CLARISA mode | Condition |
|---|---|---|---|
| Superadmin menu → `institutions` entry (both `[#if !action.isAiccra()]` branches; only the `[#else]` branch renders today, P-15) | Visible (today's `specificityValue('institutions')` rule) | Hidden | Today's expression `&& !action.hasSpecificities('partner_requests_to_clarisa')`, edited in both branches |
| Project Partners, editable, office help (`projectPartners.ftl:541-544`) | "Missing a country office?" + "Request it here." trigger | "Missing a country office?" + the pre-revive markup restored verbatim from `66947636a6~2:…/projectPartners.ftl:531`: `country.email` text, a `mailto:` link whose href and text are `MARLOSupport@cgiar.org`, then "." | `[#if action.hasSpecificities(...)]` … `[#else]` today's trigger |
| Request page failure note (`partnersSave.ftl:37-39`) | `marloRequestCreation.saveFailed` | `marloRequestCreation.sendFailed` (exists, `:2684`) | `action.clarisaMode` — the field set once at the branch, so the page shows the mode that handled the submit (J-13). FN-010 (no unit) shows `saveFailed` |

The `#requestModal` dialog markup (`:315`) stays rendered in both modes; only its trigger is withheld. Rendering it
unused costs nothing and avoids touching `projectPartners.js` (P-13).

## 6. Persistence & Phase Replication Plan

Not applicable. CLARISA mode persists nothing; MARLO mode keeps `PartnerRequestManager.createPartnerRequest` (one
transaction, revive DD) unchanged. `partner_requests` is not phase-replicated.

## 7. Validation & Save Pipeline

`validate()` is unchanged and runs before the branch, so both modes refuse the same incomplete forms. The
missing-type / missing-country guard at the top of `save()` also stays before the branch. The branch is the last
decision before the write.

## 8. Permissions & Edit Gates

Unchanged. `{crp}/partnerSave` keeps `projectsStack`; the review routes keep `superAdminStack` and their POST guards
(`PartnerRequestReviewGuardTest` stays green, SEC-001).

## 9. Specificity / Feature-Flag Strategy

- Key `partner_requests_to_clarisa`; constant `PARTNER_REQUESTS_TO_CLARISA` in both `APConstants.java`.
- **Java (submit):** in `save()`, `loggedCrp == null` → FN-010 failure, before any store or send. `loggedCrp` is the
  DB re-read when `prepare()` succeeded, null when the session held no unit or the re-read returned none, and the
  session copy when the re-read threw (P-5); in the second case its parameters
  are those loaded at login, the same lag the templates accept. Then `readClarisaMode()` scans `loggedCrp.getCustomParameters()`, skipping null rows, null parameters and null keys,
  keeps active rows (`isActive()`) whose key equals the constant, and returns `Boolean.parseBoolean` of the value;
  no such row → false. The result is stored in the `clarisaMode` field. Not `hasSpecificities`, see DD-2.
- **FTL:** `action.hasSpecificities('partner_requests_to_clarisa')` (AGENTS.md guide). Session-backed, so it can lag
  (P-3, P-4) — accepted by FN-008.
- Rollout: none. Every unit starts in MARLO mode; a superadmin switches a unit on the Parameters screen.

## 10. Integration Points

**CLARISA partner-request service** (CLARISA mode only).

| Payload field | Source | Note |
|---|---|---|
| `name`, `acronym`, `websiteLink` | the editable copy | as pre-revive |
| `institutionTypeCode` | `institutionType.id` | as pre-revive |
| `hqCountryIso` | `locElement.isoAlpha2` | as pre-revive |
| `requestSource` | the module + item text the `add*Message` methods set, in the unit's own i18n wording (AICCRA: `Cluster C<id> - <title>`, `custom/aicrra.properties:2245`; global: `Project P<id> - <title>`, `:2677`); a fixed English fallback `"Request made from MARLO"` when empty | **changed** from `"<acronym> Cluster"` (Q2, J-4 option a) |
| `misAcronym` | `loggedCrp.acronym` lower-cased with `Locale.ROOT`; null acronym → FN-005 failure without calling | **changed** from literal `"aiccra"` (Q1). Unit 45 `AICCRA` → `aiccra` as before; unit 47 `AICCRA_III` → `aiccra_iii`, which CLARISA has never received (P-9 UNVERIFIED, J-9) |
| `userId`, `externalUserName`, `externalUserMail` | the current user | as pre-revive; the email goes to CLARISA in the body, never to a log |
| `externalUserComments` | `"Request made from MARLO"` | as pre-revive |

- `postToClarisa(url, json)` is a `protected` method holding the HttpClient call (Basic auth from
  `config.getClarisaAPIUsername/Password`, `Accept`/`Content-Type: application/json`, try-with-resources closing
  client and response) and returning the status code. It is the only line that touches the network; the test
  subclass overrides it and records `url` + `json`.
- **Timeouts (J-11):** the client is built with a request configuration of 10 s connect, 10 s connection-request and
  30 s socket timeout — design choices, held in named constants the owner may adjust. `RequestConfig` needs
  HttpClient 4.3+, implied by `HttpClientBuilder` already in use (`RestConnectionUtil.java:37`, P-11). A timeout is an exception → the FN-005 failure path. Pre-revive had none.
- **Config check:** each getter is read once into a local; host, username or password blank → failure without
  calling (FN-005). The getters themselves log an ERROR when a value is null (P-16); the action adds exactly one line
  of its own (OPS-001 as narrowed). A placeholder like `XXXXXX` is not detected specially: it produces an invalid URL
  or a 401, both → failure.
- `ExternalPostUtils` is **not** reused: pre-revive used it only as a holder of username / password
  (`3e4d08bd7e:…PartnersSaveAction.java` `sendPartnerRequest`). Reading `config` directly is the same behaviour with
  one less object (DD-4).

## 11. Observability

| Event | Level | Line (no email, no credentials) |
|---|---|---|
| CLARISA 2xx | INFO | `CLARISA accepted the partner request of the Global Unit {} with status {}` |
| CLARISA non-2xx | WARN | `CLARISA did not accept the partner request of the Global Unit {}, status {}` |
| Exception (incl. timeout) | ERROR | `Could not send the partner request of the Global Unit {} to CLARISA: {}` with the exception's simple class name only — the throwable is **not** passed, so no message or stack trace can carry request data (SEC-002) |
| Config missing | ERROR | `The CLARISA connection is not configured; the partner request of the Global Unit {} was not sent` |
| No acronym | ERROR | `The Global Unit {} has no acronym; the partner request was not sent to CLARISA` |
| No unit (FN-010) | ERROR | `The partner request has no Global Unit; nothing was stored or sent` |

Exactly one single-line event of the action's logger per failure (OPS-001). Log arguments are the unit id read
null-safely (memory rule *log-arguments-must-be-null-safe*). Dropping the throwable trades the stack trace for
SEC-002 certainty: the class name and the status are enough to tell a timeout, a refused connection and a rejection
apart.

## 12. Performance & Scalability

One extra in-memory scan of the unit's custom parameters per submission; one HTTP call in CLARISA mode, on the
request thread, as pre-revive. Low volume (a few requests a week). Not applicable beyond that.

## 13. Security Considerations

- SEC-001: no guard changes; hiding a menu entry is presentation only.
- SEC-002: credentials go only into the `Authorization` header of the outbound request; the page renders only i18n
  text; the log lines above carry no user data.
- The requester's email is sent to CLARISA in the body, as before the revive (that is the integration contract).

## 14. Backwards Compatibility & Rollout

- Default MARLO → deploying changes nothing for any unit (NF-002).
- Ships with or after the revive branch (same branch; proposal R3).
- Switching a unit: Parameters screen → takes effect at the next submission; menu / link at the next login, unit
  switch, or crp refresh (`InternationalitazionFileInterceptor` reloads the session parameters on refresh) (J-15).
- **Rollback:** set the unit's value to `false` (instant for submissions). Code rollback reverts the commit; the
  `parameters` rows are inert without the code.

## 15. Decision Records

### DD-1 — Specificity over property or `isAiccra()`
- Decision: per-unit specificity `partner_requests_to_clarisa`. Rationale: proposal Option A; per unit, no deploy.
  Supersedes `CHG-PARTREQ-001` DD-1 ("instance-wide, no flag"). The seed covers unit types 1–4 that exist, a
  recorded deviation from AGENTS.md's 1, 3, 4 (§3, P-12). Status: Accepted.

### DD-2 — The submit reads the unit's stored parameters, not the session
- Decision: the submit decides with `readClarisaMode()`, which reads `loggedCrp.getCustomParameters()`, instead of
  `hasSpecificities`; `isClarisaMode()` only exposes the stored result to the page.
- Rationale: the session can hold another unit's `true` after a unit switch (P-4) and would route a MARLO-mode
  unit's request to CLARISA — sending data to the wrong system is the one defect here that cannot be undone.
  `loggedCrp` is already re-read from the DB in `prepare()` (P-5); `PartnerRequestEmails.isEnabled` reads the same
  collection (it filters no `isActive` and matches case-insensitively; this design keeps active rows and the exact
  key, as the session loaders do, `LoginAction.java:458-460`). With no unit at all, nothing is stored or sent
  (FN-010). When only the re-read failed, the session copy's login-time parameters decide — the lag the templates
  already accept, and better than refusing every MARLO-mode request (re-judgment RA-1 / RB-1).
  Side effect: a switch applies to the next submission without a new login.
- Deviation: AGENTS.md says "typical usage is through `hasSpecificities`" — "typical", not mandatory; recorded here.
- Alternative rejected: fix `ValidSessionCrpInterceptor` to drop the old unit's keys — a shared fix that changes every
  specificity's session behaviour; out of scope.
- Status: Accepted (owner, 2026-11-20).

### DD-3 — Templates keep `hasSpecificities`
- Decision: menu and office link read the session. Rationale: AGENTS.md pattern; a stale value only shows or hides a
  link, never sends data. Status: Accepted.

### DD-4 — Restore the CLARISA call inside `PartnersSaveAction`, behind a seam
- Decision: `buildClarisaPayload` (pure) + `postToClarisa` (protected, network) instead of a new injected client class.
- Rationale: the action's constructor is called positionally by `PartnersSaveActionRequestTest` (P-7); a new
  constructor parameter would change every test construction. A protected seam is enough for argument assertions.
- Alternative: a new `ClarisaPartnerRequestClient` bean — cleaner, more files and a GPL-headed class; not needed for
  one call. Status: Accepted.

### DD-5 — Rewrite, not delete, the "no CLARISA reference" test (reversion)
- Decision: `testNoReferenceToTheExternalServiceRemains` keeps its `RequestCountryOfficeAction` half (office stays
  local, FN-007) and replaces its `PartnersSaveAction` half with a behavioural MARLO-mode test: the CLARISA seam is
  never called and the request is stored (NF-002). `isAiccra()` stays forbidden in `PartnersSaveAction` (NF-001).

### DD-6 — Hide the menu entry in CLARISA mode (conditional reversion of revive FN-011)
- Decision: owner D3.

### Reversion challenges (Step 2.3)

| DD | Reverts | "What does removing this break?" | Answer |
|---|---|---|---|
| DD-5 | A static test that fails if any CLARISA code returns to `PartnersSaveAction` | It caught an accidental CLARISA path for **all** units. After the change that guarantee is needed only for MARLO mode | Covered by the behavioural MARLO-mode test (seam never called, AICCRA unit included). The office half and the `isAiccra()` ban stay static |
| DD-6 | Menu entry visible for every unit | Pending requests of a unit just switched to CLARISA become hard to find from that unit's session | Accepted (FN-009, proposal R6): reachable by URL or from a MARLO-mode unit; noted in the parameter description and the release note |
| Default-false branch | "Stored for every unit" (revive FN-001) | Nothing at deploy: no unit has a row, so every unit stays in MARLO mode | Verified by P-3 + MIG-001 (no `custom_parameters` rows) |

### Budget (tripwire for `/akili-execute`)

| Measure | Estimate |
|---|---|
| Tasks | 5 (re-counted at Phase 3: the pinned-test rewrite had to join the routing task, because adding any CLARISA code turns that test red) |
| LOC | ~470 (Java ~130, tests ~270, SQL ~35, FTL ~12, properties 0, docs ~25) |
| Review rounds | 7 (one per task + two margin for T02) |

## 16. Premise Ledger

Count: 17 rows — 15 verified, 2 `UNVERIFIED` (1 High, 1 Low). Corrected at judgment round 1: P-4 (count), P-9
(AICCRA_III), P-10 (test host), P-14 (was false); added P-15…P-17.
Blast-radius triggers: `live-path` (the design names a user action: submit a partner request), `shared-state` (it
reads a session condition that several templates read), `consumer` (it changes a constant set, a template hook and the
behaviour pinned by tests).

| # | Claim | Class | Citation (as run) | Verified at | If false | Settled by |
|---|---|---|---|---|---|---|
| P-1 | Every "Request a new partner" opener posts to `{crp}/partnerSave`, handled by `PartnersSaveAction` | live-path | `struts-projects.xml:718-719` (`{crp}/partnerSave` → `PartnersSaveAction`, `projectsStack`); openers: `grep -rln "partnerSave" marlo-web/src/main/webapp` → 11 files (projectPartners, fundingSource, powb2019_plannedCollaboration, AR2018_partnerships, capdevDescription, capdevAditional, studiesTemplates, innovationTemplates, partnersSave.ftl, global.css, global.js); none wraps the opener in `isAiccra`/`hasSpecificities` (`grep -n "partnerSave" -B3 … \| grep "isAiccra\|hasSpecificities"` → 0) | `0edf846105` | High — a second submit path would need its own branch (DD-1 scope) | — |
| P-2 | The pre-revive CLARISA call and its contract are recoverable | existence | `git show 3e4d08bd7e:marlo-web/…/PartnersSaveAction.java` → `:519 if (this.isAiccra())`, `:588 public boolean sendPartnerRequest`, URL `/api/partner-requests/create`, fields listed in §10 | `3e4d08bd7e` | High — the payload would need a new contract | — |
| P-3 | The session holds only the unit's `custom_parameters` rows; absent key → `hasSpecificities` false | data-env | `LoginAction.java:460` and `ValidSessionCrpInterceptor.java:114` put `parameter.getKey()`→value per row; `BaseAction.java:6221-6228` returns false on null | `0edf846105` | High — default-off would not hold for the templates | — |
| P-4 | A unit switch removes only the **new** unit's keys, so another unit's `true` can survive | shared-state | `ValidSessionCrpInterceptor.java:97-114` (remove loop iterates `customParameters` of the entered `crp`). Other session writers of these keys: `LoginAction.java:460`, `CrpAdminManagmentAction.java:1212`, `InternationalitazionFileInterceptor.java:145-149`, `center/ValidSessionInterceptor.java:99-110` (`grep -rn "getParameter().getKey()" marlo-web/src/main/java \| grep -i "session\|put"` → 8 hits in 5 files: `LoginAction:460`, `CrpAdminManagmentAction:1212`, `InternationalitazionFileInterceptor:145,149`, `center/ValidSessionInterceptor:99,110`, `ValidSessionCrpInterceptor:103,114`). Readers of the new key: `menu-superadmin.ftl` (both branches), `projectPartners.ftl` office block | `0edf846105` | High — DD-2 would be unnecessary | — |
| P-5 | `loggedCrp` is re-read from the DB in `prepare()` and exposes its custom parameters | location | `PartnersSaveAction.java:351-352`; same reading in `PartnerRequestEmails.isEnabled` `:98-105` | `0edf846105` | High — DD-2 would need its own DB read | — |
| P-6 | The Parameters screen lists every parameter of the unit's type (with its default when the unit has no row), groups category 2 as *Specificities*, and saves a new row per unit | location | `CrpParametersAction.java:89-110` (missing params added with `getDefaultValue()`), `:128-170` (save creates / updates per unit); `marloParameters.ftl:101` groups by category; `ParameterCategoryEnum` `Specificities(2)` | `0edf846105` | Low — T6 adds a manual insert path to the release note | — (live check in T6 confirms rendering) |
| P-7 | Tests that pin the changed behaviour | consumer | `grep -rln "menu-superadmin\|projectPartners.ftl\|partnersSave.ftl\|APConstants.java\|sendPartnerRequest\|ExternalPostUtils\|marloRequestCreation" marlo-web/src/test` → 3: `PartnersSaveActionRequestTest` (`:337-348` pins no CLARISA; `:89-98` positional constructor; `DrivenAction.isAiccra()` → true), `OtpSpecificityConstantParityTest`, `PasswordSwitchConstantParityTest` (read `APConstants` for their own keys only — a new constant does not affect them) | `0edf846105` | High — another pinning suite would break | — |
| P-8 | Consumers of the `institutions` menu entry and the review route | consumer | `grep -rln "marloInstitutions" marlo-web/src/main/webapp marlo-web/src/main/java marlo-web/src/main/resources` → 11 files (admin + superadmin `marloInstitutions.ftl`, `menu-admin.ftl`, `menu-superadmin.ftl`, `allInstitutionsPopup.ftl`, `rejectInstitutionPopup.ftl`, `institutionRequestMacro.ftl`, `partnersSave.css`, `PartnersSaveAction.java`, `struts-superadmin.xml`, `struts-admin.xml`); the menu entry is defined only in `menu-superadmin.ftl`; the other hits are the screens, their macros and styles, struts mappings, `menu-admin.ftl` (entry already removed by revive SEC-003) and the alert's `reviewUrl` in `PartnersSaveAction` (MARLO mode only) | `0edf846105` | Low — another link would also need hiding | — |
| P-9 | CLARISA accepts the lower-case unit acronym as `misAcronym` for every unit other than `AICCRA` (unit 47 `AICCRA_III` → `aiccra_iii` included) | data-env | `UNVERIFIED — confirm at source before relying on it` (pre-revive every unit sent the literal `"aiccra"`, since `isAiccra()` is always true, P-15) | — | Low — `buildClarisaPayload` adjusts one field; unit 45 `AICCRA` unaffected | Owner (Kenji) with the CLARISA team, before any non-AICCRA unit is switched on; noted in T6 and the release note |
| P-10 | CLARISA `POST /api/partner-requests/create` still exists with the 2024 contract and test credentials exist | data-env | `UNVERIFIED — confirm at source before relying on it` | — | High — CLARISA mode would fail on every request (visible failure, no data loss) | T6 first step: a CLARISA test host and an AICCRA-specific account are configured in the owner's local, gitignored `marlo-dev.properties` (`git check-ignore` → `config/.gitignore:1`; values never copied into the spec). T6 uses it for one live call, or the gap is recorded as an accepted risk (requirements Q3) |
| P-11 | Apache HttpClient is on `marlo-web`'s classpath | existence | `grep -rln "org.apache.http.impl.client" marlo-web/src/main/java` → `ClientRepository.java`, `RestConnectionUtil.java` | `0edf846105` | Low — a dependency task would be added | — |
| P-12 | The safe seed pattern for a specificity is `INSERT … SELECT FROM global_unit_types … WHERE NOT EXISTS`, types 1–4 that exist | other | `V2_6_0_20261015_1418__ReplaceOtpAllowListWithSwitchAndExclusion.sql` header + first statement | `0edf846105` | Low — the migration takes the AGENTS.md literal form | — |
| P-13 | The country-office request trigger exists only in `projectPartners.ftl` | location | `grep -rn "country.request\|requestModal\|country\.email" marlo-web/src/main/webapp/WEB-INF` → `projectPartners.ftl:315-334, 543` and an unrelated legacy `center/views/monitoring/ADRESED_IN_CRP_project/projectDescription.ftl:276,300` | `0edf846105` | Low — another template gets the same condition | — |
| P-14 | `projectPartners.redesign.country.email` and `marloRequestCreation.sendFailed` both exist; `sendFailed` has no reader | existence | `global.properties:7452` (`country.email=Email`); `grep -n "sendFailed" global.properties` → `2684:marloRequestCreation.sendFailed=The request could not be sent…`; `grep -rn sendFailed marlo-web/src` → that line only | `0edf846105` | Low — a key is added instead of reused | — |
| P-15 | `GlobalUnit.isAiccra()` returns `true` for every unit, so the non-AICCRA branch of `menu-superadmin.ftl` never renders and only the `[#else]` branch can be checked live | other | `GlobalUnit.java:333-336` ("Temporary compatibility condition … return true") | `0edf846105` | Low — the HITL check covers one branch; the other is edited for parity and pinned by the static test | — |
| P-16 | `APConfig.getClarisaAPIHost/Username/Password` are public, overridable, and each logs an ERROR when its value is null | location | `marlo-utils/src/main/java/org/cgiar/ccafs/marlo/utils/APConfig.java:321-343` | `0edf846105` | Low — the test config and OPS-001 count adjust | — |
| P-17 | The shared test double `EmailSendDoubles.EnvelopeConfig` answers only `getBaseUrl`, `getEmail_pmu`, `getEmailNotification`; the existing suite sets `loggedCrp` by reflection and passes `crpManager` as null | consumer | `EmailSendDoubles.java:90`; `PartnersSaveActionRequestTest.java:89-98` (positional constructor), `:226-228` (reflection) | `0edf846105` | Low — the new test brings its own config subclass and sets `loggedCrp` the same way; the existing suite needs no change for FN-010 because it always sets a unit | — |

## 17. Gate map (requirements §7 → test)

| Defect class | Test / check |
|---|---|
| D1 wrong branch | `PartnersSaveActionClarisaModeTest`: CLARISA mode → seam called once, manager never; `PartnersSaveActionRequestTest` (MARLO mode, AICCRA unit) → manager once, seam never |
| D2 payload | Same test asserts URL and every JSON field received by the seam, with the default texts **and** with `custom/aicrra.properties` (source = `Cluster C…`); plus one live call in T6 if P-10 settles, else recorded as an accepted risk |
| D3 failure | Non-2xx, exception, timeout-as-exception, blank config, null acronym, and FN-010 (no unit) → `requestFailed`, manager never, mail never |
| D4 secrets | Log appender on the action's logger: no requester email, username, password or `Authorization` in any failure event |
| D5 compile | `marlo-verify` clean compile |
| D6 constants | `PartnerRoutingSpecificityParityTest` |
| D7 migration | `marlo-migration` checklist; run twice on a throwaway copy and a clean schema |
| D8 / D9 FTL, Parameters | Static pins in the parity test + HITL check on the local stack (`[#else]` menu branch only, P-15) |
| D10 i18n | No `.properties` change (P-14); gate n/a |
| D11 stale session | Session holds `true`, stored parameters say MARLO → stored, seam never; also `"TRUE "` / `yes` → MARLO |

## 18. Open Risks

| # | Risk | Mitigation |
|---|---|---|
| R-1 | Institution-id collision while the CLARISA sync job runs (revive P-16 / OPS-002) | Unchanged by this spec; OPS-002 check still applies |
| R-2 | CLARISA credentials missing per environment | Visible "could not be sent" + one ERROR line; release note lists the `clarisa.api.*` keys |
| R-3 | *(closed at judgment J-11: timeouts set, §10)* | — |
| R-4 | Menu / office link lag one login after a switch | Stated in the parameter description and the release note (FN-008) |
| R-5 | P-9, P-10 open; `AICCRA_III` would send `aiccra_iii` | Owned as above; release note says so |
| R-6 | *(closed at re-judgment RB-3: the throwable is no longer logged, §11)* | — |
