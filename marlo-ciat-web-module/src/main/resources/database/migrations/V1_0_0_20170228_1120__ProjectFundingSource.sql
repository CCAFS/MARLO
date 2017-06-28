SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_funding_sources
-- ----------------------------
DROP TABLE IF EXISTS `project_funding_sources`;
CREATE TABLE `project_funding_sources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `funding_source_type_id` int(11) NOT NULL,
  `donor` text NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_funding_project_id_fk` (`project_id`),
  KEY `project_funding_funding_type_id_fk` (`funding_source_type_id`),
  KEY `project_funding_created_id_fk` (`created_by`),
  KEY `project_funding_modified_id_fk` (`modified_by`),
  CONSTRAINT `project_funding_created_id_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_funding_funding_type_id_fk` FOREIGN KEY (`funding_source_type_id`) REFERENCES `funding_source_types` (`id`),
  CONSTRAINT `project_funding_modified_id_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_funding_project_id_fk` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

