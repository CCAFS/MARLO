ALTER TABLE `section_statuses`
ADD COLUMN `research_output_id`  int(11) NULL AFTER `reserach_outcome_id`;
ALTER TABLE `section_statuses` ADD CONSTRAINT `section_research_output_fk` FOREIGN KEY (`research_output_id`) REFERENCES `research_outputs` (`id`);