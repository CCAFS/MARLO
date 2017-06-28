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
  KEY `regions_created_by_fk` (`created_by`) USING BTREE,
  KEY `regions_modified_by_fk` (`modified_by`) USING BTREE,
  CONSTRAINT `research_regions_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `research_regions_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of research_regions
-- ----------------------------
INSERT INTO `research_regions` VALUES ('1', 'Non specific', '1', '2016-12-07 11:13:41', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('2', 'Latin America and the Caribbean', '1', '2016-12-07 11:13:39', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('3', 'East Africa', '1', '2016-12-07 11:13:39', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('4', 'West Africa', '1', '2016-12-07 11:13:38', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('5', 'Sub- saharan Africa', '1', '2016-12-07 11:13:37', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('6', 'Southeast Asia', '1', '2016-12-07 11:13:36', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('7', 'Central Asia', '1', '2016-12-07 11:13:35', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('8', 'East Asia', '1', '2016-12-07 11:13:34', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('9', 'Other', '1', '2016-12-07 11:13:33', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('10', 'Global', '1', '2016-12-07 11:13:31', '3', '3', ' ');
INSERT INTO `research_regions` VALUES ('11', 'South Asia', '1', '2017-05-03 14:44:10', '3', '3', ' ');
