ALTER TABLE `submissions`
CHANGE COLUMN `crp_pgroma_id` `crp_program_id`  bigint(20) NULL AFTER `id`,
ADD COLUMN `project_id`  bigint(20) NULL AFTER `crp_program_id`;

ALTER TABLE `submissions` ADD CONSTRAINT `Submissions_ibfk_3` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`);
