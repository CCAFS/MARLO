# Parameters (Super Admin → Parameters) — Agent Context

Read this before changing anything in the Super Admin **Parameters** screen (`/superadmin/marloParameters.do`) or in
the `parameters` / `custom_parameters` layer behind it. This is a compact, as-built operational guide; inspect the
target source files after reading it.

**Pairs with `AGENTS.md` → "Specificity Implementation Guide".** That guide is the recipe for *creating* a new
specificity (migrations + `APConstants` + usage). This file describes the *screen* that operates them and the runtime
plumbing that carries a value from the database into `hasSpecificities()`. Do not duplicate the AGENTS.md templates
here — link to them.

## The Two Tables, And Which One The Screen Writes

| Table | What it holds | Who writes it |
|---|---|---|
| `parameters` | The **catalog**: one row per (key, global unit type). Key, description, `format`, `default_value`, `category`. | **Flyway migrations only.** The screen never inserts, updates or deletes a row here. |
| `custom_parameters` | The **value per Global Unit**: `parameter_id` + `global_unit_id` + `value`. | The screen (and migrations). |

So: **the Parameters screen assigns values, it does not define parameters.** Adding a new flag is always a migration
(see AGENTS.md); the screen only lets a super admin turn it on/off per Global Unit afterwards.

`custom_parameters` joins the catalog by `parameter_id`, never by key string — which is why renaming a
`parameters.key` preserves every configured value.

## Primary Files

- Route: `marlo-web/src/main/resources/struts-superadmin.xml` (`marloParameters`, namespace `/superadmin`)
- Action: `marlo-web/src/main/java/org/cgiar/ccafs/marlo/action/superadmin/CrpParametersAction.java`
- View: `marlo-web/src/main/webapp/WEB-INF/global/views/superadmin/marloParameters.ftl`
- JS: `marlo-web/src/main/webapp/global/js/superadmin/marloParameters.js`
- CSS: `marlo-web/src/main/webapp/global/css/superadmin/superadmin.css`
- Menu: `marlo-web/src/main/webapp/WEB-INF/global/views/superadmin/menu-superadmin.ftl` (slug `parameters`)
- Entities: `Parameter`, `CustomParameter`; enums `ParameterCategoryEnum`, `ParameterFormatEnum`
- HBM: `marlo-data/src/main/resources/xmls/Parameters.hbm.xml`, `CustomParameters.hbm.xml`
- DAO / Manager: `ParameterMySQLDAO`, `CustomParameterMySQLDAO`, `ParameterManagerImpl`, `CustomParameterManagerImpl`
- Runtime readers: `BaseAction.hasSpecificities(String)`, `BaseAction.specificityValue(String)`
- Session loaders: `action/home/LoginAction.java`, `interceptor/InternationalitazionFileInterceptor.java`,
  `interceptor/ValidSessionCrpInterceptor.java`, `interceptor/center/ValidSessionInterceptor.java`
- i18n: `global.properties` → `menu.superadmin.parameters`, `marloParameters.title`

## Route And Gates

```
/superadmin/marloParameters.do  ->  CrpParametersAction  ->  superAdminStack
                                ->  /WEB-INF/global/views/superadmin/marloParameters.ftl
```

- `superAdminStack` = `i18nFile`, `requireUser`, `superAdminValidation` (`SuperAdminInterceptor`),
  `keepRedirectMessages`, `accessibleStage`, `trimInputs`, `defaultStack`.
- `save()` re-checks `canAccessSuperAdmin()` (`Permission.FULL_PRIVILEGES`) and returns `NOT_AUTHORIZED` otherwise.
- Menu visibility uses the opt-out idiom
  `(action.specificityValue('parameters')?has_content)?then(action.hasSpecificities('parameters'), true)` — i.e.
  visible unless a parameter literally keyed `parameters` exists and is `false`. **No such row exists today**, so the
  entry is always visible to super admins. The same idiom is used for every super-admin menu entry.

