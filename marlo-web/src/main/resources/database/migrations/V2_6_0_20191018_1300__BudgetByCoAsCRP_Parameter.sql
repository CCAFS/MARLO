SET FOREIGN_KEY_CHECKS=0;

INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `default_value`, `category`) 
VALUES ('1', 'crp_budgetbycoas', 'Budget by CoAs Section Enabled for CRP', '1', NULL, '2');
INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `default_value`, `category`) 
VALUES ('3', 'crp_budgetbycoas', 'Budget by CoAs Section Enabled for Plataforms', '1', NULL, '2');
INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `default_value`, `category`) 
VALUES ('4', 'crp_budgetbycoas', 'Budget by CoAs Section Enabled for Center', '1', NULL, '2');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
(select (SELECT id FROM parameters WHERE description='Budget by CoAs Section Enabled for CRP'),'true','3', '1', NOW(), '3', '', global_units.id from  global_units 
where global_unit_type_id=1 AND acronym!='');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
(select (SELECT id FROM parameters WHERE description='Budget by CoAs Section Enabled for Plataforms'),'true','3', '1', NOW(), '3', '', global_units.id from  global_units 
where global_unit_type_id=3 AND acronym!='');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
(select (SELECT id FROM parameters WHERE description='Budget by CoAs Section Enabled for Center'),'true','3', '1', NOW(), '3', '', global_units.id from  global_units 
where global_unit_type_id=4 AND acronym!='');


