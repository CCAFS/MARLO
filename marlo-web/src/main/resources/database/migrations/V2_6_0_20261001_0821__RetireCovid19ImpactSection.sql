-- changes/retire-covid19-impact-section (shipped under A2-2578): retire the COVID-19 project impact
-- section's configuration -- the three specificities, the two permissions and their role grants, and
-- the section-status rows recorded for it -- while keeping project_impacts and project_impacts_categories
-- and every row they hold (DA-004). The screen, its actions, validators and marlo-data mappings are
-- removed by this spec's other tasks; this migration only retires the data-driven configuration those
-- tasks stop reading.
--
-- Delete order, children before parents (design.md section 4, P-8):
--   1. role_permissions, then center_role_permissions -- permission_id -> permissions
--   2. permissions -- the two permission strings themselves
--   3. custom_parameters -- parameter_id -> parameters
--   4. parameters -- the three specificity keys
--   5. section_statuses -- the 'impacts' section_name, an independent chain of its own
--
-- Every statement matches by key, permission string or section name, with exact equality (`=` / `IN`),
-- never LIKE and never a numeric id, global_unit_id or user id (design.md DD-3; requirements.md MIG-001).
-- A LIKE match on 'crp_show_section_impact_covid19%' would also catch
-- 'crp_show_section_impact_covid19_ranges_years' by accident; exact equality lists both keys instead,
-- and leaves a decoy such as 'crp_covid_required_other' or 'crp:{0}:project:{1}:impactsOther' untouched.
-- No `category` filter is added to the parameters/custom_parameters deletes: DA-001 requires every row
-- with these three keys to no longer exist, whatever category it is stored under, and a category filter
-- could only under-delete -- it would silently let a re-categorised key, and its custom_parameters,
-- survive. All three keys are measured as category 2 on every database checked, so the filter would be
-- redundant where visible and unsafe where not; omitting it costs nothing and closes that gap.
--
-- On the real schema the grant foreign keys (role_permissions_ibfk_1, center_role_permissions_ibfk_1,
-- both -> permissions(id)) are ON DELETE CASCADE, while custom_parameters_ibfk_1 (-> parameters(id)) is
-- ON DELETE RESTRICT (no ON DELETE clause). The explicit child-first order below does not rely on either
-- rule: it is what keeps this migration safe on a database where the CASCADE is missing or was never added.
--
-- Idempotent: every DELETE is already filtered by the same exact match, so a second run, or a database
-- that never seeded these rows, finds nothing and changes 0 rows -- which MySQL reports as success.
--
-- project_impacts and project_impacts_categories are never touched here (DA-004); their rows stay
-- referenced by section_statuses.project_impact_id (FK section_statuses_impacts), left in place, not
-- dropped (design.md DD-2).
--
-- Environment assumptions, since none of them is visible in the statements:
--
-- a. No hardcoded id of any kind -- no permission id, no parameter id, no global_unit_id, no
--    global_unit_type_id, no role id, no user id. A database where these rows were never seeded, or
--    already had this migration applied, simply changes 0 rows on every statement below.
--
-- b. Measured on aiccradb2 (2026-10-01, read-only SELECTs, no write): 3 keys x 3 global_unit_types = 9
--    `parameters` rows, 53 `custom_parameters` rows across those keys (25 + 25 + 3), 2 `permissions` rows
--    (stored with literal `{0}`/`{1}` placeholders -- never substituted), 276 `role_permissions` grants
--    and 0 `center_role_permissions` grants (empty on all five local copies -- the
--    center_role_permissions statement is included anyway because design.md section 4 and P-8 name it,
--    and nothing guarantees it stays empty in an environment this migration also runs against), and 0
--    `section_statuses` rows named 'impacts' on any local copy (kept per DD-6, in case another
--    environment holds some). `project_impacts` = 9 rows, `project_impacts_categories` = 5 rows on all
--    five local copies -- aiccradb1-aiccradb4 plus aiccradb_actsave, re-measured 2026-10-01 -- and
--    neither is touched by any statement here. No row of section_statuses has a non-null
--    project_impact_id on any of the five, so unmapping that column loses nothing.
--
-- c. Destructive, no automatic rollback: the deleted rows are not archived by this migration. Back them
--    up before running in production (backup queries):
--
--      SELECT p.* FROM permissions p
--      WHERE p.permission IN ('crp:{0}:project:{1}:impacts', 'crp:{0}:project:{1}:impacts:canEdit');
--
--      SELECT rp.* FROM role_permissions rp JOIN permissions p ON p.id = rp.permission_id
--      WHERE p.permission IN ('crp:{0}:project:{1}:impacts', 'crp:{0}:project:{1}:impacts:canEdit');
--
--      SELECT crp.* FROM center_role_permissions crp JOIN permissions p ON p.id = crp.permission_id
--      WHERE p.permission IN ('crp:{0}:project:{1}:impacts', 'crp:{0}:project:{1}:impacts:canEdit');
--
--      SELECT * FROM parameters
--      WHERE `key` IN ('crp_show_section_impact_covid19',
--        'crp_show_section_impact_covid19_ranges_years', 'crp_covid_required');
--
--      SELECT cp.* FROM custom_parameters cp JOIN parameters p ON p.id = cp.parameter_id
--      WHERE p.`key` IN ('crp_show_section_impact_covid19',
--        'crp_show_section_impact_covid19_ranges_years', 'crp_covid_required');
--
--      SELECT * FROM section_statuses WHERE section_name = 'impacts';
--
--    Rollback refines tasks.md section 7: primary is re-inserting from the backup queries above, which
--    are exact copies of what this migration removed. Fall back to the tasks.md section 7 route (the
--    2020 seed inserts -- V2_6_0_20200504_0849, V2_6_0_20200509_1015, V2_6_0_20200518_1348,
--    V2_6_0_20201009_0800 -- plus the descriptions from V2_6_0_20260925_1543, in a new migration) only
--    when no backup was taken. That fallback is less faithful: the 2020 seeds copy their
--    `custom_parameters` values from `parameter_id = 200` and their role grants from the
--    `contributionsLP6` / `contributionsLP6:canEdit` grants as they stood in 2020, so replaying them
--    would not reproduce today's 53 custom_parameters values or 276 role grants.

