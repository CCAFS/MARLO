SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` text NOT NULL,
  `acronym` varchar(50) NOT NULL,
  `crp_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `crp_id` (`crp_id`) USING BTREE,
  CONSTRAINT `roles_ibfk_1` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES ('1', 'CCFAS Admin', 'Admin', '1');
INSERT INTO `roles` VALUES ('2', 'Management Liaison', 'ML', '1');
INSERT INTO `roles` VALUES ('4', 'Contact point', 'CP', '1');
INSERT INTO `roles` VALUES ('7', 'Project leader', 'PL', '1');
INSERT INTO `roles` VALUES ('8', 'Guest', 'G', '1');
INSERT INTO `roles` VALUES ('9', 'Project coordinator', 'PC', '1');
INSERT INTO `roles` VALUES ('10', 'Finance person', 'FM', '1');
INSERT INTO `roles` VALUES ('11', 'Regional Program Leaders', 'RPL', '1');
INSERT INTO `roles` VALUES ('12', 'Flagship Leaders', 'FPL', '1');
INSERT INTO `roles` VALUES ('13', 'External Evaluator', 'E', '1');
INSERT INTO `roles` VALUES ('14', 'Program Management Unit', 'PMU', '1');
INSERT INTO `roles` VALUES ('15', 'Site Integration Leader', 'SL', '1');
INSERT INTO `roles` VALUES ('16', 'Data Manager', 'DM', '1');
INSERT INTO `roles` VALUES ('17', 'Super Admin', 'SuperAdmin', '1');
INSERT INTO `roles` VALUES ('18', 'Cluster Leader', 'CL', '1');
INSERT INTO `roles` VALUES ('19', 'Admin(A4NH)', 'Admin', '5');
INSERT INTO `roles` VALUES ('20', 'Program Management Unit', 'PMU', '5');
INSERT INTO `roles` VALUES ('21', 'Regional Program Leaders', 'RPL', '5');
INSERT INTO `roles` VALUES ('22', 'Flagship Leaders', 'FPL', '5');
INSERT INTO `roles` VALUES ('23', 'Site Integration Leader', 'SL', '5');
INSERT INTO `roles` VALUES ('24', 'Cluster Leader', 'CL', '5');
INSERT INTO `roles` VALUES ('26', 'Admin(PIM)', 'Admin', '3');
INSERT INTO `roles` VALUES ('27', 'Program Management Unit', 'PMU', '3');
INSERT INTO `roles` VALUES ('28', 'Regional Program Leaders', 'RPL', '3');
INSERT INTO `roles` VALUES ('29', 'Flagship Leaders', 'FPL', '3');
INSERT INTO `roles` VALUES ('30', 'Site Integration Leader', 'SL', '3');
INSERT INTO `roles` VALUES ('31', 'Cluster Leader', 'CL', '3');
INSERT INTO `roles` VALUES ('32', 'Admin(WLE)', 'Admin', '4');
INSERT INTO `roles` VALUES ('33', 'Program Management Unit', 'PMU', '4');
INSERT INTO `roles` VALUES ('34', 'Regional Program Leaders', 'RPL', '4');
INSERT INTO `roles` VALUES ('35', 'Flagship Leaders', 'FPL', '4');
INSERT INTO `roles` VALUES ('36', 'Site Integration Leader', 'SL', '4');
INSERT INTO `roles` VALUES ('37', 'Cluster Leader', 'CL', '4');
INSERT INTO `roles` VALUES ('38', 'Program Management Unit', 'PMU', '7');
INSERT INTO `roles` VALUES ('39', 'Regional Program Leaders', 'RPL', '7');
INSERT INTO `roles` VALUES ('40', 'Flagship Leaders', 'FPL', '7');
INSERT INTO `roles` VALUES ('41', 'Site Integration Leader', 'SL', '7');
INSERT INTO `roles` VALUES ('42', 'Cluster Leader', 'CL', '7');
INSERT INTO `roles` VALUES ('43', 'Admin(Livesotckfish)', 'Admin', '7');
INSERT INTO `roles` VALUES ('48', 'Management Liaison', 'ML', '3');
INSERT INTO `roles` VALUES ('49', 'Management Liaison', 'ML', '4');
INSERT INTO `roles` VALUES ('50', 'Management Liaison', 'ML', '5');
INSERT INTO `roles` VALUES ('51', 'Management Liaison', 'ML', '7');
