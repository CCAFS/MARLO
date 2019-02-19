ALTER TABLE `project_expected_study_regions` DROP FOREIGN KEY `project_expected_study_regions_ibfk_2`;

ALTER TABLE `project_expected_study_regions`
CHANGE COLUMN `rep_ind_region_id` `id_region`  bigint(20) NULL DEFAULT NULL AFTER `expected_id`;

ALTER TABLE `project_expected_study_regions` ADD FOREIGN KEY (`id_region`) REFERENCES `project_expected_studies` (`id`);

