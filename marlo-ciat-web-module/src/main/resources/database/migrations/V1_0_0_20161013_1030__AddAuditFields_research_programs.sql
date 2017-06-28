ALTER TABLE `research_programs`
MODIFY COLUMN `research_area_id`  int(11) NOT NULL AFTER `color`,
MODIFY COLUMN `is_active`  tinyint(1) NOT NULL DEFAULT 1 AFTER `research_area_id`,
ADD COLUMN `active_since`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP AFTER `is_active`,
ADD COLUMN `created_by`  bigint(20) NULL AFTER `active_since`,
ADD COLUMN `modified_by`  bigint(20) NULL AFTER `created_by`,
ADD COLUMN `modification_justification`  text NULL AFTER `modified_by`;

ALTER TABLE `research_programs` ADD CONSTRAINT `fk_rprogram_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `research_programs` ADD CONSTRAINT `fk_rprogram_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);