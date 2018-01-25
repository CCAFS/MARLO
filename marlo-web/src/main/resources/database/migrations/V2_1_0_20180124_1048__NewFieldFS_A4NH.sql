ALTER TABLE funding_sources_info ADD file_research bigint(20) null;
ALTER TABLE funding_sources_info ADD has_file_research tinyint(1) null default 0;


INSERT INTO `parameters` ( `key`, `description`, `format`, `default_value`, `category`) 
VALUES ('crp_has_research_human', 'Ask if the study involves research with human subjects', '1', 'false', '2');

-- Add parameter to A4NH
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) 
VALUES ((select id from parameters where `key`='crp_has_research_human'), '5', 'true', '3', '1', CURRENT_TIMESTAMP, '1', 'Added crp_has_research_human parameter to A4NH');