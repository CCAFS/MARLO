SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_expected_study_srf_targets
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_srf_targets`;
CREATE TABLE `project_expected_study_srf_targets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `srf_target_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`),
  KEY `srf_target_id` (`srf_target_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_srf_targets_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_srf_targets_ibfk_2` FOREIGN KEY (`srf_target_id`) REFERENCES `srf_slo_indicators` (`id`),
  CONSTRAINT `project_expected_study_srf_targets_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO project_expected_study_srf_targets(expected_id, srf_target_id, id_phase)
SELECT
project_expected_studies.id,
project_expected_studies.srf_target,
project_expected_study_info.id_phase
FROM
project_expected_studies
INNER JOIN project_expected_study_info ON project_expected_study_info.project_expected_study_id = project_expected_studies.id;

DELETE from project_expected_study_srf_targets where srf_target_id IS NULL;