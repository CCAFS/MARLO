ALTER TABLE `srf_target_units`
ADD COLUMN `crp_id`  bigint(20) NULL AFTER `is_active`;
ALTER TABLE `srf_target_units` ADD CONSTRAINT `srf_target_units_ibfk_crp_id` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`);