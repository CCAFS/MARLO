CREATE TABLE `report_synthesis_flagship_progress_outcome_milestone_links` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `progress_outcome_milestones_id` bigint DEFAULT NULL,
  `link` text,
  PRIMARY KEY (`id`),
  KEY `progress_outcome_milestones_id` (`progress_outcome_milestones_id`),
  CONSTRAINT `progress_outcome_milestones_links_ibfk_1` FOREIGN KEY (`progress_outcome_milestones_id`) REFERENCES `report_synthesis_flagship_progress_outcome_milestones` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;