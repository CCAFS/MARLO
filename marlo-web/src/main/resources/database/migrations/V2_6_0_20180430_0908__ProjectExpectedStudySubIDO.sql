SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_expected_study_sub_ido
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_sub_ido`;
CREATE TABLE `project_expected_study_sub_ido` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expected_id` bigint(20) DEFAULT NULL,
  `sub_ido_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`),
  KEY `sub_ido_id` (`sub_ido_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_sub_ido_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_sub_ido_ibfk_2` FOREIGN KEY (`sub_ido_id`) REFERENCES `srf_sub_idos` (`id`),
  CONSTRAINT `project_expected_study_sub_ido_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO project_expected_study_sub_ido(expected_id, sub_ido_id, id_phase)
SELECT
project_expected_studies.id,
project_expected_studies.sub_ido,
project_expected_study_info.id_phase
FROM
project_expected_studies
INNER JOIN project_expected_study_info ON project_expected_study_info.project_expected_study_id = project_expected_studies.id;