ALTER TABLE `capacity_development_types` 
ADD COLUMN `is_active` tinyint(1) NULL AFTER `category`,
ADD COLUMN `active_since` timestamp NULL AFTER `is_active`,
ADD COLUMN `created_by` bigint(20) NULL AFTER `active_since`,
ADD COLUMN `modified_by` bigint(20) NULL AFTER `created_by`,
ADD COLUMN `modification_justification` text NULL AFTER `modified_by`;

ALTER TABLE `capacity_development_types` 
ADD CONSTRAINT `capdev_type_created_fk`		FOREIGN KEY (`created_by`)	REFERENCES `users` (`id`),
ADD CONSTRAINT `capdev_type_modified_fk`	FOREIGN KEY (`modified_by`)	REFERENCES `users` (`id`);




ALTER TABLE `capdev_founding_type` 
ADD COLUMN `is_active` tinyint(1) NULL AFTER `description`,
ADD COLUMN `active_since` timestamp NULL AFTER `is_active`,
ADD COLUMN `created_by` bigint(20) NULL AFTER `active_since`,
ADD COLUMN `modified_by` bigint(20) NULL AFTER `created_by`,
ADD COLUMN `modification_justification` text NULL AFTER `modified_by`;

ALTER TABLE `capdev_founding_type` 
ADD CONSTRAINT `fundingtype_created_fk`	FOREIGN KEY (`created_by`)	REFERENCES `users` (`id`),
ADD CONSTRAINT `fundigtype_modified`	FOREIGN KEY (`modified_by`)	REFERENCES `users` (`id`);



ALTER TABLE `capdev_highest_degree` 
ADD COLUMN `is_active` tinyint(1) NULL AFTER `acronym`,
ADD COLUMN `active_since` timestamp NULL AFTER `is_active`,
ADD COLUMN `created_by` bigint(20) NULL AFTER `active_since`,
ADD COLUMN `modified_by` bigint(20) NULL AFTER `created_by`,
ADD COLUMN `modification_justification` text NULL AFTER `modified_by`;

ALTER TABLE `capdev_highest_degree` 
ADD CONSTRAINT `highest_degree_created_fk`	FOREIGN KEY (`created_by`)	REFERENCES `users` (`id`),
ADD CONSTRAINT `highest_degree_modified_fk`	FOREIGN KEY (`modified_by`)	REFERENCES `users` (`id`);