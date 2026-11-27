# Partner request approval per Global Unit — Proposal

> **Answer first.** The administrator of each Global Unit reviews, edits, accepts and rejects the partner and
> country-office requests **of their own unit**. An accept creates the institution directly, with no superadmin
> sign-off. The superadmin keeps today's screen as a global view of every unit.
>
> The data is already stored per unit: every request row carries `global_unit_id`. What is global today is the
> list, the decision checks, the guards and the alert email. The change scopes those four by the request's own unit.
>
> Approving directly has one cost: `institutions` is a catalog shared by every unit, so one unit's accept is visible
> everywhere. The proposal adds a duplicate check on accept to protect that catalog.
>
> This reverses `CHG-PARTREQ-001-SEC-001` and `SEC-003` of the archived revive spec, so specify must record it in a
> Decision Log.

## 1. Document Control

| Field | Value |
|---|---|
| Spec Path | `changes/partner-request-approval-per-unit` |
| Slug | `partner-request-approval-per-unit`, the first token of the argument |
| Type | Change |
| Deliverable | software |
| Status | **Approved** by the owner, 2026-11-27 ("apruebo, haz lo que recomiendas en Q-1 a Q-4"). Q-1 to Q-4 resolved as recommended, see § 12 |
| Approval Mode | gated |
| Owner | Kenji Tanaka (IBD) |
| Requirement source | Owner request in conversation, 2026-11-27: "revisar … si la arquitectura está guardando las solicitudes por global unit y la implicación de mover el módulo de los partners request de super admin a admin, ya que en realidad las aprobaciones deben ser por global unit y no globales". Owner decision after the review: "el admin aprueba directamente". Jira ticket: **not given yet** (asked in the report) |
| Visual source | None. The redesigned review screen (`.prq`) is reused as is, see § 8 |
| Depends on | `archive/2026-11-20-changes--revive-local-partner-request-approval` and `archive/2026-11-20-changes--partner-request-routing-specificity`, both merged into `staging` (`a54160ab47`, the current `origin/staging`) |
| Branch | `revive-local-partner-request-approval`, reused by owner decision (2026-11-27, "usemos esa misma rama"), fast-forwarded to `origin/staging` `a54160ab47` before this spec's first commit. The branch carries the two archived partner-request specs this change amends |
| Parallel-safe | yes. No other active spec touches the partner request files |
| Date | 2026-11-27 |

## 2. Intent

A partner request belongs to the Global Unit it was raised in, so the people who run that unit should decide it,
instead of a system-wide superadmin who reviews every unit's requests on a single list.

## 3. Problem / Current Behavior

**Storage is already per unit:**

| Fact | Evidence |
|---|---|
| A partner request (original and copy) is stamped with the session unit and phase | `PartnersSaveAction.java:445-455` (`setCrp(loggedCrp)`, `setPhase(...)`) |
| A country-office request is stamped the same way | `RequestCountryOfficeAction.java:122-123` |
| No row lacks a unit | Local DB `partroute_local`, 2026-11-27: `select count(*) from partner_requests where global_unit_id is null` → `0`. The column is nullable (`information_schema.columns`: `global_unit_id`, `IS_NULLABLE = YES`) |
| Requests come from many units | Same DB: rows for 17 distinct `global_unit_id` values (1, 3, 4, 5, 7, 11, 16, 17, 21, 22, 24–29, 45) |

**Review and decision are global:**

