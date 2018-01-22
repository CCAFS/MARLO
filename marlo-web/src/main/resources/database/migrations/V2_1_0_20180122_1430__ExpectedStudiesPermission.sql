INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('crp:{0}:project:{1}:expectedStudies:*', 'Can edit Expeted Studies', '1');


insert into role_permissions (role_id,permission_id)
select ro.id ,(select id from permissions where permission='crp:{0}:project:{1}:expectedStudies:*') from roles ro 
where ro.acronym in ('PL' , 'PC', 'FPL','FPM','CL','RPL','RPM','ML','CP');