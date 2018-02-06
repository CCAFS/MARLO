ALTER TABLE `parameters`
ADD COLUMN `global_unit_type_id`  bigint(20) NULL AFTER `id`;
ALTER TABLE `parameters` ADD CONSTRAINT `parameters_global_unit_id_fk` FOREIGN KEY (`global_unit_type_id`) REFERENCES `global_unit_types` (`id`);