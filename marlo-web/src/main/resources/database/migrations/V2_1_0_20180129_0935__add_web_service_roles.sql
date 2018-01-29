#Delete mel user from big data platform
DELETE cu FROM `crp_users` AS cu WHERE cu.id = (SELECT id FROM (SELECT cu2.id FROM `crp_users` AS cu2 WHERE cu2.user_id = 
(SELECT u.id FROM `users` u WHERE u.email = 'mel-system@no-reply') AND cu2.crp_id = 24) AS t);

DELETE ur FROM `user_roles` AS ur WHERE ur.id = (SELECT id FROM (SELECT ur2.id FROM `user_roles` AS ur2 WHERE ur2.user_id = (SELECT u.id 
FROM `users` u WHERE u.email = 'mel-system@no-reply') AND ur2.role_id = (SELECT r.id FROM roles r 
WHERE r.acronym = 'ES' AND crp_id = 24)) as t);

DELETE rp FROM `role_permissions` AS rp WHERE rp.id = (SELECT id FROM (SELECT rp2.id FROM `role_permissions` AS rp2 WHERE rp2.role_id = 
(SELECT r.id FROM `roles` r WHERE r.acronym = 'ES' AND crp_id = 24)
AND rp2.permission_id = (SELECT p.id FROM `permissions` p WHERE p.permission = 'api')) as t);

DELETE r FROM `roles` AS r WHERE r.id = (SELECT id FROM (SELECT r2.id FROM `roles` AS r2 WHERE r2.acronym = 'ES' AND r2.crp_id = 24) as t);
#Finish deleting mel user from big data platform

#Add more fine grained permissions to REST-api

INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:*:read', 'Read-only access to all REST Api services', 0);
INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:*:create', 'Create access to all REST Api services', 0);
INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:*:update', 'Update access to all REST Api services', 0);
INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:*:delete', 'Delete access to all REST Api services', 0);

INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:institutions:read', 'Read-only access to Institutions REST Api', 0);
INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:institutions:create', 'Create access to Institutions REST Api', 0);
INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:institutions:update', 'Update access to Institutions REST Api', 0);
INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:institutions:delete', 'Delete access to Institutions REST Api', 0);
INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:institutions:*', 'Full access to the Institutions REST Api', 0);

INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:crps:read', 'Read-only access to Crps REST Api', 0);
INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:crps:create', 'Create access to Crps REST Api', 0);
INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:crps:update', 'Update access to Crps REST Api', 0);
INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:crps:delete', 'Delete access to Crps REST Api', 0);
INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api:crps:*', 'Full access to the Crps REST Api', 0);

#Add External System role to CCAFS and FTA
INSERT INTO `roles` (`description`, `acronym`, `crp_id`, `order`) VALUES ('External System', 'ES', 1, 16);
INSERT INTO `roles` (`description`, `acronym`, `crp_id`, `order`) VALUES ('External System', 'ES', 11, 16);

#Start off by providing read only access to all services and the ability to create new institutions for role ES
#CCAFS
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ((SELECT r.id FROM `roles` r WHERE r.acronym = 'ES' AND crp_id = 1), 
(SELECT p.id FROM `permissions` p WHERE p.permission = 'api:*:read'));
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ((SELECT r.id FROM `roles` r WHERE r.acronym = 'ES' AND crp_id = 1), 
(SELECT p.id FROM `permissions` p WHERE p.permission = 'api:institutions:create'));

#FTA
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ((SELECT r.id FROM `roles` r WHERE r.acronym = 'ES' AND crp_id = 11), 
(SELECT p.id FROM `permissions` p WHERE p.permission = 'api:*:read'));
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ((SELECT r.id FROM `roles` r WHERE r.acronym = 'ES' AND crp_id = 11), 
(SELECT p.id FROM `permissions` p WHERE p.permission = 'api:institutions:create'));

#Add external user myCifor (MEL user is already added for CCAFS).
INSERT INTO `users` (`first_name`,`last_name`,`username`,`email`,`password`,`is_cgiar_user`,
`created_by`,`active_since`,`modified_by`,`modification_justification`,`is_active`,`auto_save`)
VALUES
('MyCifor','System','MyCifor','my-cifor@no-reply','c7d37b769b808113be68c16654e076aa',0,3,CURRENT_TIMESTAMP,3,'',1,1);

#Add MEL user to user_roles
INSERT INTO `user_roles` (`user_id`, `role_id`) 
VALUES ((SELECT u.id FROM `users` u WHERE u.email = 'mel-system@no-reply'), 
(SELECT r.id FROM roles r WHERE r.acronym = 'ES' AND crp_id = 1));

#Add myCifor user to user_roles
INSERT INTO `user_roles` (`user_id`, `role_id`) 
VALUES ((SELECT u.id FROM `users` u WHERE u.email = 'my-cifor@no-reply'), 
(SELECT r.id FROM roles r WHERE r.acronym = 'ES' AND crp_id = 11));

#Add MEL user to CCAFS crp_users table
INSERT INTO `crp_users` (`user_id`,`crp_id`,`is_active`,`created_by`,`active_since`,`modified_by`,`modification_justification`)
VALUES ((SELECT u.id FROM `users` u WHERE u.email = 'mel-system@no-reply'), 1, 1, 3, CURRENT_TIMESTAMP, 3, '');

#Add MyCifor user to FTA crp_users table
INSERT INTO `crp_users` (`user_id`,`crp_id`,`is_active`,`created_by`,`active_since`,`modified_by`,`modification_justification`)
VALUES ((SELECT u.id FROM `users` u WHERE u.email = 'my-cifor@no-reply'), 11, 1, 3, CURRENT_TIMESTAMP, 3, '');
