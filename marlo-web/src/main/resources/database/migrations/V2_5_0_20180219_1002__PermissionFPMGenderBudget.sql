INSERT INTO `permissions` (`id`, `permission`, `description`, `type`) VALUES (NULL, 'crp:{0}:project:{1}:fundingSource:gender', 'Permission to change gender', '1');
insert into role_permissions (role_id,permission_id)
select ro.id,p.id from permissions p,
roles ro 
 where p.permission='crp:{0}:project:{1}:fundingSource:gender' and ro.acronym='FPM';
