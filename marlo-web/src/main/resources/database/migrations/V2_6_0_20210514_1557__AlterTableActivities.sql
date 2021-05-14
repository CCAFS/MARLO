ALTER TABLE `activities`
ADD COLUMN `title_id`  bigint(20) NULL AFTER `project_id`,
ADD INDEX `fk_title_id` (`title_id`) USING BTREE ;

ALTER TABLE `activities` ADD CONSTRAINT `activities_ibfk_6` FOREIGN KEY (`title_id`) REFERENCES `activities_titles` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;



ALTER TABLE `activities_titles`
DROP COLUMN `is_active`;

