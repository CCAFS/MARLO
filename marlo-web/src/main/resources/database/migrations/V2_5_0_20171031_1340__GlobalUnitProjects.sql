SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for global_unit_projects
-- ----------------------------
DROP TABLE IF EXISTS `global_unit_projects`;
CREATE TABLE `global_unit_projects` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `global_unit_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `global_unit_project_project_id_fk` (`project_id`),
  KEY `global_unit_project_global_id_fk` (`global_unit_id`),
  KEY `global_unit_project_created_id_fk` (`created_by`),
  KEY `global_unit_project_modified_id_fk` (`modified_by`),
  CONSTRAINT `global_unit_project_created_id_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `global_unit_project_global_id_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`),
  CONSTRAINT `global_unit_project_modified_id_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `global_unit_project_project_id_fk` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of global_unit_projects
-- ----------------------------
