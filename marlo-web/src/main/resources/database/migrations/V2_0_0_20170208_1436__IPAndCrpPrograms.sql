ALTER TABLE `ip_programs`
ADD COLUMN `crp_program_id`  bigint(20) NULL AFTER `type_id`;
ALTER TABLE `ip_programs` ADD CONSTRAINT `ip_programs_program_fk` FOREIGN KEY (`crp_program_id`) REFERENCES `crp_programs` (`id`);