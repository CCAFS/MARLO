SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_funding_use_summaries
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_funding_use_summaries`;
CREATE TABLE `report_synthesis_funding_use_summaries` (
  `id` bigint(20) NOT NULL,
  `main_area` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_funding_use_summaries_ibfk_1` FOREIGN KEY (`id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_funding_use_summaries_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_funding_use_summaries_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for report_synthesis_funding_use_expenditury_areas
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_funding_use_expenditury_areas`;
CREATE TABLE `report_synthesis_funding_use_expenditury_areas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_funding_use_summary_id` bigint(20) DEFAULT NULL,
  `expenditure_area_id` bigint(20) DEFAULT NULL,
  `w1w2_percentage` double(30,2) DEFAULT NULL,
  `comments` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_funding_use_summary_id` (`report_synthesis_funding_use_summary_id`),
  KEY `expenditure_area_id` (`expenditure_area_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_funding_use_expenditury_areas_ibfk_1` FOREIGN KEY (`report_synthesis_funding_use_summary_id`) REFERENCES `report_synthesis_funding_use_summaries` (`id`),
  CONSTRAINT `report_synthesis_funding_use_expenditury_areas_ibfk_2` FOREIGN KEY (`expenditure_area_id`) REFERENCES `powb_expenditure_areas` (`id`),
  CONSTRAINT `report_synthesis_funding_use_expenditury_areas_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_funding_use_expenditury_areas_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table Permissions for Funding Use
-- ----------------------------

INSERT INTO permissions (permission, description, type)
VALUES ('crp:{0}:reportSynthesis:{1}:fundingUse', 'Can edit in Annual Report Synthesis Funding Use', 1);
--
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE 
r.acronym in( 'FPL','FPM','PMU')
AND
p.permission = 'crp:{0}:reportSynthesis:{1}:fundingUse';