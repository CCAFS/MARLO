ALTER TABLE `project_expected_study_info`
MODIFY COLUMN `status`  bigint(20) NULL DEFAULT NULL AFTER `year`;

ALTER TABLE `project_expected_study_info` ADD FOREIGN KEY (`status`) REFERENCES `general_statuses` (`id`);
