Insert Into global_unit_types (id, `name`, is_active, created_by, modified_by, active_since, modification_justification)
VALUES (4, 'CGIAR Center', 1, 3, 3, NOW(), '');

INSERT INTO global_units (id, global_unit_type_id, `name`, acronym, institution_id, is_active, created_by, modified_by, active_since, modification_justification, is_marlo, login)
VALUE (29, 4, 'Centro Internacional de Agricultura Tropical', 'CIAT50', 46, 1,3,3,NOW(),'',1,1);