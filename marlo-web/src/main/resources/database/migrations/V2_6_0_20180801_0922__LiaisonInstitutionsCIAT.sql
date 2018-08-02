INSERT INTO liaison_institutions (`name`, acronym, crp_program, is_active, global_unit_id)
SELECT p.`name`, p.acronym, p.id, 1, p.global_unit_id from crp_programs p where p.global_unit_id =29;

INSERT INTO liaison_institutions (`name`, acronym, crp_program, is_active, global_unit_id)
VALUES ('Program Management Unit', 'PMU', NULL, 1, 29);