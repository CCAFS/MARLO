ALTER TABLE `capacity_development` 
ADD COLUMN `is_estimate_participants_num` TINYINT(1) NULL DEFAULT NULL AFTER `num_participants`;