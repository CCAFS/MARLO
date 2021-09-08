CREATE TABLE `project_expected_study_references` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_expected_study_id` bigint DEFAULT NULL,
  `reference` text,
  `id_phase` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_expected_study_id` (`project_expected_study_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_references_ibfk_1` FOREIGN KEY (`project_expected_study_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_references_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `project_innovation_evidence_links` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_innovation_id` bigint DEFAULT NULL,
  `link` text,
  `id_phase` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_innovation_id` (`project_innovation_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_innovation_evidence_links_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`),
  CONSTRAINT `project_innovation_evidence_links_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;