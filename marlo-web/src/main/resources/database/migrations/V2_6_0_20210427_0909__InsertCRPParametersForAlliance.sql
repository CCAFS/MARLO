insert INTO parameters (global_unit_type_id, `key`, description,  format, default_value,  category)
select 5, `key`, description, format, default_value, category from parameters where global_unit_type_id = 1;

delete from parameters where id = (
  select id from(select MAX(id) AS id from parameters p where p.`key` = 'crp_enable_nexus_lever_sdg_fields' and p.global_unit_type_id = 5) AS c
);