| Fact | Evidence |
|---|---|
| The review list shows every unit's requests | `PartnerRequestAction.prepare()`, `PartnerRequestAction.java:251-255`: `partnerRequestManager.findAll()` filtered only by `isActive`, `isOffice` and `getPartnerRequest() != null` |
| The decision methods never check the unit | `PartnerRequestManagerImpl.acceptPartnerRequest` (`:110-150`) runs `findForUpdate` and `refusePartner`, which check existence, kind and pending state only. The other four decision methods share the same refusal order (`docs/specs/domain/admin/agent-context.md` § *Manager and outcomes*). `UNVERIFIED — confirm at source before relying on it` for the four methods not opened in this proposal |
| Every route is superadmin-only, with a global privilege | `struts-superadmin.xml:136-158` (`superAdminStack`); `struts-json.xml:917-945` and `:1341-1370` (`superAdminValidation`); `BaseAction.canAccessSuperAdmin()` = `hasAllPermissions(Permission.FULL_PRIVILEGES)`, `BaseAction.java:707-709` |
| The four JSON decision routes carry no unit in the URL | `struts-json.xml:917`, `:932`, `:1341`, `:1356`; the JS posts to `baseURL + '/rejectPartnerRequest.do'` etc., `marloInstitutions.js:523, 599, 740, 781` |
| Accept is a full-page POST to the superadmin namespace | `institutionRequestMacro.ftl:211` (`superadmin/addPartner`) |
| The admin route exists but is closed to admins | `struts-admin.xml:146-157`: `{crp}/marloInstitutions` runs `superAdminValidation` before `crpAdminStack`, and `input` redirects to `/superadmin/marloInstitutions` |
| The admin menu entry is commented out | `menu-admin.ftl:29`; the superadmin entry is at `menu-superadmin.ftl:21, 34` |
| The new-request alert goes to the global support mailbox and links to the superadmin screen | `PartnersSaveAction.java:669` (`reviewUrl = getBaseUrl() + "/superadmin/marloInstitutions.do"`); `PartnerRequestEmails` "the two alerts to the support mailbox", `PartnerRequestEmails.java:26` |

**What makes "approve directly" non-trivial:**

| Fact | Evidence |
|---|---|
| An accept inserts into the shared `institutions` catalog, which has no unit column | `PartnerRequestManagerImpl.java:125-132` (`new Institution()` … `institutionDAO.save`) |
| The same institution is requested from several units | `partroute_local`: "international food policy research institute" from 4 units, "international institute of tropical agriculture" from 3, "international maize and wheat improvement center" from 2 |
| Office requests for one institution come from several units | `partroute_local`: institutions 45, 49, 50 have office requests from 3 units each; 7 and 46 from 2 |
| Accept does no duplicate check against `institutions` | `PartnerRequestManagerImpl.java:115-132`: no lookup before the insert |
| Institution ids come from an identity column, while existing ids are CLARISA codes | `Institutions.hbm.xml:8-10` (`generator class="identity"`); `partroute_local`: `max(id) = 11912`. The claim that ids equal CLARISA codes comes from a 2026-09-18 cross-check, see risk R-4 |

**Who is an admin of a unit:**

| Fact | Evidence |
|---|---|
| `isRole()` reads only the roles of the **session** unit | `BaseAction.getRoles()`, `BaseAction.java:5610-5622`: filters `userRole.getRole().getCrp().getId() == getCrpID()` |
| The admin screens accept a superadmin, the `CRP_ADMIN_VISIBLE_PRIVILEGES` permission of the session unit, or the `CRP-Admin` role | `AccessibleAdminInterceptor.intercept`; `BaseAction.canAcessCrpAdmin()`, `BaseAction.java:731-738` |
| `CRP-Admin` is a role per unit | `partroute_local`: role 417 (`CRP-Admin`, unit 45, 1 user), role 479 (`CRP-Admin`, unit 47, 1 user) |

## 4. Proposed Outcome

| Actor | After the change |
|---|---|
| Admin of unit X | Sees **Admin → Request Institutions** and the pending partner and office requests **of unit X only**. Can edit, accept and reject them. An accept creates the institution at once |
| Admin of unit X, acting on a request of unit Y | Refused on the server (crafted id, stale tab after a unit switch). Nothing is written |
| Superadmin | Keeps **Superadmin → Request Institutions** with every unit's requests, now with a unit column and filter. Can still decide any of them, as a fallback for units with no active admin |
| Requester | Unchanged: same request forms, same decision emails |
| Unit admins | Receive the new-request alert for their unit, with a link to the admin screen |
| Unit in CLARISA mode | Unchanged: nothing is stored, and the admin entry is hidden like the superadmin one (`partner_requests_to_clarisa`) |

