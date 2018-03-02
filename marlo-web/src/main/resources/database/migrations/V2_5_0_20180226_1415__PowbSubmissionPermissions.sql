INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:powbSynthesis:manage:canSubmmit', 'Can submmit the crp POWB', 3);

--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'FPL','PMU')
AND
p.permission = 'crp:{0}:powbSynthesis:manage:canSubmmit';