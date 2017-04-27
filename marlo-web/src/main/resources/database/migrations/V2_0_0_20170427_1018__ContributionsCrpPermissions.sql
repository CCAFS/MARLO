
insert into  role_permissions (role_id,permission_id)

select id,130 from roles where acronym IN ('FPL');

insert into  role_permissions (role_id,permission_id)

select id,206 from roles where acronym IN ('FPL');