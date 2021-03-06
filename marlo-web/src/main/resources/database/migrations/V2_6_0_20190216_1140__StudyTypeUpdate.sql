UPDATE project_expected_study_info set study_type_id = 1 where study_type_id = 2;
UPDATE project_expected_study_info set study_type_id = 5 where study_type_id = 6;
UPDATE project_expected_study_info set study_type_id = 9 where study_type_id = 8;


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
INSERT INTO `study_types` VALUES ('2', 'Impact Case study');
INSERT INTO `study_types` VALUES ('3', 'EPIA: Ex-post Impact assessment (at scale)');
INSERT INTO `study_types` VALUES ('4', 'Adoption study: Ex-post adoption survey (at scale)');
INSERT INTO `study_types` VALUES ('5', 'Program evaluation (including project evaluations)');
INSERT INTO `study_types` VALUES ('6', '6. Synthesis: reviews, systematic reviews, evidence gap maps');
INSERT INTO `study_types` VALUES ('7', 'Synthesis: reviews, systematic reviews, evidence gap maps');
INSERT INTO `study_types` VALUES ('8', 'Learning');
INSERT INTO `study_types` VALUES ('9', 'Other');

SET FOREIGN_KEY_CHECKS=1;