INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, 469 From roles r
where r.acronym = 'CRP-Admin';