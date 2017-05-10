DELETE from role_permissions where role_id in (22,71) and permission_id in (
SELECT id FROM permissions where permission like '%funding%'

);