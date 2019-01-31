SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_expected_study_links
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_links`;
CREATE TABLE `project_expected_study_links` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `link` text,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_links_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_links_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of project_expected_study_links
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_expected_study_policies
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_policies`;
CREATE TABLE `project_expected_study_policies` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `project_policy_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`),
  KEY `project_policy_id` (`project_policy_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_policies_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_policies_ibfk_2` FOREIGN KEY (`project_policy_id`) REFERENCES `project_policies` (`id`),
  CONSTRAINT `project_expected_study_policies_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of project_expected_study_policies
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_expected_study_innovations
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_innovations`;
CREATE TABLE `project_expected_study_innovations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `project_innovation_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`),
  KEY `project_innovation_id` (`project_innovation_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_innovations_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_innovations_ibfk_2` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`),
  CONSTRAINT `project_expected_study_innovations_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of project_expected_study_innovations
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_expected_study_quantifications
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_quantifications`;
CREATE TABLE `project_expected_study_quantifications` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `type_quantification` text,
  `number` decimal(10,0) DEFAULT NULL,
  `target_unit` text,
  `comments` text,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_quantifications_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_quantifications_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of project_expected_study_quiantifications
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;


