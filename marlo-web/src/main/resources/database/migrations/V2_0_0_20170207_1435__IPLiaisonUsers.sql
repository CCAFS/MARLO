SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ip_liaison_users
-- ----------------------------
DROP TABLE IF EXISTS `ip_liaison_users`;
CREATE TABLE `ip_liaison_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT 'This field is a link to the table users',
  `institution_id` bigint(20) NOT NULL COMMENT 'This field is a link to the table liaison_institutions',
  PRIMARY KEY (`id`),
  KEY `FK_ip_liaison_users_users__idx` (`user_id`) USING BTREE,
  KEY `FK_ip_liaison_users_institutions__idx` (`institution_id`) USING BTREE,
  CONSTRAINT `ip_liaison_users_ibfk_1` FOREIGN KEY (`institution_id`) REFERENCES `ip_liaison_institutions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ip_liaison_users_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ip_liaison_users
-- ----------------------------
INSERT INTO `ip_liaison_users` VALUES ('1', '122', '1');
INSERT INTO `ip_liaison_users` VALUES ('2', '132', '1');
INSERT INTO `ip_liaison_users` VALUES ('3', '28', '2');
INSERT INTO `ip_liaison_users` VALUES ('4', '17', '2');
INSERT INTO `ip_liaison_users` VALUES ('5', '29', '3');
INSERT INTO `ip_liaison_users` VALUES ('7', '31', '4');
INSERT INTO `ip_liaison_users` VALUES ('8', '32', '4');
INSERT INTO `ip_liaison_users` VALUES ('9', '7', '5');
INSERT INTO `ip_liaison_users` VALUES ('11', '24', '6');
INSERT INTO `ip_liaison_users` VALUES ('12', '25', '6');
INSERT INTO `ip_liaison_users` VALUES ('13', '19', '7');
INSERT INTO `ip_liaison_users` VALUES ('14', '18', '7');
INSERT INTO `ip_liaison_users` VALUES ('15', '20', '8');
INSERT INTO `ip_liaison_users` VALUES ('16', '21', '8');
INSERT INTO `ip_liaison_users` VALUES ('17', '26', '9');
INSERT INTO `ip_liaison_users` VALUES ('18', '193', '9');
INSERT INTO `ip_liaison_users` VALUES ('19', '22', '10');
INSERT INTO `ip_liaison_users` VALUES ('20', '61', '12');
INSERT INTO `ip_liaison_users` VALUES ('21', '66', '13');
INSERT INTO `ip_liaison_users` VALUES ('22', '69', '13');
INSERT INTO `ip_liaison_users` VALUES ('23', '1056', '14');
INSERT INTO `ip_liaison_users` VALUES ('24', '88', '15');
INSERT INTO `ip_liaison_users` VALUES ('25', '1072', '16');
INSERT INTO `ip_liaison_users` VALUES ('26', '84', '19');
INSERT INTO `ip_liaison_users` VALUES ('27', '162', '19');
INSERT INTO `ip_liaison_users` VALUES ('28', '51', '5');
INSERT INTO `ip_liaison_users` VALUES ('29', '52', '20');
INSERT INTO `ip_liaison_users` VALUES ('30', '81', '21');
INSERT INTO `ip_liaison_users` VALUES ('31', '82', '22');
INSERT INTO `ip_liaison_users` VALUES ('32', '108', '23');
INSERT INTO `ip_liaison_users` VALUES ('33', '97', '24');
INSERT INTO `ip_liaison_users` VALUES ('34', '83', '18');
INSERT INTO `ip_liaison_users` VALUES ('35', '101', '25');
INSERT INTO `ip_liaison_users` VALUES ('36', '27', '9');
INSERT INTO `ip_liaison_users` VALUES ('37', '131', '1');
INSERT INTO `ip_liaison_users` VALUES ('38', '1', '13');
INSERT INTO `ip_liaison_users` VALUES ('39', '50', '4');
INSERT INTO `ip_liaison_users` VALUES ('40', '294', '1');
INSERT INTO `ip_liaison_users` VALUES ('42', '16', '5');
INSERT INTO `ip_liaison_users` VALUES ('43', '243', '12');
INSERT INTO `ip_liaison_users` VALUES ('45', '271', '21');
INSERT INTO `ip_liaison_users` VALUES ('46', '247', '12');
INSERT INTO `ip_liaison_users` VALUES ('48', '1002', '25');
INSERT INTO `ip_liaison_users` VALUES ('49', '847', '11');
INSERT INTO `ip_liaison_users` VALUES ('50', '848', '17');
INSERT INTO `ip_liaison_users` VALUES ('52', '852', '3');
INSERT INTO `ip_liaison_users` VALUES ('53', '855', '10');
INSERT INTO `ip_liaison_users` VALUES ('54', '14', '1');
INSERT INTO `ip_liaison_users` VALUES ('55', '863', '12');
INSERT INTO `ip_liaison_users` VALUES ('56', '844', '1');
INSERT INTO `ip_liaison_users` VALUES ('57', '842', '1');
INSERT INTO `ip_liaison_users` VALUES ('58', '3', '2');
INSERT INTO `ip_liaison_users` VALUES ('59', '1051', '3');
INSERT INTO `ip_liaison_users` VALUES ('60', '179', '6');
INSERT INTO `ip_liaison_users` VALUES ('61', '22', '6');
INSERT INTO `ip_liaison_users` VALUES ('62', '186', '10');
INSERT INTO `ip_liaison_users` VALUES ('63', '1024', '14');
INSERT INTO `ip_liaison_users` VALUES ('64', '1025', '14');
INSERT INTO `ip_liaison_users` VALUES ('65', '199', '19');
INSERT INTO `ip_liaison_users` VALUES ('66', '484', '19');
INSERT INTO `ip_liaison_users` VALUES ('69', '1054', '19');
INSERT INTO `ip_liaison_users` VALUES ('71', '1058', '3');
INSERT INTO `ip_liaison_users` VALUES ('74', '1072', '16');
INSERT INTO `ip_liaison_users` VALUES ('75', '1062', '1');
INSERT INTO `ip_liaison_users` VALUES ('77', '73', '20');
