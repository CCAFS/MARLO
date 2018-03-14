ALTER TABLE `global_units`
ADD COLUMN `institution_id`  bigint(20) NULL AFTER `acronym`;

ALTER TABLE `global_units` ADD CONSTRAINT `global_units_ibfk_4` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`);