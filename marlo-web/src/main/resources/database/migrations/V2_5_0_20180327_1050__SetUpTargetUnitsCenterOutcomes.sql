ALTER TABLE `center_outcomes`
ADD COLUMN `srf_target_unit_id`  bigint(20) NULL AFTER `value`;

ALTER TABLE `center_outcomes` ADD CONSTRAINT `center_outcomes_ibfk_6` FOREIGN KEY (`srf_target_unit_id`) REFERENCES `srf_target_units` (`id`);
