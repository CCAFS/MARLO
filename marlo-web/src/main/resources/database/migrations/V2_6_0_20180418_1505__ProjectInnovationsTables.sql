SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_innovations
-- ----------------------------
DROP TABLE IF EXISTS `project_innovations`;
CREATE TABLE `project_innovations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `project_innovations_project_id_fk` (`project_id`),
  KEY `project_innovations_created_id_fk` (`created_by`),
  KEY `project_innovations_modified_id_fk` (`modified_by`),
  CONSTRAINT `project_innovations_created_id_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_innovations_modified_id_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_innovations_project_id_fk` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for project_innovation_info
-- ----------------------------
DROP TABLE IF EXISTS `project_innovation_info`;
CREATE TABLE `project_innovation_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_innovation_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `title` text,
  `narrative` text,
  `phase_research_id` bigint(20) DEFAULT NULL,
  `stage_innovation_id` bigint(20) DEFAULT NULL,
  `geographic_scope_id` bigint(20) DEFAULT NULL,
  `innovation_type_id` bigint(20) DEFAULT NULL,
  `rep_ind_region_id` bigint(20) DEFAULT NULL,
  `novel` text,
  `evidence_link` text,
  `gender_focus_level_id` bigint(20) DEFAULT NULL,
  `gender_explaniation` text,
  `youth_focus_level_id` bigint(20) DEFAULT NULL,
  `youth_explaniation` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
