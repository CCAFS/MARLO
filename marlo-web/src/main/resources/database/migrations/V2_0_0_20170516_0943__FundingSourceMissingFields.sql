ALTER TABLE `section_statuses`
ADD COLUMN `funding_source_id`  bigint(20) NULL AFTER `ip_liaison_id`;

ALTER TABLE `section_statuses` ADD FOREIGN KEY (`funding_source_id`) REFERENCES `funding_sources` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

