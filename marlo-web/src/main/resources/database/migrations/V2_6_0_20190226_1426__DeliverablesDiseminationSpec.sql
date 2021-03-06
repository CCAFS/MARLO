INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (1, 'crp_has_disemination', 'Parameter to enabled/disabled Dissemination & Metadata and Quality Check in deliverable section',1,2);
INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (3, 'crp_has_disemination', 'Parameter to enabled/disabled Dissemination & Metadata and Quality Check in deliverable section',1,2);
INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (4, 'crp_has_disemination', 'Parameter to enabled/disabled Dissemination & Metadata and Quality Check in deliverable section',1,2);

INSERT INTO custom_parameters(parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT
p.id, 'true', 3, 1, now(), 3, '', g.id
FROM
global_units AS g
INNER JOIN global_unit_types AS gt ON g.global_unit_type_id = gt.id
INNER JOIN parameters AS p ON p.global_unit_type_id = gt.id
WHERE
p.`key` = 'crp_has_disemination'
AND
p.global_unit_type_id=1;

INSERT INTO custom_parameters(parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT
p.id, 'true', 3, 1, now(), 3, '', g.id
FROM
global_units AS g
INNER JOIN global_unit_types AS gt ON g.global_unit_type_id = gt.id
INNER JOIN parameters AS p ON p.global_unit_type_id = gt.id
WHERE
p.`key` = 'crp_has_disemination'
AND
p.global_unit_type_id=3;

INSERT INTO custom_parameters(parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT
p.id, 'true', 3, 1, now(), 3, '', g.id
FROM
global_units AS g
INNER JOIN global_unit_types AS gt ON g.global_unit_type_id = gt.id
INNER JOIN parameters AS p ON p.global_unit_type_id = gt.id
WHERE
p.`key` = 'crp_has_disemination'
AND
p.global_unit_type_id=4;

UPDATE custom_parameters set `value` = 'false' where global_unit_id =21 and parameter_id=(Select id from parameters where `key` = 'crp_has_disemination'
AND
global_unit_type_id=1);

UPDATE custom_parameters set `value` = 'false' where global_unit_id =22 and parameter_id=(Select id from parameters where `key` = 'crp_has_disemination'
AND
global_unit_type_id=1);

UPDATE custom_parameters set `value` = 'false' where global_unit_id =25 and parameter_id=(Select id from parameters where `key` = 'crp_has_disemination'
AND
global_unit_type_id=3);
