ALTER TABLE `email_logs`
ADD COLUMN `message_id`  text NULL AFTER `fileContent`;