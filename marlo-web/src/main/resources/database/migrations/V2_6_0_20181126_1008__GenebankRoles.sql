INSERT INTO roles (description, acronym, `order`, global_unit_id)
SELECT r.description, r.acronym, r.`order`, 26 From roles r
Where r.global_unit_id = 24; 