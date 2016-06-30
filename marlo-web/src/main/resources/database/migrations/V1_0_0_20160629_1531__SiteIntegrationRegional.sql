ALTER TABLE `crps_sites_integration`
ADD COLUMN `is_regional`  tinyint(1)  DEFAULT 0 NULL AFTER `modification_justification`;