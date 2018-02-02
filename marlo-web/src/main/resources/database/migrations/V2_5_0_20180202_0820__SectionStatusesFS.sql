-- Update missing fields for A4NH Funding Sources
update 
section_statuses se inner join 
    funding_sources fi on se.funding_source_id=fi.id
set missing_fields=CONCAT(missing_fields,',','fundingSource.hasFileResearch')
WHERE
    fi.global_unit_id = 5