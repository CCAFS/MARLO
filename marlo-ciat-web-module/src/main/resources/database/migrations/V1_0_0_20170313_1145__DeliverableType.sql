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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deliverable_types
-- ----------------------------
INSERT INTO `deliverable_types` VALUES ('1', 'Report', '1', '2017-03-13 11:29:49', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('2', 'Publication', '1', '2017-03-13 11:30:05', '3', '3', ' ');
