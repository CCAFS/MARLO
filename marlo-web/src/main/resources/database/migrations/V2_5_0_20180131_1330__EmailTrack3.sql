ALTER TABLE `email_logs`
ADD COLUMN `file_name`  varchar(500) NULL AFTER `succes_email`,
ADD COLUMN `fileContent`  mediumblob NULL AFTER `file_name`;

