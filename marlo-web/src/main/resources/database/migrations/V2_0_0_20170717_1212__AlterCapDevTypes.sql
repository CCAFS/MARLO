ALTER TABLE `capacity_development_types` 
CHANGE COLUMN `category` `category` VARCHAR(45) NULL DEFAULT NULL COMMENT '1=individual training; 2= group training' ;