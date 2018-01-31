ALTER TABLE `participant` 
CHANGE COLUMN `gender` `gender` VARCHAR(10) NOT NULL ;


ALTER TABLE `capacity_development` 
ADD COLUMN `num_men` INT(11) NULL AFTER `crp`,
ADD COLUMN `num_women` INT(11) NULL AFTER `num_men`;