ALTER TABLE `center_projects`
ADD COLUMN `project_id`  bigint(20) NULL AFTER `id`;

ALTER TABLE `center_projects` ADD CONSTRAINT `center_projects_project_id_fk` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`);