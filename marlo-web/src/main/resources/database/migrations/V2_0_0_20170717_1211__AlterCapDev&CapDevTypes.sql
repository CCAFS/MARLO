ALTER TABLE `capacity_development_types` 
ADD COLUMN `category` varchar(45) NULL AFTER `name`;


ALTER TABLE `capacity_development` 
CHANGE COLUMN `title` `title` varchar(500) NULL DEFAULT NULL ;