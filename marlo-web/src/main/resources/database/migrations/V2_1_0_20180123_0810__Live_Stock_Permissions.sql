delete rp
 from roles r
inner JOIN role_permissions rp on r.id=rp.role_id
inner join  permissions pe on pe.id=rp.permission_id


where r.crp_id in (7) and r.acronym IN ('FPM') and pe.permission like '%funding%';

insert into role_permissions (role_id,permission_id)
select r2.id,pe.id from roles r
inner JOIN role_permissions rp on r.id=rp.role_id
inner join  permissions pe on pe.id=rp.permission_id
inner join roles r2 on r2.acronym=r.acronym

where r.crp_id in (3) and r2.crp_id=7 and r.acronym IN ('CL') and pe.permission like '%project%' and pe.permission not in ('crp:{0}:project:{1}:deleteProject');

