ALTER TABLE `capacity_development` 
ADD COLUMN `other_discipline` varchar(2) NULL AFTER `is_regional`,
ADD COLUMN `discipline_suggested` varchar(200) NULL AFTER `other_discipline`,
ADD COLUMN `other_target_group` varchar(2) NULL AFTER `discipline_suggested`,
ADD COLUMN `target_group_suggested` varchar(200) NULL AFTER `other_target_group`,
ADD COLUMN `other_partner` varchar(2) NULL AFTER `target_group_suggested`,
ADD COLUMN `partner_suggested` varchar(200) NULL AFTER `other_partner`;