ALTER TABLE `project_policies`
MODIFY COLUMN `active_since`  timestamp NULL DEFAULT CURRENT_TIMESTAMP AFTER `is_active`,
MODIFY COLUMN `created_by`  bigint(20) NULL AFTER `active_since`;