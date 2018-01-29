ALTER TABLE `center_deliverables` DROP FOREIGN KEY `deliverable_project_fk`;
ALTER TABLE `center_project_funding_sources` DROP FOREIGN KEY `center_project_funding_sources_ibfk_5`;
ALTER TABLE `center_project_locations` DROP FOREIGN KEY `project_location_id_fk`;
ALTER TABLE `center_project_outputs` DROP FOREIGN KEY `po_project_fk`;
ALTER TABLE `center_project_partners` DROP FOREIGN KEY `project_partners_project_id_fk`;
ALTER TABLE `center_section_statuses` DROP FOREIGN KEY `section_statuses_project_id_fk`;
ALTER TABLE `center_submissions` DROP FOREIGN KEY `submissions_project_fk`;
ALTER TABLE `center_project_crosscuting_themes` DROP FOREIGN KEY `project_cc_theme_project_id_fk`;
ALTER TABLE `center_projects` DROP FOREIGN KEY `center_project_project_id_fk`;

ALTER TABLE `center_projects`
MODIFY COLUMN `id`  bigint(20) NOT NULL FIRST ;

ALTER TABLE `center_project_crosscuting_themes` ADD CONSTRAINT `project_cc_theme_project_fk` FOREIGN KEY (`id`) REFERENCES `center_projects` (`id`);
ALTER TABLE `center_deliverables` ADD CONSTRAINT `deliverable_project_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`);
ALTER TABLE `center_project_funding_sources` ADD CONSTRAINT `center_project_funding_project_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`);
ALTER TABLE `center_project_locations` ADD CONSTRAINT `project_location_project_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`);
ALTER TABLE `center_project_outputs` ADD CONSTRAINT `po_project_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`);
ALTER TABLE `center_project_partners` ADD CONSTRAINT `project_partners_project_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`);
ALTER TABLE `center_section_statuses` ADD CONSTRAINT `section_statutses_project_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`);
ALTER TABLE `center_submissions` ADD CONSTRAINT `submissions_project_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`);
ALTER TABLE `center_projects` ADD CONSTRAINT `center_project_prject_fk` FOREIGN KEY (`id`) REFERENCES `projects` (`id`);