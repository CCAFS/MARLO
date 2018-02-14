ALTER TABLE `center_outputs`
MODIFY COLUMN `date_added`  timestamp NULL DEFAULT CURRENT_TIMESTAMP AFTER `short_name`;