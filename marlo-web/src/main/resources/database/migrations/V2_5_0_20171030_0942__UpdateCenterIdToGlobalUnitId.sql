UPDATE center_areas set global_unit_id = research_center_id;
UPDATE center_custom_parameters set global_unit_id = center_id;
UPDATE center_leaders set global_unit_id = research_center_id;
UPDATE center_objectives set global_unit_id = research_center_id;
UPDATE center_roles set global_unit_id = center_id;
UPDATE center_users set global_unit_id = center_id;
