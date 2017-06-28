ALTER TABLE `projects`
MODIFY COLUMN `is_global`  tinyint(1) NULL AFTER `total_amount`,
ADD COLUMN `is_region`  tinyint(1) NULL AFTER `is_global`;