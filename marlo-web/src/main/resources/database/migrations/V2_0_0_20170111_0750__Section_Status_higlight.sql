ALTER TABLE `section_statuses`
ADD COLUMN `highlight_id`  bigint(20) NULL AFTER `case_study_id`;

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_Highlight` FOREIGN KEY (`highlight_id`) REFERENCES `project_highlights` (`id`);