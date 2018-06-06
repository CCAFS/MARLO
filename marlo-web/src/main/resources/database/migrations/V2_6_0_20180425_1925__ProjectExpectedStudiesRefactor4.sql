ALTER TABLE `project_expected_studies`
ADD COLUMN `year`  int(11) NULL AFTER `topic_study`,
ADD COLUMN `old_case_study_id`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `project_expected_studies` ADD CONSTRAINT `project_expected_studies_ibfk_7` FOREIGN KEY (`old_case_study_id`) REFERENCES `cases_studies` (`id`);