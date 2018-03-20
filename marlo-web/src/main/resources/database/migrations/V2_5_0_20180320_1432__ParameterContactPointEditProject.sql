INSERT INTO `parameters` (`key`, `description`, `format`, `default_value`, `category`,`global_unit_type_id`) VALUES ('crp_contact_point_edit_project', 'Contact Point Can Edit Projects', '1', NULL, '2',1);
INSERT INTO `custom_parameters` (`parameter_id`, `global_unit_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((select id from parameters where `key`='crp_contact_point_edit_project'), '1', 'false', '3', '1', CURRENT_TIMESTAMP, '3', '');
INSERT INTO `custom_parameters` (`parameter_id`, `global_unit_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((select id from parameters where `key`='crp_contact_point_edit_project'), '3', 'false', '3', '1', CURRENT_TIMESTAMP, '3', '');
INSERT INTO `custom_parameters` (`parameter_id`, `global_unit_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((select id from parameters where `key`='crp_contact_point_edit_project'), '4', 'false', '3', '1', CURRENT_TIMESTAMP, '3', '');
INSERT INTO `custom_parameters` (`parameter_id`, `global_unit_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((select id from parameters where `key`='crp_contact_point_edit_project'), '5', 'false', '3', '1', CURRENT_TIMESTAMP, '3', '');
INSERT INTO `custom_parameters` (`parameter_id`, `global_unit_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((select id from parameters where `key`='crp_contact_point_edit_project'), '7', 'false', '3', '1', CURRENT_TIMESTAMP, '3', '');
INSERT INTO `custom_parameters` (`parameter_id`, `global_unit_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((select id from parameters where `key`='crp_contact_point_edit_project'), '11', 'false', '3', '1', CURRENT_TIMESTAMP, '3', '');
INSERT INTO `custom_parameters` (`parameter_id`, `global_unit_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((select id from parameters where `key`='crp_contact_point_edit_project'), '21', 'true', '3', '1', CURRENT_TIMESTAMP, '3', '');
INSERT INTO `custom_parameters` (`parameter_id`, `global_unit_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((select id from parameters where `key`='crp_contact_point_edit_project'), '22', 'true', '3', '1', CURRENT_TIMESTAMP, '3', '');
