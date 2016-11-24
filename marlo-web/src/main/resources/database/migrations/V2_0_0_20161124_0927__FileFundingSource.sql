ALTER TABLE `funding_sources`
ADD COLUMN `file`  bigint(20) NULL AFTER `institution_id`;

ALTER TABLE `funding_sources` ADD FOREIGN KEY (`file`) REFERENCES `files` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

