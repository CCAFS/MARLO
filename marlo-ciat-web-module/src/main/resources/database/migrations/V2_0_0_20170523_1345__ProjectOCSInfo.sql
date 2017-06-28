ALTER TABLE `projects`
ADD COLUMN `ocs_code`  text NULL AFTER `id`,
ADD COLUMN `description`  text NULL AFTER `name`,
ADD COLUMN `project_type_id`  bigint(20) NULL AFTER `short_name`,
ADD COLUMN `extension_date`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP AFTER `end_date`,
ADD COLUMN `original_donor`  text NULL AFTER `contact_person_id`,
ADD COLUMN `direct_donor`  text NULL AFTER `original_donor`,
ADD COLUMN `total_amount`  double(30,2) NULL AFTER `direct_donor`;

ALTER TABLE `projects` ADD CONSTRAINT `project_type_fk` FOREIGN KEY (`project_type_id`) REFERENCES `project_types` (`id`);

