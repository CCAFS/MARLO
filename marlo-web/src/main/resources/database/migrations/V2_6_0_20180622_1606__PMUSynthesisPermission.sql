UPDATE permissions set type=3 where permission like "%crp:{0}:reportSynthesis:{1}%";

INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:reportSynthesis:*', 'Can edit in Annual Report Synthesis', 3);
--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'PMU')
AND
p.permission = 'crp:{0}:reportSynthesis:*';

