ALTER TABLE `section_statuses`
ADD COLUMN `project_policy_id`  bigint(20) NULL AFTER `report_synthesis_id`;

ALTER TABLE `section_statuses` ADD FOREIGN KEY (`project_policy_id`) REFERENCES `project_policies` (`id`);

