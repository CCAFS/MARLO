-- CLEAR CGIAR CENTER CUSTOM PARAMETERS
Create TEMPORARY table tem_table_pp AS ( 
SELECT
custom_parameters.id
FROM
parameters
INNER JOIN custom_parameters ON custom_parameters.parameter_id = parameters.id
WHERE
custom_parameters.global_unit_id = 29 AND
parameters.global_unit_type_id = 1);

Delete from custom_parameters  
where id IN (SELECT
id
FROM
tem_table_pp);

-- COPY CCAFS PARAMETERS
INSERT INTO `parameters` (global_unit_type_id,`key`, description, `format`, default_value, category)
SELECT 4, `key`, description, `format`, default_value, category from `parameters` WHERE global_unit_type_id=1;

-- COPY CCAFS CUSTOM PARAMETERS VALUES
INSERT INTO `custom_parameters` 
(parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT
(SELECT id from parameters where global_unit_type_id=4 and `key` = p.`key`) AS pp_id,
cp.`value`,
cp.created_by,
cp.is_active,
now(),
cp.modified_by,
cp.modification_justification,
29
FROM
parameters AS p
INNER JOIN custom_parameters AS cp ON cp.parameter_id = p.id AND cp.global_unit_id=1;

-- ADD CGIAR CENTER ROLES
INSERT INTO `roles` VALUES ('160', 'CIAT Admin', 'CRP-Admin', '1', '29');
INSERT INTO `roles` VALUES ('161', 'Management Liaison', 'ML', '12', '29');
INSERT INTO `roles` VALUES ('162', 'Contact point', 'CP', '9', '29');
INSERT INTO `roles` VALUES ('163', 'Project leader', 'PL', '13', '29');
INSERT INTO `roles` VALUES ('164', 'Guest', 'G', '15', '29');
INSERT INTO `roles` VALUES ('165', 'Project coordinator', 'PC', '14', '29');
INSERT INTO `roles` VALUES ('166', 'Finance person', 'FM', '3', '29');
INSERT INTO `roles` VALUES ('167', 'Regional Program Leaders', 'RPL', '7', '29');
INSERT INTO `roles` VALUES ('168', 'Flagship Leaders', 'FPL', '5', '29');
INSERT INTO `roles` VALUES ('169', 'External Evaluator', 'E', '0', '29');
INSERT INTO `roles` VALUES ('170', 'Program Management Unit', 'PMU', '2', '29');
INSERT INTO `roles` VALUES ('171', 'Site Integration Leader', 'SL', '11', '29');
INSERT INTO `roles` VALUES ('172', 'Data Manager', 'DM', '4', '29');
INSERT INTO `roles` VALUES ('173', 'Super Admin', 'SuperAdmin', '0', '29');
INSERT INTO `roles` VALUES ('174', 'Cluster Leader', 'CL', '0', '29');
INSERT INTO `roles` VALUES ('175', 'Flagship Manager', 'FPM', '0', '29');
INSERT INTO `roles` VALUES ('176', 'Regional Manager', 'RPM', '0', '29');

-- ADD CGIAR CENTER USER ROLES
INSERT INTO user_roles (user_id, role_id) VALUES (1,173);
INSERT INTO user_roles (user_id, role_id) VALUES (13,173);
INSERT INTO user_roles (user_id, role_id) VALUES (843,173);
INSERT INTO user_roles (user_id, role_id) VALUES (1057,173);

-- ADD CGIAR CENTER USER ACCESS
INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (1, 1, 3, now(), 3, '', 29);
INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (13, 1, 3, now(), 3, '', 29);
INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (843, 1, 3, now(), 3, '', 29);
INSERT INTO crp_users (user_id, is_active, created_by, active_since, modified_by, modification_justification, global_unit_id)
VALUES (1057, 1, 3, now(), 3, '', 29);

-- ADD CGIAR CENTER * PERMISSION
INSERT INTO role_permissions (role_id, permission_id) 
VALUES (173,419);

-- ACTIVATE CGIAR CENTER LOGIN ACCESS
Update global_units set is_marlo = 1, login = 1 Where id =29;