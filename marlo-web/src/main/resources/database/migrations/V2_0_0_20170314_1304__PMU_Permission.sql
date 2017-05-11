insert into role_permissions (role_id,permission_id) 
select distinct ro.id,rop.permission_id from  roles  ro 
,role_permissions rop
where ro.acronym='PMU' and ro.crp_id not in (1,5)
and rop.role_id=14;