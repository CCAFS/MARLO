CREATE TABLE IF NOT EXISTS `report_synthesis_srf_progress_targets_cases_links` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_srf_progress_targets_case_id` bigint(20) NOT NULL,
  `link_name` varchar(255) DEFAULT NULL,
  `link` varchar(500) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_srf_progress_targets_case_id` (`report_synthesis_srf_progress_targets_case_id`) USING BTREE,
  CONSTRAINT `report_synthesis_srf_progress_targets_cases_linkfk_1` FOREIGN KEY (`report_synthesis_srf_progress_targets_case_id`) REFERENCES `report_synthesis_srf_progress_targets_cases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

