#Only adding the roles to the big data platform
INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('api', 'full access to REST Api', 0);

INSERT INTO `roles` (`description`, `acronym`, `crp_id`, `order`) VALUES ('External System', 'ES', 24, 16);

INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ((SELECT r.id FROM `roles` r WHERE r.acronym = 'ES' AND crp_id = 24), 
(SELECT p.id FROM `permissions` p WHERE p.permission = 'api'));

INSERT INTO `users` (`first_name`,`last_name`,`username`,`email`,`password`,`is_cgiar_user`,
`created_by`,`active_since`,`modified_by`,`modification_justification`,`is_active`,`auto_save`)
VALUES
('MEL','System','MEL','mel-system@no-reply','c7d37b769b808113be68c16654e076aa',0,3,CURRENT_TIMESTAMP,3,'',1,1);

INSERT INTO `user_roles` (`user_id`, `role_id`) 
VALUES ((SELECT u.id FROM `users` u WHERE u.email = 'mel-system@no-reply'), 
(SELECT r.id FROM roles r WHERE r.acronym = 'ES' AND crp_id = 24));

INSERT INTO `crp_users` (`user_id`,`crp_id`,`is_active`,`created_by`,`active_since`,`modified_by`,`modification_justification`)
VALUES ((SELECT u.id FROM `users` u WHERE u.email = 'mel-system@no-reply'), 24, 1, 3, CURRENT_TIMESTAMP, 3, '');
