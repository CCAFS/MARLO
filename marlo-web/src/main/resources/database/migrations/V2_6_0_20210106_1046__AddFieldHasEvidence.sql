ALTER TABLE `srf_slo_indicator_targets`
ADD COLUMN `has_evidence`  tinyint(1) NULL AFTER `target_unit_id`;