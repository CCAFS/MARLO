--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym = 'FPM'
AND
p.permission = 'crp:{0}:powbSynthesis:{1}:tocAdjustments:canEdit';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym = 'FPM'
AND
p.permission = 'crp:{0}:powbSynthesis:{1}:evidences:canEdit';