INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.parameter_id, p.`value`, 3, 1,NOW(),3,'',26 FROM custom_parameters p 
WHERE p.global_unit_id = 24;