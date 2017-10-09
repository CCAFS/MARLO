SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for center_funding_sync_types
-- ----------------------------
DROP TABLE IF EXISTS `center_funding_sync_types`;
CREATE TABLE `center_funding_sync_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sync_name` text NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `sync_fs_created_fk` (`created_by`),
  KEY `sync_fs_modified_fk` (`modified_by`),
  CONSTRAINT `sync_fs_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `sync_fs_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_funding_sync_types
-- ----------------------------
INSERT INTO `center_funding_sync_types` VALUES ('1', 'OCS', '1', '3', '3', '2017-09-25 09:34:11', ' ');
INSERT INTO `center_funding_sync_types` VALUES ('2', 'MARLO CRP', '1', '3', '3', '2017-09-25 09:34:29', null);
