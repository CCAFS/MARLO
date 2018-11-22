UPDATE custom_parameters set `value` = 'true' where global_unit_id =21 and parameter_id=(Select id from parameters where `key` = 'crp_project_budget_zero'
AND
global_unit_type_id=1);

UPDATE custom_parameters set `value` = 'true' where global_unit_id =22 and parameter_id=(Select id from parameters where `key` = 'crp_project_budget_zero'
AND
global_unit_type_id=1);