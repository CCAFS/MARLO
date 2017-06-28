SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for institution_types
-- ----------------------------
DROP TABLE IF EXISTS `institution_types`;
CREATE TABLE `institution_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `acronym` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of institution_types
-- ----------------------------
INSERT INTO `institution_types` VALUES ('1', 'Academic Institution', 'AI');
INSERT INTO `institution_types` VALUES ('2', 'Advanced Research Institution', 'ARI');
INSERT INTO `institution_types` VALUES ('3', 'CGIAR Center', 'CG');
INSERT INTO `institution_types` VALUES ('4', 'Challenge Research Program', 'CRP');
INSERT INTO `institution_types` VALUES ('5', 'Donors', 'Donors');
INSERT INTO `institution_types` VALUES ('6', 'End users', 'End_users');
INSERT INTO `institution_types` VALUES ('7', 'Government office/department', 'GO');
INSERT INTO `institution_types` VALUES ('8', 'National agricultural research and extension services', 'NARES');
INSERT INTO `institution_types` VALUES ('9', 'Non-governmental organization/Development organization', 'NGO_DO');
INSERT INTO `institution_types` VALUES ('10', 'Private Research Institution', 'PRI');
INSERT INTO `institution_types` VALUES ('11', 'Regional Organization', 'RO');
INSERT INTO `institution_types` VALUES ('12', 'Research network', 'Research_network');
INSERT INTO `institution_types` VALUES ('18', 'Other', 'Other');
