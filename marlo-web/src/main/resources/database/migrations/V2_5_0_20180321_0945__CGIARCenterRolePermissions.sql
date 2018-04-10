INSERT INTO role_permissions (role_id,permission_id)
Select r2.id, rd.permission_id FROM
roles r, roles r2, role_permissions rd
WHERE
rd.role_id = r.id
AND
r.acronym = r2.acronym
AND
r.global_unit_id = 1
AND
r2.global_unit_id =29
AND
rd.permission_id < 489