INSERT INTO roles (description, acronym, `order`, global_unit_id)
VALUES ('Finance person', 'FM', 3, 7);

INSERT INTO role_permissions (role_id, permission_id)
SELECT
(Select r.id from roles r where r.global_unit_id = 7 and r.acronym='FM') AS roleID,
role_permissions.permission_id
FROM
role_permissions
INNER JOIN roles ON role_permissions.role_id = roles.id
WHERE
roles.global_unit_id = 1 AND
roles.acronym = 'FM';