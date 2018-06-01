SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_cross_cutting_dimensions
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_cross_cutting_dimensions`;
CREATE TABLE `report_synthesis_cross_cutting_dimensions` (
  `id` bigint(20) NOT NULL,
  `gender_description` text,
  `gender_lessons` text,
  `youth_description` text,
  `other_aspects` text,
  `cap_dev` text,
  `open_data` text,
  `intellectual_assets` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_cross_cutting_dimensions_ibfk_1` FOREIGN KEY (`id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_cross_cutting_dimensions_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_cross_cutting_dimensions_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for report_synthesis_cross_cutting_dimension_innovations
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_cross_cutting_dimension_innovations`;
CREATE TABLE `report_synthesis_cross_cutting_dimension_innovations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_cross_cutting_dimension_id` bigint(20) DEFAULT NULL,
  `project_innovation_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_cross_cutting_dimension_id` (`report_synthesis_cross_cutting_dimension_id`),
  KEY `project_innovation_id` (`project_innovation_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_cross_cutting_dimension_innovations_ibfk_1` FOREIGN KEY (`report_synthesis_cross_cutting_dimension_id`) REFERENCES `report_synthesis_cross_cutting_dimensions` (`id`),
  CONSTRAINT `report_synthesis_cross_cutting_dimension_innovations_ibfk_2` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`),
  CONSTRAINT `report_synthesis_cross_cutting_dimension_innovations_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_cross_cutting_dimension_innovations_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- ----------------------------
-- Table structure for report_synthesis_cross_cutting_dimension_assets
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_cross_cutting_dimension_assets`;
CREATE TABLE `report_synthesis_cross_cutting_dimension_assets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deliverable_intelectual_assets_id` bigint(20) DEFAULT NULL,
  `report_synthesis_cross_cutting_dimension_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `deliverable_intelectual_assets_id` (`deliverable_intelectual_assets_id`),
  KEY `report_synthesis_cross_cutting_dimension_id` (`report_synthesis_cross_cutting_dimension_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_cross_cutting_dimension_assets_ibfk_1` FOREIGN KEY (`deliverable_intelectual_assets_id`) REFERENCES `deliverable_intellectual_assets` (`id`),
  CONSTRAINT `report_synthesis_cross_cutting_dimension_assets_ibfk_2` FOREIGN KEY (`report_synthesis_cross_cutting_dimension_id`) REFERENCES `report_synthesis_cross_cutting_dimensions` (`id`),
  CONSTRAINT `report_synthesis_cross_cutting_dimension_assets_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_cross_cutting_dimension_assets_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


