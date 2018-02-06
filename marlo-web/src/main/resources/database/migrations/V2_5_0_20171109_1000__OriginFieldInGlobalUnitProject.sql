ALTER TABLE `global_unit_projects`
ADD COLUMN `origin`  tinyint(1) NOT NULL AFTER `global_unit_id`;

UPDATE `global_unit_projects` set `origin`=1;
