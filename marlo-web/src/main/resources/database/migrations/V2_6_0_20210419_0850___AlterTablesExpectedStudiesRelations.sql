ALTER TABLE `project_expected_study_nexus`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `nexus_id`,
ADD INDEX `id_phase` (`id_phase`) USING BTREE ;

ALTER TABLE `project_expected_study_nexus` ADD CONSTRAINT `expected_study_nexus_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;


ALTER TABLE `project_expected_study_lever_outcomes`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `lever_outcome_id`,
ADD INDEX `id_phase` (`id_phase`) USING BTREE ;

ALTER TABLE `project_expected_study_lever_outcomes` ADD CONSTRAINT `expected_study_lever_outcomes_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;


ALTER TABLE `project_expected_study_sdg_targets`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `sdg_target_id`,
ADD INDEX `id_phase` (`id_phase`) USING BTREE ;

ALTER TABLE `project_expected_study_sdg_targets` ADD CONSTRAINT `expected_study_sgd_target_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;


