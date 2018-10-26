INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:studies:canEdit', 'Can edit Studies without Projects', 3);

--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in ('FPL','FPM','PMU', 'RPM', 'RPL', 'ML', 'CP' )
AND
p.permission = 'crp:{0}:studies:canEdit';