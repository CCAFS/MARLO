
ALTER TABLE `capacity_development` 
CHANGE COLUMN `title` `title` varchar(200) NULL ,
CHANGE COLUMN `capdev_type` `capdev_type` bigint(20) NULL ,
CHANGE COLUMN `category` `category` int(11) NULL ,
CHANGE COLUMN `start_date` `start_date` date NULL ;
