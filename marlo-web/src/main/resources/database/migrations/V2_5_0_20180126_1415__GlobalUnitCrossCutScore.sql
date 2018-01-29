ALTER TABLE `cross_cutting_scoring`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `cross_cutting_scoring` ADD CONSTRAINT `cc_scoring_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

UPDATE cross_cutting_scoring set global_unit_id = crp_id;

ALTER TABLE `cross_cutting_scoring` DROP FOREIGN KEY `crosscut_fk1`;

ALTER TABLE `cross_cutting_scoring`
DROP COLUMN `crp_id`,
MODIFY COLUMN `global_unit_id`  bigint(20) NULL;