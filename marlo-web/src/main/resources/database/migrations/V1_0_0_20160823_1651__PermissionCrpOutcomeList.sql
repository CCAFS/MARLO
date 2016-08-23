START TRANSACTION;

INSERT INTO `permissions` (id,`permission`, `description`, `type`) VALUES (437,'crp:{0}:project:{1}:contributionsCrpList:*', 'Can view list', '1');
insert into role_permissions (role_id,permission_id)
values
(2,437),
(7,437),
(9,437),
(11,437),
(12,437);

 

COMMIT;