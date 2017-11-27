ALTER TABLE `center_project_funding_sources`
MODIFY COLUMN `funding_source_type_id`  int(11) NULL AFTER `project_id`;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for center_funding_source_types
-- ----------------------------
DROP TABLE IF EXISTS `center_funding_source_types`;
CREATE TABLE `center_funding_source_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `funding_source_type_created_fk` (`created_by`) USING BTREE,
  KEY `funding_source_type_modified_fk` (`modified_by`) USING BTREE,
  CONSTRAINT `center_funding_source_types_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_funding_source_types_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_funding_source_types
-- ----------------------------
INSERT INTO `center_funding_source_types` VALUES ('1', 'W1/W2', '1', '2017-02-28 11:08:50', '3', '3', ' ');
INSERT INTO `center_funding_source_types` VALUES ('2', 'Bilateral', '1', '2017-10-04 09:26:49', '3', '3', ' ');
INSERT INTO `center_funding_source_types` VALUES ('3', 'W3', '1', '2017-10-04 09:26:51', '3', '3', ' ');