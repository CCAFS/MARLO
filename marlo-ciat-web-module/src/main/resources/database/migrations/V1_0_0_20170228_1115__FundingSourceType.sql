SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for funding_source_types
-- ----------------------------
DROP TABLE IF EXISTS `funding_source_types`;
CREATE TABLE `funding_source_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `funding_source_type_created_fk` (`created_by`),
  KEY `funding_source_type_modified_fk` (`modified_by`),
  CONSTRAINT `funding_source_type_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `funding_source_type_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of funding_source_types
-- ----------------------------
INSERT INTO `funding_source_types` VALUES ('1', 'W1/W2', '1', '2017-02-28 11:08:50', '3', '3', ' ');
INSERT INTO `funding_source_types` VALUES ('2', 'Bilateral', '1', '2017-02-28 11:09:10', '3', '3', ' ');
