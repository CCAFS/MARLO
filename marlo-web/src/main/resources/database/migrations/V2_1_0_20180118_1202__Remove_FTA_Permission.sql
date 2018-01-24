DELETE from role_permissions
WHERE permission_id = (SELECT p.id FROM permissions p WHERE p.permission = 'crp:{0}:project:projectsList:addCoreProject')
AND role_id = (SELECT r.id FROM roles r WHERE r.acronym = 'CP' AND r.crp_id = 11);

DELETE from role_permissions
WHERE permission_id = (SELECT p.id FROM permissions p WHERE p.permission = 'crp:{0}:project:projectsList:addCoreProject')
AND role_id = (SELECT r.id FROM roles r WHERE r.acronym = 'FPL' AND r.crp_id = 11);

DELETE from role_permissions
WHERE permission_id = (SELECT p.id FROM permissions p WHERE p.permission = 'crp:{0}:project:projectsList:addCoreProject')
AND role_id = (SELECT r.id FROM roles r WHERE r.acronym = 'FPM' AND r.crp_id = 11);

DELETE from role_permissions
WHERE permission_id = (SELECT p.id FROM permissions p WHERE p.permission = 'crp:{0}:project:projectsList:addCoreProject')
AND role_id = (SELECT r.id FROM roles r WHERE r.acronym = 'ML' AND r.crp_id = 11);

DELETE from role_permissions
WHERE permission_id = (SELECT p.id FROM permissions p WHERE p.permission = 'crp:{0}:project:projectsList:addBilateralProject')
AND role_id = (SELECT r.id FROM roles r WHERE r.acronym = 'CP' AND r.crp_id = 11);

DELETE from role_permissions
WHERE permission_id = (SELECT p.id FROM permissions p WHERE p.permission = 'crp:{0}:project:projectsList:addBilateralProject')
AND role_id = (SELECT r.id FROM roles r WHERE r.acronym = 'FPL' AND r.crp_id = 11);

DELETE from role_permissions
WHERE permission_id = (SELECT p.id FROM permissions p WHERE p.permission = 'crp:{0}:project:projectsList:addBilateralProject')
AND role_id = (SELECT r.id FROM roles r WHERE r.acronym = 'FPM' AND r.crp_id = 11);

DELETE from role_permissions
WHERE permission_id = (SELECT p.id FROM permissions p WHERE p.permission = 'crp:{0}:project:projectsList:addBilateralProject')
AND role_id = (SELECT r.id FROM roles r WHERE r.acronym = 'ML' AND r.crp_id = 11);