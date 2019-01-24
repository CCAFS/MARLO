Insert into  permissions(permission, description, type)
Values ('crp:{0}:project:{1}:policyList', 'Can make changes in the Project Policies List section in the reporting round.', 1);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'ML','CP','PL', 'PC', 'RPL', 'FPL', 'PMU')
AND
p.permission = 'crp:{0}:project:{1}:policyList';

Insert into  permissions(permission, description, type)
Values ('crp:{0}:project:{1}:policies', 'Base Permission to the Project Policies section in the reporting round.', 1);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'ML','CP','PL', 'PC', 'RPL', 'FPL', 'PMU')
AND
p.permission = 'crp:{0}:project:{1}:policies';

Insert into  permissions(permission, description, type)
Values ('crp:{0}:project:{1}:policies:canEdit', 'Can make changes in the Project Policies section in the reporting round.', 1);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'ML','CP','PL', 'PC', 'RPL', 'FPL', 'PMU')
AND
p.permission = 'crp:{0}:project:{1}:policies:canEdit';