ALTER TABLE `project_locations` DROP FOREIGN KEY `project_locations_ibfk_2`;

ALTER TABLE `project_locations` DROP FOREIGN KEY `project_locations_ibfk_3`;

ALTER TABLE `project_locations`
DROP COLUMN `crp_program_region_id`,
DROP COLUMN `region_loc_element`;