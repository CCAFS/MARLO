-- Rice

INSERT INTO role_permissions (role_id,permission_id)
SELECT
r2.id AS role_id,
permissions.id AS permission_id
FROM
role_permissions
INNER JOIN roles AS r1 ON role_permissions.role_id = r1.id
INNER JOIN roles AS r2 ON r1.acronym = r2.acronym
INNER JOIN permissions ON role_permissions.permission_id = permissions.id
WHERE
r1.global_unit_id = 1 AND
r2.global_unit_id = 16;

-- RTB

INSERT INTO role_permissions (role_id,permission_id)
SELECT
r2.id AS role_id,
permissions.id AS permission_id
FROM
role_permissions
INNER JOIN roles AS r1 ON role_permissions.role_id = r1.id
INNER JOIN roles AS r2 ON r1.acronym = r2.acronym
INNER JOIN permissions ON role_permissions.permission_id = permissions.id
WHERE
r1.global_unit_id = 1 AND
r2.global_unit_id = 17;

-- Fish

INSERT INTO role_permissions (role_id,permission_id)
SELECT
r2.id AS role_id,
permissions.id AS permission_id
FROM
role_permissions
INNER JOIN roles AS r1 ON role_permissions.role_id = r1.id
INNER JOIN roles AS r2 ON r1.acronym = r2.acronym
INNER JOIN permissions ON role_permissions.permission_id = permissions.id
WHERE
r1.global_unit_id = 1 AND
r2.global_unit_id = 27;

-- GLDC

INSERT INTO role_permissions (role_id,permission_id)
SELECT
r2.id AS role_id,
permissions.id AS permission_id
FROM
role_permissions
INNER JOIN roles AS r1 ON role_permissions.role_id = r1.id
INNER JOIN roles AS r2 ON r1.acronym = r2.acronym
INNER JOIN permissions ON role_permissions.permission_id = permissions.id
WHERE
r1.global_unit_id = 1 AND
r2.global_unit_id = 28;

-- EiB

INSERT INTO role_permissions (role_id,permission_id)
SELECT
r2.id AS role_id,
permissions.id AS permission_id
FROM
role_permissions
INNER JOIN roles AS r1 ON role_permissions.role_id = r1.id
INNER JOIN roles AS r2 ON r1.acronym = r2.acronym
INNER JOIN permissions ON role_permissions.permission_id = permissions.id
WHERE
r1.global_unit_id = 1 AND
r2.global_unit_id = 25;