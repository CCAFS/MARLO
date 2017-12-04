insert into parameters (`key`, description, format, default_value, category, global_unit_type_id)
select `key`, description, format, default_value, category, 2 from center_parameters;