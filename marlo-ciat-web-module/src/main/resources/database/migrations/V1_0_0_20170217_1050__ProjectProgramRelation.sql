ALTER TABLE `projects`
ADD COLUMN `program_id`  int(11) NOT NULL AFTER `id`;
ALTER TABLE `projects` ADD CONSTRAINT `project_program_fk` FOREIGN KEY (`program_id`) REFERENCES `research_programs` (`id`);