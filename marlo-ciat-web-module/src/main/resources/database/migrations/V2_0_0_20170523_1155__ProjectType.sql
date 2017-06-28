SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_types
-- ----------------------------
DROP TABLE IF EXISTS `project_types`;
CREATE TABLE `project_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `is_active` tinyint(1) NOT NULL,
  `active_sice` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_type_created_fk` (`created_by`),
  KEY `project_type_modified_fk` (`modified_by`),
  CONSTRAINT `project_type_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_type_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_types
-- ----------------------------
INSERT INTO `project_types` VALUES ('1', 'W1/W2', '1', '2017-02-28 11:08:50', '3', '3', ' ');
INSERT INTO `project_types` VALUES ('2', 'Bilateral', '1', '2017-02-28 11:09:10', '3', '3', ' ');
INSERT INTO `project_types` VALUES ('3', 'W3', '1', '2017-05-15 09:38:28', '3', '3', ' ');
