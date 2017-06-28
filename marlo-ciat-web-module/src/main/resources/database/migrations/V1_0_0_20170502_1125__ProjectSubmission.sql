ALTER TABLE `submissions`
ADD COLUMN `project_id`  bigint(20) NULL AFTER `program_id`;

ALTER TABLE `submissions` ADD CONSTRAINT `submissions_project_fk` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`);