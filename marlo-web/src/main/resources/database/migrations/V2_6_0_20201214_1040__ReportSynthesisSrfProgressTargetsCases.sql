CREATE TABLE IF NOT EXISTS `report_synthesis_srf_progress_targets_cases` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_srf_progress_id` bigint(20) DEFAULT NULL,
  `srf_slo_indicator_targets_id` bigint(20) DEFAULT NULL,
  `brief_summary` text,
  `additional_contribution` text,  
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_srf_progress_id` (`report_synthesis_srf_progress_id`),
  KEY `srf_slo_indicator_targets_id` (`srf_slo_indicator_targets_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_srf_progress_targets_cases_ibfk_1` FOREIGN KEY (`report_synthesis_srf_progress_id`) REFERENCES `report_synthesis_srf_progress` (`id`),
  CONSTRAINT `report_synthesis_srf_progress_targets_cases_ibfk_2` FOREIGN KEY (`srf_slo_indicator_targets_id`) REFERENCES `srf_slo_indicator_targets` (`id`),
  CONSTRAINT `report_synthesis_srf_progress_targets_cases_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_srf_progress_targets_cases_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
);


CREATE TABLE IF NOT EXISTS `report_synthesis_srf_progress_targets_cases_locations`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_srf_progress_targets_case_id` bigint(20) NOT NULL,
  `loc_element_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_srf_progress_targets_case_id` (`report_synthesis_srf_progress_targets_case_id`) USING BTREE,
  KEY `loc_element_id` (`loc_element_id`) USING BTREE,
  KEY `fk_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_modified_by_users_id` (`modified_by`) USING BTREE,
  CONSTRAINT `report_synthesis_srf_progress_targets_cases_locfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_srf_progress_targets_cases_locfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_srf_progress_targets_cases_locfk_3` FOREIGN KEY (`report_synthesis_srf_progress_targets_case_id`) REFERENCES `report_synthesis_srf_progress_targets_cases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `report_synthesis_srf_progress_targets_cases_locfk_4` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)  DEFAULT CHARSET=utf8;