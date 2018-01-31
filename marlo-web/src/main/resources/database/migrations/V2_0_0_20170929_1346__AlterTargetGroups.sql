ALTER TABLE `target_groups` 
ADD COLUMN `is_active` tinyint(1) NULL AFTER `description`,
ADD COLUMN `active_since` timestamp NULL AFTER `is_active`,
ADD COLUMN `created_by` bigint(20) NULL AFTER `active_since`,
ADD COLUMN `modified_by` bigint(20) NULL AFTER `created_by`,
ADD COLUMN `modification_justification` text NULL AFTER `modified_by`;


ALTER TABLE `target_groups` 
ADD CONSTRAINT `target_group_created_fk` 	FOREIGN KEY (`created_by`)	REFERENCES `users` (`id`),
ADD CONSTRAINT `target_group_modified_fk`	FOREIGN KEY (`modified_by`)	REFERENCES `users` (`id`);