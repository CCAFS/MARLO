ALTER TABLE `center_projects` DROP FOREIGN KEY `center_projects_project_id_fk`;

ALTER TABLE `center_projects`
DROP COLUMN `project_id`;

ALTER TABLE `center_projects` ADD CONSTRAINT `center_project_project_id_fk` FOREIGN KEY (`id`) REFERENCES `projects` (`id`);


