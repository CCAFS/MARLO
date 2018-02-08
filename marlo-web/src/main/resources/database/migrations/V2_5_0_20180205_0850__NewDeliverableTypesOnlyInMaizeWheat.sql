UPDATE deliverable_types SET global_unit_id = 21 WHERE name = 'Breeding outputs';
UPDATE deliverable_types SET global_unit_id = 21 WHERE name = 'Sustainable intensification outputs';

--

-- ADD Breeding outputs 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type, global_unit_id)
VALUES ('Breeding outputs', null, 'Breeding outputs', 0,0, 22);

-- ADD Breeding technology 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Breeding technology', id, 
'Improvement of or development of a phenotyping/genotyping tool, breeding approach, new molecular breeding tools/markers, new technology (ie. double haploid)',
0,0 from deliverable_types where `name` = 'Breeding outputs' and global_unit_id=22;

-- ADD Characterization 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Characterization', id, 
'Markers for unique alleles/haplotypes, accession and passport records identified or improved',
0,0 from deliverable_types where `name` = 'Breeding outputs' and global_unit_id=22;

-- ADD Line/variety developed 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Line/variety developed', id, 
'Coded line, CIMMYT maize line, germplasm characterized/developed, new hybrid',
0,0 from deliverable_types where `name` = 'Breeding outputs' and global_unit_id=22;

-- ADD Variety released 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'variety released', id, 
'traits',
0,0 from deliverable_types where `name` = 'Breeding outputs' and global_unit_id=22;

-- ADD Seed shipment 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Seed shipment', id, 
'seed shipment',
0,0 from deliverable_types where `name` = 'Breeding outputs' and global_unit_id=22;

-- ADD Product allocation  
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Product allocation ', id, 
'Product advancement meetings, product announcements on websites, product profiles ',
0,0 from deliverable_types where `name` = 'Breeding outputs' and global_unit_id=22;

--
-- ADD Sustainable intensification outputs
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type, global_unit_id)
VALUES ('Sustainable intensification outputs', null, 'Sustainable intensification outputs', 0,0, 22);

-- ADD Technologies 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Technologies', id, 
'Crop modelling, Machinery, landscaping , tools and protocols, scaling',
0,0 from deliverable_types where `name` = 'Sustainable intensification outputs' and global_unit_id=22 ;

-- ADD Agronomic Practices 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Agronomic Practices', id, 
'e.g. crop rotation, fertilizer application, etc.',
0,0 from deliverable_types where `name` = 'Sustainable intensification outputs' and global_unit_id=22;
