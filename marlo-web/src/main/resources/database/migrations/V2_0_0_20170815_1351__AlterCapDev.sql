ALTER TABLE `capacity_development` 
ADD COLUMN `is_global` tinyint(1) NULL AFTER `duration`,
ADD COLUMN `is_regional` tinyint(1) NULL AFTER `is_global`;
