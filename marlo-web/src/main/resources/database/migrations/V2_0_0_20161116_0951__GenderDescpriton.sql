ALTER TABLE `projects`
ADD COLUMN `gender_analysis`  text NULL AFTER `status`,
ADD COLUMN `cross_cutting_gender`  tinyint(1) NULL AFTER `gender_analysis`,
ADD COLUMN `cross_cutting_youth`  tinyint(1) NULL AFTER `cross_cutting_gender`,
ADD COLUMN `cross_cutting_capacity`  tinyint(1) NULL AFTER `cross_cutting_youth`,
ADD COLUMN `dimension`  text NULL AFTER `cross_cutting_capacity`;

