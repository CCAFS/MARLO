CREATE TABLE `project_expected_study_impact_area_indicators` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `impact_area_indicator_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`) USING BTREE,
  KEY `impact_area_indicator_id` (`impact_area_indicator_id`) USING BTREE,
  KEY `id_phase` (`id_phase`) USING BTREE,
  CONSTRAINT `expected_study_imp_area_ind_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `expected_study_imp_area_ind_ibfk_2` FOREIGN KEY (`impact_area_indicator_id`) REFERENCES `st_impact_areas_indicators` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `expected_study_imp_area_ind_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `project_expected_study_outcome_indicators` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `outcome_indicator_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`) USING BTREE,
  KEY `outcome_indicator_id` (`outcome_indicator_id`) USING BTREE,
  KEY `id_phase` (`id_phase`) USING BTREE,
  CONSTRAINT `expected_study_act_area_out_ind_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `expected_study_act_area_out_ind_ibfk_2` FOREIGN KEY (`outcome_indicator_id`) REFERENCES `st_action_area_outcome_indicators` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `expected_study_act_area_out_ind_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `project_expected_study_initiatives` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `initiative_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`),
  KEY `initiative_id` (`initiative_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_initiatives_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_initiatives_ibfk_2` FOREIGN KEY (`initiative_id`) REFERENCES `global_units` (`id`),
  CONSTRAINT `project_expected_study_initiatives_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `project_expected_study_funding_sources` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `funding_source_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`),
  KEY `funding_source_id` (`funding_source_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_funding_sources_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_funding_sources_ibfk_2` FOREIGN KEY (`funding_source_id`) REFERENCES `funding_sources` (`id`),
  CONSTRAINT `project_expected_study_funding_sources_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;