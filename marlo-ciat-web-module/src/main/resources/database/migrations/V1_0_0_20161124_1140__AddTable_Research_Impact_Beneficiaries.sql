SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for research_impact_beneficiaries
-- ----------------------------
DROP TABLE IF EXISTS `research_impact_beneficiaries`;
CREATE TABLE `research_impact_beneficiaries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `impact_id` int(11) DEFAULT NULL,
  `beneficiary_id` int(11) NOT NULL,
  `research_region_id` int(11) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `impact_beneficiary_beneficiary_id` (`beneficiary_id`),
  KEY `impact_beneficiary_region` (`research_region_id`),
  KEY `impact_beneficiary_created_by` (`created_by`),
  KEY `impact_beneficiary_modified_by` (`modified_by`),
  KEY `impact_beneficiary_impact_fk` (`impact_id`),
  CONSTRAINT `impact_beneficiary_beneficiary_id` FOREIGN KEY (`beneficiary_id`) REFERENCES `beneficiaries` (`id`),
  CONSTRAINT `impact_beneficiary_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `impact_beneficiary_impact_fk` FOREIGN KEY (`impact_id`) REFERENCES `research_impacts` (`id`),
  CONSTRAINT `impact_beneficiary_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `impact_beneficiary_region` FOREIGN KEY (`research_region_id`) REFERENCES `research_regions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of research_impact_beneficiaries
-- ----------------------------
