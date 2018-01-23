ALTER TABLE `email_logs`
CHANGE COLUMN `to` `to_email`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `id`,
CHANGE COLUMN `cc` `cc_email`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `to_email`,
CHANGE COLUMN `bbc` `bbc_email`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `cc_email`,
CHANGE COLUMN `date` `date_email`  datetime NULL DEFAULT NULL AFTER `tried`,
CHANGE COLUMN `error` `error_email`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `date_email`,
CHANGE COLUMN `succes` `succes_email`  tinyint(1) NULL DEFAULT NULL AFTER `error_email`;

