ALTER TABLE `funding_sources_info`
ADD COLUMN `lead_center_id`  bigint(20) NULL AFTER `w1w2`,
ADD INDEX `lead_center_id` (`lead_center_id`) USING BTREE ;

ALTER TABLE `funding_sources_info` ADD CONSTRAINT `funding_sources_info_ibfk_8` FOREIGN KEY (`lead_center_id`) REFERENCES `institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;