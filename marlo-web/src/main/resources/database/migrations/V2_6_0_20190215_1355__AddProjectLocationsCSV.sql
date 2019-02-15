ALTER TABLE `projects_info`
ADD COLUMN `activities_csv`  tinyint(1) NULL DEFAULT NULL AFTER `partner_overall`,
ADD COLUMN `activities_csv_file`  bigint(20) NULL DEFAULT NULL AFTER `activities_csv`;

ALTER TABLE `projects_info` ADD CONSTRAINT `projects_info_ibfk_8` 
FOREIGN KEY (`activities_csv_file`) REFERENCES `files` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;