update permissions set `permission` = 'api:*' where id=505;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.id = 17
AND
p.permission = 'api:*';

