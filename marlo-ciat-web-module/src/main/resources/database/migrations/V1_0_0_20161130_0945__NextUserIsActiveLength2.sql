ALTER TABLE `research_outputs_next_users`
MODIFY COLUMN `is_active`  tinyint(1) NULL DEFAULT NULL AFTER `active_since`;