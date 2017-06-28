ALTER TABLE `section_statuses`
ADD COLUMN `research_program_id`  int(11) NULL AFTER `id`;

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_research_program_fk` FOREIGN KEY (`research_program_id`) REFERENCES `research_programs` (`id`);