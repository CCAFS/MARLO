insert into  role_permissions (role_id,permission_id)

select id,452 from roles where acronym IN ('FPL');