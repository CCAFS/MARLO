START TRANSACTION;
ALTER TABLE `projects`
ADD COLUMN `create_date`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP AFTER `scale`;
COMMIT;