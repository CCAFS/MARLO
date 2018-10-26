SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_external_partnerships
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_external_partnerships`;
CREATE TABLE `report_synthesis_external_partnerships` (
  `id` bigint(20) NOT NULL,
  `highlights` text,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  `active_since` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_external_partnerships_ibfk_1` FOREIGN KEY (`id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_external_partnerships_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_external_partnerships_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for report_synthesis_external_partnership_projects
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_external_partnership_projects`;
CREATE TABLE `report_synthesis_external_partnership_projects` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_external_partnerships_id` bigint(20) DEFAULT NULL,
  `project_partner_partnership_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  `active_since` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_external_partnerships_id` (`report_synthesis_external_partnerships_id`),
  KEY `project_partner_partnership_id` (`project_partner_partnership_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_external_partnership_projects_ibfk_1` FOREIGN KEY (`report_synthesis_external_partnerships_id`) REFERENCES `report_synthesis_external_partnerships` (`id`),
  CONSTRAINT `report_synthesis_external_partnership_projects_ibfk_2` FOREIGN KEY (`project_partner_partnership_id`) REFERENCES `project_partner_partnerships` (`id`),
  CONSTRAINT `report_synthesis_external_partnership_projects_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_external_partnership_projects_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
