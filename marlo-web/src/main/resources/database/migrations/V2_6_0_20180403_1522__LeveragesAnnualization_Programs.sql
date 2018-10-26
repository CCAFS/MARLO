ALTER TABLE `project_leverage` DROP FOREIGN KEY `project_leverage_ibfk_5`;

ALTER TABLE `project_leverage`
CHANGE COLUMN `flagship` `ip_program`  bigint(20) NULL DEFAULT NULL AFTER `year`,
ADD COLUMN `program_id`  bigint(20) NULL AFTER `year`,
DROP INDEX `flagship` ,
ADD INDEX `ip_program` (`ip_program`) USING BTREE ,
ADD INDEX `program_id` (`program_id`) USING BTREE ;

ALTER TABLE `project_leverage` ADD CONSTRAINT `project_leverage_ibfk_5` FOREIGN KEY (`ip_program`) 
REFERENCES `ip_programs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `project_leverage` ADD CONSTRAINT `project_leverage_ibfk_7` FOREIGN KEY (`program_id`) 
REFERENCES `crp_programs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;