## What The Screen Renders

One collapsible block per Global Unit (`crps`), filtered in `prepare()` to `isMarlo() && isActive()`. Inside each
block, three tabs — the `ParameterCategoryEnum` values sorted by id **descending**, so the first (and active)
tab is Settings, then Specificities, then Roles:

| Category id | Enum | Tab label | Meaning |
|---|---|---|---|
| 1 | `Roles` | Roles | Value is a **role id**; rendered as a `<select>` fed by `crps[i].roles`. |
| 2 | `Specificities` | Specificities | Feature flags. This is the category AGENTS.md's guide creates (`category = '2'`). |
| 3 | `Settings` | Settings | Operational values (phases, years, file names, …). |

The input widget comes from `parameters.format` (`ParameterFormatEnum`):

| `format` | Enum | Widget |
|---|---|---|
| 1 | `Boolean` | Yes / No radio pair, submitting the strings `"true"` / `"false"`. |
| 2 | `Date` | Text input upgraded by `marloParameters.js` to a jQuery datepicker (`yy-mm-dd`, 2012–2031). |
| 3 | `Int` | Text input upgraded to `numericInput()`. |
| 4 (or anything else) | `Text` | Plain text input, placeholder = the catalog `default_value`. |

The `format == 1` check comes **first**, so a Roles-category parameter renders the role `<select>` only when its
format is *not* boolean; a Roles parameter stored with `format = 1` would render Yes/No radios instead.

A Roles parameter stores a **`roles.id` as a string** and is consumed by parsing it back, e.g.
`Long.parseLong((String) getSession().get(APConstants.CRP_PMU_ROLE))` in `BaseAction`. The catalog keys follow the
`crp_<acronym>_rol` / `_role` pattern (`crp_pmu_rol`, `crp_admin_rol`, `crp_pl_rol`, `crp_pc_rol`, `crp_fpl_rol`,
`crp_rpl_rol`, `crp_cl_rol`, `crp_cd_rol`, `crp_cp_role`, `crp_fpm_rol`, `crp_rpm_rol`, `center_coord_role`). Pointing
one of these at the wrong role silently changes who passes the corresponding permission check, and the parse throws
(caught upstream) when the value is empty — so treat the Roles tab as higher risk than the other two.

Values are stored as **strings** in `custom_parameters.value` (`varchar(500)`). `hasSpecificities()` is just
`Boolean.parseBoolean(session value)`, so anything that is not the literal `"true"` reads as false.

### The list is a union of stored rows and synthetic defaults

`prepare()` builds `globalUnit.parameters` (a **transient** field on `GlobalUnit`, not a Hibernate mapping — the mapped
collection is `customParameters`) as:

1. every **active** `custom_parameters` row of that Global Unit; plus
2. one **synthetic, unsaved** `CustomParameter` (no id, `value = parameters.default_value`) for every catalog row that
   matches the unit's `global_unit_type_id` and has no active row yet.

That is why a brand-new parameter shows up on the screen for every Global Unit immediately after its migration runs,
already displaying its default, with nothing in `custom_parameters`.

## Save Pipeline — Read This Before Touching `save()`

There is no `Validator` and no `Action.validate()` here; the screen is super-admin-only free-form editing.

Order of events on POST (`defaultStack` runs `prepare` before `params`):

1. `prepare()` rebuilds `crps`, then — because `isHttpPost()` — **clears** each unit's parameter list, so Struts
   repopulates it purely from the submitted `crps[i].parameters[j].*` inputs.
2. `save()`, per Global Unit:
   - reloads the unit from the DB and **soft-deletes** every active `custom_parameters` row that is not in the
     submitted list (`CustomParameter.equals` compares **ids only**);
   - for each submitted row with `id == null` or `-1` → **inserts** a new `custom_parameters` row (resolving
     `parameter_id` from the submitted `parameter.id`);
   - for each submitted row with an id → loads it and updates **only `value`**.

Two consequences worth knowing:

