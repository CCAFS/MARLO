insert into crp_loc_element_types (crp_id,loc_element_type_id)

select cp.id,loc.id from loc_element_types  loc,
crps cp 
 where loc.crp_id is null and loc.is_active=1 and cp.is_marlo=1;