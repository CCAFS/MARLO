INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (1, 'crp_cd_rol', 'Identifier for CadDev Manager Role',3,1);
INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (3, 'crp_cd_rol', 'Identifier for CadDev Manager Role',3,1);
INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (4, 'crp_cd_rol', 'Identifier for CadDev Manager Role',3,1);

INSERT INTO custom_parameters(parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT
p.id, r.id, 3, 1, now(), 3, '', g.id
FROM
global_units AS g
INNER JOIN global_unit_types AS gt ON g.global_unit_type_id = gt.id
INNER JOIN parameters AS p ON p.global_unit_type_id = gt.id
INNER JOIN roles AS r ON r.global_unit_id = g.id
WHERE 
r.acronym in('CD')
AND
p.`key` = 'crp_cd_rol'
AND
p.global_unit_type_id=1;

INSERT INTO custom_parameters(parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT
p.id, r.id, 3, 1, now(), 3, '', g.id
FROM
global_units AS g
INNER JOIN global_unit_types AS gt ON g.global_unit_type_id = gt.id
INNER JOIN parameters AS p ON p.global_unit_type_id = gt.id
INNER JOIN roles AS r ON r.global_unit_id = g.id
WHERE 
r.acronym in('CD')
AND
p.`key` = 'crp_cd_rol'
AND
p.global_unit_type_id=3;

INSERT INTO custom_parameters(parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT
p.id, r.id, 3, 1, now(), 3, '', g.id
FROM
global_units AS g
INNER JOIN global_unit_types AS gt ON g.global_unit_type_id = gt.id
INNER JOIN parameters AS p ON p.global_unit_type_id = gt.id
INNER JOIN roles AS r ON r.global_unit_id = g.id
WHERE 
r.acronym in('CD')
AND
p.`key` = 'crp_cd_rol'
AND
p.global_unit_type_id=4;