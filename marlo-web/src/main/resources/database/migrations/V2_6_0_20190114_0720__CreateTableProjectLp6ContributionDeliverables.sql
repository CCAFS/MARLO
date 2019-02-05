CREATE TABLE IF NOT EXISTS `project_lp6_contribution_deliverables` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lp6_contribution_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `deliverable_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `lp6_contribution_id` (`lp6_contribution_id`) USING BTREE,
  KEY `id_phase` (`id_phase`) USING BTREE,
  KEY `deliverable_id` (`deliverable_id`) USING BTREE,
  CONSTRAINT `project_lp6_contribution_deliverables_ibfk_1` FOREIGN KEY (`lp6_contribution_id`) REFERENCES `project_lp6_contribution` (`id`),
  CONSTRAINT `project_lp6_contribution_deliverables_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
  CONSTRAINT `project_lp6_contribution_deliverables_ibfk_3` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`)
);