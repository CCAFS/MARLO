
insert into role_permissions (role_id,permission_id)
select r.id,101 from roles r


where r.crp_id in (7) and  r.acronym IN ('CL');

