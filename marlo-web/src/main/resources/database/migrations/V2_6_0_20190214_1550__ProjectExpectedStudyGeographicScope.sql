SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_expected_study_geographic_scopes
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_geographic_scopes`;
CREATE TABLE `project_expected_study_geographic_scopes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `rep_ind_geographic_scope_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`),
  KEY `rep_ind_geographic_scope_id` (`rep_ind_geographic_scope_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_geographic_scopes_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_geographic_scopes_ibfk_2` FOREIGN KEY (`rep_ind_geographic_scope_id`) REFERENCES `rep_ind_geographic_scopes` (`id`),
  CONSTRAINT `project_expected_study_geographic_scopes_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of project_expected_study_geographic_scopes
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
