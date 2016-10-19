
insert into role_permissions (role_id,permission_id)
SELECT 52,permission_id FROM role_permissions where role_id=7
UNION
SELECT 57,permission_id FROM role_permissions where role_id=7
UNION
SELECT 58,permission_id FROM role_permissions where role_id=7
union 
SELECT 59,permission_id FROM role_permissions where role_id=7
UNION

SELECT 53,permission_id FROM role_permissions where role_id=9
UNION
SELECT 54,permission_id FROM role_permissions where role_id=9
UNION
SELECT 55,permission_id FROM role_permissions where role_id=9
union 
SELECT 56,permission_id FROM role_permissions where role_id=9