INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'FPM', 'RPM')
AND
p.permission = 'crp:{0}:project:{1}:policyList';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'FPM', 'RPM')
AND
p.permission = 'crp:{0}:project:{1}:policies';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'FPM', 'RPM')
AND
p.permission = 'crp:{0}:project:{1}:policies:canEdit';