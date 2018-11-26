INSERT INTO liaison_institutions (institution_id, `name`, acronym, crp_program, is_active, global_unit_id)
SELECT l.institution_id, l.`name`, l.acronym, l.crp_program, l.is_active, 26 from liaison_institutions l WHERE
crp_program IS NULL AND global_unit_id = 24;