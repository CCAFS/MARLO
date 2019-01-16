ALTER TABLE `project_lp6_contribution` DROP FOREIGN KEY `fk_project_lp6_regions`;

ALTER TABLE `project_lp6_contribution`
DROP COLUMN `region_id`,
DROP INDEX `fk_project_lp6_regions`;

