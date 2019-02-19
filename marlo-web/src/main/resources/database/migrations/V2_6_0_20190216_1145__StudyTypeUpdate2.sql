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
INSERT INTO `study_types` VALUES ('1', 'OICR: Outcome Impact Case Report');
INSERT INTO `study_types` VALUES ('3', 'EPIA: Ex-post Impact assessment (at scale)');
INSERT INTO `study_types` VALUES ('4', 'Adoption study: Ex-post adoption survey (at scale)');
INSERT INTO `study_types` VALUES ('5', 'Program evaluation (including project evaluations)');
INSERT INTO `study_types` VALUES ('7', 'Synthesis: reviews, systematic reviews, evidence gap maps');
INSERT INTO `study_types` VALUES ('9', 'Other');
SET FOREIGN_KEY_CHECKS=1;

ALTER TABLE `project_expected_study_info`
ADD COLUMN `other_study_type`  text NULL AFTER `study_type_id`;


