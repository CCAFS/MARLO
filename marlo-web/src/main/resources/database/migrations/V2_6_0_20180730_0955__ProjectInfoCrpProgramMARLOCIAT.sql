ALTER TABLE `projects_info`
ADD COLUMN `crp_program_id`  bigint(20) NULL AFTER `liaison_user_id`;

ALTER TABLE `projects_info` ADD CONSTRAINT `projects_info_ibfk_7` FOREIGN KEY (`crp_program_id`) REFERENCES `crp_programs` (`id`);