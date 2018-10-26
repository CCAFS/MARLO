DELETE from custom_parameters where global_unit_id = 25;

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT cp.parameter_id, cp.`value`, 3, cp.is_active, now(), 3, '', 25 from custom_parameters cp
WHERE cp.global_unit_id = 24;