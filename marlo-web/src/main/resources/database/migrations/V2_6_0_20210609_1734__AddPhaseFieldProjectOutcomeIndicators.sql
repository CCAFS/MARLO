ALTER TABLE `project_outcome_indicators`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `achieved_narrative`,
ADD INDEX `id_phase` (`id_phase`) USING BTREE ;

ALTER TABLE `project_outcome_indicators` ADD CONSTRAINT `project_outcome_indicators_ibfk_5` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;