ALTER TABLE `participant` 
CHANGE COLUMN `code` `code` bigint(20) NULL ,
CHANGE COLUMN `name` `name` varchar(200) NULL ,
CHANGE COLUMN `last_name` `last_name` varchar(200) NULL ,
CHANGE COLUMN `gender` `gender` varchar(10) NULL ,
CHANGE COLUMN `citizenship` `citizenship` varchar(200) NULL ,
CHANGE COLUMN `email` `email` varchar(200) NULL ;