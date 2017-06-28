ALTER TABLE `loc_elements` DROP FOREIGN KEY `loc_elements_ibfk_5`;

ALTER TABLE `loc_elements`
DROP COLUMN `is_site_integration`,
DROP COLUMN `crp_id`;