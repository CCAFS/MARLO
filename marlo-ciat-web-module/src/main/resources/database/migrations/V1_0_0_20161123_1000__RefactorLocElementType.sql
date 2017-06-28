ALTER TABLE `loc_element_types` DROP FOREIGN KEY `loc_element_types_ibfk_1`;

ALTER TABLE `loc_element_types`
DROP COLUMN `crp_id`,
DROP COLUMN `is_scope`;