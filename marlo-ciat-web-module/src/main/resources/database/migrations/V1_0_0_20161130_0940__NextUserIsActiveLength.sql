ALTER TABLE `nextuser_types`
MODIFY COLUMN `is_active`  tinyint(1) NULL DEFAULT NULL AFTER `active_since`;