## 5. Scope

- **Listing.** The admin screen lists the requests of the session unit. The superadmin screen lists all units and shows the unit of each request.
- **Authorization per request.** Each of the five Struts decision entry points (`addPartner`, `addCountryOffices`, `rejectCountryOffices`, `RejectPartnerRequestAction`, `EditPartnerRequestAction`) allows a superadmin, or a user who holds the admin role **in the request's own unit**. The check reads the request's `global_unit_id`, not the session, which can hold another unit after a switch (routing spec P-4).
- **Defense in depth.** The manager's decision methods receive the allowed unit (`null` = any, for a superadmin) and refuse a request of another unit, with the same "nothing written" guarantee as the existing refusals.
- **Routes.** Open `{crp}/marloInstitutions` to `crpAdminStack` without `superAdminValidation`. Give the accept form and the four JSON routes a guard an admin can pass. Re-enable and re-wire the admin menu entry.
- **Duplicate guard on accept.** Before inserting, look for an existing institution with the same normalized name (Q-2, resolved: name only, blocking, no override). When one exists, the accept is refused with a new outcome, and the screen offers to reject the request with "already exists" as the reason. The existing "similar institutions" search (`searchInstitutionsName.do`) stays as the visual aid.
- **Alert email.** The new-partner and new-office alerts go to the `CRP-Admin` users of the request's unit, with the support mailbox in copy (Q-3, resolved). The review link points to `/admin/{crp}/marloInstitutions.do`. A unit with no admin falls back to the support mailbox alone.
- **Retire the dead admin view.** `crp/views/admin/marloInstitutions.ftl` is not rendered (`agent-context.md` § *JSON contract and UI*). The admin route renders the shared `.prq` view instead.
- **Docs.** `docs/specs/domain/admin/agent-context.md` § *Partner Requests* (guards, listing, emails) and `reports/ai-context/role-assignment-email-catalog.md` §17 (recipients).

## 6. Non-Goals

- Making `institutions` a per-unit catalog. It stays shared, and that is why the duplicate guard exists.
- A second approval step by a superadmin. The owner decided against it.
- Changing the CLARISA routing switch. It stays a superadmin parameter (`PARTROUTE` FN-008).
- Fixing the REST writer (`InstitutionItem.acceptPartnerRequest`, R-7 of the revive spec). It already checks the unit (`InstitutionItem.java:119, 123`) but has no lock and no pending guard, and `/api/*` is not routed in this deployment. It is recorded as a risk, not fixed here.
- Who may *submit* a request. Unchanged.
- The CLARISA id collision (R-4). It is recorded, not solved.
- Pending requests are not migrated or reassigned. They already carry their unit.

## 7. Affected Users, Systems, And Specs

| Area | Files (to be confirmed at specify) |
|---|---|
| Review action | `action/superadmin/PartnerRequestAction.java` (listing, `addPartner`, `addCountryOffices`, `rejectCountryOffices`) |
| JSON actions | `action/json/superadmin/RejectPartnerRequestAction.java`, `EditPartnerRequestAction.java` |
| Manager / DAO | `PartnerRequestManager(Impl)`, `PartnerRequestDAO` / `PartnerRequestMySQLDAO` (listing by unit, the unit check, the duplicate lookup through `InstitutionDAO`), `PartnerRequestOutcome.Status` |
| Routing | `struts-admin.xml`, `struts-superadmin.xml`, `struts-json.xml` |
| Views / JS | `menu-admin.ftl`, `menu-superadmin.ftl`, `institutionRequestMacro.ftl` (accept form namespace), `superadmin/marloInstitutions.ftl`, `crp/views/admin/marloInstitutions.ftl`, `marloInstitutions.js` (new outcome text) |
| Emails | `PartnerRequestEmails.java`, `PartnersSaveAction.java` (alert recipients and review link), `RequestCountryOfficeAction.java` |
| i18n | `global.properties` (new outcome text, unit column) |
| Tests | `PartnersSaveActionRequestTest`, the revive suites under `marlo-web/src/test/java/.../partner*` (to be listed at specify) |
| Specs amended | `CHG-PARTREQ-001-SEC-001` (superadmin only) and `SEC-003` (admin screen closed) are superseded. `FN-011` (superadmin menu entry) stays. `PARTROUTE` UI-001 (menu visibility by mode) extends to the admin menu |
| Users | Unit admins (new duties), superadmins (lose the sole-reviewer role), support mailbox (fewer alerts as the main recipient) |

