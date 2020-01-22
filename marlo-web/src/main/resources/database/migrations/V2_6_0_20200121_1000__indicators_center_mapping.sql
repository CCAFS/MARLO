SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_innovation_centers
-- ----------------------------
DROP TABLE IF EXISTS `project_innovation_centers`;
CREATE TABLE `project_innovation_centers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_innovation_id` bigint(20) DEFAULT NULL,
  `global_unit_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_innovation_id` (`project_innovation_id`) USING BTREE,
  KEY `id_phase` (`id_phase`) USING BTREE,
  KEY `global_unit_id` (`global_unit_id`) USING BTREE,
  CONSTRAINT `project_innovation_centers_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `project_innovation_centers_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `project_innovation_centers_ibfk_3` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for project_expected_study_centers
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_centers`;
CREATE TABLE `project_expected_study_centers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `global_unit_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`) USING BTREE,
  KEY `id_phase` (`id_phase`) USING BTREE,
  KEY `global_unit_id` (`global_unit_id`) USING BTREE,
  CONSTRAINT `project_expected_study_centers_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `project_expected_study_centers_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `project_expected_study_centers_ibfk_3` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for project_policy_centers
-- ----------------------------
DROP TABLE IF EXISTS `project_policy_centers`;
CREATE TABLE `project_policy_centers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_policy_id` bigint(20) DEFAULT NULL,
  `global_unit_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_policy_id` (`project_policy_id`) USING BTREE,
  KEY `id_phase` (`id_phase`) USING BTREE,
  KEY `global_unit_id` (`global_unit_id`) USING BTREE,
  CONSTRAINT `project_policy_centers_ibfk_1` FOREIGN KEY (`project_policy_id`) REFERENCES `project_policies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `project_policy_centers_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `project_policy_centers_ibfk_3` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

