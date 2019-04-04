/* Delete general execution permission */

DELETE rp.*
FROM
  role_permissions as rp
INNER JOIN permissions p ON rp.permission_id = p.id
WHERE
  p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution';

DELETE
FROM
  permissions
WHERE
  permission = 'crp:{0}:project:{1}:budgetByPartners:execution';
  
/* Create execution permission by budget type */ 
/* W1/W2 ~~ W3 ~~ Bilateral ~~ Center Funds*/
INSERT INTO permissions (`permission`,description,type)
VALUES ('crp:{0}:project:{1}:budgetByPartners:execution:1','Can Edit Project Budget Execution W1/W2', 1);
INSERT INTO permissions (`permission`,description,type)
VALUES ('crp:{0}:project:{1}:budgetByPartners:execution:2','Can Edit Project Budget Execution W3', 1);
INSERT INTO permissions (`permission`,description,type)
VALUES ('crp:{0}:project:{1}:budgetByPartners:execution:3','Can Edit Project Budget Execution Bilateral', 1);
INSERT INTO permissions (`permission`,description,type)
VALUES ('crp:{0}:project:{1}:budgetByPartners:execution:4','Can Edit Project Budget Execution Center Funds', 1);

/* Finance Manager */
INSERT INTO role_permissions (role_id,permission_id)
SELECT 10,p.id from permissions p WHERE p.permission LIKE '%crp:{0}:project:{1}:budgetByPartners:execution%';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 113,p.id from permissions p WHERE p.permission LIKE '%crp:{0}:project:{1}:budgetByPartners:execution%';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 365,p.id from permissions p WHERE p.permission LIKE '%crp:{0}:project:{1}:budgetByPartners:execution%';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 120,p.id from permissions p WHERE p.permission LIKE '%crp:{0}:project:{1}:budgetByPartners:execution%';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 198,p.id from permissions p WHERE p.permission LIKE '%crp:{0}:project:{1}:budgetByPartners:execution%';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 229,p.id from permissions p WHERE p.permission LIKE '%crp:{0}:project:{1}:budgetByPartners:execution%';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 144,p.id from permissions p WHERE p.permission LIKE '%crp:{0}:project:{1}:budgetByPartners:execution%';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 322,p.id from permissions p WHERE p.permission LIKE '%crp:{0}:project:{1}:budgetByPartners:execution%';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 340,p.id from permissions p WHERE p.permission LIKE '%crp:{0}:project:{1}:budgetByPartners:execution%';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 260,p.id from permissions p WHERE p.permission LIKE '%crp:{0}:project:{1}:budgetByPartners:execution%';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 291,p.id from permissions p WHERE p.permission LIKE '%crp:{0}:project:{1}:budgetByPartners:execution%';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 166,p.id from permissions p WHERE p.permission LIKE '%crp:{0}:project:{1}:budgetByPartners:execution%';

/* Center Contact Points */
INSERT INTO role_permissions (role_id,permission_id)
SELECT 4,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 82,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 116,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 194,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 225,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 152,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 153,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 140,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 318,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 336,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 256,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 287,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 162,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4';