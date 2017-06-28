ALTER TABLE `projects`
ADD COLUMN `date_created`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP AFTER `status_id`;