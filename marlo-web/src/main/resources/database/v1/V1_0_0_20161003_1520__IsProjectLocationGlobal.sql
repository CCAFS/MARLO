ALTER TABLE `projects`
ADD COLUMN `is_location_global`  tinyint(1) NULL AFTER `preset_date`;

update projects set is_location_global=0;