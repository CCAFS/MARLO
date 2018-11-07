INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (1, 'crp_powb_program_change', 'Parameter to Able the powb Program Change Section',1,2);
INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (3, 'crp_powb_program_change', 'Parameter to Able the powb Program Change Section',1,2);
INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (4, 'crp_powb_program_change', 'Parameter to Able the powb Program Change Section',1,2);

INSERT INTO custom_parameters(parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT
p.id, 'false', 3, 1, now(), 3, '', g.id
FROM
global_units AS g
INNER JOIN global_unit_types AS gt ON g.global_unit_type_id = gt.id
INNER JOIN parameters AS p ON p.global_unit_type_id = gt.id
WHERE
p.`key` = 'crp_powb_program_change'
AND
p.global_unit_type_id=1;

INSERT INTO custom_parameters(parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT
p.id, 'false', 3, 1, now(), 3, '', g.id
FROM
global_units AS g
INNER JOIN global_unit_types AS gt ON g.global_unit_type_id = gt.id
INNER JOIN parameters AS p ON p.global_unit_type_id = gt.id
WHERE
p.`key` = 'crp_powb_program_change'
AND
p.global_unit_type_id=3;

INSERT INTO custom_parameters(parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT
p.id, 'false', 3, 1, now(), 3, '', g.id
FROM
global_units AS g
INNER JOIN global_unit_types AS gt ON g.global_unit_type_id = gt.id
INNER JOIN parameters AS p ON p.global_unit_type_id = gt.id
WHERE
p.`key` = 'crp_powb_program_change'
AND
p.global_unit_type_id=4;
