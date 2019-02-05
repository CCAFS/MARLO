ALTER TABLE `project_lp6_contribution`
MODIFY COLUMN `id`  bigint(20) NOT NULL AUTO_INCREMENT FIRST ,
MODIFY COLUMN `contribution`  tinyint(1) NULL AFTER `phase_id`,
ADD COLUMN `active_since`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP AFTER `is_active`,
ADD COLUMN `created_by`  bigint(20) NULL AFTER `active_since`,
ADD COLUMN `modified_by`  bigint(20) NULL AFTER `created_by`,
ADD COLUMN `modification_justification`  text NULL AFTER `modified_by`,
ADD INDEX `fk_project_lp6_phase_idx` (`phase_id`) ,
ADD INDEX `fk_project_lp6_project_idx` (`project_id`) ;

ALTER TABLE `project_lp6_contribution` ADD CONSTRAINT `fk_project_lp6_id` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`);

ALTER TABLE `project_lp6_contribution` ADD CONSTRAINT `fk_project_lp6_phase` FOREIGN KEY (`phase_id`) REFERENCES `phases` (`id`);

ALTER TABLE `project_lp6_contribution` ADD CONSTRAINT `fk_project_lp6_created` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `project_lp6_contribution` ADD CONSTRAINT `fk_project_lp6_modified` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);
