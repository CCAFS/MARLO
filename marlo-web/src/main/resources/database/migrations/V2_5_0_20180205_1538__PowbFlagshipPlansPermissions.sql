INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:powbSynthesis:{1}:flagshipPlans:canEdit', 'Can edit in POWB Flagship Plans', 3);

--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym = 'FPL'
AND
p.permission = 'crp:{0}:powbSynthesis:{1}:flagshipPlans:canEdit';