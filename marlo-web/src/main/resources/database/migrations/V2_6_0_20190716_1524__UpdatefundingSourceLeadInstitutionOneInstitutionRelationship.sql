update funding_sources_info fu 
set fu.lead_center_id=(select fsi.institution_id from funding_source_institutions fsi where fu.funding_source_id = fsi.funding_source_id and fu.id_phase=fsi.id_phase)
where (select count(*) from funding_source_institutions fsi where fu.funding_source_id = fsi.funding_source_id and fu.id_phase=fsi.id_phase) =1;