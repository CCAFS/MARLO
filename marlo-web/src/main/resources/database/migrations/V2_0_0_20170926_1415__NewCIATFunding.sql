SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for center_project_funding_sources
-- ----------------------------
DROP TABLE IF EXISTS `center_project_funding_sources`;
CREATE TABLE `center_project_funding_sources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `funding_source_type_id` int(11) NOT NULL,
  `funding_source_sync_type_id` bigint(20) DEFAULT NULL,
  `code` text,
  `crp_id` bigint(20) DEFAULT NULL,
  `title` text,
  `description` text,
  `start_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `end_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `extension_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `original_donor` text,
  `direct_donor` text,
  `total_amount` double(30,2) DEFAULT NULL,
  `sync` tinyint(1) DEFAULT NULL,
  `sync_date` date DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_funding_project_id_fk` (`project_id`) USING BTREE,
  KEY `project_funding_funding_type_id_fk` (`funding_source_type_id`) USING BTREE,
  KEY `project_funding_created_id_fk` (`created_by`) USING BTREE,
  KEY `project_funding_modified_id_fk` (`modified_by`) USING BTREE,
  KEY `project_funding_crp_fk` (`crp_id`) USING BTREE,
  KEY `center_project_funding_sources_ibfk_6` (`funding_source_sync_type_id`),
  CONSTRAINT `center_project_funding_sources_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_project_funding_sources_ibfk_2` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`),
  CONSTRAINT `center_project_funding_sources_ibfk_3` FOREIGN KEY (`funding_source_type_id`) REFERENCES `center_funding_source_types` (`id`),
  CONSTRAINT `center_project_funding_sources_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_project_funding_sources_ibfk_5` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`),
  CONSTRAINT `center_project_funding_sources_ibfk_6` FOREIGN KEY (`funding_source_sync_type_id`) REFERENCES `center_funding_sync_types` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_project_funding_sources
-- ----------------------------
