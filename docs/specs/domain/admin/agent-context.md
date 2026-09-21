# Admin → Management, the Liaison Institution model, and user creation — Agent Context

Read this before touching the CRP admin **Management** screen (`/admin/{crp}/management`), **Regional Mapping**,
**PPA Partners**, **Users** (`/admin/{crp}/crpUsers`), or anything that reads `liaison_institutions` /
`liaison_users` / the `crp_cu` parameter. It is a compact, as-built operational guide; inspect the target source
files after reading it.

**Covers:** the liaison institution model (PMU, components, regions, partners), the `crp_cu` parameter, the
**Management**, **Regional Mapping** and **PPA Partners** screens as far as they touch it, and **every path that
creates a `users` row** — see § *User creation*.

**Does not cover** — no agent context exists yet for these admin actions, so inspect the source directly:
`activityManager`, `siteIntegration`, `crpPhases`, `projectPhases`, `allianceLeversManagement`, `locations`,
`targetUnits`, `marloInstitutions`, `crpDeliverables`, `feedbackManagement`, `homepageBannerManagement`,
`timelineManagement`, `feedbackRolesPermissionsManagement`, `portfolioManagement`.
Add a section to this file when you document one of them.

**Incident record:** `docs/specs/bugfix/pmu-liaison-institution-missing/` (BUG-ADMIN-PMULIAISON-001) — the failure
that produced this document, its requirements, design and verification.

**Pairs with `docs/specs/domain/parameters/agent-context.md`.** That file explains how `parameters` /
`custom_parameters` reach `hasSpecificities()`. This one explains what the admin Management screen does with one
particular parameter (`crp_cu`) and the record it points to. Do not duplicate the parameters guide here.

## The One Table With Three Meanings

`liaison_institutions` looks like a list of institutions. It is not. It is the join point between "a part of a Global
Unit" and the POWB / Annual Report / project modules, and a row plays **one of three roles**, distinguished only by
which foreign keys are NULL:

| Role | `crp_program` | `institution_id` | Example in AICCRA |
|---|---|---|---|
| **The PMU of the unit** | `NULL` | `NULL` | id 336 |
| **A component / flagship / region** | the program id | `NULL` | `KS`, `WA`, `AW`, `PDO` |
| **A partner** | `NULL` | the institution id | `CGIAR ILRI`, `AICCRA Regional Partner - CORAF` |

Both NULLs together is what marks the PMU record. Three code paths depend on that literally:

- `CanEditPowbSynthesisInterceptor` — `if (liaisonInstitution.getInstitution() != null) { throw new
  NullPointerException(); }`, under the comment *"If the LiaisonInstitution is not a PMU or Flagship."*
- the same interceptor's liaison-user lookup — `&& lu.getLiaisonInstitution().getInstitution() == null`
- `ProjectDescriptionAction` — `.filter(c -> c.isActive() && c.getInstitution() == null)` (non-PPA branch)

**Never set `institution` on a PMU record**, even though the Global Unit itself has one
(`global_units.institution_id`). Those answer different questions: the Global Unit's institution is *which
organization this unit is*; the liaison record is *which part of the unit a person acts for*.

`liaison_users` then binds a person to one of those records: `user_id` + `institution_id` (FK to
**`liaison_institutions`**, not to `institutions`) + `global_unit_id`. All three columns are `NOT NULL`.

## Where Liaison Institutions Are Created

Never by hand. Always as a side effect of creating the thing they represent:

| Role | Created in | Name / acronym come from |
|---|---|---|
| PMU | `GlobalUnitCreationManagerImpl.createLiaisonInstitution()` (at Global Unit creation) | the create form, defaulting to `"PMU"` |
| PMU (repair) | `CrpAdminManagmentAction.resolvePmuLiaisonInstitution()` (on save, when missing) | hardcoded `"PMU"` |
| Component | `CrpAdminManagmentAction` (`saveProgramsData`, new flagship branch) | the `CrpProgram` |
| Region | `CrpProgamRegionsAction` (new regional program branch) | the `CrpProgram` |
| Partner | `CrpPpaPartnersAction` (three branches, on first contact point) | the `Institution` |

Program and region rows are **mirrors**: Management and Regional Mapping overwrite their `name` / `acronym` from the
`CrpProgram` on every save, iterating `crpProgram.getLiaisonInstitutions()` — a Hibernate set keyed on the
`crp_program` column. A pre-existing comment in `CrpAdminManagmentAction` says out loud that this duplication is not
understood; treat it as legacy, not as a designed abstraction.

