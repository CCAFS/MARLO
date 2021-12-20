CREATE TABLE `report_synthesis_crp_financial_reports` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `report_synthesis_id` bigint NOT NULL,
  `financial_status_narrative` text COMMENT 'AR2021 Field',
  `personnel_2020_forecast` double(30,2) DEFAULT NULL,
  `personnel_2021_budget` double(30,2) DEFAULT NULL,
  `personnel_comments` text COMMENT 'Comments of major personel changes',
  `consultancy_2020_forecast` double(30,2) DEFAULT NULL,
  `consultancy_2021_budget` double(30,2) DEFAULT NULL,
  `consultancy_comments` text COMMENT 'Comments of major consultancy changes',
  `travel_2020_forecast` double(30,2) DEFAULT NULL,
  `travel_2021_budget` double(30,2) DEFAULT NULL,
  `travel_comments` text COMMENT 'Comments of major travel changes',
  `operation_2020_forecast` double(30,2) DEFAULT NULL,
  `operation_2021_budget` double(30,2) DEFAULT NULL,
  `operation_comments` text COMMENT 'Comments of major operation expenses changes',
  `collaborator_partnerships_2020_forecast` double(30,2) DEFAULT NULL,
  `collaborator_partnerships_2021_budget` double(30,2) DEFAULT NULL,
  `collaborator_partnerships_comments` text COMMENT 'Comments of major collaborator and partnerships changes',
  `capital_equipment_2020_forecast` double(30,2) DEFAULT NULL,
  `capital_equipment_2021_budget` double(30,2) DEFAULT NULL,
  `capital_equipment_comments` text COMMENT 'Comments of major capital and equipment changes',
  `closeout_2020_forecast` double(30,2) DEFAULT NULL,
  `closeout_2021_budget` double(30,2) DEFAULT NULL,
  `closeout_comments` text COMMENT 'Comments of major closeout cost changes',
  `crp_total_2020_forecast` double(30,2) DEFAULT NULL,
  `crp_total_2021_budget` double(30,2) DEFAULT NULL,
  `crp_total_comments` text COMMENT 'Comments of major crp total budget changes',
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `modified_by` bigint DEFAULT NULL,
  `modification_justification` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_id` (`report_synthesis_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_crp_financial_reports_ibfk_1` FOREIGN KEY (`report_synthesis_id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_crp_financial_reports_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_crp_financial_reports_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;