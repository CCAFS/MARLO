SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for center_roles
-- ----------------------------
DROP TABLE IF EXISTS `center_roles`;
CREATE TABLE `center_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` text NOT NULL,
  `acronym` varchar(50) NOT NULL,
  `center_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `crp_id` (`center_id`) USING BTREE,
  KEY `fk_roles_center` (`center_id`) USING BTREE,
  CONSTRAINT `center_roles_ibfk_1` FOREIGN KEY (`center_id`) REFERENCES `centers` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_roles
-- ----------------------------
INSERT INTO `center_roles` VALUES ('1', 'Administrator', 'Admin', '1');
INSERT INTO `center_roles` VALUES ('2', 'CIAT Program Coordinator', 'Coord', '1');
INSERT INTO `center_roles` VALUES ('3', 'Research Area Director', 'RAD', '1');
INSERT INTO `center_roles` VALUES ('4', 'Research Program Leader', 'RPL', '1');
INSERT INTO `center_roles` VALUES ('5', 'Guest', 'G', '1');
INSERT INTO `center_roles` VALUES ('6', 'Super Administrator', 'Superadmin', '1');
INSERT INTO `center_roles` VALUES ('7', 'Scientist Leader', 'SL', '1');

-- ----------------------------
-- Table structure for center_role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `center_role_permissions`;
CREATE TABLE `center_role_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `roles_permission_roles_idx` (`role_id`) USING BTREE,
  KEY `roles_permission_user_permission_idx` (`permission_id`) USING BTREE,
  CONSTRAINT `center_role_permissions_ibfk_1` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `center_role_permissions_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `center_roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_role_permissions
-- ----------------------------
INSERT INTO `center_role_permissions` VALUES ('1', '6', '419');
INSERT INTO `center_role_permissions` VALUES ('2', '3', '472');
INSERT INTO `center_role_permissions` VALUES ('3', '4', '473');
INSERT INTO `center_role_permissions` VALUES ('4', '1', '471');
INSERT INTO `center_role_permissions` VALUES ('5', '2', '480');
INSERT INTO `center_role_permissions` VALUES ('6', '7', '473');
