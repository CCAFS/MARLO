ALTER TABLE `disciplines` 
ADD COLUMN `is_active` tinyint(1) NULL AFTER `research_program`,
ADD COLUMN `active_since` timestamp NULL AFTER `is_active`,
ADD COLUMN `created_by` bigint(20) NULL AFTER `active_since`,
ADD COLUMN `modified_by` bigint(20) NULL AFTER `created_by`,
ADD COLUMN `modification_justification` text NULL AFTER `modified_by`;

ALTER TABLE `disciplines` 
ADD CONSTRAINT `discipline_created_fk`	FOREIGN KEY (`created_by`) 	REFERENCES `users` (`id`),
ADD CONSTRAINT `discipline_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`);