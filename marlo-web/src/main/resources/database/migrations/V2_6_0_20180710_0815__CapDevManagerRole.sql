INSERT INTO roles (description, acronym, `order`, global_unit_id)
SELECT 'CapDev Manager', 'CD', 19, gu.id from global_units gu
WHERE gu.is_marlo = 1;

INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:capDev:*', 'Can edit in Annual Report Synthesis Flagship Progress', 1);
--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'CD')
AND
p.permission = 'crp:{0}:capDev:*';