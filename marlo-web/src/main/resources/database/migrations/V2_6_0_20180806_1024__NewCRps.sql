
-- RTB --
DELETE from custom_parameters where global_unit_id = 17;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT cp.parameter_id, cp.`value`, 3, cp.is_active, now(), 3, '', 17 from custom_parameters cp
WHERE cp.global_unit_id = 1;

UPDATE global_units set is_marlo =1, login=1 where id = 17;

INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (1082, 1, 1082, NOW(), 1082, '', 17);

INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (1, 1, 1082, NOW(), 1082, '', 17);

-- Fish --
DELETE from custom_parameters where global_unit_id = 27;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT cp.parameter_id, cp.`value`, 3, cp.is_active, now(), 3, '', 27 from custom_parameters cp
WHERE cp.global_unit_id = 1;

UPDATE global_units set is_marlo =1, login=1 where id = 27;

INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (1082, 1, 1082, NOW(), 1082, '', 27);

INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (1, 1, 1082, NOW(), 1082, '', 27);

-- GLDC --
DELETE from custom_parameters where global_unit_id = 28;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT cp.parameter_id, cp.`value`, 3, cp.is_active, now(), 3, '', 28 from custom_parameters cp
WHERE cp.global_unit_id = 1;

UPDATE global_units set is_marlo =1, login=1 where id = 28;

INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (1082, 1, 1082, NOW(), 1082, '', 28);

INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (1, 1, 1082, NOW(), 1082, '', 28);

-- EiB

UPDATE global_units set is_marlo =1, login=1 where id = 25;

INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (1082, 1, 1082, NOW(), 1082, '', 25);

INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (1, 1, 1082, NOW(), 1082, '', 25);


