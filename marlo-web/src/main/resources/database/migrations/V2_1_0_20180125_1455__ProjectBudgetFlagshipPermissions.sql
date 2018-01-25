INSERT INTO permissions (`permission`,description,type)
VALUES ('crp:{0}:project:{1}:budgetByFlagship:*','Can Edit Project Budget Flagship', 1);

INSERT INTO permissions (`permission`,description,type)
VALUES ('crp:{0}:project:{1}:budgetByFlagship:w1w2','Can Edit Project Budget Flagship - W1/W2', 1);

INSERT INTO permissions (`permission`,description,type)
VALUES ('crp:{0}:project:{1}:budgetByFlagship:w3','Can Edit Project Budget Flagship - W3', 1);

INSERT INTO permissions (`permission`,description,type)
VALUES ('crp:{0}:project:{1}:budgetByFlagship:bilateral','Can Edit Project Budget Flagship - Bilateral', 1);

INSERT INTO permissions (`permission`,description,type)
VALUES ('crp:{0}:project:{1}:budgetByFlagship:center','Can Edit Project Budget Flagship - Center', 1);

-- Role Permission CCAFS
INSERT INTO role_permissions (role_id,permission_id)
SELECT 14,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w1w2';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 14,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w3';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 12,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w3';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 2,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w3';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 4,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w3';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 14,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:bilateral';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 12,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:bilateral';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 2,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:bilateral';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 4,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:bilateral';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 14,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:center';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 12,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:center';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 2,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:center';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 4,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:center';

-- Role Permission FTA
INSERT INTO role_permissions (role_id,permission_id)
SELECT 124,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w1w2';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 124,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w3';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 122,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w3';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 115,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w3';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 116,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w3';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 124,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:bilateral';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 122,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:bilateral';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 115,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:bilateral';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 116,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:bilateral';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 124,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:center';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 122,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:center';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 115,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:center';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 116,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:center';

-- Role Permission WLE
INSERT INTO role_permissions (role_id,permission_id)
SELECT 33,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w1w2';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 33,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w3';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 35,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w3';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 82,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w3';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 49,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:w3';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 33,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:bilateral';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 35,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:bilateral';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 82,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:bilateral';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 49,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:bilateral';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 33,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:center';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 35,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:center';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 82,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:center';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 49,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByFlagship:center';