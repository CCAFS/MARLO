ALTER TABLE `section_statuses`
ADD COLUMN `case_study_id`  bigint(20) NULL AFTER `project_outcome_id`;

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_ibfk_5` FOREIGN KEY (`case_study_id`) REFERENCES `cases_studies` (`id`);