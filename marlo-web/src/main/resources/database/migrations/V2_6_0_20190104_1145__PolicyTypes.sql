SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rep_ind_policy_types
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_policy_types`;
CREATE TABLE `rep_ind_policy_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_policy_types
-- ----------------------------
INSERT INTO `rep_ind_policy_types` VALUES ('1', 'Funder');
INSERT INTO `rep_ind_policy_types` VALUES ('2', 'Public Sector');
INSERT INTO `rep_ind_policy_types` VALUES ('3', 'Private Sector');
INSERT INTO `rep_ind_policy_types` VALUES ('4', 'Other : Please specify');
SET FOREIGN_KEY_CHECKS=1;
