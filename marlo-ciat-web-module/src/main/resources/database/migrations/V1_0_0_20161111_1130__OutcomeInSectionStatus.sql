ALTER TABLE `section_statuses`
ADD COLUMN `reserach_outcome_id`  int(11) NULL AFTER `research_program_id`;

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_research_outcome_fk` FOREIGN KEY (`reserach_outcome_id`) REFERENCES `research_outcomes` (`id`);