Insert into  permissions(permission, description, type)
Values ('crp:{0}:project:{1}:innovationsList', 'Can make changes in the Project Innovation List section in the reporting round.', 1);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'ML','CP','PL', 'PC', 'RPL', 'FPL', 'PMU')
AND
p.permission = 'crp:{0}:project:{1}:innovationsList';

Insert into  permissions(permission, description, type)
Values ('crp:{0}:project:{1}:studies', 'Can make changes in the Project Studies List section in the reporting round.', 1);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'ML','CP','PL', 'PC', 'RPL', 'FPL', 'PMU')
AND
p.permission = 'crp:{0}:project:{1}:studies';

