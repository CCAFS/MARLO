UPDATE projects SET is_region = 0;

ALTER TABLE `projects`
MODIFY COLUMN `is_global`  tinyint(1) NOT NULL AFTER `total_amount`,
MODIFY COLUMN `is_region`  tinyint(1) NOT NULL AFTER `is_global`;