SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_expected_study_crp
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_crp`;
CREATE TABLE `project_expected_study_crp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `global_unit_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`),
  KEY `global_unit_id` (`global_unit_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_crp_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_crp_ibfk_2` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`),
  CONSTRAINT `project_expected_study_crp_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for project_expected_study_institutions
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_institutions`;
CREATE TABLE `project_expected_study_institutions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `institution_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`),
  KEY `institution_id` (`institution_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_institutions_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_institutions_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`),
  CONSTRAINT `project_expected_study_institutions_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for project_expected_study_flagships
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_flagships`;
CREATE TABLE `project_expected_study_flagships` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `crp_program_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `crp_program_id` (`crp_program_id`),
  KEY `expected_id` (`expected_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_flagships_ibfk_1` FOREIGN KEY (`crp_program_id`) REFERENCES `crp_programs` (`id`),
  CONSTRAINT `project_expected_study_flagships_ibfk_2` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_flagships_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for project_expected_study_countries
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_countries`;
CREATE TABLE `project_expected_study_countries` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `loc_element_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`) USING BTREE,
  KEY `loc_element_id` (`loc_element_id`) USING BTREE,
  KEY `id_phase` (`id_phase`) USING BTREE,
  CONSTRAINT `project_expected_study_countries_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_countries_ibfk_2` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`),
  CONSTRAINT `project_expected_study_countries_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



