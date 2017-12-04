ALTER TABLE `custom_parameters` DROP FOREIGN KEY `custom_parameters_ibfk_2`;

ALTER TABLE `custom_parameters`
DROP COLUMN `crp_id`,
MODIFY COLUMN `global_unit_id`  bigint(20) NOT NULL AFTER `modification_justification`;