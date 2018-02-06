-- UPDATE Book
UPDATE deliverables_info SET type_id = 64 WHERE type_id=65;
UPDATE deliverable_types SET name = 'Book' WHERE id=64;
DELETE FROM deliverable_types where id=65;

-- UPDATE Book chapter
UPDATE deliverables_info SET type_id = 66 WHERE type_id=67;
UPDATE deliverable_types SET name = 'Book chapter' WHERE id=66;
DELETE FROM deliverable_types where id=67;

-- Article for media/Magazine/Other (not peer-reviewed)
UPDATE deliverables_info SET type_id = 61 WHERE type_id=63;
DELETE FROM deliverable_types where id=63;

-- ADD Breeding outputs 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
VALUES ('Breeding outputs', null, 'Breeding outputs', 0,0);

-- ADD Breeding technology 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Breeding technology', id, 
'Improvement of or development of a phenotyping/genotyping tool, breeding approach, new molecular breeding tools/markers, new technology (ie. double haploid)',
0,0 from deliverable_types where `name` = 'Breeding outputs';

-- ADD Characterization 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Characterization', id, 
'Markers for unique alleles/haplotypes, accession and passport records identified or improved',
0,0 from deliverable_types where `name` = 'Breeding outputs';

-- ADD Line/variety developed 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Line/variety developed', id, 
'Coded line, CIMMYT maize line, germplasm characterized/developed, new hybrid',
0,0 from deliverable_types where `name` = 'Breeding outputs';

-- ADD Variety released 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'variety released', id, 
'traits',
0,0 from deliverable_types where `name` = 'Breeding outputs';

-- ADD Seed shipment 
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Seed shipment', id, 
'seed shipment',
0,0 from deliverable_types where `name` = 'Breeding outputs';

-- ADD Product allocation  
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Product allocation ', id, 
'Product advancement meetings, product announcements on websites, product profiles ',
0,0 from deliverable_types where `name` = 'Breeding outputs';