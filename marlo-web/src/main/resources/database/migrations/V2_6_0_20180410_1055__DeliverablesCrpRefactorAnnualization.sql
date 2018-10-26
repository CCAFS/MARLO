ALTER TABLE `deliverable_crps` DROP FOREIGN KEY `deliverable_crps_ibfk_2`;

ALTER TABLE `deliverable_crps` DROP FOREIGN KEY `deliverable_crps_ibfk_3`;

ALTER TABLE `deliverable_crps`
CHANGE COLUMN `crp_id` `crp_pandr`  bigint(20) NULL AFTER `id_phase`,
CHANGE COLUMN `crp_program` `ip_program`  bigint(20) NULL DEFAULT NULL AFTER `crp_pandr`,
ADD COLUMN `global_unit`  bigint(20) NULL AFTER `ip_program`,
ADD COLUMN `crp_program`  bigint(20) NULL AFTER `global_unit`,
DROP INDEX `deliverable_id` ,
ADD INDEX `deliverable_crps_ibfk_1` (`deliverable_id`) USING BTREE ,
DROP INDEX `deliverable_crps_ibfk_2` ,
ADD INDEX `deliverable_crps_ibfk_2` (`crp_pandr`) USING BTREE ,
DROP INDEX `deliverable_crps_ibfk_3` ,
ADD INDEX `deliverable_crps_ibfk_3` (`ip_program`) USING BTREE ,
DROP INDEX `id_phase` ,
ADD INDEX `deliverable_crps_ibfk_4` (`id_phase`) USING BTREE ,
ADD INDEX `deliverable_crps_ibfk_5` (`global_unit`) USING BTREE ,
ADD INDEX `deliverable_crps_ibfk_6` (`crp_program`) USING BTREE ,
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

ALTER TABLE `deliverable_crps` ADD CONSTRAINT `deliverable_crps_ibfk_2` FOREIGN KEY (`crp_pandr`) REFERENCES `crps_pandr` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `deliverable_crps` ADD CONSTRAINT `deliverable_crps_ibfk_3` FOREIGN KEY (`ip_program`) REFERENCES `ip_programs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `deliverable_crps` ADD CONSTRAINT `deliverable_crps_ibfk_5` FOREIGN KEY (`global_unit`) REFERENCES `global_units` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `deliverable_crps` ADD CONSTRAINT `deliverable_crps_ibfk_6` FOREIGN KEY (`crp_program`) REFERENCES `crp_programs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;