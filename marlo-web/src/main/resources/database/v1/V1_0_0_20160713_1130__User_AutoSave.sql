ALTER TABLE `users`
ADD COLUMN `auto_save`  tinyint(1) NOT NULL AFTER `last_login`;