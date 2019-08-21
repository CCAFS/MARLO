INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:project:{1}:budgetByPartners:execution:canEdit', 'Can edit in project budgetByPartners Expenditure', 1);
--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'FM')
AND
p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:canEdit';