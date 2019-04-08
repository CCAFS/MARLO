/* Delete crp:{0}:project:{1}:budgetByPartners:execution:4 permission of CP role */

DELETE rp.*
FROM
  role_permissions as rp
INNER JOIN permissions p ON rp.permission_id = p.id
WHERE
  p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4' AND rp.role_id IN (4,82,116,194,225,152,153,140,318,336,256,287,162);

/* Add permission for Center Contact Points using the institutionID to edit Center Funds executions */

INSERT INTO permissions (`permission`,description,type)
VALUES ('crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}','Can Edit Project Budget Execution Center Funds for a specific liaison institution', 1);

INSERT INTO role_permissions (role_id,permission_id)
SELECT 4,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 82,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 116,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 194,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 225,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 152,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 153,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 140,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 318,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 336,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 256,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 287,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';

INSERT INTO role_permissions (role_id,permission_id)
SELECT 162,p.id from permissions p WHERE p.permission = 'crp:{0}:project:{1}:budgetByPartners:execution:4:liaison:{2}';