**A PMU row has `crp_program = NULL`, so it is in no program's collection and no resync loop ever touches it.**
Whatever name it is given is permanent until someone changes it by hand.

## Where They Are Visualized

- **Project / cluster description** (`projectDescription.ftl`) — the selects labelled `project.liaisonInstitution`
  and `project.researchProgram`. AICCRA translates them as *Management Liaison* and *Lead Program*
  (`custom/aicrra.properties`, `custom/aiccra3.properties`). With `crp_ppa_enable_project_description = true` the list
  is **every active liaison institution of the unit**, PMU row included; without it, only rows with
  `institution == null`. Rendered through `LiaisonInstitution.getComposedName()` → `acronym - name`, or just `name`
  when the acronym is blank.
- **POWB / Annual Report / Synthesis** — the horizontal tab strip *is* the liaison institution list
  (`submenu-powb.ftl`: `[#list liaisonInstitutions as institution]`), and most sections are organised by it.
  **Currently unreachable:** `main-menu.ftl` disables both Synthesis entries with a hardcoded `false &&`
  (as it also does for `additionalReporting`). Direct URLs still resolve.
- **Impact Pathway** submenu.

**No admin screen lists them.** Management shows *components*, PPA Partners shows *partners*; the liaison record stays
behind. That is why a missing one surfaces only as a foreign-key violation while saving something else, and why a
badly named one surfaces only as a blank option in a dropdown.

## `crp_cu` — The Parameter, And Its Rival

`crp_cu` holds the `liaison_institutions.id` of the unit's PMU record.

```
custom_parameters (per Global Unit, editable in Superadmin -> Parameters)
   -> copied verbatim into the session by LoginAction and ValidSessionCrpInterceptor (no validation)
   -> read from the session ONLY by CrpAdminManagmentAction.prepare(), as the field cuId
```

`BaseAction` has the only other reader, and it is commented out. Inside the action `cuId` is used for exactly two
things: resolving the PMU record when a user is **added** to the team, and finding that person's liaison rows against
that record when they are **removed**.

**Everything else that needs the PMU record ignores `crp_cu` and looks it up by the literal acronym `"PMU"`:**
`ExternalPartnershipsAction`, `IndicatorsAction`, `ExternalPartnersSummaryAction`, and — through
`APConstants.CLARISA_ACRONYM_PMU` — `FinancialSummaryItem` and `ProgressTowardsItem` in the REST v2 API, which
returns *"A Liaison Institution with the acronym PMU could not be found for &lt;unit&gt;"*.

So there are **two independent ways to find the same row, and nothing keeps them in agreement.** Consequences:

- The acronym of a PMU record **must** be exactly `"PMU"`. It is a contract, not cosmetics.
- A `crp_cu` pointing at a row whose acronym is not `"PMU"` splits the two mechanisms: Management writes liaison rows
  against one record while the AR and the REST API look at another. AICCRA is in that state (row 336 has
  `acronym = NULL`).

### How `crp_cu` is meant to be set

Never typed, and never copied from another database — it is a derived id. The order is forced by the foreign keys:

```
1. the Global Unit          (the row needs global_unit_id)
2. the PMU liaison row      (the parameter needs its id)
3. crp_cu = that id
```

`createGlobalUnit()` does exactly this, steps 4 and 5, inside one `@Transactional` method. Any script that seeds a
Global Unit must do the same. Copying `custom_parameters` from another schema without the `liaison_institutions` rows
is what produced the incident recorded in `requirements.md`.

## Resolution Chain On Save (as built)

`CrpAdminManagmentAction.resolvePmuLiaisonInstitution()`, called before any role is persisted:

1. `getLiaisonInstitutionById(cuId)` — returned as is, **whatever it is** (see Known Gaps)
2. `findByAcronymAndCrp("PMU", loggedCrp.getId())`
3. first active liaison institution of the unit with `crpProgram == null`
4. create one (`crp = loggedCrp`, name and acronym `"PMU"`)

Steps 2–4 then repair `crp_cu` (the `CustomParameter`, the `cuId` field and the session key) and log a WARN. If
nothing resolves, the save adds `programManagement.pmuLiaisonInstitution.missing` to `invalidFields` and inserts
nothing.

Order matters: the institution is resolved **before** `saveUserRole` and the notification e-mail, because those two
commit independently and used to leave a person with a role and no liaison row.

## Landmines

