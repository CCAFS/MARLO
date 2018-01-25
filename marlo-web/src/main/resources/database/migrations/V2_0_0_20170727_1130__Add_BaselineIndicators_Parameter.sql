-- Add Baseline Indicators Parameter
INSERT INTO `parameters` (`key`, `description`, `format`, `category`) 
VALUES ('crp_baseline_indicators', 'Allow to add Baseline indicators to the flagship outcomes', '1', '2');

-- Assign as TRUE the Baseline indicator parameter in CCAFS
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `is_active`, `modified_by`, `modification_justification`) 
VALUES ((select id from parameters where `key` = 'crp_baseline_indicators' limit 1), '1', 'true', '3', '1', '1082', 'Added Baseline indicators to CCAFS');