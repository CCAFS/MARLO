CREATE TABLE `project_expected_study_references` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_expected_study_id` bigint DEFAULT NULL,
  `reference` mediumtext,
  `id_phase` bigint DEFAULT NULL,
  `link` mediumtext,
  PRIMARY KEY (`id`),
  KEY `project_expected_study_id` (`project_expected_study_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_references_ibfk_1` FOREIGN KEY (`project_expected_study_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_references_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14437 DEFAULT CHARSET=utf8mb3;