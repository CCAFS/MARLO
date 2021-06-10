ALTER TABLE `crp_program_outcome_indicator`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `composed_id`,
ADD INDEX `id_phase` (`id_phase`) USING BTREE ;

ALTER TABLE `crp_program_outcome_indicator` ADD CONSTRAINT `crp_program_outcome_indicator_ibfk_4` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;