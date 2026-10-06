-- Specificity: project_activity_creation_active
-- ON  -> the project writes the activity title directly in the Activities section.
-- OFF -> the activity title is picked from the Activity management catalog, and that Admin section is shown.

-- The three inserts below are guarded so the script can be re-run after a failed attempt without duplicating
-- rows: parameters has no unique index on `key`, so a plain retry would silently insert them a second time.
INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '1', 'project_activity_creation_active', 'Allow creating activities inside a project by writing the activity title directly. When it is off, the title is chosen from the Activity management catalog and that Admin section is available.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'project_activity_creation_active' AND global_unit_type_id = '1');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '3', 'project_activity_creation_active', 'Allow creating activities inside a project by writing the activity title directly. When it is off, the title is chosen from the Activity management catalog and that Admin section is available.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'project_activity_creation_active' AND global_unit_type_id = '3');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '4', 'project_activity_creation_active', 'Allow creating activities inside a project by writing the activity title directly. When it is off, the title is chosen from the Activity management catalog and that Admin section is available.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'project_activity_creation_active' AND global_unit_type_id = '4');

-- Seed every Global Unit with the behaviour it has today: free text everywhere, catalog from id 45 on,
-- which is what BaseAction.isAiccra() resolved to before this specificity existed.
--
-- created_by and modified_by resolve user 3 instead of hardcoding it. That is the seed account most migrations
-- use, but it only exists in databases restored from the shared dump: Flyway applies database/migrations only,
-- and users is never seeded there. The subquery yields 3 where the account exists and NULL where it does not,
-- and both columns are nullable, so the foreign key to users accepts either. No code reads them.
INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, IF(g.id >= 45, 'false', 'true'), (SELECT u.id FROM users u WHERE u.id = 3), 1, NOW(),
  (SELECT u.id FROM users u WHERE u.id = 3), 'Seed project_activity_creation_active with the current behaviour', g.id
FROM global_units AS g
INNER JOIN parameters AS p ON p.global_unit_type_id = g.global_unit_type_id
WHERE p.`key` = 'project_activity_creation_active';
