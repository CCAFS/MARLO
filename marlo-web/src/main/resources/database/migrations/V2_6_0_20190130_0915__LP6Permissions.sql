Insert into  permissions(permission, description, type)
Values ('crp:{0}:project:{1}:contributionsLP6', 'Base Permission to Project Contribution LP6 section in the reporting round.', 1);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'ML','CP','PL', 'PC', 'RPL', 'FPL', 'PMU')
AND
p.permission = 'crp:{0}:project:{1}:contributionsLP6';

Insert into  permissions(permission, description, type)
Values ('crp:{0}:project:{1}:contributionsLP6:canEdit', 'Can make changes in the Project Contribution LP6 section section in the reporting round.', 1);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'ML','CP','PL', 'PC', 'RPL', 'FPL', 'PMU')
AND
p.permission = 'crp:{0}:project:{1}:contributionsLP6:canEdit';