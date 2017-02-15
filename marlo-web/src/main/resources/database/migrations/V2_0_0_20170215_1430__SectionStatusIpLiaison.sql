ALTER TABLE `section_statuses`
ADD COLUMN `ip_liaison_id`  bigint(20) NULL AFTER `ip_program_id`;

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_ibfk_ipliaison` FOREIGN KEY (`ip_liaison_id`) REFERENCES `ip_liaison_institutions` (`id`);