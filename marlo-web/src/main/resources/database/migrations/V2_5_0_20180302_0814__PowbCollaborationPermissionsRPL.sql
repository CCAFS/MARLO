--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'RPL','RPM')
AND
p.permission = 'crp:{0}:powbSynthesis:{1}:collaboration:canEdit';





INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:powbSynthesis:{1}:collaboration{2}:effort', 'Region Can Edit ', 3);

--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'RPL','RPM')
AND
p.permission = 'crp:{0}:powbSynthesis:{1}:collaboration{2}:effort';