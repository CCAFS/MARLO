insert into role_permissions (role_id,permission_id) 
select(select id from roles ri where ri.acronym = r.acronym and global_unit_id=47 ), 
rp.permission_id  
from role_permissions rp 
join roles r on r.id = rp.role_id 
join permissions p on p.id = rp.permission_id 
where global_unit_id=1 