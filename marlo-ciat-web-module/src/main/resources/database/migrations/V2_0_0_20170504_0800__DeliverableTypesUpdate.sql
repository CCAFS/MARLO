SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deliverable_types
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_types`;
CREATE TABLE `deliverable_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `deliverable_type_created_fk` (`created_by`),
  KEY `deliverable_type_modified_fk` (`modified_by`),
  CONSTRAINT `deliverable_type_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_type_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deliverable_types
-- ----------------------------
INSERT INTO `deliverable_types` VALUES ('1', 'Agricultural inputs/ Cultivars/ Lines', '1', '2017-03-13 11:29:49', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('2', 'GR Products', '1', '2017-03-13 11:30:05', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('3', 'Practices/ procedures', '1', '2017-05-04 07:41:58', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('4', 'Methods/ Scientific methodology or method', '1', '2017-05-04 07:42:12', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('5', 'Data and information outputs, including datasets, databases and models', '1', '2017-05-04 07:43:18', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('6', 'Tools and Computer Software', '1', '2017-05-04 07:43:54', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('7', 'Policy recommendations', '1', '2017-05-04 07:44:13', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('8', 'Situational and Implications analyses and quantitative and qualitative impact assessments', '1', '2017-05-04 07:45:18', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('9', 'Project designs- new researchinitiatives', '1', '2017-05-04 07:45:34', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('10', 'Reports, Reference Materials and Other Papers', '1', '2017-05-04 07:45:50', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('11', 'Peer reviewed Publications', '1', '2017-05-04 07:46:03', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('12', 'Communication Products and Multimedia', '1', '2017-05-04 07:46:57', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('13', 'Capacity building/ Capacity strengthening (external, not strategy associated to another product)', '1', '2017-05-04 07:47:08', '3', '3', ' ');
