ALTER TABLE `center_areas` DROP FOREIGN KEY `center_areas_ibfk_1`;

ALTER TABLE `center_areas`
DROP COLUMN `research_center_id`;

ALTER TABLE `center_leaders` DROP FOREIGN KEY `center_leaders_ibfk_4`;

ALTER TABLE `center_leaders`
DROP COLUMN `research_center_id`;

ALTER TABLE `center_objectives` DROP FOREIGN KEY `center_objectives_ibfk_1`;

ALTER TABLE `center_objectives`
DROP COLUMN `research_center_id`;