- **`findAll()` returns `null` on an empty table** in 433 of 446 DAOs, and `marlo-web` dereferences `findAll()`
  without a guard in ~446 places. Harmless in mature databases, fatal in a freshly created Global Unit.
  `LiaisonUserMySQLDAO` and `CrpProgramLeaderMySQLDAO` were fixed to return the empty list; the rest were not.
  Check the DAO before streaming a `findAll()` in this area.
- **`validate()` does not abort the save.** It only calls `setInvalidFields(...)`; Struts still runs `save()`. A unit
  with no flagship programs always shows a `list-flagshipsPrograms` error and never `saving.saved`, even when the PMU
  user was persisted correctly.
- **Manager writes need `@Transactional`** or the pool rolls them back silently. `saveLiaisonUser` and
  `saveLiaisonInstitution` were missing it and now have it.
- **`liaison_users` deletion in this screen filters by person + institution only**, never by Global Unit
  (`CrpAdminManagmentAction`, the two `liaisonUserManager.findAll()` blocks). When two units share one PMU record,
  removing someone from one unit deletes the other unit's liaison row.
- **`liaisonName` / `liaisonAcronym` are dead inputs.** `GlobalUnitCreateAction` reads and forwards them, but no FTL
  or JS submits them, so every new unit gets a PMU record literally named `"PMU"`.

## User Creation

**Two screens create a `users` row, and they produce different accounts.** Everything else that looks like
creation is not: `CrpAdminManagmentAction:270` and `BaseAction:701` activate an existing account,
`APCustomRealm:336` writes the LDAP username onto one, and `PartnerRequestAction` / `CrpPpaPartnersAction`
build transient `User` objects that are only e-mail recipients.

| | **Admin → Users** | **The global "create user" dialog** |
|---|---|---|
| Route | `{crp}/crpUsers.do` → `CrpUsersAction.save()` | `createUser.do` → `json.global.ManageUsersAction.create()` |
| View / JS | `admin/crpUsers.ftl` · `crp/js/admin/crpUsers.js` | `global/macros/usersPopup.ftl` · `global/js/usersManagement.js` |
| Account state | **active** | **inactive** (`addUser()` saves, then sets `active = false` and saves again) |
| Role + `crp_users` | Guest role + membership | neither |
| Password | generated and e-mailed here, or never | never — activation generates it later |
| Welcome e-mail | yes | none |

The dialog's `create-user` block is **unconditional** in the macro, so every view that imports `usersPopup.ftl`
can create users: `projects/projectPartners`, `admin/management`, `regionalMapping`, `siteIntegration`,
`activityManager`, `ppaPartners`, `impactPathway/clusterActivities`, and the two superadmin views — plus six
legacy `center/` views. **Unmapped in every `struts-*.xml`, therefore dead:**
`center/json/global/ManageUsersAction` and `json/global/SearchUserAction` (mapped once in 2017 with
`method="create"` on a class that only has `execute()`, so it never ran).

### The three branches, duplicated in both actions

`CrpUsersAction:676-726` and `ManageUsersAction:200-252` carry the same cascade, line for line. Change one and
you must change the other.

| | Condition | Names | `username` | `is_cgiar_user` | Password |
|---|---|---|---|---|---|
| 1 | `person.isFound()` | from the directory | from the directory | `1` | none — the e-mail points at corporate credentials |
| 2 | `isCognitoCgiarAddress(...)` | **required from the admin**, provisional | **never written** | `1` | none |
| 3 | otherwise | required from the admin | never written | `0` | `RandomStringUtils.randomNumeric(6)` |

**The directory is consulted before the specificity is read.** `isCognitoCgiarAddress` sits in the `else if`, so
`cognito_auth_active` is evaluated only after the directory has already failed to answer. Branch 2's names are
replaced by `CognitoCallbackAction:672-677` on first sign-in — from the **ID token**, not the directory, which is
why `username` stays null forever: nothing writes it after branch 2.

**Cognito never creates the account.** `CognitoIdentityMapper` gate 1 rejects an assertion with no matching row
and FN-002 forbids auto-provisioning, so a corporate user must be created here *first*.

### A directory outage is indistinguishable from an absent person

`LdapDirectoryService.findByEmail` catches **every** exception and returns `notFound(email, ERROR)`. It also
picks the connection from `!config.isProduction()`, so a non-production checkout targets the **internal**
controllers — `ciatroot4` / `ciatroot5.CGIARAD.ORG:3268`, which do not resolve outside the CIAT network.
**In any local environment branch 1 is unreachable and every corporate address falls to branch 2 or 3.** Plan
tests accordingly; the "directory found them" path cannot be exercised locally.

