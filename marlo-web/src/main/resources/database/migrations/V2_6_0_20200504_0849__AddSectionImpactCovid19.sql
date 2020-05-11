## create table impacts covid-19
CREATE TABLE `project_impacts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `answer` text,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
   PRIMARY KEY (`id`),
   CONSTRAINT `project_impacts_projects` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `project_impacts_phases` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `project_impacts_users1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
   CONSTRAINT `project_impacts_users2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

## create section status for covid-19
ALTER TABLE `section_statuses` ADD `project_impact_id` bigint(20) DEFAULT NULL;
ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_impacts` FOREIGN KEY (`project_impact_id`) REFERENCES `project_impacts`(`id`);

## create parameters specificity 
INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '1', 'crp_show_section_impact_covid19', 'Show section impact of COVID-19', '1', '2');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`)
SELECT @@identity, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`
FROM `custom_parameters` WHERE parameter_id = 217;

INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '3', 'crp_show_section_impact_covid19', 'Show section impact of COVID-19', '1', '2');

INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '4', 'crp_show_section_impact_covid19', 'Show section impact of COVID-19', '1', '2');