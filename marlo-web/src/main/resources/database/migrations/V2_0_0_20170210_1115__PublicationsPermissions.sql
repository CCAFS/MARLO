INSERT INTO `permissions` (`id`, `permission`, `description`, `type`) VALUES ('465', 'crp:{0}:publication:*', 'full privelegies publications', '0');
INSERT INTO `permissions` (`id`, `permission`, `description`, `type`) VALUES ('466', 'crp:{0}:publication:add', 'add publications', '0');
INSERT INTO `permissions` (`id`, `permission`, `description`, `type`) VALUES ('467', 'crp:{0}:publication:{1}:*', 'canEdit priveligies', '0');

INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('2', '465');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('11', '465');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('12', '465');

INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('4', '466');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('4', '467');

