SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE,
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_roles
-- ----------------------------
INSERT INTO `user_roles` VALUES ('1', '1', '6');
INSERT INTO `user_roles` VALUES ('2', '3', '6');
INSERT INTO `user_roles` VALUES ('3', '1057', '6');
INSERT INTO `user_roles` VALUES ('4', '843', '6');
INSERT INTO `user_roles` VALUES ('5', '28', '3');
INSERT INTO `user_roles` VALUES ('6', '1110', '3');
INSERT INTO `user_roles` VALUES ('7', '1025', '3');
INSERT INTO `user_roles` VALUES ('8', '66', '4');
INSERT INTO `user_roles` VALUES ('9', '55', '4');
INSERT INTO `user_roles` VALUES ('10', '1109', '4');
INSERT INTO `user_roles` VALUES ('11', '1111', '4');
INSERT INTO `user_roles` VALUES ('12', '904', '4');
INSERT INTO `user_roles` VALUES ('13', '1112', '4');
INSERT INTO `user_roles` VALUES ('14', '1113', '4');
INSERT INTO `user_roles` VALUES ('15', '1093', '4');
INSERT INTO `user_roles` VALUES ('17', '1114', '6');
INSERT INTO `user_roles` VALUES ('18', '1115', '6');
INSERT INTO `user_roles` VALUES ('19', '1116', '6');
INSERT INTO `user_roles` VALUES ('20', '1117', '6');
INSERT INTO `user_roles` VALUES ('22', '1118', '6');
INSERT INTO `user_roles` VALUES ('23', '13', '6');
INSERT INTO `user_roles` VALUES ('24', '1119', '6');
INSERT INTO `user_roles` VALUES ('25', '1106', '6');
INSERT INTO `user_roles` VALUES ('26', '1107', '6');
INSERT INTO `user_roles` VALUES ('27', '1120', '6');
INSERT INTO `user_roles` VALUES ('28', '1121', '6');
INSERT INTO `user_roles` VALUES ('29', '1122', '4');
INSERT INTO `user_roles` VALUES ('30', '1123', '5');
INSERT INTO `user_roles` VALUES ('31', '1124', '6');
INSERT INTO `user_roles` VALUES ('32', '1125', '5');
INSERT INTO `user_roles` VALUES ('33', '1126', '6');
INSERT INTO `user_roles` VALUES ('34', '1127', '5');
INSERT INTO `user_roles` VALUES ('35', '1129', '6');
INSERT INTO `user_roles` VALUES ('36', '1116', '2');
