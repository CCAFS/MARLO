INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '1', 'crp_bi_module_active', 'Show BI Module Tab', '1', '2');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`)
SELECT (SELECT id FROM `parameters` WHERE `key` = 'crp_bi_module_active' AND global_unit_type_id = 1), 'false', `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`
FROM `custom_parameters` WHERE parameter_id = 217;

INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '3', 'crp_bi_module_active', 'Show BI Module Tab', '1', '2');

INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `category`)
VALUES ( '4', 'crp_bi_module_active', 'Show BI Module Tab', '1', '2');
