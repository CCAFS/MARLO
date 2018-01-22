delete from  funding_source_institutions  where funding_source_id = 248;



insert into funding_source_institutions (funding_source_id, institution_id, id_phase)

select 248, institution_id, id_phase from crp_ppa_partners where crp_id=1 and is_active=1;

