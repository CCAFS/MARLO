ALTER TABLE `powb_collaboration_global_units`
ADD COLUMN `institution_id`  bigint(20) NULL AFTER `global_unit_id`;

ALTER TABLE `powb_collaboration_global_units` ADD FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`);