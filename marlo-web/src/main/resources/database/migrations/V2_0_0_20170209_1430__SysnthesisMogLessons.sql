ALTER TABLE `project_component_lessons`
MODIFY COLUMN `ip_program_id`  bigint(20) NULL DEFAULT NULL AFTER `cycle`;

ALTER TABLE `project_component_lessons` ADD CONSTRAINT `project_componect_lessosn_ip_program_fk` FOREIGN KEY (`ip_program_id`) REFERENCES `ip_programs` (`id`);