`findByEmail` is also called **twice per save** — `GuestUsersValidator:44` and `CrpUsersAction:676` — on separate
connections with no cache or pool. They can disagree on the same submission.

### How the screen decides to ask for the names

The browser cannot reach the directory, so the decision is the server's. Three inputs, all server-supplied:

- `crpUsers.ftl` renders `#cognitoAuthActive` (`hasSpecificities('cognito_auth_active')`, read from the session)
  and `#directoryUnconfirmed` (`invalidFields['input-user.firstName']` — set only when the directory failed to
  confirm the address on the last save).
- `crpUsers.js` `namesAreRequired()` reads both, plus `directoryFound` from `directoryByEmail.do`
  (`DirectoryByEmailAction`) — a **read-only** lookup gated on a session, the CRP administrator permission, and
  the corporate domain. A refused, unknown or failed lookup all answer `found: false`, so the names are asked for.
- The fields hide **only** when none of the three says otherwise and the address is corporate.

**Never add a directory lookup to `crpByEmail.do`.** It is in `struts-home.xml`'s `homeJson` package, which
extends `json-default` with **no interceptor stack** — public, and called unauthenticated by the login wizard.
It also carries an explicit FN-001 / R-D3 contract forbidding it from revealing whether an unknown address would
have been CGIAR or local.

### Landmines

- **`validate()` does not abort the save here either.** `GuestUsersValidator` reports through
  `BaseAction.addMessage`, which only appends to `validationMessage` and `missingFields` — no field error, no
  action error — so Struts runs `save()` regardless. The guard that actually stops a half-created account is
  `if (newUser.getId() != null)` inside `save()`.
- **A failed save shows the administrator nothing.** Both `[@s.iterator]` blocks in
  `global/pages/generalMessages.ftl` are commented out, so `global.js:226` finds no `#generalMessages #message`
  and notifies neither success nor error. The page simply comes back.
- **A hidden input still posts.** `slideUp()` does not clear the field, and Struts binds the empty string, which
  is not `null`. Until A2-2449 (`641f69302a`, 2026-09-10) branch 3 only checked `!= null`, so from 2019 every
  corporate address the directory could not confirm was created with **blank names and `is_cgiar_user = 0`**.
  Those accounts are refused by Cognito gate 2 and still reach the local login. The count is unverified.
- **`required=(isCgiarUser)!true` and `required=(isCGIARUser)!true`** in `crpUsers.ftl` are dead conditions: the
  getter is `isCGIARUser()`, so neither spelling resolves and both fall through to the `!true` default.

### Known Gaps

- The superadmin variant of the screen (`crpUsers.ftl:62`) picks the Global Unit from a dropdown, but
  `#cognitoAuthActive` comes from the **session's** unit while the server resolves `isCognitoCgiarAddress`
  against the **selected** one. Worst case is names asked when they were not needed; the server still decides
  correctly.
- Branch 2 accounts can never sign in by username, because nothing writes `users.username` after creation.
  Persisting the directory's login when `directoryByEmail.do` returns one would close it, and needs a uniqueness
  check: `UserMySQLDAO.getEmailByUsername` uses `uniqueResult()` and the column has no unique constraint, so a
  duplicate breaks username sign-in for the account that already had it.

## Known Gaps (deliberate, see requirements.md §8)

- Step 1 of the resolution chain does not check that the row belongs to the logged Global Unit, nor that it is a PMU
  row. A `crp_cu` pointing at another unit's record resolves and is used. As of 2026-09-02, 8 of 19 Global Units with
  the parameter were in that state; only AICCRA_III (47 → AICCRA's row 336) is an active unit. Closing it needs a data
  decision about the liaison rows already written against the shared record.
- `crp_fpl_rol` / `crp_fpm_rol` (`CrpAdminManagmentAction`), `crp_rpl_rol` / `crp_rpm_rol` (`CrpProgamRegionsAction`)
  and `crp_pl_rol` / `crp_pc_rol` (`ProjectPartnerAction`) are resolved with
  `roleManager.getRoleById(Long.parseLong((String) session.get(...)))` and assigned without a null check.
  `user_roles.role_id` is `NOT NULL`, so a dangling role parameter reproduces the same failure on another table.
  `CrpPpaPartnersAction`, `CrpContactPoint` and `FundingSourceListAction` do guard theirs.
- `createGlobalUnit()` and `resolvePmuLiaisonInstitution()` are two independent definitions of "a correctly
  configured PMU". They will drift.
