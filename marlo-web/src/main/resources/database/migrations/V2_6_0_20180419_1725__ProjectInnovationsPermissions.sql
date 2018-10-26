Insert into  permissions(permission, description, type)
Values ('crp:{0}:project:{1}:innovations:canEdit', 'Can make changes in the Project Highlights List section in the reporting round.', 1);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'ML','CP','PL', 'PC', 'RPL', 'FPL')
AND
p.permission = 'crp:{0}:project:{1}:innovations:canEdit';