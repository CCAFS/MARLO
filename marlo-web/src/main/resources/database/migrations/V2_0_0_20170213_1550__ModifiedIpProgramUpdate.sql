ALTER TABLE `ip_programs`
ADD COLUMN `modified_by`  bigint(20) NULL AFTER `active_since`;

ALTER TABLE `ip_programs` ADD CONSTRAINT `ip_programs_modfied_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);