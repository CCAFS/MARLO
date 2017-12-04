insert into role_permissions (role_id, permission_id)
SELECT
r.id,
p.id
FROM
permissions AS p
INNER JOIN center_role_permissions AS rp ON rp.permission_id = p.id 
INNER JOIN center_roles AS crr ON rp.role_id = crr.id
INNER JOIN roles AS r ON crr.description = r.description 
WHERE
r.global_unit_id = 23