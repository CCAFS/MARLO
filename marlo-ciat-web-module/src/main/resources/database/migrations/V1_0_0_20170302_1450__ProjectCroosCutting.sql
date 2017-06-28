SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_crosscuting_themes
-- ----------------------------
DROP TABLE IF EXISTS `project_crosscuting_themes`;
CREATE TABLE `project_crosscuting_themes` (
  `id` bigint(20) NOT NULL,
  `climate_change` tinyint(1) DEFAULT NULL,
  `gender_youth` tinyint(1) DEFAULT NULL,
  `policies_institutions` tinyint(1) DEFAULT NULL,
  `capacity_development` tinyint(1) DEFAULT NULL,
  `big_data` tinyint(1) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` time DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_cc_theme_created_fk` (`created_by`),
  KEY `project_cc_theme_modified_fk` (`modified_by`),
  CONSTRAINT `project_cc_theme_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_cc_theme_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_cc_theme_project_id_fk` FOREIGN KEY (`id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
