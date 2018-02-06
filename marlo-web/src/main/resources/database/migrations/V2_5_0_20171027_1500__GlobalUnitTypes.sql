SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for global_unit_types
-- ----------------------------
DROP TABLE IF EXISTS `global_unit_types`;
CREATE TABLE `global_unit_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `gu_type_created_fk` (`created_by`),
  KEY `gu_type_modified_fk` (`modified_by`),
  CONSTRAINT `gu_type_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `gu_type_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of global_unit_types
-- ----------------------------
INSERT INTO `global_unit_types` VALUES ('1', 'CRP', '1', '2017-10-27 08:59:12', '3', '3', ' ');
INSERT INTO `global_unit_types` VALUES ('2', 'Center', '2', '2017-10-27 08:59:12', '3', '3', ' ');
INSERT INTO `global_unit_types` VALUES ('3', 'Platform', '3', '2017-10-27 08:59:12', '3', '3', ' ');
