SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `section_statuses`
ADD COLUMN `upkeep`  tinyint(1) NOT NULL DEFAULT 0 AFTER `year`;