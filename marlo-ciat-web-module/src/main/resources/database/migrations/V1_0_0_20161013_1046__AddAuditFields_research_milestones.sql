ALTER TABLE `research_milestones`
MODIFY COLUMN `is_active`  tinyint(1) NOT NULL DEFAULT 1 AFTER `impact_outcome_id`,
ADD COLUMN `created_by`  bigint(20) NULL AFTER `active_since`,
ADD COLUMN `modified_by`  bigint(20) NULL AFTER `created_by`,
ADD COLUMN `modification_justification`  text NULL AFTER `modified_by`;

ALTER TABLE `research_milestones` ADD CONSTRAINT `fk_milestones_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `research_milestones` ADD CONSTRAINT `fk_milestones_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);