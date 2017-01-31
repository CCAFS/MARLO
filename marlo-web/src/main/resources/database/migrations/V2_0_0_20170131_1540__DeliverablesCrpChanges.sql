
DELETE FROM deliverable_crps;
ALTER TABLE `deliverable_crps` DROP FOREIGN KEY `deliverable_crps_ibfk_2`;

ALTER TABLE `deliverable_crps` DROP FOREIGN KEY `deliverable_crps_ibfk_3`;

ALTER TABLE `deliverable_crps`
MODIFY COLUMN `crp_program`  bigint(20) NULL AFTER `crp_id`;

ALTER TABLE `deliverable_crps` ADD CONSTRAINT `deliverable_crps_ibfk_2` FOREIGN KEY (`crp_id`) REFERENCES `crps_pandr` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `deliverable_crps` ADD CONSTRAINT `deliverable_crps_ibfk_3` FOREIGN KEY (`crp_program`) REFERENCES `ip_programs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;