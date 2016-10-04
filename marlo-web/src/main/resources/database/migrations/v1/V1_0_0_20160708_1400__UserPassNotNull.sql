ALTER TABLE `users`
MODIFY COLUMN `password`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `email`;