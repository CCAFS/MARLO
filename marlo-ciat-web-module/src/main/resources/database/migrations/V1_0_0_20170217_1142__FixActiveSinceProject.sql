ALTER TABLE `projects`
CHANGE COLUMN `active_sice` `active_since`  timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP AFTER `is_active`;