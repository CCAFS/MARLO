#project_impacts
ALTER TABLE `project_impacts` DROP FOREIGN KEY `project_impacts_phases`;
ALTER TABLE `project_impacts` DROP COLUMN `id_phase`;
ALTER TABLE `project_impacts` ADD `year` int(11) NOT NULL;

#parameters ranges years
INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '1', 'crp_show_section_impact_covid19_ranges_years', 'Years ranges to show COVID-19 session. Format: [since]-[until]. Ex.: 2020-2021 or only 2020 means that from this year onwards.', '4', '2');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`)
SELECT @@identity, '2020-2021', `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`
FROM `marlo`.`custom_parameters` WHERE parameter_id = 248;

INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '3', 'crp_show_section_impact_covid19_ranges_years', 'Years ranges to show COVID-19 session. Format: [since]-[until]. Ex.: 2020-2021 or only 2020 means that from this year onwards.', '4', '2');

INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '4', 'crp_show_section_impact_covid19_ranges_years', 'Years ranges to show COVID-19 session. Format: [since]-[until]. Ex.: 2020-2021 or only 2020 means that from this year onwards.', '4', '2');