ALTER TABLE `section_statuses`
ADD COLUMN `project_expected_id`  bigint(20) NULL AFTER `powb_synthesis_id`;

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_ibfk_expected` FOREIGN KEY (`project_expected_id`) REFERENCES `project_expected_studies` (`id`);