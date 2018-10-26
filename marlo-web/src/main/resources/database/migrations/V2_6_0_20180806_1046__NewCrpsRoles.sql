-- RTB

INSERT INTO roles (description, acronym, `order`, global_unit_id)
SELECT r.description, r.acronym, r.`order`, 17 FROM roles r
WHERE r.global_unit_id =1;

UPDATE roles set description = 'RTB Administrators' where global_unit_id =17 AND acronym = 'CRP-Admin';

-- Fish

INSERT INTO roles (description, acronym, `order`, global_unit_id)
SELECT r.description, r.acronym, r.`order`, 27 FROM roles r
WHERE r.global_unit_id =1;

UPDATE roles set description = 'Fish Administrators' where global_unit_id =27 AND acronym = 'CRP-Admin';


-- GLDC

INSERT INTO roles (description, acronym, `order`, global_unit_id)
SELECT r.description, r.acronym, r.`order`, 28 FROM roles r
WHERE r.global_unit_id =1;

UPDATE roles set description = 'GLDC Administrators' where global_unit_id =28 AND acronym = 'CRP-Admin';

-- EiB

DELETE FROM roles where global_unit_id =25;

INSERT INTO roles (description, acronym, `order`, global_unit_id)
SELECT r.description, r.acronym, r.`order`, 25 FROM roles r
WHERE r.global_unit_id =24;

UPDATE roles set description = 'EiB Administrators' where global_unit_id =25 AND acronym = 'CRP-Admin';

