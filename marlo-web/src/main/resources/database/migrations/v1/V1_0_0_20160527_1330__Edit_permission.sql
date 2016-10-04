SET FOREIGN_KEY_CHECKS=0;

UPDATE `permissions` SET `permission`='crp:{0}:admin:*' WHERE (`id`='421');
INSERT INTO `permissions` (`id`,`permission`, `description`, `type`) VALUES (422,'crp:{0}:admin:canAcess', 'Can view menu', '0');
INSERT INTO `role_permissions` VALUES ('613', '1', '422')