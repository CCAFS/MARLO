ALTER TABLE `center_outputs`
ADD COLUMN `center_program_id`  int(11) NULL AFTER `short_name`;

ALTER TABLE `center_outputs` ADD CONSTRAINT `center_outputs_program_id_fk` FOREIGN KEY (`center_program_id`) REFERENCES `center_programs` (`id`);