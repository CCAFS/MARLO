INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (1, 'crp_location_csv_activities', 'Ask for CSV activities in Locations',1,2);
INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (3, 'crp_location_csv_activities', 'Ask for CSV activities in Locations',1,2);
INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (4, 'crp_location_csv_activities', 'Ask for CSV activities in Locations',1,2);

INSERT INTO custom_parameters(parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT
p.id, 'false', 3, 1, now(), 3, '', g.id
FROM
global_units AS g
INNER JOIN global_unit_types AS gt ON g.global_unit_type_id = gt.id
INNER JOIN parameters AS p ON p.global_unit_type_id = gt.id
WHERE
p.`key` = 'crp_location_csv_activities'
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
p.`key` = 'crp_location_csv_activities'
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
p.`key` = 'crp_location_csv_activities'
AND
p.global_unit_type_id=4;

UPDATE custom_parameters AS cu
INNER JOIN parameters p ON p.id = cu.parameter_id
AND p.`key` = "crp_location_csv_activities"
AND p.global_unit_type_id = 1
SET `value`='true'
WHERE  cu.global_unit_id = 1;