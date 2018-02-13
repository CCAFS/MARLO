INSERT INTO roles (description, acronym, `order`, global_unit_id)
VALUES ('Cluster Leader', 'CL', 10, 24);

UPDATE custom_parameters SET `value` = (SELECT r.id from roles r WHERE r.global_unit_id = 24 and r.acronym = 'CL') WHERE id = 711;

INSERT INTO roles (description, acronym, `order`, global_unit_id)
VALUES ('Flagship Manager', 'FPM', 11, 24);

UPDATE custom_parameters SET `value` = (SELECT r.id from roles r WHERE r.global_unit_id = 24 and r.acronym = 'FPM') WHERE id = 730;

INSERT INTO roles (description, acronym, `order`, global_unit_id)
VALUES ('Regional Manager', 'RPM', 12, 24);

UPDATE custom_parameters SET `value` = (SELECT r.id from roles r WHERE r.global_unit_id = 24 and r.acronym = 'RPM') WHERE id = 732;