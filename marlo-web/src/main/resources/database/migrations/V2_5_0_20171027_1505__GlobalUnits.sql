SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for global_units
-- ----------------------------
DROP TABLE IF EXISTS `global_units`;
CREATE TABLE `global_units` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `global_unit_type_id` bigint(20) NOT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of global_units
-- ----------------------------
INSERT INTO `global_units` VALUES ('1', '1', 'Climate Change, Agriculture and Food Security', 'CCAFS', '1', '3', '3', '2017-10-27 09:55:52', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('3', '1', 'Policies, Institutions, and Markets', 'PIM', '1', '3', '3', '2017-10-27 10:23:28', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('4', '1', 'Water, Land and Ecosystems', 'WLE', '1', '3', '3', '2017-10-27 10:23:29', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('5', '1', 'Agriculture for Nutrition and Health ', 'A4NH', '1', '3', '3', '2017-10-27 10:23:32', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('7', '1', 'Livestock', 'Livestock', '1', '3', '3', '2017-10-27 10:23:33', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('8', '1', 'Aquatic Agricultural Systems', ' ', '1', '3', '3', '2017-10-27 10:23:36', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('9', '1', 'Dryland Cereals', ' ', '1', '3', '3', '2017-10-27 10:23:37', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('10', '1', 'Dryland Systems', ' ', '1', '3', '3', '2017-10-27 10:23:38', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('11', '1', 'Forests, Trees and Agroforestry', 'FTA', '1', '3', '3', '2017-10-27 10:23:42', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('12', '1', 'Grain Legumes', ' ', '1', '3', '3', '2017-10-27 10:24:05', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('13', '1', 'Integrated Systems for the Humid Tropics', ' ', '1', '3', '3', '2017-10-27 10:24:07', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('16', '1', 'Rice', 'rice_grisp', '1', '3', '3', '2017-10-27 10:24:12', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('17', '1', 'Roots, Tubers and Bananas', 'RTB', '1', '3', '3', '2017-10-27 10:24:13', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('20', '1', 'Genebank', ' ', '1', '3', '3', '2017-10-27 10:24:17', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('21', '1', 'Wheat', 'Wheat', '1', '3', '3', '2017-10-27 10:24:18', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('22', '1', 'Maize', 'Maize', '1', '3', '3', '2017-10-27 10:24:25', ' ', '0', '0');
INSERT INTO `global_units` VALUES ('23', '2', 'Centro Internacional de Agricultura Tropical', 'CIAT', '1', '3', '3', '2017-10-27 10:24:26', ' ', '1', '1');
INSERT INTO `global_units` VALUES ('24', '3', 'Platform for Big Data in Agriculture', 'BigData', '1', '3', '3', '2017-10-27 10:24:27', ' ', '1', '0');
INSERT INTO `global_units` VALUES ('25', '3', 'Excellence in Breeading Platform', 'breeding', '1', '3', '3', '2017-10-27 10:24:33', ' ', '1', '0');
INSERT INTO `global_units` VALUES ('26', '3', 'Genebank Platform', 'genebank', '1', '3', '3', '2017-10-27 10:24:34', ' ', '0', '0');
