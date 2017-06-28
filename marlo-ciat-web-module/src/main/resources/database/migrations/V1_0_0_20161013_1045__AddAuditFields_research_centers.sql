ALTER TABLE `research_centers`
ADD COLUMN `is_active`  tinyint(1) NOT NULL DEFAULT 1 AFTER `acronym`,
ADD COLUMN `active_since`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP AFTER `is_active`,
ADD COLUMN `created_by`  bigint(20) NULL AFTER `active_since`,
ADD COLUMN `modified_by`  bigint(20) NULL AFTER `created_by`,
ADD COLUMN `modification_justification`  text NULL AFTER `modified_by`;

ALTER TABLE `research_centers` ADD CONSTRAINT `fk_rcenter_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

ALTER TABLE `research_centers` ADD CONSTRAINT `fk_rcenter_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);