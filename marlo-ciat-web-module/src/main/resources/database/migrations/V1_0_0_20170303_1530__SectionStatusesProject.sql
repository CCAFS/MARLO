ALTER TABLE `section_statuses`
ADD COLUMN `project_id`  bigint(20) NULL AFTER `research_program_id`;

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_project_id_fk` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`);