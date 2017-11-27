
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for center_all_types
-- ----------------------------
DROP TABLE IF EXISTS `center_all_types`;
CREATE TABLE `center_all_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `class_name` varchar(45) NOT NULL COMMENT 'The name of the class where this type belongs. e.g ResearchArea, ResearchProgram.',
  `type_name` varchar(100) NOT NULL COMMENT 'The type of the object or record. e.g research area leader, program coordinator, external partiner, internal partiner, research objective, strategic objective.',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='The table that tracks all types of entity data in the system';

-- ----------------------------
-- Records of center_all_types
-- ----------------------------
INSERT INTO `center_all_types` VALUES ('1', 'ResearchProgram', 'REGIONAL_PROGRAM_TYPE');
INSERT INTO `center_all_types` VALUES ('2', 'ResearchProgram', 'FLAGSHIP_PROGRAM_TYPE');
INSERT INTO `center_all_types` VALUES ('4', 'ResearchLeader', 'Program Coordination');
INSERT INTO `center_all_types` VALUES ('5', 'ResearchLeader', 'Research Area Leader');
INSERT INTO `center_all_types` VALUES ('6', 'ResearchLeader', 'Research Program Leader');
INSERT INTO `center_all_types` VALUES ('7', 'ResearchObjective', 'Strategic Objective');
INSERT INTO `center_all_types` VALUES ('8', 'ResearchObjective', 'Research Area Objective');
INSERT INTO `center_all_types` VALUES ('9', 'ResearchPartner', 'Internal Partner');
INSERT INTO `center_all_types` VALUES ('10', 'ResearchPartner', 'External Partner');
INSERT INTO `center_all_types` VALUES ('11', 'ResearchLeader', 'Scientist leader');