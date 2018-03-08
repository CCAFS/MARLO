SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for global_units
-- ----------------------------
DROP TABLE IF EXISTS `global_units`;
CREATE TABLE `global_units` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `global_unit_type_id` bigint(20) NOT NULL,
  `smo_code` text,
  `name` text NOT NULL,
  `acronym` varchar(50) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `modification_justification` text,
  `is_marlo` tinyint(1) NOT NULL,
  `login` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `gu_type_fk` (`global_unit_type_id`),
  KEY `gu_created_fk` (`created_by`),
  KEY `gu_modified_fk` (`modified_by`),
  CONSTRAINT `gu_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `gu_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `gu_type_fk` FOREIGN KEY (`global_unit_type_id`) REFERENCES `global_unit_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of global_units
-- ----------------------------
INSERT INTO `global_units` VALUES ('1', '1', 'CRP22', 'Climate Change, Agriculture and Food Security', 'CCAFS', '1', '3', '3', '2018-02-28 07:42:58', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('3', '1', 'CRP23', 'Policies, Institutions, and Markets', 'PIM', '1', '3', '3', '2018-02-28 07:43:06', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('4', '1', 'CRP24', 'Water, Land and Ecosystems', 'WLE', '1', '3', '3', '2018-02-28 07:43:57', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('5', '1', 'CRP21', 'Agriculture for Nutrition and Health ', 'A4NH', '1', '3', '3', '2018-02-28 07:42:42', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('7', '1', 'CRP13', 'Livestock', 'Livestock', '1', '3', '3', '2018-02-28 07:36:50', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('8', '1', null, 'Aquatic Agricultural Systems', ' ', '0', '3', '3', '2018-02-28 07:44:59', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('9', '1', null, 'Dryland Cereals', ' ', '0', '3', '3', '2018-02-28 07:45:02', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('10', '1', null, 'Dryland Systems', ' ', '0', '3', '3', '2018-02-28 07:45:02', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('11', '1', null, 'Forests, Trees and Agroforestry', 'FTA', '1', '3', '3', '2017-10-27 10:23:42', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('12', '1', null, 'Grain Legumes', ' ', '0', '3', '3', '2018-02-28 07:47:39', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('13', '1', null, 'Integrated Systems for the Humid Tropics', ' ', '0', '3', '3', '2018-02-28 07:47:42', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('16', '1', 'CRP15', 'Rice', 'rice_grisp', '1', '3', '3', '2018-02-28 07:37:42', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('17', '1', 'CRP16', 'Roots, Tubers and Bananas', 'RTB', '1', '3', '3', '2018-02-28 07:42:04', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('20', '1', null, 'Genebank', ' ', '0', '3', '3', '2018-02-28 07:48:03', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('21', '1', 'CRP17', 'Wheat', 'Wheat', '1', '3', '3', '2018-02-28 07:42:29', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('22', '1', 'CRP14', 'Maize', 'Maize', '1', '3', '3', '2018-02-28 07:37:32', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('23', '2', null, 'Centro Internacional de Agricultura Tropical', 'CIAT', '1', '3', '3', '2017-10-27 10:24:26', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('24', '3', 'PTF32', 'Platform for Big Data in Agriculture', 'BigData', '1', '3', '3', '2018-02-28 07:44:40', ' ', '1', '0');
INSERT INTO `global_units` VALUES ('25', '3', 'PTF31', 'Excellence in Breeading Platform', 'EiB', '1', '3', '3', '2018-02-28 07:44:08', ' ', '1', '0');
INSERT INTO `global_units` VALUES ('26', '3', 'PTF33', 'Genebank Platform', 'Genebank', '1', '3', '3', '2018-02-28 07:44:40', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('27', '1', 'CRP12', 'Fish', 'Fish', '1', '3', '3', '2018-02-28 07:36:22', ' ', '0', '0');
