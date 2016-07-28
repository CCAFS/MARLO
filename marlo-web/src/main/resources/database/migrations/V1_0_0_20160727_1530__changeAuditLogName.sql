ALTER TABLE `auditlog`
CHANGE COLUMN `principal` `main`  bigint(20) NULL DEFAULT NULL AFTER `user_id`;

