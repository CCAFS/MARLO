ALTER TABLE `center_topics`
ADD COLUMN `program_id`  bigint(20) NULL AFTER `research_program_id`;

ALTER TABLE `center_topics` ADD CONSTRAINT `center_topics_ibfk_4` FOREIGN KEY (`program_id`) REFERENCES `crp_programs` (`id`);


ALTER TABLE `center_outputs`
ADD COLUMN `program_id`  bigint(20) NULL AFTER `short_name`;

ALTER TABLE `center_outputs` ADD CONSTRAINT `center_outputs_ibfk_4` FOREIGN KEY (`program_id`) REFERENCES `crp_programs` (`id`);