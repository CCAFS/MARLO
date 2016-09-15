ALTER TABLE `section_statuses`
ADD COLUMN `deliverable_id`  bigint(20) NULL AFTER `year`,
ADD COLUMN `project_outcome_id`  bigint(20) NULL AFTER `deliverable_id`,
ADD COLUMN `project_cofunded_id`  bigint(20) NULL AFTER `project_outcome_id`;

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_ibfk_3` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`);

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_ibfk_4` FOREIGN KEY (`project_outcome_id`) REFERENCES `project_outcomes` (`id`);

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_ibfk_5` FOREIGN KEY (`project_cofunded_id`) REFERENCES `projects_bilateral_cofinancing` (`id`);