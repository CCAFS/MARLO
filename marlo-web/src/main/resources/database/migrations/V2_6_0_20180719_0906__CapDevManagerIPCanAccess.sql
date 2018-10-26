INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'CD')
AND
p.permission = 'crp:{0}:impactPathway:canAcess';