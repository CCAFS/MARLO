SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_key_partnership_pmu
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_key_partnership_pmu`;
CREATE TABLE `report_synthesis_key_partnership_pmu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_key_partnership_id` bigint(20) DEFAULT NULL,
  `report_synthesis_key_partnership_external_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_key_partnership_id` (`report_synthesis_key_partnership_id`),
  KEY `report_synthesis_key_partnership_external_id` (`report_synthesis_key_partnership_external_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_key_partnership_pmu_ibfk_1` FOREIGN KEY (`report_synthesis_key_partnership_id`) REFERENCES `report_synthesis_key_partnerships` (`id`),
  CONSTRAINT `report_synthesis_key_partnership_pmu_ibfk_2` FOREIGN KEY (`report_synthesis_key_partnership_external_id`) REFERENCES `report_synthesis_key_partnership_external` (`id`),
  CONSTRAINT `report_synthesis_key_partnership_pmu_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_key_partnership_pmu_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_key_partnership_pmu
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
