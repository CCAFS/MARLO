UPDATE `research_programs` SET `program_type`=1 WHERE `id`='1';
UPDATE `all_types` SET `type_name`='FLAGSHIP_PROGRAM_TYPE' WHERE `id`='2';
UPDATE `all_types` SET `type_name`='REGIONAL_PROGRAM_TYPE' WHERE `id`='1';
DELETE FROM `all_types` WHERE `id`='3';