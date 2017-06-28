Delete from section_statuses;

ALTER TABLE `section_statuses` DROP FOREIGN KEY `section_statuses_ibfk_1`;

ALTER TABLE `section_statuses` DROP FOREIGN KEY `section_statuses_ibfk_2`;

ALTER TABLE `section_statuses` DROP FOREIGN KEY `section_statuses_ibfk_3`;

ALTER TABLE `section_statuses` DROP FOREIGN KEY `section_statuses_ibfk_4`;

ALTER TABLE `section_statuses` DROP FOREIGN KEY `section_statuses_ibfk_5`;

ALTER TABLE `section_statuses`
DROP COLUMN `crp_program_id`,
DROP COLUMN `project_id`,
DROP COLUMN `deliverable_id`,
DROP COLUMN `project_outcome_id`,
DROP COLUMN `project_cofunded_id`,
DROP INDEX `crp_program_id`,
DROP INDEX `project_id`,
DROP INDEX `section_statuses_ibfk_3`,
DROP INDEX `section_statuses_ibfk_4`,
DROP INDEX `section_statuses_ibfk_5`;