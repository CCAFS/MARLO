ALTER TABLE `section_statuses`
ADD COLUMN `deliverable_id`  bigint(20) NULL AFTER `research_output_id`;

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_deliverable_id_fk` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`);
