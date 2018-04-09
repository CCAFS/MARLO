DROP TABLE IF EXISTS ciat_roles_user;
CREATE TEMPORARY TABLE ciat_roles_user (
id BIGINT(20),
acronym VARCHAR(50)
);

INSERT INTO ciat_roles_user
SELECT
user_roles.user_id,

CASE roles.acronym
WHEN 'Admin'
THEN 'CRP-Admin'
WHEN 'RPL'
THEN 'FPL'
WHEN 'Superadmin'
THEN 'SuperAdmin'
ELSE roles.acronym
END as ti
FROM
roles
INNER JOIN user_roles ON user_roles.role_id = roles.id
WHERE
roles.global_unit_id = 23
AND (roles.acronym != 'SL' AND roles.acronym != 'RAD');

INSERT INTO user_roles(user_id, role_id)
SELECT ru.id, r.id 
FROM ciat_roles_user ru , roles r
WHERE r.acronym = ru.acronym
AND r.global_unit_id = 29;