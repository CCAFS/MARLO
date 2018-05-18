ALTER TABLE `section_statuses`
ADD COLUMN `project_innovation_id`  bigint(20) NULL AFTER `powb_synthesis_id`;

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_ibfk_innovation` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`);