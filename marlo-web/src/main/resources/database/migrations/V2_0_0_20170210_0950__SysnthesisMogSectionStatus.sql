ALTER TABLE `section_statuses`
ADD COLUMN `ip_program_id`  bigint(20) NULL AFTER `highlight_id`;

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_ibfk_ipprogram` FOREIGN KEY (`ip_program_id`) REFERENCES `ip_programs` (`id`);