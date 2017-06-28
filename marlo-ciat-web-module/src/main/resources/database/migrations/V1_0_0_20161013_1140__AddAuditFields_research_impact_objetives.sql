ALTER TABLE `research_impact_objectives`
ADD COLUMN `is_active`  tinyint(1) NOT NULL AFTER `impact_id`,
ADD COLUMN `active_since`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP AFTER `is_active`,
ADD COLUMN `created_by`  bigint(20) NULL AFTER `active_since`,
ADD COLUMN `modified_by`  bigint(20) NULL AFTER `created_by`,
ADD COLUMN `modification_justification`  text NULL AFTER `modified_by`;

ALTER TABLE `research_impact_objectives` ADD CONSTRAINT `fk_impact_objective_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `research_impact_objectives` ADD CONSTRAINT `fk_impact_objective_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);