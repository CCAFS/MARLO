SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for study_types
-- ----------------------------
DROP TABLE IF EXISTS `study_types`;
CREATE TABLE `study_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of study_types
-- ----------------------------
INSERT INTO `study_types` VALUES ('1', 'Outcome Case Study');
INSERT INTO `study_types` VALUES ('2', 'Impact Case study');
INSERT INTO `study_types` VALUES ('3', 'Impact assessment');
INSERT INTO `study_types` VALUES ('4', 'Adoption Study');
INSERT INTO `study_types` VALUES ('5', 'CRP/PTF Commissioned -Evaluation');
INSERT INTO `study_types` VALUES ('6', 'Evaluation');
INSERT INTO `study_types` VALUES ('7', 'Review');
INSERT INTO `study_types` VALUES ('8', 'Learning');
INSERT INTO `study_types` VALUES ('9', 'Other');
