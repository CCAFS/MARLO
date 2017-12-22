-- Enabling BigData and EiB Platforms
UPDATE `global_units` SET `acronym`='BigData', `login`='1' WHERE `id`='24';
UPDATE `global_units` SET `acronym`='EiB', `login`='1' WHERE `id`='25';
UPDATE `global_units` SET `acronym`='Genebank' WHERE `id`='26';

-- Set up platform phases
INSERT INTO `phases` (`description`, `year`, `global_unit_id`) VALUES ('Planning', '2017', '24');
INSERT INTO `phases` (`description`, `year`, `global_unit_id`) VALUES ('Planning', '2017', '25');

-- Fix mistake in is active parameter
UPDATE `global_unit_types` SET `is_active`='1' WHERE `id`='2';
UPDATE `global_unit_types` SET `is_active`='1' WHERE `id`='3';

