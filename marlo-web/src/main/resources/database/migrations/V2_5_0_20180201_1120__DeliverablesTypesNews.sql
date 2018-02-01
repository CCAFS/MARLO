-- ADD Needs/capacity assessment  
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Needs/capacity assessment', id, 
'needs/capacity assessment of NARS/partners',
0,0 from deliverable_types where `name` = 'Reports and other publications ';

-- ADD Business model   
INSERT INTO deliverable_types (`name`, parent_id, description, is_fair, admin_type)
SELECT 'Business model', id, 
'business model developed for deployment of sustainable intensification technologies',
0,0 from deliverable_types where `name` = 'Data, models and tools';