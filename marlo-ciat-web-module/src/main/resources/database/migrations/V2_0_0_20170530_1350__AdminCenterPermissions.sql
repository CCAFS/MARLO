SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission` varchar(255) NOT NULL,
  `description` text,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of permissions
-- ----------------------------
INSERT INTO `permissions` VALUES ('1', '*', 'Full privileges on all the platform', '0');
INSERT INTO `permissions` VALUES ('2', 'center:{0}:*', 'Full privileges on the Research Center', '0');
INSERT INTO `permissions` VALUES ('3', 'center:{0}:area:{1}:*', 'Full privileges on the Research Area', '0');
INSERT INTO `permissions` VALUES ('4', 'center:{0}:area:{1}:program:{2}:*', 'Full privileges on the Research Program', '0');
INSERT INTO `permissions` VALUES ('5', 'center:{0}:area:{1}:program:{2}:addProject', 'Can Add new Program Project', '0');
INSERT INTO `permissions` VALUES ('6', 'center:{0}:area:{1}:program:{2}:project:{3}:*', 'Full privileges on the Project', '0');
INSERT INTO `permissions` VALUES ('7', 'center:{0}:area:{1}:program:{2}:project:{3}:addDeliverable', 'Can Add new Project Deliverable', '0');
INSERT INTO `permissions` VALUES ('8', 'center:{0}:area:{1}:program:{2}:project:{3}:projectDescription:*', 'Full privileges on the Project Description Section', '0');
INSERT INTO `permissions` VALUES ('9', 'center:{0}:area:{1}:program:{2}:project:{3}:projectPartners:*', 'Full privileges on the Project Partners Section', '0');
INSERT INTO `permissions` VALUES ('10', 'center:{0}:area:{1}:program:{2}:project:{3}:deliverable:{4}:*', 'Full privileges on the Project Deliverables Section', '0');
INSERT INTO `permissions` VALUES ('11', 'center:{0}:admin:*', 'Full privileges on the center admin section', '0');


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `roles_permission_roles_idx` (`role_id`) USING BTREE,
  KEY `roles_permission_user_permission_idx` (`permission_id`) USING BTREE,
  CONSTRAINT `role_permissions_ibfk_1` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `role_permissions_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_permissions
-- ----------------------------
INSERT INTO `role_permissions` VALUES ('1', '6', '1');
INSERT INTO `role_permissions` VALUES ('2', '3', '3');
INSERT INTO `role_permissions` VALUES ('3', '4', '4');
INSERT INTO `role_permissions` VALUES ('4', '2', '2');
INSERT INTO `role_permissions` VALUES ('5', '2', '11');
