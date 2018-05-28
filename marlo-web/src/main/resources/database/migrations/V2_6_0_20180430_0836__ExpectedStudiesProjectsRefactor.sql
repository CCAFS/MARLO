ALTER TABLE `expected_study_projects` DROP FOREIGN KEY `expected_study_projects_ibfk_3`;

ALTER TABLE `expected_study_projects` DROP FOREIGN KEY `expected_study_projects_ibfk_4`;

ALTER TABLE `expected_study_projects`
DROP COLUMN `is_active`,
DROP COLUMN `active_since`,
DROP COLUMN `created_by`,
DROP COLUMN `modified_by`,
DROP COLUMN `modification_justification`,
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `expected_id`;

ALTER TABLE `expected_study_projects` ADD CONSTRAINT `expected_study_projects_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);