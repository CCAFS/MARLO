ALTER TABLE `center_milestones`
ADD COLUMN `srf_target_unit`  bigint(20) NULL AFTER `value`;

ALTER TABLE `center_milestones` ADD CONSTRAINT `center_milestones_ibfk_5` FOREIGN KEY (`srf_target_unit`) REFERENCES `srf_target_units` (`id`);