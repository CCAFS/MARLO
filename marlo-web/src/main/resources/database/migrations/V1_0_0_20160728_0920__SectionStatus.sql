ALTER TABLE `section_statuses`
ADD COLUMN `project_id`  bigint NULL AFTER `crp_program_id`;

ALTER TABLE `section_statuses` ADD FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

