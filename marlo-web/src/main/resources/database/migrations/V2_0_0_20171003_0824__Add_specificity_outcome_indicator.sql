-- Adding a new parameter to CRPs
-- category 	2 	=> 	Specificities
-- Format 		1	=> 	Yes/No
INSERT INTO `parameters` ( `key`, `description`, `format`, `default_value`, `category`) 
VALUES ('crp_ip_outcome_indicator', 'Visible and required the Outcome indicator field in the Impact Pathway Outcomes section', '1', 'false', '2');

-- Add parameter to MAIZE
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) 
VALUES ((select id from parameters where `key`='crp_ip_outcome_indicator'), '22', 'true', '3', '1', CURRENT_TIMESTAMP, '1', 'Added crp_ip_outcome_indicator parameter to MAIZE');

-- Add parameter to WHEAT
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) 
VALUES ((select id from parameters where `key`='crp_ip_outcome_indicator'), '21', 'true', '3', '1', CURRENT_TIMESTAMP, '1', 'Added crp_ip_outcome_indicator parameter to WHEAT');