## 8. Visual Reference

- Source: None.
- Location: n/a.
- Notes: the admin route reuses the redesigned `.prq` screen of the revive spec. The only visual additions are a unit column and filter on the superadmin list, and one new outcome message (duplicate). Neither needs a mockup.

## 9. Requirement Delta Preview

### ADDED Requirements

- A unit admin MUST be able to list, edit, accept and reject the pending partner and office requests of their own unit.
- Every decision MUST be refused, with nothing written, when the user is neither a superadmin nor an admin of the **request's** unit. Checked on the server, per request.
- Accepting a partner request MUST be refused when an institution with the same normalized name already exists. The outcome tells the reviewer which institution matched.
- The new-request alerts MUST reach the admins of the request's unit and link to the admin screen.
- The superadmin list MUST show the unit of each request.

### MODIFIED Requirements

- `CHG-PARTREQ-001-SEC-001`: "superadmin only" becomes "superadmin, or admin of the request's unit".
- `CHG-PARTREQ-001-SEC-003`: the admin route is opened to unit admins instead of being closed.
- The admin menu shows "Request Institutions" when the unit is in MARLO mode (extends `PARTROUTE` UI-001).

### REMOVED Requirements

- The superadmin as the only possible reviewer.
- The unused CRP-admin view `crp/views/admin/marloInstitutions.ftl`.

## 10. Approach Options

| Option | How | For | Against |
|---|---|---|---|
| **A. Admin only** | Move the screen and routes to admin, close the superadmin screen | Smallest UI surface; one owner per request | A unit with no active admin cannot get a request decided; superadmins lose oversight of the shared catalog |
| **B. Admin per unit + superadmin global view** *(recommended)* | One action class, two entry points: the admin route scoped to the session unit, the superadmin route unscoped. One authorization rule per request: superadmin, or admin of the request's unit | Keeps a fallback and oversight; reuses the screen, the lock, the outcomes and the POST-only checks; the rule lives in one place and also covers crafted ids | Two menu entries to keep consistent; the manager signature changes |
| C. Admin proposes, superadmin confirms new institutions | Two-step approval | Strongest protection for the catalog | Rejected by the owner ("el admin aprueba directamente") |

## 11. Recommended Approach

**Option B.** It is the smallest safe path. The revive spec already built what is hard (atomic accept, row lock and
re-read, refusal outcomes, POST-only, accessible dialogs). This change adds one dimension, the unit, at three
layers:

1. **Query.** List by unit in the DAO instead of `findAll()` plus stream filters.
2. **Guard.** One helper, `canDecide(request)` = superadmin, or a user holding the admin role in `request.getCrp()`. It reads the user's roles for that unit directly rather than `isRole()`, which reads only the session unit. It is called by all five Struts entry points.
3. **Manager.** The allowed unit is passed down and checked after `findForUpdate`, so a request decided through any future caller is still scoped.

The duplicate guard sits inside `acceptPartnerRequest`, under the same row lock, so two admins of different units
cannot both create the same institution. A different-row race (two requests for the same name from two units,
accepted at the same moment) is covered only if the lookup and insert are serialized. Specify decides between a
unique index on the normalized name and an accepted residual risk (Q-2).