- **Saving materializes every default.** The form renders *all* Global Units and *all* their parameters (collapsed
  blocks are `display:none`, so their inputs still submit). Every synthetic row has an empty id, so one click on Save
  inserts a real `custom_parameters` row for each parameter that had none — across every Global Unit on the screen.
  This is normal MARLO behaviour, not a bug, but it means the table grows by hundreds of rows on the first save and
  that "no row" (implicit default) becomes "explicit row" permanently.
- **Nothing is deleted in normal use**, because every rendered row submits its id. The delete branch only fires if a
  row disappears from the DOM.

`CustomParameterManagerImpl.saveCustomParameter` and `deleteCustomParameter` are both `@Transactional`, and
`CustomParameters.hbm.xml` maps `active` / `activeSince` / `createdBy` / `modifiedBy`, so the soft delete works here
(unlike some other MARLO tables).

## The `crp` → `System` Relabel Is Display-Only

`marloParameters.ftl` rewrites the key and description **for display only**, and `CrpParametersAction`,
`marloParameters.ftl` and `marloParameters.js` each carry an in-code note about it. The rules:

| Rendered | Rule | Stored (unchanged) |
|---|---|---|
| `system_has_contact_point` | `^crp_` → `system_` (regex, prefix only) | `crp_has_contact_point` |
| `crp_email_pl_systemAdmin_fl` | `crpAdmin` → `systemAdmin` (literal, mid-key special case) | `crp_email_pl_crpAdmin_fl` |
| description text | word-boundary `CRP` → `System`, `CRPs` → `Systems` (case-insensitive) | original wording |

The hidden inputs (`…parameter.key`, `…parameter.description`, `…parameter.category`) submit the **original** values,
so saving never rewrites the catalog.

**Always use the stored key** in SQL, in `hasSpecificities()` / `specificityValue()` calls and in `APConstants`:

```sql
SELECT * FROM parameters WHERE `key` = 'crp_has_contact_point';    -- correct
SELECT * FROM parameters WHERE `key` = 'system_has_contact_point'; -- no rows
```

The search box (`filterParametersBySearch` in `marloParameters.js`) matches the **rendered** text, so on screen you
find that parameter by typing `system_has_contact_point`. Do not copy what you typed into code.

Renaming a key for real is a different job: a Flyway `UPDATE parameters SET \`key\` = …`, both `APConstants.java`
files (the constant value MUST equal `parameters.key`), and every usage — see
`V2_6_0_20260826_1000__RenameUserIdeaSectionActiveParameter.sql` for a worked example.

## How A Value Reaches The Running Application

Parameters are not read from the database at use time. They are flattened into the **HTTP session map**, keyed by
`parameters.key`, and `BaseAction` reads that map:

```java
hasSpecificities(key)  -> Boolean.parseBoolean(session.get(key).toString());  // false on any exception
specificityValue(key)  -> session.get(key).toString();                        // null on any exception
```

A missing key therefore silently reads as `false` / `null` — a typo in a key never fails loudly.

The session map is (re)populated at four moments:

| When | Where |
|---|---|
| Login | `LoginAction` (~line 247) — puts every active custom parameter of the logged Global Unit. |
| Switching Global Unit | `ValidSessionCrpInterceptor` (removes the old unit's keys, then puts the new unit's), `center/ValidSessionInterceptor` for centers. |
| **On demand, without re-login** | `InternationalitazionFileInterceptor` — if the unit's `crp_refresh` parameter is `true`, it reloads **all** parameters into the session, clears the cached phases, and flips `crp_refresh` back to `false`. |
| CRP admin save | `CrpAdminManagmentAction` (~line 1032) re-puts them after its own save. |

**This is the key operational fact for the screen: changing a value does not affect anyone already logged in.** After
editing parameters, either have users re-login, or set that Global Unit's `crp_refresh` to `true` (it is itself a
parameter, `APConstants.CRP_REFRESH`, editable on this same screen) so the next request of each session reloads
everything and resets the flag.

