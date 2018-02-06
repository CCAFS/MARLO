INSERT INTO parameters (global_unit_type_id, `key`, description, format, default_value, category)
VALUES (2, 'crp_taw_api', 'Crp Taw Api Key', 4, null, 3);

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT id, '583c8368de6cd808f31aee05', 3, 1, NOW(), 3, '', 23 from parameters where `key` = 'crp_taw_api' AND global_unit_type_id=2;