DROP VIEW IF EXISTS `user_permissions`;

CREATE  VIEW `user_permissions` AS 
select u.id,r.acronym 'ro.acronym',r.id 'role_id', replace(`p`.`permission`,'{0}',cp.acronym)'permission' ,cp.acronym  as 'crp_acronym'  from 
users u left join user_roles ro on ro.user_id=u.id
inner join roles r on r.id=ro.role_id
inner join role_permissions rp on rp.role_id=r.id
inner join permissions p on p.id=rp.permission_id
inner join crp_users crp on u.id=crp.user_id and crp.crp_id=r.crp_id
inner  join crps cp on cp.id=crp.crp_id
where p.type=0
UNION 
select u.id,r.acronym,r.id 'rolId',
 replace(replace(`p`.`permission`,'{0}',cp.acronym),'{1}','project_id'),cp.acronym as 'crp_acronym' 
  from 
users u inner join user_roles ro on ro.user_id=u.id
inner join roles r on r.id=ro.role_id
inner join role_permissions rp on rp.role_id=r.id
inner join permissions p on p.id=rp.permission_id
inner join crp_users crp on u.id=crp.user_id and crp.crp_id=r.crp_id
inner  join crps cp on cp.id=crp.crp_id
where p.type=1;

