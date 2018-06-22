SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_melia
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_melia`;
CREATE TABLE `report_synthesis_melia` (
  `id` bigint(20) NOT NULL,
  `summary` text,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_melia_ibfk_1` FOREIGN KEY (`id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_melia_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_melia_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for report_synthesis_melia_evaluations
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_melia_evaluations`;
CREATE TABLE `report_synthesis_melia_evaluations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synhtesis_melia_id` bigint(20) DEFAULT NULL,
  `name_evaluation` text,
  `recommendation` text,
  `management_response` text,
  `status` decimal(2,0) DEFAULT NULL,
  `whom` text,
  `when` text,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `report_synhtesis_melia_id` (`report_synhtesis_melia_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_melia_evaluations_ibfk_1` FOREIGN KEY (`report_synhtesis_melia_id`) REFERENCES `report_synthesis_melia` (`id`),
  CONSTRAINT `report_synthesis_melia_evaluations_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_melia_evaluations_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for report_synthesis_melia_studies
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_melia_studies`;
CREATE TABLE `report_synthesis_melia_studies` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synhtesis_melia_id` bigint(20) DEFAULT NULL,
  `project_expected_studies_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `report_synhtesis_melia_id` (`report_synhtesis_melia_id`),
  KEY `project_expected_studies_id` (`project_expected_studies_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_melia_studies_ibfk_1` FOREIGN KEY (`report_synhtesis_melia_id`) REFERENCES `report_synthesis_melia` (`id`),
  CONSTRAINT `report_synthesis_melia_studies_ibfk_2` FOREIGN KEY (`project_expected_studies_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `report_synthesis_melia_studies_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_melia_studies_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
