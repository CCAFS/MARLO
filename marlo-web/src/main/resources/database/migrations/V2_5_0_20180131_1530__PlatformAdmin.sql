
-- TEMPORALLY MAKEUP PERMISSION TO ACCES PLATFOMRS ADMINS
INSERT INTO role_permissions (role_id, permission_id)
SELECT 138, id from permissions where permission='crp:{0}:*';