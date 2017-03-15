INSERT INTO `permissions` (`id`, `permission`, `description`, `type`) VALUES ('469', 'crp:{0}:impactPathway:{1}:submit', 'Can submit Impact', '1');
INSERT INTO `permissions` (`id`, `permission`, `description`, `type`) VALUES ('470', 'crp:{0}:impactPathway:{1}:canEdit', 'Can submit Impact', '1');

insert into role_permissions (role_id,permission_id) 
select id,469 from  roles where acronym='FPL';

insert into role_permissions (role_id,permission_id) 
select id,470 from  roles where acronym='FPL';

insert into role_permissions (role_id,permission_id) 
select distinct 68,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=12 AND p.id not in (423,469,461);

insert into role_permissions (role_id,permission_id) 
select distinct 69,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=12 AND p.id not in (423,469,461);

insert into role_permissions (role_id,permission_id) 
select distinct 70,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=12 AND p.id not in (423,469,461);

insert into role_permissions (role_id,permission_id) 
select distinct 71,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=12 AND p.id not in (423,469,461);

insert into role_permissions (role_id,permission_id) 
select distinct 72,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=12 AND p.id not in (423,469,461);

insert into role_permissions (role_id,permission_id) 
select distinct 73,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=12 AND p.id not in (423,469,461);

insert into role_permissions (role_id,permission_id) 
select distinct 74,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=12 AND p.id not in (423,469);