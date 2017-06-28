ALTER TABLE `reserach_outputs`
ADD COLUMN `is_active`  tinyint(1) NULL AFTER `reserach_outcome_id`,
ADD COLUMN `active_since`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP AFTER `is_active`,
ADD COLUMN `created_by`  bigint(20) NULL AFTER `active_since`,
ADD COLUMN `modified_by`  bigint(20) NULL AFTER `created_by`,
ADD COLUMN `modification_justification`  text NULL AFTER `modified_by`;

ALTER TABLE `reserach_outputs` ADD CONSTRAINT `fk_output_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);
ALTER TABLE `reserach_outputs` ADD CONSTRAINT `fk_output_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);