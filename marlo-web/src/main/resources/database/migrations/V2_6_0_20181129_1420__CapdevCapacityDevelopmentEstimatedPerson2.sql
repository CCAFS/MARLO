ALTER TABLE `capacity_development` 
ADD COLUMN `is_estimate_men_num` TINYINT(1) NULL DEFAULT NULL AFTER `is_estimate_participants_num`;

ALTER TABLE `capacity_development` 
ADD COLUMN `is_estimate_women_num` TINYINT(1) NULL DEFAULT NULL AFTER `is_estimate_men_num`;

ALTER TABLE `capacity_development` 
ADD COLUMN `is_estimate_others_num` TINYINT(1) NULL DEFAULT NULL AFTER `is_estimate_women_num`;