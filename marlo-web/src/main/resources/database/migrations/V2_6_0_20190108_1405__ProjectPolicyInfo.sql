SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_policy_info
-- ----------------------------
DROP TABLE IF EXISTS `project_policy_info`;
CREATE TABLE `project_policy_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_policy_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `year` bigint(5) DEFAULT NULL,
  `title` text,
  `rep_ind_policy_investment_type_id` bigint(20) DEFAULT NULL,
  `amount` double(30,2) DEFAULT NULL,
  `rep_ind_organization_type_id` bigint(20) DEFAULT NULL,
  `rep_ind_stage_process_id` bigint(20) DEFAULT NULL,
  `project_expected_study_id` bigint(20) DEFAULT NULL,
  `rep_ind_geographic_scope_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_policy_id` (`project_policy_id`),
  KEY `id_phase` (`id_phase`),
  KEY `rep_ind_policy_investment_type_id` (`rep_ind_policy_investment_type_id`),
  KEY `rep_ind_organization_type_id` (`rep_ind_organization_type_id`),
  KEY `rep_ind_stage_process_id` (`rep_ind_stage_process_id`),
  KEY `project_expected_study_id` (`project_expected_study_id`),
  KEY `rep_ind_geographic_scope_id` (`rep_ind_geographic_scope_id`),
  CONSTRAINT `project_policy_info_ibfk_1` FOREIGN KEY (`project_policy_id`) REFERENCES `project_policies` (`id`),
  CONSTRAINT `project_policy_info_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
  CONSTRAINT `project_policy_info_ibfk_3` FOREIGN KEY (`rep_ind_policy_investment_type_id`) REFERENCES `rep_ind_policy_investiment_types` (`id`),
  CONSTRAINT `project_policy_info_ibfk_4` FOREIGN KEY (`rep_ind_organization_type_id`) REFERENCES `rep_ind_organization_types` (`id`),
  CONSTRAINT `project_policy_info_ibfk_5` FOREIGN KEY (`rep_ind_stage_process_id`) REFERENCES `rep_ind_stage_process` (`id`),
  CONSTRAINT `project_policy_info_ibfk_6` FOREIGN KEY (`project_expected_study_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_policy_info_ibfk_7` FOREIGN KEY (`rep_ind_geographic_scope_id`) REFERENCES `rep_ind_geographic_scopes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_policy_info
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
