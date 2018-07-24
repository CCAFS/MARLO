INSERT INTO roles (description, acronym, `order`, global_unit_id)
SELECT r.description, r.acronym, r.`order`, 16 FROM roles r
WHERE r.global_unit_id =1;

UPDATE roles set description = 'rice Administrators' where global_unit_id =16 AND acronym = 'CRP-Admin';
UPDATE global_units set acronym = 'rice' where id = 16;