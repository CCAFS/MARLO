INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in ('PMU','FPL', 'FPM', 'RPM', 'RPL')
AND
p.permission = 'crp:{0}:publication:{1}:*';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in ('PMU','FPL', 'FPM', 'RPM', 'RPL')
AND
p.permission = 'crp:{0}:publication:add';

update permissions set `type`=1 where permission='crp:{0}:publication:{1}:*';