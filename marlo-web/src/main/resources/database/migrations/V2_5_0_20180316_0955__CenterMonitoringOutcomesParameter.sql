INSERT INTO parameters (global_unit_type_id, `key`, description, format, category)
VALUES (2, 'center_outcomes_active', 'Monitoring outcomes Active', 1, 3);

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT id, 'true', 3, 1, NOW(), 3, '', 23 from parameters where `key` = 'center_outcomes_active' AND global_unit_type_id = 2;