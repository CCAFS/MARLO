insert into user_roles (user_id, role_id)
SELECT
cr.user_id,
r.id
FROM
center_user_roles AS cr
INNER JOIN center_roles AS crr ON cr.role_id = crr.id 
INNER JOIN roles AS r ON crr.description = r.description 
WHERE
r.global_unit_id = 23