# Admin module — Agent Context

**Read this first** for routine work on the admin module, roles or permissions. Open `requirements.md`,
`design.md` and `task.md` only for broad, architectural, risky or formally tracked changes.
The full role/permission enumeration lives in **`roles-permissions-catalog.md`**.

---

## Non-negotiables

1. **Never hardcode a role id.** Role ids differ per global unit. Read them from `custom_parameters` via the
   `crp_*_rol` keys (`crp_admin_rol`, `crp_pmu_rol`, `crp_fpl_rol`, `crp_fpm_rol`, `crp_rpl_rol`, `crp_rpm_rol`,
   `crp_pl_rol`, `crp_pc_rol`, `crp_cp_role`, `crp_cl_rol`, `crp_sl_rol`, `crp_cd_rol`). Constants must exist in
   **both** `APConstants.java` files, with the constant value equal to the `parameters.key`.
2. **Never hardcode a global unit id in a migration.** Migrations seed `global_units` only up to 28. AICCRA is 45
   and AICCRA III is 47, and both exist only in real databases. Guard inserts with a join against `global_units`
   / `roles` so a fresh database yields a no-op instead of an FK failure — see
   `V2_6_0_20250616_1500__InsertFeedbackRolesPermissions.sql` for the pattern.
3. **A permission grant is phase-gated.** `getPermissions` only emits rows for phases with `editable = 1`. If a
   grant "does not work", check the phase before checking the grant.
4. **A grant on a node unlocks everything under it.** `BaseAction.hasPermission(field)` returns true for
   `base:field` **or** `base` alone (`BaseAction.java:6348`). Do not add a section-level permission expecting
   field-level granularity.

## Where things live

| Concern | Path |
|---|---|
| Permission string templates | `marlo-data/.../security/Permission.java` |
| Role entity, AICCRA label mapping | `marlo-data/.../data/model/Role.java` (`getAiccraAcronymDimanic()`) |
| Shiro realm | `marlo-data/.../security/APCustomRealm.java` |
| Permission lookup / stored procedure call | `marlo-data/.../data/dao/mysql/UserMySQLDAO.java:68` |
| Runtime expansion procedure (~3 300 lines) | `marlo-web/src/main/resources/database/migrations/V2_6_0_20240827_1048__SPPermissions19.sql` |
| Code-level access gates | `marlo-web/.../action/BaseAction.java` |
| Admin screens | `marlo-web/src/main/webapp/WEB-INF/crp/views/admin/` |
| Admin submenu inventory | `.../views/admin/menu-admin.ftl` |
| User ↔ role management screen | `.../views/admin/crpUsers.ftl` + `action/crp/admin/CrpUsersAction.java` |

## Admin module screen map

`management` (programs/themes, grants FPL/FPM) · `regionalMapping` (regions, grants RPL/RPM) ·
`siteIntegration` (grants SL) · `ppaPartners` (grants CP) · `activityManager` · `locations` ·
`crpUsers` (grants every global-unit role) · `targetUnits` · `marloInstitutions` · `crpPhases` ·
`portfolioManagement` (flag `portfolio_feature_active`) · `timelineManagement` ·
`homepageBannerManagement` · `feedbackManagement` · `feedbackRolesPermissionsManagement`.

Entry requires `crp:{0}:admin:canAcess` — held by `CRP-Admin` and `DM` only, plus `SuperAdmin` via `*`.
**`PMU` cannot reach the admin module.**

## Roles at a glance (AICCRA)

`SuperAdmin` (`*`) · `CRP-Admin` (admin module owner, also holds `crp:*`) · `PMU` = PMC (all projects, no admin) ·
`FPL` = Theme Leader · `FPM` = Theme Manager · `RPL` / `RPM` (region) · `PL` = Cluster Leader (one project) ·
`PC` = Cluster Coordinator (one project, cannot submit) · `CP` = Contact Point · `ML` · `CL` · `SL` (no grants) ·
`FM` · `DM` · `E` · `G` · `CD` · `AR` / `ARW` (REST service accounts).

Unused in AICCRA today: `FM`, `DM`, `CL`, `ML`, `E`, `AR`, `ARW`, `CD`.

## Traps found in the current configuration

- `SL` has users but **zero** rows in `role_permissions`.
- `user_roles` and `role_permissions` have **no unique index**; duplicates exist and inflate every count.
- `PL` and `CL` both render as "Cluster Leader"; `CrpUsersAction.getUserRoles()` de-duplicates by label.
- `BaseAction.isAiccra()` is `id >= 45`, so global unit **46 (`Alliance`)** is treated as AICCRA.
- Four grant families reach the runtime with `{1}` unresolved and can never match:
  `fundingSource:{1}:canEdit`, `fundingSource:{1}:budget`, `crpIndicators:{1}:*`, `synthesisProgram:{1}:*`.
- Feedback permissions exist for global unit 45 only; global unit 47 has none.
- `permissions` contains `crp:{0}:fundingSource:*` twice (ids 450, 451).
- `PL` can submit a project but **cannot** unsubmit it; `CL` is the mirror (unsubmit, no submit). `CL` is not a
  subset of `PL` despite looking like a duplicate.
- **Never audit access with a literal permission query.** Wildcard grants match without appearing in
  `role_permissions`: `crp:{0}:project:*` (PMU) implies submit, unsubmit and delete on every project, because
  Shiro treats a shorter granted permission as implying the remaining parts. Expand wildcards first.

Details and evidence: `roles-permissions-catalog.md` §11.

## Fast checks

```sql
-- What can this user actually do right now?
CALL getPermissions(<user_id>);
SELECT ro_acronym, project_id, permission FROM user_permission ORDER BY ro_acronym, permission;

-- Which phase is open? (a closed phase makes every edit grant inert)
SELECT id, description, year, editable FROM phases WHERE global_unit_id = 47 ORDER BY year, id;

-- Role ids for a global unit
SELECT id, acronym, description, `order` FROM roles WHERE global_unit_id = 47 ORDER BY `order`;
```

## Validated invariants

Checked against the database on 2026-09-01, 43/43 assertions passing. Re-runnable SQL lives in
`roles-permissions-catalog.md` §13.5 — run it before trusting any statement here in a new environment.

- Grant sets per role are identical on global units 45 and 47.
- Only `CRP-Admin` and `DM` hold `admin:canAcess`; `PMU` holds no admin grant at all.
- Configured grant counts are an **upper bound**: `RPL` carries 102 grants but resolved to 38 effective rows for
  a user with no projects in the region. Effective access depends on `crp_program_leaders`,
  `project_partner_persons` and `liaison_institutions` attachments.

## Related docs

- `roles-permissions-catalog.md` — full enumeration, matrices, findings, verification queries.
- `docs/detailed-design/detailed-design.md` §8 — authorization model. **Note:** §8.2 lists a generic seven-role
  model and names the join table `user_role`; the real table is `user_roles` and AICCRA has 20 roles. The catalog
  supersedes it in practice; correcting §8.2 is a constitutional change.
- `reports/ai-context/interceptor-validator-playbook.md` — the Struts interceptor layer that sits in front of
  these permission checks.
