ALTER TABLE `project_expected_study_regions` DROP FOREIGN KEY `project_expected_study_regions_ibfk_4`;

ALTER TABLE `project_expected_study_regions` ADD CONSTRAINT `project_expected_study_regions_ibfk_4` FOREIGN KEY (`id_region`) REFERENCES `loc_elements` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