## Known Gaps And Traps

1. **`addParameter` / `removeParameter` / `#parameter-template` in `marloParameters.js` are dead.** The FTL never
   renders a template row and has no `.addParameter` / `.removeParameter` controls, so the whole "create a parameter
   from the UI" path (including the `category = 4` hidden input in the `isTemplate` branch) is unreachable. Creating a
   catalog row is a migration. The `isTemplate` branch also has two typo'd names (`paramater.description`,
   `paramater.key`) — harmless while it stays unreachable.
2. **`${customName}.paramater.key` is typo'd in the live branch too** (line ~117 of the FTL) — it is a second, misnamed
   hidden input next to the correct `${customName}.parameter.key`. It binds to nothing. Leave it or remove it
   deliberately; do not "fix" the spelling, which would start binding an unintended property.
3. **`CrpParameters.hbm.xml` (entity `CrpParameter`, legacy table `crp_parameters`) is not registered in
   `hibernate.cfg.xml`** — dead mapping, superseded by `custom_parameters` in 2017
   (`V2_5_0_20171103_*` migrations). Do not extend it.
4. **`parameters` rows are per `global_unit_type_id`.** The same logical key exists as several rows (types `1`, `3`,
   `4`). `ParameterManager.getParameterByKey(key, globalUnitId)` disambiguates; a bare `WHERE key = …` returns
   several rows. New specificity migrations must insert **one row per type**.
5. Both `Parameter` and `CustomParameter` mappings declare `<cache usage="read-write"/>` — second-level cached.
   Changing rows directly in the database will not be visible until the cache entry is evicted or the app restarts.
6. `custom_parameters.value` is `varchar(500)`. Do not plan to store JSON or long text here.
7. **`prepare()` compares global unit type ids with `==` on boxed `Long`s**
   (`c.getGlobalUnitType().getId() == globalUnit.getGlobalUnitType().getId()`). That is reference equality; it only
   works because the ids in use (`1`, `3`, `4`, `5`) fall inside the JVM's `Long` cache. It will silently start
   dropping synthetic defaults if a `global_unit_types.id` ever exceeds 127. Use `.equals()` if you touch that line.
8. The action's `crps` list excludes non-MARLO and inactive Global Units, so a unit missing from the screen is a
   `global_units.is_marlo` / `is_active` question, not a parameters question.

## Change Recipes

**Create a new specificity / setting** → follow `AGENTS.md` → "Specificity Implementation Guide": one `parameters`
`INSERT` per `global_unit_type_id` (`1`, `3`, `4`), `category = '2'` and `format = '1'` for a boolean flag, plus the
constant in **both** `APConstants.java` files with value identical to `parameters.key`. It then appears on this screen
automatically, showing its default.

**Enable it for one Global Unit** → either this screen (Specificities tab → Yes → Save), or a `custom_parameters`
migration (template in AGENTS.md §2). Then make it visible to live sessions via `crp_refresh` (above).

**Change a value and see it immediately** → edit the value, set `crp_refresh = true` for that Global Unit, save; the
next request per session reloads the map and resets the flag.

**Rename a key** → Flyway `UPDATE parameters`, both `APConstants.java`, every usage (including FTL string literals,
which often hardcode the key instead of the constant). `custom_parameters` needs no change.

**Retire a parameter** → deactivate the `custom_parameters` rows (`is_active = 0`) and remove the code usages. Deleting
the catalog row breaks the screen's synthetic-default rendering for other units of the same type, so prefer leaving it.

## Boundaries — What This Spec Does *Not* Cover

- `bi_parameters` (`BiParameters`) and `tip_parameters` (`TipParameters`) — separate tables for the BI and TIP
  modules, with their own managers and screens.
- The CRP-admin-level screens (`crp/admin/*`), which edit a different set of per-CRP configuration.
- Roles and permissions themselves (only the Roles-category *parameter values*, which are role ids, are in scope).
