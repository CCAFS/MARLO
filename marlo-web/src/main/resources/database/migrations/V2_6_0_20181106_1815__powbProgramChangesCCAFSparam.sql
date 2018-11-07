UPDATE custom_parameters set `value` = 'true' where global_unit_id =1 and parameter_id=(Select id from parameters where `key` = 'crp_powb_program_change'
AND
global_unit_type_id=1);