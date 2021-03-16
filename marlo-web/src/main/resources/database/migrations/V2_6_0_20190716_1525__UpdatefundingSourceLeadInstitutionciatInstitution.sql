update funding_sources_info fu 
set fu.lead_center_id=46
where (select count(*) from funding_source_institutions fsi where fu.funding_source_id = fsi.funding_source_id and fu.id_phase=fsi.id_phase and fsi.institution_id = 46) =1
and LENGTH(fu.finance_code) = 4;