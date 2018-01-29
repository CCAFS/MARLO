INSERT INTO permissions (`permission`,description,type)
VALUES ('crp:{0}:project:{1}:budgetByFlagship:canEdit','Can Edit Project Budget Flagship', 1);

INSERT INTO role_permissions (role_id,permission_id)
SELECT 14,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:canEdit';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 12,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:canEdit';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 2,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:canEdit';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 4,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:canEdit';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 124,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:canEdit';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 122,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:canEdit';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 115,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:canEdit';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 116,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:canEdit';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 33,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:canEdit';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 35,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:canEdit';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 82,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:canEdit';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 49,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:canEdit';