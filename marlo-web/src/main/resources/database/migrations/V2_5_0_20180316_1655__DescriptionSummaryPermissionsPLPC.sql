INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'PL','PC')
AND
p.permission = 'crp:{0}:project:{1}:description:summary';