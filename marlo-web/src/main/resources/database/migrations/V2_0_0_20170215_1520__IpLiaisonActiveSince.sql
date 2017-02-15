ALTER TABLE `ip_liaison_institutions`
ADD COLUMN `active_since`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP AFTER `ip_program`,
ADD COLUMN `modified_by`  bigint(20) NULL AFTER `active_since`;

ALTER TABLE `ip_liaison_institutions` ADD CONSTRAINT `ip_li_ins_mb` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);

