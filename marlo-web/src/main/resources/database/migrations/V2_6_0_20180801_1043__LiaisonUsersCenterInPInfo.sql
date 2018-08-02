ALTER TABLE `projects_info` DROP FOREIGN KEY `projects_info_ibfk_7`;

ALTER TABLE `projects_info`
CHANGE COLUMN `crp_program_id` `liaison_institution_center`  bigint(20) NULL DEFAULT NULL AFTER `liaison_user_id`;

ALTER TABLE `projects_info` ADD FOREIGN KEY (`liaison_institution_center`) REFERENCES `liaison_institutions` (`id`);