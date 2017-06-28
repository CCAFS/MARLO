ALTER TABLE `project_crosscuting_themes`
MODIFY COLUMN `active_since`  timestamp NULL DEFAULT NULL AFTER `is_active`;