-- 1. role_permissions / center_role_permissions -- children of permissions, deleted first so the
--    permissions delete in step 2 never hits a foreign-key violation (MySQL error 1451).
DELETE rp FROM role_permissions rp
INNER JOIN permissions p ON p.id = rp.permission_id
WHERE p.permission IN ('crp:{0}:project:{1}:impacts', 'crp:{0}:project:{1}:impacts:canEdit');

DELETE crp FROM center_role_permissions crp
INNER JOIN permissions p ON p.id = crp.permission_id
WHERE p.permission IN ('crp:{0}:project:{1}:impacts', 'crp:{0}:project:{1}:impacts:canEdit');

-- 2. permissions -- the two permission strings themselves.
DELETE FROM permissions
WHERE permission IN ('crp:{0}:project:{1}:impacts', 'crp:{0}:project:{1}:impacts:canEdit');

-- 3. custom_parameters -- children of parameters, deleted before the catalog rows.
DELETE cp FROM custom_parameters cp
INNER JOIN parameters p ON p.id = cp.parameter_id
WHERE p.`key` IN ('crp_show_section_impact_covid19', 'crp_show_section_impact_covid19_ranges_years',
  'crp_covid_required');

-- 4. parameters -- the three specificity keys.
DELETE FROM parameters
WHERE `key` IN ('crp_show_section_impact_covid19', 'crp_show_section_impact_covid19_ranges_years',
  'crp_covid_required');

-- 5. section_statuses -- independent of the chain above; its FK (section_statuses_impacts) points at
--    project_impacts, which no statement in this migration touches.
DELETE FROM section_statuses
WHERE section_name = 'impacts';
