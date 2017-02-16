INSERT INTO `permissions` (`id`, `permission`, `description`, `type`) VALUES ('468', 'crp:{0}:crpIndicators:{1}:*', 'Can edit crp Indicators', '0');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('12', '468');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('11', '468');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('4', '468');

INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ('2', '468');
