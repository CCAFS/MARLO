-- Set Custom Parameter to PIM
INSERT INTO `custom_parameters` (`parameter_id`, `global_unit_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) 
VALUES ((select id from parameters where `key`='crp_has_research_human'), '3', 'true', '3', '1', CURRENT_TIMESTAMP, '1', 'Added crp_has_research_human parameter to PIM');

-- Reset missing fields for PIM Funding Sources
update funding_sources_info set has_file_research=null;
update 
section_statuses se inner join 
    funding_sources fi on se.funding_source_id=fi.id
set missing_fields=CONCAT(missing_fields,',','fundingSource.hasFileResearch')
WHERE
    fi.global_unit_id = 3;