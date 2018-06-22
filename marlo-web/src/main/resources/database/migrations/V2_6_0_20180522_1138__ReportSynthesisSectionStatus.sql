ALTER TABLE `section_statuses`
ADD COLUMN `report_synthesis_id`  bigint(20) NULL AFTER `project_innovation_id`;

ALTER TABLE `section_statuses` ADD FOREIGN KEY (`report_synthesis_id`) REFERENCES `report_synthesis` (`id`);