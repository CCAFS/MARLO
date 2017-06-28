SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for research_regions
-- ----------------------------
DROP TABLE IF EXISTS `research_regions`;
CREATE TABLE `research_regions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `regions_created_by_fk` (`created_by`),
  KEY `regions_modified_by_fk` (`modified_by`),
  CONSTRAINT `regions_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `regions_modified_by_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of research_regions
-- ----------------------------
INSERT INTO `research_regions` VALUES ('-1', 'Non specific', '1', '2016-11-24 11:21:19', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('1', 'Latin America and the Caribbean', '1', '2016-11-24 11:17:35', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('2', 'East Africa', '1', '2016-11-24 11:17:48', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('3', 'West Africa', '1', '2016-11-24 11:18:01', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('4', 'Sub- saharan Africa', '1', '2016-11-24 11:18:28', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('5', 'Southeast Asia', '1', '2016-11-24 11:18:44', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('6', 'Central Asia', '1', '2016-11-24 11:18:53', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('7', 'East Asia', '1', '2016-11-24 11:19:04', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('8', 'Other', '1', '2016-11-24 11:20:44', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('9', 'Global', '1', '2016-11-24 11:21:03', '3', '3', ' ');
