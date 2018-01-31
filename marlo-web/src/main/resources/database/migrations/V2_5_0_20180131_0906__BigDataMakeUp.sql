-- CLEAR BIGDATA CUSTOM PARAMETERS
Create TEMPORARY table tem_table_pp AS ( 
SELECT
custom_parameters.id
FROM
parameters
INNER JOIN custom_parameters ON custom_parameters.parameter_id = parameters.id
WHERE
custom_parameters.global_unit_id = 24 AND
parameters.global_unit_type_id = 1);

Delete from custom_parameters  
where id IN (SELECT
id
FROM
tem_table_pp);

-- COPY CCAFS PARAMETERS
INSERT INTO `parameters` (global_unit_type_id,`key`, description, `format`, default_value, category)
SELECT 3, `key`, description, `format`, default_value, category from `parameters` WHERE global_unit_type_id=1;

-- COPY CCAFS CUSTOM PARAMETERS VALUES
INSERT INTO `custom_parameters` 
(parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT
(SELECT id from parameters where global_unit_type_id=3 and `key` = p.`key`) AS pp_id,
cp.`value`,
cp.created_by,
cp.is_active,
now(),
cp.modified_by,
cp.modification_justification,
24
FROM
parameters AS p
INNER JOIN custom_parameters AS cp ON cp.parameter_id = p.id AND cp.global_unit_id=1;

-- ADD BIGDATA ROLES
INSERT INTO `roles` VALUES ('138', 'BigData Admin', 'CRP-Admin', '1', '24');
INSERT INTO `roles` VALUES ('139', 'Management Liaison', 'ML', '12', '24');
INSERT INTO `roles` VALUES ('140', 'Contact point', 'CP', '9', '24');
INSERT INTO `roles` VALUES ('141', 'Project leader', 'PL', '13', '24');
INSERT INTO `roles` VALUES ('142', 'Guest', 'G', '15', '24');
INSERT INTO `roles` VALUES ('143', 'Project coordinator', 'PC', '14', '24');
INSERT INTO `roles` VALUES ('144', 'Finance person', 'FM', '3', '24');
INSERT INTO `roles` VALUES ('145', 'Regional Program Leaders', 'RPL', '7', '24');
INSERT INTO `roles` VALUES ('146', 'Flagship Leaders', 'FPL', '5', '24');
INSERT INTO `roles` VALUES ('147', 'External Evaluator', 'E', '0', '24');
INSERT INTO `roles` VALUES ('148', 'Program Management Unit', 'PMU', '2', '24');
INSERT INTO `roles` VALUES ('149', 'Site Integration Leader', 'SL', '11', '24');
INSERT INTO `roles` VALUES ('150', 'Data Manager', 'DM', '4', '24');
INSERT INTO `roles` VALUES ('151', 'Super Admin', 'SuperAdmin', '0', '24');

-- ADD BIGDATA USER ROLES
INSERT INTO user_roles (user_id, role_id) VALUES (1,151);
INSERT INTO user_roles (user_id, role_id) VALUES (13,151);
INSERT INTO user_roles (user_id, role_id) VALUES (843,151);
INSERT INTO user_roles (user_id, role_id) VALUES (1057,151);

-- ADD BIGDATA USER ACCESS
INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (1, 1, 3, now(), 3, '', 24);
INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (13, 1, 3, now(), 3, '', 24);
INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (843, 1, 3, now(), 3, '', 24);
INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (1057, 1, 3, now(), 3, '', 24);

-- ADD BIGDATA * PERMISSION
INSERT INTO role_permissions (role_id, permission_id) 
VALUES (151,419);

-- ACTIVATE BIGDATA LOGIN ACCESS
Update global_units set is_marlo = 1, login = 1 Where id =24;