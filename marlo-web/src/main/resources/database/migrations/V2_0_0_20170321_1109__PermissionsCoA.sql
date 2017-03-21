insert into role_permissions (role_id,permission_id) 
select 31,per.id from role_permissions ro INNER JOIN permissions per on per.id=ro.permission_id where ro.role_id=29 and per.type=1;
