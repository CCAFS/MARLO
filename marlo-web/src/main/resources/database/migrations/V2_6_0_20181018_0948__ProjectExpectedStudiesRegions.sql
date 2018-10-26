SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_expected_study_regions
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_regions`;
CREATE TABLE `project_expected_study_regions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `rep_ind_region_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`),
  KEY `rep_ind_region_id` (`rep_ind_region_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_regions_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_regions_ibfk_2` FOREIGN KEY (`rep_ind_region_id`) REFERENCES `rep_ind_regions` (`id`),
  CONSTRAINT `project_expected_study_regions_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


