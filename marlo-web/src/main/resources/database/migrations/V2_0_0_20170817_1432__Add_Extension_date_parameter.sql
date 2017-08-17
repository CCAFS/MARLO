-- Add Extension Date Parameter
INSERT INTO `parameters` (`key`, `description`, `format`, `category`) 
VALUES ('crp_funding_source_extension_date', 'Allow to add an extension date in Funding sources', '1', '2');

-- Assign as TRUE the Extension Date parameter in CCAFS
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `is_active`, `modified_by`, `modification_justification`) 
VALUES ((select id from parameters where `key` = 'crp_funding_source_extension_date'), '1', 'true', '3', '1', '1082', 'Added Extension Date parameter to CCAFS');