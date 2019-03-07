SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_srf_progress
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_srf_progress`;
CREATE TABLE `report_synthesis_srf_progress` (
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
  CONSTRAINT `report_synthesis_srf_progress_ibfk_1` FOREIGN KEY (`id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_srf_progress_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_srf_progress_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_srf_progress
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_srf_progress_targets
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_srf_progress_targets`;
CREATE TABLE `report_synthesis_srf_progress_targets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_srf_progress_id` bigint(20) DEFAULT NULL,
  `srf_slo_indicator_targets_id` bigint(20) DEFAULT NULL,
  `biref_summary` text,
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
  CONSTRAINT `report_synthesis_srf_progress_targets_ibfk_1` FOREIGN KEY (`report_synthesis_srf_progress_id`) REFERENCES `report_synthesis_srf_progress` (`id`),
  CONSTRAINT `report_synthesis_srf_progress_targets_ibfk_2` FOREIGN KEY (`srf_slo_indicator_targets_id`) REFERENCES `srf_slo_indicator_targets` (`id`),
  CONSTRAINT `report_synthesis_srf_progress_targets_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_srf_progress_targets_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_srf_progress_targets
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;

