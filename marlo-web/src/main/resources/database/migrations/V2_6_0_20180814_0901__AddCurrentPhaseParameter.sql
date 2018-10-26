SET FOREIGN_KEY_CHECKS=0;

/* Current Phase parameters for CRP/Platform/Center  */
INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `default_value`, `category`) 
VALUES ('1', 'current_phase', 'The current phase of the CRP', '3', NULL, '3');
INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `default_value`, `category`) 
VALUES ('3', 'current_phase', 'The current phase of the Platform', '3', NULL, '3');
INSERT INTO `parameters` (`global_unit_type_id`, `key`, `description`, `format`, `default_value`, `category`) 
VALUES ('4', 'current_phase', 'The current phase of the Center', '3', NULL, '3');

/* CRPs */
INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the CRP'), '41', '3', '1', NOW(), '3', '', '1');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the CRP'), '12', '3', '1', NOW(), '3', '', '3');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the CRP'), '43', '3', '1', NOW(), '3', '', '4');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the CRP'), '14', '3', '1', NOW(), '3', '', '5');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the CRP'), '15', '3', '1', NOW(), '3', '', '7');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the CRP'), '31', '3', '1', NOW(), '3', '', '11');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the CRP'), '190', '3', '1', NOW(), '3', '', '16');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the CRP'), '257', '3', '1', NOW(), '3', '', '17');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the CRP'), '32', '3', '1', NOW(), '3', '', '21');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the CRP'), '33', '3', '1', NOW(), '3', '', '22');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the CRP'), '267', '3', '1', NOW(), '3', '', '27');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the CRP'), '277', '3', '1', NOW(), '3', '', '28');


/* Platforms */
INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the Platform'), '35', '3', '1', NOW(), '3', '', '24');

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the Platform'), '36', '3', '1', NOW(), '3', '', '25');


/* Centers */
INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) 
VALUES ((SELECT id FROM parameters WHERE description='The current phase of the Center'), '175', '3', '1', NOW(), '3', '', '29');