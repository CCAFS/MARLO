SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_innovation_info
-- ----------------------------
DROP TABLE IF EXISTS `project_innovation_info`;
CREATE TABLE `project_innovation_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_innovation_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `year` bigint(5) DEFAULT NULL,
  `title` text,
  `narrative` text,
  `phase_research_id` bigint(20) DEFAULT NULL,
  `stage_innovation_id` bigint(20) DEFAULT NULL,
  `geographic_scope_id` bigint(20) DEFAULT NULL,
  `innovation_type_id` bigint(20) DEFAULT NULL,
  `rep_ind_region_id` bigint(20) DEFAULT NULL,
  `project_expected_studies_id` bigint(20) DEFAULT NULL,
  `novel` text,
  `evidence_link` text,
  `gender_focus_level_id` bigint(20) DEFAULT NULL,
  `gender_explaniation` text,
  `youth_focus_level_id` bigint(20) DEFAULT NULL,
  `youth_explaniation` text,
  PRIMARY KEY (`id`),
  KEY `project_innovation_id` (`project_innovation_id`),
  KEY `id_phase` (`id_phase`),
  KEY `phase_research_id` (`phase_research_id`),
  KEY `stage_innovation_id` (`stage_innovation_id`),
  KEY `geographic_scope_id` (`geographic_scope_id`),
  KEY `innovation_type_id` (`innovation_type_id`),
  KEY `rep_ind_region_id` (`rep_ind_region_id`),
  KEY `project_expected_studies_id` (`project_expected_studies_id`),
  KEY `gender_focus_level_id` (`gender_focus_level_id`),
  KEY `youth_focus_level_id` (`youth_focus_level_id`),
  CONSTRAINT `project_innovation_info_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`),
  CONSTRAINT `project_innovation_info_ibfk_10` FOREIGN KEY (`youth_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`),
  CONSTRAINT `project_innovation_info_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
  CONSTRAINT `project_innovation_info_ibfk_3` FOREIGN KEY (`phase_research_id`) REFERENCES `rep_ind_phase_research_partnerships` (`id`),
  CONSTRAINT `project_innovation_info_ibfk_4` FOREIGN KEY (`stage_innovation_id`) REFERENCES `rep_ind_stage_innovations` (`id`),
  CONSTRAINT `project_innovation_info_ibfk_5` FOREIGN KEY (`geographic_scope_id`) REFERENCES `rep_ind_geographic_scopes` (`id`),
  CONSTRAINT `project_innovation_info_ibfk_6` FOREIGN KEY (`innovation_type_id`) REFERENCES `rep_ind_innovation_types` (`id`),
  CONSTRAINT `project_innovation_info_ibfk_7` FOREIGN KEY (`rep_ind_region_id`) REFERENCES `rep_ind_regions` (`id`),
  CONSTRAINT `project_innovation_info_ibfk_8` FOREIGN KEY (`project_expected_studies_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_innovation_info_ibfk_9` FOREIGN KEY (`gender_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_innovation_info
-- ----------------------------
