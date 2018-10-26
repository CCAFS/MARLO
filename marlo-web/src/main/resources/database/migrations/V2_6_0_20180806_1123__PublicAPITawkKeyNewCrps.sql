-- RTB

UPDATE custom_parameters set `value`= '582f0c81f9976a1964b0c240' where parameter_id = (
SELECT id from parameters where `key` = 'crp_taw_api' and global_unit_type_id=1) and global_unit_id = 17;

-- Fish

UPDATE custom_parameters set `value`= '582f0c81f9976a1964b0c240' where parameter_id = (
SELECT id from parameters where `key` = 'crp_taw_api' and global_unit_type_id=1) and global_unit_id = 28;

-- EiB

UPDATE custom_parameters set `value`= '582f0c81f9976a1964b0c240' where parameter_id = (
SELECT id from parameters where `key` = 'crp_taw_api' and global_unit_type_id=3) and global_unit_id = 25;

-- GLDC

UPDATE custom_parameters set `value`= '582f0c81f9976a1964b0c240' where parameter_id = (
SELECT id from parameters where `key` = 'crp_taw_api' and global_unit_type_id=1) and global_unit_id = 28;

-- CIAT

UPDATE custom_parameters set `value`= '583c8368de6cd808f31aee05' where parameter_id = (
SELECT id from parameters where `key` = 'crp_taw_api' and global_unit_type_id=4) and global_unit_id = 29;

