-- ADD Sustainable intensification outputs
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
VALUES ('Sustainable intensification outputs', null, 'Sustainable intensification outputs', 0,0);

-- ADD Technologies 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Technologies', id, 
'Crop modelling, Machinery, landscaping , tools and protocols, scaling',
0,0 from deliverable_types where `name` = 'Sustainable intensification outputs';

-- ADD Agronomic Practices 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Agronomic Practices', id, 
'e.g. crop rotation, fertilizer application, etc.',
0,0 from deliverable_types where `name` = 'Sustainable intensification outputs';