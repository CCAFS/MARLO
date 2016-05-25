DROP VIEW IF EXISTS `user_permissions`;
CREATE  VIEW `user_permissions` AS 
select u.id,r.acronym,r.id 'rolId', replace(`p`.`permission`,'{0}',crp.crp_id)'permission' ,crp.crp_id from 
users u inner join user_roles ro on ro.user_id=u.id
inner join roles r on r.id=ro.id
inner join role_permissions rp on rp.role_id=r.id
inner join permissions p on p.id=rp.permission_id
inner join crp_users crp on u.id=crp.user_id and crp.id=r.crp_id
where p.type=0
UNION 
select u.id,r.acronym,r.id 'rolId',
 replace(replace(`p`.`permission`,'{0}',crp.crp_id),'{1}','project_id'),crp.crp_id
  from 
users u inner join user_roles ro on ro.user_id=u.id
inner join roles r on r.id=ro.id
inner join role_permissions rp on rp.role_id=r.id
inner join permissions p on p.id=rp.permission_id
inner join crp_users crp on u.id=crp.user_id and crp.id=r.crp_id
where p.type=1;


  