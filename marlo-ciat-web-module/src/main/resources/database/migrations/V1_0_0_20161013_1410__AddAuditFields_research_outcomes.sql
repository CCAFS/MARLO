ALTER TABLE `reserach_outcomes`
MODIFY COLUMN `research_topic_id`  int(11) NULL DEFAULT NULL AFTER `research_impact_id`,
MODIFY COLUMN `active_since`  timestamp NULL DEFAULT CURRENT_TIMESTAMP AFTER `is_active`,
ADD COLUMN `created_by`  bigint(20) NULL AFTER `active_since`,
ADD COLUMN `modified_by`  bigint(20) NULL AFTER `created_by`,
ADD COLUMN `modification_justification`  text NULL AFTER `modified_by`;

ALTER TABLE `reserach_outcomes` ADD CONSTRAINT `fk_outcomes_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `reserach_outcomes` ADD CONSTRAINT `fk_outcomes_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);