**Kaizen `KZ-changes--migrate-ad-authentication-to-cognito--directory-abstraction-1`** (an enumeration scoped to
what a reviewer already named misses siblings): specify must enumerate every writer of `partner_requests`
decisions repository-wide, not only the five Struts entry points named here. The REST writers in `InstitutionItem`
and `CountryOfficeRequestItem` (`savePartnerRequest` at `InstitutionItem.java:153-240`, `CountryOfficeRequestItem.java:139`)
are known siblings.

## 12. Risks, Dependencies, And Open Questions

| # | Item | Mitigation / owner |
|---|---|---|
| R-1 | **Shared catalog.** An admin's accept is visible to every unit; a careless accept pollutes all of them | Duplicate guard; the superadmin global view; the institution keeps `created_by` = the approver (`PartnerRequestManagerImpl.java:131`) |
| R-2 | **Office locations are global.** An admin of unit X adds a country office to an institution used by unit Y | Accepted as inherent: an office is a fact about the institution, not the unit. Scoping covers who decides, not what is shared |
| R-3 | **IDOR.** Without the per-request check, an admin could decide another unit's request by changing `requestID` | The guard reads the request's unit, plus the manager check (defense in depth). Specify must add a test for a cross-unit id on each of the five entry points |
| R-4 | **CLARISA id space.** Local accepts take identity ids (`max(id) = 11912` locally) while existing ids are CLARISA codes. More approvers means more local ids that a future CLARISA code could collide with | Already true with superadmin approval; record it. `UNVERIFIED — confirm at source before relying on it`: whether any CLARISA code above the local `AUTO_INCREMENT` exists today (public list at `api.clarisa.cgiar.org/api/institutions`) |
| R-5 | **Admins who never asked for this duty.** Locally each AICCRA unit has one `CRP-Admin` user (roles 417, 479). Requests could wait unseen | Alert goes to the unit admins with the support mailbox in copy; superadmin fallback |
| R-6 | **Session lag for the menu** (as in `PARTROUTE`): the admin entry follows a mode switch only at the next login | Inherited, documented; the guard does not depend on the menu |
| R-7 | **REST writer** has no lock or pending guard | Out of scope (§ 6); `/api/*` is not routed in this deployment |
| Q-1 | Which role counts as "admin of the unit": the `CRP-Admin` role acronym, the `CRP_ADMIN_EDIT_PRIVILEGES` permission, or both (as `EditCrpAdminInterceptor` does)? | **Resolved 2026-11-27:** role **or** permission, evaluated against the request's own unit |
| Q-2 | The duplicate rule: exact normalized name only, or name + acronym? Block, or warn and allow an explicit override? A unique index? | **Resolved 2026-11-27:** block on exact normalized name (trim, case, collapsed spaces), no override; the reviewer rejects with "already exists". The unique index stays a specify decision, taken after measuring existing duplicate names in `institutions` |
| Q-3 | Alert recipients: unit admins with support in CC, or unit admins only? | **Resolved 2026-11-27:** unit admins in To, support mailbox in CC |
| Q-4 | Does the superadmin keep **deciding** on all units, or only viewing? | **Resolved 2026-11-27:** deciding (fallback for units without an admin) |
| Q-5 | Jira ticket for this work? | Owner |

## 13. Success Criteria

- An admin of unit 45 sees only unit 45's pending requests and can accept, reject and edit them; an accept creates exactly one institution.
- The same admin gets "not allowed" with nothing written for a request of unit 47, on each of the five entry points.
- A superadmin sees all units' requests, with the unit shown, and can decide any of them.
- Accepting a request whose name already exists in `institutions` creates nothing and names the existing institution.
- The new-request alert reaches unit 45's admins and links to `/admin/{crp}/marloInstitutions.do`.
- With `partner_requests_to_clarisa = true`, the admin entry is hidden and submission behaviour is unchanged.
- Compile, Checkstyle delta and the `marlo-web` suite with `-am` are green; the revive and routing suites still pass.

## 14. Next Step

```text
/akili-specify changes/partner-request-approval-per-unit
```

Standard depth: it changes authorization on a write path and amends an approved spec. Effort `xhigh` for the guard
and the manager tasks (security), per the CLAUDE.md effort dial.
