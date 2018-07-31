INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:sharedProjects:canEdit', 'Can edit MALRO shared projects sections', 0);
--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'CRP-Admin')
AND
p.permission = 'crp:{0}:sharedProjects:canEdit';