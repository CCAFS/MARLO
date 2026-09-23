-- Specificity: project_activity_creation_active
-- ON  -> the project writes the activity title directly in the Activities section.
-- OFF -> the activity title is picked from the Activity management catalog, and that Admin section is shown.

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '1', 'project_activity_creation_active', 'Allow creating activities inside a project by writing the activity title directly. When it is off, the title is chosen from the Activity management catalog and that Admin section is available.', '1', 'false', '2');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '3', 'project_activity_creation_active', 'Allow creating activities inside a project by writing the activity title directly. When it is off, the title is chosen from the Activity management catalog and that Admin section is available.', '1', 'false', '2');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '4', 'project_activity_creation_active', 'Allow creating activities inside a project by writing the activity title directly. When it is off, the title is chosen from the Activity management catalog and that Admin section is available.', '1', 'false', '2');

-- Seed every Global Unit with the behaviour it has today: free text everywhere, catalog from id 45 on,
-- which is what BaseAction.isAiccra() resolved to before this specificity existed.
INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, IF(g.id >= 45, 'false', 'true'), 3, 1, NOW(), 3, 'Seed project_activity_creation_active with the current behaviour', g.id
FROM global_units AS g
INNER JOIN parameters AS p ON p.global_unit_type_id = g.global_unit_type_id
WHERE p.`key` = 'project_activity_creation_active';
