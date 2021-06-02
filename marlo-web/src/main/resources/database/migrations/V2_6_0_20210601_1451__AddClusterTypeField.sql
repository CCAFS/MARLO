ALTER TABLE `projects_info`
ADD COLUMN `type_id`  bigint(20) NULL AFTER `type`,
ADD INDEX `projects_info_ibfk_9` (`type_id`) USING BTREE ;

ALTER TABLE `projects_info` ADD CONSTRAINT `projects_info_ibfk_9` FOREIGN KEY (`type_id`) REFERENCES `cluster_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;